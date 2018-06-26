package com.nanosoft.bd.saveme.service;

/**
 * Created by Azharul Islam on 4/24/2016.
 */

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.nanosoft.bd.saveme.activity.Operations;


public class EmergencyTextService extends Service {

    SensorManager manager;
    float accelValues;
    float currentValue;
    float lastValue;

    String ePhoneNumber, ePhoneNumber1, ePhoneNumber2,message, message1, message2, messageaddrese,
    vmessage, vsenderNumber;
    int shakeValues, random;

    SharedPreferences sharedPreferences;


    @Override
    public void onCreate() {
        super.onCreate();
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelValues = 0.00f;
        currentValue = SensorManager.GRAVITY_EARTH;
        lastValue = SensorManager.GRAVITY_EARTH;


        sharedPreferences = getSharedPreferences("SaveData", MODE_PRIVATE);
        ePhoneNumber = sharedPreferences.getString("firstEphoneNumber", "");
        ePhoneNumber1 = sharedPreferences.getString("secondEphoneNumber", "");
        ePhoneNumber2 = sharedPreferences.getString("thirdEphoneNumber", "");
        message = sharedPreferences.getString("message", "");
        message1 = sharedPreferences.getString("message1", "");
        message2 = sharedPreferences.getString("message2", "");
        messageaddrese = sharedPreferences.getString("Address", "");
        String co = Operations.getStringFromSharedPreference(this, "Coordinates");
        messageaddrese += "\n\"http://maps.google.com/maps?q=" + co;

        shakeValues = sharedPreferences.getInt("shakeValues", 14);

        /*check validation*/
        random = sharedPreferences.getInt("Random", 0);
        vsenderNumber = sharedPreferences.getString("VSenderNumber", null);
        vmessage = sharedPreferences.getString("VMessage", null);

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
            accelValues = accelValues * .9f + delta;


            if (accelValues > shakeValues) {


                if (ePhoneNumber1.length() > 0 && message1.length() > 0 || PhoneNumberUtils.compare(getApplicationContext(), vsenderNumber, ePhoneNumber1) && String.valueOf(random).equals(vmessage)) {
                    messaging1();
                    Toast.makeText(EmergencyTextService.this, "Message Send to " + ePhoneNumber1, Toast.LENGTH_SHORT).show();
                }
                if (ePhoneNumber2.length() > 0 && message2.length() > 0 || PhoneNumberUtils.compare(getApplicationContext(), vsenderNumber, ePhoneNumber2) && String.valueOf(random).equals(vmessage)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            messaging2();
                            Toast.makeText(EmergencyTextService.this, "Message Send to " + ePhoneNumber2, Toast.LENGTH_SHORT).show();
                        }
                    }, 500);
                }
                if (ePhoneNumber.length() > 0 && message.length() > 0  || PhoneNumberUtils.compare(getApplicationContext(), vsenderNumber, ePhoneNumber) && String.valueOf(random).equals(vmessage)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                        messaging();
                            Toast.makeText(EmergencyTextService.this, "Message Send to " + ePhoneNumber, Toast.LENGTH_SHORT).show();
                        }
                    }, 1000);
                } else {
                    Toast.makeText(EmergencyTextService.this, "Blank Phone Number or Text Message!!!", Toast.LENGTH_SHORT).show();
                }

            }


        }


        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public void messaging() {


        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(ePhoneNumber, null, message + " " + messageaddrese, null, null);
    }
 public void messaging1() {


        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(ePhoneNumber1, null, message1 + " " + messageaddrese, null, null);
    }

    //01990433040
    public void messaging2() {

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(ePhoneNumber2, null, message2 + "Current Location  " + messageaddrese, null, null);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
