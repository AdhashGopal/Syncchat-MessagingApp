package com.chatapp.android.core.message;

import android.content.Context;

import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 */
public class ChangeSetController {
    static Context context;

    /**
     * Create constructor
     *
     * @param context current activity
     */
    public ChangeSetController(Context context) {
        ChangeSetController.context = context;
    }

    /**
     * Set change status event method
     *
     * @param status status value
     */
    public static void setChangeStatus(String status) {

        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_CHANGE_ST);
        JSONObject obj = new JSONObject();
        try {
            obj.put("from", SessionManager.getInstance(context).getCurrentUserID());
            obj.put("status", status);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageEvent.setMessageObject(obj);
        EventBus.getDefault().post(messageEvent);
    }

}
