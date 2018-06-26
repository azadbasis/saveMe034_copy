package com.nanosoft.bd.saveme.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.maps.GetNearbyPlacesData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Module.DirectionFinder;
import Module.DirectionFinderListener;
import Module.Route;

import static com.nanosoft.bd.saveme.R.id.map;
import static com.nanosoft.bd.saveme.R.id.tvDistance;
import static com.nanosoft.bd.saveme.R.id.tvDistancePath;

public class MapDirectionActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DirectionFinderListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Toolbar toolbar;
    NavigationView navigationView;


    private GoogleMap mMap;
    private Button btnFindPath;
    private EditText etOrigin;
    private EditText etDestination;
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ProgressDialog progressDialog;

    private CheckBox mWaitForMapLoadCheckBox;

    LinearLayout mapDirectionLo, mapCustomizationLo, snapshotLayout;
    Spinner customerSelectionSp;


    //  private GoogleMap mMap;
    double latitude;
    double longitude;
    private int PROXIMITY_RADIUS = 500;
    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    Marker mCurrLocationMarker;
    LocationRequest mLocationRequest;


    String placeName;
    Spinner distanceSelectionSp;

    int bitForMarkerLongClick = 2;

    TextView commandTv;
    LinearLayout streetViewLayout;
    SupportMapFragment streetMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_direction);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Location Path");
        floatingDrawerMenu();
        buildGoogleApiClient();
        initialise();
        onClickListener();
        if (!isGPSEnabled(this)) {
            Toast.makeText(MapDirectionActivity.this, "Please Turn on GPS", Toast.LENGTH_LONG).show();
        }


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



    public boolean isGPSEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    private void onClickListener() {

        btnFindPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest();
            }
        });

        customerSelectionSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String placeSelection = customerSelectionSp.getItemAtPosition(position).toString();

                if (!placeSelection.equals("Select")) {
                    Toast.makeText(MapDirectionActivity.this, "Nearby " + placeSelection, Toast.LENGTH_SHORT).show();
                }

                if (placeSelection.equals("Hospital")) {
                    placeSelection = "hospital";
                } else if (placeSelection.equals("Bank")) {
                    placeSelection = "bank";
                } else if (placeSelection.equals("Bus Station")) {
                    placeSelection = "bus_station";
                } else if (placeSelection.equals("Shopping Mall")) {
                    placeSelection = "shopping_mall";
                } else if (placeSelection.equals("School")) {
                    placeSelection = "school";
                } else if (placeSelection.equals("Restaurant")) {
                    placeSelection = "restaurant";
                } else if (placeSelection.equals("ATM Booth")) {
                    placeSelection = "atm";
                } else if (placeSelection.equals("Mosque")) {
                    placeSelection = "mosque";
                } else if (placeSelection.equals("Police Station")) {
                    placeSelection = "police";
                } else if (placeSelection.equals("University")) {
                    placeSelection = "university";
                }

                if (placeSelection.equals("Select")) {
                    mMap.clear();
                } else {
                    setNearByPlace(placeSelection);
                }

                placeName = placeSelection;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        distanceSelectionSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String distanceSelection = distanceSelectionSp.getItemAtPosition(position).toString();


                if (!placeName.equals("Select")) {

                    if (distanceSelection.equals("Very Near")) {
                        PROXIMITY_RADIUS = 500;
                        setNearByPlace(placeName);
                    } else if (distanceSelection.equals("Near")) {
                        PROXIMITY_RADIUS = 1000;
                        setNearByPlace(placeName);
                    } else if (distanceSelection.equals("Average")) {
                        PROXIMITY_RADIUS = 1500;
                        setNearByPlace(placeName);
                    } else if (distanceSelection.equals("Far")) {
                        PROXIMITY_RADIUS = 2000;
                        setNearByPlace(placeName);
                    } else if (distanceSelection.equals("So Far")) {
                        PROXIMITY_RADIUS = 10000;
                        setNearByPlace(placeName);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }

    SupportMapFragment mapFragment;


    private void initialise() {


        //Check if Google Play Services Available or not
        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");
        }


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

//        streetMap = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.streetMap);
//        streetMap.getMapAsync(this);

        btnFindPath = (Button) findViewById(R.id.btnFindPath);
        etOrigin = (EditText) findViewById(R.id.etOrigin);
        etDestination = (EditText) findViewById(R.id.etDestination);
        mapDirectionLo = (LinearLayout) findViewById(R.id.mapDirectionLo);
        snapshotLayout = (LinearLayout) findViewById(R.id.snapshotLayout);
        mapCustomizationLo = (LinearLayout) findViewById(R.id.mapCustomizationLo);
        customerSelectionSp = (Spinner) findViewById(R.id.customerSelectionSp);
        distanceSelectionSp = (Spinner) findViewById(R.id.distanceSelectionSp);
        mWaitForMapLoadCheckBox = (CheckBox) findViewById(R.id.wait_for_map_load);
        commandTv = (TextView) findViewById(R.id.commandTv);


        streetViewLayout = (LinearLayout) findViewById(R.id.streetViewLayout);
        streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetviewpanorama);


    }

    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }


    private void floatingDrawerMenu() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        checkableDrawerMenu();


    }


    private void sendRequest() {
        mMap.clear();
        String origin = etOrigin.getText().toString();
        if (origin.length() == 0) {
            origin = Operations.GetFullAddress(getApplicationContext());
            etOrigin.setText(origin);
        }

        String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void getLocationPath(String origin, String destination) {
        mMap.clear();
        // String origin = etOrigin.getText().toString();
        //String destination = etDestination.getText().toString();
        if (origin.isEmpty()) {
            Toast.makeText(this, "Please enter origin address!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (destination.isEmpty()) {
            Toast.makeText(this, "Please enter destination address!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            new DirectionFinder(this, origin, destination).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    LatLng latlngs;
    String placeTitle;
    String destination1;
    String destination2;


    // int bitForSetFeatureAndMapType;
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        new Handler().postDelayed(new Runnable() {
            public void run() {

                if (mLocation != null) {
                    LatLng latlng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());//Dhaka, Bangladesh

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
                    mMap.addMarker(new MarkerOptions()
                            .title("Current Location")
                            .position(latlng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    );
                }
            }
        }, 1500);
        //  mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.setMapType(setMap());


        setFeatureAndMapType();


        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {
                latlngs = marker.getPosition();
/*marker animation*/
                final Handler handler = new Handler();
                final long start = SystemClock.uptimeMillis();
                final long duration = 1500;

                final Interpolator interpolator = new BounceInterpolator();

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        long elapsed = SystemClock.uptimeMillis() - start;
                        float t = Math.max(
                                1 - interpolator.getInterpolation((float) elapsed / duration), 0);
                        marker.setAnchor(0.5f, 1.0f + 2 * t);

                        if (t > 0.0) {
                            // Post again 16ms later.
                            handler.postDelayed(this, 16);
                        }
                    }
                });


                float zIndex = marker.getZIndex() + 1.0f;
                marker.setZIndex(zIndex);
/*marker animation */
                //marker.setIcon(BitmapDescriptorFactory.defaultMarker(23));
                // mapFragment.getView().setVisibility(View.GONE);
                //streetView(latlngs);
                //marker.setSnippet("azhar");
                placetitle = marker.getTitle();
                bitForMarkerLongClick = 1;

                return false;
            }
        });


        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {

                mMap.clear();

                if (bitForMarkerLongClick == 1) {

                    destination1 = Operations.GetFullAddress(getApplicationContext(), latlngs);

                    placeTitle = destination1;

                    //getLocationPath(GetFullAddress(getApplicationContext()), destination);
                    getLocationPath(Operations.GetFullAddress(getApplicationContext()), latlngs.latitude + "," + latlngs.longitude);

                    // Toast.makeText(MapDirectionActivity.this, Operations.GetFullAddress(getApplicationContext()), Toast.LENGTH_SHORT).show();

                    bitForMarkerLongClick = 0;

                }
                if (bitForMarkerLongClick == 2) {

                    destination2 = Operations.GetFullAddress(getApplicationContext(), latLng);

                    placeTitle = destination2;
                    getLocationPath(Operations.GetFullAddress(getApplicationContext()), latLng.latitude + "," + latLng.longitude);
                    // Toast.makeText(MapDirectionActivity.this, Operations.GetFullAddress(getApplicationContext()), Toast.LENGTH_SHORT).show();

                }

            }

        });


        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (bitForStreetView == 1) {
                    mMap.clear();
                    mMap.addMarker(new MarkerOptions()
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    );
                    streetView(latLng);

                    String latlng = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
                    getLocationPath(Operations.GetFullAddress(MapDirectionActivity.this), latlng);

                }

            }
        });


        //setMarker();
//Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        } else {
            // buildGoogleApiClient();
            mMap.setMyLocationEnabled(true);
        }


    }


    private void setStreetViewAndMarker() {

        mMap.clear();
        new Handler().postDelayed(new Runnable() {
            public void run() {

                if (mLocation != null) {
                    LatLng latlng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());//Dhaka, Bangladesh

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 14));
                    mMap.addMarker(new MarkerOptions()
                            .title("Current Location")
                            .position(latlng)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    );

                    streetView(latlng);
                }
            }
        }, 1500);


    }


    int bitForStreetView = 0;
    private StreetViewPanorama mStreetViewPanorama;

    SupportStreetViewPanoramaFragment streetViewPanoramaFragment;

    private void streetView(final LatLng latLng) {

        streetViewPanoramaFragment.getStreetViewPanoramaAsync(
                new OnStreetViewPanoramaReadyCallback() {
                    @Override
                    public void onStreetViewPanoramaReady(StreetViewPanorama panorama) {
                        mStreetViewPanorama = panorama;
                        // Only set the panorama to SYDNEY on startup (when no panoramas have been
                        // loaded which is when the savedInstanceState is null).
                        //if (savedInstanceState == null) {
                        mStreetViewPanorama.setPosition(latLng);
                        //}
                    }
                });


    }


    private int setMap() {
        int maptype = GoogleMap.MAP_TYPE_NORMAL;
        return maptype;
    }

    String url;
    String placetitle;

    public void setNearByPlace(final String placeName) {

        ConnectivityManager connect = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connect.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connect.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED) {
//abc@sdfsdf.ssdf
//abc@gdfgfd.ssdf

            Log.d("onClick", "Button is Clicked");
            mMap.clear();
            url = getUrl(latitude, longitude, placeName);
            Object[] DataTransfer = new Object[2];
            DataTransfer[0] = mMap;
            DataTransfer[1] = url;
            Log.d("onClick", url);
            GetNearbyPlacesData getNearbyPlacesData = new GetNearbyPlacesData();
            getNearbyPlacesData.execute(DataTransfer);
            // Toast.makeText(MapDirectionActivity.this,"Nearby "+placeName, Toast.LENGTH_LONG).show();


        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }


    }


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private String getUrl(double latitude, double longitude, String nearbyPlace) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + PROXIMITY_RADIUS);
        googlePlacesUrl.append("&type=" + nearbyPlace);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + "AIzaSyATuUiZUkEc_UgHuqsBJa1oqaODI-3mLs0");
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }


    @Override
    public void onDirectionFinderStart() {
        progressDialog = ProgressDialog.show(this, "Please wait.",
                "Finding direction..!", true);

        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        progressDialog.dismiss();
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();


        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 14));
            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            ((TextView) findViewById(tvDistance)).setText((route.distance).text);

            int pathrace = (route.distance).value;
            pathrace /= 100;
            ((TextView) findViewById(tvDistancePath)).setText(String.valueOf(pathrace) + " mins");


            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    // .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                    // .title(route.startAddress)
                    .title(Operations.GetFullAddress(getApplicationContext()))
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    //.title(route.endAddress)
                    .title(placeTitle)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.CYAN).
                    width(5);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }


    private void setNullInTv() {
        ((TextView) findViewById(tvDistance)).setText("0 km");
        ((TextView) findViewById(R.id.tvDuration)).setText("0 mins");
        ((TextView) findViewById(tvDistancePath)).setText("0 mins");

    }


