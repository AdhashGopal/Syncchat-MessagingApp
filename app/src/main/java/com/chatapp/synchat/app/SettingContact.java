package com.chatapp.synchat.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.RItemAdapter;
import com.chatapp.synchat.app.adapter.SettingChatAppCASocket;
import com.chatapp.synchat.app.dialog.ChatLockPwdDialog;
import com.chatapp.synchat.app.utils.ConnectivityInfo;

import org.appspot.apprtc.util.CryptLib;

import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.ChatLockPojo;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.service.ServiceRequest;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import static android.Manifest.permission.CAMERA;

/**
 *
 */
public class SettingContact extends CoreActivity implements ContactRefreshListener, SettingChatAppCASocket.ChatListHeaderClickListener {
    private static final String TAG = SettingContact.class.getSimpleName() + ">>";
    private final int QR_REQUEST_CODE = 25;
    private final int CAMERA_RECORD_PERMISSION_REQUEST_CODE = 14;
    boolean refreshcontactsset = false;
    RecyclerView lvContacts;
    SettingChatAppCASocket adapter;
    EditText etSearch;
    RelativeLayout new_group_layout;
    AvnNextLTProDemiTextView selectcontact;
    AvnNextLTProRegTextView selectcontactmember;
    ImageView serach, overflow, backarrow, backButton;
    InputMethodManager inputMethodManager;
    Button newGroup;
    Getcontactname getcontactname;
    RelativeLayout overflowlayout, contact1_RelativeLayout;
    ChatappContactModel aModel;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    AvnNextLTProRegTextView contact_empty;
    String username, profileimage;
    String receiverDocumentID, uniqueCurrentID;
    List<ChatappContactModel> chatappEntries = new ArrayList<>();
    ContactRefreshListener contactRefreshListener;
    RelativeLayout contacts_layout;
    private EditText mSearchEt;
    private UserInfoSession userInfoSession;
    private SessionManager sessionManager;
    private SearchView searchView;
    private Menu contactMenu;
    private ProgressBar pbLoader;
    ContactsRefreshReceiver contactsRefreshReceiver = new ContactsRefreshReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);
            showProgress(false, contacts_layout, pbLoader);
            //   pbLoader.setVisibility(View.GONE);
            loadContactsFromDB();
        }
    };
    private Handler eventHandler;
    private Runnable eventRunnable;
    private LinearLayoutManager mContactListManager;
    private int mFirstVisibleItemPosition, mLastVisibleItemPosition;
    private ServiceRequest.ServiceListener verifcationListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            hideProgressDialog();

            try {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("message")) {
                    Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
                if (jsonObject.getString("errNum").equals("0")) {

                    refreshContacts();
                } else if (jsonObject.getString("errNum").equals("1")) {


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorListener(int state) {
            hideProgressDialog();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        setContentView(R.layout.contactsetting);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.nowletschat.android.contact_refresh");
        registerReceiver(contactsRefreshReceiver, intentFilter);

        contactRefreshListener = this;

        userInfoSession = new UserInfoSession(getApplicationContext());
        lvContacts = (RecyclerView) findViewById(R.id.listContacts);
        backButton = (ImageView) findViewById(R.id.backarrow_contactsetting);
        backarrow = (ImageView) findViewById(R.id.backarrow);
        contact_empty = (AvnNextLTProRegTextView) findViewById(R.id.contact_empty);
        overflow = (ImageView) findViewById(R.id.overflow);
        overflowlayout = (RelativeLayout) findViewById(R.id.overflowLayout);
        contact1_RelativeLayout = (RelativeLayout) findViewById(R.id.r1contact);
        newGroup = (Button) findViewById(R.id.newGroup);
        //new_group_layout = (RelativeLayout) findViewById(R.id.new_group_layout);
        serach = (ImageView) findViewById(R.id.search);
        etSearch = (EditText) findViewById(R.id.etSearch);
        selectcontact = (AvnNextLTProDemiTextView) findViewById(R.id.selectcontact);
        selectcontactmember = (AvnNextLTProRegTextView) findViewById(R.id.selectcontactmember);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        contacts_layout = (RelativeLayout) findViewById(R.id.contacts_layout);
        setSupportActionBar(toolbar);
        getcontactname = new Getcontactname(SettingContact.this);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP && etSearch.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                    if (event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        etSearch.setText("");
                        return false;
                    }
                }
                return false;
            }
        });

        mContactListManager = new LinearLayoutManager(SettingContact.this, LinearLayoutManager.VERTICAL, false);
        lvContacts.setLayoutManager(mContactListManager);
