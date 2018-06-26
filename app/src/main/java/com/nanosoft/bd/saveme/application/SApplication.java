package com.nanosoft.bd.saveme.application;


import android.app.Application;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.nanosoft.bd.saveme.activity.PositionTrackerActivity;

public class SApplication extends Application {
    private final String TAG = SApplication.class.getSimpleName();
    public static Location LOCATION = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OnCreate");
        Intent location = new Intent(getApplicationContext(), PositionTrackerActivity.class);
        startService(location);
    }



}
