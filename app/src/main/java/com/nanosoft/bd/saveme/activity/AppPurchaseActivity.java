package com.nanosoft.bd.saveme.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nanosoft.bd.saveme.R;

import java.util.HashMap;
import java.util.Map;


public class AppPurchaseActivity extends DashboardActivity {

    WebView validationWv ;


    private ProgressDialog mProgressDialog;

    String validationResponse="";

    TextView validationTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ativity_app_purchase);
        setTitleFromActivityLabel(R.id.title_text);

        validationWv = (WebView) findViewById(R.id.validationWv);
       /* WebSettings webSettings = upGradeWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);*/

      //  upGradeWebView.setWebViewClient(new WebViewClient());
        // webView.loadUrl("https://m.facebook.com");
        //upGradeWebView.loadUrl("http://saveme.com.bd/AdminController/mobileSignup");

        validationTv = (TextView)findViewById(R.id.validationTv);
        String phoneNumber = Operations.getStringFromSharedPreference(this,"myphone");

        getValidation2(phoneNumber);

        new Handler().postDelayed(new Runnable() {
            public void run() {



                if (validationResponse.equals("")){
                    validationTv.setText("No response");
                }else {
                    validationTv.setText(validationResponse);
                }
            }
        }, 3000);

      //  showValidationWebView(phoneNumber);
    }

    @Override
    public void onBackPressed() {
        animation(R.id.appPurchaseLayout);

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



    public void onClickFamilyPayment(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
               // intent.setData(Uri.parse("http://smartcampus.com.bd/"));
                intent.setData(Uri.parse("http://saveme.com.bd/"));
                startActivity(intent);

    }


    private void showValidationWebView(String phoneNumber) {

        ConnectivityManager connect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {

            validationWv.setWebViewClient(new WebViewClient());
            validationWv.loadUrl("http://saveme.com.bd/AdminController/mobileSignup/"+phoneNumber);

            mProgressDialog = new ProgressDialog(AppPurchaseActivity.this);
            mProgressDialog.setMessage("Loading........");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            // mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            validationWv.setWebViewClient(new WebViewClient() {

                public void onPageFinished(WebView view, String url) {

                    mProgressDialog.dismiss();

                }
            });

        }else {
            new AlertDialog.Builder(AppPurchaseActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("No Internet Connection")
                    .setMessage("Please check your Internet Connection")
                    .setCancelable(false)
                    .setPositiveButton("Wifi Data", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    }).setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    finish();
                }
            })
                    .show();

        }

    }

    private void getValidation2(String userPhone1) {

        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://saveme.com.bd/contactmaster/checkUser.php";
// Request a string response from the provided URL.


        final String userPhone = userPhone1;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        validationResponse = response;


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                finish();
                Toast.makeText(getApplicationContext(), "Slow net connection", Toast.LENGTH_SHORT).show();
            }


        }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("userPhone", userPhone);
                return parameters;
            }
        };
        NetworkInfo info = connectivity.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            queue.add(stringRequest);
        }
        int i = 0;
        //Toast.makeText(ctx, "'" + strRes + "'", Toast.LENGTH_LONG).show();
        if (Operations.strRes != null) i = Integer.parseInt(Operations.strRes);

    }



    public void getValidation(String phoneNumber) {

        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        final String userPhone = phoneNumber;

        String insertUrl = "http://saveme.com.bd/contactmaster/checkUser.php";
        StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {


                    validationResponse = response;


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    finish();
                    Toast.makeText(getApplicationContext(), "Slow net connection", Toast.LENGTH_SHORT).show();
                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("userPhone", userPhone);
                return parameters;
            }
        };
        NetworkInfo info = connectivity.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            requestQueue.add(request);
        }
        int i = 0;
        //Toast.makeText(ctx, "'" + strRes + "'", Toast.LENGTH_LONG).show();
        if (Operations.strRes != null) i = Integer.parseInt(Operations.strRes);

    }


}
