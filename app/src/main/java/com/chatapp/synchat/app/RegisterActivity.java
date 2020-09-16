package com.chatapp.synchat.app;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.chatapphelperclass.ChatappDialogUtils;
import com.chatapp.synchat.core.model.SCLoginModel;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.service.ServiceRequest;
import com.chatapp.synchat.core.socket.MessageService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

public class RegisterActivity extends CoreActivity {
    String strNumber, strName, strLastName, strDesignation, strDepartment, strEmail, strContactEmail;
    private SessionManager sessionManager;
    private EditText numberEdt, nameEdt, lastNameEdt, designationEdt, departmentEdt, emailEdt, contactEmailEdt;
    private TextView loginTxt;
    private TextView signUpTxt;
    private ImageView backImg;
    private String android_id;
    private SCLoginModel SCLoginModel;
    String code = "", otp = "";
    private final boolean SKIP_OTP_VERIFICATION = false;


    /**
     * New user register
     */
    private ServiceRequest.ServiceListener signupListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {

            hideProgressDialog();
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();

            try {
                JSONObject json = new JSONObject(response);
                String status = json.getString("Status");


                if (status.equals("1")) {
                   /* if (!MessageService.isStarted()) {
                        startService(new Intent(RegisterActivity.this, MessageService.class));
                    }*/
//                    SessionManager.getInstance(RegisterActivity.this).setCurrentUserID(json.getString("_id"));
//                    showAlert(json.getString("message"));
                    LoginNewCallAPI();
                } else {
//                    showAlert(json.getString("message"));
                    Toast.makeText(RegisterActivity.this, json.getString("Message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorListener(int state) {
            hideProgressDialog();
            ChatappDialogUtils.showCheckInternetDialog(RegisterActivity.this);

        }
    };

    /**
     * based on response moving the screen pin entry or splash screen
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
                    SessionManager.getInstance(RegisterActivity.this).setUserProfilePic(imgPath);
                }

                if (SCLoginModel.getStatus() != null && !SCLoginModel.getStatus().equalsIgnoreCase("")) {
                    SessionManager.getInstance(RegisterActivity.this).setcurrentUserstatus(new String(Base64.decode(SCLoginModel.getStatus(), Base64.DEFAULT)));
                } else {
                    SessionManager.getInstance(RegisterActivity.this).setcurrentUserstatus("Hey there! I am using " + getResources().getString(R.string.app_name));
                }

                // success
                if (SCLoginModel.getErrNum().equals("0")) {

                    SessionManager.getInstance(RegisterActivity.this).setLoginType(SCLoginModel.getLoginType());

                    if (SCLoginModel.get_id() != null) {
                        SessionManager.getInstance(RegisterActivity.this).setCurrentUserID(SCLoginModel.get_id());
//                clearDataIfUserChanged(SCLoginModel.get_id());
                    }
                    SessionManager.getInstance(RegisterActivity.this).Islogedin(true);
                    if (SCLoginModel.getUserEmail() != null) {
                        SessionManager.getInstance(RegisterActivity.this).setCurrentUserEmailID(SCLoginModel.getUserEmail());
                    }
                    SessionManager.getInstance(RegisterActivity.this).setPhoneNumberOfCurrentUser(android_id);
                    if (SCLoginModel.getName() != null && !SCLoginModel.getName().isEmpty()) {
                        Log.d("getName--> IF", SCLoginModel.getName());
                        SessionManager.getInstance(RegisterActivity.this).setnameOfCurrentUser(new String(Base64.decode(SCLoginModel.getName(), Base64.DEFAULT)));
                    } else {
                        Log.d("getName--> ELSE", SCLoginModel.getName());
                    }
                    SessionManager.getInstance(RegisterActivity.this).setUserCountryCode("+" + "");
                    SessionManager.getInstance(RegisterActivity.this).setUserMobileNoWithoutCountryCode(android_id);

                    try {
                        JSONObject object = new JSONObject(response);
                        if (SessionManager.getInstance(RegisterActivity.this).getTwilioMode().equalsIgnoreCase(SessionManager.TWILIO_DEV_MODE)) {
                            SessionManager.getInstance(RegisterActivity.this).setLoginOTP(object.getString("code"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (SKIP_OTP_VERIFICATION) {

                    } else {

                        SharedPreferences shPref = getSharedPreferences("global_settings", MODE_PRIVATE);
                        SharedPreferences.Editor et = shPref.edit();
                        et.putString("userId", SessionManager.getInstance(RegisterActivity.this).getPhoneNumberOfCurrentUser());
                        et.apply();
                        SessionManager.getInstance(RegisterActivity.this).IsnumberVerified(true);
                        SessionManager.getInstance(RegisterActivity.this).setLoginCount(SCLoginModel.getLoginCount());
                       /* ActivityLauncher.launchPinscreen(ADFSWebViewActivity.this, null);
                        finish();*/

                        //new login - 23-7-2020
                        try {
                            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                            notificationManager.cancel(1);
                            SessionManager.getInstance(RegisterActivity.this).setUsermpin("");
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (!SessionManager.getInstance(RegisterActivity.this).getUserMpin().equals("")) {
                                        ActivityLauncher.launchPinscreen(RegisterActivity.this, SessionManager.getInstance(RegisterActivity.this).getPhoneNumberOfCurrentUser(), 0);
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                        finish();
                                    } else {
                                        ActivityLauncher.launchPinscreen(RegisterActivity.this, null, 0);
                                        overridePendingTransition(R.anim.enter, R.anim.exit);
                                        finish();
                                    }
                                }
                            }, 500);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                    }
                    SessionManager.getInstance(RegisterActivity.this).setAdminPendingStatus("");
                } else if (SCLoginModel.getErrNum().equals("1")) {

                    String error = SCLoginModel.getMessage();
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                    SessionManager.getInstance(RegisterActivity.this).setAdminPendingStatus("");
                } else if (SCLoginModel.getErrNum().equals("2")) {
                    if (SCLoginModel.get_id() != null) {
                        SessionManager.getInstance(RegisterActivity.this).setCurrentUserID(SCLoginModel.get_id());
//                clearDataIfUserChanged(SCLoginModel.get_id());
                    }
                    String error = SCLoginModel.getMessage();
                    showAlert(error, 0);
                    SessionManager.getInstance(RegisterActivity.this).setAdminPendingStatus("1");
                } else if (SCLoginModel.getErrNum().equals("3")) {
                    String error = SCLoginModel.getMessage();
                    SessionManager.getInstance(RegisterActivity.this).setLoginType(SCLoginModel.getLoginType());
                    if (SCLoginModel.getUserEmail() != null) {
                        SessionManager.getInstance(RegisterActivity.this).setCurrentUserEmailID(SCLoginModel.getUserEmail());
                    }
                    if (!MessageService.isStarted()) {
                        startService(new Intent(RegisterActivity.this, MessageService.class));
                    }
                    SessionManager.getInstance(RegisterActivity.this).setAdminPendingStatus("1");
                    if (SCLoginModel.get_id() != null) {
                        SessionManager.getInstance(RegisterActivity.this).setCurrentUserID(SCLoginModel.get_id());
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
            if (SessionManager.getInstance(RegisterActivity.this).getLoginFailedStatus().equalsIgnoreCase("")) {
                LoginNewCallAPI();
                SessionManager.getInstance(RegisterActivity.this).setLoginFailedStatus("1");
            } else {
                showCheckInternetDialog(RegisterActivity.this);
            }
        }
    };

    /**
     * shown popup for no internet connection
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
                            LoginNewCallAPI();
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
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
     * updated user login input request
     */
    private void LoginNewCallAPI() {
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

        System.out.println("====verify" + params);
        ServiceRequest request = new ServiceRequest(this);
        request.makeServiceRequest(Constants.VERIFY_NUMBER_REQUEST, Request.Method.POST, params, verifcationListener);
    }

    /**
     * Validation of user input filed
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_new);
        sessionManager = SessionManager.getInstance(RegisterActivity.this);
        numberEdt = findViewById(R.id.edt_number);
        nameEdt = findViewById(R.id.edt_name);
        lastNameEdt = findViewById(R.id.edt_last_name);
        designationEdt = findViewById(R.id.edt_designation);
        departmentEdt = findViewById(R.id.edt_department);
        emailEdt = findViewById(R.id.edt_email);
        contactEmailEdt = findViewById(R.id.edt_ref_email);
        loginTxt = findViewById(R.id.txt_organisation);
        signUpTxt = findViewById(R.id.txt_nonorganisation);
        backImg = findViewById(R.id.img_back);

        contactEmailEdt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if ((keyEvent != null && (keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (i == EditorInfo.IME_ACTION_DONE)) {
                    signUpTxt.performClick();
                }
                return false;
            }
        });

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loginTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchADFSScreen(RegisterActivity.this);
            }
        });

        signUpTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {


                    strNumber = numberEdt.getText().toString();
                    strName = nameEdt.getText().toString();
                    strLastName = lastNameEdt.getText().toString();
                    strDesignation = designationEdt.getText().toString();
                    strDepartment = departmentEdt.getText().toString();
                    strEmail = emailEdt.getText().toString();
                    strContactEmail = contactEmailEdt.getText().toString();
                    if (TextUtils.isEmpty(strNumber)) {
                        numberEdt.requestFocus();
                        numberEdt.setError("Employee ID must not be empty");
                    } else if (TextUtils.isEmpty(strName)) {
                        nameEdt.requestFocus();
                        nameEdt.setError("First Name must not be empty!");
                    } else if (TextUtils.isEmpty(strLastName)) {
                        lastNameEdt.requestFocus();
                        lastNameEdt.setError("Last Name must not be empty!");
                    } else if (TextUtils.isEmpty(strDesignation)) {
                        designationEdt.requestFocus();
                        designationEdt.setError("Designation must not be empty!");
                    } else if (TextUtils.isEmpty(strDepartment)) {
                        departmentEdt.requestFocus();
                        departmentEdt.setError("Department must not be empty!");
                    } else if (TextUtils.isEmpty(strEmail)) {
                        emailEdt.requestFocus();
                        emailEdt.setError("Email address must not be empty!");
                    } else if (!Getcontactname.isEmailValid(strEmail)) {
                        emailEdt.requestFocus();
                        emailEdt.setError("Please enter valid Email address!");
                    } else if (TextUtils.isEmpty(strContactEmail)) {
                        contactEmailEdt.requestFocus();
                        contactEmailEdt.setError("Password must not be empty!");
                    } else if (!Getcontactname.isPasswordValid(strContactEmail)) {
                        contactEmailEdt.requestFocus();
                        contactEmailEdt.setError("Please enter minimum 4 character");
                    } else {
                        registerOrganisationData();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
    }

    /**
     * Getting android UID for the device.
     */
    @Override
    protected void onResume() {
        super.onResume();
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * Non org registration request
     */
    private void registerOrganisationData() {
        showProgressDialog();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("employee_id", strNumber);
        params.put("first_name", strName);
        params.put("last_name", strLastName);
        params.put("designation", strDesignation);
        params.put("department", strDepartment);
        params.put("email", strEmail);
        params.put("password", strContactEmail);

//        params.put("imei_1", sessionManager.getImeiOne());
//        params.put("imei_2", sessionManager.getImeiTwo());

        params.put("msisdn", android_id);
        params.put("os", "A");
        ServiceRequest request = new ServiceRequest(this);
        request.makeServiceRequest(Constants.REGISTRATION, Request.Method.POST, params, signupListener);
        System.out.println("Request params-- " + params);
    }

    private void showAlert(String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this, R.style.MyDialogTheme);
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
     * shown popup for error message
     *
     * @param message getting from response
     * @param type    based on type finished current class or move splash screen
     */
    private void showAlert(String message, final int type) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(RegisterActivity.this, R.style.MyDialogTheme);
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
                    if (type == 0) {
                        finish();
                    } else {
                        ActivityLauncher.launchReloadingActivityScreen(RegisterActivity.this);
                    }

                }
            });

            // Showing Alert Message
            alertDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * finished current class to moved splash screen
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(RegisterActivity.this, ReLoadingActivityNew.class);
        startActivity(intent);
        finish();
    }
}
