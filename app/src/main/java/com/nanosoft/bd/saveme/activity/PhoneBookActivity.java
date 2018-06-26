package com.nanosoft.bd.saveme.activity;

import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.RawContacts;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.database.DatabaseManager;
import com.nanosoft.bd.saveme.phoneBook.Contact;
import com.nanosoft.bd.saveme.phoneBook.CustomAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PhoneBookActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    /*Load contact*/
    // String insertUrl = "http://smartcampus.com.bd/contactmaster/insertContact.php";
    String insertUrl = "http://saveme.com.bd/contactmaster/insertContact.php";
    // private static final String TAG = "PhoneBook: ";
    // public static final String TAG = PhoneBook.class.getSimpleName();
    ProgressDialog pDialog;
    RequestQueue requestQueue;
    String phoneNumber, name;
    List<Contact> contactList = new ArrayList<>();
    Button uploadButton, btnInvite;
    ImageButton callImage, addContact;
    EditText searchEt;
    ListView contactLv;
    CustomAdapter customAdapter;
    String errors;
    ConnectivityManager connectivity;
    int position;
    String myPhoneNumber;
    TextView btnShow;

    CheckBox allSelectCheckBox;


    DatabaseManager manager;
    Contact contact;

    private ProgressDialog mProgressDialog;
    Toolbar toolbar;
    NavigationView navigationView;
    MenuItem action_searchItem, action_sendSMS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_book);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Phone Book");
        floatingActionButtonAndDrawer();
        initialise();
        progressDialog();
        OnClick();

        new Handler().postDelayed(new Runnable() {
            public void run() {
                loadContacts();
            }
        }, 50);

        setAllList();


    }

    private void progressDialog() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading........");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.show();
    }

    private void setAllList() {

        contactLv.setTextFilterEnabled(true);
        customAdapter = new CustomAdapter(this, contactList);
        contactLv.setAdapter(customAdapter);
        contactLv.setChoiceMode(contactLv.CHOICE_MODE_MULTIPLE);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        // we register for the contextmneu
        registerForContextMenu(contactLv);
        // React to user clicks on item
    }

    private void OnClick() {

        //        btnShow.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                searchEt.setVisibility(View.VISIBLE);
//                btnShow.setVisibility(View.GONE);
//            }
//        });
//        callImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_DIAL);
//                startActivity(intent);
//            }
//        });

       /* etSearchbox.setText(Html.fromHtml("your <font color='#FF0000'>content</font>"));*/
        // pDialog = new ProgressDialog(this); pDialog.setMessage("Loading..."); pDialog.show();

        contactLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parentAdapter, View view, int position,
                                    long id) {


                // We know the View is a <extView so we can cast it
                TextView clickedView = (TextView) view;

                Toast.makeText(PhoneBookActivity.this, "Item with id [" + id + "] - Position [" + position + "] - Planet [" + clickedView.getText() + "]", Toast.LENGTH_SHORT).show();

            }
        });
        connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
    /*    uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });*/

        /*  allSelectCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (allSelectCheckBox.isChecked()) {
                    contact = new Contact("1");
                    manager.initializeCheckBoxStatus(contact);

                    // customAdapter = new CustomAdapter(getApplicationContext(), contacts);
                    contactLv.setAdapter(customAdapter);

                } else {

                    contact = new Contact("0");
                    manager.initializeCheckBoxStatus(contact);

                    // customAdapter = new CustomAdapter(getApplicationContext(), contacts);
                    contactLv.setAdapter(customAdapter);
                }
            }
        });

*/

    }

    private void initialise() {

        // callImage = (ImageButton) findViewById(R.id.callImage);
        // btnInvite = (Button) findViewById(R.id.btnInvite);

        // allSelectCheckBox = (CheckBox) findViewById(R.id.allSelectCheckBox);
        contactLv = (ListView) findViewById(R.id.contactLv);
        searchEt = (EditText) findViewById(R.id.searchEt);
        //btnShow = (TextView) findViewById(R.id.btnShow);
    }

    private void floatingActionButtonAndDrawer() {
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

        navigationView.getMenu().getItem(0).setChecked(true);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Operations.IntSaveToSharedPreference(this, "bitForCheckBoxVisibility", 0);

            animation(R.id.drawer_layout);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    finish();

                    // super.onBackPressed();
                }
            }, 700);
           // super.onBackPressed();
        }
    }

    private void animationForward(int layoutId){

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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_phone_book, menu);
        action_searchItem = menu.findItem(R.id.action_searchItem);
        action_sendSMS = menu.findItem(R.id.action_sendSMS);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_searchItem) {
            searchEt.setVisibility(View.VISIBLE);
            navigationView.getMenu().getItem(1).setChecked(true);
            return true;
        }
        if (id == R.id.action_sendSMS) {
            sendSMS();
            return true;
        }
        if (id == R.id.action_about) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            return true;
        }
        if (id == R.id.action_home) {
            final Intent intent = new Intent(PhoneBookActivity.this, HomeActivity.class);
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
        int id = item.getItemId();

        if (id == R.id.contactListItem) {
            action_sendSMS.setVisible(false);
            searchEt.setVisibility(View.GONE);
            Operations.IntSaveToSharedPreference(this, "bitForCheckBoxVisibility", 0);
            setAllList();

        } else if (id == R.id.searchItem) {
            searchEt.setVisibility(View.VISIBLE);

        } else {
            if (id == R.id.backupItem) {
                searchEt.setVisibility(View.GONE);
                backupPermission();
            } else if (id == R.id.inviteItem) {
                Operations.IntSaveToSharedPreference(this, "bitForCheckBoxVisibility", 1);
                searchEt.setVisibility(View.GONE);
                setAllList();
                action_sendSMS.setVisible(true);
            } else if (id == R.id.restoreItem) {
               // addContact("Azhar Vai", "3156241523451234");
                restorePermission();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void backupPermission() {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PhoneBookActivity.this);
        alertDialog.setTitle("Backup");
        alertDialog.setMessage("Do you want to backup your contact in Server?");

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        uploadContacts();
                    }
                });
        alertDialog.show();

    }

    private void restorePermission() {


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PhoneBookActivity.this);
        alertDialog.setTitle("Restore");
        alertDialog.setMessage("Do you want to Restore your contact from Server?");

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        mProgressDialog = new ProgressDialog(PhoneBookActivity.this);
                        mProgressDialog.setMessage("Loading........");
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.show();

                       // Toast.makeText(PhoneBookActivity.this, "mProgressDialog1", Toast.LENGTH_SHORT).show();

                        dialog.cancel();

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                new ContactSynchronizationAsyncTask().execute();
                            }
                        }, 1000);



                    }
                });
        alertDialog.show();

    }



    private void loadContacts() {
        int count = 0;

        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        while (phones.moveToNext()) {
            // read all phone contact list name....
            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            // read all phone contact list phone number....
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Contact contact = new Contact(name, phoneNumber);
            contactList.add(contact);
            count++;
        }
        //  Toast.makeText(this, "total contact" + count++, Toast.LENGTH_LONG).show();

        phones.close();



        /*search*/

        /*make cll and sending text*/


        int contactSize = Operations.getIntegerSharedPreference(getApplicationContext(), "totalContact", 0);

        if (contactSize == contactList.size()) {
            mProgressDialog.dismiss();
            customAdapter = new CustomAdapter(PhoneBookActivity.this, contactList);
            contactLv.setAdapter(customAdapter);

        } else {
            Operations.IntSaveToSharedPreference(this, "totalContact", contactList.size());
            //uploadContacts();
            Toast.makeText(this, "total contact" + count++, Toast.LENGTH_LONG).show();
            manager = new DatabaseManager(getApplicationContext());
            contact = new Contact("0");
            //  manager.initializeCheckBoxStatus(contact);
            for (int i = 0; i < contactList.size(); i++) {
                manager.addCheckBoxStatus(contact);

                if (i + 1 == contactList.size()) {
                    mProgressDialog.dismiss();
                }
            }


        }


       /* final Contact pos = customAdapter.getItem(position);*/
        contactLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                animationForward(R.id.drawer_layout);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        animation2(R.id.drawer_layout);
                    }
                }, 2000);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Intent intent = new Intent(getApplicationContext(), PhoneBookDetailActivity.class);
                        intent.putExtra("name", getItem(position).getContactName());
                        intent.putExtra("phone", getItem(position).getContactPhoneNumber());
                        startActivity(intent);
                    }
                }, 500);

            }
        });


        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                System.out.println("Text [" + s + "]- Start [" + start + "] - Before [" + before + "] - Count [" + count + "]");
                if (count < before) {
                    // We're deleting char so we need to reset the adapter data
                    customAdapter.resetData();

                }
                customAdapter.getFilter().filter(s.toString());


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        customAdapter = new CustomAdapter(PhoneBookActivity.this, contactList);
        contactLv.setAdapter(customAdapter);


    }


    public Contact getItem(int position) {
        final Contact pos = customAdapter.getItem(position);
        return pos;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // uploadContacts();
    }

    private void uploadContacts() {
       /* if (!Operations.checkUser(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "Could not upload.", Toast.LENGTH_LONG).show();
            return;
        }*/

        StringRequest request = new StringRequest(Request.Method.POST, insertUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                  /*  Toast.makeText(getApplicationContext(), myPhoneNumber, Toast.LENGTH_LONG).show();*/
                    VolleyLog.v("Response:%n %s", response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof NoConnectionError) {
                  /*  errors = "No internet Access, Check your internet connection.";
                    Toast.makeText(getApplicationContext(), errors, Toast.LENGTH_LONG).show();*/
                    finish();
                    Toast.makeText(getApplicationContext(), "Slow net connection", Toast.LENGTH_SHORT).show();

                }
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {


                Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

                Map<String, String> parameters = new HashMap<String, String>();
                Map<String, String> parametersall = new HashMap<String, String>();

                int i = 0;

                myPhoneNumber = Operations.getMyPhoneNumber(getApplicationContext());

                while (phones.moveToNext()) {
                    // read all phone contact list name....
                    name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    // read all phone contact list phone number....
                    phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    parameters.put("name", name);
                    parameters.put("phoneNumber", phoneNumber);
                    parameters.put("userPhone", myPhoneNumber);
                    parametersall.put("contact" + i, parameters.toString());
                    i++;
                }
                parametersall.put("count", String.valueOf(i));
                parametersall.put("userPhone1", myPhoneNumber);
                phones.close();

                return parametersall;
            }
        };
        NetworkInfo info = connectivity.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            requestQueue.cancelAll(request);
            requestQueue.add(request);

            Toast.makeText(getApplicationContext(), "Phone Book load successfully!", Toast.LENGTH_LONG).show();

        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_LONG).show();
        }
    }

    public void inviteClick(View view) {


        /*Checked Items and text */
        try {


            for (int i = 0; i < contactLv.getCount(); i++) {
                Contact contact = (Contact) contactLv.getItemAtPosition(i);
                manager = new DatabaseManager(this);
                if (manager.getCheckBoxStatus(i + 1).getCheckBoxStatus().equals("1")) {
                    SmsManager sms = SmsManager.getDefault();
                    String message = "'SaveMe' is a mindless helper that fills any kind of guilty for emergency support. Go on";
                    message += "\nhttp://saveme.com.bd";
                    String number = contact.getContactPhoneNumber();
                    if (number.length() > 0) {
                        sms.sendTextMessage(number, null, message, null, null);
                        Toast.makeText(this, "sms send successfully " + number, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Select a contact first", Toast.LENGTH_SHORT).show();
        }
    }


    private void sendSMS() {

        try {
            for (int i = 0; i < contactLv.getCount(); i++) {
                Contact contact = (Contact) contactLv.getItemAtPosition(i);
                manager = new DatabaseManager(this);
                if (manager.getCheckBoxStatus(i + 1).getCheckBoxStatus().equals("1")) {
                    SmsManager sms = SmsManager.getDefault();
                    String message = "'SaveMe' is a mindless helper that fills any kind of guilty for emergency support. Go on";
                    message += "\nhttp://saveme.com.bd";
                    String number = contact.getContactPhoneNumber();
                    if (number.length() > 0) {
                        sms.sendTextMessage(number, null, message, null, null);
                        Toast.makeText(this, "sms send successfully " + number, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            Toast.makeText(this, "Select a contact first", Toast.LENGTH_SHORT).show();
        }

    }


    private void addContact(String name, String phoneNumber) {

        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        int rawContactID = ops.size();

        // Adding insert operation to operations list
        // to insert a new raw contact in the table ContactsContract.RawContacts
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                .withValue(RawContacts.ACCOUNT_NAME, null)
                .build());

        // Adding insert operation to operations list
        // to insert display name in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                .withValue(StructuredName.DISPLAY_NAME, name)
                .build());

        // Adding insert operation to operations list
        // to insert Mobile Number in the table ContactsContract.Data
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
                .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                .withValue(Phone.NUMBER, phoneNumber)
                .withValue(Phone.TYPE, CommonDataKinds.Phone.TYPE_MOBILE)
                .build());

        // Adding insert operation to operations list
        // to  insert Home Phone Number in the table ContactsContract.Data
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
//                .withValue(Phone.NUMBER, "etHomePhone.getText().toString()")
//                .withValue(Phone.TYPE, Phone.TYPE_HOME)
//                .build());
//
//        // Adding insert operation to operations list
//        // to insert Home Email in the table ContactsContract.Data
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                .withValue(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
//                .withValue(Email.ADDRESS, "etHomeEmail.getText().toString()")
//                .withValue(Email.TYPE, Email.TYPE_HOME)
//                .build());
//
//        // Adding insert operation to operations list
//        // to insert Work Email in the table ContactsContract.Data
//        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
//                .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactID)
//                .withValue(ContactsContract.Data.MIMETYPE, Email.CONTENT_ITEM_TYPE)
//                .withValue(Email.ADDRESS, "etWorkEmail.getText().toString()")
//                .withValue(Email.TYPE, Email.TYPE_WORK)
//                .build());

        try {
            // Executing all the insert operations as a single database transaction
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
           // Toast.makeText(getBaseContext(), "Contact is successfully added", Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }


    }


    public void contactSynchronization() {
        ConnectivityManager connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        RequestQueue requestQueue1 = Volley.newRequestQueue(this);
        String getMethodUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20%28select%20woeid%20from%20geo.places%281%29%20where%20text%3D%22dhaka%2C%20bd%22%29&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";

      //  Toast.makeText(PhoneBookActivity.this, "GET URL", Toast.LENGTH_SHORT).show();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, getMethodUrl, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {


//                    JSONArray array = response.getJSONArray("contact");
//                    for (int i = 1; i < array.length(); i++) {                            // for full forecast use - array.length() or 10
//                        JSONObject object = array.getJSONObject(i);
//                        String contactName = object.getString("contactName");
//                        String contactNumber = object.getString("contactNumber");

                   // Toast.makeText(PhoneBookActivity.this, "Json", Toast.LENGTH_SHORT).show();

                    //forecast
                    JSONArray array = response.getJSONObject("query").getJSONObject("results").getJSONObject("channel").getJSONObject("item").getJSONArray("forecast");
                    for (int i = 1; i < array.length(); i++) {                            // for full forecast use - array.length() or 10
                        JSONObject object = array.getJSONObject(i);
                        String contactNumberFromServer = object.getString("day");
                        int contactNameFrom = object.getInt("high");
                        String contactNameFromServer = String.valueOf(contactNameFrom);
                        int minTemp = object.getInt("low");


//                        Toast.makeText(PhoneBookActivity.this, contactNameFromServer, Toast.LENGTH_SHORT).show();
//                        addContact(contactNameFromServer, contactNumberFromServer);

                        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
                        while (phones.moveToNext()) {
                            // read all phone contact list name....
                            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                            // read all phone contact list phone number....
                            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                            if (!contactNumberFromServer.equals(phoneNumber)) {
                                addContact(contactNumberFromServer, contactNameFromServer);
                                break;
                            }

                        }

                        phones.close();
                    }

                    mProgressDialog.dismiss();
                    Toast.makeText(PhoneBookActivity.this, "Contact Synchronization Completed", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(PhoneBookActivity.this, "Internet Speed Slow", Toast.LENGTH_SHORT).show();
                if (error instanceof NoConnectionError) {
                    Toast.makeText(PhoneBookActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
       // AppController.getInstance().addToRequestQueue(request);

        NetworkInfo info = connectivity.getActiveNetworkInfo();

        if (info != null && info.isConnected()) {
            requestQueue1.add(request);
        }
    }




    private  class  ContactSynchronizationAsyncTask extends AsyncTask<Void, Void , Void>{



        @Override
        protected void onPreExecute() {

            super.onPreExecute();


        }


        @Override
        protected Void doInBackground(Void... params) {

          //  contactSynchronization();

            try {
                if (isOnline()) {

                    contactSynchronization();

                }

            } catch (Exception e) {

            }


            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //Toast.makeText(getApplicationContext(), "Try", Toast.LENGTH_SHORT).show();
            try {
                if (isOnline()) {

                  //  Toast.makeText(getApplicationContext(), "Online", Toast.LENGTH_SHORT).show();

                    // if (result!= null) {
                    //    ArrayAdapter<RssItem> adapter = new ArrayAdapter<RssItem>(NewsFeedViewActivity.this, android.R.layout.simple_list_item_1, result);
                    //   itcItems.setAdapter(adapter);
                    //  itcItems.setOnItemClickListener(new ListListener(result, NewsFeedViewActivity.this));

                 //   mProgressDialog.dismiss();
                    //   }else {
                    // itcItems.setAdapter(null);
                    //  Toast.makeText(NewsFeedViewActivity.this, "News Server Down", Toast.LENGTH_SHORT).show();
                    //   mProgressDialog1.dismiss();
                    //    }

                }

            } catch (Exception e) {

            }
        }


        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }

    }

    private class ContactSyn extends AsyncTask<String, Void, List<Contact>> {


        Context context;

        @Override
        protected void onPreExecute() {

            context = PhoneBookActivity.this;
            super.onPreExecute();


        }

        @Override
        protected List<Contact> doInBackground(String... urls) {

            Toast.makeText(getApplicationContext(), "Try", Toast.LENGTH_SHORT).show();

            try {
                if (isOnline()) {
                    Toast.makeText(getApplicationContext(), "Online", Toast.LENGTH_SHORT).show();

                   // contactsSyn();

                }else {
                    Toast.makeText(getApplicationContext(), "offline", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
              //  Toast.makeText(getApplicationContext(), "Crash", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(List<Contact> result) {
            try {
                if (isOnline()) {

                    if (result!= null) {
                    //    ArrayAdapter<RssItem> adapter = new ArrayAdapter<RssItem>(NewsFeedViewActivity.this, android.R.layout.simple_list_item_1, result);
                     //   itcItems.setAdapter(adapter);
                      //  itcItems.setOnItemClickListener(new ListListener(result, NewsFeedViewActivity.this));

                        mProgressDialog.dismiss();
                    }else {
                       // itcItems.setAdapter(null);
                      //  Toast.makeText(NewsFeedViewActivity.this, "News Server Down", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }

                }

            } catch (Exception e) {

            }
        }

        public boolean isOnline() {
            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }


    }




}
