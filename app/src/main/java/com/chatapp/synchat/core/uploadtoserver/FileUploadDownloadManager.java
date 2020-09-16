package com.chatapp.synchat.core.uploadtoserver;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.FileDownloadService;

import org.appspot.apprtc.util.CryptLib;

import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.model.GroupInfoPojo;
import com.chatapp.synchat.core.service.Constants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.chatapp.synchat.app.ChatViewActivity;
import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.message.AudioMessage;
import com.chatapp.synchat.core.message.DocumentMessage;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.message.PictureMessage;
import com.chatapp.synchat.core.message.VideoMessage;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.socket.SocketManager;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import id.zelory.compressor.Compressor;

/**
 * created by  Adhash Team on 1/27/2017.
 */
public class FileUploadDownloadManager {

    private Context mContext;
    private static final String TAG = "FileUploadDownloadManag";
    public static final int FILE_TRANSFER_RATE = 1024 * 350;

    private String mCurrentUserId;
    Boolean RemovePhoto = false;
    private SharedPreferences filePref, uploadFileStatusPref, downloadFileStatusPref, pauseFilePref;
    private SharedPreferences.Editor filePrefEditor, uploadFileStatusPrefEditor, downloadFileStatusPrefEditor, pauseFileEditor;
    private final String FILE_MANAGER_PREF = "FileManagerPref";
    private final String FILE_UPLOAD_STATUS_PREF = "UploadFileStatusPref";
    private final String FILE_DOWNLOAD_STATUS_PREF = "DownloadFileStatusPref";
    private final String FILE_PAUSE_PREF = "FilePausePref";
    private final String KEY_UPLOAD_SIZE = "-uploadSize";
    private final String REMOVE_PHOTO = "RemovePhoto";
    private final String KEY_DOWNLOAD_SIZE = "-downloadSize";
    ChatViewActivity context = null;

    private GroupInfoSession groupInfoSession;
    private UserInfoSession userInfoSession;
    MessageDbController db;

    String finaltest = "";


    boolean image_check = false;
    private Gson gson;

    /**
     * create constructor & inilization of SharedPreferences
     *
     * @param mContext current activity
     */
    public FileUploadDownloadManager(Context mContext) {
        this.mContext = mContext;
        mCurrentUserId = SessionManager.getInstance(mContext).getCurrentUserID();
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder.create();
        filePref = mContext.getSharedPreferences(FILE_MANAGER_PREF, Context.MODE_PRIVATE);
        filePrefEditor = filePref.edit();

        uploadFileStatusPref = mContext.getSharedPreferences(FILE_UPLOAD_STATUS_PREF, Context.MODE_PRIVATE);
        uploadFileStatusPrefEditor = uploadFileStatusPref.edit();

        downloadFileStatusPref = mContext.getSharedPreferences(FILE_DOWNLOAD_STATUS_PREF, Context.MODE_PRIVATE);
        downloadFileStatusPrefEditor = downloadFileStatusPref.edit();
        pauseFilePref = mContext.getSharedPreferences(FILE_PAUSE_PREF, Context.MODE_PRIVATE);


        db = CoreController.getDBInstance(mContext);

        userInfoSession = new UserInfoSession(mContext);
        groupInfoSession = new GroupInfoSession(mContext);
    }

    /**
     * set Listener
     *
     * @param context current activity
     */
    public void setListener(ChatViewActivity context) {
        this.context = context;

    }

    /**
     * set Upload Progress for downloading
     *
     * @param msgId        input value(msgId)
     * @param uploadedSize input value(uploadedSize)
     * @param uploadObj    input value(uploadObj)
     */
    public void setUploadProgress(String msgId, int uploadedSize, JSONObject uploadObj) {
        if (uploadObj.has("buffer")) {
            uploadObj.remove("buffer");
        }

        String uploadKey = msgId + KEY_UPLOAD_SIZE;

        filePrefEditor.putInt(uploadKey, uploadedSize);
        filePrefEditor.putString(msgId, uploadObj.toString());
        filePrefEditor.apply();

        if (uploadedSize > 0) {
            uploadFileStatusPrefEditor.putBoolean(msgId, true);
            uploadFileStatusPrefEditor.apply();
        }
    }


    /**
     * set downloading pause file status
     *
     * @param msgId    input value(msgId)
     * @param isPaused input value(isPaused)
     */
    public void setPauseFileStatus(String msgId, boolean isPaused) {
        if (pauseFilePref != null) {
            pauseFileEditor = pauseFilePref.edit();
            pauseFileEditor.putBoolean(msgId, isPaused);
            pauseFileEditor.apply();
        }
    }

    /**
     * set downloading Pause File from server
     *
     * @param msgId      input value(msgId)
     * @param jsonObject input value(getting value from server)
     */
    public void setPauseFileObject(String msgId, JSONObject jsonObject) {
        if (pauseFilePref != null) {
            pauseFileEditor = pauseFilePref.edit();
            pauseFileEditor.putString("pauseObject_" + msgId, gson.toJson(jsonObject));
            pauseFileEditor.apply();
        }
    }

    /**
     * get downloading Pause File
     *
     * @param msgId input value(msgId)
     * @return value response
     */
    public JSONObject getPauseFileObject(String msgId) {
        if (pauseFilePref != null) {
            String pauseFileObject = pauseFilePref.getString("pauseObject_" + msgId, "");
            if (!pauseFileObject.isEmpty()) {
                return gson.fromJson(pauseFileObject, JSONObject.class);
            }
        }
        return null;
    }

    /**
     * check download file pause or not
     *
     * @param msgId input value(msgId)
     * @return response value
     */
    public boolean isFilePaused(String msgId) {
        if (pauseFilePref != null)
            return pauseFilePref.getBoolean(msgId, false);

        return false;
    }

    public int getUploadProgress(String msgId) {
        String uploadKey = msgId + KEY_UPLOAD_SIZE;

        if (filePref.contains(uploadKey)) {
            return filePref.getInt(uploadKey, 0);
        } else {
            return -1;
        }
    }

