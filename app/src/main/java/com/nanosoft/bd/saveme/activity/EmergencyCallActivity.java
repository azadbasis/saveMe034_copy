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
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.service.CallingService;


public class EmergencyCallActivity extends DashboardActivity {


    EditText phoneNumberEt;
    Spinner sensibilitySp;
    Switch emergencyCallSw;

    LinearLayout callServiceLo;
    // Button eBtnSave;

    int switchStatus;
    String ePhoneNumber;
    String sensibility;
    int indexOfSp;
    int shakeValue;
    TextView tv1;
    View view;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    /*verify no*/
    int random;
    com.nanosoft.bd.saveme.broadcastreceiver.SmsReceiver receiver;
    private BroadcastReceiver mIntentReceiver;
    String senderNumber, message;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_call);
        setTitleFromActivityLabel(R.id.title_text);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Emergency Call");
        }
        initialize();
        sharedPreferencesData();
        switchStatus();
        switchClick();
        sensibilityTypeSpinner();

        //tv1.setBackgroundColor(Color.CYAN);

    }
    @Override
    public void onBackPressed() {
        animation(R.id.emergencyCallLayout);

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

    public void sharedPreferencesData() {

        sharedPreferences = getSharedPreferences("SaveData", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        switchStatus = sharedPreferences.getInt("switchStatuss", 0);
        ePhoneNumber = sharedPreferences.getString("ePhoneNumber", "");
        sensibility = sharedPreferences.getString("sensibilitys", "Medium");


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

        editor.putInt("shakeValue", shakeValue);
        editor.commit();
    }

    private void saveData() {
        EmergencyCallActivity.this.stopService(new Intent(EmergencyCallActivity.this, CallingService.class));

        editor.putInt("switchStatuss", switchStatus);
        /*editor.putString("ePhoneNumbers", ePhoneNumber);*/
        Operations.SaveToSharedPreference(getApplicationContext(),"ePhoneNumbers",ePhoneNumber);
        editor.putString("sensibilitys", sensibility);
        editor.commit();
        phoneNumberEt.setText(ePhoneNumber);




        EmergencyCallActivity.this.startService(new Intent(EmergencyCallActivity.this, CallingService.class));
    }

    private void switchClick() {

        emergencyCallSw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    phoneNumberEt.setEnabled(true);
                    sensibilitySp.setEnabled(true);

                    switchStatus = 1;
                    phoneNumberEt.setText(ePhoneNumber);


                    sensibilitySp.setSelection(indexOfSp);
                    editor.putInt("switchStatuss", switchStatus);
                    // editor.putString("ePhoneNumbers", ePhoneNumber);
                    editor.putString("sensibilitys", sensibility);
                    editor.commit();
                    // saveData();
                    EmergencyCallActivity.this.startService(new Intent(EmergencyCallActivity.this, CallingService.class));
                    Toast toast = Toast.makeText(EmergencyCallActivity.this," Emergency Call Started ", Toast.LENGTH_SHORT);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    toast.getView().setBackgroundColor(Color.GREEN);
                    v.setTextColor(Color.BLACK);
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                  /*  ImageView view = new ImageView(getApplicationContext());
                    view.setImageResource(R.drawable.icon);*/
                   // toast.setView(view);
                    toast.show();
                    callServiceLo.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.serviceOn));

                } else {
                    phoneNumberEt.setEnabled(false);


                    sensibilitySp.setEnabled(false);
                    //eBtnSave.setEnabled(false);
                    switchStatus = 0;
                    editor.putInt("switchStatuss", switchStatus);
                    //  editor.putString("ePhoneNumbers", ePhoneNumber);
                    editor.putString("sensibilitys", sensibility);
                    editor.commit();
                    // saveData();
                    EmergencyCallActivity.this.stopService(new Intent(EmergencyCallActivity.this, CallingService.class));
                    Toast toast = Toast.makeText(EmergencyCallActivity.this," Emergency Call Stopped ", Toast.LENGTH_SHORT);
                    TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                    toast.getView().setBackgroundColor(Color.RED);
                    v.setTextColor(Color.WHITE);
                    view = toast.getView();
                    toast.setGravity(Gravity.BOTTOM, 0, 0);
                    toast.show();

                    callServiceLo.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.serviceOff));
                }

            }
        });

    }

    private void switchStatus() {

        if (switchStatus == 0) {
            emergencyCallSw.setChecked(false);
            phoneNumberEt.setEnabled(false);


            sensibilitySp.setEnabled(false);
            //eBtnSave.setEnabled(false);
            EmergencyCallActivity.this.stopService(new Intent(EmergencyCallActivity.this, CallingService.class));
            callServiceLo.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.serviceOff));

        } else if (switchStatus == 1) {
            emergencyCallSw.setChecked(true);
            phoneNumberEt.setEnabled(true);
            // phoneNumberEt1.setEnabled(true);01990433040

            sensibilitySp.setEnabled(true);

            //eBtnSave.setEnabled(true);
            phoneNumberEt.setText(ePhoneNumber);
            // phoneNumberEt1.setText(ePhoneNumber);

            sensibilitySp.setSelection(indexOfSp);

            EmergencyCallActivity.this.startService(new Intent(EmergencyCallActivity.this, CallingService.class));

            callServiceLo.setBackgroundColor(ContextCompat.getColor(this, R.color.serviceOn));
        }
    }

    private void initialize() {
        phoneNumberEt = (EditText) findViewById(R.id.ePhoneNumberEts);


        emergencyCallSw = (Switch) findViewById(R.id.emergencyCallSws);
        sensibilitySp = (Spinner) findViewById(R.id.sensibilitySps);
        callServiceLo = (LinearLayout)  findViewById(R.id.callServiceLo);
        //eBtnSave = (Button) findViewById(R.id.eBtnSave);

        phoneNumberEt.setInputType(0);


    }

    public void eBtnSave(View view) {

        callVerify();
    }


    public void callVerify() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(EmergencyCallActivity.this);
        alertDialog.setTitle("Emergency Number");
        alertDialog.setMessage("Set Emergency Number:");

        final EditText input = new EditText(EmergencyCallActivity.this);


        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_action_warning);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);


        alertDialog.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {


                        ePhoneNumber = input.getText().toString();
                        sensibility = sensibilitySp.getSelectedItem().toString();
                        random = (int) (Math.random() * 1000 + 100);
                        String emergencyRandom=String.valueOf(random);
                     /*   editor.putInt("Random", random);*/
                        Operations.SaveToSharedPreference(getApplicationContext(),"emergencyRandom",emergencyRandom);
                        editor.commit();
                        if (ePhoneNumber.length() > 0 && random > 0) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(ePhoneNumber, null, String.valueOf(random), null, null);

Operations.SaveToSharedPreference(getApplicationContext(),"ePhoneNumbers",ePhoneNumber);
                            Toast.makeText(EmergencyCallActivity.this, "Genarate random Number :  "
                                    + random, Toast.LENGTH_LONG).show();
                            showMessage("Please Wait !", "Reply the random Number To set Phone Number");
                            return;
                        } else {
                            Toast.makeText(EmergencyCallActivity.this, "Blank Phone Number!!!", Toast.LENGTH_SHORT).show();
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

 /*       IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
        //sharedPreferencesData();

        mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("get_msg");

//Process the sms format and extract body &amp; phoneNumber
                msg = msg.replace("\n", "");
                message = msg.substring(msg.lastIndexOf(":") + 1, msg.length());
                senderNumber = msg.substring(0, msg.lastIndexOf(":"));
                editor.putString("Message", message);
                editor.putString("SenderNumber", senderNumber);
//Add it to the list or do whatever you wish to 01742845898

                Toast.makeText(context, "From: " + senderNumber + " Message: " + message, Toast.LENGTH_LONG).show();

                if (PhoneNumberUtils.compare(getApplicationContext(), senderNumber, ePhoneNumber) && String.valueOf(random).equals(message)) {
                    Toast.makeText(getApplicationContext(), "Phone Number is verified", Toast.LENGTH_LONG).show();
                    saveData();
                    phoneNumberEt.setTextColor(Color.BLACK);
                    phoneNumberEt.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
                    Toast.makeText(EmergencyCallActivity.this, "Emergency Number Saved", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "Phone Number is  not verified", Toast.LENGTH_LONG).show();
                }

            }
        };
        EmergencyCallActivity.this.registerReceiver(mIntentReceiver, intentFilter);*/
    }


    @Override
    protected void onPause() {

        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);


    }



    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


}
