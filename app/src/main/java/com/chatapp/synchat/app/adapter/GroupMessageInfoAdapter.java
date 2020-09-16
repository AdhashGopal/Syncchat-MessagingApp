package com.chatapp.synchat.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.utils.TimeStampUtils;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.app.widget.CircleImageView;
import com.chatapp.synchat.core.model.GroupMessageInfoPojo;
import com.chatapp.synchat.core.chatapphelperclass.ChatappUtilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * created by  Adhash Team on 1/30/2017.
 */
public class GroupMessageInfoAdapter extends RecyclerView.Adapter<GroupMessageInfoAdapter.ViewHolderGroupInfo> {

    private Context mContext;
    private List<GroupMessageInfoPojo> infoList;
    private int readPendingCount;
    private long serverTimeDiff;
    private Getcontactname getcontactname;
    private boolean isDeliveryList;

    public GroupMessageInfoAdapter(Context mContext, List<GroupMessageInfoPojo> infoList, int readPendingCount,boolean isDeliveryList) {
        this.mContext = mContext;
        this.infoList = infoList;
        this.readPendingCount = readPendingCount;
        this.isDeliveryList=isDeliveryList;
        serverTimeDiff = SessionManager.getInstance(mContext).getServerTimeDifference();
        getcontactname = new Getcontactname(mContext);
    }


    @Override
    public int getItemCount() {
        return infoList.size();
    }

    @Override
    public ViewHolderGroupInfo onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_group_message_info, parent, false);
        return new ViewHolderGroupInfo(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolderGroupInfo holder, int position) {
        GroupMessageInfoPojo userInfo = infoList.get(position);

        holder.tvMsisdn.setText(userInfo.getReceiverMsisdn());
        holder.tvName.setText(userInfo.getReceiverName());

        getcontactname.configProfilepic(holder.ivUserDp, userInfo.getReceiverId(), false, false, R.mipmap.chat_attachment_profile_default_image_frame);

        if (userInfo.getDeliverTS().equals("")) {
            holder.tvDeliverTS.setText("-NA-");

        } else {

//            String deltime = TimeStampUtils.getServerTimeStamp(mContext, Long.parseLong(userInfo.getDeliverTS()));
            String deliverTime = getFormattedTime(userInfo.getDeliverTS());
            holder.tvDeliverTS.setText(deliverTime);
        }

        if (userInfo.getReadTS().equals("")) {
            holder.tvReadTS.setText("-NA-");
        } else {
//            String readTS = TimeStampUtils.getServerTimeStamp(mContext, Long.parseLong(userInfo.getReadTS()));
            String readTime = getFormattedTime(userInfo.getReadTS());
            holder.tvReadTS.setText(readTime);
        }

        if(readPendingCount > 0 && position == infoList.size() - 1) {
            holder.tvReadPending.setVisibility(View.GONE);
            String pendingText = readPendingCount + " remaining";
            holder.tvReadPending.setText(pendingText);
        } else {
            holder.tvReadPending.setVisibility(View.GONE);
        }

    }

    private String getFormattedTime(String ts) {
        try {
            Calendar calendar = Calendar.getInstance();
            Date today = TimeStampUtils.getDateFormat(calendar.getTimeInMillis());
            long currentItemTS = Long.parseLong(ts) + serverTimeDiff;
            Date currentItemDate = TimeStampUtils.getDateFormat(currentItemTS);
            Date currentItemDateTime = new Date(currentItemTS);

            String time = ChatappUtilities.convert24to12hourformat(currentItemDateTime.toString().substring(11, 19));
            if (!currentItemDate.equals(today)) {
                DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                String formatDate = df.format(currentItemDate);
                time = formatDate + " " + time;
            }
            return time;
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "";
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return "";
        }

    }

    public class ViewHolderGroupInfo extends RecyclerView.ViewHolder {
        CircleImageView ivUserDp;
        AvnNextLTProRegTextView tvMsisdn, tvName, tvReadTS, tvDeliverTS, tvReadPending;

        public ViewHolderGroupInfo(View itemView) {
            super(itemView);

            ivUserDp = (CircleImageView) itemView.findViewById(R.id.ivUserDp);
            tvMsisdn = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvMsisdn);
            tvName = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvName);
            tvReadTS = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvReadTS);
            tvDeliverTS = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvDeliverTS);
            tvReadPending = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvReadPending);
        }
    }
}
