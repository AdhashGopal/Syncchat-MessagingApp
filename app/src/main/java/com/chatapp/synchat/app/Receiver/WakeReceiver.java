package com.chatapp.synchat.app.Receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


import android.util.Log;

import com.chatapp.synchat.core.socket.MessageService;


/**
 * Created by arjun on 03-01-2017.
 */

public class WakeReceiver extends BroadcastReceiver {
    String phoneState;
    Context applicationcontext;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String lastState;


    @Override
    public void onReceive(Context context, Intent intent) {

        System.out.println("=aaaaa======here service stop");
        try {
            Log.i(SensorRestarterBroadcastReceiver.class.getSimpleName(), "Service Stops! Oooooooooooooppppssssss!!!!");
            context.startService(new Intent(context, MessageService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }

//        applicationcontext=context;
//
//        sharedpreferences =context.getSharedPreferences("CALLLASTSTATE", Context.MODE_PRIVATE);
//        editor = sharedpreferences.edit();
//
//
//        lastState=sharedpreferences.getString("callstate","10");
//        if(lastState.equals("10"))
//        {
//            editor.putString("callstate",TelephonyManager.EXTRA_STATE_IDLE).apply();
//
//        }
//
//
//
//        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        phoneState = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
//
//        lastState = sharedpreferences.getString("callstate", "0");
//
//        if (lastState.equals(phoneState)) {
//
//        } else {
//            if(lastState.equals(TelephonyManager.EXTRA_STATE_IDLE))
//            {
//
//                if (phoneState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//
//                // INCOMING CALL ARRIVES
//
//                   if(CallsActivity.isStarted)
//                   {
//
//                   }
//
//                    Toast.makeText(applicationcontext, "Incoming call arrives", Toast.LENGTH_SHORT).show();
//
//                }
//
//            }
//            else if (phoneState.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
//
//                // CALL ATTENDED
//
//                if (!lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//                    // OUTGOING CALL ATTENDED
////                    editor.putBoolean("isincoming",false).apply();
////                    editor.commit();
//                }
//
//
//            } else if (phoneState.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
//
//                //CALL ENDED
//
//                if (lastState.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
//
//                    // MISSED CALL
//
//
//                    }
//
//
//                    }
//
//            editor.putString("callstate", phoneState).apply();
//            editor.commit();
//        }
    }


}




