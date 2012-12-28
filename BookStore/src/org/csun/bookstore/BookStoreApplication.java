package org.csun.bookstore;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

public class BookStoreApplication extends Application {
	public final static String TAG = "BookStoreApplication";

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.d(TAG,"onConfigurationChanged()");
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "onCreate()");
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		Log.d(TAG, "onLowMemory()");
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		Log.d(TAG, "onTerminate()");
	}

}
