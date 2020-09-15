package com.chatapp.android.app;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.chatapp.android.R;
import com.chatapp.android.app.adapter.ForwardContactAdapter;
import com.chatapp.android.app.adapter.RItemAdapter;
import com.chatapp.android.app.contactlist.AddGroupChatAdapter;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.utils.GroupInfoSession;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.AudioMessage;
import com.chatapp.android.core.message.ContactMessage;
import com.chatapp.android.core.message.DocumentMessage;
import com.chatapp.android.core.message.LocationMessage;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.message.PictureMessage;
import com.chatapp.android.core.message.TextMessage;
import com.chatapp.android.core.message.VideoMessage;
import com.chatapp.android.core.message.WebLinkMessage;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.GroupInfoPojo;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.service.Constants;
import com.chatapp.android.core.service.ServiceRequest;
import com.chatapp.android.core.socket.SocketManager;
import com.chatapp.android.core.uploadtoserver.FileUploadDownloadManager;
import com.google.android.material.tabs.TabLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.appspot.apprtc.util.CryptLib;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

import id.zelory.compressor.Compressor;

/**
 *
 */
public class ForwardContact extends CoreActivity {
    ForwardContactAdapter adapter, frequentAdapter;
    InputMethodManager inputMethodManager;
    ProgressDialog dialog;
    //    TextView tvFrequentLbl;
    AvnNextLTProRegTextView resevernameforward;
    ImageView sendmessage;
    Session session;
    String mCurrentUserId, textMsgFromVendor;
    List<MessageItemChat> aSelectedMessageInfo;
    ArrayList<ChatappContactModel> ChatappEntries;
    ArrayList<ChatappContactModel> frequentList = new ArrayList<>();
    ArrayList<ChatappContactModel> grouplist = new ArrayList<>();
    ArrayList<String> userIdList = new ArrayList<>();
    Getcontactname getcontactname;
    ContactDB_Sqlite contactDB_sqlite;
    MenuItem searchItem1, searchItem2, searchItem3;
    String isExpiry = "1";
    private String mGroupId;
    //    String expireTime = "60";
//    String expireTime = "300";
    String expireTime;
    //    String expireTime = "3600";
    private RecyclerView rvListContacts, rvListGroups, rvFreqContact;
    private List<Map<String, String>> mylist = new ArrayList<>();
    private ArrayList<String> member = new ArrayList<>();
    private AddGroupChatAdapter addContactListAdapter;
    private TextView frequentContactEmpty, contactSearchEmpty, groupSearchEmpty;
    private SessionManager sessionManager;
    private List<ChatappContactModel> selectedContactsList;
    private ArrayList<ChatappContactModel> dataList;
    private FileUploadDownloadManager uploadDownloadManager;
    private SearchView searchView1, searchView2, searchView3;
    private RelativeLayout frequentContactLayout, contactSearchLayout, groupSearchLayout, Sendlayout;
    private boolean forwardFromChatapp;
    private String contact;
    private List<ChatappContactModel> contactListData = new ArrayList<ChatappContactModel>();
    private GroupInfoSession groupInfoSession;
    private UserInfoSession userInfoSession;
    private String searchKey = "";
    private int page = 0;
    private boolean isHasNextPage;
    private AlertDialog loaderDialog;
    private LinearLayout mainlayout;
    private HorizontalScrollView selectgroupmember;
    private int selectedTabPosition = 0;
    private SwipyRefreshLayout swipyRefreshLayout;


