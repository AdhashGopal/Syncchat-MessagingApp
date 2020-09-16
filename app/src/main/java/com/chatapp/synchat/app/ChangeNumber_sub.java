package com.chatapp.synchat.app;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.SessionManager;

/**
 * created by  Adhash Team on 11/22/2016.
 */
public class ChangeNumber_sub extends CoreActivity {
    ImageView backimg;
    EditText phoneNumber, phoneNumber2;
    TextView changenumber_actionbar_1, next_changenumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_number_sub);
        backimg = (ImageView) findViewById(R.id.backarrow_changenumber);
       getSupportActionBar().hide();

        changenumber_actionbar_1 = (TextView) findViewById(R.id.changenumber_actionbar_1);
        next_changenumber = (TextView) findViewById(R.id.next_changenumber);
        phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        phoneNumber2 = (EditText) findViewById(R.id.phoneNumber2);

        phoneNumber.setText(SessionManager.getInstance(ChangeNumber_sub.this).getPhoneNumberOfCurrentUser());
        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
