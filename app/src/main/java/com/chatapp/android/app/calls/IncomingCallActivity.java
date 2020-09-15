package com.chatapp.android.app.calls;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chatapp.android.R;
import com.chatapp.android.app.utils.ConnectivityInfo;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.PreferenceConnector;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import static android.Manifest.permission.RECORD_AUDIO;

/**
 * created by  Adhash Team on 7/14/2017.
 */
public class IncomingCallActivity extends CoreActivity {

    private TextView tvName, tvCallLbl;
    private ImageButton ibAnswer, ibReject;
    private ImageView ivProfilePic;

    public static final String EXTRA_DOC_ID = "DocId";
    public static final String EXTRA_FROM_USER_ID = "FromUserId";
    public static final String EXTRA_TO_USER_ID = "ToUserId";
    public static final String EXTRA_FROM_USER_MSISDN = "FromUserMsisdn";
    public static final String EXTRA_CALL_ROOM_ID = "RoomId";
    public static final String EXTRA_CALL_RECORD_ID = "recordId";
    public static final String EXTRA_CALL_TYPE = "CallType";
    public static final String EXTRA_CALL_TIME_STAMP = "CallTimeStamp";

    private final int AUDIO_RECORD_PERMISSION_REQUEST_CODE = 1;

    private String mCurrentUserId, mCallId, mRoomId, fromUserId, toUserId, fromUserMsisdn,
            profilePic, mCallTS;
    private boolean isVideoCall, resultAudioRecordPermission;
    private Ringtone ringtone;
    private Vibrator vibrator;
    private int screenWidth;
    private float rejectBtnStart, answerBtnEnd;
    private double maxWidthAnsBtn, minWidthRejectBtn, minEnableAns, minEnableReject;
    private Handler callTimeoutHandler, broadcastHandler;
    private Runnable callTimeoutRunnable, broadcastRunnable;
    public static boolean isStarted;
    public static boolean isSlow;

    //----------------------------------New Design------------------------

