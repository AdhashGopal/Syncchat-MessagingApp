package com.chatapp.synchat.app.adapter;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.synchat.BuildConfig;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.ChatViewActivity;
import com.chatapp.synchat.app.ImageZoom;
import com.chatapp.synchat.app.Savecontact;
import com.chatapp.synchat.app.dialog.ChatLockPwdDialog;
import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.GroupInfoSession;
import com.chatapp.synchat.app.utils.TimeStampUtils;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.app.viewholder.VHDeleteSelfStartted;
import com.chatapp.synchat.app.viewholder.VHStarredAudioReceived;
import com.chatapp.synchat.app.viewholder.VHStarredAudioSent;
import com.chatapp.synchat.app.viewholder.VHStarredContactReceived;
import com.chatapp.synchat.app.viewholder.VHStarredContactSent;
import com.chatapp.synchat.app.viewholder.VHStarredDocumentReceived;
import com.chatapp.synchat.app.viewholder.VHStarredDocumentsent;
import com.chatapp.synchat.app.viewholder.VHStarredImageReceived;
import com.chatapp.synchat.app.viewholder.VHStarredImageSent;
import com.chatapp.synchat.app.viewholder.VHStarredLocationREceived;
import com.chatapp.synchat.app.viewholder.VHStarredLocationSent;
import com.chatapp.synchat.app.viewholder.VHStarredMessageReceived;
import com.chatapp.synchat.app.viewholder.VHStarredMessageSent;
import com.chatapp.synchat.app.viewholder.VHStarredVideoReceived;
import com.chatapp.synchat.app.viewholder.VHStarredVideoSent;
import com.chatapp.synchat.app.viewholder.VHStarredWebLinkReceived;
import com.chatapp.synchat.app.viewholder.VHStarredWebLinkSent;
import com.chatapp.synchat.app.viewholder.VHservermessage;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.chatapphelperclass.ChatappImageUtils;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.ChatLockPojo;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.service.Constants;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class StarredMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    public static boolean starredserach_result = false;
    static MediaPlayer player;
    private final int MESSAGERECEIVED = 0;
    private final int MESSAGESENT = 1;
    private final int IMAGERECEIVED = 2;
    private final int IMAGESENT = 3;
    private final int VIDEORECEIVED = 4;
    private final int VIDEOSENT = 5;
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
    private final int DELETE_SELF = 25;
    public List<ChatappContactModel> chatappContacts;
    public Getcontactname getname;
    Timer mTimer;
    String contactName, contactNumber;
    GroupInfoSession groupInfoSession;
    String mCurrentUserId;
    private ArrayList<MessageItemChat> mListData = new ArrayList<>();
    private ArrayList<MessageItemChat> mListData1;
    private Context mContext;
    private FragmentManager fragmentManager;
    private MediaPlayer mPlayer;
    private int density;
    private List<String> originalData = null;
    private ItemFilter mFilter = new ItemFilter();
    private Session session;
    private int lastPlayedAt = -1;
    private ChatMessageItemClickListener listener;
    private UserInfoSession userInfoSession;
    private String filteredText;

    /**
     * Create constructor
     *
     * @param mContext        The activity object inherits the Context object
     * @param mListData       list of value will be shown
     * @param fragmentManager FragmentManager
     */
    public StarredMessageAdapter(Context mContext, ArrayList<MessageItemChat> mListData, FragmentManager fragmentManager) {
        this.mListData = mListData;
        this.mContext = mContext;
        this.fragmentManager = fragmentManager;
        density = (int) mContext.getResources().getDisplayMetrics().density;
        groupInfoSession = new GroupInfoSession(mContext);
        session = new Session(mContext);
        userInfoSession = new UserInfoSession(mContext);
        mCurrentUserId = SessionManager.getInstance(mContext).getCurrentUserID();
        getname = new Getcontactname(mContext);

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);

        chatappContacts = contactDB_sqlite.getSavedChatappContacts();
    }

    /**
     * Click listener
     *
     * @param listener action value
     */
    public void setItemClickListener(ChatMessageItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return this.mListData.size();
    }

    /**
     * getItem
     *
     * @param position select specific item
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

                case ("" + MessageFactory.DELETE_SELF):
                    return DELETE_SELF;

                default:
                    return WEB_LINK_SENT;
            }

        } else {

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

                case ("" + MessageFactory.DELETE_SELF):
                    return DELETE_SELF;

                default:
                    return WEB_LINK_RECEIVED;
            }
        }
    }

    /**
     * layout binding
     *
     * @param viewGroup layout view group
     * @param viewType
     * @return view holder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {


        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        View v1;
        /*from  = SessionManager.getInstance(mContext).getCurrentUserID();
        String docId = from + "-" + to + "-g";
        if (db.isGroupId(docId))

        {
            isGroupChat = true;
        }*/
        switch (viewType) {
            case MESSAGERECEIVED:
                v1 = inflater.inflate(R.layout.message_starred_received, viewGroup, false);

                TextView rsize = (TextView) v1.findViewById(R.id.txtMsg);
                rsize.setTypeface(face);
                if (session.gettextsize().equalsIgnoreCase("Small"))
                    rsize.setTextSize(11);
                else if (session.gettextsize().equalsIgnoreCase("Medium"))
                    rsize.setTextSize(14);
                else if (session.gettextsize().equalsIgnoreCase("Large"))
                    rsize.setTextSize(17);

                viewHolder = new VHStarredMessageReceived(v1);
                break;

            case IMAGERECEIVED:
                v1 = inflater.inflate(R.layout.image_starred_received, viewGroup, false);
                viewHolder = new VHStarredImageReceived(v1);
                break;

            case VIDEORECEIVED:
                v1 = inflater.inflate(R.layout.video_starred_received, viewGroup, false);
                viewHolder = new VHStarredVideoReceived(v1);
                break;

            case LOCATIONRECEIVED:
                v1 = inflater.inflate(R.layout.location_starred_received, viewGroup, false);
                viewHolder = new VHStarredLocationREceived(v1);
                break;

            case DOCUMENT_RECEIVED:
                v1 = inflater.inflate(R.layout.vh_starred_document_received, viewGroup, false);

                TextView docRecsize = (TextView) v1.findViewById(R.id.txtMsg);
                docRecsize.setTypeface(face);
                if (session.gettextsize().equalsIgnoreCase("Small"))
                    docRecsize.setTextSize(11);
                else if (session.gettextsize().equalsIgnoreCase("Medium"))
                    docRecsize.setTextSize(14);
                else if (session.gettextsize().equalsIgnoreCase("Large"))
                    docRecsize.setTextSize(17);

                viewHolder = new VHStarredDocumentReceived(v1);
                break;

            case CONTACTRECEIVED:
                v1 = inflater.inflate(R.layout.contact_starred_received, viewGroup, false);
                viewHolder = new VHStarredContactReceived(v1);
                break;

            case AUDIORECEIVED:
                v1 = inflater.inflate(R.layout.audio_starred_received, viewGroup, false);
                viewHolder = new VHStarredAudioReceived(v1);
                break;

            case WEB_LINK_RECEIVED:
                v1 = inflater.inflate(R.layout.web_link_starred_receivee, viewGroup, false);

                TextView tvLinkMsg = (TextView) v1.findViewById(R.id.txtMsg);
                tvLinkMsg.setTypeface(face);
                if (session.gettextsize().equalsIgnoreCase("Small"))
                    tvLinkMsg.setTextSize(11);
                else if (session.gettextsize().equalsIgnoreCase("Medium"))
                    tvLinkMsg.setTextSize(14);
                else if (session.gettextsize().equalsIgnoreCase("Large"))
                    tvLinkMsg.setTextSize(17);

                viewHolder = new VHStarredWebLinkReceived(v1);
                break;


            case MESSAGESENT:
                v1 = inflater.inflate(R.layout.message_starred_sent, viewGroup, false);
                TextView ssize = (TextView) v1.findViewById(R.id.txtMsg);
                ssize.setTypeface(face);
                if (session.gettextsize().equalsIgnoreCase("Small"))
                    ssize.setTextSize(11);
                else if (session.gettextsize().equalsIgnoreCase("Medium"))
                    ssize.setTextSize(14);
                else if (session.gettextsize().equalsIgnoreCase("Large"))
                    ssize.setTextSize(17);
                viewHolder = new VHStarredMessageSent(v1);
                break;

            case IMAGESENT:
                v1 = inflater.inflate(R.layout.image_starred_sent, viewGroup, false);
                viewHolder = new VHStarredImageSent(v1);
                break;

            case VIDEOSENT:
                v1 = inflater.inflate(R.layout.video_starred_sent, viewGroup, false);
                viewHolder = new VHStarredVideoSent(v1);
                break;

            case LOCATIONSENT:
                v1 = inflater.inflate(R.layout.location_starred_sent, viewGroup, false);
                viewHolder = new VHStarredLocationSent(v1);
                break;


            case CONTACTSENT:
                v1 = inflater.inflate(R.layout.contact_starred_sent, viewGroup, false);
                viewHolder = new VHStarredContactSent(v1);
                break;

            case DOCUMENT_SENT:
                v1 = inflater.inflate(R.layout.vh_starred_document_sent, viewGroup, false);
                TextView docSize = (TextView) v1.findViewById(R.id.txtMsg);
                docSize.setTypeface(face);
                if (session.gettextsize().equalsIgnoreCase("Small"))
                    docSize.setTextSize(11);
                else if (session.gettextsize().equalsIgnoreCase("Medium"))
                    docSize.setTextSize(14);
                else if (session.gettextsize().equalsIgnoreCase("Large"))
                    docSize.setTextSize(17);
                viewHolder = new VHStarredDocumentsent(v1);
                break;

            case ServerMessAGE:
                v1 = inflater.inflate(R.layout.servermessage, viewGroup, false);
                viewHolder = new VHStarredVideoReceived(v1);
                break;

            case WEB_LINK_SENT:
                v1 = inflater.inflate(R.layout.web_link_starred_sent, viewGroup, false);

                TextView tvLinkMsgSent = (TextView) v1.findViewById(R.id.txtMsg);
                tvLinkMsgSent.setTypeface(face);
                if (session.gettextsize().equalsIgnoreCase("Small"))
                    tvLinkMsgSent.setTextSize(11);
                else if (session.gettextsize().equalsIgnoreCase("Medium"))
                    tvLinkMsgSent.setTextSize(14);
                else if (session.gettextsize().equalsIgnoreCase("Large"))
                    tvLinkMsgSent.setTextSize(17);

                viewHolder = new VHStarredWebLinkSent(v1);
                break;
            case DELETE_SELF:
                v1 = inflater.inflate(R.layout.vh_self_delete_started, viewGroup, false);
                viewHolder = new VHDeleteSelfStartted(v1);
                break;

            default:
                /*v1 = inflater.inflate(R.layout.audio_starred_sent, viewGroup, false);
                viewHolder = new VHStarredAudioSent(v1);*/
                break;


        }
        return viewHolder;
    }

    /**
     * binding view data
     *
     * @param viewHolder widget view
     * @param position   view holder position
     */
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, int position) {

        final int itemPosition = position;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onItemClick(viewHolder.itemView, itemPosition);
                }
            }
        });

        switch (viewHolder.getItemViewType()) {

            case MESSAGERECEIVED:
                VHStarredMessageReceived vh2 = (VHStarredMessageReceived) viewHolder;
                configureViewHolderStarredMessageReceived(vh2, position);

                break;

            case IMAGERECEIVED:
                VHStarredImageReceived vh3 = (VHStarredImageReceived) viewHolder;
                configureViewHolderStarredImageReceived(vh3, position);
                break;

            case VIDEORECEIVED:
                VHStarredVideoReceived vh4 = (VHStarredVideoReceived) viewHolder;
                configureViewHolderStarredVideoReceived(vh4, position);
                break;

            case LOCATIONRECEIVED:

                VHStarredLocationREceived vh5 = (VHStarredLocationREceived) viewHolder;
                configureViewHolderStarredLocationReceived(vh5, position);
                break;

            case CONTACTRECEIVED:

                VHStarredContactReceived vh6 = (VHStarredContactReceived) viewHolder;
                configureViewHolderStarredContactReceived(vh6, position);
                break;

            case AUDIORECEIVED:
                VHStarredAudioReceived vh7 = (VHStarredAudioReceived) viewHolder;
                configureViewHolderStarredAudioReceived(vh7, position);
                break;

            case WEB_LINK_RECEIVED:
                VHStarredWebLinkReceived vhWebReceived = (VHStarredWebLinkReceived) viewHolder;

                configureViewHolderStarredWebLinkReceived(vhWebReceived, position);
                break;

            case DOCUMENT_RECEIVED:
                VHStarredDocumentReceived vhDocReceived = (VHStarredDocumentReceived) viewHolder;

                configureViewHolderStarredDocumentReceived(vhDocReceived, position);
                break;


            case MESSAGESENT:
                VHStarredMessageSent vh8 = (VHStarredMessageSent) viewHolder;

                configureViewHolderStarredMessageSent(vh8, position);

                break;

            case IMAGESENT:

                VHStarredImageSent vh9 = (VHStarredImageSent) viewHolder;
                configureViewHolderStarredImageSent(vh9, position);
                break;

            case VIDEOSENT:

                VHStarredVideoSent vh10 = (VHStarredVideoSent) viewHolder;
                configureViewHolderStarredVideoSent(vh10, position);
                break;

            case LOCATIONSENT:
                VHStarredLocationSent vh11 = (VHStarredLocationSent) viewHolder;
                configureViewHolderStarredLocationSent(vh11, position);
                break;


            case CONTACTSENT:
                VHStarredContactSent vh12 = (VHStarredContactSent) viewHolder;
                configureViewHolderStarredContactSent(vh12, position);
                break;

            case ServerMessAGE:

                VHservermessage vh14 = (VHservermessage) viewHolder;
                configureViewHolderServerMessage(vh14, position);
                break;

            case WEB_LINK_SENT:
                VHStarredWebLinkSent vhWebSent = (VHStarredWebLinkSent) viewHolder;
                configureViewHolderStarredWebLinkSent(vhWebSent, position);
                break;

            case DELETE_SELF:
                VHDeleteSelfStartted vhDeleteSelf = (VHDeleteSelfStartted) viewHolder;
                configureViewHolderDeleteSelf(vhDeleteSelf, position);
                break;

            case DOCUMENT_SENT:
                VHStarredDocumentsent vhStarredDocumentsent = (VHStarredDocumentsent) viewHolder;
                configureViewHolderStarredDocumentSent(vhStarredDocumentsent, position);
                break;

            default:
                /*VHStarredAudioSent vh13 = (VHStarredAudioSent) viewHolder;
                configureViewHolderStarredAudioSent(vh13, position);*/
                break;


        }
    }

    /**
     * DeleteSelf
     *
     * @param vhDeleteSelf specific view holder
     * @param position     specific position
     */
    private void configureViewHolderDeleteSelf(VHDeleteSelfStartted vhDeleteSelf, int position) {
    }

    /**
     * DocumentSent
     *
     * @param vhDocumentSent specific view holder
     * @param position       specific position
     */
    private void configureViewHolderStarredDocumentSent(VHStarredDocumentsent vhDocumentSent, int position) {

        try {
            final MessageItemChat message = mListData.get(position);
            configureDateLabel(vhDocumentSent.datelbl, position);

            if (message.isSelected())
                vhDocumentSent.mainSent.setBackgroundColor(Color.parseColor("#EBF4FA"));
                // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
            else
                vhDocumentSent.mainSent.setBackgroundColor(Color.parseColor("#00000000"));

            if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                vhDocumentSent.starred.setVisibility(View.VISIBLE);
            } else {
                vhDocumentSent.starred.setVisibility(View.GONE);
            }


            if (message != null) {
                // vh2.senderName.setText(message.getSenderName());
                if (message.getTS() == null) {
                    vhDocumentSent.time.setText("");
                } else {
                    String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                    mydate = mydate.replace(".", "");
                    vhDocumentSent.time.setText(mydate);
                }
                vhDocumentSent.fromname.setText("You");
                if (!message.getMessageId().contains("-g-")) {
                    vhDocumentSent.toname.setText(getname.getSendername(message.getReceiverID(), message.getSenderMsisdn()));
                } else {
                    vhDocumentSent.toname.setText(message.getGroupName());
                }
                SessionManager sessionmanager = SessionManager.getInstance(mContext);
                String userprofilepic = sessionmanager.getUserProfilePic();
                //TODO tharani map
                Glide.with(mContext).load(Constants.SOCKET_IP + userprofilepic).error(
                        R.mipmap.chat_attachment_profile_default_image_frame)
                        //.transform(new CircleTransform())
                        .into(vhDocumentSent.userprofile);
                try {
                    vhDocumentSent.message.setText(message.getTextMessage());
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

                vhDocumentSent.message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    openDocument(message.getChatFileLocalPath());
                        CallIntentFileProvider(message.getChatFileLocalPath());
                    }
                });

                vhDocumentSent.ivDoc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                    openDocument(message.getChatFileLocalPath());
                        CallIntentFileProvider(message.getChatFileLocalPath());
                    }
                });

                String status = message.getDeliveryStatus();
          /*  if (status == null) {
                status = MessageFactory.DELIVERY_STATUS_SENT;
            }*/
                if (status.equals("3")) {

                    vhDocumentSent.clock.setVisibility(View.GONE);
                    vhDocumentSent.singleTick.setVisibility(View.GONE);

                    vhDocumentSent.doubleTickGreen.setVisibility(View.GONE);
                    vhDocumentSent.doubleTickBlue.setVisibility(View.VISIBLE);

                } else if (status.equals("2")) {
                    vhDocumentSent.clock.setVisibility(View.GONE);
                    vhDocumentSent.singleTick.setVisibility(View.GONE);

                    vhDocumentSent.doubleTickGreen.setVisibility(View.VISIBLE);
                    vhDocumentSent.doubleTickBlue.setVisibility(View.GONE);
                } else if (status.equals("1")) {

                    vhDocumentSent.clock.setVisibility(View.GONE);
                    vhDocumentSent.singleTick.setVisibility(View.VISIBLE);

                    vhDocumentSent.doubleTickGreen.setVisibility(View.GONE);
                    vhDocumentSent.doubleTickBlue.setVisibility(View.GONE);
                } else {
                    vhDocumentSent.clock.setVisibility(View.VISIBLE);
                    vhDocumentSent.singleTick.setVisibility(View.GONE);

                    vhDocumentSent.doubleTickGreen.setVisibility(View.GONE);
                    vhDocumentSent.doubleTickBlue.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            vhDocumentSent.mainSent.setVisibility(View.GONE);
            e.printStackTrace();
        }


    }

    /**
     * WebLinkSent
     *
     * @param vhWebSent specific view holder
     * @param position  specific position
     */
    private void configureViewHolderStarredWebLinkSent(VHStarredWebLinkSent vhWebSent, int position) {
        try {
            final MessageItemChat message = mListData.get(position);

            if (message != null) {
                if (message.getWebLink().equalsIgnoreCase("")) {
                    vhWebSent.mainSent.setVisibility(View.GONE);
                } else {
                    configureDateLabel(vhWebSent.datelbl, position);
                    if (message.isSelected())
                        vhWebSent.mainSent.setBackgroundColor(Color.parseColor("#EBF4FA"));
                    else
                        vhWebSent.mainSent.setBackgroundColor(Color.parseColor("#00000000"));

                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vhWebSent.starred.setVisibility(View.VISIBLE);
                    } else {
                        vhWebSent.starred.setVisibility(View.GONE);
                    }
                    if (message.getTS() == null) {
                        vhWebSent.time.setText("");
                    } else {
                        String new_time = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                        new_time = new_time.replace(".", "");
                        vhWebSent.time.setText(new_time);
                    }
                    vhWebSent.fromname.setText("You");
                    if (!message.getMessageId().contains("-g-")) {
                        vhWebSent.toname.setText(getname.getSendername(message.getReceiverID(), message.getSenderMsisdn()));
                    } else {
                        vhWebSent.toname.setText(message.getGroupName());
                    }
                    SessionManager sessionmanager = SessionManager.getInstance(mContext);
                    String userprofilepic = sessionmanager.getUserProfilePic();
                    //TODO tharani map
                    Glide.with(mContext).load(Constants.SOCKET_IP + userprofilepic).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            .into(vhWebSent.userprofile);
                    try {
                        vhWebSent.message.setText(message.getTextMessage());
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }


                    vhWebSent.tvWebLink.setText(message.getWebLink());
                    vhWebSent.tvWebTitle.setText(message.getWebLinkTitle());


                    String linkImgUrl = message.getWebLink() + "/favicon.ico";
                    Glide.with(mContext).load(linkImgUrl).into(vhWebSent.ivWebLink);


                    String status = message.getDeliveryStatus();

                    if (status.equals("3")) {

                        vhWebSent.clock.setVisibility(View.GONE);
                        vhWebSent.singleTick.setVisibility(View.GONE);

                        vhWebSent.doubleTickGreen.setVisibility(View.GONE);
                        vhWebSent.doubleTickBlue.setVisibility(View.VISIBLE);

                    } else if (status.equals("2")) {
                        vhWebSent.clock.setVisibility(View.GONE);
                        vhWebSent.singleTick.setVisibility(View.GONE);

                        vhWebSent.doubleTickGreen.setVisibility(View.VISIBLE);
                        vhWebSent.doubleTickBlue.setVisibility(View.GONE);
                    } else if (status.equals("1")) {

                        vhWebSent.clock.setVisibility(View.GONE);
                        vhWebSent.singleTick.setVisibility(View.VISIBLE);

                        vhWebSent.doubleTickGreen.setVisibility(View.GONE);
                        vhWebSent.doubleTickBlue.setVisibility(View.GONE);
                    } else {

                        vhWebSent.clock.setVisibility(View.VISIBLE);
                        vhWebSent.singleTick.setVisibility(View.GONE);

                        vhWebSent.doubleTickGreen.setVisibility(View.GONE);
                        vhWebSent.doubleTickBlue.setVisibility(View.GONE);
                    }

                }
            }
        } catch (Exception e) {
            vhWebSent.mainSent.setVisibility(View.GONE);
            e.printStackTrace();
        }
    }

    /**
     * IntentFileProvider
     *
     * @param chatFileLocalPath string value
     */
    private void CallIntentFileProvider(String chatFileLocalPath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(chatFileLocalPath);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        File file = new File(chatFileLocalPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID, file);
        intent.setDataAndType(uri, mimeType);
        mContext.startActivity(intent);
    }

    private void openDocument(String chatFileLocalPath) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(chatFileLocalPath);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        PackageManager packageManager = mContext.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType(mimeType);
        try {

            List list = packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY);
            if (list.size() > 0) {
                File file = new File(chatFileLocalPath);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file), mimeType);
                mContext.startActivity(intent);
            } else {
                Toast.makeText(mContext, "No app installed to view this document", Toast.LENGTH_LONG).show();
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(mContext, "No app installed to view this document", Toast.LENGTH_LONG).show();
        }
    }


    /**
     * DocumentReceived
     *
     * @param vhDocumentReceived specific view holder
     * @param position           specific position
     */
    private void configureViewHolderStarredDocumentReceived(VHStarredDocumentReceived vhDocumentReceived, int position) {
        final MessageItemChat message = mListData.get(position);

        if (message.isSelected())
            vhDocumentReceived.mainReceived.setBackgroundColor(Color.parseColor("#EBF4FA"));
            // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
        else
            vhDocumentReceived.mainReceived.setBackgroundColor(Color.parseColor("#00000000"));

        if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED))
            vhDocumentReceived.starred.setVisibility(View.VISIBLE);
        else
            vhDocumentReceived.starred.setVisibility(View.GONE);

        if (message != null) {

            if (!message.getMessageId().contains("-g-")) {
                vhDocumentReceived.senderName.setText(message.getSenderName());
            } else {
                vhDocumentReceived.senderName.setVisibility(View.GONE);
            }
            configureDateLabel(vhDocumentReceived.datelbl, position);
            String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            mydate = mydate.replace(".", "");
            vhDocumentReceived.time.setText(mydate);
            try {
                vhDocumentReceived.message.setText(message.getTextMessage());

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            if (!message.getMessageId().contains("-g-")) {
                final String[] array = message.getMessageId().split("-");
                vhDocumentReceived.fromname.setText(getname.getSendername(array[1], message.getSenderMsisdn()));
                vhDocumentReceived.toname.setText("You");


                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
                ChatappContactModel info = contactDB_sqlite.getUserOpponenetDetails(array[1]);

                if (info != null && info.getAvatarImageUrl() != null) {
                    String userprofile = info.getAvatarImageUrl();
//TODO tharani map
                    Glide.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            //.transform(new CircleTransform())
                            .into(vhDocumentReceived.userprofile);
                } else {
                    vhDocumentReceived.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                }
            } else {
                vhDocumentReceived.fromname.setText(getname.getSendername(message.getGroupMsgFrom(), message.getSenderMsisdn()));
                vhDocumentReceived.toname.setText(message.getGroupName());
                String docid = message.getMessageId();
                final String[] array = docid.split("-");
                Log.e("ARRAY", array + "");
                ContactDB_Sqlite contactDB = CoreController.getContactSqliteDBintstance(mContext);
                String getAvatarImageUrl = contactDB.getUserImage(message.getGroupMsgFrom());

                if (getAvatarImageUrl != null) {
//TODO tharani map
                    Glide.with(mContext).load(Constants.SOCKET_IP + getAvatarImageUrl).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            //.transform(new CircleTransform())
                            .into(vhDocumentReceived.userprofile);
                } else {
                    vhDocumentReceived.userprofile.setBackgroundResource(R.mipmap.group_chat_attachment_profile_icon);
                }

               /* if ((groupInfoSession.getGroupInfo(array[1]) != null) && (groupInfoSession.getGroupInfo(array[1]) != null)) {
                    String userprofile = groupInfoSession.getGroupInfo(array[1]).getAvatarPath();
                    Picasso.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            .into(vhDocumentReceived.userprofile);
                } else {
                    vhDocumentReceived.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                }*/
            }
            vhDocumentReceived.relative_layout_message.setVisibility(View.VISIBLE);
            vhDocumentReceived.tvInfoMsg.setVisibility(View.GONE);
            if (message.isInfoMsg()) {
                vhDocumentReceived.relative_layout_message.setVisibility(View.GONE);
                vhDocumentReceived.tvInfoMsg.setVisibility(View.VISIBLE);

                vhDocumentReceived.tvInfoMsg.setText(message.getTextMessage());
            }

            if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_COMPLETED) {
                vhDocumentReceived.message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        openDocument(message.getChatFileLocalPath());
                        CallIntentFileProvider(message.getChatFileLocalPath());
                    }
                });

                vhDocumentReceived.ivDoc.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        openDocument(message.getChatFileLocalPath());
                        CallIntentFileProvider(message.getChatFileLocalPath());
                    }
                });
            }
        }
    }

    /**
     * WebLinkReceived
     *
     * @param vhWebReceived specific view holder
     * @param position      specific position
     */
    private void configureViewHolderStarredWebLinkReceived(VHStarredWebLinkReceived vhWebReceived, int position) {
        final MessageItemChat message = mListData.get(position);
        configureDateLabel(vhWebReceived.datelbl, position);
        if (message.isSelected())
            vhWebReceived.mainReceived.setBackgroundColor(Color.parseColor("#EBF4FA"));
            // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
        else
            vhWebReceived.mainReceived.setBackgroundColor(Color.parseColor("#00000000"));

        if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED))
            vhWebReceived.starred.setVisibility(View.VISIBLE);
        else
            vhWebReceived.starred.setVisibility(View.GONE);

        if (message != null) {

            if (!message.getMessageId().contains("-g-")) {
                final String[] array = message.getMessageId().split("-");
                vhWebReceived.fromname.setText(getname.getSendername(array[1], message.getSenderMsisdn()));
                vhWebReceived.toname.setText("You");

                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
                ChatappContactModel info = contactDB_sqlite.getUserOpponenetDetails(array[1]);

                if (info != null && info.getAvatarImageUrl() != null) {
                    String userprofile = info.getAvatarImageUrl();
//TODO tharani map
                    Glide.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            //.transform(new CircleTransform())
                            .into(vhWebReceived.userprofile);
                } else {
                    vhWebReceived.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                }
            } else {
                vhWebReceived.fromname.setText(getname.getSendername(message.getGroupMsgFrom(), message.getSenderMsisdn()));
                vhWebReceived.toname.setText(message.getGroupName());
                String docid = message.getMessageId();
                final String[] array = docid.split("-");
                String s = groupInfoSession.getGroupInfo(array[1]).getAvatarPath();
                Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
                ContactDB_Sqlite contactDB = CoreController.getContactSqliteDBintstance(mContext);
                chatappContacts = contactDB.getAllChatappContacts();
                Log.e("SIZZZE", chatappContacts + "");

                String getAvatarImageUrl = contactDB.getUserImage(message.getGroupMsgFrom());

                if (getAvatarImageUrl != null) {
//TODO tharani map
                    Glide.with(mContext).load(Constants.SOCKET_IP + getAvatarImageUrl).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            //.transform(new CircleTransform())
                            .into(vhWebReceived.userprofile);
                } else {
                    vhWebReceived.userprofile.setBackgroundResource(R.mipmap.group_chat_attachment_profile_icon);
                }

                /*if ((groupInfoSession.getGroupInfo(array[1]) != null) && (groupInfoSession.getGroupInfo(array[1]) != null)) {
                    String userprofile = groupInfoSession.getGroupInfo(array[1]).getAvatarPath();
                    Picasso.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            .transform(new CircleTransform()).into(vhWebReceived.userprofile);
                } else {
                    vhWebReceived.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                }*/
            }

            String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            mydate = mydate.replace(".", "");
            vhWebReceived.time.setText(mydate);
            vhWebReceived.fromname.setText(message.getSenderName());
            vhWebReceived.toname.setText("you");
            try {
                vhWebReceived.message.setText(message.getTextMessage());

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

            vhWebReceived.relative_layout_message.setVisibility(View.VISIBLE);
            vhWebReceived.tvInfoMsg.setVisibility(View.GONE);
            if (message.isInfoMsg()) {
                vhWebReceived.relative_layout_message.setVisibility(View.GONE);
                vhWebReceived.tvInfoMsg.setVisibility(View.VISIBLE);

                vhWebReceived.tvInfoMsg.setText(message.getTextMessage());
            }

            vhWebReceived.rlWebLink.setVisibility(View.VISIBLE);
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
            String linkImgUrl = message.getWebLink() + "/favicon.ico";
            Glide.with(mContext).load(linkImgUrl).into(vhWebReceived.ivWebLink);
        }
    }

    /**
     * ServerMessage
     *
     * @param vh14     specific view holder
     * @param position specific position
     */
    private void configureViewHolderServerMessage(VHservermessage vh14, int position) {

        final MessageItemChat message = mListData.get(position);


        if (message != null) {

            vh14.tvServerMsgLbl.setText(message.getTextMessage());

        }
    }


    /**
     * MessageReceived
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderStarredMessageReceived(VHStarredMessageReceived vh2, final int position) {
        final MessageItemChat message = mListData.get(position);

        vh2.cameraphoto.setVisibility(View.GONE);
        vh2.time.setVisibility(View.GONE);
        vh2.ts_below.setVisibility(View.GONE);
        vh2.starred.setVisibility(View.GONE);
        vh2.starredindicator_below.setVisibility(View.GONE);
        vh2.time.setVisibility(View.GONE);
        configureDateLabel(vh2.datelbl, position);
        if (message.isSelected())
            vh2.mainReceived.setBackgroundColor(Color.parseColor("#EBF4FA"));
            // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
        else
            vh2.mainReceived.setBackgroundColor(Color.parseColor("#00000000"));

        String textmessage = message.getTextMessage();
        if (!textmessage.contains("\n") && textmessage.length() <= 20) {
            vh2.time.setVisibility(View.VISIBLE);
            if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED))
                vh2.starredindicator_below.setVisibility(View.VISIBLE);
            else
                vh2.starredindicator_below.setVisibility(View.GONE);

            if (message != null) {
                String sendername = "";
                if (!message.getMessageId().contains("-g-")) {
                    final String[] array = message.getMessageId().split("-");
                    vh2.fromname.setText(getname.getSendername(array[1], message.getSenderMsisdn()));
                    vh2.toname.setText("you");

                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
                    ChatappContactModel info = contactDB_sqlite.getUserOpponenetDetails(array[1]);


                    if (info != null && info.getAvatarImageUrl() != null) {
                        String userprofile = info.getAvatarImageUrl();
//TODO tharani map
                        Glide.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                                R.mipmap.chat_attachment_profile_default_image_frame)
                                //.transform(new CircleTransform())
                                .into(vh2.userprofile);
                    } else {
                        vh2.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                    }
                } else {
                    vh2.fromname.setText(getname.getSendername(message.getGroupMsgFrom(), message.getSenderMsisdn()));
                    vh2.toname.setText(message.getGroupName());
                    String docid = message.getMessageId();
                    final String[] array = docid.split("-");
                    String toUserId = array[1];
                    ContactDB_Sqlite contactDB = CoreController.getContactSqliteDBintstance(mContext);

                    String getAvatarImageUrl = contactDB.getUserImage(message.getGroupMsgFrom());

                    if (getAvatarImageUrl != null) {
//TODO tharani map
                        Glide.with(mContext).load(Constants.SOCKET_IP + getAvatarImageUrl).error(
                                R.mipmap.chat_attachment_profile_default_image_frame)
                                //.transform(new CircleTransform())
                                .into(vh2.userprofile);
                    } else {
                        vh2.userprofile.setBackgroundResource(R.mipmap.group_chat_attachment_profile_icon);
                    }

                    /*if ((groupInfoSession.getGroupInfo(toUserId) != null) && (groupInfoSession.getGroupInfo(toUserId) != null)) {
                        String userprofile = groupInfoSession.getGroupInfo(toUserId).getAvatarPath();
                        Picasso.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                                R.mipmap.chat_attachment_profile_default_image_frame)
                                .transform(new CircleTransform()).into(vh2.userprofile);
                    } else {
                        vh2.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                    }*/
                }

                if (message.getMessageId().contains("-g-")) {
                    vh2.senderName.setText(message.getSenderName());
                } else {
                    vh2.senderName.setVisibility(View.GONE);
                }

                String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                mydate = mydate.replace(".", "");
                vh2.time.setText(mydate);

                try {
                    vh2.message.setText(message.getTextMessage());

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

                vh2.relative_layout_message.setVisibility(View.VISIBLE);

            }
        } else {
            vh2.ts_below.setVisibility(View.VISIBLE);
            if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED))
                vh2.starred.setVisibility(View.VISIBLE);
            else
                vh2.starred.setVisibility(View.GONE);

            if (message != null) {

                if (message.getMessageId().contains("-g-")) {
                    vh2.senderName.setText(message.getSenderName());
                } else {
                    vh2.senderName.setVisibility(View.GONE);
                }

                String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                // vh2.tsNextLine.setText(mydate);
                vh2.ts_below.setText(mydate);
                try {
                    vh2.message.setText(message.getTextMessage());

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

                vh2.relative_layout_message.setVisibility(View.VISIBLE);

            }


        }
    }

    /**
     * ImageReceived
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderStarredImageReceived(final VHStarredImageReceived vh2, final int position) {

        final MessageItemChat message = mListData.get(position);
        if (message != null) {

            //  vh2.senderName.setText(message.getSenderName());

            configureDateLabel(vh2.datelbl, position);

            String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            mydate = mydate.replace(".", "");

            if (!message.getMessageId().contains("-g-")) {
                final String[] array = message.getMessageId().split("-");
                vh2.fromname.setText(getname.getSendername(array[1], message.getSenderMsisdn()));
                vh2.toname.setText("you");

                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
                ChatappContactModel info = contactDB_sqlite.getUserOpponenetDetails(array[1]);


                if (info != null && info.getAvatarImageUrl() != null) {
                    String userprofile = info.getAvatarImageUrl();
//TODO tharani map
                    Glide.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            .into(vh2.userprofile);
                } else {
                    vh2.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                }
            } else {
                vh2.fromname.setText(getname.getSendername(message.getGroupMsgFrom(), message.getSenderMsisdn()));
                vh2.toname.setText(message.getGroupName());
                String docid = message.getMessageId();
                final String[] array = docid.split("-");

                ContactDB_Sqlite contactDB = CoreController.getContactSqliteDBintstance(mContext);
                chatappContacts = contactDB.getAllChatappContacts();
//                String s = groupInfoSession.getGroupInfo(array[1]).getAvatarPath();
                //Toast.makeText(mContext, s, Toast.LENGTH_LONG).show();
                String getAvatarImageUrl = contactDB.getUserImage(message.getGroupMsgFrom());

                if (getAvatarImageUrl != null) {
//TODO tharani map
                    Glide.with(mContext).load(Constants.SOCKET_IP + getAvatarImageUrl).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            //.transform(new CircleTransform())
                            .into(vh2.userprofile);
                } else {
                    vh2.userprofile.setBackgroundResource(R.mipmap.group_chat_attachment_profile_icon);
                }
               /* if ((groupInfoSession.getGroupInfo(array[1]) != null) && (groupInfoSession.getGroupInfo(array[1]) != null)) {
                    String userprofile = groupInfoSession.getGroupInfo(array[1]).getAvatarPath();
                    Picasso.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            .transform(new CircleTransform()).into(vh2.userprofile);
                } else {
                    vh2.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                }*/
            }
            if (message.getTextMessage() != null && !message.getTextMessage().equalsIgnoreCase("")) {
                vh2.caption.setVisibility(View.VISIBLE);
                vh2.rlTs.setVisibility(View.VISIBLE);
                vh2.abovecaption_layout.setVisibility(View.GONE);
                vh2.captiontext.setText(message.getTextMessage());
                if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                    vh2.starredindicator_below.setVisibility(View.VISIBLE);
                } else {
                    vh2.starredindicator_below.setVisibility(View.GONE);
                }
                vh2.time.setText(mydate);
            } else {
                vh2.rlTs.setVisibility(View.GONE);
                vh2.abovecaption_layout.setVisibility(View.VISIBLE);
                vh2.caption.setVisibility(View.GONE);
                if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                    vh2.starredindicator_above.setVisibility(View.VISIBLE);
                } else {
                    vh2.starredindicator_above.setVisibility(View.GONE);
                }
                vh2.ts_abovecaption.setText(mydate);
            }
            try {

                if (message.isSelf()) {

                } else {

                    if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_COMPLETED) {
                        final String image = message.getChatFileLocalPath();

                        File file = new File(image);

                        if (file.exists()) {

                            // vh2.imageView.setImageResource(R.drawable.chatapp_newlogo);

                            // vh2.forward_image.setVisibility(View.VISIBLE);
                            AppUtils.loadLocalImage(mContext, image, vh2.imageView);

                            vh2.imageView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(mContext, ImageZoom.class);
                                    intent.putExtra("image", image);
                                    mContext.startActivity(intent);

                                }
                            });
                        } else {

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

                    } else {
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
                    }


