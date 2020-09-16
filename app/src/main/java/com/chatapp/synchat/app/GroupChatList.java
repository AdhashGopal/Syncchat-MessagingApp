package com.chatapp.synchat.app;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.ChatListAdapter;
import com.chatapp.synchat.app.dialog.ChatLockPwdDialog;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.dialog.MuteAlertDialog;
import com.chatapp.synchat.app.dialog.ProfileImageDialog;
import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.app.utils.MuteUnmute;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.ShortcutBadgeManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.ChatLockPojo;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.MuteStatusPojo;
import com.chatapp.synchat.core.model.MuteUserPojo;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * created by  Adhash Team on 12/7/2016.
 */

/**
 *
 */
public class GroupChatList extends Fragment implements MuteAlertDialog.MuteAlertCloseListener {

    private ChatListAdapter mAdapter;
    private RecyclerView recyclerView_chat;
    private ArrayList<MessageItemChat> mChatData = new ArrayList<>();
    private SearchView searchView;
    private AvnNextLTProRegTextView userMessagechat;
    View view;
    HashMap<String, MessageItemChat> uniqueStore = new HashMap<>();
    HashMap<String, Long> typingEventMap = new HashMap<>();
    private String mCurrentUserId;
    Bitmap myTemp = null;
    private final int QR_REQUEST_CODE = 25;
    Session session;
    private SessionManager sessionManager;
    Boolean myMenuClickFlag = false;
    int count = 0;
    Getcontactname getcontactname;
    private Menu menuLongClick, menuNormal;
    String receiverDocumentID = new String();
    String username, profileimage, value;
    AvnNextLTProRegTextView archivetext;
    private RelativeLayout archive_relativelayout;
    String archivecount;
    public static boolean isGroupChatList;
    private ArrayList<MessageItemChat> selectedChatItems = new ArrayList<>();
    private GroupInfoSession groupInfoSession;
    FastScrollRecyclerView fastScroller;
    private Handler typingHandler;
    private long timeOutTyping = 3000;
    private UserInfoSession userInfoSession;
    private MessageDbController db;
    private ArrayList<String> getUsersList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

        isGroupChatList = true;

