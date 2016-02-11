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

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hrs.filltheform.R;
import com.hrs.filltheform.common.ConfigurationItem;

/**
 * Adapter for the list of items shown in the FillTheFormDialog.
 */
public class ConfigurationItemsAdapter extends RecyclerView.Adapter<ConfigurationItemsAdapter.ViewHolder> {

    private final Context appContext;
    private final FillTheFormDialogModel model;

    public ConfigurationItemsAdapter(Context context, FillTheFormDialogModel model) {
        this.appContext = context.getApplicationContext();
        this.model = model;
    }

    @Override
    public ConfigurationItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View menuItemView = LayoutInflater.from(appContext).inflate(R.layout.dialog_menu_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(menuItemView);

        int textViewStyle = R.style.DialogItemNormal;

        if (viewType == FillTheFormDialogModel.VIEW_TYPE_SELECTED_ITEM) {
            textViewStyle = R.style.DialogItemPressed;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            viewHolder.valueTextView.setTextAppearance(textViewStyle);
        } else {
            //noinspection deprecation
            viewHolder.valueTextView.setTextAppearance(appContext, textViewStyle);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        ConfigurationItem configurationItem = model.getConfigurationItem(position);

        if (configurationItem != null) {
            viewHolder.valueTextView.setText(configurationItem.getValue());

            if (configurationItem.getProfile() != null) {
                viewHolder.profileTextView.setText(configurationItem.getProfile());
            } else {
                viewHolder.profileTextView.setText(appContext.getString(R.string.profile_not_found));
            }

            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    model.onConfigurationItemClicked(position);
                }
            });

            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    model.onConfigurationItemLongClicked(position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return model.getItemsCount();
    }

    @Override
    public int getItemViewType(int position) {
        return model.getSortedConfigItemType(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView valueTextView;
        public final TextView profileTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            this.valueTextView = (TextView) itemView.findViewById(R.id.value);
            this.profileTextView = (TextView) itemView.findViewById(R.id.profile);
        }
    }
}
