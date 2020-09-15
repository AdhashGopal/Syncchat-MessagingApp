package com.chatapp.android.core.message;

import android.content.Context;

import com.chatapp.android.app.utils.GroupInfoSession;
import com.chatapp.android.core.model.GroupInfoPojo;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.socket.SocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * created by  Adhash Team on 1/31/2017.
 */
public class WebLinkMessage extends BaseMessage implements Message {

    private Context context;

    /**
     * create constructor
     *
     * @param context current activity
     */
    public WebLinkMessage(Context context) {
        super(context);
        setType(MessageFactory.web_link);
        this.context = context;
    }

    /**
     * get single Message
     *
     * @param to           input value(to)
     * @param payload      input value(payload)
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
            object.put("type", MessageFactory.web_link);
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


    public void setIDforBroadcast(String to) {
        this.to = to;
        setId(from + "-" + to + "-g");
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
            object.put("type", MessageFactory.web_link);
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

    /**
     * set WebLink message
     *
     * @param msgObj        input value(msgObj)
     * @param webLink       input value(webLink)
     * @param webLinkTitle  input value(webLinkTitle)
     * @param webLinkDesc   input value(webLinkDesc)
     * @param webLinkImgUrl input value(webLinkImgUrl)
     * @param webLinkThumb  input value(webLinkThumb)
     * @return response value
     */
    public Object getWebLinkObject(JSONObject msgObj, String webLink, String webLinkTitle,
                                   String webLinkDesc, String webLinkImgUrl, String webLinkThumb) {

        JSONObject linkObj = new JSONObject();
        try {
            linkObj.put("title", webLinkTitle);
            linkObj.put("url", webLink);
            linkObj.put("description", webLinkDesc);
            /*linkObj.put("image", webLinkImgUrl);
            linkObj.put("thumbnail_data", webLinkThumb);*/

            msgObj.put("metaDetails", linkObj);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return msgObj;
    }

    /**
     * create MessageItem and update status
     *
     * @param isSelf      input value(isSelf)
     * @param message     input value(message)
     * @param status      input value(status)
     * @param receiverUid input value(receiverUid)
     * @param senderName  input value(senderName)
     * @param isExpiry    input value(isExpiry)
     * @param expiryTime  input value(expiryTime)
     * @return response value
     */
    public MessageItemChat createMessageItem(boolean isSelf, String message, String status, String receiverUid,
                                             String senderName, String webLink, String webLinkTitle, String webLinkDesc,
                                             String webLinkImgUrl, String webLinkThumb, String isExpiry, String expiryTime) {
        item = new MessageItemChat();
        item.setMessageId(getId() + "-" + tsForServerEpoch);
        item.setIsSelf(isSelf);
        item.setStarredStatus(MessageFactory.MESSAGE_UN_STARRED);
        item.setTextMessage(message);
        item.setDeliveryStatus(status);
        item.setReceiverUid(receiverUid);
        item.setMessageType("" + type);
        item.setSenderName(senderName);
        item.setTS(getShortTimeFormat());
        item.setWebLink(webLink);
        item.setReceiverID(to);
        item.setWebLinkTitle(webLinkTitle);
        item.setWebLinkDesc(webLinkDesc);
        item.setWebLinkImgUrl(webLinkImgUrl);
        item.setWebLinkImgThumb(webLinkThumb);
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