//        lvContacts.addOnScrollListener(contactScrollListener);

        uniqueCurrentID = SessionManager.getInstance(this).getCurrentUserID();
        sessionManager = SessionManager.getInstance(this);

        pbLoader = (ProgressBar) findViewById(R.id.pbLoader);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            Drawable drawableProgress = DrawableCompat.wrap(pbLoader.getIndeterminateDrawable());
            DrawableCompat.setTint(drawableProgress, ContextCompat.getColor(this, android.R.color.holo_green_light));
            pbLoader.setIndeterminateDrawable(DrawableCompat.unwrap(drawableProgress));
        } else {
            pbLoader.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }

        loadContactsFromDB();


//        if (!SessionManager.getInstance(this).isContactSyncFinished()) {
        refreshContacts();
        //  }

        lvContacts.addOnItemTouchListener(new RItemAdapter(this, lvContacts, new RItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != 0) {
                    position = position - 1;

                    ChatappContactModel e = adapter.getItem(position);


                    if (e.getRequestStatus().equals("3")) {
                        if (e.getStatus() != null && e.getStatus().length() > 0) {

                            ChatLockPojo lockPojo = getChatLockdetailfromDB(position);
                            if (sessionManager != null && sessionManager.getLockChatEnabled().equals("1") && lockPojo != null) {

                                String stat = "", pwd = null;
                                stat = lockPojo.getStatus();
                                pwd = lockPojo.getPassword();

                                String docID = e.get_id();
                                String documentid = uniqueCurrentID.concat("-").concat(docID);
                                if (stat.equals("1")) {
                                    openUnlockChatDialog(documentid, stat, pwd, position);
                                } else {
                                    //navigateToChatviewPage(e);
                                    getcontactname.navigateToChatviewPageforChatappModel(e);
                                }
                            } else {
                                getcontactname.navigateToChatviewPageforChatappModel(e);

                            }
                        }
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        newGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplication(), SelectPeopleForGroupChat.class);
                i.putExtra("type", "grp");
                startActivity(i);
            }
        });


        serach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showSearchActions();

                etSearch.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                        try {


                            // When user changed the Text
                            if (adapter != null) {
                                if (cs.length() > 0) {
                                    if (cs.length() == 1) {
                                        etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel_normal, 0);
                                    }
                                    SettingContact.this.adapter.getFilter().filter(cs);
                                } else {
                                    etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);

                                    List<ChatappContactModel> values = new ArrayList<ChatappContactModel>();

                                    for (int i = 0; i < chatappEntries.size(); i++) {
                                        if (!chatappEntries.get(i).getRequestStatus().equals("0")) {
                                            values.add(chatappEntries.get(i));
                                        }
                                    }

                                    adapter.updateInfo(values);
                                }
                            }
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
                        /*if (adapter != null) {
                            adapter.updateInfo(chatappEntries);
                        }*/
                        etSearch.setVisibility(View.GONE);
                        serach.setVisibility(View.VISIBLE);
                        //overflowlayout.setVisibility(View.VISIBLE);
                        selectcontactmember.setVisibility(View.VISIBLE);
                        selectcontact.setVisibility(View.VISIBLE);
                        backarrow.setVisibility(View.GONE);
                        backButton.setVisibility(View.VISIBLE);
                        hideKeyboard();
                    }
                });

                showKeyboard();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /* Variables for serch */

    }

    private void loadContactsFromDB() {
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        chatappEntries = contactDB_sqlite.getSavedChatappContacts();

        //   if (chatappEntries != null && chatappEntries.size() > 0) {
        Collections.sort(chatappEntries, Getcontactname.nameAscComparator);

        lvContacts.setVisibility(View.VISIBLE);
        contact_empty.setVisibility(View.GONE);

        List<ChatappContactModel> values = new ArrayList<ChatappContactModel>();

        for (int i = 0; i < chatappEntries.size(); i++) {
            if (!chatappEntries.get(i).getRequestStatus().equals("0")) {
                values.add(chatappEntries.get(i));
            }
        }


        adapter = new SettingChatAppCASocket(SettingContact.this, values, contactRefreshListener);

        lvContacts.setAdapter(adapter);
        adapter.setChatListHeaderListener(this);
        selectcontactmember.setText("Contacts (" + values.size() + ")");
//            notifyScrollChanged();
        if (refreshcontactsset) {
            Toast.makeText(SettingContact.this, "Your contact list has been updated", Toast.LENGTH_SHORT).show();
            refreshcontactsset = false;
        }

//            if(values.size()>0){
//                adapter = new SettingChatAppCASocket(SettingContact.this, values,contactRefreshListener);
//
//                lvContacts.setAdapter(adapter);
//                selectcontactmember.setText(values.size() + " Contacts");
////            notifyScrollChanged();
//                if (refreshcontactsset) {
//                    Toast.makeText(SettingContact.this, "Your contact list has been updated", Toast.LENGTH_SHORT).show();
//                    refreshcontactsset = false;
//                }
//            }else{
//
//                contact_empty.setVisibility(View.VISIBLE);
//                lvContacts.setVisibility(View.GONE);
//                contact_empty.setText("No Contacts");
//                selectcontactmember.setText("0 Contacts");
//            }


//
//        } else {
//            contact_empty.setVisibility(View.VISIBLE);
//            lvContacts.setVisibility(View.GONE);
//            contact_empty.setText("No Contacts");
//            selectcontactmember.setText("0 Contacts");
//        }
    }

    private String valuable(String idStr) {
        CryptLib cryptLib = null;
        try {
            cryptLib = new CryptLib();
            idStr = cryptLib.decryptCipherTextWithRandomIV(idStr, kyGn());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return idStr;

    }

    private String kyGn() {
        return getResources().getString(R.string.chatapp) + getResources().getString(R.string.chatapp) + getResources().getString(R.string.adani);
    }

    private void updateProfileImage(ReceviceMessageEvent event) {
        Object[] array = event.getObjectsArray();
        try {
            JSONObject objects = new JSONObject(array[0].toString());
            String from = objects.getString("from");
            String type = objects.getString("type");
            //String docId = uniqueCurrentID + "-" + from;
            if (chatappEntries == null)
                return;
            for (ChatappContactModel chatappContactModel : chatappEntries) {
                if (type.equalsIgnoreCase("single") && chatappContactModel.get_id().equalsIgnoreCase(from)) {
                    adapter.notifyDataSetChanged();
                }
            }


        } catch (Exception e) {
            Log.e(TAG, "updateProfileImage: ", e);
        }
    }

    private void showSearchActions() {

        backarrow.setVisibility(View.VISIBLE);
        serach.setVisibility(View.GONE);
        backButton.setVisibility(View.GONE);
        selectcontact.setVisibility(View.GONE);
        selectcontactmember.setVisibility(View.GONE);
        //contact1_RelativeLayout.setVisibility(View.GONE);
        etSearch.setVisibility(View.VISIBLE);
        etSearch.requestFocus();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        contactMenu = menu;
        getMenuInflater().inflate(R.menu.new_chat, menu);
        return super.onCreateOptionsMenu(contactMenu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard();
        switch (item.getItemId()) {

//            case R.id.menuinvitefriends:
//                AppUtils.shareApp(SettingContact.this);
//                break;

            case R.id.menucontacts:
                /*Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivity(intent);*/
//                String data = "content://contacts/people/";
//                Intent contactIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data));
//                startActivity(contactIntent);
                break;

            case R.id.menurefresh:
                if (isNetworkConnected()) {
                    refreshContacts();
                    refreshcontactsset = true;
                } else {
                    Toast.makeText(SettingContact.this, "Please check your inernet connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.menuaboutHelp:
                ActivityLauncher.launchAbouthelp(SettingContact.this);
                break;
            case R.id.chats_settings:
                ActivityLauncher.launchSettingScreen(SettingContact.this);
                return true;

            case R.id.chats_contactIcon:

                if (ConnectivityInfo.isInternetConnected(this)) {
                    if (checkCameraPermission()) {
                        /*Intent qrIntent = new Intent(this, QRCodeScan.class);
                        startActivityForResult(qrIntent, QR_REQUEST_CODE);*/
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    Toast.makeText(this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
//                Intent newIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
//                // Sets the MIME type to match the Contacts Provider
//                newIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
//                startActivity(newIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean checkCameraPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                CAMERA);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(SettingContact.this, new
                String[]{CAMERA}, CAMERA_RECORD_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CAMERA_RECORD_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean cameraPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    {
                        if (cameraPermission) {
                            /*Intent qrIntent = new Intent(this, QRCodeScan.class);
                            startActivityForResult(qrIntent, QR_REQUEST_CODE);*/
                        }
                    }

                    /*if (StoragePermission && RecordPermission) {
                        Toast.makeText(ChatViewActivity.this, "Now you can record and share audio",
.                                Toast.LENGTH_LONG).show();
                    } */
                }
                break;
        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    private void refreshContacts() {
        Log.d(TAG, "refreshContacts CLICK: ");
        if (isNetworkConnected()) {
            showProgress(true, contacts_layout, pbLoader);
        }
        //  pbLoader.setVisibility(View.VISIBLE);
        ChatappContactsService.startContactService(this, true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            String qrData = data.getStringExtra("QRData");

//            SessionManager.getInstance(this).getPhoneNumberOfCurrentUser()

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("from", SessionManager.getInstance(this).getPhoneNumberOfCurrentUser());
            params.put("to", valuable(qrData));
            params.put("status", "1");

            ServiceRequest request = new ServiceRequest(this);
            request.makeServiceRequest(Constants.CONTACT_REQUEST_SENT, Request.Method.POST, params, verifcationListener);


        }
    }

    private void openUnlockChatDialog(String docId, String status, String pwd, int position) {
        String convId = userInfoSession.getChatConvId(docId);
        ChatLockPwdDialog dialog = new ChatLockPwdDialog();
        dialog.setTextLabel1("Enter your Password");
        dialog.setEditTextdata("Enter new Password");
        dialog.setforgotpwdlabel("Forgot Password");
        dialog.setHeader("Unlock Chat");
        dialog.setButtonText("Unlock");
        Bundle bundle = new Bundle();
        bundle.putSerializable("socketitems", adapter.getItem(position));
        bundle.putString("convID", convId);
        bundle.putString("status", "1");
        bundle.putString("pwd", pwd);
        bundle.putString("page", "chatlist");
        bundle.putString("type", "single");
        bundle.putString("from", uniqueCurrentID);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "chatunLock");
    }

    private ChatLockPojo getChatLockdetailfromDB(int position) {

        ChatappContactModel e = adapter.getItem(position);
        String docID = e.get_id();
        String id = uniqueCurrentID.concat("-").concat(docID);
        MessageDbController dbController = CoreController.getDBInstance(this);
        String convId = userInfoSession.getChatConvId(id);
        String receiverId = userInfoSession.getReceiverIdByConvId(convId);
        ChatLockPojo pojo = dbController.getChatLockData(receiverId, MessageFactory.CHAT_TYPE_SINGLE);
        return pojo;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (SocketManager.EVENT_GET_CONTACTS.equalsIgnoreCase(event.getEventName())) {

        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GET_USER_DETAILS)) {
//            loadUserDetails(event.getObjectsArray()[0].toString());
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_IMAGE_UPLOAD)) {
            updateProfileImage(event);
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
        hideKeyboard();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideKeyboard();
        unregisterReceiver(contactsRefreshReceiver);
    }

    @Override
    public void onBackPressed() {
        if (etSearch.getVisibility() == View.GONE) {
            hideKeyboard();
            super.onBackPressed();
        } else {
            backarrow.performClick();
        }
    }


    private void loadUserDetails(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String userId = object.getString("id");

            try {
                for (int i = mFirstVisibleItemPosition; i <= mLastVisibleItemPosition; i++) {
                    if (chatappEntries.get(i).get_id().equalsIgnoreCase(userId)) {
                        adapter.notifyItemChanged(i);
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSuccess() {
        refreshContacts();
    }

    @Override
    public void onItemHeaderClick(int position) {
        if (position == 0) {
            if (checkCameraPermission()) {
                /*Intent qrIntent = new Intent(this, QRCodeScan.class);
                startActivityForResult(qrIntent, QR_REQUEST_CODE);*/
            } else {
                requestCameraPermission();
            }
        } else {
            Intent i = new Intent(this, SelectPeopleForGroupChat.class);
            i.putExtra("type", "grp");
            startActivity(i);
        }

    }

//    private void loadContactsFromDB() {
//        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//        chatappEntries = contactDB_sqlite.getSavedChatappContacts();
//
//        if (chatappEntries != null && chatappEntries.size() > 0) {
//            Collections.sort(chatappEntries, Getcontactname.nameAscComparator);
//
//            lvContacts.setVisibility(View.VISIBLE);
//            contact_empty.setVisibility(View.GONE);
//
//            List<ChatappContactModel> values = new ArrayList<ChatappContactModel>();
//
//            for (int i = 0; i < chatappEntries.size(); i++) {
//                if(!chatappEntries.get(i).getRequestStatus().equals("0")){
//                    values.add(chatappEntries.get(i));
//                }
//            }
//
//            if(values.size()>0){
//                adapter = new SettingChatAppCASocket(SettingContact.this, values,contactRefreshListener);
//
//                lvContacts.setAdapter(adapter);
//                selectcontactmember.setText(values.size() + " Contacts");
////            notifyScrollChanged();
//                if (refreshcontactsset) {
//                    Toast.makeText(SettingContact.this, "Your contact list has been updated", Toast.LENGTH_SHORT).show();
//                    refreshcontactsset = false;
//                }
//            }else{
//
//                contact_empty.setVisibility(View.VISIBLE);
//                lvContacts.setVisibility(View.GONE);
//                contact_empty.setText("No Contacts");
//                selectcontactmember.setText("0 Contacts");
//            }
//
//
//
//        } else {
//            contact_empty.setVisibility(View.VISIBLE);
//            lvContacts.setVisibility(View.GONE);
//            contact_empty.setText("No Contacts");
//            selectcontactmember.setText("0 Contacts");
//        }
//    }
}