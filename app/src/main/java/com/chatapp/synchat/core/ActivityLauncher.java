package com.chatapp.synchat.core;

import android.app.Activity;
import android.content.Intent;

import com.chatapp.synchat.app.ADFSWebViewActivity;
import com.chatapp.synchat.app.ChangePinActivity;
import com.chatapp.synchat.app.AboutHelp;
import com.chatapp.synchat.app.About_contactus;
import com.chatapp.synchat.app.ChatappNewPageAbout;
import com.chatapp.synchat.app.Account_main_list;
import com.chatapp.synchat.app.ChangeNumber;
import com.chatapp.synchat.app.ChangeNumber_sub;
import com.chatapp.synchat.app.ChatHistory;
import com.chatapp.synchat.app.ChatSettings;
import com.chatapp.synchat.app.ChatViewActivity;
import com.chatapp.synchat.app.ChooseCountryScreen;
import com.chatapp.synchat.app.ContactSettings;
import com.chatapp.synchat.app.HomeScreen;
import com.chatapp.synchat.app.LogInActivity;
import com.chatapp.synchat.app.NotificationSettings;
import com.chatapp.synchat.app.ChatappProfileInfoScreen;
import com.chatapp.synchat.app.ChatappProfileScreen;
import com.chatapp.synchat.app.PinEnterActivity;
import com.chatapp.synchat.app.ReLoadingActivityNew;
import com.chatapp.synchat.app.RegisterActivity;
import com.chatapp.synchat.app.SettingContact;
import com.chatapp.synchat.app.ChatappSettings;
import com.chatapp.synchat.app.ChatappSmsVScreen;
import com.chatapp.synchat.app.Systemstatus;
import com.chatapp.synchat.app.UserProfile;
import com.chatapp.synchat.app.VerifyPhoneScreen;
import com.chatapp.synchat.app.WelcomeScreen;


/**
 * created by  Adhash Team on 10/5/2016.
 */
public class ActivityLauncher {

    /**
     * VerifyPhoneScreen
     *
     * @param context current activity
     */
    public static void launchVerifyPhoneScreen(Activity context) {
        Intent intent = new Intent(context, VerifyPhoneScreen.class);
        intent.putExtra("Text", true);
        context.startActivity(intent);
        context.finish();
    }

    /**
     * ReloadingActivityScreen
     *
     * @param context current activity
     */
    public static void launchReloadingActivityScreen(Activity context) {
        Intent intent = new Intent(context, ReLoadingActivityNew.class);
        context.startActivity(intent);
        context.finish();
    }

    /**
     * DownloadManagerScreen
     *
     * @param context   current activity
     * @param updateUrl pass the url data
     * @param updateMsg pass the message
     */
    public static void launchDownloadManagerScreen(Activity context, String updateUrl, String updateMsg) {
        Intent intent = new Intent(context, DownloadManagerActivity.class);
        intent.putExtra("update_url", updateUrl);
        intent.putExtra("update_msg", updateMsg);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.finish();
    }

    /**
     * RegisterScreen
     *
     * @param context current activity
     */
    public static void launchRegisterScreen(Activity context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.finish();
    }

