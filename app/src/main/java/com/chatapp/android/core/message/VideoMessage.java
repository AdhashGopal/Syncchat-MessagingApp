package com.chatapp.android.core.message;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.util.Log;

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
 *
 */
public class VideoMessage extends BaseMessage implements Message {

    private Context context;
    private static final String TAG = "VideoMessage";

    /**
     * create constructor
     *
     * @param context current activity
     */
    public VideoMessage(Context context) {
        super(context);
        this.context = context;
    }

    /**
     * get single Message from video chat
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

    /**
     * get group Message from video chat
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
        } catch (Exception e) {
            Log.e(TAG, "getGroupMessageObject: ", e);
        }
        return object;
    }

    public void setIDforBroadcast(String to) {
        this.to = to;
        setId(from + "-" + to + "-g");
    }


    /**
     *
     * create MessageItem for video
     * @param isSelf      input value(isSelf)
     * @param videoPath   input value(videoPath)
     * @param status      input value(status)
     * @param receiverUid input value(receiverUid)
     * @param senderName  input value(senderName)
     * @param caption     input value(caption)
     * @param isExpiry    input value(isExpiry)
     * @param expiryTime  input value(expiryTime)
     * @return response value
     */
    public MessageItemChat createMessageItem(boolean isSelf, String videoPath, String status,
                                             String receiverUid, String senderName, String caption, String isExpiry, String expiryTime) {
        item = new MessageItemChat();
        item.setMessageId(getId() + "-" + tsForServerEpoch);
        item.setIsSelf(isSelf);
        item.setVideoPath(videoPath);
        item.setChatFileLocalPath(videoPath);
        item.setDeliveryStatus(status);
        item.setReceiverUid(receiverUid);
        item.setMessageType("" + type);
        item.setReceiverID(to);
        item.setTextMessage(caption);
        item.setSenderName(senderName);
        item.setTS(getShortTimeFormat());
        item.setFileBufferAt(0);
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
            File file = new File(videoPath);
            byte[] bytesArray = new byte[(int) file.length()];
            item.setFileSize(String.valueOf(bytesArray.length));
        } catch (Exception e) {
            Log.e(TAG, "createMessageItem: ", e);
        }

