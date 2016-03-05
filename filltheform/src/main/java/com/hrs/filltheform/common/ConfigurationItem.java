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
package com.hrs.filltheform.common;

import android.support.annotation.NonNull;

/**
 * This class represents one configuration item.
 */
public class ConfigurationItem {

    private String id;
    private String profile;
    private String rawValue;
    private String value;

    public ConfigurationItem(String id, String profile, String rawValue) {
        this.id = id;
        this.profile = profile;
        this.rawValue = rawValue;
    }

    public ConfigurationItem(@NonNull ConfigurationItem configurationItem) {
        //noinspection ConstantConditions
        if (configurationItem != null) {
            this.id = configurationItem.id;
            this.profile = configurationItem.profile;
            this.rawValue = configurationItem.getRawValue();
            this.value = configurationItem.getValue();
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getRawValue() {
        return rawValue;
    }
}