package com.chatapp.synchat.app.calls;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.ChatViewActivity;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.utils.BlockUserUtils;
import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.IncomingMessage;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.CallItemChat;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * created by  Adhash Team on 7/31/2017.
 */
public class CallInfoActivity extends CoreActivity implements View.OnClickListener {

    private Toolbar tbCallInfo;
    private ImageButton ibBack, ibCall;
    private ImageView ivProfilePic;
    private TextView tvName;
    private RecyclerView rvCalls;

    public static final String KEY_CALL_ITEM = "CallItem";

    private Gson gson;
    private GsonBuilder gsonBuilder;
    private Session session;

    private ArrayList<CallItemChat> callItemsList = new ArrayList<>();
    private String mCurrentUserId, toUserId, toUserName, toUserMsisdn;
    private int callType;

    public static boolean isKilled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_info);

        initView();
        initData();
    }

    private void initView() {
        tbCallInfo = (Toolbar) findViewById(R.id.tbCallInfo);
        setSupportActionBar(tbCallInfo);

        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ibCall = (ImageButton) findViewById(R.id.ibCall);
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        tvName = (TextView) findViewById(R.id.tvName);

        rvCalls = (RecyclerView) findViewById(R.id.rvCalls);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvCalls.setLayoutManager(manager);

        ibBack.setOnClickListener(this);
        ibCall.setOnClickListener(this);

        initProgress("Loading...", true);
    }

    private void initData() {

        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        session = new Session(this);

        mCurrentUserId = SessionManager.getInstance(this).getCurrentUserID();

        CallItemChat callItem = (CallItemChat) getIntent().getSerializableExtra(KEY_CALL_ITEM);

        Getcontactname getcontactname = new Getcontactname(CallInfoActivity.this);

        toUserId = callItem.getOpponentUserId();
        getcontactname.configProfilepic(ivProfilePic, toUserId, false, false, R.mipmap.chat_attachment_profile_default_image_frame);

        toUserName = getcontactname.getSendername(toUserId, callItem.getOpponentUserMsisdn());
        tvName.setText(toUserName);

        if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
            callType = MessageFactory.video_call;
            ibCall.setImageResource(R.drawable.ic_video_list_call);
        } else {
            callType = MessageFactory.audio_call;
            ibCall.setImageResource(R.drawable.ic_audio_list_call);
        }

        try {
            JSONArray arrCallInfo = new JSONArray(callItem.getCalledAtObj());
            for (int i = 0; i < arrCallInfo.length(); i++) {
                String data = arrCallInfo.getString(i);
                CallItemChat item = gson.fromJson(data, CallItemChat.class);
                callItemsList.add(item);

                toUserMsisdn = item.getOpponentUserMsisdn();
            }
            CallInfoAdapter adapter = new CallInfoAdapter(this, callType, callItemsList);
            rvCalls.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ibBack: {
                finish();
            }
            break;

            case R.id.ibCall: {
                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
                if (contactDB_sqlite.getBlockedStatus(toUserId, false).equals("1")) {
                    String msg = "Unblock " + toUserName + " to place a " +
                            getString(R.string.app_name) + " call";
                    displayAlert(msg);
                } else {
                    performCall();
                }
            }
            break;
        }
    }

    private void performCall() {
        if (checkAudioRecordPermission()) {
            if (!CallMessage.isAlreadyCallClick) {
                CallMessage message = new CallMessage(CallInfoActivity.this);
                JSONObject object = (JSONObject) message.getMessageObject(toUserId, callType);

                if (object != null) {
                    SendMessageEvent callEvent = new SendMessageEvent();
                    callEvent.setEventName(SocketManager.EVENT_CALL);
                    callEvent.setMessageObject(object);
                    EventBus.getDefault().post(callEvent);
                }
                CallMessage.setCallClickTimeout();
            } else {
                Toast.makeText(CallInfoActivity.this, "Call in progress", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void displayAlert(String msg) {
        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setMessage(msg);
        dialog.setNegativeButtonText("Cancel");
        dialog.setPositiveButtonText("Unblock");
        dialog.setCancelable(false);

        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                showProgressDialog();

                BlockUserUtils.changeUserBlockedStatus(CallInfoActivity.this, EventBus.getDefault(),
                        mCurrentUserId, toUserId, false);

                dialog.dismiss();
            }

            @Override

            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getSupportFragmentManager(), "UnBlock a person");

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        switch (event.getEventName()) {

            case SocketManager.EVENT_CALL_RESPONSE:
                loadCallResMessage(event);
                break;

            case SocketManager.EVENT_BLOCK_USER:
                loadBlockEventMessage(event);
                break;

            case SocketManager.EVENT_REMOVE_CALLS:
                clearCallHistory(event);
                break;
        }

    }

    private void loadCallResMessage(ReceviceMessageEvent event) {
        Object[] obj = event.getObjectsArray();
        try {
            JSONObject object = new JSONObject(obj[0].toString());
            JSONObject callObj = object.getJSONObject("data");

            String from = callObj.getString("from");
            String callStatus = callObj.getString("call_status");
            String callConnect = MessageFactory.CALL_IN_FREE + "";
            if (callObj.has("call_connect")) {
                callConnect = callObj.getString("call_connect");
            }

            if (from.equalsIgnoreCase(mCurrentUserId) && callStatus.equals(MessageFactory.CALL_STATUS_CALLING + "")) {
                String to = callObj.getString("to");

                IncomingMessage incomingMsg = new IncomingMessage(CallInfoActivity.this);
                CallItemChat callItem = incomingMsg.loadOutgoingCall(callObj);

                boolean isVideoCall = false;
                if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
                    isVideoCall = true;
                }

                String toUserAvatar = callObj.getString("To_avatar") + "?=id" + Calendar.getInstance().getTimeInMillis();

                MessageDbController db = CoreController.getDBInstance(this);
                db.updateCallLogs(callItem);

                String ts= callObj.getString("timestamp");

                CallMessage.openCallScreen(CallInfoActivity.this, mCurrentUserId, to, callItem.getCallId(),
                        callItem.getRecordId(), toUserAvatar, callItem.getOpponentUserMsisdn(), callConnect,
                        isVideoCall, true, ts);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadBlockEventMessage(ReceviceMessageEvent event) {

        try {
            Object[] obj = event.getObjectsArray();
            JSONObject object = new JSONObject(obj[0].toString());

            String status = object.getString("status");
            String to = object.getString("to");
            String from = object.getString("from");

            if (mCurrentUserId.equalsIgnoreCase(from) && to.equalsIgnoreCase(toUserId)) {
                hideProgressDialog();

                if (status.equalsIgnoreCase("1")) {
                    Toast.makeText(this, "Contact is blocked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Contact is Unblocked", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_call_info, menu);

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);

        if (contactDB_sqlite.getBlockedStatus(toUserId, false).equals("1")) {
            menu.findItem(R.id.menuBlock).setTitle("Unblock");
        } else {
            menu.findItem(R.id.menuBlock).setTitle("Block");
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menuChat: {
                Intent intent = new Intent(CallInfoActivity.this, ChatViewActivity.class);
                intent.putExtra("receiverUid", toUserMsisdn);
                intent.putExtra("receiverName", toUserName);
                intent.putExtra("documentId", toUserId);
                intent.putExtra("Username", toUserName);
                intent.putExtra("Image", "");
                intent.putExtra("type", 0);
                intent.putExtra("msisdn", toUserMsisdn);
                startActivity(intent);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
            break;

            case R.id.menuRemoveCallLog: {
                if (ConnectivityInfo.isInternetConnected(this)) {
                    performRemoveCallLog();
                } else {
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case R.id.menuBlock: {
                if (ConnectivityInfo.isInternetConnected(this)) {
                    performMenuBlock();
                } else {
                    Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
            break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void performRemoveCallLog() {
        showProgressDialog();

        try {
            JSONArray arrCalls = new JSONArray();

            for (int i = 0; i < callItemsList.size(); i++) {
                JSONObject valObj = new JSONObject();
                valObj.put("recordId", callItemsList.get(i).getRecordId());
                valObj.put("docId", callItemsList.get(i).getCallId());
                arrCalls.put(valObj);
            }

            if (arrCalls.length() > 0) {
                JSONObject object = new JSONObject();
                object.put("from", mCurrentUserId);
                object.put("recordIds", arrCalls);

                SendMessageEvent callEvent = new SendMessageEvent();
                callEvent.setEventName(SocketManager.EVENT_REMOVE_CALLS);
                callEvent.setMessageObject(object);
                EventBus.getDefault().post(callEvent);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void clearCallHistory(ReceviceMessageEvent event) {
        String data = event.getObjectsArray()[0].toString();
        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");

            if (from.equalsIgnoreCase(mCurrentUserId)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        hideProgressDialog();

                        Intent intent = new Intent();
                        intent.putExtra("CallLogsRemoved", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }, 1000);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void performMenuBlock() {

        Getcontactname getcontactname = new Getcontactname(this);
        String receiverName = getcontactname.getSendername(toUserId, toUserMsisdn);

        String msg;
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        if (contactDB_sqlite.getBlockedStatus(toUserId, false).equals("1")) {
            msg = "Do you want to Unblock " + receiverName + "?";
        } else {
            msg = "Block " + receiverName + "? Blocked contacts will no longer be able to call you or send you messages.";
        }

        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setMessage(msg);
        dialog.setPositiveButtonText("Ok");
        dialog.setNegativeButtonText("Cancel");
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                dialog.dismiss();

                showProgressDialog();

                BlockUserUtils.changeUserBlockedStatus(CallInfoActivity.this, EventBus.getDefault(),
                        mCurrentUserId, toUserId, false);
            }

            @Override
            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });

        dialog.show(getSupportFragmentManager(), "Block alert");

    }

    @Override
    protected void onResume() {
        super.onResume();
        isKilled = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isKilled = true;
    }
}
