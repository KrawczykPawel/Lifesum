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

package com.krawczyk.lifesum.views.fragments;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnSwipeListener;
import it.gmariotti.cardslib.library.internal.Card.OnUndoSwipeListListener;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.Toast;

import com.krawczyk.lifesum.Food;
import com.krawczyk.lifesum.R;
import com.krawczyk.lifesum.views.cards.FoodCard;

/**
 * @class StoredFragment
 * @author Pawel Krawczyk
 * @since 25-05-2014 Fragment class for stored view, with add/delete food
 *        listeners on swipe actions.
 */
public class StoredFragment extends BaseFragment {

    @Override
    public int getTitleResourceId() {
        return R.string.fragment_stored;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initCards(mFoodDao.loadAll());
    }

    @Override
    public void initCards(List<Food> foodItems) {
        // Init an array of Cards
        ArrayList<Card> cards = new ArrayList<Card>();
        for (final Food item : foodItems) {
            FoodCard card = createFoodCard(item);
            card.setOnUndoSwipeListListener(new OnUndoSwipeListListener() {
                @Override
                public void onUndoSwipe(Card card) {
                    Toast.makeText(getActivity(), "Saved food to db= " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    mFoodDao.insert(item);

                }
            });
            card.setOnSwipeListener(new OnSwipeListener() {
                @Override
                public void onSwipe(Card card) {
                    Toast.makeText(getActivity(), "Delete food= " + item.getTitle(), Toast.LENGTH_SHORT).show();
                    mFoodDao.delete(item);
                }
            });
            cards.add(card);
        }
        populateCardList(cards);
    }
}