    public JSONObject getUploadProgressObject(String msgId) {
        JSONObject object = new JSONObject();
        if (filePref.contains(msgId)) {
            try {
                String msgObj = filePref.getString(msgId, "");
                object = new JSONObject(msgObj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return object;
    }

    /**
     * remove Upload Progress
     *
     * @param msgId input value(msgId)
     */
    public void removeUploadProgress(String msgId) {
        String uploadKey = msgId + KEY_UPLOAD_SIZE;
        filePrefEditor.remove(msgId);
        filePrefEditor.remove(uploadKey);
        filePrefEditor.apply();

        uploadFileStatusPrefEditor.remove(msgId);
        uploadFileStatusPrefEditor.apply();
    }

    /**
     * set Upload Connection Offline status
     */
    public void setUploadConnectionOffline() {
        Map<String, ?> uploadMsgIds = uploadFileStatusPref.getAll();
        for (String key : uploadMsgIds.keySet()) {
            uploadFileStatusPrefEditor.putBoolean(key, false);
            uploadFileStatusPrefEditor.apply();
        }
    }

    /**
     * set Download Connection Offline status
     */
    public void setDownloadConnectionOffline() {
        Map<String, ?> downloadMsgIds = downloadFileStatusPref.getAll();
        for (String key : downloadMsgIds.keySet()) {
            downloadFileStatusPrefEditor.putBoolean(key, false);
            downloadFileStatusPrefEditor.apply();
        }
    }

    /**
     * Check photo remove or not
     *
     * @return response value
     */
    public Boolean isRemovePhoto() {
        return filePref.getBoolean(REMOVE_PHOTO, false);
    }

    /**
     * set Remove Photo from SharedPreferences
     *
     * @param isRemove check remove or not
     */
    public void setRemovePhoto(boolean isRemove) {
        SharedPreferences.Editor prefEditor = filePref.edit();
        prefEditor.putBoolean(REMOVE_PHOTO, isRemove);
        prefEditor.apply();
    }

    /**
     * set Download Progress
     *
     * @param msgId          input value(msgId)
     * @param downloadedSize input value(downloadedSize)
     * @param downloadObj    input value(downloadObj)
     */
    public void setDownloadProgress(String msgId, int downloadedSize, JSONObject downloadObj) {
        downloadFileStatusPrefEditor.putBoolean(msgId, true);
        downloadFileStatusPrefEditor.apply();

        String downKey = msgId + KEY_DOWNLOAD_SIZE;
        filePrefEditor.putString(msgId, downloadObj.toString());
        filePrefEditor.putInt(downKey, downloadedSize);
        filePrefEditor.apply();
    }

    /**
     * get Download Progress
     *
     * @param msgId input value(msgId)
     * @return response value
     */
    public int getDownloadProgress(String msgId) {
        String downKey = msgId + KEY_DOWNLOAD_SIZE;
        return filePref.getInt(downKey, 0);
       /* if (filePref.contains(downKey)) {

        } else {
            return -1;
        }*/
    }

    /**
     * remove Offline Download Status
     *
     * @param msgId input value(msgId)
     */
    public void removeOfflineDownloadStatusNotExist(String msgId) {
        downloadFileStatusPrefEditor.putBoolean(msgId, true);
        downloadFileStatusPrefEditor.apply();
    }

    /**
     * remove Download Progress view
     *
     * @param msgId input value(msgId)
     */
    public void removeDownloadProgress(String msgId) {
        String downKey = msgId + KEY_DOWNLOAD_SIZE;
        filePrefEditor.remove(msgId);
        filePrefEditor.remove(downKey);
        filePrefEditor.apply();

        downloadFileStatusPrefEditor.putBoolean(msgId, true);
        downloadFileStatusPrefEditor.apply();
    }

    public static String getFileExtnFromFile(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return "." + fileName.substring(fileName.lastIndexOf(".") + 1);
        } else {
            return "";
        }
    }

    /**
     * get File Extn From filePath
     *
     * @param fileName input value(fileName)
     * @return response value
     */
    public static String getFileExtnFromPath(String fileName) {
        try {
            if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
                return "." + fileName.substring(fileName.lastIndexOf(".") + 1);
            } else {
                return "";
            }
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * uploading File
     *
     * @param eventBus input value(eventBus)
     * @param object   input value(object)
     */
    public void uploadFile(EventBus eventBus, JSONObject object) {
        try {
            String msgId = object.getString("id");
            setUploadProgress(msgId, 1, object);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SendMessageEvent event = new SendMessageEvent();
        event.setEventName(SocketManager.EVENT_NEW_FILE_MESSAGE);
        event.setMessageObject(object);
        eventBus.post(event);
    }

    /**
     * start Uploading File
     *
     * @param eventBus input value(eventBus)
     * @param object   input value(object)
     */
    public void startFileUpload(EventBus eventBus, JSONObject object) {
        try {
            Log.d(TAG, "startFileUpload: <Status>");
            int msgType = object.getInt("type");

            switch (msgType) {

                case MessageFactory.picture:
                    System.out.println("=====hereimage");
                    uploadImageChatFile(eventBus, object);
                    break;

                case MessageFactory.audio:
                    uploadAudioChatFile(eventBus, object);
                    break;

                case MessageFactory.video:
                    System.out.println("=====here");
                    uploadVideoChatFile(eventBus, object);
                    break;

                case MessageFactory.document:
                    uploadDocumentChatFile(eventBus, object);
                    break;

                case MessageFactory.user_profile_pic_update:
                    uploadUserProfilePic(eventBus, object);
                    break;

                case MessageFactory.group_profile_pic_update:
                    uploadGroupProfilePic(eventBus, object);
                    break;

            }

        } catch (Exception e) {
            Log.e(TAG, "startFileUpload: ", e);
        }
    }

    /**
     * upload Group Profile Picture
     *
     * @param eventBus input value(eventBus)
     * @param object   input value(object)
     */
    private void uploadGroupProfilePic(EventBus eventBus, JSONObject object) {
        try {
            int err = object.getInt("err");
            String from = object.getString("from");
            String imgName = object.getString("ImageName");
            String imgPath = object.getString("LocalPath");
            int type = object.getInt("type");
            int bufferAt = object.getInt("bufferAt");
            String uploadType = object.getString("uploadType");

            if (err == 0) {
                bufferAt += 1;
            }

            File file = new File(imgPath);
            File compressFile = new Compressor(mContext).compressToFile(file);

            byte[] bytesArray = new byte[(int) compressFile.length()];

            FileInputStream fis = new FileInputStream(compressFile);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();

            //    byte[] bytesArray=encrypt(bytesArrayTemp);


            byte[][] divideArray = divideArray(bytesArray, FILE_TRANSFER_RATE);

            if (bufferAt < divideArray.length) {

                SendMessageEvent event = new SendMessageEvent();
                try {
                    JSONObject msgObj = new JSONObject();

                    msgObj.put("ImageName", imgName);
                    msgObj.put("buffer", divideArray[bufferAt]);
                    msgObj.put("bufferAt", bufferAt);
                    msgObj.put("LocalPath", imgPath);
                    msgObj.put("type", type);
                    msgObj.put("from", from);
                    msgObj.put("uploadType", uploadType);

                    if (bufferAt == (divideArray.length - 1)) {
                        msgObj.put("FileEnd", 1);
                    } else {
                        msgObj.put("FileEnd", 0);
                    }

                    event.setEventName(SocketManager.EVENT_FILE_UPLOAD);
                    event.setMessageObject(msgObj);
                    eventBus.post(event);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                SendMessageEvent imgEvent = new SendMessageEvent();
                imgEvent.setEventName(SocketManager.EVENT_IMAGE_UPLOAD);

                try {
                    JSONObject imgObject = new JSONObject();
//                    imgObject.put("file", imageEncoded);
                    imgObject.put("from", mCurrentUserId);
                    imgObject.put("type", "group");
                    imgObject.put("ImageName", imgName);


                    imgEvent.setMessageObject(imgObject);
                    eventBus.post(imgEvent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Cannot read file: " + e.toString());
        }
    }

    /**
     * uploadinf User Profile Picture
     *
     * @param eventBus input value(eventBus)
     * @param object   input value(object)
     */
    private void uploadUserProfilePic(EventBus eventBus, JSONObject object) {
        try {
            int err = object.getInt("err");
            String from = object.getString("from");
            String imgName = object.getString("ImageName");
            String imgPath = object.getString("LocalPath");
            int type = object.getInt("type");
            int bufferAt = object.getInt("bufferAt");
            String uploadType = object.getString("uploadType");


            if (err == 0) {
                bufferAt += 1;
            }

            File file = new File(imgPath);
            File compressFile = new Compressor(mContext).compressToFile(file);

            byte[] bytesArray = new byte[(int) compressFile.length()];

            FileInputStream fis = new FileInputStream(compressFile);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();


            // byte[] bytesArray=bytesArrayTemp;

            byte[][] divideArray = divideArray(bytesArray, FILE_TRANSFER_RATE);

            if (bufferAt < divideArray.length) {

                SendMessageEvent event = new SendMessageEvent();
                try {
                    JSONObject msgObj = new JSONObject();

                    msgObj.put("ImageName", imgName);
                    msgObj.put("buffer", divideArray[bufferAt]);
                    msgObj.put("bufferAt", bufferAt);
                    msgObj.put("LocalPath", imgPath);
                    msgObj.put("type", type);
                    msgObj.put("from", from);
                    msgObj.put("uploadType", uploadType);
                    msgObj.put("removePhoto", "");


                    if (bufferAt == (divideArray.length - 1)) {
                        msgObj.put("FileEnd", 1);
                    } else {
                        msgObj.put("FileEnd", 0);
                    }

                    event.setEventName(SocketManager.EVENT_FILE_UPLOAD);
                    event.setMessageObject(msgObj);
                    eventBus.post(event);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                SendMessageEvent imgEvent = new SendMessageEvent();
                imgEvent.setEventName(SocketManager.EVENT_IMAGE_UPLOAD);

                try {
                    JSONObject imgObject = new JSONObject();
//                    imgObject.put("file", imageEncoded);
                    imgObject.put("from", mCurrentUserId);
                    imgObject.put("type", "single");
                    imgObject.put("ImageName", imgName);
                    imgObject.put("removePhoto", "");

                    imgEvent.setMessageObject(imgObject);
                    eventBus.post(imgEvent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        } catch (Exception e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }
    }

    /**
     * update user Profile picture
     *
     * @param eventBus input value(eventBus)
     */
    public void updatePropic(EventBus eventBus) {
        SendMessageEvent imgEvent = new SendMessageEvent();
        imgEvent.setEventName(SocketManager.EVENT_IMAGE_UPLOAD);

        try {
            JSONObject imgObject = new JSONObject();
//                    imgObject.put("file", imageEncoded);
            imgObject.put("from", mCurrentUserId);
            imgObject.put("type", "single");
//            imgObject.put("ImageName", mCurrentUserId+"-"+System.currentTimeMillis());
            if (isRemovePhoto()) {
                imgObject.put("removePhoto", "yes");
            }

            imgEvent.setMessageObject(imgObject);
            eventBus.post(imgEvent);
        } catch (Exception ex) {
            Log.e(TAG, "updatePropic: ", ex);
        }
    }

    /**
     * encryption for chat data
     *
     * @param bytesArray input value(bytesArray)
     * @param docid      input value(docid)
     * @return response value
     */
    private byte[] encrypt(byte[] bytesArray, String docid) {

        CryptLib cryptLib = null;
        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        byte[] bytesEnc = new byte[0];
        try {


            String valuable = getValuable(docid);


            bytesEnc = cryptLib.encryptDecrypt(bytesArray, mContext.getResources().getString(R.string.chatapp) + valuable + mContext.getResources().getString(R.string.adani), CryptLib.EncryptMode.ENCRYPT, cryptLib.generateRandomIV16());


            System.out.println("==byteencrypt" + bytesEnc);


            System.out.println("==byteencryptarray" + bytesArray);


            //       bytesEnc = cryptLib.encryptDecrypt(bytesArray, Constants.DUMMY_KEY,  CryptLib.EncryptMode.ENCRYPT,cryptLib.generateRandomIV16());


//            bytesEnc=cryptLib.encryptPlainTextWithBytearray(Base64.encodeToString(bytesArray, Base64.DEFAULT),Constants.DUMMY_KEY);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytesEnc;

    }

    /**
     * get Video Thumbnail data
     *
     * @param videoPath input value(videoPath)
     * @param docid     input value(docid)
     * @return response value
     */
    private String getVideoThumbnail(String videoPath, String docid) {
        String thumbData = null;

        Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        thumbBmp.compress(Bitmap.CompressFormat.PNG, 10, out);
        byte[] thumbArray = out.toByteArray();
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String valuable = getValuable(docid);
        thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
        System.out.println("===data" + thumbData);
        if (thumbData != null) {
            CryptLib cryptLib = null;
            try {
                cryptLib = new CryptLib();
                thumbData = cryptLib.encryptPlainTextWithRandomIV(thumbData, mContext.getResources().getString(R.string.chatapp) + valuable + mContext.getResources().getString(R.string.adani));

                System.out.println("===dataafterencryption" + thumbData);

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return thumbData;
    }

    /**
     * encryption for thumbnail Data
     *
     * @param thumbnailData input value(thumbnailData)
     * @param docid         input value(docid)
     * @return response value
     */
    private String encrypt(String thumbnailData, String docid) {

        CryptLib cryptLib = null;
        try {
            cryptLib = new CryptLib();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        String bytesEnc = "";
        try {


            String valuable = getValuable(docid);


            bytesEnc = cryptLib.encryptPlainTextWithRandomIV(thumbnailData, mContext.getResources().getString(R.string.chatapp) + valuable + mContext.getResources().getString(R.string.adani));


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytesEnc;

    }

    /**
     * get group value / user value based on docid
     *
     * @param docid input value(docid)
     * @return response value
     */
    public String getValuable(String docid) {
        String valuable = "";

        if (db.isGroupId(docid)
        ) {

            if (groupInfoSession.hasGroupInfo(docid)) {
                GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docid);
                valuable = infoPojo.getGroupId();

            }
        } else {
            if (userInfoSession.hasChatConvId(docid)) {
                valuable = userInfoSession.getChatConvId(docid);
            }
        }
        return valuable;

    }

    /**
     * uploading Image File for Chat
     *
     * @param eventBus input value(eventBus)
     * @param object   input value(object)
     */
    private void uploadImageChatFile(EventBus eventBus, JSONObject object) {

        try {
            System.gc();
            int err = object.getInt("err");
            int bufferAt = object.getInt("bufferAt");
            String imgPath = object.getString("LocalPath");
            String docid = object.getString("docId");

//            database.updateMessageBufferCount(docId, msgId, bufferAt);

            if (err == 0) {
                bufferAt += 1;
            }

            if (bufferAt == 0) {
                try {
                    String msgId = object.getString("toDocId");
                    CoreController.getDBInstance(mContext).deleteSendNewMessage(msgId);
                } catch (Exception e) {
                    Log.e(TAG, "uploadImageChatFile: ", e);
                }

            }

            File file = new File(imgPath);

            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inSampleSize = 8;
            Bitmap compressBmp = BitmapFactory.decodeFile(file.getPath(), bmOptions);
            if (compressBmp == null) {
                String[] fullPath = file.getPath().split("/");
                String originalFileName = fullPath[fullPath.length - 1];
                String publicDirPath = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath();
                file = new File(publicDirPath + "/" + originalFileName);
            } else {
                try {
                    File compressedFile = new Compressor(mContext).compressToFile(file);
                    if (compressedFile != null)
                        file = compressedFile;
                } catch (Exception e) {
                    Log.d(TAG, "uploadImageChatFile: ", e);
                }
            }


            byte[] bytesArrayTemp = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArrayTemp); //read file into bytes[]
            fis.close();


            byte[] bytesArray = encrypt(bytesArrayTemp, docid);


            byte[][] divideArray = divideArray(bytesArray, FILE_TRANSFER_RATE);

            if (bufferAt < divideArray.length) {
                if (bufferAt >= 0) {
                    try {
                        String msgId = object.getString("toDocId");
                        setUploadProgress(msgId, bufferAt * divideArray[bufferAt].length, object);
                    } catch (Exception e) {
                        Log.e(TAG, "uploadImageChatFile: ", e);
                    }
                }

                SendMessageEvent event = new SendMessageEvent();
                try {
                    object.put("buffer", divideArray[bufferAt]);
                    object.put("bufferAt", bufferAt);
                    object.put("UploadedSize", bufferAt * divideArray[bufferAt].length);

                    if (bufferAt == (divideArray.length - 1)) {
                        object.put("FileEnd", 1);
                    } else {
                        object.put("FileEnd", 0);
                    }

                    event.setEventName(SocketManager.EVENT_FILE_UPLOAD);
                    event.setMessageObject(object);
                    eventBus.post(event);
                    System.out.println("ImageUploadTimes" + " " + "Uploading");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("ImageUploadTimes" + " " + "Success");
                String msgId = object.getString("id");
                /*String docId = object.getString("docId");
                String from = object.getString("from");
                String to = object.getString("to");
                String imgName = object.getString("ImageName");*/

                removeUploadProgress(msgId);

                String receiverName = "";
                if (object.has("ReceiverName"))
                    receiverName = object.getString("ReceiverName");
                String chatType = object.getString("chat_type");
                String caption = object.getString("payload");

                SendMessageEvent sendEvent = new SendMessageEvent();

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(imgPath, options);
                int imageHeight = options.outHeight;
                int imageWidth = options.outWidth;

                try {
                    PictureMessage message = new PictureMessage(mContext);
                    JSONObject msgObj = null;
                    String fileName = object.getString("filename");
                    if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                        sendEvent.setEventName(SocketManager.EVENT_GROUP);

                        msgObj = (JSONObject) message.getGroupPictureMessageObject(caption, msgId, bytesArray.length,
                                imageWidth, imageHeight, fileName, receiverName);

                        msgObj.put("thumbnail_data", getThumbnailData(imgPath, docid));
                        if (object.has("is_expiry")) {
                            msgObj.put("is_expiry", object.getString("is_expiry"));
                        }
                        if (object.has("expiry_time")) {
                            msgObj.put("expiry_time", object.getString("expiry_time"));
                        }

                    } else if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_CELEBRITY)) {

                    } else if (chatType.equals(MessageFactory.CHAT_TYPE_CELEBRITY_VIDEO_THUMB)) {

                    } else {
                        String secretType = object.getString("secret_type");
                        boolean isSecretChat = false;
                        if (secretType.equalsIgnoreCase("yes")) {
                            isSecretChat = true;
                        }

                        msgObj = (JSONObject) message.getPictureMessageObject(caption, msgId, bytesArray.length,
                                imageWidth, imageHeight, fileName, isSecretChat);

                        msgObj.put("thumbnail_data", getThumbnailData(imgPath, docid));


                        if (object.has("is_expiry")) {
                            msgObj.put("is_expiry", object.getString("is_expiry"));
                        }
                        if (object.has("expiry_time")) {
                            msgObj.put("expiry_time", object.getString("expiry_time"));
                        }

                        sendEvent.setEventName(SocketManager.EVENT_MESSAGE);
                    }

                    sendEvent.setMessageObject(msgObj);
                    EventBus.getDefault().post(sendEvent);

                } catch (JSONException e) {
                    Log.e(TAG, "uploadImageChatFile: ", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Can not read file: ", e);
        }
    }


    /**
     * get Thumbnail Data for image
     *
     * @param imgPath input value(imgPath)
     * @param docid   input value(docid)
     * @return response value
     */
    private String getThumbnailData(String imgPath, String docid) {

        File file = new File(imgPath);

        try {
            file = new Compressor(mContext).compressToFile(file);
        } catch (Exception e) {
            Log.e(TAG, "createImageUploadObject: ", e);
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Bitmap compressBmp = null;
        try {
            compressBmp = new Compressor(mContext).compressToBitmap(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int height = compressBmp.getHeight();
        int width = compressBmp.getWidth();
        compressBmp = Bitmap.createScaledBitmap(compressBmp, 100, 100, false);
        compressBmp.compress(Bitmap.CompressFormat.JPEG, 30, out);

        byte[] thumbArray = out.toByteArray();

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);


//        if (!thumbData.startsWith("data:image/jpeg;base64,")) {
//            thumbData = "data:image/jpeg;base64," + thumbData;
//        }
        return encrypt(thumbData, docid);
    }

    /**
     * uploading Audio File for Chat
     *
     * @param eventBus input value(eventBus)
     * @param object   input value(object)
     */
    private void uploadAudioChatFile(EventBus eventBus, JSONObject object) {

        try {
            int err = object.getInt("err");
            String msgId = object.getString("id");
            String audioPath = object.getString("LocalPath");
            String docid = object.getString("docId");

            int bufferAt = object.getInt("bufferAt");

            if (err == 0) {
                bufferAt += 1;
            }

            if (bufferAt == 0) {
                CoreController.getDBInstance(mContext).deleteSendNewMessage(msgId);
            }

            File file = new File(audioPath);
            byte[] bytesArrayTemp = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArrayTemp); //read file into bytes[]
            fis.close();

            byte[] bytesArray = encrypt(bytesArrayTemp, docid);


            byte[][] divideArray = divideArray(bytesArray, FILE_TRANSFER_RATE);

            if (bufferAt < divideArray.length) {

                if (bufferAt >= 0) {
                    setUploadProgress(msgId, bufferAt * divideArray[bufferAt].length, object);
                }

                try {
                    object.put("buffer", divideArray[bufferAt]);
                    object.put("bufferAt", bufferAt);
                    object.put("UploadedSize", bufferAt * divideArray[bufferAt].length);

                    if (bufferAt == (divideArray.length - 1)) {
                        object.put("FileEnd", 1);
                    } else {
                        object.put("FileEnd", 0);
                    }

                    SendMessageEvent event = new SendMessageEvent();
                    event.setEventName(SocketManager.EVENT_FILE_UPLOAD);
                    event.setMessageObject(object);
                    eventBus.post(event);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                try {
//                    String msgId = object.getString("id");
                    String duration = object.getString("Duration");
                    String receiverName = object.getString("ReceiverName");
                    String chatType = object.getString("chat_type");
                    int audioFrom = object.getInt("audio_type");

//                    String audioName = object.getString("ImageName");

                    removeUploadProgress(msgId);

                    AudioMessage message = new AudioMessage(mContext);
                    JSONObject msgObj;
                    String fileName = object.getString("filename");

                    SendMessageEvent sendEvent = new SendMessageEvent();

                    if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                        sendEvent.setEventName(SocketManager.EVENT_GROUP);

                        msgObj = (JSONObject) message.getGroupAudioMessageObject(msgId, bytesArray.length,
                                duration, fileName, receiverName, audioFrom);

                        if (object.has("is_expiry")) {
                            msgObj.put("is_expiry", object.getString("is_expiry"));
                        }
                        if (object.has("expiry_time")) {
                            msgObj.put("expiry_time", object.getString("expiry_time"));
                        }

                    } else {
                        String secretType = object.getString("secret_type");
                        boolean isSecretChat = false;
                        if (secretType.equalsIgnoreCase("yes")) {
                            isSecretChat = true;
                        }

                        msgObj = (JSONObject) message.getAudioMessageObject(msgId, bytesArray.length,
                                duration, fileName, audioFrom, isSecretChat);
                        if (object.has("is_expiry")) {
                            msgObj.put("is_expiry", object.getString("is_expiry"));

                        }
                        if (object.has("expiry_time")) {
                            msgObj.put("expiry_time", object.getString("expiry_time"));
                        }
                        sendEvent.setEventName(SocketManager.EVENT_MESSAGE);
                    }

                    sendEvent.setMessageObject(msgObj);
                    EventBus.getDefault().post(sendEvent);

                } catch (JSONException e) {
                    Log.e(TAG, "uploadAudioChatFile: ", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }
    }

    /**
     * upload Video File for Chat
     *
     * @param eventBus input value(eventBus)
     * @param object   input value(object)
     */
    private void uploadVideoChatFile(EventBus eventBus, JSONObject object) {
        try {
            int err = object.getInt("err");
            String msgId = object.getString("id");
            int bufferAt = object.getInt("bufferAt");
            String videoPath = object.getString("LocalPath");
            String docid = object.getString("docId");


            if (err == 0) {
                bufferAt += 1;
            }

            if (bufferAt == 0) {
                CoreController.getDBInstance(mContext).deleteSendNewMessage(msgId);
            }

            File file = new File(videoPath);
            byte[] bytesArrayTemp = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArrayTemp); //read file into bytes[]
            fis.close();

            byte[] bytesArray = encrypt(bytesArrayTemp, docid);

            byte[][] divideArray = divideArray(bytesArray, FILE_TRANSFER_RATE);

            if (bufferAt < divideArray.length) {

                if (bufferAt >= 0) {
                    setUploadProgress(msgId, bufferAt * divideArray[bufferAt].length, object);
                }

                try {
                    try {
                        object.put("UploadedSize", bufferAt * divideArray[bufferAt].length);
                        object.put("buffer", divideArray[bufferAt]);
                        object.put("bufferAt", bufferAt);
                    } catch (Exception e) {
                        Log.e(TAG, "uploadVideoChatFile: ", e);
                        bufferAt = 0;
                        object.put("UploadedSize", bufferAt * divideArray[bufferAt].length);
                        object.put("buffer", divideArray[bufferAt]);
                        object.put("bufferAt", bufferAt);
                    }
                    if (bufferAt == (divideArray.length - 1)) {
                        object.put("FileEnd", 1);
                    } else {
                        object.put("FileEnd", 0);
                        ChatViewActivity.progressglobal = 0;

                    }

                    SendMessageEvent event = new SendMessageEvent();
                    event.setEventName(SocketManager.EVENT_FILE_UPLOAD);
                    event.setMessageObject(object);
                    eventBus.post(event);

                    System.out.println("--------uploading----------");

                } catch (Exception e) {
                    Log.e(TAG, "uploadVideoChatFile: ", e);
                }
            } else {

                System.out.println("====heremoredata");

                removeUploadProgress(msgId);

                String chatType = object.getString("chat_type");
                String receiverName = object.getString("ReceiverName");
                String caption = object.getString("payload");

                MediaMetadataRetriever mdr = new MediaMetadataRetriever();
                mdr.setDataSource(videoPath);
                int videoHeight = AppUtils.parseInt(mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
                int videoWidth = AppUtils.parseInt(mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
                String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

//                Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
//                ByteArrayOutputStream out = new ByteArrayOutputStream();
//                if(thumbBmp!=null)
//                    thumbBmp.compress(Bitmap.CompressFormat.JPEG, 25, out);
////                Bitmap compressBmp = Compressor.getDefault(mContext).compressToBitmap(thumbBmp);
//                byte[] thumbArray = out.toByteArray();
//                out.close();
//                String thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
//                thumbData = thumbData.replace("\n", "");
//                thumbData = thumbData.replace(" ", "");
////                if (!thumbData.startsWith("data:image/jpeg;base64,")) {
////                    thumbData = "data:image/jpeg;base64," + thumbData.trim();
////                }

                String videothumbnail = getVideoThumbnail(videoPath, docid);

                try {
                    VideoMessage message = new VideoMessage(mContext);
                    JSONObject msgObj = null;
                    String fileName = object.getString("filename");

                    SendMessageEvent sendEvent = new SendMessageEvent();
                    if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_CELEBRITY)) {

                    } else if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                        sendEvent.setEventName(SocketManager.EVENT_GROUP);

                        msgObj = (JSONObject) message.getGroupVideoMessageObject(msgId, bytesArray.length,
                                videothumbnail, videoWidth, videoHeight, duration, fileName, receiverName, caption);

                        if (object.has("is_expiry")) {
                            msgObj.put("is_expiry", object.getString("is_expiry"));
                        }
                        if (object.has("expiry_time")) {
                            msgObj.put("expiry_time", object.getString("expiry_time"));
                        }

                    } else {
                        String secretType = object.getString("secret_type");
                        boolean isSecretChat = false;
                        if (secretType.equalsIgnoreCase("yes")) {
                            isSecretChat = true;
                        }

                        msgObj = (JSONObject) message.getVideoMessageObject(msgId, bytesArray.length,
                                videothumbnail, videoWidth, videoHeight, duration, fileName, caption, isSecretChat, false, "");
                        if (object.has("is_expiry")) {
                            msgObj.put("is_expiry", object.getString("is_expiry"));

                        }
                        if (object.has("expiry_time")) {
                            msgObj.put("expiry_time", object.getString("expiry_time"));
                        }
                        sendEvent.setEventName(SocketManager.EVENT_MESSAGE);
                    }

                    sendEvent.setMessageObject(msgObj);
                    EventBus.getDefault().post(sendEvent);

                } catch (JSONException e) {
                    Log.e(TAG, "uploadVideoChatFile: ", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "uploadVideoChatFile: ", e);
        }
    }

    /**
     * upload Document File for Chat
     *
     * @param eventBus input value(eventBus)
     * @param object   input value(object)
     */
    private void uploadDocumentChatFile(EventBus eventBus, JSONObject object) {
        try {
            int err = object.getInt("err");
            int bufferAt = object.getInt("bufferAt");
            String docPath = object.getString("LocalPath");
            String docid = object.getString("docId");


//            database.updateMessageBufferCount(docId, msgId, bufferAt);

            if (err == 0) {
                bufferAt += 1;
            }

            if (bufferAt == 0) {
                try {
                    String msgId = object.getString("toDocId");
                    CoreController.getDBInstance(mContext).deleteSendNewMessage(msgId);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            File file = new File(docPath);
            byte[] bytesArrayTemp = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArrayTemp); //read file into bytes[]
            fis.close();

            byte[] bytesArray = encrypt(bytesArrayTemp, docid);

            byte[][] divideArray = divideArray(bytesArray, FILE_TRANSFER_RATE);

            if (bufferAt < divideArray.length) {

                SendMessageEvent event = new SendMessageEvent();
                try {
                    object.put("UploadedSize", bufferAt * divideArray[bufferAt].length);
                    object.put("buffer", divideArray[bufferAt]);
                    object.put("bufferAt", bufferAt);

                    if (bufferAt == (divideArray.length - 1)) {
                        object.put("FileEnd", 1);
                    } else {
                        object.put("FileEnd", 0);
                    }

                    event.setEventName(SocketManager.EVENT_FILE_UPLOAD);
                    event.setMessageObject(object);
                    eventBus.post(event);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                SendMessageEvent sendEvent = new SendMessageEvent();

                try {
                    String msgId = object.getString("id");
                    String receiverName = object.getString("ReceiverName");
                    String chatType = object.getString("chat_type");

                    /*String docId = object.getString("docId");
                    String docName = object.getString("ImageName");
                    int type = object.getInt("type");
                    String from = object.getString("from");
                    String to = object.getString("to");*/

                    removeUploadProgress(msgId);


                    DocumentMessage message = new DocumentMessage(mContext);
                    JSONObject msgObj;
                    String fileName = object.getString("filename");
                    String[] fullPath = docPath.split("/");
                    String originalFileName = fullPath[fullPath.length - 1];

                    if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
                        sendEvent.setEventName(SocketManager.EVENT_GROUP);

                        msgObj = (JSONObject) message.getGroupDocumentMessageObject(msgId, bytesArray.length,
                                originalFileName, docPath, fileName, receiverName);

                        if (object.has("is_expiry")) {
                            msgObj.put("is_expiry", object.getString("is_expiry"));
                        }
                        if (object.has("expiry_time")) {
                            msgObj.put("expiry_time", object.getString("expiry_time"));
                        }


                    } else {
                        String secretType = object.getString("secret_type");
                        boolean isSecretChat = false;
                        if (secretType.equalsIgnoreCase("yes")) {
                            isSecretChat = true;
                        }

                        msgObj = (JSONObject) message.getDocumentMessageObject(msgId, bytesArray.length,
                                originalFileName, docPath, fileName, isSecretChat);
                        if (object.has("is_expiry")) {
                            msgObj.put("is_expiry", object.getString("is_expiry"));

                        }
                        if (object.has("expiry_time")) {
                            msgObj.put("expiry_time", object.getString("expiry_time"));
                        }
                        sendEvent.setEventName(SocketManager.EVENT_MESSAGE);
                    }

                    sendEvent.setMessageObject(msgObj);
                    EventBus.getDefault().post(sendEvent);

                } catch (JSONException e) {
                    Log.e(TAG, "uploadDocumentChatFile: ", e);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Can not read file: " + e);
        }
    }

    /**
     * start File Downloading
     *
     * @param eventBus     input value(eventBus)
     * @param msgItem      input value(msgItem)
     * @param isSecretChat input value(boolean value)
     */
    public void startFileDownload(EventBus eventBus, MessageItemChat msgItem, boolean isSecretChat) {

        String[] fullPath = msgItem.getChatFileServerPath().split("/");
        String thumbnail = fullPath[fullPath.length - 1];

        String id;
        String docId;
        String[] splitIds = msgItem.getMessageId().split("-");

        if (msgItem.getMessageId().contains("-g")) {
            id = splitIds[3];
            docId = mCurrentUserId + "-" + splitIds[1] + "-g";
        } else {
            id = splitIds[2];
            docId = mCurrentUserId + "-" + splitIds[1];
        }

        String dataSize = msgItem.getFileSize();
        //  File dir = new File(MessageFactory.BASE_STORAGE_PATH);

        File dir = new File(mContext.getFilesDir().toString());
        if (!dir.exists()) {
            dir.mkdir();
        }

        String chatTypePath, publicDirPath = "";
        ;
        int msgType = Integer.parseInt(msgItem.getMessageType());
        File fileDir;

        switch (msgType) {
            case MessageFactory.picture:
                chatTypePath = mContext.getFilesDir() + MessageFactory.IMAGE_STORAGE_PATH;
                System.out.println("===datachatpath" + chatTypePath);
                fileDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES), chatTypePath);
                System.out.println("===datafiledir" + fileDir);
                publicDirPath = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_PICTURES).getAbsolutePath();
                System.out.println("===datapublicpath" + publicDirPath);
                break;

            case MessageFactory.document:
                chatTypePath = mContext.getFilesDir() + MessageFactory.DOCUMENT_STORAGE_PATH;
               /* if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    fileDir = new File(Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS), chatTypePath);
                    publicDirPath = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
                } else {
                    fileDir = new File(MessageFactory.DOCUMENT_STORAGE_PATH);
                }*/

                fileDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS), chatTypePath);
                publicDirPath = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
                break;

            case MessageFactory.video:
                chatTypePath = mContext.getFilesDir() + MessageFactory.VIDEO_STORAGE_PATH;
                fileDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES), chatTypePath);
                publicDirPath = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MOVIES).getAbsolutePath();
                break;

            default:
                chatTypePath = mContext.getFilesDir() + MessageFactory.AUDIO_STORAGE_PATH;
                fileDir = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC), chatTypePath);
                publicDirPath = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC).getAbsolutePath();
                break;
        }

//        fileDir = new File(chatTypePath);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        File chatDir = new File(chatTypePath);
        if (!chatDir.exists()) {
            chatDir.mkdirs();
        }

        String localFileName = MessageFactory.getMessageFileName(msgType, id,
                FileUploadDownloadManager.getFileExtnFromPath(thumbnail));
        String filePath = chatTypePath + localFileName;

        if (thumbnail != null && !thumbnail.equals("")) {

            File file = new File(filePath);
            if (!file.exists()) {
                try {
                    try {
                        file.createNewFile();
                    } catch (Exception e) {
                        Log.d(TAG, "startFileDownload: file path error.. ");
                        try {
                            file = new File(publicDirPath, localFileName);

                            file.createNewFile();
                            filePath = publicDirPath + File.separator + localFileName;
                            Log.d(TAG, "startFileDownload: " + filePath);
                            System.out.println("===datastartdownload" + filePath);

                        } catch (Exception e1) {
                            Log.e(TAG, "startFileDownload 22: ", e1);
                        }
                    }

                    System.out.println("==beforeFILEURL" + msgItem.getChatFileServerPath());

                    String test = msgItem.getChatFileServerPath();

                    if (test.contains("../uploads")) {
                        finaltest = msgItem.getChatFileServerPath().replaceAll("../uploads", "/uploads");
                    } else {
                        finaltest = msgItem.getChatFileServerPath().replaceAll("./uploads", "/uploads");
                    }

                    String localServerPath = finaltest;

                    System.out.println("==afterFILEURL" + localServerPath);


                    String convID = "";

                    if (msgItem.isGroup()) {
                        convID = msgItem.getReceiverID();
                    } else {
                        convID = msgItem.getConvId();
                    }

                    FileDownloadService.startDownloadService(mContext, Constants.SOCKET_IP_BASE + localServerPath, filePath, docId, docId + "-" + id, convID);

                    System.out.println("==FILEURL" + Constants.SOCKET_IP_BASE + localServerPath);


//                    new DownloadingTask(Constants.SOCKET_IP_BASE+localServerPath,filePath,docId,docId + "-" + id).execute();

//                    SendMessageEvent event = new SendMessageEvent();
//                    JSONObject eventObj = new JSONObject();
//                    eventObj.put("MsgId", docId + "-" + id);
//                    if (isSecretChat) {
//                        docId = docId + "-" + MessageFactory.CHAT_TYPE_SECRET;
//                    }
//                    eventObj.put("DocId", docId);
//                    eventObj.put("ImageName", thumbnail);
//                    eventObj.put("LocalPath", filePath);
//                    eventObj.put("LocalFileName", localFileName);
//                    eventObj.put("start", 0);
//                    eventObj.put("filesize", dataSize);
//                    eventObj.put("bytesRead", 0);
//                   /* eventObj.put("UploadedSize", 0);
//                    eventObj.put("TotalSize", msgItem.getFileSize());*/
//
//                    event.setEventName(SocketManager.EVENT_FILE_DOWNLOAD);
//                    event.setMessageObject(eventObj);
//                    eventBus.post(event);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                file.delete();
                startFileDownload(eventBus, msgItem, isSecretChat);
            }
        }
    }

    /**
     * divide Array for rating
     *
     * @param source    input value(eventBus)
     * @param chunksize input value(chunksize)
     * @return response value
     */
    public byte[][] divideArray(byte[] source, int chunksize) {
        System.gc();
        byte[][] ret = new byte[(int) Math.ceil(source.length / (double) chunksize)][chunksize];

       /* for(int i = 0; i < ret.length; i++) {
            ret[i] = Arrays.copyOfRange(source, from, from + chunksize);
            from += chunksize ;
        }*/

        int len = source.length;
        int counter = 0;
        for (int i = 0; i < len - chunksize + 1; i += chunksize)
            ret[counter++] = Arrays.copyOfRange(source, i, i + chunksize);

        if (len % chunksize != 0)
            ret[counter] = Arrays.copyOfRange(source, len - len % chunksize, len);

        return ret;
    }


    /**
     * start Server to Request File Uploading
     *
     * @param eventBus input value(eventBus)
     * @param object   input value(object)
     */
    public void startServerRequestFileUpload(EventBus eventBus, JSONObject object) {
        try {
            int err = object.getInt("err");
            String imgName = object.getString("ImageName");
            String fileName = object.getString("filename");
            int msgType = Integer.parseInt(object.getString("type"));
            String requestUserId = object.getString("requestUser");
            int bufferAt = object.getInt("bufferAt");
            int fileEnd = object.getInt("FileEnd");
            String msgId = object.getString("msgId");
            String convId = object.getString("convId");
            String recordId = object.getString("recordId");

            if (requestUserId.equalsIgnoreCase(mCurrentUserId) && fileEnd == 0) {

                String localFileName = MessageFactory.getMessageFileName(msgType, msgId,
                        FileUploadDownloadManager.getFileExtnFromPath(imgName));

                JSONObject sendObj = new JSONObject();
                sendObj.put("from", mCurrentUserId);
                sendObj.put("requestUser", mCurrentUserId);
                sendObj.put("ImageName", imgName);
                sendObj.put("send", 1);
                sendObj.put("type", msgType);
                sendObj.put("msgId", msgId);
                sendObj.put("convId", convId);
                sendObj.put("recordId", recordId);

                System.out.println("===" + convId);

                if (msgType == MessageFactory.picture) {
                    // localFileName = MessageFactory.IMAGE_STORAGE_PATH + localFileName;

                    localFileName = mContext.getFilesDir() + MessageFactory.IMAGE_STORAGE_PATH + localFileName;
                }

                File file = new File(localFileName);

                if (file.exists()) {
                    sendObj.put("fileExits", 1);
                    if (err == 0) {
                        bufferAt += 1;
                    }

                    if (msgType == MessageFactory.picture) {
                        try {
                            file = new Compressor(mContext).compressToFile(file);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    byte[] bytesArray = new byte[(int) file.length()];

                    try {
                        FileInputStream fis = new FileInputStream(file);
                        fis.read(bytesArray); //read file into bytes[]
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    byte[][] divideArray = divideArray(bytesArray, FILE_TRANSFER_RATE);

                    if (bufferAt < divideArray.length) {
                        try {
                            sendObj.put("buffer", divideArray[bufferAt]);
                            sendObj.put("bufferAt", bufferAt);
                            sendObj.put("filename", fileName);
                            sendObj.put("FileEnd", 0);

                            if (bufferAt == (divideArray.length - 1)) {
                                sendObj.put("FileEnd", 1);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    sendObj.put("fileExits", 0);
                }

                SendMessageEvent event = new SendMessageEvent();
                event.setEventName(SocketManager.EVENT_PHONE_DOWNLOAD);
                event.setMessageObject(sendObj);
                eventBus.post(event);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * upload Pending Files
     *
     * @param eventBus input value(eventBus)
     */
    public void uploadPendingFiles(EventBus eventBus) {

        Map<String, ?> pendingFiles = uploadFileStatusPref.getAll();
        for (String key : pendingFiles.keySet()) {
            boolean uploadInProgress = uploadFileStatusPref.getBoolean(key, true);
            if (!uploadInProgress) {
                String values = filePref.getString(key, "");

                try {
                    JSONObject object = new JSONObject(values);
                /*Log.d("get_file_obj", values);
                String fileName = object.getString("ImageName");
                object = new JSONObject();
                object.put("ImageName", fileName);*/
                    SendMessageEvent event = new SendMessageEvent();
                    if (object.has("celebrity")) {
                        System.out.println("-------type------" + " " + "celeb");
                    } else {
                        event.setEventName(SocketManager.EVENT_GET_UPLOADED_FILE_SIZE);
                        event.setMessageObject(object);
                        eventBus.post(event);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "uploadPendingFiles: ", e);
                }
            }
        }

    }

    /**
     * resume File Uploading
     *
     * @param eventBus input value(eventBus)
     * @param object   input value(object)
     */
    public void resumeFileUpload(EventBus eventBus, JSONObject object) {
        startFileUpload(eventBus, object);
    }

    /**
     * downloading Pending Files
     *
     * @param eventBus input value(eventBus)
     */
    public void downloadPendingFiles(EventBus eventBus) {
        Map<String, ?> pendingFiles = downloadFileStatusPref.getAll();
        for (String key : pendingFiles.keySet()) {
            boolean downloadInProgress = downloadFileStatusPref.getBoolean(key, true);
            if (!downloadInProgress) {
                String values = filePref.getString(key, "");

                try {
                    JSONObject object = new JSONObject(values);
                    if (object.has("LocalPath")) {
                        File file = new File(object.getString("LocalPath"));
                        if (file.exists()) {
                            long downloadedSize = file.length();
                            object.put("bytesRead", downloadedSize);
                            object.put("start", downloadedSize);
                            SendMessageEvent event = new SendMessageEvent();
                            event.setEventName(SocketManager.EVENT_FILE_DOWNLOAD);
                            event.setMessageObject(object);
                            eventBus.post(event);
                        } else {
                            String msgId = object.getString("MsgId");
                            removeDownloadProgress(msgId);
                        }
                    }
                } catch (JSONException e) {
                    removeOfflineDownloadStatusNotExist(key);
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * check external Memory Available or not
     *
     * @return response value
     */
    //StorageDetails
    public static boolean externalMemoryAvailable() {
        return android.os.Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);
    }

    /**
     * get Available Internal Memory Size for downloding
     *
     * @return response value
     */
    public static String getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long availableBlocks;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }

        return formatSize(availableBlocks * blockSize);
    }

    public static String getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize;
        long totalBlocks;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
        }

        return formatSize(totalBlocks * blockSize);
    }

    /**
     * get Available External Memory Size for file downloading
     *
     * @return response value
     */
    public static String getAvailableExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize;
            long availableBlocks;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                blockSize = stat.getBlockSizeLong();
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                blockSize = stat.getBlockSize();
                availableBlocks = stat.getAvailableBlocks();
            }
            return formatSize(availableBlocks * blockSize);
        } else {
            return "";
        }
    }

    public static String getTotalExternalMemorySize() {
        if (externalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long totalBlocks;

            long blockSize;

            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
                blockSize = stat.getBlockSizeLong();
                totalBlocks = stat.getBlockCountLong();
            } else {
                blockSize = stat.getBlockSize();
                totalBlocks = stat.getBlockCount();
            }

            return formatSize(totalBlocks * blockSize);
        } else {
            return "";
        }
    }

    /**
     * size format
     *
     * @param size input value(size)
     * @return response value
     */
    public static String formatSize(long size) {
        String suffix = null;

        if (size >= 1024) {
            suffix = " KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = " MB";
                size /= 1024;
            }
        } else {
            suffix = "  Bytes";
        }

        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));

        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }

        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    /**
     * get Image Real FilePath for downloading
     *
     * @param context current activity
     * @param uri     input value(uri)
     * @return response value
     */
    public static String getImageRealFilePath(Context context, Uri uri) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Images.Media.DATA};
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);
            String filePath = null;
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                    System.out.println("===data" + filePath);
                }
                cursor.close();
            }
            return filePath;
        }
        return null;
    }


    public static String getDocumentFilePath(Context context, Uri uri) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_NONE};
            // where id is equal to
            String sel = MediaStore.Images.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().
                    query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);
            String filePath = null;
            if (cursor != null) {
                int columnIndex = cursor.getColumnIndex(column[0]);
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                cursor.close();
            }
            return filePath;
        }
        return null;
    }

    /**
     * get Document Real FilePath for downloading
     *
     * @param context current activity
     * @param uri     input value(uri)
     * @return response value
     */
    public static String getDocumentRealFilePath(Context context, Uri uri) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            System.out.println("=====id" + id);
            String[] column = {MediaStore.Files.FileColumns.DATA};
            // where id is equal to
            String sel = MediaStore.Audio.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().
                    query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);
            String filePath = "";
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        } else {
            return null;
        }
    }

    /**
     * get Video Real FilePath for downloading
     *
     * @param context current activity
     * @param uri     input value(uri)
     * @return response value
     */
    public static String getVideoRealFilePath(Context context, Uri uri) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Video.Media.DATA};
            // where id is equal to
            String sel = MediaStore.Video.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().
                    query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);
            String filePath = "";
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        } else {
            return null;
        }
    }

    /**
     * get Audio Real FilePath for downloading
     *
     * @param context current activity
     * @param uri     input value(uri)
     * @return response value
     */
    public static String getAudioRealFilePath(Context context, Uri uri) {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            String wholeID = DocumentsContract.getDocumentId(uri);
            // Split at colon, use second item in the array
            String id = wholeID.split(":")[1];
            String[] column = {MediaStore.Audio.Media.DATA};
            // where id is equal to
            String sel = MediaStore.Audio.Media._ID + "=?";
            Cursor cursor = context.getContentResolver().
                    query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                            column, sel, new String[]{id}, null);
            String filePath = "";
            int columnIndex = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex);
            }
            cursor.close();
            return filePath;
        } else {
            return null;
        }
    }


