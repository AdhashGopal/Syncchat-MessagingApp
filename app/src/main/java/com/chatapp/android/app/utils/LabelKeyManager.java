package com.chatapp.android.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * created by  Adhash Team on 10/16/2017.
 */
public class LabelKeyManager {

    private SharedPreferences labelPref;
    private SharedPreferences.Editor labelEditor;

    private final String LABEL_KEY_PREF = "LabelKeyPref";

    public static LabelKeyManager getInstance(Context context) {
        return new LabelKeyManager(context);
    }

    public LabelKeyManager(Context context) {
        labelPref = context.getSharedPreferences(LABEL_KEY_PREF, Context.MODE_PRIVATE);
        labelEditor = labelPref.edit();
    }
}
