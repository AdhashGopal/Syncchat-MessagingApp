package com.chatapp.android.app;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.adapter.ArchiveListAdapter;
import com.chatapp.android.app.adapter.RItemAdapter;
import com.chatapp.android.app.dialog.ChatLockPwdDialog;
import com.chatapp.android.app.dialog.MuteAlertDialog;
import com.chatapp.android.app.utils.ConnectivityInfo;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.utils.GroupInfoSession;
import com.chatapp.android.app.utils.MuteUnmute;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.ShortcutBadgeManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.ChatLockPojo;
import com.chatapp.android.core.model.GroupInfoPojo;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.model.MuteStatusPojo;
import com.chatapp.android.core.model.MuteUserPojo;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * created by  Adhash Team on 1/10/2017.
 */
public class Archivelist extends CoreActivity implements MuteAlertDialog.MuteAlertCloseListener {

    private ArrayList<MessageItemChat> myArchiveListInfo;
    private String myArchiveStr = "";
    private RecyclerView myRecylerView;
    private ArchiveListAdapter myAdapter;
    ImageView backarrow_archive, archivelist_overflow;
    private SearchView searchView;
    private AvnNextLTProDemiTextView headingarchive;
    HashMap<String, MessageItemChat> uniqueStore = new HashMap<>();
    Boolean myMenuClickFlag = false;
    private UserInfoSession userInfoSession;
    int count = 0;
    Getcontactname getcontactname;
    Boolean isgroupchat = false;
    String uniqueCurrentID;
    String receiverDocumentID = new String();
    private Menu menuLongClick;
    String username, profileimage, value;
    private ArrayList<MessageItemChat> selectedChatItems = new ArrayList<>();
    private Session session;
    private SessionManager sessionManager;
    String from;
    Bitmap myTemp = null;
    String contactno = "";
    private GroupInfoSession groupInfoSession;
    ShortcutBadgeManager shortcutBadgeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        setContentView(R.layout.archivelist);
        shortcutBadgeManager = new ShortcutBadgeManager(this);
        headingarchive = (AvnNextLTProDemiTextView) findViewById(R.id.headingarchive);
        userInfoSession = new UserInfoSession(getApplication());
        backarrow_archive = (ImageView) findViewById(R.id.backarrow_archive);
        session = new Session(Archivelist.this);
        archivelist_overflow = (ImageView) findViewById(R.id.archivelist_overflow);
        uniqueCurrentID = SessionManager.getInstance(Archivelist.this).getCurrentUserID();
        backarrow_archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getcontactname = new Getcontactname(Archivelist.this);
        Bundle bundle = getIntent().getExtras();
        from = bundle.getString("from");
        classAndWidgetInitialize();

        groupInfoSession = new GroupInfoSession(Archivelist.this);
        sessionManager = SessionManager.getInstance(Archivelist.this);

        archivelist_overflow.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final AvnNextLTProRegTextView markasunread, Markasread, unmute, mute, deletearchive, Archivedrelese, Addtoshortcut, Viewcontact, Exitgroup;
                final RelativeLayout markasunreadlayout, Markasreadlayout, unmutelayout, mutelayout, deletearchivelayout, Archivedreleselayout, Addtoshortcutlayout, Viewcontactlayout, Exitgrouplayout;
                final Dialog dialogoverflow = new Dialog(Archivelist.this);
                dialogoverflow.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialogoverflow.setContentView(R.layout.customlongclickeventoverflow);
                WindowManager.LayoutParams wmlp = dialogoverflow.getWindow().getAttributes();
                wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
                wmlp.x = -5;   //x position
                wmlp.y = -5;   //y position
                /*markasunread = (AvnNextLTProRegTextView) dialogoverflow.findViewById(R.id.markasunread);
                Markasread = (AvnNextLTProRegTextView) dialogoverflow.findViewById(R.id.Markasread);
                unmute = (AvnNextLTProRegTextView) dialogoverflow.findViewById(R.id.unmute);
                mute = (AvnNextLTProRegTextView) dialogoverflow.findViewById(R.id.mute);
                Exitgroup = (AvnNextLTProRegTextView) dialogoverflow.findViewById(R.id.Exitgroup);
                deletearchive = (AvnNextLTProRegTextView) dialogoverflow.findViewById(R.id.deletearchive);
                Archivedrelese = (AvnNextLTProRegTextView) dialogoverflow.findViewById(R.id.Archivedrelese);
                Addtoshortcut = (AvnNextLTProRegTextView) dialogoverflow.findViewById(R.id.Addtoshortcut);
                Viewcontact = (AvnNextLTProRegTextView) dialogoverflow.findViewById(R.id.Viewcontact);*/
                Viewcontact = (AvnNextLTProRegTextView) dialogoverflow.findViewById(R.id.Viewcontact);
                markasunreadlayout = (RelativeLayout) dialogoverflow.findViewById(R.id.R1_overflowarchivelist);
                Markasreadlayout = (RelativeLayout) dialogoverflow.findViewById(R.id.R2_overflowarchivelist);
                unmutelayout = (RelativeLayout) dialogoverflow.findViewById(R.id.R3_overflowarchivelist);
                mutelayout = (RelativeLayout) dialogoverflow.findViewById(R.id.R4_overflowarchivelist);
                deletearchivelayout = (RelativeLayout) dialogoverflow.findViewById(R.id.R5_overflowarchivelist);
                Archivedreleselayout = (RelativeLayout) dialogoverflow.findViewById(R.id.R6_overflowarchivelist);
                Addtoshortcutlayout = (RelativeLayout) dialogoverflow.findViewById(R.id.R7_overflowarchivelist);
                Viewcontactlayout = (RelativeLayout) dialogoverflow.findViewById(R.id.R8_overflowarchivelist);
                Exitgrouplayout = (RelativeLayout) dialogoverflow.findViewById(R.id.R9_overflowarchivelist);

