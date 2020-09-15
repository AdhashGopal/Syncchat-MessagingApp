package com.chatapp.android.core.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.chatapp.android.app.utils.ConnectivityInfo;

import org.greenrobot.eventbus.EventBus;


/**
 *
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    public static String TAG = NetworkChangeReceiver.class.getSimpleName();
    private static MyInternetListener myInternetListener;

    public static void setListener(MyInternetListener listener) {
        myInternetListener = listener;
    }

    /**
     * Check internet connection
     *
     * @param context current activity
     * @return check connectivity response
     */
    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * getting online / offline response
     *
     * @param context current activity
     * @param intent  getting internet status to pass the value
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            if (isOnline(context)) {
                Log.e("keshav", "Online Connect Intenet ");
                EventBus.getDefault().post("Online");
            } else {
                EventBus.getDefault().post("Offline");
                Log.e("keshav", "Conectivity Failure !!! ");
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        /*if (isNetworkAvailable(context)) {
            Log.d(TAG, "Connected to a network");
            if(myInternetListener!=null){
                myInternetListener.networkAvailable(true);
            }
        } else {
            if(myInternetListener!=null){
                myInternetListener.networkAvailable(true);
            }
            Log.d(TAG, "Not Connected to a network");
        }*/
        if (!ConnectivityInfo.isInternetConnected(context)) {
            Toast.makeText(context, "Internet disconnected please try again", Toast.LENGTH_LONG).show();

        }
    }

    /**
     * Check internet connection
     *
     * @param context current activity
     * @return connection status
     */
    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
}
