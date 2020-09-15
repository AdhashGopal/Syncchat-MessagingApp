package com.chatapp.android.core.socket;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.chatapp.android.app.utils.AppUtils;
import com.chatapp.android.core.SessionManager;

import java.util.Timer;

/**
 * created by  Adhash Team on 3/21/2018.
 */
public class IdentifyAppKilled extends Service {

    private static final String TAG = "UEService";
    private Timer timer;
    private static final int delay = 1000; // delay for 1 sec before first start
    private static final int period = 10000;
    SessionManager session;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Service start & pass the value for MessageService
     *
     * @param intent  getting activity value from intent
     * @param flags   input value(flags)
     * @param startId input value(startId)
     * @return response value
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("ClearFromRecentService", "Service Started");
        session = SessionManager.getInstance(IdentifyAppKilled.this);
        // Toast.makeText(getApplicationContext(),"Back Service Started",Toast.LENGTH_LONG).show();
        if (session.isLoginKeySent()) {

            if (!MessageService.isStarted()) {
                Intent i = new Intent(IdentifyAppKilled.this, MessageService.class);
                startService(i);
            }

        }


        return Service.START_STICKY;
    }

    /**
     * call static start service method based on MessageService
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        if (session.isLoginKeySent()) {
            if (!MessageService.isStarted()) {
                AppUtils.startService(IdentifyAppKilled.this, MessageService.class);
            }

        }

        Log.e("ClearFromRecentService", "Service Destroyed");
    }

    /**
     * Task was completed
     *
     * @param rootIntent getting activity value from intent
     */
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");

        //Toast.makeText(getApplicationContext(),"App Killed",Toast.LENGTH_LONG).show();

        if (session != null && session.isLoginKeySent()) {

            Intent i = new Intent(IdentifyAppKilled.this, MessageService.class);
            startService(i);

        }
        // stopSelf();
    }

}