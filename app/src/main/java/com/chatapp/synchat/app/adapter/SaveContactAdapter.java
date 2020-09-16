package com.chatapp.synchat.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.core.model.ContactToSend;

import java.util.ArrayList;

/**
 * created by  Adhash Team on 3/7/2017.
 */
public class SaveContactAdapter extends RecyclerView.Adapter<SaveContactAdapter.MyViewHolder>  {

    Activity context;
    private ArrayList<ContactToSend> contactpojo = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AvnNextLTProDemiTextView type,data;
        ImageView imageview;

        public MyViewHolder(View view) {
            super(view);
            type = (AvnNextLTProDemiTextView) view.findViewById(R.id.dataType);
            data = (AvnNextLTProDemiTextView) view.findViewById(R.id.data);
            imageview = (ImageView) view.findViewById(R.id.TypeImage);

        }
    }

    public SaveContactAdapter(Activity context, ArrayList<ContactToSend> contactpojo) {
        super();
        this.context = context;
        this.contactpojo = contactpojo;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.save_contact_adapter, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ContactToSend contactToSend = contactpojo.get(position);
        holder.type.setText(contactToSend.getSubType());
        holder.data.setText(contactToSend.getNumber());

        if(contactToSend.getType().equalsIgnoreCase("Phone")){
            holder.imageview.setImageResource(R.drawable.send_phone);
        } else if(contactToSend.getType().equalsIgnoreCase("Email")){
            holder.imageview.setImageResource(R.drawable.send_mail);
        } else if(contactToSend.getType().equalsIgnoreCase("Instant Messenger")){
            holder.imageview.setImageResource(R.drawable.send_chat);
        } else if(contactToSend.getType().equalsIgnoreCase("Address")){
            holder.imageview.setImageResource(R.drawable.send_loc);
        }
       // String image = contactpojo.getImagePath();
       /* String path =  Constants.SOCKET_IP.concat(image);
        if((path != null && !path.equals(""))) {
            Picasso.with(context).load(path).fit().error(
                    R.mipmap.chat_attachment_profile_default_image_frame)
                    .into(holder.imageview);
        } else {
            holder.imageview.setImageResource(R.mipmap.chat_attachment_profile_default_image_frame);
        }*/

    }


    @Override
    public int getItemCount() {
        return this.contactpojo.size();
    }


}
