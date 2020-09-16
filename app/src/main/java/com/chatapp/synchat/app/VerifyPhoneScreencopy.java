package com.chatapp.synchat.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiEditText;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.ShortcutBadgeManager;
import com.chatapp.synchat.core.chatapphelperclass.ChatappDialogUtils;
import com.chatapp.synchat.core.chatapphelperclass.ChatappPermissionValidator;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.model.SCLoginModel;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.service.ServiceRequest;
import com.chatapp.synchat.core.socket.SocketManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import io.socket.client.Socket;

/**
 * created by  Adhash Team on 10/5/2016.
 */
public class VerifyPhoneScreencopy extends CoreActivity implements View.OnClickListener {

    String code = "", otp = "";
    private SessionManager sessionManager;
    private AvnNextLTProRegTextView supMessenger, conformCountyCode;
    AvnNextLTProDemiTextView selectCountry, choseCountry, tvTermsAndConditions;
    private ImageView ivTermsAndConditions;
    private ImageView okButton;
    public AvnNextLTProDemiEditText phoneNumber;
    private final int selectedCountry = 11;
    private boolean termsAndConditionsAccepted = true;
    private SCLoginModel SCLoginModel;
    Boolean isagree = false;

    private List<String> codeList;
    private List<String> countryList;
    CheckBox tvTermsAndConditions_checkbox;

    private final int REQUEST_CODE_PERMISSION_MULTIPLE = 123;
    private ArrayList<ChatappPermissionValidator.Constants> myPermissionConstantsArrayList;
    private boolean isDeninedRTPs = false;
    private boolean showRationaleRTPs = false;
    private SocketManager mSocketManager;
    private boolean country_wise_filter = false;
    private GPSTracker gpsTracker;

    static String[] country;
    static String[] codes;

    private ArrayList<String> list;
    private ArrayList<String> codelist;
    private static final String LETTER_SPACING = " ";
    private ImageView scroll_country;
    private RelativeLayout code_fetch_layout;
    private static final boolean SKIP_OTP_VERIFICATION = false;
    private static final String TAG = VerifyPhoneScreencopy.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone_screen);
        Log.d(TAG, "onCreate: ");
        if (SKIP_OTP_VERIFICATION) {
            findViewById(R.id.send_message).setVisibility(View.GONE);
            findViewById(R.id.charge).setVisibility(View.GONE);
        }
