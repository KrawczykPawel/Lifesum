/*
 * Copyright (C) 2014 Paweł Krawczyk
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

package com.krawczyk.lifesum.activities;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.krawczyk.lifesum.DaoMaster;
import com.krawczyk.lifesum.DaoMaster.DevOpenHelper;
import com.krawczyk.lifesum.DaoSession;
import com.krawczyk.lifesum.Food;
import com.krawczyk.lifesum.FoodDao;
import com.krawczyk.lifesum.R;
import com.krawczyk.lifesum.network.JsonHelper;
import com.krawczyk.lifesum.network.TokenRequest;
import com.krawczyk.lifesum.views.adapters.FoodItemAdapter;

/**
 * @class SearchActivity
 * @author Pawel Krawczyk
 * @since 22-05-2014
 */
public class SearchActivity extends ListActivity {

    private SQLiteDatabase mSQLiteDatabase;

    private DaoMaster mDaoMaster;

    private DaoSession mDaoSession;

    private FoodDao mFoodDao;

    private Cursor mCursor;

    private List<Food> mSearchResultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initializeDao();
        addUiListeners();
        downloadData();
    }

    private void downloadData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.lifesum.com/v1/search/query?type=food&search=cola";

        TokenRequest jsObjRequest = new TokenRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i("Lifesum", response.toString());
                findViewById(R.id.progressBar1).setVisibility(View.GONE);
                try {
                    JSONObject quizObject = (JSONObject) response.get("response");
                    String list = quizObject.optString("list");
                    mSearchResultList = JsonHelper.deserializeFoodList(list);
                    FoodItemAdapter adapter = new FoodItemAdapter(SearchActivity.this, mSearchResultList);
                    setListAdapter(adapter);
                } catch (JSONException e) {
                    Log.e("e", "", e);
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Lifesum", error.getMessage());
                findViewById(R.id.progressBar1).setVisibility(View.GONE);
            }
        });
        queue.add(jsObjRequest);
    }

    private void initializeDao() {
        DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "food-db", null);
        mSQLiteDatabase = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mSQLiteDatabase);
        mDaoSession = mDaoMaster.newSession();
        mFoodDao = mDaoSession.getFoodDao();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void addUiListeners() {
    }

}
