package com.chatapp.android.app.viewholder;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;


public class VHDocumentSent extends RecyclerView.ViewHolder {

    /**
     * Bing the widget id's
     */
    public TextView  message, time, tvDateLbl, tvSecretLbl,tvFileSize;


    public ImageView  clock, starred, imageViewindicatior,ivTick;
    public ImageButton iBtnDownload;
    public ProgressBar pbUpload;
    public View mainSent, selection_layout, relative_layout_message;

    public VHDocumentSent(View view) {
        super(view);

        tvFileSize= view.findViewById(R.id.tv_file_size);

        message = view.findViewById(R.id.txtMsg);

        time = view.findViewById(R.id.ts);
        imageViewindicatior =  view.findViewById(R.id.imageView);
        tvDateLbl = view.findViewById(R.id.tvDateLbl);
        tvSecretLbl = view.findViewById(R.id.tvSecretLbl);

        ivTick =  view.findViewById(R.id.iv_tick);

        selection_layout =  view.findViewById(R.id.selection_layout);
        clock =  view.findViewById(R.id.clock);
        pbUpload =  view.findViewById(R.id.pbUpload);
        mainSent =  view.findViewById(R.id.mainSent);
        relative_layout_message =  view.findViewById(R.id.relative_layout_message);

        starred =  view.findViewById(R.id.starredindicator);
        iBtnDownload = view.findViewById(R.id.iBtnDownload);

        //TODO tharani
        //pbUpload.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
    }
}