        if (view == null) {
            view = inflater.inflate(R.layout.chatlist, container, false);
        } else {
            if (view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
        }
        sessionManager = SessionManager.getInstance(getActivity());
        mCurrentUserId = sessionManager.getCurrentUserID();
        archive_relativelayout = (RelativeLayout) view.findViewById(R.id.archive_relativelayout);
        archivetext = (AvnNextLTProRegTextView) view.findViewById(R.id.archive);
        getcontactname = new Getcontactname(getActivity());
        userMessagechat = (AvnNextLTProRegTextView) view.findViewById(R.id.userMessagechat);
        db = CoreController.getDBInstance(getActivity());
        recyclerView_chat = (RecyclerView) view.findViewById(R.id.rv);
        recyclerView_chat.setHasFixedSize(true);
        mAdapter = new ChatListAdapter(view.getContext(), mChatData);
        mAdapter.setCallback(GroupChatList.this);
        recyclerView_chat.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView_chat.setItemAnimator(new DefaultItemAnimator());
        recyclerView_chat.setAdapter(mAdapter);

        session = new Session(getActivity());
        archivecount = String.valueOf(session.getarchivecountgroup());


        userInfoSession = new UserInfoSession(getActivity());
        typingHandler = new Handler();


        setArchieveText();

        groupInfoSession = new GroupInfoSession(getActivity());

        archivetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aIntent = new Intent(getActivity(), Archivelist.class);
                aIntent.putExtra("from", "grouplist");
                startActivity(aIntent);

            }
        });

        ImageButton ibNewChat = (ImageButton) view.findViewById(R.id.ibNewChat);
        ibNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchSettingContactScreen(getActivity());
            }
        });

        mAdapter.setChatListItemClickListener(new ChatListAdapter.ChatListItemClickListener() {
            @Override
            public void onItemClick(MessageItemChat messageItemChat, View view, int position, long imageTS) {
                if (view.getId() == R.id.storeImage) {

                    String Uid = mChatData.get(position).getMessageId().split("-")[1];
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("MessageItem", mChatData.get(position));
                    bundle.putString("userID", Uid);
                    bundle.putLong("imageTS", imageTS);
                    /*if(view instanceof CircleImageView) {
                        CircleImageView imageView = (CircleImageView) view;
                        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                        SerializeBitmap serializeBitmap = new SerializeBitmap();
                        serializeBitmap.setBitmap(bitmap);

                        bundle.putSerializable("ProfilePic", serializeBitmap);
                    }*/

                    bundle.putString("ProfilePic", null);
                    bundle.putBoolean("GroupChat", true);
                    bundle.putBoolean("FromSecretChat", false);

                    ProfileImageDialog dialog = new ProfileImageDialog();
                    dialog.setArguments(bundle);
                    dialog.show(getFragmentManager(), "profile");
                } else {
                    if (position >= 0) {
                        ChatLockPojo pojo = getChatLockdetailfromDB(position);

                        if (myMenuClickFlag) {

                            mChatData.get(position).setSelected(!mChatData.get(position).isSelected());

                            if (mChatData.get(position).isSelected()) {
                                selectedChatItems.add(mChatData.get(position));
                                count = count + 1;
                                System.out.println("count:===============" + count);
                            } else {
                                selectedChatItems.remove(mChatData.get(position));
                                count = count - 1;
                                System.out.println("count:===============" + count);
                            }
                            mAdapter.notifyDataSetChanged();

                            onCreateOptionsMenu(menuLongClick, getActivity().getMenuInflater());
                            if (count <= 0) {
                                myMenuClickFlag = false;
                                onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                                count = 0;
//                            getActivity().getMenuInflater().inflate(R.menu.chats_screen, menuNormal);
                            }
                        } else if (sessionManager.getLockChatEnabled().equals("1") && pojo != null) {
                            String stat = pojo.getStatus();
                            String pwd = pojo.getPassword();

                            MessageItemChat e = mAdapter.getItem(position);
                            String docID = e.getMessageId();
                            String[] ids = docID.split("-");
                            String documentid = ids[0] + "-" + ids[1] + "-g";
                            if (stat.equals("1")) {
                                openUnlockChatDialog(documentid, stat, pwd, position);
                            } else {
                                navigateTochatviewpage(position);
                            }
                        } else {
                            navigateTochatviewpage(position);
                        }
                    }

                }
            }

            @Override
            public void onItemLongClick(MessageItemChat messageItemChat, View view, int position) {
                try {
                    if (position >= 0) {
                        mChatData.get(position).setSelected(!mChatData.get(position).isSelected());
                        ImageView ivProfielPic = (ImageView) view.findViewById(R.id.storeImage);
                        if (ivProfielPic.getDrawable() != null) {
                            try {
                                myTemp = ((BitmapDrawable) ivProfielPic.getDrawable()).getBitmap();
                            } catch (NullPointerException e) {
                                e.printStackTrace();
                            }

                        }
                        if (mChatData.get(position).isSelected()) {
                            selectedChatItems.add(mChatData.get(position));
                            count = count + 1;
                            System.out.println("count:===============" + count);
                        } else {
                            selectedChatItems.remove(mChatData.get(position));
                            count = count - 1;
                            System.out.println("count:===============" + count);
                        }
                        mAdapter.notifyDataSetChanged();
                        myMenuClickFlag = true;
                        MessageItemChat e = mChatData.get(position);
                        e.setSelected(true);
                        mChatData.set(position, e);
                        mAdapter.notifyDataSetChanged();
                        username = e.getSenderName();
                        profileimage = e.getAvatarImageUrl();

                        onCreateOptionsMenu(menuLongClick, getActivity().getMenuInflater());
                    }
                } catch (Exception e) {
                }
            }
        });

        return view;
    }

    /**
     * Handling the database for UI stage
     */
    @Override
    public void onResume() {
        super.onResume();

        loadDbChatItem();
        isGroupChatList = true;
        setArchieveText();
        try {
            ShortcutBadger.removeCountOrThrow(getContext()); //for 1.1.4+
        } catch (Exception e) {
            // e.printStackTrace();
        }


    }

    /**
     * set ArchieveText
     */
    private void setArchieveText() {
        archivecount = String.valueOf(session.getarchivecountgroup());
        if (session.getarchivecountgroup() <= 0) {
            archive_relativelayout.setVisibility(View.GONE);
        } else {
            archive_relativelayout.setVisibility(View.VISIBLE);
            archivetext.setText("Archived chats (" + archivecount + ")");
        }
    }

    /**
     * get Chat Lockdetail from Database
     *
     * @param position specific item
     * @return value
     */
    private ChatLockPojo getChatLockdetailfromDB(int position) {
        MessageItemChat e = mAdapter.getItem(position);
        String docID = e.getMessageId();
        String[] id = docID.split("-");
        String documentID = id[0] + "-" + id[1] + "-g";
        MessageDbController dbController = CoreController.getDBInstance(getActivity());
        String convId = userInfoSession.getChatConvId(documentID);
        String receiverId = userInfoSession.getReceiverIdByConvId(convId);
        ChatLockPojo pojo = dbController.getChatLockData(receiverId, MessageFactory.CHAT_TYPE_GROUP);
        return pojo;
    }


    /**
     * get chat data from database
     */
    public void loadDbChatItem() {
        MessageDbController db = CoreController.getDBInstance(getActivity());
        ArrayList<MessageItemChat> groupChats = db.selectChatList(MessageFactory.CHAT_TYPE_GROUP);
        uniqueStore.clear();
        for (MessageItemChat msgItem : groupChats) {

            String groupId = msgItem.getReceiverID();
            String docId = mCurrentUserId + "-" + groupId + "-g";

            Log.d("GroupdID", groupId + "  ___ " + docId);

            // Get group details if not updated from messageservice class
            if (!groupInfoSession.hasGroupInfo(docId)
                    || groupInfoSession.getGroupInfo(docId).getGroupName() == null) {
                getGroupDetails(groupId);
            }

            if (db.getChatClearedStatus(groupId, MessageFactory.CHAT_TYPE_GROUP)
                    == MessageFactory.CHAT_STATUS_CLEARED) {
                msgItem = new MessageItemChat();
                msgItem.setMessageType("");
                msgItem.setReceiverID(groupId);
                msgItem.setMessageId(docId.concat("-0"));
                msgItem.setTextMessage("");
                msgItem.setTS("0");
            }

            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
            if (!session.getarchivegroup(docId)) {

                if (infoPojo != null) {
                    String avatarPath = infoPojo.getAvatarPath();
                    msgItem.setAvatarImageUrl(avatarPath);
                    msgItem.setSenderName(infoPojo.getGroupName());
                    msgItem.setGroupName(infoPojo.getGroupName());
                }
                uniqueStore.put(docId, msgItem);


            }
        }

        updateChatList();
    }

    /**
     * getGroupDetails
     *
     * @param groupId input value(groupId)
     */
    public void getGroupDetails(String groupId) {
        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_GROUP_DETAILS);

        try {
            JSONObject object = new JSONObject();
            object.put("from", mCurrentUserId);
            object.put("convId", groupId);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * navigate To chat viewpage
     *
     * @param position specific item
     */
    private void navigateTochatviewpage(int position) {
        Intent intent = new Intent(getActivity(), ChatViewActivity.class);
        MessageItemChat e = mAdapter.getItem(position);
        if (e != null) {
            if (e.getStatus() != null) {
                String[] array = e.getMessageId().split("-");
                intent.putExtra("receiverUid", e.getNumberInDevice());
                intent.putExtra("receiverName", e.getSenderName());

                receiverDocumentID = array[1];
                intent.putExtra("documentId", receiverDocumentID);
                intent.putExtra("Username", e.getSenderName());
                intent.putExtra("Image", e.getAvatarImageUrl());
                intent.putExtra("type", 0);

                startActivity(intent);
//                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.status_not_available), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * update ChatList
     */
    private void updateChatList() {

        mChatData.clear();
        for (MessageItemChat value : uniqueStore.values()) {

            // For always showing Group Title
            if (value.getMessageId() != null && value.getMessageId().contains("-g-")) {
                value.setSenderName(value.getGroupName());
            }

            mChatData.add(value);
        }
        if (mChatData.size() > 0) {
            userMessagechat.setVisibility(View.GONE);
        }
        Collections.sort(mChatData, new ChatListSorter());
        mAdapter.notifyDataSetChanged();
    }

    /**
     * Eventbus data
     *
     * @param event based on value call socket(typing,recording,group,group message,join new group,exit group,change group dp,delete message from group)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_TYPING)) {
            loadTypingStatus(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_RECORDING)) {
            loadRecordingStatus(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP)) {
            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                String groupAction = object.getString("groupType");
                if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EVENT_GROUP_MESSAGE)) {
                    loadGroupMessage(event);
                } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_JOIN_NEW_GROUP)) {
//                    joinToGroup(object);
                    loadDbChatItem();
                } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_CHANGE_GROUP_NAME)) {
                    loadChangeGroupNameMessage(object);
                } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EXIT_GROUP)) {
                    loadExitMessage(object);
                } else if (groupAction.equals(SocketManager.ACTION_CHANGE_GROUP_DP)) {
                    performGroupChangeDp(object);
                } else if (groupAction.equals(SocketManager.ACTION_EVENT_GROUP_MSG_DELETE)) {
                    deleteGroupMessage(object);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_QR_DATA)) {
            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                String userId = object.getString("_id");
                if (userId.equalsIgnoreCase(mCurrentUserId)) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.app_name) + " web connected", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //new event --> for QR code
        else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_QR_DATA_RES)) {
            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                Log.d("QRSCANNER", object.toString());
                Log.e("web", "" + object);
                String err = object.getString("err");
                Log.e("userId", "" + err);
                if (err.equalsIgnoreCase("0")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.app_name) + " web connected", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Couldn't scan code. Make sure you're on https://chat.nowletschat.com/web and scan again", Toast.LENGTH_SHORT).show();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP_DETAILS)) {
//            loadGroupDetails(event);
            loadDbChatItem();
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_VIEW_CHAT)) {
            Object[] obj = event.getObjectsArray();
            changeMsgViewedStatus(obj[0].toString());
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_DELETE_CHAT)) {
            Object[] obj = event.getObjectsArray();
            loadDeleteChat(obj[0].toString());
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_ARCHIVE_UNARCHIVE)) {
            Object[] obj = event.getObjectsArray();
            loadarchive(obj[0].toString());
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MUTE)) {
            loadMuteMessage(event.getObjectsArray()[0].toString());
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GET_USER_DETAILS)) {
            loadUserDetails(event.getObjectsArray()[0].toString());
        }

    }

    /**
     * loadUserDetails & update adapter
     *
     * @param data input value(data)
     */
    private void loadUserDetails(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String userId = object.getString("id");
            if (getUsersList.contains(userId) && mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * loadMuteMessage & update adapter
     *
     * @param data input value(data)
     */
    private void loadMuteMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");
            String chatType = object.getString("type");
            if (from.equalsIgnoreCase(mCurrentUserId) && chatType.equalsIgnoreCase("group") && mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * loadarchive & update adapter
     *
     * @param data input value(data)
     */
    private void loadarchive(String data) {
        try {
            JSONObject object = new JSONObject(data);

            try {
                String from = object.getString("from");
                String convId = object.getString("convId");
                String type = object.getString("type");
                String status = object.getString("status");

                if (from.equalsIgnoreCase(mCurrentUserId)) {
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

                        if (uniqueStore.containsKey(docId)) {
                            uniqueStore.remove(docId);
                            updateChatList();

//                            if (session.getarchivecountgroup() != 0) {
//                                if (session.getarchivegroup(mCurrentUserId + "-" + convId + "-g"))
//                                    session.removearchivegroup(mCurrentUserId + "-" + convId + "-g");
//                                Log.d("Archeivecount",String.valueOf(session.getarchivecount()));
//                                setArchieveText();
//                            }

                            mAdapter.notifyDataSetChanged();

                            HomeScreen activity = (HomeScreen) getActivity();
                            activity.changeTabTextCount();
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
     * loadDeleteChat & update adapter
     *
     * @param data input value(data)
     */
    private void loadDeleteChat(String data) {
        try {
            JSONObject object = new JSONObject(data);

            try {
                String from = object.getString("from");
                String convId = object.getString("convId");
                String type = object.getString("type");

                if (from.equalsIgnoreCase(mCurrentUserId)) {
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

                        if (uniqueStore.containsKey(docId)) {
                            uniqueStore.remove(docId);
                            updateChatList();
                            mAdapter.notifyDataSetChanged();

                            HomeScreen activity = (HomeScreen) getActivity();
                            activity.changeTabTextCount();
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
     * changeMsgViewedStatus
     *
     * @param data input value(data)
     */
    private void changeMsgViewedStatus(String data) {

        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");
            String mode = object.getString("mode");

            if (from.equalsIgnoreCase(mCurrentUserId) && mode.equalsIgnoreCase("web")) {
                String chatType = object.getString("type");
                String to = object.getString("to");

                if (chatType.equalsIgnoreCase("group")) {
                    loadDbChatItem();
                    HomeScreen activity = (HomeScreen) getActivity();
                    activity.changeTabTextCount();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * loadTypingStatus
     *
     * @param event getting value from model data
     */
    private void loadTypingStatus(ReceviceMessageEvent event) {

        Object[] object = event.getObjectsArray();
        try {
            JSONObject jsonObject = new JSONObject(object[0].toString());
            String from = (String) jsonObject.get("from");
            String to = (String) jsonObject.get("to");
            String groupId = (String) jsonObject.get("convId");
            String type = (String) jsonObject.get("type");

            if (type.equalsIgnoreCase("group") && !from.equalsIgnoreCase(mCurrentUserId)) {
                ChatappContactModel contact = CoreController.getContactSqliteDBintstance(getActivity()).getUserOpponenetDetails(from);
                if (contact != null) {
                    String typingPerson = getcontactname.getSendername(from, contact.getMsisdn());
                    setTypingMessage(groupId, typingPerson);
                } else {
                    getUserDetails(from);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * set TypingMessage
     *
     * @param groupId      input value(groupId)
     * @param typingPerson input value(typingPerson)
     */
    private void setTypingMessage(String groupId, String typingPerson) {
        String docId = mCurrentUserId + "-" + groupId + "-g";

        if (uniqueStore.containsKey(docId)) {
            MessageItemChat msgItem = uniqueStore.get(docId);
            int index = mChatData.indexOf(msgItem);

            long currentTime = Calendar.getInstance().getTimeInMillis();

            msgItem.setTypingAt(currentTime);
            msgItem.setTypePerson(typingPerson);
            handleReceiverTypingEvent(docId, currentTime);
            mChatData.set(index, msgItem);
            uniqueStore.put(docId, msgItem);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * loadRecordingStatus
     *
     * @param event getting value from model data
     */
    private void loadRecordingStatus(ReceviceMessageEvent event) {

        Object[] object = event.getObjectsArray();
        try {
            JSONObject jsonObject = new JSONObject(object[0].toString());
            String from = (String) jsonObject.get("from");
            String to = (String) jsonObject.get("to");
            String groupId = (String) jsonObject.get("convId");
            String type = (String) jsonObject.get("type");

            if (type.equalsIgnoreCase("group") && !from.equalsIgnoreCase(mCurrentUserId)) {
                ChatappContactModel contact = CoreController.getContactSqliteDBintstance(getActivity()).getUserOpponenetDetails(from);
                if (contact != null) {
                    String typingPerson = getcontactname.getSendername(from, contact.getMsisdn());
                    setRecordingMessage(groupId, typingPerson);
                } else {
                    getUserDetails(from);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * setRecordingMessage
     *
     * @param groupId      input value(groupId)
     * @param typingPerson input value(typingPerson)
     */
    private void setRecordingMessage(String groupId, String typingPerson) {
        String docId = mCurrentUserId + "-" + groupId + "-g";

        if (uniqueStore.containsKey(docId)) {
            MessageItemChat msgItem = uniqueStore.get(docId);
            int index = mChatData.indexOf(msgItem);

            long currentTime = Calendar.getInstance().getTimeInMillis();

            msgItem.setRecordingAt(currentTime);
            msgItem.setTypePerson(typingPerson);
            handleReceiverTypingEvent(docId, currentTime);
            mChatData.set(index, msgItem);
            uniqueStore.put(docId, msgItem);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * handle Receiver Typing Event
     *
     * @param docId       input value(docId)
     * @param currentTime input value(currentTime)
     */
    private void handleReceiverTypingEvent(String docId, long currentTime) {
        if (typingEventMap.size() == 0) {
            timeOutTyping = 3000;
        }
        typingEventMap.put(docId, currentTime);
        typingHandler.postDelayed(typingRunnable, timeOutTyping);
    }

    /**
     * message timeout
     */
    Runnable typingRunnable = new Runnable() {
        @Override
        public void run() {
            long currentTime = Calendar.getInstance().getTimeInMillis();

            ArrayList<String> removeList = new ArrayList<>();
//            Iterator it = typingEventMap.entrySet().iterator();
            for (Iterator it = typingEventMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry pair = (Map.Entry) it.next();
                String key = (String) pair.getKey();
                long timeOut = (long) pair.getValue();

                long timeDiff = currentTime - timeOut;
                if (timeDiff > MessageFactory.TYING_MESSAGE_TIMEOUT) {
//                    typingEventMap.remove(key);

                    MessageItemChat chat = uniqueStore.get(key);
                    if (chat != null) {
                        int index = mChatData.indexOf(chat);
                        chat.setTypingAt(0);
                        chat.setRecordingAt(0);

                        mChatData.set(index, chat);
                        uniqueStore.put(key, chat);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                /*else {
                    typingEventMap.put(key, timeDiff);
                }*/

                System.out.println(pair.getKey() + " = " + pair.getValue());
            }
            if (typingEventMap.size() > 0) {
                timeOutTyping = currentTime - Collections.max(typingEventMap.values());
                Log.d("MaxTimeout", "" + timeOutTyping);
                typingHandler.postDelayed(typingRunnable, timeOutTyping);
            } else {
                typingHandler.removeCallbacks(typingRunnable);
            }
        }
    };


    /**
     * open Unlock ChatDialog
     *
     * @param docId    input value(docId)
     * @param status   input value(status)
     * @param pwd      input value(pwd)
     * @param position input value(position)
     */
    private void openUnlockChatDialog(String docId, String status, String pwd, int position) {
        String convId = docId.split("-")[1];
        ChatLockPwdDialog dialog = new ChatLockPwdDialog();
        dialog.setTextLabel1("Enter your Password");
        dialog.setEditTextdata("Enter new Password");
        dialog.setforgotpwdlabel("Forgot Password");
        dialog.setHeader("Unlock Chat");
        dialog.setButtonText("UNLOCK");
        Bundle bundle = new Bundle();
        bundle.putSerializable("MessageItem", mChatData.get(position));
        bundle.putString("convID", convId);
        bundle.putString("status", "1");
        bundle.putString("pwd", pwd);
        bundle.putString("page", "chatview");
        bundle.putString("type", "group");
        bundle.putString("from", mCurrentUserId);
        dialog.setArguments(bundle);
        dialog.show(getFragmentManager(), "chatunLock");
    }

    /**
     * get UserDetails
     *
     * @param userId input value(userId)
     */
    public void getUserDetails(String userId) {
        getUsersList.add(userId);

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getActivity());
        ChatappContactModel contact = contactDB_sqlite.getUserOpponenetDetails(userId);

        if (contact == null) {
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
        }
    }

    /**
     * Group Change Dp
     *
     * @param object input value(getting api response)
     */
    private void performGroupChangeDp(JSONObject object) {
        try {
            String err = object.getString("err");

            if (err.equalsIgnoreCase("0")) {

                String message = object.getString("message");
                String groupId = object.getString("groupId");
                String avatar = object.getString("avatar");

                /*String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
                String path = Constants.SOCKET_IP.concat(avatar);*/

                for (int i = 0; i < mChatData.size(); i++) {

                    if (mChatData.get(i).getMessageId().contains(groupId)) {
                        mChatData.get(i).setAvatarImageUrl(avatar);

                        String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
                        GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
                        infoPojo.setAvatarPath(avatar);
                        groupInfoSession.updateGroupInfo(docId, infoPojo);
                        break;
                    }
                }
                mAdapter.notifyDataSetChanged();

/*                MessageItemChat item = new MessageItemChat();
                item.*/
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * update database
     *
     * @param object
     */
    public void loadExitMessage(JSONObject object) {
        loadDbChatItem();
    }

    /**
     * update database
     *
     * @param object
     */
    public void loadChangeGroupNameMessage(JSONObject object) {
        loadDbChatItem();
    }

    public String getMessageForType(String type, String payLoad) {
        String message = new String();
        if ("0".equals("" + type)) {
            message = payLoad;
        } else if ("1".equals("" + type)) {
            message = "New Image";
        } else if ("1".equals("" + type)) {
            message = "New Video";
        } else if ("3".equals("" + type)) {
            message = "New Location";
        } else if ("4".equals("" + type)) {
            message = "New Contact";
        } else {
            message = "New Audio";
        }
        return message;
    }

    /**
     * load GroupMessage
     *
     * @param event getting value from model class
     */
    private void loadGroupMessage(ReceviceMessageEvent event) {
        Object[] array = event.getObjectsArray();
        try {
            JSONObject objects = new JSONObject(array[0].toString());
            //{"type":0,"payload":"hjjk","id":"5808c7f76090cc12841d4b46-5808cb46bf5aa60a70fd5c23","from":"5808c7f76090cc12841d4b46","doc_id":"5808c7f76090cc12841d4b46-5808cb46bf5aa60a70fd5c23-1477902694564","thumbnail":"","dataSize":"","timestamp":1477902830388,"convId":"5816e58855dea4ec14bf7dd7"}
            Object object = objects.get("type");
            Integer type = 0;
            if (object instanceof String) {
                type = Integer.valueOf((String) objects.get("type"));
            } else if (object instanceof Integer) {
                type = Integer.valueOf((Integer) objects.get("type"));
            }

            if (type == 6) {
                /*String to = objects.getString("groupId");
                String from = objects.getString("from");
                String id = objects.getString("id");

                sendGroupAckToServer(from, to, "" + id);*/
            } else {
                String payLoad = objects.getString("payload");
                Object idObj = objects.get("id");
                String id = "";
                if (idObj instanceof String) {
                    id = String.valueOf(objects.get("id"));
                } else if (idObj instanceof Integer) {
                    id = String.valueOf(objects.get("id"));
                }
                String from = objects.getString("from");
                String to = objects.getString("to");
                if (objects.has("toDocId")) {
                    String toDocId = (String) objects.get("toDocId");
                    String[] splitDocId = toDocId.split("-");
                    String docId = mCurrentUserId.concat("-").concat(splitDocId[1]).concat("-")
                            .concat(splitDocId[2]).concat("-")
                            .concat(splitDocId[3]);
                    String name = objects.getString("msisdn");
                    String ts = objects.getString("timestamp");
                    String groupName = objects.getString("groupName");
                    String starredStatus = objects.getString("isStar");

                    MessageItemChat item = new MessageItemChat();
                    item.setTS(ts);
                    item.setStarredStatus(starredStatus);
                    /*try {
                        item.setSenderName("" + new String(Base64.decode(name, Base64.DEFAULT), "UTF-8"));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }*/

                    String groupDocId = mCurrentUserId.concat("-").concat(to).concat("-g");
                    if (groupInfoSession.hasGroupInfo(groupDocId)) {
                        GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(groupDocId);
                        if (infoPojo != null) {
                            String avatarPath = infoPojo.getAvatarPath();
                            item.setAvatarImageUrl(avatarPath);
                        }
                    }

                    item.setSenderName(name);
                    item.setGroupName(groupName);
                    item.setIsSelf(false);
                    item.setMessageId(docId);
                    item.setMessageType("" + type);
                    item.sethasNewMessage(true);
                    String strType = "" + type;
                    if (strType.equalsIgnoreCase("" + MessageFactory.text)) {
                        item.setTextMessage(payLoad);
                    } else if (strType.equalsIgnoreCase("" + MessageFactory.contact)) {
//                        item.setContactInfo(payLoad);
                    } else if (strType.equalsIgnoreCase("" + MessageFactory.picture)) {
                        item.setImagePath(payLoad);
                    }
                    item.setCount(1);
                    String uniqueID = new String();

                    String messageAck = new String();
                    if (from.equalsIgnoreCase(mCurrentUserId)) {
                        uniqueID = from + "-" + to + "-g";
                    } else /*if (to.equalsIgnoreCase(uniqueCurrentID))*/ {
                        uniqueID = mCurrentUserId + "-" + to + "-g";
//                messageAck = from;
                    }

                    messageAck = from;
                    sendGroupAckToServer(messageAck, to, "" + id);
//                    db.updateChatMessage(uniqueID, item);
                    uniqueStore.put(uniqueID, item);


                    if (session.getarchivecountgroup() >= 0) {
                        if (session.getarchivegroup(mCurrentUserId + "-" + to + "-g"))
                            session.removearchivegroup(mCurrentUserId + "-" + to + "-g");
                        Log.d("Archeivecount_loaggroup", String.valueOf(session.getarchivecount()));
                        setArchieveText();
                    }

                    updateChatList();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Group acknowledge To Server
     *
     * @param from    input value(from)
     * @param groupId input value(groupId)
     * @param id      input value(id)
     */
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
            EventBus.getDefault().post(messageEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Start Eventbus function
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
//        MenuItem secretMenuItem = menu.findItem(R.id.select_secret_chat_contact);
//        if (secretMenuItem != null) {
//            if (sessionManager.getSecretChatEnabled().equals("1")) {
//                secretMenuItem.setVisible(false);
//            } else {
//                secretMenuItem.setVisible(false);
//            }
//        }
        super.onPrepareOptionsMenu(menu);
    }

    /**
     * CreateOptionsMenu & select specific view
     *
     * @param menu     menu binding
     * @param inflater view
     */
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        Log.e("flag", "" + myMenuClickFlag);
        menuNormal = menu;
        menuLongClick = menu;

        if (myMenuClickFlag) {
            getActivity().getMenuInflater().inflate(R.menu.grouplist_longclick_menu, menu);
            MenuItem markasunread_menu = menu.findItem(R.id.markasunread_menu);
            MenuItem mute = menu.findItem(R.id.mute_menu);
            MenuItem unmute = menu.findItem(R.id.unmute_menu);
            MenuItem markread_menu = menu.findItem(R.id.markread_menu);
            MenuItem Exitgroup = menu.findItem(R.id.Exitgroup);
            MenuItem add_shortcut = menu.findItem(R.id.addchatshortcut_menu);
            final MenuItem Groupinfo = menu.findItem(R.id.Groupinfo);
            MenuItem Deletegroup = menu.findItem(R.id.delete);
            MenuItem archive = menu.findItem(R.id.archive_menu);
            ArrayList<Boolean> exitanddelete = new ArrayList<>();
            for (int i = 0; i < selectedChatItems.size(); i++) {
                String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                String receiverId = arrIds[1];
                boolean hasGroupInfo = groupInfoSession.hasGroupInfo(mCurrentUserId + "-" + receiverId + "-g");
                GroupInfoPojo infoPojo;
                if (hasGroupInfo) {
                    infoPojo = groupInfoSession.getGroupInfo(mCurrentUserId + "-" + receiverId + "-g");
                    if (infoPojo.isLiveGroup()) {
                        exitanddelete.add(infoPojo.isLiveGroup());
                    } else {
                        exitanddelete.add(infoPojo.isLiveGroup());
                    }
                }
            }
            if (exitanddelete.contains(true) && exitanddelete.contains(false)) {
                Exitgroup.setVisible(false);
                Deletegroup.setVisible(false);
            } else if (exitanddelete.contains(true)) {
                Exitgroup.setVisible(true);
                Deletegroup.setVisible(false);
            } else {
                Exitgroup.setVisible(false);
                Deletegroup.setVisible(true);
            }
            if (count >= 2) {
                add_shortcut.setVisible(false);
                Groupinfo.setVisible(false);
            }

            ArrayList<Boolean> mutearray = new ArrayList<>();
            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getActivity());

            for (int i = 0; i < selectedChatItems.size(); i++) {
                String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                String groupId = arrIds[1];
                MuteStatusPojo muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, null, groupId, false);

                if (muteData != null && muteData.getMuteStatus().equals("1")) {
                    mutearray.add(true);
                    /*mute.setVisible(true);
                    unmute.setVisible(false);*/
                } else {
                    mutearray.add(false);
                    /*mute.setVisible(false);
                    unmute.setVisible(true);*/
                }
            }
            if (mutearray.contains(true) && mutearray.contains(false)) {
                mute.setVisible(false);
                unmute.setVisible(false);
            } else if (mutearray.contains(true)) {
                mute.setVisible(false);
                unmute.setVisible(true);
            } else {
                mute.setVisible(true);
                unmute.setVisible(false);
            }
            ArrayList<Boolean> mark = new ArrayList<>();
            for (int i = 0; i < selectedChatItems.size(); i++) {
                String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                String receiverId = arrIds[1];
                if (session.getmark(receiverId)) {
                    mark.add(session.getmark(receiverId));
                    /*markread_menu.setVisible(false);
                    markasunread_menu.setVisible(true);*/
                } else {
                    mark.add(false);
                    /*markread_menu.setVisible(true);
                    markasunread_menu.setVisible(false);*/
                }
            }
            if (mark.contains(true) && mark.contains(false)) {
                markread_menu.setVisible(false);
                markasunread_menu.setVisible(false);
            } else if (mark.contains(true)) {
                markread_menu.setVisible(false);
                markasunread_menu.setVisible(true);
            } else {
                markread_menu.setVisible(true);
                markasunread_menu.setVisible(false);
            }
            /*if (markflag) {
                markasunread_menu.setVisible(true);
                markread_menu.setVisible(false);
            } else {
                markasunread_menu.setVisible(false);
                markread_menu.setVisible(true);
            }*/
            add_shortcut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    for (int i = 0; i < selectedChatItems.size(); i++) {
                        addShortcut();
                        int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                        mChatData.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected()); // setchangemenu();
                    }
                    myMenuClickFlag = false;
                    count = 0;
                    mAdapter.notifyDataSetChanged();
                    onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                    return false;
                }
            });
            Groupinfo.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    //setchangemenu();
                    viewcontact();
                    myMenuClickFlag = false;
                    mAdapter.notifyDataSetChanged();
                    count = 0;
                    onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                    return false;
                }
            });
            Exitgroup.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    showExitGroupAlert();
                    return false;
                }
            });
            Deletegroup.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    for (int i = 0; i < selectedChatItems.size(); i++) {
                        try {
                            String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                            String receiverId = arrIds[1];
                            String docId = mCurrentUserId.concat("-").concat(receiverId).concat("-g");

                            try {
                                JSONObject object = new JSONObject();
                                object.put("convId", receiverId);
                                object.put("from", mCurrentUserId);
                                object.put("type", MessageFactory.CHAT_TYPE_GROUP);

                                SendMessageEvent event = new SendMessageEvent();
                                event.setEventName(SocketManager.EVENT_DELETE_CHAT);
                                event.setMessageObject(object);
                                EventBus.getDefault().post(event);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            MessageDbController db = CoreController.getDBInstance(getActivity());
                            db.deleteChat(docId, MessageFactory.CHAT_TYPE_GROUP);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    selectedChatItems.clear();
                    loadDbChatItem();
                    myMenuClickFlag = false;
                    count = 0;
                    mAdapter.notifyDataSetChanged();
                    onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                    return false;
                }
            });
            markasunread_menu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    for (int i = 0; i < selectedChatItems.size(); i++) {
                        try {
                            String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                            String receiverId = arrIds[1];
//                            String docId = mCurrentUserId.concat("-").concat(receiverId).concat("-g");
                            try {
                                JSONObject object = new JSONObject();
                                object.put("convId", receiverId);
                                object.put("from", mCurrentUserId);
                                object.put("status", 1);
                                object.put("type", "group");
                                SendMessageEvent event = new SendMessageEvent();
                                event.setEventName(SocketManager.EVENT_MARKREAD);
                                event.setMessageObject(object);
                                EventBus.getDefault().post(event);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            session.putmark(receiverId);
                            System.out.println("docid" + receiverId);

                            int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                            mChatData.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());


                            HomeScreen activity = (HomeScreen) getActivity();
                            activity.changeTabTextCount();

                            // Badge working if supported devices

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // setchangemenu();
                    }
                    myMenuClickFlag = false;
                    mAdapter.notifyDataSetChanged();
                    selectedChatItems.clear();
                    count = 0;
                    onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                    return false;
                }
            });
            markread_menu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    ShortcutBadgeManager shortcutBadgeMgnr = new ShortcutBadgeManager(getActivity());

                    for (int i = 0; i < selectedChatItems.size(); i++) {
                        try {
                            String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                            String receiverId = arrIds[1];
                            try {
                                JSONObject object = new JSONObject();
                                object.put("convId", receiverId);
                                object.put("from", mCurrentUserId);
                                object.put("status", 0);
                                object.put("type", "group");
                                SendMessageEvent event = new SendMessageEvent();
                                event.setEventName(SocketManager.EVENT_MARKREAD);
                                event.setMessageObject(object);
                                EventBus.getDefault().post(event);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            session.Removemark(receiverId);
                            System.out.println("docid" + receiverId);
                            int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                            mChatData.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());

                            shortcutBadgeMgnr.removeMessageCount(receiverId);
                            int totalCount = shortcutBadgeMgnr.getTotalCount();

                            HomeScreen activity = (HomeScreen) getActivity();
                            activity.changeTabTextCount();

                            // Badge working if supported devices
                            try {
                                ShortcutBadger.applyCountOrThrow(getActivity(), totalCount);
                            } catch (ShortcutBadgeException e) {
                                e.printStackTrace();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        // setchangemenu();
                    }
                    myMenuClickFlag = false;
                    mAdapter.notifyDataSetChanged();
                    selectedChatItems.clear();
                    count = 0;
                    onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                    return false;
                }
            });
            unmute.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (ConnectivityInfo.isInternetConnected(getContext())) {
                        for (int i = 0; i < selectedChatItems.size(); i++) {
                            try {
                                String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                                String receiverId = arrIds[1];

                                MuteUnmute.performUnMute(getActivity(), EventBus.getDefault(), receiverId,
                                        MessageFactory.CHAT_TYPE_GROUP, "no");

                                int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                                if (chatIndex > -1) {
                                    mChatData.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());
                                }
                                mAdapter.notifyDataSetChanged();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        myMenuClickFlag = false;
                        mAdapter.notifyDataSetChanged();
                        selectedChatItems.clear();
                        count = 0;
                        onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                    } else {
                        Toast.makeText(getActivity(), "Check Your Network connection", Toast.LENGTH_SHORT).show();
                    }
                    return false;

                }
            });
            mute.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    if (ConnectivityInfo.isInternetConnected(getContext())) {

                        ArrayList<MuteUserPojo> muteUserList = new ArrayList<>();
                        for (int i = 0; i < selectedChatItems.size(); i++) {
                            String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                            String receiverId = arrIds[1];

                            String chatType = MessageFactory.CHAT_TYPE_GROUP;
                            if (selectedChatItems.get(i).getMessageId().contains("-g")) {
                                chatType = MessageFactory.CHAT_TYPE_GROUP;
                            }

                            MuteUserPojo muteUserPojo = new MuteUserPojo();
                            muteUserPojo.setReceiverId(receiverId);
                            muteUserPojo.setChatType(chatType);
                            muteUserPojo.setSecretType("no");
                            muteUserList.add(muteUserPojo);

                            int index = mChatData.indexOf(selectedChatItems.get(i));
                            if (index > -1) {
                                mChatData.get(index).setSelected(false);
                            }
                        }

                        Bundle putBundle = new Bundle();
                        putBundle.putSerializable("MuteUserList", muteUserList);

                        MuteAlertDialog dialog = new MuteAlertDialog();
                        dialog.setArguments(putBundle);
                        dialog.setCancelable(false);
                        dialog.setMuteAlertCloseListener(GroupChatList.this);
                        dialog.show(getFragmentManager(), "Mute");

                        selectedChatItems.clear();
                        myMenuClickFlag = false;
                        mAdapter.notifyDataSetChanged();
                        count = 0;
                        onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                    } else {
                        Toast.makeText(getActivity(), "Check Network Connection", Toast.LENGTH_SHORT).show();

                    }

                    return false;
                }

            });
            archive.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    String receiverId = "";
                    for (int i = 0; i < selectedChatItems.size(); i++) {
                        try {
                            String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                            receiverId = arrIds[1];
                            String docId = mCurrentUserId.concat("-").concat(receiverId);
                            if (!session.getarchivegroup(mCurrentUserId + "-" + receiverId + "-g")) {
                                session.putarchivegroup(mCurrentUserId + "-" + receiverId + "-g");
                            }
                            try {
                                JSONObject object = new JSONObject();
                                object.put("convId", receiverId);
                                object.put("from", mCurrentUserId);
                                object.put("status", 1);
                                object.put("type", MessageFactory.CHAT_TYPE_GROUP);
                                SendMessageEvent event = new SendMessageEvent();
                                event.setEventName(SocketManager.EVENT_ARCHIVE_UNARCHIVE);
                                event.setMessageObject(object);
                                EventBus.getDefault().post(event);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                       /* int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                        mChatData.remove(chatIndex);
                        mAdapter.notifyDataSetChanged();*/

                    setArchieveText();
                    selectedChatItems.clear();
                    myMenuClickFlag = false;
                    loadDbChatItem();
                    mAdapter.notifyDataSetChanged();
                    count = 0;
                    onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                    return false;
                }
            });
        } else {
            System.out.println("-------chats_screen---------");
            getActivity().getMenuInflater().inflate(R.menu.chats_screen, menu);
//            MenuItem logout = menu.findItem(R.id.logout);
//            logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    final String msg = "Do you really want to logout from web chat?";
//                    final CustomAlertDialog dialog = new CustomAlertDialog();
//                    dialog.setNegativeButtonText("Cancel");
//                    dialog.setPositiveButtonText("Confirm");
//                    dialog.setMessage(msg);
//                    dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//                        @Override
//                        public void onPositiveButtonClick() {
//                            NotificationManager notifManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//                            notifManager.cancelAll();
//                            if (ConnectivityInfo.isInternetConnected(getActivity())) {
////                                unsetDeviceToken();
//                                notifyLogoutToServer();
//                            } else {
//                                Toast.makeText(getActivity(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                        @Override
//                        public void onNegativeButtonClick() {
//                            dialog.dismiss();
//                        }
//                    });
//                    dialog.show(getFragmentManager(), "Delete member alert");
//                    return true;
//                }
//            });

            MenuItem searchItem = menu.findItem(R.id.chatsc_searchIcon);
            searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    searchView.setIconifiedByDefault(true);
                    searchView.setIconified(true);
                    searchView.setQuery("", false);
                    searchView.clearFocus();
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    try {
                        if (newText.equals("") && newText.isEmpty()) {
                            searchView.clearFocus();
                        }
                        mAdapter.getFilter().filter(newText);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                }
            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
                @Override
                public boolean onClose() {

                    menu.findItem(R.id.chatsceen_chatIcon).setVisible(true);
                    return false;
                }
            });

            searchView.setOnSearchClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    menu.findItem(R.id.chatsceen_chatIcon).setVisible(false);
                }
            });

            SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
            searchView.setIconifiedByDefault(true);
            searchView.setQuery("", false);
            searchView.clearFocus();
            searchView.setIconified(true);

            AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
            try {
                Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.set(searchTextView, 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }


    /**
     * Stop EventBus function
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }


    /**
     * OptionsItemSelected
     *
     * @param item binding wdiget it's
     * @return select specific item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        Intent i;
        switch (id) {

//            case R.id.select_contact:
//                ActivityLauncher.launchSettingContactScreen(getActivity());
//                return true;


            case R.id.chatsceen_chatIcon:
                ActivityLauncher.launchSettingContactScreen(getActivity());
                return true;

            case R.id.select_groupchat:

                i = new Intent(getActivity(), SelectPeopleForGroupChat.class);
                startActivity(i);
                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                return true;

//            case R.id.chatapp_web:
//                if (ConnectivityInfo.isInternetConnected(getActivity())) {
//                    Intent qrIntent = new Intent(getActivity(), QRCodeScan.class);
//                    startActivityForResult(qrIntent, QR_REQUEST_CODE);
//                } else {
//                    Toast.makeText(getActivity(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
//                }
//                return true;

            case R.id.starred:
                Intent staresintent = new Intent(getActivity(), StarredItemList.class);
                startActivity(staresintent);
                return true;

//            case R.id.chats_status:
//                ActivityLauncher.launchStatusScreen(getActivity());
//                return true;


            case R.id.chats_settings:
                ActivityLauncher.launchSettingScreen(getActivity());
                return true;

/*            case R.id.select_secret_chat_contact:
                Intent secretintent = new Intent(getActivity(), SecretChatList.class);
                startActivity(secretintent);
                return true;*/

            default:
                return super.onOptionsItemSelected(item);
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == QR_REQUEST_CODE) {
                String qrData = data.getStringExtra("QRData");


                try {
                    JSONObject object = new JSONObject();
                    object.put("_id", mCurrentUserId);
                    object.put("msisdn", sessionManager.getPhoneNumberOfCurrentUser());
                    object.put("random", qrData);

                    SendMessageEvent qrEvent = new SendMessageEvent();
                    qrEvent.setEventName(SocketManager.EVENT_QR_DATA);
                    qrEvent.setMessageObject(object);
                    EventBus.getDefault().post(qrEvent);
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }

            }
        }
    }

    /**
     * load the local database
     *
     * @param isMuted
     */
    @Override
    public void onMuteDialogClosed(boolean isMuted) {
        loadDbChatItem();
    }

    /**
     * Sorting the chat list data
     */
    private class ChatListSorter implements Comparator<MessageItemChat> {

        @Override
        public int compare(MessageItemChat item1, MessageItemChat item2) {

            try {
                long item1Time = AppUtils.parseLong(item1.getTS());
                long item2Time = AppUtils.parseLong(item2.getTS());
                if (item1Time < item2Time) {
                    return 1;
                } else {
                    return -1;
                }
            } catch (Exception e) {
                return -1;
            }

        }
    }

    /**
     * add shortcut
     */
    private void addShortcut() {

        String[] arrIds = selectedChatItems.get(0).getMessageId().split("-");
        String receiverId = arrIds[1];

        Bitmap bitmap = null;
        if (myTemp != null) {
            bitmap = Bitmap.createScaledBitmap(myTemp, 128, 128, true);
        }

        ShortcutBadgeManager.addChatShortcut(getActivity(), true, receiverId, username,
                profileimage, null, bitmap);
    }

    /**
     * view group info contact
     */
    private void viewcontact() {
        Intent infoIntent = new Intent(getActivity(), GroupInfo.class);
        String[] arrIds = selectedChatItems.get(0).getMessageId().split("-");
        infoIntent.putExtra("GroupId", arrIds[1]);
        infoIntent.putExtra("GroupName", username);
        infoIntent.putExtra("UserAvatar", profileimage);
        startActivity(infoIntent);
    }

    /**
     * addDay for calender
     *
     * @param date input value(date)
     * @param i    input value(i)
     * @return value
     */
    public static Date addDay(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTime();
    }
    /**
     * addhour for calender
     *
     * @param date input value(date)
     * @param i    input value(i)
     * @return value
     */
    public static Date addhour(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, i);
        return cal.getTime();
    }
    /**
     * addYear for calender
     *
     * @param date input value(date)
     * @param i    input value(i)
     * @return value
     */
    public static Date addYear(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, i);
        return cal.getTime();
    }

    private void notifyLogoutToServer() {
        try {
            JSONObject logoutObj = new JSONObject();
            logoutObj.put("from", mCurrentUserId);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_MOBILE_TO_WEB_LOGOUT);
            event.setMessageObject(logoutObj);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

   /* private void unsetDeviceToken() {
        try {
            String settingsDeviceId = Settings.Secure.getString(getActivity().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String loginKey = SessionManager.getInstance(getActivity()).getLoginKey();

            JSONObject object = new JSONObject();
            object.put("from", mCurrentUserId);
            object.put("DeviceId", settingsDeviceId);
            object.put("login_key", loginKey);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_REMOVE_MOBILE_LOGIN_NOTIFICATION);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    /**
     * Exit Group Alert
     */
    private void showExitGroupAlert() {
        final CustomAlertDialog alert = new CustomAlertDialog();
        alert.setMessage("Exit " + username + " group");
        alert.setPositiveButtonText("Exit");
        alert.setNegativeButtonText("Cancel");

        alert.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                if (selectedChatItems.size() > 0) {
                    if (ConnectivityInfo.isInternetConnected(getActivity())) {
                        for (int j = 0; j < selectedChatItems.size(); j++) {
                            try {
                                if (selectedChatItems.size() > 0) {
                                    String[] arrIds = selectedChatItems.get(j).getMessageId().split("-");
                                    String receiverId = arrIds[1];
                                    int chatIndex = mChatData.indexOf(selectedChatItems.get(j));
                                    mChatData.get(chatIndex).setSelected(!selectedChatItems.get(j).isSelected());
                                    performGroupExit(receiverId);
                                } else {
                                    break;
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    } else {
                        Toast.makeText(getActivity(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                    myMenuClickFlag = false;
                    mAdapter.notifyDataSetChanged();
                    selectedChatItems.clear();
                    count = 0;
                    onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());

                }
            }

            @Override
            public void onNegativeButtonClick() {
                alert.dismiss();
                myMenuClickFlag = false;
                mAdapter.notifyDataSetChanged();
                selectedChatItems.clear();
                count = 0;
                onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
            }
        });

        alert.show(getFragmentManager(), "Alert");
    }


    /**
     * call GroupExit function
     * @param groupid input value(groupid)
     */
    private void performGroupExit(String groupid) {
        long ts = Calendar.getInstance().getTimeInMillis();
        String msgId = mCurrentUserId + "-" + groupid + "-g-" + ts;

        try {
            JSONObject exitObject = new JSONObject();
            exitObject.put("groupType", SocketManager.ACTION_EXIT_GROUP);
            exitObject.put("from", mCurrentUserId);
            exitObject.put("groupId", groupid);
            exitObject.put("id", ts);
            exitObject.put("toDocId", msgId);

            SendMessageEvent exitGroupEvent = new SendMessageEvent();
            exitGroupEvent.setEventName(SocketManager.EVENT_GROUP);
            exitGroupEvent.setMessageObject(exitObject);
            EventBus.getDefault().post(exitGroupEvent);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * maintain the current activity data
     */
    @Override
    public void onPause() {
        super.onPause();

        // Stop update of typing message
        if (typingRunnable != null) {
            typingHandler.removeCallbacks(typingRunnable);
        }

        isGroupChatList = false;
    }


    //----------------Delete Chat------------------

    /**
     * Delete group message & update the database change adapter
     * @param objects
     */
    private void deleteGroupMessage(JSONObject objects) {
        try {
            MessageDbController db = CoreController.getDBInstance(getActivity());

            String fromId = objects.getString("from");

            if (!fromId.equalsIgnoreCase(mCurrentUserId)) {
                String chat_id = (String) objects.get("doc_id");
                String[] ids = chat_id.split("-");

                String groupAndMsgId = ids[1] + "-g-" + ids[3];

                if (mChatData.size() > 0) {
                    for (int i = 0; i < mChatData.size(); i++) {
                        if (mChatData.get(i).getMessageId().contains(groupAndMsgId)) {
//                            mChatData.get(i).setMessageType("Delete Others");
                            mChatData.get(i).setMessageType(MessageFactory.DELETE_OTHER + "");
                            mChatData.get(i).setIsSelf(false);

                            mAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}

