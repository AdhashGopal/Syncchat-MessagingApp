package com.chatapp.synchat.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.Parcelable;
import android.provider.DocumentsContract;
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
import android.webkit.MimeTypeMap;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.ForwardContactAdapter;
import com.chatapp.synchat.app.adapter.RItemAdapter;
import com.chatapp.synchat.app.contactlist.AddGroupChatAdapter;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.utils.BlockUserUtils;

import org.appspot.apprtc.util.CryptLib;

import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.app.widget.CircleImageView;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.AudioMessage;
import com.chatapp.synchat.core.message.DocumentMessage;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.message.PictureMessage;
import com.chatapp.synchat.core.message.TextMessage;
import com.chatapp.synchat.core.message.VideoMessage;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.service.ServiceRequest;
import com.chatapp.synchat.core.socket.SocketManager;
import com.chatapp.synchat.core.uploadtoserver.FileUploadDownloadManager;
import com.google.android.material.tabs.TabLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.nio.channels.FileChannel;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.NoSuchPaddingException;

import id.zelory.compressor.Compressor;

/**
 * created by  Adhash Team on 5/18/2017.
 */
public class ShareFromThirdPartyAppActivity extends CoreActivity {

    private AlertDialog loaderDialog;
    private boolean isHasNextPage;
    private int page = 0;
    private boolean multiple = false;
    private SwipyRefreshLayout swipyRefreshLayout;
    private ArrayList<String> member = new ArrayList<>();
    private AddGroupChatAdapter addContactListAdapter;
    private RelativeLayout frequentContactLayout, contactSearchLayout, groupSearchLayout, Sendlayout;
    private LinearLayout mainlayout;
    private HorizontalScrollView selectgroupmember;
    ContactDB_Sqlite contactDB_sqlite;
    MenuItem searchItem1, searchItem2, searchItem3;
    ArrayList<ChatappContactModel> ChatappEntries;
    private SearchView searchView1, searchView2, searchView3;
    private String searchKey = "";
    private List<Map<String, String>> mylist = new ArrayList<>();
    String url = "";
    private String imagePath = "";
    //RecyclerView lvContacts, rvFreqContact;
    private RecyclerView rvListContacts, rvListGroups, rvFreqContact;
    ForwardContactAdapter adapter, frequentAdapter;
    private static final String TAG = ShareFromThirdPartyAppActivity.class.getSimpleName();
    private SessionManager sessionManager;
    private Session session;
    AvnNextLTProRegTextView resevernameforward;
    private List<ChatappContactModel> selectedContactsList;
    private ArrayList<ChatappContactModel> dataList;
    ImageView sendmessage;
    private TextView tvFrequentLbl, contactSearchEmpty, frequentContactEmpty, groupSearchEmpty;
    private FileUploadDownloadManager uploadDownloadManager;
    private SearchView searchView;
    String mCurrentUserId, textMsgFromVendor;

    //    private List<Uri> uriList;
    //    private int messageType = 100;
    private Intent receivedIntent;
    private ArrayList<ChatappContactModel> chatappEntries;
    ArrayList<ChatappContactModel> frequentList = new ArrayList<>();
    ArrayList<ChatappContactModel> grouplist = new ArrayList<>();
    private List<ChatappContactModel> contactListData = new ArrayList<ChatappContactModel>();
    ArrayList<String> userIdList = new ArrayList<>();

    private GroupInfoSession groupInfoSession;
    private UserInfoSession userInfoSession;
    public static String shareAction, type;
    Getcontactname getcontactname;

    /**
     * Contact list response
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

                    addContactListAdapter = new AddGroupChatAdapter(ShareFromThirdPartyAppActivity.this, contactListData);
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
                    Toast.makeText(ShareFromThirdPartyAppActivity.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showLoaderDialog(false);
                swipyRefreshLayout.setRefreshing(false);
                Toast.makeText(ShareFromThirdPartyAppActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
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
    @SuppressLint("SetTextI18n")
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
     * shown Progress Dialog
     *
     * @param show based on value ProgressDialog dismiss or show
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
     * Child view layout
     *
     * @param relativeLayouts set view option (visible gone)
     */
    private void setLayoutVisibility(RelativeLayout... relativeLayouts) {

        for (RelativeLayout view : relativeLayouts) {
            view.setVisibility(View.GONE);
        }


    }

    /**
     * set MenuVisibility
     *
     * @param menuItems menu item visible item false
     */
    private void setMenuVisibility(MenuItem... menuItems) {


        for (MenuItem menuItem : menuItems) {
            menuItem.setVisible(false);
        }

    }

    /**
     * Load the data based on tab view. and it will get the shared data from thired party app's. (image,video,document,etc,.)
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
        contactDB_sqlite = CoreController.getContactSqliteDBintstance(ShareFromThirdPartyAppActivity.this);
        userInfoSession = new UserInfoSession(this);
        frequentContactEmpty = findViewById(R.id.frequent_contact_empty);
        contactSearchEmpty = findViewById(R.id.contact_search_empty);
        groupSearchEmpty = findViewById(R.id.group_search_empty);
//        tvFrequentLbl = (TextView) findViewById(R.id.tvFrequentLbl);
        sendmessage = (ImageView) findViewById(R.id.overlapImage);
        resevernameforward = (AvnNextLTProRegTextView) findViewById(R.id.chat_text_view);
        uploadDownloadManager = new FileUploadDownloadManager(ShareFromThirdPartyAppActivity.this);
        groupInfoSession = new GroupInfoSession(ShareFromThirdPartyAppActivity.this);
        getcontactname = new Getcontactname(ShareFromThirdPartyAppActivity.this);
        mCurrentUserId = SessionManager.getInstance(this).getCurrentUserID();
        session = new Session(ShareFromThirdPartyAppActivity.this);
        //getviewdata();
        setupProgressDialog();
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
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


        setTitle("Send to..");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeButtonEnabled(true);
        }
//        tvFrequentLbl = (TextView) findViewById(R.id.tvFrequentLbl);


        rvListContacts = (RecyclerView) findViewById(R.id.listContacts);
        LinearLayoutManager layoutManager = new LinearLayoutManager(ShareFromThirdPartyAppActivity.this, LinearLayoutManager.VERTICAL, false);
        rvListContacts.setLayoutManager(layoutManager);
        //rvListContacts.setNestedScrollingEnabled(false);

        rvListGroups = (RecyclerView) findViewById(R.id.listGroups);
        LinearLayoutManager mediaManager = new LinearLayoutManager(ShareFromThirdPartyAppActivity.this, LinearLayoutManager.VERTICAL, false);
        rvListGroups.setLayoutManager(mediaManager);

        rvFreqContact = (RecyclerView) findViewById(R.id.rvFreqContact);
        LinearLayoutManager freqManager = new LinearLayoutManager(ShareFromThirdPartyAppActivity.this, LinearLayoutManager.VERTICAL, false);
        rvFreqContact.setLayoutManager(freqManager);
        //rvFreqContact.setNestedScrollingEnabled(false);

        //initProgress("Loading contacts...", true);

//        loadContactsFromDB();

        List<String> lists = session.getBlockedIds();

        final ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        MessageDbController db = CoreController.getDBInstance(ShareFromThirdPartyAppActivity.this);
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
                try {
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
                } catch (Exception e) {
                    e.printStackTrace();
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
                    if (contact.getStatus().contains("You")) {
                        dataList.add(contact);
                        Log.e("GrpId", contact.get_id() + " Status-->" + contact.getStatus());
                    } else {
                        Log.e("GrpId-->", contact.get_id() + " Status-->" + contact.getStatus());
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
        adapter = new ForwardContactAdapter(ShareFromThirdPartyAppActivity.this, dataList);
        rvListGroups.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        rvListContacts.addOnItemTouchListener(new RItemAdapter(
                ShareFromThirdPartyAppActivity.this, rvListContacts, new RItemAdapter.OnItemClickListener() {

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

        receivedIntent = getIntent();
        url = receivedIntent.getStringExtra("GOOGLE_IMG");
        Log.e("GOOGLE", url);

        Sendlayout = (RelativeLayout) findViewById(R.id.sendlayout);

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Sizeofitem", selectedContactsList.size() + "");
                if (selectedContactsList.size() > 0 && sessionManager.isValidDevice()) {
                    sendmessage.setEnabled(false); //for disable duplicate clicks
                    sendMessage();
                }
            }
        });

        sessionManager = SessionManager.getInstance(this);


        //TODO loade more
        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));


                if (isHasNextPage) {
                    CallContactSearchAPI(searchKey);
                } else {
                    swipyRefreshLayout.setRefreshing(false);
                    Toast.makeText(ShareFromThirdPartyAppActivity.this, "No more contacts", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    /**
     * shown toast mesaage
     *
     * @param msg
     */
    public void toast(String msg) {
        Toast.makeText(ShareFromThirdPartyAppActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * add Contact To Group
     *
     * @param view         dynamic view
     * @param selectedItem specific contact
     */
    private void addContactToGroup(View view, ChatappContactModel selectedItem) {
        CheckBox c = (CheckBox) view.findViewById(R.id.selectedmember);

        LayoutInflater inflater = (LayoutInflater) ShareFromThirdPartyAppActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
                addContact(selectedItem, memberId, myMAp, inflater, ShareFromThirdPartyAppActivity.this);
            }
        }
    }

