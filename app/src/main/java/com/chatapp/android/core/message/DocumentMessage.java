package com.chatapp.android.core.message;

import android.content.Context;

import com.chatapp.android.app.utils.GroupInfoSession;
import com.chatapp.android.core.model.GroupInfoPojo;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.socket.SocketManager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * created by  Adhash Team on 2/2/2017.
 */
public class DocumentMessage extends BaseMessage implements Message {

    private Context context;

    /**
     * create constructor
     *
     * @param context current activity
     */
    public DocumentMessage(Context context) {
        super(context);
        setType(MessageFactory.document);
        this.context = context;
    }

    /**
     * get single document Message
     *
     * @param to           input value (to)
     * @param payload      input value (payload)
     * @param isSecretChat input value (isSecretChat)
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
            object.put("type", MessageFactory.document);
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
     * get Group document Message
     *
     * @param to        input value (to)
     * @param payload   input value (payload)
     * @param groupName input value (groupName)
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
            object.put("type", MessageFactory.group_document_message);
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
     * create MessageItem and update message status
     *
     * @param isSelf        input value (isSelf)
     * @param filePath      input value (filePath)
     * @param deliverStatus input value (deliverStatus)
     * @param receiverId    input value (receiverId)
     * @param receiverName  input value (receiverName)
     * @param isExpiry      input value (isExpiry)
     * @param expiryTime    input value (expiryTime)
     * @return response value
     */
    public MessageItemChat createMessageItem(boolean isSelf, String filePath, String deliverStatus,
                                             String receiverId, String receiverName, String isExpiry, String expiryTime) {
        String[] fullPath = filePath.split("/");
        String fileName = fullPath[fullPath.length - 1];

        item = new MessageItemChat();
        item.setMessageId(getId() + "-" + tsForServerEpoch);
        item.setIsSelf(isSelf);
        item.setTextMessage(fileName);
        item.setReceiverID(to);
        item.setChatFileLocalPath(filePath);
        item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_COMPLETED);
        item.setDeliveryStatus(deliverStatus);
        item.setReceiverUid(receiverId);
        item.setMessageType("" + type);
        item.setSenderName(receiverName);
        item.setTS(getShortTimeFormat());
        item.setFileBufferAt(0);
        item.setIsExpiry(isExpiry);
        item.setExpiryTime(expiryTime);

