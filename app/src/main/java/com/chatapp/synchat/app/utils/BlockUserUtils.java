package com.chatapp.synchat.app.utils;

import android.content.Context;

import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * created by  Adhash Team on 8/11/2017.
 */
public class BlockUserUtils {

    /**
     * change User Blocked Status
     *
     * @param context       current activity
     * @param eventBus      input value (eventbus value)
     * @param currentUserId input value (currentUserId)
     * @param toUserId      input value (toUserId)
     * @param isSecretChat  check isSecretChat or not
     */
    public static void changeUserBlockedStatus(Context context, EventBus eventBus, String currentUserId,
                                               String toUserId, boolean isSecretChat) {

        String status = "0";
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(context);

        if (contactDB_sqlite.getBlockedStatus(toUserId, isSecretChat) != null) {
            status = contactDB_sqlite.getBlockedStatus(toUserId, isSecretChat);
        }

        try {
            JSONObject myobj = new JSONObject();

            if (status.equals("1")) {
                myobj.put("status", "0");
            } else {
                myobj.put("status", "1");
            }

            if (isSecretChat) {
                myobj.put("secret_type", "yes");
            }

            myobj.put("from", currentUserId);
            myobj.put("to", toUserId);

            SendMessageEvent messageEvent = new SendMessageEvent();
            messageEvent.setEventName(SocketManager.EVENT_BLOCK_USER);
            messageEvent.setMessageObject(myobj);
            eventBus.post(messageEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
