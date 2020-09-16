package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.CircleImageView;

public class VHContactSent extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView senderName, time, contactName, contactNumber, invite, add, invite1, add1, tvDateLbl, tvSecretLbl,message;
    public LinearLayout contact_add_invite;
    public ImageView starredindicator_below, imageViewindicatior;
    public ImageView ivTick, clock;
    public View v1;
    public View selection_layout;
    public CircleImageView contactImage;


    public VHContactSent(View view) {
        super(view);


        imageViewindicatior =  view.findViewById(R.id.imageView);
        senderName =  view.findViewById(R.id.lblMsgFrom);
        contact_add_invite = (LinearLayout) view.findViewById(R.id.contact_add_invite);
        contactName =  view.findViewById(R.id.contactName);
        starredindicator_below =  view.findViewById(R.id.starredindicator_below);
        contactNumber =  view.findViewById(R.id.contactNumber);
        contactImage =  view.findViewById(R.id.contactImage);
        time =  view.findViewById(R.id.ts);
        ivTick =  view.findViewById(R.id.iv_tick);

        clock =  view.findViewById(R.id.clock);
        tvDateLbl =  view.findViewById(R.id.tvDateLbl);
        tvSecretLbl =  view.findViewById(R.id.tvSecretLbl);
        invite =  view.findViewById(R.id.invite);
        message =  view.findViewById(R.id.message);
        add =  view.findViewById(R.id.add);
        invite1 =  view.findViewById(R.id.invite_1);
        add1 =  view.findViewById(R.id.add_1);
        v1 =  view.findViewById(R.id.v1);
        selection_layout =  view.findViewById(R.id.selection_layout);



    }
}
