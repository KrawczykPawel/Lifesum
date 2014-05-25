/*
 * ******************************************************************************
 *   Copyright (c) 2013-2014 Gabriele Mariotti.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *  *****************************************************************************
 */

package com.krawczyk.lifesum.views.adapters;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.internal.CardThumbnail;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.krawczyk.lifesum.DaoMaster;
import com.krawczyk.lifesum.DaoMaster.DevOpenHelper;
import com.krawczyk.lifesum.DaoSession;
import com.krawczyk.lifesum.Food;
import com.krawczyk.lifesum.FoodDao;
import com.krawczyk.lifesum.R;

/**
 * List of Google Play cards Example with Undo Controller
 * 
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class ListUndoCardFragment extends BaseFragment {

    private CardArrayAdapter mCardArrayAdapter;

    private CardListView mListView;

    private List<Food> mSearchResultList;

    private SQLiteDatabase mSQLiteDatabase;

    private DaoMaster mDaoMaster;

    private DaoSession mDaoSession;

    private FoodDao mFoodDao;

    private Cursor mCursor;

    @Override
    public int getTitleResourceId() {
        return R.string.app_name;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.demo_fragment_list_gplaycard_undo, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView = (CardListView) getActivity().findViewById(R.id.carddemo_list_gplaycard);
    }

    private void initCards() {

        // Init an array of Cards
        ArrayList<Card> cards = new ArrayList<Card>();

        for (Food item : mSearchResultList) {
            CardHeader header = new CardHeader(getActivity());
            // Set the header title
            header.setTitle("");
            // Set visible the expand/collapse button
            header.setOtherButtonVisible(true);
            // Add a callback
            header.setOtherButtonClickListener(new CardHeader.OnClickCardHeaderOtherButtonListener() {
                @Override
                public void onButtonItemClick(Card card, View view) {
                    Toast.makeText(getActivity(), "Click on Other Button", Toast.LENGTH_LONG).show();
                }
            });
            CustomExpandCard expand = new CustomExpandCard(getActivity());
            expand.setItem(item);
            header.setButtonExpandVisible(true);
            // Use this code to set your drawable
            header.setOtherButtonDrawable(R.drawable.card_menu_button_other_add);
            // Add Header to card
            GooglePlaySmallCard card = new GooglePlaySmallCard(this.getActivity());
            card.setItem(item);
            card.setTitle(item.getTitle());
            card.setSecondaryTitle(item.getBrand());
            card.setThirdTitle(item.getCalories() + " Kcal");
            card.setId(Long.toString(item.getId()));
            card.setResourceIdThumbnail(R.drawable.ic_launcher);
            card.addCardHeader(header);
            card.addCardExpand(expand);
            card.init();
            cards.add(card);
        }

        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        // Enable undo controller!
        mCardArrayAdapter.setEnableUndo(true);

        // CardListView listView = (CardListView)
        // getActivity().findViewById(R.id.carddemo_list_gplaycard);
        if (mListView != null) {
            mListView.setAdapter(mCardArrayAdapter);
        }
    }

    /**
     * @param searchResultList the searchResultList to set
     */
    public void setSearchResultList(List<Food> searchResultList) {
        mSearchResultList = searchResultList;
        initializeDao();
        initCards();
    }

    private void initializeDao() {
        DevOpenHelper helper = new DaoMaster.DevOpenHelper(getActivity(), "food-db", null);
        mSQLiteDatabase = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mSQLiteDatabase);
        mDaoSession = mDaoMaster.newSession();
        mFoodDao = mDaoSession.getFoodDao();
    }

    /**
     * This class provides a simple card as Google Play *
     * 
     * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
     */
    public class GooglePlaySmallCard extends Card {

        protected TextView mTitle;

        protected TextView mSecondaryTitle;

        protected TextView mThirdTitle;

        protected int resourceIdThumbnail;

        protected int count;

        protected String title;

        protected String secondaryTitle;

        protected String thirdTitle;

        protected float rating;

        private Food item;

        public GooglePlaySmallCard(Context context) {
            this(context, R.layout.carddemo_mycard_inner_content);
        }

        public GooglePlaySmallCard(Context context, int innerLayout) {
            super(context, innerLayout);
            // init();
        }

        private void init() {

            // Add thumbnail
            CardThumbnail cardThumbnail = new CardThumbnail(mContext);

            if (resourceIdThumbnail == 0)
                cardThumbnail.setDrawableResource(R.drawable.ic_std_launcher);
            else {
                cardThumbnail.setDrawableResource(resourceIdThumbnail);
            }

            addCardThumbnail(cardThumbnail);

            setSwipeable(true);

            setOnSwipeListener(new OnSwipeListener() {
                @Override
                public void onSwipe(Card card) {
                    Toast.makeText(getContext(), "Saved food to db= " + title, Toast.LENGTH_SHORT).show();
                    mFoodDao.insert(item);

                }
            });

            setOnUndoSwipeListListener(new OnUndoSwipeListListener() {
                @Override
                public void onUndoSwipe(Card card) {
                    Toast.makeText(getContext(), "Undo food save= " + title, Toast.LENGTH_SHORT).show();
                    mFoodDao.delete(item);
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
                mTitle.setText(title);

            if (mSecondaryTitle != null)
                mSecondaryTitle.setText(secondaryTitle);
            if (mThirdTitle != null)
                mThirdTitle.setText(thirdTitle);

        }

        public Food getItem() {
            return item;
        }

        public void setItem(Food item) {
            this.item = item;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public void setTitle(String title) {
            this.title = title;
        }

        public String getSecondaryTitle() {
            return secondaryTitle;
        }

        public String getThirdTitle() {
            return thirdTitle;
        }

        public void setThirdTitle(String thirdTitle) {
            this.thirdTitle = thirdTitle;
        }

        public void setSecondaryTitle(String secondaryTitle) {
            this.secondaryTitle = secondaryTitle;
        }

        public int getResourceIdThumbnail() {
            return resourceIdThumbnail;
        }

        public void setResourceIdThumbnail(int resourceIdThumbnail) {
            this.resourceIdThumbnail = resourceIdThumbnail;
        }
    }

}
