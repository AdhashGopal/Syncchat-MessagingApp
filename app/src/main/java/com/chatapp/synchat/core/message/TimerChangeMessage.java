package com.chatapp.synchat.core.message;

import android.content.Context;

import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.model.MessageItemChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * created by  Adhash Team on 4/19/2017.
 */
public class TimerChangeMessage extends BaseMessage implements Message {
    private Context context;

    /**
     * create constructor
     *
     * @param context current activity
     */
    public TimerChangeMessage(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * get single Message
     *
     * @param to           input value(to)
     * @param payload      input value(payload)
     * @param time         input value(time)
     * @param isSecretChat input value(isSecretChat)
     * @return response value
     */
    public Object getMessageObject(String to, String payload, String time, Boolean isSecretChat) {
        this.to = to;
        setId(from + "-" + to);

        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("to", to);
            object.put("type", MessageFactory.timer_change);
            object.put("payload", payload);
            object.put("incognito_timer_mode", payload);
            object.put("incognito_timer", time);
            object.put("id", tsForServerEpoch);
            object.put("toDocId", getId() + "-" + tsForServerEpoch);

            if (isSecretChat) {
//                    setId(getId() + "-secret");
                object.put("chat_type", MessageFactory.CHAT_TYPE_SECRET);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    @Override
    public Object getMessageObject(String to, String payload, Boolean isSecretchat) {
        return null;
    }

    /**
     * get Group Message
     *
     * @param to input value(to)
     * @param payload input value(payload)
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
            object.put("type", MessageFactory.timer_change);
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
     * create MessageItem and update status
     *
     * @param isSelf input value(isSelf)
     * @param message input value(message)
     * @param status input value(status)
     * @param receiverUid input value(receiverUid)
     * @param senderName input value(senderName)
     * @param timer input value(timer)
     * @return response value
     */
    public MessageItemChat createMessageItem(boolean isSelf, String message, String status, String receiverUid,
                                             String senderName, String timer) {
        item = new MessageItemChat();
        item.setMessageId(getId() + "-" + tsForServerEpoch);
        item.setIsSelf(isSelf);
        item.setStarredStatus(MessageFactory.MESSAGE_UN_STARRED);
        item.setTextMessage(message);
        item.setDeliveryStatus(status);
        item.setReceiverUid(receiverUid);
        item.setReceiverID(to);
        item.setMessageType("" + type);
        item.setSenderName(senderName);
        item.setTS(getShortTimeFormat());
        item.setSecretTimer(timer);
        item.setSecretTimeCreatedBy(from);
        item.setIsDate(true);

        if (getId() != null && getId().contains("-g")) {
            GroupInfoSession groupInfoSession = new GroupInfoSession(context);
            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(getId());

            if (infoPojo != null) {
                String[] groupMembers = infoPojo.getGroupMembers().split(",");
                try {
                    JSONArray arrMembers = new JSONArray();
                    for (String member : groupMembers) {
                        JSONObject userObj = new JSONObject();
                        userObj.put("UserId", member);
                        userObj.put("DeliverStatus", MessageFactory.DELIVERY_STATUS_SENT);
                        if (from.equals(member)) {
                            userObj.put("DeliverStatus", MessageFactory.DELIVERY_STATUS_READ);
                        }
                        userObj.put("DeliverTime", "");
                        userObj.put("ReadTime", "");
                        arrMembers.put(userObj);
                    }
                    JSONObject deliverObj = new JSONObject();
                    deliverObj.put("GroupMessageStatus", arrMembers);
                    item.setGroupMsgDeliverStatus(deliverObj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return item;
    }

}

