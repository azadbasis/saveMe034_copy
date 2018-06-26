package com.nanosoft.bd.saveme.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.SmsManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.database.DatabaseManager;
import com.nanosoft.bd.saveme.database.DatabaseModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import Module.DirectionFinderListener;
import Module.Route;

public class FnFTrackingActivity extends DashboardActivity implements DirectionFinderListener, OnMapReadyCallback {


    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin, etFnFNumber;
    private EditText etDestination;
    private Spinner fnfPersonsp;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;
    ConnectivityManager connectivity;
    RequestQueue requestQueue;
    private BroadcastReceiver mIntentReceiver;
    String responsefnf = "0";


    double lat = 0;
    double lng = 0;
    LatLng ll = null;

    int spinnerPosition;
    int bitForRemoveBtn = 0;

    ArrayList<DatabaseModel> fnfList;
    DatabaseManager manager;

    String fnfNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fnf_tracking);
        setTitleFromActivityLabel(R.id.title_text);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        fnfPersonsp = (Spinner) findViewById(R.id.fnfPersonsp);
        etFnFNumber = (EditText) findViewById(R.id.etFnFNumber);

        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);
        connectivity = (ConnectivityManager) this.getSystemService(CONNECTIVITY_SERVICE);
        requestQueue = Volley.newRequestQueue(this);

        Points = new ArrayList<>();
        bounds = LatLngBounds.builder();
        // PeopulateUserList();

//        fnfList = new ArrayList<>();
//        manager = new DatabaseManager(this);

        showFnfInSpinner();


        fnfPersonsp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                bitForRemoveBtn = 1;
                spinnerPosition = position;
                // Toast.makeText(FnFTrackingActivity.this, "here is location id "+spinnerPosition, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    @Override
    public void onBackPressed() {
        animation(R.id.fnfTrackingLayout);

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


    public void showFnfInSpinner() {


        try {
            fnfList = new ArrayList<>();
            manager = new DatabaseManager(this);
            fnfList = manager.getAllInfo();//
            String[] list = new String[fnfList.size()];

            for (int i = 0; i < fnfList.size(); i++) {
                int id = fnfList.get(i).getFnfNumberId();
                //Toast.makeText(this, "showFnfInSpinner "+id, Toast.LENGTH_SHORT).show();
                String fnfNumber = manager.getFnfInfo(id).getFnfNumber();
                list[i] = fnfNumber;
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);
            fnfPersonsp.setAdapter(adapter);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    String[] phones;
    String location = "";

    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {

    }



    //String url = "http://smartcampus.com.bd/contactmaster/test/GetLocation.php?userPhone=";
    String url = "http://saveme.com.bd/contactmaster/test/GetLocation.php?userPhone=";
    List<LatLng> Points;
    LatLngBounds.Builder bounds;

    private void GetLocation(final String userPhone) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);

// Request a string response from the provided URL.

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + userPhone,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        //  Toast.makeText(FnFTrackingActivity.this, response, Toast.LENGTH_SHORT).show();

                        responsefnf = response;
                        countResponse++;

                        if (response.equals("0")) {

                        } else {
                            AddOriginMarker(response, userPhone);
                            if (countPhones == countResponse) SetCenter(); //drowPolyLine();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                location = error.getMessage();
                finish();
                Toast.makeText(getApplicationContext(), "Slow net connection", Toast.LENGTH_SHORT).show();
            }


        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    private void SetCenter() {
        LatLngBounds b = bounds.build();
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(b, 50));
    }

    private void drowPolyLine() {
        mMap.addPolyline(po);
    }


    PolylineOptions po;
    int countPhones = 0, countResponse = 0;

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        LatLng nanosoft = new LatLng(23.787784, 90.425591);//Dhaka, Bangladesh
        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nanosoft, 8));
        po = new PolylineOptions()
                .width(10)
                .color(Color.RED);



        String phn1 = Operations.getStringFromSharedPreference(getApplicationContext(), "fnf1");
        // Toast.makeText(this, "user phone "+phn1, Toast.LENGTH_SHORT).show();


