package com.chatapp.synchat.app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import com.chatapp.synchat.R;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.MessageItemChat;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * created by  Adhash Team on 7/3/2017.
 */
public class EmailChatHistoryUtils {

    private Context mContext;
    private SessionManager sessionManager;

    private ArrayList<MessageItemChat> msgItemList;

    /**
     * create constructor
     *
     * @param context current activity
     */
    public EmailChatHistoryUtils(Context context) {
        this.mContext = context;
        sessionManager = SessionManager.getInstance(mContext);
    }

    /**
     * send email chat
     *
     * @param docId        input value (docId)
     * @param receiverName input value (receiverName)
     * @param withMedia    input value (withMedia)
     * @param forGmail     input value (forGmail)
     * @param chatType     input value (chatType)
     */
    public void send(String docId, String receiverName, boolean withMedia, boolean forGmail, String chatType) {
        MessageDbController dbController = CoreController.getDBInstance(mContext);
        msgItemList = dbController.selectAllChatMessages(docId, chatType);

        File directory = new File(mContext.getFilesDir() + MessageFactory.CHATS_PATH);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String appName = mContext.getString(R.string.app_name);
        String fileName = appName + " Chat with " + receiverName;
        String filePath = directory + "/" + fileName + ".txt";

        String subject = fileName;
        String message = "Chat history is attached as \"" + fileName + ".txt\" file to this email.";

        try {
            File txtFile = new File(filePath);
            if (!txtFile.exists()) {
                txtFile.createNewFile();
            } else {
                txtFile.delete();
                txtFile.createNewFile();
            }

            FileWriter fw = new FileWriter(txtFile.getAbsolutePath());
            BufferedWriter bw = new BufferedWriter(fw);


            for (int i = 0; i < msgItemList.size(); i++) {
                String msg = getTextByMessageType(msgItemList.get(i));
                bw.write(msg);
            }
//            Toast.makeText(mContext, "file created", Toast.LENGTH_SHORT).show();
            bw.close();
            fw.close();

            if (withMedia) {
                if (forGmail) {
                    sendGmailWithMedia(filePath, subject, message);
                } else {
                    sendEmailWithMedia(filePath, subject, message);
                }
            } else {
                if (forGmail) {
                    sendGmail(filePath, subject, message);
                } else {
                    sendEmail(filePath, subject, message);
                }
            }

            //out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * send Gmail With Media
     *
     * @param filePath input value (filePath)
     * @param subject  input value (subject)
     * @param message  input value (message)
     */
    private void sendGmailWithMedia(String filePath, String subject, String message) {

        ArrayList<Uri> fileList = new ArrayList<>();

        File directory = new File(filePath);
        Uri path1 = Uri.fromFile(directory);


        for (int i = 0; i < msgItemList.size(); i++) {
            MessageItemChat msgItem = msgItemList.get(i);

            if (msgItem.getChatFileLocalPath() != null && !msgItem.getChatFileLocalPath().equals("")) {
                String path = msgItem.getChatFileLocalPath();
                File file = new File(path);
                if (file.exists()) {
                    Uri pathuri = Uri.fromFile(file);
                    fileList.add(pathuri);
                }
            }
        }
        fileList.add(path1);
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileList);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        final PackageManager pm = mContext.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        mContext.startActivity(Intent.createChooser(emailIntent, "Sending multiple attachment"));
        fileList.clear();

    }

    /**
     * send message via Gmail
     *
     * @param filePath input value (filePath)
     * @param subject  input value (subject)
     * @param message  input value (message)
     */
    private void sendGmail(String filePath, String subject, String message) {

        File directory = new File(filePath);
        Uri path = Uri.fromFile(directory);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.setType("application/octet-stream");
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        final PackageManager pm = mContext.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name.toLowerCase().contains("gmail"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        mContext.startActivity(emailIntent);
    }

    /**
     * send Email With Media
     *
     * @param filePath input value (filePath)
     * @param subject  input value (subject)
     * @param message  input value (message)
     */
    protected void sendEmailWithMedia(String filePath, String subject, String message) {
        File directory = new File(filePath);
        Uri path1 = Uri.fromFile(directory);

        ArrayList<Uri> fileList = new ArrayList<>();

        for (int i = 0; i < msgItemList.size(); i++) {
            MessageItemChat msgItem = msgItemList.get(i);

            if (msgItem.getChatFileLocalPath() != null && !msgItem.getChatFileLocalPath().equals("")) {
                String path = msgItem.getChatFileLocalPath();
                File file = new File(path);
                if (file.exists()) {
                    Uri pathuri = Uri.fromFile(file);
                    fileList.add(pathuri);
                }
            }
        }
        fileList.add(path1);
        Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, fileList);
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        final PackageManager pm = mContext.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.packageName.endsWith("com.android.email") || info.activityInfo.name.toLowerCase().contains("com.android.email"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        mContext.startActivity(Intent.createChooser(emailIntent, "Sending multiple attachment"));
        fileList.clear();

    }

    /**
     * send message via Email
     *
     * @param filePath input value (filePath)
     * @param subject  input value (subject)
     * @param subject  input value (subject)
     */
    protected void sendEmail(String filePath, String subject, String message) {

        File directory = new File(filePath);
        Uri path = Uri.fromFile(directory);

        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.putExtra(Intent.EXTRA_EMAIL, "");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, message);
        emailIntent.setType("application/octet-stream");
        emailIntent.putExtra(Intent.EXTRA_STREAM, path);
        final PackageManager pm = mContext.getPackageManager();
        final List<ResolveInfo> matches = pm.queryIntentActivities(emailIntent, 0);
        ResolveInfo best = null;
        for (final ResolveInfo info : matches)
            if (info.activityInfo.name.toLowerCase().contains("email"))
                best = info;
        if (best != null)
            emailIntent.setClassName(best.activityInfo.packageName, best.activityInfo.name);
        mContext.startActivity(emailIntent);
    }

    /**
     * get Text By Message Type
     *
     * @param msgItem input value (getting from model class)
     * @return value
     */
    private String getTextByMessageType(MessageItemChat msgItem) {
        String msg;

        if (msgItem.isSelf()) {
            msg = sessionManager.getnameOfCurrentUser() + " : ";
        } else {
            msg = msgItem.getSenderName() + " : ";
        }

        if (msgItem.getMessageType() != null) {
            if (msgItem.getTextMessage() != null) {
                msg = msg + msgItem.getTextMessage();
            }

            if (msgItem.getMessageType().equals(MessageFactory.picture + "")
                    || msgItem.getMessageType().equals(MessageFactory.audio + "")
                    || msgItem.getMessageType().equals(MessageFactory.video + "")
                    || msgItem.getMessageType().equals(MessageFactory.document + "")
                    || msgItem.getMessageType().equals(MessageFactory.group_document_message + "")) {

                if (msgItem.getChatFileLocalPath() != null && isFileExists(msgItem.getChatFileLocalPath())) {
                    msg = msg + " <Media Attached>";
                } else {
                    msg = msg + " <Media Omitted>";
                }
            } else if (msgItem.getMessageType().equals(MessageFactory.location + "")) {
                msg = msg + msgItem.getWebLink();
            }
        }

        return msg + "\n";
    }

    /**
     * to check FileExists or not
     *
     * @param filePath input value (filePath)
     * @return value
     */
    private boolean isFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

}