                if (selectedChatItems.size() > 0) {
                    boolean group = false;
                    boolean single = false;
                    for (int i = 0; i <= selectedChatItems.size() - 1; i++) {
                        if (selectedChatItems.get(i).isGroup()) {
                            Viewcontact.setText("Group info");

                            for (int m = 0; m <= selectedChatItems.size() - 1; m++) {
                                if (selectedChatItems.get(m).isGroup()) {
                                    group = true;
                                } else {
                                    single = true;
                                }
                            }

                            if (group == true && single == false) {
                                Exitgrouplayout.setVisibility(View.VISIBLE);
                            }else{
                                Exitgrouplayout.setVisibility(View.GONE);
                            }
                        } else {
                            Viewcontact.setText("View contact");
                            Exitgrouplayout.setVisibility(View.GONE);
                        }
                    }
                }

//                if(selectedChatItems.size()>0){
//                            for (int i=0;i<=selectedChatItems.size()-1;i++){
//                                if(selectedChatItems.get(i).isGroup()){
//                                    group = true;
//                                }else{
//                                    single = true;
//                                }
//                            }
//                        }
//                       if(group == true && single == false){
//
//                       }else{
//                            Toast.makeText(v.getContext(),"",Toast.LENGTH_SHORT).show();
//                       }
//                if (isgroupchat) {
//
//                } else {
//
//                }
                dialogoverflow.show();
                Addtoshortcutlayout.setVisibility(View.GONE);
                Viewcontactlayout.setVisibility(View.GONE);
                Archivedreleselayout.setVisibility(View.VISIBLE);

