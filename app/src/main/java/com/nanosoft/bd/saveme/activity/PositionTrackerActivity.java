package com.nanosoft.bd.saveme.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.awareness.Awareness;
import com.google.android.gms.awareness.snapshot.DetectedActivityResult;
import com.google.android.gms.awareness.snapshot.HeadphoneStateResult;
import com.google.android.gms.awareness.snapshot.LocationResult;
import com.google.android.gms.awareness.snapshot.PlacesResult;
import com.google.android.gms.awareness.snapshot.WeatherResult;
import com.google.android.gms.awareness.state.HeadphoneState;
import com.google.android.gms.awareness.state.Weather;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.application.SApplication;
import com.nanosoft.bd.saveme.service.LocationService;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * This is the activity for feature 6 in the dashboard application.
 * It displays some text and provides a way to get back to the home activity.
 */


public class PositionTrackerActivity extends DashboardActivity implements SensorEventListener,GoogleApiClient.ConnectionCallbacks,OnMapReadyCallback {

    /**
     * onCreate
     * <p/>
     * Called when the activity is first created.
     * This is where you should do all of your normal static set up: create views, bind data to lists, etc.
     * This method also provides you with a Bundle containing the activity's previously frozen state, if there was one.
     * <p/>
     * Always followed by onStart().
     *
     * @param savedInstanceState Bundle
     */

    private final String TAG = PositionTrackerActivity.class.getSimpleName();

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private long lastTime = 0;
    private float lastX, lastY, lastZ;
    private static final int THRESHOLD = 600; //used to see whether a shake gesture has been detected or not.
    TextView coordinates;
    TextView address;
    Spinner spinnerTime;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    StringBuilder builder = new StringBuilder();
/*For Awareness API*/
    private static final int GET_LOCATION_PERMISSION_REQUEST_CODE = 12345;
    private static final int GET_PLACE_PERMISSION_REQUEST_CODE = 123456;
    private static final int GET_WEATHER_PERMISSION_REQUEST_CODE = 1234567;

    private GoogleApiClient mGoogleApiClient;
/*For Awareness API*/

    private ProgressDialog mProgressDialog;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_position_tracker);
        setTitleFromActivityLabel(R.id.title_text);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        Button update = (Button) findViewById(R.id.update_button);
        update.setOnClickListener(new UpdateLocationClick());
        sharedPreferences = getSharedPreferences("SaveData", MODE_PRIVATE);
        coordinates = (TextView) findViewById(R.id.location_points);
        address = (TextView) findViewById(R.id.location_address);
        spinnerTime = (Spinner) findViewById(R.id.spinnerTime);



