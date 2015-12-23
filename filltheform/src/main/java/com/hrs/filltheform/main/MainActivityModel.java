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
package com.hrs.filltheform.main;

import android.os.Parcel;
import android.os.Parcelable;

import com.hrs.filltheform.common.PropertyChangedListener;

import java.util.List;

/**
 * This model class holds the state and manages behavior of the MainActivity.
 */
public class MainActivityModel implements Parcelable {

    public static final String PROPERTY_PERMISSIONS_HEADER = "property_permissions_header";

    public static final String PROPERTY_SYSTEM_ALERT_WINDOW_CONTAINER = "property_system_alert_window_container";
    public static final String PROPERTY_SYSTEM_ALERT_WINDOW_PERMISSION = "property_system_alert_window_permission";

    public static final String PROPERTY_LOAD_CONFIGURATION_FILE_CONTAINER = "property_load_configuration_file_container";
    public static final String PROPERTY_LOAD_CONFIGURATION_FILE_BUTTON = "property_load_configuration_file_button";
    public static final String PROPERTY_SELECT_CONFIGURATION_FILE_BUTTON = "property_select_configuration_file_button";
    public static final String PROPERTY_CONFIGURATION_FILE_ATTRIBUTES = "property_configuration_file_attributes";
    public static final String PROPERTY_SAVE_CONFIGURATION_FILE_ATTRIBUTES = "property_save_configuration_file_attributes";
    public static final String PROPERTY_CONFIGURATION_LOADING_IN_PROGRESS = "property_configuration_loading_in_progress";

    public static final String PROPERTY_ACCESSIBILITY_SERVICE_CONTAINER = "property_accessibility_service_container";
    public static final String PROPERTY_ACCESSIBILITY_SERVICE_BUTTON = "property_accessibility_service_button";

    public static final String PROPERTY_LOADED_PACKAGE_NAMES_CONTAINER = "property_loaded_package_names_container";
    public static final String PROPERTY_LOADED_PACKAGE_NAMES_EMPTY = "property_loaded_package_names_empty";
    public static final String PROPERTY_LOADED_PACKAGE_NAMES_LIST = "property_loaded_package_names_list";

    private PropertyChangedListener propertyChangedListener;

    private boolean permissionsHeaderVisible = true;
    private boolean systemAlertWindowContainerVisible = true;
    private boolean accessibilityServiceContainerVisible = true;
    private boolean loadConfigurationFileContainerVisible = false;
    private String configurationFileUri;
    private String configurationFileName;
    private boolean loadedPackageNamesContainerVisible = false;
    private boolean loadedPackageNamesEmptyVisible = false;
    private boolean loadedPackageNamesListVisible = false;
    private List<String> loadedPackageNames;
    private boolean configurationLoadingInProgress = false;

    public MainActivityModel() {
    }

    // Permissions header

    public boolean isPermissionsHeaderVisible() {
        return permissionsHeaderVisible;
    }

    private void setPermissionHeaderVisible(boolean permissionHeaderVisible) {
        this.permissionsHeaderVisible = permissionHeaderVisible;
        notifyPropertyChanged(PROPERTY_PERMISSIONS_HEADER);
    }

    // Permission buttons

