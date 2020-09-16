package com.chatapp.synchat.app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.RItemAdapter;
import com.chatapp.synchat.app.adapter.SNGAdapter;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.utils.BlockUserUtils;
import com.chatapp.synchat.app.utils.BroadcastInfoSession;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.app.widget.CircleImageView;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.model.BroadcastInfoPojo;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class SelectPeopleForGroupChat extends CoreActivity {


    RecyclerView lvContacts;
    AvnNextLTProRegTextView contact_empty_selectgroup;
    private EditText mSearchEt;
    private List<ChatappContactModel> filterContacts;
    private SNGAdapter adapter;
    TextView no_text;

    private ProgressDialog dialog;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SearchView searchView;
    private SessionManager sessionManager;
    private ImageView search;
    ImageView backarrow, backButton;
    private Map<String, String> mycontact = new HashMap();
    HorizontalScrollView selectgroupmember;
    LinearLayout mainlayout;
    AvnNextLTProRegTextView selectcontactmember;
    AvnNextLTProDemiTextView selectcontact;
    Button doneButton;
    EditText etSearch;
    List<Map<String, String>> mylist = new ArrayList<>();
    private ArrayList<String> member = new ArrayList<>();
    private String mCurrentUserId;

    private Getcontactname getcontactname;
    ArrayList<ChatappContactModel> chatappContactModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_group);
        getSupportActionBar().hide();
        /* Get the participant Provider to get the list of participants */

        contact_empty_selectgroup = (AvnNextLTProRegTextView) findViewById(R.id.contact_empty_selectgroup);
        ArrayList<ChatappContactModel> chatappEntries = new ArrayList<>();
        filterContacts = new ArrayList<>();

        lvContacts = (RecyclerView) findViewById(R.id.listContactsgroup);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SelectPeopleForGroupChat.this.getApplicationContext());
        lvContacts.setLayoutManager(mLayoutManager);
        lvContacts.setItemAnimator(new DefaultItemAnimator());
        lvContacts.setNestedScrollingEnabled(false);
        mainlayout = (LinearLayout) findViewById(R.id.maincontainer);
        selectcontactmember = (AvnNextLTProRegTextView) findViewById(R.id.selectcontactmember);
        selectcontact = (AvnNextLTProDemiTextView) findViewById(R.id.selectcontact);
        backButton = (ImageView) findViewById(R.id.backarrow_contactsetting);
        etSearch = (EditText) findViewById(R.id.etSearch);
        backarrow = (ImageView) findViewById(R.id.backarrow);
        search = (ImageView) findViewById(R.id.search);
        doneButton = (Button) findViewById(R.id.doneButton);
        no_text = (TextView) findViewById(R.id.no_text);
        selectgroupmember = (HorizontalScrollView) findViewById(R.id.selectgroupmember);

        getcontactname = new Getcontactname(this);
        sessionManager = SessionManager.getInstance(this);
        mCurrentUserId = sessionManager.getCurrentUserID();

        if (getIntent().getStringExtra("type").equals("grp")) {
            selectcontact.setText("New Group");
        } else {
            selectcontact.setText("New Broadcast");

        }

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        chatappEntries = contactDB_sqlite.getSavedChatappContacts();
        Collections.sort(chatappEntries, Getcontactname.nameAscComparator);
        adapter = new SNGAdapter(SelectPeopleForGroupChat.this, filterContacts);

        if (chatappEntries.size() > 0) {

            for (ChatappContactModel item : chatappEntries) {
                String userId = item.get_id();
                if (!contactDB_sqlite.getBlockedMineStatus(userId, false).equals("1")) {
//                if (!contactsDB.getBlockedMineStatus(userId, false).equals("1")) {
                    if (item.getRequestStatus().equals("3")) {
                        filterContacts.add(item);
                    }
                }
            }

            Collections.sort(filterContacts, Getcontactname.nameAscComparator);
            chatappEntries.clear();
            chatappEntries.addAll(filterContacts);

            if (filterContacts.size() == 0) {
                no_text.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
            } else {
                no_text.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
            }

            lvContacts.setAdapter(adapter);
            lvContacts.setVisibility(View.VISIBLE);
            contact_empty_selectgroup.setVisibility(View.GONE);
            doneButton.setVisibility(View.VISIBLE);
        } else {
            lvContacts.setVisibility(View.GONE);
            contact_empty_selectgroup.setVisibility(View.VISIBLE);
            contact_empty_selectgroup.setText("No Contacts Available to form Group");
            doneButton.setVisibility(View.GONE);
        }

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backarrow.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
                backButton.setVisibility(View.GONE);
                selectcontact.setVisibility(View.GONE);
                selectcontactmember.setVisibility(View.GONE);

                etSearch.setVisibility(View.VISIBLE);
                etSearch.requestFocus();

                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                        try {
                            SelectPeopleForGroupChat.this.adapter.getFilter().filter(cs);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                        // TODO Auto-generated method stub
                    }

                    @Override
                    public void afterTextChanged(Editable arg0) {
                        // TODO Auto-generated method stub
                    }
                });
                backarrow.setVisibility(View.VISIBLE);
                backButton.setVisibility(View.GONE);

                backarrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etSearch.getText().clear();
                        adapter.updateInfo(filterContacts);
                        etSearch.setVisibility(View.GONE);
                        search.setVisibility(View.VISIBLE);
                        selectcontactmember.setVisibility(View.VISIBLE);
                        selectcontact.setVisibility(View.VISIBLE);
                        backarrow.setVisibility(View.GONE);
                        backButton.setVisibility(View.VISIBLE);

                        if (SelectPeopleForGroupChat.this.getCurrentFocus() != null) {
                            InputMethodManager inputMethodManager =
                                    (InputMethodManager) getApplicationContext().getSystemService(
                                            Activity.INPUT_METHOD_SERVICE);
                            inputMethodManager.hideSoftInputFromWindow(
                                    SelectPeopleForGroupChat.this.getCurrentFocus().getWindowToken(), 0);

                        }
                    }
                });

            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (member.size() > 0) {
                    if (getIntent().getStringExtra("type").equals("grp")) {
                        Intent intent = new Intent(SelectPeopleForGroupChat.this, GroupCreation.class);
                        intent.putStringArrayListExtra("mylist", member);
                        startActivity(intent);
                    } else {

                        String currentUserId = SessionManager.getInstance(SelectPeopleForGroupChat.this).getCurrentUserID();


//                        String respMsg = object.getString("message");
//                        String groupId = object.getString("groupId");
//                        String members = object.getString("groupMembers");
//                        String createdBy = object.getString("createdBy");
//                        String profilePic = object.getString("profilePic");
//                        String groupName = object.getString("groupName");
//                        String ts = object.getString("timeStamp");
//                        String admin = object.getString("admin");
//                        String id = object.getString("id");


                        String groupId = getSaltString();
                        String docId = currentUserId.concat("-").concat(groupId).concat("-g");

                        BroadcastInfoSession broadcastInfoSession = new BroadcastInfoSession(SelectPeopleForGroupChat.this);

                        BroadcastInfoPojo infoPojo = new BroadcastInfoPojo();
                        infoPojo.setBroadcastId(groupId);
                        infoPojo.setCreatedBy(currentUserId);
                        infoPojo.setAvatarPath("");
                        infoPojo.setContactsModel(chatappContactModels);

                        infoPojo.setBroadcastName(member.size() + " Receiptents");
                        infoPojo.setGroupMembers(android.text.TextUtils.join(",", member));
                        infoPojo.setAdminMembers(currentUserId);
                        infoPojo.setLiveGroup(true);
                        infoPojo.setbroadcast(true);
                        broadcastInfoSession.updateGroupInfo(docId, infoPojo);

                        finish();
                        startActivity(new Intent(SelectPeopleForGroupChat.this, BroadcastListActivity.class));


//                        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getApplicationContext());
//                        contactDB_sqlite.insert_BroadcastList(member.toString());
                    }

                } else {
                    Toast.makeText(SelectPeopleForGroupChat.this, "Please select atleast one member", Toast.LENGTH_LONG).show();
                }
            }
        });


        lvContacts.addOnItemTouchListener(new RItemAdapter(
                SelectPeopleForGroupChat.this, lvContacts, new RItemAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                ChatappContactModel selectedContact = adapter.getItem(position);


                performAddMember(view, selectedContact);

            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));

    }

    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

    }

    private void performAddMember(View view, ChatappContactModel selectedContact) {
        String userId = selectedContact.get_id();
        String name = selectedContact.getMsisdn();
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);

        if (!contactDB_sqlite.getBlockedStatus(userId, false).equals("1")) {
            addContactToGroup(view, selectedContact);
        } else {
            Getcontactname getcontactname = new Getcontactname(this);
            String message = "Unblock" + " " + getcontactname.getSendername(userId, name) + " to add group?";
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
                BlockUserUtils.changeUserBlockedStatus(SelectPeopleForGroupChat.this, EventBus.getDefault(),
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

    private void addContactToGroup(View view, ChatappContactModel selectedItem) {
        CheckBox c = (CheckBox) view.findViewById(R.id.selectedmember);

        LayoutInflater inflater = (LayoutInflater) SelectPeopleForGroupChat.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Map<String, String> myMAp = new HashMap<>();
        myMAp.clear();

        myMAp.put("receiverUid", selectedItem.getMsisdn());
        myMAp.put("receiverName", selectedItem.getFirstName());
        myMAp.put("documentId", selectedItem.get_id());
        myMAp.put("Username", selectedItem.getFirstName());
        myMAp.put("Image", selectedItem.getAvatarImageUrl());
        myMAp.put("Status", selectedItem.getStatus());
        myMAp.put("id", selectedItem.get_id());
        String memberId = selectedItem.get_id();
        if (memberId != null && !memberId.equals("")) {

            chatappContactModels.add(selectedItem);
            member.add(memberId);

            mylist.add(myMAp);
            if (member.size() > 0) {
                selectgroupmember.setVisibility(View.VISIBLE);
            } else {
                selectgroupmember.setVisibility(View.GONE);
            }


            final View view1 = inflater.inflate(R.layout.viewtoinflate, mainlayout, false);
            final CircleImageView image = (CircleImageView) view1.findViewById(R.id.image);
            final AvnNextLTProRegTextView phoneNumber = (AvnNextLTProRegTextView) view1.findViewById(R.id.phonenumber);

            phoneNumber.setText(selectedItem.getMsisdn());
            getcontactname.configProfilepic(image, selectedItem.get_id(), true, false, R.mipmap.chat_attachment_profile_default_image_frame);

            final AvnNextLTProRegTextView Selectedmemname = (AvnNextLTProRegTextView) view1.findViewById(R.id.selectedmembername);
            Selectedmemname.setText(selectedItem.getFirstName());
            ImageView removeicon = (ImageView) view1.findViewById(R.id.removeicon);
            image.setPadding(20, 0, 0, 0);
            removeicon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Log.d("called 2", "" + phoneNumber.getText().toString());


                    mainlayout.removeView(view1);


                    for (int i = 0; i < mylist.size(); i++) {

                        if (mylist.get(i).get("receiverUid").equals(phoneNumber.getText().toString())) {

                            ChatappContactModel contact = new ChatappContactModel();
                            contact.setMsisdn(mylist.get(i).get("receiverUid"));
//                            contact.setNumberInDevice(mylist.get(i).get("receiverUid"));
                            contact.setFirstName(mylist.get(i).get("receiverName"));
                            contact.setAvatarImageUrl(mylist.get(i).get("Image"));
                            contact.setStatus(mylist.get(i).get("Status"));
                            contact.set_id(mylist.get(i).get("id"));
                            filterContacts.add(0, contact);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });

                            mylist.remove(i);
                            member.remove(i);
                            break;
                        }

                    }


                    /* */

                }
            });


                    /*Glide.with(SelectPeopleForGroupChat.this).load(ChatappEntries.get(position).getAvatarImageUrl()).asBitmap()

                            .fitCenter().placeholder(R.mipmap.chat_attachment_profile_default_image_frame).
                            into(new BitmapImageViewTarget(image) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    image.setImageDrawable(circularBitmapDrawable);
                                }
                            });*/


            mainlayout.addView(view1);

            int index = filterContacts.indexOf(selectedItem);
            if (index > -1) {
                filterContacts.remove(index);
            }

            etSearch.setText("");
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {

        // // TODO: 1/3/2017

        getMenuInflater().inflate(R.menu.select_people_for_group, menu);
        MenuItem searchItem = menu.findItem(R.id.menuSearch);

        if (filterContacts != null && filterContacts.size() > 0) {

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
                            //closeKeypad();
                        }
                        adapter.getFilter().filter(newText);
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

            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            searchView.setIconifiedByDefault(true);
            searchView.setQuery("", false);
            searchView.clearFocus();
            searchView.setIconified(true);

            AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
            try {
                Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
                mCursorDrawableRes.setAccessible(true);
                mCursorDrawableRes.set(searchTextView, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onCreateOptionsMenu(menu);
        // return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(SelectPeopleForGroupChat.this).unregisterReceiver(mRegistrationBroadcastReceiver);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_PRIVACY_SETTINGS)) {
            loadPrivacySetting(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_BLOCK_USER)) {
            loadBlockUserMessage(event);
        }
    }

    private void loadPrivacySetting(ReceviceMessageEvent event) {
        adapter.notifyDataSetChanged();
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
}