    /**
     * addContact list data
     *
     * @param selectedItem   input value(selectedItem)
     * @param memberId       input value(memberId)
     * @param myMAp          input value(myMAp)
     * @param inflater       input value(inflater)
     * @param forwardContact input value(forwardContact)
     */
    @SuppressLint("ClickableViewAccessibility")
    private void addContact(final ChatappContactModel selectedItem, String memberId, Map<String, String> myMAp, LayoutInflater inflater, ShareFromThirdPartyAppActivity forwardContact) {

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
//TODO tharani map
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

    /**
     * Call API without any search item
     */
    private void FavListCall() {
        contactListData.clear();
        page = 0;
        CallContactSearchAPI("");
    }

    /**
     * Call Contact API for with or without search item
     *
     * @param searchKey
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

    private void displayAlert(final String txt, final String userId) {
        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setMessage(txt);
        dialog.setNegativeButtonText("Cancel");
        dialog.setPositiveButtonText("Unblock");
        dialog.setCancelable(false);
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                BlockUserUtils.changeUserBlockedStatus(ShareFromThirdPartyAppActivity.this, EventBus.getDefault(), mCurrentUserId, userId, false);
                dialog.dismiss();
                adapter.updateInfo(dataList);
            }

            @Override

            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "Unblock a person");

    }


    /**
     * based on the value shared data extract specific function
     */
    private void sendMessage() {

        if (type != null) {
            System.out.println("======type" + type);

            if (Intent.ACTION_SEND.equals(shareAction)) {
                Uri uri = null;
                try {

                    uri = (Uri) receivedIntent.getExtras().get(Intent.EXTRA_STREAM);
                    System.out.println("======uri" + uri);
                    textMsgFromVendor = receivedIntent.getExtras().getString(Intent.EXTRA_TEXT, "");
                } catch (Exception e) {
                    Log.e(TAG, "sendMessage: ", e);
                }
                if (uri == null) {
                    if (type.startsWith("text/plain")) {
                        System.out.println("======ifuri" + uri);
                        textMsgFromVendor = receivedIntent.getExtras().getString(Intent.EXTRA_TEXT, "");
                        System.out.println("========itext" + textMsgFromVendor);
                        sendTextMessage();
                    }

                } else {
                    System.out.println("======elseuri" + uri);
                    if (textMsgFromVendor == null)
                        textMsgFromVendor = "";
                    if (type.startsWith("image/")) {
                        System.out.println("========image");
                        System.out.println("========image" + textMsgFromVendor);
                        System.out.println("========uriinthirdparty" + uri);
                        sendImageChatMessage(uri, textMsgFromVendor);
                    } else if (type.startsWith("audio/") || type.contains("application/ogg")) {
                        String extension = getFileExtnFromPath(uri.getPath());
                        System.out.println("====extension" + extension);
                        if (extension != null) {
                            if (extension.startsWith("w")) {
                                Toast.makeText(this, "Audio file type not supported", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                sendAudioMessage(uri);
                            }
                        }
                    } else if (type.startsWith("video/")) {
                        System.out.println("========video");
                        sendVideoChatMessage(uri, textMsgFromVendor);
                    } else if (type.startsWith("application/") || type.startsWith("text/plain")) {
                        System.out.println("========document");
                        sendDocumentMessage(uri);
                    }
                }
            } else if (Intent.ACTION_SEND_MULTIPLE.equals(shareAction)) {

                System.out.println("======elseif" + shareAction);

                ArrayList<Parcelable> list = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getIntent().setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                for (int i = 0; i < list.size(); i++) {
                    String s = list.get(i).toString();
                    if (s.contains("audio")) {
                        type = "audio/";
                    } else {

                    }
                }

                for (Parcelable parcel : list) {
                    Uri uri = (Uri) parcel;
                    String mimeType = "";

                    String extension = getFileExtnFromPath(uri.getPath());
                    if (extension != null) {
                        if (extension.startsWith(".3g")) {
                            if (isVideoFile(uri.getPath())) {
                                mimeType = "video/3gpp";
                            } else {
                                mimeType = "audio/3gpp";
                            }
                        } else {
                            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                        }
                    }

                /*    List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(getIntent(), PackageManager.MATCH_DEFAULT_ONLY);
                    for (ResolveInfo resolveInfo : resInfoList) {
                        String packageName = resolveInfo.activityInfo.packageName;
                        grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    }*/

                    if (type != null) {
                        if (type.startsWith("image/")) {

                            System.out.println("========image");
                            System.out.println("========uriinthirdparty" + uri);
                            String data = uri.toString();
                            if (data.contains("content://com.google.android.apps.photos")) {
                                sendImageChatMessage(uri, "");
                                break;
                            } else {
                                sendImageChatMessage(uri, "");
                            }
//                            if (type.equalsIgnoreCase("image/*")) {
//                                sendImageChatMessage(uri, "");
//                            } else if (type.contains("image/")) {
//                                sendImageChatMessage(uri, "");
//                            } else {
//                                sendImageChatMessage(uri, "");
//                                break;
//                            }
                        } else if (type.startsWith("audio/") || type.contains("application/ogg")) {
                            String data = uri.toString();
                            if (data.contains("audio")) {
                                sendAudioMessage(uri);
                            } else if (data.contains("image")) {
                                sendImageChatMessage(uri, "");
                            } else if (data.contains("video")) {
                                sendVideoChatMessage(uri, "");
                            } else {
                                sendDocumentMessage(uri);
                            }

                        } else if (type.startsWith("video/")) {
                            String data = uri.toString();
                            if (data.contains("content://com.google.android.apps.photos")) {
                                sendVideoChatMessage(uri, "");
                                break;
                            } else {
                                sendVideoChatMessage(uri, "");
                            }

                        } else if (type.startsWith("application/") || type.startsWith("text/plain")) {
                            System.out.println("========documentmultiple");
                            String data = uri.toString();
                            if (data.contains("image")) {
                                sendImageChatMessage(uri, "");
                            } else if (data.contains("audio") || data.contains("application/ogg")) {
                                sendAudioMessage(uri);
                            } else if (data.contains("video")) {
                                sendVideoChatMessage(uri, "");
                            } else {
                                sendDocumentMessage(uri);
                            }

                        } else if (type.startsWith("*/*") && url.equalsIgnoreCase("local")) {
                            String data = uri.toString();
                            if (data.contains("content://com.android.chrome.FileProvider/downloads/")) {
                                if (data.contains("audio") || data.contains("application/ogg")) {
                                    sendAudioMessage(uri);
                                } else if (data.contains("image")) {
                                    sendImageChatMessage(uri, "");
                                } else if (data.contains("video")) {
                                    sendVideoChatMessage(uri, "");
                                } else if (data.contains("content://media/external/video/")) {
                                    sendVideoChatMessage(uri, "");
                                } else {
                                    sendDocumentMessage(uri);
                                }
                            } else {
                                if (data.contains("audio") || data.contains("application/ogg")) {
                                    sendAudioMessage(uri);
                                } else if (data.contains("image")) {
                                    sendImageChatMessage(uri, "");
                                } else if (data.contains("video")) {
                                    sendVideoChatMessage(uri, "");
                                } else if (data.contains("content://media/external/video/")) {
                                    sendVideoChatMessage(uri, "");
                                } else {
                                    sendDocumentMessage(uri);
                                }
                            }

                        } else {
                            sendImageChatMessage(uri, "");
                            break;
                        }
                    }
                }
            }
            if (selectedContactsList.size() == 1) {
                Intent intent = new Intent(ShareFromThirdPartyAppActivity.this, ChatViewActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                ChatappContactModel userData = selectedContactsList.get(0);
                intent.putExtra("receiverId", userData.get_id());
                intent.putExtra("documentId", userData.get_id());
                intent.putExtra("receiverName", userData.getFirstName());
                intent.putExtra("Username", userData.getFirstName());
                intent.putExtra("Image", userData.getAvatarImageUrl());
                intent.putExtra("type", 0);
                intent.putExtra("msisdn", userData.getNumberInDevice());
                startActivity(intent);
                finish();
                File file = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH);

                /*if (file.isDirectory()) {
                    String[] children = file.list();
                    if (children != null) {
                        for (int i = 0; i < children.length; i++) {
                            new File(file, children[i]).delete();
                        }
                    }
                }*/

            } else {
                ActivityLauncher.launchHomeScreen(ShareFromThirdPartyAppActivity.this);
                File file = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH);
                /*if (file.isDirectory()) {
                    String[] children = file.list();
                    if (children != null) {
                        for (int i = 0; i < children.length; i++) {
                            new File(file, children[i]).delete();
                        }
                    }
                }*/
            }
        }
    }

    /**
     * Copy the file source to distination
     *
     * @param src input value(src)
     * @param dst input value(dst)
     * @throws IOException Error
     */
    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    /**
     * get File Extn From Path
     *
     * @param fileName input value(fileName)
     * @return value
     */
    public String getFileExtnFromPath(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    /**
     * video file path
     *
     * @param path input value(path)
     * @return value
     */
    private boolean isVideoFile(String path) {
        int height = 0;
        try {
            File file = new File(path);
            MediaPlayer mp = new MediaPlayer();
            FileInputStream fs;
            FileDescriptor fd;
            fs = new FileInputStream(file);
            fd = fs.getFD();
            mp.setDataSource(fd);
            mp.prepare();
            height = mp.getVideoHeight();
            mp.release();
        } catch (Exception e) {
            Log.e("test", "Exception trying to determine if 3gp file is video.", e);
        }
        return height > 0;
    }

    /**
     * Eventbus data
     *
     * @param event based on the value to call socket(block user, group,group message)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (SocketManager.EVENT_GET_CONTACTS.equalsIgnoreCase(event.getEventName())) {
            Object[] args = event.getObjectsArray();
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_BLOCK_USER)) {
            adapter.notifyDataSetChanged();
        } else {

            if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP)) {

                try {
                    Object[] obj = event.getObjectsArray();
                    JSONObject object = new JSONObject(obj[0].toString());
                    String groupAction = object.getString("groupType");

                    if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EVENT_GROUP_MESSAGE)) {
//                        handleGroupMessage(event);
                    }

                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    /*@Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        url = event;
        Log.e("", url);
    }
*/

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
     * Create OptionsMenu & search action
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


                /*contactListData.clear();
                page = 0;
                rvListContacts.setVisibility(View.GONE);
                contactSearchEmpty.setVisibility(View.VISIBLE);

                searchKey = newText;
*/
                return false;
            }
        });

