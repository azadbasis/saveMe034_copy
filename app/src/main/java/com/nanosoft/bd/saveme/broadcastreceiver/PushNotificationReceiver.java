package com.nanosoft.bd.saveme.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.ContactsContract;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NanoSoft on 11/15/2016.
 */

public class PushNotificationReceiver extends BroadcastReceiver {

    static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    String newDate;

    @Override
    public void onReceive(Context context, Intent intent) {

       getPoshNotification(context, Operations.getMyPhoneNumber(context));
    }


    public void getPoshNotification(final Context context, String phoneNumber) {

        sharedPreferences = context.getSharedPreferences("SaveData", context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        Date today = Calendar.getInstance().getTime();
        newDate = df.format(today);

        String oldDate = sharedPreferences.getString("oldDatePushNotification", "12/10/2016");  //"14/10/2016";


        if (!oldDate.equals(newDate) ){
            editor.putInt("bitForPushNotification", 1);
            editor.commit();
        }else {

        }


        int bitForPushNotification = sharedPreferences.getInt("bitForPushNotification",1);

        if (phoneNumber.length()>5  && bitForPushNotification == 1) {

            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            RequestQueue requestQueue = Volley.newRequestQueue(context);

            final String userPhone = phoneNumber;

            String insertUrl = "http://saveme.com.bd/contactmaster/daysRemain.php";
            StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {

                        int responseDay = Integer.parseInt(response);

                        if (responseDay>-1 && responseDay<4){
                            String msg = responseDay+" days remain to expired your SaveMe App";
                            Operations.Notify(context, "Validation Notice!!!", msg);


                            editor.putString("oldDatePushNotification", newDate);
                            editor.putInt("bitForPushNotification", 0);
                            editor.commit();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if (error instanceof NoConnectionError) {
                        // Toast.makeText(getApplicationContext(), "Slow net connection", Toast.LENGTH_SHORT).show();
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
            //  if (Operations.strRes != null) i = Integer.parseInt(Operations.strRes);
        }
    }

}
