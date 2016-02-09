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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Tests for FillTheFormDialogModel.
 */
@RunWith(PowerMockRunner.class)
public class FillTheFormDialogModelTest {

    // Nexus 5 dimensions
    private static final int SCREEN_WIDTH_PX = 1080;
    private static final int SCREEN_HEIGHT_PX = 1776;
    private static final int STATUS_BAR_HEIGHT_PX = 72;
    private static final int NORMAL_DIALOG_WIDTH_PX = 135;
    private static final int NORMAL_DIALOG_HEIGHT_PX = 135;
    private static final int EXPANDED_DIALOG_WIDTH_PX = 840;
    private static final int EXPANDED_DIALOG_HEIGHT_PX = 630;

    private PropertyChangedListener propertyChangedListener;
    private FillTheFormDialogModel model;

    @Before
    public void setUp() throws Exception {
        propertyChangedListener = mock(PropertyChangedListener.class);
        FillTheFormDialogModel.FillTheFormDialogModelHelper helper = createModelHelper();
        model = new FillTheFormDialogModel(helper);
        model.setPropertyChangedListener(propertyChangedListener);

        // Set dialog dimensions
        model.setNormalDialogDimensions(NORMAL_DIALOG_WIDTH_PX, NORMAL_DIALOG_HEIGHT_PX);
        model.setExpandedDialogDimensions(EXPANDED_DIALOG_WIDTH_PX, EXPANDED_DIALOG_HEIGHT_PX);
        model.setStatusBarHeight(STATUS_BAR_HEIGHT_PX);

        // Set configurationVariablePattern
        model.setConfigurationVariablePattern("&(\\w+);");
    }

