/*
 * Copyright (C) 2014 Pawel Krawczyk
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.krawczyk.lifesum.views.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.krawczyk.lifesum.Food;
import com.krawczyk.lifesum.R;

/**
 * @class FoodItemAdapter
 * @author Pawel Krawczyk
 * @since 23-05-2014 Classic food item adater for simple list view.
 */
public class FoodItemAdapter extends BaseAdapter {
    private static List<Food> mList;

    private final LayoutInflater mInflater;

    private final Context mContext;

    public FoodItemAdapter(Context context, List<Food> results) {
        super();
        mContext = context;
        mList = results;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Food item = mList.get(position);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.food_list_row, null);
            holder = new ViewHolder();
            holder.mName = (TextView) convertView.findViewById(R.id.column_1);
            holder.mFoodType = (TextView) convertView.findViewById(R.id.column_2);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mName.setText(item.getTitle());
        holder.mFoodType.setText(item.getBrand());
        return convertView;
    }

    /*
     * View holder
     */
    private static class ViewHolder {
        TextView mName;

        TextView mFoodType;
    }
}