        return item;
    }

    /**
     * create Video Upload message
     * @param msgId        input value(msgId)
     * @param docId        input value(docId)
     * @param videoName    input value(videoName)
     * @param videoPath    input value(videoPath)
     * @param receiverName input value(receiverName)
     * @param caption      input value(caption)
     * @param chatType     input value(chatType)
     * @param isSecretChat input value(isSecretChat)
     * @return response value
     */
    public Object createVideoUploadObject(String msgId, String docId, String videoName, String videoPath,
                                          String receiverName, String caption, String chatType, boolean isSecretChat) {
        JSONObject uploadObj = new JSONObject();

        try {
            uploadObj.put("err", 0);
            uploadObj.put("ImageName", videoName);
            uploadObj.put("id", msgId);
            uploadObj.put("from", docId.split("-")[0]);
            uploadObj.put("to", docId.split("-")[1]);
            uploadObj.put("toDocId", msgId);
            uploadObj.put("docId", docId);
            uploadObj.put("bufferAt", -1); // for get first index is 0.
            uploadObj.put("LocalPath", videoPath);
            uploadObj.put("type", MessageFactory.video);
            uploadObj.put("ReceiverName", receiverName);
            uploadObj.put("mode", "phone");
            uploadObj.put("payload", caption);
            uploadObj.put("chat_type", chatType);

            if (isSecretChat) {
                uploadObj.put("secret_type", "yes");
            } else {
                uploadObj.put("secret_type", "no");
            }

            MediaMetadataRetriever mdr = new MediaMetadataRetriever();
            mdr.setDataSource(videoPath);
            int videoHeight = Integer.parseInt(mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
            int videoWidth = Integer.parseInt(mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
            String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

//            Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
//            ByteArrayOutputStream out = new ByteArrayOutputStream();
//            thumbBmp.compress(Bitmap.CompressFormat.JPEG, 35, out);
//            byte[] thumbArray = out.toByteArray();
//            try {
//                out.close();
//            } catch (Exception e) {
//                Log.e(TAG, "createVideoUploadObject: ",e );
//            }
//            String thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
//            if (!thumbData.startsWith("data:image/jpeg;base64,")) {
//                thumbData = "data:image/jpeg;base64," + thumbData;
//            }

            File videoFile = new File(videoPath);
            uploadObj.put("size", videoFile.length());
            uploadObj.put("original_filename", videoFile.getName());
            //   uploadObj.put("thumbnail_data", thumbData);
            uploadObj.put("height", videoHeight);
            uploadObj.put("width", videoWidth);
            uploadObj.put("Duration", duration);

            FileUploadDownloadManager uploadDownloadManager = new FileUploadDownloadManager(context);
            uploadDownloadManager.setUploadProgress(item.getMessageId(), 0, uploadObj);
        } catch (Exception e) {
            Log.e(TAG, "createVideoUploadObject: ", e);
        }

        return uploadObj;
    }


    /**
     *
     * get Group Video Message chat
     * @param msgId          input value(msgId)
     * @param fileSize       input value(fileSize)
     * @param thumbData      input value(thumbData)
     * @param videoWidth     input value(videoWidth)
     * @param videoHeight    input value(videoHeight)
     * @param duration       input value(duration)
     * @param serverFilePath input value(serverFilePath)
     * @param groupName      input value(groupName)
     * @param caption        input value(caption)
     * @return response value
     */

    public Object getGroupVideoMessageObject(String msgId, int fileSize, String thumbData, int videoWidth, int videoHeight,
                                             String duration, String serverFilePath, String groupName, String caption) {

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
            msgObj.put("type", MessageFactory.video);
            msgObj.put("payload", caption);

            msgObj.put("id", picMsgId);
            msgObj.put("toDocId", msgId);
            msgObj.put("filesize", fileSize);
            msgObj.put("width", videoWidth);
            msgObj.put("height", videoHeight);
            msgObj.put("thumbnail", serverFilePath);
            msgObj.put("thumbnail_data", thumbData);
            msgObj.put("duration", duration);
            msgObj.put("groupType", SocketManager.ACTION_EVENT_GROUP_MESSAGE);
            msgObj.put("userName", groupName);
        } catch (Exception ex) {
            Log.e(TAG, "getGroupVideoMessageObject: ", ex);
        }
        return msgObj;
    }


    /**
     * get Video Message chat
     * @param msgId          input value(msgId)
     * @param fileSize       input value(fileSize)
     * @param thumbData      input value(thumbData)
     * @param videoWidth     input value(videoWidth)
     * @param videoHeight    input value(videoHeight)
     * @param duration       input value(duration)
     * @param serverFilePath input value(serverFilePath)
     * @param caption        input value(caption)
     * @param isSecretChat   input value(isSecretChat)
     * @param isStatus       input value(isStatus)
     * @param statusDocId    input value(statusDocId)
     * @return response value
     */
    public Object getVideoMessageObject(String msgId, int fileSize, String thumbData, int videoWidth, int videoHeight,
                                        String duration, String serverFilePath, String caption,
                                        boolean isSecretChat, boolean isStatus, String statusDocId) {
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

            if (isStatus) {
/*                //sample 343434343--34343
                if(msgId.contains("--")){
                    msgId= msgId.replace("--","-");
                }*/
                msgId = statusDocId;
            }
            msgObj.put("from", fromUserId);
            msgObj.put("to", toUserId);
            msgObj.put("type", MessageFactory.video);
            msgObj.put("payload", caption);
            msgObj.put("id", picMsgId);
            msgObj.put("toDocId", msgId);
            msgObj.put("filesize", fileSize);
            msgObj.put("width", videoWidth);
            msgObj.put("height", videoHeight);
            msgObj.put("thumbnail", serverFilePath);
            msgObj.put("thumbnail_data", thumbData);
            msgObj.put("duration", duration);

            if (isSecretChat) {
                msgObj.put("chat_type", MessageFactory.CHAT_TYPE_SECRET);
            }

        } catch (Exception ex) {
            Log.e(TAG, "getVideoMessageObject: ", ex);
        }
        return msgObj;
    }
}