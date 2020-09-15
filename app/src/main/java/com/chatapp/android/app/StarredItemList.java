package com.chatapp.android.app;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.adapter.StarredMessageAdapter;
import com.chatapp.android.app.dialog.ChatLockPwdDialog;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.utils.GroupInfoSession;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.ChatLockPojo;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * created by  Adhash Team on 2/3/2017.
 */
public class StarredItemList extends CoreActivity implements StarredMessageAdapter.ChatMessageItemClickListener {
    MessageItemChat myitems;
    StarredMessageAdapter mAdapter;
    private Boolean isGroupChat;
    ArrayList<MessageItemChat> items;
    ArrayList<MessageItemChat> dbItems;
    MessageDbController db;
    private String from, receiverDocumentID;
    RecyclerView rvdata;
    String uniqueCurrentID;
    private LinearLayoutManager mLayoutManager;
    private SearchView searchView;
    private UserInfoSession userInfoSession;
    private Getcontactname getcontactname;
    private String mCurrentUserId;
    private GroupInfoSession groupInfoSession;
    HashMap<String, MessageItemChat> uniqueStore = new HashMap<>();
    private SessionManager sessionManager;
//    public TextView tv_starredmsg_no;

    /**
     * data binding & get database value
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starred_listview);
        db = CoreController.getDBInstance(this);
//        tv_starredmsg_no = (TextView) findViewById(R.id.tv_nostarred);
        userInfoSession = new UserInfoSession(StarredItemList.this);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Starred Messages");
        uniqueCurrentID = SessionManager.getInstance(this).getCurrentUserID();
        getcontactname = new Getcontactname(StarredItemList.this);
        actionBar.setDisplayHomeAsUpEnabled(true);
        from = SessionManager.getInstance(this).getCurrentUserID();
        rvdata = (RecyclerView) findViewById(R.id.rvstarred);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvdata.setLayoutManager(mLayoutManager);
        rvdata.setItemAnimator(new DefaultItemAnimator());
        rvdata.setHasFixedSize(true);
        sessionManager = SessionManager.getInstance(getApplicationContext());
        mCurrentUserId = sessionManager.getCurrentUserID();
        uniqueCurrentID = SessionManager.getInstance(getApplicationContext()).getCurrentUserID();
        groupInfoSession = new GroupInfoSession(getApplicationContext());
        dbItems = new ArrayList<>();
        mAdapter = new StarredMessageAdapter(StarredItemList.this, dbItems, StarredItemList.this.getSupportFragmentManager());
//      showProgressDialog();
        updatestaredmessages_fromDB();
//      new LoadStarredMessageTask().execute();

    }

    /**
     * get group details
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
     * update stared messages from database
     */
    private void updatestaredmessages_fromDB() {
        dbItems = db.selectAllStarredMessages();
        if (dbItems.size() > 0 && mAdapter != null) {
            mAdapter.setItemClickListener(StarredItemList.this);
            rvdata.setAdapter(mAdapter);
            mAdapter.updateInfo(dbItems);
        } else {
            Toast.makeText(StarredItemList.this, "No star messages found", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * CreateOptionsMenu & searchview data
     *
     * @param menu layout bindin
     * @return value
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.select_people_for_group, menu);
        MenuItem searchItem = menu.findItem(R.id.menuSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*searchView.setIconifiedByDefault(true);
                searchView.setIconified(true);
                searchView.setQuery("", false);
                searchView.clearFocus();*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (newText.equals("") && newText.isEmpty()) {
                        searchView.clearFocus();
                        //closeKeypad();
                        mAdapter.updateInfo(dbItems);
                    }
                    mAdapter.getFilter().filter(newText);
                    /*if (newText.length() > 0) {

                        if (StarredMessageAdapter.starredserach_result) {
//                        tv_starredmsg_no.setVisibility(View.VISIBLE);
                        } else {
//                        tv_starredmsg_no.setVisibility(View.GONE);
                        }
                    } else {
                        mAdapter.updateInfo(dbItems);
//                    tv_starredmsg_no.setVisibility(View.GONE);
                    }*/
                } catch (Exception e) {
                    e.printStackTrace();
                }


                return false;
            }
        });

      /*  searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                menu.findItem(R.id.menuSearch).setVisible(true);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.findItem(R.id.menuSearch).setVisible(false);
            }
        });


        searchView.setIconifiedByDefault(true);
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);
*/
        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        searchTextView.setTextColor(Color.WHITE);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        MenuItemCompat.setActionView(searchItem, searchView);
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * OptionsItemSelected
     *
     * @param item specific item selected
     * @return value
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                mAdapter.stopAudioOnClearChat();
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * click action
     *
     * @param itemView view
     * @param position specific item
     */
    @Override
    public void onItemClick(View itemView, int position) {

        ChatLockPojo lockPojo = getChatLockdetailfromDB(position);
        if (SessionManager.getInstance(StarredItemList.this).getLockChatEnabled().equals("1")
                && lockPojo != null) {
            String stat = "", pwd = null;

            stat = lockPojo.getStatus();
            pwd = lockPojo.getPassword();

            MessageItemChat e = mAdapter.getItem(position);
            String docID = e.getMessageId();
            String[] ids = docID.split("-");
            String documentID = "";
            if (e.getMessageId().contains("-g")) {
                documentID = ids[0] + "-" + ids[1] + "-g";
            } else {
                documentID = ids[0] + "-" + ids[1];
            }
            if (stat.equals("1")) {
                openUnlockChatDialog(documentID, stat, pwd, position);
            } else {
                navigateTochatviewpage(position);
            }
        } else {
            navigateTochatviewpage(position);
        }

        mAdapter.stopAudioOnNavigate();
    }

    @Override
    public void onItemLongClick(View itemView, int position) {

    }


    /**
     * navigate To chat viewpage
     *
     * @param position select specific item
     */
    private void navigateTochatviewpage(int position) {
        MessageItemChat msgItem = mAdapter.getItem(position);
        String msgId = msgItem.getMessageId();

        if (msgId.contains("-g")) {
            msgItem.setSenderName(msgItem.getGroupName());
        }

        getcontactname.navigateToChatViewpagewithmessageitems(msgItem, "star");
        finish();
    }

    /**
     * open Unlock ChatDialog
     *
     * @param docId    input value(docId)
     * @param status   input value(status)
     * @param pwd      input value(pwd)
     * @param position input value(position)
     */
    private void openUnlockChatDialog(String docId, String status, String pwd, int position) {
        String convId = userInfoSession.getChatConvId(docId);
        ChatLockPwdDialog dialog = new ChatLockPwdDialog();
        dialog.setTextLabel1("Enter your Password");
        dialog.setEditTextdata("Enter new Password");
        dialog.setforgotpwdlabel("Forgot Password");
        dialog.setHeader("Unlock Chat");
        dialog.setButtonText("Unlock");
        Bundle bundle = new Bundle();
        bundle.putSerializable("MessageItem", mAdapter.getItem(position));
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


    /**
     * get Chat Lock detail from database
     *
     * @param position select specific item
     * @return value
     */
    private ChatLockPojo getChatLockdetailfromDB(int position) {

        MessageItemChat e = mAdapter.getItem(position);
        String docID = e.getMessageId();
        String[] id = docID.split("-");
        String documentID = "";
        String chatType = MessageFactory.CHAT_TYPE_SINGLE;
        if (e.getMessageId().contains("-g")) {
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

    /**
     * stop adapter
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mAdapter.stopAudioOnClearChat();
    }
}