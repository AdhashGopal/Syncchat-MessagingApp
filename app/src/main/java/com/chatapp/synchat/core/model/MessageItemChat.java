package com.chatapp.synchat.core.model;
/**
 * Setter-Getter class for chat messages
 */

import android.net.Uri;

import org.appspot.apprtc.util.CryptLib;
import com.chatapp.synchat.core.message.MessageFactory;

import java.io.Serializable;


/**
 *
 */
public class MessageItemChat extends ChatappContactModel implements Serializable {


    private String message, MessageDateOverlay, MessageType, id, originalName, fromName, videoPath, imagepath, audioPath, ts, date, DeliveryStatus, isExpiry, expiryTime;
    private boolean isSelf;
    private boolean isDate;

    private long MessageDateGMT;
    private Uri imageUrl;
    private int downloadStatus, playerCurrentPosition = 0, playerMaxDuration = 0;
    private String thumbnailPath, webLink, webLinkTitle, webLinkDesc, webLinkImgUrl, webLinkImgThumb, groupId;

    private int uploadStatus = MessageFactory.UPLOAD_STATUS_UPLOADING;
    private Boolean iscontactthere = false;
    private String chatFileLocalPath;
    private String chatFileServerPath;
    private String contactName, contactNumber = "", contactChatappId, DetailedContacts;

    private String fileSize, duration, chatFileWidth, chatFileHeight;
    private String DeliveryTime, ReadTime, groupMsgDeliverStatus, msgSentAt, secretMsgReadAt;
    private String receiverUid;
    private boolean isNewMessage, isMediaPlaying;
    private String newMessageCount;
    private int count;
    private int uploadDownloadProgress;
    private Object object;
    private String senderMsisdn, callType;
    boolean selected = false;
    private int fileBufferAt;
    private boolean isStatusReply;
    private String replyimagepath = "";
    //    private boolean isStarred;
    private String groupMsgFrom;
    private String groupName, prevGroupName, groupEventType;
    private String recordId, convId, thumbnailData;
    private String receiverName, secretTimerMode, secretTimer, secretTimeCreatedBy;
    private String receiverID, createdByUserId, createdToUserId;
    private String timerecevied;
    private String starredStatus = MessageFactory.MESSAGE_UN_STARRED;
    //    private boolean isLiveGroup = true; // User live in a group by default
    private boolean isInfoMsg = false; // Information message always false, while sent group event only it changed true
    private String replyMsisdn = "";
    private String replyFrom = "";
    private String replyType = "";
    private String replyMessage = "";
    private String replyId = "";
    private String replyServerLoad = "";
    private String replysender = "";
    private String imgreplyrecevie = "";
    private long toTypingAt = 0;
    private long toRecordingAt = 0;
    private String typingPerson = "";
    private String caption;
    private int audiotype;
    private String statusDocId;
    private boolean isSecretChat;
    private boolean isthisGroup = false;
    private boolean isthisBlockedMessage = false;
    private boolean filteredMessage = false;


    public boolean isGroup() {
        return isthisGroup;
    }

    public void setGroup(boolean group) {
        isthisGroup = group;
    }

    public boolean isBlockedMessage() {
        return isthisBlockedMessage;
    }

    public void setBlockedMessage(boolean blockedMessage) {
        isthisBlockedMessage = blockedMessage;
    }


    public boolean isFilteredMessage() {
        return filteredMessage;
    }