//        searchView2.setIconifiedByDefault(true);
//        searchView2.setQuery("", false);
//        searchView2.clearFocus();
//        searchView2.setIconified(true);

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
        final Handler handler = new Handler();

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
                    hideKeyboard(searchTextView2);
                   /* if (searchKey.length() >= 3) {
                        if (contactListData.size() == 0)
                            CallContactSearchAPI(searchKey);
                        hideKeyboard(searchTextView2);
                    } else {
                        Toast.makeText(ShareFromThirdPartyAppActivity.this, "Please type atleast 3 characters to search", Toast.LENGTH_SHORT).show();
                    }*/
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
        }


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

    public void hideKeyboard(AutoCompleteTextView searchTextView) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);
    }

    /**
     * send text message with encryption
     */
    private void sendTextMessage() {

        // Text Message max length
        if (textMsgFromVendor.length() > 1024) {
            textMsgFromVendor = textMsgFromVendor.substring(0, 1023);
        }
        CryptLib cryptLib = null;
        try {
            cryptLib = new CryptLib();

            MessageDbController db = CoreController.getDBInstance(this);

            for (int i = 0; i < selectedContactsList.size(); i++) {
                String convId = "";


                if (selectedContactsList.get(i).isGroup()) {

                    if (groupInfoSession.hasGroupInfo(selectedContactsList.get(i).get_id())) {
                        GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(selectedContactsList.get(i).get_id());
                        convId = infoPojo.getGroupId();

                    }
                } else {
                    if (userInfoSession.hasChatConvId(selectedContactsList.get(i).get_id())) {
                        convId = userInfoSession.getChatConvId(selectedContactsList.get(i).get_id());
                    }
                }

                textMsgFromVendor = cryptLib.encryptPlainTextWithRandomIV(textMsgFromVendor, kyGn(convId));

                String to = selectedContactsList.get(i).get_id();
                String groupname = selectedContactsList.get(i).getFirstName();
                String receiverMsisdn = selectedContactsList.get(i).getNumberInDevice();

                if (session.getarchivecount() != 0) {
                    if (session.getarchive(mCurrentUserId + "-" + to))
                        session.removearchive(mCurrentUserId + "-" + to);
                }
                if (session.getarchivecountgroup() != 0) {
                    if (session.getarchivegroup(mCurrentUserId + "-" + to + "-g"))
                        session.removearchivegroup(mCurrentUserId + "-" + to + "-g");
                }

                SendMessageEvent messageEvent = new SendMessageEvent();
                TextMessage message = (TextMessage) MessageFactory.getMessage(MessageFactory.text, this);
                JSONObject msgObj;

                if (selectedContactsList.get(i).isGroup()) {

                    messageEvent.setEventName(SocketManager.EVENT_GROUP);
                    msgObj = (JSONObject) message.getGroupMessageObject(to, textMsgFromVendor, groupname);

                    try {
                        msgObj.put("groupType", SocketManager.ACTION_EVENT_GROUP_MESSAGE);
                        msgObj.put("userName", groupname);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                    messageEvent.setMessageObject(msgObj);
                    MessageItemChat item = message.createMessageItem(true, textMsgFromVendor, MessageFactory.DELIVERY_STATUS_NOT_SENT, to, groupname, "0", "0");
                    item.setGroupName(groupname);
                    item.setGroup(true);
                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                    EventBus.getDefault().post(messageEvent);

                } else {
                    msgObj = (JSONObject) message.getMessageObject(to, textMsgFromVendor, false);
                    messageEvent.setEventName(SocketManager.EVENT_MESSAGE);

                    MessageItemChat item = message.createMessageItem(true, textMsgFromVendor,
                            MessageFactory.DELIVERY_STATUS_NOT_SENT, to, "", "", "");
                    messageEvent.setMessageObject(msgObj);
                    item.setGroup(false);
                    item.setConvId(convId);
                    item.setSenderMsisdn(receiverMsisdn);
                    item.setSenderName(receiverMsisdn);

                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                    EventBus.getDefault().post(messageEvent);
                }


            }

            ActivityLauncher.launchHomeScreen(ShareFromThirdPartyAppActivity.this);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String kyGn(String mConvId) {
        return getResources().getString(R.string.chatapp) + mConvId + getResources().getString(R.string.adani);

        //   return  Constants.DUMMY_KEY;

    }

    /**
     * getting the path value
     *
     * @param uri      input value(uri)
     * @param activity current activity
     * @return value
     */
    public String getPath(Uri uri, Activity activity) {
        Cursor cursor = null;
        try {
            String[] projection = {MediaStore.MediaColumns.DATA};
            cursor = activity.getContentResolver().query(uri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(column_index);
            }
        } catch (Exception e) {
        } finally {
            cursor.close();
        }
        return "";
    }

    /**
     * send Image Chat Message
     *
     * @param uri     input value(uri)
     * @param caption input value(caption)
     */
    public void sendImageChatMessage(Uri uri, String caption) {
        try {
            String imgPath = "";
            MessageDbController db = CoreController.getDBInstance(this);
            for (int i = 0; i < selectedContactsList.size(); i++) {
                String to = selectedContactsList.get(i).get_id();
                String receiverMsisdn = selectedContactsList.get(i).getNumberInDevice();
                String groupname = selectedContactsList.get(i).getFirstName();

                if (type.equalsIgnoreCase("image/*")) {
                    imgPath = getImageFilePath(uri);
                    System.out.println("===imagpathinmethod" + imgPath);

                    if (imgPath != null && !imgPath.equals("")) {
                        String convId = "";


                        if (selectedContactsList.get(i).isGroup()) {

                            if (groupInfoSession.hasGroupInfo(selectedContactsList.get(i).get_id())) {
                                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(selectedContactsList.get(i).get_id());
                                convId = infoPojo.getGroupId();

                            }
                        } else {
                            if (userInfoSession.hasChatConvId(selectedContactsList.get(i).get_id())) {
                                convId = userInfoSession.getChatConvId(selectedContactsList.get(i).get_id());
                                System.out.println("====conv" + convId);

                            }
                        }

                        PictureMessage message = (PictureMessage) MessageFactory.getMessage(MessageFactory.picture, this);

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(imgPath, options);
                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;

                        if (selectedContactsList.get(i).isGroup()) {
                            message.getGroupMessageObject(to, imgPath, groupname);
                        } else {
                            message.getMessageObject(to, imgPath, false);
                        }

                        MessageItemChat item = message.createMessageItem(true, caption, imgPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                to, "", imageWidth, imageHeight, "", "");

                        System.out.println("===imagpath" + imgPath);
                        String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(imgPath);
                        String imgName = item.getMessageId() + fileExtension;
                        String docId;
                        JSONObject uploadObj;

                        if (selectedContactsList.get(i).isGroup()) {
                            item.setGroupName(groupname);
                            item.setGroup(true);
                            docId = mCurrentUserId + "-" + to + "-g";
                            uploadObj = (JSONObject) message.createImageUploadObject(item.getMessageId(), docId,
                                    imgName, imgPath, groupname, "", MessageFactory.CHAT_TYPE_GROUP, false);
                            item.setImagePath(imgPath);


                        } else {
                            docId = mCurrentUserId + "-" + to;
                            item.setGroup(false);
                            item.setConvId(convId);

                            uploadObj = (JSONObject) message.createImageUploadObject(item.getMessageId(), docId, imgName, imgPath,
                                    "", caption, MessageFactory.CHAT_TYPE_SINGLE, false);

                        }
                        try {
                            String thumbdata = getThumbnailData(imgPath, convId);
                            uploadObj.put("thumbnail_data", thumbdata);
                            item.setThumbnailData(thumbdata);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("=====upload" + uploadObj);
                        uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);


                        item.setSenderMsisdn(receiverMsisdn);
                        item.setSenderName(groupname);

                        if (selectedContactsList.get(i).isGroup()) {
                            db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                        } else {
                            db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                        }
                    } else {
                        Log.e("Else", "Else");
                    }


                } else if (url.equalsIgnoreCase("")) {
                    //if there is no SD card, create new directory objects to make directory on device
                    if (Environment.getExternalStorageState() != null) {
//                        File photoDirectory = new File(Environment.getExternalStorageDirectory() + "/aChat/");
//                        File photoDirectory1 = new File(Environment.getExternalStorageDirectory() + "/aChat1/");
                        File photoDirectory = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH);
                        File photoDirectory1 = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH_BACKUP);

                        String soc = photoDirectory.getAbsolutePath();
                        String des = photoDirectory1.getAbsolutePath();
                        copyFileOrDirectory(soc, des);
                        //File photoDirectory = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH + "/");
                        if (photoDirectory.exists()) {
                            File[] dirFiles = photoDirectory.listFiles();
                            if (dirFiles.length > 0) {
                                for (int ii = 0; ii < dirFiles.length; ii++) {
                                    imgPath = dirFiles[ii].toString();
                                    Log.e("dirFiles", dirFiles[ii].toString());

                                    if (imgPath.contains(".jpg")) {
                                        if (imgPath != null && !imgPath.equals("")) {
                                            String convId = "";


                                            if (selectedContactsList.get(i).isGroup()) {

                                                if (groupInfoSession.hasGroupInfo(selectedContactsList.get(i).get_id())) {
                                                    GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(selectedContactsList.get(i).get_id());
                                                    convId = infoPojo.getGroupId();

                                                }
                                            } else {
                                                if (userInfoSession.hasChatConvId(selectedContactsList.get(i).get_id())) {
                                                    convId = userInfoSession.getChatConvId(selectedContactsList.get(i).get_id());
                                                    System.out.println("====conv" + convId);

                                                }
                                            }

                                            PictureMessage message = (PictureMessage) MessageFactory.getMessage(MessageFactory.picture, this);

                                            BitmapFactory.Options options = new BitmapFactory.Options();
                                            options.inJustDecodeBounds = true;
                                            BitmapFactory.decodeFile(imgPath, options);
                                            int imageHeight = options.outHeight;
                                            int imageWidth = options.outWidth;

                                            if (selectedContactsList.get(i).isGroup()) {
                                                message.getGroupMessageObject(to, imgPath, groupname);
                                            } else {
                                                message.getMessageObject(to, imgPath, false);
                                            }

                                            MessageItemChat item = message.createMessageItem(true, caption, imgPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                                    to, "", imageWidth, imageHeight, "", "");

                                            System.out.println("===imagpath" + imgPath);
                                            String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(imgPath);
                                            String imgName = item.getMessageId() + fileExtension;
                                            String docId;
                                            JSONObject uploadObj;

                                            if (selectedContactsList.get(i).isGroup()) {
                                                item.setGroupName(groupname);
                                                item.setGroup(true);
                                                docId = mCurrentUserId + "-" + to + "-g";
                                                uploadObj = (JSONObject) message.createImageUploadObject(item.getMessageId(), docId,
                                                        imgName, imgPath, groupname, "", MessageFactory.CHAT_TYPE_GROUP, false);
                                                item.setImagePath(imgPath);


                                            } else {
                                                docId = mCurrentUserId + "-" + to;
                                                item.setGroup(false);
                                                item.setConvId(convId);

                                                uploadObj = (JSONObject) message.createImageUploadObject(item.getMessageId(), docId, imgName, imgPath,
                                                        "", caption, MessageFactory.CHAT_TYPE_SINGLE, false);

                                            }
                                            try {
                                                String thumbdata = getThumbnailData(imgPath, convId);
                                                uploadObj.put("thumbnail_data", thumbdata);
                                                item.setThumbnailData(thumbdata);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            System.out.println("=====upload" + uploadObj);
                                            uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);

                                            item.setSenderMsisdn(receiverMsisdn);
                                            item.setSenderName(groupname);

                                            if (selectedContactsList.get(i).isGroup()) {
                                                db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                                            } else {
                                                db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                                            }
                                        } else {
                                            Log.e("Else", "Else");
                                        }
                                    } else if (imgPath.contains(".mp4")) {
                                        Uri myUri = Uri.parse(imgPath);
                                        String videoPath = getVideoFilePath(myUri);
                                        if (videoPath != null && !videoPath.equals("")) {

                                            for (int j = 0; j < selectedContactsList.size(); j++) {
                                                String to1 = selectedContactsList.get(j).get_id();
                                                String receiverMsisdn1 = selectedContactsList.get(j).getNumberInDevice();
                                                String groupname1 = selectedContactsList.get(j).getFirstName();
                                                String convId = null;

                                                if (selectedContactsList.get(j).isGroup()) {

                                                    if (groupInfoSession.hasGroupInfo(selectedContactsList.get(j).get_id())) {
                                                        GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(selectedContactsList.get(j).get_id());
                                                        convId = infoPojo.getGroupId();

                                                    }
                                                } else {
                                                    if (userInfoSession.hasChatConvId(selectedContactsList.get(j).get_id())) {
                                                        convId = userInfoSession.getChatConvId(selectedContactsList.get(j).get_id());
                                                    }
                                                }


                                                VideoMessage message = (VideoMessage) MessageFactory.getMessage(MessageFactory.video, this);

                                                if (selectedContactsList.get(j).isGroup()) {
                                                    message.getGroupMessageObject(to1, videoPath, groupname1);
                                                } else {
                                                    message.getMessageObject(to1, videoPath, false);
                                                }


                                                MessageItemChat item = message.createMessageItem(true, videoPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                                        to1, "", caption, "", "");
                                                item.setSenderMsisdn(receiverMsisdn1);


                                                String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(videoPath);
                                                String videoName = item.getMessageId() + fileExtension;

                                                JSONObject uploadObj;
                                                String docId;
                                                if (selectedContactsList.get(j).isGroup()) {
                                                    item.setGroupName(groupname1);
                                                    item.setGroup(true);
                                                    docId = mCurrentUserId + "-" + to1 + "-g";
                                                    item.setVideoPath(videoPath);
                                                    uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
                                                            videoName, videoPath, groupname1, "", MessageFactory.CHAT_TYPE_GROUP, false);
                                                } else {
                                                    item.setGroup(false);
                                                    item.setConvId(convId);
                                                    docId = mCurrentUserId + "-" + to1;
                                                    uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
                                                            videoName, videoPath, "", "", MessageFactory.CHAT_TYPE_SINGLE, false);
                                                }

                                                try {
                                                    String thumbdata = getVideoThumbnail(videoPath, convId);
                                                    uploadObj.put("thumbnail_data", thumbdata);
                                                    item.setThumbnailData(thumbdata);
                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                                uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);


                                                item.setSenderMsisdn(receiverMsisdn1);
                                                item.setSenderName(groupname1);
                                                if (selectedContactsList.get(j).isGroup()) {
                                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                                                } else {
                                                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                                                }
                                            }
                                        }
                                    }
                                }
                                dirFiles = null;
                            }
                        }
                    }

                    System.out.println("===imagpathinmethod" + imgPath);
                } else {
                    imgPath = getImageFilePath(uri);
                    String uriPath = uri.toString();
                    if (uriPath.contains("content://com.android.chrome.FileProvider/downloads/")) {
                        imgPath = uriPath.replace("content://com.android.chrome.FileProvider/downloads/", "/storage/emulated/0/Download/");
                        if (imgPath.contains("%20")) {
                            imgPath = uriPath.replace("%20", " ");
                            String s1 = imgPath.replace("content://com.android.chrome.FileProvider/downloads/", "/storage/emulated/0/Download/");
                            imgPath = s1;
                            Log.e("imgPath", s1);
                        }
                        Log.e("newString", imgPath);
                    }
                    System.out.println("===imagpathinmethod" + imgPath);

                    if (imgPath != null && !imgPath.equals("")) {
                        String convId = "";


                        if (selectedContactsList.get(i).isGroup()) {

                            if (groupInfoSession.hasGroupInfo(selectedContactsList.get(i).get_id())) {
                                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(selectedContactsList.get(i).get_id());
                                convId = infoPojo.getGroupId();

                            }
                        } else {
                            if (userInfoSession.hasChatConvId(selectedContactsList.get(i).get_id())) {
                                convId = userInfoSession.getChatConvId(selectedContactsList.get(i).get_id());
                                System.out.println("====conv" + convId);

                            }
                        }

                        PictureMessage message = (PictureMessage) MessageFactory.getMessage(MessageFactory.picture, this);

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(imgPath, options);
                        int imageHeight = options.outHeight;
                        int imageWidth = options.outWidth;

                        if (selectedContactsList.get(i).isGroup()) {
                            message.getGroupMessageObject(to, imgPath, groupname);
                        } else {
                            message.getMessageObject(to, imgPath, false);
                        }

                        MessageItemChat item = message.createMessageItem(true, caption, imgPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                to, "", imageWidth, imageHeight, "", "");

                        System.out.println("===imagpath" + imgPath);
                        String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(imgPath);
                        String imgName = item.getMessageId() + fileExtension;
                        String docId;
                        JSONObject uploadObj;

                        if (selectedContactsList.get(i).isGroup()) {
                            item.setGroupName(groupname);
                            item.setGroup(true);
                            docId = mCurrentUserId + "-" + to + "-g";
                            uploadObj = (JSONObject) message.createImageUploadObject(item.getMessageId(), docId,
                                    imgName, imgPath, groupname, "", MessageFactory.CHAT_TYPE_GROUP, false);
                            item.setImagePath(imgPath);


                        } else {
                            docId = mCurrentUserId + "-" + to;
                            item.setGroup(false);
                            item.setConvId(convId);

                            uploadObj = (JSONObject) message.createImageUploadObject(item.getMessageId(), docId, imgName, imgPath,
                                    "", caption, MessageFactory.CHAT_TYPE_SINGLE, false);

                        }
                        try {
                            String thumbdata = getThumbnailData(imgPath, convId);
                            uploadObj.put("thumbnail_data", thumbdata);
                            item.setThumbnailData(thumbdata);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("=====upload" + uploadObj);
                        uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);


                        item.setSenderMsisdn(receiverMsisdn);
                        item.setSenderName(groupname);

                        if (selectedContactsList.get(i).isGroup()) {
                            db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                        } else {
                            db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                        }
                    } else {
                        Log.e("Else", "Else");
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * copy File Or Directory
     *
     * @param srcDir input value(srcDir)
     * @param dstDir input value(dstDir)
     */
    public static void copyFileOrDirectory(String srcDir, String dstDir) {

        try {
            File src = new File(srcDir);
            File dst = new File(dstDir, src.getName());

            if (src.isDirectory()) {

                String files[] = src.list();
                int filesLength = files.length;
                for (int i = 0; i < filesLength; i++) {
                    String src1 = (new File(src, files[i]).getPath());
                    String dst1 = dst.getPath();
                    copyFileOrDirectory(src1, dst1);

                }
            } else {
                copyFile(src, dst);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * copyFile path
     *
     * @param sourceFile input value (sourceFile)
     * @param destFile   input value (destFile)
     * @throws IOException error
     */
    public static void copyFile(File sourceFile, File destFile) throws IOException {
        if (!destFile.getParentFile().exists())
            destFile.getParentFile().mkdirs();

        if (!destFile.exists()) {
            destFile.createNewFile();
        }

        FileChannel source = null;
        FileChannel destination = null;

        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        } finally {
            if (source != null) {
                source.close();
                Log.e("111", "1");
            }
            if (destination != null) {
                destination.close();
                Log.e("111", "2");
            }
        }
    }

    /**
     * send Document Message
     *
     * @param uri input value (uri)
     */
    private void sendDocumentMessage(Uri uri) {

        MessageDbController db = CoreController.getDBInstance(this);
        String docPath = "";


        String uriPath = uri.toString();
        if (uriPath.contains("content://com.android.chrome.FileProvider/downloads/")) {
            docPath = uriPath.replace("content://com.android.chrome.FileProvider/downloads/", "/storage/emulated/0/Download/");
            if (docPath.contains("%20")) {
                docPath = uriPath.replace("%20", " ");
                String s1 = docPath.replace("content://com.android.chrome.FileProvider/downloads/", "/storage/emulated/0/Download/");
                docPath = s1;
                Log.e("imgPath", s1);
            }
            Log.e("newString", docPath);
        } else {
            docPath = getDocumentPathFromURI(uri);
        }
        System.out.println("===imagpathinmethod" + docPath);


        if (docPath != null && !docPath.equals("")) {
            for (int i = 0; i < selectedContactsList.size(); i++) {
                String to = selectedContactsList.get(i).get_id();
                String receiverMsisdn = selectedContactsList.get(i).getNumberInDevice();
                String groupname = selectedContactsList.get(i).getFirstName();
                String convId = null;

                if (selectedContactsList.get(i).isGroup()) {

                    if (groupInfoSession.hasGroupInfo(selectedContactsList.get(i).get_id())) {
                        GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(selectedContactsList.get(i).get_id());
                        convId = infoPojo.getGroupId();

                    }
                } else {
                    if (userInfoSession.hasChatConvId(selectedContactsList.get(i).get_id())) {
                        convId = userInfoSession.getChatConvId(selectedContactsList.get(i).get_id());
                    }
                }


                DocumentMessage message = (DocumentMessage) MessageFactory.getMessage(MessageFactory.document, this);

                if (selectedContactsList.get(i).isGroup()) {
                    message.getGroupMessageObject(to, docPath, groupname);
                } else {
                    message.getMessageObject(to, docPath, false);
                }

                MessageItemChat item = message.createMessageItem(true, docPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                        to, "", "", "");


                String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(docPath);
                String docName = item.getMessageId() + fileExtension;
                String docId;
                JSONObject uploadObj;

                if (selectedContactsList.get(i).isGroup()) {
                    item.setGroupName(groupname);
                    item.setGroup(true);
                    docId = mCurrentUserId + "-" + to + "-g";

                    uploadObj = (JSONObject) message.createDocUploadObject(item.getMessageId(), docId,
                            docName, docPath, groupname, MessageFactory.CHAT_TYPE_GROUP, false);

                } else {
                    item.setGroup(false);
                    item.setConvId(convId);
                    docId = mCurrentUserId + "-" + to;
                    uploadObj = (JSONObject) message.createDocUploadObject(item.getMessageId(), docId,
                            docName, docPath, "", MessageFactory.CHAT_TYPE_SINGLE, false);
                }
                uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);


                item.setSenderMsisdn(receiverMsisdn);
                item.setSenderName(groupname);

                if (selectedContactsList.get(i).isGroup()) {
                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                } else {
                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                }

            }
        }
    }

    /**
     * send Video ChatMessage
     *
     * @param uri     input value (uri)
     * @param caption input value (caption)
     */
    private void sendVideoChatMessage(Uri uri, String caption) {
        MessageDbController db = CoreController.getDBInstance(this);
        Log.e("====path", "====path" + uri);
        String videoPath1 = uri.toString();
        if (videoPath1.contains("content://com.google.android.apps.photos.contentprovider")) {
//            if (videoPath1.contains(".mp4")) {
//                Uri myUri = Uri.parse(videoPath1);
//                String videoPath = getVideoFilePath(myUri);
//                if (videoPath != null && !videoPath.equals("")) {
//
//                    for (int j = 0; j < selectedContactsList.size(); j++) {
//                        String to1 = selectedContactsList.get(j).get_id();
//                        String receiverMsisdn1 = selectedContactsList.get(j).getNumberInDevice();
//                        String groupname1 = selectedContactsList.get(j).getFirstName();
//                        String convId = null;
//
//                        if (selectedContactsList.get(j).isGroup()) {
//
//                            if (groupInfoSession.hasGroupInfo(selectedContactsList.get(j).get_id())) {
//                                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(selectedContactsList.get(j).get_id());
//                                convId = infoPojo.getGroupId();
//
//                            }
//                        } else {
//                            if (userInfoSession.hasChatConvId(selectedContactsList.get(j).get_id())) {
//                                convId = userInfoSession.getChatConvId(selectedContactsList.get(j).get_id());
//                            }
//                        }
//
//
//                        VideoMessage message = (VideoMessage) MessageFactory.getMessage(MessageFactory.video, this);
//
//                        if (selectedContactsList.get(j).isGroup()) {
//                            message.getGroupMessageObject(to1, videoPath, groupname1);
//                        } else {
//                            message.getMessageObject(to1, videoPath, false);
//                        }
//
//
//                        MessageItemChat item = message.createMessageItem(true, videoPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                                to1, "", caption, "", "");
//                        item.setSenderMsisdn(receiverMsisdn1);
//
//
//                        String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(videoPath);
//                        String videoName = item.getMessageId() + fileExtension;
//
//                        JSONObject uploadObj;
//                        String docId;
//                        if (selectedContactsList.get(j).isGroup()) {
//                            item.setGroupName(groupname1);
//                            item.setGroup(true);
//                            docId = mCurrentUserId + "-" + to1 + "-g";
//                            item.setVideoPath(videoPath);
//                            uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
//                                    videoName, videoPath, groupname1, "", MessageFactory.CHAT_TYPE_GROUP, false);
//                        } else {
//                            item.setGroup(false);
//                            item.setConvId(convId);
//                            docId = mCurrentUserId + "-" + to1;
//                            uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
//                                    videoName, videoPath, "", "", MessageFactory.CHAT_TYPE_SINGLE, false);
//                        }
//
//                        try {
//                            String thumbdata = getVideoThumbnail(videoPath, convId);
//                            uploadObj.put("thumbnail_data", thumbdata);
//                            item.setThumbnailData(thumbdata);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
//
//
//                        item.setSenderMsisdn(receiverMsisdn1);
//                        item.setSenderName(groupname1);
//                        if (selectedContactsList.get(j).isGroup()) {
//                            db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
//                        } else {
//                            db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
//                        }
//                    }
//                }
//            }
//            File photoDirectory = new File(Environment.getExternalStorageDirectory() + "/aChat/");
//            File photoDirectory1 = new File(Environment.getExternalStorageDirectory() + "/aChat1/");

            File photoDirectory = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH);
            File photoDirectory1 = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH_BACKUP);

            String soc = photoDirectory.getAbsolutePath();
            String des = photoDirectory1.getAbsolutePath();
            copyFileOrDirectory(soc, des);
            //File photoDirectory = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH + "/");
            if (photoDirectory.exists()) {
                File[] dirFiles = photoDirectory.listFiles();
                if (dirFiles.length > 0) {
                    for (int ii = 0; ii < dirFiles.length; ii++) {
                        videoPath1 = dirFiles[ii].toString();
                        Log.e("dirFiles", dirFiles[ii].toString());

                        if (videoPath1.contains(".mp4")) {
                            Uri myUri = Uri.parse(videoPath1);
                            String videoPath = getVideoFilePath(myUri);
                            if (videoPath != null && !videoPath.equals("")) {

                                for (int j = 0; j < selectedContactsList.size(); j++) {
                                    String to1 = selectedContactsList.get(j).get_id();
                                    String receiverMsisdn1 = selectedContactsList.get(j).getNumberInDevice();
                                    String groupname1 = selectedContactsList.get(j).getFirstName();
                                    String convId = null;

                                    if (selectedContactsList.get(j).isGroup()) {

                                        if (groupInfoSession.hasGroupInfo(selectedContactsList.get(j).get_id())) {
                                            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(selectedContactsList.get(j).get_id());
                                            convId = infoPojo.getGroupId();

                                        }
                                    } else {
                                        if (userInfoSession.hasChatConvId(selectedContactsList.get(j).get_id())) {
                                            convId = userInfoSession.getChatConvId(selectedContactsList.get(j).get_id());
                                        }
                                    }


                                    VideoMessage message = (VideoMessage) MessageFactory.getMessage(MessageFactory.video, this);

                                    if (selectedContactsList.get(j).isGroup()) {
                                        message.getGroupMessageObject(to1, videoPath, groupname1);
                                    } else {
                                        message.getMessageObject(to1, videoPath, false);
                                    }


                                    MessageItemChat item = message.createMessageItem(true, videoPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                            to1, "", caption, "", "");
                                    item.setSenderMsisdn(receiverMsisdn1);


                                    String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(videoPath);
                                    String videoName = item.getMessageId() + fileExtension;

                                    JSONObject uploadObj;
                                    String docId;
                                    if (selectedContactsList.get(j).isGroup()) {
                                        item.setGroupName(groupname1);
                                        item.setGroup(true);
                                        docId = mCurrentUserId + "-" + to1 + "-g";
                                        item.setVideoPath(videoPath);
                                        uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
                                                videoName, videoPath, groupname1, "", MessageFactory.CHAT_TYPE_GROUP, false);
                                    } else {
                                        item.setGroup(false);
                                        item.setConvId(convId);
                                        docId = mCurrentUserId + "-" + to1;
                                        uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
                                                videoName, videoPath, "", "", MessageFactory.CHAT_TYPE_SINGLE, false);
                                    }

                                    try {
                                        String thumbdata = getVideoThumbnail(videoPath, convId);
                                        uploadObj.put("thumbnail_data", thumbdata);
                                        item.setThumbnailData(thumbdata);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);


                                    item.setSenderMsisdn(receiverMsisdn1);
                                    item.setSenderName(groupname1);
                                    if (selectedContactsList.get(j).isGroup()) {
                                        db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                                    } else {
                                        db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                                    }
                                }
                            }
                        }


                    }
                    dirFiles = null;
                }
            }
        } else {
            String videoPath = "";
            String uriPath = uri.toString();
            if (uriPath.contains("content://com.android.chrome.FileProvider/downloads/")) {
                videoPath = uriPath.replace("content://com.android.chrome.FileProvider/downloads/", "/storage/emulated/0/Download/");
                if (videoPath.contains("%20")) {
                    videoPath = uriPath.replace("%20", " ");
                    String s1 = videoPath.replace("content://com.android.chrome.FileProvider/downloads/", "/storage/emulated/0/Download/");
                    videoPath = s1;
                    Log.e("imgPath", s1);
                }
                Log.e("newString", videoPath);
            } else {
                videoPath = getVideoFilePath(uri);
            }


            if (videoPath != null && !videoPath.equals("")) {

                for (int i = 0; i < selectedContactsList.size(); i++) {
                    String to = selectedContactsList.get(i).get_id();
                    String receiverMsisdn = selectedContactsList.get(i).getNumberInDevice();
                    String groupname = selectedContactsList.get(i).getFirstName();
                    String convId = null;

                    if (selectedContactsList.get(i).isGroup()) {

                        if (groupInfoSession.hasGroupInfo(selectedContactsList.get(i).get_id())) {
                            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(selectedContactsList.get(i).get_id());
                            convId = infoPojo.getGroupId();

                        }
                    } else {
                        if (userInfoSession.hasChatConvId(selectedContactsList.get(i).get_id())) {
                            convId = userInfoSession.getChatConvId(selectedContactsList.get(i).get_id());
                        }
                    }


                    VideoMessage message = (VideoMessage) MessageFactory.getMessage(MessageFactory.video, this);

                    if (selectedContactsList.get(i).isGroup()) {
                        message.getGroupMessageObject(to, videoPath, groupname);
                    } else {
                        message.getMessageObject(to, videoPath, false);
                    }


                    MessageItemChat item = message.createMessageItem(true, videoPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                            to, "", caption, "", "");
                    item.setSenderMsisdn(receiverMsisdn);


                    String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(videoPath);
                    String videoName = item.getMessageId() + fileExtension;

                    JSONObject uploadObj;
                    String docId;
                    if (selectedContactsList.get(i).isGroup()) {
                        item.setGroupName(groupname);
                        item.setGroup(true);
                        docId = mCurrentUserId + "-" + to + "-g";
                        item.setVideoPath(videoPath);
                        uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
                                videoName, videoPath, groupname, "", MessageFactory.CHAT_TYPE_GROUP, false);
                    } else {
                        item.setGroup(false);
                        item.setConvId(convId);
                        docId = mCurrentUserId + "-" + to;
                        uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
                                videoName, videoPath, "", "", MessageFactory.CHAT_TYPE_SINGLE, false);
                    }

                    try {
                        String thumbdata = getVideoThumbnail(videoPath, convId);
                        uploadObj.put("thumbnail_data", thumbdata);
                        item.setThumbnailData(thumbdata);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);


                    item.setSenderMsisdn(receiverMsisdn);
                    item.setSenderName(groupname);
                    if (selectedContactsList.get(i).isGroup()) {
                        db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                    } else {
                        db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                    }
                }
            }
        }

    }

    /**
     * send AudioMessage
     *
     * @param uri input value (uri)
     */
    private void sendAudioMessage(Uri uri) {
        String audioPath = "";
        String uriPath = uri.toString();
        if (uriPath.contains("content://com.android.chrome.FileProvider/downloads/")) {
            audioPath = uriPath.replace("content://com.android.chrome.FileProvider/downloads/", "/storage/emulated/0/Download/");
            if (audioPath.contains("%20")) {
                audioPath = uriPath.replace("%20", " ");
                String s1 = audioPath.replace("content://com.android.chrome.FileProvider/downloads/", "/storage/emulated/0/Download/");
                audioPath = s1;
                Log.e("imgPath", s1);
            }
            Log.e("newString", audioPath);
            uri = Uri.parse(audioPath);
        } else {
            audioPath = getAudioFilePath(uri);
        }
        System.out.println("===imagpathinmethod" + audioPath);


        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(ShareFromThirdPartyAppActivity.this, uri);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            duration = getTimeString(Long.parseLong(duration));

            MessageDbController db = CoreController.getDBInstance(this);


            if (audioPath != null && !audioPath.equals("")) {

                for (int i = 0; i < selectedContactsList.size(); i++) {

                    String convId = null;

                    if (selectedContactsList.get(i).isGroup()) {

                        if (groupInfoSession.hasGroupInfo(selectedContactsList.get(i).get_id())) {
                            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(selectedContactsList.get(i).get_id());
                            convId = infoPojo.getGroupId();

                        }
                    } else {
                        if (userInfoSession.hasChatConvId(selectedContactsList.get(i).get_id())) {
                            convId = userInfoSession.getChatConvId(selectedContactsList.get(i).get_id());
                        }
                    }
                    String to = selectedContactsList.get(i).get_id();
                    String receiverMsisdn = selectedContactsList.get(i).getNumberInDevice();
                    String groupname = selectedContactsList.get(i).getFirstName();


                    AudioMessage message = (AudioMessage) MessageFactory.getMessage(MessageFactory.audio, this);
                    if (selectedContactsList.get(i).isGroup()) {
                        message.getGroupMessageObject(to, audioPath, groupname);
                    } else {
                        message.getMessageObject(to, audioPath, false);
                    }


                    MessageItemChat item = message.createMessageItem(true, audioPath, duration, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                            to, "", MessageFactory.AUDIO_FROM_ATTACHMENT, "", "");

                    String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(audioPath);
                    String audioName = item.getMessageId() + fileExtension;
                    String docId;
                    if (selectedContactsList.get(i).isGroup()) {

                        docId = mCurrentUserId + "-" + to + "-g";
                    } else {
                        docId = mCurrentUserId + "-" + to;
                    }


                    JSONObject uploadObj;
                    if (selectedContactsList.get(i).isGroup()) {
                        item.setGroupName(groupname);
                        item.setGroup(true);
                        uploadObj = (JSONObject) message.createAudioUploadObject(item.getMessageId(), docId, audioName, audioPath,
                                duration, groupname, MessageFactory.AUDIO_FROM_ATTACHMENT, MessageFactory.CHAT_TYPE_GROUP, false);
                    } else {
                        item.setGroup(false);
                        item.setConvId(convId);
                        uploadObj = (JSONObject) message.createAudioUploadObject(item.getMessageId(), docId,
                                audioName, audioPath, duration, "", MessageFactory.AUDIO_FROM_ATTACHMENT,
                                MessageFactory.CHAT_TYPE_SINGLE, false);
                    }

                    uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
                    item.setSenderMsisdn(receiverMsisdn);
                    item.setSenderName(groupname);
                    item.setaudiotype(MessageFactory.AUDIO_FROM_ATTACHMENT);

                    if (selectedContactsList.get(i).isGroup()) {
                        db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
                    } else {
                        db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * get TimeString
     *
     * @param millis input value (millis)
     * @return value
     */
    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf.append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    /**
     * get image ThumbnailData
     *
     * @param imgPath input value (imgPath)
     * @param id      input value (id)
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
     * get video ThumbnailData
     *
     * @param videoPath input value (videoPath)
     * @param id        input value (id)
     * @return value
     */
    private String getVideoThumbnail(String videoPath, String id) {
        String thumbData = null;
        byte[] thumbArray = new byte[0];
        try {


            Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            thumbBmp.compress(Bitmap.CompressFormat.PNG, 10, out);
            thumbArray = out.toByteArray();

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
     * google photo provider
     *
     * @param uri input value (uri)
     * @return value
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    /**
     * get Document Path From URI
     *
     * @param uri input value (uri)
     * @return value
     */
    public String getDocumentPathFromURI(Uri uri) {
        String path = null;

        try {
            if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                System.out.println("====pathindb" + id);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

            }

            String[] filePathColumn = {MediaStore.Files.FileColumns.DATA};
            System.out.println("====pathindb" + filePathColumn);

            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                System.out.println("====pathindb" + columnIndex);
                path = cursor.getString(columnIndex);
                System.out.println("====pathindb" + path);
                cursor.close();
                if (path == null) {

                    System.out.println("====here in null");
                    path = FileUploadDownloadManager.getDocumentRealFilePath(ShareFromThirdPartyAppActivity.this, uri);
                    System.out.println("====path" + path);
                }
            } else {
                path = uri.getPath();
                System.out.println("====pathindbelse" + path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }
//    private String getImageFilePath(Uri uri) {
//        String path = null;
//        try {
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
//            if (cursor != null) {
//                try {
//                    cursor.moveToFirst();
//                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                    path = cursor.getString(columnIndex);
//                }
//                catch (Exception e){
//                    Log.e(TAG, "getImageFilePath: ",e );
//                }
//                finally {
//                    cursor.close();
//                }
//
//                if (path == null) {
//                    path = FileUploadDownloadManager.getImageRealFilePath(ShareFromThirdPartyAppActivity.this, uri);
//                }
//            } else {
//                path = uri.getPath();
//            }
//        }
//        catch (Exception e){
//            Log.e(TAG, "getImageFilePath: ",e );
//        }
//        return path;
//    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * get Audio FilePath
     *
     * @param uri input value (uri)
     * @return value
     */
    private String getAudioFilePath(Uri uri) {
        String path = null;

        String[] filePathColumn = {MediaStore.Audio.Media.DATA};
        System.out.println("====pathindb" + filePathColumn);
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            System.out.println("====pathindb" + path);
            cursor.close();
            if (path == null) {
                path = FileUploadDownloadManager.getAudioRealFilePath(ShareFromThirdPartyAppActivity.this, uri);
            }
        } else {
            path = uri.getPath();
            System.out.println("====pathindbelse" + path);
        }

        return path;
    }

    /**
     * get Video FilePath
     *
     * @param uri input value (uri)
     * @return value
     */
    private String getVideoFilePath(Uri uri) {
        String path = null;

        String[] filePathColumn = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            cursor.close();

            if (path == null) {
                path = FileUploadDownloadManager.getVideoRealFilePath(ShareFromThirdPartyAppActivity.this, uri);
                Log.e("====path", "====path" + path);
            }
        } else {
            path = uri.getPath();
        }

        return path;
    }

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (Intent.ACTION_SEND.equals(getIntent().getAction()) || Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction())
                && null != data) {

            Uri selectedImageUri = data.getData();
            String tempPath = getPath(selectedImageUri, this);
            String url = data.getData().toString();
            if (url.startsWith("content://com.google.android.apps.photos.content")) {
                try {
                    InputStream is = getContentResolver().openInputStream(selectedImageUri);
                    if (is != null) {
                        Bitmap pictureBitmap = BitmapFactory.decodeStream(is);

                    }
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            } else {
                // startCrop(tempPath);

            }
        }

    }*/
    /*private String getFilePath(Uri uri, int messageType) {

        String path = null;

        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            cursor.close();

            if (path == null) {
                switch (messageType) {

                    case MessageFactory.picture: {
                        path = FileUploadDownloadManager.getImageRealFilePath(ShareFromThirdPartyAppActivity.this, uri);
                    }
                    break;

                    case MessageFactory.document: {
                        path = uri.getLocalPath();
                    }
                    break;

                    case MessageFactory.audio: {
                        path = FileUploadDownloadManager.getAudioRealFilePath(ShareFromThirdPartyAppActivity.this, uri);
                    }
                    break;

                    case MessageFactory.video: {
                        path = FileUploadDownloadManager.getVideoRealFilePath(ShareFromThirdPartyAppActivity.this, uri);
                    }
                    break;
                }
            }
        } else {
            path = uri.getLocalPath();
        }

        return path;
    }*/


    /**
     * Google photo uri Authority
     *
     * @param uriAuthority input value(uriAuthority)
     * @return value
     */
    private boolean isGooglePhotoDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.google.android.apps.photos.content".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    private String getUriRealPath(Context ctx, Uri uri) {
        String ret = "";

        if (isAboveKitKat()) {
            // Android OS above sdk version 19.
            ret = getUriRealPathAboveKitkat(ctx, uri);
            imagePath = ret;
            Log.e("IMAGE", ret);
        } else {
            // Android OS below sdk version 19
            ret = getImageRealPath(getContentResolver(), uri, null);
            Log.e("IMAGE", ret);
        }

        return ret;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Intent.ACTION_SEND_MULTIPLE.equals(shareAction)) {

            System.out.println("======elseif" + shareAction);

            ArrayList<Parcelable> list = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            Log.e("googlepic", list + "");
        }

    }

    /**
     * Check the OS based pass the uri value
     *
     * @param ctx current activity
     * @param uri input value(uri)
     * @return value
     */
    private String getUriRealPathAboveKitkat(Context ctx, Uri uri) {
        String ret = "";

        if (ctx != null && uri != null) {

            if (isContentUri(uri)) {
                // return FileUtils.getPath(this, uri);
                if (isGooglePhotoDoc(uri.getAuthority())) {
                    ret = uri.getLastPathSegment();
                } else {
                    ret = getImageRealPath(getContentResolver(), uri, null);
                }
            } else if (isFileUri(uri)) {
                ret = uri.getPath();
            } else if (isDocumentUri(ctx, uri)) {

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if (isMediaDoc(uriAuthority)) {
                    String idArr[] = documentId.split(":");
                    if (idArr.length == 2) {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if ("image".equals(docType)) {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(docType)) {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(docType)) {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(getContentResolver(), mediaContentUri, whereClause);
                    }

                } else if (isDownloadDoc(uriAuthority)) {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getImageRealPath(getContentResolver(), downloadUriAppendId, null);

                } else if (isExternalStoreDoc(uriAuthority)) {
                    String idArr[] = documentId.split(":");
                    if (idArr.length == 2) {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if ("primary".equalsIgnoreCase(type)) {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /**
     * Check whether current android os version is bigger than kitkat or not.
     *
     * @return value
     */
    private boolean isAboveKitKat() {
        boolean ret = false;
        ret = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        return ret;
    }

    /**
     * Check whether this uri represent a document or not
     *
     * @param ctx current activity
     * @param uri input value(uri)
     * @return value
     */
    private boolean isDocumentUri(Context ctx, Uri uri) {
        boolean ret = false;
        if (ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }


    /**
     * Check whether this uri is a content uri or not.
     *
     * @param uri input value(uri)
     * @return value
     */
    private boolean isContentUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("content".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     *  @param uri input value(uri)
     * */
    private boolean isFileUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("file".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /**
     * Check whether this document is provided by ExternalStorageProvider.
     *
     * @param uriAuthority input value(uriAuthority)
     * @return value
     */
    private boolean isExternalStoreDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.externalstorage.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }


    /**
     * Check whether this document is provided by DownloadsProvider.
     *
     * @param uriAuthority input value(uriAuthority)
     * @return value
     */
    private boolean isDownloadDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.providers.downloads.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /**
     * Check whether this document is provided by MediaProvider.
     *
     * @param uriAuthority input value(uriAuthority)
     * @return value
     */
    private boolean isMediaDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.providers.media.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }


    /**
     * Return uri represented document file real local path.
     *
     * @param contentResolver input value(contentResolver)
     * @param uri             input value(uri)
     * @param whereClause     input value(whereClause)
     * @return value
     */
    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Images.Media.DATA;
                } else if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Audio.Media.DATA;
                } else if (uri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }


    private String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {
                    MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(
                    MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            Log.e("Path", "getRealPathFromURI Exception : " + e.toString());
            return "";
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        Bitmap image = null;
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}