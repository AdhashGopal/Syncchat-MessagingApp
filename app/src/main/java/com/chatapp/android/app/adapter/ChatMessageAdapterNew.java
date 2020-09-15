package com.chatapp.android.app.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.android.R;
import com.chatapp.android.app.ChatViewActivity;
import com.chatapp.android.app.ImageZoom;
import com.chatapp.android.app.Savecontact;
import com.chatapp.android.app.ChatappContactsService;
import com.chatapp.android.app.dialog.ChatLockPwdDialog;
import com.chatapp.android.app.dialog.CustomMultiTextItemsDialog;
import com.chatapp.android.app.utils.AppUtils;
import com.chatapp.android.app.utils.ConnectivityInfo;
import com.chatapp.android.app.utils.ConstantMethods;

import org.appspot.apprtc.util.CryptLib;

import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.utils.MyLog;
import com.chatapp.android.app.utils.TimeStampUtils;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.app.viewholder.VHAudioReceived;
import com.chatapp.android.app.viewholder.VHAudioSent;
import com.chatapp.android.app.viewholder.VHCallReceived;
import com.chatapp.android.app.viewholder.VHContactReceived;
import com.chatapp.android.app.viewholder.VHContactSent;
import com.chatapp.android.app.viewholder.VHDeleteOther;
import com.chatapp.android.app.viewholder.VHDeleteSelf;
import com.chatapp.android.app.viewholder.VHDocumentReceived;
import com.chatapp.android.app.viewholder.VHDocumentSent;
import com.chatapp.android.app.viewholder.VHImageReceived;
import com.chatapp.android.app.viewholder.VHImageSent;
import com.chatapp.android.app.viewholder.VHInfoMessage;
import com.chatapp.android.app.viewholder.VHLocationReceived;
import com.chatapp.android.app.viewholder.VHLocationSent;
import com.chatapp.android.app.viewholder.VHMessageReceived;
import com.chatapp.android.app.viewholder.VHMessageSent;
import com.chatapp.android.app.viewholder.VHVideoReceived;
import com.chatapp.android.app.viewholder.VHVideoSent;
import com.chatapp.android.app.viewholder.VHWebLinkReceived;
import com.chatapp.android.app.viewholder.VHWebLinkSent;
import com.chatapp.android.app.viewholder.VHservermessage;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.ShortcutBadgeManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.ChatLockPojo;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.model.MultiTextDialogPojo;
import com.chatapp.android.core.chatapphelperclass.ChatappImageUtils;
import com.chatapp.android.core.service.Constants;
import com.chatapp.android.core.uploadtoserver.FileUploadDownloadManager;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import javax.crypto.NoSuchPaddingException;

