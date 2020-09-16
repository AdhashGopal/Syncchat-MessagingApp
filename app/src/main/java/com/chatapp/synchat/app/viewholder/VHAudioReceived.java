package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.CircleImageView;



public class VHAudioReceived extends RecyclerView.ViewHolder {

    /**
     * Bing the widget id's
     */
    public TextView senderName, duration, time_ts, recodingduration, tvDateLbl, tvSecretLbl;
    public SeekBar sbDuration;
    public ImageView playButton, download, starredindicator, record_icon, imageViewindicatior;
    public View selection_layout,audiotrack_layout_back;
    public ProgressBar pbdownload;
    public CircleImageView record_image;

    public VHAudioReceived(View view) {
        super(view);
        playButton =  view.findViewById(R.id.imageView26);
        download =  view.findViewById(R.id.download);
        senderName =  view.findViewById(R.id.lblMsgFrom);
        duration =  view.findViewById(R.id.duration);
        imageViewindicatior =  view.findViewById(R.id.imageView);
        recodingduration =  view.findViewById(R.id.recodingduration);
        tvDateLbl =  view.findViewById(R.id.tvDateLbl);
        tvSecretLbl =  view.findViewById(R.id.tvSecretLbl);
        starredindicator =  view.findViewById(R.id.starredindicator);
        time_ts =  view.findViewById(R.id.time_ts);
        sbDuration =  view.findViewById(R.id.sbDuration);
        record_image =  view.findViewById(R.id.record_image);
        audiotrack_layout_back =  view.findViewById(R.id.audiotrack_layout_back);
        record_icon =  view.findViewById(R.id.record_icon);
        pbdownload =  view.findViewById(R.id.pbUpload);
        selection_layout =  view.findViewById(R.id.selection_layout);


    }

}
