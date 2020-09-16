package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.CircleImageView;

/**
 * created by  Adhash Team on 2/6/2017.
 */
public class VHStarredAudioReceived extends RecyclerView.ViewHolder {

    /**
     * Bing the widget id's
     */
    public TextView senderName, time, duration, time_ts, fromname, toname, datelbl,recodingduration;
    public SeekBar sbDuration;
    public ImageView playButton, download, headset, starredindicator, userprofile,record_icon;
    public RelativeLayout rlPlayCtrl,audiotrack_layout;
    public DonutProgress pbDownload;
    public CircleImageView record_image;

    public VHStarredAudioReceived(View view) {
        super(view);
        rlPlayCtrl = (RelativeLayout) view.findViewById(R.id.rlPlayCtrl);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
        datelbl = (TextView) view.findViewById(R.id.datelbl);
        playButton = (ImageView) view.findViewById(R.id.imageView26);
        headset = (ImageView) view.findViewById(R.id.headset);
        download = (ImageView) view.findViewById(R.id.download);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        recodingduration = (TextView) view.findViewById(R.id.recodingduration);
        duration = (TextView) view.findViewById(R.id.duration);
        audiotrack_layout = (RelativeLayout) view.findViewById(R.id.audiotrack_layout);
        userprofile = (ImageView) view.findViewById(R.id.userprofile);
        record_image = (CircleImageView) view.findViewById(R.id.record_image);
        //tsNextLine = (TextView) view.findViewById(R.id.tsNextLine);
        starredindicator = (ImageView) view.findViewById(R.id.starredindicator);
        time_ts = (TextView) view.findViewById(R.id.time_ts);
        record_icon = (ImageView) view.findViewById(R.id.record_icon);
        sbDuration = (SeekBar) view.findViewById(R.id.sbDuration);
        pbDownload = (DonutProgress) view.findViewById(R.id.pbDownload);
        pbDownload.setMax(100);
    }
}
