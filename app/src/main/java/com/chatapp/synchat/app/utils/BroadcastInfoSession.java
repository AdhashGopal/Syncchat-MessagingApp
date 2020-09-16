package com.chatapp.synchat.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.chatapp.synchat.core.model.BroadcastInfoPojo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * created by  Adhash Team on 1/28/2017.
 */
public class BroadcastInfoSession {

    private Context mContext;
    public SharedPreferences broadcastInfoPref;
    private SharedPreferences.Editor broadcastInfoPrefEditor;

    private final String BROADCAST_INFO_PREF = "BroadcastInfoPref";

    private Gson gson;
    private GsonBuilder gsonBuilder;

    /**
     * constructor
     *
     * @param context current activity
     */
    public BroadcastInfoSession(Context context) {
        this.mContext = context;
        broadcastInfoPref = mContext.getSharedPreferences(BROADCAST_INFO_PREF, Context.MODE_PRIVATE);
    }

    /**
     * Group Info data
     *
     * @param docId input value (docId)
     * @return value
     */
    public boolean hasGroupInfo(String docId) {
        String infoKey = docId.concat("-hasInfo");
        return broadcastInfoPref.getBoolean(infoKey, false);
    }

    /**
     * get Broadcast Info
     *
     * @param docId input value (docId)
     * @return value
     */
    public BroadcastInfoPojo getBroadcastInfo(String docId) {
//        String dataKey = docId.concat("-bdData");

        String data = broadcastInfoPref.getString(docId, "");

        if (!data.equals("")) {
            gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();

            BroadcastInfoPojo infoPojo = gson.fromJson(data, BroadcastInfoPojo.class);
            return infoPojo;
        } else {
            return null;
        }

    }

    /**
     * update GroupInfo data
     *
     * @param docId    input value (docId)
     * @param infoPojo getting value from model class
     */
    public void updateGroupInfo(String docId, BroadcastInfoPojo infoPojo) {
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        try {
            JSONObject dataObj;
            if (hasGroupInfo(docId)) {
                BroadcastInfoPojo oldInfo = getBroadcastInfo(docId);
                String data = gson.toJson(oldInfo);
                dataObj = new JSONObject(data);
            } else {
                dataObj = new JSONObject();
            }

            String newData = gson.toJson(infoPojo);
            JSONObject newObj = new JSONObject(newData);

            Iterator<?> keys = newObj.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                dataObj.put(key, newObj.get(key));
            }

//            String dataKey = docId.concat("-bdData");
//            String infoKey = docId.concat("-hasInfo");
            broadcastInfoPrefEditor = broadcastInfoPref.edit();

            //   broadcastInfoPrefEditor.putBoolean(infoKey, true);
            broadcastInfoPrefEditor.putString(docId, dataObj.toString());
            broadcastInfoPrefEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteGroupInfo(String docId) {
        String dataKey = docId.concat("-bdData");
        String infoKey = docId.concat("-hasInfo");

        broadcastInfoPrefEditor = broadcastInfoPref.edit();
        broadcastInfoPrefEditor.remove(dataKey);
        broadcastInfoPrefEditor.remove(infoKey);
        broadcastInfoPrefEditor.apply();
    }

    public List<String> getBroadcastIdList() {
        List<String> broadcastIdList = new ArrayList<>();

        Map<String, ?> allEntries = broadcastInfoPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.contains("-bdData")) {
                gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                String value = entry.getValue().toString();

                BroadcastInfoPojo infoPojo = gson.fromJson(value, BroadcastInfoPojo.class);
                if (infoPojo.isLiveGroup() && infoPojo.getBroadcastId() != null && !infoPojo.getBroadcastId().equals("")) {
                    broadcastIdList.add(infoPojo.getBroadcastId());
                }
            }
        }
        return broadcastIdList;
    }
}