public class ChatMessageAdapterNew extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public static final int MESSAGERECEIVED = 0;
    public static final int MESSAGESENT = 1;
    public static final int IMAGERECEIVED = 2;
    public static final int IMAGESENT = 3;
    public static final int VIDEORECEIVED = 4;
    public static final int VIDEOSENT = 5;
    //private static final Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();
    private static final String TAG = ChatMessageAdapterNew.class.getSimpleName() + ">>>@@@@@@";
    public static List<ChatappContactModel> chatappContacts;
    private final int LOCATIONRECEIVED = 6;
    private final int LOCATIONSENT = 7;
    private final int CONTACTRECEIVED = 8;
    private final int CONTACTSENT = 9;
    private final int AUDIORECEIVED = 10;
    private final int AUDIOSENT = 11;
    private final int ServerMessAGE = 12;
    private final int WEB_LINK_RECEIVED = 13;
    private final int WEB_LINK_SENT = 14;
    private final int DOCUMENT_RECEIVED = 15;
    private final int DOCUMENT_SENT = 16;
    private final int GROUP_EVENT_INFO = 17;
    private final int MISSED_CALL_INFO = 18;
    //-------------Delete Chat---------------
    private final int MESSAGE_SELF_DELETED = 19;
    private final int MESSAGE_OTHER_DELETED = 20;
    ShortcutBadgeManager sharedprf_video_uploadprogress;
    boolean isbroadcast;
    private ArrayList<MessageItemChat> mListData = new ArrayList<>();
    private ArrayList<MessageItemChat> mOriginalValues = new ArrayList<>();
    private ArrayList<MessageItemChat> mImageData = new ArrayList<>();
    private ArrayList<String> imgzoompath = new ArrayList<>();
    private ArrayList<String> imgs = new ArrayList<String>();
    private ArrayList<String> imgscaption = new ArrayList<String>();
    private Context mContext;
    private FragmentManager fragmentManager;
    private MediaPlayer mPlayer;
    private String thumnail = "", mCurrentUserId;
    private boolean isGroupChat = false;
    private ItemFilter mFilter = new ItemFilter();
    private Boolean FirstItemSelected = false;
    private int lastPlayedAt = -1;
    private Timer mTimer;
    private Getcontactname getcontactname;
    private LayoutInflater inflater;
    private String sessionTxtSize = "Small";
    private UserInfoSession userInfoSession;
    private int selectedItemColor, unSelectedItemColor;
    private ChatViewActivity activity;
    private SessionManager sessionmanager;
    private FileUploadDownloadManager fileUploadDownloadManager;
    private String docid;
    private MessageDbController db;


    /**
     * constructor
     *
     * @param mContext        The activity object inherits the Context object
     * @param mListData       list of value
     * @param fragmentManager FragmentManager
     * @param isbroadcast     boolean value
     */
    public ChatMessageAdapterNew(Context mContext, ArrayList<MessageItemChat> mListData, FragmentManager fragmentManager, boolean isbroadcast) {
        //Log.d(TAG, "ChatMessageAdapter: constructor");
        this.mListData = mListData;
        this.mOriginalValues = mListData;
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
        setHasStableIds(true);
        userInfoSession = new UserInfoSession(mContext);
        Session session = new Session(mContext);
        sessionmanager = SessionManager.getInstance(mContext);
        sharedprf_video_uploadprogress = new ShortcutBadgeManager(mContext);
        fileUploadDownloadManager = new FileUploadDownloadManager(mContext);
        mCurrentUserId = sessionmanager.getCurrentUserID();
        getcontactname = new Getcontactname(mContext);
        selectedItemColor = ContextCompat.getColor(mContext, R.color.selected_chat);
        unSelectedItemColor = Color.TRANSPARENT;
        this.isbroadcast = isbroadcast;

        sessionTxtSize = session.gettextsize();
        inflater = LayoutInflater.from(mContext);

        db = CoreController.getDBInstance(mContext);
        docid = session.getMediaDocid();
        ArrayList<MessageItemChat> items;
        items = db.selectAllChatMessages(docid, ConstantMethods.getChatType(docid));
        mImageData.clear();
        mImageData.addAll(items);
        mediafile();
    }


    /**
     * retrieve Contact Photo
     *
     * @param context The activity object inherits the Context object
     * @param number  input value
     * @return value
     */
    public static Bitmap retrieveContactPhoto(Context context, String number) {
        ContentResolver contentResolver = context.getContentResolver();
        String contactId = null;
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

        String[] projection = new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME, ContactsContract.PhoneLookup._ID};

        Cursor cursor =
                contentResolver.query(
                        uri,
                        projection,
                        null,
                        null,
                        null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                contactId = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
            }
            cursor.close();
        }

        Bitmap photo = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_profile_default);

        try {
            InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.valueOf(contactId)));

            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
            }

            assert inputStream != null;
            inputStream.close();

        } catch (IOException e) {
            MyLog.e(TAG, "Error", e);
        }
        return photo;

    }


    /**
     * round
     *
     * @param value  input value
     * @param places input value
     * @return value
     */
    private static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    /**
     * set IsGroupChat
     *
     * @param isGroupChat boolean value
     */
    public void setIsGroupChat(boolean isGroupChat) {

        this.isGroupChat = isGroupChat;
    }

    /**
     * get list Item Count
     *
     * @return list size
     */
    @Override
    public int getItemCount() {
        //  Log.d(TAG, "getItemCount: ");
        return this.mListData.size();
    }


    /**
     * getItem
     *
     * @param position specific item position
     * @return value
     */
    public MessageItemChat getItem(int position) {
        return mListData.get(position);
    }

    /**
     * Based on ItemViewType, it will be show as layout
     *
     * @param position specific view
     * @return value
     */
    @Override
    public int getItemViewType(int position) {

        String type = mListData.get(position).getMessageType();

        if (mListData.get(position).isDate()) {
            return ServerMessAGE;
        }

        if (mListData.get(position).isSelf()) {

            switch (type) {
                case ("" + MessageFactory.text):
                    return MESSAGESENT;

                case ("" + MessageFactory.picture):
                    return IMAGESENT;

                case ("" + MessageFactory.video):
                    return VIDEOSENT;

                case ("" + MessageFactory.location):
                    return LOCATIONSENT;

                case ("" + MessageFactory.contact):
                    return CONTACTSENT;

                case ("" + MessageFactory.audio):
                    return AUDIOSENT;

                case ("" + MessageFactory.document):
                    return DOCUMENT_SENT;

                case ("" + MessageFactory.web_link):
                    return WEB_LINK_SENT;

                case ("" + MessageFactory.DELETE_SELF):
                    return MESSAGE_SELF_DELETED;


                default:
                    return WEB_LINK_SENT;
            }

        } else {
            if (type == null) {
                return MISSED_CALL_INFO;
            }

            switch (type) {
                case ("" + MessageFactory.text):
                    return MESSAGERECEIVED;

                case ("" + MessageFactory.picture):
                    return IMAGERECEIVED;

                case ("" + MessageFactory.video):
                    return VIDEORECEIVED;

                case ("" + MessageFactory.location):
                    return LOCATIONRECEIVED;

                case ("" + MessageFactory.contact):
                    return CONTACTRECEIVED;

                case ("" + MessageFactory.audio):
                    return AUDIORECEIVED;

                case ("" + MessageFactory.document):
                    return DOCUMENT_RECEIVED;

                case ("" + MessageFactory.group_event_info):
                    return GROUP_EVENT_INFO;

                case ("" + MessageFactory.web_link):
                    return WEB_LINK_RECEIVED;

                case ("" + MessageFactory.DELETE_OTHER):
                    return MESSAGE_OTHER_DELETED;

                default:
                    return MISSED_CALL_INFO;
            }
        }

    }


    /**
     * binding the layout view
     *
     * @param viewGroup layout view group
     * @param viewType
     * @return view holder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        MyLog.d(TAG, "onCreateViewHolder:  start");

        RecyclerView.ViewHolder viewHolder;

        View v1;

        switch (viewType) {
            case MESSAGERECEIVED:
                v1 = inflater.inflate(R.layout.message_received_new, viewGroup, false);

                TextView rsize = (TextView) v1.findViewById(R.id.txtMsg);
                //rsize.setTypeface(face);
                if (sessionTxtSize.equalsIgnoreCase("Small"))
                    rsize.setTextSize(11);
                else if (sessionTxtSize.equalsIgnoreCase("Medium"))
                    rsize.setTextSize(14);
                else if (sessionTxtSize.equalsIgnoreCase("Large"))
                    rsize.setTextSize(17);

                viewHolder = new VHMessageReceived(v1);

                break;

            case IMAGERECEIVED:
                v1 = inflater.inflate(R.layout.image_received_latest, viewGroup, false);
                viewHolder = new VHImageReceived(v1);
                break;

            case VIDEORECEIVED:
                v1 = inflater.inflate(R.layout.video_received_latest, viewGroup, false);
                viewHolder = new VHVideoReceived(v1);
                break;

            case LOCATIONRECEIVED:
                v1 = inflater.inflate(R.layout.location_received_new, viewGroup, false);
                viewHolder = new VHLocationReceived(v1);
                break;

            case CONTACTRECEIVED:
                v1 = inflater.inflate(R.layout.contact_received_new, viewGroup, false);
                viewHolder = new VHContactReceived(v1);
                break;

            case DOCUMENT_RECEIVED:
                v1 = inflater.inflate(R.layout.vh_document_received_new, viewGroup, false);

                TextView docRecsize = (TextView) v1.findViewById(R.id.txtMsg);
                //  docRecsize.setTypeface(face);
                if (sessionTxtSize.equalsIgnoreCase("Small"))
                    docRecsize.setTextSize(11);
                else if (sessionTxtSize.equalsIgnoreCase("Medium"))
                    docRecsize.setTextSize(14);
                else if (sessionTxtSize.equalsIgnoreCase("Large"))
                    docRecsize.setTextSize(17);

                viewHolder = new VHDocumentReceived(v1);
                break;

            case AUDIORECEIVED:
                v1 = inflater.inflate(R.layout.audio_received_new, viewGroup, false);
                viewHolder = new VHAudioReceived(v1);
                break;

            case WEB_LINK_RECEIVED:
                v1 = inflater.inflate(R.layout.vh_web_link_received_new, viewGroup, false);

                TextView tvLinkMsg = (TextView) v1.findViewById(R.id.txtMsg);
                // tvLinkMsg.setTypeface(face);
                if (sessionTxtSize.equalsIgnoreCase("Small"))
                    tvLinkMsg.setTextSize(11);
                else if (sessionTxtSize.equalsIgnoreCase("Medium"))
                    tvLinkMsg.setTextSize(14);
                else if (sessionTxtSize.equalsIgnoreCase("Large"))
                    tvLinkMsg.setTextSize(17);
                viewHolder = new VHWebLinkReceived(v1);
                break;


            case MESSAGESENT:
                v1 = inflater.inflate(R.layout.message_sent_new, viewGroup, false);
                TextView ssize = (TextView) v1.findViewById(R.id.txtMsg);
                // ssize.setTypeface(face);
                if (sessionTxtSize.equalsIgnoreCase("Small"))
                    ssize.setTextSize(11);
                else if (sessionTxtSize.equalsIgnoreCase("Medium"))
                    ssize.setTextSize(14);
                else if (sessionTxtSize.equalsIgnoreCase("Large"))
                    ssize.setTextSize(17);
                viewHolder = new VHMessageSent(v1);
                break;

            case IMAGESENT:
                v1 = inflater.inflate(R.layout.image_sent_latest, viewGroup, false);
                viewHolder = new VHImageSent(v1);
                break;

            case VIDEOSENT:
                v1 = inflater.inflate(R.layout.video_sent_latest, viewGroup, false);
                viewHolder = new VHVideoSent(v1);
                break;

            case LOCATIONSENT:
                v1 = inflater.inflate(R.layout.location_sent, viewGroup, false);
                viewHolder = new VHLocationSent(v1);
                break;


            case CONTACTSENT:
                v1 = inflater.inflate(R.layout.contact_sent, viewGroup, false);
                viewHolder = new VHContactSent(v1);
                break;

            case ServerMessAGE:
                v1 = inflater.inflate(R.layout.servermessage, viewGroup, false);
                viewHolder = new VHservermessage(v1);
                break;

            case WEB_LINK_SENT:
                v1 = inflater.inflate(R.layout.vh_web_link_sent, viewGroup, false);

                TextView tvLinkMsgSent = (TextView) v1.findViewById(R.id.txtMsg);
                //   tvLinkMsgSent.setTypeface(face);
                if (sessionTxtSize.equalsIgnoreCase("Small"))
                    tvLinkMsgSent.setTextSize(11);
                else if (sessionTxtSize.equalsIgnoreCase("Medium"))
                    tvLinkMsgSent.setTextSize(14);
                else if (sessionTxtSize.equalsIgnoreCase("Large"))
                    tvLinkMsgSent.setTextSize(17);

                viewHolder = new VHWebLinkSent(v1);
                break;

            case DOCUMENT_SENT:
                v1 = inflater.inflate(R.layout.vh_document_sent, viewGroup, false);
                TextView docSize = (TextView) v1.findViewById(R.id.txtMsg);
                //   docSize.setTypeface(face);
                if (sessionTxtSize.equalsIgnoreCase("Small"))
                    docSize.setTextSize(11);
                else if (sessionTxtSize.equalsIgnoreCase("Medium"))
                    docSize.setTextSize(14);
                else if (sessionTxtSize.equalsIgnoreCase("Large"))
                    docSize.setTextSize(17);
                viewHolder = new VHDocumentSent(v1);
                break;

            case GROUP_EVENT_INFO:
                v1 = inflater.inflate(R.layout.vh_info_msg, viewGroup, false);

                viewHolder = new VHInfoMessage(v1);
                break;

            case MISSED_CALL_INFO:
                v1 = inflater.inflate(R.layout.vh_call_received, viewGroup, false);
                viewHolder = new VHCallReceived(v1);
                break;

            case MESSAGE_SELF_DELETED:
                v1 = inflater.inflate(R.layout.vh_self_delete, viewGroup, false);
                viewHolder = new VHDeleteSelf(v1);
                break;

            case MESSAGE_OTHER_DELETED:
                v1 = inflater.inflate(R.layout.vh_other_delete, viewGroup, false);
                viewHolder = new VHDeleteOther(v1);
                break;

            default:
                v1 = inflater.inflate(R.layout.audio_sent_new, viewGroup, false);
                viewHolder = new VHAudioSent(v1);
                break;


        }
        MyLog.d(TAG, "onCreateViewHolder:  end");
        return viewHolder;
    }


    /**
     * BindView & set the data's
     *
     * @param viewHolder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        MyLog.d(TAG, "onBindViewHolder:  " + viewHolder.getItemViewType());

        switch (viewHolder.getItemViewType()) {


            case MESSAGERECEIVED:
                VHMessageReceived vh2 = (VHMessageReceived) viewHolder;
                configureViewHolderMessageReceived(vh2, position);
                configureDateLabel(vh2.tvDateLbl, position);
                break;

            case IMAGERECEIVED:
                VHImageReceived vh3 = (VHImageReceived) viewHolder;
                configureViewHolderImageReceived(vh3, position);
                configureDateLabel(vh3.tvDateLbl, position);
                break;

            case VIDEORECEIVED:

                VHVideoReceived vh4 = (VHVideoReceived) viewHolder;
                configureViewHolderVideoReceived(vh4, position);
                configureDateLabel(vh4.tvDateLbl, position);
                break;

            case LOCATIONRECEIVED:

                VHLocationReceived vh5 = (VHLocationReceived) viewHolder;
                configureViewHolderLocationReceived(vh5, position);
                configureDateLabel(vh5.tvDateLbl, position);
                break;

            case CONTACTRECEIVED:

                VHContactReceived vh6 = (VHContactReceived) viewHolder;
                configureViewHolderContactReceived(vh6, position);
                configureDateLabel(vh6.tvDateLbl, position);
                break;

            case AUDIORECEIVED:

                VHAudioReceived vh7 = (VHAudioReceived) viewHolder;
                configureViewHolderAudioReceived(vh7, position);
                configureDateLabel(vh7.tvDateLbl, position);
                break;

            case DOCUMENT_RECEIVED:
                VHDocumentReceived vhDocumentReceived = (VHDocumentReceived) viewHolder;
                configureViewHolderDocumentReceived(vhDocumentReceived, position);
                configureDateLabel(vhDocumentReceived.tvDateLbl, position);
                break;

            case WEB_LINK_RECEIVED:
                VHWebLinkReceived vhWebReceived = (VHWebLinkReceived) viewHolder;
                configureViewHolderWebLinkReceived(vhWebReceived, position);
                configureDateLabel(vhWebReceived.tvDateLbl, position);
                break;


            case MESSAGESENT:
                VHMessageSent vh8 = (VHMessageSent) viewHolder;
                configureViewHolderMessageSent(vh8, position);
                configureDateLabel(vh8.tvDateLbl, position);
                break;

            case IMAGESENT:

                VHImageSent vh9 = (VHImageSent) viewHolder;
                configureViewHolderImageSent(vh9, position);
                configureDateLabel(vh9.tvDateLbl, position);
                break;

            case VIDEOSENT:

                VHVideoSent vh10 = (VHVideoSent) viewHolder;
                configureViewHolderVideoSent(vh10, position);
                configureDateLabel(vh10.tvDateLbl, position);
                break;

            case LOCATIONSENT:
                VHLocationSent vh11 = (VHLocationSent) viewHolder;
                configureViewHolderLocationSent(vh11, position);
                configureDateLabel(vh11.tvDateLbl, position);
                break;


            case CONTACTSENT:
                VHContactSent vh12 = (VHContactSent) viewHolder;
                configureViewHolderContactSent(vh12, position);
                configureDateLabel(vh12.tvDateLbl, position);
                break;

            case ServerMessAGE:

                VHservermessage vh14 = (VHservermessage) viewHolder;
                configureViewHolderServerMessage(vh14, position);

                break;

            case WEB_LINK_SENT:
                VHWebLinkSent vhWebSent = (VHWebLinkSent) viewHolder;
                configureViewHolderWebLinkSent(vhWebSent, position);
                configureDateLabel(vhWebSent.tvDateLbl, position);
                break;

            case DOCUMENT_SENT:
                VHDocumentSent vhDocumentSent = (VHDocumentSent) viewHolder;
                configureViewHolderDocumentSent(vhDocumentSent, position);
                configureDateLabel(vhDocumentSent.tvDateLbl, position);
                break;

            case GROUP_EVENT_INFO:
                VHInfoMessage vhInfo = (VHInfoMessage) viewHolder;
                configureViewHolderInfoMessage(vhInfo, position);
                configureDateLabel(vhInfo.tvDateLbl, position);
                break;

            case MISSED_CALL_INFO:
                VHCallReceived vhCall = (VHCallReceived) viewHolder;
                configureDateLabel(vhCall.tvDateLbl, position);
                configureVHMissedCall(vhCall, position);
                break;

            case MESSAGE_SELF_DELETED:
                VHDeleteSelf vhDeleteSelf = (VHDeleteSelf) viewHolder;
                setselfTime(vhDeleteSelf, position);
                break;

            case MESSAGE_OTHER_DELETED:
                VHDeleteOther vhDeleteOther = (VHDeleteOther) viewHolder;
                setotherTime(vhDeleteOther, position);
                break;

            default:
                VHAudioSent vh13 = (VHAudioSent) viewHolder;
                configureViewHolderAudioSent(vh13, position);
                configureDateLabel(vh13.tvDateLbl, position);
                break;


        }
        MyLog.d(TAG, "onBindViewHolder: end");
    }

    /**
     * setselfTime
     *
     * @param vhSelf   specific view holder
     * @param position specific position
     */
    private void setselfTime(VHDeleteSelf vhSelf, int position) {
        MessageItemChat message = mListData.get(position);
        String time = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
        vhSelf.tv_time.setText(time);
        if (message.isSelected())
            vhSelf.selection_layout.setBackgroundColor(selectedItemColor);
        else
            vhSelf.selection_layout.setBackgroundColor(unSelectedItemColor);
    }

    /**
     * setotherTime
     *
     * @param vhOther  specific view holder
     * @param position specific position
     */
    private void setotherTime(VHDeleteOther vhOther, int position) {
        MessageItemChat message = mListData.get(position);
        String time = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
        vhOther.tv_time.setText(time);
        if (message.isSelected())
            vhOther.selection_layout.setBackgroundColor(selectedItemColor);
        else
            vhOther.selection_layout.setBackgroundColor(unSelectedItemColor);

//        if(message.getIsExpiry().equals("1"))
//        {
//            vhOther.selection_layout.setVisibility(View.GONE);
//        }
    }


    /**
     * MissedCall function
     *
     * @param vhCall   specific view holder
     * @param position specific position
     */
    private void configureVHMissedCall(VHCallReceived vhCall, int position) {

        MessageItemChat message = mListData.get(position);
        String time = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());

        if (message.getCallType() != null && message.getCallType().equals(MessageFactory.video_call + "")) {
            vhCall.tvCallLbl.setText("Missed video call at " + time);
        } else {
            vhCall.tvCallLbl.setText("Missed voice call at " + time);
        }
    }


    /**
     * InfoMessage
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderInfoMessage(VHInfoMessage vh2, int position) {
        final MessageItemChat message = mListData.get(position);


        vh2.tvInfoMsg.setVisibility(View.VISIBLE);


        String createdBy = message.getCreatedByUserId();
        String createdTo = message.getCreatedToUserId();
        String groupName = message.getGroupName();

        String createdByName = null, createdToName = null;
        String msg = null;

        switch (message.getGroupEventType()) {

            case "" + MessageFactory.join_new_group:
                if (createdBy.equalsIgnoreCase(mCurrentUserId)) {
                    createdByName = "You";
                } else {
                    createdByName = getContactNameIfExists(createdBy);
                }
                msg = createdByName + " created group '" + groupName + "'";
                break;

            case "" + MessageFactory.add_group_member:
                if (createdBy.equalsIgnoreCase(mCurrentUserId)) {
                    createdByName = "You";
                } else {
                    createdByName = getContactNameIfExists(createdBy);
                }
                if (createdTo.equalsIgnoreCase(mCurrentUserId)) {
                    createdToName = "You";
                } else {
                    createdToName = getContactNameIfExists(createdTo);
                }
                msg = createdByName + " added " + createdToName;
                break;

            case "" + MessageFactory.change_group_icon:
                if (createdBy.equalsIgnoreCase(mCurrentUserId)) {
                    createdByName = "You";
                } else {


                    // createdByName=message.getSenderOriginalName();
                    createdByName = getContactNameIfExists(createdBy);
                }

                msg = createdByName + " changed group's icon";
                break;

            case "" + MessageFactory.change_group_name:
                if (createdBy.equalsIgnoreCase(mCurrentUserId)) {
                    createdByName = "You";
                } else {
                    createdByName = getContactNameIfExists(createdBy);
                }

                msg = createdByName + " changed group's name '" + message.getPrevGroupName() + "' to '"
                        + message.getGroupName() + "'";
                break;

            case "" + MessageFactory.delete_member_by_admin:
                if (createdBy.equalsIgnoreCase(mCurrentUserId)) {
                    createdByName = "You";
                } else {
                    createdByName = getContactNameIfExists(createdBy);
                }

                if (createdTo.equalsIgnoreCase(mCurrentUserId)) {
                    createdToName = "You";
                } else {
                    createdToName = getContactNameIfExists(createdTo);
                }
                msg = createdByName + " removed " + createdToName;
                break;

            case "" + MessageFactory.make_admin_member:
                if (createdTo.equalsIgnoreCase(mCurrentUserId)) {
                    createdToName = "You are ";
                } else {
                    createdToName = getContactNameIfExists(createdTo);
                }
                msg = createdToName + " now admin";
                break;

            case "" + MessageFactory.exit_group:
                if (createdBy.equalsIgnoreCase(mCurrentUserId)) {
                    createdByName = "You ";
                } else {
                    createdByName = getContactNameIfExists(createdBy);
                }
                msg = createdByName + " left";
                break;

        }

        if (msg != null) {
            vh2.tvInfoMsg.setText(msg);
        }

    }


    /**
     * ContactName IfExists
     *
     * @param userId input value
     * @return value
     */
    private String getContactNameIfExists(String userId) {
        String userName = null;
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
        ChatappContactModel contact = contactDB_sqlite.getUserOpponenetDetails(userId);
//        ChatappContactModel contact = CoreController.getContactsDbInstance(mContext).getUserDetails(userId);
        if (contact != null) {
            //  userName = getcontactname.getSendername(userId, contact.getMsisdn());
            //  vh2.senderName.setText(message.getSenderOriginalName());

            userName = contact.getFirstName();


//            if (contact.getMsisdn() == null || contact.getMsisdn().equalsIgnoreCase("null")) {
//                userName = contact.getFirstName();
//            }
        } else if (activity != null) {
            activity.getUserDetails(userId);
        }

        return userName;
    }


    /**
     * DocumentSent
     *
     * @param vhDocumentSent specific view holder
     * @param position       specific position
     */
    private void configureViewHolderDocumentSent(VHDocumentSent vhDocumentSent, int position) {
        final MessageItemChat message = mListData.get(position);

        if (message != null) {
//            vhDocumentSent.pbUpload.setVisibility(View.GONE);
            if (message.isSelected())
                vhDocumentSent.selection_layout.setBackgroundColor(selectedItemColor);
                // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
            else
                vhDocumentSent.selection_layout.setBackgroundColor(unSelectedItemColor);

            if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                vhDocumentSent.starred.setVisibility(View.VISIBLE);
            } else {
                vhDocumentSent.starred.setVisibility(View.GONE);
            }

            // vh2.senderName.setText(message.getSenderName());
            String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            mydate = mydate.replace(".", "");
            vhDocumentSent.time.setText(mydate);
            try {
                vhDocumentSent.message.setText(Html.fromHtml("<u>" + message.getTextMessage() + "</u>"));
            } catch (Exception e) {
                MyLog.e(TAG, "Error", e);
            }
            try {
                File file = new File(message.getChatFileLocalPath());
                int file_size = Integer.parseInt(String.valueOf(file.length()));
                String fileFormattedSize = FileUploadDownloadManager.formatSize(file_size);
                if (fileFormattedSize != null && !fileFormattedSize.equals("0"))
                    vhDocumentSent.tvFileSize.setText(fileFormattedSize);
            } catch (Exception e) {
                vhDocumentSent.tvFileSize.setText("");
                MyLog.e(TAG, "configureViewHolderDocumentReceived: ", e);
            }

            if ((message.getUploadStatus() == MessageFactory.UPLOAD_STATUS_UPLOADING &&
                    message.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_NOT_SENT))
                    || message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_DOWNLOADING) {
                vhDocumentSent.pbUpload.setVisibility(View.VISIBLE);
            } else {
                vhDocumentSent.pbUpload.setVisibility(View.GONE);
            }

            if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START) {
                vhDocumentSent.iBtnDownload.setVisibility(View.VISIBLE);
            } else {
                vhDocumentSent.iBtnDownload.setVisibility(View.GONE);
            }

            if (position == 0) {
                if (!message.isInfoMsg() && message.isSelf()) {
                    vhDocumentSent.imageViewindicatior.setVisibility(View.VISIBLE);
                }
            } else {

                MessageItemChat prevmsg = mListData.get(position - 1);
                if (message.isSelf() == !prevmsg.isSelf()) {
                    vhDocumentSent.imageViewindicatior.setVisibility(View.VISIBLE);
                } else {
                    vhDocumentSent.imageViewindicatior.setVisibility(View.GONE);
                }

            }

            String status = message.getDeliveryStatus();
          /*  if (status == null) {
                status = MessageFactory.DELIVERY_STATUS_SENT;
            }*/

            if (message.isBlockedMessage()) {
                vhDocumentSent.clock.setVisibility(View.GONE);
                vhDocumentSent.ivTick.setVisibility(View.VISIBLE);
                vhDocumentSent.ivTick.setImageResource(R.mipmap.ic_single_tick);
            } else {
                switch (status) {
                    case "3":
                        vhDocumentSent.clock.setVisibility(View.GONE);
                        vhDocumentSent.ivTick.setImageResource(R.drawable.message_deliver_tick);
                        vhDocumentSent.ivTick.setVisibility(View.VISIBLE);
                        break;
                    case "2":
                        vhDocumentSent.clock.setVisibility(View.GONE);
                        vhDocumentSent.ivTick.setImageResource(R.mipmap.ic_double_tick);
                        vhDocumentSent.ivTick.setVisibility(View.VISIBLE);

                        break;
                    case "1":
                        vhDocumentSent.clock.setVisibility(View.GONE);
                        vhDocumentSent.ivTick.setVisibility(View.VISIBLE);
                        vhDocumentSent.ivTick.setImageResource(R.mipmap.ic_single_tick);

                        break;
                    default:
                        vhDocumentSent.clock.setVisibility(View.VISIBLE);
                        vhDocumentSent.ivTick.setVisibility(View.GONE);
                        break;
                }
            }


        }
    }


    /**
     * DocumentReceived
     *
     * @param vhDocumentReceived specific view holder
     * @param position           specific position
     */
    private void configureViewHolderDocumentReceived(final VHDocumentReceived vhDocumentReceived, int position) {
        final MessageItemChat message = mListData.get(position);
        if (AppUtils.isNetworkAvailable(mContext)) {
            vhDocumentReceived.iBtnDownload.setVisibility(View.GONE);
            vhDocumentReceived.Pbdownload.setVisibility(View.GONE);
            if (message.isSelected())
                vhDocumentReceived.selection_layout.setBackgroundColor(selectedItemColor);
                // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
            else
                vhDocumentReceived.selection_layout.setBackgroundColor(unSelectedItemColor);

            if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED))
                vhDocumentReceived.starred.setVisibility(View.VISIBLE);
            else
                vhDocumentReceived.starred.setVisibility(View.GONE);
            if (position == 0) {
                if (!message.isInfoMsg() && !message.isSelf()) {
                    vhDocumentReceived.imageViewindicatior.setVisibility(View.VISIBLE);
                } else {
                    vhDocumentReceived.imageViewindicatior.setVisibility(View.GONE);
                }
            } else if (mListData.get(position - 1).isInfoMsg() && !message.isSelf() && !message.isInfoMsg()) {
                vhDocumentReceived.imageViewindicatior.setVisibility(View.VISIBLE);
            } else if (message.isInfoMsg() && mListData.get(position - 1).isInfoMsg()) {
                vhDocumentReceived.imageViewindicatior.setVisibility(View.GONE);
            } else {
                MessageItemChat prevmsg = mListData.get(position - 1);
                if ((!message.isSelf() == prevmsg.isSelf()) && !message.isInfoMsg()) {
                    vhDocumentReceived.imageViewindicatior.setVisibility(View.VISIBLE);
                } else {
                    vhDocumentReceived.imageViewindicatior.setVisibility(View.GONE);
                }

            }
            if (message != null) {

                if (isGroupChat) {
                    String msisdn = message.getSenderName();
                    vhDocumentReceived.senderName.setVisibility(View.VISIBLE);


                    vhDocumentReceived.senderName.setText(getContactNameIfExists(message.getGroupMsgFrom()));
                    //  vhDocumentReceived.senderName.setText(getcontactname.getSendername(message.getGroupMsgFrom(), msisdn));
                } else {
                    vhDocumentReceived.senderName.setVisibility(View.GONE);
                }

                try {
                    String fileFormattedSize = FileUploadDownloadManager.formatSize(AppUtils.parseLong(message.getFileSize()));
                    vhDocumentReceived.tvFileSize.setText("" + fileFormattedSize);
                } catch (Exception e) {
                    vhDocumentReceived.tvFileSize.setText("");
                    MyLog.e(TAG, "configureViewHolderDocumentReceived: ", e);
                }

                String ts = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                ts = ts.replace(".", "");

                vhDocumentReceived.time.setText(ts);
                try {
                    if (message.getTextMessage() != null && message.getTextMessage().length() > 0)
                        vhDocumentReceived.message.setText(Html.fromHtml("<u>" + message.getTextMessage() + "</u>"));

                    else
                        vhDocumentReceived.message.setText(Html.fromHtml("<u>" + "Document" + "</u>"));

                } catch (Exception e) {
                    MyLog.e(TAG, "Error", e);
                }

                vhDocumentReceived.relative_layout_message.setVisibility(View.VISIBLE);
                vhDocumentReceived.tvInfoMsg.setVisibility(View.GONE);
                if (message.isInfoMsg()) {
                    vhDocumentReceived.relative_layout_message.setVisibility(View.GONE);
                    vhDocumentReceived.tvInfoMsg.setVisibility(View.VISIBLE);

                    vhDocumentReceived.tvInfoMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            vhDocumentReceived.tvInfoMsg.setText(message.getTextMessage());
                        }
                    });
                }

                if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START) {
                    vhDocumentReceived.iBtnDownload.setVisibility(View.VISIBLE);
                    vhDocumentReceived.Pbdownload.setVisibility(View.GONE);
                    vhDocumentReceived.iBtnDownload.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (AppUtils.isNetworkAvailable(mContext)) {
                                vhDocumentReceived.iBtnDownload.setVisibility(View.GONE);
                                vhDocumentReceived.Pbdownload.setVisibility(View.VISIBLE);
                            }

                        }
                    });

                } else if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_DOWNLOADING) {
                    vhDocumentReceived.iBtnDownload.setVisibility(View.GONE);
                    vhDocumentReceived.Pbdownload.setVisibility(View.VISIBLE);
                } else {
                    vhDocumentReceived.iBtnDownload.setVisibility(View.GONE);
                    vhDocumentReceived.Pbdownload.setVisibility(View.GONE);
                }
            }
        } else {
            try {
                String fileFormattedSize = FileUploadDownloadManager.formatSize(AppUtils.parseLong(message.getFileSize()));
                vhDocumentReceived.tvFileSize.setText("" + fileFormattedSize);
            } catch (Exception e) {
                vhDocumentReceived.tvFileSize.setText("");
                MyLog.e(TAG, "configureViewHolderDocumentReceived: ", e);
            }
            if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START) {
                vhDocumentReceived.iBtnDownload.setVisibility(View.VISIBLE);
                vhDocumentReceived.Pbdownload.setVisibility(View.GONE);
                vhDocumentReceived.iBtnDownload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AppUtils.isNetworkAvailable(mContext)) {
                            vhDocumentReceived.iBtnDownload.setVisibility(View.GONE);
                            vhDocumentReceived.Pbdownload.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(mContext, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

            }
            Toast.makeText(mContext, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * WebLinkSent
     *
     * @param vhWebSent specific view holder
     * @param position  specific position
     */
    private void configureViewHolderWebLinkSent(final VHWebLinkSent vhWebSent, int position) {
        final MessageItemChat message = mListData.get(position);

        if (message != null) {

            if (message.isSelected())
                vhWebSent.selection_layout.setBackgroundColor(selectedItemColor);
                // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
            else
                vhWebSent.selection_layout.setBackgroundColor(unSelectedItemColor);

            if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                vhWebSent.starred.setVisibility(View.VISIBLE);
            } else {
                vhWebSent.starred.setVisibility(View.GONE);
            }
            if (position == 0) {
                if (message.isInfoMsg() == false && message.isSelf()) {
                    vhWebSent.imageViewindicatior.setVisibility(View.VISIBLE);
                }
            } else {

                MessageItemChat prevmsg = mListData.get(position - 1);
                if (message.isSelf() == !prevmsg.isSelf()) {
                    vhWebSent.imageViewindicatior.setVisibility(View.VISIBLE);
                } else {
                    vhWebSent.imageViewindicatior.setVisibility(View.GONE);
                }

            }
            // vh2.senderName.setText(message.getSenderName());
            String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            mydate = mydate.replace(".", "");
            vhWebSent.time.setText(mydate);
            try {
                vhWebSent.message.post(new Runnable() {
                    @Override
                    public void run() {
                        vhWebSent.message.setText(message.getTextMessage());
                    }
                });
            } catch (Exception e) {
                MyLog.e(TAG, "Error", e);
            }

            if (position == 0) {
                if (message.isInfoMsg() == false && message.isSelf()) {
                    vhWebSent.imageViewindicatior.setVisibility(View.VISIBLE);
                }
            } else {

                MessageItemChat prevmsg = mListData.get(position - 1);
                if (message.isSelf() == !prevmsg.isSelf()) {
                    vhWebSent.imageViewindicatior.setVisibility(View.VISIBLE);
                } else {
                    vhWebSent.imageViewindicatior.setVisibility(View.GONE);
                }

            }
            vhWebSent.tvWebLink.setText(message.getWebLink());
//            vhWebSent.tvWebLinkDesc.setText(message.getWebLinkDesc());
            vhWebSent.tvWebTitle.setText(message.getWebLinkTitle());
            /*if (message.getWebLinkImgUrl() != null && message.getWebLinkImgUrl().equals("")) {
                vhWebSent.ivWebLink.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(message.getWebLinkImgUrl()).into(vhWebSent.ivWebLink);
            } else {
                vhWebSent.ivWebLink.setVisibility(View.GONE);
                vhWebSent.ivWebLink.setBackground(null);
            }*/

            if (message.getWebLink() != null) {
                String linkImgUrl;
                if (message.getWebLink().endsWith("/")) {
                    linkImgUrl = message.getWebLink() + "favicon.ico";
                } else {
                    linkImgUrl = message.getWebLink() + "/favicon.ico";
                }

                if (!message.getWebLink().startsWith("http")) {
                    linkImgUrl = Constants.SOCKET_IP + linkImgUrl;
                }

                AppUtils.loadImage(mContext, linkImgUrl, vhWebSent.ivWebLink, 60, R.drawable.link);
            }

            String status = message.getDeliveryStatus();
          /*  if (status == null) {
                status = MessageFactory.DELIVERY_STATUS_SENT;
            }*/

            if (message.isBlockedMessage()) {
                vhWebSent.clock.setVisibility(View.GONE);
                vhWebSent.ivTick.setVisibility(View.VISIBLE);
                vhWebSent.ivTick.setImageResource(R.mipmap.ic_single_tick);
            } else {
                switch (status) {
                    case "3":
                        vhWebSent.clock.setVisibility(View.GONE);
                        vhWebSent.ivTick.setImageResource(R.drawable.message_deliver_tick);
                        vhWebSent.ivTick.setVisibility(View.VISIBLE);
                        break;
                    case "2":
                        vhWebSent.clock.setVisibility(View.GONE);
                        vhWebSent.ivTick.setImageResource(R.mipmap.ic_double_tick);
                        vhWebSent.ivTick.setVisibility(View.VISIBLE);

                        break;
                    case "1":
                        vhWebSent.clock.setVisibility(View.GONE);
                        vhWebSent.ivTick.setVisibility(View.VISIBLE);
                        vhWebSent.ivTick.setImageResource(R.mipmap.ic_single_tick);

                        break;
                    default:
                        vhWebSent.clock.setVisibility(View.VISIBLE);
                        vhWebSent.ivTick.setVisibility(View.GONE);
                        break;
                }
            }

        }
    }


    /**
     * WebLinkReceived
     *
     * @param vhWebReceived specific view holder
     * @param position      specific position
     */
    private void configureViewHolderWebLinkReceived(final VHWebLinkReceived vhWebReceived, int position) {
        final MessageItemChat message = mListData.get(position);

        if (message.isSelected())
            vhWebReceived.selection_layout.setBackgroundColor(selectedItemColor);
            // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
        else
            vhWebReceived.selection_layout.setBackgroundColor(unSelectedItemColor);

        if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED))
            vhWebReceived.starred.setVisibility(View.VISIBLE);
        else
            vhWebReceived.starred.setVisibility(View.GONE);

        if (message != null) {

            if (isGroupChat) {
                vhWebReceived.senderName.setVisibility(View.VISIBLE);
                String msisdn = message.getSenderName();
                vhWebReceived.senderName.setText(getContactNameIfExists(message.getGroupMsgFrom()));

                // vhWebReceived.senderName.setText(message.getSenderOriginalName());
                //   vhWebReceived.senderName.setText(getcontactname.getSendername(message.getGroupMsgFrom(), msisdn));
            } else {
                vhWebReceived.senderName.setVisibility(View.GONE);
            }

            String ts = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            ts = ts.replace(".", "");
            vhWebReceived.time.setText(ts);
            try {
                vhWebReceived.message.post(new Runnable() {
                    @Override
                    public void run() {
                        vhWebReceived.message.setText(message.getTextMessage());
                    }
                });

            } catch (Exception e) {
                MyLog.e(TAG, "Error", e);
            }
            if (position == 0) {
                if (!message.isInfoMsg() && !message.isSelf()) {
                    vhWebReceived.imageViewindicatior.setVisibility(View.VISIBLE);
                } else {
                    vhWebReceived.imageViewindicatior.setVisibility(View.GONE);
                }
            } else if (mListData.get(position - 1).isInfoMsg() && !message.isSelf() && !message.isInfoMsg()) {
                vhWebReceived.imageViewindicatior.setVisibility(View.VISIBLE);
            } else if (message.isInfoMsg() && mListData.get(position - 1).isInfoMsg()) {
                vhWebReceived.imageViewindicatior.setVisibility(View.GONE);
            } else {
                MessageItemChat prevmsg = mListData.get(position - 1);
                if ((!message.isSelf() == prevmsg.isSelf()) && !message.isInfoMsg()) {
                    vhWebReceived.imageViewindicatior.setVisibility(View.VISIBLE);
                } else {
                    vhWebReceived.imageViewindicatior.setVisibility(View.GONE);
                }

            }
            vhWebReceived.relative_layout_message.setVisibility(View.VISIBLE);
            vhWebReceived.tvInfoMsg.setVisibility(View.GONE);
            if (message.isInfoMsg()) {
                vhWebReceived.relative_layout_message.setVisibility(View.GONE);
                vhWebReceived.tvInfoMsg.setVisibility(View.VISIBLE);


                vhWebReceived.tvInfoMsg.post(new Runnable() {
                    @Override
                    public void run() {
                        vhWebReceived.tvInfoMsg.setText(message.getTextMessage());
                    }
                });
            }


            vhWebReceived.tvWebLink.setText(message.getWebLink());
            vhWebReceived.tvWebTitle.setText(message.getWebLinkTitle());
//            vhWebReceived.tvWebLinkDesc.setText(message.getWebLinkDesc());
            /*if (message.getWebLinkImgUrl() != null && !message.getWebLinkImgUrl().equals("")) {
                vhWebReceived.ivWebLink.setVisibility(View.VISIBLE);
                Picasso.with(mContext).load(message.getWebLinkImgUrl()).into(vhWebReceived.ivWebLink);
            } else {
                vhWebReceived.ivWebLink.setVisibility(View.GONE);
                vhWebReceived.ivWebLink.setBackground(null);
            }*/

            if (message.getWebLink() != null) {
                String linkImgUrl;
                if (message.getWebLink().endsWith("/")) {
                    linkImgUrl = message.getWebLink() + "favicon.ico";
                } else {
                    linkImgUrl = message.getWebLink() + "/favicon.ico";
                }
                if (!message.getWebLink().startsWith("http")) {
                    linkImgUrl = Constants.SOCKET_IP + linkImgUrl;
                }
                AppUtils.loadImage(mContext, linkImgUrl, vhWebReceived.ivWebLink, 60, R.drawable.link);
            }
        }
    }

    /**
     * ServerMessage
     *
     * @param vh14     specific view holder
     * @param position specific position
     */
    private void configureViewHolderServerMessage(final VHservermessage vh14, int position) {

        final MessageItemChat message = mListData.get(position);

        if (message != null) {


            vh14.tvServerMsgLbl.post(new Runnable() {
                @Override
                public void run() {
                    vh14.tvServerMsgLbl.setText(message.getTextMessage());
                }
            });

        }
    }

    /**
     * MessageReceived
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderMessageReceived(final VHMessageReceived vh2, final int position) {
        final MessageItemChat message = mListData.get(position);

        vh2.replaylayout_recevied.setVisibility(View.GONE);
        vh2.cameraphoto.setVisibility(View.GONE);
        vh2.time.setVisibility(View.GONE);
        vh2.ts_below.setVisibility(View.GONE);
        vh2.starred.setVisibility(View.GONE);
        vh2.starredindicator_below.setVisibility(View.GONE);
        vh2.time.setVisibility(View.GONE);
        vh2.relpaymessage_recevied.setVisibility(View.GONE);
        vh2.relpaymessage_receviedmedio.setVisibility(View.GONE);

        if (message.getReplyType() != null && !message.getReplyType().equals("")) {
            vh2.replaylayout_recevied.setVisibility(View.VISIBLE);
            vh2.relpaymessage_recevied.setVisibility(View.VISIBLE);
            vh2.relpaymessage_receviedmedio.setVisibility(View.VISIBLE);
            vh2.cameraphoto.setVisibility(View.VISIBLE);
            vh2.sentimage.setVisibility(View.VISIBLE);

            if (message.isStatusReply())
                vh2.lblMsgFrom2.setText("You . Status");
            else
                vh2.lblMsgFrom2.setText(mListData.get(position).getReplySenser());

            System.out.println("====name" + mListData.get(position).getReplySenser());

            if (Integer.parseInt(message.getReplyType()) == MessageFactory.video) {
                vh2.relpaymessage_recevied.setVisibility(View.GONE);
                if (message.isStatusReply()) {
                    vh2.relpaymessage_receviedmedio.setText("Status Video");
                } else {
                    vh2.relpaymessage_receviedmedio.setText("Video");
                }
                vh2.cameraphoto.setImageResource(R.drawable.video);
            } else if (Integer.parseInt(message.getReplyType()) == MessageFactory.picture) {
                vh2.relpaymessage_recevied.setVisibility(View.GONE);
                if (message.isStatusReply()) {
                    vh2.relpaymessage_receviedmedio.setText("Status Image");
                } else {
                    vh2.relpaymessage_receviedmedio.setText("Photo");
                }
                vh2.relpaymessage_recevied.setText("");
                vh2.cameraphoto.setImageResource(R.drawable.camera);
            } else if (Integer.parseInt(message.getReplyType()) == MessageFactory.location) {
                vh2.relpaymessage_recevied.setVisibility(View.GONE);
                vh2.relpaymessage_receviedmedio.setText(message.getReplyMessage());
                vh2.relpaymessage_recevied.post(new Runnable() {
                    @Override
                    public void run() {
                        vh2.relpaymessage_recevied.setText(message.getReplyMessage());
                    }
                });
                vh2.cameraphoto.setImageResource(R.drawable.map);
            } else if (Integer.parseInt(message.getReplyType()) == MessageFactory.contact) {
                vh2.relpaymessage_receviedmedio.setVisibility(View.GONE);
                if (message.getReplyMessage() != null && !message.getReplyMessage().equals("")) {
                    vh2.relpaymessage_recevied.post(new Runnable() {
                        @Override
                        public void run() {
                            vh2.relpaymessage_recevied.setText(message.getReplyMessage());
                        }
                    });
                } else {
                    vh2.relpaymessage_recevied.setText("Contact");
                }
                vh2.cameraphoto.setImageResource(R.drawable.contact);
            } else if (Integer.parseInt(message.getReplyType()) == MessageFactory.audio) {
                vh2.relpaymessage_recevied.setVisibility(View.GONE);
                vh2.cameraphoto.setImageResource(R.drawable.audio);
                vh2.relpaymessage_receviedmedio.setVisibility(View.VISIBLE);
                vh2.relpaymessage_receviedmedio.setText("Audio");
                vh2.lblMsgFrom2.setText(mListData.get(position).getReplySenser());
            } else if (Integer.parseInt(message.getReplyType()) == MessageFactory.document
                    || Integer.parseInt(message.getReplyType()) == MessageFactory.group_document_message) {
                vh2.cameraphoto.setImageResource(R.drawable.document);
                vh2.relpaymessage_recevied.setVisibility(View.GONE);
                if (message.getReplyMessage() != null && !message.getReplyMessage().equals("")) {
                    vh2.relpaymessage_receviedmedio.setText(message.getReplyMessage());
                } else {
                    vh2.relpaymessage_receviedmedio.setText("Document");
                }
                vh2.lblMsgFrom2.setText(mListData.get(position).getReplySenser());
            } else if (Integer.parseInt(message.getReplyType()) == MessageFactory.web_link) {
//                vh2.relpaymessage_receviedmedio.setText(message.getReplyMessage());.
                vh2.relpaymessage_recevied.setVisibility(View.GONE);
                vh2.sentimage.setVisibility(View.GONE);
                vh2.relpaymessage_receviedmedio.setText(message.getReplyMessage());
                vh2.cameraphoto.setImageResource(R.drawable.link);
            } else if (Integer.parseInt(message.getReplyType()) == MessageFactory.text) {
                vh2.relpaymessage_recevied.setText(message.getReplyMessage());
                vh2.cameraphoto.setVisibility(View.GONE);
                vh2.relpaymessage_receviedmedio.setVisibility(View.GONE);
            }

           /* final String image = message.getChatFileLocalPath();


                File file = new File(image);

                if (file.exists()) {

                    // vh2.imageView.setImageResource(R.drawable.chatapp_newlogo);

                    // vh2.forward_image.setVisibility(View.VISIBLE);
                    AppUtils.loadLocalImage(mContext, image, vh2.sentimage);


                }
*/


            String base64 = "" + message.getreplyimagebase64();
            base64 = base64.replace("data:image/jpeg;base64,", "");
            Bitmap imgpath = null;// = ChatappImageUtils.decodeBitmapFromBase64(base64, 30, 30);
            CryptLib cryptLib = null;
            try {
                cryptLib = new CryptLib();
                String thumbData = cryptLib.decryptCipherTextWithRandomIV(base64, mContext.getResources().getString(R.string.chatapp) + message.getConvId() + mContext.getResources().getString(R.string.adani));
                imgpath = ChatappImageUtils.decodeBitmapFromBase64(thumbData, 30, 30);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            /*Uri imgUri = Uri.parse(imgpath);
            vh2.sentimage.setImageURI(imgUri);*/

            vh2.sentimage.setImageBitmap(imgpath);

//            getcontactname.configProfilepic(vh2.sentimage, toUserId, false, true, R.mipmap.chat_attachment_profile_default_image_frame);

            /*vh2.sentimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ImageZoom.class);
                    intent.putExtra("image", imgpath);
                    mContext.startActivity(intent);
                }
            });*/
        }


        if (position == 0) {
            if (!message.isInfoMsg() && !message.isSelf()) {
                vh2.imageViewindicatior.setVisibility(View.VISIBLE);
            } else {
                vh2.imageViewindicatior.setVisibility(View.GONE);
            }
        } else if (mListData.get(position - 1).isInfoMsg() && !message.isSelf() && !message.isInfoMsg()) {
            vh2.imageViewindicatior.setVisibility(View.VISIBLE);
        } else {
            MessageItemChat prevmsg = mListData.get(position - 1);
            if ((!message.isSelf() == prevmsg.isSelf()) && !message.isInfoMsg()) {
                vh2.imageViewindicatior.setVisibility(View.VISIBLE);
            } else {
                vh2.imageViewindicatior.setVisibility(View.GONE);
            }

        }

        if (message.isSelected())
            vh2.selection_layout.setBackgroundColor(selectedItemColor);
            // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
        else
            vh2.selection_layout.setBackgroundColor(unSelectedItemColor);
        String textmessage = message.getTextMessage();
        if (!textmessage.contains("\n") && textmessage.length() <= 14) {
            vh2.time.setVisibility(View.VISIBLE);
            if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED))
                vh2.starredindicator_below.setVisibility(View.VISIBLE);
            else
                vh2.starredindicator_below.setVisibility(View.GONE);

            if (message != null) {

                if (isGroupChat) {

                    vh2.senderName.setText(getContactNameIfExists(message.getGroupMsgFrom()));


                    //  vh2.senderName.setText( getContactNameIfExists(message.getGroupMsgFrom()));

                    // vh2.senderName.setText(message.getSenderOriginalName());
//                    String msisdn = message.getSenderName();
//                    vh2.senderName.setText(getcontactname.getSendername(message.getGroupMsgFrom(), msisdn));
                } else {
                    vh2.senderName.setVisibility(View.GONE);
                }

                String ts = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                ts = ts.replace(".", "");
                vh2.time.setText(ts);

                try {
                    vh2.message.post(new Runnable() {
                        @Override
                        public void run() {
                            vh2.message.setText(message.getTextMessage());
                        }
                    });

                } catch (Exception e) {
                    MyLog.e(TAG, "Error", e);
                }

                vh2.relative_layout_message.setVisibility(View.VISIBLE);
                vh2.tvInfoMsg.setVisibility(View.GONE);
                if (message.isInfoMsg()) {
                    vh2.relative_layout_message.setVisibility(View.GONE);
                    vh2.tvInfoMsg.setVisibility(View.VISIBLE);

                    vh2.tvInfoMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            vh2.tvInfoMsg.setText(message.getTextMessage());
                        }
                    });
                }
            }
        } else {
            vh2.ts_below.setVisibility(View.VISIBLE);

            String ts = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            ts = ts.replace(".", "");
            vh2.ts_below.setText(ts);

            try {
                vh2.message.post(new Runnable() {
                    @Override
                    public void run() {
                        vh2.message.setText(message.getTextMessage());
                    }
                });

            } catch (Exception e) {
                MyLog.e(TAG, "Error", e);
            }
            if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED))
                vh2.starred.setVisibility(View.VISIBLE);
            else
                vh2.starred.setVisibility(View.GONE);

            if (message != null) {

                if (isGroupChat) {
                    vh2.senderName.setText(getContactNameIfExists(message.getGroupMsgFrom()));

                    //   vh2.senderName.setText(message.getSenderOriginalName());
                    // vh2.senderName.setText(message.getSenderName());
                } else {
                    vh2.senderName.setVisibility(View.GONE);
                }

                try {
                    vh2.message.post(new Runnable() {
                        @Override
                        public void run() {
                            vh2.message.setText(message.getTextMessage());
                        }
                    });

                } catch (Exception e) {
                    MyLog.e(TAG, "Error", e);
                }

                vh2.relative_layout_message.setVisibility(View.VISIBLE);
                vh2.tvInfoMsg.setVisibility(View.GONE);
                if (message.isInfoMsg()) {
                    vh2.relative_layout_message.setVisibility(View.GONE);
                    vh2.tvInfoMsg.setVisibility(View.VISIBLE);


                    vh2.tvInfoMsg.post(new Runnable() {
                        @Override
                        public void run() {
                            vh2.tvInfoMsg.setText(message.getTextMessage());
                        }
                    });
                }
            }

        }

    }

    /**
     * ImageReceived
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderImageReceived(final VHImageReceived vh2, final int position) {
        final MessageItemChat message = mListData.get(position);
        if (AppUtils.isNetworkAvailable(mContext)) {

            if (message != null) {

                //  vh2.senderName.setText(message.getSenderName());

                configureDateLabel(vh2.time, position);

                String ts = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                ts = ts.replace(".", "");

                if (position == 0) {
                    if (!message.isInfoMsg() && !message.isSelf()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    } else {
                        vh2.imageViewindicatior.setVisibility(View.GONE);
                    }
                } else if (mListData.get(position - 1).isInfoMsg() && !message.isSelf() && !message.isInfoMsg()) {
                    vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                } else {
                    MessageItemChat prevmsg = mListData.get(position - 1);
                    if ((!message.isSelf() == prevmsg.isSelf()) && !message.isInfoMsg()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    } else {
                        vh2.imageViewindicatior.setVisibility(View.GONE);
                    }

                }
                vh2.time.setVisibility(View.VISIBLE);

                if (message.getTextMessage() != null && !message.getTextMessage().equalsIgnoreCase("")) {
                    vh2.captiontext.setVisibility(View.VISIBLE);
                    vh2.rlTs.setVisibility(View.VISIBLE);
                    vh2.abovecaption_layout.setVisibility(View.GONE);

                    vh2.captiontext.post(new Runnable() {
                        @Override
                        public void run() {
                            vh2.captiontext.setText(message.getTextMessage());
                        }
                    });
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vh2.starredindicator_below.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredindicator_below.setVisibility(View.GONE);
                    }
                    vh2.time.setText(ts);
                } else {
                    vh2.rlTs.setVisibility(View.GONE);
                    vh2.abovecaption_layout.setVisibility(View.VISIBLE);
                    vh2.captiontext.setVisibility(View.GONE);
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vh2.starredindicator_above.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredindicator_above.setVisibility(View.GONE);
                    }
                    vh2.ts_abovecaption.setText(ts);
                }
                if (message.isSelected())
                    vh2.selection_layout.setBackgroundColor(selectedItemColor);
                else
                    vh2.selection_layout.setBackgroundColor(unSelectedItemColor);

                if (isGroupChat) {
                    vh2.senderName.setVisibility(View.VISIBLE);
                    String msisdn = message.getSenderName();
                    Log.e("grpName", getContactNameIfExists(message.getGroupMsgFrom()));
                    vh2.senderName.setText(getContactNameIfExists(message.getGroupMsgFrom()));

                    //   vh2.senderName.setText(message.getSenderOriginalName());

                    //   vh2.senderName.setText(getcontactname.getSendername(message.getGroupMsgFrom(), msisdn));
                } else {
                    vh2.senderName.setVisibility(View.GONE);
                }
                try {

                    if (!message.isSelf()) {
                        final String image = message.getChatFileLocalPath();

                        if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_COMPLETED) {
                            vh2.download.setVisibility(View.GONE);
                            vh2.pbUpload.setVisibility(View.GONE);

                            File file = new File(image);

                            if (file.exists()) {

                                // vh2.imageView.setImageResource(R.drawable.chatapp_newlogo);

                                // vh2.forward_image.setVisibility(View.VISIBLE);
                                AppUtils.loadLocalImage(mContext, image, vh2.imageView);

                                vh2.imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (!FirstItemSelected) {

                                            Intent intent = new Intent(mContext, ImageZoom.class);
                                            intent.putExtra("captiontext", TextUtils.isEmpty(vh2.captiontext.getText().toString()) ? "" : vh2.captiontext.getText().toString());
                                            intent.putExtra("image", image);
                                            mContext.startActivity(intent);

                                      /*  Intent intent = new Intent(mContext, ImageZoom.class);
                                        intent.putExtra("from", "media");
                                        intent.putExtra("image_list", imgs);
//                                        intent.putExtra("image_position", getImagePathPos(imgzoompath.get(position)));
                                        intent.putExtra("image_position", getImagePathPos(image));
                                        intent.putExtra("captiontext_list", imgscaption);
//                                        intent.putExtra("captiontext", imgscaption.get(getImagePathPos(imgzoompath.get(position))));
                                        intent.putExtra("captiontext", imgscaption.get(getImagePathPos(image)));
//                                        intent.putExtra("image", imgzoompath.get(position));
                                        intent.putExtra("image", image);

                                        mContext.startActivity(intent);*/
                                        }
                                    }
                                });
                            } else {
                                vh2.forward_image.setVisibility(View.GONE);
                                vh2.imageView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String thumbnailData = message.getThumbnailData();
                                        String imageDataBytes = thumbnailData.substring(thumbnailData.indexOf(",") + 1);
                                        byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
                                        Bitmap thumbBmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        vh2.imageView.setImageBitmap(thumbBmp);
                                        vh2.imageView.setOnClickListener(null);
                                    }
                                });
                            }

