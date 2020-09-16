package com.chatapp.synchat.core.socket;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.service.Constants;
//import com.scottyab.aescrypt.AESCrypt;


import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashSet;

import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 *
 */
public class SocketManager {
    private Activity activity;
    private SocketCallBack callBack;
    /*
       Field name & socket key name
     */
    public static final String ACTION_NEW_ADD_GROUP_MEMBER = "23";
    public static final String EVENT_DELETE_ALL_READ_MESSAGES = "sc_delete_all_read_messages";
    public static final String EVENT_NEW_MESSAGE = "new message";
    public static final String EVENT_CREATE_USER = "create_user";
    public static final String EVENT_USER_CREATED = "usercreated";
    public static final String EVENT_REMOVE_USER = "remove_user";
    public static final String EVENT_DELETE_ACCOUNT = "sc_delete_account";
    public static final String EVENT_REMOVE_USER_ACCOUNT = "sc_remove_user_account";
    public static final String EVENT_USER_JOINED = "user joined";
    public static final String EVENT_HEARTBEAT = "Heartbeat";
    public static final String EVENT_MESSAGE_RES = "sc_message_response";
    public static final String EVENT_STOP_TYPING = "stop typing";
    public static final String EVENT_MESSAGE = "sc_message";
    public static final String EVENT_STATUS = "sc_media_status";
    public static final String EVENT_STATUS_RESPONSE = "sc_media_status_response";
    public static final String EVENT_STATUS_UPDATE = "sc_media_message_status_update";
    public static final String EVENT_STATUS_ACK = "sc_media_status_ack";
    public static final String EVENT_STATUS_OFFLINE = "sc_get_offline_status";
    public static final String EVENT_STATUS_DELETE = "sc_remove_media_status";
    public static final String EVENT_STATUS_MUTE = "sc_mute_status";
    public static final String EVENT_STATUS_FETCH_ALL = "sc_get_media_status";
    public static final String EVENT_STATUS_PRIVACY = "sc_media_status_privacy";
    public static final String EVENT_QR_DATA_RES = "qrdataresponsCe";
    public static final String EVENT_GROUP = "group";
    public static final String EVENT_REPLY_MESSAGE = "ReplyMessage";
    public static final String EVENT_GET_MESSAGE = "sc_get_offline_messages";
    public static final String EVENT_MESSAGE_STATUS_UPDATE = "sc_message_status_update";
    public static final String EVENT_MESSAGE_ACK = "sc_message_ack";
    public static final String EVENT_GET_CURRENT_TIME_STATUS = "getCurrentTimeStatus";
    public static final String EVENT_CLEAR_CHAT = "sc_clear_chat";
    public static final String EVENT_ARCHIVE_UNARCHIVE = "sc_archived_chat";
    public static final String EVENT_CAHNGE_ONLINE_STATUS = "sc_change_online_status";
    public static final String EVENT_UPDATE_ONLINE_STATUS = "UpdateOnlineStatus";
    public static final String EVENT_BROADCAST_PROFILE = "broadCastProfile";
    public static final String EVENT_CHANGE_ST = "sc_change_status";
    public static final String EVENT_TYPING = "sc_typing";
    public static final String EVENT_RECORDING = "sc_recording";
    public static final String EVENT_STOP_RECORDING = "sc_stop_recording";
    public static final String EVENT_CREATE_ROOM = "create room";
    public static final String EVENT_ADD_CONTACT = "add contact";
    public static final String EVENT_GET_FAVORITE = "GetPhoneContact";
    public static final String EVENT_GET_CONTACTS = "sc_contacts";
    public static final String EVENT_QR_DATA = "qrdata";
    public static final String EVENT_IMAGE_UPLOAD = "uploadImage";
    public static final String EVENT_GET_GROUP_LIST = "app/getGroupList";
    public static final String EVENT_GROUP_DETAILS = "getGroupDetails";
    public static final String EVENT_MOBILE_TO_WEB_LOGOUT = "mobileToWebLogout";
    public static final String EVENT_LOGOUT_OLD_USER = "LogoutOldUser";
    public static final String EVENT_STAR_MESSAGE = "StarMessage";
    public static final String EVENT_REMOVE_MESSAGE = "RemoveMessage";
    public static final String EVENT_DELETE_CHAT = "sc_delete_chat";
    public static final String EVENT_CHANGE_USER_NAME = "changeName";
    public static final String EVENT_FILE_UPLOAD = "app/fileUpload";
    public static final String EVENT_FILE_RECEIVED = "app/received";
    public static final String EVENT_FILE_DOWNLOAD = "app/fileDownload";
    public static final String EVENT_START_FILE_DOWNLOAD = "app/download";
    public static final String EVENT_GET_MOBILE_SETTINGS = "GetMobileSettings";
    public static final String EVENT_GET_USER_DETAILS = "sc_get_user_Details";
    public static final String EVENT_UPDATE_MOBILE_LOGIN_NOTIFICATION = "updateMobilePushNotificationKey";
    public static final String EVENT_REMOVE_MOBILE_LOGIN_NOTIFICATION = "unsetMobilePushNotificationKey";
    public static final String EVENT_CHECK_MOBILE_LOGIN_KEY = "checkMobileLoginKey";
    public static final String EVENT_BLOCK_USER = "sc_block_user";
    public static final String EVENT_MUTE = "sc_mute_chat";
    public static final String EVENT_MARKREAD = "sc_marked_chat";
    public static final String EVENT_PRIVACY_SETTINGS = "sc_privacy_settings";
    public static final String EVENT_PHONE_DOWNLOAD = "app/phoneDownload";
    public static final String EVENT_PHONE_DATA_RECEIVED = "app/phoneDataReceived";
    public static final String EVENT_REPORT_SPAM_USER = "sc_report_spam_user";
    public static final String EVENT_VIEW_CHAT = "sc_view_chat";
    public static final String EVENT_NEW_FILE_MESSAGE = "sc_new_message";
    public static final String EVENT_CHANGE_PROFILE_STATUS = "changeProfileStatus";
    public static final String EVENT_GET_MESSAGE_INFO = "app/getMessageInfo";
    public static final String EVENT_GET_SERVER_TIME = "sc_get_server_time";
    public static final String EVENT_CHANGE_SECRET_MSG_TIMER = "sc_change_timer";
    public static final String EVENT_TO_CONV_SETTING = "sc_to_conv_settings";
    public static final String EVENT_CHANGE_EMAIL = "sc_change_email";
    public static final String EVENT_RECOVERY_EMAIL = "sc_change_recovery_email";
    public static final String EVENT_RECOVERY_PHONE = "sc_change_recovery_phone";
    public static final String EVENT_UPDATE_GOOGLE_DRIVE_SETTINGS = "sc_drive_settings";
    public static final String EVENT_GET_GOOGLE_DRIVE_SETTINGS = "sc_get_drive_settings";
    public static final String EVENT_CHAT_LOCK = "sc_chat_lock";
    public static final String EVENT_CHAT_LOCK_FROM_WEB = "sc_set_mobile_password_chat_lock";
    public static final String EVENT_VERIFY_EMAIL = "sc_verify_email";
    public static final String EVENT_GET_APP_SETTINGS = "sc_app_settings";
    public static final String EVENT_GET_USER_STATUS = "sc_user_status";
    //TODO NEED TO INTEGRATE WITH WEB SERVICE
    public static final String EVENT_RATING = "sc_rating";
    public static final String EVENT_GET_UPLOADED_FILE_SIZE = "app/getFilesizeInBytes";
    public static final String EVENT_GET_SETTINGS = "sc_settings";
    public static final String EVENT_GET_CONV_ID = "getMobileConvId";
    public static final String EVENT_GET_ADMIN_SETTINGS = "changeAdminSettings";
    public static final String EVENT_CALL = "sc_call";
    public static final String EVENT_CALL_RESPONSE = "sc_call_response";
    public static final String EVENT_CALL_ACK = "sc_call_ack";
    public static final String EVENT_CALL_STATUS = "sc_call_status";
    public static final String EVENT_CALL_STATUS_RESONSE = "sc_call_status_response";
    public static final String EVENT_GET_OFFLINE_CALLS = "sc_get_offline_calls";
    public static final String EVENT_REMOVE_CALLS = "RemoveCalls";
    public static final String EVENT_REMOVE_ALL_CALLS = "RemoveAllCalls";
    public static final String EVENT_RETRY_CALL_CONNECT = "sc_call_retry";
    public static final String EVENT_DISCONNECT_CALL = "disconnect_call";
    public static final String EVENT_SKIP_BACKUP_MESSAGES = "sc_skipbackup_messages";
    public static final String EVENT_CONV_SETTINGS = "sc_conv_settings";
    public static final String EVENT_GET_MESSAGE_DETAILS = "sc_get_message_details";
    public static final String EVENT_REMOVED_ACCOUNT_BY_ADMIN = "RemovedByAdmin";
    public static final String EVENT_CALL_WEBRTC_MESSAGE = "sc_webrtc_message";
    public static final String EVENT_LEAVE_GROUP = "sc_leave_group";

    public static final String EVENT_ADMIN_APPROVED = "approved";

    public static final String EVENT_ADMIN_RESET_PIN = "resetpin";

    public static final String EVENT_ALLOW_PAYLOAD_INSTALL = "allow_payload_install";

