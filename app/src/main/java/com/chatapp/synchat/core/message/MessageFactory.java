package com.chatapp.synchat.core.message;

import android.content.Context;

import com.chatapp.synchat.app.calls.CallAck;

/**
 * Created by Administrator on 10/27/2016.
 */
public class MessageFactory {

    Context context;

    /**
     * status-0 not sent
     * status-1 sent
     * status-2 delivered
     * status-3 read
     */
    public static final String DELIVERY_STATUS_NOT_SENT = "0";
    public static final String DELIVERY_STATUS_SENT = "1";
    public static final String DELIVERY_STATUS_DELIVERED = "2";
    public static final String DELIVERY_STATUS_READ = "3";

    public static final String GROUP_MSG_DELIVER_ACK = "1";
    public static final String GROUP_MSG_READ_ACK = "2";

    public static final String MESSAGE_UN_STARRED = "0";
    public static final String MESSAGE_STARRED = "1";

    public static final String CHAT_TYPE_SINGLE = "single";
    public static final String CHAT_TYPE_GROUP = "group";
    public static final String CHAT_TYPE_STATUS = "status";
    public static final String CHAT_TYPE_SECRET = "secret";
    public static final String CHAT_TYPE_BROADCAST = "broadcast";
    public static final String CHAT_TYPE_CELEBRITY = "celebrity";
    public static final String CHAT_TYPE_CELEBRITY_VIDEO_THUMB = "celebrity-thumbnail";

    // public static final String BASE_STORAGE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/NowLetsChat";

    //generate file name and file path for the media to be recorded

    public static final String IMAGE_STORAGE_PATH = "/VersionChat_Images/";
    public static final String AUDIO_STORAGE_PATH = "/VersionChat_Audio/";
    public static final String VIDEO_STORAGE_PATH = "/VersionChat_Video/";
    public static final String DOCUMENT_STORAGE_PATH = "/VersionChat_Docs/";
    public static final String PROFILE_IMAGE_PATH = "/.Profile/";
    public static final String DATABASE_PATH = "/.Databases/";
    public static final String CHATS_PATH = "/.Chats/";
    public static final String GOOGLE_PHOTOS_PATH = "/GooglePhotos_Files/";
    public static final String GOOGLE_PHOTOS_PATH_BACKUP = "/Backup/";


    public static final int DOWNLOAD_STATUS_NOT_START = 0;
    public static final int DOWNLOAD_STATUS_DOWNLOADING = 1;
    public static final int DOWNLOAD_STATUS_COMPLETED = 2;

    public static final int UPLOAD_STATUS_UPLOADING = 0;
    public static final int UPLOAD_STATUS_COMPLETED = 1;

    public static final int CHAT_STATUS_UNCLEARED = 0;
    public static final int CHAT_STATUS_CLEARED = 1;

    public static final int AUDIO_FROM_RECORD = 1;
    public static final int AUDIO_FROM_ATTACHMENT = 2;

    public static final long TYING_MESSAGE_TIMEOUT = 3000;
    public static final long TYING_MESSAGE_MIN_TIME_DIFFERENCE = 2500;

//    public static final long VIDEO_FILE_MAX_UPLOAD_SIZE = 25000000; // 25 MB

    public static final int text = 0;
    public static final int picture = 1;
    public static final int video = 2;
    public static final int audio = 3;
    public static final int web_link = 4;
    public static final int contact = 5;
    public static final int document = 6;
    public static final int location = 14;
    public static final int message_ack = 11;
    public static final int call_ack = 61;
    public static final int missed_call = 21;
    public static final int nulldata = 22;


    public static final int join_new_group = 6; // Replace with other integer
    public static final int add_group_member = 7;
    public static final int change_group_icon = 8;
    public static final int delete_member_by_admin = 9;
    public static final int change_group_name = 10;
    public static final int make_admin_member = 11;
    public static final int exit_group = 12;
    public static final int group_document_message = 20;

    public static final int timer_change = 13;

    public static final int user_profile_pic_update = 50;
    public static final int group_profile_pic_update = 51;
    public static final int group_event_info = 52;

    public static final int audio_call = 0;
    public static final int video_call = 1;

    public static final int status_upload = 31;
    public static final int status_upload_ack = 32;
    public static final int offline_status = 33;
    public static final int delete_status = 34;
    public static final int mute_status = 35;


    public static final int CALL_STATUS_CALLING = 0;
    public static final int CALL_STATUS_ARRIVED = 1;
    public static final int CALL_STATUS_MISSED = 2;
    public static final int CALL_STATUS_ANSWERED = 3;
    public static final int CALL_STATUS_RECEIVED = 4;
    public static final int CALL_STATUS_REJECTED = 5;
    public static final int CALL_STATUS_END = 6;


    public static final int CALL_IN_FREE = 0;
    public static final int CALL_IN_RINGING = 1;
    public static final int CALL_IN_WAITING = 2;
    public static final int CALL_STATUS_PAUSE = 7;


    //-----------Delete Chat---------------
    public static final int DELETE_SELF = 25;
    public static final int DELETE_OTHER = 26;

    public static final int SCREEN_SHOT_TAKEN = 71;


    public static BaseMessage getMessage(int type, Context context) {
        switch (type) {
            case text:
                TextMessage message = new TextMessage(context);
                message.setType(type);
                return message;
            case picture:
                PictureMessage picMessage = new PictureMessage(context);
                picMessage.setType(type);
                return picMessage;
            case location:
                return new LocationMessage(context);
            case contact:
                ContactMessage contactMessage = new ContactMessage(context);
                contactMessage.setType(type);
                return contactMessage;
            case message_ack:
                return new MessageAck(context);
            case web_link:
                return new WebLinkMessage(context);
            case audio:
                return new AudioMessage(context);
            case document:
                return new DocumentMessage(context);
            case video:
                VideoMessage videoMessage = new VideoMessage(context);
                videoMessage.setType(type);
                return videoMessage;
            case group_event_info:
                GroupEventInfoMessage infoMessage = new GroupEventInfoMessage(context);
                infoMessage.setType(type);
                return infoMessage;
            case timer_change:
                TimerChangeMessage timerChangeMessage = new TimerChangeMessage(context);
                timerChangeMessage.setType(type);
                return timerChangeMessage;
            case call_ack:
                return new CallAck(context);
            case status_upload:
                return new StatusUploadMessage(context);
            default:
                return new TextMessage(context);

        }
    }

    public static String getMessageFileName(int msgType, String msgId, String fileExtn) {

        String fileName = "";
        String baseName = "VersionChat_";

        switch (msgType) {
            case MessageFactory.picture:
                fileName = baseName + "img_" + msgId + fileExtn;
                break;

            case MessageFactory.audio:
                fileName = baseName + "aud_" + msgId + fileExtn;
                break;

            case MessageFactory.document:
                fileName = baseName + "doc_" + msgId + fileExtn;
                break;

            case MessageFactory.video:
                fileName = baseName + "vid_" + msgId + fileExtn;
                break;
        }
        return fileName;
    }
}
