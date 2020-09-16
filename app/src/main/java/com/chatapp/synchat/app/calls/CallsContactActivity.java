package com.chatapp.synchat.app.calls;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.ChatappContactsService;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.utils.BlockUserUtils;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.RecyclerViewItemClickListener;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.IncomingMessage;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.CallItemChat;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static android.Manifest.permission.RECORD_AUDIO;

/**
 * created by  Adhash Team on 7/28/2017.
 */
public class CallsContactActivity extends CoreActivity {

    private static final String TAG = "CallsContactActivity";
    private final int AUDIO_RECORD_PERMISSION_REQUEST_CODE = 14;
    List<ChatappContactModel> values = new ArrayList<ChatappContactModel>();
    private List<ChatappContactModel> ChatappEntries = new ArrayList<>();
    private RecyclerView myCallContacts;
    private CallContactAdapter myAdapter;
    private ImageView mySearchIMG;
    private EditText etSearch;
    private ImageView myMainBackLAY, mySearchBackLAY;
    private TextView myHeaderTitle, myContactsCount;
    private ProgressDialog progressDialog;
    private Session session;
    private String mCurrentUserId, toUserId;
    private Handler eventHandler;
    private Runnable eventRunnable;
    private int callItemType;
    private int callItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_contacts_listview);
        initialize();
        initData();
    }


    /**
     * clicking action (audio / video call)
     * search the specific contact
     */
    private void onItemClicks() {
        myAdapter.setCallContactsItemClickListener(new RecyclerViewItemClickListener() {
            @Override
            public void onRVItemClick(View parentView, int position) {

                switch (parentView.getId()) {

                    case R.id.layout_inflater_contacts_items_callIMG:
                        performCall(position, MessageFactory.audio_call);
                        break;

                    case R.id.layout_inflater_contacts_items_videocallIMG:
                        performCall(position, MessageFactory.video_call);
                        break;
                }
            }
        });

        mySearchIMG.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performContactsSearch();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });


        myMainBackLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hideKeyboard();
                finish();
            }
        });

        etSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                try {
                    if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                        if (myAdapter != null) {
                            myAdapter.getFilter().filter(etSearch.getText().toString());
                            return false;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return true;
            }
        });


    }


    /**
     * Checking the runtime permission
     *
     * @param requestCode  requestCode based validate the permission
     * @param permissions
     * @param grantResults based on the grantResults check permission grant or not
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case AUDIO_RECORD_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        performCall(callItemPosition, callItemType);
                    }
                }
                break;
        }
    }

    /**
     * make a call
     *
     * @param position specific person
     * @param callType type of call (audio / video)
     */
    private void performCall(int position, int callType) {
        callItemPosition = position;
        callItemType = callType;
        if (checkAudioRecordPermission()) {
            try {
                if (!CallMessage.isAlreadyCallClick) {
                    toUserId = ChatappEntries.get(position).get_id();
                    String msisdn = ChatappEntries.get(position).getMsisdn();

                    Getcontactname getcontactname = new Getcontactname(this);
                    String receiverName = getcontactname.getSendername(toUserId, msisdn);

                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
                    if (contactDB_sqlite.getBlockedStatus(toUserId, false).equals("1")) {
                        String msg = "Unblock " + receiverName + " to place a " +
                                getString(R.string.app_name) + " call";
                        displayAlert(msg);
                    } else {
                        CallMessage message = new CallMessage(CallsContactActivity.this);
                        JSONObject object = (JSONObject) message.getMessageObject(toUserId, callType);

                        if (object != null) {
                            SendMessageEvent callEvent = new SendMessageEvent();
                            callEvent.setEventName(SocketManager.EVENT_CALL);
                            callEvent.setMessageObject(object);
                            EventBus.getDefault().post(callEvent);
                            CallMessage.setCallClickTimeout();
                        }
                    }
                } else {
                    Toast.makeText(CallsContactActivity.this, "Call in progress", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "performCall: ", e);
            }
        } else {
            requestAudioRecordPermission();
        }
    }

    /**
     * request Audio Record Permission
     */
    private void requestAudioRecordPermission() {
       /* ActivityCompat.requestPermissions(CallsContactActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, AUDIO_RECORD_PERMISSION_REQUEST_CODE);*/
        ActivityCompat.requestPermissions(CallsContactActivity.this, new
                String[]{RECORD_AUDIO}, AUDIO_RECORD_PERMISSION_REQUEST_CODE);
    }

    /**
     * display the unblock Alert (person)
     *
     * @param msg getting message from response
     */
    private void displayAlert(String msg) {
        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setMessage(msg);
        dialog.setNegativeButtonText("Cancel");
        dialog.setPositiveButtonText("Unblock");
        dialog.setCancelable(false);

        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                if (!progressDialog.isShowing()) {
                    progressDialog.show();
                }

                BlockUserUtils.changeUserBlockedStatus(CallsContactActivity.this, EventBus.getDefault(),
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


    /**
     * specific contact search for call list
     */
    private void performContactsSearch() {

        showSearchActions();
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                try {


                    // When user changed the Text
                    if (myAdapter != null) {
                        if (cs.length() > 0) {
                            if (cs.length() == 1) {
                                etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.cancel_normal, 0);
                            }
                            myAdapter.getFilter().filter(cs);
                        } else {
                            etSearch.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                            myAdapter.updateInfo(ChatappEntries);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
                // TODO Auto-generated method stub
            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
        mySearchBackLAY.setVisibility(View.VISIBLE);
        myMainBackLAY.setVisibility(View.GONE);

        mySearchBackLAY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                etSearch.getText().clear();
              /*  if (myAdapter != null) {
                    myAdapter.updateInfo(ChatappEntries);
                }*/
                etSearch.setVisibility(View.GONE);
                mySearchIMG.setVisibility(View.VISIBLE);
                myContactsCount.setVisibility(View.VISIBLE);
                myHeaderTitle.setVisibility(View.VISIBLE);
                mySearchBackLAY.setVisibility(View.GONE);
                myMainBackLAY.setVisibility(View.VISIBLE);

            }
        });

    }

    /**
     * layout view action
     */
    private void showSearchActions() {
        mySearchBackLAY.setVisibility(View.VISIBLE);
        mySearchIMG.setVisibility(View.GONE);
        myMainBackLAY.setVisibility(View.GONE);
        myHeaderTitle.setVisibility(View.GONE);
        myContactsCount.setVisibility(View.GONE);
        //contact1_RelativeLayout.setVisibility(View.GONE);
        etSearch.setVisibility(View.VISIBLE);
        etSearch.requestFocus();
    }


    /**
     * binding the data
     */
    private void initData() {

        session = new Session(this);
        mCurrentUserId = SessionManager.getInstance(this).getCurrentUserID();

        ChatappEntries.clear();
        myAdapter = new CallContactAdapter(CallsContactActivity.this, ChatappEntries);

        loadContactsFromDB();
        if (ChatappEntries != null && ChatappEntries.size() == 0) {
            progressDialog.setMessage("Loading...");
            progressDialog.show();

            progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialogInterface) {
                    if (eventHandler != null) {
                        eventHandler.removeCallbacks(eventRunnable);
                    }
                }
            });

            ChatappContactsService.startContactService(this, false);
            setEventTimeout();
        }
    }


    /**
     * make a call timeout session
     */
    private void setEventTimeout() {
        if (eventHandler == null) {
            eventHandler = new Handler();
            eventRunnable = new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        Toast.makeText(CallsContactActivity.this, "Try again", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                        if (ChatappEntries != null && ChatappEntries.size() == 0) {
                            loadContactsFromDB();
                        }
                    }
                }
            };
        }

        eventHandler.postDelayed(eventRunnable, SocketManager.CONTACT_REFRESH_TIMEOUT);
    }

    /**
     * load from database for Contacts
     */
    private void loadContactsFromDB() {
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        values = contactDB_sqlite.getSavedChatappContacts();


        for (int i = 0; i < values.size(); i++) {
            if (values.get(i).getRequestStatus().equals("3")) {
                ChatappEntries.add(values.get(i));
            }
        }
        myContactsCount.setText(ChatappEntries.size() + " Contacts");

        if (ChatappEntries != null && ChatappEntries.size() > 0) {
            Collections.sort(ChatappEntries, Getcontactname.nameAscComparator);
            myAdapter.updateInfo(ChatappEntries);
            myAdapter.notifyDataSetChanged();
            myCallContacts.setVisibility(View.VISIBLE);
            //contact_empty.setVisibility(View.GONE);
            myCallContacts.setAdapter(myAdapter);
            onItemClicks();
        } else {
            //contact_empty.setVisibility(View.VISIBLE);
            myCallContacts.setVisibility(View.GONE);
            //contact_empty.setText("No Contacts Available for chat");
        }
    }

    /**
     * binding the widget
     */
    private void initialize() {
        myCallContacts = (RecyclerView) findViewById(R.id.activity_call_contacts_Viewitems);
        mySearchIMG = (ImageView) findViewById(R.id.activity_call_contacts_listview_searchIMG);
        etSearch = (EditText) findViewById(R.id.activity_call_contacts_listview_etSearch);
        myMainBackLAY = (ImageView) findViewById(R.id.activity_call_contacts_listview_backIMG);
        mySearchBackLAY = (ImageView) findViewById(R.id.activity_call_contacts_listview_backIMG_search);
        myHeaderTitle = (TextView) findViewById(R.id.activity_call_contacts_listview_headerName);
        myContactsCount = (TextView) findViewById(R.id.activity_call_contacts_listview_contactCount);

        LinearLayoutManager mediaManager = new LinearLayoutManager(CallsContactActivity.this, LinearLayoutManager.VERTICAL, false);
        myCallContacts.setLayoutManager(mediaManager);

        progressDialog = getProgressDialogInstance();
        progressDialog.setMessage("Loading...");

        etSearch.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if (event.getAction() == MotionEvent.ACTION_UP && etSearch.getCompoundDrawables()[DRAWABLE_RIGHT] != null) {
                    if (event.getRawX() >= (etSearch.getRight() - etSearch.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        etSearch.setText("");
                        return false;
                    }
                }
                return false;
            }
        });
    }


    /**
     * Getting value from eventbus
     *
     * @param event based on the value to call socket (call response, block user, get contact)
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

            case SocketManager.EVENT_GET_CONTACTS: {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        loadContactsFromDB();
                    }
                }, 3000);
            }
            break;
        }

    }

    /**
     * to perform block / unblock contact event
     *
     * @param event getting value from model class for specific user
     */
    private void loadBlockEventMessage(ReceviceMessageEvent event) {

        try {
            Object[] obj = event.getObjectsArray();
            JSONObject object = new JSONObject(obj[0].toString());

            String status = object.getString("status");
            String to = object.getString("to");
            String from = object.getString("from");

            if (mCurrentUserId.equalsIgnoreCase(from) && to.equalsIgnoreCase(toUserId)) {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }

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


    /**
     * Make a call / call status event & update from database
     *
     * @param event getting value from model class for specific user
     */
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

                IncomingMessage incomingMsg = new IncomingMessage(CallsContactActivity.this);
                CallItemChat callItem = incomingMsg.loadOutgoingCall(callObj);

                boolean isVideoCall = false;
                if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
                    isVideoCall = true;
                }

                String toUserAvatar = callObj.getString("To_avatar") + "?=id" + Calendar.getInstance().getTimeInMillis();

                MessageDbController db = CoreController.getDBInstance(this);
                db.updateCallLogs(callItem);

                String ts = callObj.getString("timestamp");

                CallMessage.openCallScreen(CallsContactActivity.this, mCurrentUserId, to, callItem.getCallId(),
                        callItem.getRecordId(), toUserAvatar, callItem.getOpponentUserMsisdn(), callConnect,
                        isVideoCall, true, ts);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Start Eventbus performance
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Stop Eventbus performance
     */
    @Override
    public void onStop() {
        super.onStop();
        hideKeyboard();
        EventBus.getDefault().unregister(this);
    }


    /**
     * Kill the current activity & hide the keyboard
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideKeyboard();
    }


    /**
     * Kill the current activity & hide the keyboard
     */
    @Override
    public void onBackPressed() {
        if (etSearch.getVisibility() == View.GONE) {
            hideKeyboard();
            super.onBackPressed();
        } else {
            mySearchBackLAY.performClick();
        }
    }
}
