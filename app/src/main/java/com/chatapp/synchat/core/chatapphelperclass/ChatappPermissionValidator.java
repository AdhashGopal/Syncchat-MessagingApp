package com.chatapp.synchat.core.chatapphelperclass;

import android.content.pm.PackageManager;

import android.os.Build;

import android.Manifest;

import android.app.Activity;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

/**
 * created by  Adhash Team on 10/6/2016.
 */
public class ChatappPermissionValidator {

    public enum Constants {
        PERMISSION_READ_PHONE_STATE, PERMISSION_CALL,
        PERMISSION_ACCESS_FINE_LOCATION, PERMISSION_ACCESS_COARSE_LOCATION, PERMISSION_CAMERA,
        PERMISSION_WRITE_EXTERNAL_STORAGE, PERMISSION_READ_EXTERNAL_STORAGE,
        PERMISSION_SEND_SMS, PERMISSION_READ_SMS, PERMISSION_RECEIVE_SMS,MANAGE_DOCUMENTS,
        PERMISSSION_READ_CALENDAR, PERMISSION_WRITE_CALENDAR, WAKE_LOCK, Record_Audio, Record_setting, CHANGE_NETWORK_STATE, WRITE_SETTINGS
    }

    //private ArrayList<String> requestedPermissionsList = null;
    private static ArrayList<String> requiredPermissionsList = null;
    private static ArrayList<String> requiredPermissionMsgs = null;

    /******************************************************/

    /**
     * custom method to check, add and request for run time permissions
     *
     * @param mActivity:               reference of current activity
     * @param rqstedPermissionsList:   list of the permissions requested
     * @param PERMISSION_REQUEST_CODE: int type to check the corresponding response on onRequestPermissionsResult
     * @return boolean: true if all requested permissions are already granted else false
     */
    public static boolean checkPermission(final Activity mActivity, ArrayList<Constants> rqstedPermissionsList, final int PERMISSION_REQUEST_CODE) {
        if (requiredPermissionsList == null) {
            requiredPermissionsList = new ArrayList<String>();
            requiredPermissionMsgs = new ArrayList<String>();
        } else {
            requiredPermissionsList.clear();
            requiredPermissionMsgs.clear();
        }


        if (rqstedPermissionsList != null && rqstedPermissionsList.size() > 0) {
            for (Constants requestedPermission : rqstedPermissionsList) {

                switch (requestedPermission) {
                    // to get device Id
                    case WRITE_SETTINGS:
                        addPermission(Manifest.permission.WRITE_SETTINGS, mActivity);
                        break;
                    case PERMISSION_READ_PHONE_STATE:
                        addPermission(Manifest.permission.READ_PHONE_STATE, mActivity);
                        break;

                    case CHANGE_NETWORK_STATE:
                        addPermission(Manifest.permission.CHANGE_NETWORK_STATE, mActivity);
                        break;

                    case Record_Audio:
//                        addPermission(Manifest.permission.RECORD_AUDIO, mActivity);
                        break;

                    case Record_setting:
                        addPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS, mActivity);
                        break;

                    case PERMISSION_CALL:
//                        addPermission(Manifest.permission.CALL_PHONE, mActivity);
                        break;

                    case WAKE_LOCK:
                        addPermission(Manifest.permission.WAKE_LOCK, mActivity);
                        break;


                    // to access fine & corase location along with phone state for device id
                    case PERMISSION_ACCESS_FINE_LOCATION:
                        addPermission(Manifest.permission.ACCESS_FINE_LOCATION, mActivity);
                        break;

                    case PERMISSION_ACCESS_COARSE_LOCATION:
                        addPermission(Manifest.permission.ACCESS_COARSE_LOCATION, mActivity);
                        break;


                    case PERMISSION_CAMERA:
                        addPermission(Manifest.permission.CAMERA, mActivity);
                        break;


                    case PERMISSION_WRITE_EXTERNAL_STORAGE:
                        addPermission(Manifest.permission.READ_EXTERNAL_STORAGE, mActivity);
                        break;

                    /*case PERMISSION_READ_EXTERNAL_STORAGE:
                        addPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, mActivity);
                        break;*/


                    case PERMISSION_SEND_SMS:
                        addPermission(Manifest.permission.SEND_SMS, mActivity);
                        break;

                    case PERMISSION_RECEIVE_SMS:
                        addPermission(Manifest.permission.RECEIVE_SMS, mActivity);
                        break;

                    case PERMISSION_READ_SMS:
                        addPermission(Manifest.permission.READ_SMS, mActivity);
                        break;


                    case PERMISSSION_READ_CALENDAR:
                        addPermission(Manifest.permission.READ_CALENDAR, mActivity);
                        break;

                    case PERMISSION_WRITE_CALENDAR:
                        addPermission(Manifest.permission.WRITE_CALENDAR, mActivity);
                        break;

                    case MANAGE_DOCUMENTS:
                        addPermission(Manifest.permission.MANAGE_DOCUMENTS, mActivity);
                        break;

                    default:
                        break;
                }
            }
        }

        if (requiredPermissionsList.size() > 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mActivity.requestPermissions(requiredPermissionsList.toArray(new String[requiredPermissionsList.size()]), PERMISSION_REQUEST_CODE);
            }
            return false;
        } else {
            return true;
        }
    }
    /******************************************************/


    /**
     * custom method to check whether the requested permission has granted or not
     * If hasn't been granted add to requested list
     *
     * @param permission: the requested permission
     * @param mActivity:  current activity reference
     */
    private static void addPermission(final String permission, final Activity mActivity) {
        if (ContextCompat.checkSelfPermission(mActivity, permission) != PackageManager.PERMISSION_GRANTED) {
            requiredPermissionsList.add(permission);
        }
    }
}