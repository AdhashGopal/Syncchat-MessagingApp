package com.chatapp.synchat.app;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.bumptech.glide.Glide;
import com.chatapp.synchat.BuildConfig;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.CommonInGroupAdapter;
import com.chatapp.synchat.app.adapter.RItemAdapter;
import com.chatapp.synchat.app.calls.CallMessage;
import com.chatapp.synchat.app.calls.CallsActivity;
import com.chatapp.synchat.app.dialog.ChatLockPwdDialog;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.dialog.MuteAlertDialog;
import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.app.utils.BlockUserUtils;
import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.app.utils.MuteUnmute;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.IncomingMessage;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.CallItemChat;
import com.chatapp.synchat.core.model.ChatLockPojo;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.CommonInGroupPojo;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.MuteStatusPojo;
import com.chatapp.synchat.core.model.MuteUserPojo;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.service.ServiceRequest;
import com.chatapp.synchat.core.socket.SocketManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kyleduo.switchbutton.SwitchButton;


import org.appspot.apprtc.CallActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.provider.Telephony.Mms.Part.TEXT;
import static com.chatapp.synchat.app.ChatViewActivity.isGroupChat;

//import com.muzakki.ahmad.widget.CollapsingToolbarLayout;

/**
 * created by  Adhash Team on 12/26/2016.
 */
public class UserInfo extends CoreActivity implements View.OnClickListener, MuteAlertDialog.MuteAlertCloseListener {

    public static final String INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED = "finishActivityOnSaveCompleted";
    private static final int PICK_CONTACT_REQUEST = 10;
    private static final String TAG = "UserInfo";
    private static final String CHANNEL_WHATEVER = "";
    private static final int NOTIFY_ID = 11;
    final Context context = this;
    final int REQUEST_CODE_PICK_CONTACTS = 1;
    final int PICK_CONTACT = 5;
    final int REQUEST_CODE_CONTACTS = 2;
    private final int ADD_CONTACT = 21;
    private final int AUDIO_RECORD_PERMISSION_REQUEST_CODE = 14;
    SessionManager sessionManager;
    CommonInGroupAdapter adapter;
    SwitchButton swMute;
    String value, contactid26, mRawContactId, mDataId, secretType;
    MessageDbController db;
    int messagetype;
    ArrayList<MessageItemChat> selectedChatItems = new ArrayList<>();
    String phNo, userId, userName, userLastSeen, userAvatar, name, UserNumber, chatType;
    RelativeLayout medialayout;
    LinearLayout media_lineralayout;
    UserInfoSession userInfoSession;
    String status = "";
    int screenWidth = 0;
    int screenHeight = 0;
    private Toolbar toolbar;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView ivProfilePic;
    /* SimpleTarget profileImageTarget = new SimpleTarget<Bitmap>() {
         @Override
         public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
             ivProfilePic.setImageBitmap(resource);
         }
     };*/
    private TextView tvStatus, tvPhone, tvgroup_count, tvCommonGroup, head, media, mute, custom_notification, mediacount;
    private RecyclerView rvMedia, rvCommonGroup;
    private Session session;
    private GroupInfoSession groupInfoSession;
    private Typeface avnRegFont, avnDemiFont;
    private Boolean ismutecheckchange = false, isSecretUserInfo;
    private ArrayList<String> imgzoompath;
    private ArrayList<String> imgCaptions;
    private ArrayList<String> imgs;
    private ArrayList<Uri> uriList;
    private String mCurrentUserId, mLocDbDocId;
    private ArrayList<MessageItemChat> horizontalList;
    private HorizontalAdapter horizontalAdapter;
    private ArrayList<MessageItemChat> mChatData;
    private ArrayList<CommonInGroupPojo> commonGroupList;
    private TextView user_name_title, tvBlock, user_last_online;
    private BroadcastReceiver mReceiver;
    private ContactDB_Sqlite contactDB_sqlite;
    private int clickCount = 0, favStatus = 0;

    /**
     * set Favourite list data response. based on type shown Favourite icon.
     */
    private ServiceRequest.ServiceListener favListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            try {

                JSONObject jsonObject = new JSONObject(response);


                if (jsonObject.getInt("Status") == 1) {
                    favStatus = jsonObject.getInt("fav_type");
                    setFavouriteIcon(favStatus);

                } else if (jsonObject.getInt("Status") == -1) {

                } else {
                    Toast.makeText(UserInfo.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }

        }

        @Override
        public void onErrorListener(int state) {

        }
    };

    /**
     * get Favourite list data response
     */
    private ServiceRequest.ServiceListener getFavStatusListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            try {

                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.getInt("Status") == 1) {
                    favStatus = jsonObject.getInt("fav_type");
                    setFavouriteIcon(favStatus);
                } else if (jsonObject.getInt("Status") == -1) {

                } else {

                    Toast.makeText(UserInfo.this, jsonObject.getString("Message"), Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }

        }

