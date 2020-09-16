package com.chatapp.synchat.app;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.Request;
import com.chatapp.synchat.R;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.service.ServiceRequest;
import com.chatapp.synchat.core.uploadtoserver.FileUploadDownloadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.philio.pinentry.PinEntryView;

import static com.chatapp.synchat.app.ShareFromThirdPartyAppActivity.isDownloadsDocument;


public class PinEnterActivity extends CoreActivity {
    List<EditText> editTexts = new ArrayList<>();
    EditText otpedit, otpedit1, otpedit2, otpedit3, currentlyFocusedEditText;
    int j = 0;
    int pincounter = 0;
    String lastotp;
    InputMethodManager imm;

    TextView enter_pintext, resetPin;
    int newPinFlow = 0;
    private PinEntryView pinEntryView;
    // Uri mSharedImageUri;
    String ret = "";
    private SessionManager sessionManager;
    private int pinResetStatus = 0;
    int data1 = 0;
    private boolean blockdata = false;
    private RelativeLayout resetLay;


    /**
     * getting all other media file like image, video, document, audio file. enter pin will moved home screen
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin_enter);
        sessionManager = SessionManager.getInstance(PinEnterActivity.this);
        enter_pintext = (TextView) findViewById(R.id.enter_pintext);
        resetPin = (TextView) findViewById(R.id.txt_reset_pin);
        pinEntryView = (PinEntryView) findViewById(R.id.pin_entry_simple);
        resetLay = findViewById(R.id.reset_lay);
//        memoryTest();
//        String path = storage.getExternalStorageDirectory();

        resetPin.setVisibility(SessionManager.getInstance(PinEnterActivity.this).getLoginType() == 1 ? View.GONE : View.VISIBLE);

        resetPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityLauncher.launchADFSScreen(PinEnterActivity.this, 1);
            }
        });


        try {

            if (getIntent().hasExtra("reset_pin_status")) {
                pinResetStatus = getIntent().getIntExtra("reset_pin_status", 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {

            if (SessionManager.getInstance(this).getCurrentUserID() != null && !SessionManager.getInstance(this).getCurrentUserID().equalsIgnoreCase("")) {
//            if (SessionManager.getInstance(PinEnterActivity.this).getLoginSuccessStatus().equalsIgnoreCase("0")) {
                if (getIntent().getStringExtra("msisdn") == null) {
                    if (Intent.ACTION_SEND.equals(getIntent().getAction()) || Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction()) || Intent.ACTION_GET_CONTENT.equals(getIntent().getAction()) || Intent.ACTION_OPEN_DOCUMENT_TREE.equals(getIntent().getAction()) || Intent.ACTION_OPEN_DOCUMENT.equals(getIntent().getAction())) {
                        if (SessionManager.getInstance(PinEnterActivity.this).getUserMpin().equals("")) {
                            newPinFlow = 1;
//                    resetPin.setVisibility(View.GONE);
                            resetLay.setVisibility(View.GONE);
                            enter_pintext.setText(getResources().getString(R.string.enter_new_pin));
                        } else {

                            try {
                                newPinFlow = 0;
                                getIntent().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getIntent().putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                getIntent().addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                getIntent().addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                getIntent().addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
                                ArrayList<Parcelable> list = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);
                                File outerFolder = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH);
                                deleteRecursive(outerFolder);
                                if (list != null) {
                                    if (list.size() >= 2) {
                                        for (Parcelable parcelable : list) {
                                            Uri uri = (Uri) parcelable;
                                            Log.e("Uri-->", uri + "");
                                            String value = uri.toString();
                                            if (value.contains("content://com.google.android.apps.photos")) {
                                                if (value.contains("video")) {
                                                    getPathFromInputStreamUrivideo(this, uri);
                                                } else {
                                                    getPathFromInputStreamUri(this, uri);
                                                }
                                            } else {

                                                ret = "local";
                                                String type = getIntent().getType();

                                                if (type.startsWith("image/")) {
                                                    String img1 = getImageFilePath(uri);
                                                    File file = new File(img1);
                                                    getFileFolderSize(file);
                                                    long size = 0;
                                                    size = getFileFolderSize(file);
                                                    double sizeMB = (double) size / 1024 / 1024;

                                                    Log.e("MB", sizeMB + "");
                                                    int datavalue = (int) sizeMB;
                                                    Log.e("MB1", datavalue + "");
                                                    data1 += datavalue;
                                                    Log.e("TOTal", data1 + "");
                                                } else if (type.startsWith("audio/") || type.contains("application/ogg")) {
                                                    String extension = getFileExtnFromPath(uri.getPath());
                                                    Log.e("dat", extension);

                                                    File file = new File(extension);
                                                    getFileFolderSize(file);
                                                    long size = 0;
                                                    size = getFileFolderSize(file);
                                                    double sizeMB = (double) size / 1024 / 1024;

                                                    Log.e("MB", sizeMB + "");
                                                    int datavalue = (int) sizeMB;
                                                    Log.e("MB1", datavalue + "");
                                                    data1 += datavalue;
                                                    Log.e("TOTal", data1 + "");
                                                } else if (type.startsWith("video/")) {
                                                    String videoPath = getVideoFilePath(uri);
                                                    Log.e("dat", videoPath);

                                                    File file = new File(videoPath);
                                                    getFileFolderSize(file);
                                                    long size = 0;
                                                    size = getFileFolderSize(file);
                                                    double sizeMB = (double) size / 1024 / 1024;

                                                    Log.e("MB", sizeMB + "");
                                                    int datavalue = (int) sizeMB;
                                                    Log.e("MB1", datavalue + "");
                                                    data1 += datavalue;
                                                    Log.e("TOTal", data1 + "");
                                                } else if (type.startsWith("application/*")) {
                                                    String data = uri.toString();
                                                    if (data.contains("image")) {
                                                        String path = getImageFilePath(uri);
                                                        Log.e("dat", path);

                                                        File file = new File(path);
                                                        getFileFolderSize(file);
                                                        long size = 0;
                                                        size = getFileFolderSize(file);
                                                        double sizeMB = (double) size / 1024 / 1024;

                                                        Log.e("MB", sizeMB + "");
                                                        int datavalue = (int) sizeMB;
                                                        Log.e("MB1", datavalue + "");
                                                        data1 += datavalue;
                                                        Log.e("TOTal", data1 + "");
                                                    } else if (data.contains("audio")) {
                                                        String extension = getAudioFilePath(uri);
                                                        Log.e("dat", extension);

                                                        File file = new File(extension);
                                                        getFileFolderSize(file);
                                                        long size = 0;
                                                        size = getFileFolderSize(file);
                                                        double sizeMB = (double) size / 1024 / 1024;

                                                        Log.e("MB", sizeMB + "");
                                                        int datavalue = (int) sizeMB;
                                                        Log.e("MB1", datavalue + "");
                                                        data1 += datavalue;
                                                        Log.e("TOTal", data1 + "");
                                                    } else if (data.contains("video")) {
                                                        String path = getVideoFilePath(uri);
                                                        Log.e("dat", path);

                                                        File file = new File(path);
                                                        getFileFolderSize(file);
                                                        long size = 0;
                                                        size = getFileFolderSize(file);
                                                        double sizeMB = (double) size / 1024 / 1024;

                                                        Log.e("MB", sizeMB + "");
                                                        int datavalue = (int) sizeMB;
                                                        Log.e("MB1", datavalue + "");
                                                        data1 += datavalue;
                                                        Log.e("TOTal", data1 + "");
                                                    } else {
                                                        if (type.contains("zip")) {
                                                            blockdata = true;
                                                        } else {
                                                            String path = getDocumentPathFromURI(uri);
                                                            Log.e("dat", path);

                                                            File file = new File(path);
                                                            getFileFolderSize(file);
                                                            long size = 0;
                                                            size = getFileFolderSize(file);
                                                            double sizeMB = (double) size / 1024 / 1024;

                                                            Log.e("MB", sizeMB + "");
                                                            int datavalue = (int) sizeMB;
                                                            Log.e("MB1", datavalue + "");
                                                            data1 += datavalue;
                                                            Log.e("TOTal", data1 + "");
                                                        }

                                                    }
                                                } else if (type.equalsIgnoreCase("application/") || type.startsWith("text/plain") || type.contains("zip")) {

                                                    if (type.contains("zip")) {
                                                        blockdata = true;
                                                    } else {
                                                        String path = getDocumentPathFromURI(uri);
                                                        Log.e("dat", path);

                                                        File file = new File(path);
                                                        getFileFolderSize(file);
                                                        long size = 0;
                                                        size = getFileFolderSize(file);
                                                        double sizeMB = (double) size / 1024 / 1024;

                                                        Log.e("MB", sizeMB + "");
                                                        int datavalue = (int) sizeMB;
                                                        Log.e("MB1", datavalue + "");
                                                        data1 += datavalue;
                                                        Log.e("TOTal", data1 + "");
                                                    }
                                                } else if (type.equalsIgnoreCase("*/*")) {
                                                    String data = uri.toString();
                                                    if (data.contains("image")) {
                                                        String path = getImageFilePath(uri);
                                                        Log.e("dat", path);

                                                        File file = new File(path);
                                                        getFileFolderSize(file);
                                                        long size = 0;
                                                        size = getFileFolderSize(file);
                                                        double sizeMB = (double) size / 1024 / 1024;

                                                        Log.e("MB", sizeMB + "");
                                                        int datavalue = (int) sizeMB;
                                                        Log.e("MB1", datavalue + "");
                                                        data1 += datavalue;
                                                        Log.e("TOTal", data1 + "");
                                                    } else if (data.contains("audio")) {
                                                        String extension = getAudioFilePath(uri);
                                                        Log.e("dat", extension);

                                                        File file = new File(extension);
                                                        getFileFolderSize(file);
                                                        long size = 0;
                                                        size = getFileFolderSize(file);
                                                        double sizeMB = (double) size / 1024 / 1024;

                                                        Log.e("MB", sizeMB + "");
                                                        int datavalue = (int) sizeMB;
                                                        Log.e("MB1", datavalue + "");
                                                        data1 += datavalue;
                                                        Log.e("TOTal", data1 + "");
                                                    } else if (data.contains("video")) {
                                                        String path = getVideoFilePath(uri);
                                                        Log.e("dat", path);

                                                        File file = new File(path);
                                                        getFileFolderSize(file);
                                                        long size = 0;
                                                        size = getFileFolderSize(file);
                                                        double sizeMB = (double) size / 1024 / 1024;

                                                        Log.e("MB", sizeMB + "");
                                                        int datavalue = (int) sizeMB;
                                                        Log.e("MB1", datavalue + "");
                                                        data1 += datavalue;
                                                        Log.e("TOTal", data1 + "");
                                                    } else {
                                                        if (type.contains("zip")) {
                                                            blockdata = true;
                                                        } else {
                                                            String path = getDocumentPathFromURI(uri);
                                                            Log.e("dat", path);

                                                            File file = new File(path);
                                                            getFileFolderSize(file);
                                                            long size = 0;
                                                            size = getFileFolderSize(file);
                                                            double sizeMB = (double) size / 1024 / 1024;

                                                            Log.e("MB", sizeMB + "");
                                                            int datavalue = (int) sizeMB;
                                                            Log.e("MB1", datavalue + "");
                                                            data1 += datavalue;
                                                            Log.e("TOTal", data1 + "");
                                                        }

                                                    }
                                                }


                                            }
                                        }
                                    }
                                } else {
                                    Uri uri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
                                    if (uri != null) {
                                        Log.e("Uri-->", uri + "");
                                        String value = uri.toString();
                                        File outerFolder1 = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH);
                                        deleteRecursive(outerFolder1);
                                        if (value.contains("content://com.google.android.apps.photos")) {
                                            if (value.contains("video")) {
                                                getPathFromInputStreamUrivideo(this, uri);
                                            } else {
                                                getPathFromInputStreamUri(this, uri);
                                            }
                                        } else {
                                            ret = "local";

                                            String type = getIntent().getType();

                                            if (type.startsWith("image/")) {
                                                String img = "";
                                                String uriPath = uri.toString();
                                                if (uriPath.contains("content://com.android.chrome.FileProvider/downloads/")) {
                                                    img = uriPath.replace("content://com.android.chrome.FileProvider/downloads/", "/storage/emulated/0/Download/");
                                                    Log.e("newString", img);
                                                } else {
                                                    img = getImageFilePath(uri);
                                                }


                                                File file = new File(img);
                                                getFileFolderSize(file);
                                                long size = 0;
                                                size = getFileFolderSize(file);
                                                double sizeMB = (double) size / 1024 / 1024;

                                                Log.e("MB", sizeMB + "");
                                                int datavalue = (int) sizeMB;
                                                Log.e("MB1", datavalue + "");
                                                data1 += datavalue;
                                                Log.e("TOTal", data1 + "");
                                            } else if (type.startsWith("audio/") || type.contains("application/ogg")) {

                                                String path = "";
                                                String uriPath = uri.toString();
                                                if (uriPath.contains("content://com.android.chrome.FileProvider/downloads/")) {
                                                    path = uriPath.replace("content://com.android.chrome.FileProvider/downloads/", "/storage/emulated/0/Download/");
                                                    Log.e("newString", path);
                                                } else {
                                                    path = getAudioFilePath(uri);
                                                }
                                                Log.e("dat", path);


                                                Log.e("dat", path);

                                                File file = new File(path);
                                                getFileFolderSize(file);
                                                long size = 0;
                                                size = getFileFolderSize(file);
                                                double sizeMB = (double) size / 1024 / 1024;

                                                Log.e("MB", sizeMB + "");
                                                int datavalue = (int) sizeMB;
                                                Log.e("MB1", datavalue + "");
                                                data1 += datavalue;
                                                Log.e("TOTal", data1 + "");
                                            } else if (type.startsWith("video/")) {

                                                String videoPath = "";
                                                String uriPath = uri.toString();
                                                if (uriPath.contains("content://com.android.chrome.FileProvider/downloads/")) {
                                                    videoPath = uriPath.replace("content://com.android.chrome.FileProvider/downloads/", "/storage/emulated/0/Download/");
                                                    Log.e("newString", videoPath);
                                                } else {
                                                    videoPath = getVideoFilePath(uri);
                                                }


                                                Log.e("dat", videoPath);

                                                File file = new File(videoPath);
                                                getFileFolderSize(file);
                                                long size = 0;
                                                size = getFileFolderSize(file);
                                                double sizeMB = (double) size / 1024 / 1024;

                                                Log.e("MB", sizeMB + "");
                                                int datavalue = (int) sizeMB;
                                                Log.e("MB1", datavalue + "");
                                                data1 += datavalue;
                                                Log.e("TOTal", data1 + "");
                                            } else if (type.startsWith("application/") || type.startsWith("text/plain")) {

                                                if (type.contains("/zip") || type.contains("application/x-rar-compressed")) {
                                                    blockdata = true;
                                                    Toast.makeText(PinEnterActivity.this, "Format Not Support", Toast.LENGTH_LONG).show();
                                                } else {
                                                    String path = "";
                                                    String uriPath = uri.toString();
                                                    if (uriPath.contains("content://com.android.chrome.FileProvider/downloads/")) {
                                                        path = uriPath.replace("content://com.android.chrome.FileProvider/downloads/", "/storage/emulated/0/Download/");
                                                        Log.e("newString", path);
                                                    } else {
                                                        path = getDocumentPathFromURI(uri);
                                                    }
                                                    Log.e("dat", path);

                                                    File file = new File(path);
                                                    getFileFolderSize(file);
                                                    long size = 0;
                                                    size = getFileFolderSize(file);
                                                    double sizeMB = (double) size / 1024 / 1024;

                                                    Log.e("MB", sizeMB + "");
                                                    int datavalue = (int) sizeMB;
                                                    Log.e("MB1", datavalue + "");
                                                    data1 += datavalue;
                                                    Log.e("TOTal", data1 + "");
                                                }
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        newPinFlow = 1;
//                resetPin.setVisibility(View.GONE);
                        resetLay.setVisibility(View.GONE);
                        enter_pintext.setText(getResources().getString(R.string.enter_new_pin));
                    }
                } else {
                    newPinFlow = 0;
                }
            } else {
                Intent intent = new Intent(PinEnterActivity.this, ReLoadingActivityNew.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        pinEntryView.setOnPinEnteredListener(new PinEntryView.OnPinEnteredListener() {
            @Override
            public void onPinEntered(final String pin) {

                try {

                    if (newPinFlow == 1) {

                        if (pincounter == 0) {
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    resetLay.setVisibility(View.GONE);
                                    enter_pintext.setText(getResources().getString(R.string.confirm_pin));
                                    pinEntryView.setText("");
                                    pincounter = 1;
                                    lastotp = pin;
                                    //Do something after 100ms
                                }
                            }, 500);


                        } else {
                            if (lastotp.equals(pin)) {
//                            if (Intent.ACTION_SEND.equals(getIntent().getAction())) {
//                                Intent intent=new Intent(PinEnterActivity.this,ShareFromThirdPartyAppActivity.class);
//                                intent.putExtras(getIntent());
//                                startActivity(intent);
//
//                            }
//                            else {


                                //   }
                                if (pinResetStatus == 1) {
                                    makeResetCompletedRequest(pin);
                                } else {
                                    SessionManager.getInstance(PinEnterActivity.this).setUsermpin(pin);
                                    ActivityLauncher.launchHomeScreen(PinEnterActivity.this);
                                }


                            } else {
                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        resetLay.setVisibility(View.VISIBLE);
                                        enter_pintext.setText(getResources().getString(R.string.enter_pin));

                                        pincounter = 0;
                                        pinEntryView.setText("");
                                        Toast.makeText(PinEnterActivity.this, "Pin didn't match. Enter again.", Toast.LENGTH_SHORT).show();
                                        //Do something after 100ms
                                    }
                                }, 500);

                            }
                        }

                    } else {
                        System.out.println("========existingpin");
                        if (pin.equals(SessionManager.getInstance(PinEnterActivity.this).getUserMpin())) {
                            if (Intent.ACTION_SEND.equals(getIntent().getAction()) || Intent.ACTION_SEND_MULTIPLE.equals(getIntent().getAction())) {

                                if (ret.equalsIgnoreCase("local")) {
                                    if (data1 <= 100) {
                                        if (blockdata) {
                                            Toast.makeText(PinEnterActivity.this, "Format Not Support", Toast.LENGTH_LONG).show();
                                        } else {
                                            Intent intent = new Intent(PinEnterActivity.this, ShareFromThirdPartyAppActivity.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            System.out.println("========navtoactivity");
                                            Bundle bundle = getIntent().getExtras();
                                            intent.putExtra("GOOGLE_IMG", ret);
                                            intent.putExtras(bundle);
                                            ShareFromThirdPartyAppActivity.shareAction = getIntent().getAction();
                                            ShareFromThirdPartyAppActivity.type = getIntent().getType();

                                            startActivity(intent);
                                            finish();
                                        }

                                    } else {
                                        hideKeyboard();
                                        ActivityLauncher.launchHomeScreen(PinEnterActivity.this);
                                        Toast.makeText(PinEnterActivity.this, "You can upload maximum " + 100 + " MB file only", Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    File file = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH);
                                    getFileFolderSize(file);
                                    long size = 0;
                                    size = getFileFolderSize(file);
                                    double sizeMB = (double) size / 1024 / 1024;

                                    Log.e("MB", sizeMB + "");
                                    int datavalue = (int) sizeMB;
                                    Log.e("MB1", datavalue + "");

                                    if (datavalue <= 100) {
                                        Intent intent = new Intent(PinEnterActivity.this, ShareFromThirdPartyAppActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        System.out.println("========navtoactivity");
                                        Bundle bundle = getIntent().getExtras();
                                        intent.putExtra("GOOGLE_IMG", ret);
                                        intent.putExtras(bundle);
                                        ShareFromThirdPartyAppActivity.shareAction = getIntent().getAction();
                                        ShareFromThirdPartyAppActivity.type = getIntent().getType();
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        if (file.isDirectory()) {
                                            String[] children = file.list();
                                            if (children != null) {
                                                for (int i = 0; i < children.length; i++) {
                                                    new File(file, children[i]).delete();
                                                }
                                            }
                                        }
                                        hideKeyboard();
                                        ActivityLauncher.launchHomeScreen(PinEnterActivity.this);
                                        Toast.makeText(PinEnterActivity.this, "You can upload maximum " + 100 + " MB file only", Toast.LENGTH_LONG).show();
                                    }
                                }


                            } else {
                                hideKeyboard();
                                ActivityLauncher.launchHomeScreen(PinEnterActivity.this);
                            }
                        } else {

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    resetLay.setVisibility(View.VISIBLE);
                                    enter_pintext.setText(getResources().getString(R.string.enter_pin));

                                    pincounter = 0;
                                    pinEntryView.setText("");
                                    Toast.makeText(PinEnterActivity.this, "Pin didn't match. Enter again.", Toast.LENGTH_SHORT).show();
                                    //Do something after 100ms
                                }
                            }, 500);

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


    }

    /**
     * getVideoFilePath
     *
     * @param uri video uri path
     * @return value
     */
    private String getVideoFilePath(Uri uri) {
        String path = null;

        String[] filePathColumn = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            cursor.close();

            if (path == null) {
                path = FileUploadDownloadManager.getVideoRealFilePath(PinEnterActivity.this, uri);
                Log.e("====path", "====path" + path);
            }
        } else {
            path = uri.getPath();
        }

        return path;
    }

    public String getDownloadImageFilePath(Uri uri) {
        String path = null;
        DownloadManager.Request dmr = new DownloadManager.Request(Uri.parse(String.valueOf(uri)));

        String fileName1 = URLUtil.guessFileName(String.valueOf(uri), null, MimeTypeMap.getFileExtensionFromUrl(String.valueOf(uri)));
        dmr.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName1);
        dmr.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        dmr.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(dmr);
        return path;
    }

    /**
     * getImageFilePath
     *
     * @param uri image uri path
     * @return value
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


    /**
     * getting file name
     *
     * @param fileName name of the file path
     * @return value
     */
    public String getFileExtnFromPath(String fileName) {
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    /**
     * getAudioFilePath
     *
     * @param uri audio uri path
     * @return value
     */
    private String getAudioFilePath(Uri uri) {
        String path = null;

        String[] filePathColumn = {MediaStore.Audio.Media.DATA};
        System.out.println("====pathindb" + filePathColumn);
        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            System.out.println("====pathindb" + path);
            cursor.close();
            if (path == null) {
                path = FileUploadDownloadManager.getAudioRealFilePath(PinEnterActivity.this, uri);
            }
        } else {
            path = uri.getPath();
            System.out.println("====pathindbelse" + path);
        }

        return path;
    }

    /**
     * getDocumentPathFromURI
     *
     * @param uri Document uri path
     * @return value
     */
    public String getDocumentPathFromURI(Uri uri) {

        String path = null;

        if (isDownloadsDocument(uri)) {
            final String id = DocumentsContract.getDocumentId(uri);
            System.out.println("====pathindb" + id);
            uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

        }

        String[] filePathColumn = {MediaStore.Files.FileColumns.DATA};
        System.out.println("====pathindb" + filePathColumn);

        Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            System.out.println("====pathindb" + columnIndex);
            path = cursor.getString(columnIndex);
            System.out.println("====pathindb" + path);
            cursor.close();
            if (path == null) {

                System.out.println("====here in null");
                path = FileUploadDownloadManager.getDocumentRealFilePath(PinEnterActivity.this, uri);
                System.out.println("====path" + path);
            }
        } else {
            path = uri.getPath();
            System.out.println("====pathindbelse" + path);
        }
        return path;
    }

    /**
     * Killed the activity
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        blockdata = false;
    }

    public String getdownloadPath(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null,
                null, null, null);

        if (cursor == null) { // Source is Dropbox or other similar local file
            // path
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            try {
                int idx = cursor
                        .getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
            } catch (Exception e) {
                result = "";
            }
            cursor.close();
        }
        return result;
    }

    /**
     * getPathFromInputStreamUrivideo
     *
     * @param context current activity
     * @param uri     video path uri
     * @return value
     */
    public String getPathFromInputStreamUrivideo(Context context, Uri uri) {
        InputStream inputStream = null;
        String urifilePath = null;
        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                File videoFile = createTemporalFileFromPhone(inputStream);
                urifilePath = videoFile.getPath();
            } catch (FileNotFoundException e) {
                //Logger.printStackTrace(e);
            } catch (IOException e) {
                //Logger.printStackTrace(e);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return urifilePath;
    }

    private void showAlert(String message) {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PinEnterActivity.this, R.style.MyDialogTheme);
        alertDialog.setCancelable(true);
        // Setting Dialog Title
        alertDialog.setTitle("SynChat");
        // Setting Dialog Message
        alertDialog.setMessage(message);
        // Setting Icon to Dialog
        //  alertDialog.setIcon(R.drawable.tick);
        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    private void makePinResetRequest() {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
            String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            params.put("msisdn", android_id);
            params.put("app_version_name", getVersionName());
            params.put("imei_1", sessionManager.getImeiOne());
            params.put("imei_2", sessionManager.getImeiTwo());
            params.put("OS", "android");
            params.put("reset_request", "1");// 1- reset request, 0- pin reset completed
            params.put("user_id", "Android");

            ServiceRequest request = new ServiceRequest(this);
            request.makeServiceRequest(Constants.VERIFY_NUMBER_REQUEST, Request.Method.POST, params, pinResetRequestListener);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private ServiceRequest.ServiceListener pinResetRequestListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            try {

                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.getInt("Status") == 0) {

                } else if (jsonObject.getInt("Status") == 1) {

                } else {

                }
            } catch (JSONException e) {
                e.printStackTrace();

            }

        }

        @Override
        public void onErrorListener(int state) {

        }
    };

    /**
     * Reset pin request
     *
     * @param pin String value
     */
    private void makeResetCompletedRequest(String pin) {
        try {
            HashMap<String, String> params = new HashMap<String, String>();
//            String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            /*params.put("msisdn", android_id);
            params.put("app_version_name", getVersionName());
            params.put("imei_1", sessionManager.getImeiOne());
            params.put("imei_2", sessionManager.getImeiTwo());
            params.put("OS", "android");*/
            params.put("reset_request", "0");// 1- reset request, 0- pin reset completed
            params.put("user_id", SessionManager.getInstance(this).getCurrentUserID());

            ServiceRequest request = new ServiceRequest(this);
            request.makeServiceRequest(Constants.PIN_RESET_COMPLETED, Request.Method.POST, pin, params, resetCompletedRequestListener);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * Reset pin response (status 1 means move to home screen)
     */
    private ServiceRequest.ServiceRequestListener resetCompletedRequestListener = new ServiceRequest.ServiceRequestListener() {
        @Override
        public void onCompleteListener(String response, String inputValue) {
            try {

                JSONObject jsonObject = new JSONObject(response);

                if (jsonObject.getInt("Status") == 1) {
                    SessionManager.getInstance(PinEnterActivity.this).setResetPinStatus(1);
                    SessionManager.getInstance(PinEnterActivity.this).setUsermpin(inputValue);
                    ActivityLauncher.launchHomeScreen(PinEnterActivity.this);
                } else {
                    SessionManager.getInstance(PinEnterActivity.this).setResetPinStatus(0);
                    Toast.makeText(PinEnterActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }

        }

        @Override
        public void onErrorListener(int state) {

        }
    };

    /**
     * get project version Name
     *
     * @return this is also number
     */
    private String getVersionName() {
        String verName = "";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            verName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }

        return verName;
    }

    /**
     * Share the google photo data
     *
     * @param uriAuthority google auth
     * @return value
     */
    private boolean isGooglePhotoDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.google.android.apps.photos.content".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    private String getUriRealPath(Context ctx, Uri uri) {

        if (isAboveKitKat()) {
            // Android OS above sdk version 19.
            ret = getUriRealPathAboveKitkat(ctx, uri);
        } else {
            // Android OS below sdk version 19
            ret = getImageRealPath(getContentResolver(), uri, null);
        }

        return ret;
    }

    /**
     * getUriRealPathAboveKitkat for OS version
     *
     * @param ctx current activity
     * @param uri for all type of data (video.audio,image,document,etc,.)
     * @return value
     */
    private String getUriRealPathAboveKitkat(Context ctx, Uri uri) {
        String ret = "";

        if (ctx != null && uri != null) {

            if (isContentUri(uri)) {
                if (isGooglePhotoDoc(uri.getAuthority())) {
                    ret = uri.getLastPathSegment();
                } else {
                    ret = getImageRealPath(getContentResolver(), uri, null);
                }
            } else if (isFileUri(uri)) {
                ret = uri.getPath();
            } else if (isDocumentUri(ctx, uri)) {

                // Get uri related document id.
                String documentId = DocumentsContract.getDocumentId(uri);

                // Get uri authority.
                String uriAuthority = uri.getAuthority();

                if (isMediaDoc(uriAuthority)) {
                    String idArr[] = documentId.split(":");
                    if (idArr.length == 2) {
                        // First item is document type.
                        String docType = idArr[0];

                        // Second item is document real id.
                        String realDocId = idArr[1];

                        // Get content uri by document type.
                        Uri mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        if ("image".equals(docType)) {
                            mediaContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                        } else if ("video".equals(docType)) {
                            mediaContentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                        } else if ("audio".equals(docType)) {
                            mediaContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                        }

                        // Get where clause with real document id.
                        String whereClause = MediaStore.Images.Media._ID + " = " + realDocId;

                        ret = getImageRealPath(getContentResolver(), mediaContentUri, whereClause);
                    }

                } else if (isDownloadDoc(uriAuthority)) {
                    // Build download uri.
                    Uri downloadUri = Uri.parse("content://downloads/public_downloads");

                    // Append download document id at uri end.
                    Uri downloadUriAppendId = ContentUris.withAppendedId(downloadUri, Long.valueOf(documentId));

                    ret = getImageRealPath(getContentResolver(), downloadUriAppendId, null);

                } else if (isExternalStoreDoc(uriAuthority)) {
                    String idArr[] = documentId.split(":");
                    if (idArr.length == 2) {
                        String type = idArr[0];
                        String realDocId = idArr[1];

                        if ("primary".equalsIgnoreCase(type)) {
                            ret = Environment.getExternalStorageDirectory() + "/" + realDocId;
                        }
                    }
                }
            }
        }

        return ret;
    }

    /* Check whether current android os version is bigger than kitkat or not. */
    private boolean isAboveKitKat() {
        boolean ret = false;
        ret = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        return ret;
    }

    /* Check whether this uri represent a document or not. */
    private boolean isDocumentUri(Context ctx, Uri uri) {
        boolean ret = false;
        if (ctx != null && uri != null) {
            ret = DocumentsContract.isDocumentUri(ctx, uri);
        }
        return ret;
    }

    /* Check whether this uri is a content uri or not.
     *  content uri like content://media/external/images/media/1302716
     *  */
    private boolean isContentUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("content".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this uri is a file uri or not.
     *  file uri like file:///storage/41B7-12F1/DCIM/Camera/IMG_20180211_095139.jpg
     * */
    private boolean isFileUri(Uri uri) {
        boolean ret = false;
        if (uri != null) {
            String uriSchema = uri.getScheme();
            if ("file".equalsIgnoreCase(uriSchema)) {
                ret = true;
            }
        }
        return ret;
    }

    /* Check whether this document is provided by ExternalStorageProvider. */
    private boolean isExternalStoreDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.externalstorage.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by DownloadsProvider. */
    private boolean isDownloadDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.providers.downloads.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Check whether this document is provided by MediaProvider. */
    private boolean isMediaDoc(String uriAuthority) {
        boolean ret = false;

        if ("com.android.providers.media.documents".equals(uriAuthority)) {
            ret = true;
        }

        return ret;
    }

    /* Return uri represented document file real local path.*/
    private String getImageRealPath(ContentResolver contentResolver, Uri uri, String whereClause) {
        String ret = "";

        // Query the uri with condition.
        Cursor cursor = contentResolver.query(uri, null, whereClause, null, null);

        if (cursor != null) {
            boolean moveToFirst = cursor.moveToFirst();
            if (moveToFirst) {

                // Get columns name by uri type.
                String columnName = MediaStore.Images.Media.DATA;

                if (uri == MediaStore.Images.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Images.Media.DATA;
                } else if (uri == MediaStore.Audio.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Audio.Media.DATA;
                } else if (uri == MediaStore.Video.Media.EXTERNAL_CONTENT_URI) {
                    columnName = MediaStore.Video.Media.DATA;
                }

                // Get column index.
                int imageColumnIndex = cursor.getColumnIndex(columnName);

                // Get column value which is the uri related file local path.
                ret = cursor.getString(imageColumnIndex);
            }
        }

        return ret;
    }

    /**
     * getPathFromInputStreamUri for google photo
     *
     * @param context current activity
     * @param uri     uri data
     * @return value
     */
    public String getPathFromInputStreamUri(Context context, Uri uri) {
        InputStream inputStream = null;
        String filePath = null;
        if (uri.getAuthority() != null) {
            try {
                inputStream = context.getContentResolver().openInputStream(uri);
                File photoFile = createTemporalFileFrom(inputStream);
                filePath = photoFile.getPath();
            } catch (FileNotFoundException e) {
                //Logger.printStackTrace(e);
            } catch (IOException e) {
                //Logger.printStackTrace(e);
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return filePath;
    }

    /**
     * createTemporalFile for google photo
     *
     * @param inputStream local space
     * @return value
     * @throws IOException error throws
     */
    private File createTemporalFileFrom(InputStream inputStream) throws IOException {
        File targetFile = null;
        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];
            targetFile = createTemporalFile();
            OutputStream outputStream = new FileOutputStream(targetFile);
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return targetFile;
    }


    /**
     * createTemporalFileFromPhone for google photo
     *
     * @param inputStream local space
     * @return value
     * @throws IOException error throws
     */
    private File createTemporalFileFromPhone(InputStream inputStream) throws IOException {
        File targetFile = null;
        if (inputStream != null) {
            int read;
            byte[] buffer = new byte[8 * 1024];
            targetFile = createTemporalVideoFile();
            OutputStream outputStream = new FileOutputStream(targetFile);
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return targetFile;
    }

    /**
     * createTemporalFile for google photo
     *
     * @return value
     */
    private File createTemporalFile() {
        return new File(createPath(), getTimeStamp() + ".jpg");
    }

    /**
     * createTemporalVideoFile for google photo
     *
     * @return value
     */
    private File createTemporalVideoFile() {
        return new File(createvideoPath(), getTimeStamp() + ".mp4");
    }

    /**
     * getTimeStamp for create temporal file name
     *
     * @return value
     */
    public static long getTimeStamp() {
        long aMilliSecTimeStamp = System.currentTimeMillis();
        return aMilliSecTimeStamp;
    }

    /**
     * createvideoPath for google photo
     *
     * @return value
     */
    public File createvideoPath() {
        String folder_main = "SynChat";
        // File outerFolder = new File(Environment.getExternalStorageDirectory(), folder_main);

        File outerFolder = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH);

        File inerDire = new File(outerFolder.getAbsoluteFile(), System.currentTimeMillis() + ".mp4");
        if (!outerFolder.exists()) {
            outerFolder.mkdirs();
        }
        if (!outerFolder.exists()) {
            try {
                inerDire.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outerFolder;
    }

    /**
     * create File Path
     *
     * @return value
     */
    public File createPath() {
        String folder_main = "SynChat";
        //File outerFolder = new File(Environment.getExternalStorageDirectory(), folder_main);
        File outerFolder = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH);
        File inerDire = new File(outerFolder.getAbsoluteFile(), System.currentTimeMillis() + ".jpg");
        if (!outerFolder.exists()) {
            outerFolder.mkdirs();
        }
        if (!outerFolder.exists()) {
            try {
                inerDire.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return outerFolder;
    }

    /**
     * delete folder
     *
     * @param file
     */
    public void deleteRecursive(File file) {
        ///storage/emulated/0/aChat

        //file = new File(Environment.getExternalStorageDirectory() + "/" + "aChat");
        file = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH);
        if (file.isDirectory()) {
            String[] children = file.list();
            if (children != null) {
                for (int i = 0; i < children.length; i++) {
                    new File(file, children[i]).delete();
                }
            }
        }

        String folder_main1 = "SynChat";
        //File outerFolder1 = new File(Environment.getExternalStorageDirectory(), folder_main1);
        File outerFolder1 = new File(getApplicationContext().getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH_BACKUP);
        if (!outerFolder1.exists()) {
            outerFolder1.mkdirs();
        }

    }

    /**
     * getFileFolderSize
     *
     * @param dir file dir
     * @return value
     */
    public static long getFileFolderSize(File dir) {
        long size = 0;
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isFile()) {
                    size += file.length();
                } else
                    size += getFileFolderSize(file);
            }
        } else if (dir.isFile()) {
            size += dir.length();
        }
        return size;
    }

}