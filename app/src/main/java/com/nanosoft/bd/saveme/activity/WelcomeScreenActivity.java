package com.nanosoft.bd.saveme.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.common.collect.Lists;
import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.viewpager.SpringIndicator;
import com.nanosoft.bd.saveme.viewpager.viewpager.ScrollerViewPager;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import github.chenupt.multiplemodel.viewpager.ModelPagerAdapter;
import github.chenupt.multiplemodel.viewpager.PagerModelManager;

public class WelcomeScreenActivity extends AppCompatActivity {

    RelativeLayout introMessage;
    LinearLayout appContent;
    //Introduce an delay
    private final int WAIT_TIME = 20;
    ProgressBar spinner;

    RelativeLayout signUpLo;
    WebView signUpWebView;

    private ProgressDialog mProgressDialog;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    ScrollerViewPager viewPager;

    int bitForItemMenu;
    int bitForSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("LoadingScreenActivity  screen started");
        setContentView(R.layout.activity_welcomescreen);

        spinner = (ProgressBar) findViewById(R.id.mainSpinner1);
        introMessage = (RelativeLayout) findViewById(R.id.welcome_message_layout);
        appContent = (LinearLayout) findViewById(R.id.app_content_layout);

        signUpLo = (RelativeLayout) findViewById(R.id.signUpLo);
        signUpWebView = (WebView) findViewById(R.id.signUpWebView);


        sharedPreferences = getSharedPreferences("SaveData", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        bitForSignUp  = getIntent().getIntExtra("signUp", 0);
        if (bitForSignUp==1){
            signUp();
        }


        viewPager = (ScrollerViewPager) findViewById(R.id.view_pager);
        SpringIndicator springIndicator = (SpringIndicator) findViewById(R.id.indicator);
        // Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        // setSupportActionBar(toolbar);

        PagerModelManager manager = new PagerModelManager();
        manager.addCommonFragment(GuideFragment.class, getBgRes(), getTitles());
        ModelPagerAdapter adapter = new ModelPagerAdapter(getSupportFragmentManager(), manager);
        viewPager.setAdapter(adapter);
        viewPager.fixScrollSpeed();

        // just set viewPager
        springIndicator.setViewPager(viewPager);


    }


    private List<String> getTitles(){
        return Lists.newArrayList("•", "•", "•", "•","•","•","•","•","•");
    }

    private List<Integer> getBgRes(){
        return Lists.newArrayList(R.drawable.img_emergency_call, R.drawable.img_emergency_text, R.drawable.img_map_direction,
                R.drawable.img_location_notifier,R.drawable.img_sms_tracking, R.drawable.img_fnf_tracking,R.drawable.img_phone_book,
                R.drawable.img_position_tracker,R.drawable.img_news_feed
        );
    }

    int bitForOthersMatter  = 0;