//        phones = new String[]{
//                /*Operations.getStringFromSharedPreference(this, "etfirstPhone"),
//                Operations.getStringFromSharedPreference(this, "etsecondPhone"),
//                Operations.getStringFromSharedPreference(this, "etthirdPhone"),
//                Operations.getStringFromSharedPreference(this, "etfourthPhone"),
//                Operations.getStringFromSharedPreference(this, "etfifthPhone"),*/
//              phn1
//
//        };

        fnfList = new ArrayList<>();
        DatabaseManager manager = new DatabaseManager(this);
        fnfList = manager.getAllInfo();
        phones = new String[fnfList.size()];

        for (int i = 0; i < fnfList.size(); i++) {
            int id = fnfList.get(i).getFnfNumberId();
            String fnfNumber = manager.getFnfInfo(id).getFnfNumber();
            phones[i] = fnfNumber;
        }


        for (final String phone : phones) {

            if (phone.length() > 1) {
                GetLocation(phone);
                countPhones++;
            }


        }

        mMap.setMyLocationEnabled(true);
        if (responsefnf.equals("0")) {

        } else {

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }


        }


    }


    void AddOriginMarker(String loc, String phone) {
        //  Cursor cursor=null;
        //String[] parts = new String[cursor.getCount()];
        String[] parts = loc.split(";");

       /* double lat = 0;
        double lng = 0;
        LatLng ll = null;*/
        String date = null;

        lat = Double.parseDouble(parts[0]);
        lng = Double.parseDouble(parts[1]);
        ll = new LatLng(lat, lng);
        date = parts[2];
        po.add(ll);


        originMarkers.add(mMap.addMarker(new MarkerOptions()
                .title(phone + ", " + date + ", " + Operations.getSpecificLocation(this, lat, lng))
                .position(ll)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.fnfloc))
        ));
        Points.add(new LatLng(lat, lng));
        bounds.include(ll);

    }


    public void btnDeleteFnF(View view) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FnFTrackingActivity.this);
        alertDialog.setTitle("Remove");
        alertDialog.setMessage("Do you want to Remove your FnF number?");

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if (bitForRemoveBtn == 1) {

                            //DatabaseManager manager = new DatabaseManager(this);
                            // int id = manager.getFnfInfo(spinnerPosition + 1).getFnfNumberId();
                            manager = new DatabaseManager(FnFTrackingActivity.this);
                            fnfList = new ArrayList<>();
                            fnfList = manager.getAllInfo();

                            int id = fnfList.get(spinnerPosition).getFnfNumberId();

                            //  Toast.makeText(this, "" + id, Toast.LENGTH_SHORT).show();
                            manager.deleteFnf(id);
                            showFnfInSpinner();
                            // String number = manager.getFnfInfo(id).getFnfNumber();
                            Toast.makeText(FnFTrackingActivity.this, "Deleted!!!", Toast.LENGTH_SHORT).show();
                            bitForRemoveBtn = 0;

                        } else {
                            Toast.makeText(FnFTrackingActivity.this, "Select your position first from spinner", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        alertDialog.show();


        //  manager.deleteFnf(Id);
    }

    public void addFnFbtn(View view) {


        fnfNumber = etFnFNumber.getText().toString();

        if (fnfNumber.length() > 0) {

            checkUser(FnFTrackingActivity.this);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    etFnFNumber.setText("");
                    if (responseFnfForValidUser.equals("1") || responseFnfForValidUser.equals("2")) {

                        int fnfrandom = (int) (Math.random() * 1000 + 100);
                        Operations.SaveToSharedPreference(getApplicationContext(), "fnfrandom", String.valueOf(fnfrandom));

                        if (fnfrandom > 0) {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(fnfNumber, null, String.valueOf(fnfrandom), null, null);
                            Toast.makeText(FnFTrackingActivity.this, "Code Delivered", Toast.LENGTH_SHORT).show();
                            Operations.SaveToSharedPreference(getApplicationContext(), "fnfNumber", fnfNumber);
                        }

                    } else {
                        Toast.makeText(FnFTrackingActivity.this, "This user is not registered", Toast.LENGTH_SHORT).show();
                    }


                }
            }, 4000);


        }
    }

    static String responseFnfForValidUser;

    public void checkUser(final Context ctx) {

        ConnectivityManager connectivity = (ConnectivityManager) ctx.getSystemService(CONNECTIVITY_SERVICE);
        RequestQueue requestQueue = Volley.newRequestQueue(ctx);

        final String userPhone = fnfNumber;
        //  String insertUrl = "http://smartcampus.com.bd/contactmaster/checkUser.php";
        String insertUrl = "http://saveme.com.bd/contactmaster/checkUser.php";
        StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // strRes = response;
                try {
                    // strRes = response;
                    responseFnfForValidUser = response;


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
