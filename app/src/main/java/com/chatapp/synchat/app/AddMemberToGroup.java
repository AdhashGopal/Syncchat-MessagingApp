package com.chatapp.synchat.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.AddGroupMemberAdapter;
import com.chatapp.synchat.app.adapter.RItemAdapter;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.utils.BlockUserUtils;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.message.GroupEventInfoMessage;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * created by  Adhash Team on 11/30/2016.
 */
public class AddMemberToGroup extends CoreActivity implements RItemAdapter.OnItemClickListener, View.OnClickListener {

    private RecyclerView rvContacts;
    private ImageButton ibBack;
    private TextView tvNoContacts;

    private List<ChatappContactModel> ChatappContacts, filterContacts;
    private String mCurrentUserId, mGroupId, mGroupName, mGroupUserIds;
    private AddGroupMemberAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_group);

        initView();
        initData();
    }

    private void initData() {

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        ChatappContacts = contactDB_sqlite.getLinkedChatappContacts();


        mCurrentUserId = SessionManager.getInstance(AddMemberToGroup.this).getCurrentUserID();

        Bundle bundle = getIntent().getExtras();
        mGroupId = bundle.getString("GroupId", "");
        mGroupName = bundle.getString("GroupName", "");
        mGroupUserIds = bundle.getString("GroupUserIds", "");

        initProgress("Loading ...", false);
        filterContacts = new ArrayList<>();

        if (ChatappContacts != null && ChatappContacts.size() > 0) {

            for (ChatappContactModel item : ChatappContacts) {
                String userId = item.get_id();
                if (!mGroupUserIds.contains(userId) && !contactDB_sqlite.getBlockedMineStatus(userId, false).equals("1")) {
//                if (!mGroupUserIds.contains(userId) && !contactsDB.getBlockedMineStatus(userId, false).equals("1")) {
                    filterContacts.add(item);
                }
            }

            Collections.sort(filterContacts, Getcontactname.nameAscComparator);

            adapter = new AddGroupMemberAdapter(AddMemberToGroup.this, filterContacts);
            rvContacts.setAdapter(adapter);
            rvContacts.addOnItemTouchListener(new RItemAdapter(AddMemberToGroup.this,
                    rvContacts, AddMemberToGroup.this));
        } else {
            Toast.makeText(AddMemberToGroup.this, "Your contacts are not available in " + getResources().getString(R.string.app_name), Toast.LENGTH_SHORT).show();
        }

        if (filterContacts.size() == 0) {
            rvContacts.setVisibility(View.GONE);
            tvNoContacts.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ibBack.setOnClickListener(AddMemberToGroup.this);

        rvContacts = (RecyclerView) findViewById(R.id.rvContacts);

        LinearLayoutManager manager = new LinearLayoutManager(AddMemberToGroup.this);
        rvContacts.setLayoutManager(manager);

        tvNoContacts = (TextView) findViewById(R.id.tvNoContacts);
    }

    @Override
    public void onItemClick(View view, int position) {
        showAddAlert(position);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    private void showAddAlert(final int position) {
        String userId = filterContacts.get(position).get_id();
        String name = filterContacts.get(position).getMsisdn();

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);

        if (!contactDB_sqlite.getBlockedStatus(userId, false).equals("1")) {
            final CustomAlertDialog dialog = new CustomAlertDialog();
            String msg = "Add " + filterContacts.get(position).getFirstName() + " to \"" + mGroupName + "\" group";
            dialog.setMessage(msg);

            dialog.setPositiveButtonText("Ok");
            dialog.setNegativeButtonText("Cancel");

            dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                @Override
                public void onPositiveButtonClick() {
                    if (internetcheck()) {
                        performAddMemberGroup(filterContacts.get(position).get_id());
                    } else {
                        Toast.makeText(AddMemberToGroup.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onNegativeButtonClick() {
                    dialog.dismiss();
                }
            });
            dialog.show(getSupportFragmentManager(), "Add member");
        } else {
            Getcontactname getcontactname = new Getcontactname(this);
            String message = "Unblock" + " " + getcontactname.getSendername(userId, name) + " " + "to add group?";
            displayAlert(message, userId);
        }
    }

    private void displayAlert(final String txt, final String userId) {
        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setMessage(txt);
        dialog.setNegativeButtonText("Cancel");
        dialog.setPositiveButtonText("Unblock");
        dialog.setCancelable(false);
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                BlockUserUtils.changeUserBlockedStatus(AddMemberToGroup.this, EventBus.getDefault(),
                        mCurrentUserId, userId, false);
                dialog.dismiss();
            }

            @Override

            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "Unblock a person");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {

        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP)) {
            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                String groupAction = object.getString("groupType");

                if (groupAction.equalsIgnoreCase(SocketManager.ACTION_ADD_GROUP_MEMBER)) {
                    loadAddMemberMessage(object);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_BLOCK_USER)) {
            loadBlockUserMessage(event);
        }
    }

    private void loadBlockUserMessage(ReceviceMessageEvent event) {
        try {
            Object[] obj = event.getObjectsArray();
            JSONObject object = new JSONObject(obj[0].toString());

            String from = object.getString("from");
            String to = object.getString("to");

            if (mCurrentUserId.equalsIgnoreCase(from)) {
                for (int i = 0; i < filterContacts.size(); i++) {
                    String userId = filterContacts.get(i).get_id();
                    if (userId.equals(to)) {
                        String stat = object.getString("status");
                        if (stat.equalsIgnoreCase("1")) {
                            Toast.makeText(this, "Contact is blocked", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Contact is Unblocked", Toast.LENGTH_SHORT).show();
                        }
                        adapter.notifyDataSetChanged();
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean internetcheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }

    private void loadAddMemberMessage(JSONObject object) {

        try {
            String msg = object.getString("message");
            String err = object.getString("err");
            String groupId = object.getString("groupId");
            String msgId = object.getString("id");
            String timeStamp = object.getString("timeStamp");
            String from = object.getString("from");

            JSONObject newUserObj = object.getJSONObject("newUser");
            String newUserId = newUserObj.getString("_id");
            String newUserMsisdn = newUserObj.getString("msisdn");
         //   String newUserPhNo = newUserObj.getString("PhNumber");
//            String newUserName = newUserObj.getString("Name");
            if (object.has("Status")) {
                String newUserStatus = newUserObj.getString("Status");
            }

            String senderOriginalName="";

            if(object.has("fromuser_name")) {

                try {
                    senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }


            }

            GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, this);
            MessageItemChat item = message.createMessageItem(MessageFactory.add_group_member, false, msg, MessageFactory.DELIVERY_STATUS_READ,
                    mGroupId, mGroupName, from, newUserId,senderOriginalName);
            String docId = mCurrentUserId.concat("-").concat(mGroupId).concat("-g");
            item.setSenderName(mGroupName);
            item.setGroupName(mGroupName);
            item.setIsInfoMsg(true);
            item.setMessageId(docId.concat("-").concat(timeStamp));
//            db.updateChatMessage(docId, item);

            Intent exitIntent = new Intent();
            exitIntent.putExtra("MemberAdded", true);
            setResult(RESULT_OK, exitIntent);
            hideProgressDialog();
            finish();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void performAddMemberGroup(String newUserId) {
        long ts = Calendar.getInstance().getTimeInMillis();
        String msgId = mCurrentUserId + "-" + mGroupId + "-g-" + ts;

        try {
            JSONObject object = new JSONObject();
            object.put("groupType", SocketManager.ACTION_ADD_GROUP_MEMBER);
            object.put("from", SessionManager.getInstance(AddMemberToGroup.this).getCurrentUserID());
            object.put("id", ts);
            object.put("toDocId", msgId);
            object.put("groupId", mGroupId);
            object.put("newuser", newUserId);
            object.put("add_new_group_name", true);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_GROUP);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);

            showProgressDialog();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(AddMemberToGroup.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(AddMemberToGroup.this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ibBack:
                finish();
                break;

        }

    }
}
