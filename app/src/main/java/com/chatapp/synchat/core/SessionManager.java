package com.chatapp.synchat.core;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.chatapp.synchat.app.ReLoadingActivityNew;
import com.chatapp.synchat.core.socket.MessageService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import me.leolin.shortcutbadger.ShortcutBadgeException;
import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Administrator on 10/6/2016.
 */
public class SessionManager {

    private ActiveSessionDispatcher dispatcher;
    private static SessionManager instance;
    private static Context context;
    private static SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private static String SESSION_STORAGE_ID = "APP_STORAGE";
    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String KEY_PHONE_NO = "ph_number_id";
    private static final String KEY_USERNAME = "username_id";
    private static final String KEY_LOGIN_KEY = "LoginKey";
    private static final String KEY_IS_LOGIN_KEY_SENT = "IsLoginKeySent";
    private static final String KEY_IS_VALID_DEVICE = "IsValidDevice";
    private static final String KEY_IS_SCREEN_ACTIVATED = "IsScreenActivated";
    private static final String KEY_IS_INITIAL_GET_GROUP_LIST = "IsInitialGetGroupList";
    private static final String KEY_SERVER_TS = "ServerTS";
    private static final String KEY_SERVER_TIME_DIFFERENCE = "ServerTimeDiff";
    private static final String KEY_EMAIL_CHATLOCK = "chatLockEmail";
    private static final String KEY_REC_EMAIL_CHATLOCK = "chatLockRecEmail";
    private static final String KEY_REC_PHONE_CHATLOCK = "chatLockPhoneRecovery";
    private static final String KEY_CHAT_LOCK_EMAIL_VERIFIED = "ChatLockEmailVerified";
    private static final String KEY_IS_CHAT_LOCKED = "IsChatLocked";
    private static final String KEY_RATING = "rating";
    private static final String KEY_WElCOME = "welcome";
    private static final String KEY_TIME = "tsNextLine";
    private static final String KEY_COUNTRY_CODE = "CountryCode";
    private static final String KEY_MOBILE_NO = "MobileNo";
    private static final String KEY_LOGIN_COUNT = "LoginCount";
    private static final String KEY_IS_APP_SETTINGS_RECEIVED = "IsAppSettingsReceived";
    private static final String KEY_IS_USER_DETAILS_RECEIVED = "IsUserDetailsReceived";
    private static final String KEY_PRIVACY_LAST_SEEN = "PrivacyLastSeen";
    private static final String KEY_PRIVACY_PROFILE_PIC = "PrivacyProfilePic";
    private static final String KEY_PRIVACY_PROFILE_STATUS = "PrivacyProfileStatus";
    private static final String KEY_READ_RECEIPT = "ReadReceipt";
    public static final String TWILIO_DEV_MODE = "development";
    public static final String AUTOSTART_MODE = "autostart";


    public static final String KEY_PREV_LOGIN_USER_ID = "PrevLoginId";

    public static final String KEY_FOLLOWING_IDS = "following_ids";
    public static final String TOKEN = "token_";
    public static final String TOKEN_HASH = "token_hash";

    public static final String KEY_CELEBRITY_CHECK="0";
    public static final String KEY_CELEBRITY_NAME="celebrityname";

    private static final String MPIN = "mupin";

    private SessionManager() {
        dispatcher = new ActiveSessionDispatcher();
        pref = context.getSharedPreferences(SESSION_STORAGE_ID, Context.MODE_PRIVATE);
        editor = pref.edit();

    }

    public class ActiveSessionDispatcher {
        private BlockingQueue<Runnable> dispatchQueue
                = new LinkedBlockingQueue<Runnable>();
        private Thread mThread;

        public ActiveSessionDispatcher() {
            mThread = new Thread(dispatchRunnable);
            mThread.start();
        }

        public Runnable dispatchRunnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        dispatchQueue.take().run();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        private void addWork(Runnable work) {
            try {
                dispatchQueue.put(work);
            } catch (Exception e) {
            }
        }
    }

