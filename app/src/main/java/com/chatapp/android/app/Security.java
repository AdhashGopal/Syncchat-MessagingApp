package com.chatapp.android.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatapp.android.R;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.service.Constants;

public class Security extends CoreActivity {
    ImageView backimg_sec;
    TextView text1, text2, text3, text4, text_actionbar_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        Typeface custom_font = CoreController.getInstance().getAvnNextLTProRegularTypeface();

        text1 = (TextView) findViewById(R.id.security_text1);
        text2 = (TextView) findViewById(R.id.security_text2);
        text3 = (TextView) findViewById(R.id.security_text3);
        text4 = (TextView) findViewById(R.id.security_text4);
        text1.setTypeface(custom_font);
        text2.setTypeface(custom_font);
        text3.setTypeface(custom_font);
        text4.setTypeface(custom_font);
        backimg_sec = (ImageView) findViewById(R.id.backarrow_security_account);
        getSupportActionBar().hide();

        text_actionbar_1 = (TextView) findViewById(R.id.text_actionbar_1);
        text_actionbar_1.setTypeface(custom_font);
        text1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(Constants.SOCKET_IP + "security"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        backimg_sec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityLauncher.launchAccount(Security.this);
                finish();
            }
        });
    }
}
