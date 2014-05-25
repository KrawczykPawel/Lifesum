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

import it.gmariotti.cardslib.library.internal.CardExpand;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.krawczyk.lifesum.Food;
import com.krawczyk.lifesum.R;

/**
 * This class provides an example of custom expand/collapse area. It uses
 * carddemo_example_inner_expand layout.
 * <p/>
 * You have to override the
 * {@link #setupInnerViewElements(android.view.ViewGroup, android.view.View)});
 * 
 * @author Gabriele Mariotti (gabri.mariotti@gmail.com)
 */
public class CustomExpandCard extends CardExpand {

    private Food item;

    public CustomExpandCard(Context context) {
        super(context, R.layout.expand_item);
    }

    /**
     * @return the item
     */
    public Food getItem() {
        return item;
    }

    /**
     * @param item the item to set
     */
    public void setItem(Food item) {
        this.item = item;
    }

    @Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        if (view == null)
            return;
        // Retrieve TextView elements
        TextView tx2 = (TextView) view.findViewById(R.id.textView2);
        TextView tx4 = (TextView) view.findViewById(R.id.textView4);
        TextView tx6 = (TextView) view.findViewById(R.id.textView6);
        tx2.setText(item.getProtein().toString());
        tx4.setText(item.getCarbohydrates().toString());
        tx6.setText(item.getFat().toString());
    }
}
