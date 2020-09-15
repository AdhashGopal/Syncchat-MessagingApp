package com.chatapp.android.app;

import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.chatapp.android.R;
import com.chatapp.android.core.ActivityLauncher;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * created by  Adhash Team on 10/10/2017.
 */
public class EmailVerifyPendingActivity extends CoreActivity {

    private SocketManager mSocketManager;
    private Handler verifyHandler;
    private Runnable verifyRunnable;

    private final long VERIFICATION_CALLBACK_TIME = 45000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verify_pending);

        initProgress("Verifying...", true);
        initSocketCallback();

        verifyHandler = new Handler();
        verifyRunnable = new Runnable() {
            @Override
            public void run() {
                getUserDetails();
                verifyHandler.postDelayed(this, VERIFICATION_CALLBACK_TIME);
            }
        };
        verifyHandler.post(verifyRunnable);
    }

    public void getUserDetails() {
//        showProgressDialog();
        try {
            JSONObject eventObj = new JSONObject();
            eventObj.put("userId", SessionManager.getInstance(this).getCurrentUserID());

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_GET_USER_DETAILS);
            event.setMessageObject(eventObj);
            EventBus.getDefault().post(event);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    private void initSocketCallback() {
        mSocketManager = new SocketManager(EmailVerifyPendingActivity.this, new SocketManager.SocketCallBack() {
            @Override
            public void onSuccessListener(String eventName, Object... response) {
                if (eventName.equalsIgnoreCase(SocketManager.EVENT_GET_USER_DETAILS)) {
//                    hideProgressDialog();

                    String data = response[0].toString();

                    try {
                        JSONObject object = new JSONObject(data);
                        String userId = object.getString("id");

                        if (userId.equalsIgnoreCase(SessionManager.getInstance(EmailVerifyPendingActivity.this).getCurrentUserID())) {
                            String strVerifyObj = object.getString("email_verification");
                            String verified = "0";

                            JSONObject verifyObj = new JSONObject(strVerifyObj);
                            if (verifyObj.has("status")) {
                                verified = verifyObj.getString("status");
                            }

                            if(verified.equals("1")) {
                                SessionManager.getInstance(EmailVerifyPendingActivity.this).setIsEmailLinkVerified(true);
                                ActivityLauncher.launchProfileInfoScreen(EmailVerifyPendingActivity.this, SessionManager.getInstance(EmailVerifyPendingActivity.this).getPhoneNumberOfCurrentUser());
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        mSocketManager.connect();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        verifyHandler.removeCallbacks(verifyRunnable);
        mSocketManager.disconnect();
    }
}
