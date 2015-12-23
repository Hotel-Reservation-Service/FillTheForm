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
package com.hrs.filltheform.dialog;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.hrs.filltheform.R;
import com.hrs.filltheform.common.ConfigurationItem;
import com.hrs.filltheform.common.PropertyChangedListener;
import com.hrs.filltheform.data.RandomDataGenerator;

import java.util.List;

/**
 * FillTheFormDialog shows a list of input data available for the selected AccessibilityNode. It also gives the option to open the MainActivity.
 */
public class FillTheFormDialog implements PropertyChangedListener, FillTheFormDialogModel.FillTheFormDialogModelHelper {

    private WindowManager windowManager;
    private FrameLayout dialogView;
    private WindowManager.LayoutParams dialogParams;
    private View expandIcon;
    private View dialogMenu;
    private RecyclerView configurationItemsView;

    private Context appContext;
    private FillTheFormDialogModel model;
    private AccessibilityNodeInfo selectedNodeInfo;
    private int dialogInitialOffset;
    private ConfigurationItemsAdapter configurationItemsAdapter;

    public FillTheFormDialog(Context context) {
        this.appContext = context.getApplicationContext();
        // Get dialog dimensions
        Resources resources = appContext.getResources();
        int normalDialogWidth = resources.getDimensionPixelOffset(R.dimen.normal_dialog_width);
        int normalDialogHeight = resources.getDimensionPixelOffset(R.dimen.normal_dialog_height);
        int expandedDialogWidth = resources.getDimensionPixelOffset(R.dimen.expanded_dialog_width);
        int expandedDialogHeight = resources.getDimensionPixelOffset(R.dimen.expanded_dialog_height);
        this.dialogInitialOffset = normalDialogWidth;
        // Set up the model
        this.model = new FillTheFormDialogModel(this);
        model.setPropertyChangedListener(this);
        model.setNormalDialogDimensions(normalDialogWidth, normalDialogHeight);
        model.setExpandedDialogDimensions(expandedDialogWidth, expandedDialogHeight);
        model.setStatusBarHeight(getStatusBarHeight());
        // Prepare dialog view
        prepareDialogView();
        // Prepare random generator
        RandomDataGenerator.init();
    }

    public void showDialog(AccessibilityNodeInfo selectedNodeInfo, List<ConfigurationItem> selectedConfigurationItems) {
        this.selectedNodeInfo = selectedNodeInfo;
        model.showDialog(selectedConfigurationItems);
    }

    @Override
    public String getRandomValue(ConfigurationItem configurationItem) {
        if (RandomDataGenerator.isRandomKey(configurationItem.getValue())) {
            return RandomDataGenerator.getRandomContent(configurationItem.getValue());
        }
        return null;
    }

