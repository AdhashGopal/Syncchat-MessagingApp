package com.chatapp.android.core.message;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.chatapp.android.app.utils.GroupInfoSession;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.ShortcutBadgeManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.model.GroupInfoPojo;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.socket.MessageService;
import com.chatapp.android.core.socket.SocketManager;
import com.chatapp.android.core.uploadtoserver.FileUploadDownloadManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 *
 */
public class MesssageObjectReceiver {
    private ActiveSessionDispatcher dispatcher;
    Context context;
    HashMap<String, MessageItemChat> uniqueQue = new HashMap<>();
    private ShortcutBadgeManager shortcutBadgeMgnr;
    private GroupInfoSession groupInfoSession;
    private UserInfoSession userInfoSession;
    private String mCurrentUserId;
    private FileUploadDownloadManager uploadDownloadManager;
    private IncomingMessage incomingMsg;
    private EventBus eventBus;
    private Session session;
    private String connected;
    private boolean wifiEnabled = false;
    private SocketManager mSocketManager;
    private MessageService callBack;

    /**
     * create constructor
     *
     * @param context current activity
     */
    public MesssageObjectReceiver(MessageService context) {
        this.context = context;
        this.callBack = context;

        session = new Session(context);
        uploadDownloadManager = new FileUploadDownloadManager(context);
        dispatcher = new ActiveSessionDispatcher();
        shortcutBadgeMgnr = new ShortcutBadgeManager(context);
        groupInfoSession = new GroupInfoSession(context);
        userInfoSession = new UserInfoSession(context);
        mCurrentUserId = SessionManager.getInstance(context).getCurrentUserID();
        incomingMsg = new IncomingMessage(context);
        incomingMsg.setCallback(callBack);
        mSocketManager = new SocketManager(context, new SocketManager.SocketCallBack() {
            @Override
            public void onSuccessListener(String eventName, Object... response) {

            }
        });
    }

