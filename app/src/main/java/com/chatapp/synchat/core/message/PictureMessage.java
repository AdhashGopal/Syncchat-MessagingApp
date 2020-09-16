package com.chatapp.synchat.core.message;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.socket.SocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import id.zelory.compressor.Compressor;

import static com.chatapp.synchat.core.message.TextMessage.TAG_KEY;

/**
 * Created by Administrator on 11/2/2016.
 */
public class PictureMessage extends BaseMessage implements Message {
    private Context context;
    private static final String TAG = "PictureMessage";

    /**
     * create constructor
     *
     * @param context current activity
     */
    public PictureMessage(Context context) {
        super(context);
        setType(MessageFactory.picture);
        this.context = context;
    }

    /**
     * set Message chat
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
        } catch (Exception e) {
            Log.e(TAG, "getMessageObject: ", e);
        }
        return object;
    }

    public void setIDforBroadcast(String to) {
        this.to = to;
        setId(from + "-" + to + "-g");
    }


    /**
     * get Group Message chat
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
            object.put("groupType", SocketManager.ACTION_EVENT_GROUP_MESSAGE);
            object.put("userName", groupName);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * create MessageItem for picture event
     *
     * @param isSelf      input value(isSelf)
     * @param caption     input value(caption)
     * @param imgPath     input value(imgPath)
     * @param status      input value(status)
     * @param receiverUid input value(receiverUid)
     * @param senderName  input value(senderName)
     * @param imageWidth  input value(imageWidth)
     * @param imageHeight input value(imageHeight)
     * @param isExpiry    input value(isExpiry)
     * @param expiryTime  input value(expiryTime)
     * @return response value
     */
    public MessageItemChat createMessageItem(boolean isSelf, String caption, String imgPath, String status,
                                             String receiverUid, String senderName, int imageWidth, int imageHeight, String isExpiry, String expiryTime) {
        item = new MessageItemChat();
        item.setMessageId(getId() + "-" + tsForServerEpoch);
        item.setIsSelf(isSelf);
        item.setTextMessage(caption);
        item.setImagePath(imgPath);
        item.setChatFileLocalPath(imgPath);
        item.setDeliveryStatus(status);
        item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_COMPLETED);
        item.setReceiverUid(receiverUid);
        item.setMessageType("" + type);
        item.setSenderName(senderName);
        item.setReceiverID(to);
        item.setTS(getShortTimeFormat());
        item.setFileBufferAt(0);
        item.setChatFileWidth(String.valueOf(imageWidth));
        item.setChatFileHeight(String.valueOf(imageHeight));
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

