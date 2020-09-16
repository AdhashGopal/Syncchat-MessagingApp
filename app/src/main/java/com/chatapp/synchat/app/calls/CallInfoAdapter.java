package com.chatapp.synchat.app.calls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.utils.TimeStampUtils;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.CallItemChat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * created by  Adhash Team on 7/31/2017.
 */
public class CallInfoAdapter extends RecyclerView.Adapter<CallInfoAdapter.VHCallInfo> {

    private Context mContext;
    private ArrayList<CallItemChat> callItemList;

    private Date todayDate, yesterdayDate;
    private String strCallType;

    /**
     * Create constructor
     *
     * @param context      The activity object inherits the Context object
     * @param callType     call type
     * @param callItemList arraylist of value
     */
    public CallInfoAdapter(Context context, int callType, ArrayList<CallItemChat> callItemList) {
        this.mContext = context;
        this.callItemList = callItemList;

        todayDate = TimeStampUtils.getDateFormat(Calendar.getInstance().getTimeInMillis());
        yesterdayDate = TimeStampUtils.getYesterdayDate(todayDate);

        if (callType == MessageFactory.audio_call) {
            strCallType = "voice";
        } else {
            strCallType = "video";
        }
    }

    /**
     * layout binding
     *
     * @param parent   layout view group
     * @param viewType
     * @return view holder
     */
    @Override
    public VHCallInfo onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_call_info, parent, false);
        return new VHCallInfo(view);
    }

    /**
     * binding view data
     * based on call status show the values
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(VHCallInfo holder, int position) {
        if (position == 0) {
            holder.tvDate.setVisibility(View.VISIBLE);
            holder.divider.setVisibility(View.VISIBLE);
        } else {
            holder.tvDate.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
        }

        CallItemChat callItem = callItemList.get(position);
        if (!callItem.isSelf()) {
            String callStatus = callItem.getCallStatus();

            if (callStatus.equals(MessageFactory.CALL_STATUS_REJECTED + "")
                    || callStatus.equals(MessageFactory.CALL_STATUS_MISSED + "")) {
                holder.ivCallType.setBackgroundResource(R.drawable.ic_in_call_missed);
                String text = "Missed " + strCallType + " call...";
                holder.tvCallType.setText(text);
                holder.tvCallStatus.setVisibility(View.INVISIBLE);
            } else {
                holder.ivCallType.setBackgroundResource(R.drawable.ic_in_call_received);
                String text = "Incoming " + strCallType + " call";
                holder.tvCallType.setText(text);
                holder.tvCallStatus.setVisibility(View.VISIBLE);
                holder.tvCallStatus.setText(callItem.getCallDuration());
            }
        } else {
            holder.ivCallType.setBackgroundResource(R.drawable.ic_out_call);
            String text = "Outgoing " + strCallType + " call...";
            holder.tvCallType.setText(text);
            holder.tvCallStatus.setVisibility(View.VISIBLE);
            holder.tvCallStatus.setText(callItem.getCallDuration());
        }

        Date currentItemDate = TimeStampUtils.getDateFormat(Long.parseLong(callItem.getTS()));
        String time = TimeStampUtils.get12HrTimeFormat(mContext, callItem.getTS());
        holder.tvTime.setText(time);

        if (currentItemDate != null) {
            if (currentItemDate.equals(todayDate)) {
                holder.tvDate.setText("Today");
            } else if (currentItemDate.equals(yesterdayDate)) {
                holder.tvDate.setText("Yesterday");
            } else {
                DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                String formatDate = df.format(currentItemDate);
                holder.tvDate.setText(formatDate);
            }
        } else {
            holder.tvDate.setText("");
        }

    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return callItemList.size();
    }

    /**
     * widgets view holder
     */
    public class VHCallInfo extends RecyclerView.ViewHolder {

        private TextView tvDate, tvCallType, tvCallStatus, tvTime;
        private ImageView ivCallType;
        private View divider;

        public VHCallInfo(View itemView) {
            super(itemView);

            tvDate = (TextView) itemView.findViewById(R.id.tvDate);
            tvCallType = (TextView) itemView.findViewById(R.id.tvCallType);
            tvCallStatus = (TextView) itemView.findViewById(R.id.tvCallStatus);
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);

            ivCallType = (ImageView) itemView.findViewById(R.id.ivCallType);

            divider = itemView.findViewById(R.id.divider);
        }
    }

}
