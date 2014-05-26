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

package com.krawczyk.lifesum.views.fragments;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.internal.CardHeader;
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.krawczyk.lifesum.Food;
import com.krawczyk.lifesum.FoodDao;
import com.krawczyk.lifesum.R;
import com.krawczyk.lifesum.views.cards.CustomExpandCard;
import com.krawczyk.lifesum.views.cards.FoodCard;

public abstract class BaseFragment extends Fragment {
    protected FoodDao mFoodDao;

    protected CardArrayAdapter mCardArrayAdapter;

    protected CardListView mListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitle();
        mListView = (CardListView) getActivity().findViewById(R.id.carddemo_list_gplaycard);
        getActivity().findViewById(R.id.progress).setVisibility(View.GONE);
    }

    protected void setTitle() {
        getActivity().setTitle(getTitleResourceId());
    }

    public abstract int getTitleResourceId();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // This ID represents the Home or Up button. In the case of this
                // activity, the Up button is shown. Use NavUtils to allow users
                // to navigate up one level in the application structure. For
                // more details, see the Navigation pattern on Android Design:
                //
                // http://developer.android.com/design/patterns/navigation.html#up-vs-back
                //
                NavUtils.navigateUpFromSameTask(getActivity());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setFoodDao(FoodDao foodDao) {
        mFoodDao = foodDao;
    }

    protected FoodCard createFoodCard(Food item) {
        CardHeader header = new CardHeader(getActivity());
        CustomExpandCard expand = new CustomExpandCard(getActivity());
        expand.setItem(item);
        header.setButtonExpandVisible(true);
        // Add Header to card
        FoodCard card = new FoodCard(this.getActivity());
        card.setItem(item);
        card.setTitle(item.getTitle());
        card.setSecondaryTitle(item.getBrand());
        card.setThirdTitle(item.getCalories() + getString(R.string.kcal));
        card.setId(Long.toString(item.getId()));
        card.setResourceIdThumbnail(R.drawable.ic_launcher);
        card.addCardHeader(header);
        card.addCardExpand(expand);
        card.init(mFoodDao);
        return card;
    }

    protected void populateCardList(ArrayList<Card> cards) {
        mCardArrayAdapter = new CardArrayAdapter(getActivity(), cards);

        // Enable undo controller!
        mCardArrayAdapter.setEnableUndo(false);

        // CardListView listView = (CardListView)
        // getActivity().findViewById(R.id.carddemo_list_gplaycard);
        if (mListView != null) {
            mListView.setAdapter(mCardArrayAdapter);
        }
    }

    public abstract void initCards(List<Food> foodItems);
}