        @Override
        public void onErrorListener(int state) {
            Log.e("state", status + "");
        }
    };


    /**
     * Calender view
     *
     * @param date calender date
     * @param i    position
     * @return value
     */
    public static Date addDay(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTime();
    }

    public static Date addhour(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.HOUR, i);
        return cal.getTime();
    }

    public static Date addYear(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, i);
        return cal.getTime();
    }

    /**
     * get Cursor String
     *
     * @param cursor     get index value
     * @param columnName name
     * @return value
     */
    private static String getCursorString(Cursor cursor, String columnName) {
        int index = cursor.getColumnIndex(columnName);
        if (index != -1) return cursor.getString(index);
        return null;
    }

    /**
     * convert dp To Px
     *
     * @param dp      value of dp
     * @param context current activity
     * @return value
     */
    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    /**
     * API call for set Favourite
     *
     * @param fav
     */
    private void CallFavAPI(int fav) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userid", SessionManager.getInstance(UserInfo.this).getCurrentUserID());
        params.put("fav_userid", userId);
        params.put("fav_type", String.valueOf(fav));//0-fav_remove,1-fav_add
        ServiceRequest request = new ServiceRequest(UserInfo.this);
        request.makeServiceRequest(Constants.SET_FAVOURITE, Request.Method.POST, params, favListener);
    }

    /**
     * API call for get Favourite
     *
     * @param userId
     */
    private void CallFavStatusAPI(String userId) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("userid", SessionManager.getInstance(UserInfo.this).getCurrentUserID());
        params.put("fav_userid", userId);
        ServiceRequest request = new ServiceRequest(UserInfo.this);
        request.makeServiceRequest(Constants.GET_FAVOURITE_STATUS, Request.Method.POST, params, getFavStatusListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        db = CoreController.getDBInstance(this);
        groupInfoSession = new GroupInfoSession(UserInfo.this);
        imgzoompath = new ArrayList<String>();
        imgCaptions = new ArrayList<String>();
        imgs = new ArrayList<String>();
        uriList = new ArrayList<>();
        initView();
    }

    /**
     * binding the value
     */
    private void initView() {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        userInfoSession = new UserInfoSession(this);
        Typeface typeface = CoreController.getInstance().getAvnNextLTProDemiTypeface();
        collapsingToolbarLayout.setCollapsedTitleTypeface(typeface);
        collapsingToolbarLayout.setExpandedTitleTypeface(typeface);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
        mediacount = (TextView) findViewById(R.id.mediacount);
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        user_name_title = (TextView) findViewById(R.id.user_name_title);
        user_last_online = (TextView) findViewById(R.id.user_last_online);
        horizontalList = new ArrayList<MessageItemChat>();
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvPhone = (TextView) findViewById(R.id.tvPhone);
        medialayout = (RelativeLayout) findViewById(R.id.medialayout);
        tvgroup_count = (TextView) findViewById(R.id.tvCommonGroup_Count);
        //  String id = tvPhone.getText().toString();
        tvCommonGroup = (TextView) findViewById(R.id.tvCommonGroup);
        head = (TextView) findViewById(R.id.head);
        media = (TextView) findViewById(R.id.media);
        mute = (TextView) findViewById(R.id.mute);
        custom_notification = (TextView) findViewById(R.id.custom_notification);
        media_lineralayout = (LinearLayout) findViewById(R.id.media_lineralayout);
        rvMedia = (RecyclerView) findViewById(R.id.rvMedia);
//        rvMedia.setNestedScrollingEnabled(false);
        LinearLayoutManager mediaManager = new LinearLayoutManager(UserInfo.this, LinearLayoutManager.HORIZONTAL, false);
        rvMedia.setLayoutManager(mediaManager);

        rvCommonGroup = (RecyclerView) findViewById(R.id.rvCommonGroup);

//        rvCommonGroup.setNestedScrollingEnabled(false);
        LinearLayoutManager groupManager = new LinearLayoutManager(UserInfo.this);
        rvCommonGroup.setLayoutManager(groupManager);
        sessionManager = SessionManager.getInstance(UserInfo.this);
        session = new Session(UserInfo.this);
        mCurrentUserId = sessionManager.getCurrentUserID();
        tvBlock = (TextView) findViewById(R.id.tv_block);

        swMute = (SwitchButton) findViewById(R.id.swMute);
        findViewById(R.id.block_layout).setOnClickListener(this);
        findViewById(R.id.iv_msg).setOnClickListener(this);
        findViewById(R.id.iv_fav).setOnClickListener(this);
        findViewById(R.id.iv_audio_call).setOnClickListener(this);
        findViewById(R.id.iv_video_call).setOnClickListener(this);
        initProgress("Loading...", true);

        setItemTouchListener();

        initData();
        ivProfilePic.setOnClickListener(UserInfo.this);
        swMute.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (ConnectivityInfo.isInternetConnected(UserInfo.this)) {
                            if (isChecked) {
                                ismutecheckchange = true;
                                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(UserInfo.this);

                                String convId = userInfoSession.getChatConvId(mLocDbDocId);
                                MuteStatusPojo muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, userId, convId, isSecretUserInfo);

                                if (muteData == null || muteData.getMuteStatus().equals("0") || muteData.getMuteStatus().isEmpty()) {
                                    MuteUserPojo muteUserPojo = new MuteUserPojo();
                                    muteUserPojo.setReceiverId(userId);
                                    muteUserPojo.setChatType(MessageFactory.CHAT_TYPE_SINGLE);

                                    if (isSecretUserInfo) {
                                        muteUserPojo.setSecretType("yes");
                                    } else {
                                        muteUserPojo.setSecretType("no");
                                    }

                                    ArrayList<MuteUserPojo> muteUserList = new ArrayList<>();
                                    muteUserList.add(muteUserPojo);

                                    Bundle putBundle = new Bundle();
                                    putBundle.putSerializable("MuteUserList", muteUserList);

                                    MuteAlertDialog dialog = new MuteAlertDialog();
                                    dialog.setArguments(putBundle);
                                    dialog.setCancelable(false);
                                    dialog.setMuteAlertCloseListener(UserInfo.this);
                                    dialog.show(getSupportFragmentManager(), "Mute");
                                }
                            } else {
                                //ismutecheckchange = true;
                                if (ismutecheckchange) {

                                } else {

                                }
                                MuteUnmute.performUnMute(UserInfo.this, EventBus.getDefault(), userId, MessageFactory.CHAT_TYPE_SINGLE,
                                        secretType);
                                showProgressDialog();
                            }
                        } else {
                            swMute.setChecked(!isChecked);
                            Toast.makeText(UserInfo.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater menuInflater = getMenuInflater();
//        menuInflater.inflate(R.menu.user_screen, menu);
//        MenuItem addcontact_menu = menu.findItem(R.id.add_contact);
//        MenuItem add_existing_contact = menu.findItem(R.id.add_existing_contact);
//        MenuItem share = menu.findItem(R.id.share);
//        MenuItem edit = menu.findItem(R.id.edit);
//        MenuItem view = menu.findItem(R.id.view);
//
//        Getcontactname objContact = new Getcontactname(UserInfo.this);
//        boolean isAlreadyContact = objContact.isContactExists(userId);
//
//        if (!isAlreadyContact) {
//            addcontact_menu.setVisible(true);
//            add_existing_contact.setVisible(true);
//            share.setVisible(false);
//            edit.setVisible(false);
//            view.setVisible(false);
//
//            addcontact_menu.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
//                    intent.putExtra(INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED, true);
//                    // Sets the MIME type to match the Contacts Provider
//                    intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
//                    intent.putExtra(ContactsContract.Intents.Insert.PHONE, userName);
////                startActivity(intent);
//                    startActivityForResult(intent, ADD_CONTACT);
//                    return false;
//                }
//            });
//            add_existing_contact.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem item) {
//                    startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), PICK_CONTACT_REQUEST);
//
//                    return false;
//                }
//            });
//
//        } else {
//            share.setVisible(true);
//            edit.setVisible(true);
//            view.setVisible(true);
//        }
//        return true;
//    }

    /**
     * getting the data
     */
    private void initData() {
        userId = getIntent().getStringExtra("UserId");
        userName = getIntent().getStringExtra("UserName");
        userLastSeen = getIntent().getStringExtra("UserLastSeen");
        UserNumber = getIntent().getStringExtra("UserNumber");
        userAvatar = getIntent().getStringExtra("UserAvatar");
        isSecretUserInfo = getIntent().getBooleanExtra("FromSecretChat", false);

        CallFavStatusAPI(userId);

//        userAvatar = userAvatar.concat("?id=").concat(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        userAvatar = new Getcontactname(this).getAvatarUrl(userId);
        mLocDbDocId = mCurrentUserId.concat("-").concat(userId);
        if (isSecretUserInfo) {
            mLocDbDocId = mLocDbDocId + "-secret";
            secretType = "yes";
            chatType = MessageFactory.CHAT_TYPE_SECRET;
        } else {
            secretType = "no";
            chatType = MessageFactory.CHAT_TYPE_SINGLE;
        }

        mChatData = new ArrayList<>();
        loadFromDB();
        contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        String blockStatus = contactDB_sqlite.getBlockedStatus(userId, false);
        String convId = userInfoSession.getChatConvId(mLocDbDocId);
        MuteStatusPojo muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, userId, convId, isSecretUserInfo);

        if (muteData != null && muteData.getMuteStatus().equals("1")) {
            swMute.setChecked(true);
        } else {
            swMute.setChecked(false);
        }

        if (blockStatus.equals("1")) {
            tvBlock.setText("Unblock");
        } else {
            tvBlock.setText("Block");
        }

        avnDemiFont = CoreController.getInstance().getAvnNextLTProDemiTypeface();
        makeCollapsingToolbarLayoutLooksGood(collapsingToolbarLayout);

        collapsingToolbarLayout.setTitle(userName);
//        collapsingToolbarLayout.setSubtitle(userLastSeen);

//        user_name_title.setText(userName);
//        user_last_online.setText(userLastSeen);
        getUserDetails(userId);

        profilepicUpdation();
        //String from = SessionManager.getInstance(UserInfo.this).getCurrentUserID();

        MessageDbController db = CoreController.getDBInstance(this);

        commonGroupList = new ArrayList<>();
        ArrayList<MessageItemChat> groupChats = db.selectChatList(MessageFactory.CHAT_TYPE_GROUP);
        Getcontactname getcontactname = new Getcontactname(this);

        for (MessageItemChat chat : groupChats) {
            String docID = mCurrentUserId + "-" + chat.getReceiverID() + "-g";

            if (groupInfoSession.hasGroupInfo(docID)) {
                GroupInfoPojo groupInfoPojo = groupInfoSession.getGroupInfo(docID);
                if (groupInfoPojo.getGroupMembers() != null && groupInfoPojo.getGroupMembers().contains(userId)
                        && groupInfoPojo.isLiveGroup()) {
                    StringBuilder sb = new StringBuilder();
                    String memername = "";

                    String[] contacts = groupInfoPojo.getGroupMembers().split(",");

                    for (int i = 0; i < contacts.length; i++) {
                        if (!contacts[i].equalsIgnoreCase(mCurrentUserId)) {
//                            ChatappContactModel info = contactsDB.getUserDetails(contacts[i]);
                            ChatappContactModel info = contactDB_sqlite.getUserOpponenetDetails(contacts[i]);

                            if (info != null) {

                                memername = getcontactname.getSendername(contacts[i], info.getMsisdn());
                                memername = info.getFirstName();
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

//                    statusTextView.setText(sb);

                    CommonInGroupPojo commonGroupPojo = new CommonInGroupPojo();
                    commonGroupPojo.setGroupName(groupInfoPojo.getGroupName());
                    commonGroupPojo.setAvatarPath(groupInfoPojo.getAvatarPath());
                    commonGroupPojo.setGroupContactNames(sb.toString());
                    commonGroupPojo.setGroupId(groupInfoPojo.getGroupId());
                    commonGroupList.add(commonGroupPojo);
                }
            }
        }
        if (commonGroupList == null || commonGroupList.size() == 0) {
            tvgroup_count.setText("");
            findViewById(R.id.group_common_layout).setVisibility(View.GONE);
        } else {
            Integer groupCount = commonGroupList.size();
            tvgroup_count.setText("" + groupCount);
        }
        adapter = new CommonInGroupAdapter(UserInfo.this, commonGroupList);
        rvCommonGroup.setAdapter(adapter);

        statusUpdation();
    }

    /**
     * load From dataBase value
     */
    private void loadFromDB() {
        ArrayList<MessageItemChat> items;
        items = db.selectAllChatMessages(mLocDbDocId, chatType);
        mChatData.clear();
        mChatData.addAll(items);
        mediafile();

    }

    /**
     * Getting media file
     */
    protected void mediafile() {
        try {
            for (int i = 0; i < mChatData.size(); i++) {
                String type = mChatData.get(i).getMessageType();
                int mtype = Integer.parseInt(type);
                if (MessageFactory.picture == mtype) {
                    MessageItemChat msgItem = mChatData.get(i);
                    if (msgItem.getImagePath() != null) {
                        String path = msgItem.getImagePath();
                        File file = new File(path);
                        if (file.exists()) {
                            Uri pathuri = Uri.fromFile(file);
                            horizontalList.add(msgItem);
                            imgzoompath.add(path);
                            imgs.add(path);
                            uriList.add(pathuri);
                            if (mChatData.get(i).getCaption() != null)
                                imgCaptions.add(mChatData.get(i).getCaption());
                            else
                                imgCaptions.add("");

                        }

                    } else if (msgItem.getChatFileLocalPath() != null) {
                        String path = msgItem.getChatFileLocalPath();
                        File file = new File(path);
                        if (file.exists()) {
                            Uri pathuri = Uri.fromFile(file);
                            horizontalList.add(msgItem);
                            imgzoompath.add(path);
                            imgs.add(path);
                            uriList.add(pathuri);
                            if (mChatData.get(i).getCaption() != null)
                                imgCaptions.add(mChatData.get(i).getCaption());
                            else
                                imgCaptions.add("");

                        }

                    }

                } else if (MessageFactory.video == mtype) {
                    MessageItemChat msgItem = mChatData.get(i);
                    if (msgItem.getVideoPath() != null) {
                        String path = msgItem.getVideoPath();
                        File file = new File(path);
                        if (file.exists()) {
                            Uri pathuri = Uri.fromFile(file);
                            horizontalList.add(msgItem);
                            imgzoompath.add(path);
                            imgCaptions.add("");
                            uriList.add(pathuri);
                        }
                    } else if (msgItem.getChatFileLocalPath() != null) {
                        String path = msgItem.getChatFileLocalPath();
                        File file = new File(path);
                        if (file.exists()) {
                            Uri pathuri = Uri.fromFile(file);
                            horizontalList.add(msgItem);
                            imgzoompath.add(path);
                            imgCaptions.add("");
                            uriList.add(pathuri);
                        }
                    }
                } else if (MessageFactory.document == mtype) {
                    MessageItemChat msgItem = mChatData.get(i);
                    if (msgItem.getChatFileLocalPath() != null) {
                        String path = msgItem.getChatFileLocalPath();
                        File file = new File(path);
                        if (file.exists()) {
                            Uri pathuri = Uri.fromFile(file);
                            horizontalList.add(msgItem);
                            imgzoompath.add(path);
                            imgCaptions.add("");
                            uriList.add(pathuri);
                        }
                    }
                } else if (MessageFactory.audio == mtype) {
                    MessageItemChat msgItem = mChatData.get(i);
//                if (msgItem.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                    if (msgItem.getAudioPath() != null) {
                        String path = msgItem.getAudioPath();
                        File file = new File(path);
                        if (file.exists()) {
                            Uri pathuri = Uri.fromFile(file);
                            horizontalList.add(msgItem);
                            imgzoompath.add(path);
                            imgCaptions.add("");
                            uriList.add(pathuri);
                        }
                    } else if (msgItem.getChatFileLocalPath() != null) {
                        String path = msgItem.getChatFileLocalPath();
                        File file = new File(path);
                        if (file.exists()) {
                            Uri pathuri = Uri.fromFile(file);
                            horizontalList.add(msgItem);
                            imgzoompath.add(path);
                            imgCaptions.add("");
                            uriList.add(pathuri);
                        }
                    }
//                }
                }
            }
            horizontalAdapter = new HorizontalAdapter(horizontalList);
            rvMedia.setAdapter(horizontalAdapter);
            int count = horizontalList.size();
            mediacount.setText((String.valueOf(count)));
            rvMedia.addOnItemTouchListener(new RItemAdapter(UserInfo.this, rvMedia, new RItemAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    if (position < 5) {
                        if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.picture)) {
                            Intent intent = new Intent(getApplication(), ImageZoom.class);
                            intent.putExtra("from", "media");
                            intent.putExtra("captiontext", imgCaptions.get(position));
                            intent.putExtra("image", imgzoompath.get(position));
                            startActivity(intent);
                        } else if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.video)) {
                            try {
/*
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imgzoompath.get(position)));
                            intent.setDataAndType(Uri.parse(imgzoompath.get(position)), "video/*");
                            startActivity(intent);
*/
                            /*String extension = MimeTypeMap.getFileExtensionFromUrl(imgzoompath.get(position));
                            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            File file = new File(imgzoompath.get(position));
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri uri = FileProvider.getUriForFile(UserInfo.this, BuildConfig.APPLICATION_ID, file);
                            intent.setDataAndType(uri, mimeType);
                            startActivity(intent);
*/
                                CallIntentFileProvider(position);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(UserInfo.this, "No app installed to play this video", Toast.LENGTH_SHORT).show();
                            }
                        } else if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.audio)) {
                            MessageItemChat msgItem = horizontalList.get(position);
//                        if (msgItem.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                            try {
                              /*  Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(uriList.get(position), "audio/*");
                                startActivity(intent);*/

                            /*String extension = MimeTypeMap.getFileExtensionFromUrl(imgzoompath.get(position));
                            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                            File file = new File(imgzoompath.get(position));
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri uri = FileProvider.getUriForFile(UserInfo.this, BuildConfig.APPLICATION_ID, file);
                            intent.setDataAndType(uri, mimeType);
                            startActivity(intent);
*/
                                CallIntentFileProvider(position);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(UserInfo.this, "No app installed to play this audio", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e(TAG, "onItemClick: ", e);
                            }
//                        }
                        } else if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.document)) {


                            try {

                                CallIntentFileProvider(position);
                            } catch (ActivityNotFoundException e) {
                                Toast.makeText(UserInfo.this, "No app installed to play this audio", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Log.e(TAG, "onItemClick: ", e);
                            }

                        }
                    }
                }

                @Override
                public void onItemLongClick(View view, int position) {

                }
            }));


            medialayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserInfo.this, MediaAcitivity.class);
                    intent.putExtra("username", userName);
                    intent.putExtra("docid", mLocDbDocId);
                    startActivity(intent);
                }
            });
            if (horizontalList.size() == 0) {
                media_lineralayout.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * IntentFileProvider
     *
     * @param position value of position
     */
    private void CallIntentFileProvider(int position) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(imgzoompath.get(position));
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        File file = new File(imgzoompath.get(position));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(UserInfo.this, BuildConfig.APPLICATION_ID, file);
        intent.setDataAndType(uri, mimeType);
        startActivity(intent);
    }

    /**
     * menu selection
     *
     * @param item menu item
     * @return value
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
          /*  if (session.getmute(userId + "mute")) {
                swMute.setChecked(false);
            } else {
                swMute.setChecked(true);
            }*/
            changemute();