    //------------Delete Chat---------------------------------------
    public static final String EVENT_DELETE_MESSAGE = "sc_remove_message_everyone";
    public static final String EVENT_SINGLE_ACK = "sc_deleted_message_ack";
    public static final String EVENT_SINGLE_OFFLINE_MSG = "sc_get_offline_deleted_messages";
    public static final String ACTION_EVENT_GROUP_ACK = "21";
    public static final String ACTION_EVENT_GROUP_OFFLINE = "20";
    public static final String ACTION_EVENT_GROUP_MSG_DELETE = "19";
    public static final String ACTION_EVENT_GROUP_MSG_ALL_READ = "99";

    //------------Celebrity Events-----------------------------------

    public static final String EVENT_FOLLOW_LIST = "sc_celebrity_follow_search";
    public static final String EVENT_SUGGESTION_LIST = "sc_celebrity_search";
    public static final String EVENT_FOLLOWERS_LIST = "sc_celebrity_follower_search";
    public static final String EVENT_FOLLOW_STATUS = "sc_celebrity_follow_event";
    public static final String EVENT_CELEBRITY_PROFILE = "sc_get_celebrity_details";
    public static final String EVENT_CREATE_POST = "sc_celebrity_uploads";
    public static final String EVENT_NEW_POST = "sc_new_celebrity_post";
    public static final String EVENT_MY_POST_FEED = "sc_get_my_feed";
    public static final String EVENT_FEEDS_TIMELINE = "sc_timeline_feed";
    public static final String EVENT_LIKE = "sc_feed_like_event";
    public static final String EVENT_FOLLOWING_IDS = "sc_get_followings_ids";

    //----------------------Celebrity Comments ----------------------------------------

    public static final String EVENT_COMMENTS = "sc_feed_comment_event";

    public static final String EVENT_GET_COMMENTS = "sc_get_feed_comment";

    public static final String EVENT_EDIT_COMMENTS = "sc_edit_feed_comment_event";

    public static final String EVENT_DELETE_COMMENT = "sc_delete_feed_comment_event";

    public static final String ACTION_NEW_GROUP = "1";
    public static final String ACTION_CHANGE_GROUP_DP = "2";
    public static final String ACTION_DELETE_GROUP_MEMBER = "4";
    public static final String ACTION_ADD_GROUP_MEMBER = "5";
    public static final String ACTION_CHANGE_GROUP_NAME = "6";
    public static final String ACTION_MAKE_GROUP_ADMIN = "7";
    public static final String ACTION_EXIT_GROUP = "8";
    public static final String ACTION_EVENT_GROUP_MESSAGE = "9";
    public static final String ACTION_JOIN_NEW_GROUP = "10";
    public static final String ACTION_ACK_GROUP_EVENTS = "11";
    public static final String ACTION_ACK_GROUP_MESSAGE = "12";
    public static final String ACTION_GET_GROUP_CHAT_HISTORY = "13";
    public static final String ACTION_GET_OFFLINE_CREATED_GROUPS = "14";
    public static final String ACTION_EVENT_GROUP_REPlY_MESSAGE = "18";


    public static final long RESPONSE_TIMEOUT = 45000;  // 45 seconds
    public static final long CONTACT_REFRESH_TIMEOUT = 90000; // 90 seconds
    public static final long USER_DETAILS_GET_TIME_OUT = 5 * 60 * 1000; // 3 minutes

    private HashSet<Object> uniqueBucket;

    public static boolean isConnected;
    public static final String ROOM_STRING = "room";
    String ip = Constants.SOCKET_URL_;
    private static final String TAG = SocketManager.class.getSimpleName();
    private String provider_id = "";
    private Context context;

    private String sProviderID = "";

    /**
     * SocketCallBack interface (SuccessListener)
     */
    public interface SocketCallBack {
        void onSuccessListener(String eventName, Object... response);
    }

   /* public SocketManager(Activity activity, SocketCallBack callBack) {
        this.activity = activity;
        this.callBack = callBack;
        this.uniqueBucket = new HashSet<Object>();
        System.out.println("provider_idsession-----------" + provider_id);
    }*/


    /**
     * Socket initialization
     *
     * @param activity current activity
     * @param callBack socket callback connection manager
     */
    public SocketManager(Context activity, SocketCallBack callBack) {
        this.callBack = callBack;
        this.context = activity;

        getSocketIp();
    }

    /**
     * get Socket Ip address for connection
     */
    private void getSocketIp() {
        try {
            if (mSocket != null) {
                mSocket.close();
                mSocket.off();
                mSocket = null;
            }
            if (Constants.IS_ENCRYPTION_ENABLED) {

                mSocket = IO.socket(ip + "?" + "referer=" + ip);

            } else {

                mSocket = IO.socket(ip);

            }
            mSocket.io().reconnection(true);
//            Toast.makeText(context, "New Socket Connection Created ", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "getSocketIp: ", e);
        }
    }


    public void setTaskId(String providerID) {
        sProviderID = providerID;
        System.out.println("sTaskID partner session--------------" + sProviderID);
    }

    private Socket mSocket;