//                        vh2.forward_image.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                ArrayList<MessageItemChat> selectedChatItems = new ArrayList<>();
//                                selectedChatItems.add(message);
//
//                                Bundle fwdBundle = new Bundle();
//                                fwdBundle.putSerializable("MsgItemList", selectedChatItems);
//                                fwdBundle.putBoolean("FromChatapp", true);
//
//                                Intent intent = new Intent(mContext, ForwardContact.class);
//                                intent.putExtras(fwdBundle);
//                                mContext.startActivity(intent);
//                            }
//                        });
                        } else {
                            if (message.getUploadDownloadProgress() == 0) {
                                vh2.download.setVisibility(View.VISIBLE);
                                vh2.pbUpload.setVisibility(View.GONE);
                                long filesize = AppUtils.parseLong(message.getFileSize());
                                vh2.imagesize.setText(size(filesize));
                                vh2.imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (AppUtils.isNetworkAvailable(mContext)) {
                                            vh2.download.setVisibility(View.GONE);
                                            vh2.pbUpload.setVisibility(View.VISIBLE);
                                            vh2.pbUpload.setProgress(message.getUploadDownloadProgress());
                                        }

                                    }
                                });


                            } else {
                                vh2.download.setVisibility(View.GONE);
                                vh2.pbUpload.setVisibility(View.GONE);
                            }

                            try {
                                vh2.imageView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String thumbnailData = message.getThumbnailData();
                                        String imageDataBytes = thumbnailData.substring(thumbnailData.indexOf(",") + 1);
                                        byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
                                        Bitmap thumbBmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                        vh2.imageView.setImageBitmap(thumbBmp);
                                    }
                                });
                            } catch (Exception e) {
                                vh2.imageView.setImageBitmap(null);
                                MyLog.e(TAG, "Error", e);
                            }

                        }

                        if (AppUtils.isNetworkAvailable(mContext)) {
                            if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_DOWNLOADING) {
                                vh2.download.setVisibility(View.GONE);
                                vh2.pbUpload.setVisibility(View.VISIBLE);

                            }
                        } else {
                            Toast.makeText(mContext, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
                        }


                    }

                } catch (Exception e) {
                    MyLog.e(TAG, "Error", e);
                }
            }
        } else {
            Toast.makeText(mContext, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
            if (message.getUploadDownloadProgress() == 0) {
                vh2.download.setVisibility(View.VISIBLE);
                vh2.pbUpload.setVisibility(View.GONE);
                long filesize = AppUtils.parseLong(message.getFileSize());
                vh2.imagesize.setText(size(filesize));
                vh2.imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AppUtils.isNetworkAvailable(mContext)) {
                            vh2.download.setVisibility(View.GONE);
                            vh2.pbUpload.setVisibility(View.VISIBLE);
                            vh2.pbUpload.setProgress(message.getUploadDownloadProgress());
                        }

                    }
                });
            } else {
                vh2.download.setVisibility(View.VISIBLE);
                vh2.pbUpload.setVisibility(View.GONE);
            }

            try {
                vh2.imageView.post(new Runnable() {
                    @Override
                    public void run() {
                        String thumbnailData = message.getThumbnailData();
                        String imageDataBytes = thumbnailData.substring(thumbnailData.indexOf(",") + 1);
                        byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
                        Bitmap thumbBmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        vh2.imageView.setImageBitmap(thumbBmp);
                    }
                });
            } catch (Exception e) {
                vh2.imageView.setImageBitmap(null);
                MyLog.e(TAG, "Error", e);
            }
        }
    }


    /**
     * AudioReceived
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderAudioReceived(final VHAudioReceived vh2, final int position) {
        final MessageItemChat message = mListData.get(position);
        if (AppUtils.isNetworkAvailable(mContext)) {
            if (message != null) {
                // configureDateLabel(vh2.time, position);
                String ts = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                ts = ts.replace(".", "");
                vh2.time_ts.setText(ts);

                if (position == 0) {
                    if (!message.isInfoMsg() && !message.isSelf()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    } else {
                        vh2.imageViewindicatior.setVisibility(View.GONE);
                    }
                } else if (mListData.get(position - 1).isInfoMsg() && !message.isSelf() && !message.isInfoMsg()) {
                    vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                } else if (message.isInfoMsg() && mListData.get(position - 1).isInfoMsg()) {
                    vh2.imageViewindicatior.setVisibility(View.GONE);
                } else {
                    MessageItemChat prevmsg = mListData.get(position - 1);
                    if ((!message.isSelf() == prevmsg.isSelf()) && !message.isInfoMsg()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    } else {
                        vh2.imageViewindicatior.setVisibility(View.GONE);
                    }

                }

                if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                    vh2.starredindicator.setVisibility(View.VISIBLE);
                } else {
                    vh2.starredindicator.setVisibility(View.GONE);
                }

                if (message.isSelected())
                    vh2.selection_layout.setBackgroundColor(selectedItemColor);
                    // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
                else
                    vh2.selection_layout.setBackgroundColor(unSelectedItemColor);

                if (message.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                    vh2.audiotrack_layout_back.setVisibility(View.GONE);
                    vh2.record_image.setVisibility(View.VISIBLE);
                    vh2.record_icon.setVisibility(View.VISIBLE);
                    vh2.recodingduration.setVisibility(View.VISIBLE);
                    if (message.getDuration() != null) {
                        vh2.recodingduration.setText(message.getDuration());
                    }
                    try {
                        String[] array = message.getMessageId().split("-");
//                    if(message.get)
                        getcontactname.configProfilepic(vh2.record_image, array[1], false, false, R.mipmap.chat_attachment_profile_default_image_frame);
                    } catch (Exception e) {
                        Log.e(TAG, "configureViewHolderAudioReceived: ", e);
                    }
                } else {
                    vh2.audiotrack_layout_back.setVisibility(View.VISIBLE);
                    vh2.record_image.setVisibility(View.GONE);
                    vh2.record_icon.setVisibility(View.GONE);
                    vh2.recodingduration.setVisibility(View.GONE);
                    String duration = message.getDuration();
                    if (duration != null && !duration.equalsIgnoreCase("")) {
//                    String surationasec = getTimeString(AppUtils.parseLong(duration));
                        vh2.duration.setText(duration);
                    }
                }
                if (isGroupChat) {
                    vh2.senderName.setVisibility(View.VISIBLE);
                    String msisdn = message.getSenderName();

                    vh2.senderName.setText(getContactNameIfExists(message.getGroupMsgFrom()));

                    //   vh2.senderName.setText(message.getSenderOriginalName());

                    // vh2.senderName.setText(getcontactname.getSendername(message.getGroupMsgFrom(), msisdn));
                } else {
                    vh2.senderName.setVisibility(View.GONE);
                }

                try {
                    if (!message.isSelf()) {
                        vh2.sbDuration.setProgress(message.getPlayerCurrentPosition());

                        if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_COMPLETED) {
                            final Uri uri = Uri.parse(message.getChatFileLocalPath());

                            // Remove user dragging in seekbar
                            vh2.download.setVisibility(View.GONE);
                            vh2.pbdownload.setVisibility(View.GONE);
                            vh2.playButton.setVisibility(View.VISIBLE);
                            if (message.isMediaPlaying()) {
                                long value = message.getPlayerCurrentPosition() * 1000;
                                if (message.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                                    vh2.recodingduration.setText(getTimeString(value));
                                } else {
                                    vh2.duration.setText(getTimeString(value));
                                }
                                vh2.playButton.setImageResource(R.drawable.ic_pause);
                            } else {
                                vh2.playButton.setImageResource(R.drawable.ic_play);
                                if (message.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                                    vh2.recodingduration.setText(message.getDuration());
                                } else {
                                    vh2.duration.setText(message.getDuration());
                                }
                            }

                            vh2.playButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if (lastPlayedAt > -1) {
                                        mListData.get(lastPlayedAt).setIsMediaPlaying(false);
                                        mTimer.cancel();
                                        mPlayer.release();
                                    }

                                    if (lastPlayedAt != position) {
                                        playAudio(position, message, vh2.sbDuration, vh2.duration);
                                    } else {
                                        lastPlayedAt = -1;
                                    }

                                    notifyDataSetChanged();
                                }
                            });

                            vh2.sbDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                                @Override
                                public void onStopTrackingTouch(SeekBar seekBar) {
                                    // TODO Auto-generated method stub
                                    if (mListData.get(position).isMediaPlaying()) {
                                        mTimer.cancel();
                                        mPlayer.release();
                                        playAudio(position, message, vh2.sbDuration, vh2.duration);
                                    }
                                    try {
                                        notifyDataSetChanged();
                                    } catch (Exception e) {
                                        MyLog.e(TAG, "Error", e);
                                    }
                                }

                                @Override
                                public void onStartTrackingTouch(SeekBar seekBar) {
                                    // TODO Auto-generated method stub
                                }

                                @Override
                                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                    // TODO Auto-generated method stub
                                    try {
                                        if (position != RecyclerView.NO_POSITION && fromUser) {
                                            mListData.get(position).setPlayerCurrentPosition(progress);
                                        }
                                    } catch (Exception e) {
                                        Log.e(TAG, "onProgressChanged: ", e);
                                    }
                                }
                            });


                        } else {

                            if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START) {
                                vh2.download.setVisibility(View.VISIBLE);
                                vh2.playButton.setVisibility(View.GONE);
                                vh2.pbdownload.setVisibility(View.GONE);
                                vh2.download.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (AppUtils.isNetworkAvailable(mContext)) {
                                            vh2.download.setVisibility(View.GONE);
                                            vh2.pbdownload.setVisibility(View.VISIBLE);
                                            vh2.playButton.setVisibility(View.GONE);
                                        } else {
                                            Toast.makeText(mContext, "Please Check your Internet Connection", Toast.LENGTH_LONG).show();
                                        }


                                    }
                                });


                            } else if ((message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_DOWNLOADING)) {
                                vh2.playButton.setVisibility(View.GONE);
                            } else {
                                vh2.download.setVisibility(View.GONE);
                                vh2.pbdownload.setVisibility(View.VISIBLE);
                                vh2.playButton.setVisibility(View.VISIBLE);
                            }
                     /*   vh2.download.setVisibility(View.GONE);
                        vh2.playButton.setVisibility(View.GONE);
                        vh2.pbDownload.setVisibility(View.GONE);*/


                        }


                    }
                } catch (Exception e) {
                    MyLog.e(TAG, "Error", e);
                }

            }
        } else {
            if (message.getUploadDownloadProgress() == 0) {
                if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED))
                    vh2.starredindicator.setVisibility(View.VISIBLE);
                else
                    vh2.starredindicator.setVisibility(View.GONE);

                if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START) {
                    vh2.download.setVisibility(View.VISIBLE);
                    vh2.playButton.setVisibility(View.GONE);
                    vh2.pbdownload.setVisibility(View.GONE);
                    vh2.download.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (AppUtils.isNetworkAvailable(mContext)) {
                                vh2.download.setVisibility(View.GONE);
                                vh2.pbdownload.setVisibility(View.VISIBLE);
                                vh2.playButton.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(mContext, "Please Check your Internet Connection", Toast.LENGTH_LONG).show();
                            }


                        }
                    });


                } else if ((message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_DOWNLOADING)) {
                    vh2.playButton.setVisibility(View.GONE);
                }
            }
            Toast.makeText(mContext, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
        }


    }


    /**
     * VideoReceived
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderVideoReceived(final VHVideoReceived vh2, final int position) {
        final MessageItemChat message = mListData.get(position);
        if (AppUtils.isNetworkAvailable(mContext)) {

            if (message != null) {
                String ts = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                ts = ts.replace(".", "");

                String surationasec = "";
                //vh2.senderName.setText(message.getSenderName());

                if (isGroupChat) {
                    Log.e("grpName", "video--->" + getContactNameIfExists(message.getGroupMsgFrom()));
                    vh2.txtSenderName.setText(getContactNameIfExists(message.getGroupMsgFrom()));
                } else {
                    vh2.txtSenderName.setVisibility(View.GONE);
                }

                if (message.getDuration() != null) {
                    String duration = message.getDuration();
//                surationasec = getTimeString(AppUtils.parseLong(duration));
                    vh2.duration_above.setText(surationasec);
                    vh2.duration.setText(surationasec);
                }

                if (message.getTextMessage() != null && !message.getTextMessage().equalsIgnoreCase("")) {
                    vh2.video_belowlayout.setVisibility(View.VISIBLE);
                    vh2.videoabove_layout.setVisibility(View.GONE);
                    vh2.captiontext.setVisibility(View.VISIBLE);

                    vh2.captiontext.post(new Runnable() {
                        @Override
                        public void run() {
                            vh2.captiontext.setText(message.getTextMessage());
                        }
                    });
                    vh2.time.setText(ts);

                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {

                        vh2.starredindicator_below.setBackgroundResource(R.drawable.starred_white);
                        //   vh2.starredindicator_below.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredindicator_below.setBackgroundResource(0);

                        //   vh2.starredindicator_below.setVisibility(View.GONE);
                    }
                } else {
                    vh2.video_belowlayout.setVisibility(View.GONE);
                    vh2.videoabove_layout.setVisibility(View.VISIBLE);
                    vh2.captiontext.setVisibility(View.GONE);
                    vh2.ts_abovecaption.setText(ts);

                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {

                        vh2.starredindicator_above.setBackgroundResource(R.drawable.starred_white);

                        //vh2.starredindicator_above.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredindicator_above.setBackgroundResource(0);

//                    vh2.starredindicator_above.setVisibility(View.GONE);
                    }
                }

                if (position == 0) {
                    if (!message.isInfoMsg() && !message.isSelf()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    } else {
                        vh2.imageViewindicatior.setVisibility(View.GONE);
                    }
                } else if (mListData.get(position - 1).isInfoMsg() && !message.isSelf() && !message.isInfoMsg()) {
                    vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                } else if (message.isInfoMsg() && mListData.get(position - 1).isInfoMsg()) {
                    vh2.imageViewindicatior.setVisibility(View.GONE);
                } else {
                    MessageItemChat prevmsg = mListData.get(position - 1);
                    if ((!message.isSelf() == prevmsg.isSelf()) && !message.isInfoMsg()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    } else {
                        vh2.imageViewindicatior.setVisibility(View.GONE);
                    }

                }
                if (message.isSelected())
                    vh2.selection_layout.setBackgroundColor(selectedItemColor);
                    // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
                else
                    vh2.selection_layout.setBackgroundColor(unSelectedItemColor);

                vh2.download.setVisibility(View.GONE);

                try {
                    if (message.isSelf()) {
//                    Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(message.getChatFileLocalPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
//
//                    vh2.thumbnail.setImageBitmap(thumbBmp);

                        String thumbnailData = message.getThumbnailData();
                        String imageDataBytes = thumbnailData.substring(thumbnailData.indexOf(",") + 1);
                        byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
                        Bitmap thumbBmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        vh2.thumbnail.setImageBitmap(thumbBmp);
                        //  AppUtils.loadVideoThumbnail(mContext, message.getChatFileLocalPath(), vh2.thumbnail, 0.5f, 150);
                    } else {
                        boolean isFileNotExists = true;

                        if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_COMPLETED) {
                            vh2.pbdownload.setVisibility(View.GONE);
                            vh2.overlay.setVisibility(View.VISIBLE);
                            /**
                             * image already downloaded
                             * */
                            File file = new File(message.getChatFileLocalPath());
                            if (file.exists()) {
                                isFileNotExists = false;

//                            Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(message.getChatFileLocalPath(), MediaStore.Video.Thumbnails.MICRO_KIND);
//
//                            vh2.thumbnail.setImageBitmap(thumbBmp);


                                //    Glide.clear(vh2.thumbnail);

                                String thumbnailData = message.getThumbnailData();
                                String imageDataBytes = thumbnailData.substring(thumbnailData.indexOf(",") + 1);
                                byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
                                Bitmap thumbBmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                vh2.thumbnail.setImageBitmap(thumbBmp);

                            }

                        } else if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START) {
                            vh2.download.setVisibility(View.VISIBLE);
                            long filesize = AppUtils.parseLong(message.getFileSize());
                            vh2.imagesize.setText(size(filesize));
                            vh2.overlay.setVisibility(View.GONE);
                            vh2.pbdownload.setVisibility(View.GONE);
                            vh2.download.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (AppUtils.isNetworkAvailable(mContext)) {
                                        vh2.download.setVisibility(View.GONE);
                                        vh2.pbdownload.setVisibility(View.VISIBLE);
                                    }


                                }
                            });


                        } else {
                            vh2.overlay.setVisibility(View.GONE);
                            vh2.download.setVisibility(View.GONE);
                            vh2.pbdownload.setVisibility(View.VISIBLE);
                        }

                        if (isFileNotExists) {
                            vh2.thumbnail.post(new Runnable() {
                                @Override
                                public void run() {
                                    String thumbData = message.getThumbnailData();
                                    if (thumbData.startsWith("data:image/jpeg;base64,")) {
                                        thumbData = thumbData.replace("data:image/jpeg;base64,", "");
                                    }
                                    byte[] decodedString = Base64.decode(thumbData, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                    if (vh2.thumbnail != null)
                                        vh2.thumbnail.setImageBitmap(decodedByte);
                                }
                            });

                        }

                    }
                } catch (Exception e) {
                    MyLog.e(TAG, "Error", e);
                }

            }
        } else {
            vh2.videoabove_layout.setVisibility(View.GONE);
            if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                vh2.starredindicator_below.setBackgroundResource(R.drawable.starred_white);
            } else {
                vh2.starredindicator_below.setBackgroundResource(0);
            }
            boolean isFileNotExists = true;
            if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_NOT_START) {
                vh2.download.setVisibility(View.VISIBLE);
                long filesize = AppUtils.parseLong(message.getFileSize());
                vh2.imagesize.setText(size(filesize));
                vh2.overlay.setVisibility(View.GONE);
                vh2.pbdownload.setVisibility(View.GONE);
                vh2.download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AppUtils.isNetworkAvailable(mContext)) {
                            vh2.download.setVisibility(View.GONE);
                            vh2.pbdownload.setVisibility(View.VISIBLE);
                        } else {
                            Toast.makeText(mContext, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


            } else {
                vh2.overlay.setVisibility(View.GONE);
                vh2.download.setVisibility(View.VISIBLE);
            }

            if (isFileNotExists) {
                vh2.thumbnail.post(new Runnable() {
                    @Override
                    public void run() {
                        String thumbData = message.getThumbnailData();
                        if (thumbData.startsWith("data:image/jpeg;base64,")) {
                            thumbData = thumbData.replace("data:image/jpeg;base64,", "");
                        }
                        byte[] decodedString = Base64.decode(thumbData, Base64.DEFAULT);
                        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                        if (vh2.thumbnail != null)
                            vh2.thumbnail.setImageBitmap(decodedByte);
                    }
                });

            }
            Toast.makeText(mContext, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();

        }


    }


    /**
     * LocationReceived
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderLocationReceived(final VHLocationReceived vh2, final int position) {
        if (AppUtils.isNetworkAvailable(mContext)) {
            final MessageItemChat message = mListData.get(position);
            if (message != null) {

                if (message.isSelected())
                    vh2.selection_layout.setBackgroundColor(selectedItemColor);
                    // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
                else
                    vh2.selection_layout.setBackgroundColor(unSelectedItemColor);

                vh2.time.setVisibility(View.VISIBLE);

                String ts = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                ts = ts.replace(".", "");
                vh2.time.setText(ts);

                if (position == 0) {
                    if (!message.isInfoMsg() && !message.isSelf()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    } else {
                        vh2.imageViewindicatior.setVisibility(View.GONE);
                    }
                } else if (mListData.get(position - 1).isInfoMsg() && !message.isSelf()) {
                    vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                } else {
                    MessageItemChat prevmsg = mListData.get(position - 1);
                    if ((!message.isSelf() == prevmsg.isSelf()) && !message.isInfoMsg()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    } else {
                        vh2.imageViewindicatior.setVisibility(View.GONE);
                    }

                }

                if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                    vh2.starredindicator_below.setVisibility(View.VISIBLE);
                } else {
                    vh2.starredindicator_below.setVisibility(View.GONE);
                }


                if (isGroupChat) {
                    vh2.senderName.setVisibility(View.VISIBLE);
                    String msisdn = message.getSenderName();

                    vh2.senderName.setText(getContactNameIfExists(message.getGroupMsgFrom()));

                    //   vh2.senderName.setText(message.getSenderOriginalName());

                    //    vh2.senderName.setText(getcontactname.getSendername(message.getGroupMsgFrom(), msisdn));
                } else {
                    vh2.senderName.setVisibility(View.GONE);
                }

           /* String thumbData = message.getWebLinkImgThumb();
            if (thumbData != null) {
                if (thumbData.startsWith("data:image/jpeg;base64,")) {
                    thumbData = thumbData.replace("data:image/jpeg;base64,", "");
                }
                byte[] decodedString = Base64.decode(thumbData, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                vh2.ivMap.setImageBitmap(decodedByte);

                *//*Glide.with(mContext).
                        load(sessionmanager.getStaticMapUrl()).
                        crossFade().
                        centerCrop().
                        dontAnimate().
                        skipMemoryCache(true).
                        into(vh2.ivMap);*//*

                Glide.with(mContext).
                        load(R.drawable.google_map).
                        crossFade().
                        centerCrop().
                        dontAnimate().into(vh2.ivMap);

            } else {
                vh2.ivMap.setImageBitmap(null);
            }
*/
                //TODO tharani map
                Glide.with(mContext).
                        load(R.drawable.google_map).centerCrop().
                        dontAnimate().into(vh2.ivMap);
                //crossFade().



           /* if (message.getWebLinkImgUrl() != null) {


                ImageLoader imageLoader = CoreController.getInstance().getImageLoader();
                imageLoader.get(message.getWebLinkImgUrl(), new ImageLoader.ImageListener() {
                    @Override
                    public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                        if (response.getBitmap() != null) {
//                            vh2.ivMap.setImageBitmap(response.getBitmap());
                            Glide.with(mContext).
                                    load(sessionmanager.getStaticMapUrl()).
                                    placeholder(R.mipmap.chat_attachment_profile_default_image_frame).
                                    crossFade().
                                    centerCrop().
                                    dontAnimate().
                                    skipMemoryCache(true).
                                    into(vh2.ivMap);
                        }
                    }

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        vh2.ivMap.setImageBitmap(null);
                    }
                });
            } else {
                vh2.ivMap.setImageBitmap(null);
            }*/

            }
        } else {
            Toast.makeText(mContext, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
        }

    }


    /**
     * Contact Received
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderContactReceived(VHContactReceived vh2, final int position) {
        final MessageItemChat message = mListData.get(position);
        final String contactName = message.getContactName();
        final String contactNumber = message.getContactNumber();
        final String contactAvatar = message.getAvatarImageUrl();
        final String contactDetails = message.getDetailedContacts();
        final String contactChatappId = message.getContactChatappId();
        Boolean isChatappcontact = false;
        vh2.add.setVisibility(View.GONE);
        vh2.invite.setVisibility(View.GONE);
        if (message != null) {
            if (message.isSelected()) {
                vh2.selection_layout.setBackgroundColor(selectedItemColor);
                // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
            } else {
                vh2.selection_layout.setBackgroundColor(unSelectedItemColor);
            }
            if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                vh2.starredindicator_below.setVisibility(View.VISIBLE);
            } else {
                vh2.starredindicator_below.setVisibility(View.GONE);
            }
            if (isGroupChat) {
                vh2.senderName.setVisibility(View.VISIBLE);
                String msisdn = message.getSenderName();
                vh2.senderName.setText(getContactNameIfExists(message.getGroupMsgFrom()));

                //   vh2.senderName.setText(message.getSenderOriginalName());

                // vh2.senderName.setText(getcontactname.getSendername(message.getGroupMsgFrom(), msisdn));
            } else {
                vh2.senderName.setVisibility(View.GONE);
            }


            if (position == 0) {
                if (!message.isInfoMsg() && !message.isSelf()) {
                    vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                } else {
                    vh2.imageViewindicatior.setVisibility(View.GONE);
                }
            } else if (mListData.get(position - 1).isInfoMsg() && !message.isSelf() && !message.isInfoMsg()) {
                vh2.imageViewindicatior.setVisibility(View.VISIBLE);
            } else if (message.isInfoMsg() && mListData.get(position - 1).isInfoMsg()) {
                vh2.imageViewindicatior.setVisibility(View.GONE);
            } else {
                MessageItemChat prevmsg = mListData.get(position - 1);
                if ((!message.isSelf() == prevmsg.isSelf()) && !message.isInfoMsg()) {
                    vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                } else {
                    vh2.imageViewindicatior.setVisibility(View.GONE);
                }

            }

            String msisdn = message.getSenderName();
            vh2.senderName.setText(getcontactname.getSendername(message.getGroupMsgFrom(), msisdn));

            String ts = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            ts = ts.replace(".", "");
            vh2.time.setText(ts);

            getcontactname.configProfilepic(vh2.contactimage, contactChatappId, false, true, R.mipmap.contact_off);

            if (contactNumber != null) {
                if (contactNumber.equals("")) {
                    vh2.add.setVisibility(View.GONE);
                    vh2.contact_add_invite.setVisibility(View.GONE);
                    vh2.v1.setVisibility(View.GONE);
                    vh2.invite.setVisibility(View.GONE);
                    vh2.message1.setVisibility(View.GONE);

                } else {

                    if (contactChatappId != null && !contactChatappId.equals("")) {
                        if (!contactChatappId.equalsIgnoreCase(mCurrentUserId)) {
                            vh2.add.setVisibility(View.GONE);
                            vh2.add1.setVisibility(View.GONE);
                            vh2.invite1.setVisibility(View.GONE);
                            vh2.invite.setVisibility(View.GONE);
                            vh2.message1.setVisibility(View.VISIBLE);
                            vh2.v1.setVisibility(View.VISIBLE);
                        } else {
                            vh2.add.setVisibility(View.GONE);
                            vh2.add1.setVisibility(View.GONE);
                            vh2.invite1.setVisibility(View.GONE);
                            vh2.invite.setVisibility(View.GONE);
                            vh2.message1.setVisibility(View.GONE);
                            vh2.v1.setVisibility(View.GONE);
                        }
                    } else {
                        vh2.add.setVisibility(View.GONE);
                        vh2.add1.setVisibility(View.GONE);
                        vh2.invite1.setVisibility(View.GONE);
                        vh2.invite.setVisibility(View.VISIBLE);
                        vh2.v1.setVisibility(View.VISIBLE);
                        vh2.message1.setVisibility(View.GONE);
                    }

                }

            }

            vh2.invite1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    performUserInvite(contactName, contactDetails);
                    return false;
                }
            });
            vh2.add1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    Intent intent = new Intent(mContext, Savecontact.class);
                    intent.putExtra("name", contactName);
                    intent.putExtra("number", contactNumber);
                    mContext.startActivity(intent);

                    return false;
                }
            });
            vh2.contactName.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (contactChatappId == null || !contactChatappId.equalsIgnoreCase(mCurrentUserId)) {
                        Intent intent = new Intent(mContext, Savecontact.class);
                        intent.putExtra("name", contactName);
                        intent.putExtra("number", contactNumber);
                        intent.putExtra("contactList", contactDetails);
                        mContext.startActivity(intent);
                    }
                    return false;
                }
            });
            vh2.message1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checKForChatViewNavigation(contactNumber, contactName, contactAvatar, contactChatappId, "received");
                }
            });
            vh2.invite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    performUserInvite(contactName, contactDetails);
                    return false;
                }
            });
            vh2.add.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    Intent intent = new Intent(mContext, Savecontact.class);
                    intent.putExtra("name", contactName);
                    intent.putExtra("number", contactNumber);
                    mContext.startActivity(intent);

                    return false;
                }
            });

            try {
                vh2.contactName.setText(contactName);
                vh2.contactNumber.setText(contactNumber);
            } catch (Exception e) {
                MyLog.e(TAG, "Error", e);
            }
            if (contactNumber == null || contactNumber.isEmpty()) {
                vh2.contactNumber.setText("No Number");
            }


        }
    }


    /**
     * ChatView Navigation
     *
     * @param msisdn      input value(msisdn)
     * @param contactName input value(contactName)
     * @param image       input value(image)
     * @param perID       input value(perID)
     * @param fromTAG     input value(fromTAG)
     */
    private void checKForChatViewNavigation(String msisdn, String contactName, String image, String perID, String fromTAG) {
        ChatLockPojo lockPojo = getChatLockdetailfromDB();

        if (sessionmanager.getLockChatEnabled().equals("1") && lockPojo != null) {
            String stat = lockPojo.getStatus();
            String pwd = lockPojo.getPassword();

            String documentid = mCurrentUserId + "-" + perID;
            if (stat.equals("1")) {
                openUnlockChatDialog(documentid, stat, pwd, contactName, image, msisdn);
            } else {

                if (fromTAG.equalsIgnoreCase("received")) {
                    for (int i = 0; i < ChatappContactsService.contactEntries.size(); i++) {
                        String myStrPhno = msisdn.replace(sessionmanager.getCountryCodeOfCurrentUser(), "");
                        if (ChatappContactsService.contactEntries.get(i).getNumberInDevice().startsWith(sessionmanager.getCountryCodeOfCurrentUser())) {
                            if (ChatappContactsService.contactEntries.get(i).getNumberInDevice().equalsIgnoreCase(msisdn)) {
                                navigateTochatviewpage(ChatappContactsService.contactEntries.get(i).getFirstName(), msisdn, image, perID);
                            }
                        } else if (ChatappContactsService.contactEntries.get(i).getNumberInDevice().equalsIgnoreCase(myStrPhno)) {
                            navigateTochatviewpage(ChatappContactsService.contactEntries.get(i).getFirstName(), msisdn, image, perID);
                        } else if (i == ChatappContactsService.contactEntries.size() - 1) {
                            navigateTochatviewpage(msisdn, msisdn, image, perID);
                        }

                    }
                } else {
                    navigateTochatviewpage(contactName, msisdn, image, perID);
                }
            }
        } else {
            if (fromTAG.equalsIgnoreCase("received")) {

                Getcontactname objContact = new Getcontactname(mContext);
                if (perID != null && !perID.equals("")) {
                    boolean isAlreadyContact = objContact.isContactExists(perID);
                    if (!isAlreadyContact) {
                        navigateTochatviewpage(msisdn, msisdn, image, perID);
                    } else {
                        navigateTochatviewpage(contactName, msisdn, image, perID);
                    }
                }
            } else {
                navigateTochatviewpage(contactName, msisdn, image, perID);
            }
        }
    }


    /**
     * open UnlockChat Dialog
     *
     * @param documentid  input value(document id)
     * @param stat        input value(state)
     * @param pwd         input value(password)
     * @param contactname input value(contactname)
     * @param image       input value(image)
     * @param msisdn      input value(msisdn)
     */
    public void openUnlockChatDialog(String documentid, String stat, String pwd, String contactname, String image, String msisdn) {

        String convId = userInfoSession.getChatConvId(documentid);
        ChatLockPwdDialog dialog = new ChatLockPwdDialog();
        dialog.setTextLabel1("Enter your Password");
        dialog.setEditTextdata("Enter new Password");
        dialog.setforgotpwdlabel("Forgot Password");
        dialog.setHeader("Unlock Chat");
        dialog.setButtonText("Unlock");
        Bundle bundle = new Bundle();
        bundle.putString("convID", convId);
        bundle.putString("status", "1");
        bundle.putString("pwd", pwd);
        bundle.putString("contactName", contactname);
        bundle.putString("avatar", image);
        bundle.putString("msisdn", msisdn);
        String uid = documentid.split("-")[1];
        bundle.putString("docid", uid);
        bundle.putString("page", "chatlist");
        bundle.putString("type", "single");
        bundle.putString("from", mCurrentUserId);
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, "chatunLock");
    }


    /**
     * navigate To chat viewpage
     *
     * @param contactName   input value(contactName)
     * @param contactNumber input value(contactNumber)
     * @param image         input value(image)
     * @param perSonID      input value(perSonID)
     */
    private void navigateTochatviewpage(String contactName, String contactNumber, String image, String perSonID) {
        Intent intent = new Intent(mContext, ChatViewActivity.class);
        intent.putExtra("receiverUid", "");
        intent.putExtra("receiverName", "");
        intent.putExtra("documentId", perSonID);
        intent.putExtra("type", 0);
        intent.putExtra("backfrom", true);
        intent.putExtra("Username", contactName);
        intent.putExtra("msisdn", contactNumber);
        intent.putExtra("Image", image);
        mContext.startActivity(intent);
    }


    /**
     * MessageSent
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderMessageSent(final VHMessageSent vh2, final int position) {
        final MessageItemChat message = mListData.get(position);
        vh2.replaylayout.setVisibility(View.GONE);
        vh2.cameraphoto.setVisibility(View.GONE);
        vh2.sentimage.setVisibility(View.GONE);
        vh2.replaymessage.setVisibility(View.GONE);
        vh2.replaymessagemedio.setVisibility(View.GONE);
        vh2.clock.setVisibility(View.GONE);
        vh2.doubleTickBlue.setVisibility(View.GONE);
        vh2.time.setVisibility(View.GONE);
        vh2.timebelow.setVisibility(View.GONE);
        vh2.starredbelow.setVisibility(View.GONE);
        vh2.clockbelow.setVisibility(View.GONE);
        vh2.ivTickBelow.setVisibility(View.GONE);
        vh2.layout_reply_tsalign_above.setVisibility(View.GONE);
        vh2.layout_reply_tsalign.setVisibility(View.GONE);
        vh2.message_sent_singleChar_layout.setVisibility(View.GONE);
        Log.d(TAG, "isEmoji: " + AppUtils.isEmoji(message.getTextMessage()));
        if (message.getTextMessage().contains("\n")) {
            String[] aStrMsgArr = message.getTextMessage().split("\n");


            if (aStrMsgArr[0].length() < 10) {
                vh2.below_layout.setVisibility(View.GONE);
                vh2.message_sent_singleChar_layout.setVisibility(View.VISIBLE);
            }
            String ts = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            ts = ts.replace(".", "");
            vh2.ts_new.setText(ts);

            if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                vh2.star_new.setVisibility(View.VISIBLE);
            } else {
                vh2.star_new.setVisibility(View.GONE);
            }
            String status = message.getDeliveryStatus();
            if (message.isBlockedMessage()) {
                vh2.clock_new.setVisibility(View.GONE);
                vh2.ivSingleLineTick.setImageResource(R.mipmap.ic_single_tick);
                vh2.ivSingleLineTick.setVisibility(View.VISIBLE);
            } else {
                switch (status) {

                    case "22":
                        vh2.clock_new.setVisibility(View.GONE);
                        vh2.ivSingleLineTick.setImageResource(R.mipmap.ic_single_tick);
                        vh2.ivSingleLineTick.setVisibility(View.VISIBLE);
                        break;

                    case "3":
                        vh2.clock_new.setVisibility(View.GONE);
                        vh2.ivSingleLineTick.setImageResource(R.drawable.message_deliver_tick);
                        vh2.ivSingleLineTick.setVisibility(View.VISIBLE);

                        break;
                    case "2":
                        vh2.clock_new.setVisibility(View.GONE);
                        vh2.ivSingleLineTick.setImageResource(R.mipmap.ic_double_tick);
                        vh2.ivSingleLineTick.setVisibility(View.VISIBLE);

                        break;
                    case "1":

                        vh2.clock_new.setVisibility(View.GONE);
                        vh2.ivSingleLineTick.setImageResource(R.mipmap.ic_single_tick);
                        vh2.ivSingleLineTick.setVisibility(View.VISIBLE);
                        break;

                    default:
                        vh2.clock_new.setVisibility(View.VISIBLE);
                        vh2.ivSingleLineTick.setVisibility(View.GONE);
                        break;
                }

            }
        }
        Boolean replymsg = false;
        if (position == 0) {
            if (message.isInfoMsg() == false && message.isSelf()) {
                vh2.imageViewindicatior.setVisibility(View.VISIBLE);
            }
        } else {
            MessageItemChat prevmsg = mListData.get(position - 1);
            if (message.isSelf() == !prevmsg.isSelf()) {
                vh2.imageViewindicatior.setVisibility(View.VISIBLE);
            } else {
                vh2.imageViewindicatior.setVisibility(View.GONE);
            }

        }
        if (message.getReplyType() != null && !message.getReplyType().equals("")) {
            vh2.replaymessage.setVisibility(View.VISIBLE);
            vh2.replaylayout.setVisibility(View.VISIBLE);
            replymsg = true;
            if (message.isStatusReply()) {
                String name = getcontactname.getSendername(message.getReceiverID(), message.getMsisdn());
                if (name != null)
                    vh2.lblMsgFrom2.setText(name + " . Status");
            } else
                vh2.lblMsgFrom2.setText(message.getReplySenser());
            final String replyMsgText = message.getReplyMessage();
            vh2.replaymessage.post(new Runnable() {
                @Override
                public void run() {
                    vh2.replaymessage.setText(replyMsgText);
                }
            });

            int replyMsgType = Integer.parseInt(message.getReplyType());
            switch (replyMsgType) {
                case MessageFactory.audio: {
                    vh2.replaymessagemedio.setVisibility(View.VISIBLE);
                    vh2.replaymessage.setVisibility(View.GONE);
                    vh2.cameraphoto.setVisibility(View.VISIBLE);
                    vh2.cameraphoto.setImageResource(R.drawable.audio);
                    vh2.replaymessagemedio.setText("Audio");
                }
                break;

                case MessageFactory.video: {
                    vh2.sentimage.setVisibility(View.VISIBLE);
                    vh2.replaymessagemedio.setVisibility(View.VISIBLE);
                    vh2.replaymessage.setVisibility(View.GONE);
                    if (message.isStatusReply()) {
                        vh2.replaymessagemedio.setText("Status Video");
                    } else {
                        vh2.replaymessagemedio.setText("Video");
                    }
                    vh2.cameraphoto.setVisibility(View.VISIBLE);
                    vh2.cameraphoto.setImageResource(R.drawable.video);
                    Bitmap photo = ChatappImageUtils.decodeBitmapFromBase64(message.getreplyimagebase64(), 30, 30);
                    vh2.sentimage.setImageBitmap(photo);
                }
                break;

                case MessageFactory.document: {
                    vh2.replaymessagemedio.setVisibility(View.VISIBLE);
                    vh2.replaymessage.setVisibility(View.GONE);
                    vh2.cameraphoto.setVisibility(View.VISIBLE);
                    vh2.cameraphoto.setImageResource(R.drawable.document);
                    if (replyMsgText != null && !replyMsgText.equals("")) {
                        vh2.replaymessagemedio.setText(replyMsgText);
                    } else {
                        vh2.replaymessagemedio.setText("Document");
                    }
                }
                break;

                case MessageFactory.contact: {
                    vh2.replaymessagemedio.setVisibility(View.VISIBLE);
                    vh2.replaymessage.setVisibility(View.GONE);
                    vh2.cameraphoto.setVisibility(View.VISIBLE);
                    vh2.cameraphoto.setImageResource(R.drawable.contact);
                    vh2.replaymessagemedio.setText("Contact");
                    if (replyMsgText != null && !replyMsgText.equals("")) {
                        vh2.replaymessagemedio.setText(replyMsgText);
                    } else {
                        vh2.replaymessagemedio.setText("Contact");
                    }
                }
                break;

                case MessageFactory.location: {
                    vh2.sentimage.setVisibility(View.VISIBLE);
                    vh2.replaymessagemedio.setVisibility(View.VISIBLE);
                    vh2.replaymessage.setVisibility(View.GONE);
                    vh2.replaymessagemedio.setText("Location");
                    vh2.cameraphoto.setVisibility(View.VISIBLE);
                    vh2.cameraphoto.setImageResource(R.drawable.map);
                }
                break;

                case MessageFactory.picture: {
                    vh2.replaymessagemedio.setVisibility(View.VISIBLE);
                    vh2.replaylayout.setVisibility(View.VISIBLE);
                    vh2.cameraphoto.setVisibility(View.VISIBLE);
                    vh2.cameraphoto.setImageResource(R.mipmap.chat_attachment_camera_grey_icon_off);
                    vh2.sentimage.setVisibility(View.VISIBLE);

                    if (message.isStatusReply()) {
                        vh2.replaymessagemedio.setText("Status Image");
                    } else
                        vh2.replaymessagemedio.setText("Photo");
                }
                break;
            }
        }

       /* if (message.getreplyimgpath() != null && !message.getreplyimgpath().equals("")) {


            String thumbData = message.getreplyimagebase64();
            Bitmap thumbBmp = ChatappImageUtils.decodeBitmapFromBase64(thumbData, 50, 50);
            vh2.sentimage.setImageBitmap(thumbBmp);
        } else */
        if (message.getreplyimagebase64() != null && !message.getreplyimagebase64().equals("")) {
            String thumbData = message.getreplyimagebase64();
            Bitmap thumbBmp = ChatappImageUtils.decodeBitmapFromBase64(thumbData, 50, 50);
            vh2.sentimage.setImageBitmap(thumbBmp);
        } else {
            vh2.sentimage.setImageBitmap(null);
        }

        if (message != null) {
            if (message.isSelected())
                vh2.selection_layout.setBackgroundColor(selectedItemColor);
                // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
            else
                vh2.selection_layout.setBackgroundColor(unSelectedItemColor);
            String textmessage = message.getTextMessage();
            if (!textmessage.contains("\n") && textmessage.length() <= 15) {
                if (!replymsg) {
                    vh2.time.setVisibility(View.VISIBLE);
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vh2.starred.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starred.setVisibility(View.GONE);
                    }

                    // vh2.senderName.setText(message.getSenderName());
                    String ts = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                    ts = ts.replace(".", "");
                    vh2.time.setText(ts);
                    vh2.ts_new.setText(ts);

                    try {
                        vh2.message.post(new Runnable() {
                            @Override
                            public void run() {
                                vh2.message.setText(message.getTextMessage());
                            }
                        });


                    } catch (Exception e) {
                        MyLog.e(TAG, "Error", e);
                    }

                    String status = message.getDeliveryStatus();


          /*  if (status == null) {
                status = MessageFactory.DELIVERY_STATUS_SENT;
            }*/
                    if (message.isBlockedMessage()) {
                        vh2.clock.setVisibility(View.GONE);
                        vh2.doubleTickBlue.setVisibility(View.VISIBLE);
                        vh2.doubleTickBlue.setImageResource(R.mipmap.ic_single_tick);
                    } else {


                        switch (status) {
                            case "3":
                                vh2.clock.setVisibility(View.GONE);
                                vh2.doubleTickBlue.setVisibility(View.VISIBLE);
                                vh2.doubleTickBlue.setImageResource(R.drawable.message_deliver_tick);
                                break;
                            case "2":
                                vh2.clock.setVisibility(View.GONE);
                                vh2.doubleTickBlue.setVisibility(View.VISIBLE);
                                vh2.doubleTickBlue.setImageResource(R.mipmap.ic_double_tick);
                                break;
                            case "1":
                                vh2.clock.setVisibility(View.GONE);
                                vh2.doubleTickBlue.setVisibility(View.VISIBLE);
                                vh2.doubleTickBlue.setImageResource(R.mipmap.ic_single_tick);
                                break;
                            case "22":
                                vh2.clock.setVisibility(View.GONE);
                                vh2.doubleTickBlue.setVisibility(View.VISIBLE);
                                vh2.doubleTickBlue.setImageResource(R.mipmap.ic_single_tick);
                                break;

                            default:
                                vh2.clock.setVisibility(View.VISIBLE);
                                vh2.doubleTickBlue.setVisibility(View.GONE);
                                break;
                        }
                    }
                } else {
                    vh2.layout_reply_tsalign_above.setVisibility(View.VISIBLE);
                    vh2.ts_reply_above.setVisibility(View.VISIBLE);
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vh2.starredindicator_reply_above.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredindicator_reply_above.setVisibility(View.GONE);
                    }

                    String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                    mydate = mydate.replace(".", "");
                    vh2.ts_reply_above.setText(mydate);
                    try {
                        vh2.message.post(new Runnable() {
                            @Override
                            public void run() {
                                vh2.message.setText(message.getTextMessage());
                            }
                        });

                    } catch (Exception e) {
                        MyLog.e(TAG, "Error", e);
                    }

                    String status = message.getDeliveryStatus();
          /*  if (status == null) {
                status = MessageFactory.DELIVERY_STATUS_SENT;
            }*/
                    if (message.isBlockedMessage()) {
                        vh2.clock.setVisibility(View.GONE);
                        vh2.doubleTickBlue.setVisibility(View.VISIBLE);
                        vh2.doubleTickBlue.setImageResource(R.mipmap.ic_single_tick);
                    } else {

                        switch (status) {
                            case "3":
                                vh2.clock_reply_above.setVisibility(View.GONE);
                                vh2.single_tick_green_above_reply.setVisibility(View.VISIBLE);
                                vh2.single_tick_green_above_reply.setImageResource(R.drawable.message_deliver_tick);

                                break;
                            case "2":
                                vh2.clock_reply_above.setVisibility(View.GONE);
                                vh2.single_tick_green_above_reply.setVisibility(View.VISIBLE);
                                vh2.single_tick_green_above_reply.setImageResource(R.mipmap.ic_double_tick);

                                break;
                            case "1":
                                vh2.clock_reply_above.setVisibility(View.GONE);
                                vh2.single_tick_green_above_reply.setVisibility(View.VISIBLE);
                                vh2.single_tick_green_above_reply.setImageResource(R.mipmap.ic_single_tick);

                                break;
                            default:
                                vh2.clock_reply_above.setVisibility(View.VISIBLE);
                                vh2.single_tick_green_above_reply.setVisibility(View.GONE);

                                break;
                        }
                    }
                }
            } else {
                if (!replymsg) {
                    vh2.timebelow.setVisibility(View.VISIBLE);
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vh2.starredbelow.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredbelow.setVisibility(View.GONE);
                    }

                    String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                    mydate = mydate.replace(".", "");
                    vh2.timebelow.setText(mydate);
                    try {
                        vh2.message.post(new Runnable() {
                            @Override
                            public void run() {
                                vh2.message.setText(message.getTextMessage());
                            }
                        });
                    } catch (Exception e) {
                        MyLog.e(TAG, "Error", e);
                    }

                    String status = message.getDeliveryStatus();
          /*  if (status == null) {
                status = MessageFactory.DELIVERY_STATUS_SENT;
            }*/
                    if (message.isBlockedMessage()) {
                        vh2.clock.setVisibility(View.GONE);
                        vh2.doubleTickBlue.setVisibility(View.VISIBLE);
                        vh2.doubleTickBlue.setImageResource(R.mipmap.ic_single_tick);
                    } else {

                        switch (status) {
                            case "3":
                                vh2.clockbelow.setVisibility(View.GONE);
                                vh2.ivTickBelow.setImageResource(R.drawable.message_deliver_tick);
                                vh2.ivTickBelow.setVisibility(View.VISIBLE);

                                break;
                            case "2":
                                vh2.clockbelow.setVisibility(View.GONE);
                                vh2.ivTickBelow.setImageResource(R.mipmap.ic_double_tick);
                                vh2.ivTickBelow.setVisibility(View.VISIBLE);
                                break;
                            case "1":
                                vh2.clockbelow.setVisibility(View.GONE);
                                vh2.ivTickBelow.setImageResource(R.mipmap.ic_single_tick);
                                vh2.ivTickBelow.setVisibility(View.VISIBLE);
                                break;
                            default:
                                vh2.clockbelow.setVisibility(View.VISIBLE);
                                vh2.ivTickBelow.setVisibility(View.GONE);
                                break;
                        }
                    }
                } else {
                    vh2.layout_reply_tsalign.setVisibility(View.VISIBLE);
                    vh2.ts_reply.setVisibility(View.VISIBLE);
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vh2.starredindicator_reply.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredindicator_reply.setVisibility(View.GONE);
                    }

                    String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                    mydate = mydate.replace(".", "");
                    vh2.ts_reply.setText(mydate);
                    try {
                        vh2.message.post(new Runnable() {
                            @Override
                            public void run() {
                                vh2.message.setText(message.getTextMessage());
                            }
                        });
                    } catch (Exception e) {
                        MyLog.e(TAG, "Error", e);
                    }

                    String status = message.getDeliveryStatus();


          /*  if (status == null) {
                status = MessageFactory.DELIVERY_STATUS_SENT;
            }*/

                    if (message.isBlockedMessage()) {
                        vh2.clock.setVisibility(View.GONE);
                        vh2.doubleTickBlue.setVisibility(View.VISIBLE);
                        vh2.doubleTickBlue.setImageResource(R.mipmap.ic_single_tick);
                    } else {

                        switch (status) {
                            case "3":
                                vh2.clock_reply.setVisibility(View.GONE);
                                vh2.single_tick_green_below_reply.setVisibility(View.VISIBLE);
                                vh2.single_tick_green_below_reply.setImageResource(R.drawable.message_deliver_tick);


                                break;
                            case "2":
                                vh2.clock_reply.setVisibility(View.GONE);
                                vh2.single_tick_green_below_reply.setVisibility(View.VISIBLE);
                                vh2.single_tick_green_below_reply.setImageResource(R.mipmap.ic_double_tick);

                                break;
                            case "1":
                                vh2.clock_reply.setVisibility(View.GONE);
                                vh2.single_tick_green_below_reply.setVisibility(View.VISIBLE);
                                vh2.single_tick_green_below_reply.setImageResource(R.mipmap.ic_single_tick);

                                break;
                            default:
                                vh2.clock_reply.setVisibility(View.VISIBLE);
                                vh2.single_tick_green_below_reply.setVisibility(View.GONE);

                                break;
                        }
                    }
                }
            }


        }

    }

    /**
     * ImageSent
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderImageSent(final VHImageSent vh2, final int position) {
        try {
            final MessageItemChat message = mListData.get(position);
            if (message != null) {
                Boolean caption = false;

                // vh2.senderName.setText(message.getSenderName());
                String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                mydate = mydate.replace(".", "");

                if (message.isSelected())
                    vh2.selection_layout.setBackgroundColor(selectedItemColor);
                    // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
                else
                    vh2.selection_layout.setBackgroundColor(unSelectedItemColor);

                if (message.getTextMessage() != null && !message.getTextMessage().trim().equalsIgnoreCase("")) {
                    vh2.captiontext.setVisibility(View.VISIBLE);
                    vh2.ts_abovecaption.setVisibility(View.GONE);
                    vh2.time_layout.setVisibility(View.VISIBLE);

                    vh2.captiontext.post(new Runnable() {
                        @Override
                        public void run() {
                            vh2.captiontext.setText(message.getTextMessage());
                        }
                    });
                    caption = true;
                    vh2.time.setText(mydate);
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vh2.starredindicator_below.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredindicator_below.setVisibility(View.GONE);
                    }
                } else {
                    vh2.captiontext.setVisibility(View.GONE);
                    vh2.ts_abovecaption.setVisibility(View.VISIBLE);
                    vh2.time_layout.setVisibility(View.GONE);
                    vh2.ts_abovecaption.setText(mydate);
                    caption = false;
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vh2.starredindicator_above.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredindicator_above.setVisibility(View.GONE);
                    }

                }


                if (position == 0) {
                    if (!message.isInfoMsg() && message.isSelf()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    }
                } else {

                    MessageItemChat prevmsg = mListData.get(position - 1);
                    if (message.isSelf() == !prevmsg.isSelf()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    } else {
                        vh2.imageViewindicatior.setVisibility(View.GONE);
                    }

                }
                if ((message.getUploadStatus() == MessageFactory.UPLOAD_STATUS_UPLOADING &&
                        message.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_NOT_SENT))
                        || message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_DOWNLOADING) {
                    try {
                        vh2.rlDownload.setVisibility(View.VISIBLE);
                        vh2.pbUpload.setVisibility(View.VISIBLE);
                        vh2.ivPauseResumeIcon.setVisibility(View.VISIBLE);
                        vh2.ivPauseResumeIcon.setColorFilter(Color.WHITE);
                        vh2.pbUpload.setProgress(message.getUploadDownloadProgress());
                    } catch (Exception e) {
                        MyLog.e(TAG, "Error", e);
                    }
                } else {
                    vh2.rlDownload.setVisibility(View.GONE);
                    vh2.ivPauseResumeIcon.setVisibility(View.GONE);
                    vh2.pbUpload.setVisibility(View.GONE);
                }


                try {
                    String imgPath = message.getChatFileLocalPath();
                    if (message.getUploadStatus() == MessageFactory.UPLOAD_STATUS_COMPLETED) {
                        if (message.getChatFileLocalPath().contains("GooglePhotos_Files")) {
                            File file1 = new File(mContext.getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH_BACKUP + "/" + MessageFactory.GOOGLE_PHOTOS_PATH + "/");

                            if (file1.isDirectory()) {
                                String[] children = file1.list();
                                if (children != null) {
                                    for (int i = 0; i < children.length; i++) {
                                        Log.e("getName", children[i]);
                                        if (imgPath.contains(children[i])) {
                                            message.setChatFileLocalPath(mContext.getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH_BACKUP + "/" + MessageFactory.GOOGLE_PHOTOS_PATH + "/" + children[i]);
                                            String data = mContext.getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH_BACKUP + "/" + MessageFactory.GOOGLE_PHOTOS_PATH + "/" + children[i];
                                            if (isGroupChat) {
                                                db.updateChatMessage(message, MessageFactory.CHAT_TYPE_GROUP);
                                            } else {
                                                db.updateChatMessage(message, MessageFactory.CHAT_TYPE_SINGLE);
                                            }
                                            File file = new File(data);
                                            if (file.exists()) {
                                                // vh2.imageView.setImageResource(R.drawable.chatapp_newlogo);
                                                AppUtils.loadLocalImage(mContext, data, vh2.imageView);
                                            } else {
                                                vh2.imageView.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        try {
                                                            String thumbData = message.getThumbnailData();
                                                            String imageDataBytes = thumbData.substring(thumbData.indexOf(",") + 1);
                                                            byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
                                                            Bitmap thumbBmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                                            vh2.imageView.setImageBitmap(thumbBmp);
                                                        } catch (Exception e) {
                                                            Log.e(TAG, "run: ", e);
                                                        }
                                                    }
                                                });

                                            }
                                        }
                                    }
                                }
                            }

                        } else {
                            File file = new File(imgPath);
                            if (file.exists()) {
                                // vh2.imageView.setImageResource(R.drawable.chatapp_newlogo);
                                AppUtils.loadLocalImage(mContext, imgPath, vh2.imageView);
                            } else {
                                vh2.imageView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            String thumbData = message.getThumbnailData();
                                            String imageDataBytes = thumbData.substring(thumbData.indexOf(",") + 1);
                                            byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
                                            Bitmap thumbBmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                                            vh2.imageView.setImageBitmap(thumbBmp);
                                        } catch (Exception e) {
                                            Log.e(TAG, "run: ", e);
                                        }
                                    }
                                });

                            }
                        }
                    }


                    if (message.getUploadStatus() == MessageFactory.UPLOAD_STATUS_COMPLETED
                            && message.isSelf()) {
                        final String image = message.getChatFileLocalPath();
                        File imgFile = new File(image);

                        if (imgFile.exists()) {
                            // vh2.forward_image.setVisibility(View.VISIBLE);

                            vh2.imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (!FirstItemSelected) {
                                        Intent intent = new Intent(mContext, ImageZoom.class);
                                        intent.putExtra("image", image);
                                        intent.putExtra("captiontext", TextUtils.isEmpty(vh2.captiontext.getText().toString()) ? "" : vh2.captiontext.getText().toString());
                                        mContext.startActivity(intent);
                                   /* Intent intent = new Intent(mContext, ImageZoom.class);
                                    intent.putExtra("from", "media");
                                    intent.putExtra("image_list", imgs);
//                                    intent.putExtra("image_position", getImagePathPos(imgzoompath.get(position)));
                                    intent.putExtra("image_position", getImagePathPos(image));
                                    intent.putExtra("captiontext_list", imgscaption);
//                                    intent.putExtra("captiontext", imgscaption.get(getImagePathPos(imgzoompath.get(position))));
                                    intent.putExtra("captiontext", imgscaption.get(getImagePathPos(image)));
//                                    intent.putExtra("image", imgzoompath.get(position));
                                    intent.putExtra("image", image);

                                    mContext.startActivity(intent);*/
                                    }
                                }
                            });

