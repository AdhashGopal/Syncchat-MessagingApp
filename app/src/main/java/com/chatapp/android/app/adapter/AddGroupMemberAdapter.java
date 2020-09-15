package com.chatapp.android.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.model.ChatappContactModel;

import java.util.List;


public class AddGroupMemberAdapter extends RecyclerView.Adapter<AddGroupMemberAdapter.ViewHolderAddGroupMember> {

    private Context mContext;
    private List<ChatappContactModel> contactList;
    private Session session;
    Getcontactname getcontactname;
    private int blockedContactColor, unblockedContactColor;
    ContactDB_Sqlite contactDB_sqlite;

    /**
     * AddGroupMember Adapter constructor
     *
     * @param mContext    The activity object inherits the Context object
     * @param contactList list of value
     */
    public AddGroupMemberAdapter(Context mContext, List<ChatappContactModel> contactList) {
        this.mContext = mContext;
        this.contactList = contactList;
        this.session = new Session(mContext);
        getcontactname = new Getcontactname(mContext);

        blockedContactColor = ContextCompat.getColor(mContext, R.color.blocked_user_bg);
        unblockedContactColor = ContextCompat.getColor(mContext, R.color.white);

        contactDB_sqlite = CoreController.getContactSqliteDBintstance(mContext);
    }


    /**
     *  binding the layout view
     * @param parent layout view group
     * @param viewType
     * @return view holder
     */
    @Override
    public ViewHolderAddGroupMember onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_group_info, parent, false);
        ViewHolderAddGroupMember holder = new ViewHolderAddGroupMember(itemView);
        return holder;
    }

    /**
     * BindView & set the data's
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolderAddGroupMember holder, int position) {

        ChatappContactModel contact = contactList.get(position);

        holder.tvName.setText(contact.getFirstName());
//        holder.tvStatus.setText(contact.getStatus());
        getcontactname.setProfileStatusText(holder.tvStatus, contact.get_id(), contact.getStatus(), false);

        String toID = contactList.get(position).get_id();
        getcontactname.configProfilepic(holder.ivUserDp, toID, false, false, R.mipmap.chat_attachment_profile_default_image_frame);

        if (contactDB_sqlite.getBlockedStatus(toID, false).equals("1")) {
            holder.itemView.setBackgroundColor(blockedContactColor);
        } else {
            holder.itemView.setBackgroundColor(unblockedContactColor);
        }
    }

    /**
     * get list Item Count
     * @return list size
     */
    @Override
    public int getItemCount() {
        return contactList.size();
    }

    /**
     * Binding view holder
     */
    public class ViewHolderAddGroupMember extends RecyclerView.ViewHolder {

        CircleImageView ivUserDp;
        AvnNextLTProRegTextView tvName, tvAdmin, tvStatus;

        public ViewHolderAddGroupMember(View itemView) {
            super(itemView);

            ivUserDp = (CircleImageView) itemView.findViewById(R.id.ivUserDp);
            tvName = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvName);
            tvAdmin = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvAdmin);
            tvStatus = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvStatus);
        }
    }

}