    private FillTheFormDialogModel.FillTheFormDialogModelHelper createModelHelper() {
        return new FillTheFormDialogModel.FillTheFormDialogModelHelper() {

            private boolean firstNameAlreadyReturned;
            private boolean deviceModelAlreadyReturned;

            @Override
            public boolean isConfigurationVariableKey(String variableKey) {
                switch (variableKey) {
                    case "device_model":
                    case "device_manufacturer":
                    case "random_first_name":
                    case "random_last_name":
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public String getConfigurationVariableValue(String variableKey) {
                switch (variableKey) {
                    case "device_model":
                        if (deviceModelAlreadyReturned) {
                            deviceModelAlreadyReturned = false;
                            return "Nexus 9";
                        }
                        deviceModelAlreadyReturned = true;
                        return "Nexus 42";
                    case "device_manufacturer":
                        return "Google";
                    case "random_first_name":
                        if (firstNameAlreadyReturned) {
                            firstNameAlreadyReturned = false;
                            return "Peter";
                        }
                        firstNameAlreadyReturned = true;
                        return "Luke";
                    case "random_last_name":
                        return "Skywalker";
                    default:
                        return null;
                }
            }
        };
    }

    private List<ConfigurationItem> createSelectedConfigurationItems() {
        List<ConfigurationItem> selectedConfigurationItems = new ArrayList<>();
        selectedConfigurationItems.add(new ConfigurationItem("first_name", "myprofile", "Ivan"));
        selectedConfigurationItems.add(new ConfigurationItem("first_name", "other_profile", "Max"));
        selectedConfigurationItems.add(new ConfigurationItem("last_name", "other_profile", "Mustermann"));
        selectedConfigurationItems.add(new ConfigurationItem("last_name", "myprofile", "Jukic"));
        selectedConfigurationItems.add(new ConfigurationItem("last_name", "myprofile", "I have &device_model;\nfrom &device_manufacturer;"));
        return selectedConfigurationItems;
    }

    @Test
    public void testShowDialogWhenSelectedConfigItemIsNull() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        assertTrue(model.isDialogVisible());
        assertEquals(selectedConfigurationItems, model.getSortedConfigurationItems());
        assertEquals("Ivan", model.getConfigurationItem(0).getValue());
        assertEquals("Max", model.getConfigurationItem(1).getValue());
        assertEquals("Mustermann", model.getConfigurationItem(2).getValue());
        assertEquals("Jukic", model.getConfigurationItem(3).getValue());
        assertEquals("I have Nexus 42\nfrom Google", model.getConfigurationItem(4).getValue());
        assertEquals(5, model.getItemsCount());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CLEAR_CONFIGURATION_VARIABLES);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_INITIAL_POSITION);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEMS_LIST);
        assertEquals(selectedConfigurationItems.get(0).getValue(), model.getSelectedConfigItemValue());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
    }

    @Test
    public void testShowDialogWhenSelectedConfigItemIsNotNull() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        assertTrue(model.isDialogVisible());
        assertEquals("Ivan", model.getConfigurationItem(0).getValue());
        assertEquals("Jukic", model.getConfigurationItem(1).getValue());
        assertEquals("I have Nexus 42\nfrom Google", model.getConfigurationItem(2).getValue());
        assertEquals("Max", model.getConfigurationItem(3).getValue());
        assertEquals("Mustermann", model.getConfigurationItem(4).getValue());
        assertEquals(selectedConfigurationItems.get(0).getValue(), model.getSelectedConfigItemValue());
        verify(propertyChangedListener, times(2)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CLEAR_CONFIGURATION_VARIABLES);
        verify(propertyChangedListener, times(2)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_INITIAL_POSITION);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON_FAST_MODE);
        verify(propertyChangedListener, times(2)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEMS_LIST);
    }

    @Test
    public void testShowDialogWhenSelectedConfigItemIsNotTheFirstItemFromTheList() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);
        model.onConfigurationItemClicked(3);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        assertTrue(model.isDialogVisible());
        assertEquals("Max", model.getConfigurationItem(0).getValue());
        assertEquals("Mustermann", model.getConfigurationItem(1).getValue());
        assertEquals("Ivan", model.getConfigurationItem(2).getValue());
        assertEquals("Jukic", model.getConfigurationItem(3).getValue());
        assertEquals("I have Nexus 42\nfrom Google", model.getConfigurationItem(4).getValue());
        assertEquals("Max", model.getSelectedConfigItemValue());
        verify(propertyChangedListener, times(3)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CLEAR_CONFIGURATION_VARIABLES);
        verify(propertyChangedListener, times(4)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_INITIAL_POSITION);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON_FAST_MODE);
        verify(propertyChangedListener, times(3)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEMS_LIST);
    }

    @Test
    public void testShowDialogShouldKeepTheLastSelectedItemOnTop() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);
        model.onConfigurationItemClicked(3);
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        assertTrue(model.isDialogVisible());
        assertEquals("Max", model.getConfigurationItem(0).getValue());
        assertEquals("Mustermann", model.getConfigurationItem(1).getValue());
        assertEquals("Ivan", model.getConfigurationItem(2).getValue());
        assertEquals("Jukic", model.getConfigurationItem(3).getValue());
        assertEquals("I have Nexus 42\nfrom Google", model.getConfigurationItem(4).getValue());
        assertEquals("Max", model.getSelectedConfigItemValue());
        verify(propertyChangedListener, times(4)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CLEAR_CONFIGURATION_VARIABLES);
        verify(propertyChangedListener, times(5)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_INITIAL_POSITION);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON_FAST_MODE);
        verify(propertyChangedListener, times(4)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEMS_LIST);
    }

    @Test
    public void testWhenConfigurationVariablePatternIsNull() {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();
        model.setConfigurationVariablePattern(null);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        assertTrue(model.isDialogVisible());
        assertEquals(selectedConfigurationItems, model.getSortedConfigurationItems());
        assertEquals("Ivan", model.getConfigurationItem(0).getValue());
        assertEquals("Max", model.getConfigurationItem(1).getValue());
        assertEquals("Mustermann", model.getConfigurationItem(2).getValue());
        assertEquals("Jukic", model.getConfigurationItem(3).getValue());
        assertEquals("I have &device_model;\nfrom &device_manufacturer;", model.getConfigurationItem(4).getValue());
        assertEquals(5, model.getItemsCount());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CLEAR_CONFIGURATION_VARIABLES);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_INITIAL_POSITION);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEMS_LIST);
        assertEquals(selectedConfigurationItems.get(0).getValue(), model.getSelectedConfigItemValue());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
    }

    @Test
    public void testWhenConfigurationVariablePatternIsEmptyString() {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();
        model.setConfigurationVariablePattern("");

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        assertTrue(model.isDialogVisible());
        assertEquals(selectedConfigurationItems, model.getSortedConfigurationItems());
        assertEquals("Ivan", model.getConfigurationItem(0).getValue());
        assertEquals("Max", model.getConfigurationItem(1).getValue());
        assertEquals("Mustermann", model.getConfigurationItem(2).getValue());
        assertEquals("Jukic", model.getConfigurationItem(3).getValue());
        assertEquals("I have &device_model;\nfrom &device_manufacturer;", model.getConfigurationItem(4).getValue());
        assertEquals(5, model.getItemsCount());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CLEAR_CONFIGURATION_VARIABLES);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_INITIAL_POSITION);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEMS_LIST);
        assertEquals(selectedConfigurationItems.get(0).getValue(), model.getSelectedConfigItemValue());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
    }

    @Test
    public void testOnConfigurationItemClickedWhenSelectedConfigItemIsNull() throws Exception {
        // run
        model.onConfigurationItemClicked(0);

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
        assertEquals(null, model.getSelectedConfigItemValue());
    }

    @Test
    public void testOnConfigurationItemClickedWhenSelectedConfigItemIsNotNull() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // run
        model.onConfigurationItemClicked(2);

        // verify
        verify(propertyChangedListener, times(2)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
        assertEquals(model.getConfigurationItem(2).getValue(), model.getSelectedConfigItemValue());
    }

    @Test
    public void testGetSelectedConfigItemValueShouldReturnNull() throws Exception {
        // run
        String value = model.getSelectedConfigItemValue();

        // verify
        assertEquals(null, value);
    }

    @Test
    public void testGetSelectedConfigItemValueShouldReturnValue() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // run
        String value = model.getSelectedConfigItemValue();

        // verify
        assertEquals("Ivan", value);
    }

    @Test
    public void testGetSelectedConfigItemValueShouldReturnRandomValueForFirstName() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = new ArrayList<>();
        selectedConfigurationItems.add(new ConfigurationItem("first_name", "myprofile", "random_first_name"));

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);
        String value = model.getSelectedConfigItemValue();

        // verify
        assertEquals("Luke", value);
    }

    @Test
    public void testGetSelectedConfigItemValueShouldReturnRandomValueForLastName() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = new ArrayList<>();
        selectedConfigurationItems.add(new ConfigurationItem("last_name", "myprofile", "random_last_name"));

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);
        String value = model.getSelectedConfigItemValue();

        // verify
        assertEquals("Skywalker", value);
    }

    @Test
    public void testGetSortedConfigItemTypeWhenSortedConfigurationItemsIsNull() throws Exception {
        // run
        int viewType = model.getSortedConfigItemType(0);

        // verify
        assertEquals(FillTheFormDialogModel.VIEW_TYPE_NORMAL_ITEM, viewType);
    }

    @Test
    public void testGetSortedConfigItemTypeShouldReturnViewTypeNormal() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = new ArrayList<>();
        selectedConfigurationItems.add(new ConfigurationItem("first_name", "myprofile", "random_first_name"));
        selectedConfigurationItems.add(new ConfigurationItem("last_name", "myprofile", "random_last_name"));

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);
        int viewType = model.getSortedConfigItemType(1);

        // verify
        assertEquals(FillTheFormDialogModel.VIEW_TYPE_NORMAL_ITEM, viewType);
    }

    @Test
    public void testGetSortedConfigItemTypeShouldReturnViewTypeSelected() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = new ArrayList<>();
        selectedConfigurationItems.add(new ConfigurationItem("first_name", "myprofile", "random_first_name"));
        selectedConfigurationItems.add(new ConfigurationItem("last_name", "myprofile", "random_last_name"));

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);
        int viewType = model.getSortedConfigItemType(0);

        // verify
        assertEquals(FillTheFormDialogModel.VIEW_TYPE_SELECTED_ITEM, viewType);
    }

    @Test
    public void testGetSortedConfigurationItems() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        assertEquals(selectedConfigurationItems, model.getSortedConfigurationItems());
    }

    @Test
    public void testSelectedItemValueShouldAlternate() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = new ArrayList<>();
        selectedConfigurationItems.add(new ConfigurationItem("first_name", "myprofile", "&random_first_name;"));

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        String editTextContent = model.getSelectedConfigItemValue();
        assertEquals("Luke", editTextContent);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        editTextContent = model.getSelectedConfigItemValue();
        assertEquals("Peter", editTextContent);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        editTextContent = model.getSelectedConfigItemValue();
        assertEquals("Luke", editTextContent);
    }

    @Test
    public void testSelectedItemValueShouldAlternateAndDialogContentShouldNotChange() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = new ArrayList<>();
        selectedConfigurationItems.add(new ConfigurationItem("first_name", "myprofile", "random_first_name"));

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);
        model.getConfigurationItem(0);

        // verify
        String editTextContent = model.getSelectedConfigItemValue();
        assertEquals("Luke", editTextContent);
        String dialogListSelectedItem = model.getConfigurationItem(0).getValue();
        assertEquals("random_first_name", dialogListSelectedItem);

        // run
        model.onConfigurationItemClicked(0);

        // verify
        editTextContent = model.getSelectedConfigItemValue();
        assertEquals("Peter", editTextContent);
        dialogListSelectedItem = model.getConfigurationItem(0).getValue();
        assertEquals("random_first_name", dialogListSelectedItem);

        // run
        model.onConfigurationItemClicked(0);

        // verify
        editTextContent = model.getSelectedConfigItemValue();
        assertEquals("Luke", editTextContent);
        dialogListSelectedItem = model.getConfigurationItem(0).getValue();
        assertEquals("random_first_name", dialogListSelectedItem);
    }

    @Test
    public void testSelectedItemAndDialogContentValuesShouldAlternate() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = new ArrayList<>();
        selectedConfigurationItems.add(new ConfigurationItem("user_description", "myprofile", "&random_first_name; has &device_model;"));

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);
        model.getConfigurationItem(0);

        // verify
        String editTextContent = model.getSelectedConfigItemValue();
        assertEquals("Luke has Nexus 42", editTextContent);
        String dialogListSelectedItem = model.getConfigurationItem(0).getValue();
        assertEquals("Peter has Nexus 9", dialogListSelectedItem);

        // run
        model.onConfigurationItemClicked(0);

        // verify
        editTextContent = model.getSelectedConfigItemValue();
        assertEquals("Peter has Nexus 9", editTextContent);
        dialogListSelectedItem = model.getConfigurationItem(0).getValue();
        assertEquals("Luke has Nexus 42", dialogListSelectedItem);

        // run
        model.onConfigurationItemClicked(0);

        // verify
        editTextContent = model.getSelectedConfigItemValue();
        assertEquals("Luke has Nexus 42", editTextContent);
        dialogListSelectedItem = model.getConfigurationItem(0).getValue();
        assertEquals("Peter has Nexus 9", dialogListSelectedItem);
    }

    @Test
    public void testToggleFastMode() throws Exception {
        // prepare
        model.setFastModeEnabled(true);

        // run
        model.toggleFastMode();

        // verify
        assertFalse(model.isFastModeEnabled());
    }

    @Test
    public void testSetFastModeEnabled() throws Exception {
        // run
        model.setFastModeEnabled(true);

        // verify
        assertTrue(model.isFastModeEnabled());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_FAST_MODE);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_FAST_MODE_BUTTON);

        // run
        model.setFastModeEnabled(true);

        // verify
        assertTrue(model.isFastModeEnabled());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_FAST_MODE);
        verify(propertyChangedListener, times(2)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_FAST_MODE_BUTTON);

        // run
        model.setFastModeEnabled(false);

        // verify
        assertFalse(model.isFastModeEnabled());
        verify(propertyChangedListener, times(2)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_FAST_MODE);
        verify(propertyChangedListener, times(3)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_FAST_MODE_BUTTON);
    }

    @Test
    public void testClickAndFocusedEventsShouldBeIgnoredWhenDialogIsNotVisible() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_CLICKED, selectedConfigurationItems);

        // verify
        assertFalse(model.isDialogVisible());
        assertFalse(model.isDialogExpanded());
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CLEAR_CONFIGURATION_VARIABLES);
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON);
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON_FAST_MODE);
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_VISIBILITY);
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_INITIAL_POSITION);
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEMS_LIST);
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_FOCUSED, selectedConfigurationItems);

        // verify
        assertFalse(model.isDialogVisible());
        assertFalse(model.isDialogExpanded());
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CLEAR_CONFIGURATION_VARIABLES);
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON);
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON_FAST_MODE);
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_VISIBILITY);
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_INITIAL_POSITION);
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEMS_LIST);
        verify(propertyChangedListener, times(0)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
    }

    @Test
    public void testLongClickShouldBeIgnoredWhenDialogIsVisibleAndInFastMode() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        assertTrue(model.isDialogVisible());
        assertFalse(model.isDialogExpanded());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CLEAR_CONFIGURATION_VARIABLES);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON_FAST_MODE);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_VISIBILITY);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_INITIAL_POSITION);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEMS_LIST);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);

        // prepare
        model.setFastModeEnabled(true);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        assertTrue(model.isDialogVisible());
        assertFalse(model.isDialogExpanded());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CLEAR_CONFIGURATION_VARIABLES);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON_FAST_MODE);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_VISIBILITY);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_INITIAL_POSITION);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEMS_LIST);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
    }

    @Test
    public void testItemShouldBeSelectedTwiceAfterTwoLongClicksInNormalMode() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        verify(propertyChangedListener, times(2)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
    }

    @Test
    public void testItemShouldBeSelectedOnceAfterTwoLongClicksInFastMode() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();
        model.setFastModeEnabled(true);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
    }

    @Test
    public void testItemShouldBeSelectedOnceAfterLongClickThenFocusChangeAndClickInNormalMode() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_CLICKED, selectedConfigurationItems);

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_FOCUSED, selectedConfigurationItems);

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
    }

    @Test
    public void testItemShouldBeSelectedThreeTimesAfterLongClickThenFocusChangeAndClickInFastMode() throws Exception {
        // prepare
        List<ConfigurationItem> selectedConfigurationItems = createSelectedConfigurationItems();
        model.setFastModeEnabled(true);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_LONG_CLICKED, selectedConfigurationItems);

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_CLICKED, selectedConfigurationItems);

        // verify
        verify(propertyChangedListener, times(2)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);

        // run
        model.showDialog(FillTheFormDialogModel.EVENT_TYPE_VIEW_FOCUSED, selectedConfigurationItems);

        // verify
        verify(propertyChangedListener, times(3)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED);
    }

    // Dialog position

    @Test
    public void testSetDialogPosition() throws Exception {
        // run
        model.setDialogPosition(42, 24);

        // verify
        assertEquals(42, model.getDialogPositionX());
        assertEquals(24, model.getDialogPositionY());
        assertFalse(model.isDialogExpanded());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_POSITION);
    }

    @Test
    public void testSetInitialDialogPosition() throws Exception {
        // run
        model.setInitialDialogPosition(42, 24);

        // verify
        assertEquals(42, model.getDialogPositionX());
        assertEquals(24, model.getDialogPositionY());
        assertFalse(model.isDialogExpanded());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_POSITION);
    }

    // Touch events tests

    @Test
    public void testOnActionMoveWhenDialogIsInitiallyInTopLeftCornerAndNotExpanded() throws Exception {
        // prepare
        model.setScreenDimensions(SCREEN_WIDTH_PX, SCREEN_HEIGHT_PX);
        model.setInitialDialogPosition(0, 0);
        model.setInitialTouchEvent(5, 5);

        // run
        model.onActionMove(25, 55);

        // verify
        assertEquals(20, model.getDialogPositionX());
        assertEquals(50, model.getDialogPositionY());
        assertFalse(model.isDialogExpanded());
        verify(propertyChangedListener, times(2)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_POSITION);
    }

    @Test
    public void testOnActionMoveWhenDialogIsInitiallyInTopLeftCornerAndThenExpanded() throws Exception {
        // prepare
        model.setScreenDimensions(SCREEN_WIDTH_PX, SCREEN_HEIGHT_PX);
        model.setInitialDialogPosition(0, 0);
        model.setInitialTouchEvent(5, 5);

        // run
        model.onActionMove(25, 55);
        model.onActionUp();

        // verify
        assertEquals(20, model.getDialogPositionX());
        assertEquals(50, model.getDialogPositionY());
        assertTrue(model.isDialogExpanded());
        assertFalse(model.isExpandIconVisible());
        verify(propertyChangedListener, times(3)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_POSITION);
    }

    @Test
    public void testOnActionMoveWhenDialogIsInitiallyInBottomCornerAndNotExpanded() throws Exception {
        // prepare
        model.setScreenDimensions(SCREEN_WIDTH_PX, SCREEN_HEIGHT_PX);
        model.setInitialDialogPosition(SCREEN_WIDTH_PX, SCREEN_HEIGHT_PX);
        model.setInitialTouchEvent(SCREEN_WIDTH_PX - 25, SCREEN_HEIGHT_PX - 25);

        // run
        model.onActionMove(SCREEN_WIDTH_PX - 5, SCREEN_HEIGHT_PX - 5);

        // verify
        assertEquals(SCREEN_WIDTH_PX - NORMAL_DIALOG_WIDTH_PX, model.getDialogPositionX());
        assertEquals(SCREEN_HEIGHT_PX - STATUS_BAR_HEIGHT_PX - NORMAL_DIALOG_HEIGHT_PX, model.getDialogPositionY());
        assertFalse(model.isDialogExpanded());
        verify(propertyChangedListener, times(2)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_POSITION);
    }

    @Test
    public void testOnActionMoveWhenDialogIsInitiallyInBottomCornerAndThenExpanded() throws Exception {
        // prepare
        model.setScreenDimensions(SCREEN_WIDTH_PX, SCREEN_HEIGHT_PX);
        model.setInitialDialogPosition(SCREEN_WIDTH_PX, SCREEN_HEIGHT_PX);
        model.setInitialTouchEvent(SCREEN_WIDTH_PX - 25, SCREEN_HEIGHT_PX - 25);

        // run
        model.onActionMove(SCREEN_WIDTH_PX - 5, SCREEN_HEIGHT_PX - 5);
        model.onActionUp();

        // verify
        assertEquals(SCREEN_WIDTH_PX - EXPANDED_DIALOG_WIDTH_PX, model.getDialogPositionX());
        assertEquals(SCREEN_HEIGHT_PX - STATUS_BAR_HEIGHT_PX - EXPANDED_DIALOG_HEIGHT_PX, model.getDialogPositionY());
        assertTrue(model.isDialogExpanded());
        assertFalse(model.isExpandIconVisible());
        verify(propertyChangedListener, times(3)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_POSITION);
    }

    @Test
    public void testOnActionMoveWhenDialogIsNotExpandedAndMovedToIllegalCoordinates() throws Exception {
        // prepare
        model.setScreenDimensions(SCREEN_WIDTH_PX, SCREEN_HEIGHT_PX);
        model.setInitialDialogPosition(0, 0);
        model.setInitialTouchEvent(5, 5);

        // run
        model.onActionMove(-5, -5);

        // verify
        assertEquals(0, model.getDialogPositionX());
        assertEquals(0, model.getDialogPositionY());
        assertFalse(model.isDialogExpanded());
        verify(propertyChangedListener, times(2)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_POSITION);

        // run
        model.onActionMove(SCREEN_WIDTH_PX + 100, SCREEN_HEIGHT_PX + 100);

        // verify
        assertEquals(SCREEN_WIDTH_PX - NORMAL_DIALOG_WIDTH_PX, model.getDialogPositionX());
        assertEquals(SCREEN_HEIGHT_PX - STATUS_BAR_HEIGHT_PX - NORMAL_DIALOG_HEIGHT_PX, model.getDialogPositionY());
        verify(propertyChangedListener, times(3)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_POSITION);
    }

    @Test
    public void testOnActionMoveWhenDialogIsExpandedAndMovedToIllegalCoordinates() throws Exception {
        // prepare
        model.setScreenDimensions(SCREEN_WIDTH_PX, SCREEN_HEIGHT_PX);
        model.setInitialDialogPosition(0, 0);
        model.setInitialTouchEvent(5, 5);

        // run
        model.onActionUp();
        model.onActionMove(-5, -5);

        // verify
        assertEquals(0, model.getDialogPositionX());
        assertEquals(0, model.getDialogPositionY());
        assertTrue(model.isDialogExpanded());
        assertFalse(model.isExpandIconVisible());
        verify(propertyChangedListener, times(3)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_POSITION);

        // run
        model.onActionMove(SCREEN_WIDTH_PX + 100, SCREEN_HEIGHT_PX + 100);

        // verify
        assertEquals(SCREEN_WIDTH_PX - EXPANDED_DIALOG_WIDTH_PX, model.getDialogPositionX());
        assertEquals(SCREEN_HEIGHT_PX - STATUS_BAR_HEIGHT_PX - EXPANDED_DIALOG_HEIGHT_PX, model.getDialogPositionY());
        assertTrue(model.isDialogExpanded());
        verify(propertyChangedListener, times(4)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_DIALOG_POSITION);
    }

    @Test
    public void testGetExpandedDialogWidth() throws Exception {
        // verify
        assertEquals(EXPANDED_DIALOG_WIDTH_PX, model.getExpandedDialogWidth());
    }

    @Test
    public void testGetExpandedDialogHeight() throws Exception {
        // verify
        assertEquals(EXPANDED_DIALOG_HEIGHT_PX, model.getExpandedDialogHeight());
    }

    @Test
    public void testGetNormalDialogWidth() throws Exception {
        // verify
        assertEquals(NORMAL_DIALOG_WIDTH_PX, model.getNormalDialogWidth());
    }

    @Test
    public void testGetNormalDialogHeight() throws Exception {
        // verify
        assertEquals(NORMAL_DIALOG_HEIGHT_PX, model.getNormalDialogHeight());
    }

    @Test
    public void testIsDialogVisibleShouldReturnFalse() throws Exception {
        // run
        model.onCloseButtonClicked();

        // verify
        assertFalse(model.isDialogVisible());
    }

    @Test
    public void testIsDialogExpandedShouldReturnFalseWhenCloseButtonIsClicked() throws Exception {
        // run
        model.onCloseButtonClicked();

        // verify
        assertFalse(model.isDialogExpanded());
    }

    @Test
    public void testIsDialogExpandedShouldReturnFalseWhenMinimizeButtonIsClicked() throws Exception {
        // run
        model.onMinimizeButtonClicked();

        // verify
        assertFalse(model.isDialogExpanded());
    }


    @Test
    public void testOnCloseButtonClicked() throws Exception {
        // run
        model.onCloseButtonClicked();

        // verify
        assertFalse(model.isDialogVisible());
        assertFalse(model.isDialogExpanded());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CLOSE_BUTTON);
    }

    @Test
    public void testOnMinimizeButtonClicked() throws Exception {
        // run
        model.onMinimizeButtonClicked();

        // verify
        assertFalse(model.isDialogExpanded());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_MINIMIZE_BUTTON);
    }

    @Test
    public void testOnOpenFillTheFormAppButtonClicked() throws Exception {
        // run
        model.onOpenFillTheFormAppButtonClicked();

        // verify
        assertFalse(model.isDialogVisible());
        assertFalse(model.isDialogExpanded());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_CLOSE_BUTTON);
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_OPEN_FILL_THE_FORM_APP_BUTTON);
    }

    @Test
    public void testIsExpandIconVisibleShouldReturnTrueAfterCloseButtonIsClicked() throws Exception {
        // run
        model.onMinimizeButtonClicked();

        // verify
        assertTrue(model.isExpandIconVisible());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON);
    }

    @Test
    public void testIsExpandIconVisibleShouldReturnTrueAfterMinimizeButtonIsClicked() throws Exception {
        // run
        model.onMinimizeButtonClicked();

        // verify
        assertTrue(model.isExpandIconVisible());
        verify(propertyChangedListener, times(1)).onPropertyChanged(FillTheFormDialogModel.PROPERTY_EXPAND_ICON);
    }
}