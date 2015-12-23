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
package com.hrs.filltheform.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;

import com.hrs.filltheform.R;
import com.hrs.filltheform.util.LogUtil;
import com.hrs.filltheform.util.ToastUtil;

/**
 * Helper class for easier permissions handling.
 */
class PermissionsManager {

    private static final String TAG = PermissionsManager.class.getSimpleName();

    // Check

    public static boolean isSystemAlertWindowPermissionEnabled(AppCompatActivity activity) {
        //noinspection SimplifiableIfStatement
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(activity);
        }
        return true;
    }

    // Ask

    public static void askForSystemAlertWindowPermission(AppCompatActivity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, requestCode);
        } else {
            ToastUtil.show(activity, activity.getString(R.string.permission_system_alert_window_already_enabled));
        }
    }

    // App settings

    public static void openAppSettings(AppCompatActivity activity) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    // Accessibility settings

    public static void openAccessibilitySettings(AppCompatActivity activity) {
        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        activity.startActivity(intent);
    }

    // Check if accessibility service is enabled

    public static boolean isAccessibilityServiceEnabled(Context context, String accessibilityService) {
        int accessibilityServiceEnabled = 0;
        try {
            accessibilityServiceEnabled = Settings.Secure.getInt(
                    context.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            LogUtil.e(TAG, "Setting not found: "
                    + e.getMessage());
        }

        if (accessibilityServiceEnabled == 1) {
            TextUtils.SimpleStringSplitter stringColonSplitter = new TextUtils.SimpleStringSplitter(':');

            String settingValue = Settings.Secure.getString(
                    context.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);

            if (settingValue != null) {
                stringColonSplitter.setString(settingValue);
                while (stringColonSplitter.hasNext()) {
                    String service = stringColonSplitter.next();

                    if (service.equalsIgnoreCase(accessibilityService)) {
                        LogUtil.v(TAG, "Accessibility service is enabled");
                        return true;
                    }
                }
            }
        }

        LogUtil.v(TAG, "Accessibility service is disabled");
        return false;
    }
}
