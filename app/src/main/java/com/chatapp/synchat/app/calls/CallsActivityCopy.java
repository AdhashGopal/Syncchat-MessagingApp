package com.chatapp.synchat.app.calls;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.CallItemChat;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.appspot.apprtc.AppRTCAudioManager;
import org.appspot.apprtc.AppRTCClient;
import org.appspot.apprtc.CallFragment;
import org.appspot.apprtc.CpuMonitor;
import org.appspot.apprtc.DirectRTCClient;
import org.appspot.apprtc.HudFragment;
import org.appspot.apprtc.PeerConnectionClient;
import org.appspot.apprtc.PercentFrameLayout;
import org.appspot.apprtc.UnhandledExceptionHandler;
import org.appspot.apprtc.WebSocketRTCClient;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.FileVideoCapturer;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RendererCommon;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;
import org.webrtc.StatsReport;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoFileRenderer;
import org.webrtc.VideoRenderer;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * created by  Adhash Team on 7/18/2017.
 */

/**
 * Activity for peer connection call setup, call waiting
 * and call view.
 */
public class CallsActivityCopy extends CoreActivity implements AppRTCClient.SignalingEvents,
        PeerConnectionClient.PeerConnectionEvents,
        CallFragment.OnCallEvents {

    public int cameraswirchcount = 0;
    public boolean cameraswitched = false;
    public static final String EXTRA_DOC_ID = "DocId";
    public static final String EXTRA_FROM_USER_ID = "FromUserId";
    public static final String EXTRA_TO_USER_ID = "ToUserId";
    public static final String EXTRA_USER_MSISDN = "Msisdn";
    public static final String EXTRA_IS_OUTGOING_CALL = "OutgoingCall";
    public static final String EXTRA_OPPONENT_PROFILE_PIC = "ProfilePic";
    public static final String EXTRA_NAVIGATE_FROM = "NavigateFrom";
    public static final String EXTRA_CALL_CONNECT_STATUS = "CallConnectStatus";
    public static final String EXTRA_CALL_TIME_STAMP = "CallTimeStamp";

    public static final String EXTRA_ROOMID = "org.appspot.apprtc.ROOMID";
    public static final String EXTRA_LOOPBACK = "org.appspot.apprtc.LOOPBACK";
    public static final String EXTRA_VIDEO_CALL = "org.appspot.apprtc.VIDEO_CALL";
    public static final String EXTRA_SCREENCAPTURE = "org.appspot.apprtc.SCREENCAPTURE";
    public static final String EXTRA_CAMERA2 = "org.appspot.apprtc.CAMERA2";
    public static final String EXTRA_VIDEO_WIDTH = "org.appspot.apprtc.VIDEO_WIDTH";
    public static final String EXTRA_VIDEO_HEIGHT = "org.appspot.apprtc.VIDEO_HEIGHT";
    public static final String EXTRA_VIDEO_FPS = "org.appspot.apprtc.VIDEO_FPS";
    public static final String EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED =
            "org.appsopt.apprtc.VIDEO_CAPTUREQUALITYSLIDER";
    public static final String EXTRA_VIDEO_BITRATE = "org.appspot.apprtc.VIDEO_BITRATE";
    public static final String EXTRA_VIDEOCODEC = "org.appspot.apprtc.VIDEOCODEC";
    public static final String EXTRA_HWCODEC_ENABLED = "org.appspot.apprtc.HWCODEC";
    public static final String EXTRA_CAPTURETOTEXTURE_ENABLED = "org.appspot.apprtc.CAPTURETOTEXTURE";
    public static final String EXTRA_FLEXFEC_ENABLED = "org.appspot.apprtc.FLEXFEC";
    public static final String EXTRA_AUDIO_BITRATE = "org.appspot.apprtc.AUDIO_BITRATE";
    public static final String EXTRA_AUDIOCODEC = "org.appspot.apprtc.AUDIOCODEC";
    public static final String EXTRA_NOAUDIOPROCESSING_ENABLED =
            "org.appspot.apprtc.NOAUDIOPROCESSING";
    public static final String EXTRA_AECDUMP_ENABLED = "org.appspot.apprtc.AECDUMP";
    public static final String EXTRA_OPENSLES_ENABLED = "org.appspot.apprtc.OPENSLES";
    public static final String EXTRA_DISABLE_BUILT_IN_AEC = "org.appspot.apprtc.DISABLE_BUILT_IN_AEC";
    public static final String EXTRA_DISABLE_BUILT_IN_AGC = "org.appspot.apprtc.DISABLE_BUILT_IN_AGC";
    public static final String EXTRA_DISABLE_BUILT_IN_NS = "org.appspot.apprtc.DISABLE_BUILT_IN_NS";
    public static final String EXTRA_ENABLE_LEVEL_CONTROL = "org.appspot.apprtc.ENABLE_LEVEL_CONTROL";
    public static final String EXTRA_DISPLAY_HUD = "org.appspot.apprtc.DISPLAY_HUD";
    public static final String EXTRA_TRACING = "org.appspot.apprtc.TRACING";
    public static final String EXTRA_CMDLINE = "org.appspot.apprtc.CMDLINE";
    public static final String EXTRA_RUNTIME = "org.appspot.apprtc.RUNTIME";
    public static final String EXTRA_VIDEO_FILE_AS_CAMERA = "org.appspot.apprtc.VIDEO_FILE_AS_CAMERA";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_WIDTH";
    public static final String EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT =
            "org.appspot.apprtc.SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT";
    public static final String EXTRA_USE_VALUES_FROM_INTENT =
            "org.appspot.apprtc.USE_VALUES_FROM_INTENT";
    public static final String EXTRA_DATA_CHANNEL_ENABLED = "org.appspot.apprtc.DATA_CHANNEL_ENABLED";
    public static final String EXTRA_ORDERED = "org.appspot.apprtc.ORDERED";
    public static final String EXTRA_MAX_RETRANSMITS_MS = "org.appspot.apprtc.MAX_RETRANSMITS_MS";
    public static final String EXTRA_MAX_RETRANSMITS = "org.appspot.apprtc.MAX_RETRANSMITS";
    public static final String EXTRA_PROTOCOL = "org.appspot.apprtc.PROTOCOL";
    public static final String EXTRA_NEGOTIATED = "org.appspot.apprtc.NEGOTIATED";
    public static final String EXTRA_ID = "org.appspot.apprtc.ID";

    private static final String TAG = "CallRTCClient";
    private static final int CAPTURE_PERMISSION_REQUEST_CODE = 1;

    // List of mandatory application permissions.
    private static final String[] MANDATORY_PERMISSIONS = {"android.permission.MODIFY_AUDIO_SETTINGS",
            "android.permission.RECORD_AUDIO", "android.permission.INTERNET"};

    // Peer connection statistics callback period in ms.
    private static final int STAT_CALLBACK_PERIOD = 1000;
    // Local preview screen position before call is connected.
    private static final int LOCAL_X_CONNECTING = 0;
    private static final int LOCAL_Y_CONNECTING = 0;
    private static final int LOCAL_WIDTH_CONNECTING = 100;
    private static final int LOCAL_HEIGHT_CONNECTING = 100;
    // Local preview screen position after call is connected.
    private static final int LOCAL_X_CONNECTED = 72;
    private static final int LOCAL_Y_CONNECTED = 72;
    private static final int LOCAL_WIDTH_CONNECTED = 25;
    private static final int LOCAL_HEIGHT_CONNECTED = 25;
    // Remote video screen position
    private static final int REMOTE_X = 0;
    private static final int REMOTE_Y = 0;
    private static final int REMOTE_WIDTH = 100;
    private static final int REMOTE_HEIGHT = 100;
    private PeerConnectionClient peerConnectionClient = null;
    private AppRTCClient appRtcClient;
    private AppRTCClient.SignalingParameters signalingParameters;
    private AppRTCAudioManager audioManager = null;
    private EglBase rootEglBase;
    private SurfaceViewRenderer localRender;
    private SurfaceViewRenderer remoteRenderScreen;
    private VideoFileRenderer videoFileRenderer;
    private final List<VideoRenderer.Callbacks> remoteRenderers =
            new ArrayList<VideoRenderer.Callbacks>();
    private PercentFrameLayout localRenderLayout;
    private PercentFrameLayout remoteRenderLayout;
    private TextView tvName, tvCallLbl, tvDuration, tvCallStatus;
    private ImageView ivProfilePic;
    private RendererCommon.ScalingType scalingType;
    private Toast logToast;
    private boolean commandLineRun;
    private int runTimeMs;
    private boolean activityRunning;
    private AppRTCClient.RoomConnectionParameters roomConnectionParameters;
    private PeerConnectionClient.PeerConnectionParameters peerConnectionParameters;
    private boolean iceConnected;
    private boolean isError;
    private boolean callControlFragmentVisible = true;
    private long callStartedTimeMs = 0;
    private boolean micEnabled = true;
    private boolean speakerEnabled = true;
    private boolean screencaptureEnabled = false;
    private static Intent mediaProjectionPermissionResultData;
    private static int mediaProjectionPermissionResultCode;

    // Controls
    private CallFragment callFragment;
    private HudFragment hudFragment;
    private CpuMonitor cpuMonitor;

    public static boolean isStarted;
    private String mCurrentUserId, fromUserId, toUserId, mRoomId,mRecordId, mCallId, mPrevCallStatus, mCallTs;
    private MediaPlayer mediaPlayer;
    private boolean isConnectedToUser = false, needToSendServer = true, isOutgoingCall, isVideoCall,
            isArrivedToUser, isAnsweredToUser, isCallReconnecting;

    public boolean canEndCall;

    private Timer timer;
    private int callDuration = 0;
    private Handler callTimeoutHandler, retryCallHandler, reconnectHandler;
    private Runnable callTimeoutRunnable, retryCallRunnable, reconnectRunnable;
    private String mCallerName;

    public static final long MISSED_CALL_TIMEOUT = 60 * 1000; // 60 seconds
    private final long CALL_RETRY_DURATION = 6000; // 6 seconds
    private final long RECONNECT_CALL_TIMEOUT = 10 * 1000; // 10 seconds
    private final long OFFLINE_RECONNECT_CALL_TIMEOUT = 40 * 1000; // 40 seconds

    private boolean showDisconnectNotify = true;
    public static String opponentUserId = "";
    public Activity activity;


    private RelativeLayout disconnect_layout;
    private RelativeLayout Call_Disconnect;
    private ImageView ibToggleSpeaker;
    private ImageView button_call_switch_camera;
    private ImageView button_call_toggle_mic;


    private LinearLayout buttons_call_container;
    private RelativeLayout bottom_layout;
    private RelativeLayout call_header;
    private String Outgoing_call_type = "";

    private RelativeLayout local_video_relativelayout;
    private RelativeLayout dummy_layout;


    @SuppressLint("LongLogTag")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isStarted = true;

        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime());
        Log.d("Eventcalltrigger__callsacy", date);
        IntentFilter intentFilter = new IntentFilter(getPackageName() + ".incoming_call");
        registerReceiver(incomingCallReceiver, intentFilter);

        Thread.setDefaultUncaughtExceptionHandler(new UnhandledExceptionHandler(this));

        Log.d("NavigateFrom", getIntent().getExtras().getString("NavigateFrom", ""));

        // Set window styles for fullscreen-window size. Needs to be done before
        // adding content.
