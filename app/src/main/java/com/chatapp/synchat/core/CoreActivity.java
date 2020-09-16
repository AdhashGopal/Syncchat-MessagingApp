package com.chatapp.synchat.core;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.chatapp.synchat.R;
import com.chatapp.synchat.core.message.ChangeSetController;
import com.chatapp.synchat.core.socket.MessageService;
import com.chatapp.synchat.core.uploadtoserver.FileUploadDownloadManager;


import java.util.List;

import static android.Manifest.permission.RECORD_AUDIO;

/**
 * * created by  Adhash Team on 10/5/2016.
 */
public class CoreActivity extends AppCompatActivity {
    private ProgressDialog progressDialog;

    private SessionManager sessionManager;
    private String mCurrentUserId;
    private boolean isValidDevice, isLoginKeySent;
    private Handler statusHandler;
    private Runnable statusRunnable;

    /**
     * To start SessionManager and get the value
     */
    @Override
    public void onStart() {
        super.onStart();
        sessionManager = SessionManager.getInstance(CoreActivity.this);
        mCurrentUserId = sessionManager.getCurrentUserID();
        isValidDevice = sessionManager.isValidDevice();
        isLoginKeySent = sessionManager.isLoginKeySent();
    }

    /**
     * Stop the handler
     */
    @Override
    public void onStop() {
        super.onStop();

        statusHandler = new Handler();
        statusRunnable = new Runnable() {
            @Override
            public void run() {
                if (!sessionManager.isScreenActivated()) {
                    ChangeSetController.setChangeStatus("0");
                }
            }
        };
        statusHandler.postDelayed(statusRunnable, 2000);
    }


    /**
     * binding the progress
     *
     * @param message    getting message from input data
     * @param cancelable based on boolean value progress enable or disable
     */
    public void initProgress(String message, boolean cancelable) {
        progressDialog = getProgressDialogInstance();
        progressDialog.setMessage(message);
        progressDialog.setCancelable(cancelable);
    }

    /**
     * shown ProgressDialog
     *
     * @return response for ProgressDialog
     */
    public ProgressDialog getProgressDialogInstance() {
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setIndeterminateDrawable(ContextCompat.getDrawable(this, R.drawable.color_primary_progress_dialog));
        dialog.setIndeterminate(true);
//        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        return dialog;
    }

    /**
     * showKeyboard function
     */
    public void showKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(
                    getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED, 0);
        }
    }

    /**
     * hideKeyboard function
     */
    public void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    /**
     * Shown ProgressDialog
     */
    public void showProgressDialog() {
        if (progressDialog != null && !progressDialog.isShowing() && !isFinishing())
            progressDialog.show();
    }

    /**
     * hide ProgressDialog
     */
    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing() && !isFinishing())
            progressDialog.dismiss();
    }

    /**
     * inilization of StrictMode
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Your own code to create the view
        // ...

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        checkForUpdates();
    }

    /***
     * handling message service class
     */
    @Override
    protected void onResume() {
        super.onResume();
        // ... your own onResume implementation
        checkForCrashes();

        if (isAppRunning() && mCurrentUserId != null && !mCurrentUserId.equals("") && isValidDevice) {
            // Maintain online status
            sessionManager.setIsScreenActivated(true);
            final Handler ha = new Handler();
            ha.postDelayed(new Runnable() {

                @Override
                public void run() {
                    if (sessionManager.isScreenActivated()) {
                        ChangeSetController.setChangeStatus("1");

                        ha.postDelayed(this, 60000);
                    }

                }
            }, 60000);

            ChangeSetController.setChangeStatus("1");

//            if (!ContactsSync.isStarted) {
//                Intent contactIntent = new Intent(CoreActivity.this, ContactsSync.class);
//                startService(contactIntent);
//            }

            if (!MessageService.isStarted() && isLoginKeySent) {
                Intent msgSvcIntent = new Intent(CoreActivity.this, MessageService.class);
                startService(msgSvcIntent);
            }
        }
    }

    /**
     * sessionManager handling the screen view
     */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterManagers();

        sessionManager.setIsScreenActivated(false);
    }

    /**
     * remove callback handler
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!isAppRunning()) {
            if (statusHandler != null) {
                statusHandler.removeCallbacks(statusRunnable);
            }
            ChangeSetController.setChangeStatus("0");
        }
    }

    private void checkForCrashes() {
        //CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        //UpdateManager.register(this);
    }

    private void unregisterManagers() {
        //UpdateManager.unregister();
    }

    /***
     * Check the app was running state or not
     * @return response value
     */
    private boolean isAppRunning() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }

        return false;
    }

    /**
     * check Audio Record Permission
     *
     * @return response value
     */
    public boolean checkAudioRecordPermission() {
       /* int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);*/
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return /*result == PackageManager.PERMISSION_GRANTED &&*/
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * show Progress
     *
     * @param show  based on value progrss visible or not
     * @param view1 based on view shown the widget
     * @param view2 based on view shown the widget
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show, final View view1, final View view2) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            view1.setVisibility(show ? View.GONE : View.VISIBLE);
            view1.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view1.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            view2.setVisibility(show ? View.VISIBLE : View.GONE);
            view2.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    view2.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }

    }


    /**
     * Image File Path based on uri
     *
     * @param uri input value(uri)
     * @return response value
     */
    public String getImageFilePath(Uri uri) {
        String path = null;
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null) {
                try {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    path = cursor.getString(columnIndex);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    cursor.close();
                }

                if (path == null) {
                    path = FileUploadDownloadManager.getImageRealFilePath(this, uri);
                }
            } else {
                path = uri.getPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }


}