                ArrayList<Boolean> exitanddelete = new ArrayList<>();
                for (int i = 0; i < selectedChatItems.size(); i++) {
                    String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                    String receiverId = arrIds[1];
                    boolean hasGroupInfo = groupInfoSession.hasGroupInfo(uniqueCurrentID + "-" + receiverId + "-g");
                    GroupInfoPojo infoPojo;
                    if (hasGroupInfo) {
                        infoPojo = groupInfoSession.getGroupInfo(uniqueCurrentID + "-" + receiverId + "-g");
                        if (infoPojo.isLiveGroup()) {
                            exitanddelete.add(infoPojo.isLiveGroup());
                        } else {
                            exitanddelete.add(infoPojo.isLiveGroup());
                        }
                    }
                }
                if (selectedChatItems.size() > 0) {
                    boolean group = false;
                    boolean single = false;
                    for (int i = 0; i <= selectedChatItems.size() - 1; i++) {
                        if (selectedChatItems.get(i).isGroup()) {
                            if (exitanddelete.contains(true) && exitanddelete.contains(false)) {
                                Exitgrouplayout.setVisibility(View.GONE);
                                deletearchivelayout.setVisibility(View.GONE);
                            } else if (exitanddelete.contains(true)) {
                                for (int m = 0; m <= selectedChatItems.size() - 1; m++) {
                                    if (selectedChatItems.get(m).isGroup()) {
                                        group = true;
                                    } else {
                                        single = true;
                                    }
                                }
                                if (group == true && single == false) {
                                    Exitgrouplayout.setVisibility(View.VISIBLE);
                                }else{
                                    Exitgrouplayout.setVisibility(View.GONE);
                                }
                                deletearchivelayout.setVisibility(View.GONE);
                            } else {
                                Exitgrouplayout.setVisibility(View.GONE);
                                if (group == true && single == false) {
                                    deletearchivelayout.setVisibility(View.VISIBLE);
                                }else{
                                    deletearchivelayout.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            if (group == true && single == false) {
                                deletearchivelayout.setVisibility(View.VISIBLE);
                            }else{
                                deletearchivelayout.setVisibility(View.GONE);
                            }
                        }
                    }
                }
//                if (from.contains("chatlist")) {
//                    deletearchivelayout.setVisibility(View.VISIBLE);
//                } else if (from.contains("grouplist")) {
//                    if (exitanddelete.contains(true) && exitanddelete.contains(false)) {
//                        Exitgrouplayout.setVisibility(View.GONE);
//                        deletearchivelayout.setVisibility(View.GONE);
//                    } else if (exitanddelete.contains(true)) {
//                        Exitgrouplayout.setVisibility(View.VISIBLE);
//                        deletearchivelayout.setVisibility(View.GONE);
//                    } else {
//                        Exitgrouplayout.setVisibility(View.GONE);
//                        deletearchivelayout.setVisibility(View.VISIBLE);
//                    }
//                }

                if (count >0) {
                    Addtoshortcutlayout.setVisibility(View.GONE);
                    Viewcontactlayout.setVisibility(View.GONE);
                    archivelist_overflow.setVisibility(View.VISIBLE);
                    Archivedreleselayout.setVisibility(View.VISIBLE);

                }
                ArrayList<Boolean> mutearray = new ArrayList<>();
                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(Archivelist.this);

                for (int i = 0; i < selectedChatItems.size(); i++) {
                    String msgId = selectedChatItems.get(i).getMessageId();
                    String[] arrIds = msgId.split("-");
                    String receiverId = arrIds[1];
                    String convId = null;

                    if (msgId.contains("-g")) {
                        convId = receiverId;
                        receiverId = null;
                    } else {
                        String docId = uniqueCurrentID + "-" + receiverId;
                        convId = userInfoSession.getChatConvId(docId);
                    }

                    MuteStatusPojo muteData = contactDB_sqlite.getMuteStatus(uniqueCurrentID, receiverId, convId, false);
//                    MuteStatusPojo muteData = contactsDB.getMuteStatus(uniqueCurrentID, receiverId, convId, false);

                    mutearray.add((muteData != null && muteData.getMuteStatus().equals("1")));
                }
                if (mutearray.contains(true) && mutearray.contains(false)) {
                    mutelayout.setVisibility(View.GONE);
                    unmutelayout.setVisibility(View.GONE);
                } else if (mutearray.contains(true)) {
                    mutelayout.setVisibility(View.GONE);
                    unmutelayout.setVisibility(View.GONE);
                } else {
                    mutelayout.setVisibility(View.GONE);
                    unmutelayout.setVisibility(View.GONE);
                }
                ArrayList<Boolean> mark = new ArrayList<>();
                for (int i = 0; i < selectedChatItems.size(); i++) {
                    String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                    String receiverId = arrIds[1];
                    if (session.getmark(receiverId)) {
                        mark.add(session.getmark(receiverId));

                    } else {
                        mark.add(false);

                    }
                }
                if (mark.contains(true) && mark.contains(false)) {
                    markasunreadlayout.setVisibility(View.GONE);
                    Markasreadlayout.setVisibility(View.GONE);
                } else if (mark.contains(true)) {
                    markasunreadlayout.setVisibility(View.GONE);
                    Markasreadlayout.setVisibility(View.GONE);
                } else {
                    markasunreadlayout.setVisibility(View.GONE);
                    Markasreadlayout.setVisibility(View.GONE);
                }
            /*if (markflag) {
                markasunread_menu.setVisible(true);
                markread_menu.setVisible(false);
            } else {
                markasunread_menu.setVisible(false);
                markread_menu.setVisible(true);
            }*/

                /*if (username == null) {
                    addcontact_menu.setVisible(true);
                }*/
                Addtoshortcutlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < selectedChatItems.size(); i++) {
                            addShortcut(selectedChatItems.get(i).isGroup());
                            int chatIndex = myArchiveListInfo.indexOf(selectedChatItems.get(i));
                            myArchiveListInfo.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected()); // setchangemenu();
                        }
                        myMenuClickFlag = false;
                        count = 0;
                        selectedChatItems.clear();
                        myAdapter.notifyDataSetChanged();
                        archivelist_overflow.setVisibility(View.GONE);
                        dialogoverflow.dismiss();

                    }
                });
                Exitgrouplayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialogoverflow.dismiss();
                        showExitGroupAlert();
                        myAdapter.notifyDataSetChanged();
                    }
                });
                Viewcontactlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedChatItems.size() > 0) {
                            for (int i = 0; i <= selectedChatItems.size() - 1; i++) {
                                if (selectedChatItems.get(i).isGroup()) {
                                    Groupinfo();
                                } else {
                                    viewcontact();
                                }
                            }
                        }
