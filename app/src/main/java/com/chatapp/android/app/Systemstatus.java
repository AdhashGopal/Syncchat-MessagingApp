package com.chatapp.android.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatapp.android.R;
import com.chatapp.android.core.CoreActivity;

public class Systemstatus extends CoreActivity {
    ImageView backimg_s_status;
    TextView title, content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_status);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        backimg_s_status = (ImageView) findViewById(R.id.backarrow_system_status);

        getSupportActionBar().hide();
        backimg_s_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityLauncher.launchSettingScreen(Systemstatus.this);
                finish();
            }
        });

    }
}
