package com.chatapp.synchat.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiButton;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconsPopup;


/**
 * created by  Adhash Team on 11/29/2016.
 */
public class ChangeGroupName extends CoreActivity implements View.OnClickListener {

    private EmojiconEditText etGroupName;
    private ImageButton ibBack;
    private ImageView ibSmiley;

    private AvnNextLTProRegTextView tvCount;
    private AvnNextLTProDemiButton btnCancel, btnOk;
    private FrameLayout emoji;
    private EmojiconsPopup popup;
    EmojIconActions emojIcon;
    private String newGroupName;
    private String mCurrentUserId, mGroupId, mOldName;
    private boolean status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_group_name);
        getSupportActionBar().hide();

        initView();
        initData();
    }

    /**
     * layout view binding
     */
    private void initView() {
        etGroupName = (EmojiconEditText) findViewById(R.id.etGroupName);

        Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        etGroupName.setTypeface(face);
        etGroupName.addTextChangedListener(groupNameWatcher);

        tvCount = (AvnNextLTProRegTextView) findViewById(R.id.tvCount);

        etGroupName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* remove the emojis in case edit text is clicked */
                status = !status;
                emoji.setVisibility(View.GONE);
            }
        });

        ibSmiley = (ImageView) findViewById(R.id.ibSmiley);

        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ibBack.setOnClickListener(this);

        btnCancel = (AvnNextLTProDemiButton) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(ChangeGroupName.this);

        btnOk = (AvnNextLTProDemiButton) findViewById(R.id.btnOk);
        btnOk.setOnClickListener(ChangeGroupName.this);

        emoji = (FrameLayout) findViewById(R.id.emojicons);
        setEmojiconFragment(false);

        View rootView = findViewById(R.id.llTitle);
        emojIcon = new EmojIconActions(this, rootView, etGroupName, ibSmiley);

        emojIcon.ShowEmojIcon();

        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("", "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("", "Keyboard Closed!");
            }
        });

    }

    /**
     * group name with Emoji
     */
    TextWatcher groupNameWatcher = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence cs, int a, int b, int c) {
            // TODO Auto-generated method stub
            int countgroupname = 25 - cs.length();
            tvCount.setText(String.valueOf(countgroupname));
        }
    };

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                // .replace(com.layer.atlas.R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }

    /**
     * binding the data
     */
    private void initData() {
        Bundle getBundle = getIntent().getExtras();
        mGroupId = getBundle.getString("GroupId", "");
        mOldName = getBundle.getString("GroupName", "");

        etGroupName.setText(mOldName);

        mCurrentUserId = SessionManager.getInstance(ChangeGroupName.this).getCurrentUserID();
    }

    /**
     * checking the internet connection
     * @return true / false for internet connection
     */
    private Boolean internetcheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }

    /**
     * clicking action
     * @param view specific view of widget id
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.btnCancel:
                Intent cancelIntent = new Intent();
                cancelIntent.putExtra("NameChanged", false);
                setResult(RESULT_CANCELED, cancelIntent);
                finish();
                break;

            case R.id.btnOk:
                newGroupName = etGroupName.getText().toString().trim();
                if (internetcheck()) {
                    if (newGroupName.equals("")) {
                        Toast.makeText(ChangeGroupName.this, "Please enter group name", Toast.LENGTH_SHORT).show();
                    } else {
                        performChangeGroupName(newGroupName);
                    }
                } else {
                    Toast.makeText(ChangeGroupName.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.ibBack:
                finish();
                break;


        }

    }

    /**
     * update group name
     * @param newGroupName input value (newGroupName)
     */
    private void performChangeGroupName(String newGroupName) {

        long ts = Calendar.getInstance().getTimeInMillis();
        String msgId = mCurrentUserId + "-" + mGroupId + "-g-" + ts;

        String deviceId = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);


        try {
            JSONObject object = new JSONObject();
            object.put("groupType", SocketManager.ACTION_CHANGE_GROUP_NAME);
            object.put("from", mCurrentUserId);
            object.put("groupId", mGroupId);
            object.put("groupNewName", newGroupName);
            object.put("id", ts);
            object.put("toDocId", msgId);
            object.put("DeviceId", deviceId);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_GROUP);
            event.setMessageObject(object);
            EventBus.getDefault().post(event);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * EventBus data
     * @param event based on value to call socket (Group, Change group name)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP)) {

            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                String groupAction = object.getString("groupType");

                if (groupAction.equalsIgnoreCase(SocketManager.ACTION_CHANGE_GROUP_NAME)) {
                    loadChangeGroupNameMessage(object);
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }

        }
    }

    /**
     * Change Group Name
     * @param object getting value from server response
     */
    private void loadChangeGroupNameMessage(JSONObject object) {
        try {
            String err = object.getString("err");
            if (err.equalsIgnoreCase("0")) {
                Intent okIntent = new Intent();
                okIntent.putExtra("NameChanged", true);
                okIntent.putExtra("newGroupName", newGroupName);
                setResult(RESULT_OK, okIntent);
                finish();
            }

        } catch (JSONException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Start EventBus function
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(ChangeGroupName.this);
    }

    /**
     * Stop EventBus function
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(ChangeGroupName.this);
    }


    /**
     * Kill the current activity
     */
    @Override
    public void onBackPressed() {
        if (status) {
            /* Remove the emojicon */
            status = !status;
            emoji.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }
}
