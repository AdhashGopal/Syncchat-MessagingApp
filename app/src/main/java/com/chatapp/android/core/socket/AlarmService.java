package com.chatapp.android.core.socket;

import android.app.ActivityManager;
import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.chatapp.android.app.calls.CallMessage;
import com.chatapp.android.app.calls.IncomingCallActivity;
import com.chatapp.android.app.utils.ConnectivityInfo;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.SendMessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import static com.chatapp.android.app.calls.IncomingCallActivity.EXTRA_CALL_RECORD_ID;
import static com.chatapp.android.app.calls.IncomingCallActivity.EXTRA_CALL_ROOM_ID;
import static com.chatapp.android.app.calls.IncomingCallActivity.EXTRA_CALL_TIME_STAMP;
import static com.chatapp.android.app.calls.IncomingCallActivity.EXTRA_CALL_TYPE;
import static com.chatapp.android.app.calls.IncomingCallActivity.EXTRA_DOC_ID;
import static com.chatapp.android.app.calls.IncomingCallActivity.EXTRA_FROM_USER_ID;
import static com.chatapp.android.app.calls.IncomingCallActivity.EXTRA_FROM_USER_MSISDN;
import static com.chatapp.android.app.calls.IncomingCallActivity.EXTRA_TO_USER_ID;

/**
 * created by  Adhash Team on 3/21/2018.
 */
public class AlarmService extends IntentService {
    public static final String ACTION1 = "ACTION1";
    public static final String ACTION2 = "ACTION2";
    private static final int NOTIFY_ID = 11;
    private String mCurrentUserId = "";


    /**
     * Whatsapp call function
     * @param intent based on value to make audio / video call
     */
    @Override
    protected void onHandleIntent(Intent intent) {

        try{
            mCurrentUserId = SessionManager.getInstance(getApplicationContext()).getCurrentUserID();
            boolean isServiceRunning = false;
            ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);


            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (IdentifyAppKilled.class.getName().equals(service.service.getClassName())) {
                    isServiceRunning = true;
                    break;
                }
            }
//        Logger.print("AlarmService", "Service is running " + isServiceRunning);
            if (!isServiceRunning) {
                startService(new Intent(getApplicationContext(), IdentifyAppKilled.class));
            }

            final String action = intent.getAction();
            if (ACTION1.equals(action)) {
                Log.e("ACTION", "ACTION1");
                Bundle b = new Bundle();
                b = intent.getExtras();

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                final String mCallId = b.getString(EXTRA_DOC_ID);
                final String fromUserId = b.getString(EXTRA_FROM_USER_ID);
                final String fromUserMsisdn = b.getString(EXTRA_FROM_USER_MSISDN);
                String toUserId = b.getString(EXTRA_TO_USER_ID);
                final String mRoomId = b.getString(EXTRA_CALL_ROOM_ID);
                String mRecordId = b.getString(EXTRA_CALL_RECORD_ID);
                final boolean isVideoCall = b.getBoolean(EXTRA_CALL_TYPE, false);
                final String mCallTS = b.getString(EXTRA_CALL_TIME_STAMP);



                String[] splitIds = mCallId.split("-");
                String id = splitIds[2];
                String callDocId = fromUserId + "-" + mCurrentUserId + "-" + id;

                JSONObject object = CallMessage.getCallStatusObject(mCurrentUserId, fromUserId,
                        id, callDocId, mRecordId, MessageFactory.CALL_STATUS_ANSWERED,isVideoCall?"1":"0");

                Log.e("==calllog", "==call answered from opponent" + object);

                MessageDbController db = CoreController.getDBInstance(this);
                db.updateCallStatus(mCallId, MessageFactory.CALL_STATUS_ANSWERED, "00:00");

                SendMessageEvent event = new SendMessageEvent();
                event.setEventName(SocketManager.EVENT_CALL_STATUS);
                event.setMessageObject(object);
                EventBus.getDefault().post(event);
                CallMessage.openCallScreen(getApplicationContext(), mCurrentUserId, fromUserId, mCallId,
                        mRoomId, "", fromUserMsisdn, MessageFactory.CALL_IN_FREE + "",
                        isVideoCall, false, mCallTS);

                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(NOTIFY_ID);
                if (notificationManager != null) {
                    notificationManager.cancelAll();
                }

            } else if (ACTION2.equals(action)) {
                Bundle b = new Bundle();
                b = intent.getExtras();

                String mCallId = b.getString(EXTRA_DOC_ID);
                String fromUserId = b.getString(EXTRA_FROM_USER_ID);
                String fromUserMsisdn = b.getString(EXTRA_FROM_USER_MSISDN);
                String toUserId = b.getString(EXTRA_TO_USER_ID);
                String mRoomId = b.getString(EXTRA_CALL_ROOM_ID);
                String mRecordId = b.getString(EXTRA_CALL_RECORD_ID);
                boolean isVideoCall = b.getBoolean(EXTRA_CALL_TYPE, false);
                String mCallTS = b.getString(EXTRA_CALL_TIME_STAMP);


                String[] splitIds = mCallId.split("-");
                String id = splitIds[2];
                String callDocId = fromUserId + "-" + mCurrentUserId + "-" + id;
                //changed for iOS
                int callStatus = MessageFactory.CALL_STATUS_END;
                if (!ConnectivityInfo.isInternetConnected(getApplicationContext())) {
                    callStatus = MessageFactory.CALL_STATUS_MISSED;
                }

                JSONObject object = CallMessage.getCallStatusObject(mCurrentUserId, fromUserId, id,
                        callDocId, mRecordId, callStatus,isVideoCall?"1":"0");

                Log.e("==calllog", "==call disconnect from opponent" + object);

                MessageDbController db = CoreController.getDBInstance(this);
                db.updateCallStatus(mCallId, callStatus, "00:00");

                SendMessageEvent event = new SendMessageEvent();
                event.setEventName(SocketManager.EVENT_CALL_STATUS);
                event.setMessageObject(object);
                EventBus.getDefault().post(event);


                NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancel(NOTIFY_ID);
                if (notificationManager != null) {
                    notificationManager.cancelAll();
                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }


    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public AlarmService() {
        super("Alarm");
    }
}