    public void onEnableSystemAlertWindowContainerButtonClicked() {
        notifyPropertyChanged(PROPERTY_SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    // Permissions enable/disable

    public void setSystemAlertWindowPermissionEnabled(boolean enabled) {
        setSystemAlertWindowContainerVisible(!enabled);
    }

    // Permission view containers

    public boolean isSystemAlertWindowContainerVisible() {
        return systemAlertWindowContainerVisible;
    }

    private void setSystemAlertWindowContainerVisible(boolean systemAlertWindowContainerVisible) {
        this.systemAlertWindowContainerVisible = systemAlertWindowContainerVisible;
        notifyPropertyChanged(PROPERTY_SYSTEM_ALERT_WINDOW_CONTAINER);
    }

    // Load configuration file

    public void checkIfLoadingOfConfigurationFileIsAllowed() {
        boolean allowed;
        allowed = !(systemAlertWindowContainerVisible || accessibilityServiceContainerVisible);
        setLoadConfigurationFileContainerVisible(allowed);
        setLoadedPackageNamesContainerVisible(allowed);
        setPermissionHeaderVisible(!allowed);
        notifyPropertyChanged(PROPERTY_CONFIGURATION_FILE_ATTRIBUTES);
        notifyPropertyChanged(PROPERTY_CONFIGURATION_LOADING_IN_PROGRESS);
    }

    public boolean isLoadConfigurationFileContainerVisible() {
        return loadConfigurationFileContainerVisible;
    }

    private void setLoadConfigurationFileContainerVisible(boolean loadConfigurationFileContainerVisible) {
        this.loadConfigurationFileContainerVisible = loadConfigurationFileContainerVisible;
        notifyPropertyChanged(PROPERTY_LOAD_CONFIGURATION_FILE_CONTAINER);
    }

    public void onLoadConfigurationFileButtonClicked() {
        setLoadedPackageNamesListVisible(false);
        setConfigurationLoadingInProgress(true);
        notifyPropertyChanged(PROPERTY_LOAD_CONFIGURATION_FILE_BUTTON);
    }

    public void onSelectConfigurationFileButtonClicked() {
        notifyPropertyChanged(PROPERTY_SELECT_CONFIGURATION_FILE_BUTTON);
    }

    public String getConfigurationFileUri() {
        return configurationFileUri;
    }

    public String getConfigurationFileName() {
        return configurationFileName;
    }

    public void setConfigurationFileAttributes(String configurationFileUri, String configurationFileName) {
        this.configurationFileUri = configurationFileUri;
        this.configurationFileName = configurationFileName;
        notifyPropertyChanged(PROPERTY_CONFIGURATION_FILE_ATTRIBUTES);
    }

    public void saveConfigurationFileAttributes() {
        notifyPropertyChanged(PROPERTY_SAVE_CONFIGURATION_FILE_ATTRIBUTES);
    }

    public boolean isConfigurationLoadingInProgress() {
        return configurationLoadingInProgress;
    }

    private void setConfigurationLoadingInProgress(boolean configurationLoadingInProgress) {
        this.configurationLoadingInProgress = configurationLoadingInProgress;
        notifyPropertyChanged(PROPERTY_CONFIGURATION_LOADING_IN_PROGRESS);
    }

    // Accessibility service

    public void onEnableAccessibilityServiceButtonClicked() {
        notifyPropertyChanged(PROPERTY_ACCESSIBILITY_SERVICE_BUTTON);
    }

    public void setAccessibilityServiceEnabled(boolean enabled) {
        setAccessibilityServiceContainerVisible(!enabled);
    }

    public boolean isAccessibilityServiceContainerVisible() {
        return accessibilityServiceContainerVisible;
    }

    private void setAccessibilityServiceContainerVisible(boolean accessibilityServiceContainerVisible) {
        this.accessibilityServiceContainerVisible = accessibilityServiceContainerVisible;
        notifyPropertyChanged(PROPERTY_ACCESSIBILITY_SERVICE_CONTAINER);
    }

    // Package names

    public List<String> getLoadedPackageNames() {
        return loadedPackageNames;
    }

    public void onPackageNamesLoaded(List<String> loadedPackageNames) {
        this.loadedPackageNames = loadedPackageNames;
        setConfigurationLoadingInProgress(false);
        if (loadedPackageNames == null || loadedPackageNames.isEmpty()) {
            setLoadedPackageNamesEmptyVisible(true);
            setLoadedPackageNamesListVisible(false);
        } else {
            setLoadedPackageNamesEmptyVisible(false);
            setLoadedPackageNamesListVisible(true);
        }
    }

    public boolean isLoadedPackageNamesListVisible() {
        return loadedPackageNamesListVisible;
    }

    private void setLoadedPackageNamesListVisible(boolean loadedPackageNamesListVisible) {
        this.loadedPackageNamesListVisible = loadedPackageNamesListVisible;
        notifyPropertyChanged(PROPERTY_LOADED_PACKAGE_NAMES_LIST);
    }

    public boolean isLoadedPackageNamesContainerVisible() {
        return loadedPackageNamesContainerVisible;
    }

    private void setLoadedPackageNamesContainerVisible(boolean loadedPackageNamesContainerVisible) {
        this.loadedPackageNamesContainerVisible = loadedPackageNamesContainerVisible;
        notifyPropertyChanged(PROPERTY_LOADED_PACKAGE_NAMES_CONTAINER);
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

    public boolean isLoadedPackageNamesEmptyVisible() {
        return loadedPackageNamesEmptyVisible;
    }

    private void setLoadedPackageNamesEmptyVisible(boolean loadedPackageNamesEmptyVisible) {
        this.loadedPackageNamesEmptyVisible = loadedPackageNamesEmptyVisible;
        notifyPropertyChanged(PROPERTY_LOADED_PACKAGE_NAMES_EMPTY);
    }

    // Parcelable

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(permissionsHeaderVisible ? (byte) 1 : (byte) 0);
        dest.writeByte(systemAlertWindowContainerVisible ? (byte) 1 : (byte) 0);
        dest.writeByte(accessibilityServiceContainerVisible ? (byte) 1 : (byte) 0);
        dest.writeByte(loadConfigurationFileContainerVisible ? (byte) 1 : (byte) 0);
        dest.writeString(this.configurationFileUri);
        dest.writeString(this.configurationFileName);
        dest.writeByte(loadedPackageNamesContainerVisible ? (byte) 1 : (byte) 0);
        dest.writeByte(loadedPackageNamesEmptyVisible ? (byte) 1 : (byte) 0);
        dest.writeByte(loadedPackageNamesListVisible ? (byte) 1 : (byte) 0);
        dest.writeStringList(this.loadedPackageNames);
        dest.writeByte(configurationLoadingInProgress ? (byte) 1 : (byte) 0);
    }

    protected MainActivityModel(Parcel in) {
        this.permissionsHeaderVisible = in.readByte() != 0;
        this.systemAlertWindowContainerVisible = in.readByte() != 0;
        this.accessibilityServiceContainerVisible = in.readByte() != 0;
        this.loadConfigurationFileContainerVisible = in.readByte() != 0;
        this.configurationFileUri = in.readString();
        this.configurationFileName = in.readString();
        this.loadedPackageNamesContainerVisible = in.readByte() != 0;
        this.loadedPackageNamesEmptyVisible = in.readByte() != 0;
        this.loadedPackageNamesListVisible = in.readByte() != 0;
        this.loadedPackageNames = in.createStringArrayList();
        this.configurationLoadingInProgress = in.readByte() != 0;
    }

    public static final Creator<MainActivityModel> CREATOR = new Creator<MainActivityModel>() {
        public MainActivityModel createFromParcel(Parcel source) {
            return new MainActivityModel(source);
        }

        public MainActivityModel[] newArray(int size) {
            return new MainActivityModel[size];
        }
    };
}
