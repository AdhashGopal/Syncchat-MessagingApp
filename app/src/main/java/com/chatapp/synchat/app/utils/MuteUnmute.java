package com.chatapp.synchat.app.utils;


import android.content.Context;

import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * created by  Adhash Team on 5/8/2017.
 */
public class MuteUnmute {

    /**
     * To perform Mute event
     *
     * @param eventBus     input value(eventBus)
     * @param from         input value(from)
     * @param to           input value(to)
     * @param convId       input value(convId)
     * @param type         input value(type)
     * @param secretType   input value(secretType)
     * @param status       input value(status)
     * @param option       input value(option)
     * @param notifyStatus input value(notifyStatus)
     */
    public static void muteUnmute(EventBus eventBus, String from, String to, String convId,
                                  String type, String secretType, int status, String option,
                                  int notifyStatus) {
        try {
            JSONObject object = new JSONObject();
            object.put("from", from);
            object.put("status", status);
            object.put("option", option);
            object.put("type", type);
            object.put("notify_status", notifyStatus);

            if (type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_SINGLE)) {
                object.put("to", to);
                object.put("secret_type", secretType);
            }

            if (convId != null && !convId.isEmpty()) {
                object.put("convId", convId);
            }

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_MUTE);
            event.setMessageObject(object);
            eventBus.post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * To perform UnMute event
     *
     * @param context    current activity
     * @param eventBus   input value(eventBus)
     * @param receiverId input value(receiverId)
     * @param chatType   input value(chatType)
     * @param secretType input value(secretType)
     */
    public static void performUnMute(Context context, EventBus eventBus, String receiverId,
                                     String chatType, String secretType) {
        Session session = new Session(context);
        SessionManager sessionManager = SessionManager.getInstance(context);
        UserInfoSession userInfoSession = new UserInfoSession(context);

        String mCurrentUserId = sessionManager.getCurrentUserID();

        String docId = mCurrentUserId + "-" + receiverId;
        String convId = null;

        if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
            docId = docId + "-g";
            convId = receiverId;
        } else {
            if (secretType.equalsIgnoreCase("yes")) {
                docId = docId + "-secret";
            }
            if (userInfoSession.hasChatConvId(docId)) {
                convId = userInfoSession.getChatConvId(docId);
            }
        }

        MuteUnmute.muteUnmute(eventBus, mCurrentUserId, receiverId, convId,
                chatType, secretType, 0, "", 1);

       /* session.setMuteDuration(docId, "");
        sessionManager.setMuteStatus(docId, false);
        session.setNotificationOnMute(docId, true);
        DateFormat dff = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.ENGLISH);
        Date cDate = new Date();
        String reportDate = dff.format(cDate);
        session.puttime(receiverId + "time", reportDate);*/
    }

}
