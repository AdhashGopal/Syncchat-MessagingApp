package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;


/**
 * created by  Adhash Team on 2/6/2017.
 */
public class VHStarredWebLinkSent extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */

    public TextView message, time, tvDateLbl, tvWebTitle, tvWebLink, tvWebLinkDesc, fromname, toname, datelbl;

    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock, starred, ivWebLink, userprofile;

    public RelativeLayout mainSent, rlWebLink, maintextview;

    public VHStarredWebLinkSent(View view) {
        super(view);


        message = (TextView) view.findViewById(R.id.txtMsg);

        time = (TextView) view.findViewById(R.id.ts);

        tvDateLbl = (TextView) view.findViewById(R.id.tvDateLbl);

        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);

        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);

        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);
        userprofile = (ImageView) view.findViewById(R.id.userprofile);
        clock = (ImageView) view.findViewById(R.id.clock);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
        datelbl = (TextView) view.findViewById(R.id.datelbl);
        mainSent = (RelativeLayout) view.findViewById(R.id.mainSent);
        rlWebLink = (RelativeLayout) view.findViewById(R.id.rlWebLink);

        starred = (ImageView) view.findViewById(R.id.starredindicator);

        tvWebTitle = (TextView) view.findViewById(R.id.tvWebTitle);
        tvWebLink = (TextView) view.findViewById(R.id.tvWebLink);
        tvWebLinkDesc = (TextView) view.findViewById(R.id.tvWebLinkDesc);
        maintextview = (RelativeLayout) view.findViewById(R.id.relative_layout_message);
        ivWebLink = (ImageView) view.findViewById(R.id.ivWebLink);

    }
}
