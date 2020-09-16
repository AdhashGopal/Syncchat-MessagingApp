package com.chatapp.synchat.app.calls;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

/**
 * created by  Adhash Team on 8/2/2017.
 */

public class CallNotifyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent service = new Intent();
        service.setComponent(new ComponentName(context, CallNotifyService.class));
        context.stopService(service);
    }

}
