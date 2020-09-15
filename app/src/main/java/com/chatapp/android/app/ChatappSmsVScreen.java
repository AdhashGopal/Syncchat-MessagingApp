package com.chatapp.android.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProDemiEditText;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.core.ActivityLauncher;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.model.SCLoginModel;
import com.chatapp.android.core.chatapphelperclass.ChatappDialogUtils;
import com.chatapp.android.core.chatapphelperclass.ChatappSMSReceiver;
import com.chatapp.android.core.service.Constants;
import com.chatapp.android.core.service.ServiceRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicInteger;

/*
* created by  Adhash Team on 10/5/2016.
 */
public class ChatappSmsVScreen extends CoreActivity {

    private AvnNextLTProDemiTextView tv, mobile_number, instructionlabel;
    private ImageButton editNumber;
    private ProgressBar progressBar;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    ImageView back,iv_okbutton;

    AvnNextLTProDemiEditText edit_text;
    ///private TextView timer;
    private AlertDialog.Builder alertDialog;
    private String tag_string_req = "string_req";
    protected static final String TAG = "ChatappSmsVScreen";
    IntentFilter intentFilter;
    private AvnNextLTProDemiTextView textresend;

    AvnNextLTProDemiTextView timer;
    private final AtomicInteger mRefreshRequestCounter = new AtomicInteger(0);
    ChatappSmsVScreen msessionmanager;
    /* Object of the receiver */
    private ChatappSMSReceiver readSms;
    CountDownTimer cTimer = null;

    private SCLoginModel SCLoginModel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_verification_screen);
        SessionManager.getInstance(ChatappSmsVScreen.this).IsnumberVerified(false);
        back = (ImageView) findViewById(R.id.back);
        iv_okbutton = (ImageView) findViewById(R.id.okButton);
        mobile_number = (AvnNextLTProDemiTextView) findViewById(R.id.textView1);
        mobile_number.setText("to  " + SessionManager.getInstance(this).getPhoneNumberOfCurrentUser());
        // editNumber = (ImageButton) findViewById(R.id.editNumber);
        edit_text = (AvnNextLTProDemiEditText) findViewById(R.id.edit_text);
        textresend = (AvnNextLTProDemiTextView) findViewById(R.id.textresend);

        timer = (AvnNextLTProDemiTextView) findViewById(R.id.timer);
        boolean navigateFrom = getIntent().getBooleanExtra("FromVerifyPage", false);

        if (navigateFrom) {
            SessionManager.getInstance(ChatappSmsVScreen.this).setOTPCountDownTime();
        }

        initProgress("Loading...", true);
        displayTimer();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchVerifyPhoneScreen(ChatappSmsVScreen.this);
            }
        });
        textresend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resend();
                //VerifyPhoneNumber.makeStringReq();

            }
        });
        iv_okbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text.getText().toString().length() == 4) {
                    verifyCode(edit_text.getText().toString());
                }else if (edit_text.getText().toString().length() == 0) {
                    Toast.makeText(ChatappSmsVScreen.this,"Please enter the Verification Code",Toast.LENGTH_SHORT).show();
                }

            }
        });

        Session session=new Session(this);
        session.putgalleryPrefs("def");

		/* Initialise the progress dialog */
       /* pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);*/

       /* final SpannableStringBuilder sb = new SpannableStringBuilder(" Wrong NUMBER ?");
        final SpannableStringBuilder sbd = new SpannableStringBuilder(SessionManager.getInstance(ChatappSmsVScreen.this).getPhoneNumberOfCurrentUser());


        final ForegroundColorSpan fcs = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));


        final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
        final RelativeSizeSpan ass = new RelativeSizeSpan(1f);


        sb.setSpan(fcs, 0, sb.length(), Spannable.SPAN_USER);

// make them also bold
        sb.setSpan(bss, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        textresend.setEnabled(false);

        sb.setSpan(ass, 0, sb.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mobile_number.append(sb);*/

        readSms = new ChatappSMSReceiver() {
            @Override
            protected void onSmsReceived(String s) {
                /* Call the verify SMS code API */
                Log.e("SMS Code", s);
           //     edit_text.setText(s);
                verifyCode(s);
            }
        };


