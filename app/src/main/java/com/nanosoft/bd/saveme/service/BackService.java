package com.nanosoft.bd.saveme.service;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nanosoft.bd.saveme.activity.AppPurchaseActivity;
import com.nanosoft.bd.saveme.activity.Operations;
import com.nanosoft.bd.saveme.application.SApplication;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import Module.UserLocation;

/**
 * Created by Azharul on 21-Jul-16.
 */
public class BackService extends android.app.IntentService {


    public BackService() {
        super("BackService");
    }

    RequestQueue requestQueue;
    ConnectivityManager connectivity;

    @Override
    public void onCreate() {
        super.onCreate();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        doWork();
    }

    /**
     * Perform the task
     */
    void doWork() {

        if (SApplication.LOCATION != null) {
            double lat = SApplication.LOCATION.getLatitude();
            double lon = SApplication.LOCATION.getLongitude();
            String co = "Lat:" + lat + " Lon:" + lon;

            String address = "";

            Geocoder geocoder = new Geocoder(getApplicationContext(), new Locale("en"));
            try {
                // get address from location
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && addresses.size() != 0) {
                    StringBuilder builder = new StringBuilder();
                    Address returnAddress = addresses.get(0);
                    for (int i = 0; i < returnAddress.getMaxAddressLineIndex(); i++) {
                        builder.append(returnAddress.getAddressLine(i));
                        builder.append(", ");
                    }
                    address = builder.toString();

                    // Toast.makeText(getApplicationContext(), messageLocation, Toast.LENGTH_LONG).show();
                } else {
                }
            } catch (IOException e) {
            }

            Operations.SaveToSharedPreference(this, "Address", address);
            Operations.SaveToSharedPreference(this, "Coordinate", co);


            if (Operations.IsOnline(getApplicationContext())) {

                upDate(lat, lon, co, address);
                List<UserLocation> list = Operations.GetLocallySavedLocations(getApplicationContext());
                if (list.size() > 0) {
                    for (UserLocation location : list) {
                        upDate(location.Latitude, location.Longitue, "", Operations.GetFullAddress(getApplicationContext(), location.getLatLng()));
                    }
                    Operations.ClearLocallySavedLocations(getApplicationContext());
                }
            } else {
                UserLocation ul = new UserLocation();
                ul.Name = "";
                ul.Latitude = lat;
                ul.Longitue = lon;
                Operations.SaveLocationLocally(getApplicationContext(), ul);
            }
            //Toast.makeText(this, "please, register!", Toast.LENGTH_LONG).show();

        }
    }

  //  String insertUrl = "http://smartcampus.com.bd/contactmaster/insertLocation.php";
    String insertUrl = "http://saveme.com.bd/contactmaster/insertLocation.php";

    private void upDate(final double lat, final double lon, final String co, final String address) {

        StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    String errors = "No internet Access, Check your internet connection.";
                    Toast.makeText(getApplicationContext(), errors, Toast.LENGTH_LONG).show();

                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                Map<String, String> parameters = new HashMap<String, String>();
                //  Map<String, String> parametersall = new HashMap<String, String>();


                Calendar c = Calendar.getInstance();
                //String currentDateTime = String.format("%1$tm-%1$te-%1$tY %1$tH:%1$tM:%1$tS", c);

                //DateFormat.getDateTimeInstance().format(new Date());

                parameters.put("coordinates", co);
                parameters.put("latitude", String.valueOf(lat));
                parameters.put("longitude", String.valueOf(lon));
                parameters.put("address", address);
                //parameters.put("currentDateTime", currentDateTime);
                parameters.put("userPhone", Operations.getStringFromSharedPreference(getApplicationContext(), "myphone"));

                return parameters;
            }
        };
        NetworkInfo info = connectivity.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            requestQueue.add(request);
            //Toast.makeText(getApplicationContext(),"Now You are at: "+"\n"+address+"!",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }


    }



/*Notify(co);
    private void Notify(String str) {

        // Invoking the default notification service

        NotificationManager mNotificationManager;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this);
        mBuilder.setContentTitle("Reminder");
        mBuilder.setSmallIcon(R.drawable.icon);
        mBuilder.setContentText(str);
        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent=new Intent(this,HomeActivity.class);
        PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);



        // notificationID allows you to update the notification later  on.


        mNotificationManager.notify(0, mBuilder.build());
    }
*/
}