//                    try {
//                        vh2.imageView.setImageBitmap(ChatappImageUtils.decodeBitmapFromFile(message.getChatFileLocalPath(), 220, 200));
//
//                        vh2.imageView.setOnClickListener(new View.OnClickListener() {
//                            public void onClick(View v) {
//                                Intent intent = new Intent(mContext, ImageZoom.class);
//                                intent.putExtra("image", message.getChatFileLocalPath());
//                                mContext.startActivity(intent);
//                            }
//                        });
//                    } catch (Exception e) {
//                        vh2.imageView.setImageBitmap(null);
//                        e.printStackTrace();
//                    }

                }

                // bm=null;

              /*  vh2.imageView.setOnTouchListener(new View.OnTouchListener() {

                    @Override
                    public boolean onTouch(View arg0, MotionEvent arg1) {
                        // this will make sure event is not propagated to others, nesting same view area
                        return false;
                    }

                });*/

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * VideoReceived
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderStarredVideoReceived(final VHStarredVideoReceived vh2, final int position) {
        final MessageItemChat message = mListData.get(position);
        if (message != null) {
            configureDateLabel(vh2.datelbl, position);
            String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            mydate = mydate.replace(".", "");
            String surationasec = "";

            if (message.getDuration() != null) {
                String duration = message.getDuration();
                surationasec = getTimeString(Long.parseLong(duration));
                vh2.duration.setText(surationasec);
            }
            if (!message.getMessageId().contains("-g-")) {
                final String[] array = message.getMessageId().split("-");
                vh2.fromname.setText(getname.getSendername(array[1], message.getSenderMsisdn()));
                vh2.toname.setText("you");

                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
                ChatappContactModel info = contactDB_sqlite.getUserOpponenetDetails(array[1]);

                if (info != null && info.getAvatarImageUrl() != null) {
                    String userprofile = info.getAvatarImageUrl();
//TODO tharani map
                    Glide.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            //.transform(new CircleTransform())
                            .into(vh2.userprofile);
                } else {
                    vh2.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                }
            } else {
                vh2.fromname.setText(getname.getSendername(message.getGroupMsgFrom(), message.getSenderMsisdn()));
                vh2.toname.setText(message.getGroupName());
                String docid = message.getMessageId();
                final String[] array = docid.split("-");

                ContactDB_Sqlite contactDB = CoreController.getContactSqliteDBintstance(mContext);
                chatappContacts = contactDB.getAllChatappContacts();
                Log.e("SIZZZE", chatappContacts + "");
                String getAvatarImageUrl = contactDB.getUserImage(message.getGroupMsgFrom());

                if (getAvatarImageUrl != null) {
//TODO tharani map
                    Glide.with(mContext).load(Constants.SOCKET_IP + getAvatarImageUrl).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            //.transform(new CircleTransform())
                            .into(vh2.userprofile);
                } else {
                    vh2.userprofile.setBackgroundResource(R.mipmap.group_chat_attachment_profile_icon);
                }
                /*if ((groupInfoSession.getGroupInfo(array[1]) != null) && (groupInfoSession.getGroupInfo(array[1]) != null)) {
                    String userprofile = groupInfoSession.getGroupInfo(array[1]).getAvatarPath();
                    Picasso.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            .transform(new CircleTransform()).into(vh2.userprofile);
                } else {
                    vh2.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                }*/
            }
            //TODO tharani map
            Glide.with(mContext).clear(vh2.thumbnail);
            if (message.getTextMessage() != null && !message.getTextMessage().equalsIgnoreCase("")) {
                vh2.video_belowlayout.setVisibility(View.VISIBLE);
                vh2.videoabove_layout.setVisibility(View.GONE);
                vh2.caption.setVisibility(View.VISIBLE);
                vh2.captiontext.setText(message.getTextMessage());
                vh2.time.setText(mydate);
                if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                    vh2.starredindicator_above.setVisibility(View.VISIBLE);
                } else {
                    vh2.starredindicator_above.setVisibility(View.GONE);
                }
            } else {
                vh2.video_belowlayout.setVisibility(View.GONE);
                vh2.videoabove_layout.setVisibility(View.VISIBLE);
                vh2.caption.setVisibility(View.GONE);
                vh2.duration_above.setText(surationasec);
                vh2.ts_abovecaption.setText(mydate);
                if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                    vh2.starredindicator_below.setVisibility(View.VISIBLE);
                } else {
                    vh2.starredindicator_below.setVisibility(View.GONE);
                }
            }

            try {
                if (message.isSelf()) {
                    vh2.thumbnail.setImageBitmap(ThumbnailUtils.createVideoThumbnail(message.getVideoPath(),
                            MediaStore.Images.Thumbnails.MINI_KIND));
                } else {


                    if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_COMPLETED) {

                        File file = new File(message.getChatFileLocalPath());

                        if (file.exists()) {
                            vh2.thumbnail.setImageBitmap(ThumbnailUtils.createVideoThumbnail(message.getChatFileLocalPath(),
                                    MediaStore.Images.Thumbnails.MINI_KIND));

                            vh2.thumbnail.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    try {
                                        if (message.getChatFileLocalPath() != null) {
                                            /*Intent intent = new Intent();
                                            intent.setAction(Intent.ACTION_VIEW);
                                            intent.setDataAndType(Uri.parse(message.getChatFileLocalPath()), "video/*");
                                            mContext.startActivity(intent);*/
                                            CallIntentFileProvider(message.getChatFileLocalPath());
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });


                        } else {
                            String thumbnailData = message.getThumbnailData();
                            String imageDataBytes = thumbnailData.substring(thumbnailData.indexOf(",") + 1);
                            byte[] decodedString = Base64.decode(imageDataBytes, Base64.DEFAULT);
                            Bitmap thumbBmp = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            vh2.thumbnail.setImageBitmap(thumbBmp);

                        }


                    } else {
                        if (message.getUploadDownloadProgress() == 0) {
                            vh2.download.setVisibility(View.VISIBLE);
                        }
                        if (message.getThumbnailData() != null) {
                            byte[] decodedString = Base64.decode(message.getThumbnailData(), Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            vh2.thumbnail.setImageBitmap(decodedByte);
                        }
                    }

                }
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

        }
    }


    /**
     * LocationReceived
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderStarredLocationReceived(VHStarredLocationREceived vh2, final int position) {
        final MessageItemChat message = mListData.get(position);
        if (message != null) {
            configureDateLabel(vh2.datelbl, position);
            String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            mydate = mydate.replace(".", "");
            vh2.time.setText(mydate);
            if (!message.getMessageId().contains("-g-")) {
                final String[] array = message.getMessageId().split("-");
                vh2.fromname.setText(getname.getSendername(array[1], message.getSenderMsisdn()));
                vh2.toname.setText("You");

                ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
                ChatappContactModel info = contactDB_sqlite.getUserOpponenetDetails(array[1]);

                if (info != null && info.getAvatarImageUrl() != null) {
                    String userprofile = info.getAvatarImageUrl();
//TODO tharani map
                    Glide.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            //.transform(new CircleTransform())
                            .into(vh2.userprofile);
                } else {
                    vh2.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                }
            } else {
                vh2.fromname.setText(getname.getSendername(message.getGroupMsgFrom(), message.getSenderMsisdn()));
                vh2.toname.setText(message.getGroupName());
                String docid = message.getMessageId();
                final String[] array = docid.split("-");
                ContactDB_Sqlite contactDB = CoreController.getContactSqliteDBintstance(mContext);
                String getAvatarImageUrl = contactDB.getUserImage(message.getGroupMsgFrom());

                if (getAvatarImageUrl != null) {
//TODO tharani map
                    Glide.with(mContext).load(Constants.SOCKET_IP + getAvatarImageUrl).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            .into(vh2.userprofile);
                } else {
                    vh2.userprofile.setBackgroundResource(R.mipmap.group_chat_attachment_profile_icon);
                }
                /*if ((groupInfoSession.getGroupInfo(array[1]) != null) && (groupInfoSession.getGroupInfo(array[1]) != null)) {
                    String userprofile = groupInfoSession.getGroupInfo(array[1]).getAvatarPath();
                    Picasso.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                            R.mipmap.chat_attachment_profile_default_image_frame)
                            .transform(new CircleTransform()).into(vh2.userprofile);
                } else {
                    vh2.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                }*/
            }
            //TODO tharani map
            Glide.with(mContext).
                    load(R.drawable.google_map).
                    transition(withCrossFade()).
                    centerCrop().
                    dontAnimate().into(vh2.ivMap);
            /*if (message.getWebLinkImgThumb() != null) {
                byte[] decodedString = Base64.decode(message.getWebLinkImgThumb(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                vh2.ivMap.setImageBitmap(decodedByte);*/

            vh2.ivMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Uri gmmIntentUri = Uri.parse(message.getWebLink());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mContext.startActivity(mapIntent);
                }
            });
           /* } else {
                vh2.ivMap.setBackgroundResource(0);
            }*/

            /*if (message.getWebLinkImgUrl() != null && !message.getWebLinkImgUrl().equals("")) {
                Picasso.with(mContext).load(message.getWebLinkImgUrl()).into(vh2.ivMap);
            } else {
                vh2.ivMap.setBackgroundResource(0);
            }*/


        }
    }


    /**
     * ContactReceived
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderStarredContactReceived(VHStarredContactReceived vh2, final int position) {
        final MessageItemChat message = mListData.get(position);
        Boolean isChatappcontact = false;
        vh2.add.setVisibility(View.GONE);
        vh2.invite.setVisibility(View.GONE);
        if (message != null) {
            configureDateLabel(vh2.tvDateLbl, position);
            if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                vh2.starredindicator_below.setVisibility(View.VISIBLE);
            } else {
                vh2.starredindicator_below.setVisibility(View.GONE);
            }
            if (message.getMessageId().contains("-g-")) {
                vh2.senderName.setVisibility(View.VISIBLE);
                vh2.senderName.setText(message.getSenderName());
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
            if (!message.getMessageId().contains("-g-")) {
                final String[] array = message.getMessageId().split("-");
                vh2.fromname.setText(getname.getSendername(array[1], message.getSenderMsisdn()));
                vh2.toname.setText("you");
            } else {
                vh2.fromname.setText(getname.getSendername(message.getGroupMsgFrom(), message.getSenderMsisdn()));
                vh2.toname.setText(message.getGroupName());
            }

            vh2.senderName.setText(message.getSenderName());

            String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
            mydate = mydate.replace(".", "");
            vh2.time.setText(mydate);

            final String contactName = message.getContactName();
            final String contactNumber = message.getContactNumber();
            final String contactDetails = message.getDetailedContacts();

            String chatappcontactid = null;
            if (chatappContacts != null) {
                for (int i = 0; i < chatappContacts.size(); i++) {
                    if (chatappContacts.get(i) != null && chatappContacts.get(i).getNumberInDevice() != null && chatappContacts.get(i).getNumberInDevice().contains("" + contactNumber)) {
                        chatappcontactid = chatappContacts.get(i).get_id();
                        getname.configProfilepic(vh2.contactimage, chatappcontactid, true, false, R.mipmap.contact_off);
                        break;
                    } else {
                        vh2.contactimage.setImageResource(R.mipmap.contact_off);
                    }
                }
            }

            if (contactNumber.equals("")) {
                vh2.contact_add_invite.setVisibility(View.GONE);
                vh2.add.setVisibility(View.GONE);
                vh2.invite.setVisibility(View.GONE);
                vh2.message1.setVisibility(View.GONE);
                vh2.v1.setVisibility(View.GONE);
            }

            //  Boolean isalreadysavecontact = message.getcontactsavethere();
            isChatappcontact = chatappcontactid != null && !chatappcontactid.equalsIgnoreCase("");


            if (contactNumber.isEmpty() || contactNumber.equals("")) {

                vh2.contact_add_invite.setVisibility(View.GONE);
            }

            if (!isChatappcontact && contactNumber.equals("")) {
                vh2.add.setVisibility(View.GONE);
                vh2.invite.setVisibility(View.GONE);
                vh2.message1.setVisibility(View.GONE);


            } else if (!isChatappcontact && !contactNumber.equals("")) {
                vh2.add.setVisibility(View.GONE);
                vh2.invite.setVisibility(View.VISIBLE);
                vh2.v1.setVisibility(View.VISIBLE);
                vh2.message1.setVisibility(View.GONE);

            } else if (isChatappcontact && contactNumber.equals("")) {
                vh2.add.setVisibility(View.GONE);
                vh2.invite.setVisibility(View.GONE);
                vh2.message1.setVisibility(View.GONE);
            } else if (isChatappcontact && !contactNumber.equals("")) {
                vh2.add.setVisibility(View.GONE);
                vh2.invite.setVisibility(View.GONE);
                vh2.message1.setVisibility(View.VISIBLE);
                vh2.v1.setVisibility(View.VISIBLE);
            }

            vh2.invite1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    performInvite();
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
                    Intent intent = new Intent(mContext, Savecontact.class);
                    intent.putExtra("name", contactName);
                    intent.putExtra("number", contactNumber);
                    intent.putExtra("contactList", contactDetails);
                    mContext.startActivity(intent);
                    return false;
                }
            });
            final String finalChatappcontactid = chatappcontactid;
            vh2.message1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ChatViewActivity.class);
                    intent.putExtra("receiverUid", "");
                    intent.putExtra("receiverName", "");
                    intent.putExtra("documentId", finalChatappcontactid);
                    intent.putExtra("type", 0);
                    intent.putExtra("backfrom", true);
                    intent.putExtra("Username", contactName);
                    intent.putExtra("msisdn", contactNumber);
                    intent.putExtra("Image", "");
                    mContext.startActivity(intent);
                }
            });
            vh2.invite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    performInvite();
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
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            if (contactName == null || contactName.isEmpty()) {
                vh2.contactName.setText("Unknown");
            } else if (contactNumber == null || contactNumber.isEmpty()) {
                vh2.contactNumber.setText("No Number");
            }


        }
    }


    /**
     * AudioReceived
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderStarredAudioReceived(final VHStarredAudioReceived vh2, final int position) {

        try {
            final MessageItemChat message = mListData.get(position);

            if (message != null) {
                configureDateLabel(vh2.datelbl, position);

                String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                mydate = mydate.replace(".", "");
                vh2.time_ts.setText(mydate);

                if (!message.getMessageId().contains("-g-")) {
                    final String[] array = message.getMessageId().split("-");
                    vh2.fromname.setText(getname.getSendername(array[1], message.getSenderMsisdn()));
                    vh2.toname.setText("You");

                    ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
                    ChatappContactModel info = contactDB_sqlite.getUserOpponenetDetails(array[1]);

                    if (info != null && info.getAvatarImageUrl() != null) {
                        String userprofile = info.getAvatarImageUrl();
//TODO tharani map
                        Glide.with(mContext).load(Constants.SOCKET_IP + userprofile).error(
                                R.mipmap.chat_attachment_profile_default_image_frame)
                                //.transform(new CircleTransform())
                                .into(vh2.userprofile);
                    } else {
                        vh2.userprofile.setBackgroundResource(R.mipmap.chat_attachment_profile_default_image_frame);
                    }
                } else {
                    vh2.fromname.setText(getname.getSendername(message.getGroupMsgFrom(), message.getSenderMsisdn()));
                    vh2.toname.setText(message.getGroupName());
                    String docid = message.getMessageId();
                    final String[] array = docid.split("-");
                    ContactDB_Sqlite contactDB = CoreController.getContactSqliteDBintstance(mContext);
                    String getAvatarImageUrl = contactDB.getUserImage(message.getGroupMsgFrom());

                    if (getAvatarImageUrl != null) {
//TODO tharani map
                        Glide.with(mContext).load(Constants.SOCKET_IP + getAvatarImageUrl).error(
                                R.mipmap.chat_attachment_profile_default_image_frame)
                                .into(vh2.userprofile);
                    } else {
                        vh2.userprofile.setBackgroundResource(R.mipmap.group_chat_attachment_profile_icon);
                    }

                }

            /*if (!message.getMessageId().contains("-g-")) {
                String toUserId = message.getMessageId().split("-")[1];
                vh2.fromname.setText(getname.getSendername(toUserId, message.getSenderMsisdn()));
                getname.configProfilepic(vh2.record_image, toUserId, true, false, R.mipmap.contact_off);
            } else {
                String toUserId = message.getGroupMsgFrom();
                getname.configProfilepic(vh2.record_image, toUserId, true, false, R.mipmap.contact_off);
                vh2.fromname.setText(message.getGroupName());
            }
            vh2.toname.setText("you");
            SessionManager sessionmanager = SessionManager.getInstance(mContext);
            String userprofilepic = sessionmanager.getUserProfilePic();
            Glide.with(mContext).load(Constants.SOCKET_IP + userprofilepic).error(
                    R.mipmap.chat_attachment_profile_default_image_frame)
                    //.transform(new CircleTransform())
                    .into(vh2.userprofile);
*/
                if (message.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                    vh2.audiotrack_layout.setVisibility(View.GONE);
                    vh2.record_image.setVisibility(View.VISIBLE);
                    vh2.record_icon.setVisibility(View.VISIBLE);
                    vh2.recodingduration.setVisibility(View.VISIBLE);
                    if (message.getDuration() != null) {
                        // String duration = message.getDuration();
                        // String surationasec = getTimeString(Long.parseLong(duration));
                        vh2.recodingduration.setText(message.getDuration());
                    }
                } else {
                    vh2.audiotrack_layout.setVisibility(View.VISIBLE);
                    vh2.record_image.setVisibility(View.GONE);
                    vh2.record_icon.setVisibility(View.GONE);
                    vh2.recodingduration.setVisibility(View.GONE);
                    String duration = message.getDuration();
                    if (duration != null && !duration.equalsIgnoreCase("")) {
//                    String surationasec = getTimeString(Long.parseLong(duration));
                        vh2.duration.setText(duration);
                    }
                }

                try {
                    if (message.isSelf()) {

                    } else {
                        vh2.sbDuration.setProgress(message.getPlayerCurrentPosition());

                        if (message.getDownloadStatus() == MessageFactory.DOWNLOAD_STATUS_COMPLETED) {
                            // Remove user dragging in seekbar
                            vh2.download.setVisibility(View.GONE);
                            vh2.pbDownload.setVisibility(View.GONE);
                            vh2.playButton.setVisibility(View.VISIBLE);

                            if (message.isMediaPlaying()) {
                                vh2.playButton.setBackgroundResource(R.drawable.ic_pause);

                                long value = message.getPlayerCurrentPosition() * 1000;
                                if (message.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                                    vh2.recodingduration.setText(getTimeString(value));
                                } else {
                                    vh2.duration.setText(getTimeString(value));
                                }
                            } else {
                                vh2.playButton.setBackgroundResource(R.drawable.ic_play);
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
                                        playAudio(position, message, vh2.sbDuration);
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
                                        playAudio(position, message, vh2.sbDuration);
                                    }
                                    try {
                                        notifyDataSetChanged();
                                    } catch (Exception e) {
                                        e.printStackTrace();
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


                        } else {

                            if (message.getUploadDownloadProgress() == 0) {
                                vh2.download.setVisibility(View.VISIBLE);
                                vh2.playButton.setVisibility(View.GONE);
                                vh2.pbDownload.setVisibility(View.GONE);
                                vh2.download.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        vh2.download.setVisibility(View.GONE);
                                        vh2.pbDownload.setVisibility(View.VISIBLE);

                                    }
                                });


                            } else {
                                vh2.download.setVisibility(View.GONE);
                                vh2.pbDownload.setVisibility(View.GONE);
                                vh2.playButton.setVisibility(View.VISIBLE);
                            }


                        }


                    }
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * MessageSent
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderStarredMessageSent(VHStarredMessageSent vh2, final int position) {

        try {
            final MessageItemChat message = mListData.get(position);
            vh2.fromname.setText("You");
            if (!message.getMessageId().contains("-g-")) {
                vh2.toname.setText(getname.getSendername(message.getReceiverID(), message.getSenderMsisdn()));
            } else {
                vh2.toname.setText(message.getGroupName());
            }
            SessionManager sessionmanager = SessionManager.getInstance(mContext);
            String userprofilepic = sessionmanager.getUserProfilePic();
            //TODO tharani map
            Glide.with(mContext).load(Constants.SOCKET_IP + userprofilepic).error(
                    R.mipmap.chat_attachment_profile_default_image_frame)
                    //.transform(new CircleTransform())
                    .into(vh2.userprofile);


            // vh2.replaymessagemedio.setVisibility(View.GONE);
            vh2.clock.setVisibility(View.GONE);
            vh2.singleTick.setVisibility(View.GONE);
            vh2.doubleTickGreen.setVisibility(View.GONE);
            vh2.doubleTickBlue.setVisibility(View.GONE);
            vh2.time.setVisibility(View.GONE);
            vh2.timebelow.setVisibility(View.GONE);
            vh2.starredbelow.setVisibility(View.GONE);
            vh2.clockbelow.setVisibility(View.GONE);
            vh2.singleTickbelow.setVisibility(View.GONE);
            vh2.doubleTickGreenbelow.setVisibility(View.GONE);
            vh2.doubleTickBluebelow.setVisibility(View.GONE);

            configureDateLabel(vh2.datelbl, position);


            if (message != null) {

                if (message.isSelected())
                    vh2.mainSent.setBackgroundColor(Color.parseColor("#EBF4FA"));
                    // vh2.mainSent.setBackgroundResource(R.drawable.background_selector);
                else
                    vh2.mainSent.setBackgroundColor(Color.parseColor("#00000000"));
                String textmessage = message.getTextMessage();
                if (!textmessage.contains("\n") && textmessage.length() <= 20) {
                    vh2.time.setVisibility(View.VISIBLE);
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vh2.starred.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starred.setVisibility(View.GONE);
                    }

                    String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                    mydate = mydate.replace(".", "");
                    vh2.time.setText(mydate);

                    try {
                        vh2.message.setText(message.getTextMessage());
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }

                    String status = message.getDeliveryStatus();
          /*  if (status == null) {
                status = MessageFactory.DELIVERY_STATUS_SENT;
            }*/
                    if (status.equals("3")) {

                        vh2.clock.setVisibility(View.GONE);
                        vh2.singleTick.setVisibility(View.GONE);

                        vh2.doubleTickGreen.setVisibility(View.GONE);
                        vh2.doubleTickBlue.setVisibility(View.VISIBLE);

                    } else if (status.equals("2")) {
                        vh2.clock.setVisibility(View.GONE);
                        vh2.singleTick.setVisibility(View.GONE);

                        vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                        vh2.doubleTickBlue.setVisibility(View.GONE);
                    } else if (status.equals("1")) {

                        vh2.clock.setVisibility(View.GONE);
                        vh2.singleTick.setVisibility(View.VISIBLE);

                        vh2.doubleTickGreen.setVisibility(View.GONE);
                        vh2.doubleTickBlue.setVisibility(View.GONE);
                    } else {
                        vh2.clock.setVisibility(View.VISIBLE);
                        vh2.singleTick.setVisibility(View.GONE);
                        vh2.doubleTickGreen.setVisibility(View.GONE);
                        vh2.doubleTickBlue.setVisibility(View.GONE);
                    }
                } else {
                    vh2.timebelow.setVisibility(View.VISIBLE);
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vh2.starredbelow.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredbelow.setVisibility(View.GONE);
                    }

                    String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                    mydate = mydate.replace(".", "");
                    vh2.time.setText(mydate);

                    try {
                        vh2.message.setText(message.getTextMessage());
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }

                    String status = message.getDeliveryStatus();
          /*  if (status == null) {
                status = MessageFactory.DELIVERY_STATUS_SENT;
            }*/
                    if (status.equals("3")) {

                        vh2.clockbelow.setVisibility(View.GONE);
                        vh2.singleTickbelow.setVisibility(View.GONE);
                        vh2.doubleTickGreenbelow.setVisibility(View.GONE);
                        vh2.doubleTickBluebelow.setVisibility(View.VISIBLE);

                    } else if (status.equals("2")) {
                        vh2.clockbelow.setVisibility(View.GONE);
                        vh2.singleTickbelow.setVisibility(View.GONE);

                        vh2.doubleTickGreenbelow.setVisibility(View.VISIBLE);
                        vh2.doubleTickBluebelow.setVisibility(View.GONE);
                    } else if (status.equals("1")) {

                        vh2.clockbelow.setVisibility(View.GONE);
                        vh2.singleTickbelow.setVisibility(View.VISIBLE);

                        vh2.doubleTickGreenbelow.setVisibility(View.GONE);
                        vh2.doubleTickBluebelow.setVisibility(View.GONE);
                    } else {

                        vh2.clockbelow.setVisibility(View.VISIBLE);
                        vh2.singleTickbelow.setVisibility(View.GONE);
                        vh2.doubleTickGreenbelow.setVisibility(View.GONE);
                        vh2.doubleTickBluebelow.setVisibility(View.GONE);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * ImageSent
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderStarredImageSent(final VHStarredImageSent vh2, final int position) {

        try {
            final MessageItemChat message = mListData.get(position);
            Boolean caption = false;
            if (message != null) {

                String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                mydate = mydate.replace(".", "");

                configureDateLabel(vh2.datelbl, position);
                // vh2.senderName.setText(message.getSenderName());

                vh2.fromname.setText("You");
                if (!message.getMessageId().contains("-g-")) {
                    vh2.toname.setText(getname.getSendername(message.getReceiverID(), message.getSenderMsisdn()));
                } else {
                    vh2.toname.setText(message.getGroupName());
                }
                if (message.getTextMessage() != null && !message.getTextMessage().trim().equalsIgnoreCase("")) {
                    vh2.caption.setVisibility(View.VISIBLE);
                    vh2.ts_abovecaption.setVisibility(View.GONE);
                    vh2.time_layout.setVisibility(View.VISIBLE);
                    vh2.captiontext.setText(message.getTextMessage());
                    caption = true;
                    vh2.time.setText(mydate);
                    vh2.starredindicator_below.setVisibility(View.VISIBLE);

                } else {
                    vh2.caption.setVisibility(View.GONE);
                    vh2.ts_abovecaption.setVisibility(View.VISIBLE);
                    vh2.time_layout.setVisibility(View.GONE);
                    vh2.ts_abovecaption.setText(mydate);
                    caption = false;
                    vh2.starredindicator_above.setVisibility(View.VISIBLE);
                }
                SessionManager sessionmanager = SessionManager.getInstance(mContext);
                String userprofilepic = sessionmanager.getUserProfilePic();
                //TODO tharani map
                Glide.with(mContext).load(Constants.SOCKET_IP + userprofilepic).error(
                        R.mipmap.chat_attachment_profile_default_image_frame)
                        //.transform(new CircleTransform())
                        .into(vh2.userprofile);
            /*try {
                String imgData = message.getImagePath();
                byte[] decodedString = Base64.decode(imgData, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                if(decodedByte != null) {
                    vh2.imageView.setImageBitmap(decodedByte);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }*/
                try {
                    final String imgPath = message.getChatFileLocalPath();
                    vh2.imageView.setImageBitmap(ChatappImageUtils.decodeBitmapFromFile(imgPath, 220, 200));

                    vh2.imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(mContext, ImageZoom.class);
                            intent.putExtra("image", imgPath);
                            mContext.startActivity(intent);
                        }
                    });

                /*Double width = Double.parseDouble(message.getChatFileWidth());
                Double height = Double.parseDouble(message.getChatFileHeight());

                ViewGroup.LayoutParams params = vh2.imageView.getLayoutParams();
                float scale = mContext.getResources().getDisplayMetrics().density;
                int widthSize = (int) (250 * scale + 0.5f);
                int heightSize = (int) (150 * scale + 0.5f);

                if(width > 329) {
                    params.width = widthSize;
                } else {
                    params.width = width.intValue();
                }

                if(height > 149) {
                    params.height = heightSize;
                } else {
                    params.height = height.intValue();
                }

                vh2.imageView.setLayoutParams(params);*/

//                Picasso.with(mContext).load(imgPath).into(vh2.imageView);

                } catch (Exception e) {
                    e.printStackTrace();
                }

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

                if (status.equals("3")) {
                    vh2.clock_above.setVisibility(View.GONE);
                    vh2.single_tick_green_above.setVisibility(View.GONE);
                    vh2.double_tick_green_above.setVisibility(View.GONE);
                    vh2.double_tick_blue_above.setVisibility(View.VISIBLE);

                } else if (status.equals("2")) {
                    vh2.clock_above.setVisibility(View.GONE);
                    vh2.single_tick_green_above.setVisibility(View.GONE);
                    vh2.double_tick_green_above.setVisibility(View.VISIBLE);
                    vh2.double_tick_blue_above.setVisibility(View.GONE);
                } else if (status.equals("1")) {

                    vh2.clock_above.setVisibility(View.GONE);
                    vh2.single_tick_green_above.setVisibility(View.VISIBLE);
                    vh2.double_tick_green_above.setVisibility(View.GONE);
                    vh2.double_tick_blue_above.setVisibility(View.GONE);
                } else {
                    vh2.clock_above.setVisibility(View.VISIBLE);
                    vh2.single_tick_green_above.setVisibility(View.GONE);
                    vh2.double_tick_green_above.setVisibility(View.GONE);
                    vh2.double_tick_blue_above.setVisibility(View.GONE);
                }
            } else {
                vh2.ts_abovecaption_layout.setVisibility(View.GONE);
                vh2.time_layout.setVisibility(View.VISIBLE);
                vh2.rlMsgStatus_above.setVisibility(View.GONE);
                vh2.rlMsgStatus.setVisibility(View.VISIBLE);


                if (status.equals("3")) {

                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.GONE);
                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.VISIBLE);

                } else if (status.equals("2")) {
                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.GONE);

                    vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
                } else if (status.equals("1")) {

                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.VISIBLE);

                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
                } else {


                    vh2.clock.setVisibility(View.VISIBLE);
                    vh2.singleTick.setVisibility(View.GONE);

                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    /**
     * VideoSent
     *
     * @param vh2      specific view holder
     * @param position specific position
     */
    private void configureViewHolderStarredVideoSent(VHStarredVideoSent vh2, final int position) {
        try {
            final MessageItemChat message = mListData.get(position);
            if (message != null) {
                String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                mydate = mydate.replace(".", "");

                String surationasec = "";
                Boolean caption = false;
                configureDateLabel(vh2.datelbl, position);
                // vh2.senderName.setText(message.getSenderName());

                MediaMetadataRetriever mdr = new MediaMetadataRetriever();
                if (message.getChatFileLocalPath() != null) {
                    mdr.setDataSource(message.getChatFileLocalPath());
                    String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    surationasec = getTimeString(Long.parseLong(duration));

                }
                vh2.fromname.setText("You");
                if (!message.getMessageId().contains("-g-")) {
                    vh2.toname.setText(getname.getSendername(message.getReceiverID(), message.getSenderMsisdn()));
                } else {
                    vh2.toname.setText(message.getGroupName());
                }
                if (message.getTextMessage() != null) {
                    if (!message.getTextMessage().equalsIgnoreCase("")) {
                        vh2.caption.setVisibility(View.VISIBLE);
                        vh2.captiontext.setText(message.getTextMessage());
                    } else {
                        vh2.caption.setVisibility(View.GONE);
                    }
                } else {
                    vh2.caption.setVisibility(View.GONE);
                }
                SessionManager sessionmanager = SessionManager.getInstance(mContext);
                String userprofilepic = sessionmanager.getUserProfilePic();
                //TODO tharani map
                Glide.with(mContext).load(Constants.SOCKET_IP + userprofilepic).error(
                        R.mipmap.chat_attachment_profile_default_image_frame)
                        //.transform(new CircleTransform())
                        .into(vh2.userprofile);

                Glide.with(mContext).clear(vh2.thumbnail);
                if (message.getTextMessage() != null && !message.getTextMessage().trim().equalsIgnoreCase("")) {
                    caption = true;
                    vh2.videoabove_layout.setVisibility(View.GONE);
                    vh2.video_belowlayout.setVisibility(View.VISIBLE);
                    vh2.caption.setVisibility(View.VISIBLE);
                    vh2.captiontext.setText(message.getTextMessage());
                    vh2.time.setText(mydate);
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vh2.starredindicator_below.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredindicator_below.setVisibility(View.GONE);
                    }
                    vh2.duration.setText(surationasec);

                } else {

                    vh2.videoabove_layout.setVisibility(View.VISIBLE);
                    vh2.video_belowlayout.setVisibility(View.GONE);
                    vh2.caption.setVisibility(View.GONE);
                    vh2.ts_abovecaption.setText(mydate);
                    caption = false;
                    if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                        vh2.starredindicator_above.setVisibility(View.VISIBLE);
                    } else {
                        vh2.starredindicator_above.setVisibility(View.GONE);
                    }
                    vh2.duration_above.setText(surationasec);
                }

                // vh2.thumbnail.setImageDrawable (null);
                try {


                    vh2.thumbnail.setImageBitmap(ThumbnailUtils.createVideoThumbnail(message.getVideoPath(),
                            MediaStore.Images.Thumbnails.MINI_KIND));

                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }

                vh2.thumbnail.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        try {
                            if (message.getChatFileLocalPath() != null) {
                            /*Intent intent = new Intent();
                            intent.setAction(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(message.getChatFileLocalPath()), "video/*");
                            mContext.startActivity(intent);*/
                                CallIntentFileProvider(message.getChatFileLocalPath());
                            }
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }

                    }
                });


                String status = message.getDeliveryStatus();

                if (!caption) {
                    vh2.videoabove_layout.setVisibility(View.VISIBLE);
                    vh2.video_belowlayout.setVisibility(View.GONE);

                    if (status.equals("3")) {
                        vh2.clock_above.setVisibility(View.GONE);
                        vh2.single_tick_green_above.setVisibility(View.GONE);
                        vh2.double_tick_green_above.setVisibility(View.GONE);
                        vh2.double_tick_blue_above.setVisibility(View.VISIBLE);

                    } else if (status.equals("2")) {
                        vh2.clock_above.setVisibility(View.GONE);
                        vh2.single_tick_green_above.setVisibility(View.GONE);
                        vh2.double_tick_green_above.setVisibility(View.VISIBLE);
                        vh2.double_tick_blue_above.setVisibility(View.GONE);
                    } else if (status.equals("1")) {

                        vh2.clock_above.setVisibility(View.GONE);
                        vh2.single_tick_green_above.setVisibility(View.VISIBLE);
                        vh2.double_tick_green_above.setVisibility(View.GONE);
                        vh2.double_tick_blue_above.setVisibility(View.GONE);
                    } else {
                        vh2.clock_above.setVisibility(View.VISIBLE);
                        vh2.single_tick_green_above.setVisibility(View.GONE);
                        vh2.double_tick_green_above.setVisibility(View.GONE);
                        vh2.double_tick_blue_above.setVisibility(View.GONE);
                    }
                } else {

                    vh2.videoabove_layout.setVisibility(View.GONE);
                    vh2.video_belowlayout.setVisibility(View.VISIBLE);

                    if (status.equals("3")) {
                        vh2.clock.setVisibility(View.GONE);
                        vh2.singleTick.setVisibility(View.GONE);
                        vh2.doubleTickGreen.setVisibility(View.GONE);
                        vh2.doubleTickBlue.setVisibility(View.VISIBLE);

                    } else if (status.equals("2")) {
                        vh2.clock.setVisibility(View.GONE);
                        vh2.singleTick.setVisibility(View.GONE);
                        vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                        vh2.doubleTickBlue.setVisibility(View.GONE);
                    } else if (status.equals("1")) {
                        vh2.clock.setVisibility(View.GONE);
                        vh2.singleTick.setVisibility(View.VISIBLE);
                        vh2.doubleTickGreen.setVisibility(View.GONE);
                        vh2.doubleTickBlue.setVisibility(View.GONE);
                    } else {
                        vh2.clock.setVisibility(View.VISIBLE);
                        vh2.singleTick.setVisibility(View.GONE);
                        vh2.doubleTickGreen.setVisibility(View.GONE);
                        vh2.doubleTickBlue.setVisibility(View.GONE);
                    }

                }

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
    private void configureViewHolderStarredLocationSent(final VHStarredLocationSent vh2, final int position) {
        try {
            final MessageItemChat message = mListData.get(position);
            if (message != null) {

                configureDateLabel(vh2.datelbl, position);
                // vh2.senderName.setText(message.getSenderName());

                String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                mydate = mydate.replace(".", "");
                vh2.time.setText(mydate);

                vh2.fromname.setText("You");
                if (!message.getMessageId().contains("-g-")) {
                    vh2.toname.setText(getname.getSendername(message.getReceiverID(), message.getSenderMsisdn()));
                } else {
                    vh2.toname.setText(message.getGroupName());
                }
                SessionManager sessionmanager = SessionManager.getInstance(mContext);
                String userprofilepic = sessionmanager.getUserProfilePic();
                //TODO tharani map
                Glide.with(mContext).load(Constants.SOCKET_IP + userprofilepic).error(
                        R.mipmap.chat_attachment_profile_default_image_frame)
                        //.transform(new CircleTransform())
                        .into(vh2.userprofile);

           /* ImageLoader imageLoader = CoreController.getInstance().getImageLoader();
            imageLoader.get(message.getWebLinkImgUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        vh2.ivMap.setImageBitmap(response.getBitmap());

                        vh2.ivMap.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Uri gmmIntentUri = Uri.parse(message.getWebLink());
                                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                mContext.startActivity(mapIntent);
                            }
                        });
                    }
                }

                @Override
                public void onErrorResponse(VolleyError error) {
                    vh2.ivMap.setImageResource(0);
                }
            });*/
//TODO tharani map
                Glide.with(mContext).
                        load(R.drawable.google_map).
                        transition(withCrossFade()).
                        centerCrop().
                        dontAnimate().into(vh2.ivMap);
                vh2.ivMap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Uri gmmIntentUri = Uri.parse(message.getWebLink());
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mContext.startActivity(mapIntent);
                    }
                });
                String status = message.getDeliveryStatus();

                if (status.equals("3")) {
                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.GONE);
                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.VISIBLE);

                } else if (status.equals("2")) {
                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.GONE);
                    vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
                } else if (status.equals("1")) {
                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.VISIBLE);
                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
                } else {
                    vh2.clock.setVisibility(View.VISIBLE);
                    vh2.singleTick.setVisibility(View.GONE);
                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
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
    private void configureViewHolderStarredContactSent(VHStarredContactSent vh2, final int position) {

        try {
            final MessageItemChat message = mListData.get(position);
            Boolean isChatappcontact = false;

            vh2.fromname.setText("You");
            if (!message.getMessageId().contains("-g-")) {
                vh2.toname.setText(getname.getSendername(message.getReceiverID(), message.getSenderMsisdn()));
            } else {
                vh2.toname.setText(message.getGroupName());
            }


            Boolean name = false;
            if (message != null) {
                configureDateLabel(vh2.tvDateLbl, position);
                if (message.getStarredStatus().equals(MessageFactory.MESSAGE_STARRED)) {
                    vh2.starredindicator_below.setVisibility(View.VISIBLE);
                } else {
                    vh2.starredindicator_below.setVisibility(View.GONE);
                }

                // vh2.senderName.setText(message.getSenderName());
                String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                mydate = mydate.replace(".", "");
                vh2.time.setText(mydate);
                Uri propic = message.getImageUrl();
                final String contactName = message.getContactName();
                final String contactNumber = message.getContactNumber();
                final String contactAvatar = message.getAvatarImageUrl();
                String modifiedMsisdn = contactNumber;
            /*String modifiedMsisdn = "";
            if (contactNumber.startsWith("+91")) {
                modifiedMsisdn = contactNumber.replace("+91", "");
            } else {
                modifiedMsisdn = "+91" + contactNumber;
            }*/

                modifiedMsisdn = modifiedMsisdn.replaceAll("\\s+", "");

                final String contactDetails = message.getDetailedContacts();
                vh2.contact_add_invite.setVisibility(View.GONE);
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

                if (contactNumber.equals("")) {
                    vh2.contact_add_invite.setVisibility(View.GONE);
                    vh2.message.setVisibility(View.GONE);
                    vh2.add.setVisibility(View.GONE);
                    vh2.invite.setVisibility(View.GONE);
                    vh2.v1.setVisibility(View.GONE);
                }
                try {
                    vh2.contactName.setText(contactName);
                    vh2.contactNumber.setText(contactNumber);
                    vh2.invite.setVisibility(View.GONE);


                } catch (StringIndexOutOfBoundsException e) {
                    vh2.contactNumber.setText("No Number");
                } catch (Exception e) {
                    vh2.contactNumber.setText("No Number");
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
                if (contactName == null || contactName.isEmpty()) {
                    vh2.contactName.setText("Unknown");
                } else if (contactNumber == null || contactNumber.isEmpty()) {
                    vh2.contactNumber.setText("No Number");
                }

                String chatappcontactid = null;

                for (int i = 0; i < chatappContacts.size(); i++) {
                    if (chatappContacts.get(i) != null && chatappContacts.get(i).getNumberInDevice() != null
                            && (chatappContacts.get(i).getNumberInDevice().equalsIgnoreCase("" + modifiedMsisdn))
                            || (chatappContacts.get(i).getMsisdn().equalsIgnoreCase(modifiedMsisdn))) {
                        chatappcontactid = chatappContacts.get(i).get_id();
                        getname.configProfilepic(vh2.contactImage, chatappcontactid, false, false, R.mipmap.contact_off);
                        isChatappcontact = true;
                        break;
                    } else {
                        // isChatappcontact = false;
                       /* Bitmap myImage = retrieveContactPhoto(mContext, contactNumber);
                        vh2.contactImage.setImageBitmap(myImage);*/

                        vh2.contactImage.setImageResource(R.mipmap.contact_off);
                    }

                }

                if (!isChatappcontact && contactNumber.equals("")) {
                    vh2.add.setVisibility(View.GONE);
                    vh2.invite.setVisibility(View.GONE);
                    vh2.message.setVisibility(View.GONE);


                } else if (!isChatappcontact && !contactNumber.equals("")) {
                    vh2.add.setVisibility(View.GONE);
                    vh2.invite.setVisibility(View.VISIBLE);
                    vh2.v1.setVisibility(View.VISIBLE);
                    vh2.message.setVisibility(View.GONE);

                } else if (isChatappcontact && contactNumber.equals("")) {
                    vh2.add.setVisibility(View.GONE);
                    vh2.invite.setVisibility(View.GONE);
                    vh2.message.setVisibility(View.GONE);
                } else if (isChatappcontact && !contactNumber.equals("")) {
                    vh2.add.setVisibility(View.GONE);
                    vh2.invite.setVisibility(View.GONE);
                    vh2.message.setVisibility(View.VISIBLE);
                    vh2.v1.setVisibility(View.VISIBLE);
                }

                String status = message.getDeliveryStatus();

                if (status.equals("3")) {

                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.GONE);

                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.VISIBLE);

                } else if (status.equals("2")) {
                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.GONE);

                    vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
                } else if (status.equals("1")) {

                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.VISIBLE);

                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
                } else {


                    vh2.clock.setVisibility(View.VISIBLE);
                    vh2.singleTick.setVisibility(View.GONE);

                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
                }

                vh2.invite.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        performInvite();
                        return false;
                    }
                });
                vh2.invite1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        performInvite();
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
                vh2.invite.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        performInvite();
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
                        Intent intent = new Intent(mContext, Savecontact.class);
                        intent.putExtra("name", contactName);
                        intent.putExtra("number", contactNumber);
                        intent.putExtra("contactList", contactDetails);
                        mContext.startActivity(intent);
                        return false;
                    }
                });

                final String finalChatappcontactid = chatappcontactid;
                vh2.message.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (finalChatappcontactid != null) {
                            checKForChatViewNavigation(contactNumber, contactName, contactAvatar, finalChatappcontactid);
                        }
                        return false;
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void configureViewHolderStarredAudioSent(final VHStarredAudioSent vh2, final int position) {

        try {
            final MessageItemChat message = mListData.get(position);
            if (message != null) {

                configureDateLabel(vh2.datelbl, position);
                // vh2.senderName.setText(message.getSenderName());

                String mydate = TimeStampUtils.get12HrTimeFormat(mContext, message.getTS());
                mydate = mydate.replace(".", "");
                vh2.time.setText(mydate);

                vh2.fromname.setText("You");
                if (message.getDuration() != null) {
                    String duration = message.getDuration();
                    //  String surationasec = getTimeString(Long.parseLong(duration));
                    vh2.duration.setText(duration);
                }
                if (!message.getMessageId().contains("-g-")) {
                    vh2.toname.setText(getname.getSendername(message.getReceiverID(), message.getSenderMsisdn()));
                } else {
                    vh2.toname.setText(message.getGroupName());
                }
                SessionManager sessionmanager = SessionManager.getInstance(mContext);
                String userprofilepic = sessionmanager.getUserProfilePic();
                //TODO tharani map
                Glide.with(mContext).load(Constants.SOCKET_IP + userprofilepic).error(
                        R.mipmap.chat_attachment_profile_default_image_frame)
                        //.transform(new CircleTransform())
                        .into(vh2.userprofile);
                if (message.isMediaPlaying()) {
                    vh2.playButton.setBackgroundResource(R.drawable.ic_pausesent);

                    long value = message.getPlayerCurrentPosition() * 1000;
                    if (value < message.getPlayerMaxDuration()) {
                        vh2.duration.setText(getTimeString(value));
                    }
                } else {
                    vh2.playButton.setBackgroundResource(R.drawable.audio_playsent);
                }

                if (message.getChatFileLocalPath() == null) {
                    message.setChatFileLocalPath("");
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
                            playAudio(position, message, vh2.sbDuration);
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
                            playAudio(position, message, vh2.sbDuration);
                        }
                        try {
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
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

                if (message.getUploadStatus() == MessageFactory.UPLOAD_STATUS_UPLOADING) {
                    try {
                        vh2.playButton.setVisibility(View.INVISIBLE);
                        vh2.sbDuration.setVisibility(View.INVISIBLE);
                        vh2.pbUpload.setVisibility(View.VISIBLE);
                        vh2.pbUpload.setProgress(message.getUploadDownloadProgress());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    vh2.pbUpload.setVisibility(View.GONE);
                    vh2.playButton.setVisibility(View.VISIBLE);
                    vh2.sbDuration.setVisibility(View.VISIBLE);
                }

                vh2.sbDuration.setProgress(message.getPlayerCurrentPosition());

                String status = message.getDeliveryStatus();

                if (status.equals("3")) {

                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.GONE);

                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.VISIBLE);

                } else if (status.equals("2")) {
                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.GONE);

                    vh2.doubleTickGreen.setVisibility(View.VISIBLE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
                } else if (status.equals("1")) {

                    vh2.clock.setVisibility(View.GONE);
                    vh2.singleTick.setVisibility(View.VISIBLE);

                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
                } else {


                    vh2.clock.setVisibility(View.VISIBLE);
                    vh2.singleTick.setVisibility(View.GONE);

                    vh2.doubleTickGreen.setVisibility(View.GONE);
                    vh2.doubleTickBlue.setVisibility(View.GONE);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void performInvite() {
        Resources resources = mContext.getResources();
        Intent emailIntent = new Intent();
        emailIntent.setAction(Intent.ACTION_SEND);
        // Native email client doesn't currently support HTML, but it doesn't hurt to try in case they fix it
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "E-Mail");
        emailIntent.setType("message/rfc822");
        PackageManager pm = mContext.getPackageManager();
        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.setType("text/plain");
        Intent openInChooser = Intent.createChooser(emailIntent, "E-Mail");
        List<ResolveInfo> resInfo = pm.queryIntentActivities(sendIntent, 0);
        List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();
        for (int i = 0; i < resInfo.size(); i++) {
            // Extract the label, append it, and repackage it in a LabeledIntent
            ResolveInfo ri = resInfo.get(i);
            String packageName = ri.activityInfo.packageName;
            if (packageName.contains("android.email")) {
                emailIntent.setPackage(packageName);
            } else if (packageName.contains("twitter") || packageName.contains("facebook") || packageName.contains("mms") || packageName.contains("android.gm")) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(packageName, ri.activityInfo.name));
                intent.setAction(Intent.ACTION_SEND);
                intent.setType("text/plain");
                if (packageName.contains("twitter")) {
                    intent.putExtra(Intent.EXTRA_TEXT, "Twitter");
                } else if (packageName.contains("facebook")) {
                    // Warning: Facebook IGNORES our text. They say "These fields are intended for users to express themselves. Pre-filling these fields erodes the authenticity of the user voice."
                    // One workaround is to use the Facebook SDK to post, but that doesn't allow the user to choose how they want to share. We can also make a custom landing page, and the link
                    // will show the <meta content ="..."> text from that page with our link in Facebook.
                    intent.putExtra(Intent.EXTRA_TEXT, "facebook");
                } else if (packageName.contains("mms")) {
                    intent.putExtra(Intent.EXTRA_TEXT, "SMS");
                } else if (packageName.contains("android.gm")) { // If Gmail shows up twice, try removing this else-if clause and the reference to "android.gm" above
                    intent.putExtra(Intent.EXTRA_SUBJECT, "G-Mail");
                    intent.setType("message/rfc822");
                }
                intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
            }
        }
        // convert intentList to array
        LabeledIntent[] extraIntents = intentList.toArray(new LabeledIntent[intentList.size()]);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, extraIntents);
        mContext.startActivity(openInChooser);
    }


    /**
     * configure Date Label
     *
     * @param tvDateLbl textview
     * @param position  specific position
     */
    private void configureDateLabel(TextView tvDateLbl, int position) {

        MessageItemChat item = mListData.get(position);
        if (item.getTS() != null && !item.getTS().equals("")) {
            String currentItemTS = item.getTS();
            if (currentItemTS.equals("0")) {
                tvDateLbl.setText("");
            } else {
                String ts = TimeStampUtils.getServerTimeStamp(mContext, Long.parseLong(currentItemTS));
                Date currentItemDate = TimeStampUtils.getDateFormat(Long.parseLong(ts));
                if (currentItemDate != null) {
                    setDateText(tvDateLbl, currentItemDate, currentItemTS);
                }
            }
        } else {
            tvDateLbl.setText("");
        }
    }


    /**
     * setDate
     *
     * @param tvDateLbl       textview
     * @param currentItemDate currentDate
     * @param ts              target time
     */
    private void setDateText(TextView tvDateLbl, Date currentItemDate, String ts) {
        Calendar calendar = Calendar.getInstance();
        Date today = TimeStampUtils.getDateFormat(calendar.getTimeInMillis());
        Date yesterday = TimeStampUtils.getYesterdayDate(today);

        if (currentItemDate.equals(today)) {
            String formatTime = TimeStampUtils.get12HrTimeFormat(mContext, ts);
            formatTime = formatTime.replace(".", "");
            tvDateLbl.setText(formatTime);
        } else if (currentItemDate.equals(yesterday)) {
            tvDateLbl.setText("Yesterday");
        } else {
            DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            String formatDate = df.format(currentItemDate);
            tvDateLbl.setText(formatDate);
        }
    }


    /**
     * updated arraylist
     *
     * @param aitem updated new data
     */
    public void updateInfo(ArrayList<MessageItemChat> aitem) {
        this.mListData = aitem;
        notifyDataSetChanged();
    }


    /**
     * getItemId
     *
     * @param position specific position
     * @return value
     */
    public long getItemId(int position) {
        return position;
    }


    /**
     * Filter the array list data
     */
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();
            try {
                ArrayList<MessageItemChat> nlist = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    results.values = mListData;
                    results.count = mListData.size();
                    System.out.println("nlist size in if part" + results.count);
                } else {
                    for (int i = 0; i < mListData.size(); i++) {
                        MessageItemChat chat_message_item = mListData.get(i);
                        Log.e("fgfg", filterString);
                        if ((chat_message_item.getTextMessage() != null && chat_message_item.getTextMessage().toLowerCase().
                                contains(filterString)) || (chat_message_item.getContactName() != null &&
                                chat_message_item.getContactName().toLowerCase().contains(filterString))) {
                            Log.e("fgfg", chat_message_item.getTextMessage());
                            nlist.add(chat_message_item);
                        }
                    }
                    results.values = nlist;
                    results.count = nlist.size();
                    System.out.println("nlist size in else part" + results.count);
                   /* if (results.count == 0) {
                        starredserach_result = true;
                    } else {
                        starredserach_result = false;
                    }*/
                }
            } catch (Exception e) {

            }


            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            mListData = (ArrayList<MessageItemChat>) results.values;
