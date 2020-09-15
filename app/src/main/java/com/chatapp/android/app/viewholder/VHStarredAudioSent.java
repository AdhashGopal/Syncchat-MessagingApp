package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.chatapp.android.R;

/**
 * created by  Adhash Team on 2/6/2017.
 */
public class VHStarredAudioSent extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView senderName, time,toname,fromname,datelbl,duration;
    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock, playButton,starredindicator,userprofile;
    public SeekBar sbDuration;
    public DonutProgress pbUpload;

    /**
     * one more field to show audio data to be added
     */
    public VHStarredAudioSent(View view) {
        super(view);

        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
        datelbl = (TextView) view.findViewById(R.id.datelbl);
        duration=(TextView)view.findViewById(R.id.duration);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        playButton = (ImageView) view.findViewById(R.id.imageView26);
        userprofile=(ImageView)view.findViewById(R.id.userprofile);
        time = (TextView) view.findViewById(R.id.ts);
        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);
        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);
        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);
        clock = (ImageView) view.findViewById(R.id.clock);
        starredindicator = (ImageView) view.findViewById(R.id.starredindicator);
        sbDuration = (SeekBar) view.findViewById(R.id.sbDuration);
        pbUpload = (DonutProgress) view.findViewById(R.id.pbUpload);
        pbUpload.setMax(100);
    }

}
