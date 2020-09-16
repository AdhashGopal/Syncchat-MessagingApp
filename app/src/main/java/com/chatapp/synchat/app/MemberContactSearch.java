package com.chatapp.synchat.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.GroupPeopleSearch;
import com.chatapp.synchat.app.adapter.RItemAdapter;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.GroupMembersPojo;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class MemberContactSearch extends AppCompatActivity {
    RecyclerView lvContacts;
    private SearchView searchView;
    GroupPeopleSearch adapter;
    private Toolbar toolbar;

    /**
     * Selected group user get the data from another activity to load adapter
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactsearch);

        try {

            toolbar = (Toolbar) findViewById(R.id.toolbar);

            getSupportActionBar().setTitle("Search Participant");


            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            lvContacts = (RecyclerView) findViewById(R.id.listContactsgroup);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MemberContactSearch.this.getApplicationContext());
            lvContacts.setLayoutManager(mLayoutManager);
            lvContacts.setItemAnimator(new DefaultItemAnimator());
            lvContacts.setNestedScrollingEnabled(false);
            Bundle bundle = getIntent().getExtras();
            ArrayList<GroupMembersPojo> item = (ArrayList<GroupMembersPojo>) bundle.getSerializable("test");
            if (item != null) {
                for (int i = 0; i < item.size(); i++) {
                    String name = item.get(i).getContactName();
                    if (name.equalsIgnoreCase("you")) {
                        item.remove(item.get(i));
                        adapter = new GroupPeopleSearch(MemberContactSearch.this, item);
                        lvContacts.setAdapter(adapter);
                    }
                }
            }

            lvContacts.addOnItemTouchListener(new RItemAdapter(this, lvContacts, new RItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    GroupMembersPojo contactSearchModel = adapter.getItem(position);

                    String userId = contactSearchModel.getUserId();
                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(MemberContactSearch.this);
                    ChatappContactModel contact = new ChatappContactModel();

                    contact.set_id(userId);
                    contact.setStatus(decodeBase64(contactSearchModel.getStatus()));
                    contact.setAvatarImageUrl(contactSearchModel.getUserDp());
                    contact.setMsisdn(contactSearchModel.getMsisdn());
                    contact.setRequestStatus("3");

                    contact.setFirstName(contactSearchModel.getContactName());

                    contactDB_sqlite.updateUserDetails(userId, contact);


                    if (!contactDB_sqlite.getBlockedStatus(userId, false).equals("1")) {

                        try {

                            Intent intent = new Intent(MemberContactSearch.this, ChatViewActivity.class);
//                intent.putExtra("receiverUid", contactSearchModel.getNumberInDevice());
                            intent.putExtra("receiverUid", contactSearchModel.getMsisdn());
                            String firstName = "";
                            if (contactSearchModel.getName() != null)
                                firstName = contactSearchModel.getContactName();
                            intent.putExtra("receiverName", firstName);
                            intent.putExtra("documentId", contactSearchModel.getUserId());
                            intent.putExtra("Username", firstName);
                            intent.putExtra("Image", contactSearchModel.getUserDp());
                            intent.putExtra("type", 0);
                            intent.putExtra("Userstatus", contactSearchModel.getStatus());
                            String msisdn = contactSearchModel.getMsisdn();
                            intent.putExtra("msisdn", msisdn);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {

                    /*String message = "Unblock" + " " + contactSearchModel.getName() + " to start conversation?";
                    displayAlert(message, userId);*/
                    }

                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            }));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String decodeBase64(String status) {
        return new String(Base64.decode(status, Base64.DEFAULT));
    }

    /**
     * Search user name function
     *
     * @param menu layout binding
     * @return value
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        getMenuInflater().inflate(R.menu.new_chat_fragment, menu);
        MenuItem chats_contactIcon = menu.findItem(R.id.chats_contactIcon);
        chats_contactIcon.setVisible(false);
        MenuItem menurefresh = menu.findItem(R.id.menurefresh);
        menurefresh.setVisible(false);
        MenuItem menuSetting = menu.findItem(R.id.chats_settings);
        menuSetting.setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.chatsc_searchIcon);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("") && newText.isEmpty()) {
                    searchView.clearFocus();
                }
                adapter.getFilter().filter(newText);
                return false;
            }
        });


        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        searchTextView.setTextColor(Color.WHITE);
        searchTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(searchTextView);
                    return true;
                }
                return false;
            }
        });

        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * hide keyboard
     *
     * @param searchTextView
     */
    public void hideKeyboard(AutoCompleteTextView searchTextView) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);
    }

    /**
     * finish current activity
     *
     * @param item menu item
     * @return value
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
