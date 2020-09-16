package com.chatapp.synchat.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.MessageData;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.app.widget.CircleImageView;
import com.chatapp.synchat.app.widget.CircleTransform;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.model.GroupMembersPojo;
import com.chatapp.synchat.core.service.Constants;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * created by  Adhash Team on 11/29/2016.
 */

public class GroupInfoAdapter extends RecyclerView.Adapter<GroupInfoAdapter.ViewHolderGroupInfo> {
    private String filteredText;
    private List<GroupMembersPojo> membersList;
    private ArrayList<GroupMembersPojo> mListData;
    Session session;
    private Context mContext;
    SessionManager sessionmanager;
    Getcontactname getcontactname;
    private final Pattern sPattern = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$");
    private static final String TAG = GroupInfoAdapter.class.getSimpleName();

    /**
     * Create constructor
     *
     * @param mContext The activity object inherits the Context object
     * @param list     list of value will be shown
     */
    public GroupInfoAdapter(Context mContext, List<GroupMembersPojo> list) {
        this.membersList = list;
        this.mContext = mContext;
        session = new Session(mContext);
        sessionmanager = SessionManager.getInstance(mContext);
        getcontactname = new Getcontactname(mContext);
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        if (membersList == null)
            return 0;
        return membersList.size();
    }

    /**
     * layout binding
     *
     * @param parent   layout view group
     * @param viewType
     * @return view holder
     */
    @Override
    public ViewHolderGroupInfo onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_group_info, parent, false);
        return new ViewHolderGroupInfo(itemView);
    }

    /**
     * binding view data
     *
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(ViewHolderGroupInfo holder, int position) {

        GroupMembersPojo member = membersList.get(position);
        String memmberid = member.getUserId();
        Log.d(TAG, "onBindViewHolder:  " + position + ": " + member.getName());
        if (member.getContactName() != null && !member.getContactName().trim().isEmpty())
            holder.tvName.setText(member.getContactName());
        else
            holder.tvName.setText(member.getName());

        holder.tvAdmin.setVisibility(View.GONE);

        if (member.getIsAdminUser().equalsIgnoreCase("1")) {
            holder.tvAdmin.setVisibility(View.VISIBLE);
        }

        String status = membersList.get(position).getStatus();

        if (sessionmanager.getCurrentUserID().equalsIgnoreCase(memmberid)) {
            holder.tvStatus.setText(sessionmanager.getcurrentUserstatus());

            String userprofilepic = sessionmanager.getUserProfilePic();
            Picasso.with(mContext).load(Constants.SOCKET_IP + userprofilepic).error(
                    R.drawable.personprofile)
                    .transform(new CircleTransform()).into(holder.ivUserDp);
        } else {
            getcontactname.setProfileStatusText(holder.tvStatus, memmberid, status, false);
            getcontactname.configProfilepic(holder.ivUserDp, memmberid, false, false, R.mipmap.chat_attachment_profile_default_image_frame);
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    /**
     * filter the arraylist value
     *
     * @return select specific value
     */
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                try {

                    ArrayList<GroupMembersPojo> FilteredArrList = new ArrayList<>();

                    if (mListData == null) {
                        mListData = new ArrayList<>(membersList); // saves the original data in mOriginalValues
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

                            String senderName = mListData.get(i).getName();
                            if (senderName.toLowerCase().contains(constraint)) {
                                FilteredArrList.add(mListData.get(i));
                                Log.e("Result-->", mListData.get(i) + "");
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

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                try {
                    membersList = (ArrayList<GroupMembersPojo>) results.values; // has the filtered values
                    if (membersList.size() == 0) {
                        EventBus.getDefault().post(new MessageData("nodatafound"));
                    } else {
                        EventBus.getDefault().post(new MessageData("datafound"));
                    }
                    notifyDataSetChanged();  // notifies the data with new filtered values
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        return filter;
    }


    /**
     * widgets view holder
     */
    public class ViewHolderGroupInfo extends RecyclerView.ViewHolder {
        CircleImageView ivUserDp;
        AvnNextLTProRegTextView tvName, tvAdmin, tvStatus;

        public ViewHolderGroupInfo(View itemView) {
            super(itemView);

            ivUserDp = (CircleImageView) itemView.findViewById(R.id.ivUserDp);
            tvName = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvName);
            tvAdmin = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvAdmin);
            tvStatus = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvStatus);
        }
    }
}

