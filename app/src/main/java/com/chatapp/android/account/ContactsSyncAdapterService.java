/*******************************************************************************
 * Copyright 2010 Sam Steele
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.chatapp.android.account;

import android.accounts.Account;
import android.accounts.OperationCanceledException;
import android.app.Service;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.ContactsContract.RawContacts;
import android.util.Log;

import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.model.ChatappContactModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author sam
 */
public class ContactsSyncAdapterService extends Service {
    private static final String TAG = "ContactsSyncAdapterService";
    private static SyncAdapterImpl sSyncAdapter = null;
    private static ContentResolver mContentResolver = null;
    private static final String COLUMN_USER_ID = RawContacts.SYNC1;
    private static final String COLUMN_USER_NAME = RawContacts.SYNC2;

    public static final long MIN_TIME_ACCOUNT_CREATE = 20 * 60 * 1000; // 20 minutes

    public ContactsSyncAdapterService() {
        super();
    }

    private class SyncAdapterImpl extends AbstractThreadedSyncAdapter {
        private Context mContext;

        public SyncAdapterImpl(Context context) {
            super(context, true);
            mContext = context;
        }

        @Override
        public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
            try {
                performSync(mContext, account, extras, authority, provider, syncResult);
            } catch (OperationCanceledException e) {
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        IBinder ret = null;
        ret = getSyncAdapter().getSyncAdapterBinder();
        return ret;
    }

    private SyncAdapterImpl getSyncAdapter() {
        if (sSyncAdapter == null)
            sSyncAdapter = new SyncAdapterImpl(this);
        return sSyncAdapter;
    }

    public static void addContact(Account account, String userId, String name, String msisdn) {
        ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();

        ContentProviderOperation.Builder builder = ContentProviderOperation.newInsert(RawContacts.CONTENT_URI);
        builder.withValue(RawContacts.ACCOUNT_NAME, account.name);
        builder.withValue(RawContacts.ACCOUNT_TYPE, account.type);
        builder.withValue(RawContacts.SYNC1, userId);
        operationList.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.CommonDataKinds.StructuredName.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        builder.withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name);
        operationList.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.com.chatapp.android.voice_call");
        builder.withValue(ContactsContract.Data.DATA4, userId);
        builder.withValue(ContactsContract.Data.DATA5, msisdn);
        builder.withValue(ContactsContract.Data.DATA6, "Voice call " + msisdn);
        operationList.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.com.chatapp.android.video_call");
        builder.withValue(ContactsContract.Data.DATA7, userId);
        builder.withValue(ContactsContract.Data.DATA8, msisdn);
        builder.withValue(ContactsContract.Data.DATA9, "Video call " + msisdn);
        operationList.add(builder.build());

        builder = ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI);
        builder.withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0);
        builder.withValue(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/vnd.com.chatapp.android.message");
        builder.withValue(ContactsContract.Data.DATA1, userId);
        builder.withValue(ContactsContract.Data.DATA2, msisdn);
        builder.withValue(ContactsContract.Data.DATA3, "Message to " + msisdn);
        operationList.add(builder.build());

        try {
            mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static class SyncEntry {
        public Long raw_id = 0L;
        public Long userId = null;
    }

    public void performSync(Context context, Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult)
            throws OperationCanceledException {
        HashMap<String, SyncEntry> localContacts = new HashMap<String, SyncEntry>();
        mContentResolver = context.getContentResolver();

        // Load the local contacts
        Uri rawContactUri = RawContacts.CONTENT_URI.buildUpon().appendQueryParameter(RawContacts.ACCOUNT_NAME, account.name).appendQueryParameter(
                RawContacts.ACCOUNT_TYPE, account.type).build();
        Cursor c1 = mContentResolver.query(rawContactUri, new String[]{BaseColumns._ID, COLUMN_USER_ID}, null, null, null);

        ArrayList<ContentProviderOperation> deleteOps = new ArrayList<ContentProviderOperation>();

        if (c1 != null) {
            while (c1.moveToNext()) {
                SyncEntry entry = new SyncEntry();
                entry.raw_id = c1.getLong(c1.getColumnIndex(BaseColumns._ID));
                entry.userId = c1.getLong(c1.getColumnIndex(COLUMN_USER_ID));
                localContacts.put(c1.getString(1), entry);
            }
            c1.close();
        }

        /*try {
            String[] args = new String[] {c1.getString(c1.getColumnIndex(BaseColumns._ID))};
            deleteOps.add(ContentProviderOperation.newDelete(RawContacts.CONTENT_URI).withSelection(RawContacts.CONTACT_ID + "=?", args).build());
            mContentResolver.applyBatch(ContactsContract.AUTHORITY, deleteOps);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }*/


        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        ArrayList<ChatappContactModel> chatappEntries = contactDB_sqlite.getSavedChatappContacts();


        for (ChatappContactModel contactModel : chatappEntries) {
            ArrayList<ContentProviderOperation> operationList = new ArrayList<ContentProviderOperation>();
            try {
                // If we don't have any contacts, create one. Otherwise, set a
                // status message
                String userId = contactModel.get_id();
                String name = contactModel.getFirstName();
                String number = contactModel.getMsisdn();
                if (localContacts.get(userId) == null) {
                    addContact(account, userId, name, number);
                } else {

                }
                if (operationList.size() > 0) {
                    mContentResolver.applyBatch(ContactsContract.AUTHORITY, operationList);
                }
            } catch (Exception e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }

        sendCompletedBroadcast(context);
    }

    private static long getContactID(ContentResolver contactHelper, String number) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        String[] projection = {ContactsContract.PhoneLookup._ID};
        Cursor cursor = null;

        try {
            cursor = contactHelper.query(contactUri, projection, null, null, null);
            if (cursor.moveToFirst()) {
                int personID = cursor.getColumnIndex(ContactsContract.PhoneLookup._ID);
                return cursor.getLong(personID);
            }
            return -1;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
                cursor = null;
            }
        }
        return -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sendCompletedBroadcast(this);
    }

    public static void sendCompletedBroadcast(Context context) {
        Intent intent = new Intent();
        intent.setAction(context.getPackageName() + ".account_complete");
        context.sendBroadcast(intent);
        Log.d("Myaccountcompleted", "called");
    }
}
