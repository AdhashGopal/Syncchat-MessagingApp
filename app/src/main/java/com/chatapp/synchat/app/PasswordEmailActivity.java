package com.chatapp.synchat.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.core.CoreActivity;

/**
 * created by  Adhash Team on 4/26/2017.
 */
public class PasswordEmailActivity extends CoreActivity {

    ImageView back_navigator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.password_mail);
        back_navigator = (ImageView) findViewById(R.id.back_navigator);

        back_navigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