//            if (mListData.size() > 0) {
//                starredserach_result = true;
//            } else {
//                starredserach_result = false;
//            }
            notifyDataSetChanged();
               /* ArrayList<MessageItemChat> filtered = (ArrayList<MessageItemChat>) results.values;
                notifyDataSetChanged();*/
        }

    }

    /**
     * Filter the array list data
     *
     * @return filter value
     */
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                try {
                    ArrayList<MessageItemChat> FilteredArrList = new ArrayList<>();

                    if (mListData1 == null) {
                        mListData1 = new ArrayList<>(mListData); // saves the original data in mOriginalValues
                    }
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mListData1.size();
                        results.values = mListData1;
                        filteredText = null;

                    } else {
                        filteredText = constraint.toString();
                        constraint = constraint.toString().toLowerCase();

                        for (int i = 0; i < mListData1.size(); i++) {
                            MessageItemChat chat_message_item = mListData1.get(i);
                            if ((chat_message_item.getTextMessage() != null && chat_message_item.getTextMessage().toLowerCase().
                                    contains(filteredText)) || (chat_message_item.getContactName() != null &&
                                    chat_message_item.getContactName().toLowerCase().contains(filteredText))) {
                                Log.e("fgfg", chat_message_item.getTextMessage());
                                FilteredArrList.add(chat_message_item);
                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                } catch (Exception e) {

                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListData = (ArrayList<MessageItemChat>) results.values;
                notifyDataSetChanged();
            }
        };
        return filter;
    }


    /**
     * getTime in String
     *
     * @param millis input value
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


    /**
     * playAudio
     *
     * @param position   specific position
     * @param message    getting message from response
     * @param sbDuration time duration
     */
    private void playAudio(final int position, MessageItemChat message, final SeekBar sbDuration) {
        Uri uri = Uri.parse(message.getChatFileLocalPath());
        mPlayer = MediaPlayer.create(mContext, uri);
        mTimer = new Timer();

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
                        Log.d("SeekProgressEnd", "called");
                        notifyDataSetChanged();
                    }
                });
            }
        });
    }


    /**
     * stop Audio OnNavigate
     */
    public void stopAudioOnNavigate() {
        if (mTimer != null && mPlayer != null) {
            mTimer.cancel();
            mPlayer.release();
        }
    }

    /**
     * ChatViewNavigation
     *
     * @param msisdn      input value
     * @param contactName input value
     * @param image       input value
     * @param toUserId    input value
     */
    private void checKForChatViewNavigation(String msisdn, String contactName, String image, String toUserId) {
        ChatLockPojo lockPojo = getChatLockdetailfromDB(toUserId);
        if (SessionManager.getInstance(mContext).getLockChatEnabled().equals("1")
                && lockPojo != null) {

            String status = lockPojo.getStatus();
            String pwd = lockPojo.getPassword();

            String documentid = mCurrentUserId + "-" + toUserId;
            if (status.equals("1")) {
                openUnlockChatDialog(documentid, status, pwd, contactName, image, msisdn);
            } else {
                navigateTochatviewpage(contactName, msisdn, image, toUserId);
            }
        } else {
            navigateTochatviewpage(contactName, msisdn, image, toUserId);
        }
    }


    /**
     * navigateTochatviewpage
     *
     * @param contactName   input value
     * @param contactNumber input value
     * @param image         input value
     * @param toUserId      input value
     */
    private void navigateTochatviewpage(String contactName, String contactNumber, String image, String toUserId) {
        Intent intent = new Intent(mContext, ChatViewActivity.class);
        intent.putExtra("receiverUid", "");
        intent.putExtra("receiverName", "");
        intent.putExtra("documentId", toUserId);
        intent.putExtra("type", 0);
        intent.putExtra("backfrom", true);
        intent.putExtra("Username", contactName);
        intent.putExtra("msisdn", contactNumber);
        intent.putExtra("Image", image);
        mContext.startActivity(intent);
    }

    /**
     * Chat Lockdetail from Database
     *
     * @param toUserId input value
     * @return value
     */
    private ChatLockPojo getChatLockdetailfromDB(String toUserId) {
        String id = mCurrentUserId.concat("-").concat(toUserId);
        MessageDbController dbController = CoreController.getDBInstance(mContext);
        String convId = userInfoSession.getChatConvId(id);
        String receiverId = userInfoSession.getReceiverIdByConvId(convId);
        ChatLockPojo pojo = dbController.getChatLockData(receiverId, MessageFactory.CHAT_TYPE_SINGLE);
        return pojo;
    }

    /**
     * open Unlock ChatDialog
     *
     * @param documentid  input value
     * @param stat        input value
     * @param pwd         input value
     * @param contactname input value
     * @param image       input value
     * @param msisdn      input value
     */
    public void openUnlockChatDialog(String documentid, String stat, String pwd, String contactname, String image, String msisdn) {

        String toUserId = documentid.split("-")[1];


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

        bundle.putString("docid", toUserId);
        bundle.putString("page", "chatlist");
        bundle.putString("type", "single");
        bundle.putString("from", mCurrentUserId);
        dialog.setArguments(bundle);
        dialog.show(fragmentManager, "chatunLock");
    }

    /**
     * stop Audio On Clear Chat
     */
    public void stopAudioOnClearChat() {

        if (mTimer != null && mPlayer != null) {
            mTimer.cancel();
            mPlayer.release();
        }
    }

    /**
     * ChatMessageItem ClickListener interface
     */
    public interface ChatMessageItemClickListener {
        void onItemClick(View itemView, int position);

        void onItemLongClick(View itemView, int position);
    }
}