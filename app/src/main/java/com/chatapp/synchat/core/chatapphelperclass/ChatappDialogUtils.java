package com.chatapp.synchat.core.chatapphelperclass;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;

import com.chatapp.synchat.R;

/**
 *
 */
public class ChatappDialogUtils {

    /**
     * show runtime Permission Denied Dialog
     *
     * @param mContext current activity
     */
    public static void showPermissionDeniedDialog(final Activity mContext) {
        if (!mContext.isFinishing()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
            alertDialogBuilder.setTitle(("Alert"));
            alertDialogBuilder.setMessage(mContext.getResources().getString(R.string.reGrantPermissionMsg));
            alertDialogBuilder.setPositiveButton(mContext.getResources().getString(R.string.appSetting),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
                            Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, uri);
                            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            mContext.startActivity(settingsIntent);
                        }
                    });

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog = alertDialogBuilder.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
        }

    }

    /**
     * Check internet connection
     * @param mContext  current activity
     */
    public static void showCheckInternetDialog(final Activity mContext) {
        if (!mContext.isFinishing()) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
            alertDialog.setTitle(R.string.login_failed);
            alertDialog.setMessage(R.string.check_internet_connection);

            alertDialog.setNegativeButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {

                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
    }

}
