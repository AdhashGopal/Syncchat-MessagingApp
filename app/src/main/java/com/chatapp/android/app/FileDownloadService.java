package com.chatapp.android.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

import com.chatapp.android.R;

import org.appspot.apprtc.util.CryptLib;

import com.chatapp.android.app.utils.GroupInfoSession;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.uploadtoserver.FileUploadDownloadManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class FileDownloadService extends Service {

    private MessageDbController db;

    public static final String DOWNLOAD_PATH = "downloadPath";
    public static final String LOCAL_PATH = "localPath";
    public static final String DOCID = "docId";
    public static final String MSGID = "msgId";
    public static final String CONVID = "convID";

    String downloadPath, localPath, docId, msgId, convid;
    private FileUploadDownloadManager uploadDownloadManager;
    Context context;

    private GroupInfoSession groupInfoSession;
    private UserInfoSession userInfoSession;

    /**
     * Start service getting value from based on intent data to downloading task
     *
     * @param intent  input value(intent)
     * @param flags   input value(flags)
     * @param startId input value(startId)
     * @return value
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            downloadPath = intent.getStringExtra(DOWNLOAD_PATH);
            localPath = intent.getStringExtra(LOCAL_PATH);
            docId = intent.getStringExtra(DOCID);
            msgId = intent.getStringExtra(MSGID);
            convid = intent.getStringExtra(CONVID);

            new DownloadingTask(downloadPath, localPath, docId, msgId, convid).execute();

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
//        throw new UnsupportedOperationException("Not yet implemented");
    }


    /**
     * object creating for FileUploadDownloadManager, UserInfoSession and GroupInfoSession
     */
    @Override
    public void onCreate() {
        super.onCreate();
        uploadDownloadManager = new FileUploadDownloadManager(FileDownloadService.this);

        userInfoSession = new UserInfoSession(FileDownloadService.this);
        groupInfoSession = new GroupInfoSession(FileDownloadService.this);

        context = this;


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * start DownloadService for file
     *
     * @param context      input value(current activity)
     * @param downloadPath input value(downloadPath)
     * @param localpath    input value(localpath)
     * @param docid        input value(docid)
     * @param msgid        input value(msgid)
     * @param convid       input value(convid)
     */
    public static void startDownloadService(Context context, String downloadPath, String localpath, String docid, String msgid, String convid) {
        Intent intent = new Intent(context, FileDownloadService.class);
        intent.putExtra(DOWNLOAD_PATH, downloadPath);
        intent.putExtra(LOCAL_PATH, localpath);
        intent.putExtra(MSGID, msgid);
        intent.putExtra(DOCID, docid);
        intent.putExtra(CONVID, convid);
        System.out.println("==datadownload" + downloadPath);
        System.out.println("==datadownload" + localpath);
        System.out.println("==datadownload" + convid);


        context.startService(intent);

    }


    /**
     * send Download Broadcast
     *
     * @param msgId input value (msgId)
     */
    private void sendDownloadBroadcast(String msgId) {
        Intent broadcastIntent = new Intent("com.file.download.completed");
        broadcastIntent.putExtra(MSGID, msgId);
        sendBroadcast(broadcastIntent);
    }


    /**
     * Downloading the file to AsyncTask
     */
    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File file = null;
        private String downloadurl, localpath, docid, msgid, convID;


        public DownloadingTask(String url, String localpath, String docid, String msgid, String convID) {
            super();
            this.downloadurl = url;
            this.localpath = localpath;
            this.docid = docid;
            this.msgid = msgid;
            this.convID = convID;
            // do stuff
            System.out.println("==datadownload" + url);
            System.out.println("==datadownload" + localpath);
            System.out.println("==datadownload" + convID);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            MessageDbController db = CoreController.getDBInstance(context);
            db.updateMessageDownloadStatus(docid, msgid, MessageFactory.DOWNLOAD_STATUS_DOWNLOADING);
        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (file != null) {
                    MessageDbController db = CoreController.getDBInstance(context);

                    if (decrypt(file)) {

                        db.updateMessageDownloadStatus(docid, this.msgid, MessageFactory.DOWNLOAD_STATUS_COMPLETED);

                        sendDownloadBroadcast(this.msgid);

                    } else {
                        db.updateMessageDownloadStatus(docid, this.msgid, MessageFactory.DOWNLOAD_STATUS_NOT_START);

                    }

                } else {

                    Log.e("Download Exception", "Download Failed");

                }
            } catch (Exception e) {
                e.printStackTrace();

                Log.e("Download Exception", "Download Failed with Exception - " + e.getLocalizedMessage());

            }


            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            MessageDbController db = CoreController.getDBInstance(context);
            db.updateMessageDownloadStatus(docid, msgid, MessageFactory.DOWNLOAD_STATUS_DOWNLOADING);
        }


        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadurl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("Download Exception", "Server returned HTTP " + c.getResponseCode()
                            + " " + c.getResponseMessage());

                }


                file = new File(localpath);
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }


                FileOutputStream fos = new FileOutputStream(file);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }

                //Close all connection after doing task
                fos.close();
                is.close();

            } catch (Exception e) {

                //Read exception if something went wrong
                e.printStackTrace();
                file = null;
                Log.e("Download Exception", "Download Error Exception " + e.getMessage());
            }

            return null;
        }
    }


    /**
     * decrypt file format
     *
     * @param file input value(file path)
     * @return value
     */
    private boolean decrypt(File file) {

        try {

            CryptLib cryptLib = new CryptLib();

            byte[] bytesArray = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(bytesArray); //read file into bytes[]
            fis.close();

            file.delete();


//            if (db.isGroupId(docId)
//                    ) {
//
//                if (groupInfoSession.hasGroupInfo(docId)) {
//                    GroupInfoPojo infoPojo = groupInfoSession.getGroupInfo(docId);
//                    valuable = infoPojo.getGroupId();
//
//                }
//            }
//            else
//            {
//                if (userInfoSession.hasChatConvId(docId)) {
//                    valuable = userInfoSession.getChatConvId(docId);
//                }
//            }


            byte[] buffer1;
            try {
                buffer1 = cryptLib.encryptDecrypt(bytesArray, getResources().getString(R.string.chatapp) + convid + getResources().getString(R.string.adani), CryptLib.EncryptMode.DECRYPT, cryptLib.generateRandomIV16());
                System.out.println("==bytestring" + buffer1.toString());
                System.out.println("==byte" + buffer1);

                System.out.println("==bytearray" + bytesArray);
                System.out.println("==byteencryptionkey");
                System.out.println("==byteconvid" + convid);

            } catch (Exception e) {
                e.printStackTrace();
                buffer1 = bytesArray;

            }


            //    byte[] buffer1=  cryptLib.encryptDecrypt(bytesArray, Constants.DUMMY_KEY, CryptLib.EncryptMode.DECRYPT,cryptLib.generateRandomIV16());


//            byte[] buffer1=  cryptLib.decryptCipherTextWithBytearray( Base64.encodeToString(bytesArray, Base64.DEFAULT),(Constants.DUMMY_KEY));

            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            FileOutputStream outputStreamOri = new FileOutputStream(file, true);
            outputStreamOri.write(buffer1);
            outputStreamOri.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;


    }


}
