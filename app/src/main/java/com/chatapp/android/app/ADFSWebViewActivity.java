package com.chatapp.android.app;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.chatapp.android.R;
import com.chatapp.android.app.utils.AppUtils;
import com.chatapp.android.app.utils.Keys;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.core.ActivityLauncher;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.ChangeSetController;
import com.chatapp.android.core.model.SCLoginModel;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.service.Constants;
import com.chatapp.android.core.service.ServiceRequest;
import com.chatapp.android.core.socket.MessageService;
import com.chatapp.android.core.socket.SocketManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static android.Manifest.permission.READ_PHONE_STATE;

public class ADFSWebViewActivity extends AppCompatActivity {

    private static final boolean SKIP_OTP_VERIFICATION = false;
    //    final String websiteURL = "https://adanissouat.adani.com/adfs/ls/?wtrealm=https://achatauthuat.azurewebsites.net&wctx=WsFedOwinState%3dTo8tT5C9zhQ4Qci3CYznVjsAf-itbr5fUdjOWT-d5NvDSE7tHO1yN8UTuMuW7QkihwTJ5Agr49X2hwBdH4_XgZ5Q-1WCfC3IaBaKNsqA7sRi3FyzJOj3vBF3eT39e0bO8WZQ24bX90LFVqiDsmhdgQE8uBWBha8cE184uaBHcmj1yyEsPXxtX0NWwx20yIleVjIu_e4XCygGI_jBNQQ2_Q&wa=wsignin1.0&wreply=https%3A%2F%2Fachatauthuat.azurewebsites.net%2FAccount%2FLoginCallbackChatAppAdfs%3Fimei%3D992929929292%2C828282828%26Device%3DA%26DeviceId%3D282828288282";
    private WebView webView;
    private ProgressBar progressBar;
    private ImageView logo;
    private SCLoginModel SCLoginModel;
    private SessionManager sessionManager;
    private String urlParams = "";
    private String isLive = "";
    private String android_id;
    private static final int PERMISSION_REQUEST_CODE = 200;
    private int pinResetStatus = 0;
    private AlertDialog.Builder alertDialog;

