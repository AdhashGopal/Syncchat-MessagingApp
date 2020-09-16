package com.chatapp.synchat.app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.dialog.ChatLockAlertDialog;
import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiButton;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiEditText;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * created by  Adhash Team on 5/2/2017.
 */
public class EmailSettings extends CoreActivity implements View.OnClickListener {
    RelativeLayout relativeLayout;
    String uniqueCurrentID, specifiedEmail, recoveryEmail, recoveryPhone;
    ImageView close, okIconEmail, okIconRecoveryEmail, okIconRecoveryPhone;
    AvnNextLTProDemiEditText email_et, recovery_email_et, recoveryPhone_et;
    AvnNextLTProDemiButton button_next;
    CheckBox checkBox_chatLock;
//    String emailfromresponse, phonefromresponse, recemailfromresponse;
//    String emailChatlock = "", recemailChatlock = "", recPhoneChatlock = "";

    private SessionManager sessionManager;
    public Getcontactname getcontactname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_lock_dialog);

        relativeLayout = (RelativeLayout) findViewById(R.id.chat_lock_dialog_layout);

        sessionManager = SessionManager.getInstance(EmailSettings.this);
        uniqueCurrentID = sessionManager.getCurrentUserID();
        getcontactname = new Getcontactname(EmailSettings.this);
        Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();

        close = (ImageView) findViewById(R.id.chatLockclose);
        email_et = (AvnNextLTProDemiEditText) findViewById(R.id.email_Et);
        checkBox_chatLock = (CheckBox) findViewById(R.id.checkBox_chatLock);
        button_next = (AvnNextLTProDemiButton) findViewById(R.id.button_ok);
        button_next.setOnClickListener(EmailSettings.this);
        recovery_email_et = (AvnNextLTProDemiEditText) findViewById(R.id.recoveryMail_Et);
        recoveryPhone_et = (AvnNextLTProDemiEditText) findViewById(R.id.recoveryphone_Et);
        okIconEmail = (ImageView) findViewById(R.id.okIconEmail);
        okIconEmail.setOnClickListener(EmailSettings.this);
        okIconRecoveryPhone = (ImageView) findViewById(R.id.okIconRecoveryPhone);
        okIconRecoveryPhone.setOnClickListener(EmailSettings.this);
        okIconRecoveryEmail = (ImageView) findViewById(R.id.okIconRecoveryEmail);
        okIconRecoveryEmail.setOnClickListener(EmailSettings.this);
        close.setOnClickListener(this);
        checkBox_chatLock.setTypeface(face);

        String emailChatlock = sessionManager.getUserEmailId();
        String recemailChatlock = sessionManager.getRecoveryEMailId();
        String recPhoneChatlock = sessionManager.getRecoveryPhoneNo();
        String verifyStatus = sessionManager.getChatLockEmailIdVerifyStatus();

        if (!emailChatlock.equals("")) {
            email_et.setText(emailChatlock);
            email_et.setKeyListener(null);
            okIconEmail.setVisibility(View.GONE);
        }

        if (!recemailChatlock.equals("")) {
            recovery_email_et.setText(recemailChatlock);
            recovery_email_et.setKeyListener(null);
            okIconRecoveryEmail.setVisibility(View.GONE);
        }

        if (!recPhoneChatlock.equals("")) {
            recoveryPhone_et.setText(recPhoneChatlock);
            recoveryPhone_et.setKeyListener(null);
            okIconRecoveryPhone.setVisibility(View.GONE);
        }

        if (verifyStatus.equalsIgnoreCase("yes")) {
            checkBox_chatLock.setChecked(true);
            checkBox_chatLock.setEnabled(false);
        } else {
            checkBox_chatLock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (checkBox_chatLock.isChecked()) {
                        updateChatLockemailVerificationdata();
                    }
                }
            });
        }

        if (!emailChatlock.equals("") && !recemailChatlock.equals("") && !recPhoneChatlock.equals("")) {
            button_next.setText(getResources().getString(R.string.change_email));
        }

        initProgress("Loading...", true);
    }

    private void updateChatLockemailVerificationdata() {

        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_VERIFY_EMAIL);
        try {
            JSONObject obj = new JSONObject();
            obj.put("from", uniqueCurrentID);
            obj.put("verify_email", "yes");
            messageEvent.setMessageObject(obj);
            EventBus.getDefault().post(messageEvent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private void loadEmailID() {
        if (ConnectivityInfo.isInternetConnected(EmailSettings.this)) {
            showProgressDialog();

            SendMessageEvent messageEvent = new SendMessageEvent();
            messageEvent.setEventName(SocketManager.EVENT_CHANGE_EMAIL);
            try {
                JSONObject obj = new JSONObject();
                obj.put("from", uniqueCurrentID);
                obj.put("email", specifiedEmail);
                messageEvent.setMessageObject(obj);
                EventBus.getDefault().post(messageEvent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Check your network connection", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadRecoveryPhone() {
        if (ConnectivityInfo.isInternetConnected(this)) {
            showProgressDialog();

            SendMessageEvent messageEvent = new SendMessageEvent();
            messageEvent.setEventName(SocketManager.EVENT_RECOVERY_PHONE);
            try {
                JSONObject obj = new JSONObject();
                obj.put("from", uniqueCurrentID);
                obj.put("recovery_phone", recoveryPhone);
                messageEvent.setMessageObject(obj);
                EventBus.getDefault().post(messageEvent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Check your network connection", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadRecoveryEmail() {

        if (ConnectivityInfo.isInternetConnected(this)) {
            showProgressDialog();

            SendMessageEvent messageEvent = new SendMessageEvent();
            messageEvent.setEventName(SocketManager.EVENT_RECOVERY_EMAIL);
            try {
                JSONObject obj = new JSONObject();
                obj.put("from", uniqueCurrentID);
                obj.put("recovery_email", recoveryEmail);
                messageEvent.setMessageObject(obj);
                EventBus.getDefault().post(messageEvent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Check your network connection", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.chatLockclose:
                finish();
                break;


            case R.id.okIconEmail:
                specifiedEmail = email_et.getText().toString().trim();

                if (specifiedEmail.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email address must not be empty!", Toast.LENGTH_SHORT).show();
                } else if (!Getcontactname.isEmailValid(specifiedEmail)) {
                    Toast.makeText(getApplicationContext(), "Please enter valid Email address!", Toast.LENGTH_SHORT).show();
                } else if (specifiedEmail.equalsIgnoreCase(recoveryEmail)) {
                    Toast.makeText(getApplicationContext(), "Email & recovery email must not be same", Toast.LENGTH_SHORT).show();
                } else {
                    loadEmailID();
                }
                break;

            case R.id.okIconRecoveryPhone:
                recoveryPhone = recoveryPhone_et.getText().toString().trim();
                if (recoveryPhone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Phone number must not be empty!", Toast.LENGTH_SHORT).show();
                } else {
                    loadRecoveryPhone();
                }
                break;


            case R.id.okIconRecoveryEmail:
                recoveryEmail = recovery_email_et.getText().toString().trim();

                if (recoveryEmail.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Email address must not be empty!", Toast.LENGTH_SHORT).show();
                } else if (!Getcontactname.isEmailValid(recoveryEmail)) {
                    Toast.makeText(getApplicationContext(), "Please enter valid Email address!", Toast.LENGTH_SHORT).show();
                } else if (recoveryEmail.equalsIgnoreCase(specifiedEmail)) {
                    Toast.makeText(getApplicationContext(), "Email & recovery email must not be same", Toast.LENGTH_SHORT).show();
                }  else {
                    loadRecoveryEmail();
                }

                break;

            case R.id.button_ok:
                specifiedEmail = email_et.getText().toString().trim();
                recoveryPhone = recoveryPhone_et.getText().toString().trim();
                recoveryEmail = recovery_email_et.getText().toString().trim();

                if (specifiedEmail.equals("") || recoveryPhone.equals("") || recoveryEmail.equals("")) {
                    Toast.makeText(EmailSettings.this, "Field must not be empty", Toast.LENGTH_SHORT).show();
                } else if (!checkBox_chatLock.isChecked()) {
                    Toast.makeText(EmailSettings.this, "Please select verify settings", Toast.LENGTH_SHORT).show();
                } else if (!sessionManager.getUserEmailId().equals("")
                        && !sessionManager.getRecoveryEMailId().equals("")
                        && !sessionManager.getRecoveryPhoneNo().equals("")) {
                    finish();
                } else {
                    //Toast.makeText(EmailSettings.this, "save entered data", Toast.LENGTH_SHORT).show();

                    final String msg = "Save entered data by clicking ";

                    final ChatLockAlertDialog dialog = new ChatLockAlertDialog();
                    dialog.setNegativeButtonText("Ok");
                    dialog.setPositiveButtonText("Cancel");
                    dialog.setTitle(msg);
                    dialog.setImagedrawable("set");
                    dialog.setCustomDialogCloseListener(new ChatLockAlertDialog.OnCustomDialogCloseListener() {
                        @Override
                        public void onPositiveButtonClick() {
                            dialog.dismiss();
                        }

                        @Override
                        public void onNegativeButtonClick() {
                            dialog.dismiss();
                        }
                    });

                    dialog.show(getSupportFragmentManager(), "Alert Chatlock");

                }
                break;
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_RECOVERY_EMAIL)) {
            loadRecoveryEmailMessage(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CHANGE_EMAIL)) {
            responseforeventchangemail(event);
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_RECOVERY_PHONE)) {
            responseforeventRecoveryPhone(event);
        }
    }


    private void responseforeventchangemail(ReceviceMessageEvent event) {

        Object[] array = event.getObjectsArray();
        try {
            JSONObject objects = new JSONObject(array[0].toString());
            String err = objects.getString("err");
            if (err.equals("0")) {
                String from = objects.getString("from");

                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    hideProgressDialog();
                    okIconEmail.setEnabled(false);

                    Toast.makeText(EmailSettings.this, "Email address added successfully", Toast.LENGTH_SHORT).show();
                    okIconEmail.setImageResource(R.drawable.ic_double_tick_26);
                    recovery_email_et.requestFocus();
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadRecoveryEmailMessage(ReceviceMessageEvent event) {

        Object[] array = event.getObjectsArray();
        try {
            JSONObject objects = new JSONObject(array[0].toString());
            String err = objects.getString("err");
            if (err.equals("0")) {
                String from = objects.getString("from");

                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    hideProgressDialog();
                    okIconRecoveryEmail.setEnabled(false);

                    Toast.makeText(getApplicationContext(), "Recovery  email address added successfully", Toast.LENGTH_SHORT).show();
                    okIconRecoveryEmail.setImageResource(R.drawable.ic_double_tick_26);
                    recoveryPhone_et.requestFocus();
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void responseforeventRecoveryPhone(ReceviceMessageEvent event) {

        Object[] array = event.getObjectsArray();
        try {
            JSONObject objects = new JSONObject(array[0].toString());
            String err = objects.getString("err");
            if (err.equals("0")) {
                String from = objects.getString("from");

                if (from.equalsIgnoreCase(uniqueCurrentID)) {
                    hideProgressDialog();
                    okIconRecoveryPhone.setEnabled(false);

                    Toast.makeText(getApplicationContext(), "Recovery  mobile number added successfully", Toast.LENGTH_SHORT).show();
                    okIconRecoveryPhone.setImageResource(R.drawable.ic_double_tick_26);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

}

