package com.nanosoft.bd.saveme.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.nanosoft.bd.saveme.activity.EmergencyCallActivity;
import com.nanosoft.bd.saveme.activity.EmergencyTextActivity;
import com.nanosoft.bd.saveme.service.EmergencyTextService;
import com.nanosoft.bd.saveme.activity.FnFTrackingActivity;
import com.nanosoft.bd.saveme.activity.Operations;
import com.nanosoft.bd.saveme.database.DatabaseManager;
import com.nanosoft.bd.saveme.database.DatabaseModel;

public class SmsReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String pn1 = Operations.getStringFromSharedPreference(context, "etfirstPhone");
        String pn2 = Operations.getStringFromSharedPreference(context, "etsecondPhone");
        String pn3 = Operations.getStringFromSharedPreference(context, "etthirdPhone");
        String pn4 = Operations.getStringFromSharedPreference(context, "etfourthPhone");
        String pn5 = Operations.getStringFromSharedPreference(context, "etfifthPhone");

        String pn6 = Operations.getStringFromSharedPreference(context, "etsixthPhone");
        String pn7 = Operations.getStringFromSharedPreference(context, "etseventhPhone");
        String pn8 = Operations.getStringFromSharedPreference(context, "eteighthPhone");
        String pn9 = Operations.getStringFromSharedPreference(context, "etninththPhone");
        String pn10 = Operations.getStringFromSharedPreference(context, "ettenththPhone");
        /*activity_fnf_tracking*/
        String pn11 = Operations.getStringFromSharedPreference(context, "fnfNumber");
        /*emergencycall*/
        String pn12 = Operations.getStringFromSharedPreference(context, "ePhoneNumbers");
        String emergencyRandom= Operations.getStringFromSharedPreference(context,"emergencyRandom");
  /*emergencycall*/

        /*emergencyText*/
        String pn13 = Operations.getStringFromSharedPreference(context, "vePhoneNumber");
        String pn14 = Operations.getStringFromSharedPreference(context, "vePhoneNumber2");
        String pn15 = Operations.getStringFromSharedPreference(context, "vePhoneNumber3");
        String firstRandom= Operations.getStringFromSharedPreference(context,"FirstRandom");
        String secondRandom= Operations.getStringFromSharedPreference(context,"SecondRandom");
        String thirdRandom= Operations.getStringFromSharedPreference(context,"ThirdRandom");
        /*emergencyText*/

        String smsCode = Operations.getStringFromSharedPreference(context, "smsCode");
        String fnfrandom = Operations.getStringFromSharedPreference(context, "fnfrandom");
        String message = Operations.GetFullAddress(context);// Operations.getStringFromSharedPreference(context, "Address");
        String co = Operations.getStringFromSharedPreference(context, "Coordinate");


       //String address= Operations.GetFullAddress(context);
        String uri = "http://maps.google.com/maps?q=" + co.replace(" ", ",").replace("Lat:", "").replace("Lon:", "");
        StringBuffer smsBody = new StringBuffer();
        smsBody.append(Uri.parse(uri));
        message += smsBody + "\n";
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;

        // To display a Toast whenever there is an SMS.
        //Toast.makeText(context,"Recieved",Toast.LENGTH_LONG).show();

        Object[] pdus = (Object[]) extras.get("pdus");
        for (int i = 0; i < pdus.length; i++) {
            SmsMessage SMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
            String sender = SMessage.getOriginatingAddress();
            String body = SMessage.getMessageBody().toString();

            // A custom Intent that will used as another Broadcast
            String destination = PhoneNumberUtils.compare(sender, pn1) ? pn1 : PhoneNumberUtils.compare(sender, pn2) ? pn2 :
                            PhoneNumberUtils.compare(sender, pn3) ? pn3 : PhoneNumberUtils.compare(sender, pn4) ? pn4 :
                           PhoneNumberUtils.compare(sender, pn5) ? pn5 : PhoneNumberUtils.compare(sender, pn6) ? pn6 :
                            PhoneNumberUtils.compare(sender, pn7) ? pn7 : PhoneNumberUtils.compare(sender, pn8) ? pn8 :
                           PhoneNumberUtils.compare(sender, pn9) ? pn9 : PhoneNumberUtils.compare(sender, pn10) ? pn10 :
                           PhoneNumberUtils.compare(sender, pn11) ? pn11 : PhoneNumberUtils.compare(sender, pn12) ? pn12 :
                            PhoneNumberUtils.compare(sender, pn13) ? pn13 : PhoneNumberUtils.compare(sender, pn14) ? pn14 :
                            PhoneNumberUtils.compare(sender, pn15) ? pn15 : "";

        /*activity_sms_trackers*/
            if (destination.length() > 0 && smsCode.length() > 0 && String.valueOf(smsCode).equals(body)) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(destination, null, message, null, null);

                Toast.makeText(context, "Message Send to " + sender, Toast.LENGTH_SHORT).show();
            }
             /*activity_sms_trackers*/