//        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
                | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        /*getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);*/
        setContentView(R.layout.activity_av_calls);

        disableLockScreen();

        iceConnected = false;
        signalingParameters = null;
        scalingType = RendererCommon.ScalingType.SCALE_ASPECT_FILL;

        // Create UI controls.
        localRender = (SurfaceViewRenderer) findViewById(R.id.local_video_view);
        remoteRenderScreen = (SurfaceViewRenderer) findViewById(R.id.remote_video_view);
        localRenderLayout = (PercentFrameLayout) findViewById(R.id.local_video_layout);
        remoteRenderLayout = (PercentFrameLayout) findViewById(R.id.remote_video_layout);

        tvName = (TextView) findViewById(R.id.tvName);
        tvCallLbl = (TextView) findViewById(R.id.tvCallLbl);
        tvDuration = (TextView) findViewById(R.id.tvDuration);
        tvCallStatus = (TextView) findViewById(R.id.tvCallStatus);
        ivProfilePic = (ImageView) findViewById(R.id.ivProfilePic);
        disconnect_layout = (RelativeLayout) findViewById(R.id.disconnect_layout);
        Call_Disconnect = (RelativeLayout) findViewById(R.id.Call_Disconnect);
        ibToggleSpeaker = (ImageView) findViewById(R.id.ibToggleSpeaker);
        button_call_switch_camera = (ImageView) findViewById(R.id.button_call_switch_camera);
        button_call_toggle_mic = (ImageView) findViewById(R.id.button_call_toggle_mic);

        buttons_call_container = (LinearLayout) findViewById(R.id.buttons_call_container);
        bottom_layout = (RelativeLayout) findViewById(R.id.bottom_layout);
        call_header = (RelativeLayout) findViewById(R.id.call_header);

        local_video_relativelayout=(RelativeLayout)findViewById(R.id.local_video_relativelayout);
        dummy_layout=(RelativeLayout)findViewById(R.id.dummy_layout);


//-----------------------------New Design--------------------------------------------

//        disconnect_layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                canEndCall = true;
//                disconnect(true);
//            }
//        });

        Call_Disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                canEndCall = true;
                disconnect(true);
            }
        });
        ibToggleSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabled = SpeakerOn();
                if (enabled) {
                    ibToggleSpeaker.setImageResource(org.appspot.apprtc.R.drawable.ic_specker_on);
                } else {
                    ibToggleSpeaker.setImageResource(org.appspot.apprtc.R.drawable.ic_specker_off);
                }
            }
        });
        button_call_switch_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (peerConnectionClient != null) {
                    peerConnectionClient.switchCamera();
                    if (cameraswitched) {
                        cameraswitched = false;
                    } else {
                        cameraswitched = true;
                    }
                    updateVideoView();
                }
            }
        });
        button_call_toggle_mic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean enabled = ToggleMic();
                button_call_toggle_mic.setAlpha(enabled ? 1.0f : 0.3f);
            }
        });


