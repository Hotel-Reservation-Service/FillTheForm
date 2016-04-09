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
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.view.accessibility.AccessibilityEvent;

import com.hrs.filltheform.R;
import com.hrs.filltheform.common.ConfigurationItem;
import com.hrs.filltheform.common.event.EventResolver;
import com.hrs.filltheform.common.event.EventResolverListener;
import com.hrs.filltheform.dialog.FillTheFormDialog;
import com.hrs.filltheform.util.LogUtil;
import com.hrs.filltheform.util.ToastUtil;
import com.hrs.filltheformcompanion.FillTheFormCompanion;

import java.util.ArrayList;
import java.util.List;

/**
 * MyAccessibilityService initializes ServiceConfiguration. After successful configuration loading it sends loaded package names to the MainActivity.
 * This services also receives the Accessibility Events. It uses EventResolver and ServiceConfiguration to process these events.
 * When data for a specific AccessibilityNode is available it shows FillTheFormDialog to the user.
 */
public class MyAccessibilityService extends android.accessibilityservice.AccessibilityService implements ServiceConfiguration.ServiceConfigurationListener, EventResolverListener {

    private static final String TAG = MyAccessibilityService.class.getSimpleName();

    public static final String INTENT_ASK_FOR_LOADED_PACKAGE_NAMES = "com.hrs.filltheform.INTENT_ASK_FOR_LOADED_PACKAGE_NAMES";
    public static final String INTENT_SEND_LOADED_PACKAGE_NAMES = "com.hrs.filltheform.INTENT_SEND_LOADED_PACKAGE_NAMES";
    public static final String INTENT_EXTRA_PACKAGE_NAMES = "com.hrs.filltheform.INTENT_EXTRA_PACKAGE_NAMES";

    private ServiceConfiguration configuration;
    private EventResolver eventResolver;
    private FillTheFormDialog fillTheFormDialog;
    private boolean showConfigurationSuccessMessage;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            resolveBroadcastIntent(intent);
        }
    };

    private void resolveBroadcastIntent(Intent intent) {
        if (intent.getAction().equalsIgnoreCase(INTENT_ASK_FOR_LOADED_PACKAGE_NAMES)) {
            if (configuration != null) {
                configuration.resendConfigurationData();
            }
        } else {
            checkCompanionActions(intent);
        }
    }

    // Service setup

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_ASK_FOR_LOADED_PACKAGE_NAMES);
        addCompanionActions(intentFilter);
        registerReceiver(broadcastReceiver, intentFilter);
        setUpServiceConfiguration();
    }

    private void setUpServiceConfiguration() {
        configuration = new ServiceConfiguration();
        configuration.setConfigurationLoaderListener(this);
        eventResolver = new ServiceEventResolver(configuration);
        eventResolver.setEventResolverListener(this);
        fillTheFormDialog = new FillTheFormDialog(this);
    }

    // Configuration management

    @Override
    public void onResendConfiguration(List<String> packageNames) {
        sendLoadedPackageNames(packageNames);
    }

    @Override
    public void onConfigurationCompleted(List<String> packageNames, List<String> profiles) {
        sendLoadedPackageNames(packageNames);
        fillTheFormDialog.setProfiles(profiles);
        sendBroadcast(new Intent(FillTheFormCompanion.INTENT_REPORT_CONFIGURATION_FINISHED));
        if (showConfigurationSuccessMessage) {
            ToastUtil.show(this, getString(R.string.configuration_success));
        }
    }

    @Override
    public void onConfigurationFailed(String errorMessage) {
        ToastUtil.show(this, getString(R.string.error_loading_configuration_file_prefix) + errorMessage);
        sendLoadedPackageNames(null);
        sendBroadcast(new Intent(FillTheFormCompanion.INTENT_REPORT_CONFIGURATION_FINISHED));
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
    public void onDataForSelectedNodeAvailable(AccessibilityNodeInfoCompat selectedNodeInfo, int accessibilityEventType, List<ConfigurationItem> selectedConfigurationItems) {
        fillTheFormDialog.showDialog(selectedNodeInfo, accessibilityEventType, selectedConfigurationItems);
    }

    @Override
    public void onDataForSelectedNodeNotAvailable(AccessibilityNodeInfoCompat selectedNodeInfo) {
        LogUtil.d(TAG, getString(R.string.values_not_found) + selectedNodeInfo.toString());
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

    // FillTheFormCompanion support

    private void addCompanionActions(IntentFilter intentFilter) {
        intentFilter.addAction(FillTheFormCompanion.INTENT_READ_CONFIGURATION_FILE);
        intentFilter.addAction(FillTheFormCompanion.INTENT_HIDE_FILL_THE_FORM_DIALOG);
        intentFilter.addAction(FillTheFormCompanion.INTENT_SET_FAST_MODE);
        intentFilter.addAction(FillTheFormCompanion.INTENT_SET_NORMAL_MODE);
        intentFilter.addAction(FillTheFormCompanion.INTENT_REQUEST_NUMBER_OF_PROFILES);
        intentFilter.addAction(FillTheFormCompanion.INTENT_SELECT_NEXT_PROFILE);
    }

    private void checkCompanionActions(Intent intent) {
        if (fillTheFormDialog == null) {
            return;
        }
        String action = intent.getAction();
        switch (action) {
            case FillTheFormCompanion.INTENT_READ_CONFIGURATION_FILE:
                showConfigurationSuccessMessage = intent.getBooleanExtra(FillTheFormCompanion.INTENT_EXTRA_SHOW_CONFIGURATION_SUCCESS_MESSAGE, false);
                String configurationFilePath = intent.getStringExtra(FillTheFormCompanion.INTENT_EXTRA_CONFIGURATION_FILE_PATH);
                @FillTheFormCompanion.ConfigurationSource int configurationFileSource = intent.getIntExtra(FillTheFormCompanion.INTENT_EXTRA_CONFIGURATION_FILE_SOURCE, FillTheFormCompanion.SOURCE_ASSETS);
                configuration.init(this, configurationFileSource, configurationFilePath);
                fillTheFormDialog.setConfigurationVariablePattern(configuration.getConfigurationVariablePattern());
                break;
            case FillTheFormCompanion.INTENT_HIDE_FILL_THE_FORM_DIALOG:
                fillTheFormDialog.hideDialog();
                break;
            case FillTheFormCompanion.INTENT_SET_FAST_MODE:
                fillTheFormDialog.setFastMode();
                break;
            case FillTheFormCompanion.INTENT_SET_NORMAL_MODE:
                fillTheFormDialog.setNormalMode();
                break;
            case FillTheFormCompanion.INTENT_REQUEST_NUMBER_OF_PROFILES:
                int numberOfProfiles = configuration.getNumberOfProfiles();
                // Answer with number of profiles
                Intent broadcastIntent = new Intent();
                broadcastIntent.setAction(FillTheFormCompanion.INTENT_SEND_NUMBER_OF_PROFILES);
                broadcastIntent.putExtra(FillTheFormCompanion.INTENT_EXTRA_NUMBER_OF_PROFILES, numberOfProfiles);
                sendBroadcast(broadcastIntent);
                break;
            case FillTheFormCompanion.INTENT_SELECT_NEXT_PROFILE:
                fillTheFormDialog.selectNextProfile();
                break;
            default:
                break;
        }
    }
}
