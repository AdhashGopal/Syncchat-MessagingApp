package com.chatapp.android.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.adapter.BlockContactAdapter;
import com.chatapp.android.app.adapter.RItemAdapter;
import com.chatapp.android.app.dialog.CustomMultiTextItemsDialog;
import com.chatapp.android.app.utils.BlockUserUtils;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.model.BlockListPojo;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.MultiTextDialogPojo;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * created by  Adhash Team on 3/6/2017.
 */
public class BlockContactList extends CoreActivity {
    BlockContactAdapter blockListAdapter;
    ArrayList<BlockListPojo> blockdata = new ArrayList<>();
    RecyclerView rvblock;
    String id, mCurrentUserId;
    TextView usernoblockLabel;
    Session session;
    private LinearLayoutManager mLayoutManager;
    private final int CHANGE_IN_BLOCK_USER = 4;
    BlockListPojo blockListPojo;
    private ProgressDialog progressDialog;

    private boolean isBlockedUsersChanged = false;
    public static final String KEY_BLOCKED_USERS_CHANGED = "BlockedUsersChanged";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocked_listview);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("" +
                "Blocked contacts");

        actionBar.setDisplayHomeAsUpEnabled(true);
        rvblock = (RecyclerView) findViewById(R.id.block_list);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvblock.setLayoutManager(mLayoutManager);
        rvblock.setItemAnimator(new DefaultItemAnimator());
        rvblock.setHasFixedSize(true);
        mCurrentUserId = SessionManager.getInstance(this).getCurrentUserID();
        usernoblockLabel = (TextView) findViewById(R.id.usernoblockLabel);

        session = new Session(BlockContactList.this);

        blockListAdapter = new BlockContactAdapter(this, blockdata);
        rvblock.setAdapter(blockListAdapter);

        setAdapter();

        rvblock.addOnItemTouchListener(new RItemAdapter(this, rvblock, new RItemAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                showUnblockAlert(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
                showUnblockAlert(position);
            }

        }));

    }

    private void showUnblockAlert(int position) {
        blockListPojo = blockdata.get(position);
        id = blockListPojo.getId();
        String contactName = blockListPojo.getNumber();
        Getcontactname getcontactname = new Getcontactname(BlockContactList.this);
        contactName = getcontactname.getSendername(id, contactName);

        List<MultiTextDialogPojo> labelsList = new ArrayList<>();
        MultiTextDialogPojo pojo = new MultiTextDialogPojo();
        if (blockListPojo.isSecretChat()) {
            pojo.setLabelText("Unblock  " + contactName + " in secret chat?");
        } else {
            pojo.setLabelText("Unblock  " + contactName + "?");
        }
        labelsList.add(pojo);

        CustomMultiTextItemsDialog dialog = new CustomMultiTextItemsDialog();
        dialog.setLabelsList(labelsList);
        dialog.setDialogItemClickListener(new CustomMultiTextItemsDialog.DialogItemClickListener() {
            @Override
            public void onDialogItemClick(int position) {
                switch (position) {

                    case 0:
                        // Need to check data with local database
                        BlockUserUtils.changeUserBlockedStatus(BlockContactList.this,
                                EventBus.getDefault(), mCurrentUserId, id, blockListPojo.isSecretChat());
                }
            }
        });
        dialog.show(getSupportFragmentManager(), "block User");
    }

    private void setAdapter() {
        blockdata.clear();

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        ArrayList<ChatappContactModel> contactList = contactDB_sqlite.getAllChatappContacts();

        for (int i = 0; i < contactList.size(); i++) {
            ChatappContactModel contact = contactList.get(i);
            String userId = contact.get_id();

            if (contactDB_sqlite.getBlockedStatus(userId, false).equals("1")) {
//            if (contactsDB.getBlockedStatus(userId, false).equals("1")) {
                BlockListPojo blockListPojo = new BlockListPojo();

                String msisdn = contact.getMsisdn();
                Getcontactname getcontactname = new Getcontactname(BlockContactList.this);
                String name = getcontactname.getSendername(userId, msisdn);
                String image = ("uploads/users/").concat(userId).concat(".jpg?id=") + Calendar.getInstance().getTimeInMillis();

                blockListPojo.setName(name);
                blockListPojo.setImagePath(image);
                blockListPojo.setNumber(msisdn);
                blockListPojo.setId(userId);
                blockListPojo.setIsSecretChat(false);

                blockdata.add(blockListPojo);
            }

            if (contactDB_sqlite.getBlockedStatus(userId, true).equals("1")) {
//            if (contactsDB.getBlockedStatus(userId, true).equals("1")) {
                BlockListPojo blockListPojo = new BlockListPojo();

                String msisdn = contact.getMsisdn();
                Getcontactname getcontactname = new Getcontactname(BlockContactList.this);
                String name = getcontactname.getSendername(userId, msisdn);
                String image = ("uploads/users/").concat(userId).concat(".jpg?id=") + Calendar.getInstance().getTimeInMillis();

                blockListPojo.setName(name);
                blockListPojo.setImagePath(image);
                blockListPojo.setNumber(msisdn);
                blockListPojo.setId(userId);
                blockListPojo.setIsSecretChat(true);

                blockdata.add(blockListPojo);
            }
        }

        if (blockdata.size() > 0) {
            usernoblockLabel.setVisibility(View.GONE);
        }

        blockListAdapter.notifyDataSetChanged();

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_BLOCK_USER)) {

            String data = event.getObjectsArray()[0].toString();
            try {
                JSONObject object = new JSONObject(data);
                String from = object.getString("from");
                if (from.equalsIgnoreCase(mCurrentUserId)) {
                    unblock(event);
                    blockdata.remove(blockListPojo);
                    blockListAdapter.notifyDataSetChanged();

                    isBlockedUsersChanged = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void unblock(ReceviceMessageEvent event) {
        try {
            Object[] obj = event.getObjectsArray();
            JSONObject object = new JSONObject(obj[0].toString());
            String stat = object.getString("status");
            String toid = object.getString("to");
            /*if (id.equalsIgnoreCase(toid)) {
                if (stat.equalsIgnoreCase("0")) {
                    List<String> blockedIds=session.getBlockedIds();
                    session.unBlock(toid);
                    blockedIds.remove(toid);
                    session.setBlockedIds(blockedIds);
                 }

                }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHANGE_IN_BLOCK_USER && resultCode == RESULT_OK && data != null) {
            boolean isBlocked = data.getBooleanExtra("BlockAdded", true);

            if (isBlocked) {
                setAdapter();
            }
            isBlockedUsersChanged = true;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.block_contact, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.block_user:
                performBlockUser();
                break;

            case android.R.id.home:
                goBackScreen();
                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

    public void performBlockUser() {
        Intent intent = new Intent(BlockContactList.this, SelectPeopleForBlock.class);
        startActivityForResult(intent, CHANGE_IN_BLOCK_USER);
    }

    @Override
    public void onBackPressed() {
        goBackScreen();

        super.onBackPressed();
    }

    private void goBackScreen() {
        if (isBlockedUsersChanged) {
            Intent resultIntent = new Intent();
            resultIntent.putExtra(KEY_BLOCKED_USERS_CHANGED, true);
            setResult(RESULT_OK, resultIntent);
        }
    }
}