    /**
     * Login verification and move to pin entry screen based on mpin status & start message service
     */
    private ServiceRequest.ServiceListener verifcationListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {

            try {

                if (SCLoginModel == null)
                    SCLoginModel = new SCLoginModel();
                GsonBuilder gsonBuilder = new GsonBuilder();
                Gson gson = gsonBuilder.create();
                SCLoginModel = gson.fromJson(response, SCLoginModel.class);
                if (SCLoginModel.getProfilePic() != null) {
                    String imgPath = SCLoginModel.getProfilePic() + "?id=" + Calendar.getInstance().getTimeInMillis();
                    SessionManager.getInstance(ADFSWebViewActivity.this).setUserProfilePic(imgPath);
                }

                if (SCLoginModel.getStatus() != null && !SCLoginModel.getStatus().equalsIgnoreCase("")) {
                    SessionManager.getInstance(ADFSWebViewActivity.this).setcurrentUserstatus(new String(Base64.decode(SCLoginModel.getStatus(), Base64.DEFAULT)));
                } else {
                    SessionManager.getInstance(ADFSWebViewActivity.this).setcurrentUserstatus("Hey there! I am using " + getResources().getString(R.string.app_name));
                }

                // success
                if (SCLoginModel.getErrNum().equals("0")) {
                    if (SCLoginModel.get_id() != null) {
                        SessionManager.getInstance(ADFSWebViewActivity.this).setCurrentUserID(SCLoginModel.get_id());
                        if (SCLoginModel.getUserEmail() != null) {
                            SessionManager.getInstance(ADFSWebViewActivity.this).setLoginType(SCLoginModel.getLoginType());
                            SessionManager.getInstance(ADFSWebViewActivity.this).setCurrentUserEmailID(SCLoginModel.getUserEmail());
                        }
//                clearDataIfUserChanged(SCLoginModel.get_id());
                    }
                    SessionManager.getInstance(ADFSWebViewActivity.this).Islogedin(true);

                    SessionManager.getInstance(ADFSWebViewActivity.this).setPhoneNumberOfCurrentUser(android_id);
                    if (SCLoginModel.getName() != null && !SCLoginModel.getName().isEmpty()) {
                        Log.d("getName--> IF", SCLoginModel.getName());
                        SessionManager.getInstance(ADFSWebViewActivity.this).setnameOfCurrentUser(new String(Base64.decode(SCLoginModel.getName(), Base64.DEFAULT)));
                    } else {
                        Log.d("getName--> ELSE", SCLoginModel.getName());
                    }
                    SessionManager.getInstance(ADFSWebViewActivity.this).setUserCountryCode("+" + "");
                    SessionManager.getInstance(ADFSWebViewActivity.this).setUserMobileNoWithoutCountryCode(android_id);

                    try {
                        JSONObject object = new JSONObject(response);
                        if (SessionManager.getInstance(ADFSWebViewActivity.this).getTwilioMode().equalsIgnoreCase(SessionManager.TWILIO_DEV_MODE)) {
                            SessionManager.getInstance(ADFSWebViewActivity.this).setLoginOTP(object.getString("code"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (SKIP_OTP_VERIFICATION) {

                    } else {

                        SharedPreferences shPref = getSharedPreferences("global_settings", MODE_PRIVATE);
                        SharedPreferences.Editor et = shPref.edit();
                        et.putString("userId", SessionManager.getInstance(ADFSWebViewActivity.this).getPhoneNumberOfCurrentUser());
                        et.apply();
                        SessionManager.getInstance(ADFSWebViewActivity.this).IsnumberVerified(true);
                        SessionManager.getInstance(ADFSWebViewActivity.this).setLoginCount(SCLoginModel.getLoginCount());
                       /* ActivityLauncher.launchPinscreen(ADFSWebViewActivity.this, null);
                        finish();*/

                        //new login - 23-7-2020
                        try {
                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(1);
                            SessionManager.getInstance(ADFSWebViewActivity.this).setUsermpin("");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!SessionManager.getInstance(ADFSWebViewActivity.this).getUserMpin().equals("")) {
                                        ActivityLauncher.launchPinscreen(ADFSWebViewActivity.this, SessionManager.getInstance(ADFSWebViewActivity.this).getPhoneNumberOfCurrentUser(), 0);
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                        finish();
                                    } else {
                                        ActivityLauncher.launchPinscreen(ADFSWebViewActivity.this, null, 0);
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                        finish();
                                    }
                                }
                            }, 500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                    SessionManager.getInstance(ADFSWebViewActivity.this).setAdminPendingStatus("");
                } else if (SCLoginModel.getErrNum().equals("1")) {

                    String error = SCLoginModel.getMessage();
                    Toast.makeText(ADFSWebViewActivity.this, error, Toast.LENGTH_SHORT).show();
                    SessionManager.getInstance(ADFSWebViewActivity.this).setAdminPendingStatus("");
                } else if (SCLoginModel.getErrNum().equals("2")) {
                    if (SCLoginModel.get_id() != null) {
                        SessionManager.getInstance(ADFSWebViewActivity.this).setCurrentUserID(SCLoginModel.get_id());
//                clearDataIfUserChanged(SCLoginModel.get_id());
                    }
                    String error = SCLoginModel.getMessage();
                    showAlert(error, 0);
                    SessionManager.getInstance(ADFSWebViewActivity.this).setAdminPendingStatus("1");
                } else if (SCLoginModel.getErrNum().equals("3")) {
                    String error = SCLoginModel.getMessage();
                    SessionManager.getInstance(ADFSWebViewActivity.this).setAdminPendingStatus("1");
                    if (SCLoginModel.get_id() != null) {
                        SessionManager.getInstance(ADFSWebViewActivity.this).setLoginType(SCLoginModel.getLoginType());
                        SessionManager.getInstance(ADFSWebViewActivity.this).setCurrentUserID(SCLoginModel.get_id());
                        if (SCLoginModel.getUserEmail() != null) {
                            SessionManager.getInstance(ADFSWebViewActivity.this).setCurrentUserEmailID(SCLoginModel.getUserEmail());
                        }
                    }
                    if (!MessageService.isStarted()) {
                        startService(new Intent(ADFSWebViewActivity.this, MessageService.class));
                    }
                    showAlert(error, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onErrorListener(int state) {
//            ChatappDialogUtils.showCheckInternetDialog(ADFSWebViewActivity.this);
            if (SessionManager.getInstance(ADFSWebViewActivity.this).getLoginFailedStatus().equalsIgnoreCase("")) {
                makeVerificationRequest();
                SessionManager.getInstance(ADFSWebViewActivity.this).setLoginFailedStatus("1");
            } else {
                showCheckInternetDialog(ADFSWebViewActivity.this);
            }
        }
    };

    public void clearSharedPreferences(Context ctx) {
        File dir = new File(ctx.getFilesDir().getParent() + "/shared_prefs/");
        String[] children = dir.list();
        for (int i = 0; i < children.length; i++) {
            // clear each preference file
            ctx.getSharedPreferences(children[i].replace(".xml", ""), Context.MODE_PRIVATE).edit().clear().apply();
            //delete the file
            new File(dir, children[i]).delete();
        }
        SessionManager.getInstance(ADFSWebViewActivity.this).setClearLocalDataStatus1("1");
    }

    private void clearCache() {
        MessageDbController msgDb = CoreController.getDBInstance(ADFSWebViewActivity.this);
        msgDb.deleteDatabase();
       /* MessageDbController msgDb = CoreController.getDBInstance(ReLoadingActivityNew.this);
        msgDb.deleteDatabase();
*/
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(ADFSWebViewActivity.this);
        contactDB_sqlite.deleteDatabase();

        UserInfoSession userInfoSession = new UserInfoSession(ADFSWebViewActivity.this);
        userInfoSession.clearData();

        Session session = new Session(ADFSWebViewActivity.this);
        session.clearData();
        SessionManager.getInstance(ADFSWebViewActivity.this).clearData();

    }

    public boolean checkPhoneStatePermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Check runtime permission
     */
    public void requestPhoneStatePermission() {
        ActivityCompat.requestPermissions(ADFSWebViewActivity.this, new String[]{READ_PHONE_STATE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adfs);

        try {
            if (getIntent().hasExtra("reset_pin_status")) {
                pinResetStatus = getIntent().getIntExtra("reset_pin_status", 0);
            }
            alertDialog = new AlertDialog.Builder(ADFSWebViewActivity.this, R.style.MyDialogTheme);

        } catch (Exception e) {
            e.printStackTrace();
        }


        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        sessionManager = SessionManager.getInstance(ADFSWebViewActivity.this);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorAccent)); //status bar or the time bar at the top
        }
        progressBar = findViewById(R.id.pBar);
        logo = findViewById(R.id.logo);
        webView = findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
//        webView.getSettings().setDomStorageEnabled(true);
//        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        CookieSyncManager cookieSyncMngr = CookieSyncManager.createInstance(ADFSWebViewActivity.this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
        if (checkPhoneStatePermission()) {
            getImeiNumber();
        } else {
            requestPhoneStatePermission();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        if (AppUtils.isNetworkAvailable(ADFSWebViewActivity.this)) {
            webView.setWebViewClient(new WebViewClient() {


                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // TODO Auto-generated method stub
                    view.loadUrl(url);
                    return true;
                }

                @Override
                public void onPageFinished(WebView view, String url) {

               /* Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
                fadeIn.setDuration(1000);
                webView.startAnimation(fadeIn);*/
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            logo.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                            webView.setVisibility(View.VISIBLE);
                        }
                    }, 1000);

                    System.out.println("Test13213-->" + url);
                    if (url.equalsIgnoreCase(SessionManager.getInstance(ADFSWebViewActivity.this).getAdfsReturnUrl() + "1")) {
                        makeVerificationRequest();
                    } else if (url.equalsIgnoreCase(SessionManager.getInstance(ADFSWebViewActivity.this).getAdfsReturnUrl() + "-1")) {
                        showAlert("Unauthorized login please contact admin", 0);
                    } else if (url.equalsIgnoreCase(SessionManager.getInstance(ADFSWebViewActivity.this).getAdfsReturnUrl() + "0")) {
                        showAlert("Something went wrong!!!", 1);
                    } else if (url.equalsIgnoreCase(SessionManager.getInstance(ADFSWebViewActivity.this).getAdfsReturnUrl() + "4")) {
                        SessionManager.getInstance(ADFSWebViewActivity.this).setResetPinStatus(0);
                        ActivityLauncher.launchPinscreen(ADFSWebViewActivity.this, null, 1); //SCLoginModel.getResetPin()
                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    } else if (url.equalsIgnoreCase(SessionManager.getInstance(ADFSWebViewActivity.this).getAdfsReturnUrl() + "5")) {
                        showAlert("Please enter the valid username or password", 0);
                    } else if (url.equalsIgnoreCase(SessionManager.getInstance(ADFSWebViewActivity.this).getAdfsReturnUrl() + "6")) {
                        // we need do logout the user since user trying from old device
                        performLogout();
                    }

                    // TODO Auto-generated method stub
                    super.onPageFinished(view, url);

                }

                @Override
                public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                    super.onReceivedError(view, request, error);
                    showAlert("Something went wrong!!!", 1);
                }

                @Override
                public void onReceivedSslError(WebView view, final SslErrorHandler handler, SslError error) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    String message = "SSL Certificate error.";
                    switch (error.getPrimaryError()) {
                        case SslError.SSL_UNTRUSTED:
                            message = "The certificate authority is not trusted.";
                            break;
                        case SslError.SSL_EXPIRED:
                            message = "The certificate has expired.";
                            break;
                        case SslError.SSL_IDMISMATCH:
                            message = "The certificate Hostname mismatch.";
                            break;
                        case SslError.SSL_NOTYETVALID:
                            message = "The certificate is not yet valid.";
                            break;
                        case SslError.SSL_DATE_INVALID:
                            message = "The date of the certificate is invalid.";
                            break;
                        case SslError.SSL_INVALID:
                            message = "A generic error occurred.";
                            break;
                        case SslError.SSL_MAX_ERROR:
                            message = "Unknown error occurred.";
                            break;
                    }
                    message += " Do you want to continue anyway?";

                    builder.setTitle("SSL Certificate Error");
                    builder.setMessage(message);
                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.proceed();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            handler.cancel();
                        }
                    });
                    final AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }

//        CookieManager.getInstance().setAcceptCookie(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // If Android 6.0+ i must add support for Third Party Cookies
//            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        }
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);
        webView.getSettings().setSupportMultipleWindows(false);
        webView.getSettings().setSupportZoom(true);
        webView.setVerticalScrollBarEnabled(true);
        webView.setHorizontalScrollBarEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
