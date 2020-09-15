package com.chatapp.android.app.adapter;


/**
 * Controller class to feed the chatlist view based on chatlist gettter-setter item values
 */


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.android.R;
import com.chatapp.android.app.ChatList;
import com.chatapp.android.app.GroupChatList;
import com.chatapp.android.app.HomeScreen;
import com.chatapp.android.app.MessageData;
import com.chatapp.android.app.UserInfo;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.utils.MuteUnmute;
import com.chatapp.android.app.utils.TimeStampUtils;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.ShortcutBadgeManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.GroupInfoPojo;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.model.MuteStatusPojo;
import com.chatapp.android.core.service.Constants;
import com.google.api.client.util.DateTime;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * created by  Adhash Team 15/04/16.
 */

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable, FastScrollRecyclerView.SectionedAdapter {

    public List<MessageItemChat> mDisplayedValues;
    public Getcontactname getcontactname;
    int messageheaderCounter = 0;
    private List<MessageItemChat> mListData;
    private ShortcutBadgeManager shortcutBadgeManager;
    private ChatListItemClickListener listener;
    private String currentUserId;
    private Context mContext;
    private Session session;
    private UserInfoSession userInfoSession;
    private long imageTS;
    private GroupChatList callback;
    private SessionManager sessionManager;
    private String filteredText;
    private MessageDbController db;
    private ContactDB_Sqlite contactDB_sqlite;
    private String EXTime = "";
    private List<ChatappContactModel> selectedContactsList;


    /**
     * Create constructor
     *
     * @param mContext  The activity object inherits the Context object
     * @param mListData list of value
     */
    public ChatListAdapter(Context mContext, ArrayList<MessageItemChat> mListData) {
        this.mListData = mListData;
        this.mDisplayedValues = mListData;
        this.mListData = mListData;
        this.mContext = mContext;
        session = new Session(mContext);
        userInfoSession = new UserInfoSession(mContext);
        sessionManager = SessionManager.getInstance(mContext);
        shortcutBadgeManager = new ShortcutBadgeManager(mContext);
        currentUserId = SessionManager.getInstance(mContext).getCurrentUserID();
        getcontactname = new Getcontactname(mContext);
        imageTS = Calendar.getInstance().getTimeInMillis();
        db = CoreController.getDBInstance(mContext);
        contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return this.mDisplayedValues.size();
    }


    @Override
    public int getItemViewType(int position) {
        return 1;
    }


    /**
     * Get the value of MessageItemChat
     *
     * @param position select specific value from MessageItemChat
     * @return value
     */
    public MessageItemChat getItem(int position) {
        return mDisplayedValues.get(position);
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

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());


        View v = inflater.inflate(R.layout.lp_f3_chat, viewGroup, false);
        viewHolder = new ViewHolderChat(v);

        return viewHolder;
    }

    /**
     * binding view data
     *
     * @param viewHolder widget view
     * @param position   view holder position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        try {
            final ViewHolderChat vh2 = (ViewHolderChat) viewHolder;

            configureViewHolderChat(vh2, position);
            setItemClickListener(vh2, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * widget ItemClick Listener
     *
     * @param vh2      viewholder
     * @param position position of value
     */
    private void setItemClickListener(final ViewHolderChat vh2, final int position) {
        if (listener != null) {
            vh2.rlChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("===chat clicked");
                    listener.onItemClick(mDisplayedValues.get(position), vh2.rlChat, position, 0);
                }
            });

            vh2.rlChat.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    System.out.println("===chat long clicked");
                    listener.onItemLongClick(mDisplayedValues.get(position), vh2.itemView, position);
                    return false;
                }
            });

            vh2.storeImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("===image clicked");
                    listener.onItemClick(mDisplayedValues.get(position), vh2.storeImage, position, imageTS);
                }
            });

            vh2.storeImage.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClick(mDisplayedValues.get(position), vh2.itemView, position);
                    return false;
                }
            });
        }
    }


    /**
     * configure ViewHolder
     *
     * @param vh       specific view holder
     * @param position set position of value
     */
    @SuppressLint("NewApi")
    private void configureViewHolderChat(ViewHolderChat vh, int position) {
        final MessageItemChat chat = mDisplayedValues.get(position);

        vh.ivMsgType.setVisibility(View.GONE);

        if (chat.isSecretChat())
            vh.ivChatIcon.setVisibility(View.VISIBLE);
        else
            vh.ivChatIcon.setVisibility(View.GONE);

        if (chat.getMessageType() != null) {
            vh.newMessage.setVisibility(View.VISIBLE);
            vh.tvTyping.setVisibility(View.GONE);
            if (!chat.getMessageType().equals("" + MessageFactory.text)) {
                vh.ivMsgType.setVisibility(View.VISIBLE);
            }

            if (chat.getTypingAt() != 0) {
                vh.newMessage.setVisibility(View.GONE);
                vh.ivMsgType.setVisibility(View.GONE);
                vh.tvTyping.setVisibility(View.VISIBLE);
                if (chat.getMessageId().contains("-g-")) {
                    vh.tvTyping.setText(chat.getTypePerson().concat(" typing..."));
                } else {
                    vh.tvTyping.setText("typing...");
                }
            } else if (chat.getRecordingAt() != 0) {
                vh.newMessage.setVisibility(View.GONE);
                vh.ivMsgType.setVisibility(View.GONE);
                vh.tvTyping.setVisibility(View.VISIBLE);
                if (chat.getMessageId().contains("-g-")) {
                    vh.tvTyping.setText(chat.getTypePerson().concat(" recording..."));
                } else {
                    vh.tvTyping.setText("recording...");
                }
            }
            vh.storeImage.setVisibility(View.VISIBLE);
            vh.header_text.setVisibility(View.GONE);
            if (filteredText == null) {
                messageheaderCounter = 0;

            }


            if (chat.getMessageType().equals("" + MessageFactory.text)) {

                try {


                    vh.newMessage.setTextColor(ContextCompat.getColor(mContext, R.color.chatlist_messagecolor));
                    String text = chat.getTextMessage();
                    if (chat.isFilteredMessage()) {

                        if (messageheaderCounter == 0) {
                            vh.header_text.setVisibility(View.VISIBLE);
                            messageheaderCounter = 1;
                        }
                        vh.storeImage.setVisibility(View.GONE);
                        if (filteredText.length() > 0) {

                            if (filteredText.contains("+")) {

                                String htmlText = text.replaceAll("(?i)(" + filteredText.replaceFirst(Pattern.quote("+"), "011") + ")", "<b><font color='#000000'>$1</font></b>");
                                vh.newMessage.setText(Html.fromHtml(htmlText));
                            } else {
                                String htmlText = text.replaceAll("(?i)(" + filteredText + ")", "<b><font color='#000000'>$1</font></b>");
                                vh.newMessage.setText(Html.fromHtml(htmlText));
                            }

                        }


                    } else {
                        vh.storeImage.setVisibility(View.VISIBLE);
                        vh.newMessage.setText(text);
                    }

                    vh.ivMsgType.setVisibility(View.GONE);
                } catch (Exception e) {
                    e.printStackTrace();

                }
            } else if (chat.getMessageType().equals("" + MessageFactory.picture)) {
                vh.newMessage.setText("Image");
                vh.ivMsgType.setImageResource(R.drawable.camera_iconnn);
            } else if (chat.getMessageType().equals("" + MessageFactory.contact)) {
                vh.newMessage.setText("Contact");
                vh.ivMsgType.setImageResource(R.drawable.contact);
            } else if (chat.getMessageType().equals("" + MessageFactory.video)) {
                vh.newMessage.setText("Video");
                vh.ivMsgType.setImageResource(R.drawable.video);
            } else if (chat.getMessageType().equals("" + MessageFactory.audio)) {
                vh.newMessage.setText("Audio");
                vh.ivMsgType.setImageResource(R.drawable.audio);
            } else if (chat.getMessageType().equals("" + MessageFactory.document) || chat.getMessageType().equals("" + MessageFactory.group_document_message)) {
                vh.newMessage.setText("Document");
                vh.ivMsgType.setImageResource(R.drawable.document);
            } else if (chat.getMessageType().equals("" + MessageFactory.web_link)) {
                vh.newMessage.setText("Weblink");
                vh.ivMsgType.setImageResource(R.drawable.link);
            } else if (chat.getMessageType().equals("" + MessageFactory.location)) {
                vh.newMessage.setText("location");
                vh.ivMsgType.setImageResource(R.drawable.map);
            } else if (chat.getMessageType().equals("" + MessageFactory.missed_call)) {
                String callType = chat.getCallType();

//                String tsNextLine = TimeStampUtils.get12HrTimeFormat(mContext, chat.getTS());

                if (callType != null && callType.equals(MessageFactory.video_call + "")) {
                    vh.newMessage.setText("Missed video call");
                } else {
                    vh.newMessage.setText("Missed voice call");
                }
                vh.ivMsgType.setImageResource(R.drawable.ic_missed_call);
            } else if (chat.getMessageType().equals("" + MessageFactory.group_event_info)) {
                vh.ivMsgType.setVisibility(View.GONE);

                String createdBy = chat.getCreatedByUserId();
                String createdTo = chat.getCreatedToUserId();
                String groupName = chat.getGroupName();

                String createdByName = null, createdToName = null;
                String msg = null;


                switch (chat.getGroupEventType()) {

                    case "" + MessageFactory.join_new_group:
                        if (createdBy.equalsIgnoreCase(currentUserId)) {
                            createdByName = "You";
                        } else {
                            createdByName = getContactNameIfExists(createdBy);
                        }
                        msg = createdByName + " created group '" + groupName + "'";
                        break;

                    case "" + MessageFactory.add_group_member:
                        if (createdBy.equalsIgnoreCase(currentUserId)) {
                            createdByName = "You";
                        } else {
                            createdByName = getContactNameIfExists(createdBy);
                        }
                        if (createdTo.equalsIgnoreCase(currentUserId)) {
                            createdToName = "You";
                        } else {
                            createdToName = getContactNameIfExists(createdTo);
                        }
                        msg = createdByName + " added " + createdToName;
                        break;

                    case "" + MessageFactory.change_group_icon:
                        if (createdBy.equalsIgnoreCase(currentUserId)) {
                            createdByName = "You";
                        } else {
                            createdByName = getContactNameIfExists(createdBy);
                        }

                        msg = createdByName + " changed group's icon";
                        break;

                    case "" + MessageFactory.change_group_name:
                        if (createdBy.equalsIgnoreCase(currentUserId)) {
                            createdByName = "You";
                        } else {
                            createdByName = getContactNameIfExists(createdBy);
                        }

                        msg = createdByName + " changed group's name '" + chat.getPrevGroupName() + "' to '"
                                + chat.getGroupName() + "'";
                        break;

                    case "" + MessageFactory.delete_member_by_admin:
                        if (createdBy.equalsIgnoreCase(currentUserId)) {
                            createdByName = "You";
                        } else {
                            createdByName = getContactNameIfExists(createdBy);
                        }

                        if (createdTo.equalsIgnoreCase(currentUserId)) {
                            createdToName = "You";
                        } else {
                            createdToName = getContactNameIfExists(createdTo);
                        }
                        msg = createdByName + " removed " + createdToName;
//                        refresh();
                        break;

                    case "" + MessageFactory.make_admin_member:
                        if (createdTo.equalsIgnoreCase(currentUserId)) {
                            createdToName = "You are ";
                        } else {
                            createdToName = getContactNameIfExists(createdTo);
                        }
                        msg = createdToName + " now admin";
                        break;

                    case "" + MessageFactory.exit_group:
                        if (createdBy.equalsIgnoreCase(currentUserId)) {
                            createdByName = "You ";
                        } else {
                            createdByName = getContactNameIfExists(createdBy);
                        }
                        msg = createdByName + " left";
                        break;

                }

                if (msg != null) {
                    vh.newMessage.setText(msg);
                } else {
                    vh.newMessage.setText("");
                }

            } else if (chat.getMessageType().equals("" + MessageFactory.DELETE_SELF)) {
                vh.newMessage.setText(mContext.getResources().getString(R.string.you_deleted_text));
                vh.ivMsgType.setImageResource(R.drawable.icon_deleted);
            } else if (chat.getMessageType().equals("" + MessageFactory.DELETE_OTHER)) {

                vh.newMessage.setText(mContext.getResources().getString(R.string.other_deleted_text));
                vh.ivMsgType.setImageResource(R.drawable.icon_deleted);

            } else if (chat.getMessageType().equals("" + MessageFactory.SCREEN_SHOT_TAKEN)) {
                vh.newMessage.setTextColor(ContextCompat.getColor(mContext, R.color.chatlist_messagecolor));
                vh.newMessage.setText(chat.getTextMessage());
                vh.ivMsgType.setImageResource(0);
            } else {
                vh.newMessage.setText("");
                vh.ivMsgType.setImageResource(0);
            }
        } else {
            vh.newMessage.setText("");
        }
        try {
            if (filteredText != null) {
                if (!chat.isFilteredMessage()) {
                    String htmlText = chat.getSenderName().replaceAll("(?i)(" + filteredText + ")", "<b><font color='#000000'>$1</font></b>");

                    vh.storeName.setText(Html.fromHtml(htmlText));
                } else {
                    if (chat.getGroupName() == null) {
                        vh.storeName.setText(chat.getSenderName());
                    } else {
                        vh.storeName.setText(chat.getGroupName().equalsIgnoreCase("null") ? chat.getSenderName() : chat.getGroupName());
                    }
                }
            } else {
                if (chat.isGroup()) {
                    vh.storeName.setText(chat.getGroupName().equalsIgnoreCase("null") ? chat.getSenderName() : chat.getGroupName());
                } else {
                    vh.storeName.setText(chat.getSenderName());
                }


            }

            if (chat.isSecretChat()) {
                vh.storeName.setTextColor(ContextCompat.getColor(mContext, R.color.secret_chat_list_color));
            } else {
                vh.storeName.setTextColor(ContextCompat.getColor(mContext, R.color.chat_list_header));
            }


            configureDateLabel(vh.newMessageDate, position);
        } catch (Exception e) {
            e.printStackTrace();
        }

        vh.newMessageCount.setVisibility(View.GONE);
        String[] arrDocId = chat.getMessageId().split("-");
        String toUserId = arrDocId[1];
        String docId = currentUserId.concat("-").concat(toUserId);
        MuteStatusPojo muteData = null;
        String convId = null;

        String groupId = currentUserId.concat("-").concat(toUserId.replace("-0", "-g") + "-g");
        Log.d("AG", "loadFromDB: adapter1 " + groupId);
        Log.d("AG", "loadFromDB: adapter2 " + chat.getMessageId());

        if (db.isGroupId(groupId)) {
            // if (chat.getMessageId().contains("-g-")) {
            docId = docId.concat("-g");
            convId = toUserId;
            muteData = contactDB_sqlite.getMuteStatus(currentUserId, null, arrDocId[1], false);

            String path = chat.getAvatarImageUrl();
            if (path != null && !path.equals("")) {
                if (!path.contains(Constants.SOCKET_IP)) {
                    path = Constants.SOCKET_IP + path;
                }
                //RequestOptions options=new RequestOptions().error(R.mipmap.group_chat_attachment_profile_icon);
                Glide.with(mContext).load(path)
                        .into(vh.storeImage);
            } else {
                Glide.with(mContext).load(R.mipmap.group_chat_attachment_profile_icon)
                        .into(vh.storeImage);
            }
        } else {

            convId = userInfoSession.getChatConvId(docId);
            muteData = contactDB_sqlite.getMuteStatus(currentUserId, toUserId, convId, false);
//            getcontactname.configProfilepic(vh.storeImage, toUserId, false, true, R.mipmap.chat_attachment_profile_default_image_frame);
            //TODO tharani map
            Glide.with(mContext)
                    .load(getcontactname.getAvatarUrl(toUserId))
                    .into(vh.storeImage);

        }

        if (muteData != null && muteData.getMuteStatus().equals("1")) {
            ChatappContactModel contact = contactDB_sqlite.getUserOpponenetDetails(currentUserId);
            selectedContactsList = new ArrayList<>();
            selectedContactsList.clear();
            selectedContactsList.add(contact);
            if (!chat.isSecretChat())
                vh.mute_chatlist.setVisibility(View.VISIBLE);

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String dateString = formatter.format(new Date(muteData.getTs()));
            Log.e("Hours", dateString);


            Date date = null;

            if (muteData.getDuration().equalsIgnoreCase("8 Hours")) {
                try {
                    date = formatter.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.HOUR, 8);
                EXTime = formatter.format(calendar.getTime());
                Log.e("Hours1", formatter.format(calendar.getTime()));
            } else if (muteData.getDuration().equalsIgnoreCase("1 Week")) {
                try {
                    date = formatter.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.HOUR, 168);
                EXTime = formatter.format(calendar.getTime());
                Log.e("Hours1", calendar.getTime().toString());
            } else if (muteData.getDuration().equalsIgnoreCase("1 Year")) {
                try {
                    date = formatter.parse(dateString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.HOUR, 8064);
                EXTime = formatter.format(calendar.getTime());
                Log.e("Hours1", calendar.getTime().toString());
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String currentDateandTime = sdf.format(new Date());
            Log.e("Hours2", currentDateandTime);

            String[] splited = currentDateandTime.split(" ");
            String[] splited1 = EXTime.split(" ");

            String[] arrIds = chat.getMessageId().split("-");
            String receiverId = arrIds[1];

            if (splited[0].equalsIgnoreCase(splited1[0])) {

                String currentTime = splited[1].toString();
                String targetTime = splited1[1].toString();
                if (currentTime.compareTo(targetTime) >= 0) {
                    for (int i = 0; i < selectedContactsList.size(); i++) {
                        if (selectedContactsList.get(i).isGroup()) {
                            MuteUnmute.performUnMute(mContext, EventBus.getDefault(), receiverId, MessageFactory.CHAT_TYPE_GROUP,
                                    "no");
                        } else {
                            MuteUnmute.performUnMute(mContext, EventBus.getDefault(), receiverId, MessageFactory.CHAT_TYPE_SINGLE,
                                    "no");
                        }
                    }
                    vh.mute_chatlist.setVisibility(View.GONE);
                } else {
                    Log.e("TIMEDATA", "currentTime ->" + currentTime + "targetTime ->" + targetTime);
                }
            }

        } else {
            vh.mute_chatlist.setVisibility(View.GONE);
        }


        if (convId != null && !convId.equals("")) {
            int countMsg = shortcutBadgeManager.getSingleBadgeCount(convId);
//            int countMsg=0;
           /* if(convId.contains("-g")) {
                countMsg = sessionManager.getGroupChatCount(convId); // single chat count

            }else {
                countMsg = sessionManager.getSingleChatCount(convId); // single chat count
            }*/

            if (countMsg > 0 || !session.getmark(toUserId)) {
                if (!chat.isSecretChat())
                    vh.newMessageCount.setVisibility(View.VISIBLE);
                if (countMsg > 0) {
                    vh.newMessageCount.setText("" + countMsg);
                } else {
                    vh.newMessageCount.setText("");
                }
            } else {
                vh.newMessageCount.setVisibility(View.GONE);
            }
        } else {
            vh.newMessageCount.setVisibility(View.GONE);
        }

        if (chat.isSelected()) {
            vh.tick.setVisibility(View.VISIBLE);
        } else {
            vh.tick.setVisibility(View.GONE);
        }


    }


    /**
     * configure DateLabel
     *
     * @param tvDateLbl label value
     * @param position  set position of value
     */
    private void configureDateLabel(TextView tvDateLbl, int position) {

        MessageItemChat item = mDisplayedValues.get(position);
        if (item.getTS() != null && !item.getTS().equals("")) {
            String currentItemTS = item.getTS();

            if (currentItemTS.equals("0")) {
                tvDateLbl.setText("");
            } else {

                Date currentItemDate = TimeStampUtils.getMessageTStoDate(mContext, currentItemTS);
                if (currentItemDate != null) {
                    String mydate = TimeStampUtils.get12HrTimeFormat(mContext, item.getTS());
                    mydate = mydate.replace(".", "");
                    setDateText(tvDateLbl, currentItemDate, currentItemTS, mydate);
                } else {
                    tvDateLbl.setText("");
                }
            }
        } else {
            tvDateLbl.setText("");
        }
    }


    /**
     * set Date in TextView
     *
     * @param tvDateLbl       label
     * @param currentItemDate date value
     * @param ts
     * @param time            set time
     */
    private void setDateText(TextView tvDateLbl, Date currentItemDate, String ts, String time) {
        Date today = TimeStampUtils.getDateFormat(Calendar.getInstance().getTimeInMillis());
        Date yesterday = TimeStampUtils.getYesterdayDate(today);

        if (currentItemDate.equals(today)) {
            tvDateLbl.setText(time);
        } else if (currentItemDate.equals(yesterday)) {
            tvDateLbl.setText("Yesterday");
        } else {
            DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            String formatDate = df.format(currentItemDate);
            tvDateLbl.setText(formatDate);
        }
    }


    /**
     * Filter the user list
     *
     * @return filter value
     */
    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                try {
                    mDisplayedValues = (ArrayList<MessageItemChat>) results.values; // has the filtered values
                    if (mDisplayedValues.size() == 0) {
                        EventBus.getDefault().post(new MessageData("nodatafound"));
                    } else {
                        EventBus.getDefault().post(new MessageData("datafound"));
                    }


                    notifyDataSetChanged();  // notifies the data with new filtered values
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                try {

                    ArrayList<MessageItemChat> FilteredArrList = new ArrayList<>();

                    if (mListData == null) {
                        mListData = new ArrayList<>(mDisplayedValues); // saves the original data in mOriginalValues
                    }

                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mListData.size();
                        results.values = mListData;
                        filteredText = null;

                    } else {
                        filteredText = constraint.toString();
                        constraint = constraint.toString().toLowerCase();

                        for (int i = 0; i < mListData.size(); i++) {

                            String senderName = mListData.get(i).getSenderName();
                            if (senderName.toLowerCase().contains(constraint)) {
                                FilteredArrList.add(mListData.get(i));
                            }
                        }

                        MessageDbController db = CoreController.getDBInstance(mContext);
                        List<MessageItemChat> messages = db.getSearchMessages(constraint);
                        FilteredArrList.addAll(messages);

                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return results;
            }
        };
        return filter;
    }


    /**
     * Chat List ItemClick Listener
     *
     * @param listener Listener
     */
    public void setChatListItemClickListener(ChatListItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * get ContactName if create anyone
     *
     * @param userId value of userid
     * @return value
     */
    private String getContactNameIfExists(String userId) {
        String userName = null;
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
        ChatappContactModel contact = contactDB_sqlite.getUserOpponenetDetails(userId);
//        ChatappContactModel contact = CoreController.getContactsDbInstance(mContext).getUserDetails(userId);
        if (contact != null) {
            userName = contact.getFirstName();
//            userName = getcontactname.getSendername(userId, contact.getMsisdn());
//
//            if (contact.getMsisdn() == null || contact.getMsisdn().equalsIgnoreCase("null")) {
//                userName = contact.getFirstName();
//            }
        } else {
            if (callback != null) {
                callback.getUserDetails(userId);
            }
        }
        return userName;
    }

    /**
     * get Section Name
     *
     * @param position position of value
     * @return value
     */
    @Override
    public String getSectionName(int position) {
        return mListData.get(position).getSenderName().substring(0, 1);
    }

    /**
     * setCallback
     *
     * @param groupChatList get GroupChatList data from model class
     */
    public void setCallback(GroupChatList groupChatList) {
        callback = groupChatList;
    }

    /**
     * ChatListItemClickListener interface (onItemClick, onItemLongClick)
     */
    public interface ChatListItemClickListener {
        void onItemClick(MessageItemChat messageItemChat, View view, int position, long imageTS);

        void onItemLongClick(MessageItemChat messageItemChat, View view, int position);

    }

    /**
     * Reloading
     */
    public void refresh() {
        try {
            Intent intent = ((HomeScreen) mContext).getIntent();
            ((HomeScreen) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            ((HomeScreen) mContext).finish();
            ((HomeScreen) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            ((HomeScreen) mContext).startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}




