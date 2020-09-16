package com.chatapp.synchat.app;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreActivity;

/**
 * created by  Adhash Team on 11/18/2016.
 */
public class ChangeNumber extends CoreActivity {
    ImageView backimg;
    TextView text1, text2, text3;
    AvnNextLTProDemiTextView changenumber_actionbar_1, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_number);

        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        backimg = (ImageView) findViewById(R.id.backarrow_changenumber);
        text1 = (TextView) findViewById(R.id.changenumber_1sttext);
        text2 = (TextView) findViewById(R.id.changenumber_2ndtext);
        text3 = (TextView) findViewById(R.id.changenumber_3rdtext);
        getSupportActionBar().hide();

        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        next = (AvnNextLTProDemiTextView) findViewById(R.id.next_changenumber);
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchChangenumber2(ChangeNumber.this);
            }
        });

        changenumber_actionbar_1 = (AvnNextLTProDemiTextView) findViewById(R.id.changenumber_actionbar_1);

    }
}