//---------------------------------------------------New Code End--------------------------------------------------


        callFragment = new CallFragment();
        hudFragment = new HudFragment();

        // Show/hide call control fragment on view click.
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleCallControlFragmentVisibility();
            }
        };

        localRender.setOnClickListener(listener);
        remoteRenderScreen.setOnClickListener(listener);
        remoteRenderers.add(remoteRenderScreen);

        final Intent intent = getIntent();

        // Create video renderers.
        rootEglBase = EglBase.create();
        localRender.init(rootEglBase.getEglBaseContext(), null);
        String saveRemoteVideoToFile = intent.getStringExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE);

        // When saveRemoteVideoToFile is set we save the video from the remote to a file.
        if (saveRemoteVideoToFile != null) {
            int videoOutWidth = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_WIDTH, 0);
            int videoOutHeight = intent.getIntExtra(EXTRA_SAVE_REMOTE_VIDEO_TO_FILE_HEIGHT, 0);
            try {
                videoFileRenderer = new VideoFileRenderer(
                        saveRemoteVideoToFile, videoOutWidth, videoOutHeight, rootEglBase.getEglBaseContext());
                remoteRenderers.add(videoFileRenderer);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to open video file for output: " + saveRemoteVideoToFile, e);
            }
        }
        remoteRenderScreen.init(rootEglBase.getEglBaseContext(), null);

        localRender.setZOrderMediaOverlay(true);
        localRender.setEnableHardwareScaler(true /* enabled */);
        remoteRenderScreen.setEnableHardwareScaler(true /* enabled */);
        updateVideoView();

        // Check for mandatory permissions.
        for (String permission : MANDATORY_PERMISSIONS) {
            if (checkCallingOrSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                logAndToast("Permission " + permission + " is not granted");
                setResult(RESULT_CANCELED);
                finish();
                return;
            }
        }

        Uri roomUri = intent.getData();
        if (roomUri == null) {
            logAndToast(getString(org.appspot.apprtc.R.string.missing_url));
            Log.e(TAG, "Didn't get any URL in intent!");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        mCurrentUserId = SessionManager.getInstance(this).getCurrentUserID();

        mCallId = intent.getStringExtra(EXTRA_DOC_ID);
        fromUserId = intent.getStringExtra(EXTRA_FROM_USER_ID);
        toUserId = intent.getStringExtra(EXTRA_TO_USER_ID);
        opponentUserId = intent.getStringExtra(EXTRA_TO_USER_ID);
        mCallTs = intent.getStringExtra(EXTRA_CALL_TIME_STAMP);
        isVideoCall = intent.getBooleanExtra(EXTRA_VIDEO_CALL, true);


        Getcontactname getcontactname = new Getcontactname(this);

        // For audio calls only
        if (!isVideoCall) {
            Outgoing_call_type = "0";


            buttons_call_container.setWeightSum(2);
            button_call_switch_camera.setVisibility(View.GONE);
            button_call_toggle_mic.setVisibility(View.VISIBLE);


            String profilePic = intent.getStringExtra(EXTRA_OPPONENT_PROFILE_PIC);

            if (fromUserId.equalsIgnoreCase(SessionManager.getInstance(this).getCurrentUserID())) {
                getcontactname.configProfilepic(ivProfilePic, toUserId, false, false, R.mipmap.chat_attachment_profile_default_image_frame);


            } else {
                getcontactname.configProfilepic(ivProfilePic, fromUserId, false, false, R.mipmap.chat_attachment_profile_default_image_frame);
            }
//            callFragment.setRootBackColor(ContextCompat.getColor(VideoCallActivity.this, R.color.colorPrimary));
        } else {
            Outgoing_call_type = "1";
            ivProfilePic.setVisibility(View.GONE);
            buttons_call_container.setWeightSum(3);
            button_call_switch_camera.setVisibility(View.VISIBLE);
            bottom_layout.setBackgroundColor(Color.TRANSPARENT);
            call_header.setBackgroundColor(Color.TRANSPARENT);

        }

        showInfoCtrls();

        String msisdn = intent.getStringExtra(EXTRA_USER_MSISDN);

        if (fromUserId.equalsIgnoreCase(SessionManager.getInstance(this).getCurrentUserID())) {
            mCallerName = getcontactname.getSendername(toUserId, msisdn);
        } else {
            mCallerName = getcontactname.getSendername(fromUserId, msisdn);
        }
        tvName.setText(mCallerName);

        isOutgoingCall = getIntent().getBooleanExtra(EXTRA_IS_OUTGOING_CALL, false);
        if (isOutgoingCall) {
            tvDuration.setText("CALLING");
            startRetryCallConnect();

            // For send busy status to third user until opponent user pick the call
            IncomingCallActivity.isStarted = true;

            String callConnectedStatus = getIntent().getExtras().getString(
                    EXTRA_CALL_CONNECT_STATUS, MessageFactory.CALL_IN_FREE + "");

            mPrevCallStatus = callConnectedStatus;
            setCallStatusText(callConnectedStatus);
            handleCallTimeout();
        } else {
            tvDuration.setText("Connecting..");
            tvCallStatus.setVisibility(View.GONE);
        }

        // Get Intent parameters.
        mRoomId = intent.getStringExtra(EXTRA_ROOMID);
        Log.d(TAG, "Room ID: " + mRoomId);
        if (mRoomId == null || mRoomId.length() == 0) {
            logAndToast(getString(org.appspot.apprtc.R.string.missing_url));
            Log.e(TAG, "Incorrect room ID in intent!");
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        boolean loopback = intent.getBooleanExtra(EXTRA_LOOPBACK, false);
        boolean tracing = intent.getBooleanExtra(EXTRA_TRACING, false);

        int videoWidth = intent.getIntExtra(EXTRA_VIDEO_WIDTH, 0);
        int videoHeight = intent.getIntExtra(EXTRA_VIDEO_HEIGHT, 0);

        screencaptureEnabled = intent.getBooleanExtra(EXTRA_SCREENCAPTURE, false);
        // If capturing format is not specified for screencapture, use screen resolution.
        if (screencaptureEnabled && videoWidth == 0 && videoHeight == 0) {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager windowManager =
                    (WindowManager) getApplication().getSystemService(Context.WINDOW_SERVICE);
            windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
            videoWidth = displayMetrics.widthPixels;
            videoHeight = displayMetrics.heightPixels;
        }
        PeerConnectionClient.DataChannelParameters dataChannelParameters = null;
        if (intent.getBooleanExtra(EXTRA_DATA_CHANNEL_ENABLED, true)) {
            dataChannelParameters = new PeerConnectionClient.DataChannelParameters(intent.getBooleanExtra(EXTRA_ORDERED, true),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS_MS, -1),
                    intent.getIntExtra(EXTRA_MAX_RETRANSMITS, -1), intent.getStringExtra(EXTRA_PROTOCOL),
                    intent.getBooleanExtra(EXTRA_NEGOTIATED, false), intent.getIntExtra(EXTRA_ID, -1));
        }
        peerConnectionParameters =
                new PeerConnectionClient.PeerConnectionParameters(intent.getBooleanExtra(EXTRA_VIDEO_CALL, true), loopback,
                        tracing, videoWidth, videoHeight, intent.getIntExtra(EXTRA_VIDEO_FPS, 0),
                        intent.getIntExtra(EXTRA_VIDEO_BITRATE, 0), intent.getStringExtra(EXTRA_VIDEOCODEC),
                        intent.getBooleanExtra(EXTRA_HWCODEC_ENABLED, true),
                        intent.getBooleanExtra(EXTRA_FLEXFEC_ENABLED, false),
                        intent.getIntExtra(EXTRA_AUDIO_BITRATE, 0), intent.getStringExtra(EXTRA_AUDIOCODEC),
                        intent.getBooleanExtra(EXTRA_NOAUDIOPROCESSING_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_AECDUMP_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_OPENSLES_ENABLED, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AEC, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_AGC, false),
                        intent.getBooleanExtra(EXTRA_DISABLE_BUILT_IN_NS, false),
                        intent.getBooleanExtra(EXTRA_ENABLE_LEVEL_CONTROL, false), dataChannelParameters);
        commandLineRun = intent.getBooleanExtra(EXTRA_CMDLINE, false);
        runTimeMs = intent.getIntExtra(EXTRA_RUNTIME, 0);

        Log.d(TAG, "VIDEO_FILE: '" + intent.getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA) + "'");

        // Create connection client. Use DirectRTCClient if room name is an IP otherwise use the
        // standard WebSocketRTCClient.
        if (loopback || !DirectRTCClient.IP_PATTERN.matcher(mRoomId).matches()) {
            appRtcClient = new WebSocketRTCClient(this,getApplicationContext());
        } else {
            Log.i(TAG, "Using DirectRTCClient because room name looks like an IP.");
            appRtcClient = new DirectRTCClient(this);
        }
        // Create connection parameters.
        roomConnectionParameters = new AppRTCClient.RoomConnectionParameters(roomUri.toString(), mRoomId, loopback);

        // Create CPU monitor
        cpuMonitor = new CpuMonitor(this);
        hudFragment.setCpuMonitor(cpuMonitor);

        // Send intent arguments to fragments.
        callFragment.setArguments(intent.getExtras());
        hudFragment.setArguments(intent.getExtras());
        // Activate call and HUD fragments and start the call.
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.add(org.appspot.apprtc.R.id.call_fragment_container, callFragment);
        ft.add(org.appspot.apprtc.R.id.hud_fragment_container, hudFragment);
        ft.commit();

        // For command line execution run connection for <runTimeMs> and exit.
        if (commandLineRun && runTimeMs > 0) {
            (new Handler()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    disconnect(false);
                }
            }, runTimeMs);
        }

        peerConnectionClient = PeerConnectionClient.getInstance();
        if (loopback) {
            PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
            options.networkIgnoreMask = 0;
            peerConnectionClient.setPeerConnectionFactoryOptions(options);
        }
        peerConnectionClient.createPeerConnectionFactory(
                CallsActivityCopy.this, peerConnectionParameters, CallsActivityCopy.this);

        if (screencaptureEnabled) {
            MediaProjectionManager mediaProjectionManager =
                    (MediaProjectionManager) getApplication().getSystemService(
                            Context.MEDIA_PROJECTION_SERVICE);
            startActivityForResult(
                    mediaProjectionManager.createScreenCaptureIntent(), CAPTURE_PERMISSION_REQUEST_CODE);
        } else {
            startCall();
        }
        EventBus.getDefault().register(this);
    }

    private void setCallStatusText(String callConnectedStatus) {

        if (mediaPlayer == null) {
//            if (callConnectedStatus.equals(MessageFactory.CALL_IN_FREE + "")) {
//                playCallerTone(R.raw.call_tone);
//            } else {
//                playCallerTone(R.raw.call_busy);
//            }
        } else if (!mPrevCallStatus.equals(callConnectedStatus)) {
//            if (callConnectedStatus.equals(MessageFactory.CALL_IN_FREE + "")) {
//                playCallerTone(R.raw.call_tone);
//            } else {
//                playCallerTone(R.raw.call_busy);
//            }
        }

        mPrevCallStatus = callConnectedStatus;

        switch (callConnectedStatus) {

            case MessageFactory.CALL_IN_FREE + "": {
                tvCallStatus.setVisibility(View.GONE);
                playCallerTone(R.raw.call_tone);
            }
            break;

            case MessageFactory.CALL_IN_RINGING + "": {
                tvCallStatus.setVisibility(View.VISIBLE);
                tvCallStatus.setText(mCallerName + " busy");
                playCallerTone(R.raw.call_busy);
            }
            break;
            case MessageFactory.CALL_IN_WAITING + "": {
                tvCallStatus.setVisibility(View.VISIBLE);
                tvCallStatus.setText(mCallerName + " in another call");
                playCallerTone(R.raw.call_busy);
            }
            break;
        }
    }

    private void playCallerTone(int resId) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }

        mediaPlayer = MediaPlayer.create(CallsActivityCopy.this, resId);
        mediaPlayer.setLooping(true);

        setMediaVolume(40);

        mediaPlayer.start();
    }

    private void setMediaVolume(int volume) {
        AudioManager audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int volumePercent = (volume * maxVolume) / 100;
        audio.setStreamVolume(AudioManager.STREAM_MUSIC, volumePercent, 0);
    }

    private void startRetryCallConnect() {
        retryCallHandler = new Handler();
        retryCallRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isArrivedToUser && !isConnectedToUser && !CallMessage.arrivedCallId.equalsIgnoreCase(mCallId)) {
                    long timeDiff = System.currentTimeMillis() - callStartedTimeMs;
                    if (timeDiff < 35 * 1000) {
                        try {
                            JSONObject object = new JSONObject();
                            object.put("from", mCurrentUserId);
                            object.put("to", toUserId);
                            object.put("recordId", mRecordId);

                            SendMessageEvent event = new SendMessageEvent();
                            event.setEventName(SocketManager.EVENT_RETRY_CALL_CONNECT);
                            event.setMessageObject(object);
                            EventBus.getDefault().post(event);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        MessageDbController dbController = CoreController.getDBInstance(CallsActivityCopy.this);
                        CallItemChat callItem = dbController.getCallStatus(mCallId);
                        if (callItem == null || callItem.getCallStatus().equals(MessageFactory.CALL_STATUS_CALLING + "")) {
                            retryCallHandler.postDelayed(retryCallRunnable, CALL_RETRY_DURATION);
                        }
                    }
                } else {
                    stopRetryCallConnect();
                }
            }
        };
        retryCallHandler.postDelayed(retryCallRunnable, CALL_RETRY_DURATION);
    }

    private void showInfoCtrls() {
        tvName.setVisibility(View.VISIBLE);
        tvCallLbl.setVisibility(View.VISIBLE);
        tvDuration.setVisibility(View.VISIBLE);
        // tvDuration.setText("");

        if (isVideoCall) {
            tvCallLbl.setText(getString(R.string.video_call_appname) + " VIDEO CALL");
        } else {
            tvCallLbl.setText(getString(R.string.video_call_appname) + " VOICE CALL");
        }

        if (isCallReconnecting) {
            tvDuration.setVisibility(View.INVISIBLE);
            tvCallStatus.setVisibility(View.VISIBLE);
            tvCallStatus.setText("Reconnecting");
        }
    }

    private void hideInfoCtrls() {
        tvName.setVisibility(View.GONE);
        tvCallLbl.setVisibility(View.GONE);
        tvDuration.setVisibility(View.GONE);
        tvCallStatus.setVisibility(View.GONE);
    }

    private void handleCallTimeout() {
        callTimeoutHandler = new Handler();
        callTimeoutRunnable = new Runnable() {
            @Override
            public void run() {
                if(!iceConnected){
                    disconnect(true);
                }

            }
        };
        callTimeoutHandler.postDelayed(callTimeoutRunnable, MISSED_CALL_TIMEOUT);
    }

    private void disableLockScreen() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Activity.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock lock = keyguardManager.newKeyguardLock(KEYGUARD_SERVICE);
        lock.disableKeyguard();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != CAPTURE_PERMISSION_REQUEST_CODE)
            return;
        mediaProjectionPermissionResultCode = resultCode;
        mediaProjectionPermissionResultData = data;
        startCall();
    }

    private boolean useCamera2() {
        return Camera2Enumerator.isSupported(this) && getIntent().getBooleanExtra(EXTRA_CAMERA2, true);
    }

    private boolean captureToTexture() {
        return getIntent().getBooleanExtra(EXTRA_CAPTURETOTEXTURE_ENABLED, false);
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    // Activity interfaces
    @Override
    public void onPause() {
        super.onPause();
        activityRunning = false;
        // Don't stop the video when using screencapture to allow user to show other apps to the remote
        // end.
        if (peerConnectionClient != null && !screencaptureEnabled) {
            peerConnectionClient.stopVideoSource();

        }
        cpuMonitor.pause();
        String callDocId = "";
        String id = "";
        if (mCallId != null) {
            String[] splitIds = mCallId.split("-");
            id = splitIds[2];
            callDocId = toUserId + "-" + fromUserId + "-" + id;
        }


        JSONObject object = CallMessage.getCallStatusObject(toUserId, fromUserId, id, callDocId, mRecordId, MessageFactory.CALL_STATUS_PAUSE,isVideoCall?"1":"0");
        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_CALL_STATUS);
        event.setMessageObject(object);
        EventBus.getDefault().post(event);
    }

    @Override
    public void onResume() {
        super.onResume();
        activityRunning = true;
        // Video is not paused for screencapture. See onPause.
        if (peerConnectionClient != null && !screencaptureEnabled) {
            peerConnectionClient.startVideoSource();
        }
        cpuMonitor.resume();
    }

    @Override
    protected void onDestroy() {

        disconnect(false);

        opponentUserId = "";
        unregisterReceiver(incomingCallReceiver);

        stopRetryCallConnect();
        stopReconnectCall();

        if (callTimeoutHandler != null && callTimeoutRunnable != null) {
            callTimeoutHandler.removeCallbacks(callTimeoutRunnable);
        }

        if (logToast != null) {
            logToast.cancel();
        }
        activityRunning = false;
        rootEglBase.release();

        isStarted = false;

        // For remove busy status once opponent user pick the call
        IncomingCallActivity.isStarted = false;

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    // CallFragment.OnCallEvents interface implementation.
    @Override
    public void onCallHangUp() {
        canEndCall = true;
        disconnect(true);
    }

    @Override
    public void onCameraSwitch() {
        if (peerConnectionClient != null) {
            peerConnectionClient.switchCamera();
            if (cameraswitched) {
                cameraswitched = false;
            } else {
                cameraswitched = true;
            }
            updateVideoView();
        }
    }

    @Override
    public void onVideoScalingSwitch(RendererCommon.ScalingType scalingType) {
        this.scalingType = scalingType;
        updateVideoView();
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

//        speakerEnabled = !speakerEnabled;

        if (audioManager.getSelectedAudioDevice().equals(AppRTCAudioManager.AudioDevice.EARPIECE)) {
            audioManager.setAudioDeviceInternal(AppRTCAudioManager.AudioDevice.SPEAKER_PHONE);
            setMediaVolume(80);
            speakerEnabled = true;
        } else {
            audioManager.setAudioDeviceInternal(AppRTCAudioManager.AudioDevice.EARPIECE);
            setMediaVolume(40);
            speakerEnabled = false;
        }

        return speakerEnabled;
    }

    @Override
    public void onPageClick() {
        if (isVideoCall) {
            if (tvName.getVisibility() == View.VISIBLE) {
                hideInfoCtrls();
            } else {
                showInfoCtrls();
            }
        }
    }

    @Override
    public void gotomsg() {
        onBackPressed();
    }

    // Helper functions.
    private void toggleCallControlFragmentVisibility() {
        if (!iceConnected || !callFragment.isAdded()) {
            return;
        }
        // Show/hide call control fragment
        callControlFragmentVisible = !callControlFragmentVisible;
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (callControlFragmentVisible) {
            disconnect_layout.setVisibility(View.VISIBLE);
            bottom_layout.setVisibility(View.VISIBLE);
            call_header.setVisibility(View.VISIBLE);

            ft.show(callFragment);
            ft.show(hudFragment);

        } else {

            disconnect_layout.setVisibility(View.GONE);
            bottom_layout.setVisibility(View.GONE);
            call_header.setVisibility(View.GONE);

            ft.hide(callFragment);
            ft.hide(hudFragment);
        }
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();
    }

    private void updateVideoView() {
        if (cameraswitched) {
            remoteRenderLayout.setPosition(REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT);
            remoteRenderScreen.setScalingType(scalingType);
            remoteRenderScreen.setMirror(false);

            // For remove busy status once opponent user pick the call
            IncomingCallActivity.isStarted = false;


            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            if (iceConnected) {
                isConnectedToUser = true;
                startTimer();
//            startNotification();

                localRenderLayout.setPosition(
                        LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED, LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED);
                localRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            } else {
                localRenderLayout.setPosition(
                        LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING);
                localRender.setScalingType(scalingType);
            }
            localRender.setMirror(false);

            localRender.requestLayout();
            remoteRenderScreen.requestLayout();
        } else {
            remoteRenderLayout.setPosition(REMOTE_X, REMOTE_Y, REMOTE_WIDTH, REMOTE_HEIGHT);
            remoteRenderScreen.setScalingType(scalingType);
            remoteRenderScreen.setMirror(false);

            // For remove busy status once opponent user pick the call
            IncomingCallActivity.isStarted = false;


            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
            }

            if (iceConnected) {

                isConnectedToUser = true;
                startTimer();

                RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) local_video_relativelayout.getLayoutParams();
                lp.addRule(RelativeLayout.ABOVE, dummy_layout.getId());
//            startNotification();

                localRenderLayout.setPosition(
                        LOCAL_X_CONNECTED, LOCAL_Y_CONNECTED, LOCAL_WIDTH_CONNECTED, LOCAL_HEIGHT_CONNECTED);
                localRender.setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FIT);
            } else {
                localRenderLayout.setPosition(
                        LOCAL_X_CONNECTING, LOCAL_Y_CONNECTING, LOCAL_WIDTH_CONNECTING, LOCAL_HEIGHT_CONNECTING);
                localRender.setScalingType(scalingType);
            }
            localRender.setMirror(true);

            localRender.requestLayout();
            remoteRenderScreen.requestLayout();
        }

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
            // This method will be called each time the number of available audio
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
        if (isVideoCall) {
            hideInfoCtrls();
        } else {

        }

        stopReconnectCall();

        long delta = System.currentTimeMillis() - callStartedTimeMs;
        Log.i(TAG, "Call connected: delay=" + delta + "ms");
        if (peerConnectionClient == null || isError) {
            Log.w(TAG, "Call is connected in closed or error state");
            return;
        }
        // Update video view.
        updateVideoView();
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
    private void disconnect(final boolean isMissedCall) {
        CallMessage.isAlreadyCallClick = false;
        if (timer != null) {
            timer.cancel();
        }

        if (needToSendServer) {
            if (!isMissedCall) {
                sendCallDisconnectToServer(isMissedCall, false);
            } else {
                sendCallDisconnectToServer(isMissedCall, true);

            }
            needToSendServer = false;
        } else {
            MessageDbController db = CoreController.getDBInstance(this);
            db.updateCallStatus(mCallId, MessageFactory.CALL_STATUS_END, getCallDuration());
        }

        activityRunning = false;
        if (appRtcClient != null) {
            appRtcClient.disconnectFromRoom();
            appRtcClient = null;
        }
        if (peerConnectionClient != null) {
            peerConnectionClient.close();
            peerConnectionClient = null;
        }
        if (localRender != null) {
            localRender.release();
            localRender = null;
        }
        if (videoFileRenderer != null) {
            videoFileRenderer.release();
            videoFileRenderer = null;
        }
        if (remoteRenderScreen != null) {
            remoteRenderScreen.release();
            remoteRenderScreen = null;
        }
        if (audioManager != null) {
            audioManager.stop();
            audioManager = null;
        }
        if (iceConnected && !isError) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sendCallDisconnectToServer(isMissedCall, false);
                if (isMissedCall)
                    if (showDisconnectNotify) {
                        Toast.makeText(CallsActivityCopy.this, "Call disconnected", Toast.LENGTH_SHORT).show();
                    }
                finish();
            }
        }, 1000);

    }

    private void disconnectWithErrorMessage(final String errorMessage) {
        if (commandLineRun || !activityRunning) {
            Log.e(TAG, "Critical error: " + errorMessage);
            disconnect(false);
        } else {
           /* new AlertDialog.Builder(this)
                    .setTitle(getText(org.appspot.apprtc.R.string.channel_error_title))
                    .setMessage(errorMessage)
                    .setCancelable(false)
                    .setNeutralButton(org.appspot.apprtc.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    disconnect(false);
                                }
                            })
                    .create()
                    .show();*/

        }
    }

    // Log |msg| and Toast about it.
    private void logAndToast(String msg) {
        Log.d(TAG, msg);
        if (logToast != null) {
            logToast.cancel();
        }
//        logToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
//        logToast.show();
    }

    private void reportError(final String description) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError) {
                    isError = true;
                    disconnectWithErrorMessage(description);
                }
            }
        });
    }

    private VideoCapturer createVideoCapturer() {
        VideoCapturer videoCapturer = null;
        String videoFileAsCamera = getIntent().getStringExtra(EXTRA_VIDEO_FILE_AS_CAMERA);
        if (videoFileAsCamera != null) {
            try {
                videoCapturer = new FileVideoCapturer(videoFileAsCamera);
            } catch (IOException e) {
                reportError("Failed to open video file for emulated camera");
                return null;
            }
        } else if (screencaptureEnabled) {
            if (mediaProjectionPermissionResultCode != Activity.RESULT_OK) {
                reportError("User didn't give permission to capture the screen.");
                return null;
            }
            return new ScreenCapturerAndroid(
                    mediaProjectionPermissionResultData, new MediaProjection.Callback() {
                @Override
                public void onStop() {
                    reportError("User revoked permission to capture the screen.");
                }
            });
        } else if (useCamera2()) {
            if (!captureToTexture()) {
                reportError(getString(org.appspot.apprtc.R.string.camera2_texture_only_error));
                return null;
            }

            Logging.d(TAG, "Creating capturer using camera2 API.");
            videoCapturer = createCameraCapturer(new Camera2Enumerator(this));
        } else {
            Logging.d(TAG, "Creating capturer using camera1 API.");
            videoCapturer = createCameraCapturer(new Camera1Enumerator(captureToTexture()));
        }
        if (videoCapturer == null) {
            reportError("Failed to open camera");
            return null;
        }
        return videoCapturer;
    }

    // -----Implementation of AppRTCClient.AppRTCSignalingEvents ---------------
    // All callbacks are invoked from websocket signaling looper thread and
    // are routed to UI thread.
    private void onConnectedToRoomInternal(final AppRTCClient.SignalingParameters params) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;

        signalingParameters = params;
        logAndToast("Creating peer connection, delay=" + delta + "ms");
        VideoCapturer videoCapturer = null;
        if (peerConnectionParameters.videoCallEnabled) {
            videoCapturer = createVideoCapturer();
        }
        peerConnectionClient.createPeerConnection(rootEglBase.getEglBaseContext(), localRender,
                remoteRenderers, videoCapturer, signalingParameters);

        if (signalingParameters.initiator) {
            logAndToast("Creating OFFER...");
            // Create offer. Offer SDP will be sent to answering client in
            // PeerConnectionEvents.onLocalDescription event.
            peerConnectionClient.createOffer();
        } else {
            if (params.offerSdp != null) {
                peerConnectionClient.setRemoteDescription(params.offerSdp);
                logAndToast("Creating ANSWER...");
                // Create answer. Answer SDP will be sent to offering client in
                // PeerConnectionEvents.onLocalDescription event.
                peerConnectionClient.createAnswer();
            }
            if (params.iceCandidates != null) {
                // Add remote ICE candidates from room.
                for (IceCandidate iceCandidate : params.iceCandidates) {
                    peerConnectionClient.addRemoteIceCandidate(iceCandidate);
                }
            }
        }
    }

    @Override
    public void onConnectedToRoom(final AppRTCClient.SignalingParameters params) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onConnectedToRoomInternal(params);
            }
        });
    }

    @Override
    public void onRemoteDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received remote SDP for non-initilized peer connection.");
                    return;
                }
                logAndToast("Received remote " + sdp.type + ", delay=" + delta + "ms");
                peerConnectionClient.setRemoteDescription(sdp);
                if (!signalingParameters.initiator) {
                    logAndToast("Creating ANSWER...");
                    // Create answer. Answer SDP will be sent to offering client in
                    // PeerConnectionEvents.onLocalDescription event.
                    peerConnectionClient.createAnswer();
                }
            }
        });
    }

    @Override
    public void onRemoteIceCandidate(final IceCandidate candidate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate for a non-initialized peer connection.");
                    return;
                }
                peerConnectionClient.addRemoteIceCandidate(candidate);
            }
        });
    }

    @Override
    public void onRemoteIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (peerConnectionClient == null) {
                    Log.e(TAG, "Received ICE candidate removals for a non-initialized peer connection.");
                    return;
                }
                peerConnectionClient.removeRemoteIceCandidates(candidates);
            }
        });
    }

    @Override
    public void onChannelClose() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("Remote end hung up; dropping PeerConnection");
                /*disconnect(false);
                showAlert(CallsActivity.this, "On channel close");*/
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        reconnectCall();
                    }
                }, 2000);
            }
        });
    }

    @Override
    public void onChannelError(final String description) {
        reportError(description);
    }

    // -----Implementation of PeerConnectionClient.PeerConnectionEvents.---------
    // Send local peer connection SDP and ICE candidates to remote party.
    // All callbacks are invoked from peer connection client looper thread and
    // are routed to UI thread.
    @Override
    public void onLocalDescription(final SessionDescription sdp) {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
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
        });


        //------------------------------Send Value to Opponent User------------------------------------

        JSONObject object = new JSONObject();
        try {
            object.put("from", mCurrentUserId);
            object.put("to", opponentUserId);
            object.put("type", Outgoing_call_type);
            object.put("id", mRoomId);
            object.put("roomid", mRoomId);
            object.put("toDocId", mCurrentUserId + "-" + opponentUserId + "-" + mRoomId);
            if (object != null) {
                SendMessageEvent callEvent = new SendMessageEvent();
                callEvent.setEventName(SocketManager.EVENT_CALL);
                callEvent.setMessageObject(object);
                EventBus.getDefault().post(callEvent);
            }

        } catch (JSONException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onIceCandidate(final IceCandidate candidate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidate(candidate);
                }
            }
        });
    }

    @Override
    public void onIceCandidatesRemoved(final IceCandidate[] candidates) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (appRtcClient != null) {
                    appRtcClient.sendLocalIceCandidateRemovals(candidates);
                }
            }
        });
    }

    @Override
    public void onIceConnected() {
        final long delta = System.currentTimeMillis() - callStartedTimeMs;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("ICE connected, delay=" + delta + "ms");
                iceConnected = true;
                callConnected();
            }
        });
    }

    @Override
    public void onIceDisconnected() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                logAndToast("ICE disconnected");
                iceConnected = false;
                canEndCall=true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (canEndCall) {
                            disconnect(false);
                        } else {
                            reconnectCall();
                        }
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onPeerConnectionClosed() {
    }

    @Override
    public void onPeerConnectionStatsReady(final StatsReport[] reports) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!isError && iceConnected) {
                    hudFragment.updateEncoderStatistics(reports);
                }
            }
        });
    }

    @Override
    public void onPeerConnectionError(final String description) {
        reportError(description);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {

        Log.d("Event_name", event.getEventName().toString());

        switch (event.getEventName()) {

            case SocketManager.EVENT_CALL_STATUS: {
                String data = event.getObjectsArray()[0].toString();
                loadCallStatusMessage(data);
                Log.d(TAG, "eventcallstatus");
            }
            break;

            case SocketManager.EVENT_DISCONNECT_CALL: {
                String data = event.getObjectsArray()[0].toString();
                loadCallStatusMessage(data);
            }
            break;

            case SocketManager.EVENT_CALL_RESPONSE: {
                String data = event.getObjectsArray()[0].toString();

                Log.d("Call_response", data);
                calldisconnect(data);
            }
            break;

            case SocketManager.EVENT_RETRY_CALL_CONNECT: {
                String data = event.getObjectsArray()[0].toString();
                loadCallRetryResponse(data);
            }
            break;


        }
    }

    private void calldisconnect(String data){
        try {
            JSONObject object = new JSONObject(data);
           JSONObject dataObj= object.getJSONObject("data");
           mRecordId = dataObj.getString("recordId");
            /*String call_status=object.getString("call_status");
            if( call_status.equals(""+MessageFactory.CALL_STATUS_REJECTED) || call_status.equals(""+MessageFactory.CALL_STATUS_END) ){
                finish();
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCallStatusMessage(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String recordId = object.getString("recordId");
            String status = object.getString("call_status");
            if (recordId.equalsIgnoreCase(mRecordId)) {
                switch (status) {
                    case MessageFactory.CALL_STATUS_END + "":
                    case MessageFactory.CALL_STATUS_REJECTED + "":
                    case MessageFactory.CALL_STATUS_RECEIVED + "":
                    case MessageFactory.CALL_STATUS_MISSED + "":
                        needToSendServer = false;
                        canEndCall = true;
                        disconnect(false);
                        break;
                    case MessageFactory.CALL_STATUS_ARRIVED + "":
                        stopRetryCallConnect();
                        break;
                    case MessageFactory.CALL_STATUS_ANSWERED + "":
                        isAnsweredToUser = true;
                        if (callTimeoutHandler != null) {
                            callTimeoutHandler.removeCallbacks(callTimeoutRunnable);
                        }
                        break;
                    case MessageFactory.CALL_STATUS_PAUSE + "":
                        logAndToast(mCallerName + " video call is paused");
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadCallRetryResponse(String data) {
        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");
            String recordId = object.getString("recordId");

            if (from.equalsIgnoreCase(mCurrentUserId) && mRecordId.equalsIgnoreCase(recordId)) {
                if (object.has("call_connect")) {
                    String callConnect = object.getString("call_connect");
                    setCallStatusText(callConnect);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void stopRetryCallConnect() {
        CallMessage.arrivedCallId = "";
        isArrivedToUser = true;
        if (retryCallRunnable != null && retryCallHandler != null) {
            retryCallHandler.removeCallbacks(retryCallRunnable);
        }
    }

    private void stopReconnectCall() {

        isCallReconnecting = false;
        if (!isVideoCall) {
            tvDuration.setVisibility(View.VISIBLE);
        } else {
            hideInfoCtrls();
        }

        if (reconnectRunnable != null && reconnectHandler != null) {
            reconnectHandler.removeCallbacks(reconnectRunnable);
            tvCallStatus.setVisibility(View.GONE);
        }
    }

    private void sendCallDisconnectToServer(boolean isMissedCall, boolean needDBUpdate) {
        MessageDbController db = CoreController.getDBInstance(this);
        JSONObject object;

        String id="";
       if(mCallId!=null){

           String[] splitIds = mCallId.split("-");
            id = splitIds[2];
       }


        if (isOutgoingCall) {

            if (isConnectedToUser || isAnsweredToUser) {
                object = CallMessage.getCallStatusObject(fromUserId, toUserId, id, mCallId, mRecordId, MessageFactory.CALL_STATUS_END,isVideoCall?"1":"0");
                if (needDBUpdate) {
                    db.updateCallStatus(mCallId, MessageFactory.CALL_STATUS_END, getCallDuration());
                }
            } else if (isMissedCall) {
                object = CallMessage.getCallStatusObject(fromUserId, toUserId, id, mCallId, mRecordId, MessageFactory.CALL_STATUS_MISSED,isVideoCall?"1":"0");
                if (needDBUpdate) {
                    db.updateCallStatus(mCallId, MessageFactory.CALL_STATUS_MISSED, getCallDuration());
                }
            } else {
                object = CallMessage.getCallStatusObject(fromUserId, toUserId, id, mCallId, mRecordId, MessageFactory.CALL_STATUS_REJECTED,isVideoCall?"1":"0");
                if (needDBUpdate) {
                    db.updateCallStatus(mCallId, MessageFactory.CALL_STATUS_REJECTED, getCallDuration());
                }
            }
        } else {
            String callDocId = toUserId + "-" + fromUserId + "-" + id;
            object = CallMessage.getCallStatusObject(toUserId, fromUserId, id, callDocId, mRecordId, MessageFactory.CALL_STATUS_END,isVideoCall?"1":"0");
            if (needDBUpdate) {
                db.updateCallStatus(mCallId, MessageFactory.CALL_STATUS_END, getCallDuration());
            }
        }

        SendMessageEvent event = new SendMessageEvent();

        if (isMissedCall) {
            event.setEventName(SocketManager.EVENT_CALL_STATUS);
            //sendAckToServer(object);
        } else if (needDBUpdate) {
            event.setEventName(SocketManager.EVENT_CALL_STATUS);
        } else {
            event.setEventName(SocketManager.EVENT_CALL_STATUS);
        }
        event.setMessageObject(object);
        EventBus.getDefault().post(event);


    }


    private void sendAckToServer(JSONObject object) {
        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_CALL_STATUS);
        event.setMessageObject(object);
        EventBus.getDefault().post(event);
    }

    private void startTimer() {
        if (timer == null) {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    callDuration++;
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tvDuration.setText(getCallDuration());
                        }
                    });
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

    /*private void startNotification() {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(ns);

        Notification notification = new Notification(R.drawable.ic_launcher, null,
                System.currentTimeMillis());

        RemoteViews notificationView = new RemoteViews(getPackageName(),
                R.layout.av_call_notification);

        //the intent that is started when the notification is clicked (works)
        Intent notificationIntent = new Intent(this, VideoCallActivity.class);
        PendingIntent pendingNotificationIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        notification.contentView = notificationView;
        notification.contentIntent = pendingNotificationIntent;
        notification.flags |= Notification.FLAG_NO_CLEAR;

        //this is the intent that is supposed to be called when the
        //button is clicked
        Intent switchIntent = new Intent(this, VideoCallActivity.class);
        PendingIntent pendingSwitchIntent = PendingIntent.getBroadcast(this, 0,
                switchIntent, 0);

        notificationView.setOnClickPendingIntent(R.id.closeOnFlash,
                pendingSwitchIntent);

        notificationManager.notify(1, notification);
    }*/

    @Override
    public void onBackPressed() {

        //super.onBackPressed();
        /*Class backActivity;

        String navigateFrom = getIntent().getExtras().getString(EXTRA_NAVIGATE_FROM, "");

        if (ChatViewActivity.class.getSimpleName().equals(navigateFrom) && !ChatViewActivity.isKilled) {
            backActivity = ChatViewActivity.class;
        } else if (CallHistoryActivity.class.getSimpleName().equals(navigateFrom)) {
            backActivity = CallHistoryActivity.class;
        } else if (CallInfoActivity.class.getSimpleName().equals(navigateFrom) && !CallInfoActivity.isKilled) {
            backActivity = CallInfoActivity.class;
        } else {
            backActivity = HomeScreen.class;
        }

        Intent backIntent = new Intent(CallsActivity.this, backActivity);
//        backIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(backIntent);*/
    }

    private void reconnectCall() {

        // Reset timer for once reconnected call time starts
       /* if(timer != null) {
            timer.cancel();
            timer = null;
        }*/

        if (reconnectRunnable == null) {
            reconnectHandler = new Handler();
            reconnectRunnable = new Runnable() {
                @Override
                public void run() {
                    disconnect(false);
                }
            };
        }

        long timeout = RECONNECT_CALL_TIMEOUT;
        if (!ConnectivityInfo.isInternetConnected(this)) {
            timeout = OFFLINE_RECONNECT_CALL_TIMEOUT;
        }

        if (!canEndCall) {
            isCallReconnecting = true;
            showInfoCtrls();
        }

        reconnectHandler.postDelayed(reconnectRunnable, timeout);

        if (appRtcClient != null) {
            appRtcClient.disconnectFromRoom();
            appRtcClient.connectToRoom(roomConnectionParameters);
        }


    }

    BroadcastReceiver incomingCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            long currentCallTS = 0, newCallTS = 0;
            if (mCallTs != null && !mCallTs.equals("0")) {
                currentCallTS = AppUtils.parseLong(mCallTs);
            }

            String strNewCallTS = intent.getStringExtra(EXTRA_CALL_TIME_STAMP);
            if (strNewCallTS != null && !strNewCallTS.equals("0")) {
                newCallTS = AppUtils.parseLong(strNewCallTS);
            }

            String from = intent.getStringExtra(EXTRA_FROM_USER_ID);
            if (newCallTS > currentCallTS && from.equalsIgnoreCase(toUserId)) {
                String callId = intent.getStringExtra(EXTRA_DOC_ID);
                String fromMsisdn = intent.getStringExtra(EXTRA_USER_MSISDN);
                String to = intent.getStringExtra(EXTRA_TO_USER_ID);
                String roomId = intent.getStringExtra(EXTRA_DOC_ID);
                boolean isVideoCall = intent.getBooleanExtra(EXTRA_VIDEO_CALL, false);

               /* CallMessage.openCallScreen(context, from, to, callId, roomId, "", fromMsisdn,
                        MessageFactory.CALL_IN_FREE + "", isVideoCall, false, strNewCallTS);*/
                showDisconnectNotify = false;
                finish();
            }

            Log.d("Broadcastincoming", strNewCallTS + "---" + currentCallTS);
        }
    };


    //-----------------------------------New Code-----------------------------------------

    private boolean ToggleMic() {
        if (peerConnectionClient != null) {
            micEnabled = !micEnabled;
            peerConnectionClient.setAudioEnabled(micEnabled);
        }
        return micEnabled;
    }

    private boolean SpeakerOn() {
        if (audioManager.getSelectedAudioDevice().equals(AppRTCAudioManager.AudioDevice.EARPIECE)) {
            audioManager.setAudioDeviceInternal(AppRTCAudioManager.AudioDevice.SPEAKER_PHONE);
            setMediaVolume(80);
            speakerEnabled = true;
        } else {
            audioManager.setAudioDeviceInternal(AppRTCAudioManager.AudioDevice.EARPIECE);
            setMediaVolume(40);
            speakerEnabled = false;
        }

        return speakerEnabled;
    }

}

