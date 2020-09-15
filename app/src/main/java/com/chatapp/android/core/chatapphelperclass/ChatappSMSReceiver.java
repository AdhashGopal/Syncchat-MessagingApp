package com.chatapp.android.core.chatapphelperclass;

import android.content.Intent;

import android.os.Bundle;

import android.telephony.SmsManager;

import android.telephony.SmsMessage;

import android.content.BroadcastReceiver;

import android.content.Context;

import android.util.Log;

/**
 */
public abstract class ChatappSMSReceiver extends BroadcastReceiver {

    /* Service to auto matically read the SMS for verifying the account of the user */
    final SmsManager sms = SmsManager.getDefault();


    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();
        try {

            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");
                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();
                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();
                    if (message != null) {
                        int length = message.length();
                        message = message.substring(length - 4, length);

                        onSmsReceived(message);
                        abortBroadcast();
                    }
                }
            }
        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }

    protected abstract void onSmsReceived(String s);

}
