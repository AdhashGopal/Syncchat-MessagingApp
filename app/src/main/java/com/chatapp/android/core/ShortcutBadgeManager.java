package com.chatapp.android.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import com.chatapp.android.R;
import com.chatapp.android.app.ChatViewActivity;
import com.chatapp.android.core.chatapphelperclass.ChatappImageUtils;

/**
 * created by  Adhash Team on 12/2/2016.
 */
public class ShortcutBadgeManager {
    private SharedPreferences badgePref, callPref;
    private final String SHORTCUT_BADGE_PREF = "ShorcutBadgePref";
    private final String SHORTCUT_CALL_BADGE_PREF = "ShorcutBadgeCallPref";

    private final String KEY_BADGE_TOTAL_COUNT = "TotalCount";
    private final String KEY_SINGLE_MSG_COUNT = "SingleMsgCount";
    private final String KEY_GROUP_MSG_COUNT = "GroupMsgCount";
    private final String contactRefresh_time = "contactrefreshtime";
    private final String firsttimeSyncCompleted = "firsttime_sync_complete";
    private final String firsttimetimeloading = "firsttimetimeloading";

    private final String KEY_BADGE_CALL_SINGLE_COUNT = "CallSingleCount";

    private static final String TAG = "ShortcutBadgeManager";

    /**
     * constructor
     *
     * @param context current activity
     */
    public ShortcutBadgeManager(Context context) {
        badgePref = context.getSharedPreferences(SHORTCUT_BADGE_PREF, Context.MODE_PRIVATE);
        callPref = context.getSharedPreferences(SHORTCUT_CALL_BADGE_PREF, Context.MODE_PRIVATE);
    }

    /**
     * Message Count
     *
     * @param countKey based on value to find group / single chat count
     */
    public void putMessageCount(String countKey) {
        int totCount = getTotalCount() + 1;
        int count = getSingleBadgeCount(countKey) + 1;

        SharedPreferences.Editor badgeEditor = badgePref.edit();
        badgeEditor.putInt(countKey, count);
        badgeEditor.putInt(KEY_BADGE_TOTAL_COUNT, totCount);

        if (countKey.contains("-g")) {  // For adding group message count
            int groupMsgCount = getAllGroupMsgCount() + 1;
            badgeEditor.putInt(KEY_GROUP_MSG_COUNT, groupMsgCount);
        } else {
            int singleMsgCount = getAllSingleMsgCount() + 1;
            badgeEditor.putInt(KEY_SINGLE_MSG_COUNT, singleMsgCount);
        }

        badgeEditor.apply();

    }


    /**
     * Call Count
     *
     * @param countKey based on value to find group / single chat count
     */
    public void putCallCount(String countKey) {

        int count = getSingleCallBadgeCount(countKey) + 1;
        int totCount = getTotalCallCount() + 1;

        SharedPreferences.Editor badgeEditor = callPref.edit();
        badgeEditor.putInt(countKey, count);
        badgeEditor.putInt(KEY_BADGE_TOTAL_COUNT, totCount);


        badgeEditor.apply();

    }

    /**
     * file upload ingprogress method
     *
     * @param value input value(value)
     * @param msgId input value(msgId)
     */
    public void setfileuploadingprogress(int value, String msgId) {
        SharedPreferences.Editor badgeEditor = badgePref.edit();
        badgeEditor.putInt(msgId, value);
        badgeEditor.apply();
    }

    /**
     * getting file upload ingprogress
     *
     * @param msgId based on msgid to calculate the time
     * @return response value
     */
    public int getfileuploadingprogress(String msgId) {
        int time = 0;
        try {
            time = badgePref.getInt(msgId, 0);
        } catch (Exception e) {

        }

        return time;
    }

    /**
     * remove Message Count
     *
     * @param countKey updated message count based on countkey single / group chat
     */
    public void removeMessageCount(String countKey) {
        int totCount = getTotalCount();
        int singleDocIdCount = getSingleBadgeCount(countKey);
        totCount = totCount - singleDocIdCount;

        String keyAllMsgCount;
        int allMsgCount;
        if (countKey.contains("-g")) {
            keyAllMsgCount = KEY_GROUP_MSG_COUNT;
            allMsgCount = getAllGroupMsgCount() - singleDocIdCount;
        } else {
            keyAllMsgCount = KEY_SINGLE_MSG_COUNT;
            allMsgCount = getAllSingleMsgCount() - singleDocIdCount;
        }

        SharedPreferences.Editor badgeEditor = badgePref.edit();
        badgeEditor.putInt(countKey, 0);
        badgeEditor.putInt(keyAllMsgCount, allMsgCount);
        badgeEditor.putInt(KEY_BADGE_TOTAL_COUNT, totCount);
        badgeEditor.apply();
    }


    /**
     * remove Call Count
     */
    public void removeCallCount() {
        SharedPreferences.Editor badgeEditor = callPref.edit();
        badgeEditor.clear();
        badgeEditor.apply();
    }

    /**
     * get preference Total Count
     *
     * @return response value
     */
    public int getTotalCount() {
        int totalCount = badgePref.getInt(KEY_BADGE_TOTAL_COUNT, 0);
        return totalCount;
    }

