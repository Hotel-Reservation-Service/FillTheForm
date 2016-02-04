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
package com.hrs.filltheform.data;

import android.content.Context;

import com.hrs.filltheform.R;
import com.hrs.filltheform.util.LogUtil;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * ConfigurationVariables defines variables that can be used in the configuration file.
 * These variables are useful when we want to provide more data about the device.
 */
public class ConfigurationVariables {

    private static final String TAG = ConfigurationVariables.class.getSimpleName();

    // Device info
    private static final String DEVICE_MANUFACTURER = "device_manufacturer";
    private static final String DEVICE_MODEL = "device_model";
    private static final String DEVICE_ANDROID_VERSION = "device_android_version";
    private static final String DEVICE_IP_ADDRESS = "device_ip_address";

    private RandomDataGenerator randomDataGenerator;
    private String deviceIpAddress;
    private final String deviceIpAddressFailure;

    public ConfigurationVariables(Context context) {
        deviceIpAddressFailure = context.getString(R.string.device_ip_address_failure);
        this.randomDataGenerator = new RandomDataGenerator();
    }

    public void clear() {
        this.deviceIpAddress = null;
    }

    public boolean isConfigurationVariableKey(String variableKey) {
        switch (variableKey) {
            case DEVICE_MANUFACTURER:
            case DEVICE_MODEL:
            case DEVICE_ANDROID_VERSION:
            case DEVICE_IP_ADDRESS:
                return true;
            default:
                return randomDataGenerator.isRandomVariableKey(variableKey);
        }
    }

    /**
     * @param variableKey Defines the wanted value.
     * @return Null if there is no value available for the provided variableKey.
     */
    public String getValue(String variableKey) {
        switch (variableKey) {
            case DEVICE_MANUFACTURER:
                return android.os.Build.MANUFACTURER;
            case DEVICE_MODEL:
                return android.os.Build.MODEL;
            case DEVICE_ANDROID_VERSION:
                return String.valueOf(android.os.Build.VERSION.SDK_INT);
            case DEVICE_IP_ADDRESS:
                if (deviceIpAddress == null) {
                    deviceIpAddress = getLocalIpAddress();
                }
                return deviceIpAddress;
            default:
                return randomDataGenerator.getRandomContent(variableKey);
        }
    }

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface networkInterface = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddress = networkInterface.getInetAddresses(); enumIpAddress.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddress.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            LogUtil.d(TAG, ex.toString());
        }
        return deviceIpAddressFailure;
    }
}
