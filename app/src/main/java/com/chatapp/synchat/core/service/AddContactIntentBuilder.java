package com.chatapp.synchat.core.service;

/**
 * created by  Adhash Team on 5/8/2017.
 */

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.provider.Contacts;
import android.provider.ContactsContract;


public class AddContactIntentBuilder {
    private Intent intent;
    private ArrayList<ContentValues> parcels;
    private int numEmails = 0;
    private int numPhones = 0;
    private int numIm = 0;


    public AddContactIntentBuilder(String contactName) {

        intent = new Intent();

        intent.setAction(Intent.ACTION_INSERT);
        intent.setData(ContactsContract.Contacts.CONTENT_URI);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, contactName);
        if (isHoneycomb()) {
            parcels = new ArrayList<ContentValues>();
        }
    }



    public AddContactIntentBuilder addFormattedAddress(String address, int type) {

        if (isHoneycomb()) {
            ContentValues addressData = new ContentValues();

            //System.out.println("-----" + address.split("\n"));

            /*String newAddress[] = address.split("\n");
            String city[] = newAddress[1].split(",");
            String data[] = city[1].split(" ");
*/            addressData.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
            addressData.put(ContactsContract.CommonDataKinds.StructuredPostal.STREET, address);
            /*addressData.put(ContactsContract.CommonDataKinds.StructuredPostal.CITY, city[0]);
            addressData.put(ContactsContract.CommonDataKinds.StructuredPostal.REGION, data[1]);
            addressData.put(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE, data[2]);
            addressData.put(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY, newAddress[2]);*/
            addressData.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE, type);
            parcels.add(addressData);
        } else {
            intent.putExtra(Contacts.Intents.Insert.POSTAL, address);
            intent.putExtra(Contacts.Intents.Insert.POSTAL_TYPE, type);
        }
        return this;
    }


    public AddContactIntentBuilder addPhone(String phone, int type) {
        if (isHoneycomb()) {
            ContentValues data = new ContentValues();
            data.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            data.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone);
            data.put(ContactsContract.CommonDataKinds.Phone.TYPE, type);
            parcels.add(data);
        } else {
            switch (numPhones) {
                case 0:
                    intent.putExtra(Contacts.Intents.Insert.PHONE, phone);
                    intent.putExtra(Contacts.Intents.Insert.PHONE_TYPE, type);
                    break;
                case 1:
                    intent.putExtra(Contacts.Intents.Insert.SECONDARY_PHONE, phone);
                    intent.putExtra(Contacts.Intents.Insert.SECONDARY_PHONE_TYPE, type);
                    break;
                case 2:
                    intent.putExtra(Contacts.Intents.Insert.TERTIARY_PHONE, phone);
                    intent.putExtra(Contacts.Intents.Insert.TERTIARY_PHONE_TYPE, type);
                    break;
                default:
                    throw new IllegalStateException(String.format("can't add %d phone numbers in Android <v11",
                            (numEmails + 1)));
            }
        }
        numPhones++;
        return this;
    }



    public AddContactIntentBuilder addIMfield(String imfield, int type) {
        if (isHoneycomb()) {
            ContentValues data = new ContentValues();
            data.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE);
            data.put(ContactsContract.CommonDataKinds.Im.DATA, imfield);
            data.put(ContactsContract.CommonDataKinds.Im.PROTOCOL, type);
            parcels.add(data);
        } else {
            intent.putExtra(Contacts.Intents.Insert.IM_ISPRIMARY, imfield);
            intent.putExtra(Contacts.Intents.Insert.IM_PROTOCOL, type);


        }
        return this;
    }




    public AddContactIntentBuilder addEmail(String email, int type) {
        if (isHoneycomb()) {
            ContentValues data = new ContentValues();
            data.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
            data.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email);

            data.put(ContactsContract.CommonDataKinds.Email.TYPE, type);
            parcels.add(data);
        } else {
            switch (numEmails) {
                case 0:
                    intent.putExtra(Contacts.Intents.Insert.EMAIL, email);
                    intent.putExtra(Contacts.Intents.Insert.EMAIL_TYPE, type);
                    break;
                case 1:
                    intent.putExtra(Contacts.Intents.Insert.SECONDARY_EMAIL, email);
                    intent.putExtra(Contacts.Intents.Insert.SECONDARY_EMAIL_TYPE, type);
                    break;
                case 2:
                    intent.putExtra(Contacts.Intents.Insert.TERTIARY_EMAIL, email);
                    intent.putExtra(Contacts.Intents.Insert.TERTIARY_EMAIL_TYPE, type);
                    break;
                default:
                    throw new IllegalStateException(String.format("can't add %d emails in Android <v11", (numEmails + 1)));
            }
        }
        numEmails++;
        return this;
    }


    public AddContactIntentBuilder addPhoto(byte[] photo) {
        if (isHoneycomb()) {
            ContentValues addressData = new ContentValues();

            addressData.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE);
            addressData.put(ContactsContract.CommonDataKinds.Photo.PHOTO, photo);
            parcels.add(addressData);
        }
        return this;
    }

    public Intent build() {
        if (isHoneycomb()) {
            intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, parcels);
        }
        return intent;
    }


    private static boolean isHoneycomb() {
        return VersionHelper.getVersionSdkIntCompat() >= VersionHelper.VERSION_HONEYCOMB;
    }


    public static class VersionHelper {

        public static final int VERSION_CUPCAKE = 3;
        public static final int VERSION_DONUT = 4;
        public static final int VERSION_FROYO = 8;
        public static final int VERSION_HONEYCOMB = 11;
        public static final int VERSION_JELLYBEAN = 16;

        private static Field sdkIntField = null;
        private static boolean fetchedSdkIntField = false;

        public static int getVersionSdkIntCompat() {
            try {
                Field field = getSdkIntField();
                if (field != null) {
                    return (Integer) field.get(null);
                }
            } catch (IllegalAccessException ignore) {
                // ignore
            }
            return VERSION_CUPCAKE; // cupcake
        }

        private static Field getSdkIntField() {
            if (!fetchedSdkIntField) {
                try {
                    sdkIntField = Build.VERSION.class.getField("SDK_INT");
                } catch (NoSuchFieldException ignore) {
                    // ignore
                }
                fetchedSdkIntField = true;
            }
            return sdkIntField;
        }
    }
}

