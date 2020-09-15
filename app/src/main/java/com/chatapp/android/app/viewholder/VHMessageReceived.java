package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;



public class VHMessageReceived extends RecyclerView.ViewHolder {

    /**
     * Bing the widget id's
     */
    public TextView senderName, time, tvDateLbl, tvSecretLbl, tvInfoMsg,lblMsgFrom2,relpaymessage_recevied,relpaymessage_receviedmedio,ts_below;
    public TextView message;
    public ImageView starred,sentimage,cameraphoto,starredindicator_below,imageViewindicatior;
    public View relative_layout_message,replaylayout_recevied,selection_layout;


    public VHMessageReceived(View view) {
        super(view);
        relative_layout_message =  view.findViewById(R.id.relative_layout_message);
        lblMsgFrom2 =  view.findViewById(R.id.lblMsgFrom2);
        relpaymessage_recevied =  view.findViewById(R.id.relpaymessage_recevied);
        relpaymessage_receviedmedio =  view.findViewById(R.id.relpaymessage_receviedmedio);
        senderName =  view.findViewById(R.id.lblMsgFrom);
        replaylayout_recevied =  view.findViewById(R.id.replaylayout_recevied);
        message =  view.findViewById(R.id.txtMsg);
        cameraphoto =  view.findViewById(R.id.cameraphoto);
        sentimage =  view.findViewById(R.id.sentimage);
        time =  view.findViewById(R.id.ts);
        tvDateLbl =  view.findViewById(R.id.tvDateLbl);
        tvSecretLbl =  view.findViewById(R.id.tvSecretLbl);
        starred =  view.findViewById(R.id.starredindicator);
        starredindicator_below =  view.findViewById(R.id.starredindicator_below);
        ts_below =  view.findViewById(R.id.ts_below);
        tvInfoMsg =  view.findViewById(R.id.tvInfoMsg);
        imageViewindicatior =  view.findViewById(R.id.imageView);
        selection_layout =  view.findViewById(R.id.selection_layout);

    }

}
