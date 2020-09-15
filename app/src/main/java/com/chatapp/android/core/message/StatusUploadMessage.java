package com.chatapp.android.core.message;

import android.content.Context;
import android.util.Log;

import com.chatapp.android.core.model.MessageItemChat;

import org.json.JSONObject;


public class StatusUploadMessage extends BaseMessage implements Status {

    private Context context;
    private static final String TAG = StatusUploadMessage.class.getSimpleName();
    public StatusUploadMessage(Context context) {
        super(context);
        this.context = context;
    }



    public MessageItemChat createMessageItem(boolean isSelf, String message, String status, String receiverUid, String senderName) {
        item = new MessageItemChat();
        item.setMessageId( tsForServerEpoch);
        item.setIsSelf(isSelf);
        item.setStarredStatus(MessageFactory.MESSAGE_UN_STARRED);
        item.setTextMessage(message);
        item.setDeliveryStatus(status);
        item.setReceiverUid(receiverUid);
        //item.setReceiverID(to);
        item.setMessageType("" + type);
        item.setSenderName(senderName);
        item.setTS(getShortTimeFormat());

        return item;
    }

    @Override
    public Object getMessageObject(String payload) {

        setId(from);
        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("type", MessageFactory.text);
            object.put("payload", payload);
            object.put("id", tsForServerEpoch);
            object.put("toDocId", getId() + "-" + tsForServerEpoch);


        } catch (Exception e) {
            Log.e(TAG, "getMessageObject: ",e );
        }
        return object;
    }


}
