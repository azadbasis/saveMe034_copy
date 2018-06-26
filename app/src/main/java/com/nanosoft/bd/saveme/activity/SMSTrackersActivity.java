package com.nanosoft.bd.saveme.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.nanosoft.bd.saveme.R;

public class SMSTrackersActivity extends DashboardActivity {
    String smsCode = null;
    EditText etfirstPhone;
    EditText etsecondPhone;
    EditText etthirdPhone;
    EditText etfourthPhone;
    EditText etfifthPhone;
    EditText etsixthPhone;
    EditText etseventhPhone;
    EditText eteighthPhone;
    EditText etninththPhone;
    EditText ettenththPhone;

    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_trackers);
        setTitleFromActivityLabel(R.id.title_text);
        String addr = Operations.GetFullAddress(getApplicationContext());
        String co = Operations.getStringFromSharedPreference(getApplicationContext(), "Coordinate");
        String uri = "http://maps.google.com/maps?q=" + co.replace(" ", ",").replace("Lat:", "").replace("Lon:", "");
        addr = addr + "\n" + uri;


        tv = (TextView) findViewById(R.id.tvCode);
        etfirstPhone = (EditText) findViewById(R.id.etfirstPhone);
        etsecondPhone = (EditText) findViewById(R.id.etsecondPhone);
        etthirdPhone = (EditText) findViewById(R.id.etthirdPhone);
        etfourthPhone = (EditText) findViewById(R.id.etfourthPhone);
        etfifthPhone = (EditText) findViewById(R.id.etfifthPhone);
        etsixthPhone = (EditText) findViewById(R.id.etsixthPhone);
        etseventhPhone = (EditText) findViewById(R.id.etseventhPhone);
        eteighthPhone = (EditText) findViewById(R.id.eteighthPhone);
        etninththPhone = (EditText) findViewById(R.id.etninethPhone);
        ettenththPhone = (EditText) findViewById(R.id.ettenthPhone);

        tv.setText(Operations.getStringFromSharedPreference(this, "smsCode"));
        etfirstPhone.setText(Operations.getStringFromSharedPreference(this, "etfirstPhone"));
        etsecondPhone.setText(Operations.getStringFromSharedPreference(this, "etsecondPhone"));
        etthirdPhone.setText(Operations.getStringFromSharedPreference(this, "etthirdPhone"));
        etfourthPhone.setText(Operations.getStringFromSharedPreference(this, "etfourthPhone"));
        etfifthPhone.setText(Operations.getStringFromSharedPreference(this, "etfifthPhone"));

        etsixthPhone.setText(Operations.getStringFromSharedPreference(this, "etsixthPhone"));
        etseventhPhone.setText(Operations.getStringFromSharedPreference(this, "etseventhPhone"));
        eteighthPhone.setText(Operations.getStringFromSharedPreference(this, "eteighthPhone"));
        etninththPhone.setText(Operations.getStringFromSharedPreference(this, "etninththPhone"));
        ettenththPhone.setText(Operations.getStringFromSharedPreference(this, "ettenththPhone"));
        
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    TextView tv;

    public void btnGenerateClick(View view) {
        generateCode();
    }

    private void generateCode() {
        smsCode = String.format("%X", (int) (Math.random() * 100000 + 100000));
        tv.setText(smsCode);
        Operations.SaveToSharedPreference(this, "smsCode", smsCode);
    }

    public void SendSMS(EditText et, String nem) {
        if (smsCode == null) generateCode();
        String smsNumber = et.getText().toString();
/*if(PhoneNumberUtils.compare(getApplicationContext(), senderNumber, ePhoneNumber) && String.valueOf(random).equals(message)){

}*/
        if (smsNumber.length()>9 ) {
            Operations.SaveToSharedPreference(this, nem, smsNumber);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(smsNumber, null, smsCode, null, null);
            et.setBackgroundColor(Color.argb(0x0, 0x3640e, 0x87a23, 0xbde31));
        }else {
            Toast.makeText(this, "Number must be at least 10 digits", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickSms(View v) {
        Button btn = (Button) v;
        switch (btn.getId()) {
            case R.id.btnsendcode1:
                SendSMS((EditText) findViewById(R.id.etfirstPhone), "etfirstPhone");
                break;
            case R.id.btnsendcode2:
                SendSMS((EditText) findViewById(R.id.etsecondPhone), "etsecondPhone");
                break;
            case R.id.btnsendcode3:
                SendSMS((EditText) findViewById(R.id.etthirdPhone), "etthirdPhone");
                break;
            case R.id.btnsendcode4:
                SendSMS((EditText) findViewById(R.id.etfourthPhone), "etfourthPhone");
                break;
            case R.id.btnsendcode5:
                SendSMS((EditText) findViewById(R.id.etfifthPhone), "etfifthPhone");
                break;
            case R.id.btnsendcode6:
                SendSMS((EditText) findViewById(R.id.etsixthPhone), "etsixthPhone");
                break;
            case R.id.btnsendcode7:
                SendSMS((EditText) findViewById(R.id.etseventhPhone), "etseventhPhone");
                break;
            case R.id.btnsendcode8:
                SendSMS((EditText) findViewById(R.id.eteighthPhone), "eteighthPhone");
                break;
            case R.id.btnsendcode9:
                SendSMS((EditText) findViewById(R.id.etninethPhone), "etninththPhone");
                break;
            case R.id.btnsendcode10:
                SendSMS((EditText) findViewById(R.id.ettenthPhone), "ettenththPhone");
                break;
            default:
                break;
        }

    }
/*
*
public void onClickFeature (View v)
{
    int id = v.getId ();
    switch (id) {
      case R.id.home_btn_feature1 :
           startActivity (new Intent(getApplicationContext(), EmergencyCallActivity.class));
           break;
      case R.id.home_btn_feature2 :
           startActivity (new Intent(getApplicationContext(), PhoneBook.class));
           break;
      case R.id.home_btn_feature3 :
           startActivity (new Intent(getApplicationContext(), EmergencyTextActivity.class));
           break;
      case R.id.home_btn_feature4 :
           startActivity (new Intent(getApplicationContext(), MapActivity.class));
           break;
      case R.id.home_btn_feature5 :
           startActivity (new Intent(getApplicationContext(), RssFeedsActivity.class));
           break;
      case R.id.home_btn_feature6 :
           startActivity (new Intent(getApplicationContext(), PositionTrackerActivity.class));
           break;
        case R.id.home_btn_feature7 :
           startActivity (new Intent(getApplicationContext(), SMSTrackersActivity.class));
           break;
      default:
    	   break;
    }
}

*
* */

    @Override
    public void onBackPressed() {
        animation(R.id.smsTrackerLayout);

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


}
