package com.chatapp.android.core.service;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chatapp.android.app.ChatappContactsService;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.ShortcutBadgeManager;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.ContactsPojo;
import com.chatapp.android.core.socket.SocketManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * created by  Adhash Team on 2/28/2017.
 */
public class ContactsSync extends Service {

    public static boolean isStarted;
    private static final String TAG = ContactsSync.class.getSimpleName();
    private boolean isContactLoading;
    ShortcutBadgeManager contactDBtime;
    @Override
    public void onCreate() {
        isStarted = true;
        contactDBtime = new ShortcutBadgeManager(this);
        loadContacts();
        getContentResolver().registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, true, contentObserver);
    }

    private class MyContentObserver extends ContentObserver {

        public MyContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);

            long accountStarted = SessionManager.getInstance(ContactsSync.this).getAccountSyncStartTS();
            long accountCompleted = SessionManager.getInstance(ContactsSync.this).getAccountSyncCompletedTS();

            if (!ChatappContactsService.isStarted && !isContactLoading && accountCompleted >= accountStarted) {
                loadContacts();
            }
        }

    }

    private void loadContacts() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                readContacts();
            }
        }).start();
    }

    public void readContacts() {
        try{
        isContactLoading = true;

        ContentResolver cr = getContentResolver();
        JSONArray arrContacts = new JSONArray();
        HashMap<String, ContactsPojo> Contactdata = new HashMap<String, ContactsPojo>();


        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        String phone = null;
        String phType = "";
        String name = "";
        String phNo = "";
        ContactsPojo contactsPojo = null;
        if (cur != null && cur.getCount() > 0) {

            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
                                    + " = ?", new String[]{id}, null);
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            phType = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                            String type = "";

                            switch (getNumber(phType)) {
                                case 1:
                                    type = "Home";
                                    break;
                                case 2:
                                    type = "Mobile";
                                    break;
                                case 3:
                                    type = "Work";
                                    break;
                                case 4:
                                    type = "Work Fax";
                                    break;
                                case 5:
                                    type = "Home Fax";
                                    break;
                                case 6:
                                    type = "Pager";
                                    break;
                                case 7:
                                    type = "Other";
                                    break;
                                case 8:
                                    type = "Callback";
                                    break;
                                default:
                                    type = "Custom";
                                    break;

                            }

                            try {
                                if (phone!=null && !phone.equals("") && !phone.isEmpty()) {
                                    phNo = phone.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
                                }
                                contactsPojo = new ContactsPojo();
                                JSONObject contactObj = new JSONObject();
                                contactObj.put("Phno", phNo);
                                contactObj.put("Name", name);
                                contactObj.put("Type", type);
                                try {
                                    arrContacts.put(contactObj);
                                    contactsPojo.setNumber(phNo);
                                    contactsPojo.setName(name);
                                    contactsPojo.setType(type);
                                    Contactdata.put(phNo, contactsPojo);

                                    // data.add(contactsPojo);
                                } catch (IllegalArgumentException e) {
                                    e.printStackTrace();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        pCur.close();
                    }

                }
            }

            cur.close();
        }
        if (ChatappContactsService.contactEntries != null) {
            ChatappContactsService.contactEntries.clear();
        }

        Set<Map.Entry<String, ContactsPojo>> set = Contactdata.entrySet();
        for (Map.Entry<String, ContactsPojo> entry : set) {
            ChatappContactModel d = new ChatappContactModel();
            d.setFirstName(entry.getValue().getName());
            d.setNumberInDevice(entry.getValue().getNumber());
            d.setType(entry.getValue().getType());
            ChatappContactsService.contactEntries.add(d);
        }

        ChatappContactsService.contact = arrContacts.toString();
        ChatappContactsService.contactLoadedAt = Calendar.getInstance().getTimeInMillis();
        try {
            Collections.sort(ChatappContactsService.contactEntries, Getcontactname.nameAscComparator);
        }
        catch (Exception e){
            Log.e(TAG, "readContacts: ",e );
        }
        contactDBtime.setfirsttimecontactSyncCompleted(true);
        //contactDBtime.setContactLastRefreshTime(System.currentTimeMillis());
        if (SocketManager.isConnected && SessionManager.getInstance(ContactsSync.this).getBackupRestored()) {
            ChatappContactsService.startContactService(ContactsSync.this, true);
        }
        isContactLoading = false;


        }catch (Exception e){
            Log.e(TAG, "readContacts: ",e );
        }
    }

    public int getNumber(String data) {
        try {
            if (data != null) {
                return Integer.parseInt(data);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return 0;
    }

    MyContentObserver contentObserver = new MyContentObserver(new Handler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*@Override
    protected void onHandleIntent(Intent intent) {
        String dataString = intent.getDataString();

        savedContactsMap = new HashMap<>();
       // loadContacts();
        getContentResolver().registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, true, contentObserver);
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStarted = false;
        //  getContentResolver().unregisterContentObserver(contentObserver);

//        Intent contactIntent = new Intent(ContactsSync.this, ContactsSync.class);
//        startService(contactIntent);
    }
}
