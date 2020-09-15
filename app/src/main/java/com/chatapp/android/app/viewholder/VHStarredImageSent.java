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
public class VHStarredImageSent extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public AvnNextLTProRegTextView captiontext;
    public TextView senderName, time, fromname, toname, datelbl,ts_abovecaption;
    public RelativeLayout caption;
    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock, imageView, starredindicator_below, userprofile;
    public RelativeLayout ts_abovecaption_layout,rlMsgStatus_above,time_layout,rlMsgStatus;
    public ImageView  starredindicator_above,single_tick_green_above,double_tick_green_above,double_tick_blue_above,clock_above;

    public VHStarredImageSent(View view) {
        super(view);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);

        imageView = (ImageView) view.findViewById(R.id.imgshow);
        starredindicator_below = (ImageView) view.findViewById(R.id.starredindicator_below);
        userprofile = (ImageView) view.findViewById(R.id.userprofile);
        time = (TextView) view.findViewById(R.id.ts);

        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);

        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);

        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);
        caption=(RelativeLayout)view.findViewById(R.id.caption);
        clock = (ImageView) view.findViewById(R.id.clock);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
        datelbl = (TextView) view.findViewById(R.id.datelbl);
        captiontext = (AvnNextLTProRegTextView) view.findViewById(R.id.captiontext);


        single_tick_green_above = (ImageView) view.findViewById(R.id.single_tick_green_above);
        double_tick_green_above = (ImageView) view.findViewById(R.id.double_tick_green_above);
        double_tick_blue_above = (ImageView) view.findViewById(R.id.double_tick_blue_above);
        clock_above = (ImageView) view.findViewById(R.id.clock_above);
        starredindicator_above = (ImageView) view.findViewById(R.id.starredindicator_above);
        ts_abovecaption = (TextView) view.findViewById(R.id.ts_abovecaption);
        ts_abovecaption_layout=(RelativeLayout)view.findViewById(R.id.ts_abovecaption_layout);
        rlMsgStatus_above=(RelativeLayout)view.findViewById(R.id.rlMsgStatus_above);
        time_layout=(RelativeLayout)view.findViewById(R.id.time);
        rlMsgStatus=(RelativeLayout)view.findViewById(R.id.rlMsgStatus);

    }
}
