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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hrs.filltheform.R;
import com.hrs.filltheform.common.PropertyChangedListener;
import com.hrs.filltheform.common.reader.ConfigurationReader;
import com.hrs.filltheform.service.MyAccessibilityService;
import com.hrs.filltheform.util.ToastUtil;

import java.util.List;

/**
 * MainActivity takes care of enabling permissions, selecting/loading the configuration file and launching the configured app.
 */
public class MainActivity extends AppCompatActivity implements PropertyChangedListener {

    private static final String SAMPLE_APP_CONFIG_FILE_NAME = "sample_app_config.xml";
    private static final String ACCESSIBILITY_SERVICE = "com.hrs.filltheform/com.hrs.filltheform.service.MyAccessibilityService";

    public static final String MY_SHARED_PREFERENCES = "FillTheFormPreferences";
    private static final int SYSTEM_ALERT_WINDOW_REQUEST_CODE = 123;
    private static final int READ_EXTERNAL_STORAGE_REQUEST_CODE = 124;
    private static final int FILE_OPEN_REQUEST_CODE = 125;

    private static final String MODEL_KEY = "model_key";
    private static final String CONFIGURATION_FILE_PATH_KEY = "configuration_file_path_key";
    private static final String CONFIGURATION_FILE_NAME_KEY = "configuration_file_name_key";

    private MainActivityModel model;