    /**
     * User contact list response
     */
    private ServiceRequest.ServiceListener resultListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            try {
                showLoaderDialog(false);
                JSONObject jsonObject = new JSONObject(response);
                System.out.println("Contact--->" + response);

                isHasNextPage = jsonObject.getBoolean("isNext");

                if (jsonObject.getInt("Status") == 1) {
                    if (isHasNextPage) {
                        page++;
                        swipyRefreshLayout.setRefreshing(true);
                    } else {
                        swipyRefreshLayout.setRefreshing(false);
                    }

                    ChatappContactModel contactSearchModel = null;
                    JSONArray jArray = (JSONArray) jsonObject.getJSONArray("users");
                    if (jArray != null) {
                        for (int i = 0; i < jArray.length(); i++) {
                            contactSearchModel = new ChatappContactModel();
//                            System.out.println("ContactList--->" + jArray.getJSONObject(i).getJSONObject("Privacy").getString("status"));
                            contactSearchModel.set_id(jArray.getJSONObject(i).getString("id"));
                            contactSearchModel.setMsisdn(jArray.getJSONObject(i).getString("msisdn"));
                            contactSearchModel.setFirstName(jArray.getJSONObject(i).getString("Name"));
                            contactSearchModel.setRequestStatus(String.valueOf(jArray.getJSONObject(i).getInt("RequestStatus")));
                            contactSearchModel.setBlockStatus(jArray.getJSONObject(i).getString("IsBlocked"));
                            contactSearchModel.setFavouriteStatus(jArray.getJSONObject(i).getInt("Fav_type"));
                            contactSearchModel.setAvatarImageUrl(jArray.getJSONObject(i).getString("AvatarImageUrl"));
                            contactSearchModel.setStatus(jArray.getJSONObject(i).getString("Status"));

                            if (member.size() > 0) {
                                if (!member.contains(jArray.getJSONObject(i).getString("id")))
                                    contactListData.add(contactSearchModel);
                            } else {
                                contactListData.add(contactSearchModel);
                            }

                        }
                    }
                    rvListContacts.post(new Runnable() {
                        @Override
                        public void run() {
                            rvListContacts.scrollToPosition(adapter.getItemCount() - 1);
                            // Here adapter.getItemCount()== child count
                        }
                    });

                    if (frequentList.size() > 0) {
                        for (int i = 0; i < frequentList.size(); i++) {
                            for (int j = 0; j < contactListData.size(); j++) {
                                if (frequentList.get(i).get_id().equalsIgnoreCase(contactListData.get(j).get_id())) {
                                    contactListData.remove(contactListData.get(i));
                                    Log.e("SIZE", contactListData.size() + "" + contactListData.get(i).getFirstName());
                                } else {
                                    Log.e("ADDDD", contactListData.get(i).getFirstName());
                                }
                            }
                        }
                    }

                    addContactListAdapter = new AddGroupChatAdapter(ForwardContact.this, contactListData);
                    rvListContacts.setAdapter(addContactListAdapter);
                    rvListContacts.setVisibility(View.VISIBLE);
                    contactSearchEmpty.setVisibility(View.GONE);
                    swipyRefreshLayout.setRefreshing(false);
                    if (contactListData.size() > 2)
                        rvListContacts.scrollToPosition(contactListData.size() - 1);
                } else if (jsonObject.getInt("Status") == -1) {
                    rvListContacts.setVisibility(View.GONE);
                    swipyRefreshLayout.setRefreshing(false);
                    contactSearchEmpty.setText(jsonObject.getString("Message"));
                } else {
                    swipyRefreshLayout.setRefreshing(false);
                    Toast.makeText(ForwardContact.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showLoaderDialog(false);
                swipyRefreshLayout.setRefreshing(false);
                Toast.makeText(ForwardContact.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onErrorListener(int state) {
            showLoaderDialog(false);
        }
    };

    /**
     * binding Progress Dialog
     */
    public void setupProgressDialog() {

        int llPadding = 30;
        LinearLayout ll = new LinearLayout(this);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setPadding(llPadding, llPadding, llPadding, llPadding);
        ll.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams llParam = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        ll.setLayoutParams(llParam);

        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        progressBar.setPadding(0, 0, llPadding, 0);
        progressBar.setLayoutParams(llParam);

        llParam = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        llParam.gravity = Gravity.CENTER;
        TextView tvText = new TextView(this);
        tvText.setText("Please wait searching contacts...");
//        tvText.setTextColor(Color.parseColor("#000000"));
//        tvText.setTextSize(16);
        tvText.setLayoutParams(llParam);

        ll.addView(progressBar);
        ll.addView(tvText);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(ll);

        loaderDialog = builder.create();

        Window window = loaderDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(loaderDialog.getWindow().getAttributes());
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            loaderDialog.getWindow().setAttributes(layoutParams);
        }

    }

    /**
     * visible ProgressDialog
     *
     * @param show based on value ProgressDialog dismiss or open view state
     */
    private void showLoaderDialog(boolean show) {

        if (show)
            loaderDialog.show();
        else {
            if (loaderDialog.isShowing()) {
                loaderDialog.dismiss();
            }
        }
    }

    /**
     * Dynamic view
     *
     * @param relativeLayouts set child view
     */
    private void setLayoutVisibility(RelativeLayout... relativeLayouts) {

        for (RelativeLayout view : relativeLayouts) {
            view.setVisibility(View.GONE);
        }


    }

    /**
     * Menu visibility
     *
     * @param menuItems specific menu visibility
     */
    private void setMenuVisibility(MenuItem... menuItems) {


        for (MenuItem menuItem : menuItems) {
            menuItem.setVisible(false);
        }

    }

    /**
     * handling the data view for recent contact, contact list and group. share the data to select single or group
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forward_with_freq_contact);
        frequentContactLayout = findViewById(R.id.frequent_contact_layout);
        swipyRefreshLayout = findViewById(R.id.swipyrefreshlayout);
        swipyRefreshLayout.setColorSchemeResources(R.color.tabFourColor);
        contactSearchLayout = findViewById(R.id.contact_search_layout);
        groupSearchLayout = findViewById(R.id.group_search_layout);
        mainlayout = (LinearLayout) findViewById(R.id.maincontainer);
        selectgroupmember = (HorizontalScrollView) findViewById(R.id.selectgroupmember);
        contactDB_sqlite = CoreController.getContactSqliteDBintstance(ForwardContact.this);
        userInfoSession = new UserInfoSession(this);
        frequentContactEmpty = findViewById(R.id.frequent_contact_empty);
        contactSearchEmpty = findViewById(R.id.contact_search_empty);
        groupSearchEmpty = findViewById(R.id.group_search_empty);
//        tvFrequentLbl = (TextView) findViewById(R.id.tvFrequentLbl);
        sendmessage = (ImageView) findViewById(R.id.overlapImage);
        resevernameforward = (AvnNextLTProRegTextView) findViewById(R.id.chat_text_view);
        uploadDownloadManager = new FileUploadDownloadManager(ForwardContact.this);
        groupInfoSession = new GroupInfoSession(ForwardContact.this);
        getcontactname = new Getcontactname(ForwardContact.this);
        mCurrentUserId = SessionManager.getInstance(this).getCurrentUserID();
        expireTime = SessionManager.getInstance(this).getAutoDeleteTime();
        session = new Session(ForwardContact.this);
        //getviewdata();
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);


        /**
         * based on tab view shown specific data
         */
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tabLayout.getSelectedTabPosition() == 0) {
                    frequentContactLayout.setVisibility(View.VISIBLE);
                    setLayoutVisibility(contactSearchLayout, groupSearchLayout);
                    searchItem1.setVisible(true);
                    setMenuVisibility(searchItem2, searchItem3);
                } else if (tabLayout.getSelectedTabPosition() == 1) {
                    contactSearchLayout.setVisibility(View.VISIBLE);
                    setLayoutVisibility(frequentContactLayout, groupSearchLayout);
                    searchItem2.setVisible(true);
                    setMenuVisibility(searchItem1, searchItem3);
                    FavListCall();
//                    invalidateOptionsMenu();
                } else {
                    groupSearchLayout.setVisibility(View.VISIBLE);
                    setLayoutVisibility(contactSearchLayout, frequentContactLayout);
                    searchItem3.setVisible(true);
                    setMenuVisibility(searchItem1, searchItem2);
//                    invalidateOptionsMenu();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        setTitle("Forward to..");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        rvListContacts = (RecyclerView) findViewById(R.id.listContacts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ForwardContact.this, LinearLayoutManager.VERTICAL, false);
        rvListContacts.setLayoutManager(layoutManager);


        rvListGroups = (RecyclerView) findViewById(R.id.listGroups);
        LinearLayoutManager mediaManager = new LinearLayoutManager(ForwardContact.this, LinearLayoutManager.VERTICAL, false);
        rvListGroups.setLayoutManager(mediaManager);

        rvFreqContact = (RecyclerView) findViewById(R.id.rvFreqContact);
        LinearLayoutManager freqManager = new LinearLayoutManager(ForwardContact.this, LinearLayoutManager.VERTICAL, false);
        rvFreqContact.setLayoutManager(freqManager);


        List<String> lists = session.getBlockedIds();

        final ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        MessageDbController db = CoreController.getDBInstance(ForwardContact.this);
        ChatappEntries = contactDB_sqlite.getLinkedChatappContacts();
        List<ChatappContactModel> frequentContact = contactDB_sqlite.getFrequentContacts(this, mCurrentUserId);

        //grouplist
        ArrayList<MessageItemChat> groupChats = db.selectChatList(MessageFactory.CHAT_TYPE_GROUP);

        Log.d("getDataFromDB", "" + "  __ " + grouplist.size() + "  __ " + groupChats.size());

        for (MessageItemChat msgItem : groupChats) {
//            getGroupDetails(msgItem.getConvId());
//            Log.d("getDataFromDB", msgItem.getBroadcastName());
            String groupId = msgItem.getReceiverID();
            String docIdd = mCurrentUserId + "-" + groupId + "-g";
            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docIdd);

            if (lists != null && lists.size() != 0) {
                if (!lists.contains(msgItem.getReceiverID())) {
                    if (infoPojo.getGroupName() != null) {
                        StringBuilder sb = null;
                        ChatappContactModel vChatappContactModel = new ChatappContactModel();
                        String groupid = msgItem.getReceiverID().split("-")[0];
                        vChatappContactModel.set_id(groupid);
                        vChatappContactModel.setType(msgItem.getGroupEventType());
                        vChatappContactModel.setAvatarImageUrl(infoPojo.getAvatarPath());
                        vChatappContactModel.setFirstName(infoPojo.getGroupName());
                        String from = msgItem.getMessageId().split("-")[0];
                        String to = msgItem.getReceiverID();
                        String docId = from + "-" + to + "-g";
                        boolean hasGroupInfo = groupInfoSession.hasGroupInfo(docId);

                        if (hasGroupInfo) {
                            infoPojo = groupInfoSession.getGroupInfo(docId);
                            sb = new StringBuilder();
                            String memername = "";
                            if (infoPojo != null && infoPojo.getGroupMembers() != null) {
                                String[] contacts = infoPojo.getGroupMembers().split(",");

                                for (int i = 0; i < contacts.length; i++) {
                                    if (!contacts[i].equalsIgnoreCase(from)) {
                                        ChatappContactModel info = contactDB_sqlite.getUserOpponenetDetails(contacts[i]);
                                        if (info != null) {
                                            memername = info.getFirstName();
                                            //  memername = getcontactname.getSendername(contacts[i], info.getMsisdn());
                                            sb.append(memername);
                                            if (contacts.length - 1 != i) {
                                                sb.append(", ");
                                            }
                                        }
                                    } else {
                                        memername = "You";
                                        sb.append(memername);
                                        if (contacts.length - 1 != i) {
                                            sb.append(", ");
                                        }
                                    }
                                }
                            }
                        }

                        vChatappContactModel.setStatus(String.valueOf(sb));
                        grouplist.add(vChatappContactModel);
                    }
                }
            } else {
                if (infoPojo.getGroupName() != null) {
                    StringBuilder sb = null;
                    ChatappContactModel vChatappContactModel = new ChatappContactModel();
                    String groupid = msgItem.getReceiverID().split("-")[0];
                    vChatappContactModel.set_id(groupid);
                    vChatappContactModel.setType("");
                    vChatappContactModel.setAvatarImageUrl(infoPojo.getAvatarPath());
                    vChatappContactModel.setFirstName(infoPojo.getGroupName());
                    vChatappContactModel.setGroup(true);
                    String from = msgItem.getMessageId().split("-")[0];
                    String to = msgItem.getReceiverID();
                    String docId = from + "-" + to + "-g";

                    boolean hasGroupInfo = groupInfoSession.hasGroupInfo(docId);

                    if (hasGroupInfo) {

                        infoPojo = groupInfoSession.getGroupInfo(docId);
                        sb = new StringBuilder();
                        String memername = "";
                        if (infoPojo != null && infoPojo.getGroupMembers() != null) {
                            String[] contacts = infoPojo.getGroupMembers().split(",");

                            for (int i = 0; i < contacts.length; i++) {
                                if (!contacts[i].equalsIgnoreCase(from)) {
                                    ChatappContactModel info = contactDB_sqlite.getUserOpponenetDetails(contacts[i]);
                                    if (info != null) {
                                        memername = info.getFirstName();

                                        //   memername = getcontactname.getSendername(contacts[i], info.getMsisdn());
                                        sb.append(memername);
                                        if (contacts.length - 1 != i) {
                                            sb.append(", ");
                                        }
                                    }
                                } else {
                                    memername = "You";
                                    sb.append(memername);
                                    if (contacts.length - 1 != i) {
                                        sb.append(", ");
                                    }
                                }
                            }
                        }
                    }

                    vChatappContactModel.setStatus(String.valueOf(sb));
                    grouplist.add(vChatappContactModel);
                }
            }
        }
//

        if (frequentContact.size() > 0) {
            frequentContactEmpty.setVisibility(View.GONE);
            for (ChatappContactModel data : frequentContact) {
                frequentList.add(data);
                userIdList.add(data.get_id());
            }
            frequentAdapter = new ForwardContactAdapter(this, frequentList);
            rvFreqContact.setAdapter(frequentAdapter);
            frequentAdapter.notifyDataSetChanged();

        } else {
            frequentContactEmpty.setVisibility(View.VISIBLE);
        }

        dataList = new ArrayList<>();
        if (lists != null && lists.size() != 0) {
            for (ChatappContactModel contact : ChatappEntries) {
                if (!lists.contains(contact.get_id()) && !userIdList.contains(contact.get_id())) {
                    dataList.add(contact);
                }
            }

            for (ChatappContactModel contact : grouplist) {
                if (!userIdList.contains(contact.get_id())) {
                    Log.e("GrpId", contact.get_id() + " Status-->" + contact.getStatus());
                    if (contact.getStatus().contains("You")) {
                        dataList.add(contact);
                    } else {

                    }

                }
            }

        } else {
            for (ChatappContactModel contact : ChatappEntries) {
                if (!userIdList.contains(contact.get_id())) {
                    dataList.add(contact);
                }
            }

            for (ChatappContactModel contact : grouplist) {
                if (!userIdList.contains(contact.get_id())) {
                    Log.e("GrpId", contact.get_id() + " Status-->" + contact.getStatus());
                    if (contact.getStatus().contains("You")) {
                        dataList.add(contact);
                    } else {

                    }

                }
            }
        }


        if (dataList.size() > 0) {
            groupSearchEmpty.setVisibility(View.GONE);
        } else {
            groupSearchEmpty.setVisibility(View.VISIBLE);
        }
        Collections.sort(dataList, Getcontactname.nameAscComparator);
        adapter = new ForwardContactAdapter(ForwardContact.this, dataList);
        rvListGroups.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        rvListContacts.addOnItemTouchListener(new RItemAdapter(
                ForwardContact.this, rvListContacts, new RItemAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                if (position > -1) {
                    int index = contactListData.indexOf(addContactListAdapter.getItem(position));
                    ChatappContactModel userData = contactListData.get(index);
                    userData.setSelected(!userData.isSelected());
                    if (contactDB_sqlite.getBlockedStatus(userData.get_id(), false).equals("0")) {
                        if (userData.isSelected()) {
                            getcontactname.userDetails(userData.get_id(), userData);
                            addContactToGroup(view, userData);
                        } else {

                        }

                       /* if (userData.isSelected()) {
                            selectedContactsList.add(userData);
                        } else {
                            if (selectedContactsList.contains(userData)) {
                                selectedContactsList.remove(userData);
                            }
                        }*/
                    } else {
                        toast("First Unblock to select this contact");
                    }


                    // contactListData.set(index, userData);
                    addContactListAdapter.notifyDataSetChanged();

                    if (selectedContactsList.size() == 0) {
                        Sendlayout.setVisibility(View.GONE);
                        sendmessage.setVisibility(View.GONE);
                    } else {

                        Sendlayout.setVisibility(View.VISIBLE);
                        sendmessage.setVisibility(View.VISIBLE);

                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.bottom_up);
                        Sendlayout.setAnimation(animation);

                        StringBuilder sb = new StringBuilder();
                        int nameIndex = 0;
                        for (ChatappContactModel contact : selectedContactsList) {
                            sb.append(contact.getFirstName());
                            nameIndex++;
                            if (selectedContactsList.size() > nameIndex) {
                                sb.append(", ");
                            }
                        }

                        resevernameforward.setText(sb);
                        //getviewdata();
                    }
                }

            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));

