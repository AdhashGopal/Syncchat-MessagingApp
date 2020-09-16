package com.chatapp.synchat.core.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.CallItemChat;
import com.chatapp.synchat.core.model.ChatLockPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.OfflineRetryEventPojo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * created by  Adhash Team on 12/21/2017.
 */
public class MessageDbController extends SQLiteOpenHelper {


    /**
     * Database field name & tablenames
     */
    public static final String DB_NAME = "Chat";
    public static final int MESSAGE_SELECTION_UNLIMITED_FOR_SEARCH = 5000;
    public static final int MESSAGE_SELECTION_LIMIT = 120;
    public static final int MESSAGE_SELECTION_LIMIT_FIRST_TIME = 50;
    public static final int MESSAGE_PAGE_LOADED_LIMIT = 100;
    private static final int DB_VERSION = 1;
    private static final String TAG = MessageDbController.class.getSimpleName();
    private final String TABLE_CHAT_LIST = "ChatList";
    private final String TABLE_MESSAGES = "ChatMessages";
    private final String TABLE_CHAT_LOCK = "ChatLock";
    private final String TABLE_TEMP_DELETE_MESSAGES = "TempDeleteMessages";
    private final String TABLE_TEMP_STAR_MESSAGES = "TempStarMessages";
    private final String TABLE_TEMP_SEND_NEW_MESSAGE = "TempSendNewMessage";
    private final String TABLE_OFFLINE_EVENTS = "OfflineEvents";
    private final String TABLE_CALL_LOGS = "CallLogs";
    private final String COLUMN_OID = "OID";
    private final String COLUMN_STATUS = "Status";
    private final String COLUMN_TIME_STAMP = "TimeStamp";
    private final String COLUMN_MESSAGE_ID = "MessageId";
    private final String COLUMN_CHAT_ID = "ChatId";
    private final String COLUMN_RECEIVER_ID = "ReceiverId";
    private final String COLUMN_MESSAGE_DATA = "MessageData";
    private final String COLUMN_DELIVERY_STATUS = "DeliveryStatus";
    private final String COLUMN_IS_SELF = "IsSelf";
    private final String COLUMN_CHAT_TYPE = "ChatType";
    private final String COLUMN_CLEAR_STATUS = "ClearStatus";
    private final String COLUMN_SENDER_ID = "SenderId";
    private final String COLUMN_PASSWORD = "Password";
    private final String COLUMN_LOCK_STATUS = "LockStatus";
    private final String COLUMN_RECORD_ID = "RecordId";
    private final String COLUMN_DELETE_MESSAGE_DATA = "DeleteMessageData";
    private final String COLUMN_STAR_MESSAGE_DATA = "StarMessageData";
    private final String COLUMN_EVENT_ID = "EventId";
    private final String COLUMN_EVENT_NAME = "EventName";
    private final String COLUMN_EVENT_DATA = "EventData";
    private final String COLUMN_CALL_ID = "CallId";
    private final String COLUMN_CALL_DATA = "CallData";
    String CREATE_TABLE_MESSAGE = "CREATE TABLE " + TABLE_MESSAGES + "(" + COLUMN_OID
            + " INTEGER PRIMARY KEY," + COLUMN_MESSAGE_ID + " TEXT," + COLUMN_RECEIVER_ID
            + " TEXT," + COLUMN_CHAT_ID + " TEXT," + COLUMN_RECORD_ID + " TEXT," + COLUMN_MESSAGE_DATA
            + " TEXT," + COLUMN_DELIVERY_STATUS + " TEXT," + COLUMN_IS_SELF + " INTEGER,"
            + COLUMN_CHAT_TYPE + " TEXT," + COLUMN_TIME_STAMP + " INTEGER," + COLUMN_STATUS + " INTEGER)";
    String CREATE_TABLE_CHAT_LIST = "CREATE TABLE " + TABLE_CHAT_LIST + "(" + COLUMN_OID
            + " INTEGER PRIMARY KEY," + COLUMN_RECEIVER_ID + " TEXT," + COLUMN_CHAT_ID
            + " TEXT," + COLUMN_MESSAGE_DATA + " TEXT," + COLUMN_CHAT_TYPE + " TEXT,"
            + COLUMN_CLEAR_STATUS + " INTEGER," + COLUMN_TIME_STAMP + " INTEGER," + COLUMN_STATUS
            + " INTEGER)";
    String CREATE_TABLE_CHAT_LOCK = "CREATE TABLE " + TABLE_CHAT_LOCK + "(" + COLUMN_OID
            + " INTEGER PRIMARY KEY," + COLUMN_SENDER_ID + " TEXT," + COLUMN_RECEIVER_ID
            + " TEXT," + COLUMN_CHAT_TYPE + " TEXT," + COLUMN_PASSWORD + " TEXT,"
            + COLUMN_LOCK_STATUS + " TEXT," + COLUMN_STATUS + " INTEGER)";
    String CREATE_TABLE_TEMP_DELETE_MESSAGES = "CREATE TABLE " + TABLE_TEMP_DELETE_MESSAGES
            + "(" + COLUMN_OID + " INTEGER PRIMARY KEY," + COLUMN_RECORD_ID + " TEXT,"
            + COLUMN_DELETE_MESSAGE_DATA + " TEXT," + COLUMN_CHAT_TYPE + " TEXT," + COLUMN_STATUS
            + " INTEGER)";
    String CREATE_TABLE_TEMP_STAR_MESSAGES = "CREATE TABLE " + TABLE_TEMP_STAR_MESSAGES
            + "(" + COLUMN_OID + " INTEGER PRIMARY KEY," + COLUMN_RECORD_ID + " TEXT,"
            + COLUMN_STAR_MESSAGE_DATA + " TEXT," + COLUMN_CHAT_TYPE + " TEXT," + COLUMN_STATUS
            + " INTEGER)";
    String CREATE_TABLE_TEMP_SEND_NEW_MESSAGE = "CREATE TABLE " + TABLE_TEMP_SEND_NEW_MESSAGE + "("
            + COLUMN_OID + " INTEGER PRIMARY KEY," + COLUMN_EVENT_ID + " TEXT," + COLUMN_EVENT_NAME
            + " TEXT," + COLUMN_EVENT_DATA + " TEXT," + COLUMN_STATUS + " INTEGER)";
    String CREATE_TABLE_OFFLINE_EVENTS = "CREATE TABLE " + TABLE_OFFLINE_EVENTS + "("
            + COLUMN_OID + " INTEGER PRIMARY KEY," + COLUMN_EVENT_ID + " TEXT," + COLUMN_EVENT_NAME
            + " TEXT," + COLUMN_EVENT_DATA + " TEXT," + COLUMN_STATUS + " INTEGER)";
    String CREATE_TABLE_CALL_LOGS = "CREATE TABLE " + TABLE_CALL_LOGS + "(" + COLUMN_OID
            + " INTEGER PRIMARY KEY," + COLUMN_CALL_ID + " TEXT," + COLUMN_CALL_DATA + " TEXT,"
            + COLUMN_STATUS + " INTEGER)";
    private int isBlocked = 0;
    private Context mContext;
    private Gson gson;
    private GsonBuilder gsonBuilder;
    private String mCurrentUserId;

    private SQLiteDatabase mDatabaseInstance;

    /**
     * create constructor & object create for GsonBuilder
     *
     * @param context current activity
     */
    public MessageDbController(Context context) {
        super(context, DB_NAME, null, DB_VERSION);

        this.mContext = context;
        gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
    }

