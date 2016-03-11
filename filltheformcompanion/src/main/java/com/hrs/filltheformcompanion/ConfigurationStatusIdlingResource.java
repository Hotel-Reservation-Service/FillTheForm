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

import android.support.test.espresso.IdlingResource;

/**
 * ConfigurationStatusIdlingResource is used when combining FillTheFormCompanion with Espresso.
 * The goal is to wait until FillTheForm service is configured.
 */
public class ConfigurationStatusIdlingResource implements IdlingResource {
    private final FillTheFormCompanion companion;
    private ResourceCallback resourceCallback;

    public ConfigurationStatusIdlingResource(FillTheFormCompanion companion) {
        this.companion = companion;
    }

    @Override
    public String getName() {
        return ConfigurationStatusIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = companion.isConfigurationFinished();
        if (idle) {
            resourceCallback.onTransitionToIdle();
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
        this.resourceCallback = resourceCallback;
    }
}