    /**
     * ADFSScreen
     *
     * @param context current activity
     */
    public static void launchADFSScreen(Activity context) {
        Intent intent = new Intent(context, LogInActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.finish();
    }

    /**
     * ADFSScreen with pin status
     *
     * @param context        current activity
     * @param resetPinStatus reset pin data
     */
    public static void launchADFSScreen(Activity context, int resetPinStatus) {
        Intent intent = new Intent(context, ADFSWebViewActivity.class);
        intent.putExtra("reset_pin_status", resetPinStatus);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.finish();
    }

    public static void launchAccount(Activity context) {
        Intent intent = new Intent(context, Account_main_list.class);
        context.startActivity(intent);
//        context.finish();
        //  context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void launchChangenumber2(Activity context) {
        Intent intent = new Intent(context, ChangeNumber_sub.class);
        context.startActivity(intent);
//        context.finish();
        //  context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void launchChangenumber(Activity context) {
        Intent intent = new Intent(context, ChangeNumber.class);
        context.startActivity(intent);
//        context.finish();
        //  context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public static void launchChatSetting(Activity context) {
        Intent intent = new Intent(context, ChatSettings.class);
        context.startActivity(intent);
//        context.finish();
        //  context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * ChatHistory
     *
     * @param context current activity
     */
    public static void launchChatHistory(Activity context) {
        Intent intent = new Intent(context, ChatHistory.class);
        context.startActivity(intent);
//        context.finish();
    }

    public static void launchSystemstatus(Activity context) {
        Intent intent = new Intent(context, Systemstatus.class);
        context.startActivity(intent);
//        context.finish();
        //context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * About screen
     *
     * @param context current activity
     */
    public static void launchAboutnew(Activity context) {
        Intent intent = new Intent(context, ChatappNewPageAbout.class);
        context.startActivity(intent);
//        context.finish();
    }

    /**
     * SettingContactScreen
     *
     * @param context current activity
     */
    public static void launchSettingContactScreen(Activity context) {
        Intent intent = new Intent(context, SettingContact.class);
        context.startActivity(intent);
        // context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * Abouthelp
     *
     * @param context current activity
     */
    public static void launchAbouthelp(Activity context) {
        Intent intent = new Intent(context, AboutHelp.class);
        context.startActivity(intent);
        // context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * ChangePin
     *
     * @param context current activity
     */
    public static void launchChangePin(Activity context) {
        Intent intent = new Intent(context, ChangePinActivity.class);
        context.startActivity(intent);
        // context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * Profile Info Screen
     *
     * @param context current activity
     * @param msisdn  getting the value of msisdn
     */
    public static void launchProfileInfoScreen(Activity context, String msisdn) {
        Intent intent = new Intent(context, ChatappProfileInfoScreen.class);
        if (msisdn != null)
            intent.putExtra("msisdn", msisdn);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.finish();
    }


    /**
     * Pin screen
     *
     * @param context current activity
     * @param msisdn  getting the value of msisdn
     */
    public static void launchPinscreen(Activity context, String msisdn) {
        Intent intent = new Intent(context, PinEnterActivity.class);
        if (msisdn != null)
            intent.putExtra("msisdn", msisdn);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.finish();
    }

    /**
     * Pinscreen with reset number
     *
     * @param context        current activity
     * @param msisdn         getting the value of msisdn
     * @param resetPinStatus getting pin status
     */
    public static void launchPinscreen(Activity context, String msisdn, int resetPinStatus) {
        Intent intent = new Intent(context, PinEnterActivity.class);
        intent.putExtra("reset_pin_status", resetPinStatus);
        if (msisdn != null)
            intent.putExtra("msisdn", msisdn);
        if (resetPinStatus == 1) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        }

        context.startActivity(intent);
        context.finish();
    }

    public static void launchProfileScreen(Activity context) {
        Intent intent = new Intent(context, ChatappProfileScreen.class);
        context.startActivity(intent);
        // context.finish();
        // context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
        context.finish();
    }
    /*public static void launchWebViewScreen(Activity context) {
        Intent intent = new Intent(context, Webview.class);
        context.startActivity(intent);
    }*/

    /**
     * SettingScreen
     *
     * @param context current activity
     */
    public static void launchSettingScreen(Activity context) {
        Intent intent = new Intent(context, ChatappSettings.class);
        context.startActivity(intent);
        // context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * hHomeScreen
     *
     * @param context current activity
     */
    public static void launchHomeScreen(Activity context) {
        Intent intent = new Intent(context, HomeScreen.class);
        context.startActivity(intent);
        context.finish();
    }

    public static void launchChooseCountryScreen(Activity context, int requestCode) {
        Intent intent = new Intent(context, ChooseCountryScreen.class);
        context.startActivityForResult(intent, requestCode);
    }

    public static void launchWelcomeScreen(Activity context) {
        Intent intent = new Intent(context, WelcomeScreen.class);
        context.startActivity(intent);
        context.finish();
    }


    public static void launchChatViewScreen(Activity context) {
        Intent intent = new Intent(context, ChatViewActivity.class);
        context.startActivity(intent);
        context.finish();
    }

    public static void launchNotification(Activity context) {
        Intent intent = new Intent(context, NotificationSettings.class);
        context.startActivity(intent);
//        context.finish();
    }

    public static void launchContactSettings(Activity context) {
        Intent intent = new Intent(context, ContactSettings.class);
        context.startActivity(intent);
//        context.finish();
    }

    public static void launchChatBackup(Activity context) {

//        context.finish();
    }

    /**
     * UserProfile screen
     *
     * @param context current activity
     */
    public static void launchUserProfile(Activity context) {
        Intent intent = new Intent(context, UserProfile.class);
        context.startActivity(intent);
        //  context.overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }


    public static void launchSMSVerificationScreen(Activity context, String msisdn, String code, String phone, String otp) {
        Intent intent = new Intent(context,
                ChatappSmsVScreen.class);
        if (msisdn != null)
            intent.putExtra("msisdn", msisdn);
        if (code != null)
            intent.putExtra("code", "" + code);
        if (phone != null)
            intent.putExtra("Phone", phone);

        if (otp != null)
            intent.putExtra("otp", otp);

        if (context instanceof VerifyPhoneScreen) {
            intent.putExtra("FromVerifyPage", true);
        }
        context.startActivity(intent);
        context.finish();

    }

    /**
     * About & contactus screen
     *
     * @param context current activity
     */
    public static void launchAbout_contactus(AboutHelp context) {
        Intent intent = new Intent(context, About_contactus.class);
        context.startActivity(intent);
//        context.finish();
    }

}
