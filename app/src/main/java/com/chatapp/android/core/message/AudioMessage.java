package com.chatapp.android.core.message;

import android.content.Context;

import com.chatapp.android.app.utils.GroupInfoSession;
import com.chatapp.android.core.model.GroupInfoPojo;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.socket.SocketManager;
import com.chatapp.android.core.uploadtoserver.FileUploadDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * Created by Administrator on 10/27/2016.
 */
public class AudioMessage extends BaseMessage implements Message {

    private Context context;

    /**
     * create constructor
     *
     * @param context current activity
     */
    public AudioMessage(Context context) {
        super(context);
        this.context = context;
        setType(MessageFactory.audio);
    }

    /**
     * getting single Message for chat
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
            object.put("type", MessageFactory.picture);
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
     * getting grooup Message for chat
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
            object.put("type", MessageFactory.picture);
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
     * set ID for Broadcast
     *
     * @param to input value(to)
     */
    public void setIDforBroadcast(String to) {
        this.to = to;
        setId(from + "-" + to + "-g");
    }

    /**
     * create group set Message Item
     *
     * @param isSelf      input value(isSelf)
     * @param filePath    input value(filePath)
     * @param duration    input value(duration)
     * @param status      input value(status)
     * @param receiverUid input value(receiverUid)
     * @param senderName  input value(senderName)
     * @param audioFrom   input value(audioFrom)
     * @param isExpiry    input value(isExpiry)
     * @param expiryTime  input value(expiryTime)
     * @return response value
     */
    public MessageItemChat createMessageItem(boolean isSelf, String filePath, String duration, String status,
                                             String receiverUid, String senderName, int audioFrom, String isExpiry, String expiryTime) {
        item = new MessageItemChat();
        item.setMessageId(getId() + "-" + tsForServerEpoch);
        item.setIsSelf(isSelf);
        item.setChatFileLocalPath(filePath);
        item.setAudioPath(filePath);
        item.setDuration(duration);
        item.setDeliveryStatus(status);
        item.setReceiverUid(receiverUid);
        item.setReceiverID(to);
        item.setMessageType("" + type);
        item.setSenderName(senderName);
        item.setTS(getShortTimeFormat());
        item.setFileBufferAt(0);
        item.setaudiotype(audioFrom);
        item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_COMPLETED);
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

        try {
            File file = new File(filePath);
            byte[] bytesArray = new byte[(int) file.length()];
            item.setFileSize(String.valueOf(bytesArray.length));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    /**
     * Audio File Uploading
     *
     * @param msgId        input value(isSelf)
     * @param docId        input value(docId)
     * @param fileName     input value(fileName)
     * @param audioPath    input value(audioPath)
     * @param duration     input value(duration)
     * @param receiverName input value(receiverName)
     * @param audioType    input value(audioType)
     * @param chatType     input value(chatType)
     * @param isSecretChat input value(isSecretChat)
     * @return response value
     */
    public Object createAudioUploadObject(String msgId, String docId, String fileName, String audioPath, String duration,
                                          String receiverName, int audioType, String chatType, boolean isSecretChat) {
        JSONObject uploadObj = new JSONObject();

        try {
            uploadObj.put("err", 0);
            uploadObj.put("ImageName", fileName);
            uploadObj.put("id", msgId);
            uploadObj.put("from", docId.split("-")[0]);
            uploadObj.put("to", docId.split("-")[1]);
            uploadObj.put("toDocId", msgId);
            uploadObj.put("docId", docId);
            uploadObj.put("bufferAt", -1); // for get first index is 0.
            uploadObj.put("LocalPath", audioPath);
            uploadObj.put("Duration", duration);
            uploadObj.put("type", MessageFactory.audio);
            uploadObj.put("ReceiverName", receiverName);
            uploadObj.put("mode", "phone");
            uploadObj.put("audio_type", audioType);
            uploadObj.put("chat_type", chatType);

            if (isSecretChat) {
                uploadObj.put("secret_type", "yes");
            } else {
                uploadObj.put("secret_type", "no");
            }

            File file = new File(audioPath);
            uploadObj.put("size", file.length());
            uploadObj.put("original_filename", file.getName());

            FileUploadDownloadManager uploadDownloadManager = new FileUploadDownloadManager(context);
            uploadDownloadManager.setUploadProgress(msgId, 0, uploadObj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return uploadObj;
    }

    /**
     * @param msgId          input value(msgId)
     * @param fileSize       input value(fileSize)
     * @param duration       input value(duration)
     * @param serverFilePath input value(serverFilePath)
     * @param audioFrom      input value(audioFrom)
     * @param isSecretChat   input value(isSecretChat)
     * @return response value
     */
    public Object getAudioMessageObject(String msgId, int fileSize, String duration, String serverFilePath,
                                        int audioFrom, boolean isSecretChat) {
        JSONObject msgObj = new JSONObject();
        try {
            String[] splitIds = msgId.split("-");
            String fromUserId = splitIds[0];
            String toUserId = splitIds[1];
            String audioMsgId;
            if (msgId.contains("-g")) {
                audioMsgId = splitIds[3];
            } else {
                audioMsgId = splitIds[2];
            }

            msgObj.put("from", fromUserId);
            msgObj.put("to", toUserId);
            msgObj.put("type", MessageFactory.audio);
            msgObj.put("payload", "");
            msgObj.put("id", audioMsgId);
            msgObj.put("toDocId", msgId);
            msgObj.put("filesize", fileSize);
            msgObj.put("duration", duration);
            msgObj.put("thumbnail", serverFilePath);
            msgObj.put("audio_type", audioFrom);

            if (isSecretChat) {
                msgObj.put("chat_type", MessageFactory.CHAT_TYPE_SECRET);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return msgObj;
    }

    /**
     * @param msgId          input value(msgId)
     * @param fileSize       input value(fileSize)
     * @param duration       input value(duration)
     * @param serverFilePath input value(serverFilePath)
     * @param groupName      input value(groupName)
     * @param audioFrom      input value(audioFrom)
     * @return response value
     */
    public Object getGroupAudioMessageObject(String msgId, int fileSize, String duration,
                                             String serverFilePath, String groupName, int audioFrom) {
        JSONObject msgObj = new JSONObject();
        try {
            String[] splitIds = msgId.split("-");
            String fromUserId = splitIds[0];
            String toUserId = splitIds[1];
            String audioMsgId;
            if (msgId.contains("-g")) {
                audioMsgId = splitIds[3];
            } else {
                audioMsgId = splitIds[2];
            }
            msgObj.put("from", fromUserId);
            msgObj.put("to", toUserId);
            msgObj.put("type", MessageFactory.audio);
            msgObj.put("payload", "");

            msgObj.put("id", audioMsgId);
            msgObj.put("toDocId", msgId);
            msgObj.put("filesize", fileSize);
            msgObj.put("duration", duration);
            msgObj.put("thumbnail", serverFilePath);
            msgObj.put("groupType", SocketManager.ACTION_EVENT_GROUP_MESSAGE);
            msgObj.put("userName", groupName);
            msgObj.put("audio_type", audioFrom);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return msgObj;
    }


}
