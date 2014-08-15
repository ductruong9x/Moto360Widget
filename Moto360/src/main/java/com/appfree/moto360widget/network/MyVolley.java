package com.appfree.moto360widget.network;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.appfree.moto360widget.util.Util;


public class MyVolley {

	private static int MAX_IMAGE_CACHE_ENTIRES;

	private static RequestQueue mRequestQueue;
	private static ImageLoader mImageLoader;

	private MyVolley() {
		// no instances
	}

	public static void init(Context context) {
		// mRequestQueue = Volley.newRequestQueue(context, new
		// ExtHttpClientStack(new SslHttpClient(Util.getKeyStore(context,
		// R.raw.oc_keystore), "123465@appota", 443)));
		MAX_IMAGE_CACHE_ENTIRES = Util.getCacheSize(context);
		mRequestQueue = Volley.newRequestQueue(context, null);
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapLruCache(
				MAX_IMAGE_CACHE_ENTIRES));
	}

	public static RequestQueue getRequestQueue() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("RequestQueue not initialized");
		}
	}

	/**
	 * Returns instance of ImageLoader initialized with {@see FakeImageCache}
	 * which effectively means that no memory caching is used. This is useful
	 * for images that you know that will be show only once.
	 * 
	 * @return
	 */
	public static ImageLoader getImageLoader() {
		if (mImageLoader != null) {
			return mImageLoader;
		} else {
			throw new IllegalStateException("ImageLoader not initialized");
		}
	}
}
