package com.nanosoft.bd.saveme.activity;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.text.Html;
import android.text.InputType;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.service.BackService;
import com.nanosoft.bd.saveme.service.LocationService;

import java.util.concurrent.TimeUnit;

import static com.nanosoft.bd.saveme.R.drawable;
import static com.nanosoft.bd.saveme.R.id;
import static com.nanosoft.bd.saveme.R.layout;

/**
 * This is a simple activity that demonstrates the dashboard user interface pattern.
 */

public class HomeActivity extends DashboardActivity {


    String timeInterval;
    private ImageView home_btn_feature1 = null;
    private ImageView home_btn_feature2 = null;
    private ImageView home_btn_feature3 = null;
    private ImageView home_btn_feature4 = null;
    private ImageView home_btn_feature5 = null;
    private ImageView home_btn_feature6 = null;
    private ImageView home_btn_feature7 = null;


    private BroadcastReceiver mIntentReceiver;
    String senderNumber, message, pn;

    String phoneNumber;


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(layout.activity_home);

        startService(new Intent(this, LocationService.class));

        home_btn_feature1 = (ImageView) findViewById(id.home_btn_feature1);
        home_btn_feature2 = (ImageView) findViewById(id.home_btn_feature2);
        home_btn_feature3 = (ImageView) findViewById(id.home_btn_feature3);
        home_btn_feature4 = (ImageView) findViewById(id.home_btn_feature4);
        home_btn_feature5 = (ImageView) findViewById(id.home_btn_feature5);
        home_btn_feature6 = (ImageView) findViewById(id.home_btn_feature6);
        home_btn_feature7 = (ImageView) findViewById(id.home_btn_feature7);


      /*  Typeface font = Typeface.createFromAsset(getAssets(), "customfont/poste.ttf");
        home_btn_feature1.setTypeface(font);
        home_btn_feature2.setTypeface(font);
        home_btn_feature3.setTypeface(font);
        home_btn_feature4.setTypeface(font);
        home_btn_feature5.setTypeface(font);
        home_btn_feature6.setTypeface(font);
        home_btn_feature7.setTypeface(font);*/


        sharedPreferences = getSharedPreferences("SaveData", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        int bitForPN = getIntent().getIntExtra("bitForPN",0);

        //   Toast.makeText(this, ""+bitForPN, Toast.LENGTH_SHORT).show();

        if (bitForPN==1){
            phoneNumber = getIntent().getStringExtra("phoneNumber");
            bitForPN=0;
        }


        if (Operations.ContainsKey(this, "timeInterval"))
            timeInterval = Operations.getStringFromSharedPreference(this, "timeInterval");
        else
            timeInterval = "30";

        CheckPhoneNumber();
        //  Toast.makeText(HomeActivity.this,Operations.getMyPhoneNumber(this) , Toast.LENGTH_SHORT).show();
        //schedule timer to check and update location0
        ActNotify();

        Operations.checkUserFirstTime = true;
        Operations.checkUser(this);

        //set footnote
        TextView tv = (TextView) findViewById(id.tvfootnote);
        tv.setBackgroundColor(Color.argb(250, 255, 255, 255));
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        tv.setText(Html.fromHtml("Design and concept: <a style='color:#123456;' href='http://nanoit.biz/'>Nanosoft</a>"));
        tv.setTextColor(Color.parseColor("#000000"));



         /*USE SPLASH SCREEEN*/
        View v = (View) findViewById(R.id.imageView);

        // Animation animation =new ScaleAnimation(1,5,1,5,.5f,.5f);
        Animation animation = new TranslateAnimation(0, -1500f, 0, 0);
        animation.setStartOffset(500);
        animation.setInterpolator(new AccelerateInterpolator(200));
        animation.setDuration(5000);
        animation.setFillAfter(true);

        Animation animation1 = new AlphaAnimation(1f, 0f);
        animation1.setDuration(2000);
        animation1.setFillAfter(true);


        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);