//        mProgressDialog = new ProgressDialog(PositionTrackerActivity.this);
//        mProgressDialog.setMessage("Loading........");
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        // mProgressDialog.setCancelable(false);
//        mProgressDialog.show();




        String timeInterval = "";
        if (sharedPreferences.contains("timeInterval"))
            timeInterval = sharedPreferences.getString("timeInterval", "30");
        else
            timeInterval = "30";

        Integer integet = Integer.parseInt(timeInterval);

        spinnerTime.setSelection(integet / 15 - 1);

        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                String val = spinnerTime.getItemAtPosition(position).toString();
                Operations.SaveToSharedPreference(getApplicationContext(), "timeInterval", val);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (SApplication.LOCATION != null) {
            double lat = SApplication.LOCATION.getLatitude();
            double lon = SApplication.LOCATION.getLongitude();
            coordinates.setText(lat + " " + lon);
            Geocoder geocoder = new Geocoder(getApplicationContext(), new Locale("en"));


            try {
                List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                if (addresses != null && addresses.size() != 0) {
                    // StringBuilder builder = new StringBuilder();
                    Address returnAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("");
                    int i;
                    for (i = 0; i < returnAddress.getMaxAddressLineIndex(); i++) {
                        builder.append(returnAddress.getAddressLine(i));
                        builder.append(" ");
                    }

                    address.setText(builder);
                    address.setVisibility(View.VISIBLE);
                    // String add = address.getText().toString();
                    // Toast.makeText(getApplicationContext(), add, Toast.LENGTH_LONG).show();

                } else {
                    Log.e(TAG, "Addresses null");
                }
            } catch (IOException e) {
                Log.e(TAG, "Geocoder exception " + e);
            }
        } else {
            coordinates.setText("No location yet");
            address.setVisibility(View.INVISIBLE);
        }
      /*  sharedPreferences = getSharedPreferences("SaveData", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        String add = address.getText().toString();
        editor.putString("Address", add);
        editor.commit();*/

        Operations.SaveToSharedPreference(this, "Address", address.getText().toString());
        Operations.SaveToSharedPreference(this, "Coordinate", coordinates.getText().toString());
       /* PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, new Intent(getApplicationContext(), PositionTrackerActivity.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        Intent myIntent = new Intent(PositionTrackerActivity.this, LocationService.class);
        pendingIntent = PendingIntent.getService(PositionTrackerActivity.this, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 10);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(PositionTrackerActivity.this, add, Toast.LENGTH_LONG).show();*/
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        buildApiClient();
    }




    private void buildApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(PositionTrackerActivity.this)
                .addApi(Awareness.API)
                .addConnectionCallbacks(this)
                .build();
        mGoogleApiClient.connect();
    }
    @Override
    public void onBackPressed() {
        animation(R.id.positionTrackerLayout);

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
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            long currentTime = System.currentTimeMillis();
            if ((currentTime - lastTime) > 100) {
                long diffTime = (currentTime - lastTime);
                lastTime = currentTime;
                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;
                if (speed > THRESHOLD) {
                    getRandomNumber();
                }
                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /*
        It's a good practice to unregister the sensor when the application hibernates to save battery power.
     */
    protected void onPause() {
        super.onPause();

        mSensorManager.unregisterListener(this);
        PositionTrackerActivity.this.stopService(new Intent(PositionTrackerActivity.this, LocationService.class));
    }

    protected void onResume() {
        super.onResume();
        PositionTrackerActivity.this.startService(new Intent(PositionTrackerActivity.this, LocationService.class));
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        mProgressDialog = new ProgressDialog(PositionTrackerActivity.this);
        mProgressDialog.setMessage("Loading........");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        callSnapShotGroupApis();
    }

    private void getRandomNumber() {
        Random randNumber = new Random();
        int iNumber = randNumber.nextInt(100);
        TextView text = (TextView) findViewById(R.id.number);
        text.setText("" + iNumber);
        LinearLayout ball = (LinearLayout) findViewById(R.id.ball);
        Animation a = AnimationUtils.loadAnimation(this, R.anim.move_down_ball_first);
        ball.setVisibility(View.INVISIBLE);
        ball.setVisibility(View.VISIBLE);
        ball.clearAnimation();
        ball.startAnimation(a);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        callSnapShotGroupApis();
    }

    private void callSnapShotGroupApis() {
        //get info about user's current activity
        getCurrentActivity();

        //get the current state of the headphones.
        getHeadphoneStatus();

        //get current location. This will need location permission, so first check that.
        if (ContextCompat.checkSelfPermission(PositionTrackerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    PositionTrackerActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    GET_LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }

        //get current place. This will need location permission, so first check that.
        if (ContextCompat.checkSelfPermission(PositionTrackerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    PositionTrackerActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    GET_PLACE_PERMISSION_REQUEST_CODE);
        } else {
            getPlace();
        }

        //get current weather conditions. This will need location permission, so first check that.
        if (ContextCompat.checkSelfPermission(PositionTrackerActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    PositionTrackerActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    GET_WEATHER_PERMISSION_REQUEST_CODE);
        } else {
            getWeather();
        }
    }


    private void getCurrentActivity() {
        Awareness.SnapshotApi.getDetectedActivity(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DetectedActivityResult>() {
                    @Override
                    public void onResult(@NonNull DetectedActivityResult detectedActivityResult) {
                        if (!detectedActivityResult.getStatus().isSuccess()) {
                          //  Toast.makeText(PositionTrackerActivity.this, "Could not get the current activity.", Toast.LENGTH_LONG).show();
                            return;
                        }
                        ActivityRecognitionResult ar = detectedActivityResult.getActivityRecognitionResult();
                        DetectedActivity probableActivity = ar.getMostProbableActivity();

                        //set the activity name
                        TextView activityName = (TextView) findViewById(R.id.probable_activity_name);
                        switch (probableActivity.getType()) {
                            case DetectedActivity.IN_VEHICLE:
                                activityName.setText("In vehicle");
                                break;
                            case DetectedActivity.ON_BICYCLE:
                                activityName.setText("On bicycle");
                                break;
                            case DetectedActivity.ON_FOOT:
                                activityName.setText("On foot");
                                break;
                            case DetectedActivity.RUNNING:
                                activityName.setText("Running");
                                break;
                            case DetectedActivity.STILL:
                                activityName.setText("Still");
                                break;
                            case DetectedActivity.TILTING:
                                activityName.setText("Tilting");
                                break;
                            case DetectedActivity.UNKNOWN:
                                activityName.setText("Unknown");
                                break;
                            case DetectedActivity.WALKING:
                                activityName.setText("Walking");
                                break;
                        }

                        //set the confidante level
                        ProgressBar confidenceLevel = (ProgressBar) findViewById(R.id.probable_activity_confidence);
                        confidenceLevel.setProgress(probableActivity.getConfidence());

                        //display the time
                        TextView timeTv = (TextView) findViewById(R.id.probable_activity_time);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a dd-MM-yyyy", Locale.getDefault());
                        timeTv.setText("as on: " + sdf.format(new Date(ar.getTime())));
                    }
                });
    }

    private void getHeadphoneStatus() {
        Awareness.SnapshotApi.getHeadphoneState(mGoogleApiClient)
                .setResultCallback(new ResultCallback<HeadphoneStateResult>() {
                    @Override
                    public void onResult(@NonNull HeadphoneStateResult headphoneStateResult) {
                        if (!headphoneStateResult.getStatus().isSuccess()) {
                            //Toast.makeText(PositionTrackerActivity.this, "Could not get headphone state.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        HeadphoneState headphoneState = headphoneStateResult.getHeadphoneState();

                        //display the status
                        TextView headphoneStatusTv = (TextView) findViewById(R.id.headphone_status);
                        headphoneStatusTv.setText(headphoneState.getState() == HeadphoneState.PLUGGED_IN ? "Plugged in." : "Unplugged.");
                    }
                });
    }
    Location location;
    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    private void getLocation() {
        //noinspection MissingPermission
        Awareness.SnapshotApi.getLocation(mGoogleApiClient)
                .setResultCallback(new ResultCallback<LocationResult>() {
                    @Override
                    public void onResult(@NonNull LocationResult locationResult) {
                        if (!locationResult.getStatus().isSuccess()) {
                           // Toast.makeText(PositionTrackerActivity.this, "Could not get location.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //get location
                        location = locationResult.getLocation();
                       // ((TextView) findViewById(R.id.current_latlng)).setText(location.getLatitude() + ", " + location.getLongitude());
                        ((TextView) findViewById(R.id.current_latlng)).setText(Operations.GetFullAddress(getApplicationContext()));

                        //display the time
                        TextView timeTv = (TextView) findViewById(R.id.latlng_time);
                        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a dd-MM-yyyy", Locale.getDefault());
                        timeTv.setText("as on: " + sdf.format(new Date(location.getTime())));

                        //Load the current map image from Google map
                        String url = "https://maps.googleapis.com/maps/api/staticmap?center="
                                + location.getLatitude() + "," + location.getLongitude()
                                + "&zoom=20&size=400x250&key=" + getString(R.string.api_key);
                       // Picasso.with(PositionTrackerActivity.this).load(url).into((ImageView) findViewById(R.id.current_map));
                    }
                });
    }

    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    private void getPlace() {
        //noinspection MissingPermission
        Awareness.SnapshotApi.getPlaces(mGoogleApiClient)
                .setResultCallback(new ResultCallback<PlacesResult>() {
                    @Override
                    public void onResult(@NonNull final PlacesResult placesResult) {
                        if (!placesResult.getStatus().isSuccess()) {
                          //  Toast.makeText(PositionTrackerActivity.this, "Could not get places.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //get the list of all like hood places
                        List<PlaceLikelihood> placeLikelihoodList = placesResult.getPlaceLikelihoods();

                        // Show the top 5 possible location results.
                        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.current_place_container);
                        linearLayout.removeAllViews();
                        if (placeLikelihoodList != null) {



                            for (int i = 0; i < 10 && i < placeLikelihoodList.size(); i++) {
                                PlaceLikelihood p = placeLikelihoodList.get(i);
                                // Toast.makeText(SearchActivity.this, p.getPlace().getName()+ ", "+p.getPlace().getAddress(), Toast.LENGTH_SHORT).show();
                                //add place row
                                View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.row_nearby_place, linearLayout, false);

                                //View v = LayoutInflater.from(SearchActivity.this).inflate(R.layout.)
                                ((TextView) v.findViewById(R.id.place_name)).setText(p.getPlace().getName());
                                ((TextView) v.findViewById(R.id.place_address)).setText(p.getPlace().getAddress());
                                linearLayout.addView(v);
                            }

                            mProgressDialog.dismiss();
                        } else {
                          //  Toast.makeText(PositionTrackerActivity.this, "Could not get nearby places.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @RequiresPermission("android.permission.ACCESS_FINE_LOCATION")
    private void getWeather() {
        //noinspection MissingPermission
        Awareness.SnapshotApi.getWeather(mGoogleApiClient)
                .setResultCallback(new ResultCallback<WeatherResult>() {
                    @Override
                    public void onResult(@NonNull WeatherResult weatherResult) {
                        if (!weatherResult.getStatus().isSuccess()) {
                          //  Toast.makeText(PositionTrackerActivity.this, "Could not get weather.", Toast.LENGTH_LONG).show();
                            return;
                        }

                        //parse and display current weather status
                        Weather weather = weatherResult.getWeather();
                        String weatherReport = "Temperature: " + weather.getTemperature(Weather.CELSIUS)
                                + "\nHumidity: " + weather.getHumidity();
                        ((TextView) findViewById(R.id.weather_status)).setText(weatherReport);
                    }
                });
    }
    @Override
    public void onConnectionSuspended(int i) {
        new AlertDialog.Builder(this)
                .setMessage("Cannot connect to google api services.")
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialogInterface, final int i) {
                        finish();
                    }
                }).show();
    }
    private GoogleMap mMap;
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMyLocationEnabled(true);

        new Handler().postDelayed(new Runnable() {
            public void run() {

                if (location!= null){
                LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());//Dhaka, Bangladesh

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 17));
                mMap.addMarker(new MarkerOptions()
                        .title(Operations.GetFullAddress(getApplicationContext()))
                        .position(latlng)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                );
                }else {
                    Toast.makeText(PositionTrackerActivity.this, "Please Turn on GPS and Internet", Toast.LENGTH_SHORT).show();
                }
            }
        }, 1500);






        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    public class UpdateLocationClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {

            if (SApplication.LOCATION != null) {
                double lat = SApplication.LOCATION.getLatitude();
                double lon = SApplication.LOCATION.getLongitude();
                coordinates.setText(lat + " " + lon);
                Geocoder geocoder = new Geocoder(getApplicationContext(), new Locale("en"));
                try {
                    // get address from location
                    List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
                    if (addresses != null && addresses.size() != 0) {
                        StringBuilder builder = new StringBuilder();
                        Address returnAddress = addresses.get(0);
                        for (int i = 0; i < returnAddress.getMaxAddressLineIndex(); i++) {
                            builder.append(returnAddress.getAddressLine(i));
                            builder.append(" ");
                        }
                        address.setText(builder);
                        address.setVisibility(View.VISIBLE);
                        String add = address.getText().toString();


                        sharedPreferences = getSharedPreferences("SaveData", MODE_PRIVATE);
                        //editor = sharedPreferences.edit();
                        //  editor.putString("Address", "here is location");
                        String message = sharedPreferences.getString("messageLocation", add);


                        /*PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                0, new Intent(getApplicationContext(), PositionTrackerActivity.class),
                                PendingIntent.FLAG_UPDATE_CURRENT);*/

                        /*PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                                0, new Intent(getApplicationContext(), BackService.class),
                                PendingIntent.FLAG_UPDATE_CURRENT);

           *//* PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                    0, new Intent(getApplicationContext(), PositionTrackerActivity.class),
                    PendingIntent.FLAG_UPDATE_CURRENT);*//*

                       *//* mAlarmManager.set(AlarmManager.RTC,
                                10000, pendingIntent);
                        mAlarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 10000,
                                20000, pendingIntent);*//*

                        mAlarmManager.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + 10000,
                                20000, pendingIntent);*/

                        Toast.makeText(PositionTrackerActivity.this, add, Toast.LENGTH_LONG).show();


                        // editor.commit();
                        // Toast.makeText(getApplicationContext(), messageLocation, Toast.LENGTH_LONG).show();
                    } else {
                        Log.e(TAG, "Addresses null");
                    }
                } catch (IOException e) {
                    Log.e(TAG, "Geocoder exception " + e);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Check GPS status and internet connection", Toast.LENGTH_LONG).show();
                coordinates.setText("No location yet");
                address.setVisibility(View.INVISIBLE);
            }


        }

/*

        long millis = Long.parseLong(((EditText) findViewById(R.id.etTime))
                .getText().toString());
*/


    }


}
