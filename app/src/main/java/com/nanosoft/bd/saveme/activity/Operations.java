package com.nanosoft.bd.saveme.activity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
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
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.application.SApplication;
import com.nanosoft.bd.saveme.broadcastreceiver.SmsReceiver;
import com.nanosoft.bd.saveme.service.BackService;
import com.nanosoft.bd.saveme.service.CallingService;
import com.nanosoft.bd.saveme.service.EmergencyTextService;
import com.nanosoft.bd.saveme.service.LocationService;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import Module.UserLocation;


/**
 * Created by Nanosoft-Android on 8/1/2016.
 *
 * This Class for common method
 *
 *
 */
public class Operations {


    /*Get location Details*/
    public static String GetFullAddress(Context context) {
        String address = "";

        if (SApplication.LOCATION != null) {
            double lat = SApplication.LOCATION.getLatitude();
            double lon = SApplication.LOCATION.getLongitude();
            String co = lat + "," + lon;


            Geocoder geocoder = new Geocoder(context, new Locale("en"));
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
                    builder.append(returnAddress.getAdminArea() + ", ");
                    builder.append(returnAddress.getCountryName() + ", ");

                    address = builder.toString();


                    // Toast.makeText(getApplicationContext(), messageLocation, Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
            }

            Operations.SaveToSharedPreference(context, "Address", address);
            Operations.SaveToSharedPreference(context, "Coordinate", co);
        }

