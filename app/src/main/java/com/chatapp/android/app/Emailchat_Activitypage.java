package com.chatapp.android.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.adapter.ChatappCASocket;
import com.chatapp.android.app.dialog.CustomAlertDialog;
import com.chatapp.android.app.utils.EmailChatHistoryUtils;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.socket.SocketManager;
import com.chatapp.android.core.uploadtoserver.FileUploadDownloadManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * created by  Adhash Team on 3/9/2017.
 */
public class Emailchat_Activitypage extends CoreActivity implements AdapterView.OnItemClickListener {

    RecyclerView lvContacts;
    ChatappCASocket adapter;
    InputMethodManager inputMethodManager;
    ProgressDialog dialog;
    private SessionManager sessionManager;
    AvnNextLTProRegTextView resevernameforward;
    private List<ChatappContactModel> selectedContactsList;
    private List<ChatappContactModel> dataList;
    ImageView sendmessage;
    private FileUploadDownloadManager uploadDownloadManager;
    private ArrayList<String> myEmailChatInfo = new ArrayList<String>();
    private SearchView searchView;
    RelativeLayout sendlayout;
    String mCurrentUserId, textMsgFromVendor;
    //    MessageDbController db;
    private String contact;
    String id, mReceiverName;
    final Context context = this;
    private ArrayList<MessageItemChat> mChatData;
    ArrayList<ChatappContactModel> chatappEntries;
    ContactRefreshListener contactRefreshListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forward_contact);

        setTitle("Choose chat..");
        lvContacts = (RecyclerView) findViewById(R.id.listContacts);

        sendmessage = (ImageView) findViewById(R.id.overlapImage);
        resevernameforward = (AvnNextLTProRegTextView) findViewById(R.id.chat_text_view);
        sendlayout = (RelativeLayout) findViewById(R.id.sendlayout);

        sendmessage.setVisibility(View.GONE);
        sendlayout.setVisibility(View.GONE);

        mChatData = new ArrayList<>();
        uploadDownloadManager = new FileUploadDownloadManager(Emailchat_Activitypage.this);
        mCurrentUserId = SessionManager.getInstance(this).getCurrentUserID();

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        chatappEntries = contactDB_sqlite.getSavedChatappContacts();
        if (chatappEntries == null) {
            syncContacts();
        } else {
            dataList = new ArrayList<>();
            for (ChatappContactModel contact : chatappEntries) {
                contact.setSelected(false);
                dataList.add(contact);
            }

            Collections.sort(dataList, Getcontactname.nameAscComparator);
            adapter = new ChatappCASocket(Emailchat_Activitypage.this, dataList,contactRefreshListener);
            lvContacts.setAdapter(adapter);
            lvContacts.setHasFixedSize(true);
            LinearLayoutManager mediaManager = new LinearLayoutManager(Emailchat_Activitypage.this, LinearLayoutManager.VERTICAL, false);
            lvContacts.setLayoutManager(mediaManager);
        }

        selectedContactsList = new ArrayList<>();
        adapter.setChatListItemClickListener(new ChatappCASocket.ChatListItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ChatappContactModel userData = adapter.getItem(position);
                id = userData.get_id();
                if (userData.getFirstName() == null || userData.getFirstName().equals("")) {
                    mReceiverName = userData.getMsisdn();
                } else {
                    mReceiverName = userData.getFirstName();
                }
                loadFromDB();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });

        sessionManager = SessionManager.getInstance(this);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {


    }

    private void loadFromDB() {
        ArrayList<MessageItemChat> items;
        String singeDocId = mCurrentUserId.concat("-").concat(id);
        MessageDbController db = CoreController.getDBInstance(this);
        items = db.selectAllChatMessages(singeDocId, MessageFactory.CHAT_TYPE_SINGLE);
        mChatData.clear();
        mChatData.addAll(items);

        if (mChatData.size() > 0) {
            performMenuEmailChat();
        } else {
            Toast.makeText(this, "No conversation found", Toast.LENGTH_SHORT).show();
        }

    }

    private void performMenuEmailChat() {
        final String msg = "Attaching media will generate a large email message.";

        final CustomAlertDialog dialog = new CustomAlertDialog();
        dialog.setNegativeButtonText("Without media");
        dialog.setPositiveButtonText("Attach media");
        dialog.setMessage(msg);
        dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
            @Override
            public void onPositiveButtonClick() {
                dialog.dismiss();
                final ProgressDialog dialogpr = ProgressDialog.show(Emailchat_Activitypage.this, "",
                        "Loading. Please wait...", true);
                final Timer timer2 = new Timer();
                timer2.schedule(new TimerTask() {
                    public void run() {
                        dialogpr.dismiss();
                        timer2.cancel(); //this will cancel the timer of the system
                    }
                }, 2500); // the timer will count 2.5 seconds....

                String docId;
                docId = mCurrentUserId.concat("-").concat(id);
                EmailChatHistoryUtils emilChat = new EmailChatHistoryUtils(Emailchat_Activitypage.this);
                emilChat.send(docId, mReceiverName, true, true, MessageFactory.CHAT_TYPE_SINGLE);
            }

            @Override
            public void onNegativeButtonClick() {
                dialog.dismiss();
                String docId;
                docId = mCurrentUserId.concat("-").concat(id);

                EmailChatHistoryUtils emilChat = new EmailChatHistoryUtils(Emailchat_Activitypage.this);
                emilChat.send(docId, mReceiverName, false, true, MessageFactory.CHAT_TYPE_SINGLE);
            }
        });
        dialog.show(getSupportFragmentManager(), "Delete member alert");
    }


    private void syncContacts() {
        contactsFromCursor();
    }

    private void contactsFromCursor() {
        ChatappContactsService.contactEntries = new ArrayList<>();
        showProgressDialog();

        Uri contactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // The content URI of the phone contacts
        String[] projection = {                                  // The columns to return for each row
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String selection = null;                                 //Selection criteria
        String[] selectionArgs = {};                             //Selection criteria
        String sortOrder = null;                                 //The sort order for the returned rows

        Cursor cursor = getContentResolver().query(contactsUri, projection, selection, selectionArgs, sortOrder);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            JSONArray arrContacts = new JSONArray();
            do {
                ChatappContactModel d = new ChatappContactModel();
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


                d.setFirstName(name);
//                contact += phNumber.trim() + ",";

                try {
                    String phNo = phNumber.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
                    d.setNumberInDevice(phNo);

                    JSONObject contactObj = new JSONObject();
                    contactObj.put("Phno", phNo);
                    contactObj.put("Name", name);
                    arrContacts.put(contactObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ChatappContactsService.contactEntries.add(d);

            } while (cursor.moveToNext());
            contact = arrContacts.toString();
        }
        updateDataToTheServer();
    }

    private void updateDataToTheServer() {
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_GET_FAVORITE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("indexAt","0");
            jsonObject.put("msisdn", SessionManager.getInstance(this).getPhoneNumberOfCurrentUser());
            jsonObject.put("Contacts", contact);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageEvent.setMessageObject(jsonObject);
        EventBus.getDefault().post(messageEvent);
    }


    private void storeContact(ReceviceMessageEvent event) throws JSONException {
        Object[] args = event.getObjectsArray();
        JSONObject data = new JSONObject(args[0].toString());
        JSONArray array = data.getJSONArray("Favorites");

        chatappEntries = new ArrayList<>();

        for (int contactIndex = 0; contactIndex < ChatappContactsService.contactEntries.size(); contactIndex++) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = new JSONObject(array.get(i).toString());
                String msisdn = obj.getString("msisdn");
                String id = obj.getString("_id");
                String profilePic = obj.getString("ProfilePic");

                ChatappContactModel entry = ChatappContactsService.contactEntries.get(contactIndex);
                if (msisdn.contains(entry.getNumberInDevice())) {
                    chatappEntries.add(entry);

                }
            }
        }

        dataList = new ArrayList<>();
        for (ChatappContactModel contact : chatappEntries) {
            contact.setSelected(false);
            dataList.add(contact);
        }

        adapter = new ChatappCASocket(Emailchat_Activitypage.this, dataList,contactRefreshListener);
        lvContacts.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (SocketManager.EVENT_GET_CONTACTS.equalsIgnoreCase(event.getEventName())) {
            try {
                storeContact(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (SocketManager.EVENT_GET_CONTACTS.equalsIgnoreCase(event.getEventName())) {
            try {
                storeContact(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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


    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forward_contact, menu);
        MenuItem searchItem = menu.findItem(R.id.chats_searchIcon1);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setIconifiedByDefault(true);
                searchView.setIconified(true);
                searchView.setQuery("", false);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try{
                    if (newText.equals("") && newText.isEmpty()) {
                        searchView.clearFocus();
                    }
                    adapter.getFilter().filter(newText);
                }catch (Exception e){
                    e.printStackTrace();
                }

                return false;
            }
        });

        searchView.setIconifiedByDefault(true);
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}