    private void permission() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {Manifest.permission.SEND_SMS},
                    REQUEST_CODE_ASK_PERMISSIONS);


            return;
        } else {
            if (bitForOthersMatter==1){
                othersMatter();
                bitForOthersMatter=0;
            }

        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   othersMatter();
                } else {
                    Toast.makeText(this, "You have to allow this permission", Toast.LENGTH_LONG).show();
                }
                break;

            case REQUEST_CODE_ASK_PERMISSIONS_FOR_SIGN_UP :
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    signUp();
                } else {
                    Toast.makeText(this, "You have to allow this permission", Toast.LENGTH_LONG).show();
                }
                break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }






    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;


    private void alertDialogForPermission() {

        android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(WelcomeScreenActivity.this);
        alertDialog.setTitle("Permission!!!");
        alertDialog.setMessage("Welcome Marshmallow user. You need some permission to run this App. Please Allow this permission.\n\n(Just click permission & turn on all permission.)");
        alertDialog.setIcon(R.drawable.ic_action_warning);

        alertDialog.setNegativeButton("Deny",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.setPositiveButton("Allow",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                      /*  final Intent i = new Intent();
                        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        i.addCategory(Intent.CATEGORY_DEFAULT);
                        i.setData(Uri.parse("package:" + getPackageName(), ));
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
                        startActivity(i);*/

                       /* Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);*/





                       /* if (Manifest.permission.CAMERA)) {
                            // Have permission, do the thing!
                            Toast.makeText(this, "TODO: Camera things", Toast.LENGTH_LONG).show();
                        }*/


                    }
                });
        alertDialog.show();


    }



    public void dismisWelcomeMessageBox(View view) {


        if (Build.VERSION.SDK_INT > 22) {
            bitForOthersMatter = 1;
            permission();

        }else {
            othersMatter();
        }

    }

    private void othersMatter() {

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {


                System.out.println("Going to Profile Data");
      /* Create an Intent that will start the ProfileData-Activity. */
                ConnectivityManager connect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

// ARE WE CONNECTED TO THE NET
                if (connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                        connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
                    // ...
                    introMessage.setVisibility(View.INVISIBLE);
                    appContent.setVisibility(View.VISIBLE);
                      /*progressbar*/
                    findViewById(R.id.mainSpinner1).setVisibility(View.VISIBLE);
                    spinner.setIndeterminate(true);
                    spinner.getIndeterminateDrawable().setColorFilter(Color.parseColor("#80DAEB"),
                            PorterDuff.Mode.MULTIPLY);

                    this.Sleep(400);

                    Intent mainIntent = new Intent(WelcomeScreenActivity.this, HomeActivity.class);
                    // WelcomeScreenActivity.this.startActivity(mainIntent);
                    //startActivity(mainIntent);
                    startActivityForResult(mainIntent, 1);
                    finish();
                } else {
                    // Toast.makeText(WelcomeScreenActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(WelcomeScreenActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("No Internet Connection")
                            .setMessage("Please check your Internet Connection")
                            .setCancelable(false)
                            .setPositiveButton("Wifi Data", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                                }
                            })/*.setNegativeButton("Sim Data",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           // startActivity(new Intent(Settings.));
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setComponent(new ComponentName("com.android.settings",
                                    "com.android.settings.Settings$DataConnectionActivity"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                    })*/.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            finish();
                        }
                    })
                            .show();

                }


            }


            private void Sleep(int i) {
            }
        }, WAIT_TIME);
    }

        /*progressbar*/






    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Exit")
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        System.exit(0);
                        Intent in = new Intent(WelcomeScreenActivity.this, HomeActivity.class);
                        in.setAction("stop");
                        stopService(in);
                    }
                }).setNegativeButton("No", null).show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult

   /* @Override
    public void onDestroy()
    {
        android.os.Process.killProcess(android.os.Process.myPid());
        super.onDestroy();
    }*/


    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_welcome_screen, menu);

        action_sign_up = menu.findItem(R.id.action_sign_up);
        action_back = menu.findItem(R.id.action_back);


        int bitForItemMenu = sharedPreferences.getInt("bitForItemMenu",0);

        if (bitForItemMenu==1){
            action_sign_up.setVisible(false);
            action_back.setVisible(false);

        }

        if (bitForSignUp==1) {
            action_sign_up.setVisible(false);
            action_back.setVisible(true);
        }

        //   webView.setWebViewClient(new WebViewClient());
        //  webView.loadUrl("http://m.facebook.com/khoiop");





        return true;
    }

    MenuItem action_sign_up, action_back;


    final private int REQUEST_CODE_ASK_PERMISSIONS_FOR_SIGN_UP = 112;
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_up) {

            if (Build.VERSION.SDK_INT > 22) {

                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.SEND_SMS},
                            REQUEST_CODE_ASK_PERMISSIONS_FOR_SIGN_UP);
                }else {
                    signUp();
                }

            }else {
                signUp();
            }




            return true;
        } else if (id == R.id.action_back) {

            setTitle("SaveMe");
            action_sign_up.setVisible(true);
            action_back.setVisible(false);
            introMessage.setVisibility(View.VISIBLE);
            signUpLo.setVisibility(View.GONE);

            return true;
        }
        else if (id == R.id.action_help) {
            String url = "http://saveme.com.bd/#guide";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void signUp() {

        ConnectivityManager connect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {

            setTitle("Sign Up");

            introMessage.setVisibility(View.GONE);
            signUpLo.setVisibility(View.VISIBLE);

            WebSettings webSettings = signUpWebView.getSettings();
            webSettings.setJavaScriptEnabled(true);

            if (bitForSignUp==0) {
                action_sign_up.setVisible(false);
                action_back.setVisible(true);
            }
//       // webView.setWebChromeClient(new WebChromeClient());
            signUpWebView.setWebViewClient(new WebViewClient());
            // webView.loadUrl("https://m.facebook.com");
            signUpWebView.loadUrl("http://saveme.com.bd/AdminController/mobileSignup");
            //webView.addChildrenForAccessibility()

            //  String signUpUlr = signUpWebView.getUrl();

            //  signUpUlr.su

            mProgressDialog = new ProgressDialog(WelcomeScreenActivity.this);
            mProgressDialog.setMessage("Loading........");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            signUpWebView.setWebViewClient(new WebViewClient() {


                public void onPageFinished(WebView view, String url) {
                    try {

                        mProgressDialog.dismiss();

                        int webProgress = signUpWebView.getProgress();
                        // if (webProgress==100) {
                        String signUpUlr = "http://saveme.com.bd/AdminController/adminPanelView/"; //signUpWebView.getOriginalUrl();
                        String newSignUpUlr = signUpWebView.getUrl();
                        String finalUrl = newSignUpUlr.substring(signUpUlr.length(), newSignUpUlr.length());

                        // Toast.makeText(WelcomeScreenActivity.this, finalUrl.length()+""+ finalUrl, Toast.LENGTH_SHORT).show();

                        // newSignUpUlr.sub

                        // Toast.makeText(WelcomeScreenActivity.this,finalUrl , Toast.LENGTH_SHORT).show();

                        if (isNumber(finalUrl)) {
                            Toast.makeText(WelcomeScreenActivity.this, "Sign Up Completed", Toast.LENGTH_SHORT).show();

//                            editor.putInt("bitForItemMenu",1);
//                            editor.commit();

                            if (Build.VERSION.SDK_INT > 22) {
                                // Toast.makeText(this, "V-6", Toast.LENGTH_SHORT).show();
                                permission();
                                Intent intent = new Intent(WelcomeScreenActivity.this, HomeActivity.class);
                                intent.putExtra("bitForPN", 1);
                                intent.putExtra("phoneNumber", finalUrl);
                                startActivityForResult(intent, 1);
                                finish();
                            }else {
                                Intent intent = new Intent(WelcomeScreenActivity.this, HomeActivity.class);
                                intent.putExtra("bitForPN", 1);
                                intent.putExtra("phoneNumber", finalUrl);
                                startActivityForResult(intent, 1);
                                finish();
                            }



                        }
                    } catch (Exception e) {
                        //  Toast.makeText(WelcomeScreenActivity.this, "dfggtfsdetgf", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }

                }
            });

        }else {
            // Toast.makeText(WelcomeScreenActivity.this, "No internet Connection", Toast.LENGTH_SHORT).show();
            new AlertDialog.Builder(WelcomeScreenActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("No Internet Connection")
                    .setMessage("Please check your Internet Connection")
                    .setCancelable(false)
                    .setPositiveButton("Wifi Data", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })/*.setNegativeButton("Sim Data",  new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           // startActivity(new Intent(Settings.));
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.setComponent(new ComponentName("com.android.settings",
                                    "com.android.settings.Settings$DataConnectionActivity"));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);

                        }
                    })*/.setNeutralButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                }
            })
                    .show();

        }

    }

    private boolean isNumber(String signUpUlr) {
        Pattern p = Pattern.compile("[0-9]{1,}");
        Matcher m = p.matcher(signUpUlr);
        boolean b = m.matches();
        return b;
    }

    @Override
    protected void onResume() {


        super.onResume();
    }
}
