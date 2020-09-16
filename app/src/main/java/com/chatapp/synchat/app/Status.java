package com.chatapp.synchat.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.cursoradapter.widget.SimpleCursorAdapter;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.DatabaseClassForDB;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SCLoginModel;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


public class Status extends CoreActivity implements View.OnClickListener {
    ImageView backarrow_status;
    private ListView lv1;
    RelativeLayout rl_status;
    private EmojiconTextView textViewStatus;
    AvnNextLTProRegTextView currentStatus, selectStatus;

    AvnNextLTProDemiTextView editStatusText;
    SimpleCursorAdapter dataAdapter;
    Session session;
    int selectedPosition = -1;

    ArrayList<String> list = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    //StatusAdapter adapter;
    DatabaseClassForDB statusDB = new DatabaseClassForDB(this);
    View v;
    /* Green Dao database to get the user status */
    protected static final String TAG = "ActionBarActivity";

    private SCLoginModel SCLoginModel = null;

    ImageButton editStatus;
    Button updateButton;
    private ProgressDialog progressDialog;

    private final int NEW_STATUS_REQUEST_CODE = 2;

    private Handler eventHandler;
    private Runnable eventRunnable;
    String[] values;
    SharedPreferences sharedpreferences;
    Typeface typeface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);
        //setTitle("Status");
        session = new Session(Status.this);
        getSupportActionBar().hide();

        /* Get the user status from local database */
        v = new View(Status.this);
        backarrow_status = (ImageView) findViewById(R.id.backarrow_status);
        backarrow_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        textViewStatus = (EmojiconTextView) findViewById(R.id.textViewStatus);
        textViewStatus.setTextColor(ContextCompat.getColor(Status.this, R.color.appthemecolour));
        /* Get the current user */

        textViewStatus.setText(SessionManager.getInstance(Status.this).getcurrentUserstatus());
        editStatus = (ImageButton) findViewById(R.id.editStatus);
        updateButton = (Button) findViewById(R.id.updateButton);

        sharedpreferences = getSharedPreferences("status", Context.MODE_PRIVATE);


        editStatus.setOnClickListener(this);

        rl_status = (RelativeLayout) findViewById(R.id.statusInfo_rl);
        rl_status.setOnClickListener(this);

        lv1 = (ListView) findViewById(R.id.listViewStatus);
        //statusDB.insetData();
        list = statusDB.displayStatusList();
        selectStatus = (AvnNextLTProRegTextView) findViewById(R.id.selectStatus);
        currentStatus = (AvnNextLTProRegTextView) findViewById(R.id.currentStatus);
        editStatusText = (AvnNextLTProDemiTextView) findViewById(R.id.status_edit_text);


        typeface = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        textViewStatus.setTypeface(typeface);

        progressDialog = getProgressDialogInstance();
        progressDialog.setMessage("Loading...");
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if(eventHandler != null) {
                    eventHandler.removeCallbacks(eventRunnable);
                }
            }
        });

        values = new String[]{"Hey there! I am using " + getResources().getString(R.string.app_name), "Available", "Busy", "At school", "At the movies",
                "At work", "Battery about to die"};

        for (int i = 0; i < values.length; i++) {
            // Only add to list if not status added in local db
            if(!list.contains(values[i])) {
                list.add(values[i]);
            }
            if (SessionManager.getInstance(Status.this).getcurrentUserstatus().equals(list.get(i)))
                selectedPosition = i;
        }


        lv1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);




        //v = lv1.getChildAt(selectedPosition);
        // v.setBackgroundColor(Color.parseColor("#e3e3e3"));
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                View v = getViewByPosition(position, lv1);
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
//                session.putposition(position);
                String val = (String) parent.getItemAtPosition(position);
                textViewStatus.setText(val);
                lv1.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
                lv1.setItemChecked(position, true);
                System.out.println("position===================>status" + position);
                //v.setBackgroundColor(Color.parseColor("#e3e3e3"));

                for (int i = 0; i < list.size(); i++) {
                    View v1 = getViewByPosition(i, lv1);
                    if (i != position) {
                        tv.setTextColor(Color.BLACK);
                    } else {
                        tv.setTextColor(ContextCompat.getColor(Status.this, R.color.colorPrimary));
                    }
                }
                //    final View renderer = super.getView(position, convertView, parent);
                /* Upload the status to the server and update the database */
                updateUserData(val);
            }
        });

        editStatusText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = sharedpreferences.edit();
                Set<String> stringSet = new HashSet<String>();
                stringSet.addAll(list);
                editor.putStringSet("status",stringSet);
                editor.commit();

                Intent intent = new Intent(Status.this,StatusEditActivity.class);
                intent.putExtra("values",list);
                intent.putExtra("current_status",textViewStatus.getText().toString());
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onResume() {

        Set<String> set = sharedpreferences.getStringSet("status", null);

        if(set!=null){
            list.clear();
            list.addAll(set);
        }
        else{
            SharedPreferences.Editor editor = sharedpreferences.edit();
            Set<String> stringSet = new HashSet<String>();
            stringSet.addAll(list);
            editor.putStringSet("status",stringSet);
            editor.commit();
        }

        for (int i = 0; i < list.size(); i++) {
            if (SessionManager.getInstance(Status.this).getcurrentUserstatus().equals(list.get(i)))
                selectedPosition = i;
        }

        adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item_emoji, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                // Get the Item from ListView
                View view = super.getView(position, convertView, parent);

                // Initialize a TextView for ListView each Item
                TextView tv = (TextView) view.findViewById(android.R.id.text1);
                tv.setTypeface(typeface);
                tv.setTextSize(13);
                tv.setTextScaleX(1);

                if (selectedPosition != position) {
                    // Set the text color of TextView (ListView Item)
                    tv.setTextColor(Color.BLACK);
                } else {
                    tv.setTextColor(ContextCompat.getColor(Status.this, R.color.colorPrimary));
                }

                // Generate ListView Item using TextView
                return view;
            }
        };
        lv1.setAdapter(adapter);

        super.onResume();
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public void onClick(View v) {
        // Intent intent = null;
        switch (v.getId()) {
            case R.id.editStatus:
            case R.id.statusInfo_rl:
                String status = textViewStatus.getText().toString();
                Intent intent = new Intent(Status.this, StatusNewScreen.class);
                intent.putExtra("STATUS", status);
                startActivityForResult(intent, NEW_STATUS_REQUEST_CODE);
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed here it is 2
        if (resultCode == RESULT_OK && requestCode == NEW_STATUS_REQUEST_CODE) {
            String message = data.getStringExtra("MESSAGE");

            textViewStatus.setText(message);
            /* Update the status on the server here */
            updateUserData(message);

            addNewStatus(message);
        }

    }
    private Boolean internetCheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }
    private void addNewStatus(String status) {

        if(!list.contains(status)) {
            list.add(0, status);
            selectedPosition = 0;
            SharedPreferences.Editor editor = sharedpreferences.edit();
            Set<String> stringSet = new HashSet<String>();
            stringSet.addAll(list);
            editor.putStringSet("status",stringSet);
            editor.commit();
        } else {
            int index = list.indexOf(status);
            selectedPosition = index;
        }

        adapter.notifyDataSetChanged();


    }



    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.right_out);
    }


    /* This method will post the current user data to the server */
    private void updateUserData(String status) {
        if(ConnectivityInfo.isInternetConnected(this)) {
            if (!progressDialog.isShowing()) {
                progressDialog.show();
                setEventTimeout();
            }

            try {
                JSONObject statusObj = new JSONObject();
                statusObj.put("from", SessionManager.getInstance(Status.this).getCurrentUserID());
                statusObj.put("status", status);

                SendMessageEvent statusEvent = new SendMessageEvent();
                statusEvent.setMessageObject(statusObj);
                statusEvent.setEventName(SocketManager.EVENT_CHANGE_PROFILE_STATUS);
                EventBus.getDefault().post(statusEvent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void setEventTimeout() {
        if(eventHandler == null) {
            eventHandler = new Handler();
            eventRunnable = new Runnable() {
                @Override
                public void run() {
                    if(progressDialog != null && progressDialog.isShowing()) {
                        Toast.makeText(Status.this, "Try again", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            };
        }

        eventHandler.postDelayed(eventRunnable, SocketManager.RESPONSE_TIMEOUT);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {

        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CHANGE_PROFILE_STATUS)) {
            Object[] array = event.getObjectsArray();
            try {
                JSONObject object = new JSONObject(array[0].toString());

                String err = object.getString("err");
                if (err.equalsIgnoreCase("0")) {

                    String from = object.getString("from");
                    if(from.equalsIgnoreCase(SessionManager.getInstance(Status.this).getCurrentUserID())) {
                        if(progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            String message = object.getString("message");
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                            String status = object.getString("status");
                            try {
                                byte[] decodeStatus = Base64.decode(status, Base64.DEFAULT);
                                status = new String(decodeStatus, "UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            textViewStatus.setText(status);
                            addNewStatus(status);
                        }




                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(Status.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(Status.this);
    }
}