//                        if (isgroupchat) {
//                            Groupinfo();
//                        } else {
//                            viewcontact();
//                        }
                        myMenuClickFlag = false;
                        myAdapter.notifyDataSetChanged();
                        count = 0;
                        selectedChatItems.clear();
                        dialogoverflow.dismiss();
                    }
                });


                markasunreadlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < selectedChatItems.size(); i++) {
                            String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                            String receiverId = arrIds[1];
                            session.putmark(receiverId);
                            System.out.println("docid" + receiverId);

                            int chatIndex = myArchiveListInfo.indexOf(selectedChatItems.get(i));
                            myArchiveListInfo.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());

                            // setchangemenu();
                        }
                        myMenuClickFlag = false;
                        myAdapter.notifyDataSetChanged();
                        selectedChatItems.clear();
                        count = 0;
                        archivelist_overflow.setVisibility(View.GONE);
                        dialogoverflow.dismiss();
                    }
                });


                Markasreadlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < selectedChatItems.size(); i++) {
                            String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                            String receiverId = arrIds[1];
                            session.Removemark(receiverId);
                            System.out.println("docid" + receiverId);
                            int chatIndex = myArchiveListInfo.indexOf(selectedChatItems.get(i));
                            myArchiveListInfo.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());

                            // setchangemenu();
                        }
                        myMenuClickFlag = false;
                        myAdapter.notifyDataSetChanged();
                        selectedChatItems.clear();
                        count = 0;
                        archivelist_overflow.setVisibility(View.GONE);
                        dialogoverflow.dismiss();
                    }
                });
                deletearchivelayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedChatItems.size() > 0) {
                            for (int j = 0; j <= selectedChatItems.size() - 1; j++) {
                                if (selectedChatItems.get(j).isGroup()) {
                                    for (int i = 0; i < selectedChatItems.size(); i++) {
                                        String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                                        String receiverId = arrIds[1];
                                        String docId = uniqueCurrentID.concat("-").concat(receiverId).concat("-g");
                                        MessageDbController db = CoreController.getDBInstance(Archivelist.this);
                                        db.deleteChat(docId, MessageFactory.CHAT_TYPE_GROUP);
                                        if (session.getarchivegroup(uniqueCurrentID + "-" + receiverId + "-g")) {
                                            session.removearchivegroup(uniqueCurrentID + "-" + receiverId + "-g");
                                        }
                                        if (session.getarchivecount() == 0) {
                                            finish();
                                        }
                                    }
                                    selectedChatItems.clear();
                                    myMenuClickFlag = false;
                                    count = 0;
                                    dialogoverflow.dismiss();
                                    myAdapter.notifyDataSetChanged();
                                    loadData();
                                } else {
                                    for (int i = 0; i < selectedChatItems.size(); i++) {
                                        String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                                        String receiverId = arrIds[1];
                                        String docId = uniqueCurrentID.concat("-").concat(receiverId);
                                        MessageDbController db = CoreController.getDBInstance(Archivelist.this);
                                        db.deleteChat(docId, MessageFactory.CHAT_TYPE_SINGLE);
                                        if (session.getarchive(uniqueCurrentID + "-" + receiverId)) {
                                            session.removearchive(uniqueCurrentID + "-" + receiverId);
                                        }
                                        if (session.getarchivecount() == 0) {
                                            finish();
                                        }
                                    }
                                    selectedChatItems.clear();
                                    myMenuClickFlag = false;
                                    myAdapter.notifyDataSetChanged();
                                    count = 0;
                                    dialogoverflow.dismiss();
                                    loadData();
                                }
                            }
                        }
