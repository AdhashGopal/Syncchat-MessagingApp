package com.chatapp.synchat.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.chatapp.synchat.core.model.GroupInfoPojo;
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
public class GroupInfoSession {

    private Context mContext;
    private SharedPreferences groupInfoPref;
    private SharedPreferences.Editor groupInfoPrefEditor;

    private final String GROUP_INFO_PREF = "GroupInfoPref";

    private Gson gson;
    private GsonBuilder gsonBuilder;

    /**
     * Create constructor
     *
     * @param context current activity
     */
    public GroupInfoSession(Context context) {
        this.mContext = context;
        groupInfoPref = mContext.getSharedPreferences(GROUP_INFO_PREF, Context.MODE_PRIVATE);
    }

    /**
     * check hasinfo value from Group Info
     *
     * @param docId input value (docId)
     * @return value
     */
    public boolean hasGroupInfo(String docId) {
        String infoKey = docId.concat("-hasInfo");
        return groupInfoPref.getBoolean(infoKey, false);
    }

    /**
     * get GroupInfo
     *
     * @param docId input value (docId)
     * @return value
     */
    public GroupInfoPojo getGroupInfo(String docId) {
        String dataKey = docId.concat("-groupData");
        String data = groupInfoPref.getString(dataKey, "");

        if (!data.equals("")) {
            gsonBuilder = new GsonBuilder();
            gson = gsonBuilder.create();

            GroupInfoPojo infoPojo = gson.fromJson(data, GroupInfoPojo.class);
            return infoPojo;
        } else {
            return null;
        }

    }

    /**
     * update GroupInfo
     *
     * @param docId    input value (docId)
     * @param infoPojo getting value from model class
     */
    public void updateGroupInfo(String docId, GroupInfoPojo infoPojo) {
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        try {
            JSONObject dataObj;
            if (hasGroupInfo(docId)) {
                GroupInfoPojo oldInfo = getGroupInfo(docId);
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

            String dataKey = docId.concat("-groupData");
            String infoKey = docId.concat("-hasInfo");
            groupInfoPrefEditor = groupInfoPref.edit();

            groupInfoPrefEditor.putBoolean(infoKey, true);
            groupInfoPrefEditor.putString(dataKey, dataObj.toString());
            groupInfoPrefEditor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void deleteGroupInfo(String docId) {
        String dataKey = docId.concat("-groupData");
        String infoKey = docId.concat("-hasInfo");

        groupInfoPrefEditor = groupInfoPref.edit();
        groupInfoPrefEditor.remove(dataKey);
        groupInfoPrefEditor.remove(infoKey);
        groupInfoPrefEditor.apply();
    }

    /**
     * get Group List
     * @return value
     */
    public List<String> getGroupIdList() {
        List<String> groupIdList = new ArrayList<>();

        Map<String, ?> allEntries = groupInfoPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String key = entry.getKey();
            if (key.contains("-groupData")) {
                gsonBuilder = new GsonBuilder();
                gson = gsonBuilder.create();
                String value = entry.getValue().toString();

                GroupInfoPojo infoPojo = gson.fromJson(value, GroupInfoPojo.class);
                if (infoPojo.isLiveGroup() && infoPojo.getGroupId() != null && !infoPojo.getGroupId().equals("")) {
                    groupIdList.add(infoPojo.getGroupId());
                }
            }
        }
        return groupIdList;
    }
}
