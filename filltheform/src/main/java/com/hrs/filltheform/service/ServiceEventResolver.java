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
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.view.accessibility.AccessibilityRecordCompat;
import android.view.accessibility.AccessibilityEvent;

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

    private final ServiceConfiguration configuration;
    private EventResolverListener eventResolverListener;

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

        String eventPackageName = null;

        for (int i = 0; i < configuration.getPackageNames().size(); i++) {
            if (event.getPackageName().equals(configuration.getPackageNames().get(i))) {
                eventPackageName = configuration.getPackageNames().get(i);
                break;
            }
        }

        if (eventPackageName == null) {
            return;
        }

        final AccessibilityRecordCompat record = AccessibilityEventCompat.asRecord(event);
        final AccessibilityNodeInfoCompat node = record.getSource();

        if (node == null) {
            return;
        }

        boolean found = false;
        for (int i = 0; i < configuration.getIdGroups().size(); i++) {
            String idGroupKey = configuration.getIdGroups().keyAt(i);
            String targetViewId = eventPackageName + ":id/" + idGroupKey;
            if (targetViewId.equals(node.getViewIdResourceName())) {
                found = true;
                notifyEventResolverListener(node, event, idGroupKey);
                break;
            } else {
                List<AccessibilityNodeInfoCompat> nodeInfoList = node.findAccessibilityNodeInfosByViewId(targetViewId);
                if (nodeInfoList != null && nodeInfoList.size() > 0) {
                    AccessibilityNodeInfoCompat nodeInfo = nodeInfoList.get(0);
                    found = true;
                    notifyEventResolverListener(nodeInfo, event, idGroupKey);
                    break;
                }
            }
        }
        if (!found) {
            if (eventResolverListener != null) {
                eventResolverListener.onDataForSelectedNodeNotAvailable(node);
            }
        }
    }

    private void notifyEventResolverListener(AccessibilityNodeInfoCompat nodeInfo, AccessibilityEvent event, String idGroupKey) {
        List<ConfigurationItem> selectedConfigurationItems = configuration.getIdGroups().get(idGroupKey);
        if (eventResolverListener != null) {
            eventResolverListener.onDataForSelectedNodeAvailable(nodeInfo, event.getEventType(), selectedConfigurationItems);
        }
    }
}
