package com.chatapp.android.app;

import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatapp.android.R;

import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.SessionManager;

import java.util.ArrayList;
import java.util.List;

import me.philio.pinentry.PinEntryView;


public class ChangePinActivity extends CoreActivity {

    List<EditText> editTexts = new ArrayList<>();
    EditText otpedit, otpedit1, otpedit2, otpedit3, currentlyFocusedEditText;
    int j = 0;
    int pincounter=0;
    String lastotp="";
    InputMethodManager imm;

    TextView enter_pintext;
    private PinEntryView pinEntryView;
    ImageView back_arrow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_change);

//        imm = (InputMethodManager) getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        pinEntryView = (PinEntryView) findViewById(R.id.pin_entry_simple);

        enter_pintext=(TextView)findViewById(R.id.enter_pintext);
        back_arrow=(ImageView)findViewById(R.id.back_arrow);
        back_arrow.setVisibility(View.VISIBLE);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        /**
         * Updated the pin entry
         */
        pinEntryView.setOnPinEnteredListener(new PinEntryView.OnPinEnteredListener() {
            @Override
            public void onPinEntered(final String pin) {
                if(pin.equals(SessionManager.getInstance(ChangePinActivity.this).getUserMpin()) || pincounter==1 || pincounter==2)
                {
                    if(pincounter==0)
                    {

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                enter_pintext.setText("Enter New PIN");
                                pincounter=1;
                                pinEntryView.setText("");
                            }
                        }, 500);


                    }
                    else if(pincounter==1)
                    {
                        if(pin.equals(SessionManager.getInstance(ChangePinActivity.this).getUserMpin()))
                        {
                            pinEntryView.setText("");
                            Toast.makeText(ChangePinActivity.this, "Existing PIN , enter new one", Toast.LENGTH_SHORT).show();
                        }
                        else {

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    enter_pintext.setText("Confirm PIN");
                                    pincounter = 2;
                                    lastotp = pin;
                                    pinEntryView.setText("");
                                }
                            }, 500);
                        }

                    }

                    else
                    {
                        if(lastotp.equals(pin))
                        {

                            SessionManager.getInstance(ChangePinActivity.this).setUsermpin(pin);

                            finish();
                            Toast.makeText(ChangePinActivity.this, "Your PIN has been changed successfully", Toast.LENGTH_SHORT).show();


                        }

                        else
                        {

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    enter_pintext.setText("Enter New PIN");

                                    pincounter=1;
                                    pinEntryView.setText("");

                                    Toast.makeText(ChangePinActivity.this, "PIN didn't match. Enter again.", Toast.LENGTH_SHORT).show();

                                }
                            }, 500);
                        }
                    }

                }
                else
                {

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            enter_pintext.setText(getResources().getString(R.string.wrong_pin));
                            pinEntryView.setText("");
                            Toast.makeText(ChangePinActivity.this, getResources().getString(R.string.wrong_pin), Toast.LENGTH_SHORT).show();

                        }
                    }, 500);


                }
            }
        });
    }


}