//            finish();
            onBackPressed();
        }
//        else if (item.getItemId() == R.id.edit) {
//            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
//
//            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
//            intent.putExtra(ContactsContract.Intents.Insert.NAME, userName);
//            intent.putExtra(ContactsContract.Intents.Insert.PHONE, phNo);
//            startActivityForResult(intent, REQUEST_CODE_PICK_CONTACTS);
//            // getContactName(context,phNo);
//
//        } else if (item.getItemId() == R.id.view)
//
//        {
//            displayContacts();
//            /*Intent intent = new Intent(Intent.ACTION_VIEW);
//            Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, 3824);
//            intent.setData(uri);
//            context.startActivity(intent);*/
//        } else if (item.getItemId() == R.id.share) {
//            String contactChatappId = "";
//
//            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//
//
//            ArrayList<ChatappContactModel> ChatappEntries = contactDB_sqlite.getSavedChatappContacts();
//
//            if (ChatappEntries != null) {
//                for (ChatappContactModel ChatappMOdel : ChatappEntries) {
//                    if ((ChatappMOdel.getNumberInDevice() != null && ChatappMOdel.getNumberInDevice().equalsIgnoreCase(phNo))
//                            || (ChatappMOdel.getMsisdn() != null && ChatappMOdel.getMsisdn().equalsIgnoreCase(phNo))) {
//                        contactChatappId = ChatappMOdel.get_id();
//                        break;
//                    }
//                }
//            }
//
//            messagetype = MessageFactory.contact;
//            MessageItemChat msgItem = new MessageItemChat();
//            msgItem.setContactName(userName);
//            msgItem.setContactNumber(phNo);
//            msgItem.setContactchatappId(contactChatappId);
//            msgItem.setMessageType("" + messagetype);
//            selectedChatItems.add(msgItem);
//
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("MsgItemList", selectedChatItems);
//            bundle.putBoolean("FromChatapp", true);
//
//            Intent intent = new Intent(context, ForwardContact.class);
//            intent.putExtras(bundle);
//            context.startActivity(intent);
//
//           /* Intent intent = new Intent(context, ForwardContact.class);
//            // intent.putExtra("message", newtext);
//            intent.putExtra("message1", new Gson().toJson(selectedChatItems));
//            startActivity(intent);*/
//        }
        return super.onOptionsItemSelected(item);
    }

    private void displayContacts() {

        contactid26 = null;

        ContentResolver contentResolver = getContentResolver();

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phNo));

        Cursor cursor =
                contentResolver.query(
                        uri,
                        new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID},
                        null,
                        null,
                        null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String contactName = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup.DISPLAY_NAME));
                contactid26 = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));

            }
            cursor.close();
        }
        if (contactid26 == null) {
            Toast.makeText(UserInfo.this, "No contact found associated with this number", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent_contacts = new Intent(Intent.ACTION_VIEW, Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contactid26)));
            //Intent intent_contacts = new Intent(Intent.ACTION_VIEW, Uri.parse("content://contacts/people/" + contactid26));
            startActivity(intent_contacts);
        }

    }

    /**
     * getContactName
     *
     * @param context current activity
     * @param number  get number
     * @return value
     */
    private String getContactName(Context context, String number) {

        name = null;

        // define the columns I want the query to return
        String[] projection = new String[]{
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup._ID};

        // encode the phone number and build the filter URI
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        // query tsNextLine
        Cursor cursor = context.getContentResolver().query(contactUri, projection, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

            } else {
            }
            cursor.close();
        }
        return name;

    }

    /**
     * getNameByURI
     *
     * @param contactUri uri value
     * @return value
     */
    private String getNameByURI(Uri contactUri) {
        Cursor cursor = context.getContentResolver().query(contactUri, null, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));

            } else {
            }
            cursor.close();
        }
        return name;
    }

    /**
     * ItemTouchListener for recyclerview
     */
    private void setItemTouchListener() {
        rvCommonGroup.addOnItemTouchListener(new RItemAdapter(this, rvCommonGroup, new RItemAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(View view, int position) {

                ChatLockPojo lockPojo = getChatLockdetailfromDB(position);
                if (sessionManager.getLockChatEnabled().equals("1") && lockPojo != null) {

                    String stat = lockPojo.getStatus();
                    String pwd = lockPojo.getPassword();

                    CommonInGroupPojo e = adapter.getItem(position);
                    String docID = e.getGroupId();
                    String documentid = mCurrentUserId + "-" + docID + "-g";
                    if (stat.equals("1")) {
                        openUnlockChatDialog(documentid, stat, pwd, position);
                    } else {
                        navigateTochatviewpage(position);
                    }
                } else {
                    navigateTochatviewpage(position);
                }
            }


            @Override
            public void onItemLongClick(View view, int position) {

            }

        }


        ));
    }

    /**
     * open Unlock Chat Dialog
     *
     * @param docId    specific docid
     * @param status   value of status
     * @param pwd      value of password
     * @param position value of position
     */
    private void openUnlockChatDialog(String docId, String status, String pwd, int position) {
        String convId = docId.split("-")[1];
        ChatLockPwdDialog dialog = new ChatLockPwdDialog();
        dialog.setTextLabel1("Enter your Password");
        dialog.setEditTextdata("Enter new Password");
        dialog.setforgotpwdlabel("Forgot Password");
        dialog.setHeader("Unlock Chat");
        dialog.setButtonText("UNLOCK");
        Bundle bundle = new Bundle();
        bundle.putSerializable("commoningroup", adapter.getItem(position));
        bundle.putString("convID", convId);
        bundle.putString("status", "1");
        bundle.putString("pwd", pwd);
        bundle.putString("page", "chatlist");
        bundle.putString("type", "group");
        bundle.putString("from", mCurrentUserId);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "chatunLock");
    }

    /**
     * Pass the value one activity another
     *
     * @param position
     */
    private void navigateTochatviewpage(int position) {
        Intent intent = new Intent(UserInfo.this, ChatViewActivity.class);
        CommonInGroupPojo data = commonGroupList.get(position);
        String username = data.getGroupName();
        String profileimage = data.getAvatarPath();
        intent.putExtra("receiverUid", data.getGroupId());
        intent.putExtra("receiverName", username);
        intent.putExtra("documentId", data.getGroupId());

        intent.putExtra("Username", username);
        intent.putExtra("Image", profileimage);
        intent.putExtra("msisdn", "");
        intent.putExtra("type", 0);

        startActivity(intent);
        isGroupChat = false;
    }

    /**
     * Getting the value for activity result
     *
     * @param requestCode getting the requestCode
     * @param resultCode  getting the resultCode
     * @param data        data value
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK && data != null) {
            getContactName(context, phNo);
            collapsingToolbarLayout.setTitle(name);

        } else if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
            loadContactInfo(data.getData());
        } else if (requestCode == ADD_CONTACT && resultCode == RESULT_OK) {
            getNameByURI(data.getData());
        }

    }


    /**
     * load ContactInfo  AsyncTask
     *
     * @param contactUri value of uri
     */
    private void loadContactInfo(Uri contactUri) {

        /*
         * We should always run database queries on a background thread. The database may be
         * locked by some process for a long tsNextLine.  If we locked up the UI thread while waiting
         * for the query to come back, we might get an "Application Not Responding" dialog.
         */
        AsyncTask<Uri, Void, Boolean> task = new AsyncTask<Uri, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Uri... uris) {
                Log.v("Retreived ContactURI", uris[0].toString());

                return doesContactContainHomeEmail(uris[0]);
            }

            @Override
            protected void onPostExecute(Boolean exists) {
                if (exists) {
                    Log.v("", "Updating...");
                    updateContact();
                } else {
                    Log.v("", "Inserting...");
                    insertEmailContact();
                }
            }
        };

        task.execute(contactUri);
    }

    /**
     * does Contact Contain HomeEmail
     *
     * @param contactUri value of uri
     * @return value
     */
    private Boolean doesContactContainHomeEmail(Uri contactUri) {
        boolean returnValue = false;
        Cursor mContactCursor = getContentResolver().query(contactUri, null, null, null, null);
        Log.v("Contact", "Got Contact Cursor");

        try {
            if (mContactCursor.moveToFirst()) {
                String mContactId = getCursorString(mContactCursor,
                        ContactsContract.Contacts._ID);

                Cursor mRawContactCursor = getContentResolver().query(
                        ContactsContract.RawContacts.CONTENT_URI,
                        null,
                        ContactsContract.Data.CONTACT_ID + " = ?",
                        new String[]{mContactId},
                        null);

                Log.v("RawContact", "Got RawContact Cursor");

                try {
                    ArrayList<String> mRawContactIds = new ArrayList<String>();
                    while (mRawContactCursor.moveToNext()) {
                        String rawId = getCursorString(mRawContactCursor, ContactsContract.RawContacts._ID);
                        Log.v("RawContact", "ID: " + rawId);
                        mRawContactIds.add(rawId);
                    }

                    for (String rawId : mRawContactIds) {
                        // Make sure the "last checked" RawContactId is set locally for use in insert & update.
                        mRawContactId = rawId;
                        Cursor mDataCursor = getContentResolver().query(
                                ContactsContract.Data.CONTENT_URI,
                                null,
                                ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.Email.TYPE + " = ?",
                                new String[]{mRawContactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)},
                                null);

                        if (mDataCursor.getCount() > 0) {
                            mDataCursor.moveToFirst();
                            mDataId = getCursorString(mDataCursor, ContactsContract.Data._ID);
                            Log.v("Data", "Found data item with MIMETYPE and EMAIL.TYPE");
                            mDataCursor.close();
                            returnValue = true;
                            break;
                        } else {
                            Log.v("Data", "Data doesn't contain MIMETYPE and EMAIL.TYPE");
                            mDataCursor.close();
                        }
                        returnValue = false;
                    }
                } finally {
                    mRawContactCursor.close();
                }
            }
        } catch (Exception e) {
            Log.w("UpdateContact", e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) {
                Log.w("UpdateContact", "\t" + ste.toString());
            }
            throw new RuntimeException();
        } finally {
            mContactCursor.close();
        }

        return returnValue;
    }

    /**
     * insert EmailContact
     */
    public void insertEmailContact() {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            ops.add(ContentProviderOperation
                    .newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, mRawContactId)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, userName)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)
                    // .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Phone")
                    .build());


            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (Exception e) {
            // Display warning
            Log.w("UpdateContact", e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) {
                Log.w("UpdateContact", "\t" + ste.toString());
            }
            Context ctx = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(ctx, "Update failed", duration);
            e.printStackTrace();
            toast.show();
        }
    }

    /**
     * update Contact
     */
    public void updateContact() {
        try {
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " = ?", new String[]{mRawContactId})
                    .withSelection(ContactsContract.Data._ID + " = ?", new String[]{mDataId})
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, userName)
                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)

                    .build());


            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);

        } catch (Exception e) {
            // Display warning
            Log.w("UpdateContact", e.getMessage());
            for (StackTraceElement ste : e.getStackTrace()) {
                Log.w("UpdateContact", "\t" + ste.toString());
            }
            Context ctx = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(ctx, "Update failed", duration);
            toast.show();
        }
    }

    /**
     * Click action
     *
     * @param view specific view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ivProfilePic:
                if (userAvatar != null && !userAvatar.isEmpty()) {
                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(UserInfo.this);

                    String profilePicVisibility = contactDB_sqlite.getProfilePicVisibility(userId);

                    if (profilePicVisibility.equals(ContactDB_Sqlite.PRIVACY_TO_NOBODY)) {
                        userAvatar = "";
                    }
                    Intent imgIntent = new Intent(UserInfo.this, ImageZoom.class);
                    imgIntent.putExtra("ProfilePath", userAvatar);
                    startActivity(imgIntent);
                    break;
                }

                break;

            case R.id.block_layout:
                if (AppUtils.isNetworkAvailable(context)) {
                    blockUser();
                } else {
                    Toast.makeText(context, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
                }


                break;
            case R.id.iv_msg:
                openChatPage();
                break;
            case R.id.iv_fav:
               /* clickCount++;
                if (clickCount % 2 == 0) {
                    favouriteUser(0);
                } else {
                    favouriteUser(1);
                }*/
                favouriteUser(favStatus);
                break;
            case R.id.iv_audio_call:
                if (AppUtils.isNetworkAvailable(context)) {
                    makeCall(false);
                } else {
                    Toast.makeText(context, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.iv_video_call:
                if (AppUtils.isNetworkAvailable(context)) {
                    makeCall(true);
                } else {
                    Toast.makeText(context, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }

    /**
     * Based on response set favourite icon
     *
     * @param favouriteIcon o means unfav 1 means fav
     */
    private void setFavouriteIcon(int favouriteIcon) {
        if (favouriteIcon == 0) {

            findViewById(R.id.iv_fav).setBackgroundResource(R.drawable.fav_unfocus);

        } else {
            findViewById(R.id.iv_fav).setBackgroundResource(R.drawable.fav_focus);
        }

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) findViewById(R.id.iv_fav).getLayoutParams();
        params.width = dpToPx(22, UserInfo.this);
        params.height = dpToPx(22, UserInfo.this);
        params.setMargins(0, (int) getResources().getDimension(R.dimen.margin_5), (int) getResources().getDimension(R.dimen.margin_15), 0);
        findViewById(R.id.iv_fav).setPadding((int) getResources().getDimension(R.dimen.margin_7), (int) getResources().getDimension(R.dimen.margin_7), (int) getResources().getDimension(R.dimen.margin_7), (int) getResources().getDimension(R.dimen.margin_7));
        findViewById(R.id.iv_fav).setLayoutParams(params);
    }

    /**
     * open ChatPage
     */
    private void openChatPage() {
        Intent intent = new Intent(getApplicationContext(), ChatViewActivity.class);
        intent.putExtra("msisdn", "");
        intent.putExtra("Username", userName);
        intent.putExtra("documentId", userId);
        intent.putExtra("receiverUid", "");
        intent.putExtra("Image", userAvatar);
        intent.putExtra("type", 0);
        intent.putExtra("receiverName", userName);
        intent.putExtra("msisdn", phNo);
        startActivity(intent);
    }

    /**
     * Block user popup
     */
    private void blockUser() {
        String msg;
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        if (contactDB_sqlite.getBlockedStatus(userId, false).equals("1")) {
            msg = "Do you want to Unblock " + userName + "?";
        } else {
            msg = "Block " + userName + "? Blocked contacts will no longer be able to call you or send you messages.";
        }

        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setMessage(msg);
        dialog.setPositiveButtonText("Ok");
        dialog.setNegativeButtonText("Cancel");
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                dialog.dismiss();

                putBlockUser();
            }

            @Override
            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });

        dialog.show(getSupportFragmentManager(), "Block alert");
    }


    /**
     * shown favouriteUser popup
     *
     * @param favType
     */
    private void favouriteUser(final int favType) {
        final int requestFavId;
        String msg_add = "Are you sure want to add this contact as favourite ?";
        String msg_remove = "Are you sure want to remove this contact from favourite ?";


        final CustomAlertDialog dialog = new CustomAlertDialog();
        if (favType == 0) {
            requestFavId = 1;
            dialog.setMessage(msg_add);
        } else {
            requestFavId = 0;
            dialog.setMessage(msg_remove);
        }

        dialog.setPositiveButtonText("Ok");
        dialog.setNegativeButtonText("Cancel");
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                dialog.dismiss();

                CallFavAPI(requestFavId);
            }

            @Override
            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });

        dialog.show(getSupportFragmentManager(), "Add Favourite Contact");
    }


    /**
     * Make video call
     *
     * @param isVideoCall boolean value
     */
    private void makeCall(boolean isVideoCall) {


        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        if (contactDB_sqlite.getBlockedStatus(userId, false).equals("1")) {
            DisplayAlert("Unblock" + " " + userName + " " + "to place a "
                    + getString(R.string.app_name) + " call");
        } else {
            // performCall(isVideoCall);
            Activecall(isVideoCall);
        }

    }

    /**
     * Video call action
     *
     * @param isVideoCall boolean value
     */
    private void Activecall(boolean isVideoCall) {
        if (ConnectivityInfo.isInternetConnected(this))
            if (checkAudioRecordPermission()) {

                CallMessage message = new CallMessage(UserInfo.this);
                boolean isOutgoingCall = true;
                int type;

                if (isVideoCall) {
                    type = MessageFactory.video_call;

                } else {
                    type = MessageFactory.audio_call;
                }

                JSONObject object = (JSONObject) message.getMessageObject(userId, type);


                String roomid = null;
                String timestamp = null;
                try {
                    roomid = object.getString("id");
                    timestamp = object.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String callid = mCurrentUserId + "-" + userId + "-" + timestamp;
//        CallMessage.openCallScreen(ChatViewActivity.this, mCurrentUserId, receiverUid, callid,
//                roomid, "", receiverMsisdn, "999", isVideoCall, true, timestamp);


                Log.d(TAG, "openCallScreen: start");
                if (!CallsActivity.isStarted) {
                    if (isOutgoingCall) {
                        CallsActivity.opponentUserId = userId;
                    }

//                    if (object != null) {
//                        SendMessageEvent callEvent = new SendMessageEvent();
//                        callEvent.setEventName(SocketManager.EVENT_CALL);
//                        callEvent.setMessageObject(object);
//                        EventBus.getDefault().post(callEvent);
//                    }

                    PreferenceManager.setDefaultValues(context, org.appspot.apprtc.R.xml.preferences, false);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

                    String keyprefRoomServerUrl = context.getString(org.appspot.apprtc.R.string.pref_room_server_url_key);
                    String roomUrl = sharedPref.getString(
                            keyprefRoomServerUrl, Constants.SOCKET_IP_BASE);
                    Log.d(TAG, "openCallScreen: 1");

                    int videoWidth = 0;
                    int videoHeight = 0;
                    String resolution = context.getString(org.appspot.apprtc.R.string.pref_resolution_default);
                    String[] dimensions = resolution.split("[ x]+");
                    if (dimensions.length == 2) {
                        try {
                            videoWidth = Integer.parseInt(dimensions[0]);
                            videoHeight = Integer.parseInt(dimensions[1]);
                        } catch (NumberFormatException e) {
                            videoWidth = 0;
                            videoHeight = 0;
                            Log.e("ChatappCallError", "Wrong video resolution setting: " + resolution);
                        }
                    }
                    Log.d(TAG, "openCallScreen: 2");
                    Uri uri = Uri.parse(roomUrl);
                    Intent intent = new Intent(context, CallsActivity.class);
//            Intent intent = new Intent(context, CallNotifyService.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                            intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) { // This is at least android 10...

                        NotificationManager mgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        if (mgr.getNotificationChannel(CHANNEL_WHATEVER) == null) {
                            mgr.createNotificationChannel(new NotificationChannel(CHANNEL_WHATEVER, "Whatever", NotificationManager.IMPORTANCE_HIGH));
                        }

                        mgr.notify(NOTIFY_ID, buildNormal(context, intent, fullScreenPendingIntent).build());

                    }
                    intent.setData(uri);
                    intent.putExtra(CallsActivity.EXTRA_IS_OUTGOING_CALL, isOutgoingCall);
                    intent.putExtra(CallsActivity.EXTRA_DOC_ID, callid);
                    intent.putExtra(CallsActivity.EXTRA_FROM_USER_ID, mCurrentUserId);
                    intent.putExtra(CallsActivity.EXTRA_TO_USER_ID, userId);
                    intent.putExtra(CallsActivity.EXTRA_USER_MSISDN, phNo);
                    intent.putExtra(CallsActivity.EXTRA_OPPONENT_PROFILE_PIC, "");
                    intent.putExtra(CallsActivity.EXTRA_NAVIGATE_FROM, context.getClass().getSimpleName()); // For navigating from call activity
                    intent.putExtra(CallsActivity.EXTRA_CALL_CONNECT_STATUS, "0");
                    intent.putExtra(CallsActivity.EXTRA_CALL_TIME_STAMP, timestamp);

                    intent.putExtra(CallsActivity.EXTRA_ROOMID, roomid);
                    intent.putExtra(CallsActivity.EXTRA_LOOPBACK, false);
                    intent.putExtra(CallsActivity.EXTRA_VIDEO_CALL, isVideoCall);
                    intent.putExtra(CallsActivity.EXTRA_SCREENCAPTURE, false);
                    intent.putExtra(CallsActivity.EXTRA_CAMERA2, true);
                    intent.putExtra(CallsActivity.EXTRA_VIDEO_WIDTH, videoWidth);
                    intent.putExtra(CallsActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
                    intent.putExtra(CallsActivity.EXTRA_VIDEO_FPS, 0);
                    intent.putExtra(CallsActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false);
                    intent.putExtra(CallsActivity.EXTRA_VIDEO_BITRATE, 0);
                    intent.putExtra(CallsActivity.EXTRA_VIDEOCODEC, context.getString(org.appspot.apprtc.R.string.pref_videocodec_default));
                    intent.putExtra(CallsActivity.EXTRA_HWCODEC_ENABLED, false);
                    intent.putExtra(CallsActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, true);
                    intent.putExtra(CallsActivity.EXTRA_FLEXFEC_ENABLED, false);
                    intent.putExtra(CallsActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, false);
                    intent.putExtra(CallsActivity.EXTRA_AECDUMP_ENABLED, false);
                    intent.putExtra(CallsActivity.EXTRA_OPENSLES_ENABLED, false);
                    intent.putExtra(CallsActivity.EXTRA_DISABLE_BUILT_IN_AEC, false);
                    intent.putExtra(CallsActivity.EXTRA_DISABLE_BUILT_IN_AGC, false);
                    intent.putExtra(CallsActivity.EXTRA_DISABLE_BUILT_IN_NS, false);
                    intent.putExtra(CallsActivity.EXTRA_ENABLE_LEVEL_CONTROL, false);
                    intent.putExtra(CallsActivity.EXTRA_AUDIO_BITRATE, 0);
                    intent.putExtra(CallsActivity.EXTRA_AUDIOCODEC, context.getString(org.appspot.apprtc.R.string.pref_audiocodec_default));
                    intent.putExtra(CallsActivity.EXTRA_DISPLAY_HUD, false);
                    intent.putExtra(CallsActivity.EXTRA_TRACING, false);
                    intent.putExtra(CallsActivity.EXTRA_CMDLINE, false);
                    intent.putExtra(CallsActivity.EXTRA_RUNTIME, 0);

                    intent.putExtra(CallActivity.EXTRA_DATA_CHANNEL_ENABLED, true);
                    intent.putExtra(CallActivity.EXTRA_ORDERED, true);
                    intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS_MS, -1);
                    intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS, -1);
                    intent.putExtra(CallActivity.EXTRA_PROTOCOL, context.getString(org.appspot.apprtc.R.string.pref_data_protocol_default));
                    intent.putExtra(CallActivity.EXTRA_NEGOTIATED, false);
                    intent.putExtra(CallActivity.EXTRA_ID, -1);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Log.d(TAG, "openCallScreen: 3");


                }
            } else {
                requestAudioRecordPermission();
            }


    }

    /**
     * get push notification
     *
     * @param context                 current activity
     * @param intent
     * @param fullScreenPendingIntent navigation of intent
     * @return value
     */
    private NotificationCompat.Builder buildNormal(Context context, Intent intent, PendingIntent fullScreenPendingIntent) {

        NotificationCompat.Builder b =
                new NotificationCompat.Builder(context, CHANNEL_WHATEVER);

        b.setAutoCancel(true)
                .setSmallIcon(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? R.drawable.ic_launcher : R.drawable.ic_launcher)
                .setDefaults(Notification.DEFAULT_ALL)
                .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
                .setContentText(TEXT)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(fullScreenPendingIntent, true);
        Notification incomingCallNotification = b.build();

        return (b);

    }

    private void performCall(boolean isVideoCall) {

        if (ConnectivityInfo.isInternetConnected(this)) {

            if (checkAudioRecordPermission()) {
                if (!CallMessage.isAlreadyCallClick) {
                    int callType;
                    if (isVideoCall) {
                        callType = MessageFactory.video_call;
                        //callType_global = MessageFactory.video_call;
                    } else {
                        callType = MessageFactory.audio_call;
                        // callType_global = MessageFactory.audio_call;
                    }

                    CallMessage message = new CallMessage(UserInfo.this);
                    JSONObject object = (JSONObject) message.getMessageObject(userId, callType);

                    if (object != null) {
                        SendMessageEvent callEvent = new SendMessageEvent();
                        callEvent.setEventName(SocketManager.EVENT_CALL);
                        callEvent.setMessageObject(object);
                        EventBus.getDefault().post(callEvent);
                    }
                    CallMessage.setCallClickTimeout();
                } else {
                    Toast.makeText(UserInfo.this, "Call in progress", Toast.LENGTH_SHORT).show();
                }

            /*Intent i = new Intent(this, CallHistoryActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
                    Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);

            startActivity(i);*/

            } else {
                requestAudioRecordPermission();
            }
        } else {
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * request AudioRecord Permission
     */
    private void requestAudioRecordPermission() {
        ActivityCompat.requestPermissions(UserInfo.this, new
                String[]{RECORD_AUDIO}, AUDIO_RECORD_PERMISSION_REQUEST_CODE);
    }

    /**
     * DisplayAlert for block/ unblock user
     *
     * @param txt message value
     */
    private void DisplayAlert(final String txt) {
        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setMessage(txt);
        dialog.setNegativeButtonText("Cancel");
        dialog.setPositiveButtonText("Unblock");
        dialog.setCancelable(false);
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                putBlockUser();
                dialog.dismiss();
            }

            @Override

            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "Unblock a person");

    }

    /**
     * set block user
     */
    private void putBlockUser() {
        BlockUserUtils.changeUserBlockedStatus(UserInfo.this, EventBus.getDefault(),
                mCurrentUserId, userId, false);
    }

    /**
     * block / unblock contact
     *
     * @param event getting value from model class
     */
    private void blockunblockcontact(ReceviceMessageEvent event) {
        String toid = "", fromid = "";
        try {
            Object[] obj = event.getObjectsArray();
            JSONObject object = new JSONObject(obj[0].toString());

            String stat = object.getString("status");
            toid = object.getString("to");
            fromid = object.getString("from");

            if (mCurrentUserId.equalsIgnoreCase(fromid) && toid.equalsIgnoreCase(userId)) {

                if (stat.equalsIgnoreCase("1")) {
                    tvBlock.setText("Unblock");
                    Toast.makeText(this, "Contact is blocked", Toast.LENGTH_SHORT).show();
                } else {
                    tvBlock.setText("Block");
                    Toast.makeText(this, "Contact is Unblocked", Toast.LENGTH_SHORT).show();
                }
            } else if (mCurrentUserId.equalsIgnoreCase(toid) && fromid.equalsIgnoreCase(userId)) {
               /* getcontactname.configProfilepic(ivProfilePic, userId, true, false, R.mipmap.chat_attachment_profile_default_image_frame);

                if (stat.equalsIgnoreCase("1")) {
                    statusTextView.setVisibility(View.GONE);

                } else {
                    statusTextView.setVisibility(View.VISIBLE);
                }*/
            }

        } catch (Exception e) {
            Log.e(TAG, "blockunblockcontact: ", e);
        }
    }

    /**
     * Pass the value to one activity to another
     */
    private void changemute() {
        Intent muteintant = new Intent();
        muteintant.putExtra("muteactivity", ismutecheckchange);
        setResult(RESULT_OK, muteintant);
    }

    /**
     * Mute Dialog Closed
     *
     * @param isMuted boolean value
     */
    @Override
    public void onMuteDialogClosed(boolean isMuted) {
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        String convId = userInfoSession.getChatConvId(mLocDbDocId);
        MuteStatusPojo muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, userId, convId, isSecretUserInfo);

        if (muteData != null && muteData.getMuteStatus().equals("1")) {
            swMute.setChecked(true);
        } else {
            swMute.setChecked(false);

        }
    }

    /**
     * get TimeString for current time based
     *
     * @param millis value of millis
     * @return value
     */
    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf.append(String.format("%02d", minutes)).append(":").append(String.format("%02d", seconds));

        return buf.toString();
    }

    /**
     * Eventbus value
     *
     * @param event call socket event based on response
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_PRIVACY_SETTINGS)) {
            loadPrivacySetting(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GET_USER_DETAILS)) {
            loadUserDetails(event);
            statusUpdation();
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MUTE)) {
            loadMuteMessage(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CALL_RESPONSE)) {
            loadCallResMessage(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_BLOCK_USER)) {
            blockunblockcontact(event);
        }

    }

    /**
     * load Call Recevice call status
     *
     * @param event getting the model class value
     */
    private void loadCallResMessage(ReceviceMessageEvent event) {
        Object[] obj = event.getObjectsArray();
        try {
            Log.d(TAG, "loadCallResMessage: ");
            JSONObject object = new JSONObject(obj[0].toString());
            JSONObject callObj = object.getJSONObject("data");

            String from = callObj.getString("from");
            String callStatus = callObj.getString("call_status");
            if (from.equalsIgnoreCase(mCurrentUserId) && callStatus.equals(MessageFactory.CALL_STATUS_CALLING + "")) {
                String to = callObj.getString("to");

                IncomingMessage incomingMsg = new IncomingMessage(this);
                CallItemChat callItem = incomingMsg.loadOutgoingCall(callObj);

                boolean isVideoCall = false;
                if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
                    isVideoCall = true;
                }

                String toUserAvatar = callObj.getString("To_avatar") + "?=id" + Calendar.getInstance().getTimeInMillis();

                MessageDbController db = CoreController.getDBInstance(this);
                db.updateCallLogs(callItem);

                String ts = callObj.getString("timestamp");

//                CallMessage.openCallScreen(this, mCurrentUserId, to, callItem.getCallId(),
//                        callItem.getRecordId(), toUserAvatar, callItem.getOpponentUserMsisdn(),
//                        MessageFactory.CALL_IN_FREE + "", isVideoCall, true, ts);

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * get UserDetails
     *
     * @param userId value
     */
    public void getUserDetails(String userId) {
        JSONObject eventObj = Getcontactname.getUserDetailsObject(this, userId);
        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_GET_USER_DETAILS);
        event.setMessageObject(eventObj);
        EventBus.getDefault().post(event);
    }

    /**
     * Start Eventbus
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Stop Eventbus
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    /**
     * load Mute Message
     *
     * @param event getting model class value
     */
    private void loadMuteMessage(ReceviceMessageEvent event) {

        try {
            JSONObject object = new JSONObject(event.getObjectsArray()[0].toString());
            String from = object.getString("from");

            if (from.equalsIgnoreCase(mCurrentUserId)) {
                String convId = object.getString("convId");

                String to;
                if (object.has("to")) {
                    to = object.getString("to");
                } else {
                    to = userInfoSession.getReceiverIdByConvId(convId);
                }

                if (to != null && !to.equals("") && to.equalsIgnoreCase(userId)) {
                    String secretType = "no";
                    if (object.has("secret_type")) {
                        secretType = object.getString("secret_type");
                    }
                    boolean isSecretChat = secretType.equalsIgnoreCase("yes");

                    String status = object.getString("status");
                    if (status.equals("1")) {
                        if (isSecretChat == isSecretUserInfo) {
                            swMute.setChecked(true);
                        }
                    } else {
                        if (isSecretChat == isSecretUserInfo) {
                            swMute.setChecked(false);
                        }
                    }

                    hideProgressDialog();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * load UserDetails
     *
     * @param event getting model class value
     */
    private void loadUserDetails(ReceviceMessageEvent event) {

        Object[] data = event.getObjectsArray();

        try {
            JSONObject object = new JSONObject(data[0].toString());

            if (object.has("Status")) {
                status = object.getString("Status");
                try {
                    byte[] nameBuffer = Base64.decode(status, Base64.DEFAULT);
                    status = new String(nameBuffer, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                status = getResources().getString(R.string.default_user_status);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * User PrivacySetting (last seen, nobody, everyone option)
     *
     * @param event
     */
    private void loadPrivacySetting(ReceviceMessageEvent event) {
        Object[] object = event.getObjectsArray();
        System.out.println("OBJECt--->" + object);
        try {
            JSONObject jsonObject = new JSONObject(object[0].toString());
            String status = (String) jsonObject.get("status");
            String lastseen = String.valueOf(jsonObject.get("last_seen"));
            String profile = String.valueOf(jsonObject.get("profile_photo"));
            JSONArray contactUserList = jsonObject.getJSONArray("contactUserList");
            Boolean iscontact = false;
            if (contactUserList != null) {
                iscontact = contactUserList.toString().contains(mCurrentUserId);
            }
            if (userAvatar != null && !userAvatar.equals("")) {
                if (profile.equalsIgnoreCase("nobody")) {
                    ivProfilePic.setImageResource(R.drawable.group_icon2);
                } else if (profile.equalsIgnoreCase("everyone")) {
                    if (!userAvatar.startsWith(Constants.SOCKET_IP)) {
                        userAvatar = userAvatar.concat(Constants.SOCKET_IP);
                    }
                    Glide.with(UserInfo.this).load(userAvatar).into(ivProfilePic);
                } else if (profile.equalsIgnoreCase("mycontacts") && iscontact) {
                    if (!userAvatar.startsWith(Constants.SOCKET_IP)) {
                        userAvatar = userAvatar.concat(Constants.SOCKET_IP);
                    }
                    Glide.with(UserInfo.this).load(userAvatar).into(ivProfilePic);
                } else {
                    ivProfilePic.setImageResource(R.drawable.group_icon2);
                }
            }
            if (status.equalsIgnoreCase("nobody")) {
                tvStatus.setVisibility(View.GONE);
            } else if (status.equalsIgnoreCase("everyone")) {
                tvStatus.setVisibility(View.VISIBLE);
            } else if (status.equalsIgnoreCase("mycontacts") && iscontact) {
                tvStatus.setVisibility(View.VISIBLE);
            } else {
                tvStatus.setVisibility(View.GONE);
            }

        } catch (Exception e) {

        }
    }

    /**
     * profile picture Updation & updated database
     */
    private void profilepicUpdation() {

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);

        if (contactDB_sqlite.getBlockedMineStatus(userId, isSecretUserInfo).equals(ContactDB_Sqlite.BLOCKED_STATUS)) {
            Glide.with(context).load(R.drawable.group_icon2).into(ivProfilePic);
        } else {

            String profilePicVisibility = contactDB_sqlite.getProfilePicVisibility(userId);

            if (profilePicVisibility.equals(ContactDB_Sqlite.PRIVACY_STATUS_NOBODY)) {
                Glide.with(context).load(R.drawable.group_icon2).into(ivProfilePic);
            } else {

                boolean canShow = false;
                if (profilePicVisibility.equals(ContactDB_Sqlite.PRIVACY_STATUS_EVERYONE)) {
                    canShow = true;
                } else {
                    if (contactDB_sqlite.getMyContactStatus(userId).equals("1")) {
                        canShow = true;
                    }
                }

                if (canShow) {
                    if (userAvatar != null) {
                        if (!userAvatar.startsWith(Constants.SOCKET_IP)) {
                            userAvatar = Constants.SOCKET_IP.concat(userAvatar);
                        }
/*                        Picasso picasso = Picasso.with(context);
                        RequestCreator requestCreator = picasso.load(userAvatar).error(
                                R.drawable.group_icon2);
                        requestCreator.into(ivProfilePic);*/
                        AppUtils.loadImage(context, userAvatar, ivProfilePic, 0, R.drawable.group_icon2);
                    } else {
                        Glide.with(context).load(R.drawable.group_icon2).into(ivProfilePic);
                    }
                } else {

                    Glide.with(context).load(R.drawable.group_icon2).into(ivProfilePic);
                }
            }
        }

    }


    /**
     * status Updation
     */
    private void statusUpdation() {

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        ArrayList<ChatappContactModel> ChatappEntries = contactDB_sqlite.getSavedChatappContacts();

        boolean isChatappContact = false;

        if (ChatappEntries != null) {
            for (int i = 0; i < ChatappEntries.size(); i++) {
                ChatappContactModel contactModel = ChatappEntries.get(i);
                if (userId.equalsIgnoreCase(contactModel.get_id())) {
                    status = contactModel.getStatus();
                    phNo = contactModel.getNumberInDevice();
                    isChatappContact = true;

                    String msisdn = contactModel.getMsisdn();
                    if (msisdn != null && !msisdn.equals("")) {
                        tvPhone.setText(msisdn);
                    } else {
                        tvPhone.setText(phNo);
                    }
                    break;
                }
            }
        }

        if (!isChatappContact) {

            ChatappContactModel contact = contactDB_sqlite.getUserOpponenetDetails(userId);
            if (contact != null) {
                status = contact.getStatus();
                tvPhone.setText(contact.getMsisdn());
            }
        }

        Getcontactname objGetContactName = new Getcontactname(this);
        boolean isDisplayed = objGetContactName.setProfileStatusText(tvStatus, userId, status, isSecretUserInfo);
        if (!isDisplayed) {
            tvStatus.setVisibility(View.GONE);
        } else {
            tvStatus.setVisibility(View.VISIBLE);
        }

        Log.d("VisibilityS", tvStatus.getText().toString() + "," + tvStatus.getVisibility());
    }

    /**
     * Collapsing Toolbar Layout
     *
     * @param collapsingToolbarLayout view
     */
    private void makeCollapsingToolbarLayoutLooksGood(CollapsingToolbarLayout collapsingToolbarLayout) {
        try {
            final Field field = collapsingToolbarLayout.getClass().getDeclaredField("mCollapsingTextHelper");
            field.setAccessible(true);
            final Object object = field.get(collapsingToolbarLayout);
            final Field tpf = object.getClass().getDeclaredField("mTextPaint");
            tpf.setAccessible(true);
            avnDemiFont = CoreController.getInstance().getAvnNextLTProDemiTypeface();
            ((TextPaint) tpf.get(object)).setTypeface(avnDemiFont);
            ((TextPaint) tpf.get(object)).setColor(getResources().getColor(R.color.white));
        } catch (Exception ignored) {
        }
    }

    /**
     * get Chat Lock detail from Database
     *
     * @param position position of value
     * @return value
     */
    private ChatLockPojo getChatLockdetailfromDB(int position) {
        CommonInGroupPojo e = adapter.getItem(position);
        String groupId = e.getGroupId();
        String id = mCurrentUserId.concat("-").concat(groupId).concat("-g");
        MessageDbController dbController = CoreController.getDBInstance(this);
        String convId = userInfoSession.getChatConvId(id);
        String receiverId = userInfoSession.getReceiverIdByConvId(convId);
        ChatLockPojo pojo = dbController.getChatLockData(receiverId, MessageFactory.CHAT_TYPE_GROUP);
        return pojo;
    }

    /*
    Target profileImageTarget = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            ivProfilePic.setImageBitmap(bitmap);
            //BitmapDrawable bit=new BitmapDrawable(bitmap);
            //collapsingToolbarLayout.setBackgroundDrawable(bit);
            ivProfilePic.setOnClickListener(UserInfo.this);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };*/

    @Override
    protected void onResume() {
        super.onResume();
        try {
            /*contactDB_sqlite = CoreController.getContactSqliteDBintstance(UserInfo.this);
            String blockStatus = contactDB_sqlite.getBlockedStatus(userId, false);
            if(blockStatus.equalsIgnoreCase("1"))
            {
                collapsingToolbarLayout.setSubtitle("");
            }
            IntentFilter intentFilter = new IntentFilter(
                    "android.intent.action.MAIN");

            mReceiver = new BroadcastReceiver() {

                @Override
                public void onReceive(Context context, Intent intent) {
                    //extract our message from intent
                    String userLastSeen = intent.getStringExtra("userLastSeen");

                        collapsingToolbarLayout.setSubtitle(userLastSeen);



                }
            };*/
            //registering our receiver
//            this.registerReceiver(mReceiver, intentFilter);

//                LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver,intentFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Pause the current activity
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mReceiver != null)
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Killed the current activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mReceiver != null)
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Horizontal Recyclerview Adapter
     */
    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        private List<MessageItemChat> horizontalList;

        public HorizontalAdapter(List<MessageItemChat> horizontalList) {
            this.horizontalList = horizontalList;
        }

        /**
         * layout binding
         * @param parent
         * @param viewType
         * @return
         */
        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.horizontal_item_view, parent, false);

            return new MyViewHolder(itemView);
        }

        /**
         * binding the value
         * @param holder viewholder
         * @param position position of data
         */
        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            holder.Vido.setVisibility(View.GONE);
            holder.duration.setVisibility(View.GONE);
            if (position < 5) {
                if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.picture)) {
                    holder.image.setVisibility(View.VISIBLE);

                    if (horizontalList.get(position).getImagePath() != null) {
                        String path = horizontalList.get(position).getImagePath();
                        File file = new File(path);
                        if (file.exists()) {
                            Bitmap myBitmap = BitmapFactory.decodeFile(path);
                            holder.image.setImageBitmap(myBitmap);
                        }
                    } else if (horizontalList.get(position).getChatFileLocalPath() != null) {
                        String path = horizontalList.get(position).getChatFileLocalPath();
                        File file = new File(path);
                        if (file.exists()) {
                            try {
                                Bitmap myBitmap = BitmapFactory.decodeFile(path);

                                Bitmap s = RotateBitmap(myBitmap, 90);

                                holder.image.setImageBitmap(myBitmap);
                            } catch (Exception e) {
                                Log.e(TAG, "onBindViewHolder: ", e);
                            }
                        }
                    }
                } else if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.video)) {
                    try {
                        if (horizontalList.get(position).getVideoPath() != null) {
                            String path = horizontalList.get(position).getVideoPath();
                            File file = new File(path);
                            if (file.exists()) {
                                Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
                                holder.image.setImageBitmap(thumbBmp);
                                MediaMetadataRetriever mdr = new MediaMetadataRetriever();
                                mdr.setDataSource(horizontalList.get(position).getChatFileLocalPath());
                                String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                String setduration = getTimeString(Long.parseLong(duration));
                                holder.Vido.setVisibility(View.VISIBLE);
                                holder.duration.setVisibility(View.VISIBLE);
                                holder.duration.setText(setduration);
                            }

                        } else if (horizontalList.get(position).getChatFileLocalPath() != null) {
                            String path = horizontalList.get(position).getChatFileLocalPath();
                            File file = new File(path);
                            if (file.exists()) {
                                Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
                                holder.image.setImageBitmap(thumbBmp);
                                MediaMetadataRetriever mdr = new MediaMetadataRetriever();
                                mdr.setDataSource(horizontalList.get(position).getChatFileLocalPath());
                                String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                String setduration = getTimeString(Long.parseLong(duration));
                                holder.Vido.setVisibility(View.VISIBLE);
                                holder.duration.setVisibility(View.VISIBLE);
                                holder.duration.setText(setduration);
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.audio)) {
//                    if (horizontalList.get(position).getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                    holder.Vido.setVisibility(View.GONE);
                    holder.duration.setVisibility(View.VISIBLE);
                    holder.image.setBackgroundResource(R.drawable.ic_media_audio);
                    String path = horizontalList.get(position).getChatFileLocalPath();
                    File file = new File(path);
                    String duration = "";
                    if (file.exists()) {
                        duration = horizontalList.get(position).getDuration();
                    }
                    holder.duration.setText(duration);
//                    }
                } else if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.document)) {

                    holder.Vido.setVisibility(View.GONE);
                    holder.duration.setVisibility(View.GONE);
                    holder.image.setBackgroundResource(R.drawable.document_icon);
                    String path = horizontalList.get(position).getChatFileLocalPath();

                }

//                if (horizontalList.size() >= 5) {
//                    if (horizontalList.size() - 1 == position) {
//                        holder.arrow.setVisibility(View.VISIBLE);
//                        holder.arrow.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                Intent intent = new Intent(UserInfo.this, MediaAcitivity.class);
//                                intent.putExtra("username", userName);
//                                intent.putExtra("docid", mLocDbDocId);
//                                startActivity(intent);
//                                // Toast.makeText(UserInfo.this,holder.txtView.getText().toString(),Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    } else {
//                        holder.arrow.setVisibility(View.GONE);
//                        holder.arrow.setOnClickListener(null);
//                    }
//
//                }
            } else {
                holder.arrow.setVisibility(View.VISIBLE);
                holder.image.setVisibility(View.GONE);

                holder.arrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(UserInfo.this, MediaAcitivity.class);
                        intent.putExtra("username", userName);
                        intent.putExtra("docid", mLocDbDocId);
                        startActivity(intent);
                        // Toast.makeText(UserInfo.this,holder.txtView.getText().toString(),Toast.LENGTH_SHORT).show();
                    }
                });
            }

            // holder.image.setText(horizontalList.get(position));
        }

        /**
         * Image value
         * @param source image source
         * @param angle angle of image
         * @return value
         */
        public Bitmap RotateBitmap(Bitmap source, float angle) {
            try {
                if (source != null) {
                    Matrix matrix = new Matrix();
                    matrix.postRotate(angle);
                    int wid = source.getWidth();
                    int hig = source.getHeight();

                    return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
                }
            } catch (Exception e) {
                Log.e(TAG, "RotateBitmap: ", e);
            }
            return null;
        }


        //--------------------Rotate------------------------------------

        /**
         * get count
         * @return
         */
        @Override
        public int getItemCount() {


            if (horizontalList.size() <= 5) {
                return horizontalList.size();
            }

            return 6;
        }

        /**
         * view binding
         */
        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView image, arrow, Vido;
            public AvnNextLTProDemiTextView duration;

            public MyViewHolder(View view) {
                super(view);
                image = (ImageView) view.findViewById(R.id.Image);
                arrow = (ImageView) view.findViewById(R.id.arrow);
                Vido = (ImageView) view.findViewById(R.id.Vido);
                duration = (AvnNextLTProDemiTextView) view.findViewById(R.id.duration);

            }
        }

    }

    /**
     * killed the current activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        isGroupChat = false;
    }
}





