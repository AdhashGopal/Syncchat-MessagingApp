package com.chatapp.synchat.app.calls;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.app.HomeScreen;
import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.core.ShortcutBadgeManager;
import com.chatapp.synchat.core.service.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.ChatappSettings;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.utils.BlockUserUtils;
import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.RecyclerViewItemClickListener;
import com.chatapp.synchat.app.utils.TimeStampUtils;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.IncomingMessage;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.CallItemChat;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.appspot.apprtc.CallActivity;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import static android.Manifest.permission.RECORD_AUDIO;

/**
 * created by  Adhash Team on 7/28/2017.
 */
public class CallHistoryFragment extends Fragment implements View.OnClickListener, RecyclerViewItemClickListener {
    private static final String TAG = CallHistoryFragment.class.getSimpleName() + ">>>@@";
    private View view;
    private RecyclerView rvCalls;
    private ImageButton ibNewCall;
    private TextView tvNoCalls;
    private SearchView searchView;
    private final int AUDIO_RECORD_PERMISSION_REQUEST_CODE = 14;
    private Getcontactname getcontactname;
    private CallHistoryAdapter adapter;

    private ArrayList<CallItemChat> callsList;
    private String mCurrentUserId, unBlockedUserId = "";
    private boolean needRefresh;

    private Gson gson;
    private GsonBuilder gsonBuilder;
    private CoreActivity mActivity;

    private final int REQUEST_CODE_REMOVE_CALL_LOG = 1;


