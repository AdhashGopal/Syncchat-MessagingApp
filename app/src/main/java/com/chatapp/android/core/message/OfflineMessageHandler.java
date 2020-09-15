package com.chatapp.android.core.message;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.model.OfflineRetryEventPojo;
import com.chatapp.android.core.socket.MessageService;
import com.chatapp.android.core.socket.SocketManager;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * created by  Adhash Team on 6/2/2017.
 */
public class OfflineMessageHandler {

    private MessageService callBack;
    private Context mContext;
    private String mCurrentUserId;
    private boolean isOfflineInProgress;
    private ArrayList<String> offlineEventNames = new ArrayList<>();

    private int msgIndex;
    private Handler msgHandler = new Handler();
    private Runnable msgRunnable;

    /**
     * create constructor
     *
     * @param messageService getting message from Service class
     */
    public OfflineMessageHandler(MessageService messageService) {
        this.mContext = messageService;
        this.callBack = messageService;
        mCurrentUserId = SessionManager.getInstance(messageService).getCurrentUserID();
        setOfflineEventNames();
    }

    /**
     * Offline Message handling from group message
     *
     * @param eventName input value(eventName)
     * @param message   input value(message)
     * @return response value
     */
    public boolean isOfflineMessageEvent(String eventName, Object message) {
        if (eventName.equalsIgnoreCase(SocketManager.EVENT_REPLY_MESSAGE) ||
                eventName.equalsIgnoreCase(SocketManager.EVENT_MESSAGE) ||
                eventName.equalsIgnoreCase(SocketManager.EVENT_NEW_FILE_MESSAGE)) {
            return true;
        } else if (eventName.equalsIgnoreCase(SocketManager.EVENT_GROUP)) {
            try {
                JSONObject object = (JSONObject) message;
                String actionType = object.getString("groupType");

                return (actionType.equals(SocketManager.ACTION_EVENT_GROUP_MESSAGE) ||
                        actionType.equals(SocketManager.ACTION_EVENT_GROUP_REPlY_MESSAGE));
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return false;
        }

    }

    /**
     * handle offline uploading image
     */
    private void setOfflineEventNames() {
        offlineEventNames.add(SocketManager.EVENT_IMAGE_UPLOAD);
//        offlineEventNames.add(SocketManager.EVENT_FILE_UPLOAD);
//        offlineEventNames.add(SocketManager.EVENT_FILE_DOWNLOAD);
//        offlineEventNames.add(SocketManager.EVENT_START_FILE_DOWNLOAD);
        offlineEventNames.add(SocketManager.EVENT_CHAT_LOCK_FROM_WEB);
    }

    /**
     * store Offline Event Data & update from database
     *
     * @param eventName input value(eventName)
     * @param message   input value(message)
     */
    public void storeOfflineEventData(String eventName, Object message) {
        if (isOfflineEvent(eventName)) {
            OfflineRetryEventPojo retryEventPojo = new OfflineRetryEventPojo();
            retryEventPojo.setEventName(eventName);
            retryEventPojo.setEventObject(message.toString());

            CoreController.getDBInstance(mContext).updateOfflineEvents(retryEventPojo);
        }
    }

    /**
     * get offline event name
     *
     * @param eventName input value(eventName)
     * @return response value
     */
    private boolean isOfflineEvent(String eventName) {
        return (offlineEventNames.contains(eventName));
    }

    /**
     * handling send Offline Messages
     */
    public void sendOfflineMessages() {
        if (!isOfflineInProgress) {
            msgIndex = 0;
            performSend();
        }
    }

    /**
     * Handling the offline message from database to store the value
     */
    private void performSend() {

        try {
            isOfflineInProgress = true;

            final ArrayList<OfflineRetryEventPojo> offlineStoredData = CoreController.getDBInstance(
                    mContext).getOfflineEvents(mCurrentUserId);

            for (int i = 0; i < offlineStoredData.size(); i++) {
                OfflineRetryEventPojo data = offlineStoredData.get(i);
                callBack.sendOfflineMessage(data.getEventObject(), data.getEventName());
                Log.d("OfflineDataLoadTime", data.getEventObject().toString());
            }

            final ArrayList<OfflineRetryEventPojo> offlineMsgData = CoreController.getDBInstance(
                    mContext).getSendNewMessage(mCurrentUserId);

            if (offlineMsgData.size() > 0) {
                msgRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (offlineMsgData.size() > msgIndex) {
                            OfflineRetryEventPojo pojo = offlineMsgData.get(msgIndex);
                            callBack.sendOfflineMessage(pojo.getEventObject(), pojo.getEventName());
                            msgIndex++;
                            if (msgIndex == offlineMsgData.size()) {
                                isOfflineInProgress = false;
                            } else {
                                msgHandler.postDelayed(this, 750);
                            }
                        } else {
                            isOfflineInProgress = false;
                        }
                    }
                };
                msgHandler.postDelayed(msgRunnable, 750);
            } else {
                isOfflineInProgress = false;
            }


       /* for (int i = 0; i < offlineMsgData.size(); i++) {
            OfflineRetryEventPojo data = offlineMsgData.get(i);
            callBack.sendOfflineMessage(data.getEventObject(), data.getEventName());
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Log.d("OfflineDataLoadTime", data.getEventObject().toString());
        }*/
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * Remove callback function
     */
    public void onServiceDestroy() {
        if (msgRunnable != null) {
            msgHandler.removeCallbacks(msgRunnable);
        }
    }

    /**
     * to handle offline send StarredMessages
     */
    public void sendOfflineStarredMessages() {
        try {
            MessageDbController db = CoreController.getDBInstance(mContext);
            ArrayList<JSONObject> starredList = db.getAllTempStarredMessage(mCurrentUserId);
            for (int i = 0; i < starredList.size(); i++) {
                callBack.sendOfflineMessage(starredList.get(i), SocketManager.EVENT_STAR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * To handling send Offline Deleted Messages
     */
    public void sendOfflineDeletedMessages() {
        try {

            MessageDbController db = CoreController.getDBInstance(mContext);
            ArrayList<JSONObject> deletedList = db.getAllTempDeletedMessage(mCurrentUserId);
            for (int i = 0; i < deletedList.size(); i++) {
                callBack.sendOfflineMessage(deletedList.get(i), SocketManager.EVENT_REMOVE_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