    /**
     * Sqlite database initialization
     *
     * @return database value
     */
    private SQLiteDatabase getDatabaseInstance() {
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
    public void close() {
        if (mDatabaseInstance != null && mDatabaseInstance.isOpen()) {
            mDatabaseInstance.close();
        }
    }

    /**
     * create table for sqlite database
     *
     * @param db execute the database table
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_MESSAGE);
        db.execSQL(CREATE_TABLE_CHAT_LIST);
        db.execSQL(CREATE_TABLE_CHAT_LOCK);
        db.execSQL(CREATE_TABLE_TEMP_DELETE_MESSAGES);
        db.execSQL(CREATE_TABLE_TEMP_STAR_MESSAGES);
        db.execSQL(CREATE_TABLE_CALL_LOGS);
        db.execSQL(CREATE_TABLE_TEMP_SEND_NEW_MESSAGE);
        db.execSQL(CREATE_TABLE_OFFLINE_EVENTS);


    }

    /**
     * if the files are exiting means upgrade the table
     *
     * @param db execute the database file
     * @param i  check the old version
     * @param i1 check the new version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_LIST);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT_LOCK);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMP_DELETE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMP_STAR_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALL_LOGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEMP_SEND_NEW_MESSAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OFFLINE_EVENTS);


        // Create tables again
        onCreate(db);
    }

    /**
     * delete Database chat table
     */
    public void deleleChatTable() {
        mDatabaseInstance.execSQL("DELETE FROM " + TABLE_MESSAGES);
        mDatabaseInstance.execSQL("DELETE FROM " + TABLE_CHAT_LOCK);
        mDatabaseInstance.execSQL("DELETE FROM " + TABLE_TEMP_DELETE_MESSAGES);
        mDatabaseInstance.execSQL("DELETE FROM " + TABLE_TEMP_SEND_NEW_MESSAGE);
        mDatabaseInstance.execSQL("DELETE FROM " + TABLE_TEMP_STAR_MESSAGES);
        mDatabaseInstance.execSQL("DELETE FROM " + TABLE_CALL_LOGS);
        mDatabaseInstance.execSQL("DELETE FROM " + TABLE_CHAT_LIST);
    }

    /**
     * update Chat Message
     *
     * @param item     input value(item)
     * @param chatType input value(chatType)
     */
    public void updateChatMessage(MessageItemChat item, String chatType) {

        String selectQuery = "SELECT " + COLUMN_OID + " FROM " + TABLE_MESSAGES + " WHERE " +
                COLUMN_MESSAGE_ID + "='" + item.getMessageId() + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                updateMessage(item);
            } else {
                selectCur.close();
                insertNewMessage(item, chatType);
            }
        }
    }

    /**
     * update ChatMessage
     *
     * @param item      input value(item)
     * @param chatType  input value(chatType)
     * @param isBlocked input value(isBlocked)
     */
    public void updateChatMessage(MessageItemChat item, String chatType, int isBlocked) {

        this.isBlocked = isBlocked;

        String selectQuery = "SELECT " + COLUMN_OID + " FROM " + TABLE_MESSAGES + " WHERE " +
                COLUMN_MESSAGE_ID + "='" + item.getMessageId() + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                updateMessage(item);
            } else {
                selectCur.close();
                insertNewMessage(item, chatType);
            }
        }
    }

    /**
     * check Chat Message
     *
     * @param item     input value(item)
     * @param chatType input value(chatType)
     * @return
     */
    public boolean checkChatMessage(MessageItemChat item, String chatType) {

        String selectQuery = "SELECT " + COLUMN_OID + " FROM " + TABLE_MESSAGES + " WHERE " +
                COLUMN_MESSAGE_ID + "='" + item.getMessageId() + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.getCount() > 0) {
                selectCur.close();
                return false;
            } else {
                selectCur.close();

            }
        }
        return true;
    }

    /**
     * update chat messageb
     *
     * @param item input value(item)
     */
    private void updateMessage(MessageItemChat item) {
        String jsonMessage = gson.toJson(item);
        ContentValues updateValues = new ContentValues();
        updateValues.put(COLUMN_MESSAGE_DATA, jsonMessage);
        updateValues.put(COLUMN_DELIVERY_STATUS, item.isBlockedMessage() ? "22" : item.getDeliveryStatus());

        String recordId = item.getRecordId();
        if (recordId != null && !recordId.equals("")) {
            updateValues.put(COLUMN_RECORD_ID, item.getRecordId());
        }

        getDatabaseInstance().update(TABLE_MESSAGES, updateValues, COLUMN_MESSAGE_ID + "='" + item.getMessageId() + "'", null);
    }


    /**
     * insert new message
     *
     * @param message  input value(message)
     * @param chatType input value(chatType)
     */
    private void insertNewMessage(MessageItemChat message, String chatType) {
        if (message == null) {
            Log.e(TAG, "insertNewMessage: message null.. check ");
            return;
        }
        String msgData = gson.toJson(message);

        String receiverId = message.getReceiverID();
        String chatId = getChatId(receiverId, chatType);

        Long ts = AppUtils.parseLong(message.getTS());

        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE_ID, message.getMessageId());
        values.put(COLUMN_RECEIVER_ID, message.getReceiverID());
        values.put(COLUMN_CHAT_ID, chatId);
        values.put(COLUMN_MESSAGE_DATA, msgData);
        values.put(COLUMN_DELIVERY_STATUS, message.isBlockedMessage() ? "22" : message.getDeliveryStatus());
        values.put(COLUMN_CHAT_TYPE, chatType);
        values.put(COLUMN_TIME_STAMP, ts);
        values.put(COLUMN_STATUS, 1);

        String recordId = message.getRecordId();
        if (recordId != null && !recordId.equals("")) {
            values.put(COLUMN_RECORD_ID, message.getRecordId());
        }

        if (message.isSelf()) {
            values.put(COLUMN_IS_SELF, 1);
        } else {
            values.put(COLUMN_IS_SELF, 0);
        }
        // Inserting Row
        getDatabaseInstance().insert(TABLE_MESSAGES, null, values);
        updateChatList(message.getReceiverID(), msgData, chatType, message.getTS());
    }


    private void insertNewBroadcastMessage(MessageItemChat message, String chatType) {
        if (message == null) {
            Log.e(TAG, "insertNewBroadcastMessage: message null.. check ");
            return;
        }
        String msgData = gson.toJson(message);

        String receiverId = message.getReceiverID();
        String chatId = getChatId(receiverId, chatType);

        Long ts = AppUtils.parseLong(message.getTS());

        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE_ID, message.getMessageId());
        values.put(COLUMN_RECEIVER_ID, message.getReceiverID());
        values.put(COLUMN_CHAT_ID, chatId);
        values.put(COLUMN_MESSAGE_DATA, msgData);
        values.put(COLUMN_DELIVERY_STATUS, message.getDeliveryStatus());
        values.put(COLUMN_CHAT_TYPE, chatType);
        values.put(COLUMN_TIME_STAMP, ts);
        values.put(COLUMN_STATUS, 1);

        String recordId = message.getRecordId();
        if (recordId != null && !recordId.equals("")) {
            values.put(COLUMN_RECORD_ID, message.getRecordId());
        }

        if (message.isSelf()) {
            values.put(COLUMN_IS_SELF, 1);
        } else {
            values.put(COLUMN_IS_SELF, 0);
        }
        // Inserting Row
        getDatabaseInstance().insert(TABLE_MESSAGES, null, values);
        updateChatList(message.getReceiverID(), msgData, chatType, message.getTS());
    }

    /**
     * getting select All Chat Messages
     *
     * @param chatId   input value(chatId)
     * @param chatType input value(chatType)
     * @return response value
     */
    public ArrayList<MessageItemChat> selectAllChatMessages(String chatId, String chatType) {
        // Chat id referred by below things ----> single_chat(from-to), group_chat(from-to-g), secret_chat(from-to-secret)

        /*String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + "='" + chatId
                + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "' ORDER BY 1 DESC";*/

        String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + "='" + chatId
                /*+ "' AND " + COLUMN_CHAT_TYPE + "='" + chatType */ + "' ORDER BY 1 DESC";

        ArrayList<MessageItemChat> chats = new ArrayList<>();
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_DATA));
                MessageItemChat messageItemChat = gson.fromJson(data, MessageItemChat.class);
                chats.add(messageItemChat);
            }
            cursor.close();
        }
        return chats;
    }

    /**
     * getting select all Messages With Limit
     *
     * @param chatId   input value(chatId)
     * @param chatType input value(chatType)
     * @param ts       input value(ts)
     * @param limit    input value(limit)
     * @return response value
     */
    public ArrayList<MessageItemChat> selectAllMessagesWithLimit(String chatId, String chatType, String ts, int limit) {
        // Chat id referred by below things ----> single_chat(from-to), group_chat(from-to-g), secret_chat(from-to-secret)

        /*String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + "='" + chatId
                + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "' ORDER BY 1 DESC";*/

        String query;
        long timeStamp;
        try {
            timeStamp = Long.parseLong(ts);
        } catch (NumberFormatException e) {
            timeStamp = 0;
        }

        if (timeStamp == 0) {
            query = "SELECT DISTINCT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + "='" + chatId
                    + "' ORDER BY 1 DESC LIMIT " + limit;
        } else {
            query = "SELECT DISTINCT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + "='" + chatId
                    + "' AND " + COLUMN_TIME_STAMP + "<" + timeStamp + " ORDER BY 1 DESC LIMIT " + limit;
        }

      /*  if (timeStamp == 0) {
            query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + "='" + chatId
                    + "' ORDER BY 1 DESC LIMIT " + limit+" GROUP BY "+COLUMN_MESSAGE_ID;
        } else {
            query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + "='" + chatId
                    + "' AND " + COLUMN_TIME_STAMP + "<" + timeStamp + " ORDER BY 1 DESC LIMIT " + limit+" GROUP BY "+COLUMN_MESSAGE_ID;
        }*/

        ArrayList<MessageItemChat> chats = new ArrayList<>();
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_DATA));
                MessageItemChat messageItemChat = gson.fromJson(data, MessageItemChat.class);
