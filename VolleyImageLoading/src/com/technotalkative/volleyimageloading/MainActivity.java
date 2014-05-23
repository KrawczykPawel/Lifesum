package com.technotalkative.volleyimageloading;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

/**
 * Image Loading example using Volley library.
 * 1. NetworkImageView with NetworkImageView.setImageUrl()
 * 2. Simple ImageView with IamgeLoader.get()
 * 
 * @author Paresh Mayani
 */

public class MainActivity extends Activity {

	private RequestQueue mRequestQueue;
	private ImageLoader imageLoader;

	private ImageView imageView;
	private NetworkImageView networkImageView;

	private static final String IMAGE_URL = "http://developer.android.com/images/gp-device.png";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		networkImageView = (NetworkImageView) findViewById(R.id.networkImageView);
		imageView = (ImageView) findViewById(R.id.imageView1);
		mRequestQueue = Volley.newRequestQueue(this);
		imageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(
				BitmapLruCache.getDefaultLruCacheSize()));
	}

	public void btnLoadImageClick(View v) {

		imageLoader.get(IMAGE_URL, ImageLoader.getImageListener(imageView,
				R.drawable.ic_launcher, R.drawable.ic_error));
	}

	public void btnLoadNetWorkImageViewClick(View v) {
		networkImageView.setImageUrl(IMAGE_URL, imageLoader);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
