package com.chatapp.synchat.app.calls;

import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.SessionManager;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import static android.Manifest.permission.RECORD_AUDIO;

/**
 * created by  Adhash Team on 10/13/2017.
 */
public class CallPrepareScreen extends CoreActivity {

    private String mCurrentUserId, mReceiverId, mReceiverMsisdn;
    private final int AUDIO_RECORD_PERMISSION_REQUEST_CODE = 14;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);

        if (SessionManager.getInstance(this).isValidDevice()) {
            initProgress("Loading...", true);
            mCurrentUserId = SessionManager.getInstance(this).getCurrentUserID();

            if (getIntent().getData() != null) {
                Cursor cursor = managedQuery(getIntent().getData(), null, null, null, null);
                if (cursor.moveToNext()) {

                    mReceiverId = cursor.getString(cursor.getColumnIndex("DATA4"));
                    boolean isVideoCall = false;

                    if (mReceiverId != null && !mReceiverId.equals("")) {
                        mReceiverMsisdn = cursor.getString(cursor.getColumnIndex("DATA5"));
                    } else {
                        mReceiverId = cursor.getString(cursor.getColumnIndex("DATA7"));
                        mReceiverMsisdn = cursor.getString(cursor.getColumnIndex("DATA8"));
                        isVideoCall = true;
                    }

                    /*Getcontactname getcontactname = new Getcontactname(this);
                    String contactName = getcontactname.getSendername(userId, msisdn);*/
                    if (mReceiverId != null && !mReceiverId.trim().equals("")) {
                        showProgressDialog();
                        performCall(isVideoCall);
                    } else {
                        finish();
                    }
                }
            } else {
                // How did we get here without data?
                finish();
            }
        } else {
            Toast.makeText(this, "Something wrong", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void performCall(boolean isVideoCall) {

        if (ConnectivityInfo.isInternetConnected(this)) {

            if (checkAudioRecordPermission()) {
                if (!CallMessage.isAlreadyCallClick && !IncomingCallActivity.isStarted && !CallsActivity.isStarted) {
                    int callType;
                    if (isVideoCall) {
                        callType = MessageFactory.video_call;
                    } else {
                        callType = MessageFactory.audio_call;
                    }

                    CallMessage message = new CallMessage(this);
                    JSONObject object = (JSONObject) message.getMessageObject(mReceiverId, callType);

                    if (object != null) {
                        SendMessageEvent callEvent = new SendMessageEvent();
                        callEvent.setEventName(SocketManager.EVENT_CALL);
                        callEvent.setMessageObject(object);
                        EventBus.getDefault().post(callEvent);
                    }
                    CallMessage.setCallClickTimeout();
                } else {
                    Toast.makeText(this, "Call in progress", Toast.LENGTH_SHORT).show();
                    finish();
                }

            }
            else {
                requestAudioRecordPermission();
            }
        } else {
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            finish();
        }

    }
    private void requestAudioRecordPermission() {
        ActivityCompat.requestPermissions(CallPrepareScreen.this, new
                String[]{RECORD_AUDIO}, AUDIO_RECORD_PERMISSION_REQUEST_CODE);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CALL_RESPONSE)) {
            loadCallResMessage(event);
        }
    }

    private void loadCallResMessage(ReceviceMessageEvent event) {
        Object[] obj = event.getObjectsArray();
        try {
            JSONObject object = new JSONObject(obj[0].toString());
            String err = object.getString("err");

            JSONObject callObj = object.getJSONObject("data");
            String from = callObj.getString("from");
            String callStatus = callObj.getString("call_status");

            String callConnect = MessageFactory.CALL_IN_FREE + "";
            if (callObj.has("call_connect")) {
                callConnect = callObj.getString("call_connect");
            }

            if (from.equalsIgnoreCase(mCurrentUserId) && callStatus.equals(MessageFactory.CALL_STATUS_CALLING + "")) {
                hideProgressDialog();
                IncomingMessage incomingMsg = new IncomingMessage(this);
                CallItemChat callItem = incomingMsg.loadOutgoingCall(callObj);

                boolean isVideoCall = false;
                if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
                    isVideoCall = true;
                }

                String toUserAvatar = callObj.getString("To_avatar") + "?=id" + Calendar.getInstance().getTimeInMillis();

                MessageDbController db = CoreController.getDBInstance(this);
                db.updateCallLogs(callItem);

                String ts= callObj.getString("timestamp");

                CallMessage.openCallScreen(this, mCurrentUserId, callItem.getOpponentUserId(), callItem.getCallId(),
                        callItem.getRecordId(), toUserAvatar, callItem.getOpponentUserMsisdn(), callConnect,
                        isVideoCall, true, ts);
                finish();
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
