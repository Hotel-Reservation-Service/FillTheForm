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
package com.hrs.filltheform.common.event;

import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;

import com.hrs.filltheform.common.ConfigurationItem;

import java.util.List;

/**
 * EventResolverListener is used to receive results from EventResolver.
 */
public interface EventResolverListener {
    void onDataForSelectedNodeAvailable(AccessibilityNodeInfoCompat selectedNodeInfo, int accessibilityEventType, List<ConfigurationItem> selectedConfigurationItems);

    void onDataForSelectedNodeNotAvailable(AccessibilityNodeInfoCompat selectedNodeInfo);
}
