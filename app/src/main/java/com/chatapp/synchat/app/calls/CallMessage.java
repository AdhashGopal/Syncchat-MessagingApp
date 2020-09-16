package com.chatapp.synchat.app.calls;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.chatapphelperclass.ChatappUtilities;
import com.chatapp.synchat.core.service.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * created by  Adhash Team on 7/18/2017.
 */
public class CallMessage {

    private Context context;
    private String id, mCurrentUserId, tsForServerEpoch, tsForServer;
    private static final String TAG = CallMessage.class.getSimpleName() + ">>>@@";
    public static boolean isAlreadyCallClick;
    public static final long CALL_CLICK_TIMEOUT = 15000;

    // Remove retry call event until opponent user gets new call notification
    public static String arrivedCallId = "";

    /**
     * constructor
     *
     * @param context The activity object inherits the Context object
     */
    public CallMessage(Context context) {
        this.context = context;
        mCurrentUserId = SessionManager.getInstance(context).getCurrentUserID();

    }

    /**
     * getting Message Object value
     *
     * @param to       to user id
     * @param callType based on call type(audio/video)
     * @return value
     */
    public Object getMessageObject(String to, int callType) {
        tsForServer = ChatappUtilities.tsInGmt();
        tsForServerEpoch = new ChatappUtilities().gmtToEpoch(tsForServer);
        setId(mCurrentUserId + "-" + to);

        JSONObject object = new JSONObject();
        try {
            object.put("from", mCurrentUserId);
            object.put("to", to);
            object.put("type", callType);
            object.put("id", tsForServerEpoch);
            object.put("roomid", tsForServerEpoch);
            object.put("toDocId", getId() + "-" + tsForServerEpoch);
            return object;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * getId
     *
     * @return value
     */
    public String getId() {
        return id;
    }

    /**
     * setId
     *
     * @param id pass the value
     */
    public void setId(String id) {
        this.id = id;
    }

/*    public CallItemChat getCallCount(String currentUserId, String receiverId, CallItemChat currentCallItem) {
        MessageDbController dbController = CoreController.getDBInstance(context);
        CallItemChat topCallItem = dbController.selectTopCallLog(currentUserId);

        String callAtObj = null;
        if (topCallItem != null && topCallItem.isSelf() == currentCallItem.isSelf()
                && topCallItem.getOpponentUserId().equalsIgnoreCase(receiverId)
                && topCallItem.getCallType().equals(currentCallItem.getCallType())) {
            callAtObj = topCallItem.getCalledAtObj();
        }

        try {
            JSONArray arrTimes;
            if (callAtObj == null || callAtObj.equals("")) {
                arrTimes = new JSONArray();
            } else {
                arrTimes = new JSONArray(callAtObj);
            }
            arrTimes.put(currentCallItem.getTS());
            currentCallItem.setCallCount(arrTimes.length());
            currentCallItem.setCalledAtObj(arrTimes.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return currentCallItem;
    }*/

    /**
     * get Short TimeFormat
     *
     * @return value
     */
    public String getShortTimeFormat() {
        long deviceTS = Calendar.getInstance().getTimeInMillis();
        long timeDiff = SessionManager.getInstance(context).getServerTimeDifference();
        long localTime = deviceTS - timeDiff;
        return String.valueOf(localTime);
    }

    /**
     * get Call Status Object
     *
     * @param from       input value(from user)
     * @param to         input value(to user)
     * @param id         input value(id)
     * @param callDocId  input value(call doc id)
     * @param recordId   input value( record id)
     * @param callStatus input value( call status)
     * @param callType   input value( call type)
     * @return value
     */
    public static JSONObject getCallStatusObject(String from, String to, String id, String callDocId, String recordId, int callStatus, String callType) {
        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("to", to);
            object.put("id", id);
            object.put("toDocId", callDocId);
            object.put("recordId", recordId);
            object.put("device_type", 1);
            object.put("call_status", callStatus + "");
            object.put("type", callType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public static JSONObject getOpponentCallConnectedObject(String from, String to, String callDocId,
                                                            String recordId, int connectStatus) {
        JSONObject object = new JSONObject();
        try {
            object.put("from", from);
            object.put("to", to);
            object.put("toDocId", callDocId);
            object.put("recordId", recordId);
            object.put("connected_status", connectStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    /**
     * get roomid for call connection
     *
     * @return value
     */
    public String getroomid() {
        tsForServer = ChatappUtilities.tsInGmt();
        tsForServerEpoch = new ChatappUtilities().gmtToEpoch(tsForServer);
        return tsForServerEpoch;
    }


    /**
     * open Call full Screen to handling the call connection
     *
     * @param context                current activity
     * @param from                   from user
     * @param to                     to user
     * @param id                     connection id
     * @param roomId                 room id
     * @param opponentUserProfilePic user image
     * @param msisdn                 get msisdn value
     * @param callConnect            call connect status
     * @param isVideoCall            check video call or not
     * @param isOutgoingCall         check out going call or not
     * @param ts                     timestamp
     */
    public static void openCallScreen(Context context, String from, String to, String id, String roomId,
                                      String opponentUserProfilePic, String msisdn, String callConnect,
                                      boolean isVideoCall, boolean isOutgoingCall, String ts) {
        Log.d(TAG, "openCallScreen: start");
        if (!CallsActivity.isStarted) {
            if (isOutgoingCall) {
                CallsActivity.opponentUserId = to;
            }

            PreferenceManager.setDefaultValues(context, org.appspot.apprtc.R.xml.preferences, false);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

            String keyprefRoomServerUrl = context.getString(org.appspot.apprtc.R.string.pref_room_server_url_key);
            String roomUrl = sharedPref.getString(
                    keyprefRoomServerUrl, Constants.SOCKET_IP_BASE);
            Log.d(TAG, "openCallScreen: 1");

            int videoWidth = 0;
            int videoHeight = 0;
            String resolution = context.getString(org.appspot.apprtc.R.string.pref_resolution_default);
            String[] dimensions = resolution.split("[ x]+");
            if (dimensions.length == 2) {
                try {
                    videoWidth = Integer.parseInt(dimensions[0]);
                    videoHeight = Integer.parseInt(dimensions[1]);
                } catch (NumberFormatException e) {
                    videoWidth = 0;
                    videoHeight = 0;
                    Log.e("ChatappCallError", "Wrong video resolution setting: " + resolution);
                }
            }
            Log.d(TAG, "openCallScreen: 2");
            Uri uri = Uri.parse(roomUrl);
            Intent intent = new Intent(context, CallsActivity.class);
//            Intent intent = new Intent(context, CallNotifyService.class);

            intent.setData(uri);
            intent.putExtra(CallsActivity.EXTRA_IS_OUTGOING_CALL, isOutgoingCall);
            intent.putExtra(CallsActivity.EXTRA_DOC_ID, id);
            intent.putExtra(CallsActivity.EXTRA_FROM_USER_ID, from);
            intent.putExtra(CallsActivity.EXTRA_TO_USER_ID, to);
            intent.putExtra(CallsActivity.EXTRA_USER_MSISDN, msisdn);
            intent.putExtra(CallsActivity.EXTRA_OPPONENT_PROFILE_PIC, opponentUserProfilePic);
            intent.putExtra(CallsActivity.EXTRA_NAVIGATE_FROM, context.getClass().getSimpleName()); // For navigating from call activity
            intent.putExtra(CallsActivity.EXTRA_CALL_CONNECT_STATUS, callConnect);
            intent.putExtra(CallsActivity.EXTRA_CALL_TIME_STAMP, ts);

            intent.putExtra(CallsActivity.EXTRA_ROOMID, roomId);
            intent.putExtra(CallsActivity.EXTRA_LOOPBACK, false);
            intent.putExtra(CallsActivity.EXTRA_VIDEO_CALL, isVideoCall);
            intent.putExtra(CallsActivity.EXTRA_SCREENCAPTURE, false);
            intent.putExtra(CallsActivity.EXTRA_CAMERA2, true);
            intent.putExtra(CallsActivity.EXTRA_VIDEO_WIDTH, videoWidth);
            intent.putExtra(CallsActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
            intent.putExtra(CallsActivity.EXTRA_VIDEO_FPS, 0);
            intent.putExtra(CallsActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false);
            intent.putExtra(CallsActivity.EXTRA_VIDEO_BITRATE, 0);
            intent.putExtra(CallsActivity.EXTRA_VIDEOCODEC, context.getString(org.appspot.apprtc.R.string.pref_videocodec_default));
            intent.putExtra(CallsActivity.EXTRA_HWCODEC_ENABLED, false);
            intent.putExtra(CallsActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, true);
            intent.putExtra(CallsActivity.EXTRA_FLEXFEC_ENABLED, false);
            intent.putExtra(CallsActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, false);
            intent.putExtra(CallsActivity.EXTRA_AECDUMP_ENABLED, false);
            intent.putExtra(CallsActivity.EXTRA_OPENSLES_ENABLED, false);
            intent.putExtra(CallsActivity.EXTRA_DISABLE_BUILT_IN_AEC, false);
            intent.putExtra(CallsActivity.EXTRA_DISABLE_BUILT_IN_AGC, false);
            intent.putExtra(CallsActivity.EXTRA_DISABLE_BUILT_IN_NS, false);
            intent.putExtra(CallsActivity.EXTRA_ENABLE_LEVEL_CONTROL, false);
            intent.putExtra(CallsActivity.EXTRA_AUDIO_BITRATE, 0);
            intent.putExtra(CallsActivity.EXTRA_AUDIOCODEC, context.getString(org.appspot.apprtc.R.string.pref_audiocodec_default));
            intent.putExtra(CallsActivity.EXTRA_DISPLAY_HUD, false);
            intent.putExtra(CallsActivity.EXTRA_TRACING, false);
            intent.putExtra(CallsActivity.EXTRA_CMDLINE, false);
            intent.putExtra(CallsActivity.EXTRA_RUNTIME, 0);

            intent.putExtra(CallsActivity.EXTRA_DATA_CHANNEL_ENABLED, true);
            intent.putExtra(CallsActivity.EXTRA_ORDERED, true);
            intent.putExtra(CallsActivity.EXTRA_MAX_RETRANSMITS_MS, -1);
            intent.putExtra(CallsActivity.EXTRA_MAX_RETRANSMITS, -1);
            intent.putExtra(CallsActivity.EXTRA_PROTOCOL, context.getString(org.appspot.apprtc.R.string.pref_data_protocol_default));
            intent.putExtra(CallsActivity.EXTRA_NEGOTIATED, false);
            intent.putExtra(CallsActivity.EXTRA_ID, -1);

            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            Log.d(TAG, "openCallScreen: 3");
//            context.startService(intent);
        }
    }

    /**
     * resumeCall (waiting state)
     *
     * @param context current activity
     */
    public static void resumeCall(Context context) {
        System.out.println("==here CallMessage");
        Intent callIntent = new Intent(context, CallsActivity.class);
        callIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        context.startActivity(callIntent);
    }

    /**
     * set Call Click Timeout
     */
    public static void setCallClickTimeout() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    isAlreadyCallClick = false;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, CALL_CLICK_TIMEOUT);

        isAlreadyCallClick = true;
    }
}