//        edit_text.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });

        mobile_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchVerifyPhoneScreen(ChatappSmsVScreen.this);
            }
        });

        intentFilter = new IntentFilter("android.provider.Telephony.SMS_RECEIVED");
        intentFilter.setPriority(1000);


        progressBar = (ProgressBar) findViewById(R.id.progressBar1);
        // @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
        // progressBar.textView_Timer
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 1;
                    // Update the progress bar and display the

                    // current value in the text view
                    handler.post(new Runnable() {
                        public void run() {
                            progressBar.setProgress(progressStatus);
                            //timer.setText(progressStatus + "/"
                            // + progressBar.getMax());
                            // timer.setText(progressStatus+"/"+progressBar.getMax());
                        }
                    });
                    try {
                        // Sleep for 2 seconds.

                        // Just to display the progress slowly
                        Thread.sleep(900);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

      /*  editNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                *//* Move back to verify phone number screen *//*
                ActivityLauncher.launchVerifyPhoneScreen(ChatappSmsVScreen.this);
            }
        });
*/
        // Alert Builder to accept verification code from user
        alertDialog = new AlertDialog.Builder(ChatappSmsVScreen.this);
        alertDialog.setMessage("Enter Verification Code");
        alertDialog.setCancelable(false);
        final EditText input = new EditText(ChatappSmsVScreen.this);
        input.setText("");

        //Auto Verify
        //autoVerify();
        //verifyCode(bundle.getCharSequence("code").toString());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (input.getText().toString().length() == 4) {
                    verifyCode(input.getText().toString());
                }
            }
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setView(input);

        // alertDialog.show();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                try {
                    //alertDialog.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 300 * 3 * 1);

        if (SessionManager.getInstance(ChatappSmsVScreen.this).getTwilioMode().equalsIgnoreCase(
                SessionManager.TWILIO_DEV_MODE)) {
            Log.d(TAG, "onCreate: otp : "+SessionManager.getInstance(ChatappSmsVScreen.this).getLoginOTP());
           // edit_text.setText(SessionManager.getInstance(ChatappSmsVScreen.this).getLoginOTP());
        }
    }

    private void autoVerify() {
        try{
            String code=getIntent().getStringExtra("otp");
            verifyCode(code);
        }
        catch (Exception e){
            Log.e(TAG, "onCreate: ", e);
        }

    }

    private void displayTimer() {
        long smsSentAt = SessionManager.getInstance(this).getOTPCountDownTime();
        long timeDiff = Calendar.getInstance().getTimeInMillis() - smsSentAt;

        long countDownStartFrom;
        if (timeDiff >= 0 && timeDiff <= 60000) {
            countDownStartFrom = 60000 - timeDiff;

            cTimer = new CountDownTimer(countDownStartFrom, 1000) {

                public void onTick(long millisUntilFinished) {
                    textresend.setEnabled(false);
                    timer.setText("" + millisUntilFinished / 1000);
                }

                public void onFinish() {
                    timer.setText("0");
                    textresend.setEnabled(true);
                }
            }.start();
        } else {
            timer.setText("0");
            textresend.setEnabled(true);
        }
    }


    private void resend() {
        showProgressDialog();
        HashMap<String, String> params = new HashMap<String, String>();
        String cCode = SessionManager.getInstance(ChatappSmsVScreen.this).getUserCountryCode();
        String uPhone = SessionManager.getInstance(ChatappSmsVScreen.this).getPhoneNumberOfCurrentUser();
        params.put("msisdn", uPhone);
        String android_id = android.provider.Settings.Secure.getString(getContentResolver(),
                android.provider.Settings.Secure.ANDROID_ID);
        params.put("DeviceId", uPhone);
        params.put("manufacturer", Build.MANUFACTURER);
        params.put("Version", Build.VERSION.RELEASE);
        params.put("OS", "android");
        params.put("PhNumber", SessionManager.getInstance(ChatappSmsVScreen.this).getUserMobileNoWithoutCountryCode());
        params.put("CountryCode", cCode);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String currentDateandTime = sdf.format(new Date());
        params.put("DateTime", currentDateandTime);
        textresend.setEnabled(false);
        ServiceRequest request = new ServiceRequest(this);
        request.makeServiceRequest(Constants.VERIFY_NUMBER_REQUEST, Request.Method.POST, params, resendListener);
    }

    ServiceRequest.ServiceListener resendListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            hideProgressDialog();

            if (SCLoginModel == null)
                SCLoginModel = new SCLoginModel();

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            SCLoginModel = gson.fromJson(response, SCLoginModel.class);

            if(SCLoginModel!=null) {
                Log.d(TAG, "onCompleteListener OTP: " + SCLoginModel.getCode());

            }
            // success
            if (SCLoginModel!=null && SCLoginModel.getErrNum().equals("0")) {
                            /* Move to the SMS verification screen */

                    /* Save the msisdn in shared preferences here */
                SharedPreferences shPref = getSharedPreferences("global_settings",
                        MODE_PRIVATE);
                SharedPreferences.Editor et = shPref.edit();

                try {
                    if (SCLoginModel.getName() != null && !SCLoginModel.getName().contentEquals("")) {
                        byte[] data = Base64.decode(SCLoginModel.getName(), Base64.DEFAULT);
                        String text = new String(data);
                        et.putString("userName", text);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (SCLoginModel.getStatus() != null && !SCLoginModel.getStatus().contentEquals("")) {
                        byte[] data = Base64.decode(SCLoginModel.getStatus(), Base64.DEFAULT);
                        String text = new String(data);
                        et.putString("Status", text);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                et.apply();

                SessionManager.getInstance(ChatappSmsVScreen.this).setOTPCountDownTime();
                displayTimer();

                try {
                    JSONObject object = new JSONObject(response);
                    String code = object.getString("code");
                    SessionManager.getInstance(ChatappSmsVScreen.this).setLoginOTP(code);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                textresend.setEnabled(true);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                        ChatappSmsVScreen.this);
                alertDialog.setTitle("Login failed");
                alertDialog.setMessage("Please verify your mobile number and try again!");

                alertDialog.setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }

           // skipOTP();

        }

        @Override
        public void onErrorListener(int state) {
            hideProgressDialog();
            textresend.setEnabled(true);
            ChatappDialogUtils.showCheckInternetDialog(ChatappSmsVScreen.this);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    /* API to verify the code */
    private void verifyCode(final String s) {
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
        params.put("code", s);
        params.put("callToken", "Android");
        params.put("pushToken", SessionManager.getInstance(this).getCurrentUserID());
        ServiceRequest request = new ServiceRequest(this);
        request.makeServiceRequest(Constants.VERIFY_SMS_CODE, Request.Method.POST, params, verifyCodeListener);

    }

    ServiceRequest.ServiceListener verifyCodeListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            Log.d("Loginrequest",response);

            hideProgressDialog();
            if (SCLoginModel == null)
                SCLoginModel = new SCLoginModel();
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            SCLoginModel = gson.fromJson(response, SCLoginModel.class);
            Log.d(TAG, "onCompleteListener: loginCount: "+SCLoginModel.getLoginCount());
            // success
            if (SCLoginModel.getErrNum().equals("0")) {
                    /* Move to the profile info screen */
//                Toast.makeText(ChatappSmsVScreen.this, "Mobile number verified successfully", Toast.LENGTH_SHORT).show();
                SessionManager.getInstance(ChatappSmsVScreen.this).IsnumberVerified(true);
                SessionManager.getInstance(ChatappSmsVScreen.this).setLoginCount(SCLoginModel.getLoginCount());
                //Log.d("CODE", loginModel.getCode());
                SharedPreferences shPref = getSharedPreferences("global_settings",
                        MODE_PRIVATE);
                SharedPreferences.Editor et = shPref.edit();
                et.putString("userId", SessionManager.getInstance(ChatappSmsVScreen.this).getPhoneNumberOfCurrentUser());
                et.apply();
                ActivityLauncher.launchProfileInfoScreen(ChatappSmsVScreen.this, SessionManager.getInstance(ChatappSmsVScreen.this).getPhoneNumberOfCurrentUser());

            } else {
                Toast.makeText(ChatappSmsVScreen.this, "Code mismatch. Please re-enter the code", Toast.LENGTH_LONG).show();
                SessionManager.getInstance(ChatappSmsVScreen.this).IsnumberVerified(true);
                SessionManager.getInstance(ChatappSmsVScreen.this).setLoginCount(SCLoginModel.getLoginCount());
                SharedPreferences shPref = getSharedPreferences("global_settings",
                        MODE_PRIVATE);
                SharedPreferences.Editor et = shPref.edit();
                et.putString("userId", SessionManager.getInstance(ChatappSmsVScreen.this).getPhoneNumberOfCurrentUser());
                et.apply();
                ActivityLauncher.launchProfileInfoScreen(ChatappSmsVScreen.this, SessionManager.getInstance(ChatappSmsVScreen.this).getPhoneNumberOfCurrentUser());

            }
        }

        @Override
        public void onErrorListener(int state) {
            hideProgressDialog();
        }
    };


}
