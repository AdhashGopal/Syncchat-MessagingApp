package com.chatapp.android.core.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chatapp.android.app.utils.TimeStampUtils;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.MuteStatusPojo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * created by  Adhash Team on 2/14/2018.
 */
public class ContactDB_Sqlite extends SQLiteOpenHelper {

    /**
     * Database field name & tablenames
     */
    private final static String DB_Name = "contacts_db";
    private final static int DB_Version = 1;
    private final Context context;

    public static final String PRIVACY_STATUS_EVERYONE = "0";
    public static final String PRIVACY_STATUS_MY_CONTACTS = "1";
    public static final String PRIVACY_STATUS_NOBODY = "2";

    public static final String PRIVACY_TO_EVERYONE = "everyone";
    public static final String PRIVACY_TO_MY_CONTACTS = "mycontacts";
    public static final String PRIVACY_TO_NOBODY = "nobody";

    private static final String CONTACT_SAVED_STATUS = "1";
    private static final String CONTACT_UNSAVED_STATUS = "0";

    public static final String UN_BLOCKED_STATUS = "0";
    public static final String BLOCKED_STATUS = "1";
    public static final String REVISION_COUNT = "revisioncount";

    private final static String REQUESTSTATUS = "RequestStatus";


    private final static String TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS = "chatapp_CONTACT__USERSDETAILS_BLOCK_UNBLOCK_STATUS";
    private final static String TABLE_USERSDETAILS_MUTESTATUS = "chatapp_CONTACT__USERSDETAILS_MUTESTATUS";
    private final static String TABLE_FREQUENT_CONTACTS = "chatapp_CONTACT__FREQUENT_CONTACTS";

    private final static String KEY_REQUEST_STATUS = "RequestStatus";

    // private final static String TABLE_BROADCAST = "chatapp_BROADCAST";

    //table for TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS fields
    private final static String NOINDEVICE = "NoInDevice";
    private final static String FIRSTNAME = "FirstName";
    private final static String AVATARIMAGEURL = "AvatarImageURL";
    private final static String MSISDN = "Msisdn";
    private final static String TYPE = "Type";
    private final static String ISSELECTED = "IsSelected";
    private final static String COUNTRYCODE = "CountryCode";
    private final static String ID = "_id";
    private final static String USERID = "UserID";
    private final static String STATUS = "status";


    private final static String KEY_MY_CONTACT_STATUS = "MyContactStatus";
    private final static String KEY_LAST_SEEN_VISIBILITY = "LastSeenVisibility";
    private final static String KEY_PROFILE_PIC_VISIBILITY = "ProfilePicVisibility";
    private final static String KEY_PROFILE_STATUS_VISIBILITY = "ProfileStatusVisibility";
    private final static String KEY_USER_DETAILS = "UserDetails";
    private final static String KEY_DP_UPDATED_TIME = "DpUpdatedTime";
    private final static String KEY_CONTACT_SAVED_REVISION = "ContactSavedRevision";
    private final static String KEY_BLOCKED_OPPONENET_DETAILS = "BlockedDetails";
    private final static String KEY_BLOCKED_MINE_DETAILS = "BlockedMineDetails";
    private final static String KEY_SECRET_TIMER_DETAILS = "SecretTimerDetails";

    private final static String KEY_NORMAL_CHAT = "NormalChat";
    private final static String KEY_SECRET_CHAT = "SecretChat";
    private final static String REVISION = "ContactSavedRevision";

    //table for TABLE_USERSDETAILS_MUTESTATUS fields
    private final static String KEY_TO_USERID = "ToUserID";
    private final static String KEY_CONVERSATION_ID = "ConversationID";

    private final static String TO_USERID = "touseruniqueid";
    private final static String CONVS_ID = "convsid";

    private final static String KEY_DURATION = "Duration";
    private final static String KEY_TIME_STAMP = "TimeStamp";
    private final static String KEY_NOTIFY_STATUS = "NotifyStatus";
    private final static String KEY_EXPIRE_TS = "ExpireTS";
    public static final String KEY_SECRET_TIMER_ID = "TimerId";
    public static final String KEY_SECRET_TIMER = "Timer";
    public static final String KEY_SECRET_TIMER_MODE = "TimerMode";
    public static final String KEY_SECRET_TIMER_CREATED_BY = "TimerCreatedBy";
    private final String KEY_FREQUENTLY_CONTACTS = "FrequentContacts";


    String CREATE_TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS = "CREATE TABLE " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + "(" + ID
            + " INTEGER PRIMARY KEY ," + USERID + " TEXT UNIQUE," + KEY_MY_CONTACT_STATUS
            + " TEXT," + KEY_LAST_SEEN_VISIBILITY + " TEXT," + KEY_PROFILE_PIC_VISIBILITY + " TEXT," + KEY_PROFILE_STATUS_VISIBILITY + " TEXT,"
            + KEY_USER_DETAILS + " TEXT," + KEY_DP_UPDATED_TIME + " TEXT,"
            + KEY_CONTACT_SAVED_REVISION + " TEXT," + KEY_BLOCKED_OPPONENET_DETAILS + " TEXT," + KEY_BLOCKED_MINE_DETAILS + " TEXT,"
            + KEY_SECRET_TIMER_DETAILS + " TEXT)";

    String CREATE_TABLE_USERSDETAILS_MUTESTATUS = "CREATE TABLE " + TABLE_USERSDETAILS_MUTESTATUS + "(" + ID
            + " INTEGER PRIMARY KEY ," + USERID + " TEXT," + TO_USERID + " TEXT," + CONVS_ID + " TEXT," + KEY_TO_USERID + " TEXT,"
            + KEY_CONVERSATION_ID + " TEXT)";

    String CREATE_TABLE_FREQUENT_CONTACTS = "CREATE TABLE " + TABLE_FREQUENT_CONTACTS + "(" + ID
            + " INTEGER PRIMARY KEY ," + USERID + " TEXT," + CONVS_ID + " TEXT UNIQUE," + REVISION_COUNT + " INTEGER)";


    private Gson gson;
    private GsonBuilder gsonBuilder;

    private SQLiteDatabase mDatabaseInstance;


    /**
     * Sqlite database initialization
     *
     * @return database value
     */
    private synchronized SQLiteDatabase getDatabaseInstance() {
        if (mDatabaseInstance == null) {
            mDatabaseInstance = getWritableDatabase();
        }

        if (!mDatabaseInstance.isOpen()) {
            mDatabaseInstance = getWritableDatabase();
        }

        return mDatabaseInstance;
    }

    /**
     * Sqlite database closed
     */
    public synchronized void close() {
        if (mDatabaseInstance != null && mDatabaseInstance.isOpen()) {
            mDatabaseInstance.close();
        }
    }

