package com.chatapp.synchat.app.calls;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.appspot.apprtc.AppRTCAudioManager;
import org.appspot.apprtc.AppRTCClient;
import org.appspot.apprtc.CallFragment;
import org.appspot.apprtc.DirectRTCClient;
import org.appspot.apprtc.PeerConnectionClient;
import org.appspot.apprtc.WebSocketRTCClient;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.RendererCommon;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;

import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * created by  Adhash Team on 8/2/2017.
 */
public class CallNotifyService extends Service implements AppRTCClient.SignalingEvents,
        PeerConnectionClient.PeerConnectionEvents,
        CallFragment.OnCallEvents {

    public static final String EXTRA_DOC_ID = "DocId";
    public static final String EXTRA_FROM_USER_ID = "FromUserId";
    public static final String EXTRA_TO_USER_ID = "ToUserId";
    public static final String EXTRA_USER_MSISDN = "Msisdn";
    public static final String EXTRA_IS_OUTGOING_CALL = "OutgoingCall";
    public static final String EXTRA_OPPONENT_PROFILE_PIC = "ProfilePic";
    public static final String EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL";
    public static final String EXTRA_ROOMID = "org.appspot.apprtc.ROOMID";

    public static boolean isServiceStarted = false;
    private String TAG = "CallService";

    private AppRTCClient appRtcClient;
    private PeerConnectionClient peerConnectionClient = null;

    private RendererCommon.ScalingType scalingType;
    private AppRTCClient.RoomConnectionParameters roomConnectionParameters;
    private AppRTCAudioManager audioManager = null;

    private boolean micEnabled = true;
    private boolean speakerEnabled = true;
    private boolean isError;
    private boolean iceConnected;
    private long callStartedTimeMs = 0;
    private int callDuration = 0;
    private String mCurrentUserId, fromUserId, toUserId, mRoomId, mCallId;
    private boolean isConnectedToUser = false, needToSendServer = true, isOutgoingCall, isVideoCall;

    private Timer timer;

    private static final int STAT_CALLBACK_PERIOD = 1000;

    @Override
    public void onCreate() {
        super.onCreate();
        isServiceStarted = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mCurrentUserId = SessionManager.getInstance(this).getCurrentUserID();

        mCallId = intent.getStringExtra(EXTRA_DOC_ID);
        fromUserId = intent.getStringExtra(EXTRA_FROM_USER_ID);
        toUserId = intent.getStringExtra(EXTRA_TO_USER_ID);
        isVideoCall = intent.getBooleanExtra(EXTRA_VIDEO_CALL, true);
        mRoomId = intent.getStringExtra(EXTRA_ROOMID);

        connectRoom(intent);

        /*Intent callIntent = new Intent(this, CallsActivity.class);
        callIntent.putExtras(intent.getExtras());
        callIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(callIntent);*/

        return super.onStartCommand(intent, flags, startId);
    }

    private void connectRoom(Intent intent) {
        boolean loopback = intent.getBooleanExtra(CallsActivity.EXTRA_LOOPBACK, false);
        boolean tracing = intent.getBooleanExtra(CallsActivity.EXTRA_TRACING, false);

        int videoWidth = intent.getIntExtra(CallsActivity.EXTRA_VIDEO_WIDTH, 0);
        int videoHeight = intent.getIntExtra(CallsActivity.EXTRA_VIDEO_HEIGHT, 0);

        PeerConnectionClient.DataChannelParameters dataChannelParameters = null;
        if (intent.getBooleanExtra(CallsActivity.EXTRA_DATA_CHANNEL_ENABLED, true)) {
            dataChannelParameters = new PeerConnectionClient.DataChannelParameters(intent.getBooleanExtra(CallsActivity.EXTRA_ORDERED, true),
                    intent.getIntExtra(CallsActivity.EXTRA_MAX_RETRANSMITS_MS, -1),
                    intent.getIntExtra(CallsActivity.EXTRA_MAX_RETRANSMITS, -1), intent.getStringExtra(CallsActivity.EXTRA_PROTOCOL),
                    intent.getBooleanExtra(CallsActivity.EXTRA_NEGOTIATED, false), intent.getIntExtra(CallsActivity.EXTRA_ID, -1));
        }
       /* peerConnectionParameters =
                new PeerConnectionClient.PeerConnectionParameters(intent.getBooleanExtra(EXTRA_VIDEO_CALL, true), loopback,
                        tracing, videoWidth, videoHeight, intent.getIntExtra(CallsActivity.EXTRA_VIDEO_FPS, 0),
                        intent.getIntExtra(CallsActivity.EXTRA_VIDEO_BITRATE, 0), intent.getStringExtra(CallsActivity.EXTRA_VIDEOCODEC),
                        intent.getBooleanExtra(CallsActivity.EXTRA_HWCODEC_ENABLED, true),
                        intent.getBooleanExtra(CallsActivity.EXTRA_FLEXFEC_ENABLED, false),
                        intent.getIntExtra(CallsActivity.EXTRA_AUDIO_BITRATE, 0), intent.getStringExtra(CallsActivity.EXTRA_AUDIOCODEC),
                        intent.getBooleanExtra(CallsActivity.EXTRA_NOAUDIOPROCESSING_ENABLED, false),
                        intent.getBooleanExtra(CallsActivity.EXTRA_AECDUMP_ENABLED, false),
                        intent.getBooleanExtra(CallsActivity.EXTRA_OPENSLES_ENABLED, false),
                        intent.getBooleanExtra(CallsActivity.EXTRA_DISABLE_BUILT_IN_AEC, false),
                        intent.getBooleanExtra(CallsActivity.EXTRA_DISABLE_BUILT_IN_AGC, false),
                        intent.getBooleanExtra(CallsActivity.EXTRA_DISABLE_BUILT_IN_NS, false),
                        intent.getBooleanExtra(CallsActivity.EXTRA_ENABLE_LEVEL_CONTROL, false), dataChannelParameters);
        commandLineRun = intent.getBooleanExtra(CallsActivity.EXTRA_CMDLINE, false);
        runTimeMs = intent.getIntExtra(CallsActivity.EXTRA_RUNTIME, 0);*/

        Log.d(TAG, "VIDEO_FILE: '" + intent.getStringExtra(CallsActivity.EXTRA_VIDEO_FILE_AS_CAMERA) + "'");

        // Create connection client. Use DirectRTCClient if room name is an IP otherwise use the
        // standard WebSocketRTCClient.
        if (loopback || !DirectRTCClient.IP_PATTERN.matcher(mRoomId).matches()) {
            appRtcClient = new WebSocketRTCClient(this,getApplicationContext());
        } else {
            Log.i(TAG, "Using DirectRTCClient because room name looks like an IP.");
            appRtcClient = new DirectRTCClient(this);
        }
        Uri roomUri = intent.getData();
        // Create connection parameters.
        roomConnectionParameters = new AppRTCClient.RoomConnectionParameters(roomUri.toString(), mRoomId, loopback);

        // Create CPU monitor
//        cpuMonitor = new CpuMonitor(this);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceStarted = false;
    }

    // CallFragment.OnCallEvents interface implementation.
    @Override
    public void onCallHangUp() {
        disconnect();
    }

    @Override
    public void onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
        }
    }

    @Override
    public void onVideoScalingSwitch(RendererCommon.ScalingType scalingType) {
        this.scalingType = scalingType;
    }

    @Override
    public void onCaptureFormatChange(int width, int height, int framerate) {
        if (peerConnectionClient != null) {
            peerConnectionClient.changeCaptureFormat(width, height, framerate);
        }
    }

    @Override
    public boolean onToggleMic() {
        if (peerConnectionClient != null) {
            micEnabled = !micEnabled;
            peerConnectionClient.setAudioEnabled(micEnabled);
        }
        return micEnabled;
    }

    @Override
    public boolean onToggleSpeaker() {

        speakerEnabled = !speakerEnabled;

        if (speakerEnabled) {
            audioManager.setAudioDeviceInternal(AppRTCAudioManager.AudioDevice.SPEAKER_PHONE);
        } else {
            audioManager.setAudioDeviceInternal(AppRTCAudioManager.AudioDevice.EARPIECE);
        }

        return speakerEnabled;
    }

    @Override
    public void onPageClick() {

    }

    @Override
    public void gotomsg() {

    }

    private void startCall() {
        if (appRtcClient == null) {
            Log.e(TAG, "AppRTC client is not allocated for a call.");
            return;
        }
        callStartedTimeMs = System.currentTimeMillis();

        // Start room connection.
        logAndToast(getString(org.appspot.apprtc.R.string.connecting_to, roomConnectionParameters.roomUrl));
        appRtcClient.connectToRoom(roomConnectionParameters);

        // Create and audio manager that will take care of audio routing,
        // audio modes, audio device enumeration etc.
        audioManager = AppRTCAudioManager.create(this);
        // Store existing audio settings and change audio mode to
        // MODE_IN_COMMUNICATION for best possible VoIP performance.
        Log.d(TAG, "Starting the audio manager...");
        audioManager.start(new AppRTCAudioManager.AudioManagerEvents() {
            // This method will be called each tsNextLine the number of available audio
            // devices has changed.
            @Override
            public void onAudioDeviceChanged(
                    AppRTCAudioManager.AudioDevice audioDevice, Set<AppRTCAudioManager.AudioDevice> availableAudioDevices) {
                onAudioManagerDevicesChanged(audioDevice, availableAudioDevices);
            }
        });
    }

    // Should be called from UI thread
    private void callConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        Log.i(TAG, "Call connected: delay=" + delta + "ms");
        if (peerConnectionClient == null || isError) {
            Log.i(TAG, "Call connected: line=" + 249);
            Log.w(TAG, "Call is connected in closed or error state");
            return;
        }

        // Enable statistics callback.
        peerConnectionClient.enableStatsEvents(true, STAT_CALLBACK_PERIOD);
    }

    // This method is called when the audio manager reports audio device change,
    // e.g. from wired headset to speakerphone.
    private void onAudioManagerDevicesChanged(
            final AppRTCAudioManager.AudioDevice device, final Set<AppRTCAudioManager.AudioDevice> availableDevices) {
        Log.d(TAG, "onAudioManagerDevicesChanged: " + availableDevices + ", "
                + "selected: " + device);
        // TODO(henrika): add callback handler.
    }

    // Disconnect from remote resources, dispose of local resources, and exit.
    private void disconnect() {
        if (timer != null) {
            timer.cancel();
        }

        MessageDbController db = CoreController.getDBInstance(this);
        db.updateCallStatus(mCallId, MessageFactory.CALL_STATUS_END, getCallDuration());

        if (appRtcClient != null) {
            appRtcClient.disconnectFromRoom();
            appRtcClient = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }

        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
        if (iceConnected && !isError) {
            //setResult(RESULT_OK);
        } else {
            Log.i(TAG, "Call connected: line=" + 291);
            //setResult(RESULT_CANCELED);
        }
        onDestroy();
    }

    private void disconnectWithErrorMessage(final String errorMessage) {
        /*if (commandLineRun || !activityRunning) {
            Log.e(TAG, "Critical error: " + errorMessage);
            disconnect();
        } else {
            new AlertDialog.Builder(this)
                    .setTitle(getText(org.appspot.apprtc.R.string.channel_error_title))
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setNeutralButton(org.appspot.apprtc.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    disconnect();
                                }
                            })
                    .create()
                    .show();
        }*/
    }

    // Log |msg| and Toast about it.
    private void logAndToast(String msg) {
        Log.d(TAG, msg);

//        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
//        logToast.show();
    }

    private void reportError(final String description) {
        Log.i(TAG, "Call connected: line=" + 328);

        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError) {
                    isError = true;
                    disconnectWithErrorMessage(description);
                }
            }
        });*/
    }

    @Override
    public void onConnectedToRoom(final AppRTCClient.SignalingParameters params) {
//        onConnectedToRoomInternal(params);
        Log.d("backservicecall", "connected");
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {

    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
                    return;
                }
                peerConnectionClient.addRemoteIceCandidate(candidate);
            }
        });*/
    }

    @Override
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
       /* runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
                    return;
                }
                peerConnectionClient.removeRemoteIceCandidates(candidates);
            }
        });*/
    }

    @Override
    public void onChannelClose() {
        disconnect();
    }

    @Override
    public void onChannelError(final String description) {
        Log.i(TAG, "Call connected: line=" + 387);
        reportError(description);
    }

    // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
    // Send local peer connection SDP and ICE candidates to remote party.
    // All callbacks are invoked from peer connection client looper thread and
    // are routed to UI thread.
    @Override
    public void onLocalDescription(final SessionDescription sdp) {
       /* final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    logAndToast("Sending " + sdp.type + ", delay=" + delta + "ms");
                    if (signalingParameters.initiator) {
                        appRtcClient.sendOfferSdp(sdp);
                    } else {
                        appRtcClient.sendAnswerSdp(sdp);
                    }
                }
                if (peerConnectionParameters.videoMaxBitrate > 0) {
                    Log.d(TAG, "Set video maximum bitrate: " + peerConnectionParameters.videoMaxBitrate);
                    peerConnectionClient.setVideoMaxBitrate(peerConnectionParameters.videoMaxBitrate);
                }
            }
        });*/
    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        if (appRtcClient != null) {
            appRtcClient.sendLocalIceCandidate(candidate);
        }
    }

    @Override
    public void onIceCandidatesRemoved(final IceCandidate[] candidates) {

                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidateRemovals(candidates);
                }

    }

    @Override
    public void onIceConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        iceConnected = true;
        startTimer();
        callConnected();
        Log.d("backservicecalloppo", "connected");
    }

    @Override
    public void onIceDisconnected() {
        iceConnected = false;
        disconnect();
    }

    @Override
    public void onPeerConnectionClosed() {
    }

    @Override
    public void onPeerConnectionStatsReady(final StatsReport[] reports) {

    }

    @Override
    public void onPeerConnectionError(final String description) {
        Log.i(TAG, "Call connected: line=" + 459);
        reportError(description);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {

        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CALL_STATUS)) {
            String data = event.getObjectsArray()[0].toString();
            try {
                JSONObject object = new JSONObject(data);
                String recordId = object.getString("recordId");
                String status = object.getString("call_status");
                if (recordId.equalsIgnoreCase(mRoomId) &&
                        (status.equals(MessageFactory.CALL_STATUS_END + "") ||
                                status.equals(MessageFactory.CALL_STATUS_RECEIVED + ""))) {
                    needToSendServer = false;
                    disconnect();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendCallDisconnectToServer() {
        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_CALL_STATUS);

        /*MessageDbController db = CoreController.getDBInstance();
        JSONObject object;

        if (isOutgoingCall) {

            if (isConnectedToUser) {
                object = CallMessage.getCallStatusObject(fromUserId, toUserId, mRoomId, MessageFactory.CALL_STATUS_END);
                db.updateCallStatus(mCallId, MessageFactory.CALL_STATUS_END, getCallDuration());
            } else {
                object = CallMessage.getCallStatusObject(fromUserId, toUserId, mRoomId, MessageFactory.CALL_STATUS_REJECTED);
                db.updateCallStatus(mCallId, MessageFactory.CALL_STATUS_REJECTED, getCallDuration());
            }
        } else {
            object = CallMessage.getCallStatusObject(toUserId, fromUserId, mRoomId, MessageFactory.CALL_STATUS_END);
            db.updateCallStatus(mCallId, MessageFactory.CALL_STATUS_END, getCallDuration());
        }
        event.setMessageObject(object);
        EventBus.getDefault().post(event);*/
    }

    private void startTimer() {
        if (timer == null) {

            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    callDuration++;
                }
            }, 1000, 1000);
        }
    }

    private String getCallDuration() {
        int hr, min, sec;
        String durationStr, secStr, minStr, hrStr = "";

        sec = callDuration % 60;
        min = (callDuration / 60) % 60;
        hr = (callDuration / (60 * 60)) % 60;

        if (sec < 10) {
            secStr = "0" + sec;
        } else {
            secStr = String.valueOf(sec);
        }

        if (min < 10) {
            minStr = "0" + min + ":";
        } else {
            minStr = min + ":";
        }

        if (hr > 0) {
            if (hr < 10) {
                hrStr = "0" + hr + ":";
            } else {
                hrStr = hr + ":";
            }
        }

        durationStr = hrStr + minStr + secStr;
        return durationStr;
    }
}
