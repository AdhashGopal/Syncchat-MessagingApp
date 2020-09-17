package com.chatapp.synchat.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.agrawalsuneet.dotsloader.loaders.TashieLoader;
import com.andexert.library.RippleView;
import com.android.volley.Request;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.ShortcutBadgeManager;
import com.chatapp.synchat.core.chatapphelperclass.ChatappPermissionValidator;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.model.SCLoginModel;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.service.ServiceRequest;
import com.chatapp.synchat.core.socket.MessageService;
import com.chatapp.synchat.core.socket.SocketManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.scottyab.rootbeer.RootBeer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import io.socket.client.Socket;

import static android.Manifest.permission.READ_PHONE_STATE;


public class ReLoadingActivityNew extends Activity {

    private static final boolean SKIP_OTP_VERIFICATION = false;
    private static final String TAG = ReLoadingActivityNew.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 200;
    private final int AUDIO_RECORD_PERMISSION_REQUEST_CODE = 14;
    private final int REQUEST_CODE_PERMISSION_MULTIPLE = 123;
    private final int REQUEST_CODE_PERMISSION_MULTIPLE1 = 1234;
    Handler mHandler = new Handler();
    AlertDialog.Builder builder = null;
    ReLoadingActivityNew context;
    String code = "", otp = "";
    String android_id;
    RootBeer rootBeer;
    private TextView organisationDesc;
    private ProgressDialog progressDialog;
    private SessionManager sessionManager;
    private ArrayList<ChatappPermissionValidator.Constants> myPermissionConstantsArrayList;
    private SocketManager mSocketManager;
    private boolean isDeninedRTPs = false;
    private boolean showRationaleRTPs = false;
    private SCLoginModel SCLoginModel;
    private int aChatPayloadUpdate = 0;
    private String aChatPayloadUpdateUrl = "";


