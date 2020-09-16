package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;


/**
 * created by  Adhash Team on 1/31/2017.
 */
public class VHWebLinkReceived extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView senderName, message, time, tvDateLbl, tvSecretLbl, tvInfoMsg, tvWebTitle, tvWebLink, tvWebLinkDesc;
    public ImageView starred, ivWebLink, imageViewindicatior;
    public View relative_layout_message, selection_layout;


    public VHWebLinkReceived(View view) {
        super(view);

        relative_layout_message =  view.findViewById(R.id.relative_layout_message);
        imageViewindicatior =  view.findViewById(R.id.imageView);
        senderName =  view.findViewById(R.id.lblMsgFrom);

        message =  view.findViewById(R.id.txtMsg);

        time =  view.findViewById(R.id.ts);

        tvDateLbl =  view.findViewById(R.id.tvDateLbl);
        tvSecretLbl =  view.findViewById(R.id.tvSecretLbl);

        starred =  view.findViewById(R.id.starredindicator);
        
        selection_layout =  view.findViewById(R.id.selection_layout);

        tvInfoMsg =  view.findViewById(R.id.tvInfoMsg);

        tvWebTitle =  view.findViewById(R.id.tvWebTitle);
        tvWebLink =  view.findViewById(R.id.tvWebLink);
        tvWebLinkDesc =  view.findViewById(R.id.tvWebLinkDesc);

        ivWebLink =  view.findViewById(R.id.ivWebLink);
    }

}