/*activity_fnf_tracking*/
            if (destination.length() > 0 && fnfrandom.length() > 0 && String.valueOf(fnfrandom).equals(body)) {


                DatabaseManager manager = new DatabaseManager(context);
                DatabaseModel databaseModel = new DatabaseModel(pn11);
                manager.addFnf(databaseModel);

                Intent intentone = new Intent(context.getApplicationContext(), FnFTrackingActivity.class);
                intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentone);
               /* FnFTrackingActivity fnf = new FnFTrackingActivity();
                fnf.PeopulateUserList();*/
                Toast.makeText(context, "FnF user added", Toast.LENGTH_SHORT).show();



            }
            /*activity_fnf_tracking*/



            /*emergencycall*/
            if (destination.length() > 0 && emergencyRandom.length() > 0 && String.valueOf(emergencyRandom).equals(body)) {

                Operations.SaveToSharedPreference(context,"ePhoneNumber",pn12);

                Intent intentone = new Intent(context.getApplicationContext(), EmergencyCallActivity.class);
                intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentone);


                Toast.makeText(context, "Emergency number is verified", Toast.LENGTH_SHORT).show();
            }


            if (destination.length() > 0 && firstRandom.length() > 0 && String.valueOf(firstRandom).equals(body)) {
                context.stopService(new Intent(context, EmergencyTextService.class));
                Operations.SaveToSharedPreference(context,"firstEphoneNumber",pn13);
                context.startService(new Intent(context, EmergencyTextService.class));

                Intent intentone = new Intent(context.getApplicationContext(), EmergencyTextActivity.class);
                intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentone);


                Toast.makeText(context, "Emergency number is verified", Toast.LENGTH_SHORT).show();
            }


            if (destination.length() > 0 && secondRandom.length() > 0 && String.valueOf(secondRandom).equals(body)) {
                context.stopService(new Intent(context, EmergencyTextService.class));
                Operations.SaveToSharedPreference(context,"secondEphoneNumber",pn14);
                context.startService(new Intent(context, EmergencyTextService.class));

                Intent intentone = new Intent(context.getApplicationContext(), EmergencyTextActivity.class);
                intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentone);


                Toast.makeText(context, "Emergency number is verified", Toast.LENGTH_SHORT).show();
            }

            if (destination.length() > 0 && thirdRandom.length() > 0 && String.valueOf(thirdRandom).equals(body)) {
                context.stopService(new Intent(context, EmergencyTextService.class));

                Operations.SaveToSharedPreference(context,"thirdEphoneNumber",pn15);

                context.startService(new Intent(context, EmergencyTextService.class));

                Intent intentone = new Intent(context.getApplicationContext(), EmergencyTextActivity.class);
                intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentone);


                Toast.makeText(context, "Emergency number is verified", Toast.LENGTH_SHORT).show();
            }



            else {
                //Toast.makeText(context, "Emergency number is not verified", Toast.LENGTH_SHORT).show();
            }
            /*emergencycall*/




            Intent in = new Intent("SmsMessage.intent.MAIN").putExtra("get_msg", sender + ":" + body);



            context.sendBroadcast(in);

        }


    }
}
