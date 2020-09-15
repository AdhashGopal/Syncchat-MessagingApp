package com.chatapp.android.app.utils;

/**
 * created by  Adhash Team on 7/10/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.chatapp.android.core.SessionManager;

public class DateChangeBroadcastReceiver extends BroadcastReceiver {

    /**
     * get server time receiver
     * @param context current activity
     * @param intent pass the value one activity to another
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        long deviceTS = System.currentTimeMillis();
        long serverTS = SessionManager.getInstance(context).getServerTS();

        if (serverTS != 0) {
            // Add 1 minute on every minute to Server timestamp
            if(intent.getAction().equalsIgnoreCase(Intent.ACTION_TIME_TICK)) {
                serverTS = serverTS + (60 * 1000);
            }

            long timeDiff = deviceTS - serverTS;
            SessionManager.getInstance(context).setServerTimeDifference(serverTS, timeDiff);
        }
    }
}
