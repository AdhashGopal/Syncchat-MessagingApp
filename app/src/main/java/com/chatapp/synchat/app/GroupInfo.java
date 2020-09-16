package com.chatapp.synchat.app;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.synchat.BuildConfig;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.BroadcastInfoAdapter;
import com.chatapp.synchat.app.adapter.GroupInfoAdapter;
import com.chatapp.synchat.app.adapter.RItemAdapter;
import com.chatapp.synchat.app.contactlist.SearchPeopleForGroupChat;
import com.chatapp.synchat.app.dialog.ChatLockPwdDialog;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.dialog.CustomMultiTextItemsDialog;
import com.chatapp.synchat.app.dialog.MuteAlertDialog;
import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.app.utils.BroadcastInfoSession;
import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.app.utils.MuteUnmute;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiButton;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.chatapphelperclass.ChatappRegularExp;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.message.PictureMessage;
import com.chatapp.synchat.core.model.BroadcastInfoPojo;
import com.chatapp.synchat.core.model.ChatLockPojo;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.model.GroupMembersPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.MultiTextDialogPojo;
import com.chatapp.synchat.core.model.MuteStatusPojo;
import com.chatapp.synchat.core.model.MuteUserPojo;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.socket.SocketManager;
import com.chatapp.synchat.core.uploadtoserver.FileUploadDownloadManager;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.kyleduo.switchbutton.SwitchButton;
import com.soundcloud.android.crop.Crop;

import org.appspot.apprtc.util.CryptLib;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.crypto.NoSuchPaddingException;

import id.zelory.compressor.Compressor;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * created by  Adhash Team on 11/29/2016.
 */
public class GroupInfo extends CoreActivity implements View.OnClickListener, RItemAdapter.OnItemClickListener, MuteAlertDialog.MuteAlertCloseListener {

    private static final String TAG = "GroupInfo";
    private final int GALLERY_REQUEST_CODE = 1;
    private final int CAMERA_REQUEST_CODE = 2;
    private final int ADD_MEMBER_REQUEST_CODE = 3;
    private final int CHANGE_GROUP_NAME_REQUEST_CODE = 4;
    public RelativeLayout mute_layout;
    AvnNextLTProDemiTextView tvParticipantTitle;
    List<BroadcastInfoPojo> broadcastInfoPojos;
    GroupMembersPojo mCurrentUserData;
    Getcontactname getcontactname;
    RelativeLayout medialayout;
    LinearLayout media_lineralayout;
    String uid, contactname, image, msisdn;
    Dialog listDialog;
    TextView mediacount;
    Boolean ismutechange = false;
    AvnNextLTProDemiTextView mute;
    Boolean isgroupemty = false;
    private AvnNextLTProDemiButton btnExitGroup, btnDeleteGroup;
    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private RecyclerView rvGroupMembers, rvMedia;
    private SwitchButton swMute;
    private AvnNextLTProRegTextView tvMembersCount, tvAddMember, custom_notification, groupempty, tvGroupCreatedInfo;
    private ImageView ivGroupDp;
    private int valueposition;
    private SessionManager sessionManager, sessionManagerBroadcast;
    private Session session;
    private GroupInfoSession groupInfoSession;
    private BroadcastInfoSession broadcastInfoSession;
    private GroupInfoAdapter adapter;
    private String mGroupId, mGroupName, groupUserIds, mCurrentUserId, mLocDbDocId, value;
    private List<GroupMembersPojo> savedMembersList, unsavedMembersList;
    public List<GroupMembersPojo> allMembersList;
    private boolean isAdminUser, isLiveGroup, isContact;
    private Menu groupMenu;
    private ArrayList<String> imgzoompath;
    private ArrayList<String> imgCaptions;
    private ArrayList<MessageItemChat> mChatData;
    private Uri cameraImageUri;
    private ArrayList<MessageItemChat> horizontalList;
    private HorizontalAdapter horizontalAdapter;
    private int membersCount;
    private UserInfoSession userInfoSession;
    private ProgressBar progressBar;
    private ArrayList<Uri> uriList;
    private ArrayList<String> imgs;
    private ArrayList<String> member;
    private ImageView searchMember;
    private ProgressDialog dialog, progress;

