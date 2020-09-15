package com.chatapp.android.app;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.chatapp.android.app.adapter.BlockListAdapter;
import com.chatapp.android.app.adapter.RItemAdapter;
import com.chatapp.android.app.utils.BlockUserUtils;
import com.chatapp.android.app.utils.ConnectivityInfo;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * created by  Adhash Team on 3/7/2017.
 */
public class SelectPeopleForBlock extends CoreActivity {
    String id, from;
    RecyclerView recyclerView;
    Session session;
    SearchView searchView;
    ChatappContactModel item;
    private BlockListAdapter adapter;
    private List<ChatappContactModel> ChatappEntries = new ArrayList<>();
    private List<String> filteredList = new ArrayList<>();
    private ArrayList<ChatappContactModel> myFinalList = new ArrayList<>();
    private ArrayList<ChatappContactModel> selectedItem = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_people_block);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Select Contact");
        actionBar.setDisplayHomeAsUpEnabled(true);
        session = new Session(SelectPeopleForBlock.this);

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        ChatappEntries = contactDB_sqlite.getSavedChatappContacts();
        from = SessionManager.getInstance(this).getCurrentUserID();

        ArrayList<ChatappContactModel> contactList = contactDB_sqlite.getAllChatappContacts();

        for (int i = 0; i < contactList.size(); i++) {
            String toUserId = contactList.get(i).get_id();
            if (contactDB_sqlite.getBlockedStatus(toUserId, false).equalsIgnoreCase("0")) {
                filteredList.add(contactList.get(i).get_id());
            }
        }

        if (filteredList.size() != 0) {
            for (int i = 0; i < filteredList.size(); i++) {
                ChatappContactModel chatappContactModel = new ChatappContactModel();

                for (int j = 0; j < ChatappEntries.size(); j++) {
                    if (ChatappEntries.get(j).get_id().equalsIgnoreCase(filteredList.get(i))) {

                        chatappContactModel = ChatappEntries.get(j);

                        myFinalList.add(chatappContactModel);
                    }

                }

            }
        } else {
            myFinalList.addAll(ChatappEntries);
        }
        Collections.sort(myFinalList, Getcontactname.nameAscComparator);
        adapter = new BlockListAdapter(SelectPeopleForBlock.this, myFinalList);
        recyclerView = (RecyclerView) findViewById(R.id.listToBlock);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SelectPeopleForBlock.this.getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);

        initProgress("Loading...", false);

        recyclerView.addOnItemTouchListener(new RItemAdapter(this, recyclerView, new RItemAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {
                if (ConnectivityInfo.isInternetConnected(SelectPeopleForBlock.this)) {
                    showProgressDialog();

                    item = adapter.getItem(position);
                    selectedItem.add(item);
                    id = item.get_id();

                    BlockUserUtils.changeUserBlockedStatus(SelectPeopleForBlock.this, EventBus.getDefault(),
                            from, id, false);

                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(SelectPeopleForBlock.this, "Check your network connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {


            }


        }));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_BLOCK_USER)) {
            block(event);
        }
    }

    public void block(ReceviceMessageEvent event) {
        try {
            Object[] obj = event.getObjectsArray();
            JSONObject object = new JSONObject(obj[0].toString());
            Log.e("Response---", object.toString());
            String stat = object.getString("status");
            String toid = object.getString("to");
            String fromUserId = object.getString("from");
            if (from.equalsIgnoreCase(fromUserId)) {
                hideProgressDialog();

                loadBlockContactList(object);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void loadBlockContactList(JSONObject object) {
        try {
            String err = object.getString("err");
            if (err.equalsIgnoreCase("0")) {
                Intent okIntent = new Intent();
                okIntent.putExtra("BlockAdded", true);
                // okIntent.putExtra("mySelectedData",item);
                setResult(RESULT_OK, okIntent);
                finish();
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.select_people_for_group, menu);
        MenuItem searchItem = menu.findItem(R.id.menuSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        if (myFinalList.size() > 0) {
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
                        if (newText.length() > 0) {
                            adapter.getFilter().filter(newText);
                        } else {
                            adapter.updateInfo(myFinalList);
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
            });

            searchView.setOnCloseListener(new SearchView.OnCloseListener() {
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
        MenuItemCompat.setActionView(searchItem, searchView);
        return super.onCreateOptionsMenu(menu);
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {


            case android.R.id.home:
                finish();
                return true;


        }
        return super.onOptionsItemSelected(item);
    }

}