    public void setFilteredMessage(boolean filteredMessage) {
        this.filteredMessage = filteredMessage;
    }


    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }


    public String getMsgSentAt() {
        return msgSentAt;
    }

    public void setMsgSentAt(String msgSentAt) {
        this.msgSentAt = msgSentAt;
    }

    public String getSecretMsgReadAt() {
        return secretMsgReadAt;
    }

    public void setSecretMsgReadAt(String secretMsgReadAt) {
        this.secretMsgReadAt = secretMsgReadAt;
    }

    public String getSecretTimeCreatedBy() {
        return secretTimeCreatedBy;
    }

    public void setSecretTimeCreatedBy(String secretTimeCreatedBy) {
        this.secretTimeCreatedBy = secretTimeCreatedBy;
    }

    public String getSecretTimerMode() {
        return secretTimerMode;
    }

    public void setSecretTimerMode(String secretTimerMode) {
        this.secretTimerMode = secretTimerMode;
    }

    public String getSecretTimer() {
        return secretTimer;
    }

    public void setSecretTimer(String secretTimer) {
        this.secretTimer = secretTimer;
    }

    public int getPlayerCurrentPosition() {
        return playerCurrentPosition;
    }

    public void setPlayerCurrentPosition(int playerCurrentPosition) {
        this.playerCurrentPosition = playerCurrentPosition;
    }

    public int getPlayerMaxDuration() {
        return playerMaxDuration;
    }

    public void setPlayerMaxDuration(int playerMaxDuration) {
        this.playerMaxDuration = playerMaxDuration;
    }

    public boolean isMediaPlaying() {
        return isMediaPlaying;
    }

    public void setIsMediaPlaying(boolean mediaPlaying) {
        isMediaPlaying = mediaPlaying;
    }

    public String getGroupEventType() {
        return groupEventType;
    }

    public void setGroupEventType(String groupEventType) {
        this.groupEventType = groupEventType;
    }

    public String getPrevGroupName() {
        return prevGroupName;
    }

    public void setPrevGroupName(String prevGroupName) {
        this.prevGroupName = prevGroupName;
    }

    public String getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getCreatedToUserId() {
        return createdToUserId;
    }

    public void setCreatedToUserId(String createdToUserId) {
        this.createdToUserId = createdToUserId;
    }

    public String getGroupMsgDeliverStatus() {
        return groupMsgDeliverStatus;
    }

    public void setGroupMsgDeliverStatus(String groupMsgDeliverStatus) {
        this.groupMsgDeliverStatus = groupMsgDeliverStatus;
    }

    public int getaudiotype() {
        return audiotype;
    }

    public void setaudiotype(int audiotype) {
        this.audiotype = audiotype;
    }

    public void setTypePerson(String typingPerson) {
        this.typingPerson = typingPerson;
    }

    public String getTypePerson() {
        return typingPerson;
    }

    public long getTypingAt() {
        return toTypingAt;
    }

    public void setTypingAt(long toTypingAt) {
        this.toTypingAt = toTypingAt;
    }

    public long getRecordingAt() {
        return toRecordingAt;
    }

    public void setRecordingAt(long toRecordingAt) {
        this.toRecordingAt = toRecordingAt;
    }
    public String getChatFileWidth() {
        return chatFileWidth;
    }

    public void setChatFileWidth(String chatFileWidth) {
        this.chatFileWidth = chatFileWidth;
    }

    public String getChatFileHeight() {
        return chatFileHeight;
    }

    public void setChatFileHeight(String chatFileHeight) {
        this.chatFileHeight = chatFileHeight;
    }

    public int getUploadStatus() {
        return uploadStatus;
    }

    public void setUploadStatus(int uploadStatus) {
        this.uploadStatus = uploadStatus;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactChatappId() {
        return contactChatappId;
    }

    public void setContactchatappId(String contactChatappId) {
        this.contactChatappId = contactChatappId;
    }

    public void setreplyimagebase64(String imgreplyrecevie) {
        this.imgreplyrecevie = imgreplyrecevie;
    }

    public String getreplyimagebase64() {
        return imgreplyrecevie;
    }

    public String getreplyimgpath() {
        return replyimagepath;
    }

    public void setreplyimagepath(String replyimagepath) {
        this.replyimagepath = replyimagepath;
    }

    public String getReplySenser() {

        return replysender;
    }

    public void setReplySender(String replysender) {

        this.replysender = replysender;
    }

    public String getReplyFrom() {
        return replyFrom;
    }

    public void setReplyFrom(String replyFrom) {
        this.replyFrom = replyFrom;
    }

    public String getReplyMsisdn() {
        return replyMsisdn;
    }

    public void setReplyMsisdn(String replyMsisdn) {
        this.replyMsisdn = replyMsisdn;
    }

    public String getReplyType() {
        return replyType;
    }

    public void setReplyType(String replyType) {
        this.replyType = replyType;
    }

    public String getReplyMessage() {

        try {
            CryptLib cryptLib = new CryptLib();

            return cryptLib.decryptCipherTextWithRandomIV(replyMessage, getValuableValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return replyMessage;
    }


    private String getValuableValue() {
        if (isGroup()) {
            return "Chatapp" + receiverID + "Adani";
        }
        return "Chatapp" + convId + "Adani";

        // return Constants.DUMMY_KEY;
    }

    public void setReplyMessage(String replyMessage) {
        this.replyMessage = replyMessage;
    }

    public String getReplyId() {
        return replyId;
    }

    public void setReplyId(String replyId) {
        this.replyId = replyId;
    }

    public String getReplyServerLoad() {
        return replyServerLoad;
    }

    public void setReplyServerLoad(String replyServerLoad) {
        this.replyServerLoad = replyServerLoad;
    }

    public int getUploadDownloadProgress() {
        return uploadDownloadProgress;
    }

    public void setUploadDownloadProgress(int uploadDownloadProgress) {
        this.uploadDownloadProgress = uploadDownloadProgress;
    }

    public String getChatFileLocalPath() {
        return chatFileLocalPath;
    }

    public void setChatFileLocalPath(String chatFileLocalPath) {
        this.chatFileLocalPath = chatFileLocalPath;
    }

    public String getChatFileServerPath() {
        return chatFileServerPath;
    }

    public void setChatFileServerPath(String chatFileServerPath) {
        this.chatFileServerPath = chatFileServerPath;
    }

    public String getSenderMsisdn() {
        return senderMsisdn;
    }

    public void setSenderMsisdn(String senderMsisdn) {
        this.senderMsisdn = senderMsisdn;
    }

    public String getWebLink() {

        try {
            CryptLib cryptLib = new CryptLib();

            return cryptLib.decryptCipherTextWithRandomIV(webLink, getValuableValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return webLink;
    }

    public void setWebLink(String webLink) {
        this.webLink = webLink;
    }

    public String getWebLinkTitle() {
        return webLinkTitle;
    }

    public void setWebLinkTitle(String webLinkTitle) {
        this.webLinkTitle = webLinkTitle;
    }

    public String getWebLinkDesc() {
        return webLinkDesc;
    }

    public void setWebLinkDesc(String webLinkDesc) {
        this.webLinkDesc = webLinkDesc;
    }

    public String getWebLinkImgUrl() {

        return webLinkImgUrl;
    }

    public void setWebLinkImgUrl(String webLinkImgUrl) {
        this.webLinkImgUrl = webLinkImgUrl;
    }

    public String getWebLinkImgThumb() {
        return webLinkImgThumb;
    }

    public void setWebLinkImgThumb(String webLinkImgThumb) {
        this.webLinkImgThumb = webLinkImgThumb;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getFileBufferAt() {
        return fileBufferAt;
    }

    public void setFileBufferAt(int fileBufferAt) {
        this.fileBufferAt = fileBufferAt;
    }

    public String getThumbnailData() {
        try {
            CryptLib cryptLib = new CryptLib();

            String imageDataBytes = thumbnailData.substring(thumbnailData.indexOf(",") + 1);


            return cryptLib.decryptCipherTextWithRandomIV(imageDataBytes, getValuableValue());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbnailData;
    }

    public void setThumbnailData(String thumbnailData) {
        this.thumbnailData = thumbnailData;
    }

    public String getStarredStatus() {
        return starredStatus;
    }

    public void setStarredStatus(String starredStatus) {
        this.starredStatus = starredStatus;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getConvId() {
        return convId;
    }

    public void setConvId(String convId) {
        this.convId = convId;
    }

    public boolean isInfoMsg() {
        return isInfoMsg;
    }

    public void setIsInfoMsg(boolean infoMsg) {
        isInfoMsg = infoMsg;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

/*    public boolean isLiveGroup() {
        return isLiveGroup;
    }

    public void setIsLiveGroup(boolean liveGroup) {
        isLiveGroup = liveGroup;
    }*/

    public String getGroupMsgFrom() {
        return groupMsgFrom;
    }

    public void setGroupMsgFrom(String groupMsgFrom) {
        this.groupMsgFrom = groupMsgFrom;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public boolean hasNewMessage() {
        return isNewMessage;
    }

    public void sethasNewMessage(boolean isNewMessage) {
        this.isNewMessage = isNewMessage;
    }


    public void setMessageId(String id) {
        this.id = id;
    }

    public String getMessageId() {
        return id;
    }


    public String getMessageDateOverlay() {
        return MessageDateOverlay;
    }

    public void setMessageDateOverlay(String MessageDateOverlay) {
        this.MessageDateOverlay = MessageDateOverlay;
    }


    public long getMessageDateGMTEpoch() {
        return MessageDateGMT;
    }

    public void setMessageDateGMTEpoch(long MessageDateGMT) {
        this.MessageDateGMT = MessageDateGMT;
    }

    public String getDeliveryTime() {
        return DeliveryTime;
    }

    public void setDeliveryTime(String DeliveryTime) {
        this.DeliveryTime = DeliveryTime;
    }

    public String getReadTime() {
        return ReadTime;
    }

    public void setReadTime(String ReadTime) {
        this.ReadTime = ReadTime;
    }

    public String getTS() {
        return ts;
    }

    public void setTS(String ts) {
        this.ts = ts;
    }

    /*public String getTime() {
        return timerecevied;
    }

    public void setTime(String timerecevied) {
        this.timerecevied = timerecevied;
    }*/


    public String getSenderName() {
        return fromName;
    }

    public void setSenderName(String fromName) {
        this.fromName = fromName;
    }


    public String getSenderOriginalName() {
        return originalName;
    }

    public void setSenderOriginalName(String originalName) {
        this.originalName = originalName;
    }


    public boolean isSelf() {
        return isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }


    /**
     * 0-text,1-image,2-video,3-location,4-contact,5-audio
     */
    public void setMessageType(String MessageType) {
        this.MessageType = MessageType;
    }

    public String getMessageType() {
        return MessageType;
    }


    public String getTextMessage() {

        try {

            CryptLib  cryptLib = new CryptLib();

            return  cryptLib.decryptCipherTextWithRandomIV(message, getValuableValue());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return message;


    }

    public void setTextMessage(String message) {
        this.message = message;
    }


    public void setImagePath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getImagePath() {
        return imagepath;
    }


    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public String getAudioPath() {
        return audioPath;
    }


    public boolean isDate() {
        return isDate;
    }

    public void setIsDate(boolean isDate) {
        this.isDate = isDate;
    }


    public void setDate(String date) {
        this.date = date;
    }


    public String getDate() {
        return date;
    }


    /**
     * status-0 not sent
     * status-1 sent
     * status-2 delivered
     * status-3 read
     */


    public String getDeliveryStatus() {
        return DeliveryStatus;
    }

    public void setDeliveryStatus(String DeliveryStatus) {
        this.DeliveryStatus = DeliveryStatus;
    }


    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }


    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }


    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }


    public Uri getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(Uri imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getNewMessageCount() {
        return newMessageCount;
    }

    public void setNewMessageCount(String newMessageCount) {
        this.newMessageCount = newMessageCount;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public Boolean getcontactsavethere() {
        return iscontactthere;
    }

    public void setcontactsavethere(Boolean iscontactthere) {
        this.iscontactthere = iscontactthere;
    }

    public String getDetailedContacts() {
        return DetailedContacts;
    }

    public void setDetailedContacts(String DetailedContacts) {
        this.DetailedContacts = DetailedContacts;
    }

    public boolean isStatusReply() {
        return isStatusReply;
    }

    public void setStatusReply(boolean statusReply) {
        isStatusReply = statusReply;
    }

    public String getStatusDocId() {
        return statusDocId;
    }

    public void setStatusDocId(String statusDocId) {
        this.statusDocId = statusDocId;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public boolean isSecretChat() {
        return isSecretChat;
    }

    public void setSecretChat(boolean secretChat) {
        isSecretChat = secretChat;
    }

    public String getIsExpiry() {
        return isExpiry;
    }

    public void setIsExpiry(String isExpiry) {
        this.isExpiry = isExpiry;
    }

    public String getExpiryTime() {
        return expiryTime;
    }

    public void setExpiryTime(String expiryTime) {
        this.expiryTime = expiryTime;
    }

}
