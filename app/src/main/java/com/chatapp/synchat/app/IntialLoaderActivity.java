package com.chatapp.synchat.app;

import android.accounts.AccountAuthenticatorActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.chatapp.synchat.R;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.SessionManager;

import static android.Manifest.permission.RECORD_AUDIO;

/**
 * Created by Administrator on 10/7/2016.
 */
public class IntialLoaderActivity extends AccountAuthenticatorActivity {

    Handler mHandler = new Handler();


    private final int AUDIO_RECORD_PERMISSION_REQUEST_CODE = 14;
    IntialLoaderActivity context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loader);

        context=this;

        SharedPreferences preferences =getSharedPreferences("GroupInfoPref", Context.MODE_PRIVATE);


        requestAudioRecordPermission();
        loadContacts();
//
//        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
//            if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
//                    != PackageManager.PERMISSION_GRANTED) {
//                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, CONTACTS_REQUEST_CODE);
//            } else {
//                loadContacts();
//            }
//        } else {
//            loadContacts();
//        }
    }

    private void requestAudioRecordPermission() {
        ActivityCompat.requestPermissions(IntialLoaderActivity.this, new
                String[]{RECORD_AUDIO}, AUDIO_RECORD_PERMISSION_REQUEST_CODE);
    }

    public boolean checkAudioRecordPermission() {
       /* int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);*/
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return /*result == PackageManager.PERMISSION_GRANTED &&*/
                result1 == PackageManager.PERMISSION_GRANTED;
    }

//    private String getDeviceInfo() {
//        Locale current = getResources().getConfiguration().locale;
//        Log.d("Deviceinfo_country", current.getCountry());
//        Log.d("Deviceinfo_code", current.getLanguage());
//
//        TelephonyManager manager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
//        String carrierName = manager.getNetworkOperatorName();
//        Log.d("Deviceinfo_opera", carrierName+"-test");
//
//        /*Log.d("Deviceinfo_manu", DeviceInfo.getDeviceInfo(this, DeviceInfo.Device.DEVICE_MANUFACTURE));
//        Log.d("Deviceinfo_model", DeviceInfo.getDeviceInfo(this, DeviceInfo.Device.DEVICE_HARDWARE_MODEL));
//        Log.d("Deviceinfo_name", DeviceInfo.getDeviceInfo(this, DeviceInfo.Device.DEVICE_SYSTEM_NAME));
//        Log.d("Deviceinfo_version", DeviceInfo.getDeviceInfo(this, DeviceInfo.Device.DEVICE_SYSTEM_VERSION));
//        Log.d("Deviceinfo_version", DeviceInfo.getDeviceInfo(this, DeviceInfo.Device.));*/
//
//        for(DeviceInfo.Device data : DeviceInfo.Device.values()) {
//            Log.d("Deviceinfo_" + data, DeviceInfo.getDeviceInfo(this, data));
//        }
//
//        Log.d("StorageTT_interfree", FileUploadDownloadManager.getAvailableInternalMemorySize());
//        Log.d("StorageTT_intertot", FileUploadDownloadManager.getTotalInternalMemorySize());
//        Log.d("StorageTT_extfree", FileUploadDownloadManager.getAvailableExternalMemorySize());
//        Log.d("StorageTT_exttot", FileUploadDownloadManager.getTotalExternalMemorySize());
//
//        Log.d("DeviceBuildInfo_produ", Build.PRODUCT);
//        Log.d("DeviceBuildInfo_device", Build.DEVICE);
//        Log.d("DeviceBuildInfo_release", Build.VERSION.RELEASE);
//        Log.d("DeviceBuildInfo_board", Build.BOARD);
//        Log.d("DeviceBuildInfo_arch", System.getProperty("os.arch"));
//        Log.d("DeviceBuildInfo_kernal", System.getProperty("os.version"));
//
//        Log.d("RoamStauts", ""+About_contactus.isCallRoamActivated(this));
//
//        return "";
//    }

//    private void installAppShortcut() {
//        if(!SessionManager.getInstance(this).isAppShortcutCreated()) {
//            Intent shortcutIntent = new Intent(this, IntialLoaderActivity.class);
//
//            shortcutIntent.setAction(Intent.ACTION_MAIN);
//            shortcutIntent.putExtra("duplicate", false);
//            //shortcutIntent is added with addIntent
//            Intent addIntent = new Intent();
//            addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
//            addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, getString(R.string.app_name));
//            addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
//                    Intent.ShortcutIconResource.fromContext(this, R.drawable.chatapp_newlogo));
//
//            addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
//            sendBroadcast(addIntent);
//
//            addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
//            sendBroadcast(addIntent);
//            SessionManager.getInstance(this).setIsAppShortcutCreated();
//        }
//    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            SessionManager sessionManager = SessionManager.getInstance(IntialLoaderActivity.this);
            loadActivity(sessionManager);
        }
    };

    private void loadActivity(SessionManager mSessionManager) {
        if (!mSessionManager.getlogin()) {
            ActivityLauncher.launchVerifyPhoneScreen(this);
        } else if (!mSessionManager.getIsprofileUpdate()) {
            ActivityLauncher.launchProfileInfoScreen(this, null);
        }    else if(mSessionManager.getUserMpin()==null) {
            launch(0);

        }
        else
        {
            launch(1);
        }
    }

    public void launch(final int state)
    {

        Thread timer_thread = new Thread() {
            public void run() {
                try{
                    sleep(3000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    if(state==0)
                    {
                        ActivityLauncher.launchPinscreen(context,SessionManager.getInstance(context).getPhoneNumberOfCurrentUser());

                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();

                    }
                    else
                    {
                        ActivityLauncher.launchPinscreen(context,null);

                        overridePendingTransition(R.anim.enter, R.anim.exit);
                        finish();
                    }

                }
            }
        };
        timer_thread.start();


    }

    private void loadContacts() {
//        if(!ContactsSync.isStarted) {
//            Intent intent = new Intent(IntialLoaderActivity.this, ContactsSync.class);
//            startService(intent);
//        }

       /* LoadContacts contacts = new LoadContacts(this);
        contacts.execute();*/

        if (SessionManager.getInstance(this).getlogin()) {
            loadActivity(SessionManager.getInstance(this));
        } else {
            mHandler.postDelayed(runnable, 3000);
        }
    }

//    @RequiresApi(api = Build.VERSION_CODES.M)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
//
//        if (requestCode == CONTACTS_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            requestAudioRecordPermission();
//            loadContacts();
//        } else {
//
//        }
//
//    }

    @Override
    public void onBackPressed() {
    }

//    private void createChatappAccount() {
//        Account account = new Account(getString(R.string.app_name), getString(R.string.account_type));
//        AccountManager am = AccountManager.get(this);
//
//        if (am.addAccountExplicitly(account, null, null)) {
//            Bundle result = new Bundle();
//            result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
//            result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
//            setAccountAuthenticatorResult(result);
//
//            ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true);
//            ContentResolver.setMasterSyncAutomatically(true);
//        }
//    }

}