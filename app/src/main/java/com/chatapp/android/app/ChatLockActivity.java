package com.chatapp.android.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProRegButton;
import com.chatapp.android.core.CoreActivity;

/**
 * created by  Adhash Team on 4/21/2017.
 */
public class ChatLockActivity extends CoreActivity implements View.OnClickListener {
    AvnNextLTProRegButton button_setPwd;
    EditText new_pwd, cnfrm_pwd;
    String newPwd, cnfrmPwd;
    ImageView back_navigator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_lock);
        button_setPwd = (AvnNextLTProRegButton) findViewById(R.id.button_setPwd);
        button_setPwd.setOnClickListener(this);
        new_pwd = (EditText) findViewById(R.id.newPassword_Et);
        cnfrm_pwd = (EditText) findViewById(R.id.confirmPassword_Et);
        back_navigator = (ImageView) findViewById(R.id.back_navigator);
        back_navigator.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_setPwd) {

            newPwd = new_pwd.getText().toString();
            cnfrmPwd = cnfrm_pwd.getText().toString();
            if (newPwd.contains(" ")) {
                Toast.makeText(ChatLockActivity.this, "Your password can't contain spaces.", Toast.LENGTH_SHORT).show();
            }
            if (newPwd.equals("") || cnfrmPwd.equals("")) {
                Toast.makeText(ChatLockActivity.this, "Field must not be empty", Toast.LENGTH_SHORT).show();
            } else if (!newPwd.equals(cnfrmPwd)) {
                Toast.makeText(ChatLockActivity.this, "Password doesn't match", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ChatLockActivity.this, PasswordEmailActivity.class);
                startActivity(intent);
            }

        } else if (v.getId() == R.id.back_navigator) {
            finish();
        }
    }
}