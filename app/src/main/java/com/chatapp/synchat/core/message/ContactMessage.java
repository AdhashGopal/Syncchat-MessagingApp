package com.chatapp.synchat.core.message;

import android.content.Context;

import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.socket.SocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 11/2/2016.
 */
public class ContactMessage extends BaseMessage {

    private Context context;

    /**
     * Create constructor
     *
     * @param context current activity
     */
    public ContactMessage(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * get single Message
     * @param to input value(to)
     * @param payload input value(payload)
     * @param contactChatappId input value(contactChatappId)
     * @param contactName input value(contactName)
     * @param contactNumber input value(contactNumber)
     * @param contactDetails input value(contactDetails)
     * @param isSecretChat input value(isSecretChat)
     * @return response value
     */
    public Object getMessageObject(String to, String payload, String contactChatappId,
                                   String contactName, String contactNumber,String contactDetails, boolean isSecretChat) {
        this.to  = to;
        setId(from + "-" + to);
        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("to", to);
            object.put("type", MessageFactory.contact);
            object.put("payload", payload);
            object.put("id", tsForServerEpoch);
            object.put("toDocId", getId() + "-" + tsForServerEpoch);
            if(contactChatappId != null && !contactChatappId.equals("")) {
                object.put("createdTo", contactChatappId);
            }
            object.put("contact_name", contactName);
            object.put("createdTomsisdn", contactNumber);
            object.put("contactDetails",contactDetails);

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
     * @param to input value(to)
     * @param payload input value(payload)
     * @param groupName input value(groupName)
     * @param contactChatappId input value(contactChatappId)
     * @param contactName input value(contactName)
     * @param contactNumber input value(contactNumber)
     * @param contactDetails input value(contactDetails)
     * @return response value
     */
    public Object getGroupMessageObject(String to, String payload, String groupName, String contactChatappId,
                                        String contactName, String contactNumber,String contactDetails) {
        this.to  = to;
        setId(from + "-" + to + "-g");
        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("to", to);
            object.put("type", MessageFactory.contact);
            object.put("payload", payload);
            object.put("id", tsForServerEpoch);
            object.put("toDocId", getId() + "-" + tsForServerEpoch);
            object.put("groupType", SocketManager.ACTION_EVENT_GROUP_MESSAGE);
            object.put("userName", groupName);
            if(contactChatappId != null && !contactChatappId.equals("")) {
                object.put("createdTo", contactChatappId);
            }
            object.put("contact_name", contactName);
            object.put("createdTomsisdn", contactNumber);
            object.put("contactDetails",contactDetails);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * create Message Item status
     * @param isSelf input value(isSelf)
     * @param message input value(message)
     * @param status input value(status)
     * @param receiverUid input value(receiverUid)
     * @param senderName input value(senderName)
     * @param contactName input value(contactName)
     * @param contactNo input value(contactNo)
     * @param contactChatappId input value(contactChatappId)
     * @param contactDetail input value(contactDetail)
     * @return response value
     */
    public MessageItemChat createMessageItem(boolean isSelf, String message, String status,
                                             String receiverUid, String senderName, String contactName,
                                             String contactNo, String contactChatappId,String contactDetail) {
        item = new MessageItemChat();
        item.setMessageId(getId() + "-" + tsForServerEpoch);
        item.setIsSelf(isSelf);
        item.setDeliveryStatus(status);
        item.setReceiverUid(receiverUid);
        item.setReceiverID(to);
        item.setMessageType("" + type);
//        item.setContactInfo(message);
        item.setSenderName(senderName);
        item.setTS(getShortTimeFormat());
        item.setContactName(contactName);
        item.setContactNumber(contactNo);
        item.setDetailedContacts(contactDetail);
        item.setContactchatappId(contactChatappId);

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