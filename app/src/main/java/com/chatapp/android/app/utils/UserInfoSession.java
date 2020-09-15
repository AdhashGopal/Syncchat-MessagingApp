package com.chatapp.android.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.chatapp.android.core.SessionManager;

/**
 * created by  Adhash Team on 8/2/2017.
 */
public class UserInfoSession {

    private Context mContext;
    private SharedPreferences userInfoPref;
    private String mCurrentUSerId;

    private final String USER_INFO_PREF = "UserInfoPref";

    /**
     * constructor
     *
     * @param context current activity
     */
    public UserInfoSession(Context context) {
        this.mContext = context;
        userInfoPref = mContext.getSharedPreferences(USER_INFO_PREF, Context.MODE_PRIVATE);
        mCurrentUSerId = SessionManager.getInstance(context).getCurrentUserID();
    }

    /**
     * clear local Data
     */
    public void clearData() {
        SharedPreferences.Editor userInfoPrefEditor = userInfoPref.edit();
        userInfoPrefEditor.clear();
        userInfoPrefEditor.apply();
    }

    /**
     * update Chat ConvId
     *
     * @param docId      input value (docId)
     * @param receiverId input value (receiverId)
     * @param convId     input value (convId)
     */
    public void updateChatConvId(String docId, String receiverId, String convId) {
        if (!mCurrentUSerId.equalsIgnoreCase(receiverId)) {
            String hasConvKey = docId.concat("-hasCovId");
            String convKey = docId.concat("-chatCovId");
            String receiverKey = convId.concat("-chatUserId");

            SharedPreferences.Editor userInfoPrefEditor = userInfoPref.edit();
            userInfoPrefEditor.putBoolean(hasConvKey, true);
            userInfoPrefEditor.putString(convKey, convId);
            userInfoPrefEditor.putString(receiverKey, receiverId);
            userInfoPrefEditor.apply();
        }
    }

    /**
     * ChatConvId
     *
     * @param docId input value ()
     * @return value
     */
    public boolean hasChatConvId(String docId) {
        String hasConvKey = docId.concat("-hasCovId");
        return userInfoPref.getBoolean(hasConvKey, false);
    }

    /**
     * get Chat ConvId
     *
     * @param docId input value (docId)
     * @return value
     */
    public String getChatConvId(String docId) {
        String convKey = docId.concat("-chatCovId");
        return userInfoPref.getString(convKey, "");
    }

    /**
     * get Receiver Id By ConvId
     *
     * @param convId input value (convId)
     * @return value
     */
    public String getReceiverIdByConvId(String convId) {
        String receiverKey = convId.concat("-chatUserId");
        return userInfoPref.getString(receiverKey, "");
    }

    /**
     * update User Msisdn
     *
     * @param toUserId input value (toUserId)
     * @param msisdn   input value (msisdn)
     */
    public void updateUserMsisdn(String toUserId, String msisdn) {
        String msisdnKey = toUserId.concat("-Msisdn");
        SharedPreferences.Editor userInfoPrefEditor = userInfoPref.edit();
        userInfoPrefEditor.putString(msisdnKey, msisdn);
        userInfoPrefEditor.apply();
    }

    public String getUserMsisdn(String toUserId) {
        String msisdnKey = toUserId.concat("-Msisdn");
        return userInfoPref.getString(msisdnKey, "");
    }

}
