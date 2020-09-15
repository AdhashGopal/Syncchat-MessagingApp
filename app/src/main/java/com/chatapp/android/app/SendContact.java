package com.chatapp.android.app;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.adapter.ContactToSendAdapter;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.model.ContactToSend;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * created by  Adhash Team on 4/4/2017.
 */
public class SendContact extends CoreActivity {
    public String contactID;
    public TextView Uname, organisation;
    private LinearLayoutManager mLayoutManager;
    ContactToSendAdapter contactToSendAdapter;
    ArrayList<ContactToSend> savedData = new ArrayList<>();
    ArrayList<ContactToSend> items = new ArrayList<>();
    RecyclerView rvcontacts;
    SessionManager sessionmanager;

    private static final int REQUEST_CODE_CONTACTS = 4;
    ContactToSend contactToSend;
    Button OkBtn;
    String value, name;
    public ImageView image_backNavigate;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_contact);
        rvcontacts = (RecyclerView) findViewById(R.id.rv_contacts);
        Uname = (TextView) findViewById(R.id.contact_save_name);
        organisation = (TextView) findViewById(R.id.organisation_details);
        OkBtn = (Button) findViewById(R.id.doneButton);
        image_backNavigate = (ImageView) findViewById(R.id.backarrow_Contact);
        sessionmanager = SessionManager.getInstance(SendContact.this);
        getSupportActionBar().hide();
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvcontacts.setLayoutManager(mLayoutManager);
        rvcontacts.setItemAnimator(new DefaultItemAnimator());
        rvcontacts.setHasFixedSize(true);


        initData();
    }

    /**
     * data binding
     */
    private void initData() {
        Intent intentContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intentContact, REQUEST_CODE_CONTACTS);

        OkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent okIntent = new Intent();
                okIntent.putExtra("ContactToSend", items);
                okIntent.putExtra("name", name);
                okIntent.putExtra("title", title);
                // okIntent.putExtra("mySelectedData",item);
                setResult(RESULT_OK, okIntent);
                finish();
            }
        });

        image_backNavigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    /**
     * based on the activity result to perform the specific function
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CONTACTS && resultCode == RESULT_OK && null != data) {
            ContentResolver cr = getContentResolver();
            Uri contactData = data.getData();
            Cursor cur = cr.query(contactData, null, null, null, null);
            String phone = null;
            String phType = null;
            String emailType = null;
            String emailContact = null;

            if (cur != null) {
                while (cur.moveToNext()) {
                    String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                    name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contactToSend = new ContactToSend();
                    contactToSend.setNumber(name);
                    contactToSend.setType("Name");
                    contactToSend.setSubType(name);
                    Uname.setText(name);
                    if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                        System.out.println("name : " + name + ", ID : " + id);

                        Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{
                                id
                        }, null);

                        if (pCur != null) {
                            while (pCur.moveToNext()) {
                                phone = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                phType = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
                                System.out.println("phone" + phone);
                                phone =  phone.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
                                if(!phone.startsWith(sessionmanager.getCountryCodeOfCurrentUser())){
                                    phone = sessionmanager.getCountryCodeOfCurrentUser().concat(" ").concat(phone);
                                }
                                //PhNo.setText(phone);
                                contactToSend = new ContactToSend();
                                contactToSend.setNumber(phone);

                                if (phType == null || phType.equals("")) {
                                    contactToSend.setSubType("Custom");
                                } else {
                                    switch (Integer.parseInt(phType)) {
                                        case 1:
                                            contactToSend.setSubType("Home");
                                            break;
                                        case 2:
                                            contactToSend.setSubType("Mobile");
                                            break;
                                        case 3:
                                            contactToSend.setSubType("Work");
                                            break;
                                        case 4:
                                            contactToSend.setSubType("Work Fax");
                                            break;
                                        case 5:
                                            contactToSend.setSubType("Home Fax");
                                            break;
                                        case 6:
                                            contactToSend.setSubType("Pager");
                                            break;
                                        case 7:
                                            contactToSend.setSubType("Other");
                                            break;
                                        case 8:
                                            contactToSend.setSubType("Callback");
                                            break;
                                        default:
                                            contactToSend.setSubType("Custom");
                                            break;

                                    }
                                }
                                contactToSend.setType("Phone");
                                savedData.add(contactToSend);
                                items.add(contactToSend);
                            }
                            pCur.close();
                        }

                        Cursor emailCur = cr.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", new String[]{
                                id
                        }, null);
                        if (emailCur != null) {
                            while (emailCur.moveToNext()) {
                                emailContact = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                emailType = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                                System.out.println("Email " + emailContact + " Email Type : " + emailType);
                                contactToSend = new ContactToSend();
                                contactToSend.setNumber(emailContact);
                                contactToSend.setType("Email");

                                if (emailType == null || emailType.equals("")) {
                                    contactToSend.setSubType("Other");
                                } else {
                                    switch (emailType) {
                                        case "1":
                                            contactToSend.setSubType("Home");
                                            break;
                                        case "2":
                                            contactToSend.setSubType("Work");
                                            break;
                                        default:
                                            contactToSend.setSubType("Other");
                                            break;
                                    }
                                }
                                savedData.add(contactToSend);
                                items.add(contactToSend);
                                //Email.setText(emailContact);
                            }
                            emailCur.close();
                        }

                        String addrWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                        String[] addrWhereParams = new String[]{id,
                                ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE};
                        Cursor addrCur = cr.query(ContactsContract.Data.CONTENT_URI,
                                null, addrWhere, addrWhereParams, null);
                        if (addrCur != null) {
                            while (addrCur.moveToNext()) {
                                // String poBox = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
                                String street = addrCur.getString(
                                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
                                String city = addrCur.getString(
                                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
                                String state = addrCur.getString(
                                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
                                String postalCode = addrCur.getString(
                                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
                                String country = addrCur.getString(
                                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
                                String type = addrCur.getString(
                                        addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));

                                //String formatted_address = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS));

                                String address = street + " " + city + " " + state + " " + postalCode + " " + country;
                                //String address = formatted_address;
                                System.out.println("address" + address);
                                contactToSend = new ContactToSend();

                                if (type == null || type.equals("")) {
                                    contactToSend.setSubType("Other");
                                } else {
                                    switch (type) {
                                        case "1":
                                            contactToSend.setSubType("Home");
                                            break;
                                        case "2":
                                            contactToSend.setSubType("Work");
                                            break;
                                        default:
                                            contactToSend.setSubType("Other");
                                            break;
                                    }
                                }

                                contactToSend.setNumber(address);
                                contactToSend.setType("Address");
                                savedData.add(contactToSend);
                                items.add(contactToSend);
                            }
                            addrCur.close();
                        }

                   /* String noteWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                    String[] noteWhereParams = new String[]{id,
                            ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE};
                    Cursor noteCur = cr.query(ContactsContract.Data.CONTENT_URI, null, noteWhere, noteWhereParams, null);
                    if (noteCur.moveToNext()) {
                        String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
                        contactToSend = new ContactToSend();
                        contactToSend.setNumber(note);
                        c`ontactToSend.setSubType("Note");
                        contactToSend.setType("Note");
                        savedData.add(contactToSend);
                    }
                    noteCur.close();*/

                        String imWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                        String[] imWhereParams = new String[]{id,
                                ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE};
                        Cursor imCur = cr.query(ContactsContract.Data.CONTENT_URI,
                                null, imWhere, imWhereParams, null);
                        if (imCur != null) {
                            while (imCur.moveToNext()) {
                                String imName = imCur.getString(
                                        imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
                                String imType;
                                imType = imCur.getString(
                                        imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));

                                int protocol = imCur
                                        .getInt(imCur
                                                .getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
                                contactToSend = new ContactToSend();
                                switch (protocol) {
                                    case -1:
                                        contactToSend.setSubType("Others");
                                        break;
                                    case 0:
                                        contactToSend.setSubType("AIM");
                                        break;
                                    case 1:
                                        contactToSend.setSubType("MSN");
                                        break;
                                    case 2:
                                        contactToSend.setSubType("YAHOO");
                                        break;
                                    case 3:
                                        contactToSend.setSubType("SKYPE");
                                        break;
                                    case 4:
                                        contactToSend.setSubType("QQ");
                                        break;
                                    case 5:
                                        contactToSend.setSubType("GOOGLE_TALK");
                                        break;
                                    case 6:
                                        contactToSend.setSubType("ICQ");
                                        break;
                                    case 7:
                                        contactToSend.setSubType("JABBER");
                                        break;
                                    case 8:
                                        contactToSend.setSubType("NETMEETING");
                                        break;
                                    default:
                                        contactToSend.setSubType("CUSTOM");
                                        break;
                                }

                                contactToSend.setNumber(imName);
                                // contactToSend.setSubType("CUSTOM");
                                contactToSend.setType("Instant Messenger");
                                savedData.add(contactToSend);
                                items.add(contactToSend);

                            }
                            imCur.close();
                        }

                        String orgWhere = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?";
                        String[] orgWhereParams = new String[]{id,
                                ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE};
                        Cursor orgCur = cr.query(ContactsContract.Data.CONTENT_URI,
                                null, orgWhere, orgWhereParams, null);

                        if (orgCur != null) {
                            if (orgCur.moveToNext()) {
                                String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
                                title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.COMPANY));

                                organisation.setText(title);
                                contactToSend = new ContactToSend();
                                contactToSend.setNumber(orgName);
                                contactToSend.setType("Organisation");
                                contactToSend.setSubType(title);
                            }
                            orgCur.close();
                        }


                        HashSet<ContactToSend> hashSet = new HashSet<ContactToSend>();
                        hashSet.addAll(savedData);
                        savedData.clear();
                        savedData.addAll(hashSet);

                        contactToSendAdapter = new ContactToSendAdapter(this, savedData, new ContactToSendAdapter.OnItemCheckListener() {


                            @Override
                            public void onItemCheck(ContactToSend contact) {
                                items.add(contact);
                            }

                            @Override
                            public void onItemUncheck(ContactToSend contact) {
                                items.remove(contact);
                            }


                        });
                        rvcontacts.setAdapter(contactToSendAdapter);

                    }

                }

                cur.close();
            }
        } else {
            finish();
        }
    }

}

