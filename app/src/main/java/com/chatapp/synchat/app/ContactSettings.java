package com.chatapp.synchat.app;

/**
 * created by  Adhash Team on 11/23/2016.
 */

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.core.CoreActivity;
import com.google.android.gms.common.api.GoogleApiClient;
import com.chatapp.synchat.core.service.Constants;

import java.util.ArrayList;
import java.util.List;

public class ContactSettings extends CoreActivity {

    final Context context = this;

    RelativeLayout invite_friends;
    ImageView backcontact;
    AvnNextLTProDemiTextView invite, show;
    AvnNextLTProDemiTextView Text_security;
    String head, body;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_settings);
        getSupportActionBar().hide();
        invite = (AvnNextLTProDemiTextView) findViewById(R.id.r2_txt1_contacts);
        show = (AvnNextLTProDemiTextView) findViewById(R.id.r3_txt1_contacts);

        Text_security = (AvnNextLTProDemiTextView) findViewById(R.id.Text_security);

        backcontact = (ImageView) findViewById(R.id.image_contacts_arrow);
        backcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityLauncher.launchSettingScreen(ContactSettings.this);
                finish();
            }
        });
        invite_friends = (RelativeLayout) findViewById(R.id.R2_contacts);

        invite_friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                shareIt();

               /* final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_contacts_friend);
                dialog.setCanceledOnTouchOutside(true);
                dialog.show();
*/

                /*Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out Chatapp for your smartphones..Download it today from  ");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Chatapp is now available and there is no PIN or username to remember..It works in internet data plan ");


                Intent chooserIntent = Intent.createChooser(shareIntent, "Invite Friends");
                chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(chooserIntent);*/
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        //client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }



    private void shareIt() {
//sharing implementation here
       /* Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Chatapp App Invitation");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));*/
//        Intent emailIntent =findTwitterClient();
//
////        Intent shareIntent = findTwitterClient();
        String appname = getResources().getString(R.string.app_name);
//        emailIntent.setType("application/image");
//        emailIntent.putExtra(Intent.EXTRA_TEXT, Constants.getAppStoreLink(ContactSettings.this));
//        emailIntent.putExtra(Intent.EXTRA_SUBJECT, appname + " : Android");
//        startActivity(Intent.createChooser(emailIntent, "Invite..."));

        List<Intent> intentShareList = new ArrayList<Intent>();
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        List<ResolveInfo> resolveInfoList = getPackageManager().queryIntentActivities(shareIntent, 0);

        for (ResolveInfo resInfo : resolveInfoList) {
            String packageName = resInfo.activityInfo.packageName;
            String name = resInfo.activityInfo.name;

            if (packageName.contains("com.facebook") ||
                    packageName.contains("com.twitter.android") ||
                    packageName.contains("com.google.android.apps.plus") ||
                    packageName.contains("com.google.android.gm") ||
                    packageName.contains("com.whatsapp")) {

                if (name.contains("com.twitter.android.DMActivity")) {
                    continue;
                }

                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, Constants.getAppStoreLink(ContactSettings.this));
                intent.putExtra(Intent.EXTRA_SUBJECT, appname + " : Android");
                intentShareList.add(intent);
            }
        }

        if (intentShareList.isEmpty()) {
            Toast.makeText(ContactSettings.this, "No apps to share !", Toast.LENGTH_SHORT).show();
        } else {
            Intent chooserIntent = Intent.createChooser(intentShareList.remove(0), "Share via");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentShareList.toArray(new Parcelable[]{}));
            startActivity(chooserIntent);
        }

    }

}