        return address;
    }
    /*Ending Get location Details*/


    /*Start get specific location*/
    public static String getSpecificLocation(Context context, double lat, double lon) {
        String address = "";

        if (SApplication.LOCATION != null) {
            Geocoder geocoder = new Geocoder(context, new Locale("en"));
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
                    // builder.append(returnAddress.getAdminArea() + ", ");
                    builder.append(returnAddress.getCountryName() + ". ");

                    address = builder.toString();
                }
            } catch (IOException e) {
            }

        }
        return address;

    }
    /*Ending get specific location*/

    public static void SaveToSharedPreference(Context ctx, String key, String value) {
        SharedPreferences.Editor editor;
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    static SharedPreferences sharedPreferences;


    public static String getStringFromSharedPreference(Context ctx, String key) {
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void IntSaveToSharedPreference(Context ctx, String key, int value) {
        SharedPreferences.Editor editor;
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }


    public static int getIntegerSharedPreference(Context ctx, String key, int defaultValue) {
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public static String getStringSharedPreference(Context ctx, String key, String defaultValue) {
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public static Boolean ContainsKey(Context ctx, String key) {
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        return sharedPreferences.contains(key);
    }


    //get phone number
    public static String getMyPhoneNumber(Context ctx) {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        //String imsi = mTelephonyMgr.getSubscriberId();
        String phnNo = mTelephonyMgr.getLine1Number();
        //if (phnNo == null || phnNo.length() < 11) {
        if (ContainsKey(ctx, "myphone"))
            phnNo = getStringFromSharedPreference(ctx, "myphone");
        else
            phnNo = "1";
        // }
        return phnNo;
    }

    static String strRes;

    public static boolean checkUserFirstTime = false;


    public static boolean checkUser(final Context ctx) {

        ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);

        final String userPhone = getMyPhoneNumber(ctx);
        //  String insertUrl = "http://smartcampus.com.bd/contactmaster/checkUser.php";
        String insertUrl = "http://saveme.com.bd/contactmaster/checkUser.php";
        StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {


            @Override
            public void onResponse(String response) {

                try {
                    strRes = response;


                    if (checkUserFirstTime && response.equals("2")) {

                        TextView tv = new TextView(ctx);
                        tv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String url = "http://saveme.com.bd/#myModal-signup";
                                Intent i = new Intent(Intent.ACTION_VIEW);
                                i.setData(Uri.parse(url));
                                ctx.startActivity(i);
                            }
                        });
                        tv.setLinksClickable(true);
                        tv.setText(Html.fromHtml("<a href='http://saveme.com.bd/#myModal-signup'>http://saveme.com.bd</a>"), TextView.BufferType.SPANNABLE);
                        tv.setGravity(Gravity.CENTER);
                        tv.setTextSize(14);
                        new AlertDialog.Builder(ctx).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Validation Expired")
                                .setMessage("Please, Visit this site to renew")
                                .setView(tv)
                                .setCancelable(false)
                                .setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                        ((HomeActivity) ctx).finish();
                                        ctx.startService(new Intent(ctx, CallingService.class));
                                        ctx.stopService(new Intent(ctx, CallingService.class));

                                        ctx.startService(new Intent(ctx, EmergencyTextService.class));
                                        ctx.stopService(new Intent(ctx, EmergencyTextService.class));

                                        ctx.startService(new Intent(ctx, LocationService.class));
                                        ctx.stopService(new Intent(ctx, LocationService.class));

                                        ctx.startService(new Intent(ctx, BackService.class));
                                        ctx.stopService(new Intent(ctx, BackService.class));


                                        PackageManager pm = ctx.getPackageManager();
                                        ComponentName componentName = new ComponentName(ctx, SmsReceiver.class);
                                        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                                                PackageManager.DONT_KILL_APP);

                                    }
                                }).show();

                    } else if (getMyPhoneNumber(ctx).length() > 1 && checkUserFirstTime && response.equals("3")) {

                        new AlertDialog.Builder(ctx).setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Sign Up")
                                .setMessage("You have to Sign Up first.")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        SaveToSharedPreference(ctx, "myphone", "1");
                                        Intent intent = new Intent(ctx, WelcomeScreenActivity.class);
                                        intent.putExtra("signUp", 1);
                                        ctx.startActivity(intent);
                                        ((HomeActivity) ctx).finish();

                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SaveToSharedPreference(ctx, "myphone", "1");
                                ((HomeActivity) ctx).finish();
                            }
                        }).show();

                    } else if (checkUserFirstTime && response.equals("1")) {
                        checkUserFirstTime = false;


                        int switchStatusForECall = sharedPreferences.getInt("switchStatuss", 0);
                        int switchStatusForEText = sharedPreferences.getInt("switchStatus", 0);

                        if (switchStatusForECall == 1) {
                            ctx.startService(new Intent(ctx, CallingService.class));
                        }
                        if (switchStatusForEText == 1) {
                            ctx.startService(new Intent(ctx, EmergencyTextService.class));
                        }
                        ctx.startService(new Intent(ctx, LocationService.class));
                        ctx.startService(new Intent(ctx, BackService.class));

                        PackageManager pm = ctx.getPackageManager();
                        ComponentName componentName = new ComponentName(ctx, SmsReceiver.class);
                        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                PackageManager.DONT_KILL_APP);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                    ((HomeActivity) ctx).finish();
                    Toast.makeText(ctx, "Slow net connection", Toast.LENGTH_SHORT).show();

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
        if (strRes != null) i = Integer.parseInt(strRes);
        return i == 1;
    }

    /////////////////////////////////////////////////code for saving locations
    private static List<UserLocation> list = new ArrayList<>();

    public static void SaveSelectedLocation(Context ctx, UserLocation loc) {
        boolean found = false;
        if (list.size() > 0) {
            for (UserLocation ul : list) {
                if (ul.Name.equals(loc.Name)) {
                    ul.Latitude = loc.Latitude;
                    ul.Longitue = loc.Longitue;
                    found = true;
                    break;
                }
            }
        }

        if (!found)
            list.add(loc);

        Gson gson = new Gson();
        String json = gson.toJson(list);
        SaveToSharedPreference(ctx, "locationlist", json);
    }


    public static List<UserLocation> GetSavedLocations(Context ctx) {
        String str = getStringFromSharedPreference(ctx, "locationlist");
        if (str.length() > 0) {
            Gson gson = new Gson();
            list = gson.fromJson(str, new TypeToken<List<UserLocation>>() {
            }.getType());
            return list;
        } else return new ArrayList<UserLocation>();
    }


    public static void Delete(Context ctx, UserLocation userLocation) {
        for (UserLocation ul : list
                ) {
            if (ul.Name.equals(userLocation.Name)) {
                list.remove(ul);
                break;
            }
        }

        Gson gson = new Gson();
        String json = gson.toJson(list);
        SaveToSharedPreference(ctx, "locationlist", json);
    }


    static String currentPlace = "";


    ////
    public static void NotifyInSavedArea(final Context ctx, Location location) {
        list = GetSavedLocations(ctx);

        SharedPreferences.Editor editor;
        sharedPreferences = ctx.getSharedPreferences("SaveData", ctx.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());

        int i =0;

        for (UserLocation temp : list) {


            DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
            Date today = Calendar.getInstance().getTime();
            String newDate = df.format(today);

            String oldDate = sharedPreferences.getString("oldDate", "12/10/2016");  //"14/10/2016";
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date strDate = null;
            try {
                strDate = sdf.parse(oldDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (strDate.after(new Date())) {

                editor.putInt("bit", 1);
                editor.putInt("bitForByPass", 1);
                editor.putInt("bitForDistanceLimit", 1);
                editor.commit();

            } else {

            }


            int bit = sharedPreferences.getInt("bit", 1);

            int bitForDistanceLimit = sharedPreferences.getInt("bitForDistanceLimit", 1);
            String ePhoneNumber = getStringFromSharedPreference(ctx, "vePhoneNumber");


            String [] locationName = new String[list.size()];
            double [] distance = new double[list.size()];


            locationName[i]= temp.Name;

         //   double distance = distance(ll.latitude, ll.longitude, temp.Latitude, temp.Longitue);
            distance [i]= distance(ll.latitude, ll.longitude, temp.Latitude, temp.Longitue);

            if (distance[i] > 1 && bitForDistanceLimit == 1 ) {
                editor.putInt("bit", 1);
                editor.commit();
            }




            if (distance[i] < 0.1 && !currentPlace.equals(temp.Name) && ePhoneNumber.length() > 0) {

                int mynumber = sharedPreferences.getInt("fnftime", 1);


                if (bit == 1) {
                      android.telephony.SmsManager smsManager = android.telephony.SmsManager.getDefault();
                     smsManager.sendTextMessage(ePhoneNumber, null, "Your friends and family are in/at " + temp.Name + "\n\"http://maps.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude(), null, null);

                    String msg = "Your friends and family are in/at " + temp.Name + "\n\"http://maps.google.com/maps?q=" + location.getLatitude() + "," + location.getLongitude();
                    Notify(ctx,"Current Location", msg);
                    currentPlace = temp.Name;
                    rnd = new Random().nextInt();


                    String OldOldDate = sharedPreferences.getString("OldOldDate", oldDate);
                    editor.putString("OldOldDate", OldOldDate);
                    editor.commit();

                    int bitForByPass = sharedPreferences.getInt("bitForByPass", 1);


                    if (mynumber == 1) {
                        editor.putInt("bit", 0);
                        editor.putString("oldDate", newDate);
                        editor.putInt("bitForByPass", 1);
                        editor.putInt("bitForDistanceLimit", 0);
                        editor.commit();
                    }
                    if (mynumber == 2) {

                        if (bitForByPass == 2) {
                            editor.putInt("bit", 0);
                            editor.putString("oldDate", newDate);
                            editor.putInt("bitForByPass", 1);
                            editor.putInt("bitForDistanceLimit", 0);
                            editor.commit();
                        } else {
                            bitForByPass++;
                            editor.putInt("bitForByPass", bitForByPass);
                            editor.commit();
                        }
                    }
                    if (mynumber == 3) {

                        if (bitForByPass == 3) {
                            editor.putInt("bit", 0);
                            editor.putString("oldDate", newDate);
                            editor.putInt("bitForByPass", 1);
                            editor.putInt("bitForDistanceLimit", 0);
                            editor.commit();
                        } else {
                            bitForByPass++;
                            editor.putInt("bitForByPass", bitForByPass);
                            editor.commit();
                        }

                    }
                    if (mynumber == 4) {

                        if (bitForByPass == 4) {
                            editor.putInt("bit", 0);
                            editor.putString("oldDate", newDate);
                            editor.putInt("bitForByPass", 1);
                            editor.putInt("bitForDistanceLimit", 0);
                            editor.commit();
                        } else {
                            bitForByPass++;
                            editor.putInt("bitForByPass", bitForByPass);
                            editor.commit();
                        }
                    }
                    if (mynumber == 5) {

                        if (bitForByPass == 5) {
                            editor.putInt("bit", 0);
                            editor.putString("oldDate", newDate);
                            editor.putInt("bitForByPass", 1);
                            editor.putInt("bitForDistanceLimit", 0);
                            editor.commit();
                        } else {
                            bitForByPass++;
                            editor.putInt("bitForByPass", bitForByPass);
                            editor.commit();
                        }
                    }

                    //  if (d<0.03){
                    editor.putInt("bit", 0);
                    editor.commit();
                    // }


                }


            } else {

                currentPlace = "";
            }


        }
    }


    //message sent for a location
    public static Map<String, Integer> SavedLocationMsgCount = new HashMap<>();


    public static void Notify(Context ctx, String contentTitle, String contentText) {
        Intent intent = new Intent(ctx, WelcomeScreenActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(ctx);

        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.fnfloc)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setDefaults(Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent)
                .setContentInfo("Info");


        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(rnd, b.build());
    }

    static int rnd = 0;
    public static boolean splashShown = false;

    /**
     * calculates the distance between two locations in KM
     */
    private static double distance(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c; // output distance, in KM
    }


    public static boolean IsOnline(Context ctx) {
        ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo info = connectivity.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            return true;
        } else {
            return false;
        }
    }


    //region CODE for saving location locally while offline
    private static List<UserLocation> listLocal = new ArrayList<>();

    public static void SaveLocationLocally(Context ctx, UserLocation loc) {
        listLocal.add(loc);
        Gson gson = new Gson();
        String json = gson.toJson(listLocal);//convert into json string-it is a data saving structure
        SaveToSharedPreference(ctx, "locationlist1", json);
    }

    public static List<UserLocation> GetLocallySavedLocations(Context ctx) {
        String str = getStringFromSharedPreference(ctx, "locationlist1");
        if (str.length() > 0) {
            Gson gson = new Gson();
            listLocal = gson.fromJson(str, new TypeToken<List<UserLocation>>() {
            }.getType());
            return listLocal;
        } else return new ArrayList<UserLocation>();
    }

    public static void ClearLocallySavedLocations(Context ctx) {
        listLocal = new ArrayList<>();

        Gson gson = new Gson();
        String json = gson.toJson(listLocal);
        SaveToSharedPreference(ctx, "locationlist1", json);
    }

    public static String GetFullAddress(Context applicationContext, LatLng latLng) {
        String address = "";

        if (latLng != null) {
            double lat = latLng.latitude;
            double lon = latLng.longitude;

            Geocoder geocoder = new Geocoder(applicationContext, new Locale("en"));
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
                    builder.append(returnAddress.getAdminArea() + ", ");
                    builder.append(returnAddress.getCountryName() + ", ");

                    address = builder.toString();

                    // Toast.makeText(getApplicationContext(), messageLocation, Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
            }
        }

        return address;
    }
    //endregion
}
