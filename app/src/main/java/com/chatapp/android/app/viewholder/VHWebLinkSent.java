package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;

public class VHWebLinkSent extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView senderName, message, tvDateLbl, tvSecretLbl, tvWebTitle, tvWebLink, tvWebLinkDesc;
    public TextView time;
    public ImageView  clock, starred, ivWebLink, imageViewindicatior,ivTick;
    public View  selection_layout;

    public VHWebLinkSent(View view) {
        super(view);
        imageViewindicatior =  view.findViewById(R.id.imageView);
        senderName =  view.findViewById(R.id.lblMsgFrom);
        message =  view.findViewById(R.id.txtMsg);
        time = view.findViewById(R.id.ts);
        tvDateLbl =  view.findViewById(R.id.tvDateLbl);
        tvSecretLbl =  view.findViewById(R.id.tvSecretLbl);
        ivTick=view.findViewById(R.id.iv_tick);
        clock =  view.findViewById(R.id.clock);
        starred =  view.findViewById(R.id.starredindicator);
        selection_layout =  view.findViewById(R.id.selection_layout);
        tvWebTitle =  view.findViewById(R.id.tvWebTitle);
        tvWebLink =  view.findViewById(R.id.tvWebLink);
        tvWebLinkDesc =  view.findViewById(R.id.tvWebLinkDesc);
        ivWebLink =  view.findViewById(R.id.ivWebLink);
    }
}
