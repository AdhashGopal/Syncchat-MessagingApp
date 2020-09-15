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
public class VHStarredVideoSent extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public AvnNextLTProRegTextView captiontext;

    public TextView senderName, time, fromname, toname, datelbl, duration,duration_above,ts_abovecaption;

    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock, starredindicator_below, userprofile;
    public ImageView single_tick_green_above,double_tick_green_above,double_tick_blue_above,starredindicator_above,clock_above;
    public RelativeLayout caption,videoabove_layout,video_belowlayout;
    public ImageView thumbnail;

    public VHStarredVideoSent(View view) {
        super(view);
        caption = (RelativeLayout) view.findViewById(R.id.caption);
        captiontext = (AvnNextLTProRegTextView) view.findViewById(R.id.captiontext);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        time = (TextView) view.findViewById(R.id.ts);
        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);
        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);
        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);
        clock = (ImageView) view.findViewById(R.id.clock);
        starredindicator_below = (ImageView) view.findViewById(R.id.starredindicator_below);
        thumbnail = (ImageView) view.findViewById(R.id.vidshow);
        userprofile = (ImageView) view.findViewById(R.id.userprofile);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
        datelbl = (TextView) view.findViewById(R.id.datelbl);
        duration = (TextView) view.findViewById(R.id.duration);

        duration_above=(TextView) view.findViewById(R.id.duration_above);
        single_tick_green_above = (ImageView) view.findViewById(R.id.single_tick_green_above);
        double_tick_green_above = (ImageView) view.findViewById(R.id.double_tick_green_above);
        double_tick_blue_above = (ImageView) view.findViewById(R.id.double_tick_blue_above);
        clock_above = (ImageView) view.findViewById(R.id.clock_above);
        starredindicator_above = (ImageView) view.findViewById(R.id.starredindicator_above);
        ts_abovecaption = (TextView) view.findViewById(R.id.ts_abovecaption);
        videoabove_layout=(RelativeLayout)view.findViewById(R.id.videoabove_layout);
        video_belowlayout=(RelativeLayout)view.findViewById(R.id.video_belowlayout);

    }
}
