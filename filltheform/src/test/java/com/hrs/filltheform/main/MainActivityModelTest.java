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
 * Tests for MainActivityModel.
 */
@RunWith(PowerMockRunner.class)
public class MainActivityModelTest {

    private PropertyChangedListener propertyChangedListener;
    private MainActivityModel model;

    @Before
    public void setUp() throws Exception {
        propertyChangedListener = mock(PropertyChangedListener.class);
        model = new MainActivityModel();
        model.setPropertyChangedListener(propertyChangedListener);
    }

    @Test
    public void testCheckIfLoadingOfConfigurationFileIsNotAllowed() throws Exception {
        // run
        model.checkIfLoadingOfConfigurationFileIsAllowed();

        // verify
        assertFalse(model.isLoadConfigurationFileContainerVisible());
        assertFalse(model.isLoadedPackageNamesContainerVisible());
        assertTrue(model.isPermissionsHeaderVisible());
        assertTrue(model.isSystemAlertWindowContainerVisible());
        assertTrue(model.isAccessibilityServiceContainerVisible());
        assertTrue(model.isReadExternalStorageContainerVisible());
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_CONFIGURATION_FILE_ATTRIBUTES);
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_CONFIGURATION_LOADING_IN_PROGRESS);
    }

    @Test
    public void testCheckIfLoadingOfConfigurationFileIsAllowed() throws Exception {
        // run
        model.setSystemAlertWindowPermissionEnabled(true);
        model.setAccessibilityServiceEnabled(true);
        model.setReadExternalStorageEnabled(true);
        model.checkIfLoadingOfConfigurationFileIsAllowed();

        // verify
        assertTrue(model.isLoadConfigurationFileContainerVisible());
        assertTrue(model.isLoadedPackageNamesContainerVisible());
        assertFalse(model.isPermissionsHeaderVisible());
        assertFalse(model.isSystemAlertWindowContainerVisible());
        assertFalse(model.isAccessibilityServiceContainerVisible());
        assertFalse(model.isReadExternalStorageContainerVisible());
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_CONFIGURATION_FILE_ATTRIBUTES);
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_CONFIGURATION_LOADING_IN_PROGRESS);
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_ACCESSIBILITY_SERVICE_CONTAINER);
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_SYSTEM_ALERT_WINDOW_CONTAINER);
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_READ_EXTERNAL_STORAGE_CONTAINER);
    }

    @Test
    public void testOnEnableSystemAlertWindowContainerButtonClicked() throws Exception {
        // run
        model.onEnableSystemAlertWindowContainerButtonClicked();

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_SYSTEM_ALERT_WINDOW_PERMISSION);
    }

    @Test
    public void testOnEnableAccessibilityServiceButtonClicked() throws Exception {
        // run
        model.onEnableAccessibilityServiceButtonClicked();

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_ACCESSIBILITY_SERVICE_BUTTON);
    }

    @Test
    public void testOnEnableReadExternalStorageButtonClicked() throws Exception {
        // run
        model.onEnableReadExternalStorageButtonClicked();

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_READ_EXTERNAL_STORAGE_PERMISSION);
    }

    @Test
    public void testOnLoadConfigurationFileButtonClicked() throws Exception {
        // run
        model.onLoadConfigurationFileButtonClicked();

        // verify
        assertFalse(model.isLoadedPackageNamesContainerVisible());
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_LOADED_PACKAGE_NAMES_LIST);
        assertTrue(model.isConfigurationLoadingInProgress());
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_CONFIGURATION_LOADING_IN_PROGRESS);
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_LOAD_CONFIGURATION_FILE_BUTTON);
    }

    @Test
    public void testOnSelectConfigurationFileButtonClicked() throws Exception {
        // run
        model.onSelectConfigurationFileButtonClicked();

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_SELECT_CONFIGURATION_FILE_BUTTON);
    }

    @Test
    public void testGetConfigurationFileUriShouldBeNull() throws Exception {
        // verify
        assertEquals(null, model.getConfigurationFilePath());
    }

    @Test
    public void testGetConfigurationFileNameShouldBeNull() throws Exception {
        // verify
        assertEquals(null, model.getConfigurationFileName());
    }

    @Test
    public void testGetConfigurationFileUri() throws Exception {
        // prepare
        model.setConfigurationFileAttributes("uri", "name");

        // verify
        assertEquals("uri", model.getConfigurationFilePath());
    }

    @Test
    public void testGetConfigurationFileName() throws Exception {
        // prepare
        model.setConfigurationFileAttributes("uri", "name");

        // verify
        assertEquals("name", model.getConfigurationFileName());
    }

    @Test
    public void testSetConfigurationFileAttributes() throws Exception {
        // prepare
        model.setConfigurationFileAttributes("uri", "name");

        // verify
        assertEquals("uri", model.getConfigurationFilePath());
        assertEquals("name", model.getConfigurationFileName());
    }

    @Test
    public void testSaveConfigurationFileAttributes() throws Exception {
        // run
        model.saveConfigurationFileAttributes();

        // verify
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_SAVE_CONFIGURATION_FILE_ATTRIBUTES);
    }

    @Test
    public void testIsConfigurationLoadingInProgressShouldReturnFalse() throws Exception {
        // verify
        assertFalse(model.isConfigurationLoadingInProgress());
    }

    @Test
    public void testGetLoadedPackageNamesShouldReturnNull() throws Exception {
        // verify
        assertEquals(null, model.getLoadedPackageNames());
    }

    @Test
    public void testOnPackageNamesLoadedWhenPackageNamesAreNotNull() throws Exception {
        // prepare
        List<String> loadedPackageNames = new ArrayList<>();
        loadedPackageNames.add("com.test.one");
        loadedPackageNames.add("com.test.two");

        // run
        model.onPackageNamesLoaded(loadedPackageNames);

        // verify
        assertEquals(loadedPackageNames, model.getLoadedPackageNames());
        assertFalse(model.isConfigurationLoadingInProgress());
        assertFalse(model.isLoadedPackageNamesEmptyVisible());
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_LOADED_PACKAGE_NAMES_EMPTY);
        assertTrue(model.isLoadedPackageNamesListVisible());
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_LOADED_PACKAGE_NAMES_LIST);
    }

    @Test
    public void testOnPackageNamesLoadedWhenPackageNamesAreNull() throws Exception {
        // run
        model.onPackageNamesLoaded(null);

        // verify
        assertEquals(null, model.getLoadedPackageNames());
        assertFalse(model.isConfigurationLoadingInProgress());
        assertTrue(model.isLoadedPackageNamesEmptyVisible());
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_LOADED_PACKAGE_NAMES_EMPTY);
        assertFalse(model.isLoadedPackageNamesListVisible());
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_LOADED_PACKAGE_NAMES_LIST);
    }

    @Test
    public void testOnPackageNamesLoadedWhenPackageNamesAreEmpty() throws Exception {
        // prepare
        List<String> loadedPackageNames = new ArrayList<>();

        // run
        model.onPackageNamesLoaded(loadedPackageNames);

        // verify
        assertEquals(0, model.getLoadedPackageNames().size());
        assertFalse(model.isConfigurationLoadingInProgress());
        assertTrue(model.isLoadedPackageNamesEmptyVisible());
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_LOADED_PACKAGE_NAMES_EMPTY);
        assertFalse(model.isLoadedPackageNamesListVisible());
        verify(propertyChangedListener, times(1)).onPropertyChanged(MainActivityModel.PROPERTY_LOADED_PACKAGE_NAMES_LIST);
    }
}