package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;


/**
 * created by  Adhash Team on 2/6/2017.
 */
public class VHStarredVideoReceived extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView senderName, time, toname, fromname, datelbl, duration,duration_above,ts_abovecaption;
    public ImageView thumbnail, download, starredindicator_below, userprofile;
    public ImageView starredindicator_above;
    public RelativeLayout caption,videoabove_layout,video_belowlayout;
    public AvnNextLTProRegTextView captiontext;

    public VHStarredVideoReceived(View view) {
        super(view);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        userprofile = (ImageView) view.findViewById(R.id.userprofile);
        time = (TextView) view.findViewById(R.id.ts);
        thumbnail = (ImageView) view.findViewById(R.id.vidshow);
        starredindicator_below = (ImageView) view.findViewById(R.id.starredindicator_below);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
        datelbl = (TextView) view.findViewById(R.id.datelbl);
        download = (ImageView) view.findViewById(R.id.download);
        duration = (TextView) view.findViewById(R.id.duration);
        caption = (RelativeLayout) view.findViewById(R.id.caption);
        captiontext = (AvnNextLTProRegTextView) view.findViewById(R.id.captiontext);


        duration_above=(TextView) view.findViewById(R.id.duration_above);
        starredindicator_above = (ImageView) view.findViewById(R.id.starredindicator_above);
        ts_abovecaption = (TextView) view.findViewById(R.id.ts_abovecaption);
        videoabove_layout=(RelativeLayout)view.findViewById(R.id.videoabove_layout);
        video_belowlayout=(RelativeLayout)view.findViewById(R.id.video_belowlayout);
    }
}
