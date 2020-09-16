package com.chatapp.synchat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.core.CoreActivity;

public class Account_main_list extends CoreActivity {

    RelativeLayout privacy, security, changenumber, deletemyaccount;
    ImageView backpress;
    AvnNextLTProDemiTextView privacy_text, security_text, change_num_text, delete_text, text_actionbar_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accountmain);
        getSupportActionBar().hide();

        privacy_text = (AvnNextLTProDemiTextView) findViewById(R.id.account_txt1);
        security_text = (AvnNextLTProDemiTextView) findViewById(R.id.account_txt2);
        change_num_text = (AvnNextLTProDemiTextView) findViewById(R.id.account_txt3);
        delete_text = (AvnNextLTProDemiTextView) findViewById(R.id.account_txt4);

        backpress = (ImageView) findViewById(R.id.backarrow_account);
        privacy = (RelativeLayout) findViewById(R.id.account_r2);
        security = (RelativeLayout) findViewById(R.id.account_r3);
        changenumber = (RelativeLayout) findViewById(R.id.account_r4);
        deletemyaccount = (RelativeLayout) findViewById(R.id.account_r5);

        privacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_privacy = new Intent(getApplicationContext(), ChatappPrivacy.class);
                startActivity(intent_privacy);

            }
        });
        security.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_security = new Intent(getApplicationContext(), Security.class);
                startActivity(intent_security);
            }
        });
        changenumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_change = new Intent(getApplicationContext(), ChangeNumber.class);
                startActivity(intent_change);
            }
        });
        deletemyaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_del = new Intent(getApplicationContext(), DeleteAccount.class);
                startActivity(intent_del);

            }
        });
        // Get ListView object from xml

        backpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ActivityLauncher.launchSettingScreen(Account_main_list.this);
                finish();
            }
        });
    }

}





