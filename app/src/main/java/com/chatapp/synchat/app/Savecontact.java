package com.chatapp.synchat.app;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.adapter.SaveContactAdapter;
import com.chatapp.synchat.app.dialog.CustomAlertDialog;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.model.ContactToSend;
import com.chatapp.synchat.core.service.AddContactIntentBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * created by  Adhash Team on 12/28/2016.
 */
public class Savecontact extends CoreActivity {
    ImageView backarrow_Contact;
    String name, number;
    TextView text_actionbar_1;
    Button addcontact;
    RecyclerView rv_contact;
    TextView contactname, contactnumber;
    private String contacts;
    private String myDataMob, myDataHome, myDataWork, WorkFax, HomeFax, callback, pager, other, custom;
    private String AIM, MSN, YAHOO, SKYPE, GOOGLE_TALK, ICQ, QQ, JABBER, GOOGLE_NETMEETING, CUSTOM;
    private String homeEmail, workEmail, otherEmail;
    private String homeaddressData, workaddressData, otheraddressData;
    private LinearLayoutManager mLayoutManager;
    SaveContactAdapter savecontactAdapter;
    ArrayList<ContactToSend> contactsData = new ArrayList<>();

    /**
     * Data binding & add contact data
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.savecontact);

        getSupportActionBar().hide();
        /*try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }*/
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            name = bundle.getString("name");
            number = bundle.getString("number");
            contacts = bundle.getString("contactList");
        }
        addcontact = (Button) findViewById(R.id.r2_viewcontact);
        rv_contact = (RecyclerView) findViewById(R.id.contact_items);
        contactname = (TextView) findViewById(R.id.contact_save_name);
        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv_contact.setLayoutManager(mLayoutManager);
        rv_contact.setItemAnimator(new DefaultItemAnimator());
        rv_contact.setHasFixedSize(true);
        backarrow_Contact = (ImageView) findViewById(R.id.backarrow_Contact);

        text_actionbar_1 = (TextView) findViewById(R.id.text_actionbar_1);
        contactname.setText(name);
        backarrow_Contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        addcontact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CustomAlertDialog dialog = new CustomAlertDialog();
                dialog.setMessage(getResources().getString(R.string.new_exitcontact));
                dialog.setPositiveButtonText("NEW");
                dialog.setNegativeButtonText("EXISTING");

                dialog.setCustomDialogCloseListener(new CustomAlertDialog.OnCustomDialogCloseListener() {
                    @Override
                    public void onPositiveButtonClick() {
                        /*Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
                        // Sets the MIME type to match the Contacts Provider
                        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
                        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
                        intent.putExtra(ContactsContract.Intents.Insert.PHONE, number);
                        startActivity(intent);
                        finish();*/
                        Intent intent = new AddContactIntentBuilder(name)
                                .addPhone(myDataMob, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                                .addPhone(myDataHome, ContactsContract.CommonDataKinds.Phone.TYPE_HOME)
                                .addPhone(myDataWork, ContactsContract.CommonDataKinds.Phone.TYPE_WORK)
                                .addPhone(WorkFax, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK)
                                .addPhone(HomeFax, ContactsContract.CommonDataKinds.Phone.TYPE_FAX_HOME)
                                .addPhone(callback, ContactsContract.CommonDataKinds.Phone.TYPE_CALLBACK)
                                .addPhone(pager, ContactsContract.CommonDataKinds.Phone.TYPE_PAGER)
                                .addPhone(other, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)
                                .addPhone(custom, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                                .addEmail(homeEmail, ContactsContract.CommonDataKinds.Email.TYPE_HOME)
                                .addEmail(workEmail, ContactsContract.CommonDataKinds.Email.TYPE_WORK)
                                .addEmail(otherEmail, ContactsContract.CommonDataKinds.Email.TYPE_OTHER)
                                .addFormattedAddress(homeaddressData, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME)
                                .addIMfield(AIM, ContactsContract.CommonDataKinds.Im.PROTOCOL_AIM)
                                .addIMfield(MSN, ContactsContract.CommonDataKinds.Im.PROTOCOL_MSN)
                                .addIMfield(YAHOO, ContactsContract.CommonDataKinds.Im.PROTOCOL_YAHOO)
                                .addIMfield(SKYPE, ContactsContract.CommonDataKinds.Im.PROTOCOL_SKYPE)
                                .addIMfield(GOOGLE_TALK, ContactsContract.CommonDataKinds.Im.PROTOCOL_GOOGLE_TALK)
                                .addIMfield(ICQ, ContactsContract.CommonDataKinds.Im.PROTOCOL_ICQ)
                                .addIMfield(QQ, ContactsContract.CommonDataKinds.Im.PROTOCOL_QQ)
                                .addIMfield(JABBER, ContactsContract.CommonDataKinds.Im.PROTOCOL_JABBER)
                                .addIMfield(GOOGLE_NETMEETING, ContactsContract.CommonDataKinds.Im.PROTOCOL_NETMEETING)
                                .addIMfield(CUSTOM, ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM)
                                .addFormattedAddress(workaddressData, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK)
                                .addFormattedAddress(otheraddressData, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER)
                                .build();

                        startActivity(intent);
                    }

                    @Override
                    public void onNegativeButtonClick() {
                        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                        startActivityForResult(intent, 1);
                        finish();
                    }
                });

                dialog.show(getSupportFragmentManager(), "Save contact");


            }
        });


        try {
            if (contacts != null) {
                JSONArray contactmail_List =null,contactIM_List = null,contactaddress_List=null;
                JSONObject data = new JSONObject(contacts);
                JSONArray contactph_List = data.getJSONArray("phone_number");
                if(data.has("email")) {
                    contactmail_List = data.getJSONArray("email");
                }
                if(data.has("im")) {
                    contactIM_List = data.getJSONArray("im");
                }
                if(data.has("address")) {
                   contactaddress_List = data.getJSONArray("address");
                }
                System.out.println("" + contactph_List);
                if (contactph_List.length() > 0) {
                    for (int i = 0; i < contactph_List.length(); i++) {
                        ContactToSend cts = new ContactToSend();
                        JSONObject phone = contactph_List.getJSONObject(i);
                        String type = phone.getString("type");
                        cts.setSubType(type);
                        String value = phone.getString("value");
                        cts.setNumber(value);
                        cts.setType("Phone");
                        contactsData.add(cts);
                        switch (type) {
                            case "Mobile":
                                myDataMob = value;
                                break;
                            case "Home":
                                myDataHome = value;
                                break;
                            case "Work":
                                myDataWork = value;
                                break;
                            case "Work Fax":
                                WorkFax = value;
                                break;
                            case "Home Fax":
                                HomeFax = value;
                                break;
                            case "Callback":
                                callback = value;
                                break;
                            case "Pager":
                                pager = value;
                                break;
                            case "Other":
                                other = value;
                                break;
                            case "Custom":
                                custom = value;
                                break;
                        }

                    }
                }
        if(contactIM_List != null) {
            if (contactIM_List.length() > 0) {
                for (int i = 0; i < contactIM_List.length(); i++) {
                    ContactToSend cts = new ContactToSend();
                    JSONObject IM = contactIM_List.getJSONObject(i);
                    String type = IM.getString("type");
                    cts.setSubType(type);
                    String value = IM.getString("value");
                    cts.setNumber(value);
                    cts.setType("Instant Messenger");
                    contactsData.add(cts);
                    switch (type) {
                        case "AIM":
                            AIM = value;
                            break;
                        case "MSN":
                            MSN = value;
                            break;
                        case "YAHOO":
                            YAHOO = value;
                            break;
                        case "SKYPE":
                            SKYPE = value;
                            break;
                        case "QQ":
                            QQ = value;
                            break;
                        case "GOOGLE_TALK":
                            GOOGLE_TALK = value;
                            break;
                        case "ICQ":
                            ICQ = value;
                            break;
                        case "JABBER":
                            JABBER = value;
                            break;
                        case "NETMEETING":
                            GOOGLE_NETMEETING = value;
                            break;
                        default:
                            CUSTOM = value;
                            break;
                    }
                }
            }
        }     if(contactmail_List != null) {
                    if (contactmail_List.length() > 0) {
                        for (int i = 0; i < contactmail_List.length(); i++) {
                            ContactToSend cts = new ContactToSend();
                            JSONObject mail = contactmail_List.getJSONObject(i);
                            String type = mail.getString("type");
                            cts.setSubType(type);
                            String value = mail.getString("value");
                            cts.setNumber(value);
                            cts.setType("Email");
                            contactsData.add(cts);
                            switch (type) {
                                case "Home":
                                    homeEmail = value;
                                    break;
                                case "Work":
                                    workEmail = value;
                                    break;
                                default:
                                    otherEmail = value;
                                    break;
                            }
                        }
                    }
                }
                if(contactaddress_List != null) {
                    if (contactaddress_List.length() > 0) {
                        for (int i = 0; i < contactaddress_List.length(); i++) {
                            ContactToSend cts = new ContactToSend();
                            JSONObject address = contactaddress_List.getJSONObject(i);
                            String type = address.getString("type");
                            cts.setSubType(type);
                            String value = address.getString("value");
                            cts.setNumber(value);
                            cts.setType("Address");
                            contactsData.add(cts);


                            switch (type) {
                                case "Home":
                                    homeaddressData = value;
                                    break;
                                case "Work":
                                    workaddressData = value;
                                    break;
                                default:
                                    otheraddressData = value;
                                    break;
                            }

                        }

                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        HashSet<ContactToSend> hashSet = new HashSet<ContactToSend>();
        hashSet.addAll(contactsData);
        contactsData.clear();
        contactsData.addAll(hashSet);

        savecontactAdapter = new SaveContactAdapter(this, contactsData);
        rv_contact.setAdapter(savecontactAdapter);



        /*for(int j = 0; j <contactsData.size();j++){
            String type = contactsData.get(j).getSubType();
            switch (type){

                case

            }
        }*/
    }


}