    /**
     * set value for Eventbus to pass the data
     *
     * @param eventBus set the value's
     */
    public void setEventBusObject(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    /**
     * store Group Message In DataBase based on response
     *
     * @param response based on response type to call specific method.
     */
    public void storeGroupMsgInDataBase(Object[] response) {
        try {
            JSONObject objects = new JSONObject(response[0].toString());
            Integer type = Integer.parseInt(objects.getString("type"));

            switch (type) {
                case MessageFactory.join_new_group:
                    loadNewGroupMessage(objects);
                    Log.d("GroupEvent___new", objects.toString());
                    break;

                case MessageFactory.add_group_member:
                    joinExistingGroupMessage(objects);
                    Log.d("GroupEvent___joinexist", objects.toString());
                    break;

                case MessageFactory.change_group_icon:
                    loadGroupDpChangeMessage(objects);
                    Log.d("GroupEvent___changeicon", objects.toString());
                    break;

                case MessageFactory.delete_member_by_admin:
                    loadOfflineDeleteMemberMessage(objects);
                    Log.d("GroupEvent___deletemem", objects.toString());
                    break;

                case MessageFactory.change_group_name:
                    loadChangeGroupNameMessage(objects);
                    Log.d("GroupEvent___changename", objects.toString());
                    break;

                case MessageFactory.make_admin_member:
                    loadOfflineMakeAdminMessage(objects);
                    Log.d("GroupEvent___makeadmin", objects.toString());
                    break;

                case MessageFactory.exit_group:
                    loadOfflineExitGroupMessage(objects);
                    Log.d("GroupEvent___exitgroup", objects.toString());
                    break;

                default:
                    loadGroupMessage(objects);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exit Group from Offline Message chat
     *
     * @param object based on response manage delivery status & update database
     */
    private void loadOfflineExitGroupMessage(JSONObject object) {
        try {
            String err = object.getString("err");
            if (err.equals("0")) {
                String from = object.getString("from");
                String msgId = object.getString("id");
                String groupId = object.getString("groupId");
                String groupName = object.getString("groupName");

                String senderOriginalName = "";

                if (object.has("fromuser_name")) {

                    try {
                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }

                GroupInfoPojo infoPojo = new GroupInfoPojo();

                String docId = mCurrentUserId + "-" + groupId + "-g";
                if (groupInfoSession.hasGroupInfo(docId)) {
                    infoPojo = groupInfoSession.getGroupInfo(docId);
                }

                String group_mem = infoPojo.getGroupMembers();
                if (group_mem != null) {
                    group_mem = group_mem.replace(from, "");
                    group_mem = group_mem.replace(",,", ",");
                    infoPojo.setGroupMembers(group_mem);
                }


                if (from.equalsIgnoreCase(mCurrentUserId)) {
                    infoPojo.setLiveGroup(false);
                    callBack.removeUser(groupId);
                } else {
                    getUserDetailsNotExists(from);
                }

                groupInfoSession.updateGroupInfo(docId, infoPojo);
                //For update latest group details
                callBack.getGroupDetails(groupId);

                GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
                MessageItemChat item = message.createMessageItem(MessageFactory.exit_group, false, null,
                        MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, from, null, senderOriginalName);
                item.setMessageId(docId.concat("-").concat(msgId));

                if (object.has("timestamp")) {
                    String ts = object.getString("timestamp");
                    item.setTS(ts);
                }

                MessageDbController db = CoreController.getDBInstance(context);
                db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);

                sendGroupAckToServer(mCurrentUserId, groupId, msgId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * join Existing Group Message
     *
     * @param object based on response manage delivery status & update database
     */
    private void joinExistingGroupMessage(JSONObject object) {
        try {
            String err = object.getString("err");
            if (err.equals("0")) {
                String msgId = object.getString("id");
                String createdBy = object.getString("createdBy");
                String groupId = object.getString("groupId");
                String groupAvatar = object.getString("profilePic");
                String groupName = object.getString("groupName");
                String groupMembers = object.getString("groupMembers");
                String admin = object.getString("admin");
                String timeStamp = object.getString("timeStamp");
                String createdTo = object.getString("createdTo");

                String senderOriginalName = "";

                if (object.has("fromuser_name")) {

                    try {
                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }

                try {
                    JSONObject createdToObj = new JSONObject(createdTo);
                    createdTo = createdToObj.getString("_id");
                } catch (JSONException e) {

                }

                // get user details if not available in local db
                getUserDetailsNotExists(createdBy);
                getUserDetailsNotExists(createdTo);

                String docId = mCurrentUserId + "-" + groupId + "-g";

                GroupInfoPojo infoPojo = new GroupInfoPojo();
                infoPojo.setCreatedBy(createdBy);
                infoPojo.setAvatarPath(groupAvatar);
                infoPojo.setGroupName(groupName);
                infoPojo.setGroupMembers(groupMembers);
                infoPojo.setAdminMembers(admin);
                infoPojo.setLiveGroup(true);
                groupInfoSession.updateGroupInfo(docId, infoPojo);

                if (createdTo.equalsIgnoreCase(mCurrentUserId)) {
                    callBack.createGroup(groupId);
                    callBack.getGroupDetails(groupId);

                    SendMessageEvent event = new SendMessageEvent();
                    event.setEventName(SocketManager.EVENT_GROUP);
                    JSONObject groupObj = new JSONObject();
                    groupObj.put("groupType", SocketManager.ACTION_JOIN_NEW_GROUP);
                    groupObj.put("from", mCurrentUserId);
                    groupObj.put("groupId", groupId);
                    groupObj.put("createdBy", createdBy);
                    groupObj.put("groupName", groupName);
                    groupObj.put("timeStamp", msgId);
                    groupObj.put("id", msgId);
                    event.setMessageObject(groupObj);
                    eventBus.post(event);
                }

                /*long deviceTS = Calendar.getInstance().getTimeInMillis();
                long timeDiff = SessionManager.getInstance(context).getServerTimeDifference();
                long localTime = deviceTS - timeDiff;*/

                GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
                MessageItemChat item = message.createMessageItem(MessageFactory.add_group_member, false, null,
                        MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, createdBy, createdTo, senderOriginalName);
                item.setMessageId(docId.concat("-").concat(msgId));
                item.setTS(msgId);

                MessageDbController db = CoreController.getDBInstance(context);
                db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);


                sendGroupAckToServer(mCurrentUserId, groupId, msgId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * load New Group Message
     *
     * @param object based on response manage to join new group & update database
     */
    private void loadNewGroupMessage(JSONObject object) {
        try {
            String err = object.getString("err");
            if (err.equals("0")) {
                String msgId = object.getString("id");
                String createdBy = object.getString("createdBy");
                String groupId = object.getString("groupId");
                String groupAvatar = object.getString("profilePic");
                String groupName = object.getString("groupName");
                String groupMembers = object.getString("groupMembers");
                String admin = object.getString("admin");
                String timeStamp = object.getString("timeStamp");

                String senderOriginalName = "";

                if (object.has("fromuser_name")) {

                    try {
                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }

                // get user details if not available in local db
                getUserDetailsNotExists(createdBy);

                String docId = mCurrentUserId + "-" + groupId + "-g";
                GroupEventInfoMessage createdMsg = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
                MessageItemChat createdMsgItem = createdMsg.createMessageItem(MessageFactory.join_new_group, false,
                        null, MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, createdBy, null, senderOriginalName);
                String createdTS = String.valueOf(Long.parseLong(timeStamp) - 10);
                createdMsgItem.setTS(createdTS);
                String createdMsgId = String.valueOf(Long.parseLong(msgId) - 10);
                createdMsgItem.setMessageId(docId.concat("-").concat(createdMsgId));

                /*GroupEventInfoMessage addedMsg = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
                MessageItemChat addedMsgItem = addedMsg.createMessageItem(MessageFactory.add_group_member, false, null,
                        MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, createdBy, mCurrentUserId);
                addedMsgItem.setMessageId(docId.concat("-").concat(msgId));*/

                MessageDbController db = CoreController.getDBInstance(context);
                db.updateChatMessage(createdMsgItem, MessageFactory.CHAT_TYPE_GROUP);
//                db.updateChatMessage(docId, addedMsgItem);

                GroupInfoPojo infoPojo = new GroupInfoPojo();
                infoPojo.setCreatedBy(createdBy);
                infoPojo.setAvatarPath(groupAvatar);
                infoPojo.setGroupName(groupName);
                infoPojo.setGroupMembers(groupMembers);
                infoPojo.setAdminMembers(admin);
                infoPojo.setLiveGroup(true);
                groupInfoSession.updateGroupInfo(docId, infoPojo);

                callBack.createGroup(groupId);
                callBack.getGroupDetails(groupId);

                SendMessageEvent event = new SendMessageEvent();
                event.setEventName(SocketManager.EVENT_GROUP);
                JSONObject groupObj = new JSONObject();
                groupObj.put("groupType", SocketManager.ACTION_JOIN_NEW_GROUP);
                groupObj.put("from", mCurrentUserId);
                groupObj.put("groupId", groupId);
                groupObj.put("createdBy", createdBy);
                groupObj.put("groupName", groupName);
                groupObj.put("timeStamp", timeStamp);
                groupObj.put("id", msgId);
                event.setMessageObject(groupObj);
                eventBus.post(event);

                sendGroupAckToServer(mCurrentUserId, groupId, msgId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * load Group Message for uploading file and manage the data
     *
     * @param objects based on response manage delivery status & update database
     */
    private void loadGroupMessage(JSONObject objects) {
        MessageDbController db = CoreController.getDBInstance(context);

        try {
            String from = objects.getString("from");
            String groupId = objects.getString("groupId");

            if (from.equalsIgnoreCase(mCurrentUserId)) {
                String msgId = objects.getString("toDocId");

                String tempDocId = from + "-" + groupId + "-g";
                MessageItemChat tempItem = db.getParticularMessage(msgId);
                if (tempItem != null) {
                    groupMessageRes(objects);
                } else {
                    MessageItemChat msgItem = incomingMsg.loadGroupMessageFromWeb(objects);
                    db.updateChatMessage(msgItem, MessageFactory.CHAT_TYPE_GROUP);
                }
            } else {
                MessageItemChat msgItem = incomingMsg.loadGroupMessage(objects);
//                String docId = mCurrentUserId + "-" + msgItem.getReceiverID() + "-g";

                if (msgItem.getGroupMsgFrom() != null) {
                    if (!msgItem.getGroupMsgFrom().equalsIgnoreCase(mCurrentUserId)) {
                        msgItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);
                        changeBadgeCount(groupId);
                        sendGroupAckToServer(mCurrentUserId, msgItem.getReceiverID(), msgItem.get_id());
                    }
                }


                chkStatus();
                chkmobiledataon();
                /*Boolean dataroaming = isDataRoamingEnabled();
                if (dataroaming) {
                    String[] values = session.getromingPrefsName().split(",");
                    for (int i = 0; i < values.length; i++) {
                        if (values[i].equalsIgnoreCase("photo")) {
                            if (MessageFactory.picture == Integer.parseInt(msgItem.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), msgItem, false);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Audio")) {
                            if (MessageFactory.audio == Integer.parseInt(msgItem.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), msgItem, false);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Videos")) {
                            if (MessageFactory.video == Integer.parseInt(msgItem.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), msgItem, false);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Documents")) {
                            if (MessageFactory.document == Integer.parseInt(msgItem.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), msgItem, false);
                            }
                        }
                    }
                }*/
                if (connected.equalsIgnoreCase("CONNECTED")) {
                    String[] values = session.getmobiledataPrefsName().split(",");
                    for (int i = 0; i < values.length; i++) {
                        if (values[i].equalsIgnoreCase("photo")) {
                            if (MessageFactory.picture == Integer.parseInt(msgItem.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), msgItem, false);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Audio")) {
                            if (MessageFactory.audio == Integer.parseInt(msgItem.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), msgItem, false);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Videos")) {
                            if (MessageFactory.video == Integer.parseInt(msgItem.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), msgItem, false);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Documents")) {
                            if (MessageFactory.document == Integer.parseInt(msgItem.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), msgItem, false);
                            }
                        }
                    }

                }
                if (wifiEnabled) {
                    String[] values = session.getwifiPrefsName().split(",");
                    for (int i = 0; i < values.length; i++) {
                        if (values[i].equalsIgnoreCase("photo")) {
                            if (MessageFactory.picture == Integer.parseInt(msgItem.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), msgItem, false);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Audio")) {
                            if (MessageFactory.audio == Integer.parseInt(msgItem.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), msgItem, false);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Videos")) {
                            if (MessageFactory.video == Integer.parseInt(msgItem.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), msgItem, false);
                            }
                        }
                        if (values[i].equalsIgnoreCase("Documents")) {
                            if (MessageFactory.document == Integer.parseInt(msgItem.getMessageType())) {
                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), msgItem, false);
                            }
                        }
                    }

                }

                db.updateChatMessage(msgItem, MessageFactory.CHAT_TYPE_GROUP);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public Boolean isDataRoamingEnabled() {
        try {
            // return true or false if data roaming is enabled or not
            return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.DATA_ROAMING) == 1;
        } catch (Settings.SettingNotFoundException e) {
            // return null if no such settings exist (device with no radio data ?)
            return null;
        }
    }

    public void chkStatus() {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        wifiEnabled = wifiManager.isWifiEnabled();
    }

    public void chkmobiledataon() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mobileInfo =
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        connected = mobileInfo.getState().toString();
    }

    private void groupMessageRes(JSONObject jsonObject) {

        try {
            String from = jsonObject.getString("from");
            String groupId = jsonObject.getString("groupId");
            String doc_id;
            if (jsonObject.has("toDocId")) {
                doc_id = jsonObject.getString("toDocId");
            } else {
                doc_id = jsonObject.getString("doc_id");
            }
            String deliver = jsonObject.getString("deliver");
            String recordId = jsonObject.getString("recordId");
            String type = jsonObject.getString("type");
            String timeStamp = jsonObject.getString("timestamp");

            if (type.equalsIgnoreCase(MessageFactory.text + "")) {
                String message = jsonObject.getString("message");
                long messagelength = session.getsentmessagelength() + message.length();
                session.putsentmessagelength(messagelength);
                int sentmesagecount = session.getsentmessagecount();
                sentmesagecount = sentmesagecount + 1;
                session.putsentmessagecount(sentmesagecount);
            } else if (type.equalsIgnoreCase(MessageFactory.picture + "") || type.equalsIgnoreCase(MessageFactory.audio + "") || type.equalsIgnoreCase(MessageFactory.video + "")) {
                long filesize = Long.parseLong(jsonObject.getString("filesize"));
                filesize = session.getsentmedialength() + filesize;
                session.putsentmedialength(filesize);
            }
            MessageDbController db = CoreController.getDBInstance(context);
            db.updateChatMessage(from + "-" + groupId + "-g", doc_id, deliver, recordId,
                    groupId, timeStamp);
            db.deleteSendNewMessage(doc_id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * getting update Group Message Status
     *
     * @param objects based on response getting the value and update the database
     */
    public void updateGroupMsgStatus(JSONObject objects) {
        try {
            String err = objects.getString("err");
            if (err.equalsIgnoreCase("0")) {
                String from = objects.getString("from");
                String groupId = objects.getString("groupId");
                String msgId = objects.getString("msgId");
                String deliverStatus = objects.getString("status");
                String timeStamp = objects.getString("currenttime");

                String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
                MessageDbController db = CoreController.getDBInstance(context);
                db.updateGroupMessageStatus(docId, docId.concat("-").concat(msgId), deliverStatus,
                        timeStamp, from, mCurrentUserId);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    /**
     * Exit group from chat Message
     *
     * @param object based on response getting the value and update the database
     */
    public void loadExitMessage(JSONObject object) {
        try {

            String err = object.getString("err");

            if (err.equalsIgnoreCase("0")) {
                String msgId = object.getString("id");
                String groupId = object.getString("groupId");
                String timeStamp = object.getString("timeStamp");
                String from = object.getString("from");

                String senderOriginalName = "";

                if (object.has("fromuser_name")) {

                    try {
                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }

                String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
                GroupInfoPojo groupInfoPojo = groupInfoSession.getGroupInfo(docId);

                if (groupInfoPojo == null) {
                    groupInfoPojo = new GroupInfoPojo();
                }

                String group_mem = groupInfoPojo.getGroupMembers();
                if (group_mem != null) {
                    group_mem = group_mem.replace(from, "");
                    group_mem = group_mem.replace(",,", ",");
                    groupInfoPojo.setGroupMembers(group_mem);
                }
                String groupName = groupInfoPojo.getGroupName();


                if (from.equalsIgnoreCase(mCurrentUserId)) {
                    groupInfoPojo.setIsAdminUser("0");
                    groupInfoPojo.setLiveGroup(false);
                    groupInfoSession.updateGroupInfo(docId, groupInfoPojo);
                } else {
                    getUserDetailsNotExists(from);
                }

                GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
                MessageItemChat item = message.createMessageItem(MessageFactory.exit_group, false, null,
                        MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, from, null, senderOriginalName);
                item.setMessageId(docId.concat("-").concat(msgId));
                item.setTS(timeStamp);

                MessageDbController db = CoreController.getDBInstance(context);
                db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                sendGroupAckToServer(mCurrentUserId, groupId, msgId);
                callBack.getGroupDetails(groupId);
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Change Group Name in chat Message
     *
     * @param object based on response getting the value and update the database
     */
    public void loadChangeGroupNameMessage(JSONObject object) {

        try {
            String groupId = object.getString("groupId");
            String id = object.getString("id");
            String groupPrevName = object.getString("prev_name");
            String groupNewName = object.getString("groupName");
            String from = object.getString("from");

            String senderOriginalName = "";

            if (object.has("fromuser_name")) {

                try {
                    senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
            String timeStamp;
            if (object.has("timeStamp")) {
                timeStamp = object.getString("timeStamp");
            } else {
                timeStamp = object.getString("timestamp");
            }

            if (object.has("changed_name")) {
                groupNewName = object.getString("changed_name");
            }
            getUserDetailsNotExists(from);

            String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");

            GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
            MessageItemChat item = message.createMessageItem(MessageFactory.change_group_name, false, null,
                    MessageFactory.DELIVERY_STATUS_READ, groupId, groupNewName, from, null, senderOriginalName);
            item.setPrevGroupName(groupPrevName);
            item.setMessageId(docId.concat("-").concat(id));
            item.setTS(timeStamp);

            MessageDbController db = CoreController.getDBInstance(context);
            db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);

            boolean hasGroupInfo = groupInfoSession.hasGroupInfo(docId);
            GroupInfoPojo infoPojo;
            if (hasGroupInfo) {
                infoPojo = groupInfoSession.getGroupInfo(docId);
            } else {
                infoPojo = new GroupInfoPojo();
            }
            infoPojo.setGroupId(groupId);
            infoPojo.setGroupName(groupNewName);
            groupInfoSession.updateGroupInfo(docId, infoPojo);

            sendGroupAckToServer(mCurrentUserId, groupId, id);

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * load Make Admin Message in chat
     *
     * @param object based on response getting the value and update the database
     */
    public void loadMakeAdminMessage(JSONObject object) {

        try {
            String err = object.getString("err");

            if (err.equalsIgnoreCase("0")) {

                String groupId = object.getString("groupId");
                String msgId = object.getString("id");
                String timeStamp = object.getString("timeStamp");
                String senderOriginalName = "";

                if (object.has("fromuser_name")) {

                    try {
                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }
//                    String toDocId = object.getString("toDocId");
                String from = object.getString("from");
                String groupName = "";
                if (object.has("groupName")) {
                    groupName = object.getString("groupName");
                }

                String newAdminUserId = object.getString("adminuser");
                String adminMembers = object.getString("admin");
//                    String newAdminMsisdn = object.getString("newadminmsisdn");

                getUserDetailsNotExists(newAdminUserId);

                String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
                if (infoPojo == null) {
                    infoPojo = new GroupInfoPojo();
                } else {
                    groupName = infoPojo.getGroupName();
                }

                if (adminMembers == null || adminMembers.equals("")) {
                    adminMembers = infoPojo.getAdminMembers().concat(",").concat(newAdminUserId);
                }
                infoPojo.setAdminMembers(adminMembers);
                groupInfoSession.updateGroupInfo(docId, infoPojo);

                if (infoPojo.isLiveGroup()) {
                    GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
                    MessageItemChat item = message.createMessageItem(MessageFactory.make_admin_member, false, null,
                            MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, from, newAdminUserId, senderOriginalName);
                    item.setMessageId(docId.concat("-").concat(msgId));
                    item.setTS(timeStamp);

                    MessageDbController db = CoreController.getDBInstance(context);
                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                    sendGroupAckToServer(mCurrentUserId, groupId, msgId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * load Offline Make Admin Message in chat
     *
     * @param object based on response getting the value and update the database
     */
    private void loadOfflineMakeAdminMessage(JSONObject object) {
        try {
            String err = object.getString("err");

            if (err.equalsIgnoreCase("0")) {
                String msgId = object.getString("id");
                String groupId = object.getString("groupId");
                String newAdminId = object.getString("createdTo");
                String timeStamp = object.getString("timestamp");
//                String toDocId = object.getString("toDocId");
                String from = object.getString("from");
                String groupName = object.getString("groupName");

                String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);

                String senderOriginalName = "";

                if (object.has("fromuser_name")) {

                    try {
                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }

                getUserDetailsNotExists(from);
                getUserDetailsNotExists(newAdminId);

                if (infoPojo == null) {
                    infoPojo = new GroupInfoPojo();
                }
                String adminMembers = infoPojo.getAdminMembers();
                adminMembers = adminMembers + "'" + newAdminId;
                infoPojo.setAdminMembers(adminMembers);
                groupInfoSession.updateGroupInfo(docId, infoPojo);

                if (infoPojo.isLiveGroup()) {
                    GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
                    MessageItemChat item = message.createMessageItem(MessageFactory.make_admin_member, false, null,
                            MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, from, newAdminId, senderOriginalName);
                    item.setMessageId(docId.concat("-").concat(msgId));
                    item.setTS(timeStamp);
                    MessageDbController db = CoreController.getDBInstance(context);
                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);

                    sendGroupAckToServer(mCurrentUserId, groupId, msgId);
                    callBack.getGroupDetails(groupId);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete Member in chat Message
     *
     * @param object based on response getting the value and update the database
     */
    public void loadDeleteMemberMessage(JSONObject object) {

        try {
            String err = object.getString("err");

            if (err.equalsIgnoreCase("0")) {
//                String msg = object.getString("message");
                String msgId = object.getString("id");
                String groupId = object.getString("groupId");
                String timeStamp = object.getString("timeStamp");
                String toDocId = object.getString("toDocId");
                String from = object.getString("from");
                String removeId = object.getString("removeId");


                String senderOriginalName = "";

                if (object.has("fromuser_name")) {

                    try {
                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }
//                String fromMsisdn = object.getString("from_msisdn");
//                String removeMsisdn = object.getString("remove_msisdn");
                String groupName = "";
                if (object.has("groupName")) {
                    groupName = object.getString("groupName");
                }

                getUserDetailsNotExists(from);
                getUserDetailsNotExists(removeId);

                String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);

                if (infoPojo == null) {
                    infoPojo = new GroupInfoPojo();
                    infoPojo.setGroupName(groupName);
                    infoPojo.setGroupId(groupId);

                } else {
                    groupName = infoPojo.getGroupName();
                    String group_mem = infoPojo.getGroupMembers();
                    if (group_mem != null) {
                        group_mem = group_mem.replace(removeId, "");
                        group_mem = group_mem.replace(",,", ",");
                        infoPojo.setGroupMembers(group_mem);
                    }
                }

                if (removeId.equalsIgnoreCase(mCurrentUserId)) {
                    infoPojo.setLiveGroup(false);
                    infoPojo.setIsAdminUser("0");
//                    callBack.removeUser(groupId);
//                    mSocketManager.disconnect();
//                    mSocketManager.connect();
                    removeGroup(groupId);
                }

                groupInfoSession.updateGroupInfo(docId, infoPojo);

                GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
                MessageItemChat item = message.createMessageItem(MessageFactory.delete_member_by_admin, false, null, MessageFactory.DELIVERY_STATUS_READ,
                        groupId, groupName, from, removeId, senderOriginalName);
                item.setMessageId(docId.concat("-").concat(msgId));
                item.setTS(timeStamp);
                MessageDbController db = CoreController.getDBInstance(context);
                db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);

                sendGroupAckToServer(mCurrentUserId, groupId, msgId);
                callBack.getGroupDetails(groupId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void removeGroup(String groupId) {
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_LEAVE_GROUP);
        JSONObject object = new JSONObject();
        try {
            object.put("groupId", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageEvent.setMessageObject(object);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * Offline Delete Member in chat Message
     *
     * @param object based on response getting the value and update the database
     */
    public void loadOfflineDeleteMemberMessage(JSONObject object) {

        try {
            String err = object.getString("err");

            if (err.equalsIgnoreCase("0")) {
                String msgId = object.getString("id");
                String groupId = object.getString("groupId");
                String removeId = object.getString("createdTo");
                String timeStamp = object.getString("timestamp");
//                String toDocId = object.getString("toDocId");
                String from = object.getString("from");
                String groupName = object.getString("groupName");
                String senderOriginalName = "";

                if (object.has("fromuser_name")) {

                    try {
                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }

                String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);

                if (infoPojo != null) {
                    groupName = infoPojo.getGroupName();
                } else {
                    infoPojo = new GroupInfoPojo();
                    infoPojo.setGroupId(groupId);
                    infoPojo.setIsAdminUser("0");
                }

                // get user details if not available in local db
                getUserDetailsNotExists(from);
                getUserDetailsNotExists(removeId);

                if (removeId.equalsIgnoreCase(mCurrentUserId)) {
                    infoPojo.setIsAdminUser("0");
                    infoPojo.setLiveGroup(false);
                    callBack.removeUser(groupId);
                    groupInfoSession.updateGroupInfo(docId, infoPojo);
                }

                GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
                MessageItemChat item = message.createMessageItem(MessageFactory.delete_member_by_admin, false, null,
                        MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, from, removeId, senderOriginalName);
                item.setMessageId(docId.concat("-").concat(msgId));
                item.setTS(timeStamp);

                MessageDbController db = CoreController.getDBInstance(context);
                db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);

                sendGroupAckToServer(mCurrentUserId, groupId, msgId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Change Group Dp in chat Message
     *
     * @param object based on response getting the value and update the database
     */
    public void loadGroupDpChangeMessage(JSONObject object) {
        try {
            String from = object.getString("from");
            String groupId = object.getString("groupId");
            String groupDp;
            if (object.has("avatar")) {
                groupDp = object.getString("avatar");
            } else {
                groupDp = object.getString("thumbnail");
            }
            String groupName = object.getString("groupName");
            String senderOriginalName = "";

            if (object.has("fromuser_name")) {

                try {
                    senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }

            String timeStamp;
            if (object.has("timeStamp")) {
                timeStamp = object.getString("timeStamp");
            } else {
                timeStamp = object.getString("timestamp");
            }

            // get user details if not available in local db
            getUserDetailsNotExists(from);

            String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");

            GroupInfoPojo groupInfo = groupInfoSession.getGroupInfo(docId);
            groupInfo.setAvatarPath(groupDp);
            groupInfoSession.updateGroupInfo(docId, groupInfo);

            GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
            MessageItemChat item = message.createMessageItem(MessageFactory.change_group_icon, false, null,
                    MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, from, null, senderOriginalName);
            item.setAvatarImageUrl(groupDp);
            item.setTS(timeStamp);

            String id;
            if (object.has("id")) {
                id = object.getString("id");
                sendGroupAckToServer(mCurrentUserId, groupId, id);
            } else {
                id = Calendar.getInstance().getTimeInMillis() + "";
            }
            item.setMessageId(docId.concat("-").concat(id));

            MessageDbController db = CoreController.getDBInstance(context);
            db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add Member in group chat Message
     *
     * @param object based on response getting the value and update the database
     */
    public void loadAddMemberMessage(JSONObject object) {
        try {
            String msg;/* = object.getString("message");*/
            String err = object.getString("err");
            String groupId = object.getString("groupId");
            String msgId = object.getString("id");
//            String timeStamp = object.getString("timeStamp");
            String from = object.getString("from");
//            String msisdn = object.getString("msisdn");
            String groupName = "";
            if (object.has("groupName")) {
                groupName = object.getString("groupName");
            }

            String senderOriginalName = "";

            if (object.has("fromuser_name")) {

                try {
                    senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }

            JSONObject newUserObj = object.getJSONObject("newUser");
            String newUserId = newUserObj.getString("_id");

            getUserDetailsNotExists(from);
            getUserDetailsNotExists(newUserId);

            String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
            if (infoPojo != null) {
                groupName = infoPojo.getGroupName();
            }

            GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
            MessageItemChat item = message.createMessageItem(MessageFactory.add_group_member, false, null,
                    MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, from, newUserId, senderOriginalName);
            item.setMessageId(docId.concat("-").concat(msgId));

            MessageDbController db = CoreController.getDBInstance(context);
            db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);

            if (newUserId.equalsIgnoreCase(mCurrentUserId)) {
                callBack.createGroup(groupId);
                callBack.getGroupDetails(groupId);

                SendMessageEvent event = new SendMessageEvent();
                event.setEventName(SocketManager.EVENT_GROUP);
                JSONObject groupObj = new JSONObject();
                groupObj.put("groupType", SocketManager.ACTION_JOIN_NEW_GROUP);
                groupObj.put("from", mCurrentUserId);
                groupObj.put("groupId", groupId);
                groupObj.put("createdBy", from);
                groupObj.put("groupName", groupName);
                groupObj.put("timeStamp", msgId);
                groupObj.put("id", msgId);
                event.setMessageObject(groupObj);
                eventBus.post(event);
            }

            sendGroupAckToServer(mCurrentUserId, groupId, msgId);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public class ActiveSessionDispatcher {
        private BlockingQueue<Runnable> dispatchQueue
                = new LinkedBlockingQueue<Runnable>();
        private Thread mThread;

        public ActiveSessionDispatcher() {
            mThread = new Thread(dispatchRunnable);
            mThread.start();
        }

        public Runnable dispatchRunnable = new Runnable() {
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
        };

        private void addWork(Runnable work) {
            try {
                dispatchQueue.put(work);
            } catch (Exception e) {
            }
        }

    }

    private void changeBadgeCount(String convId) {

        shortcutBadgeMgnr.putMessageCount(convId);
        int totalCount = shortcutBadgeMgnr.getTotalCount();

        try {
            ShortcutBadger.applyCountOrThrow(context, totalCount);
        } catch (ShortcutBadgeException e) {
            e.printStackTrace();
        }
    }

    /**
     * add New Group Data in chat
     *
     * @param object based on response getting the value and update the database
     */
    public void addNewGroupData(JSONObject object) {
        try {
            String groupId = object.getString("groupId");
            String members = object.getString("groupMembers");
            String from = object.getString("from");
            String createdBy = object.getString("createdBy");
            String profilePic = object.getString("profilePic");
            String groupName = object.getString("groupName");
            String ts = object.getString("timeStamp");
            String admin = object.getString("admin");
            String msg = object.getString("message");
            String senderOriginalName = "";

            if (object.has("fromuser_name")) {

                try {
                    senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }
            String id;
            if (object.has("id")) {
                id = object.getString("id");
            } else {
                id = "" + Calendar.getInstance().getTimeInMillis();
            }

            getUserDetailsNotExists(from);

            String docId = mCurrentUserId + "-" + groupId + "-g";

            GroupInfoPojo infoPojo = new GroupInfoPojo();
            infoPojo.setCreatedBy(createdBy);
            infoPojo.setAvatarPath(profilePic);
            infoPojo.setGroupName(groupName);
            infoPojo.setGroupMembers(members);
            infoPojo.setAdminMembers(admin);
            infoPojo.setLiveGroup(true);
            groupInfoSession.updateGroupInfo(docId, infoPojo);

            getGroupDetails(groupId);

            if (!createdBy.equals(mCurrentUserId)) {
                sendGroupAckToServer(mCurrentUserId, groupId, id);
            } else {
                GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
                MessageItemChat item = message.createMessageItem(MessageFactory.join_new_group, false, null,
                        MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, from, null, senderOriginalName);
                item.setMessageId(docId.concat("-").concat(id));
                item.setTS(ts);

                MessageDbController db = CoreController.getDBInstance(context);
                db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getGroupDetails(String groupId) {
        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_GROUP_DETAILS);

        try {
            JSONObject object = new JSONObject();
            object.put("from", mCurrentUserId);
            object.put("convId", groupId);
            event.setMessageObject(object);
            eventBus.post(event);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void sendGroupAckToServer(String from, String groupId, String id) {
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_GROUP);
        try {
            JSONObject groupAckObj = new JSONObject();
            groupAckObj.put("groupType", SocketManager.ACTION_ACK_GROUP_MESSAGE);
            groupAckObj.put("from", from);
            groupAckObj.put("groupId", groupId);
            groupAckObj.put("status", MessageFactory.GROUP_MSG_DELIVER_ACK);
            groupAckObj.put("msgId", id);
            messageEvent.setMessageObject(groupAckObj);
            eventBus.post(messageEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void getUserDetailsNotExists(String userId) {

        if (!userId.equalsIgnoreCase(mCurrentUserId)) {
//
            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(context);
            ChatappContactModel contactModel = contactDB_sqlite.getUserOpponenetDetails(userId);
            if (contactModel == null) {
                callBack.getUserDetails(userId);
            }
        }
    }

}

