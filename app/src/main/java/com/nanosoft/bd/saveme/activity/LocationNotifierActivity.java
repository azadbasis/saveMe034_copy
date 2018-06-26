package com.nanosoft.bd.saveme.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.application.SApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Module.DirectionFinderListener;
import Module.Route;
import Module.UserLocation;

public class LocationNotifierActivity extends DashboardActivity implements DirectionFinderListener, OnMapReadyCallback {
    Spinner locList;
    LatLng selectedLocation;
    EditText etName;
    Button btnSaveLocation;
    Button btnDeleteLocation;
    private GoogleMap mMap;
    private TextView tvLatLng;
    private Spinner fnfspinner;
    private Toast toast;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    String item = "9";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_notifier);
        setTitleFromActivityLabel(R.id.title_text);

        sharedPreferences = getSharedPreferences("SaveData", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fnfspinner = (Spinner) findViewById(R.id.fnfspinner);



        int messageNumber = sharedPreferences.getInt("fnftime", 0);
        // String value = Operations.getStringFromSharedPreference(this, "fnftime");
        //   String value =String.valueOf(messageNumber) ;
        // int pos = 0;


        //for (int i = 0; i < fnfspinner.getCount(); i++) {
        //      item=fnfspinner.getItemAtPosition(messageNumber).toString();


        // }

//            if (item.equals(value)) {
//                pos = i;
//            }

        //   Toast.makeText(this, pos+"", Toast.LENGTH_SHORT).show();

        fnfspinner.setSelection(messageNumber - 1, true);

        fnfspinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //  item = parent.getItemAtPosition(position).toString();
                item = fnfspinner.getItemAtPosition(position).toString();


                if (item.equals("One sms/day")) {
                    item = "0";
                } else if (item.equals("Two sms/day")) {
                    item = "1";
                } else if (item.equals("Three sms/day")) {
                    item = "2";

                } else if (item.equals("Four sms/day")) {
                    item = "3";
                } else if (item.equals("Five sms/day")) {
                    item = "4";
                }

                String oldOldDate = sharedPreferences.getString("OldOldDate", "12/10/2016");
                editor.putInt("bit", 1);
                editor.putString("oldDate", oldOldDate);
                editor.putInt("bitForDistanceLimit", 1);
                editor.putInt("fnftime", Integer.parseInt(item) + 1);
                editor.commit();
                //  Toast.makeText(LocationNotifierActivity.this, ""+sharedPreferences.getInt("fnftime",5), Toast.LENGTH_SHORT).show();
                //  Operations.SaveToSharedPreference(getApplicationContext(), "fnftime", item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tvLatLng = ((TextView) findViewById(R.id.tvLatLng));
        locList = (Spinner) findViewById(R.id.lstLocations);
        locList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                try {


                    UserLocation ul = (UserLocation) locList.getItemAtPosition(position);

                    tvLatLng.setText(ul.Latitude + "," + ul.Longitue);



                    // etName.setText(ul.Name);
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions()
                            .position(ul.getLatLng())
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    );
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ul.getLatLng(), 12));
                } catch (Exception ex) {
                }
                ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        etName = (EditText) findViewById(R.id.etName);
        btnSaveLocation = (Button) findViewById(R.id.btnSaveLocation);
        btnSaveLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ePhoneNumber = Operations.getStringFromSharedPreference(LocationNotifierActivity.this, "vePhoneNumber");
                if (ePhoneNumber.length() > 0) {

                    UserLocation ul = new UserLocation();
                    ul.Name = etName.getText().toString();
                    ul.Name = ul.Name.trim();
                    if (ul.Name.length() > 0) {
                        ul.Longitue = selectedLocation.longitude;
                        ul.Latitude = selectedLocation.latitude;
                        Operations.SaveSelectedLocation(getApplicationContext(), ul);

                        tvLatLng.setText(null);
                        etName.setText(null);
                        PeopulateList();
                    } else {
                        Toast.makeText(LocationNotifierActivity.this, "Input is Blank", Toast.LENGTH_SHORT).show();
                    }
//                    Operations.getStringFromSharedPreference(getApplicationContext(), "fnftime");
                } else {


                    startActivity(new Intent(LocationNotifierActivity.this, EmergencyTextActivity.class));
                    showToast();

                }
            }
        });
        btnDeleteLocation = (Button) findViewById(R.id.btnDeleteLocation);
        btnDeleteLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               final UserLocation ul = (UserLocation) locList.getSelectedItem();

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LocationNotifierActivity.this);
                alertDialog.setTitle("Delete!!!");
                alertDialog.setMessage("Do you want to Delete this Location name?");
                alertDialog.setIcon(android.R.drawable.ic_dialog_alert);

                alertDialog.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                                Operations.Delete(getApplicationContext(), ul);
                                PeopulateList();
                            }
                        });
                alertDialog.show();

            }
        });

        PeopulateList();
        etName.setText("");
    }


    private String getAddress(double latitude, double longitude){

        Geocoder geocoder;
        List<Address> addresses = new ArrayList<>();

        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e) {
            e.printStackTrace();
        }

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName();



        return address;
    }


    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> route) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
//        mMap.clear();
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        double lat = 0, lon = 0;
        if (SApplication.LOCATION != null) {
            lat = SApplication.LOCATION.getLatitude();
            lon = SApplication.LOCATION.getLongitude();
        }
        LatLng curentLocation = new LatLng(lat, lon);
        selectedLocation = curentLocation;
        tvLatLng.setText(lat + "," + lon);
       // tvLatLng.setText(Operations.GetFullAddress(getApplicationContext()));

       // Toast.makeText(this, getAddress(lat,lon), Toast.LENGTH_SHORT).show();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curentLocation, 17f));
        mMap.addMarker(new MarkerOptions()
                .position(curentLocation)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        );
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onMapClick(LatLng latLng) {
                tvLatLng.setText(latLng.latitude + "," + latLng.longitude);
              //  tvLatLng.setText(Operations.GetFullAddress(getApplicationContext(),latLng));
               // Toast.makeText(LocationNotifierActivity.this, getAddress(latLng.latitude,latLng.longitude), Toast.LENGTH_SHORT).show();

                mMap.clear();
                Marker m = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                );
                selectedLocation = latLng;
            }
        });
    }

    private void PeopulateList() {
        List<UserLocation> lst = Operations.GetSavedLocations(this);
        ArrayAdapter<UserLocation> adapter = new ArrayAdapter<UserLocation>(this, android.R.layout.simple_list_item_1, lst);
        locList.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        animation(R.id.custom_toast_layout_id);

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


    @Override
    protected void onResume() {
        super.onResume();


    }

    public void showToast() {


        int toastDurationInMilliSeconds = 10000;
        toast = Toast.makeText(this, " OPPS!!! Setup First Emergency Number, \n To SMS FnF Location", Toast.LENGTH_LONG);
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000) {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }

            public void onFinish() {
                toast.cancel();
            }
        };

        View toastView = toast.getView();
        TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
        toastMessage.setTextSize(15);
        toastMessage.setTextColor(Color.WHITE);
        toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.emgtextcircle, 0, 0, 0);
        toastMessage.setGravity(Gravity.CENTER);
        toastMessage.setCompoundDrawablePadding(10);
        toastView.setBackgroundColor(Color.BLACK);
        toast.show();
        toastCountDown.start();

    }

}