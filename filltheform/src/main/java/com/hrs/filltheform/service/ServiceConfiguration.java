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

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.util.SimpleArrayMap;

import com.hrs.filltheform.common.ConfigurationItem;
import com.hrs.filltheform.common.reader.ConfigurationReader;
import com.hrs.filltheform.common.reader.ConfigurationReaderListener;
import com.hrs.filltheform.data.XmlConfigurationFileReader;
import com.hrs.filltheformcompanion.FillTheFormCompanion;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * ServiceConfiguration holds the data loaded from the configuration file using ConfigurationReader.
 */
public class ServiceConfiguration implements ConfigurationReaderListener {

    public interface ServiceConfigurationListener {
        void onResendConfiguration(List<String> packageNames);

        void onConfigurationCompleted(List<String> packageNames, List<String> profiles);

        void onConfigurationFailed(String errorMessage);
    }

    private final List<String> packageNames = new ArrayList<>();
    private final SimpleArrayMap<String, List<ConfigurationItem>> idGroups = new SimpleArrayMap<>();
    private final Set<String> profiles = new LinkedHashSet<>();

    private ServiceConfigurationListener serviceConfigurationListener;
    private ConfigurationReader configurationReader;

    public void setConfigurationLoaderListener(ServiceConfigurationListener serviceConfigurationListener) {
        this.serviceConfigurationListener = serviceConfigurationListener;
    }

    public void init(Context context, @FillTheFormCompanion.ConfigurationSource int source, @NonNull String configurationFilePath) {
        packageNames.clear();
        idGroups.clear();
        profiles.clear();
        if (configurationReader == null) {
            configurationReader = new XmlConfigurationFileReader(context, this);
        }
        configurationReader.readConfigurationFile(source, configurationFilePath);
    }

    @Override
    public void onPackageName(String packageName) {
        packageNames.add(packageName);
    }

    @Override
    public void onConfigurationItem(ConfigurationItem configurationItem) {
        List<ConfigurationItem> list = idGroups.get(configurationItem.getId());
        if (list == null) {
            list = new ArrayList<>();
            idGroups.put(configurationItem.getId(), list);
        }
        list.add(configurationItem);
        // Add profile
        if (configurationItem.getProfile() != null) {
            profiles.add(configurationItem.getProfile());
        }
    }

    @Override
    public void onReadingCompleted() {
        if (serviceConfigurationListener != null) {
            List<String> profilesList = new ArrayList<>(profiles);
            serviceConfigurationListener.onConfigurationCompleted(packageNames, profilesList);
        }
    }

    @Override
    public void onReadingFailed(String errorMessage) {
        if (serviceConfigurationListener != null) {
            serviceConfigurationListener.onConfigurationFailed(errorMessage);
        }
    }

    public void resendConfigurationData() {
        if (serviceConfigurationListener != null && !packageNames.isEmpty()) {
            serviceConfigurationListener.onResendConfiguration(packageNames);
        }
    }

    public SimpleArrayMap<String, List<ConfigurationItem>> getIdGroups() {
        return idGroups;
    }

    public List<String> getPackageNames() {
        return packageNames;
    }

    public String getConfigurationVariablePattern() {
        if (configurationReader != null) {
            return configurationReader.getConfigurationVariablePattern();
        }
        return null;
    }

    // FillTheFormCompanion support

    public int getNumberOfProfiles() {
        return profiles.size();
    }
}
