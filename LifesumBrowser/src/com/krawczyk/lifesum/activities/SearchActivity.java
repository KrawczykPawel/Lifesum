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

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.krawczyk.lifesum.network.TokenRequest;
import com.technotalkative.volleyexamplesimple.R;

/**
 * @class SearchActivity
 * @author Pawel Krawczyk
 * @since 22-05-2014
 */
public class SearchActivity extends Activity {

    private TextView txtDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        txtDisplay = (TextView) findViewById(R.id.txtDisplay);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "https://api.lifesum.com/v1/search/query?type=food&search=cola";

        TokenRequest jsObjRequest = new TokenRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                txtDisplay.setText("Response => " + response.toString());
                findViewById(R.id.progressBar1).setVisibility(View.GONE);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TEST", error.getMessage());
                txtDisplay.setText("Error => " + error.getLocalizedMessage());
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
}
