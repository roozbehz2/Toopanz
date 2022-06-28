package com.roozbeh.toopan.communication.volleyPackage;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.collection.LruCache;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;


/**
 * Created by shuaib1413 on 13/04/2016.
 */
public enum VolleyController {
    INSTANCE;

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    VolleyController() {
        VolleyLog.DEBUG = false;
    }

    public synchronized RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return mRequestQueue;
    }

    public synchronized ImageLoader getImageLoader(final Context context) {
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(getRequestQueue(context),
                    new ImageLoader.ImageCache() {
                        private final LruCache<String, Bitmap>
                                cache = new LruBitmapCache(context);

                        @Override
                        public Bitmap getBitmap(String url) {
                            return cache.get(url);
                        }

                        @Override
                        public void putBitmap(String url, Bitmap bitmap) {
                            cache.put(url, bitmap);
                        }
                    });
        }
        return mImageLoader;
    }

    <T> void addToRequestQueue(Context context, Request<T> req) {
//        req.setShouldRetryServerErrors(true);
//        req.setTag(magnetAd);
        req.setRetryPolicy(new DefaultRetryPolicy(2500, 2, 1));
        getRequestQueue(context).add(req);
    }

    public <T> void addToRequestQueue(Context context, Request<T> req, String tag) {
        req.setTag(tag);
        req.setRetryPolicy(new DefaultRetryPolicy(2500, 2, 1));
        getRequestQueue(context).add(req);
    }

}

