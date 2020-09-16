package com.chatapp.synchat.app;

/**
 * Activity showing the list of all the chats
 * you have done,with most recent one on top and sorted in decreasing order of time of last message
 */


import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.ChatListAdapter;
import com.chatapp.synchat.app.dialog.ChatLockPwdDialog;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.dialog.MuteAlertDialog;
import com.chatapp.synchat.app.dialog.ProfileImageDialog;
import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.app.utils.BroadcastInfoSession;
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
import com.chatapp.synchat.core.message.IncomingMessage;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.BroadcastInfoPojo;
import com.chatapp.synchat.core.model.ChatLockPojo;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.MuteStatusPojo;
import com.chatapp.synchat.core.model.MuteUserPojo;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.service.Constants;
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
import java.util.List;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 */
public class ChatListCopy extends Fragment implements MuteAlertDialog.MuteAlertCloseListener {

    private ChatListAdapter mAdapter;
    private RecyclerView recyclerView_chat;
    private ArrayList<MessageItemChat> mChatData = new ArrayList<>();
    private SearchView searchView;
    private AvnNextLTProRegTextView userMessagechat;
    AvnNextLTProRegTextView archivetext;
    private RelativeLayout archive_relativelayout;
    private ImageButton ibNewChat;
    View view;
    Bitmap myTemp = null;
    private MessageDbController db;
    private static String SECRET_CHAT_PREFIX = "secret_";
    HashMap<String, MessageItemChat> uniqueStore = new HashMap<>();
    HashMap<String, Long> typingEventMap = new HashMap<>();
    private String uniqueCurrentID;
    String phoneno;
    FastScrollRecyclerView fastScroller;
    String archivecount, archivecount_group;
    String username, profileimage, value;
    Boolean myMenuClickFlag = false;
    private final int QR_REQUEST_CODE = 25;
    Session session;
    private SessionManager sessionManager;
    int count = 0;
    String receiverDocumentID = new String();
    private Menu menuLongClick, menuNormal;
    Boolean muteflag = true;
    private UserInfoSession userInfoSession;
    public static boolean isChatListPage;
    String sessionarchived;
    String receiverID;
    Boolean isPasswordProtected = true;
    private ArrayList<MessageItemChat> selectedChatItems = new ArrayList<>();
    private Handler typingHandler;
    private long timeOutTyping = 3000;
    Getcontactname getcontactname;
    private static final String TAG = "ChatList" + ">>>@@@@";
    private LinearLayoutManager mChatListManager;
    private int mFirstVisibleItemPosition, mLastVisibleItemPosition;
    ShortcutBadgeManager shortcutBadgeManager;
    private boolean isSecretChatDeleted;
    //grouphcatlist
    private GroupInfoSession groupInfoSession;
    private String mCurrentUserId;
    private ArrayList<String> getUsersList = new ArrayList<>();
    public boolean isthisGroup = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true);

//        test();

        if (view == null) {
            view = inflater.inflate(R.layout.chatlist, container, false);
        } else {
            if (view.getParent() != null)
                ((ViewGroup) view.getParent()).removeView(view);
        }
        shortcutBadgeManager = new ShortcutBadgeManager(getActivity());
        uniqueCurrentID = SessionManager.getInstance(getActivity()).getCurrentUserID();
        userInfoSession = new UserInfoSession(getContext());
        getcontactname = new Getcontactname(getContext());
        userMessagechat = (AvnNextLTProRegTextView) view.findViewById(R.id.userMessagechat);
        archive_relativelayout = (RelativeLayout) view.findViewById(R.id.archive_relativelayout);
        archivetext = (AvnNextLTProRegTextView) view.findViewById(R.id.archive);

        recyclerView_chat = (RecyclerView) view.findViewById(R.id.rv);
        recyclerView_chat.setHasFixedSize(true);

        mAdapter = new ChatListAdapter(view.getContext(), mChatData);
        mChatListManager = new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView_chat.setLayoutManager(mChatListManager);
//        recyclerView_chat.setItemAnimator(new DefaultItemAnimator());
        recyclerView_chat.setAdapter(mAdapter);
        recyclerView_chat.setNestedScrollingEnabled(true);
        recyclerView_chat.setHasFixedSize(true);
        recyclerView_chat.addOnScrollListener(chatListener);

        db = CoreController.getDBInstance(getActivity());
        sessionManager = SessionManager.getInstance(getActivity());
        session = new Session(getActivity());
        archivecount = String.valueOf(session.getarchivecount());
        mCurrentUserId = sessionManager.getCurrentUserID();

        typingHandler = new Handler();

        setArchieveText();
        //groupchat
        groupInfoSession = new GroupInfoSession(getActivity());

        ibNewChat = (ImageButton) view.findViewById(R.id.ibNewChat);

        ibNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchSettingContactScreen(getActivity());
            }
        });

        archivetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aIntent = new Intent(getActivity(), Archivelist.class);
                aIntent.putExtra("from", "chatlist");
                startActivity(aIntent);

            }
        });

        mAdapter.setChatListItemClickListener(new ChatListAdapter.ChatListItemClickListener() {
            @Override
            public void onItemClick(MessageItemChat list, View view, int position, long imageTS) {
                Log.d(TAG, "onItemClick: ");
                if (view.getId() == R.id.storeImage) {
                    String Uid = mChatData.get(position).getMessageId().split("-")[1];
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("MessageItem", mChatData.get(position));
                    bundle.putString("userID", Uid);
//                    /*if(view instanceof CircleImageView) {
//                        CircleImageView imageView = (CircleImageView) view;
//                        Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
//                        SerializeBitmap serializeBitmap = new SerializeBitmap();
//                        serializeBitmap.setBitmap(bitmap);
//
//                        bundle.putSerializable("ProfilePic", serializeBitmap);
//                    }*/
//
////                    String imageTS = contactDB_sqlite.getDpUpdatedTime(Uid);
////                    final String avatar = Constants.USER_PROFILE_URL + userId + ".jpg?id=" + imageTS;
                    bundle.putSerializable("ProfilePic", null);
                    //need
                    if (list.isGroup()) {
                        bundle.putSerializable("GroupChat", true);
                    } else {
                        bundle.putSerializable("GroupChat", false);
                    }
                    bundle.putLong("imageTS", imageTS);
                    bundle.putBoolean("FromSecretChat", false);
                    ProfileImageDialog dialog = new ProfileImageDialog();
                    dialog.setArguments(bundle);
                    dialog.show(getFragmentManager(), "profile");
                }
//                /*else if(isPasswordProtected){
//
//                    final CustomAlertDialog dialogAlert = new CustomAlertDialog();
//                    dialogAlert.setTitle("Enter Password");
//                    dialogAlert.setEditTextdata("Enter your password");
//                    dialogAlert.setPositiveButtonText("OK");
//                    dialogAlert.setNegativeButtonText("Forgot Password");
//
//                    dialogAlert.setCancelable(false);
//
//                    dialogAlert.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//                        @Override
//                        public void onPositiveButtonClick() {
//
//                            dialogAlert.dismiss();
//                            callSuccessAlert();
//                        }
//
//                        @Override
//                        public void onNegativeButtonClick() {
//                            dialogAlert.dismiss();
//                        }
//                    });
//                    dialogAlert.show(getFragmentManager(), "PasswordAlert");
//                }*/
                else {

                    if (position >= 0) {
                        ChatLockPojo lockPojo = getChatLockdetailfromDB(position, list);

                        if (myMenuClickFlag) {

                            mChatData.get(position).setSelected(!mChatData.get(position).isSelected());

                            if (mChatData.get(position).isSelected()) {
                                selectedChatItems.add(mChatData.get(position));
                                count = count + 1;
                            } else {
                                selectedChatItems.remove(mChatData.get(position));
                                count = count - 1;
                            }
                            mAdapter.notifyDataSetChanged();

                            onCreateOptionsMenu(menuLongClick, getActivity().getMenuInflater());

                            if (list.isGroup()) {
                                if (count <= 0) {
                                    myMenuClickFlag = false;
                                    onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                                    count = 0;
//                            getActivity().getMenuInflater().inflate(R.menu.chats_screen, menuNormal);
                                }
                            } else {
                                if (count == 0) {
                                    myMenuClickFlag = false;
                                    onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
//                            getActivity().getMenuInflater().inflate(R.menu.chats_screen, menuNormal);
                                }
                            }

                        } else if (sessionManager.getLockChatEnabled().equals("1") && lockPojo != null) {
                            String stat = lockPojo.getStatus();
                            String pwd = lockPojo.getPassword();

                            MessageItemChat e = mAdapter.getItem(position);
                            String docID = e.getMessageId();
                            String[] ids = docID.split("-");
                            String documentid = "";
                            if (list.isGroup()) {
                                documentid = ids[0] + "-" + ids[1] + "-g";
                            } else {
                                documentid = ids[0] + "-" + ids[1];
                            }

                            if (stat.equals("1")) {
                                openUnlockChatDialog(documentid, stat, pwd, position, list);
                            } else {
                                navigateTochatviewpage(position, list);
                            }
                        } else {
                            navigateTochatviewpage(position, list);
                        }
                    }

                }
                Log.d("ChatListEnd", Calendar.getInstance().getTimeInMillis() + "");
            }

            @Override
            public void onItemLongClick(MessageItemChat list, View view, int position) {
                isthisGroup = list.isGroup();
                if (position >= 0) {
                    mChatData.get(position).setSelected(!mChatData.get(position).isSelected());

                    ImageView ivProfielPic = (ImageView) view.findViewById(R.id.storeImage);
                    if (ivProfielPic.getDrawable() != null) {
                        try {
                            //  myTemp = ((BitmapDrawable) ivProfielPic.getDrawable()).getBitmap();

                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                    }


                    if (mChatData.get(position).isSelected()) {
                        selectedChatItems.add(mChatData.get(position));
                        count = count + 1;
                    } else {
                        selectedChatItems.remove(mChatData.get(position));
                        count = count - 1;
                    }
//                mAdapter.notifyDataSetChanged();
                    if (list.isGroup()) {
                        mAdapter.notifyDataSetChanged();
                        myMenuClickFlag = true;
                        MessageItemChat e = mChatData.get(position);
                        e.setSelected(true);
                        mChatData.set(position, e);
                        mAdapter.notifyDataSetChanged();
                        username = e.getSenderName();
                        profileimage = e.getAvatarImageUrl();
                    } else {
                        myMenuClickFlag = true;
                        MessageItemChat e = mChatData.get(position);
                        e.setSelected(true);
                        mChatData.set(position, e);
                        mAdapter.notifyDataSetChanged();
                        username = e.getSenderName();
                        phoneno = e.getSenderMsisdn();
                        profileimage = e.getAvatarImageUrl();
                        if (count == 0) {
                            myMenuClickFlag = false;
                        }
                    }
//                count = count + 1;
                    onCreateOptionsMenu(menuLongClick, getActivity().getMenuInflater()); /*else {
                    tick.setVisibility(View.GONE);


                }*/
                }
            }
        });

        return view;
    }