//                if(Integer.parseInt(messageItemChat.getMessageType())==MessageFactory.text)
//                {
//                    EDAlgorithm edAlgorithm=new EDAlgorithm();
//                    messageItemChat.setTextMessage(edAlgorithm.dec( messageItemChat.getTextMessage()));
//
//                }
                if (!chats.contains(messageItemChat)) {
                    chats.add(messageItemChat);
                }

            }
            cursor.close();
        }
        return chats;
    }

    /**
     * get update ChatList
     *
     * @param receiverId input value(receiverId)
     * @param msgData    input value(msgData)
     * @param chatType   input value(chatType)
     * @param ts         input value(ts)
     */
    private void updateChatList(String receiverId, String msgData, String chatType, String ts) {

        String selectQuery = "SELECT * FROM " + TABLE_CHAT_LIST + " WHERE " + COLUMN_CHAT_ID + " LIKE '" + mCurrentUserId
                + "%' AND " + COLUMN_RECEIVER_ID + "='" + receiverId + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        Long timeStamp = Long.parseLong(ts);

        if (selectCur != null) {
            if (selectCur.moveToFirst()) {
                int rowId = selectCur.getInt(selectCur.getColumnIndex(COLUMN_OID));

                ContentValues updateValues = new ContentValues();
                updateValues.put(COLUMN_MESSAGE_DATA, msgData);
                updateValues.put(COLUMN_CLEAR_STATUS, MessageFactory.CHAT_STATUS_UNCLEARED);
                updateValues.put(COLUMN_TIME_STAMP, timeStamp);
                /*updateValues.put(COLUMN_CHAT_TYPE, chatType);
                updateValues.put(COLUMN_RECEIVER_ID, receiverId);
                updateValues.put(COLUMN_TIME_STAMP, ts);
                updateValues.put(COLUMN_STATUS, 1);*/

                getDatabaseInstance().update(TABLE_CHAT_LIST, updateValues, COLUMN_OID + "=" + rowId, null);
                selectCur.close();
            } else {
                selectCur.close();

                String chatId = getChatId(receiverId, chatType);

                ContentValues chatListValues = new ContentValues();
                chatListValues.put(COLUMN_RECEIVER_ID, receiverId);
                chatListValues.put(COLUMN_CHAT_ID, chatId);
                chatListValues.put(COLUMN_CHAT_TYPE, chatType);
                chatListValues.put(COLUMN_CLEAR_STATUS, MessageFactory.CHAT_STATUS_UNCLEARED);
                chatListValues.put(COLUMN_MESSAGE_DATA, msgData);
                chatListValues.put(COLUMN_TIME_STAMP, timeStamp);
                chatListValues.put(COLUMN_STATUS, 1);

                getDatabaseInstance().insert(TABLE_CHAT_LIST, null, chatListValues);
            }
        }
    }

    /**
     * get Chat Cleared Status
     *
     * @param receiverId input value(receiverId)
     * @param chatType   input value(chatType)
     * @return
     */
    public int getChatClearedStatus(String receiverId, String chatType) {
        String selectQuery = "SELECT " + COLUMN_OID + "," + COLUMN_CLEAR_STATUS + " FROM " + TABLE_CHAT_LIST + " WHERE " +
                COLUMN_RECEIVER_ID + "='" + receiverId + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        int clearedStatus = MessageFactory.CHAT_STATUS_UNCLEARED;

        if (selectCur != null) {
            if (selectCur.moveToNext()) {
                int id = selectCur.getInt(selectCur.getColumnIndex(COLUMN_OID));
                clearedStatus = selectCur.getInt(selectCur.getColumnIndex(COLUMN_CLEAR_STATUS));
            }
            selectCur.close();
        }

        return clearedStatus;
    }

    /**
     * update Chat List Status
     *
     * @param receiverId input value(receiverId)
     * @param chatType   input value(chatType)
     * @param status     input value(status)
     */
    public void updateChatListStatus(String receiverId, String chatType, int status) {

        String selectQuery = "SELECT " + COLUMN_OID + " FROM " + TABLE_CHAT_LIST + " WHERE " +
                COLUMN_RECEIVER_ID + "='" + receiverId + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.moveToFirst()) {
                int rowId = selectCur.getInt(selectCur.getColumnIndex(COLUMN_OID));
                selectCur.close();

                ContentValues updateValues = new ContentValues();
                updateValues.put(COLUMN_CLEAR_STATUS, status);

                getDatabaseInstance().update(TABLE_CHAT_LIST, updateValues, COLUMN_OID + "=" + rowId, null);
            } else {
                selectCur.close();
            }
        }
    }

    /**
     * select ChatList
     *
     * @param chatType input value(chatType)
     * @return response value
     */
    public ArrayList<MessageItemChat> selectChatList(String chatType) {
        mCurrentUserId = SessionManager.getInstance(mContext).getCurrentUserID();
        String query = "SELECT * FROM " + TABLE_CHAT_LIST + " WHERE " + COLUMN_CHAT_ID + " LIKE '" + mCurrentUserId
                + "%' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";

        ArrayList<MessageItemChat> chats = new ArrayList<>();
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_DATA));
                MessageItemChat messageItemChat = gson.fromJson(data, MessageItemChat.class);
                chats.add(messageItemChat);
            }
            cursor.close();
        }
        return chats;
    }

    /**
     * getting all select Starred Messages
     *
     * @return response value
     */
    public ArrayList<MessageItemChat> selectAllStarredMessages() {
        mCurrentUserId = SessionManager.getInstance(mContext).getCurrentUserID();
        String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + " LIKE '" + mCurrentUserId
                + "%' AND " + COLUMN_CHAT_TYPE + "!='" + MessageFactory.CHAT_TYPE_SECRET + "'";

        ArrayList<MessageItemChat> chats = new ArrayList<>();
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_DATA));
                MessageItemChat messageItemChat = gson.fromJson(data, MessageItemChat.class);
                if (messageItemChat != null && messageItemChat.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED + "")) {
                    chats.add(messageItemChat);
                }
            }
            cursor.close();
        }
        return chats;
    }

    /**
     * getting Message By RecordId
     *
     * @param recordId input value(recordId)
     * @return response value
     */
    public MessageItemChat getMessageByRecordId(String recordId) {

        mCurrentUserId = SessionManager.getInstance(mContext).getCurrentUserID();
        String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_RECORD_ID + "='"
                + recordId + "'";

        MessageItemChat chat = null;
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                String data = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_DATA));
                chat = gson.fromJson(data, MessageItemChat.class);
            }
            cursor.close();
        }
        return chat;
    }

    /**
     * update Send NewMessage
     *
     * @param pojo getting value from pojo class
     */
    public void updateSendNewMessage(OfflineRetryEventPojo pojo) {
        ContentValues offlineMsgValues = new ContentValues();
        offlineMsgValues.put(COLUMN_EVENT_ID, pojo.getEventId());
        offlineMsgValues.put(COLUMN_EVENT_NAME, pojo.getEventName());
        offlineMsgValues.put(COLUMN_EVENT_DATA, pojo.getEventObject().toString());
        offlineMsgValues.put(COLUMN_STATUS, 1);

        getDatabaseInstance().insert(TABLE_TEMP_SEND_NEW_MESSAGE, null, offlineMsgValues);
    }

    /**
     * get Send NewMessage
     *
     * @param currentUserId input value(currentUserId)
     * @return response value
     */
    public ArrayList<OfflineRetryEventPojo> getSendNewMessage(String currentUserId) {
        String query = "SELECT * FROM " + TABLE_TEMP_SEND_NEW_MESSAGE + " WHERE " + COLUMN_EVENT_ID
                + " LIKE '" + currentUserId + "%'";

        ArrayList<OfflineRetryEventPojo> offlineMessages = new ArrayList<>();
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String eventId = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_ID));
                String eventName = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_NAME));
                String eventData = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_DATA));

                try {
                    JSONObject object = new JSONObject(eventData);

                    OfflineRetryEventPojo pojo = new OfflineRetryEventPojo();
                    pojo.setEventId(eventId);
                    pojo.setEventName(eventName);
                    pojo.setEventObject(object);
                    offlineMessages.add(pojo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }

        return offlineMessages;
    }

    /***
     *delete Send NewMessage
     * @param msgId input value(msgId)
     */
    public void deleteSendNewMessage(String msgId) {
        String query = "SELECT " + COLUMN_OID + " FROM " + TABLE_TEMP_SEND_NEW_MESSAGE + " WHERE "
                + COLUMN_EVENT_ID + "='" + msgId + "'";

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_OID));
                getDatabaseInstance().delete(TABLE_TEMP_SEND_NEW_MESSAGE, COLUMN_OID + "=" + id, null);
            }
            cursor.close();
        }

    }

    public void deleteAllReadMessages() {
       /* String query = "DELETE " + " FROM " + TABLE_MESSAGES + " WHERE "
                + COLUMN_DELIVERY_STATUS + "='" + "3" + "'";
        getDatabaseInstance().rawQuery(query, null);*/

        getDatabaseInstance().delete(TABLE_MESSAGES, COLUMN_DELIVERY_STATUS + "=" + "3", null);

    }

    /**
     * update Offline Events
     *
     * @param pojo getting value from pojo class
     */
    public void updateOfflineEvents(OfflineRetryEventPojo pojo) {
        mCurrentUserId = SessionManager.getInstance(mContext).getCurrentUserID();
        String eventId = mCurrentUserId + "-" + Calendar.getInstance().getTimeInMillis();

        ContentValues offlineEventValues = new ContentValues();
        offlineEventValues.put(COLUMN_EVENT_ID, eventId);
        offlineEventValues.put(COLUMN_EVENT_NAME, pojo.getEventName());
        offlineEventValues.put(COLUMN_EVENT_DATA, pojo.getEventObject().toString());
        offlineEventValues.put(COLUMN_STATUS, 1);

        getDatabaseInstance().insert(TABLE_OFFLINE_EVENTS, null, offlineEventValues);
    }

    /**
     * get offline Events
     *
     * @param currentUserId input value(currentUserId)
     * @return response value
     */
    public ArrayList<OfflineRetryEventPojo> getOfflineEvents(String currentUserId) {
        String query = "SELECT * FROM " + TABLE_OFFLINE_EVENTS + " WHERE " + COLUMN_EVENT_ID
                + " LIKE '" + currentUserId + "%'";

        ArrayList<OfflineRetryEventPojo> offlineEvents = new ArrayList<>();
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String eventId = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_ID));
                String eventName = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_NAME));
                String eventData = cursor.getString(cursor.getColumnIndex(COLUMN_EVENT_DATA));

                try {
                    JSONObject object = new JSONObject(eventData);

                    OfflineRetryEventPojo pojo = new OfflineRetryEventPojo();
                    pojo.setEventId(eventId);
                    pojo.setEventName(eventName);
                    pojo.setEventObject(object);
                    offlineEvents.add(pojo);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }

        return offlineEvents;
    }


    // Other functions

    /**
     * update Chat LockData
     *
     * @param receiverId input value(receiverId)
     * @param chatId     input value(chatId)
     * @param status     input value(status)
     * @param password   input value(password)
     * @param chatType   input value(chatType)
     */
    public void updateChatLockData(String receiverId, String chatId, String status, String password, String chatType) {

        mCurrentUserId = SessionManager.getInstance(mContext).getCurrentUserID();

        String selectQuery = "SELECT " + COLUMN_OID + " FROM " + TABLE_CHAT_LOCK + " WHERE " +
                COLUMN_SENDER_ID + "='" + mCurrentUserId + "' AND " + COLUMN_RECEIVER_ID
                + "='" + receiverId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.moveToNext()) {
                int id = selectCur.getInt(selectCur.getColumnIndex(COLUMN_OID));
                selectCur.close();

                ContentValues values = new ContentValues();
                values.put(COLUMN_PASSWORD, password);
                values.put(COLUMN_LOCK_STATUS, status);
                values.put(COLUMN_CHAT_TYPE, chatType);
                values.put(COLUMN_STATUS, 1);

                getDatabaseInstance().update(TABLE_CHAT_LOCK, values, COLUMN_OID + "=" + id, null);
            } else {
                selectCur.close();
                ContentValues values = new ContentValues();
                values.put(COLUMN_SENDER_ID, mCurrentUserId);
                values.put(COLUMN_RECEIVER_ID, receiverId);
                values.put(COLUMN_PASSWORD, password);
                values.put(COLUMN_CHAT_TYPE, chatType);
                values.put(COLUMN_LOCK_STATUS, status);
                values.put(COLUMN_STATUS, 1);

                getDatabaseInstance().insert(TABLE_CHAT_LOCK, null, values);
            }
        }

    }

    /**
     * get Chat LockData
     *
     * @param receiverId input value(receiverId)
     * @param chatType   input value(chatType)
     * @return response value
     */
    public ChatLockPojo getChatLockData(String receiverId, String chatType) {
        ChatLockPojo pojo = null;

        mCurrentUserId = SessionManager.getInstance(mContext).getCurrentUserID();

        String query = "SELECT * FROM " + TABLE_CHAT_LOCK + " WHERE " + COLUMN_SENDER_ID + "='" + mCurrentUserId
                + "' AND " + COLUMN_RECEIVER_ID + "='" + receiverId + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                String password = cursor.getString(cursor.getColumnIndex(COLUMN_PASSWORD));
                String lockStatus = cursor.getString(cursor.getColumnIndex(COLUMN_LOCK_STATUS));

                pojo = new ChatLockPojo();
                pojo.setSenderId(mCurrentUserId);
                pojo.setReceiverId(receiverId);
                pojo.setPassword(password);
                pojo.setStatus(lockStatus);
            }
            cursor.close();
        }

        return pojo;
    }

    /**
     * getting ChatId
     *
     * @param receiverId input value(receiverId)
     * @param chatType   input value(chatType)
     * @return
     */
    private String getChatId(String receiverId, String chatType) {
        mCurrentUserId = SessionManager.getInstance(mContext).getCurrentUserID();
        String chatId = mCurrentUserId + "-" + receiverId;
        if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
            chatId = chatId + "-g";
        } else if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_SECRET)) {
            chatId = chatId + "-secret";
        }