//        setTitle(R.string.verify_phone_number_txt);
        initView();
        initData();



       String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);

        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String s = editable.toString();
                if (country_wise_filter) {
                    if (s.equalsIgnoreCase("0") || s.equalsIgnoreCase("00") || s.equalsIgnoreCase("000")) {
                        phoneNumber.setText("");
                        phoneNumber.setError("Enter valid Mobile Number");
                    }
                }
            }
        });


        scroll_country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityLauncher.launchChooseCountryScreen(VerifyPhoneScreencopy.this, selectedCountry);
            }
        });


        code_fetch_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ActivityLauncher.launchChooseCountryScreen(VerifyPhoneScreencopy.this, selectedCountry);
            }
        });

    }

    private void initData() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }
        }

        Resources res = getResources();
        String[] code = res.getStringArray(R.array.country_code);
        String[] country = res.getStringArray(R.array.country_list);
        codeList = Arrays.asList(code);
        countryList = Arrays.asList(country);
        Log.e("" + code.length, "" + country.length);

        ShortcutBadgeManager manager = new ShortcutBadgeManager(this);
        manager.clearBadgeCount();
        manager.setContactLastRefreshTime(0L);
        SessionManager.getInstance(VerifyPhoneScreencopy.this).setnameOfCurrentUser("");
        checkAndRequestPermissions();
        initSocketCallback();
        mSocketManager.connect();
    }

    private void initView() {
        choseCountry = (AvnNextLTProDemiTextView) findViewById(R.id.selectCountry);
        selectCountry = (AvnNextLTProDemiTextView) findViewById(R.id.selectCountry);
        okButton = (ImageView) findViewById(R.id.okButton);
        sessionManager = SessionManager.getInstance(VerifyPhoneScreencopy.this);
        phoneNumber = (AvnNextLTProDemiEditText) findViewById(R.id.phoneNumber);
        tvTermsAndConditions = (AvnNextLTProDemiTextView) findViewById(R.id.tvTermsAndConditions);
        tvTermsAndConditions_checkbox = (CheckBox) findViewById(R.id.tvTermsAndConditions_checkbox);
        scroll_country = (ImageView) findViewById(R.id.scroll_country);
        code_fetch_layout = (RelativeLayout) findViewById(R.id.code_fetch_layout);

        Context c = getApplicationContext();
        Resources res = c.getResources();

        country = res.getStringArray(R.array.country_list);
        codes = res.getStringArray(R.array.country_code);

        list = new ArrayList<String>();
        codelist = new ArrayList<String>();
        list.clear();
        codelist.clear();
        for (int i = 0; i < country.length; i++) {
            list.add(country[i]);
            codelist.add(codes[i]);
        }


//        gpsTracker = new com.nowletschat.android.HandyScript.gps.GPSTracker(VerifyPhoneScreen.this);
//        if (gpsTracker.canGetLocation() && gpsTracker.isgpsenabled()) {
//
//            double MyCurrent_lat = gpsTracker.getLatitude();
//            double MyCurrent_long = gpsTracker.getLongitude();
//
//            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//            try {
//                List<Address> addresses = geocoder.getFromLocation(MyCurrent_lat, MyCurrent_long, 1);
//                if (addresses != null && !addresses.isEmpty()) {
//
//                    String Str_getCountryCode = addresses.get(0).getCountryCode();
//                    if (Str_getCountryCode.length() > 0 && !Str_getCountryCode.equals(null) && !Str_getCountryCode.equals("null")) {
//                        String Str_countyCode = CountryDialCode.getCountryCode(Str_getCountryCode);
//
//                        for(int i=0; i<codes.length;i++){
//                            String code=codes[i];
//                            String testcode="+"+code;
//                            if(testcode.equalsIgnoreCase(Str_countyCode)){
//                                choseCountry.setText(Str_countyCode);
//                            }
//                        }
//
//                        //choseCountry.setText(Str_countyCode);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//
//        }
//        else if(gpsTracker.canGetLocation() && gpsTracker.isnetworksenabled()){
//
//            double MyCurrent_lat = gpsTracker.getLatitude();
//            double MyCurrent_long = gpsTracker.getLongitude();
//            System.out.println("Network Enabled:"+" "+MyCurrent_lat+" "+MyCurrent_long);
//            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
//            try {
//                List<Address> addresses = geocoder.getFromLocation(MyCurrent_lat, MyCurrent_long, 1);
//                if (addresses != null && !addresses.isEmpty()) {
//
//                    String Str_getCountryCode = addresses.get(0).getCountryCode();
//                    if (Str_getCountryCode.length() > 0 && !Str_getCountryCode.equals(null) && !Str_getCountryCode.equals("null")) {
//                        String Str_countyCode = CountryDialCode.getCountryCode(Str_getCountryCode);
//
//                        for(int i=0; i<codes.length;i++){
//                            String code=codes[i];
//                            String testcode="+"+code;
//                            if(testcode.equalsIgnoreCase(Str_countyCode)){
//                                choseCountry.setText(country[i]+ " " + Str_countyCode);
//                            }
//                        }
//
//                        //choseCountry.setText(Str_countyCode);
//                    }
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                final Dialog dialog = new Dialog(VerifyPhoneScreencopy.this);
                final AvnNextLTProDemiTextView agree, disagree;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.setContentView(R.layout.dialog_termsand_condition);
                agree = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.agreed);
                disagree = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.disagree);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);

                tvTermsAndConditions.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        dialog.show();
                        agree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                isagree = true;
                                tvTermsAndConditions_checkbox.setChecked(isagree);
                                dialog.dismiss();
                            }
                        });
                        disagree.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialog.dismiss();
                                isagree = false;
                                tvTermsAndConditions_checkbox.setChecked(isagree);

                            }
                        });

                    }
                });
            }
        }, 100);
        tvTermsAndConditions_checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    isagree = true;
                    tvTermsAndConditions_checkbox.setChecked(isChecked);
                } else {
                    isagree = false;
                    tvTermsAndConditions_checkbox.setChecked(isChecked);
                }
            }
        });
        choseCountry.setOnClickListener(this);
        okButton.setOnClickListener(this);


        Intent i = getIntent();
        //  String text = i.getStringExtra("TextBox");
        //  phoneNumber.setText(text);
        initProgress("Configure settings....", true);
        showProgressDialog();
    }

    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            myPermissionConstantsArrayList = new ArrayList<>();

            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.PERMISSION_READ_SMS);


            if (ChatappPermissionValidator.checkPermission(VerifyPhoneScreencopy.this, myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE)) {
                onPermissionGranted();
            }
        } else {
            onPermissionGranted();
        }
    }

    private void onPermissionGranted() {


    }

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

    private void onPermissionResult() {
        if (isDeninedRTPs) {
            if (!showRationaleRTPs) {
                //goToSettings();
                ChatappDialogUtils.showPermissionDeniedDialog(VerifyPhoneScreencopy.this);
            } else {
                isDeninedRTPs = false;
                ChatappPermissionValidator.checkPermission(this,
                        myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE);
            }
        } else {
            onPermissionGranted();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.selectCountry || view.getId() == R.id.countryDropDownMain) {
            ActivityLauncher.launchChooseCountryScreen(this, selectedCountry);
        }
        if (view.getId() == R.id.okButton) {
            hideKeyboard();
            String number = phoneNumber.getText().toString().trim();
            number = number.replace(" ", "");
            String code = choseCountry.getText().toString();
            if (!choseCountry.getText().toString().equalsIgnoreCase("Code") && !choseCountry.getText().toString().equalsIgnoreCase("") && !choseCountry.getText().toString().equalsIgnoreCase(null)) {
                if (isagree) {
                    if (!number.equals("") && number.length() > 4) {
                        makeVerificationRequest();
                    } else {
                        Toast.makeText(VerifyPhoneScreencopy.this, "Enter valid number", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerifyPhoneScreencopy.this, "Please accept Terms and Condition", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(VerifyPhoneScreencopy.this, "Please select country code", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed here it is 2
        if (resultCode != RESULT_CANCELED && requestCode == selectedCountry) {
            String message = data.getStringExtra("MESSAGE");
            code = data.getStringExtra("CODE");
            if (code.equalsIgnoreCase("263")) {
                country_wise_filter = true;
                choseCountry.setText("  (+" + code + ")");
            } else {
                country_wise_filter = false;
                choseCountry.setText("  (+" + code + ")");
            }

            sessionManager.setCountryCodeOfCurrentUser("+" + code);
        }
    }


    /* This API checks if the user has entered a number of not.
 * In case the number is not entered, an alert is displayed to the user
 * else send this number to the server and execute the login API
 */
    private void showAlertDialog(String title, String msg) {
        String number = phoneNumber.getText().toString().trim();
        number = number.replace(" ", "");
        if (number.length() < 5) {
            CustomAlertDialog dialog = new CustomAlertDialog();
            dialog.setMessage("Enter valid number");
            dialog.setPositiveButtonText("OK");
            dialog.setNegativeButtonText("CANCEL");
            dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                @Override
                public void onPositiveButtonClick() {

                }

                @Override
                public void onNegativeButtonClick() {

                }
            });
            dialog.show(getSupportFragmentManager(), "CustomAlert");
        } else {
            if (!termsAndConditionsAccepted) {
                CustomAlertDialog dialog = new CustomAlertDialog();
                dialog.setMessage("Please Accept Terms and Conditions");
                dialog.setPositiveButtonText("OK");
                dialog.setNegativeButtonText("CANCEL");
                dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                    @Override
                    public void onPositiveButtonClick() {

                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }
                });
                dialog.show(getSupportFragmentManager(), "CustomAlert");
            } else {

                CustomAlertDialog dialog = new CustomAlertDialog();
                dialog.setPositiveButtonText("OK");
                dialog.setNegativeButtonText("EDIT");

                // String msg2 = "<b>We will be verifying the phone number:\n\n" + "+" + code + " " + phoneNumber.getText().toString() + "\n\nIs this OK, or would you like to edit the number?<b>";
                String msg2 = "We will be verifying the phone number:<br><br>" + "<b>+" + code + " " + phoneNumber.getText().toString() + "</b><br><br>Is this OK, or would you like to edit the number?";
                dialog.setMessage(msg2);

                dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        makeVerificationRequest();
                    }

                    @Override
                    public void onNegativeButtonClick() {

                    }
                });
                dialog.show(getSupportFragmentManager(), "CustomAlert");
            }
        }
    }

    private void makeVerificationRequest() {
        showProgressDialog();
        HashMap<String, String> params = new HashMap<String, String>();
        String cCode = code;
        String uPhone = phoneNumber.getText().toString();
        System.out.println("Country_code" + "" + cCode);
        params.put("msisdn", "+" + cCode + uPhone);
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        params.put("DeviceId", android_id);
        params.put("manufacturer", Build.MANUFACTURER);
        params.put("Version", Build.VERSION.RELEASE);
        params.put("OS", "android");
        params.put("PhNumber", uPhone);
        params.put("CountryCode", "+" + cCode);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String currentDateandTime = sdf.format(new Date());
        params.put("DateTime", currentDateandTime);
        params.put("callToken", "Android");
        ServiceRequest request = new ServiceRequest(this);
        request.makeServiceRequest(Constants.VERIFY_NUMBER_REQUEST, Request.Method.POST, params, verifcationListener);
    }

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
                SessionManager.getInstance(VerifyPhoneScreencopy.this).setUserProfilePic(imgPath);
            }

            if (SCLoginModel.getStatus() != null && !SCLoginModel.getStatus().equalsIgnoreCase("")) {
                SessionManager.getInstance(VerifyPhoneScreencopy.this).setcurrentUserstatus(new String(Base64.decode(SCLoginModel.getStatus(), Base64.DEFAULT)));
            } else {
                SessionManager.getInstance(VerifyPhoneScreencopy.this).setcurrentUserstatus("Hey there! I am using "
                        + getResources().getString(R.string.app_name));
            }
            if (SCLoginModel.get_id() != null) {
                SessionManager.getInstance(VerifyPhoneScreencopy.this).setCurrentUserID(SCLoginModel.get_id());

                clearDataIfUserChanged(SCLoginModel.get_id());
            }

            if(Constants.IS_ENCRYPTION_ENABLED){
            if (SCLoginModel.getToken() != null) {
                String tokenOriginal = SCLoginModel.getToken();
                String encodedToken = null;
                MessageDigest digest = null;
                try {
                    digest = MessageDigest.getInstance("SHA-256");
                    byte[] hash = digest.digest(tokenOriginal.getBytes(StandardCharsets.UTF_8));
                    encodedToken = Base64.encodeToString(hash, Base64.DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onCompleteListener securityToken: " + tokenOriginal);

                sessionManager.setUserSecurityToken(tokenOriginal);
                sessionManager.setUserSecurityTokenHash(encodedToken);

            }
            }
            // success
            if (SCLoginModel.getErrNum().equals("0")) {
                SessionManager.getInstance(VerifyPhoneScreencopy.this).Islogedin(true);
                String message = "+" + code + phoneNumber.getText().toString();
                SessionManager.getInstance(VerifyPhoneScreencopy.this).setPhoneNumberOfCurrentUser(message);
                if (SCLoginModel.getName() != null && !SCLoginModel.getName().isEmpty()) {
                    SessionManager.getInstance(VerifyPhoneScreencopy.this).setnameOfCurrentUser(new String(Base64.decode(SCLoginModel.getName(), Base64.DEFAULT)));
                }
                SessionManager.getInstance(VerifyPhoneScreencopy.this).setUserCountryCode("+" + code);
                SessionManager.getInstance(VerifyPhoneScreencopy.this).setUserMobileNoWithoutCountryCode(phoneNumber.getText().toString());

                try {
                    JSONObject object = new JSONObject(response);
                    if (SessionManager.getInstance(VerifyPhoneScreencopy.this).getTwilioMode().equalsIgnoreCase(
                            SessionManager.TWILIO_DEV_MODE)) {
                        SessionManager.getInstance(VerifyPhoneScreencopy.this).setLoginOTP(object.getString("code"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (SKIP_OTP_VERIFICATION) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            skipOTP(otp);

                        }
                    }, 500);
                } else {
                    hideProgressDialog();
                    ActivityLauncher.launchPinscreen(VerifyPhoneScreencopy.this, SessionManager.getInstance(VerifyPhoneScreencopy.this).getPhoneNumberOfCurrentUser());
                }
            } else if (SCLoginModel.getErrNum().equals("1")) {
                hideProgressDialog();
                String error = SCLoginModel.getMessage();
                Toast.makeText(VerifyPhoneScreencopy.this, error, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onErrorListener(int state) {
            hideProgressDialog();
            ChatappDialogUtils.showCheckInternetDialog(VerifyPhoneScreencopy.this);

        }
    };

    private void clearDataIfUserChanged(String userId) {
        String prevLoginUserId = sessionManager.getPrevLoginUserId();
        if (!prevLoginUserId.equals("") && !prevLoginUserId.equalsIgnoreCase(userId)) {
            MessageDbController msgDb = CoreController.getDBInstance(VerifyPhoneScreencopy.this);
            msgDb.deleteDatabase();

            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(VerifyPhoneScreencopy.this);
            contactDB_sqlite.deleteDatabase();

            UserInfoSession userInfoSession = new UserInfoSession(VerifyPhoneScreencopy.this);
            userInfoSession.clearData();

            Session session = new Session(VerifyPhoneScreencopy.this);
            session.clearData();

        }
    }

    private void initSocketCallback() {
        mSocketManager = new SocketManager(VerifyPhoneScreencopy.this, new SocketManager.SocketCallBack() {
            @Override
            public void onSuccessListener(String eventName, Object... response) {
                if (eventName.equalsIgnoreCase(SocketManager.EVENT_GET_SETTINGS)) {
                    hideProgressDialog();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initProgress("Loading....", true);
                        }
                    });

                    String data = response[0].toString();

                    try {
                        JSONObject object = new JSONObject(data);
                        String twilioMode = object.getString("twilio");

                        SessionManager sessionManager = SessionManager.getInstance(VerifyPhoneScreencopy.this);
                        sessionManager.setTwilioMode(twilioMode);
                        mSocketManager.disconnect();

                        String contactUsEmailId = object.getString("contactus_email_address");
                        String chatLock = object.getString("chat_lock");
                        String secretChat = object.getString("secret_chat");
                        sessionManager.setContactUsEMailId(contactUsEmailId);
                        sessionManager.setLockChatEnabled(chatLock);
                        sessionManager.setSecretChatEnabled(secretChat);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (eventName.equalsIgnoreCase(Socket.EVENT_CONNECT)) {
                    JSONObject object = new JSONObject();
                    mSocketManager.send(object, SocketManager.EVENT_GET_SETTINGS);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSocketManager.disconnect();
    }


    private void skipOTP(final String OTP) {
        Log.d(TAG, "skipOTP: " + OTP);

        String msisdn = SessionManager.getInstance(this).getPhoneNumberOfCurrentUser();
        String mobileNo = SessionManager.getInstance(this).getUserMobileNoWithoutCountryCode();
        String countryCode = SessionManager.getInstance(this).getUserCountryCode();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("msisdn", msisdn);
        String android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        params.put("DeviceId", msisdn);
        params.put("manufacturer", Build.MANUFACTURER);
        params.put("Version", Build.VERSION.RELEASE);
        params.put("OS", "android");
        params.put("PhNumber", mobileNo);
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

    ServiceRequest.ServiceListener verifyCodeListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            Log.d("Loginrequest", response);

            hideProgressDialog();
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
                SessionManager.getInstance(VerifyPhoneScreencopy.this).IsnumberVerified(true);
                SessionManager.getInstance(VerifyPhoneScreencopy.this).setLoginCount(SCLoginModel.getLoginCount());
                //Log.d("CODE", loginModel.getCode());
                SharedPreferences shPref = getSharedPreferences("global_settings",
                        MODE_PRIVATE);
                SharedPreferences.Editor et = shPref.edit();
                et.putString("userId", SessionManager.getInstance(VerifyPhoneScreencopy.this).getPhoneNumberOfCurrentUser());
                et.apply();
                Session session = new Session(VerifyPhoneScreencopy.this);
                session.putgalleryPrefs("def");
                ActivityLauncher.launchProfileInfoScreen(VerifyPhoneScreencopy.this, SessionManager.getInstance(VerifyPhoneScreencopy.this).getPhoneNumberOfCurrentUser());

            } else {
                Toast.makeText(VerifyPhoneScreencopy.this, "Code mismatch. Please re-enter the code", Toast.LENGTH_LONG).show();
                SharedPreferences shPref = getSharedPreferences("global_settings",
                        MODE_PRIVATE);
                SharedPreferences.Editor et = shPref.edit();
                et.putString("userId", SessionManager.getInstance(VerifyPhoneScreencopy.this).getPhoneNumberOfCurrentUser());
                et.apply();
            }
        }

        @Override
        public void onErrorListener(int state) {
            hideProgressDialog();
        }
    };
}


