package com.nanosoft.bd.saveme.service;


import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nanosoft.bd.saveme.activity.Operations;
import com.nanosoft.bd.saveme.application.SApplication;
import com.nanosoft.bd.saveme.broadcastreceiver.PushNotificationReceiver;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class LocationService extends Service {
    private static final String TAG = LocationService.class.getSimpleName();
    private LocationManager mLocationManager = null;
    private static final int INTERVAL = 1000; // minimum time interval between location updates (milliseconds)
    private static final float DISTANCE = 10f; // minimum distance between location updates (meters)

    private class LocationListener implements android.location.LocationListener {
        Location mLastLocation;

        public LocationListener(String provider) {
            mLastLocation = new Location(provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            mLastLocation.set(location);
            SApplication.LOCATION = location;
            Operations.NotifyInSavedArea(getApplicationContext(), location);
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

    }

    LocationListener[] mLocationListeners = new LocationListener[]{
            new LocationListener(LocationManager.GPS_PROVIDER),
            new LocationListener(LocationManager.NETWORK_PROVIDER)
    };

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        setIntervalResponseForPushNotification();
        return START_STICKY;

    }



    /*
        Request location updates from both providers: GPS and network.
     */
    @Override
    public void onCreate() {

        if (mLocationManager == null) {
            mLocationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, INTERVAL, DISTANCE, mLocationListeners[1]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "network provider does not exist, " + ex.getMessage());
        }
        try {
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, INTERVAL, DISTANCE, mLocationListeners[0]);
        } catch (java.lang.SecurityException ex) {
            Log.i(TAG, "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.d(TAG, "gps provider does not exist " + ex.getMessage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationManager != null) {
            for (int i = 0; i < mLocationListeners.length; i++) {
                try {
                    mLocationManager.removeUpdates(mLocationListeners[i]);
                } catch (Exception ex) {
                    Log.i(TAG, "fail to remove location listners, ignore", ex);
                }
            }
        }
    }



    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;


    private void setIntervalResponseForPushNotification() {


  //      int bitForaAlarmState = Operations.getIntegerSharedPreference(getApplicationContext(),"bitForaAlarmState",1);


      //  if (bitForaAlarmState==1) {

            alarmMgr = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getApplicationContext(), PushNotificationReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);

// Set the alarm to start at 8:30 a.m.
            Calendar calendar = Calendar.getInstance();
//        calendar.setTimeInMillis(System.currentTimeMillis());
//        calendar.set(Calendar.HOUR_OF_DAY, 8);
//        calendar.set(Calendar.MINUTE, 30);

// setRepeating() lets you specify a precise custom interval--in this case,
// 20 minutes.
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 86400000, alarmIntent);

            //Operations.IntSaveToSharedPreference(getApplicationContext(),"bitForaAlarmState",0);
       // }

    }


}