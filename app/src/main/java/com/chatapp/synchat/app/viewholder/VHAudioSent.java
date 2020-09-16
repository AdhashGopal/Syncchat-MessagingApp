package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.CircleImageView;



public class VHAudioSent extends RecyclerView.ViewHolder {

    /**
     * Bing the widget id's
     */
    public TextView senderName, time, duration, recodingduration, tvDateLbl, tvSecretLbl;
    public ImageView ivTick, clock, playButton, starredindicator, record_icon, imageViewindicatior;
    public ImageView headset;
    public SeekBar sbDuration;
    public DonutProgress pbUpload;
    public View  audiotrack_layout, selection_layout;
    public CircleImageView record_image;

    /**
     * one more field to show audio data to be added
     */
    public VHAudioSent(View view) {
        super(view);
        senderName =  view.findViewById(R.id.lblMsgFrom);
        playButton = view.findViewById(R.id.iv_play_icon);
        time =  view.findViewById(R.id.ts);
        imageViewindicatior = view.findViewById(R.id.imageView);
        duration =  view.findViewById(R.id.duration);
        recodingduration =  view.findViewById(R.id.recodingduration);
        ivTick = view.findViewById(R.id.single_tick_green);

        clock = view.findViewById(R.id.clock);
        starredindicator = view.findViewById(R.id.starredindicator);
        sbDuration =  view.findViewById(R.id.sbDuration);
        pbUpload =  view.findViewById(R.id.pbUpload);
        tvDateLbl =  view.findViewById(R.id.tvDateLbl);
        tvSecretLbl =  view.findViewById(R.id.tvSecretLbl);
        record_image =  view.findViewById(R.id.record_image);
        audiotrack_layout =  view.findViewById(R.id.audiotrack_layout);
        record_icon = view.findViewById(R.id.record_icon);
        headset=view.findViewById(R.id.headset);
        pbUpload.setMax(100);
        selection_layout =  view.findViewById(R.id.selection_layout);
        

    }


}
