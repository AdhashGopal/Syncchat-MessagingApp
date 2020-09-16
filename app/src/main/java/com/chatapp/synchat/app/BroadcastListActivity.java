package com.chatapp.synchat.app;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.BroadcastListAdapter;
import com.chatapp.synchat.app.utils.BroadcastInfoSession;
import com.chatapp.synchat.core.model.BroadcastInfoPojo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BroadcastListActivity extends AppCompatActivity {

    private RecyclerView recyclerView_chat;

    BroadcastListAdapter mAdapter;
    ArrayList<String> d = new ArrayList<>();
    List<BroadcastInfoPojo> broadcastInfoPojos = new ArrayList<>();


    /**
     * Load adapter view
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_broadcast_list);

        try {


            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setTitle("Broadcast List");

            recyclerView_chat = (RecyclerView) findViewById(R.id.rv);
            recyclerView_chat.setHasFixedSize(true);


            BroadcastInfoSession broadcastInfoSession = new BroadcastInfoSession(this);

            Map<String, ?> allEntries = broadcastInfoSession.broadcastInfoPref.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                d.add(entry.getKey());
                // Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            }

            for (int i = 0; i < d.size(); i++) {
                broadcastInfoPojos.add(broadcastInfoSession.getBroadcastInfo(d.get(i)));
            }

            //   getGroupInfo

            mAdapter = new BroadcastListAdapter(this, broadcastInfoPojos);
            LinearLayoutManager mChatListManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView_chat.setLayoutManager(mChatListManager);
//        recyclerView_chat.setItemAnimator(new DefaultItemAnimator());
            recyclerView_chat.setAdapter(mAdapter);
            recyclerView_chat.setNestedScrollingEnabled(true);
            recyclerView_chat.setHasFixedSize(true);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }
}
