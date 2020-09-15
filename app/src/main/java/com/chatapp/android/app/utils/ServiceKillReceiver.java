package com.chatapp.android.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * created by  Adhash Team on 3/26/2018.
 */

public class ServiceKillReceiver extends BroadcastReceiver {
    private static final String TAG = "ServiceKillReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive: ");
        AppUtils.restartService(context);
    }
}
