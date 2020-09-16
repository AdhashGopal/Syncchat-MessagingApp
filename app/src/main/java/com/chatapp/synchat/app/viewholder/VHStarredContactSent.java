package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.CircleImageView;


/**
 * created by  Adhash Team on 2/6/2017.
 */
public class VHStarredContactSent extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView senderName, time, contactName, contactNumber, invite, add, invite1, add1, message, tvDateLbl, tvSecretLbl,toname,fromname;
    public LinearLayout contact_add_invite;
    public ImageView starredindicator_below, imageViewindicatior;
    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock;
    public View v1;
    public RelativeLayout rlContactSent, selection_layout, relative_layout_message;
    public CircleImageView contactImage;

    /**
     * one more field to show contact data to be added
     */
    public VHStarredContactSent(View view) {
        super(view);

        rlContactSent = (RelativeLayout) view.findViewById(R.id.rlContactSent);
        imageViewindicatior = (ImageView) view.findViewById(R.id.imageView);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        contact_add_invite = (LinearLayout) view.findViewById(R.id.contact_add_invite);
        contactName = (TextView) view.findViewById(R.id.contactName);
        starredindicator_below = (ImageView) view.findViewById(R.id.starredindicator_below);
        contactNumber = (TextView) view.findViewById(R.id.contactNumber);
        contactImage = (CircleImageView) view.findViewById(R.id.contactImage);
        time = (TextView) view.findViewById(R.id.ts);
        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);
        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);
        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);
        clock = (ImageView) view.findViewById(R.id.clock);
        tvDateLbl = (TextView) view.findViewById(R.id.datelbl);
        tvSecretLbl = (TextView) view.findViewById(R.id.tvSecretLbl);
        invite = (TextView) view.findViewById(R.id.invite);
        message = (TextView) view.findViewById(R.id.message);
        add = (TextView) view.findViewById(R.id.add);
        invite1 = (TextView) view.findViewById(R.id.invite_1);
        add1 = (TextView) view.findViewById(R.id.add_1);
        v1 = view.findViewById(R.id.v1);
        selection_layout = (RelativeLayout) view.findViewById(R.id.selection_layout);
        relative_layout_message = (RelativeLayout) view.findViewById(R.id.relative_layout_message);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);


    }
}
