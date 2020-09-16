package com.chatapp.synchat.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.ChatappCASocket;
import com.chatapp.synchat.app.adapter.RItemAdapter;
import com.chatapp.synchat.app.dialog.ChatLockPwdDialog;
import com.chatapp.synchat.app.utils.ConnectivityInfo;

import org.appspot.apprtc.util.CryptLib;

import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.core.ActivityLauncher;
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

import java.lang.reflect.Field;
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
public class SettingContactFragment extends Fragment implements ContactRefreshListener {
    private static final String TAG = SettingContactFragment.class.getSimpleName() + ">>";
    private final int CAMERA_RECORD_PERMISSION_REQUEST_CODE = 14;
    private final int QR_REQUEST_CODE = 25;
    boolean refreshcontactsset = false;
    RecyclerView lvContacts;
    ChatappCASocket adapter;
    RelativeLayout new_group_layout;
    ImageView serach, backButton;
    InputMethodManager inputMethodManager;
    Button newGroup;
    Getcontactname getcontactname;
    String deviceId;
    ChatappContactModel aModel;
    BroadcastReceiver mRegistrationBroadcastReceiver;
    AvnNextLTProRegTextView contact_empty, friends_count;
    String username, profileimage;
    String receiverDocumentID, uniqueCurrentID;
    List<ChatappContactModel> chatappEntries = new ArrayList<>();
    ContactRefreshListener contactRefreshListener;
    ProgressBar mProgressView;
    RelativeLayout contact_layout;
    ContactsRefreshReceiver contactsRefreshReceiver = new ContactsRefreshReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);

            showProgress(false);
            loadContactsFromDB();
        }
    };
    private EditText mSearchEt;
    private UserInfoSession userInfoSession;
    private SessionManager sessionManager;
    private SearchView searchView;
    private ProgressDialog progressDialog;
    private Menu contactMenu;
    private Handler eventHandler;
    private Runnable eventRunnable;
    private LinearLayoutManager mContactListManager;
    private int mFirstVisibleItemPosition, mLastVisibleItemPosition;
    private ServiceRequest.ServiceListener verifcationListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            try {


                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("message")) {
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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
            //    hideProgressDialog();

        }
    };
    private ServiceRequest.ServiceListener contactStatusListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {

            progressDialog.hide();
            deviceId = null;

            try {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("message")) {
                    Toast.makeText(getActivity(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
                if (jsonObject.getString("errNum").equals("0")) {
                    refreshContacts();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


//            if (SCLoginModel == null)
//                SCLoginModel = new SCLoginModel();
//            GsonBuilder gsonBuilder = new GsonBuilder();
//            Gson gson = gsonBuilder.create();
//            SCLoginModel = gson.fromJson(response, SCLoginModel.class);
//            otp=SCLoginModel.getCode();
//            if (SCLoginModel.getProfilePic() != null) {
//                String imgPath = SCLoginModel.getProfilePic() + "?id=" + Calendar.getInstance().getTimeInMillis();
//                SessionManager.getInstance(VerifyPhoneScreen.this).setUserProfilePic(imgPath);
//            }
//
//            if (SCLoginModel.getStatus() != null && !SCLoginModel.getStatus().equalsIgnoreCase("")) {
//                SessionManager.getInstance(VerifyPhoneScreen.this).setcurrentUserstatus(new String(Base64.decode(SCLoginModel.getStatus(), Base64.DEFAULT)));
//            } else {
//                SessionManager.getInstance(VerifyPhoneScreen.this).setcurrentUserstatus("Hey there! I am using "
//                        + getResources().getString(R.string.app_name));
//            }
//            if (SCLoginModel.get_id() != null) {
//                SessionManager.getInstance(VerifyPhoneScreen.this).setCurrentUserID(SCLoginModel.get_id());
//
//                clearDataIfUserChanged(SCLoginModel.get_id());
//            }
//            // success
//            if (SCLoginModel.getErrNum().equals("0")) {
//                SessionManager.getInstance(VerifyPhoneScreen.this).Islogedin(true);
//                String message =phoneNumber.getText().toString();
//                SessionManager.getInstance(VerifyPhoneScreen.this).setPhoneNumberOfCurrentUser(message);
//                if (SCLoginModel.getName() != null && !SCLoginModel.getName().isEmpty()) {
//                    SessionManager.getInstance(VerifyPhoneScreen.this).setnameOfCurrentUser(new String(Base64.decode(SCLoginModel.getName(), Base64.DEFAULT)));
//                }
//                SessionManager.getInstance(VerifyPhoneScreen.this).setUserCountryCode("+" + code);
//                SessionManager.getInstance(VerifyPhoneScreen.this).setUserMobileNoWithoutCountryCode(phoneNumber.getText().toString());
//
//                try {
//                    JSONObject object = new JSONObject(response);
//                    if(SessionManager.getInstance(VerifyPhoneScreen.this).getTwilioMode().equalsIgnoreCase(
//                            SessionManager.TWILIO_DEV_MODE)) {
//                        SessionManager.getInstance(VerifyPhoneScreen.this).setLoginOTP(object.getString("code"));
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                if(SKIP_OTP_VERIFICATION){
//                    new Handler().postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            skipOTP(otp);
//                        }
//                    },500);
//                }
//                else {
//                    hideProgressDialog();
//                    SharedPreferences shPref = getSharedPreferences("global_settings",
//                            MODE_PRIVATE);
//                    SharedPreferences.Editor et = shPref.edit();
//                    et.putString("userId", SessionManager.getInstance(VerifyPhoneScreen.this).getPhoneNumberOfCurrentUser());
//                    et.apply();
//                    SessionManager.getInstance(VerifyPhoneScreen.this).IsnumberVerified(true);
//                    SessionManager.getInstance(VerifyPhoneScreen.this).setLoginCount(SCLoginModel.getLoginCount());
//                    ActivityLauncher.launchProfileInfoScreen(VerifyPhoneScreen.this, SessionManager.getInstance(VerifyPhoneScreen.this).getPhoneNumberOfCurrentUser());
//
//                    //          ActivityLauncher.launchPinscreen(VerifyPhoneScreen.this, SessionManager.getInstance(VerifyPhoneScreen.this).getPhoneNumberOfCurrentUser());
//
//                    //  ActivityLauncher.launchSMSVerificationScreen(VerifyPhoneScreen.this, message, "" + code, phoneNumber.getText().toString(), otp,GCM_Id);
//                }
//            }
//            else if (SCLoginModel.getErrNum().equals("1")){
//                hideProgressDialog();
//                String error=SCLoginModel.getMessage();
//                Toast.makeText(VerifyPhoneScreen.this, error, Toast.LENGTH_SHORT).show();
//            }
        }

        @Override
        public void onErrorListener(int state) {
//            hideProgressDialog();
//            ChatappDialogUtils.showCheckInternetDialog(VerifyPhoneScreen.this);
            progressDialog.dismiss();

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.contactsettingfragment, parent, false);


        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.color_primary_progress_dialog));
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(true);


        setHasOptionsMenu(true);

        contactRefreshListener = this;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.nowletschat.android.contact_refresh");
        getActivity().registerReceiver(contactsRefreshReceiver, intentFilter);

        userInfoSession = new UserInfoSession(getActivity());
        lvContacts = (RecyclerView) rootView.findViewById(R.id.listContacts);
        mProgressView = (ProgressBar) rootView.findViewById(R.id.progress_bar);

        contact_empty = (AvnNextLTProRegTextView) rootView.findViewById(R.id.contact_empty);
        friends_count = (AvnNextLTProRegTextView) rootView.findViewById(R.id.friends_count);


        newGroup = (Button) rootView.findViewById(R.id.newGroup);
        contact_layout = (RelativeLayout) rootView.findViewById(R.id.contact_layout);


        if (deviceId != null) {
            progressDialog.show();

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("from", SessionManager.getInstance(getActivity()).getPhoneNumberOfCurrentUser());
            params.put("to", valuable(deviceId));
            params.put("status", "1");
            System.out.println("==qr" + params);
            System.out.println("===dev" + valuable(deviceId));
            ServiceRequest request = new ServiceRequest(getActivity());
            request.makeServiceRequest(Constants.CONTACT_REQUEST_SENT, Request.Method.POST, params, contactStatusListener);
        }

        newGroup.setVisibility(View.GONE);
        getcontactname = new Getcontactname(getActivity());


        mContactListManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        lvContacts.setLayoutManager(mContactListManager);
//        lvContacts.addOnScrollListener(contactScrollListener);

        uniqueCurrentID = SessionManager.getInstance(getActivity()).getCurrentUserID();
        sessionManager = SessionManager.getInstance(getActivity());


        loadContactsFromDB();


//        if (!SessionManager.getInstance(this).isContactSyncFinished()) {
        refreshContacts();
        //  }

        lvContacts.addOnItemTouchListener(new RItemAdapter(getActivity(), lvContacts, new RItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
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

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        newGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), SelectPeopleForGroupChat.class);
                i.putExtra("type", "grp");
                startActivity(i);
            }
        });


        /* Variables for serch */
        return rootView;

    }

    private void loadContactsFromDB() {
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getActivity());
        chatappEntries = contactDB_sqlite.getSavedChatappContacts();

        System.out.println("====chatlist size" + chatappEntries.size());

        if (chatappEntries != null && chatappEntries.size() > 0) {
            Collections.sort(chatappEntries, Getcontactname.nameAscComparator);

            List<ChatappContactModel> values = new ArrayList<ChatappContactModel>();

            for (int i = 0; i < chatappEntries.size(); i++) {
                if (!chatappEntries.get(i).getRequestStatus().equals("0")) {
                    values.add(chatappEntries.get(i));
                }
            }

            if (values.size() > 0) {
                adapter = new ChatappCASocket(getActivity(), values, contactRefreshListener);
                friends_count.setText(("Contacts (" + values.size() + ")"));
                lvContacts.setVisibility(View.VISIBLE);
                contact_empty.setVisibility(View.GONE);
                lvContacts.setAdapter(adapter);

//            notifyScrollChanged();
                if (refreshcontactsset) {
                    Toast.makeText(getActivity(), "Your contact list has been updated", Toast.LENGTH_SHORT).show();
                    refreshcontactsset = false;
                }
            } else {
                friends_count.setVisibility(View.GONE);
                contact_empty.setVisibility(View.VISIBLE);
                lvContacts.setVisibility(View.GONE);
                contact_empty.setText("No Contacts");
            }


        } else {
            contact_empty.setVisibility(View.VISIBLE);
            lvContacts.setVisibility(View.GONE);
            contact_empty.setText("No Contacts");
            friends_count.setVisibility(View.GONE);

        }
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }

    public boolean checkCameraPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(),
                CAMERA);

        return result == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        requestPermissions(new
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
                            /*Intent qrIntent = new Intent(getActivity(), QRCodeScan.class);
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

    private void refreshContacts() {
        Log.d(TAG, "refreshContacts CLICK: ");
        if (isNetworkConnected()) {
            showProgress(true);
        }
        ChatappContactsService.startContactService(getActivity(), true);
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
        dialog.show(getChildFragmentManager(), "chatunLock");
    }

    private ChatLockPojo getChatLockdetailfromDB(int position) {

        ChatappContactModel e = adapter.getItem(position);
        String docID = e.get_id();
        String id = uniqueCurrentID.concat("-").concat(docID);
        MessageDbController dbController = CoreController.getDBInstance(getActivity());
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

//    @Override
//    public void onBackPressed() {
//        if (etSearch.getVisibility() == View.GONE) {
//            hideKeyboard();
//            super.onBackPressed();
//        } else {
//            backarrow.performClick();
//        }
//    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        getActivity().unregisterReceiver(contactsRefreshReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == QR_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            String qrData = data.getStringExtra("QRData");
            System.out.println("==" + qrData);
//            SessionManager.getInstance(this).getPhoneNumberOfCurrentUser()

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("from", SessionManager.getInstance(getActivity()).getPhoneNumberOfCurrentUser());
            params.put("to", valuable(qrData));
            params.put("status", "1");
            System.out.println("====ph" + SessionManager.getInstance(getActivity()).getPhoneNumberOfCurrentUser());
            System.out.println("====ph" + valuable(qrData));
            ServiceRequest request = new ServiceRequest(getActivity());
            request.makeServiceRequest(Constants.CONTACT_REQUEST_SENT, Request.Method.POST, params, verifcationListener);


        }
    }

    private String kyGn() {
        return getResources().getString(R.string.chatapp) + getResources().getString(R.string.chatapp) + getResources().getString(R.string.adani);
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
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        contactMenu = menu;
        getActivity().getMenuInflater().inflate(R.menu.new_chat_fragment, menu);

        MenuItem searchItem = menu.findItem(R.id.chatsc_searchIcon);
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


                    if (adapter != null) {
                        if (newText.length() > 0) {
                            if (newText.length() == 1) {
                            }
                            SettingContactFragment.this.adapter.getFilter().filter(newText);
                        } else {

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
                return false;
            }
        });


        searchView.setIconifiedByDefault(true);
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);

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
            mCursorDrawableRes.set(searchTextView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }


        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {

                menu.findItem(R.id.chats_contactIcon).setVisible(true);
                return false;
            }
        });

        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.findItem(R.id.chats_contactIcon).setVisible(false);
            }
        });

    }

    public void hideKeyboard(AutoCompleteTextView searchTextView) {

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

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
                    Toast.makeText(getActivity(), "Please check your inernet connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.menuaboutHelp:
                ActivityLauncher.launchAbouthelp(getActivity());
                break;

            case R.id.chats_settings:
                ActivityLauncher.launchSettingScreen(getActivity());
                return true;

            case R.id.chats_contactIcon:

                if (ConnectivityInfo.isInternetConnected(getActivity())) {
                    if (checkCameraPermission()) {
                        /*Intent qrIntent = new Intent(getActivity(), QRCodeScan.class);
                        startActivityForResult(qrIntent, QR_REQUEST_CODE);*/
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    Toast.makeText(getActivity(), "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
//                Intent newIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
//                // Sets the MIME type to match the Contacts Provider
//                newIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
//                startActivity(newIntent);
                break;
        }
        return super.onOptionsItemSelected(item);
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
        System.out.println("====v" + idStr);
        return idStr;

    }

    @Override
    public void onSuccess() {
        refreshContacts();
    }


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            if (isAdded()) {
                int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

                contact_layout.setVisibility(show ? View.GONE : View.VISIBLE);
                contact_layout.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        contact_layout.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                mProgressView.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
            }
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            contact_layout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}