package com.chatapp.synchat.app;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.client.Socket;

/**
 * created by  Adhash Team on 11/18/2016.
 */
public class ChatappPrivacy extends CoreActivity {
    ImageView backimg;
    LinearLayout Linearprivacy1, Linearprivacy2, Linearprivacy3, block_layout;
    TextView lastseen, profilepic, status, lastseen_head, profilepic_head, status_head, block_list_count;
    final Context context = this;
    CheckBox checkbox2;
    private SessionManager sessionManager;
    private Session session;
    private String mCurrentUserId;
    TextView head1, head2, info1, info2, block, block_sub, readreceipts;
    ArrayList<String> mydata = new ArrayList<>();
   /* SharedPreferences pref = getApplicationContext().getSharedPreferences("MyPref", 0); // 0 - for private mode
    SharedPreferences.Editor editor = pref.edit();*/

    private final int BLOCK_USER_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);
        sessionManager = SessionManager.getInstance(ChatappPrivacy.this);
        session = new Session(ChatappPrivacy.this);
        final Typeface custom_font = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        final Typeface custom_font2 = CoreController.getInstance().getAvnNextLTProDemiTypeface();
        mCurrentUserId = SessionManager.getInstance(this).getCurrentUserID();

        block_list_count = (TextView) findViewById(R.id.block_list_count);
        lastseen_head = (TextView) findViewById(R.id.text_last_seen_data);
        profilepic_head = (TextView) findViewById(R.id.text_propic);
        status_head = (TextView) findViewById(R.id.text_status);

        head2 = (TextView) findViewById(R.id.text_call_setting);
        info1 = (TextView) findViewById(R.id.info1);
        info2 = (TextView) findViewById(R.id.info2);
        block = (TextView) findViewById(R.id.text_block_contacts);
        block_layout = (LinearLayout) findViewById(R.id.Linear5);
        block_sub = (TextView) findViewById(R.id.block_info);
        readreceipts = (TextView) findViewById(R.id.r18_txt1_privacy);
        lastseen = (TextView) findViewById(R.id.last_seen);
        profilepic = (TextView) findViewById(R.id.propic);
        status = (TextView) findViewById(R.id.status);
        checkbox2 = (CheckBox) findViewById(R.id.checkbox2);
        Linearprivacy1 = (LinearLayout) findViewById(R.id.Linear_p1);
        Linearprivacy2 = (LinearLayout) findViewById(R.id.Linear_p2);
        Linearprivacy3 = (LinearLayout) findViewById(R.id.Linear_p3);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        backimg = (ImageView) findViewById(R.id.backarrow_privacy);
        getSupportActionBar().hide();

        checkbox2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (checkbox2.isChecked()) {
                    sessionManager.setSendReadReceipt(true);
                } else {
                    sessionManager.setSendReadReceipt(false);
                }
            }
        });

        if (sessionManager.canSendReadReceipt()) {
            checkbox2.setChecked(true);
        } else {
            checkbox2.setChecked(false);
        }

        getUserPrivacySettings();

        backimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityLauncher.launchAccount(ChatappPrivacy.this);
                finish();
            }
        });

        calculateBlockedUserCount();

        block_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BlockContactList.class);
                intent.putExtra("mssdn", mydata);
                startActivityForResult(intent, BLOCK_USER_REQUEST_CODE);
                mydata.clear();
            }
            //}
        });

        Linearprivacy1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                RadioGroup rg;
                TextView text4, text;
                final RadioButton r1, r3;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.activity_last_seen);
                text = (TextView) dialog.findViewById(R.id.text);
                text4 = (TextView) dialog.findViewById(R.id.text4);
                text4.setTypeface(custom_font2);
                r3 = (RadioButton) dialog.findViewById(R.id.r3);
                rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
                r1 = (RadioButton) dialog.findViewById(R.id.r1);

                r1.setTypeface(custom_font);

                r3.setTypeface(custom_font);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                if (sessionManager.getLastSeenVisibleTo().equalsIgnoreCase("nobody")) {
                    r3.setChecked(true);
                } else {
                    r1.setChecked(true);
                }
                text.setText("Last seen");
                text.setTypeface(custom_font2);

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (ConnectivityInfo.isInternetConnected(ChatappPrivacy.this)) {
                            if (session.getconnect_disconnect()) {
                                if (r1.isChecked()) {
                                    privacyeventcallback("everyone", "last_seen");
                                    dialog.dismiss();
                                }  else if (r3.isChecked()) {
                                    privacyeventcallback("nobody", "last_seen");
                                    dialog.dismiss();
                                }
                            } else {
                                Toast toast = Toast.makeText(ChatappPrivacy.this, "Try again Server not connected", Toast.LENGTH_LONG);
                                toast.setGravity(Gravity.CENTER, 0, 0);
                                toast.show();
                            }

                        } else {
                            dialog.dismiss();
                            final CustomAlertDialog dialogAlert = new CustomAlertDialog();
                            dialogAlert.setTitle("No Internet Connection");
                            dialogAlert.setMessage("You are offline..Please Check your Internet connection");
                            dialogAlert.setPositiveButtonText("Ok");

                            dialogAlert.setCancelable(false);

                            dialogAlert.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                                @Override
                                public void onPositiveButtonClick() {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegativeButtonClick() {

                                }
                            });
                            dialogAlert.show(getSupportFragmentManager(), "InternetAlert");

                        }
                    }
                });
                text4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        Linearprivacy2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                RadioGroup rg;
                TextView text, text4;
                final RadioButton r1, r3;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.activity_last_seen);
                text = (TextView) dialog.findViewById(R.id.text);
                text4 = (TextView) dialog.findViewById(R.id.text4);
                text4.setTypeface(custom_font2);
                r3 = (RadioButton) dialog.findViewById(R.id.r3);
                rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
                r1 = (RadioButton) dialog.findViewById(R.id.r1);

                r1.setTypeface(custom_font);

                r3.setTypeface(custom_font);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                if (sessionManager.getProfilePicVisibleTo().equalsIgnoreCase("nobody")) {
                    r3.setChecked(true);
                } else {
                    r1.setChecked(true);
                }
                text.setText("Profile Photo");
                text.setTypeface(custom_font2);
                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (ConnectivityInfo.isInternetConnected(ChatappPrivacy.this)) {
                            if (session.getconnect_disconnect()) {
                                if (r1.isChecked()) {
                                    privacyeventcallback("everyone", "profile_photo");
                                    dialog.dismiss();
                                }
                                else if (r3.isChecked()) {
                                    privacyeventcallback("nobody", "profile_photo");
                                    dialog.dismiss();
                                }
                            } else {
                                Toast.makeText(ChatappPrivacy.this, "Try again Server not connected", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            dialog.dismiss();
                            final CustomAlertDialog dialogAlert = new CustomAlertDialog();
                            dialogAlert.setTitle("No Internet Connection");
                            dialogAlert.setMessage("You are offline..Please Check your Internet connection");
                            dialogAlert.setPositiveButtonText("Ok");

                            dialogAlert.setCancelable(false);

                            dialogAlert.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                                @Override
                                public void onPositiveButtonClick() {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegativeButtonClick() {

                                }
                            });
                            dialogAlert.show(getSupportFragmentManager(), "InternetAlert");
                        }
                    }
                });
                text4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });
        Linearprivacy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                RadioGroup rg;
                TextView text, text4;
                final RadioButton r1, r3;
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                dialog.setContentView(R.layout.activity_last_seen);
                text = (TextView) dialog.findViewById(R.id.text);
                text4 = (TextView) dialog.findViewById(R.id.text4);
                text4.setTypeface(custom_font2);
                r3 = (RadioButton) dialog.findViewById(R.id.r3);
                rg = (RadioGroup) dialog.findViewById(R.id.radiogroup);
                r1 = (RadioButton) dialog.findViewById(R.id.r1);

                r1.setTypeface(custom_font);

                r3.setTypeface(custom_font);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
                text.setText("Status");
                text.setTypeface(custom_font2);
                if (sessionManager.getProfileStatusVisibleTo().equalsIgnoreCase("nobody")) {
                    r3.setChecked(true);
                } else {
                    r1.setChecked(true);
                }

                rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if (ConnectivityInfo.isInternetConnected(ChatappPrivacy.this)) {
                            if (session.getconnect_disconnect()) {
                                if (r1.isChecked()) {
                                    privacyeventcallback("everyone", "status");
                                    dialog.dismiss();

                                } else if (r3.isChecked()) {
                                    privacyeventcallback("nobody", "status");
                                    dialog.dismiss();
                                }
                            } else {
                                Toast.makeText(ChatappPrivacy.this, "Try again Server not connected", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            dialog.dismiss();
                            final CustomAlertDialog dialogAlert = new CustomAlertDialog();
                            dialogAlert.setTitle("No Internet Connection");
                            dialogAlert.setMessage("You are offline..Please Check your Internet connection");
                            dialogAlert.setPositiveButtonText("Ok");

                            dialogAlert.setCancelable(false);

                            dialogAlert.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                                @Override
                                public void onPositiveButtonClick() {
                                    dialog.dismiss();
                                }

                                @Override
                                public void onNegativeButtonClick() {

                                }
                            });
                            dialogAlert.show(getSupportFragmentManager(), "InternetAlert");


                        }
                    }
                });
                text4.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

            }
        });

       /* text4.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Toast.makeText(ChatappPrivacy.this, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });*/
       /* Linearprivacy3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items_3 = {"EveryOne", "My contacts", "Nobody"};

                final AlertDialog.Builder builder = new AlertDialog.Builder(ChatappPrivacy.this);
                builder.setTitle("Status");
                builder.setSingleChoiceItems(items_3, -1, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();
                        status.setText(items_3[item]);
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Toast.makeText(ChatappPrivacy.this, "Fail", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();


            }
        });*/


    }

    private void calculateBlockedUserCount() {
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        ArrayList<ChatappContactModel> contactList = contactDB_sqlite.getAllChatappContacts();

        int count = 0;

        for (int i = 0; i < contactList.size(); i++) {
            ChatappContactModel contact = contactList.get(i);
            String userId = contact.get_id();

            if (contactDB_sqlite.getBlockedStatus(userId, false).equals("1")) {
//            if (contactsDB.getBlockedStatus(userId, false).equals("1")) {
                count++;
            }

            if (contactDB_sqlite.getBlockedStatus(userId, true).equals("1")) {
//            if (contactsDB.getBlockedStatus(userId, true).equals("1")) {
                count++;
            }
        }

        block_list_count.setText(" " + count);
    }

    private void getUserPrivacySettings() {
        setPrivacyText(sessionManager.getProfileStatusVisibleTo(), sessionManager.getLastSeenVisibleTo(),
                sessionManager.getProfilePicVisibleTo());

        if (!sessionManager.isUserDetailsReceived()) {
            try {
                JSONObject eventObj = new JSONObject();
                eventObj.put("userId", mCurrentUserId);

                SendMessageEvent event = new SendMessageEvent();
                event.setEventName(SocketManager.EVENT_GET_USER_DETAILS);
                event.setMessageObject(eventObj);
                EventBus.getDefault().post(event);
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void setPrivacyText(String statusVisibility, String lastSeenVisibility, String profilePicVisibility) {
        if (statusVisibility.equalsIgnoreCase("mycontacts")) {
            status.setText(getString(R.string.My_Contacts));
        } else if (statusVisibility.equalsIgnoreCase("nobody")) {
            status.setText(getString(R.string.Nobody));
        } else {
            status.setText(getString(R.string.EveryOne));
        }

        if (lastSeenVisibility.equalsIgnoreCase("mycontacts")) {
            lastseen.setText(getString(R.string.My_Contacts));
        } else if (lastSeenVisibility.equalsIgnoreCase("nobody")) {
            lastseen.setText(getString(R.string.Nobody));
        } else {
            lastseen.setText(getString(R.string.EveryOne));
        }

        if (profilePicVisibility.equalsIgnoreCase("mycontacts")) {
            profilepic.setText(getString(R.string.My_Contacts));
        } else if (profilePicVisibility.equalsIgnoreCase("nobody")) {
            profilepic.setText(getString(R.string.Nobody));
        } else {
            profilepic.setText(getString(R.string.EveryOne));
        }
    }

    private void privacyeventcallback(String setting, String privacyvalue) {
        String status = setting;
        String privacy = privacyvalue;
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_PRIVACY_SETTINGS);
        try {
            JSONObject myobj = new JSONObject();
            myobj.put("from", mCurrentUserId);
            myobj.put("status", status);
            myobj.put("privacy", privacy);
            messageEvent.setMessageObject(myobj);
            EventBus.getDefault().post(messageEvent);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_PRIVACY_SETTINGS)) {
            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());

                String statusVisibility = "";
                if (object.has("status")) {
                    statusVisibility = object.getString("status");
                }

                String lastSeenVisibility = "";
                if (object.has("last_seen")) {
                    lastSeenVisibility = object.getString("last_seen");
                }

                String profilePicVisibility = "";
                if (object.has("profile_photo")) {
                    profilePicVisibility = object.getString("profile_photo");
                }
                String userid = object.getString("from");

                if (mCurrentUserId.equalsIgnoreCase(userid)) {
                    setPrivacyText(statusVisibility, lastSeenVisibility, profilePicVisibility);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.getEventName().equalsIgnoreCase(Socket.EVENT_DISCONNECT)) {
            session.setconnect_disconnectevent(false);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == BLOCK_USER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean isChanged = data.getBooleanExtra(BlockContactList.KEY_BLOCKED_USERS_CHANGED, false);
            if (isChanged) {
                calculateBlockedUserCount();
            }
        }
    }
}
