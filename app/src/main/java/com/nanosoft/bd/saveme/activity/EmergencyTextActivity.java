/*
 * Copyright (C) 2011 Wglxy.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nanosoft.bd.saveme.activity;

import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.application.SApplication;
import com.nanosoft.bd.saveme.service.EmergencyTextService;
import com.nanosoft.bd.saveme.service.LocationService;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * This is the activity for feature 3 in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 */

public class EmergencyTextActivity extends DashboardActivity {


    LinearLayout textServiceLo;

    EditText phoneNumberEt, phoneNumberEt1, phoneNumberEt2, etmessage, etmessage1, etmessage2;
    Spinner sensibilitySp;
    Switch emergencyCallSw;
    Button eBtnSave;
    int switchStatus;
    String vePhoneNumber, vePhoneNumber1, vePhoneNumber2, message, message1, message2, messageLocation;
    String sensibility;
    int indexOfSp;
    int shakeValue;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView coordinatess;
    TextView address;
    TextView tvMsg1;
    private final String TAG = PositionTrackerActivity.class.getSimpleName();

    int bitForMessageSave = 0;


    /*call verify*/
    int random;
    String vsenderNumber, vPhoneNumber2, vPhoneNumber3, vmessage;
    private BroadcastReceiver mIntentReceiver;
    /*location Sensor*/
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_text);
        setTitleFromActivityLabel(R.id.title_text);


        sharedPreferences = getSharedPreferences("SaveData", MODE_PRIVATE);
        initialize();
        sharedPreferencesData();
        switchStatus();
        switchClick();


        startService(new Intent(this, LocationService.class));
    /*location Track*/
        // mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //   mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
