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

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;
import android.widget.Toast;

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
import com.krawczyk.lifesum.Lifesum;
import com.krawczyk.lifesum.R;
import com.krawczyk.lifesum.network.JsonHelper;
import com.krawczyk.lifesum.network.TokenRequest;
import com.krawczyk.lifesum.views.fragments.BaseFragment;
import com.krawczyk.lifesum.views.fragments.SearchFragment;
import com.krawczyk.lifesum.views.fragments.StoredFragment;

/**
 * @class MainActivity
 * @author Pawel Krawczyk
 * @since 22-05-2014 General activity of application.
 */
public class MainActivity extends Activity {

    /**
     *
     */
    private static final String DOUBLE_QUOTE = "\"";

    private static final String LIFESUM_TAG = "Lifesum";

    private static final String ENDPOINT_URL = "https://api.lifesum.com/v1/search/query?type=food&search=";

    private List<Food> mSearchResultList;

    private ListView mDrawerList;

    private DrawerLayout mDrawer;

    private CustomActionBarDrawerToggle mDrawerToggle;

    private int mCurrentTitle = R.string.app_name;

    private int mSelectedFragment;

    private BaseFragment mBaseFragment;

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
        initNavigationDrawer();
        loadFragment(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshLoading(false);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(BUNDLE_SELECTEDFRAGMENT, mSelectedFragment);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        // Create the search view
        final SearchView searchView = new SearchView(getActionBar().getThemedContext());
        searchView.setQueryHint(getString(R.string.search_hint));
        menu.add(Menu.NONE, Menu.NONE, 1, getString(R.string.search_hint)).setIcon(R.drawable.ic_action_search).setActionView(searchView)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        searchView.setOnQueryTextListener(new OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 0) {
                    // Search
                    search(newText);
                }
                return false;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 0) {
                    // Search and hide keyboard
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
                    Toast.makeText(getBaseContext(), getString(R.string.search_progress), Toast.LENGTH_SHORT).show();
                    search(query);
                } else {
                    // Do something when there's no input
                    Toast.makeText(getBaseContext(), getString(R.string.search_error), Toast.LENGTH_SHORT).show();
                }
                return false;
            }

        });
        return true;
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

    /*
     * Load fragment from saved instance state or SearchFragment by default
     */
    private void loadFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mSelectedFragment = savedInstanceState.getInt(BUNDLE_SELECTEDFRAGMENT);
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager.findFragmentById(R.id.fragment_main) == null) {
                mBaseFragment = selectFragment(mSelectedFragment);
                mBaseFragment.setFoodDao(mFoodDao);
            }
        } else {
            mBaseFragment = new SearchFragment();
            mBaseFragment.setFoodDao(mFoodDao);
            openFragment(mBaseFragment);
        }
    }

    /**
     * Initialize ORM system -> open database and create Dao objects. See link
     * for more.
     * 
     * @link http://greendao-orm.com/
     */
    private void initializeDao() {
        DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "food-db", null);
        mSQLiteDatabase = helper.getWritableDatabase();
        mDaoMaster = new DaoMaster(mSQLiteDatabase);
        mDaoSession = mDaoMaster.newSession();
        mFoodDao = mDaoSession.getFoodDao();
    }

    /*
     * Fragment selector for navigation drawer
     */
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

    /*
     * Opening selected fragment
     */
    private void openFragment(BaseFragment baseFragment) {
        if (baseFragment != null) {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_main, baseFragment);
            // fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
            if (baseFragment.getTitleResourceId() > 0) {
                mCurrentTitle = baseFragment.getTitleResourceId();
            }
        }
    }

    /*
     * Static array of navigation drawer's titles.
     */
    private static final String[] options = {
            Lifesum.getApplication().getString(R.string.fragment_search), Lifesum.getApplication().getString(R.string.fragment_stored)
    };

    /*
     * Initialize navigation drawer with shadow and custom action bar drawer
     * toggle. Custom drawer listener set.
     */
    private void initNavigationDrawer() {
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        initNavigationDrawerList();
        mDrawerToggle = new CustomActionBarDrawerToggle(this, mDrawer);
        mDrawer.setDrawerListener(mDrawerToggle);
    }

    /*
     * Initialize navigation drawer list with simple item adapter and values.
     * Custom on click listener set.
     */
    private void initNavigationDrawerList() {
        mDrawerList = (ListView) findViewById(R.id.drawer);
        if (mDrawerList != null) {
            mDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options));
            mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        }
    }

    /*
     * Search indeterminate progress bar switch.
     */
    private void refreshLoading(boolean isLoading) {
        if (isLoading == true) {
            findViewById(R.id.progress).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.progress).setVisibility(View.GONE);
        }
    }

    /**
     * Search in Lifesum api with given query.
     */
    private void search(String query) {
        Log.i(LIFESUM_TAG, "Search started for query: " + query);
        refreshLoading(true);
        downloadData(query);
    }

    /*
     * Downloading data using Volley request queue from Lifesum endpoint with
     * given query. Handling both correct response and error response.
     */
    private void downloadData(String query) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = ENDPOINT_URL + DOUBLE_QUOTE + query + DOUBLE_QUOTE;
        TokenRequest jsObjRequest = new TokenRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.i(LIFESUM_TAG, "Volley response: " + response.toString());
                try {
                    // Retrieving response tag value from json
                    JSONObject quizObject = (JSONObject) response.get("response");
                    String list = quizObject.optString("list");
                    // Deserializng json into food objects list using Gson
                    // library
                    mSearchResultList = JsonHelper.deserializeFoodList(list);
                    mBaseFragment.initCards(mSearchResultList);
                    // Standard adapter, before card were introduced
                    // FoodItemAdapter adapter = new
                    // FoodItemAdapter(SearchActivity.this, mSearchResultList);
                    // setListAdapter(adapter);
                } catch (JSONException e) {
                    Log.e(LIFESUM_TAG, "JSONException", e);
                }
                refreshLoading(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(LIFESUM_TAG, "Volley error: " + error.getMessage());
                refreshLoading(false);
            }
        });
        // Adding request to the queue
        queue.add(jsObjRequest);
    }

    /*
     * Action bar toggle drawer with changing fragment title
     */
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

    /*
     * Drawer item click listener
     */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // Highlight the selected item, update the title, and close the
            // drawer
            // update selected item and title, then close the drawer
            mDrawerList.setItemChecked(position, true);
            mBaseFragment = selectFragment(position);
            mSelectedFragment = position;
            if (mBaseFragment != null) {
                openFragment(mBaseFragment);
            }
            mDrawer.closeDrawer(mDrawerList);
        }
    }

}