        rvListGroups.addOnItemTouchListener(new RItemAdapter(this, rvListGroups, new RItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > -1) {
                    int index = dataList.indexOf(adapter.getItem(position));
                    ChatappContactModel userData = dataList.get(index);
                    userData.setSelected(!userData.isSelected());
                    if (contactDB_sqlite.getBlockedStatus(userData.get_id(), false).equals("0")) {
                        if (userData.isSelected()) {
                            selectedContactsList.add(userData);
                        } else {
                            if (selectedContactsList.contains(userData)) {
                                selectedContactsList.remove(userData);
                            }
                        }
                    } else {
                        toast("First Unblock to select this contact");
                    }


                    dataList.set(index, userData);
                    adapter.notifyDataSetChanged();

                    if (selectedContactsList.size() == 0) {
                        Sendlayout.setVisibility(View.GONE);
                        sendmessage.setVisibility(View.GONE);
                    } else {

                        Sendlayout.setVisibility(View.VISIBLE);
                        sendmessage.setVisibility(View.VISIBLE);

                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.bottom_up);
                        Sendlayout.setAnimation(animation);

                        StringBuilder sb = new StringBuilder();
                        int nameIndex = 0;
                        for (ChatappContactModel contact : selectedContactsList) {
                            sb.append(contact.getFirstName());
                            nameIndex++;
                            if (selectedContactsList.size() > nameIndex) {
                                sb.append(", ");
                            }
                        }

                        resevernameforward.setText(sb);

                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        rvFreqContact.addOnItemTouchListener(new RItemAdapter(this, rvFreqContact, new RItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > -1) {
                    int index = frequentList.indexOf(frequentAdapter.getItem(position));
                    ChatappContactModel userData = frequentList.get(index);

                    if (!member.contains(userData.get_id())) {
                        userData.setSelected(!userData.isSelected());
                    } else {
                        userData.setSelected(false);
                    }

                    if (contactDB_sqlite.getBlockedStatus(userData.get_id(), false).equals("0")) {
                        if (userData.isSelected()) {
                            if (!member.contains(userData.get_id())) {
                                selectedContactsList.add(userData);
                                member.add(userData.get_id());
                                frequentAdapter.notifyDataSetChanged();
                            } else {
                                if (selectedContactsList.contains(userData)) {
                                    selectedContactsList.remove(userData);
                                    frequentAdapter.notifyDataSetChanged();
                                }
                            }

                        } else {
                            if (selectedContactsList.contains(userData)) {
                                selectedContactsList.remove(userData);
                                member.remove(userData.get_id());
                                userData.setSelected(false);
                            }
                        }
                    } else {
                        toast("First Unblock to select this contact");
                    }

                    frequentList.set(index, userData);
                    frequentAdapter.notifyDataSetChanged();

                    if (selectedContactsList.size() == 0) {
                        Sendlayout.setVisibility(View.GONE);
                        sendmessage.setVisibility(View.GONE);
                    } else {

                        Sendlayout.setVisibility(View.VISIBLE);
                        sendmessage.setVisibility(View.VISIBLE);

                        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                                R.anim.bottom_up);
                        Sendlayout.setAnimation(animation);

                        StringBuilder sb = new StringBuilder();
                        int nameIndex = 0;
                        for (ChatappContactModel contact : selectedContactsList) {
                            sb.append(contact.getFirstName());
                            nameIndex++;
                            if (selectedContactsList.size() > nameIndex) {
                                sb.append(",");
                            }
                        }

                        resevernameforward.setText(sb);
                    }
                } else {

                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));
        selectedContactsList = new ArrayList<>();

        final Intent intent = getIntent();

        String shareAction = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(shareAction) && type != null) {
            if ("text/plain".equals(type)) {
                textMsgFromVendor = intent.getExtras().getString(Intent.EXTRA_TEXT, "");
            }
        }

