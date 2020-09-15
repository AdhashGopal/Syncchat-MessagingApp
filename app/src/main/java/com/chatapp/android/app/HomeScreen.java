package com.chatapp.android.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.NotificationManager;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.chatapp.android.R;
import com.chatapp.android.app.adapter.PagerAdapter;
import com.chatapp.android.app.calls.CallHistoryFragment;
import com.chatapp.android.app.contactlist.ContactSearchActivity;
import com.chatapp.android.app.dialog.ChatLockPwdDialog;
import com.chatapp.android.app.dialog.CustomAlertDialog;
import com.chatapp.android.app.utils.AppUpdateDialogAsync;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.PreferenceConnector;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.ShortcutBadgeManager;
import com.chatapp.android.core.chatapphelperclass.ChatappDialogUtils;
import com.chatapp.android.core.chatapphelperclass.ChatappImageUtils;
import com.chatapp.android.core.chatapphelperclass.ChatappPermissionValidator;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.message.PictureMessage;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.service.Constants;
import com.chatapp.android.core.service.ServiceRequest;
import com.chatapp.android.core.socket.IdentifyAppKilled;
import com.chatapp.android.core.socket.MessageService;
import com.chatapp.android.core.socket.SocketManager;
import com.chatapp.android.core.uploadtoserver.FileUploadDownloadManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabLayout;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WAKE_LOCK;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class HomeScreen extends CoreActivity implements SearchView.OnQueryTextListener, View.OnClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String FROM_MISSED_CALL_NOTIFICATION = "FromMissedNotify";
    final static int REQUEST_LOCATION = 199;
    public static SessionManager sessionManager;
    public static boolean callHistActive = false;
    private static Socket mSocket;
    final int PERMISSION_REQUEST_CODE = 111;
    final Context context = this;
    private final int REQUEST_CODE_PERMISSION_MULTIPLE = 123;
    private final AtomicInteger mRefreshRequestCounter = new AtomicInteger(0);
    private final int GALLERY_REQUEST_CODE = 1;
    private final int CAMERA_REQUEST_CODE = 2;
    private final int AUDIO_RECORD_PERMISSION_REQUEST_CODE = 14;
    PendingResult<LocationSettingsResult> result;
    Bitmap photo;
    String mUserId, uniqueCurrentID;
    Toolbar toolbar;
    Cursor cur;
    String status = "", from, myID, username, pwd, stat;
    TabLayout tabLayout;
    TextView tvName;
    Uri cameraImageUri;
    //    private DrawerLayout mDrawerLayout;
    MessageItemChat messageItemChat;
    SharedPreferences user_mes_del_Pref;
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    boolean navigate_check = false;
    String manufacturer = "";
    AlertDialog.Builder alertDialog;
    private Typeface avnRegFont, avnDemiFont;
    private UserInfoSession userInfoSession;
    public ViewPager viewPager;
    private boolean isDeninedRTPs = false;
    private boolean showRationaleRTPs = false;
    private ArrayList<ChatappPermissionValidator.Constants> myPermissionConstantsArrayList;
    //    NavigationView navigationView;
    private ImageView bgimage;
    private ImageView ivProfilePic;
    private boolean isDeviceAlertShowing;
    //--------------------------------Handy Slider-------------------------
    private String[] title;
    private int[] icon;
    private ListView mDrawerList;
    private AppUpdateDialogAsync appUpdateDialogAsync;
    private ImageButton ibNewChat;


    /**
     * delete message for local session based on response
     */
    private ServiceRequest.ServiceListener verifcationListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            hideProgressDialog();
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.getString("errNum").equals("0")) {
                    SharedPreferences.Editor editor = user_mes_del_Pref.edit();
                    editor.remove("delete");
                    editor.clear().commit();
                    Toast.makeText(HomeScreen.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();

                } else if (jsonObject.getString("errNum").equals("1")) {
                    SharedPreferences.Editor editor = user_mes_del_Pref.edit();
                    editor.remove("delete");
                    editor.clear().commit();
                    Toast.makeText(HomeScreen.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
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


    /**
     * Calender for set time
     *
     * @param date based on date param, we get time
     * @param i
     * @return value
     */
    public static Date addDay(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTime();
    }

    private void getGroupList() {
        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_GET_GROUP_LIST);

        try {
            JSONObject object = new JSONObject();
            object.put("from", SessionManager.getInstance(this).getCurrentUserID());
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            NotificationManager notificationCallManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationCallManager != null) {
//                        notificationCallManager.notify(555, notificationBuilder.build());
                notificationCallManager.cancel(5556);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (getIntent() != null && getIntent().getBooleanExtra("fromDisconnectCall", false)) {
            finish();
        }
        SessionManager.getInstance(HomeScreen.this).setResetPinStatus(1);
        MessageService.clearNotificationData();
        ibNewChat = (ImageButton) findViewById(R.id.ibNewChat);

        tabLayout = (TabLayout) findViewById(R.id.tablayout);
        tabLayout.addTab(tabLayout.newTab().setText("CHATS"));
        //tabLayout.addTab(tabLayout.newTab().setText("Groups"));
        tabLayout.addTab(tabLayout.newTab().setText("CALLS"));
        tabLayout.addTab(tabLayout.newTab().setText("CONTACTS"));
        //tabLayout.addTab(tabLayout.newTab().setText("STATUS"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        uniqueCurrentID = SessionManager.getInstance(this).getCurrentUserID();
        userInfoSession = new UserInfoSession(HomeScreen.this);

        avnRegFont = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        avnDemiFont = CoreController.getInstance().getAvnNextLTProDemiTypeface();

        /*Intent backIntent = new Intent(HomeScreen.this, BackupTest.class);
        startActivity(backIntent);*/

//        requestAudioRecordPermission();
        //  requestPermission();
        manufacturer = android.os.Build.MANUFACTURER;

        System.out.println("===device manufacturer" + manufacturer);

        //if(manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
        if (manufacturer.equalsIgnoreCase("xiaomi") || manufacturer.equalsIgnoreCase("Xiaomi") || manufacturer.equalsIgnoreCase("oppo") || manufacturer.equalsIgnoreCase("vivo")) {
            System.out.println("");
           /* if(PreferenceConnector.readString(HomeScreen.this,PreferenceConnector.AUTOSTART,
                    "").length()<2||PreferenceConnector.readString(HomeScreen.this,
                    PreferenceConnector.AUTOSTART,"").equals("autostart")){
                requestPermission();
                requestAutostart();
            }else{
                requestPermission();
            }*/

            if (PreferenceConnector.readString(HomeScreen.this, PreferenceConnector.AUTOSTART,
                    "").equals("autostarton")) {
                requestPermission();
            } else {
                requestPermission();
                requestAutostart();
            }

        } else {
            requestPermission();
        }
      /*
        if(manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            //   intent.setClassName("com.miui.powerkeeper","com.miui.powerkeeper.ui.PowerHideModeActivity");
            startActivity(intent);
        }*/


        startService(new Intent(this, IdentifyAppKilled.class));

        // checkAndStartService();

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));
//        getSupportActionBar().setDisplayShowTitleEnabled(false);
//        overrideFonts(this, findViewById(R.id.toolbar_title));
        title = new String[]{"username", getResources().getString(R.string.navigation_label_home),
                getResources().getString(R.string.navigation_label_myOrders),
                getResources().getString(R.string.navigation_label_money),
                getResources().getString(R.string.navigation_label_transactions1),
                getResources().getString(R.string.navigation_label_chat),
                getResources().getString(R.string.navigation_label_notification1),
                getResources().getString(R.string.navigation_label_review1),
                getResources().getString(R.string.navigation_label_invite),
                getResources().getString(R.string.navigation_label_report_issue),
                getResources().getString(R.string.navigation_label_aboutUs),
                getResources().getString(R.string.navigation_label_logout),
        };

        icon = new int[]{R.drawable.no_profile_image_avatar_icon, R.drawable.home_icon,
                R.drawable.my_orders_icon, R.drawable.plumbal_money, R.drawable.icon_menu_transaction, R.drawable.icon_menu_chat, R.drawable.icon_menu_notification, R.drawable.icon_menu_review,
                R.drawable.invite_and_earn, R.drawable.report_issue_icon, R.drawable.aboutus_icon, R.drawable.logout};


        Bundle bundle = getIntent().getExtras();
        boolean fromMissedNotify = false;
        if (bundle != null) {
            fromMissedNotify = bundle.getBoolean(FROM_MISSED_CALL_NOTIFICATION, false);

            if (!fromMissedNotify) {
                from = bundle.getString("fromNotify");
                myID = bundle.getString("ID");
                username = bundle.getString("uname");
                stat = bundle.getString("status");
                pwd = bundle.getString("pwd");
                messageItemChat = (MessageItemChat) bundle.getSerializable("MessageItem");

                if (!TextUtils.isEmpty(from)) {

                    if (from.equalsIgnoreCase("service")) {
                        //Toast.makeText(this, "Sorry! This Chat is Already Locked..Unlock to proceed...",Toast.LENGTH_SHORT).show();
                        performNavigationToChatView(myID, username, stat, pwd, messageItemChat);
                    }
                }
            }
        }

        Session session = new Session(this);
        //session.putmessagePrefs("");


        viewPager = (ViewPager) findViewById(R.id.viewpager);

        ChatList tab1 = new ChatList();
        //  GroupChatList tab2 = new GroupChatList();
        CallHistoryFragment tab3 = new CallHistoryFragment();
        //  SettingContactFragment tab4 = new StatusHomeFragment();

//        SettingContactFragment tab4 = new SettingContactFragment();
        ContactSearchActivity tab4 = new ContactSearchActivity();
        SharedPreferences sharedpreferences = getSharedPreferences("url", Context.MODE_PRIVATE);
        String path = sharedpreferences.getString("url", "");

       /* if(!path.equals("")) {
            String[] dataArray=path.split("=");
            String idStr = dataArray[1];
            try {
                idStr= URLDecoder.decode(idStr, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            tab4.deviceId = idStr;
            sharedpreferences.edit().remove("url").apply();

        }*/

        final List<Fragment> tabFragments = new ArrayList<>();
        tabFragments.add(tab1);
        tabFragments.add(tab3);
        tabFragments.add(tab4);

        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tabFragments);
        //int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(0);
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        if (fromMissedNotify) {
            viewPager.setCurrentItem(2);
        }

        if (!path.equals("")) {
            viewPager.setCurrentItem(2);
        }

        ibNewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tabLayout.getTabAt(2).select();

            }
        });

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                View view = getCurrentFocus();
                if (view != null) {
                    if (viewPager.getCurrentItem() != 2) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                    if (viewPager.getCurrentItem() == 0) {
                        ibNewChat.setVisibility(View.VISIBLE);
                    } else {
                        ibNewChat.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                Fragment selectedFragment = tabFragments.get(tab.getPosition());
                if (selectedFragment instanceof CallHistoryFragment) {

                    CallHistoryFragment ob = (CallHistoryFragment) selectedFragment;
                    ob.refreshCallsList();
                    callHistActive = true;
                } else if (selectedFragment instanceof ContactSearchActivity) {

                    ibNewChat.setVisibility(View.GONE);
                } else {
                    callHistActive = false;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        if (!MessageService.isStarted()) {
            startService(new Intent(this, MessageService.class));
        }

//        init_navigator();
        sessionManager = SessionManager.getInstance(HomeScreen.this);
        sessionManager.IsapplicationisKilled(false);
        sessionManager.setPushdisplay(true);

//        if (SessionManager.getInstance(HomeScreen.this).isLoginKeySent() && !SessionManager.getInstance(HomeScreen.this).isValidDevice()) {
//            showDeviceChangedAlert();
//        } else {
        createUser();
        validateDeviceWithAccount();
        getServerTime();

//            Date startDate = null;
//            Date cDate = new Date();
        final DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
//            if (!sessionManager.isratingenabled() && sessionManager.iswelcomeenabled()) {

              /*  try {
                    startDate = df.parse(sessionManager.gettime());
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/
//                if (startDate.before(cDate)) {
//
//                    final Dialog dialog = new Dialog(HomeScreen.this);
//
//                    AvnNextLTProDemiTextView ok, notnow;
//                    final ImageView smiley_ic;
//                    final AvnNextLTProRegTextView likeit, hated;
//                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                    dialog.setContentView(R.layout.dialog_ratingbar);
//                    ok = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.submit);
//                    notnow = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.notnow);
//                    SeekBar seekBar = (SeekBar) dialog.findViewById(R.id.sbDuration);
//                    smiley_ic = (ImageView) dialog.findViewById(R.id.smiley_ic);
//                    seekBar.setProgress(60);
//                    seekBar.incrementProgressBy(10);
//                    seekBar.setMax(100);
//                    likeit = (AvnNextLTProRegTextView) dialog.findViewById(R.id.likeit);
//                    hated = (AvnNextLTProRegTextView) dialog.findViewById(R.id.hated);
//                    dialog.setCanceledOnTouchOutside(false);
//                    dialog.setCancelable(false);
//                    dialog.show();
//                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//
//                        @Override
//                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//                            if (progress < 10 && fromUser) {
//                                seekBar.setProgress(10);
//                            }
//
//                            if (progress > 0 && progress < 20) {
//                                hated.setTextColor(ContextCompat.getColor(HomeScreen.this, R.color.black));
//                                likeit.setTextColor(ContextCompat.getColor(HomeScreen.this, R.color.unselect_color));
//                                likeit.setText(getResources().getString(R.string.rating_txt_like));
//                                smiley_ic.setImageResource(R.mipmap.smiley_hateit);
//                                status = getResources().getString(R.string.rating_txt_hate);
//                            } else if (progress > 20 && progress < 40) {
//                                hated.setTextColor(ContextCompat.getColor(HomeScreen.this, R.color.unselect_color));
//                                likeit.setText(getResources().getString(R.string.rating_txt_dont_like));
//                                likeit.setTextColor(ContextCompat.getColor(HomeScreen.this, R.color.black));
//                                smiley_ic.setImageResource(R.mipmap.smiley_dontlike);
//                                status = getResources().getString(R.string.rating_txt_dont_like);
//                            } else if (progress > 40 && progress < 60) {
//                                hated.setTextColor(ContextCompat.getColor(HomeScreen.this, R.color.unselect_color));
//                                likeit.setText(getResources().getString(R.string.rating_txtit_was_ok));
//                                likeit.setTextColor(ContextCompat.getColor(HomeScreen.this, R.color.black));
//                                smiley_ic.setImageResource(R.mipmap.smiley_like);
//                                status = getResources().getString(R.string.rating_txtit_was_ok);
//                            } else if (progress > 60 && progress < 80) {
//                                hated.setTextColor(ContextCompat.getColor(HomeScreen.this, R.color.unselect_color));
//                                likeit.setText(getResources().getString(R.string.rating_txt_like));
//                                likeit.setTextColor(ContextCompat.getColor(HomeScreen.this, R.color.black));
//                                smiley_ic.setImageResource(R.mipmap.smileylikedit);
//                                status = getResources().getString(R.string.rating_txt_like);
//                            } else if (progress > 80 && progress < 100) {
//                                hated.setTextColor(ContextCompat.getColor(HomeScreen.this, R.color.unselect_color));
//                                likeit.setText(getResources().getString(R.string.rating_txt_love));
//                                likeit.setTextColor(ContextCompat.getColor(HomeScreen.this, R.color.black));
//                                smiley_ic.setImageResource(R.mipmap.smiley_love);
//                                status = getResources().getString(R.string.rating_txt_love);
//                            }
//                        }
//
//                        @Override
//                        public void onStartTrackingTouch(SeekBar seekBar) {
//
//                        }
//
//                        @Override
//                        public void onStopTrackingTouch(SeekBar seekBar) {
//
//                        }
//                    });
//                    ok.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            // String rating=String.valueOf(ratingbar.getRating());
//                            sendRatingToServer(status);
//                            launchMarket();
//                            sessionManager.setrating(true);
//                            dialog.dismiss();
//                        }
//                    });
//                    notnow.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Date cDate = new Date();
//                            Date ss = addDay(cDate, 1);
//                            String reportDate = df.format(ss);
//                            sessionManager.puttime(reportDate);
//                            dialog.dismiss();
//                        }
//                    });
//
//                }
//            }
//        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor((getResources().getColor(R.color.Statusbar)));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        } else {
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }


        if (mSocket == null || !mSocket.connected()) {
            try {
                mSocket = IO.socket(Constants.SOCKET_URL);
                if (!mSocket.connected())
                    mSocket.connect();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }

        createTabIcons();

        user_mes_del_Pref = getSharedPreferences("user_message_delete", Context.MODE_PRIVATE);
        String del = user_mes_del_Pref.getString("delete", "");
        if (del.equals("1")) {
            MessageDbController db = CoreController.getDBInstance(HomeScreen.this);
            db.deleleChatTable();
            showProgressDialog();
            String id = sessionManager.getCurrentUserID();
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("id", sessionManager.getCurrentUserID());
            ServiceRequest request = new ServiceRequest(this);
            request.makeServiceRequest(Constants.MESSAGES_DELETE, Request.Method.POST, params, verifcationListener);
        }

        hideKeyboard();

    }

    private void requestAutostart() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(HomeScreen.this, R.style.MyDialogTheme);

        // Setting Dialog Title
        alertDialog.setTitle("aChat");

        // Setting Dialog Message
        alertDialog.setMessage("Add your app into Autostart to receive notification's from aChat in background");

        // Setting Icon to Dialog
        //  alertDialog.setIcon(R.drawable.tick);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                try {


                    PreferenceConnector.writeString(HomeScreen.this, PreferenceConnector.AUTOSTART, "autostarton");
                    // Write your code here to invoke YES event

                    Intent intent = new Intent();

                    switch (manufacturer) {

                        case "Xiaomi":
                            intent.setComponent(new ComponentName("com.miui.securitycenter",
                                    "com.miui.permcenter.autostart.AutoStartManagementActivity"));

                        case "xiaomi":
                            intent.setComponent(new ComponentName("com.miui.securitycenter",
                                    "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                            break;
                        case "oppo":
                            intent.setComponent(new ComponentName("com.coloros.safecenter",
                                    "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                            break;
                        case "vivo":
                            intent.setComponent(new ComponentName("com.vivo.permissionmanager",
                                    "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                            break;
                    }

                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton("LATER", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                PreferenceConnector.writeString(HomeScreen.this, PreferenceConnector.AUTOSTART, "autostart");
                // Write your code here to invoke NO event
                Toast.makeText(getApplicationContext(), "Your app will not receive notification's in background, To enable" +
                        "Go to Settings -> Autostart -> Add your app manually", Toast.LENGTH_LONG).show();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

       /* Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
        //   intent.setClassName("com.miui.powerkeeper","com.miui.powerkeeper.ui.PowerHideModeActivity");
        startActivity(intent);*/
    }

    private void requestAudioRecordPermission() {
        ActivityCompat.requestPermissions(HomeScreen.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, AUDIO_RECORD_PERMISSION_REQUEST_CODE);
    }


    /**
     * Need to allowed the all Run time Permission
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            myPermissionConstantsArrayList = new ArrayList<>();

            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.PERMISSION_CAMERA);
            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.PERMISSION_READ_EXTERNAL_STORAGE);
            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.Record_setting);
            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.PERMISSION_WRITE_EXTERNAL_STORAGE);
            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.MANAGE_DOCUMENTS);
            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.WAKE_LOCK);

            ActivityCompat.requestPermissions(this, new
                    String[]{CAMERA, READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE, RECORD_AUDIO, MODIFY_AUDIO_SETTINGS, WAKE_LOCK}, REQUEST_CODE_PERMISSION_MULTIPLE);

            if (ChatappPermissionValidator.checkPermission(HomeScreen.this, myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE)) {
                onPermissionGranted();
            }
        } else {
            onPermissionGranted();
        }
    }

    private void onPermissionGranted() {

    }

    /**
     * create new user
     */

    public void createUser() {
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_CREATE_USER);
        JSONObject object = new JSONObject();
        try {
            object.put("_id", SessionManager.getInstance(getApplicationContext()).getCurrentUserID());
            object.put("mode", "phone");
            object.put("chat_type", "single");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageEvent.setMessageObject(object);
        EventBus.getDefault().post(messageEvent);
    }

    public void overrideFonts(final Context context, final View v) {
        try {
            if (v instanceof ViewGroup) {
                ViewGroup vg = (ViewGroup) v;
                for (int i = 0; i < vg.getChildCount(); i++) {
                    View child = vg.getChildAt(i);
                    overrideFonts(context, child);
                }
            } else if (v instanceof TextView) {
                TextView tv = ((TextView) v);
                if (tv.getText().toString().equalsIgnoreCase(getResources().getString(R.string.app_name))) {
                    tv.setTypeface(avnDemiFont);
                } else {
                    tv.setTypeface(avnRegFont);
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * Validate the device with account
     */
    public void validateDeviceWithAccount() {
        String userId = SessionManager.getInstance(HomeScreen.this).getCurrentUserID();

        try {
            JSONObject msgObj = new JSONObject();
            msgObj.put("from", userId);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_CHECK_MOBILE_LOGIN_KEY);
            event.setMessageObject(msgObj);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


//    private void init_navigator() {
//        // Navigation Drawer
//        ActionBarDrawerToggle mActionBarDrawerToggle;
//
//        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
//
//        // Code here will be triggered once the drawer closes as we don't want anything to happen so we leave this blank
//        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
//                toolbar, R.string.navigation_drawer_opened, R.string.navigation_drawer_closed);
//
//        //calling sync state is necessary or else your hamburger icon wont show up
//        mActionBarDrawerToggle.syncState();
//
//        mDrawerLayout.addDrawerListener(HomeScreen.this);
//
//        // mNavigationView.setItemIconTintList(null);
//        navigationView = (NavigationView) findViewById(R.id.navigation_view);
//
//
//        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
//
//            @Override
//            public boolean onNavigationItemSelected(MenuItem menuItem) {
//
//                int id = menuItem.getItemId();
//
//                switch (id) {
//                    case R.id.chat:
//                        viewPager.setCurrentItem(0);
//                        mDrawerLayout.closeDrawer(GravityCompat.START);
//                        return true;
//                    case R.id.contact:
//                        /*viewPager.setCurrentItem(2);
//                        mDrawerLayout.closeDrawer(GravityCompat.START);*/
//                        Intent contactIntent = new Intent(HomeScreen.this, SettingContact.class);
//                        startActivity(contactIntent);
//                        mDrawerLayout.closeDrawer(GravityCompat.START);
//                        return true;
////                    case R.id.acc:
////                        Intent intent = new Intent(getApplicationContext(), Account_main_list.class);
////                        startActivity(intent);
////                        mDrawerLayout.closeDrawer(GravityCompat.START);
////                        return true;
//                    case R.id.abt:
//                        Intent intent_abt = new Intent(getApplicationContext(), AboutHelp.class);
//                        startActivity(intent_abt);
//                        mDrawerLayout.closeDrawer(GravityCompat.START);
//                        return true;
////                    case R.id.datause:
////                        Intent intent_datausage = new Intent(getApplicationContext(), DataUsage.class);
////                        startActivity(intent_datausage);
////                        mDrawerLayout.closeDrawer(GravityCompat.START);
////                        return true;
//                    case R.id.notify:
//                        Intent intent_notify = new Intent(getApplicationContext(), NotificationSettings.class);
//                        startActivity(intent_notify);
//                        mDrawerLayout.closeDrawer(GravityCompat.START);
//                        return true;
//
//
//                }
//                mDrawerLayout.closeDrawer(GravityCompat.START);
//                return true;
//            }
//
//        });
//
//        overrideFonts(this, mDrawerLayout);
//    }

//    private void HandySliderActive(){
//        mMenuAdapter = new HomeMenuListAdapter(context, title, icon);
//        mDrawerList.setAdapter(mMenuAdapter);
//        mMenuAdapter.notifyDataSetChanged();
//    }

    /**
     * getting server time
     */
    private void getServerTime() {
        try {
            String userId = SessionManager.getInstance(HomeScreen.this).getCurrentUserID();
            JSONObject timeObj = new JSONObject();
            timeObj.put("from", userId);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_GET_SERVER_TIME);
            event.setMessageObject(timeObj);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * create tab view layout initialization
     */
    private void createTabIcons() {

        View tab1 = LayoutInflater.from(this).inflate(R.layout.count_on_tabs_view, null);
        TextView tab1Title = (TextView) tab1.findViewById(R.id.tvTitle);
//        TextView tab1Count = (TextView) tab1.findViewById(R.id.tvCount);
        tab1Title.setText("CHATS");
        tabLayout.getTabAt(0).setCustomView(tab1);

/*
        View tab2 = LayoutInflater.from(this).inflate(R.layout.count_on_tabs_view, null);
        TextView tab2Title = (TextView) tab2.findViewById(R.id.tvTitle);
//        TextView tab2Count = (TextView) tab2.findViewById(R.id.tvCount);
        tab2Title.setText("GROUPS");
        tabLayout.getTabAt(1).setCustomView(tab2);
*/

        View tab3 = LayoutInflater.from(this).inflate(R.layout.count_on_tabs_view, null);
        TextView tab3Title = (TextView) tab3.findViewById(R.id.tvTitle);
        tab3Title.setText("CALLS");
        tabLayout.getTabAt(1).setCustomView(tab3);


        View tab4 = LayoutInflater.from(this).inflate(R.layout.count_on_tabs_view, null);
        TextView tab4Title = (TextView) tab4.findViewById(R.id.tvTitle);
        tab4Title.setText("CONTACTS");
        tabLayout.getTabAt(2).setCustomView(tab4);


        changeTabTextCount();
    }

    /**
     * Check run time permission request
     */
    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            myPermissionConstantsArrayList = new ArrayList<>();
//            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.PERMISSION_READ_PHONE_STATE);
            //   myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.PERMISSSION_READ_CONTACTS);
            //    myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.PERMISSION_WRITE_CONTACTS);
            if (ChatappPermissionValidator.checkPermission(HomeScreen.this, myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE)) {
//                onPermissionGranted();
            }
        } else {
//            onPermissionGranted();
        }
    }

    /**
     * on run time permission result
     *
     * @param requestCode  permission request code
     * @param permissions  string[] permission
     * @param grantResults permission result
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_MULTIPLE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        String permission = permissions[i];
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            isDeninedRTPs = true;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                showRationaleRTPs = shouldShowRequestPermissionRationale(permission);
                            }
                        }
                        break;
                    }
                    onPermissionResult();
                } else {

                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }


//    private void onPermissionGranted() {
//        new getAllContactOfPhone().execute();
//    }

    @Override
    public void finishAfterTransition() {
        super.finishAfterTransition();
    }

    /**
     * run time permission result. if your denied the permission will be shown popup dialog
     */
    private void onPermissionResult() {
        if (isDeninedRTPs) {
            if (!showRationaleRTPs) {
                //goToSettings();
                ChatappDialogUtils.showPermissionDeniedDialog(HomeScreen.this);
            } else {
                isDeninedRTPs = false;
                ChatappPermissionValidator.checkPermission(this,
                        myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE);
            }
        } else {
//            onPermissionGranted();
        }
    }

    /**
     *  All permission result data
     * @param requestCode
     * @param resultCode
     * @param data  getting the data result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {

                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    beginCrop(selectedImageUri);
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
                beginCrop(cameraImageUri);
            } else {
                if (resultCode == Activity.RESULT_CANCELED) {

                } else {
                    Toast.makeText(this, "Sorry! Failed to capture image",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {

            if (data != null) {
                Uri uri = Crop.getOutput(data);
                String filePath = uri.getPath();

                try {
                    Bitmap alignedBitmap = ChatappImageUtils.getAlignedBitmap(ChatappImageUtils.getThumbnailBitmap(filePath, 150), filePath);
                    uploadImage(alignedBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *  Image crop
     * @param source Image uri data
     */
    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    /**
     * Upload image
     * @param circleBmp uploading image for bitmap format
     */
    private void uploadImage(Bitmap circleBmp) {

        if (circleBmp != null) {
            try {
                File imgDir = new File(MessageFactory.PROFILE_IMAGE_PATH);
                if (!imgDir.exists()) {
                    imgDir.mkdirs();
                }

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

                String serverFileName = SessionManager.getInstance(HomeScreen.this).getCurrentUserID().concat(".jpg");

                PictureMessage message = new PictureMessage(HomeScreen.this);
                JSONObject object = (JSONObject) message.createUserProfileImageObject(serverFileName, profileImgPath);
                FileUploadDownloadManager fileUploadDownloadMgnr = new FileUploadDownloadManager(HomeScreen.this);
                fileUploadDownloadMgnr.startFileUpload(EventBus.getDefault(), object);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }


    /**
     * Getting data one view to another view
     * @param event Based EventBus we getting the data's (updated the profile image, tab count, login key and login push notification key)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        hideProgressDialog();
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_IMAGE_UPLOAD)) {
            Object[] array = event.getObjectsArray();
            try {
                JSONObject objects = new JSONObject(array[0].toString());
                String err = objects.getString("err");
                String message = objects.getString("message");

                if (err.equalsIgnoreCase("0")) {
                    String from = objects.getString("from");
                    String type = objects.getString("type");

                    if (from.equalsIgnoreCase(SessionManager.getInstance(HomeScreen.this).getCurrentUserID())
                            && type.equalsIgnoreCase("single")) {
                        String path = objects.getString("file") + "?id=" + Calendar.getInstance().getTimeInMillis();

                        Picasso.with(HomeScreen.this).load(Constants.SOCKET_IP.concat(path))
                                .error(R.drawable.ic_profile_default).into(ivProfilePic);
                        /*Picasso.with(HomeScreen.this).load(path)
                                .error(R.drawable.ic_account_circle_white_64dp).fit().into(bgimage);*/
                        Toast.makeText(HomeScreen.this, message, Toast.LENGTH_SHORT).show();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MESSAGE)) {
            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                String docId = object.getString("doc_id");
                if (docId.contains(SessionManager.getInstance(HomeScreen.this).getCurrentUserID())) {
                    changeTabTextCount();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP)) {

            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                String groupAction = object.getString("groupType");

                if (groupAction.equalsIgnoreCase(SocketManager.ACTION_EVENT_GROUP_MESSAGE)) {
                    Object[] array = event.getObjectsArray();
                    JSONObject objects = new JSONObject(array[0].toString());
                    if (objects.has("payload")) {
                        changeTabTextCount();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_UPDATE_MOBILE_LOGIN_NOTIFICATION)) {
            loadDeviceLoginMessage(event.getObjectsArray());
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CHECK_MOBILE_LOGIN_KEY)) {
            loadCheckLoginKey(event.getObjectsArray());
        }
    }

    /**
     * Checking login ket
     * @param objectsArray getting response from backend
     */
    private void loadCheckLoginKey(Object[] objectsArray) {
        try {
            JSONObject object = new JSONObject(objectsArray[0].toString());

            String err = object.getString("err");
            if (err.equalsIgnoreCase("0")) {
                String msg = object.getString("msg");

                JSONObject apiObj = object.getJSONObject("apiMobileKeys");
//                String deviceId = apiObj.getString("DeviceId");
                String loginKey = apiObj.getString("login_key");
                String timeStamp = apiObj.getString("timestamp");

                String deviceLoginKey = SessionManager.getInstance(HomeScreen.this).getLoginKey();
                if (!loginKey.equalsIgnoreCase(deviceLoginKey)) {
//                    showDeviceChangedAlert();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * getting response for device login
     * @param objectsArray backend response object
     */
    private void loadDeviceLoginMessage(Object[] objectsArray) {
        try {
            JSONObject object = new JSONObject(objectsArray[0].toString());

            String err = object.getString("err");
            if (err.equalsIgnoreCase("0")) {
                String msg = object.getString("msg");

                JSONObject apiObj = object.getJSONObject("apiMobileKeys");
                String deviceId = apiObj.getString("DeviceId");
                String loginKey = apiObj.getString("login_key");
                String timeStamp = apiObj.getString("timestamp");

                String deviceLoginKey = sessionManager.getLoginKey();

                String settingsDeviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                if (!deviceId.equals(settingsDeviceId)) {
//                    showDeviceChangedAlert();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

//    @Override
//    public void onDrawerSlide(View drawerView, float slideOffset) {
//        overrideFonts(HomeScreen.this, navigationView);
//    }
//
//    @Override
//    public void onDrawerOpened(View drawerView) {
//        ivProfilePic = (ImageView) drawerView.findViewById(R.id.ivProfilePic);
//        tvName = (TextView) drawerView.findViewById(R.id.tvName);
//
//        bgimage = (ImageView) drawerView.findViewById(R.id.bgimage);
//        ivProfilePic.setOnClickListener(HomeScreen.this);
//        tvName.setText(SessionManager.getInstance(HomeScreen.this).getnameOfCurrentUser());
//
//        String path = Constants.SOCKET_IP + SessionManager.getInstance(HomeScreen.this).getUserProfilePic();
//
//        Picasso.with(HomeScreen.this).load(path).error(R.drawable.nav_menu_background).into(ivProfilePic);
//    }
//
//    @Override
//    public void onDrawerClosed(View drawerView) {
//
//    }
//
//    @Override
//    public void onDrawerStateChanged(int newState) {
//
//    }

    private void showDeviceChangedAlert() {
        Intent msgSvcIntent = new Intent(HomeScreen.this, MessageService.class);
        stopService(msgSvcIntent);

        CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setMessage("This " + getResources().getString(R.string.app_name) + " account used in another one mobile, Do you want to continue with this mobile?");
        dialog.setNegativeButtonText("No");
        dialog.setPositiveButtonText("Yes");
        dialog.setCancelable(false);
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                SessionManager.getInstance(HomeScreen.this).logoutUser(true);
                finish();
            }

            @Override
            public void onNegativeButtonClick() {
//                sessionManager.logoutUser();

                Intent mainIntent = new Intent(Intent.ACTION_MAIN);
                mainIntent.addCategory(Intent.CATEGORY_HOME);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mainIntent);
                finish();
            }
        });

        if (!isDeviceAlertShowing) {
            dialog.show(getSupportFragmentManager(), "Account validate");
            isDeviceAlertShowing = true;
        }
    }

    /**
     * Tab view change text count
     */
    public void changeTabTextCount() {
        int chatbadge = 0, groupbadge = 0;
        int callbadge = 0;
        ShortcutBadgeManager mgnr = new ShortcutBadgeManager(HomeScreen.this);
        MessageDbController db = CoreController.getDBInstance(HomeScreen.this);
        Session session = new Session(HomeScreen.this);

        ArrayList<MessageItemChat> databases = db.selectChatList(MessageFactory.CHAT_TYPE_SINGLE);

        for (MessageItemChat msgItem : databases) {
            String toUserId = msgItem.getReceiverID();
            String docID = uniqueCurrentID + "-" + toUserId;
            String convId = userInfoSession.getChatConvId(docID);

            if (convId != null && !convId.equals("") && !session.getarchive(docID)) {
                int count = mgnr.getSingleBadgeCount(convId);
                    /*if (!mark) {
                        count = count + 1;
                    }*/


                if (msgItem.getMessageType().equals(String.valueOf(MessageFactory.missed_call))) {

                    int call_count = mgnr.getSingleCallBadgeCount(convId);
                    if (call_count > 0) {

                        callbadge = callbadge + 1;
                    }
                } else {
                    if (count > 0) {

                        chatbadge = chatbadge + 1;
                    }
                }

            }
        }

/*        TextView tvSingleCount = (TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tvCount);
        if (chatbadge > 0) {
            tvSingleCount.setVisibility(View.VISIBLE);
            tvSingleCount.setText(String.valueOf(chatbadge));
        } else {
            tvSingleCount.setVisibility(View.GONE);
        }*/

        ArrayList<MessageItemChat> groupChats = db.selectChatList(MessageFactory.CHAT_TYPE_GROUP);
        groupbadge = 0;
        for (MessageItemChat msgItem : groupChats) {

            String groupId = msgItem.getReceiverID();
//            Boolean mark = session.getmark(groupId);
            int count = mgnr.getSingleBadgeCount(groupId);
            String docID = uniqueCurrentID + "-" + groupId + "-g";
            /*if (!mark) {
                count = count + 1;
            }*/
            if (count > 0 && !session.getarchive(docID)) {
                groupbadge = groupbadge + 1;
            }
        }

        TextView tvSingleCount = (TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tvCount);

        TextView tvCallCount = (TextView) tabLayout.getTabAt(1).getCustomView().findViewById(R.id.tvCount);

        if (chatbadge > 0) {
            chatbadge = chatbadge + groupbadge;
            tvSingleCount.setVisibility(View.VISIBLE);
            tvSingleCount.setText(String.valueOf(chatbadge));
        } else if (groupbadge > 0) {
            chatbadge = chatbadge + groupbadge;
            tvSingleCount.setVisibility(View.VISIBLE);
            tvSingleCount.setText(String.valueOf(chatbadge));
        } else {

            tvSingleCount.setVisibility(View.GONE);
        }

        if (callbadge > 0) {
            tvCallCount.setVisibility(View.VISIBLE);
//            tvCallCount.setVisibility(View.GONE);
            tvCallCount.setText(String.valueOf(callbadge));

        } else {
            tvCallCount.setVisibility(View.GONE);
        }

        /*String groupCount = "" + mgnr.getAllGroupMsgCount();
        TextView tvGroupCount = (TextView) tabLayout.getTabAt(0).getCustomView().findViewById(R.id.tvCount);
        if (tvGroupCount != null) {
            if (mgnr.getAllGroupMsgCount() > 0) {
                tvGroupCount.setVisibility(View.VISIBLE);
                tvGroupCount.setText(groupCount);
            } else {
                tvGroupCount.setVisibility(View.GONE);
            }
        }*/

        //String singleCount = "" + mgnr.getAllSingleMsgCount();


        /*if (tvSingleCount != null) {
            if (mgnr.getAllSingleMsgCount() > 0) {
                tvSingleCount.setVisibility(View.VISIBLE);
                tvSingleCount.setText(singleCount);
            } else {
                tvSingleCount.setVisibility(View.GONE);
            }
        }*/
    }

    /**
     * Click profile view
     * @param view View of Profile Activity
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ivProfilePic:
                Intent profileIntent = new Intent(HomeScreen.this, UserProfile.class);
                startActivity(profileIntent);
                //  mDrawerLayout.closeDrawer(GravityCompat.START);
                break;
        }

    }

    private String createCameraImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp + ".jpg";
    }

    private void newuserFromContact() {
        //mSocket.on("GetContacts", onNewMes);
    }

    private void Accknowlage(String number) {

        mSocket.connect();
        JSONObject obj = new JSONObject();
        try {

            obj.put("conType", "2");
            obj.put("from", SessionManager.getInstance(HomeScreen.this).getPhoneNumberOfCurrentUser());
            obj.put("memNum", number);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Log.d("Team TBT Request",contacts);

        mSocket.emit("giveToFav", obj, new Ack() {
            @Override
            public void call(Object... args) {
            }
        });

    }

    /**
     * Pass data for one activity to another using for bundle
     * @param intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            from = bundle.getString("fromNotify");
            myID = bundle.getString("ID");
            username = bundle.getString("uname");
            stat = bundle.getString("status");
            pwd = bundle.getString("pwd");
            messageItemChat = (MessageItemChat) bundle.getSerializable("MessageItem");


            if (from != null && from.equalsIgnoreCase("service")) {
                //Toast.makeText(this, "Sorry! This Chat is Already Locked..Unlock to proceed...",Toast.LENGTH_SHORT).show();
                performNavigationToChatView(myID, username, stat, pwd, messageItemChat);
            }
        }

    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow
            //Log.d("Team TBT search 1", query);
        }
    }

    /**
     * Menu view for home activity
     * @param menu inflate menu xml
     * @return value
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        if (viewPager.getCurrentItem() == 0) {
            menu.findItem(R.id.refresh).setVisible(false);

        } /*else if (viewPager.getCurrentItem() == 1) {
            menu.findItem(R.id.refresh).setVisible(false);

        }*/ else if (viewPager.getCurrentItem() == 2) {
            // configure
            menu.findItem(R.id.refresh).setVisible(false);

        }

        return true;
    }

    /**
     * Select menu option
     * @param item call refresh menu
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        // User pressed the search button

        //Log.d("Team TBT search", query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        // User changed the text
        //Log.d("Team TBT search", newText);
        return false;
    }
//
//    class getAllContactOfPhone extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            try {
//                ContentResolver cr = getContentResolver();
//                cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
//                String contacts = "";
//                if (cur.getCount() > 0) {
//
//                    while (cur.moveToNext()) {
//                        String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
//                        String name = cur.getString(cur.getColumnIndex(ContactsContract.CEVENT_CLEAR_CHATontacts.DISPLAY_NAME));
//                        if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//                            Cursor pCur = cr.query(
//                                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                                    null,
//                                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
//                                    new String[]{id}, null);
//                            while (pCur.moveToNext()) {
//
//
////                                int phoneType = pCur.getInt(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
////                          if(      phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE  )
////                          {
//
//                                String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//                                phoneNo = phoneNo.replaceAll("\\s+", "");
//                                phoneNo = phoneNo.replaceAll("[^\\d.]", "");
//                                if (!phoneNo.trim().equals("null")) {
//                                    contacts += phoneNo.trim() + ",";//}
//
//                                    mycontact.put(phoneNo.trim(), name);
//
//
//                                }
//                                //Log.d("single contact", phoneNo.trim());
//                            }
//
//                            pCur.close();
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (cur != null) {
//                    cur.close();
//                }
//            }
//
//            Log.d("Contacts", mycontact.toString());
//            return null;
//        }
//
//
//    }


    private void sendUserStatus() {
        try {
            JSONObject object = new JSONObject();
            object.put("from", SessionManager.getInstance(this).getCurrentUserID());
            object.put("msisdn", SessionManager.getInstance(this).getPhoneNumberOfCurrentUser());
            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_GET_USER_STATUS);
            event.setMessageObject(object);
            System.out.println("sendUserStatus" + "12112");
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("sendUserStatus" + "JSONException");
        }

   /* try {
        JSONObject object = new JSONObject();
        object.put("from", from);
        object.put("status", status);
        object.put("option", "");
        object.put("type", "");
        object.put("notify_status", "");
        object.put("convId", "");
        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_MUTE);
        event.setMessageObject(object);
    } catch (JSONException e) {
        e.printStackTrace();
    }*/
    }

    /**
     * User view stage to handle the data
     */
    @Override
    public void onResume() {
        super.onResume();
       /* if(SessionManager.getInstance(this).getCurrentUserID() != null && !SessionManager.getInstance(this).getCurrentUserID().equalsIgnoreCase("")){
            sendUserStatus();
        }*/
        /*Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            from = bundle.getString("fromNotify");
            myID = bundle.getString("ID");
            username = bundle.getString("uname");
            stat = bundle.getString("status");
            pwd = bundle.getString("pwd");
            messageItemChat =  (MessageItemChat) bundle.getSerializable("MessageItem");

            if(from.equalsIgnoreCase("service")){
                performNavigationToChatView(myID, username,stat,pwd,messageItemChat);
            }
        }*/

        MessageService.clearNotificationData();
//        new ChangeSetController(HomeScreen.this).setChangeStatus("1");
//        ChangeSetController.setChangeStatus("1");
        //if (sessionManager.getIsloggeggInfirsttime().equals("true")) {
        //}

        //;FirebaseMessaging.getInstance().subscribeToTopic("news");
        checkAndRequestPermissions();
        changeTabTextCount();

       /* if (SessionManager.getInstance(HomeScreen.this).isLoginKeySent() && !SessionManager.getInstance(HomeScreen.this).isValidDevice()) {
            showDeviceChangedAlert();
        }*/


    }

    /**
     * Handle the clearnotification data
     */
    @Override
    protected void onPostResume() {
        super.onPostResume();

        MessageService.clearNotificationData();
    }

    @Override
    protected void onPause() {
        super.onPause();
       /* if (sessionManager.getIsloggeggInfirsttime().equals("true")) {

            //LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        }*/


    }

    /**
     * click back press it will moving to chat tab
     */
    @Override
    public void onBackPressed() {

        if (viewPager.getCurrentItem() == 1 || viewPager.getCurrentItem() == 2) {

            viewPager.setCurrentItem(0);
        } else {
            super.onBackPressed();
        }


        /*if (sessionManager.getIsloggeggInfirsttime().equals("true")) {

            Toast.makeText(HomeScreen.this, "Please Wait Contact is uploading", Toast.LENGTH_SHORT).show();

        } else {

        }*/

        Log.d("log11", "1");

        //AppController.getInstance().emitHeartbeat("0");
        //AppController.getInstance().informContactsOfChangingStatus("0");


    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        Log.d("log11", "2");

        //AppController.getInstance().emitHeartbeat("0");
        //AppController.getInstance().informContactsOfChangingStatus("0");
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Start EventBus :  Ready to pass the data
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(HomeScreen.this);
    }


//    private String getallLocalname(String ContactNumber, String countrycode) {
//
//        String LocalNAme = "";
//
//
//        int LenghtOfCountryCode = countrycode.length();
//        String phonenumbertoCompare = ContactNumber.substring(LenghtOfCountryCode);
//
//
//        Iterator myVeryOwnIterator = mycontact.keySet().iterator();
//        while (myVeryOwnIterator.hasNext()) {
//
//            String key = (String) myVeryOwnIterator.next();
//            String value = mycontact.get(key);
//            if (key.contains(phonenumbertoCompare)) {
//                LocalNAme = value;
//
//            }
//        }
//
//        return LocalNAme;
//
//    }

    /**
     * Stop EventBus : stop to pass the data
     */
    @Override
    public void onStop() {
        super.onStop();
//        new ChangeSetController(HomeScreen.this).setChangeStatus("0");
        EventBus.getDefault().unregister(HomeScreen.this);
    }

    /**
     *  activity close stage
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        sessionManager = null;

        //mRegistrationBroadcastReceiver=null;
        //mSocket=null;
        mUserId = null;


    }

    /**
     * Getting image uri
     * @param inContext current class
     * @param inImage image data
     * @return value
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        try {
            bytes.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void launchMarket() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
        try {
            startActivity(myAppLinkToMarket);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, " unable to find market app", Toast.LENGTH_LONG).show();
        }
    }

    private void sendRatingToServer(String status) {
        try {
            JSONObject object = new JSONObject();
            object.put("rating", status);
            object.put("from", sessionManager.getCurrentUserID());
            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_RATING);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Navigation on Chat view for missed call notification
     * @param receiverDocumentID getting id
     * @param username getting username
     * @param stat getting status
     * @param pwd getting password
     * @param messageItemChat get model class data
     */
    public void performNavigationToChatView(String receiverDocumentID, String username, String stat, String pwd, MessageItemChat messageItemChat) {

        openUnlockChatDialog(receiverDocumentID, stat, pwd, messageItemChat);

    }

    /**
     *  Show unlock chat dialog
     * @param receiverDocumentID getting id
     * @param stat getting status
     * @param pwd getting password
     * @param messageItemChat get model class data
     */
    private void openUnlockChatDialog(String receiverDocumentID, String stat, String pwd, MessageItemChat messageItemChat) {
        String convId = userInfoSession.getChatConvId(receiverDocumentID);
        ChatLockPwdDialog dialog = new ChatLockPwdDialog();
        dialog.setTextLabel1("Enter your Password");
        dialog.setEditTextdata("Enter new Password");
        dialog.setforgotpwdlabel("Forgot Password");
        dialog.setHeader("Unlock Chat");
        dialog.setButtonText("Unlock");
        Bundle bundle = new Bundle();
        bundle.putSerializable("MessageItem", messageItemChat);
        bundle.putString("convID", convId);
        bundle.putString("status", "1");
        bundle.putString("pwd", pwd);
        bundle.putString("page", "chatlist");
        bundle.putString("type", "single");
        bundle.putString("from", uniqueCurrentID);
        dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "chatunLock");
    }

    private void enableGpsService() {

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(30 * 1000);
        mLocationRequest.setFastestInterval(5 * 1000);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);

        result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                //final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(HomeScreen.this, REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    /*
     */
/** A method to download json data from url *//*

    @SuppressLint("LongLogTag")
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);


            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }

        return data;
    }

    private void locationCall(){

        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("location="+"11.0176"+","+"76.9674");
        sb.append("&radius=5000");
//        sb.append("&types="+type);
        sb.append("&sensor=true");
        sb.append("&key=AIzaSyCxCprNGQpecOfQN0pCu2nxzUmBoMSA8no");


        // Creating a new non-ui thread task to download Google place json data
        PlacesTask placesTask = new PlacesTask();

        // Invokes the "doInBackground()" method of the class PlaceTask
        placesTask.execute(sb.toString());
    }

    */
/** A class, to download Google Places *//*

    private class PlacesTask extends AsyncTask<String, Integer, String> {

        String data = null;

        // Invoked by execute() method of this object
        @Override
        protected String doInBackground(String... url) {
            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(String result){
            ParserTask parserTask = new ParserTask();

            // Start parsing the Google places in JSON format
            // Invokes the "doInBackground()" method of the class ParseTask
            parserTask.execute(result);
        }

    }

    */
/** A class to parse the Google Places in JSON format *//*

    private class ParserTask extends AsyncTask<String, Integer, List<HashMap<String,String>>>{

        JSONObject jObject;

        // Invoked by execute() method of this object
        @Override
        protected List<HashMap<String,String>> doInBackground(String... jsonData) {

            List<HashMap<String, String>> places = null;
            PlaceJSONParser placeJsonParser = new PlaceJSONParser();

            try{
                jObject = new JSONObject(jsonData[0]);

                */
/** Getting the parsed data as a List construct *//*

                places = placeJsonParser.parse(jObject);

            }catch(Exception e){
                Log.d("Exception",e.toString());
            }
            return places;
        }

        // Executed after the complete execution of doInBackground() method
        @Override
        protected void onPostExecute(List<HashMap<String,String>> list){


            for(int i=0;i<list.size();i++){


                HashMap<String, String> hmPlace = list.get(i);

                // Getting latitude of the place
                double lat = Double.parseDouble(hmPlace.get("lat"));

                // Getting longitude of the place
                double lng = Double.parseDouble(hmPlace.get("lng"));

                // Getting name
                String name = hmPlace.get("place_name");

                // Getting vicinity
                String vicinity = hmPlace.get("vicinity");

                LatLng latLng = new LatLng(lat, lng);

                System.out.println("testing location-->"+lat+"-"+lng+"---"+name+"---"+vicinity);

            }

        }

    }
*/


}
