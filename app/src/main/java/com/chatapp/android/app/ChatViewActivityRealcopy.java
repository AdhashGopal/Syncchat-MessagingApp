//package com.chatapp.android.app;
//
//import android.animation.Animator;
//import android.animation.AnimatorListenerAdapter;
//import android.annotation.TargetApi;
//import android.app.Activity;
//import android.app.NotificationManager;
//import android.app.ProgressDialog;
//import android.content.ActivityNotFoundException;
//import android.content.ClipData;
//import android.content.ClipboardManager;
//import android.content.ContentProviderOperation;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.content.SharedPreferences;
//import android.content.pm.PackageManager;
//import android.content.pm.ResolveInfo;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
//import android.media.MediaMetadataRetriever;
//import android.media.MediaRecorder;
//import android.media.Ringtone;
//import android.media.RingtoneManager;
//import android.media.ThumbnailUtils;
//import android.net.Uri;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.SystemClock;
//import android.preference.PreferenceManager;
//import android.provider.ContactsContract;
//import android.provider.DocumentsContract;
//import android.provider.MediaStore;
//import android.support.v4.app.ActivityCompat;
//import android.support.v7.app.AlertDialog;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.PopupMenu;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.Toolbar;
//import android.text.Editable;
//import android.text.Html;
//import android.text.TextWatcher;
//import android.util.Base64;
//import android.util.Log;
//import android.util.Patterns;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.animation.AccelerateDecelerateInterpolator;
//import android.view.animation.Animation;
//import android.view.animation.AnimationUtils;
//import android.view.inputmethod.EditorInfo;
//import android.view.inputmethod.InputMethodManager;
//import android.webkit.MimeTypeMap;
//import android.widget.ArrayAdapter;
//import android.widget.Chronometer;
//import android.widget.FrameLayout;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.andexert.library.RippleView;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.ImageLoader;
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
//import com.chatapp.android.R;
//import com.chatapp.android.app.adapter.At_InfoAdapter;
//import com.chatapp.android.app.adapter.ChatMessageAdapterNew;
//import com.chatapp.android.app.adapter.RItemAdapter;
//import com.chatapp.android.app.calls.CallMessage;
//import com.chatapp.android.app.calls.CallsActivity;
//import com.chatapp.android.app.dialog.ChatLockPwdDialog;
//import com.chatapp.android.app.dialog.CustomAlertDialog;
//import com.chatapp.android.app.dialog.CustomDeleteDialog;
//import com.chatapp.android.app.dialog.CustomMultiTextItemsDialog;
//import com.chatapp.android.app.dialog.MuteAlertDialog;
//import com.chatapp.android.app.utils.AppUtils;
//import com.chatapp.android.app.utils.BlockUserUtils;
//import com.chatapp.android.app.utils.ConnectivityInfo;
//import org.appspot.apprtc.util.CryptLib;
//import com.chatapp.android.app.utils.EmailChatHistoryUtils;
//import com.chatapp.android.app.utils.Getcontactname;
//import com.chatapp.android.app.utils.GroupInfoSession;
//import com.chatapp.android.app.utils.MuteUnmute;
//import com.chatapp.android.app.utils.UserInfoSession;
//import com.chatapp.android.app.widget.AvnNextLTProRegEditText;
//import com.chatapp.android.app.widget.CircleImageView;
//import com.chatapp.android.app.widget.CustomRecyclerView;
//import com.chatapp.android.core.CoreActivity;
//import com.chatapp.android.core.CoreController;
//import com.chatapp.android.core.Session;
//import com.chatapp.android.core.SessionManager;
//import com.chatapp.android.core.ShortcutBadgeManager;
//import com.chatapp.android.core.chatapphelperclass.ChatappImageUtils;
//import com.chatapp.android.core.chatapphelperclass.ChatappRegularExp;
//import com.chatapp.android.core.chatapphelperclass.ChatappUtilities;
//import com.chatapp.android.core.database.ContactDB_Sqlite;
//import com.chatapp.android.core.database.MessageDbController;
//import com.chatapp.android.core.message.AudioMessage;
//import com.chatapp.android.core.message.BaseMessage;
//import com.chatapp.android.core.message.ContactMessage;
//import com.chatapp.android.core.message.DocumentMessage;
//import com.chatapp.android.core.message.GroupEventInfoMessage;
//import com.chatapp.android.core.message.IncomingMessage;
//import com.chatapp.android.core.message.LocationMessage;
//import com.chatapp.android.core.message.MessageAck;
//import com.chatapp.android.core.message.MessageFactory;
//import com.chatapp.android.core.message.PictureMessage;
//import com.chatapp.android.core.message.TextMessage;
//import com.chatapp.android.core.message.VideoMessage;
//import com.chatapp.android.core.message.WebLinkMessage;
//import com.chatapp.android.core.model.CallItemChat;
//import com.chatapp.android.core.model.ChatLockPojo;
//import com.chatapp.android.core.model.ChatappContactModel;
//import com.chatapp.android.core.model.ContactToSend;
//import com.chatapp.android.core.model.GroupInfoPojo;
//import com.chatapp.android.core.model.GroupMembersPojo;
//import com.chatapp.android.core.model.Imagepath_caption;
//import com.chatapp.android.core.model.MessageItemChat;
//import com.chatapp.android.core.model.MultiTextDialogPojo;
//import com.chatapp.android.core.model.MuteStatusPojo;
//import com.chatapp.android.core.model.MuteUserPojo;
//import com.chatapp.android.core.model.ReceviceMessageEvent;
//import com.chatapp.android.core.model.SendMessageEvent;
//import com.chatapp.android.core.service.Constants;
//import com.chatapp.android.core.socket.MessageService;
//import com.chatapp.android.core.socket.SocketManager;
//import com.chatapp.android.core.uploadtoserver.FileUploadDownloadManager;
//import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
//import com.google.android.gms.common.GooglePlayServicesRepairableException;
//import com.google.android.gms.location.places.Place;
//import com.google.android.gms.location.places.ui.PlacePicker;
//import com.google.android.gms.maps.model.LatLng;
//
//import org.appspot.apprtc.CallActivity;
//import org.greenrobot.eventbus.EventBus;
//import org.greenrobot.eventbus.Subscribe;
//import org.greenrobot.eventbus.ThreadMode;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.select.Elements;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.UnsupportedEncodingException;
//import java.net.URL;
//import java.security.NoSuchAlgorithmException;
//import java.text.DateFormat;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Calendar;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import javax.crypto.NoSuchPaddingException;
//
//import droidninja.filepicker.FilePickerBuilder;
//import droidninja.filepicker.FilePickerConst;
//import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
//import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
//import id.zelory.compressor.Compressor;
//import io.codetail.animation.SupportAnimator;
//import io.codetail.animation.ViewAnimationUtils;
//import io.socket.client.Socket;
//import me.leolin.shortcutbadger.ShortcutBadgeException;
//import me.leolin.shortcutbadger.ShortcutBadger;
//
//import static android.Manifest.permission.RECORD_AUDIO;
//import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
//
///**
// */
//public class ChatViewActivityRealcopy extends CoreActivity implements View.OnClickListener, View.OnLongClickListener, MuteAlertDialog.MuteAlertCloseListener, RippleView.OnRippleCompleteListener {
//    private static final int RESULT_LOAD_IMAGE = 1;
//    private static final int RESULT_LOAD_VIDEO = 2;
//    private static final int RESULT_CAPTURE_VIDEO = 3;
//    private static final int REQUEST_CODE_CONTACTS = 4;
//    private static final int RESULT_SHARE_LOCATION = 5;
//    private static final int CHECKING_LOCATION = 6;
//    private static final int REQUEST_SELECT_AUDIO = 7;
//    private static final int RESULT_WALLPAPER = 8;
//    private static final int REQUEST_CODE_DOCUMENT = 9;
//    private static final int PICK_CONTACT_REQUEST = 10;
//    private final int EXIT_GROUP_REQUEST_CODE = 11;
//    private final int AUDIO_RECORD_PERMISSION_REQUEST_CODE = 14;
//    private final int MUTE_ACTIVITY = 18;
//    private final int ADD_CONTACT = 21;
//    int lastvisibleitempostion = 0;
//    private final int REQUEST_CODE_FORWARD_MSG = 15;
//    private ImageView background;
//    boolean isAlrdychatlocked = false;
//    public static int progressglobal = 0;
//    private static final int CAMERA_REQUEST = 1888;
//    private String mReceiverId;
//    ArrayList<ContactToSend> contacts;
//    Getcontactname getcontactname;
//    public static final String INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED = "finishActivityOnSaveCompleted";
//    Bitmap myTemp = null;
//    String convId, docId = "", mFirstVisibleMsgId;
//    private boolean isMessageLoadedOnScroll = true;
//    private boolean isMessageLoading = false;
//    int unreadmsgcount = 0;
//    String emailChatlock, recemailChatlock, recPhoneChatlock;
//    private ImageView sendButton, capture_timer, selEmoji, selKeybord, ivVoiceCall, ivVideoCall;
//    private CustomRecyclerView recyclerView_chat;
//    private EmojiconEditText sendMessage;
//    private CircleImageView ivProfilePic;
//    private TextView groupUsername, tvWebLink, tvWebLinkDesc, tvWebTitle, message_old;
//    private TextView receiverNameText;
//    private TextView statusTextView, tvTyping;
//    private ImageView history, ivWebLink, ivWebLinkClose;
//    private LinearLayout atlas_actionbar_call, llAddBlockContact;
//    private RelativeLayout backButton, hearderdate, rlWebLink;//,root;
//    private TextView dateView, Ifname, messagesetmedio, tvBlock, tvAddToContact;
//    private RelativeLayout nameMAincontainer;
//    //    private LinearLayout bottomlayoutinner;
//    private TextView groupleftmess;
//    String ContactString;
//    private Toolbar include;
//    RelativeLayout layout_new;
//    EmojIconActions emojIcon;
//    private Animation loaderAnimation;
//    private RelativeLayout rlChatActions;
//    int avoid_twotimescall = 0;
//    TextView unreadcount;
//    private static final String TAG = ChatViewActivityRealcopy.class.getSimpleName() + ">>>@@@@@@";
//    ImageView image_to;
//    ShortcutBadgeManager sharedprf_video_uploadprogress;
//    long fromLastTypedAt = 0, toLastTypedAt = 0, lastViewStatusSentAt = 0;
//    Handler toTypingHandler = new Handler();
//
//    private UserInfoSession userInfoSession;
//    ImageView cameraphoto, videoimage, audioimage, personimage;
//    String mesgid;
//    //    private CoordinatorLayout root;
//    private ImageView iBtnBack, overflow, backnavigate;
//    ImageView cameraimage, sentimage;
//    RelativeLayout cameralayout;
//    RelativeLayout overflowlayout, Relative_body;
//    private ImageButton iBtnScroll;
//    View rootView;
//    String[] array;
//    String messageold = "", mDataId, mRawContactId;
//    RelativeLayout r1messagetoreplay, RelativeLayout_group_delete, text_lay_out, rlSend;
//    ImageView emai1send, gmailsend, record;
//    private ImageView delete, attachment, copy, forward, starred, info, replymess, close, Documentimage;
//    public boolean isFirstItemSelected = false, hasGroupInfo;
//    String chatType, mypath, audioRecordPath;
//    AvnNextLTProRegEditText Search1;
//    TextView add_contact, block_contact, report_spam;
//    String imgDecodableString;
//    RelativeLayout imageLayout;
//    RelativeLayout emailgmail;
//    String value, ReplySender = "";
//    String date;
//    Bitmap bitmap;
//    Drawable d;
//    private String mReceiverName = "";
//    public Boolean reply = false, isSelectedWithUnStarMsg;
//
//    private String mymsgid = "", locationName;
//    final Context context = this;
//    private String msgid;
//    private String receiverName;
//    private String from;
//    public static String to;
//    public static String Chat_to;
//    private ArrayList<MessageItemChat> mChatData;
//    String isExpiry = "0";
//    String expireTime = "0";
//
//    private ArrayList<String> onTimeriD = new ArrayList<>();
//    boolean ackMsgid=false;
//
//
//    private ChatMessageAdapterNew mAdapter;
//    public static boolean isChatPage, isKilled;
//
//    private ImageView longpressback;
//    String name, number;
//    boolean backfrom, canShowLastSeen, isLastSeenCalled;
//    String replytype;
//    public static boolean isGroupChat;
//    private boolean enableGroupChat, gmailintent = false, isMenuBtnClick;
//    String receiverUid;
//    private String mGroupId, mCurrentUserId, receiverAvatar;
//    public static String Activity_GroupId = "";
//    private FileUploadDownloadManager uploadDownloadManager;
//    Session session;
//    private SessionManager sessionManager;
//    private String imgpath;
//    int pastVisiblesItems, visibleItemCount, totalItemCount;
//    private LinearLayoutManager mLayoutManager;
//    private String receiverMsisdn;
//    Chronometer myChronometer;
//
//    public static final String HTML_FRONT_TAG = "<font color=\"#01a9e5\">";
//    public static final String HTML_END_TAG = "</font>  ";
//    public static List<GroupMembersPojo> at_memberlist = new ArrayList<GroupMembersPojo>();
//    public static GroupMembersPojo groupMembersPojo;
//    private RecyclerView rvGroupMembers;
//    private At_InfoAdapter adapter;
//    private List<GroupMembersPojo> savedMembersList, unsavedMembersList;
//    public static List<GroupMembersPojo> allMembersList;
//    GroupMembersPojo mCurrentUserData;
//    private int membersCount;
//    ArrayList<MessageItemChat> selectedChatItems = new ArrayList<>();
//    TextView slidetocencel;
//    //    ArrayList<String> selected_data = new ArrayList<String>();
//    private Boolean Mutemenu;
//    private ArrayList<String> myEmailChatInfo = new ArrayList<String>();
//    private GroupInfoSession groupInfoSession;
//    private boolean hasLinkInfo;
//    private String webLink, webLinkTitle, webLinkDesc, webLinkImgUrl;
//    private String imageAsBytes;
//    private String contactname, mConvId = "";
//    private Menu chatMenu;
//    private boolean deletestarred = false, audioRecordStarted = false;
//    private MediaRecorder audioRecorder;
//    //    private LoadingDots dotTexting;
//    private ImageView attachment_icon;
//    private LinearLayout mRevealView;
//    private boolean hidden = true;
//
//    private LinearLayout image_choose;
//    private LinearLayout document_choose;
//    private LinearLayout video_choose;
//    private LinearLayout audio_choose;
//    private LinearLayout location_choose;
//    private LinearLayout contact_choose, camera_choose;
//
//    private RippleView Ripple_call;
//    private RippleView Ripple_Video;
//    private int callType_global;
//    FrameLayout frame_lyt_nolonger;
//    RelativeLayout rt_nolonger;
//    //----------Delete Chat--------------
//
//    public static Activity Chat_Activity;
//    private String Delete_Type = "";
//    private MessageDbController db;
//    public FrameLayout frameL;
//    boolean isBroadcast = false;
//
//    List<ChatappContactModel> chatappContactModels;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.d(TAG, "onCreate: ");
//        setContentView(R.layout.chat_message_screen_new);
//        Chat_Activity = ChatViewActivityRealcopy.this;
//
//        if (getIntent().hasExtra("broadcast")) {
//            isBroadcast = true;
//
//            chatType = MessageFactory.CHAT_TYPE_GROUP;
//            chatappContactModels = (List<ChatappContactModel>) getIntent().getSerializableExtra("contactModel");
//
//            //    overflow.setVisibility(View.GONE);
//
//        }
//        EventBus.getDefault().register(this);
//
////        getSupportActionBar().hide();
//        session = new Session(ChatViewActivityRealcopy.this);
//        sessionManager = SessionManager.getInstance(ChatViewActivityRealcopy.this);
//        sharedprf_video_uploadprogress = new ShortcutBadgeManager(ChatViewActivityRealcopy.this);
//        userInfoSession = new UserInfoSession(ChatViewActivityRealcopy.this);
//        getcontactname = new Getcontactname(ChatViewActivityRealcopy.this);
//        db = CoreController.getDBInstance(ChatViewActivityRealcopy.this);
//        sessionManager.setwelcomeemabled(true);
//
//        initView();
//
//        attachment_icon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                hideKeyboard();
//                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getApplicationContext());
//                if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
//                    DisplayAlert("Unblock" + " " + mReceiverName + " " + "to send message?");
//                } else {
//                    int cx = mRevealView.getRight();
//                    int cy = mRevealView.getBottom();
//                    makeEffect(mRevealView, cx, cy);
//                }
//
//            }
//
//
//        });
//
//        sendMessage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (iBtnScroll != null && iBtnScroll.getVisibility() == View.VISIBLE) {
//                    iBtnScroll.post(new Runnable() {
//                        @Override
//                        public void run() {
//                            iBtnScroll.performClick();
//                        }
//                    });
//                }
//                hideRevealView();
//            }
//        });
//
//        sendMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean b) {
//                hideRevealView();
//            }
//        });
//
//
//        allMembersList = new ArrayList<>();
//        savedMembersList = new ArrayList<>();
//        unsavedMembersList = new ArrayList<>();
//        adapter = new At_InfoAdapter(ChatViewActivityRealcopy.this, allMembersList);
//        rvGroupMembers.setAdapter(adapter);
//
//        if (isGroupChat) {
//            getGroupDetails();
//            capture_timer.setVisibility(View.GONE);
//        }
//
//        adapter.setChatListItemClickListener(new At_InfoAdapter.AtInfoAdapterItemClickListener() {
//            @Override
//            public void onItemClick(GroupMembersPojo member, int position) {
//                at_memberlist.add(member);
//                sendMessage.append(Html.fromHtml("" + HTML_FRONT_TAG + member.getContactName() + HTML_END_TAG));
//                rvGroupMembers.setVisibility(View.GONE);
//                //AppUtils.slideDown(rvGroupMembers);
//            }
//        });
//
//
////        if(isBroadcast)
////        {
////            ivVideoCall.setVisibility(View.GONE);
////            ivVoiceCall.setVisibility(View.GONE);
////            Ripple_call.setVisibility(View.GONE);
////            Ripple_Video.setVisibility(View.GONE);
////        }
//
//
//    }
//
//    private void getGroupDetails() {
//        SendMessageEvent event = new SendMessageEvent();
//        event.setEventName(SocketManager.EVENT_GROUP_DETAILS);
//
//        try {
//            JSONObject object = new JSONObject();
//            object.put("from", mCurrentUserId);
//            object.put("convId", mGroupId);
//            event.setMessageObject(object);
//            EventBus.getDefault().post(event);
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void makeEffect(final LinearLayout layout, int cx, int cy) {
//        try {
//            int radius = Math.max(layout.getWidth(), layout.getHeight());
//
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//
//                SupportAnimator animator =
//                        ViewAnimationUtils.createCircularReveal(layout, cx, cy, 0, radius);
//                animator.setInterpolator(new AccelerateDecelerateInterpolator());
//                animator.setDuration(800);
//
//                SupportAnimator animator_reverse = animator.reverse();
//
//                if (hidden) {
//                    layout.setVisibility(View.VISIBLE);
//                    animator.start();
//                    hidden = false;
//                } else {
//                    animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
//                        @Override
//                        public void onAnimationStart() {
//
//                        }
//
//                        @Override
//                        public void onAnimationEnd() {
//                            layout.setVisibility(View.INVISIBLE);
//                            hidden = true;
//
//                        }
//
//                        @Override
//                        public void onAnimationCancel() {
//
//                        }
//
//                        @Override
//                        public void onAnimationRepeat() {
//
//                        }
//                    });
//                    animator_reverse.start();
//
//                }
//            } else {
//                if (hidden) {
//                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(layout, cx, cy, 0, radius);
//                    layout.setVisibility(View.VISIBLE);
//                    anim.start();
//                    hidden = false;
//
//                } else {
//                    Animator anim = android.view.ViewAnimationUtils.createCircularReveal(layout, cx, cy, radius, 0);
//                    anim.addListener(new AnimatorListenerAdapter() {
//                        @Override
//                        public void onAnimationEnd(Animator animation) {
//                            super.onAnimationEnd(animation);
//                            layout.setVisibility(View.INVISIBLE);
//                            hidden = true;
//                        }
//                    });
//                    anim.start();
//
//                }
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "makeEffect: ", e);
//        }
//    }
//
//
//    private void initDataBase() {
//        loadFromDB();
//
//        if (msgid != null && !msgid.equals("")) {
//            Starredmsg();
//        }
//        layout_new.setVisibility(View.GONE);
//        if (!isGroupChat) {
//            if (!getcontactname.isContactExists(mReceiverId) && (!sessionManager.isFirstMessage(to + "firstmsg"))) {
//                //     layout_new.setVisibility(View.VISIBLE);
//            } else if (!getcontactname.isContactExists(mReceiverId)) {
//                //   llAddBlockContact.setVisibility(View.VISIBLE);
//                setTopLayoutBlockText();
//            }
//
//            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//            String blockStatus = contactDB_sqlite.getBlockedStatus(to, false);
//            if (blockStatus.equals("1")) {
//                block_contact.setText("Unblock");
//            } else {
//                block_contact.setText("Block");
//            }
//        }
//
//        getgroupmemberdetails();
//        profilepicupdation();
//    }
//
//    private void setTopLayoutBlockText() {
//        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//        if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
//            tvBlock.setText("Unblock");
//        } else {
//            tvBlock.setText("Block");
//        }
//    }
//
//
//    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
//    private void initView() {
//        rvGroupMembers = (RecyclerView) findViewById(R.id.rvGroupMembers);
//        frame_lyt_nolonger = (FrameLayout) findViewById(R.id.fr_nolonger_participant_message);
//        rt_nolonger = (RelativeLayout) findViewById(R.id.relativeLayout);
//        frameL = (FrameLayout) findViewById(R.id.frame);
//        groupleftmess = (TextView) findViewById(R.id.groupleftmess);
//        sendButton = (ImageView) findViewById(R.id.enter_chat1);
//        record = (ImageView) findViewById(R.id.record);
//        myChronometer = (Chronometer) findViewById(R.id.chronometer);
//        include = (Toolbar) findViewById(R.id.chatheaderinclude);
//        setSupportActionBar(include);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        rvGroupMembers.setLayoutManager(llm);
//        rlChatActions = (RelativeLayout) findViewById(R.id.rlChatActions);
//        rlChatActions.setVisibility(View.GONE);
//
//        mRevealView = (LinearLayout) findViewById(R.id.reveal_items);
//        mRevealView.setVisibility(View.GONE);
//
//
//        Ripple_call = (RippleView) findViewById(R.id.Ripple_call);
//        Ripple_Video = (RippleView) findViewById(R.id.Ripple_Video);
//        RelativeLayout rlChatSeen = (RelativeLayout) findViewById(R.id.rlChatSeen);
//        background = (ImageView) findViewById(R.id.background);
//        add_contact = (TextView) findViewById(R.id.add_contact);
//        block_contact = (TextView) findViewById(R.id.block_contact);
//        report_spam = (TextView) findViewById(R.id.report_spam);
//        personimage = (ImageView) findViewById(R.id.personimage);
//        Documentimage = (ImageView) findViewById(R.id.Documentimage);
//        ivProfilePic = (CircleImageView) include.findViewById(R.id.profileImageChatScreen);
//        nameMAincontainer = (RelativeLayout) include.findViewById(R.id.nameMAincontainer);
//        statusTextView = (TextView) include.findViewById(R.id.statuschatsceen);
//        tvTyping = (TextView) include.findViewById(R.id.tvTyping);
//        unreadcount = (TextView) findViewById(R.id.unreadcount);
//        messagesetmedio = (TextView) findViewById(R.id.messagesetmedio);
//        rlWebLink = (RelativeLayout) findViewById(R.id.rlWebLink);
//        tvWebTitle = (TextView) findViewById(R.id.tvWebTitle);
//        tvWebLink = (TextView) findViewById(R.id.tvWebLink);
//        tvWebLinkDesc = (TextView) findViewById(R.id.tvWebLinkDesc);
//        ivWebLink = (ImageView) findViewById(R.id.ivWebLink);
//        ivWebLinkClose = (ImageView) findViewById(R.id.ivWebLinkClose);
//        layout_new = (RelativeLayout) findViewById(R.id.layout_new);
//        ivWebLinkClose.setOnClickListener(this);
//        image_to = (ImageView) findViewById(R.id.image_to);
//        slidetocencel = (TextView) findViewById(R.id.slidetocencel);
//        nameMAincontainer.setOnClickListener(this);
//        replymess = (ImageView) rlChatActions.findViewById(R.id.replymess);
//        close = (ImageView) findViewById(R.id.close);
//        r1messagetoreplay = (RelativeLayout) findViewById(R.id.r1messagetoreplay);
//        text_lay_out = (RelativeLayout) findViewById(R.id.text_lay_out);
//        rlSend = (RelativeLayout) findViewById(R.id.rlSend);
//        Ifname = (TextView) findViewById(R.id.Ifname);
//        message_old = (TextView) findViewById(R.id.message);
//        message_old.setTextColor(getResources().getColor(R.color.title));
//        dateView = (TextView) findViewById(R.id.dateView);
//        dateView.setText("Today");
//        receiverNameText = (TextView) include.findViewById(R.id.usernamechatsceen);
//        backButton = (RelativeLayout) include.findViewById(R.id.backButton);
//        RelativeLayout_group_delete = (RelativeLayout) include.findViewById(R.id.RelativeLayout_group_delete);
//        delete = (ImageView) rlChatActions.findViewById(R.id.delete);
//        attachment = (ImageView) include.findViewById(R.id.attachment);
//        attachment_icon = (ImageView) findViewById(R.id.attachment_icon);
//        selKeybord = (ImageView) findViewById(R.id.keybordButton);
//        hearderdate = (RelativeLayout) findViewById(R.id.hearderdate);
//        sendMessage = (EmojiconEditText) findViewById(R.id.chat_edit_text1);
//        recyclerView_chat = findViewById(R.id.list_view_messages);
//        iBtnBack = (ImageView) findViewById(R.id.iBtnBack);
//        iBtnScroll = (ImageButton) findViewById(R.id.iBtnScroll);
//        backnavigate = (ImageView) rlChatActions.findViewById(R.id.iBtnBack2);
//        imageLayout = (RelativeLayout) findViewById(R.id.Relative_recycler);
//        Search1 = (AvnNextLTProRegEditText) rlChatActions.findViewById(R.id.etSearch);
//        overflow = (ImageView) include.findViewById(R.id.overflow);
//        //overflowlayout = (RelativeLayout) include.findViewById(R.id.overflowLayout);
//        image_choose = (LinearLayout) findViewById(R.id.image_choose);
//        document_choose = (LinearLayout) findViewById(R.id.document_choose);
//        video_choose = (LinearLayout) findViewById(R.id.video_choose);
//        audio_choose = (LinearLayout) findViewById(R.id.audio_choose);
//        location_choose = (LinearLayout) findViewById(R.id.location_choose);
//        contact_choose = (LinearLayout) findViewById(R.id.contact_choose);
//
//        camera_choose = (LinearLayout) findViewById(R.id.camera_choose);
//
//
//        Ripple_call.setOnRippleCompleteListener(this);
//        Ripple_call.setOnClickListener(this);
//        Ripple_Video.setOnRippleCompleteListener(this);
//        Ripple_Video.setOnClickListener(this);
//
//        image_choose.setOnClickListener(this);
//        document_choose.setOnClickListener(this);
//        video_choose.setOnClickListener(this);
//        audio_choose.setOnClickListener(this);
//        location_choose.setOnClickListener(this);
//        contact_choose.setOnClickListener(this);
//
//        camera_choose.setOnClickListener(this);
//
//        overflow.setOnClickListener(this);
//        iBtnBack.setOnClickListener(this);
//        ivProfilePic.setOnClickListener(this);
//        add_contact.setOnClickListener(this);
//        block_contact.setOnClickListener(this);
//        report_spam.setOnClickListener(this);
//        iBtnScroll.setOnClickListener(this);
//        videoimage = (ImageView) findViewById(R.id.videoimage);
//        cameraphoto = (ImageView) findViewById(R.id.cameraphoto);
//        audioimage = (ImageView) findViewById(R.id.audioimage);
//        sentimage = (ImageView) findViewById(R.id.sentimage);
//        cameraimage = (ImageView) findViewById(R.id.imagecamera);
//        cameralayout = (RelativeLayout) findViewById(R.id.cameralayout);
//        Relative_body = (RelativeLayout) findViewById(R.id.Relative_body);
//        emailgmail = (RelativeLayout) findViewById(R.id.email_gmail);
//        emai1send = (ImageView) findViewById(R.id.emai1send);
//        gmailsend = (ImageView) findViewById(R.id.gmailsend);
//        copy = (ImageView) rlChatActions.findViewById(R.id.copychat);
//        forward = (ImageView) rlChatActions.findViewById(R.id.forward);
//        info = (ImageView) rlChatActions.findViewById(R.id.info);
//        starred = (ImageView) rlChatActions.findViewById(R.id.starred);
//        longpressback = (ImageView) rlChatActions.findViewById(R.id.iBtnBack3);
//
//        capture_timer = (ImageView) findViewById(R.id.capture_timer);
//        capture_timer.setVisibility(View.VISIBLE);
//
////        dotTexting = (LoadingDots) findViewById(R.id.dotTexting);
////        dotTexting.startAnimation();
//
//        selEmoji = (ImageView) findViewById(R.id.emojiButton);
//        rootView = findViewById(R.id.mainRelativeLayout);
//        //  textView = (EmojiconTextView) findViewById(R.id.textView);
//        //popup = new EmojiconsPopup(rootView, this);
//        emojIcon = new EmojIconActions(this, rootView, sendMessage, selEmoji);
//        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smile);
//
//        emojIcon.ShowEmojIcon();
//
//        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
//            @Override
//            public void onKeyboardOpen() {
//                hideRevealView();
//                Log.e(TAG, "Keyboard opened!");
//            }
//
//            @Override
//            public void onKeyboardClose() {
//                Log.e(TAG, "Keyboard Closed!");
//
//            }
//        });
//
//        // textView.setUseSystemDefault(false);
//        if (session.isEnterKeyPressToSend()) {
//            sendMessage.setSingleLine(true);
//            sendMessage.setMaxLines(4);
//            sendMessage.setImeOptions(EditorInfo.IME_ACTION_DONE);
//        }
//
//        sendMessage.setOnKeyListener(enterKeyListener);
//        //   Typeface typeface = CoreController.getInstance().getAvnNextLTProRegularTypeface();
//        //  sendMessage.setTypeface(typeface);
//
//        llAddBlockContact = (LinearLayout) findViewById(R.id.llAddBlockContact);
//
//        tvBlock = (TextView) findViewById(R.id.tvBlock);
//        tvBlock.setOnClickListener(this);
//
//        tvAddToContact = (TextView) findViewById(R.id.tvAddToContact);
//        tvAddToContact.setOnClickListener(this);
//
//        if (!session.getgalleryPrefsName().isEmpty()) {
//            Bitmap bitmap = BitmapFactory
//                    .decodeFile(session.getgalleryPrefsName());
//
//            background.setFitsSystemWindows(false);
//            background.setImageBitmap(bitmap);
//
//            /*d = ChatappImageLoader.decodeSampledBitmapFromDescriptor()
//
//            Picasso.with(ChatViewActivity.this).load(session.getgalleryPrefsName()).into(imageLayout);*/
//        }
//
//        record.setOnTouchListener(audioRecordTouchListener);
//        record.setOnLongClickListener(ChatViewActivityRealcopy.this);
//        //  recyclerView_chat.setHasStableIds(true);
//
//        recyclerView_chat.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                emailgmail.setVisibility(View.GONE);
//                return false;
//            }
//        });
//
//        Search1.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                final int DRAWABLE_LEFT = 0;
//                final int DRAWABLE_TOP = 1;
//                final int DRAWABLE_RIGHT = 2;
//                final int DRAWABLE_BOTTOM = 3;
//                int spaceArea = 70;
//                if (event.getAction() == MotionEvent.ACTION_UP && Search1.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
//                    if (event.getRawX() + spaceArea >= (Search1.getRight() - Search1.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
//
//                        Search1.setText("");
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
//
//        recyclerView_chat.addOnItemTouchListener(new RItemAdapter(this, recyclerView_chat, new RItemAdapter.OnItemClickListener() {
//
//            @Override
//            public void onItemClick(View view, int position) {
//
//                if (position >= 0) {
//                    if (isFirstItemSelected) {
//
//                        boolean isDeletedMsgItem = performSelection(position);
////                        if(isDeletedMsgItem)
////                            return;
//
//                        if (selectedChatItems.size() == 0) {
//                            showUnSelectedActions();
//                        }
//
//                    } else {
//                        String type = mChatData.get(position).getMessageType();
//                        boolean isSelf = mChatData.get(position).isSelf();
//                        int messagetype = Integer.parseInt(type);
//
//                        if (MessageFactory.picture == messagetype) {
//                            MessageItemChat item = mChatData.get(position);
//                            if (item.getUploadDownloadProgress() == 0 && !item.isSelf() && item.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START) {
//                                item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_DOWNLOADING);
//                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, false);
//                            } else if (item.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START
//                                    && item.isSelf()) {
//                                /*Download option for sent documents from web chat*/
//                                item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_DOWNLOADING);
//                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, false);
//                            }
//                        } else if (MessageFactory.audio == messagetype) {
//                            MessageItemChat item = mChatData.get(position);
//                            if (item.getUploadDownloadProgress() == 0 && !item.isSelf() && item.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START) {
//                                item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_DOWNLOADING);
//                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, false);
//                            } else if (item.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START
//                                    && item.isSelf()) {
//                                /*Download option for sent documents from web chat*/
//                                item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_DOWNLOADING);
//                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, false);
//                            }
//                        } else if (MessageFactory.document == messagetype) {
//
//                            MessageItemChat item = mChatData.get(position);
//                            if (item.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START && !item.isSelf()) {
//                                item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_DOWNLOADING);
//                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, false);
//                            } else if (item.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START
//                                    && item.isSelf()) {
//                                /*Download option for sent documents from web chat*/
//                                item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_DOWNLOADING);
//                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, false);
//                            } else {
//                                if(item.getDownloadStatus()==MessageFactory.DOWNLOAD_STATUS_COMPLETED) {
//                                    String extension = MimeTypeMap.getFileExtensionFromUrl(item.getChatFileLocalPath());
//                                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//
//                                    PackageManager packageManager = getPackageManager();
//                                    Intent testIntent = new Intent(Intent.ACTION_VIEW);
//                                    testIntent.setType(mimeType);
//                                    try {
//
//                                        List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
//                                        if (list.size() > 0) {
//                                            File file = new File(item.getChatFileLocalPath());
//                                            Intent intent = new Intent(Intent.ACTION_VIEW);
//                                            intent.setDataAndType(Uri.fromFile(file), mimeType);
//                                            startActivity(intent);
//                                        } else {
//                                            Toast.makeText(ChatViewActivityRealcopy.this, "No app installed to view this document", Toast.LENGTH_LONG).show();
//                                        }
//                                    } catch (ActivityNotFoundException e) {
//                                        Toast.makeText(ChatViewActivityRealcopy.this, "No app installed to view this document", Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                                else
//                                {
//
//                                }
//
//
//                            }
//                        } else if (MessageFactory.video == messagetype) {
//                            MessageItemChat item = mChatData.get(position);
//                            if (item.getUploadDownloadProgress() == 0 && !item.isSelf() &&
//                                    item.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START) {
//                                item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_DOWNLOADING);
//                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, false);
//                            } else if (!item.isSelf() && item.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_COMPLETED) {
//                                try {
//
//                                    String videoPath = item.getChatFileLocalPath();
//                                    File file = new File(item.getChatFileLocalPath());
//                                    if (!file.exists()) {
//                                        try {
//
//                                            String[] filePathSplited = item.getChatFileLocalPath().split(File.separator);
//                                            String fileName = filePathSplited[filePathSplited.length - 1];
//                                            String publicDirPath = Environment.getExternalStoragePublicDirectory(
//                                                    Environment.DIRECTORY_MOVIES).getAbsolutePath();
//
//                                            videoPath = publicDirPath + File.separator + fileName;
//                                        } catch (Exception e) {
//                                            Log.e(TAG, "configureViewHolderImageReceived: ", e);
//                                        }
//                                    }
//                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoPath));
//                                    String path = "file://" + videoPath;
//                                    intent.setDataAndType(Uri.parse(path), "video/*");
//                                    startActivity(intent);
//
//                                } catch (ActivityNotFoundException e) {
//                                    Toast.makeText(ChatViewActivityRealcopy.this, "No app installed to play this video", Toast.LENGTH_LONG).show();
//                                }
//                            } else if (item.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START
//                                    && item.isSelf()) {
//                                /*Download option for sent documents from web chat*/
//                                item.setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_DOWNLOADING);
//                                uploadDownloadManager.startFileDownload(EventBus.getDefault(), item, false);
//                            }
//                        } else if (MessageFactory.location == messagetype) {
//                            MessageItemChat item = mChatData.get(position);
//                            String path = "geo:37.7749,-122.4194";
//                            Uri gmmIntentUri = Uri.parse(item.getWebLink());
//                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                            startActivity(mapIntent);
//                           /* mapIntent.setPackage("com.google.android.apps.maps");
//                            if (mapIntent.resolveActivity(getPackageManager()) != null) {
//                                startActivity(mapIntent);
//                            }*/
//                        }
//                        else if(MessageFactory.text==messagetype)
//                        {
//                            MessageItemChat item = mChatData.get(position);
//                            if(!item.getReplyType().equals(""))
//                            {
//                             String id=item.getReplyId();
//
//                             for(int i=0;i<mChatData.size();i++)
//                             {
//                                 if(mChatData.get(i).getRecordId().equals(id))
//                                 {
//
//                                     recyclerView_chat.scrollToPosition(i);
//                                     mChatData.get(i).setSelected(true);
//                                     mAdapter.notifyItemChanged(i);
//                                 }
//                             }
//
//                               // recyclerView_chat.scrollToPosition(item.getReplyId());
//                            }
//                        }
//
//
//                    }
//                    mAdapter.notifyDataSetChanged();
//                }
//
//            }
//
//            @Override
//            public void onItemLongClick(final View view, final int position) {
//
//                if (position >= 0) {
//
//                    boolean isDeletedMsgItem = performSelection(position);
////                    if(isDeletedMsgItem)
////                        return;
//
//                    if (selectedChatItems.size() <= 0) {
//                        showUnSelectedActions();
//                    } else {
//                        isFirstItemSelected = true;
//                        backnavigate.setVisibility(View.GONE);
//                        Search1.setVisibility(View.GONE);
//                    }
//
////                mAdapter.setSelected(position);
//
////                    info.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                            Search1.setText("");
////                            MessageItemChat messageItemChat = mChatData.get(position);
////
////                            if (!isGroupChat) {
////                                Intent intent = new Intent(context, SingleMessageInfoActivity.class);
////                                intent.putExtra("selectedData", messageItemChat);
////                                startActivity(intent);
////
////                            } else {
////
////                                Intent groupinfointent = new Intent(context, GroupMessageInfoActivity.class);
////                                groupinfointent.putExtra("selectedData", messageItemChat);
////                                startActivity(groupinfointent);
////                            }
////
////                            mChatData.get(0).setSelected(false);
////                            mAdapter.notifyDataSetChanged();
////                            showUnSelectedActions();
////                        }
////                    });
//
//
//                    copy.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            String finaltext = "";
//
//                            if (selectedChatItems.size() == 1) {
//                                finaltext = selectedChatItems.get(0).getTextMessage();
//                            } else {
//
//                                for (int i = 0; i < selectedChatItems.size(); i++) {
//                                    String mesgid = selectedChatItems.get(i).getMessageId();
//                                    final String[] array = mesgid.split("-");
//                                    if (!isGroupChat) {
//                                        date = array[2];
//                                    } else {
//                                        if (array.length > 3) {
//                                            date = array[3];
//                                        }
//                                    }
//
//                                    long l = Long.parseLong(date);
//
//                                    Date d = new Date(l);
//                                    DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//                                    String myDate = sdf.format(d);
//                                    Date dateObj = new Date(l);
//                                    String timeStamp = new SimpleDateFormat("h:mm a", Locale.ENGLISH).format(dateObj);
//                                    timeStamp = timeStamp.replace(".", "");
//                                    String sendername = selectedChatItems.get(i).getSenderName();
//                                    String textmessage = String.valueOf("\n" + "[" + myDate + "," + timeStamp + "]" + sendername + ":" + selectedChatItems.get(i).getTextMessage());
//                                    finaltext = finaltext + textmessage;
//                                }
//                            }
//                            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
//                            ClipData clip = ClipData.newPlainText("text", finaltext);
//                            cm.setPrimaryClip(clip);
//
//                            Toast.makeText(ChatViewActivityRealcopy.this, "Message copied", Toast.LENGTH_SHORT).show();
//
//                            showUnSelectedActions();
//                        }
//
//                    });
//                    forward.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Search1.setText("");
//                            Bundle bundle = new Bundle();
//                            bundle.putSerializable("MsgItemList", selectedChatItems);
//                            bundle.putBoolean("FromChatapp", true);
//
//                            Intent intent = new Intent(context, ForwardContact.class);
//                            intent.putExtras(bundle);
//                            startActivityForResult(intent, REQUEST_CODE_FORWARD_MSG);
//                        }
//                    });
//
//                    delete.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Search1.setText("");
//
//                            if (Delete_Type.equalsIgnoreCase("delete received message")) {
//                                final CustomAlertDialog dialog = new CustomAlertDialog();
//                                dialog.setMessage("Are you sure you want to delete this message?");
//                                dialog.setNegativeButtonText("No");
//                                dialog.setPositiveButtonText("Yes");
//                                dialog.setCancelable(false);
//                                dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//                                    @Override
//                                    public void onPositiveButtonClick() {
//
//
//                                        for (int i = 0; i < selectedChatItems.size(); i++) {
//
//                                            MessageItemChat msgItem = selectedChatItems.get(i);
//
//                                            if (msgItem.isMediaPlaying()) {
//                                                int chatIndex = mChatData.indexOf(msgItem);
//                                                mAdapter.stopAudioOnMessageDelete(chatIndex);
//                                            }
//
//                                            String lastMsgStatus;
//                                            if (mChatData.size() == 1) {
//                                                lastMsgStatus = "1";
//                                            } else {
//                                                lastMsgStatus = "0";
//                                            }
//
//                                            String docId = from + "-" + to;
//                                            if (isGroupChat) {
//                                                docId = docId + "-g";
//                                                mConvId = mGroupId;
//                                            } else {
//                                                if (mConvId == null) {
//                                                    mConvId = msgItem.getConvId();
//                                                }
//                                            }
//
//                                            SendMessageEvent messageEvent = new SendMessageEvent();
//                                            messageEvent.setEventName(SocketManager.EVENT_REMOVE_MESSAGE);
//                                            try {
//                                                JSONObject deleteMsgObj = new JSONObject();
//                                                deleteMsgObj.put("from", from);
//                                                deleteMsgObj.put("type", chatType);
//                                                deleteMsgObj.put("convId", mConvId);
//                                                deleteMsgObj.put("status", "1");
//                                                deleteMsgObj.put("recordId", msgItem.getRecordId());
//                                                deleteMsgObj.put("last_msg", lastMsgStatus);
//                                                messageEvent.setMessageObject(deleteMsgObj);
//                                                EventBus.getDefault().post(messageEvent);
//
//                                                db.deleteChatMessage(docId, msgItem.getMessageId(), chatType);
//                                                db.updateTempDeletedMessage(msgItem.getRecordId(), deleteMsgObj);
//
//                                                int index = mChatData.indexOf(selectedChatItems.get(i));
//                                                if (index > -1) {
//                                                    mChatData.remove(index);
//                                                }
//                                            } catch (Exception ex) {
//                                                ex.printStackTrace();
//                                            }
//                                        }
//
//                                        mAdapter.notifyDataSetChanged();
//                                        showUnSelectedActions();
//                                    }
//
//                                    @Override
//                                    public void onNegativeButtonClick() {
//                                        dialog.dismiss();
//
//                                    }
//                                });
//                                dialog.show(getSupportFragmentManager(), "Delete member alert");
//                            } else if (Delete_Type.equalsIgnoreCase("delete sent message")) {
//                                final CustomDeleteDialog dialog = new CustomDeleteDialog();
//                                dialog.setMessage("Are you sure you want to delete this message?");
//                                dialog.setForMeButtonText("Delete for me");
//                                dialog.setEveryOneButtonText("Delete for everyone");
//                                dialog.setCancelButtonText("Cancel");
//                                dialog.setCancelable(false);
//                                dialog.setDeleteDialogCloseListener(new CustomDeleteDialog.OnDeleteDialogCloseListener() {
//                                    @Override
//                                    public void onForMeButtonClick() {
//                                        for (int i = 0; i < selectedChatItems.size(); i++) {
//                                            MessageItemChat msgItem = selectedChatItems.get(i);
//                                            if (msgItem.isMediaPlaying()) {
//                                                int chatIndex = mChatData.indexOf(msgItem);
//                                                mAdapter.stopAudioOnMessageDelete(chatIndex);
//                                            }
//                                            String lastMsgStatus;
//                                            if (mChatData.size() == 1) {
//                                                lastMsgStatus = "1";
//                                            } else {
//                                                lastMsgStatus = "0";
//                                            }
//                                            String docId = from + "-" + to;
//                                            if (isGroupChat) {
//                                                docId = docId + "-g";
//                                                mConvId = mGroupId;
//                                            } else {
//                                                if (mConvId == null) {
//                                                    mConvId = msgItem.getConvId();
//                                                }
//                                            }
//                                            SendMessageEvent messageEvent = new SendMessageEvent();
//                                            messageEvent.setEventName(SocketManager.EVENT_REMOVE_MESSAGE);
//                                            try {
//                                                JSONObject deleteMsgObj = new JSONObject();
//                                                deleteMsgObj.put("from", from);
//                                                deleteMsgObj.put("type", chatType);
//                                                deleteMsgObj.put("convId", mConvId);
//                                                deleteMsgObj.put("status", "1");
//                                                deleteMsgObj.put("recordId", msgItem.getRecordId());
//                                                deleteMsgObj.put("last_msg", lastMsgStatus);
//                                                messageEvent.setMessageObject(deleteMsgObj);
//                                                EventBus.getDefault().post(messageEvent);
//                                                int index = mChatData.indexOf(selectedChatItems.get(i));
//                                                mChatData.get(index).setMessageType(MessageFactory.DELETE_SELF + "");
//                                                mChatData.get(index).setIsSelf(true);
//
//
//                                                if (isGroupChat) {
//                                                    String str_ids = msgItem.getMessageId();
//                                                    String[] ids = str_ids.split("-");
//                                                    String groupAndMsgId = ids[1] + "-g-" + ids[3];
//                                                    mAdapter.notifyDataSetChanged();
//                                                    db.deleteSingleMessage(groupAndMsgId, str_ids, chatType, "self");
//                                                    db.deleteChatListPage(groupAndMsgId, str_ids, chatType, "self");
//                                                } else {
//                                                    mAdapter.notifyDataSetChanged();
//                                                    db.deleteSingleMessage(docId, msgItem.getMessageId(), chatType, "self");
//                                                    db.deleteChatListPage(docId, msgItem.getMessageId(), chatType, "self");
//                                                }
//
//                                            } catch (Exception ex) {
//                                                ex.printStackTrace();
//                                            }
//                                        }
//                                        showUnSelectedActions();
//                                    }
//
//                                    @Override
//                                    public void onEveryOneButtonClick() {
//                                        for (int i = 0; i < selectedChatItems.size(); i++) {
//                                            MessageItemChat msgItem = selectedChatItems.get(i);
//                                            if (msgItem.isMediaPlaying()) {
//                                                int chatIndex = mChatData.indexOf(msgItem);
//                                                mAdapter.stopAudioOnMessageDelete(chatIndex);
//                                            }
//                                            String lastMsgStatus;
//                                            if (mChatData.size() == 1) {
//                                                lastMsgStatus = "1";
//                                            } else {
//                                                lastMsgStatus = "0";
//                                            }
//                                            String docId = from + "-" + to;
//                                            if (isGroupChat) {
//                                                docId = docId + "-g";
//                                                mConvId = mGroupId;
//                                            } else {
//                                                if (mConvId == null) {
//                                                    mConvId = msgItem.getConvId();
//                                                }
//                                            }
//                                            //     SendMessageEvent messageEvent = new SendMessageEvent();
//                                            //      messageEvent.setEventName(SocketManager.EVENT_REMOVE_MESSAGE);
//                                            try {
//                                                JSONObject deleteMsgObj = new JSONObject();
//                                                deleteMsgObj.put("from", from);
//                                                deleteMsgObj.put("type", chatType);
//                                                deleteMsgObj.put("convId", mConvId);
//                                                deleteMsgObj.put("status", "1");
//                                                deleteMsgObj.put("recordId", msgItem.getRecordId());
//                                                deleteMsgObj.put("last_msg", lastMsgStatus);
//                                                //  messageEvent.setMessageObject(deleteMsgObj);
//                                                // EventBus.getDefault().post(messageEvent);
//                                                int index = mChatData.indexOf(selectedChatItems.get(i));
//
//                                                //--------------Delete Chat--------------
//                                                SendMessageEvent Deletemessage = new SendMessageEvent();
//                                                Deletemessage.setEventName(SocketManager.EVENT_DELETE_MESSAGE);
//                                                try {
//                                                    JSONObject deleteMsg = new JSONObject();
//                                                    deleteMsg.put("from", from);
//                                                    deleteMsg.put("recordId", msgItem.getRecordId());
//                                                    deleteMsg.put("convId", mConvId);
//                                                    if (isGroupChat) {
//                                                        deleteMsg.put("type", "group");
//                                                    } else {
//                                                        deleteMsg.put("type", "single");
//                                                    }
//                                                    Deletemessage.setMessageObject(deleteMsg);
//                                                    EventBus.getDefault().post(Deletemessage);
//                                                } catch (Exception ex) {
//                                                    ex.printStackTrace();
//                                                }
//
//
//                                               /* mChatData.get(index).setMessageType(MessageFactory.DELETE_SELF + "");
//                                                mChatData.get(index).setIsSelf(true);
//
//
//                                                if (isGroupChat) {
//                                                    String str_ids = msgItem.getMessageId();
//                                                    String[] ids = str_ids.split("-");
//                                                    String groupAndMsgId = ids[1] + "-g-" + ids[3];
//                                                    mAdapter.notifyDataSetChanged();
//                                                    db.deleteSingleMessage(groupAndMsgId, str_ids, chatType, "self");
//                                                    db.deleteChatListPage(groupAndMsgId, str_ids, chatType, "self");
//                                                } else {
//                                                    mAdapter.notifyDataSetChanged();
//                                                    db.deleteSingleMessage(docId, msgItem.getMessageId(), chatType, "self");
//                                                    db.deleteChatListPage(docId, msgItem.getMessageId(), chatType, "self");
//                                                }*/
//
//                                            } catch (Exception ex) {
//                                                ex.printStackTrace();
//                                            }
//                                        }
//
//                                        showUnSelectedActions();
//                                    }
//
//                                    @Override
//                                    public void onCancelButtonClick() {
//                                        dialog.dismiss();
//                                    }
//                                });
//                                dialog.show(getSupportFragmentManager(), "Delete member alert");
//                            }
//
//                            view.setSelected(false);
//                        }
//                    });
//
//
//                    starred.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//
//                            Search1.setText("");
//
//                            for (int i = 0; i < selectedChatItems.size(); i++) {
//                                MessageItemChat msgItem = selectedChatItems.get(i);
//
//                                // Add only un-starNextLine if don't have selected starNextLine messages
//                                String starStatus;
//                                if (isSelectedWithUnStarMsg) {
//                                    starStatus = MessageFactory.MESSAGE_STARRED;
//                                } else {
//                                    if (msgItem.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
//                                        starStatus = MessageFactory.MESSAGE_UN_STARRED;
//                                    } else {
//                                        starStatus = MessageFactory.MESSAGE_STARRED;
//                                    }
//                                }
//
//                                SendMessageEvent messageEvent = new SendMessageEvent();
//                                messageEvent.setEventName(SocketManager.EVENT_STAR_MESSAGE);
//                                try {
//                                    JSONObject starMsgObj = new JSONObject();
//                                    starMsgObj.put("from", from);
//                                    starMsgObj.put("type", chatType);
//                                    starMsgObj.put("status", starStatus);
//                                    starMsgObj.put("recordId", msgItem.getRecordId());
//                                    starMsgObj.put("doc_id", msgItem.getMessageId());
//                                    messageEvent.setMessageObject(starMsgObj);
//
//                                    if (isGroupChat) {
//                                        starMsgObj.put("convId", mGroupId);
//                                        EventBus.getDefault().post(messageEvent);
//
//                                        db.updateStarredMessage(msgItem.getMessageId(), starStatus, MessageFactory.CHAT_TYPE_GROUP);
//                                        db.updateTempStarredMessage(msgItem.getRecordId(), starMsgObj);
//                                    } else {
//                                        if (mConvId != null && !mConvId.equals("")) {
//                                            starMsgObj.put("convId", mConvId);
//                                            EventBus.getDefault().post(messageEvent);
//
//                                            db.updateStarredMessage(msgItem.getMessageId(), starStatus, MessageFactory.CHAT_TYPE_SINGLE);
//                                            db.updateTempStarredMessage(msgItem.getRecordId(), starMsgObj);
//                                        }
//                                    }
//                                } catch (Exception ex) {
//                                    ex.printStackTrace();
//                                }
//
//                                String docId = from + "-" + to;
//                                if (isGroupChat) {
//                                    docId = docId + "-g";
//                                }
//
//                                /*MessageDbController db = CoreController.getDBInstance();
//                                db.updateStarredMessage(docId, msgItem.getMessageId(), starStatus);*/
//
//                                int index = mChatData.indexOf(selectedChatItems.get(i));
//                                if (index > -1) {
//                                    mChatData.get(index).setStarredStatus(starStatus);
//                                    mChatData.get(index).setSelected(false);
//                                }
//                            }
//
//                            showUnSelectedActions();
//                        }
//
//                    });
//
//
//                    longpressback.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Search1.setText("");
//                            mAdapter.setfirstItemSelected(false);
//                            showUnSelectedActions();
//                        }
//                    });
//
//                    replymess.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (selectedChatItems.size() == 1) {
//                                Search1.setText("");
//                                r1messagetoreplay.setVisibility(View.VISIBLE);
//                                reply = true;
//                                MessageItemChat msgItem = selectedChatItems.get(0);
//                                int type = Integer.parseInt(msgItem.getMessageType());
//                                mymsgid = msgItem.getRecordId();
//                                replytype = msgItem.getMessageType();
//
//                                //session.putposition(postionreplay);
//                                personimage.setVisibility(View.GONE);
//                                Documentimage.setVisibility(View.GONE);
//                                cameraphoto.setVisibility(View.GONE);
//                                audioimage.setVisibility(View.GONE);
//                                sentimage.setVisibility(View.GONE);
//                                videoimage.setVisibility(View.GONE);
//                                messagesetmedio.setVisibility(View.GONE);
//                                ImageView ivLocation = (ImageView) findViewById(R.id.ivLocation);
//                                ivLocation.setVisibility(View.GONE);
//
//                                if (msgItem.isSelf() && !isGroupChat) {
//                                    ReplySender = "you";
//                                    Ifname.setText("You");
//                                    if (MessageFactory.picture == type) {
//                                        messagesetmedio.setVisibility(View.VISIBLE);
//                                        sentimage.setVisibility(View.VISIBLE);
//                                        cameraphoto.setVisibility(View.VISIBLE);
//                                        message_old.setVisibility(View.GONE);
//                                        messagesetmedio.setText("Photo");
//                                        sentimage.setVisibility(View.VISIBLE);
//                                        try {
//                                            imgpath = msgItem.getImagePath();
//                                            imageAsBytes = msgItem.getThumbnailData();
//                                            sentimage.setImageBitmap(ChatappImageUtils.decodeBitmapFromBase64(
//                                                    imageAsBytes, 50, 50));
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else if (MessageFactory.text == type) {
//                                        message_old.setVisibility(View.VISIBLE);
//                                        messageold = msgItem.getTextMessage();
//                                        message_old.setText(messageold);
//                                        message_old.setTextColor(getResources().getColor(R.color.title));
//                                    } else if (MessageFactory.web_link == type) {
//                                        message_old.setVisibility(View.VISIBLE);
//                                        messageold = msgItem.getWebLinkTitle();
//                                        message_old.setText(messageold);
//                                    } else if (MessageFactory.document == type) {
//                                        messagesetmedio.setVisibility(View.VISIBLE);
//                                        Documentimage.setVisibility(View.VISIBLE);
//                                        message_old.setVisibility(View.GONE);
//                                        messageold = msgItem.getTextMessage();
//                                        messagesetmedio.setText(messageold);
//                                    } else if (MessageFactory.video == type) {
//                                        messagesetmedio.setVisibility(View.VISIBLE);
//                                        videoimage.setVisibility(View.VISIBLE);
//                                        message_old.setVisibility(View.GONE);
//                                        messagesetmedio.setText("Video");
//                                        sentimage.setVisibility(View.VISIBLE);
//                                        try {
//                                            imageAsBytes = msgItem.getThumbnailData().replace("data:image/jpeg;base64,", "");
//                                            Bitmap photo = ConvertToImage(imageAsBytes);
//                                            sentimage.setImageBitmap(photo);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else if (MessageFactory.audio == type) {
//                                        messagesetmedio.setVisibility(View.VISIBLE);
//                                        audioimage.setVisibility(View.VISIBLE);
//                                        message_old.setVisibility(View.GONE);
//                                        messagesetmedio.setText("Audio");
//                                    } else if (MessageFactory.contact == type) {
//                                        contactname = "" + msgItem.getContactName();
//                                        messagesetmedio.setVisibility(View.VISIBLE);
//                                        personimage.setVisibility(View.VISIBLE);
//                                        message_old.setVisibility(View.GONE);
//                                        messagesetmedio.setText(contactname);
//                                    } else if (MessageFactory.location == type) {
//                                        messagesetmedio.setVisibility(View.VISIBLE);
//                                        ivLocation.setVisibility(View.VISIBLE);
//                                        message_old.setVisibility(View.GONE);
//                                        messagesetmedio.setText(msgItem.getWebLinkTitle());
//                                        sentimage.setVisibility(View.VISIBLE);
//                                        locationName = msgItem.getWebLinkTitle();
//                                        try {
//                                            imageAsBytes = msgItem.getWebLinkImgThumb().replace("data:image/jpeg;base64,", "");
//                                            Bitmap photo = ConvertToImage(imageAsBytes);
//                                            sentimage.setImageBitmap(photo);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                    //  msgItem.setReplySender("you");
//                                } else if (!msgItem.isSelf() && !isGroupChat) {
//                                    if (MessageFactory.picture == type) {
//                                        messagesetmedio.setVisibility(View.VISIBLE);
//                                        cameraphoto.setVisibility(View.VISIBLE);
//                                        message_old.setVisibility(View.GONE);
//                                        messagesetmedio.setText("Photo");
//                                        sentimage.setVisibility(View.VISIBLE);
//                                        try {
//                                            imgpath = msgItem.getChatFileLocalPath();
//                                            imageAsBytes = msgItem.getThumbnailData();
//                                            sentimage.setImageBitmap(ChatappImageUtils.decodeBitmapFromBase64(
//                                                    imageAsBytes, 50, 50));
//
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else if (MessageFactory.text == type) {
//                                        message_old.setVisibility(View.VISIBLE);
//                                        messageold = msgItem.getTextMessage();
//                                        message_old.setTextColor(getResources().getColor(R.color.title));
//                                        message_old.setText(messageold);
//
//                                    } else if (MessageFactory.web_link == type) {
//                                        message_old.setVisibility(View.VISIBLE);
//                                        messageold = msgItem.getTextMessage();
//                                        message_old.setText(messageold);
//                                    } else if (MessageFactory.document == type) {
//                                        messagesetmedio.setVisibility(View.VISIBLE);
//                                        Documentimage.setVisibility(View.VISIBLE);
//                                        message_old.setVisibility(View.GONE);
//                                        messageold = msgItem.getTextMessage();
//                                        messagesetmedio.setText(messageold);
//                                    } else if (MessageFactory.video == type) {
//                                        messagesetmedio.setVisibility(View.VISIBLE);
//                                        videoimage.setVisibility(View.VISIBLE);
//                                        message_old.setVisibility(View.GONE);
//                                        messagesetmedio.setText("Video");
//                                        sentimage.setVisibility(View.VISIBLE);
//                                        try {
//                                            imageAsBytes = msgItem.getThumbnailData().replace("data:image/jpeg;base64,", "");
//                                            Bitmap photo = ConvertToImage(imageAsBytes);
//                                            sentimage.setImageBitmap(photo);
////                Picasso.with(mContext).load(imgPath).into(vh2.imageView);
//
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    } else if (MessageFactory.audio == type) {
//                                        messagesetmedio.setVisibility(View.VISIBLE);
//                                        audioimage.setVisibility(View.VISIBLE);
//                                        message_old.setVisibility(View.GONE);
//                                        messagesetmedio.setText("Audio");
//                                    } else if (MessageFactory.contact == type) {
//                                        contactname = "Contact:" + msgItem.getContactName();
//                                        messagesetmedio.setVisibility(View.VISIBLE);
//                                        personimage.setVisibility(View.VISIBLE);
//                                        message_old.setVisibility(View.GONE);
//                                        messagesetmedio.setText(contactname);
//                                    } else if (MessageFactory.location == type) {
//                                        messagesetmedio.setVisibility(View.VISIBLE);
//                                        ivLocation.setVisibility(View.VISIBLE);
//                                        message_old.setVisibility(View.GONE);
//                                        messagesetmedio.setText(msgItem.getWebLinkTitle());
//                                        sentimage.setVisibility(View.VISIBLE);
//                                        locationName = msgItem.getWebLinkTitle();
//                                        try {
//                                            imageAsBytes = msgItem.getWebLinkImgThumb().replace("data:image/jpeg;base64,", "");
//                                            Bitmap photo = ConvertToImage(imageAsBytes);
//                                            sentimage.setImageBitmap(photo);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
////                Picasso.with(mContext).load(imgPath).into(vh2.imageView);
//                                    ReplySender = mReceiverName;
//                                    Ifname.setText(mReceiverName);
//                                } else if (isGroupChat) {
//                                    if (msgItem.isSelf()) {
//                                        ReplySender = "You";
//                                        Ifname.setText(ReplySender);
//                                        if (MessageFactory.picture == type) {
//                                            messagesetmedio.setVisibility(View.VISIBLE);
//                                            cameraphoto.setVisibility(View.VISIBLE);
//                                            message_old.setVisibility(View.GONE);
//                                            messagesetmedio.setText("Photo");
//                                            sentimage.setVisibility(View.VISIBLE);
//                                            try {
//                                                imgpath = msgItem.getImagePath();
//                                                /*
//                                                sentimage.setImageBitmap(ChatappImageUtils.decodeBitmapFromFile(imgpath, 50, 50));*/
//                                                imageAsBytes = msgItem.getThumbnailData();
//                                                sentimage.setImageBitmap(ChatappImageUtils.decodeBitmapFromBase64(
//                                                        imageAsBytes, 50, 50));
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        } else if (MessageFactory.text == type) {
//                                            message_old.setVisibility(View.VISIBLE);
//                                            messageold = msgItem.getTextMessage();
//                                            message_old.setText(messageold);
//                                        } else if (MessageFactory.web_link == type) {
//                                            message_old.setVisibility(View.VISIBLE);
//                                            messageold = msgItem.getTextMessage();
//                                            message_old.setText(messageold);
//                                        } else if (MessageFactory.document == type) {
//                                            messagesetmedio.setVisibility(View.VISIBLE);
//                                            Documentimage.setVisibility(View.VISIBLE);
//                                            message_old.setVisibility(View.GONE);
//                                            messageold = msgItem.getTextMessage();
//                                            messagesetmedio.setText(messageold);
//                                        } else if (MessageFactory.video == type) {
//                                            messagesetmedio.setVisibility(View.VISIBLE);
//                                            videoimage.setVisibility(View.VISIBLE);
//                                            message_old.setVisibility(View.GONE);
//                                            messagesetmedio.setText("Video");
//                                            sentimage.setVisibility(View.VISIBLE);
//                                            try {
//                                                imageAsBytes = msgItem.getThumbnailData().replace("data:image/jpeg;base64,", "");
//                                                Bitmap photo = ConvertToImage(imageAsBytes);
//                                                sentimage.setImageBitmap(photo);
////                Picasso.with(mContext).load(imgPath).into(vh2.imageView);
//
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        } else if (MessageFactory.audio == type) {
//                                            messagesetmedio.setVisibility(View.VISIBLE);
//                                            audioimage.setVisibility(View.VISIBLE);
//                                            message_old.setVisibility(View.GONE);
//                                            messagesetmedio.setText("Audio");
//                                        } else if (MessageFactory.contact == type) {
//                                            contactname = "Contact:" + msgItem.getContactName();
//                                            messagesetmedio.setVisibility(View.VISIBLE);
//                                            personimage.setVisibility(View.VISIBLE);
//                                            message_old.setVisibility(View.GONE);
//                                            messagesetmedio.setText(contactname);
//                                        } else if (MessageFactory.location == type) {
//                                            messagesetmedio.setVisibility(View.VISIBLE);
//                                            ivLocation.setVisibility(View.VISIBLE);
//                                            message_old.setVisibility(View.GONE);
//                                            messagesetmedio.setText(msgItem.getWebLinkTitle());
//                                            sentimage.setVisibility(View.VISIBLE);
//                                            locationName = msgItem.getWebLinkTitle();
//                                            try {
//                                                imageAsBytes = msgItem.getWebLinkImgThumb().replace("data:image/jpeg;base64,", "");
//                                                Bitmap photo = ConvertToImage(imageAsBytes);
//                                                sentimage.setImageBitmap(photo);
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//
//                                    } else {
//                                        ReplySender = getcontactname.getSendername(msgItem.getGroupMsgFrom(), msgItem.getSenderName());
//                                        Ifname.setText(ReplySender);
//                                        if (MessageFactory.picture == type) {
//                                            cameraphoto.setVisibility(View.VISIBLE);
//                                            message_old.setVisibility(View.GONE);
//                                            messagesetmedio.setVisibility(View.VISIBLE);
//                                            messagesetmedio.setText("Photo");
//                                            sentimage.setVisibility(View.VISIBLE);
//                                            try {
//                                                imgpath = msgItem.getChatFileLocalPath();
//                                                /*
//                                                sentimage.setImageBitmap(ChatappImageUtils.decodeBitmapFromFile(imgpath, 50, 50));*/
////                Picasso.with(mContext).load(imgPath).into(vh2.imageView);
//                                                imageAsBytes = msgItem.getThumbnailData();
//                                                sentimage.setImageBitmap(ChatappImageUtils.decodeBitmapFromBase64(
//                                                        imageAsBytes, 50, 50));
//
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        } else if (MessageFactory.text == type) {
//                                            message_old.setVisibility(View.VISIBLE);
//                                            messageold = msgItem.getTextMessage();
//                                            message_old.setText(messageold);
//                                        } else if (MessageFactory.web_link == type) {
//                                            message_old.setVisibility(View.VISIBLE);
//                                            messageold = msgItem.getTextMessage();
//                                            message_old.setText(messageold);
//                                        } else if (MessageFactory.document == type) {
//                                            messagesetmedio.setVisibility(View.VISIBLE);
//                                            Documentimage.setVisibility(View.VISIBLE);
//                                            message_old.setVisibility(View.GONE);
//                                            messageold = msgItem.getTextMessage();
//                                        } else if (MessageFactory.video == type) {
//                                            messagesetmedio.setVisibility(View.VISIBLE);
//                                            videoimage.setVisibility(View.VISIBLE);
//                                            message_old.setVisibility(View.GONE);
//                                            messagesetmedio.setText("Video");
//                                            sentimage.setVisibility(View.VISIBLE);
//                                            try {
//                                                imageAsBytes = msgItem.getThumbnailData().replace("data:image/jpeg;base64,", "");
//                                                Bitmap photo = ConvertToImage(imageAsBytes);
//                                                sentimage.setImageBitmap(photo);
////                Picasso.with(mContext).load(imgPath).into(vh2.imageView);
//
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
////                Picasso.with(mContext).load(imgPath).into(vh2.imageView);
//                                        else if (MessageFactory.audio == type) {
//                                            audioimage.setVisibility(View.VISIBLE);
//                                            messagesetmedio.setVisibility(View.VISIBLE);
//                                            message_old.setVisibility(View.GONE);
//                                            messagesetmedio.setText("Photo");
//                                        } else if (MessageFactory.contact == type) {
//                                            contactname = "Contact:" + msgItem.getContactName();
//                                            messagesetmedio.setVisibility(View.VISIBLE);
//                                            personimage.setVisibility(View.VISIBLE);
//                                            message_old.setVisibility(View.GONE);
//                                            messagesetmedio.setText(contactname);
//                                        } else if (MessageFactory.location == type) {
//                                            messagesetmedio.setVisibility(View.VISIBLE);
//                                            ivLocation.setVisibility(View.VISIBLE);
//                                            message_old.setVisibility(View.GONE);
//                                            messagesetmedio.setText(msgItem.getWebLinkTitle());
//                                            sentimage.setVisibility(View.VISIBLE);
//                                            locationName = msgItem.getWebLinkTitle();
//                                            try {
//                                                imageAsBytes = msgItem.getWebLinkImgThumb().replace("data:image/jpeg;base64,", "");
//                                                Bitmap photo = ConvertToImage(imageAsBytes);
//                                                sentimage.setImageBitmap(photo);
//                                            } catch (Exception e) {
//                                                e.printStackTrace();
//                                            }
//                                        }
//
//                                    }
//                                }
//
//                                //msgItem.setReplySender(username);
//
//                                showUnSelectedActions();
//                            }
//
//                            close.setOnClickListener(new View.OnClickListener() {
//                                @Override
//                                public void onClick(View v) {
//                                    sendMessage.getText().clear();
//                                    r1messagetoreplay.setVisibility(View.GONE);
//                                    reply = false;
//
//                                    showUnSelectedActions();
//                                    iBtnBack.setVisibility(View.VISIBLE);
//                                }
//                            });
//                        }
//                    });
//                }
//            }
//        }));
//
//
//        recyclerView_chat.addOnScrollListener(new RecyclerView.OnScrollListener() {
//                                                  @Override
//                                                  public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                                                      super.onScrolled(recyclerView, dx, dy);
//
//                                                  }
//
//                                                  @Override
//                                                  public void onScrollStateChanged(final RecyclerView recyclerView, int newState) {
//
//
//                                                      try {
//
//                                                          super.onScrollStateChanged(recyclerView, newState);
//
//                                                          if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                                                              visibleItemCount = mLayoutManager.getChildCount();
//                                                              totalItemCount = mLayoutManager.getItemCount();
//                                                              pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
//                                                              lastvisibleitempostion = mLayoutManager.findLastVisibleItemPosition();
//
//                                                              if (pastVisiblesItems > -1 && lastvisibleitempostion > -1) {
//                                                                  if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
//                                                                      iBtnScroll.setVisibility(View.GONE);
//                                                                      unreadcount.setVisibility(View.GONE);
//                                                                      unreadmsgcount = 0;
//                                                                      changeBadgeCount(mConvId);
//                                                                  } else {
//
//                                                                      iBtnScroll.setVisibility(View.VISIBLE);
//                                                                  }
//
//                                                                  MessageItemChat scrolledMsgItem = mChatData.get(pastVisiblesItems);
//                                                                  final MessageItemChat lastMsgItem = mChatData.get(lastvisibleitempostion);
//
//                                                                  String scrolledMsgId = scrolledMsgItem.getMessageId();
//                                                                  if (scrolledMsgId.equalsIgnoreCase(mFirstVisibleMsgId)
//                                                                          && isMessageLoadedOnScroll) {
//                                                                      frameL.setVisibility(View.VISIBLE);
//                                                                      if (!recyclerView.canScrollVertically(-1)) {
//                                                                          frameL.startAnimation(loaderAnimation);
//                                                                      }
//
//
//                                                                      recyclerView_chat.post(new Runnable() {
//                                                                          @Override
//                                                                          public void run() {
//
//                                                                              isMessageLoadedOnScroll = false;
//
//                                                                              String ts = mChatData.get(0).getTS();
//                                                                              ArrayList<MessageItemChat> items = db.selectAllMessagesWithLimit(docId, chatType,
//                                                                                      ts, MessageDbController.MESSAGE_PAGE_LOADED_LIMIT);
//                                                                              Collections.sort(items, msgComparator);
//
//                                                                              if (items.size() > MessageDbController.MESSAGE_PAGE_LOADED_LIMIT - 1) {
//                                                                                  isMessageLoadedOnScroll = true;
//                                                                              }
//
//                                                                              for (int i = 0; i < items.size(); i++) {
//                                                                                  mChatData.add(i, items.get(i));
//                                                                                  mAdapter.notifyItemInserted(mChatData.size());
//                                                                              }
//
//
//                                                                              frameL.startAnimation(loaderAnimation);
//
//                                                                              final int tempIndex = mChatData.indexOf(lastMsgItem);
//                                                                              mFirstVisibleMsgId = mChatData.get(0).getMessageId();
//                                                                              if (tempIndex > -1) {
//                                                                                  recyclerView_chat.post(new Runnable() {
//                                                                                      @Override
//                                                                                      public void run() {
//                                                                                          recyclerView_chat.scrollToPosition(tempIndex - 1);
//                                                                                      }
//                                                                                  });
//                                                                              }
//                                                                              //Do something after 100ms
//                                                                          }
//                                                                      });
//
//                                                                  }
//                                                              }
////                                                          Toast.makeText(ChatViewActivity.this, "Scrolled", Toast.LENGTH_SHORT).show();
//                                                          }
//
//                                                      }
//                                                      catch (Exception e)
//                                                      {
//                                                          e.printStackTrace();
//                                                      }
//                                                  }
//                                              }
//
//        );
//
//
////       /* popup.setSizeForSoftKeyboard();
////        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
////                                       @Override
////                                       public void onDismiss() {
////                                           changeEmojiKeyboardIcon(selEmoji, R.mipmap.ic_msg_panel_smiles);
////                                       }
////                                   }
////
////        );
////        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener()
////
////                                                 {
////
////                                                     @Override
////                                                     public void onKeyboardOpen(int keyBoardHeight) {
////
////                                                     }
////
////                                                     @Override
////                                                     public void onKeyboardClose() {
////                                                         if (popup.isShowing())
////                                                             popup.dismiss();
////                                                     }
////                                                 }
////
////        );
////        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener()
////
////                                           {
////
////                                               @Override
////                                               public void onEmojiconClicked(Emojicon emojicon) {
////                                                   if (sendMessage == null || emojicon == null) {
////                                                       return;
////                                                   }
////
////                                                   int start = sendMessage.getSelectionStart();
////                                                   int end = sendMessage.getSelectionEnd();
////                                                   if (start < 0) {
////                                                       sendMessage.append(emojicon.getEmoji());
////                                                   } else {
////                                                       sendMessage.getText().replace(Math.min(start, end),
////                                                               Math.max(start, end), emojicon.getEmoji(), 0,
////                                                               emojicon.getEmoji().length());
////                                                   }
////                                               }
////                                           }
////
////        );
////        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener()
////
////                                                    {
////
////                                                        @Override
////                                                        public void onEmojiconBackspaceClicked(View v) {
////                                                            KeyEvent event = new KeyEvent(
////                                                                    0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
////                                                            sendMessage.dispatchKeyEvent(event);
////                                                        }
////                                                    }
////
////        );
////*/
//        sendMessage.addTextChangedListener(watch);
//        if (session.isEnterKeyPressToSend()) {
//            sendMessage.setImeOptions(EditorInfo.IME_ACTION_SEND);
//        }
//
//        ivVoiceCall = (ImageView) findViewById(R.id.ivVoiceCall);
//        ivVoiceCall.setOnClickListener(this);
//
//        ivVideoCall = (ImageView) findViewById(R.id.ivVideoCall);
//        ivVideoCall.setOnClickListener(this);
////        ivVideoCall.setVisibility(View.GONE);
//
//        initData();
//        initDataBase();
//
//        if (isGroupChat) {
//            if (isBroadcast) {
//                enableGroupChat = true;
//            }
//            if (!enableGroupChat) {
//                frame_lyt_nolonger.setVisibility(View.VISIBLE);
//                rt_nolonger.setVisibility(View.GONE);
//                rlSend.setVisibility(View.GONE);
//            } else {
//                frame_lyt_nolonger.setVisibility(View.GONE);
//                rt_nolonger.setVisibility(View.VISIBLE);
//                rlSend.setVisibility(View.VISIBLE);
//            }
//        }
//
//        timerReceiver();
//        downloadReceiver();
//
//        loaderAnimation = AnimationUtils.loadAnimation(ChatViewActivityRealcopy.this, R.anim.fast_fade_out);
//        //use this to make it longer:  animation.setDuration(1000);
//        loaderAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                frameL.setVisibility(View.GONE);
//            }
//        });
//    }
//    public void updateDownload(final String msgId, final String end) {
//
//        for (int i = 0; i < mChatData.size(); i++) {
//            if (msgId.equalsIgnoreCase(mChatData.get(i).getMessageId())) {
////                int progress = (bytesRead / fileSize) * 100;
//
//                if (end.equalsIgnoreCase("1")) {
//                    mChatData.get(i).setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_COMPLETED);
////                        db.updateMessageDownloadStatus(docId, msgId, MessageFactory.DOWNLOAD_STATUS_COMPLETED);
//                }
//
//                break;
//            }
//        }
//
//        mAdapter.notifyDataSetChanged();
//
//
//    }
//
//    private void writeBufferToFile(JSONObject object) {
//
//        try {
//            String fileName = object.getString("ImageName");
//            String localPath = object.getString("LocalPath");
////            String localFileName = object.getString("LocalFileName");
//            String end = object.getString("end");
//            int bytesRead = object.getInt("bytesRead");
//            int fileSize = object.getInt("filesize");
//            String msgId = object.getString("MsgId");
//
//            for (int i = 0; i < mChatData.size(); i++) {
//                if (msgId.equalsIgnoreCase(mChatData.get(i).getMessageId())) {
//                    int progress = (bytesRead / fileSize) * 100;
//                    mChatData.get(i).setUploadDownloadProgress(progress);
//
//                    if (end.equalsIgnoreCase("1")) {
//                        mChatData.get(i).setDownloadStatus(MessageFactory.DOWNLOAD_STATUS_COMPLETED);
////                        db.updateMessageDownloadStatus(docId, msgId, MessageFactory.DOWNLOAD_STATUS_COMPLETED);
//                    }
//                    break;
//                }
//            }
//
//            mAdapter.notifyDataSetChanged();
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    TextWatcher watch = new TextWatcher() {
//
//        @Override
//        public void afterTextChanged(Editable arg0) {
//            // TODO Auto-generated method stub
//            // checkLinkDetails(sendMessage.getText().toString().trim());
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//                                      int arg3) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence cs, int a, int b, int c) {
//            // TODO Auto-generated method stub
//
//            handleTypingEvent(cs.length());
//
//            if (cs.length() > 0) {
//
//                //@ feature -> change ImageCaptionActivity also
////                if (isGroupChat) {
////                    if (cs.length() == 1) {
////                        String value = cs.toString();
////                        if (value.equalsIgnoreCase("@")) {
////                            if (allMembersList.size() > 0)
//////                                AppUtils.slideUp(rvGroupMembers);
////                                rvGroupMembers.setVisibility(View.VISIBLE);
////                        }
////                    } else {
////                        String value = cs.toString();
////                        String value1 = value.substring(value.length() - 2);
////                        if (value1.contains(" @")) {
////                            if (allMembersList.size() > 0)
//////                                AppUtils.slideUp(rvGroupMembers);
////                                rvGroupMembers.setVisibility(View.VISIBLE);
////                        }
////                    }
////
////                }
//                capture_timer.setVisibility(View.GONE);
//                attachment_icon.setVisibility(View.GONE);
//                sendButton.setVisibility(View.VISIBLE);
//                record.setVisibility(View.GONE);
//
//
//            } else {
////                if(cs.length() == 0){
////                    if(cs.toString().equalsIgnoreCase("@")){
////                        AppUtils.slideDown(rvGroupMembers.getRootView());
////                    }
////                }
//                if (isGroupChat) {
////
////                    AppUtils.slideDown(rvGroupMembers);
//                    rvGroupMembers.setVisibility(View.GONE);
////                    rvGroupMembers.setVisibility(View.GONE);
//                } else {
//                    capture_timer.setVisibility(View.VISIBLE);
//
//                }
//
//                attachment_icon.setVisibility(View.VISIBLE);
//                sendButton.setVisibility(View.GONE);
//                record.setVisibility(View.VISIBLE);
//            }
//
//        }
//    };
//
//    private void sendTypeEvent() {
//        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//        boolean blockedStatus = contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1");
//        if (!blockedStatus) {
//            JSONObject msgObj = new JSONObject();
//            SendMessageEvent messageEvent = new SendMessageEvent();
//            messageEvent.setEventName(SocketManager.EVENT_TYPING);
//            try {
//                msgObj.put("from", from);
//                msgObj.put("to", to);
//
//                if (!isGroupChat) {
//                    msgObj.put("convId", mConvId);
//                    msgObj.put("type", "single");
//                } else {
//                    msgObj.put("convId", mGroupId);
//                    msgObj.put("type", "group");
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            messageEvent.setMessageObject(msgObj);
//            EventBus.getDefault().post(messageEvent);
//        }
//    }
//
//    private void handleTypingEvent(int length) {
//        if (length > 0) {
//            long currentTime = Calendar.getInstance().getTimeInMillis();
//            long timeDiff = currentTime - fromLastTypedAt;
//            if (timeDiff > MessageFactory.TYING_MESSAGE_MIN_TIME_DIFFERENCE) {
//                fromLastTypedAt = currentTime;
//                if (isGroupChat) {
//                    if (enableGroupChat) {
//                        sendTypeEvent();
//                    }
//                } else {
//                    sendTypeEvent();
//                }
//            }
//        }
//    }
//
//    private String getTimeString(long millis) {
//        StringBuffer buf = new StringBuffer();
//
//        int hours = (int) (millis / (1000 * 60 * 60));
//        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
//        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);
//
//        buf.append(String.format("%02d", minutes))
//                .append(":")
//                .append(String.format("%02d", seconds));
//
//        return buf.toString();
//    }
//
//    private void checkLinkDetails(String text) {
//
//        text = " " + text;
//        String[] arrSplitText = text.split(" ");
//        for (final String splitText : arrSplitText) {
//
//            if (Patterns.WEB_URL.matcher(splitText).matches()) {
//
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            String url = splitText;
//                            if (!splitText.startsWith("http")) {
//                                url = "http://" + splitText;
//                            }
//                            final Document doc = Jsoup.connect(url).get();
//
//                            Elements metaElems = doc.select("meta");
////                            webLinkDesc = metaElems.tagName("description").first().attr("content");
//
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    webLinkTitle = doc.title();
//                                    webLink = doc.baseUri();
//                                    webLinkImgUrl = webLink + "/favicon.ico";
////                                    webLinkImgUrl = webLink.substring(0, webLink.lastIndexOf("/")) + "/favicon.ico";
////                                            webLinkImgUrl = doc.head().select("meta[itemprop=image]").first().attr("content");
//                                    tvWebTitle.setText(webLinkTitle);
//                                    tvWebLink.setText(webLink);
////                                    tvWebLinkDesc.setText(webLinkDesc);
//                                    Glide.with(ChatViewActivityRealcopy.this).load(webLinkImgUrl).into(ivWebLink);
//
//                                    if (rlWebLink.getVisibility() == View.GONE) {
//                                        rlWebLink.setVisibility(View.VISIBLE);
//                                    }
//                                }
//                            });
//
//                            hasLinkInfo = true;
//                                    /*String keywords = doc.select("meta[name=keywords]").first().attr("content");
//                                    Log.e("Meta keyword : ", keywords);
//                                    String description = doc.select("meta[name=description]").first().attr("content");
//                                    Log.e("Meta description : ", description);*/
//                        } catch (IOException ex) {
//                            ex.printStackTrace();
//                        } catch (NullPointerException nullEx) {
//                            hasLinkInfo = false;
//                            nullEx.printStackTrace();
//                        } catch (Exception e) {
//                            hasLinkInfo = false;
//                            e.printStackTrace();
//                        }
//                    }
//                }).start();
//                break;
//            } else {
//                hasLinkInfo = false;
//            }
//
//        }
//
//
//    }
//
//    private void initData() {
//        //Log.d(TAG, "initData: ");
//        mChatData = new ArrayList<>();
//        uploadDownloadManager = new FileUploadDownloadManager(ChatViewActivityRealcopy.this);
//        uploadDownloadManager.setListener(this);
//        groupInfoSession = new GroupInfoSession(ChatViewActivityRealcopy.this);
//
//        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
//        recyclerView_chat.setLayoutManager(mLayoutManager);
//        //recyclerView_chat.setItemAnimator(new DefaultItemAnimator());
//        //((DefaultItemAnimator) recyclerView_chat.getItemAnimator()).setSupportsChangeAnimations(false);
//
///*        RecyclerView.ItemAnimator animator = recyclerView_chat.getItemAnimator();
//
//        if (animator instanceof DefaultItemAnimator) {
//            ((DefaultItemAnimator) animator).setSupportsChangeAnimations(false);
//        }*/
//        mAdapter = new ChatMessageAdapterNew(this, mChatData, ChatViewActivityRealcopy.this.getSupportFragmentManager(), isBroadcast);
//        mAdapter.setCallback(ChatViewActivityRealcopy.this);
//        //performance improvement code
//        mAdapter.setHasStableIds(true);
//        recyclerView_chat.setItemViewCacheSize(20);
//        recyclerView_chat.setDrawingCacheEnabled(true);
//        recyclerView_chat.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
//        recyclerView_chat.getRecycledViewPool().setMaxRecycledViews(ChatMessageAdapterNew.MESSAGERECEIVED, 50);
//        recyclerView_chat.getRecycledViewPool().setMaxRecycledViews(ChatMessageAdapterNew.MESSAGESENT, 50);
//        recyclerView_chat.getRecycledViewPool().setMaxRecycledViews(ChatMessageAdapterNew.IMAGERECEIVED, 50);
//        recyclerView_chat.getRecycledViewPool().setMaxRecycledViews(ChatMessageAdapterNew.IMAGESENT, 50);
//        recyclerView_chat.getRecycledViewPool().setMaxRecycledViews(ChatMessageAdapterNew.VIDEORECEIVED, 50);
//        recyclerView_chat.getRecycledViewPool().setMaxRecycledViews(ChatMessageAdapterNew.VIDEOSENT, 50);
//        recyclerView_chat.post(new Runnable() {
//            @Override
//            public void run() {
//                recyclerView_chat.setAdapter(mAdapter);
//            }
//        });
//
//        recyclerView_chat.setHasFixedSize(true);
//        recyclerView_chat.setNestedScrollingEnabled(false);
//
//
//        if (getIntent().getData() != null) {
//            Cursor cursor = managedQuery(getIntent().getData(), null, null, null, null);
//            if (cursor.moveToNext()) {
//                receiverUid = cursor.getString(cursor.getColumnIndex("DATA1"));
//                to = cursor.getString(cursor.getColumnIndex("DATA1"));
//                receiverMsisdn = cursor.getString(cursor.getColumnIndex("DATA2"));
//                backfrom = true;
//                mReceiverName = getcontactname.getSendername(receiverUid, receiverMsisdn);
//            }
//
//        } else {
//
//            Bundle bundle = getIntent().getExtras();
//            if (bundle != null) {
//                backfrom = bundle.getBoolean("backfrom");
//                receiverUid = bundle.getString("receiverUid");
//                msgid = bundle.getString("msgid", "");
//                receiverName = bundle.getString("receiverName");
//                to = bundle.getString("documentId");
//
//                mReceiverName = bundle.getString("Username");
//                receiverAvatar = bundle.getString("Image");
//                receiverMsisdn = bundle.getString("msisdn");
//
//
//            }
//        }
//
//        mReceiverId = to;
//        Chat_to = mReceiverId;
//        if (session.getmark(to)) {
//            session.Removemark(to);
//        }
//
//        if (mReceiverName == null || mReceiverName.equalsIgnoreCase("")) {
//            mReceiverName = getcontactname.getSendername(receiverUid, receiverMsisdn);
//        }
//        receiverNameText.setText(mReceiverName);
//
//        from = sessionManager.getCurrentUserID();
//        mCurrentUserId = sessionManager.getCurrentUserID();
//        sendButton.setOnClickListener(this);
//        attachment.setOnClickListener(this);
//        capture_timer.setOnClickListener(this);
//    }
//
//    private boolean checkIsAlreadyLocked(String docId) {
//        String convId = userInfoSession.getChatConvId(docId);
//        String receiverId = userInfoSession.getReceiverIdByConvId(convId);
//        ChatLockPojo lockPojo = db.getChatLockData(receiverId, chatType);
//        if (sessionManager.getLockChatEnabled().equals("1") && lockPojo != null) {
//            String stat = lockPojo.getStatus();
//            String pwd = lockPojo.getPassword();
//            isAlrdychatlocked = stat.equals("1");
//        } else {
//            isAlrdychatlocked = false;
//        }
//
//        return isAlrdychatlocked;
//    }
//
//    private void getReceiverOnlineTimeStatus() {
//        try {
//            JSONObject object = new JSONObject();
//            object.put("from", mCurrentUserId);
//            object.put("to", to);
//
//            SendMessageEvent event = new SendMessageEvent();
//            event.setEventName(SocketManager.EVENT_GET_CURRENT_TIME_STATUS);
//            event.setMessageObject(object);
//            EventBus.getDefault().post(event);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void wallpaperdisplay() {
//        if (session.getgalleryPrefsName().equals("def")) {
//            background.setImageResource(R.drawable.chat_background);
//        } else if (session.getgalleryPrefsName().equals("no")) {
//            Bitmap bmp = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bmp);
//            canvas.drawColor(Color.parseColor("#f0f0f0"));
//            background.setImageBitmap(bmp);
//        } else if (!session.getgalleryPrefsName().equals("")) {
//            session.putgalleryPrefs(session.getgalleryPrefsName());
//        } else if (!session.getColor().equals("")) {
//            try {
//                Bitmap bmp = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
//                Canvas canvas = new Canvas(bmp);
//                canvas.drawColor(Color.parseColor(session.getColor()));
//                background.setImageBitmap(bmp);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            Bitmap bmp = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
//            Canvas canvas = new Canvas(bmp);
//            canvas.drawColor(Color.parseColor("#f0f0f0"));
//            background.setImageBitmap(bmp);
//        }
//    }
//
//    private void loadFromDB() {
//        Log.d(TAG, "loadFromDB: ");
//        docId = from + "-" + to + "-g";
////        if(isBroadcast)
////        {
////            docId = from + "-" + to + "-g"+ Message.;
////
////        }
//        if (db.isGroupId(docId)) {
//            ivVideoCall.setVisibility(View.GONE);
//            ivVoiceCall.setVisibility(View.GONE);
//            Ripple_call.setVisibility(View.GONE);
//            Ripple_Video.setVisibility(View.GONE);
//            isGroupChat = true;
//            mGroupId = to;
//            mConvId = to;
//            Activity_GroupId = mGroupId;
//            chatType = MessageFactory.CHAT_TYPE_GROUP;
//
//            String groupDocId = from + "-" + to + "-g";
//            final ArrayList<MessageItemChat> dbItems = db.selectAllMessagesWithLimit(docId, chatType,
//                    "", MessageDbController.MESSAGE_SELECTION_LIMIT_FIRST_TIME);
//
//
//            Collections.sort(dbItems, msgComparator);
//
//            if (dbItems.size() > 0) {
//                mFirstVisibleMsgId = dbItems.get(0).getMessageId();
//            }
//
////            boolean val;
////            for(int i=0;i<selectedChatItems.size();i++)
////            {
////
////                 val=dbItems.contains(selectedChatItems.get(i));
//////                if(val>0) {
//////
//////                    dbItems.get(val).setSelected(true);
//////                }
////            }
//
//            mChatData.clear();
//            mChatData.addAll(dbItems);
//
//
//            notifyDatasetChange();
//
//            sendAllAcksToServer(dbItems);
//            changeBadgeCount(groupDocId);
//
//            hasGroupInfo = groupInfoSession.hasGroupInfo(docId);
//
//
//            if (hasGroupInfo) {
//                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
//                StringBuilder sb = new StringBuilder();
//                String memername;
//                if (infoPojo != null && infoPojo.getGroupMembers() != null) {
//                    String[] contacts = infoPojo.getGroupMembers().split(",");
//
//                    for (int i = 0; i < contacts.length; i++) {
//                        if (!contacts[i].equalsIgnoreCase(from)) {
//                            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                            ChatappContactModel info = contactDB_sqlite.getUserOpponenetDetails(contacts[i]);
//                            if (info != null) {
//                                //  memername = getcontactname.getSendername(contacts[i], info.getMsisdn());
//                                memername = info.getFirstName();
//                                sb.append(memername);
//                                if (contacts.length - 1 != i) {
//                                    sb.append(", ");
//                                }
//                            }
//                        } else {
//                            memername = "You";
//                            sb.append(memername);
//                            if (contacts.length - 1 != i) {
//                                sb.append(", ");
//                            }
//                        }
//                    }
//
//                    statusTextView.setText(sb);
//                    if (isBroadcast) {
//                        enableGroupChat = true;
//                    } else {
//                        enableGroupChat = infoPojo.isLiveGroup();
//                    }
//                }
//            }
//
//        } else {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    getReceiverOnlineTimeStatus();
//                }
//            }, 500);
//
//
//            isGroupChat = false;
//            chatType = MessageFactory.CHAT_TYPE_SINGLE;
//
//            if (isBroadcast) {
//                ivVideoCall.setVisibility(View.GONE);
//                ivVoiceCall.setVisibility(View.GONE);
//                Ripple_call.setVisibility(View.GONE);
//                Ripple_Video.setVisibility(View.GONE);
//            }
//
//            docId = from + "-" + to;
//            String singeDocId = from + "-" + to;
////            final ArrayList<MessageItemChat> items = db.selectAllChatMessages(docId, chatType);
//            ArrayList<MessageItemChat> items = db.selectAllMessagesWithLimit(docId, chatType,
//                    "", MessageDbController.MESSAGE_SELECTION_LIMIT_FIRST_TIME);
//
////            Toast.makeText(context, docId, Toast.LENGTH_SHORT).show();
//
//            BaseMessage baseMessage = new BaseMessage(this);
//            Long timeStamp = Long.parseLong(baseMessage.getShortTimeFormat());
//            //  String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";
//
//
//            for (int i = 0; i < items.size(); i++) {
////                if (Integer.parseInt(items.get(i).getMessageType())==MessageFactory.text) {
//
//                if (items.get(i).getIsExpiry() != null) {
//
//                    if (items.get(i).getIsExpiry().equals("1") && items.get(i).getReadTime() != null) {
//                        Long expiredTime = Long.parseLong(items.get(i).getReadTime()) + (Long.parseLong(items.get(i).getExpiryTime()) * 1000);
//
//                        if (expiredTime <= timeStamp) {
//
//                            MessageItemChat msgItem = items.get(i);
//
//                            if (msgItem.isMediaPlaying()) {
//                                int chatIndex = mChatData.indexOf(msgItem);
//                                mAdapter.stopAudioOnMessageDelete(chatIndex);
//                            }
//
//                            String lastMsgStatus;
//                            if (mChatData.size() == 1) {
//                                lastMsgStatus = "1";
//                            } else {
//                                lastMsgStatus = "0";
//                            }
//
//                            String docId = from + "-" + to;
//                            if (isGroupChat) {
//                                docId = docId + "-g";
//                                mConvId = mGroupId;
//                            } else {
//                                if (mConvId == null) {
//                                    mConvId = msgItem.getConvId();
//                                }
//                            }
//
////                            SendMessageEvent messageEvent = new SendMessageEvent();
////                            messageEvent.setEventName(SocketManager.EVENT_REMOVE_MESSAGE);
////                            try {
////                                JSONObject deleteMsgObj = new JSONObject();
////                                deleteMsgObj.put("from", from);
////                                deleteMsgObj.put("type", chatType);
////                                deleteMsgObj.put("convId", mConvId);
////                                deleteMsgObj.put("status", "1");
////                                deleteMsgObj.put("recordId", msgItem.getRecordId());
////                                deleteMsgObj.put("last_msg", lastMsgStatus);
////                                messageEvent.setMessageObject(deleteMsgObj);
////                                EventBus.getDefault().post(messageEvent);
//
//                            db.deleteChatMessage(docId, msgItem.getMessageId(), chatType);
//                            //       db.updateTempDeletedMessage(msgItem.getRecordId(), deleteMsgObj);
//
//                            int index = mChatData.indexOf(items.get(i));
//                            if (index > -1) {
//                                mChatData.remove(index);
//                            }
////                            } catch (Exception ex) {
////                                ex.printStackTrace();
////                            }
//                        } else {
//                            long timer=timeStamp - expiredTime;
//
//                            onTimer(Math.abs(timer),items.get(i));
//                        }
//                    }
//                }
//
//            }
//
//            items = db.selectAllMessagesWithLimit(docId, chatType,
//                    "", MessageDbController.MESSAGE_SELECTION_LIMIT_FIRST_TIME);
//
//            Collections.sort(items, msgComparator);
//
//            if (items.size() > 0) {
//                mFirstVisibleMsgId = items.get(0).getMessageId();
//            }
//
//            if (userInfoSession.hasChatConvId(docId)) {
//                mConvId = userInfoSession.getChatConvId(docId);
//            }
//
//            getUserDetails(to);
//            changeBadgeCount(singeDocId);
//            sendAllAcksToServer(items);
//
//            mChatData.clear();
//            mChatData.addAll(items);
//            notifyDatasetChange();
//            Log.d(TAG, "loadFromDB: finished");
//
//            if (isBroadcast) {
//
//                StringBuilder csvBuilder = new StringBuilder();
//
//                for (ChatappContactModel city : chatappContactModels) {
//
//                    csvBuilder.append(city.getFirstName());
//
//                    csvBuilder.append(",");
//
//                }
//
//                String csv = csvBuilder.toString();
//
//                csv = csv.substring(0, csv.length() - 1);
//
//                statusTextView.setText(csv);
//            }
//        }
//
//        if (isBroadcast) {
//            ivVideoCall.setVisibility(View.GONE);
//            ivVoiceCall.setVisibility(View.GONE);
//            Ripple_call.setVisibility(View.GONE);
//            Ripple_Video.setVisibility(View.GONE);
//        }
//    }
//
//    private Comparator<MessageItemChat> msgComparator = new Comparator<MessageItemChat>() {
//
//        @Override
//        public int compare(MessageItemChat lhs, MessageItemChat rhs) {
//
//            long lhsMsgTS = Long.parseLong(lhs.getTS());
//            long rhsMsgTS = Long.parseLong(rhs.getTS());
//
//            if (lhsMsgTS > rhsMsgTS) {
//                return 1;
//            } else {
//                return -1;
//            }
//        }
//
//
//    };
//
//
//    private void getgroupmemberdetails() {
//        String docId = mCurrentUserId.concat("-").concat(to).concat("-g");
//        hasGroupInfo = groupInfoSession.hasGroupInfo(docId);
//        if (hasGroupInfo) {
//            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
//            if (infoPojo != null) {
//                String[] membersId = infoPojo.getGroupMembers().split(",");
//                for (int i = 0; i < membersId.length; i++) {
//                    getUserDetails(membersId[i]);
//                }
//
//            }
//        }
//    }
//
//    private void performGroupChangeDp(JSONObject object) {
//        try {
//            String err = object.getString("err");
//
//            if (err.equalsIgnoreCase("0")) {
//                String from = object.getString("from");
//                String groupId = object.getString("groupId");
//                String avatar = object.getString("avatar");
//                String groupName = object.getString("groupName");
//                String senderOriginalName="";
////
//                if(object.has("fromuser_name")) {
//
//                    try {
//                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//                String timeStamp;
//                if (object.has("timeStamp")) {
//                    timeStamp = object.getString("timeStamp");
//                } else {
//                    timeStamp = object.getString("timestamp");
//                }
//
//
//                if (groupId.equalsIgnoreCase(mGroupId)) {
//                    receiverAvatar = avatar;
///*                    Glide.with(ChatViewActivity.this).load(Constants.SOCKET_IP.concat(receiverAvatar))
//
//                            .error(R.mipmap.group_chat_attachment_profile_icon).into(ivProfilePic);*/
//
//                    AppUtils.loadImage(ChatViewActivityRealcopy.this, Constants.SOCKET_IP.concat(receiverAvatar),
//                            ivProfilePic, 100, R.mipmap.group_chat_attachment_profile_icon);
//                    GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
//                    MessageItemChat item = message.createMessageItem(MessageFactory.change_group_icon, false, null,
//                            MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, from, null,senderOriginalName);
//                    item.setAvatarImageUrl(avatar);
//                    item.setTS(timeStamp);
//
//                    String id;
//                    if (object.has("id")) {
//                        id = object.getString("id");
//                    } else {
//                        id = Calendar.getInstance().getTimeInMillis() + "";
//                    }
//                    item.setMessageId(docId.concat("-").concat(id));
//
//                    if (!from.equalsIgnoreCase(mCurrentUserId)) {
//                        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                        ChatappContactModel contactModel = contactDB_sqlite.getUserOpponenetDetails(from);
//                        if (contactModel != null) {
//                            mChatData.add(item);
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    } else {
//                        mChatData.add(item);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }
//
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void sendAllAcksToServer(ArrayList<MessageItemChat> items) {
//        // For sent & receive acks
//        MessageItemChat lastDeliveredItem = null;
//        MessageItemChat lastSentItem = null;
//        JSONArray arrSentMsgRecordIds = new JSONArray();
//
//        String chatType = MessageFactory.CHAT_TYPE_SINGLE;
//        if (isGroupChat) {
//            chatType = MessageFactory.CHAT_TYPE_GROUP;
//        }
//
//        for (MessageItemChat msgItem : items) {
//            String msgStatus = msgItem.getDeliveryStatus();
//
//            if (msgItem.getConvId() != null && !msgItem.getConvId().equals("")) {
//                mConvId = msgItem.getConvId();
//            }
//
//            if (msgStatus != null) {
//                if (!msgItem.isSelf() && !msgStatus.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
//                    if (isGroupChat && msgItem.getGroupMsgFrom() != null) {
//                        lastDeliveredItem = msgItem;
//                    } else if (!isGroupChat) {
//                        lastDeliveredItem = msgItem;
//                    }
//                } else if (msgItem.isSelf() && (msgStatus.equals(MessageFactory.DELIVERY_STATUS_SENT)
//                        || msgStatus.equals(MessageFactory.DELIVERY_STATUS_DELIVERED))) {
//                    if (msgItem.getRecordId() != null && arrSentMsgRecordIds.length() < 100) {
//                        arrSentMsgRecordIds.put(msgItem.getRecordId());
//                    }
//                }
//
//                if (msgItem.isSelf() && !msgStatus.equals(MessageFactory.DELIVERY_STATUS_NOT_SENT)) {
//                    lastSentItem = msgItem;
//                }
//
//                // For getting reply message details from server
//                if (!msgItem.isSelf() && msgItem.getReplyId() != null && !msgItem.getReplyId().equals("")
//                        && (msgItem.getReplyType() == null || msgItem.getReplyType().equals(""))) {
//                    getReplyMessageDetails(mReceiverId, msgItem.getReplyId(), chatType, "no", msgItem.getMessageId());
//                }
//            }
//        }
//
//        if (lastDeliveredItem != null) {
//            if (sessionManager.canSendReadReceipt()) {
//                if (isGroupChat) {
//                    String msgId = lastDeliveredItem.getMessageId().split("-")[3];
//                    sendGroupAckToServer(mCurrentUserId, mGroupId, msgId, MessageFactory.GROUP_MSG_READ_ACK);
//                } else {
//                    String msgId = lastDeliveredItem.getMessageId().split("-")[2];
//                    String ackDocId = to.concat("-").concat(mCurrentUserId).concat("-").concat(msgId);
//                    sendAckToServer(to, ackDocId, msgId);
//                }
//
//                sendViewedStatusToWeb(lastDeliveredItem.getConvId());
//            }
//        } else if (lastSentItem != null && lastSentItem.getConvId() != null) {
//            sendViewedStatusToWeb(lastSentItem.getConvId());
//        }
//
//        if (arrSentMsgRecordIds.length() > 0) {
//            getMessageInfo(arrSentMsgRecordIds);
//        }
//
//        if (!isGroupChat) {
//            String docId = mCurrentUserId + "-" + mReceiverId;
//            if (userInfoSession.hasChatConvId(docId)) {
//                mConvId = userInfoSession.getChatConvId(docId);
//            }
//        }
//    }
//
//    public void getReplyMessageDetails(String toUserId, String recordId, String chatType,
//                                       String secretType, String msgId) {
//        try {
//            JSONObject object = new JSONObject();
//            object.put("from", mCurrentUserId);
//            object.put("to", toUserId);
//            object.put("recordId", recordId);
//            object.put("requestMsgId", msgId);
//            object.put("type", chatType);
//            object.put("secret_type", secretType);
//
//            SendMessageEvent event = new SendMessageEvent();
//            event.setEventName(SocketManager.EVENT_GET_MESSAGE_DETAILS);
//            event.setMessageObject(object);
//            EventBus.getDefault().post(event);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void getMessageInfo(JSONArray arrSentMsgRecordIds) {
//
//        try {
//            JSONObject object = new JSONObject();
//            object.put("recordId", arrSentMsgRecordIds);
//            object.put("from", mCurrentUserId);
//            if (isGroupChat) {
//                object.put("type", MessageFactory.CHAT_TYPE_GROUP);
//            } else {
//                object.put("type", MessageFactory.CHAT_TYPE_SINGLE);
//            }
//
//            SendMessageEvent infoEvent = new SendMessageEvent();
//            infoEvent.setEventName(SocketManager.EVENT_GET_MESSAGE_INFO);
//            infoEvent.setMessageObject(object);
//            EventBus.getDefault().post(infoEvent);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendViewedStatusToWeb(String convId) {
//        try {
//            JSONObject object = new JSONObject();
//            object.put("from", mCurrentUserId);
//            object.put("convId", convId);
//            if (isGroupChat) {
//                object.put("type", "group");
//            } else {
//                object.put("type", "single");
//            }
//            object.put("mode", "phone");
//
//            SendMessageEvent event = new SendMessageEvent();
//            event.setEventName(SocketManager.EVENT_VIEW_CHAT);
//            event.setMessageObject(object);
//            EventBus.getDefault().post(event);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void changeBadgeCount(String docId) {
//        session.Removemark(to);
//        if (mConvId != null && !mConvId.equals("")) {
//            ShortcutBadgeManager shortcutBadgeMgnr = new ShortcutBadgeManager(ChatViewActivityRealcopy.this);
//            shortcutBadgeMgnr.removeMessageCount(mConvId);
//            int totalCount = shortcutBadgeMgnr.getTotalCount();
//            // Badge working if supported devices
//            if (totalCount > 0) {
//                try {
//                    ShortcutBadger.applyCountOrThrow(context, totalCount);
//                } catch (ShortcutBadgeException e) {
//                    e.printStackTrace();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public void openDialog(View include) {
//        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        final View inflatedView;
//        inflatedView = layoutInflater.inflate(R.layout.custom_dialog_options_menu, null, false);
//        final LinearLayout layoutGallery, layoutDocument, layoutVideo, layoutContacts, layoutAudio, layoutLocation;
//        TextView tvGallery, tvDocument, tvVideo, tvContacts, tvAudio, tvLocation;
//        tvGallery = (TextView) inflatedView.findViewById(R.id.tvGallery);
//        tvDocument = (TextView) inflatedView.findViewById(R.id.tvDocument);
//        tvVideo = (TextView) inflatedView.findViewById(R.id.tvVideo);
//        tvAudio = (TextView) inflatedView.findViewById(R.id.tvAudio);
//        tvContacts = (TextView) inflatedView.findViewById(R.id.tvContact);
//        tvLocation = (TextView) inflatedView.findViewById(R.id.tvLocation);
//
//        layoutGallery = (LinearLayout) inflatedView.findViewById(R.id.layoutGallery);
//        layoutGallery.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FloatingView.dismissWindow();
//
//                Intent i = new Intent(ChatViewActivityRealcopy.this, ImagecaptionActivity.class);
//                i.putExtra("phoneno", mReceiverName);
//                i.putExtra("from", "Gallary");
//                startActivityForResult(i, RESULT_LOAD_IMAGE);
//
//
//            }
//        });
//        layoutDocument = (LinearLayout) inflatedView.findViewById(R.id.layoutDocument);
//        layoutDocument.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FloatingView.dismissWindow();
//
//                FilePickerBuilder.getInstance().setMaxCount(1)
//                        .setSelectedFiles(new ArrayList<String>())
//                        .setActivityTheme(R.style.AppTheme)
//                        .pickDocument(ChatViewActivityRealcopy.this);
//
//                /*Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);//mms quality video not hd
//                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);//max 120s video
//                intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 26214400L);//max 2.5 mb size recording
//                startActivityForResult(intent, RESULT_CAPTURE_VIDEO);*/
//            }
//        });
//        layoutVideo = (LinearLayout) inflatedView.findViewById(R.id.layoutVideo);
//        layoutVideo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FloatingView.dismissWindow();
//                /*Intent intent = new Intent();
//                intent.setType("text/plain");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent, RESULT_LOAD_VIDEO);*/
//                Intent i = new Intent(ChatViewActivityRealcopy.this, ImagecaptionActivity.class);
//                i.putExtra("phoneno", mReceiverName);
//                i.putExtra("from", "Video");
//                startActivityForResult(i, RESULT_LOAD_VIDEO);
//
//
//            }
//        });
//        layoutContacts = (LinearLayout) inflatedView.findViewById(R.id.layoutContact);
//        layoutContacts.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FloatingView.dismissWindow();
//                Intent intentContact = new Intent(ChatViewActivityRealcopy.this, SendContact.class);
//                startActivityForResult(intentContact, REQUEST_CODE_CONTACTS);
//            }
//        });
//
//        layoutAudio = (LinearLayout) inflatedView.findViewById(R.id.layoutAudio);
//        layoutAudio.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FloatingView.dismissWindow();
//                /*Intent intent_upload = new Intent();
//                intent_upload.setType("audio*//*");
//                intent_upload.setAction(Intent.ACTION_GET_CONTENT);
//                startActivityForResult(intent_upload, REQUEST_SELECT_AUDIO);*/
//                Intent audioIntent = new Intent(ChatViewActivityRealcopy.this, AudioFilesListActivity.class);
//                startActivityForResult(audioIntent, REQUEST_SELECT_AUDIO);
//            }
//        });
//        layoutLocation = (LinearLayout) inflatedView.findViewById(R.id.layoutLocation);
//        layoutLocation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                FloatingView.dismissWindow();
//                GPSTracker gps = new GPSTracker(getApplicationContext());
//                // if (gps.canGetLocation()) {
//
//                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//                try {
//                    startActivityForResult(builder.build(ChatViewActivityRealcopy.this), RESULT_SHARE_LOCATION);
//                } catch (GooglePlayServicesRepairableException e) {
//                    e.printStackTrace();
//                } catch (GooglePlayServicesNotAvailableException e) {
//                    e.printStackTrace();
//                }
//                /*} else {
//                    View clSnackBar = findViewById(R.id.clSnackBar);
//                    if (clSnackBar != null) {
//                        Snackbar snackbar = Snackbar.make(clSnackBar, "Please Enable GPS", Snackbar.LENGTH_SHORT);
//                        snackbar.show();
//                        View view2 = snackbar.getView();
//                        TextView txtv = (TextView) view2.findViewById(android.support.design.R.id.snackbar_text);
//                        txtv.setGravity(Gravity.CENTER_HORIZONTAL);
//                    }
//                }*/
//            }
//        });
//        FloatingView.onShowPopup(this, inflatedView, include);
//    }
//
//
//    @Override
//    public void onStart() {
//        super.onStart();
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//
//        if (toTypingRunnable != null) {
//            toTypingHandler.removeCallbacks(toTypingRunnable);
//        }
//        if (mConvId != null && !mConvId.equals("")) {
////            sendViewedStatusToWeb(mConvId);
//        }
//
//    }
//
//
//    @Override
//    public void onClick(View view) {
//        if (view.getId() == R.id.enter_chat1) {
//            if (!isBroadcast) {
//                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                if (contactDB_sqlite.getBlockedStatus(to, false).equals("1")) {
//                    DisplayAlert("Unblock" + " " + mReceiverName + " " + "to send message?");
//                } else {
//                    if ((!hasLinkInfo && !reply)) {
//                        reply = false;
//                        sendTextMessage();
//                    } else if (reply) {
//                        r1messagetoreplay.setVisibility(View.GONE);
//                        sendparticularmsgreply();
//                        if (at_memberlist != null) {
//                            at_memberlist.clear();
//                        }
//                    } else {
//                        sendWebLinkMessage();
//                        if (at_memberlist != null) {
//                            at_memberlist.clear();
//                        }
//                    }
//                }
//            } else {
//                if ((!hasLinkInfo && !reply)) {
//                    reply = false;
//                    sendTextMessageBroadcast();
//                } else if (reply) {
//                    r1messagetoreplay.setVisibility(View.GONE);
//                    sendparticularmsgreply();
//                    if (at_memberlist != null) {
//                        at_memberlist.clear();
//                    }
//                } else {
//                    sendWebLinkMessage();
//                    if (at_memberlist != null) {
//                        at_memberlist.clear();
//                    }
//                }
//            }
//        } else if (view.getId() == R.id.attachment) {
//            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//            if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
//                DisplayAlert("Unblock" + " " + mReceiverName + " " + "to send message?");
//            } else {
//                openDialog(include);
//            }
//        } else if (view.getId() == R.id.Ripple_Video) {
//            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//            if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
//                DisplayAlert("Unblock" + " " + mReceiverName + " " + "to place a "
//                        + getString(R.string.app_name) + " call");
//            } else {
//                Activecall(true);
//            }
//        } else if (view.getId() == R.id.Ripple_call) {
//            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//            if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
//                DisplayAlert("Unblock" + " " + mReceiverName + " " + "to place a "
//                        + getString(R.string.app_name) + " call");
//            } else {
//                Activecall(false);
//            }
//        } else if (view.getId() == R.id.iBtnBack) {
//            if (backfrom) {
//                mAdapter.stopAudioOnClearChat();
//                Intent intent = new Intent(this, HomeScreen.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//                finish();
//            } else {
//                finish();
//            }
//            Chat_Activity = null;
//        } else if (view.getId() == R.id.nameMAincontainer || view.getId() == R.id.profileImageChatScreen) {
//            goInfoPage();
//        } else if (view.getId() == R.id.overflow) {
//            if (!isBroadcast) {
//                openMenu();
//            }
//        } else if (view.getId() == R.id.capture_timer) {
//            alertTimer();
//        }
////        else if (view.getId() == R.id.capture_image) {
////            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
////            if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
////                DisplayAlert("Unblock" + " " + mReceiverName + " " + "to send message?");
////            } else {
////                Intent i = new Intent(ChatViewActivity.this, ImagecaptionActivity.class);
////                i.putExtra("phoneno", mReceiverName);
////                i.putExtra("from", "Camera");
////                startActivityForResult(i, CAMERA_REQUEST);
////            }
////        }
////        else if (view.getId() == R.id.emojiButton) {
////            emojIcon.ShowEmojIcon();
////        }
//        else if (view.getId() == R.id.iBtnScroll) {
//            if (mChatData.size() > 0)
//                recyclerView_chat.scrollToPosition(mChatData.size() - 1);
//            iBtnScroll.setVisibility(View.GONE);
//            unreadcount.setVisibility(View.GONE);
//        } else if (view.getId() == R.id.ivWebLinkClose) {
//            if (rlWebLink.getVisibility() == View.VISIBLE) {
//                rlWebLink.setVisibility(View.GONE);
//            }
//        } else if (view.getId() == R.id.report_spam) {
//            performreportspam();
//        } else if (view.getId() == R.id.block_contact) {
//            performMenuBlock();
//        } else if (view.getId() == R.id.add_contact) {
//            addnewcontact(receiverMsisdn);
//        } else if (view.getId() == R.id.tvAddToContact) {
//            Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
//            intent.putExtra(INTENT_KEY_FINISH_ACTIVITY_ON_SAVE_COMPLETED, true);
//            intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
//            intent.putExtra(ContactsContract.Intents.Insert.PHONE, receiverNameText.getText().toString());
//            startActivityForResult(intent, ADD_CONTACT);
//        } else if (view.getId() == R.id.tvBlock) {
//            performMenuBlock();
//        }
//        //-----------------------attachment Choose------------------------------
//        else if (view.getId() == R.id.image_choose) {
//            hideRevealView();
//            Intent i = new Intent(ChatViewActivityRealcopy.this, ImagecaptionActivity.class);
//            i.putExtra("phoneno", mReceiverName);
//            i.putExtra("from", "Gallary");
//            i.putExtra("group", "value");
//            startActivityForResult(i, RESULT_LOAD_IMAGE);
//        } else if (view.getId() == R.id.document_choose) {
//            hideRevealView();
//            FilePickerBuilder.getInstance().setMaxCount(1)
//                    .setSelectedFiles(new ArrayList<String>())
//                    .setActivityTheme(R.style.AppTheme)
//                    .pickDocument(ChatViewActivityRealcopy.this);
//
//        } else if (view.getId() == R.id.video_choose) {
//            hideRevealView();
//            Intent i = new Intent(ChatViewActivityRealcopy.this, ImagecaptionActivity.class);
//            i.putExtra("phoneno", mReceiverName);
//            i.putExtra("from", "Video");
//            i.putExtra("group", "value");
//            startActivityForResult(i, RESULT_LOAD_VIDEO);
//        } else if (view.getId() == R.id.location_choose) {
//            hideRevealView();
//   /*         GPSTracker gps = new GPSTracker(getApplicationContext());
//            if (gps.canGetLocation()) {
//*/
//            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
//            try {
//                startActivityForResult(builder.build(ChatViewActivityRealcopy.this), RESULT_SHARE_LOCATION);
//            } catch (GooglePlayServicesRepairableException e) {
//                e.printStackTrace();
//            } catch (GooglePlayServicesNotAvailableException e) {
//                e.printStackTrace();
//            }
// /*           } else {
//                View clSnackBar = findViewById(R.id.clSnackBar);
//                if (clSnackBar != null) {
//                    Snackbar snackbar = Snackbar.make(clSnackBar, "Please Enable GPS", Snackbar.LENGTH_SHORT);
//                    snackbar.show();
//                    View view2 = snackbar.getView();
//                    TextView txtv = (TextView) view2.findViewById(android.support.design.R.id.snackbar_text);
//                    txtv.setGravity(Gravity.CENTER_HORIZONTAL);
//                }
//            }*/
//        } else if (view.getId() == R.id.audio_choose) {
//            hideRevealView();
//            Intent audioIntent = new Intent(ChatViewActivityRealcopy.this, AudioFilesListActivity.class);
//            startActivityForResult(audioIntent, REQUEST_SELECT_AUDIO);
//        } else if (view.getId() == R.id.contact_choose) {
//            hideRevealView();
//            Intent intentContact = new Intent(ChatViewActivityRealcopy.this, SendContact.class);
//            startActivityForResult(intentContact, REQUEST_CODE_CONTACTS);
//        } else if (view.getId() == R.id.camera_choose) {
//            hideRevealView();
//            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//            if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
//                DisplayAlert("Unblock" + " " + mReceiverName + " " + "to send message?");
//            } else {
//                Intent i = new Intent(ChatViewActivityRealcopy.this, ImagecaptionActivity.class);
//                i.putExtra("phoneno", mReceiverName);
//                i.putExtra("from", "Camera");
//                startActivityForResult(i, CAMERA_REQUEST);
//            }
//        }
//    }
//
//    private void hideRevealView() {
//        if (mRevealView.getVisibility() == View.VISIBLE) {
//            mRevealView.setVisibility(View.GONE);
//            hidden = true;
//        }
//    }
//
//
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        try {
//            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//            MuteStatusPojo muteData = null;
//            if (isGroupChat) {
//                //chatMenu.findItem(R.id.menuSecretChat).setVisible(false);
//                muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, null, mGroupId, false);
//            } else {
//                muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, mReceiverId, mConvId, false);
//            }
//
//            if (muteData != null && muteData.getMuteStatus().equals("1")) {
//                chatMenu.findItem(R.id.menuMute).setTitle("Unmute");
//            } else {
//                chatMenu.findItem(R.id.menuMute).setTitle("Mute");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return super.onPrepareOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        chatMenu = menu;
//
//        if (!isBroadcast) {
//            getMenuInflater().inflate(R.menu.activity_chat_view, menu);
//        }
//
//        return super.onCreateOptionsMenu(chatMenu);
//
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//        switch (item.getItemId()) {
//
//            case R.id.menuSearch:
//                performMenuSearch();
//                break;
///*
//            case R.id.menuSecretChat:
//                startSecretChat();
//                break;*/
//
//
//            case R.id.menuMute:
//                performMenuMute();
//                break;
//
//            case R.id.menuWallpaper:
//                performMenuWallpaper();
//                break;
//
//            case R.id.menuBlock:
//                performMenuBlock();
//                break;
//
//            case R.id.menuClearChat:
//                performMenuClearChat();
//                break;
//
//            case R.id.menuEmailChat:
//                performMenuEmailChat();
//                break;
//
//            case R.id.menuAddShortcut:
//                addShortcut();
//
//                break;
//
//            case R.id.menuMore:
////                showMoreMenu();
//                openMenu();
//                break;
//
//            case R.id.menuChatLock:
//                /*Intent intent = new Intent(ChatViewActivity.this, EmailSettings.class);
//                startActivity(intent);*/
//                performChatlock();
//                break;
//        }
//
//        return true;
//    }
//
//
//
//    private void updateProfileImage(ReceviceMessageEvent event) {
//        Object[] array = event.getObjectsArray();
//        try {
//            JSONObject objects = new JSONObject(array[0].toString());
//            String from = objects.getString("from");
//            String type = objects.getString("type");
//
//            if (type.equalsIgnoreCase("single") && to.equalsIgnoreCase(from)) {
//                String path = objects.getString("file") + "?id=" + Calendar.getInstance().getTimeInMillis();
//                profilepicupdation();
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "updateProfileImage: ", e);
//        }
//    }
//
//    @Override
//    public void onOptionsMenuClosed(Menu menu) {
//        isMenuBtnClick = false;
//        super.onOptionsMenuClosed(menu);
//    }
//
//    private void openMenu() {
//
//        View popupView = findViewById(R.id.popup);
//
//        if (isMenuBtnClick) {
//            List<MultiTextDialogPojo> labelsList = new ArrayList<>();
//            String strBlock, strChatlock;
//            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//            if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
//                strBlock = "Unblock";
//            } else {
//                strBlock = "Block";
//            }
//
//
//            MultiTextDialogPojo pojo = new MultiTextDialogPojo();
//            if (!isGroupChat) {
//                pojo.setLabelText(strBlock);
//                labelsList.add(pojo);
//            }
//
//            pojo = new MultiTextDialogPojo();
//            pojo.setLabelText("Clear Chat");
//            labelsList.add(pojo);
//
//            pojo = new MultiTextDialogPojo();
//            pojo.setLabelText("Email Chat");
//            labelsList.add(pojo);
//            pojo = new MultiTextDialogPojo();
//            pojo.setLabelText("Add Shortcut");
//            labelsList.add(pojo);
//
//            if (sessionManager.getLockChatEnabled().equals("1")) {
//                if (!checkIsAlreadyLocked(docId)) {
//                    strChatlock = "Lock Chat";
//                } else {
//                    strChatlock = "Unlock Chat";
//                }
//                pojo = new MultiTextDialogPojo();
//                pojo.setLabelText(strChatlock);
//                labelsList.add(pojo);
//            }
//
//            CustomMultiTextItemsDialog dialog = new CustomMultiTextItemsDialog();
//            dialog.setLabelsList(labelsList);
//            dialog.setDialogItemClickListener(new CustomMultiTextItemsDialog.DialogItemClickListener() {
//                @Override
//                public void onDialogItemClick(int position) {
//                    switch (position) {
//
//                        case 0:
//                            performMenuBlock();
//                            break;
//
//                        case 1:
//                            performMenuClearChat();
//                            break;
//
//                        case 2:
//                            performMenuEmailChat();
//                            break;
//
//                        case 3:
//                            addShortcut();
//                            break;
//
//                        case 4:
//                            performChatlock();
//                            break;
//
//                    }
//                }
//            });
//            dialog.show(getSupportFragmentManager(), "custom multi");
//        } else {
//            PopupMenu popup = new PopupMenu(this, popupView);
//            MenuInflater inflater = popup.getMenuInflater();
//            inflater.inflate(R.menu.activity_chat_view, popup.getMenu());
//            popup.getMenu().findItem(R.id.menuSearch).setVisible(false);
//            popup.getMenu().findItem(R.id.menuMute).setVisible(false);
//            popup.getMenu().findItem(R.id.menuWallpaper).setVisible(false);
//            popup.getMenu().findItem(R.id.menuMore).setVisible(false);
//            if (!isGroupChat) {
//                popup.getMenu().findItem(R.id.menuBlock).setVisible(true);
//            }
//            popup.getMenu().findItem(R.id.menuClearChat).setVisible(true);
//            //    popup.getMenu().findItem(R.id.menuEmailChat).setVisible(true);
//            //    popup.getMenu().findItem(R.id.menuAddShortcut).setVisible(true);
//
//            if (sessionManager.getLockChatEnabled().equals("1")) {
//                popup.getMenu().findItem(R.id.menuChatLock).setVisible(true);
//
//                if (!checkIsAlreadyLocked(docId)) {
//                    popup.getMenu().findItem(R.id.menuChatLock).setTitle("Lock Chat");
//                } else {
//                    popup.getMenu().findItem(R.id.menuChatLock).setTitle("Unlock Chat");
//                }
//            } else {
//                popup.getMenu().findItem(R.id.menuChatLock).setVisible(false);
//            }
//
//
//            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//            if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
//                popup.getMenu().findItem(R.id.menuBlock).setTitle("Unblock");
//            } else {
//                popup.getMenu().findItem(R.id.menuBlock).setTitle("Block");
//            }
//
//
//            if (!checkIsAlreadyLocked(docId)) {
//                popup.getMenu().findItem(R.id.menuChatLock).setTitle("Lock Chat");
//            } else {
//                popup.getMenu().findItem(R.id.menuChatLock).setTitle("Unlock Chat");
//            }
//
//            popup.getMenu().findItem(R.id.menuBlock).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem menuItem) {
//                    performMenuBlock();
//                    return false;
//                }
//            });
//
//            popup.getMenu().findItem(R.id.menuEmailChat).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem menuItem) {
//                    performMenuEmailChat();
//                    return false;
//                }
//            });
//
//            popup.getMenu().findItem(R.id.menuClearChat).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem menuItem) {
//                    performMenuClearChat();
//                    return false;
//                }
//            });
//
//            popup.getMenu().findItem(R.id.menuAddShortcut).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem menuItem) {
//                    addShortcut();
//                    return false;
//                }
//            });
//
//
//            popup.getMenu().findItem(R.id.menuChatLock).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
//                @Override
//                public boolean onMenuItemClick(MenuItem menuItem) {
//                    performChatlock();
//                    return false;
//                }
//            });
//
//            popup.show();
//        }
//    }
//
//    private void performChatlock() {
//        if (sessionManager.getLockChatEnabled().equals("1")) {
//            emailChatlock = sessionManager.getUserEmailId();
//            recemailChatlock = sessionManager.getRecoveryEMailId();
//            recPhoneChatlock = sessionManager.getRecoveryPhoneNo();
//            String emailVerifyStatus = sessionManager.getChatLockEmailIdVerifyStatus();
//
//            if (!emailChatlock.equals("") && !recemailChatlock.equals("") && !recPhoneChatlock.equals("")
//                    && emailVerifyStatus.equalsIgnoreCase("yes")) {
//
//                final String docId;
//                if (isGroupChat) {
//                    docId = mCurrentUserId + "-" + to + "-g";
//                } else {
//                    docId = mCurrentUserId + "-" + to;
//                }
//                String convId = userInfoSession.getChatConvId(docId);
//                String receiverId = userInfoSession.getReceiverIdByConvId(convId);
//                ChatLockPojo lockPojo = db.getChatLockData(receiverId, chatType);
//
//                if (lockPojo != null) {
//                    String stat = lockPojo.getStatus();
//                    String pwd = lockPojo.getPassword();
//                    if (stat.equals("0")) {
//                        if (ConnectivityInfo.isInternetConnected(ChatViewActivityRealcopy.this)) {
//                            openChatLockDialog(docId);
//                        } else {
//                            showInternetAlert();
//                        }
//                    } else {
//                        openUnlockChatDialog(docId, stat, pwd);
//                    }
//                } else {
//                    if (ConnectivityInfo.isInternetConnected(ChatViewActivityRealcopy.this)) {
//                        openChatLockDialog(docId);
//                    } else {
//                        showInternetAlert();
//                    }
//                }
//
//            } else {
//                Intent intent = new Intent(ChatViewActivityRealcopy.this, EmailSettings.class);
//                startActivity(intent);
//            }
//        }
//    }
//
//    private void showInternetAlert() {
//        final CustomAlertDialog dialog = new CustomAlertDialog();
//        dialog.setMessage("Check Your Internet Connection");
//        dialog.setNegativeButtonText("cancel");
//        dialog.setPositiveButtonText("ok");
//        dialog.setCancelable(false);
//        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//            @Override
//            public void onPositiveButtonClick() {
//                dialog.dismiss();
//            }
//
//            @Override
//            public void onNegativeButtonClick() {
//                dialog.dismiss();
//
//            }
//        });
//        dialog.show(getSupportFragmentManager(), "Delete member alert");
//    }
//
//    private void openChatLockDialog(String docId) {
//        convId = userInfoSession.getChatConvId(docId);
//        ChatLockPwdDialog dialog = new ChatLockPwdDialog();
//        dialog.setTextLabel1("New password");
//        dialog.setTextLabel2("Confirm password");
//        dialog.setEditTextdata("Enter new Password");
//        dialog.setEditTextdata2("Enter Confirm Password");
//        dialog.setHeader("Set chat password");
//        dialog.setButtonText("Lock");
//        Bundle bundle = new Bundle();
//        bundle.putString("from", mCurrentUserId);
//        if (docId.contains("-g")) {
//            bundle.putString("type", "group");
//        } else {
//            bundle.putString("type", "single");
//        }
//        bundle.putString("page", "chatview");
//        if (docId.contains("-g")) {
//            bundle.putString("convID", to);
//        } else {
//            bundle.putString("convID", convId);
//        }
//
//        bundle.putString("status", "0");
//        dialog.setArguments(bundle);
//        dialog.show(getSupportFragmentManager(), "chatLock");
//    }
//
//
//    private void openUnlockChatDialog(String docId, String status, String pwd) {
//        String convId = userInfoSession.getChatConvId(docId);
//        ChatLockPwdDialog dialog = new ChatLockPwdDialog();
//        dialog.setTextLabel1("Enter your Password");
//        dialog.setEditTextdata("Enter new Password");
//        dialog.setforgotpwdlabel("Forgot Password");
//        dialog.setHeader("Unlock Chat");
//        dialog.setButtonText("Unlock");
//        Bundle bundle = new Bundle();
//        if (docId.contains("-g")) {
//            bundle.putString("convID", to);
//        } else {
//            bundle.putString("convID", convId);
//        }
//        bundle.putString("status", "1");
//        bundle.putString("pwd", pwd);
//        bundle.putString("page", "chatview");
//        if (docId.contains("-g")) {
//            bundle.putString("type", "group");
//        } else {
//            bundle.putString("type", "single");
//        }
//        bundle.putString("from", mCurrentUserId);
//        dialog.setArguments(bundle);
//        dialog.show(getSupportFragmentManager(), "chatunLock");
//    }
//
//
//    private void performreportspam() {
//        new AlertDialog.Builder(context)
//                .setMessage("Are you sure you want to Report this message?")
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        SendMessageEvent messageEvent = new SendMessageEvent();
//                        messageEvent.setEventName(SocketManager.EVENT_REPORT_SPAM_USER);
//                        try {
//                            JSONObject myobj = new JSONObject();
//                            myobj.put("from", from);
//                            myobj.put("to", to);
//
//                            messageEvent.setMessageObject(myobj);
//                            EventBus.getDefault().post(messageEvent);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                })
//
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
//        /*int permissionCheck = ContextCompat.checkSelfPermission(thisActivity,
//                Manifest.permission.CAMERA);*/
//    }
//
//    private void sendTextMessageBroadcast() {
//        String data = sendMessage.getText().toString().trim();
//        String data_at = data;
//
//        TextMessage message = (TextMessage) MessageFactory.getMessage(MessageFactory.text, this);
//
//        message.setIDforBroadcast(mReceiverId);
//        MessageItemChat item = message.createMessageItem(true, data, MessageFactory.DELIVERY_STATUS_NOT_SENT, mReceiverId, receiverNameText.getText().toString(), isExpiry, expireTime);
//
//        item.setDeliveryStatus("1");
//        db.updateChatMessage(item, chatType);
//        mChatData.add(item);
//
//        notifyDatasetChange();
//        sendMessage.getText().clear();
//
//        forwardToBroadcast(item, chatappContactModels);
//    }
//
//    private String kyGn()
//    {
//        return getResources().getString(R.string.chatapp)+mConvId+getResources().getString(R.string.adani);
//
//        //   return  Constants.DUMMY_KEY;
//
//    }
//
//    private void sendTextMessage() {
//        if (sendMessage.getText().toString().trim().length() > 0) {
//
//            try {
//                CryptLib cryptLib = new CryptLib();
//                String data = cryptLib.encryptPlainTextWithRandomIV(sendMessage.getText().toString().trim(), kyGn());
//
//
//                //      String data = edAlgorithm.enc(sendMessage.getText().toString().trim());
//
//                // String data = sendMessage.getText().toString().trim();
//                String data_at = data;
//
//                if (at_memberlist != null && at_memberlist.size() >= 0)
//                    for (GroupMembersPojo groupMembersPojo : at_memberlist) {
//                        String userName = "@" + groupMembersPojo.getContactName();
//
//                        data_at = data_at.replace(userName, TextMessage.TAG_KEY + groupMembersPojo.getUserId() + TextMessage.TAG_KEY);
//                    }
//
//                if (!data.equalsIgnoreCase("")) {
//                    if (session.getarchivecount() != 0) {
//                        if (session.getarchive(from + "-" + to))
//                            session.removearchive(from + "-" + to);
//                    }
//                    if (session.getarchivecountgroup() != 0) {
//                        if (session.getarchivegroup(from + "-" + to + "-g"))
//                            session.removearchivegroup(from + "-" + to + "-g");
//                    }
//                    SendMessageEvent messageEvent = new SendMessageEvent();
//                    TextMessage message = (TextMessage) MessageFactory.getMessage(MessageFactory.text, this);
//                    JSONObject msgObj;
//                    if (isGroupChat) {
//                        messageEvent.setEventName(SocketManager.EVENT_GROUP);
//                        msgObj = (JSONObject) message.getGroupMessageObject(to, data_at, receiverNameText.getText().toString());
//
//                        try {
//                            msgObj.put("groupType", SocketManager.ACTION_EVENT_GROUP_MESSAGE);
//                            msgObj.put("userName", receiverNameText.getText().toString());
//                        } catch (JSONException ex) {
//                            ex.printStackTrace();
//                        }
//                    } else {
//                        msgObj = (JSONObject) message.getMessageObject(to, data, false);
//
//                        try {
//                            msgObj.put("is_expiry", isExpiry);
//                            msgObj.put("expiry_time", expireTime);
//                        } catch (JSONException ex) {
//                            ex.printStackTrace();
//                        }
//                        messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
//                    }
//                    ///data_at
//                    ///data_at
//                    MessageItemChat item = message.createMessageItem(true, data, MessageFactory.DELIVERY_STATUS_NOT_SENT, mReceiverId, receiverNameText.getText().toString(), isExpiry, expireTime);
//                    messageEvent.setMessageObject(msgObj);
//
//                    if (isGroupChat) {
//                        if (enableGroupChat) {
//                            item.setGroupName(receiverNameText.getText().toString());
//                            //    item.setTextMessage(sendMessage.getText().toString().trim());
//                            item.setGroup(true);
//
//                            db.updateChatMessage(item, chatType);
//                            mChatData.add(item);
//                            EventBus.getDefault().post(messageEvent);
//                            notifyDatasetChange();
//                            sendMessage.getText().clear();
//                        } else {
//                            Toast.makeText(ChatViewActivityRealcopy.this, "You are not a member in this group", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        item.setSenderMsisdn(receiverMsisdn);
//                        item.setSenderName(receiverNameText.getText().toString());
//                        item.setConvId(mConvId);
//                        //   item.setTextMessage(sendMessage.getText().toString().trim());
//                        item.setGroup(false);
//
//                        mChatData.add(item);
//                        EventBus.getDefault().post(messageEvent);
//                        db.updateChatMessage(item, chatType);
//                        notifyDatasetChange();
//                        sendMessage.getText().clear();
//                    }
//
//                }
//                if (at_memberlist != null) {
//                    at_memberlist.clear();
//                }
//
//
//            } catch(Exception e){
//                e.printStackTrace();
//            }
//        }
////        isExpiry="0";
//    }
//
//    private void sendparticularmsgreply() {
//
//        try {
//            CryptLib cryptLib = new CryptLib();
//            String data = cryptLib.encryptPlainTextWithRandomIV(sendMessage.getText().toString().trim(),kyGn());
//            //            String data = sendMessage.getText().toString().trim();
//            String data_at = data;
//            if (at_memberlist != null && at_memberlist.size() >= 0)
//                for (GroupMembersPojo groupMembersPojo : at_memberlist) {
//                    String userName = "@" + groupMembersPojo.getContactName();
//
//                    data_at = data.replace(userName, TextMessage.TAG_KEY + groupMembersPojo.getUserId() + TextMessage.TAG_KEY);
//                }
//            if (!data.equalsIgnoreCase("")) {
//                if (session.getarchivecount() != 0) {
//                    if (session.getarchive(from + "-" + to))
//                        session.removearchive(from + "-" + to);
//                }
//                if (session.getarchivecountgroup() != 0) {
//                    if (session.getarchivegroup(from + "-" + to + "-g"))
//                        session.removearchivegroup(from + "-" + to + "-g");
//                }
//                SendMessageEvent messageEvent = new SendMessageEvent();
//                TextMessage message = (TextMessage) MessageFactory.getMessage(MessageFactory.text, this);
//                JSONObject msgObj;
//                if (isGroupChat) {
//                    messageEvent.setEventName(SocketManager.EVENT_GROUP);
//                    msgObj = (JSONObject) message.getGroupMessageObject(to, data_at, receiverNameText.getText().toString());
//                    try {
//                        msgObj.put("groupType", SocketManager.ACTION_EVENT_GROUP_REPlY_MESSAGE);
//                        msgObj.put("userName", receiverNameText.getText().toString());
//                        msgObj.put("recordId", mymsgid);
//                    } catch (JSONException ex) {
//                        ex.printStackTrace();
//                    }
//                } else {
//                    messageEvent.setEventName(SocketManager.EVENT_REPLY_MESSAGE);
//                    msgObj = (JSONObject) message.getMessageObject(to, data, false);
//                    try {
//                        msgObj.put("recordId", mymsgid);
//                        msgObj.put("is_expiry", isExpiry);
//                        msgObj.put("expiry_time", expireTime);
//                    } catch (JSONException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//                messageEvent.setMessageObject(msgObj);
//                MessageItemChat item = message.createMessageItem(true, data_at,
//                        MessageFactory.DELIVERY_STATUS_NOT_SENT, mReceiverId, receiverNameText.getText().toString(), isExpiry, expireTime);
//                item.setReplyType(replytype);
//                if (Integer.parseInt(replytype) == MessageFactory.text) {
//                    item.setReplyMessage(messageold);
//                } else if (Integer.parseInt(replytype) == MessageFactory.picture) {
//                    item.setreplyimagepath(imgpath);
//                    item.setreplyimagebase64(imageAsBytes);
//                } else if (Integer.parseInt(replytype) == MessageFactory.audio) {
//                    item.setReplyMessage("Audio");
//                } else if (Integer.parseInt(replytype) == MessageFactory.video) {
//                    item.setReplyMessage("Video");
//                    item.setreplyimagebase64(imageAsBytes);
//                } else if (Integer.parseInt(replytype) == MessageFactory.document) {
//                    item.setReplyMessage(messageold);
//                } else if (Integer.parseInt(replytype) == MessageFactory.web_link) {
//                    item.setReplyMessage(messageold);
//                } else if (Integer.parseInt(replytype) == MessageFactory.contact) {
//                    item.setReplyMessage(contactname);
//                } else if (Integer.parseInt(replytype) == MessageFactory.location) {
//                    if (locationName != null && !locationName.equals("")) {
//                        item.setReplyMessage(locationName);
//                    } else {
//                        item.setReplyMessage("Location");
//                    }
//                    item.setreplyimagebase64(imageAsBytes);
//                }
//
//                item.setSenderMsisdn(receiverMsisdn);
//                item.setSenderName(mReceiverName);
//                item.setReplySender(ReplySender);
//                reply = false;
//
//
//                if (isGroupChat) {
//                    if (enableGroupChat) {
//                        item.setReplyType(replytype);
//                        item.setreplyimagebase64(imageAsBytes);
//                        if (Integer.parseInt(replytype) == MessageFactory.text) {
//                            item.setReplyMessage(messageold);
//                        } else if (Integer.parseInt(replytype) == MessageFactory.picture) {
//                            item.setreplyimagepath(imgpath);
//                        } else if (Integer.parseInt(replytype) == MessageFactory.audio) {
//                            item.setReplyMessage("Audio");
//                        } else if (Integer.parseInt(replytype) == MessageFactory.document) {
//                            item.setReplyMessage(messageold);
//                        } else if (Integer.parseInt(replytype) == MessageFactory.web_link) {
//                            item.setReplyMessage(messageold);
//                        } else if (Integer.parseInt(replytype) == MessageFactory.contact) {
//                            item.setReplyMessage(contactname);
//                        }
//                        item.setGroupName(receiverNameText.getText().toString());
//                        item.setGroup(true);
//
//                        //  item.setTextMessage(sendMessage.getText().toString().trim());
//
//                        db.updateChatMessage(item, chatType);
//                        mChatData.add(item);
//                        EventBus.getDefault().post(messageEvent);
//                        notifyDatasetChange();
//                        sendMessage.getText().clear();
//                    } else {
//                        Toast.makeText(ChatViewActivityRealcopy.this, "You are not a member in this group", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//
//                    item.setSenderMsisdn(receiverMsisdn);
//                    item.setSenderName(receiverNameText.getText().toString());
//                    item.setConvId(mConvId);
//                    //  item.setTextMessage(sendMessage.getText().toString().trim());
//                    item.setGroup(false);
//                    db.updateChatMessage(item, chatType);
//                    mChatData.add(item);
//                    EventBus.getDefault().post(messageEvent);
//                    notifyDatasetChange();
//                    sendMessage.getText().clear();
//                }
//
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    private void goInfoPage() {
//
//        showUnSelectedActions();
//        selectedChatItems.clear();
//
//        if (isGroupChat) {
//            Intent infoIntent = new Intent(ChatViewActivityRealcopy.this, GroupInfo.class);
//            infoIntent.putExtra("GroupId", mGroupId);
//            infoIntent.putExtra("GroupName", receiverNameText.getText().toString());
//            startActivity(infoIntent);
//        } else if (isBroadcast) {
//            Intent infoIntent = new Intent(ChatViewActivityRealcopy.this, GroupInfo.class);
//            infoIntent.putExtra("GroupId", receiverUid);
//            infoIntent.putExtra("GroupName", receiverNameText.getText().toString());
//            infoIntent.putExtra("isbroadcast", true);
//            startActivity(infoIntent);
//
//        } else {
//            Intent infoIntent = new Intent(ChatViewActivityRealcopy.this, UserInfo.class);
//            infoIntent.putExtra("UserId", to);
//            infoIntent.putExtra("UserName", receiverNameText.getText().toString());
//            infoIntent.putExtra("UserAvatar", receiverAvatar);
//            infoIntent.putExtra("UserNumber", receiverMsisdn);
//            infoIntent.putExtra("FromSecretChat", false);
//            startActivityForResult(infoIntent, MUTE_ACTIVITY);
//        }
//    }
//
//    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
//        iconToBeChanged.setImageResource(drawableResourceId);
//    }
//
//    private void notifyDatasetChange() {
//
//        recyclerView_chat.post(new Runnable() {
//            @Override
//            public void run() {
//                mAdapter.setIsGroupChat(isGroupChat);
//                mAdapter.notifyDataSetChanged();
//                if (mChatData.size() > 0)
//                    recyclerView_chat.scrollToPosition(mChatData.size() - 1);
//            }
//        });
//
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(ReceviceMessageEvent event) {
//        //Toast.makeText(this, "onMessageEvent " + event.getEventName(), Toast.LENGTH_SHORT).show();
//        if (!isGroupChat) {
//            if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MESSAGE_RES)) {
//                loadMessageRes(event);
//            } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_PRIVACY_SETTINGS)) {
//                loadPrivacySetting(event);
//            } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MESSAGE)) {
//                loadMessage(event);
//            } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MESSAGE_STATUS_UPDATE)) {
//                loadMessageStatusupdate(event);
//            } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GET_MESSAGE)) {
//                loadMessage(event);
//            } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_BLOCK_USER)) {
//                blockunblockcontact(event);
//            } else if(event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MESSAGE_ACK)) {
//                loadackMesssage(event);
//            }
//            else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_REPORT_SPAM_USER)) {
//                try {
//                    Object[] obj = event.getObjectsArray();
//                    JSONObject object = new JSONObject(obj[0].toString());
//                    Toast.makeText(this, "Contact is reported as spam ", Toast.LENGTH_LONG).show();
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            } else if (event.getEventName().equalsIgnoreCase(Socket.EVENT_CONNECT)) {
//                if (!isLastSeenCalled) {
//                    getReceiverOnlineTimeStatus();
//                }
//            } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CALL_RESPONSE)) {
//                loadCallResMessage(event);
//            }
//        } else {
//
//            if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP)) {
//
//                try {
//                    Object[] obj = event.getObjectsArray();
//                    JSONObject object = new JSONObject(obj[0].toString());
//                    String groupAction = object.getString("groupType");
//
//                    if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EVENT_GROUP_MESSAGE)) {
//                        handleGroupMessage(event);
//                    } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_ACK_GROUP_MESSAGE)) {
////                    Toast.makeText(ChatViewActivity.this, "".concat(String.valueOf(object)), Toast.LENGTH_LONG).show();
//                        createUpdateGroupMsgStatus(object);
//                    } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_CHANGE_GROUP_NAME)) {
//                        loadChangeGroupNameMessage(object);
//                    } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EXIT_GROUP)) {
//                        loadExitMessage(object);
//                    } else if (groupAction.equals(SocketManager.ACTION_CHANGE_GROUP_DP)) {
//                        performGroupChangeDp(object);
//                    } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_DELETE_GROUP_MEMBER)) {
//                        loadDeleteMemberMessage(object);
//                    } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_ADD_GROUP_MEMBER)) {
//                        loadAddMemberMessage(object);
//                    } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_MAKE_GROUP_ADMIN)) {
//                        loadMakeAdminMessage(object);
//                    } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EVENT_GROUP_MSG_DELETE)) {
//                        deleteGroupMessage(event);
//                    } else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EVENT_GROUP_OFFLINE)) {
//                        getGroupOffline(event);
//                    }
//                    else if (groupAction.equalsIgnoreCase(SocketManager.ACTION_JOIN_NEW_GROUP)) {
//
//
//                        Integer type = Integer.parseInt(object.getString("type"));
//                        if(type==MessageFactory.add_group_member)
//                        {
//                            addNewGroupData(object);
//                        }
//                    }
//
//
//                } catch (JSONException ex) {
//                    ex.printStackTrace();
//                }
//            } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP_DETAILS)) {
//                loadGroupDetails(event);
//            }
//        }
//
//        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CLEAR_CHAT)) {
//            Object[] obj = event.getObjectsArray();
//            try {
//                JSONObject jsonObject=new JSONObject(obj[0].toString());
//                load_clear_chat(jsonObject);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//
//        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_TYPING)) {
//            loadTypingStatus(event);
//        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CAHNGE_ONLINE_STATUS)) {
//            loadOnlineStatus(event);
//        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GET_CURRENT_TIME_STATUS)) {
//            loadCurrentTimeMessage(event);
//        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_STAR_MESSAGE)) {
//            loadStarredMessage(event);
//        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_REMOVE_MESSAGE)) {
//            loadDeleteMessage(event);
//        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_FILE_RECEIVED)) {
//            try {
//                Object[] obj = event.getObjectsArray();
//                JSONObject object = new JSONObject(obj[0].toString());
//                new loadFileUploaded(object).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_START_FILE_DOWNLOAD)) {
//            try {
//                Object[] response = event.getObjectsArray();
//                JSONObject jsonObject = (JSONObject) response[1];
//                writeBufferToFile(jsonObject);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MUTE)) {
//            Object[] response = event.getObjectsArray();
//            loadMuteMessage(response[0].toString());
//        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GET_MESSAGE_DETAILS)) {
//            loadReplyMessageDetails(event.getObjectsArray()[0].toString());
//        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_DELETE_MESSAGE)) {
//            deleteSingleMessage(event);
//        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_SINGLE_OFFLINE_MSG)) {
//            getSingleOffline(event);
//        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_IMAGE_UPLOAD)) {
//            updateProfileImage(event);
//        }
//        else if(event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GET_MESSAGE_INFO))
//        {
//            Object[] response = event.getObjectsArray();
//            loadMessageOfflineAcks(response[0].toString());
//        }
//        }
//
//
//    private void loadMessageOfflineAcks(String data) {
//
//        try {
//            JSONObject object = new JSONObject(data);
//            String err = object.getString("err");
//
//            if (err.equals("0")) {
//
//                String chatType = object.getString("type");
//                JSONArray arrMessages = object.getJSONArray("messageDetails");
//                if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_SINGLE)) {
//                    updateSingleMessageOfflineAcks(arrMessages);
//                } else if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
//                    updateGroupMessageOfflineAcks(arrMessages);
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void updateSingleMessageOfflineAcks(JSONArray arrMessages) {
//        MessageDbController db = CoreController.getDBInstance(this);
//
//        try {
//            for (int i = 0; i < arrMessages.length(); i++) {
//                JSONObject msgObj = arrMessages.getJSONObject(i);
//
//                String from = msgObj.getString("from");
//                if (from.equalsIgnoreCase(mCurrentUserId)) {
//                    String to = msgObj.getString("to");
//                    String msgId = msgObj.getString("msgId");
//                    String chatId = msgObj.getString("doc_id");
//                    String timeStamp = msgObj.getString("timestamp");
//                    String deliverStatus = msgObj.getString("message_status");
//                    String deliverTime = msgObj.getString("time_to_deliever");
//                    String readTime = msgObj.getString("time_to_seen");
//
////                    String docId = chatId;
//                    updateSingleChatList(chatId,deliverStatus);
//
//
////                    if (deliverStatus.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
////                        updateChatList(docId,deliverStatus);
////
//////                        db.updateChatMessage(docId, chatId, MessageFactory.DELIVERY_STATUS_DELIVERED, deliverTime, true);
//////                        db.updateChatMessage(docId, chatId, deliverStatus, readTime, true);
////                    } else {
////                        db.updateChatMessage(docId, chatId, MessageFactory.DELIVERY_STATUS_DELIVERED, deliverTime, true);
////                    }
//
//                }
//
//            }
//            mAdapter.notifyDataSetChanged();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void updateSingleChatList(String offlineDocid,String deliverStatus)
//    {
//        for (int i = 0; i < mChatData.size(); i++) {
//            MessageItemChat items = mChatData.get(i);
//            if (items != null && items.getMessageId().equalsIgnoreCase(offlineDocid)) {
//                items.setDeliveryStatus("" + deliverStatus);
//                mChatData.get(i).setDeliveryStatus(deliverStatus);
//                }
//
//            if (mChatData.get(i).getDeliveryStatus().equals("3")) {
//                if(mChatData.get(i).getIsExpiry()!=null) {
//                    if (mChatData.get(i).getIsExpiry().equals("1")) {
//                        if (isExpiry.equals("1") && !onTimeriD.contains(mChatData.get(i).getMessageId())) {
//
//                            onTimeriD.add(mChatData.get(i).getMessageId());
//                            long timer = Long.parseLong(mChatData.get(i).getExpiryTime()) * 1000;
//                            onTimer(timer, mChatData.get(i));
//                        }
//                    }
//                }
//            }
//        }
//        }
//
//
//    private void updateGroupMessageOfflineAcks(JSONArray arrMessages) {
//
//        try {
//            MessageDbController db = CoreController.getDBInstance(this);
//
//            for (int i = 0; i < arrMessages.length(); i++) {
//                JSONObject msgObj = arrMessages.getJSONObject(i);
//
//                String from = msgObj.getString("from");
//                if (from.equalsIgnoreCase(mCurrentUserId)) {
//                    String msgId = msgObj.getString("msgId");
//                    String chatId = msgObj.getString("doc_id");
//                    String timeStamp = msgObj.getString("timestamp");
//
//                    JSONArray arrMsgStatus = msgObj.getJSONArray("message_status");
//
//                    for (int j = 0; j < arrMsgStatus.length(); j++) {
//                        JSONObject statusObj = arrMsgStatus.getJSONObject(j);
//                        String ackUserId = statusObj.getString("id");
//                        String deliverStatus = statusObj.getString("status");
//                        String deliverTime = statusObj.getString("time_to_deliever");
//                        String readTime = statusObj.getString("time_to_seen");
//
//                        String groupId = chatId.split("-")[1];
//                        String docId = from.concat("-").concat(groupId).concat("-g");
//
//                        if (deliverStatus.equalsIgnoreCase(MessageFactory.GROUP_MSG_READ_ACK)) {
//                            updateGroupMsgStatus(docId, chatId, MessageFactory.GROUP_MSG_DELIVER_ACK,
//                                    deliverTime, ackUserId, mCurrentUserId);
//                            updateGroupMsgStatus(docId, chatId, deliverStatus, readTime,
//                                    ackUserId, mCurrentUserId);
//                        } else {
//                            synchronized (this) {
//                                updateGroupMsgStatus(docId, chatId, deliverStatus, deliverTime,
//                                        ackUserId, mCurrentUserId);
//                            }
//
//                        }
//                    }
//                }
//                }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void deleteSingleMessage(ReceviceMessageEvent event) {
//        Object[] array = event.getObjectsArray();
//        try {
//            JSONObject objects = new JSONObject(array[0].toString());
//            int errorState = objects.getInt("err");
//            if (errorState == 0) {
//                String fromId = objects.getString("from");
//                String docId, msgId, type, recId, lastMsg_Status, convId;
////                String deleteStatus = objects.getString("status");
//                String chat_id = (String) objects.get("doc_id");
//                String[] ids = chat_id.split("-");
//                type = objects.getString("type");
////                recId = objects.getString("recordId");
////                convId = objects.getString("convId");
//                docId = ids[1] + "-" + ids[0];
//                msgId = docId + "-" + ids[2];
//
//                if (fromId.equalsIgnoreCase(from)) {
//                    for (MessageItemChat items : mChatData) {
//                        if (items != null && items.getMessageId().equalsIgnoreCase(chat_id)) {
//                            int index = mChatData.indexOf(items);
//                            if (index > -1 && mChatData.get(index).isMediaPlaying()) {
//                                mAdapter.stopAudioOnMessageDelete(index);
//                            }
//
//                            mChatData.get(index).setMessageType(MessageFactory.DELETE_SELF + "");
//                            mChatData.get(index).setIsSelf(true);
//                            mAdapter.notifyDataSetChanged();
//                            db.deleteSingleMessage(docId, items.getMessageId(), chatType, "self");
//                            db.deleteChatListPage(docId, items.getMessageId(), chatType, "self");
//
//                            break;
//                        }
//                    }
//                }
//
//                if (!fromId.equalsIgnoreCase(from) && fromId.equalsIgnoreCase(Chat_to)) {
//
//                    for (MessageItemChat items : mChatData) {
//                        if (items != null && items.getMessageId().equalsIgnoreCase(msgId)) {
//                            int index = mChatData.indexOf(items);
//                            if (index > -1 && mChatData.get(index).isMediaPlaying()) {
//                                mAdapter.stopAudioOnMessageDelete(index);
//                            }
//
//                            mChatData.get(index).setMessageType(MessageFactory.DELETE_OTHER + "");
//                            mChatData.get(index).setIsSelf(false);
//
//                            mAdapter.notifyDataSetChanged();
//
//                            db.deleteSingleMessage(docId, msgId, type, "other");
//                            db.deleteChatListPage(docId, msgId, type, "other");
//
//                            break;
//                        }
//                    }
//                }
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void deleteGroupMessage(ReceviceMessageEvent event) {
//        Object[] array = event.getObjectsArray();
//        try {
//            JSONObject objects = new JSONObject(array[0].toString());
//
//            String fromId = objects.getString("from");
////            String deleteStatus = objects.getString("status");
//            String chat_id = (String) objects.get("doc_id");
//            String[] ids = chat_id.split("-");
//
//            String docId, msgId, type, recId, convId;
//
//            type = objects.getString("type");
////            recId = objects.getString("recordId");
////            convId = objects.getString("convId");
//            docId = fromId + "-" + ids[1] + "-g";
//            msgId = docId + "-" + ids[3];
//            String groupAndMsgId = ids[1] + "-g-" + ids[3];
//
//
//            if (fromId.equalsIgnoreCase(from)) {
//                if (mChatData.size() > 0) {
//                    for (int i = 0; i < mChatData.size(); i++) {
//                        if (mChatData.get(i).getMessageId().contains(chat_id)) {
//                            if (i > -1 && mChatData.get(i).isMediaPlaying()) {
//                                mAdapter.stopAudioOnMessageDelete(i);
//                            }
//
//                            mChatData.get(i).setMessageType(MessageFactory.DELETE_SELF + "");
//                            mChatData.get(i).setIsSelf(true);
//
//                            String[] from_ids = chat_id.split("-");
//                            String from_groupAndMsgId = from_ids[1] + "-g-" + from_ids[3];
//
//                            mAdapter.notifyDataSetChanged();
//                            db.deleteSingleMessage(from_groupAndMsgId, chat_id, chatType, "self");
//                            db.deleteChatListPage(from_groupAndMsgId, chat_id, chatType, "self");
//
//                            break;
//                        }
//                    }
//                }
//            }
//
//
//            if (!fromId.equalsIgnoreCase(from)) {
//                if (mChatData.size() > 0) {
//                    for (int i = 0; i < mChatData.size(); i++) {
//                        if (mChatData.get(i).getMessageId().contains(groupAndMsgId)) {
//                            if (i > -1 && mChatData.get(i).isMediaPlaying()) {
//                                mAdapter.stopAudioOnMessageDelete(i);
//                            }
//
//                            mChatData.get(i).setMessageType(MessageFactory.DELETE_OTHER + "");
//                            mChatData.get(i).setIsSelf(false);
//
//                            mAdapter.notifyDataSetChanged();
//
//                            db.deleteSingleMessage(groupAndMsgId, msgId, type, "other");
//                            db.deleteChatListPage(groupAndMsgId, msgId, type, "other");
//
//                            break;
//                        }
//                    }
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void getSingleOffline(ReceviceMessageEvent event) {
//        Object[] array = event.getObjectsArray();
//        try {
//            JSONObject objects = new JSONObject(array[0].toString());
//            int is_everyone = objects.getInt("is_deleted_everyone");
//            if (is_everyone == 1) {
//                String fromId = objects.getString("from");
//                if (!fromId.equalsIgnoreCase(mCurrentUserId)) {
//                    String chat_id = objects.getString("docId");
//                    String[] ids = chat_id.split("-");
//                    String docId, msgId, chat_type, recId, lastMsg_Status, convId, deleteStatus;
//
//                    chat_type = objects.getString("chat_type");
////                    recId = objects.getString("recordId");
////                    convId = objects.getString("convId");
////                    deleteStatus = objects.getString("is_deleted_everyone");
//
//                    docId = ids[1] + "-" + ids[0];
//                    msgId = docId + "-" + ids[2];
//
//                    if (mChatData.size() > 0) {
//                        for (int i = 0; i < mChatData.size(); i++) {
//                            if (mChatData.get(i).getMessageId().equalsIgnoreCase(msgId)) {
//                                if (i > -1 && mChatData.get(i).isMediaPlaying()) {
//                                    mAdapter.stopAudioOnMessageDelete(i);
//                                }
//
//                                mChatData.get(i).setMessageType(MessageFactory.DELETE_OTHER + "");
//                                mChatData.get(i).setIsSelf(false);
//
//                                mAdapter.notifyDataSetChanged();
//
//                                db.deleteSingleMessage(docId, msgId, chat_type, "other");
//                                db.deleteChatListPage(docId, msgId, chat_type, "other");
//
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void getGroupOffline(ReceviceMessageEvent event) {
//        Object[] array = event.getObjectsArray();
//        try {
//            JSONObject objects = new JSONObject(array[0].toString());
//            String status = objects.getString("err");
//            String fromID, toDocId, type;
//            if (status.equalsIgnoreCase("0")) {
//                int groupType = objects.getInt("groupType");
//                if (groupType == 20) {
//                    int is_deleted_everyone = objects.getInt("is_deleted_everyone");
//                    if (is_deleted_everyone == 1) {
//                        fromID = objects.getString("from");
//                        toDocId = objects.getString("toDocId");
//                        type = objects.getString("groupName");
//                        String[] ids = toDocId.split("-");
//
//
//                        String groupAndMsgId = ids[1] + "-g-" + ids[3];
//
//
//                        if (!fromID.equalsIgnoreCase(from)) {
//                            if (mChatData.size() > 0) {
//                                for (int i = 0; i < mChatData.size(); i++) {
//                                    if (mChatData.get(i).getMessageId().contains(groupAndMsgId)) {
//                                        if (i > -1 && mChatData.get(i).isMediaPlaying()) {
//                                            mAdapter.stopAudioOnMessageDelete(i);
//                                        }
//
//                                        mChatData.get(i).setMessageType(MessageFactory.DELETE_OTHER + "");
//                                        mChatData.get(i).setIsSelf(false);
//
//                                        mAdapter.notifyDataSetChanged();
//
//                                        db.deleteSingleMessage(groupAndMsgId, toDocId, type, "other");
//                                        db.deleteChatListPage(groupAndMsgId, toDocId, type, "other");
//
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void loadReplyMessageDetails(String data) {
//        try {
//            JSONObject object = new JSONObject(data);
//            String err = object.getString("err");
//            JSONObject dataObj = object.getJSONObject("data");
//            String from = dataObj.getString("from");
//            String convId = dataObj.getString("convId");
//
//            if (err.equals("0") && convId.equalsIgnoreCase(mConvId)) {
//
//                String to = dataObj.getString("to");
//                String requestMsgId = dataObj.getString("requestMsgId");
//                String chatType = dataObj.getString("type");
//
//                String docId = from + "-" + to;
//                if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
//                    docId = docId + "-g";
//                } else {
//                    String secretType = dataObj.getString("secret_type");
//                    if (secretType.equalsIgnoreCase("yes")) {
//                        docId = docId + "-secret";
//                    }
//                }
//
//                try {
//                    for (int i = 0; i < mChatData.size(); i++) {
//                        MessageItemChat msgItem = mChatData.get(i);
//
//                        if (msgItem.getMessageId().equalsIgnoreCase(requestMsgId)) {
//                            MessageItemChat modifiedItem = db.getParticularMessage(requestMsgId);
//                            modifiedItem.setSelected(msgItem.isSelected());
//                            mChatData.set(i, modifiedItem);
//                            mAdapter.notifyItemChanged(i);
//                            break;
//                        }
//                    }
//                } catch (ArrayIndexOutOfBoundsException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadGroupDetails(ReceviceMessageEvent event) {
//        try {
//            Object[] array = event.getObjectsArray();
//            JSONObject objects = new JSONObject(array[0].toString());
//
//            String groupId = objects.getString("_id");
//
//            if (groupId.equalsIgnoreCase(mGroupId)) {
//
//                allMembersList.clear();
//                savedMembersList.clear();
//                unsavedMembersList.clear();
//                JSONArray arrMembers = objects.getJSONArray("GroupUsers");
//                membersCount = arrMembers.length();
//                for (int i = 0; i < arrMembers.length(); i++) {
//                    JSONObject userObj = arrMembers.getJSONObject(i);
//                    String userId = userObj.getString("id");
//                    String active = userObj.getString("active");
//                    String isDeleted = userObj.getString("isDeleted");
//                    String msisdn = userObj.getString("msisdn");
//                    String phNumber = userObj.getString("msisdn");
//                    String name = userObj.getString("ContactName");
//                    String status = userObj.getString("Status");
//                    String userDp = userObj.getString("avatar");
//                    String adminUser = userObj.getString("isAdmin");
//                    String contactmsisdn = userObj.getString("ContactName");
//                    String isExitsContact = userObj.getString("isExitsContact");
//
//                    String contact = userObj.getString("Name");
//
//
//                    try {
//                        if (ChatappRegularExp.isEncodedBase64String(status)) {
//                            byte[] arrStatus = Base64.decode(status, Base64.DEFAULT);
//                            status = new String(arrStatus, "UTF-8");
//                        }
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                        status = "";
//                    }
//
//
//                    //   String contactName = getcontactname.getSendername(userId, msisdn);
//
//
//                    //   String contactName = getcontactname.getSendername(userId, msisdn);
//
//                    String contactName = contact;
//
//
//                    byte[] data1 = Base64.decode(contactName, Base64.DEFAULT);
//
//                    try {
//                        contactName = new String(data1, "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//
//                    if (mCurrentUserId.equalsIgnoreCase(userId)) {
//                        contactName = "You";
//                        status = SessionManager.getInstance(this).getcurrentUserstatus();
//                    }
//
//                    GroupMembersPojo data = new GroupMembersPojo();
//                    data.setUserId(userId);
//                    data.setActive(active);
//                    data.setIsDeleted(isDeleted);
//                    data.setMsisdn(msisdn);
//                    data.setPhNumber(phNumber);
//                    data.setName(name);
//                    data.setStatus(status);
//                    data.setUserDp(userDp);
//                    data.setIsAdminUser(adminUser);
//                    data.setContactName(contactName);
//
//                    if (userId.equalsIgnoreCase(mCurrentUserId)) {
//                        mCurrentUserData = data;
//                    } else {
//                        if (msisdn.equalsIgnoreCase(contactName)) {
//                            unsavedMembersList.add(data);
//                        } else {
//                            savedMembersList.add(data);
//                        }
//                    }
//
//                }
//                Collections.sort(savedMembersList, Getcontactname.groupMemberAsc);
//                Collections.sort(unsavedMembersList, Getcontactname.groupMemberAsc);
//                allMembersList.addAll(savedMembersList);
//                if (membersCount > 0)
//                    allMembersList.addAll(unsavedMembersList);
//
////                if (mCurrentUserData != null) {
////                    allMembersList.add(mCurrentUserData);
////                }
//
//                adapter.notifyDataSetChanged();
//
//            }
//        } catch (JSONException e) {
//
//        }
//    }
//
//
//    private void Activecall(boolean isVideoCall) {
//        if (ConnectivityInfo.isInternetConnected(this))
//            if (checkAudioRecordPermission()) {
//                CallMessage message = new CallMessage(ChatViewActivityRealcopy.this);
//                boolean isOutgoingCall = true;
//                int type;
//
//                if(isVideoCall)
//                {
//                    type=MessageFactory.video_call;
//
//                }
//                else
//                {
//                    type=MessageFactory.audio_call;
//                }
//
//                JSONObject object = (JSONObject) message.getMessageObject(mReceiverId,type);
//
//
//                String roomid = null;
//                String timestamp=null;
//                try {
//                    roomid = object.getString("id");
//                    timestamp = object.getString("id");
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
////                roomid=message.getroomid();
////                timestamp=message.getroomid();
//                String callid = mCurrentUserId + "-" + mReceiverId + "-" + timestamp;
////        CallMessage.openCallScreen(ChatViewActivity.this, mCurrentUserId, receiverUid, callid,
////                roomid, "", receiverMsisdn, "999", isVideoCall, true, timestamp);
//
//
//                if (!CallsActivity.isStarted) {
//                    if (isOutgoingCall) {
//                        CallsActivity.opponentUserId = to;
//                    }
//
//                    if (object != null) {
//                        SendMessageEvent callEvent = new SendMessageEvent();
//                        callEvent.setEventName(SocketManager.EVENT_CALL);
//                        callEvent.setMessageObject(object);
//                        EventBus.getDefault().post(callEvent);
//                    }
//
//                    PreferenceManager.setDefaultValues(context, org.appspot.apprtc.R.xml.preferences, false);
//                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
//
//                    String keyprefRoomServerUrl = context.getString(org.appspot.apprtc.R.string.pref_room_server_url_key);
//                    String roomUrl = sharedPref.getString(
//                            keyprefRoomServerUrl, context.getString(org.appspot.apprtc.R.string.pref_room_server_url_default));
//
//
//                    int videoWidth = 0;
//                    int videoHeight = 0;
//                    String resolution = context.getString(org.appspot.apprtc.R.string.pref_resolution_default);
//                    String[] dimensions = resolution.split("[ x]+");
//                    if (dimensions.length == 2) {
//                        try {
//                            videoWidth = Integer.parseInt(dimensions[0]);
//                            videoHeight = Integer.parseInt(dimensions[1]);
//                        } catch (NumberFormatException e) {
//                            videoWidth = 0;
//                            videoHeight = 0;
//                            Log.e("ChatappCallError", "Wrong video resolution setting: " + resolution);
//                        }
//                    }
//                    Uri uri = Uri.parse(roomUrl);
//                    Intent intent = new Intent(context, CallsActivity.class);
////            Intent intent = new Intent(context, CallNotifyService.class);
//
//                    intent.setData(uri);
//                    intent.putExtra(CallsActivity.EXTRA_IS_OUTGOING_CALL, isOutgoingCall);
//                    intent.putExtra(CallsActivity.EXTRA_DOC_ID, callid);
//                    intent.putExtra(CallsActivity.EXTRA_FROM_USER_ID, from);
//                    intent.putExtra(CallsActivity.EXTRA_TO_USER_ID, to);
//                    intent.putExtra(CallsActivity.EXTRA_USER_MSISDN, receiverMsisdn);
//                    intent.putExtra(CallsActivity.EXTRA_OPPONENT_PROFILE_PIC, "");
//                    intent.putExtra(CallsActivity.EXTRA_NAVIGATE_FROM, context.getClass().getSimpleName()); // For navigating from call activity
//                    intent.putExtra(CallsActivity.EXTRA_CALL_CONNECT_STATUS, "0");
//                    intent.putExtra(CallsActivity.EXTRA_CALL_TIME_STAMP, timestamp);
//
//                    intent.putExtra(CallsActivity.EXTRA_ROOMID, roomid);
//                    intent.putExtra(CallsActivity.EXTRA_LOOPBACK, false);
//                    intent.putExtra(CallsActivity.EXTRA_VIDEO_CALL, isVideoCall);
//                    intent.putExtra(CallsActivity.EXTRA_SCREENCAPTURE, false);
//                    intent.putExtra(CallsActivity.EXTRA_CAMERA2, true);
//                    intent.putExtra(CallsActivity.EXTRA_VIDEO_WIDTH, videoWidth);
//                    intent.putExtra(CallsActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
//                    intent.putExtra(CallsActivity.EXTRA_VIDEO_FPS, 0);
//                    intent.putExtra(CallsActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false);
//                    intent.putExtra(CallsActivity.EXTRA_VIDEO_BITRATE, 0);
//                    intent.putExtra(CallsActivity.EXTRA_VIDEOCODEC, context.getString(org.appspot.apprtc.R.string.pref_videocodec_default));
//                    intent.putExtra(CallsActivity.EXTRA_HWCODEC_ENABLED, false);
//                    intent.putExtra(CallsActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, true);
//                    intent.putExtra(CallsActivity.EXTRA_FLEXFEC_ENABLED, false);
//                    intent.putExtra(CallsActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, false);
//                    intent.putExtra(CallsActivity.EXTRA_AECDUMP_ENABLED, false);
//                    intent.putExtra(CallsActivity.EXTRA_OPENSLES_ENABLED, false);
//                    intent.putExtra(CallsActivity.EXTRA_DISABLE_BUILT_IN_AEC, false);
//                    intent.putExtra(CallsActivity.EXTRA_DISABLE_BUILT_IN_AGC, false);
//                    intent.putExtra(CallsActivity.EXTRA_DISABLE_BUILT_IN_NS, false);
//                    intent.putExtra(CallsActivity.EXTRA_ENABLE_LEVEL_CONTROL, false);
//                    intent.putExtra(CallsActivity.EXTRA_AUDIO_BITRATE, 0);
//                    intent.putExtra(CallsActivity.EXTRA_AUDIOCODEC, context.getString(org.appspot.apprtc.R.string.pref_audiocodec_default));
//                    intent.putExtra(CallsActivity.EXTRA_DISPLAY_HUD, false);
//                    intent.putExtra(CallsActivity.EXTRA_TRACING, false);
//                    intent.putExtra(CallsActivity.EXTRA_CMDLINE, false);
//                    intent.putExtra(CallsActivity.EXTRA_RUNTIME, 0);
//
//                    intent.putExtra(CallActivity.EXTRA_DATA_CHANNEL_ENABLED, true);
//                    intent.putExtra(CallActivity.EXTRA_ORDERED, true);
//                    intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS_MS, -1);
//                    intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS, -1);
//                    intent.putExtra(CallActivity.EXTRA_PROTOCOL, context.getString(org.appspot.apprtc.R.string.pref_data_protocol_default));
//                    intent.putExtra(CallActivity.EXTRA_NEGOTIATED, false);
//                    intent.putExtra(CallActivity.EXTRA_ID, -1);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                    startActivity(intent);
//
//
//                }
//
//
//            } else {
//                requestAudioRecordPermission();
//            }
//
//
//    }
//
//
//    private void loadCallResMessage(ReceviceMessageEvent event) {
//        Object[] obj = event.getObjectsArray();
//        try {
//            Log.d(TAG, "loadCallResMessage: ");
//            JSONObject object = new JSONObject(obj[0].toString());
//            JSONObject callObj = object.getJSONObject("data");
//
//            String from = callObj.getString("from");
//            String callStatus = callObj.getString("call_status");
//            if (from.equalsIgnoreCase(mCurrentUserId) && callStatus.equals(MessageFactory.CALL_STATUS_CALLING + "")) {
//                String to = callObj.getString("to");
//
//                IncomingMessage incomingMsg = new IncomingMessage(this);
//                CallItemChat callItem = incomingMsg.loadOutgoingCall(callObj);
//
//                boolean isVideoCall = false;
//                if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
//                    isVideoCall = true;
//                }
//
//                String toUserAvatar = callObj.getString("To_avatar") + "?=id" + Calendar.getInstance().getTimeInMillis();
//
//                MessageDbController db = CoreController.getDBInstance(this);
//                db.updateCallLogs(callItem);
//
//                String ts = callObj.getString("timestamp");
//
//                CallMessage.openCallScreen(this, mCurrentUserId, to, callItem.getCallId(),
//                        callItem.getRecordId(), toUserAvatar, callItem.getOpponentUserMsisdn(),
//                        MessageFactory.CALL_IN_FREE + "", isVideoCall, true, ts);
//                // needRefresh = true;
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadMuteMessage(String data) {
//        try {
//            JSONObject object = new JSONObject(data);
//            String from = object.getString("from");
//            if (from.equalsIgnoreCase(mCurrentUserId)) {
//                String convId = object.getString("convId");
//                if (convId.equalsIgnoreCase(mConvId)) {
////                    onCreateOptionsMenu(chatMenu);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadMakeAdminMessage(JSONObject object) {
//        try {
//            String groupId = object.getString("groupId");
//
//            if (groupId.equalsIgnoreCase(mGroupId)) {
//                String msgId = object.getString("id");
//                String timeStamp = object.getString("timeStamp");
////                    String toDocId = object.getString("toDocId");
//                String from = object.getString("from");
//
//                String senderOriginalName="";
////
//                if(object.has("fromuser_name")) {
//
//                    try {
//                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//
//                String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
//                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
//                String groupName;
//                if (object.has("groupName")) {
//                    groupName = object.getString("groupName");
//                } else {
//                    groupName = infoPojo.getGroupName();
//                }
//                String newAdminUserId = object.getString("adminuser");
//
//                GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
//                MessageItemChat item = message.createMessageItem(MessageFactory.make_admin_member, false, null,
//                        MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, from, newAdminUserId,senderOriginalName);
//                item.setMessageId(docId.concat("-").concat(msgId));
//                item.setTS(timeStamp);
//
//                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                ChatappContactModel newAdminContact = contactDB_sqlite.getUserOpponenetDetails(newAdminUserId);
//
//                if (newAdminUserId.equalsIgnoreCase(mCurrentUserId)) {
//                    mChatData.add(item);
//                    mAdapter.notifyDataSetChanged();
//                } else if (newAdminContact != null) {
//                    mChatData.add(item);
//                    mAdapter.notifyDataSetChanged();
//                }
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadAddMemberMessage(JSONObject object) {
//        try {
//            String groupId = object.getString("groupId");
//            if (groupId.equalsIgnoreCase(mGroupId)) {
//                String msgId = object.getString("id");
//                String timeStamp = object.getString("timeStamp");
//                String from = object.getString("from");
//                String groupName = "";
//                if (object.has("groupName")) {
//                    groupName = object.getString("groupName");
//                }
//
//                String senderOriginalName="";
//                if(object.has("fromuser_name")) {
//
//                    try {
//                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//                JSONObject newUserObj = object.getJSONObject("newUser");
//                String newUserId = newUserObj.getString("_id");
//
//                String docId = mCurrentUserId.concat("-").concat(groupId).concat("-g");
//                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
//                if (infoPojo != null) {
//                    groupName = infoPojo.getGroupName();
//                }
//
//                GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
//                MessageItemChat item = message.createMessageItem(MessageFactory.add_group_member, false, null,
//                        MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, from, newUserId,senderOriginalName);
//                item.setMessageId(docId.concat("-").concat(msgId));
//                item.setTS(timeStamp);
//                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                ChatappContactModel fromUserContact = contactDB_sqlite.getUserOpponenetDetails(from);
//                ChatappContactModel newUserContact = contactDB_sqlite.getUserOpponenetDetails(newUserId);
//                StringBuilder sb = new StringBuilder();
//
//                sb.append(statusTextView.getText().toString()+","+newUserContact.getFirstName());
//
//                statusTextView.setText(sb);
//
//
//                if (from.equalsIgnoreCase(mCurrentUserId)) {
//                    if (newUserContact != null) {
//                        mChatData.add(item);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                } else {
//                    if (newUserId.equalsIgnoreCase(mCurrentUserId)) {
//                        if (fromUserContact != null) {
//                            mChatData.add(item);
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    } else {
//                        if (fromUserContact != null && newUserContact != null) {
//                            mChatData.add(item);
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    }
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void loadDeleteMemberMessage(JSONObject object) {
//        try {
//            String err = object.getString("err");
//
//            if (err.equalsIgnoreCase("0")) {
//                String from = object.getString("from");
//                String groupId = object.getString("groupId");
//                String senderOriginalName="";
////
//                if(object.has("fromuser_name")) {
//
//                    try {
//                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//
//                if (groupId.equalsIgnoreCase(mGroupId)) {
//
//                    String msgId = object.getString("id");
//
//                    String timeStamp;
//                    if (object.has("timeStamp")) {
//                        timeStamp = object.getString("timeStamp");
//                    } else {
//                        timeStamp = object.getString("timestamp");
//                    }
//
//                    String removeId;
//                    if (object.has("removeId")) {
//                        removeId = object.getString("removeId");
//                    } else {
//                        removeId = object.getString("createdTo");
//                    }
//
//                    GroupInfoPojo groupInfoPojo = groupInfoSession.getGroupInfo(docId);
//                    String groupName = groupInfoPojo.getGroupName();
//
//                    GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
//                    MessageItemChat item = message.createMessageItem(MessageFactory.delete_member_by_admin, false, null, MessageFactory.DELIVERY_STATUS_READ,
//                            groupId, groupName, from, removeId,senderOriginalName);
//                    item.setMessageId(docId.concat("-").concat(msgId));
//                    item.setTS(timeStamp);
//
//                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                    ChatappContactModel fromUserContact = contactDB_sqlite.getUserOpponenetDetails(from);
//                    ChatappContactModel removedContact = contactDB_sqlite.getUserOpponenetDetails(removeId);
//
//
//                    StringBuilder sb = new StringBuilder();
//
//                    String[] oldMmbers=statusTextView.getText().toString().split(",");
//
//                    for(int i=0;i<oldMmbers.length;i++)
//                    {
//                        if(!oldMmbers[i].equals(removedContact.getFirstName()))
//                        {
//                            if(removeId.equalsIgnoreCase(mCurrentUserId)) {
//                                if(!oldMmbers[i].equalsIgnoreCase("you")) {
//                                    sb.append(oldMmbers[i] + ",");
//                                }
//                            }
//                            else
//                            {
//                                sb.append(oldMmbers[i] + ",");
//                            }
//
//                        }
//                    }
//
//                    String newMem = sb.substring(0, sb.length() - 1);
//                    statusTextView.setText(newMem);
//
//                    if (removeId.equalsIgnoreCase(mCurrentUserId)) {
//                        if (isBroadcast) {
//                            enableGroupChat = true;
//                        } else {
//                            enableGroupChat = false;
//                            frame_lyt_nolonger.setVisibility(View.VISIBLE);
//                            rt_nolonger.setVisibility(View.GONE);
//                            rlSend.setVisibility(View.GONE);
//
//                        }
//
//                        if (fromUserContact != null) {
//                            mChatData.add(item);
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    } else if (from.equalsIgnoreCase(mCurrentUserId)) {
//                        if (removedContact != null) {
//                            mChatData.add(item);
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    } else {
//                        if (fromUserContact != null && removedContact != null) {
//                            mChatData.add(item);
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    }
//
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    public void addNewGroupData(JSONObject object) {
//        try {
//            String err = object.getString("err");
//            if (err.equals("0")) {
//                String msgId = object.getString("id");
//                String createdBy = object.getString("createdBy");
//                String groupId = object.getString("groupId");
//                String groupAvatar = object.getString("profilePic");
//                String groupName = object.getString("groupName");
//                String groupMembers = object.getString("groupMembers");
//                String admin = object.getString("admin");
//                String timeStamp = object.getString("timeStamp");
//                String createdTo = object.getString("createdTo");
//
//                String senderOriginalName="";
//
//                if (groupId.equalsIgnoreCase(mGroupId)) {
//
//                    if (object.has("fromuser_name")) {
//
//                        try {
//                            senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
//                        } catch (UnsupportedEncodingException e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//
//                    try {
//                        JSONObject createdToObj = new JSONObject(createdTo);
//                        createdTo = createdToObj.getString("_id");
//                    } catch (JSONException e) {
//
//                    }
//
//                    String docId = mCurrentUserId + "-" + groupId + "-g";
//
//
//                    GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
//                    MessageItemChat item = message.createMessageItem(MessageFactory.add_group_member, false, null,
//                            MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, createdBy, createdTo, senderOriginalName);
//                    item.setMessageId(docId.concat("-").concat(msgId));
//                    item.setTS(msgId);
//
//                    mChatData.add(item);
//                    mAdapter.notifyDataSetChanged();
//                    enableGroupChat = true;
//                    frame_lyt_nolonger.setVisibility(View.GONE);
//                    rt_nolonger.setVisibility(View.VISIBLE);
//                    rlSend.setVisibility(View.VISIBLE);
//                }
////                MessageDbController db = CoreController.getDBInstance(context);
////                db.updateChatMessage(item, MessageFactory.CHAT_TYPE_GROUP);
////
////
////                sendGroupAckToServer(mCurrentUserId, groupId, msgId);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void load_clear_chat(JSONObject object) {
//
//        try {
//            String from = object.getString("from");
//            String convId = object.getString("convId");
//            String type = object.getString("type");
//
//            if (from.equalsIgnoreCase(mCurrentUserId) && convId.equalsIgnoreCase(mConvId)) {
//                int star_status;
//                Boolean starred = false;
//                if (object.has("star_status")) {
//                    star_status = object.getInt("star_status");
//                    starred = star_status != 0;
//                }
//
//                if (type.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
//
//                    if (starred) {
//                        ArrayList<MessageItemChat> value = new ArrayList<>();
//                        for (int i = 0; i < mChatData.size(); i++) {
//                            if (mChatData.get(i).getStarredStatus().equalsIgnoreCase(MessageFactory.MESSAGE_UN_STARRED)) {
//                                value.add(mChatData.get(i));
//                            }
//                        }
//
//                        for (int i = 0; i < value.size(); i++) {
//                            if (value.get(i).isMediaPlaying()) {
//                                int chatIndex = mChatData.indexOf(value.get(i));
//                                if (chatIndex > -1 && mChatData.get(chatIndex).isMediaPlaying()) {
//                                    mAdapter.stopAudioOnMessageDelete(chatIndex);
//                                }
//                            }
//                            mChatData.remove(value.get(i));
//                        }
//
//                    } else {
//                        mAdapter.stopAudioOnClearChat();
//                        mChatData.clear();
//
//                    }
//                } else {
//
//                    if (starred) {
//                        ArrayList<MessageItemChat> value = new ArrayList<>();
//                        for (int i = 0; i < mChatData.size(); i++) {
//                            if (mChatData.get(i).getStarredStatus().equalsIgnoreCase(MessageFactory.MESSAGE_UN_STARRED)) {
//                                value.add(mChatData.get(i));
//                            }
//                        }
//
//                        for (int i = 0; i < value.size(); i++) {
//                            if (value.get(i).isMediaPlaying()) {
//                                int chatIndex = mChatData.indexOf(value.get(i));
//                                if (chatIndex > -1 && mChatData.get(chatIndex).isMediaPlaying()) {
//                                    mAdapter.stopAudioOnMessageDelete(chatIndex);
//                                }
//                            }
//                            mChatData.remove(value.get(i));
//                        }
//
//                    } else {
//                        mChatData.clear();
//
//                    }
//                }
//
//
//                mAdapter.notifyDataSetChanged();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private class loadFileUploaded extends AsyncTask<String, Void, String> {
//        JSONObject jsonObject_bg;
//
//        public loadFileUploaded(JSONObject object) {
//            jsonObject_bg = object;
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            loadFileUploaded(jsonObject_bg);
//            return "";
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            //UI thread
//        }
//    }
//
//    private void loadFileUploaded(JSONObject object) {
//
//        try {
//            int uploadedSize = object.getInt("UploadedSize");
//            int totalSize = object.getInt("size");
//            String msgId = object.getString("id");
//
//            final int progress = (uploadedSize * 100) / totalSize;
//
//            for (MessageItemChat msgItem : mChatData) {
//                if (msgItem.getMessageId().equalsIgnoreCase(msgId)) {
//                    final int index = mChatData.indexOf(msgItem);
//                    if (progress >= mChatData.get(index).getUploadDownloadProgress()) {
//                        sharedprf_video_uploadprogress.setfileuploadingprogress(progress, msgId);
//                        if (progress == 0) {
//                            runOnUiThread(new Runnable() {
//                                public void run() {
//                                    // UI code goes here
//                                    mChatData.get(index).setUploadDownloadProgress(2);
//                                    mAdapter.notifyDataSetChanged();
//                                }
//                            });
//                        } else {
//                            runOnUiThread(
//                                    new Runnable() {
//                                        public void run() {
//                                            // UI code goes here
//                                            mChatData.get(index).setUploadDownloadProgress(progress);
//                                            mAdapter.notifyDataSetChanged();
//                                        }
//                                    });
////                            for (int i = progressglobal; i <= progress; i++) {
////                                handler.postDelayed(new Runnable() {
////                                    public void run() {
////
////                                    }
////                                }, i * 2000);
////
////                            }
//                        }
//                        runOnUiThread(new Runnable() {
//                            public void run() {
//                                // UI code goes here
//                                progressglobal = progress;
//                            }
//                        });
//
//
//                    }
//
//                    break;
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void loadDeleteMessage(ReceviceMessageEvent event) {
//        Object[] array = event.getObjectsArray();
//        try {
//            JSONObject objects = new JSONObject(array[0].toString());
//            int errorState = objects.getInt("err");
//            if (errorState == 0) {
//                String fromId = objects.getString("from");
//
//                if (fromId.equalsIgnoreCase(from)) {
//                    String deleteStatus = objects.getString("status");
//                    String chat_id = (String) objects.get("doc_id");
//                    String[] ids = chat_id.split("-");
//
//                    String docId;
//                    String msgId;
//                    if (chat_id.contains("-g-")) {
//                        docId = fromId + "-" + ids[1] + "-g";
//                        msgId = docId + "-" + ids[3];
//                    } else {
//                        if (fromId.equalsIgnoreCase(ids[0])) {
//                            docId = ids[0] + "-" + ids[1];
//                        } else {
//                            docId = ids[1] + "-" + ids[0];
//                        }
//                        msgId = docId + "-" + ids[2];
//                    }
//
//                    //  String groupAndMsgId = ids[1] + "-g-" + ids[3];
//
//                    if (deleteStatus.equalsIgnoreCase("1")) {
//                        for (MessageItemChat items : mChatData) {
//                            if (items != null && items.getMessageId().equalsIgnoreCase(msgId)) {
//                                int index = mChatData.indexOf(items);
//                                if (index > -1 && mChatData.get(index).isMediaPlaying()) {
//                                    mAdapter.stopAudioOnMessageDelete(index);
//                                }
//
////                                if(fromId.equalsIgnoreCase(from)){
////                                    mChatData.get(index).setMessageType(MessageFactory.DELETE_SELF + "");
////                                    mChatData.get(index).setIsSelf(true);
////                                    mAdapter.notifyDataSetChanged();
////                                    db.deleteSingleMessage(groupAndMsgId, chat_id, chatType, "self");
////                                    db.deleteChatListPage(groupAndMsgId, chat_id, chatType, "self");
////                                }else{
////                                    mChatData.get(index).setMessageType(MessageFactory.DELETE_OTHER + "");
////                                    mChatData.get(index).setIsSelf(false);
////                                    mAdapter.notifyDataSetChanged();
////                                    db.deleteSingleMessage(groupAndMsgId, chat_id, chatType, "other");
////                                    db.deleteChatListPage(groupAndMsgId, chat_id, chatType, "other");
////                                }
//
////                                mChatData.remove(index);
//                                mAdapter.notifyDataSetChanged();
//                                break;
//                            }
//                        }
//
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            Log.e(TAG, "loadDeleteMessage: ", ex);
//        }
//    }
//
//    private void loadStarredMessage(ReceviceMessageEvent event) {
//
//        Object[] array = event.getObjectsArray();
//        try {
//            JSONObject objects = new JSONObject(array[0].toString());
//            int errorState = objects.getInt("err");
//            if (errorState == 0) {
//                String fromId = objects.getString("from");
//                if (fromId.equalsIgnoreCase(from)) {
//                    String chat_id = objects.getString("doc_id");
//                    String[] ids = chat_id.split("-");
//                    String docId;
//                    if (fromId.equalsIgnoreCase(ids[0])) {
//                        docId = ids[0] + "-" + ids[1];
//                    } else {
//                        docId = ids[1] + "-" + ids[0];
//                    }
//
//                    if (chat_id.contains("-g-")) {
//                        docId = docId + "-g";
//                    }
//
//                    String starred = objects.getString("status");
//                    String msgId = docId + "-" + ids[2];
//
//                    /*MessageDbController db = CoreController.getDBInstance();
//                    MessageItemChat item = db.updateStarredMessage(docId, chat_id, starNextLine);*/
//                    for (MessageItemChat items : mChatData) {
//                        if (items != null && items.getMessageId().equalsIgnoreCase(msgId)) {
//                            int index = mChatData.indexOf(items);
//                            mChatData.get(index).setStarredStatus(starred);
//                            mChatData.get(index).setSelected(false);
//                            mAdapter.notifyDataSetChanged();
//                            break;
//                        }
//                    }
//                }
//            }
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void handleGroupMessage(ReceviceMessageEvent event) {
//        Object[] array = event.getObjectsArray();
//        try {
//            JSONObject objects = new JSONObject(array[0].toString());
//            Object object = objects.get("type");
//
//            Integer type = 0;
//            if (object instanceof String) {
//                type = Integer.valueOf((String) objects.get("type"));
//            } else if (object instanceof Integer) {
//                type = Integer.valueOf((Integer) objects.get("type"));
//            }
//
//            switch (type) {
//
//                case MessageFactory.delete_member_by_admin:
//                    loadDeleteMemberMessage(objects);
//                    break;
//
//                default:
//                    loadGroupMessage(objects);
//                    break;
//            }
//
//
//            //-------------Delete Chat----------------
//            if (isGroupChat) {
//                int group_type, is_deleted;
//                String new_msgId;
//                if (objects.has("groupType")) {
//                    group_type = objects.getInt("groupType");
//                    if (group_type == 9) {
//                        if (objects.has("is_deleted_everyone")) {
//                            is_deleted = objects.getInt("is_deleted_everyone");
//                            if (is_deleted == 1) {
//                                String chat_id = (String) objects.get("toDocOId");
//                                String[] ids = chat_id.split("-");
//                                new_msgId = mCurrentUserId + "-" + ids[1] + "-g-" + ids[3];
//                                String groupAndMsgId = ids[1] + "-g-" + ids[3];
//                                try {
//                                    String fromId = objects.getString("from");
//
//                                    if (fromId.equalsIgnoreCase(from)) {
//                                        if (mChatData.size() > 0) {
//                                            for (int i = 0; i < mChatData.size(); i++) {
//                                                if (mChatData.get(i).getMessageId().contains(chat_id)) {
//                                                    if (i > -1 && mChatData.get(i).isMediaPlaying()) {
//                                                        mAdapter.stopAudioOnMessageDelete(i);
//                                                    }
//
//                                                    mChatData.get(i).setMessageType(MessageFactory.DELETE_OTHER + "");
//                                                    mChatData.get(i).setIsSelf(false);
//
//
//                                                    mAdapter.notifyDataSetChanged();
//                                                    db.deleteSingleMessage(groupAndMsgId, chat_id, chatType, "self");
//                                                    db.deleteChatListPage(groupAndMsgId, chat_id, chatType, "self");
//
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    }
//
//
//                                    if (!fromId.equalsIgnoreCase(mCurrentUserId)) {
//                                        if (mChatData.size() > 0) {
//                                            for (int i = 0; i < mChatData.size(); i++) {
//                                                if (mChatData.get(i).getMessageId().contains(groupAndMsgId)) {
//                                                    if (i > -1 && mChatData.get(i).isMediaPlaying()) {
//                                                        mAdapter.stopAudioOnMessageDelete(i);
//                                                    }
//
//                                                    mChatData.get(i).setMessageType(MessageFactory.DELETE_OTHER + "");
//                                                    mChatData.get(i).setIsSelf(false);
//
//                                                    mAdapter.notifyDataSetChanged();
//
//                                                    db.deleteSingleMessage(groupAndMsgId, new_msgId, "group", "other");
//                                                    db.deleteChatListPage(groupAndMsgId, new_msgId, "group", "other");
//                                                    break;
//                                                }
//                                            }
//                                        }
//                                    }
//                                } catch (JSONException ex) {
//                                    ex.printStackTrace();
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void loadGroupMessage(JSONObject objects) {
//        try {
//            String from = objects.getString("from");
//            String groupId = objects.getString("groupId");
//
//
//            int msgIndex = -1;
//
//            if (from.equalsIgnoreCase(mCurrentUserId)) {
//                if (mGroupId.equalsIgnoreCase(groupId)) {
//                    String msgId = objects.getString("toDocId");
//
//                    for (int i = 0; i < mChatData.size(); i++) {
//                        if (mChatData.get(i).getMessageId().equalsIgnoreCase(msgId)) {
//                            msgIndex = i;
//                            break;
//                        }
//                    }
//
//                    if (msgIndex > -1) {
//                        groupMessageRes(objects, msgIndex);
//                    } else {
//                        IncomingMessage incomingMessage = new IncomingMessage(ChatViewActivityRealcopy.this);
//                        MessageItemChat msgItem = incomingMessage.loadGroupMessageFromWeb(objects);
//                        if (msgItem != null) {
//                            mChatData.add(msgItem);
//                            notifyDatasetChange();
//                        }
//                    }
//                }
//            } else {
//                if (groupId.equalsIgnoreCase(mGroupId)) {
//                    IncomingMessage incomingMessage = new IncomingMessage(ChatViewActivityRealcopy.this);
//                    MessageItemChat msgItem = incomingMessage.loadGroupMessage(objects);
//                    String senderName = getcontactname.getSendername(msgItem.getReceiverID(), msgItem.getSenderName());
//                    msgItem.setSenderName(senderName);
//
//                    if (msgItem != null) {
//
//                        if (!from.equalsIgnoreCase(mCurrentUserId)) {
//                            if (lastvisibleitempostion == mChatData.size() - 1) {
//                                mChatData.add(msgItem);
//                                notifyDatasetChange();
//                            } else {
//                                unreadmsgcount++;
//
//                                unreadmessage();
//                                mChatData.add(msgItem);
//                                mAdapter.notifyDataSetChanged();
//                            }
//                        } else {
//                            notifyDatasetChange();
//                        }
//                    }
//
//                    String id = objects.getString("id");
//                    String uniqueID = mCurrentUserId + "-" + to + "-g";
//
//                    if (!from.equalsIgnoreCase(mCurrentUserId)) {
//                        if(isChatPage) {
//
//                            sendGroupAckToServer(mCurrentUserId, to, id, MessageFactory.GROUP_MSG_READ_ACK);
//                        }
//                        else
//                        {
//                            ackMsgid=true;
//                        }
//                    }
//
//                    if (isChatPage) {
//                        changeBadgeCount(uniqueID);
//                    }
//
//                    sendChatViewedStatus();
//
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void loadExitMessage(JSONObject object) {
//        try {
//            String from = object.getString("from");
//            String groupId = object.getString("groupId");
//            String timeStamp = object.getString("timeStamp");
//            String id = object.getString("id");
//            String senderOriginalName="";
//
//
//            if(object.has("fromuser_name")) {
//
//                try {
//                    senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//
//            if (groupId.equalsIgnoreCase(mGroupId)) {
//
//                String docId = sessionManager.getCurrentUserID().concat("-").concat(groupId).concat("-g");
//                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
//                String groupName = infoPojo.getGroupName();
//
//                GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
//                MessageItemChat item = message.createMessageItem(MessageFactory.exit_group, false, null,
//                        MessageFactory.DELIVERY_STATUS_READ, groupId, groupName, from, null,senderOriginalName);
//
//                item.setMessageId(docId.concat("-").concat(id));
//                item.setTS(timeStamp);
//                if (from.equalsIgnoreCase(mCurrentUserId)) {
//                    mChatData.add(item);
//                    mAdapter.notifyDataSetChanged();
//                } else {
//                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                    ChatappContactModel contactModel = contactDB_sqlite.getUserOpponenetDetails(from);
//                    if (contactModel != null) {
//                        mChatData.add(item);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    public void loadChangeGroupNameMessage(JSONObject object) {
//        try {
//            String from = object.getString("from");
//            String id = object.getString("id");
//            String msg = object.getString("message");
//            String groupId = object.getString("groupId");
//            String timeStamp = object.getString("timeStamp");
//            String groupNewName = object.getString("groupName");
//            String groupPrevName = object.getString("prev_name");
//            String senderOriginalName="";
//
//            if (groupId.equalsIgnoreCase(mGroupId)) {
//
//                if (object.has("fromuser_name")) {
//
//                    try {
//                        senderOriginalName = new String(Base64.decode(object.getString("fromuser_name"), Base64.DEFAULT), "UTF-8");
//                    } catch (UnsupportedEncodingException e) {
//                        e.printStackTrace();
//                    }
//
//
//                }
//
//                String docId = sessionManager.getCurrentUserID().concat("-").concat(groupId).concat("-g");
//                GroupEventInfoMessage message = (GroupEventInfoMessage) MessageFactory.getMessage(MessageFactory.group_event_info, context);
//                MessageItemChat item = message.createMessageItem(MessageFactory.change_group_name, false, null,
//                        MessageFactory.DELIVERY_STATUS_READ, groupId, groupNewName, from, null, senderOriginalName);
//                receiverNameText.setText(groupNewName);
//                item.setPrevGroupName(groupPrevName);
//                item.setMessageId(docId.concat("-").concat(id));
//                item.setTS(timeStamp);
//                if (from.equalsIgnoreCase(mCurrentUserId)) {
//                    mChatData.add(item);
//                    mAdapter.notifyDataSetChanged();
//                } else {
//                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                    ChatappContactModel fromUserContact = contactDB_sqlite.getUserOpponenetDetails(from);
//
//                    if (fromUserContact != null) {
//                        mChatData.add(item);
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }
//            }
//
//        } catch (JSONException ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    private void groupMessageRes(JSONObject jsonObject, int msgIndex) {
//
//        try {
//            String deliver = jsonObject.getString("deliver");
//            String recordId = jsonObject.getString("recordId");
//
//            mChatData.get(msgIndex).setUploadStatus(MessageFactory.UPLOAD_STATUS_COMPLETED);
//            mChatData.get(msgIndex).setDeliveryStatus(deliver);
//            mChatData.get(msgIndex).setRecordId(recordId);
//            mAdapter.notifyDataSetChanged();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private  void createUpdateGroupMsgStatus(JSONObject objects)
//    {
//        try {
//            String err = objects.getString("err");
//            if (err.equalsIgnoreCase("0")) {
//                String from = objects.getString("from");
//
//                String groupId = objects.getString("groupId");
//                String msgId = objects.getString("msgId");
//                String deliverStatus = objects.getString("status");
////                String recordId = objects.getString("recordId");
//                String docId = sessionManager.getCurrentUserID().concat("-").concat(groupId).concat("-g");
//                String resMsgId = docId.concat("-").concat(msgId);
//                String timeStamp = objects.getString("currenttime");
//                if (groupId.equalsIgnoreCase(mGroupId)) {
//                    updateGroupMsgStatus(docId, resMsgId, deliverStatus,
//                            timeStamp, from, mCurrentUserId);
//                }
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void updateGroupMsgStatus(String docId, String resMsgId, String deliverStatus, String timeStamp,
//                                      String from, String currentUserId)
//    {
//
//        for (int i = 0; i < mChatData.size(); i++) {
//                    MessageItemChat dbItem = mChatData.get(i);
//                    if (dbItem != null && dbItem.getMessageId().equalsIgnoreCase(resMsgId)) {
//
//
//                        if (deliverStatus.equalsIgnoreCase(MessageFactory.GROUP_MSG_DELIVER_ACK)) {
//                            if (dbItem.isSelf() && dbItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)) {
//                                updateGroupMsgDeliverStatus(docId,from,dbItem,timeStamp);
//                            } else if (!dbItem.isSelf() && from.equals(currentUserId)) {
//                                dbItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);
//                            }
//                        } else if (deliverStatus.equalsIgnoreCase(MessageFactory.GROUP_MSG_READ_ACK)) {
//                            if (dbItem.isSelf() && !dbItem.getDeliveryStatus().equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
//                                updateGroupMsgReadStatus(docId,from,dbItem,timeStamp);
//                            } else if (!dbItem.isSelf() && from.equals(currentUserId)) {
//                                dbItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_READ);
//                            }
//                        }
//
//                        break;
//                    }
//                    else {
//
//                        if (deliverStatus.equalsIgnoreCase(MessageFactory.GROUP_MSG_DELIVER_ACK)) {
//                            if (dbItem.isSelf() && dbItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)) {
//                                updateGroupMsgDeliverStatus(docId,from,dbItem,timeStamp);
//                            } else if (!dbItem.isSelf() && from.equals(currentUserId)) {
//                                dbItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);
//                            }
//                        } else if (deliverStatus.equalsIgnoreCase(MessageFactory.GROUP_MSG_READ_ACK)) {
//                            if (dbItem.isSelf() && !dbItem.getDeliveryStatus().equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
//                                updateGroupMsgReadStatus(docId,from,dbItem,timeStamp);
//                            } else if (!dbItem.isSelf() && from.equals(currentUserId)) {
//                                dbItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_READ);
//                            }
//                        }
//                    }
//
//
//                }
//                mAdapter.notifyDataSetChanged();
//                }
//
//
//
//
//    private void updateGroupMsgDeliverStatus(String docId, String ackUserId, MessageItemChat msgItem,
//                                             String deliverOrReadTS) {
//
//        String msgStatus = msgItem.getGroupMsgDeliverStatus();
//        try {
//            boolean isDeliveredAll = true;
//
//            JSONObject msgDeliverObj = new JSONObject(msgStatus);
//            JSONArray arrMembers = msgDeliverObj.getJSONArray("GroupMessageStatus");
//            for (int i = 0; i < arrMembers.length(); i++) {
//                JSONObject userObj = arrMembers.getJSONObject(i);
//                String userId = userObj.getString("UserId");
//                String deliverStatus = userObj.getString("DeliverStatus");
//
//                if (userId.equals(ackUserId) && deliverStatus.equals(MessageFactory.DELIVERY_STATUS_SENT) &&
//                        !deliverStatus.equals(MessageFactory.DELIVERY_STATUS_READ)) {
//                    userObj.put("DeliverStatus", MessageFactory.DELIVERY_STATUS_DELIVERED);
//                    userObj.put("DeliverTime", deliverOrReadTS);
//                    arrMembers.put(i, userObj);
//                    msgDeliverObj.put("GroupMessageStatus", arrMembers);
//                    msgItem.setGroupMsgDeliverStatus(msgDeliverObj.toString());
//                }
//
//                deliverStatus = userObj.getString("DeliverStatus");
//                int deliver = Integer.parseInt(deliverStatus);
//                if (deliver < 2) {
//                    isDeliveredAll = false;
//                }
//            }
//
//            if (isDeliveredAll) {
//                msgItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void updateGroupMsgReadStatus(String docId, String ackUserId, MessageItemChat msgItem,
//                                          String deliverOrReadTS) {
//
//        String msgStatus = msgItem.getGroupMsgDeliverStatus();
//        try {
//            JSONObject msgDeliverObj = new JSONObject(msgStatus);
//            JSONArray arrMembers = msgDeliverObj.getJSONArray("GroupMessageStatus");
//
//            boolean isReadAll = true;
//            if(arrMembers!=null && arrMembers.length()>0) {
//                for (int i = 0; i < arrMembers.length(); i++) {
//                    JSONObject userObj = arrMembers.getJSONObject(i);
//                    String userId = userObj.getString("UserId");
////                if(userId.equals(ackUserId) && msgItem.getCallStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)) {
//                    if (userId.equals(ackUserId)) {
//                        userObj.put("DeliverStatus", MessageFactory.DELIVERY_STATUS_READ);
//                        // for updating deliver time if not exists
//                        if (userObj.getString("DeliverTime").equals("")) {
//                            userObj.put("DeliverTime", deliverOrReadTS);
//                        }
//                        userObj.put("ReadTime", deliverOrReadTS);
//                        arrMembers.put(i, userObj);
//                        msgDeliverObj.put("GroupMessageStatus", arrMembers);
//                        msgItem.setGroupMsgDeliverStatus(msgDeliverObj.toString());
//                    }
//
//                    String deliverStatus = userObj.getString("DeliverStatus");
//                    if (!deliverStatus.equals(MessageFactory.DELIVERY_STATUS_READ)) {
//                        isReadAll = false;
//                    }
//                }
//            }
//            if (isReadAll) {
//                msgItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_READ);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendGroupAckToServer(String from, String groupId, String id, String status) {
//        SendMessageEvent messageEvent = new SendMessageEvent();
//        messageEvent.setEventName(SocketManager.EVENT_GROUP);
//        try {
//            JSONObject groupAckObj = new JSONObject();
//            groupAckObj.put("groupType", SocketManager.ACTION_ACK_GROUP_MESSAGE);
//            groupAckObj.put("from", from);
//            groupAckObj.put("groupId", groupId);
//            groupAckObj.put("status", status);
//            groupAckObj.put("msgId", id);
//            messageEvent.setMessageObject(groupAckObj);
//            EventBus.getDefault().post(messageEvent);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }
//
//    @Override
//    public boolean onCreatePanelMenu(int featureId, Menu menu) {
//        return super.onCreatePanelMenu(featureId, menu);
//    }
//
//
//    private void loadPrivacySetting(ReceviceMessageEvent event) {
//        Object[] object = event.getObjectsArray();
//
//        try {
//            JSONObject jsonObject = new JSONObject(object[0].toString());
//            String from = jsonObject.getString("from");
//            String status = (String) jsonObject.get("status");
//            String lastseen = String.valueOf(jsonObject.get("last_seen"));
//            String profile = String.valueOf(jsonObject.get("profile_photo"));
//            JSONArray contactUserList = jsonObject.getJSONArray("contactUserList");
//            if (from.equalsIgnoreCase(mReceiverId)) {
//                Boolean iscontact = false;
//                if (contactUserList != null) {
//                    iscontact = contactUserList.toString().contains(mCurrentUserId);
//                }
//                if (!isGroupChat) {
//                    if (lastseen.equalsIgnoreCase("nobody")) {
//                        canShowLastSeen = false;
//                        if (!statusTextView.getText().toString().equalsIgnoreCase("online")) {
//                            statusTextView.setVisibility(View.GONE);
//                        }
//                    } else if (lastseen.equalsIgnoreCase("everyone")) {
//                        statusTextView.setVisibility(View.VISIBLE);
//                        canShowLastSeen = true;
//                    } else if (lastseen.equalsIgnoreCase("mycontacts") && iscontact) {
//                        canShowLastSeen = true;
//                        statusTextView.setVisibility(View.VISIBLE);
//                    } else {
//                        canShowLastSeen = false;
//                        if (!statusTextView.getText().toString().equalsIgnoreCase("online")) {
//                            statusTextView.setVisibility(View.GONE);
//                        }
//                    }
//                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                    if (contactDB_sqlite.getBlockedMineStatus(to, false).equals(ContactDB_Sqlite.BLOCKED_STATUS)) {
//                        canShowLastSeen = false;
//                        statusTextView.setVisibility(View.GONE);
//                    }
//
//
//                }
//                profilepicupdation();
//            }
//
//        } catch (Exception e) {
//
//        }
//    }
//
//    private void loadOnlineStatus(ReceviceMessageEvent event) {
//        Object[] object = event.getObjectsArray();
//        try {
//            JSONObject jsonObject = new JSONObject(object[0].toString());
//            String id = (String) jsonObject.get("_id");
//            String status = String.valueOf(jsonObject.get("Status"));
//            if (id.equalsIgnoreCase(to)) {
//                if (status.equalsIgnoreCase("1")) {
//                    statusTextView.setText("Online");
//                } else {
//                    if (canShowLastSeen && !sessionManager.getLastSeenVisibleTo().equalsIgnoreCase("nobody")) {
//                        String lastSeen = jsonObject.getString("DateTime");
//                        setOnlineStatusText(lastSeen);
//                    } else {
//                        statusTextView.setText("");
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadCurrentTimeMessage(ReceviceMessageEvent event) {
//        Object[] object = event.getObjectsArray();
//        try {
//            JSONObject jsonObject = new JSONObject(object[0].toString());
//            String id = (String) jsonObject.get("_id");
//            String status = String.valueOf(jsonObject.get("Status"));
//
//            if (id.equalsIgnoreCase(to)) {
//                isLastSeenCalled = true;
//                canShowLastSeen = true;
//                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//
//                JSONObject privacyObj = jsonObject.getJSONObject("Privacy");
//                if (privacyObj.has("last_seen")) {
//
//                    String showLastSeen = privacyObj.getString("last_seen");
//                    if (showLastSeen.equalsIgnoreCase(ContactDB_Sqlite.PRIVACY_TO_NOBODY)) {
//                        canShowLastSeen = false;
//                        contactDB_sqlite.updateLastSeenVisibility(to, ContactDB_Sqlite.PRIVACY_STATUS_NOBODY);
//                    } else if (showLastSeen.equalsIgnoreCase(ContactDB_Sqlite.PRIVACY_TO_MY_CONTACTS)) {
//                        String isContactUser = jsonObject.getString("is_contact_user");
//
//                        contactDB_sqlite.updateLastSeenVisibility(to, ContactDB_Sqlite.PRIVACY_STATUS_MY_CONTACTS);
//                        contactDB_sqlite.updateMyContactStatus(to, isContactUser);
//
//                        if (isContactUser.equals("0")) {
//                            canShowLastSeen = false;
//                        }
//                    } else {
//                        contactDB_sqlite.updateLastSeenVisibility(to, ContactDB_Sqlite.PRIVACY_STATUS_EVERYONE);
//                    }
//                }
//
//                if (contactDB_sqlite.getBlockedMineStatus(to, false).equals(ContactDB_Sqlite.BLOCKED_STATUS)) {
//                    canShowLastSeen = false;
//                    statusTextView.setVisibility(View.GONE);
//                }
//
//                if ("1".equalsIgnoreCase(status)) {
//                    statusTextView.setText("Online");
//                } else {
//                    if (canShowLastSeen && !sessionManager.getLastSeenVisibleTo().equalsIgnoreCase("nobody")) {
//                        String lastSeen = jsonObject.getString("DateTime");
//                        if (!lastSeen.equals("0") && !lastSeen.equalsIgnoreCase("null")) {
//                            setOnlineStatusText(lastSeen);
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void loadTypingStatus(ReceviceMessageEvent event) {
//        Object[] object = event.getObjectsArray();
//        try {
//            JSONObject jsonObject = new JSONObject(object[0].toString());
//            String from_typing = (String) jsonObject.get("from");
//            String to_typing = (String) jsonObject.get("to");
//            String convId = (String) jsonObject.get("convId");
//            String type = (String) jsonObject.get("type");
//            String typingPerson = "";
//            if (!isGroupChat) {
//                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                boolean blockedStatus = contactDB_sqlite.getBlockedStatus(from_typing, false).equals("1");
//
//                if (from_typing.equalsIgnoreCase(to) && type.equalsIgnoreCase("single") &&
//                        convId.equals(mConvId) && !blockedStatus) {
//                    tvTyping.setText("typing...");
//                    tvTyping.setVisibility(View.VISIBLE);
//                    statusTextView.setVisibility(View.GONE);
////                    dotTexting.setVisibility(View.VISIBLE);
//                    handleReceiverTypingEvent();
//                }
//            } else {
//                if (!from_typing.equalsIgnoreCase(from) && to_typing.equalsIgnoreCase(to)) {
//
//                    if (!from_typing.equalsIgnoreCase(from)) {
//                        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                        ChatappContactModel info = contactDB_sqlite.getUserOpponenetDetails(from_typing);
//                        if (info != null) {
//                            typingPerson = info.getMsisdn();
//                            //  typingPerson = getcontactname.getSendername(from_typing, typingPerson);
//
//                            typingPerson=info.getFirstName();
//
//                        }
//
//                        for (int i = 0; i < mChatData.size(); i++) {
//                            MessageItemChat item = mChatData.get(i);
//                            String groupid[] = item.getMessageId().split("-");
//                            if (convId.equalsIgnoreCase(groupid[1])) {
//                                tvTyping.setText(typingPerson + " is typing....");
//                                tvTyping.setVisibility(View.VISIBLE);
////                                dotTexting.setVisibility(View.VISIBLE);
//                                statusTextView.setVisibility(View.GONE);
//                                handleReceiverTypingEvent();
//                                break;
//                            }
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    Runnable toTypingRunnable = new Runnable() {
//        @Override
//        public void run() {
//            long currentTime = Calendar.getInstance().getTimeInMillis();
//            if (currentTime > toLastTypedAt) {
//                tvTyping.setVisibility(View.GONE);
//                statusTextView.setVisibility(View.VISIBLE);
//                //  dotTexting.setVisibility(View.GONE);
//            }
//        }
//    };
//
//    private void handleReceiverTypingEvent() {
//        toLastTypedAt = Calendar.getInstance().getTimeInMillis() + MessageFactory.TYING_MESSAGE_MIN_TIME_DIFFERENCE;
//        toTypingHandler.postDelayed(toTypingRunnable, MessageFactory.TYING_MESSAGE_TIMEOUT);
//    }
//
//    public void getUserDetails(String userId) {
//        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//        ChatappContactModel contactModel = contactDB_sqlite.getUserOpponenetDetails(userId);
//
//        if (contactModel == null) {
//            try {
//                JSONObject eventObj = new JSONObject();
//                eventObj.put("userId", userId);
//                SendMessageEvent event = new SendMessageEvent();
//                event.setEventName(SocketManager.EVENT_GET_USER_DETAILS);
//                event.setMessageObject(eventObj);
//                EventBus.getDefault().post(event);
//            } catch (JSONException ex) {
//                ex.printStackTrace();
//            }
//        }
//
//    }
//
//    private void setOnlineStatusText(String lastSeen) {
//        try {
//            Long lastSeenTime = Long.parseLong(lastSeen);
//            Long serverDiff = sessionManager.getServerTimeDifference();
//            lastSeenTime = lastSeenTime + serverDiff;
//
//            Date lastSeenAt = new Date(lastSeenTime);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
//            Date date2 = new Date();
//            String currentDate = sdf.format(date2);
//            currentDate = currentDate.substring(0, 10);
//
//            String strLastSeenAt = sdf.format(lastSeenAt);
//            String onlineStatus;
//            if (lastSeen != null) {
//                if (currentDate.equals(strLastSeenAt.substring(0, 10))) {
//                    lastSeen = ChatappUtilities.convert24to12hourformat(strLastSeenAt.substring(11, 19));
//                    lastSeen = lastSeen.replace(".", "");
//                    onlineStatus = "last seen at " + lastSeen;
//                } else {
//                    String last = ChatappUtilities.convert24to12hourformat(strLastSeenAt.substring(11, 19));
//                    String[] separated = strLastSeenAt.substring(0, 10).split("-");
//                    last = last.replace(".", "");
//                    String date = separated[2] + "-" + separated[1] + "-" + separated[0];
//                    onlineStatus = "last seen " + date + " " + last;
//                }
//
//                statusTextView.setText(onlineStatus);
//
//            }
//        } catch (Exception e) {
//            statusTextView.setText("");
//        }
//    }
//
//    private void loadMessageStatusupdate(ReceviceMessageEvent event) {
//        Object[] object = event.getObjectsArray();
//
//        try {
//            JSONObject jsonObject = new JSONObject(object[0].toString());
//            String from = jsonObject.getString("from");
//            String to = jsonObject.getString("to");
//            String msgIds = jsonObject.getString("msgIds");
//            String doc_id = jsonObject.getString("doc_id");
//            String status = jsonObject.getString("status");
//            String secretType = jsonObject.getString("secret_type");
//            if (from.equalsIgnoreCase(mReceiverId) && secretType.equalsIgnoreCase("no")) {
//
//                if (sessionManager.canSendReadReceipt() || !(status.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ))) {
//
//                    for (int i = 0; i < mChatData.size(); i++) {
//                        MessageItemChat items = mChatData.get(i);
//                        if (items != null && items.getMessageId().equalsIgnoreCase(doc_id)) {
//                            items.setDeliveryStatus("" + status);
//                            mChatData.get(i).setDeliveryStatus(status);
////                            break;
//                        } else if (status.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_DELIVERED) && mChatData.get(i).isSelf()) {
//                            if (mChatData.get(i).getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)) {
//                                mChatData.get(i).setDeliveryStatus(status);
//                            }
//                        } else if (mChatData.get(i).isSelf() && status.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ) &&
//                                (mChatData.get(i).getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)
//                                        || (mChatData.get(i).getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_DELIVERED)))) {
//                            mChatData.get(i).setDeliveryStatus(status);
//                        }
//
//                        if (mChatData.get(i).getDeliveryStatus().equals("3")) {
//                            if(mChatData.get(i).getIsExpiry()!=null) {
//                                if (mChatData.get(i).getIsExpiry().equals("1")) {
//                                    if (isExpiry.equals("1") && !onTimeriD.contains(mChatData.get(i).getMessageId())) {
//
//                                        onTimeriD.add(mChatData.get(i).getMessageId());
//                                        long timer = Long.parseLong(mChatData.get(i).getExpiryTime()) * 1000;
//                                        onTimer(timer, mChatData.get(i));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//                mAdapter.notifyDataSetChanged();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//
//    public void onTimer(long milliseconds,MessageItemChat messageItemChat) {
//
//        RefreshDatabaseService.startRefreshService(this, milliseconds,messageItemChat);
////        new CountDownTimer(milliseconds, 1000) {
////
////            public void onTick(long millisUntilFinished) {
////                //  Toast.makeText(this, millisUntilFinished, Toast.LENGTH_SHORT).show();
////            }
////
////            public void onFinish() {
////                new Handler().postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        loadFromDB();
////                    }
////
////                }, 100);
////            }
////
////            }.start();
//
//    }
//
//
//    //  public void refreshDb()
////    {
////
////        ArrayList<MessageItemChat> items = db.selectAllMessagesWithLimit(docId, chatType,
////                "", MessageDbController.MESSAGE_SELECTION_LIMIT_FIRST_TIME);
////
//////            Toast.makeText(context, docId, Toast.LENGTH_SHORT).show();
////
////        BaseMessage baseMessage=new BaseMessage(this);
////        Long timeStamp=Long.parseLong(baseMessage.getShortTimeFormat());
////        //  String timeStamp = TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()) + "";
////
////
////        for (int i = 0; i < items.size(); i++) {
//////                if (Integer.parseInt(items.get(i).getMessageType())==MessageFactory.text) {
////
////            if(items.get(i).getIsExpiry() != null) {
////
////                if (items.get(i).getIsExpiry().equals("1") && items.get(i).getReadTime() != null) {
////                    Long expiredTime = Long.parseLong(items.get(i).getReadTime()) + (Long.parseLong(items.get(i).getExpiryTime()) * 1000);
////
////                    if (expiredTime < timeStamp) {
////
////                        MessageItemChat msgItem = items.get(i);
////
////                        if (msgItem.isMediaPlaying()) {
////                            int chatIndex = mChatData.indexOf(msgItem);
////                            mAdapter.stopAudioOnMessageDelete(chatIndex);
////                        }
////
////                        String lastMsgStatus;
////                        if (mChatData.size() == 1) {
////                            lastMsgStatus = "1";
////                        } else {
////                            lastMsgStatus = "0";
////                        }
////
////                        String docId = from + "-" + to;
////                        if (isGroupChat) {
////                            docId = docId + "-g";
////                            mConvId = mGroupId;
////                        } else {
////                            if (mConvId == null) {
////                                mConvId = msgItem.getConvId();
////                            }
////                        }
////
////                        SendMessageEvent messageEvent = new SendMessageEvent();
////                        messageEvent.setEventName(SocketManager.EVENT_REMOVE_MESSAGE);
////                        try {
////                            JSONObject deleteMsgObj = new JSONObject();
////                            deleteMsgObj.put("from", from);
////                            deleteMsgObj.put("type", chatType);
////                            deleteMsgObj.put("convId", mConvId);
////                            deleteMsgObj.put("status", "1");
////                            deleteMsgObj.put("recordId", msgItem.getRecordId());
////                            deleteMsgObj.put("last_msg", lastMsgStatus);
////                            messageEvent.setMessageObject(deleteMsgObj);
////                            EventBus.getDefault().post(messageEvent);
////
////                            db.deleteChatMessage(docId, msgItem.getMessageId(), chatType);
////                            db.updateTempDeletedMessage(msgItem.getRecordId(), deleteMsgObj);
////
////                            int index = mChatData.indexOf(selectedChatItems.get(i));
////                            if (index > -1) {
////                                mChatData.remove(index);
////                            }
////                        } catch (Exception ex) {
////                            ex.printStackTrace();
////                        }
////                    } else {
////                        onTimer(timeStamp - expiredTime);
////                    }
////                }
////            }
////
////        }
////
////        items = db.selectAllMessagesWithLimit(docId, chatType,
////                "", MessageDbController.MESSAGE_SELECTION_LIMIT_FIRST_TIME);
////
////        Collections.sort(items, msgComparator);
////
////        mChatData.clear();
////        mChatData.addAll(items);
////        notifyDatasetChange();
////
////
////    }
//
//
////    public void onTimer(long milliseconds)
////    {
////
////        final Handler countdownHandler = new Handler();
////        Timer countdownTimer = new Timer();
////
////        TimerTask task = new TimerTask() {
////            @Override
////            public void run() {
////                countdownHandler.post(doA);
////            }
////        };
////        countdownTimer.schedule(task, milliseconds);
////
////    }
////
////    final Runnable doA = new Runnable() {
////        @Override
////        public void run() {
////            loadFromDB();
////        }
////    };
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CODE_CONTACTS && resultCode == RESULT_OK && data != null) {
//            Bundle bundle = data.getExtras();
//            try {
//                contacts = (ArrayList<ContactToSend>) bundle.getSerializable("ContactToSend");
//                String Uname = bundle.getString("name");
//                String Title = bundle.getString("title");
//                JSONArray phone = new JSONArray();
//                JSONArray email = new JSONArray();
//                JSONArray address = new JSONArray();
//                JSONArray IM = new JSONArray();
//                JSONArray Organisation = new JSONArray();
//                JSONArray name = new JSONArray();
//                String contactChatappId = "";
//
//                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                ArrayList<ChatappContactModel> savedChatappContacts = contactDB_sqlite.getSavedChatappContacts();
//
//                if (contacts.size() > 0) {
//                    for (int i = 0; i < contacts.size(); i++) {
//                        if (contacts.get(i).getType().equalsIgnoreCase("Phone")) {
//
//                            try {
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("type", contacts.get(i).getSubType());
//                                jsonObject.put("value", contacts.get(i).getNumber());
//                                phone.put(jsonObject);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else if (contacts.get(i).getType().equalsIgnoreCase("Email")) {
//
//                            try {
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("type", contacts.get(i).getSubType());
//                                jsonObject.put("value", contacts.get(i).getNumber());
//                                email.put(jsonObject);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else if (contacts.get(i).getType().equalsIgnoreCase("Address")) {
//
//                            try {
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("type", contacts.get(i).getSubType());
//                                jsonObject.put("value", contacts.get(i).getNumber());
//                                address.put(jsonObject);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else if (contacts.get(i).getType().equalsIgnoreCase("Instant Messenger")) {
//                            try {
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("type", contacts.get(i).getSubType());
//                                jsonObject.put("value", contacts.get(i).getNumber());
//                                IM.put(jsonObject);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else if (contacts.get(i).getType().equalsIgnoreCase("Organisation")) {
//
//                            try {
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("type", contacts.get(i).getSubType());
//                                jsonObject.put("value", contacts.get(i).getNumber());
//                                Organisation.put(jsonObject);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else if (contacts.get(i).getType().equalsIgnoreCase("Name")) {
//
//                            try {
//                                JSONObject jsonObject = new JSONObject();
//                                jsonObject.put("type", contacts.get(i).getSubType());
//                                jsonObject.put("value", contacts.get(i).getNumber());
//                                name.put(jsonObject);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                    MessageItemChat itemChat = new MessageItemChat();
//                    JSONObject finalObj = new JSONObject();
//                    try {
//                        finalObj.put("phone_number", phone);
//                        finalObj.put("email", email);
//                        finalObj.put("address", address);
//                        finalObj.put("im", IM);
//                        finalObj.put("organisation", Organisation);
//                        finalObj.put("name", name);
//                        ContactString = finalObj.toString();
//                        itemChat.setDetailedContacts(ContactString);
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//
//
//                    if (contacts.size() >= 1) {
//                        number = contacts.get(0).getNumber();
//                        number = number.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
//                    } else {
//                        number = "";
//                    }
//
//
//                    for (ChatappContactModel ChatappModel : savedChatappContacts) {
//
//                        /*if (ChatappModel.getCountryCode() != null) {
//                            if (number.startsWith(ChatappMOdel.getCountryCode())) {
//                                String Phno2 = number.replace(ChatappMOdel.getCountryCode(), "");
//                                String Phno1 = ChatappMOdel.getNumberInDevice().replace(ChatappMOdel.getCountryCode(), "");
//                                if (Phno1.equalsIgnoreCase(Phno2)) {
//                                    contactChatappId = ChatappMOdel.get_id();
//                                    break;
//                                }
//
//                            } else {
//                                String Phno1 = ChatappMOdel.getNumberInDevice().replace(ChatappMOdel.getCountryCode(), "");
//                                if ((number.equalsIgnoreCase(Phno1))) {
//                                    contactChatappId = ChatappMOdel.get_id();
//                                    break;
//                                }
//
//                            }
//                        }*/
//
//                        if (number.equalsIgnoreCase(ChatappModel.getNumberInDevice())
//                                || number.equalsIgnoreCase(ChatappModel.getMsisdn())) {
//                            contactChatappId = ChatappModel.get_id();
//                            break;
//                        }
//                    }
//
//                }
//
//                // Check whether contact is current user details
//                String phNo = number.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
//                if (phNo.equalsIgnoreCase(sessionManager.getPhoneNumberOfCurrentUser()) ||
//                        phNo.equalsIgnoreCase(sessionManager.getUserMobileNoWithoutCountryCode())) {
//                    contactChatappId = mCurrentUserId;
//                }
//
//                sendContactMessage("", contactChatappId, Uname, number, ContactString);
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (requestCode == RESULT_SHARE_LOCATION && resultCode == RESULT_OK && null != data) {
//            Place place = PlacePicker.getPlace(ChatViewActivityRealcopy.this, data);
//            if (place != null) {
//                LatLng latlng = place.getLatLng();
//                if (place.getAddress() != null) {
//                    String address = place.getAddress().toString().trim();
//                    String name = place.getName().toString();
//                    if (name.length() > 0 && name.charAt(0) == '(') {
//                        String[] parts = name.split(",");
//                        name = parts[0].trim() + "," + parts[1].trim();
//                    }
//
//                    if (latlng != null) {
//                        sendLocationMessage(latlng, name, address);
//                    }
//                }
//            }
//
//        } else if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            try {
//                Bundle bundle = data.getExtras();
//                try {
//                    ArrayList<Imagepath_caption> pathlist = (ArrayList<Imagepath_caption>) bundle.getSerializable("pathlist");
//                    for (int i = 0; i < pathlist.size(); i++) {
//                        String path = pathlist.get(i).getPath();
//                        String caption = pathlist.get(i).getCaption();
//                        sendImageChatMessage(path, caption, false);
//                    }
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        else if (requestCode == RESULT_LOAD_VIDEO && resultCode == RESULT_OK) {
//            ArrayList<Imagepath_caption> pathlist = new ArrayList<>();
//            Bundle bundle = data.getExtras();
//            try {
//                pathlist = (ArrayList<Imagepath_caption>) bundle.getSerializable("pathlist");                for (int i = 0; i < pathlist.size(); i++) {
//                    String path = pathlist.get(i).getPath();
//                    String caption = pathlist.get(i).getCaption();
//                    sendVideoChatMessage(path, caption, false);
//                }
//            } catch (NullPointerException e) {
//                e.printStackTrace();
//            }
//        }
//        else if (requestCode == RESULT_WALLPAPER && resultCode == RESULT_OK && null != data) {
//            try {
//                Uri selectedImageUri = data.getData();
//                String[] filePathColumn = {MediaStore.Images.Media.DATA};
//                Cursor cursor = getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
//                cursor.moveToFirst();
//                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//                imgDecodableString = cursor.getString(columnIndex);
//                if (imgDecodableString == null) {
//                    imgDecodableString = getRealFilePath(data);
//                }
//                cursor.close();
//
//                session.putgalleryPrefs(imgDecodableString);
//                Bitmap bitmap = BitmapFactory.decodeFile(imgDecodableString);
//                background.setImageBitmap(bitmap);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (requestCode == EXIT_GROUP_REQUEST_CODE) { // Finish activity when exit group by user
//            if (resultCode == RESULT_OK && data != null) {
//                boolean isExitFromGroup = data.getBooleanExtra("exitFromGroup", false);
//                boolean ismuteGroupchange = data.getBooleanExtra("ismutechange", false);
//                boolean isgroupemty = data.getBooleanExtra("isgroupempty", false);
//                if (isExitFromGroup) {
//                    finish();
//                }
//                if (ismuteGroupchange) {
//                    finish();
//                }
//                if (isgroupemty) {
//                    text_lay_out.setVisibility(View.GONE);
//                    rlSend.setVisibility(View.GONE);
//                    RelativeLayout_group_delete.setVisibility(View.VISIBLE);
//                }
//            }
//        } else if (requestCode == MUTE_ACTIVITY) { // Finish activity when exit group by user
//            if (resultCode == RESULT_OK && data != null) {
//                boolean ismutechange = data.getBooleanExtra("muteactivity", false);
//                if (ismutechange) {
//                    finish();
//                }
//            }
//        } else if (requestCode == REQUEST_CODE_FORWARD_MSG) {
//            if (resultCode == RESULT_OK) {
//                boolean isMultiForward = data.getBooleanExtra("MultiForward", false);
//                if (isMultiForward) {
//                    reloadAdapter();
//                    showUnSelectedActions();
//                }
//            } else {
//                if (selectedChatItems != null && selectedChatItems.size() > 0) {
//                    for (MessageItemChat msgItem : selectedChatItems) {
//                        int index = mChatData.indexOf(msgItem);
//                        if (index > -1) {
//                            mChatData.get(index).setSelected(true);
//                        }
//                    }
//                    mAdapter.notifyDataSetChanged();
//                } else {
//                    showUnSelectedActions();
//                }
//            }
//        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
//            try {
//                ArrayList<Imagepath_caption> pathlist = new ArrayList<>();
//                Bundle bundle = data.getExtras();
//                try {
//                    pathlist = (ArrayList<Imagepath_caption>) bundle.getSerializable("pathlist");
//
//                    String path = pathlist.get(0).getPath();
//                    String caption = pathlist.get(0).getCaption();
//
//                    sendImageChatMessage(path, caption, false);
//
//
//                } catch (NullPointerException e) {
//                    e.printStackTrace();
//                }
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        } else if (requestCode == REQUEST_CODE_DOCUMENT && resultCode == RESULT_OK) {
//            Uri fileUri = data.getData();
//
////            sendDocumentMessage(fileUri.getPath());
//        } else if (requestCode == REQUEST_SELECT_AUDIO && resultCode == RESULT_OK) {
//            String fileName = data.getStringExtra("FileName");
//            String filePath = data.getStringExtra("FilePath");
//            String duration = data.getStringExtra("Duration");
//            sendAudioMessage(filePath, duration, MessageFactory.AUDIO_FROM_ATTACHMENT);
//        } else if (requestCode == FilePickerConst.REQUEST_CODE_DOC) {
//            if (resultCode == Activity.RESULT_OK && data != null) {
//                ArrayList<String> docPaths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_DOCS);
//                if (docPaths.size() > 0) {
//                    sendDocumentMessage(docPaths.get(0));
//                }
//            }
//        } else if (requestCode == PICK_CONTACT_REQUEST && resultCode == RESULT_OK) {
//            loadContactInfo(data.getData());
//        } else if (requestCode == ADD_CONTACT && resultCode == RESULT_OK) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    llAddBlockContact.setVisibility(View.GONE);
//                    ChatappContactsService.savedNumber = receiverNameText.getText().toString();
//                    ChatappContactsService.startContactService(ChatViewActivityRealcopy.this, false);
//                    ChatappContactsService.setBroadCastSavedName(new ChatappContactsService.BroadCastSavedName() {
//                        @Override
//                        public void savedName(final String name) {
//                            receiverNameText.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    receiverNameText.setText(name);
//
//                                }
//                            });
//
//
//                        }
//                    });
//                }
//            });
//
//        }
//    }
//
//    private synchronized void reloadAdapter() {
//
//        final ArrayList<MessageItemChat> dbItems = db.selectAllMessagesWithLimit(docId, chatType,
//                "", MessageDbController.MESSAGE_SELECTION_LIMIT);
//        Collections.sort(dbItems, msgComparator);
//
//        mChatData.clear();
//        mChatData.addAll(dbItems);
//        notifyDatasetChange();
//    }
//
//    private void loadContactInfo(Uri contactUri) {
//
//        AsyncTask<Uri, Void, Boolean> task = new AsyncTask<Uri, Void, Boolean>() {
//
//            @Override
//            protected Boolean doInBackground(Uri... uris) {
//                return doesContactContainHomeEmail(uris[0]);
//            }
//
//            @Override
//            protected void onPostExecute(Boolean exists) {
//                if (exists) {
//                    updateContact();
//                } else {
//                    insertEmailContact();
//                }
//            }
//        };
//
//        task.execute(contactUri);
//    }
//
//    private Boolean doesContactContainHomeEmail(Uri contactUri) {
//        boolean returnValue = false;
//        Cursor mContactCursor = getContentResolver().query(contactUri, null, null, null, null);
//
//        try {
//            if (mContactCursor.moveToFirst()) {
//                String mContactId = getCursorString(mContactCursor,
//                        ContactsContract.Contacts._ID);
//
//                Cursor mRawContactCursor = getContentResolver().query(
//                        ContactsContract.RawContacts.CONTENT_URI,
//                        null,
//                        ContactsContract.Data.CONTACT_ID + " = ?",
//                        new String[]{mContactId},
//                        null);
//
//                Log.v("RawContact", "Got RawContact Cursor");
//
//                try {
//                    ArrayList<String> mRawContactIds = new ArrayList<String>();
//                    while (mRawContactCursor.moveToNext()) {
//                        String rawId = getCursorString(mRawContactCursor, ContactsContract.RawContacts._ID);
//                        Log.v("RawContact", "ID: " + rawId);
//                        mRawContactIds.add(rawId);
//                    }
//
//                    for (String rawId : mRawContactIds) {
//                        // Make sure the "last checked" RawContactId is set locally for use in insert & update.
//                        mRawContactId = rawId;
//                        Cursor mDataCursor = getContentResolver().query(
//                                ContactsContract.Data.CONTENT_URI,
//                                null,
//                                ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ? AND " + ContactsContract.CommonDataKinds.Email.TYPE + " = ?",
//                                new String[]{mRawContactId, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE, String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)},
//                                null);
//
//                        if (mDataCursor.getCount() > 0) {
//                            mDataCursor.moveToFirst();
//                            mDataId = getCursorString(mDataCursor, ContactsContract.Data._ID);
//                            Log.v("Data", "Found data item with MIMETYPE and EMAIL.TYPE");
//                            mDataCursor.close();
//                            returnValue = true;
//                            break;
//                        } else {
//                            Log.v("Data", "Data doesn't contain MIMETYPE and EMAIL.TYPE");
//                            mDataCursor.close();
//                        }
//                        returnValue = false;
//                    }
//                } finally {
//                    mRawContactCursor.close();
//                }
//            }
//        } catch (Exception e) {
//            Log.w("UpdateContact", e.getMessage());
//            for (StackTraceElement ste : e.getStackTrace()) {
//                Log.w("UpdateContact", "\t" + ste.toString());
//            }
//            throw new RuntimeException();
//        } finally {
//            mContactCursor.close();
//        }
//
//        return returnValue;
//    }
//
//    private String getCursorString(Cursor cursor, String columnName) {
//        int index = cursor.getColumnIndex(columnName);
//        if (index != -1) return cursor.getString(index);
//        return null;
//    }
//
//    public void insertEmailContact() {
//        try {
//            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//
//            ops.add(ContentProviderOperation
//                    .newInsert(ContactsContract.Data.CONTENT_URI)
//                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, mRawContactId)
//                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, receiverMsisdn)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)
//                    // .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, "Phone")
//                    .build());
//
//
//            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//
//        } catch (Exception e) {
//            // Display warning
//            Log.w("UpdateContact", e.getMessage());
//            for (StackTraceElement ste : e.getStackTrace()) {
//                Log.w("UpdateContact", "\t" + ste.toString());
//            }
//            Context ctx = getApplicationContext();
//            int duration = Toast.LENGTH_SHORT;
//            Toast toast = Toast.makeText(ctx, "Update failed", duration);
//            e.printStackTrace();
//            toast.show();
//        }
//    }
//
//    public void updateContact() {
//        try {
//            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
//
//            ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
//                    .withSelection(ContactsContract.Data.RAW_CONTACT_ID + " = ?", new String[]{mRawContactId})
//                    .withSelection(ContactsContract.Data._ID + " = ?", new String[]{mDataId})
//                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, receiverMsisdn)
//                    .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)
//
//                    .build());
//
//
//            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
//
//        } catch (Exception e) {
//            // Display warning
//            Log.w("UpdateContact", e.getMessage());
//            for (StackTraceElement ste : e.getStackTrace()) {
//                Log.w("UpdateContact", "\t" + ste.toString());
//            }
//            Context ctx = getApplicationContext();
//            int duration = Toast.LENGTH_SHORT;
//            Toast toast = Toast.makeText(ctx, "Update failed", duration);
//            toast.show();
//        }
//    }
//
//    @TargetApi(Build.VERSION_CODES.KITKAT)
//    private String getRealFilePath(Intent data) {
//        Uri selectedImage = data.getData();
//        String wholeID = DocumentsContract.getDocumentId(selectedImage);
//        // Split at colon, use second item in the array
//        String id = wholeID.split(":")[1];
//        String[] column = {MediaStore.Images.Media.DATA};
//        // where id is equal to
//        String sel = MediaStore.Images.Media._ID + "=?";
//        Cursor cursor = getContentResolver().
//                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        column, sel, new String[]{id}, null);
//        String filePath = "";
//        int columnIndex = 0;
//        if (cursor != null) {
//            columnIndex = cursor.getColumnIndex(column[0]);
//        }
//        if (cursor != null && cursor.moveToFirst()) {
//            filePath = cursor.getString(columnIndex);
//        }
//        cursor.close();
//        return filePath;
//    }
//
//
//    public void sendImageChatMessage(String imgPath, String caption, boolean isRetry) {
//        if (imgPath != null) {
//            String data_at = caption;
//            if (ImagecaptionActivity.at_memberlist != null && ImagecaptionActivity.at_memberlist.size() >= 0)
//                for (GroupMembersPojo groupMembersPojo : ImagecaptionActivity.at_memberlist) {
//                    String userName = "@" + groupMembersPojo.getContactName();
//
//                    data_at = data_at.replace(userName, TextMessage.TAG_KEY + groupMembersPojo.getUserId() + TextMessage.TAG_KEY);
//                }
//            PictureMessage message = (PictureMessage) MessageFactory.getMessage(MessageFactory.picture, this);
//
//            MessageItemChat item = null;
//            boolean canSent = false;
//
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            BitmapFactory.decodeFile(imgPath, options);
//            int imageHeight = options.outHeight;
//            int imageWidth = options.outWidth;
//
//            if (isGroupChat) {
//                if (enableGroupChat) {
//                    canSent = true;
//                    message.getGroupMessageObject(to, imgPath, receiverNameText.getText().toString());
//                    item = message.createMessageItem(true, caption, imgPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                            mReceiverId, receiverNameText.getText().toString(), imageWidth, imageHeight, isExpiry, expireTime);
//                    item.setGroup(true);
//                    item.setGroupName(receiverNameText.getText().toString());
//                } else {
//                    Toast.makeText(ChatViewActivityRealcopy.this, "You are not a member in this group", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                canSent = true;
//                if (isBroadcast) {
//                    message.setIDforBroadcast(mReceiverId);
//                } else {
//                    message.getMessageObject(to, imgPath, false);
//                }
//                item = message.createMessageItem(true, caption, imgPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                        mReceiverId, receiverNameText.getText().toString(), imageWidth, imageHeight, isExpiry, expireTime);
//                item.setGroup(false);
//                item.setConvId(mConvId);
//                item.setSenderMsisdn(receiverMsisdn);
//
//            }
//
//            if (item != null) {
//                item.setCaption(caption);
//            }
//            if (canSent) {
//
//                try {
//                    File file = new File(imgPath);
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    Bitmap compressBmp = Compressor.getDefault(context).compressToBitmap(file);
//                    compressBmp = Bitmap.createScaledBitmap(compressBmp, 100, 100, false);
//                    compressBmp.compress(Bitmap.CompressFormat.JPEG, 30, out);
//                    byte[] thumbArray = out.toByteArray();
//                    out.close();
//
//                    String thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
//                    CryptLib cryptLib = null;
//                    try {
//                        cryptLib = new CryptLib();
//                        thumbData = cryptLib.encryptPlainTextWithRandomIV(thumbData, kyGn());
//
//                    } catch (NoSuchAlgorithmException e) {
//                        e.printStackTrace();
//                    } catch (NoSuchPaddingException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    item.setThumbnailData(thumbData);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (!isRetry) {
//                    if (isBroadcast) {
//                        item.setDeliveryStatus("1");
//                    }
//                    db.updateChatMessage(item, chatType);
//                    mChatData.add(item);
//                    notifyDatasetChange();
//                }
//
//                if (isBroadcast) {
//                    forwardToBroadcast(item, chatappContactModels);
//                } else {
//                    String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(imgPath);
//                    String imgName = item.getMessageId() + fileExtension;
//                    String docId;
//                    if (isGroupChat) {
//                        docId = mCurrentUserId + "-" + to + "-g";
//                    } else {
//                        docId = mCurrentUserId + "-" + to;
//                    }
//
//                    JSONObject uploadObj = (JSONObject) message.createImageUploadObject(item.getMessageId(), docId, imgName, imgPath,
//                            receiverNameText.getText().toString(), data_at, chatType, false);
//                    try {
//                        uploadObj.put("is_expiry", isExpiry);
//                        uploadObj.put("expiry_time", expireTime);
//                    } catch (JSONException ex) {
//                        ex.printStackTrace();
//                    }
////
//                    uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
//                }
//            }
//        }
//    }
//
//    private void sendLocationMessage(final LatLng latlng, final String addressName, final String address) {
//
//        final String thumbUrl = "https://maps.googleapis.com/maps/api/staticmap?center=" + latlng.latitude + "," + latlng.longitude
//                + "&zoom=15&size=180x180&maptype=roadmap&markers=color:red%7Clabel:N%7C" + latlng.latitude
//                + "," + latlng.longitude;
////        final String thumbUrl = "https://static.pexels.com/photos/20974/pexels-photo.jpg";
//
////        final String thumbUrl = "https://maps.googleapis.com/maps/api/staticmap?center=40.714728,-73.998672&zoom=12&size=400x400&key=AIzaSyCm0zmTw3_oQNR6x-gdD1Y3175BOAfUa2o";
//
//        initProgress("Loading address...", true);
//        showProgressDialog();
//
//        ImageLoader imageLoader = CoreController.getInstance().getImageLoader();
//        imageLoader.get(thumbUrl, new ImageLoader.ImageListener() {
//            @Override
//            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
//                Bitmap bitmap = imageContainer.getBitmap();
//                //use bitmap
//                if (bitmap != null) {
//
//                    hideProgressDialog();
//
//                    Bitmap newBmp = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
//
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    newBmp.compress(Bitmap.CompressFormat.JPEG, 50, out);
//                    byte[] thumbArray = out.toByteArray();
//                    try {
//                        out.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    String thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
//                    if (thumbData != null) {
//                        thumbData = thumbData.replace("\n", "");
//                        if (!thumbData.startsWith("data:image/jpeg;base64,")) {
//                            thumbData = "data:image/jpeg;base64," + thumbData;
//                        }
//
//                        String point = latlng.latitude + "," + latlng.longitude;
//
//
//                        String urlTemp = "https://maps.google.com/maps?q=" + point
//                                + " (" + addressName + ")&amp;z=15&amp;hl=en";
//
//                        try {
//
//                            CryptLib cryptLib = new CryptLib();
//
//                            String url = cryptLib.encryptPlainTextWithRandomIV(urlTemp, kyGn());
//
//
//                            //    String url=urlTemp;
//
//                            SendMessageEvent messageEvent = new SendMessageEvent();
//                            LocationMessage message = (LocationMessage) MessageFactory.getMessage(MessageFactory.location, ChatViewActivityRealcopy.this);
//
//                            JSONObject msgObj;
//                            String receiverName = receiverNameText.getText().toString();
//                            if (isGroupChat) {
//                                messageEvent.setEventName(SocketManager.EVENT_GROUP);
//                                msgObj = (JSONObject) message.getGroupMessageObject(to, point, receiverName);
//
//                            } else if (isBroadcast) {
//                                message.setIDforBroadcast(mReceiverId);
//                                msgObj = null;
//                            } else {
//                                msgObj = (JSONObject) message.getMessageObject(to, point, false);
//                                messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
//                            }
//
//                            MessageItemChat item = message.createMessageItem(true, point, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                                    mReceiverId, receiverName, addressName, address, url, thumbUrl, thumbData, isExpiry, expireTime);
//                            if (isBroadcast) {
//                                forwardToBroadcast(item, chatappContactModels);
//                            } else {
//                                msgObj = (JSONObject) message.getLocationObject(msgObj, addressName, address, url, thumbUrl, thumbData);
//                                try {
//                                    msgObj.put("is_expiry", isExpiry);
//                                    msgObj.put("expiry_time", expireTime);
//                                } catch (JSONException ex) {
//                                    ex.printStackTrace();
//                                }
//                                messageEvent.setMessageObject(msgObj);
//                            }
//
//                            if (isGroupChat) {
//                                if (enableGroupChat) {
//                                    item.setGroupName(receiverNameText.getText().toString());
//                                    item.setGroup(true);
//                                    db.updateChatMessage(item, chatType);
//                                    mChatData.add(item);
//
//                                    EventBus.getDefault().post(messageEvent);
//                                    notifyDatasetChange();
//                                } else {
//                                    Toast.makeText(ChatViewActivityRealcopy.this, "You are not a member in this group", Toast.LENGTH_SHORT).show();
//                                }
//                            } else {
//                                item.setSenderMsisdn(receiverMsisdn);
//                                item.setSenderName(receiverNameText.getText().toString());
//                                item.setGroup(false);
//                                item.setConvId(mConvId);
//                                if (!isBroadcast) {
//                                    EventBus.getDefault().post(messageEvent);
//                                } else {
//
//                                    item.setDeliveryStatus("1");
//                                }
//                                db.updateChatMessage(item, chatType);
//                                mChatData.add(item);
//                                notifyDatasetChange();
//                            }
//                        }
//                        catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//                    }
//
//                }
//            }
//
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                hideProgressDialog();
//                Toast.makeText(ChatViewActivityRealcopy.this, "Try again", Toast.LENGTH_SHORT).show();
//            }
//        });
//
//    }
//
//    private void sendContactMessage(String data, String contactChatappId, String contactName, String contactNumber, String contactDetail) {
//
//        SendMessageEvent messageEvent = new SendMessageEvent();
//        ContactMessage message = (ContactMessage) MessageFactory.getMessage(MessageFactory.contact, this);
//
//        JSONObject msgObj;
//        if (isGroupChat) {
//            messageEvent.setEventName(SocketManager.EVENT_GROUP);
//            msgObj = (JSONObject) message.getGroupMessageObject(to, data, receiverNameText.getText().toString(), contactChatappId, contactName, contactNumber, contactDetail);
//
//        } else {
//            messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
//            msgObj = (JSONObject) message.getMessageObject(to, data, contactChatappId, contactName, contactNumber, contactDetail, false);
//        }
//
//        messageEvent.setMessageObject(msgObj);
//        MessageItemChat item = message.createMessageItem(true, data, MessageFactory.DELIVERY_STATUS_NOT_SENT, mReceiverId,
//                receiverNameText.getText().toString(), contactName, contactNumber, contactChatappId, contactDetail);
//        item.setGroupName(receiverNameText.getText().toString());
//        item.setSenderMsisdn(receiverMsisdn);
//        item.setContactchatappId(contactChatappId);
//      /*  String image = ("uploads/users/").concat(item.get_id()).concat(".jpg");
//        String path =  Constants.SOCKET_IP.concat(image);*/
//        item.setAvatarImageUrl(receiverAvatar);
//        item.setDetailedContacts(contactDetail);
//
//        if (isGroupChat && enableGroupChat) {
//            db.updateChatMessage(item, chatType);
//            mChatData.add(item);
//            notifyDatasetChange();
//            EventBus.getDefault().post(messageEvent);
//        } else if (!isGroupChat) {
//            db.updateChatMessage(item, chatType);
//            mChatData.add(item);
//            notifyDatasetChange();
//            EventBus.getDefault().post(messageEvent);
//        }
//    }
//
//    private void sendAudioMessage(String filePath, String duration, int audioFrom) {
//
//
//        AudioMessage message = (AudioMessage) MessageFactory.getMessage(MessageFactory.audio, this);
//        MessageItemChat item = null;
//        boolean canSent = false;
//
//        if (isGroupChat) {
//            if (enableGroupChat) {
//                canSent = true;
//                message.getGroupMessageObject(to, filePath, receiverNameText.getText().toString());
//                item = message.createMessageItem(true, filePath, duration, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                        mReceiverId, receiverNameText.getText().toString(), audioFrom, isExpiry, expireTime);
//                item.setGroupName(receiverNameText.getText().toString());
//                item.setGroup(true);
//            } else {
//                Toast.makeText(ChatViewActivityRealcopy.this, "You are not a member in this group", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            canSent = true;
//
//
//            if (isBroadcast) {
//                message.setIDforBroadcast(mReceiverId);
//
//            } else {
//                message.getMessageObject(to, filePath, false);
//            }
//            item = message.createMessageItem(true, filePath, duration, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                    mReceiverId, receiverNameText.getText().toString(), audioFrom, isExpiry, expireTime);
//            item.setSenderMsisdn(receiverMsisdn);
//            item.setaudiotype(audioFrom);
//            item.setGroup(false);
//            item.setConvId(mConvId);
//        }
//
//        if (canSent) {
//            if (isBroadcast) {
//                item.setDeliveryStatus("1");
//            }
//            db.updateChatMessage(item, chatType);
//            mChatData.add(item);
//            notifyDatasetChange();
//
//            if (isBroadcast) {
//                forwardToBroadcast(item, chatappContactModels);
//            } else {
//                String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(filePath);
//                String audioName = item.getMessageId() + fileExtension;
//                String docId;
//                if (isGroupChat) {
//                    docId = mCurrentUserId + "-" + to + "-g";
//                } else {
//                    docId = mCurrentUserId + "-" + to;
//                }
//
//                JSONObject uploadObj = (JSONObject) message.createAudioUploadObject(item.getMessageId(), docId,
//                        audioName, filePath, duration, receiverNameText.getText().toString(), audioFrom, chatType, false);
//                try {
//                    uploadObj.put("is_expiry", isExpiry);
//                    uploadObj.put("expiry_time", expireTime);
//                } catch (JSONException ex) {
//                    ex.printStackTrace();
//                }
//
//                uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
//            }
//        }
//    }
//
//    public void sendVideoChatMessage(String videoPath, String caption, boolean isRetry) {
//        if (videoPath != null) {
//            String data_at = caption;
//            if (ImagecaptionActivity.at_memberlist != null && ImagecaptionActivity.at_memberlist.size() >= 0)
//                for (GroupMembersPojo groupMembersPojo : ImagecaptionActivity.at_memberlist) {
//                    String userName = "@" + groupMembersPojo.getContactName();
//
//                    data_at = data_at.replace(userName, TextMessage.TAG_KEY + groupMembersPojo.getUserId() + TextMessage.TAG_KEY);
//                }
//            VideoMessage message = (VideoMessage) MessageFactory.getMessage(MessageFactory.video, this);
//
//            MessageItemChat item = null;
//            boolean canSent = false;
//
//            if (isGroupChat) {
//                if (enableGroupChat) {
//                    canSent = true;
//                    message.getGroupMessageObject(to, videoPath, receiverNameText.getText().toString());
//                    item = message.createMessageItem(true, videoPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                            mReceiverId, receiverNameText.getText().toString(), caption, isExpiry, expireTime);
//                    item.setGroupName(receiverNameText.getText().toString());
//                    item.setGroup(true);
//                } else {
//                    Toast.makeText(ChatViewActivityRealcopy.this, "You are not a member in this group", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                canSent = true;
//                if (isBroadcast) {
//                    message.setIDforBroadcast(mReceiverId);
//                } else {
//                    message.getMessageObject(to, videoPath, false);
//                }
//                item = message.createMessageItem(true, videoPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                        mReceiverId, receiverNameText.getText().toString(), caption, isExpiry, expireTime);
//                item.setSenderMsisdn(receiverMsisdn);
//                item.setGroup(false);
//                item.setConvId(mConvId);
//            }
//
//
//            if (canSent) {
//
//                Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                thumbBmp.compress(Bitmap.CompressFormat.PNG, 10, out);
//                byte[] thumbArray = out.toByteArray();
//                try {
//                    out.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                String thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
//                if (thumbData != null) {
//                    CryptLib cryptLib = null;
//                    try {
//                        cryptLib = new CryptLib();
//                        thumbData = cryptLib.encryptPlainTextWithRandomIV(thumbData, kyGn());
//
//                    } catch (NoSuchAlgorithmException e) {
//                        e.printStackTrace();
//                    } catch (NoSuchPaddingException e) {
//                        e.printStackTrace();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//
//                    item.setThumbnailData(thumbData);
//                }
//                item.setCaption(caption);
//
//                if (!isRetry) {
//                    if (isBroadcast) {
//                        item.setDeliveryStatus("1");
//                    }
//                    db.updateChatMessage(item, chatType);
//                    mChatData.add(item);
//                    notifyDatasetChange();
//                }
//                if (isBroadcast) {
//                    forwardToBroadcast(item, chatappContactModels);
//                } else {
//                    String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(videoPath);
//                    String videoName = item.getMessageId() + fileExtension;
//                    String docId;
//                    if (isGroupChat) {
//                        docId = mCurrentUserId + "-" + to + "-g";
//                    } else {
//                        docId = mCurrentUserId + "-" + to;
//                    }
//
//                    JSONObject uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
//                            videoName, videoPath, receiverNameText.getText().toString(), data_at, chatType, false);
//                    try {
//                        uploadObj.put("is_expiry", isExpiry);
//                        uploadObj.put("expiry_time", expireTime);
//                    } catch (JSONException ex) {
//                        ex.printStackTrace();
//                    }
//                    uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
//                }
//            }
//        }
//    }
//
//    private void sendDocumentMessage(String filePath) {
//
//        DocumentMessage message = (DocumentMessage) MessageFactory.getMessage(MessageFactory.document, this);
//
//        MessageItemChat item = null;
//        boolean canSent = false;
//
//        if (isGroupChat) {
//            if (enableGroupChat) {
//                canSent = true;
//                message.getGroupMessageObject(to, filePath, receiverNameText.getText().toString());
//                item = message.createMessageItem(true, filePath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                        mReceiverId, receiverNameText.getText().toString(), isExpiry, expireTime);
//                item.setGroupName(receiverNameText.getText().toString());
//                item.setGroup(true);
//            } else {
//                Toast.makeText(ChatViewActivityRealcopy.this, "You are not a member in this group", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            canSent = true;
//            if (isBroadcast) {
//                message.setIDforBroadcast(mReceiverId);
//            } else {
//                message.getMessageObject(to, filePath, false);
//            }
//            item = message.createMessageItem(true, filePath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                    mReceiverId, receiverNameText.getText().toString(), isExpiry, expireTime);
//            item.setSenderMsisdn(receiverMsisdn);
//            item.setGroup(false);
//            item.setConvId(mConvId);
//        }
//
//        if (canSent) {
//            if (isBroadcast) {
//                item.setDeliveryStatus("1");
//            }
//            db.updateChatMessage(item, chatType);
//            mChatData.add(item);
//            notifyDatasetChange();
//
//            if (isBroadcast) {
//                forwardToBroadcast(item, chatappContactModels);
//            } else {
//                String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(filePath);
//                String docName = item.getMessageId() + fileExtension;
//                String docId;
//                if (isGroupChat) {
//                    docId = mCurrentUserId + "-" + to + "-g";
//                } else {
//                    docId = mCurrentUserId + "-" + to;
//                }
//
//                JSONObject uploadObj = (JSONObject) message.createDocUploadObject(item.getMessageId(), docId,
//                        docName, filePath, receiverNameText.getText().toString(), chatType, false);
//                try {
//                    uploadObj.put("is_expiry", isExpiry);
//                    uploadObj.put("expiry_time", expireTime);
//                } catch (JSONException ex) {
//                    ex.printStackTrace();
//                }
//                uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
//            }
//        }
//    }
//
//    private void loadackMesssage(ReceviceMessageEvent event) {
//        final Object[] array = event.getObjectsArray();
//
//        //   loadFromDB();
//
//        new Thread() {
//            public void run() {
//                try {
//                    JSONObject object = new JSONObject(array[0].toString());
//                    String from = object.getString("from");
//                    if (from.equalsIgnoreCase(mCurrentUserId)) {
//                        String to = object.getString("to");
//                        String chatId = object.getString("doc_id");
//                        String status = object.getString("status");
//                        String timeStamp = object.getString("currenttime");
//                        String secretType = object.getString("secret_type");
//
//                        String docId = from + "-" + to;
//                        String[] splitIds = chatId.split("-");
//                        String id = splitIds[2];
//                        String msgId = docId + "-" + id;
//
//                        if (secretType.equalsIgnoreCase("yes")) {
//                            docId = docId + "-secret";
//                        }
//                        MessageDbController db = CoreController.getDBInstance(getApplicationContext());
//
//
//                        ArrayList<MessageItemChat> ackMessageItem = db.getMessageItem(docId, msgId, status, timeStamp, false);
//
//
//                        for(int i=0;i<ackMessageItem.size();i++) {
//
//                            if (status.equals("3")) {
//                                if (ackMessageItem.get(i).getIsExpiry() != null) {
//                                    if (ackMessageItem.get(i).getIsExpiry().equals("1")) {
//                                        long timer = Long.parseLong(ackMessageItem.get(i).getExpiryTime()) * 1000;
//                                        onTimer(timer, ackMessageItem.get(i));
//                                    }
//                                }
//                            }
//                        }
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }.start();
//
//    }
//
//    private void loadMessage(ReceviceMessageEvent event) {
//        Object[] array = event.getObjectsArray();
//        try {
//            JSONObject objects = new JSONObject(array[0].toString());
//
//            int normal_Offline = 0;
//            if (objects.has("is_deleted_everyone")) {
//                normal_Offline = objects.getInt("is_deleted_everyone");
//            }
//
//            String type = String.valueOf(objects.getString("type"));
//            String secretType = objects.getString("secret_type");
//            if (secretType.equalsIgnoreCase("no")) {
//                IncomingMessage incomingMsg = new IncomingMessage(ChatViewActivityRealcopy.this);
//                MessageItemChat item = incomingMsg.loadSingleMessage(objects);
//                String from = objects.getString("from");
//                String to = objects.getString("to");
//
//                session.Removemark(from);
//
//                String id = objects.getString("msgId");
//                String convId = objects.getString("convId");
//
//                String doc_id;
//                if (objects.has("docId")) {
//                    doc_id = objects.getString("docId");
//                } else {
//                    doc_id = objects.getString("doc_id");
//                }
//                String uniqueID = "";
//                if (from.equalsIgnoreCase(mCurrentUserId)) {
//                    uniqueID = from + "-" + to;
//                    item.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_SENT);
//                } else if (to.equalsIgnoreCase(mCurrentUserId)) {
//                    uniqueID = to + "-" + from;
//
//                    if (sessionManager.canSendReadReceipt()) {
//                        uniqueID = to + "-" + from;
//                        if (from.equalsIgnoreCase(mReceiverId)) {
//                            if(isChatPage) {
//                                sendAckToServer(from, doc_id, "" + id);
//                            }
//                            else
//                            {
//                                ackMsgid=true;
//                            }
//                        }
//                    }
//                    if(isChatPage) {
//                        item.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_READ);
//                    }
//                    else
//                    {
//                        item.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);
//                    }
//
//
//
////                    if(item.getIsExpiry()!=null) {
////                        if (item.getIsExpiry().equals("1")) {
////                            long timer = Long.parseLong(item.getExpiryTime()) * 1000;
////                            onTimer(timer, item);
////                        }
////                    }
//
//                    getUserDetails(from);
//                }
//
//                item.setMessageId(uniqueID + "-" + id);
//                if (secretType.equalsIgnoreCase("yes")) {
//                    uniqueID = uniqueID + "-" + MessageFactory.CHAT_TYPE_SECRET;
//                    String timer = objects.getString("incognito_timer");
//                    item.setSecretTimer(timer);
//                    item.setSecretTimeCreatedBy(from);
//                }
//
//                if (session.getPrefsNameintoneouttone()) {
//                    try {
//                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                        r.play();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (from.equalsIgnoreCase(mReceiverId)) {
//                    mConvId = convId;
//
//                    if (isChatPage) {
//                        changeBadgeCount(uniqueID);
//                    }
//                    sendChatViewedStatus();
//
//                    if(String.valueOf(objects.getString("type")).equals(String.valueOf(MessageFactory.missed_call)))
//                    {
//                        if (lastvisibleitempostion < mChatData.size() - 5) {
//                           if(mChatData.size()!=0 &&!mChatData.get(mChatData.size()-1).getMessageId().equals(uniqueID + "-" + id)) {
//                               unreadmsgcount++;
//                               unreadmessage();
//                               mChatData.add(item);
//                               mAdapter.notifyDataSetChanged();
//                           }
//                        } else {
//                            if(mChatData.size()!=0 &&!mChatData.get(mChatData.size()-1).getMessageId().equals(uniqueID + "-" + id)) {
//                                mChatData.add(item);
//                                notifyDatasetChange();
//                            }
//                        }
//                    }
//                    else {
//
//                    if (lastvisibleitempostion < mChatData.size() - 5) {
//                        unreadmsgcount++;
//                        unreadmessage();
//                        mChatData.add(item);
//                        mAdapter.notifyDataSetChanged();
//                    } else {
//                        mChatData.add(item);
//                        notifyDatasetChange();
//                    }
//                     }
//
//
//                    //-------------Delete Chat-----------------
//
//                    if (normal_Offline == 1) {
//                        String new_docId, new_msgId, new_type, new_recId, new_convId;
//                        try {
//                            String fromId = objects.getString("from");
//                            if (!fromId.equalsIgnoreCase(mCurrentUserId)) {
//                                String chat_id = (String) objects.get("docId");
//                                String[] ids = chat_id.split("-");
//
//                                new_type = objects.getString("chat_type");
////                                new_recId = objects.getString("recordId");
////                                new_convId = objects.getString("convId");
//
//                                if (new_type.equalsIgnoreCase("single")) {
//                                    new_docId = ids[1] + "-" + ids[0];
//                                    new_msgId = new_docId + "-" + ids[2];
//
//                                    for (int i = 0; i < mChatData.size(); i++) {
//                                        if (mChatData.get(i).getMessageId().equalsIgnoreCase(new_msgId)) {
//
////                                            mChatData.get(i).setMessageType("Delete Others");
//                                            mChatData.get(i).setMessageType(MessageFactory.DELETE_OTHER + "");
//                                            mChatData.get(i).setIsSelf(false);
//                                            mAdapter.notifyDataSetChanged();
//
//                                            db.deleteSingleMessage(new_docId, new_msgId, new_type, "other");
//                                            db.deleteChatListPage(new_docId, new_msgId, new_type, "other");
//                                            break;
//                                        }
//                                    }
//
//                                } else {
//
//                                    new_docId = fromId + "-" + ids[1] + "-g";
//                                    new_msgId = docId + "-" + ids[3];
//
//                                    String groupAndMsgId = ids[1] + "-g-" + ids[3];
//
//
//                                    if (mChatData.size() > 0) {
//                                        for (int i = 0; i < mChatData.size(); i++) {
//                                            if (mChatData.get(i).getMessageId().contains(groupAndMsgId)) {
//                                                if (i > -1 && mChatData.get(i).isMediaPlaying()) {
//                                                    mAdapter.stopAudioOnMessageDelete(i);
//                                                }
//
////                                                mChatData.get(i).setMessageType("Delete Others");
//                                                mChatData.get(i).setMessageType(MessageFactory.DELETE_OTHER + "");
//                                                mChatData.get(i).setIsSelf(false);
//
//                                                mAdapter.notifyDataSetChanged();
//
//                                                db.deleteSingleMessage(groupAndMsgId, new_msgId, type, "other");
//                                                db.deleteChatListPage(groupAndMsgId, new_msgId, type, "other");
//                                                break;
//                                            }
//                                        }
//                                    }
//
//
//                                }
//                            }
//                        } catch (JSONException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//
//                }
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void sendChatViewedStatus() {
//        long currentTime = Calendar.getInstance().getTimeInMillis();
//        long timeDiff = currentTime - lastViewStatusSentAt;
//
//        if (timeDiff > 4000) {
//            lastViewStatusSentAt = currentTime;
//            if (mConvId != null && !mConvId.equals("")) {
//                sendViewedStatusToWeb(mConvId);
//            }
//        }
//    }
//
//    private void sendAckToServer(String to, String doc_id, String id) {
//        SendMessageEvent messageEvent = new SendMessageEvent();
//        messageEvent.setEventName(SocketManager.EVENT_MESSAGE_ACK);
//        MessageAck ack = (MessageAck) MessageFactory.getMessage(MessageFactory.message_ack, this);
//        messageEvent.setMessageObject((JSONObject) ack.getMessageObject(to, doc_id, MessageFactory.DELIVERY_STATUS_READ, id, false));
//        EventBus.getDefault().post(messageEvent);
//    }
//
//    private void loadMessageRes(ReceviceMessageEvent event) {
//        Object[] array = event.getObjectsArray();
//        try {
//            JSONObject objects = new JSONObject(array[0].toString());
//            int errorState = objects.getInt("err");
//            if (errorState == 0) {
//                String chat_id = (String) objects.get("doc_id");
//                String[] ids = chat_id.split("-");
//                String doc_id = ids[0] + "-" + ids[1];
//                int delivery = (int) objects.get("deliver");
//                String to = ids[1];
//
//                JSONObject msgData = objects.getJSONObject("data");
//                String recordId = msgData.getString("recordId");
//                String convId = msgData.getString("convId");
//
//                String secretType = msgData.getString("secret_type");
//                if (to.equalsIgnoreCase(mReceiverId) && secretType.equalsIgnoreCase("no")) {
//
//                    layout_new.setVisibility(View.GONE);
//
//                    mConvId = convId;
//                    MessageItemChat item = null;
//
//                    for (MessageItemChat msgItemChat : mChatData) {
//                        if (msgItemChat.getMessageId().equalsIgnoreCase(chat_id)) {
//                            item = msgItemChat;
//                            break;
//                        }
//                    }
//                    if (item != null) {
//                        int msgIndex = mChatData.indexOf(item);
//                        mChatData.get(msgIndex).setUploadStatus(MessageFactory.UPLOAD_STATUS_COMPLETED);
//                        mChatData.get(msgIndex).setDeliveryStatus("" + delivery);
//                        mChatData.get(msgIndex).setRecordId(recordId);
//                        mChatData.get(msgIndex).setConvId(convId);
//                        mAdapter.notifyDataSetChanged();
//
//                    } else {
//                        String toUserId = msgData.getString("to");
//
//                        if (toUserId.equalsIgnoreCase(mReceiverId)) {
//                            IncomingMessage incomingMessage = new IncomingMessage(ChatViewActivityRealcopy.this);
//                            MessageItemChat newMsgItem = incomingMessage.loadSingleMessageFromWeb(objects);
//                            if (newMsgItem != null) {
//                                mChatData.add(newMsgItem);
//                                notifyDatasetChange();
//                            }
//                        }
//                    }
//
//                    if (session.getPrefsNameintoneouttone()) {
//
//                        try {
//                            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//                            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//                            r.play();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                } /*else if (errorState == 1) {
//                SessionManager.getInstance(ChatViewActivity.this).logoutUser();
//            }*/
//
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void performMenuEmailChat() {
//        final String msg = "Attaching media will generate a large email message.";
//
//        final CustomAlertDialog dialog = new CustomAlertDialog();
//        dialog.setNegativeButtonText("Without media");
//        dialog.setPositiveButtonText("Attach media");
//        dialog.setMessage(msg);
//        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//            @Override
//            public void onPositiveButtonClick() {
//                dialog.dismiss();
//                final ProgressDialog dialogpr = ProgressDialog.show(ChatViewActivityRealcopy.this, "",
//                        "Loading. Please wait...", true);
//                final Timer timer2 = new Timer();
//                timer2.schedule(new TimerTask() {
//                    public void run() {
//                        dialogpr.dismiss();
//                        timer2.cancel(); //this will cancel the timer of the system
//                    }
//                }, 2500); // the timer will count 2.5 seconds....
//
//
//                emailgmail.setVisibility(View.VISIBLE);
//                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
//                        R.anim.bottom_up);
//                emailgmail.setAnimation(animation);
//                dialogpr.dismiss();
//                emai1send.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        gmailintent = false;
//
//                        String docId;
//                        String chatType = MessageFactory.CHAT_TYPE_SINGLE;
//                        if (isGroupChat) {
//                            docId = mCurrentUserId.concat("-").concat(to).concat("-g");
//                            chatType = MessageFactory.CHAT_TYPE_GROUP;
//                        } else {
//                            docId = mCurrentUserId.concat("-").concat(to);
//                        }
//
//                        EmailChatHistoryUtils emailChat = new EmailChatHistoryUtils(ChatViewActivityRealcopy.this);
//                        emailChat.send(docId, receiverNameText.getText().toString(), true, false, chatType);
//
//                        emailgmail.setVisibility(View.GONE);
//
//                    }
//                });
//                gmailsend.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //  sendgmail();
//                        // textfilesend();
//                        gmailintent = true;
//
//                        String docId;
//                        String chatType = MessageFactory.CHAT_TYPE_SINGLE;
//                        if (isGroupChat) {
//                            docId = mCurrentUserId.concat("-").concat(to).concat("-g");
//                            chatType = MessageFactory.CHAT_TYPE_GROUP;
//                        } else {
//                            docId = mCurrentUserId.concat("-").concat(to);
//                        }
//
//                        EmailChatHistoryUtils emailChat = new EmailChatHistoryUtils(ChatViewActivityRealcopy.this);
//                        emailChat.send(docId, receiverNameText.getText().toString(), true, true, chatType);
//
//                        emailgmail.setVisibility(View.GONE);
//                    }
//                });
//            }
//
//            @Override
//            public void onNegativeButtonClick() {
//                dialog.dismiss();
//                emailgmail.setVisibility(View.VISIBLE);
//                final ProgressDialog dialogpr = ProgressDialog.show(ChatViewActivityRealcopy.this, "",
//                        "Loading. Please wait...", true);
//                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
//                        R.anim.bottom_up);
//                emailgmail.setAnimation(animation);
//                dialogpr.dismiss();
//                emai1send.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String docId;
//                        String chatType = MessageFactory.CHAT_TYPE_SINGLE;
//                        if (isGroupChat) {
//                            docId = mCurrentUserId.concat("-").concat(to).concat("-g");
//                            chatType = MessageFactory.CHAT_TYPE_GROUP;
//                        } else {
//                            docId = mCurrentUserId.concat("-").concat(to);
//                        }
//
//                        EmailChatHistoryUtils emailChat = new EmailChatHistoryUtils(ChatViewActivityRealcopy.this);
//                        emailChat.send(docId, receiverNameText.getText().toString(), false, false, chatType);
//
//                        //sendgmailmedia();
//                        emailgmail.setVisibility(View.GONE);
//
//
//                    }
//                });
//                gmailsend.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String docId;
//                        String chatType = MessageFactory.CHAT_TYPE_SINGLE;
//                        if (isGroupChat) {
//                            docId = mCurrentUserId.concat("-").concat(to).concat("-g");
//                            chatType = MessageFactory.CHAT_TYPE_GROUP;
//                        } else {
//                            docId = mCurrentUserId.concat("-").concat(to);
//                        }
//
//                        EmailChatHistoryUtils emailChat = new EmailChatHistoryUtils(ChatViewActivityRealcopy.this);
//                        emailChat.send(docId, receiverNameText.getText().toString(), false, true, chatType);
//
//                        //   sendgmailmedia();
//                        emailgmail.setVisibility(View.GONE);
//                    }
//                });
//            }
//        });
//        dialog.show(getSupportFragmentManager(), "Delete member alert");
//    }
//
//    private void performMenuClearChat() {
//
//        final String msg = "Are you sure you want to clear chat messages in this chat?";
//
//        final CustomAlertDialog dialog = new CustomAlertDialog();
//        dialog.setNegativeButtonText("Cancel");
//        dialog.setPositiveButtonText("Clear");
//        dialog.setCheckBoxtext("Keep Starred Messages");
//        dialog.setMessage(msg);
//        final String docId;
//        deletestarred = false;
//        if (isGroupChat) {
//            docId = mCurrentUserId + "-" + to + "-g";
//        } else {
//            docId = mCurrentUserId + "-" + to;
//        }
//
//        dialog.setCheckBoxCheckedChangeListener(new CustomAlertDialog.OnDialogCheckBoxCheckedChangeListener() {
//            @Override
//            public void onCheckedChange(boolean isChecked) {
//                deletestarred = isChecked;
//            }
//        });
//
//        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//            @Override
//            public void onPositiveButtonClick() {
//                int star_status = 0;
//                if (deletestarred) {
//                    star_status = 1;
//                    // db.clearUnStarredMessage(docId);
//                }
//                if (AppUtils.isNetworkAvailable(context)) {
//                    if (!isGroupChat) {
//                        if (userInfoSession.hasChatConvId(docId)) {
//                            try {
//                                String convId = userInfoSession.getChatConvId(docId);
//                                JSONObject object = new JSONObject();
//                                object.put("convId", convId);
//                                object.put("from", mCurrentUserId);
//                                object.put("star_status", star_status);
//                                object.put("type", MessageFactory.CHAT_TYPE_SINGLE);
//                                //   load_clear_chat(object);
//                                SendMessageEvent event = new SendMessageEvent();
//                                event.setEventName(SocketManager.EVENT_CLEAR_CHAT);
//                                event.setMessageObject(object);
//                                EventBus.getDefault().post(event);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            // db.clearAllSingleChatMessage(docId);
//                        }
//                    } else {
//
//                        try {
//                            JSONObject object = new JSONObject();
//                            object.put("convId", to);
//                            object.put("from", mCurrentUserId);
//                            object.put("star_status", star_status);
//                            object.put("type", MessageFactory.CHAT_TYPE_GROUP);
//                            // load_clear_chat(object);
//                            SendMessageEvent event = new SendMessageEvent();
//                            event.setEventName(SocketManager.EVENT_CLEAR_CHAT);
//                            event.setMessageObject(object);
//                            EventBus.getDefault().post(event);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                } else {
//                    Toast.makeText(ChatViewActivityRealcopy.this, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
//                }
//                // db.clearAllGroupChatMessage(docId);
//
//
//            }
//
//            @Override
//            public void onNegativeButtonClick() {
//                dialog.dismiss();
//            }
//        });
//        dialog.show(getSupportFragmentManager(), "Delete member alert");
//    }
//
//
//    public void addnewcontact(final String number) {
//        CustomAlertDialog dialog = new CustomAlertDialog();
//        dialog.setMessage(getResources().getString(R.string.new_exitcontact));
//        dialog.setPositiveButtonText("NEW");
//        dialog.setNegativeButtonText("EXISTING");
//
//        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//            @Override
//            public void onPositiveButtonClick() {
//                Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
//                // Sets the MIME type to match the Contacts Provider
//                intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
//                intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
//                intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
//                startActivity(intent);
//                finish();
//            }
//
//            @Override
//            public void onNegativeButtonClick() {
//                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
//                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
//                startActivityForResult(intent, 1);
//                finish();
//            }
//        });
//
//        dialog.show(getSupportFragmentManager(), "Save contact");
//
//    }
//
//    private void performMenuBlock() {
//
//        String msg;
//        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//        if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
//            msg = "Do you want to Unblock " + mReceiverName + "?";
//        } else {
//            msg = "Block " + mReceiverName + "? Blocked contacts will no longer be able to call you or send you messages.";
//        }
//
//        final CustomAlertDialog dialog = new CustomAlertDialog();
//        dialog.setMessage(msg);
//        dialog.setPositiveButtonText("Ok");
//        dialog.setNegativeButtonText("Cancel");
//        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//            @Override
//            public void onPositiveButtonClick() {
//                dialog.dismiss();
//
//                putBlockUser();
//            }
//
//            @Override
//            public void onNegativeButtonClick() {
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show(getSupportFragmentManager(), "Block alert");
//
//    }
//
//    private void putBlockUser() {
//        BlockUserUtils.changeUserBlockedStatus(ChatViewActivityRealcopy.this, EventBus.getDefault(),
//                mCurrentUserId, mReceiverId, false);
//    }
//
//    private void performMenuMute() {
//        if (ConnectivityInfo.isInternetConnected(ChatViewActivityRealcopy.this)) {
//
//            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//            MuteStatusPojo muteData = null;
//            if (isGroupChat) {
//                muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, null, mGroupId, false);
//            } else {
//                muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, mReceiverId, mConvId, false);
//            }
//
//            if (muteData != null && muteData.getMuteStatus().equals("1")) {
//                if (!isGroupChat) {
//                    MuteUnmute.performUnMute(ChatViewActivityRealcopy.this, EventBus.getDefault(), mReceiverId,
//                            MessageFactory.CHAT_TYPE_SINGLE, "no");
//                } else {
//                    MuteUnmute.performUnMute(ChatViewActivityRealcopy.this, EventBus.getDefault(), mReceiverId,
//                            MessageFactory.CHAT_TYPE_GROUP, "no");
//                }
//            } else {
//                MuteUserPojo muteUserPojo = new MuteUserPojo();
//                muteUserPojo.setReceiverId(mReceiverId);
//                muteUserPojo.setSecretType("no");
//                if (isGroupChat) {
//                    muteUserPojo.setChatType(MessageFactory.CHAT_TYPE_GROUP);
//                } else {
//                    muteUserPojo.setChatType(MessageFactory.CHAT_TYPE_SINGLE);
//                }
//
//                ArrayList<MuteUserPojo> muteUserList = new ArrayList<>();
//                muteUserList.add(muteUserPojo);
//
//                Bundle putBundle = new Bundle();
//                putBundle.putSerializable("MuteUserList", muteUserList);
//
//                MuteAlertDialog dialog = new MuteAlertDialog();
//                dialog.setArguments(putBundle);
//                dialog.setCancelable(false);
//                dialog.setMuteAlertCloseListener(ChatViewActivityRealcopy.this);
//                dialog.show(getSupportFragmentManager(), "Mute");
//            }
//        } else {
//            Toast.makeText(ChatViewActivityRealcopy.this, "Check Your Network Connection", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void performMenuWallpaper() {
//        List<MultiTextDialogPojo> labelsList = new ArrayList<>();
//        MultiTextDialogPojo label = new MultiTextDialogPojo();
//        label.setImageResource(R.drawable.gallery_ic);
//        label.setLabelText("Gallery");
//        labelsList.add(label);
//
//        label = new MultiTextDialogPojo();
//        label.setImageResource(R.drawable.solidcolor_ic);
//        label.setLabelText("Solid color");
//        labelsList.add(label);
//
//        label = new MultiTextDialogPojo();
//        label.setImageResource(R.drawable.default_ic);
//        label.setLabelText("Default");
//        labelsList.add(label);
//
//        label = new MultiTextDialogPojo();
//        label.setImageResource(R.drawable.nowallpaper_ic);
//        label.setLabelText("No wallpaper");
//        labelsList.add(label);
//
//        CustomMultiTextItemsDialog dialog = new CustomMultiTextItemsDialog();
//        dialog.setTitleText("Wallpaper");
//        dialog.setLabelsList(labelsList);
//        dialog.setDialogItemClickListener(new CustomMultiTextItemsDialog.DialogItemClickListener() {
//            @Override
//            public void onDialogItemClick(int position) {
//                switch (position) {
//
//                    case 0:
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//                            photoPickerIntent.setType("image/*");
//                            photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(photoPickerIntent, RESULT_WALLPAPER);
//                        } else {
//                            Intent intent = new Intent();
//                            intent.setType("image/*");
//                            intent.setAction(Intent.ACTION_GET_CONTENT);
//                            startActivityForResult(intent, RESULT_WALLPAPER);
//                        }
//                        break;
//
//                    case 1:
//                        Intent intent = new Intent(context, WallpaperColor.class);
//                        startActivity(intent);
//                        break;
//                    case 2:
//                        String setbgdef = "def";
//                        background.setImageResource(R.drawable.chat_background);
//                        session.putgalleryPrefs(setbgdef);
//                        break;
//                    case 3:
//                        String setbg = "no";
//                        Bitmap bmp = Bitmap.createBitmap(128, 128, Bitmap.Config.ARGB_8888);
//                        Canvas canvas = new Canvas(bmp);
//                        canvas.drawColor(Color.parseColor("#f0f0f0"));
//                        background.setImageBitmap(bmp);
//                        session.putgalleryPrefs(setbg);
//                        break;
//
//                }
//            }
//        });
//        dialog.show(getSupportFragmentManager(), "Profile Pic");
//    }
//
//
//    private void performMenuSearch() {
//
//        showSearchActions();
//        showProgressDialog();
//        ArrayList<MessageItemChat> allMessages = db.selectAllChatMessages(docId, chatType);
//        Collections.sort(allMessages, msgComparator);
//        mChatData.clear();
//        mChatData.addAll(allMessages);
//        hideProgressDialog();
//
//        Search1.getBackground().clearColorFilter();
//        Search1.requestFocus();
//        showKeyboard();
//        Search1.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
//                if (cs.length() > 0) {
//                    mAdapter.getFilter().filter(cs);
//                } else {
//                    mAdapter.updateInfo(mChatData);
//                }
//
//            }
//
//            @Override
//            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//                                          int arg3) {
//                // TODO Auto-generated method stub
//            }
//
//            @Override
//            public void afterTextChanged(Editable arg0) {
//                // TODO Auto-generated method stub
//
//            }
//        });
//
//        backnavigate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Search1.setText("");
//                mAdapter.updateInfo(mChatData);
//                showUnSelectedActions();
//
//                InputMethodManager inputMethodManager =
//                        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
//                if (ChatViewActivityRealcopy.this.getCurrentFocus() != null) {
//                    inputMethodManager.hideSoftInputFromWindow(
//                            ChatViewActivityRealcopy.this.getCurrentFocus().getWindowToken(), 0);
//                }
//
//            }
//        });
//    }
//
//    public Bitmap ConvertToImage(String image) {
//        byte[] imageAsBytes = Base64.decode(image.getBytes(), Base64.DEFAULT);
//        return BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
//    }
//
//    protected void sendEmail() {
//
//        File directory = new File(Environment.getExternalStorageDirectory()
//                + File.separator + "serlization" + "/ChatList.txt");
//        Uri path = Uri.fromFile(directory);
//
//        Intent emailIntent = new Intent(Intent.ACTION_SEND);
//
//        emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name) + " Chat");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, "Your Chat history in " + getResources().getString(R.string.app_name));
//        emailIntent.setType("application/octet-stream");
//        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
//        final PackageManager pm = this.getPackageManager();
//        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
//        ResolveInfo best = null;
//        for (final ResolveInfo info : matches)
//            if (info.activityInfo.name.toLowerCase().contains("email"))
//                best = info;
//        if (best != null)
//            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
//        startActivity(emailIntent);
//    }
//
//    private void unreadmessage() {
//        int lastvisibleitempostion = mLayoutManager.findLastVisibleItemPosition();
//
//        if (lastvisibleitempostion == mChatData.size() - 1) {
//            iBtnScroll.setVisibility(View.GONE);
//            unreadcount.setVisibility(View.GONE);
//            unreadmsgcount = 0;
//        } else {
//            unreadcount.setVisibility(View.VISIBLE);
//            iBtnScroll.setVisibility(View.VISIBLE);
//            unreadcount.setText(String.valueOf(unreadmsgcount));
//        }
//
//    }
//
//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        try {
//            bytes.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }
//
//
//    private void addShortcut() {
//        final CustomAlertDialog dialog = new CustomAlertDialog();
//        dialog.setMessage("Do you want to  create shortcut?");
//        dialog.setPositiveButtonText("Ok");
//        dialog.setNegativeButtonText("Cancel");
//        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//            @Override
//            public void onPositiveButtonClick() {
//                String receiverName = receiverNameText.getText().toString();
//
//                Bitmap bitmap = null;
//                if (receiverAvatar != null && !receiverAvatar.equals("")) {
//                    Drawable drawable = ivProfilePic.getDrawable();
//                    if (drawable != null) {
//                        try {
//                            myTemp = ((BitmapDrawable) drawable).getBitmap();
//                            bitmap = Bitmap.createScaledBitmap(myTemp, 128, 128, true);
//                        } catch (ClassCastException ex) {
//                            try {
//                                myTemp = ((GlideBitmapDrawable) ivProfilePic.getDrawable().getCurrent()).getBitmap();
//                                bitmap = Bitmap.createScaledBitmap(myTemp, 128, 128, true);
//
//                            } catch (Exception e) {
//                                Log.e(TAG, "onPositiveButtonClick: ", e);
//                            }
//                        } catch (Exception ex2) {
//                            Log.e(TAG, "onPositiveButtonClick: ", ex2);
//                        }
//                    }
//                }
//                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(ChatViewActivityRealcopy.this);
//
//                String imageTS = contactDB_sqlite.getDpUpdatedTime(to);
//                if (imageTS == null || imageTS.isEmpty())
//                    imageTS = "1";
//                String avatar = Constants.USER_PROFILE_URL + to + ".jpg?id=" + imageTS;
//                receiverAvatar = avatar;
//                if (receiverAvatar != null) {
//                    try {
//                        new DownloadImage(receiverAvatar).execute();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    ShortcutBadgeManager.addChatShortcut(ChatViewActivityRealcopy.this, isGroupChat, to, receiverName,
//                            receiverAvatar, receiverMsisdn, bitmap);
//                }
//            }
//
//            @Override
//            public void onNegativeButtonClick() {
//
//            }
//        });
//        dialog.show(getSupportFragmentManager(), "CustomAlert");
//
//    }
//
//    //--------------------Ripple Effect Click Event------------------------------------
//
//    @Override
//    public void onComplete(RippleView rippleView) {
//        switch (rippleView.getId()) {
///*
//            case R.id.Ripple_call:
//                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//                if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
//                    DisplayAlert("Unblock" + " " + mReceiverName + " " + "to place a "
//                            + getString(R.string.app_name) + " call");
//                } else {
//                    //performCall(false);
//                    Activecall(false);
//                }
//                break;
//
//            case R.id.Ripple_Video:
//                ContactDB_Sqlite contactDB_sqlites = CoreController.getContactSqliteDBintstance(this);
//                if (contactDB_sqlites.getBlockedStatus(mReceiverId, false).equals("1")) {
//                    DisplayAlert("Unblock" + " " + mReceiverName + " " + "to place a "
//                            + getString(R.string.app_name) + " call");
//                } else {
//                    // performCall(true);
//                    Activecall(true);
//                }
//                break;
//*/
//
//
//        }
//
//    }
//
//    // DownloadImage AsyncTask
//    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
//
//        String imageURL;
//
//        public DownloadImage(String receiverAvatar) {
//            imageURL = receiverAvatar;
//        }
//
//        @Override
//        protected Bitmap doInBackground(String... URL) {
//
//            Bitmap bitmap = null;
//            try {
//                // Download Image from URL
//                InputStream input = new URL(imageURL).openStream();
//                // Decode Bitmap
//                bitmap = BitmapFactory.decodeStream(input);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return bitmap;
//        }
//
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            ShortcutBadgeManager.addChatShortcut(ChatViewActivityRealcopy.this, isGroupChat, to, receiverNameText.getText().toString(),
//                    receiverAvatar, receiverMsisdn, result);
//        }
//    }
//
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        mAdapter.stopAudioOnClearChat();
//        if (mRevealView.getVisibility() == View.VISIBLE) {
//            mRevealView.setVisibility(View.GONE);
//            hidden = true;
//        } else if (backfrom) {
//            Intent intent = new Intent(this, HomeScreen.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//            finish();
//        } else {
//            finish();
//        }
//        Chat_Activity = null;
//    }
//
//    @Override
//    protected void onRestart() {
//        super.onRestart();
//        if (isGroupChat) {
//            String docId = sessionManager.getCurrentUserID() + "-" + mGroupId + "-g";
//
//            try {
//                if (groupInfoSession.hasGroupInfo(docId)) {
//                    GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
//                    receiverNameText.setText(infoPojo.getGroupName());
//                    String path = Constants.SOCKET_IP.concat(infoPojo.getAvatarPath());
///*                    Glide.with(ChatViewActivity.this).load(path)
//                            .error(R.mipmap.chat_attachment_profile_default_image_frame).into(ivProfilePic);*/
//                    AppUtils.loadImage(ChatViewActivityRealcopy.this, path, ivProfilePic, 100,
//                            R.mipmap.group_chat_attachment_profile_icon);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//
//            //    loadFromDB();
//        }
//    }
//
//    private View.OnKeyListener enterKeyListener = new View.OnKeyListener() {
//        @Override
//        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
//
//            if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
//                if (session.isEnterKeyPressToSend()) {
//                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(ChatViewActivityRealcopy.this);
//                    if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
//                        DisplayAlert("Unblock " + mReceiverName + " to send message?");
//                    } else {
//                        if ((!hasLinkInfo && !reply)) {
//                            reply = false;
//                            if (isBroadcast) {
//                                sendTextMessageBroadcast();
//                            } else {
//                                sendTextMessage();
//                            }
//
//                        } else if (reply) {
//                            r1messagetoreplay.setVisibility(View.GONE);
//                            sendparticularmsgreply();
//                        } else {
//                            sendWebLinkMessage();
//                        }
//                    }
//                    return true;
//                }
//            }
//
//            return false;
//        }
//    };
//
//    private void sendWebLinkMessage() {
//
//        try {
//            CryptLib cryptLib = new CryptLib();
//            String data = cryptLib.encryptPlainTextWithRandomIV(sendMessage.getText().toString().trim(), kyGn());
//
//            //           String data = sendMessage.getText().toString().trim();
//
//            if (!data.equalsIgnoreCase("")) {
//                if (session.getarchivecount() != 0) {
//                    if (session.getarchive(from + "-" + to))
//                        session.removearchive(from + "-" + to);
//                }
//                if (session.getarchivecountgroup() != 0) {
//                    if (session.getarchivegroup(from + "-" + to + "-g"))
//                        session.removearchivegroup(from + "-" + to + "-g");
//                }
//
//                SendMessageEvent messageEvent = new SendMessageEvent();
//                WebLinkMessage message = (WebLinkMessage) MessageFactory.getMessage(MessageFactory.web_link, this);
//
//                String webLinkThumb = null;
//                if (ivWebLink.getDrawable() != null) {
//                    Bitmap linkBmp = null;
//                    try {
//                        linkBmp = ((BitmapDrawable) ivWebLink.getDrawable().getCurrent()).getBitmap();
//                    } catch (Exception e) {
//                        linkBmp = ((GlideBitmapDrawable) ivWebLink.getDrawable().getCurrent()).getBitmap();
//                    }
//
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    linkBmp.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
//                    byte[] byteArray = byteArrayOutputStream.toByteArray();
//                    webLinkThumb = Base64.encodeToString(byteArray, Base64.DEFAULT);
//                    try {
//                        byteArrayOutputStream.close();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//                JSONObject msgObj;
//                String receiverName = receiverNameText.getText().toString();
//                if (isGroupChat) {
//                    messageEvent.setEventName(SocketManager.EVENT_GROUP);
//                    msgObj = (JSONObject) message.getGroupMessageObject(to, data, receiverName);
//                } else if (isBroadcast) {
//                    message.setIDforBroadcast(mReceiverId);
//                    msgObj = null;
//                } else {
//                    msgObj = (JSONObject) message.getMessageObject(to, data, false);
//                    try {
//                        msgObj.put("is_expiry", isExpiry);
//                        msgObj.put("expiry_time", expireTime);
//                    } catch (JSONException ex) {
//                        ex.printStackTrace();
//                    }
//                    messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
//                }
//
//                MessageItemChat item = message.createMessageItem(true, data, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                        mReceiverId, receiverName, webLink, webLinkTitle, webLinkDesc, webLinkImgUrl, webLinkThumb, isExpiry, expireTime);
//
//                if (!isBroadcast) {
//                    msgObj = (JSONObject) message.getWebLinkObject(msgObj, webLink, webLinkTitle, webLinkDesc, webLinkImgUrl, webLinkThumb);
//                    messageEvent.setMessageObject(msgObj);
//                }
//
//                if (isGroupChat) {
//                    if (enableGroupChat) {
//                        item.setGroupName(receiverNameText.getText().toString());
//                        db.updateChatMessage(item, chatType);
//                        mChatData.add(item);
//                        EventBus.getDefault().post(messageEvent);
//                        notifyDatasetChange();
//                    } else {
//                        Toast.makeText(ChatViewActivityRealcopy.this, "You are not a member in this group", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    item.setSenderMsisdn(receiverMsisdn);
//                    item.setSenderName(receiverNameText.getText().toString());
//                    if (isBroadcast) {
//                        item.setDeliveryStatus("1");
//                    }
//                    db.updateChatMessage(item, chatType);
//                    mChatData.add(item);
//                    if (!isBroadcast) {
//                        EventBus.getDefault().post(messageEvent);
//                    }
//                    notifyDatasetChange();
//                }
//
//
//                sendMessage.getText().clear();
//                hasLinkInfo = false;
//                rlWebLink.setVisibility(View.GONE);
//                webLink = "";
//                webLinkTitle = "";
//                webLinkImgUrl = "";
//                webLinkDesc = "";
//            }
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    private void showActionBar() {
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().show();
//        }
//    }
//
//    private void hideActionBar() {
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().hide();
//        }
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        db = CoreController.getDBInstance(ChatViewActivityRealcopy.this);
//        isChatPage = true;
//        isKilled = false;
//
//
//        if (isGroupChat) {
//            GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(from + "-" + to + "-g");
//            if (infoPojo != null) {
//                if (isBroadcast) {
//                    enableGroupChat = true;
//                } else {
//                    enableGroupChat = infoPojo.isLiveGroup();
//                }
//            }
//        }
//        session.puttoid(docId);
//
//       /* if (chatMenu != null) {
//            onCreateOptionsMenu(chatMenu);
//        }*/
//
//        MessageService.clearNotificationData();
//        wallpaperdisplay();
//        NotificationManager notifManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);
//        notifManager.cancelAll();
//
//        try {
//            ShortcutBadger.removeCountOrThrow(this); //for 1.1.4+
//        } catch (Exception e) {
//            //  e.printStackTrace();
//        }
//
//
//        //----------Delete Chat---------------
////        if (isGroupChat) {
////            sendGroupOffline();
////        } else {
////            sendSingleOffline();
////        }
//
//        if (!isGroupChat) {
//            sendNormalOffline();
//        }
//
//        if(ackMsgid)
//        {
//            for(int i=0;i<mChatData.size();i++){
//                MessageItemChat msgItem=mChatData.get(i);
//                //  for (MessageItemChat msgItem : mChatData) {
//                MessageItemChat lastDeliveredItem = null;
//
//                String msgStatus = msgItem.getDeliveryStatus();
//                if (msgStatus != null) {
//                    if (!msgItem.isSelf() && msgStatus.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_DELIVERED)) {
//                        if (isGroupChat && msgItem.getGroupMsgFrom() != null) {
//                            lastDeliveredItem = msgItem;
//                        } else if (!isGroupChat) {
//                            lastDeliveredItem = msgItem;
//                        }
//                    }
//
//
//                    if (lastDeliveredItem != null) {
//                        if (sessionManager.canSendReadReceipt()) {
//                            if (isGroupChat) {
//                                String msgId = lastDeliveredItem.getMessageId().split("-")[3];
//                                sendGroupAckToServer(mCurrentUserId, mGroupId, msgId, MessageFactory.GROUP_MSG_READ_ACK);
//                                changeBadgeCount(lastDeliveredItem.getMessageId());
//                            } else {
//                                String msgId = lastDeliveredItem.getMessageId().split("-")[2];
//                                String ackDocId = to.concat("-").concat(mCurrentUserId).concat("-").concat(msgId);
//                                sendAckToServer(to, ackDocId, msgId);
//                                msgItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_READ);
//                                mAdapter.notifyItemChanged(i);
//                                changeBadgeCount(lastDeliveredItem.getMessageId());
//                            }
//
//
//
//                        }
//                    }
//                }
//
//
//            }
//
//            ackMsgid=false;
//        }
//
//        // profilepicupdation();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        session.puttoid("");
//        isChatPage = false;
//
//    }
//
//    @Override
//    public boolean onKeyDown(int keycode, KeyEvent e) {
//
//        switch (keycode) {
//            case KeyEvent.KEYCODE_MENU:
//                isMenuBtnClick = true;
//                return super.onKeyDown(keycode, e);
//
//            default:
//                return super.onKeyDown(keycode, e);
//        }
//
//    }
//
//    private void DisplayAlert(final String txt) {
//        final CustomAlertDialog dialog = new CustomAlertDialog();
//        dialog.setMessage(txt);
//        dialog.setNegativeButtonText("Cancel");
//        dialog.setPositiveButtonText("Unblock");
//        dialog.setCancelable(false);
//        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//            @Override
//            public void onPositiveButtonClick() {
//                putBlockUser();
//                dialog.dismiss();
//            }
//
//            @Override
//
//            public void onNegativeButtonClick() {
//                dialog.dismiss();
//            }
//        });
//        dialog.show(getSupportFragmentManager(), "Unblock a person");
//
//    }
//
//    private void blockunblockcontact(ReceviceMessageEvent event) {
//        String toid = "", fromid = "";
//        try {
//            Object[] obj = event.getObjectsArray();
//            JSONObject object = new JSONObject(obj[0].toString());
//
//            String stat = object.getString("status");
//            toid = object.getString("to");
//            fromid = object.getString("from");
//
//            if (mCurrentUserId.equalsIgnoreCase(fromid) && toid.equalsIgnoreCase(mReceiverId)) {
//                setTopLayoutBlockText();
//                if (stat.equalsIgnoreCase("1")) {
//                    block_contact.setText("Unblock");
//                    Toast.makeText(this, "Contact is blocked", Toast.LENGTH_SHORT).show();
//                } else {
//                    block_contact.setText("Block");
//                    Toast.makeText(this, "Contact is Unblocked", Toast.LENGTH_SHORT).show();
//                }
//            } else if (mCurrentUserId.equalsIgnoreCase(toid) && fromid.equalsIgnoreCase(mReceiverId)) {
//                getcontactname.configProfilepic(ivProfilePic, to, true, false, R.mipmap.chat_attachment_profile_default_image_frame);
//
//                if (stat.equalsIgnoreCase("1")) {
//                    statusTextView.setVisibility(View.GONE);
//
//                } else {
//                    statusTextView.setVisibility(View.VISIBLE);
//                }
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    @Override
//    public boolean onLongClick(View view) {
//
//        if (view.getId() == R.id.record) {
//
//            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//            if (contactDB_sqlite.getBlockedStatus(mReceiverId, false).equals("1")) {
//
//                avoid_twotimescall++;
//                if (avoid_twotimescall == 1) {
//                    DisplayAlert("Unblock " + mReceiverName + " to send message?");
//                } else if (avoid_twotimescall == 2) {
//                    avoid_twotimescall = 0;
//                }
//
//            } else {
//                if (checkAudioRecordPermission()) {
//                    //   final Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();
//                    record.setImageResource(R.drawable.record_hold);
//                    sendMessage.setVisibility(View.GONE);
//                    selEmoji.setImageResource(R.drawable.record_usericon);
//                    myChronometer.setVisibility(View.VISIBLE);
//                    image_to.setVisibility(View.VISIBLE);
//                    slidetocencel.setVisibility(View.VISIBLE);
//                    capture_timer.setVisibility(View.GONE);
//                    attachment_icon.setVisibility(View.GONE);
//                    // myChronometer.setTypeface(face);
//                    selEmoji.setEnabled(false);
//                    startAudioRecord();
//                } else {
//                    requestAudioRecordPermission();
//                }
//            }
//        }
//
//
//        return true;
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//        switch (requestCode) {
//            case AUDIO_RECORD_PERMISSION_REQUEST_CODE:
//                if (grantResults.length > 0) {
//                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
//                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
//
//                    /*if (StoragePermission && RecordPermission) {
//                        Toast.makeText(ChatViewActivity.this, "Now you can record and share audio",
//.                                Toast.LENGTH_LONG).show();
//                    } */
//                }
//                break;
//        }
//    }
//
//    private void startAudioRecord() {
//
//        try {
//            if (!audioRecordStarted) {
//                audioRecordStarted = true;
//                File audioDir = new File(MessageFactory.AUDIO_STORAGE_PATH);
//                if (!audioDir.exists()) {
//                    audioDir.mkdirs();
//                }
//
//                audioRecordPath = MessageFactory.AUDIO_STORAGE_PATH + MessageFactory.getMessageFileName(MessageFactory.audio,
//                        Calendar.getInstance().getTimeInMillis() + "rc", ".mp3");
//                File file = new File(audioRecordPath);
//                try {
//                    file.createNewFile();
//                } catch (Exception e) {
//                    audioRecordPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath() +
//                            MessageFactory.getMessageFileName(MessageFactory.audio,
//                                    Calendar.getInstance().getTimeInMillis() + "rc", ".mp3");
//                    ;
//                    file = new File(audioRecordPath);
//                    try {
//                        file.createNewFile();
//                    } catch (Exception e1) {
//                        Log.e(TAG, "startAudioRecord: ", e1);
//                    }
//                }
//                audioRecorder = new MediaRecorder();
//                audioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//                audioRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//                audioRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//                audioRecorder.setOutputFile(audioRecordPath);
//                audioRecorder.prepare();
//                audioRecorder.start();
//                myChronometer.start();
//                myChronometer.setBase(SystemClock.elapsedRealtime());
//            }
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
//
//    private void requestAudioRecordPermission() {
//        ActivityCompat.requestPermissions(ChatViewActivityRealcopy.this, new
//                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, AUDIO_RECORD_PERMISSION_REQUEST_CODE);
//    }
//
//    private View.OnTouchListener audioRecordTouchListener = new View.OnTouchListener() {
//
//        @Override
//        public boolean onTouch(View pView, MotionEvent pEvent) {
//            pView.onTouchEvent(pEvent);
//
//            if (pEvent.getAction() == MotionEvent.ACTION_UP) {
//                try {
//
//                    if (audioRecordStarted) {
//                        String sendAudioPath = audioRecordPath;
//                        audioRecordPath = "";
//                        record.setImageResource(R.drawable.record);
//                        selEmoji.setImageResource(R.drawable.smile);
//                        sendMessage.setVisibility(View.VISIBLE);
//                        myChronometer.setVisibility(View.GONE);
//                        image_to.setVisibility(View.GONE);
//                        slidetocencel.setVisibility(View.GONE);
//                        if (!isGroupChat) {
//                            capture_timer.setVisibility(View.VISIBLE);
//                        }
//                        attachment_icon.setVisibility(View.VISIBLE);
////                    audioRecorder.stop();
//                        audioRecorder.release();
//                        myChronometer.stop();
//
//                        File file = new File(sendAudioPath);
//                        if (file.exists()) {
//                            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//                            mmr.setDataSource(ChatViewActivityRealcopy.this, Uri.parse(sendAudioPath));
//                            String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
//                            durationStr = getTimeString(Long.parseLong(durationStr));
//                            showAudioRecordSentAlert(sendAudioPath, durationStr);
//                        }
//                    }
//                } catch (RuntimeException e) {
//                    e.printStackTrace();
//                }
//                audioRecordStarted = false;
//                selEmoji.setEnabled(true);
//            }
//            return false;
//        }
//    };
//
//    private void showAudioRecordSentAlert(final String audioRecordPath, final String durationStr) {
//        CustomAlertDialog dialog = new CustomAlertDialog();
//        dialog.setMessage("You want send this recorded audio?");
//        dialog.setPositiveButtonText("Send");
//        dialog.setNegativeButtonText("Cancel");
//        dialog.setCancelable(false);
//
//        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
//            @Override
//            public void onPositiveButtonClick() {
//                sendAudioMessage(audioRecordPath, durationStr, MessageFactory.AUDIO_FROM_RECORD);
//            }
//
//            @Override
//            public void onNegativeButtonClick() {
//
//            }
//        });
//        dialog.show(getSupportFragmentManager(), "Record Alert");
//    }
//
//    private void showUnSelectedActions() {
//        include.setVisibility(View.VISIBLE);
//        rlChatActions.setVisibility(View.GONE);
//
//        isFirstItemSelected = false;
//        selectedChatItems.clear();
//
//        for (int i = 0; i < mChatData.size(); i++) {
//            mChatData.get(i).setSelected(false);
//        }
//
//        mAdapter.notifyDataSetChanged();
//    }
//
//    private boolean performSelection(int position) {
////        MessageItemChat msgItem = mChatData.get(position);
//        MessageItemChat msgItem = mAdapter.getItem(position);
//        String msgType = msgItem.getMessageType();
//        boolean SelfType = msgItem.isSelf();
//
//        if (msgType.equalsIgnoreCase(MessageFactory.DELETE_SELF + "") || msgType.equalsIgnoreCase(MessageFactory.DELETE_OTHER + "")) {
//            Delete_Type = "delete received message";
////            return true;
//        } else if ((!msgType.equalsIgnoreCase(MessageFactory.DELETE_SELF + "") || !msgType.equalsIgnoreCase(MessageFactory.DELETE_OTHER + "")) && !SelfType) {
//            Delete_Type = "delete received message";
//        } else {
//            Delete_Type = "delete sent message";
//        }
//
//        if (!msgItem.isInfoMsg() && !msgItem.getMessageType().equals(MessageFactory.missed_call + "") && !msgItem.getMessageType().equals(MessageFactory.DELETE_SELF + "") && !msgItem.getMessageType().equals(MessageFactory.DELETE_OTHER + "")) {
//            mypath = msgItem.getChatFileLocalPath();
//
//            int index = mChatData.indexOf(msgItem);
//            mChatData.get(index).setSelected(!msgItem.isSelected());
//
//            if (!selectedChatItems.contains(msgItem)) {
//                selectedChatItems.add(msgItem);
//            } else {
//                selectedChatItems.remove(msgItem);
//            }
//
//            for (MessageItemChat chat : selectedChatItems) {
//                if (chat.getMessageType().equals(MessageFactory.text + "")
//                        || (chat.getMessageType().equals(MessageFactory.contact + ""))
//                        || (chat.getMessageType().equals(MessageFactory.web_link + ""))
//                        || (chat.getMessageType().equals(MessageFactory.location + ""))) {
//                    forward.setVisibility(View.VISIBLE);
//                } else {
//                    if (chat.isSelf()) {
//                        if (chat.getUploadStatus() == MessageFactory.UPLOAD_STATUS_COMPLETED) {
//                            forward.setVisibility(View.VISIBLE);
//                        } else {
//                            forward.setVisibility(View.GONE);
//                            break;
//                        }
//                    } else {
//                        if (chat.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_COMPLETED) {
//                            forward.setVisibility(View.VISIBLE);
//                        } else {
//                            forward.setVisibility(View.GONE);
//                            break;
//                        }
//                    }
//                }
//            }
//
//            // Add copy action only if all selected messages as text
//            boolean allTextMsg = true;
//            boolean isDeletedMsg = false;
//            for (MessageItemChat selectedItem : selectedChatItems) {
//                if (selectedItem.getMessageType().equals(MessageFactory.DELETE_SELF + "") ||
//                        selectedItem.getMessageType().equals(MessageFactory.DELETE_OTHER + "")) {
//                    isDeletedMsg = true;
//                    break;
//                }
//            }
//
//            for (MessageItemChat selectedItem : selectedChatItems) {
//                if (!selectedItem.getMessageType().equals(MessageFactory.text + "")
//                        && !selectedItem.getMessageType().equals(MessageFactory.web_link + "")) {
//                    allTextMsg = false;
//                    break;
//                }
//
//            }
//            if (allTextMsg) {
//                copy.setVisibility(View.VISIBLE);
//            } else {
//                copy.setVisibility(View.GONE);
//            }
//
//            if (selectedChatItems.size() > 0) {
//                isFirstItemSelected = true;
//                if (selectedChatItems.size() == 1) {
//                    replymess.setVisibility(View.VISIBLE);
//
//                    if (selectedChatItems.get(0).isSelf() && !isDeletedMsg) {
//
//                        //   info.setVisibility(View.VISIBLE);
//                    } else {
//                        info.setVisibility(View.GONE);
//                    }
//
//                } else {
//                    replymess.setVisibility(View.GONE);
//                    reply = false;
//                    info.setVisibility(View.GONE);
//                }
//
//                isSelectedWithUnStarMsg = false;
//                for (MessageItemChat selectedItem : selectedChatItems) {
//                    if (selectedItem.getStarredStatus().equals(MessageFactory.MESSAGE_UN_STARRED)) {
//                        isSelectedWithUnStarMsg = true;
//                        break;
//                    }
//                }
//
//                if (isSelectedWithUnStarMsg) {
//                    starred.setImageResource(R.drawable.ic_starred);
//                } else {
//                    starred.setImageResource(R.drawable.ic_unstarred);
//                }
//
//                if (isDeletedMsg) {
//                    showDeleteActions();
//                } else {
//                    showBaseActions();
//                }
//            }
//
//            mAdapter.setfirstItemSelected(isFirstItemSelected);
//            mAdapter.notifyDataSetChanged();
//        }
//        return false;
//    }
//
//    private void showBaseActions() {
//        include.setVisibility(View.GONE);
//        rlChatActions.setVisibility(View.VISIBLE);
//        starred.setVisibility(View.VISIBLE);
//        delete.setVisibility(View.VISIBLE);
//        longpressback.setVisibility(View.VISIBLE);
//    }
//
//    private void showDeleteActions() {
//        include.setVisibility(View.GONE);
//        rlChatActions.setVisibility(View.VISIBLE);
//        starred.setVisibility(View.GONE);
//        forward.setVisibility(View.GONE);
//        replymess.setVisibility(View.GONE);
//        info.setVisibility(View.GONE);
//        delete.setVisibility(View.VISIBLE);
//        longpressback.setVisibility(View.VISIBLE);
//    }
//
//    private void showSearchActions() {
//        // Make all selected items to unselect
//        include.setVisibility(View.GONE);
//        rlChatActions.setVisibility(View.VISIBLE);
//
//        replymess.setVisibility(View.GONE);
//        copy.setVisibility(View.GONE);
//        starred.setVisibility(View.GONE);
//        info.setVisibility(View.GONE);
//        delete.setVisibility(View.GONE);
//        forward.setVisibility(View.GONE);
//        longpressback.setVisibility(View.GONE);
//        backnavigate.setVisibility(View.VISIBLE);
//        Search1.setVisibility(View.VISIBLE);
//    }
//
//    /*private View.OnTouchListener btnSlideListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View view, MotionEvent motionEvent) {
//
//            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvSlideLbl
//                        .getLayoutParams();
//                params.leftMargin = dp(30);
//                tvSlideLbl.setLayoutParams(params);
//                ViewProxy.setAlpha(tvSlideLbl, 1);
//                startedDraggingX = -1;
//                // startRecording();
//                startrecord();
//                record.getParent()
//                        .requestDisallowInterceptTouchEvent(true);
//                recordPanel.setVisibility(View.VISIBLE);
//            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
//                    || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
//                startedDraggingX = -1;
//                stoprecord();
//                // stopRecording(true);
//            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
//                float x = motionEvent.getX();
//                if (x < -distCanMove) {
//                    stoprecord();
//                    // stopRecording(false);
//                }
//                x = x + ViewProxy.getX(record);
//                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) tvSlideLbl
//                        .getLayoutParams();
//                if (startedDraggingX != -1) {
//                    float dist = (x - startedDraggingX);
//                    params.leftMargin = dp(30) + (int) dist;
//                    tvSlideLbl.setLayoutParams(params);
//                    float alpha = 1.0f + dist / distCanMove;
//                    if (alpha > 1) {
//                        alpha = 1;
//                    } else if (alpha < 0) {
//                        alpha = 0;
//                    }
//                    ViewProxy.setAlpha(tvSlideLbl, alpha);
//                }
//                if (x <= ViewProxy.getX(tvSlideLbl) + tvSlideLbl.getWidth()
//                        + dp(30)) {
//                    if (startedDraggingX == -1) {
//                        startedDraggingX = x;
//                        distCanMove = (recordPanel.getMeasuredWidth()
//                                - tvSlideLbl.getMeasuredWidth() - dp(48)) / 2.0f;
//                        if (distCanMove <= 0) {
//                            distCanMove = dp(80);
//                        } else if (distCanMove > dp(80)) {
//                            distCanMove = dp(80);
//                        }
//                    }
//                }
//                if (params.leftMargin > dp(30)) {
//                    params.leftMargin = dp(30);
//                    tvSlideLbl.setLayoutParams(params);
//                    ViewProxy.setAlpha(tvSlideLbl, 1);
//                    startedDraggingX = -1;
//                }
//            }
//            view.onTouchEvent(motionEvent);
//            return true;
//        }
//    };*/
//
//
//    private void Starredmsg() {
//        String msgidstarred = "";
//        if (msgid != null || !msgid.equals("")) {
//            final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//            for (int i = 0; i < mChatData.size(); i++) {
//                try {
//                    String[] array = mChatData.get(i).getMessageId().split("-");
//                    if (mChatData.get(i).getMessageId().contains("-g")) {
//                        msgidstarred = array[3];
//                    } else {
//                        msgidstarred = array[2];
//                    }
//                    if (msgid.equalsIgnoreCase(msgidstarred)) {
//                        recyclerView_chat.getLayoutManager().scrollToPosition(i);
//                        mChatData.get(i).setSelected(true);
//                    }
//
//                } catch (Exception e) {
//
//                }
//            }
//            final Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    for (int i = 0; i < mChatData.size(); i++) {
//                        mChatData.get(i).setSelected(false);
//                    }
//                    mAdapter.notifyDataSetChanged();
//                }
//            }, 2500);
//
//        }
//
//    }
//
//    public int dp(float value) {
//        return (int) Math.ceil(1 * value);
//    }
//
//    private void profilepicupdation() {
//        if (receiverAvatar != null) {
//            if (!isGroupChat) {
//                getcontactname.configProfilepic(ivProfilePic, to, true, true, R.mipmap.chat_attachment_profile_default_image_frame);
//            } else {
//
//                if (receiverAvatar.startsWith("./")) {
//                    receiverAvatar = receiverAvatar.replaceFirst("./", "");
//                    receiverAvatar = Constants.SOCKET_IP + receiverAvatar;
//                }
//                if (receiverAvatar.startsWith(Constants.SOCKET_IP)) {
//                    /*Picasso.with(context).load(receiverAvatar).transform(new CircleTransform()).error(
//                            R.mipmap.group_chat_attachment_profile_icon)
//                            .into(ivProfilePic);
//                    */
///*
//                    Glide.with(context).load(receiverAvatar).error(
//                            R.mipmap.group_chat_attachment_profile_icon)
//                            .dontAnimate()
//
//                            .into(ivProfilePic);
//*/
//
//                    AppUtils.loadImage(context, receiverAvatar, ivProfilePic, 100, R.mipmap.group_chat_attachment_profile_icon);
//                } else {
//                    ivProfilePic.setImageResource(R.mipmap.group_chat_attachment_profile_icon);
//                }
//
//            }
//        }
//
//    }
//
//    @Override
//    public void onMuteDialogClosed(boolean isMuted) {
//
//    }
//
//    private void performCall(boolean isVideoCall) {
//
//        if (ConnectivityInfo.isInternetConnected(this)) {
//
//            if (checkAudioRecordPermission()) {
//                if (!CallMessage.isAlreadyCallClick) {
//                    int callType;
//                    if (isVideoCall) {
//                        callType = MessageFactory.video_call;
//                        callType_global = MessageFactory.video_call;
//                    } else {
//                        callType = MessageFactory.audio_call;
//                        callType_global = MessageFactory.audio_call;
//                    }
//
//                    CallMessage message = new CallMessage(ChatViewActivityRealcopy.this);
//                    JSONObject object = (JSONObject) message.getMessageObject(to, callType);
//
//                    if (object != null) {
//                        SendMessageEvent callEvent = new SendMessageEvent();
//                        callEvent.setEventName(SocketManager.EVENT_CALL);
//                        callEvent.setMessageObject(object);
//                        EventBus.getDefault().post(callEvent);
//                    }
//                    CallMessage.setCallClickTimeout();
//                } else {
//                    Toast.makeText(ChatViewActivityRealcopy.this, "Call in progress", Toast.LENGTH_SHORT).show();
//                }
//
//            /*Intent i = new Intent(this, CallHistoryActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT |
//                    Intent.FLAG_ACTIVITY_NEW_TASK |
//                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK | Intent.FLAG_ACTIVITY_NO_HISTORY);
//
//            startActivity(i);*/
//
//            } else {
//                requestAudioRecordPermission();
//            }
//        } else {
//            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
//        }
//
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        isKilled = true;
//        hideKeyboard();
//        unregisterReceiver(timerReceiver);
//        unregisterReceiver(fileDownloadReceiver);
//        EventBus.getDefault().unregister(this);
//    }
//
//
//    //------------Delete Chat----------------
//
//    private void sendSingleOffline() {
//        SendMessageEvent groupMsgEvent = new SendMessageEvent();
//        groupMsgEvent.setEventName(SocketManager.EVENT_SINGLE_OFFLINE_MSG);
//        try {
//            JSONObject object = new JSONObject();
//            object.put("msg_to", mCurrentUserId);
//            groupMsgEvent.setMessageObject(object);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        EventBus.getDefault().post(groupMsgEvent);
//    }
//
//    private void sendGroupOffline() {
//        SendMessageEvent groupMsgEvent = new SendMessageEvent();
//        groupMsgEvent.setEventName(SocketManager.EVENT_GROUP);
//        try {
//            JSONObject object = new JSONObject();
//            object.put("from", mCurrentUserId);
//            object.put("groupType", SocketManager.ACTION_EVENT_GROUP_OFFLINE);
//            groupMsgEvent.setMessageObject(object);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        EventBus.getDefault().post(groupMsgEvent);
//    }
//
//    private void sendNormalOffline() {
//        SendMessageEvent groupMsgEvent = new SendMessageEvent();
//        groupMsgEvent.setEventName(SocketManager.EVENT_GET_MESSAGE);
//        try {
//            JSONObject object = new JSONObject();
//            object.put("msg_to", mCurrentUserId);
//            groupMsgEvent.setMessageObject(object);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        EventBus.getDefault().post(groupMsgEvent);
//    }
//
//
//    public void forwardToBroadcast(MessageItemChat msgItem, List<ChatappContactModel> selectedContactsList) {
//
//        switch (msgItem.getMessageType()) {
//
//            case (MessageFactory.text + ""):
//
//                for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
//                    ChatappContactModel userData = selectedContactsList.get(contactIndex);
//
//                    SendMessageEvent messageEvent = new SendMessageEvent();
//                    TextMessage message = (TextMessage) MessageFactory.getMessage(MessageFactory.text, ChatViewActivityRealcopy.this);
//                    JSONObject msgObj;
//
//                    msgObj = (JSONObject) message.getMessageObject(userData.get_id(), msgItem.getTextMessage(), false);
//                    messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
//                    messageEvent.setMessageObject(msgObj);
//
//                    MessageItemChat item = message.createMessageItem(true, msgItem.getTextMessage(), MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                            userData.get_id(), userData.getFirstName(), isExpiry, expireTime);
//                    item.setSenderMsisdn(userData.getNumberInDevice());
//                    item.setSenderName(userData.getFirstName());
//
//                    MessageDbController db = CoreController.getDBInstance(ChatViewActivityRealcopy.this);
//                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
//
//                    EventBus.getDefault().post(messageEvent);
//                }
//
//
//                break;
//
//
//            case (MessageFactory.audio + ""):
//
//                for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
//                    ChatappContactModel userData = selectedContactsList.get(contactIndex);
//
//                    String filePath = msgItem.getChatFileLocalPath();
//                    String duration = msgItem.getDuration();
//
//                    AudioMessage message = (AudioMessage) MessageFactory.getMessage(MessageFactory.audio, ChatViewActivityRealcopy.this);
//
//                    message.getMessageObject(userData.get_id(), filePath, false);
//
//
//                    MessageItemChat item = message.createMessageItem(true, filePath, duration, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                            userData.get_id(), userData.getFirstName(), msgItem.getaudiotype(), isExpiry, expireTime);
//
//                    String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(filePath);
//                    String audioName = item.getMessageId() + fileExtension;
//
//                    String docId;
//
//                    docId = mCurrentUserId + "-" + userData.get_id();
//
//                    JSONObject uploadObj;
//
//                    uploadObj = (JSONObject) message.createAudioUploadObject(item.getMessageId(), docId, audioName, filePath,
//                            duration, userData.getFirstName(), msgItem.getaudiotype(), MessageFactory.CHAT_TYPE_SINGLE, false);
//
//
//                    uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
//                    item.setSenderMsisdn(userData.getNumberInDevice());
//                    item.setSenderName(userData.getFirstName());
//                    item.setaudiotype(msgItem.getaudiotype());
//
//                    MessageDbController db = CoreController.getDBInstance(ChatViewActivityRealcopy.this);
//
//                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
//
//
//                }
//                break;
//
//            case (MessageFactory.video + ""):
//
//                for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
//                    ChatappContactModel userData = selectedContactsList.get(contactIndex);
//
//                    String videoPath = msgItem.getChatFileLocalPath();
//
//                    VideoMessage message = (VideoMessage) MessageFactory.getMessage(MessageFactory.video, ChatViewActivityRealcopy.this);
//
//                    message.getMessageObject(userData.get_id(), videoPath, false);
//
//
//                    MessageItemChat item = message.createMessageItem(true, videoPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                            userData.get_id(), userData.getFirstName(), "", isExpiry, expireTime);
//
//
//                    Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
//                    ByteArrayOutputStream out = new ByteArrayOutputStream();
//                    thumbBmp.compress(Bitmap.CompressFormat.PNG, 10, out);
//                    byte[] thumbArray = out.toByteArray();
//                    try {
//                        out.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                    String thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
//                    if (thumbData != null) {
//                        item.setThumbnailData(thumbData);
//                    }
//
//                    String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(videoPath);
//                    String videoName = item.getMessageId() + fileExtension;
//
//                    JSONObject uploadObj;
//                    String docId;
//
//                    docId = mCurrentUserId + "-" + userData.get_id();
//                    uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
//                            videoName, videoPath, userData.getFirstName(), "", MessageFactory.CHAT_TYPE_SINGLE, false);
//
//
//                    uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
//                    item.setSenderMsisdn(userData.getNumberInDevice());
//                    item.setSenderName(userData.getFirstName());
//                    MessageDbController db = CoreController.getDBInstance(ChatViewActivityRealcopy.this);
//
//                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
//
//                }
//                break;
//
//            case (MessageFactory.picture + ""):
//
//                for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
//                    ChatappContactModel userData = selectedContactsList.get(contactIndex);
//
//                    String imgPath = msgItem.getChatFileLocalPath();
//                    PictureMessage message = (PictureMessage) MessageFactory.getMessage(MessageFactory.picture, ChatViewActivityRealcopy.this);
//
//
//                    message.getMessageObject(userData.get_id(), imgPath, false);
//
//
//                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    options.inJustDecodeBounds = true;
//                    BitmapFactory.decodeFile(imgPath, options);
//                    int imageHeight = options.outHeight;
//                    int imageWidth = options.outWidth;
//
//                    MessageItemChat item = message.createMessageItem(true, msgItem.getTextMessage(), imgPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                            userData.get_id(), userData.getFirstName(), imageWidth, imageHeight, isExpiry, expireTime);
//
//                    String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(imgPath);
//                    String imgName = item.getMessageId() + fileExtension;
//
//                    String docId;
//                    JSONObject uploadObj;
//
//                    docId = mCurrentUserId + "-" + userData.get_id();
//                    uploadObj = (JSONObject) message.createImageUploadObject(item.getMessageId(), docId,
//                            imgName, imgPath, userData.getFirstName(), "", MessageFactory.CHAT_TYPE_SINGLE, false);
//
//
//                    uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
//
//                    item.setSenderMsisdn(userData.getNumberInDevice());
//                    item.setSenderName(userData.getFirstName());
//                    MessageDbController db = CoreController.getDBInstance(ChatViewActivityRealcopy.this);
//
//                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
//
//                }
//                break;
//
//            case (MessageFactory.document + ""):
//
//                for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
//                    ChatappContactModel userData = selectedContactsList.get(contactIndex);
//
//                    String filePath = msgItem.getChatFileLocalPath();
//
//                    DocumentMessage message = (DocumentMessage) MessageFactory.getMessage(MessageFactory.document, ChatViewActivityRealcopy.this);
//
//
//                    message.getMessageObject(userData.get_id(), filePath, false);
//
//                    MessageItemChat item = message.createMessageItem(true, filePath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                            userData.get_id(), userData.getFirstName(), "", "");
//
//                    String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(filePath);
//                    String docName = item.getMessageId() + fileExtension;
//                    String docId;
//                    JSONObject uploadObj;
//
//                    docId = mCurrentUserId + "-" + userData.get_id();
//                    uploadObj = (JSONObject) message.createDocUploadObject(item.getMessageId(), docId,
//                            docName, filePath, userData.getFirstName(), MessageFactory.CHAT_TYPE_SINGLE, false);
//
//
//                    uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
//                    item.setChatFileLocalPath(filePath);
//                    item.setSenderMsisdn(userData.getNumberInDevice());
//                    item.setSenderName(userData.getFirstName());
//                    MessageDbController db = CoreController.getDBInstance(ChatViewActivityRealcopy.this);
//
//                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
//
//                }
//                break;
//
//            case (MessageFactory.web_link + ""):
//
//                for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
//                    ChatappContactModel userData = selectedContactsList.get(contactIndex);
//
//                    String data = msgItem.getTextMessage();
//                    String webLink = msgItem.getWebLink();
//                    String webLinkTitle = msgItem.getWebLinkTitle();
//                    String webLinkDesc = msgItem.getWebLinkDesc();
//                    String webLinkImgUrl = msgItem.getWebLinkImgUrl();
//                    String webLinkThumb = msgItem.getWebLinkImgThumb();
//
//                    SendMessageEvent messageEvent = new SendMessageEvent();
//                    WebLinkMessage message = (WebLinkMessage) MessageFactory.getMessage(MessageFactory.web_link, ChatViewActivityRealcopy.this);
//                    JSONObject msgObj;
//
//                    msgObj = (JSONObject) message.getMessageObject(userData.get_id(), data, false);
//
//
//                    MessageItemChat item = message.createMessageItem(true, data, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                            userData.get_id(), userData.getFirstName(), webLink, webLinkTitle, webLinkDesc, webLinkImgUrl, webLinkThumb, "", "");
//                    msgObj = (JSONObject) message.getWebLinkObject(msgObj, webLink, webLinkTitle, webLinkDesc, webLinkImgUrl, webLinkThumb);
//                    messageEvent.setMessageObject(msgObj);
//
//                    item.setSenderMsisdn(userData.getNumberInDevice());
//                    item.setSenderName(userData.getFirstName());
//                    MessageDbController db = CoreController.getDBInstance(ChatViewActivityRealcopy.this);
//
//                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
//                    messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
//
//                    EventBus.getDefault().post(messageEvent);
//                }
//
//                break;
//
//            case (MessageFactory.location + ""):
//
//                for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
//                    ChatappContactModel userData = selectedContactsList.get(contactIndex);
//
//                    String data = msgItem.getTextMessage();
//                    String webLink = msgItem.getWebLink();
//                    String webLinkTitle = msgItem.getWebLinkTitle();
//                    String webLinkDesc = msgItem.getWebLinkDesc();
//                    String webLinkImgUrl = msgItem.getWebLinkImgUrl();
//                    String webLinkThumb = msgItem.getWebLinkImgThumb();
//
//                    SendMessageEvent messageEvent = new SendMessageEvent();
//                    LocationMessage message = (LocationMessage) MessageFactory.getMessage(MessageFactory.location, ChatViewActivityRealcopy.this);
//                    JSONObject msgObj;
//
//                    msgObj = (JSONObject) message.getMessageObject(userData.get_id(), data, false);
//
//
//                    MessageItemChat item = message.createMessageItem(true, data, MessageFactory.DELIVERY_STATUS_NOT_SENT,
//                            userData.get_id(), userData.getFirstName(), webLinkTitle, webLinkDesc, webLink, webLinkImgUrl, webLinkThumb, isExpiry, expireTime);
//                    msgObj = (JSONObject) message.getLocationObject(msgObj, webLinkTitle, webLinkDesc, webLink, webLinkImgUrl, webLinkThumb);
//                    messageEvent.setMessageObject(msgObj);
//
//                    item.setSenderMsisdn(userData.getNumberInDevice());
//                    item.setSenderName(userData.getFirstName());
//                    MessageDbController db = CoreController.getDBInstance(ChatViewActivityRealcopy.this);
//
//                    db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
//                    messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
//
//
//                    EventBus.getDefault().post(messageEvent);
//                }
//
//                break;
//        }
//    }
//
//
//    public void alertTimer() {
//        AlertDialog.Builder builderSingle = new AlertDialog.Builder(ChatViewActivityRealcopy.this);
//        builderSingle.setTitle("Select Time");
//
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(ChatViewActivityRealcopy.this, android.R.layout.select_dialog_singlechoice);
//        arrayAdapter.add("30 Seconds");
//        arrayAdapter.add("1 Minute");
//        arrayAdapter.add("5 Minute");
//        arrayAdapter.add("30 Minute");
//
//        arrayAdapter.add("Off");
//
//
//        builderSingle.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                switch (which) {
//                    case 0:
//                        capture_timer.setImageResource(R.drawable.clock_verify_on);
//                        isExpiry = "1";
//                        expireTime = "30";
//                        break;
//                    case 1:
//                        capture_timer.setImageResource(R.drawable.clock_verify_on);
//
//                        isExpiry = "1";
//                        expireTime = "60";
//                        break;
//                    case 2:
//                        capture_timer.setImageResource(R.drawable.clock_verify_on);
//                        isExpiry = "1";
//                        expireTime = "300";
//                        break;
//                    case 3:
//                        capture_timer.setImageResource(R.drawable.clock_verify_on);
//                        isExpiry = "1";
//                        expireTime = "1800";
//                        break;
//
//                    case 4:
//                        capture_timer.setImageResource(R.drawable.clock_verify);
//                        isExpiry = "0";
//                        expireTime = "0";
//                        break;
//
//                }
//                dialog.dismiss();
////                AlertDialog.Builder builderInner = new AlertDialog.Builder(ChatViewActivity.this);
////                builderInner.setMessage(strName);
////                builderInner.setTitle("Your Selected Item is");
////                builderInner.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog,int which) {
////                        dialog.dismiss();
////                    }
////                });
////                builderInner.show();
//            }
//        });
//        builderSingle.show();
//    }
//
//
//    public void timerReceiver() {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.nowletschat.android.timerreceiver");
//        registerReceiver(timerReceiver, intentFilter);
//
//
//    }
//
//
//
//    public void downloadReceiver() {
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction("com.file.download.completed");
//        registerReceiver(fileDownloadReceiver, intentFilter);
//
//
//    }
//
//
//
//    TimerReceiver timerReceiver = new TimerReceiver() {
//        @Override
//        public void onReceive(Context context, final Intent intent) {
//            super.onReceive(context, intent);
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    deletespecific((MessageItemChat) intent.getSerializableExtra("messageItem"));
//
//                    // deletespecific()
//                    //  deletespecific((MessageItemChat) intent.getSerializableExtra("messageItem"));
//                    //      loadFromDB();
//                }
//            }, 100);
//
//        }
//    };
//
//
//    FileDownloadReceiver fileDownloadReceiver = new FileDownloadReceiver() {
//        @Override
//        public void onReceive(Context context, final Intent intent) {
//            super.onReceive(context, intent);
//
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    updateDownload(intent.getStringExtra("msgId"),"1");
//
//                    // deletespecific()
//                    //  deletespecific((MessageItemChat) intent.getSerializableExtra("messageItem"));
//                    //      loadFromDB();
//                }
//            }, 100);
//
//
//
//        }
//    };
//
//
//    private void deletespecific(MessageItemChat msgItem) {
//
//        int index=-1,selectedIndex=-1;
//        for(int i=0;i<mChatData.size();i++)
//        {
//            if(mChatData.get(i).getMessageId().equals(msgItem.getMessageId()))
//            {
//                index=i;
//            }
//
//        }
//
//        for(int j=0;j<selectedChatItems.size();j++)
//        {
//            if(selectedChatItems.get(j).getMessageId().equals(msgItem.getMessageId()))
//            {
//                selectedIndex=j;
//            }
//        }
//
//        if (msgItem.isMediaPlaying() && index!=-1) {
//            mAdapter.stopAudioOnMessageDelete(index);
//        }
//        String lastMsgStatus;
//        if (mChatData.size() == 1) {
//            lastMsgStatus = "1";
//        } else {
//            lastMsgStatus = "0";
//        }
//        String docId = from + "-" + to;
//
//        if (mConvId == null) {
//            mConvId = msgItem.getConvId();
//        }
//
//
//        if(selectedIndex!=-1) {
//            selectedChatItems.remove(selectedIndex);
//        }
//        if(selectedChatItems.size()==0)
//        {
//            showUnSelectedActions();
//        }
//
//
//        if(index!=-1) {
//
//            mChatData.remove(index);
//
//            mAdapter.notifyDataSetChanged();
//
//
//            //       db.deleteSingleMessage(docId, msgItem.getMessageId(), chatType, "other");
//            //        db.deleteChatListPage(docId, msgItem.getMessageId(), chatType, "other");
//            db.deleteChatMessage(docId, msgItem.getMessageId(), chatType);
////            db.updateTempDeletedMessage(msgItem.getRecordId(), deleteMsgObj);
//        }
//
//
//    }
//
//    public void showKeyboard() {
//        if (getCurrentFocus() != null) {
//            InputMethodManager inputMethodManager =
//                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            inputMethodManager.toggleSoftInputFromWindow(
//                    getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
//        }
//    }
//
//}
//
//
////  UPDATE GROUP MESSAGE STATUS
//
////    private void updateGroupMsgStatus(JSONObject objects) {
////        try {
////            String err = objects.getString("err");
////            if (err.equalsIgnoreCase("0")) {
////                String from = objects.getString("from");
////                String groupId = objects.getString("groupId");
////                String msgId = objects.getString("msgId");
////                String deliverStatus = objects.getString("status");
//////                String recordId = objects.getString("recordId");
////                String docId = sessionManager.getCurrentUserID().concat("-").concat(groupId).concat("-g");
////                String resMsgId = docId.concat("-").concat(msgId);
////                String timeStamp = objects.getString("currenttime");
////
////
////                for (int i = 0; i < mChatData.size(); i++) {
////                    MessageItemChat dbItem = mChatData.get(i);
////                    if (dbItem != null && dbItem.getMessageId().equalsIgnoreCase(resMsgId)) {
////
////
////                        if (deliverStatus.equalsIgnoreCase(MessageFactory.GROUP_MSG_DELIVER_ACK)) {
////                            if (dbItem.isSelf() && dbItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)) {
////                                updateGroupMsgDeliverStatus(docId,from,dbItem,timeStamp);
////                            } else if (!dbItem.isSelf() && from.equals(sessionManager.getCurrentUserID())) {
////                                dbItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);
////                            }
////                        } else if (deliverStatus.equalsIgnoreCase(MessageFactory.GROUP_MSG_READ_ACK)) {
////                            if (dbItem.isSelf() && !dbItem.getDeliveryStatus().equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
////                                updateGroupMsgReadStatus(docId,from,dbItem,timeStamp);
////                            } else if (!dbItem.isSelf() && from.equals(sessionManager.getCurrentUserID())) {
////                                dbItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_READ);
////                            }
////                        }
////
////                        break;
////                    }
////                    else {
////
////                        if (deliverStatus.equalsIgnoreCase(MessageFactory.GROUP_MSG_DELIVER_ACK)) {
////                            if (dbItem.isSelf() && dbItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)) {
////                                updateGroupMsgDeliverStatus(docId,from,dbItem,timeStamp);
////                            } else if (!dbItem.isSelf() && from.equals(sessionManager.getCurrentUserID())) {
////                                dbItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);
////                            }
////                        } else if (deliverStatus.equalsIgnoreCase(MessageFactory.GROUP_MSG_READ_ACK)) {
////                            if (dbItem.isSelf() && !dbItem.getDeliveryStatus().equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
////                                updateGroupMsgReadStatus(docId,from,dbItem,timeStamp);
////                            } else if (!dbItem.isSelf() && from.equals(sessionManager.getCurrentUserID())) {
////                                dbItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_READ);
////                            }
////                        }
////                    }
////
////
////                }
////                mAdapter.notifyDataSetChanged();
////            }
////        } catch (JSONException e) {
////            e.printStackTrace();
////        }
////    }
//
//
//
/////  CHAT OLD SCROLL
//
//
//
//
////  super.onScrollStateChanged(recyclerView, newState);
////
////                                                      if (newState == RecyclerView.SCROLL_STATE_IDLE) {
////                                                          visibleItemCount = mLayoutManager.getChildCount();
////                                                          totalItemCount = mLayoutManager.getItemCount();
////                                                          pastVisiblesItems = mLayoutManager.findFirstVisibleItemPosition();
////                                                          lastvisibleitempostion = mLayoutManager.findLastVisibleItemPosition();
////
////                                                          if (pastVisiblesItems > -1 && lastvisibleitempostion > -1) {
////                                                              if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
////                                                                  iBtnScroll.setVisibility(View.GONE);
////                                                                  unreadcount.setVisibility(View.GONE);
////                                                                  unreadmsgcount = 0;
////                                                                  changeBadgeCount(mConvId);
////                                                              } else {
////                                                                  iBtnScroll.setVisibility(View.VISIBLE);
////                                                              }
////
////                                                              MessageItemChat scrolledMsgItem = mChatData.get(pastVisiblesItems);
////                                                              final MessageItemChat lastMsgItem = mChatData.get(lastvisibleitempostion);
////
////                                                              String scrolledMsgId = scrolledMsgItem.getMessageId();
//////                                                              if (scrolledMsgId.equalsIgnoreCase(mFirstVisibleMsgId)
//////                                                                      && isMessageLoadedOnScroll) {
////
////                                                              if (!recyclerView.canScrollVertically(-1)) {
////                                                                  // top of scroll view
//////                                                                      frameL.setVisibility(View.VISIBLE);
////                                                                  Animation animation = AnimationUtils.loadAnimation(ChatViewActivity.this, R.anim.fade_out);
////                                                                  //use this to make it longer:  animation.setDuration(1000);
////                                                                  animation.setAnimationListener(new Animation.AnimationListener() {
////                                                                      @Override
////                                                                      public void onAnimationStart(Animation animation) {
////                                                                      }
////
////                                                                      @Override
////                                                                      public void onAnimationRepeat(Animation animation) {
////                                                                      }
////
////                                                                      @Override
////                                                                      public void onAnimationEnd(Animation animation) {
////                                                                          frameL.setVisibility(View.VISIBLE);
////
////                                                                      }
////                                                                  });
////                                                                  frameL.startAnimation(animation);
////
//////                                                                      frameL.startAnimation();
//////                                                                      frameL.getLayoutTransition().enableTransitionType(LayoutTransition.APPEARING);
////
//////                                                                      progressBar.bringToFront();
//////                                                                      progressBar.setVisibility(View.VISIBLE);
////
////                                                              }
////
////                                                              isMessageLoadedOnScroll = false;
////
////                                                              String ts = mChatData.get(0).getTS();
////                                                              ArrayList<MessageItemChat> items = db.selectAllMessagesWithLimit(docId, chatType,
////                                                                      ts, MessageDbController.MESSAGE_PAGE_LOADED_LIMIT + 100);
////                                                              Collections.sort(items, msgComparator);
////
////                                                              if (items.size() < MessageDbController.MESSAGE_PAGE_LOADED_LIMIT - 1) {
////                                                                  if (items.size() == 0) {
////                                                                      isMessageLoadedOnScroll = false;
////                                                                  } else {
////                                                                      isMessageLoadedOnScroll = true;
////                                                                  }
////
////                                                              }
////                                                              Log.d(TAG, "onScrollStateChanged: items Size " + items.size());
////
////                                                              for (int i = 0; i < items.size(); i++) {
////                                                                  mChatData.add(i, items.get(i));
////                                                                  mAdapter.notifyItemInserted(mChatData.size());
////                                                              }
////                                                              mAdapter.notifyDataSetChanged();
////
////
////                                                              Animation animation = AnimationUtils.loadAnimation(ChatViewActivity.this, R.anim.fade_out);
////                                                              //use this to make it longer:  animation.setDuration(1000);
////                                                              animation.setAnimationListener(new Animation.AnimationListener() {
////                                                                  @Override
////                                                                  public void onAnimationStart(Animation animation) {
////                                                                  }
////
////                                                                  @Override
////                                                                  public void onAnimationRepeat(Animation animation) {
////                                                                  }
////
////                                                                  @Override
////                                                                  public void onAnimationEnd(Animation animation) {
////                                                                      frameL.setVisibility(View.GONE);
////
////                                                                  }
////                                                              });
////                                                              frameL.startAnimation(animation);
//////                                                                          frameL.setVisibility(View.GONE);
//////                                                                          frameL.getLayoutTransition().enableTransitionType(LayoutTransition.DISAPPEARING);
////
////                                                              overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//////                                                                          mAdapter.notifyDataSetChanged();
////                                                              final int tempIndex = mChatData.indexOf(lastMsgItem);
////                                                              mFirstVisibleMsgId = mChatData.get(0).getMessageId();
////                                                              if (tempIndex > -1) {
////                                                                  Log.d(TAG, "onScrollStateChanged: scrollToPosition: " + tempIndex);
////                                                                  recyclerView_chat.post(new Runnable() {
////                                                                      @Override
////                                                                      public void run() {
////                                                                          recyclerView_chat.scrollToPosition(tempIndex - 1);
////                                                                      }
////                                                                  });
////                                                              }
////                                                              //Do something after 100ms
////
////
////                                                          }
////                                                      }
////                                                          Toast.makeText(ChatViewActivity.this, "Scrolled", Toast.LENGTH_SHORT).show();
////                                                      }
//