    public void initProgress() {
        progress = new ProgressDialog(this);
        progress.setCancelable(false);//you can cancel it by pressing back button
        progress.setMessage("Loading ...");
       /* progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressBar.setProgress(0);//initially progress is 0
        progressBar.setMax(100);//sets the maximum value 100*/
        progress.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.color_primary_progress_dialog));
        progress.setIndeterminate(true);
        progress.show();//displays the progress bar
    }

    /**
     * set mute option
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_info);

        try {
            horizontalList = new ArrayList<MessageItemChat>();
            uriList = new ArrayList<>();
            imgs = new ArrayList<String>();
            imgzoompath = new ArrayList<String>();
            imgCaptions = new ArrayList<String>();
            getcontactname = new Getcontactname(GroupInfo.this);
            dialog = new ProgressDialog(GroupInfo.this);
            dialog.setMessage("please wait.");
            dialog.show();

            if (savedInstanceState != null) {
                cameraImageUri = Uri.parse(savedInstanceState.getString("ImageUri"));
            } else {
                cameraImageUri = Uri.parse("");
            }
            initProgress();
            initProgress("Loading...", true);

            initView();
            initData();
            exit_delete();
            swMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (ConnectivityInfo.isInternetConnected(GroupInfo.this)) {
                        if (isChecked) {
                            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(GroupInfo.this);
                            MuteStatusPojo muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, null, mGroupId, false);

                            if (muteData == null || muteData.getMuteStatus().equals("0") || muteData.getMuteStatus().equals("")) {
                                MuteUserPojo muteUserPojo = new MuteUserPojo();
                                muteUserPojo.setReceiverId(mGroupId);
                                muteUserPojo.setSecretType("no");
                                muteUserPojo.setChatType(MessageFactory.CHAT_TYPE_GROUP);

                                ArrayList<MuteUserPojo> muteUserList = new ArrayList<>();
                                muteUserList.add(muteUserPojo);

                                Bundle putBundle = new Bundle();
                                putBundle.putSerializable("MuteUserList", muteUserList);

                                MuteAlertDialog dialog = new MuteAlertDialog();
                                dialog.setArguments(putBundle);
                                dialog.setCancelable(false);
                                dialog.setMuteAlertCloseListener(GroupInfo.this);
                                dialog.show(getSupportFragmentManager(), "Mute");
                            }
                        } else {
                            MuteUnmute.performUnMute(GroupInfo.this, EventBus.getDefault(), mGroupId,
                                    MessageFactory.CHAT_TYPE_GROUP, "no");

                            ismutechange = true;

                            showProgressDialog();
                        }
                    } else {
                        swMute.setChecked(!isChecked);
                        Toast.makeText(GroupInfo.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
                    }
                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }


        //broadcast();
    }

    /**
     * binding the id's
     */
    private void initView() {
        searchMember = (ImageView) findViewById(R.id.searchMember);
        progressBar = (ProgressBar) findViewById(R.id.pbHeaderProgress);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.expandedappbar);
        mediacount = (TextView) findViewById(R.id.mediacount);
        medialayout = (RelativeLayout) findViewById(R.id.medialayout);
        media_lineralayout = (LinearLayout) findViewById(R.id.media_lineralayout);
        rvGroupMembers = (RecyclerView) findViewById(R.id.rvGroupMembers);
        rvGroupMembers.addOnItemTouchListener(new RItemAdapter(GroupInfo.this,
                rvGroupMembers, GroupInfo.this));
        rvMedia = (RecyclerView) findViewById(R.id.rvMedia);

        LinearLayoutManager mediaManager = new LinearLayoutManager(GroupInfo.this, LinearLayoutManager.HORIZONTAL, false);
        rvMedia.setLayoutManager(mediaManager);
        swMute = (SwitchButton) findViewById(R.id.swMute);
        mute = (AvnNextLTProDemiTextView) findViewById(R.id.mute);
        mute_layout = (RelativeLayout) findViewById(R.id.mute_layout);
        session = new Session(GroupInfo.this);
        tvMembersCount = (AvnNextLTProRegTextView) findViewById(R.id.tvMembersCount);
        tvParticipantTitle = (AvnNextLTProDemiTextView) findViewById(R.id.tvParticipantTitle);
        groupempty = (AvnNextLTProRegTextView) findViewById(R.id.groupempty);
        tvAddMember = (AvnNextLTProRegTextView) findViewById(R.id.tvAddMember);
        tvAddMember.setOnClickListener(GroupInfo.this);

        custom_notification = (AvnNextLTProRegTextView) findViewById(R.id.custom_notification);

        btnExitGroup = (AvnNextLTProDemiButton) findViewById(R.id.btnExitGroup);
        btnExitGroup.setOnClickListener(GroupInfo.this);
        btnDeleteGroup = (AvnNextLTProDemiButton) findViewById(R.id.btnDeleteGroup);
        btnDeleteGroup.setOnClickListener(GroupInfo.this);
        ivGroupDp = (ImageView) findViewById(R.id.ivGroupDp);
        ivGroupDp.setOnClickListener(GroupInfo.this);

        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvGroupMembers.setLayoutManager(llm);
        rvGroupMembers.setHasFixedSize(true);
        rvGroupMembers.setNestedScrollingEnabled(false);


    }

    /**
     * getting the values
     */
    private void initData() {

        sessionManager = SessionManager.getInstance(GroupInfo.this);
        mCurrentUserId = sessionManager.getCurrentUserID();
        Bundle data = getIntent().getExtras();
        if (data != null) {
            mGroupId = data.getString("GroupId", "");
            mGroupName = data.getString("GroupName", "");
        } else {
            mGroupId = "";
            mGroupName = "";
        }

        if (getIntent().hasExtra("isbroadcast")) {
            mLocDbDocId = mCurrentUserId.concat("-").concat(mGroupId);

        } else {
            mLocDbDocId = mCurrentUserId.concat("-").concat(mGroupId).concat("-g");

        }

        mChatData = new ArrayList<>();
        loadFromDB();
        collapsingToolbarLayout.setTitle(mGroupName);
        Typeface typeface = CoreController.getInstance().getAvnNextLTProDemiTypeface();
        collapsingToolbarLayout.setCollapsedTitleTypeface(typeface);
        collapsingToolbarLayout.setExpandedTitleTypeface(typeface);
        membersCount = 0;

        allMembersList = new ArrayList<>();
        savedMembersList = new ArrayList<>();
        unsavedMembersList = new ArrayList<>();

        userInfoSession = new UserInfoSession(GroupInfo.this);
        groupInfoSession = new GroupInfoSession(GroupInfo.this);
        broadcastInfoSession = new BroadcastInfoSession(GroupInfo.this);

        try {
            if (getIntent().hasExtra("isbroadcast")) {
                BroadcastInfoPojo broadcastInfoPojo = broadcastInfoSession.getBroadcastInfo(mCurrentUserId.concat("-").concat(mGroupId).concat("-g"));

                List<ChatappContactModel> chatappContactModels = broadcastInfoPojo.getContactsModel();
                String memerlist = broadcastInfoPojo.getGroupMembers();

//            if (memerlist!=null && memerlist.equalsIgnoreCase("")) {
//
//                tvParticipantTitle.setVisibility(View.GONE);
//                //  groupempty.setVisibility(View.VISIBLE);
//                tvMembersCount.setVisibility(View.GONE);
//            }
//
//
//            String[] useridsplitmemeber = memerlist.split(",");
//            int countofmember = useridsplitmemeber.length;
//            if (countofmember <= 0) {
//
//                tvParticipantTitle.setVisibility(View.GONE);
//                //       groupempty.setVisibility(View.VISIBLE);
//            }

                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                BroadcastInfoAdapter broadcastInfoAdapter = new BroadcastInfoAdapter(this, chatappContactModels);
                rvGroupMembers.setAdapter(broadcastInfoAdapter);
                progressBar.setVisibility(View.GONE);
            } else {
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                adapter = new GroupInfoAdapter(GroupInfo.this, allMembersList);
                rvGroupMembers.setAdapter(adapter);
                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(mCurrentUserId.concat("-").concat(mGroupId).concat("-g"));
                if (infoPojo != null) {
                    String memerlist = infoPojo.getGroupMembers();
                    Log.d(TAG, "initData: emptygroup set");
                    if (memerlist != null && memerlist.equalsIgnoreCase("")) {

                        tvParticipantTitle.setVisibility(View.GONE);
                        //  groupempty.setVisibility(View.VISIBLE);
                        tvMembersCount.setVisibility(View.GONE);
                    }


                    String[] useridsplitmemeber = memerlist.split(",");
                    int countofmember = useridsplitmemeber.length;
                    if (countofmember <= 0) {

                        tvParticipantTitle.setVisibility(View.GONE);
                        //       groupempty.setVisibility(View.VISIBLE);
                    }
                }
                if (infoPojo != null) {
                    isLiveGroup = infoPojo.isLiveGroup();
                    if (infoPojo.getAvatarPath() != null) {
                        String path = Constants.SOCKET_IP.concat(infoPojo.getAvatarPath());
//                    Picasso.with(GroupInfo.this).load(path).error(R.mipmap.group_chat_attachment_profile_icon).into(ivGroupDp);
                        //TODO tharani map
                        Glide.with(GroupInfo.this).load(path).placeholder(R.mipmap.group_chat_attachment_profile_icon)
                                .centerCrop().dontAnimate().into(ivGroupDp);

                    }
                }
                mute_gone_exit();

                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
                MuteStatusPojo muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, null, mGroupId, false);

                if (muteData != null && muteData.getMuteStatus().equals("1")) {
                    swMute.setChecked(true);
                } else {
                    swMute.setChecked(false);

                }

           /* if (isLiveGroup) {
                if (ConnectivityInfo.isInternetConnected(GroupInfo.this)) {
                    progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getGroupDetails();
                        }
                    }, 2000);
                }


            } else {
                progressBar.setVisibility(View.GONE);
            }*/
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        searchMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (allMembersList != null) {
                    Log.e("event1", allMembersList.size() + "");
                    Intent intent = new Intent(GroupInfo.this, MemberContactSearch.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("test", (Serializable) allMembersList);
                    intent.putExtras(bundle);
                    //EventBus.getDefault().post(allMembersList);
                    startActivity(intent);
                }
            }
        });
        /*searchMember.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.equals("")) {
                    searchMember.clearFocus();
                }
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // do stuff
            }
        });*/
    }

    /**
     * Maintain the UI view data
     */
    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (getIntent().hasExtra("isbroadcast")) {

            } else {
                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(mCurrentUserId.concat("-").concat(mGroupId).concat("-g"));
                if (infoPojo != null) {

                    if (infoPojo.isLiveGroup()) {
                        if (ConnectivityInfo.isInternetConnected(GroupInfo.this)) {

                            if (savedMembersList.size() == 0 || unsavedMembersList.size() == 0) {
                                progressBar.setVisibility(View.VISIBLE);
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        getGroupDetails();
                                    }
                                }, 1000);
                            } else {
                                progressBar.setVisibility(View.GONE);
                            }

                        }


                    } else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * load From database
     */
    private void loadFromDB() {
        ArrayList<MessageItemChat> items;
        MessageDbController db = CoreController.getDBInstance(this);
        if (getIntent().hasExtra("isbroadcast")) {
            items = db.selectAllChatMessages(mLocDbDocId, MessageFactory.CHAT_TYPE_SINGLE);
        } else {
            items = db.selectAllChatMessages(mLocDbDocId, MessageFactory.CHAT_TYPE_GROUP);

        }
        mChatData.clear();
        mChatData.addAll(items);
        mediafile();
    }

    /*
        protected void mediafile() {

            for (int i = 0; i < mChatData.size(); i++) {
                try {
                    String type = mChatData.get(i).getMessageType();
                    int mtype = Integer.parseInt(type);

                    if (MessageFactory.picture == mtype) {
                        MessageItemChat msgItem = mChatData.get(i);
                        if (msgItem.getChatFileLocalPath() != null) {
                            String path = msgItem.getImagePath();
                            File file = new File(path);
                            if (file.exists()) {
                                horizontalList.add(msgItem);
                                imgzoompath.add(path);
                                if (mChatData.get(i).getCaption() != null)
                                    imgCaptions.add(mChatData.get(i).getCaption());
                                else
                                    imgCaptions.add("");
                            }
                        } */
/*else if (msgItem.getChatFileLocalPath() != null) {
                    String path = msgItem.getChatFileLocalPath();
                    File file = new File(path);
                    if (file.exists()) {
                        Uri pathuri = Uri.fromFile(file);
                        horizontalList.add(msgItem);
                        imgzoompath.add(path);
                    }
                }*//*


                } else if (MessageFactory.audio == mtype) {
                    MessageItemChat msgItem = mChatData.get(i);
                    if (msgItem.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                        if (msgItem.getAudioPath() != null) {
                            String path = msgItem.getAudioPath();
                            File file = new File(path);
                            if (file.exists()) {
                                Uri pathuri = Uri.fromFile(file);
                                horizontalList.add(msgItem);
                                imgzoompath.add(path);
                                imgCaptions.add("");

                            }
                        } else if (msgItem.getChatFileLocalPath() != null) {
                            String path = msgItem.getChatFileLocalPath();
                            File file = new File(path);
                            if (file.exists()) {
                                Uri pathuri = Uri.fromFile(file);
                                horizontalList.add(msgItem);
                                imgzoompath.add(path);
                                imgCaptions.add("");
                            }
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
                        }
                    } else if (msgItem.getChatFileLocalPath() != null) {
                        String path = msgItem.getChatFileLocalPath();
                        File file = new File(path);
                        if (file.exists()) {
                            Uri pathuri = Uri.fromFile(file);
                            horizontalList.add(msgItem);
                            imgzoompath.add(path);
                            imgCaptions.add("");
                        }
                    }
                }
            } catch (Exception e) {

            }
        }


        horizontalAdapter = new HorizontalAdapter(horizontalList);
        rvMedia.setAdapter(horizontalAdapter);
        int count = horizontalList.size();
        mediacount.setText((String.valueOf(count)));
        rvMedia.addOnItemTouchListener(new RItemAdapter(GroupInfo.this, rvMedia, new RItemAdapter.OnItemClickListener() {
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

                            CallIntentFileProvider(position);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(GroupInfo.this, "No app installed to play this video", Toast.LENGTH_SHORT).show();
                        }
                    } else if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.audio)) {

                        try {

                            CallIntentFileProvider(position);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(GroupInfo.this, "No app installed to play this audio", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "onItemClick: ", e);
                        }

                    }
                }


                */
