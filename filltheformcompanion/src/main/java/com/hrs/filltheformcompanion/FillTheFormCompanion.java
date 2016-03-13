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
package com.hrs.filltheformcompanion;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * FillTheFormCompanion is used to communicate with FillTheForm service.
 * For example, when we want to use FillTheForm in automated UI tests.
 */
public class FillTheFormCompanion {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FillTheFormCompanion.SOURCE_ASSETS, FillTheFormCompanion.SOURCE_EXTERNAL_STORAGE, FillTheFormCompanion.SOURCE_OTHER})
    public @interface ConfigurationSource {
    }

    // Configuration file source types
    public static final int SOURCE_ASSETS = 0;
    public static final int SOURCE_EXTERNAL_STORAGE = 1;
    public static final int SOURCE_OTHER = 2;

    /* INTENT CONSTANTS */
    // Configuration
    public static final String INTENT_READ_CONFIGURATION_FILE = "com.hrs.filltheform.INTENT_READ_CONFIGURATION_FILE";
    public static final String INTENT_EXTRA_CONFIGURATION_FILE_PATH = "com.hrs.filltheform.INTENT_EXTRA_CONFIGURATION_FILE_PATH";
    public static final String INTENT_EXTRA_CONFIGURATION_FILE_SOURCE = "com.hrs.filltheform.INTENT_EXTRA_CONFIGURATION_FILE_SOURCE";
    public static final String INTENT_EXTRA_SHOW_CONFIGURATION_SUCCESS_MESSAGE = "com.hrs.filltheform.INTENT_EXTRA_SHOW_CONFIGURATION_SUCCESS_MESSAGE";
    public static final String INTENT_REPORT_CONFIGURATION_FINISHED = "com.hrs.filltheform.INTENT_REPORT_CONFIGURATION_FINISHED";
    // Visibility
    public static final String INTENT_HIDE_FILL_THE_FORM_DIALOG = "com.hrs.filltheform.INTENT_HIDE_FILL_THE_FORM_DIALOG";
    // Mode management
    public static final String INTENT_SET_FAST_MODE = "com.hrs.filltheform.INTENT_SET_FAST_MODE";
    public static final String INTENT_SET_NORMAL_MODE = "com.hrs.filltheform.INTENT_SET_NORMAL_MODE";
    // Profiles information
    public static final String INTENT_REQUEST_NUMBER_OF_PROFILES = "com.hrs.filltheform.INTENT_REQUEST_NUMBER_OF_PROFILES";
    public static final String INTENT_SEND_NUMBER_OF_PROFILES = "com.hrs.filltheform.INTENT_SEND_NUMBER_OF_PROFILES";
    public static final String INTENT_EXTRA_NUMBER_OF_PROFILES = "com.hrs.filltheform.INTENT_EXTRA_NUMBER_OF_PROFILES";
    // Profiles navigation
    public static final String INTENT_SELECT_NEXT_PROFILE = "com.hrs.filltheform.INTENT_SELECT_NEXT_PROFILE";

    private static final int NO_PROFILES = 0;

    private final Context context;
    private int numberOfProfiles = NO_PROFILES;
    private boolean configurationFinished;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(INTENT_SEND_NUMBER_OF_PROFILES)) {
                numberOfProfiles = intent.getExtras().getInt(INTENT_EXTRA_NUMBER_OF_PROFILES, NO_PROFILES);
            } else if (intent.getAction().equalsIgnoreCase(INTENT_REPORT_CONFIGURATION_FINISHED)) {
                setConfigurationFinished(true);
                requestNumberOfProfiles();
            }
        }
    };

    public FillTheFormCompanion(Context context) {
        this.context = context;
        // Register receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(INTENT_SEND_NUMBER_OF_PROFILES);
        intentFilter.addAction(INTENT_REPORT_CONFIGURATION_FINISHED);
        context.registerReceiver(broadcastReceiver, intentFilter);
        // Ask FillTheForm for number of profiles
        requestNumberOfProfiles();
    }

    // Configuration

    /**
     * The successful configuration loading needs to be confirmed from FillTheForm service.
     * If you are using this method - be sure to use ConfigurationStatusIdlingResource in your Espresso test.
     *
     * @param source                Configuration file source.
     * @param configurationFilePath Configuration file path.
     */
    public void configureFillTheForm(@ConfigurationSource int source, @NonNull String configurationFilePath) {
        setConfigurationFinished(false);
        numberOfProfiles = NO_PROFILES;
        // Request new configuration from FillTheForm service
        Bundle extras = new Bundle();
        extras.putInt(INTENT_EXTRA_CONFIGURATION_FILE_SOURCE, source);
        extras.putString(INTENT_EXTRA_CONFIGURATION_FILE_PATH, configurationFilePath);
        sendBroadcast(INTENT_READ_CONFIGURATION_FILE, extras);
    }

    public boolean isConfigurationFinished() {
        return configurationFinished;
    }

    private void setConfigurationFinished(boolean configurationFinished) {
        this.configurationFinished = configurationFinished;
    }

    // Visibility

    public void hideFillTheFormDialog() {
        sendBroadcast(INTENT_HIDE_FILL_THE_FORM_DIALOG);

    }

    // Mode management

    public void setFastMode() {
        sendBroadcast(INTENT_SET_FAST_MODE);

    }

    public void setNormalMode() {
        sendBroadcast(INTENT_SET_NORMAL_MODE);

    }

    // Profiles information

    /**
     * This value requires response from FillTheForm service.
     * Request is sent using {@link #requestNumberOfProfiles()}.
     * If you are using this method - be sure to use NumberOfProfilesIdlingResource in your Espresso test.
     *
     * @return Number of profiles loaded in FillTheForm service.
     */
    public int getNumberOfProfiles() {
        return numberOfProfiles;
    }

    boolean isNumberOfProfilesUpdated() {
        return numberOfProfiles != NO_PROFILES;
    }

    public void requestNumberOfProfiles() {
        // Ask FillTheForm for number of profiles
        sendBroadcast(INTENT_REQUEST_NUMBER_OF_PROFILES);
    }

    // Profiles navigation

    public void selectNextProfile() {
        sendBroadcast(INTENT_SELECT_NEXT_PROFILE);

    }

    // Broadcast actions

    private void sendBroadcast(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        context.sendBroadcast(intent);
    }

    private void sendBroadcast(String action, Bundle extras) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtras(extras);
        context.sendBroadcast(intent);
    }
}
