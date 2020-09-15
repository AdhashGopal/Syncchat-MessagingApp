package com.chatapp.android.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.app.widget.CircleTransform;
import com.chatapp.android.core.model.CommonInGroupPojo;
import com.chatapp.android.core.service.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class CommonInGroupAdapter extends RecyclerView.Adapter<CommonInGroupAdapter.CommonInGroupHolder> {

    private Context mContext;
    private ArrayList<CommonInGroupPojo> dataList;

    /**
     * Create constructor
     *
     * @param mContext The activity object inherits the Context object
     * @param dataList list of value
     */
    public CommonInGroupAdapter(Context mContext, ArrayList<CommonInGroupPojo> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    /**
     * widgets view holder
     */
    public class CommonInGroupHolder extends RecyclerView.ViewHolder {

        public CircleImageView ivGroupDp;
        public AvnNextLTProRegTextView tvGroupName, tvGroupContacts;

        public CommonInGroupHolder(View itemView) {
            super(itemView);

            ivGroupDp = (CircleImageView) itemView.findViewById(R.id.ivGroupDp);
            tvGroupName = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvGroupName);
            tvGroupContacts = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvGroupContacts);
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
    public CommonInGroupHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_common_in_group,
                parent, false);
        CommonInGroupHolder holder = new CommonInGroupHolder(view);
        return holder;
    }

    /**
     * fetch the data
     *
     * @param position gettting specific value
     * @return value
     */
    public CommonInGroupPojo getItem(int position) {
        return dataList.get(position);
    }

    /**
     * binding view data
     *
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(CommonInGroupHolder holder, int position) {

        String avatarPath = dataList.get(position).getAvatarPath();
        if (avatarPath != null && !avatarPath.equals("")) {
            Picasso.with(mContext).load(Constants.SOCKET_IP.concat(avatarPath)).error(
                    R.mipmap.group_chat_attachment_profile_icon).transform(new CircleTransform()).into(holder.ivGroupDp);
        } else {
            holder.ivGroupDp.setImageResource(R.mipmap.group_chat_attachment_profile_icon);
        }
        holder.tvGroupName.setText(dataList.get(position).getGroupName());
        if (dataList.get(position).getGroupContactNames() != null) {
            holder.tvGroupContacts.setText(dataList.get(position).getGroupContactNames());
        } else {
            holder.tvGroupContacts.setText("");
        }
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