    /**
     * get call preference total count
     *
     * @return response value
     */
    public int getTotalCallCount() {
        int totalCount = callPref.getInt(KEY_BADGE_TOTAL_COUNT, 0);
        return totalCount;
    }


    /**
     * get Single BadgeCount
     *
     * @param countKey based on key get count value
     * @return response value
     */
    public int getSingleBadgeCount(String countKey) {
        int count = badgePref.getInt(countKey, 0);
        return count;
    }

    /**
     * get Single Call Badge Count
     *
     * @param countKey based on key get count value
     * @return response value
     */
    public int getSingleCallBadgeCount(String countKey) {
        int count = callPref.getInt(countKey, 0);
        return count;
    }

    /**
     * get All Group Message Count
     *
     * @return response value
     */
    public int getAllGroupMsgCount() {
        int count = badgePref.getInt(KEY_GROUP_MSG_COUNT, 0);
        return count;
    }

    /**
     * get All Single Message Count
     *
     * @return response value
     */
    public int getAllSingleMsgCount() {
        int count = badgePref.getInt(KEY_SINGLE_MSG_COUNT, 0);
        return count;
    }

    /**
     * Clear badge count for SharedPreferences value
     */
    public void clearBadgeCount() {
        SharedPreferences.Editor badgeEditor = badgePref.edit();
        badgeEditor.clear();
        badgeEditor.apply();
    }

    /**
     * Add shortCut
     *
     * @param context        current activity
     * @param isGroupChat    input value(isGroupChat)
     * @param receiverId     input value(receiverId)
     * @param receiverName   input value(receiverName)
     * @param receiverAvatar input value(receiverAvatar)
     * @param receiverMsisdn input value(receiverMsisdn)
     * @param avatarBmp      input value(avatarBmp)
     */
    public static void addChatShortcut(Context context, boolean isGroupChat, String receiverId, String receiverName,
                                       String receiverAvatar, String receiverMsisdn, Bitmap avatarBmp) {
        Intent shortcutIntent = new Intent(context, ChatViewActivity.class);

        shortcutIntent.putExtra("backfrom", true);
        shortcutIntent.putExtra("receiverUid", "");
        shortcutIntent.putExtra("receiverName", "");
        shortcutIntent.putExtra("documentId", receiverId);
        shortcutIntent.putExtra("Username", receiverName);
        shortcutIntent.putExtra("Image", receiverAvatar);
        shortcutIntent.putExtra("msisdn", receiverMsisdn);
        shortcutIntent.setAction(Intent.ACTION_MAIN);

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, receiverName);

        if (receiverAvatar != null && !receiverAvatar.equals("")) {
            if (avatarBmp != null) {
                Bitmap bitmap = Bitmap.createScaledBitmap(avatarBmp, 128, 128, true);
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON, ChatappImageUtils.getCroppedBitmap(bitmap));
            } else {
                if (!isGroupChat) {
                    addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                            Intent.ShortcutIconResource.fromContext(context,
                                    R.mipmap.chat_attachment_profile_default_image_frame));
                } else {
                    addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                            Intent.ShortcutIconResource.fromContext(context,
                                    R.mipmap.group_chat_attachment_profile_icon));

                }
            }
        } else {
            if (!isGroupChat) {
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                        Intent.ShortcutIconResource.fromContext(context,
                                R.mipmap.chat_attachment_profile_default_image_frame));
            } else {
                addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                        Intent.ShortcutIconResource.fromContext(context,
                                R.mipmap.group_chat_attachment_profile_icon));

            }
        }
        addIntent.putExtra("duplicate", false);

        addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);

        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
        context.sendBroadcast(addIntent);

        Toast.makeText(context, "Shortcut created", Toast.LENGTH_SHORT).show();
    }

    /**
     * set Contact Last Refresh Time
     * @param value response value
     */
    public void setContactLastRefreshTime(long value) {
        Log.d(TAG, ">>>setContactLastRefreshTime: " + value);
        SharedPreferences.Editor badgeEditor = badgePref.edit();
        badgeEditor.putLong(contactRefresh_time, value);
        badgeEditor.apply();
    }

    /**
     * set first time contact Sync Completed
     * @param value response value
     */
    public void setfirsttimecontactSyncCompleted(boolean value) {
        SharedPreferences.Editor badgeEditor = badgePref.edit();
        badgeEditor.putBoolean(firsttimeSyncCompleted, value);
        badgeEditor.apply();
    }

    /**
     * set first time loading newlogin
     * @param value response value
     */
    public void setfirsttimeloading_newlogin(boolean value) {
        SharedPreferences.Editor badgeEditor = badgePref.edit();
        badgeEditor.putBoolean(firsttimetimeloading, value);
        badgeEditor.apply();
    }

    public long getlastContact_refreshTime() {
        long time = badgePref.getLong(contactRefresh_time, 0);
        Log.d(TAG, "getlastContact_refreshTime: " + time);
        return time;
    }


    public boolean getfirstTimecontactSyncCompleted() {
        boolean time = badgePref.getBoolean(firsttimeSyncCompleted, false);
        return time;
    }


    public boolean getfirsttimeloading_newlogin() {
        boolean time = badgePref.getBoolean(firsttimetimeloading, false);
        return time;
    }

}