    private TextView call_status;
    private CircleImageView circle_profile_image;
    private TextView caller_name;
    private TextView caller_status;
    private RelativeLayout accept_layout;
    private RelativeLayout disconnect_layout;
    private String mRecordId = "";
    double minnetspeed = 2.0;
    double netkilobyte = 0.0;
    double netspeed = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                +WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                +WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_incoming_call);
        System.out.println("==here INcomingCallsACtivity");
        init();
    }

    /**
     * binding Widgets
     */
    private void init() {
        isStarted = true;
        EventBus.getDefault().register(this);

        tvName = (TextView) findViewById(R.id.tvName);
        tvCallLbl = (TextView) findViewById(R.id.tvCallLbl);
        ibAnswer = (ImageButton) findViewById(R.id.ibAnswer);
        ibReject = (ImageButton) findViewById(R.id.ibReject);
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);

        call_status = (TextView) findViewById(R.id.call_status);
        caller_name = (TextView) findViewById(R.id.caller_name);
        caller_status = (TextView) findViewById(R.id.caller_status);

        ConnectivityManager connManager;
        connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mWifi.isConnected()) {
            //wifi connected
            netkilobyte = TrafficStats.getTotalRxBytes() / 1024;
            //  Toast.makeText(IncomingCallActivity.this, "wifi" , Toast.LENGTH_SHORT).show();
        } else if (mMobile.isConnected()) {
            //mobile connected
            netkilobyte = TrafficStats.getMobileRxBytes() / 1024;
            //  Toast.makeText(IncomingCallActivity.this, "mobile" , Toast.LENGTH_SHORT).show();

        }
        try {
            NotificationManager notificationCallManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationCallManager != null) {
                notificationCallManager.cancel(555);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        double netmbps = (netkilobyte / 1024);

        netspeed = (netmbps / 1000);

        int a = (int) Math.round(netspeed);

        PreferenceConnector.writeInteger(IncomingCallActivity.this, PreferenceConnector.NETVALUE, a);

       /* if(netspeed<minnetspeed){
            call_status.setText("INCOMING POOR CONNECTION");
            PreferenceConnector.writeLong(IncomingCallActivity.this,PreferenceConnector.NETVALUE,netspeed);
        }else {
            call_status.setText("INCOMING");
        }*/


        circle_profile_image = (CircleImageView) findViewById(R.id.circle_profile_image);

        accept_layout = (RelativeLayout) findViewById(R.id.accept_layout);
        disconnect_layout = (RelativeLayout) findViewById(R.id.disconnect_layout);


        mCurrentUserId = SessionManager.getInstance(IncomingCallActivity.this).getCurrentUserID();

        mCallId = getIntent().getStringExtra(EXTRA_DOC_ID);
        fromUserId = getIntent().getStringExtra(EXTRA_FROM_USER_ID);
        fromUserMsisdn = getIntent().getStringExtra(EXTRA_FROM_USER_MSISDN);
        toUserId = getIntent().getStringExtra(EXTRA_TO_USER_ID);
        mRoomId = getIntent().getStringExtra(EXTRA_CALL_ROOM_ID);
        mRecordId = getIntent().getStringExtra(EXTRA_CALL_RECORD_ID);
        System.out.println("Record ID 1 --->" + mRecordId);
        isVideoCall = getIntent().getBooleanExtra(EXTRA_CALL_TYPE, false);
        mCallTS = getIntent().getStringExtra(EXTRA_CALL_TIME_STAMP);

        Getcontactname getcontactname = new Getcontactname(this);
        String name;
        if (fromUserId.equalsIgnoreCase(mCurrentUserId)) {
            name = getcontactname.getSendername(toUserId, fromUserMsisdn);
        } else {
            name = getcontactname.getSendername(fromUserId, fromUserMsisdn);
        }
        //tvName.setText(name);
        caller_name.setText(name);

//        getcontactname.configProfilepic(circle_profile_image, fromUserId, false, false, R.drawable.personprofile);
        getcontactname.configProfilepic(circle_profile_image, fromUserId, false, false, R.drawable.profile_image_small);


//        getcontactname.configProfilepic(ivProfilePic, fromUserId, false, false, R.drawable.call_place_holder_icon);
        getcontactname.configProfilepic(ivProfilePic, fromUserId, false, false, R.drawable.profile_image_outgoing);


        if (isVideoCall) {
            call_status.setText("aChat VIDEO CALL");
        } else {
            call_status.setText("aChat VOICE CALL");
        }

        playRingtone();
        if (!canRecordAudio()) {
            requestAudioRecordPermission();
        }

        // Device width
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        maxWidthAnsBtn = screenWidth * 0.75;
        minWidthRejectBtn = screenWidth * 0.25;
        minEnableAns = screenWidth * 0.35;
        minEnableReject = screenWidth * 0.4;

//        ibAnswer.setOnTouchListener(answerButtonTouchListener);
//        ibReject.setOnTouchListener(rejectButtonTouchListener);

        //--------------------------------------New Code--------------------------------------

        disconnect_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectCall();
            }
        });

        accept_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                answerCall();

            }
        });

        //--------------------------------------New Code--------------------------------------

        ibAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                answerCall();


            }
        });

        ibReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rejectCall();

            }
        });

        ibAnswer.post(new Runnable() {
            @Override
            public void run() {
                answerBtnEnd = ibAnswer.getX() + ibAnswer.getWidth() + 20;
            }
        });

        ibReject.post(new Runnable() {
            @Override
            public void run() {
                rejectBtnStart = ibReject.getX() - ibAnswer.getWidth() - 20;
            }
        });

        callTimeoutHandler = new Handler();
        callTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if (IncomingCallActivity.isStarted) {
                    System.out.println("====here in timeout");
                    finish();
                }
            }
        };
        callTimeoutHandler.postDelayed(callTimeoutRunnable, CallsActivity.MISSED_CALL_TIMEOUT);
    }

    /**
     * playRingtone mode (silent, vibrate and normal)
     */
    private void playRingtone() {
        AudioManager am = (AudioManager) getSystemService(AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:

                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                long[] pattern = {0, 1000, 1500}; // start, vibrate duration, sleep duration
                vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                vibrator.vibrate(pattern, 0);
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                try {
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    ringtone = RingtoneManager.getRingtone(IncomingCallActivity.this, notification);
                    ringtone.play();
                } catch (Exception e) {
                    Log.d("RingtoneException", e.toString());
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * get value from Eventbus
     *
     * @param event based on the value call socket (attend,reject,status)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {

        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CALL_STATUS)
                || event.getEventName().equalsIgnoreCase(SocketManager.EVENT_DISCONNECT_CALL)) {
            String data = event.getObjectsArray()[0].toString();

            System.out.println("=====data" + data);

            try {
                JSONObject object = new JSONObject(data);
                String recordId = object.getString("recordId");
                System.out.println("Record ID 2 --->" + recordId);


                String callStatus = object.getString("call_status");
                System.out.println("=====data222" + callStatus);
                // Close activity if sender tap end button before receiver pick call
                Log.e("==calllog", "==incomingcallactivityty" + object);
                if (callStatus.equalsIgnoreCase(String.valueOf(MessageFactory.CALL_STATUS_REJECTED))) {
                    System.out.println("=====hereinif" + callStatus);
                    finish();
                }
               /*if(!object.has("device_type") && callStatus== MessageFactory.CALL_STATUS_REJECTED+"") {
                   callStatus ="2";
               }*/

                if (mRecordId.isEmpty())
                    mRecordId = recordId;

                if (recordId.equalsIgnoreCase(mRecordId)) {
                    switch (callStatus) {
                        case MessageFactory.CALL_STATUS_END + "":

                            break;
                        case MessageFactory.CALL_STATUS_REJECTED + "":

//                            finish();

                            break;
                        case MessageFactory.CALL_STATUS_RECEIVED + "":
                            break;
                        case MessageFactory.CALL_STATUS_MISSED + "":
                            String from = object.getString("from");
                            if (!from.equalsIgnoreCase(mCurrentUserId)) {
                                MessageDbController db = CoreController.getDBInstance(this);
                                db.updateCallStatus(mCallId, MessageFactory.CALL_STATUS_REJECTED, "00:00");
                            }
                            finish();
                            break;

                    }
                }
                if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_DISCONNECT_CALL)) {
                    System.out.println("====eventdiscioonnce" + event.getEventName());
                    System.out.println("===here diconnect heere");
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CALL_RESPONSE)) {
            System.out.println("====event" + event.getEventName());
            String data = event.getObjectsArray()[0].toString();
            System.out.println("====event" + data);
            Log.d("Call_response", data);
            calldisconnect(data);
            Log.e("==calllog", "==incomingcallactivityty" + data);
        }
    }

    @Override
    public void onBackPressed() {

    }


    /**
     * Stop the current activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.out.println("====here in ondestroy");
        EventBus.getDefault().unregister(this);
        stopRingtone();
        isStarted = false;

        if (broadcastRunnable != null) {
            broadcastHandler.removeCallbacks(broadcastRunnable);
        }
    }

    /**
     * Stop Ringtone
     */
    private void stopRingtone() {
        if (ringtone != null && ringtone.isPlaying()) {
            ringtone.stop();
        }

        if (vibrator != null) {
            vibrator.cancel();
        }
    }

    /**
     * Call disconnect
     *
     * @param data get response from server
     */
    private void calldisconnect(String data) {
        try {
            System.out.println("====here in discuonnecr");
            JSONObject object = new JSONObject(data);
            JSONObject dataObj = object.getJSONObject("data");
            mRecordId = dataObj.getString("recordId");
            /*String call_status=object.getString("call_status");
            if( call_status.equals(""+MessageFactory.CALL_STATUS_REJECTED) || call_status.equals(""+MessageFactory.CALL_STATUS_END) ){
                finish();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * check permission
     *
     * @return value
     */
    public boolean canRecordAudio() {
        int recordPermission = ContextCompat.checkSelfPermission(this, RECORD_AUDIO);
        return recordPermission == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Request Audio permission
     */
    private void requestAudioRecordPermission() {
        ActivityCompat.requestPermissions(this, new
                String[]{RECORD_AUDIO}, AUDIO_RECORD_PERMISSION_REQUEST_CODE);
    }

    /**
     * based on permisson get the value
     *
     * @param requestCode  get permission requestCode
     * @param permissions  get permissions state
     * @param grantResults get permission grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AUDIO_RECORD_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    resultAudioRecordPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                }
                break;
        }
    }

    /**
     * based on key event to perform the action
     * @param event key event action type
     * @return value
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                    stopRingtone();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                    stopRingtone();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    private View.OnTouchListener answerButtonTouchListener = new View.OnTouchListener() {
        float mPrevX;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if (ConnectivityInfo.isInternetConnected(IncomingCallActivity.this)) {
                float currX;
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN: {
                        mPrevX = event.getX();
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        if (ibReject.getVisibility() == View.VISIBLE) {
                            ibReject.setVisibility(View.INVISIBLE);
                        }

                        currX = event.getRawX();
                        int position = (int) (currX - mPrevX);

                        if (position > 0 && position < maxWidthAnsBtn && position < rejectBtnStart) {
                            Log.d("Incom_AnsDrag", position + "");
                            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
                            marginParams.setMargins(position, (int) view.getY(), 0, 0);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                            view.setLayoutParams(layoutParams);
                        }
                        break;
                    }

                    case MotionEvent.ACTION_CANCEL:
                        break;

                    case MotionEvent.ACTION_UP:
                        ibReject.setVisibility(View.VISIBLE);
                        float position = view.getX();

                        if (position >= minEnableAns) {
                            answerCall();
                        } else {
                            ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
                            marginParams.setMargins(0, (int) view.getY(), 0, 0);
                            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                            view.setLayoutParams(layoutParams);
                        }
                        break;
                }
            }

            return true;
        }
    };

    private View.OnTouchListener rejectButtonTouchListener = new View.OnTouchListener() {
        float mPrevX;

        @Override
        public boolean onTouch(View view, MotionEvent event) {
            float currX;
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN: {
                    mPrevX = event.getX();
                    break;
                }

                case MotionEvent.ACTION_MOVE: {
                    if (ibAnswer.getVisibility() == View.VISIBLE) {
                        ibAnswer.setVisibility(View.INVISIBLE);
                    }

                    currX = event.getRawX();

                    int position = (int) (currX - mPrevX);

                    if (position > minWidthRejectBtn && position < screenWidth && position > answerBtnEnd) {
                        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
                        marginParams.setMargins(position, (int) view.getY(), 0, 0);
                        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(marginParams);
                        view.setLayoutParams(layoutParams);
                    }
                    break;
                }

                case MotionEvent.ACTION_CANCEL:
                    break;

                case MotionEvent.ACTION_UP:
                    ibAnswer.setVisibility(View.VISIBLE);
                    float position = view.getX();

                    if (position <= minEnableReject) {
                        rejectCall();
                    } else {
                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) view.getLayoutParams();
                        params.setMargins(10, 10, 10, 10);
                        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

                        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
                            params.addRule(RelativeLayout.ALIGN_PARENT_END);
                        }

                        view.setLayoutParams(params);
                    }
                    break;
            }

            return true;
        }
    };

    /**
     * Answer the call action method to record in database
     */
    private void answerCall() {
        if (canRecordAudio() || resultAudioRecordPermission) {
            String[] splitIds = mCallId.split("-");
            String id = splitIds[2];
            String callDocId = fromUserId + "-" + mCurrentUserId + "-" + id;

            JSONObject object = CallMessage.getCallStatusObject(mCurrentUserId, fromUserId,
                    id, callDocId, mRecordId, MessageFactory.CALL_STATUS_ANSWERED, isVideoCall ? "1" : "0");

            Log.e("==calllog", "==call answered from opponent" + object);

            MessageDbController db = CoreController.getDBInstance(this);
            db.updateCallStatus(mCallId, MessageFactory.CALL_STATUS_ANSWERED, "00:00");

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_CALL_STATUS);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    CallMessage.openCallScreen(IncomingCallActivity.this, mCurrentUserId, fromUserId, mCallId,
                            mRoomId, profilePic, fromUserMsisdn, MessageFactory.CALL_IN_FREE + "",
                            isVideoCall, false, mCallTS);
                    finish();
                }
            }, 1000);

            ibReject.setOnTouchListener(null);
            ibAnswer.setOnTouchListener(null);

        } else {
            requestAudioRecordPermission();
        }
    }

    /**
     * Reject the call action method to record in database
     */
    private void rejectCall() {
        String[] splitIds = mCallId.split("-");
        String id = splitIds[2];
        String callDocId = fromUserId + "-" + mCurrentUserId + "-" + id;
        //changed for iOS
        int callStatus = MessageFactory.CALL_STATUS_END;
        if (!ConnectivityInfo.isInternetConnected(IncomingCallActivity.this)) {
            callStatus = MessageFactory.CALL_STATUS_MISSED;
        }

        JSONObject object = CallMessage.getCallStatusObject(mCurrentUserId, fromUserId, id, callDocId, mRecordId, callStatus, isVideoCall ? "1" : "0");

        Log.e("==calllog", "==call disconnect from opponent" + object);

        MessageDbController db = CoreController.getDBInstance(this);
        db.updateCallStatus(mCallId, callStatus, "00:00");

        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_CALL_STATUS);
        event.setMessageObject(object);
        EventBus.getDefault().post(event);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 1000);

        ibReject.setOnTouchListener(null);
        ibAnswer.setOnTouchListener(null);
    }

    /**
     * send Incoming Call Broadcast
     */
    private void sendIncomingCallBroadcast() {
        if (broadcastHandler == null) {
            broadcastHandler = new Handler();
            broadcastRunnable = new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent();
                    intent.setAction(getPackageName() + ".incoming_call");
                    intent.putExtras(getIntent().getExtras());
                    sendBroadcast(intent);
                    broadcastHandler.postDelayed(this, 5000);
                }
            };
            broadcastHandler.postDelayed(broadcastRunnable, 2500);
        }
    }

    /**
     * handling the call UI view
     */
    @Override
    protected void onResume() {
        super.onResume();
        sendIncomingCallBroadcast();
    }

    /**
     * handling the call pause view
     */
    @Override
    protected void onPause() {
        super.onPause();
        try {
//            rejectCall();
//            finish();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
   /* public static void exitApplicationAndRemoveFromRecent(Activity context)
    {
        Intent intent = new Intent(context, context.cl);

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_NO_ANIMATION);

        context.startActivity(intent);
    }*/
}