package com.chatapp.android.app.utils;

/**
 * created by  Adhash Team on 3/12/2018.
 */
public class AppDataUtil {
    private static AppDataUtil ourInstance = new AppDataUtil();
    public long lastDialogShowTime=0L;
    public static AppDataUtil getInstance() {
        return ourInstance;
    }

    private AppDataUtil() {
    }
}
