package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.CircleImageView;


/**
 * created by  Adhash Team on 2/6/2017.
 */
public class VHStarredContactReceived extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView senderName, time, contactName, contactNumber, invite, add, invite1, add1, message1, tvDateLbl, tvSecretLbl,toname,fromname;
    public ImageView starredindicator_below, imageViewindicatior;
    public RelativeLayout rlContactReceived, selection_layout;
    public View v1;
    public LinearLayout contact_add_invite;
    public CircleImageView contactimage;

    /**
     * one more field to show contact data to be added
     */
    public VHStarredContactReceived(View view) {
        super(view);

        rlContactReceived = (RelativeLayout) view.findViewById(R.id.rlContactReceived);
        imageViewindicatior = (ImageView) view.findViewById(R.id.imageView);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        starredindicator_below = (ImageView) view.findViewById(R.id.starredindicator_below);
        contactimage = (CircleImageView) view.findViewById(R.id.contactImage);

        time = (TextView) view.findViewById(R.id.ts);
        tvDateLbl = (TextView) view.findViewById(R.id.datelbl);
        tvSecretLbl = (TextView) view.findViewById(R.id.tvSecretLbl);

        contactName = (TextView) view.findViewById(R.id.contactName);

        contactNumber = (TextView) view.findViewById(R.id.contactNumber);

        invite = (TextView) view.findViewById(R.id.invite);
        v1 = view.findViewById(R.id.v1);
        add = (TextView) view.findViewById(R.id.add);
        invite1 = (TextView) view.findViewById(R.id.invite_1);
        message1 = (TextView) view.findViewById(R.id.message_1);
        add1 = (TextView) view.findViewById(R.id.add_1);
        contact_add_invite = (LinearLayout) view.findViewById(R.id.contact_add_invite);
        selection_layout = (RelativeLayout) view.findViewById(R.id.selection_layout);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);

    }
}
