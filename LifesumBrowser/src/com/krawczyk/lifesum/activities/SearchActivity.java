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

import it.gmariotti.cardslib.demo.Utils;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

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
import com.krawczyk.lifesum.views.fragments.BaseFragment;
import com.krawczyk.lifesum.views.fragments.SearchFragment;
import com.krawczyk.lifesum.views.fragments.StoredFragment;

/**
 * @class SearchActivity
 * @author Pawel Krawczyk
 * @since 22-05-2014
 */
public class SearchActivity extends Activity {

    private List<Food> mSearchResultList;

    private ListView mDrawerList;

    private DrawerLayout mDrawer;

    private CustomActionBarDrawerToggle mDrawerToggle;

    private int mCurrentTitle = R.string.app_name;

    private int mSelectedFragment;

    private BaseFragment mBaseFragment;

    protected ActionMode mActionMode;

    private static String TAG = "Lifesum";

    private SQLiteDatabase mSQLiteDatabase;

    private DaoMaster mDaoMaster;

    private DaoSession mDaoSession;

    private FoodDao mFoodDao;

    // Used in savedInstanceState
    private static String BUNDLE_SELECTEDFRAGMENT = "BDL_SELFRG";

    private static final int SEARCH = 0;

    private static final int STORED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        initializeDao();
        addUiListeners();
        downloadData();
        initNavigationDrawer();
        if (savedInstanceState != null) {
            mSelectedFragment = savedInstanceState.getInt(BUNDLE_SELECTEDFRAGMENT);

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            if (fragmentManager.findFragmentById(R.id.fragment_main) == null) {
                mBaseFragment = selectFragment(mSelectedFragment);
                mBaseFragment.setFoodDao(mFoodDao);
            }
            // if (mBaseFragment==null)
            // mBaseFragment = selectFragment(mSelectedFragment);
        } else {
            mBaseFragment = new SearchFragment();
            mBaseFragment.setFoodDao(mFoodDao);
            openFragment(mBaseFragment);
        }
    }

    private void initNavigationDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        _initMenu();
        mDrawerToggle = new CustomActionBarDrawerToggle(this, mDrawer);
        mDrawer.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content
        // view
        // boolean drawerOpen = mDrawer.isDrawerOpen(mDrawerList);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
         * The action bar home/up should open or close the drawer.
         * ActionBarDrawerToggle will take care of this.
         */
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class CustomActionBarDrawerToggle extends ActionBarDrawerToggle {

        public CustomActionBarDrawerToggle(Activity mActivity, DrawerLayout mDrawerLayout) {
            super(mActivity, mDrawerLayout, R.drawable.ic_navigation_drawer, R.string.app_name, mCurrentTitle);
        }

        @Override
        public void onDrawerClosed(View view) {
            getActionBar().setTitle(getString(mCurrentTitle));
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }

        @Override
        public void onDrawerOpened(View drawerView) {
            getActionBar().setTitle(getString(R.string.app_name));
            invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            // Highlight the selected item, update the title, and close the
            // drawer
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mBaseFragment = selectFragment(position);
            mSelectedFragment = position;

            if (mBaseFragment != null)
                openFragment(mBaseFragment);
            mDrawer.closeDrawer(mDrawerList);
        }
    }

    private void initializeDao() {
        DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "food-db", null);
        mSQLiteDatabase = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mSQLiteDatabase);
        mDaoSession = mDaoMaster.newSession();
        mFoodDao = mDaoSession.getFoodDao();
    }

    private BaseFragment selectFragment(int position) {
        BaseFragment baseFragment = null;

        switch (position) {

            case SEARCH:
                baseFragment = new SearchFragment();
                break;
            case STORED:
                baseFragment = new StoredFragment();
                break;
            default:
                break;
        }
        baseFragment.setFoodDao(mFoodDao);
        return baseFragment;
    }

    private void openFragment(BaseFragment baseFragment) {
        if (baseFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.fragment_main, baseFragment);
            // fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            if (baseFragment.getTitleResourceId() > 0)
                mCurrentTitle = baseFragment.getTitleResourceId();

        }
    }

    public static final String[] options = {
            "Search", "Stored"
    };

    private void _initMenu() {
        mDrawerList = (ListView) findViewById(R.id.drawer);

        if (mDrawerList != null) {
            mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));

            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_SELECTEDFRAGMENT, mSelectedFragment);
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
                    mBaseFragment.initCards(mSearchResultList);
                    // FoodItemAdapter adapter = new
                    // FoodItemAdapter(SearchActivity.this, mSearchResultList);
                    // setListAdapter(adapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    protected void addUiListeners() {
    }

}
