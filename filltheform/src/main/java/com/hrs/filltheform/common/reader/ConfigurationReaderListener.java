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
package com.hrs.filltheform.common.reader;

import com.hrs.filltheform.common.ConfigurationItem;

/**
 * ConfigurationReaderListener receives the information about the status and items read by of ConfigurationReader.
 */
public interface ConfigurationReaderListener {
    /**
     * Called when ConfigurationReader has read a new package name.
     *
     * @param packageName Package name that has just been read.
     */
    void onPackageName(String packageName);

    /**
     * Called when ConfigurationReader has read a new configuration item.
     *
     * @param configurationItem Configuration item that has just been read.
     */
    void onConfigurationItem(ConfigurationItem configurationItem);

    /**
     * Called when reading is finished.
     */
    void onReadingCompleted();

    /**
     * Called when an error occurs during the reading.
     *
     * @param errorMessage Error message.
     */
    void onReadingFailed(String errorMessage);
}