        animationSet.setDuration(1200);
        animationSet.setFillAfter(true);
        v.startAnimation(animationSet);
       /*USE SPLASH SCREEEN*/

        bitForNumberVerification = getIntent().getIntExtra("bitForNumberVerification", 0);

        NumberVerification();




        int bitForProgressDialog = getIntent().getIntExtra("bitForProgressDialog",0);



        if (bitForProgressDialog==1) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Verifying........");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();
            bitForProgressDialog = 0;
        }

    }

    private void animation(int layoutId){

        View view = (View) findViewById(layoutId);

        Animation animation = new TranslateAnimation(0,-1500f, 0, 0);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);
        animationSet.setDuration(1000);
        animationSet.setFillAfter(true);
        view.startAnimation(animationSet);

    }



    private void NumberVerification() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Toast.makeText(HomeActivity.this, ""+bitForNumberVerification+"  "+Operations.strRes, Toast.LENGTH_SHORT).show();  mProgressDialog = new ProgressDialog(HomeActivity.this);


                if (bitForNumberVerification == 1 && Operations.strRes.equals("1")) {

                    verifyUserPhone();

                    //  Toast.makeText(HomeActivity.this, "it is use for number verification", Toast.LENGTH_SHORT).show();
                    bitForNumberVerification = 0;

                }
            }
        }, 3000);


    }

    private void verifyUserPhone() {



        IntentFilter intentFilter = new IntentFilter("SmsMessage.intent.MAIN");
        //sharedPreferencesData();
        timeOut();

        mIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String msg = intent.getStringExtra("get_msg");

//Process the sms format and extract body &amp; phoneNumber
                msg = msg.replace("\n", "");
                message = msg.substring(msg.lastIndexOf(":") + 1, msg.length());
                senderNumber = msg.substring(0, msg.lastIndexOf(":"));

                Operations.SaveToSharedPreference(getApplicationContext(), "Message", message);
                Operations.SaveToSharedPreference(getApplicationContext(), "SenderNumber", senderNumber);
//Add it to the list or do whatever you wish to 01742845898


            /*    Toast.makeText(context, "From: " + senderNumber + " Message: " + message, Toast.LENGTH_LONG).show();*/
                String random = Operations.getStringFromSharedPreference(getApplicationContext(), "random");
                String userPhone = Operations.getStringFromSharedPreference(getApplicationContext(), "pn");


                if (PhoneNumberUtils.compare(getApplicationContext(), senderNumber, userPhone) && random.equals(message)) {

                    bitForTimeOut = 1;
                    mProgressDialog.dismiss();

                    Operations.SaveToSharedPreference(getApplicationContext(), "myphone", userPhone);
                    editor.putInt("bitForItemMenu",1);
                    editor.commit();

                    Intent newIntent = new Intent(HomeActivity.this, HomeActivity.class);
                   // newIntent.putExtra("bitForNumberVerification", 1);
                   // newIntent.putExtra("bitForProgressDialog",1);
                    startActivity(newIntent);
                    finish();

                    Toast.makeText(getApplicationContext(), "Phone Number is verified", Toast.LENGTH_LONG).show();


                } else {
                    Toast.makeText(getApplicationContext(), "Phone Number is  not verified", Toast.LENGTH_LONG).show();
                }

            }
        };
        HomeActivity.this.registerReceiver(mIntentReceiver, intentFilter);
    }


    int bitForTimeOut = 0;
    private ProgressDialog mProgressDialog;

    private void timeOut() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (bitForTimeOut == 0) {

                    mProgressDialog.dismiss();

                    new AlertDialog.Builder(HomeActivity.this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Operation Timeout")
                            .setMessage("-Timeout Reasons-\n1. Invalid phone number.\n2. Insufficient balance to send SMS.\n3. Go to 'SIM card' settings, set SIM1 or SIM2 in 'SMS messages' settings.")
                            .setCancelable(false)
                            .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Operations.SaveToSharedPreference(getApplicationContext(),"myphone","1");
                                    finish();
                                }
                            }).setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Operations.SaveToSharedPreference(getApplicationContext(),"myphone","1");
                            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }).show();

                }


            }
        }, 50000);


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
            }
            if (resultCode == RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult


    private void ActNotify() {

        Intent intents = new Intent(this, BackService.class);
        int timemili = Integer.parseInt(timeInterval) * 60000;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(1000);
        PendingIntent pendingIntent = PendingIntent.getService(this,
                0, intents,
                PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        mAlarmManager.cancel(pendingIntent);
        mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, timemili, pendingIntent);
    }

    boolean okPressed = false;


    int bitForNumberVerification;

    private void CheckPhoneNumber() {

        if (Operations.getMyPhoneNumber(this).equals("1")) {

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setTitle("Phone Number");
            alertDialog.setCancelable(false);
            alertDialog.setMessage("Please enter phone number:");


            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);

            final EditText input = new EditText(this);

            input.setLayoutParams(lp);
            alertDialog.setView(input);
            input.setText(phoneNumber);
            alertDialog.setIcon(drawable.ic_action_warning);
            input.setInputType(InputType.TYPE_CLASS_NUMBER);


            alertDialog.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            String pn = input.getText().toString();
                            Operations.SaveToSharedPreference(getApplicationContext(), "pn", pn);
                            if (pn.length() > 7) {

                                sendSmsVerify(pn);
                                Operations.SaveToSharedPreference(getApplicationContext(), "myphone", input.getText().toString());
                                okPressed = true;
                                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);

                                intent.putExtra("bitForNumberVerification", 1);
                                intent.putExtra("bitForProgressDialog",1);
                                startActivity(intent);
                                finish();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Operations.SaveToSharedPreference(getApplicationContext(), "myphone", "1");
                                    }
                                }, 3000);

                            } else {
                                okPressed = true;
                                toast("You must input a valid Mobile/phone number");
                                CheckPhoneNumber();
                            }
                        }
                    });

            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            //System.exit(0);
                            Intent i = new Intent(HomeActivity.this, WelcomeScreenActivity.class);
                            startActivityForResult(i, 2);

                            finish();

                        }
                    });
            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    CheckPhoneNumber();
                  /*  if (!okPressed) System.exit(0);*/
                }
            });

            alertDialog.show();
        } else {
            Operations.SaveToSharedPreference(getApplicationContext(), "myphone", Operations.getMyPhoneNumber(this));
        }
    }


    private void sendSmsVerify(final String pn) {

        //  String  ePhoneNumber = input.getText().toString();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {


                int random = (int) (Math.random() * 1000 + 100);
                Operations.SaveToSharedPreference(getApplicationContext(), "random", String.valueOf(random));

                if (pn.length() > 0 && random > 0 && Operations.strRes.equals("1")) {

                    SmsManager smsManager = SmsManager.getDefault();    // *************************
                    smsManager.sendTextMessage(pn, null, String.valueOf(random), null, null);  // *****************


          /*  Toast.makeText(HomeActivity.this, "Genarate random Number :  "
                    + random, Toast.LENGTH_LONG).show();
           showMessage("Please Wait !", "Reply the random Number To set Phone Number");*/
                    return;
                } else {
                   /* Toast.makeText(HomeActivity.this, "Verifying SMS failed!!! Please Try Again..", Toast.LENGTH_LONG).show();
                    mProgressDialog = new ProgressDialog(getApplicationContext());
                    mProgressDialog.dismiss();
                    finish();*/
                }
            }
        }, 3000);




    }


    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    protected void onDestroy() {
        super.onDestroy();
    }


    protected void onPause() {
        super.onPause();
    }


    protected void onRestart() {
        super.onRestart();
    }


    protected void onResume() {
        super.onResume();
    }


    protected void onStart() {
        super.onStart();
    }


    protected void onStop() {
        super.onStop();
    }


} // end class