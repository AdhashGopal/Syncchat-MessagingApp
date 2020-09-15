package com.chatapp.android.app.calls;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.utils.AppUtils;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.utils.RecyclerViewItemClickListener;
import com.chatapp.android.app.utils.TimeStampUtils;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.CallItemChat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * created by  Adhash Team on 7/28/2017.
 */
public class CallHistoryAdapter extends RecyclerView.Adapter<CallHistoryAdapter.VHCallHistory> implements Filterable {

    public List<CallItemChat> mDisplayedValues;
    public List<CallItemChat> mDisplayedValues_previous = new ArrayList<>();
    private Context mContext;
    private RecyclerViewItemClickListener listener;
    private List<CallItemChat> mOriginalValues;

    private Getcontactname getcontactname;

    /**
     * Create constructor
     *
     * @param context      The activity object inherits the Context object
     * @param callItemList arraylist of value
     */
    public CallHistoryAdapter(Context context, ArrayList<CallItemChat> callItemList) {
        this.mContext = context;
        this.mDisplayedValues = callItemList;
        this.mOriginalValues = callItemList;

        getcontactname = new Getcontactname(mContext);
    }

    /**
     * layout binding
     *
     * @param parent   layout view group
     * @param viewType
     * @return view holder
     */
    @Override
    public VHCallHistory onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_call_history, parent, false);
        VHCallHistory holder = new VHCallHistory(view);
        return holder;
    }

    /**
     * binding view data
     * based on call status show the values
     *
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(final VHCallHistory holder, int position) {
        CallItemChat callItem = mDisplayedValues.get(position);

        String toUserId = callItem.getOpponentUserId();
        System.out.println("=====call==toUserId" + toUserId);
        getcontactname.configProfilepic(holder.ivProfilePic, toUserId, false, false, R.mipmap.chat_attachment_profile_default_image_frame);

     /*   if(TextUtils.isEmpty(getcontactname.getSendername(toUserId, callItem.getOpponentUserMsisdn())))
        {
            holder.parent.setVisibility(View.GONE);
        }else {
            holder.parent.setVisibility(View.VISIBLE);
        }*/

        holder.tvName.setText(getcontactname.getSendername(toUserId, callItem.getOpponentUserMsisdn()));

        System.out.println("=====call==type" + callItem.getCallType());

        if (callItem.getCallType().equals(MessageFactory.video_call + "")) {
            holder.ibCall.setImageResource(R.drawable.lets_video);
            holder.ibCall.setTag(MessageFactory.video_call + "");
        } else {
            holder.ibCall.setImageResource(R.drawable.lets_call_icon);
            holder.ibCall.setTag(MessageFactory.audio_call + "");
        }
        holder.ibCall.setTag(R.string.data, callItem);
        holder.parent.setTag(R.string.data, callItem);
        if (!callItem.isSelf()) {
            String callStatus = callItem.getCallStatus();
            System.out.println("=====call==callStatus" + callStatus);
            if (callStatus.equals(MessageFactory.CALL_STATUS_REJECTED + "")
                    || callStatus.equals(MessageFactory.CALL_STATUS_MISSED + "")) {
                holder.ivCallType.setBackgroundResource(R.drawable.ic_in_call_missed);
            } else {
                holder.ivCallType.setBackgroundResource(R.drawable.ic_in_call_received);
            }
        } else {
            holder.ivCallType.setBackgroundResource(R.drawable.ic_out_call);
        }

        setDateText(callItem.getCallCount(), holder.tvTS, callItem.getTS());

//        holder.ibCall.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(listener != null) {
//                    listener.onRVItemClick(holder.ibCall, holder.getAdapterPosition());
//                }
//            }
//        });
        holder.call_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onRVItemClick(holder.ibCall, holder.getAdapterPosition());
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onRVItemClick(holder.itemView, holder.getAdapterPosition());
                }
            }
        });
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return mDisplayedValues.size();
    }

    public CallItemChat getItem(int position) {
        return mDisplayedValues.get(position);
    }

    /**
     * OnItemClick Listener event
     *
     * @param listener
     */
    public void setOnItemClickListener(RecyclerViewItemClickListener listener) {
        this.listener = listener;
    }


    /**
     * filter the arraylist data
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


                    mDisplayedValues = (ArrayList<CallItemChat>) results.values; // has the filtered values
                    if (mDisplayedValues.size() == 0) {
//                    Toast.makeText(context, "No Contacts Matching Your Query...", Toast.LENGTH_SHORT).show();
                    }

                    if (mDisplayedValues != mDisplayedValues_previous) {
                        notifyDataSetChanged();  // notifies the data with new filtered values
                        mDisplayedValues_previous = mDisplayedValues;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values

                try {
                    ArrayList<CallItemChat> FilteredArrList = new ArrayList<>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<>(mDisplayedValues); // saves the original data in mOriginalValues
                    }

                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {


                            String contactName = mOriginalValues.get(i).getCallerName();
                            String contactNo = mOriginalValues.get(i).getOpponentUserMsisdn();

                            if (contactName.toLowerCase().contains(constraint) || contactNo.toLowerCase().contains(constraint)) {
                                FilteredArrList.add(mOriginalValues.get(i));
                            }
                        }
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
     * set Date Textview
     *
     * @param callCount based on value to set call count
     * @param tvDateLbl date value
     * @param ts        timestamp
     */
    private void setDateText(int callCount, TextView tvDateLbl, String ts) {
        Date today = TimeStampUtils.getDateFormat(Calendar.getInstance().getTimeInMillis());
        Date yesterday = TimeStampUtils.getYesterdayDate(today);

        Date currentItemDate = TimeStampUtils.getDateFormat(AppUtils.parseLong(ts));
        String time = TimeStampUtils.get12HrTimeFormat(mContext, ts);

        if (currentItemDate != null) {
            if (currentItemDate.equals(today)) {
                String txt = "(" + callCount + ") Today " + time;
                tvDateLbl.setText(txt);
            } else if (currentItemDate.equals(yesterday)) {
                String txt = "(" + callCount + ") Yesterday " + time;
                tvDateLbl.setText(txt);
            } else {
                DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                String formatDate = df.format(currentItemDate);
                String txt = "(" + callCount + ") " + formatDate + " " + time;
                tvDateLbl.setText(txt);
            }
        } else {
            tvDateLbl.setText("");
        }
    }

    /**
     * widgets view holder
     */
    public class VHCallHistory extends RecyclerView.ViewHolder {

        private ImageView ivProfilePic, ivCallType;
        private TextView tvName, tvTS;
        private ImageView ibCall;
        private RelativeLayout parent;
        private RelativeLayout call_layout;

        public VHCallHistory(View itemView) {
            super(itemView);
            parent = (RelativeLayout) itemView.findViewById(R.id.rlParent);
            ivProfilePic = (ImageView) itemView.findViewById(R.id.ivProfilePic);
            ivCallType = (ImageView) itemView.findViewById(R.id.ivCallType);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            tvTS = (TextView) itemView.findViewById(R.id.tvTS);
            ibCall = (ImageView) itemView.findViewById(R.id.ibCall);
            call_layout = (RelativeLayout) itemView.findViewById(R.id.call_layout);
        }
    }

}
