package com.chatapp.synchat.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.UltimateSettingAdapter;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.app.widget.CircleImageView;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.model.ChatappSettingsModel;
import com.chatapp.synchat.core.service.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ChatappSettings extends CoreActivity implements View.OnClickListener {

    /* Display the settings page - options to do stuff like help, check profile etc */
    private ListView lv_Settings;
    private AvnNextLTProRegTextView Username, Status;
    private ImageView profileImage, backnavigate;
    private RelativeLayout headerlayout;

    /*private String[] values = { "Help", "Profile", "Account", "Chat setting",
        "Notification", "Contacts" };*/
//    private String[] values = {"Privacy","About & Help","Change Pin"};
    private String[] values = {"About & Help", "Change Pin"};

    //    private int[] imagesIcon = {R.drawable.ic_account,R.drawable.ic_help,R.drawable.ic_account};
    private int[] imagesIcon = {R.drawable.ic_help, R.drawable.ic_account};

    ArrayList<ChatappSettingsModel> dataList = new ArrayList<>();

    /**
     * Binding the widget id and value
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);
        setTitle("Settings");

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        backnavigate = (ImageView) findViewById(R.id.backnavigate);

        backnavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        for (int i = 0; i < values.length; i++) {
            boolean canAdd = false;
            if (i != 3) {
                canAdd = true;
            } else if (SessionManager.getInstance(this).getLockChatEnabled().equals("1")) {
                canAdd = true;
            }

            if (canAdd) {
                ChatappSettingsModel model = new ChatappSettingsModel();
                model.setTitle(values[i]);
                model.setResourceId(imagesIcon[i]);
                dataList.add(model);
            }
        }

        lv_Settings = (ListView) findViewById(R.id.listViewSettings);
        LayoutInflater infalter = getLayoutInflater();
        ViewGroup header = (ViewGroup) infalter.inflate(R.layout.listviewheader, lv_Settings, false);
        lv_Settings.addHeaderView(header);
        lv_Settings.setAdapter(new UltimateSettingAdapter(ChatappSettings.this, dataList));


        Username = (AvnNextLTProRegTextView) header.findViewById(R.id.username);
        Status = (AvnNextLTProRegTextView) header.findViewById(R.id.userstatus);

        if (!SessionManager.getInstance(ChatappSettings.this).getcurrentUserstatus().isEmpty()) {
            Status.setText(SessionManager.getInstance(ChatappSettings.this).getcurrentUserstatus());
        }
        Username.setText(SessionManager.getInstance(ChatappSettings.this).getnameOfCurrentUser());
        String uname = Username.getText().toString();
        /*if (!uname.equals("")) {
            uname = uname.toLowerCase();
            uname = uname.substring(0, 1).toUpperCase() + uname.substring(1).toLowerCase();
        }*/
        Username.setText(uname);
        profileImage = (CircleImageView) header.findViewById(R.id.userprofile);
        headerlayout = (RelativeLayout) header.findViewById(R.id.header);
        headerlayout.setOnClickListener(ChatappSettings.this);
        String pic = SessionManager.getInstance(ChatappSettings.this).getUserProfilePic();
        if (pic != null && !pic.isEmpty()) {

            /*Glide.with(Settings.this).load(pic).asBitmap()

                    .fitCenter().placeholder(R.mipmap.chat_attachment_profile_default_image_frame).
                    into(new BitmapImageViewTarget(profileImage) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(getResources(), resource);
                            circularBitmapDrawable.setCircular(true);
                            profileImage.setImageDrawable(circularBitmapDrawable);
                        }
                    });*/
            Picasso.with(this).load(Constants.SOCKET_IP.concat(pic)).error(
                    R.mipmap.chat_attachment_profile_default_image_frame).into(profileImage);
        }

        lv_Settings.setOnItemClickListener(new OnItemClickListener() {


            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (!SessionManager.getInstance(ChatappSettings.this).getLockChatEnabled().equals("1")
                        && position > 3) {
                    position++;
                }

                switch (position) {
                   /* case 1:
                        Intent intent_privacy = new Intent(getApplicationContext(), ChatappPrivacy.class);
                        startActivity(intent_privacy);

                        break;*/
//                    case 1:
//                        ActivityLauncher.launchChatSetting(ChatappSettings.this);
////                        finish();
//                        break;
                    //   case 3:
                    //  ActivityLauncher.launchNotification(ChatappSettings.this);
//                        finish();
                    //     break;

                    //  case 4:
                    //  Intent intent = new Intent(ChatappSettings.this, EmailSettings.class);
                    //   startActivity(intent);
                    //    break;

//                    case 2:
//                        ActivityLauncher.launchContactSettings(ChatappSettings.this);
//                        break;
                    case 1:
                        ActivityLauncher.launchAbouthelp(ChatappSettings.this);
                        break;
                    case 2:
                        ActivityLauncher.launchChangePin(ChatappSettings.this);
                        break;
                }

            }

        });
    }

    /**
     * clicking action
     *
     * @param view specific view action
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.header:
                ActivityLauncher.launchUserProfile(ChatappSettings.this);
                break;
        }

    }

    /**
     * kill the current activity
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //  overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * Handling UI screen to update the profile picture
     */
    @Override
    protected void onResume() {
        super.onResume();
        Status.setText(SessionManager.getInstance(ChatappSettings.this).getcurrentUserstatus());
        Username.setText(SessionManager.getInstance(ChatappSettings.this).getnameOfCurrentUser());

        String pic = SessionManager.getInstance(ChatappSettings.this).getUserProfilePic();
        if (pic != null && !pic.isEmpty()) {
//TODO tharani map
            Glide.with(this).load(Constants.SOCKET_IP.concat(pic)).placeholder(R.mipmap.chat_attachment_profile_default_image_frame)
                    .centerCrop().dontAnimate().skipMemoryCache(true).into(profileImage);

        } else {
            profileImage.setImageResource(R.mipmap.chat_attachment_profile_default_image_frame);
        }
        /*String uname = Username.getText().toString();
        if (!uname.equals("")) {
            uname = uname.toLowerCase();
            uname = uname.substring(0, 1).toUpperCase() + uname.substring(1).toLowerCase();
        }
        Username.setText(uname);*/

    }

}
