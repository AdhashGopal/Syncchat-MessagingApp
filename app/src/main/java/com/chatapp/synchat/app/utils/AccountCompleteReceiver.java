package com.chatapp.synchat.app.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chatapp.synchat.core.SessionManager;

import java.util.Calendar;

/**
 * created by  Adhash Team on 11/10/2017.
 */
public class AccountCompleteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        SessionManager.getInstance(context).setAccountSyncCompletedTS(Calendar.getInstance().getTimeInMillis());
        Log.d("Myaccountcompleted", getClass().getSimpleName());
    }
}
