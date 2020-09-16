package com.chatapp.synchat.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.StatusEditAdapter;
import com.chatapp.synchat.core.CoreActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class StatusEditActivity extends CoreActivity {

    ArrayList<String> listValues = new ArrayList<String>();
    ListView list;
    StatusEditAdapter adapter;
    SharedPreferences sharedpreferences;
    String currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_edit);

        list = (ListView)findViewById(R.id.listViewStatusEdit);
        sharedpreferences = getSharedPreferences("status", Context.MODE_PRIVATE);

        getSupportActionBar().setTitle("Edit");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Set<String> set = sharedpreferences.getStringSet("status", null);
        if(set.size()>0){
            listValues.addAll(set);
        }

        currentStatus = getIntent().getStringExtra("current_status");

        adapter = new StatusEditAdapter(this,listValues);
        list.setAdapter(adapter);

    }

    public void deleteStatus(int pos){
        if(!listValues.get(pos).equals(currentStatus)) {
            listValues.remove(pos);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            Set<String> set = new HashSet<String>();
            set.addAll(listValues);
            editor.remove("status");
            editor.putStringSet("status", set);
            editor.commit();
            adapter.notifyDataSetChanged();
        }
        else{
            Toast.makeText(this, "Cannot delete your current status", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.status_delete_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id==android.R.id.home){
            finish();
        }else if(id == R.id.action_delete_all){

            SharedPreferences.Editor editor = sharedpreferences.edit();
            Set<String> set = new HashSet<String>();
            set.add(getIntent().getStringExtra("current_status"));
            editor.remove("status");
            editor.putStringSet("status",set);
            editor.commit();
            finish();

        }
        return super.onOptionsItemSelected(item);
    }
}