    private int getStatusBarHeight() {
        Resources resources = appContext.getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private void prepareDialogView() {
        windowManager = (WindowManager) appContext.getSystemService(Context.WINDOW_SERVICE);
        dialogParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);

        dialogView = new FrameLayout(appContext);
        dialogView.setOnTouchListener(new View.OnTouchListener() {
            Point screenSize = new Point();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        windowManager.getDefaultDisplay().getSize(screenSize);
                        model.setScreenDimensions(screenSize.x, screenSize.y);
                        model.setInitialDialogPosition(dialogParams.x, dialogParams.y);
                        model.setInitialTouchEvent(event.getRawX(), event.getRawY());
                        return true;
                    case MotionEvent.ACTION_UP:
                        model.onActionUp();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        model.onActionMove(event.getRawX(), event.getRawY());
                        return true;
                }
                return false;
            }
        });

        @SuppressLint("InflateParams") View dialogContent = LayoutInflater.from(appContext).inflate(R.layout.dialog, null);
        dialogMenu = dialogContent.findViewById(R.id.dialog_menu);
        expandIcon = dialogContent.findViewById(R.id.expand_icon);

        // Set up dialog content
        ImageButton closeButton = (ImageButton) dialogContent.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.onCloseButtonClicked();
            }
        });
        ImageButton minimizeButton = (ImageButton) dialogContent.findViewById(R.id.minimize_button);
        minimizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.onMinimizeButtonClicked();
            }
        });
        ImageButton openFillTheFormAppButton = (ImageButton) dialogContent.findViewById(R.id.open_fill_the_form_app_button);
        openFillTheFormAppButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.onOpenFillTheFormAppButtonClicked();
            }
        });

        // Configuration items list
        configurationItemsView = (RecyclerView) dialogContent.findViewById(R.id.configuration_items_view);
        configurationItemsView.setHasFixedSize(true);
        configurationItemsView.setLayoutManager(new LinearLayoutManager(appContext));
        // Set adapter
        configurationItemsAdapter = new ConfigurationItemsAdapter(appContext, model);
        configurationItemsView.setAdapter(configurationItemsAdapter);
        dialogView.addView(dialogContent);
    }

    // Setting up the dialog position

    private void setUpInitialDialogPosition() {
        final Rect outBounds = new Rect();
        selectedNodeInfo.getBoundsInScreen(outBounds);

        dialogParams.gravity = Gravity.START | Gravity.TOP;

        dialogParams.width = model.getNormalDialogWidth();
        dialogParams.height = model.getNormalDialogHeight();

        dialogParams.x = outBounds.right - dialogInitialOffset;
        dialogParams.y = outBounds.top;

        model.setDialogPosition(dialogParams.x, dialogParams.y);
    }

    // Fill selected node with data

    private void fillTheSelectedNodeWithData(String inputData) {
        Bundle arguments = new Bundle();
        arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, inputData);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            ClipboardManager clipboard = (ClipboardManager) appContext.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(inputData, inputData);
            clipboard.setPrimaryClip(clip);
            selectedNodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
        } else {
            selectedNodeInfo.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
        }
    }

    // Handle property changes

    @Override
    public void onPropertyChanged(String property) {
        switch (property) {
            case FillTheFormDialogModel.PROPERTY_DIALOG_VISIBILITY:
                if (model.isDialogVisible()) {
                    windowManager.addView(dialogView, dialogParams);
                } else {
                    windowManager.removeView(dialogView);
                }
                break;
            case FillTheFormDialogModel.PROPERTY_EXPAND_ICON:
                if (model.isExpandIconVisible()) {
                    expandIcon.setVisibility(View.VISIBLE);
                } else {
                    expandIcon.setVisibility(View.GONE);
                }
                break;
            case FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEMS_LIST:
                configurationItemsAdapter.notifyDataSetChanged();
                configurationItemsView.scrollToPosition(0);
                break;
            case FillTheFormDialogModel.PROPERTY_CONFIGURATION_ITEM_SELECTED:
                fillTheSelectedNodeWithData(model.getSelectedConfigItemValue());
                configurationItemsAdapter.notifyDataSetChanged();
                break;
            case FillTheFormDialogModel.PROPERTY_DIALOG_EXPANDED:
                if (model.isDialogExpanded()) {
                    dialogMenu.setVisibility(View.VISIBLE);
                    dialogParams.width = model.getExpandedDialogWidth();
                    dialogParams.height = model.getExpandedDialogHeight();
                } else {
                    dialogMenu.setVisibility(View.GONE);
                    dialogParams.width = model.getNormalDialogWidth();
                    dialogParams.height = model.getNormalDialogHeight();
                }
                windowManager.updateViewLayout(dialogView, dialogParams);
                break;
            case FillTheFormDialogModel.PROPERTY_DIALOG_INITIAL_POSITION:
                if (!model.isDialogExpanded()) {
                    setUpInitialDialogPosition();
                }
                break;
            case FillTheFormDialogModel.PROPERTY_DIALOG_POSITION:
                dialogParams.x = model.getDialogPositionX();
                dialogParams.y = model.getDialogPositionY();
                windowManager.updateViewLayout(dialogView, dialogParams);
                break;
            case FillTheFormDialogModel.PROPERTY_OPEN_FILL_THE_FORM_APP_BUTTON:
                Intent launchIntent = appContext.getPackageManager().getLaunchIntentForPackage(appContext.getPackageName());
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                appContext.startActivity(launchIntent);
                break;
            default:
                break;
        }
    }
}
