package com.chatapp.synchat.core.message;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.model.CallItemChat;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.socket.MessageService;
import com.chatapp.synchat.core.uploadtoserver.FileUploadDownloadManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;

/**
 * created by  Adhash Team on 2/27/2017.
 */
public class IncomingMessage {

    private Context mContext;

    private String mCurrentUserId;
    private Session session;
    private Getcontactname getcontactname;
    private MessageService msgSvcCallback;

    /**
     * Create constructor
     *
     * @param context current activity
     */
    public IncomingMessage(Context context) {
        this.mContext = context;
        session = new Session(mContext);
        mCurrentUserId = SessionManager.getInstance(mContext).getCurrentUserID();
        getcontactname = new Getcontactname(mContext);
    }

    /**
     * load Group in chat Message
     *
     * @param objects based on response to check media file downloading status & updated database
     * @return response value
     */
    public MessageItemChat loadGroupMessage(JSONObject objects) {
        try {
            Integer type = Integer.parseInt(objects.getString("type"));

            String id = objects.getString("id");
            String from = objects.getString("from");
            String to = objects.getString("to");

            String toDocId = (String) objects.get("toDocId");
            String[] splitDocId = toDocId.split("-");
            String msgId = splitDocId[3];
            String name = objects.getString("msisdn");


            String senderOriginalName = "";
//
            if (objects.has("fromuser_name")) {

                try {
                    senderOriginalName = new String(Base64.decode(objects.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }

//            String convId = objects.getString("convId");
            String recordId = objects.getString("recordId");
            String starredStatus = objects.getString("isStar");

            String ts = objects.getString("timestamp");
            if (session.getarchivecountgroup() != 0) {
                if (session.getarchivegroup(mCurrentUserId + "-" + to + "-g"))
                    session.removearchivegroup(mCurrentUserId + "-" + to + "-g");
            }

            MessageItemChat item = new MessageItemChat();
//            item.setConvId(convId);
            item.setRecordId(recordId);
            item.set_id(id);
            item.setStarredStatus(starredStatus);
            item.setTS(ts);
            item.setGroupMsgFrom(from);
            item.setSenderName(name);
            item.setGroup(true);
            if (objects.has("expiry_time")) {
                item.setExpiryTime(objects.getString("expiry_time"));
            }

            if (objects.has("is_expiry")) {
                item.setIsExpiry(objects.getString("is_expiry"));
            }


            item.setSenderOriginalName(senderOriginalName);
            //     item.setC

            item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_NOT_START);

            if (objects.has("payload")) {
                String payLoad = objects.getString("payload");
                item.setTextMessage(payLoad);
            }
            if (type == MessageFactory.picture || type == MessageFactory.audio || type == MessageFactory.video
                    || type == MessageFactory.group_document_message) {
                long filesize = AppUtils.parseLong(objects.getString("filesize"));
                filesize = session.getreceviedmedialength() + filesize;
                session.putreceivemedialength(filesize);
            }

            if (objects.has("replyDetails")) {
                JSONObject aReplyObject = objects.getJSONObject("replyDetails");
                item.setReplyMsisdn(aReplyObject.getString("From_msisdn"));
                item.setReplyFrom(aReplyObject.getString("from"));
                String userID = aReplyObject.getString("from");
                String replyname = aReplyObject.getString("From_msisdn");
                item.setReplyType(aReplyObject.getString("type"));
                if (aReplyObject.has("message")) {
                    item.setReplyMessage(aReplyObject.getString("message"));
                }
                if (aReplyObject.has("thumbnail_data")) {
                    String thumbnail_data = aReplyObject.getString("thumbnail_data");
                    item.setreplyimagebase64(thumbnail_data);
                }
                item.setReplyId(aReplyObject.getString("_id"));

                replyname = getcontactname.getSendername(userID, replyname);

                if (item.getReplyFrom().equalsIgnoreCase(mCurrentUserId)) {
                    item.setReplySender("you");
                } else {
                    item.setReplySender(replyname);
                }

                if (aReplyObject.has("link_details")) {
                    try {
                        JSONObject replyLinkObj = aReplyObject.getJSONObject("link_details");
                        String replyLocTitle = "";
                        if (replyLinkObj.has("title")) {
                            replyLocTitle = replyLinkObj.getString("title");
                        }
                        String replyLocThumbData = replyLinkObj.getString("thumbnail_data");

                        item.setReplyMessage(replyLocTitle);
                        item.setreplyimagebase64(replyLocThumbData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            item.setMessageType("" + type);
            String strType = "" + type;
            if (strType.equalsIgnoreCase("" + MessageFactory.text)) {
                String payLoad = objects.getString("payload");
                long messagelength = session.getreceviedmessagelength() + payLoad.length();
                session.putreceivemessagelength(messagelength);
                int receviedmesagecount = session.getreceviedmessagecount();
                receviedmesagecount = receviedmesagecount + 1;
                session.putreceivemessagecount(receviedmesagecount);

            } else if (strType.equalsIgnoreCase("" + MessageFactory.contact)) {
                String contactName = objects.getString("contact_name");
                String contactNumber = "", contactDetails = "";
                if (objects.has("createdTomsisdn")) {
                    contactNumber = objects.getString("createdTomsisdn");
                    item.setContactNumber(contactNumber);
                }
                if (objects.has("contact_details")) {
                    contactDetails = objects.getString("contact_details");
                    item.setDetailedContacts(contactDetails);
                }

                if (objects.has("createdTo")) {
                    String contactChatappId = objects.getString("createdTo");
                    item.setContactchatappId(contactChatappId);
                }


                if (objects.has("createdTo")) {
                    String contactChatappId = objects.getString("createdTo");
                    item.setContactchatappId(contactChatappId);
                }
                item.setContactName(contactName);

            } else if (strType.equalsIgnoreCase("" + MessageFactory.picture)) {
                String thumbnail = objects.getString("thumbnail");
                String fileSize = objects.getString("filesize");
                String thumbData = objects.getString("thumbnail_data");

                if (thumbnail != null && !thumbnail.equals("")) {
                    File dir = new File(mContext.getFilesDir() + MessageFactory.IMAGE_STORAGE_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    String localFileName = MessageFactory.getMessageFileName(
                            MessageFactory.picture, id, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
                    String filePath = mContext.getFilesDir() + MessageFactory.IMAGE_STORAGE_PATH + localFileName;
                    item.setChatFileLocalPath(filePath);
                }

                item.setChatFileServerPath(thumbnail);
                item.setFileBufferAt(0);
                item.setUploadDownloadProgress(0);

                item.setThumbnailData(thumbData);
                item.setFileSize(fileSize);
            } else if (strType.equalsIgnoreCase("" + MessageFactory.group_document_message)) {
                item.setMessageType("" + MessageFactory.document);
                String thumbnail = objects.getString("thumbnail");
                String fileSize = objects.getString("filesize");
                String originalFileName = objects.getString("original_filename");
                if (thumbnail != null && !thumbnail.equals("")) {
                    File dir = new File(mContext.getFilesDir() + MessageFactory.DOCUMENT_STORAGE_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    String localFileName = MessageFactory.getMessageFileName(
                            MessageFactory.document, id, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
                    String filePath = mContext.getFilesDir() + MessageFactory.DOCUMENT_STORAGE_PATH + localFileName;
                    item.setChatFileLocalPath(filePath);
                    item.setTextMessage(originalFileName);
                    item.setThumbnailData(thumbnail);
                }

                item.setChatFileServerPath(thumbnail);
                item.setFileSize(fileSize);

            } else if (strType.equalsIgnoreCase("" + MessageFactory.audio)) {
                String thumbnail = objects.getString("thumbnail");
                String fileSize = objects.getString("filesize");

                if (thumbnail != null && !thumbnail.equals("")) {
                    File dir = new File(mContext.getFilesDir() + MessageFactory.AUDIO_STORAGE_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    String localFileName = MessageFactory.getMessageFileName(
                            MessageFactory.audio, id, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
                    String filePath = mContext.getFilesDir() + MessageFactory.AUDIO_STORAGE_PATH + localFileName;
                    item.setChatFileLocalPath(filePath);
                }

                String duration = objects.getString("duration");
                item.setChatFileServerPath(thumbnail);
                item.setFileSize(fileSize);
                item.setDuration(duration);
            } else if (strType.equalsIgnoreCase("" + MessageFactory.video)) {
                String thumbnail = objects.getString("thumbnail");
                String fileSize = objects.getString("filesize");

                if (thumbnail != null && !thumbnail.equals("")) {
                    File dir = new File(mContext.getFilesDir() + MessageFactory.VIDEO_STORAGE_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    String localFileName = MessageFactory.getMessageFileName(
                            MessageFactory.video, id, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
                    String filePath = mContext.getFilesDir() + MessageFactory.VIDEO_STORAGE_PATH + localFileName;
                    item.setChatFileLocalPath(filePath);
                }

                item.setChatFileServerPath(thumbnail);

                String thumbData = objects.getString("thumbnail_data");
                String duration = objects.getString("duration");
                item.setDuration(duration);
                item.setThumbnailData(thumbData);
                item.setFileSize(fileSize);
            }
            if (objects.has("link_details") && !strType.equalsIgnoreCase("" + MessageFactory.group_document_message)) {
                String payLoad = objects.getString("payload");
                JSONObject linkObj = objects.getJSONObject("link_details");

                String recLink = "";
                if (linkObj.has("url")) {
                    recLink = linkObj.getString("url");
                }

                String recLinkTitle = "";
                if (linkObj.has("title")) {
                    recLinkTitle = linkObj.getString("title");
                }

                String recLinkDesc = "";
                if (linkObj.has("description")) {
                    recLinkDesc = linkObj.getString("description");
                }

                String recLinkImgUrl = "";
                if (linkObj.has("image")) {
                    recLinkImgUrl = linkObj.getString("image");
                }

                String recLinkImgThumb = "";
                if (linkObj.has("thumbnail_data")) {
                    recLinkImgThumb = linkObj.getString("thumbnail_data");
                }


                item.setWebLink(recLink);
                item.setWebLinkTitle(recLinkTitle);
                item.setWebLinkDesc(recLinkDesc);
                item.setWebLinkImgUrl(recLinkImgUrl);
                item.setWebLinkImgThumb(recLinkImgThumb);
            }

            if (objects.has("is_tag_applied")) {
                String istagapply = objects.getString("is_tag_applied");
                if (istagapply.equalsIgnoreCase("1")) {
                    if (objects.has("payload")) {
                        String payLoads = objects.getString("payload");

                        ContactDB_Sqlite contactDB_sqlite = new ContactDB_Sqlite(mContext);

                        String[] splitedTexts = payLoads.split(" ");
                        String newPayLoad = "";
                        if (splitedTexts != null && splitedTexts.length > 0) {
                            for (int i = 0; i < splitedTexts.length; i++) {
                                String text = splitedTexts[i];
                                if (text.contains(TextMessage.TAG_KEY)) {
                                    String userName = "";
                                    String userId = text.replace(TextMessage.TAG_KEY, "");
                                    if (userId != null && userId.equals(mCurrentUserId)) {
                                        userName = "You";
                                    } else {
                                        ChatappContactModel chatappContactModel = contactDB_sqlite.getUserOpponenetDetails(userId);
                                        userName = chatappContactModel.getFirstName();

                                        if (userName == null || userName.isEmpty()) {
                                            userName = chatappContactModel.getMsisdn();
                                        }
                                    }
                                    userName = "@" + userName;
                                    newPayLoad = newPayLoad + userName + " ";
                                } else {
                                    newPayLoad = newPayLoad + text + " ";
                                }

                            }
                        }

                        item.setTextMessage(newPayLoad);
                    }
                } else {
                    if (objects.has("payload")) {
//                        String payLoads = objects.getString("payload");
//
//                        item.setTextMessage(payLoads);
                    }
                }
            }
            item.setCount(1);

            // For showing Group name in chat list
            String groupName = objects.getString("groupName");
            item.setGroupName(groupName);

            item.sethasNewMessage(true);
            String docId;

            if (from.equalsIgnoreCase(mCurrentUserId)) {
                docId = from + "-" + to + "-g";
                item.setIsSelf(true);
                item.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_SENT);
            } else /*if (to.equalsIgnoreCase(uniqueCurrentID))*/ {
                item.setIsSelf(false);
                item.sethasNewMessage(true);
                docId = mCurrentUserId + "-" + to + "-g";
            }
            item.setReceiverID(to);
            item.setMessageId(docId.concat("-").concat(msgId));

            GroupInfoSession groupInfoSession = new GroupInfoSession(mContext);
            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);

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

            if (objects.has("replyId") && !objects.has("replyDetails")) {
                String replyRecordId = objects.getString("replyId");
                if (msgSvcCallback != null) {
                    MessageDbController db = CoreController.getDBInstance(mContext);
                    MessageItemChat replyMsgItem = db.getMessageByRecordId(replyRecordId);
                    if (replyMsgItem == null) {
                        msgSvcCallback.getReplyMessageDetails(to, to, replyRecordId, MessageFactory.CHAT_TYPE_GROUP,
                                "no", item.getMessageId());
                    } else {
                        item = getReplyDetailsByMessageItem(replyMsgItem, item);
                    }
                }
            }

            return item;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * load Single Message From Web link
     *
     * @param objects based on response to check media file downloading status & updated database
     * @return response value
     */
    public MessageItemChat loadSingleMessageFromWeb(JSONObject objects) {

        try {
            String id = objects.getString("id");
            int delivery = (int) objects.get("deliver");

            JSONObject msgData = objects.getJSONObject("data");
            String secretType = msgData.getString("secret_type");
            String chat_id = (String) objects.get("doc_id");
            String[] ids = chat_id.split("-");
            String doc_id = ids[0] + "-" + ids[1];

            String recordId = msgData.getString("recordId");
            String convId = msgData.getString("convId");
            String fromUserId = msgData.getString("from");
            String toUserId = msgData.getString("to");

            String toMsisdn = msgData.getString("To_msisdn");
            String ts = msgData.getString("timestamp");

            MessageItemChat newMsgItem = new MessageItemChat();
            boolean isSecretTimerMsg = false;
            if (secretType.equalsIgnoreCase("yes")) {

                String secretMsgTimer = msgData.getString("incognito_timer");
                newMsgItem.setSecretTimer(secretMsgTimer);
                newMsgItem.setSecretTimeCreatedBy(mCurrentUserId);

                if (msgData.getString("type").equals(MessageFactory.timer_change + "")) {
                    isSecretTimerMsg = true;
                }
            }

            if (isSecretTimerMsg) {
                MessageItemChat timerMsgItem = getTimerChangeMessage(msgData, newMsgItem);
                return timerMsgItem;
            } else {
                newMsgItem.setIsSelf(true);
                newMsgItem.setStarredStatus(MessageFactory.MESSAGE_UN_STARRED);
                newMsgItem.setUploadStatus(MessageFactory.UPLOAD_STATUS_COMPLETED);
                newMsgItem.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_NOT_START);
                newMsgItem.setDeliveryStatus("" + delivery);
                newMsgItem.setReceiverUid(toUserId);
                newMsgItem.setTS(ts);
                newMsgItem.setSenderMsisdn(toMsisdn);
                newMsgItem.setMessageId(chat_id);
                newMsgItem.setConvId(convId);
                newMsgItem.setRecordId(recordId);
                newMsgItem.setReceiverID(toUserId);
                newMsgItem.setSenderName(toMsisdn);
                newMsgItem.setMsgSentAt(ts);
                String savedName = getcontactname.getSendername(toUserId, toMsisdn);
                newMsgItem.setSenderName(savedName);

                int type = Integer.parseInt(msgData.getString("type"));
                newMsgItem.setMessageType("" + type);
                switch (type) {

                    case MessageFactory.text:
                        String textMsg = msgData.getString("message");
                        newMsgItem.setTextMessage(textMsg);
                        break;

                    case MessageFactory.picture:
                        String caption = msgData.getString("message");
                        String thumbnail = msgData.getString("thumbnail");
                        String thumbData = msgData.getString("thumbnail_data");
                        String locFileId = msgData.getString("id");
                        String fileSize = msgData.getString("filesize");
                        String width = msgData.getString("width");
                        String height = msgData.getString("height");
                        String localFilePath = mContext.getFilesDir() + MessageFactory.IMAGE_STORAGE_PATH + MessageFactory.getMessageFileName(
                                MessageFactory.picture, locFileId, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));

                        newMsgItem.setTextMessage(caption);
                        newMsgItem.setChatFileServerPath(thumbnail);
                        newMsgItem.setThumbnailData(thumbData);
                        newMsgItem.setFileSize(fileSize);
                        newMsgItem.setChatFileWidth(width);
                        newMsgItem.setChatFileHeight(height);
                        newMsgItem.setChatFileLocalPath(localFilePath);
                        break;

                    case MessageFactory.contact:
                        String contactName = msgData.getString("contact_name");
                        String contactNumber = msgData.getString("createdTomsisdn");
                        String contactDetails = msgData.getString("contact_details");

                        if (msgData.has("createdTo")) {
                            String contactChatappId = msgData.getString("createdTo");
                            newMsgItem.setContactchatappId(contactChatappId);
                        }
                        newMsgItem.setContactName(contactName);
                        newMsgItem.setContactNumber(contactNumber);
                        newMsgItem.setDetailedContacts(contactDetails);
                        break;

                    case MessageFactory.web_link:
                        String linkMsg = msgData.getString("message");
                        newMsgItem.setTextMessage(linkMsg);
                        break;

                    case MessageFactory.document:
                        String originalFileName = msgData.getString("original_filename");
                        String docPath = msgData.getString("thumbnail");
                        String thumbnailData = msgData.getString("thumbnail_data");
//                    String docMsg = msgData.getString("message");
                        String dataSize = msgData.getString("filesize");
                        String locDocFileId = msgData.getString("id");

                        if (docPath != null && !docPath.equals("")) {
                            File dir = new File(mContext.getFilesDir() + MessageFactory.DOCUMENT_STORAGE_PATH);
                            if (!dir.exists()) {
                                dir.mkdir();
                            }
                            String localFileName = MessageFactory.getMessageFileName(
                                    MessageFactory.document, locDocFileId, FileUploadDownloadManager.getFileExtnFromPath(docPath));
                            String filePath = mContext.getFilesDir() + MessageFactory.DOCUMENT_STORAGE_PATH + localFileName;
                            newMsgItem.setChatFileLocalPath(filePath);
                            newMsgItem.setTextMessage(originalFileName);
                        }

                        newMsgItem.setChatFileServerPath(docPath);
                        newMsgItem.setFileBufferAt(0);
                        newMsgItem.setUploadDownloadProgress(0);
                        newMsgItem.setFileSize(dataSize);
                        newMsgItem.setThumbnailData(thumbnailData);
                        break;

                    case MessageFactory.video:
                        String videoPath = msgData.getString("thumbnail");
                        String videoSize = msgData.getString("filesize");
                        String locVideoFileId = msgData.getString("id");
                        if (videoPath != null && !videoPath.equals("")) {
                            File dir = new File(mContext.getFilesDir() + MessageFactory.VIDEO_STORAGE_PATH);
                            if (!dir.exists()) {
                                dir.mkdir();
                            }
                            String localFileName = MessageFactory.getMessageFileName(
                                    MessageFactory.video, locVideoFileId, FileUploadDownloadManager.getFileExtnFromPath(videoPath));
                            String filePath = mContext.getFilesDir() + MessageFactory.VIDEO_STORAGE_PATH + localFileName;
                            newMsgItem.setChatFileLocalPath(filePath);
                        }

                        newMsgItem.setChatFileServerPath(videoPath);
                        String videoThumbData = objects.getString("thumbnail_data");
                        String duration = objects.getString("duration");
                        newMsgItem.setDuration(duration);
                        newMsgItem.setThumbnailData(videoThumbData);
                        newMsgItem.setFileSize(videoSize);
                        break;
                }

                if (msgData.has("replyDetails")) {
                    JSONObject aReplyObject = msgData.getJSONObject("replyDetails");
                    newMsgItem.setReplyMsisdn(aReplyObject.getString("From_msisdn"));
                    String replyname = aReplyObject.getString("From_msisdn");
                    newMsgItem.setReplyFrom(aReplyObject.getString("from"));
                    String userId = aReplyObject.getString("from");
                    newMsgItem.setReplyType(aReplyObject.getString("type"));
                    if (aReplyObject.has("message")) {
                        newMsgItem.setReplyMessage(aReplyObject.getString("message"));
                    }
                    newMsgItem.setReplyId(aReplyObject.getString("_id"));
                    newMsgItem.setReplyServerLoad(aReplyObject.getString("server_load"));
                    if (aReplyObject.has("thumbnail_data")) {
                        String thumbnail_data = aReplyObject.getString("thumbnail_data");
                        newMsgItem.setreplyimagebase64(thumbnail_data);
                    }

                    replyname = getcontactname.getSendername(userId, replyname);

                    if (newMsgItem.getReplyFrom().equalsIgnoreCase(mCurrentUserId)) {
                        newMsgItem.setReplySender("you");
                    } else {
                        newMsgItem.setReplySender(replyname);
                    }

                    if (aReplyObject.has("link_details")) {
                        try {
                            JSONObject replyLinkObj = aReplyObject.getJSONObject("link_details");
                            String replyLocTitle = replyLinkObj.getString("title");
                            String replyLocThumbData = replyLinkObj.getString("thumbnail_data");
                        /*String replyLocThumbUrl = replyLinkObj.getString("image");
                        String replyLocDesc = replyLinkObj.getString("description");
                        String replyLocUrl = replyLinkObj.getString("url");*/

                            newMsgItem.setReplyMessage(replyLocTitle);
                            newMsgItem.setreplyimagebase64(replyLocThumbData);

                        /*item.setWebLinkImgUrl(replyLocThumbUrl);
                        item.setWebLinkDesc(replyLocDesc);
                        item.setWebLink(replyLocUrl);*/
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                // This block for Location and weblink messages
                if (msgData.has("link_details")) {
                    JSONObject linkObj = msgData.getJSONObject("link_details");

                    String recLink = "";
                    if (linkObj.has("url")) {
                        recLink = linkObj.getString("url");
                    }

                    String recLinkTitle = "";
                    if (linkObj.has("title")) {
                        recLinkTitle = linkObj.getString("title");
                    }

                    String recLinkDesc = "";
                    if (linkObj.has("description")) {
                        recLinkDesc = linkObj.getString("description");
                    }

                    String recLinkImgUrl = "";
                    if (linkObj.has("image")) {
                        recLinkImgUrl = linkObj.getString("image");
                    }

                    String recLinkImgThumb = "";
                    if (linkObj.has("thumbnail_data")) {
                        recLinkImgThumb = linkObj.getString("thumbnail_data");
                    }

                    newMsgItem.setWebLink(recLink);
                    newMsgItem.setWebLinkTitle(recLinkTitle);
                    newMsgItem.setWebLinkDesc(recLinkDesc);
                    newMsgItem.setWebLinkImgUrl(recLinkImgUrl);
                    newMsgItem.setWebLinkImgThumb(recLinkImgThumb);
                }

                return newMsgItem;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;

    }

    /**
     * get Timer Change in chat Message
     *
     * @param msgData set chat item data and updated the database value for delivery status
     * @return response value
     */
    private MessageItemChat getTimerChangeMessage(JSONObject msgData, MessageItemChat newMsgItem) {
        try {
            String from = msgData.getString("from");
            String to = msgData.getString("to");
            String convId = msgData.getString("convId");
            String recordId = msgData.getString("recordId");
            String timer = msgData.getString("incognito_timer");
            String timerMode = msgData.getString("incognito_timer_mode");
            String toUserMsgId;
            if (msgData.has("doc_id")) {
                toUserMsgId = msgData.getString("doc_id");
            } else {
                toUserMsgId = msgData.getString("docId");
            }
            String msgId = msgData.getString("id");
            String timeStamp = msgData.getString("timestamp");
            String fromMsisdn = msgData.getString("ContactMsisdn");

            MessageItemChat item = new MessageItemChat();
            String docId;
            if (from.equalsIgnoreCase(mCurrentUserId)) {
                docId = from + "-" + to;
                item.setReceiverID(to);
            } else {
                docId = to + "-" + from;
                item.setReceiverID(from);
            }

            item.setIsSelf(false);
            item.setIsDate(true);
            item.setConvId(convId);
            item.setRecordId(recordId);
            item.setSecretTimer(timer);
            item.setSecretTimeCreatedBy(from);
            item.setSecretTimerMode(timerMode);
            item.setMessageId(docId + "-" + msgId);
            item.setTS(timeStamp);
            item.setSenderMsisdn(fromMsisdn);
            item.setMessageType(MessageFactory.timer_change + "");
            item.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);

            MessageDbController db = CoreController.getDBInstance(mContext);
            docId = docId + "-" + MessageFactory.CHAT_TYPE_SECRET;
            db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SECRET);

            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
            contactDB_sqlite.updateSecretMessageTimer(to, timer, from, item.getMessageId());

            return item;

        } catch (JSONException e) {
            return null;
        }
    }

    /**
     * load Group Message From Weblink
     *
     * @param objects based on response to check media file downloading status & updated database
     * @return response value
     */
    public MessageItemChat loadGroupMessageFromWeb(JSONObject objects) {
        try {
            Integer type = Integer.parseInt(objects.getString("type"));

            String id = objects.getString("id");
            String from = objects.getString("from");
            String to = objects.getString("groupId");

            String toDocId = (String) objects.get("toDocId");
            String[] splitDocId = toDocId.split("-");
            String msgId = splitDocId[3];
            String name = objects.getString("msisdn");
//            String convId = objects.getString("convId");
            String recordId = objects.getString("recordId");
            String starredStatus = objects.getString("isStar");

            String ts = objects.getString("timestamp");
            if (session.getarchivecountgroup() != 0) {
                if (session.getarchivegroup(mCurrentUserId + "-" + to + "-g"))
                    session.removearchivegroup(mCurrentUserId + "-" + to + "-g");
            }

            MessageItemChat item = new MessageItemChat();
//            item.setConvId(convId);
            item.setRecordId(recordId);
            item.set_id(id);
            item.setStarredStatus(starredStatus);
            item.setTS(ts);
            item.setGroup(true);
            item.setGroupMsgFrom(from);
            item.setSenderName(name);
            item.setUploadStatus(MessageFactory.UPLOAD_STATUS_COMPLETED);
            item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_NOT_START);
            item.setMessageId(toDocId);
            String payLoad = objects.getString("payload");
            item.setTextMessage(payLoad);
            if (type == MessageFactory.picture || type == MessageFactory.audio || type == MessageFactory.video) {
                long filesize = AppUtils.parseLong(objects.getString("filesize"));
                filesize = session.getreceviedmedialength() + filesize;
                session.putreceivemedialength(filesize);
            }

            if (objects.has("replyDetails")) {
                JSONObject aReplyObject = objects.getJSONObject("replyDetails");
                item.setReplyMsisdn(aReplyObject.getString("From_msisdn"));
                item.setReplyFrom(aReplyObject.getString("from"));
                String userID = aReplyObject.getString("from");
                String replyname = aReplyObject.getString("From_msisdn");
                item.setReplyType(aReplyObject.getString("type"));
                if (aReplyObject.has("message")) {
                    item.setReplyMessage(aReplyObject.getString("message"));
                }
                if (aReplyObject.has("thumbnail_data")) {
                    String thumbnail_data = aReplyObject.getString("thumbnail_data");
                    item.setreplyimagebase64(thumbnail_data);
                }
                item.setReplyId(aReplyObject.getString("_id"));

                replyname = getcontactname.getSendername(userID, replyname);

                if (item.getReplyFrom().equalsIgnoreCase(mCurrentUserId)) {
                    item.setReplySender("you");
                } else {
                    item.setReplySender(replyname);
                }

                if (aReplyObject.has("link_details")) {
                    try {
                        JSONObject replyLinkObj = aReplyObject.getJSONObject("link_details");
                        String replyLocTitle = replyLinkObj.getString("title");
                        String replyLocThumbData = replyLinkObj.getString("thumbnail_data");

                        item.setReplyMessage(replyLocTitle);
                        item.setreplyimagebase64(replyLocThumbData);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            item.setMessageType("" + type);
            String strType = "" + type;
            if (strType.equalsIgnoreCase("" + MessageFactory.text)) {
                long messagelength = session.getreceviedmessagelength() + payLoad.length();
                session.putreceivemessagelength(messagelength);
                int receviedmesagecount = session.getreceviedmessagecount();
                receviedmesagecount = receviedmesagecount + 1;
                session.putreceivemessagecount(receviedmesagecount);

            } else if (strType.equalsIgnoreCase("" + MessageFactory.contact)) {
                String contactName = objects.getString("contact_name");
                String contactNumber = objects.getString("createdTomsisdn");
                String contactDetails = objects.getString("contact_details");
                if (objects.has("createdTo")) {
                    String contactChatappId = objects.getString("createdTo");
                    item.setContactchatappId(contactChatappId);
                }
                item.setContactName(contactName);
                item.setContactNumber(contactNumber);
                item.setDetailedContacts(contactDetails);
            } else if (strType.equalsIgnoreCase("" + MessageFactory.picture)) {
                String thumbnail = objects.getString("thumbnail");
                String fileSize = objects.getString("filesize");
                String thumbData = objects.getString("thumbnail_data");

                if (thumbnail != null && !thumbnail.equals("")) {
                    File dir = new File(mContext.getFilesDir() + MessageFactory.IMAGE_STORAGE_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    String localFileName = MessageFactory.getMessageFileName(
                            MessageFactory.picture, id, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
                    String filePath = mContext.getFilesDir() + MessageFactory.IMAGE_STORAGE_PATH + localFileName;
                    item.setChatFileLocalPath(filePath);
                }

                item.setChatFileServerPath(thumbnail);
                item.setFileBufferAt(0);
                item.setUploadDownloadProgress(0);

                item.setThumbnailData(thumbData);
                item.setFileSize(fileSize);
            } else if (strType.equalsIgnoreCase("" + MessageFactory.group_document_message)) {
                item.setMessageType("" + MessageFactory.document);

                String thumbnail = objects.getString("thumbnail");
                String thumbnailData = objects.getString("thumbnail_data");
                String originalFileName = objects.getString("original_filename");
                String fileSize = objects.getString("filesize");

                if (thumbnail != null && !thumbnail.equals("")) {
                    File dir = new File(mContext.getFilesDir() + MessageFactory.DOCUMENT_STORAGE_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    String localFileName = MessageFactory.getMessageFileName(
                            MessageFactory.document, id, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
                    String filePath = mContext.getFilesDir() + MessageFactory.DOCUMENT_STORAGE_PATH + localFileName;
                    item.setChatFileLocalPath(filePath);
                    item.setThumbnailData(thumbnailData);
                    item.setTextMessage(originalFileName);
                }

                item.setChatFileServerPath(thumbnail);
                item.setFileSize(fileSize);
            } else if (strType.equalsIgnoreCase("" + MessageFactory.audio)) {
                String thumbnail = objects.getString("thumbnail");
                String fileSize = objects.getString("filesize");

                if (thumbnail != null && !thumbnail.equals("")) {
                    File dir = new File(mContext.getFilesDir() + MessageFactory.AUDIO_STORAGE_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    String localFileName = MessageFactory.getMessageFileName(
                            MessageFactory.audio, id, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
                    String filePath = mContext.getFilesDir() + MessageFactory.AUDIO_STORAGE_PATH + localFileName;
                    item.setChatFileLocalPath(filePath);
                }

                String duration = objects.getString("duration");
                item.setChatFileServerPath(thumbnail);
                item.setFileSize(fileSize);
                item.setDuration(duration);
            } else if (strType.equalsIgnoreCase("" + MessageFactory.video)) {
                String thumbnail = objects.getString("thumbnail");
                String fileSize = objects.getString("filesize");

                if (thumbnail != null && !thumbnail.equals("")) {
                    File dir = new File(mContext.getFilesDir() + MessageFactory.VIDEO_STORAGE_PATH);
                    if (!dir.exists()) {
                        dir.mkdir();
                    }
                    String localFileName = MessageFactory.getMessageFileName(MessageFactory.video, id, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
                    String filePath = mContext.getFilesDir() + MessageFactory.VIDEO_STORAGE_PATH + localFileName;
                    item.setChatFileLocalPath(filePath);
                }

                item.setChatFileServerPath(thumbnail);

                String thumbData = objects.getString("thumbnail_data");
                String duration = objects.getString("duration");
                item.setDuration(duration);
                item.setThumbnailData(thumbData);
                item.setFileSize(fileSize);
            }
            if (objects.has("link_details")) {
                JSONObject linkObj = objects.getJSONObject("link_details");

                String recLink = "";
                if (linkObj.has("url")) {
                    recLink = linkObj.getString("url");
                }

                String recLinkTitle = "";
                if (linkObj.has("title")) {
                    recLinkTitle = linkObj.getString("title");
                }

                String recLinkDesc = "";
                if (linkObj.has("description")) {
                    recLinkDesc = linkObj.getString("description");
                }

                String recLinkImgUrl = "";
                if (linkObj.has("image")) {
                    recLinkImgUrl = linkObj.getString("image");
                }

                String recLinkImgThumb = "";
                if (linkObj.has("thumbnail_data")) {
                    recLinkImgThumb = linkObj.getString("thumbnail_data");
                }


                item.setWebLink(recLink);
                item.setWebLinkTitle(recLinkTitle);
                item.setWebLinkDesc(recLinkDesc);
                item.setWebLinkImgUrl(recLinkImgUrl);
                item.setWebLinkImgThumb(recLinkImgThumb);
            }
            item.setCount(1);

            // For showing Group name in chat list
            String groupName = objects.getString("groupName");
            item.setGroupName(groupName);

            item.sethasNewMessage(true);
            String docId;

            if (from.equalsIgnoreCase(mCurrentUserId)) {
                docId = from + "-" + to + "-g";
                item.setIsSelf(true);
                item.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_SENT);
            } else /*if (to.equalsIgnoreCase(uniqueCurrentID))*/ {
                item.setIsSelf(false);
                item.sethasNewMessage(true);
                docId = mCurrentUserId + "-" + to + "-g";
            }
            item.setReceiverID(to);
            item.setMessageId(docId.concat("-").concat(msgId));

            GroupInfoSession groupInfoSession = new GroupInfoSession(mContext);
            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);

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

//            JSONArray arrMsgStatus = objects.getJSONArray("status");

            return item;

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * load Single in chat Message
     *
     * @param objects based on response to check media file downloading status & updated database
     * @return response value
     */
    public MessageItemChat loadSingleMessage(JSONObject objects) {
        MessageItemChat item = new MessageItemChat();

        Log.d("Object", objects.toString());
        try {
            String type = objects.getString("type");

            if (type.equals(MessageFactory.missed_call + "")) {
                item = getMissedCallMessage(objects);
            } else {
                String payLoad = objects.getString("payload");
                String id = objects.getString("msgId");
                String from = objects.getString("from");
                if (objects.has("reply_type") && objects.getString("reply_type").equals("status")) {
                    item.setStatusReply(true);
                }

                if (type.equalsIgnoreCase(MessageFactory.text + "")) {
                    long messagelength = session.getreceviedmessagelength() + payLoad.length();
                    session.putreceivemessagelength(messagelength);
                    int receviedmesagecount = session.getreceviedmessagecount();
                    receviedmesagecount = receviedmesagecount + 1;
                    session.putreceivemessagecount(receviedmesagecount);
                    System.out.println("receviedmesagecount" + receviedmesagecount);
                } else if (type.equalsIgnoreCase(MessageFactory.picture + "") || type.equalsIgnoreCase(MessageFactory.audio + "")
                        || type.equalsIgnoreCase(MessageFactory.video + "") || type.equalsIgnoreCase(MessageFactory.document + "")) {
                    long filesize = AppUtils.parseLong(objects.getString("filesize"));
                    filesize = session.getreceviedmedialength() + filesize;
                    session.putreceivemedialength(filesize);
                }

                String thumbnail = objects.getString("thumbnail");
                String name = objects.getString("Name");
                String dataSize = objects.getString("filesize");
                String convId = objects.getString("convId");
                String recordId = objects.getString("recordId");
//            String starredStatus = objects.getString("isStar");
                String ts = objects.getString("timestamp");

                JSONObject linkObj;
                try {
                    linkObj = objects.getJSONObject("link_details");
                } catch (JSONException e) {
                    linkObj = new JSONObject();
                }
//            JSONObject linkObj = objects.getJSONObject("link_details");

                String webLink = "";
                if (linkObj.has("url")) {
                    webLink = linkObj.getString("url");
                }

                String webLinkTitle = "";
                if (linkObj.has("title")) {
                    webLinkTitle = linkObj.getString("title");
                }

                String webLinkDesc = "";
                if (linkObj.has("description")) {
                    webLinkDesc = linkObj.getString("description");
                }

                String webLinkImgUrl = "";
                if (linkObj.has("image")) {
                    webLinkImgUrl = linkObj.getString("image");
                }

                String webLinkImgThumb = "";
                if (linkObj.has("thumbnail_data")) {
                    webLinkImgThumb = linkObj.getString("thumbnail_data");
                }

                try {
                    if (objects.has("ContactMsisdn")) {
                        String senderMsisdn = objects.getString("ContactMsisdn");
                        String phContactName = senderMsisdn;
                        phContactName = getcontactname.getSendername(from, senderMsisdn);
                        item.setSenderName(phContactName);
                        item.setSenderMsisdn(senderMsisdn);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                item.setIsSelf(false);
                item.setTS(ts);
                item.setConvId(convId);
                item.setGroup(false);
                item.setRecordId(recordId);
                item.setStarredStatus(MessageFactory.MESSAGE_UN_STARRED);
               /* if (objects.has("replyDetails")) {
                    JSONObject aReplyObject = objects.getJSONObject("replyDetails");
                    item = getReplyDetailsByObject(aReplyObject, item);

                }*/

                if (objects.has("replyDetails")) {
                    JSONObject aReplyObject = objects.getJSONObject("replyDetails");
                    String replyname = "";
                    String userID = "";
                    if (aReplyObject.has("From_msisdn")) {
                        item.setReplyMsisdn(aReplyObject.getString("From_msisdn"));
                        replyname = aReplyObject.getString("From_msisdn");
                    }
                    if (aReplyObject.has("from")) {
                        item.setReplyFrom(aReplyObject.getString("from"));
                        userID = aReplyObject.getString("from");
                        item.setReplyType(aReplyObject.getString("type"));
                        if (aReplyObject.has("message")) {
                            item.setReplyMessage(aReplyObject.getString("message"));
                        }
                        if (aReplyObject.has("thumbnail_data")) {
                            String thumbnail_data = aReplyObject.getString("thumbnail_data");
                            item.setreplyimagebase64(thumbnail_data);
                        }
                        item.setReplyId(aReplyObject.getString("_id"));

                        replyname = getcontactname.getSendername(userID, replyname);

                        if (item.getReplyFrom().equalsIgnoreCase(mCurrentUserId)) {
                            item.setReplySender("you");
                        } else {
                            item.setReplySender(replyname);
                        }
                    }




                  /*  if (aReplyObject.has("link_details")) {
                        try {
                            JSONObject replyLinkObj = aReplyObject.getJSONObject("link_details");
//                            String replyLocTitle = replyLinkObj.getString("title");
                            String replyLocThumbData = replyLinkObj.getString("thumbnail_data");

                            item.setReplyMessage("5656556");
                            item.setreplyimagebase64(replyLocThumbData);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }*/
                }


                item.setMessageType("" + type);
                item.sethasNewMessage(true);
                item.setTextMessage(payLoad);

                item.setCount(1);

                if (type.equalsIgnoreCase("" + MessageFactory.text)) {
                    item.setTextMessage(payLoad);
                } else if (type.equalsIgnoreCase("" + MessageFactory.contact)) {
//                item.setContactInfo(payLoad);
                    String contactName = objects.getString("contact_name");
                    if (objects.has("createdTomsisdn")) {
                        String contactNumber = objects.getString("createdTomsisdn");
                        item.setContactNumber(contactNumber);
                    }
                    if (objects.has("contact_details")) {
                        String contactDetails = objects.getString("contact_details");
                        item.setDetailedContacts(contactDetails);
                    }
                    if (objects.has("createdTo")) {
                        String contactChatappId = objects.getString("createdTo");
                        item.setContactchatappId(contactChatappId);
//                        String AvtarImage = Constants.userprofileurl + contactChatappId + ".jpg?id=" + Calendar.getInstance().getTimeInMillis();
//                        item.setAvatarImageUrl(AvtarImage);
                    }

                    item.setContactName(contactName);

                } else if (type.equalsIgnoreCase("" + MessageFactory.picture)) {


                    String width = objects.getString("width");
                    String height = objects.getString("height");

                    if (thumbnail != null && !thumbnail.equals("")) {
                      /*  File dir = new File(Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES), MessageFactory.IMAGE_STORAGE_PATH);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }*/

                        File dir = new File(mContext.getFilesDir() + MessageFactory.IMAGE_STORAGE_PATH);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        String localFileName = MessageFactory.getMessageFileName(MessageFactory.picture, id, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
                        String filePath = mContext.getFilesDir() + MessageFactory.IMAGE_STORAGE_PATH + localFileName;
                        item.setChatFileLocalPath(filePath);
                        item.setChatFileWidth(width);
                        item.setChatFileHeight(height);
                    }

                    item.setChatFileServerPath(thumbnail);
                    if (objects.has("thumbnail_data")) {
                        String thumbData = objects.getString("thumbnail_data");
                        item.setThumbnailData(thumbData);
                    }
                    item.setFileSize(dataSize);
                } else if (type.equalsIgnoreCase("" + MessageFactory.audio)) {
                    String audiotype = "";
                    if (thumbnail != null && !thumbnail.equals("")) {
                        File dir = new File(mContext.getFilesDir() + MessageFactory.AUDIO_STORAGE_PATH);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        if (objects.has("audio_type")) {
                            audiotype = objects.getString("audio_type");
                            item.setaudiotype(Integer.parseInt(audiotype));
                        }

                        String localFileName = MessageFactory.getMessageFileName(
                                MessageFactory.audio, id, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
                        String filePath = mContext.getFilesDir() + MessageFactory.AUDIO_STORAGE_PATH + localFileName;
                        item.setChatFileLocalPath(filePath);
                    }

                    String duration = objects.getString("duration");
                    item.setDuration(duration);
                    item.setChatFileServerPath(thumbnail);
                    item.setFileSize(dataSize);
                } else if (type.equalsIgnoreCase("" + MessageFactory.document)) {

                    String originalFileName = objects.getString("original_filename");
//                    item.setMessageType("" + MessageFactory.document);
                    if (thumbnail != null && !thumbnail.equals("")) {
                        File dir = new File(mContext.getFilesDir() + MessageFactory.DOCUMENT_STORAGE_PATH);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        String localFileName = MessageFactory.getMessageFileName(
                                MessageFactory.document, id, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
                        String filePath = mContext.getFilesDir() + MessageFactory.DOCUMENT_STORAGE_PATH + localFileName;
                        item.setChatFileLocalPath(filePath);
                        item.setTextMessage(originalFileName);
                        item.setChatFileServerPath(thumbnail);
                        item.setFileSize(dataSize);

                        if (objects.has("thumbnail_data")) {
                            String thumbData = objects.getString("thumbnail_data");

                            item.setThumbnailData(thumbData);

                        }

                    }
                } else if (type.equalsIgnoreCase("" + MessageFactory.video)) {
                    if (thumbnail != null && !thumbnail.equals("")) {
                        File dir = new File(mContext.getFilesDir() + MessageFactory.VIDEO_STORAGE_PATH);
                        if (!dir.exists()) {
                            dir.mkdir();
                        }
                        String localFileName = MessageFactory.getMessageFileName(
                                MessageFactory.video, id, FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
                        String filePath = mContext.getFilesDir() + MessageFactory.VIDEO_STORAGE_PATH + localFileName;
                        item.setChatFileLocalPath(filePath);
                    }

                    item.setChatFileServerPath(thumbnail);

                    if (objects.has("thumbnail_data")) {
                        String thumbData = objects.getString("thumbnail_data");
                        item.setThumbnailData(thumbData);

                    }

                    String duration = objects.getString("duration");
                    item.setDuration(duration);
                    item.setFileSize(dataSize);
                }

                String docId = mCurrentUserId + "-" + from;
                item.setMessageId(docId + "-" + id);

                String secretType = objects.getString("secret_type");
                if (secretType.equalsIgnoreCase("yes")) {
                    docId = docId + "-" + MessageFactory.CHAT_TYPE_SECRET;
                }

               /* if (objects.has("replyId") && !objects.has("replyDetails")) {
                    String replyRecordId = objects.getString("replyId");
                    item.setReplyId(replyRecordId);

                    MessageDbController db = CoreController.getDBInstance(mContext);
                    MessageItemChat replyMsgItem = db.getMessageByRecordId(replyRecordId);
                    if (replyMsgItem == null) {
                        if (msgSvcCallback != null) {
                            msgSvcCallback.getReplyMessageDetails(from, convId, replyRecordId,
                                    MessageFactory.CHAT_TYPE_SINGLE, secretType, item.getMessageId());
                        }
                    } else {
                        item = getReplyDetailsByMessageItem(replyMsgItem, item);
                    }
                }*/


                if (objects.has("replyId") && !objects.has("replyDetails")) {
                    String replyRecordId = objects.getString("replyId");
                    if (msgSvcCallback != null) {
                        MessageDbController db = CoreController.getDBInstance(mContext);
                        MessageItemChat replyMsgItem = db.getMessageByRecordId(replyRecordId);
                        if (replyMsgItem == null) {
                            msgSvcCallback.getReplyMessageDetails(from, convId, replyRecordId, MessageFactory.CHAT_TYPE_SINGLE,
                                    "no", item.getMessageId());
                        } else {
                            item = getReplyDetailsByMessageItem(replyMsgItem, item);
                        }
                    }
                }

                item.setWebLink(webLink);
                item.setWebLinkTitle(webLinkTitle);
                item.setWebLinkDesc(webLinkDesc);
                item.setWebLinkImgUrl(webLinkImgUrl);
                item.setWebLinkImgThumb(webLinkImgThumb);
                item.setReceiverID(from);
                item.setFileBufferAt(0);
                item.setUploadDownloadProgress(0);
                item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_NOT_START);
                if (objects.has("is_expiry")) {
                    item.setIsExpiry(objects.getString("is_expiry"));

                }
                if (objects.has("expiry_time")) {
                    item.setExpiryTime(objects.getString("expiry_time"));
                }

            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return item;
    }

    /**
     * get Reply Details By specific message
     *
     * @param replyObject based on response to getting server data(thumbnail)
     * @param item        set the value from pojo class
     * @return response value
     */
    public MessageItemChat getReplyDetailsByObject(JSONObject replyObject, MessageItemChat item) {
        try {
            String fromMsisdn = "";
            if (replyObject.has("DisplayMsisdn")) {
                fromMsisdn = replyObject.getString("DisplayMsisdn");
            } else {
                if (replyObject.has("From_msisdn"))
                    fromMsisdn = replyObject.getString("From_msisdn");
                else if (replyObject.has("ContactMsisdn"))
                    fromMsisdn = replyObject.getString("ContactMsisdn");
            }
            item.setReplyMsisdn(fromMsisdn);

            item.setReplyFrom(replyObject.getString("from"));
            String userId = replyObject.getString("from");
            item.setReplyType(replyObject.getString("type"));
            if (replyObject.has("message")) {
                if (replyObject.has("original_filename")) {

                    String fileName = replyObject.getString("original_filename");
                    if (fileName != null && !fileName.trim().isEmpty())
                        item.setReplyMessage(replyObject.getString("original_filename"));
                    else
                        item.setReplyMessage(replyObject.getString("message"));

                } else {
                    item.setReplyMessage(replyObject.getString("message"));
                }

            }
            item.setReplyId(replyObject.getString("_id"));
            item.setReplyServerLoad(replyObject.getString("server_load"));
            if (replyObject.has("thumbnail_data")) {
                String thumbnail_data = replyObject.getString("thumbnail_data");
                String thumbnail_url = replyObject.getString("thumbnail");
                item.setreplyimagebase64(thumbnail_data);
                item.setThumbnailPath(thumbnail_url);
                //123
            }

            String replyname = getcontactname.getSendername(userId, fromMsisdn);

            if (item.getReplyFrom().equalsIgnoreCase(mCurrentUserId)) {
                item.setReplySender("you");
            } else {
                item.setReplySender(replyname);
            }

            if (replyObject.has("link_details")) {
                try {
                    JSONObject replyLinkObj = replyObject.getJSONObject("link_details");
                    String replyLocTitle = replyLinkObj.getString("title");
                    item.setReplyMessage(replyLocTitle);
                        /*String replyLocThumbUrl = replyLinkObj.getString("image");
                        String replyLocDesc = replyLinkObj.getString("description");
                        String replyLocUrl = replyLinkObj.getString("url");*/

                    String replyLocThumbData = replyLinkObj.getString("thumbnail_data");
                    item.setreplyimagebase64(replyLocThumbData);

                        /*item.setWebLinkImgUrl(replyLocThumbUrl);
                        item.setWebLinkDesc(replyLocDesc);
                        item.setWebLink(replyLocUrl);*/
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return item;
    }

    /**
     * get Reply Details By Message Item
     *
     * @param replyMsgItem set the value from pojo class
     * @param item         set the value from pojo class
     * @return response value
     */
    public MessageItemChat getReplyDetailsByMessageItem(MessageItemChat replyMsgItem, MessageItemChat item) {

        String repliedUserId, repliedMsisdn, replyname;
        if (replyMsgItem.isSelf()) {
            repliedUserId = mCurrentUserId;
            repliedMsisdn = SessionManager.getInstance(mContext).getPhoneNumberOfCurrentUser();
            replyname = "you";
        } else {
            repliedUserId = replyMsgItem.getReceiverID();
            repliedMsisdn = replyMsgItem.getSenderMsisdn();
            replyname = getcontactname.getSendername(repliedUserId, repliedMsisdn);
        }

        item.setReplyMsisdn(repliedMsisdn);
        item.setReplyFrom(repliedUserId);
        item.setReplySender(replyname);
        item.setReplyType(replyMsgItem.getMessageType());
        item.setReplyMessage(replyMsgItem.getTextMessage());
        item.setReplyId(replyMsgItem.getRecordId());
        item.setreplyimagebase64(replyMsgItem.getThumbnailData());

        if (replyMsgItem.getMessageType() != null &&
                (replyMsgItem.getMessageType().equals(MessageFactory.web_link + "") ||
                        replyMsgItem.getMessageType().equals(MessageFactory.location + ""))) {
            item.setReplyMessage(replyMsgItem.getWebLinkTitle());
            item.setreplyimagebase64(replyMsgItem.getWebLinkImgThumb());
        }

//        item.setReplyServerLoad(replyObject.getString("server_load"));

        return item;
    }

    /**
     * getting MissedCall in chat Message
     *
     * @param object based on the response call status updated
     * @return response value
     */
    public MessageItemChat getMissedCallMessage(JSONObject object) {
        MessageItemChat msgItem = new MessageItemChat();

        try {
            String type = object.getString("type");
            String from = object.getString("from");
            String to = object.getString("to");
            String id = object.getString("id");
            String recordId = object.getString("recordId");
            String convId = object.getString("convId");
            String senderMsisdn = object.getString("ContactMsisdn");

            String ts = object.getString("timestamp");

            String callId;
            if (object.has("docId")) {
                callId = object.getString("docId");
            } else {
                callId = object.getString("doc_id");
            }

            String docId = mCurrentUserId + "-" + from;
            String msgId = docId + "-" + id;

            msgItem.set_id(id);
            msgItem.setMessageId(msgId);
            msgItem.setConvId(convId);
            msgItem.setRecordId(recordId);
            msgItem.setReceiverID(from);
            msgItem.setMessageType(type);
            msgItem.setIsSelf(false);
            msgItem.setTS(ts);
            msgItem.setSenderMsisdn(senderMsisdn);
            msgItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);
            msgItem.setStarredStatus(MessageFactory.MESSAGE_UN_STARRED);

            // TODO: 8/18/2017 Need call_type for offline call logs from json
            String callType = object.getString("call_type");
            msgItem.setCallType(callType);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return msgItem;
    }

    /**
     * getting OutgoingCall in chat Message
     *
     * @param object based on the response call status updated
     * @return response value
     */
    public CallItemChat loadOutgoingCall(JSONObject object) {

        try {
            String type = object.getString("type");
            String from = object.getString("from");
            String to = object.getString("to");
            String id = object.getString("id");
            String recordId = object.getString("recordId");
            String toUserMsisdn = object.getString("To_msisdn");
            String fromUserMsisdn = object.getString("ContactMsisdn");
            String callStatus = object.getString("call_status");
            String callId = object.getString("doc_id");
            String ts = object.getString("timestamp");
            String receiverName = getcontactname.getSendername(to, toUserMsisdn);

            CallItemChat callItem = new CallItemChat();
            callItem.setId(id);
            callItem.setCallType(type);
            callItem.setOpponentUserId(to);
            callItem.setRecordId(recordId);
            callItem.setOpponentUserMsisdn(toUserMsisdn);
            callItem.setCallStatus(callStatus);
            callItem.setCallId(callId);
            callItem.setTS(ts);
            callItem.setIsSelf(true);
            callItem.setCallerName(receiverName);
//            callItem = CallMessage.getCallCount(from, to, callItem);
            return callItem;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * getting IncomingCall in chat Message
     *
     * @param object based on the response call status updated
     * @return response value
     */
    public CallItemChat loadIncomingCall(JSONObject object) {

        try {

            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
            String type = object.getString("type");
            String from = object.getString("from");
            String from_name = object.getString("from_name");
            String from_avatar = object.getString("From_avatar");
            String from_status = object.getString("from_status");
            String to = object.getString("to");
            String id = object.getString("id");
            String recordId = object.getString("recordId");
            String fromUserMsisdn = object.getString("ContactMsisdn");

            //String callStatus = object.getString("call_status");
            String ts = object.getString("timestamp");
//            String receiverName = getcontactname.getSendername(from, fromUserMsisdn);
//            String receiverName = object.getString("from_name");


            ChatappContactModel contact = new ChatappContactModel();
            ;

            contact.set_id(from);
            contact.setStatus(from_status);
            contact.setAvatarImageUrl(from_avatar);
            contact.setMsisdn(fromUserMsisdn);
            contact.setRequestStatus("3");

            contact.setFirstName(from_name);

            contactDB_sqlite.updateUserDetails(from, contact);


            String receiverName = getcontactname.getSendername(from, fromUserMsisdn);

            String callId = to + "-" + from + "-" + id;

            CallItemChat callItem = new CallItemChat();
            callItem.setId(id);
            callItem.setCallType(type);
            callItem.setOpponentUserId(from);
            callItem.setRecordId(recordId);
            callItem.setOpponentUserMsisdn(fromUserMsisdn);
            callItem.setCallStatus(MessageFactory.CALL_STATUS_ARRIVED + "");
            callItem.setCallId(callId);
            callItem.setTS(ts);
            callItem.setIsSelf(false);
            callItem.setCallerName(receiverName);
//            callItem = CallMessage.getCallCount(to, from, callItem);
            return callItem;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * To handling OfflineCall status
     *
     * @param object based on the response call status updated
     * @return response value
     */
    public CallItemChat loadOfflineCall(JSONObject object) {

        try {
            String type = object.getString("type");
            String from = object.getString("from");
            String to = object.getString("to");
            String id = object.getString("msgId");
            String recordId = object.getString("recordId");
            String fromUserMsisdn = "";
            if (object.has("ContactMsisdn")) {
                fromUserMsisdn = object.getString("ContactMsisdn");
            }

            String callStatus = object.getString("call_status");
            String ts = object.getString("timestamp");
            String receiverName = getcontactname.getSendername(from, fromUserMsisdn);

            String callId = to + "-" + from + "-" + id;

            CallItemChat callItem = new CallItemChat();
            callItem.setId(id);
            callItem.setCallType(type);
            callItem.setOpponentUserId(from);
            callItem.setRecordId(recordId);
            callItem.setOpponentUserMsisdn(fromUserMsisdn);
            callItem.setCallStatus(callStatus);
            callItem.setCallId(callId);
            callItem.setTS(ts);
            callItem.setIsSelf(false);
            callItem.setCallerName(receiverName);
            callItem.setCallDuration("00:00");
//            callItem = CallMessage.getCallCount(to, from, callItem);
            return callItem;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * setCallback function
     * @param msgSvcCallback inilization of MessageService
     */
    public void setCallback(MessageService msgSvcCallback) {
        this.msgSvcCallback = msgSvcCallback;
    }
}
