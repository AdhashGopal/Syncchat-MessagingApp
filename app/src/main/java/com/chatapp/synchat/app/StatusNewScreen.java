package com.chatapp.synchat.app;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.core.CoreController;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


public class StatusNewScreen extends FragmentActivity {

    /*
    `* This code takes in status input from the user and returns the status back
    `* to the Status class where it is updated on the server
    */

    private Button okBtn, cancleBtn;
    private EmojiconEditText et;
    private TextView textSize, statusCharCount;
    EmojIconActions emojIcon;
    private boolean status;

    /* Global variables for empjis */
    ImageView happyFace;
    ImageView backnavigate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_new);
       // setTitle("Add new status");

//        getWindow().setSoftInputMode(
//                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        // Enabled SoftKey
        /*getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);*/
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE
                        | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        backnavigate = (ImageView) findViewById(R.id.backnavigate);

        backnavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et = (EmojiconEditText) findViewById(R.id.editText_addStatus);
        et.addTextChangedListener(myTextEditorWatcher);
        textSize = (TextView) findViewById(R.id.textSize);
        statusCharCount = (TextView) findViewById(R.id.statusCharCount);

        Typeface typeface = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        et.setTypeface(typeface);

        Intent i = getIntent();
        et.setText(i.getStringExtra("STATUS"));
        et.setSelection(et.getText().length());

        int aLength = 139 - et.getText().toString().trim().length();
        statusCharCount.setText(String.valueOf(aLength));

        okBtn = (Button) findViewById(R.id.okAddNewStatus);

        /* Return the status to the Status Class */
        okBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (ConnectivityInfo.isInternetConnected(getApplication())) {
                    String message = et.getText().toString().trim();
                    if (message.length() > 0) {
                        Intent intent = new Intent();
                        intent.putExtra("MESSAGE", message);
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        Toast.makeText(StatusNewScreen.this, "Profile status can't be empty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(StatusNewScreen.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        cancleBtn = (Button) findViewById(R.id.cancleAddNewStatus);
        cancleBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();

            }
        });

        happyFace = (ImageView) findViewById(R.id.happyFace);
//        setEmojiconFragment(false);


        et.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /* remove the emojis in case edit text is clicked */
                status = !status;
            }
        });


        View rootView = findViewById(R.id.addatatusmainlayout);
        emojIcon = new EmojIconActions(this, rootView, et, happyFace);
//        emojIcon.setUseSystemEmoji(false);
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.ShowEmojIcon();

        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e("", "Keyboard opened!");

            }

            @Override
            public void onKeyboardClose() {
                Log.e("", "Keyboard Closed!");
//                emojIcon.ShowEmojIcon();

            }
        });
    }

    protected void hideKeyboard() {
        if (getCurrentFocus() != null) {
            final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    private final TextWatcher myTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
           /* int aStrVal = 0;
            System.out.println("-----count----" + count);
            int aLength = et.getText().toString().trim().length();
            int bLength = et.getText().toString().length();*/

            int aCount = s.length();

            displayCount(aCount);

           /* if(count==1){
             aStrVal = count;
            }else{
                aStrVal = 139 -count;
            }
            //statusCharCount.setText(aStrVal);
            System.out.println("--------strVal--" + aStrVal);

            System.out.println("--------length without--"+aLength+"---with--"+bLength);*/
        }

        @Override
        public void afterTextChanged(Editable s) {
            //textSize.setText(String.valueOf(138 - s.length()));
        }
    };

    private void displayCount(int count) {
        int aStrVal = 0;

        if (count == 0) {
            statusCharCount.setText("139");
        } else {
            int aVal = 139 - count;
            statusCharCount.setText(String.valueOf(aVal));
        }
        System.out.println("----length---" + aStrVal);
    }


    private void setEmojiconFragment(boolean useSystemDefault) {
        getSupportFragmentManager()
                .beginTransaction()
                // .replace(com.layer.atlas.R.id.emojicons, EmojiconsFragment.newInstance(useSystemDefault))
                .commit();
    }


    @Override
    public void onBackPressed() {
        if (status) {
            /* Remove the emojicon */
            status = !status;
        } else {
            super.onBackPressed();
        }
    }


}
