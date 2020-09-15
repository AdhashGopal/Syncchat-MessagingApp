package com.chatapp.android.app;


import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.SessionManager;

import java.util.Calendar;

/**
 */
public class ChatappNewPageAbout extends CoreActivity {

    private AvnNextLTProRegTextView tvVersion, tvtextView3;
    ImageView back_arrow;
    SessionManager sessionManager;

    /**
     * Getting the app version name
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        sessionManager = SessionManager.getInstance(this);
        tvVersion = (AvnNextLTProRegTextView) findViewById(R.id.tvVersion);
        tvtextView3 = (AvnNextLTProRegTextView) findViewById(R.id.textView3);
        PackageManager manager = getPackageManager();
        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
        try {
            PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
            String version = "Version " + info.versionName;
            tvVersion.setText(version);
//            tvtextView3.setText("\u00a9 " + year + " aChat Inc");
            tvtextView3.setText(sessionManager.getCopyRights());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        back_arrow=(ImageView)findViewById(R.id.back_arrow);
        back_arrow.setVisibility(View.VISIBLE);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }


}