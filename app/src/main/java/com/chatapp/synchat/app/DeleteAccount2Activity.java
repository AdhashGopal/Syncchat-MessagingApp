package com.chatapp.synchat.app;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiButton;
import com.chatapp.synchat.app.widget.AvnNextLTProRegEditText;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.message.ChangeSetController;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * created by  Adhash Team on 4/17/2017.
 */
public class DeleteAccount2Activity extends CoreActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private ImageButton ibBack;
    private Spinner spinReason;
    private AvnNextLTProRegEditText etImproveMsg;
    private AvnNextLTProDemiButton btnSubmit;

    private SessionManager sessionManager;
    private String mCurrentUserId, mCurrentUserMsisdn, strReason;
    private String[] deleteReasons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account_2);

        initView();
        initData();
    }

    private void initView() {


        ibBack = (ImageButton) findViewById(R.id.ibBack);
        spinReason = (Spinner) findViewById(R.id.spinReason);
        etImproveMsg = (AvnNextLTProRegEditText) findViewById(R.id.etImproveMsg);
        btnSubmit = (AvnNextLTProDemiButton) findViewById(R.id.btnSubmit);

        ibBack.setOnClickListener(DeleteAccount2Activity.this);
        btnSubmit.setOnClickListener(DeleteAccount2Activity.this);
        spinReason.setOnItemSelectedListener(DeleteAccount2Activity.this);

        initProgress("Loading...", true);
    }

    private void initData() {
        sessionManager = SessionManager.getInstance(DeleteAccount2Activity.this);

        mCurrentUserId = sessionManager.getCurrentUserID();
        mCurrentUserMsisdn = sessionManager.getPhoneNumberOfCurrentUser();

        deleteReasons = getResources().getStringArray(R.array.delete_ac_reasons);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(DeleteAccount2Activity.this, R.layout.delete_ac_reason_text, deleteReasons);
        spinReason.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_DELETE_ACCOUNT)) {
            try {
                JSONObject object = new JSONObject(event.getObjectsArray()[0].toString());
                String from = object.getString("from");

                if (from.equalsIgnoreCase(mCurrentUserId)) {
                   hideProgressDialog();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ibBack:
                finish();
                break;

            case R.id.btnSubmit:
                if (strReason == null || strReason.equals("") || strReason.equals(deleteReasons[0])) {
                    Toast.makeText(DeleteAccount2Activity.this, "Reason required", Toast.LENGTH_SHORT).show();
                } else {
                    showDeleteWarnAlert();
                }
                break;
        }

    }

    private void showDeleteWarnAlert() {
        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setPositiveButtonText("Process");
        dialog.setNegativeButtonText("Cancel");
        dialog.setMessage(getResources().getString(R.string.delete_ac_warn_msg));

        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                if (ConnectivityInfo.isInternetConnected(DeleteAccount2Activity.this)) {
                    performDeleteAccount();
                } else {
                    Toast.makeText(DeleteAccount2Activity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "Delete account alert");
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        strReason = deleteReasons[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void performDeleteAccount() {

       showProgressDialog();

        String improveMsg = etImproveMsg.getText().toString().trim();

        try {
            ChangeSetController.setChangeStatus("0");

            JSONObject logoutObj = new JSONObject();
            logoutObj.put("from", mCurrentUserId);

            SendMessageEvent logOutEvent = new SendMessageEvent();
            logOutEvent.setEventName(SocketManager.EVENT_MOBILE_TO_WEB_LOGOUT);
            logOutEvent.setMessageObject(logoutObj);
            EventBus.getDefault().post(logOutEvent);


            JSONObject object = new JSONObject();
            object.put("from", mCurrentUserId);
            object.put("msisdn", mCurrentUserMsisdn);
            object.put("reason", strReason);
            object.put("messagetext", improveMsg);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_DELETE_ACCOUNT);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(DeleteAccount2Activity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(DeleteAccount2Activity.this);
    }
}
