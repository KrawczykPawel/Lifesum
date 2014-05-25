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
import it.gmariotti.cardslib.library.view.CardListView;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.krawczyk.lifesum.Food;
import com.krawczyk.lifesum.R;

/**
 * List of Google Play cards Example with Undo Controller
 * 
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class StoredFragment extends BaseFragment {

    private CardArrayAdapter mCardArrayAdapter;

    private CardListView mListView;

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
        initCards(mFoodDao.loadAll());
    }

    public void initCards(List<Food> foodItems) {
        // Init an array of Cards
        ArrayList<Card> cards = new ArrayList<Card>();
        if (foodItems != null && foodItems.size() > 0) {
            for (Food item : foodItems) {
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
                FoodCard card = new FoodCard(this.getActivity());
                card.setItem(item);
                card.setTitle(item.getTitle());
                card.setSecondaryTitle(item.getBrand());
                card.setThirdTitle(item.getCalories() + " Kcal");
                card.setId(Long.toString(item.getId()));
                card.setResourceIdThumbnail(R.drawable.ic_launcher);
                card.addCardHeader(header);
                card.addCardExpand(expand);
                card.init(mFoodDao);
                cards.add(card);
            }
        } else {
            FoodCard card = new FoodCard(this.getActivity());
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
}
