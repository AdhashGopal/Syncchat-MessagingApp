package com.chatapp.android.app.contactlist;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
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
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.chatapp.android.R;
import com.chatapp.android.app.BroadcastListActivity;
import com.chatapp.android.app.GroupCreation;
import com.chatapp.android.app.adapter.RItemAdapter;
import com.chatapp.android.app.dialog.CustomAlertDialog;
import com.chatapp.android.app.utils.BlockUserUtils;
import com.chatapp.android.app.utils.BroadcastInfoSession;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.message.GroupEventInfoMessage;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.BroadcastInfoPojo;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.service.Constants;
import com.chatapp.android.core.service.ServiceRequest;
import com.chatapp.android.core.socket.SocketManager;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.omadahealth.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class SearchPeopleForGroupChat extends AppCompatActivity {


    RecyclerView lvContacts;
    AvnNextLTProRegTextView contact_empty_selectgroup;
    TextView no_text;
    ImageView backarrow, backButton;
    HorizontalScrollView selectgroupmember;
    LinearLayout mainlayout;
    AvnNextLTProRegTextView selectcontactmember;
    AvnNextLTProDemiTextView selectcontact;
    Button doneButton;
    EditText etSearch;
    List<Map<String, String>> mylist = new ArrayList<>();

    ArrayList<ChatappContactModel> chatappContactModels = new ArrayList<>();
    ArrayList<String> myMemberList = new ArrayList<>();
    private ProgressDialog progressDialog;
    private EditText mSearchEt;
    private List<ChatappContactModel> contactListData = new ArrayList<ChatappContactModel>();
    private AddGroupChatAdapter adapter;
    private ProgressDialog dialog;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private SearchView searchView;
    private SessionManager sessionManager;
    private ImageView search;
    private Map<String, String> mycontact = new HashMap();
    private ArrayList<String> member = new ArrayList<>();
    private String mCurrentUserId;
    private Getcontactname getcontactname;
    private String searchKey = "", mGroupId = "", mGroupName = "";
    private int page = 0;
    private boolean isHasNextPage;
    private AlertDialog loaderDialog;
    private ContactDB_Sqlite contactDB_sqlite;


    /**
     * Contact api response
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
//                            if (jArray.getJSONObject(i).getJSONObject("Privacy").getString("profile_photo").equalsIgnoreCase("everyone"))
                            contactSearchModel.setAvatarImageUrl(jArray.getJSONObject(i).getString("AvatarImageUrl"));
//                            else
//                                contactSearchModel.setAvatarImageUrl("");
//                            if (jArray.getJSONObject(i).getJSONObject("Privacy").getString("status").equalsIgnoreCase("everyone")) {
                            contactSearchModel.setStatus(jArray.getJSONObject(i).getString("Status"));

//                            } else {
//                                contactSearchModel.setStatus("");
//                            }
                            if (member.size() > 0) {
                                if (myMemberList != null) {
                                    if (!myMemberList.contains(jArray.getJSONObject(i).getString("id"))) {
                                        contactListData.add(contactSearchModel);
                                    }
                                } else {
                                    if (!member.contains(jArray.getJSONObject(i).getString("id")))
                                        contactListData.add(contactSearchModel);
                                }

                            } else {

                                if (myMemberList != null) {
                                    if (!myMemberList.contains(jArray.getJSONObject(i).getString("id"))) {
                                        contactListData.add(contactSearchModel);
                                    }
                                } else {
                                    contactListData.add(contactSearchModel);
                                }

                            }

                        }
                    }

                    adapter = new AddGroupChatAdapter(SearchPeopleForGroupChat.this, contactListData);
                    lvContacts.setAdapter(adapter);
                    lvContacts.setVisibility(View.VISIBLE);
                    contact_empty_selectgroup.setVisibility(View.GONE);
                    swipyRefreshLayout.setRefreshing(false);
                    if (contactListData.size() > 2)
                        lvContacts.scrollToPosition(contactListData.size() - 1);
                } else if (jsonObject.getInt("Status") == -1) {
                    lvContacts.setVisibility(View.GONE);
                    swipyRefreshLayout.setRefreshing(false);
                    contact_empty_selectgroup.setText(jsonObject.getString("Message"));
                } else {
                    swipyRefreshLayout.setRefreshing(false);
                    Toast.makeText(SearchPeopleForGroupChat.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                showLoaderDialog(false);
                swipyRefreshLayout.setRefreshing(false);
                Toast.makeText(SearchPeopleForGroupChat.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public void onErrorListener(int state) {
            showLoaderDialog(false);
        }
    };
    private Toolbar toolbar;
    private SwipyRefreshLayout swipyRefreshLayout;

    /**
     * setup for ProgressDialog
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
     * show LoaderDialog
     *
     * @param show based on boolean value LoaderDialog shown or not
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
     * Call Contact Search API
     *
     * @param searchKey search value
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_group);
        swipyRefreshLayout = findViewById(R.id.swipyrefreshlayout);
        swipyRefreshLayout.setColorSchemeResources(R.color.tabFourColor);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

//        setSupportActionBar(toolbar);

        getSupportActionBar().setSubtitle("Add Participant");


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.arrow_left));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* Get the participant Provider to get the list of participants */

        contact_empty_selectgroup = (AvnNextLTProRegTextView) findViewById(R.id.contact_empty_selectgroup);
        ArrayList<ChatappContactModel> chatappEntries = new ArrayList<>();


        lvContacts = (RecyclerView) findViewById(R.id.listContactsgroup);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(SearchPeopleForGroupChat.this.getApplicationContext());
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


        swipyRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                Log.d("MainActivity", "Refresh triggered at "
                        + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));


                if (isHasNextPage) {
                    CallContactSearchAPI(searchKey);
                } else {
                    swipyRefreshLayout.setRefreshing(false);
                    Toast.makeText(SearchPeopleForGroupChat.this, "No more contacts", Toast.LENGTH_SHORT).show();
                }

            }
        });

        try {
            getProgressDialogInstance();
            Bundle bundle = getIntent().getExtras();
            mGroupId = bundle.getString("GroupId", "");
            mGroupName = bundle.getString("GroupName", "");
            myMemberList = (ArrayList<String>) getIntent().getSerializableExtra("myGroupList");

            if (TextUtils.isEmpty(mGroupName)) {
                getSupportActionBar().setTitle("New Group");
            } else {
                getSupportActionBar().setTitle(mGroupName);
                contact_empty_selectgroup.setText("Add contacts to " + mGroupName + " group");
            }

            contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        } catch (Exception e) {

            e.printStackTrace();
        }


        lvContacts.setAdapter(adapter);
        lvContacts.setVisibility(View.VISIBLE);
        contact_empty_selectgroup.setVisibility(View.GONE);
        doneButton.setVisibility(View.VISIBLE);
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

                        if (getIntent().getIntExtra("member", 0) == 1) {
                            performAddMemberGroup();

                        } else {
                            Intent intent = new Intent(SearchPeopleForGroupChat.this, GroupCreation.class);
                            intent.putStringArrayListExtra("mylist", member);
                            startActivity(intent);
                        }

                    } else {

                        try {
                            String currentUserId = SessionManager.getInstance(SearchPeopleForGroupChat.this).getCurrentUserID();


                            String groupId = getSaltString();
                            String docId = currentUserId.concat("-").concat(groupId).concat("-g");

                            BroadcastInfoSession broadcastInfoSession = new BroadcastInfoSession(SearchPeopleForGroupChat.this);

                            BroadcastInfoPojo infoPojo = new BroadcastInfoPojo();
                            infoPojo.setBroadcastId(groupId);
                            infoPojo.setCreatedBy(currentUserId);
                            infoPojo.setAvatarPath("");
//                        infoPojo.setContactsModel(chatappContactModels);
                            infoPojo.setContactsModel(chatappContactModels);

                            infoPojo.setBroadcastName(member.size() + " Receiptents");
                            infoPojo.setGroupMembers(TextUtils.join(",", member));
                            infoPojo.setAdminMembers(currentUserId);
                            infoPojo.setLiveGroup(true);
                            infoPojo.setbroadcast(true);
                            broadcastInfoSession.updateGroupInfo(docId, infoPojo);

                            finish();
                            startActivity(new Intent(SearchPeopleForGroupChat.this, BroadcastListActivity.class));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                } else {
                    Toast.makeText(SearchPeopleForGroupChat.this, "Please select atleast one member", Toast.LENGTH_LONG).show();
                }
            }
        });


        lvContacts.addOnItemTouchListener(new RItemAdapter(
                SearchPeopleForGroupChat.this, lvContacts, new RItemAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                ChatappContactModel selectedContact = adapter.getItem(position);


                performAddMember(view, selectedContact);

            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        }));

        /*lvContacts.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        } else {
                            Toast.makeText(SearchPeopleForGroupChat.this, "No more contacts", Toast.LENGTH_SHORT).show();
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });*/

        setupProgressDialog();
        FavListCall();
    }

    /**
     * get groupid value
     *
     * @return value
     */
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

    /**
     * AddMember function
     *
     * @param view            add member in layout
     * @param selectedContact select value from model response
     */
    private void performAddMember(View view, ChatappContactModel selectedContact) {
        String userId = selectedContact.get_id();
        String name = selectedContact.getMsisdn();


//        contactDB_sqlite.insertUser_Details(selectedContact.get_id(),selectedContact.getFirstName(),selectedContact.getAvatarImageUrl(),selectedContact.getStatus());


        if (!contactDB_sqlite.getBlockedStatus(userId, false).equals("1")) {
           /* if (TextUtils.isEmpty(mGroupId)) {
                addContactToGroup(view, selectedContact);
            } else {
                performAddMemberGroup(userId);
            }*/
            getcontactname.userDetails(userId, selectedContact);
            addContactToGroup(view, selectedContact);
        } else {
//            Getcontactname getcontactname = new Getcontactname(this);
            String message = "Unblock" + " " + selectedContact.getFirstName() + " to add group?";
            displayAlert(message, userId);
        }
    }

    /**
     * displayAlert for unblock user
     *
     * @param txt    text value based on response
     * @param userId specific userid
     */
    private void displayAlert(final String txt, final String userId) {
        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setMessage(txt);
        dialog.setNegativeButtonText("Cancel");
        dialog.setPositiveButtonText("Unblock");
        dialog.setCancelable(false);
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                BlockUserUtils.changeUserBlockedStatus(SearchPeopleForGroupChat.this, EventBus.getDefault(),
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

    private String decodeBase64(String encodedStatus) {
        return new String(Base64.decode(encodedStatus, Base64.DEFAULT));
    }

    /**
     * addContact list
     *
     * @param selectedItem selected user
     * @param memberId     user id
     * @param myMAp        add member in new arraylist
     * @param inflater     layout binding
     */
    private void addContact(ChatappContactModel selectedItem, String memberId, Map<String, String> myMAp, LayoutInflater inflater) {
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
//        ChatappContactModel info = contactDB_sqlite.getUserDetails(selectedItem.get_id());
//        getcontactname.configProfilepic(image, selectedItem.get_id(), true, false, R.mipmap.chat_attachment_profile_default_image_frame);
//        Picasso.with(this).load(Constants.SOCKET_IP + info.getAvatarImageUrl()).error(R.mipmap.chat_attachment_profile_default_image_frame).into(image);

//TODO tharani map
        Glide.with(this).load(Constants.SOCKET_IP + selectedItem.getAvatarImageUrl()).thumbnail(0.1f)
                .dontAnimate()
                .error(R.mipmap.chat_attachment_profile_default_image_frame)
                .dontTransform()
                .into(image);

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
                        contact.setFavouriteStatus(Integer.parseInt(mylist.get(i).get("Fav_type")));

                        contactListData.add(0, contact);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                        mylist.remove(i);
                        member.remove(i);
                        if (mylist.size() == 0) {
                            FavListCall();
                        } else {
                            Log.e("", mylist.size() + "");
                        }
                        break;
                    }

                }

            }
        });

        mainlayout.addView(view1);

        int index = contactListData.indexOf(selectedItem);
        if (index > -1) {
            contactListData.remove(index);
        }

        etSearch.setText("");
        Collections.sort(contactListData, new Comparator<ChatappContactModel>() {
            @Override
            public int compare(ChatappContactModel o1, ChatappContactModel o2) {
                return o1.getFirstName().compareToIgnoreCase(o2.getFirstName());
            }
        });
        adapter.notifyDataSetChanged();
    }

    /**
     * addContact To Group
     *
     * @param view         layout binding
     * @param selectedItem selected user getting from model class
     */
    private void addContactToGroup(View view, ChatappContactModel selectedItem) {
        CheckBox c = (CheckBox) view.findViewById(R.id.selectedmember);

        LayoutInflater inflater = (LayoutInflater) SearchPeopleForGroupChat.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final Map<String, String> myMAp = new HashMap<>();
        myMAp.clear();

        myMAp.put("receiverUid", selectedItem.getMsisdn());
        myMAp.put("receiverName", selectedItem.getFirstName());
        myMAp.put("documentId", selectedItem.get_id());
        myMAp.put("Username", selectedItem.getFirstName());
        myMAp.put("Image", selectedItem.getAvatarImageUrl());
        myMAp.put("Status", selectedItem.getStatus());
        myMAp.put("id", selectedItem.get_id());
        myMAp.put("Fav_type", String.valueOf(selectedItem.getFavouriteStatus()));

        String memberId = selectedItem.get_id();

        if (memberId != null && !memberId.equals("")) {

            if (member.contains(memberId)) {
                Toast.makeText(this, "Sorry this participant already added", Toast.LENGTH_SHORT).show();
            } else if (myMemberList != null) {
                if (myMemberList.contains(memberId)) {
                    Toast.makeText(this, "Sorry this participant already added", Toast.LENGTH_SHORT).show();
                } else {
                    addContact(selectedItem, memberId, myMAp, inflater);
                }
            } else {
                addContact(selectedItem, memberId, myMAp, inflater);
            }
        }
    }


    /**
     * CreateOptions Menu view & search contact user name
     *
     * @param menu menu layout binding
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

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                FavListCall();
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setIconifiedByDefault(true);
                searchView.setQuery("", false);
                searchView.clearFocus();
                searchView.setIconified(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("") && newText.isEmpty()) {
                    searchView.clearFocus();
                }
                contactListData.clear();
                page = 0;
                lvContacts.setVisibility(View.GONE);
                contact_empty_selectgroup.setVisibility(View.VISIBLE);
                searchKey = newText;

                return false;
            }
        });

//        searchView.setIconifiedByDefault(true);
//        searchView.setQuery("", false);
//        searchView.clearFocus();
//        searchView.setIconified(true);

        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        searchTextView.setTextColor(Color.WHITE);
        searchTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);

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

        searchTextView.addTextChangedListener(filterTextWatcher);

        searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(searchTextView);
                   /* if (searchKey.length() >= 1) {
                        if (contactListData.size() == 0)
                            CallContactSearchAPI(searchKey);
                        hideKeyboard(searchTextView);
                    }*/ /*else {
                        Toast.makeText(SearchPeopleForGroupChat.this, "Please type atleast 3 characters to search", Toast.LENGTH_SHORT).show();
                    }*/
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
     * API call For ContactSearch
     */
    private void FavListCall() {
        contactListData.clear();
        page = 0;
        CallContactSearchAPI("");
    }


    /**
     * Hide keyboard
     *
     * @param searchTextView
     */
    public void hideKeyboard(AutoCompleteTextView searchTextView) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);
    }

    /**
     * unregisterReceiver for LocalBroadcastManager
     */
    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(SearchPeopleForGroupChat.this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }

    /**
     * finish current activity
     *
     * @param item binding menu id's
     * @return value
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        /*switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                return true;
        }*/
        return super.onOptionsItemSelected(item);
    }

    /**
     * Start EventBus function
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Stop EventBus function
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * Getting value from eventbus
     *
     * @param event based on the value to call socket (block user, group, privacy setting)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_PRIVACY_SETTINGS)) {
            loadPrivacySetting(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_BLOCK_USER)) {
            loadBlockUserMessage(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP)) {
            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                String groupAction = object.getString("groupType");

//                if (groupAction.equalsIgnoreCase(SocketManager.ACTION_NEW_ADD_GROUP_MEMBER)) {
                loadAddMemberMessage(object);
//                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Add Member Message
     *
     * @param object based on response to handle the group create
     */
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

            String senderOriginalName = "";

            if (object.has("fromuser_name")) {

                try {
                    senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }

            GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, this);
            MessageItemChat item = message.createMessageItem(MessageFactory.add_group_member, false, msg, MessageFactory.DELIVERY_STATUS_READ,
                    mGroupId, mGroupName, from, newUserId, senderOriginalName);
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

    /**
     * showProgressDialog method
     */
    public void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing() && !isFinishing())
            progressDialog.show();
    }

    /**
     * hideProgressDialog method
     */
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
            progressDialog.dismiss();
    }

    /**
     * binding the ProgressDialog function
     *
     * @return value
     */
    public ProgressDialog getProgressDialogInstance() {


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


        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.color_primary_progress_dialog));
        progressDialog.setIndeterminate(true);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return progressDialog;
    }

    /**
     * update the adapter
     *
     * @param event
     */
    private void loadPrivacySetting(ReceviceMessageEvent event) {
        adapter.notifyDataSetChanged();
    }

    /**
     * handling the block user
     *
     * @param event based on the value shown user blocked or not
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
     * make a Add Member in Group
     */
    private void performAddMemberGroup() {
        long ts = Calendar.getInstance().getTimeInMillis();
        String msgId = mCurrentUserId + "-" + mGroupId + "-g-" + ts;
        JSONArray jsArray = new JSONArray(member);
        try {
            JSONObject object = new JSONObject();
            object.put("groupType", SocketManager.ACTION_NEW_ADD_GROUP_MEMBER);
            object.put("from", SessionManager.getInstance(SearchPeopleForGroupChat.this).getCurrentUserID());
            object.put("id", ts);
            object.put("toDocId", msgId);
            object.put("groupId", mGroupId);
//            object.put("newuser", newUserId);
            object.put("groupMembers", jsArray);
//            object.put("add_new_group_name", true);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_GROUP);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
            showProgressDialog();
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

}