        Sendlayout = (RelativeLayout) findViewById(R.id.sendlayout);

        forwardFromChatapp = getIntent().getBooleanExtra("FromChatapp", false);

        if (getIntent() != null) {
            aSelectedMessageInfo = (List<MessageItemChat>) intent.getSerializableExtra("MsgItemList");
        }

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int msgIndex = 0; msgIndex < aSelectedMessageInfo.size(); msgIndex++) {

                    MessageItemChat msgItem = aSelectedMessageInfo.get(msgIndex);

                    switch (msgItem.getMessageType()) {

                        case (MessageFactory.text + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappContactModel userData = selectedContactsList.get(contactIndex);

                                SendMessageEvent messageEvent = new SendMessageEvent();
                                TextMessage message = (TextMessage) MessageFactory.getMessage(MessageFactory.text, ForwardContact.this);

                                JSONObject msgObj;
                                if (userData.isGroup()) {
                                    String data = msgItem.getTextMessage();
                                    CryptLib cryptLib = null;
                                    try {
                                        cryptLib = new CryptLib();
                                        data = cryptLib.encryptPlainTextWithRandomIV(data, kyGn(userData.get_id()));

                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    } catch (NoSuchPaddingException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    messageEvent.setEventName(SocketManager.EVENT_GROUP);
                                    msgObj = (JSONObject) message.getGroupMessageObject(userData.get_id(), data, userData.getFirstName());

                                    try {
                                        msgObj.put("groupType", SocketManager.ACTION_EVENT_GROUP_MESSAGE);
                                        msgObj.put("userName", userData.getFirstName());
                                        msgObj.put("is_expiry", isExpiry);
                                        msgObj.put("expiry_time", expireTime);
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                    messageEvent.setMessageObject(msgObj);
                                    MessageItemChat item = message.createMessageItem(true, data, MessageFactory.DELIVERY_STATUS_NOT_SENT, userData.get_id(), userData.getFirstName(), isExpiry, expireTime);
                                    item.setGroupName(userData.getFirstName());
                                    item.setGroup(true);
                                    MessageDbController db = CoreController.getDBInstance(ForwardContact.this);
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);


                                } else {
                                    String docid = "";

                                    String data = msgItem.getTextMessage();
                                    CryptLib cryptLib = null;
                                    try {
                                        cryptLib = new CryptLib();
                                        docid = SessionManager.getInstance(getApplicationContext()).getCurrentUserID() + "-" + userData.get_id();
                                        if (userInfoSession.hasChatConvId(docid)) {
                                            data = cryptLib.encryptPlainTextWithRandomIV(data, kyGn(userInfoSession.getChatConvId(docid)));
                                        }

                                    } catch (NoSuchAlgorithmException e) {
                                        e.printStackTrace();
                                    } catch (NoSuchPaddingException e) {
                                        e.printStackTrace();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }


                                    msgObj = (JSONObject) message.getMessageObject(userData.get_id(), data, false);
                                    messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
                                    messageEvent.setMessageObject(msgObj);

                                    MessageItemChat item = message.createMessageItem(true, data, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                            userData.get_id(), userData.getFirstName(), isExpiry, expireTime);

                                    item.setGroup(false);
                                    if (userInfoSession.hasChatConvId(docid)) {
                                        item.setConvId(userInfoSession.getChatConvId(docid));
                                    }
                                    item.setSenderMsisdn(userData.getNumberInDevice());
                                    item.setSenderName(userData.getFirstName());

                                    MessageDbController db = CoreController.getDBInstance(ForwardContact.this);
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                                }

                                EventBus.getDefault().post(messageEvent);
                            }

                            break;

                        case (MessageFactory.contact + ""):
                            //   ArrayList<String> selected_data = getIntent().getStringArrayListExtra("message");
                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappContactModel userData = selectedContactsList.get(contactIndex);
                                SendMessageEvent messageEvent = new SendMessageEvent();
                                ContactMessage message = (ContactMessage) MessageFactory.getMessage(MessageFactory.contact, ForwardContact.this);

                                String contactName = msgItem.getContactName();
                                String contactNumber = msgItem.getContactNumber();
                                String contactChatappId = msgItem.getContactChatappId();
                                String contactDetails = msgItem.getDetailedContacts();

                                JSONObject msgObj;
                                if (userData.isGroup()) {
                                    messageEvent.setEventName(SocketManager.EVENT_GROUP);
                                    msgObj = (JSONObject) message.getGroupMessageObject(userData.get_id(), "", userData.getFirstName(), contactChatappId, contactName, contactNumber, contactDetails);
                                    try {
                                        msgObj.put("is_expiry", isExpiry);
                                        msgObj.put("expiry_time", expireTime);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
                                    msgObj = (JSONObject) message.getMessageObject(userData.get_id(), "", contactChatappId, contactName, contactNumber, contactDetails, false);
                                }

                                messageEvent.setMessageObject(msgObj);
                                MessageItemChat item = message.createMessageItem(true, "", MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName(), contactName, contactNumber, contactChatappId, contactDetails);
                                item.setGroupName(userData.getFirstName());
                                item.setSenderMsisdn(userData.getNumberInDevice());
//                                item.setSenderName(userData.getFirstName());
                                item.setAvatarImageUrl(userData.getAvatarImageUrl());
                                item.setDetailedContacts(contactDetails);
                                item.setContactchatappId(contactChatappId);
                                MessageDbController db = CoreController.getDBInstance(ForwardContact.this);

                                if (userData.isGroup()) {
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                                } else {
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                                }

                                EventBus.getDefault().post(messageEvent);
                            }
                            break;

                        case (MessageFactory.audio + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappContactModel userData = selectedContactsList.get(contactIndex);

                                String filePath = msgItem.getChatFileLocalPath();
                                String duration = msgItem.getDuration();
//
//                                String from = msgItem.getMessageId().split("-")[0];
//                                String to = msgItem.getReceiverID();
//                                String docId = from + "-" + to + "-g";
//
//                                Log.d("DocumentID",docId+" __ "+from+" __ "+to);
//
//                                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
//
//                                AudioMessage message = (AudioMessage) MessageFactory.getMessage(MessageFactory.audio, ForwardContact.this);
//                                MessageItemChat item = null;
//                                boolean canSent = false;
//
//                                String chatType = null;
//                                if (userData.isGroup()) {
//                                    boolean enableGroupChat = infoPojo.isLiveGroup();
//                                    if (enableGroupChat) {
//                                        chatType = MessageFactory.CHAT_TYPE_GROUP;
//                                        canSent = true;
//                                        message.getGroupMessageObject(to, filePath, userData.getFirstName());
//                                        item = message.createMessageItem(true, filePath, duration, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                                                userData.get_id(), userData.getFirstName(), msgItem.getaudiotype());
//                                        item.setBroadcastName(userData.getFirstName());
//                                    } else {
//                                        Toast.makeText(ForwardContact.this, "You are not a member in this group", Toast.LENGTH_SHORT).show();
//                                    }
//                                } else {
//                                    chatType = MessageFactory.CHAT_TYPE_SINGLE;
//                                    canSent = true;
//                                    message.getMessageObject(to, filePath, false);
//                                    item = message.createMessageItem(true, filePath, duration, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                                            userData.get_id(), userData.getFirstName(),  msgItem.getaudiotype());
//                                    item.setSenderMsisdn(userData.getNumberInDevice());
//                                    item.setaudiotype(msgItem.getaudiotype());
//                                }
//
//                                if (canSent) {
//                                    MessageDbController db = CoreController.getDBInstance(ForwardContact.this);
//                                    db.updateChatMessage(item, chatType);
//
//                                    String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(filePath);
//                                    String audioName = item.getMessageId() + fileExtension;
//
//                                    JSONObject uploadObj;
//                                    if (userData.isGroup()) {
//                                        docId = docId;
//                                    } else {
//                                        docId = mCurrentUserId + "-" + to;
//                                    }
//                                    uploadObj = (JSONObject) message.createAudioUploadObject(item.getMessageId(), docId,
//                                            audioName, filePath, duration, userData.getFirstName(), msgItem.getaudiotype(), chatType, false);
//                                    uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
//
//                                }

                                AudioMessage message = (AudioMessage) MessageFactory.getMessage(MessageFactory.audio, ForwardContact.this);
                                if (userData.isGroup()) {
                                    message.getGroupMessageObject(userData.get_id(), filePath, userData.getFirstName());
                                } else {
                                    message.getMessageObject(userData.get_id(), filePath, false);
                                }

                                MessageItemChat item = message.createMessageItem(true, filePath, duration, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName(), msgItem.getaudiotype(), isExpiry, expireTime);

                                String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(filePath);
                                String audioName = item.getMessageId() + fileExtension;

                                String docId;
                                if (userData.isGroup()) {

                                    docId = mCurrentUserId + "-" + userData.get_id() + "-g";
                                } else {
                                    docId = mCurrentUserId + "-" + userData.get_id();
                                }

                                JSONObject uploadObj;
                                if (userData.isGroup()) {
                                    item.setGroup(true);
                                    item.setGroupName(userData.getFirstName());
                                    uploadObj = (JSONObject) message.createAudioUploadObject(item.getMessageId(), docId, audioName, filePath,
                                            duration, userData.getFirstName(), msgItem.getaudiotype(), MessageFactory.CHAT_TYPE_GROUP, false);
                                    try {
                                        uploadObj.put("is_expiry", isExpiry);
                                        uploadObj.put("expiry_time", expireTime);
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                } else {
                                    item.setGroup(false);
                                    if (userInfoSession.hasChatConvId(docId)) {
                                        item.setConvId(userInfoSession.getChatConvId(docId));
                                    }


                                    uploadObj = (JSONObject) message.createAudioUploadObject(item.getMessageId(), docId, audioName, filePath,
                                            duration, userData.getFirstName(), msgItem.getaudiotype(), MessageFactory.CHAT_TYPE_SINGLE, false);
                                }

                                uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                item.setaudiotype(msgItem.getaudiotype());

                                MessageDbController db = CoreController.getDBInstance(ForwardContact.this);
                                if (userData.isGroup()) {
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                                } else {
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                                }

                            }
                            break;

                        case (MessageFactory.video + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappContactModel userData = selectedContactsList.get(contactIndex);

                                String videoPath = msgItem.getChatFileLocalPath();

                                VideoMessage message = (VideoMessage) MessageFactory.getMessage(MessageFactory.video, ForwardContact.this);
                                if (userData.isGroup()) {
                                    message.getGroupMessageObject(userData.get_id(), videoPath, userData.getFirstName());

                                } else {
                                    message.getMessageObject(userData.get_id(), videoPath, false);
                                }

                                MessageItemChat item = message.createMessageItem(true, videoPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName(), "", isExpiry, expireTime);


//                                Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
//                                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                                thumbBmp.compress(Bitmap.CompressFormat.PNG, 10, out);
//                                byte[] thumbArray = out.toByteArray();
//                                try {
//                                    out.close();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                                String thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
//                                if (thumbData != null) {
//                                    item.setThumbnailData(thumbData);
//                                }

                                String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(videoPath);
                                String videoName = item.getMessageId() + fileExtension;

                                JSONObject uploadObj;
                                String docId;
                                if (userData.isGroup()) {
                                    item.setGroupName(userData.getFirstName());
                                    item.setGroup(true);
                                    docId = mCurrentUserId + "-" + userData.get_id() + "-g";
                                    item.setVideoPath(videoPath);
                                    uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
                                            videoName, videoPath, userData.getFirstName(), "", MessageFactory.CHAT_TYPE_GROUP, false);
                                    try {
                                        uploadObj.put("is_expiry", isExpiry);
                                        uploadObj.put("expiry_time", expireTime);
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                    try {
                                        String thumbdata = getVideoThumbnail(videoPath, userData.get_id());
                                        uploadObj.put("thumbnail_data", thumbdata);
                                        item.setThumbnailData(thumbdata);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                } else {
                                    item.setGroup(false);
                                    docId = mCurrentUserId + "-" + userData.get_id();

                                    uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
                                            videoName, videoPath, userData.getFirstName(), "", MessageFactory.CHAT_TYPE_SINGLE, false);

                                    try {
                                        if (userInfoSession.hasChatConvId(docId)) {
                                            item.setConvId(userInfoSession.getChatConvId(docId));
                                            String thumbdata = getVideoThumbnail(videoPath, item.getConvId());
                                            uploadObj.put("thumbnail_data", thumbdata);
                                            item.setThumbnailData(thumbdata);

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }


                                uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                MessageDbController db = CoreController.getDBInstance(ForwardContact.this);
                                if (userData.isGroup()) {
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                                } else {
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                                }
                            }
                            break;

                        case (MessageFactory.picture + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappContactModel userData = selectedContactsList.get(contactIndex);

                                String imgPath = msgItem.getChatFileLocalPath();
                                PictureMessage message = (PictureMessage) MessageFactory.getMessage(MessageFactory.picture, ForwardContact.this);

                                if (userData.isGroup()) {
                                    message.getGroupMessageObject(userData.get_id(), imgPath, userData.getFirstName());
                                } else {
                                    message.getMessageObject(userData.get_id(), imgPath, false);
                                }

                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(imgPath, options);
                                int imageHeight = options.outHeight;
                                int imageWidth = options.outWidth;

                                MessageItemChat item = message.createMessageItem(true, "", imgPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName(), imageWidth, imageHeight, isExpiry, expireTime);

                                String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(imgPath);
                                String imgName = item.getMessageId() + fileExtension;

                                String docId;
                                JSONObject uploadObj;
                                if (userData.isGroup()) {
                                    item.setGroup(true);
                                    item.setGroupName(userData.getFirstName());
                                    docId = mCurrentUserId + "-" + userData.get_id() + "-g";
                                    uploadObj = (JSONObject) message.createImageUploadObject(item.getMessageId(), docId,
                                            imgName, imgPath, userData.getFirstName(), "", MessageFactory.CHAT_TYPE_GROUP, false);
                                    try {
                                        try {
                                            uploadObj.put("is_expiry", isExpiry);
                                            uploadObj.put("expiry_time", expireTime);
                                        } catch (JSONException ex) {
                                            ex.printStackTrace();
                                        }
                                        String thumbdata = getThumbnailData(imgPath, userData.get_id());
                                        uploadObj.put("thumbnail_data", thumbdata);
                                        item.setThumbnailData(thumbdata);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    item.setImagePath(imgPath);
                                } else {
                                    item.setGroup(false);
                                    docId = mCurrentUserId + "-" + userData.get_id();


                                    uploadObj = (JSONObject) message.createImageUploadObject(item.getMessageId(), docId,
                                            imgName, imgPath, userData.getFirstName(), "", MessageFactory.CHAT_TYPE_SINGLE, false);

                                    try {
                                        if (userInfoSession.hasChatConvId(docId)) {
                                            item.setConvId(userInfoSession.getChatConvId(docId));
                                            String thumbdata = getThumbnailData(imgPath, item.getConvId());
                                            uploadObj.put("thumbnail_data", thumbdata);
                                            item.setThumbnailData(thumbdata);

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    item.setImagePath(imgPath);
                                }

                                uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);

                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                MessageDbController db = CoreController.getDBInstance(ForwardContact.this);
                                if (userData.isGroup()) {
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                                } else {
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                                }

                            }
                            break;

                        case (MessageFactory.document + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappContactModel userData = selectedContactsList.get(contactIndex);

                                String filePath = msgItem.getChatFileLocalPath();

                                DocumentMessage message = (DocumentMessage) MessageFactory.getMessage(MessageFactory.document, ForwardContact.this);

                                if (userData.isGroup()) {
                                    message.getGroupMessageObject(userData.get_id(), filePath, userData.getFirstName());
                                } else {
                                    message.getMessageObject(userData.get_id(), filePath, false);
                                }
                                MessageItemChat item = message.createMessageItem(true, filePath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName(), isExpiry, expireTime);

                                String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(filePath);
                                String docName = item.getMessageId() + fileExtension;
                                String docId;
                                JSONObject uploadObj;
                                if (userData.isGroup()) {
                                    item.setGroup(true);
                                    item.setGroupName(userData.getFirstName());
                                    docId = mCurrentUserId + "-" + userData.get_id() + "-g";
                                    uploadObj = (JSONObject) message.createDocUploadObject(item.getMessageId(), docId,
                                            docName, filePath, userData.getFirstName(), MessageFactory.CHAT_TYPE_GROUP, false);
                                    try {
                                        uploadObj.put("is_expiry", isExpiry);
                                        uploadObj.put("expiry_time", expireTime);
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    }
                                } else {
                                    item.setGroup(false);
                                    docId = mCurrentUserId + "-" + userData.get_id();
                                    if (userInfoSession.hasChatConvId(docId)) {
                                        item.setConvId(userInfoSession.getChatConvId(docId));
                                    }
                                    uploadObj = (JSONObject) message.createDocUploadObject(item.getMessageId(), docId,
                                            docName, filePath, userData.getFirstName(), MessageFactory.CHAT_TYPE_SINGLE, false);
                                }

                                uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
                                item.setChatFileLocalPath(filePath);
                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                MessageDbController db = CoreController.getDBInstance(ForwardContact.this);
                                if (userData.isGroup()) {

                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                                } else {
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                                }
                            }
                            break;

                        case (MessageFactory.web_link + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappContactModel userData = selectedContactsList.get(contactIndex);

                                String data = msgItem.getTextMessage();
                                String webLink = msgItem.getWebLink();
                                String webLinkTitle = msgItem.getWebLinkTitle();
                                String webLinkDesc = msgItem.getWebLinkDesc();
                                String webLinkImgUrl = msgItem.getWebLinkImgUrl();
                                String webLinkThumb = msgItem.getWebLinkImgThumb();

                                SendMessageEvent messageEvent = new SendMessageEvent();
                                WebLinkMessage message = (WebLinkMessage) MessageFactory.getMessage(MessageFactory.web_link, ForwardContact.this);
                                JSONObject msgObj;
                                if (userData.isGroup()) {
                                    msgObj = (JSONObject) message.getGroupMessageObject(userData.get_id(), data, userData.getFirstName());
                                } else {
                                    msgObj = (JSONObject) message.getMessageObject(userData.get_id(), data, false);
                                }

                                MessageItemChat item = message.createMessageItem(true, data, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName(), webLink, webLinkTitle, webLinkDesc, webLinkImgUrl, webLinkThumb, isExpiry, expireTime);
                                msgObj = (JSONObject) message.getWebLinkObject(msgObj, webLink, webLinkTitle, webLinkDesc, webLinkImgUrl, webLinkThumb);
                                messageEvent.setMessageObject(msgObj);

                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                MessageDbController db = CoreController.getDBInstance(ForwardContact.this);
                                if (userData.isGroup()) {
                                    item.setGroup(true);
                                    item.setGroupName(userData.getFirstName());
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                                    try {
                                        msgObj.put("is_expiry", isExpiry);
                                        msgObj.put("expiry_time", expireTime);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    messageEvent.setEventName(SocketManager.EVENT_GROUP);
                                } else {
                                    item.setGroup(false);
                                    String docId = mCurrentUserId + "-" + userData.get_id();
                                    if (userInfoSession.hasChatConvId(docId)) {
                                        item.setConvId(userInfoSession.getChatConvId(docId));
                                    }

                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                                    messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
                                }
                                EventBus.getDefault().post(messageEvent);
                            }

                            break;

                        case (MessageFactory.location + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappContactModel userData = selectedContactsList.get(contactIndex);

                                String data = msgItem.getTextMessage();
                                String webLink = msgItem.getWebLink();
                                String webLinkTitle = msgItem.getWebLinkTitle();
                                String webLinkDesc = msgItem.getWebLinkDesc();
                                String webLinkImgUrl = msgItem.getWebLinkImgUrl();
                                String webLinkThumb = msgItem.getWebLinkImgThumb();

                                SendMessageEvent messageEvent = new SendMessageEvent();
                                LocationMessage message = (LocationMessage) MessageFactory.getMessage(MessageFactory.location, ForwardContact.this);
                                JSONObject msgObj;
                                if (userData.isGroup()) {
                                    msgObj = (JSONObject) message.getGroupMessageObject(userData.get_id(), data, userData.getFirstName());
                                } else {
                                    msgObj = (JSONObject) message.getMessageObject(userData.get_id(), data, false);
                                }

                                MessageItemChat item = message.createMessageItem(true, data, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName(), webLinkTitle, webLinkDesc, webLink, webLinkImgUrl, webLinkThumb, isExpiry, expireTime);
                                msgObj = (JSONObject) message.getLocationObject(msgObj, webLinkTitle, webLinkDesc, webLink, webLinkImgUrl, webLinkThumb);
                                messageEvent.setMessageObject(msgObj);

                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                MessageDbController db = CoreController.getDBInstance(ForwardContact.this);
                                if (userData.isGroup()) {
                                    item.setGroup(true);
                                    item.setGroupName(userData.getFirstName());
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                                    try {
                                        msgObj.put("is_expiry", isExpiry);
                                        msgObj.put("expiry_time", expireTime);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    messageEvent.setEventName(SocketManager.EVENT_GROUP);
                                } else {
                                    item.setGroup(false);
                                    String docId = mCurrentUserId + "-" + userData.get_id();
                                    if (userInfoSession.hasChatConvId(docId)) {
                                        item.setConvId(userInfoSession.getChatConvId(docId));
                                    }
                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                                    messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
                                }

                                EventBus.getDefault().post(messageEvent);
                            }

                            break;
                    }

                }

                if (selectedContactsList.size() == 1) {
                    Intent intent = new Intent(ForwardContact.this, ChatViewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    ChatappContactModel userData = selectedContactsList.get(0);
                    intent.putExtra("receiverUid", userData.get_id());
                    intent.putExtra("documentId", userData.get_id());
                    intent.putExtra("receiverName", userData.getFirstName());
                    intent.putExtra("Username", userData.getFirstName());
                    intent.putExtra("Image", userData.getAvatarImageUrl());
                    intent.putExtra("type", 0);
                    intent.putExtra("msisdn", userData.getNumberInDevice());
                    startActivity(intent);
                    finish();
                } else {
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("MultiForward", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                }

            }

        });
        /* Variables for serch */
        sessionManager = SessionManager.getInstance(this);


        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));


                if (isHasNextPage) {
                    CallContactSearchAPI(searchKey);
                } else {
                    swipyRefreshLayout.setRefreshing(false);
                    Toast.makeText(ForwardContact.this, "No more contacts", Toast.LENGTH_SHORT).show();
                }

            }
        });
        /*
        rvListContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

            }

            @Override
            public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {


                try {

                    super.onScrollStateChanged(recyclerView, newState);

                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (isHasNextPage) {
                            CallContactSearchAPI(searchKey);
                        }
                        else {
                            Toast.makeText(ForwardContact.this, "No more contacts", Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
*/

        setupProgressDialog();
    }

    /**
     * Eventbus data
     *
     * @param event getting value to call socket(block user)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (SocketManager.EVENT_GET_CONTACTS.equalsIgnoreCase(event.getEventName())) {
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_BLOCK_USER)) {
            loadBlockUserMessage(event);
        }
    }

    /**
     * To check getting user is blocked or not
     *
     * @param event
     */
    private void loadBlockUserMessage(ReceviceMessageEvent event) {
        try {
            Object[] obj = event.getObjectsArray();
            JSONObject object = new JSONObject(obj[0].toString());

            String from = object.getString("from");
            String to = object.getString("to");

            if (mCurrentUserId.equalsIgnoreCase(from)) {
                for (int i = 0; i < contactListData.size(); i++) {
                    String userId = contactListData.get(i).get_id();
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

    /**
     * get GroupDetails
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
     * Start Eventbus function
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Stop Eventbus function
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * forward menu option view & search contact
     *
     * @param menu menu layout binding
     * @return value
     */
    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forward_contact, menu);
        searchItem1 = menu.findItem(R.id.chats_searchIcon1);
        searchItem2 = menu.findItem(R.id.chats_searchIcon2);
        searchItem3 = menu.findItem(R.id.chats_searchIcon3);


        searchView1 = (SearchView) MenuItemCompat.getActionView(searchItem1);
        searchView1.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {


                    if (query.equals("") && query.isEmpty()) {
                        searchView1.clearFocus();
                    }

                    if (frequentAdapter != null) {
                        frequentAdapter.getFilter().filter(query);
                    }
                    searchView1.setIconifiedByDefault(true);
                    searchView1.setIconified(true);
                    searchView1.setQuery("", false);
                    searchView1.clearFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (newText.equals("") && newText.isEmpty()) {
                        searchView1.clearFocus();
                    }
                    if (frequentAdapter != null) {
                        frequentAdapter.getFilter().filter(newText);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        searchView1.setIconifiedByDefault(true);
        searchView1.setQuery("", false);
        searchView1.clearFocus();
        searchView1.setIconified(true);

        AutoCompleteTextView searchTextView1 = (AutoCompleteTextView) searchView1.findViewById(R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView1, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
        }


        searchView2 = (SearchView) MenuItemCompat.getActionView(searchItem2);
        searchView2.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                FavListCall();
                //getviewdata();
                return false;
            }
        });
        searchView2.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                /*searchView2.setIconifiedByDefault(true);
                searchView2.setQuery("", false);
                searchView2.clearFocus();
                searchView2.setIconified(true);*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (newText.equals("") && newText.isEmpty()) {
                        searchView2.clearFocus();
                    }
                    if (addContactListAdapter != null) {
                        addContactListAdapter.getFilter().filter(newText);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                //contactListData.clear();

                /*contactListData.clear();
                page = 0;
                rvListContacts.setVisibility(View.GONE);
                contactSearchEmpty.setVisibility(View.VISIBLE);

                searchKey = newText;*/

                return false;
            }
        });

        searchView2.setIconifiedByDefault(true);
        searchView2.setQuery("", false);
        searchView2.clearFocus();
        searchView2.setIconified(true);

        final AutoCompleteTextView searchTextView2 = (AutoCompleteTextView) searchView2.findViewById(R.id.search_src_text);
        searchTextView2.setImeOptions(EditorInfo.IME_ACTION_DONE);

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {

                if (searchKey.length() >= 1) {
                    if (contactListData.size() == 0)
                        CallContactSearchAPI(searchKey);


//                    hideKeyboard(searchTextView);
                } /*else {
                    Toast.makeText(getActivity(), "Please type atleast 1 characters to search", Toast.LENGTH_SHORT).show();
                }*/

            }
        };
        /*final Handler handler = new Handler();
        TextWatcher filterTextWatcher = new TextWatcher() {

            public void afterTextChanged(Editable s) {

                handler.postDelayed(runnable, 500);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                handler.removeCallbacks(runnable);
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

        };

        searchTextView2.addTextChangedListener(filterTextWatcher);
        searchTextView2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    *//*if (searchKey.length() >= 1) {
                        if (contactListData.size() == 0)
                            CallContactSearchAPI(searchKey);
//                        hideKeyboard(searchTextView2);
                    }*//*
                    hideKeyboard(searchTextView2);
                    *//*else {
                        Toast.makeText(ForwardContact.this, "Please type atleast 3 characters to search", Toast.LENGTH_SHORT).show();
                    }*//*
                    return true;
                }
                return false;
            }
        });
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView2, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
        }*/


        searchView3 = (SearchView) MenuItemCompat.getActionView(searchItem3);
        searchView3.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    if (query.equals("") && query.isEmpty()) {
                        searchView3.clearFocus();
                    }
                    adapter.getFilter().filter(query);


                    searchView3.setIconifiedByDefault(true);
                    searchView3.setIconified(true);
                    searchView3.setQuery("", false);
                    searchView3.clearFocus();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (newText.equals("") && newText.isEmpty()) {
                        searchView3.clearFocus();
                    }

                    adapter.getFilter().filter(newText);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return false;
            }
        });

        searchView3.setIconifiedByDefault(true);
        searchView3.setQuery("", false);
        searchView3.clearFocus();
        searchView3.setIconified(true);

        AutoCompleteTextView searchTextView3 = (AutoCompleteTextView) searchView3.findViewById(R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView3, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
        }


        return super.onCreateOptionsMenu(menu);
    }

    /**
     * hide Keyboard
     *
     * @param searchTextView
     */
    public void hideKeyboard(AutoCompleteTextView searchTextView) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);
    }

    /**
     * API call for ContactSearch
     *
     * @param searchKey input value(search value)
     */
    private void CallContactSearchAPI(String searchKey) {
        showLoaderDialog(true);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userid", SessionManager.getInstance(this).getCurrentUserID());
        params.put("searchKey", searchKey);
        params.put("limit", "10");
        params.put("page", String.valueOf(page));

        ServiceRequest request = new ServiceRequest(this);
        request.makeServiceRequest(Constants.SEARCH_CONTACTS, Request.Method.POST, params, resultListener);

    }

    /**
     * API call for ContactSearch without search value
     */
    private void FavListCall() {
        contactListData.clear();
        page = 0;
        CallContactSearchAPI("");
    }

    private String kyGn(String id) {
        return getResources().getString(R.string.chatapp) + id + getResources().getString(R.string.adani);

        //  return Constants.DUMMY_KEY;

    }

    /**
     * image ThumbnailData
     *
     * @param imgPath input value(imgPath)
     * @param id      input value(id)
     * @return value
     */
    private String getThumbnailData(String imgPath, String id) {
        String thumbData = null;
        try {
            File file = new File(imgPath);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Bitmap compressBmp = new Compressor(this).compressToBitmap(file);
            compressBmp = Bitmap.createScaledBitmap(compressBmp, 100, 100, false);
            compressBmp.compress(Bitmap.CompressFormat.JPEG, 30, out);
            byte[] thumbArray = out.toByteArray();
            out.close();

            thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
            CryptLib cryptLib = null;
            try {
                cryptLib = new CryptLib();
                thumbData = cryptLib.encryptPlainTextWithRandomIV(thumbData, kyGn(id));

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return thumbData;
    }

    /**
     * video ThumbnailData
     *
     * @param videoPath input value(videoPath)
     * @param id        input value(id)
     * @return value
     */
    private String getVideoThumbnail(String videoPath, String id) {
        String thumbData = null;

        Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        thumbBmp.compress(Bitmap.CompressFormat.PNG, 10, out);
        byte[] thumbArray = out.toByteArray();
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
        if (thumbData != null) {
            CryptLib cryptLib = null;
            try {
                cryptLib = new CryptLib();
                thumbData = cryptLib.encryptPlainTextWithRandomIV(thumbData, kyGn(id));

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return thumbData;
    }


    /**
     * kill the current activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        /*etSearch.getText().clear();
         *//*if (adapter != null) {
                            adapter.updateInfo(chatappEntries);
                        }*//*
        etSearch.setVisibility(View.GONE);
        serach.setVisibility(View.VISIBLE);
        //overflowlayout.setVisibility(View.VISIBLE);
        selectcontactmember.setVisibility(View.VISIBLE);
        selectcontact.setVisibility(View.VISIBLE);
        backarrow.setVisibility(View.GONE);
        backButton.setVisibility(View.VISIBLE);*/
        hideKeyboard();
    }

    /**
     * Show toast message
     *
     * @param msg
     */
    public void toast(String msg) {
        Toast.makeText(ForwardContact.this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * add group contact user to send the data
     *
     * @param view
     * @param selectedItem
     */
    private void addContactToGroup(View view, ChatappContactModel selectedItem) {
        CheckBox c = (CheckBox) view.findViewById(R.id.selectedmember);

        LayoutInflater inflater = (LayoutInflater) ForwardContact.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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

            if (member.contains(memberId)) {
                Toast.makeText(this, "Sorry this contact already added", Toast.LENGTH_SHORT).show();
            } else {
                addContact(selectedItem, memberId, myMAp, inflater, this);
            }
        }
    }

    /**
     * Layout view updated & remove and add user
     *
     * @param selectedItem   input value(selectedItem)
     * @param memberId       input value(memberId)
     * @param myMAp          input value(myMAp)
     * @param inflater       input value(inflater)
     * @param forwardContact input value(forwardContact)
     */
    @SuppressLint("ClickableViewAccessibility")
    private void addContact(final ChatappContactModel selectedItem, String memberId, Map<String, String> myMAp, LayoutInflater inflater, ForwardContact forwardContact) {

        member.add(memberId);


        if (selectedItem.isSelected()) {
            selectedContactsList.add(selectedItem);
        } else {
            if (selectedContactsList.contains(selectedItem)) {
                selectedContactsList.remove(selectedItem);
            }
        }

        mylist.add(myMAp);
        if (member.size() > 0) {
            selectgroupmember.setVisibility(View.VISIBLE);
            //getviewdata();
        } else {
            selectgroupmember.setVisibility(View.GONE);
        }


        final View view1 = inflater.inflate(R.layout.viewtoinflate, mainlayout, false);

        CircleImageView image = (CircleImageView) view1.findViewById(R.id.image);
        final AvnNextLTProRegTextView phoneNumber = (AvnNextLTProRegTextView) view1.findViewById(R.id.phonenumber);
        AvnNextLTProRegTextView Selectedmemname = (AvnNextLTProRegTextView) view1.findViewById(R.id.selectedmembername);
        ImageView removeicon = (ImageView) view1.findViewById(R.id.removeicon);


        phoneNumber.setText(selectedItem.getMsisdn());
        Glide.with(this).load(Constants.SOCKET_IP + selectedItem.getAvatarImageUrl()).thumbnail(0.1f)
                .dontAnimate()
                .error(R.mipmap.chat_attachment_profile_default_image_frame)
                .dontTransform()
                .into(image);


        Selectedmemname.setText(selectedItem.getFirstName());

        image.setPadding(20, 0, 0, 0);

        removeicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mainlayout.removeView(view1);

                // Toast.makeText(ForwardContact.this,"",Toast.LENGTH_LONG).show();

                for (int i = 0; i < mylist.size(); i++) {

                    if (mylist.get(i).get("receiverUid").equals(phoneNumber.getText().toString())) {

                        ChatappContactModel contact = new ChatappContactModel();
                        contact.setMsisdn(mylist.get(i).get("receiverUid"));
                        contact.setFirstName(mylist.get(i).get("receiverName"));
                        contact.setAvatarImageUrl(mylist.get(i).get("Image"));
                        contact.setStatus(mylist.get(i).get("Status"));
                        contact.set_id(mylist.get(i).get("id"));
                        contactListData.add(0, contact);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                addContactListAdapter.notifyDataSetChanged();
                            }
                        });

                        mylist.remove(i);
                        member.remove(i);
                        break;
                    }

                }
                if (selectedContactsList.contains(selectedItem)) {
                    selectedContactsList.remove(selectedItem);
                }
                if (selectedContactsList.size() == 0) {
                    Sendlayout.setVisibility(View.GONE);
                    sendmessage.setVisibility(View.GONE);
                } else {

                    Sendlayout.setVisibility(View.VISIBLE);
                    sendmessage.setVisibility(View.VISIBLE);

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.bottom_up);
                    Sendlayout.setAnimation(animation);

                    StringBuilder sb = new StringBuilder();
                    int nameIndex = 0;
                    for (ChatappContactModel contact : selectedContactsList) {
                        sb.append(contact.getFirstName());
                        nameIndex++;
                        if (selectedContactsList.size() > nameIndex) {
                            sb.append(", ");
                        }
                    }

                    resevernameforward.setText(sb);

                }

            }
        });

        mainlayout.addView(view1);

        int index = contactListData.indexOf(selectedItem);
        if (index > -1) {
            contactListData.remove(index);
        }

        addContactListAdapter.notifyDataSetChanged();


    }
}
