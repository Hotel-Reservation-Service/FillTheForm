/*
 * Copyright (C) 2015 HRS GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hrs.filltheform.dialog;

import com.hrs.filltheform.common.ConfigurationItem;
import com.hrs.filltheform.common.PropertyChangedListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This model class holds the state and manages behavior of the FillTheFormDialog.
 */
class FillTheFormDialogModel {

    public interface FillTheFormDialogModelHelper {

        boolean isConfigurationVariableKey(String variableKey);

        String getConfigurationVariableValue(String variableKey);
    }

    public static final String PROPERTY_EXPAND_ICON = "property_expand_icon";
    public static final String PROPERTY_EXPAND_ICON_FAST_MODE = "property_expand_icon_fast_mode";
    public static final String PROPERTY_CLOSE_BUTTON = "property_close_button";
    public static final String PROPERTY_OPEN_FILL_THE_FORM_APP_BUTTON = "property_open_fill_the_form_app_button";
    public static final String PROPERTY_MINIMIZE_BUTTON = "property_minimize_button";
    public static final String PROPERTY_DIALOG_VISIBILITY = "property_dialog_visibility";
    public static final String PROPERTY_DIALOG_EXPANDED = "property_dialog_expanded";
    public static final String PROPERTY_DIALOG_POSITION = "property_dialog_position";
    public static final String PROPERTY_DIALOG_INITIAL_POSITION = "property_dialog_initial_position";
    public static final String PROPERTY_CONFIGURATION_ITEMS_LIST = "property_configuration_items_list";
    public static final String PROPERTY_CLEAR_CONFIGURATION_VARIABLES = "property_clear_configuration_variables";
    public static final String PROPERTY_FAST_MODE = "property_fast_mode";
    public static final String PROPERTY_FAST_MODE_BUTTON = "property_fast_mode_button";
    public static final String PROPERTY_ACTION_SET_TEXT = "property_action_set_text";
    public static final String PROPERTY_ACTION_PASTE = "property_action_paste";

    public static final int EVENT_TYPE_UNKNOWN = 0;
    public static final int EVENT_TYPE_VIEW_LONG_CLICKED = 2;
    public static final int EVENT_TYPE_VIEW_CLICKED = 1;
    public static final int EVENT_TYPE_VIEW_FOCUSED = 8;

    public static final int VIEW_TYPE_SELECTED_ITEM = 1;
    public static final int VIEW_TYPE_NORMAL_ITEM = 2;

    private static final int MAX_CLICK_DURATION = 200;

    private PropertyChangedListener propertyChangedListener;
    private final FillTheFormDialogModelHelper helper;

    // Visibility and expanded status
    private boolean expandIconVisible = true;
    private boolean dialogVisible = false;
    private boolean dialogExpanded = false;

    // Screen and dialog size
    private int screenWidth;
    private int screenHeight;
    private int statusBarHeight;
    private int expandedDialogWidth;
    private int expandedDialogHeight;
    private int normalDialogWidth;
    private int normalDialogHeight;

    // Dialog position
    private int dialogPositionX;
    private int dialogPositionY;

    // Touch events data
    private int initialDialogPositionX;
    private int initialDialogPositionY;
    private float initialTouchEventX;
    private float initialTouchEventY;
    private long startClickTime;

    // Configuration items data
    private List<ConfigurationItem> sortedConfigurationItems;
    private ConfigurationItem selectedConfigItem;
    private String configurationVariablePattern;

    // Profiles
    private List<String> profiles;
    private int selectedProfileIndex;

    // Fast mode
    private boolean fastModeEnabled;

    public FillTheFormDialogModel(FillTheFormDialogModelHelper helper) {
        this.helper = helper;
    }

    // Select configuration item

    public void onConfigurationItemClicked(int position) {
        setSelectedConfigItem(position);
        notifyPropertyChanged(PROPERTY_ACTION_SET_TEXT);
    }

    public void onConfigurationItemLongClicked(int position) {
        setSelectedConfigItem(position);
        notifyPropertyChanged(PROPERTY_ACTION_PASTE);
    }

    public String getSelectedConfigItemValue() {
        ConfigurationItem preparedConfigurationItem = prepareSelectedConfigurationItemForInput();
        if (preparedConfigurationItem != null) {
            return preparedConfigurationItem.getValue();
        }
        return null;
    }

