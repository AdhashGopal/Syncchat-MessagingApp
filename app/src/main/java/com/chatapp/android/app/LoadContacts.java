package com.chatapp.android.app;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.provider.ContactsContract;

import com.chatapp.android.core.model.ChatappContactModel;

import java.util.ArrayList;

/**
 * Created by Administrator on 10/21/2016.
 */
public class LoadContacts extends AsyncTask<Void, Void, Void> {
    Context context;
    static ArrayList<ChatappContactModel> data;

    interface ContactsCallBack {
        void loadContact(ArrayList<ChatappContactModel> data);
    }

    ContactsCallBack contactsCallBack;
    private ProgressDialog dialog;

    public void setContactsCallBack(ContactsCallBack contactsCallBack) {
        this.contactsCallBack = contactsCallBack;
    }

    public LoadContacts(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        if (data != null && contactsCallBack != null)
            contactsCallBack.loadContact(data);
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        if (contactsCallBack != null)
            contactsCallBack.loadContact(data);
        super.onPostExecute(aVoid);
    }

    @Override
    protected Void doInBackground(Void... params) {
        data = new ArrayList<>();
        Cursor c = context.getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);
        while (c.moveToNext()) {
            ChatappContactModel d = new ChatappContactModel();
            String contactName = c
                    .getString(c
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phNumber = c
                    .getString(c
                            .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            String phNo = phNumber.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
            d.setNumberInDevice(phNo);

            d.setFirstName(contactName);

            data.add(d);
        }
        return null;
    }
}