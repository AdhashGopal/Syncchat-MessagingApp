package com.chatapp.synchat.app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.AboutHelpAdapter;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.SessionManager;

public class AboutHelp extends CoreActivity {
    /**
     * Called when the activity is first created.
     */

    ListView lview;
    AboutHelpAdapter lviewAdapter;
    SessionManager sessionManager;
    private final static String value[] = { "Contact Us", "Terms and Privacy Policy", "About",
    };

    private final static String subvalue[] = {"", "Questions? Need Help?", "",
            "", ""};

    ImageView backnavigate;


    /**
     * Clicking the specific view to perform specific action
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_help);
        setTitle("About and Help");
        getSupportActionBar().hide();
        sessionManager = SessionManager.getInstance(this);
        backnavigate = (ImageView) findViewById(R.id.backnavigate);
        backnavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        lview = (ListView) findViewById(R.id.listViewHelp);
        lviewAdapter = new AboutHelpAdapter(this, value, subvalue);
        lview.setAdapter(lviewAdapter);

        lview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            // Intent intent = null;

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                switch (position) {
//                    case 0:
//                        Uri uri = Uri.parse(Constants.FAQ); // missing 'http://' will cause crashed
//                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                        startActivity(intent);
                     //   break;
                    case 0:
                        Log.e("enter", "true");
                        System.out.println("am in contactus");
                        ActivityLauncher.launchAbout_contactus(AboutHelp.this);
                        break;
//                    case 2:
//                        ActivityLauncher.launchSystemstatus(AboutHelp.this);
//                        break;
                    case 2:
                        ActivityLauncher.launchAboutnew(AboutHelp.this);
                        break;
                    case 1:
                      /*  Uri uriterms = Uri.parse(Constants.pricvacy_policy); // missing 'http://' will cause crashed
                        Intent intentterms = new Intent(Intent.ACTION_VIEW, uriterms);
                        startActivity(intentterms);*/
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(sessionManager.getTermsUrl()));
                        startActivity(browserIntent);
                        break;
                }


            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