//    @Override
//    public void onDirectionFinderSuccess(List<Route> routes) {
//        progressDialog.dismiss();
//        polylinePaths = new ArrayList<>();
//        originMarkers = new ArrayList<>();
//        destinationMarkers = new ArrayList<>();
//
//        for (Route route : routes) {
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 14));
//            ((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
//            ((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
//
//            originMarkers.add(mMap.addMarker(new MarkerOptions()
//                    // .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue))
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
//                    // .title(route.startAddress)
//                    .title(Operations.GetFullAddress(getApplicationContext()))
//                    .position(route.startLocation)));
//            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
//                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//                    //.title(route.endAddress)
//                    .title(placeTitle)
//                    .position(route.endLocation)));
//
//            PolylineOptions polylineOptions = new PolylineOptions().
//                    geodesic(true).
//                    color(Color.CYAN).
//                    width(5);
//
//            for (int i = 0; i < route.points.size(); i++)
//                polylineOptions.add(route.points.get(i));
//
//            polylinePaths.add(mMap.addPolyline(polylineOptions));
//        }
//    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            animation(R.id.drawer_layout);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    finish();


                }
            }, 700);
            //super.onBackPressed();
//            Operations.IntSaveToSharedPreference(getApplicationContext(),"featureMenuNumber",0);
//            Operations.IntSaveToSharedPreference(getApplicationContext(),"mapTypeMenuNumber",0);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map_direction, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            return true;
        }
        if (id == R.id.action_home) {
            final Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        if (id == R.id.action_help) {
            String url = "http://saveme.com.bd/#guide";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
            return true;
        }
        if (id == R.id.action_payment) {
            startActivity(new Intent(getApplicationContext(), AppPurchaseActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        //MapStyleOptions style;


        int id = item.getItemId();

        if (id == R.id.nav_map_direction) {
            mapDirectionLo.setVisibility(View.VISIBLE);
            mapCustomizationLo.setVisibility(View.GONE);
            snapshotLayout.setVisibility(View.GONE);
            mapFragment.getView().setVisibility(View.VISIBLE);
            streetViewPanoramaFragment.getView().setVisibility(View.GONE);
            btnFindPath.setVisibility(View.VISIBLE);
            setTitle("Location Path");
            bitForMarkerLongClick = 2;
            mMap.clear();
            bitForStreetView = 0;
            Operations.IntSaveToSharedPreference(getApplicationContext(), "featureMenuNumber", 0);
            commandTv.setVisibility(View.VISIBLE);
            commandTv.setText("• Long click on your map to see path");
            setNullInTv();
            checkableDrawerMenu();


        } else if (id == R.id.nav_map_customization) {

            mapDirectionLo.setVisibility(View.GONE);
            mapCustomizationLo.setVisibility(View.VISIBLE);
            snapshotLayout.setVisibility(View.GONE);
            mapFragment.getView().setVisibility(View.VISIBLE);
            streetViewPanoramaFragment.getView().setVisibility(View.GONE);
            bitForMarkerLongClick = 0;
            btnFindPath.setVisibility(View.GONE);
            setTitle("Find Place");
            setNullInTv();
            mMap.clear();
            bitForStreetView = 0;
            Operations.IntSaveToSharedPreference(getApplicationContext(), "featureMenuNumber", 1);
            commandTv.setVisibility(View.VISIBLE);
            commandTv.setText("• First click marker then Long click on your map to see path");
            checkableDrawerMenu();


        } else if (id == R.id.nav_street_view) {

            mapDirectionLo.setVisibility(View.GONE);
            mapCustomizationLo.setVisibility(View.GONE);
            snapshotLayout.setVisibility(View.GONE);
            //  mapFragment.getView().setVisibility(View.VISIBLE);
            streetViewPanoramaFragment.getView().setVisibility(View.VISIBLE);
            btnFindPath.setVisibility(View.GONE);
            bitForMarkerLongClick = 0;
            setTitle("Street View");
            mMap.clear();
            setNullInTv();
            bitForStreetView = 1;
            Operations.IntSaveToSharedPreference(getApplicationContext(), "featureMenuNumber", 2);
            commandTv.setVisibility(View.VISIBLE);
            commandTv.setText("• Click on your map to see path & street view");
            checkableDrawerMenu();
            setStreetViewAndMarker();

        } else if (id == R.id.nav_map_snapshot) {
            storagePermission();

        } else if (id == R.id.nav_map_normal) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            Toast.makeText(this, "Map Type Normal", Toast.LENGTH_SHORT).show();
            Operations.IntSaveToSharedPreference(getApplicationContext(), "mapTypeMenuNumber", 0);
            checkableDrawerMenu();
        } else if (id == R.id.nav_map_satellite) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            Toast.makeText(this, "Map Type Satellite", Toast.LENGTH_SHORT).show();
            Operations.IntSaveToSharedPreference(getApplicationContext(), "mapTypeMenuNumber", 1);
            checkableDrawerMenu();

        } else if (id == R.id.nav_map_hybrid) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            Toast.makeText(this, "Map Type Hybrid", Toast.LENGTH_SHORT).show();
            Operations.IntSaveToSharedPreference(getApplicationContext(), "mapTypeMenuNumber", 2);
            checkableDrawerMenu();
        } else if (id == R.id.nav_map_terrain) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            Toast.makeText(this, "Map Type Terrain", Toast.LENGTH_SHORT).show();
            Operations.IntSaveToSharedPreference(getApplicationContext(), "mapTypeMenuNumber", 3);
            checkableDrawerMenu();
        } else if (id == R.id.nav_map_none) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
            Toast.makeText(this, "Map Type None", Toast.LENGTH_SHORT).show();
            Operations.IntSaveToSharedPreference(getApplicationContext(), "mapTypeMenuNumber", 4);
            checkableDrawerMenu();

        } else if (id == R.id.nav_mapstyle_retro) {
            //style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_retro);
            Toast.makeText(this, "Map Type Metro", Toast.LENGTH_SHORT).show();
            Operations.IntSaveToSharedPreference(getApplicationContext(), "mapTypeMenuNumber", 5);
            checkableDrawerMenu();

        } else if (id == R.id.nav_mapstyle_night) {
            // style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
            Toast.makeText(this, "Map Type Night", Toast.LENGTH_SHORT).show();
            Operations.IntSaveToSharedPreference(getApplicationContext(), "mapTypeMenuNumber", 6);
            checkableDrawerMenu();

        } else if (id == R.id.nav_mapstyle_grayscale) {
            //style = MapStyleOptions.loadRawResourceStyle(this, R.raw.mapstyle_night);
            Toast.makeText(this, "Map Type Grayscale", Toast.LENGTH_SHORT).show();
            Operations.IntSaveToSharedPreference(getApplicationContext(), "mapTypeMenuNumber", 7);
            checkableDrawerMenu();

        } else if (id == R.id.nav_mapstyle_transit) {
            //style = MapStyleOptions.loadRawResourceStyle(this, R.raw.m);
            Toast.makeText(this, "Map Type Transit", Toast.LENGTH_SHORT).show();
            Operations.IntSaveToSharedPreference(getApplicationContext(), "mapTypeMenuNumber", 8);
            checkableDrawerMenu();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    int featureMenuNumber;
    int mapTypeMenuNumber;

    private void checkableDrawerMenu() {

        featureMenuNumber = Operations.getIntegerSharedPreference(getApplicationContext(), "featureMenuNumber", 0);
        mapTypeMenuNumber = Operations.getIntegerSharedPreference(getApplicationContext(), "mapTypeMenuNumber", 0);

        navigationView.getMenu().getItem(0).getSubMenu().getItem(0).setChecked(false);
        navigationView.getMenu().getItem(0).getSubMenu().getItem(1).setChecked(false);
        navigationView.getMenu().getItem(0).getSubMenu().getItem(2).setChecked(false);
        navigationView.getMenu().getItem(0).getSubMenu().getItem(3).setChecked(false);
        navigationView.getMenu().getItem(1).getSubMenu().getItem(0).setChecked(false);
        navigationView.getMenu().getItem(1).getSubMenu().getItem(1).setChecked(false);
        navigationView.getMenu().getItem(1).getSubMenu().getItem(2).setChecked(false);
        navigationView.getMenu().getItem(1).getSubMenu().getItem(3).setChecked(false);
        navigationView.getMenu().getItem(1).getSubMenu().getItem(4).setChecked(false);

        navigationView.getMenu().getItem(0).getSubMenu().getItem(featureMenuNumber).setChecked(true);

        navigationView.getMenu().getItem(1).getSubMenu().getItem(mapTypeMenuNumber).setChecked(true);


    }

    private void setFeatureAndMapType() {

        if (featureMenuNumber == 0) {
            mapDirectionLo.setVisibility(View.VISIBLE);
            mapCustomizationLo.setVisibility(View.GONE);
            snapshotLayout.setVisibility(View.GONE);
            streetViewPanoramaFragment.getView().setVisibility(View.GONE);
            btnFindPath.setVisibility(View.VISIBLE);
            setTitle("Location Path");
            commandTv.setVisibility(View.VISIBLE);
            commandTv.setText("• Long click on your map to see path");
            bitForMarkerLongClick = 2;
            //setStreetViewAndMarker();
        } else if (featureMenuNumber == 1) {
            snapshotLayout.setVisibility(View.GONE);
            mapDirectionLo.setVisibility(View.GONE);
            mapCustomizationLo.setVisibility(View.VISIBLE);
            btnFindPath.setVisibility(View.GONE);
            streetViewPanoramaFragment.getView().setVisibility(View.GONE);
            bitForMarkerLongClick = 0;
            setTitle("Find Place");
            // setStreetViewAndMarker();





            commandTv.setText("• First click marker then Long click on your map to see path");
        } else if (featureMenuNumber == 2) {
            snapshotLayout.setVisibility(View.GONE);
            mapDirectionLo.setVisibility(View.GONE);
            btnFindPath.setVisibility(View.GONE);
            mapCustomizationLo.setVisibility(View.GONE);

            // streetViewPanoramaFragment.getView().setVisibility(View.VISIBLE);
            //  mapFragment.getView().setVisibility(View.VISIBLE);
            // streetViewLayout.setVisibility(View.VISIBLE);

            bitForMarkerLongClick = 0;
            setTitle("Street View");
            commandTv.setVisibility(View.VISIBLE);
            commandTv.setText("• Click on your map to see path & street view");
            mMap.clear();

            bitForStreetView = 1;
            bitForMarkerLongClick = 0;
            setStreetViewAndMarker();


        } else if (featureMenuNumber == 3) {

            storagePermission();


          /*  snapshotLayout.setVisibility(View.VISIBLE);
            mapDirectionLo.setVisibility(View.GONE);
            mapCustomizationLo.setVisibility(View.GONE);
            btnFindPath.setVisibility(View.GONE);
            streetViewPanoramaFragment.getView().setVisibility(View.GONE);
            bitForMarkerLongClick=2;
            commandTv.setVisibility(View.GONE);
            setTitle("Take Snapshot");
            setStreetViewAndMarker();*/

        }
        if (mapTypeMenuNumber == 0) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        } else if (mapTypeMenuNumber == 1) {
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        } else if (mapTypeMenuNumber == 2) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        } else if (mapTypeMenuNumber == 3) {
            mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        } else if (mapTypeMenuNumber == 4) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
        }


    }

    private int REQUEST_CODE_FOR_STORAGE = 101;


    private void storagePermission() {


        if (Build.VERSION.SDK_INT > 22) {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_CODE_FOR_STORAGE);

                // ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);


                return;
            } else {
                featureMenuNumber3Execution();

            }

        } else {
            featureMenuNumber3Execution();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (REQUEST_CODE_FOR_STORAGE) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    featureMenuNumber3Execution();
                } else {
                    featureMenuNumber3Execution();
                    Toast.makeText(this, "You have to allow this permission to save snapshot", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void featureMenuNumber3Execution() {

        snapshotLayout.setVisibility(View.VISIBLE);
        mapDirectionLo.setVisibility(View.GONE);
        mapCustomizationLo.setVisibility(View.GONE);
        //  mapFragment.getView().setVisibility(View.VISIBLE);
        streetViewPanoramaFragment.getView().setVisibility(View.GONE);
        btnFindPath.setVisibility(View.GONE);
        bitForMarkerLongClick = 2;
        setTitle("Take Snapshot");
        mMap.clear();
        setNullInTv();
        bitForStreetView = 0;
        Operations.IntSaveToSharedPreference(getApplicationContext(), "featureMenuNumber", 3);
        commandTv.setVisibility(View.GONE);
        checkableDrawerMenu();
        // setStreetViewAndMarker();

    }


    Location mLocation;

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        mLocation = mLastLocation;

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            if (mLastLocation != null && featureMenuNumber == 1) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }


        }

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {


        Log.d("onLocationChanged", "entered");

        mLastLocation = location;
        if (mCurrLocationMarker != null) {
            mCurrLocationMarker.remove();
        }

        //Place current location marker
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title("Current Position");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA));
        mCurrLocationMarker = mMap.addMarker(markerOptions);
        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        Toast.makeText(MapDirectionActivity.this, "Your Current Location", Toast.LENGTH_SHORT).show();

        Log.d("onLocationChanged", String.format("latitude:%.3f longitude:%.3f", latitude, longitude));

        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            Log.d("onLocationChanged", "Removing Location Updates");
        }
        Log.d("onLocationChanged", "Exit");


    }

    /*SNAPSHOT OPERATION*/
    public void onScreenshot(View view) {
        takeSnapshot();
    }

    private void takeSnapshot() {
        if (mMap == null) {
            return;
        }

        final ImageView snapshotHolder = (ImageView) findViewById(R.id.snapshot_holder);

        final GoogleMap.SnapshotReadyCallback callback = new GoogleMap.SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // Callback is called from the main thread, so we can modify the ImageView safely.
                snapshotHolder.setImageBitmap(snapshot);
                saveSnapshot(snapshot);
            }
        };

        if (mWaitForMapLoadCheckBox.isChecked()) {
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    mMap.snapshot(callback);
                }
            });
        } else {
            mMap.snapshot(callback);
        }
    }

    /**
     * Called when the clear button is clicked.
     */
    public void onClearScreenshot(View view) {
        ImageView snapshotHolder = (ImageView) findViewById(R.id.snapshot_holder);
        snapshotHolder.setImageDrawable(null);
    }





/*FOR SNAPSHOT */

    private void saveSnapshot(Bitmap thumbnail) {


        // Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        //thumbnail.copyPixelsFromBuffer(Bitmap.CompressFormat.JPEG);


        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "/SaveMe/Image/");
        if (!directory.exists()) {
            directory.mkdirs();

        }

        String imageName = "";

        if (imageName.length() < 2) {
            imageName = String.valueOf(System.currentTimeMillis());
        }

        File destination = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/SaveMe/Image/"
                + File.separator
                + imageName + ".jpg");
        // System.currentTimeMillis();
        //Toast.makeText(AddMedicalHistoryActivity.this, Environment.getExternalStorageDirectory().getAbsolutePath() , Toast.LENGTH_SHORT).show();
        // selectedImagePathOfCamera = String.valueOf(destination);


        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();

            Toast.makeText(this, "√ Saved.\nFind this image to SaveMe folder", Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            Toast.makeText(this, "Warning !!!\nYou have to allow storage permission to save this snapshot", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } catch (IOException e) {
            Toast.makeText(this, "Warning !!!\nYou have to allow storage permission to save this snapshot", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // profilePictureIMG.setImageBitmap(thumbnail);
    }



    /*SNAPSHOT OPERATION*/

}