    private View permissionsHeader;
    private View systemAlertWindowContainer;
    private View loadConfigurationFileContainer;
    private View loadedPackageNamesProgressBar;
    private View loadedPackageNamesEmpty;
    private TextView configurationFileName;
    private View accessibilityServiceContainer;
    private View readExternalStorageContainer;
    private LinearLayout loadedPackageNamesContainer;
    private LinearLayout loadedPackageNamesList;

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(MyAccessibilityService.INTENT_SEND_LOADED_PACKAGE_NAMES)) {
                List<String> loadedPackageNames = intent.getStringArrayListExtra(MyAccessibilityService.INTENT_EXTRA_PACKAGE_NAMES);
                model.onPackageNamesLoaded(loadedPackageNames);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpModel(savedInstanceState);
        bindViews();
        readConfigurationFileAttributesFromSharedPreferences();
    }

    private void setUpModel(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            model = new MainActivityModel();
        } else {
            model = savedInstanceState.getParcelable(MODEL_KEY);
        }
        if (model != null) {
            model.setPropertyChangedListener(this);
        }
    }

    private void bindViews() {
        permissionsHeader = findViewById(R.id.permissions_header);
        systemAlertWindowContainer = findViewById(R.id.system_alert_window_container);
        Button systemAlertWindowButton = (Button) findViewById(R.id.system_alert_window_button);
        systemAlertWindowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.onEnableSystemAlertWindowContainerButtonClicked();
            }
        });
        accessibilityServiceContainer = findViewById(R.id.accessibility_service_container);
        Button accessibilityServiceButton = (Button) findViewById(R.id.accessibility_service_button);
        accessibilityServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.onEnableAccessibilityServiceButtonClicked();
            }
        });
        readExternalStorageContainer = findViewById(R.id.read_external_storage_container);
        Button readExternalStorageButton = (Button) findViewById(R.id.read_external_storage_button);
        readExternalStorageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.onEnableReadExternalStorageButtonClicked();
            }
        });
        loadConfigurationFileContainer = findViewById(R.id.load_configuration_file_container);
        Button loadConfigurationFileButton = (Button) findViewById(R.id.load_configuration_file_button);
        loadConfigurationFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.onLoadConfigurationFileButtonClicked();
            }
        });
        Button selectConfigurationFileButton = (Button) findViewById(R.id.select_configuration_file_button);
        selectConfigurationFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.onSelectConfigurationFileButtonClicked();
            }
        });
        configurationFileName = (TextView) findViewById(R.id.configuration_file_name);
        loadedPackageNamesContainer = (LinearLayout) findViewById(R.id.loaded_package_names_container);
        loadedPackageNamesList = (LinearLayout) findViewById(R.id.loaded_package_names_list);
        loadedPackageNamesProgressBar = findViewById(R.id.loaded_package_names_progress_bar);
        loadedPackageNamesEmpty = findViewById(R.id.loaded_package_names_empty);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
        // Register broadcast receiver
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MyAccessibilityService.INTENT_SEND_LOADED_PACKAGE_NAMES);
        registerReceiver(broadcastReceiver, intentFilter);
        // Ask for package names
        Intent intent = new Intent(MyAccessibilityService.INTENT_ASK_FOR_LOADED_PACKAGE_NAMES);
        sendBroadcast(intent);
    }

    private void checkPermissions() {
        boolean systemAlertWindowPermissionEnabled = PermissionsManager.isSystemAlertWindowPermissionEnabled(this);
        boolean readExternalStoragePermissionEnabled = PermissionsManager.isReadExternalStoragePermissionEnabled(this);
        boolean accessibilityServiceEnabled = PermissionsManager.isAccessibilityServiceEnabled(this, ACCESSIBILITY_SERVICE);
        // Set up the model
        model.setSystemAlertWindowPermissionEnabled(systemAlertWindowPermissionEnabled);
        model.setAccessibilityServiceEnabled(accessibilityServiceEnabled);
        model.setReadExternalStorageEnabled(readExternalStoragePermissionEnabled);
        model.checkIfLoadingOfConfigurationFileIsAllowed();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MODEL_KEY, model);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    // Options menu

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_accessibility_settings) {
            PermissionsManager.openAccessibilitySettings(this);
        } else if (id == R.id.action_settings) {
            PermissionsManager.openAppSettings(this);
            return true;
        } else if (id == R.id.action_draw_over_other_apps_setting) {
            PermissionsManager.askForSystemAlertWindowPermission(this, SYSTEM_ALERT_WINDOW_REQUEST_CODE);
        } else if (id == R.id.sample_app_configuration_file) {
            model.setConfigurationFileAttributes(null, SAMPLE_APP_CONFIG_FILE_NAME);
        }

        return super.onOptionsItemSelected(item);
    }

    // Handle permissions

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == READ_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                model.setReadExternalStorageEnabled(true);
            } else {
                model.setReadExternalStorageEnabled(false);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SYSTEM_ALERT_WINDOW_REQUEST_CODE) {
            model.setSystemAlertWindowPermissionEnabled(PermissionsManager.isSystemAlertWindowPermissionEnabled(this));
        } else if (requestCode == FILE_OPEN_REQUEST_CODE && resultCode == RESULT_OK) {
            Uri configurationFileUri;
            if (data != null) {
                configurationFileUri = data.getData();
                String configurationFileName = getFileNameFromUri(configurationFileUri);
                model.setConfigurationFileAttributes(configurationFileUri.toString(), configurationFileName);
                model.saveConfigurationFileAttributes();
                getContentResolver().takePersistableUriPermission(configurationFileUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }
        }
    }

    private String getFileNameFromUri(Uri uri) {
        String fileName = null;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            cursor.moveToFirst();
            fileName = cursor.getString(nameIndex);
            cursor.close();
        }
        return fileName;
    }

    // Property changes handling

    @Override
    public void onPropertyChanged(String property) {
        switch (property) {
            case MainActivityModel.PROPERTY_PERMISSIONS_HEADER:
                if (model.isPermissionsHeaderVisible()) {
                    permissionsHeader.setVisibility(View.VISIBLE);
                } else {
                    permissionsHeader.setVisibility(View.GONE);
                }
                break;
            case MainActivityModel.PROPERTY_SYSTEM_ALERT_WINDOW_CONTAINER:
                if (model.isSystemAlertWindowContainerVisible()) {
                    systemAlertWindowContainer.setVisibility(View.VISIBLE);
                } else {
                    systemAlertWindowContainer.setVisibility(View.GONE);
                }
                break;
            case MainActivityModel.PROPERTY_SYSTEM_ALERT_WINDOW_PERMISSION:
                PermissionsManager.askForSystemAlertWindowPermission(this, SYSTEM_ALERT_WINDOW_REQUEST_CODE);
                break;
            case MainActivityModel.PROPERTY_READ_EXTERNAL_STORAGE_CONTAINER:
                if (model.isReadExternalStorageContainerVisible()) {
                    readExternalStorageContainer.setVisibility(View.VISIBLE);
                } else {
                    readExternalStorageContainer.setVisibility(View.GONE);
                }
                break;
            case MainActivityModel.PROPERTY_READ_EXTERNAL_STORAGE_PERMISSION:
                PermissionsManager.askForReadExternalStoragePermission(this, READ_EXTERNAL_STORAGE_REQUEST_CODE);
                break;
            case MainActivityModel.PROPERTY_ACCESSIBILITY_SERVICE_CONTAINER:
                if (model.isAccessibilityServiceContainerVisible()) {
                    accessibilityServiceContainer.setVisibility(View.VISIBLE);
                } else {
                    accessibilityServiceContainer.setVisibility(View.GONE);
                }
                break;
            case MainActivityModel.PROPERTY_ACCESSIBILITY_SERVICE_BUTTON:
                PermissionsManager.openAccessibilitySettings(this);
                break;
            case MainActivityModel.PROPERTY_LOAD_CONFIGURATION_FILE_CONTAINER:
                if (model.isLoadConfigurationFileContainerVisible()) {
                    loadConfigurationFileContainer.setVisibility(View.VISIBLE);
                } else {
                    loadConfigurationFileContainer.setVisibility(View.GONE);
                }
                break;
            case MainActivityModel.PROPERTY_CONFIGURATION_FILE_ATTRIBUTES:
                configurationFileName.setText(model.getConfigurationFileName());
                break;
            case MainActivityModel.PROPERTY_SAVE_CONFIGURATION_FILE_ATTRIBUTES:
                storeConfigurationFileAttributesInSharedPreferences();
                break;
            case MainActivityModel.PROPERTY_LOAD_CONFIGURATION_FILE_BUTTON:
                Intent intent = new Intent(MyAccessibilityService.INTENT_READ_CONFIGURATION_FILE);
                String configurationFilePath = model.getConfigurationFilePath();
                if (TextUtils.isEmpty(configurationFilePath)) {
                    intent.putExtra(MyAccessibilityService.INTENT_EXTRA_CONFIGURATION_FILE_PATH, model.getConfigurationFileName());
                    intent.putExtra(MyAccessibilityService.INTENT_EXTRA_CONFIGURATION_FILE_SOURCE, ConfigurationReader.SOURCE_ASSETS);
                } else {
                    intent.putExtra(MyAccessibilityService.INTENT_EXTRA_CONFIGURATION_FILE_PATH, configurationFilePath);
                    intent.putExtra(MyAccessibilityService.INTENT_EXTRA_CONFIGURATION_FILE_SOURCE, ConfigurationReader.SOURCE_OTHER);
                }
                sendBroadcast(intent);
                break;
            case MainActivityModel.PROPERTY_SELECT_CONFIGURATION_FILE_BUTTON:
                intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                startActivityForResult(intent, FILE_OPEN_REQUEST_CODE);
                break;
            case MainActivityModel.PROPERTY_LOADED_PACKAGE_NAMES_CONTAINER:
                if (model.isLoadedPackageNamesContainerVisible()) {
                    loadedPackageNamesContainer.setVisibility(View.VISIBLE);
                } else {
                    loadedPackageNamesContainer.setVisibility(View.GONE);
                }
                break;
            case MainActivityModel.PROPERTY_CONFIGURATION_LOADING_IN_PROGRESS:
                if (model.isConfigurationLoadingInProgress()) {
                    loadedPackageNamesProgressBar.setVisibility(View.VISIBLE);
                } else {
                    loadedPackageNamesProgressBar.setVisibility(View.GONE);
                }
                break;
            case MainActivityModel.PROPERTY_LOADED_PACKAGE_NAMES_EMPTY:
                if (model.isLoadedPackageNamesEmptyVisible()) {
                    loadedPackageNamesEmpty.setVisibility(View.VISIBLE);
                } else {
                    loadedPackageNamesEmpty.setVisibility(View.GONE);
                }
                break;
            case MainActivityModel.PROPERTY_LOADED_PACKAGE_NAMES_LIST:
                if (model.isLoadedPackageNamesListVisible()) {
                    populateLoadedPackageNamesList();
                    loadedPackageNamesList.setVisibility(View.VISIBLE);
                } else {
                    loadedPackageNamesList.setVisibility(View.GONE);
                    loadedPackageNamesList.removeAllViews();
                }
                break;
            default:
                break;
        }
    }

    // Show loaded package names

    private void populateLoadedPackageNamesList() {
        loadedPackageNamesList.removeAllViews();
        final List<String> loadedPackageNames = model.getLoadedPackageNames();
        for (int i = 0; i < loadedPackageNames.size(); i++) {
            final String packageName = loadedPackageNames.get(i);
            Button button = new Button(this);
            button.setText(packageName);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage(packageName);
                    if (launchIntent != null) {
                        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(launchIntent);
                    } else {
                        ToastUtil.show(getApplicationContext(), getString(R.string.app_is_not_installed));
                    }
                }
            });
            loadedPackageNamesList.addView(button);
        }
    }

    // Configuration file attributes management

    private void storeConfigurationFileAttributesInSharedPreferences() {
        SharedPreferences.Editor editor = getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE).edit();
        editor.putString(CONFIGURATION_FILE_PATH_KEY, model.getConfigurationFilePath());
        editor.putString(CONFIGURATION_FILE_NAME_KEY, model.getConfigurationFileName());
        editor.apply();
    }

    private void readConfigurationFileAttributesFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences(MY_SHARED_PREFERENCES, MODE_PRIVATE);
        String configurationFilePath = prefs.getString(CONFIGURATION_FILE_PATH_KEY, null);
        String configurationFileName = prefs.getString(CONFIGURATION_FILE_NAME_KEY, null);
        if (configurationFilePath != null) {
            model.setConfigurationFileAttributes(configurationFilePath, configurationFileName);
        } else {
            model.setConfigurationFileAttributes(null, SAMPLE_APP_CONFIG_FILE_NAME);
        }
    }
}
