package com.chatapp.android.app;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.Nullable;

import com.chatapp.android.R;
import com.chatapp.android.account.ContactsSyncAdapterService;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.ShortcutBadgeManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.chatapphelperclass.ChatappRegularExp;
import com.chatapp.android.core.socket.MessageService;
import com.chatapp.android.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class ChatappContactsService extends Service {

    private SessionManager sessionManager;
    Cursor pCur;

    public static List<ChatappContactModel> contactEntries = new ArrayList<>();
    private List<ChatappContactModel> contacts;
    private List<ChatappContactModel> chatappEntries;
    private List<ChatappContactModel> othercontacts;

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    public static String contact = new String();
    public static long contactLoadedAt = 0;
    private Session session;
    private String uniqueCurrentID;
    private String currentUserno;

    private UserInfoSession userInfoSession;
    private Handler eventHandler;
    private Runnable eventRunnable;

    public static boolean isStarted = false;
    public static String savedNumber = "";
    private static BroadCastSavedName broadCastSavedName;
    public static final String KEY_FROM_CONTACT_SYNC_SVC = "FromContactSyncSvc";
    public static final String KEY_REFRESH_COMPLETED = "RefreshCompleted";
    ShortcutBadgeManager contactDBtime;
    private static final String TAG = "ScimContactsService" + ">>>";

    @Override
    public void onCreate() {
        super.onCreate();

        isStarted = true;
        EventBus.getDefault().register(this);
        contactDBtime = new ShortcutBadgeManager(this);
        contacts = new ArrayList<>();
        chatappEntries = new ArrayList<>();
        othercontacts = new ArrayList<>();

        userInfoSession = new UserInfoSession(this);

        sessionManager = SessionManager.getInstance(this);
        session = new Session(this);

        currentUserno = sessionManager.getPhoneNumberOfCurrentUser();
        uniqueCurrentID = sessionManager.getCurrentUserID();
    }

    /**
     * Start service
     *
     * @param intent  input value(intent)
     * @param flags   input value(flags)
     * @param startId input value(startId)
     * @return value
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: ");
        boolean fromContactSync = false;

        if (intent != null) {
            fromContactSync = intent.getBooleanExtra(KEY_FROM_CONTACT_SYNC_SVC, false);
        }

        long timeDiff = Calendar.getInstance().getTimeInMillis() - contactLoadedAt;
        long minRefreshDiff = 3 * 60 * 1000; // 3 minutes

        if (savedNumber != null && savedNumber.length() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateDataToTheServer();
                }
            }, 100);

            //    new ReadContactsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (contact == null || contact.equals("") || (timeDiff > minRefreshDiff || fromContactSync)) {

            Calendar c = Calendar.getInstance();
            //    System.out.println("Current time => " + c.getTime());

            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedDate = df.format(c.getTime());

            Log.d(TAG, "Readcontacts_start___  " + formattedDate);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateDataToTheServer();
                }
            }, 100);


            //new ReadContactsTask().execute();
            //   new ReadContactsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateDataToTheServer();
                }
            }, 100);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * EventBus data
     *
     * @param event based on eventbus value to call socket for get contactand privacy setting
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(final ReceviceMessageEvent event) {
        //    Log.d(TAG, event.getEventName());

        if (SocketManager.EVENT_GET_CONTACTS.equalsIgnoreCase(event.getEventName())) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        storeContact(event);
                        Log.d("UpdateUserdetails", "storecontatc");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_PRIVACY_SETTINGS)) {
            loadPrivacySetting(event);
        }
    }

    private void test(ReceviceMessageEvent event) {
        try {
            Object[] args = event.getObjectsArray();
            JSONObject data = new JSONObject(args[0].toString());
            Log.d("UpdateUserdetails", event.getEventName());
        } catch (Exception e) {
        }

    }

    /**
     * Load contact data
     *
     * @param event (getting value)
     * @throws JSONException error
     */
    private void storeContact(ReceviceMessageEvent event) throws JSONException {
        Object[] args = event.getObjectsArray();
        JSONObject data = new JSONObject(args[0].toString());
        setAdapter(data.toString());

        if (!sessionManager.isContactSyncFinished()) {
            sessionManager.setContactSyncFinished();
        }
    }


    /**
     * set adapter for contact list
     *
     * @param values value for contact data
     */
    private void setAdapter(String values) {
        Log.d(TAG, "setAdapter() ");
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        long savedRevision = sessionManager.getContactSavedRevision() + 1;
        //for manual refresh --> update the old revision to new revision
       /* if (savedRevision >1) {
            Log.d(TAG, "setAdapter: revision now updated");
            CoreController.getContactSqliteDBintstance(this).updateRevision_when_contatct_delete(savedRevision, savedRevision - 1);
        }*/
        try {
            session.setFavContacts(values);
            contacts.clear();
            chatappEntries.clear();
            othercontacts.clear();
            contactEntries.clear();

            JSONObject data = new JSONObject(values);

            JSONArray array = data.getJSONArray("Favorites");

            //  for (int contactIndex = 0; contactIndex < contactEntries.size(); contactIndex++) {

            for (int i = 0; i < array.length(); i++) {
                boolean contactUpdated = false;
                JSONObject obj = new JSONObject(array.get(i).toString());
                String msisdn = obj.getString("msisdn");
                String id = obj.getString("_id");
                String countryCode = obj.getString("CountryCode");
                String profilePic = obj.getString("ProfilePic");
                String userStatus = obj.getString("Status");
                String locPhNo = obj.getString("msisdn");
                JSONObject privacy = obj.getJSONObject("privacy");
                String last_seen = privacy.getString("last_seen");
                String profile = privacy.getString("profile_photo");
                String status = privacy.getString("status");
                String profile_photo_status = privacy.getString("profile_photo_status");
                String requestStatus = obj.getString("RequestStatus");
                String docId = uniqueCurrentID + "-" + id;
                if (obj.has("convId")) {
                    String convId = obj.getString("convId");
                    userInfoSession.updateChatConvId(docId, id, convId);
                }


                String local_phNo = obj.getString("msisdn");

                //  String last_seen_status = privacy.getString("last_seen_status");
                //   String profile_status = privacy.getString("profile_status");

                String mineContactStatus = "0";
                if (privacy.has("contactUserList")) {
                    String toContactsList = privacy.getString("contactUserList");
                    if (toContactsList.contains(uniqueCurrentID)) {
                        mineContactStatus = "1";
                    }
                }


                if (!contactUpdated) {
                    String normalConvId = userInfoSession.getChatConvId(docId);
                    if (normalConvId == null || normalConvId.equals("")) {
                        getConvId(id, "yes");
                    }

//                    docId = docId + "-secret";
                    // String secretConvId = userInfoSession.getChatConvId(docId);
                    //    if (secretConvId == null || secretConvId.equals("")) {
                    //        getConvId(id, "yes");
                    //  }
                    contactUpdated = true;
                }


//                    if (locPhNo.equalsIgnoreCase(contactEntries.get(contactIndex).getNumberInDevice())
//                            || msisdn.equalsIgnoreCase(contactEntries.get(contactIndex).getNumberInDevice())
//                            || msisdn.equalsIgnoreCase("+"+contactEntries.get(contactIndex).getNumberInDevice())
//                            || contactEntries.get(contactIndex).getNumberInDevice().contains(local_phNo)
//                            )
//
//                    {


                try {
                    if (ChatappRegularExp.isEncodedBase64String(userStatus)) {
                        userStatus = new String(Base64.decode(userStatus, Base64.DEFAULT), "UTF-8");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "setAdapter: ", e);
                }

                ChatappContactModel singleentry = new ChatappContactModel();

                singleentry.set_id(id);
                singleentry.setStatus(userStatus);
                singleentry.setCountryCode(countryCode);
                singleentry.setMsisdn(msisdn);
                singleentry.setAvatarImageUrl(profilePic);
                singleentry.setType("Mobile");
                singleentry.setRequestStatus(requestStatus);

                String name = obj.getString("Name");
                byte[] data1 = Base64.decode(name, Base64.DEFAULT);

                try {
                    name = new String(data1, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                singleentry.setFirstName(name);

                //Log.d(TAG, "setAdapter: contact position start>:");
                //new db sqlite
                contactDB_sqlite.updateMyContactStatus(id, mineContactStatus);
                contactDB_sqlite.updateLastSeenVisibility(id, getPrivacyStatus(last_seen));
                contactDB_sqlite.updateProfilePicVisibility(id, getPrivacyStatus(profile));
                contactDB_sqlite.updateProfileStatusVisibility(id, getPrivacyStatus(status));

                //Log.d(TAG, "setAdapter: contact position end");
                contactEntries.add(singleentry);

                ChatappContactModel entry = contactEntries.get(i);
//                        if (!id.equalsIgnoreCase(uniqueCurrentID)) {
                chatappEntries.add(entry);


                //new db sqlite
                contactDB_sqlite.updateUserDetails(id, entry);
                contactDB_sqlite.updateSavedRevision(id, savedRevision);

                Log.d("Chatapp_contacts", entry.getFirstName() + "    ---" + id);
                //  }


                if (!profile.equalsIgnoreCase("nobody")) {
                    if (!profile_photo_status.equalsIgnoreCase("0")) {
                        contactEntries.get(i).setAvatarImageUrl(profilePic);
                    }
                }
            }
            //   }
            // }
            sessionManager.setContactSavedRevision(savedRevision);
            Log.d(TAG, String.valueOf(savedRevision));


//            for (int i = 0; i < contactEntries.size(); i++) {
//                if (contactEntries.get(i).get_id() == null) {
//                    othercontacts.add(contactEntries.get(i));
//                }
//            }
//
//            for (int i = 0; i < chatappEntries.size(); i++) {
//                String a1 = chatappEntries.get(i).get_id();
//                for (int j = i + 1; j < chatappEntries.size(); j++) {
//                    String a2 = chatappEntries.get(j).get_id();
//                    if (a1!=null && a2!=null &&  a1.equalsIgnoreCase(a2)) {
//                        chatappEntries.remove(chatappEntries.get(j));
//                    }
//                }
//
//            }
//            for (int i = 0; i < othercontacts.size(); i++) {
//                String a1 = othercontacts.get(i).getNumberInDevice();
//                for (int j = i + 1; j < othercontacts.size(); j++) {
//                    String a2 = othercontacts.get(j).getNumberInDevice();
//                    if (a1!=null && a2!=null && a1.equalsIgnoreCase(a2)) {
//                        othercontacts.remove(othercontacts.get(j));
//                    }
//                }
//            }
//
//            for (int i = 0; i < othercontacts.size(); i++) {
//                String mCurrentUserMsisdn = sessionManager.getPhoneNumberOfCurrentUser();
//                if (othercontacts.get(i).getNumberInDevice().startsWith(sessionManager.getCountryCodeOfCurrentUser())) {
//                    if (othercontacts.get(i).getNumberInDevice().equalsIgnoreCase(mCurrentUserMsisdn)) {
//                        othercontacts.remove(i);
//                    }
//                } else {
//                    String myStrPhno = mCurrentUserMsisdn.replace(sessionManager.getCountryCodeOfCurrentUser(), "");
//                    if (othercontacts.get(i).getNumberInDevice().equalsIgnoreCase(myStrPhno)) {
//                        othercontacts.remove(i);
//                    }
//                }
//
//            }

            contacts.addAll(contactEntries);
            //    contacts.addAll(othercontacts);

            sendContactBroadcast(true);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    long accountStarted = sessionManager.getAccountSyncStartTS();
                    long accountCompleted = sessionManager.getAccountSyncCompletedTS();
                    long currentTime = Calendar.getInstance().getTimeInMillis();
                    long startedDiff = currentTime - accountStarted;
                    long completedDiff = currentTime - accountCompleted;
                    if (startedDiff > ContactsSyncAdapterService.MIN_TIME_ACCOUNT_CREATE &&
                            completedDiff > ContactsSyncAdapterService.MIN_TIME_ACCOUNT_CREATE) {
                        createChatappAccount();
                    }
                }
            }).start();
            stopSelf();
            Log.d(TAG, "setAdapter: end");

        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    /**
     * send Contact Broadcast
     *
     * @param isRefreshed boolean value
     */
    private void sendContactBroadcast(boolean isRefreshed) {
        Intent broadcastIntent = new Intent("com.nowletschat.android.contact_refresh");
        broadcastIntent.putExtra(KEY_REFRESH_COMPLETED, isRefreshed);
        sendBroadcast(broadcastIntent);
    }

    /**
     * create Chatapp Account
     */
    private void createChatappAccount() {
        sessionManager.setAccountSyncStartTS(Calendar.getInstance().getTimeInMillis());

        Account account = new Account(getString(R.string.app_name), getString(R.string.account_type));
        AccountManager removeManager = AccountManager.get(this);
        removeManager.removeAccount(account, new AccountManagerCallback<Boolean>() {
            @Override
            public void run(AccountManagerFuture<Boolean> accountManagerFuture) {
                Account account = new Account(getString(R.string.app_name), getString(R.string.account_type));
                AccountManager addManager = AccountManager.get(ChatappContactsService.this);
                if (addManager.addAccountExplicitly(account, null, null)) {
                    ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);
                    ContentResolver.setMasterSyncAutomatically(true);
                }
            }
        }, null);
    }

    /**
     * get Privacy Status (Mycontact, nobody and everyone)
     *
     * @param visibleTo based on value shown status
     */
    private String getPrivacyStatus(String visibleTo) {
        switch (visibleTo.toLowerCase()) {

            case ContactDB_Sqlite.PRIVACY_TO_MY_CONTACTS:
                return ContactDB_Sqlite.PRIVACY_STATUS_MY_CONTACTS;

            case ContactDB_Sqlite.PRIVACY_TO_NOBODY:
                return ContactDB_Sqlite.PRIVACY_STATUS_NOBODY;

            default:
                return ContactDB_Sqlite.PRIVACY_STATUS_EVERYONE;
        }
    }

    /**
     * get ConvId
     *
     * @param receiverId input value (receiverId)
     * @param secretType input value (secretType)
     */
    private void getConvId(String receiverId, String secretType) {

        try {
            JSONObject obj = new JSONObject();
            obj.put("from", uniqueCurrentID);
            obj.put("to", receiverId);
            obj.put("secret_type", secretType);

            SendMessageEvent event = new SendMessageEvent();
            event.setEventName(SocketManager.EVENT_GET_CONV_ID);
            event.setMessageObject(obj);
            EventBus.getDefault().post(event);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void loadPrivacySetting(ReceviceMessageEvent event) {
        try {
            Object[] objects = event.getObjectsArray();
            JSONObject object = new JSONObject(objects[0].toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * kill the current activity
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        isStarted = false;

        if (eventHandler != null) {
            eventHandler.removeCallbacks(eventRunnable);
        }

        EventBus.getDefault().unregister(this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * set BroadCast SavedName
     */
    public static void setBroadCastSavedName(BroadCastSavedName broadCastSavedNme) {
        broadCastSavedName = broadCastSavedNme;
    }

//    private class ReadContactsTask extends AsyncTask<Void, Integer, Long> {
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//            Log.d(TAG, "onPreExecute: ");
//        }
//
//        protected Long doInBackground(Void... urls) {
//
//            try {
//                readContacts();
//            } catch (Exception e) {
//                Log.d(TAG, "doInBackground: ",e);
//            }
//            return 0L;
//        }
//
//        protected void onProgressUpdate(Integer... progress) {
//        }
//
//        protected void onPostExecute(Long result) {
//
//            Calendar c = Calendar.getInstance();
//
//
//            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            String formattedDate = df.format(c.getTime());
//
//            Log.d(TAG,"onPostExecute "+ formattedDate);
//            updateDataToTheServer();
//        }
//
//    }

    /**
     * update Data To TheServer
     */
    public void updateDataToTheServer() {
        Log.d(TAG, "updateDataToTheServer: ");
        if (SocketManager.isConnected) {
            SendMessageEvent messageEvent = new SendMessageEvent();
            messageEvent.setEventName(SocketManager.EVENT_GET_FAVORITE);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("indexAt", "0");
                jsonObject.put("msisdn", sessionManager.getPhoneNumberOfCurrentUser());
                jsonObject.put("Contacts", contact);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            messageEvent.setMessageObject(jsonObject);
            EventBus.getDefault().post(messageEvent);
            setEventTimeout();
        } else {
            stopSelf();
//            sendContactBroadcast(true);
        }
    }

    /**
     * getNumber for user
     */
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

//    public void readContacts() throws RemoteException {
//        Log.d(TAG, "^^^ readContacts: start >>>");
//        final String SELECTION = ContactsContract.Contacts.HAS_PHONE_NUMBER;
//        final String[] PROJECTION = new String[] {
//                ContactsContract.CommonDataKinds.Phone._ID,
//                ContactsContract.Contacts.DISPLAY_NAME,
//                ContactsContract.CommonDataKinds.Phone.NUMBER,
//                ContactsContract.CommonDataKinds.Phone.TYPE
//        };
//
//
//        ContentResolver cr = getContentResolver();
//        ContentProviderClient mCProviderClient = cr.acquireContentProviderClient(ContactsContract.Contacts.CONTENT_URI);
//        Cursor cur;
//        boolean firstTimeSyncCompleted=contactDBtime.getfirstTimecontactSyncCompleted();
//
//        Log.d(TAG, "readContacts: first sync completed: "+firstTimeSyncCompleted);
//        //long lastRefreshTime= contactDBtime.getlastContact_refreshTime();
//
//        cur = mCProviderClient.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION,
//                SELECTION, null, null);
//
///*        if ( !firstTimeSyncCompleted  || contactDBtime.getlastContact_refreshTime()<=0) {
//
//            cur = mCProviderClient.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION,
//                    SELECTION, null, null);
//
//
//            Log.d("UpdateUserdetails", "IF_readcontact");
//        } else {
//            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
//            int oldContactscount=contactDB_sqlite.getSavedChatappContacts().size();
//            Log.d(TAG, "readContacts: old contacts: "+oldContactscount);
//            if(oldContactscount>0) {
//                Log.d(TAG, "readContacts: lastUpdate>: " + lastRefreshTime);
//                String lastUpdateTime = String.valueOf(lastRefreshTime);
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
//                   *//* cur = mCProviderClient.query(ContactsContract.Contacts.CONTENT_URI, null,
//                            ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + " > ?",
//                            new String[]{lastUpdateTime},
//                            null        // Ordering
//                    );*//*
//                    cur = mCProviderClient.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION,
//                            SELECTION, null, null);
//                }
//                else {
//                    cur = mCProviderClient.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION,
//                            SELECTION, null, null);
//                }
//            }
//            else {
//                cur = mCProviderClient.query(ContactsContract.Contacts.CONTENT_URI, PROJECTION,
//                        SELECTION, null, null);
//            }*/
///*            cur = mCProviderClient.query(ContactsContract.Contacts.CONTENT_URI, null,
//                    null, null, null);*/
//         /*   Log.d("UpdateUserdetails", "ELSE_readcontact");
//
//        }*/
//
//        JSONArray arrContacts = new JSONArray();
//        HashMap<String, ContactsPojo> Contactdata = new HashMap<String, ContactsPojo>();
//        String phone = null;
//        String phType = "";
//        String name = "";
//        String phNo = "";
//        ContactsPojo contactsPojo = null;
//        if (cur != null && cur.getCount() > 0) {
//            Log.d(TAG, "readContacts: total count: "+cur.getCount());
//            final int nameIndex = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
//            final int numberIndex = cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//            final int phoneTypeIndex= cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
//            while (cur.moveToNext()) {
///*                String id = cur.getString(cur
//                        .getColumnIndex(ContactsContract.Contacts._ID));*/
//               /* try {
//                    //testing purpose
//                    String lastUpdate = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP));
//                    Log.d(TAG, "readContacts lastUpdate: "+lastUpdate );
//                }
//                catch (Exception e){
//                    Log.e(TAG, "readContacts: ",e );
//                }*/
//
//                name = cur
//                        .getString(nameIndex);
//                // Log.d(TAG, ""+name);
//
///*                if (Integer.parseInt(cur.getString(cur
//                        .getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
//
//                    cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                            INNER_PROJECTION,
//                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID
//                                    + " = ?", new String[]{id}, null);
//
//                    if (pCur != null) {
//                        while (pCur.moveToNext()) {*/
//                //    Log.d(TAG, "readContacts: innserCursor"+innerCursorCount++);
//                phone = cur.getString(numberIndex);
//                phType = cur.getString(phoneTypeIndex);
//                String type = "";
//
//
//                switch (getNumber(phType)) {
//                    case 1:
//                        type = "Home";
//                        break;
//                    case 2:
//                        type = "Mobile";
//                        break;
//                    case 3:
//                        type = "Work";
//                        break;
//                    case 4:
//                        type = "Work Fax";
//                        break;
//                    case 5:
//                        type = "Home Fax";
//                        break;
//                    case 6:
//                        type = "Pager";
//                        break;
//                    case 7:
//                        type = "Other";
//                        break;
//                    case 8:
//                        type = "Callback";
//                        break;
//                    default:
//                        type = "Custom";
//                        break;
//
//                }
//
//                try {
//                    if (!phone.equals("") && !phone.isEmpty()) {
//                        phNo = phone.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
//                    }
//
//                    if(broadCastSavedName!=null){
//                        if(phNo!=null && savedNumber!=null && phNo.contains(savedNumber)){
//                            broadCastSavedName.savedName(name);
//                            savedNumber=null;
//                        }
//                    }
//                    if(phNo!=null && phNo.startsWith("0") && phNo.length()==11){
//                        //replace the 0 start characters
//                        phNo= phNo.replaceFirst("0","");
//                        Log.d(TAG, "readContacts: phno: "+phNo);
//                    }
//                    contactsPojo = new ContactsPojo();
//                    JSONObject contactObj = new JSONObject();
//                    contactObj.put("Phno", phNo);
//                    contactObj.put("Name", name);
//                    contactObj.put("Type", type);
//                    try {
//                        arrContacts.put(contactObj);
//                        contactsPojo.setNumber(phNo);
//                        contactsPojo.setName(name);
//                        contactsPojo.setType(type);
//                        Contactdata.put(phNo, contactsPojo);
//
//                        // data.add(contactsPojo);
////                                    System.out.println("---" + arrContacts.length());
////                                    System.out.println("---" + Contactdata.size());
//                    } catch (IllegalArgumentException e) {
//                        e.printStackTrace();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//            //         pCur.close();
//            //   }
//            //   }
//            //  }
//
//            cur.close();
//        }
//
//        if (contactEntries != null) {
//            contactEntries.clear();
//        }
//        Set<Map.Entry<String, ContactsPojo>> set = Contactdata.entrySet();
//        for (Map.Entry<String, ContactsPojo> entry : set) {
//            ChatappContactModel d = new ChatappContactModel();
//            d.setFirstName(entry.getValue().getName());
//            d.setNumberInDevice(entry.getValue().getNumber());
//            d.setType(entry.getValue().getType());
//            contactEntries.add(d);
//            if (currentUserno.equalsIgnoreCase(d.getNumberInDevice())) {
//                contactEntries.remove(d);
//            }
//        }
//
//        contact = arrContacts.toString();
//        contactLoadedAt = Calendar.getInstance().getTimeInMillis();
//        Collections.sort(contactEntries, Getcontactname.nameAscComparator);
//        contactDBtime.setfirsttimecontactSyncCompleted(true);
//        contactDBtime.setContactLastRefreshTime(System.currentTimeMillis());
//        Log.d(TAG, "^^^ readContacts: end");
//    }

    /**
     * Event Timeout to stopself
     */
    private void setEventTimeout() {
        if (eventHandler == null) {
            eventHandler = new Handler();
            eventRunnable = new Runnable() {
                @Override
                public void run() {
                    sendContactBroadcast(false);
                    stopSelf();
                }
            };
            eventHandler.postDelayed(eventRunnable, SocketManager.CONTACT_REFRESH_TIMEOUT);
        }
    }

    /**
     * start ContactService
     *
     * @param context       current activity
     * @param isContactSync check the boolean value
     */
    public static void startContactService(Context context, boolean isContactSync) {
        if (MessageService.isStarted()) {
            Intent intent = new Intent(context, ChatappContactsService.class);
            intent.putExtra(KEY_FROM_CONTACT_SYNC_SVC, isContactSync);
            context.startService(intent);
        }
    }

    /**
     * BroadCast SavedName interface (get the value)
     */
    public interface BroadCastSavedName {
        void savedName(String name);
    }

}