    private void setSelectedConfigItem(int position) {
        if (sortedConfigurationItems != null) {
            setSelectedConfigItem(sortedConfigurationItems.get(position));
        } else {
            setSelectedConfigItem(null);
        }
    }

    private void setSelectedConfigItem(ConfigurationItem selectedConfigItem) {
        this.selectedConfigItem = selectedConfigItem;
    }

    private void selectItemWithNextProfile() {
        if (sortedConfigurationItems == null
                || selectedConfigItem == null
                || selectedConfigItem.getProfile() == null
                || profiles == null
                || profiles.isEmpty()) {
            return;
        }
        selectedProfileIndex = (selectedProfileIndex + 1) % profiles.size();
        String selectedProfile = profiles.get(selectedProfileIndex);
        for (int i = 0; i < sortedConfigurationItems.size(); i++) {
            ConfigurationItem item = sortedConfigurationItems.get(i);
            if (item.getProfile() != null && item.getProfile().equals(selectedProfile)) {
                setSelectedConfigItem(item);
                break;
            }
        }
    }

    // Profiles

    public void setProfiles(List<String> profiles) {
        this.profiles = profiles;
        selectedProfileIndex = 0;
    }

    public void selectNextProfile() {
        selectItemWithNextProfile();
    }

    // Configuration items data

    private void setSortedConfigurationItems(List<ConfigurationItem> selectedConfigurationItems) {
        final List<ConfigurationItem> sortedConfigurationItems = new ArrayList<>(selectedConfigurationItems);

        // Sort the list by last used profile
        if (selectedConfigItem != null && selectedConfigItem.getProfile() != null) {
            // If the same field is selected again - the last selected item should go on top
            boolean removedSelectedItem = sortedConfigurationItems.remove(selectedConfigItem);

            // Last used profile group should be on top
            Collections.sort(sortedConfigurationItems, new Comparator<ConfigurationItem>() {
                @Override
                public int compare(ConfigurationItem lhs, ConfigurationItem rhs) {
                    if (lhs.getProfile() != null
                            && lhs.getProfile().equalsIgnoreCase(selectedConfigItem.getProfile())
                            && rhs.getProfile() != null
                            && !rhs.getProfile().equals(selectedConfigItem.getProfile())) {
                        return -1;
                    } else {
                        return 0;
                    }
                }
            });

            // Put the last selected item on top
            if (removedSelectedItem) {
                sortedConfigurationItems.add(0, selectedConfigItem);
            }
        }

        this.sortedConfigurationItems = sortedConfigurationItems;
    }

    public List<ConfigurationItem> getSortedConfigurationItems() {
        return sortedConfigurationItems;
    }

    public int getItemsCount() {
        return sortedConfigurationItems.size();
    }

    public int getSortedConfigItemType(int position) {
        if (sortedConfigurationItems != null && position == sortedConfigurationItems.indexOf(selectedConfigItem)) {
            return VIEW_TYPE_SELECTED_ITEM;
        } else {
            return VIEW_TYPE_NORMAL_ITEM;
        }
    }

    public ConfigurationItem getConfigurationItem(int position) {
        ConfigurationItem configurationItem = sortedConfigurationItems.get(position);
        return prepareConfigurationItemForDialogList(configurationItem);
    }

    private ConfigurationItem prepareConfigurationItemForDialogList(ConfigurationItem configurationItem) {
        if (helper != null && configurationItem != null && configurationItem.getValue() == null) {
            if (configurationVariablePattern != null) {
                String newConfigurationItemValue = replaceVariableKeysWithValues(configurationItem.getRawValue());
                configurationItem.setValue(newConfigurationItemValue);
            } else {
                configurationItem.setValue(configurationItem.getRawValue());
            }
        }
        return configurationItem;
    }

    private ConfigurationItem prepareSelectedConfigurationItemForInput() {
        if (selectedConfigItem != null) {
            // Check if the selected config item raw value is configuration variable key
            if (helper != null && helper.isConfigurationVariableKey(selectedConfigItem.getRawValue())) {
                String configurationVariableValue = helper.getConfigurationVariableValue(selectedConfigItem.getRawValue());
                if (configurationVariableValue != null) {
                    ConfigurationItem preparedConfigurationItem = new ConfigurationItem(selectedConfigItem);
                    preparedConfigurationItem.setValue(configurationVariableValue);
                    return preparedConfigurationItem;
                }
            } else if (selectedConfigItem.getValue() == null) {
                ConfigurationItem preparedConfigurationItem = new ConfigurationItem(prepareConfigurationItemForDialogList(selectedConfigItem));
                selectedConfigItem.setValue(null);
                return preparedConfigurationItem;
            } else {
                ConfigurationItem preparedConfigurationItem = new ConfigurationItem(selectedConfigItem);
                selectedConfigItem.setValue(null);
                return preparedConfigurationItem;
            }
        }
        return null;
    }

