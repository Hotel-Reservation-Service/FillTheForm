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

import android.support.annotation.NonNull;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.hrs.filltheform.common.ConfigurationItem;
import com.hrs.filltheform.common.event.EventResolver;
import com.hrs.filltheform.common.event.EventResolverListener;

import java.util.List;

/**
 * ServiceEventResolver manages Accessibility Events received by MyAccessibilityService.
 */
public class ServiceEventResolver implements EventResolver {

    private static final String APP_PACKAGE_NAME = "com.hrs.filltheform";
    private static final String ANDROID_SYSTEM_UI_PREFIX = "com.android.systemui";

    private EventResolverListener eventResolverListener;
    private ServiceConfiguration configuration;

    public ServiceEventResolver(@NonNull ServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    public void setEventResolverListener(EventResolverListener eventResolverListener) {
        this.eventResolverListener = eventResolverListener;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (APP_PACKAGE_NAME.equals(event.getPackageName())) {
            return;
        } else if (ANDROID_SYSTEM_UI_PREFIX.equals(event.getPackageName())) {
            return;
        }

        AccessibilityNodeInfo node = event.getSource();
        if (node == null) {
            return;
        }

        String eventPackageName = null;

        for (int i = 0; i < configuration.getPackageNames().size(); i++) {
            if (event.getPackageName().equals(configuration.getPackageNames().get(i))) {
                eventPackageName = configuration.getPackageNames().get(i);
                break;
            }
        }

        if (eventPackageName != null) {
            boolean found = false;
            for (int i = 0; i < configuration.getIdGroups().size(); i++) {
                String idGroupKey = configuration.getIdGroups().keyAt(i);
                List<AccessibilityNodeInfo> nodeInfoList = node.findAccessibilityNodeInfosByViewId(eventPackageName + ":id/" + idGroupKey);
                if (nodeInfoList != null && nodeInfoList.size() > 0) {
                    AccessibilityNodeInfo info = nodeInfoList.get(0);
                    found = true;
                    List<ConfigurationItem> selectedConfigurationItems = configuration.getIdGroups().get(idGroupKey);
                    if (eventResolverListener != null) {
                        eventResolverListener.onDataForSelectedNodeAvailable(info, selectedConfigurationItems);
                    }
                    break;
                }
            }
            if (!found) {
                if (eventResolverListener != null) {
                    eventResolverListener.onDataForSelectedNodeNotAvailable(node);
                }
            }
        }
    }
}
