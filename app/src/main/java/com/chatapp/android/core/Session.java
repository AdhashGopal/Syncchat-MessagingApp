package com.chatapp.android.core;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * created by  Adhash Team on 10/13/2016.
 */
public class Session {

    public static final String MyPREFERENCES = "Preference";
    public static final String POPUP = "popup";
    public static final String LASTSEEN = "lastseen";
    public static final String COLOR = "lastseen";
    public static final String GROUPRINGTONE = "groupringtone";
    public static final String GROUPPOPUP = "grouppopup";
    public static final String GROUPVIBRATE = "groupvibrate";
    public static final String GROUPLIGHT = "grouplight";
    public static final String PROFILE = "profile";
    public static final String STATUS = "status";
    public static final String VIBRATE = "vibrate";
    public static final String LIGHT = "light";

    public static final String MOBILEDATA = "mobiledata";
    public static final String MUTECHAT = "mutechat";
    public static final String DOCUMENTID = "documentId";
    public static final String GALLERY = "gallery";
    public static final String RINGTONE = "ringtone";
    public static final String TSIZE = "textsize";
    private static final String BACKUPPERIOD = "backupperiod";
    private static final String ACCOUNTTYPE = "accounttype";
    public static final String WIFI = "wifi";
    public static final String ROAMING = "roaming";
    private static final String MESSAGE = "message";
    private static final String BACKGROUND = "background";
    private static final String POSITION = "position";
    private static final String INTONEOUTTONE = "intoneouttone";
    private static final String ARCHIVE = "archive";
    private static final String KEY_BLOCKED_CONTACT_IDS = "blockedIds";
    private static final String ARCHIVECOUNT = "archivecount";
    private static final String KEY_BLOCKED_CONTACT_COUNT = "BlockedContactsCount";
    private static final String KEY_ENTER_TO_SEND = "EnterToSend";
    private static final String KEY_FAV_CONTACTS_DATA = "FavContacts";
    private static final String KEY_READRECEPTS = "readrecepts";
    private static final String KEY_TO_NOTIFICATION = "tovalue";
    private static final String KEY_LAST_OFFLINE_HISTORY = "LastOfflineHistory";
    private static final String KEY_MEDIA_FRAGMENT_DOCID = "mediafragmentdocid";
    private static final String KET_SCOKET_CONNECT_DISCONNECT = "conncect_disconnect";
    private static final String KEY_SENTMESSAGECOUNT = "sentmessagecount";
    private static final String KEY_RECEIVEMESSAGECOUNT = "receivemessagecount";
    private static final String KEY_RECEIVEMESSAGELENGTH = "receviermessagelength";
    private static final String KEY_SENTMESSAGELENGTH = "sentmessagelength";
    private static final String KEY_SENTMEDIALENGTH = "sentmedialength";
    private static final String KEY_RECEVIEDMEDIALENGTH = "receviedmedialength";
    private static final String KEY_BLOCKEDUSERID = "blockedUserIds";

    ArrayList<String> BlockedUserIds = new ArrayList<String>();
    private static final Type LIST_TYPE = new TypeToken<List<String>>() {
    }.getType();

    /*public static final int LAST_SEEN_EVERY_ONE = 0;
    public static final int LAST_SEEN_MY_CONTACTS = 1;
    public static final int LAST_SEEN_NO_BODY = 2;*/

    SharedPreferences Pref;

    public Session(Context context) {
        Pref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    }

    public Boolean getconnect_disconnect() {
        return Pref.getBoolean(KET_SCOKET_CONNECT_DISCONNECT, false);
    }

    public void setconnect_disconnectevent(Boolean isconnect) {
        Editor prefEditor = Pref.edit();
        prefEditor.putBoolean(KET_SCOKET_CONNECT_DISCONNECT, isconnect);
        prefEditor.apply();
    }

