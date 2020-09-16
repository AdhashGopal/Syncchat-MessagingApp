package com.chatapp.synchat.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.CircleImageView;
import com.chatapp.synchat.core.model.BlockListPojo;
import com.chatapp.synchat.core.service.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * created by  Adhash Team on 3/7/2017.
 */
public class BlockContactAdapter extends RecyclerView.Adapter<BlockContactAdapter.MyViewHolder> {

    Activity context;
    private ArrayList<BlockListPojo> blockListPojos = new ArrayList<>();


    /**
     * widgets view holder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AvnNextLTProDemiTextView uname, unumber;
        CircleImageView imageview;
        public ImageView ivSecretIcon;

        public MyViewHolder(View view) {
            super(view);
            uname = (AvnNextLTProDemiTextView) view.findViewById(R.id.BlockUserName);
            unumber = (AvnNextLTProDemiTextView) view.findViewById(R.id.blockUserNumber);
            imageview = (CircleImageView) view.findViewById(R.id.block_user_image);
            ivSecretIcon = (ImageView) view.findViewById(R.id.ivSecretIcon);
        }
    }

    /**
     * Create constructor
     *
     * @param context        The activity object inherits the Context object
     * @param blockListPojos list of value
     */
    public BlockContactAdapter(Activity context, ArrayList<BlockListPojo> blockListPojos) {
        super();
        this.context = context;
        this.blockListPojos = blockListPojos;

    }

    /**
     * layout binding
     *
     * @param parent   layout view group
     * @param viewType
     * @return view holder
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.block_contact_adapter, parent, false);
        return new MyViewHolder(itemView);
    }


    /**
     * binding view data
     *
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BlockListPojo blockListPojo = blockListPojos.get(position);
        holder.uname.setText(blockListPojo.getName());
//        holder.unumber.setText(blockListPojo.getNumber());
        String image = blockListPojo.getImagePath();

        if ((image != null && !image.equals(""))) {
            String path = Constants.SOCKET_IP.concat(image);
            Picasso.with(context).load(path).fit().error(
                    R.mipmap.chat_attachment_profile_default_image_frame)
                    .into(holder.imageview);
        } else {
            Picasso.with(context).load(R.mipmap.chat_attachment_profile_default_image_frame).into(holder.imageview);
        }

        if (blockListPojo.isSecretChat()) {
            holder.ivSecretIcon.setVisibility(View.VISIBLE);
        } else {
            holder.ivSecretIcon.setVisibility(View.GONE);
        }
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return this.blockListPojos.size();
    }
}