    /**
     * get Picture Message chat
     *
     * @param caption        input value(caption)
     * @param msgId          input value(msgId)
     * @param fileSize       input value(fileSize)
     * @param imageWidth     input value(imageWidth)
     * @param imageHeight    input value(imageHeight)
     * @param serverFilePath input value(serverFilePath)
     * @param isSecretChat   input value(isSecretChat)
     * @return response value
     */
    public Object getPictureMessageObject(String caption, String msgId, int fileSize, int imageWidth,
                                          int imageHeight, String serverFilePath, boolean isSecretChat) {
        JSONObject msgObj = new JSONObject();
        try {
            String[] splitIds = msgId.split("-");
            String fromUserId = splitIds[0];
            String toUserId = splitIds[1];
            String picMsgId;
            if (msgId.contains("-g")) {
                picMsgId = splitIds[3];
            } else {
                try {
                    picMsgId = splitIds[2];
                } catch (Exception e) {
                    //for status upload it only have xxxxxxxx-yyyy
                    picMsgId = splitIds[1];
                }
            }

            msgObj.put("from", fromUserId);
            msgObj.put("to", toUserId);
            msgObj.put("type", MessageFactory.picture);
            msgObj.put("payload", caption);
            msgObj.put("id", picMsgId);
            msgObj.put("toDocId", msgId);
            msgObj.put("filesize", fileSize);
            msgObj.put("width", imageWidth);
            msgObj.put("height", imageHeight);
            msgObj.put("thumbnail", serverFilePath);

            if (isSecretChat) {
                msgObj.put("chat_type", MessageFactory.CHAT_TYPE_SECRET);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return msgObj;
    }

    /**
     * get Group Picture Message chat
     *
     * @param caption        input value(caption)
     * @param msgId          input value(msgId)
     * @param fileSize       input value(fileSize)
     * @param imageWidth     input value(imageWidth)
     * @param imageHeight    input value(imageHeight)
     * @param serverFilePath input value(serverFilePath)
     * @param groupName      input value(groupName)
     * @return response value
     */
    public Object getGroupPictureMessageObject(String caption, String msgId, int fileSize, int imageWidth,
                                               int imageHeight, String serverFilePath, String groupName) {
        JSONObject msgObj = new JSONObject();
        try {
            String[] splitIds = msgId.split("-");
            String fromUserId = splitIds[0];
            String toUserId = splitIds[1];
            String picMsgId;
            if (msgId.contains("-g")) {
                picMsgId = splitIds[3];
            } else {
                picMsgId = splitIds[2];
            }
            msgObj.put("from", fromUserId);
            msgObj.put("to", toUserId);
            msgObj.put("type", MessageFactory.picture);

            if (caption.contains(TAG_KEY)) {
                msgObj.put("payload", caption);
                msgObj.put("is_tag_applied", "1");
            } else {
                msgObj.put("payload", caption);
                msgObj.put("is_tag_applied", "0");
            }
            //msgObj.put("payload", caption);

            msgObj.put("id", picMsgId);
            msgObj.put("toDocId", msgId);
            msgObj.put("filesize", fileSize);
            msgObj.put("width", imageWidth);
            msgObj.put("height", imageHeight);
            msgObj.put("thumbnail", serverFilePath);
            msgObj.put("groupType", SocketManager.ACTION_EVENT_GROUP_MESSAGE);
            msgObj.put("userName", groupName);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return msgObj;
    }

    /**
     * @param msgId        input value(msgId)
     * @param docId        input value(docId)
     * @param fileName     input value(fileName)
     * @param imgPath      input value(imgPath)
     * @param receiverName input value(receiverName)
     * @param caption      input value(caption)
     * @param chatType     input value(chatType)
     * @param isSecretChat input value(isSecretChat)
     * @return response value
     */
    public Object createImageUploadObject(String msgId, String docId, String fileName, String imgPath, String receiverName,
                                          String caption, String chatType, boolean isSecretChat) {
        JSONObject uploadObj = new JSONObject();

        try {
            uploadObj.put("err", 0);
            uploadObj.put("ImageName", fileName);
            uploadObj.put("id", msgId);
            uploadObj.put("docId", docId);
            uploadObj.put("from", docId.split("-")[0]);
            uploadObj.put("to", docId.split("-")[1]);
            uploadObj.put("toDocId", msgId);
            uploadObj.put("bufferAt", -1); // for get first index is 0.
            uploadObj.put("LocalPath", imgPath);
            uploadObj.put("type", MessageFactory.picture);
            uploadObj.put("ReceiverName", receiverName);
            uploadObj.put("mode", "phone");
            uploadObj.put("payload", caption);
            uploadObj.put("chat_type", chatType);

            if (isSecretChat) {
                uploadObj.put("secret_type", "yes");
            } else {
                uploadObj.put("secret_type", "no");
            }

            File file = new File(imgPath);

            try {
                file = new Compressor(context).compressToFile(file);
            } catch (Exception e) {
                Log.e(TAG, "createImageUploadObject: ", e);
            }

            uploadObj.put("size", file.length());
            uploadObj.put("original_filename", file.getName());

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Bitmap compressBmp = null;
            try {
                compressBmp = new Compressor(context).compressToBitmap(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            int height = compressBmp.getHeight();
            int width = compressBmp.getWidth();
            compressBmp = Bitmap.createScaledBitmap(compressBmp, 100, 100, false);
            compressBmp.compress(Bitmap.CompressFormat.JPEG, 30, out);
            byte[] thumbArray = out.toByteArray();

            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

//            String thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
//            if (!thumbData.startsWith("data:image/jpeg;base64,")) {
//                thumbData = "data:image/jpeg;base64," + thumbData;
//            }
//
//            try {
//                uploadObj.put("thumbnail_data", thumbData);
//                uploadObj.put("height", height);
//                uploadObj.put("width", width);
//            }
//            catch (Exception e)
//            {
//                uploadObj.put("thumbnail_data", "");
//                uploadObj.put("height", "");
//                uploadObj.put("width", "");
//            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return uploadObj;
    }

    public Object createImageDownloadObject(String msgId, String docId, String imgServerPath,
                                            String imgLocalPath, String localFileName, String dataSize) {
        JSONObject downloadObj = new JSONObject();

        try {
            downloadObj.put("DocId", docId);
            downloadObj.put("MsgId", msgId);
            downloadObj.put("ImageName", imgServerPath);
            downloadObj.put("LocalPath", imgLocalPath);
            downloadObj.put("LocalFileName", localFileName);
            downloadObj.put("FileSize", dataSize);
            downloadObj.put("bytesRead", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return downloadObj;
    }

    /**
     * @param fileName input value(fileName)
     * @param imgPath  input value(imgPath)
     * @return response value
     */
    public Object createUserProfileImageObject(String fileName, String imgPath) {

        JSONObject uploadObj = new JSONObject();
        try {
            uploadObj.put("err", 0);
            uploadObj.put("ImageName", fileName);
            uploadObj.put("from", from);
            uploadObj.put("bufferAt", -1); // for get first index is 0.
            uploadObj.put("LocalPath", imgPath);
            uploadObj.put("type", MessageFactory.user_profile_pic_update);
            uploadObj.put("uploadType", "single");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return uploadObj;
    }

    /**
     * @param fileName input value(fileName)
     * @param imgPath  input value(imgPath)
     * @return response value
     */
    public Object createGroupProfileImageObject(String fileName, String imgPath) {

        JSONObject uploadObj = new JSONObject();
        try {
            uploadObj.put("err", 0);
            uploadObj.put("ImageName", fileName);
            uploadObj.put("from", from);
            uploadObj.put("bufferAt", -1); // for get first index is 0.
            uploadObj.put("LocalPath", imgPath);
            uploadObj.put("type", MessageFactory.group_profile_pic_update);
            uploadObj.put("uploadType", "group");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return uploadObj;
    }
}