    /**
     * Verification code response. if code is 0 means moving to pin entry screen. orelse shown as error message
     */
    ServiceRequest.ServiceListener verifyCodeListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            Log.d("Loginrequest", response);

//            hideProgressDialog();
//            progressDialog.dismiss();
            if (SCLoginModel == null)
                SCLoginModel = new SCLoginModel();
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            SCLoginModel = gson.fromJson(response, SCLoginModel.class);
            Log.d(TAG, "onCompleteListener: loginCount: " + SCLoginModel.getLoginCount());
            // success
            if (SCLoginModel.getErrNum().equals("0")) {
                /* Move to the profile info screen */
//                Toast.makeText(VerifyPhoneScreen.this, "Mobile number verified successfully", Toast.LENGTH_SHORT).show();
                SessionManager.getInstance(ReLoadingActivityNew.this).IsnumberVerified(true);
                SessionManager.getInstance(ReLoadingActivityNew.this).setLoginCount(SCLoginModel.getLoginCount());
                //Log.d("CODE", loginModel.getCode());
                SharedPreferences shPref = getSharedPreferences("global_settings",
                        MODE_PRIVATE);
                SharedPreferences.Editor et = shPref.edit();
                et.putString("userId", SessionManager.getInstance(ReLoadingActivityNew.this).getPhoneNumberOfCurrentUser());
                et.apply();
                Session session = new Session(ReLoadingActivityNew.this);
                session.putgalleryPrefs("def");
                ActivityLauncher.launchPinscreen(ReLoadingActivityNew.this, null);

            } else {
                Toast.makeText(ReLoadingActivityNew.this, "Code mismatch. Please re-enter the code", Toast.LENGTH_LONG).show();
                SharedPreferences shPref = getSharedPreferences("global_settings",
                        MODE_PRIVATE);
                SharedPreferences.Editor et = shPref.edit();
                et.putString("userId", SessionManager.getInstance(ReLoadingActivityNew.this).getPhoneNumberOfCurrentUser());
                et.apply();
            }
        }

        @Override
        public void onErrorListener(int state) {
//            hideProgressDialog();
//            progressDialog.dismiss();
        }
    };
    private String GCM_Id = "";
    private RippleView orgRippleView;
    private RippleView nonOrgRippleView;
    private ImageView imgLogoOne;
    private ImageView imgLogoTwo;
    private TashieLoader dottedLoader;
    private int nonOrganisationVisible = 0;
    /**
     * Verification socket response. getting user value to store session.
     */
    private ServiceRequest.ServiceListener verifcationListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {

            if (SCLoginModel == null)
                SCLoginModel = new SCLoginModel();
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            SCLoginModel = gson.fromJson(response, SCLoginModel.class);
            otp = SCLoginModel.getCode();
            if (SCLoginModel.getProfilePic() != null) {
                String imgPath = SCLoginModel.getProfilePic() + "?id=" + Calendar.getInstance().getTimeInMillis();
                SessionManager.getInstance(ReLoadingActivityNew.this).setUserProfilePic(imgPath);
            }

            if (SCLoginModel.getStatus() != null && !SCLoginModel.getStatus().equalsIgnoreCase("")) {
                SessionManager.getInstance(ReLoadingActivityNew.this).setcurrentUserstatus(new String(Base64.decode(SCLoginModel.getStatus(), Base64.DEFAULT)));
            } else {
                SessionManager.getInstance(ReLoadingActivityNew.this).setcurrentUserstatus("Hey there! I am using " + getResources().getString(R.string.app_name));
            }

            if (SCLoginModel.get_id() != null) {
                Log.d("UserId-->", SCLoginModel.get_id());
            }

            // success
            SessionManager.getInstance(ReLoadingActivityNew.this).setLoginSuccessStatus(SCLoginModel.getErrNum());
            if (SCLoginModel.getErrNum().equals("0")) {
                if (SCLoginModel.get_id() != null) {
                    SessionManager.getInstance(ReLoadingActivityNew.this).setLoginType(SCLoginModel.getLoginType());

                    SessionManager.getInstance(ReLoadingActivityNew.this).setCurrentUserID(SCLoginModel.get_id());
                    SessionManager.getInstance(ReLoadingActivityNew.this).setCurrentUserEmailID(SCLoginModel.getUserEmail());
                    System.out.println("TestUserID--> ReLoadingActivityNew 0--" + SCLoginModel.get_id());
//                clearDataIfUserChanged(SCLoginModel.get_id());
                }
                SessionManager.getInstance(ReLoadingActivityNew.this).Islogedin(true);
                String message = android_id;
                SessionManager.getInstance(ReLoadingActivityNew.this).setPhoneNumberOfCurrentUser(message);
                if (SCLoginModel.getName() != null && !SCLoginModel.getName().isEmpty()) {
                    Log.d("getName--> IF", SCLoginModel.getName());
                    SessionManager.getInstance(ReLoadingActivityNew.this).setnameOfCurrentUser(new String(Base64.decode(SCLoginModel.getName(), Base64.DEFAULT)));
                } else {
                    Log.d("getName--> ELSE", SCLoginModel.getName());
                }


                SessionManager.getInstance(ReLoadingActivityNew.this).setUserCountryCode("+" + code);
                SessionManager.getInstance(ReLoadingActivityNew.this).setUserMobileNoWithoutCountryCode(android_id);

                try {
                    JSONObject object = new JSONObject(response);
                    if (SessionManager.getInstance(ReLoadingActivityNew.this).getTwilioMode().equalsIgnoreCase(SessionManager.TWILIO_DEV_MODE)) {
                        SessionManager.getInstance(ReLoadingActivityNew.this).setLoginOTP(object.getString("code"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (SKIP_OTP_VERIFICATION) {
                  /*  new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            skipOTP(otp);
                        }
                    }, 500);*/
                } else {
//                    hideProgressDialog();
//                    progressDialog.dismiss();
                    SharedPreferences shPref = getSharedPreferences("global_settings", MODE_PRIVATE);
                    SharedPreferences.Editor et = shPref.edit();
                    et.putString("userId", SessionManager.getInstance(ReLoadingActivityNew.this).getPhoneNumberOfCurrentUser());
                    et.apply();
                    SessionManager.getInstance(ReLoadingActivityNew.this).IsnumberVerified(true);
                    SessionManager.getInstance(ReLoadingActivityNew.this).setLoginCount(SCLoginModel.getLoginCount());


                    dottedLoader.setVisibility(View.GONE);
                    translate(imgLogoOne, imgLogoTwo);
                  /*  if(sessionManager.getChatUpdaterDownload().equalsIgnoreCase("")||aChatPayloadUpdate==1){

                        ActivityLauncher.launchDownloadManagerScreen(ReLoadingActivityNew.this, aChatPayloadUpdateUrl, "");
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                    else {*/

                    // old login - 23-7-2020
                    /*if (SCLoginModel.getResetPin() == 1) {
                        try {
                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(1);
                            SessionManager.getInstance(ReLoadingActivityNew.this).setUsermpin("");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!SessionManager.getInstance(ReLoadingActivityNew.this).getUserMpin().equals("")) {
                                        ActivityLauncher.launchPinscreen(context, SessionManager.getInstance(context).getPhoneNumberOfCurrentUser(), SCLoginModel.getResetPin());
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                        finish();
                                    } else {
                                        ActivityLauncher.launchPinscreen(ReLoadingActivityNew.this, null, SCLoginModel.getResetPin());
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                        finish();
                                    }
                                }
                            }, 500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else {
                        if (!SessionManager.getInstance(ReLoadingActivityNew.this).getUserMpin().equals("")) {
                            ActivityLauncher.launchPinscreen(context, SessionManager.getInstance(context).getPhoneNumberOfCurrentUser(), SCLoginModel.getResetPin());
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();
                        } else {
                            ActivityLauncher.launchPinscreen(ReLoadingActivityNew.this, null, SCLoginModel.getResetPin());
                            overridePendingTransition(R.anim.enter, R.anim.exit);
                            finish();
                        }
                    }*/

                    //new login - 23-7-2020
                    try {
                        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(1);
                        SessionManager.getInstance(ReLoadingActivityNew.this).setUsermpin("");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!SessionManager.getInstance(ReLoadingActivityNew.this).getUserMpin().equals("")) {
                                    ActivityLauncher.launchPinscreen(context, SessionManager.getInstance(context).getPhoneNumberOfCurrentUser(), SCLoginModel.getResetPin());
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    finish();
                                } else {
                                    ActivityLauncher.launchPinscreen(ReLoadingActivityNew.this, null, SCLoginModel.getResetPin());
                                    overridePendingTransition(R.anim.enter, R.anim.exit);
                                    finish();
                                }
                            }
                        }, 500);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    //}

//                    ActivityLauncher.launchRegisterScreen(ReLoadingActivityNew.this);
                    /* orgRippleView.setVisibility(View.VISIBLE);
                    nonOrgRippleView.setVisibility(View.VISIBLE);
                    Animation animSlideleft = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.left_to_right);
                    orgRippleView.startAnimation(animSlideleft);
                    Animation animSlideRight = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.right_to_left);
                    nonOrgRippleView.startAnimation(animSlideRight);*/

                    //  ActivityLauncher.launchSMSVerificationScreen(VerifyPhoneScreen.this, message, "" + code, phoneNumber.getText().toString(), otp,GCM_Id);
                }
                SessionManager.getInstance(ReLoadingActivityNew.this).setAdminPendingStatus("");
            } else if (SCLoginModel.getErrNum().equals("1")) {
                /*if(sessionManager.getChatUpdaterDownload().equalsIgnoreCase("")||aChatPayloadUpdate==1){


                    ActivityLauncher.launchDownloadManagerScreen(ReLoadingActivityNew.this, aChatPayloadUpdateUrl, "");
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }*/
                String error = SCLoginModel.getMessage();
                Toast.makeText(ReLoadingActivityNew.this, error, Toast.LENGTH_SHORT).show();
                SessionManager.getInstance(ReLoadingActivityNew.this).setAdminPendingStatus("");
            } else if (SCLoginModel.getErrNum().equals("2")) {
                if (SCLoginModel.get_id() != null) {
                    SessionManager.getInstance(ReLoadingActivityNew.this).setCurrentUserID(SCLoginModel.get_id());
                    System.out.println("TestUserID--> ReLoadingActivityNew 2--" + SCLoginModel.get_id());
//                clearDataIfUserChanged(SCLoginModel.get_id());
                }

                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(1);

                /*if(sessionManager.getChatUpdaterDownload().equalsIgnoreCase("")||aChatPayloadUpdate==1){

                    ActivityLauncher.launchDownloadManagerScreen(ReLoadingActivityNew.this, aChatPayloadUpdateUrl, "");
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }*/

                String error = SCLoginModel.getMessage();

//                if (error.equalsIgnoreCase("DeviceID not Registered")) {

                dottedLoader.setVisibility(View.GONE);
                translate(imgLogoOne, imgLogoTwo);
//                ActivityLauncher.launchRegisterScreen(ReLoadingActivityNew.this);

                if (nonOrganisationVisible == 2) {
                    organisationDesc.setVisibility(View.GONE);
                    Animation animSlideRightLeft = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.slide_left_in);
                    nonOrgRippleView.startAnimation(animSlideRightLeft);
                    nonOrgRippleView.setVisibility(View.VISIBLE);

                } else {
                    /*Animation animSlideRightLeft = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.slide_left_in);
                    nonOrgRippleView.startAnimation(animSlideRightLeft);
                    nonOrgRippleView.setVisibility(View.VISIBLE);*/


                    Animation animSlideRight = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.bottom_up);
                    orgRippleView.startAnimation(animSlideRight);
                    orgRippleView.setVisibility(View.VISIBLE);
                    animSlideRight.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            if (nonOrganisationVisible == 0) {
                                Animation animSlideLeftIn = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.left_in);
                                organisationDesc.startAnimation(animSlideLeftIn);
                                organisationDesc.setVisibility(View.VISIBLE);
                            } else {
                                Animation animSlideRightLeft = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.right_to_left);
                                nonOrgRippleView.startAnimation(animSlideRightLeft);
                                nonOrgRippleView.setVisibility(View.VISIBLE);

                                Animation animSlideLeftIn = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.left_in);
                                organisationDesc.startAnimation(animSlideLeftIn);
                                organisationDesc.setVisibility(View.VISIBLE);
                            }


                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
                SessionManager.getInstance(ReLoadingActivityNew.this).setAdminPendingStatus("1");
            } else if (SCLoginModel.getErrNum().equals("3")) {
                String error = SCLoginModel.getMessage();
                SessionManager.getInstance(ReLoadingActivityNew.this).setAdminPendingStatus("1");
                if (SCLoginModel.get_id() != null) {
                    SessionManager.getInstance(ReLoadingActivityNew.this).setLoginType(SCLoginModel.getLoginType());
                    SessionManager.getInstance(ReLoadingActivityNew.this).setCurrentUserID(SCLoginModel.get_id());
                    SessionManager.getInstance(ReLoadingActivityNew.this).setCurrentUserEmailID(SCLoginModel.getUserEmail());
                    System.out.println("TestUserID--> ReLoadingActivityNew 3--" + SCLoginModel.get_id());
                }
                if (!MessageService.isStarted()) {
                    startService(new Intent(ReLoadingActivityNew.this, MessageService.class));
                }
                showAlert(error);
            }
        }

        @Override
        public void onErrorListener(int state) {

            if (SessionManager.getInstance(ReLoadingActivityNew.this).getLoginFailedStatus().equalsIgnoreCase("")) {
                makeVerificationRequest();
                SessionManager.getInstance(ReLoadingActivityNew.this).setLoginFailedStatus("1");
            } else {
                showCheckInternetDialog(ReLoadingActivityNew.this);
            }
        }
    };
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            loadActivity(sessionManager);
        }
    };

    /**
     * Check the internet connection
     *
     * @param mContext current Activity
     */
    public void showCheckInternetDialog(final Activity mContext) {
        if (!mContext.isFinishing()) {
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mContext);
            alertDialog.setTitle(R.string.login_failed);
            alertDialog.setMessage(R.string.check_internet_connection);

            alertDialog.setNegativeButton(R.string.retry,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            makeVerificationRequest();

                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }
    /*public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));

                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }*/

    /**
     * Admin approval Alert
     *
     * @param message backend content
     */
    private void showAlert(String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReLoadingActivityNew.this, R.style.MyDialogTheme);
        alertDialog.setCancelable(false);
        // Setting Dialog Title
        alertDialog.setTitle("SynChat");
        // Setting Dialog Message
        alertDialog.setMessage(message);
        // Setting Icon to Dialog
        //  alertDialog.setIcon(R.drawable.tick);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
//                System.exit(0);
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    /**
     * Shown statusbar
     *
     * @param reLoadingActivityNew Current Activity
     */
    private void statusBar(ReLoadingActivityNew reLoadingActivityNew) {
        Window window = reLoadingActivityNew.getWindow();

// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(reLoadingActivityNew, R.color.transparent));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        statusBar(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reloading_new);
        imgLogoOne = findViewById(R.id.img_logo_one);
        imgLogoTwo = findViewById(R.id.img_logo_two);
        orgRippleView = findViewById(R.id.org_ripple_view);
        nonOrgRippleView = findViewById(R.id.nonorg_ripple_view);
        dottedLoader = findViewById(R.id.dot_loader);
        dottedLoader.setVisibility(View.VISIBLE);
        organisationDesc = findViewById(R.id.txt_organisation_desc);
        orgRippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchADFSScreen(ReLoadingActivityNew.this);
            }
        });
        nonOrgRippleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchRegisterScreen(ReLoadingActivityNew.this);
            }
        });

        builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
        onNewIntent(getIntent());
        context = this;
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        sessionManager = SessionManager.getInstance(ReLoadingActivityNew.this);
        sessionManager.setDeviceId(android_id);
        rootBeer = new RootBeer(context);

        initData();
        if (!AppUtils.isNetworkAvailable(ReLoadingActivityNew.this)) {
            makeVerificationRequest();
        }

    }

    /**
     * initialized of getting mobile IMEI method, Battery Optimisation & socket initialized
     */
    private void initData() {

        try {
            if (SessionManager.getInstance(ReLoadingActivityNew.this).getClearLocalDataStatus1().equalsIgnoreCase("")) {
                clearCache();
                clearSharedPreferences(ReLoadingActivityNew.this);
            }

          /*  Intent launchIntent =getPackageManager().getLaunchIntentForPackage("com.whatsapp.mobile");
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            }*/


        } catch (Exception e) {
            e.printStackTrace();
        }

        ShortcutBadgeManager manager = new ShortcutBadgeManager(this);
        manager.setContactLastRefreshTime(0L);
//        manager.clearBadgeCount();

        //  SessionManager.getInstance(ReLoadingActivityNew.this).setnameOfCurrentUser("");
//        checkAndRequestPermissions();
//        checkAndRequestPermissions1();

        if (checkPhoneStatePermission()) {
            getImeiNumber();
            BattertyOptimisation();

            initSocketCallback();
            mSocketManager.connect();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    requestPhoneStatePermission();
                }
            }, 1000);

        }

    }

    /**
     * get project version code
     *
     * @return retun number.
     */
    private int getVersionCode() {
        int verCode = 0;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            verCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }

        return verCode;
    }


    /**
     * get project version Name
     *
     * @return this is also number
     */
    private String getVersionName() {
        String verName = "";
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(getPackageName(), 0);
            verName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }

        return verName;
    }

    @Override
    protected void onResume() {
        super.onResume();

//        if(checkPhoneStatePermission()){
//            initSocketCallback();
//            if (!mSocketManager.isConnected())
//                mSocketManager.connect();
//        }else {
//            requestPhoneStatePermission();
//        }
    }

    /**
     * socket callback method for user details and based on the response moving the screen
     */
    private void initSocketCallback() {

        mSocketManager = new SocketManager(ReLoadingActivityNew.this, new SocketManager.SocketCallBack() {
            @Override
            public void onSuccessListener(String eventName, Object... response) {
                try {
                    if (eventName.equalsIgnoreCase(SocketManager.EVENT_GET_SETTINGS)) {

                        System.out.println("==" + eventName.equalsIgnoreCase(SocketManager.EVENT_GET_SETTINGS));

                        String data = response[0].toString();
                        System.out.println("===data" + data);

                        try {
                            JSONObject object = new JSONObject(data);
                            String twilioMode = object.getString("twilio");

                            SessionManager sessionManager = SessionManager.getInstance(ReLoadingActivityNew.this);
                            sessionManager.setTwilioMode(twilioMode);
                            mSocketManager.disconnect();
                            String adfsReturnUrl = object.getString("adfs_return_url");
                            SessionManager.getInstance(ReLoadingActivityNew.this).setAdfsReturnUrl(adfsReturnUrl);
                            String contactUsEmailId = object.getString("contactus_email_address");
                            System.out.println("===contactUsEmailId" + contactUsEmailId);
                            String chatLock = object.getString("chat_lock");
                            String secretChat = object.getString("secret_chat");
                            final String androidUpdateTitle = object.getString("android_update_title");
                            final String androidUpdateMsg = object.getString("android_update_msg");
                            final String rootedAlertTitle = object.getString("root_title");
                            final String rootedAlertMsg = object.getString("root_msg");
                            final String mapStaticImage = object.getString("google_map_icon");
                            int rootAllowed = object.getInt("root_allowed");// 0 - not allowed, 1 - allowed
                            int androidVersion = object.getInt("android_version");
                            int autoUpdate = object.getInt("autoupdate");// in app installation,0- browser download, 1- in app download
                            final String autoDeleteTimeInSeconds = object.getString("auto_delete_sec"); // auto delete send or receive message after read
                            Log.d("autoDeleteTimeInSeconds", "autoDeleteTimeInSeconds --> " + autoDeleteTimeInSeconds);
                            sessionManager.setAutoDeleteTime(autoDeleteTimeInSeconds);

                            final String updateUrl = object.getString("update_url");
                            if (object.has("achat_payload_url")) {
                                aChatPayloadUpdateUrl = object.getString("achat_payload_url");
                            }


                            if (object.has("achat_payload")) {
                                aChatPayloadUpdate = object.getInt("achat_payload");
                            }

                            final String orgDesc = object.getString("org_desc");

                            organisationDesc.setText(orgDesc);

                            final String adfsUrl = object.getString("adfs_url");
                            sessionManager.setADFSUrl(adfsUrl);
                            nonOrganisationVisible = object.getInt("non_organisation");

                            final String termsUrl = object.getString("terms_privacy_url");
                            final String copyRights = object.getString("copyright");
                            //test

                            sessionManager.setCopyRights(copyRights);
                            sessionManager.setTermsUrl(termsUrl);

                            sessionManager.setContactUsEMailId(contactUsEmailId);
                            sessionManager.setLockChatEnabled(chatLock);
                            sessionManager.setSecretChatEnabled(secretChat);
                            sessionManager.setStaticMapUrl(mapStaticImage);

                            if (rootAllowed == 0) {
                                if (!sessionManager.getRootStatus().equals("1")) {
                                    if (rootBeer.isRootedWithoutBusyBoxCheck()) {
                                        //we found indication of root
                                        ReLoadingActivityNew.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                builder.setTitle(rootedAlertTitle);
                                                builder.setMessage(rootedAlertMsg);
                                                builder.setCancelable(false);
                                                String positiveText = "CLOSE";
                                                builder.setPositiveButton(positiveText,
                                                        new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                // dismiss alert dialog, update preferences with game score and restart play fragment
                                                                finish();
                                                                dialog.dismiss();
                                                            }
                                                        });

                                                AlertDialog dialog = builder.create();
                                                if (dialog.isShowing()) {
                                                    dialog.dismiss();
                                                    dialog.show();
                                                } else {
                                                    dialog.show();
                                                }
                                            }
                                        });

                                        sessionManager.setRootStatus("0");
                                    } else {
                                        sessionManager.setRootStatus("1");
                                        moveScreen(autoUpdate, androidVersion, updateUrl, androidUpdateTitle, androidUpdateMsg);
                                    }
                                } else {
                                    sessionManager.setRootStatus("1");
                                    moveScreen(autoUpdate, androidVersion, updateUrl, androidUpdateTitle, androidUpdateMsg);
                                }

                            } else {
                                moveScreen(autoUpdate, androidVersion, updateUrl, androidUpdateTitle, androidUpdateMsg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (eventName.equalsIgnoreCase(Socket.EVENT_CONNECT)) {
                        JSONObject object = new JSONObject();
                        mSocketManager.send(object, SocketManager.EVENT_GET_SETTINGS);
                        System.out.println("==" + "here");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Screen navigation for updated apk screen based on socket response
     *
     * @param autoUpdate         based on the response to check apk updated or not (in app installation,0- browser download, 1- in app download)
     * @param androidVersion     android version based on response (compare the current version and response version. if current version above the response version means move to download the updated apk)
     * @param updateUrl          getting url from response
     * @param androidUpdateTitle getting Title from response
     * @param androidUpdateMsg   getting UpdateMsg from response
     */
    public void moveScreen(final int autoUpdate, int androidVersion, final String updateUrl, final String androidUpdateTitle, final String androidUpdateMsg) {
        if (androidVersion > getVersionCode()) {

            ReLoadingActivityNew.this.runOnUiThread(new Runnable() {
                public void run() {
                    if (autoUpdate == 0) {
                        builder.setTitle(androidUpdateTitle);
                        builder.setMessage(androidUpdateMsg);
                        builder.setCancelable(false);
                        String positiveText = getString(android.R.string.ok);
                        builder.setPositiveButton(positiveText,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // dismiss alert dialog, update preferences with game score and restart play fragment

                                        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(updateUrl));
                                        startActivity(viewIntent);
                                        dialog.dismiss();
                                    }
                                });

                        AlertDialog dialog = builder.create();
                        if (dialog != null) {
                            if (dialog.isShowing()) {
                                dialog.dismiss();
                                dialog.show();
                            } else {
                                if (!dialog.isShowing())
                                    dialog.show();
                            }
                        }

                    } else {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                ActivityLauncher.launchDownloadManagerScreen(ReLoadingActivityNew.this, updateUrl, androidUpdateMsg);
                                overridePendingTransition(R.anim.enter, R.anim.exit);
                            }
                        }, 500);
                    }


                }
            });

        } else {
            try {
               /* File file = new File(Environment.getExternalStorageDirectory() + "/aChat");
                if (file.exists()) {
                    file.delete();
                }*/
//                deleteDir(new File(Environment.getExternalStorageDirectory() + "/aChat"));
                //                loadContacts();


                makeVerificationRequest();


            } catch (Exception e) {
                e.printStackTrace();
            }


        }


    }

    /**
     * Socket disconnect on back press
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSocketManager.disconnect();
    }

    /**
     * Pin entry view request data
     */
    private void normalFlow() {
        if (SessionManager.getInstance(this).getCurrentUserID() != null && !SessionManager.getInstance(this).getCurrentUserID().equalsIgnoreCase("") && SessionManager.getInstance(this).getAdminPendingStatus().equalsIgnoreCase("1")) {


            HashMap<String, String> params = new HashMap<String, String>();
            String cCode = code;
//        String uPhone = android_id;
            System.out.println("Country_code" + "" + cCode);
            params.put("msisdn", android_id);
            params.put("DeviceId", android_id);
            params.put("pushToken", GCM_Id);
            params.put("manufacturer", Build.MANUFACTURER);
            params.put("Version", Build.VERSION.RELEASE);
            params.put("app_version", String.valueOf(getVersionCode()));
            params.put("app_version_name", getVersionName());
            params.put("imei_1", sessionManager.getImeiOne());
            params.put("imei_2", sessionManager.getImeiTwo());
            params.put("OS", "android");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String currentDateandTime = sdf.format(new Date());
            params.put("DateTime", currentDateandTime);
            params.put("callToken", "Android");

            System.out.println("====verify" + params);
            ServiceRequest request = new ServiceRequest(this);
            request.makeServiceRequest(Constants.VERIFY_NUMBER_REQUEST, Request.Method.POST, params, verifcationListener);

        } else if (SessionManager.getInstance(this).getCurrentUserID() != null && !SessionManager.getInstance(this).getCurrentUserID().equalsIgnoreCase("") && SessionManager.getInstance(ReLoadingActivityNew.this).getResetPinStatus() == 0) {

            ActivityLauncher.launchPinscreen(context, null, 1); //SCLoginModel.getResetPin()

            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();

           /* if(SessionManager.getInstance(ReLoadingActivityNew.this).getResetPinStatus()==0){
                ActivityLauncher.launchPinscreen(context, null, 1); //SCLoginModel.getResetPin()
            }else {
                ActivityLauncher.launchPinscreen(context, SessionManager.getInstance(context).getPhoneNumberOfCurrentUser(), 0); //SCLoginModel.getResetPin()
            }

            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();*/
            //new login - 23-7-2020
            /*if (!SessionManager.getInstance(ReLoadingActivityNew.this).getUserMpin().equals("")) {
                ActivityLauncher.launchPinscreen(context, SessionManager.getInstance(context).getPhoneNumberOfCurrentUser(), 0); //SCLoginModel.getResetPin()
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            } else {
                ActivityLauncher.launchPinscreen(ReLoadingActivityNew.this, null, 0);//SCLoginModel.getResetPin()
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }*/
        } else if (SessionManager.getInstance(this).getCurrentUserID() != null && !SessionManager.getInstance(this).getCurrentUserID().equalsIgnoreCase("") && SessionManager.getInstance(ReLoadingActivityNew.this).getResetPinStatus() == 1) {
            ActivityLauncher.launchPinscreen(context, SessionManager.getInstance(context).getPhoneNumberOfCurrentUser(), 0); //SCLoginModel.getResetPin()

            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
        } else {
            loadLogin();
        }
    }

    /**
     * Reset Pin check response move to pin entry screen
     */
    private void resetPinStatusCheck() {

        if (SessionManager.getInstance(this).getCurrentUserID() != null && !SessionManager.getInstance(this).getCurrentUserID().equalsIgnoreCase("")) {

            ServiceRequest.ServiceListener ResetPinListener = new ServiceRequest.ServiceListener() {
                @Override
                public void onCompleteListener(String response) {

                    if (SCLoginModel == null)
                        SCLoginModel = new SCLoginModel();
                    GsonBuilder gsonBuilder = new GsonBuilder();
                    Gson gson = gsonBuilder.create();
                    SCLoginModel = gson.fromJson(response, SCLoginModel.class);

                    if (SCLoginModel.getStatus().equals("1")) {
                        SessionManager.getInstance(ReLoadingActivityNew.this).setUsermpin("");
                        ActivityLauncher.launchPinscreen(context, null, 1);
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    } else {
                        normalFlow();
                    }
                }

                @Override
                public void onErrorListener(int state) {

                }
            };
            HashMap<String, String> params = new HashMap<String, String>();
            params.put("user_id", SessionManager.getInstance(this).getCurrentUserID());
            ServiceRequest request = new ServiceRequest(this);
            request.makeServiceRequest(Constants.CHECK_PIN_RESET_STATUS, Request.Method.POST, params, ResetPinListener);
        } else {
            normalFlow();
        }
    }


    /**
     * Reset Pin API call
     */
    private void makeVerificationRequest() {

        //   if(!GCM_Id.equalsIgnoreCase("") && GCM_Id!=null ){

//                showProgressDialog();
        if (AppUtils.isNetworkAvailable(ReLoadingActivityNew.this)) {
            resetPinStatusCheck();
        } else {
            normalFlow();
        }

    }

    /**
     * Layout animation
     *
     * @param viewToMove Layout view
     * @param target     view target
     */
    private void translate(View viewToMove, View target) {
        viewToMove.animate()
                .x(target.getX())
                .y(target.getY())
                .setDuration(500)
                .start();
    }

    private void clearDataIfUserChanged(String userId) {
        String prevLoginUserId = sessionManager.getPrevLoginUserId();
        if (!prevLoginUserId.equals("") && !prevLoginUserId.equalsIgnoreCase(userId)) {
            MessageDbController msgDb = CoreController.getDBInstance(ReLoadingActivityNew.this);
            msgDb.deleteDatabase();

            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(ReLoadingActivityNew.this);
            contactDB_sqlite.deleteDatabase();

            UserInfoSession userInfoSession = new UserInfoSession(ReLoadingActivityNew.this);
            userInfoSession.clearData();

            Session session = new Session(ReLoadingActivityNew.this);
            session.clearData();

        }
    }

    /**
     * clear temporary data
     *
     * @param ctx The activity object inherits the Context object.
     */
    public void clearSharedPreferences(Context ctx) {
        File dir = new File(ctx.getFilesDir().getParent() + "/shared_prefs/");
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            // clear each preference file
            ctx.getSharedPreferences(children[i].replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().apply();
            //delete the file
            new File(dir, children[i]).delete();
        }
        SessionManager.getInstance(ReLoadingActivityNew.this).setClearLocalDataStatus1("1");


    }

    /**
     * Clear all local database data
     */
    private void clearCache() {
        MessageDbController msgDb = CoreController.getDBInstance(ReLoadingActivityNew.this);
        msgDb.deleteDatabase();
       /* MessageDbController msgDb = CoreController.getDBInstance(ReLoadingActivityNew.this);
        msgDb.deleteDatabase();
*/
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(ReLoadingActivityNew.this);
        contactDB_sqlite.deleteDatabase();

        UserInfoSession userInfoSession = new UserInfoSession(ReLoadingActivityNew.this);
        userInfoSession.clearData();

        Session session = new Session(ReLoadingActivityNew.this);
        session.clearData();
        SessionManager.getInstance(ReLoadingActivityNew.this).clearData();

    }

    private void skipOTP(final String OTP) {
        Log.d(TAG, "skipOTP: " + OTP);

        String msisdn = SessionManager.getInstance(this).getPhoneNumberOfCurrentUser();
        String mobileNo = SessionManager.getInstance(this).getUserMobileNoWithoutCountryCode();
        String countryCode = SessionManager.getInstance(this).getUserCountryCode();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("msisdn", msisdn);
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        params.put("DeviceId", msisdn);
        params.put("manufacturer", Build.MANUFACTURER);
        params.put("Version", Build.VERSION.RELEASE);
        params.put("OS", "android");
        params.put("PhNumber", mobileNo);
        params.put("imei_1", sessionManager.getImeiOne());
        params.put("imei_2", sessionManager.getImeiTwo());
        params.put("CountryCode", countryCode);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String currentDateandTime = sdf.format(new Date());
        params.put("DateTime", currentDateandTime);
        params.put("code", OTP);
        params.put("callToken", "Android");
        params.put("pushToken", SessionManager.getInstance(this).getCurrentUserID());
        ServiceRequest request = new ServiceRequest(this);
        request.makeServiceRequest(Constants.VERIFY_SMS_CODE, Request.Method.POST, params, verifyCodeListener);

    }


    /**
     * Show alert for error response
     *
     * @param error shown the response error
     */
    public void alert(String error) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(error);
        alertDialogBuilder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


        Button bg = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        bg.setTextColor(Color.BLUE);


        //    AlertDialog alertDialog = new AlertDialog.Builder(ReLoadingActivityNew.this).create();
