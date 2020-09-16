package com.chatapp.synchat.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.socket.MessageService;

/**
 * created by  Adhash Team on 9/19/2017.
 */
public class DeviceBootCompletedReceiver extends BroadcastReceiver {

    /**
     * Checking the device boot or not and update the value
     *
     * @param context current activity
     * @param intent pass the value one to another activity
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())
                && !MessageService.isStarted() && SessionManager.getInstance(context).getBackupRestored()) { // Only start service after login completed
            // Marshmallow+
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                Intent restartServiceIntent = new Intent(context, MessageService.class);
                restartServiceIntent.setPackage(context.getPackageName());
                PendingIntent restartServicePendingIntent = PendingIntent.getService(context, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmService = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmService != null) {
                    alarmService.set(
                            AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime() + 500,
                            restartServicePendingIntent);
                }
            } else {
                //below Marshmallow{
                context.startService(new Intent(context, MessageService.class));
            }
        }
    }
}