    /**
     * create constructor & object create for GsonBuilder
     *
     * @param context current activity
     */
    public ContactDB_Sqlite(Context context) {

        super(context, DB_Name, null, DB_Version);
        this.context = context;
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    /**
     * create table for sqlite database
     *
     * @param db execute the database table
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS);
        db.execSQL(CREATE_TABLE_USERSDETAILS_MUTESTATUS);
        db.execSQL(CREATE_TABLE_FREQUENT_CONTACTS);


    }

    /**
     * if the files are exiting means upgrade the table
     *
     * @param db         execute the database file
     * @param oldVersion check the old version
     * @param newVersion check the new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERSDETAILS_MUTESTATUS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FREQUENT_CONTACTS);


        //  db.execSQL("DROP TABLE IF EXISTS " + CREATE_TABLE_BROADCAST);

        onCreate(db);
    }

    /**
     * delete Database value
     */
    public void deleteDatabase() {
        close();
        context.deleteDatabase(DB_Name);
    }

    /***
     * update User Details
     * @param userId input value(userId)
     * @param model based on update / insert opponent user details
     */
    // update opponent user contact details //
    public void updateUserDetails(final String userId, ChatappContactModel model) {
        String selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";

        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                updateOpponenet_UserDetails(userId, model);

            } else {
                selectCur.close();
                insertOpponenet_UserDetails(userId, model);
            }
        }
    }

    /**
     * checking the User Request Status
     *
     * @param userId input value(userId)
     * @return response value
     */
    public boolean checkUserRequestStatus(final String userId) {
        long savedRevision = SessionManager.getInstance(context).getContactSavedRevision();
        Cursor cursor = getDatabaseInstance().rawQuery("select * from chatapp_CONTACT__USERSDETAILS_BLOCK_UNBLOCK_STATUS", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String UserDetails = cursor.getString(cursor.getColumnIndex(KEY_USER_DETAILS));
                String revsiosn = cursor.getString(cursor.getColumnIndex(KEY_CONTACT_SAVED_REVISION));
                if (revsiosn != null) {
                    long contctSaveRevision = Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_CONTACT_SAVED_REVISION)));
                    if (contctSaveRevision >= savedRevision) {
                        try {
                            JSONObject jsonObject_userdetails = new JSONObject(UserDetails);

                            if (userId.equals(jsonObject_userdetails.getString(USERID))) {
                                if (jsonObject_userdetails.getString(REQUESTSTATUS).equals("3")) {
                                    return true;
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                cursor.moveToNext();
            }
        }
        return false;
    }

    /**
     * insert opponent user for contact details
     *
     * @param userId input value(userId)
     * @param model  getting data from model class
     */

    private void insertOpponenet_UserDetails(String userId, ChatappContactModel model) {
        Log.d("OpponenetDetaiInsert", userId);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(USERID, model.get_id());
            jsonObject.put(FIRSTNAME, model.getFirstName());
            jsonObject.put(STATUS, model.getStatus());
            jsonObject.put(AVATARIMAGEURL, model.getAvatarImageUrl());
            jsonObject.put(NOINDEVICE, model.getNumberInDevice());
            jsonObject.put(MSISDN, model.getMsisdn());
            jsonObject.put(TYPE, model.getType());
            jsonObject.put(ISSELECTED, model.isSelected());
            jsonObject.put(COUNTRYCODE, model.getCountryCode());
            jsonObject.put(REQUESTSTATUS, model.getRequestStatus());
        } catch (Exception e) {

        }

        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_USER_DETAILS, jsonObject.toString());

        getDatabaseInstance().insert(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, null, values);
    }

    /**
     * Update opponent user for contact details
     *
     * @param userId input value(userId)
     * @param model  getting data from model class
     */

    private void updateOpponenet_UserDetails(String userId, ChatappContactModel model) {
        Log.d("OpponenetDetailupdate", model.getFirstName() + "  ---- " + model.get_id());
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(USERID, model.get_id());
            jsonObject.put(FIRSTNAME, model.getFirstName());
            jsonObject.put(STATUS, model.getStatus());
            jsonObject.put(AVATARIMAGEURL, model.getAvatarImageUrl());
            jsonObject.put(NOINDEVICE, model.getNumberInDevice());
            jsonObject.put(MSISDN, model.getMsisdn());
            jsonObject.put(TYPE, model.getType());
            jsonObject.put(ISSELECTED, model.isSelected());
            jsonObject.put(COUNTRYCODE, model.getCountryCode());
            jsonObject.put(REQUESTSTATUS, model.getRequestStatus());
        } catch (Exception e) {

        }

        ContentValues values = new ContentValues();
        values.put(KEY_USER_DETAILS, jsonObject.toString());
        getDatabaseInstance().update(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, values, USERID + "='" + userId + "'", null);
    }


    public void updateOpponenet_UserAvatar(String userId, ChatappContactModel model) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(USERID, model.get_id());
            jsonObject.put(AVATARIMAGEURL, model.getAvatarImageUrl());
        } catch (Exception e) {

        }

        ContentValues values = new ContentValues();
        values.put(KEY_USER_DETAILS, jsonObject.toString());
        getDatabaseInstance().update(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, values, USERID + "='" + userId + "'", null);
    }


    /**
     * get opponent user for contact details
     *
     * @param userId input value(userId)
     * @return response value
     */
    public ChatappContactModel getUserOpponenetDetails(final String userId) {

        String query = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";

        ChatappContactModel chatappContactModel = new ChatappContactModel();

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String data = cursor.getString(cursor.getColumnIndex(KEY_USER_DETAILS));
                    if (data != null) {
                        JSONObject jsonObject_userdetails = new JSONObject(data);
                        chatappContactModel.set_id(jsonObject_userdetails.getString(USERID));
                        chatappContactModel.setFirstName(jsonObject_userdetails.getString(FIRSTNAME));
                        chatappContactModel.setStatus(jsonObject_userdetails.getString(STATUS));
                        chatappContactModel.setAvatarImageUrl(jsonObject_userdetails.getString(AVATARIMAGEURL));
                        chatappContactModel.setNumberInDevice(jsonObject_userdetails.getString(NOINDEVICE));
                        chatappContactModel.setMsisdn(jsonObject_userdetails.getString(MSISDN));
                        if (jsonObject_userdetails.has(TYPE)) {
                            chatappContactModel.setType(jsonObject_userdetails.getString(TYPE));
                        }
                        if (jsonObject_userdetails.has(REQUESTSTATUS)) {
                            chatappContactModel.setRequestStatus(jsonObject_userdetails.getString(REQUESTSTATUS));
                        }
                        if (jsonObject_userdetails.getString(ISSELECTED).equals("true")) {
                            chatappContactModel.setSelected(true);
                        } else {
                            chatappContactModel.setSelected(false);
                        }
                        if (jsonObject_userdetails.has(COUNTRYCODE)) {
                            chatappContactModel.setCountryCode(jsonObject_userdetails.getString(COUNTRYCODE));
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }

        return chatappContactModel;
    }

    public String getUserRequestStatusDetails(final String userId) {

        String query = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";

        String requestStatus = "0";

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String data = cursor.getString(cursor.getColumnIndex(KEY_USER_DETAILS));
                    if (data != null) {
                        JSONObject jsonObject_userdetails = new JSONObject(data);
                        if (jsonObject_userdetails.has(REQUESTSTATUS)) {
                            requestStatus = jsonObject_userdetails.getString(REQUESTSTATUS);
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }

        return requestStatus;
    }

    /**
     * Update opponent user saved status
     *
     * @param userId   input value(userId)
     * @param revision update the value based on version
     */
    public void updateSavedRevision(final String userId, long revision) {
        String selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        System.out.print(".....Devtesting...updateSavedRevision." + String.valueOf(selectCur.getCount()));

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                updateOpponenet_UserDetails_savedRevision(userId, revision);
            } else {
                selectCur.close();
                insertOpponenet_UserDetails_savedRevision(userId, revision);
            }
        }
    }

    /**
     * insert Opponent UserDetails saved status
     *
     * @param userId   input value(userId)
     * @param revision input value(revision)
     */
    private void insertOpponenet_UserDetails_savedRevision(String userId, long revision) {

        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(REVISION, revision);

        getDatabaseInstance().insert(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, null, values);
    }

    /**
     * update Opponent UserDetails saved status
     *
     * @param userId   input value(userId)
     * @param revision input value(revision)
     */
    private void updateOpponenet_UserDetails_savedRevision(String userId, long revision) {
        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(REVISION, revision);

        getDatabaseInstance().update(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, values, USERID + "='" + userId + "'", null);
    }

    /**
     * get opponent userdetails & saved status
     *
     * @param userId input value(userId)
     * @return response value
     */

    public long getOpponenet_UserDetails_savedRevision(final String userId) {
        long value = 0;
        String query = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String revsionvalue = cursor.getString(cursor.getColumnIndex(REVISION));
                if (revsionvalue == null) {
                    value = 0;
                } else {
                    value = Long.parseLong(revsionvalue);
                }

            }
            cursor.close();
        }

        return value;
    }

    public void updateRevision_when_contatct_delete(long currentRevision, long oldRevision) {

        ContentValues values = new ContentValues();
        values.put(REVISION, currentRevision);

        getDatabaseInstance().update(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, values, REVISION + "='" + oldRevision + "'", null);


    }

    /**
     * update current user in opponent user's contacts list status
     *
     * @param userId input value(userId)
     * @param status input value(status)
     */

    public void updateMyContactStatus(final String userId, String status) {
        String selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                update_MyContactStatus(userId, status);
            } else {
                selectCur.close();
                insert_MyContactStatus(userId, status);
            }
        }
    }

    /**
     * insert MyContact Status
     *
     * @param userId input value(userId)
     * @param status input value(status)
     */
    private void insert_MyContactStatus(String userId, String status) {

        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_MY_CONTACT_STATUS, status);

        getDatabaseInstance().insert(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, null, values);
    }

    /**
     * update MyContact Status
     *
     * @param userId input value(userId)
     * @param status input value(status)
     */
    private void update_MyContactStatus(String userId, String status) {
        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_MY_CONTACT_STATUS, status);

        getDatabaseInstance().update(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, values, USERID + "='" + userId + "'", null);

    }

    /**
     * get current user in opponent user's contacts list status
     *
     * @param userId input value(userId)
     * @return response value
     */

    public String getMyContactStatus(String userId) {
        try {
            JSONObject contactObj = getContactDetails(userId);
            if (contactObj.has(KEY_MY_CONTACT_STATUS)) {
                return contactObj.getString(KEY_MY_CONTACT_STATUS);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return CONTACT_UNSAVED_STATUS;
    }

    /**
     * Update LastSeen Visibility for chatview
     *
     * @param userId    input value(userId)
     * @param visibleTo input value(visibleTo)
     */
    public void updateLastSeenVisibility(final String userId, String visibleTo) {
        String selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);
        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                update_LastSeenVisibility(userId, visibleTo);
            } else {
                selectCur.close();
                insert_LastSeenVisibility(userId, visibleTo);
            }
        }
    }

    /**
     * insert LastSeen Visibility for chatview
     *
     * @param userId    input value(userId)
     * @param visibleTo input value(visibleTo)
     */
    private void insert_LastSeenVisibility(String userId, String visibleTo) {
        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_LAST_SEEN_VISIBILITY, visibleTo);

        getDatabaseInstance().insert(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, null, values);
    }

    /**
     * update LastSeen Visibility for chatview
     *
     * @param userId    input value(userId)
     * @param visibleTo input value(visibleTo)
     */
    private void update_LastSeenVisibility(String userId, String visibleTo) {
        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_LAST_SEEN_VISIBILITY, visibleTo);

        getDatabaseInstance().update(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, values, USERID + "='" + userId + "'", null);

    }

    /**
     * get opponent user's last seen visibility to others
     *
     * @param userId input value(userId)
     * @return response value
     */
    //
    public String getLastSeenVisibility(String userId) {

        try {
            JSONObject contactObj = getContactDetails(userId);
            if (contactObj.has(KEY_LAST_SEEN_VISIBILITY)) {
                return contactObj.getString(KEY_LAST_SEEN_VISIBILITY);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return PRIVACY_STATUS_EVERYONE;
    }

    /**
     * update opponent user's profile picture visibility to others
     *
     * @param userId    input value(userId)
     * @param visibleTo input value(visibleTo)
     */

    public void updateProfilePicVisibility(final String userId, String visibleTo) {
        String selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                update_ProfilePicVisibility(userId, visibleTo);
            } else {
                selectCur.close();
                insert_ProfilePicVisibility(userId, visibleTo);
            }
        }
    }

    /**
     * insert user's profile picture visibility to others
     *
     * @param userId    input value(userId)
     * @param visibleTo input value(visibleTo)
     */
    private void insert_ProfilePicVisibility(String userId, String visibleTo) {
        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_PROFILE_PIC_VISIBILITY, visibleTo);

        getDatabaseInstance().insert(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, null, values);
    }

    /**
     * update user's profile picture visibility to others
     *
     * @param userId    input value(userId)
     * @param visibleTo input value(visibleTo)
     */
    private void update_ProfilePicVisibility(String userId, String visibleTo) {
        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_PROFILE_PIC_VISIBILITY, visibleTo);

        getDatabaseInstance().update(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, values, USERID + "='" + userId + "'", null);

    }

    /**
     * get user's profile picture visibility for everyone
     *
     * @param userId input value(userId)
     * @return response value
     */
    // get opponent user's profile picture visibility to others
    public String getProfilePicVisibility(String userId) {
        try {
            JSONObject contactObj = getContactDetails(userId);
            if (contactObj.has(KEY_PROFILE_PIC_VISIBILITY)) {
                return contactObj.getString(KEY_PROFILE_PIC_VISIBILITY);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return PRIVACY_STATUS_EVERYONE;
    }

    /**
     * update opponent user's profile status visibility to others
     *
     * @param userId    input value(userId)
     * @param visibleTo input value(visibleTo)
     */

    public void updateProfileStatusVisibility(final String userId, String visibleTo) {

        String selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                update_ProfileStatusVisibility(userId, visibleTo);
            } else {
                selectCur.close();
                insert_ProfileStatusVisibility(userId, visibleTo);
            }
        }
    }

    /**
     * insert user's profile picture visibility
     *
     * @param userId    input value(userId)
     * @param visibleTo input value(visibleTo)
     */
    private void insert_ProfileStatusVisibility(String userId, String visibleTo) {
        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_PROFILE_STATUS_VISIBILITY, visibleTo);

        getDatabaseInstance().insert(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, null, values);
    }

    /**
     * update user's profile status visibility
     *
     * @param userId    input value(userId)
     * @param visibleTo input value(visibleTo)
     */
    private void update_ProfileStatusVisibility(String userId, String visibleTo) {
        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_PROFILE_STATUS_VISIBILITY, visibleTo);

        getDatabaseInstance().update(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, values, USERID + "='" + userId + "'", null);

    }

    /**
     * get Profile Status Visibility
     *
     * @param userId input value(userId)
     * @return response value
     */
    // get opponent user's profile status visibility to others
    public String getProfileStatusVisibility(String userId) {
        try {
            JSONObject contactObj = getContactDetails(userId);
            if (contactObj.has(KEY_PROFILE_STATUS_VISIBILITY)) {
                return contactObj.getString(KEY_PROFILE_STATUS_VISIBILITY);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return PRIVACY_STATUS_EVERYONE;
    }

    /**
     * get update Blocked Status
     *
     * @param userId       input value(userId)
     * @param status       input value(status)
     * @param isSecretChat input value(isSecretChat)
     */

    public void updateBlockedStatus(final String userId, String status, boolean isSecretChat) {

        String selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                update_User_block_unblock_status(userId, status, isSecretChat);
            } else {
                selectCur.close();
                insert_User_block_unblock_status(userId, status, isSecretChat);
            }
        }
    }

    /**
     * update User block / unblock status
     *
     * @param userId       input value(userId)
     * @param status       input value(status)
     * @param isSecretChat input value(isSecretChat)
     */
    private void update_User_block_unblock_status(String userId, String status, boolean isSecretChat) {
        final JSONObject blockObj = new JSONObject();
        try {

            if (isSecretChat) {
                blockObj.put(KEY_SECRET_CHAT, status);
            } else {
                blockObj.put(KEY_NORMAL_CHAT, status);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_BLOCKED_OPPONENET_DETAILS, blockObj.toString());

        getDatabaseInstance().update(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, values, USERID + "='" + userId + "'", null);
    }

    /**
     * insert User block / unblock status
     *
     * @param userId       input value(userId)
     * @param status       input value(status)
     * @param isSecretChat input value(isSecretChat)
     */
    private void insert_User_block_unblock_status(String userId, String status, boolean isSecretChat) {

        final JSONObject blockObj = new JSONObject();
        try {
            if (isSecretChat) {
                blockObj.put(KEY_SECRET_CHAT, status);
            } else {
                blockObj.put(KEY_NORMAL_CHAT, status);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_BLOCKED_OPPONENET_DETAILS, blockObj.toString());

        getDatabaseInstance().insert(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, null, values);
    }

    /**
     * get Blocked Status (normal chat / secret chat)
     *
     * @param userId       input value(userId)
     * @param isSecretChat input value(isSecretChat)
     * @return response value
     */

    public String getBlockedStatus(String userId, boolean isSecretChat) {
        try {
            JSONObject contactObj = getContactDetails(userId);
            if (contactObj.has(KEY_BLOCKED_OPPONENET_DETAILS)) {
                String string = contactObj.getString(KEY_BLOCKED_OPPONENET_DETAILS);
                JSONObject blockObj = new JSONObject(string);

                if (isSecretChat && blockObj.has(KEY_SECRET_CHAT)) {
                    return blockObj.getString(KEY_SECRET_CHAT);
                } else if (!isSecretChat && blockObj.has(KEY_NORMAL_CHAT)) {
                    return blockObj.getString(KEY_NORMAL_CHAT);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return UN_BLOCKED_STATUS;
    }

    /**
     * update current user is blocked status by opponent user
     *
     * @param userId       input value(userId)
     * @param status       input value(status)
     * @param isSecretChat input value(isSecretChat)
     */

    public void updateBlockedMineStatus(final String userId, String status, boolean isSecretChat) {
        String selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                update_BlockedMineStatus(userId, status, isSecretChat);
            } else {
                selectCur.close();
                insert_BlockedMineStatus(userId, status, isSecretChat);

            }
        }
    }

    /**
     * insert current user Blocked Status
     *
     * @param userId       input value(userId)
     * @param status       input value(status)
     * @param isSecretChat input value(isSecretChat)
     */
    private void insert_BlockedMineStatus(String userId, String status, boolean isSecretChat) {
        final JSONObject blockObj = new JSONObject();
        try {
            if (isSecretChat) {
                blockObj.put(KEY_SECRET_CHAT, status);
            } else {
                blockObj.put(KEY_NORMAL_CHAT, status);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_BLOCKED_MINE_DETAILS, blockObj.toString());

        getDatabaseInstance().insert(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, null, values);
    }

    /**
     * update current user Blocked Status
     *
     * @param userId       input value(userId)
     * @param status       input value(status)
     * @param isSecretChat input value(isSecretChat)
     */
    private void update_BlockedMineStatus(String userId, String status, boolean isSecretChat) {
        final JSONObject blockObj = new JSONObject();
        try {

            if (isSecretChat) {
                blockObj.put(KEY_SECRET_CHAT, status);
            } else {
                blockObj.put(KEY_NORMAL_CHAT, status);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_BLOCKED_MINE_DETAILS, blockObj.toString());

        getDatabaseInstance().update(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, values, USERID + "='" + userId + "'", null);
    }

    /**
     * get current user blocked status
     *
     * @param userId       input value(userId)
     * @param isSecretChat input value(isSecretChat)
     * @return
     */

    public String getBlockedMineStatus(String userId, boolean isSecretChat) {
        JSONObject contactObj = null;
        String string = null;

        try {
            contactObj = getContactDetails(userId);

            if (contactObj.has(KEY_BLOCKED_MINE_DETAILS)) {
                string = contactObj.getString(KEY_BLOCKED_MINE_DETAILS);
                JSONObject blockObj = new JSONObject(string);

                if (isSecretChat && blockObj.has(KEY_SECRET_CHAT)) {
                    return blockObj.getString(KEY_SECRET_CHAT);
                } else if (!isSecretChat && blockObj.has(KEY_NORMAL_CHAT)) {
                    return blockObj.getString(KEY_NORMAL_CHAT);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return UN_BLOCKED_STATUS;
    }

    /**
     * Update opponent user's profile image updated time
     *
     * @param userId    input value(userId)
     * @param timeStamp input value(timeStamp)
     */
    public void updateDpUpdatedTime(final String userId, String timeStamp) {
        String selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);
        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                update_DpUpdatedTime(userId, timeStamp);
            } else {
                selectCur.close();
                insert_DpUpdatedTime(userId, timeStamp);
            }
        }
    }

    /**
     * insert user's profile image time
     *
     * @param userId    input value(userId)
     * @param timeStamp input value(timeStamp)
     */
    private void insert_DpUpdatedTime(String userId, String timeStamp) {
        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_DP_UPDATED_TIME, timeStamp);

        getDatabaseInstance().insert(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, null, values);
    }

    /**
     * update user's profile image time
     *
     * @param userId    input value(userId)
     * @param timeStamp input value(timeStamp)
     */
    private void update_DpUpdatedTime(String userId, String timeStamp) {
        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(KEY_DP_UPDATED_TIME, timeStamp);

        getDatabaseInstance().update(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, values, USERID + "='" + userId + "'", null);
    }

    /**
     * get opponent user's profile image updated time
     *
     * @param userId input value(userId)
     */
    public String getDpUpdatedTime(String userId) {

        try {
            JSONObject contactObj = getContactDetails(userId);
            if (contactObj.has(KEY_DP_UPDATED_TIME)) {
                return contactObj.getString(KEY_DP_UPDATED_TIME);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return "0";
    }

    /**
     * update secret message expiration time
     *
     * @param userId    input value(userId)
     * @param timer     input value(timer)
     * @param createdBy input value(createdBy)
     * @param msgId     input value(msgId)
     */

    public void updateSecretMessageTimer(final String userId, String timer, String createdBy, String msgId) {
        String selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                update_SecretMessageTimer(userId, timer, createdBy, msgId);
            } else {
                selectCur.close();
                insert_SecretMessageTimer(userId, timer, createdBy, msgId);
            }
        }
    }

    /**
     * update Secret Message Timer
     *
     * @param userId    input value(userId)
     * @param timer     input value(timer)
     * @param createdBy input value(createdBy)
     * @param msgId     input value(msgId)
     */
    private void update_SecretMessageTimer(String userId, String timer, String createdBy, String msgId) {

        try {
            final JSONObject contactObj = getContactDetails(userId);
            boolean needUpdate = true;
            if (contactObj.has(KEY_SECRET_TIMER_DETAILS)) {
                try {
                    String string = contactObj.getString(KEY_SECRET_TIMER_DETAILS);
                    JSONObject timerObj = new JSONObject(string);
                    String prevCreatedBy = timerObj.getString(KEY_SECRET_TIMER_CREATED_BY);
                    String prevTimer = timerObj.getString(KEY_SECRET_TIMER);

                    if (prevCreatedBy.equalsIgnoreCase(createdBy) && prevTimer.equalsIgnoreCase(timer)) {
                        needUpdate = false;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            if (needUpdate) {

                try {
                    JSONObject timerObj = new JSONObject();
                    timerObj.put(KEY_SECRET_TIMER_CREATED_BY, createdBy);
                    timerObj.put(KEY_SECRET_TIMER, timer);
                    timerObj.put(KEY_SECRET_TIMER_ID, msgId);

                    ContentValues values = new ContentValues();
                    values.put(USERID, userId);
                    values.put(KEY_SECRET_TIMER_DETAILS, timerObj.toString());

                    getDatabaseInstance().update(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, values, USERID + "='" + userId + "'", null);

                } catch (Exception e) {
                    Log.d("ContactsDBError", e.getMessage() + "");
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * insert Secret Message Timer
     *
     * @param userId    input value(userId)
     * @param timer     input value(timer)
     * @param createdBy input value(createdBy)
     * @param msgId     input value(msgId)
     */
    private void insert_SecretMessageTimer(String userId, String timer, String createdBy, String msgId) {
        try {
            JSONObject timerObj = new JSONObject();
            timerObj.put(KEY_SECRET_TIMER_CREATED_BY, createdBy);
            timerObj.put(KEY_SECRET_TIMER, timer);
            timerObj.put(KEY_SECRET_TIMER_ID, msgId);

            ContentValues values = new ContentValues();
            values.put(USERID, userId);
            values.put(KEY_SECRET_TIMER_DETAILS, timerObj.toString());

            getDatabaseInstance().insert(TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS, null, values);

        } catch (Exception e) {

        }
    }

    /**
     * get secret message expiration time
     *
     * @param userId input value(userId)
     * @return
     */

    public String getSecretMessageTimer(String userId) {
        try {
            JSONObject contactObj = getContactDetails(userId);
            if (contactObj.has(KEY_SECRET_TIMER_DETAILS)) {
                String string = contactObj.getString(KEY_SECRET_TIMER_DETAILS);
                JSONObject timerObj = new JSONObject(string);
                return timerObj.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * update Mute Status
     *
     * @param currentUserId input value(currentUserId)
     * @param toUserId      input value(toUserId)
     * @param convId        input value(convId)
     * @param status        input value(status)
     * @param duration      input value(duration)
     * @param notifyStatus  input value(notifyStatus)
     * @param isSecretChat  input value(isSecretChat)
     */
    public void updateMuteStatus(String currentUserId, String toUserId, String convId, int status, String duration, String notifyStatus, boolean isSecretChat) {

        String selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_MUTESTATUS + " WHERE " +
                TO_USERID + "='" + toUserId + "' AND " + CONVS_ID + "='" + convId + "'";

        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                update_MuteStatus_UserID_Conv_ID(currentUserId, toUserId, convId, status, duration, notifyStatus, isSecretChat);

            } else {
                selectCur.close();
                insert_MuteStatus_UserID_Conv_ID(currentUserId, toUserId, convId, status, duration, notifyStatus, isSecretChat);

            }
        }

    }

    /**
     * update Mute Status to check UserID & Conv_ID
     *
     * @param currentUserId input value(currentUserId)
     * @param toUserId      input value(toUserId)
     * @param convId        input value(convId)
     * @param status        input value(status)
     * @param duration      input value(duration)
     * @param notifyStatus  input value(notifyStatus)
     * @param isSecretChat  input value(isSecretChat)
     */
    private void update_MuteStatus_UserID_Conv_ID(String currentUserId, String toUserId, String convId,
                                                  int status, String duration, String notifyStatus, boolean isSecretChat) {
        final JSONObject valObj = new JSONObject();
        final JSONObject muteObj = new JSONObject();
        try {
            Date currentDate = Calendar.getInstance().getTime();
            muteObj.put(KEY_DURATION, duration);
            muteObj.put(KEY_TIME_STAMP, currentDate.getTime());

            if (status == 1) {
                if (duration.equalsIgnoreCase("8 Hours")) {
                    Date date = TimeStampUtils.addHour(currentDate, 8);
                    muteObj.put(KEY_EXPIRE_TS, date.getTime());
                } else if (duration.equalsIgnoreCase("1 Week")) {
                    Date date = TimeStampUtils.addDay(currentDate, 7);
                    muteObj.put(KEY_EXPIRE_TS, date.getTime());
                } else if (duration.equalsIgnoreCase("1 Year")) {
                    Date date = TimeStampUtils.addYear(currentDate, 1);
                    muteObj.put(KEY_EXPIRE_TS, date.getTime());
                }
            }

            if (notifyStatus == null || notifyStatus.equals("")) {
                muteObj.put(KEY_NOTIFY_STATUS, "0");
            } else {
                muteObj.put(KEY_NOTIFY_STATUS, notifyStatus);
            }

            if (isSecretChat) {
                muteObj.put(KEY_SECRET_CHAT, status);

                if (toUserId != null && !toUserId.equals("")) {
                    valObj.put(KEY_SECRET_CHAT, muteObj);
                }
            } else {
                muteObj.put(KEY_NORMAL_CHAT, status);

                if (toUserId != null && !toUserId.equals("")) {
                    valObj.put(KEY_NORMAL_CHAT, muteObj);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject json_valobj = new JSONObject();
        JSONObject json_muteObj = new JSONObject();
        try {
            if (toUserId != null && !toUserId.trim().equals("")) {
                json_valobj.put(toUserId, valObj);
            }
            if (convId != null && !convId.trim().equals("")) {
                json_muteObj.put(convId, muteObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(USERID, currentUserId);
        values.put(TO_USERID, toUserId);
        values.put(CONVS_ID, convId);
        values.put(KEY_TO_USERID, json_valobj.toString());
        values.put(KEY_CONVERSATION_ID, json_muteObj.toString());

        getDatabaseInstance().update(TABLE_USERSDETAILS_MUTESTATUS, values, TO_USERID + "='" + toUserId + "'" + " AND " + CONVS_ID + "='" + convId + "'", null);
    }

    /**
     * insert Mute Status to check UserID & Conv_ID
     *
     * @param currentUserId input value(currentUserId)
     * @param toUserId      input value(toUserId)
     * @param convId        input value(convId)
     * @param status        input value(status)
     * @param duration      input value(duration)
     * @param notifyStatus  input value(notifyStatus)
     * @param isSecretChat  input value(isSecretChat)
     */
    private void insert_MuteStatus_UserID_Conv_ID(String currentUserId, String toUserId, String convId,
                                                  int status, String duration, String notifyStatus, boolean isSecretChat) {
        final JSONObject valObj = new JSONObject();
        final JSONObject muteObj = new JSONObject();
        try {
            Date currentDate = Calendar.getInstance().getTime();
            muteObj.put(KEY_DURATION, duration);
            muteObj.put(KEY_TIME_STAMP, currentDate.getTime());

            if (status == 1) {
                if (duration.equalsIgnoreCase("8 Hours")) {
                    Date date = TimeStampUtils.addHour(currentDate, 8);
                    muteObj.put(KEY_EXPIRE_TS, date.getTime());
                } else if (duration.equalsIgnoreCase("1 Week")) {
                    Date date = TimeStampUtils.addDay(currentDate, 7);
                    muteObj.put(KEY_EXPIRE_TS, date.getTime());
                } else if (duration.equalsIgnoreCase("1 Year")) {
                    Date date = TimeStampUtils.addYear(currentDate, 1);
                    muteObj.put(KEY_EXPIRE_TS, date.getTime());
                }
            }

            if (notifyStatus == null || notifyStatus.equals("")) {
                muteObj.put(KEY_NOTIFY_STATUS, "0");
            } else {
                muteObj.put(KEY_NOTIFY_STATUS, notifyStatus);
            }

            if (isSecretChat) {
                muteObj.put(KEY_SECRET_CHAT, status);

                if (toUserId != null && !toUserId.equals("")) {
                    valObj.put(KEY_SECRET_CHAT, muteObj);
                }
            } else {
                muteObj.put(KEY_NORMAL_CHAT, status);

                if (toUserId != null && !toUserId.equals("")) {
                    valObj.put(KEY_NORMAL_CHAT, muteObj);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject json_valobj = new JSONObject();
        JSONObject json_muteObj = new JSONObject();
        try {
            if (toUserId != null && !toUserId.trim().equals("")) {
                json_valobj.put(toUserId, valObj);
            }
            if (convId != null && !convId.trim().equals("")) {
                json_muteObj.put(convId, muteObj);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ContentValues values = new ContentValues();
        values.put(USERID, currentUserId);
        values.put(TO_USERID, toUserId);
        values.put(CONVS_ID, convId);
        values.put(KEY_TO_USERID, json_valobj.toString());
        values.put(KEY_CONVERSATION_ID, json_muteObj.toString());

        getDatabaseInstance().insert(TABLE_USERSDETAILS_MUTESTATUS, null, values);
    }

    /**
     * get opponent user contact details
     *
     * @param currentUserId input value(currentUserId)
     * @param toUserId      input value(toUserId)
     * @param convId        input value(convId)
     * @param isSecretChat  input value(isSecretChat)
     * @return response value
     */
    public MuteStatusPojo getMuteStatus(String currentUserId, String toUserId, String convId, boolean isSecretChat) {

        MuteStatusPojo muteData_top = null;
        JSONObject toUserIdObj = new JSONObject();
        JSONObject convIdObj = new JSONObject();
        String selectQuery;

        if (toUserId != null && convId != "") {
            selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_MUTESTATUS + " WHERE " +
                    TO_USERID + "='" + toUserId + "' AND " + CONVS_ID + "='" + convId + "'";
        } else if (convId.equals("")) {
            selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_MUTESTATUS + " WHERE " +
                    TO_USERID + "='" + toUserId + "'";
        } else {
            selectQuery = "SELECT * FROM " + TABLE_USERSDETAILS_MUTESTATUS + " WHERE " +
                    CONVS_ID + "='" + convId + "'";
        }

        Cursor cursor = getDatabaseInstance().rawQuery(selectQuery, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {

                String convsJson = cursor.getString(cursor.getColumnIndex(KEY_CONVERSATION_ID));
                if (toUserId != null && !toUserId.trim().equals("")) {
                    String touserJson = cursor.getString(cursor.getColumnIndex(KEY_TO_USERID));

                    try {
                        JSONObject tempObj = new JSONObject(touserJson);
                        Log.e("JSON", touserJson);
                        if (isSecretChat) {
                            toUserIdObj = tempObj.getJSONObject(toUserId).getJSONObject(KEY_SECRET_CHAT);
                        } else {
                            toUserIdObj = tempObj.getJSONObject(toUserId).getJSONObject(KEY_NORMAL_CHAT);
                            Log.e("JSON1", toUserIdObj.toString());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                try {
                    convIdObj = new JSONObject(convsJson);
                    convIdObj = convIdObj.getJSONObject(convId);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        } else {
            return null;
        }

        // check last updated mute status either by userid or convid
        long toUserIdTS = 0, convIdTS = 0;
        if (toUserIdObj.has(KEY_TIME_STAMP)) {
            try {
                toUserIdTS = toUserIdObj.getLong(KEY_TIME_STAMP);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (convIdObj.has(KEY_TIME_STAMP)) {
            try {
                convIdTS = convIdObj.getLong(KEY_TIME_STAMP);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        MuteStatusPojo muteData = null;
        JSONObject muteObj;
        if (toUserIdTS >= convIdTS) {
            muteObj = toUserIdObj;
        } else {
            muteObj = convIdObj;
        }

        try {
            Long ts = 0L;
            Long expireTS = 0L;
            if (muteObj.has(KEY_TIME_STAMP))
                ts = muteObj.getLong(KEY_TIME_STAMP);
            String duration = "";
            if (muteObj.has(KEY_DURATION))
                duration = muteObj.getString(KEY_DURATION);
            String notifyStatus = "";
            if (muteObj.has(KEY_NOTIFY_STATUS))
                notifyStatus = muteObj.getString(KEY_NOTIFY_STATUS);
            if (muteObj.has(KEY_EXPIRE_TS)) {
                expireTS = muteObj.getLong(KEY_EXPIRE_TS);
                Log.e("expireTS", expireTS.toString());
            }

            String muteStatus = "";
            if (isSecretChat) {
                if (muteObj.has(KEY_SECRET_CHAT))
                    muteStatus = muteObj.getString(KEY_SECRET_CHAT);
            } else {
                if (muteObj.has(KEY_NORMAL_CHAT))
                    muteStatus = muteObj.getString(KEY_NORMAL_CHAT);
            }

            muteData = new MuteStatusPojo();
            muteData.setTs(ts);
            muteData.setDuration(duration);
            muteData.setNotifyStatus(notifyStatus);
            muteData.setMuteStatus(muteStatus);
            muteData.setExpireTs(expireTS);

            muteData_top = muteData;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return muteData_top;
    }

    /**
     * get user Contact Details
     *
     * @param userId input value(UserId)
     * @return response valuew
     * @throws JSONException error
     */
    public JSONObject getContactDetails(String userId) throws JSONException {
        String query = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        JSONObject jsonObject = new JSONObject();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                jsonObject.put(KEY_MY_CONTACT_STATUS, cursor.getString(cursor.getColumnIndex(KEY_MY_CONTACT_STATUS)));
                jsonObject.put(KEY_LAST_SEEN_VISIBILITY, cursor.getString(cursor.getColumnIndex(KEY_LAST_SEEN_VISIBILITY)));
                jsonObject.put(KEY_PROFILE_PIC_VISIBILITY, cursor.getString(cursor.getColumnIndex(KEY_PROFILE_PIC_VISIBILITY)));
                jsonObject.put(KEY_PROFILE_STATUS_VISIBILITY, cursor.getString(cursor.getColumnIndex(KEY_PROFILE_STATUS_VISIBILITY)));
                jsonObject.put(KEY_USER_DETAILS, cursor.getString(cursor.getColumnIndex(KEY_USER_DETAILS)));
                jsonObject.put(KEY_CONTACT_SAVED_REVISION, cursor.getString(cursor.getColumnIndex(KEY_CONTACT_SAVED_REVISION)));
                jsonObject.put(KEY_DP_UPDATED_TIME, cursor.getString(cursor.getColumnIndex(KEY_DP_UPDATED_TIME)));
                jsonObject.put(KEY_BLOCKED_OPPONENET_DETAILS, cursor.getString(cursor.getColumnIndex(KEY_BLOCKED_OPPONENET_DETAILS)));
                jsonObject.put(KEY_BLOCKED_MINE_DETAILS, cursor.getString(cursor.getColumnIndex(KEY_BLOCKED_MINE_DETAILS)));
                jsonObject.put(KEY_SECRET_TIMER_DETAILS, cursor.getString(cursor.getColumnIndex(KEY_SECRET_TIMER_DETAILS)));
            }
            cursor.close();
        }

        return jsonObject;
    }

    /**
     * get Saved user contacts
     *
     * @return response value
     */
    public ArrayList<ChatappContactModel> getSavedChatappContacts() {
        ArrayList<ChatappContactModel> contactsList = new ArrayList<>();
        long savedRevision = SessionManager.getInstance(context).getContactSavedRevision();
        Cursor cursor = getDatabaseInstance().rawQuery("select * from chatapp_CONTACT__USERSDETAILS_BLOCK_UNBLOCK_STATUS", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String UserDetails = cursor.getString(cursor.getColumnIndex(KEY_USER_DETAILS));
                String revsiosn = cursor.getString(cursor.getColumnIndex(KEY_CONTACT_SAVED_REVISION));
                if (revsiosn != null) {
                    long contctSaveRevision = Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_CONTACT_SAVED_REVISION)));
                    if (contctSaveRevision >= savedRevision) {
                        try {
                            JSONObject jsonObject_userdetails = new JSONObject(UserDetails);
                            ChatappContactModel chatappContactModel = new ChatappContactModel();
                            chatappContactModel.set_id(jsonObject_userdetails.getString(USERID));
                            chatappContactModel.setFirstName(jsonObject_userdetails.getString(FIRSTNAME));
                            chatappContactModel.setStatus(jsonObject_userdetails.getString(STATUS));
                            chatappContactModel.setAvatarImageUrl(jsonObject_userdetails.getString(AVATARIMAGEURL));
                            chatappContactModel.setNumberInDevice(jsonObject_userdetails.getString(NOINDEVICE));
                            chatappContactModel.setMsisdn(jsonObject_userdetails.getString(MSISDN));
                            chatappContactModel.setType(jsonObject_userdetails.getString(TYPE));
                            chatappContactModel.setCountryCode(jsonObject_userdetails.getString(COUNTRYCODE));
                            chatappContactModel.setSelected(false);
                            chatappContactModel.setRequestStatus(jsonObject_userdetails.getString(REQUESTSTATUS));
                            contactsList.add(chatappContactModel);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                cursor.moveToNext();
            }
        }
        return contactsList;
    }

    /**
     * get Linked user Contacts
     *
     * @return response value
     */
    public ArrayList<ChatappContactModel> getLinkedChatappContacts() {
        ArrayList<ChatappContactModel> contactsList = new ArrayList<>();

        long savedRevision = SessionManager.getInstance(context).getContactSavedRevision();

        Cursor cursor = getDatabaseInstance().rawQuery("select * from chatapp_CONTACT__USERSDETAILS_BLOCK_UNBLOCK_STATUS", null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String UserDetails = cursor.getString(cursor.getColumnIndex(KEY_USER_DETAILS));
                String revsiosn = cursor.getString(cursor.getColumnIndex(KEY_CONTACT_SAVED_REVISION));
                if (revsiosn != null) {
                    long contctSaveRevision = Long.parseLong(cursor.getString(cursor.getColumnIndex(KEY_CONTACT_SAVED_REVISION)));
                    if (contctSaveRevision >= savedRevision) {
                        try {
                            JSONObject jsonObject_userdetails = new JSONObject(UserDetails);
                            if (jsonObject_userdetails.getString(REQUESTSTATUS).equals("3") && getBlockedStatus(jsonObject_userdetails.getString(USERID), false).equals("0")) {

                                ChatappContactModel chatappContactModel = new ChatappContactModel();
                                chatappContactModel.set_id(jsonObject_userdetails.getString(USERID));
                                chatappContactModel.setFirstName(jsonObject_userdetails.getString(FIRSTNAME));
                                chatappContactModel.setStatus(jsonObject_userdetails.getString(STATUS));
                                chatappContactModel.setAvatarImageUrl(jsonObject_userdetails.getString(AVATARIMAGEURL));
                                chatappContactModel.setNumberInDevice(jsonObject_userdetails.getString(NOINDEVICE));
                                chatappContactModel.setMsisdn(jsonObject_userdetails.getString(MSISDN));
                                chatappContactModel.setType(jsonObject_userdetails.getString(TYPE));
                                chatappContactModel.setCountryCode(jsonObject_userdetails.getString(COUNTRYCODE));
                                chatappContactModel.setSelected(false);
                                chatappContactModel.setRequestStatus(jsonObject_userdetails.getString(REQUESTSTATUS));
                                contactsList.add(chatappContactModel);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                }
                cursor.moveToNext();
            }
        }
        return contactsList;
    }

    /**
     * get All user Contacts
     *
     * @return response value
     */
    public ArrayList<ChatappContactModel> getAllChatappContacts() {
        ArrayList<ChatappContactModel> contactsList = new ArrayList<>();
        try {

            String query = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS;

            Cursor cursor = getDatabaseInstance().rawQuery(query, null);
            if (cursor.moveToFirst()) {
                while (!cursor.isAfterLast()) {
                    String UserDetails = cursor.getString(cursor.getColumnIndex(KEY_USER_DETAILS));
                    try {
                        JSONObject jsonObject_userdetails = new JSONObject(UserDetails);
                        ChatappContactModel chatappContactModel = new ChatappContactModel();
                        chatappContactModel.set_id(jsonObject_userdetails.getString(USERID));
                        chatappContactModel.setFirstName(jsonObject_userdetails.getString(FIRSTNAME));
                        chatappContactModel.setStatus(jsonObject_userdetails.getString(STATUS));
                        chatappContactModel.setAvatarImageUrl(jsonObject_userdetails.getString(AVATARIMAGEURL));
                        chatappContactModel.setNumberInDevice(jsonObject_userdetails.getString(NOINDEVICE));
                        chatappContactModel.setMsisdn(jsonObject_userdetails.getString(MSISDN));
                        chatappContactModel.setType(jsonObject_userdetails.getString(TYPE));
                        chatappContactModel.setSelected(false);
                        chatappContactModel.setCountryCode(jsonObject_userdetails.getString(COUNTRYCODE));
                        contactsList.add(chatappContactModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    cursor.moveToNext();
                }
            }


//            if (cursor != null) {
//                while (cursor.moveToNext()) {
//                    String UserDetails = cursor.getString(cursor.getColumnIndex(KEY_USER_DETAILS));
//                    try {
//                        JSONObject jsonObject_userdetails = new JSONObject(UserDetails);
//                        ChatappContactModel chatappContactModel = new ChatappContactModel();
//                        chatappContactModel.set_id(jsonObject_userdetails.getString(USERID));
//                        chatappContactModel.setFirstName(jsonObject_userdetails.getString(FIRSTNAME));
//                        chatappContactModel.setStatus(jsonObject_userdetails.getString(STATUS));
//                        chatappContactModel.setAvatarImageUrl(jsonObject_userdetails.getString(AVATARIMAGEURL));
//                        chatappContactModel.setNumberInDevice(jsonObject_userdetails.getString(NOINDEVICE));
//                        chatappContactModel.setMsisdn(jsonObject_userdetails.getString(MSISDN));
//                        chatappContactModel.setType(jsonObject_userdetails.getString(TYPE));
//                        chatappContactModel.setSelected(false);
//                        chatappContactModel.setCountryCode(jsonObject_userdetails.getString(COUNTRYCODE));
//                        contactsList.add(chatappContactModel);
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//
//                }
//                cursor.close();
//            }

        } catch (Exception e) {
        }

        return contactsList;
    }

    /**
     * update Frequent Contact users
     *
     * @param userId    input value(UserId)
     * @param convId    input value(convId)
     * @param timeStamp input value(timeStamp)
     */
    public void updateFrequentContact(String userId, String convId, String timeStamp) {

        String selectQuery = "SELECT * FROM " + TABLE_FREQUENT_CONTACTS + " WHERE " +
                CONVS_ID + "='" + convId + "'";

        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                update_FrequentContact(userId, convId, timeStamp);
            } else {
                selectCur.close();
                insert_FrequentContact(userId, convId, timeStamp);
            }
        }
    }

    /**
     * insert Frequent Contact users
     *
     * @param userId    input value(UserId)
     * @param convId    input value(convId)
     * @param timeStamp input value(timeStamp)
     */
    private void insert_FrequentContact(String userId, String convId, String timeStamp) {

        ContentValues values = new ContentValues();
        values.put(USERID, userId);
        values.put(CONVS_ID, convId);
        values.put(REVISION_COUNT, 1);

        getDatabaseInstance().insert(TABLE_FREQUENT_CONTACTS, null, values);

    }

    /**
     * update Frequent Contact users
     *
     * @param userId    input value(UserId)
     * @param convId    input value(convId)
     * @param timeStamp input value(timeStamp)
     */
    public void update_FrequentContact(String userId, String convId, String timeStamp) {

        String query = "SELECT * FROM " + TABLE_FREQUENT_CONTACTS + " WHERE " +
                CONVS_ID + "='" + convId + "'";

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int count_revision = cursor.getInt(cursor.getColumnIndex(REVISION_COUNT));

                ContentValues values = new ContentValues();
                values.put(USERID, userId);
                values.put(CONVS_ID, convId);
                values.put(REVISION_COUNT, count_revision + 1);

                getDatabaseInstance().update(TABLE_FREQUENT_CONTACTS, values, CONVS_ID + "='" + convId + "'", null);
            }
            cursor.close();
        }

    }

    /**
     * get Frequent Contacts list
     *
     * @param context current activity
     * @param userId  input value(UserId)
     * @return response value
     */
    public List<ChatappContactModel> getFrequentContacts(Context context, String userId) {
        UserInfoSession userInfoSession = new UserInfoSession(context);
        List<ChatappContactModel> dataList = new ArrayList<>();
//        String selectQuery = "SELECT * MAX(" + REVISION_COUNT + ")" +
//                "FROM " + TABLE_FREQUENT_CONTACTS + " WHERE " +
//                USERID + "='" + userId + "'";

//        String selectQuery = "SELECT * FROM " + TABLE_FREQUENT_CONTACTS + " WHERE " + USERID + "='"
//                + userId + "'";

//        Log.d("Query", selectQuery);
        Cursor cursor = getDatabaseInstance().rawQuery("SELECT * FROM chatapp_CONTACT__FREQUENT_CONTACTS where UserID = '" + userId + "'  order by revisioncount desc", null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                String conv = cursor.getString(cursor.getColumnIndex(CONVS_ID));
                if (conv != null) {
                    String userIds = userInfoSession.getReceiverIdByConvId(conv);

                    if (userIds != null && !userIds.equals("") && getBlockedStatus(userIds, false).equals("0")) {
                        ChatappContactModel data = getUserOpponenetDetails(userIds);
                        if (data.getRequestStatus().equals("3")) {
                            dataList.add(data);
                        }
                    }
                    if (dataList.size() > 2) {
                        break;
                    }
                }
            }
            cursor.close();
        }

        return dataList;
    }

    /**
     * get UserImage
     *
     * @param userId input value(UserId)
     * @return response value
     */
    public String getUserImage(final String userId) {
        String query = "SELECT * FROM " + TABLE_USERSDETAILS_BLOCK_UNBLOCK_STATUS + " WHERE " + USERID + "='"
                + userId + "'";
//        ChatappContactModel chatappContactModel = new ChatappContactModel();
        String getImageUrl = "";
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                try {
                    String data = cursor.getString(cursor.getColumnIndex(KEY_USER_DETAILS));
                    if (data != null) {
                        JSONObject jsonObject_userdetails = new JSONObject(data);
                        getImageUrl = jsonObject_userdetails.getString(AVATARIMAGEURL);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }
        return getImageUrl;
    }
}