    public void setEnterKeyPressToSend(boolean isEnabled) {
        Editor prefEditor = Pref.edit();
        prefEditor.putBoolean(KEY_ENTER_TO_SEND, isEnabled);
        prefEditor.apply();
    }

    public void setLastOfflineHistoryAt(long currentTime) {
        Editor prefEditor = Pref.edit();
        prefEditor.putLong(KEY_LAST_OFFLINE_HISTORY, currentTime);
        prefEditor.apply();
    }

    public Long getLastOfflineHistoryAt() {
        return Pref.getLong(KEY_LAST_OFFLINE_HISTORY, 0);
    }

    public boolean isEnterKeyPressToSend() {
        return Pref.getBoolean(KEY_ENTER_TO_SEND, false);
    }

    public void setFavContacts(String arrContacts) {
        Editor prefEditor = Pref.edit();
        prefEditor.putString(KEY_FAV_CONTACTS_DATA, arrContacts);
        prefEditor.apply();
    }

    public String getFavContacts() {
        return Pref.getString(KEY_FAV_CONTACTS_DATA, "");
    }

    public void setMsisdn(String userId, String msisdn) {
        if(userId!=null) {
            Editor prefEditor = Pref.edit();
            prefEditor.putString(userId.concat("-msisdn"), msisdn);
            prefEditor.apply();
        }
    }

    public String getMsisdn(String userId) {
        if(userId!=null) {
            return Pref.getString(userId.concat("-msisdn"), "");
        }
        return "";
    }


    public void setName(String userId, String name) {
        if(userId!=null) {
            Editor prefEditor = Pref.edit();
            prefEditor.putString(userId.concat("-name"), name);
            prefEditor.apply();
        }
    }

    public String getName(String userId) {
        if(userId!=null) {
            return Pref.getString(userId.concat("-name"), "");
        }
        return "";
    }


    public void setImageTS(String key,String value) {
        if(key!=null) {
            Editor prefEditor = Pref.edit();
            prefEditor.putString(key, value);
            prefEditor.apply();
        }
    }

    public String getImageTS(String imageTs) {
        if(imageTs!=null) {
            return Pref.getString(imageTs, "");
        }
        return "";
    }



    public int getarchivecountgroup() {

        return Pref.getInt(ARCHIVECOUNT, 0);

    }

    public void putarchivegroup(String docId) {
        if (!getarchive(docId)) {
            Editor editor = Pref.edit();
            editor.putBoolean(docId + "-archivegroup", true);
            int archiveCount = Pref.getInt(ARCHIVECOUNT, 0);
            archiveCount += 1;

            editor.putInt(ARCHIVECOUNT, archiveCount);
            editor.apply();
        }
    }