    /**
     * Socket connection
     */
    public void connect() {
        try {


            Log.d("SOCKET MANAGER", "connecting to socket");
            if (!mSocket.connected()) {
                mSocket.off();
                mSocket.on(Socket.EVENT_CONNECT, onConnectMessage);
                mSocket.on(Socket.EVENT_DISCONNECT, onDisconnectMessage);
                mSocket.connect();
                System.out.println("===here connected in socket");
            } else {
                addAllListener();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Check socket connect or not
     *
     * @return response value
     */
    public boolean isConnected() {
        return mSocket.connected();
    }

    /**
     * The purpose of this method is to receive the call back when the server get connected
     */
    private Emitter.Listener privacysettings = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "privacysettings " + args[0]);
            invokeCallBack(EVENT_PRIVACY_SETTINGS, args);
        }
    };
    private Emitter.Listener rating = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "rating " + args[0]);
            invokeCallBack(EVENT_RATING, args);
        }
    };
    private Emitter.Listener Mute_Unmute = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "muteunmute " + args[0]);
            invokeCallBack(EVENT_MUTE, args);
        }
    };
    private Emitter.Listener MarkRead_Unread = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "markreadunread " + args[0]);
            invokeCallBack(EVENT_MARKREAD, args);
        }
    };
    private Emitter.Listener archive_unarchive = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "clearchat " + args[0]);
            invokeCallBack(EVENT_ARCHIVE_UNARCHIVE, args);
        }
    };
    private Emitter.Listener clearchat = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "clearchat " + args[0]);
            invokeCallBack(EVENT_CLEAR_CHAT, args);
        }
    };
    private Emitter.Listener ReplyMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "ReplyMessage " + args[0]);
            invokeCallBack(EVENT_REPLY_MESSAGE, args);
        }
    };
    private Emitter.Listener stopTypingListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "heartBeatListener " + args[0]);
            invokeCallBack(EVENT_STOP_TYPING, args);
        }
    };

    private Emitter.Listener heartBeatListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "heartBeatListener " + args[0]);
            invokeCallBack(EVENT_HEARTBEAT, args);
        }
    };
    private Emitter.Listener messageResponseListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageResponseListener" + args[0]);
            invokeCallBack(EVENT_MESSAGE_RES, args);
        }
    };
    private Emitter.Listener messageListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageListener" + args[0]);
            invokeCallBack(EVENT_MESSAGE, args);
        }
    };

    private Emitter.Listener statusListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageListener" + args[0]);
            invokeCallBack(EVENT_STATUS, args);
        }
    };

    private Emitter.Listener statusResponseListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageListener" + args[0]);
            invokeCallBack(EVENT_STATUS_RESPONSE, args);
        }
    };

    private Emitter.Listener statusUpdateListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageListener" + args[0]);
            invokeCallBack(EVENT_STATUS_UPDATE, args);
        }
    };


    private Emitter.Listener statusAckListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageListener" + args[0]);
            invokeCallBack(EVENT_STATUS_ACK, args);
        }
    };

    private Emitter.Listener statusOfflineListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageListener" + args[0]);
            invokeCallBack(EVENT_STATUS_OFFLINE, args);
        }
    };

    private Emitter.Listener statusRemoveListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageListener" + args[0]);
            invokeCallBack(EVENT_STATUS_DELETE, args);
        }
    };

    private Emitter.Listener statusMuteListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageListener" + args[0]);
            invokeCallBack(EVENT_STATUS_MUTE, args);
        }
    };

    private Emitter.Listener statusFetchAllListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageListener" + args[0]);
            invokeCallBack(EVENT_STATUS_FETCH_ALL, args);
        }
    };

    private Emitter.Listener statusPrivacyListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageListener" + args[0]);
            invokeCallBack(EVENT_STATUS_PRIVACY, args);
        }
    };

    private Emitter.Listener groupListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "groupListener" + args[0]);
            invokeCallBack(EVENT_GROUP, args);
        }
    };
    private Emitter.Listener getMessageListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "getMessageListener" + args[0]);
            invokeCallBack(EVENT_GET_MESSAGE, args);
        }
    };
    private Emitter.Listener messageStatusListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageStatusListener" + args[0]);
            invokeCallBack(EVENT_MESSAGE_STATUS_UPDATE, args);
        }
    };
    private Emitter.Listener messageAckListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "messageAckListener" + args[0]);
            invokeCallBack(EVENT_MESSAGE_ACK, args);
        }
    };

    private Emitter.Listener getCurrentTimeStatusListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "getCurrentTimeStatusListener" + args[0]);
            invokeCallBack(EVENT_GET_CURRENT_TIME_STATUS, args);
        }
    };
    private Emitter.Listener changeOnlineStatusListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            //      Log.d("SOCKET MANAGER", "changeOnlineStatusListener" + args[0]);
            invokeCallBack(EVENT_CAHNGE_ONLINE_STATUS, args);
        }
    };
    private Emitter.Listener updateOnlineStatusListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "updateOnlineStatusListener" + args[0]);
            invokeCallBack(EVENT_UPDATE_ONLINE_STATUS, args);
        }
    };
    private Emitter.Listener changeStateListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "changeStateListener" + args[0]);
            invokeCallBack(EVENT_CHANGE_ST, args);
        }
    };
    private Emitter.Listener typingListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "typingListener" + args[0]);
            invokeCallBack(EVENT_TYPING, args);
        }
    };
    private Emitter.Listener recordingListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "recordingListener" + args[0]);
            invokeCallBack(EVENT_RECORDING, args);
        }
    };
    private Emitter.Listener stopRecordingListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "stopRecordingListener" + args[0]);
            invokeCallBack(EVENT_STOP_RECORDING, args);
        }
    };

    private Emitter.Listener broadCastProfileListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "broadCastProfileListener" + args[0]);
            invokeCallBack(EVENT_BROADCAST_PROFILE, args);
        }
    };
    private Emitter.Listener addUserListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "broadCastProfileListener" + args[0]);
            invokeCallBack(EVENT_CREATE_USER, args);
        }
    };
    private Emitter.Listener userCreatedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "User Created" + args[0]);
            invokeCallBack(EVENT_USER_CREATED, args);
        }
    };


    private Emitter.Listener createRoomListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "createRoomListener" + args[0]);
            invokeCallBack(EVENT_CREATE_ROOM, args);
        }
    };
    private Emitter.Listener userJoinedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "userJoinedListener" + args[0]);
            invokeCallBack(EVENT_USER_JOINED, args);
        }
    };
    private Emitter.Listener addContact = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "addContact" + args[0]);
            invokeCallBack(EVENT_ADD_CONTACT, args);
        }
    };

    private Emitter.Listener getFavouriteListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "getFavouriteListener" + args[0]);
            invokeCallBack(EVENT_GET_FAVORITE, args);
        }
    };

    private Emitter.Listener getContacts = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "getContacts" + args[0]);
            invokeCallBack(EVENT_GET_CONTACTS, args);
        }
    };

    private Emitter.Listener qrDataListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "QR_Data_Listener " + args[0]);
            invokeCallBack(EVENT_QR_DATA, args);
        }
    };
    private Emitter.Listener imageUploadListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Upload Image Listener " + args[0]);
            invokeCallBack(EVENT_IMAGE_UPLOAD, args);
        }
    };
    private Emitter.Listener groupDetailsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Group Details Listener " + args[0]);
            invokeCallBack(EVENT_GROUP_DETAILS, args);
        }
    };
    private Emitter.Listener removeUserListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Group Details Listener " + args[0]);
            invokeCallBack(EVENT_REMOVE_USER, args);
        }
    };

    private Emitter.Listener deleteAccountListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Delete account Listener " + args[0]);
            invokeCallBack(EVENT_DELETE_ACCOUNT, args);
        }
    };

    private Emitter.Listener removeUserAccountListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Delete account Listener " + args[0]);
            invokeCallBack(EVENT_REMOVE_USER_ACCOUNT, args);
        }
    };


    private Emitter.Listener Logoutlistener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "logout " + args[0]);
            invokeCallBack(EVENT_MOBILE_TO_WEB_LOGOUT, args);
        }
    };

    private Emitter.Listener LogoutOldUserListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "logout " + args[0]);
            invokeCallBack(EVENT_LOGOUT_OLD_USER, args);
        }
    };


    private Emitter.Listener BlockusermessageListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "blockuser " + args[0]);
            invokeCallBack(EVENT_BLOCK_USER, args);
        }
    };

    private Emitter.Listener starredMessageListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "starNextLine" + args[0]);
            invokeCallBack(EVENT_STAR_MESSAGE, args);
        }
    };

    private Emitter.Listener removeMsgListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Remove Msg" + args[0]);
            invokeCallBack(EVENT_REMOVE_MESSAGE, args);
        }
    };

    private Emitter.Listener deleteChatListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Delete Msg" + args[0]);
            invokeCallBack(EVENT_DELETE_CHAT, args);
        }
    };

    private Emitter.Listener getGroupListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Group List" + args[0]);
            invokeCallBack(EVENT_GET_GROUP_LIST, args);
        }
    };

    private Emitter.Listener changeNameListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Change Name" + args[0]);
            invokeCallBack(EVENT_CHANGE_USER_NAME, args);
        }
    };

    private Emitter.Listener fileUploadListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "file upload" + args[0]);
            invokeCallBack(EVENT_FILE_UPLOAD, args);
        }
    };

    private Emitter.Listener fileReceivedListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "file received" + args[0]);
            invokeCallBack(EVENT_FILE_RECEIVED, args);
        }
    };

    private Emitter.Listener fileDownloadListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "file download" + args[0]);
            invokeCallBack(EVENT_FILE_DOWNLOAD, args);
        }
    };

    private Emitter.Listener fileDownloadStartListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "file download start" + args[0]);
            invokeCallBack(EVENT_START_FILE_DOWNLOAD, args);
        }
    };

    private Emitter.Listener getMobileSettingsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Get Mobile settings" + args[0]);
            invokeCallBack(EVENT_GET_MOBILE_SETTINGS, args);
        }
    };

    private Emitter.Listener getUserDetailsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Get User details" + args[0]);
            invokeCallBack(EVENT_GET_USER_DETAILS, args);
        }
    };

    private Emitter.Listener getMobileLoginNotifyListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Get Mobile Login notification details" + args[0]);
            invokeCallBack(EVENT_UPDATE_MOBILE_LOGIN_NOTIFICATION, args);
        }
    };

    private Emitter.Listener removeMobileLoginNotifyListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Remove Mobile Login notification details" + args[0]);
            invokeCallBack(EVENT_REMOVE_MOBILE_LOGIN_NOTIFICATION, args);
        }
    };

    private Emitter.Listener checkMobileLoginKeyListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Check Mobile Login details" + args[0]);
            invokeCallBack(EVENT_CHECK_MOBILE_LOGIN_KEY, args);
        }
    };

    private Emitter.Listener phoneDownloadListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Phone file download" + args[0]);
            invokeCallBack(EVENT_PHONE_DOWNLOAD, args);
        }
    };

    private Emitter.Listener phoneDataListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Phone file data received" + args[0]);
            invokeCallBack(EVENT_PHONE_DATA_RECEIVED, args);
        }
    };

    private Emitter.Listener ReportSpamUserListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "reportspam" + args[0]);
            invokeCallBack(EVENT_REPORT_SPAM_USER, args);
        }
    };

    private Emitter.Listener viewStatusListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "change message viewed state" + args[0]);
            invokeCallBack(EVENT_VIEW_CHAT, args);
        }
    };

    private Emitter.Listener toConvSetting = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "to conversation settings" + args[0]);
            invokeCallBack(EVENT_TO_CONV_SETTING, args);
        }
    };


    private Emitter.Listener newFileMsgListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "New File Messgae" + args[0]);
            invokeCallBack(EVENT_NEW_FILE_MESSAGE, args);
        }
    };

    private Emitter.Listener profileStatusListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Change status" + args[0]);
            invokeCallBack(EVENT_CHANGE_PROFILE_STATUS, args);
        }
    };

    private Emitter.Listener msgInfoListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Message info" + args[0]);
            invokeCallBack(EVENT_GET_MESSAGE_INFO, args);
        }
    };

    private Emitter.Listener getServerTimeListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Server Time info" + args[0]);
            invokeCallBack(EVENT_GET_SERVER_TIME, args);
        }
    };

    private Emitter.Listener changeSecretTimerListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Secret Timer info" + args[0]);
            invokeCallBack(EVENT_CHANGE_SECRET_MSG_TIMER, args);
        }
    };

    private Emitter.Listener changeEmailListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "change Email" + args[0]);
            invokeCallBack(EVENT_CHANGE_EMAIL, args);
        }
    };


    private Emitter.Listener changeRecoveryEmail = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Recover Email" + args[0]);
            invokeCallBack(EVENT_RECOVERY_EMAIL, args);
        }
    };

    private Emitter.Listener changeRecoveryPhone = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Recover Phone" + args[0]);
            invokeCallBack(EVENT_RECOVERY_PHONE, args);
        }
    };

    private Emitter.Listener chatLock = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "ChatLock" + args[0]);
            invokeCallBack(EVENT_CHAT_LOCK, args);
        }
    };

    private Emitter.Listener chatLockFromWeb = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "ChatLock web" + args[0]);
            invokeCallBack(EVENT_CHAT_LOCK_FROM_WEB, args);
        }
    };

    private Emitter.Listener chatLockEmailverify = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "ChatLockVerifyemail" + args[0]);
            invokeCallBack(EVENT_VERIFY_EMAIL, args);
        }
    };
    private Emitter.Listener updateGDSettingsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Google drive settings" + args[0]);
            invokeCallBack(EVENT_UPDATE_GOOGLE_DRIVE_SETTINGS, args);
        }
    };

    private Emitter.Listener getGDSettingsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "get google drive settings" + args[0]);
            invokeCallBack(EVENT_GET_GOOGLE_DRIVE_SETTINGS, args);
        }
    };

    private Emitter.Listener getAppSettingsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "get app settings" + args[0]);
            invokeCallBack(EVENT_GET_APP_SETTINGS, args);
        }
    };

    private Emitter.Listener getUserStatusListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "get app settings" + args[0]);
            invokeCallBack(EVENT_GET_USER_STATUS, args);
        }
    };


    private Emitter.Listener getFileUploadListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "get_file uploaded size" + args[0]);
            invokeCallBack(EVENT_GET_UPLOADED_FILE_SIZE, args);
        }
    };

    private Emitter.Listener getSettingsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "get settings" + args[0]);
            invokeCallBack(EVENT_GET_SETTINGS, args);
        }
    };

    private Emitter.Listener getConvIdListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "get conv id" + args[0]);
            invokeCallBack(EVENT_GET_CONV_ID, args);
        }
    };

    private Emitter.Listener getAdminSettingsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "get admin settings" + args[0]);
            invokeCallBack(EVENT_GET_ADMIN_SETTINGS, args);
        }
    };

    private Emitter.Listener callListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "audio video call" + args[0]);
            invokeCallBack(EVENT_CALL, args);
        }
    };

    private Emitter.Listener adminApprovalListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "admin approval call" + args[0]);
            invokeCallBack(EVENT_ADMIN_APPROVED, args);
        }
    };

    private Emitter.Listener leaveGroupListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "admin approval call" + args[0]);
            invokeCallBack(EVENT_LEAVE_GROUP, args);
        }
    };


    private Emitter.Listener adminResetPinListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "adminResetPinListener" + args[0]);
            invokeCallBack(EVENT_ADMIN_RESET_PIN, args);
        }
    };

    private Emitter.Listener allowPayloadInstallListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "allowPayloadInstallListener" + args[0]);
            invokeCallBack(EVENT_ALLOW_PAYLOAD_INSTALL, args);
        }
    };


    private Emitter.Listener callResponseListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "call_response" + args[0]);
            invokeCallBack(EVENT_CALL_RESPONSE, args);
        }
    };

    private Emitter.Listener callAckListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "call_ack" + args[0]);
            invokeCallBack(EVENT_CALL_ACK, args);
        }
    };

    private Emitter.Listener callStatusListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "call_status" + args[0]);
            invokeCallBack(EVENT_CALL_STATUS, args);
        }
    };

    private Emitter.Listener callStatusResListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "call_status_response" + args[0]);
            invokeCallBack(EVENT_CALL_STATUS_RESONSE, args);
        }
    };

    private Emitter.Listener offCallsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Off calls" + args[0]);
            invokeCallBack(EVENT_GET_OFFLINE_CALLS, args);
        }
    };

    private Emitter.Listener removeCallsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Remove calls" + args[0]);
            invokeCallBack(EVENT_REMOVE_CALLS, args);
        }
    };

    private Emitter.Listener removeAllCallsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET MANAGER", "Remove all calls" + args[0]);
            invokeCallBack(EVENT_REMOVE_ALL_CALLS, args);
        }
    };

    private Emitter.Listener retryCallConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_RETRY_CALL_CONNECT, args);
        }
    };

    private Emitter.Listener disconnectCallListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_DISCONNECT_CALL, args);
        }
    };

    private Emitter.Listener skipBackupListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_SKIP_BACKUP_MESSAGES, args);
        }
    };

    private Emitter.Listener convSettingsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_CONV_SETTINGS, args);
        }
    };

    private Emitter.Listener getMessageDetailsListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_GET_MESSAGE_DETAILS, args);
        }
    };

    private Emitter.Listener removeAccountAdminListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_REMOVED_ACCOUNT_BY_ADMIN, args);
        }
    };

    private Emitter.Listener onConnectMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "Connected");
            isConnected = true;
            removeAllListener();
            addAllListener();
            invokeCallBack(Socket.EVENT_CONNECT, args);
        }
    };

    private Emitter.Listener onDisconnectMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d("SOCKET LETSCHAT MANAGER", "DISCONNECTED");
            isConnected = false;
            invokeCallBack(Socket.EVENT_DISCONNECT, args);
        }
    };

    private Emitter.Listener socketWebrtcMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_CALL_WEBRTC_MESSAGE, args);
        }
    };


    //--------------------Delete Chat-----------------------

    private Emitter.Listener getDeleteMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_DELETE_MESSAGE, args);
        }
    };

    private Emitter.Listener getSingleACK = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_SINGLE_ACK, args);
        }
    };

    private Emitter.Listener getSingleOffline = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_SINGLE_OFFLINE_MSG, args);
        }
    };

    //--------------------Celebrity Module-------------------

    private Emitter.Listener getFollowList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_FOLLOW_LIST, args);
        }
    };

    private Emitter.Listener getSuggestionList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_SUGGESTION_LIST, args);
        }
    };

    private Emitter.Listener getFollowersList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_FOLLOWERS_LIST, args);
        }
    };

    private Emitter.Listener getFollowStatus = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_FOLLOW_STATUS, args);
        }
    };

    private Emitter.Listener getCelebrityProfile = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_CELEBRITY_PROFILE, args);
        }
    };

    private Emitter.Listener getCreatePost = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_CREATE_POST, args);
        }
    };

    private Emitter.Listener getReceivedPost = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_NEW_POST, args);
        }
    };

    private Emitter.Listener getMyPostFeed = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_MY_POST_FEED, args);
        }
    };

    private Emitter.Listener getFeedsTimeLine = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_FEEDS_TIMELINE, args);
        }
    };
    private Emitter.Listener celebrityLikeListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_LIKE, args);
        }
    };

    private Emitter.Listener getFollowingIds = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_FOLLOWING_IDS, args);
        }
    };

    //-------------------------------------Get Comments----------------------------


    private Emitter.Listener getComments = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_COMMENTS, args);
        }
    };

    private Emitter.Listener getcommentlist = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_GET_COMMENTS, args);
        }
    };

    private Emitter.Listener editcommentlistener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_EDIT_COMMENTS, args);
        }
    };

    private Emitter.Listener deletecommentlistener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            invokeCallBack(EVENT_DELETE_COMMENT, args);
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            invokeCallBack(EVENT_NEW_MESSAGE, args);
        }
    };

    private Emitter.Listener onDeleteAllMessages = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            invokeCallBack(EVENT_DELETE_ALL_READ_MESSAGES, args);
        }
    };

    /**
     * Disconnect the socket function
     */
    public void disconnect() {
        try {
            removeAllListener();
            mSocket.off(Socket.EVENT_CONNECT, onConnectMessage);
            mSocket.off(Socket.EVENT_DISCONNECT, onDisconnectMessage);
            mSocket.disconnect();
        } catch (Exception e) {
        }
    }

    /**
     * Add all socket listener
     */
    private void addAllListener() {
        mSocket.on(EVENT_DELETE_ALL_READ_MESSAGES, onDeleteAllMessages);

        mSocket.on(EVENT_NEW_MESSAGE, onNewMessage);
        mSocket.on(EVENT_MESSAGE, messageListener);
        mSocket.on(EVENT_STATUS, statusListener);
        mSocket.on(EVENT_STATUS_RESPONSE, statusResponseListener);
        mSocket.on(EVENT_STATUS_UPDATE, statusUpdateListener);
        mSocket.on(EVENT_STATUS_ACK, statusAckListener);
        mSocket.on(EVENT_STATUS_OFFLINE, statusOfflineListener);
        mSocket.on(EVENT_STATUS_DELETE, statusRemoveListener);
        mSocket.on(EVENT_STATUS_MUTE, statusMuteListener);
        mSocket.on(EVENT_STATUS_FETCH_ALL, statusFetchAllListener);
        mSocket.on(EVENT_STATUS_PRIVACY, statusPrivacyListener);

        mSocket.on(EVENT_RATING, rating);
        mSocket.on(EVENT_CLEAR_CHAT, clearchat);
        mSocket.on(EVENT_MUTE, Mute_Unmute);
        mSocket.on(EVENT_MARKREAD, MarkRead_Unread);
        mSocket.on(EVENT_ARCHIVE_UNARCHIVE, archive_unarchive);
        mSocket.on(EVENT_CREATE_USER, addUserListener);
        mSocket.on(EVENT_USER_CREATED, userCreatedListener);
        mSocket.on(EVENT_REPLY_MESSAGE, ReplyMessage);
        mSocket.on(EVENT_PRIVACY_SETTINGS, privacysettings);
        mSocket.on(EVENT_USER_JOINED, userJoinedListener);
        mSocket.on(EVENT_HEARTBEAT, heartBeatListener);
        mSocket.on(EVENT_MESSAGE_RES, messageResponseListener);
        mSocket.on(EVENT_GROUP, groupListener);
        mSocket.on(EVENT_GET_MESSAGE, getMessageListener);
        mSocket.on(EVENT_MESSAGE_STATUS_UPDATE, messageStatusListener);
        mSocket.on(EVENT_MESSAGE_ACK, messageAckListener);
        mSocket.on(EVENT_GET_CURRENT_TIME_STATUS, getCurrentTimeStatusListener);
        mSocket.on(EVENT_CAHNGE_ONLINE_STATUS, changeOnlineStatusListener);
        mSocket.on(EVENT_UPDATE_ONLINE_STATUS, updateOnlineStatusListener);
        mSocket.on(EVENT_CHANGE_ST, changeStateListener);
        mSocket.on(EVENT_TYPING, typingListener);
        mSocket.on(EVENT_RECORDING, recordingListener);
        mSocket.on(EVENT_STOP_RECORDING, stopRecordingListener);
        mSocket.on(EVENT_BROADCAST_PROFILE, broadCastProfileListener);
        mSocket.on(EVENT_STOP_TYPING, stopTypingListener);
        mSocket.on(EVENT_CREATE_ROOM, createRoomListener);
        mSocket.on(EVENT_ADD_CONTACT, addContact);
        mSocket.on(EVENT_GET_FAVORITE, getFavouriteListener);
        mSocket.on(EVENT_GET_CONTACTS, getContacts);
        mSocket.on(EVENT_QR_DATA, qrDataListener);
        mSocket.on(EVENT_IMAGE_UPLOAD, imageUploadListener);
        mSocket.on(EVENT_GROUP_DETAILS, groupDetailsListener);
        mSocket.on(EVENT_REMOVE_USER, removeUserListener);
        mSocket.on(EVENT_DELETE_ACCOUNT, deleteAccountListener);
        mSocket.on(EVENT_REMOVE_USER_ACCOUNT, removeUserAccountListener);
        mSocket.on(EVENT_MOBILE_TO_WEB_LOGOUT, Logoutlistener);
        mSocket.on(EVENT_LOGOUT_OLD_USER, LogoutOldUserListener);
        mSocket.on(EVENT_BLOCK_USER, BlockusermessageListener);
        mSocket.on(EVENT_STAR_MESSAGE, starredMessageListener);
        mSocket.on(EVENT_REMOVE_MESSAGE, removeMsgListener);
        mSocket.on(EVENT_DELETE_CHAT, deleteChatListener);
        mSocket.on(EVENT_GET_GROUP_LIST, getGroupListener);
        mSocket.on(EVENT_REPORT_SPAM_USER, ReportSpamUserListener);
        mSocket.on(EVENT_CHANGE_USER_NAME, changeNameListener);
        mSocket.on(EVENT_FILE_UPLOAD, fileUploadListener);
        mSocket.on(EVENT_FILE_RECEIVED, fileReceivedListener);
        mSocket.on(EVENT_FILE_DOWNLOAD, fileDownloadListener);
        mSocket.on(EVENT_START_FILE_DOWNLOAD, fileDownloadStartListener);
        mSocket.on(EVENT_GET_MOBILE_SETTINGS, getMobileSettingsListener);
        mSocket.on(EVENT_GET_USER_DETAILS, getUserDetailsListener);
        mSocket.on(EVENT_UPDATE_MOBILE_LOGIN_NOTIFICATION, getMobileLoginNotifyListener);
        mSocket.on(EVENT_REMOVE_MOBILE_LOGIN_NOTIFICATION, removeMobileLoginNotifyListener);
        mSocket.on(EVENT_CHECK_MOBILE_LOGIN_KEY, checkMobileLoginKeyListener);
        mSocket.on(EVENT_PHONE_DOWNLOAD, phoneDownloadListener);
        mSocket.on(EVENT_PHONE_DATA_RECEIVED, phoneDataListener);
        mSocket.on(EVENT_VIEW_CHAT, viewStatusListener);
        mSocket.on(EVENT_NEW_FILE_MESSAGE, newFileMsgListener);
        mSocket.on(EVENT_CHANGE_PROFILE_STATUS, profileStatusListener);
        mSocket.on(EVENT_GET_MESSAGE_INFO, msgInfoListener);
        mSocket.on(EVENT_GET_SERVER_TIME, getServerTimeListener);
        mSocket.on(EVENT_TO_CONV_SETTING, toConvSetting);
        mSocket.on(EVENT_CHANGE_EMAIL, changeEmailListener);
        mSocket.on(EVENT_RECOVERY_EMAIL, changeRecoveryEmail);
        mSocket.on(EVENT_RECOVERY_PHONE, changeRecoveryPhone);
        mSocket.on(EVENT_CHAT_LOCK, chatLock);
        mSocket.on(EVENT_CHAT_LOCK_FROM_WEB, chatLockFromWeb);
        mSocket.on(EVENT_VERIFY_EMAIL, chatLockEmailverify);
        mSocket.on(EVENT_CHANGE_SECRET_MSG_TIMER, changeSecretTimerListener);
        mSocket.on(EVENT_UPDATE_GOOGLE_DRIVE_SETTINGS, updateGDSettingsListener);
        mSocket.on(EVENT_GET_GOOGLE_DRIVE_SETTINGS, getGDSettingsListener);
        mSocket.on(EVENT_GET_APP_SETTINGS, getAppSettingsListener);
        mSocket.on(EVENT_GET_USER_STATUS, getUserStatusListener);
        mSocket.on(EVENT_GET_UPLOADED_FILE_SIZE, getFileUploadListener);
        mSocket.on(EVENT_GET_SETTINGS, getSettingsListener);
        mSocket.on(EVENT_GET_CONV_ID, getConvIdListener);
        mSocket.on(EVENT_GET_ADMIN_SETTINGS, getAdminSettingsListener);
        mSocket.on(EVENT_CALL, callListener);
        mSocket.on(EVENT_ADMIN_APPROVED, adminApprovalListener);
        mSocket.on(EVENT_LEAVE_GROUP, leaveGroupListener);
        mSocket.on(EVENT_ADMIN_RESET_PIN, adminResetPinListener);
        mSocket.on(EVENT_ALLOW_PAYLOAD_INSTALL, allowPayloadInstallListener);
        mSocket.on(EVENT_CALL_RESPONSE, callResponseListener);
        mSocket.on(EVENT_CALL_ACK, callAckListener);
        mSocket.on(EVENT_CALL_STATUS, callStatusListener);
        mSocket.on(EVENT_CALL_STATUS_RESONSE, callStatusResListener);
        mSocket.on(EVENT_CALL_WEBRTC_MESSAGE, socketWebrtcMessage);
        mSocket.on(EVENT_GET_OFFLINE_CALLS, offCallsListener);
        mSocket.on(EVENT_REMOVE_CALLS, removeCallsListener);
        mSocket.on(EVENT_REMOVE_ALL_CALLS, removeAllCallsListener);
        mSocket.on(EVENT_RETRY_CALL_CONNECT, retryCallConnectListener);
        mSocket.on(EVENT_DISCONNECT_CALL, disconnectCallListener);
        mSocket.on(EVENT_SKIP_BACKUP_MESSAGES, skipBackupListener);
        mSocket.on(EVENT_CONV_SETTINGS, convSettingsListener);
        mSocket.on(EVENT_GET_MESSAGE_DETAILS, getMessageDetailsListener);
        mSocket.on(EVENT_REMOVED_ACCOUNT_BY_ADMIN, removeAccountAdminListener);

        //--------Delete Chat-----------------------------------------
        mSocket.on(EVENT_DELETE_MESSAGE, getDeleteMessage);
        mSocket.on(EVENT_SINGLE_ACK, getSingleACK);
        mSocket.on(EVENT_SINGLE_OFFLINE_MSG, getSingleOffline);

        //--------Celebrity-------------
        mSocket.on(EVENT_FOLLOW_LIST, getFollowList);
        mSocket.on(EVENT_SUGGESTION_LIST, getSuggestionList);
        mSocket.on(EVENT_FOLLOWERS_LIST, getFollowersList);
        mSocket.on(EVENT_FOLLOW_STATUS, getFollowStatus);
        mSocket.on(EVENT_CELEBRITY_PROFILE, getCelebrityProfile);
        mSocket.on(EVENT_CREATE_POST, getCreatePost);
        mSocket.on(EVENT_NEW_POST, getReceivedPost);
        mSocket.on(EVENT_MY_POST_FEED, getMyPostFeed);
        mSocket.on(EVENT_FEEDS_TIMELINE, getFeedsTimeLine);
        mSocket.on(EVENT_LIKE, celebrityLikeListener);
        mSocket.on(EVENT_FOLLOWING_IDS, getFollowingIds);
        //------------------Comments---------------------
        mSocket.on(EVENT_COMMENTS, getComments);
        mSocket.on(EVENT_GET_COMMENTS, getcommentlist);
        mSocket.on(EVENT_EDIT_COMMENTS, editcommentlistener);
        mSocket.on(EVENT_DELETE_COMMENT, deletecommentlistener);
    }

    /**
     * Remove all socket listener
     */
    private void removeAllListener() {
        mSocket.off(EVENT_DELETE_ALL_READ_MESSAGES, onDeleteAllMessages);
        mSocket.off(EVENT_CREATE_USER, addUserListener);
        mSocket.off(EVENT_USER_CREATED, userCreatedListener);
        mSocket.off(EVENT_RATING, rating);
        mSocket.off(EVENT_USER_JOINED, userJoinedListener);
        mSocket.off(EVENT_PRIVACY_SETTINGS, privacysettings);
        mSocket.off(EVENT_CLEAR_CHAT, clearchat);
        mSocket.off(EVENT_MUTE, Mute_Unmute);
        mSocket.off(EVENT_MARKREAD, MarkRead_Unread);
        mSocket.off(EVENT_ARCHIVE_UNARCHIVE, archive_unarchive);
        mSocket.off(EVENT_NEW_MESSAGE, onNewMessage);
        mSocket.off(EVENT_HEARTBEAT, heartBeatListener);
        mSocket.off(EVENT_MESSAGE_RES, messageResponseListener);
        mSocket.off(EVENT_MESSAGE, messageListener);
        mSocket.off(EVENT_STATUS, statusListener);
        mSocket.off(EVENT_STATUS_RESPONSE, statusResponseListener);
        mSocket.off(EVENT_STATUS_UPDATE, statusUpdateListener);
        mSocket.off(EVENT_STATUS_ACK, statusAckListener);
        mSocket.off(EVENT_STATUS_OFFLINE, statusOfflineListener);
        mSocket.off(EVENT_STATUS_DELETE, statusRemoveListener);
        mSocket.off(EVENT_STATUS_MUTE, statusMuteListener);
        mSocket.off(EVENT_STATUS_FETCH_ALL, statusFetchAllListener);
        mSocket.off(EVENT_STATUS_PRIVACY, statusPrivacyListener);
        mSocket.off(EVENT_REPLY_MESSAGE, ReplyMessage);
        mSocket.off(EVENT_GROUP, groupListener);
        mSocket.off(EVENT_GET_MESSAGE, getMessageListener);
        mSocket.off(EVENT_MESSAGE_STATUS_UPDATE, messageStatusListener);
        mSocket.off(EVENT_MESSAGE_ACK, messageAckListener);
        mSocket.off(EVENT_GET_CURRENT_TIME_STATUS, getCurrentTimeStatusListener);
        mSocket.off(EVENT_CAHNGE_ONLINE_STATUS, changeOnlineStatusListener);
        mSocket.off(EVENT_UPDATE_ONLINE_STATUS, updateOnlineStatusListener);
        mSocket.off(EVENT_CHANGE_ST, changeStateListener);
        mSocket.off(EVENT_TYPING, typingListener);
        mSocket.off(EVENT_RECORDING, recordingListener);
        mSocket.off(EVENT_STOP_RECORDING, stopRecordingListener);
        mSocket.off(EVENT_BROADCAST_PROFILE, broadCastProfileListener);
        mSocket.off(EVENT_STOP_TYPING, stopTypingListener);
        mSocket.off(EVENT_CREATE_ROOM, createRoomListener);
        mSocket.off(EVENT_REPORT_SPAM_USER, ReportSpamUserListener);
        mSocket.off(EVENT_ADD_CONTACT, addContact);
        mSocket.off(EVENT_GET_FAVORITE, getFavouriteListener);
        mSocket.off(EVENT_GET_CONTACTS, getContacts);
        mSocket.off(EVENT_QR_DATA, qrDataListener);
        mSocket.off(EVENT_IMAGE_UPLOAD, imageUploadListener);
        mSocket.off(EVENT_GROUP_DETAILS, groupDetailsListener);
        mSocket.off(EVENT_REMOVE_USER, removeUserListener);
        mSocket.off(EVENT_DELETE_ACCOUNT, deleteAccountListener);
        mSocket.off(EVENT_REMOVE_USER_ACCOUNT, removeUserAccountListener);
        mSocket.off(EVENT_MOBILE_TO_WEB_LOGOUT, Logoutlistener);
        mSocket.off(EVENT_LOGOUT_OLD_USER, LogoutOldUserListener);
        mSocket.off(EVENT_STAR_MESSAGE, starredMessageListener);
        mSocket.off(EVENT_BLOCK_USER, BlockusermessageListener);
        mSocket.off(EVENT_REMOVE_MESSAGE, removeMsgListener);
        mSocket.off(EVENT_DELETE_CHAT, deleteChatListener);
        mSocket.off(EVENT_GET_GROUP_LIST, groupListener);
        mSocket.off(EVENT_CHANGE_USER_NAME, changeNameListener);
        mSocket.off(EVENT_FILE_UPLOAD, fileUploadListener);
        mSocket.off(EVENT_FILE_RECEIVED, fileReceivedListener);
        mSocket.off(EVENT_FILE_DOWNLOAD, fileDownloadListener);
        mSocket.off(EVENT_START_FILE_DOWNLOAD, fileDownloadStartListener);
        mSocket.off(EVENT_GET_MOBILE_SETTINGS, getMobileSettingsListener);
        mSocket.off(EVENT_GET_USER_DETAILS, getUserDetailsListener);
        mSocket.off(EVENT_UPDATE_MOBILE_LOGIN_NOTIFICATION, getMobileLoginNotifyListener);
        mSocket.off(EVENT_REMOVE_MOBILE_LOGIN_NOTIFICATION, removeMobileLoginNotifyListener);
        mSocket.off(EVENT_CHECK_MOBILE_LOGIN_KEY, checkMobileLoginKeyListener);
        mSocket.off(EVENT_PHONE_DOWNLOAD, phoneDownloadListener);
        mSocket.off(EVENT_PHONE_DATA_RECEIVED, phoneDataListener);
        mSocket.off(EVENT_VIEW_CHAT, viewStatusListener);
        mSocket.off(EVENT_NEW_FILE_MESSAGE, newFileMsgListener);
        mSocket.off(EVENT_CHANGE_PROFILE_STATUS, newFileMsgListener);
        mSocket.off(EVENT_GET_MESSAGE_INFO, msgInfoListener);
        mSocket.off(EVENT_GET_SERVER_TIME, getServerTimeListener);
        mSocket.off(EVENT_TO_CONV_SETTING, toConvSetting);
        mSocket.off(EVENT_CHANGE_EMAIL, changeEmailListener);
        mSocket.off(EVENT_RECOVERY_EMAIL, changeRecoveryEmail);
        mSocket.off(EVENT_RECOVERY_PHONE, changeRecoveryPhone);
        mSocket.off(EVENT_CHAT_LOCK, chatLock);
        mSocket.off(EVENT_CHAT_LOCK_FROM_WEB, chatLockFromWeb);
        mSocket.off(EVENT_VERIFY_EMAIL, chatLockEmailverify);
        mSocket.off(EVENT_CHANGE_SECRET_MSG_TIMER, changeSecretTimerListener);
        mSocket.off(EVENT_UPDATE_GOOGLE_DRIVE_SETTINGS, updateGDSettingsListener);
        mSocket.off(EVENT_GET_GOOGLE_DRIVE_SETTINGS, getGDSettingsListener);
        mSocket.off(EVENT_GET_APP_SETTINGS, getAppSettingsListener);
        mSocket.off(EVENT_GET_USER_STATUS, getUserStatusListener);
        mSocket.off(EVENT_GET_UPLOADED_FILE_SIZE, getFileUploadListener);
        mSocket.off(EVENT_GET_SETTINGS, getSettingsListener);
        mSocket.off(EVENT_GET_CONV_ID, getConvIdListener);
        mSocket.off(EVENT_GET_ADMIN_SETTINGS, getAdminSettingsListener);
        mSocket.off(EVENT_CALL, callListener);
        mSocket.off(EVENT_ADMIN_APPROVED, adminApprovalListener);
        mSocket.off(EVENT_LEAVE_GROUP, leaveGroupListener);
        mSocket.off(EVENT_ADMIN_RESET_PIN, adminResetPinListener);
        mSocket.off(EVENT_ALLOW_PAYLOAD_INSTALL, allowPayloadInstallListener);
        mSocket.off(EVENT_CALL_RESPONSE, callResponseListener);
        mSocket.off(EVENT_CALL_ACK, callAckListener);
        mSocket.off(EVENT_CALL_STATUS, callStatusListener);
        mSocket.off(EVENT_CALL_STATUS_RESONSE, callStatusResListener);
        mSocket.off(EVENT_GET_OFFLINE_CALLS, offCallsListener);
        mSocket.off(EVENT_REMOVE_CALLS, removeCallsListener);
        mSocket.off(EVENT_REMOVE_ALL_CALLS, removeAllCallsListener);
        mSocket.off(EVENT_RETRY_CALL_CONNECT, retryCallConnectListener);
        mSocket.off(EVENT_DISCONNECT_CALL, disconnectCallListener);
        mSocket.off(EVENT_SKIP_BACKUP_MESSAGES, skipBackupListener);
        mSocket.off(EVENT_CONV_SETTINGS, convSettingsListener);
        mSocket.off(EVENT_GET_MESSAGE_DETAILS, getMessageDetailsListener);
        mSocket.off(EVENT_REMOVED_ACCOUNT_BY_ADMIN, removeAccountAdminListener);
        mSocket.off(EVENT_CALL_WEBRTC_MESSAGE, socketWebrtcMessage);


        //-----------Delete Chat-----------------
        mSocket.off(EVENT_DELETE_MESSAGE, getDeleteMessage);
        mSocket.off(EVENT_SINGLE_ACK, getSingleACK);
        mSocket.off(EVENT_SINGLE_OFFLINE_MSG, getSingleOffline);

        //--------------Celebrity----------------
        mSocket.off(EVENT_FOLLOW_LIST, getFollowList);
        mSocket.off(EVENT_SUGGESTION_LIST, getSuggestionList);
        mSocket.off(EVENT_FOLLOWERS_LIST, getFollowersList);
        mSocket.off(EVENT_FOLLOW_STATUS, getFollowStatus);
        mSocket.off(EVENT_CELEBRITY_PROFILE, getCelebrityProfile);
        mSocket.off(EVENT_CREATE_POST, getCreatePost);
        mSocket.off(EVENT_NEW_POST, getReceivedPost);
        mSocket.off(EVENT_MY_POST_FEED, getMyPostFeed);
        mSocket.off(EVENT_FEEDS_TIMELINE, getFeedsTimeLine);
        mSocket.off(EVENT_LIKE, celebrityLikeListener);
        mSocket.off(EVENT_FOLLOWING_IDS, getFollowingIds);

        //------------------Comments---------------------
        mSocket.off(EVENT_COMMENTS, getComments);
        mSocket.off(EVENT_GET_COMMENTS, getcommentlist);
        mSocket.off(EVENT_EDIT_COMMENTS, editcommentlistener);
        mSocket.off(EVENT_DELETE_COMMENT, deletecommentlistener);
    }

    /**
     * Each socket to call send method, to handling the securityToken and encryption
     *
     * @param message   input value(message)
     * @param eventName input value(eventName)
     */
    public void send(Object message, String eventName) {


        System.out.println("----------eventName-------------" + eventName);

        if (Constants.IS_ENCRYPTION_ENABLED && message != null && !eventName.equals(EVENT_CREATE_USER)) {
            try {
                Log.e(TAG, "invokeCallBack: event name" + eventName);
                String tokenHashKey = SessionManager.getInstance(context).getSecurityTokenHash();
                if (tokenHashKey != null)
                    //message = AESCrypt.encrypt(tokenHashKey, message.toString());
                    Log.d(TAG, "send: " + message);
            } catch (Exception e) {
                Log.e(TAG, "send: ", e);
            }
        }

        switch (eventName) {

            case EVENT_DELETE_ALL_READ_MESSAGES:
                mSocket.emit(EVENT_DELETE_ALL_READ_MESSAGES, message);
                break;

            case EVENT_NEW_MESSAGE:
                mSocket.emit(EVENT_NEW_MESSAGE, message);
                break;

            case EVENT_PRIVACY_SETTINGS:
                mSocket.emit(EVENT_PRIVACY_SETTINGS, message);
                break;

            case EVENT_ARCHIVE_UNARCHIVE:
                mSocket.emit(EVENT_ARCHIVE_UNARCHIVE, message);
                break;

            case EVENT_CLEAR_CHAT:
                mSocket.emit(EVENT_CLEAR_CHAT, message);
                break;
            case EVENT_MUTE:
                mSocket.emit(EVENT_MUTE, message);
                break;
            case EVENT_MARKREAD:
                mSocket.emit(EVENT_MARKREAD, message);
                break;

            case EVENT_HEARTBEAT:
                mSocket.emit(EVENT_HEARTBEAT, message);
                break;

            case EVENT_REPLY_MESSAGE:
                mSocket.emit(EVENT_REPLY_MESSAGE, message);
                break;

            case EVENT_MESSAGE_RES:
                mSocket.emit(EVENT_MESSAGE_RES, message);
                break;

            case EVENT_MESSAGE:
                mSocket.emit(EVENT_MESSAGE, message);
                break;

            case EVENT_STATUS:
                mSocket.emit(EVENT_STATUS, message);
                break;

            case EVENT_STATUS_RESPONSE:
                mSocket.emit(EVENT_STATUS_RESPONSE, message);
                break;

            case EVENT_STATUS_UPDATE:
                mSocket.emit(EVENT_STATUS_UPDATE, message);
                break;

            case EVENT_STATUS_ACK:
                mSocket.emit(EVENT_STATUS_ACK, message);
                break;

            case EVENT_STATUS_OFFLINE:
                mSocket.emit(EVENT_STATUS_OFFLINE, message);
                break;

            case EVENT_STATUS_DELETE:
                mSocket.emit(EVENT_STATUS_DELETE, message);
                break;

            case EVENT_STATUS_MUTE:
                mSocket.emit(EVENT_STATUS_MUTE, message);
                break;

            case EVENT_STATUS_FETCH_ALL:
                mSocket.emit(EVENT_STATUS_FETCH_ALL, message);
                break;


            case EVENT_STATUS_PRIVACY:
                mSocket.emit(EVENT_STATUS_PRIVACY, message);
                break;
            case EVENT_GROUP:
                mSocket.emit(EVENT_GROUP, message);
                break;

            case EVENT_GET_MESSAGE:
                mSocket.emit(EVENT_GET_MESSAGE, message);
                break;

            case EVENT_MESSAGE_STATUS_UPDATE:
                mSocket.emit(EVENT_MESSAGE_STATUS_UPDATE, message);
                break;

            case EVENT_MESSAGE_ACK:
                //Log.d("MessageAck", message.toString());
                mSocket.emit(EVENT_MESSAGE_ACK, message);
                break;

            case EVENT_GET_CURRENT_TIME_STATUS:
                mSocket.emit(EVENT_GET_CURRENT_TIME_STATUS, message);
                break;

            case EVENT_CAHNGE_ONLINE_STATUS:
                mSocket.emit(EVENT_CAHNGE_ONLINE_STATUS, message);
                break;

            case EVENT_UPDATE_ONLINE_STATUS:
                mSocket.emit(EVENT_UPDATE_ONLINE_STATUS, message);
                break;

            case EVENT_CHANGE_ST:
                mSocket.emit(EVENT_CHANGE_ST, message);
                break;

            case EVENT_TYPING:
                mSocket.emit(EVENT_TYPING, message);
                break;

            case EVENT_STOP_TYPING:
                mSocket.emit(EVENT_STOP_TYPING, message);
                break;

            case EVENT_RECORDING:
                mSocket.emit(EVENT_RECORDING, message);
                break;

            case EVENT_STOP_RECORDING:
                mSocket.emit(EVENT_STOP_RECORDING, message);
                break;

            case EVENT_BROADCAST_PROFILE:
                mSocket.emit(EVENT_BROADCAST_PROFILE, message);
                break;

            case EVENT_CREATE_USER:
                mSocket.emit(EVENT_CREATE_USER, message);
                break;

            case EVENT_USER_CREATED:
                mSocket.emit(EVENT_USER_CREATED, message);
                break;

            case EVENT_CREATE_ROOM:
                mSocket.emit(EVENT_CREATE_ROOM, message);
                break;

            case EVENT_ADD_CONTACT:
                mSocket.emit(EVENT_ADD_CONTACT, message);
                break;

            case EVENT_GET_FAVORITE:
                mSocket.emit(EVENT_GET_FAVORITE, message);
                break;

            case EVENT_GET_CONTACTS:
                mSocket.emit(EVENT_GET_CONTACTS, message);
                break;

            case EVENT_QR_DATA:
                mSocket.emit(EVENT_QR_DATA, message);
                break;

            case EVENT_IMAGE_UPLOAD:
                mSocket.emit(EVENT_IMAGE_UPLOAD, message);
                break;

            case EVENT_GROUP_DETAILS:
                mSocket.emit(EVENT_GROUP_DETAILS, message);
                break;

            case EVENT_REMOVE_USER:
                mSocket.emit(EVENT_REMOVE_USER, message);
                break;

            case EVENT_DELETE_ACCOUNT:
                mSocket.emit(EVENT_DELETE_ACCOUNT, message);
                break;
            case EVENT_REMOVE_USER_ACCOUNT:
                mSocket.emit(EVENT_REMOVE_USER_ACCOUNT, message);
                break;

            case EVENT_MOBILE_TO_WEB_LOGOUT:
                mSocket.emit(EVENT_MOBILE_TO_WEB_LOGOUT, message);
                break;

            case EVENT_STAR_MESSAGE:
                mSocket.emit(EVENT_STAR_MESSAGE, message);
                break;

            case EVENT_REMOVE_MESSAGE:
                mSocket.emit(EVENT_REMOVE_MESSAGE, message);
                break;

            case EVENT_DELETE_CHAT:
                mSocket.emit(EVENT_DELETE_CHAT, message);
                break;

            case EVENT_GET_GROUP_LIST:
                mSocket.emit(EVENT_GET_GROUP_LIST, message);
                break;

            case EVENT_CHANGE_USER_NAME:
                mSocket.emit(EVENT_CHANGE_USER_NAME, message);
                break;

            case EVENT_FILE_UPLOAD:
                mSocket.emit(EVENT_FILE_UPLOAD, message);
                break;

            case EVENT_FILE_RECEIVED:
                mSocket.emit(EVENT_FILE_RECEIVED, message);
                break;

            case EVENT_FILE_DOWNLOAD:
                mSocket.emit(EVENT_FILE_DOWNLOAD, message);
                break;

            case EVENT_START_FILE_DOWNLOAD:
                mSocket.emit(EVENT_START_FILE_DOWNLOAD, message);
                break;

            case EVENT_GET_MOBILE_SETTINGS:
                mSocket.emit(EVENT_GET_MOBILE_SETTINGS, message);
                break;

            case EVENT_GET_USER_DETAILS:
                mSocket.emit(EVENT_GET_USER_DETAILS, message);
                break;

            case EVENT_UPDATE_MOBILE_LOGIN_NOTIFICATION:
                mSocket.emit(EVENT_UPDATE_MOBILE_LOGIN_NOTIFICATION, message);
                break;

            case EVENT_REMOVE_MOBILE_LOGIN_NOTIFICATION:
                mSocket.emit(EVENT_REMOVE_MOBILE_LOGIN_NOTIFICATION, message);
                break;

            case EVENT_CHECK_MOBILE_LOGIN_KEY:
                mSocket.emit(EVENT_CHECK_MOBILE_LOGIN_KEY, message);
                break;

            case EVENT_BLOCK_USER:
                mSocket.emit(EVENT_BLOCK_USER, message);
                break;

            case EVENT_PHONE_DOWNLOAD:
                mSocket.emit(EVENT_PHONE_DOWNLOAD, message);
                break;

            case EVENT_PHONE_DATA_RECEIVED:
                mSocket.emit(EVENT_PHONE_DATA_RECEIVED, message);
                break;

            case EVENT_REPORT_SPAM_USER:
                mSocket.emit(EVENT_REPORT_SPAM_USER, message);
                break;

            case EVENT_VIEW_CHAT:
                mSocket.emit(EVENT_VIEW_CHAT, message);
                break;

            case EVENT_NEW_FILE_MESSAGE:
                mSocket.emit(EVENT_NEW_FILE_MESSAGE, message);
                break;

            case EVENT_CHANGE_PROFILE_STATUS:
                mSocket.emit(EVENT_CHANGE_PROFILE_STATUS, message);
                break;

            case EVENT_GET_MESSAGE_INFO:
                mSocket.emit(EVENT_GET_MESSAGE_INFO, message);
                break;

            case EVENT_GET_SERVER_TIME:
                mSocket.emit(EVENT_GET_SERVER_TIME, message);
                break;

            case EVENT_CHANGE_SECRET_MSG_TIMER:
                mSocket.emit(EVENT_CHANGE_SECRET_MSG_TIMER, message);
                break;

            case EVENT_TO_CONV_SETTING:
                mSocket.emit(EVENT_TO_CONV_SETTING, message);
                break;

            case EVENT_USER_JOINED:
                mSocket.emit(EVENT_USER_JOINED, message);
                break;

            case EVENT_CHANGE_EMAIL:
                mSocket.emit(EVENT_CHANGE_EMAIL, message);
                break;

            case EVENT_RECOVERY_EMAIL:
                mSocket.emit(EVENT_RECOVERY_EMAIL, message);
                break;

            case EVENT_RECOVERY_PHONE:
                mSocket.emit(EVENT_RECOVERY_PHONE, message);
                break;

            case EVENT_CHAT_LOCK:
                mSocket.emit(EVENT_CHAT_LOCK, message);
                break;

            case EVENT_CHAT_LOCK_FROM_WEB:
                mSocket.emit(EVENT_CHAT_LOCK_FROM_WEB, message);
                break;

            case EVENT_VERIFY_EMAIL:
                mSocket.emit(EVENT_VERIFY_EMAIL, message);
                break;

            case EVENT_UPDATE_GOOGLE_DRIVE_SETTINGS:
                mSocket.emit(EVENT_UPDATE_GOOGLE_DRIVE_SETTINGS, message);
                break;

            case EVENT_GET_GOOGLE_DRIVE_SETTINGS:
                mSocket.emit(EVENT_GET_GOOGLE_DRIVE_SETTINGS, message);
                break;

            case EVENT_RATING:
                mSocket.emit(EVENT_RATING, message);
                break;

            case EVENT_GET_APP_SETTINGS:
                mSocket.emit(EVENT_GET_APP_SETTINGS, message);
                break;
            case EVENT_GET_USER_STATUS:
                mSocket.emit(EVENT_GET_USER_STATUS, message);
                break;
            case EVENT_GET_UPLOADED_FILE_SIZE:
                mSocket.emit(EVENT_GET_UPLOADED_FILE_SIZE, message);
                break;

            case EVENT_GET_SETTINGS:
                mSocket.emit(EVENT_GET_SETTINGS, message);
                break;

            case EVENT_GET_CONV_ID:
                mSocket.emit(EVENT_GET_CONV_ID, message);
                break;

            case EVENT_CALL:
                mSocket.emit(EVENT_CALL, message);
                break;
            case EVENT_ADMIN_APPROVED:
                mSocket.emit(EVENT_ADMIN_APPROVED, message);
                break;
            case EVENT_LEAVE_GROUP:
                mSocket.emit(EVENT_LEAVE_GROUP, message);
                break;
            case EVENT_ADMIN_RESET_PIN:
                mSocket.emit(EVENT_ADMIN_RESET_PIN, message);
                break;
            case EVENT_ALLOW_PAYLOAD_INSTALL:
                mSocket.emit(EVENT_ALLOW_PAYLOAD_INSTALL, message);
                break;
            case EVENT_CALL_ACK:
                mSocket.emit(EVENT_CALL_ACK, message);
                break;

            case EVENT_CALL_STATUS:
                mSocket.emit(EVENT_CALL_STATUS, message);
                break;

            case EVENT_GET_OFFLINE_CALLS:
                mSocket.emit(EVENT_GET_OFFLINE_CALLS, message);
                break;

            case EVENT_REMOVE_CALLS:
                mSocket.emit(EVENT_REMOVE_CALLS, message);
                break;

            case EVENT_REMOVE_ALL_CALLS:
                mSocket.emit(EVENT_REMOVE_ALL_CALLS, message);
                break;

            case EVENT_RETRY_CALL_CONNECT:
                mSocket.emit(EVENT_RETRY_CALL_CONNECT, message);
                break;

            case EVENT_DISCONNECT_CALL:
                mSocket.emit(EVENT_DISCONNECT_CALL, message);
                break;

            case EVENT_SKIP_BACKUP_MESSAGES:
                mSocket.emit(EVENT_SKIP_BACKUP_MESSAGES, message);
                break;

            case EVENT_CONV_SETTINGS:
                mSocket.emit(EVENT_CONV_SETTINGS, message);
                break;

            case EVENT_GET_MESSAGE_DETAILS:
                mSocket.emit(EVENT_GET_MESSAGE_DETAILS, message);
                break;


            //------Delete Chat------------------------------------
            case EVENT_DELETE_MESSAGE:
                mSocket.emit(EVENT_DELETE_MESSAGE, message);
                break;

            case EVENT_SINGLE_ACK:
                mSocket.emit(EVENT_SINGLE_ACK, message);
                break;

            case EVENT_SINGLE_OFFLINE_MSG:
                mSocket.emit(EVENT_SINGLE_OFFLINE_MSG, message);
                break;


            //------------Cerlebrity--------------------
            case EVENT_FOLLOW_LIST:
                mSocket.emit(EVENT_FOLLOW_LIST, message);
                break;

            case EVENT_SUGGESTION_LIST:
                mSocket.emit(EVENT_SUGGESTION_LIST, message);
                break;

            case EVENT_FOLLOWERS_LIST:
                mSocket.emit(EVENT_FOLLOWERS_LIST, message);
                break;

            case EVENT_FOLLOW_STATUS:
                mSocket.emit(EVENT_FOLLOW_STATUS, message);
                break;

            case EVENT_CELEBRITY_PROFILE:
                mSocket.emit(EVENT_CELEBRITY_PROFILE, message);
                break;

            case EVENT_CREATE_POST:
                mSocket.emit(EVENT_CREATE_POST, message);
                break;

            case EVENT_NEW_POST:
                mSocket.emit(EVENT_NEW_POST, message);
                break;

            case EVENT_MY_POST_FEED:
                mSocket.emit(EVENT_MY_POST_FEED, message);
                break;

            case EVENT_FEEDS_TIMELINE:
                mSocket.emit(EVENT_FEEDS_TIMELINE, message);
                break;

            case EVENT_LIKE:
                mSocket.emit(EVENT_LIKE, message);
                break;

            case EVENT_FOLLOWING_IDS:
                mSocket.emit(EVENT_FOLLOWING_IDS, message);
                break;


            case EVENT_GET_COMMENTS:
                mSocket.emit(EVENT_GET_COMMENTS, message);
                break;

            case EVENT_EDIT_COMMENTS:
                mSocket.emit(EVENT_EDIT_COMMENTS, message);
                break;

            case EVENT_DELETE_COMMENT:
                mSocket.emit(EVENT_DELETE_COMMENT, message);
                break;

            case EVENT_CALL_WEBRTC_MESSAGE:
                mSocket.emit(EVENT_CALL_WEBRTC_MESSAGE, message);
                break;
        }
    }


    /**
     * To check the message are encryption & to call back function for onSuccessListener each socket
     *
     * @param eventName input value(eventName)
     * @param args      input value(args)
     */
    public void invokeCallBack(final String eventName, final Object... args) {
        if (Constants.IS_ENCRYPTION_ENABLED && args != null && !eventName.equals(EVENT_CREATE_USER) && !eventName.equals(EVENT_USER_CREATED)) {
            try {
                String response = args[0].toString();
//                String encryptionTokenHashKey = SessionManager.getInstance(context).getSecurityTokenHash();
//                String messageAfterDecrypt = AESCrypt.decrypt(encryptionTokenHashKey, response);
//                args[0]=messageAfterDecrypt;
            } catch (Exception e) {
                Log.e(TAG, "invokeCallBack: ", e);
            }
        }

        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (callBack != null) {
                        callBack.onSuccessListener(eventName, args);
                    }
                }
            });
        } else {
            if (callBack != null) {
                callBack.onSuccessListener(eventName, args);
            }
        }
    }

    public void createRoom(String userID) {
        if (TextUtils.isEmpty(userID)) {
            return;
        }
        mSocket.emit(ROOM_STRING, userID);
    }

    private final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
            return new java.security.cert.X509Certificate[]{};
        }

        public void checkClientTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain,
                                       String authType) throws CertificateException {
        }
    }};


}
