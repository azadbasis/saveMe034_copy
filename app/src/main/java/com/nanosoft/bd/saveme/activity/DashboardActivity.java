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

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nanosoft.bd.saveme.R;


public abstract class DashboardActivity extends AppCompatActivity {

RelativeLayout db1_root;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.main);
        getSupportActionBar().hide();

        db1_root = (RelativeLayout) findViewById(R.id.db1_root);

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


    public void onClickHome(View v) {
        goHome(this);
    }


    public void onClickSearch(View v) {
       // startActivity(new Intent(getApplicationContext(), SearchActivity.class));
    }


    public void onClickAbout(View v) {

//        animationControl();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//
//            }
//        }, 500);

        startActivity(new Intent(getApplicationContext(), AboutActivity.class));

    }


    public void onClickFeature(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.home_btn_feature1:

                if (Build.VERSION.SDK_INT > 22) {
                    permissionPhoneCall();
                }else {
                    animationControl();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getApplicationContext(), EmergencyCallActivity.class));
                        }
                    }, 500);


                }
                break;


            case R.id.home_btn_feature2:

                if (Build.VERSION.SDK_INT > 22) {
                    permissionPhoneBook();

                }else {

                    animationControl();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getApplicationContext(), PhoneBookActivity.class));
                        }
                    }, 500);

                }

                break;


            case R.id.home_btn_feature3:

                if (Build.VERSION.SDK_INT > 22) {
                    bitForLocation=1;
                    permissionLocation();
                   // bitForLocation = 0;

                }else {

                    animationControl();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(), EmergencyTextActivity.class));
                        }
                    }, 500);

                }
                break;


            case R.id.home_btn_feature4:

                if (Build.VERSION.SDK_INT > 22) {
                    bitForLocation=2;
                    permissionLocation();
                   // bitForLocation = 0;

                }else {
                    animationControl();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getApplicationContext(), MapDirectionActivity.class));
                        }
                    }, 500);


                }
                break;


            case R.id.home_btn_feature5:

                animationControl();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(), NewsFeedViewActivity.class));
                    }
                }, 500);

                break;


            case R.id.home_btn_feature6:
                if (Build.VERSION.SDK_INT > 22) {
                    bitForLocation=3;
                    permissionLocation();
                  //  bitForLocation = 0;

                }else {

                    animationControl();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(getApplicationContext(), PositionTrackerActivity.class));
                        }
                    }, 500);

                }
                break;


            case R.id.home_btn_feature7:
                if (Build.VERSION.SDK_INT > 22) {
                    bitForLocation=4;
                    permissionLocation();
                   // bitForLocation = 0;

                }else {

                    animationControl();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getApplicationContext(), SMSTrackersActivity.class));
                        }
                    }, 500);

                }
                break;


            case R.id.home_btn_feature8:
                if (Build.VERSION.SDK_INT > 22) {
                    bitForLocation=5;
                    permissionLocation();
                    //bitForLocation = 0;

                }else {

                    animationControl();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getApplicationContext(), FnFTrackingActivity.class));
                        }
                    }, 500);

                }
                break;


            case R.id.home_btn_feature9:
                if (Build.VERSION.SDK_INT > 22) {
                    bitForLocation=6;
                    permissionLocation();
                  //  bitForLocation = 0;

                }else {

                    animationControl();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getApplicationContext(), LocationNotifierActivity.class));
                        }
                    }, 500);

                }
                break;


            case R.id.home_btn_feature10:

                animationControl();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        startActivity(new Intent(getApplicationContext(), AppPurchaseActivity.class));
                    }
                }, 500);

                break;
            default:
                break;
        }
    }


    public void goHome(Context context) {
        final Intent intent = new Intent(context, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.startActivity(intent);
    }

    public void setTitleFromActivityLabel(int textViewId) {
        TextView tv = (TextView) findViewById(textViewId);
        if (tv != null) tv.setText(getTitle());
    } // end setTitleText


    public void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    } // end toast

    public void trace(String msg) {
        Log.d("Demo", msg);
        toast(msg);
    }


    private void permissionPhoneBook() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {Manifest.permission.READ_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS_FOR_PHONE_BOOK);

            return;
        } else {

            animationControl();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivity(new Intent(getApplicationContext(), PhoneBookActivity.class));
                }
            }, 500);


        }


    }

    private void permissionLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_CODE_ASK_PERMISSIONS_FOR_LOCATION);

            return;
        } else {

            animationControl();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    targetActivity();
                }
            }, 500);



        }


    }
    private void animationControl(){

        animation(R.id.db1_root);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                animation2(R.id.db1_root);
            }
        }, 2000);
    }

    private void targetActivity(){
        if (bitForLocation == 1){
            startActivity(new Intent(getApplicationContext(), EmergencyTextActivity.class));
        }else if (bitForLocation == 2){
            startActivity(new Intent(getApplicationContext(), MapDirectionActivity.class));
        }else if (bitForLocation == 3){
            startActivity(new Intent(getApplicationContext(), PositionTrackerActivity.class));
        }else if (bitForLocation == 4){
            startActivity(new Intent(getApplicationContext(), SMSTrackersActivity.class));
        }else if (bitForLocation == 5){
            startActivity(new Intent(getApplicationContext(), FnFTrackingActivity.class));
        }else if (bitForLocation == 6){
            startActivity(new Intent(getApplicationContext(), LocationNotifierActivity.class));
        }

        bitForLocation=0;
    }



    private void permissionStorage() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }else {
            startActivity(new Intent(getApplicationContext(), EmergencyCallActivity.class));
        }
    }



    final private int REQUEST_CODE_ASK_PERMISSIONS_FOR_PHONE_CALL = 121;
    final private int REQUEST_CODE_ASK_PERMISSIONS_FOR_LOCATION = 131;
    final private int REQUEST_CODE_ASK_PERMISSIONS_FOR_PHONE_BOOK = 141;

    private void animation(int layoutId){

        View view = (View) findViewById(layoutId);

        Animation animation = new TranslateAnimation(0,-1500f, 0, 0);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);
        animationSet.setDuration(800);
        animationSet.setFillAfter(true);
        view.startAnimation(animationSet);

    }
    private void animation2(int layoutId){

        View view = (View) findViewById(layoutId);

        Animation animation = new TranslateAnimation(0,0, 0, 0);
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(animation);
        animationSet.setDuration(700);
        animationSet.setFillAfter(true);
        view.startAnimation(animationSet);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS_FOR_PHONE_CALL:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    animationControl();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getApplicationContext(), EmergencyCallActivity.class));
                        }
                    }, 500);


                } else {
                    Toast.makeText(this, "You have to allow this permission", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_FOR_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                  //  targetActivity();

                    animationControl();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            targetActivity();
                        }
                    }, 500);

                } else {
                    Toast.makeText(this, "You have to allow this permission", Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_CODE_ASK_PERMISSIONS_FOR_PHONE_BOOK:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    animationControl();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            startActivity(new Intent(getApplicationContext(), PhoneBookActivity.class));
                        }
                    }, 500);


                } else {
                    Toast.makeText(this, "You have to allow this permission", Toast.LENGTH_LONG).show();
                }

                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void permissionPhoneCall() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_ASK_PERMISSIONS_FOR_PHONE_CALL);

            return;
        } else {

            animationControl();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    startActivity(new Intent(getApplicationContext(), EmergencyCallActivity.class));
                }
            }, 500);

        }


    }

    int bitForLocation = 0;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;

} // end class