//    private void test() {
//        File dbDir = new File(getActivity().getFilesDir() + "/what_up_db.cblite2");
//        String[] allFiles = dbDir.list();
//
//        File backUpFolder = new File(MessageFactory.DATABASE_PATH);
//        if (!backUpFolder.exists()) {
//            backUpFolder.mkdirs();
//        }
//        String backUpFilePath = backUpFolder + "/" + getResources().getString(R.string.app_name) + "_db.zip";
//
//        for (int i = 0; i < allFiles.length; i++) {
//
//            try {
//                File myFile = new File(dbDir + "/" + allFiles[i]);
//                boolean isDirectory = myFile.isDirectory();
//                if (!isDirectory) {
//                    String path = backUpFolder + "/" + myFile.getName();
//                    File zipFile = new File(path);
//                    if (zipFile.exists()) {
//                        zipFile.delete();
//                        zipFile.createNewFile();
//                    }
//                    FileOutputStream dest = new FileOutputStream(path);
//                    BufferedOutputStream bufferedStream = new BufferedOutputStream(dest);
//                    ZipOutputStream out = new ZipOutputStream(bufferedStream);
//                    byte data[] = new byte[1024];
//
//                    FileInputStream fi = new FileInputStream(dbDir + "/" + allFiles[i]);
//                    BufferedInputStream origin = new BufferedInputStream(fi, 1024);
//
//                    ZipEntry entry = new ZipEntry(allFiles[i]);
//                    out.putNextEntry(entry);
//                    int count;
//
//                    while ((count = origin.read(data, 0, 1024)) != -1) {
//                        out.write(data, 0, count);
//                    }
//
//                    fi.close();
//                    origin.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }


    private void navigateTochatviewpage(int position, MessageItemChat list) {
        Intent intent = new Intent(getActivity(), ChatViewActivity.class);
        NotificationManager notifManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
        MessageItemChat e = mAdapter.getItem(position);
        if (e != null) {
            if (e.getStatus() != null) {
                if (list.isGroup()) {
                    String[] array = e.getMessageId().split("-");
                    intent.putExtra("receiverUid", e.getNumberInDevice());
                    intent.putExtra("receiverName", e.getSenderName());

                    receiverDocumentID = array[1];
                    intent.putExtra("documentId", receiverDocumentID);
                    intent.putExtra("Username", e.getSenderName());
                    intent.putExtra("Image", e.getAvatarImageUrl());
                    intent.putExtra("type", 0);

                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.fast_enter,R.anim.fast_exit);

                    //   getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                } else {
                    getcontactname.navigateToChatViewpagewithmessageitems(e, "chatlist");
                }

            } else {
                Toast.makeText(getActivity(), getResources().getString(R.string.status_not_available), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private ChatLockPojo getChatLockdetailfromDB(int position, MessageItemChat list) {
        MessageItemChat e = mAdapter.getItem(position);
        String docID = e.getMessageId();
        String[] id = docID.split("-");
        ChatLockPojo lockPojo;
        if (list.isGroup()) {
            String documentID = id[0] + "-" + id[1] + "-g";
            String convId = userInfoSession.getChatConvId(documentID);
            String receiverId = userInfoSession.getReceiverIdByConvId(convId);
            lockPojo = db.getChatLockData(receiverId, MessageFactory.CHAT_TYPE_GROUP);
        } else {
            String documentID = id[0] + "-" + id[1];
            String convId = userInfoSession.getChatConvId(documentID);
            String receiverId = userInfoSession.getReceiverIdByConvId(convId);
            lockPojo = db.getChatLockData(receiverId, MessageFactory.CHAT_TYPE_SINGLE);
        }

        return lockPojo;
    }

//    private void callSuccessAlert() {
//
//        final CustomAlertDialog dialogAlert = new CustomAlertDialog();
//        dialogAlert.setTitle("Password reset link has been sent to your registered email id\n");
//        dialogAlert.setImagedrawable("setImage");
//        dialogAlert.setCancelable(true);
//
//        dialogAlert.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//            @Override
//            public void onPositiveButtonClick() {
//
//            }
//
//            @Override
//            public void onNegativeButtonClick() {
//
//            }
//        });
//
//        dialogAlert.show(getFragmentManager(), "PasswordAlert");
//    }


    private void openUnlockChatDialog(String docId, String status, String pwd, int position, MessageItemChat list) {

        if (list.isGroup()) {
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
        } else {
            String convId = userInfoSession.getChatConvId(docId);
            ChatLockPwdDialog dialog = new ChatLockPwdDialog();
            dialog.setTextLabel1("Enter your Password");
            dialog.setEditTextdata("Enter new Password");
            dialog.setforgotpwdlabel("Forgot Password");
            dialog.setHeader("Unlock Chat");
            dialog.setButtonText("Unlock");
            Bundle bundle = new Bundle();
            bundle.putSerializable("MessageItem", mAdapter.getItem(position));
            bundle.putString("convID", convId);
            bundle.putString("status", "1");
            bundle.putString("pwd", pwd);
            bundle.putString("page", "chatlist");
            bundle.putString("type", "single");
            bundle.putString("from", uniqueCurrentID);
            dialog.setArguments(bundle);
            dialog.show(getFragmentManager(), "chatunLock");
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        loadDbChatItem();
        isChatListPage = true;

        setArchieveText();
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            } else {
                ShortcutBadger.removeCountOrThrow(getContext()); //for 1.1.4+
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                mAdapter.notifyDataSetChanged();
            }
        }, 2000);


    }

    private void setArchieveText() {
        archivecount = String.valueOf(session.getarchivecount());
        archivecount_group = String.valueOf(session.getarchivecountgroup());

        int count = Integer.parseInt(archivecount) + Integer.parseInt(archivecount_group);

        if (session.getarchivecount() <= 0 && session.getarchivecountgroup() <= 0) {
            archive_relativelayout.setVisibility(View.GONE);
        } else {
            archive_relativelayout.setVisibility(View.VISIBLE);
            archivetext.setText("Archived chats (" + count + ")");
        }


//        if (session.getarchivecountgroup() <= 0) {
//            archive_relativelayout.setVisibility(View.GONE);
//        } else {
//            archive_relativelayout.setVisibility(View.VISIBLE);
//            archivetext.setText("Archived chats (" + count + ")");
//        }
    }

    private void loadDbChatItem() {

        try {

            MessageDbController dbManager = CoreController.getDBInstance(getActivity());
            ArrayList<MessageItemChat> singleChats = dbManager.selectChatList(MessageFactory.CHAT_TYPE_SINGLE);
            BroadcastInfoSession broadcastInfoSession=new BroadcastInfoSession(getContext());

            ArrayList<String> d=new ArrayList<>();
            List<BroadcastInfoPojo> broadcastInfoPojos=new ArrayList<>();

            Map<String, ?> allEntries = broadcastInfoSession.broadcastInfoPref.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                d.add( entry.getKey());
                // Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            }
            int index;
            int si=singleChats.size();


            ArrayList<String> bID=new ArrayList<>();
            for(int i=0;i<d.size();i++) {
                bID.add( broadcastInfoSession.getBroadcastInfo(d.get(i)).getBroadcastId());

                for(int j=0;j<singleChats.size();j++)
                {
                    if(singleChats.get(j).getReceiverID().equals(bID.get(i)))
                    {
                        singleChats.remove(singleChats.get(j));

                    }
                }
            }



            ArrayList<MessageItemChat> groupChats = db.selectChatList(MessageFactory.CHAT_TYPE_GROUP);
            //  ArrayList<MessageItemChat> secretChats = db.selectChatList(MessageFactory.CHAT_TYPE_SECRET);
            Session session = new Session(getActivity());
            uniqueStore.clear();

            for (MessageItemChat msgItem : singleChats) {
                if (msgItem.getReceiverID() != null) {
                    receiverID = msgItem.getReceiverID();
                    String docId = uniqueCurrentID + "-" + receiverID;
                    if (!session.getarchive(docId)) {
                        String msisdn = null, name = null;
                        if (msgItem.getSenderMsisdn() != null) {
                            name = getcontactname.getSendername(receiverID, msgItem.getSenderMsisdn());
                            msisdn = msgItem.getSenderMsisdn();
                        } else {
                            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getContext());
                            ChatappContactModel contactModel = contactDB_sqlite.getUserOpponenetDetails(receiverID);
                            if (contactModel != null) {
                                msisdn = contactModel.getMsisdn();
                                name = getcontactname.getSendername(receiverID, msisdn);
                            }
                        }

                        if (db.getChatClearedStatus(receiverID, MessageFactory.CHAT_TYPE_SINGLE)
                                == MessageFactory.CHAT_STATUS_CLEARED) {
                            msgItem = new MessageItemChat();
                            msgItem.setReceiverID(receiverID);
                            msgItem.setMessageType("");
                            msgItem.setMessageId(docId.concat("-0"));
                            msgItem.setTextMessage("");
                            msgItem.setTS("0");
                            msgItem.setGroup(false);
                        }

                        if (msisdn != null) {
                            msgItem.setSenderMsisdn(msisdn);
                            msgItem.setSenderName(name);
                            msgItem.setGroup(false);
                            uniqueStore.put(docId, msgItem);
                        }
                    }

                    updateToconvSettings(uniqueCurrentID, receiverID);
                }
            }

/*
        for (MessageItemChat msgItem : secretChats) {
            if (msgItem.getReceiverID() != null) {
                receiverID = msgItem.getReceiverID();
                String docId = uniqueCurrentID + "-" + receiverID;
                if (!session.getarchive(docId)) {
                    String msisdn = null, name = null;
                    if (msgItem.getSenderMsisdn() != null) {
                        name = getcontactname.getSendername(receiverID, msgItem.getSenderMsisdn());
                        msisdn = msgItem.getSenderMsisdn();
                    } else {
                        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getContext());
                        ChatappContactModel contactModel = contactDB_sqlite.getUserOpponenetDetails(receiverID);
                        if (contactModel != null) {
                            msisdn = contactModel.getMsisdn();
                            name = getcontactname.getSendername(receiverID, msisdn);
                        }
                    }

                    if (db.getChatClearedStatus(receiverID, MessageFactory.CHAT_TYPE_SINGLE)
                            == MessageFactory.CHAT_STATUS_CLEARED) {
                        msgItem = new MessageItemChat();
                        msgItem.setReceiverID(receiverID);
                        msgItem.setMessageType("");
                        msgItem.setMessageId(docId.concat("-0"));
                        msgItem.setTextMessage("");
                        msgItem.setTS("0");
                        msgItem.setGroup(false);
                    }
                    msgItem.setSecretChat(true);
                    if (msisdn != null) {
                        msgItem.setSenderMsisdn(msisdn);
                        msgItem.setSenderName(name);
                        msgItem.setGroup(false);
                        uniqueStore.put(SECRET_CHAT_PREFIX + docId, msgItem);
                    }
                }

                updateToconvSettings(uniqueCurrentID, receiverID);
            }
        }*/

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
                    msgItem.setGroup(true);
                }

                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
                if (!session.getarchivegroup(docId)) {

                    if (infoPojo != null) {
                        String avatarPath = infoPojo.getAvatarPath();
                        msgItem.setAvatarImageUrl(avatarPath);
                        msgItem.setSenderName(infoPojo.getGroupName());
                        msgItem.setGroupName(infoPojo.getGroupName());
                        msgItem.setGroup(true);
                    }
                    uniqueStore.put(docId, msgItem);


                }
            }
            updateChatList();
            setArchieveText();


        }catch (Exception e){
            e.printStackTrace();
        }



    }

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


    private void updateToconvSettings(String from, String to) {
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_TO_CONV_SETTING);
        try {
            JSONObject obj = new JSONObject();
            obj.put("from", from);
            obj.put("to", to);
            obj.put("type", "single");
            obj.put("chat_type", "normal");
            messageEvent.setMessageObject(obj);
            EventBus.getDefault().post(messageEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateChatList() {
        mChatData.clear();

        for (MessageItemChat value : uniqueStore.values()) {

            // For always showing Group Title
            if (value.getMessageId() != null && value.getMessageId().contains("-g-")) {
                value.setSenderName(value.getGroupName());
            } else {


                String phContactName = getcontactname.getSendername(value.getReceiverID(), value.getSenderMsisdn());
                value.setSenderName(phContactName);
            }

            mChatData.add(value);
        }
        if (mChatData.size() > 0) {
            userMessagechat.setVisibility(View.GONE);
        }
        Collections.sort(mChatData, new ChatListSorter());
        mAdapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_TYPING)) {
            loadTypingStatus(event);
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
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MESSAGE)) {
            loadMessage(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_QR_DATA_RES)) {
            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                Log.d("QRSCANNER", object.toString());
                Log.e("web", "" + object);
                String err = object.getString("err");
                Log.e("userId", "" + err);
                if (err.equalsIgnoreCase("0")) {
                    Toast.makeText(getActivity(), getResources().getString(R.string.app_name) + "Connected", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Couldn't scan code", Toast.LENGTH_SHORT).show();
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
            //loadarchivelist(obj[0].toString());
//            loadarchive(obj[0].toString());
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_PRIVACY_SETTINGS)) {
            loadPrivacySetting(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_BLOCK_USER)) {
            loadBlockUserdata(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MUTE)) {
            loadMuteMessage(event.getObjectsArray()[0].toString());
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MARKREAD)) {
            loadMark();
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MESSAGE_RES)) {
            loadMessageRes(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GET_USER_DETAILS)) {
//            loadUserDetails(event.getObjectsArray()[0].toString());
            new loadUserDetails(event.getObjectsArray()[0].toString()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_DELETE_MESSAGE)) {
            deleteSingleMessage(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_IMAGE_UPLOAD)) {
            updateProfileImage(event);
        }
    }

    private void updateProfileImage(ReceviceMessageEvent event) {
        Object[] array = event.getObjectsArray();
        try {
            JSONObject objects = new JSONObject(array[0].toString());
            String from = objects.getString("from");
            String type = objects.getString("type");
            String docId = uniqueCurrentID + "-" + from;
            if (type.equalsIgnoreCase("single") && (uniqueStore.containsKey(docId)
                    || uniqueStore.containsKey(SECRET_CHAT_PREFIX + docId))) {
                String path = objects.getString("file") + "?id=" + Calendar.getInstance().getTimeInMillis();
                Log.d(TAG, "updateProfileImage: " + path);
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Log.e(TAG, "updateProfileImage: ", e);
        }
    }

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

    public void loadExitMessage(JSONObject object) {
        loadDbChatItem();
    }


    public void loadChangeGroupNameMessage(JSONObject object) {
        loadDbChatItem();
    }


    private void loadMark() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void loadMuteMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");
            String chatType = object.getString("type");
            String secretType = "no";
            if (object.has("secret_type")) {
                secretType = object.getString("secret_type");
            }

            if (from != null && from.equalsIgnoreCase(uniqueCurrentID)
                    && chatType != null && chatType.equalsIgnoreCase("single")
                    && secretType.equalsIgnoreCase("no") && mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            } else if (from != null && from.equalsIgnoreCase(mCurrentUserId) &&
                    chatType != null && chatType.equalsIgnoreCase("group") && mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadBlockUserdata(ReceviceMessageEvent event) {
        try {
            Object[] obj = event.getObjectsArray();
            JSONObject object = new JSONObject(obj[0].toString());
            Log.e("Response---", object.toString());
            String stat = object.getString("status");
            String toid = object.getString("to");
            String from = object.getString("from");
            if (uniqueCurrentID != null && uniqueCurrentID.equalsIgnoreCase(toid)) {
                mAdapter.notifyDataSetChanged();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

   /* public void loadToConvSettings(ReceviceMessageEvent event){
        Object[] array = event.getObjectsArray();
        try {
            JSONObject objects = new JSONObject(array[0].toString());
            String is_blocked = objects.getString("is_blocked");
            String to = objects.getString("to");
            if(is_blocked.equalsIgnoreCase("1")){
                session.putblockedUserID(to,true);
            } else {
                session.putblockedUserID(to,false);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }*/

    private void loadPrivacySetting(ReceviceMessageEvent event) {
        mAdapter.notifyDataSetChanged();
    }

    private void loadarchive(String data) {
        try {
            JSONObject object = new JSONObject(data);

            try {
                String from = object.getString("from");
                String convId = object.getString("convId");
                String type = object.getString("type");
                String status = object.getString("status");

                if (from != null && from.equalsIgnoreCase(uniqueCurrentID)) {
                    String receiverId;
                    if (type != null && type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                        receiverId = convId;
                    } else {
                        receiverId = userInfoSession.getReceiverIdByConvId(convId);
                    }

                    if (!receiverId.equals("")) {
                        String docId = from.concat("-").concat(receiverId);
                        if (type != null && type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
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

    private void loadDeleteChat(String data) {
        try {
            JSONObject object = new JSONObject(data);

            try {
                String from = object.getString("from");
                String convId = object.getString("convId");
                String type = object.getString("type");

                if (from != null && from.equalsIgnoreCase(uniqueCurrentID)) {
                    String receiverId;
                    if (type != null && type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                        receiverId = convId;
                    } else {
                        receiverId = userInfoSession.getReceiverIdByConvId(convId);
                    }

                    if (!receiverId.equals("")) {
                        String docId = from.concat("-").concat(receiverId);
                        if (type != null && type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                            docId = docId.concat("-g");
                        }

                        if (isSecretChatDeleted) {
                            isSecretChatDeleted = false;
                            if (uniqueStore.containsKey(SECRET_CHAT_PREFIX + docId))
                                uniqueStore.remove(SECRET_CHAT_PREFIX + docId);
                            updateChatList();
                            mAdapter.notifyDataSetChanged();

                            HomeScreen activity = (HomeScreen) getActivity();
                            activity.changeTabTextCount();
                        } else {
                            if (uniqueStore.containsKey(docId))
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

    private void changeMsgViewedStatus(String data) {

        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");
            String mode = object.getString("mode");

            if (from != null && from.equalsIgnoreCase(uniqueCurrentID) &&
                    mode != null && mode.equalsIgnoreCase("web")) {
                String chatType = object.getString("type");
                /*String convId = object.getString("convId");
                String to = object.getString("to");*/

                if (chatType != null && chatType.equalsIgnoreCase("single")) {
                    loadDbChatItem();
                    HomeScreen activity = (HomeScreen) getActivity();
                    activity.changeTabTextCount();
                } else if (chatType != null && chatType.equalsIgnoreCase("group")) {
                    loadDbChatItem();
                    HomeScreen activity = (HomeScreen) getActivity();
                    activity.changeTabTextCount();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadMessage(ReceviceMessageEvent event) {
        Object[] array = event.getObjectsArray();
        try {

            JSONObject objects = new JSONObject(array[0].toString());


            String secretType = objects.getString("secret_type");
            if (secretType != null && secretType.equalsIgnoreCase("no")) {
                IncomingMessage incomingMsg = new IncomingMessage(getActivity());
                MessageItemChat item = incomingMsg.loadSingleMessage(objects);
                String from = objects.getString("from");
                String to = objects.getString("to");
                String id = objects.getString("id");

                String docId;
                if (from != null && from.equalsIgnoreCase(uniqueCurrentID)) {
                    docId = from + "-" + to;
                } else {
                    docId = to + "-" + from;
                }
                item.setMessageId(docId + "-" + id);

                if (session.getarchivecount() >= 0) {
                    if (session.getarchive(uniqueCurrentID + "-" + to))
                        session.removearchive(uniqueCurrentID + "-" + to);

                    setArchieveText();
                }
                if (session.getarchivecountgroup() >= 0) {
                    if (session.getarchivegroup(uniqueCurrentID + "-" + to + "-g"))
                        session.removearchivegroup(uniqueCurrentID + "-" + to + "-g");
                }

                if (uniqueStore.containsKey(docId)) {
                    MessageItemChat oldItem = uniqueStore.get(docId);
                    if (oldItem.isSelected()) {
                        item.setSelected(oldItem.isSelected());
                        int selectedIndex = selectedChatItems.indexOf(oldItem);
                        selectedChatItems.set(selectedIndex, item);
                    }
                } else if (uniqueStore.containsKey(SECRET_CHAT_PREFIX + docId)) {
                    MessageItemChat oldItem = uniqueStore.get(SECRET_CHAT_PREFIX + docId);
                    if (oldItem.isSelected()) {
                        item.setSelected(oldItem.isSelected());
                        int selectedIndex = selectedChatItems.indexOf(oldItem);
                        selectedChatItems.set(selectedIndex, item);
                    }
                }

                // db.updateChatMessage(uniqueID, item);
                uniqueStore.put(docId, item);
                updateChatList();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadMessageRes(ReceviceMessageEvent event) {
        Object[] array = event.getObjectsArray();
        try {
            JSONObject objects = new JSONObject(array[0].toString());
            int errorState = objects.getInt("err");
            if (errorState == 0) {
                JSONObject msgData = objects.getJSONObject("data");

                String from = msgData.getString("from");
                String secretType = msgData.getString("secret_type");
                if (from != null && from.equalsIgnoreCase(uniqueCurrentID) &&
                        secretType != null && secretType.equalsIgnoreCase("no")) {

                    String toUserId = msgData.getString("to");

//                    IncomingMessage incomingMessage = new IncomingMessage(getActivity());
//                    MessageItemChat newMsgItem = incomingMessage.loadSingleMessageFromWeb(objects);
//                    if (newMsgItem != null) {
//                        mChatData.add(newMsgItem);
//                    }


                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        Log.e("flag", "" + myMenuClickFlag);
        menuNormal = menu;
        menuLongClick = menu;

        if (isthisGroup) {
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
                boolean group = false;
                boolean single = false;
                if (exitanddelete.contains(true) && exitanddelete.contains(false)) {
                    Exitgroup.setVisible(false);
                    Deletegroup.setVisible(false);
                } else if (exitanddelete.contains(true)) {
                    for (int m = 0; m <= selectedChatItems.size() - 1; m++) {
                        if (selectedChatItems.get(m).isGroup()) {
                            group = true;
                        } else {
                            single = true;
                        }
                    }
                    if (group == true && single == false) {
                        Exitgroup.setVisible(true);
                    } else {
                        Exitgroup.setVisible(false);
                    }
                    Deletegroup.setVisible(false);
                } else {
                    Exitgroup.setVisible(false);
                    for (int m = 0; m <= selectedChatItems.size() - 1; m++) {
                        if (selectedChatItems.get(m).isGroup()) {
                            group = true;
                        } else {
                            single = true;
                        }
                    }
                    if (group == true && single == false) {
                        Deletegroup.setVisible(true);
                    } else {
                        Deletegroup.setVisible(false);
                    }

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
                    markasunread_menu.setVisible(false);
                } else {
                    markread_menu.setVisible(false);
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
                        viewcontact(true);
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
                                ContactDB_Sqlite contat = CoreController.getContactSqliteDBintstance(getActivity());
                                db.deleteChat(docId, MessageFactory.CHAT_TYPE_GROUP);
                                ChatappContactModel chatappContactModel = contat.getUserOpponenetDetails(receiverId);
                                if (chatappContactModel != null) {
                                    Bitmap bitmap = null;
                                    if (myTemp != null) {
                                        bitmap = Bitmap.createScaledBitmap(myTemp, 128, 128, true);
                                    }

/*                                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                                        // Marshmallow+
                                        shortcutBadgeManager.androidoreo_shortcut(getActivity(), true, receiverId, username,
                                                profileimage, null, bitmap,"delete");
                                    } else {
                                        shortcutBadgeManager.RemoveApplicationShortcutIcon(getActivity(), true, receiverId, username,
                                                profileimage, null, bitmap);
//                                ShortcutBadgeManager.RemoveApplicationShortcutIcon(getActivity(),true,username,receiverId,chatappContactModel.getAvatarImageUrl(),chatappContactModel.getMsisdn());

                                    }*/
                                }

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
                            dialog.setMuteAlertCloseListener(ChatListCopy.this);
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
//                MenuItem logout = menu.findItem(R.id.logout);
//                logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        final String msg = "Do you really want to logout from web chat?";
//                        final CustomAlertDialog dialog = new CustomAlertDialog();
//                        dialog.setNegativeButtonText("Cancel");
//                        dialog.setPositiveButtonText("Confirm");
//                        dialog.setMessage(msg);
//                        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//                            @Override
//                            public void onPositiveButtonClick() {
//                                NotificationManager notifManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//                                notifManager.cancelAll();
//                                if (ConnectivityInfo.isInternetConnected(getActivity())) {
////                                unsetDeviceToken();
//                                    notifyLogoutToServer();
//                                } else {
//                                    Toast.makeText(getActivity(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onNegativeButtonClick() {
//                                dialog.dismiss();
//                            }
//                        });
//                        dialog.show(getFragmentManager(), "Delete member alert");
//                        return true;
//                    }
//                });

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
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        return false;
                    }
                });

//                searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//                    @Override
//                    public boolean onClose() {
//
//                    //    menu.findItem(R.id.chatsceen_chatIcon).setVisible(true);
//                        return false;
//                    }
//                });
//
//                searchView.setOnSearchClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                     //   menu.findItem(R.id.chatsceen_chatIcon).setVisible(false);
//                    }
//                });

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
        } else {
            if (myMenuClickFlag) {
                System.out.println("-------chatlist_longclick_menu---------");
                getActivity().getMenuInflater().inflate(R.menu.chatlist_longclick_menu, menu);
                MenuItem add_shortcut = menu.findItem(R.id.addchatshortcut_menu);
                final MenuItem viewcontact = menu.findItem(R.id.viewcontact_menu);
                MenuItem markasunread_menu = menu.findItem(R.id.markasunread_menu);
                MenuItem addcontact_menu = menu.findItem(R.id.adddcontact_menu);
                MenuItem mute = menu.findItem(R.id.mute_menu);
                MenuItem unmute = menu.findItem(R.id.unmute_menu);
                final MenuItem delete = menu.findItem(R.id.delete_menu);
                MenuItem markread_menu = menu.findItem(R.id.markread_menu);
                final MenuItem archive = menu.findItem(R.id.archive_menu);
                addcontact_menu.setVisible(false);
                if (count >= 2) {
                    addcontact_menu.setVisible(false);
                    add_shortcut.setVisible(false);
                    viewcontact.setVisible(false);
                    delete.setVisible(false);
                    markasunread_menu.setVisible(false);
                    markread_menu.setVisible(false);
                }

                ArrayList<Boolean> mutearray = new ArrayList<>();
                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getActivity());
                for (int i = 0; i < selectedChatItems.size(); i++) {
                    String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                    String receiverId = arrIds[1];
                    String docId = uniqueCurrentID + "-" + receiverId;
                    String convId = userInfoSession.getChatConvId(docId);
                    MuteStatusPojo muteData = contactDB_sqlite.getMuteStatus(uniqueCurrentID, receiverId, convId, false);
                    mutearray.add((muteData != null && muteData.getMuteStatus().equals("1")));

                    /*if (sessionManager.getMuteStatus(docId)) {

                     *//*mute.setVisible(true);
                    unmute.setVisible(false);*//*
                } else {
                    mutearray.add(false);
                    *//*mute.setVisible(false);
                    unmute.setVisible(true);*//*
                }*/
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
                    markasunread_menu.setVisible(false);
                } else {
                    markread_menu.setVisible(false);
                    markasunread_menu.setVisible(false);
                }
            /*if (markflag) {
                markasunread_menu.setVisible(true);
                markread_menu.setVisible(false);
            } else {
                markasunread_menu.setVisible(false);
                markread_menu.setVisible(true);
            }*/
                if (selectedChatItems.size() == 1) {
                    String[] arrIds = selectedChatItems.get(0).getMessageId().split("-");
                    String receiverId = arrIds[1];
                    long contactRevision = contactDB_sqlite.getOpponenet_UserDetails_savedRevision(receiverId);
//                long contactRevision = contactsDB.getSavedRevision(receiverId);
                    long syncedRevision = sessionManager.getContactSavedRevision();
                    if (syncedRevision > contactRevision) {
                        addcontact_menu.setVisible(false);
                    }
                }
                if (selectedChatItems.size() == 1 && selectedChatItems.get(0).isSecretChat()) {
                    archive.setVisible(false);
                    markasunread_menu.setVisible(false);
                    markread_menu.setVisible(false);
                    mute.setVisible(false);
                    unmute.setVisible(false);
                }
                addcontact_menu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneno);
                        startActivity(intent);
                        for (int i = 0; i < mChatData.size(); i++) {
                            mChatData.get(i).setSelected(false);
                        }
                        selectedChatItems.clear();
                        myMenuClickFlag = false;
                        count = 0;
                        mAdapter.notifyDataSetChanged();
                        onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                        return false;
                    }
                });
                delete.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        String msg = "";
                        if (selectedChatItems.size() > 1) {
                            msg = "Delete" + " " + selectedChatItems.size() + " " + "chats?";
                        } else {
                            msg = "Are you sure you want to delete chat messages in this chat?";
                        }
                        final CustomAlertDialog dialog = new CustomAlertDialog();
                        dialog.setNegativeButtonText("Cancel");
                        dialog.setPositiveButtonText("Delete");
                        dialog.setMessage(msg);
                        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                            @Override
                            public void onPositiveButtonClick() {
                                for (int i = 0; i < selectedChatItems.size(); i++) {
                                    try {
                                        String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                                        String receiverId = arrIds[1];
                                        String docId = uniqueCurrentID.concat("-").concat(receiverId);
                                        boolean isSecretChat = selectedChatItems.get(i).isSecretChat();
                                        if (isSecretChat) {
                                            db.deleteChat(docId + "-secret", MessageFactory.CHAT_TYPE_SECRET);
                                            if (selectedChatItems.size() == 1)
                                                isSecretChatDeleted = true;
                                        } else {
                                            db.deleteChat(docId, MessageFactory.CHAT_TYPE_SINGLE);
                                        }
                                        if (!isSecretChat && userInfoSession.hasChatConvId(docId)) {
                                            try {
                                                String convId = userInfoSession.getChatConvId(docId);

                                                JSONObject object = new JSONObject();
                                                object.put("convId", convId);
                                                object.put("from", uniqueCurrentID);
                                                object.put("type", MessageFactory.CHAT_TYPE_SINGLE);

                                                SendMessageEvent event = new SendMessageEvent();
                                                event.setEventName(SocketManager.EVENT_DELETE_CHAT);
                                                event.setMessageObject(object);
                                                EventBus.getDefault().post(event);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }


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
                            }

                            @Override
                            public void onNegativeButtonClick() {
                                selectedChatItems.clear();
                                loadDbChatItem();
                                myMenuClickFlag = false;
                                count = 0;
                                mAdapter.notifyDataSetChanged();
                                onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                                dialog.dismiss();
                            }
                        });
                        dialog.show(getFragmentManager(), "Delete member alert");
                        return false;
                    }
                });
                add_shortcut.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        for (int i = 0; i < selectedChatItems.size(); i++) {
                            try {
                                addShortcut();
                                int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                                mChatData.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected()); // setchangemenu();
                            } catch (Exception e) {
                            }

                        }
                        myMenuClickFlag = false;
                        count = 0;
                        mAdapter.notifyDataSetChanged();
                        onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                        return false;
                    }
                });
                viewcontact.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        //setchangemenu();
                        viewcontact(false);
                        myMenuClickFlag = false;
                        mAdapter.notifyDataSetChanged();
                        count = 0;
                        onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                        return false;
                    }
                });
                markasunread_menu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {


                        if (ConnectivityInfo.isInternetConnected(getContext())) {
                            for (int i = 0; i < selectedChatItems.size(); i++) {
                                try {
                                    String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                                    String receiverId = arrIds[1];
                                    String docId = uniqueCurrentID.concat("-").concat(receiverId);
                                    if (userInfoSession.hasChatConvId(docId)) {
                                        String convId = userInfoSession.getChatConvId(docId);
                                        try {
                                            JSONObject object = new JSONObject();
                                            object.put("convId", convId);
                                            object.put("from", uniqueCurrentID);
                                            object.put("status", 1);
                                            object.put("type", "single");
                                            SendMessageEvent event = new SendMessageEvent();
                                            event.setEventName(SocketManager.EVENT_MARKREAD);
                                            event.setMessageObject(object);
                                            EventBus.getDefault().post(event);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    session.putmark(receiverId);
                                    System.out.println("docid" + receiverId);

                                    int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                                    mChatData.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());


                                    HomeScreen activity = (HomeScreen) getActivity();
                                    activity.changeTabTextCount();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                // Badge working if supported devices


                                // setchangemenu();
                            }

                        } else {
                            for (int i = 0; i < selectedChatItems.size(); i++) {
                                int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                                mChatData.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());
                                mAdapter.notifyDataSetChanged();
                            }
                            Toast.makeText(getActivity(), "Check Network Connection", Toast.LENGTH_SHORT).show();
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

                        if (ConnectivityInfo.isInternetConnected(getContext())) {
                            for (int i = 0; i < selectedChatItems.size(); i++) {
                                try {
                                    if (selectedChatItems.size() > 0) {
                                        String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                                        String receiverId = arrIds[1];
                                        String docId = uniqueCurrentID.concat("-").concat(receiverId);
                                        if (userInfoSession.hasChatConvId(docId)) {
                                            String convId = userInfoSession.getChatConvId(docId);
                                            try {
                                                JSONObject object = new JSONObject();
                                                object.put("convId", convId);
                                                object.put("from", uniqueCurrentID);
                                                object.put("status", 0);
                                                object.put("type", "single");
                                                SendMessageEvent event = new SendMessageEvent();
                                                event.setEventName(SocketManager.EVENT_MARKREAD);
                                                event.setMessageObject(object);
                                                EventBus.getDefault().post(event);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }

                                            shortcutBadgeMgnr.removeMessageCount(convId);
                                            int totalCount = shortcutBadgeMgnr.getTotalCount();

                                            HomeScreen activity = (HomeScreen) getActivity();
                                            activity.changeTabTextCount();

                                            // Badge working if supported devices
                                            try {
                                                ShortcutBadger.applyCountOrThrow(getActivity(), totalCount);
                                            } catch (ShortcutBadgeException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        session.Removemark(receiverId);
                                        System.out.println("docid" + receiverId);
                                        int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                                        mChatData.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());
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
                        } else {
                            for (int i = 0; i < selectedChatItems.size(); i++) {
                                int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                                mChatData.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());
                                mAdapter.notifyDataSetChanged();
                            }
                            Toast.makeText(getActivity(), "Check Network Connection", Toast.LENGTH_SHORT).show();

                        }
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
                                            MessageFactory.CHAT_TYPE_SINGLE, "no");

                                    int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                                    if (chatIndex > -1) {
                                        mChatData.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            mAdapter.notifyDataSetChanged();
                            selectedChatItems.clear();
                            myMenuClickFlag = false;
                            count = 0;
                            onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                        } else {
                            for (int i = 0; i < selectedChatItems.size(); i++) {
                                int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                                mChatData.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());
                                mAdapter.notifyDataSetChanged();
                            }
                            Toast.makeText(getActivity(), "Check Network Connection", Toast.LENGTH_SHORT).show();
                        }
                        myMenuClickFlag = false;
                        mAdapter.notifyDataSetChanged();
                        selectedChatItems.clear();

                        count = 0;
                        onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
                        return false;

                    }
                });
                mute.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if (ConnectivityInfo.isInternetConnected(getContext())) {

                            ArrayList<MuteUserPojo> muteUserList = new ArrayList<>();
                            for (int i = 0; i < selectedChatItems.size(); i++) {
                                boolean isSecretChat = selectedChatItems.get(i).isSecretChat();
                                //skip the mute option for secret chat
                                if (isSecretChat)
                                    continue;
                                String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                                String receiverId = arrIds[1];

                                String chatType = MessageFactory.CHAT_TYPE_SINGLE;
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
                            dialog.setMuteAlertCloseListener(ChatListCopy.this);
                            dialog.show(getFragmentManager(), "Mute");
                        } else {
                            for (int i = 0; i < selectedChatItems.size(); i++) {
                                int chatIndex = mChatData.indexOf(selectedChatItems.get(i));
                                mChatData.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());
                                mAdapter.notifyDataSetChanged();
                            }
                            Toast.makeText(getActivity(), "Check Network Connection", Toast.LENGTH_SHORT).show();

                        }
                        selectedChatItems.clear();

                        myMenuClickFlag = false;
                        mAdapter.notifyDataSetChanged();
                        count = 0;
                        onCreateOptionsMenu(menuNormal, getActivity().getMenuInflater());
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
                                if (!session.getarchive(uniqueCurrentID + "-" + receiverId)) {
                                    session.putarchive(uniqueCurrentID + "-" + receiverId);
                                }
                                String docId = uniqueCurrentID.concat("-").concat(receiverId);
                                if (userInfoSession.hasChatConvId(docId)) {
                                    try {
                                        String convId = userInfoSession.getChatConvId(docId);
                                        JSONObject object = new JSONObject();
                                        object.put("convId", convId);
                                        object.put("from", uniqueCurrentID);
                                        object.put("status", 1);
                                        object.put("type", MessageFactory.CHAT_TYPE_SINGLE);
                                        SendMessageEvent event = new SendMessageEvent();
                                        event.setEventName(SocketManager.EVENT_ARCHIVE_UNARCHIVE);
                                        event.setMessageObject(object);
                                        EventBus.getDefault().post(event);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

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

//                MenuItem logout = menu.findItem(R.id.logout);
//                logout.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                    @Override
//                    public boolean onMenuItemClick(MenuItem item) {
//                        final String msg = "Do you really want to Logout?";
//                        final CustomAlertDialog dialog = new CustomAlertDialog();
//                        dialog.setNegativeButtonText("Cancel");
//                        dialog.setPositiveButtonText("Confirm");
//                        dialog.setMessage(msg);
//                        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//                            @Override
//                            public void onPositiveButtonClick() {
//                                NotificationManager notifManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
//                                notifManager.cancelAll();
//                                if (ConnectivityInfo.isInternetConnected(getActivity())) {
////                                unsetDeviceToken();
//                                    notifyLogoutToServer();
//                                } else {
//                                    Toast.makeText(getActivity(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
//                                }
//                            }
//
//                            @Override
//                            public void onNegativeButtonClick() {
//                                dialog.dismiss();
//                            }
//                        });
//                        dialog.show(getFragmentManager(), "Delete member alert");
//                        return true;
//                    }
//                });

                MenuItem searchItem = menu.findItem(R.id.chatsc_searchIcon);
                searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
                setSearchViewCollapseExpand(menu);

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
                                    //closeKeypad();
                                }
                                mAdapter.getFilter().filter(newText);
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        return false;
                    }
                });

//                searchView.setOnCloseListener(new SearchView.OnCloseListener() {
//                    @Override
//                    public boolean onClose() {
//                        menu.findItem(R.id.chatsceen_chatIcon).setVisible(true);
//                        return false;
//                    }
//                });
//
//                searchView.setOnSearchClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        menu.findItem(R.id.chatsceen_chatIcon).setVisible(false);
//                    }
//                });

                SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
                searchView.setIconifiedByDefault(true);
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchView.setIconified(true);

                AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
                searchTextView.setTextColor(Color.WHITE);

                try {
                    Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                    mCursorDrawableRes.setAccessible(true);
                    mCursorDrawableRes.set(searchTextView, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

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

    /*private void unsetDeviceToken() {
        try {
            String settingsDeviceId = Settings.Secure.getString(getActivity().getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            String loginKey = SessionManager.getInstance(getActivity()).getLoginKey();

            JSONObject object = new JSONObject();
            object.put("from", uniqueCurrentID);
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

    private void setSearchViewCollapseExpand(Menu menu) {

        MenuItem searchItem = menu.findItem(R.id.chatsc_searchIcon);

        if (searchItem != null) {

            MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    HomeScreen homeScreen = (HomeScreen) getActivity();
                    homeScreen.getSupportActionBar().show();
                    return true;
                }

                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    HomeScreen homeScreen = (HomeScreen) getActivity();
                    homeScreen.getSupportActionBar().hide();
                    return true;
                }
            });

            MenuItemCompat.setActionView(searchItem, searchView);
        }
    }


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


    private void addShortcut() {
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getActivity());

        Log.d("profileimage", profileimage);
        String[] arrIds = selectedChatItems.get(0).getMessageId().split("-");
        String receiverId = arrIds[1];
        String imageTS = contactDB_sqlite.getDpUpdatedTime(receiverId);
        if (imageTS == null || imageTS.isEmpty())
            imageTS = "1";
        String avatar = Constants.USER_PROFILE_URL + receiverId + ".jpg?id=" + imageTS;
        String receiverMsisdn = selectedChatItems.get(0).getSenderMsisdn();

        Bitmap bitmap = null;
        if (profileimage != null && myTemp != null) {
            bitmap = Bitmap.createScaledBitmap(myTemp, 128, 128, true);
        }

/*        shortcutBadgeManager.RemoveApplicationShortcutIcon(getActivity(), false, receiverId, username,
                avatar, receiverMsisdn, bitmap);*/
        shortcutBadgeManager.addChatShortcut(getActivity(), false, receiverId, username,
                avatar, receiverMsisdn, bitmap);
    }

    private void viewcontact(boolean value) {
        if (value == true) {
            Intent infoIntent = new Intent(getActivity(), GroupInfo.class);
            String[] arrIds = selectedChatItems.get(0).getMessageId().split("-");
            infoIntent.putExtra("GroupId", arrIds[1]);
            infoIntent.putExtra("GroupName", username);
            infoIntent.putExtra("UserAvatar", profileimage);
            startActivity(infoIntent);
        } else {
            Intent infoIntent = new Intent(getActivity(), UserInfo.class);
            String[] arrIds = selectedChatItems.get(0).getMessageId().split("-");
            infoIntent.putExtra("UserId", arrIds[1]);
            infoIntent.putExtra("UserName", username);
            infoIntent.putExtra("UserAvatar", profileimage);
            infoIntent.putExtra("UserNumber", phoneno);
            infoIntent.putExtra("FromSecretChat", false);
            startActivity(infoIntent);
        }

    }

    public static Date addDay(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTime();
    }

    public static Date addhour(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, i);
        return cal.getTime();
    }

    public static Date addYear(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, i);
        return cal.getTime();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        Intent i;
        switch (id) {

//            case R.id.select_contact:
//                ActivityLauncher.launchSettingContactScreen(getActivity());
//                //   startActivity(new Intent(getActivity(), ConnectActivity.class));
//                return true;


//            case R.id.chatsceen_chatIcon:
//                ActivityLauncher.launchSettingContactScreen(getActivity());
//                return true;

            case R.id.select_groupchat:

                i = new Intent(getActivity(), SelectPeopleForGroupChat.class);
                i.putExtra("type","grp");
                startActivity(i);
//                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                return true;




            case R.id.select_brodcast:

                i = new Intent(getActivity(), SelectPeopleForGroupChat.class);
                i.putExtra("type","brdcst");
                startActivity(i);
//                getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
                return true;

            case R.id.view_brodcast:

                i = new Intent(getActivity(), BroadcastListActivity.class);
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

//            case R.id.chats_status:
//                ActivityLauncher.launchStatusScreen(getActivity());
//                return true;

            case R.id.starred:
                Intent staresintent = new Intent(getActivity(), StarredItemList.class);
                startActivity(staresintent);
                return true;

            case R.id.chats_notifications:

                Intent intent_notify = new Intent(getActivity(), NotificationSettings.class);
                startActivity(intent_notify);
                return true;

            case R.id.chats_settings:
                ActivityLauncher.launchSettingScreen(getActivity());
                return true;

            case R.id.myprofile:

                Intent intent_profile = new Intent(getActivity(), UserProfile.class);
                startActivity(intent_profile);
                return true;

            case R.id.chats_help:

            ActivityLauncher.launchAbouthelp(getActivity());
                return true;
//            case R.id.select_secret_chat_contact:
//                Intent secretintent = new Intent(getActivity(), SecretChatList.class);
//                startActivity(secretintent);
//                return true;


            default:
                return super.onOptionsItemSelected(item);
        }


    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            String qrData = data.getStringExtra("QRData");

            try {
                JSONObject object = new JSONObject();
                object.put("_id", SessionManager.getInstance(getActivity()).getCurrentUserID());
                object.put("msisdn", SessionManager.getInstance(getActivity()).getPhoneNumberOfCurrentUser());
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

    @Override
    public void onMuteDialogClosed(boolean isMuted) {
        loadDbChatItem();
    }

    private class ChatListSorter implements Comparator<MessageItemChat> {

        @Override
        public int compare(MessageItemChat item1, MessageItemChat item2) {
            long item1Time = AppUtils.parseLong(item1.getTS());
            long item2Time = AppUtils.parseLong(item2.getTS());

            if (item1Time < item2Time) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        // Stop update of typing message
        if (typingRunnable != null) {
            typingHandler.removeCallbacks(typingRunnable);
        }

        isChatListPage = false;
    }

    private void loadTypingStatus(ReceviceMessageEvent event) {
        Object[] object = event.getObjectsArray();
        try {
            JSONObject jsonObject = new JSONObject(object[0].toString());
            String from = (String) jsonObject.get("from");
            String to = (String) jsonObject.get("to");
            String convId = (String) jsonObject.get("convId");
            String type = (String) jsonObject.get("type");

            if (type.equalsIgnoreCase("group")) {
                if (type.equalsIgnoreCase("group") && !from.equalsIgnoreCase(mCurrentUserId)) {
                    ChatappContactModel contact = CoreController.getContactSqliteDBintstance(getActivity()).getUserOpponenetDetails(from);
                    if (contact != null) {
                        String typingPerson = contact.getFirstName();
                    //    String typingPerson = getcontactname.getSendername(from, contact.getMsisdn());
                        setTypingMessage(convId, typingPerson);
                    } else {
                        getUserDetails(from);
                    }
                }
            } else {
                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getActivity());
                boolean blockedStatus = contactDB_sqlite.getBlockedStatus(to, false).equals("1");

                if (to != null && to.equalsIgnoreCase(uniqueCurrentID) && !blockedStatus) {
                    // Document id reversed from receiver
                    String docId = to + "-" + from;
                    if (uniqueStore.containsKey(docId)) {

                        if (userInfoSession.hasChatConvId(docId)) {
                            String savedConvId = userInfoSession.getChatConvId(docId);
                            if (savedConvId != null && savedConvId.equalsIgnoreCase(convId)) {

                                MessageItemChat msgItem = uniqueStore.get(docId);
                                int index = mChatData.indexOf(msgItem);

                                long currentTime = Calendar.getInstance().getTimeInMillis();

                                msgItem.setTypingAt(currentTime);
                                handleReceiverTypingEvent(docId, currentTime);
                                mChatData.set(index, msgItem);
                                uniqueStore.put(docId, msgItem);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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


    private void setTypingMessage(String groupId, String typingPerson) {
        String docId = mCurrentUserId + "-" + groupId + "-g";

        MessageItemChat msgItem = null;
        if (uniqueStore.containsKey(docId)) {

            msgItem = uniqueStore.get(docId);
        }
        if (uniqueStore.containsKey(SECRET_CHAT_PREFIX + docId)) {
            msgItem = uniqueStore.get(SECRET_CHAT_PREFIX + docId);
        }


        if (msgItem != null) {
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

    private void handleReceiverTypingEvent(String docId, long currentTime) {
        if (typingEventMap.size() == 0) {
            timeOutTyping = MessageFactory.TYING_MESSAGE_TIMEOUT;
        }
        typingEventMap.put(docId, currentTime);
        typingHandler.postDelayed(typingRunnable, timeOutTyping);
    }

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

                        mChatData.set(index, chat);
                        uniqueStore.put(key, chat);
                        mAdapter.notifyDataSetChanged();
                    }
                }
                /*else {
                    typingEventMap.put(key, timeDiff);
                }*/

            }
            if (typingEventMap.size() > 0) {
                timeOutTyping = currentTime - Collections.max(typingEventMap.values());
                typingHandler.postDelayed(typingRunnable, timeOutTyping);
            } else {
                typingHandler.removeCallbacks(typingRunnable);
            }
        }
    };

    private RecyclerView.OnScrollListener chatListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            notifyScrollChanged();
        }
    };

    private void notifyScrollChanged() {
        mFirstVisibleItemPosition = mChatListManager.findFirstVisibleItemPosition();
        mLastVisibleItemPosition = mChatListManager.findLastVisibleItemPosition();
        int totalItems = mChatData.size();

        try {
            if (totalItems > mFirstVisibleItemPosition && totalItems > mLastVisibleItemPosition) {
                for (int i = mFirstVisibleItemPosition; i <= mLastVisibleItemPosition; i++) {
                    String userId = mChatData.get(i).getReceiverID();
                    boolean vaolue = mChatData.get(i).isGroup();

                    if (mChatData.get(i).isGroup()) {
                    } else {
//                        MessageService.getUpdatedUserDetails(EventBus.getDefault(), userId);
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

//    private void loadUserDetails(String data) {
//        try {
//            JSONObject object = new JSONObject(data);
//            String userId = object.getString("id");
//            String docId = uniqueCurrentID + "-" + userId;
//            if (uniqueStore.containsKey(docId)) {
//                try {
//                    for (int i = mFirstVisibleItemPosition; i <= mLastVisibleItemPosition; i++) {
//                        if (mChatData.get(i).getReceiverID().equalsIgnoreCase(userId)) {
//                            mAdapter.notifyItemChanged(i);
//                        }
//                    }
//                } catch (ArrayIndexOutOfBoundsException e) {
//                    e.printStackTrace();
//                }
////            receivedUserDetailsMap.put(userId, Calendar.getInstance().getTimeInMillis());
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    private void loadUserDetails(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String userId = object.getString("id");
            if (getUsersList.contains(userId) && mAdapter != null) {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        // UI code goes here
                        mAdapter.notifyDataSetChanged();
                    }
                });

            } else {
                String docId = uniqueCurrentID + "-" + userId;
                if (uniqueStore.containsKey(docId) || uniqueStore.containsKey(SECRET_CHAT_PREFIX + docId)) {
                    try {
                        for (int i = mFirstVisibleItemPosition; i <= mLastVisibleItemPosition; i++) {
                            getActivity().runOnUiThread(new Runnable() {
                                public void run() {
                                    // UI code goes here
                                    mAdapter.notifyDataSetChanged();
                                }
                            });

                        }
                    } catch (Exception e) {
                        Log.e(TAG, "loadUserDetails: ", e);
                    }
//            receivedUserDetailsMap.put(userId, Calendar.getInstance().getTimeInMillis());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class loadUserDetails extends AsyncTask<String, Void, String> {
        String datagolbal;

        public loadUserDetails(String data) {
            datagolbal = data;
        }

        @Override
        protected String doInBackground(String... params) {
            loadUserDetails(datagolbal);
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            //UI thread
        }
    }


    //------------Delete Chat-------------------
    private void deleteSingleMessage(ReceviceMessageEvent event) {
        Object[] array = event.getObjectsArray();
        try {
            MessageDbController db = CoreController.getDBInstance(getActivity());
            JSONObject objects = new JSONObject(array[0].toString());
            int errorState = objects.getInt("err");
            if (errorState == 0) {
                String fromId = objects.getString("from");

                if (fromId != null && !fromId.equalsIgnoreCase(uniqueCurrentID)) {
                    String deleteStatus = objects.getString("status");
                    String chat_id = (String) objects.get("doc_id");
                    String[] ids = chat_id.split("-");

                    String docId;
                    String msgId;

                    docId = ids[1] + "-" + ids[0];
                    msgId = docId + "-" + ids[2];

                    if (deleteStatus != null && deleteStatus.equalsIgnoreCase("1")) {
                        if (mChatData.size() > 0) {
                            for (int i = 0; i < mChatData.size(); i++) {
                                if (mChatData.get(i).getMessageId().equalsIgnoreCase(msgId)) {
//                                    mChatData.get(i).setMessageType("Delete Others");
                                    mChatData.get(i).setMessageType(MessageFactory.DELETE_OTHER + "");
                                    mChatData.get(i).setIsSelf(false);

                                    mAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
