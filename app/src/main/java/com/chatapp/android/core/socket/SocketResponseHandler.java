package com.chatapp.android.core.socket;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * created by  Adhash Team 02/04/16.
 */

public abstract class SocketResponseHandler
{
    private Context context;

    public abstract void execute (String event,JSONObject jsonObject) throws JSONException;

    public SocketResponseHandler(Context ctx)
    {
        this.context = ctx;
    }

    public void handleJSONObjectResponse(String event,JSONObject jsonObject) throws Exception
    {
        Log.d("app controller 7","");
        execute(event,jsonObject);

    }
}