//        else if(chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_BROADCAST))
//        {
//            chatId = chatId + "-g";
//        }
        return chatId;
    }

    /**
     * Delete chat list
     *
     * @param chatId   input value(chatId)
     * @param chatType input value(chatType)
     */
    public void deleteChat(String chatId, String chatType) {
        String query = "SELECT " + COLUMN_OID + " FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + "='" + chatId
                + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_OID));
                getDatabaseInstance().delete(TABLE_MESSAGES, COLUMN_OID + "=" + id, null);
            }
            cursor.close();
        }

        String chatListQuery = "SELECT " + COLUMN_OID + " FROM " + TABLE_CHAT_LIST + " WHERE " + COLUMN_CHAT_ID + "='"
                + chatId + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";

        Cursor listCursor = getDatabaseInstance().rawQuery(chatListQuery, null);
        if (listCursor != null) {
            if (listCursor.moveToNext()) {
                int id = listCursor.getInt(listCursor.getColumnIndex(COLUMN_OID));
                getDatabaseInstance().delete(TABLE_CHAT_LIST, COLUMN_OID + "=" + id, null);
            }
            listCursor.close();
        }
    }

    /**
     * Delete Chat Message based on type(single / group)
     *
     * @param chatId   input value(chatId)
     * @param msgId    input value(msgId)
     * @param chatType input value(chatType)
     */
    public void deleteChatMessage(String chatId, String msgId, String chatType) {
        String query = "SELECT " + COLUMN_OID + " FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_MESSAGE_ID + "='" + msgId
                + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_OID));
                getDatabaseInstance().delete(TABLE_MESSAGES, COLUMN_OID + "=" + id, null);
            }

            String topMsgQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE "
                    + COLUMN_CHAT_ID + "='" + chatId + "' ORDER BY 1 DESC LIMIT 1";
            Cursor topMsgCursor = getDatabaseInstance().rawQuery(topMsgQuery, null);
            if (topMsgCursor.moveToNext()) {
                int id = topMsgCursor.getInt(topMsgCursor.getColumnIndex(COLUMN_OID));
                String msgData = topMsgCursor.getString(topMsgCursor.getColumnIndex(COLUMN_MESSAGE_DATA));
                MessageItemChat msgItem = gson.fromJson(msgData, MessageItemChat.class);
                updateChatList(msgItem.getReceiverID(), msgData, chatType, msgItem.getTS());
            } else {
                if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                    String groupId = chatId.split("-")[1];
                    createNewGroupChatList(groupId, chatId);
                } else {
                    deleteChat(chatId, chatType);
                }
            }
            topMsgCursor.close();

            cursor.close();
        }
    }

    /**
     * clear UnStarred Message
     *
     * @param chatId     input value(chatId)
     * @param receiverId input value(receiverId)
     * @param chatType   input value(chatType)
     */
    public void clearUnStarredMessage(String chatId, String receiverId, String chatType) {
        String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + " LIKE '"
                + chatId + "%' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";

        String lastMsg = null, lastMsgTS = null;

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_OID));
                String msgData = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_DATA));
                MessageItemChat msgItem = gson.fromJson(msgData, MessageItemChat.class);
                if (msgItem.getStarredStatus().equals(MessageFactory.MESSAGE_UN_STARRED)) {
                    getDatabaseInstance().delete(TABLE_MESSAGES, COLUMN_OID + "=" + id, null);
                } else {
                    lastMsg = msgData;
                    lastMsgTS = msgItem.getTS();
                }
            }
            cursor.close();
        }

        if (lastMsg != null && !lastMsg.equals("")) {
            updateChatList(receiverId, lastMsg, chatType, lastMsgTS);
        }

    }

    /**
     * clear all Group ChatMessage
     *
     * @param chatId     input value(chatId)
     * @param receiverId input value(receiverId)
     */
    public void clearAllGroupChatMessage(String chatId, String receiverId) {
        String query = "SELECT " + COLUMN_OID + " FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + " LIKE '"
                + chatId + "%'";

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_OID));
                getDatabaseInstance().delete(TABLE_MESSAGES, COLUMN_OID + "=" + id, null);
            }
            cursor.close();
        }

        updateChatListStatus(receiverId, MessageFactory.CHAT_TYPE_GROUP, MessageFactory.CHAT_STATUS_CLEARED);
    }

    /**
     * clear all Single ChatMessage
     *
     * @param chatId     input value(chatId)
     * @param receiverId input value(receiverId)
     */
    public void clearAllSingleChatMessage(String chatId, String receiverId) {
        String query = "SELECT " + COLUMN_OID + " FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + " LIKE '"
                + chatId + "%'";

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_OID));
                getDatabaseInstance().delete(TABLE_MESSAGES, COLUMN_OID + "=" + id, null);
            }
            cursor.close();
        }

        updateChatListStatus(receiverId, MessageFactory.CHAT_TYPE_SINGLE, MessageFactory.CHAT_STATUS_CLEARED);
    }

    /**
     * update temporary Deleted Message
     *
     * @param recordId      input value(recordId)
     * @param deleteMsgData input value(deleteMsgData)
     */
    public void updateTempDeletedMessage(String recordId, JSONObject deleteMsgData) {
        ContentValues deleteValues = new ContentValues();
        deleteValues.put(COLUMN_RECORD_ID, recordId);
        deleteValues.put(COLUMN_DELETE_MESSAGE_DATA, deleteMsgData.toString());
        deleteValues.put(COLUMN_STATUS, 1);

        getDatabaseInstance().insert(TABLE_TEMP_DELETE_MESSAGES, null, deleteValues);
    }

    /**
     * get all temporary DeletedMessage
     *
     * @param currentUserId input value(currentUserId)
     * @return response value
     */
    public ArrayList<JSONObject> getAllTempDeletedMessage(String currentUserId) {
        String query = "SELECT " + COLUMN_DELETE_MESSAGE_DATA + " FROM " + TABLE_TEMP_DELETE_MESSAGES;

        ArrayList<JSONObject> deletedObjects = new ArrayList<>();
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String msgData = cursor.getString(cursor.getColumnIndex(COLUMN_DELETE_MESSAGE_DATA));
                try {
                    JSONObject data = new JSONObject(msgData);
                    deletedObjects.add(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }

        return deletedObjects;
    }

    /**
     * delete temporary DeletedMessage
     *
     * @param recordId input value(recordId)
     * @param chatType input value(chatType)
     */
    public void deleteTempDeletedMessage(String recordId, String chatType) {
        String query = "SELECT " + COLUMN_OID + " FROM " + TABLE_TEMP_DELETE_MESSAGES + " WHERE "
                + COLUMN_RECORD_ID + "='" + recordId + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_OID));
                getDatabaseInstance().delete(TABLE_TEMP_DELETE_MESSAGES, COLUMN_OID + "=" + id, null);
            }
            cursor.close();
        }

    }

    /**
     * update StarredMessage
     *
     * @param msgId      input value(msgId)
     * @param starStatus input value(starStatus)
     * @param chatType   input value(chatType)
     */
    public void updateStarredMessage(String msgId, String starStatus, String chatType) {
        String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_MESSAGE_ID + "='" + msgId
                + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_OID));
                String msgData = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_DATA));

                MessageItemChat msgItem = gson.fromJson(msgData, MessageItemChat.class);
                msgItem.setStarredStatus(starStatus);

                String modifiedData = gson.toJson(msgItem);
                ContentValues updateValues = new ContentValues();
                updateValues.put(COLUMN_MESSAGE_DATA, modifiedData);

                getDatabaseInstance().update(TABLE_MESSAGES, updateValues, COLUMN_OID + "=" + id, null);
            }
            cursor.close();
        }

    }

    /**
     * update temporary StarredMessage
     *
     * @param recordId    input value(recordId)
     * @param starMsgData input value(starMsgData)
     */
    public void updateTempStarredMessage(String recordId, JSONObject starMsgData) {
        ContentValues starValues = new ContentValues();
        starValues.put(COLUMN_RECORD_ID, recordId);
        starValues.put(COLUMN_STAR_MESSAGE_DATA, starMsgData.toString());
        starValues.put(COLUMN_STATUS, 1);

        getDatabaseInstance().insert(TABLE_TEMP_STAR_MESSAGES, null, starValues);
    }

    /**
     * get all temporary StarredMessage
     *
     * @param currentUserId input value(currentUserId)
     * @return response value
     */
    public ArrayList<JSONObject> getAllTempStarredMessage(String currentUserId) {
        String query = "SELECT " + COLUMN_STAR_MESSAGE_DATA + " FROM " + TABLE_TEMP_STAR_MESSAGES;

        ArrayList<JSONObject> starredObjects = new ArrayList<>();
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                String msgData = cursor.getString(cursor.getColumnIndex(COLUMN_STAR_MESSAGE_DATA));
                try {
                    JSONObject data = new JSONObject(msgData);
                    starredObjects.add(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            cursor.close();
        }

        return starredObjects;
    }

    /**
     * delete temporary StarredMessage
     *
     * @param recordId input value(recordId)
     * @param chatType input value(chatType)
     */
    public void deleteTempStarredMessage(String recordId, String chatType) {
        String query = "SELECT " + COLUMN_OID + " FROM " + TABLE_TEMP_DELETE_MESSAGES + " WHERE "
                + COLUMN_RECORD_ID + "='" + recordId + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";

        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_OID));
                getDatabaseInstance().delete(TABLE_TEMP_DELETE_MESSAGES, COLUMN_OID + "=" + id, null);
            }
            cursor.close();
        }

    }

    /**
     * get Particular Message
     *
     * @param msgId input value(msgId)
     * @return response value
     */
    public MessageItemChat getParticularMessage(String msgId) {
        String query = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_MESSAGE_ID + "='" + msgId + "'";

        MessageItemChat msgItem = null;
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                String msgData = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_DATA));

                msgItem = gson.fromJson(msgData, MessageItemChat.class);
            }
            cursor.close();
        }

        return msgItem;
    }

    /**
     * check isGroupId
     *
     * @param docId input value(docId)
     * @return response value
     */
    public boolean isGroupId(String docId) {
        String query = "SELECT " + COLUMN_OID + " FROM " + TABLE_CHAT_LIST + " WHERE " + COLUMN_CHAT_ID + "='"
                + docId + "' AND " + COLUMN_CHAT_TYPE + "='" + MessageFactory.CHAT_TYPE_GROUP + "'";

        boolean isGroupChat = false;
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                isGroupChat = true;
            }
            cursor.close();
        }

        return isGroupChat;
    }


    // Calls methods

    /**
     * update Call Logs
     *
     * @param callItem getting value from model class
     */
    public void updateCallLogs(CallItemChat callItem) {
        try {
            String selectQuery = "SELECT " + COLUMN_OID + " FROM " + TABLE_CALL_LOGS + " WHERE " +
                    COLUMN_CALL_ID + "='" + callItem.getCallId() + "'";
            Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

            if (selectCur != null) {
                if (selectCur.getCount() > 0) {
                    selectCur.close();
                    updateCallEntry(callItem);
                } else {
                    selectCur.close();
                    insertNewCallEntry(callItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * insert New Call Entry
     *
     * @param callItem getting value from model class
     */
    private void insertNewCallEntry(CallItemChat callItem) {

        try {
            String callData = gson.toJson(callItem);

            ContentValues values = new ContentValues();
            values.put(COLUMN_CALL_ID, callItem.getCallId());
            values.put(COLUMN_CALL_DATA, callData);
            values.put(COLUMN_STATUS, 1);

            // Inserting Row
            getDatabaseInstance().insert(TABLE_CALL_LOGS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * update Call Entry
     *
     * @param callItem getting value from model class
     */
    private void updateCallEntry(CallItemChat callItem) {
        try {
            String jsonMessage = gson.toJson(callItem);
            ContentValues updateValues = new ContentValues();
            updateValues.put(COLUMN_CALL_DATA, jsonMessage);

            getDatabaseInstance().update(TABLE_CALL_LOGS, updateValues, COLUMN_CALL_ID + "='" + callItem.getCallId() + "'", null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * select all Calls
     *
     * @param currentUserId input value(currentUserId)
     * @return response value
     */
    public ArrayList<CallItemChat> selectAllCalls(String currentUserId) {
        String selectQuery = "SELECT " + COLUMN_CALL_DATA + " FROM " + TABLE_CALL_LOGS + " WHERE "
                + COLUMN_CALL_ID + " LIKE '" + currentUserId + "%'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        ArrayList<CallItemChat> callsList = new ArrayList<>();

        if (selectCur != null) {
            while (selectCur.moveToNext()) {
                try {
                    String callData = selectCur.getString(selectCur.getColumnIndex(COLUMN_CALL_DATA));
                    CallItemChat callItemChat = gson.fromJson(callData, CallItemChat.class);
                    if (callItemChat != null) {
                        callsList.add(callItemChat);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "selectAllCalls: ", e);
                }
            }
            selectCur.close();
        }

        return callsList;
    }

    /**
     * update Call Status
     *
     * @param callId     input value(callId)
     * @param callStatus input value(callStatus)
     * @param duration   input value(duration)
     */
    public void updateCallStatus(String callId, int callStatus, String duration) {

        try {
            String selectQuery = "SELECT * FROM " + TABLE_CALL_LOGS + " WHERE " +
                    COLUMN_CALL_ID + "='" + callId + "'";
            Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

            if (selectCur != null) {
                if (selectCur.moveToNext()) {

                    String callData = selectCur.getString(selectCur.getColumnIndex(COLUMN_CALL_DATA));
                    CallItemChat callItem = gson.fromJson(callData, CallItemChat.class);
                    if (!callItem.getCallStatus().equals(MessageFactory.CALL_STATUS_MISSED + "")
                            && !callItem.getCallStatus().equals(MessageFactory.CALL_STATUS_REJECTED + "")) {
                        callItem.setCallStatus(callStatus + "");
                        callItem.setCallDuration(duration);
                        updateCallLogs(callItem);
                    }
                }
                selectCur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * get Call Status
     *
     * @param callId input value(callId)
     * @return response value
     */
    public CallItemChat getCallStatus(String callId) {
        String selectQuery = "SELECT * FROM " + TABLE_CALL_LOGS + " WHERE " + COLUMN_CALL_ID
                + "='" + callId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        CallItemChat callItem = null;

        if (selectCur != null) {
            if (selectCur.moveToNext()) {
                String callData = selectCur.getString(selectCur.getColumnIndex(COLUMN_CALL_DATA));
                callItem = gson.fromJson(callData, CallItemChat.class);
            }
            selectCur.close();
        }
        return callItem;
    }

    /**
     * delete Call Log
     *
     * @param callId input value(callId)
     */
    public void deleteCallLog(String callId) {
        getDatabaseInstance().delete(TABLE_CALL_LOGS, COLUMN_CALL_ID + " = ?", new String[]{callId});
    }

    /**
     * delete all Call Logs
     */
    public void deleteAllCallLogs() {
        getDatabaseInstance().execSQL("DELETE FROM " + TABLE_CALL_LOGS);
    }

    /**
     * delete Database
     */
    public void deleteDatabase() {
        close();
        mContext.deleteDatabase(DB_NAME);
    }

    /**
     * getting MessageItem
     *
     * @param docId           input value(docId)
     * @param msgId           input value(msgId)
     * @param status          input value(status)
     * @param deliverOrReadTS input value(deliverOrReadTS)
     * @param isSelfMsg       input value(isSelfMsg)
     * @return response value
     */
    public ArrayList<MessageItemChat> getMessageItem(String docId, String msgId, String status,
                                                     String deliverOrReadTS, boolean isSelfMsg) {

        ArrayList<MessageItemChat> msgItemArray = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_CHAT_ID + "='" + docId + "'";


//        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " +
//                COLUMN_MESSAGE_ID + "='" + msgId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        MessageItemChat msgItem = null;

        if (selectCur != null) {

            while (selectCur.moveToNext()) {

                MessageItemChat itemChat = gson.fromJson(selectCur.getString(selectCur.getColumnIndex(COLUMN_MESSAGE_DATA)), MessageItemChat.class);


                try {
                    if (itemChat.getReadTime().equals(deliverOrReadTS)) {
                        msgItemArray.add(itemChat);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            selectCur.close();
        }

        return msgItemArray;
    }

    /**
     * update ChatMessage
     *
     * @param docId           input value(docId)
     * @param msgId           input value(msgId)
     * @param status          input value(status)
     * @param deliverOrReadTS input value(deliverOrReadTS)
     * @param isSelfMsg       input value(isSelfMsg)
     * @return response value
     */
    public MessageItemChat updateChatMessage(String docId, String msgId, String status,
                                             String deliverOrReadTS, boolean isSelfMsg) {
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " +
                COLUMN_MESSAGE_ID + "='" + msgId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        MessageItemChat msgItem = null;

        if (selectCur != null) {

            if (selectCur.moveToNext()) {

                long timeStamp = selectCur.getLong(selectCur.getColumnIndex(COLUMN_TIME_STAMP));
                String msgData = selectCur.getString(selectCur.getColumnIndex(COLUMN_MESSAGE_DATA));
                String newMsgChatType = selectCur.getString(selectCur.getColumnIndex(COLUMN_CHAT_TYPE));
                msgItem = gson.fromJson(msgData, MessageItemChat.class);

                if (!msgItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_READ)
                        && status.equals(MessageFactory.DELIVERY_STATUS_DELIVERED)) {
                    msgItem.setDeliveryTime(deliverOrReadTS);
                    msgItem.setDeliveryStatus(status);
                    updateChatMessage(msgItem, newMsgChatType);
                }

                if (status.equals(MessageFactory.DELIVERY_STATUS_READ)) {
                    msgItem.setReadTime(deliverOrReadTS);
                    msgItem.setDeliveryStatus(status);
                    if (msgItem.getDeliveryTime() == null || msgItem.getDeliveryTime().equals("0")) {
                        msgItem.setDeliveryTime(deliverOrReadTS);
                    }

                    if (msgItem.isSelf()) {
                        msgItem.setSecretMsgReadAt(deliverOrReadTS);
                    }
                    updateChatMessage(msgItem, newMsgChatType);
                }

                String msgQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_TIME_STAMP
                        + "<" + timeStamp + " AND " + COLUMN_DELIVERY_STATUS + "<'" + status + "'"
                        + " AND " + COLUMN_CHAT_ID + "='" + docId + "'";
                Cursor msgCur = getDatabaseInstance().rawQuery(msgQuery, null);

                while (msgCur.moveToNext()) {
                    String uniqueChatMessageJson = msgCur.getString(msgCur.getColumnIndex(COLUMN_MESSAGE_DATA));
                    String chatType = msgCur.getString(msgCur.getColumnIndex(COLUMN_CHAT_TYPE));

                    if (status.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_DELIVERED)) {
                        MessageItemChat oldMsgItem = gson.fromJson(uniqueChatMessageJson, MessageItemChat.class);
                        if ((isSelfMsg == oldMsgItem.isSelf()) && oldMsgItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)) {
                            oldMsgItem.setDeliveryTime(deliverOrReadTS);
                            oldMsgItem.setDeliveryStatus(status);
                            updateChatMessage(oldMsgItem, chatType);
                        }
                    } else if (status.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
                        MessageItemChat oldMsgItem = gson.fromJson(uniqueChatMessageJson, MessageItemChat.class);

                        if (isSelfMsg && oldMsgItem.isSelf()) {
                            if (oldMsgItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)
                                    || oldMsgItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_DELIVERED)) {

                                // From sender message
                                if (oldMsgItem.getDeliveryTime() == null || oldMsgItem.getDeliveryTime().equals("0")) {
                                    oldMsgItem.setDeliveryTime(deliverOrReadTS);
                                }

                                oldMsgItem.setSecretMsgReadAt(deliverOrReadTS);
                                oldMsgItem.setReadTime(deliverOrReadTS);
                                oldMsgItem.setDeliveryStatus(status);
                                updateChatMessage(oldMsgItem, chatType);
                            }
                        } else if (!isSelfMsg && !oldMsgItem.isSelf()) {
                            // From receiver message
                            if (oldMsgItem.getDeliveryTime() == null || oldMsgItem.getDeliveryTime().equals("0")) {
                                oldMsgItem.setDeliveryTime(deliverOrReadTS);
                            }
                            oldMsgItem.setSecretMsgReadAt(deliverOrReadTS);
                            oldMsgItem.setReadTime(deliverOrReadTS);
                            oldMsgItem.setDeliveryStatus(status);
                            updateChatMessage(oldMsgItem, chatType);
                        }
                    }
                }
                msgCur.close();
            }
            selectCur.close();
        }

        return msgItem;
    }

    /**
     * update ChatMessage with id's
     *
     * @param docId    input value(docId)
     * @param msgId    input value(msgId)
     * @param status   input value(status)
     * @param recordId input value(recordId)
     * @param convId   input value(convId)
     * @param sentTS   input value(sentTS)
     * @return response value
     */
    public MessageItemChat updateChatMessage(String docId, String msgId, String status, String recordId,
                                             String convId, String sentTS) {
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " +
                COLUMN_MESSAGE_ID + "='" + msgId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        MessageItemChat msgItem = null;

        if (selectCur != null) {

            if (selectCur.moveToNext()) {

                long timeStamp = selectCur.getLong(selectCur.getColumnIndex(COLUMN_TIME_STAMP));
                String msgData = selectCur.getString(selectCur.getColumnIndex(COLUMN_MESSAGE_DATA));
                String newMsgChatType = selectCur.getString(selectCur.getColumnIndex(COLUMN_CHAT_TYPE));
                msgItem = gson.fromJson(msgData, MessageItemChat.class);

                if (status.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_DELIVERED)) {
                    msgItem.setDeliveryTime(sentTS);
                } else if (status.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
                    msgItem.setReadTime(sentTS);
                } else if (status.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_SENT)) {
                    if (msgItem.getTS() != null && !msgItem.getTS().equals("")) {
                        sentTS = msgItem.getTS();
                    }
                    msgItem.setMsgSentAt(sentTS);
                }
                msgItem.setDeliveryStatus(status);
                if (recordId != null && !recordId.equals("")) {
                    msgItem.setRecordId(recordId);
                }
                if (convId != null && !convId.equals("")) {
                    msgItem.setConvId(convId);
                }
                msgItem.setUploadStatus(MessageFactory.UPLOAD_STATUS_COMPLETED);

                String msgQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_TIME_STAMP
                        + "<" + timeStamp + " AND " + COLUMN_DELIVERY_STATUS + "<'" + status + "'"
                        + " AND " + COLUMN_CHAT_ID + "='" + docId + "'";
                Cursor msgCur = getDatabaseInstance().rawQuery(msgQuery, null);

                updateChatMessage(msgItem, newMsgChatType);

                while (msgCur.moveToNext()) {
                    String uniqueChatMessageJson = msgCur.getString(msgCur.getColumnIndex(COLUMN_MESSAGE_DATA));
                    String chatType = msgCur.getString(msgCur.getColumnIndex(COLUMN_CHAT_TYPE));

                    if (status.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_DELIVERED)) {

                        MessageItemChat oldMsgItem = gson.fromJson(uniqueChatMessageJson, MessageItemChat.class);
                        if (oldMsgItem.isSelf() && oldMsgItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)) {
                            oldMsgItem.setDeliveryStatus(status);
                            oldMsgItem.setDeliveryTime(sentTS);
                            oldMsgItem.setUploadStatus(MessageFactory.UPLOAD_STATUS_COMPLETED);
                            updateChatMessage(oldMsgItem, chatType);
                        }
                    } else if (status.equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {

                        MessageItemChat oldMsgItem = gson.fromJson(uniqueChatMessageJson, MessageItemChat.class);
                        if (oldMsgItem.isSelf()
                                && (oldMsgItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)
                                || oldMsgItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_DELIVERED))) {
                            oldMsgItem.setDeliveryStatus(status);
                            oldMsgItem.setReadTime(sentTS);
                            oldMsgItem.setUploadStatus(MessageFactory.UPLOAD_STATUS_COMPLETED);
                            updateChatMessage(oldMsgItem, chatType);
                        }
                    }
                }
                msgCur.close();
            }
            selectCur.close();
        }

        return msgItem;
    }

    /**
     * update Message Download Status
     *
     * @param docId          input value(docId)
     * @param msgId          input value(msgId)
     * @param downloadStatus input value(downloadStatus)
     * @return response value
     */
    public MessageItemChat updateMessageDownloadStatus(String docId, String msgId, int downloadStatus) {
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " +
                COLUMN_MESSAGE_ID + "='" + msgId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        MessageItemChat msgItem = null;

        if (selectCur != null) {
            if (selectCur.moveToNext()) {
                long timeStamp = selectCur.getLong(selectCur.getColumnIndex(COLUMN_TIME_STAMP));
                String msgData = selectCur.getString(selectCur.getColumnIndex(COLUMN_MESSAGE_DATA));
                String chatType = selectCur.getString(selectCur.getColumnIndex(COLUMN_CHAT_TYPE));
                msgItem = gson.fromJson(msgData, MessageItemChat.class);
                msgItem.setDownloadStatus(downloadStatus);
                msgItem.setUploadDownloadProgress(100);
                updateChatMessage(msgItem, chatType);
            }
            selectCur.close();
        }

        return msgItem;
    }

    /**
     * get Search Messages
     *
     * @param constraint input value(constraint)
     * @return response value
     */
    public List<MessageItemChat> getSearchMessages(CharSequence constraint) {
        List<MessageItemChat> textMessages = new ArrayList<>();
        try {

            String selectQuery = "SELECT * FROM " + TABLE_MESSAGES;
            Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

            MessageItemChat msgItem = null;

            if (selectCur != null) {

                while (selectCur.moveToNext()) {

                    String msgData = selectCur.getString(selectCur.getColumnIndex(COLUMN_MESSAGE_DATA));
                    msgItem = gson.fromJson(msgData, MessageItemChat.class);
                    if (msgItem != null) {
                        if (msgItem.getMessageType().equals(String.valueOf(MessageFactory.text))) {
                            String message = msgItem.getTextMessage().toLowerCase();
                            constraint = constraint.toString().toLowerCase();

                            if (message.contains(constraint)) {
                                msgItem.setFilteredMessage(true);
                                textMessages.add(msgItem);
                            }
                        }
                    }


                }
                selectCur.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return textMessages;
    }

    /**
     * update Group Message Status
     *
     * @param docId           input value(docId)
     * @param msgId           input value(msgId)
     * @param status          input value(status)
     * @param deliverOrReadTS input value(deliverOrReadTS)
     * @param ackUserId       input value(ackUserId)
     * @param currentUserId   input value(currentUserId)
     * @return response value
     */
    public MessageItemChat updateGroupMessageStatus(String docId, String msgId, String status, String deliverOrReadTS,
                                                    String ackUserId, String currentUserId) {
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " +
                COLUMN_MESSAGE_ID + "='" + msgId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        MessageItemChat msgItem = null;

        if (selectCur != null) {

            if (selectCur.moveToNext()) {

                long timeStamp = selectCur.getLong(selectCur.getColumnIndex(COLUMN_TIME_STAMP));
                String msgData = selectCur.getString(selectCur.getColumnIndex(COLUMN_MESSAGE_DATA));
                String newMsgChatType = selectCur.getString(selectCur.getColumnIndex(COLUMN_CHAT_TYPE));
                msgItem = gson.fromJson(msgData, MessageItemChat.class);

                msgItem.setUploadStatus(MessageFactory.UPLOAD_STATUS_COMPLETED);
                if (status.equalsIgnoreCase(MessageFactory.GROUP_MSG_DELIVER_ACK)) {
                    if (msgItem.isSelf() && msgItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)) {
                        updateGroupMsgDeliverStatus(docId, ackUserId, msgItem, deliverOrReadTS);
                    } else if (!msgItem.isSelf() && ackUserId.equals(currentUserId)) {
                        msgItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);
                        updateChatMessage(msgItem, MessageFactory.CHAT_TYPE_GROUP);
                    }
                } else if (status.equalsIgnoreCase(MessageFactory.GROUP_MSG_READ_ACK)) {
                    if (msgItem.isSelf() && !msgItem.getDeliveryStatus().equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
                        updateGroupMsgReadStatus(docId, ackUserId, msgItem, deliverOrReadTS);
                    } else if (!msgItem.isSelf() && ackUserId.equals(currentUserId)) {
                        msgItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_READ);
                        updateChatMessage(msgItem, MessageFactory.CHAT_TYPE_GROUP);
                    }
                }

                String msgQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " + COLUMN_TIME_STAMP
                        + "<" + timeStamp + " AND " + COLUMN_DELIVERY_STATUS + "<'" + status + "'"
                        + " AND " + COLUMN_CHAT_ID + "='" + docId + "'";
                Cursor msgCur = getDatabaseInstance().rawQuery(msgQuery, null);

                updateChatMessage(msgItem, newMsgChatType);

                while (msgCur.moveToNext()) {
                    String uniqueChatMessageJson = msgCur.getString(msgCur.getColumnIndex(COLUMN_MESSAGE_DATA));

                    if (status.equalsIgnoreCase(MessageFactory.GROUP_MSG_DELIVER_ACK)) {
                        MessageItemChat oldMsgItem = gson.fromJson(uniqueChatMessageJson, MessageItemChat.class);
                        if (oldMsgItem.isSelf() && oldMsgItem.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)) {
                            updateGroupMsgDeliverStatus(docId, ackUserId, oldMsgItem, deliverOrReadTS);
                        } else if (!oldMsgItem.isSelf() && ackUserId.equals(currentUserId)) {
                            oldMsgItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);
                            updateChatMessage(oldMsgItem, MessageFactory.CHAT_TYPE_GROUP);
                        }
                    } else if (status.equalsIgnoreCase(MessageFactory.GROUP_MSG_READ_ACK)) {
                        MessageItemChat oldMsgItem = gson.fromJson(uniqueChatMessageJson, MessageItemChat.class);
                        if (oldMsgItem.isSelf() && !oldMsgItem.getDeliveryStatus().equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
                            updateGroupMsgReadStatus(docId, ackUserId, oldMsgItem, deliverOrReadTS);
                        } else if (!oldMsgItem.isSelf() && ackUserId.equals(currentUserId)) {
                            oldMsgItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_READ);
                            updateChatMessage(oldMsgItem, MessageFactory.CHAT_TYPE_GROUP);
                        }
                    }
                }
                msgCur.close();
            }

            selectCur.close();
        }

        return msgItem;
    }

    // update group message deliver status

    /**
     * update Group Message Deliver Status
     *
     * @param docId           input value(docId)
     * @param ackUserId       input value(ackUserId)
     * @param msgItem         input value(msgItem)
     * @param deliverOrReadTS input value(deliverOrReadTS)
     */
    private void updateGroupMsgDeliverStatus(String docId, String ackUserId, MessageItemChat msgItem,
                                             String deliverOrReadTS) {

        String msgStatus = msgItem.getGroupMsgDeliverStatus();
        try {
            boolean isDeliveredAll = true;

            JSONObject msgDeliverObj = new JSONObject(msgStatus);
            JSONArray arrMembers = msgDeliverObj.getJSONArray("GroupMessageStatus");
            for (int i = 0; i < arrMembers.length(); i++) {
                JSONObject userObj = arrMembers.getJSONObject(i);
                String userId = userObj.getString("UserId");
                String deliverStatus = userObj.getString("DeliverStatus");

                if (userId.equals(ackUserId) && deliverStatus.equals(MessageFactory.DELIVERY_STATUS_SENT) &&
                        !deliverStatus.equals(MessageFactory.DELIVERY_STATUS_READ)) {
                    userObj.put("DeliverStatus", MessageFactory.DELIVERY_STATUS_DELIVERED);
                    userObj.put("DeliverTime", deliverOrReadTS);
                    arrMembers.put(i, userObj);
                    msgDeliverObj.put("GroupMessageStatus", arrMembers);
                    msgItem.setGroupMsgDeliverStatus(msgDeliverObj.toString());
                }

                deliverStatus = userObj.getString("DeliverStatus");
                int deliver = Integer.parseInt(deliverStatus);
                if (deliver < 2) {
                    isDeliveredAll = false;
                }
            }

            if (isDeliveredAll) {
                msgItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_DELIVERED);
            }
            updateChatMessage(msgItem, MessageFactory.CHAT_TYPE_GROUP);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // update group message read status

    /**
     * update Group Message Read Status
     *
     * @param docId           input value(docId)
     * @param ackUserId       input value(ackUserId)
     * @param msgItem         input value(msgItem)
     * @param deliverOrReadTS input value(deliverOrReadTS)
     */
    private void updateGroupMsgReadStatus(String docId, String ackUserId, MessageItemChat msgItem,
                                          String deliverOrReadTS) {

        String msgStatus = msgItem.getGroupMsgDeliverStatus();
        try {
            JSONObject msgDeliverObj = new JSONObject(msgStatus);
            JSONArray arrMembers = msgDeliverObj.getJSONArray("GroupMessageStatus");

            boolean isReadAll = true;
            if (arrMembers != null && arrMembers.length() > 0) {
                for (int i = 0; i < arrMembers.length(); i++) {
                    JSONObject userObj = arrMembers.getJSONObject(i);
                    String userId = userObj.getString("UserId");
//                if(userId.equals(ackUserId) && msgItem.getCallStatus().equals(MessageFactory.DELIVERY_STATUS_SENT)) {
                    if (userId.equals(ackUserId)) {
                        userObj.put("DeliverStatus", MessageFactory.DELIVERY_STATUS_READ);
                        // for updating deliver time if not exists
                        if (userObj.getString("DeliverTime").equals("")) {
                            userObj.put("DeliverTime", deliverOrReadTS);
                        }
                        userObj.put("ReadTime", deliverOrReadTS);
                        arrMembers.put(i, userObj);
                        msgDeliverObj.put("GroupMessageStatus", arrMembers);
                        msgItem.setGroupMsgDeliverStatus(msgDeliverObj.toString());
                    }

                    String deliverStatus = userObj.getString("DeliverStatus");
                    if (!deliverStatus.equals(MessageFactory.DELIVERY_STATUS_READ)) {
                        isReadAll = false;
                    }
                }
            }
            if (isReadAll) {
                msgItem.setDeliveryStatus(MessageFactory.DELIVERY_STATUS_READ);
            }
            updateChatMessage(msgItem, MessageFactory.CHAT_TYPE_GROUP);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * create New Group ChatList
     *
     * @param groupId    input value(groupId)
     * @param groupDocId input value(groupDocId)
     */
    public void createNewGroupChatList(String groupId, String groupDocId) {
        try {
            MessageItemChat data = new MessageItemChat();
            data.setMessageType("");
            data.setMessageId(groupDocId.concat("-0"));
            data.setTextMessage("");
            data.setReceiverID(groupId);
            data.setTS("0");

            String msgData = gson.toJson(data);
            updateChatList(groupId, msgData, MessageFactory.CHAT_TYPE_GROUP, "0");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * select all Secret ChatMessage
     *
     * @param docId          input value(docId)
     * @param serverTimeDiff input value(serverTimeDiff)
     * @return response value
     */
    public ArrayList<MessageItemChat> selectAllSecretChatMessage(String docId, long serverTimeDiff) {
        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " +
                COLUMN_CHAT_ID + "='" + docId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        long currentTime = Calendar.getInstance().getTimeInMillis() - serverTimeDiff;
        ArrayList<MessageItemChat> messageItemChats = new ArrayList<>();
        ArrayList<MessageItemChat> removeMsgItems = new ArrayList<>();

        while (selectCur.moveToNext()) {
            try {
                String msgData = selectCur.getString(selectCur.getColumnIndex(COLUMN_MESSAGE_DATA));
                MessageItemChat msgItem = gson.fromJson(msgData, MessageItemChat.class);
                if (!msgItem.isSelf() && msgItem.getDeliveryStatus().equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_READ)) {
                    String strReadAt = msgItem.getSecretMsgReadAt();
                    String strTimer = msgItem.getSecretTimer();

                    long readAt = Long.parseLong(strReadAt);
                    long timer = Long.parseLong(strTimer);
                    long timeDiff = currentTime - readAt;

                    if (timeDiff >= timer) {
                        removeMsgItems.add(msgItem);
                    }
                } else if (msgItem.isSelf() && !msgItem.getDeliveryStatus().equalsIgnoreCase(MessageFactory.DELIVERY_STATUS_NOT_SENT)) {
                    String strSentAt = msgItem.getMsgSentAt();
                    String strTimer = msgItem.getSecretTimer();

                    long sentAt = Long.parseLong(strSentAt);
                    long timer = Long.parseLong(strTimer);
                    long timeDiff = currentTime - sentAt;

                    if (timeDiff >= timer) {
                        removeMsgItems.add(msgItem);
                    }
                }

                if (msgItem.isDate()) {
                    removeMsgItems.add(msgItem);
                }

                messageItemChats.add(msgItem);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        selectCur.close();
        for (MessageItemChat removeItem : removeMsgItems) {
            deleteChatMessage(docId, removeItem.getMessageId(), MessageFactory.CHAT_TYPE_SECRET);
            messageItemChats.remove(removeItem);
        }

        return messageItemChats;
    }

    public void updateSecretMessageReadAt(String docId, String msgId, long readAt) {

        String selectQuery = "SELECT * FROM " + TABLE_MESSAGES + " WHERE " +
                COLUMN_MESSAGE_ID + "='" + msgId + "'";
        Cursor selectCur = getDatabaseInstance().rawQuery(selectQuery, null);

        if (selectCur != null) {
            if (selectCur.moveToNext()) {
                int id = selectCur.getInt(selectCur.getColumnIndex(COLUMN_OID));
                String msgData = selectCur.getString(selectCur.getColumnIndex(COLUMN_MESSAGE_DATA));

                MessageItemChat msgItem = gson.fromJson(msgData, MessageItemChat.class);
                if (msgItem != null) {
                    msgItem.setSecretMsgReadAt(readAt + "");
                    updateChatMessage(msgItem, MessageFactory.CHAT_TYPE_SECRET);
                }
            }
            selectCur.close();
        }
    }


    //-----------Delete Chat----------------------

    /**
     * delete SingleMessage
     *
     * @param groupAndMsgId input value(groupAndMsgId)
     * @param msgId         input value(msgId)
     * @param chatType      input value(chatType)
     * @param msgType       input value(msgType)
     */
    public void deleteSingleMessage(String groupAndMsgId, String msgId, String chatType, String msgType) {
        String query = "";
        //group msg
        if (msgId.contains("-g")) {
            query = "SELECT " + COLUMN_MESSAGE_DATA + "," + COLUMN_OID + " FROM " + TABLE_MESSAGES + " WHERE " +
                    COLUMN_MESSAGE_ID + " LIKE '" + "%" + groupAndMsgId + "%' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";
            ;
        }
        //single msg
        else {
            query = "SELECT " + COLUMN_MESSAGE_DATA + "," + COLUMN_OID + " FROM " + TABLE_MESSAGES + " WHERE " +
                    COLUMN_MESSAGE_ID + "='" + msgId + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";
            ;
        }
        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            Log.d(TAG, "deleteSingleMessage: 1");
            if (cursor.moveToFirst()) {
                Log.d(TAG, "deleteSingleMessage: 2");
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_OID));
                String chatData = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_DATA));

                MessageItemChat msgItem = gson.fromJson(chatData, MessageItemChat.class);
                switch (msgType) {
                    case "self":
                        msgItem.setMessageType(MessageFactory.DELETE_SELF + "");
                        msgItem.setIsSelf(true);
                        break;

                    case "other":
                        msgItem.setMessageType(MessageFactory.DELETE_OTHER + "");
                        msgItem.setIsSelf(false);
                        break;
                }

                ContentValues updateValues = new ContentValues();
                updateValues.put(COLUMN_MESSAGE_DATA, gson.toJson(msgItem));
                getDatabaseInstance().update(TABLE_MESSAGES, updateValues, COLUMN_OID + "=" + id, null);
                cursor.close();
            } else {
                cursor.close();
            }
        }
    }

    /**
     * delete Chat ListPage
     *
     * @param groupAndMsgId input value(groupAndMsgId)
     * @param msgId         input value(msgId)
     * @param chatType      input value(chatType)
     * @param msgType       input value(msgType)
     */
    public void deleteChatListPage(String groupAndMsgId, String msgId, String chatType, String msgType) {
        mCurrentUserId = SessionManager.getInstance(mContext).getCurrentUserID();
        String[] split = msgId.split("-");
        String query = "";
        //group msg

        query = "SELECT " + COLUMN_MESSAGE_DATA + "," + COLUMN_OID + " FROM " + TABLE_CHAT_LIST + " WHERE " + COLUMN_CHAT_ID + " LIKE '" + mCurrentUserId
                + "%' AND " + COLUMN_RECEIVER_ID + "='" + split[1] + "' AND " + COLUMN_CHAT_TYPE + "='" + chatType + "'";


        Cursor cursor = getDatabaseInstance().rawQuery(query, null);
        if (cursor != null) {
            Log.d(TAG, "deleteSingleMessage: 1");
            if (cursor.moveToFirst()) {
                Log.d(TAG, "deleteSingleMessage: 2");
                int id = cursor.getInt(cursor.getColumnIndex(COLUMN_OID));
                String chatData = cursor.getString(cursor.getColumnIndex(COLUMN_MESSAGE_DATA));

                MessageItemChat msgItem = gson.fromJson(chatData, MessageItemChat.class);
                switch (msgType) {
                    case "self":
//                        msgItem.setMessageType("Delete Self");
                        msgItem.setMessageType(MessageFactory.DELETE_SELF + "");
                        msgItem.setIsSelf(true);
                        break;

                    case "other":
//                        msgItem.setMessageType("Delete Others");
                        msgItem.setMessageType(MessageFactory.DELETE_OTHER + "");
                        msgItem.setIsSelf(false);
                        break;
                }

                ContentValues updateValues = new ContentValues();
                updateValues.put(COLUMN_MESSAGE_DATA, gson.toJson(msgItem));
                getDatabaseInstance().update(TABLE_CHAT_LIST, updateValues, COLUMN_OID + "=" + id, null);
                cursor.close();
            } else {
                cursor.close();
            }
        }
    }


//    public void insert_BroadcastList(String list) {
//
//        ContentValues values = new ContentValues();
//        values.put(BROADCAST_LIST, list);
//        getDatabaseInstance().insert(TABLE_BROADCAST, null, values);
//
//    }
//
//
//    public ArrayList<String> getAllBroadcast() {
//
//        ArrayList<String> list = new ArrayList<String>();
//
//        // Select All Query
//        String selectQuery = "SELECT  * FROM " + TABLE_BROADCAST;
//
//        SQLiteDatabase db = this.getReadableDatabase();
//        try {
//
//            Cursor cursor = db.rawQuery(selectQuery, null);
//            try {
//
//                // looping through all rows and adding to list
//                if (cursor.moveToFirst()) {
//                    do {
//                        list.add(cursor.getString(cursor.getColumnIndex(BROADCAST_LIST)));
//                    } while (cursor.moveToNext());
//                }
//
//            } finally {
//                try { cursor.close(); } catch (Exception ignore) {}
//            }
//
//        } finally {
//            try { db.close(); } catch (Exception ignore) {}
//        }
//
//        return list;
//    }


}
