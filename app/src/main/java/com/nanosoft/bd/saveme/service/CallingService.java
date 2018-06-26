package com.nanosoft.bd.saveme.service;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

public class CallingService extends Service {
    SensorManager manager;
    float accelValue;
    float currentValue;
    float lastValue;

    String ePhoneNumber, message, senderNumber;
    int shakeValue, random;

    SharedPreferences sharedPreferences;


    @Override
    public void onCreate() {
        super.onCreate();
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelValue = 0.00f;
        currentValue = SensorManager.GRAVITY_EARTH;
        lastValue = SensorManager.GRAVITY_EARTH;


        sharedPreferences = getSharedPreferences("SaveData", MODE_PRIVATE);
        ePhoneNumber = sharedPreferences.getString("ePhoneNumbers", "");
        shakeValue = sharedPreferences.getInt("shakeValue", 14);
        /*check validation*/
        random = sharedPreferences.getInt("Random", 0);
        senderNumber = sharedPreferences.getString("SenderNumber", null);
        message = sharedPreferences.getString("Message", null);

    }

    @Override
    public void onDestroy() {
        manager.unregisterListener(listener);
        super.onDestroy();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        manager.registerListener(listener, manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        return START_STICKY;
    }

    private SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            lastValue = currentValue;

            currentValue = (float) Math.sqrt((double) (x * x + y * y + z * z));

            float delta = currentValue - lastValue;
            accelValue = accelValue * .9f + delta;


            if (accelValue > shakeValue) {

                if (ePhoneNumber.length() > 0 || PhoneNumberUtils.compare(getApplicationContext(), senderNumber, ePhoneNumber) && String.valueOf(random).equals(message)) {

                    Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + ePhoneNumber));
                    callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    if (ActivityCompat.checkSelfPermission(CallingService.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                        return;
                    }
                    startActivity(callIntent);

                } else {
                    Toast.makeText(CallingService.this, "No Phone Number!!!", Toast.LENGTH_SHORT).show();
                }
            }
        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
