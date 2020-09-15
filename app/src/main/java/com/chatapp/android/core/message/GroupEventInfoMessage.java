package com.chatapp.android.core.message;

import android.content.Context;

import com.chatapp.android.core.model.MessageItemChat;

import org.json.JSONException;
import org.json.JSONObject;


public class GroupEventInfoMessage extends BaseMessage implements Message {

    private Context context;

    /**
     * create constructor
     *
     * @param context current activity
     */
    public GroupEventInfoMessage(Context context) {
        super(context);
        this.context = context;
    }


    /**
     * get single Message chat
     *
     * @param to           input value(to)
     * @param payload      input value(payload)
     * @param isSecretchat input value(isSecretchat)
     * @return response value
     */
    @Override
    public Object getMessageObject(String to, String payload, Boolean isSecretchat) {
        this.to = to;
        setId(from + "-" + to);
        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("to", to);
            object.put("type", MessageFactory.group_event_info);
            object.put("payload", payload);
            object.put("id", tsForServerEpoch);
            object.put("toDocId", getId() + "-" + tsForServerEpoch);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * get Group Message
     *
     * @param to        input value(to)
     * @param payload   input value(payload)
     * @param groupName input value(groupName)
     * @return response value
     */
    @Override
    public Object getGroupMessageObject(String to, String payload, String groupName) {
        this.to = to;
        setId(from + "-" + to + "-g");
        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("to", to);
            object.put("type", MessageFactory.group_event_info);
            object.put("payload", payload);
            object.put("id", tsForServerEpoch);
            object.put("convId", to);
            object.put("toDocId", getId() + "-" + tsForServerEpoch);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * create chat MessageItem
     *
     * @param eventType          input value(eventType)
     * @param isSelf             input value(isSelf)
     * @param message            input value(message)
     * @param status             input value(status)
     * @param receiverUid        input value(receiverUid)
     * @param groupName          input value(groupName)
     * @param createdById        input value(createdById)
     * @param createdToId        input value(createdToId)
     * @param senderoriginalname input value(senderoriginalname)
     * @return response value
     */
    public MessageItemChat createMessageItem(int eventType, boolean isSelf, String message, String status, String receiverUid,
                                             String groupName, String createdById, String createdToId, String senderoriginalname) {
        item = new MessageItemChat();
        item.setMessageId(getId() + "-" + tsForServerEpoch);
        item.setIsSelf(isSelf);
        item.setStarredStatus(MessageFactory.MESSAGE_UN_STARRED);
        item.setTextMessage(message);
        item.setDeliveryStatus(status);
        item.setReceiverUid(receiverUid);
        item.setReceiverID(to);
        item.setMessageType("" + type);
        item.setGroupEventType("" + eventType);
        item.setSenderName(groupName);
        item.setGroupName(groupName);
        item.setTS(getShortTimeFormat());
        item.setIsInfoMsg(true);
        item.setGroup(true);
        item.setCreatedByUserId(createdById);
        item.setCreatedToUserId(createdToId);
        item.setReceiverID(receiverUid);
        item.setSenderOriginalName(senderoriginalname);

        return item;
    }

}

