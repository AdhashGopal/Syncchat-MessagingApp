package com.chatapp.android.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.chatapp.android.R;
import com.chatapp.android.app.utils.ConnectivityInfo;
import com.chatapp.android.app.widget.AvnNextLTProDemiButton;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


/**
 * Created by Administrator on 10/13/2016.
 */
public class ChangeNameScreen extends CoreActivity implements View.OnClickListener {


    private AvnNextLTProDemiButton okBtn, cancleBtn;
    private EmojiconEditText et;
    private AvnNextLTProRegTextView textSize;
    private ImageButton ibBack;
    EmojIconActions emojIcon;
    private boolean status;
    private String mCurrentUserId;
    FrameLayout emoji;
    ImageView happyFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_name);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mCurrentUserId = SessionManager.getInstance(ChangeNameScreen.this).getCurrentUserID();


        // Enabled SoftKey
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ibBack.setOnClickListener(ChangeNameScreen.this);

        et = (EmojiconEditText) findViewById(R.id.editText_addStatus);
        Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        et.setTypeface(face);

        et.addTextChangedListener(myTextEditorWatcher);
        textSize = (AvnNextLTProRegTextView) findViewById(R.id.textSize);
        Intent i = getIntent();
        et.setText(i.getStringExtra("name"));
        okBtn = (AvnNextLTProDemiButton) findViewById(R.id.okAddNewStatus);

        /* Return the status to the Status Class */
        okBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (et.getText().toString().trim().length() == 0) {
                    Toast.makeText(ChangeNameScreen.this, getResources().getString(R.string.empty_profile_name_alert), Toast.LENGTH_LONG).show();
                } else if (ConnectivityInfo.isInternetConnected(ChangeNameScreen.this)) {
                    SendMessageEvent event = new SendMessageEvent();
                    event.setEventName(SocketManager.EVENT_CHANGE_USER_NAME);
                    String mCurrentuname = et.getText().toString();

                    try {
                        JSONObject object = new JSONObject();
                        object.put("from", mCurrentUserId);
                   /* byte[] data = (et.getText().toString()).getBytes();
                    String base64 = Base64.encodeToString(data, Base64.DEFAULT);*/
                        object.put("name", mCurrentuname);
                        event.setMessageObject(object);
                        EventBus.getDefault().post(event);
                    } catch (JSONException ex) {
                        ex.printStackTrace();
                    }
                } else {
                    Toast.makeText(ChangeNameScreen.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();

                }
            }
        });
        cancleBtn = (AvnNextLTProDemiButton) findViewById(R.id.cancleAddNewStatus);
        cancleBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        emoji = (FrameLayout) findViewById(R.id.emojicons);
        happyFace = (ImageView) findViewById(R.id.happyFace);
        setEmojiconFragment(false);

        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /* remove the emojis in case edit text is clicked */
                status = !status;
                emoji.setVisibility(View.GONE);
            }
        });


        final View rootView = findViewById(R.id.addatatusmainlayout);
        emojIcon = new EmojIconActions(this, rootView, et, happyFace);
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
     * EventBus data
     *
     * @param event based on value to call socket (change user name)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CHANGE_USER_NAME)) {
            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                String err = object.getString("err");

                // byte[] uname=name.getBytes();

                if (err.equals("0")) {

                    String from = object.getString("from");

                    if (from.equalsIgnoreCase(mCurrentUserId)) {
                        String name = object.getString("name");
                        byte[] data = Base64.decode(name, Base64.DEFAULT);
                        String text = null;
                        try {
                            text = new String(data, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        System.out.print("--------data-------" + text);
                        SessionManager.getInstance(ChangeNameScreen.this).setnameOfCurrentUser(text);
                        Intent intent = new Intent();
                        intent.putExtra("name", true);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }

            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

    }

    /**
     * Start EventBus function
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(ChangeNameScreen.this);
    }

    /**
     * Stop EventBus function
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(ChangeNameScreen.this);
    }


    /**
     * Getting user name
     */
    private final TextWatcher myTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            textSize.setText(String.valueOf(139 - s.length()));
        }
    };

    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                // .replace(com.layer.atlas.R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }


    /**
     * Kill the currrent activity
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


    /**
     * Clicking action
     *
     * @param view specific view action
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ibBack:
                finish();
                break;

        }

    }
}
