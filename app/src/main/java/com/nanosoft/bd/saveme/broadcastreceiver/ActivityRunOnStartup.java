package com.nanosoft.bd.saveme.broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.nanosoft.bd.saveme.activity.WelcomeScreenActivity;
import com.nanosoft.bd.saveme.service.BackService;
import com.nanosoft.bd.saveme.service.CallingService;
import com.nanosoft.bd.saveme.service.EmergencyTextService;
import com.nanosoft.bd.saveme.activity.HomeActivity;
import com.nanosoft.bd.saveme.service.LocationService;

import static android.content.Context.MODE_PRIVATE;

public class ActivityRunOnStartup extends BroadcastReceiver {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;




    @Override
    public void onReceive(Context context, Intent intent) {


        sharedPreferences = context.getSharedPreferences("SaveData", MODE_PRIVATE);
        editor = sharedPreferences.edit();



            if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {

                int switchStatusForECall = sharedPreferences.getInt("switchStatuss", 0);

                int switchStatusForEText = sharedPreferences.getInt("switchStatus", 0);



//                Intent serviceIntent1 = new Intent(context, CallingService.class);
//                Intent serviceIntent2 = new Intent(context, EmergencyTextService.class);
                Intent serviceIntent3 = new Intent(context, LocationService.class);
                Intent serviceIntent4 = new Intent(context, BackService.class);
                Intent i = new Intent(context, WelcomeScreenActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


                if (switchStatusForECall==1) {
                    Intent serviceIntent1 = new Intent(context, CallingService.class);
                    context.startService(serviceIntent1);
                }if (switchStatusForEText==1){
                    Intent serviceIntent2 = new Intent(context, EmergencyTextService.class);
                    context.startService(serviceIntent2);
                }


//                context.startService(serviceIntent1);
//                context.startService(serviceIntent2);
                context.startService(serviceIntent3);
                context.startService(serviceIntent4);
                context.startActivity(i);
            }
        }
    }

