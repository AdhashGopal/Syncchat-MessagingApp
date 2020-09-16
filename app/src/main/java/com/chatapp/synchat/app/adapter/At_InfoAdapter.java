package com.chatapp.synchat.app.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;


import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.app.widget.CircleImageView;
import com.chatapp.synchat.app.widget.CircleTransform;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.model.GroupMembersPojo;
import com.chatapp.synchat.core.service.Constants;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.regex.Pattern;


public class At_InfoAdapter extends RecyclerView.Adapter<At_InfoAdapter.ViewHolderGroupInfo> {

    private List<GroupMembersPojo> membersList;
    Session session;
    private Context mContext;
    SessionManager sessionmanager;
    Getcontactname getcontactname;
    private final Pattern sPattern = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$");
    private static final String TAG = "GroupInfoAdapter";
    private AtInfoAdapterItemClickListener listener;


    /**
     * Create constructor
     *
     * @param mContext The activity object inherits the Context object
     * @param list     list of value
     */
    public At_InfoAdapter(Context mContext, List<GroupMembersPojo> list) {
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
     * @param parent layout view group
     * @param viewType
     * @return view holder
     */
    @Override
    public ViewHolderGroupInfo onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.at_group_info, parent, false);
        return new ViewHolderGroupInfo(itemView);
    }


    /**
     * binding view data
     *
     * @param holder widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(ViewHolderGroupInfo holder, final int position) {

        final GroupMembersPojo member = membersList.get(position);
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
//            holder.tvStatus.setText(sessionmanager.getcurrentUserstatus());

            String userprofilepic = sessionmanager.getUserProfilePic();
            Picasso.with(mContext).load(Constants.SOCKET_IP + userprofilepic).error(
                    R.drawable.personprofile)
                    .transform(new CircleTransform()).into(holder.ivUserDp);
        } else {
            getcontactname.setProfileStatusText(holder.tvStatus, memmberid, status, false);
            getcontactname.configProfilepic(holder.ivUserDp, memmberid, false, false, R.mipmap.chat_attachment_profile_default_image_frame);
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClick(member, position);
            }
        });
    }

    /**
     * widgets view holder
     */
    public class ViewHolderGroupInfo extends RecyclerView.ViewHolder {
        CircleImageView ivUserDp;
        AvnNextLTProRegTextView tvName, tvAdmin, tvStatus;
        LinearLayout linearLayout;

        public ViewHolderGroupInfo(View itemView) {
            super(itemView);

            linearLayout = (LinearLayout) itemView.findViewById(R.id.linear);
            ivUserDp = (CircleImageView) itemView.findViewById(R.id.ivUserDp);
            tvName = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvName);
            tvAdmin = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvAdmin);
            tvStatus = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvStatus);
        }
    }

    /**
     * ItemClick interface
     */
    public interface AtInfoAdapterItemClickListener {
        void onItemClick(GroupMembersPojo member, int position);
    }

    /**
     * setChatListItemClickListener
     *
     * @param listener value
     */
    public void setChatListItemClickListener(At_InfoAdapter.AtInfoAdapterItemClickListener listener) {
        this.listener = listener;
    }

}