//                        if (from.contains("chatlist")) {
//
//                        } else if (from.contains("grouplist")) {
//
//                        }
                    }

                });
                mutelayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (ConnectivityInfo.isInternetConnected(Archivelist.this)) {
                            ArrayList<MuteUserPojo> muteUserList = new ArrayList<>();
                            for (int i = 0; i < selectedChatItems.size(); i++) {
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

                                int index = myArchiveListInfo.indexOf(selectedChatItems.get(i));
                                if (index > -1) {
                                    myArchiveListInfo.get(index).setSelected(false);
                                }
                            }

                            Bundle putBundle = new Bundle();
                            putBundle.putSerializable("MuteUserList", muteUserList);

                            MuteAlertDialog dialog = new MuteAlertDialog();
                            dialog.setArguments(putBundle);
                            dialog.setCancelable(false);
                            dialog.setMuteAlertCloseListener(Archivelist.this);
                            dialog.show(getSupportFragmentManager(), "Mute");

                            selectedChatItems.clear();
                            myMenuClickFlag = false;
                            dialogoverflow.dismiss();

                            count = 0;
                        } else {
                            Toast.makeText(Archivelist.this, "Check Network Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                unmutelayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i = 0; i < selectedChatItems.size(); i++) {
                            String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                            String receiverId = arrIds[1];

                            String chatType = MessageFactory.CHAT_TYPE_SINGLE;
                            if (selectedChatItems.get(i).getMessageId().contains("-g")) {
                                chatType = MessageFactory.CHAT_TYPE_GROUP;
                            }

                            MuteUnmute.performUnMute(Archivelist.this, EventBus.getDefault(), receiverId,
                                    chatType, "no");

                            int chatIndex = myArchiveListInfo.indexOf(selectedChatItems.get(i));
                            myArchiveListInfo.get(chatIndex).setSelected(!selectedChatItems.get(i).isSelected());
                        }
                        myMenuClickFlag = false;
                        myAdapter.notifyDataSetChanged();
                        selectedChatItems.clear();

                        count = 0;
                        archivelist_overflow.setVisibility(View.GONE);
                        dialogoverflow.dismiss();
                    }
                });

                Archivedreleselayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String receiverId = "";
                        String docId = "";
                        for (int i = 0; i < selectedChatItems.size(); i++) {
                            String[] arrIds = selectedChatItems.get(i).getMessageId().split("-");
                            receiverId = arrIds[1];

                            if (selectedChatItems.size() > 0) {
                                for (int k = 0; k <= selectedChatItems.size() - 1; k++) {
                                    if (selectedChatItems.get(k).isGroup()) {
                                        docId = uniqueCurrentID.concat("-").concat(receiverId) + "-g";
                                        if (session.getarchivegroup(uniqueCurrentID + "-" + receiverId + "-g")) {
                                            session.removearchivegroup(uniqueCurrentID + "-" + receiverId + "-g");
                                            try {

                                                String convId = receiverId;
                                                JSONObject object = new JSONObject();
                                                object.put("convId", convId);
                                                object.put("from", uniqueCurrentID);
                                                object.put("status", 0);
                                                object.put("type", MessageFactory.CHAT_TYPE_SINGLE);
                                                SendMessageEvent event = new SendMessageEvent();
                                                event.setEventName(SocketManager.EVENT_ARCHIVE_UNARCHIVE);
                                                event.setMessageObject(object);
                                                EventBus.getDefault().post(event);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } else {
                                        docId = uniqueCurrentID.concat("-").concat(receiverId);
                                        if (session.getarchive(uniqueCurrentID + "-" + receiverId)) {
                                            session.removearchive(uniqueCurrentID + "-" + receiverId);
                                        }

                                        if (userInfoSession.hasChatConvId(docId)) {
                                            try {
                                                String convId = userInfoSession.getChatConvId(docId);
                                                JSONObject object = new JSONObject();
                                                object.put("convId", convId);
                                                object.put("from", uniqueCurrentID);
                                                object.put("status", 0);
                                                object.put("type", MessageFactory.CHAT_TYPE_SINGLE);
                                                SendMessageEvent event = new SendMessageEvent();
                                                event.setEventName(SocketManager.EVENT_ARCHIVE_UNARCHIVE);
                                                event.setMessageObject(object);
                                                EventBus.getDefault().post(event);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }

//                            if (from.contains("chatlist")) {
//
//                            } else if (from.contains("grouplist")) {
//
//                            }


                            int chatIndex = myArchiveListInfo.indexOf(selectedChatItems.get(i));
                            myArchiveListInfo.remove(chatIndex);
                        }

                        selectedChatItems.clear();
                        myMenuClickFlag = false;
                        myAdapter.notifyDataSetChanged();
                        count = 0;
                        loadData();
                        dialogoverflow.dismiss();
                        archivelist_overflow.setVisibility(View.GONE);
                        if (myArchiveListInfo.size() == 0) {
                            finish();
                        }
                    }
                });

                return false;
            }
        });
    }

    private void classAndWidgetInitialize() {
        myRecylerView = (RecyclerView) findViewById(R.id.archive_list_RCYLR);
        myArchiveListInfo = new ArrayList<>();
        setTitle("Archived Chats");

        myRecylerView.addOnItemTouchListener(new RItemAdapter(Archivelist.this, myRecylerView, new RItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position >= 0) {
                    if (myMenuClickFlag) {

                        myArchiveListInfo.get(position).setSelected(!myArchiveListInfo.get(position).isSelected());

                        if (myArchiveListInfo.get(position).isSelected()) {
                            selectedChatItems.add(myArchiveListInfo.get(position));
                            count = count + 1;
                        } else {
                            selectedChatItems.remove(myArchiveListInfo.get(position));
                            count = count - 1;
                        }
                        myAdapter.notifyDataSetChanged();
                        MessageItemChat e = myArchiveListInfo.get(position);
                        if (count == 0) {
                            myMenuClickFlag = false;
                            archivelist_overflow.setVisibility(View.GONE);
                        }
                    } else {
                        ChatLockPojo lockPojo = getChatLockdetailfromDB(position);

                        if (sessionManager.getLockChatEnabled().equals("1") && lockPojo != null) {
                            String status = lockPojo.getStatus();
                            String pwd = lockPojo.getPassword();

                            MessageItemChat e = myAdapter.getItem(position);
                            String docID = e.getMessageId();
                            String[] ids = docID.split("-");
                            String documentID = "";
                            if (e.isGroup()) {
                                documentID = ids[0] + "-" + ids[1] + "-g";
                            } else {
                                documentID = ids[0] + "-" + ids[1];
                            }
//                            if (isgroupchat) {
//
//                            } else {
//
//                            }
                            if (status.equals("1")) {
                                openUnlockChatDialog(documentID, status, pwd, position);
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
            public void onItemLongClick(View view, int position) {
                if (position >= 0) {
                    myArchiveListInfo.get(position).setSelected(!myArchiveListInfo.get(position).isSelected());
                    ImageView ivProfielPic = (ImageView) view.findViewById(R.id.storeImage);
                    if (ivProfielPic.getDrawable() != null) {
                        try {
                            myTemp = ((BitmapDrawable) ivProfielPic.getDrawable()).getBitmap();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (myArchiveListInfo.get(position).isSelected()) {
                        selectedChatItems.add(myArchiveListInfo.get(position));
                        count = count + 1;
                    } else {
                        selectedChatItems.remove(myArchiveListInfo.get(position));
                        count = count - 1;
                    }
                    myAdapter.notifyDataSetChanged();

                    myMenuClickFlag = true;
                    MessageItemChat e = myArchiveListInfo.get(position);
                    e.setSelected(true);
                    myArchiveListInfo.set(position, e);
                    myAdapter.notifyDataSetChanged();
                    username = e.getSenderName();
                    profileimage = e.getAvatarImageUrl();
                    archivelist_overflow.setVisibility(View.VISIBLE);

                    if (count == 0) {
                        myMenuClickFlag = false;
                        archivelist_overflow.setVisibility(View.GONE);

//                            getActivity().getMenuInflater().inflate(R.menu.chats_screen, menuNormal);
                    }
//                count = count + 1;

                 /*else {
                    tick.setVisibility(View.GONE);
                    myMenuClickFlag = false;

                }*/
                }
            }
        }));
    }

    private void loadData() {

        uniqueStore.clear();
        myArchiveListInfo.clear();
        MessageDbController db = CoreController.getDBInstance(this);

        Session session = new Session(Archivelist.this);

        ArrayList<MessageItemChat> messageItemList = db.selectChatList(MessageFactory.CHAT_TYPE_SINGLE);
        ArrayList<MessageItemChat> messageItemList_group = db.selectChatList(MessageFactory.CHAT_TYPE_GROUP);
        for (MessageItemChat msgItem : messageItemList) {
            String docID = uniqueCurrentID + "-" + msgItem.getReceiverID();
            if (session.getarchive(docID)) {
                msgItem.setGroup(false);
                uniqueStore.put(docID, msgItem);
            }
        }
        for (MessageItemChat msgItem : messageItemList_group) {
            String docID = uniqueCurrentID + "-" + msgItem.getReceiverID() + "-g";
            if (session.getarchivegroup(docID)) {
                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docID);
                if (infoPojo != null) {
                    String avatarPath = infoPojo.getAvatarPath();
                    msgItem.setAvatarImageUrl(avatarPath);
                    msgItem.setSenderName(infoPojo.getGroupName());
                    msgItem.setGroup(true);
                }
                uniqueStore.put(docID, msgItem);
            }
        }


//        if (from.contains("grouplist")) {
//            isgroupchat = true;
//
//            ArrayList<MessageItemChat> messageItemList = db.selectChatList(MessageFactory.CHAT_TYPE_GROUP);
//            for (MessageItemChat msgItem : messageItemList) {
//                String docID = uniqueCurrentID + "-" + msgItem.getReceiverID() + "-g";
//                if (session.getarchivegroup(docID)) {
//                    GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docID);
//                    if (infoPojo != null) {
//                        String avatarPath = infoPojo.getAvatarPath();
//                        msgItem.setAvatarImageUrl(avatarPath);
//                        msgItem.setSenderName(infoPojo.getBroadcastName());
//                    }
//                    uniqueStore.put(docID, msgItem);
//                }
//            }
//
//        }

        updateChatList();

        myAdapter = new ArchiveListAdapter(getApplicationContext(), myArchiveListInfo);
        myRecylerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        myRecylerView.setItemAnimator(new DefaultItemAnimator());
        myRecylerView.setAdapter(myAdapter);
    }


    private void openUnlockChatDialog(String docId, String status, String pwd, int position) {
        String convId = userInfoSession.getChatConvId(docId);
        ChatLockPwdDialog dialog = new ChatLockPwdDialog();
        dialog.setTextLabel1("Enter your Password");
        dialog.setEditTextdata("Enter new Password");
        dialog.setforgotpwdlabel("Forgot Password");
        dialog.setHeader("Unlock Chat");
        dialog.setButtonText("Unlock");
        Bundle bundle = new Bundle();
        bundle.putSerializable("MessageItem", myAdapter.getItem(position));
        if (docId.contains("-g")) {
            bundle.putString("convID", docId.split("-")[1]);
        } else {
            bundle.putString("convID", convId);
        }
        bundle.putString("status", "1");
        bundle.putString("pwd", pwd);
        bundle.putString("page", "chatlist");
        if (docId.contains("-g")) {
            bundle.putString("type", "group");
        } else {
            bundle.putString("type", "single");
        }
        bundle.putString("from", uniqueCurrentID);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "chatunLock");
    }


    private void navigateTochatviewpage(int position) {
        MessageItemChat e = myAdapter.getItem(position);
        if (e != null) {
            if (e.getStatus() != null) {
                getcontactname.navigateToChatViewpagewithmessageitems(e, "archive");
                finish();

            } else {
                Toast.makeText(Archivelist.this, getResources().getString(R.string.status_not_available), Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void updateChatList() {
        for (MessageItemChat value : uniqueStore.values()) {
            if (!value.isGroup()) {
                String phContactName = getcontactname.getSendername(value.getReceiverID(), value.getSenderMsisdn());
                value.setSenderName(phContactName);
                myArchiveListInfo.add(value);
            } else {
                value.setSenderName(value.getSenderName());
                myArchiveListInfo.add(value);
            }
        }

        // Collections.sort(myArchiveListInfo, new ChatListSorter());
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
    }

    private void addShortcut(boolean group) {

        String[] arrIds = selectedChatItems.get(0).getMessageId().split("-");
        String receiverId = arrIds[1];

        Bitmap bitmap = null;
        if (profileimage != null && myTemp != null) {
            try {
                bitmap = Bitmap.createScaledBitmap(myTemp, 128, 128, true);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
/*        shortcutBadgeManager.RemoveApplicationShortcutIcon(Archivelist.this, group, receiverId, username,
                profileimage, contactno, bitmap);*/
        shortcutBadgeManager.addChatShortcut(Archivelist.this, group, receiverId, username,
                profileimage, contactno, bitmap);
    }

    private void viewcontact() {
        Intent infoIntent = new Intent(Archivelist.this, UserInfo.class);
        infoIntent.putExtra("UserId", receiverDocumentID);
        infoIntent.putExtra("UserName", username);
        infoIntent.putExtra("UserAvatar", profileimage);
        infoIntent.putExtra("UserNumber", contactno);
        infoIntent.putExtra("FromSecretChat", false);
        startActivity(infoIntent);
    }

    private void Groupinfo() {
        Intent infoIntent = new Intent(Archivelist.this, GroupInfo.class);
        String[] arrIds = selectedChatItems.get(0).getMessageId().split("-");
        infoIntent.putExtra("GroupId", arrIds[1]);
        infoIntent.putExtra("GroupName", username);
        infoIntent.putExtra("UserAvatar", profileimage);
        startActivity(infoIntent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_ARCHIVE_UNARCHIVE)) {
            Object[] obj = event.getObjectsArray();
            //loadarchivelist(obj[0].toString());

            //mycode
            loadarchive(obj[0].toString());
        }
    }

    private void loadarchive(String data) {
        try {
            JSONObject object = new JSONObject(data);

            try {
                String from = object.getString("from");
                String convId = object.getString("convId");

                String type = object.getString("type");
                String status = object.getString("status");

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

                        if (uniqueStore.containsKey(docId)) {
                            uniqueStore.remove(docId);
                            updateChatList();
                            myAdapter.notifyDataSetChanged();
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

    @Override
    public void onStart() {
        super.onStart();
        loadData();
        if (myAdapter != null) {
            myAdapter.notifyDataSetChanged();
        }
      /*  if (myArchiveListInfo.size() == 0) {
            finish();
        }*/
        if (from.equalsIgnoreCase("chatlist")) {
            if (session.getarchivecount() == 0 && session.getarchivecountgroup() == 0) {
                finish();
            }
        } else {
//            if () {
//                finish();
//            }
        }
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private void showExitGroupAlert() {

        AlertDialog.Builder alert = new AlertDialog.Builder(Archivelist.this);
        alert.setMessage("Exit " + username + " group");
        alert.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                for (int j = 0; j < selectedChatItems.size(); j++) {
                    String[] arrIds = selectedChatItems.get(j).getMessageId().split("-");
                    String receiverId = arrIds[1];
                    int chatIndex = myArchiveListInfo.indexOf(selectedChatItems.get(j));
                    myArchiveListInfo.get(chatIndex).setSelected(!selectedChatItems.get(j).isSelected());
                    performGroupExit(receiverId);
                }
                myMenuClickFlag = false;
                myAdapter.notifyDataSetChanged();
                selectedChatItems.clear();
                count = 0;
            }
        });
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                myMenuClickFlag = false;
                myAdapter.notifyDataSetChanged();
                selectedChatItems.clear();
                count = 0;

            }
        });
        alert.show();
    }

    private void performGroupExit(String groupid) {
        long ts = Calendar.getInstance().getTimeInMillis();
        String msgId = uniqueCurrentID + "-" + groupid + "-g-" + ts;

        try {
            JSONObject exitObject = new JSONObject();
            exitObject.put("groupType", SocketManager.ACTION_EXIT_GROUP);
            exitObject.put("from", uniqueCurrentID);
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


    private ChatLockPojo getChatLockdetailfromDB(int position) {
        MessageItemChat e = myAdapter.getItem(position);
        String docID = e.getMessageId();
        String[] id = docID.split("-");
        String documentID = "";
        String chatType = MessageFactory.CHAT_TYPE_SINGLE;
        if (e.isGroup()) {
            documentID = id[0] + "-" + id[1] + "-g";
            chatType = MessageFactory.CHAT_TYPE_GROUP;
        } else {
            documentID = id[0] + "-" + id[1];
        }
        MessageDbController dbController = CoreController.getDBInstance(this);
        String convId = userInfoSession.getChatConvId(documentID);
        String receiverId = userInfoSession.getReceiverIdByConvId(convId);
        ChatLockPojo pojo = dbController.getChatLockData(receiverId, chatType);
        return pojo;
    }

    @Override
    public void onMuteDialogClosed(boolean isMuted) {
        myAdapter.notifyDataSetChanged();
        archivelist_overflow.setVisibility(View.GONE);
    }
}













