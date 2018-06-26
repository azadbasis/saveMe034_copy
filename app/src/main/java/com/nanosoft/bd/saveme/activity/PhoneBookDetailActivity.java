package com.nanosoft.bd.saveme.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;

import com.nanosoft.bd.saveme.R;
import com.nanosoft.bd.saveme.phoneBook.Contact;
public class PhoneBookDetailActivity extends DashboardActivity {

    TextView contactDetailName;
    TextView contactDetailPhone;

    /*ContactDBManager dbManager;
    ContactModel contactModel;*/

    int id;
    String phoneNo, name;
    Contact contactModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_book_detail);

        contactDetailName = (TextView) findViewById(R.id.contactDetailNameID);
        contactDetailPhone = (TextView) findViewById(R.id.contactDetailPhoneID);

        name = getIntent().getStringExtra("name");
        phoneNo = getIntent().getStringExtra("phone");
        contactDetailName.setText(name);
        contactDetailPhone.setText(phoneNo);

    }

    @Override
    public void onBackPressed() {
        animation(R.id.phoneBookLayout);

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

    public void callNumber(View view) {

        if (Build.VERSION.SDK_INT > 22) {
            permissionPhoneCall();

        }else {
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo));
            startActivity(callIntent);
        }

    }

    private void permissionPhoneCall() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[] {Manifest.permission.CALL_PHONE},
                    REQUEST_CODE_ASK_PERMISSIONS);

            return;
        } else {
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNo));
            startActivity(callIntent);
        }


    }

    final private int REQUEST_CODE_ASK_PERMISSIONS = 111;

    public void msgNumber(View view) {
        Intent msgIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + phoneNo));
        startActivity(msgIntent);
    }

}
