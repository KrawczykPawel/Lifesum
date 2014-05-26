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

package com.krawczyk.lifesum.views.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.krawczyk.lifesum.Food;
import com.krawczyk.lifesum.FoodDao;
import com.krawczyk.lifesum.R;

/**
 * @class FoodCard
 * @author Pawel Krawczyk
 * @since 25-05-2014 Card for presenting food data on the list.
 */
public class FoodCard extends Card {

    protected TextView mTitle;

    protected TextView mSecondaryTitle;

    protected TextView mThirdTitle;

    protected int mResourceIdThumbnail;

    protected int mCount;

    protected String mTitleString;

    protected String mSecondaryTitleString;

    protected String mThirdTitleString;

    private Food mFood;

    public FoodCard(Context context) {
        this(context, R.layout.food_item);
    }

    public FoodCard(Context context, int innerLayout) {
        super(context, innerLayout);
        // init();
    }

    /*
     * Initialize food card by adding thumbnail and swipe listeners with
     * actions.
     */
    public void init(final FoodDao foodDao) {

        // Add thumbnail
        CardThumbnail cardThumbnail = new CardThumbnail(mContext);

        if (mResourceIdThumbnail == 0)
            cardThumbnail.setDrawableResource(R.drawable.ic_std_launcher);
        else {
            cardThumbnail.setDrawableResource(mResourceIdThumbnail);
        }

        addCardThumbnail(cardThumbnail);

        setSwipeable(true);

        setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipe(Card card) {
                // Save food to db
                Toast.makeText(getContext(), "Saved food to db= " + mTitleString, Toast.LENGTH_SHORT).show();
                foodDao.insert(mFood);

            }
        });

        setOnUndoSwipeListListener(new OnUndoSwipeListListener() {
            @Override
            public void onUndoSwipe(Card card) {
                // Delete food from db
                Toast.makeText(getContext(), "Undo food save= " + mTitleString, Toast.LENGTH_SHORT).show();
                foodDao.delete(mFood);
            }
        });

    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        // Retrieve elements
        mTitle = (TextView) parent.findViewById(R.id.carddemo_myapps_main_inner_title);
        mSecondaryTitle = (TextView) parent.findViewById(R.id.carddemo_myapps_main_inner_secondaryTitle);
        mThirdTitle = (TextView) parent.findViewById(R.id.carddemo_myapps_main_inner_thirdTitle);

        if (mTitle != null)
            mTitle.setText(mTitleString);

        if (mSecondaryTitle != null)
            mSecondaryTitle.setText(mSecondaryTitleString);
        if (mThirdTitle != null)
            mThirdTitle.setText(mThirdTitleString);

    }

    public Food getItem() {
        return mFood;
    }

    public void setItem(Food item) {
        this.mFood = item;
    }

    @Override
    public String getTitle() {
        return mTitleString;
    }

    @Override
    public void setTitle(String title) {
        this.mTitleString = title;
    }

    public String getSecondaryTitle() {
        return mSecondaryTitleString;
    }

    public String getThirdTitle() {
        return mThirdTitleString;
    }

    public void setThirdTitle(String thirdTitle) {
        this.mThirdTitleString = thirdTitle;
    }

    public void setSecondaryTitle(String secondaryTitle) {
        this.mSecondaryTitleString = secondaryTitle;
    }

    public int getResourceIdThumbnail() {
        return mResourceIdThumbnail;
    }

    public void setResourceIdThumbnail(int resourceIdThumbnail) {
        this.mResourceIdThumbnail = resourceIdThumbnail;
    }
}