        if (fileName.contains(".pdf")) {
            item.setThumbnailData(getPDFThumbData(filePath));
        }

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
     * create Document Upload
     *
     * @param msgId        input value (msgId)
     * @param docId        input value (docId)
     * @param docName      input value (docName)
     * @param filePath     input value (filePath)
     * @param receiverName input value (receiverName)
     * @param chatType     input value (chatType)
     * @param isSecretChat input value (isSecretChat)
     * @return response value
     */
    public Object createDocUploadObject(String msgId, String docId, String docName, String filePath,
                                        String receiverName, String chatType, boolean isSecretChat) {
        JSONObject uploadObj = new JSONObject();

        try {
            uploadObj.put("err", 0);
            uploadObj.put("ImageName", docName);
            uploadObj.put("id", msgId);
            uploadObj.put("from", docId.split("-")[0]);
            uploadObj.put("to", docId.split("-")[1]);
            uploadObj.put("toDocId", msgId);
            uploadObj.put("docId", docId);
            uploadObj.put("bufferAt", -1); // for get first index is 0.
            uploadObj.put("LocalPath", filePath);
            uploadObj.put("type", MessageFactory.document);
            uploadObj.put("ReceiverName", receiverName);
            uploadObj.put("mode", "phone");
            uploadObj.put("chat_type", chatType);

            if (isSecretChat) {
                uploadObj.put("secret_type", "yes");
            } else {
                uploadObj.put("secret_type", "no");
            }

            File file = new File(filePath);
            uploadObj.put("size", file.length());
            uploadObj.put("original_filename", file.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return uploadObj;
    }

    /**
     * set Document Message
     *
     * @param msgId            input value (msgId)
     * @param fileSize         input value (fileSize)
     * @param originalFileName input value (originalFileName)
     * @param locFilePath      input value (locFilePath)
     * @param serverFilePath   input value (serverFilePath)
     * @param isSecretChat     input value (isSecretChat)
     * @return response value
     */
    public Object getDocumentMessageObject(String msgId, int fileSize, String originalFileName, String locFilePath,
                                           String serverFilePath, boolean isSecretChat) {
        JSONObject msgObj = new JSONObject();
        try {
            String[] splitIds = msgId.split("-");
            String fromUserId = splitIds[0];
            String toUserId = splitIds[1];
            String docMsgId;
            if (msgId.contains("-g")) {
                docMsgId = splitIds[3];
            } else {
                docMsgId = splitIds[2];
            }

            msgObj.put("from", fromUserId);
            msgObj.put("to", toUserId);
            msgObj.put("type", MessageFactory.document);
            msgObj.put("payload", "");
            msgObj.put("original_filename", originalFileName);

            msgObj.put("id", docMsgId);
            msgObj.put("toDocId", msgId);
            msgObj.put("filesize", fileSize);
            msgObj.put("thumbnail", serverFilePath);
            if (locFilePath.contains(".pdf")) {
                msgObj.put("thumbnail_data", getPDFThumbData(locFilePath));
            }

            if (isSecretChat) {
                msgObj.put("chat_type", MessageFactory.CHAT_TYPE_SECRET);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return msgObj;
    }


    /**
     * set Group DocumentMessage
     *
     * @param msgId            input value (msgId)
     * @param fileSize         input value (fileSize)
     * @param originalFileName input value (originalFileName)
     * @param locFilePath      input value (locFilePath)
     * @param serverFilePath   input value (serverFilePath)
     * @param groupName        input value (groupName)
     * @return response value
     */
    public Object getGroupDocumentMessageObject(String msgId, int fileSize, String originalFileName, String locFilePath,
                                                String serverFilePath, String groupName) {

        JSONObject msgObj = new JSONObject();
        try {
            String[] splitIds = msgId.split("-");
            String fromUserId = splitIds[0];
            String toUserId = splitIds[1];
            String docMsgId;
            if (msgId.contains("-g")) {
                docMsgId = splitIds[3];
            } else {
                docMsgId = splitIds[2];
            }
            msgObj.put("from", fromUserId);
            msgObj.put("to", toUserId);
            msgObj.put("type", MessageFactory.group_document_message);
            msgObj.put("original_filename", originalFileName);
            msgObj.put("id", docMsgId);
            msgObj.put("toDocId", msgId);
            msgObj.put("filesize", fileSize);
            msgObj.put("thumbnail", serverFilePath);
            msgObj.put("groupType", SocketManager.ACTION_EVENT_GROUP_MESSAGE);
            msgObj.put("userName", groupName);
            msgObj.put("payload", "");

            if (locFilePath.contains(".pdf")) {
                msgObj.put("thumbnail_data", getPDFThumbData(locFilePath));
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return msgObj;
    }

    public String getPDFThumbData(String filePath) {
        String thumbData = "";
        int pageNumber = 0;
       /* PdfiumCore pdfiumCore = new PdfiumCore(context);
        try {
            //http://www.programcreek.com/java-api-examples/index.php?api=android.os.ParcelFileDescriptor
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(new File(filePath), ParcelFileDescriptor.MODE_READ_ONLY);
            PdfDocument pdfDocument = pdfiumCore.newDocument(fd);
            pdfiumCore.openPage(pdfDocument, pageNumber);
            *//*int width = pdfiumCore.getPageWidthPoint(pdfDocument, pageNumber);
            int height = pdfiumCore.getPageHeightPoint(pdfDocument, pageNumber);*//*
            Bitmap bmp = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
            pdfiumCore.renderPageBitmap(pdfDocument, bmp, pageNumber, 0, 0, 300, 300);
            pdfiumCore.closeDocument(pdfDocument); // important!

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] thumbBuffer = byteArrayOutputStream .toByteArray();
            thumbData = Base64.encodeToString(thumbBuffer, Base64.DEFAULT);
            if (!thumbData.startsWith("data:image/jpeg;base64,")) {
                thumbData = "data:image/jpeg;base64," + thumbData;
            }
        } catch (Exception e) {
            //todo with exception
            e.printStackTrace();
        }*/

        return thumbData;
    }
}
