package com.chatapp.synchat.core.socket;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.ChatViewActivity;
import com.chatapp.synchat.app.HomeScreen;
import com.chatapp.synchat.app.PinEnterActivity;
import com.chatapp.synchat.app.ReLoadingActivityNew;
import com.chatapp.synchat.app.calls.CallAck;
import com.chatapp.synchat.app.calls.CallMessage;
import com.chatapp.synchat.app.calls.CallsActivity;
import com.chatapp.synchat.app.calls.IncomingCallActivity;
import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.app.utils.DateChangeBroadcastReceiver;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.app.utils.MyLog;
import com.chatapp.synchat.app.utils.StringCryptUtils;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.ShortcutBadgeManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.DatabaseClassForDB;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.ChangeSetController;
import com.chatapp.synchat.core.message.IncomingMessage;
import com.chatapp.synchat.core.message.MessageAck;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.message.MesssageObjectReceiver;
import com.chatapp.synchat.core.message.OfflineMessageHandler;
import com.chatapp.synchat.core.model.CallItemChat;
import com.chatapp.synchat.core.model.ChatLockPojo;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.model.GroupMembersPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.MuteStatusPojo;
import com.chatapp.synchat.core.model.OfflineRetryEventPojo;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.uploadtoserver.FileUploadDownloadManager;

import org.appspot.apprtc.util.CryptLib;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import io.socket.client.Socket;
import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;

//import com.chatapp.android.app.encryption.AESUtil;


/**
 *
 */
public class MessageService extends Service {

    public static final long MIN_GET_OFFLINE_MESSAGES_TIME = 60 * 1000; // 60 seconds
    public static final String BACK_UP_NEVER = "Never";
    public static final String BACK_UP_ONLY_I_TAP = "Only when I tap backup";
    public static final String BACK_UP_DAILY = "Daily";
    public static final String BACK_UP_WEEKLY = "Weekly";
    public static final String BACK_UP_MONTHLY = "Monthly";
    private static final String NOTIFICATION_CHANNEL_ID = "1";
    private static final String TAG = MessageService.class.getSimpleName();
    public static SocketManager manager;
    static public MessageService service;
    public static ArrayList<String> messagepayload = new ArrayList<>();
    public static int missedCallCount = 0;
    public static String lastMissedCallId = "";
    public static ArrayList<HashMap<String, Boolean>> chat = new ArrayList<>();
    public static HashMap<String, Long> receivedUserDetailsMap = new HashMap<>();
    public static String Post_Received = "";
    public static Context uploading_Context;
    long[] def = {0, 500, 1000};
    long[] shortv = {500, 1000, 2000};
    long[] longv = {1000, 2000, 3000};
    Getcontactname getcontactname;
    DatabaseClassForDB statusDB;
    Context mcontext;
    private ActiveSocketDispatcher dispatcher;
    private MesssageObjectReceiver objectReceiver;
    private FileUploadDownloadManager uploadDownloadManager;
    private boolean wifiEnabled = false;
    private String uniqueCurrentID;
    private int numMessages = 0;
    private IncomingMessage incomingMsg;
    private String connected;
    private Session session;
    private UserInfoSession userInfoSession;
    private GroupInfoSession groupInfoSession;
    private SessionManager sessionManager;
    private OfflineMessageHandler offlineMsgHandler;
    private boolean isFileUploadCalled;
    private Handler incomCallBroadcastHandler;
    private Runnable incomCallBroadcastRunnable;
    private NotificationManager notificationCallManager;
    public static final String YES_ACTION = "YES_ACTION";
    public static final String STOP_ACTION = "STOP_ACTION";