    public static SessionManager getInstance(Context context) {
        SessionManager.context = context;
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public boolean isLoginKeySent() {
        return pref.getBoolean(KEY_IS_LOGIN_KEY_SENT, false);
    }

    public void setLoginKeySent(final boolean isSent) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean(KEY_IS_LOGIN_KEY_SENT, isSent);
                editor.apply();
            }
        });
    }

    public void setLoginKey(final String loginKey) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_LOGIN_KEY, loginKey);
                editor.apply();
            }
        });
    }

    public boolean isratingenabled() {
        return pref.getBoolean(KEY_RATING, false);
    }

    public void setrating(final boolean setrating) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean(KEY_RATING, setrating);
                editor.apply();
            }
        });
    }

    public boolean iswelcomeenabled() {
        return pref.getBoolean(KEY_WElCOME, false);
    }

    public void setwelcomeemabled(final boolean setrating) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean(KEY_WElCOME, setrating);
                editor.apply();
            }
        });
    }

    public String getLoginKey() {
        String loginKey = pref.getString(KEY_LOGIN_KEY, "");

        if (loginKey.equals("")) {
            Calendar calendar = Calendar.getInstance();
            loginKey = String.valueOf(calendar.getTimeInMillis());
            editor.putString(KEY_LOGIN_KEY, loginKey);
            editor.apply();
        }

        return loginKey;
    }

    public boolean isValidDevice() {
        return pref.getBoolean(KEY_IS_VALID_DEVICE, true);
    }

    public void setIsValidDevice(final boolean isValidDevice) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean(KEY_IS_VALID_DEVICE, isValidDevice);
                editor.apply();
            }
        });
    }

    public boolean isInitialGetGroupList() {
        return pref.getBoolean(KEY_IS_INITIAL_GET_GROUP_LIST, true);
    }

    public void setIsInitialGetGroupList() {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean(KEY_IS_INITIAL_GET_GROUP_LIST, false);
                editor.apply();
            }
        });
    }

    public boolean isScreenActivated() {
        return pref.getBoolean(KEY_IS_SCREEN_ACTIVATED, false);
    }

    public void setIsScreenActivated(final boolean isActive) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean(KEY_IS_SCREEN_ACTIVATED, isActive);
                editor.apply();
            }
        });
    }

    public void logoutUser(boolean keepPrevLoginId) {
        // Clearing all data from Shared Preferences
        String prevLoginId = getCurrentUserID();

        editor.clear();
        editor.commit();

        if (keepPrevLoginId) {
            editor.putString(KEY_PREV_LOGIN_USER_ID, prevLoginId);
        }

        ShortcutBadgeManager shortcutBadgeManager = new ShortcutBadgeManager(context);
        shortcutBadgeManager.clearBadgeCount();

        try {
            ShortcutBadger.applyCountOrThrow(context, 0);
        } catch (ShortcutBadgeException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent svcIntent = new Intent(context, MessageService.class);
        context.stopService(svcIntent);

        // After logout redirect user to Loing Activity
        Intent homeIntent = new Intent(context, ReLoadingActivityNew.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        // Staring Login Activity
        context.startActivity(homeIntent);

    }

    public String getPrevLoginUserId() {
        return pref.getString(KEY_PREV_LOGIN_USER_ID, "");
    }

    public void setServerTimeDifference(final long serverTS, final Long timeDiff) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putLong(KEY_SERVER_TS, serverTS);
                editor.putLong(KEY_SERVER_TIME_DIFFERENCE, timeDiff);
                editor.apply();
            }
        });
    }

    public Long getServerTS() {
        return pref.getLong(KEY_SERVER_TS, 0);
    }

    public Long getServerTimeDifference() {
        return pref.getLong(KEY_SERVER_TIME_DIFFERENCE, 0);
    }

    public void setIsAppSettingsReceived(final Boolean isAppSettingsReceived) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean(KEY_IS_APP_SETTINGS_RECEIVED, isAppSettingsReceived);
                editor.apply();
            }
        });
    }

    public boolean isAppSettingsReceived() {
        return pref.getBoolean(KEY_IS_APP_SETTINGS_RECEIVED, false);
    }

    public void setUserDetailsReceived(final Boolean isUserDetailsReceived) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean(KEY_IS_USER_DETAILS_RECEIVED, isUserDetailsReceived);
                editor.apply();
            }
        });
    }

    public boolean isUserDetailsReceived() {
        return pref.getBoolean(KEY_IS_USER_DETAILS_RECEIVED, false);
    }

    public void setProfilePicVisibleTo(final String visibleTo) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_PRIVACY_PROFILE_PIC, visibleTo);
                editor.apply();
            }
        });
    }

    public String getProfilePicVisibleTo() {
        return pref.getString(KEY_PRIVACY_PROFILE_PIC, "everyone");
    }

    public void setProfileStatusVisibleTo(final String visibleTo) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_PRIVACY_PROFILE_STATUS, visibleTo);
                editor.apply();
            }
        });
    }

    public String getProfileStatusVisibleTo() {
        return pref.getString(KEY_PRIVACY_PROFILE_STATUS, "everyone");
    }

    public void setLastSeenVisibleTo(final String visibleTo) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_PRIVACY_LAST_SEEN, visibleTo);
                editor.apply();
            }
        });
    }

    public String getLastSeenVisibleTo() {
        return pref.getString(KEY_PRIVACY_LAST_SEEN, "everyone");
    }

    public void setSendReadReceipt(final Boolean canSend) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean(KEY_READ_RECEIPT, canSend);
                editor.apply();
            }
        });
    }

    public Boolean canSendReadReceipt() {
        return pref.getBoolean(KEY_READ_RECEIPT, true);
    }

    public void setLogIn(final boolean state) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean(IS_LOGIN, state);
                editor.commit();
            }
        });
    }

    public boolean isLogin() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void clearSharedPrefs() {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.clear();
                editor.commit();
            }
        });
    }

    //check whether user logged in first tsNextLine

    /***************************************************/


    public void setLoggedInFirstTime(final String code) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("DEVICE_ID", code);
                editor.commit();
            }
        });
    }

    public String getIsloggeggInfirsttime() {
        return pref.getString("DEVICE_ID", "");
    }


    /******************************************/


    public void setUniqueIdForserverContactUpdate(final String ContactUpdateId) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("CONTACT_UPDATE_ID", ContactUpdateId);
                editor.commit();
            }
        });

    }

    public String getUniqueIdForSERVERCONTACTUPDATE() {
        return pref.getString("CONTACT_UPDATE_ID", "");
    }

    public void setUserSecurityToken(String token) {
        editor.putString(TOKEN, token);
        editor.apply();
    }

    public void setUserSecurityTokenHash(String tokenHash) {
        editor.putString(TOKEN_HASH, tokenHash);
        editor.apply();
    }

    public String getSecurityToken() {
        return pref.getString(TOKEN, "");
    }

    public String getSecurityTokenHash() {
        return pref.getString(TOKEN_HASH, "");
    }


    /*************************************/

    //Saving the phone number of the current user

    /******************************************/


    public void setPhoneNumberOfCurrentUser(final String ContactCurrentUser) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_PHONE_NO, ContactCurrentUser);
                editor.commit();
            }
        });
    }

    public String getPhoneNumberOfCurrentUser() {
        return pref.getString(KEY_PHONE_NO, "");
    }


    public void setUsermpin(final String mobileNo) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(MPIN, mobileNo);
                editor.commit();
            }
        });

    }

    public String getUserMpin() {
        return pref.getString(MPIN, "");
    }

    public void setLastCallType(final String LastCallType) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("LastCallType", LastCallType);
                editor.commit();
            }
        });
    }

    public String getLastCallType() {
        return pref.getString("LastCallType", "");
    }

    public void setLastCallAvatar(final String LastCallAvatar) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("LastCallAvatar", LastCallAvatar);
                editor.commit();
            }
        });
    }

    public String getLastCallAvatar() {
        return pref.getString("LastCallAvatar", "");
    }


    public void setCountryCodeOfCurrentUser(final String CountryCode) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_COUNTRY_CODE, CountryCode);
                editor.commit();
            }
        });
    }

    public String getCountryCodeOfCurrentUser() {
        return pref.getString(KEY_COUNTRY_CODE, "");
    }


    /*************************************/
    //saving the current User name

    /******************************************/

    public void setUserCountryCode(final String countryCode) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_COUNTRY_CODE, countryCode);
                editor.commit();
            }
        });

    }

    public String getUserCountryCode() {
        return pref.getString(KEY_COUNTRY_CODE, "");
    }


    public void setLoginType(final int loginType) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putInt("loginType", loginType);
                editor.commit();
            }
        });

    }

    public int getLoginType() {
        return pref.getInt("loginType", 0);
    }


    public void setUserMobileNoWithoutCountryCode(final String mobileNo) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_MOBILE_NO, mobileNo);
                editor.commit();
            }
        });

    }

    public String getUserMobileNoWithoutCountryCode() {
        return pref.getString(KEY_MOBILE_NO, "");
    }

    public void setLoginCount(final String loginCount) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_LOGIN_COUNT, loginCount);
                editor.commit();
            }
        });

    }

    public String getLoginCount() {
        return pref.getString(KEY_LOGIN_COUNT, "");
    }

    public void setnameOfCurrentUser(final String ContactCurrentUser) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_USERNAME, ContactCurrentUser);
                editor.commit();
            }
        });

    }

    public String getnameOfCurrentUser() {
        return pref.getString(KEY_USERNAME, "");
    }

    /*************************************/

    public void setUserProfilePic(final String url) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("PROFILE_PIC", url);
                editor.commit();
            }
        });
    }

    public String getUserProfilePic() {
        return pref.getString("PROFILE_PIC", "");
    }

    public void setCurrentUserID(final String code) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("_ID", code);
                editor.commit();
            }
        });
    }

    public String getCurrentUserID() {
        return pref.getString("_ID", "");

    }


    public void setCurrentUserEmailID(final String emailID) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("emailID", emailID);
                editor.commit();
            }
        });
    }

    public String getCurrentUserEmailID() {
        return pref.getString("emailID", "");

    }


    public void setLoginSuccessStatus(final String code) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("LoginStatus", code);
                editor.commit();
            }
        });
    }

    public String getLoginSuccessStatus() {
        return pref.getString("LoginStatus", "");

    }

    public void setApplicationPauseState(final String code) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("ApplicationPauseState", code);
                editor.commit();
            }
        });
    }

    public String getApplicationPauseState() {
        return pref.getString("ApplicationPauseState", "");

    }


    public void setAdminPendingStatus(final String code) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("AdminPendingStatus", code);
                editor.commit();
            }
        });
    }

    public String getAdminPendingStatus() {
        return pref.getString("AdminPendingStatus", "");

    }


    public void setResetPinStatus(final int code) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putInt("ResetPInStatus", code);
                editor.commit();
            }
        });
    }

    public int getResetPinStatus() {
        return pref.getInt("ResetPInStatus", 0);

    }


    public void setClearLocalDataStatus1(final String code) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("_LocalIData1", code);
                editor.commit();
            }
        });
    }

    public String getClearLocalDataStatus1() {
        return pref.getString("_LocalIData1", "");

    }


    public void setUpdaterStatus(final String updaterStatus) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("UpdaterStatus", updaterStatus);
                editor.commit();
            }
        });
    }

    public String getUpdaterStatus() {
        return pref.getString("UpdaterStatus", "");

    }



    public void setcurrentUserstatus(final String code) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("Ststus", code);
                editor.commit();
            }
        });
    }

    public String getcurrentUserstatus() {
        return pref.getString("Ststus", "");

    }

    public void setAdfsReturnUrl(final String url) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("AdfsReturnUrl", url);
                editor.commit();
            }
        });
    }

    public String getAdfsReturnUrl() {
        return pref.getString("AdfsReturnUrl", "");

    }

    public void setLoginFailedStatus(final String status) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("LoginFailedStatus", status);
                editor.commit();
            }
        });
    }

    public String getLoginFailedStatus() {
        return pref.getString("LoginFailedStatus", "");

    }


    public void Islogedin(final Boolean staus) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean("loginStatus", staus);
                editor.commit();
            }
        });
    }

    public Boolean getlogin() {
        return pref.getBoolean("loginStatus", false);
    }


    public void IsprofileUpdate(final Boolean profileUpdate) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean("profileStatus", profileUpdate);
                editor.commit();
            }
        });
    }

    public Boolean getIsprofileUpdate() {
        return pref.getBoolean("profileStatus", false);
    }


    public void IsnumberVerified(final Boolean numberVerified) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean("numberVerified", numberVerified);
                editor.commit();
            }
        });
    }

    public Boolean getnumberVerified() {
        return pref.getBoolean("numberVerified", false);
    }

    public void IsBackupRestored(final Boolean isRestored) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean("backupRestoredStatus", isRestored);
                editor.commit();
            }
        });
    }

    public Boolean getBackupRestored() {
        return pref.getBoolean("backupRestoredStatus", false);
    }

    public void setChatdoc(final String chatdoc) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("chatdoc", chatdoc);
                editor.commit();
            }
        });

    }

    public String getChatdoc() {
        return pref.getString("chatdoc", "");
    }


    public void IsapplicationisKilled(final Boolean appiskilled) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean("appiskilled", appiskilled);
                editor.commit();
            }
        });

    }

    public Boolean getapplicationisKilled() {
        return pref.getBoolean("appiskilled", false);
    }


    public void setPushdisplay(final Boolean Pushdisplay) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean("Pushdisplay", Pushdisplay);
                editor.commit();
            }
        });
    }

    public Boolean getPushdisplay() {
        return pref.getBoolean("Pushdisplay", true);
    }


    public void setlastgroupcreated(final String lastgroupcreated) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("lastgroupcreated", lastgroupcreated);
                editor.commit();
            }
        });
    }

    public String getlastgroupcreated() {
        return pref.getString("lastgroupcreated", "");
    }

    public boolean isFirstMessage(String key) {
        return pref.getBoolean(key, false);
    }

    public void setIsFirstMessage(String userId, boolean isFirstMessage) {
        String key = userId + "firstmsg";
        editor.putBoolean(key, isFirstMessage);
        editor.apply();
    }


    public void setUserEmailId(final String email) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_EMAIL_CHATLOCK, email);
                editor.commit();
            }
        });

    }

    public String getUserEmailId() {
        return pref.getString(KEY_EMAIL_CHATLOCK, "");
    }


    public void setRecoveryEMailId(final String mailId) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_REC_EMAIL_CHATLOCK, mailId);
                editor.commit();
            }
        });

    }

    public String getRecoveryEMailId() {
        return pref.getString(KEY_REC_EMAIL_CHATLOCK, "");
    }


    public void setRecoveryPhoneNo(final String phoneNo) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_REC_PHONE_CHATLOCK, phoneNo);
                editor.commit();
            }
        });

    }

    public String getRecoveryPhoneNo() {
        return pref.getString(KEY_REC_PHONE_CHATLOCK, "");
    }

    public void setChatLockEmailIdVerifyStatus(final String verifyStatus) {

        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_CHAT_LOCK_EMAIL_VERIFIED, verifyStatus);
                editor.commit();
            }
        });

    }

    public String getChatLockEmailIdVerifyStatus() {
        return pref.getString(KEY_CHAT_LOCK_EMAIL_VERIFIED, "");
    }

    public void setBackUpSize(final Long size) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putLong("BackUpSize", size);
                editor.apply();
            }
        });
    }

    public long getBackUpSize() {
        return pref.getLong("BackUpSize", 0);
    }

    public void setBackUpTS(final long timeStamp) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putLong("BackUpTS", timeStamp);
                editor.apply();
            }
        });
    }

    public long getBackUpTS() {
        return pref.getLong("BackUpTS", 0);
    }

    public void setBackUpMailAccount(final String mailId) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("BackUpMailId", mailId);
                editor.apply();
            }
        });
    }

    public String getBackMailAccount() {
        return pref.getString("BackUpMailId", "");
    }


    /*public void setFavouriteContact(final String favouriteContactUserId, final int favType) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("favouriteContactUserId", String.valueOf(favType));
                editor.apply();
            }
        });
    }

    public int getFavouriteContact(final String favouriteContactUserId) {
        return pref.getInt(favouriteContactUserId, 0);
    }*/


    public void setBackUpOver(final String backUpOver) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("BackUpOver", backUpOver);
                editor.apply();
            }
        });
    }

    public String getBackUpOver() {
        return pref.getString("BackUpOver", "");
    }

    public void setBackUpDuration(final String backUpDuration) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("BackUpDuration", backUpDuration);
                editor.apply();
            }
        });
    }

    public String getBackUpDuration() {
        return pref.getString("BackUpDuration", "");
    }

    public void setBackUpDriveFileName(final String fileName) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("BackUpDriveFileName", fileName);
                editor.apply();
            }
        });
    }

    public String getBackUpDriveFileName() {
        return pref.getString("BackUpDriveFileName", "");
    }

    public void setBackUpDriveFileId(final String fileName) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("BackUpDriveFileId", fileName);
                editor.apply();
            }
        });
    }

    public String getBackUpDriveFileId() {
        return pref.getString("BackUpDriveFileId", "");
    }

    public void setBackUpServiceStartedAt() {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putLong("BackUpSvcStartedAt", Calendar.getInstance().getTimeInMillis());
                editor.apply();
            }
        });
    }

    public long getBackUpServiceStartedAt() {
        return pref.getLong("BackUpSvcStartedAt", 0);
    }

    public void puttime(final String time) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(KEY_TIME, time);
                editor.commit();
            }
        });

    }

    public String gettime() {
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date cDate = new Date();
        String reportDate = df.format(cDate);
        String time = pref.getString(KEY_TIME, reportDate);
        String prefs = time;
        return prefs;

    }

    public void setTwilioMode(final String mode) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("TwilioMode", mode);
                editor.apply();
            }
        });
    }

    public String getTwilioMode() {
        return pref.getString("TwilioMode", "");
    }

    public void setSMSVerifyEnabled(final String status) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("SMSVerifyEnabled", status);
                editor.apply();
            }
        });
    }

    public String getSMSVerifyEnabled() {
        return pref.getString("SMSVerifyEnabled", "");
    }

    public void setEmailVerifyEnabled(final String status) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("EmailVerifyEnabled", status);
                editor.apply();
            }
        });
    }

    public String getEmailVerifyEnabled() {
        return pref.getString("EmailVerifyEnabled", "");
    }

    public void setEmailLinkVerifyEnabled(final String status) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("EmailLinkVerifyEnabled", status);
                editor.apply();
            }
        });
    }

    public String getEmailLinkVerifyEnabled() {
        return pref.getString("EmailLinkVerifyEnabled", "");
    }

    public void setIsEmailLinkVerified(final boolean isEmailLinkVerified) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putBoolean("IsEmailLinkVerified", isEmailLinkVerified);
                editor.apply();
            }
        });
    }

    public boolean isEmailLinkVerified() {
        return pref.getBoolean("IsEmailLinkVerified", false);
    }

    public void setLoginOTP(final String otp) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("LoginOTP", otp);
                editor.apply();
            }
        });
    }

    public String getLoginOTP() {
        return pref.getString("LoginOTP", "");
    }

    public void setLoginEmailOTP(final String emailOtp) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("EmailLoginOTP", emailOtp);
                editor.apply();
            }
        });
    }

    public String getLoginEmailOTP() {
        return pref.getString("EmailLoginOTP", "");
    }



    public void setTermsUrl(final String termsUrl) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("termsUrl", termsUrl);
                editor.apply();
            }
        });
    }

    public String getTermsUrl() {
        return pref.getString("termsUrl", "");
    }


    public void setADFSUrl(final String adfsUrl) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("adfsUrl", adfsUrl);
                editor.apply();
            }
        });
    }

    public String getADFSUrl() {
        return pref.getString("adfsUrl", "");
    }

    public void setCopyRights(final String copyRights) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("copyRights", copyRights);
                editor.apply();
            }
        });
    }

    public String getCopyRights() {
        return pref.getString("copyRights", "");
    }


    public void setAutoDeleteTime(final String autoDeleteTime) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("autoDeleteTime", autoDeleteTime);
                editor.apply();
            }
        });
    }

    public String getAutoDeleteTime() {
        return pref.getString("autoDeleteTime", "86400");
    }


    public void setContactUsEMailId(final String emailId) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("ContactMailId", emailId);
                editor.apply();
            }
        });
    }

    public String getContactUsEMailId() {
        return pref.getString("ContactMailId", "");
    }

    public void setChatUpdaterDownload(final String aChatUpdaterDownload) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("aChatUpdater", aChatUpdaterDownload);
                editor.apply();
            }
        });
    }

    public String getChatUpdaterDownload() {
        return pref.getString("aChatUpdater", "");
    }

    public void setSecretChatEnabled(final String secretChatEnabled) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("SecretChatEnabled", secretChatEnabled);
                editor.apply();
            }
        });
    }

    public String getSecretChatEnabled() {
        return pref.getString("SecretChatEnabled", "");
    }

    public void setLockChatEnabled(final String lockChatEnabled) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("LockChatEnabled", lockChatEnabled);
                editor.apply();
            }
        });
    }

    public String getLockChatEnabled() {
        return pref.getString("LockChatEnabled", "");
    }

    public void setSingleChatCount(final String chatKey, final int chatCount) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putInt(chatKey, chatCount);
                editor.apply();
            }
        });
    }

    public int getSingleChatCount(final String chatKey) {
        return pref.getInt(chatKey, 0);
    }


    public void setGroupChatCount(final String chatKey, final int chatCount) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putInt(chatKey, chatCount);
                editor.apply();
            }
        });
    }

    public int getGroupChatCount(final String chatKey) {
        return pref.getInt(chatKey, 0);
    }

    public void setSocketDisconnectedTS(final long ts) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putLong("SocketDisconnectTs", ts);
                editor.apply();
            }
        });
    }

    public long getSocketDisconnectedTS() {
        return pref.getLong("SocketDisconnectTs", 0);
    }

    public void setOTPCountDownTime() {
        editor.putLong("OTPCountTimer", Calendar.getInstance().getTimeInMillis());
        editor.apply();
    }

    public long getOTPCountDownTime() {
        return pref.getLong("OTPCountTimer", Calendar.getInstance().getTimeInMillis());
    }

    public void setFileUploadMaxSize(String size) {
        editor.putString("UploadFileMaxSize", size);
        editor.apply();
    }

    public String getFileUploadMaxSize() {
        return pref.getString("UploadFileMaxSize", "5");
    }

    public void setFileUploadMaxCount(String count) {
        editor.putString("UploadFileMaxCount", count);
        editor.apply();
    }

    public String getFileUploadMaxCount() {
        return pref.getString("UploadFileMaxCount", "9");
    }

    public void setIsAppShortcutCreated() {
        editor.putBoolean("AppShortcutAdded", true);
        editor.apply();
    }

    public boolean isAppShortcutCreated() {
        return pref.getBoolean("AppShortcutAdded", false);
    }

    public void setContactSyncFinished() {
        editor.putBoolean("ContactSyncFinish", true);
        editor.apply();
    }

    public boolean isContactSyncFinished() {
        return pref.getBoolean("ContactSyncFinish", false);
    }

    public void setContactSavedRevision(long savedRevision) {
        editor.putLong("ContactSavedRevision", savedRevision);
        editor.apply();
    }

    public long getContactSavedRevision() {
        return pref.getLong("ContactSavedRevision", 0);
    }

    public void setAccountSyncCompletedTS(long timeStamp) {
        editor.putLong("AccountSyncCompleted", timeStamp);
        editor.apply();
    }

    public long getAccountSyncCompletedTS() {
        return pref.getLong("AccountSyncCompleted", 0);
    }

    public void setAccountSyncStartTS(long timeStamp) {
        editor.putLong("AppAccountSyncStartTS", timeStamp);
        editor.apply();
    }

    public long getAccountSyncStartTS() {
        return pref.getLong("AppAccountSyncStartTS", 0);
    }


    public void setCouchbaseDBChanged() {
        editor.putBoolean("CouchbaseCompleted", true);
        editor.apply();
    }

    public boolean isCouchbaseDBChanged() {
        return pref.getBoolean("CouchbaseCompleted", false);
    }


    //---------------For Celebrity Functionality----------------

    public void setFollowingIds(final String code) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("following_ids", code);
                editor.commit();
            }
        });
    }

    public String getFollowingIds() {
        return pref.getString("following_ids", "");
    }

    //---------------------Celebrity Or Not Checking----------------------


    public void setcelebritystatus(final String code) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_CELEBRITY_CHECK, code);
                editor.apply();
            }
        });
    }

    public String getcelebritystatus() {
        return pref.getString(KEY_CELEBRITY_CHECK, "");
    }

    public void setcelebrityname(final String name) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString(KEY_CELEBRITY_NAME, name);
                editor.apply();
            }
        });
    }

    public String getcelebrityname() {
        return pref.getString(KEY_CELEBRITY_NAME, "");
    }

    public void setRootStatus(final String code) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("ROOT_STATUS", code);
                editor.commit();
            }
        });
    }

    public String getRootStatus() {
        return pref.getString("ROOT_STATUS", "");
    }

    public void setImeiOne(final String code) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("IMEI_ONE", code);
                editor.commit();
            }
        });
    }
    public void setDeviceId(final String code) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("MOBILE_DEVICE_ID", code);
                editor.commit();
            }
        });
    }
    public String getDeviceId() {
        return pref.getString("MOBILE_DEVICE_ID", "");
    }
    public String getImeiOne() {
        return pref.getString("IMEI_ONE", "");
    }

    public void setImeiTwo(final String code) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("IMEI_TWO", code);
                editor.commit();
            }
        });
    }

    public String getImeiTwo() {
        return pref.getString("IMEI_TWO", "");
    }

    public void setStaticMapUrl(final String code) {
        dispatcher.addWork(new Runnable() {
            @Override
            public void run() {
                editor.putString("STATIC_MAP", code);
                editor.commit();
            }
        });
    }

    public String getStaticMapUrl() {
        return pref.getString("STATIC_MAP", "");
    }

    public void clearData() {
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.apply();
    }
}