//        webView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        webView.getSettings().setLoadsImagesAutomatically(true);
//        webView.getSettings().setAppCacheEnabled(true);
//        webView.getSettings().setAllowFileAccess(true);
//        webView.getSettings().setAllowContentAccess(true);

//        webView.loadUrl(websiteURL);
//        if(!TextUtils.isEmpty(urlParams))
//        {
        Log.d("android_id--->", android_id);
        if (!TextUtils.isEmpty(android_id)) {
            //urlParams = Keys.DATA + new String(Base64.encode(("A," + android_id + "," + sessionManager.getImeiOne() + "," + sessionManager.getImeiTwo() + "," + (Constants.SOCKET_IP_BASE.equalsIgnoreCase("https://achat.adani.com") ? "L" : "U")).getBytes(), Base64.DEFAULT));

            if (pinResetStatus == 1) {
                urlParams = Keys.DATA + new String(Base64.encode(("A," + android_id + "," + Build.MANUFACTURER + "," + Build.VERSION.RELEASE + "," + getVersionName() + "," + (Constants.SOCKET_IP_BASE.equalsIgnoreCase("https://achat.adani.com") ? "L" : "U" + "," + " " + "," + "R")).getBytes(), Base64.DEFAULT));

            } else {
                urlParams = Keys.DATA + new String(Base64.encode(("A," + android_id + "," + Build.MANUFACTURER + "," + Build.VERSION.RELEASE + "," + getVersionName() + "," + (Constants.SOCKET_IP_BASE.equalsIgnoreCase("https://achat.adani.com") ? "L" : "U" + "," + " " + "," + "N")).getBytes(), Base64.DEFAULT));

            }


            webView.loadUrl(sessionManager.getADFSUrl() + "id=" + urlParams);
            Log.d("ADFSC--->", urlParams);
        } else {
            //        webView.loadUrl(sessionManager.getADFSUrl() + urlParams);
            Log.d("ADFSC--->", urlParams);
        }

