package com.chatapp.synchat.core.message;

import android.content.Context;
import android.util.Log;

import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.model.MessageItemChat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 10/27/2016.
 */
public class TextMessage extends BaseMessage implements Message {

    private Context context;
    public static final String TAG_KEY = "@@***";
    private static final String TAG = TextMessage.class.getSimpleName();

    /**
     * create constructor
     *
     * @param context current activity
     */
    public TextMessage(Context context) {
        super(context);
        this.context = context;
    }


    /**
     * get single Message
     *
     * @param to input value(to)
     * @param payload input value(payload)
     * @param isSecretChat input value(isSecretChat)
     * @return response value
     */
    @Override
    public Object getMessageObject(String to, String payload, Boolean isSecretChat) {
        this.to = to;
        setId(from + "-" + to);
        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("to", to);
            object.put("type", MessageFactory.text);
            object.put("payload", payload);
            object.put("id", tsForServerEpoch);
            object.put("toDocId", getId() + "-" + tsForServerEpoch);

            if (isSecretChat) {
//                setId(getId() + "-secret");
                object.put("chat_type", MessageFactory.CHAT_TYPE_SECRET);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
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
            object.put("type", MessageFactory.text);
            if (payload.contains(TAG_KEY)) {
                object.put("payload", payload);
                object.put("is_tag_applied", "1");
            } else {
                object.put("payload", payload);
                object.put("is_tag_applied", "0");
            }
            object.put("id", tsForServerEpoch);
            object.put("convId", to);
            object.put("toDocId", getId() + "-" + tsForServerEpoch);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public void setIDforBroadcast(String to) {
        this.to = to;
        setId(from + "-" + to + "-g");
    }

    /**
     * create MessageItem and update status
     *
     * @param isSelf input value(isSelf)
     * @param message input value(message)
     * @param status input value(status)
     * @param receiverUid input value(receiverUid)
     * @param senderName input value(senderName)
     * @param isExpiry input value(isExpiry)
     * @param expiryTime input value(expiryTime)
     * @return response value
     */
    public MessageItemChat createMessageItem(boolean isSelf, String message, String status, String receiverUid, String senderName, String isExpiry, String expiryTime) {
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
        item.setIsExpiry(isExpiry);
        item.setExpiryTime(expiryTime);

        if (getId() != null && getId().contains("-g")) {
            GroupInfoSession groupInfoSession = new GroupInfoSession(context);
            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(getId());

            if (infoPojo != null && infoPojo.getGroupMembers() != null) {
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
                } catch (Exception e) {
                    Log.e(TAG, "createMessageItem: ", e);
                }
            }
        }

        return item;
    }

}
