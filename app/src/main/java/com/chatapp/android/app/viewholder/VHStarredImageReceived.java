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
public class VHStarredImageReceived extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public AvnNextLTProRegTextView captiontext;
    public TextView senderName, time, toname, fromname, datelbl,ts_abovecaption;
    public ImageView imageView, starredindicator_below, userprofile;
    public RelativeLayout caption,abovecaption_layout,rlTs;
    public ImageView  starredindicator_above;

    public VHStarredImageReceived(View view) {
        super(view);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        imageView = (ImageView) view.findViewById(R.id.imgshow);
        userprofile = (ImageView) view.findViewById(R.id.userprofile);
        time = (TextView) view.findViewById(R.id.ts);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
        datelbl = (TextView) view.findViewById(R.id.datelbl);
        starredindicator_below = (ImageView) view.findViewById(R.id.starredindicator_below);
        captiontext = (AvnNextLTProRegTextView) view.findViewById(R.id.captiontext);
        caption = (RelativeLayout) view.findViewById(R.id.caption);
        rlTs = (RelativeLayout) view.findViewById(R.id.rlTs);

        starredindicator_above = (ImageView) view.findViewById(R.id.starredindicator_above);
        ts_abovecaption = (TextView) view.findViewById(R.id.ts_abovecaption);
        abovecaption_layout=(RelativeLayout)view.findViewById(R.id.abovecaption);

    }
}
