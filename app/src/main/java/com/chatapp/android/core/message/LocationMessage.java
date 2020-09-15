package com.chatapp.android.core.message;

import android.content.Context;

import com.chatapp.android.app.utils.GroupInfoSession;
import com.chatapp.android.core.model.GroupInfoPojo;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.socket.SocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class LocationMessage extends BaseMessage implements Message {

    private Context context;

    /**
     * create constructor
     *
     * @param context current activity
     */
    public LocationMessage(Context context) {
        super(context);
        setType(MessageFactory.location);
        this.context = context;
    }


    /**
     * get single Message for location
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
            object.put("type", type);
            object.put("payload", "");
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
     * get Location message
     *
     * @param msgObj input value(msgObj)
     * @param addressName input value(addressName)
     * @param address input value(address)
     * @param locationUrl input value(locationUrl)
     * @param thumbUrl input value(thumbUrl)
     * @param locationImgThumb input value(locationImgThumb)
     * @return response value
     */
    public Object getLocationObject(JSONObject msgObj, String addressName, String address,
                                    String locationUrl, String thumbUrl, String locationImgThumb) {

        JSONObject locationObj = new JSONObject();
        try {
            locationObj.put("title", addressName);
            locationObj.put("url", locationUrl);
            locationObj.put("description", address);
            locationObj.put("image", thumbUrl);
            locationObj.put("thumbnail_data", locationImgThumb);
            msgObj.put("metaDetails", locationObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgObj;
    }

    /**
     * get Group location Message
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
            object.put("type", MessageFactory.location);
            object.put("payload", payload);
            object.put("id", tsForServerEpoch);
            object.put("toDocId", getId() + "-" + tsForServerEpoch);
            object.put("groupType", SocketManager.ACTION_EVENT_GROUP_MESSAGE);
            object.put("userName", groupName);
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
     * create Message Item for location
     * @param isSelf input value(isSelf)
     * @param message input value(message)
     * @param status input value(status)
     * @param receiverUid input value(receiverUid)
     * @param senderName input value(senderName)
     * @param addressName input value(addressName)
     * @param address input value(address)
     * @param mapUrl input value(mapUrl)
     * @param thumbUrl input value(thumbUrl)
     * @param thumbData input value(thumbData)
     * @param isExpiry input value(isExpiry)
     * @param expiryTime input value(expiryTime)
     * @return response value
     */
    public MessageItemChat createMessageItem(boolean isSelf, String message, String status, String receiverUid,
                                             String senderName, String addressName, String address, String mapUrl,
                                             String thumbUrl, String thumbData, String isExpiry, String expiryTime) {
        item = new MessageItemChat();
        item.setMessageId(getId() + "-" + tsForServerEpoch);
        item.setIsSelf(isSelf);
        item.setTextMessage(message);
        item.setDeliveryStatus(status);
        item.setReceiverID(to);
        item.setReceiverUid(receiverUid);
        item.setMessageType("" + type);
        item.setSenderName(senderName);
        item.setTS(getShortTimeFormat());

        item.setWebLink(mapUrl);
        item.setWebLinkTitle(addressName);
        item.setWebLinkDesc(address);
        item.setWebLinkImgUrl(thumbUrl);
        item.setWebLinkImgThumb(thumbData);
        item.setIsExpiry(isExpiry);
        item.setExpiryTime(expiryTime);

        if (getId().contains("-g")) {
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