//    mSensorManager.registerListener((SensorEventListener) this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        etmessage1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String message = etmessage1.getText().toString();
                // Toast.makeText(EmergencyTextActivity.this, ""+message.length()+ " "+ count + " "+ s, Toast.LENGTH_SHORT).show();
                if (message.length() > 59) {
                    Toast.makeText(EmergencyTextActivity.this, "You reached Maximum character", Toast.LENGTH_SHORT).show();
                    etmessage1.setText(message.substring(1,60));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                bitForMessageSave = 1;

            }
        });


        etmessage2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String message = etmessage2.getText().toString();
                // Toast.makeText(EmergencyTextActivity.this, ""+message.length()+ " "+ count + " "+ s, Toast.LENGTH_SHORT).show();
                if (message.length() > 59) {
                    Toast.makeText(EmergencyTextActivity.this, "You reached Maximum character", Toast.LENGTH_SHORT).show();
                    etmessage2.setText(message.substring(1,60));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                bitForMessageSave = 1;
            }
        });


        etmessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {




            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String message = etmessage.getText().toString();
               // Toast.makeText(EmergencyTextActivity.this, ""+message.length()+ " "+ count + " "+ s, Toast.LENGTH_SHORT).show();
                if (message.length() > 59) {
                    Toast.makeText(EmergencyTextActivity.this, "You reached Maximum character", Toast.LENGTH_SHORT).show();
                    etmessage.setText(message.substring(1,60));
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                bitForMessageSave = 1;
            }
        });

        sensibilityTypeSpinner();
    }

    @Override
    public void onBackPressed() {
        animation(R.id.emergencyTextLayout);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                finish();

               // super.onBackPressed();
            }
        }, 700);

    }

    private void animation(int layoutId){

        View view = (View) findViewById(layoutId);

        Animation animation = new TranslateAnimation(0,1500f, 0, 0);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);
        animationSet.setDuration(800);
        animationSet.setFillAfter(true);
        view.startAnimation(animationSet);

    }


    public void sharedPreferencesData() {

        sharedPreferences = getSharedPreferences("SaveData", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        switchStatus = sharedPreferences.getInt("switchStatus", 0);
        vePhoneNumber = sharedPreferences.getString("firstEphoneNumber", "");
        vePhoneNumber1 = sharedPreferences.getString("secondEphoneNumber", "");
        vePhoneNumber2 = sharedPreferences.getString("thirdEphoneNumber", "");
        message = sharedPreferences.getString("message", "");
        message1 = sharedPreferences.getString("message1", "");
        message2 = sharedPreferences.getString("message2", "");
        sensibility = sharedPreferences.getString("sensibility", "Medium");
        messageLocation = sharedPreferences.getString("Address", "");

        if (sensibility.equals("Very High")) {
            indexOfSp = 0;
            shakeValue = 8;
        } else if (sensibility.equals("High")) {
            indexOfSp = 1;
            shakeValue = 11;
        } else if (sensibility.equals("Medium")) {
            indexOfSp = 2;
            shakeValue = 14;
        } else if (sensibility.equals("Low")) {
            indexOfSp = 3;
            shakeValue = 17;
        } else if (sensibility.equals("Very Low")) {
            indexOfSp = 4;
            shakeValue = 20;
        }
        editor.putInt("shakeValues", shakeValue);
        editor.commit();
    }

    private void saveData() {
        EmergencyTextActivity.this.stopService(new Intent(EmergencyTextActivity.this, EmergencyTextService.class));
/*location service*/
        //  mSensorManager.unregisterListener((SensorEventListener) this);
        EmergencyTextActivity.this.stopService(new Intent(EmergencyTextActivity.this, LocationService.class));

/*location service*/


        vePhoneNumber = phoneNumberEt.getText().toString();
        sensibility = sensibilitySp.getSelectedItem().toString();
        vePhoneNumber1 = phoneNumberEt1.getText().toString();
        vePhoneNumber2 = phoneNumberEt2.getText().toString();
        message = etmessage.getText().toString();
        message1 = etmessage1.getText().toString();
        message2 = etmessage2.getText().toString();

        editor.putInt("switchStatus", switchStatus);
        editor.putString("vePhoneNumber", vePhoneNumber);
        editor.putString("vePhoneNumber1", vePhoneNumber1);
        editor.putString("vePhoneNumber2", vePhoneNumber2);
        editor.putString("message", message);
        editor.putString("message1", message1);
        editor.putString("message2", message2);
        editor.putString("sensibility", sensibility);
        editor.commit();
        phoneNumberEt.setText(vePhoneNumber);
        phoneNumberEt.setVisibility(View.VISIBLE);
        phoneNumberEt1.setText(vePhoneNumber1);
        phoneNumberEt1.setVisibility(View.VISIBLE);
        phoneNumberEt2.setText(vePhoneNumber2);
        phoneNumberEt2.setVisibility(View.VISIBLE);
        EmergencyTextActivity.this.startService(new Intent(EmergencyTextActivity.this, EmergencyTextService.class));
        /*location service start*/
        EmergencyTextActivity.this.startService(new Intent(EmergencyTextActivity.this, LocationService.class));
//        mSensorManager.registerListener((SensorEventListener) this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        /*location service start*/
    }


    private void sensibilityTypeSpinner() {


        if (switchStatus == 1) {
            sensibilitySp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    sensibility = (String) parent.getItemAtPosition(position);
                    saveData();
                    sharedPreferencesData();
                    //Toast.makeText(SettingsActivity.this, sensibility, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }


    private void switchClick() {

        emergencyCallSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    phoneNumberEt.setEnabled(true);
                    sensibilitySp.setEnabled(true);
                    eBtnSave.setEnabled(true);
                    phoneNumberEt1.setEnabled(true);
                    phoneNumberEt2.setEnabled(true);
                    etmessage.setEnabled(true);
                    etmessage1.setEnabled(true);
                    etmessage2.setEnabled(true);
                    switchStatus = 1;
                    phoneNumberEt.setText(vePhoneNumber);
                    phoneNumberEt1.setText(vePhoneNumber1);
                    phoneNumberEt2.setText(vePhoneNumber2);
                    etmessage.setText(message);
                    etmessage1.setText(message1);

                    etmessage2.setText(message2);
                    tvMsg1.setText(messageLocation);

                    sensibilitySp.setSelection(indexOfSp);
                    // saveData();
                    /*veryfy no*/
                    editor.putInt("switchStatus", switchStatus);
                    editor.putString("message", message);
                    editor.putString("message1", message1);
                    editor.putString("message2", message2);
                    editor.putString("sensibility", sensibility);
                    editor.commit();

                    EmergencyTextActivity.this.startService(new Intent(EmergencyTextActivity.this, EmergencyTextService.class));
                    EmergencyTextActivity.this.startService(new Intent(EmergencyTextActivity.this, LocationService.class));
                    textServiceLo.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.serviceOn));
                    //  mSensorManager.registerListener((SensorEventListener) EmergencyTextActivity.this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                } else {
                    phoneNumberEt.setEnabled(false);
                    phoneNumberEt1.setEnabled(false);
                    phoneNumberEt2.setEnabled(false);
                    etmessage.setEnabled(false);
                    etmessage1.setEnabled(false);
                    etmessage2.setEnabled(false);
                    sensibilitySp.setEnabled(false);
                    eBtnSave.setEnabled(false);
                    switchStatus = 0;
                    // saveData();
                    /*veryfy no*/
                    editor.putInt("switchStatus", switchStatus);
                    editor.putString("message1", message1);
                    editor.putString("message2", message2);
                    editor.putString("sensibility", sensibility);
                    editor.commit();
                    EmergencyTextActivity.this.stopService(new Intent(EmergencyTextActivity.this, EmergencyTextService.class));
                    // mSensorManager.unregisterListener((SensorEventListener) EmergencyTextActivity.this);
                    EmergencyTextActivity.this.stopService(new Intent(EmergencyTextActivity.this, LocationService.class));
                    textServiceLo.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.serviceOff));
                }

            }
        });

    }

    private void switchStatus() {

        if (switchStatus == 0) {
            emergencyCallSw.setChecked(false);
            phoneNumberEt.setEnabled(false);
            phoneNumberEt1.setEnabled(false);
            phoneNumberEt2.setEnabled(false);
            etmessage.setEnabled(false);
            etmessage1.setEnabled(false);
            etmessage2.setEnabled(false);
            sensibilitySp.setEnabled(false);
            eBtnSave.setEnabled(false);
            EmergencyTextActivity.this.stopService(new Intent(EmergencyTextActivity.this, EmergencyTextService.class));
            textServiceLo.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.serviceOff));

        } else if (switchStatus == 1) {
            emergencyCallSw.setChecked(true);
            phoneNumberEt.setEnabled(true);
            phoneNumberEt1.setEnabled(true);
            phoneNumberEt2.setEnabled(true);
            phoneNumberEt.setText(vePhoneNumber);
            phoneNumberEt1.setText(vePhoneNumber1);
            phoneNumberEt2.setText(vePhoneNumber2);
            etmessage.setEnabled(true);
            etmessage1.setEnabled(true);
            etmessage2.setEnabled(true);
            sensibilitySp.setEnabled(true);
            eBtnSave.setEnabled(true);
            etmessage.setText(message);
            etmessage1.setText(message1);
            tvMsg1.setText(messageLocation);
            etmessage2.setText(message2);
            sensibilitySp.setSelection(indexOfSp);

            EmergencyTextActivity.this.startService(new Intent(EmergencyTextActivity.this, EmergencyTextService.class));

            textServiceLo.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.serviceOn));
            // EmergencyTextActivity.this.startService(new Intent(EmergencyTextActivity.this, LocationService.class));
            // mSensorManager.registerListener((SensorEventListener) EmergencyTextActivity.this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private void initialize() {
        phoneNumberEt = (EditText) findViewById(R.id.ePhoneNumberEt);
        phoneNumberEt1 = (EditText) findViewById(R.id.ePhoneNumberEt1);
        phoneNumberEt2 = (EditText) findViewById(R.id.ePhoneNumberEt2);
        etmessage = (EditText) findViewById(R.id.etmessage);
        etmessage1 = (EditText) findViewById(R.id.etmessage1);
        etmessage2 = (EditText) findViewById(R.id.etmessage2);
        emergencyCallSw = (Switch) findViewById(R.id.emergencyCallSw);
        sensibilitySp = (Spinner) findViewById(R.id.sensibilitySp);
        eBtnSave = (Button) findViewById(R.id.eBtnSave);
        address = (TextView) findViewById(R.id.tvLocation);
        coordinatess = (TextView) findViewById(R.id.location_pointss);

        tvMsg1 = (TextView) findViewById(R.id.tv1msg);
        textServiceLo = (LinearLayout) findViewById(R.id.textServiceLo);

    }

    public void eBtnSaves(View view) {


        vePhoneNumber = phoneNumberEt.getText().toString();
        sensibility = sensibilitySp.getSelectedItem().toString();
        vePhoneNumber1 = phoneNumberEt1.getText().toString();
        vePhoneNumber2 = phoneNumberEt2.getText().toString();
        message = etmessage.getText().toString();
        message1 = etmessage1.getText().toString();
        message2 = etmessage2.getText().toString();

        if (bitForMessageSave == 1) {
            saveData();
            bitForMessageSave = 0;
        }
        //saveData();

        phoneNumberEt.setText(vePhoneNumber);
        phoneNumberEt1.setText(vePhoneNumber1);
        phoneNumberEt2.setText(vePhoneNumber2);
/*location check*/
        if (SApplication.LOCATION != null) {
            double lat = SApplication.LOCATION.getLatitude();
            double lon = SApplication.LOCATION.getLongitude();
            coordinatess.setText(lat + "," + lon);
            Operations.SaveToSharedPreference(this, "Coordinates", coordinatess.getText().toString());
            Geocoder geocoder = new Geocoder(getApplicationContext(), new Locale("en"));
            try {
                // get address from location
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && addresses.size() != 0) {
                    StringBuilder builder = new StringBuilder();
                    Address returnAddress = addresses.get(0);
                    for (int i = 0; i < returnAddress.getMaxAddressLineIndex(); i++) {
                        builder.append(returnAddress.getAddressLine(i));
                        builder.append(" ");
                    }
                    address.setText(builder);
                    address.setVisibility(View.VISIBLE);
                    String add = address.getText().toString();


                    sharedPreferences = getSharedPreferences("SaveData", MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putString("Address", add);
                    editor.commit();

                    Toast.makeText(getApplicationContext(), add, Toast.LENGTH_LONG).show();
                } else {
                    Log.e(TAG, "Addresses null");
                }
            } catch (IOException e) {
                Log.e(TAG, "Geocoder exception " + e);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Check GPS status and internet connection", Toast.LENGTH_LONG).show();
            coordinatess.setText("No location yet");
            address.setVisibility(View.INVISIBLE);
        }
/*location check*/

        Toast.makeText(EmergencyTextActivity.this, "Emergency Number Saved", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(EmergencyTextActivity.this, EmergencyTextActivity.class);
        startActivity(intent);
        finish();
    }


    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    public void eBtnSave1(View view) {
        callVerifyOne();
        phoneNumberEt.setText(vePhoneNumber);

    }


    public void callVerifyOne() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmergencyTextActivity.this);
        alertDialog.setTitle("Emergency Number");
        alertDialog.setMessage("Set Emergency Number to verify:");
        final EditText input = new EditText(EmergencyTextActivity.this);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(Color.RED);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(3);
        input.setBackground(shape);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_action_warning);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);


        alertDialog.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        vePhoneNumber = input.getText().toString();
                        sensibility = sensibilitySp.getSelectedItem().toString();
                        random = (int) (Math.random() * 100000 + 100000);

                        /*editor.putInt("Random", random);*/
                        String firstRandom = String.valueOf(random);
                        Operations.SaveToSharedPreference(getApplicationContext(), "FirstRandom", firstRandom);
                        /*editor.commit();*/
                        if (vePhoneNumber.length() > 0 && random > 0) {

                            EmergencyTextActivity.this.stopService(new Intent(EmergencyTextActivity.this, EmergencyTextService.class));
                            EmergencyTextActivity.this.startService(new Intent(EmergencyTextActivity.this, EmergencyTextService.class));

                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(vePhoneNumber, null, String.valueOf(random), null, null);
                            Operations.SaveToSharedPreference(getApplicationContext(), "vePhoneNumber", vePhoneNumber);

                            Toast.makeText(EmergencyTextActivity.this, "Genarate random Number :  "
                                    + random, Toast.LENGTH_LONG).show();
                            showMessage("Please Wait !", "Please, Reply with the code sent to you.");
                            return;
                        } else {
                            Toast.makeText(EmergencyTextActivity.this, "Blank Phone Number!!!", Toast.LENGTH_SHORT).show();
                        }


                    }

                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }

    @Override
    protected void onResume() {
        super.onResume();
       /* IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
        //sharedPreferencesData();

                mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("get_msg");

//Process the sms format and extract body &amp; phoneNumber
                msg = msg.replace("\n", "");
                vmessage = msg.substring(msg.lastIndexOf(":") + 1, msg.length());
                vsenderNumber = msg.substring(0, msg.lastIndexOf(":"));
                editor.putString("VMessage", vmessage);
                editor.putString("VSenderNumber", vsenderNumber);
//Add it to the list or do whatever you wish to 01742845898

                Toast.makeText(context, "From: " + vsenderNumber + " Message: " + vmessage, Toast.LENGTH_LONG).show();
if(vsenderNumber.length()>0) {
    if (PhoneNumberUtils.compare(getApplicationContext(), vsenderNumber, vePhoneNumber) && String.valueOf(random).equals(vmessage)) {

        phoneNumberEt.setText(vePhoneNumber);
        Toast.makeText(getApplicationContext(), "Phone Number is verified", Toast.LENGTH_LONG).show();
        saveData();
    }
    if (PhoneNumberUtils.compare(getApplicationContext(), vsenderNumber, vePhoneNumber1) && String.valueOf(random).equals(vmessage)) {

        phoneNumberEt1.setText(vePhoneNumber1);

        Toast.makeText(getApplicationContext(), "Phone Number is verified", Toast.LENGTH_LONG).show();
        saveData();

    }
    if (PhoneNumberUtils.compare(getApplicationContext(), vsenderNumber, vePhoneNumber2) && String.valueOf(random).equals(vmessage)) {

        phoneNumberEt2.setText(vePhoneNumber2);
        Toast.makeText(getApplicationContext(), "Phone Number is verified", Toast.LENGTH_LONG).show();
        saveData();

    }
}else {
                    Toast.makeText(getApplicationContext(), "Phone Number is  not verified", Toast.LENGTH_LONG).show();
                }

            }
        };
        EmergencyTextActivity.this.registerReceiver(mIntentReceiver, intentFilter);*/
    }

    public void eBtnSave2(View view) {
        callVerifyTwo();
        phoneNumberEt1.setText(vePhoneNumber1);

    }

    private void callVerifyTwo() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmergencyTextActivity.this);
        alertDialog.setTitle("Emergency Number");
        alertDialog.setMessage("Set Emergency Number:");

        final EditText input = new EditText(EmergencyTextActivity.this);

        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(Color.RED);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(3);
        input.setBackground(shape);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_action_warning);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);


        alertDialog.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        vePhoneNumber1 = input.getText().toString();
                        sensibility = sensibilitySp.getSelectedItem().toString();
                        random = (int) (Math.random() * 1000000 + 100000);
                        String secondRandom = String.valueOf(random);
                      /*  editor.putInt("Random", random);
                        editor.commit();*/

                        Operations.SaveToSharedPreference(getApplicationContext(), "SecondRandom", secondRandom);
                        if (vePhoneNumber1.length() > 0 && random > 0) {


                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(vePhoneNumber1, null, String.valueOf(random), null, null);

                            Operations.SaveToSharedPreference(getApplicationContext(), "vePhoneNumber2", vePhoneNumber1);

                            Toast.makeText(EmergencyTextActivity.this, "Genarate random Number :  "
                                    + random, Toast.LENGTH_LONG).show();
                            showMessage("Please Wait !", "Please, Reply with the code sent to you");
                            return;
                        } else {
                            Toast.makeText(EmergencyTextActivity.this, "Blank Phone Number!!!", Toast.LENGTH_SHORT).show();
                        }


                    }

                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }

    public void eBtnSave3(View view) {
        callVerifyThree();
        phoneNumberEt2.setText(vePhoneNumber2);
    }

    private void callVerifyThree() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmergencyTextActivity.this);
        alertDialog.setTitle("Emergency Number");
        alertDialog.setMessage("Set Emergency Number:");

        final EditText input = new EditText(EmergencyTextActivity.this);
        ShapeDrawable shape = new ShapeDrawable(new RectShape());
        shape.getPaint().setColor(Color.RED);
        shape.getPaint().setStyle(Paint.Style.STROKE);
        shape.getPaint().setStrokeWidth(3);
        input.setBackground(shape);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_action_warning);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);


        alertDialog.setPositiveButton("Send",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        vePhoneNumber2 = input.getText().toString();
                        sensibility = sensibilitySp.getSelectedItem().toString();
                        random = (int) (Math.random() * 1000000 + 100000);
                        String thirdRandom = String.valueOf(random);
                        Operations.SaveToSharedPreference(getApplicationContext(), "ThirdRandom", thirdRandom);
                       /* editor.putInt("Random", random);
                        editor.commit();*/
                        if (vePhoneNumber2.length() > 0 && random > 0) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(vePhoneNumber2, null, String.valueOf(random), null, null);

                            Operations.SaveToSharedPreference(getApplicationContext(), "vePhoneNumber3", vePhoneNumber2);

                            Toast.makeText(EmergencyTextActivity.this, "Genarate random Number :  "
                                    + random, Toast.LENGTH_LONG).show();
                            showMessage("Please Wait !", "Please, Reply with the code sent to you");
                            return;
                        } else {
                            Toast.makeText(EmergencyTextActivity.this, "Blank Phone Number!!!", Toast.LENGTH_SHORT).show();
                        }


                    }

                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }


  /*  public void setLocation(View view) {
        Intent intent=new Intent(this,LocationNotifierActivity.class);
        startActivity(intent);
    }*/
}
