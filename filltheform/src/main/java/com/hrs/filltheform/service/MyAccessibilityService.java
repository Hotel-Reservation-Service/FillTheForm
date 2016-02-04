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
package com.hrs.filltheform.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hrs.filltheform.R;
import com.hrs.filltheform.common.ConfigurationItem;
import com.hrs.filltheform.common.event.EventResolver;
import com.hrs.filltheform.common.event.EventResolverListener;
import com.hrs.filltheform.common.reader.ConfigurationReader;
import com.hrs.filltheform.dialog.FillTheFormDialog;
import com.hrs.filltheform.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * MyAccessibilityService initializes ServiceConfiguration. After successful configuration loading it sends loaded package names to the MainActivity.
 * This services also receives the Accessibility Events. It uses EventResolver and ServiceConfiguration to process these events.
 * When data for a specific AccessibilityNode is available it shows FillTheFormDialog to the user.
 */
public class MyAccessibilityService extends android.accessibilityservice.AccessibilityService implements ServiceConfiguration.ServiceConfigurationListener, EventResolverListener {

    public static final String INTENT_READ_CONFIGURATION_FILE = "com.hrs.filltheform.INTENT_READ_CONFIGURATION_FILE";
    public static final String INTENT_EXTRA_CONFIGURATION_FILE_URI = "com.hrs.filltheform.INTENT_EXTRA_CONFIGURATION_FILE_URI";
    public static final String INTENT_EXTRA_CONFIGURATION_FILE_SOURCE = "com.hrs.filltheform.INTENT_EXTRA_CONFIGURATION_FILE_SOURCE";
    public static final String INTENT_ASK_FOR_LOADED_PACKAGE_NAMES = "com.hrs.filltheform.INTENT_ASK_FOR_LOADED_PACKAGE_NAMES";
    public static final String INTENT_SEND_LOADED_PACKAGE_NAMES = "com.hrs.filltheform.INTENT_SEND_LOADED_PACKAGE_NAMES";
    public static final String INTENT_EXTRA_PACKAGE_NAMES = "com.hrs.filltheform.INTENT_EXTRA_PACKAGE_NAMES";

    private ServiceConfiguration configuration;
    private EventResolver eventResolver;
    private FillTheFormDialog fillTheFormDialog;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(INTENT_READ_CONFIGURATION_FILE)) {
                String configurationFileUri = intent.getStringExtra(INTENT_EXTRA_CONFIGURATION_FILE_URI);
                @ConfigurationReader.ConfigurationSource int configurationFileSource = intent.getIntExtra(INTENT_EXTRA_CONFIGURATION_FILE_SOURCE, ConfigurationReader.SOURCE_ASSETS);
                configuration.init(getApplicationContext(), configurationFileSource, configurationFileUri);
                fillTheFormDialog.setConfigurationVariablePattern(configuration.getConfigurationVariablePattern());
            } else if (intent.getAction().equalsIgnoreCase(INTENT_ASK_FOR_LOADED_PACKAGE_NAMES)) {
                if (configuration != null) {
                    configuration.resendConfigurationData();
                }
            }
        }
    };

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_READ_CONFIGURATION_FILE);
        intentFilter.addAction(INTENT_ASK_FOR_LOADED_PACKAGE_NAMES);
        registerReceiver(broadcastReceiver, intentFilter);
        setUpServiceConfiguration();
    }

    private void setUpServiceConfiguration() {
        configuration = new ServiceConfiguration();
        configuration.setConfigurationLoaderListener(this);
        eventResolver = new ServiceEventResolver(configuration);
        eventResolver.setEventResolverListener(this);
        fillTheFormDialog = new FillTheFormDialog(getApplicationContext());
    }

    // Configuration management

    @Override
    public void onConfigurationCompleted(List<String> packageNames) {
        sendLoadedPackageNames(packageNames);
    }

    @Override
    public void onConfigurationFailed(String errorMessage) {
        ToastUtil.show(this, getString(R.string.error_loading_configuration_file_prefix) + errorMessage);
        sendLoadedPackageNames(null);
    }

    private void sendLoadedPackageNames(List<String> packageNames) {
        Intent intent = new Intent(INTENT_SEND_LOADED_PACKAGE_NAMES);
        intent.putStringArrayListExtra(INTENT_EXTRA_PACKAGE_NAMES, (ArrayList<String>) packageNames);
        sendBroadcast(intent);
    }

    // Event handling

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        eventResolver.onAccessibilityEvent(event);
    }

    @Override
    public void onDataForSelectedNodeAvailable(AccessibilityNodeInfo selectedNodeInfo, List<ConfigurationItem> selectedConfigurationItems) {
        fillTheFormDialog.showDialog(selectedNodeInfo, selectedConfigurationItems);
    }

    @Override
    public void onDataForSelectedNodeNotAvailable(AccessibilityNodeInfo selectedNodeInfo) {
        ToastUtil.show(getApplicationContext(), getString(R.string.values_not_found));
    }

    // Lifecycle

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onInterrupt() {

    }
}