    public void removearchivegroup(String docId) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putBoolean(docId + "-archivegroup", false);
        int archiveCount = Pref.getInt(ARCHIVECOUNT, 0);
        archiveCount -= 1;
        if (archiveCount < 0) {
            archiveCount = 0;
        }
        editor.putInt(ARCHIVECOUNT, archiveCount);
        editor.commit();

    }

    public boolean getarchivegroup(String docId) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Boolean isArchived = Pref.getBoolean(docId + "-archivegroup", false);
        return isArchived;
    }

    public int getarchivecount() {

        return Pref.getInt(ARCHIVE, 0);

    }

    public void putarchive(String docId) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putBoolean(docId + "-archive", true);
        int archiveCount = Pref.getInt(ARCHIVE, 0);
        archiveCount += 1;
        editor.putInt(ARCHIVE, archiveCount);
        editor.commit();

    }

    public void removearchive(String docId) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putBoolean(docId + "-archive", false);
        int archiveCount = Pref.getInt(ARCHIVE, 0);
        archiveCount -= 1;
        if (archiveCount < 0) {
            archiveCount = 0;
        }
        editor.putInt(ARCHIVE, archiveCount);
        editor.commit();

    }

    public boolean getarchive(String docId) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Boolean isArchived = Pref.getBoolean(docId + "-archive", false);
        return isArchived;
    }

    public boolean getmark(String userKey) {
        if(userKey!=null)
        return Pref.getBoolean(userKey.concat("-mark"), true);
        return true;
    }

    public void putmark(String userKey) {
        if(userKey!=null) {
            Editor editor = Pref.edit();
            editor.putBoolean(userKey.concat("-mark"), false);
            editor.apply();
        }
    }

    public void Removemark(String userKey) {
        if(userKey!=null) {
            Editor editor = Pref.edit();
            editor.putBoolean(userKey.concat("-mark"), true);
            editor.apply();
        }
    }

    public void putPrefsintoneouttone(Boolean intoneouttone) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putBoolean(INTONEOUTTONE, intoneouttone);

        editor.commit();

    }

    public Boolean getPrefsNameintoneouttone() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Boolean Name = Pref.getBoolean(INTONEOUTTONE, false);

        return Name;

    }

    public void puttoid(String to) {
        Editor editor = Pref.edit();
        editor.putString(KEY_TO_NOTIFICATION, to);
        editor.commit();

    }

    public String gettoid() {
        String Name = Pref.getString(KEY_TO_NOTIFICATION, "");
        return Name;
    }

    public void putColor(String color) {

        Editor editor = Pref.edit();
        editor.putString(COLOR, color);

        editor.commit();

    }

    public String getColor() {

        String color = Pref.getString(COLOR, "");


        String prefs = color;

        return prefs;

    }

    public void putTone(String tone) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(RINGTONE, tone);

        editor.commit();

    }

    public String getTone() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(RINGTONE, "");


        String prefs = Name;

        return prefs;

    }

    public void puttextsize(String text) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(TSIZE, text);

        editor.commit();

    }


    public String gettextsize() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(TSIZE, "");


        String prefs = Name;

        return prefs;

    }

    public String getromingPrefsName() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(ROAMING, "");


        String prefs = Name;

        return prefs;

    }

    public void putromingPrefs(String roming) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(ROAMING, roming);


        editor.commit();

    }

    public void putbackground(String background) {


        Editor editor = Pref.edit();
        editor.putString(BACKGROUND, background);

        editor.commit();

    }

    public String getbackground() {


        String Name = Pref.getString(BACKGROUND, "");


        String prefs = Name;

        return prefs;

    }

    public void putbackgrounddef(String background) {


        Editor editor = Pref.edit();
        editor.putString(BACKGROUND, background);

        editor.commit();

    }

    public String getbackgrounddef() {


        String Name = Pref.getString(BACKGROUND, "");


        String prefs = Name;

        return prefs;

    }


    public void putgalleryPrefs(String gallery) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(GALLERY, gallery);

        editor.commit();

    }

    public String getgalleryPrefsName() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(GALLERY, "");


        String prefs = Name;

        return prefs;

    }

    public void putvibratePrefs(String vibrate) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(VIBRATE, vibrate);

        editor.commit();

    }

    public String getvibratePrefsName() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(VIBRATE, "");


        String prefs = Name;

        return prefs;

    }

    public void putpopupPrefs(String popup) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(POPUP, popup);

        editor.commit();

    }

    public String getpopupPrefsName() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(POPUP, "");


        String prefs = Name;

        return prefs;

    }

    public void putlightPrefs(String light) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(LIGHT, light);

        editor.commit();

    }

    public String getlightPrefsName() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(LIGHT, "");


        String prefs = Name;

        return prefs;

    }

    public void putmobiledataPrefs(String mobiledata) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(MOBILEDATA, mobiledata);

        editor.commit();

    }

    public String getmobiledataPrefsName() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(MOBILEDATA, "");
        String prefs = Name;

        return prefs;

    }

    public String getwifiPrefsName() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(WIFI, "");


        String prefs = Name;

        return prefs;

    }

    public void putwifiPrefs(String wifi) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(WIFI, wifi);

        editor.commit();

    }

    public List<String> getBlockedIds() {
        List<String> tst;
        tst = new Gson().fromJson(Pref.getString(KEY_BLOCKED_CONTACT_IDS, null), LIST_TYPE);
        return tst;
    }

    public void putlightPrefsgroup(String grouplight) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(GROUPLIGHT, grouplight);

        editor.commit();

    }

    public String getlightPrefsNamegroup() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(GROUPLIGHT, "");


        String prefs = Name;

        return prefs;

    }

    public void putpopupPrefsgroup(String grouppopup) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(GROUPPOPUP, grouppopup);

        editor.commit();

    }

    public String getpopupPrefsNamegroup() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(GROUPPOPUP, "");

        String prefs = Name;

        return prefs;

    }

    public void putgroupTone(String grouptone) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(GROUPRINGTONE, grouptone);

        editor.commit();

    }

    public String getgroupTone() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(GROUPRINGTONE, "");


        String prefs = Name;

        return prefs;

    }

    public void putvibratePrefsgroup(String groupvibrate) {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        Editor editor = Pref.edit();
        editor.putString(GROUPVIBRATE, groupvibrate);

        editor.commit();

    }

    public String getvibratePrefsNamegroup() {

//        LoginPref = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String Name = Pref.getString(GROUPVIBRATE, "");


        String prefs = Name;

        return prefs;

    }

    public String getMediaDocid() {

        return Pref.getString(KEY_MEDIA_FRAGMENT_DOCID, "");
    }

    public void putMediadocid(String Mediadocid) {
        Editor editor = Pref.edit();
        editor.putString(KEY_MEDIA_FRAGMENT_DOCID, Mediadocid);
        editor.commit();
    }

    public void putsentmessagecount(int count) {
        Editor editor = Pref.edit();
        editor.putInt(KEY_SENTMESSAGECOUNT, count);
        editor.commit();
    }

    public int getsentmessagecount() {
        int Name = Pref.getInt(KEY_SENTMESSAGECOUNT, 0);
        int prefs = Name;
        return prefs;
    }

    public void putreceivemessagecount(int count) {
        Editor editor = Pref.edit();
        editor.putInt(KEY_RECEIVEMESSAGECOUNT, count);
        editor.commit();
    }

    public int getreceviedmessagecount() {
        int Name = Pref.getInt(KEY_RECEIVEMESSAGECOUNT, 0);
        int prefs = Name;
        return prefs;
    }

    public void putreceivemessagelength(long count) {
        Editor editor = Pref.edit();
        editor.putLong(KEY_RECEIVEMESSAGELENGTH, count);
        editor.commit();
    }

    public long getreceviedmessagelength() {
        long Name = Pref.getLong(KEY_RECEIVEMESSAGELENGTH, 0);
        long prefs = Name;
        return prefs;
    }

    public void putsentmessagelength(long count) {
        Editor editor = Pref.edit();
        editor.putLong(KEY_SENTMESSAGELENGTH, count);
        editor.commit();
    }

    public long getsentmessagelength() {
        long Name = Pref.getLong(KEY_SENTMESSAGELENGTH, 0);
        long prefs = Name;
        return prefs;
    }

    public void putsentmedialength(long count) {
        Editor editor = Pref.edit();
        editor.putLong(KEY_SENTMEDIALENGTH, count);
        editor.commit();
    }

    public long getsentmedialength() {
        long Name = Pref.getLong(KEY_SENTMEDIALENGTH, 0);
        long prefs = Name;
        return prefs;
    }

    public void putreceivemedialength(long count) {
        Editor editor = Pref.edit();
        editor.putLong(KEY_RECEVIEDMEDIALENGTH, count);
        editor.commit();
    }

    public long getreceviedmedialength() {
        long Name = Pref.getLong(KEY_RECEVIEDMEDIALENGTH, 0);
        long prefs = Name;
        return prefs;
    }

    public void clearData() {
        SharedPreferences.Editor editor = Pref.edit();
        editor.clear();
        editor.apply();
    }
}
