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

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents one configuration item.
 */
public class ConfigurationItem {

    private String id;
    private String profile;
    private String rawValue;
    private String value;
    private String label;
    private List<String> rememberLastEntryForIds = new ArrayList<>();
    private boolean lastEntryItem;

    public ConfigurationItem(String id, String profile) {
        this.id = id;
        this.profile = profile;
    }

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
            this.label = configurationItem.getLabel();
            if (!configurationItem.getRememberLastEntryForIds().isEmpty()) {
                for (String id : configurationItem.getRememberLastEntryForIds()) {
                    rememberLastEntryForIds.add(id);
                }
            }
            this.lastEntryItem = configurationItem.isLastEntryItem();
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

    public void setRawValue(String rawValue) {
        this.rawValue = rawValue;
    }

    public String getRawValue() {
        return rawValue;
    }

    public String getLabel() {
        if (label != null) {
            return label;
        } else {
            return value;
        }
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isLastEntryItem() {
        return lastEntryItem;
    }

    public void setLastEntryItem(boolean lastEntryItem) {
        this.lastEntryItem = lastEntryItem;
    }

    public boolean shouldRememberLastEntry() {
        return !rememberLastEntryForIds.isEmpty();
    }

    public List<String> getRememberLastEntryForIds() {
        return rememberLastEntryForIds;
    }

    public void rememberLastEntryForId(String id) {
        rememberLastEntryForIds.add(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigurationItem)) return false;

        ConfigurationItem that = (ConfigurationItem) o;

        if (lastEntryItem != that.lastEntryItem) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (profile != null ? !profile.equals(that.profile) : that.profile != null) return false;
        if (rawValue != null ? !rawValue.equals(that.rawValue) : that.rawValue != null)
            return false;
        if (value != null ? !value.equals(that.value) : that.value != null) return false;
        if (label != null ? !label.equals(that.label) : that.label != null) return false;
        return rememberLastEntryForIds != null ? rememberLastEntryForIds.equals(that.rememberLastEntryForIds) : that.rememberLastEntryForIds == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (profile != null ? profile.hashCode() : 0);
        result = 31 * result + (rawValue != null ? rawValue.hashCode() : 0);
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (label != null ? label.hashCode() : 0);
        result = 31 * result + (rememberLastEntryForIds != null ? rememberLastEntryForIds.hashCode() : 0);
        result = 31 * result + (lastEntryItem ? 1 : 0);
        return result;
    }
}