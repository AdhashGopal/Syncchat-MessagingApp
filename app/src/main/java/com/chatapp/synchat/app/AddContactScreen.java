package com.chatapp.synchat.app;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiButton;
import com.chatapp.synchat.app.widget.AvnNextLTProRegEditText;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Administrator on 10/26/2016.
 */
public class AddContactScreen extends CoreActivity {


    private AvnNextLTProRegEditText name, phoneNumber;
    private AvnNextLTProDemiButton save, cancel;

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        Toast.makeText(this, "Contact EVENT " + event.getEventName(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contact_screen);
        setTitle("Add Contact");
        name = (AvnNextLTProRegEditText) findViewById(R.id.name);
        phoneNumber = (AvnNextLTProRegEditText) findViewById(R.id.phoneNumber);
        save = (AvnNextLTProDemiButton) findViewById(R.id.save);
        cancel = (AvnNextLTProDemiButton) findViewById(R.id.cancel);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneNumber.getText().length() > 0) {
                    addContactTodataBase();
                } else {
                    Toast.makeText(getApplicationContext(), "Phone Number Cannot be Empty", Toast.LENGTH_SHORT).show();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addContactTodataBase() {
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_ADD_CONTACT);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("msisdn", SessionManager.getInstance(this).getPhoneNumberOfCurrentUser());
            jsonObject.put("favourite", phoneNumber.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageEvent.setMessageObject(jsonObject);
        EventBus.getDefault().post(messageEvent);
    }
}
