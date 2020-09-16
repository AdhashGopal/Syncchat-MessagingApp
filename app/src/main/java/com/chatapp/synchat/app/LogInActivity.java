package com.chatapp.synchat.app;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.model.SCLoginModel;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.service.ServiceRequest;
import com.chatapp.synchat.core.socket.MessageService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import static com.chatapp.synchat.core.chatapphelperclass.ChatappDialogUtils.showCheckInternetDialog;

public class LogInActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;
    private String mEmailId = "";
    private String mPassword = "";
    private String android_id = "";
    private EditText email, password;
    private TextView loginBtn;
    private ImageView backImg;
    private SCLoginModel SCLoginModel;
    private final boolean SKIP_OTP_VERIFICATION = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        email = (EditText) findViewById(R.id.edt_email);
        password = (EditText) findViewById(R.id.edt_password);
        loginBtn = (TextView) findViewById(R.id.txt_login);
        backImg = findViewById(R.id.img_back);

        getDeviceDetails();
        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEmailId = email.getText().toString();
                mPassword = password.getText().toString();

                if (TextUtils.isEmpty(mEmailId)) {
                    email.requestFocus();
                    email.setError("Email address must not be empty!");
                } else if (!Getcontactname.isEmailValid(mEmailId)) {
                    email.requestFocus();
                    email.setError("Please enter valid Email address!");
                } else if (TextUtils.isEmpty(mPassword)) {
                    password.requestFocus();
                    password.setError("Password must not be empty!");
                } else if (!Getcontactname.isPasswordValid(mPassword)) {
                    password.requestFocus();
                    password.setError("Please enter minimum 4 character");
                } else {
                    logIndata();
                }
            }
        });


    }

    private void getDeviceDetails() {
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    private void logIndata() {
        showProgressDialog();
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("msisdn", android_id);
        params.put("email", mEmailId);
        params.put("password", mPassword);
        params.put("manufacturer", Build.MANUFACTURER);
        params.put("OS", "Android");
        params.put("Version", Build.VERSION.RELEASE);
        params.put("DeviceId", android_id);
        ServiceRequest request = new ServiceRequest(this);
        request.makeServiceRequest(Constants.LOGIN, Request.Method.POST, params, loginListener);
        System.out.println("Request params-- " + params);
    }

    /**
     * New user register
     */
    private ServiceRequest.ServiceListener loginListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {

            hideProgressDialog();

            try {
                JSONObject json = new JSONObject(response);
                String status = json.getString("Status");


                if (status.equals("1")) {
                    try {

                        if (SCLoginModel == null)
                            SCLoginModel = new SCLoginModel();
                        GsonBuilder gsonBuilder = new GsonBuilder();
                        Gson gson = gsonBuilder.create();
                        SCLoginModel = gson.fromJson(response, SCLoginModel.class);
                        if (SCLoginModel.getProfilePic() != null) {
                            String imgPath = SCLoginModel.getProfilePic() + "?id=" + Calendar.getInstance().getTimeInMillis();
                            SessionManager.getInstance(LogInActivity.this).setUserProfilePic(imgPath);
                        }

                        if (SCLoginModel.getStatus() != null && !SCLoginModel.getStatus().equalsIgnoreCase("")) {
                            SessionManager.getInstance(LogInActivity.this).setcurrentUserstatus(new String(Base64.decode(SCLoginModel.getStatus(), Base64.DEFAULT)));
                        } else {
                            SessionManager.getInstance(LogInActivity.this).setcurrentUserstatus("Hey there! I am using " + getResources().getString(R.string.app_name));
                        }

                        // success
                        if (SCLoginModel.getErrNum().equals("0")) {

                            SessionManager.getInstance(LogInActivity.this).setLoginType(SCLoginModel.getLoginType());

                            if (SCLoginModel.get_id() != null) {
                                SessionManager.getInstance(LogInActivity.this).setCurrentUserID(SCLoginModel.get_id());
//                clearDataIfUserChanged(SCLoginModel.get_id());
                            }
                            SessionManager.getInstance(LogInActivity.this).Islogedin(true);
                            if (SCLoginModel.getUserEmail() != null) {
                                SessionManager.getInstance(LogInActivity.this).setCurrentUserEmailID(SCLoginModel.getUserEmail());
                            }
                            SessionManager.getInstance(LogInActivity.this).setPhoneNumberOfCurrentUser(android_id);
                            if (SCLoginModel.getName() != null && !SCLoginModel.getName().isEmpty()) {
                                Log.d("getName--> IF", SCLoginModel.getName());
                                SessionManager.getInstance(LogInActivity.this).setnameOfCurrentUser(new String(Base64.decode(SCLoginModel.getName(), Base64.DEFAULT)));
                            } else {
                                Log.d("getName--> ELSE", SCLoginModel.getName());
                            }
                            SessionManager.getInstance(LogInActivity.this).setUserCountryCode("+" + "");
                            SessionManager.getInstance(LogInActivity.this).setUserMobileNoWithoutCountryCode(android_id);

                            try {
                                JSONObject object = new JSONObject(response);
                                if (SessionManager.getInstance(LogInActivity.this).getTwilioMode().equalsIgnoreCase(SessionManager.TWILIO_DEV_MODE)) {
                                    SessionManager.getInstance(LogInActivity.this).setLoginOTP(object.getString("code"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            if (SKIP_OTP_VERIFICATION) {

                            } else {

                                SharedPreferences shPref = getSharedPreferences("global_settings", MODE_PRIVATE);
                                SharedPreferences.Editor et = shPref.edit();
                                et.putString("userId", SessionManager.getInstance(LogInActivity.this).getPhoneNumberOfCurrentUser());
                                et.apply();
                                SessionManager.getInstance(LogInActivity.this).IsnumberVerified(true);
                                SessionManager.getInstance(LogInActivity.this).setLoginCount(SCLoginModel.getLoginCount());
                       /* ActivityLauncher.launchPinscreen(ADFSWebViewActivity.this, null);
                        finish();*/

                                //new login - 23-7-2020
                                try {
                                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                                    notificationManager.cancel(1);
                                    SessionManager.getInstance(LogInActivity.this).setUsermpin("");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!SessionManager.getInstance(LogInActivity.this).getUserMpin().equals("")) {
                                                ActivityLauncher.launchPinscreen(LogInActivity.this, SessionManager.getInstance(LogInActivity.this).getPhoneNumberOfCurrentUser(), 0);
                                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                                finish();
                                            } else {
                                                ActivityLauncher.launchPinscreen(LogInActivity.this, null, 0);
                                                overridePendingTransition(R.anim.enter, R.anim.exit);
                                                finish();
                                            }
                                        }
                                    }, 500);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                            SessionManager.getInstance(LogInActivity.this).setAdminPendingStatus("");
                        } else if (SCLoginModel.getErrNum().equals("1")) {

                            String error = SCLoginModel.getMessage();
                            Toast.makeText(LogInActivity.this, error, Toast.LENGTH_SHORT).show();
                            SessionManager.getInstance(LogInActivity.this).setAdminPendingStatus("");
                        } else if (SCLoginModel.getErrNum().equals("2")) {
                            if (SCLoginModel.get_id() != null) {
                                SessionManager.getInstance(LogInActivity.this).setCurrentUserID(SCLoginModel.get_id());
//                clearDataIfUserChanged(SCLoginModel.get_id());
                            }
                            String error = SCLoginModel.getMessage();
                            showAlert(error, 0);
                            SessionManager.getInstance(LogInActivity.this).setAdminPendingStatus("1");
                        } else if (SCLoginModel.getErrNum().equals("3")) {
                            String error = SCLoginModel.getMessage();
                            SessionManager.getInstance(LogInActivity.this).setLoginType(SCLoginModel.getLoginType());
                            if (SCLoginModel.getUserEmail() != null) {
                                SessionManager.getInstance(LogInActivity.this).setCurrentUserEmailID(SCLoginModel.getUserEmail());
                            }
                            if (!MessageService.isStarted()) {
                                startService(new Intent(LogInActivity.this, MessageService.class));
                            }
                            SessionManager.getInstance(LogInActivity.this).setAdminPendingStatus("1");
                            if (SCLoginModel.get_id() != null) {
                                SessionManager.getInstance(LogInActivity.this).setCurrentUserID(SCLoginModel.get_id());
                            }
                            showAlert(error, 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
//                    showAlert(json.getString("message"));
                    Toast.makeText(LogInActivity.this, json.getString("Message"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorListener(int state) {
            hideProgressDialog();
            if (SessionManager.getInstance(LogInActivity.this).getLoginFailedStatus().equalsIgnoreCase("")) {
                SessionManager.getInstance(LogInActivity.this).setLoginFailedStatus("1");
            } else {
                showCheckInternetDialog(LogInActivity.this);
            }

        }
    };

    /**
     * shown popup for error message
     *
     * @param message getting from response
     * @param type    based on type finished current class or move splash screen
     */
    private void showAlert(String message, final int type) {
        try {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(LogInActivity.this, R.style.MyDialogTheme);
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
                        ActivityLauncher.launchReloadingActivityScreen(LogInActivity.this);
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
     * Shown ProgressDialog
     */
    public void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing() && !isFinishing())
            progressDialog.show();
    }

    /**
     * hide ProgressDialog
     */
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
            progressDialog.dismiss();
    }

    /**
     * binding the progress
     *
     * @param message    getting message from input data
     * @param cancelable based on boolean value progress enable or disable
     */
    public void initProgress(String message, boolean cancelable) {
        progressDialog = getProgressDialogInstance();
        progressDialog.setMessage(message);
        progressDialog.setCancelable(cancelable);
    }

    /**
     * shown ProgressDialog
     *
     * @return response for ProgressDialog
     */
    public ProgressDialog getProgressDialogInstance() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.color_primary_progress_dialog));
        dialog.setIndeterminate(true);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

}