//        alertDialog.setMessage(error);
//        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
//                new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        alertDialog.show();
    }


    /**
     * Session Pin activity
     *
     * @param mSessionManager getting the value from session data to launch the specific screen based the value (0 and 1)
     */
    private void loadActivity(SessionManager mSessionManager) {
//        if (!mSessionManager.getlogin()) {
//            ActivityLauncher.launchVerifyPhoneScreen(this);
//        } else if (!mSessionManager.getIsprofileUpdate()) {
//            ActivityLauncher.launchProfileInfoScreen(this, null);
//        }    else
        if (!mSessionManager.getUserMpin().equals("")) {
            launch(0);

        } else {
            launch(1);
        }
    }

    /**
     * @param state state based moved moved screen. (if state o means moved Pin entry screen)
     */
    public void launch(final int state) {

        Thread timer_thread = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    if (state == 0) {
                        ActivityLauncher.launchPinscreen(context, SessionManager.getInstance(context).getPhoneNumberOfCurrentUser());

                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    } else {
                        makeVerificationRequest();

//                        ActivityLauncher.launchPinscreen(context,null);
//
//                        overridePendingTransition(R.anim.enter, R.anim.exit);
//                        finish();

                    }

                }
            }
        };
        timer_thread.start();

    }

    private void loadContacts() {

        if (SessionManager.getInstance(this).getlogin()) {
            loadActivity(SessionManager.getInstance(this));
        } else {
            mHandler.postDelayed(runnable, 1000);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String action = intent.getAction();

        String data = intent.getDataString();
        if (Intent.ACTION_VIEW.equals(action) && data != null) {
            SharedPreferences sharedpreferences = getSharedPreferences("url", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();

            editor.putString("url", intent.getDataString());
            editor.commit();
            this.finish();
        }
    }


    /**
     * get Device IMEI number
     */
    public void getImeiNumber() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        String imeiNumber2 = tm.getDeviceId(1);
        String imeiNumber1 = tm.getDeviceId(0);

        sessionManager.setImeiOne(imeiNumber1);
        sessionManager.setImeiTwo(imeiNumber2);
        System.out.println("IMEI-->" + imeiNumber1 + "====" + imeiNumber2);
    }

    /**
     * Phone permission
     *
     * @return Permission allowed or not
     */
    public boolean checkPhoneStatePermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Read Permission
     */
    public void requestPhoneStatePermission() {
        ActivityCompat.requestPermissions(ReLoadingActivityNew.this, new String[]{READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }

    /**
     * Validate Permission method
     *
     * @param requestCode  based on the permission code
     * @param permissions  check the corresponding permission
     * @param grantResults permission to check grant or not
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        try {
            switch (requestCode) {
                case PERMISSION_REQUEST_CODE:
                    boolean phoneStateAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (phoneStateAccepted) {
                        getImeiNumber();
                        BattertyOptimisation();
                        initSocketCallback();
                        mSocketManager.connect();
                    } else if (Build.VERSION.SDK_INT >= 23 && !shouldShowRequestPermissionRationale(permissions[0])) {
                        // User selected the Never Ask Again Option
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", ReLoadingActivityNew.this.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, Constants.PHONE_SETTINGS);

                        Toast.makeText(ReLoadingActivityNew.this, "Grand Permission for Telephone to use app", Toast.LENGTH_LONG).show();
                    } else {
                        showAlert();
                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * Getting the response for permission result. Show alert for mobile permission
     *
     * @param requestCode based on the request code validate the data
     * @param resultCode  response the request code
     * @param data        Pass the value
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.PHONE_SETTINGS) {
//            if(resultCode == Activity.RESULT_OK){
//                getImeiNumber();
//                BattertyOptimisation();
//                initSocketCallback();
//                mSocketManager.connect();
//            }else {
            showAlert();
//            }
//            if(checkPhoneStatePermission()){
//                getImeiNumber();
//                BattertyOptimisation();
//                initSocketCallback();
//                mSocketManager.connect();
//            }else {
//                requestPhoneStatePermission();
//            }
        }

//        if(resultCode == RESULT_OK){
//            switch (requestCode) {
//                case Constants.PHONE_SETTINGS:
//                    getImeiNumber();
//                    BattertyOptimisation();
//                    initSocketCallback();
//                    mSocketManager.connect();
//                    break;
//
//            }
//        }else {
//            showAlert("aChat must need phone state permission to proceed further");
//        }

    }

    /**
     * Shown Alert for Need mobile access permission
     */
    private void showAlert() {

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReLoadingActivityNew.this, R.style.MyDialogTheme);
        alertDialog.setTitle("SynChat");
        alertDialog.setMessage("SynChat must need Telephone permission to proceed further");
        alertDialog.setCancelable(false);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestPhoneStatePermission();
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        alertDialog.show();
    }

    /**
     * Batterty Optimisation method
     */
    public void BattertyOptimisation() {
        //----------------------------------Battery Optimization Stop-------------------------------
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }
        //----------------------------------Battery Optimization Stop-------------------------------


       /* String manufacturer = "xiaomi";
        if(manufacturer.equalsIgnoreCase(android.os.Build.MANUFACTURER)) {
            //this will open auto start screen where user can enable permission for your app
            Intent intent = new Intent();
            intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
         //   intent.setClassName("com.miui.powerkeeper","com.miui.powerkeeper.ui.PowerHideModeActivity");
            startActivity(intent);
        }*/
    }

    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete local directory
     *
     * @param dir file path
     * @return return value
     */
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    /**
     * Load Login View
     */
    private void loadLogin() {

        ReLoadingActivityNew.this.runOnUiThread(new Runnable() {
            public void run() {
                dottedLoader.setVisibility(View.GONE);

                if (AppUtils.isNetworkAvailable(ReLoadingActivityNew.this)) {
                    translate(imgLogoOne, imgLogoTwo);
                }


                if (nonOrganisationVisible == 2) {
                    organisationDesc.setVisibility(View.GONE);
                    Animation animSlideRightLeft = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.slide_left_in);
                    nonOrgRippleView.startAnimation(animSlideRightLeft);
                    nonOrgRippleView.setVisibility(View.VISIBLE);

                } else {
                    /*Animation animSlideRightLeft = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.slide_left_in);
                    nonOrgRippleView.startAnimation(animSlideRightLeft);
                    nonOrgRippleView.setVisibility(View.VISIBLE);*/


                    Animation animSlideRight = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.bottom_up);
                    orgRippleView.startAnimation(animSlideRight);
                    orgRippleView.setVisibility(View.VISIBLE);
                    animSlideRight.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {

                            if (nonOrganisationVisible == 0) {
                                Animation animSlideLeftIn = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.left_in);
                                organisationDesc.startAnimation(animSlideLeftIn);
                                organisationDesc.setVisibility(View.VISIBLE);
                            } else {
                                Animation animSlideRightLeft = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.right_to_left);
                                nonOrgRippleView.startAnimation(animSlideRightLeft);
                                nonOrgRippleView.setVisibility(View.VISIBLE);

                                Animation animSlideLeftIn = AnimationUtils.loadAnimation(ReLoadingActivityNew.this, R.anim.left_in);
                                organisationDesc.startAnimation(animSlideLeftIn);
                                organisationDesc.setVisibility(View.VISIBLE);
                            }


                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                }
            }
        });

//                ActivityLauncher.launchRegisterScreen(ReLoadingActivityNew.this);


    }

}