/*if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.picture)) {
                    Intent intent = new Intent(getApplication(), ImageZoom.class);
                    intent.putExtra("from", "media");
                    intent.putExtra("image", imgzoompath.get(position));
                    startActivity(intent);
                }
                if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.video)) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imgzoompath.get(position)));
                    intent.setDataAndType(Uri.parse(imgzoompath.get(position)), "video/*");
                    startActivity(intent);

                }*//*

            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));


        medialayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupInfo.this, MediaAcitivity.class);
                intent.putExtra("username", mGroupName);
                intent.putExtra("docid", mLocDbDocId);
                startActivity(intent);
            }
        });
        if (horizontalList.size() == 0) {
            media_lineralayout.setVisibility(View.GONE);
        }
    }
*/

    /**
     * based on type media file are execute
     */
    protected void mediafile() {
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
//            if (msgItem.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
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
//            }
            }
        }
        horizontalAdapter = new HorizontalAdapter(horizontalList);
        rvMedia.setAdapter(horizontalAdapter);
        int count = horizontalList.size();
        mediacount.setText((String.valueOf(count)));
        rvMedia.addOnItemTouchListener(new RItemAdapter(GroupInfo.this, rvMedia, new RItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position < 5) {
                    if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.picture)) {
                        Intent intent = new Intent(getApplication(), ImageZoom.class);
                        intent.putExtra("from", "media");
                        intent.putExtra("captiontext", imgCaptions.get(position));
                        intent.putExtra("image", imgzoompath.get(position));
                        startActivity(intent);
                    } else if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.document)) {
                        try {
                            CallIntentFileProvider(position);
                        } catch (ActivityNotFoundException e) {
                            Toast.makeText(GroupInfo.this, "No app installed to open this document", Toast.LENGTH_SHORT).show();
                        }
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
                            Toast.makeText(GroupInfo.this, "No app installed to play this video", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(GroupInfo.this, "No app installed to play this audio", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Log.e(TAG, "onItemClick: ", e);
                        }
//                        }
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
                Intent intent = new Intent(GroupInfo.this, MediaAcitivity.class);
                intent.putExtra("username", mGroupName);
                intent.putExtra("docid", mLocDbDocId);
                startActivity(intent);
            }
        });
        if (horizontalList.size() == 0) {
            media_lineralayout.setVisibility(View.GONE);
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
        Uri uri = FileProvider.getUriForFile(GroupInfo.this, BuildConfig.APPLICATION_ID, file);
        intent.setDataAndType(uri, mimeType);
        startActivity(intent);
    }

    /**
     * getGroupDetails
     */
    private void getGroupDetails() {
        if (ConnectivityInfo.isInternetConnected(GroupInfo.this)) {
            if (progressBar.getVisibility() == View.GONE) {
                progressBar.setVisibility(View.VISIBLE);
            }

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_GROUP_DETAILS);
            CryptLib cryptLib = null;
            try {
                cryptLib = new CryptLib();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            }
            try {

                JSONObject object = new JSONObject();
                object.put("from", mCurrentUserId);
                object.put("convId", mGroupId);
                event.setMessageObject(object);
               /* try {
//                    event.setMessageObject(AESUtil.encrypt(object.toString()));

                    System.out.println("Encrypt-->"+AESUtil.encrypt(object.toString()));
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
                EventBus.getDefault().post(event);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        } else {

            loadgroupdetail();
//            String mDocId = mCurrentUserId.concat("-").concat(mGroupId).concat("-g");
//            boolean hasGroupInfo = groupInfoSession.hasGroupInfo(mDocId);
//            GroupInfoPojo infoPojo;
//            if (hasGroupInfo) {
//                infoPojo = groupInfoSession.getGroupInfo(mDocId);
//                notifyAdapter(mDocId, infoPojo, true);
//            }

        }
    }

    /**
     * put camera image
     *
     * @param bundle
     */
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("ImageUri", cameraImageUri.toString());
    }

    /**
     * Clicking action
     *
     * @param view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnExitGroup:
                if (isLiveGroup) {
                    showExitGroupAlert();
                    exit_delete();
                } else {

                    Toast.makeText(GroupInfo.this, "You are not a member", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btnDeleteGroup:
                showDeleteGroupAlert();
                break;

            case R.id.tvAddMember:
                if (isLiveGroup) {
                    if (membersCount < 255) {
                        goAddMemberScreen();
                    } else {
                        Toast.makeText(GroupInfo.this, "Already this group have 256 members", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(GroupInfo.this, "You are not a member", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ivGroupDp:
                String docId = mCurrentUserId.concat("-").concat(mGroupId).concat("-g");
                GroupInfoPojo data = groupInfoSession.getGroupInfo(docId);
                if (data != null && data.getAvatarPath() != null && !data.getAvatarPath().isEmpty()) {
                    Intent imgIntent = new Intent(GroupInfo.this, ImageZoom.class);
                    imgIntent.putExtra("ProfilePath", Constants.SOCKET_IP + data.getAvatarPath());
                    startActivity(imgIntent);
                }
                break;

        }

    }

    /**
     * show Group Dp Alert
     */
    private void showGroupDpAlert() {
        List<MultiTextDialogPojo> labelsList = new ArrayList<>();
        MultiTextDialogPojo label = new MultiTextDialogPojo();
        label.setImageResource(R.drawable.blue_camera);
        label.setLabelText("Take Image From Camera");
        labelsList.add(label);

        label = new MultiTextDialogPojo();
        label.setImageResource(R.drawable.gallery);
        label.setLabelText("Add Image From Gallery");
        labelsList.add(label);

        CustomMultiTextItemsDialog dialog = new CustomMultiTextItemsDialog();
        dialog.setTitleText("Profile Picture");
        dialog.setNegativeButtonText("Cancel");
        dialog.setLabelsList(labelsList);

        dialog.setDialogItemClickListener(new CustomMultiTextItemsDialog.DialogItemClickListener() {
            @Override
            public void onDialogItemClick(int position) {
                switch (position) {

                    case 0:
                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                            StrictMode.setVmPolicy(builder.build());
                        }
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File cameraImageOutputFile = new File(
                                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                createCameraImageFileName());
                        cameraImageUri = Uri.fromFile(cameraImageOutputFile);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                        intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                        startActivityForResult(intent, CAMERA_REQUEST_CODE);

                        break;

                    case 1:
                        try {
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, GALLERY_REQUEST_CODE);
                        } catch (Exception e) {
                            Intent photoPickerIntent = new Intent();
                            photoPickerIntent.setType("image/*");
                            photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), GALLERY_REQUEST_CODE);
                            e.printStackTrace();
                        }
                        break;

                }
            }
        });
        dialog.show(getSupportFragmentManager(), "Profile Pic");
    }

    /**
     * create Camera Image FileName
     *
     * @return value
     */
    private String createCameraImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp + ".jpg";
    }

    /**
     * go to Add Member Screen
     */
    private void goAddMemberScreen() {
        //        Intent addMemberIntent = new Intent(GroupInfo.this, AddMemberToGroup.class);
        Intent addMemberIntent = new Intent(GroupInfo.this, SearchPeopleForGroupChat.class);
        /*i = new Intent(getActivity(), SearchPeopleForGroupChat.class);
        i.putExtra("type", "grp");
        startActivity(i);*/
        addMemberIntent.putExtra("type", "grp");
        addMemberIntent.putExtra("member", 1);
        addMemberIntent.putExtra("GroupId", mGroupId);
        addMemberIntent.putExtra("GroupName", mGroupName);
        addMemberIntent.putStringArrayListExtra("myGroupList", member);
//        addMemberIntent.putExtra("GroupUserIds", groupUserIds);
        startActivityForResult(addMemberIntent, ADD_MEMBER_REQUEST_CODE);
    }

    /**
     * Eventbus value
     *
     * @param event based on value call socket
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {

        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP)) {
            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                String groupAction = object.getString("groupType");

                if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EXIT_GROUP)) {
                    loadExitMessage(object);
                } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_DELETE_GROUP_MEMBER)) {
                    loadDeleteMemberMessage(object);
                } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_CHANGE_GROUP_DP)) {
                    loadGroupDpChangeMessage(object);
                } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_MAKE_GROUP_ADMIN)) {
                    loadMakeAdminMessage(object);
                } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_CHANGE_GROUP_NAME)) {
                    loadGroupDpChangeName(object);

                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP_DETAILS)) {

            loadGroupDetails(event);
//            new loadGroupUserDetails(event).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_IMAGE_UPLOAD)) {
            loadProfilePicMessage(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_PRIVACY_SETTINGS)) {
            loadPrivacySetting(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MUTE)) {
            loadMuteMessage(event);
        }
    }

    /**
     * load Mute Message
     *
     * @param event getting value from model class
     */
    private void loadMuteMessage(ReceviceMessageEvent event) {
        try {
            JSONObject object = new JSONObject(event.getObjectsArray()[0].toString());
            String from = object.getString("from");
            String convId = object.getString("convId");

            if (from.equalsIgnoreCase(mCurrentUserId) && convId.equalsIgnoreCase(mGroupId)) {
                String status = object.getString("status");
                if (status.equals("1")) {
                    swMute.setChecked(true);
                } else {
                    swMute.setChecked(false);
                }

                hideProgressDialog();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * update the adapter class
     *
     * @param event
     */
    private void loadPrivacySetting(ReceviceMessageEvent event) {
        adapter.notifyDataSetChanged();
    }

    /**
     * Make Admin Message
     *
     * @param object getting value from response
     */
    private void loadMakeAdminMessage(JSONObject object) {

        getGroupDetails();

        try {
            String err = object.getString("err");

            if (err.equalsIgnoreCase("0")) {
                String msg;
                String groupid = object.getString("groupId");
                if (mGroupId.equalsIgnoreCase(groupid)) {
                    String msgId = object.getString("id");
                    String timeStamp = object.getString("timeStamp");
                    String toDocId = object.getString("toDocId");
                    String from = object.getString("from");
                    String admin = object.getString("admin");
                    String adminmsddn = object.getString("newadminmsisdn");
                    String adminuser = object.getString("adminuser");
                  /*  TextMessage message = (TextMessage) MessageFactory.getMessage(MessageFactory.text, this);
                    MessageItemChat item = message.createMessageItem(false, "", MessageFactory.DELIVERY_STATUS_READ,
                            mGroupId, mGroupName);*/
                    String docId = SessionManager.getInstance(GroupInfo.this).getCurrentUserID().concat("-")
                            .concat(mGroupId).concat("-g");
                   /* item.setSenderName(mGroupName);
                    item.setBroadcastName(mGroupName);
                    item.setMessageId(docId.concat("-").concat(msgId));*/
                    GroupInfoPojo data = groupInfoSession.getGroupInfo(docId);
                    data.setAdminMembers(admin);
                    groupInfoSession.updateGroupInfo(docId, data);
                    // db.updateChatMessage(docId, item);
                    sendGroupAckToServer();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * load Profile Pic Message
     *
     * @param event getting value from model class
     */
    private void loadProfilePicMessage(ReceviceMessageEvent event) {
        Object[] array = event.getObjectsArray();
        try {
            JSONObject objects = new JSONObject(array[0].toString());
            String err = objects.getString("err");
            String message = objects.getString("message");
            message = "Group Icon Changed Successfully";
            if (err.equalsIgnoreCase("0") && objects.getString("file") != null) {
                String type = objects.getString("type");

                if (type.equalsIgnoreCase("group")) {
                    String from = objects.getString("from");
                    String avatar = objects.getString("file");

                    if (from.equalsIgnoreCase(mCurrentUserId) && type.equalsIgnoreCase("group")) {

                        long ts = Calendar.getInstance().getTimeInMillis();
                        String msgId = mLocDbDocId + "-" + ts;

                        JSONObject object = new JSONObject();
                        try {
                            object.put("groupType", SocketManager.ACTION_CHANGE_GROUP_DP);
                            object.put("from", mCurrentUserId);
                            object.put("groupId", mGroupId);
                            object.put("avatar", avatar);
                            object.put("id", ts);
                            object.put("toDocId", msgId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

//                        Toast.makeText(GroupInfo.this, message, Toast.LENGTH_SHORT).show();

                        SendMessageEvent messageEvent = new SendMessageEvent();
                        messageEvent.setEventName(SocketManager.EVENT_GROUP);
                        messageEvent.setMessageObject(object);
                        EventBus.getDefault().post(messageEvent);

                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * load Group Dp ChangeName
     *
     * @param object getting value from api response
     */
    private void loadGroupDpChangeName(JSONObject object) {
        try {
            if (object.getString("groupId").equalsIgnoreCase(mGroupId)) {
                mGroupName = object.getString("groupName");
                collapsingToolbarLayout.setTitle(mGroupName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * loadGroupDpChangeMessage
     *
     * @param object getting value from api response
     */
    private void loadGroupDpChangeMessage(JSONObject object) {
        try {
            String err = object.getString("err");
            if (err.equalsIgnoreCase("0")) {
                String from = object.getString("from");
                String message = object.getString("message");
                String groupId = object.getString("groupId");
                final String avatar = object.getString("avatar");
                String groupName = object.getString("groupName");

                if (groupId.equalsIgnoreCase(mGroupId)) {
                    String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
                    // For replace exiting path
                    GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
                    infoPojo.setAvatarPath(avatar);

//                    final String path = object.getString("avatar") + "?id=" + Calendar.getInstance().getTimeInMillis();
//                    final String path = Constants.SOCKET_IP.concat(avatar);
//                    AppUtils.loadImage(this, path, ivGroupDp);
//                    Glide.with(GroupInfo.this).load(Constants.SOCKET_IP.concat(avatar)).placeholder(R.mipmap.chat_attachment_profile_default_image_frame).crossFade().centerCrop().dontAnimate().skipMemoryCache(true).into(ivGroupDp);

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
//TODO tharani map
                            Glide.with(GroupInfo.this).load(Constants.SOCKET_IP.concat(avatar)).placeholder(R.mipmap.chat_attachment_profile_default_image_frame)
                                    .centerCrop().dontAnimate().skipMemoryCache(true).into(ivGroupDp);
                            hideProgressDialog();
                        }
                    }, 2000);

                    /*String path = Constants.SOCKET_IP.concat(avatar);
                    Picasso.with(GroupInfo.this).load(path).error(R.mipmap.group_chat_attachment_profile_icon).into(ivGroupDp);
*/

                }
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * load Delete Member Message
     *
     * @param object getting value from api response
     */
    private void loadDeleteMemberMessage(JSONObject object) {
        getGroupDetails();


        try {
            String err = object.getString("err");

            if (err.equalsIgnoreCase("0")) {
                String msg = object.getString("message");
                String msgId = object.getString("id");
                String timeStamp = object.getString("timeStamp");
                String removeId = object.getString("removeId");
                member.remove(removeId);
                String toDocId = object.getString("toDocId");
                String from = object.getString("from");
                if (removeId.equalsIgnoreCase(mCurrentUserId)) {
                    exit_delete();
                }

               /* TextMessage message = (TextMessage) MessageFactory.getMessage(MessageFactory.text, this);
                MessageItemChat item = message.createMessageItem(false, msg, MessageFactory.DELIVERY_STATUS_READ,
                        mGroupId, mGroupName);
                String docId = SessionManager.getInstance(GroupInfo.this).getCurrentUserID().concat("-")
                        .concat(mGroupId).concat("-g");
                item.setSenderName(mGroupName);
                item.setBroadcastName(mGroupName);
                item.setMessageId(docId.concat("-").concat(msgId));
                db.updateChatMessage(docId, item);
*/
                // sendGroupAckToServer();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * send Group AckToServer
     */
    private void sendGroupAckToServer() {
        SendMessageEvent event = new SendMessageEvent();
        try {
            JSONObject object = new JSONObject();
            object.put("from", mCurrentUserId);
            object.put("groupId", mGroupId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ExitMessage
     *
     * @param object getting value from api response
     */
    private void loadExitMessage(JSONObject object) {
        try {
            String msg = object.getString("message");
            String groupId = object.getString("groupId");
            String timeStamp = object.getString("timeStamp");
            String from = object.getString("from");

            if (mGroupId.equalsIgnoreCase(groupId) && from.equalsIgnoreCase(mCurrentUserId)) {
//                removeUser(groupId);

                // For finish ChatViewActivity and go ChatList page
                Intent exitIntent = new Intent();
                exitIntent.putExtra("exitFromGroup", true);
                setResult(RESULT_OK, exitIntent);
                finish();
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    private void removeUser(String groupId) {
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_REMOVE_USER);
        JSONObject object = new JSONObject();
        try {
            object.put("_id", groupId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageEvent.setMessageObject(object);
        EventBus.getDefault().post(messageEvent);
    }

    /**
     * load GroupDetails
     *
     * @param event getting value from api response
     */
    private void loadGroupDetails(ReceviceMessageEvent event) {
        progressBar.setVisibility(View.GONE);
        if (progress != null) {
            progress.dismiss();
        }
        try {
            Object[] array = event.getObjectsArray();
            JSONObject objects = new JSONObject(array[0].toString());

            String displayName = objects.getString("DisplayName");

            String groupCreatedAt = objects.getString("GroupcreatedAt");//time stamp
            String displayPic = objects.getString("GroupIcon");
            String isAdmin = objects.getString("isAdmin");
            String groupId = objects.getString("_id");

            if (groupId.equalsIgnoreCase(mGroupId)) {
                groupUserIds = "";
                mCurrentUserData = null;

                if (displayName != null && !displayName.equals("")) {
                    mGroupName = displayName;
                    collapsingToolbarLayout.setTitle(mGroupName);
                }

                if (isAdmin.equalsIgnoreCase("1") && isLiveGroup) {
                    isAdminUser = true;
                    tvAddMember.setVisibility(View.VISIBLE);

                    if (groupMenu != null) {
                        MenuItem menuItem = groupMenu.findItem(R.id.addNewMember);
                        menuItem.setVisible(true);
                        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                    }
                } else {
                    tvAddMember.setVisibility(View.GONE);
                }
                member = new ArrayList<>();
                member.clear();
                allMembersList.clear();
                savedMembersList.clear();
                unsavedMembersList.clear();
                String groupContactNames = "";

                JSONArray arrMembers = objects.getJSONArray("GroupUsers");
                membersCount = arrMembers.length();

                for (int i = 0; i < arrMembers.length(); i++) {
                    JSONObject userObj = arrMembers.getJSONObject(i);
                    String userId = userObj.getString("id");
                    String active = userObj.getString("active");
                    String isDeleted = userObj.getString("isDeleted");
                    String msisdn = userObj.getString("msisdn");
                    String phNumber = userObj.getString("msisdn");
                    String name = userObj.getString("ContactName");
                    String status = userObj.getString("Status");
                    String userDp = userObj.getString("avatar");
                    String adminUser = userObj.getString("isAdmin");
                    String contactmsisdn = userObj.getString("ContactName");
                    String isExitsContact = userObj.getString("isExitsContact");
                    member.add(userId);
                    String contact = userObj.getString("Name");
                    try {
                        if (ChatappRegularExp.isEncodedBase64String(status)) {
                            byte[] arrStatus = Base64.decode(status, Base64.DEFAULT);
                            status = new String(arrStatus, "UTF-8");
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        status = "";
                    }


                    //   String contactName = getcontactname.getSendername(userId, msisdn);

                    String contactName = contact;


                    byte[] data1 = Base64.decode(contactName, Base64.DEFAULT);

                    try {
                        contactName = new String(data1, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    if (mCurrentUserId.equalsIgnoreCase(userId)) {
                        contactName = "You";
                        status = SessionManager.getInstance(this).getcurrentUserstatus();
                    }

                    groupContactNames = groupContactNames.concat(contactName);

                    GroupMembersPojo data = new GroupMembersPojo();
                    data.setUserId(userId);
                    data.setActive(active);
                    data.setIsDeleted(isDeleted);
                    data.setMsisdn(msisdn);
                    data.setPhNumber(phNumber);
                    data.setName(name);
                    data.setStatus(status);
                    data.setUserDp(userDp);
                    data.setIsAdminUser(adminUser);
                    data.setContactName(contactName);

                    groupUserIds = groupUserIds.concat(userId);
                    if (userId.equalsIgnoreCase(mCurrentUserId)) {
                        mCurrentUserData = data;
                    } else {
                        if (msisdn.equalsIgnoreCase(contactName)) {
                            unsavedMembersList.add(data);
                        } else {
                            savedMembersList.add(data);
                        }
                    }

                    if ((arrMembers.length() - 1) > i) {
                        try {
                            groupUserIds = groupUserIds.concat(",");
                            groupContactNames = groupContactNames.concat(",");
                        } catch (Exception e) {
                        }

                    }
                }

                String mDocId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
                boolean hasGroupInfo = groupInfoSession.hasGroupInfo(mDocId);
                GroupInfoPojo infoPojo;
                if (hasGroupInfo) {
                    infoPojo = groupInfoSession.getGroupInfo(mDocId);
                } else {
                    infoPojo = new GroupInfoPojo();
                }
                infoPojo.setGroupId(mGroupId);
                infoPojo.setGroupName(mGroupName);
                infoPojo.setGroupMembers(groupUserIds);
                infoPojo.setAvatarPath(displayPic);
                infoPojo.setIsAdminUser(isAdmin);

                tvMembersCount.setText(membersCount + "/256");
                //  tvGroupCreatedInfo.setText("Created by "+ TimeStampUtils.getMessageTStoDate(this,groupCreatedAt));
                notifyAdapter(mDocId, infoPojo, true);

            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    /**
     * adapter update
     *
     * @param docId      value of docid
     * @param infoPojo   model class value
     * @param needUpdate boolean value
     */
    private void notifyAdapter(String docId, GroupInfoPojo infoPojo, boolean needUpdate) {
        Collections.sort(savedMembersList, Getcontactname.groupMemberAsc);
        Collections.sort(unsavedMembersList, Getcontactname.groupMemberAsc);
        allMembersList.addAll(savedMembersList);
        if (membersCount > 0)
            allMembersList.addAll(unsavedMembersList);

        if (mCurrentUserData != null) {
            allMembersList.add(mCurrentUserData);
        }

        if (needUpdate) {
            String groupContactNames = "";
            String groupUserIds = "";
            for (int i = 0; i < allMembersList.size(); i++) {
                if (i != 0) {
                    groupContactNames = groupContactNames + ", ";
                    groupUserIds = groupUserIds + ",";
                }
                groupContactNames = groupContactNames + allMembersList.get(i).getContactName();
                groupUserIds = groupUserIds + allMembersList.get(i).getUserId();
            }
            infoPojo.setGroupContactNames(groupContactNames);
            infoPojo.setGroupMembers(groupUserIds);
//            groupInfoSession.updateGroupInfo(docId, infoPojo);
        }

        adapter.notifyDataSetChanged();
    }

    /**
     * group details value
     */
    private void loadgroupdetail() {

        String docId = mCurrentUserId.concat("-").concat(mGroupId).concat("-g");

        boolean hasInfo = groupInfoSession.hasGroupInfo(docId);
        if (hasInfo) {
            GroupInfoPojo data = groupInfoSession.getGroupInfo(docId);
            String displayName = data.getGroupName();
            String displayPic = data.getAvatarPath();
            String isAdmin = data.getIsAdminUser();
            String groupId = data.getGroupId();
            isLiveGroup = data.isLiveGroup();
            Log.e("Check with server data", displayPic);

            if (groupId != null && groupId.equalsIgnoreCase(mGroupId)) {
                groupUserIds = "";

                if (displayName != null && !displayName.equals("")) {
                    mGroupName = displayName;
                    collapsingToolbarLayout.setTitle(mGroupName);

                }


                allMembersList.clear();
                savedMembersList.clear();
                unsavedMembersList.clear();

                String groupmembers = data.getGroupMembers();
                String adminuser = data.getAdminMembers();
                String adminmember[] = {};
                if (adminuser != null) {
                    adminmember = adminuser.split(",");
                }
                String array[] = groupmembers.split(",");
                if (groupmembers != null && !groupmembers.isEmpty()) {
                    membersCount = array.length;
                }

                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(GroupInfo.this);

                for (int i = 0; i < array.length; i++) {

                    String userId = null, msisdn = null, phNumber = null, status = null, userDp = null;
                    String contactName = "";

//                    if (array[i].equalsIgnoreCase(mCurrentUserId)) {
//
//                        } else {
                    if (array[i].equalsIgnoreCase(mCurrentUserId)) {
                        userId = mCurrentUserId;
                        msisdn = sessionManager.getPhoneNumberOfCurrentUser();
                        phNumber = sessionManager.getPhoneNumberOfCurrentUser();
                        userDp = sessionManager.getUserProfilePic();

                    } else {
                        ChatappContactModel userInfo = contactDB_sqlite.getUserOpponenetDetails(array[i]);
                        if (userInfo != null) {
                            userId = userInfo.get_id();
                            msisdn = userInfo.getMsisdn();
                            phNumber = userInfo.getMsisdn();
                            status = userInfo.getStatus();
                            contactName = userInfo.getFirstName();
                            if (ChatappRegularExp.isEncodedBase64String(status)) {
                                try {
                                    status = new String(Base64.decode(status, Base64.DEFAULT), "UTF-8");
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                            userDp = userInfo.getAvatarImageUrl();
                        }

                    }
                    String active = "";
                    String adminUser = "";

                    String isExitsContact = "";
                    String isDeleted = "";
                    for (int j = 0; j < adminmember.length; j++) {
                        if (array[i].equalsIgnoreCase(adminmember[j])) {
                            active = "";
                            adminUser = "1";
                            isExitsContact = "";
                            isDeleted = "";
                        }

                    }
                    // contactName = getcontactname.getSendername(userId, phNumber);

                    if (mCurrentUserId.equalsIgnoreCase(userId)) {
                        contactName = "You";
                        status = SessionManager.getInstance(this).getcurrentUserstatus();
                    }

                    if (adminUser.equalsIgnoreCase("1") && isLiveGroup) {
                        isAdminUser = true;
                        if (mCurrentUserId.equalsIgnoreCase(array[i])) {
                            tvAddMember.setVisibility(View.VISIBLE);
                            if (groupMenu != null) {
                                MenuItem menuItem = groupMenu.findItem(R.id.addNewMember);
                                menuItem.setVisible(true);
                                menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                            }
                        } else {
                            isAdminUser = false;
                            tvAddMember.setVisibility(View.GONE);
                        }

                    }
                    GroupMembersPojo value = new GroupMembersPojo();
                    value.setUserId(userId);
                    value.setActive(active);
                    value.setIsDeleted(isDeleted);
                    value.setMsisdn(msisdn);
                    value.setPhNumber(phNumber);
                    value.setName(contactName);
                    value.setStatus(status);
                    value.setUserDp(userDp);
                    value.setIsAdminUser(adminUser);
                    value.setContactName(contactName);
                    if (userId != null) {
                        groupUserIds = groupUserIds.concat(userId);
                    }

//                        if(userId.equalsIgnoreCase(mCurrentUserId)){
//                            groupUserIds = groupUserIds.concat(mCurrentUserId);
//                        }else{
//
//                        }
                    if (userId != null && userId.equalsIgnoreCase(mCurrentUserId)) {
                        mCurrentUserData = value;
                    } else {
                        if (msisdn != null) {
                            if (msisdn.equalsIgnoreCase(contactName)) {
                                unsavedMembersList.add(value);
                            } else {
                                savedMembersList.add(value);
                            }
                        }
                    }
                }
            }
            tvMembersCount.setText(membersCount + "/256");
            notifyAdapter(docId, data, false);
        }

        //  }
    }


    /**
     * Group Exit event
     */
    private void performGroupExit() {
        long ts = Calendar.getInstance().getTimeInMillis();
        String msgId = mCurrentUserId + "-" + mGroupId + "-g-" + ts;

        try {
            JSONObject exitObject = new JSONObject();
            exitObject.put("groupType", SocketManager.ACTION_EXIT_GROUP);
            exitObject.put("from", mCurrentUserId);
            exitObject.put("groupId", mGroupId);
            exitObject.put("id", ts);
            exitObject.put("toDocId", msgId);

            SendMessageEvent exitGroupEvent = new SendMessageEvent();
            exitGroupEvent.setEventName(SocketManager.EVENT_GROUP);
            exitGroupEvent.setMessageObject(exitObject);
            EventBus.getDefault().post(exitGroupEvent);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Delete group function
     */
    private void performDeleteGroup() {
        try {
            JSONObject object = new JSONObject();
            object.put("convId", mGroupId);
            object.put("from", mCurrentUserId);
            object.put("type", MessageFactory.CHAT_TYPE_GROUP);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_DELETE_CHAT);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String docId = mCurrentUserId.concat("-").concat(mGroupId).concat("-g");
        MessageDbController db = CoreController.getDBInstance(this);
        db.deleteChat(docId, MessageFactory.CHAT_TYPE_GROUP);

    }

    /**
     * menu view
     *
     * @param menu layout
     * @return value
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.group_info, menu);
        this.groupMenu = menu;
        groupMenu.findItem(R.id.addNewMember).setVisible(false);
        if (!isLiveGroup) {
            groupMenu.findItem(R.id.editGroupName).setVisible(false);
            groupMenu.findItem(R.id.changeGroupDp).setVisible(false);
        }
        return true;
    }

    /**
     * option select menu
     *
     * @param item item selected
     * @return value
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.editGroupName:
                if (isLiveGroup) {
                    Intent editNameIntent = new Intent(GroupInfo.this, ChangeGroupName.class);
                    editNameIntent.putExtra("GroupId", mGroupId);
                    editNameIntent.putExtra("GroupName", mGroupName);
                    startActivityForResult(editNameIntent, CHANGE_GROUP_NAME_REQUEST_CODE);
                } else {
                    Toast.makeText(GroupInfo.this, "You are not a member in this group", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.addNewMember:
                goAddMemberScreen();
                break;

            case R.id.changeGroupDp:

                if (isLiveGroup) {
                    showGroupDpAlert();
                } else {
                    Toast.makeText(GroupInfo.this, "You are not a member", Toast.LENGTH_SHORT).show();
                }

                break;

            case android.R.id.home:
              /*  if (session.getmute(mGroupId + "mute")) {
                    swMute.setChecked(false);
                } else {
                    swMute.setChecked(true);
                }*/
                changemute();
                finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Change mute value
     */
    private void changemute() {
        Intent muteintant = new Intent();
        muteintant.putExtra("ismutechange", ismutechange);
        muteintant.putExtra("isgroupempty", isgroupemty);
        setResult(RESULT_OK, muteintant);
    }

    /**
     * Exit group Alert
     */
    private void showExitGroupAlert() {

        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setNegativeButtonText("Cancel");
        dialog.setPositiveButtonText("Exit");
        dialog.setMessage("Exit " + mGroupName + " group");
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                if (ConnectivityInfo.isInternetConnected(getApplication())) {
                    performGroupExit();
                    /*String docId = mCurrentUserId.concat("-").concat(mGroupId).concat("-g");
                    GroupInfoPojo data = groupInfoSession.getGroupInfo(docId);
                    String memerlist = data.getGroupMembers();
                    String[] useridsplitmemeber = memerlist.split(",");
                    int countofmember = useridsplitmemeber.length;
                    String adminuserid = data.getAdminMembers();
                    String[] useridsplit = adminuserid.split(",");
                    int countofadmin = useridsplit.length;
                    String isadminuser = data.getIsAdminUser();
                    if (countofadmin == 1 && isadminuser.equals("1") && countofmember > 1) {
                        performMakeAdminUser(useridsplitmemeber[0]);
                    }
                    if (memerlist.equals("") || countofmember <= 0) {
                        isgroupemty = true;
                    }*/
                } else {
                    Toast.makeText(GroupInfo.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "Exit group alert");
    }

    /**
     * Click action for specific view
     *
     * @param view layout binding
     * @param position specific item
     */
    @Override
    public void onItemClick(View view, int position) {
        try {
            if (!allMembersList.get(position).getUserId().equalsIgnoreCase(mCurrentUserId)) {
                //showDeleteMemberAlert(position);
                valueposition = position;
                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(GroupInfo.this);


                if (contactDB_sqlite.checkUserRequestStatus(allMembersList.get(position).getUserId())) {
                    isContact = true;
                    ShowListdialog(position);
                } else {
                    if (isAdminUser) {
                        isContact = false;
                        ShowListdialog(position);
                    } /*else {

                        Toast.makeText(this, "This user is not in your contact List", Toast.LENGTH_SHORT).show();
                    }*/
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "onItemClick: ", e);
        }
    }

    /**
     * On long Click action
     *
     * @param view     specific view
     * @param position value of position
     */
    @Override
    public void onItemLongClick(View view, int position) {
        if (isAdminUser && !allMembersList.get(position).getUserId().equalsIgnoreCase(mCurrentUserId)
                && !allMembersList.get(position).getIsAdminUser().equals("1")) {
            showMakeAdminAlert(position);
        }
    }

    /**
     * Show admin alert
     *
     * @param position value of position
     */
    private void showMakeAdminAlert(final int position) {

        final String msg = "Make " + allMembersList.get(position).getContactName()
                + " as admin in " + mGroupName + " group";

        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setNegativeButtonText("Cancel");
        dialog.setPositiveButtonText("Ok");
        dialog.setMessage(msg);
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                if (ConnectivityInfo.isInternetConnected(getApplication())) {
                    performMakeAdminUser(allMembersList.get(position).getUserId());
                } else {
                    Toast.makeText(GroupInfo.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "Make admin alert");
    }

    /**
     * Make a admin in user
     *
     * @param adminUserId user id
     */
    private void performMakeAdminUser(String adminUserId) {


        SendMessageEvent makeAdminEvent = new SendMessageEvent();
        makeAdminEvent.setEventName(SocketManager.EVENT_GROUP);

        try {
            String docId = mCurrentUserId.concat("-").concat(mGroupId).concat("-g");
            Calendar calendar = Calendar.getInstance();
            String timeStamp = String.valueOf(calendar.getTimeInMillis());

            JSONObject adminObj = new JSONObject();
            adminObj.put("groupType", SocketManager.ACTION_MAKE_GROUP_ADMIN);
            adminObj.put("from", mCurrentUserId);
            adminObj.put("groupId", mGroupId);
            adminObj.put("adminuser", adminUserId);
            adminObj.put("id", timeStamp);
            adminObj.put("toDocId", docId.concat("-").concat(timeStamp));

            makeAdminEvent.setMessageObject(adminObj);


            /*item.setSenderName(mGroupName);
            item.setBroadcastName(mGroupName);
            item.setMessageId(docId.concat("-").concat(timeStamp));*/
//            Log.e("<<<<<-------Doc Id--------->>>>>", docId);
//            db.updateChatMessage(message.getId(), item);
            EventBus.getDefault().post(makeAdminEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Delete member Alert
     *
     * @param position value of position
     */
    private void showDeleteMemberAlert(final int position) {

        final String msg = "Remove  " + allMembersList.get(position).getContactName()
                + " from " + mGroupName + " group";

        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setNegativeButtonText("Cancel");
        dialog.setPositiveButtonText("Confirm");
        dialog.setMessage(msg);
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                if (ConnectivityInfo.isInternetConnected(getApplication())) {
                    if (position > -1) {
                        performDeleteMember(msg, allMembersList.get(position).getUserId());
                    }
                } else {
                    Toast.makeText(GroupInfo.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "Delete member alert");
    }

    /*private void ShowListdialog(final int position) {
        String[] val = {"Message" + "\t" + membersList.get(position).getContactName(), "View" + "\t" + membersList.get(position).getContactName(),
                "Remove" + "\t" + membersList.get(position).getContactName()};
        String[] val2 = {"Message" + "\t" + membersList.get(position).getContactName(), "View" + "\t" + membersList.get(position).getContactName()};

        listDialog = new Dialog(this);
        LayoutInflater li = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.navigation_list, null, false);
        listDialog.setContentView(v);
        listDialog.setCancelable(true);


        ListView list1 = (ListView) listDialog.findViewById(R.id.listview);
        if (isAdminUser) {
            list1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, val));
        } else {
            list1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, val2));

        }


        listDialog.show();

        list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {


                String name = membersList.get(valueposition).getName();
                contactname = membersList.get(valueposition).getContactName();
                uid = membersList.get(valueposition).getUserId();
                image = membersList.get(valueposition).getUserDp();
                msisdn = membersList.get(valueposition).getMsisdn();

                switch (position) {

                    case 0:
                        Intent intent = new Intent(getApplicationContext(), ChatViewActivity.class);

                        intent.putExtra("msisdn", "");
                        intent.putExtra("Username", contactname);
                        intent.putExtra("documentId", uid);
                        intent.putExtra("receiverUid", "");
                        intent.putExtra("Image", image);
                        intent.putExtra("type", 0);
                        intent.putExtra("receiverName", contactname);
                        intent.putExtra("msisdn", msisdn);
                        startActivity(intent);
                        listDialog.dismiss();
                        break;

                    case 1:
                        Intent infoIntent = new Intent(GroupInfo.this, UserInfo.class);
                        infoIntent.putExtra("UserId", uid);
                        infoIntent.putExtra("UserName", contactname);
                        infoIntent.putExtra("UserAvatar", image);
                        startActivity(infoIntent);
                        listDialog.dismiss();
                        break;

                    case 2:
                        showDeleteMemberAlert(valueposition);
                        listDialog.dismiss();
                        break;

                }
            }
        });
    }*/

    /**
     * list of dialog
     *
     * @param position based on position
     */
    private void ShowListdialog(final int position) {
        List<MultiTextDialogPojo> labelsList = new ArrayList<>();
        MultiTextDialogPojo label = new MultiTextDialogPojo();
        // label.setImageResource(R.drawable.blue_camera);
        if (isContact) {
            label.setLabelText("Message" + "\t" + allMembersList.get(position).getContactName());
            labelsList.add(label);

            label = new MultiTextDialogPojo();
            label.setLabelText("View" + "\t" + allMembersList.get(position).getContactName());
            labelsList.add(label);
        }
        if (isAdminUser) {
            label = new MultiTextDialogPojo();
            label.setLabelText("Remove" + "  " + allMembersList.get(position).getContactName());
            labelsList.add(label);
        }
        CustomMultiTextItemsDialog dialog = new CustomMultiTextItemsDialog();

        dialog.setLabelsList(labelsList);

        dialog.setDialogItemClickListener(new CustomMultiTextItemsDialog.DialogItemClickListener() {
            @Override
            public void onDialogItemClick(int position) {

                String name = allMembersList.get(valueposition).getName();
                contactname = allMembersList.get(valueposition).getContactName();
                uid = allMembersList.get(valueposition).getUserId();
                image = allMembersList.get(valueposition).getUserDp();
                msisdn = allMembersList.get(valueposition).getMsisdn();

                switch (position) {

                    case 0:
                        if (!isContact) {
                            showDeleteMemberAlert(valueposition);
                            break;

                        }
                        ChatLockPojo lockPojo = getChatLockdetailfromDB(position);

                        if (sessionManager.getLockChatEnabled().equals("1") && lockPojo != null) {

                            String stat = lockPojo.getStatus();
                            String pwd = lockPojo.getPassword();

                            String documentid = mCurrentUserId + "-" + uid;
                            if (stat.equals("1")) {
                                openUnlockChatDialog(documentid, stat, pwd, contactname, image, msisdn);
                            } else {
                                navigateTochatviewpage();
                            }
                        } else {
                            navigateTochatviewpage();
                        }
                        break;

                    case 1:
                        Intent infoIntent = new Intent(GroupInfo.this, UserInfo.class);
                        if (contactname == null || contactname.isEmpty())
                            contactname = msisdn;
                        infoIntent.putExtra("UserId", uid);
                        infoIntent.putExtra("UserName", contactname);
                        infoIntent.putExtra("UserAvatar", image);
                        infoIntent.putExtra("UserNumber", msisdn);
                        infoIntent.putExtra("FromSecretChat", false);
                        startActivity(infoIntent);
                        break;

                    case 2:
                        showDeleteMemberAlert(valueposition);
                        break;

                }
            }
        });
        dialog.show(getSupportFragmentManager(), "Profile Pic");
    }

    /**
     * open Unlock Chat Dialog
     *
     * @param documentid  doc id
     * @param stat        value of state
     * @param pwd         value of password
     * @param contactname value of contactname
     * @param image       value of image
     * @param msisdn      value msisdn
     */
    public void openUnlockChatDialog(String documentid, String stat, String pwd, String contactname, String image, String msisdn) {

        String convId = userInfoSession.getChatConvId(documentid);
        ChatLockPwdDialog dialog = new ChatLockPwdDialog();
        dialog.setTextLabel1("Enter your Password");
        dialog.setEditTextdata("Enter new Password");
        dialog.setforgotpwdlabel("Forgot Password");
        dialog.setHeader("Unlock Chat");
        dialog.setButtonText("Unlock");
        Bundle bundle = new Bundle();
        bundle.putString("convID", convId);
        bundle.putString("status", "1");
        bundle.putString("pwd", pwd);
        bundle.putString("contactName", contactname);
        bundle.putString("avatar", image);
        bundle.putString("msisdn", msisdn);
        String uid = documentid.split("-")[1];
        bundle.putString("docid", uid);
        bundle.putString("page", "chatlist");
        bundle.putString("type", "single");
        bundle.putString("from", mCurrentUserId);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "chatunLock");
    }

    /**
     * navigate To chatviewpage
     */
    private void navigateTochatviewpage() {
        Intent intent = new Intent(getApplicationContext(), ChatViewActivity.class);
        if (contactname == null || contactname.isEmpty())
            contactname = msisdn;
        intent.putExtra("msisdn", "");
        intent.putExtra("Username", contactname);
        intent.putExtra("documentId", uid);
        intent.putExtra("receiverUid", "");
        intent.putExtra("Image", image);
        intent.putExtra("type", 0);
        intent.putExtra("receiverName", contactname);
        intent.putExtra("msisdn", msisdn);
        startActivity(intent);
    }

    /**
     * perform DeleteMember
     *
     * @param msg
     * @param deleteUserId getting userid
     */
    private void performDeleteMember(String msg, String deleteUserId) {
        SendMessageEvent exitGroupEvent = new SendMessageEvent();
        exitGroupEvent.setEventName(SocketManager.EVENT_GROUP);

        try {
            String docId = mCurrentUserId.concat("-").concat(mGroupId).concat("-g");
            long ts = Calendar.getInstance().getTimeInMillis();

            JSONObject deleteObj = new JSONObject();
            deleteObj.put("groupType", SocketManager.ACTION_DELETE_GROUP_MEMBER);
            deleteObj.put("from", mCurrentUserId);
            deleteObj.put("groupId", mGroupId);
            deleteObj.put("removeId", deleteUserId);
            deleteObj.put("id", ts);
            deleteObj.put("toDocId", docId.concat("-").concat(ts + ""));

            exitGroupEvent.setMessageObject(deleteObj);
            EventBus.getDefault().post(exitGroupEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * getting value from activity
     *
     * @param requestCode requestCode based geeting value
     * @param resultCode  resultCode based geeting value
     * @param data        getting the data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_MEMBER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean isMemberAdded = data.getBooleanExtra("MemberAdded", false);
            if (isMemberAdded) {
                getGroupDetails();
            }
        } else if (requestCode == CHANGE_GROUP_NAME_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean isNameChanged = data.getBooleanExtra("NameChanged", false);
            if (isNameChanged) {
                String newGroupName = data.getStringExtra("newGroupName");
                Toast.makeText(GroupInfo.this, "Group name changed to " + newGroupName, Toast.LENGTH_SHORT).show();
                getGroupDetails();
            }
        } else if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    if (data != null) {
                        Uri selectedImageUri = data.getData();
                        if (ConnectivityInfo.isInternetConnected(getApplication())) {
                            beginCrop(selectedImageUri);
                        } else {
                            Toast.makeText(GroupInfo.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (OutOfMemoryError | NullPointerException e) {
                }
            } else {
                if (resultCode == Activity.RESULT_CANCELED) {

                } else {
                    Toast.makeText(this, "Sorry! Failed to capture image",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                /*if (data != null) {
                    Bitmap photo = (Bitmap) data.getExtras().get("data");
                    Uri tempUri = getImageUri(getApplicationContext(), photo);
                    beginCrop(tempUri);
                }*/
                if (ConnectivityInfo.isInternetConnected(getApplication())) {
                    beginCrop(cameraImageUri);
                } else {
                    Toast.makeText(GroupInfo.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            } else {
                if (resultCode == Activity.RESULT_CANCELED) {

                } else {
                    Toast.makeText(this, "Sorry! Failed to capture image",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(data);
            String filePath = uri.getPath();

            File image = new File(filePath);
            Bitmap compressBmp = null;
            try {
                compressBmp = new Compressor(GroupInfo.this).compressToBitmap(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
            uploadImage(compressBmp);

            /*try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                Bitmap alignedBitmap = ChatappImageUtils.getAlignedBitmap(bitmap, filePath);

            } catch (IOException e) {
                e.printStackTrace();
            }*/
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    /**
     * uploadImage
     *
     * @param circleBmp image value
     */
    private void uploadImage(Bitmap circleBmp) {

        if (circleBmp != null) {
            try {
                /*File imgDir = new File(MessageFactory.PROFILE_IMAGE_PATH);
                if (!imgDir.exists()) {
                    imgDir.mkdirs();
                }
                showProgressDialog();
                String profileImgPath = imgDir + "/" + Calendar.getInstance().getTimeInMillis() + "_pro.jpg";

                File file = new File(profileImgPath);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();

                OutputStream outStream = new FileOutputStream(file);
                circleBmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();

                String serverFileName = mCurrentUserId + "-" + mGroupId + "-g-"
                        + Calendar.getInstance().getTimeInMillis() + ".jpg";

                PictureMessage message = new PictureMessage(GroupInfo.this);
                JSONObject object = (JSONObject) message.createGroupProfileImageObject(serverFileName, profileImgPath);
                FileUploadDownloadManager fileUploadDownloadMgnr = new FileUploadDownloadManager(GroupInfo.this);
                fileUploadDownloadMgnr.startFileUpload(EventBus.getDefault(), object);
*/


                showProgressDialog();

                File file = getBitmapFile(circleBmp, "pro_group_" + Calendar.getInstance().getTimeInMillis() + ".jpg");

                Log.d("USER_PROFILE", "uploadImage: " + file.getAbsolutePath());
                Log.d("USER_PROFILE", "file exist: " + file.exists());

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
                String serverFileName = mCurrentUserId + "-" + mGroupId + "-g-"
                        + Calendar.getInstance().getTimeInMillis() + ".jpg";
                PictureMessage message = new PictureMessage(GroupInfo.this);
                JSONObject object = (JSONObject) message.createGroupProfileImageObject(serverFileName, file.getAbsolutePath());
                FileUploadDownloadManager fileUploadDownloadMgnr = new FileUploadDownloadManager(GroupInfo.this);
                fileUploadDownloadMgnr.startFileUpload(EventBus.getDefault(), object);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get BitmapFile
     *
     * @param imageToSave value of image resource
     * @param fileName    value of filename
     * @return value
     */
    private File getBitmapFile(Bitmap imageToSave, String fileName) {


        String path;
        path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
/*        File direct = new File(Environment.getExternalStorageDirectory() + "/chatapp");

        if (!direct.exists()) {

           boolean created= direct.mkdirs();
           direct.setWritable(true);

            Log.d("USER_PROFILE", "dirCreated: "+created);


        }
        else {
            Log.d("USER_PROFILE", "dir exists ");
        }*/
        File file = new File(path + "/" + fileName);

        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("USER_PROFILE", "getBitmapFile: " + e.getMessage());
        }

        return file;
    }

    /**
     * Start Eventbus
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(GroupInfo.this);
    }

    /**
     * Stop eventbus
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(GroupInfo.this);
    }

    /**
     * Exit from group view
     */
    private void exit_delete() {
        boolean hasGroupInfo = groupInfoSession.hasGroupInfo(mCurrentUserId + "-" + mGroupId + "-g");
        GroupInfoPojo infoPojo;
        if (hasGroupInfo) {
            infoPojo = groupInfoSession.getGroupInfo(mCurrentUserId + "-" + mGroupId + "-g");
            if (infoPojo.isLiveGroup()) {
                btnExitGroup.setVisibility(View.VISIBLE);
                btnDeleteGroup.setVisibility(View.GONE);
            } else {
                btnExitGroup.setVisibility(View.GONE);
                btnDeleteGroup.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * Delete Group Alert
     */
    private void showDeleteGroupAlert() {

        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setNegativeButtonText("Cancel");
        dialog.setPositiveButtonText("Delete");
        dialog.setMessage("Delete " + mGroupName + " group");
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                performDeleteGroup();
                Intent intent = new Intent(GroupInfo.this, HomeScreen.class);
                startActivity(intent);
            }

            @Override
            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "Delete group alert");
    }

    /**
     * Mute Dialog Closed
     * @param isMuted boolean value
     */
    @Override
    public void onMuteDialogClosed(boolean isMuted) {
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        MuteStatusPojo muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, null, mGroupId, false);

        swMute.setChecked((muteData != null && muteData.getMuteStatus().equals("1")));
    }

    /**
     * getTimeString
     * @param millis long value
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
     * Mute option enabling
     */
    private void mute_gone_exit() {
        if (isLiveGroup) {
            swMute.setVisibility(View.VISIBLE);
            mute.setText("Mute");
        } else {
            swMute.setVisibility(View.GONE);
            mute.setText("You're no longer a participant in this group");
        }
    }

    /**
     * get Chat Lockd etail from Database
     * @param position value of position
     * @return value
     */
    private ChatLockPojo getChatLockdetailfromDB(int position) {
        String id = mCurrentUserId.concat("-").concat(uid);
        MessageDbController dbController = CoreController.getDBInstance(this);
        String convId = userInfoSession.getChatConvId(id);
        String receiverId = userInfoSession.getReceiverIdByConvId(convId);
        ChatLockPojo pojo = dbController.getChatLockData(receiverId, MessageFactory.CHAT_TYPE_SINGLE);
        return pojo;
    }

    /**
     * View
     */
    public void broadcast() {
        mute_layout.setVisibility(View.GONE);
        tvAddMember.setVisibility(View.GONE);
        btnExitGroup.setVisibility(View.GONE);
        btnDeleteGroup.setVisibility(View.GONE);
    }


    /**
     * Horizontal Adapter
     */
    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {

        private List<MessageItemChat> horizontalList;

        public HorizontalAdapter(List<MessageItemChat> horizontalList) {
            this.horizontalList = horizontalList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.horizontal_item_view, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            try {
                holder.Vido.setVisibility(View.GONE);
                holder.duration.setVisibility(View.GONE);

            /*if (horizontalList.size() < 5) {
                if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.picture)) {
                    if (horizontalList.get(position).getImagePath() != null) {
                        String path = horizontalList.get(position).getImagePath();
                        File file = new File(path);
                        if (file.exists()) {
//                            Bitmap myBitmap = BitmapFactory.decodeFile(path);
                            Bitmap myBitmap = ChatappImageUtils.decodeBitmapFromFile(path, 100, 100);// BitmapFactory.decodeFile(path);
                            holder.image.setImageBitmap(myBitmap);
                        }
                    } else if (horizontalList.get(position).getChatFileLocalPath() != null) {
                        String path = horizontalList.get(position).getChatFileLocalPath();
                        File file = new File(path);
                        if (file.exists()) {
                            Bitmap myBitmap = ChatappImageUtils.decodeBitmapFromFile(path, 100, 100);// BitmapFactory.decodeFile(path);
                            holder.image.setImageBitmap(myBitmap);
                        }
                    }

                }

                if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.video)) {

                    if (horizontalList.get(position).getVideoPath() != null) {
                        String path = horizontalList.get(position).getVideoPath();
                        File file = new File(path);
                        if (file.exists()) {
                            Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
                            holder.image.setImageBitmap(thumbBmp);
                            MediaMetadataRetriever mdr = new MediaMetadataRetriever();
                            mdr.setDataSource(horizontalList.get(position).getChatFileLocalPath());
                            String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            String setduration = getTimeString(AppUtils.parseLong(duration));
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
                            String setduration = getTimeString(AppUtils.parseLong(duration));
                            holder.Vido.setVisibility(View.VISIBLE);
                            holder.duration.setVisibility(View.VISIBLE);
                            holder.duration.setText(setduration);
                        }

                    }


                }


            } else */
                if (position < 5) {
                    if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.picture)) {
                        if (horizontalList.get(position).getImagePath() != null) {
                            String path = horizontalList.get(position).getImagePath();
                            File file = new File(path);
                            if (file.exists()) {
                                Bitmap myBitmap = BitmapFactory.decodeFile(path);
                                Bitmap bitmap = Bitmap.createScaledBitmap(myBitmap, 128, 128, true);
                                holder.image.setImageBitmap(bitmap);
                            }
                        } else if (horizontalList.get(position).getChatFileLocalPath() != null) {
                            String path = horizontalList.get(position).getChatFileLocalPath();
                            File file = new File(path);
                            if (file.exists()) {
                                Bitmap myBitmap = BitmapFactory.decodeFile(path);
                                Bitmap bitmap = Bitmap.createScaledBitmap(myBitmap, 128, 128, true);
                                holder.image.setImageBitmap(bitmap);
                                holder.image.setImageBitmap(myBitmap);
                            }
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
//                    if (horizontalList.get(position).getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                        holder.Vido.setVisibility(View.GONE);
                        holder.duration.setVisibility(View.GONE);
                        holder.image.setBackgroundResource(R.drawable.document_icon);
                        String path = horizontalList.get(position).getChatFileLocalPath();
                        File file = new File(path);
                    } else if (Integer.parseInt(horizontalList.get(position).getMessageType()) == (MessageFactory.video)) {

                        if (horizontalList.get(position).getVideoPath() != null) {
                            String path = horizontalList.get(position).getVideoPath();
                            File file = new File(path);
                            if (file.exists()) {
                                Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
                                Bitmap bitmap = Bitmap.createScaledBitmap(thumbBmp, 128, 128, true);
                                holder.image.setImageBitmap(bitmap);
                                MediaMetadataRetriever mdr = new MediaMetadataRetriever();
                                mdr.setDataSource(horizontalList.get(position).getChatFileLocalPath());
                                String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                String setduration = getTimeString(AppUtils.parseLong(duration));
                                holder.Vido.setVisibility(View.VISIBLE);
                                holder.duration.setVisibility(View.VISIBLE);
                                holder.duration.setText(setduration);
                            }

                        } else if (horizontalList.get(position).getChatFileLocalPath() != null) {
                            try {
                                String path = horizontalList.get(position).getChatFileLocalPath();
                                File file = new File(path);
                                if (file.exists()) {
                                    Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
                                    Bitmap bitmap = Bitmap.createScaledBitmap(thumbBmp, 128, 128, true);
                                    holder.image.setImageBitmap(bitmap);
                                    MediaMetadataRetriever mdr = new MediaMetadataRetriever();
                                    mdr.setDataSource(horizontalList.get(position).getChatFileLocalPath());
                                    String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                    String setduration = getTimeString(AppUtils.parseLong(duration));
                                    holder.Vido.setVisibility(View.VISIBLE);
                                    holder.duration.setVisibility(View.VISIBLE);
                                    holder.duration.setText(setduration);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }


                        }
                    }
                /*if (horizontalList.size() - 1 == position) {
                    holder.arrow.setVisibility(View.VISIBLE);
                } else {
                    holder.arrow.setVisibility(View.GONE);
                }*/
                } else {
                    holder.arrow.setVisibility(View.VISIBLE);
                    holder.image.setVisibility(View.GONE);

                    holder.arrow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(GroupInfo.this, MediaAcitivity.class);
                            intent.putExtra("username", mGroupName);
                            intent.putExtra("docid", mLocDbDocId);
                            startActivity(intent);
                            // Toast.makeText(UserInfo.this,holder.txtView.getText().toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            /*holder.arrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GroupInfo.this, MediaAcitivity.class);
                    intent.putExtra("username", mGroupName);
                    intent.putExtra("docid", mLocDbDocId);
                    startActivity(intent);
                    // Toast.makeText(UserInfo.this,holder.txtView.getText().toString(),Toast.LENGTH_SHORT).show();
                }
            });
            */
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        @Override
        public int getItemCount() {

            if (horizontalList.size() <= 5) {
                return horizontalList.size();
            }

            return 6;
        }

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

}