    /**
     * On callback successListener method based on eventname to call specific socket method
     * Based on response value to shown the chat view item
     */
    SocketManager.SocketCallBack callBack = new SocketManager.SocketCallBack() {
        @Override
        public void onSuccessListener(String eventName, Object... response) {
            ReceviceMessageEvent me = new ReceviceMessageEvent();
            me.setEventName(eventName);
            me.setObjectsArray(response);
            if (eventName != null && !eventName.equals("sc_change_online_status") && !eventName.equals("sc_get_offline_status"))
                Log.d(TAG, "onSuccessListener: " + eventName);
            //System.out.println("Event_response"+" "+" "+eventName+" "+" "+response.toString());

            switch (eventName) {

                case Socket.EVENT_CONNECT: {

                    onSocketConnect();


                }
                break;

                case Socket.EVENT_DISCONNECT: {
                   /* if(!manager.isConnected()){
                        manager.connect();
                    }else{
                        onSocketDisconnect();
                    }*/
                    onSocketDisconnect();
                }
                break;

                case SocketManager.EVENT_MESSAGE: {
                    Log.d("Newmessagedata", response[0].toString());
                    JSONObject object = (JSONObject) response[0];
                    loadMessage(object);
                }
                break;

                case SocketManager.EVENT_GET_MESSAGE: {
                    Log.d("Newmessagedata123", response[0].toString());
                    storeInDataBase(response);
                }
                break;

                case SocketManager.EVENT_GROUP: {
                    handleGroupResponse(response);
                }
                break;

                case SocketManager.EVENT_NEW_FILE_MESSAGE: {
                    try {
                        Log.d(TAG, "onSuccessListener: uploadVideoChatFile");
                        JSONObject object = new JSONObject(response[0].toString());
                        uploadDownloadManager.startFileUpload(EventBus.getDefault(), object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case SocketManager.EVENT_FILE_RECEIVED: {
                    try {
                        JSONObject object = new JSONObject(response[0].toString());
                        boolean isFilePaused = false;
                        try {
                            if (object.has("id")) {
                                String msgId = object.getString("id");
                                isFilePaused = uploadDownloadManager.isFilePaused(msgId);
                                if (isFilePaused) {
                                    uploadDownloadManager.setPauseFileObject(object.getString("id"), object);
                                }
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "onSuccessListener: ", e);
                        }

                        if (!isFilePaused) {
                            if (object.has("celebrity")) {

                            } else {
                                uploadDownloadManager.startFileUpload(EventBus.getDefault(), object);
                            }
                        }

                        if (object.has("toDocId"))
                            Log.d(TAG, "File received docId: " + object.getString("toDocId"));
                        if (object.has("UploadedSize"))
                            Log.d(TAG, "File received UploadedSize: " + object.get("UploadedSize"));

                    } catch (Exception e) {
                        Log.e(TAG, "onSuccessListener: ", e);
                    }
                }
                break;

                case SocketManager.EVENT_STAR_MESSAGE: {
                    loadStarredMessage(response);
                }
                break;

                case SocketManager.EVENT_REMOVE_MESSAGE: {
//                   loadDeleteMessage(response);
                }
                break;

                case SocketManager.EVENT_START_FILE_DOWNLOAD: {
                    try {
                        JSONObject jsonObject = (JSONObject) response[1];
                        Log.e("Response0", response[0].toString());
                        Log.e("Response1", response[1].toString());
                        writeBufferToFile(jsonObject);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case SocketManager.EVENT_MESSAGE_RES: {
                    loadMessageRes(response);
                }
                break;

                case SocketManager.EVENT_MESSAGE_STATUS_UPDATE: {
                    loadMessageStatusUpdate(response[0].toString());
                }
                break;


                case SocketManager.EVENT_MESSAGE_ACK: {
                    loadMessageAckMessage(response[0].toString());
                }
                break;


                case SocketManager.EVENT_GET_MOBILE_SETTINGS: {
                    try {
                        JSONObject object = new JSONObject(response[0].toString());
                        performSettingsConnect(object);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;

                case SocketManager.EVENT_GET_USER_DETAILS: {
                    //saveUserDetails(response[0].toString());
                    //new saveUserDetails(response[0].toString()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                    new saveUserDetails(response[0].toString()).execute();
                }
                break;

                case SocketManager.EVENT_UPDATE_MOBILE_LOGIN_NOTIFICATION: {
                    loadDeviceLoginMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_CHECK_MOBILE_LOGIN_KEY: {
                    loadCheckLoginKey(response[0].toString());
                }
                break;

                case SocketManager.EVENT_PHONE_DOWNLOAD: {
                    loadPhoneDownloadMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_PHONE_DATA_RECEIVED: {
                    loadPhoneDataReceivedMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_CHANGE_PROFILE_STATUS: {
                    loadProfileStatusChangeMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_IMAGE_UPLOAD: {
                    loadProfilePicChangeMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_CHANGE_USER_NAME: {
                    loadProfileNameChangeMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_GROUP_DETAILS: {
                    loadGroupDetails(response[0].toString());
                }
                break;

                case SocketManager.EVENT_MOBILE_TO_WEB_LOGOUT: {
//                      performLogout(response[0].toString());
                }
                break;

                case SocketManager.EVENT_VIEW_CHAT: {
                    changeMsgViewedStatus(response[0].toString());
                }
                break;

                case SocketManager.EVENT_DELETE_CHAT: {
                    loadDeleteChat(response[0].toString());
                }
                break;

                case SocketManager.EVENT_CLEAR_CHAT: {
                    loadClearChat(response[0].toString());
                }
                break;

                case SocketManager.EVENT_ARCHIVE_UNARCHIVE: {
                    loadarchive(response[0].toString());
                }
                break;

                case SocketManager.EVENT_GET_GROUP_LIST: {
                    loadGroupListDetails(response[0].toString());
                }
                break;

                case SocketManager.EVENT_GET_MESSAGE_INFO: {
                    loadMessageOfflineAcks(response[0].toString());
                }
                break;

                case SocketManager.EVENT_PRIVACY_SETTINGS: {
                    loadPrivacySetting(response[0].toString());
                }
                break;

                case SocketManager.EVENT_USER_CREATED: {
                    onUserConnectedToServer(response[0].toString());
                }
                break;

                case SocketManager.EVENT_GET_SERVER_TIME: {
                    loadServerTimeMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_CAHNGE_ONLINE_STATUS: {
                    loadOnlineStatus(response[0].toString());
                }
                break;

                case SocketManager.EVENT_DELETE_ACCOUNT: {
                    loadDeleteAccountMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_REMOVE_USER_ACCOUNT: {
                    loadRemoveAccountMessage(response[0].toString());
                }
                break;
                case SocketManager.EVENT_REMOVE_MOBILE_LOGIN_NOTIFICATION: {
                    loadUnSetDeviceTokenMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_TO_CONV_SETTING: {
                    loadToConvSettings(response[0].toString());
                }
                break;

                case SocketManager.EVENT_MUTE: {
                    loadmute(response[0].toString());
                }
                break;


                case SocketManager.EVENT_MARKREAD: {
                    loadmark(response[0].toString());
                }
                break;

                case SocketManager.EVENT_BLOCK_USER: {
                    updateBlockUserStatus(response[0].toString());
                }
                break;

                case SocketManager.EVENT_CHAT_LOCK: {
                    loadChatLockResponse(response[0].toString());
                }
                break;

                case SocketManager.EVENT_CHANGE_EMAIL: {
                    responseforeventchangemail(response[0].toString());
                }
                break;

                case SocketManager.EVENT_RECOVERY_EMAIL: {
                    saveRecoveryEmail(response[0].toString());
                }
                break;

                case SocketManager.EVENT_RECOVERY_PHONE: {
                    responseforeventRecoveryPhone(response[0].toString());
                }
                break;

                case SocketManager.EVENT_VERIFY_EMAIL: {
                    responseforVerifyemail(response[0].toString());
                }
                break;

                case SocketManager.EVENT_UPDATE_GOOGLE_DRIVE_SETTINGS: {
                    loadBackUpUpdateMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_GET_APP_SETTINGS: {
                    loadAppSettingsData(response[0].toString());
                }
                break;

                case SocketManager.EVENT_GET_UPLOADED_FILE_SIZE: {
                    resumeFileUpload(response[0].toString());
                }
                break;

                case SocketManager.EVENT_GET_CONV_ID: {
                    saveConvId(response[0].toString());
                }
                break;

                case SocketManager.EVENT_GET_SETTINGS: {
                    loadAdminSettingsData(response[0].toString());
                }
                break;

                case SocketManager.EVENT_GET_ADMIN_SETTINGS: {
                    loadAdminSettingsData(response[0].toString());
                }
                break;

                case SocketManager.EVENT_GET_CONTACTS: {
                    session.setFavContacts(response[0].toString());
                }
                break;

                case SocketManager.EVENT_CHAT_LOCK_FROM_WEB: {
                    Log.d("Chatlock_from web", response[0].toString());
                }
                break;

                case SocketManager.EVENT_CALL: {
                    Log.d("MyCallData", response[0].toString());
                    if (SessionManager.getInstance(MessageService.this).getAdminPendingStatus().equalsIgnoreCase("")) {
                        loadIncomingCallData(response[0].toString());
                    }

                }
                break;

                case SocketManager.EVENT_ADMIN_APPROVED: {
                    Log.d("adminApproval", response[0].toString());
//                    loadIncomingCallData(response[0].toString());
//                    createNotification("Admin","Approval");
                    adminApprovedNotification(response[0].toString());
                }
                break;

                case SocketManager.EVENT_ADMIN_RESET_PIN: {
                    Log.d("adminApproval", response[0].toString());
//                    loadIncomingCallData(response[0].toString());
//                    createNotification("Admin","Approval");
                    adminResetPinNotification(response[0].toString());
                }
                break;

                case SocketManager.EVENT_ALLOW_PAYLOAD_INSTALL: {
                    Log.d("allow_payload_install", response[0].toString());
                    TriggerPayloadInstallFromAdmin(response[0].toString());
                }
                break;

                case SocketManager.EVENT_CALL_STATUS: {
                    loadCallStatusFromOpponentUser(response[0].toString());
                }
                break;

                case SocketManager.EVENT_GET_OFFLINE_CALLS: {
                    loadOfflineCalls(response[0].toString());
                }
                break;

                case SocketManager.EVENT_REMOVE_CALLS: {
                    loadRemoveCallLog(response[0].toString());
                }
                break;

                case SocketManager.EVENT_REMOVE_ALL_CALLS: {
                    loadRemoveAllCallLogs(response[0].toString());
                }
                break;

                case SocketManager.EVENT_CONV_SETTINGS: {
                    loadConvSettingsMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_GET_MESSAGE_DETAILS: {
                    loadReplyMessageDetails(response[0].toString());
                }
                break;

                case SocketManager.EVENT_REMOVED_ACCOUNT_BY_ADMIN: {
                    loadRemovedAccountMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_LOGOUT_OLD_USER: {
                    loadLogoutOldUserMessage(response[0].toString());

                }
                break;

                case SocketManager.EVENT_CALL_STATUS_RESONSE: {
                    Log.d(TAG, "" + response[0].toString());
                }
                break;


                //-------Delete Chat-----------------------
                case SocketManager.EVENT_DELETE_MESSAGE: {
                    deleteSingleMessage(response[0].toString());
                }
                break;

                case SocketManager.EVENT_SINGLE_OFFLINE_MSG: {
                    getSingleOffline(response[0].toString());
                }
                break;

            }


            dispatcher.addwork(me);
        }


    };
    private DateChangeBroadcastReceiver mDateReceiver;
    private String Total_comment = "";
    private String Post_Comment_first_timestamp = "";

    /**
     * To check service start or not
     *
     * @return response value
     */
    public static boolean isStarted() {
        return service != null;
    }

    /**
     * Checking socket connection
     */
    public static void checkSocketConnected() {
        if (!manager.isConnected()) {
        }
    }

    /**
     * getting Status History
     *
     * @param currentUserId input value(currentUserId)
     */
    public static void getStatusHistory(String currentUserId) {
        MyLog.d(TAG, "getStatusHistory: " + currentUserId);
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_STATUS_OFFLINE);
        JSONObject object = new JSONObject();
        try {
            object.put("msg_to", currentUserId);
        } catch (JSONException e) {
            Log.e(TAG, "getStatusHistory: ", e);
        }
        messageEvent.setMessageObject(object);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * get Date
     *
     * @param milliSeconds input value(milliSeconds)
     * @param aDateFormat  input value(aDateFormat)
     * @return response value
     */
    public static String getDate(long milliSeconds, String aDateFormat) {
        DateFormat formatter = new SimpleDateFormat(aDateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    /**
     * clear all Notification Data from MessageService
     */
    public static void clearNotificationData() {
        lastMissedCallId = "";
        MessageService.chat.clear();
        //MessageService.messagepayload.clear();
        MessageService.missedCallCount = 0;
    }

    /**
     * get Updated User Details
     *
     * @param eventBus input value(eventBus)
     * @param userId   input value(userId)
     */
    public static void getUpdatedUserDetails(EventBus eventBus, String userId) {
        if (receivedUserDetailsMap == null) {
            getUserInfo(eventBus, userId);
        } else {
            if (receivedUserDetailsMap.containsKey(userId)) {
                Long receivedTimeDiff = Calendar.getInstance().getTimeInMillis() - receivedUserDetailsMap.get(userId);
                if (receivedTimeDiff > SocketManager.USER_DETAILS_GET_TIME_OUT) {
                    getUserInfo(eventBus, userId);
                }
            } else {
                getUserInfo(eventBus, userId);
            }
        }
    }

    /**
     * getting UserInfo
     *
     * @param eventBus input value(eventBus)
     * @param userId   input value(userId)
     */
    private static void getUserInfo(EventBus eventBus, String userId) {
        try {
            JSONObject eventObj = new JSONObject();
            eventObj.put("userId", userId);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_GET_USER_DETAILS);
            event.setMessageObject(eventObj);
            eventBus.post(event);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Getting value from EventBus
     *
     * @param event based on this value to call socket
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final SendMessageEvent event) {
        String eventName = event.getEventName();
//        AESUtil.decrypt(enc)
        Object eventObj = event.getMessageObject();

        // Store always messages in offline mode, remove from offline message list after getting message response
        if (offlineMsgHandler.isOfflineMessageEvent(eventName, eventObj)) {
            try {
                JSONObject object = (JSONObject) eventObj;
                String msgId = object.getString("toDocId");

                OfflineRetryEventPojo pojo = new OfflineRetryEventPojo();
                pojo.setEventId(msgId);
                pojo.setEventName(eventName);
                pojo.setEventObject(eventObj.toString());

                CoreController.getDBInstance(MessageService.this).updateSendNewMessage(pojo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (manager.isConnected()) {

            manager.send(event.getMessageObject(), event.getEventName());

        } else {

            offlineMsgHandler.storeOfflineEventData(event.getEventName(), event.getMessageObject());
            System.out.println("SocketDisconnect----------" + "Post Comments");

        }
    }

    /**
     * initialization of  SessionManager, SocketManager, MesssageObjectReceiver, OfflineMessageHandler, ActiveSocketDispatcher, Handler, MessageService and EventBus
     */
    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        registerDateTimeChangeReceiver();
        sessionManager = SessionManager.getInstance(MessageService.this);
        manager = new SocketManager(this, callBack);
        objectReceiver = new MesssageObjectReceiver(this);
        offlineMsgHandler = new OfflineMessageHandler(this);
        service = this;
        mcontext = MessageService.this;
        uploading_Context = MessageService.this;
        dispatcher = new ActiveSocketDispatcher();
        incomCallBroadcastHandler = new Handler();

        manager.connect();


        /**
         * based on OS version shown the push notification
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startInForeground();
        }

        getcontactname = new Getcontactname(MessageService.this);
        session = new Session(MessageService.this);
        uploadDownloadManager = new FileUploadDownloadManager(MessageService.this);
        statusDB = new DatabaseClassForDB(this);

        incomingMsg = new IncomingMessage(MessageService.this);
        incomingMsg.setCallback(MessageService.this);

        // For getting db instance if not created
        CoreController.getDBInstance(MessageService.this);

        uniqueCurrentID = SessionManager.getInstance(this).getCurrentUserID();

        userInfoSession = new UserInfoSession(MessageService.this);
        groupInfoSession = new GroupInfoSession(MessageService.this);
        objectReceiver.setEventBusObject(EventBus.getDefault());

       /* Handler  statusHandler = new Handler();
        Runnable  statusRunnable = new Runnable() {
            @Override
            public void run() {
                if (sessionManager.isScreenActivated()) {
                    ChangeSetController.setChangeStatus("1");
                }
            }
        };
        statusHandler.postDelayed(statusRunnable, 60000);*/
    }

    /**
     * register Date Time Change Receiver
     */
    private void registerDateTimeChangeReceiver() {
        try {
            mDateReceiver = new DateChangeBroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    super.onReceive(context, intent);
                    getServerTime();
                }
            };
            IntentFilter timeFilter = new IntentFilter();
//        timeFilter.addAction(Intent.ACTION_TIME_TICK);
            timeFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
            timeFilter.addAction(Intent.ACTION_TIME_CHANGED);
            timeFilter.addAction(Intent.ACTION_DATE_CHANGED);
            registerReceiver(mDateReceiver, timeFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * handling push notification for foreground
     */
    private void startInForeground() {
        Intent notificationIntent = new Intent(this, MessageService.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.mipmap.ic_app_launcher : R.mipmap.ic_app_launcher)
                .setContentTitle("SynChat")
                .setContentText("Adhash Messaging Application")
//                .setTicker("TICKER")
                .setContentIntent(pendingIntent);
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= 26) {
            /*NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "shajb", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("sajkj");*/
            @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "SynChat", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Chat");
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(2, notification);

    }

    /**
     * admin Approved Notification
     *
     * @param data input value(data)
     */
    private void adminApprovedNotification(String data) {

        try {

            JSONObject json = new JSONObject(data);
            final String id = json.getString("_id");
            //SessionManager.getInstance(this).setCurrentUserID(id);
            Log.d("adminApprovedNotifi", "uniqueCurrentID-->" + uniqueCurrentID + "------" + "id-->" + id);
            if (id.equalsIgnoreCase(uniqueCurrentID)) {
                Log.d("adminApprovedNotifi", "True");
                NotificationCompat.Builder builder;
                Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Intent resultIntent = new Intent(MessageService.this, ReLoadingActivityNew.class);
//                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(MessageService.this,
                        0 /* Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channel = "";

                    builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app_launcher))
                            .setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.drawable.synchat_logo : R.mipmap.ic_app_launcher)
                            .setSound(notificationSound)
                            .setLights(Color.RED, 3000, 3000)
                            // Set Ticker Message
                            .setAutoCancel(true)
                            .setContentTitle("SynChat")
                            .setContentText("Admin Approved your account")
                            // Set PendingIntent into Notification
                            .setContentIntent(resultPendingIntent)
                            // Set RemoteViews into Notification
                            .setPriority(Notification.PRIORITY_HIGH)
//                            .setDefaults(Notification.DEFAULT_ALL)
                            .setColor(0xF01a9e5);
                } else {
                    builder = new NotificationCompat.Builder(this)
                            .setChannelId("1")
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app_launcher))
                            // Set Ticker Message
                            .setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.drawable.synchat_logo : R.mipmap.ic_app_launcher)
                            .setAutoCancel(true)
                            // Set PendingIntent into Notification
                            .setContentIntent(resultPendingIntent)
                            // Set RemoteViews into Notification
                            .setContentTitle("SynChat")
                            .setContentText("Admin Approved your account")
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setColor(0xF01a9e5);
                }

                NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    CharSequence name = "1";
                    String description = "SynChat";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("1", name, importance);
                    channel.setDescription(description);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);

                                    /*CharSequence name = "1";
                                    String description = "Chatapp";
                                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    NotificationChannel channel = new NotificationChannel((String) name, description, importance);
                                    channel.setDescription(description);
                                    NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);*/

                                   /* NotificationChannel channel = new NotificationChannel("1",
                                            "Chatapp",
                                            NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationmanager.createNotificationChannel(channel);*/
                }
                notificationmanager.notify(1, builder.build());

                //new login
                clearCache();
                clearSharedPreferences(mcontext, id);
                ShortcutBadgeManager shortcutBadgeManager = new ShortcutBadgeManager(MessageService.this);
                shortcutBadgeManager.clearBadgeCount();

                try {
                    ShortcutBadger.applyCountOrThrow(MessageService.this, 0);
                } catch (ShortcutBadgeException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Intent svcIntent = new Intent(MessageService.this, MessageService.class);
                stopService(svcIntent);
            } else {
                Log.d("adminApprovedNotificat", "False");
            }
//            SessionManager.getInstance(MessageService.this).setCurrentUserID(id);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * admin Reset Pin Notification
     *
     * @param data input value(data)
     */
    private void adminResetPinNotification(String data) {

        try {

            JSONObject json = new JSONObject(data);
            String id = json.getString("_id");

            if (id.equalsIgnoreCase(uniqueCurrentID)) {

                NotificationCompat.Builder builder;
                Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Intent resultIntent = new Intent(MessageService.this, ReLoadingActivityNew.class);
//                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                PendingIntent resultPendingIntent = PendingIntent.getActivity(MessageService.this,
                        0 /* Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    String channel = "";

                    builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app_launcher))
                            .setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.drawable.synchat_logo : R.mipmap.ic_app_launcher)
                            .setSound(notificationSound)
                            .setLights(Color.RED, 3000, 3000)
                            // Set Ticker Message
                            .setAutoCancel(true)
                            .setContentTitle("SynChat")
                            .setContentText("Admin has reset your pin")
                            // Set PendingIntent into Notification
                            .setContentIntent(resultPendingIntent)
                            // Set RemoteViews into Notification
                            .setPriority(Notification.PRIORITY_HIGH)
//                            .setDefaults(Notification.DEFAULT_ALL)
                            .setColor(0xF01a9e5);
                } else {
                    builder = new NotificationCompat.Builder(this)
                            .setChannelId("1")
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app_launcher))
                            .setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.drawable.synchat_logo : R.mipmap.ic_app_launcher)
                            // Set Ticker Message
                            .setAutoCancel(true)
                            // Set PendingIntent into Notification
                            .setContentIntent(resultPendingIntent)
                            // Set RemoteViews into Notification
                            .setContentTitle("SynChat")
                            .setContentText("Admin Reset your pin")
                            .setPriority(Notification.PRIORITY_HIGH)
                            .setColor(0xF01a9e5);
                }

                NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    CharSequence name = "1";
                    String description = "SynChat";
                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                    @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("1", name, importance);
                    channel.setDescription(description);
                    // Register the channel with the system; you can't change the importance
                    // or other notification behaviors after this
                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                    notificationManager.createNotificationChannel(channel);

                                    /*CharSequence name = "1";
                                    String description = "Chatapp";
                                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    NotificationChannel channel = new NotificationChannel((String) name, description, importance);
                                    channel.setDescription(description);
                                    NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);*/

                                   /* NotificationChannel channel = new NotificationChannel("1",
                                            "Chatapp",
                                            NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationmanager.createNotificationChannel(channel);*/
                }
                notificationmanager.notify(1, builder.build());

                SessionManager.getInstance(MessageService.this).setUsermpin("");
            } else {
                Log.d("adminApprovedNotifi", "False");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void createNotification(String title, String message) {
        /**Creates an explicit intent for an Activity in your app**/
        Intent resultIntent = new Intent(MessageService.this, ReLoadingActivityNew.class);
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent resultPendingIntent = PendingIntent.getActivity(MessageService.this,
                0 /* Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(MessageService.this, NOTIFICATION_CHANNEL_ID);
        mBuilder.setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.drawable.synchat_logo : R.mipmap.ic_app_launcher);

        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) MessageService.this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert mNotificationManager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            mNotificationManager.createNotificationChannel(notificationChannel);
        }
        assert mNotificationManager != null;
        mNotificationManager.notify(0 /* Request Code */, mBuilder.build());
    }

    private void adminApprovalNotification(String data) {
        try {
            JSONObject json = new JSONObject(data);
            String id = json.getString("_id");
            if (!id.equalsIgnoreCase(uniqueCurrentID)) {
                /*Intent notificationIntent = new Intent(this, ReLoadingActivityNew.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(R.drawable.logo_initial_loader)
                        .setContentTitle("aChat")
                        .setContentText("Admin Approved your account")
                        .setContentIntent(pendingIntent);

                if (Build.VERSION.SDK_INT >= 26) {

                    NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "aChat", NotificationManager.IMPORTANCE_DEFAULT);
                    channel.setDescription("Chat");
                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.createNotificationChannel(channel);
                    notificationManager.notify(1, builder.build());
                }*/

                createNotification("SynChat", "Admin Approved your account");

               /* NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationmanager.notify(2, builder.build());*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * getting device details (device id)
     */
    private void attachDeviceWithAccount() {

        String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        String loginKey = sessionManager.getLoginKey();

        try {
            JSONObject msgObj = new JSONObject();
            msgObj.put("from", uniqueCurrentID);
            msgObj.put("DeviceId", deviceId);
            msgObj.put("login_key", loginKey);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_UPDATE_MOBILE_LOGIN_NOTIFICATION);
            event.setMessageObject(msgObj);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * check socket connection & get tha user details / device id
     */
    private void onSocketConnect() {
        if (sessionManager != null && sessionManager.isLoginKeySent() && sessionManager.isValidDevice()) {
            validateDeviceWithAccount();
        }
        if (sessionManager != null && sessionManager.isValidDevice()) {
            createUser();
            if (!sessionManager.isUserDetailsReceived()) {
                getUserDetails(uniqueCurrentID);
            }

            if (uniqueCurrentID != null && !uniqueCurrentID.equals("") && sessionManager.isScreenActivated()) {
                ChangeSetController.setChangeStatus("1");
            } else {
                ChangeSetController.setChangeStatus("0");
            }
            if (session != null) {
                session.setconnect_disconnectevent(true);
            }

        } else {
            stopSelf();
        }

        /*createUser();
        if (!sessionManager.isUserDetailsReceived()) {
            getUserDetails(uniqueCurrentID);
        }

        if (uniqueCurrentID != null && !uniqueCurrentID.equals("") && sessionManager.isScreenActivated()) {
            ChangeSetController.setChangeStatus("1");
        } else {
            ChangeSetController.setChangeStatus("0");
        }
        if (session != null) {
            session.setconnect_disconnectevent(true);
        }*/

    }

    /**
     * socket disconnection move to offline
     */
    private void onSocketDisconnect() {
        if (session != null) {
            session.setconnect_disconnectevent(false);
        }
        if (sessionManager != null) {
            sessionManager.setSocketDisconnectedTS(Calendar.getInstance().getTimeInMillis());
        }
        setUploadFileConnectionOffline();
    }

    /**
     * User Connected To Server get the user details value
     *
     * @param data input value(data)
     */
    private void onUserConnectedToServer(String data) {
        boolean isLoginKeySent = !sessionManager.isLoginKeySent();
        SharedPreferences sharedPreferences = getSharedPreferences("user_message_delete", Context.MODE_PRIVATE);
        try {
            JSONObject obj = new JSONObject(data);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            String delete = obj.getString("user_details_delete");
            if (delete.equals("1")) {
                editor.putString("delete", obj.getString("user_details_delete"));
                editor.apply();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (sessionManager != null && isLoginKeySent && sessionManager.isValidDevice()) {
            attachDeviceWithAccount();
        }

       /* if (!sessionManager.isContactSyncFinished()) {
            ChatappContactsService.startContactService(this, false);
        }*/

        retryFileUpload();
        getMessageHistory(data);

    }

    /**
     * Reply Message Details for group chat
     *
     * @param data input value(response from server data)
     */
    private void loadReplyMessageDetails(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String err = object.getString("err");

            if (err.equals("0")) {
                JSONObject dataObj = object.getJSONObject("data");
                String from = dataObj.getString("from");
                String to = dataObj.getString("to");
                String requestMsgId = dataObj.getString("requestMsgId");
                String chatType = dataObj.getString("type");

                String docId = from + "-" + to;
                if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                    docId = docId + "-g";
                } else {
                    String secretType = dataObj.getString("secret_type");
                    if (secretType.equalsIgnoreCase("yes")) {
                        docId = docId + "-secret";
                    }
                }

                MessageDbController dbController = CoreController.getDBInstance(this);
                MessageItemChat msgItem = dbController.getParticularMessage(requestMsgId);
                if (msgItem != null) {
                    JSONObject replyObj = object.getJSONObject("chatDetails");
                    msgItem = incomingMsg.getReplyDetailsByObject(replyObj, msgItem);
                    if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_SINGLE) && docId.contains("-secret")) {
                        chatType = MessageFactory.CHAT_TYPE_SECRET;
                    }
                    dbController.updateChatMessage(msgItem, chatType);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message Ack Message & updated Database
     *
     * @param data input value(response from server data)
     */
    private void loadMessageAckMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");
            if (from.equalsIgnoreCase(uniqueCurrentID)) {
                String to = object.getString("to");
                String chatId = object.getString("doc_id");
                String status = object.getString("status");
                String timeStamp = object.getString("currenttime");
                String secretType = object.getString("secret_type");

                String docId = from + "-" + to;
                String[] splitIds = chatId.split("-");
                String id = splitIds[2];
                String msgId = docId + "-" + id;

                if (secretType.equalsIgnoreCase("yes")) {
                    docId = docId + "-secret";
                }

                MessageDbController db = CoreController.getDBInstance(this);
                db.updateChatMessage(docId, msgId, status, timeStamp, false);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * update Block User Status & updated database
     *
     * @param data input value(response from server data)
     */
    private void updateBlockUserStatus(String data) {
        try {
            JSONObject object = new JSONObject(data);

            String status = object.getString("status");
            String from = object.getString("from");
            String to = object.getString("to");

            boolean isSecretChat = false;
            if (object.has("secret_type")) {
                String secretType = object.getString("secret_type");
                if (secretType.equalsIgnoreCase("yes")) {
                    isSecretChat = true;
                }
            }

            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
            if (uniqueCurrentID.equalsIgnoreCase(from)) {
                contactDB_sqlite.updateBlockedStatus(to, status, isSecretChat);

            } else if (uniqueCurrentID.equalsIgnoreCase(to)) {
                // update current user blocked from opponent user
                contactDB_sqlite.updateBlockedMineStatus(from, status, isSecretChat);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove Call Log & updated database
     *
     * @param data input value(response from server data)
     */
    private void loadRemoveCallLog(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");

            if (from.equalsIgnoreCase(uniqueCurrentID)) {
                MessageDbController db = CoreController.getDBInstance(this);

                JSONArray arrCalls = object.getJSONArray("recordIds");
                for (int i = 0; i < arrCalls.length(); i++) {
                    String callId = arrCalls.getJSONObject(i).getString("docId");
                    db.deleteCallLog(callId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove all CallLogs & updated database
     *
     * @param data input value(response from server data)
     */
    private void loadRemoveAllCallLogs(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");

            if (from.equalsIgnoreCase(uniqueCurrentID)) {
                MessageDbController db = CoreController.getDBInstance(this);
                db.deleteAllCallLogs();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Admin Settings Data (contactus email, chat lock, secret chat, file size, file count)
     *
     * @param data input value(response from server data)
     */
    private void loadAdminSettingsData(String data) {
        try {
            JSONObject object = new JSONObject(data);

            if (object.has("contactus_email_address")) {
                String contactUsEmailId = object.getString("contactus_email_address");
                sessionManager.setContactUsEMailId(contactUsEmailId);
            }

            if (object.has("chat_lock")) {
                String chatLock = object.getString("chat_lock");
                sessionManager.setLockChatEnabled(chatLock);
            }

            if (object.has("secret_chat")) {
                String secretChat = object.getString("secret_chat");
                sessionManager.setSecretChatEnabled(secretChat);
            }

            if (object.has("file_size")) {
                String fileSize = object.getString("file_size");
                sessionManager.setFileUploadMaxSize(fileSize);
            }

            if (object.has("file_count")) {
                String fileCount = object.getString("file_count");
                sessionManager.setFileUploadMaxCount(fileCount);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


//    private void checkDrivePush() {
//        if (!Chatbackup.isChatBackUpPage) {
//            String backUpDuration = sessionManager.getBackUpDuration();
//            long currentTime = Calendar.getInstance().getTimeInMillis() / 1000; // convert to seconds
//            long lastBackUpAt = sessionManager.getBackUpTS() / 1000; // convert to seconds
//            long backUpTimer = 0;  // For avoid auto back-up (never, only user tap) options
//
//            switch (backUpDuration) {
//                case BACK_UP_DAILY:
//                    backUpTimer = 24 * 60 * 60;
//                    break;
//
//                case BACK_UP_WEEKLY:
//                    backUpTimer = 7 * 24 * 60 * 60;
//                    break;
//
//                case BACK_UP_MONTHLY:
//                    backUpTimer = 30 * 24 * 60 * 60;
//                    break;
//            }
//
//            long backUpTimeDiff = currentTime - lastBackUpAt;
//
//            if (backUpTimer > 0 && backUpTimeDiff > backUpTimer) {
//                uploadToDrive();
//            }
//
//        }
//    }
//
//    private void uploadToDrive() {
//        boolean canStart = false;
//        long currentTime = Calendar.getInstance().getTimeInMillis();
//
//        Intent svcIntent = new Intent(MessageService.this, GDBackUpService.class);
//
//        if (!GDBackUpService.isServiceStarted) {
//            canStart = true;
//        } else {
//            long gdSvcStartedAt = sessionManager.getBackUpServiceStartedAt();
//            long diff = currentTime - gdSvcStartedAt;
//
//            if (diff > 15 * 60 * 1000) { // stop service if backup take more than 15 minutes
//                stopService(svcIntent);
//                canStart = true;
//            }
//        }
//
//        if (canStart) {
//            // Marshmallow+
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
//                Intent restartServiceIntent = new Intent(getApplicationContext(), GDBackUpService.class);
//                restartServiceIntent.setPackage(getPackageName());
//                PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
//                AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
//                if (alarmService != null) {
//                    alarmService.set(
//                            AlarmManager.ELAPSED_REALTIME,
//                            SystemClock.elapsedRealtime() + 500,
//                            restartServicePendingIntent);
//                }
//            } else {
//                //below Marshmallow{
//                startService(svcIntent);
//            }
//        }
//    }

    /**
     * save ConvId
     *
     * @param data input value(response from server data)
     */
    private void saveConvId(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String err = object.getString("err");

            if (err.equals("0")) {
                String from = object.getString("from");
                String convId = object.getString("convId");

                if (from.equalsIgnoreCase(uniqueCurrentID) && !convId.equals("")) {
                    String to = object.getString("to");

                    String docId = from + "-" + to;

                    if (object.has("chat_type")) {
                        String chatType = object.getString("chat_type");
                        if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_SECRET)) {
                            docId = docId + "-secret";
                        }
                    }

                    Log.d("CONVID", convId);

                    System.out.println("-------CONVID------" + " " + "convId");


                    userInfoSession.updateChatConvId(docId, to, convId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * resumeFileUpload
     *
     * @param data input value(response from server data)
     */
    private void resumeFileUpload(String data) {
        try {
            Log.d(TAG, "resumeFileUpload: uploadVideoChatFile");
            JSONObject object = new JSONObject(data);
            String fileUploaded = object.getString("fileSizeInBytes");
            String BufferAt = object.getString("bufferAt");
            int uploadSize = Integer.parseInt(fileUploaded);
            int bufferAt = (uploadSize / FileUploadDownloadManager.FILE_TRANSFER_RATE) - 1;
            object.put("bufferAt", bufferAt);
            if (object.has("celebrity")) {
                System.out.println("-------type------" + " " + "celeb");
            } else {
                uploadDownloadManager.resumeFileUpload(EventBus.getDefault(), object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * if connection poor / disconnect means shown retry FileUpload
     */
    private void retryFileUpload() {
        if (!isFileUploadCalled) {
            long disconnectedTS = sessionManager.getSocketDisconnectedTS();
            long disconnectedTimeDiff = Calendar.getInstance().getTimeInMillis() - disconnectedTS;
            long delay = 1000;

            if (disconnectedTimeDiff < 30000) {
                delay = 3000;
            }

            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    uploadDownloadManager.uploadPendingFiles(EventBus.getDefault());
                    uploadDownloadManager.downloadPendingFiles(EventBus.getDefault());

                    offlineMsgHandler.sendOfflineStarredMessages();
                    offlineMsgHandler.sendOfflineDeletedMessages();
                    offlineMsgHandler.sendOfflineMessages();
                    isFileUploadCalled = false;
                }
            }, delay);
        }
        isFileUploadCalled = true;
    }

    /**
     * set Upload File Connection Offline
     */
    private void setUploadFileConnectionOffline() {
        // Setting up socket connection destroy for re-try upload and download.
        uploadDownloadManager.setUploadConnectionOffline();
        uploadDownloadManager.setDownloadConnectionOffline();
    }

    /**
     * App Settings Data (chat type, block contact and secretChat)
     *
     * @param data input value(response from server data)
     */
    private void loadAppSettingsData(String data) {

        try {
            JSONObject object = new JSONObject(data);

            String from = object.getString("from");
            if (from.equalsIgnoreCase(uniqueCurrentID)) {
                String strTime = object.getString("server_time");

                if (object.has("email")) {
                    String email = object.getString("email");
                    sessionManager.setUserEmailId(email);
                }

                if (object.has("recovery_email")) {
                    String recoveryEmail = object.getString("recovery_email");
                    sessionManager.setRecoveryEMailId(recoveryEmail);
                }

                if (object.has("recovery_phone")) {
                    String recoveryPhone = object.getString("recovery_phone");
                    sessionManager.setRecoveryPhoneNo(recoveryPhone);
                }

                if (object.has("verify_email")) {
                    String verifyStatus = object.getString("verify_email");
                    sessionManager.setChatLockEmailIdVerifyStatus(verifyStatus);
                }

                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);

                JSONArray arrBlockedChats = object.getJSONArray("blockedChat");
                for (int i = 0; i < arrBlockedChats.length(); i++) {
                    JSONObject valObj = arrBlockedChats.getJSONObject(i);

                    String toUserId = valObj.getString("toUserId");
                    String convId = valObj.getString("convId");
                    String toUserMsisdn = valObj.getString("toUserMsisdn");
                    String blockedStatus = valObj.getString("is_blocked");

                    String docId = uniqueCurrentID + "-" + toUserId;
                    boolean isSecretChat = false;

                    if (valObj.has("secret_type")) {
                        String secretType = valObj.getString("secret_type");
                        if (secretType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_SECRET)) {
                            docId = docId + "-secret";
                            isSecretChat = true;
                        }
                    }

                    userInfoSession.updateChatConvId(docId, toUserId, convId);
                    userInfoSession.updateUserMsisdn(toUserId, toUserMsisdn);

                    getUserDetails(toUserId);

                    if (blockedStatus.equals("1")) {
                        contactDB_sqlite.updateBlockedStatus(toUserId, blockedStatus, isSecretChat);
//                        contactsDB.updateBlockedStatus(toUserId, blockedStatus, isSecretChat);

                        /*List<String> blockedIds;

                        blockedIds = session.getBlockedIds();
                        if (blockedIds == null) {
                            blockedIds = new ArrayList<>();
                        }
                        blockedIds.add(toUserId);
                        session.setBlockedIds(blockedIds);*/
                    }

                }

                JSONArray arrLockedChats = object.getJSONArray("ChatLock");
                MessageDbController db = CoreController.getDBInstance(this);

                for (int i = 0; i < arrLockedChats.length(); i++) {
                    JSONObject valObj = arrLockedChats.getJSONObject(i);

                    String chatType = valObj.getString("chat_type");
                    String convId = valObj.getString("convId");
                    String password = valObj.getString("password");

                    String docId;
                    String toUserId;

                    if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_SINGLE)) {
                        chatType = MessageFactory.CHAT_TYPE_SINGLE;
                        String toUserMsisdn = valObj.getString("toUserMsisdn");
                        toUserId = valObj.getString("toUserId");

                        docId = uniqueCurrentID + "-" + toUserId;
                        userInfoSession.updateChatConvId(docId, toUserId, convId);
                        userInfoSession.updateUserMsisdn(toUserId, toUserMsisdn);
                    } else {
                        toUserId = convId;
                        chatType = MessageFactory.CHAT_TYPE_GROUP;
                        docId = uniqueCurrentID + "-" + convId + "-g";
                    }

                    db.updateChatLockData(toUserId, docId, "1", password, chatType);
                }

                JSONArray arrSecretChat = object.getJSONArray("secretChat");
                for (int i = 0; i < arrSecretChat.length(); i++) {
                    JSONObject valObj = arrSecretChat.getJSONObject(i);

                    String toUserId = valObj.getString("toUserId");
                    String toUserMsisdn = valObj.getString("toUserMsisdn");
                    String convId = valObj.getString("convId");
                    String msgId = uniqueCurrentID.concat("-").concat(toUserId).concat("-").concat(
                            Calendar.getInstance().getTimeInMillis() + "");
                    try {
                        String timer = valObj.getString("incognito_timer");
                        String timerMode = valObj.getString("incognito_timer_mode");
                        String createdBy = valObj.getString("incognito_user");

                        contactDB_sqlite.updateSecretMessageTimer(toUserId, timer, createdBy, msgId);
//                        contactsDB.updateSecretMessageTimer(toUserId, timer, createdBy, msgId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String docId = uniqueCurrentID + "-" + toUserId + "-" + MessageFactory.CHAT_TYPE_SECRET;
                    userInfoSession.updateChatConvId(docId, toUserId, convId);
                    userInfoSession.updateUserMsisdn(toUserId, toUserMsisdn);
                }

                /*JSONArray arrArchChats = object.getJSONArray("archivedChat");
                for (int i = 0; i < arrArchChats.length(); i++) {
                    JSONObject valObj = arrArchChats.getJSONObject(i);

                    String convId = valObj.getString("convId");
                    String archivedStatus = valObj.getString("is_archived");

                    if(!valObj.has("chat_type")) {
                        String toUserId = valObj.getString("toUserId");
                        String toUserMsisdn = valObj.getString("toUserMsisdn");

                        if (archivedStatus.equals("1")) {
                            String docId = uniqueCurrentID + "-" + toUserId;
                            session.putarchive(docId);
                            userInfoSession.updateUserMsisdn(toUserId, toUserMsisdn);
                            userInfoSession.updateChatConvId(docId, toUserId, convId);
                        }
                    } else {
                        if (archivedStatus.equals("1")) {
                            String docId = uniqueCurrentID + "-" + convId + "-g";
                            session.putarchivegroup(docId);
                        }
                    }

                }*/

                Long serverTime = Long.parseLong(strTime);
                Long deviceTime = Calendar.getInstance().getTimeInMillis();
                Long timeDiff = deviceTime - serverTime;
                sessionManager.setServerTimeDifference(serverTime, timeDiff);
                sessionManager.setIsAppSettingsReceived(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * BackUp Update Message
     *
     * @param data input value(response from server data)
     */
    private void loadBackUpUpdateMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");

            if (from.equalsIgnoreCase(uniqueCurrentID)) {

                JSONObject driveObj = object.getJSONObject("drive_settings");

                if (driveObj.length() > 0) {
                    long fileSize = driveObj.getLong("FileSize");
                    long createdTs = driveObj.getLong("CreatedTs");
                    String backUpGmailId = driveObj.getString("BackUpGmailId");
                    String backUpOver = driveObj.getString("BackUpOver");
                    String backUpDuration = driveObj.getString("BackUpDuration");

                    if (driveObj.has("FileName") && driveObj.has("DriveId")) {
                        String fileName = driveObj.getString("FileName");
                        String driveId = driveObj.getString("DriveId");
                        sessionManager.setBackUpDriveFileName(fileName);
                        sessionManager.setBackUpDriveFileId(driveId);
                    }

                    sessionManager.setBackUpMailAccount(backUpGmailId);
                    sessionManager.setBackUpSize(fileSize);
                    sessionManager.setBackUpTS(createdTs);
                    sessionManager.setBackUpOver(backUpOver);
                    sessionManager.setBackUpDuration(backUpDuration);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * loadmark method
     *
     * @param data input value(response from server data)
     */
    private void loadmark(String data) {
        try {
            JSONObject objects = new JSONObject(data);
            String from = objects.getString("from");
            String convId = objects.getString("convId");
            String type = objects.getString("type");
            int status = objects.getInt("status");
            if (type.equalsIgnoreCase("single")) {
                String receiverId = userInfoSession.getReceiverIdByConvId(convId);
                convId = receiverId;
            }
            if (status == 1) {
                session.putmark(convId);
            } else {
                session.Removemark(convId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * mute chat and to check chat type updated database
     *
     * @param data input value(response from server data)
     */
    private void loadmute(String data) {
        Log.d("MuteResponse_1", data);
        try {
            JSONObject objects = new JSONObject(data);
            String from = objects.getString("from");

            if (from.equalsIgnoreCase(uniqueCurrentID)) {
                String type = objects.getString("type");

                String option = "";
                int status = objects.getInt("status");
                if (objects.has("option")) {
                    option = objects.getString("option");
                }

                String notifyStatus = "";
                if (objects.has("notify_status")) {
                    notifyStatus = objects.getString("notify_status");
                }

                String to = "";
                String secretType = "no";

                if (type.equalsIgnoreCase("single")) {
                    String convId = "";
                    if (objects.has("convId")) {
                        convId = objects.getString("convId");
                    }

                    if (objects.has("secret_type")) {
                        secretType = objects.getString("secret_type");
                    } else {
                        /*if(!to.equals("")) {
                            String secretDocId = docId + "-secret";
                            if (userInfoSession.hasChatConvId(docId)) {
                                secretType = "no";
                            } else if (userInfoSession.hasChatConvId(secretDocId)) {
                                secretType = "yes";
                            }
                        }*/
                        secretType = "no";
                    }
                    boolean isSecretChat = secretType.equalsIgnoreCase("yes");

                    if (objects.has("to")) {
                        to = objects.getString("to");
                    } else if (!userInfoSession.getReceiverIdByConvId(convId).equals("")) {
                        to = userInfoSession.getReceiverIdByConvId(convId);
                    } else {
                        getToUserIdByConvId(convId, secretType);
                    }

                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
                    contactDB_sqlite.updateMuteStatus(uniqueCurrentID, to, convId, status, option, notifyStatus, isSecretChat);


                } else {
                    String convId = objects.getString("convId");
                    to = convId;
                    /*String docId = uniqueCurrentID + "-" + convId + "-g";
                    session.setMuteDuration(docId, option);*/
                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
                    contactDB_sqlite.updateMuteStatus(uniqueCurrentID, null, convId, status, option, notifyStatus, false);

                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * get To User Id By ConvId
     *
     * @param convId     input value(convId)
     * @param secretType input value(secretType)
     */
    private void getToUserIdByConvId(String convId, String secretType) {
        try {
            JSONObject object = new JSONObject();
            object.put("from", uniqueCurrentID);
            object.put("convId", convId);
            object.put("secret_type", secretType);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_CONV_SETTINGS);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * getting ConvId Settings Message & updated session manager
     *
     * @param data input value(response from server data)
     */
    private void loadConvSettingsMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");
            if (from.equalsIgnoreCase(uniqueCurrentID)) {
                String to = object.getString("to_user_id");
                String convId = object.getString("convId");
                String secretType = object.getString("secret_type");

                String docId = from + "-" + to;
                if (secretType.equalsIgnoreCase("yes")) {
                    docId = docId + "-secret";
                }

                userInfoSession.updateChatConvId(docId, to, convId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * response for Verify email
     *
     * @param data input value(response from server data)
     */
    private void responseforVerifyemail(String data) {

        try {
            JSONObject objects = new JSONObject(data);
            String err = objects.getString("err");
            if (err.equals("0")) {
                String from = objects.getString("from");

                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    String verifyStatus = objects.getString("verify_email");
                    sessionManager.setChatLockEmailIdVerifyStatus(verifyStatus);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * response for event change email
     *
     * @param data input value(response from server data)
     */
    private void responseforeventchangemail(String data) {
        try {
            JSONObject objects = new JSONObject(data);
            String err = objects.getString("err");
            if (err.equals("0")) {
                String from = objects.getString("from");
                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    String email = objects.getString("email");
                    sessionManager.setUserEmailId(email);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * save Recovery Email
     *
     * @param data input value(response from server data)
     */
    private void saveRecoveryEmail(String data) {

        try {
            JSONObject objects = new JSONObject(data);
            String err = objects.getString("err");
            if (err.equals("0")) {
                String from = objects.getString("from");
                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    String recoveryEmail = objects.getString("recovery_email");
                    SessionManager.getInstance(this).setRecoveryEMailId(recoveryEmail);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * response for event Recovery Phone
     *
     * @param data input value(response from server data)
     */
    private void responseforeventRecoveryPhone(String data) {

        try {
            JSONObject objects = new JSONObject(data);
            String err = objects.getString("err");
            if (err.equals("0")) {
                String from = objects.getString("from");

                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    String recoveryPhone = objects.getString("recovery_phone");
                    sessionManager.setRecoveryPhoneNo(recoveryPhone);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Chat Lock Response
     *
     * @param data input value(response from server data)
     */
    private void loadChatLockResponse(String data) {
        try {
            JSONObject objects = new JSONObject(data);

            String err = objects.getString("err");
            String message = objects.getString("msg");
            String mode = objects.getString("mode");
            // TODO: 7/14/2017 get locked chat user ids on login
//            if (err.equals("0")) {

            String from = objects.getString("from");
            String password = objects.getString("password");
            String convId = objects.getString("convId");
            String status = objects.getString("status");
            String type = objects.getString("type");

            MessageDbController db = CoreController.getDBInstance(this);
            String receiverId = userInfoSession.getReceiverIdByConvId(convId);
            String docId, chatType;
            if (type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                chatType = MessageFactory.CHAT_TYPE_GROUP;
                docId = from.concat("-").concat(convId).concat("-g");
                receiverId = convId;
            } else {
                chatType = MessageFactory.CHAT_TYPE_SINGLE;
                docId = from.concat("-").concat(receiverId);
            }

            if (mode.equalsIgnoreCase("phone")) {
                password = objects.getString("mobile_password");
            } else {
                password = getEncryptPwd(password, convId);

                if (status.equalsIgnoreCase("1")) {
                    try {
                        JSONObject object = new JSONObject();
                        object.put("from", from);
                        object.put("type", type);
                        object.put("convId", convId);
                        object.put("mode", "phone");
                        object.put("mobile_password", password);

                        SendMessageEvent postEvent = new SendMessageEvent();
                        postEvent.setEventName(SocketManager.EVENT_CHAT_LOCK_FROM_WEB);
                        postEvent.setMessageObject(object);
                        EventBus.getDefault().post(postEvent);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            db.updateChatLockData(receiverId, docId, status, password, chatType);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * To Conv Settings & updated database
     *
     * @param data input value(response from server data)
     */
    public void loadToConvSettings(String data) {
        try {
            JSONObject object = new JSONObject(data);
            if (object.has("from")) {
                String from = object.getString("from");

                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    String status = object.getString("is_blocked");
                    String to = object.getString("to");

                    if (object.has("privacy")) {
                        updateToUserPrivacySettings(to, object);
                    }

                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
                    contactDB_sqlite.updateBlockedMineStatus(to, status, false);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * UnSet Device Token Message
     *
     * @param data input value(response from server data)
     */
    private void loadUnSetDeviceTokenMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");

            if (from.equalsIgnoreCase(uniqueCurrentID)) {
                notifyLogoutToServer();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete Account Message
     *
     * @param data input value(response from server data)
     */
    private void loadDeleteAccountMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");

            if (from.equalsIgnoreCase(uniqueCurrentID)) {
                performLogout();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove Account Message perform to logout
     *
     * @param data input value(response from server data)
     */
    private void loadRemoveAccountMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");

            if (from.equalsIgnoreCase(uniqueCurrentID)) {
                performLogout();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Remove Account Message perform to logout
     *
     * @param data input value(response from server data)
     */
    private void loadRemovedAccountMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String userId = object.getString("_id");
            if (userId.equalsIgnoreCase(uniqueCurrentID)) {
                performLogout();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Logout Old User Message
     *
     * @param data input value(response from server data)
     */
    private void loadLogoutOldUserMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String userId = object.getString("_id");
            String msidn = object.getString("msisdn");

            @SuppressLint("HardwareIds")
            String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            Log.d(TAG, "loadLogoutOldUserMessage" + userId + "---->" + uniqueCurrentID);
//            if (userId.equalsIgnoreCase(uniqueCurrentID)&&msidn.equalsIgnoreCase(deviceId)) {
            if (userId.equalsIgnoreCase(uniqueCurrentID) && msidn.equalsIgnoreCase(deviceId)) {
                performAdminLogout();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * notify Logout To Server (Web)
     */
    private void notifyLogoutToServer() {
        try {
            JSONObject logoutObj = new JSONObject();
            logoutObj.put("from", uniqueCurrentID);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_MOBILE_TO_WEB_LOGOUT);
            event.setMessageObject(logoutObj);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * load Online Status
     *
     * @param data input value(response from server data)
     */
    private void loadOnlineStatus(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            String id = (String) jsonObject.get("_id");
            String status = String.valueOf(jsonObject.get("Status"));
            if (id.equalsIgnoreCase(uniqueCurrentID) && status.equals("0")
                    && sessionManager.isScreenActivated()) {
                ChangeSetController.setChangeStatus("1");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Server Time Message
     *
     * @param data input value(response from server data)
     */
    private void loadServerTimeMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            if (object.has("from")) {
                String from = object.getString("from");
                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    if (object.has("server_time")) {
                        String strTime = object.getString("server_time");
                        try {
                            Long serverTime = Long.parseLong(strTime);
                            Long deviceTime = Calendar.getInstance().getTimeInMillis();
                            Long timeDiff = deviceTime - serverTime;
                            sessionManager.setServerTimeDifference(serverTime, timeDiff);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Privacy Setting (status, last seen, profile photo) & updated database
     *
     * @param event input value(response from server data)
     */
    private void loadPrivacySetting(String event) {
        try {
            JSONObject object = new JSONObject(event);
            String userid = object.getString("from");

            String statusVisibility = "";
            if (object.has("status")) {
                statusVisibility = object.getString("status");
            }

            String lastSeenVisibility = "";
            if (object.has("last_seen")) {
                lastSeenVisibility = object.getString("last_seen");
            }

            String profilePicVisibility = "";
            if (object.has("profile_photo")) {
                profilePicVisibility = object.getString("profile_photo");
            }

            if (userid.equalsIgnoreCase(uniqueCurrentID)) {
                sessionManager.setProfilePicVisibleTo(profilePicVisibility);
                sessionManager.setProfileStatusVisibleTo(statusVisibility);
                sessionManager.setLastSeenVisibleTo(lastSeenVisibility);
            } else {
                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(MessageService.this);
                String mineContactsStatus = "0";
                JSONArray contactUserList = object.getJSONArray("contactUserList");
                if (contactUserList != null && contactUserList.toString().contains(uniqueCurrentID)) {
                    mineContactsStatus = "1";
                }
                //new Db sqlite
                contactDB_sqlite.updateMyContactStatus(userid, mineContactsStatus);
                contactDB_sqlite.updateLastSeenVisibility(userid, getPrivacyStatus(lastSeenVisibility));
                contactDB_sqlite.updateProfilePicVisibility(userid, getPrivacyStatus(profilePicVisibility));
                contactDB_sqlite.updateProfileStatusVisibility(userid, getPrivacyStatus(statusVisibility));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get Privacy Status
     *
     * @param visibleTo based on type( my contact, everyone, nobody)
     * @return response value
     */
    private String getPrivacyStatus(String visibleTo) {
        switch (visibleTo.toLowerCase()) {

            case ContactDB_Sqlite.PRIVACY_TO_MY_CONTACTS:
                return ContactDB_Sqlite.PRIVACY_STATUS_MY_CONTACTS;

            case ContactDB_Sqlite.PRIVACY_TO_NOBODY:
                return ContactDB_Sqlite.PRIVACY_STATUS_NOBODY;

            default:
                return ContactDB_Sqlite.PRIVACY_STATUS_EVERYONE;
        }
    }

    /**
     * Message History
     *
     * @param data input value(response from server data)
     */
    private void getMessageHistory(String data) {
        try {
            JSONObject object = new JSONObject(data);

            String from = object.getString("id");
            String mode = "";
            if (object.has("mode"))
                mode = object.getString("mode");
            if (from.equalsIgnoreCase(uniqueCurrentID) && mode.equalsIgnoreCase("phone")) {
                // Avoid multiple hits in off-line message to server
                Calendar calendar = Calendar.getInstance();
                long currentTime = calendar.getTimeInMillis();
                long timeDiff = currentTime - session.getLastOfflineHistoryAt();
                if (timeDiff > MIN_GET_OFFLINE_MESSAGES_TIME) {
                    getStatusHistory(uniqueCurrentID);
                    getHistory();
                    getGroupHistory();

                    session.setLastOfflineHistoryAt(currentTime);
                }

                //----------Delete Chat---------------
                sendSingleOffline();
                sendGroupOffline();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message Offline based on chat type
     *
     * @param data input value(response from server data)
     */
    private void loadMessageOfflineAcks(String data) {

        try {
            JSONObject object = new JSONObject(data);
            String err = object.getString("err");

            if (err.equals("0")) {

                String chatType = object.getString("type");
                JSONArray arrMessages = object.getJSONArray("messageDetails");
                if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_SINGLE)) {
                    updateSingleMessageOfflineAcks(arrMessages);
                } else if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                    updateGroupMessageOfflineAcks(arrMessages);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * update Group Message Offline
     *
     * @param arrMessages input value(response from server data)
     */
    private void updateGroupMessageOfflineAcks(JSONArray arrMessages) {

        try {
            MessageDbController db = CoreController.getDBInstance(this);

            for (int i = 0; i < arrMessages.length(); i++) {
                JSONObject msgObj = arrMessages.getJSONObject(i);

                String from = msgObj.getString("from");
                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    String msgId = msgObj.getString("msgId");
                    String chatId = msgObj.getString("doc_id");
                    String timeStamp = msgObj.getString("timestamp");

                    JSONArray arrMsgStatus = msgObj.getJSONArray("message_status");

                    for (int j = 0; j < arrMsgStatus.length(); j++) {
                        JSONObject statusObj = arrMsgStatus.getJSONObject(j);
                        String ackUserId = statusObj.getString("id");
                        String deliverStatus = statusObj.getString("status");
                        String deliverTime = statusObj.getString("time_to_deliever");
                        String readTime = statusObj.getString("time_to_seen");

                        String groupId = chatId.split("-")[1];
                        String docId = from.concat("-").concat(groupId).concat("-g");

                        if (deliverStatus.equalsIgnoreCase(MessageFactory.GROUP_MSG_READ_ACK)) {
                            db.updateGroupMessageStatus(docId, chatId, MessageFactory.GROUP_MSG_DELIVER_ACK,
                                    deliverTime, ackUserId, uniqueCurrentID);
                            db.updateGroupMessageStatus(docId, chatId, deliverStatus, readTime,
                                    ackUserId, uniqueCurrentID);
                        } else {
                            synchronized (this) {
                                db.updateGroupMessageStatus(docId, chatId, deliverStatus, deliverTime,
                                        ackUserId, uniqueCurrentID);
                            }

                        }
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * update Single Message Offline
     *
     * @param arrMessages input value(response from server data)
     */
    private void updateSingleMessageOfflineAcks(JSONArray arrMessages) {
        MessageDbController db = CoreController.getDBInstance(this);

        try {
            for (int i = 0; i < arrMessages.length(); i++) {
                JSONObject msgObj = arrMessages.getJSONObject(i);
                if (msgObj.has("from")) {
                    String from = msgObj.getString("from");
                    if (from.equalsIgnoreCase(uniqueCurrentID)) {
                        String to = msgObj.getString("to");
                        String msgId = msgObj.getString("msgId");
                        String chatId = msgObj.getString("doc_id");
                        String timeStamp = msgObj.getString("timestamp");
                        String deliverStatus = msgObj.getString("message_status");
                        String deliverTime = msgObj.getString("time_to_deliever");
                        String readTime = msgObj.getString("time_to_seen");

                        String docId = from.concat("-").concat(to);
                        if (deliverStatus.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
                            db.updateChatMessage(docId, chatId, MessageFactory.DELIVERY_STATUS_DELIVERED, deliverTime, true);
                            db.updateChatMessage(docId, chatId, deliverStatus, readTime, true);
                        } else {
                            db.updateChatMessage(docId, chatId, MessageFactory.DELIVERY_STATUS_DELIVERED, deliverTime, true);
                        }

                    }
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * load archive based on chat type
     *
     * @param data input value(response from server data)
     */
    private void loadarchive(String data) {
        try {
            JSONObject object = new JSONObject(data);

            try {
                String from = object.getString("from");
                String convId = object.getString("convId");
                String type = object.getString("type");
                int status = object.getInt("status");

                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    String receiverId;
                    if (type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                        receiverId = convId;
                    } else {
                        receiverId = userInfoSession.getReceiverIdByConvId(convId);
                    }

                    if (!receiverId.equals("")) {
                        String docId = from.concat("-").concat(receiverId);
                        if (type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                            docId = docId.concat("-g");
                            if (status == 1) {
                                if (!session.getarchivegroup(uniqueCurrentID + "-" + receiverId + "-g")) {
                                    session.putarchivegroup(uniqueCurrentID + "-" + receiverId + "-g");
                                }
                            } else {
                                if (session.getarchivegroup(uniqueCurrentID + "-" + receiverId + "-g")) {
                                    session.removearchivegroup(uniqueCurrentID + "-" + receiverId + "-g");
                                }

                            }
                        } else {
                            if (status == 1) {
                                if (!session.getarchive(uniqueCurrentID + "-" + receiverId)) {
                                    session.putarchive(uniqueCurrentID + "-" + receiverId);
                                }

                            } else {
                                if (session.getarchive(uniqueCurrentID + "-" + receiverId)) {
                                    session.removearchive(uniqueCurrentID + "-" + receiverId);
                                }

                            }
                        }


                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * ClearChat based on chat type (group / single) & updated database
     *
     * @param data input value(response from server data)
     */
    private void loadClearChat(String data) {
        try {
            JSONObject object = new JSONObject(data);

            try {
                int star_status;
                Boolean starred = false;
                String from = object.getString("from");
                String convId = object.getString("convId");
                String type = object.getString("type");
                if (object.has("star_status")) {
                    star_status = object.getInt("star_status");
                    starred = star_status != 0;
                }
                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    String receiverId;
                    if (type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                        receiverId = convId;
                    } else {
                        receiverId = userInfoSession.getReceiverIdByConvId(convId);
                    }

                    if (!receiverId.equals("")) {
                        String docId = from.concat("-").concat(receiverId);
                        if (type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                            docId = docId.concat("-g");
                        }
                        if (type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                            MessageDbController db = CoreController.getDBInstance(this);
                            if (starred) {
                                db.clearUnStarredMessage(docId, receiverId, MessageFactory.CHAT_TYPE_GROUP);
                            } else {
                                db.clearAllGroupChatMessage(docId, receiverId);
                            }
                        } else {
                            MessageDbController db = CoreController.getDBInstance(this);
                            if (starred) {
                                db.clearUnStarredMessage(docId, receiverId, MessageFactory.CHAT_TYPE_SINGLE);
                            } else {
                                db.clearAllSingleChatMessage(docId, receiverId);
                            }

                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * load Delete Chat based on chat type (group / single) & updated database
     *
     * @param data input value(response from server data)
     */
    private void loadDeleteChat(String data) {
        try {
            JSONObject object = new JSONObject(data);

            try {
                String from = object.getString("from");
                String to = object.getString("to");
                String convId = object.getString("convId");
                String type = object.getString("type");

                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    String receiverId;
                    String chatType = MessageFactory.CHAT_TYPE_SINGLE;
                    if (type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                        receiverId = convId;
                        chatType = MessageFactory.CHAT_TYPE_GROUP;
                    } else {
                        receiverId = userInfoSession.getReceiverIdByConvId(convId);
                    }
                    receiverId = from;

                    if (!receiverId.equals("")) {
//                        String docId = from.concat("-").concat(receiverId);
                        String docId = from.concat("-").concat(to);
                        if (type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                            docId = docId.concat("-g");
                        }

                        MessageDbController db = CoreController.getDBInstance(this);
                        db.deleteChat(docId, chatType);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * change Message Viewed Status
     *
     * @param data input value(response from server data)
     */
    private void changeMsgViewedStatus(String data) {

        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");
            String mode = object.getString("mode");

            if (from.equalsIgnoreCase(uniqueCurrentID) && mode.equalsIgnoreCase("web")) {
                String to = object.getString("to");
                String chatType = object.getString("type");
                String convId = object.getString("convId");

                /*String docId;
                if (chatType.equalsIgnoreCase("group")) {
                    docId = from + to + "-g";
                } else {
                    docId = from + to;
                }*/

                ShortcutBadgeManager shortcutBadgeMgnr = new ShortcutBadgeManager(MessageService.this);
                shortcutBadgeMgnr.removeMessageCount(convId);
                int totalCount = shortcutBadgeMgnr.getTotalCount();

                session.Removemark(to);

                // Badge working if supported devices
                try {
                    ShortcutBadger.applyCountOrThrow(MessageService.this, totalCount);
                } catch (ShortcutBadgeException e) {
                    e.printStackTrace();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * perform Logout for aChat & remove all data from database
     */
    private void performLogout() {
        MessageDbController db = CoreController.getDBInstance(this);
        db.deleteDatabase();

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        contactDB_sqlite.deleteDatabase();

        notifyLogoutToServer();
        ChangeSetController.setChangeStatus("0");
        SessionManager.getInstance(MessageService.this).logoutUser(false);
    }

    /**
     * perform Admin Logout
     */
    private void performAdminLogout() {

        clearSharedPreferences(mcontext, "");
        clearCache();
        ShortcutBadgeManager shortcutBadgeManager = new ShortcutBadgeManager(MessageService.this);
        shortcutBadgeManager.clearBadgeCount();

        try {
            ShortcutBadger.applyCountOrThrow(MessageService.this, 0);
        } catch (ShortcutBadgeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent svcIntent = new Intent(MessageService.this, MessageService.class);
        stopService(svcIntent);

        // After logout redirect user to Loing Activity
        Intent homeIntent = new Intent(MessageService.this, ReLoadingActivityNew.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        startActivity(homeIntent);
//        System.exit(0);

    }

    /**
     * clear SharedPreferences
     *
     * @param ctx current activity
     * @param Id  Input value(ID)
     */
    public void clearSharedPreferences(Context ctx, String Id) {
        try {
            File dir = new File(ctx.getFilesDir().getParent() + "/shared_prefs/");
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                // clear each preference file
                ctx.getSharedPreferences(children[i].replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().apply();
                //delete the file
                new File(dir, children[i]).delete();
            }
            SessionManager.getInstance(MessageService.this).setClearLocalDataStatus1("1");
            if (!TextUtils.isEmpty(Id)) {
                SessionManager.getInstance(MessageService.this).setCurrentUserID(Id);
                SessionManager.getInstance(MessageService.this).setAdminPendingStatus("1");
            }

        } catch (Exception e) {
            Log.d(TAG, "clearSharedPreferences" + "Exception");

            e.printStackTrace();
        }

    }

    /**
     * clear Cache for database & session
     */
    private void clearCache() {
        try {
            MessageDbController msgDb = CoreController.getDBInstance(MessageService.this);
            msgDb.deleteDatabase();

            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(MessageService.this);
            contactDB_sqlite.deleteDatabase();

            UserInfoSession userInfoSession = new UserInfoSession(MessageService.this);
            userInfoSession.clearData();

            Session session = new Session(MessageService.this);
            session.clearData();
            SessionManager.getInstance(MessageService.this).clearData();
            /*Intent intent=new Intent(MessageService.this,ReLoadingActivityNew.class);
            startActivity(intent);*/

        } catch (Exception e) {
            Log.d(TAG, "clearCache" + "Exception");
            e.printStackTrace();
        }


    }

    /**
     * Profile Name Change Message
     *
     * @param response input value(response from server data)
     */
    private void loadProfileNameChangeMessage(String response) {
        try {
            JSONObject object = new JSONObject(response);
            String err = object.getString("err");

            if (err.equals("0")) {
                String from = object.getString("from");

                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    String name = object.getString("name");
                    byte[] data = Base64.decode(name, Base64.DEFAULT);

                    try {
                        name = new String(data, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    SessionManager.getInstance(MessageService.this).setnameOfCurrentUser(name);
                }
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Message Update delivery status and updated database
     *
     * @param data input value(response from server data)
     */
    private void loadMessageStatusUpdate(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            Log.e("status_update", jsonObject.toString());
            String from = jsonObject.getString("from");
            String to = jsonObject.getString("to");
            String msgIds = jsonObject.getString("msgIds");
            String chatId = jsonObject.getString("doc_id");
            String status = jsonObject.getString("status");
            String secretType = jsonObject.getString("secret_type");
            String timeStamp = jsonObject.getString("currenttime");

            MessageDbController db = CoreController.getDBInstance(this);
            String docId = to + "-" + from;
            if (secretType.equalsIgnoreCase("yes")) {
                docId = docId + "-" + MessageFactory.CHAT_TYPE_SECRET;
            }
            if (status.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
                if (SessionManager.getInstance(MessageService.this).canSendReadReceipt()) {
                    db.updateChatMessage(docId, chatId, status, timeStamp, true);
                }
            } else {
                db.updateChatMessage(docId, chatId, status, timeStamp, true);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Profile Status Change Message
     *
     * @param data input value(response from server data)
     */
    private void loadProfileStatusChangeMessage(String data) {

        try {
            JSONObject object = new JSONObject(data);

            String err = object.getString("err");

            if (err.equalsIgnoreCase("0")) {
                String from = object.getString("from");
                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    String status = object.getString("status");

                    try {
                        byte[] decodeStatus = Base64.decode(status, Base64.DEFAULT);
                        status = new String(decodeStatus, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    sessionManager.setcurrentUserstatus(status);
                    if (!statusDB.isAlreadyInsertedStatus(status)) {
                        statusDB.insetData(status);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Profile Picture Change Message & updated database
     *
     * @param data input value(response from server data)
     */
    private void loadProfilePicChangeMessage(String data) {

        try {
            JSONObject objects = new JSONObject(data);
            String err = objects.getString("err");

            if (err.equalsIgnoreCase("0")) {
                String message = objects.getString("message");
                String from = objects.getString("from");
                String type = objects.getString("type");

                if (from.equalsIgnoreCase(uniqueCurrentID)
                        && type.equalsIgnoreCase("single")) {
                    if (objects.has("removePhoto")) {
                        String removePhoto = objects.getString("removePhoto");
                        if (removePhoto.equalsIgnoreCase("yes")) {
                            sessionManager.setUserProfilePic("");
                        } else {
                            String path = objects.getString("file") + "?id=" + Calendar.getInstance().getTimeInMillis();
                            sessionManager.setUserProfilePic(path);
                        }
                    } else {
                        String path = objects.getString("file") + "?id=" + Calendar.getInstance().getTimeInMillis();
                        sessionManager.setUserProfilePic(path);
                    }
                } else if (type.equalsIgnoreCase("single")) {
                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);

                    ChatappContactModel contactModel = contactDB_sqlite.getUserOpponenetDetails(from);


                    if (contactModel != null && objects.has("profile_changed_time")) {
                        contactDB_sqlite.updateDpUpdatedTime(from, objects.getString("profile_changed_time"));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Phone Data Received Message
     *
     * @param data input value(response from server data)
     */
    private void loadPhoneDataReceivedMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);

            int err = object.getInt("err");
            if (err == 0) {
                Log.d("UploadProgress", data);
                uploadDownloadManager.startServerRequestFileUpload(EventBus.getDefault(), object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Phone Download Message
     *
     * @param data input value(response from server data)
     */
    private void loadPhoneDownloadMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);

            int err = object.getInt("err");
            if (err == 0) {
                object.put("bufferAt", -1); // For index from 0
                uploadDownloadManager.startServerRequestFileUpload(EventBus.getDefault(), object);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * validate Device With Account (login key)
     */
    private void validateDeviceWithAccount() {
        try {
            JSONObject msgObj = new JSONObject();
            msgObj.put("from", uniqueCurrentID);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_CHECK_MOBILE_LOGIN_KEY);
            event.setMessageObject(msgObj);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Check LoginKey
     *
     * @param data input value(response from server data)
     */
    private void loadCheckLoginKey(String data) {
        try {
            JSONObject object = new JSONObject(data);

            String err = object.getString("err");
            if (err.equalsIgnoreCase("0")) {
                String msg = object.getString("msg");

                JSONObject apiObj = object.getJSONObject("apiMobileKeys");
                String deviceId = apiObj.getString("DeviceId");
                String loginKey = apiObj.getString("login_key");
                String timeStamp = apiObj.getString("timestamp");

                /*String settingsDeviceId = Settings.Secure.getString(getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                if (settingsDeviceId.equalsIgnoreCase(deviceId)) {
                    sessionManager.setLoginKeySent(true);
                }*/

                String deviceLoginKey = sessionManager.getLoginKey();

                if (!loginKey.equalsIgnoreCase(deviceLoginKey)) {
//                    sessionManager.setIsValidDevice(false);
                    sessionManager.setIsValidDevice(true);
                } else {
                    getServerTime();
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * get ServerTime
     */
    private void getServerTime() {
        try {
            JSONObject timeObj = new JSONObject();
            timeObj.put("from", uniqueCurrentID);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_GET_SERVER_TIME);
            event.setMessageObject(timeObj);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Device Login Message
     *
     * @param data input value(response from server data)
     */
    private void loadDeviceLoginMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);

            String err = object.getString("err");
            if (err.equalsIgnoreCase("0")) {
                String msg = object.getString("msg");

                JSONObject apiObj = object.getJSONObject("apiMobileKeys");
                String deviceId = apiObj.getString("DeviceId");
                String loginKey = apiObj.getString("login_key");
                String timeStamp = apiObj.getString("timestamp");

                String settingsDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                if (settingsDeviceId.equalsIgnoreCase(deviceId)) {
                    sessionManager.setLoginKeySent(true);
                    sessionManager.setIsValidDevice(true);
                    sessionManager.setLoginKey(loginKey);
                    getServerTime();
                } else {
//                    sessionManager.setIsValidDevice(false);
                    sessionManager.setIsValidDevice(true);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * save User Details (message status and updated database)
     *
     * @param data input value(response from server data)
     */
    private void saveUserDetails(String data) {

        try {
            JSONObject object = new JSONObject(data);

            String userId = object.getString("id");
            String avatar = object.getString("avatar");
            String msisdn = object.getString("msisdn");
            String name = "";
            String status = "";
            if (object.has("Name")) {
                name = object.getString("Name");
                try {
                    byte[] nameBuffer = Base64.decode(name, Base64.DEFAULT);
                    name = new String(nameBuffer, "UTF-8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (object.has("Status")) {
                status = object.getString("Status");
                try {
                    byte[] nameBuffer = Base64.decode(status, Base64.DEFAULT);
                    status = new String(nameBuffer, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                status = getResources().getString(R.string.default_user_status);
            }

            String dpChangedTS = "0";
            if (object.has("profile_changed_time")) {
                dpChangedTS = object.getString("profile_changed_time");
            }

            if (userId.equalsIgnoreCase(uniqueCurrentID)) {
                JSONObject objPrivacy = object.getJSONObject("privacy");

                if (objPrivacy.has("status")) {
                    String statusVisibleTo = objPrivacy.getString("status");
                    sessionManager.setProfileStatusVisibleTo(statusVisibleTo);
                }

                if (objPrivacy.has("last_seen")) {
                    String lastSeenVisibleTo = objPrivacy.getString("last_seen");
                    sessionManager.setLastSeenVisibleTo(lastSeenVisibleTo);
                }

                if (objPrivacy.has("profile_photo")) {
                    String profilePhotoVisibleTo = objPrivacy.getString("profile_photo");
                    sessionManager.setProfilePicVisibleTo(profilePhotoVisibleTo);
                }

                sessionManager.setUserDetailsReceived(true);
            } else {
                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
                ChatappContactModel contact = contactDB_sqlite.getUserOpponenetDetails(userId);
                if (contact == null) {
                    contact = new ChatappContactModel();
                }
                contact.set_id(userId);
                contact.setStatus(status);
                contact.setAvatarImageUrl(avatar);
                contact.setMsisdn(msisdn);
                String requestStatus;
                /*if (object.has("RequestStatus")) {
                    requestStatus = object.getString("RequestStatus");
                } else {
                    requestStatus = contactDB_sqlite.getUserRequestStatusDetails(userId);
                }*/
//                contact.setRequestStatus(requestStatus);
                contact.setRequestStatus("3");

               /* if (sessionManager.getContactSavedRevision() > contactDB_sqlite.getOpponenet_UserDetails_savedRevision(userId)) {

                    contact.setFirstName(name);
                }*/


                contact.setFirstName(name);


                contactDB_sqlite.updateUserDetails(userId, contact);
                contactDB_sqlite.updateDpUpdatedTime(userId, dpChangedTS);

                updateToUserPrivacySettings(userId, object);

                if (receivedUserDetailsMap == null) {
                    receivedUserDetailsMap = new HashMap<>();
                }
                receivedUserDetailsMap.put(userId, Calendar.getInstance().getTimeInMillis());
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * update To User Privacy Settings (my contact, nobody, everyone)
     *
     * @param userId input value(userId)
     * @param object input value(response from server data)
     */
    private void updateToUserPrivacySettings(String userId, JSONObject object) {
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);

        try {
            JSONObject objPrivacy = object.getJSONObject("privacy");

            if (objPrivacy.has("status")) {
                String statusVisibleTo = objPrivacy.getString("status").toLowerCase();
                switch (statusVisibleTo) {

                    case ContactDB_Sqlite.PRIVACY_TO_NOBODY:
                        contactDB_sqlite.updateProfileStatusVisibility(userId, ContactDB_Sqlite.PRIVACY_STATUS_NOBODY);
                        break;

                    case ContactDB_Sqlite.PRIVACY_TO_MY_CONTACTS:
                        contactDB_sqlite.updateProfileStatusVisibility(userId, ContactDB_Sqlite.PRIVACY_STATUS_MY_CONTACTS);
                        break;

                    default:
                        contactDB_sqlite.updateProfileStatusVisibility(userId, ContactDB_Sqlite.PRIVACY_STATUS_EVERYONE);
                        break;
                }
            }

            if (objPrivacy.has("last_seen")) {
                String lastSeenVisibleTo = objPrivacy.getString("last_seen");
                switch (lastSeenVisibleTo) {

                    case ContactDB_Sqlite.PRIVACY_TO_NOBODY:
                        contactDB_sqlite.updateLastSeenVisibility(userId, ContactDB_Sqlite.PRIVACY_STATUS_NOBODY);
                        break;

                    case ContactDB_Sqlite.PRIVACY_TO_MY_CONTACTS:
                        contactDB_sqlite.updateLastSeenVisibility(userId, ContactDB_Sqlite.PRIVACY_STATUS_MY_CONTACTS);
                        break;

                    default:
                        contactDB_sqlite.updateLastSeenVisibility(userId, ContactDB_Sqlite.PRIVACY_STATUS_EVERYONE);
                        break;
                }
            }

            if (objPrivacy.has("profile_photo")) {
                String profilePhotoVisibleTo = objPrivacy.getString("profile_photo");
                switch (profilePhotoVisibleTo) {

                    case ContactDB_Sqlite.PRIVACY_TO_NOBODY:
                        contactDB_sqlite.updateProfilePicVisibility(userId, ContactDB_Sqlite.PRIVACY_STATUS_NOBODY);
                        break;

                    case ContactDB_Sqlite.PRIVACY_TO_MY_CONTACTS:
                        contactDB_sqlite.updateProfilePicVisibility(userId, ContactDB_Sqlite.PRIVACY_STATUS_MY_CONTACTS);
                        break;

                    default:
                        contactDB_sqlite.updateProfilePicVisibility(userId, ContactDB_Sqlite.PRIVACY_STATUS_EVERYONE);
                        break;
                }
            }

            if (object.has("is_contact_user")) {
                String myContactStatus = object.getString("is_contact_user");
                contactDB_sqlite.updateMyContactStatus(userId, myContactStatus);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * perform Settings Connect (call status)
     *
     * @param object input value(response from server data)
     */
    private void performSettingsConnect(JSONObject object) {
        Log.d("GetmobileCalled", object.toString());
        try {
            String from = object.getString("requestUser");

            if (from.equalsIgnoreCase(uniqueCurrentID)) {
                int send = object.getInt("send");

                if (send == 0) {
                    object.put("send", 1);

                    if (IncomingCallActivity.isStarted || CallMessage.isAlreadyCallClick) {
                        object.put("call_connect", MessageFactory.CALL_IN_RINGING);
                    } else if (!IncomingCallActivity.isStarted && !CallsActivity.isStarted) {
                        object.put("call_connect", MessageFactory.CALL_IN_FREE);
                    } else {
                        object.put("call_connect", MessageFactory.CALL_IN_WAITING);
                    }

                    SendMessageEvent event = new SendMessageEvent();
                    event.setEventName(SocketManager.EVENT_GET_MOBILE_SETTINGS);
                    event.setMessageObject(object);
                    EventBus.getDefault().post(event);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Message Reciecved based on message type & updated database
     *
     * @param respose input value(response from server data)
     */
    private void loadMessageRes(Object[] respose) {

        try {
            JSONObject objects = new JSONObject(respose[0].toString());
            Log.e("loadMessageRes1", objects.toString());
            int errorState = objects.getInt("err");
            if (errorState == 0) {
                int delivery = (int) objects.get("deliver");
                String id = objects.getString("id");

                JSONObject msgData = objects.getJSONObject("data");
                String recordId = msgData.getString("recordId");
                String convId = msgData.getString("convId");
                String fromUserId = msgData.getString("from");
                String toUserId = msgData.getString("to");
                String type = msgData.getString("type");
                String timeStamp = msgData.getString("timestamp");

                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(MessageService.this);

                if (type.equalsIgnoreCase(MessageFactory.text + "")) {
                    String message = msgData.getString("message");
                    long messagelength = session.getsentmessagelength() + message.length();
                    session.putsentmessagelength(messagelength);
                    int sentmesagecount = session.getsentmessagecount();
                    sentmesagecount = sentmesagecount + 1;
                    session.putsentmessagecount(sentmesagecount);
                } else if (type.equalsIgnoreCase(MessageFactory.picture + "") || type.equalsIgnoreCase(MessageFactory.audio + "") || type.equalsIgnoreCase(MessageFactory.video + "")) {
                    long filesize = Long.parseLong(msgData.getString("filesize"));
                    filesize = session.getsentmedialength() + filesize;
                    session.putsentmedialength(filesize);
                }

                sessionManager.setIsFirstMessage(toUserId, true);

                MessageDbController db = CoreController.getDBInstance(this);

                String secretType = msgData.getString("secret_type");
                String chat_id = (String) objects.get("doc_id");
                String[] ids = chat_id.split("-");
                String doc_id = ids[0] + "-" + ids[1];

                String chatType = MessageFactory.CHAT_TYPE_SINGLE;
                if (secretType.equalsIgnoreCase("yes")) {
                    doc_id = doc_id.concat("-").concat(MessageFactory.CHAT_TYPE_SECRET);
                    chatType = MessageFactory.CHAT_TYPE_SECRET;
                } else {
                    contactDB_sqlite.updateFrequentContact(uniqueCurrentID, convId, timeStamp);
                }

                MessageItemChat item = db.updateChatMessage(doc_id, chat_id, "" + delivery,
                        recordId, convId, timeStamp);
                db.deleteSendNewMessage(chat_id);

                if (!userInfoSession.hasChatConvId(doc_id)) {
                    userInfoSession.updateChatConvId(doc_id, ids[1], convId);
                }

                // Add message to local DB from Web
                if (item == null && fromUserId.equalsIgnoreCase(uniqueCurrentID)) {
                    item = incomingMsg.loadSingleMessageFromWeb(objects);
                    if (item != null) {
                        db.updateChatMessage(item, chatType);
                    }
                }
            } /*else if (errorState == 1) {
                SessionManager.getInstance(MessageService.this).logoutUser();
            }*/

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * store Group Message In DataBase & delete chat (Single / group)
     *
     * @param response input value(response from server data)
     */
    // Store Group message to local DB
    private void storeGroupMsgInDataBase(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());
            String to;
            if (objects.has("to")) {
                to = objects.getString("to");
            } else {
                to = objects.getString("groupId");
            }

            String docId = uniqueCurrentID.concat("-").concat(to).concat("-g");
            objectReceiver.storeGroupMsgInDataBase(response);
            if (objects.has("payload")) {

                String stat = "", pwd = "";
                ChatLockPojo lockPojo = getChatLockdetailfromDB(docId, MessageFactory.CHAT_TYPE_GROUP);
                if (sessionManager.getLockChatEnabled().equals("1") && lockPojo != null) {
                    stat = lockPojo.getStatus();
                    pwd = lockPojo.getPassword();
                }

                if (!stat.equalsIgnoreCase("1")) {
                    if (!ChatViewActivity.isChatPage) {
                        CustomshowNotification(objects, false, false);
                    } else {
                        if (!session.gettoid().equalsIgnoreCase(docId)) {
                            CustomshowNotification(objects, false, false);
                        }

                    }
                }


                //-------------Delete Chat----------------
                int group_type, is_deleted;
                String new_msgId, groupId;
                if (objects.has("groupType")) {
                    group_type = objects.getInt("groupType");
                    if (group_type == 9) {
                        if (objects.has("is_deleted_everyone")) {
                            is_deleted = objects.getInt("is_deleted_everyone");
                            if (is_deleted == 1) {
                                try {
                                    MessageDbController db = CoreController.getDBInstance(this);
                                    String fromId = objects.getString("from");

                                    if (!fromId.equalsIgnoreCase(uniqueCurrentID)) {
                                        String chat_id = (String) objects.get("toDocOId");
                                        String[] ids = chat_id.split("-");

                                        groupId = objects.getString("groupId");
                                        new_msgId = uniqueCurrentID + "-g-" + ids[1] + "-g-" + ids[3];
                                        String groupAndMsgId = ids[1] + "-g-" + ids[3];

                                        if (ChatViewActivity.Chat_Activity == null) {
                                            db.deleteSingleMessage(groupAndMsgId, new_msgId, "group", "other");
                                            db.deleteChatListPage(groupAndMsgId, new_msgId, "group", "other");
                                        } else if (!ChatViewActivity.Activity_GroupId.equalsIgnoreCase(groupId)) {
                                            db.deleteSingleMessage(groupAndMsgId, new_msgId, "group", "other");
                                            db.deleteChatListPage(groupAndMsgId, new_msgId, "group", "other");
                                        }

                                    }
                                } catch (JSONException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        }
                    }
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * handle Group Response
     *
     * @param response input value(response from server data)
     */
    // Handle Group Event response for all group event
    private void handleGroupResponse(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());
            String groupAction = objects.getString("groupType");

            if (objects.has("from")) {
                String from = objects.getString("from");
                if (!from.equalsIgnoreCase(uniqueCurrentID)) {
                    getUserDetails(from);
                }
            }

            if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EVENT_GROUP_MESSAGE)) {
                storeGroupMsgInDataBase(response);
            } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_JOIN_NEW_GROUP)) {
                storeGroupMsgInDataBase(response);
            } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_NEW_GROUP)) {
                addNewGroupInDataBase(response);
            } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_ACK_GROUP_MESSAGE)) {
                updateGroupMsgStatus(response);
            } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EXIT_GROUP)) {
                performExitGroup(response);
            } else if (groupAction.equals(SocketManager.ACTION_CHANGE_GROUP_NAME)) {
                performChangeGroupName(response);
            } else if (groupAction.equals(SocketManager.ACTION_CHANGE_GROUP_DP)) {
                performGroupChangeDp(response);
            } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_DELETE_GROUP_MEMBER)) {
                performDeleteMemberMessage(response);
            } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_MAKE_GROUP_ADMIN)) {
                performAddAdminMessage(response);
            } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_ADD_GROUP_MEMBER)) {
                performAddMemberMessage(response);
            } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EVENT_GROUP_MSG_DELETE)) {
                deleteGroupMessage(response);
            } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EVENT_GROUP_OFFLINE)) {
                getGroupOffline(response);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * perform Add Admin Message
     *
     * @param response input value(response from server data)
     */
    private void performAddAdminMessage(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());
            objectReceiver.loadMakeAdminMessage(objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * perform Add Member Message
     *
     * @param response input value(response from server data)
     */
    private void performAddMemberMessage(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());
            objectReceiver.loadAddMemberMessage(objects);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * perform Delete Member Message
     *
     * @param response input value(response from server data)
     */
    private void performDeleteMemberMessage(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());
            objectReceiver.loadDeleteMemberMessage(objects);

            try {
                String groupId = objects.getString("groupId");
                String removeId = objects.getString("removeId");

                if (removeId.equalsIgnoreCase(uniqueCurrentID)) {
//                    removeUser(groupId);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * perform Group Change Dp
     *
     * @param response input value(response from server data)
     */
    private void performGroupChangeDp(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());
            objectReceiver.loadGroupDpChangeMessage(objects);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * perform Change GroupName
     *
     * @param response input value(response from server data)
     */
    private void performChangeGroupName(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());
//            String groupId = objects.getString("groupId");

            objectReceiver.loadChangeGroupNameMessage(objects);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * perform Exit Group
     *
     * @param response input value(response from server data)
     */
    private void performExitGroup(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());
            String groupId = objects.getString("groupId");

//            removeUser(groupId);
            objectReceiver.loadExitMessage(objects);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * update Group Message Status
     *
     * @param response input value(response from server data)
     */
    private void updateGroupMsgStatus(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());

            objectReceiver.updateGroupMsgStatus(objects);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * add New Group In DataBase
     *
     * @param response input value(response from server data)
     */
    private void addNewGroupInDataBase(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());
            objectReceiver.addNewGroupData(objects);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadDeleteMessage(Object[] data) {

        try {
            JSONObject objects = new JSONObject(data[0].toString());
            int errorState = objects.getInt("err");
            if (errorState == 0) {
                String fromId = objects.getString("from");

                if (fromId.equalsIgnoreCase(uniqueCurrentID)) {
                    String recordId = objects.getString("recordId");
                    String chat_id = objects.getString("doc_id");
                    String deleteStatus = objects.getString("status");

                    String[] ids = chat_id.split("-");

                    String docId;
                    String msgId, chatType = MessageFactory.CHAT_TYPE_SINGLE;
                    if (chat_id.contains("-g-")) {
                        docId = fromId + "-" + ids[1] + "-g";
                        msgId = docId + "-" + ids[3];
                        chatType = MessageFactory.CHAT_TYPE_GROUP;
                    } else {
                        if (fromId.equalsIgnoreCase(ids[0])) {
                            docId = ids[0] + "-" + ids[1];
                        } else {
                            docId = ids[1] + "-" + ids[0];
                        }
                        msgId = docId + "-" + ids[2];
                    }

                    if (deleteStatus.equalsIgnoreCase("1")) {
                        MessageDbController db = CoreController.getDBInstance(this);
                        db.deleteChatMessage(docId, msgId, chatType);
                        db.deleteTempDeletedMessage(recordId, chatType);
                    }
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * load Starred Message based on chat type(Single / group)
     *
     * @param data input value(response from server data)
     */
    private void loadStarredMessage(Object[] data) {

        try {
            JSONObject objects = new JSONObject(data[0].toString());
            int errorState = objects.getInt("err");
            if (errorState == 0) {
                String fromId = objects.getString("from");
                if (fromId.equalsIgnoreCase(uniqueCurrentID)) {
                    String chat_id = (String) objects.get("doc_id");
                    String[] ids = chat_id.split("-");

                    String docId;
                    String chatType = MessageFactory.CHAT_TYPE_SINGLE;
                    if (fromId.equalsIgnoreCase(ids[0])) {
                        docId = ids[0] + "-" + ids[1];
                    } else {
                        docId = ids[1] + "-" + ids[0];
                    }

                    if (chat_id.contains("-g-")) {
                        docId = docId + "-g";
                        chatType = MessageFactory.CHAT_TYPE_GROUP;
                    }

                    String starred = objects.getString("status");
                    /*String msgId;
                    if(chat_id.contains("-g-")) {
                        msgId = docId + "-" + ids[3];
                    } else {
                        msgId = docId + "-" + ids[2];
                    }*/
                    String recordId = objects.getString("recordId");

                    MessageDbController db = CoreController.getDBInstance(this);
                    db.updateStarredMessage(chat_id, starred, chatType);
                    db.deleteTempStarredMessage(uniqueCurrentID, recordId);
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * store In DataBase (load message)
     *
     * @param response input value(response from server data)
     */
    private void storeInDataBase(Object[] response) {
        try {
            JSONObject object = (JSONObject) response[0];
            loadMessage(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * create User (setting event)
     */
    private void createUser() {

        try {
            if (!sessionManager.isAppSettingsReceived()) {
                getAppSettings();
            }

            SendMessageEvent messageEvent = new SendMessageEvent();
            messageEvent.setEventName(SocketManager.EVENT_CREATE_USER);
            JSONObject object = new JSONObject();

            object.put("_id", uniqueCurrentID);
            object.put("mode", "phone");
            object.put("chat_type", "single");
            if (Constants.IS_ENCRYPTION_ENABLED) {
                String securityToken = SessionManager.getInstance(getApplicationContext()).getSecurityToken();
                Log.d("createUser", "securityToken: " + securityToken);
                object.put("token", securityToken);
            }

            messageEvent.setMessageObject(object);
            EventBus.getDefault().post(messageEvent);

            SendMessageEvent settingsEvent = new SendMessageEvent();
            settingsEvent.setMessageObject(new JSONObject());
            settingsEvent.setEventName(SocketManager.EVENT_GET_SETTINGS);
            EventBus.getDefault().post(settingsEvent);

            if (sessionManager.isInitialGetGroupList()) {
                getGroupList();
            } else {
                List<String> liveGroupIds = groupInfoSession.getGroupIdList();
                for (String groupId : liveGroupIds) {
                    createGroup(groupId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * get AppSettings
     */
    private void getAppSettings() {
        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_GET_APP_SETTINGS);

        try {
            JSONObject object = new JSONObject();
            object.put("from", uniqueCurrentID);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * get GroupList
     */
    private void getGroupList() {
        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_GET_GROUP_LIST);

        try {
            JSONObject object = new JSONObject();
            object.put("from", uniqueCurrentID);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Group List Details & updated database
     *
     * @param data input value(response from server data)
     */
    private void loadGroupListDetails(String data) {

        try {
            JSONObject object = new JSONObject(data);

            JSONArray arrGroups = object.getJSONArray("GroupDetails");
            for (int i = 0; i < arrGroups.length(); i++) {
                JSONObject valObj = arrGroups.getJSONObject(i);
                String groupId = valObj.getString("groupId");
                String isDeleted = valObj.getString("isDeleted");

                if (isDeleted.equalsIgnoreCase("0")) {
                    String groupDocId = uniqueCurrentID.concat("-").concat(groupId).concat("-g");

                    boolean hasInfo = groupInfoSession.hasGroupInfo(groupDocId);
                    if (!hasInfo) {
                        MessageDbController db = CoreController.getDBInstance(this);
                        db.createNewGroupChatList(groupId, groupDocId);
                        getGroupDetails(groupId);
                    } else {
                        GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(groupDocId);
                        if (infoPojo.getGroupContactNames() == null) {
                            getGroupDetails(groupId);
                        }
                    }
                    createGroup(groupId);
                }
            }

            sessionManager.setIsInitialGetGroupList();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * get GroupDetails
     *
     * @param groupId input value(groupId)
     */
    public void getGroupDetails(String groupId) {
        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_GROUP_DETAILS);

        try {
            JSONObject object = new JSONObject();
            object.put("from", uniqueCurrentID);
            object.put("convId", groupId);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * create Group
     *
     * @param groupId input value(groupId)
     */
    public void createGroup(String groupId) {

        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_CREATE_USER);
        JSONObject object = new JSONObject();
        try {
            object.put("_id", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageEvent.setMessageObject(object);
        EventBus.getDefault().post(messageEvent);

    }

    /**
     * remove User
     *
     * @param groupId input value(groupId)
     */
    public void removeUser(String groupId) {
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_REMOVE_USER);
        JSONObject object = new JSONObject();
        try {
            object.put("_id", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageEvent.setMessageObject(object);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * getting history (online / offline)
     */
    private void getHistory() {
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_GET_MESSAGE);
        JSONObject object = new JSONObject();
        try {
            object.put("msg_to", uniqueCurrentID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageEvent.setMessageObject(object);
        EventBus.getDefault().post(messageEvent);

        // Getting offline call history
        SendMessageEvent callEvent = new SendMessageEvent();
        callEvent.setEventName(SocketManager.EVENT_GET_OFFLINE_CALLS);
        JSONObject callObj = new JSONObject();
        try {
            callObj.put("to", uniqueCurrentID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        callEvent.setMessageObject(callObj);
        EventBus.getDefault().post(callEvent);
    }

    /**
     * getting Group History
     */
    public void getGroupHistory() {
        SendMessageEvent groupMsgEvent = new SendMessageEvent();
        groupMsgEvent.setEventName(SocketManager.EVENT_GROUP);
        try {
            JSONObject object = new JSONObject();
            object.put("groupType", SocketManager.ACTION_GET_GROUP_CHAT_HISTORY);
            object.put("from", uniqueCurrentID);
            groupMsgEvent.setMessageObject(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(groupMsgEvent);
    }


    public void showNotification(JSONObject data) {
        NotificationManager mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = null;
        Intent intent = new Intent(this, HomeScreen.class);
        contentIntent = PendingIntent.getActivity(this, 0, intent, 0);
        try {
            String typeStr = data.getString("type");
            int type = Integer.parseInt(typeStr);
            String payLoad = data.getString("payload");

            if (type == MessageFactory.text) {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.drawable.synchat_logo : R.mipmap.ic_app_launcher)
                                .setContentTitle(this.getResources().getString(R.string.notification_title))
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(payLoad))
                                .setContentText(payLoad);
                if (Build.VERSION.SDK_INT >= 21) {
                    mBuilder.setPriority(Notification.PRIORITY_HIGH);
                    mBuilder.setVibrate(new long[0]);
                }
                mBuilder.setContentIntent(contentIntent);
                mNotificationManager.notify(1, mBuilder.build());
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * Custom push Notification based on chat type(single / group)
     *
     * @param data         input value(getting value from server response)
     * @param isSingleChat input value(isSingleChat)
     * @param isSecretMsg  input value(isSecretMsg)
     */
    @SuppressLint("RestrictedApi")
    public void CustomshowNotification(JSONObject data, boolean isSingleChat, boolean isSecretMsg) {
        // Using RemoteViews to bind custom layouts into Notification
        if (SessionManager.getInstance(MessageService.this).getAdminPendingStatus().equalsIgnoreCase("")) {
            try {
                String from = data.getString("from");
                if (!from.equalsIgnoreCase(uniqueCurrentID)) {
                    String username = "";
                    String varchatmess = "";
                    String varmsg = "";
                    String Name = "";
                    String thumbnail = "";
                    String groupName = "";
                    String doc_id = "";
                    String payLoad = "";
                    String resendTo = "";
                    String msgid = "";
                    String time = "";
                    String contactno = "";
                    PendingIntent pIntent;
                    String receiverDocumentID = "";

                    System.out.println("data+data" + data);
                    RemoteViews remoteViews = new RemoteViews(getPackageName(),
                            R.layout.customnotification);
                    DateFormat dff = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                    Date cDate = new Date();
                    DateFormat df = new SimpleDateFormat("H:mm a");
                    if (isSingleChat) {
                        if (!isSecretMsg) {
                            resendTo = data.getString("from");
                            String typeStr = data.getString("type");
                            int type = Integer.parseInt(typeStr);
                            String id = data.getString("id");
//                        Name = data.getString("Name");
                            Name = decodeString(data.getString("Name"));
                            contactno = data.getString("ContactMsisdn");
                            username = getcontactname.getSendername(from, contactno);
                        /*if (TextUtils.isEmpty(username)) {
                            username =decodeString(Name) ;
                        }*/
                            if (!data.has("replyDetails")) {
                                thumbnail = data.getString("From_avatar");
                            }

                            if (data.has("doc_id")) {
                                doc_id = (String) data.get("doc_id");
                            } else {
                                if (data.has("docId")) {
                                    doc_id = (String) data.get("docId");
                                }
                            }
//                        doc_id = (String) data.get("docId");
//                        doc_id = (String) data.get("doc_id");
//                        Log.d("Jsonobject_inside"," "+doc_id);
//                        if(doc_id != null){
//                            doc_id = (String) data.get("doc_id");
//                            Log.d("Jsonobject_inside"," "+doc_id);
//                        }else{
//                            doc_id = (String) data.get("docId");
//                            Log.d("Jsonobject_inside"," "+doc_id);
//                        }


                            String[] array = doc_id.split("-");

                            HashMap<String, Boolean> chatAndType = new HashMap<>();
                            chatAndType.put(resendTo, isSecretMsg);
                            if (!chat.contains(chatAndType)) {
                                chat.add(chatAndType);
                            }

                            receiverDocumentID = new String();
                            if (array[0].equalsIgnoreCase(uniqueCurrentID)) {
                                receiverDocumentID = array[1];
                            } else if (array[1].equalsIgnoreCase(uniqueCurrentID)) {
                                receiverDocumentID = array[0];
                            }
                            msgid = data.getString("msgId");
                            // payLoad = data.getString("payload");
                            payLoad = getString(R.string.notification_text_icon);
                            if (MessageFactory.text == type) {
                                messagepayload.add(username + " : " + payLoad + "\n");
                                // messagepayload.add(username + " : " + "Messaged you" + "\n");


                            } else if (MessageFactory.video == type) {
                                payLoad = getString(R.string.notification_video_icon);
                                // linemessage = linemessage + username + ":" + payLoad + "\n"+",";
                                messagepayload.add(username + " : " + payLoad + "\n");
                            } else if (MessageFactory.picture == type) {
                                payLoad = getString(R.string.notification_camera_icon);
                                // linemessage = linemessage + username + ":" + payLoad + "\n"+";";
                                messagepayload.add(username + " : " + payLoad + "\n");
                            } else if (MessageFactory.audio == type) {
                                payLoad = getString(R.string.notification_audio_icon);
                                //linemessage = linemessage + username + ":" + payLoad + "\n"+",";
                                messagepayload.add(username + " : " + payLoad + "\n");

                            } else if (MessageFactory.contact == type) {
                                payLoad = "Contact";
                                // linemessage = linemessage + username + ":" + payLoad + "\n"+",";
                                messagepayload.add(username + " : " + payLoad + "\n");
                            } else if (MessageFactory.web_link == type) {
                                payLoad = "Weblink";
                                // linemessage = linemessage + username + ":" + payLoad + "\n"+",";
                                messagepayload.add(username + " : " + payLoad + "\n");
                            } else if (MessageFactory.location == type) {
                                payLoad = getString(R.string.notification_location_icon);
                                messagepayload.add(username + " : " + payLoad + "\n");
                            } else if (MessageFactory.document == type) {
                                payLoad = getString(R.string.notification_document_icon);
                                messagepayload.add(username + " : " + payLoad + "\n");
                            }
                            // session.putmessagePrefs(messagepayload.toString());
                            long aMilliesecond = AppUtils.parseLong(data.getString("timestamp"));
                            time = getDate(aMilliesecond, "h:mm a");
                            String countDocId = uniqueCurrentID.concat("-").concat(resendTo);
//                    changeBadgeCount(countDocId);
                        }
                    } else {
                        try {
                            String phoneno;
                            String userid = data.getString("from");
                            String typeStr = data.getString("type");
                            int type = Integer.parseInt(typeStr);
                            // payLoad = data.getString("payload");
                            payLoad = getString(R.string.notification_text_icon);
                            resendTo = (String) data.get("groupId");
                        /*try {
                            startDate = dff.parse(session.gettime(resendTo + "tsNextLine"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/
                            String contactno2 = data.getString("msisdn");
                            phoneno = getcontactname.getSendername(userid, contactno2);
                            groupName = data.getString("groupName");
                            if (data.has("From_avatar")) {
                                thumbnail = data.getString("From_avatar");
                            }
                      /*  try {

                            startDate = dff.parse(session.gettime(resendTo + "tsNextLine"));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }*/
                            username = groupName;
                            if (MessageFactory.text == type) {
                                // linemessage = linemessage + username + "@" + phoneno + ":" + payLoad + "\n"+",";
                                messagepayload.add(phoneno + "@" + username + " : " + payLoad + "\n");
                                //  System.out.println("line message========================================>" + linemessage);
                            } else if (MessageFactory.video == type) {
                                payLoad = getString(R.string.notification_video_icon);
                                //linemessage = linemessage + username + "@" + phoneno + ":" + payLoad + "\n"+",";
                                messagepayload.add(phoneno + "@" + username + " : " + payLoad + "\n");
                                ;
                            } else if (MessageFactory.picture == type) {
                                payLoad = getString(R.string.notification_camera_icon);
                                //   linemessage = linemessage + username + "@" + phoneno + ":" + payLoad + "\n"+",";
                                messagepayload.add(phoneno + "@" + username + " : " + payLoad + "\n");
                            } else if (MessageFactory.audio == type) {
                                payLoad = getString(R.string.notification_audio_icon);
                                // linemessage = linemessage + username + "@" + phoneno + ":" + payLoad + "\n"+",";
                                messagepayload.add(phoneno + "@" + username + " : " + payLoad + "\n");
                            } else if (MessageFactory.contact == type) {
                                payLoad = "Contact";
                                //linemessage = linemessage + username + "@" + phoneno + ":" + payLoad + "\n"+",";
                                messagepayload.add(phoneno + "@" + username + " : " + payLoad + "\n");
                            } else if (MessageFactory.web_link == type) {
                                payLoad = "Weblink";
                                //linemessage = linemessage + username + "@" + phoneno + ":" + payLoad + "\n"+",";
                                messagepayload.add(phoneno + "@" + username + " : " + payLoad + "\n");
                            } else if (MessageFactory.location == type) {
                                payLoad = getString(R.string.notification_location_icon);
                                messagepayload.add(phoneno + "@" + username + " : " + payLoad + "\n");
                            } else if (MessageFactory.group_document_message == type) {
                                payLoad = getString(R.string.notification_document_icon);
                                messagepayload.add(phoneno + "@" + username + " : " + payLoad + "\n");
                            }

                            //  session.putmessagePrefs(linemessage);
                            doc_id = data.getString("toDocId");

                            HashMap<String, Boolean> chatAndType = new HashMap<>();
                            chatAndType.put(resendTo, isSecretMsg);
                            if (!chat.contains(chatAndType)) {
                                chat.add(chatAndType);
                            }

                            String[] array = doc_id.split("-");
                            receiverDocumentID = array[1];


                        } catch (JSONException ex) {
                            ex.printStackTrace();
                        }
                    }
                    if (chat.size() == 1) {
                        MessageItemChat mchat = new MessageItemChat();
                        mchat.setMessageId(uniqueCurrentID.concat("-").concat(receiverDocumentID).concat("-").concat(msgid));
                        mchat.setTextMessage(payLoad);
                        //   mchat.setTextMessage(username+" Messaged you");
                        mchat.setSenderName(username);
                        mchat.setNumberInDevice(contactno);

                        Intent intent = null;

                        if (isSingleChat) {
                            if (!isSecretMsg) {

//                             intent = new Intent(this, PinEnterActivity.class);
//                             intent.putExtra("msisdn", SessionManager.getInstance(mcontext).getPhoneNumberOfCurrentUser());


                                if (isAppRunning()) {
                                    intent = new Intent(this, ChatViewActivity.class);
                                    navigateToChatFromService(intent, receiverDocumentID, username);
                                } else {
                                    intent = new Intent(this, PinEnterActivity.class);
                                    intent.putExtra("msisdn", SessionManager.getInstance(mcontext).getPhoneNumberOfCurrentUser());
                                }


                            }
                        } else {

                            if (isAppRunning()) {
                                intent = new Intent(this, ChatViewActivity.class);
                                navigateToChatFromService(intent, receiverDocumentID, username);
                            } else {
                                intent = new Intent(this, PinEnterActivity.class);
                                intent.putExtra("msisdn", SessionManager.getInstance(mcontext).getPhoneNumberOfCurrentUser());

                            }

                        }

                        pIntent = PendingIntent.getActivity(this, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);
                    } else {
                        Intent intent = null;

                        if (isAppRunning()) {
                            intent = new Intent(this, HomeScreen.class);

                        } else {
                            intent = new Intent(this, PinEnterActivity.class);
                            intent.putExtra("msisdn", SessionManager.getInstance(mcontext).getPhoneNumberOfCurrentUser());
                        }
                        pIntent = PendingIntent.getActivity(this, 0, intent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                    }

                    // Set Notification Title
                    NotificationCompat.Builder builder;
                    Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        String channel = "";
                    /*if(!session.getTone().equalsIgnoreCase("None"))
                    {
                        channel="1";
                    }else {
                        channel="12";
                    }*/
                        builder = new NotificationCompat.Builder(getApplicationContext(), "1")
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app_launcher))
                                .setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.drawable.synchat_logo : R.mipmap.ic_app_launcher)
                                .setSound(notificationSound)
                                .setLights(Color.RED, 3000, 3000)
                                // Set Ticker Message
                                .setTicker("Message from \t" + username)
                                .setAutoCancel(true)
                                // Set PendingIntent into Notification
                                .setContentIntent(pIntent)
                                // Set RemoteViews into Notification
                                .setContent(remoteViews)
                                .setPriority(Notification.PRIORITY_HIGH)
//                            .setDefaults(Notification.DEFAULT_ALL)
                                .setColor(0xF01a9e5);
                    } else {
                        builder = new NotificationCompat.Builder(this)
                                .setChannelId("1")
                                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_app_launcher))
                                .setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.mipmap.ic_app_launcher : R.mipmap.ic_app_launcher)
                                // Set Ticker Message
                                .setTicker("Message from \t" + username)
                                .setAutoCancel(true)
                                // Set PendingIntent into Notification
                                .setContentIntent(pIntent)
                                // Set RemoteViews into Notification
                                .setContent(remoteViews)
                                .setPriority(Notification.PRIORITY_HIGH)
                                .setColor(0xF01a9e5);
                    }

                    // Set Icon

//                        .setColor(ContextCompat.getColor(MessageService.this, R.color.transparent));

//                if (chat.size() == 1) {
//                    builder.addAction(R.drawable.reload, "reply", pIntent);
//                }


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        int smallIconViewId = getResources().getIdentifier("right_icon", "id", android.R.class.getPackage().getName());

                        if (smallIconViewId != 0) {
                            RemoteViews view = builder.getContentView();
                            if (view != null)
                                view.setViewVisibility(smallIconViewId, View.INVISIBLE);

                            if (builder.getHeadsUpContentView() != null)
                                builder.getHeadsUpContentView().setViewVisibility(smallIconViewId, View.INVISIBLE);

                            if (builder.getBigContentView() != null)
                                builder.getBigContentView().setViewVisibility(smallIconViewId, View.INVISIBLE);
                        }
                    }


                    NotificationCompat.BigTextStyle style =
                            new NotificationCompat.BigTextStyle(builder);
                    int j = 0;
                    for (int i = messagepayload.size() - 1; i >= 0; i--) {
                        varchatmess = varchatmess + messagepayload.get(i);
                        if (j >= 10) {
                            break;
                        }
                        System.out.println(varmsg);
                    }
                    style.bigText(varchatmess);
                    if (chat.size() == 1) {
                        if (messagepayload.size() == 1) {
                            style.setSummaryText(messagepayload.size() + " new message");
                        } else {
                            style.setSummaryText(messagepayload.size() + " new messages");
                        }
                    } else {
                        style.setSummaryText(messagepayload.size() + " messages from " + chat.size() + " chats");
                    }
                    style.setBigContentTitle(this.getResources().getString(R.string.notification_title));


                    remoteViews.setImageViewResource(R.id.imagenotileft, R.mipmap.ic_app_launcher);
                    remoteViews.setTextViewText(R.id.title, (this.getResources().getString(R.string.notification_title)));

                    String strMessageCount = " messages ";
                    if (messagepayload.size() == 1) {
                        strMessageCount = " message ";
                    }

                    String strChatCount = " chats";
                    if (chat.size() == 1) {
                        strChatCount = "  chat";
                    }
                    Log.e("sizeeeee", messagepayload.size() + "");
                    payLoad = messagepayload.size() + strMessageCount + "from " + chat.size() + strChatCount;
                    remoteViews.setTextViewText(R.id.text, payLoad);
//
//                if (session.gettextsize().equalsIgnoreCase("Small"))
//                    remoteViews.setFloat(R.id.text, payLoad, 11);
//                else if (session.gettextsize().equalsIgnoreCase("Medium"))
//                    remoteViews.setFloat(R.id.text, payLoad, 14);
//                else if (session.gettextsize().equalsIgnoreCase("Large"))
//                    remoteViews.setFloat(R.id.text, payLoad, 17);


                    if (isSingleChat) {
                        if (session.getlightPrefsName().equals("None") || session.getlightPrefsName().equals("White"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.transparent1);
                        else if (session.getlightPrefsName().equals("Red"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.red);
                        else if (session.getlightPrefsName().equals("Yellow"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.yellow);
                        else if (session.getlightPrefsName().equals("Green"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.green);
                        else if (session.getlightPrefsName().equals("Cyan"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.cyan);
                        else if (session.getlightPrefsName().equals("Blue"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.blue);
                        else if (session.getlightPrefsName().equals("Purple"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.purple);
                    } else {
                        if (session.getlightPrefsNamegroup().equals("None") || session.getlightPrefsNamegroup().equals("White"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.transparent1);
                        else if (session.getlightPrefsNamegroup().equals("Red"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.red);
                        else if (session.getlightPrefsNamegroup().equals("Yellow"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.yellow);
                        else if (session.getlightPrefsNamegroup().equals("Green"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.green);
                        else if (session.getlightPrefsNamegroup().equals("Cyan"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.cyan);
                        else if (session.getlightPrefsNamegroup().equals("Blue"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.blue);
                        else if (session.getlightPrefsNamegroup().equals("Purple"))
                            remoteViews.setInt(R.id.relative_customnotify, "setBackgroundResource", R.color.purple);
                    }
                    // Locate and set the Text into customnotificationtext.xml TextViews
                    remoteViews.setTextViewText(R.id.title, (this.getResources().getString(R.string.notification_title)));
                    remoteViews.setTextViewText(R.id.text, payLoad);
//                if (session.gettextsize().equalsIgnoreCase("Small"))
//                    remoteViews.setFloat(R.id.text1, tsNextLine.toUpperCase(), 11);
//                else if (session.gettextsize().equalsIgnoreCase("Medium"))
//                    remoteViews.setFloat(R.id.text1, tsNextLine.toUpperCase(), 14);
//                else if (session.gettextsize().equalsIgnoreCase("Large"))
//                    remoteViews.setFloat(R.id.text1, tsNextLine.toUpperCase(), 17);
//                remoteViews.setTextViewText(R.id.text1, tsNextLine.toUpperCase());
                    if (session.getvibratePrefsName().equals("Default"))
                        builder.setVibrate(def);
                    else if (session.getvibratePrefsName().equals("Short"))
                        builder.setVibrate(shortv);
                    else if (session.getvibratePrefsName().equals("Long"))
                        builder.setVibrate(longv);
                    // Create Notification Manager

                    if (!isSecretMsg) {
                        if (isSingleChat) {
                            String locDbDocId = uniqueCurrentID + "-" + resendTo;
                            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);

                            String convId = userInfoSession.getChatConvId(locDbDocId);
                            MuteStatusPojo muteData = contactDB_sqlite.getMuteStatus(uniqueCurrentID, resendTo, convId, false);


                            if (muteData != null && muteData.getMuteStatus().equals("1")) {
                                boolean canNotify = false;
                                if (cDate.getTime() > muteData.getExpireTs()) {
                                    canNotify = true;
                                    contactDB_sqlite.updateMuteStatus(uniqueCurrentID, resendTo, resendTo, 0, "", "0", isSecretMsg);
                                }
                                if (!canNotify && muteData.getNotifyStatus().equals("1")) {
                                    canNotify = true;
                                }

                                if (canNotify) {
                                    if (!(session.getTone().contains("None"))) {
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                            grantUriPermission("com.android.systemui", notification, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                            r.play();

                                        }
                                    }


                                    NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                        CharSequence name = "1";
                                        String description = "SynChat";
                                    /*int importance =0;
                                    if(!session.getTone().equalsIgnoreCase("None"))
                                    {
                                        importance = NotificationManager.IMPORTANCE_DEFAULT;

                                    }*/
                                        int importance = NotificationManager.IMPORTANCE_DEFAULT;

                                        @SuppressLint("WrongConstant") NotificationChannel notificationChannel = new NotificationChannel("1", name, importance);
                                        notificationChannel.setDescription(description);


                                        // Register the channel with the system; you can't change the importance
                                        // or other notification behaviors after this
                                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                        notificationManager.createNotificationChannel(notificationChannel);


                                   /* CharSequence name = "1";
                                    String description = "Chatapp";
                                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    NotificationChannel channel = new NotificationChannel("1", description, importance);
                                    channel.setDescription(description);
                                    NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);*/

                                    /*NotificationChannel channel = new NotificationChannel("1",
                                            "Chatapp",
                                            NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationmanager.createNotificationChannel(channel);*/
                                    }
                                    notificationmanager.notify(1, builder.build());
                                }
                            } else {
                                if (!(session.getTone().contains("None"))) {
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        grantUriPermission("com.android.systemui", notification, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                        r.play();
                                    }
                                }
                                NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                                    CharSequence name = "1";
                                    String description = "SynChat";
                                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("1", name, importance);
                                    channel.setDescription(description);
                                    // Register the channel with the system; you can't change the importance
                                    // or other notification behaviors after this
                                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);

                               /* CharSequence name = "1";
                                String description = "Chatapp";
                                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                NotificationChannel channel = new NotificationChannel((String) name, description, importance);
                                channel.setDescription(description);
                                NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                                notificationManager.createNotificationChannel(channel);*/


                                /*NotificationChannel channel = new NotificationChannel("1",
                                        "Chatapp",
                                        NotificationManager.IMPORTANCE_DEFAULT);
                                notificationmanager.createNotificationChannel(channel);*/
                                }
                                notificationmanager.notify(1, builder.build());
                            }
                        } else {
                            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);

                            MuteStatusPojo muteData = contactDB_sqlite.getMuteStatus(uniqueCurrentID, null, resendTo, false);
                            if (muteData != null && muteData.getMuteStatus().equals("1")) {
                                boolean canNotify = false;
                                if (cDate.getTime() > muteData.getExpireTs()) {
                                    canNotify = true;
                                    contactDB_sqlite.updateMuteStatus(uniqueCurrentID, resendTo, resendTo, 0, "", "0", isSecretMsg);
                                }
                                if (!canNotify && muteData.getNotifyStatus().equals("1")) {
                                    canNotify = true;
                                }

                                if (canNotify) {
                                    if (!(session.getgroupTone().contains("None"))) {
                                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                            grantUriPermission("com.android.systemui", notification, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                            r.play();
                                        }
                                    }
                                    NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                                        CharSequence name = "1";
                                        String description = "SynChat";
                                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                        @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("1", name, importance);
                                        channel.setDescription(description);
                                        // Register the channel with the system; you can't change the importance
                                        // or other notification behaviors after this
                                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                        notificationManager.createNotificationChannel(channel);

                                    /*CharSequence name = "1";
                                    String description = "Chatapp";
                                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    NotificationChannel channel = new NotificationChannel((String) name, description, importance);
                                    channel.setDescription(description);
                                    NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);*/

                                   /* NotificationChannel channel = new NotificationChannel("1",
                                            "Chatapp",
                                            NotificationManager.IMPORTANCE_DEFAULT);
                                    notificationmanager.createNotificationChannel(channel);*/
                                    }
                                    notificationmanager.notify(1, builder.build());
                                }
                            } else {
                                if (!(session.getgroupTone().contains("None"))) {
                                    //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                        grantUriPermission("com.android.systemui", notification, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                                        r.play();
                                    }
                                }
                                NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


                                    CharSequence name = "1";
                                    String description = "SynChat";
                                    int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                    @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("1", name, importance);
                                    channel.setDescription(description);
                                    // Register the channel with the system; you can't change the importance
                                    // or other notification behaviors after this
                                    NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                    notificationManager.createNotificationChannel(channel);


                               /* CharSequence name = "1";
                                String description = "Chatapp";
                                int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                NotificationChannel channel = new NotificationChannel((String) name, description, importance);
                                channel.setDescription(description);
                                NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
                                notificationManager.createNotificationChannel(channel);*/

                               /* NotificationChannel channel = new NotificationChannel("1",
                                        "Chatapp",
                                        NotificationManager.IMPORTANCE_DEFAULT);
                                notificationmanager.createNotificationChannel(channel);*/
                                }
                                notificationmanager.notify(1, builder.build());
                            }
                            // }
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.logo_initial_loader : R.drawable.logo_initial_loader;
    }

    /**
     * shown MissedCall Notification
     *
     * @param toUserId     input value(toUserId)
     * @param toUserMsisdn input value(toUserMsisdn)
     */
    private void showMissedCallNotification(String toUserId, String toUserMsisdn) {
        missedCallCount++;
        String content;
        if (missedCallCount > 1) {
            content = missedCallCount + " Missed calls";
        } else {
            content = missedCallCount + " Missed call";
            lastMissedCallId = toUserId;
        }

        if (lastMissedCallId.equalsIgnoreCase(toUserId)) {
            String name = getcontactname.getSendername(toUserId, toUserMsisdn);
            content = content + " from " + name;
        }

        Intent intent = new Intent(this, HomeScreen.class);
        intent.putExtra(HomeScreen.FROM_MISSED_CALL_NOTIFICATION, true);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                // Set Icon
                .setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.drawable.synchat_logo : R.mipmap.ic_app_launcher)
                // Set Ticker Message
                .setTicker("")
                .setContentTitle(getString(R.string.app_name))
                .setContentText(content)
                .setAutoCancel(true)
                // Set PendingIntent into Notification
                .setContentIntent(pIntent)
                // Set RemoteViews into Notification

                .setColor(ContextCompat.getColor(MessageService.this, R.color.transparent));
        /*if (chat.size() == 1) {
            builder.addAction(R.drawable.reload, "reply", pIntent);
        }*/
        NotificationCompat.BigTextStyle style =
                new NotificationCompat.BigTextStyle(builder);
        if (session.getvibratePrefsName().equals("Default"))
            builder.setVibrate(def);
        else if (session.getvibratePrefsName().equals("Short"))
            builder.setVibrate(shortv);
        else if (session.getvibratePrefsName().equals("Long"))
            builder.setVibrate(longv);

        style.bigText(content)
                .setBigContentTitle(getString(R.string.app_name));
//                .setSummaryText("Big summary");
        /*.setContentTitle("Title")
                .setContentText("Summary")*/

        builder.setStyle(style);

        /*String locDbDocId = uniqueCurrentID + "-" + resendTo;
        if (session.isNotificationOnMute(locDbDocId)) {
            if (startDate.before(cDate) && !(session.getTone().contains("None"))) {
                //if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                r.play();
            }

        }*/
        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.notify(2, builder.build());
    }

    /**
     * change Badge Count for chat
     *
     * @param convId input value(convId)
     */
    private void changeBadgeCount(String convId) {
        ShortcutBadgeManager shortcutBadgeMgnr = new ShortcutBadgeManager(MessageService.this);
        shortcutBadgeMgnr.putMessageCount(convId);
        /*if(convId.contains("-g")) {
            sessionManager.setGroupChatCount(convId,sessionManager.getGroupChatCount(convId)+ 1);
        }else {
            sessionManager.setSingleChatCount(convId,sessionManager.getSingleChatCount(convId)+ 1);
        }*/

        int totalCount = shortcutBadgeMgnr.getTotalCount();

        try {
            ShortcutBadger.applyCountOrThrow(MessageService.this, totalCount);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * change Badge Call Count
     *
     * @param convId input value(convId)
     */
    private void changeBadgeCallCount(String convId) {
        ShortcutBadgeManager shortcutBadgeMgnr = new ShortcutBadgeManager(MessageService.this);
        shortcutBadgeMgnr.putCallCount(convId);
        int totalCount = shortcutBadgeMgnr.getTotalCallCount();

        try {
            ShortcutBadger.applyCountOrThrow(MessageService.this, totalCount);

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * decoding String data
     *
     * @param encoded input value(encoded)
     * @return response value
     */
    private String decodeString(String encoded) {
        byte[] dataDec = Base64.decode(encoded, Base64.DEFAULT);
        String decodedString = "";
        try {
            decodedString = new String(dataDec, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            return decodedString;
        }
    }

    /**
     * loadMessage for all type(media, status, delete event) & updated database
     *
     * @param objects input value(response from server data)
     */
    private void loadMessage(JSONObject objects) {
        try {
            int normal_Offline = 0;
            String type = objects.getString("type");
            if (objects.has("is_deleted_everyone")) {
                normal_Offline = objects.getInt("is_deleted_everyone");
            }
            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);

            ChatappContactModel contact = new ChatappContactModel();


            contact.set_id(objects.getString("from"));
            contact.setStatus(objects.getString("from_status"));
            if (objects.has("From_avatar"))
                contact.setAvatarImageUrl(objects.getString("From_avatar"));
            else if (objects.has("avatar"))
                contact.setAvatarImageUrl(objects.getString("avatar"));
            else if (objects.has("ContactMsisdn"))
                contact.setMsisdn(objects.getString("ContactMsisdn"));
            contact.setRequestStatus("3");
            if (objects.has("from_name"))
                contact.setFirstName(decodeString(objects.getString("from_name")));
            else if (objects.has("Name"))
                contact.setFirstName(decodeString(objects.getString("Name")));
            contactDB_sqlite.updateUserDetails(objects.getString("from"), contact);


            if (type.equalsIgnoreCase("" + MessageFactory.timer_change)) {
                loadSecretTimerChangeMessage(objects);
            } else {
                MessageItemChat item = incomingMsg.loadSingleMessage(objects);
                String from = objects.getString("from");
                String to = objects.getString("to");
                String secretType = objects.getString("secret_type");
                String msgId = objects.getString("msgId");
//                String id = objects.getString("id");
                String convId = objects.getString("convId");
                String stat = "", pwd = "";
                String doc_id;
                if (objects.has("docId")) {
                    doc_id = objects.getString("docId");
                } else {
                    doc_id = objects.getString("doc_id");
                }
                String uniqueID = to + "-" + from;

                boolean isSecretMsg = false;
                if (secretType.equalsIgnoreCase("yes")) {
                    isSecretMsg = true;
                    sendAckToServer(from, doc_id, "" + msgId, true);
                } else {
//                        session.putmark(from);
                    sendAckToServer(from, doc_id, "" + msgId, false);
                }
                getUserDetails(from);

                MessageDbController db = CoreController.getDBInstance(this);
                item.setMessageId(uniqueID + "-" + msgId);
                item.setIsSelf(false);
                item.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);

                String chatType = MessageFactory.CHAT_TYPE_SINGLE;
                if (secretType.equalsIgnoreCase("yes")) {
                    chatType = MessageFactory.CHAT_TYPE_SECRET;
                    uniqueID = uniqueID + "-" + MessageFactory.CHAT_TYPE_SECRET;
                    String timer = objects.getString("incognito_timer");
                    String timerCreatedBy = objects.getString("incognito_user");
                    item.setSecretTimer(timer);
                    item.setSecretTimeCreatedBy(timerCreatedBy);

                    //new dbsqlite
//                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
                    contactDB_sqlite.updateSecretMessageTimer(to, timer, timerCreatedBy, item.getMessageId());
                }

                if (session.getarchivecount() != 0) {
                    if (session.getarchive(uniqueCurrentID + "-" + from))
                        session.removearchive(uniqueCurrentID + "-" + from);
                }


                if (type.equals(String.valueOf(MessageFactory.missed_call))) {
                    if (db.checkChatMessage(item, chatType)) {
                        if (!HomeScreen.callHistActive) {
                            changeBadgeCallCount(convId);
                            showMissedCallNotification(from, item.getSenderMsisdn());
                        }
                    }

                }

                db.updateChatMessage(item, chatType);

                if (!type.equals(String.valueOf(MessageFactory.missed_call))) {
                    changeBadgeCount(convId);
                }

                ChatLockPojo lockPojo = getChatLockdetailfromDB(uniqueID, chatType);
                if (sessionManager.getLockChatEnabled().equals("1") && lockPojo != null) {
                    stat = lockPojo.getStatus();
                    pwd = lockPojo.getPassword();
                }

//                if (type.equalsIgnoreCase("" + MessageFactory.missed_call)) {
                //            showMissedCallNotification(from, item.getSenderMsisdn());
                //          }

                if (!ChatViewActivity.isChatPage) {
                    if (isSecretMsg) {
                        if (!from.equalsIgnoreCase(uniqueCurrentID)) {
                            CustomshowNotification(objects, true, isSecretMsg);
                        }
                    } else if (!stat.equals("1")) {
                        if (!from.equalsIgnoreCase(uniqueCurrentID)) {
                            if (!type.equals(String.valueOf(MessageFactory.missed_call))) {
                                CustomshowNotification(objects, true, isSecretMsg);
                            }
                        }
                    }
                } else {
                    if (!from.equalsIgnoreCase(uniqueCurrentID)) {
                        if (!stat.equals("1")) {
                            if (!session.gettoid().equalsIgnoreCase(uniqueID)) {
                                if (!type.equals(String.valueOf(MessageFactory.missed_call))) {
                                    CustomshowNotification(objects, true, isSecretMsg);
                                }
                            }
                        }
                    }
                }
                if (!userInfoSession.hasChatConvId(uniqueID)) {
                    userInfoSession.updateChatConvId(uniqueID, from, convId);
                }

                chkStatus();
                chkmobiledataon();
                if (connected.equalsIgnoreCase("CONNECTED")) {
                    String[] values = session.getmobiledataPrefsName().split(",");
                    for (int i = 0; i < values.length; i++) {
                        if (values[i].equalsIgnoreCase("photo")) {
                            if (MessageFactory.picture == Integer.parseInt(item.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Audio")) {
                            if (MessageFactory.audio == Integer.parseInt(item.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Videos")) {
                            if (MessageFactory.video == Integer.parseInt(item.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Documents")) {
                            if (MessageFactory.document == Integer.parseInt(item.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                            }
                        }
                    }
                }
                /*Boolean dataroaming = isDataRoamingEnabled();
                if (dataroaming) {
                    String[] values = session.getromingPrefsName().split(",");
                    for (int i = 0; i < values.length; i++) {
                        if (values[i].equalsIgnoreCase("photo")) {
                            if (MessageFactory.picture == Integer.parseInt(item.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Audio")) {
                            if (MessageFactory.audio == Integer.parseInt(item.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Videos")) {
                            if (MessageFactory.video == Integer.parseInt(item.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Documents")) {
                            if (MessageFactory.document == Integer.parseInt(item.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                            }
                        }
                    }
                }*/
                if (wifiEnabled) {
                    //  uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                    String[] values = session.getwifiPrefsName().split(",");
                    for (int i = 0; i < values.length; i++) {
                        if (values[i].equalsIgnoreCase("photo")) {
                            if (MessageFactory.picture == Integer.parseInt(item.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Audio")) {
                            if (MessageFactory.audio == Integer.parseInt(item.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Videos")) {
                            if (MessageFactory.video == Integer.parseInt(item.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Documents")) {
                            if (MessageFactory.document == Integer.parseInt(item.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, isSecretMsg);
                            }
                        }
                    }
                }


                //-------------Delete Chat-----------------

                if (normal_Offline == 1) {
                    String new_docId, new_msgId, new_type, new_recId, new_convId;
                    try {
                        String fromId = objects.getString("from");

                        if (!fromId.equalsIgnoreCase(uniqueCurrentID)) {
                            String chat_id = (String) objects.get("docId");
                            String[] ids = chat_id.split("-");

                            new_type = objects.getString("chat_type");
//                            new_recId = objects.getString("recordId");
//                            new_convId = objects.getString("convId");

                            if (new_type.equalsIgnoreCase("single")) {
                                new_docId = ids[1] + "-" + ids[0];
                                new_msgId = new_docId + "-" + ids[2];
                                db.deleteSingleMessage(new_docId, new_msgId, "single", "other");
                                db.deleteChatListPage(new_docId, new_msgId, "single", "other");
                            } else {
                                new_docId = ids[1] + "-g-" + ids[0];
                                new_msgId = uniqueCurrentID + "-g-" + ids[1] + "-g-" + ids[3];
                                String groupAndMsgId = ids[1] + "-g-" + ids[3];

                                db.deleteSingleMessage(groupAndMsgId, new_msgId, "group", "other");
                                db.deleteChatListPage(groupAndMsgId, new_msgId, "group", "other");
                            }


                        }
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Secret Timer ChangeMessage for delivery d=status updated and update database too
     *
     * @param objects input value(response from server data)
     */
    private void loadSecretTimerChangeMessage(JSONObject objects) {
        try {
            String secretType = objects.getString("secret_type");
            if (secretType.equalsIgnoreCase("yes")) {
                String from = objects.getString("from");
                String to = objects.getString("to");
                String convId = objects.getString("convId");
                String recordId = objects.getString("recordId");
                String timer = objects.getString("incognito_timer");
                String timerMode = objects.getString("incognito_timer_mode");
                String toUserMsgId;
                if (objects.has("doc_id")) {
                    toUserMsgId = objects.getString("doc_id");
                } else {
                    toUserMsgId = objects.getString("docId");
                }
                String msgId = objects.getString("id");
                String timeStamp = objects.getString("timestamp");
                String fromMsisdn = objects.getString("ContactMsisdn");

                MessageItemChat item = new MessageItemChat();
                String docId;
                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    docId = from + "-" + to;
                    item.setReceiverID(to);
                } else {
                    docId = to + "-" + from;
                    item.setReceiverID(from);
                    sendAckToServer(from, toUserMsgId, msgId, true);
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

                MessageDbController db = CoreController.getDBInstance(this);
                docId = docId + "-" + MessageFactory.CHAT_TYPE_SECRET;
                db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SECRET);


                //new dbsqlite
                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
                contactDB_sqlite.updateSecretMessageTimer(from, timer, uniqueCurrentID, item.getMessageId());

                if (!userInfoSession.hasChatConvId(docId)) {
                    userInfoSession.updateChatConvId(docId, from + "-" + MessageFactory.CHAT_TYPE_SECRET, convId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Boolean isDataRoamingEnabled() {
        try {
            // return true or false if data roaming is enabled or not
            return Settings.Secure.getInt(getApplication().getContentResolver(), Settings.Secure.DATA_ROAMING) == 1;
        } catch (Settings.SettingNotFoundException e) {
            // return null if no such settings exist (device with no radio data ?)
            return false;
        }
    }

    /**
     * Check wifi status
     */
    public void chkStatus() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiEnabled = wifiManager.isWifiEnabled();
    }

    /**
     * Check mobile data Connectivity status
     */
    public void chkmobiledataon() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        connected = mobileInfo.getState().toString();
    }

    /**
     * get User Details response data or local database
     *
     * @param userId input value(userId)
     */
    public void getUserDetails(String userId) {
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);

        if (contactDB_sqlite.getUserOpponenetDetails(userId) == null) {
            try {
                JSONObject eventObj = new JSONObject();
                eventObj.put("userId", userId);

                SendMessageEvent event = new SendMessageEvent();
                event.setEventName(SocketManager.EVENT_GET_USER_DETAILS);
                event.setMessageObject(eventObj);
                EventBus.getDefault().post(event);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                JSONObject eventObj = new JSONObject();
                eventObj.put("userId", userId);
                SendMessageEvent event = new SendMessageEvent();
                event.setEventName(SocketManager.EVENT_GET_USER_DETAILS);
                event.setMessageObject(eventObj);
                EventBus.getDefault().post(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    }

    /**
     * Getting file downloading status
     *
     * @param object input value(response from server data)
     */
    private void writeBufferToFile(JSONObject object) {

        try {
            String fileName = object.getString("ImageName");
            String localPath = object.getString("LocalPath");
//            String localFileName = object.getString("LocalFileName");
            String end = object.getString("end");
            int bytesRead = object.getInt("bytesRead");
            int fileSize = object.getInt("filesize");
            String msgId = object.getString("MsgId");

            if (object.get("bufferData") instanceof byte[]) {
                byte[] buffer = (byte[]) object.get("bufferData");

                File file = new File(localPath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                FileOutputStream outputStream = new FileOutputStream(file, true);
                try {
                    outputStream.write(buffer);
                } finally {
                    outputStream.close();
                }

                object.remove("bufferData");
                if (uploadDownloadManager.getDownloadProgress(msgId) < 0) {
                    String docId = object.getString("DocId");
                    MessageDbController db = CoreController.getDBInstance(this);
                    db.updateMessageDownloadStatus(docId, msgId, MessageFactory.DOWNLOAD_STATUS_DOWNLOADING);
                }
                uploadDownloadManager.setDownloadProgress(msgId, bytesRead, object);

                if (end.equalsIgnoreCase("1")) {

                    if (decrypt(file)) {
                        String docId = object.getString("DocId");
                        uploadDownloadManager.removeDownloadProgress(msgId);
                        MessageDbController db = CoreController.getDBInstance(this);
                        db.updateMessageDownloadStatus(docId, msgId, MessageFactory.DOWNLOAD_STATUS_COMPLETED);

                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Checking for AppRunning status
     *
     * @return response value
     */
    private boolean isAppRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                    return true;
            }
        }

        return false;
    }

    /**
     * decrypt file
     *
     * @param file input value(file)
     * @return response value
     */
    private boolean decrypt(File file) {

        try {

            CryptLib cryptLib = new CryptLib();

            byte[] bytesArray = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();

            file.delete();


            byte[] buffer1 = cryptLib.encryptDecrypt(bytesArray, (Constants.DUMMY_KEY), CryptLib.EncryptMode.DECRYPT, cryptLib.generateRandomIV16());


//            byte[] buffer1=  cryptLib.decryptCipherTextWithBytearray( Base64.encodeToString(bytesArray, Base64.DEFAULT),(Constants.DUMMY_KEY));

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileOutputStream outputStreamOri = new FileOutputStream(file, true);
            outputStreamOri.write(buffer1);
            outputStreamOri.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;


    }

    /**
     * send Ack To Server to check delivery status
     *
     * @param to           input value(to)
     * @param doc_id       input value(doc_id)
     * @param id           input value(id)
     * @param isSecretChat input value(isSecretChat)
     */
    private void sendAckToServer(String to, String doc_id, String id, boolean isSecretChat) {
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_MESSAGE_ACK);
        MessageAck ack = (MessageAck) MessageFactory.getMessage(MessageFactory.message_ack, (this));
        messageEvent.setMessageObject((JSONObject) ack.getMessageObject(to, doc_id,
                MessageFactory.DELIVERY_STATUS_DELIVERED, id, isSecretChat));
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * send Call Ack To Server (call action)
     *
     * @param callObj  input value(getting value from server response)
     * @param callItem input value(getting value from model class)
     */
    private void sendCallAckToServer(JSONObject callObj, CallItemChat callItem) {
        try {
            String callId;
            if (callObj.has("docId")) {
                callId = callObj.getString("docId");
            } else {
                callId = callObj.getString("doc_id");
            }
            SendMessageEvent messageEvent = new SendMessageEvent();
            messageEvent.setEventName(SocketManager.EVENT_CALL_ACK);
            CallAck ack = (CallAck) MessageFactory.getMessage(MessageFactory.call_ack, (this));
            messageEvent.setMessageObject((JSONObject) ack.getMessageObject(callItem.getOpponentUserId(),
                    callId, MessageFactory.DELIVERY_STATUS_DELIVERED, callItem.getId()));
            EventBus.getDefault().post(messageEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Service start
     *
     * @param intent  based on this move one activity to another
     * @param flags   input value(flags)
     * @param startId input value(startId)
     * @return response value
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.makeText(this, "SERVICE IS START_STICKY " + intent, Toast.LENGTH_LONG).show();
        return START_STICKY;
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * kill the service and unregister for eventbus
     */
    @Override
    public void onDestroy() {

        System.out.println("=aaaaa===here in destroy");

        EventBus.getDefault().unregister(this);
        offlineMsgHandler.onServiceDestroy();
        service = null;
        manager.disconnect();

        if (incomCallBroadcastRunnable != null) {
            incomCallBroadcastHandler.removeCallbacks(incomCallBroadcastRunnable);
        }

        try {
            unregisterReceiver(mDateReceiver);
        } catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Receiver not registered")) {
                // Ignore this exception. It's a known bug and is exactly what is desired.
                //Log.w(TAG,"Tried to unregister the receiver when it's not registered");
            } else {
                // unexpected, re-throw
                throw e;
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            stopForeground(true); //true will remove notification
        }

       /* Intent broadcastIntent = new Intent("com.restart.service");
        sendBroadcast(broadcastIntent);*/

        Intent broadcastIntent = new Intent("com.chatapp.android.RestartSensor");
        sendBroadcast(broadcastIntent);


        super.onDestroy();
    }

    /**
     * killed service
     *
     * @param rootIntent based on this move one activity to another
     */
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        if (sessionManager != null && sessionManager.isLoginKeySent() && sessionManager.isValidDevice()) {
            Intent restartServiceIntent = new Intent(getApplicationContext(), this.getClass());
            restartServiceIntent.setPackage(getPackageName());

            PendingIntent restartServicePendingIntent = PendingIntent.getService(getApplicationContext(), 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
            AlarmManager alarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
            alarmService.set(
                    AlarmManager.ELAPSED_REALTIME,
                    SystemClock.elapsedRealtime() + 500,
                    restartServicePendingIntent);

            sendBroadcast(new Intent("SERVICE_KILLED"));
        }
        /*if (sessionManager != null && sessionManager.isLoginKeySent() && sessionManager.isValidDevice()) {
            startService(new Intent(this, MessageService.class));
        }*/


        Intent s = new Intent();
        s.setAction("SERVICE_KILLED");
        sendBroadcast(s);

        Intent msgSvcIntent = new Intent(this, MessageService.class);
        startService(msgSvcIntent);


        super.onTaskRemoved(rootIntent);
    }

    /**
     * Getting Group Details
     *
     * @param data input value(response from server data)
     */
    private void loadGroupDetails(String data) {

        try {
            JSONObject objects = new JSONObject(data);

            String displayName = objects.getString("DisplayName");
            String displayPic = objects.getString("GroupIcon");
            String isAdmin = objects.getString("isAdmin");
            String groupId = objects.getString("_id");

            String adminMembers = "";
            List<GroupMembersPojo> savedMembersList = new ArrayList<>();
            List<GroupMembersPojo> unsavedMembersList = new ArrayList<>();
            List<GroupMembersPojo> allMembersList = new ArrayList<>();
            GroupMembersPojo currentUserPojo = null;

            JSONArray arrMembers = objects.getJSONArray("GroupUsers");
            for (int i = 0; i < arrMembers.length(); i++) {
                JSONObject userObj = arrMembers.getJSONObject(i);

                String userId = userObj.getString("id");
                String active = userObj.getString("active");
                String isDeleted = userObj.getString("isDeleted");
                String msisdn = "";
                if (userObj.has("msisdn"))
                    msisdn = userObj.getString("msisdn");

//                String phNumber = userObj.getString("msisdn");
//                String name = userObj.getString("Name");
                String status = userObj.getString("Status");
                String userDp = userObj.getString("avatar");
                String adminUser = userObj.getString("isAdmin");
//                String savedContactName = userObj.getString("ContactName");
                String isExitsContact = userObj.getString("isExitsContact");
                String contactName = getcontactname.getSendername(userId, msisdn);
                if (uniqueCurrentID.equalsIgnoreCase(userId)) {
                    contactName = "You";
                } else {
                    getUserDetails(userId);
                }


                String savedName = getcontactname.getSendername(userId, msisdn);
                GroupMembersPojo membersPojo = new GroupMembersPojo();
                membersPojo.setUserId(userId);
                membersPojo.setContactName(savedName);
                membersPojo.setMsisdn(msisdn);

                if (userId.equalsIgnoreCase(uniqueCurrentID)) {
                    currentUserPojo = membersPojo;
                } else {
                    if (msisdn.equalsIgnoreCase(savedName)) {
                        unsavedMembersList.add(membersPojo);
                    } else {
                        savedMembersList.add(membersPojo);
                    }
                }

                if (adminUser.equals("1")) {
                    adminMembers = adminMembers.concat(userId).concat(",");
                }
            }

            if (currentUserPojo != null) {
                allMembersList.addAll(savedMembersList);
                allMembersList.addAll(unsavedMembersList);
                allMembersList.add(currentUserPojo);

                String groupContactNames = "";
                String groupMembers = "";
                for (int i = 0; i < allMembersList.size(); i++) {
                    GroupMembersPojo pojo = allMembersList.get(i);
                    groupContactNames = groupContactNames.concat(pojo.getContactName());
                    groupMembers = groupMembers.concat(pojo.getUserId());
                    if ((arrMembers.length() - 1) != i) {
                        groupContactNames = groupContactNames.concat(", ");
                        groupMembers = groupMembers.concat(",");
                    }
                }

                String docId = uniqueCurrentID.concat("-").concat(groupId).concat("-g");
                GroupInfoSession groupInfoSession = new GroupInfoSession(MessageService.this);
                boolean hasInfo = groupInfoSession.hasGroupInfo(docId);
                GroupInfoPojo groupData;
                if (!hasInfo) {
                    groupData = new GroupInfoPojo();
                } else {
                    groupData = groupInfoSession.getGroupInfo(docId);
                }
                if (groupData != null) {
                    groupData.setGroupId(groupId);
                    groupData.setGroupName(displayName);
                    groupData.setIsAdminUser(isAdmin);
                    groupData.setAvatarPath(displayPic);
                    groupData.setLiveGroup(true);
                    groupData.setGroupMembers(groupMembers);
                    groupData.setAdminMembers(adminMembers);
                    groupData.setGroupContactNames(groupContactNames);
                    groupInfoSession.updateGroupInfo(docId, groupData);
                }
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * calling for send Offline Message
     *
     * @param message   input value(message)
     * @param eventName input value(eventName)
     */
    public void sendOfflineMessage(Object message, String eventName) {
        manager.send(message, eventName);
    }

    /**
     * get Chat Lock detail from Database
     *
     * @param documentID input value(documentID)
     * @param chatType   input value(chatType)
     * @return response value
     */
    public ChatLockPojo getChatLockdetailfromDB(String documentID, String chatType) {
        MessageDbController dbController = CoreController.getDBInstance(this);
        String convId = userInfoSession.getChatConvId(documentID);
        String receiverId = userInfoSession.getReceiverIdByConvId(convId);
        ChatLockPojo pojo = dbController.getChatLockData(receiverId, chatType);
        return pojo;
    }

    /**
     * navigate To Chat From Service class
     *
     * @param intent             based on this move one activity to another
     * @param receiverDocumentID input value()
     * @param username           input value()
     */
    public void navigateToChatFromService(Intent intent, String receiverDocumentID, String username) {

        intent.putExtra("receiverUid", "");
        intent.putExtra("receiverName", username);
        intent.putExtra("documentId", receiverDocumentID);
        intent.putExtra("Image", "");
        intent.putExtra("type", 0);
        intent.putExtra("backfrom", true);
        intent.putExtra("Username", username);
    }

    /**
     * Incoming CallData
     *
     * @param data input value(response from server data)
     */
    private void loadIncomingCallData(String data) {
        try {

           /* if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                if (SessionManager.getInstance(MessageService.this).getApplicationPauseState().equalsIgnoreCase("1")) {
                    Inbox(data);
                } else {
                    incomingCallData(data);
                }

            } else {
                incomingCallData(data);
            }*/

            if (SessionManager.getInstance(MessageService.this).getApplicationPauseState().equalsIgnoreCase("1")) {
                Inbox(data);
            } else {
                incomingCallData(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * incoming CallData & update call status (Moveing to call screen)
     *
     * @param data input value(response from server data)
     */
    private void incomingCallData(String data) {
        try {
            JSONObject callObj = new JSONObject(data);
            String from = callObj.getString("from");
            String to = callObj.getString("to");
            String callStatus = callObj.getString("call_status");
            String Room_id = callObj.getString("roomid");
            String recordId = callObj.getString("recordId");
            if (to.equalsIgnoreCase(uniqueCurrentID) && callStatus.equals(MessageFactory.CALL_STATUS_CALLING + "")) {

                CallItemChat callItem = incomingMsg.loadIncomingCall(callObj);
                MessageDbController db = CoreController.getDBInstance(this);
                db.updateCallLogs(callItem);

                boolean isVideoCall = false;
                if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
                    isVideoCall = true;
                }

                if (!CallsActivity.isStarted && !IncomingCallActivity.isStarted) {


                    String ts = callObj.getString("timestamp");

                    Intent intent = new Intent(this, IncomingCallActivity.class);
                    intent.putExtra(IncomingCallActivity.EXTRA_DOC_ID, callItem.getCallId());
                    intent.putExtra(IncomingCallActivity.EXTRA_FROM_USER_ID, callItem.getOpponentUserId());
                    intent.putExtra(IncomingCallActivity.EXTRA_TO_USER_ID, to);
                    intent.putExtra(IncomingCallActivity.EXTRA_FROM_USER_MSISDN, callItem.getOpponentUserMsisdn());
                    intent.putExtra(IncomingCallActivity.EXTRA_CALL_ROOM_ID, Room_id);
                    intent.putExtra(IncomingCallActivity.EXTRA_CALL_RECORD_ID, recordId);
                    intent.putExtra(IncomingCallActivity.EXTRA_CALL_TYPE, isVideoCall);
                    intent.putExtra(IncomingCallActivity.EXTRA_CALL_TIME_STAMP, ts);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    /*if(callObj.has("device_type"))
                    {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS );
                    }else {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS );
                    }*/


                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    if (callItem.getOpponentUserId().equalsIgnoreCase(CallsActivity.opponentUserId)) {
                        sendIncomingCallBroadcast(intent);
                    }

                    // Ack to call sender(reverse order)
                    JSONObject ackObj = CallMessage.getCallStatusObject(to, callItem.getOpponentUserId(),
                            callItem.getId(), callItem.getCallId(), callItem.getRecordId(), MessageFactory.CALL_STATUS_ARRIVED, callItem.getCallType());
                    SendMessageEvent event = new SendMessageEvent();
                    event.setEventName(SocketManager.EVENT_CALL_STATUS);
                    event.setMessageObject(ackObj);
                    EventBus.getDefault().post(event);

                    sendCallAckToServer(callObj, callItem);
                } else {

                }
                /*else if (CallsActivity.opponentUserId != null && !from.equalsIgnoreCase(CallsActivity.opponentUserId)) {
                    db.updateCallStatus(callItem.getCallId(), MessageFactory.CALL_STATUS_MISSED, "00:00");
                }*/
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }



/*
    private void loadIncomingCallData(String data) {
        try {
            JSONObject callObj = new JSONObject(data);
            String from = callObj.getString("from");
            String to = callObj.getString("to");
            String callStatus = callObj.getString("call_status");
            String Room_id = callObj.getString("roomid");
            String recordId = callObj.getString("recordId");
            if (to.equalsIgnoreCase(uniqueCurrentID) && callStatus.equals(MessageFactory.CALL_STATUS_CALLING + "")) {

                CallItemChat callItem = incomingMsg.loadIncomingCall(callObj);
                MessageDbController db = CoreController.getDBInstance(this);
                db.updateCallLogs(callItem);

                boolean isVideoCall = false;
                if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
                    isVideoCall = true;
                }

                if (!CallsActivity.isStarted && !IncomingCallActivity.isStarted) {
                    String ts = callObj.getString("timestamp");

                    Intent intent = new Intent(this, IncomingCallActivity.class);
                    intent.putExtra(IncomingCallActivity.EXTRA_DOC_ID, callItem.getCallId());
                    intent.putExtra(IncomingCallActivity.EXTRA_FROM_USER_ID, callItem.getOpponentUserId());
                    intent.putExtra(IncomingCallActivity.EXTRA_TO_USER_ID, to);
                    intent.putExtra(IncomingCallActivity.EXTRA_FROM_USER_MSISDN, callItem.getOpponentUserMsisdn());
                    intent.putExtra(IncomingCallActivity.EXTRA_CALL_ROOM_ID, Room_id);
                    intent.putExtra(IncomingCallActivity.EXTRA_CALL_RECORD_ID, recordId);
                    intent.putExtra(IncomingCallActivity.EXTRA_CALL_TYPE, isVideoCall);
                    intent.putExtra(IncomingCallActivity.EXTRA_CALL_TIME_STAMP, ts);
//                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    */
/*if(callObj.has("device_type"))
                    {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS );
                    }else {
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS );
                    }*//*



                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    if (callItem.getOpponentUserId().equalsIgnoreCase(CallsActivity.opponentUserId)) {
                        sendIncomingCallBroadcast(intent);
                    }

                    // Ack to call sender(reverse order)
                    JSONObject ackObj = CallMessage.getCallStatusObject(to, callItem.getOpponentUserId(),
                            callItem.getId(), callItem.getCallId(), callItem.getRecordId(), MessageFactory.CALL_STATUS_ARRIVED);
                    SendMessageEvent event = new SendMessageEvent();
                    event.setEventName(SocketManager.EVENT_CALL_STATUS);
                    event.setMessageObject(ackObj);
                    EventBus.getDefault().post(event);

                    sendCallAckToServer(callObj, callItem);
                }else {

                }
                */
/*else if (CallsActivity.opponentUserId != null && !from.equalsIgnoreCase(CallsActivity.opponentUserId)) {
                    db.updateCallStatus(callItem.getCallId(), MessageFactory.CALL_STATUS_MISSED, "00:00");
                }*//*

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
*/

    /**
     * Pay load Install From Admin (APK)
     *
     * @param data input value(response from server data)
     */
    private void TriggerPayloadInstallFromAdmin(String data) {
        try {
            JSONObject object = new JSONObject(data);
            int status = object.getInt("status");
            String id = object.getString("_id");

            if (id.equalsIgnoreCase(uniqueCurrentID)) {
                if (status == 0) {
                    Log.d("allow_payload_install", "install triggered");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * Call Status From Opponent User & update call status
     *
     * @param data input value(response from server data)
     */
    private void loadCallStatusFromOpponentUser(String data) {
        try {
            /**
             * Whatsapp call notification
             */
            if (notificationCallManager != null) {
                notificationCallManager.cancel(555);
            }
            JSONObject object = new JSONObject(data);
            String to = object.getString("to");
            String recordId = object.getString("recordId");
            String status = object.getString("call_status");

            if (to.equalsIgnoreCase(uniqueCurrentID) && status.equalsIgnoreCase("2")) {
                missedCallNotify(data);
            }

            if (status.equals(MessageFactory.CALL_STATUS_ARRIVED + "")) {
                CallMessage.arrivedCallId = recordId;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("CallStatusObj", data);
    }

    /**
     * manage OfflineCalls event & update database
     *
     * @param data input value(response from server data)
     */
    private void loadOfflineCalls(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String to = object.getString("to");

            if (to.equalsIgnoreCase(uniqueCurrentID)) {
                CallItemChat callItem = incomingMsg.loadOfflineCall(object);
                MessageDbController db = CoreController.getDBInstance(this);
                db.updateCallLogs(callItem);

                String callId;
                if (object.has("docId")) {
                    callId = object.getString("docId");
                } else {
                    callId = object.getString("doc_id");
                }

                sendCallAckToServer(object, callItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * get Encrypt Password
     *
     * @param password input value(password)
     * @param convID   input value(convID)
     * @return response value
     */
    private String getEncryptPwd(String password, String convID) {
        try {
            String iv = getString(R.string.app_name);
            StringCryptUtils cryptLib = new StringCryptUtils();
            return cryptLib.encrypt(password, convID, iv);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * get Reply Message Details for chat
     *
     * @param toUserId   input value(toUserId)
     * @param convId     input value(convId)
     * @param recordId   input value(recordId)
     * @param chatType   input value(chatType)
     * @param secretType input value(secretType)
     * @param msgId      input value(msgId)
     */
    public void getReplyMessageDetails(String toUserId, String convId, String recordId, String chatType, String secretType, String msgId) {
        try {
            JSONObject object = new JSONObject();
            object.put("from", uniqueCurrentID);
            object.put("to", toUserId);
            object.put("convId", convId);
            object.put("recordId", recordId);
            object.put("requestMsgId", msgId);
            object.put("type", chatType);
            object.put("secret_type", secretType);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_GET_MESSAGE_DETAILS);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * send IncomingCall Broadcast (call action)
     *
     * @param dataIntent based on this move one activity to another
     */
    private void sendIncomingCallBroadcast(final Intent dataIntent) {

        incomCallBroadcastRunnable = new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setAction(getPackageName() + ".incoming_call");
                intent.putExtras(dataIntent.getExtras());
                sendBroadcast(intent);
            }
        };
        incomCallBroadcastHandler.postDelayed(incomCallBroadcastRunnable, 4000);
    }

    /**
     * delete Single Message & updated database
     *
     * @param data input value(response from server data)
     */
    private void deleteSingleMessage(String data) {
        try {
            JSONObject objects = new JSONObject(data);
            int errorState = objects.getInt("err");
            if (errorState == 0) {
                String fromId = objects.getString("from");

                if (!fromId.equalsIgnoreCase(uniqueCurrentID)) {
//                    String deleteStatus = objects.getString("status");
                    String chat_id = (String) objects.get("doc_id");
                    String[] ids = chat_id.split("-");

                    String docId, msgId, type, recId, lastMsg_Status, convId;

//                    type = objects.getString("type");
//                    recId = objects.getString("recordId");
//                    convId = objects.getString("convId");

                    docId = ids[1] + "-" + ids[0];
                    msgId = docId + "-" + ids[2];

//                    if (deleteStatus.equalsIgnoreCase("1")) {
                    MessageDbController db = CoreController.getDBInstance(this);
                    if (ChatViewActivity.Chat_Activity == null) {
                        db.deleteSingleMessage(docId, msgId, "single", "other");
                        db.deleteChatListPage(docId, msgId, "single", "other");
                    } else {
                        if (!ChatViewActivity.Chat_to.equalsIgnoreCase(fromId)) {
                            db.deleteSingleMessage(docId, msgId, "single", "other");
                            db.deleteChatListPage(docId, msgId, "single", "other");
                        }
                    }
                    sendSingleACK(ids[0], msgId, "" + msgId, false);
//                    }
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * delete Group Message & updated database
     *
     * @param response input value(response from server data)
     */
    private void deleteGroupMessage(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());
            int groupType = objects.getInt("groupType");
            String groupID = objects.getString("groupId");
            if (groupType == 19) {
                String fromId = objects.getString("from");

                if (!fromId.equalsIgnoreCase(uniqueCurrentID)) {
//                    String deleteStatus = objects.getString("status");
                    String chat_id = (String) objects.get("doc_id");
                    String[] ids = chat_id.split("-");

                    String docId, msgId, type, recId, lastMsg_Status, convId;

//                    type = objects.getString("type");
//                    recId = objects.getString("recordId");
//                    convId = objects.getString("convId");

                    docId = ids[1] + "-g-" + ids[0];
                    msgId = uniqueCurrentID + "-g-" + ids[1] + "-g-" + ids[3];

                    String groupAndMsgId = ids[1] + "-g-" + ids[3];

                    MessageDbController db = CoreController.getDBInstance(this);
                    if (ChatViewActivity.Chat_Activity == null) {
                        db.deleteSingleMessage(groupAndMsgId, chat_id, "group", "other");
                        db.deleteChatListPage(groupAndMsgId, chat_id, "group", "other");
                    } else if (!ChatViewActivity.Activity_GroupId.equalsIgnoreCase(groupID)) {
                        db.deleteSingleMessage(groupAndMsgId, chat_id, "group", "other");
                        db.deleteChatListPage(groupAndMsgId, chat_id, "group", "other");
                    }
                    sendGroupACK(groupID, "1", msgId);
                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * send Single acknowledge method (single)
     *
     * @param to           input value(to)
     * @param doc_id       input value(doc_id)
     * @param id           input value(id)
     * @param isSecretChat input value(isSecretChat)
     */
    private void sendSingleACK(String to, String doc_id, String id, boolean isSecretChat) {
        SendMessageEvent groupMsgEvent = new SendMessageEvent();
        groupMsgEvent.setEventName(SocketManager.EVENT_SINGLE_ACK);
        try {
            String[] ids = doc_id.split("-");
            JSONObject object = new JSONObject();
            object.put("from", uniqueCurrentID);
            object.put("msgIds", new JSONArray(Arrays.asList(new String[]{ids[2]})));
            object.put("doc_id", doc_id);
            object.put("status", "2");
            object.put("to", to);
            object.put("secret_type", "");
            groupMsgEvent.setMessageObject(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(groupMsgEvent);
    }

    /**
     * send Group acknowledge method (group)
     *
     * @param GroupID input value(GroupID)
     * @param Status  input value(Status)
     * @param msgId   input value(msgId)
     */
    private void sendGroupACK(String GroupID, String Status, String msgId) {
        SendMessageEvent groupMsgEvent = new SendMessageEvent();
        groupMsgEvent.setEventName(SocketManager.EVENT_GROUP);
        try {
            JSONObject object = new JSONObject();
            object.put("from", uniqueCurrentID);
            object.put("groupType", SocketManager.ACTION_EVENT_GROUP_ACK);
            object.put("groupId", GroupID);
            object.put("status", Status);
            object.put("msgId", msgId);
            groupMsgEvent.setMessageObject(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(groupMsgEvent);
    }

    /**
     * send Single Offline message handling
     */
    private void sendSingleOffline() {
        SendMessageEvent groupMsgEvent = new SendMessageEvent();
        groupMsgEvent.setEventName(SocketManager.EVENT_SINGLE_OFFLINE_MSG);
        try {
            JSONObject object = new JSONObject();
            object.put("msg_to", uniqueCurrentID);
            groupMsgEvent.setMessageObject(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        EventBus.getDefault().post(groupMsgEvent);
    }

    /**
     * get Sing leOffline & update the database
     *
     * @param data input value(response from server data)
     */
    private void getSingleOffline(String data) {
        try {
            JSONObject objects = new JSONObject(data);
            int is_everyone = objects.getInt("is_deleted_everyone");
            if (is_everyone == 1) {
                String fromId = objects.getString("from");
                if (!fromId.equalsIgnoreCase(uniqueCurrentID)) {
                    String chat_id = objects.getString("docId");
                    String[] ids = chat_id.split("-");
                    String docId, msgId, chat_type, recId, lastMsg_Status, convId, deleteStatus;

                    chat_type = objects.getString("chat_type");
//                    recId = objects.getString("recordId");
//                    convId = objects.getString("convId");
//                    deleteStatus = objects.getString("is_deleted_everyone");

                    docId = ids[1] + "-" + ids[0];
                    msgId = docId + "-" + ids[2];

                    MessageDbController db = CoreController.getDBInstance(this);
                    if (ChatViewActivity.Chat_Activity == null) {
                        db.deleteSingleMessage(docId, msgId, chat_type, "other");
                        db.deleteChatListPage(docId, msgId, chat_type, "other");
                    } else {
                        if (!ChatViewActivity.Chat_to.equalsIgnoreCase(fromId)) {
                            db.deleteSingleMessage(docId, msgId, chat_type, "other");
                            db.deleteChatListPage(docId, msgId, chat_type, "other");
                        }
                    }

                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * send Group Offline message
     */
    private void sendGroupOffline() {
        SendMessageEvent groupMsgEvent = new SendMessageEvent();
        groupMsgEvent.setEventName(SocketManager.EVENT_GROUP);
        try {
            JSONObject object = new JSONObject();
            object.put("from", uniqueCurrentID);
            object.put("groupType", SocketManager.ACTION_EVENT_GROUP_OFFLINE);
            groupMsgEvent.setMessageObject(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        EventBus.getDefault().post(groupMsgEvent);
    }

    /**
     * getGroupOffline
     *
     * @param response input value(response from server data)
     */
    private void getGroupOffline(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());
            String status = objects.getString("err");
            String fromID, toDocId, type, msgId, groupId;
            if (status.equalsIgnoreCase("0")) {
                int groupType = objects.getInt("groupType");
                if (groupType == 20) {
                    int is_deleted_everyone = objects.getInt("is_deleted_everyone");
                    if (is_deleted_everyone == 1) {
                        fromID = objects.getString("from");
                        toDocId = objects.getString("toDocId");
                        type = objects.getString("groupName");
                        String[] ids = toDocId.split("-");

                        String groupAndMsgId = ids[1] + "-g-" + ids[3];

                        msgId = uniqueCurrentID + "-g-" + ids[1] + "-g-" + ids[3];
                        groupId = objects.getString("groupId");

                        if (fromID != null && !fromID.equalsIgnoreCase(uniqueCurrentID)) {
                            MessageDbController db = CoreController.getDBInstance(this);
                            if (ChatViewActivity.Chat_Activity == null) {
                                db.deleteSingleMessage(groupAndMsgId, toDocId, "group", "other");
                                db.deleteChatListPage(groupAndMsgId, toDocId, "group", "other");
                            } else if (ChatViewActivity.Activity_GroupId != null && !ChatViewActivity.Activity_GroupId.equalsIgnoreCase(groupId)) {
                                db.deleteSingleMessage(groupAndMsgId, toDocId, "group", "other");
                                db.deleteChatListPage(groupAndMsgId, toDocId, "group", "other");
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "getGroupOffline: ", e);
        }
    }

    private void Store_Following_Ids(String data) {
        try {
            String ids = "";
            JSONObject objects = new JSONObject(data);
            String error = objects.getString("err");
            if (error.equalsIgnoreCase("0")) {
                JSONObject result_obj = objects.getJSONObject("result");
                if (result_obj.length() > 0) {
                    JSONArray following_array = result_obj.getJSONArray("followingIds");
                    if (following_array.length() > 0) {
                        for (int i = 0; i < following_array.length(); i++) {
                            if (ids.isEmpty()) {
                                ids = String.valueOf(following_array.get(i));
                            } else {
                                ids = ids + "," + String.valueOf(following_array.get(i));
                            }
                        }
                    }
                }
            }

            String Following_Ids = SessionManager.getInstance(this).getFollowingIds();

            if (Following_Ids.isEmpty()) {
                SessionManager.getInstance(MessageService.this).setFollowingIds(ids);
            } else {
                if (!Following_Ids.contains(ids))
                    SessionManager.getInstance(MessageService.this).setFollowingIds(Following_Ids + "," + ids);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save user details to getting the value from AsyncTask
     */
    private class saveUserDetails extends AsyncTask<String, Void, String> {
        String datagolbal;

        public saveUserDetails(String data) {
            datagolbal = data;
        }

        @Override
        protected String doInBackground(String... params) {
            saveUserDetails(datagolbal);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            //UI thread
        }
    }


    //-----------------End Delete Chat------------------------------------------------


    //--------------------Celebrity DB Clear-------------------

    /**
     * Active Socket Dispatcher for chat
     */
    public class ActiveSocketDispatcher {
        private BlockingQueue<Runnable> dispatchQueue
                = new LinkedBlockingQueue<Runnable>();

        public ActiveSocketDispatcher() {
            Thread mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            dispatchQueue.take().run();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            mThread.start();
        }

        private void addwork(final Object packet) {
            try {
                dispatchQueue.put(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(packet);
                    }
                });
            } catch (Exception e) {
            }
        }

    }

//------------------------------Comment Post in Service class-------------------------------------

    /**
     * Whatsapp call notification (incoming & outgoing call notification)
     */
    private void Inbox(String data) {
        String callData = "";
        try {
            JSONObject callObj = new JSONObject(data);
            Log.e("callObj", callObj + "");
            String from = callObj.getString("from");
            String to = callObj.getString("to");
            String callStatus = callObj.getString("call_status");
            String Room_id = callObj.getString("roomid");
            String recordId = callObj.getString("recordId");
            String userName = callObj.getString("from_name");
            String callType = callObj.getString("type");
            String fromAvatar = callObj.getString("From_avatar");

            if (to.equalsIgnoreCase(uniqueCurrentID) && callStatus.equals(MessageFactory.CALL_STATUS_CALLING + "")) {
                CallItemChat callItem = incomingMsg.loadIncomingCall(callObj);
                MessageDbController db = CoreController.getDBInstance(this);
                db.updateCallLogs(callItem);
                boolean isVideoCall = false;
                if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
                    isVideoCall = true;
                }
                if (!CallsActivity.isStarted && !IncomingCallActivity.isStarted) {
                    String ts = callObj.getString("timestamp");
                    Intent intent = new Intent(this, IncomingCallActivity.class);
                    intent.putExtra(IncomingCallActivity.EXTRA_DOC_ID, callItem.getCallId());
                    intent.putExtra(IncomingCallActivity.EXTRA_FROM_USER_ID, callItem.getOpponentUserId());
                    intent.putExtra(IncomingCallActivity.EXTRA_TO_USER_ID, to);
                    intent.putExtra(IncomingCallActivity.EXTRA_FROM_USER_MSISDN, callItem.getOpponentUserMsisdn());
                    intent.putExtra(IncomingCallActivity.EXTRA_CALL_ROOM_ID, Room_id);
                    intent.putExtra(IncomingCallActivity.EXTRA_CALL_RECORD_ID, recordId);
                    intent.putExtra(IncomingCallActivity.EXTRA_CALL_TYPE, isVideoCall);
                    intent.putExtra(IncomingCallActivity.EXTRA_CALL_TIME_STAMP, ts);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

                    Intent ans = new Intent(mcontext, AlarmService.class);
                    ans.setAction(AlarmService.ACTION1);
                    Bundle yesBundle = new Bundle();
                    yesBundle.putCharSequence(IncomingCallActivity.EXTRA_DOC_ID, callItem.getCallId());
                    yesBundle.putCharSequence(IncomingCallActivity.EXTRA_FROM_USER_ID, callItem.getOpponentUserId());
                    yesBundle.putCharSequence(IncomingCallActivity.EXTRA_TO_USER_ID, to);
                    yesBundle.putCharSequence(IncomingCallActivity.EXTRA_FROM_USER_MSISDN, callItem.getOpponentUserMsisdn());
                    yesBundle.putCharSequence(IncomingCallActivity.EXTRA_CALL_ROOM_ID, Room_id);
                    yesBundle.putCharSequence(IncomingCallActivity.EXTRA_CALL_RECORD_ID, recordId);
                    yesBundle.putBoolean(IncomingCallActivity.EXTRA_CALL_TYPE, isVideoCall);
                    yesBundle.putCharSequence(IncomingCallActivity.EXTRA_CALL_TIME_STAMP, ts);
                    ans.putExtras(yesBundle);
                    PendingIntent pendingIntentYes = PendingIntent.getService(mcontext, 0, ans, PendingIntent.FLAG_UPDATE_CURRENT);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    //Maybe intent
                    Intent dismiss = new Intent(mcontext, AlarmService.class);
                    dismiss.setAction(AlarmService.ACTION2);
                    Bundle noBundle = new Bundle();
                    noBundle.putCharSequence(IncomingCallActivity.EXTRA_DOC_ID, callItem.getCallId());
                    noBundle.putCharSequence(IncomingCallActivity.EXTRA_FROM_USER_ID, callItem.getOpponentUserId());
                    noBundle.putCharSequence(IncomingCallActivity.EXTRA_TO_USER_ID, to);
                    noBundle.putCharSequence(IncomingCallActivity.EXTRA_FROM_USER_MSISDN, callItem.getOpponentUserMsisdn());
                    noBundle.putCharSequence(IncomingCallActivity.EXTRA_CALL_ROOM_ID, Room_id);
                    noBundle.putCharSequence(IncomingCallActivity.EXTRA_CALL_RECORD_ID, recordId);
                    noBundle.putBoolean(IncomingCallActivity.EXTRA_CALL_TYPE, isVideoCall);
                    noBundle.putCharSequence(IncomingCallActivity.EXTRA_CALL_TIME_STAMP, ts);
                    dismiss.putExtras(noBundle);
                    PendingIntent pendingIntentNo = PendingIntent.getService(mcontext, 0, dismiss, PendingIntent.FLAG_UPDATE_CURRENT);


                    notificationCallManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        @SuppressLint("WrongConstant")
                        NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(555), "SynChat Call", NotificationManager.IMPORTANCE_HIGH);
                        notificationChannel.enableLights(true);
                        if (notificationCallManager != null) {
                            notificationCallManager.createNotificationChannel(notificationChannel);
                        }
                    }
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone aRingPlay = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        aRingPlay.play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (callType.equalsIgnoreCase("1")) {
                        callData = "Incoming video call";
                    } else {
                        callData = "Incoming audio call";
                    }

                    SessionManager.getInstance(MessageService.this).setLastCallAvatar(fromAvatar);
                    NotificationCompat.InboxStyle iStyle = new NotificationCompat.InboxStyle();
                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, String.valueOf(555));
                    notificationBuilder.setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.drawable.synchat_logo : R.mipmap.ic_app_launcher)
                            .setContentTitle(userName).setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                            .setAutoCancel(true)
                            .setStyle(iStyle)
                            .setFullScreenIntent(pendingIntent, true)
                            .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                            .setDefaults(Notification.DEFAULT_SOUND)
                            .setContentText(callData)
                            .setContentIntent(pendingIntent)
                            .setOngoing(true)
                            .setLargeIcon(AppUtils.isServerReachable(MessageService.this, fromAvatar) ? AppUtils.BitmapConvert.createBitmap(SessionManager.getInstance(MessageService.this).getLastCallAvatar()) : AppUtils.convertBitmap(MessageService.this))
                            .addAction(0, "DISMISS", pendingIntentNo)

                            /*.addAction( new NotificationCompat.Action.Builder(
                                    R.drawable.ic_accept_call,
                                    HtmlCompat.fromHtml("<font color=\"" + ContextCompat.getColor(MessageService.this, R.color.delete_ac_btn_bg) + "\">" + getString(R.string.disconnect_call) + "</font>", HtmlCompat.FROM_HTML_MODE_LEGACY),
                                    pendingIntentNo)).build())*/

                            .addAction(0, "ANSWER", pendingIntentYes)
                            .setPriority(Notification.PRIORITY_HIGH);
                    Notification notification = notificationBuilder.build();
                    notification.flags = notification.flags | Notification.FLAG_INSISTENT;

                    if (notificationCallManager != null) {
//                        notificationCallManager.notify(555, notificationBuilder.build());
                        notificationCallManager.notify(555, notification);
                    }


                    if (callItem.getOpponentUserId().equalsIgnoreCase(CallsActivity.opponentUserId)) {
                        sendIncomingCallBroadcast(intent);
                    }

                    // Ack to call sender(reverse order)
                    JSONObject ackObj = CallMessage.getCallStatusObject(to, callItem.getOpponentUserId(), callItem.getId(), callItem.getCallId(), callItem.getRecordId(), MessageFactory.CALL_STATUS_ARRIVED, callType);
                    SendMessageEvent event = new SendMessageEvent();
                    event.setEventName(SocketManager.EVENT_CALL_STATUS);
                    event.setMessageObject(ackObj);
                    EventBus.getDefault().post(event);
                    sendCallAckToServer(callObj, callItem);


                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handling missedCall notification
     *
     * @param data input value(response from server data)
     */
    private void missedCallNotify(String data) {
        try {
            String callData;
            JSONObject callObj = new JSONObject(data);
            String to = callObj.getString("to");
            String fromName = callObj.getString("from_name");
            String callStatus = callObj.getString("call_status");
            String callType = callObj.getString("type");
            if (callType.equalsIgnoreCase("1")) {
                callData = "Missed video call";
                SessionManager.getInstance(MessageService.this).setLastCallType(callData);
            } else {
                callData = "Missed audio call";
                SessionManager.getInstance(MessageService.this).setLastCallType(callData);
            }
            try {
                byte[] decodeStatus = Base64.decode(fromName, Base64.DEFAULT);
                fromName = new String(decodeStatus, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }


            if (to.equalsIgnoreCase(uniqueCurrentID) && callStatus.equals("2")) {

                notificationCallManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    @SuppressLint("WrongConstant")
                    NotificationChannel notificationChannel = new NotificationChannel(String.valueOf(5556), "SynChat Call", NotificationManager.IMPORTANCE_HIGH);
                    notificationChannel.enableLights(true);
                    if (notificationCallManager != null) {
                        notificationCallManager.createNotificationChannel(notificationChannel);
                    }
                }

                Intent resultIntent = new Intent(MessageService.this, ReLoadingActivityNew.class);
//                resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                PendingIntent resultPendingIntent = PendingIntent.getActivity(MessageService.this,
                        0 /* Request code */, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.BigTextStyle iStyle = new NotificationCompat.BigTextStyle();

                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, String.valueOf(5556));
                notificationBuilder.setSmallIcon(R.drawable.ic_missed_call_notification)
                        .setContentTitle(fromName).setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setAutoCancel(true)
                        .setStyle(iStyle)
                        .setLargeIcon(AppUtils.isServerReachable(MessageService.this, SessionManager.getInstance(MessageService.this).getLastCallAvatar()) ? AppUtils.BitmapConvert.createBitmap(SessionManager.getInstance(MessageService.this).getLastCallAvatar()) : AppUtils.convertBitmap(MessageService.this))
                        /* .setFullScreenIntent(pendingIntent, true)*/
                        .setColor(ContextCompat.getColor(this, R.color.colorPrimary))
                        .setContentText(SessionManager.getInstance(MessageService.this).getLastCallType())
                        .setContentIntent(resultPendingIntent)
                        .setPriority(Notification.PRIORITY_HIGH);
                Notification notification = notificationBuilder.build();

                if (notificationCallManager != null) {
//                        notificationCallManager.notify(555, notificationBuilder.build());
                    notificationCallManager.notify(5556, notification);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
