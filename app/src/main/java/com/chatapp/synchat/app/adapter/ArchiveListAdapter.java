package com.chatapp.synchat.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.TimeStampUtils;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.app.viewholder.ViewHolderArchive;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.MuteStatusPojo;
import com.chatapp.synchat.core.service.Constants;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * created by  Adhash Team on 1/10/2017.
 */
public class ArchiveListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    ArrayList<MessageItemChat> myArchiveListInfo;
    private Context mContext;
    private String currentUserId;
    Session session;
    private Getcontactname getcontactname;

    private UserInfoSession userInfoSession;

    public ArchiveListAdapter(Context mContext, ArrayList<MessageItemChat> aArchiveListInfo) {
        this.myArchiveListInfo = aArchiveListInfo;
        this.mContext = mContext;
        session = new Session(mContext);
        getcontactname = new Getcontactname(mContext);
        userInfoSession = new UserInfoSession(mContext);

        currentUserId = SessionManager.getInstance(mContext).getCurrentUserID();
    }


    public MessageItemChat getItem(int position) {
        return myArchiveListInfo.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup aViewGroup, int viewType) {

        RecyclerView.ViewHolder aViewHolder;
        LayoutInflater aInflater = LayoutInflater.from(aViewGroup.getContext());
        View v = aInflater.inflate(R.layout.archivelistrow, aViewGroup, false);
        aViewHolder = new ViewHolderArchive(v);
        return aViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder aHolder, int position) {

        ViewHolderArchive vh2 = (ViewHolderArchive) aHolder;
        configureViewHolderChat(vh2, position);
    }

    private void configureViewHolderChat(ViewHolderArchive aArchiveHolder, int position) {
        MessageItemChat aArchiveInfo = myArchiveListInfo.get(position);
        aArchiveHolder.storeName.setText(aArchiveInfo.getSenderName());
        aArchiveHolder.newMessage.setText(aArchiveInfo.getTextMessage());
        aArchiveHolder.newMessageCount.setVisibility(View.GONE);
        String[] arrDocId = aArchiveInfo.getMessageId().split("-");

        try {
            configureDateLabel(aArchiveHolder.newMessageDate, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String toUserId;
        if (currentUserId.equalsIgnoreCase(arrDocId[0])) {
            toUserId = arrDocId[1];
        } else {
            toUserId = arrDocId[0];
        }

        if (aArchiveInfo.getMessageId().contains("-g")) {
            if (aArchiveInfo.getAvatarImageUrl() != null && !aArchiveInfo.getAvatarImageUrl().isEmpty()) {
                Picasso.with(mContext).load(Constants.SOCKET_IP + aArchiveInfo.getAvatarImageUrl()).error(
                        R.mipmap.chat_attachment_profile_default_image_frame)
//                        .transform(new CircleTransform())
                        .into(aArchiveHolder.storeImage);
            } else {
                Picasso.with(mContext).load(R.mipmap.chat_attachment_profile_default_image_frame).into(aArchiveHolder.storeImage);
            }
        } else {
            getcontactname.configProfilepic(aArchiveHolder.storeImage, toUserId, false, false, R.mipmap.chat_attachment_profile_default_image_frame);
        }

        MuteStatusPojo muteData = null;
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);

        if (aArchiveInfo.getMessageId().contains("-g-")) {
            muteData = contactDB_sqlite.getMuteStatus(currentUserId, null, toUserId, false);
        } else {
            String docId = currentUserId + "-" + toUserId;
            String convId = userInfoSession.getChatConvId(docId);
            muteData = contactDB_sqlite.getMuteStatus(currentUserId, toUserId, convId, false);
//            muteData = contactsDB.getMuteStatus(currentUserId, toUserId, convId, false);
        }

        if (muteData != null && muteData.getMuteStatus().equals("1")) {
            aArchiveHolder.mute_chatlist.setVisibility(View.VISIBLE);
        } else {
            aArchiveHolder.mute_chatlist.setVisibility(View.GONE);
        }
        if (session.getmark(toUserId)) {
            aArchiveHolder.newMessageCount.setVisibility(View.GONE);
            // vh.tick.setVisibility(View.GONE);

        } else {
            aArchiveHolder.newMessageCount.setVisibility(View.VISIBLE);
            //vh.tick.setVisibility(View.VISIBLE);
        }
        if (aArchiveInfo.isSelected()) {
            aArchiveHolder.tick.setVisibility(View.VISIBLE);
        } else {
            aArchiveHolder.tick.setVisibility(View.GONE);
        }
    }

    private void configureDateLabel(TextView tvDateLbl, int position) {

        MessageItemChat item = myArchiveListInfo.get(position);
        if (item.getTS() == null || item.getTS().equals("") || item.getTS().equals("0")) {
            tvDateLbl.setText("");
        } else {
            String ts = TimeStampUtils.getServerTimeStamp(mContext, Long.parseLong(item.getTS()));
            Date currentItemDate = TimeStampUtils.getDateFormat(Long.parseLong(ts));
            if (currentItemDate != null) {
                setDateText(tvDateLbl, currentItemDate, ts);
            }
        }
    }

    private void setDateText(TextView tvDateLbl, Date currentItemDate, String ts) {
        Calendar calendar = Calendar.getInstance();
        Date today = TimeStampUtils.getDateFormat(calendar.getTimeInMillis());
        Date yesterday = TimeStampUtils.getYesterdayDate(today);

        if (currentItemDate.equals(today)) {
            String formatTime = TimeStampUtils.get12HrTimeFormat(mContext, ts);
            tvDateLbl.setText(formatTime);
        } else if (currentItemDate.equals(yesterday)) {
            tvDateLbl.setText("Yesterday");
        } else {
            DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
            String formatDate = df.format(currentItemDate);
            tvDateLbl.setText(formatDate);
        }
    }


    @Override
    public int getItemCount() {
        return myArchiveListInfo.size();
    }
}