    /**
     * check fragment state
     *
     * @param isVisibleToUser fragmetnt visible state
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            ShortcutBadgeManager mgnr = new ShortcutBadgeManager(getActivity());
            mgnr.removeCallCount();
            HomeScreen activity = (HomeScreen) getActivity();
            activity.changeTabTextCount();
            // Refresh your fragment here
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_call_history, container, false);

        initView();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initData();
            }
        }, 500);


        return view;
    }


    /**
     * binding widget id's
     */
    private void initView() {
        rvCalls = (RecyclerView) view.findViewById(R.id.rvCalls);
        ibNewCall = (ImageButton) view.findViewById(R.id.ibNewCall);
        tvNoCalls = (TextView) view.findViewById(R.id.tvNoCalls);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        rvCalls.setLayoutManager(manager);

        mActivity = ((CoreActivity) getActivity());
        mActivity.initProgress("Loading...", true);

        ibNewCall.setOnClickListener(this);

        view.findViewById(R.id.btnCall).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CallsActivity.isStarted) {
                    CallMessage.resumeCall(getActivity());
                }
            }
        });
        //new
        checkAudioRecordPermission();

        setHasOptionsMenu(true);
    }

    /**
     * get data
     */
    private void initData() {
        refreshCallsList();
        mCurrentUserId = SessionManager.getInstance(getActivity()).getCurrentUserID();

        getcontactname = new Getcontactname(getActivity());

        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();

        new loadCallHistoryFromDBAsyntask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }


    /**
     * CallHistory From Database Asyntask
     */
    private class loadCallHistoryFromDBAsyntask extends AsyncTask<String, Void, String> {
        public loadCallHistoryFromDBAsyntask() {
        }

        @Override
        protected String doInBackground(String... URL) {
            try {
                loadCallHistoryFromDB();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return "";
        }

        @Override
        protected void onPostExecute(String result) {
        }
    }


    /**
     * get value from database (CallHistory)
     */
    private void loadCallHistoryFromDB() {
        if (getActivity() != null) {
            MessageDbController db = CoreController.getDBInstance(getActivity());
            callsList = db.selectAllCalls(mCurrentUserId);
            Collections.sort(callsList, new CallsListSorter());
            callsList = removeArrivedStatusItems();
            callsList = filterCallsList();
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    // UI code goes here
                    if (callsList.size() == 0) {
                        tvNoCalls.setVisibility(View.VISIBLE);
                    } else {
                        tvNoCalls.setVisibility(View.GONE);
                    }
                    ;
                    adapter = new CallHistoryAdapter(getActivity(), callsList);
                    adapter.setOnItemClickListener(CallHistoryFragment.this);
                    rvCalls.setAdapter(adapter);
                }
            });

        }
    }


    /**
     * remove Arrived StatusItems
     *
     * @return value
     */
    private ArrayList<CallItemChat> removeArrivedStatusItems() {
        ArrayList<CallItemChat> removedItems = new ArrayList<>();
        for (int i = 0; i < callsList.size(); i++) {
            CallItemChat callItem = callsList.get(i);
            if (!callItem.getCallStatus().equals(MessageFactory.CALL_STATUS_ARRIVED + "")) {
                removedItems.add(callItem);
            }
        }
        return removedItems;
    }

    /**
     * filterCallsList
     *
     * @return value
     */
    private ArrayList<CallItemChat> filterCallsList() {
        ArrayList<CallItemChat> tempList = new ArrayList<>();

        CallItemChat prevItem = null;
        for (int i = 0; i < callsList.size(); i++) {

            CallItemChat currentItem = callsList.get(i);
            if (prevItem != null && prevItem.isSelf() == currentItem.isSelf()
                    && prevItem.getOpponentUserId().equalsIgnoreCase(currentItem.getOpponentUserId())
                    && prevItem.getCallType().equals(currentItem.getCallType())
                    && getCallStatus(prevItem, currentItem) && isDateEqual(prevItem, currentItem)) {
                tempList.add(currentItem);
            } else {
                prevItem = currentItem;
                callsList.set(i, prevItem);
            }
            prevItem = mergeCallItems(prevItem, currentItem);
        }

        for (int i = 0; i < tempList.size(); i++) {
            callsList.remove(tempList.get(i));
        }

        return callsList;
    }

    /**
     * isDateEqual
     *
     * @param prevItem    input value
     * @param currentItem input value
     * @return value
     */
    private boolean isDateEqual(CallItemChat prevItem, CallItemChat currentItem) {
        Date prevItemDate = TimeStampUtils.getMessageTStoDate(getActivity(), prevItem.getTS());
        Date currentItemDate = TimeStampUtils.getMessageTStoDate(getActivity(), currentItem.getTS());
        return prevItemDate != null && currentItemDate != null && prevItemDate.equals(currentItemDate);
    }


    /**
     * mergeCallItems
     *
     * @param prevItem    getting value from model class
     * @param currentItem getting value from model class
     * @return value
     */
    private CallItemChat mergeCallItems(CallItemChat prevItem, CallItemChat currentItem) {
        try {
            String callAtObj = prevItem.getCalledAtObj();

            // Add call details from current item to old item
            JSONArray arrTimes;
            if (callAtObj == null || callAtObj.equals("")) {
                arrTimes = new JSONArray();
            } else {
                arrTimes = new JSONArray(callAtObj);
            }
            String strObj = gson.toJson(currentItem);
            arrTimes.put(strObj);

            prevItem.setCallCount(arrTimes.length());
            prevItem.setCalledAtObj(arrTimes.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return prevItem;
    }

    /**
     * getCallStatus
     *
     * @param prevItem    getting value from model class
     * @param currentItem getting value from model class
     * @return value
     */
    private boolean getCallStatus(CallItemChat prevItem, CallItemChat currentItem) {
        if (prevItem.isSelf() && currentItem.isSelf()) {
            return true;
        } else {
            boolean con1 = prevItem.getCallStatus().equals(MessageFactory.CALL_STATUS_REJECTED + "");
            boolean con2 = prevItem.getCallStatus().equals(MessageFactory.CALL_STATUS_MISSED + "");
            boolean con3 = con1 || con2;

            boolean con4 = currentItem.getCallStatus().equals(MessageFactory.CALL_STATUS_REJECTED + "");
            boolean con5 = currentItem.getCallStatus().equals(MessageFactory.CALL_STATUS_MISSED + "");
            boolean con6 = con4 || con5;

            return con3 == con6;
        }
    }

    /**
     * load database
     */
    public void refreshCallsList() {
        loadCallHistoryFromDB();
    }

    /**
     * CallsListSorter
     */
    private class CallsListSorter implements Comparator<CallItemChat> {

        @Override
        public int compare(CallItemChat item1, CallItemChat item2) {
            long item1Time = Long.parseLong(item1.getTS());
            long item2Time = Long.parseLong(item2.getTS());

            if (item1Time < item2Time) {
                return 1;
            } else {
                return -1;
            }
        }
    }

    /**
     * click action for specific view
     *
     * @param view action
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ibNewCall: {
                Intent intent = new Intent(getActivity(), CallsContactActivity.class);
                startActivity(intent);
                needRefresh = true;
            }
            break;
        }
    }

    /**
     * click action for specific view
     *
     * @param parentView view
     * @param position   specific value
     */
    @Override
    public void onRVItemClick(View parentView, int position) {

        switch (parentView.getId()) {

            case R.id.ibCall: {
                Log.d(TAG, "on call icon Click: ");
                CallItemChat callItem;
                try {
                    callItem = (CallItemChat) parentView.getTag(R.string.data);
                } catch (Exception e) {
                    callItem = callsList.get(position);
                }
                String toUserId = callItem.getOpponentUserId();
                String receiverName = getcontactname.getSendername(toUserId, callItem.getOpponentUserMsisdn());

                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getActivity());

                if (contactDB_sqlite.getBlockedStatus(toUserId, false).equals("1")) {
                    String msg = "Unblock " + receiverName + " to place a " +
                            getString(R.string.app_name) + " call";
                    displayAlert(msg, toUserId);
                } else {
                    activecall(callItem);
                }
            }
            break;

            case R.id.rlParent: {
                Log.d(TAG, "on call icon Click: ");
                CallItemChat callItem;
                try {
                    callItem = (CallItemChat) parentView.getTag(R.string.data);
                } catch (Exception e) {
                    callItem = callsList.get(position);
                }
                String toUserId = callItem.getOpponentUserId();
                String receiverName = getcontactname.getSendername(toUserId, callItem.getOpponentUserMsisdn());

                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getActivity());

                if (contactDB_sqlite.getBlockedStatus(toUserId, false).equals("1")) {
                    String msg = "Unblock " + receiverName + " to place a " +
                            getString(R.string.app_name) + " call";
                    displayAlert(msg, toUserId);
                } else {
                    activecall(callItem);
                }
//                CallItemChat callItem;
//                try {
//                    callItem = (CallItemChat) parentView.getTag(R.string.data);
//                } catch (Exception e) {
//                    callItem = callsList.get(position);
//                }
//                Intent intent = new Intent(getActivity(), CallInfoActivity.class);
//                intent.putExtra(CallInfoActivity.KEY_CALL_ITEM, callItem);
//                startActivityForResult(intent, REQUEST_CODE_REMOVE_CALL_LOG);
            }
            break;

        }
    }

    /**
     * activecall
     * @param callItem getting value from model class
     */
    private void activecall(CallItemChat callItem) {
        if (ConnectivityInfo.isInternetConnected(getActivity())) {
            if (checkAudioRecordPermission()) {
                CallMessage message = new CallMessage(getActivity());
                boolean isOutgoingCall = true;

                JSONObject object = (JSONObject) message.getMessageObject(callItem.getOpponentUserId(), Integer.parseInt(callItem.getCallType()));


                String roomid = null;
                String timestamp = null;
                try {
                    roomid = object.getString("id");
                    timestamp = object.getString("id");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String callid = mCurrentUserId + "-" + callItem.getOpponentUserId() + "-" + timestamp;
//        CallMessage.openCallScreen(ChatViewActivity.this, mCurrentUserId, receiverUid, callid,
//                roomid, "", receiverMsisdn, "999", isVideoCall, true, timestamp);


                if (!CallsActivity.isStarted) {
                    if (isOutgoingCall) {
                        CallsActivity.opponentUserId = callItem.getOpponentUserId();
                    }


//                    if (object != null) {
//                        SendMessageEvent callEvent = new SendMessageEvent();
//                        callEvent.setEventName(SocketManager.EVENT_CALL);
//                        callEvent.setMessageObject(object);
//                        EventBus.getDefault().post(callEvent);
//                    }

                    PreferenceManager.setDefaultValues(getActivity(), org.appspot.apprtc.R.xml.preferences, false);
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    String keyprefRoomServerUrl = getActivity().getString(org.appspot.apprtc.R.string.pref_room_server_url_key);
                    String roomUrl = sharedPref.getString(
                            keyprefRoomServerUrl, Constants.SOCKET_IP_BASE);

                    boolean isVideoCall = false;
                    if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
                        isVideoCall = true;
                    }


                    int videoWidth = 0;
                    int videoHeight = 0;
                    String resolution = getActivity().getString(org.appspot.apprtc.R.string.pref_resolution_default);
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
                    Uri uri = Uri.parse(roomUrl);
                    Intent intent = new Intent(getActivity(), CallsActivity.class);
//            Intent intent = new Intent(context, CallNotifyService.class);

                    intent.setData(uri);
                    intent.putExtra(CallsActivity.EXTRA_IS_OUTGOING_CALL, isOutgoingCall);
                    intent.putExtra(CallsActivity.EXTRA_DOC_ID, callid);
                    intent.putExtra(CallsActivity.EXTRA_FROM_USER_ID, mCurrentUserId);
                    intent.putExtra(CallsActivity.EXTRA_TO_USER_ID, callItem.getOpponentUserId());
                    intent.putExtra(CallsActivity.EXTRA_USER_MSISDN, callItem.getOpponentUserMsisdn());
                    intent.putExtra(CallsActivity.EXTRA_OPPONENT_PROFILE_PIC, "");
                    intent.putExtra(CallsActivity.EXTRA_NAVIGATE_FROM, getActivity().getClass().getSimpleName()); // For navigating from call activity
                    intent.putExtra(CallsActivity.EXTRA_CALL_CONNECT_STATUS, "0");
                    intent.putExtra(CallsActivity.EXTRA_CALL_TIME_STAMP, timestamp);

                    intent.putExtra(CallsActivity.EXTRA_ROOMID, roomid);
                    intent.putExtra(CallsActivity.EXTRA_LOOPBACK, false);
                    intent.putExtra(CallsActivity.EXTRA_VIDEO_CALL, isVideoCall);
                    intent.putExtra(CallsActivity.EXTRA_SCREENCAPTURE, false);
                    intent.putExtra(CallsActivity.EXTRA_CAMERA2, true);
                    intent.putExtra(CallsActivity.EXTRA_VIDEO_WIDTH, videoWidth);
                    intent.putExtra(CallsActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
                    intent.putExtra(CallsActivity.EXTRA_VIDEO_FPS, 0);
                    intent.putExtra(CallsActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED, false);
                    intent.putExtra(CallsActivity.EXTRA_VIDEO_BITRATE, 0);
                    intent.putExtra(CallsActivity.EXTRA_VIDEOCODEC, getActivity().getString(org.appspot.apprtc.R.string.pref_videocodec_default));
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
                    intent.putExtra(CallsActivity.EXTRA_AUDIOCODEC, getActivity().getString(org.appspot.apprtc.R.string.pref_audiocodec_default));
                    intent.putExtra(CallsActivity.EXTRA_DISPLAY_HUD, false);
                    intent.putExtra(CallsActivity.EXTRA_TRACING, false);
                    intent.putExtra(CallsActivity.EXTRA_CMDLINE, false);
                    intent.putExtra(CallsActivity.EXTRA_RUNTIME, 0);

                    intent.putExtra(CallActivity.EXTRA_DATA_CHANNEL_ENABLED, true);
                    intent.putExtra(CallActivity.EXTRA_ORDERED, true);
                    intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS_MS, -1);
                    intent.putExtra(CallActivity.EXTRA_MAX_RETRANSMITS, -1);
                    intent.putExtra(CallActivity.EXTRA_PROTOCOL, getActivity().getString(org.appspot.apprtc.R.string.pref_data_protocol_default));
                    intent.putExtra(CallActivity.EXTRA_NEGOTIATED, false);
                    intent.putExtra(CallActivity.EXTRA_ID, -1);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);


                }

            } else {
                requestAudioRecordPermission();
            }
        } else {
            Toast.makeText(getContext(), "Please check your internet connection", Toast.LENGTH_SHORT).show();

        }


    }

    private void performCall(CallItemChat callItem) {
        Log.d(TAG, "performCall: ");
        if (AppUtils.isNetworkAvailable(getActivity())) {
            if (checkAudioRecordPermission()) {
                if (!CallMessage.isAlreadyCallClick) {

                    String toUserId = callItem.getOpponentUserId();

                    CallMessage message = new CallMessage(getActivity());
                    JSONObject object = (JSONObject) message.getMessageObject(toUserId, Integer.parseInt(callItem.getCallType()));

                    if (object != null) {
                        SendMessageEvent callEvent = new SendMessageEvent();
                        callEvent.setEventName(SocketManager.EVENT_CALL);
                        callEvent.setMessageObject(object);
                        EventBus.getDefault().post(callEvent);
                    }

                    CallMessage.setCallClickTimeout();
                    //open call page immediately
                    boolean isVideoCall = false;
                    if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
                        isVideoCall = true;
                    }
                    try {
                        String to = object.getString("to");
                        String toUserAvatar = "";

                        MessageDbController db = CoreController.getDBInstance(getActivity());
                        db.updateCallLogs(callItem);

                        String ts = "";
                        CallMessage.openCallScreen(getActivity(), mCurrentUserId, to, callItem.getCallId(),
                                callItem.getRecordId(), toUserAvatar, callItem.getOpponentUserMsisdn(),
                                MessageFactory.CALL_IN_FREE + "", isVideoCall, true, ts);
                    } catch (Exception e) {
                        Log.e(TAG, "performCall: ", e);
                    }
                } else {
                    Toast.makeText(getActivity(), "Call in progress", Toast.LENGTH_SHORT).show();
                }
            } else {
                requestAudioRecordPermission();
            }

            Log.d(TAG, "performCall: end");
        } else {
            Toast.makeText(getActivity(), getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * getting permission for audio
     */
    private void requestAudioRecordPermission() {
        /*ActivityCompat.requestPermissions(getActivity(), new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, AUDIO_RECORD_PERMISSION_REQUEST_CODE);*/
        ActivityCompat.requestPermissions(getActivity(), new
                String[]{RECORD_AUDIO}, AUDIO_RECORD_PERMISSION_REQUEST_CODE);
    }

    /**
     * check Audio Record Permission
     * @return value
     */
    public boolean checkAudioRecordPermission() {
       /* int result = ContextCompat.checkSelfPermission(getActivity(),
                WRITE_EXTERNAL_STORAGE);*/
        int result1 = ContextCompat.checkSelfPermission(getActivity(),
                RECORD_AUDIO);
        return /*result == PackageManager.PERMISSION_GRANTED &&*/
                result1 == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Call Recevice Message
     * @param event based on the value connect incoming call
     */
    private void loadCallResMessage(ReceviceMessageEvent event) {
        Object[] obj = event.getObjectsArray();
        try {
            Log.d(TAG, "loadCallResMessage: ");
            JSONObject object = new JSONObject(obj[0].toString());
            JSONObject callObj = object.getJSONObject("data");

            String from = callObj.getString("from");
            String callStatus = callObj.getString("call_status");
            if (from.equalsIgnoreCase(mCurrentUserId) && callStatus.equals(MessageFactory.CALL_STATUS_CALLING + "")) {
                String to = callObj.getString("to");

                IncomingMessage incomingMsg = new IncomingMessage(getActivity());
                CallItemChat callItem = incomingMsg.loadOutgoingCall(callObj);

                boolean isVideoCall = false;
                if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
                    isVideoCall = true;
                }

                String toUserAvatar = callObj.getString("To_avatar") + "?=id" + Calendar.getInstance().getTimeInMillis();

                MessageDbController db = CoreController.getDBInstance(getActivity());
                db.updateCallLogs(callItem);

                String ts = callObj.getString("timestamp");

//                CallMessage.openCallScreen(getActivity(), mCurrentUserId, to, callItem.getCallId(),
//                        callItem.getRecordId(), toUserAvatar, callItem.getOpponentUserMsisdn(),
//                        MessageFactory.CALL_IN_FREE + "", isVideoCall, true, ts);
                needRefresh = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Eventbus value
     * @param event corresponding response to call soclet (call response,block user, remove call,call state)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        switch (event.getEventName()) {

            case SocketManager.EVENT_CALL_RESPONSE:
                loadCallResMessage(event);
                break;

            case SocketManager.EVENT_BLOCK_USER:
                loadBlockEventMessage(event);
                break;

            case SocketManager.EVENT_REMOVE_ALL_CALLS:
                loadRemoveCallHistory(event);
                break;

            case SocketManager.EVENT_CALL:
                loadIncomingCallMessage(event);
                break;
        }

    }

    /**
     * Incoming CallMessage
     * @param event based on value enable refresh data true
     */
    private void loadIncomingCallMessage(ReceviceMessageEvent event) {
        try {
            String data = event.getObjectsArray()[0].toString();
            JSONObject callObj = new JSONObject(data);

            String to = callObj.getString("to");
            if (to.equalsIgnoreCase(mCurrentUserId)) {
                needRefresh = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * Remove Call History
     * @param event updated local database based on response
     */
    private void loadRemoveCallHistory(ReceviceMessageEvent event) {

        String data = event.getObjectsArray()[0].toString();

        try {
            JSONObject object = new JSONObject(data);
            String from = object.getString("from");

            if (from.equalsIgnoreCase(mCurrentUserId)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mActivity.hideProgressDialog();
                        loadCallHistoryFromDB();
                    }
                }, 1000);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Block Event Message
     * @param event check user blocked or not
     */
    private void loadBlockEventMessage(ReceviceMessageEvent event) {

        try {
            Object[] obj = event.getObjectsArray();
            JSONObject object = new JSONObject(obj[0].toString());

            String status = object.getString("status");
            String to = object.getString("to");
            String from = object.getString("from");

            if (mCurrentUserId.equalsIgnoreCase(from) && to.equalsIgnoreCase(unBlockedUserId)) {
                mActivity.hideProgressDialog();

                if (status.equalsIgnoreCase("1")) {
                    Toast.makeText(getActivity(), "Contact is blocked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Contact is Unblocked", Toast.LENGTH_SHORT).show();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Enabling the blocked user alert
     * @param msg getting message from response
     * @param toUserId userid
     */
    private void displayAlert(String msg, final String toUserId) {
        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setMessage(msg);
        dialog.setNegativeButtonText("Cancel");
        dialog.setPositiveButtonText("Unblock");
        dialog.setCancelable(false);
        unBlockedUserId = toUserId;

        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                mActivity.hideProgressDialog();

                BlockUserUtils.changeUserBlockedStatus(getActivity(), EventBus.getDefault(),
                        mCurrentUserId, toUserId, false);
                dialog.dismiss();
            }

            @Override

            public void onNegativeButtonClick() {
                dialog.dismiss();
            }
        });
        dialog.show(getFragmentManager(), "UnBlock a person");

    }

    /**
     * Start EventBus
     */
    @Override
    public void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }


    /**
     * Stop Eventbus
     */
    @Override
    public void onStop() {
        super.onStop();


        EventBus.getDefault().unregister(this);
    }


    /**
     * getting value from activity
     * @param requestCode based on requestCode to call local db call history
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_REMOVE_CALL_LOG) {
            loadCallHistoryFromDB();
        }
    }


    /**
     * Create OptionsMenu & search contact
     * @param menu binding menu id's
     * @param inflater binding layout view
     */
    @Override
    public void onCreateOptionsMenu(final Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_call_history, menu);

        MenuItem searchItem = menu.findItem(R.id.menuSearch);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
               /* searchView.setIconifiedByDefault(true);
                searchView.setIconified(true);
                searchView.setQuery("", false);
                searchView.clearFocus();*/
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (newText.equals("") && newText.isEmpty()) {
                        searchView.clearFocus();
                    }
                    adapter.getFilter().filter(newText);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }
        });

        SearchManager searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView.setIconifiedByDefault(true);
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);

        final AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        searchTextView.setTextColor(Color.WHITE);
        searchTextView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideKeyboard(searchTextView);
                    return true;
                }
                return false;
            }
        });
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /***
     * hide keyboard
     * @param searchTextView
     */
    public void hideKeyboard(AutoCompleteTextView searchTextView) {

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(searchTextView.getWindowToken(), 0);

    }


    /**
     * Select option menu
     * @param item select specific item,
     * @return value
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menuClearCallLog: {
                if (ConnectivityInfo.isInternetConnected(getActivity())) {
                    if (callsList != null && callsList.size() > 0) {
                        performClearCallLogs();
                    }
                } else {
                    Toast.makeText(getActivity(), "Check your internet connection", Toast.LENGTH_SHORT).show();
                }
            }
            break;

            case R.id.menuSettings: {
                Intent intent = new Intent(getActivity(), ChatappSettings.class);
                startActivity(intent);
            }
            break;

        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Clear call logs function
     */
    private void performClearCallLogs() {
        mActivity.showProgressDialog();

        try {
            JSONObject object = new JSONObject();
            object.put("from", mCurrentUserId);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_REMOVE_ALL_CALLS);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * MAintain tha UI view * call local database for call history
     */
    @Override
    public void onResume() {
        super.onResume();

        if (needRefresh) {
            loadCallHistoryFromDB();
            needRefresh = false;
        }
    }
}