    private String replaceVariableKeysWithValues(String text) {
        String newText = text.replace("\\n", "\n");
        // If entry contains variables then we will replace them with appropriate values
        Pattern pattern = Pattern.compile(configurationVariablePattern);
        Matcher m = pattern.matcher(newText);
        StringBuffer sb = new StringBuffer(newText.length());
        while (m.find() && m.groupCount() > 0) {
            String variableKey = m.group(1);
            String value = helper.getConfigurationVariableValue(variableKey);
            if (value != null) {
                m.appendReplacement(sb, Matcher.quoteReplacement(value));
            }
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public void setConfigurationVariablePattern(String configurationVariablePattern) {
        this.configurationVariablePattern = configurationVariablePattern;
    }

    // Dialog position

    public void setDialogPosition(int x, int y) {
        this.dialogPositionX = x;
        this.dialogPositionY = y;
        notifyPropertyChanged(PROPERTY_DIALOG_POSITION);
    }

    public int getDialogPositionX() {
        return dialogPositionX;
    }

    public int getDialogPositionY() {
        return dialogPositionY;
    }

    // Touch events

    public void onActionMove(float x, float y) {
        int newPositionX = initialDialogPositionX + (int) (x - initialTouchEventX);
        int newPositionY = initialDialogPositionY + (int) (y - initialTouchEventY);
        if (newPositionX < 0) {
            newPositionX = 0;
        }
        int maximumXValue;
        if (isDialogExpanded()) {
            maximumXValue = screenWidth - expandedDialogWidth;
        } else {
            maximumXValue = screenWidth - normalDialogWidth;
        }
        if (newPositionX > maximumXValue) {
            newPositionX = maximumXValue;
        }
        if (newPositionY < 0) {
            newPositionY = 0;
        }
        int maximumYValue;
        if (isDialogExpanded()) {
            maximumYValue = screenHeight - expandedDialogHeight;
        } else {
            maximumYValue = screenHeight - normalDialogHeight;
        }
        if (newPositionY > maximumYValue) {
            newPositionY = maximumYValue;
        }
        setDialogPosition(newPositionX, newPositionY);
    }

    public void onActionUp() {
        long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
        if (clickDuration < MAX_CLICK_DURATION && !isDialogExpanded()) {
            setDialogExpanded(true);
            int maximumXValue = screenWidth - expandedDialogWidth;
            if (dialogPositionX > maximumXValue) {
                dialogPositionX = maximumXValue;
            }
            int maximumYValue = screenHeight - expandedDialogHeight;
            if (dialogPositionY > maximumYValue) {
                dialogPositionY = maximumYValue;
            }
            notifyPropertyChanged(PROPERTY_DIALOG_POSITION);
        }
    }

    public void setInitialTouchEvent(float x, float y) {
        this.initialTouchEventX = x;
        this.initialTouchEventY = y;
        this.startClickTime = Calendar.getInstance().getTimeInMillis();
    }

    public void setInitialDialogPosition(int x, int y) {
        this.initialDialogPositionX = x;
        this.initialDialogPositionY = y;
        setDialogPosition(x, y);
    }

    // Screen dimensions

    public void setScreenDimensions(int screenWidth, int screenHeight) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight - statusBarHeight;
    }

    public void setStatusBarHeight(int statusBarHeight) {
        this.statusBarHeight = statusBarHeight;
    }

    // Expanded and normal dialog dimensions

    public void setExpandedDialogDimensions(int expandedDialogWidth, int expandedDialogHeight) {
        this.expandedDialogWidth = expandedDialogWidth;
        this.expandedDialogHeight = expandedDialogHeight;
    }

    public int getExpandedDialogWidth() {
        return expandedDialogWidth;
    }

    public int getExpandedDialogHeight() {
        return expandedDialogHeight;
    }

    public void setNormalDialogDimensions(int normalDialogWidth, int normalDialogHeight) {
        this.normalDialogWidth = normalDialogWidth;
        this.normalDialogHeight = normalDialogHeight;
    }

    public int getNormalDialogWidth() {
        return normalDialogWidth;
    }

    public int getNormalDialogHeight() {
        return normalDialogHeight;
    }

    // Show dialog on screen

    public void showDialog(int modelEventType, List<ConfigurationItem> selectedConfigurationItems) {
        if (isDialogVisible() && isFastModeEnabled() && modelEventType == EVENT_TYPE_VIEW_LONG_CLICKED) {
            return;
        } else if (!isDialogVisible() && (modelEventType == EVENT_TYPE_VIEW_CLICKED || modelEventType == EVENT_TYPE_VIEW_FOCUSED)) {
            return;
        }
        notifyPropertyChanged(PROPERTY_CLEAR_CONFIGURATION_VARIABLES);
        setSortedConfigurationItems(selectedConfigurationItems);
        if (!isDialogVisible()) {
            setExpandIconVisible(true);
            setDialogVisible(true);
            notifyPropertyChanged(PROPERTY_DIALOG_INITIAL_POSITION);
        }
        notifyPropertyChanged(PROPERTY_CONFIGURATION_ITEMS_LIST);
        if (isFastModeEnabled() || modelEventType == EVENT_TYPE_VIEW_LONG_CLICKED) {
            setSelectedConfigItem(sortedConfigurationItems.get(0));
            notifyPropertyChanged(PROPERTY_ACTION_SET_TEXT);
        }
    }

    // Dialog visibility

    public boolean isDialogVisible() {
        return dialogVisible;
    }

    private void setDialogVisible(boolean dialogVisible) {
        this.dialogVisible = dialogVisible;
        notifyPropertyChanged(PROPERTY_DIALOG_VISIBILITY);
    }

    public void hideDialog() {
        onCloseButtonClicked();
    }

    // Dialog expanded

    public boolean isDialogExpanded() {
        return dialogExpanded;
    }

    private void setDialogExpanded(boolean dialogExpanded) {
        this.dialogExpanded = dialogExpanded;
        setExpandIconVisible(!dialogExpanded);
        notifyPropertyChanged(PROPERTY_DIALOG_EXPANDED);
    }

    // Close button

    public void onCloseButtonClicked() {
        if (isDialogVisible()) {
            setDialogVisible(false);
            setDialogExpanded(false);
            notifyPropertyChanged(PROPERTY_CLOSE_BUTTON);
        }
    }

    // Minimize button

    public void onMinimizeButtonClicked() {
        setDialogExpanded(false);
        notifyPropertyChanged(PROPERTY_MINIMIZE_BUTTON);
    }

    // Open fill the form app button

    public void onOpenFillTheFormAppButtonClicked() {
        if (isDialogVisible()) {
            onCloseButtonClicked();
            notifyPropertyChanged(PROPERTY_OPEN_FILL_THE_FORM_APP_BUTTON);
        }
    }

    // Expand icon

    public boolean isExpandIconVisible() {
        return expandIconVisible;
    }

    private void setExpandIconVisible(boolean expandIconVisible) {
        this.expandIconVisible = expandIconVisible;
        notifyPropertyChanged(PROPERTY_EXPAND_ICON);
        notifyPropertyChanged(PROPERTY_EXPAND_ICON_FAST_MODE);
    }

    // Fast mode

    public void toggleFastMode() {
        setFastModeEnabled(!fastModeEnabled);
    }

    public void setFastModeEnabled(boolean enabled) {
        boolean saveFastModeToSharedPrefs = fastModeEnabled != enabled;
        this.fastModeEnabled = enabled;
        if (saveFastModeToSharedPrefs) {
            notifyPropertyChanged(PROPERTY_FAST_MODE);
        }
        notifyPropertyChanged(PROPERTY_FAST_MODE_BUTTON);
    }

    public boolean isFastModeEnabled() {
        return fastModeEnabled;
    }

    // Property changes

    public void setPropertyChangedListener(PropertyChangedListener propertyChangedListener) {
        this.propertyChangedListener = propertyChangedListener;
    }

    private void notifyPropertyChanged(String property) {
        if (propertyChangedListener != null) {
            propertyChangedListener.onPropertyChanged(property);
        }
    }
}
