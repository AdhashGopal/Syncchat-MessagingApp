package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;


public class VHImageReceived extends RecyclerView.ViewHolder {

    /**
     * Bing the widget id's
     */
    public TextView senderName, time, tvDateLbl, tvSecretLbl,ts_abovecaption,imagesize;
    public TextView captiontext;
    public ImageView imageView,starredindicator_below, forward_image, imageViewindicatior;
    public ProgressBar pbUpload;
    public View selection_layout, rlTs,abovecaption_layout,download;
    public ImageView  starredindicator_above;

    public VHImageReceived(View view) {
        super(view);
        senderName =  view.findViewById(R.id.lblMsgFrom);
        imageView =  view.findViewById(R.id.imgshow);
        imageViewindicatior =  view.findViewById(R.id.imageView);
        starredindicator_below =  view.findViewById(R.id.starredindicator_below);
        time =  view.findViewById(R.id.ts);
        pbUpload =  view.findViewById(R.id.pbUpload);
        download =  view.findViewById(R.id.download);
        forward_image =  view.findViewById(R.id.forward_image);
        captiontext =  view.findViewById(R.id.captiontext);
        tvDateLbl =  view.findViewById(R.id.tvDateLbl);
        tvSecretLbl =  view.findViewById(R.id.tvSecretLbl);
        selection_layout =  view.findViewById(R.id.selection_layout);
        rlTs =  view.findViewById(R.id.rlTs);
        imagesize=view.findViewById(R.id.imagesize);
        starredindicator_above =  view.findViewById(R.id.starredindicator_above);
        ts_abovecaption =  view.findViewById(R.id.ts_abovecaption);
        abovecaption_layout=view.findViewById(R.id.abovecaption);

    }
}