//    private class DownloadingTask extends AsyncTask<Void, Void, Void> {
//
//        File file=null;
//        private  String downloadurl,localpath,docid,msgid;
//
//
//        public DownloadingTask(String url,String localpath,String docid,String msgid) {
//            super();
//            this.downloadurl=url;
//            this.localpath=localpath;
//            this.docid=docid;
//            this.msgid=msgid;
//            // do stuff
//        }
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            try {
//                if (file != null) {
//                    if(decrypt(file)) {
//                        removeDownloadProgress(msgid);
//                        MessageDbController db = CoreController.getDBInstance(mContext);
//                        db.updateMessageDownloadStatus(docid, msgid, MessageFactory.DOWNLOAD_STATUS_COMPLETED);
//                        if(context!=null) {
//                            context.updateDownload(msgid, "1");
//                        }
//
//                    }
//
//                } else {
//
//                    Log.e(TAG, "Download Failed");
//
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//
//                Log.e(TAG, "Download Failed with Exception - " + e.getLocalizedMessage());
//
//            }
//
//
//            super.onPostExecute(result);
//        }
//
//        @Override
//        protected void onProgressUpdate(Void... values) {
//
//            MessageDbController db = CoreController.getDBInstance(mContext);
//            db.updateMessageDownloadStatus(docid, msgid, MessageFactory.DOWNLOAD_STATUS_DOWNLOADING);
//            }
//
//
//
//        @Override
//        protected Void doInBackground(Void... arg0) {
//            try {
//                URL url = new URL(downloadurl);//Create Download URl
//                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
//                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
//                c.connect();//connect the URL Connection
//
//                //If Connection response is not OK then show Logs
//                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
//                    Log.e(TAG, "Server returned HTTP " + c.getResponseCode()
//                            + " " + c.getResponseMessage());
//
//                }
//
//
//                file = new File(localpath);
//                if (!file.exists()) {
//                    try {
//                        file.createNewFile();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//                FileOutputStream fos = new FileOutputStream(file);//Get OutputStream for NewFile Location
//
//                InputStream is = c.getInputStream();//Get InputStream for connection
//
//                byte[] buffer = new byte[1024];//Set buffer type
//                int len1 = 0;//init length
//                while ((len1 = is.read(buffer)) != -1) {
//                    fos.write(buffer, 0, len1);//Write new file
//                }
//
//                //Close all connection after doing task
//                fos.close();
//                is.close();
//
//            } catch (Exception e) {
//
//                //Read exception if something went wrong
//                e.printStackTrace();
//                file=null;
//                Log.e(TAG, "Download Error Exception " + e.getMessage());
//            }
//
//            return null;
//        }
//    }
//
//    private boolean decrypt(File file)
//    {
//
//        try {
//
//            CryptLib cryptLib=new CryptLib();
//
//            byte[] bytesArray = new byte[(int) file.length()];
//
//            FileInputStream fis = new FileInputStream(file);
//            fis.read(bytesArray); //read file into bytes[]
//            fis.close();
//
//            file.delete();
//
//
//            byte[] buffer1=  cryptLib.encryptDecrypt(bytesArray,(Constants.DUMMY_KEY), CryptLib.EncryptMode.DECRYPT,cryptLib.generateRandomIV16());
//
//
////            byte[] buffer1=  cryptLib.decryptCipherTextWithBytearray( Base64.encodeToString(bytesArray, Base64.DEFAULT),(Constants.DUMMY_KEY));
//
//            if (!file.exists()) {
//                try {
//                    file.createNewFile();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            FileOutputStream outputStreamOri = new FileOutputStream(file, true);
//            outputStreamOri.write(buffer1);
//            outputStreamOri.close();
//
//            return true;
//
//        }
//        catch (Exception e)
//        {
//            e.printStackTrace();
//        }
//        return false;
//
//
//
//    }

}
