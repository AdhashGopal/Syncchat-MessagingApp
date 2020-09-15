package com.chatapp.android.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.core.model.ContactToSend;


import java.util.ArrayList;


public class ContactToSendAdapter extends RecyclerView.Adapter<ContactToSendAdapter.MyViewHolder> {

    Activity context;
    View itemView;
    private ArrayList<ContactToSend> contacts = new ArrayList<>();

    /**
     * OnItemCheckListener interface (onItemCheck, onItemUncheck)
     */
    public interface OnItemCheckListener {
        void onItemCheck(ContactToSend ContactToSend);

        void onItemUncheck(ContactToSend ContactToSend);
    }

    @NonNull
    private OnItemCheckListener onItemCheckListener;

    /**
     * widgets view holder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView Unumber, Utype;
        public ImageView image;
        public CheckBox checkBox;
        View ItemView;


        public MyViewHolder(View ItemView) {
            super(ItemView);
            this.ItemView = ItemView;
            Unumber = (TextView) ItemView.findViewById(R.id.contact_save_number);

            Utype = (TextView) ItemView.findViewById(R.id.detail);
            image = (ImageView) ItemView.findViewById(R.id.imageview_contact);
            checkBox = (CheckBox) ItemView.findViewById(R.id.checkContact_elmts);
            checkBox.setClickable(false);
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            ItemView.setOnClickListener(onClickListener);
        }
    }

    /**
     * Create constructor
     *
     * @param context             The activity object inherits the Context object
     * @param ContactToSend       list of value
     * @param onItemCheckListener onItemCheckListener action
     */
    public ContactToSendAdapter(Activity context, ArrayList<ContactToSend> ContactToSend, @NonNull OnItemCheckListener onItemCheckListener) {
        super();
        this.context = context;
        this.contacts = ContactToSend;
        this.onItemCheckListener = onItemCheckListener;

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
                .inflate(R.layout.send_contact_list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    /**
     * binding view data
     *
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final ContactToSend contactToSend = contacts.get(position);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.checkBox.setChecked(!(holder.checkBox.isChecked()));
                if (holder.checkBox.isChecked()) {
                    onItemCheckListener.onItemCheck(contactToSend);
                } else {
                    onItemCheckListener.onItemUncheck(contactToSend);
                }
            }
        });

        holder.Utype.setText(contactToSend.getSubType());
        holder.Unumber.setText(contactToSend.getNumber());
        if (contactToSend.getType().equalsIgnoreCase("Email")) {
            holder.image.setImageResource(R.drawable.send_mail);
        } else if (contactToSend.getType().equalsIgnoreCase("Phone")) {
            holder.image.setImageResource(R.drawable.send_phone);
        } else if (contactToSend.getType().equalsIgnoreCase("Address")) {
            holder.image.setImageResource(R.drawable.send_loc);
        } else if (contactToSend.getType().equalsIgnoreCase("Instant Messenger")) {
            holder.image.setImageResource(R.drawable.send_chat);
        }
        //String image = blockListPojo.getImagePath();
        //  String path =  Constants.SOCKET_IP.concat(image);
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return this.contacts.size();
    }


}