//                        vh2.forward_image.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                ArrayList<MessageItemChat> selectedChatItems = new ArrayList<>();
//                                selectedChatItems.add(message);
//
//                                Bundle fwdBundle = new Bundle();
//                                fwdBundle.putSerializable("MsgItemList", selectedChatItems);
//                                fwdBundle.putBoolean("FromChatapp", true);
//
//                                Intent intent = new Intent(mContext, ForwardContact.class);
//                                intent.putExtras(fwdBundle);
//                                mContext.startActivity(intent);
//                            }
//                        });
                        }
                    } else {
                        vh2.forward_image.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    MyLog.e(TAG, "Error", e);
                }
                String status = message.getDeliveryStatus();

                if (status == null) {
                    status = MessageFactory.DELIVERY_STATUS_SENT;
                }
                if (!caption) {
                    vh2.ts_abovecaption_layout.setVisibility(View.VISIBLE);
                    vh2.rlMsgStatus_above.setVisibility(View.VISIBLE);
                    vh2.time_layout.setVisibility(View.GONE);
                    vh2.rlMsgStatus.setVisibility(View.GONE);
                    if (message.isBlockedMessage()) {
                        vh2.clock.setVisibility(View.GONE);
                        vh2.ivTickAbove.setVisibility(View.VISIBLE);
                        vh2.ivTickAbove.setImageResource(R.mipmap.ic_single_tick);
                    } else {
                        switch (status) {
                            case "3":
                                vh2.clock_above.setVisibility(View.GONE);
                                vh2.ivTickAbove.setVisibility(View.VISIBLE);
                                vh2.ivTickAbove.setImageResource(R.drawable.message_deliver_tick);
                                break;
                            case "2":
                                vh2.clock_above.setVisibility(View.GONE);
                                vh2.ivTickAbove.setVisibility(View.VISIBLE);
                                vh2.ivTickAbove.setImageResource(R.mipmap.ic_double_tick);

                                break;
                            case "1":
                                vh2.clock_above.setVisibility(View.GONE);
                                vh2.ivTickAbove.setVisibility(View.VISIBLE);
                                vh2.ivTickAbove.setImageResource(R.mipmap.ic_single_tick);
                                break;
                            default:
                                vh2.clock_above.setVisibility(View.VISIBLE);
                                vh2.ivTickAbove.setVisibility(View.GONE);
                                break;
                        }
                    }
                } else {
                    vh2.ts_abovecaption_layout.setVisibility(View.GONE);
                    vh2.time_layout.setVisibility(View.VISIBLE);
                    vh2.rlMsgStatus_above.setVisibility(View.GONE);
                    vh2.rlMsgStatus.setVisibility(View.VISIBLE);

                    if (message.isBlockedMessage()) {
                        vh2.clock.setVisibility(View.GONE);
                        vh2.ivTick.setVisibility(View.VISIBLE);
                        vh2.ivTick.setImageResource(R.mipmap.ic_single_tick);
                    } else {
                        switch (status) {
                            case "3":
                                vh2.clock.setVisibility(View.GONE);
                                vh2.ivTick.setVisibility(View.VISIBLE);
                                vh2.ivTick.setImageResource(R.drawable.message_deliver_tick);
                                break;
                            case "2":
                                vh2.clock.setVisibility(View.GONE);
                                vh2.ivTick.setVisibility(View.VISIBLE);
                                vh2.ivTick.setImageResource(R.mipmap.ic_double_tick);

                                break;
                            case "1":

                                vh2.clock.setVisibility(View.GONE);
                                vh2.ivTick.setVisibility(View.VISIBLE);
                                vh2.ivTick.setImageResource(R.mipmap.ic_single_tick);
                                break;
                            default:
                                vh2.clock.setVisibility(View.VISIBLE);
                                vh2.ivTick.setVisibility(View.GONE);
                                break;
                        }
                    }
                }

                if (fileUploadDownloadManager.isFilePaused(message.getMessageId())
                        && message.getUploadStatus() == MessageFactory.UPLOAD_STATUS_UPLOADING
                ) {
                    //show the pause icon
                    vh2.ivPauseResumeIcon.setVisibility(View.VISIBLE);
                    vh2.ivPauseResumeIcon.setTag("paused");
                    vh2.ivPauseResumeIcon.setImageResource(R.drawable.ic_action_reload);
                    vh2.ivPauseResumeIcon.setColorFilter(Color.WHITE);
                    //hide the progressbar & its bg
                    vh2.rlDownload.setVisibility(View.GONE);
                    vh2.pbUpload.setVisibility(View.GONE);
                }
                vh2.ivPauseResumeIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Not completed
                        if (message.getUploadStatus() != MessageFactory.UPLOAD_STATUS_COMPLETED) {
                            //already paused --> resume the upload
                            if (ConnectivityInfo.isInternetConnected(mContext)) {
                                if (v.getTag() != null && v.getTag().toString().equals("paused")) {
                                    v.setTag(null);
                                    vh2.rlDownload.setVisibility(View.VISIBLE);
                                    vh2.pbUpload.setVisibility(View.VISIBLE);
                                    fileUploadDownloadManager.setPauseFileStatus(message.getMessageId(), false);
                                    vh2.ivPauseResumeIcon.setImageResource(R.drawable.ic_pause);
                                    vh2.ivPauseResumeIcon.setColorFilter(Color.WHITE);
                                    JSONObject pauseFileObject = fileUploadDownloadManager.getPauseFileObject(message.getMessageId());
                                    if (pauseFileObject != null) {
                                        vh2.pbUpload.setVisibility(View.VISIBLE);
                                        fileUploadDownloadManager.startFileUpload(EventBus.getDefault(), pauseFileObject);
                                    }
                                    //if no bytes send -> we not get pauseFileObject. So again re-upload file
                                    else {
                                        //reupload image
                                        if (mContext instanceof ChatViewActivity) {
                                            ChatViewActivity chatViewActivity = (ChatViewActivity) mContext;
                                            String caption = "";
                                            if (message.getCaption() != null)
                                                caption = message.getCaption();
                                            chatViewActivity.sendImageChatMessage(message.getImagePath(), caption, true);
                                        }
                                    }
                                } else {
                                    v.setTag("paused");
                                    vh2.rlDownload.setVisibility(View.GONE);
                                    vh2.pbUpload.setVisibility(View.GONE);
                                    vh2.pbUpload.setVisibility(View.GONE);
                                    vh2.ivPauseResumeIcon.setImageResource(R.drawable.ic_action_reload);
                                    vh2.ivPauseResumeIcon.setColorFilter(Color.WHITE);
                                    fileUploadDownloadManager.setPauseFileStatus(message.getMessageId(), true);
                                }
                                //first time --> pause the upload
                            } else {
                                Toast.makeText(mContext, mContext.getString(R.string.check_internet_connection), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Log.d(TAG, "configureViewHolderImageSent: end");
    }

    /**
     * VideoSent
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderVideoSent(final VHVideoSent vh2, final int position) {
        try {
            final MessageItemChat message = mListData.get(position);
            Boolean caption = false;
            String surationasec = "";
            if (message != null) {
                // vh2.senderName.setText(message.getSenderName());
                String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                mydate = mydate.replace(".", "");

                MediaMetadataRetriever mdr = new MediaMetadataRetriever();
                if (message.getUploadStatus() == MessageFactory.UPLOAD_STATUS_COMPLETED) {
                    if (message.getChatFileLocalPath() != null) {
                        String uri = message.getChatFileLocalPath();
                        if (message.getChatFileLocalPath().contains("GooglePhotos_Files")) {
                            File file1 = new File(mContext.getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH_BACKUP + "/" + MessageFactory.GOOGLE_PHOTOS_PATH + "/");

                            if (file1.isDirectory()) {
                                String[] children = file1.list();
                                if (children != null) {
                                    for (int i = 0; i < children.length; i++) {
                                        Log.e("getName", children[i]);
                                        if (uri.contains(children[i])) {
                                            message.setChatFileLocalPath(mContext.getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH_BACKUP + "/" + MessageFactory.GOOGLE_PHOTOS_PATH + "/" + children[i]);
                                            String data = mContext.getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH_BACKUP + "/" + MessageFactory.GOOGLE_PHOTOS_PATH + "/" + children[i];
                                            if (isGroupChat) {
                                                db.updateChatMessage(message, MessageFactory.CHAT_TYPE_GROUP);
                                            } else {
                                                db.updateChatMessage(message, MessageFactory.CHAT_TYPE_SINGLE);
                                            }
                                            try {
                                                mdr.setDataSource(data);
                                                String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                                surationasec = getTimeString(AppUtils.parseLong(duration));

                                            } catch (Exception e) {
                                                MyLog.e(TAG, "Error", e);
                                            }
                                            //TODO tharani map
                                            Glide.with(mContext).clear(vh2.thumbnail);

                                            vh2.thumbnail.setImageBitmap(ThumbnailUtils.createVideoThumbnail(data,
                                                    MediaStore.Images.Thumbnails.MINI_KIND));
                                        }
                                    }
                                }
                            }


                        } else {
                            if (message.getChatFileLocalPath() != null) {
                                try {
                                    mdr.setDataSource(message.getChatFileLocalPath());
                                    String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                    surationasec = getTimeString(AppUtils.parseLong(duration));

                                } catch (Exception e) {
                                    MyLog.e(TAG, "Error", e);
                                }
                                //TODO tharani map
                                Glide.with(mContext).clear(vh2.thumbnail);

                                vh2.thumbnail.setImageBitmap(ThumbnailUtils.createVideoThumbnail(message.getVideoPath(),
                                        MediaStore.Images.Thumbnails.MINI_KIND));
                            }
                        }

                    }
                } else {
                    if (message.getChatFileLocalPath() != null) {
                        String uri = message.getChatFileLocalPath();
                        if (message.getChatFileLocalPath().contains("GooglePhotos_Files")) {
                            File file1 = new File(mContext.getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH_BACKUP + "/" + MessageFactory.GOOGLE_PHOTOS_PATH + "/");

                            if (file1.isDirectory()) {
                                String[] children = file1.list();
                                if (children != null) {
                                    for (int i = 0; i < children.length; i++) {
                                        Log.e("getName", children[i]);
                                        if (uri.contains(children[i])) {
                                            message.setChatFileLocalPath(mContext.getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH_BACKUP + "/" + MessageFactory.GOOGLE_PHOTOS_PATH + "/" + children[i]);
                                            String data = mContext.getFilesDir() + MessageFactory.GOOGLE_PHOTOS_PATH_BACKUP + "/" + MessageFactory.GOOGLE_PHOTOS_PATH + "/" + children[i];
                                            if (isGroupChat) {
                                                db.updateChatMessage(message, MessageFactory.CHAT_TYPE_GROUP);
                                            } else {
                                                db.updateChatMessage(message, MessageFactory.CHAT_TYPE_SINGLE);
                                            }
                                            try {
                                                mdr.setDataSource(data);
                                                String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                                surationasec = getTimeString(AppUtils.parseLong(duration));

                                            } catch (Exception e) {
                                                MyLog.e(TAG, "Error", e);
                                            }
                                            //TODO tharani map
                                            Glide.with(mContext).clear(vh2.thumbnail);

                                            vh2.thumbnail.setImageBitmap(ThumbnailUtils.createVideoThumbnail(data,
                                                    MediaStore.Images.Thumbnails.MINI_KIND));
                                        }
                                    }
                                }
                            }


                        } else {
                            if (message.getChatFileLocalPath() != null) {
                                try {
                                    mdr.setDataSource(message.getChatFileLocalPath());
                                    String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                                    surationasec = getTimeString(AppUtils.parseLong(duration));

                                } catch (Exception e) {
                                    MyLog.e(TAG, "Error", e);
                                }
                                //TODO tharani map
                                Glide.with(mContext).clear(vh2.thumbnail);

                                vh2.thumbnail.setImageBitmap(ThumbnailUtils.createVideoThumbnail(message.getVideoPath(),
                                        MediaStore.Images.Thumbnails.MINI_KIND));
                            }
                        }

                    }
                }


                if (message.getTextMessage() != null && !message.getTextMessage().trim().equalsIgnoreCase("")) {
                    caption = true;
                    vh2.videoabove_layout.setVisibility(View.GONE);
                    vh2.video_belowlayout.setVisibility(View.VISIBLE);
                    vh2.captiontext.setVisibility(View.VISIBLE);
                    vh2.captiontext.post(new Runnable() {
                        @Override
                        public void run() {
                            vh2.captiontext.setText(message.getTextMessage());
                        }
                    });
                    vh2.time.setText(mydate);
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {

                        vh2.starredindicator_below.setBackgroundResource(R.drawable.starred_white);
                        vh2.starredindicator_below.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredindicator_below.setBackgroundResource(0);


                        //   vh2.starredindicator_below.setVisibility(View.GONE);
                    }
                    vh2.duration.setText(surationasec);

                } else {

                    vh2.videoabove_layout.setVisibility(View.VISIBLE);
                    vh2.video_belowlayout.setVisibility(View.GONE);
                    vh2.captiontext.setVisibility(View.GONE);
                    vh2.ts_abovecaption.setText(mydate);
                    caption = false;
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {

                        vh2.starredindicator_above.setBackgroundResource(R.drawable.starred_white);
                        vh2.starredindicator_above.setVisibility(View.VISIBLE);

                    } else {
                        vh2.starredindicator_below.setBackgroundResource(0);
                        vh2.starredindicator_above.setVisibility(View.GONE);

                    }
                    vh2.duration_above.setText(surationasec);
                }

                if (position == 0) {
                    if (!message.isInfoMsg() && message.isSelf()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    }
                } else {

                    MessageItemChat prevmsg = mListData.get(position - 1);
                    if (message.isSelf() == !prevmsg.isSelf()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    } else {
                        vh2.imageViewindicatior.setVisibility(View.GONE);
                    }

                }
                if (message.isSelected())
                    vh2.selection_layout.setBackgroundColor(selectedItemColor);

                else
                    vh2.selection_layout.setBackgroundColor(unSelectedItemColor);

                // Glide.clear(vh2.thumbnail);


//            String thumbnailData = message.getThumbnailData();
//            String imageDataBytes = thumbnailData.substring(thumbnailData.indexOf(",") + 1);
//            byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
//            Bitmap thumbBmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//            vh2.thumbnail.setImageBitmap(thumbBmp);

//                vh2.thumbnail.setImageBitmap(ThumbnailUtils.createVideoThumbnail(message.getVideoPath(),
//                        MediaStore.Images.Thumbnails.MINI_KIND));

                if (message.getUploadStatus() == MessageFactory.UPLOAD_STATUS_UPLOADING &&
                        message.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_NOT_SENT)) {
                    try {
                        vh2.ivPlay.setVisibility(View.GONE);
                        vh2.pbUpload.setVisibility(View.VISIBLE);
                        vh2.ivPauseResume.setVisibility(View.VISIBLE);
                        vh2.ivPauseResume.setColorFilter(Color.WHITE);
                        vh2.pbUpload.setProgress(sharedprf_video_uploadprogress.getfileuploadingprogress(message.getMessageId()));
                    } catch (Exception e) {
                        MyLog.e(TAG, "Error", e);
                    }
                } else {
                    vh2.pbUpload.setVisibility(View.GONE);
                    vh2.ivPauseResume.setVisibility(View.GONE);
                    vh2.ivPlay.setVisibility(View.VISIBLE);
                }

                vh2.thumbnail.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        try {
                            if (message.isSelf()) {
                                try {
                                    if (!FirstItemSelected) {
                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(message.getChatFileLocalPath()));
                                        String path = "file://" + message.getChatFileLocalPath();
                                        intent.setDataAndType(Uri.parse(path), "video/*");
                                        mContext.startActivity(intent);
                                    }
                                } catch (ActivityNotFoundException e) {
                                    Toast.makeText(mContext, "No app installed to play this video", Toast.LENGTH_LONG).show();
                                }
                            }
                        } catch (Exception e) {
                        }
                    }
                });


                String status = message.getDeliveryStatus();
                if (!caption) {
                    vh2.videoabove_layout.setVisibility(View.VISIBLE);
                    vh2.video_belowlayout.setVisibility(View.GONE);
                    if (message.isBlockedMessage()) {
                        vh2.clock.setVisibility(View.GONE);
                        vh2.ivTickAbove.setVisibility(View.VISIBLE);
                        vh2.ivTickAbove.setImageResource(R.mipmap.ic_single_tick);
                    } else {
                        switch (status) {
                            case "3":
                                vh2.clock_above.setBackgroundResource(0);
                                vh2.ivTickAbove.setBackgroundResource(R.drawable.message_deliver_tick);
                                vh2.ivTickAbove.setVisibility(View.VISIBLE);

                                break;
                            case "2":

                                vh2.clock_above.setBackgroundResource(0);
                                vh2.ivTickAbove.setBackgroundResource(R.mipmap.ic_double_tick);
                                vh2.ivTickAbove.setVisibility(View.VISIBLE);
                                break;
                            case "1":
                                vh2.clock_above.setBackgroundResource(0);
                                vh2.ivTickAbove.setBackgroundResource(R.mipmap.ic_single_tick);
                                vh2.ivTickAbove.setVisibility(View.VISIBLE);
                                break;
                            default:
                                vh2.clock_above.setBackgroundResource(R.drawable.clock);
                                vh2.ivTickAbove.setBackgroundResource(0);
                                vh2.ivTickAbove.setVisibility(View.GONE);
                                vh2.clock.setVisibility(View.VISIBLE);
                                break;
                        }
                    }
                } else {

                    vh2.videoabove_layout.setVisibility(View.GONE);
                    vh2.video_belowlayout.setVisibility(View.VISIBLE);
                    if (message.isBlockedMessage()) {
                        vh2.clock.setVisibility(View.GONE);
                        vh2.ivTick.setVisibility(View.VISIBLE);
                        vh2.ivTick.setImageResource(R.mipmap.ic_single_tick);
                    } else {
                        switch (status) {
                            case "3":
                                vh2.clock.setBackgroundResource(0);
                                vh2.ivTick.setBackgroundResource(R.drawable.message_deliver_tick);
                                vh2.ivTick.setVisibility(View.VISIBLE);

                                break;
                            case "2":

                                vh2.clock.setBackgroundResource(0);
                                vh2.ivTick.setBackgroundResource(R.mipmap.ic_double_tick);
                                vh2.ivTick.setVisibility(View.VISIBLE);

                                break;
                            case "1":
                                vh2.clock.setBackgroundResource(0);
                                vh2.ivTick.setBackgroundResource(R.mipmap.ic_single_tick);
                                vh2.ivTick.setVisibility(View.VISIBLE);
                                break;
                            default:
                                vh2.clock.setBackgroundResource(R.drawable.clock);
                                vh2.ivTick.setBackgroundResource(0);
                                vh2.ivTick.setVisibility(View.GONE);
                                vh2.clock.setVisibility(View.VISIBLE);
                                break;
                        }
                    }

                }


                if (fileUploadDownloadManager.isFilePaused(message.getMessageId())
                        && message.getUploadStatus() == MessageFactory.UPLOAD_STATUS_UPLOADING
                ) {
                    //show the pause icon
                    vh2.ivPauseResume.setVisibility(View.VISIBLE);
                    vh2.ivPauseResume.setTag("paused");
                    vh2.ivPauseResume.setImageResource(R.drawable.ic_action_reload);
                    vh2.ivPauseResume.setColorFilter(Color.WHITE);
                    //hide the progressbar & its bg

                    vh2.pbUpload.setVisibility(View.GONE);
                }

                vh2.ivPauseResume.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ConnectivityInfo.isInternetConnected(mContext)) {
                            //Not completed
                            if (message.getUploadStatus() != MessageFactory.UPLOAD_STATUS_COMPLETED) {
                                //already paused --> resume the upload
                                if (v.getTag() != null && v.getTag().toString().equals("paused")) {
                                    v.setTag(null);
                                    vh2.pbUpload.setVisibility(View.VISIBLE);
                                    fileUploadDownloadManager.setPauseFileStatus(message.getMessageId(), false);
                                    vh2.ivPauseResume.setImageResource(R.drawable.ic_pause);
                                    vh2.ivPauseResume.setColorFilter(Color.WHITE);
                                    JSONObject pauseFileObject = fileUploadDownloadManager.getPauseFileObject(message.getMessageId());
                                    if (pauseFileObject != null) {
                                        vh2.pbUpload.setVisibility(View.VISIBLE);
                                        fileUploadDownloadManager.startFileUpload(EventBus.getDefault(), pauseFileObject);
                                    }
                                    //if no bytes send -> we not get pauseFileObject. So again re-upload file
                                    else {
                                        //reupload image
                                        if (mContext instanceof ChatViewActivity) {
                                            ChatViewActivity chatViewActivity = (ChatViewActivity) mContext;
                                            String caption = "";
                                            if (message.getCaption() != null)
                                                caption = message.getCaption();
                                            chatViewActivity.sendVideoChatMessage(message.getVideoPath(), caption, true);
                                        }
                                    }
                                }
                                //first time --> pause the upload
                                else {
                                    v.setTag("paused");
                                    vh2.pbUpload.setVisibility(View.GONE);
                                    vh2.pbUpload.setVisibility(View.GONE);
                                    vh2.ivPauseResume.setImageResource(R.drawable.ic_action_reload);
                                    vh2.ivPauseResume.setColorFilter(Color.WHITE);
                                    fileUploadDownloadManager.setPauseFileStatus(message.getMessageId(), true);
                                }
                            }
                        } else {
                            Toast.makeText(mContext, "Please check your Internet Connection.", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * LocationSent
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderLocationSent(final VHLocationSent vh2, final int position) {
        try {
            final MessageItemChat message = mListData.get(position);
            if (message != null) {

                if (message.isSelected())
                    vh2.selection_layout.setBackgroundColor(selectedItemColor);
                    // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
                else
                    vh2.selection_layout.setBackgroundColor(unSelectedItemColor);

                // vh2.senderName.setText(message.getSenderName());
                String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                mydate = mydate.replace(".", "");
                vh2.time.setText(mydate);
                if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                    vh2.starredindicator_below.setVisibility(View.VISIBLE);
                } else {
                    vh2.starredindicator_below.setVisibility(View.GONE);
                }

                if (position == 0) {
                    if (message.isInfoMsg() == false && message.isSelf()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    }
                } else {

                    MessageItemChat prevmsg = mListData.get(position - 1);
                    if (message.isSelf() == !prevmsg.isSelf()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    } else {
                        vh2.imageViewindicatior.setVisibility(View.GONE);
                    }

                }
                if (message.getUploadStatus() == MessageFactory.UPLOAD_STATUS_UPLOADING) {
                    try {
                        vh2.pbUpload.setVisibility(View.VISIBLE);
                        vh2.pbUpload.setProgress(message.getUploadDownloadProgress());
                    } catch (Exception e) {
                        MyLog.e(TAG, "Error", e);
                    }
                } else {
                    vh2.pbUpload.setVisibility(View.GONE);
                }
            /*byte[] decodedString = Base64.decode(message.getThumbnailData(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            vh2.ivMap.setImageBitmap(decodedByte); // image for loading...*/
//            Picasso.with(mContext).load(message.getWebLinkImgUrl()).error(R.drawable.ic_add_user).into(vh2.ivMap);

            /*if (message.getWebLinkImgThumb() != null) {
                String thumbData = message.getWebLinkImgThumb();
                if (thumbData != null) {
//                    if (thumbData.startsWith("data:image/jpeg;base64,")) {
//                        thumbData = thumbData.replace("data:image/jpeg;base64,", "");
//                    }
//                    byte[] decodedString = Base64.decode(thumbData, Base64.DEFAULT);
//                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
//                    vh2.ivMap.setImageBitmap(decodedByte);
                    Glide.with(mContext).
                            load(sessionmanager.getStaticMapUrl()).
                            placeholder(R.mipmap.chat_attachment_profile_default_image_frame).
                            crossFade().
                            centerCrop().
                            dontAnimate().
                            skipMemoryCache(true).
                            into(vh2.ivMap);
                } else {
                    vh2.ivMap.setImageBitmap(null);
                }
            } else {
                vh2.ivMap.setImageBitmap(null);
            }*/
//TODO tharani map
                Glide.with(mContext).
                        load(R.drawable.google_map).centerCrop().
                        dontAnimate().into(vh2.ivMap);
                //crossFade().



/*
            ImageLoader imageLoader = CoreController.getInstance().getImageLoader();
            imageLoader.get(message.getWebLinkImgUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
//                        vh2.ivMap.setImageBitmap(response.getBitmap());
                        Glide.with(mContext).
                                load(sessionmanager.getStaticMapUrl()).
                                placeholder(R.mipmap.chat_attachment_profile_default_image_frame).
                                crossFade().
                                centerCrop().
                                dontAnimate().
                                skipMemoryCache(true).
                                into(vh2.ivMap);
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    if (message.getWebLinkImgThumb() != null) {
                        String thumbData = message.getWebLinkImgThumb();
                        if (thumbData != null) {
                           *//* if (thumbData.startsWith("data:image/jpeg;base64,")) {
                                thumbData = thumbData.replace("data:image/jpeg;base64,", "");
                            }
                            byte[] decodedString = Base64.decode(thumbData, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            vh2.ivMap.setImageBitmap(decodedByte);*//*
                            Glide.with(mContext).
                                    load(sessionmanager.getStaticMapUrl()).
                                    placeholder(R.mipmap.chat_attachment_profile_default_image_frame).
                                    crossFade().
                                    centerCrop().
                                    dontAnimate().
                                    skipMemoryCache(true).
                                    into(vh2.ivMap);
                        } else {
                            vh2.ivMap.setImageBitmap(null);
                        }
                    } else {
                        vh2.ivMap.setImageBitmap(null);
                    }
                }
            });*/

                String status = message.getDeliveryStatus();
                if (message.isBlockedMessage()) {
                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.VISIBLE);

                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
                } else {
                    switch (status) {
                        case "3":

                            vh2.clock.setVisibility(View.GONE);
                            vh2.singleTick.setVisibility(View.GONE);
                            vh2.doubleTickGreen.setVisibility(View.GONE);
                            vh2.doubleTickBlue.setVisibility(View.VISIBLE);

                            break;
                        case "2":
                            vh2.clock.setVisibility(View.GONE);
                            vh2.singleTick.setVisibility(View.GONE);

                            vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                            vh2.doubleTickBlue.setVisibility(View.GONE);
                            break;
                        case "1":

                            vh2.clock.setVisibility(View.GONE);
                            vh2.singleTick.setVisibility(View.VISIBLE);

                            vh2.doubleTickGreen.setVisibility(View.GONE);
                            vh2.doubleTickBlue.setVisibility(View.GONE);
                            break;
                        default:


                            vh2.clock.setVisibility(View.VISIBLE);
                            vh2.singleTick.setVisibility(View.GONE);

                            vh2.doubleTickGreen.setVisibility(View.GONE);
                            vh2.doubleTickBlue.setVisibility(View.GONE);
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * ContactSent
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderContactSent(VHContactSent vh2, final int position) {
        try {
            final MessageItemChat message = mListData.get(position);

            if (message != null) {

                if (message.isSelected()) {
                    vh2.selection_layout.setBackgroundColor(selectedItemColor);
                } else {
                    vh2.selection_layout.setBackgroundColor(unSelectedItemColor);
                }
                if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                    vh2.starredindicator_below.setVisibility(View.VISIBLE);
                } else {
                    vh2.starredindicator_below.setVisibility(View.GONE);
                }

                // vh2.senderName.setText(message.getSenderName());
                String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                mydate = mydate.replace(".", "");
                vh2.time.setText(mydate);
                final String contactName = message.getContactName();
                final String contactNumber = message.getContactNumber();
                final String contactAvatar = message.getAvatarImageUrl();
                final String contactChatappID = message.getContactChatappId();
                final String contactDetails = message.getDetailedContacts();

                if (position == 0) {
                    if (!message.isInfoMsg() && message.isSelf()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    }
                } else {

                    MessageItemChat prevmsg = mListData.get(position - 1);
                    if (message.isSelf() == !prevmsg.isSelf()) {
                        vh2.imageViewindicatior.setVisibility(View.VISIBLE);
                    } else {
                        vh2.imageViewindicatior.setVisibility(View.GONE);
                    }

                }


                if (contactNumber != null) {
                    vh2.contactName.setText(contactName);
                } else {
                    vh2.contactName.setText("");
                }

                getcontactname.configProfilepic(vh2.contactImage, contactChatappID, false, true, R.mipmap.contact_off);

                if (contactNumber != null) {
                    if (contactNumber.equals("")) {
                        vh2.add.setVisibility(View.GONE);
                        vh2.contact_add_invite.setVisibility(View.GONE);
                        vh2.v1.setVisibility(View.GONE);
                        vh2.invite.setVisibility(View.GONE);
                        vh2.message.setVisibility(View.GONE);

                    } else {
                        if (contactChatappID != null) {
                            if (contactChatappID.equals("")) {
                                vh2.add.setVisibility(View.GONE);
                                vh2.add1.setVisibility(View.GONE);
                                vh2.invite1.setVisibility(View.GONE);
                                vh2.invite.setVisibility(View.VISIBLE);
                                vh2.v1.setVisibility(View.VISIBLE);
                                vh2.message.setVisibility(View.GONE);

                            } else {
                                vh2.add.setVisibility(View.GONE);
                                vh2.add1.setVisibility(View.GONE);
                                vh2.invite1.setVisibility(View.GONE);
                                vh2.invite.setVisibility(View.GONE);
                                vh2.message.setVisibility(View.VISIBLE);
                                vh2.v1.setVisibility(View.VISIBLE);

                                if (contactChatappID.equalsIgnoreCase(mCurrentUserId)) {
                                    vh2.message.setVisibility(View.GONE);
                                    vh2.v1.setVisibility(View.GONE);
                                }
                            }
                        } else {
                            vh2.add.setVisibility(View.GONE);
                            vh2.add1.setVisibility(View.GONE);
                            vh2.invite1.setVisibility(View.GONE);
                            vh2.invite.setVisibility(View.VISIBLE);
                            vh2.v1.setVisibility(View.VISIBLE);
                            vh2.message.setVisibility(View.GONE);
                        }

                    }

                }


                String status = message.getDeliveryStatus();
                if (message.isBlockedMessage()) {
                    vh2.clock.setVisibility(View.GONE);
                    vh2.ivTick.setVisibility(View.VISIBLE);
                    vh2.ivTick.setImageResource(R.mipmap.ic_single_tick);
                } else {
                    switch (status) {
                        case "3":
                            vh2.clock.setVisibility(View.GONE);
                            vh2.ivTick.setVisibility(View.VISIBLE);
                            vh2.ivTick.setImageResource(R.drawable.message_deliver_tick);
                            break;
                        case "2":
                            vh2.clock.setVisibility(View.GONE);
                            vh2.ivTick.setVisibility(View.VISIBLE);
                            vh2.ivTick.setImageResource(R.mipmap.ic_double_tick);
                            break;
                        case "1":

                            vh2.clock.setVisibility(View.GONE);
                            vh2.ivTick.setVisibility(View.VISIBLE);
                            vh2.ivTick.setImageResource(R.mipmap.ic_single_tick);
                            break;
                        default:
                            vh2.clock.setVisibility(View.VISIBLE);
                            vh2.ivTick.setVisibility(View.GONE);
                            break;
                    }
                }

                vh2.invite.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        performUserInvite(contactName, contactDetails);
                        return false;
                    }
                });
                vh2.invite1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        performUserInvite(contactName, contactDetails);
                        return false;
                    }
                });
                vh2.add.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Intent intent = new Intent(mContext, Savecontact.class);
                        intent.putExtra("name", contactName);
                        intent.putExtra("number", contactNumber);
                        intent.putExtra("contactList", contactDetails);
                        mContext.startActivity(intent);
                        return false;
                    }
                });
                vh2.add1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Intent intent = new Intent(mContext, Savecontact.class);
                        intent.putExtra("name", contactName);
                        intent.putExtra("number", contactNumber);
                        intent.putExtra("contactList", contactDetails);
                        mContext.startActivity(intent);
                        return false;
                    }
                });
                vh2.contactName.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        Intent intent = new Intent(mContext, Savecontact.class);
                        intent.putExtra("name", contactName);
                        intent.putExtra("number", contactNumber);
                        intent.putExtra("contactList", contactDetails);
                        mContext.startActivity(intent);
                        return false;
                    }
                });
                vh2.message.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        checKForChatViewNavigation(contactNumber, contactName, contactAvatar, contactChatappID, "send");
                        return false;
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * UserInvite
     *
     * @param contactName    input value(contactName)
     * @param contactDetails input value(contactDetails)
     */
    private void performUserInvite(String contactName, String contactDetails) {

        ArrayList<String> aDetails = null;

        try {
            aDetails = new ArrayList<String>();
            JSONObject data = new JSONObject(contactDetails);
            JSONArray contactmail_List = null;
            JSONArray contactph_List = data.getJSONArray("phone_number");
            if (contactph_List.length() > 0) {
                for (int i = 0; i < contactph_List.length(); i++) {

                    JSONObject phone = contactph_List.getJSONObject(i);
                    String value = phone.getString("value");
                    aDetails.add(value);
                }

                if (data.has("email")) {
                    contactmail_List = data.getJSONArray("email");
                    if (contactmail_List.length() > 0) {
                        for (int i = 0; i < contactmail_List.length(); i++) {

                            JSONObject mail = contactmail_List.getJSONObject(i);
                            String value = mail.getString("value");
                            aDetails.add(value);
                        }
                    }
                }

            }


        } catch (JSONException e) {
            MyLog.e(TAG, "Error", e);
        }
        ShowInviteAlertForBoth(aDetails, contactName);

    }


    /**
     * @param detail arraylist value
     * @param name   input value(name)
     */
    private void ShowInviteAlertForBoth(final ArrayList<String> detail, String name) {

        final List<MultiTextDialogPojo> labelsList = new ArrayList<>();
        MultiTextDialogPojo pojo = new MultiTextDialogPojo();
        pojo.setLabelText(mContext.getResources().getString(R.string.invite_contacts) + " " + name + " " + "via");
        labelsList.add(pojo);
        for (int i = 0; i < detail.size(); i++) {
            pojo = new MultiTextDialogPojo();
            pojo.setLabelText(detail.get(i));
            labelsList.add(pojo);
        }

        CustomMultiTextItemsDialog dialog = new CustomMultiTextItemsDialog();
        dialog.setLabelsList(labelsList);
        dialog.setDialogItemClickListener(new CustomMultiTextItemsDialog.DialogItemClickListener() {
            @Override
            public void onDialogItemClick(int position) {
                String appname = mContext.getResources().getString(R.string.app_name);
                if (!Getcontactname.isEmailValid(labelsList.get(position).getLabelText())) {
                    SMSintent(labelsList.get(position).getLabelText(), appname);

                } else {
                    performInvite(labelsList.get(position).getLabelText(), appname);

                }

            }


        });
        dialog.show(fragmentManager, "Invite contact");
    }


    /**
     * SMSintent
     *
     * @param labelText input value (labelText)
     * @param appname   input value (appname)
     */
    private void SMSintent(String labelText, String appname) {
        Intent smsIntent;

        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
                /*String defaultApplication = Settings.Secure.getString(mContext.getContentResolver(), "sms_default_application");
                PackageManager pm = mContext.getPackageManager();
                smsIntent = pm.getLaunchIntentForPackage(defaultApplication);*/
                smsIntent = new Intent(Intent.ACTION_SEND);
                String packageName = Telephony.Sms.getDefaultSmsPackage(mContext);
                smsIntent.setPackage(packageName);
                smsIntent.setType("text/plain");
                smsIntent.putExtra("address", labelText);
//                smsIntent.setAction(Intent.ACTION_SEND);
                smsIntent.putExtra(Intent.EXTRA_TEXT, Constants.getAppStoreLink(mContext));
            } else {
                smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.setType("vnd.android-dir/mms-sms");
                smsIntent.putExtra("address", labelText);
                smsIntent.putExtra("sms_body", Constants.getAppStoreLink(mContext));
            }

            if (smsIntent == null) {
                Toast.makeText(mContext, "No app installed to handle this event", Toast.LENGTH_SHORT).show();
            } else {
                mContext.startActivity(smsIntent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "No app installed to handle this event", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * AudioSent
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderAudioSent(final VHAudioSent vh2, final int position) {
        final MessageItemChat message = mListData.get(position);
        if (message != null) {
            if (message.isSelected())
                vh2.selection_layout.setBackgroundColor(selectedItemColor);
            else
                vh2.selection_layout.setBackgroundColor(unSelectedItemColor);

            String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            mydate = mydate.replace(".", "");
            vh2.time.setText(mydate);

            if (message.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                vh2.audiotrack_layout.setVisibility(View.GONE);
                vh2.record_image.setVisibility(View.VISIBLE);
                vh2.record_icon.setVisibility(View.VISIBLE);
                vh2.recodingduration.setVisibility(View.VISIBLE);
                if (message.getDuration() != null) {
                    // String duration = message.getDuration();
                    //String surationasec = getTimeString(AppUtils.parseLong(duration));
                    vh2.recodingduration.setText(message.getDuration());
                }

                String userprofilepic = sessionmanager.getUserProfilePic();

                AppUtils.loadImage(mContext, Constants.SOCKET_IP + userprofilepic, vh2.record_image, 60, R.drawable.personprofile);

            } else {
                vh2.audiotrack_layout.setVisibility(View.VISIBLE);
                vh2.record_image.setVisibility(View.GONE);
                vh2.record_icon.setVisibility(View.GONE);
                vh2.recodingduration.setVisibility(View.GONE);
                if (message.getDuration() != null) {
                    String duration = message.getDuration();
//                    String surationasec = getTimeString(AppUtils.parseLong(duration));
                    vh2.duration.setText(duration);
                }
            }

        }
        if (position == 0) {
            assert message != null;
            if (!message.isInfoMsg() && message.isSelf()) {
                vh2.imageViewindicatior.setVisibility(View.VISIBLE);
            }
        } else {

            MessageItemChat prevmsg = mListData.get(position - 1);
            assert message != null;
            if (message.isSelf() == !prevmsg.isSelf()) {
                vh2.imageViewindicatior.setVisibility(View.VISIBLE);
            } else {
                vh2.imageViewindicatior.setVisibility(View.GONE);
            }

        }
        if (message.getChatFileLocalPath() == null) {
            message.setChatFileLocalPath("");
        }

        if (message.isMediaPlaying()) {
            long value = message.getPlayerCurrentPosition() * 1000;
            if (message.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                vh2.recodingduration.setText(getTimeString(value));
            } else {
                vh2.duration.setText(getTimeString(value));
            }
            vh2.playButton.setBackgroundResource(R.drawable.ic_pause);
        } else {
            vh2.playButton.setBackgroundResource(R.drawable.ic_play);
            if (message.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                vh2.recodingduration.setText(message.getDuration());
            } else {
                vh2.duration.setText(message.getDuration());
            }
        }

        vh2.sbDuration.setProgress(message.getPlayerCurrentPosition());

        vh2.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (lastPlayedAt > -1) {
                    mListData.get(lastPlayedAt).setIsMediaPlaying(false);
                    mTimer.cancel();
                    mPlayer.release();
                }

                if (lastPlayedAt != position) {
                    playAudio(position, message, vh2.sbDuration, vh2.duration);
                } else {
                    lastPlayedAt = -1;
                }

                notifyDataSetChanged();
            }
        });

        vh2.sbDuration.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
                if (mListData.get(position).isMediaPlaying()) {
                    mTimer.cancel();
                    mPlayer.release();
                    playAudio(position, message, vh2.sbDuration, vh2.duration);
                }
                try {
                    notifyDataSetChanged();
                } catch (Exception e) {
                    MyLog.e(TAG, "Error", e);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // TODO Auto-generated method stub
                try {
                    if (position != RecyclerView.NO_POSITION && fromUser) {
                        mListData.get(position).setPlayerCurrentPosition(progress);
                    }
                } catch (Exception e) {

                }
            }
        });

        if (message.getUploadStatus() == MessageFactory.UPLOAD_STATUS_UPLOADING &&
                message.getDeliveryStatus().equals(MessageFactory.DELIVERY_STATUS_NOT_SENT)) {
            try {
                vh2.playButton.setVisibility(View.INVISIBLE);
                vh2.sbDuration.setVisibility(View.INVISIBLE);
                vh2.pbUpload.setVisibility(View.VISIBLE);
                vh2.pbUpload.setProgress(message.getUploadDownloadProgress());
            } catch (Exception e) {
                MyLog.e(TAG, "Error", e);
            }
        } else {
            vh2.pbUpload.setVisibility(View.GONE);
            vh2.playButton.setVisibility(View.VISIBLE);
            vh2.sbDuration.setVisibility(View.VISIBLE);
        }

        String status = message.getDeliveryStatus();
        if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
            vh2.starredindicator.setVisibility(View.VISIBLE);
        } else {
            vh2.starredindicator.setVisibility(View.GONE);
        }
        if (message.isBlockedMessage()) {
            vh2.clock.setVisibility(View.GONE);
            vh2.ivTick.setVisibility(View.VISIBLE);
            vh2.ivTick.setImageResource(R.mipmap.ic_single_tick);
        } else {
            switch (status) {
                case "3":
                    vh2.clock.setVisibility(View.GONE);
                    vh2.ivTick.setVisibility(View.VISIBLE);
                    vh2.ivTick.setImageResource(R.drawable.message_deliver_tick);
                    break;
                case "2":
                    vh2.clock.setVisibility(View.GONE);
                    vh2.ivTick.setVisibility(View.VISIBLE);
                    vh2.ivTick.setImageResource(R.mipmap.ic_double_tick);
                    break;
                case "1":

                    vh2.clock.setVisibility(View.GONE);
                    vh2.ivTick.setVisibility(View.VISIBLE);
                    vh2.ivTick.setImageResource(R.mipmap.ic_single_tick);
                    break;
                default:
                    vh2.clock.setVisibility(View.VISIBLE);
                    vh2.ivTick.setVisibility(View.GONE);
                    break;
            }
        }

    }


    /**
     * stop Audio OnClearChat
     */
    public void stopAudioOnClearChat() {
        if (mTimer != null && mPlayer != null) {
            mTimer.cancel();
            mPlayer.release();
            for (int i = 0; i < mListData.size(); i++) {
                mListData.get(i).setIsMediaPlaying(false);
            }
        }
    }


    /**
     * playAudio
     *
     * @param position     specific item
     * @param message      getting value from model class
     * @param sbDuration   input value(sbDuration)
     * @param durationtext input value(durationtext)
     */
    private void playAudio(final int position, MessageItemChat message, final SeekBar sbDuration, final TextView durationtext) {
        File file = new File(message.getChatFileLocalPath());
        String audioPath = message.getChatFileLocalPath();
        if (!file.exists()) {
            try {
                String[] filePathSplited = message.getChatFileLocalPath().split(File.separator);
                String fileName = filePathSplited[filePathSplited.length - 1];

                String publicDirPath = Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_MUSIC).getAbsolutePath();
                audioPath = publicDirPath + File.separator + fileName;
            } catch (Exception e) {
                Log.e(TAG, "configureViewHolderImageReceived: ", e);
            }
        }

        Uri uri = Uri.parse(audioPath);
        mPlayer = MediaPlayer.create(mContext, uri);
        mTimer = new Timer();

        try {
            mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    mPlayer.start();
                    if (mListData.get(position).getPlayerCurrentPosition() < mPlayer.getDuration()) {
                        mPlayer.seekTo(mListData.get(position).getPlayerCurrentPosition() * 1000);

                    } else {
                        mPlayer.seekTo((mListData.get(position).getPlayerCurrentPosition() - 1) * 1000);
                    }
                    lastPlayedAt = position;

                    final int duration = mPlayer.getDuration();

                    final int amongToupdate = 1000;
                    mListData.get(position).setIsMediaPlaying(true);
                    mListData.get(position).setPlayerMaxDuration(mPlayer.getDuration());
                    final int max = mPlayer.getDuration() / 1000;
                    sbDuration.setMax(max);

                    mTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            try {
                                ((Activity) mContext).runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        if (!(amongToupdate * mListData.get(position).getPlayerCurrentPosition() >= duration)) {
                                            int progress = mListData.get(position).getPlayerCurrentPosition();
                                            progress += 1;

                                            if (max >= progress) {
                                                mListData.get(position).setPlayerCurrentPosition(progress);
                                            }
                                        }
                                        notifyDataSetChanged();
                                    }
                                });
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    }, 0, amongToupdate);

                    mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mPlayer.release();
                            mTimer.cancel();
                            mListData.get(position).setPlayerCurrentPosition(0);
                            mListData.get(position).setIsMediaPlaying(false);
                            lastPlayedAt = -1;
                            notifyDataSetChanged();
                        }
                    });
                }
            });
        } catch (Exception e) {
            MyLog.e(TAG, "Error", e);
        }
    }


    /**
     * Invite
     *
     * @param labelText input value(labelText)
     * @param appname   input value(appname)
     */
    public void performInvite(String labelText, String appname) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("message/rfc822");
        emailIntent.putExtra(Intent.EXTRA_TEXT, Constants.getAppStoreLink(mContext));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{labelText});
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, appname + " : Android");
        mContext.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }


    /**
     * DateLabel
     *
     * @param tvDateLbl input value (tvDateLbl)
     * @param position  specific value
     */
    private void configureDateLabel(TextView tvDateLbl, int position) {

        tvDateLbl.setVisibility(View.GONE);

        MessageItemChat item = mListData.get(position);
        String currentItemTS = item.getTS();
        Date currentItemDate = TimeStampUtils.getMessageTStoDate(mContext, currentItemTS);

        if (position == 0 && currentItemDate != null) {
            // Log.d(TAG, "configureDateLabel: date visible");
            tvDateLbl.setVisibility(View.VISIBLE);
            setDateText(tvDateLbl, currentItemDate);
        } else {
            MessageItemChat prevItem = mListData.get(position - 1);
            String prevItemTS = prevItem.getTS();
            Date prevItemDate = TimeStampUtils.getMessageTStoDate(mContext, prevItemTS);

            if (currentItemDate != null && prevItemDate != null) {
                if (!currentItemDate.equals(prevItemDate)) {
                    tvDateLbl.setVisibility(View.VISIBLE);
                    setDateText(tvDateLbl, currentItemDate);
                }
            } else {
                tvDateLbl.setVisibility(View.GONE);
            }
        }
    }


    /**
     * DateText
     *
     * @param tvDateLbl       textview
     * @param currentItemDate date value
     */
    private void setDateText(TextView tvDateLbl, Date currentItemDate) {
        Date today = TimeStampUtils.getDateFormat(Calendar.getInstance().getTimeInMillis());
        Date yesterday = TimeStampUtils.getYesterdayDate(today);

        if (currentItemDate.equals(today)) {
            tvDateLbl.setText("Today");
        } else if (currentItemDate.equals(yesterday)) {
            tvDateLbl.setText("Yesterday");
        } else {
            DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            String formatDate = df.format(currentItemDate);
            tvDateLbl.setText(formatDate);
        }
    }


    /**
     * updateInfo
     *
     * @param aitem updated arraylist value
     */
    public void updateInfo(ArrayList<MessageItemChat> aitem) {
        this.mListData = aitem;
        notifyDataSetChanged();
    }


    /**
     * getItemId
     *
     * @param position specific value
     * @return value
     */
    public long getItemId(int position) {
        return position;
    }


    /**
     * filter array value
     *
     * @return value
     */
    public Filter getFilter() {
        return mFilter;
    }


    /**
     * setfirstItemSelected
     *
     * @param isFirstItemSelected (boolean value)
     */
    public void setfirstItemSelected(boolean isFirstItemSelected) {

        FirstItemSelected = isFirstItemSelected;

    }


    /**
     * setCallback
     *
     * @param activity The activity object inherits the Context object
     */
    public void setCallback(ChatViewActivity activity) {
        this.activity = activity;
    }


    /**
     * getTimeString
     *
     * @param millis input value(millis)
     * @return value
     */
    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf.append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

    public String convertDuration(long duration) {
        String out = null;
        long hours = 0;
        try {
            hours = (duration / 3600000);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            MyLog.e(TAG, "Error", e);
            return out;
        }
        long remaining_minutes = (duration - (hours * 3600000)) / 60000;
        String minutes = String.valueOf(remaining_minutes);
        if (minutes.equals(0)) {
            minutes = "00";
        }
        long remaining_seconds = (duration - (hours * 3600000) - (remaining_minutes * 60000));
        String seconds = String.valueOf(remaining_seconds);
        if (seconds.length() < 2) {
            seconds = "00";
        } else {
            seconds = seconds.substring(0, 2);
        }

        if (hours > 0) {
            out = hours + ":" + minutes + ":" + seconds;
        } else {
            out = minutes + ":" + seconds;
        }

        return out;

    }


    /**
     * stop Audio OnMessage Delete
     *
     * @param position specific value
     */
    public void stopAudioOnMessageDelete(int position) {
        if (position > -1 && position == lastPlayedAt) {
            mTimer.cancel();
            mPlayer.release();
        }
    }


    /**
     * get Chat Lockdetail from Database
     *
     * @return
     */
    private ChatLockPojo getChatLockdetailfromDB() {
        String chatappcontactid = "";
        String id = mCurrentUserId.concat("-").concat(chatappcontactid);
        MessageDbController dbController = CoreController.getDBInstance(mContext);
        String convId = userInfoSession.getChatConvId(id);
        String receiverId = userInfoSession.getReceiverIdByConvId(convId);
        return dbController.getChatLockData(receiverId, MessageFactory.CHAT_TYPE_SINGLE);
    }


    /**
     * file size
     *
     * @param size input value (size)
     * @return value
     */
    public String size(long size) {
        String hrSize = "";
        double k = size / 1024.0;
        double m = size / 1048576.0;
        double g = size / 1073741824.0;
        double t = size / (1073741824.0 * 1024.0);
        DecimalFormat form = new DecimalFormat("0.00");
        if (t > 1) {
            t = round(t, 1);
            hrSize = (t + "").concat(" TB");
        } else if (g > 1) {
            g = round(g, 1);
            hrSize = (g + "").concat(" GB");
        } else if (m > 1) {
            m = round(m, 1);
            hrSize = (m + "").concat(" MB");
        } else if (k > 1) {
            k = round(k, 1);
            hrSize = (k + "").concat(" KB");
        } else {
            hrSize = (size + "").concat(" Bytes");
        }

        return hrSize;
    }

    private String getMessageFormat(String data) {
        /*Spanned result;
        data = data + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;;&#160;;&#160;";

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            result = Html.fromHtml(data, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(data);
        }*/

        String result = data + " \u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0\u00A0";
        return result;
    }


    /**
     * mediafile (image)
     */
    protected void mediafile() {
        try {
            for (int i = 0; i < mImageData.size(); i++) {
                String type = mImageData.get(i).getMessageType();
                int mtype = Integer.parseInt(type);
                if (MessageFactory.picture == mtype) {
                    MessageItemChat msgItem = mImageData.get(i);
                    if (msgItem.getImagePath() != null) {
                        String path = msgItem.getImagePath();
                        File file = new File(path);
                        if (file.exists()) {
                            imgzoompath.add(path);
                            imgs.add(path);
                            if (mImageData.get(i).getCaption() != null)
                                imgscaption.add(mImageData.get(i).getCaption());
                            else
                                imgscaption.add("null");
//                        gridlist.add(msgItem);
                        }
                    } else if (msgItem.getChatFileLocalPath() != null) {
                        String path = msgItem.getChatFileLocalPath();
                        File file = new File(path);
                        if (file.exists()) {
                            imgzoompath.add(path);
                            imgs.add(path);
                            if (mImageData.get(i).getCaption() != null)
                                imgscaption.add(mImageData.get(i).getCaption());
                            else
                                imgscaption.add("null");
//                        gridlist.add(msgItem);
                        }
                    }

                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private int getImagePathPos(String imagePath) {
        return imgs.indexOf(imagePath);
    }

    public void pasueAudioOnMessageDelete(int position) {
        if (position > -1 && position == lastPlayedAt) {
            mTimer.cancel();
            mPlayer.pause();
        }
    }


    /**
     * ItemFilter for search in chat account
     */
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<>(mListData); // saves the original data in mOriginalValues
            }

            final ArrayList<MessageItemChat> nlist = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                results.count = mOriginalValues.size();
                results.values = mOriginalValues;
            } else {
                for (int i = 0; i < mOriginalValues.size(); i++) {
                    MessageItemChat chat_message_item = mOriginalValues.get(i);
                    if (chat_message_item.getTextMessage() != null && chat_message_item.getTextMessage().toLowerCase().contains(filterString)) {
                        nlist.add(chat_message_item);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mListData = (ArrayList<MessageItemChat>) results.values;
            notifyDataSetChanged();
               /* ArrayList<MessageItemChat> filtered = (ArrayList<MessageItemChat>) results.values;
                notifyDataSetChanged();*/
        }

    }

}

