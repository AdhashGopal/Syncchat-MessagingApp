package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;

public class VHVideoReceived extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView txtSenderName, time, duration, tvDateLbl, tvSecretLbl,duration_above,ts_abovecaption,imagesize;

    public ImageView thumbnail,starredindicator_below, overlay, imageViewindicatior;
    public TextView captiontext;

    public ImageView starredindicator_above;
    public View  caption, selection_layout,videoabove_layout,video_belowlayout,download;
    public ProgressBar pbdownload;



    public VHVideoReceived(View view) {
        super(view);
        txtSenderName =  view.findViewById(R.id.txtChatFrom);
        time =  view.findViewById(R.id.ts);
        imageViewindicatior =  view.findViewById(R.id.imageView);
        duration =  view.findViewById(R.id.duration);
        thumbnail =  view.findViewById(R.id.vidshow);
        download =  view.findViewById(R.id.download);
        starredindicator_below =  view.findViewById(R.id.starredindicator_below);
        caption =  view.findViewById(R.id.caption);
        captiontext = view.findViewById(R.id.captiontext);
        overlay =  view.findViewById(R.id.overlay);
        pbdownload =  view.findViewById(R.id.pbUpload);
        tvDateLbl =  view.findViewById(R.id.tvDateLbl);
        tvSecretLbl =  view.findViewById(R.id.tvSecretLbl);
        selection_layout =  view.findViewById(R.id.selection_layout);
        imagesize=view.findViewById(R.id.imagesize);
        duration_above= view.findViewById(R.id.duration_above);
        starredindicator_above =  view.findViewById(R.id.starredindicator_above);
        ts_abovecaption =  view.findViewById(R.id.ts_abovecaption);
        videoabove_layout=view.findViewById(R.id.videoabove_layout);
        video_belowlayout=view.findViewById(R.id.video_belowlayout);
    }
}
