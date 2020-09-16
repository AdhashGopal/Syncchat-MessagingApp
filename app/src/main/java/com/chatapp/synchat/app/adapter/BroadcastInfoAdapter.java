package com.chatapp.synchat.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.app.widget.CircleImageView;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.model.ChatappContactModel;

import java.util.List;
import java.util.regex.Pattern;

/**
 * created by  Adhash Team on 11/29/2016.
 */

public class BroadcastInfoAdapter extends RecyclerView.Adapter<BroadcastInfoAdapter.ViewHolderGroupInfo> {

    List<ChatappContactModel> chatappContactModels;
    Session session;
    private Context mContext;
    SessionManager sessionmanager;
    Getcontactname getcontactname;
    private final Pattern sPattern = Pattern.compile("^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$");
    private static final String TAG = BroadcastInfoAdapter.class.getSimpleName();

    /**
     * Create constructor
     *
     * @param mContext             The activity object inherits the Context object
     * @param chatappContactModels list of value
     */
    public BroadcastInfoAdapter(Context mContext, List<ChatappContactModel> chatappContactModels) {
        this.chatappContactModels = chatappContactModels;
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

        return chatappContactModels.size();
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

        holder.tvName.setText(chatappContactModels.get(position).getFirstName());

        holder.tvStatus.setText(chatappContactModels.get(position).getStatus());

        holder.ivUserDp.setImageResource(R.drawable.personprofile);


//        String memmberid = member.getUserId();
//        Log.d(TAG, "onBindViewHolder:  "+position+": "+member.getName());
//        if(member.getContactName()!=null && !member.getContactName().trim().isEmpty())
//            holder.tvName.setText(member.getContactName());
//        else
//            holder.tvName.setText(member.getName());
//
//        holder.tvAdmin.setVisibility(View.GONE);
//
//        if (member.getIsAdminUser().equalsIgnoreCase("1")) {
//            holder.tvAdmin.setVisibility(View.VISIBLE);
//        }
//
//        String status = membersList.get(position).getStatus();
//
//        if (sessionmanager.getCurrentUserID().equalsIgnoreCase(memmberid)) {
//            holder.tvStatus.setText(sessionmanager.getcurrentUserstatus());
//
//            String userprofilepic = sessionmanager.getUserProfilePic();
//            Picasso.with(mContext).load(Constants.SOCKET_IP + userprofilepic).error(
//                    R.drawable.personprofile)
//                    .transform(new CircleTransform()).into(holder.ivUserDp);
//        } else {
//            getcontactname.setProfileStatusText(holder.tvStatus, memmberid, status, false);
//            getcontactname.configProfilepic(holder.ivUserDp, memmberid, false, false, R.mipmap.chat_attachment_profile_default_image_frame);
//        }
    }


    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
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