//        webView.loadUrl("http://achatauthuat.azurewebsites.net/Account/SignUpAdfs?Id=" + urlParams);

//        }


//        System.out.println("ADFSC---> "+new String(Base64.encode(("A," + sessionManager.getDeviceId()+","+sessionManager.getImeiOne() + "," + sessionManager.getImeiTwo()+ Keys.DATA).getBytes(), Base64.DEFAULT)));


    }

    /**
     * No Internet Alert popup
     */
    private void internetAlert() {

        if (!AppUtils.isNetworkAvailable(ADFSWebViewActivity.this)) {
            try {
                alertDialog.setCancelable(false);
                // Setting Dialog Title
                alertDialog.setTitle("aChat");
                // Setting Dialog Message
                alertDialog.setMessage("You are offline..Please Check your Internet connection");
                // Setting Icon to Dialog
                //  alertDialog.setIcon(R.drawable.tick);
                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        ActivityLauncher.launchReloadingActivityScreen(ADFSWebViewActivity.this);
                    }
                });
                alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                // Showing Alert Message
                alertDialog.show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    /**
     * Getting android UID for the device.
     */
    @Override
    protected void onResume() {
        super.onResume();
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        internetAlert();

    }

    /**
     * killed webview
     */
    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * More info this method can be found at
     * http://developer.android.com/training/camera/photobasics.html
     *
     * @return
     * @throws IOException
     */

    /**
     * killed webview
     */
    @Override
    protected void onDestroy() {
        try {
            webView.clearFormData();
            webView.clearCache(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onDestroy();
    }

    private void clearDataIfUserChanged(String userId) {
        String prevLoginUserId = sessionManager.getPrevLoginUserId();
        if (!prevLoginUserId.equals("") && !prevLoginUserId.equalsIgnoreCase(userId)) {
            MessageDbController msgDb = CoreController.getDBInstance(ADFSWebViewActivity.this);
            msgDb.deleteDatabase();

            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(ADFSWebViewActivity.this);
            contactDB_sqlite.deleteDatabase();

            UserInfoSession userInfoSession = new UserInfoSession(ADFSWebViewActivity.this);
            userInfoSession.clearData();

            Session session = new Session(ADFSWebViewActivity.this);
            session.clearData();

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
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            verCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }

        return verCode;
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
        if (!TextUtils.isEmpty(imeiNumber1)) {
            sessionManager.setImeiOne(imeiNumber1);
        }
        if (!TextUtils.isEmpty(imeiNumber2)) {
            sessionManager.setImeiTwo(imeiNumber2);
        }

        System.out.println("IMEI-->" + imeiNumber1 + "====" + imeiNumber2);
    }


    /**
     * Make request for mobile verification and based on the navigate the screen
     */
    private void makeVerificationRequest() {
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("msisdn", android_id);
        params.put("DeviceId", android_id);
        params.put("pushToken", "");
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

        ServiceRequest request = new ServiceRequest(this);
        request.makeServiceRequest(Constants.VERIFY_NUMBER_REQUEST, Request.Method.POST, params, verifcationListener);

    }
   /* public static String getPhoneIMEI(Context context) {
        String deviceID = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = context.checkCallingOrSelfPermission(Manifest.permission.READ_PHONE_STATE);
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                permissionResult = context.checkCallingOrSelfPermission("android.permission.READ_PRIVILEGED_PHONE_STATE");
            }
            boolean isPermissionGranted = permissionResult == PackageManager.PERMISSION_GRANTED;
            if (!isPermissionGranted) {
                deviceID = getDeviceIDFromReflection(context);
            } else {
                deviceID = getDeviceIDFromSystem(context);
            }
        } else {
            deviceID = getDeviceIDFromSystem(context);
        }

        Log.i(TAG,"getPhoneIMEI : " + deviceID);
        return deviceID;
    }*/

    /**
     * get project version Name
     *
     * @return this is also number
     */
    private String getVersionName() {
        String verName = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            verName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }

        return verName;
    }


    /**
     * Login Error popup
     *
     * @param message shown response error
     * @param type    based on type 0 means finish popup 1 means moved to splash screen
     */
    private void showAlert(String message, final int type) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ADFSWebViewActivity.this, R.style.MyDialogTheme);
            alertDialog.setCancelable(false);
            // Setting Dialog Title
            alertDialog.setTitle("aChat");
            // Setting Dialog Message
            alertDialog.setMessage(message);
            // Setting Icon to Dialog
            //  alertDialog.setIcon(R.drawable.tick);
            // Setting Positive "Yes" Button
            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (type == 0) {
                        finish();
                    } else {
                        ActivityLauncher.launchReloadingActivityScreen(ADFSWebViewActivity.this);
                    }

                }
            });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private String generateHashWithHmac256(String message) {
        String messageDigest = "";
        try {
            final String hashingAlgorithm = "HmacSHA256";

            byte[] bytes = hmac(hashingAlgorithm, "ungeneohlelwenilwakho".getBytes(), message.getBytes());

            messageDigest = bytesToHex(bytes);

            Log.i("", "message digest: " + messageDigest);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return messageDigest;
    }

    public static byte[] hmac(String algorithm, byte[] key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(message);
    }

    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
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
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                boolean phoneStateAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (phoneStateAccepted) {
                    getImeiNumber();
                }
                break;

        }
    }


    /**
     * No internet popup
     *
     * @param mContext current activity
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

    /**
     * Delete all local database
     */
    private void performLogout() {
        MessageDbController db = CoreController.getDBInstance(this);
        db.deleteDatabase();

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        contactDB_sqlite.deleteDatabase();

        notifyLogoutToServer();
        ChangeSetController.setChangeStatus("0");
        SessionManager.getInstance(ADFSWebViewActivity.this).logoutUser(false);
    }

    /**
     * logout mobile via web
     */
    private void notifyLogoutToServer() {
        try {
            JSONObject logoutObj = new JSONObject();
            logoutObj.put("from", SessionManager.getInstance(ADFSWebViewActivity.this).getCurrentUserID());
            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_MOBILE_TO_WEB_LOGOUT);
            event.setMessageObject(logoutObj);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
