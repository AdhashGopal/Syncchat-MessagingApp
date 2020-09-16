package com.chatapp.synchat.app;

/**
 * created by  Adhash Team on 11/23/2016.
 */

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatHistory extends CoreActivity implements View.OnClickListener {
    private AvnNextLTProDemiTextView Text_chathistory;
    AvnNextLTProDemiTextView r1_txt1;
    private RelativeLayout unarchive, clearchat, Archive, Emailchat;
    private String uniqueCurrentID, docId, to;
    private ArrayList<MessageItemChat> mChatData = new ArrayList<>();
    private HashMap<String, String> savedContactsMap;
    ImageView chatback;
    private UserInfoSession userInfoSession;
    private MessageDbController db;
    Session session;
    Boolean deletestarred = false;
    HashMap<String, MessageItemChat> uniqueStore = new HashMap<>();
    final Context context = this;
    Boolean isGroupChat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_history);
        Emailchat = (RelativeLayout) findViewById(R.id.R1_chathistory);
        Archive = (RelativeLayout) findViewById(R.id.R2_chathistory);
        clearchat = (RelativeLayout) findViewById(R.id.R3_chathistory);
        unarchive = (RelativeLayout) findViewById(R.id.R4_chathistory);
        chatback = (ImageView) findViewById(R.id.chathistory_back);
        r1_txt1 = (AvnNextLTProDemiTextView) findViewById(R.id.r1_txt1);
        getSupportActionBar().hide();
        Archive.setOnClickListener(ChatHistory.this);
        unarchive.setOnClickListener(ChatHistory.this);
        clearchat.setOnClickListener(ChatHistory.this);
        Emailchat.setOnClickListener(ChatHistory.this);
        Text_chathistory = (AvnNextLTProDemiTextView) findViewById(R.id.Text_chathistory);
        userInfoSession = new UserInfoSession(ChatHistory.this);
        session = new Session(ChatHistory.this);
        db = CoreController.getDBInstance(this);
        uniqueCurrentID = SessionManager.getInstance(getApplication()).getCurrentUserID();
        chatback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityLauncher.launchChatSetting(ChatHistory.this);
                finish();
            }
        });

        ArrayList<MessageItemChat> databases = db.selectChatList(MessageFactory.CHAT_TYPE_SINGLE);
        ArrayList<MessageItemChat> databasesgroup = db.selectChatList(MessageFactory.CHAT_TYPE_GROUP);
        if (session.getarchivecount() == databases.size() && session.getarchivecountgroup() == databasesgroup.size()) {
            unarchive.setVisibility(View.VISIBLE);
            Archive.setVisibility(View.GONE);
        } else {
            unarchive.setVisibility(View.GONE);
            Archive.setVisibility(View.VISIBLE);
        }


       /* Archive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to archive all chats?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

            }
        });
        clearchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = new CheckBox(v.getContext());
                checkBox.setText("Are you sure you want to clear messages in all chats?");
                LinearLayout linearLayout = new LinearLayout(v.getContext());
                linearLayout.setLayoutParams( new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.FILL_PARENT));
                //linearLayout.setOrientation(1);
                linearLayout.addView(checkBox);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
                alertDialogBuilder.setView(linearLayout);
                //alertDialogBuilder.setTitle("This is the title of alert dialog");
                alertDialogBuilder.setMessage("Keep Starred messages");
                alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // do something
                    }
                });
                alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        // do something
                    }
                });
                alertDialogBuilder.show();

            }
        });
        deletechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(context)
                        .setMessage("Are you sure you want to Delete all chats?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .show();

            }
        });*/
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.R1_chathistory:
                Intent intent = new Intent(this, Emailchat_Activitypage.class);
                startActivity(intent);
                break;

            case R.id.R2_chathistory:
                final CustomAlertDialog dialog = new CustomAlertDialog();
                dialog.setMessage("Are you sure you want to archive all chats?");
                dialog.setPositiveButtonText("Ok");
                dialog.setNegativeButtonText("Cancel");
                dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        archivelist();
                        archivelistgroup();
                        //deletechat now ->unarchive
                        Toast.makeText(ChatHistory.this, "All Chats are Archived", Toast.LENGTH_SHORT).show();
                        Archive.setVisibility(View.GONE);
                        unarchive.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }
                });
                dialog.show(getSupportFragmentManager(), "CustomAlert");
                break;

            case R.id.R3_chathistory:

                CustomAlertDialog dialog2 = new CustomAlertDialog();
                dialog2.setMessage("Are you sure you want to clear messages in all chats?");
                dialog2.setCheckBoxtext("Keep starNextLine Message");
                dialog2.setPositiveButtonText("Clear");
                dialog2.setNegativeButtonText("Cancel");

                dialog2.setCheckBoxCheckedChangeListener(new CustomAlertDialog.OnDialogCheckBoxCheckedChangeListener() {
                    @Override
                    public void onCheckedChange(boolean isChecked) {
                        deletestarred = true;
                    }
                });

                dialog2.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                    @Override
                    public void onPositiveButtonClick() {

                        int star_status = 0;
                        if (deletestarred) {
                            star_status = 1;
                            // db.clearUnStarredMessage(docId);
                        }

                        ArrayList<MessageItemChat> databases = db.selectChatList(MessageFactory.CHAT_TYPE_SINGLE);

                        for (MessageItemChat msgItem : databases) {
                            //db.deleteChat(docID);
                            // session.removearchive(docID);
                            // session.removearchivegroup(docID);
                            String docID = uniqueCurrentID + "-" + msgItem.getReceiverID();

                            if (userInfoSession.hasChatConvId(docID)) {
                                try {
                                    String convId = userInfoSession.getChatConvId(docID);
                                    JSONObject object = new JSONObject();
                                    object.put("convId", convId);
                                    object.put("from", uniqueCurrentID);
                                    object.put("star_status", star_status);
                                    object.put("type", MessageFactory.CHAT_TYPE_SINGLE);
                                    SendMessageEvent event = new SendMessageEvent();
                                    event.setEventName(SocketManager.EVENT_CLEAR_CHAT);
                                    event.setMessageObject(object);
                                    EventBus.getDefault().post(event);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }


                        }

                        ArrayList<MessageItemChat> databases_group = db.selectChatList(MessageFactory.CHAT_TYPE_GROUP);
                        for (MessageItemChat msgItem : databases_group) {
                            try {
                                JSONObject object = new JSONObject();
                                String[] array = msgItem.getMessageId().split("-");
                                object.put("convId", array[1]);
                                object.put("from", uniqueCurrentID);
                                object.put("star_status", star_status);
                                object.put("type", MessageFactory.CHAT_TYPE_GROUP);
                                SendMessageEvent event = new SendMessageEvent();
                                event.setEventName(SocketManager.EVENT_CLEAR_CHAT);
                                event.setMessageObject(object);
                                EventBus.getDefault().post(event);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }


                        Toast.makeText(ChatHistory.this, "All chats are Cleared", Toast.LENGTH_SHORT).show();

                        // db.deleteAllChatList();

                    }


                    @Override
                    public void onNegativeButtonClick() {

                    }
                });
                dialog2.show(getSupportFragmentManager(), "CustomAlert");
                break;


            case R.id.R4_chathistory:
                CustomAlertDialog dialog1 = new CustomAlertDialog();
                dialog1.setMessage("Are you sure you want to Unarchive all chats?");
                dialog1.setPositiveButtonText("Ok");
                dialog1.setNegativeButtonText("Cancel");
                dialog1.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        unarchivelist();
                        unarchivelistgroup();
                        Archive.setVisibility(View.VISIBLE);
                        unarchive.setVisibility(View.GONE);
                        Toast.makeText(ChatHistory.this, "All Chats are Unrchived", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }
                });
                dialog1.show(getSupportFragmentManager(), "CustomAlert");
                break;


        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CLEAR_CHAT)) {
            Object[] obj = event.getObjectsArray();
            load_clear_chat(obj[0].toString());
        }
    }

    private void load_clear_chat(String data) {
        try {
            JSONObject object = new JSONObject(data);

            try {
                String from = object.getString("from");
                String convId = object.getString("convId");
                String type = object.getString("type");
                int star_status;
                Boolean starred = false;
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

                            if (starred) {
                                ArrayList<MessageItemChat> value = new ArrayList<>();
                                for (int i = 0; i < mChatData.size(); i++) {
                                    if (mChatData.get(i).getStarredStatus().equalsIgnoreCase(MessageFactory.MESSAGE_UN_STARRED)) {
                                        value.add(mChatData.get(i));
                                    }
                                }

                                for (int i = 0; i < value.size(); i++) {
                                    mChatData.remove(value.get(i));
                                }

                            } else {
                                mChatData.clear();

                            }
                        } else {

                            if (starred) {
                                ArrayList<MessageItemChat> value = new ArrayList<>();
                                for (int i = 0; i < mChatData.size(); i++) {
                                    if (mChatData.get(i).getStarredStatus().equalsIgnoreCase(MessageFactory.MESSAGE_UN_STARRED)) {
                                        value.add(mChatData.get(i));
                                    }
                                }

                                for (int i = 0; i < value.size(); i++) {
                                    mChatData.remove(value.get(i));
                                }

                            } else {
                                mChatData.clear();

                            }
                        }

                    }
                    //  mAdapter.notifyDataSetChanged();
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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    protected void sendEmail() {
        Intent LaunchIntent = getPackageManager().getLaunchIntentForPackage("com.android.email");
        LaunchIntent.putExtra(Intent.EXTRA_SUBJECT, "tset");
        LaunchIntent.putExtra(Intent.EXTRA_TEXT, "tset");
        startActivity(LaunchIntent);
    }

    private void archivelist() {

        ArrayList<MessageItemChat> databases = db.selectChatList(MessageFactory.CHAT_TYPE_SINGLE);
        uniqueStore.clear();
        for (MessageItemChat msgItem : databases) {
            String docID = uniqueCurrentID + "-" + msgItem.getReceiverID();

            session.putarchive(docID);
            if (!session.getarchive(docID)) {
                session.putarchive(docID);
            }
            if (userInfoSession.hasChatConvId(docID)) {
                try {
                    String convId = userInfoSession.getChatConvId(docID);
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
        }

    }

    private void unarchivelist() {

        ArrayList<MessageItemChat> databases = db.selectChatList(MessageFactory.CHAT_TYPE_SINGLE);
        uniqueStore.clear();
        for (MessageItemChat msgItem : databases) {
            String docID = uniqueCurrentID + "-" + msgItem.getReceiverID();

            if (session.getarchive(docID)) {
                session.removearchive(docID);
            }
            if (userInfoSession.hasChatConvId(docID)) {
                try {
                    String convId = userInfoSession.getChatConvId(docID);
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

    private void archivelistgroup() {
        ArrayList<MessageItemChat> databases = db.selectChatList(MessageFactory.CHAT_TYPE_GROUP);
        uniqueStore.clear();
        for (MessageItemChat msgItem : databases) {

            String docID = uniqueCurrentID + "-" + msgItem.getReceiverID() + "-g";

            GroupInfoSession groupInfoSession = new GroupInfoSession(ChatHistory.this);
            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docID);
            if (!session.getarchivegroup(docID)) {
                session.putarchivegroup(docID);
            }
            try {
                String[] array = docID.split("-");
                JSONObject object = new JSONObject();
                object.put("convId", array[1]);
                object.put("from", uniqueCurrentID);
                object.put("status", 1);
                object.put("type", MessageFactory.CHAT_TYPE_GROUP);
                SendMessageEvent event = new SendMessageEvent();
                event.setEventName(SocketManager.EVENT_ARCHIVE_UNARCHIVE);
                event.setMessageObject(object);
                EventBus.getDefault().post(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("value-------count------------->" + session.getarchivecountgroup());
        }
    }

    private void unarchivelistgroup() {
        ArrayList<MessageItemChat> databases = db.selectChatList(MessageFactory.CHAT_TYPE_GROUP);
        uniqueStore.clear();
        for (MessageItemChat msgItem : databases) {
            String docID = uniqueCurrentID + "-" + msgItem.getReceiverID() + "-g";

            String[] array = docID.split("-");
            if (session.getarchivegroup(docID)) {
                session.removearchivegroup(docID);
            }
            try {

                String convId = array[1];
                JSONObject object = new JSONObject();
                object.put("convId", convId);
                object.put("from", uniqueCurrentID);
                object.put("status", 0);
                object.put("type", MessageFactory.CHAT_TYPE_GROUP);
                SendMessageEvent event = new SendMessageEvent();
                event.setEventName(SocketManager.EVENT_ARCHIVE_UNARCHIVE);
                event.setMessageObject(object);
                EventBus.getDefault().post(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            MessageItemChat data;
            GroupInfoSession groupInfoSession = new GroupInfoSession(ChatHistory.this);
            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docID);
            // session.removearchivegroup(docID);
            System.out.println("value-------count------------->" + session.getarchivecountgroup());
        }


    }
}




