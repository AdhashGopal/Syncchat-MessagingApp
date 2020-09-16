package com.chatapp.synchat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.chatapp.synchat.R;
import com.chatapp.synchat.core.CoreActivity;

/**
 * Created by Administrator on 10/11/2016.
 */
public class WelcomeScreen extends CoreActivity {
    Button continue_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);
        setTitle(R.string.welcome_title);
        init();
    }

    private void init() {
        continue_btn = (Button) findViewById(R.id.continueToChats);
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomeScreen.this, HomeScreen.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }
}
