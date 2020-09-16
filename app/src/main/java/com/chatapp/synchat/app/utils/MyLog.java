package com.chatapp.synchat.app.utils;

import android.util.Log;

import com.chatapp.synchat.BuildConfig;


/**
 * created by  Adhash Team on 2/27/2018.
 */

public class MyLog {

    public static void  d(String TAG,String msg){
        if(BuildConfig.DEBUG)
            Log.d(TAG, ""+msg);
    }


    public static void  e(String TAG,String msg,Throwable throwable){
        if(BuildConfig.DEBUG)
            Log.e(TAG, ""+msg,throwable);
    }

}
