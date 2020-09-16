package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;


/**
 * created by  Adhash Team on 2/2/2017.
 */
public class VHStarredDocumentsent extends RecyclerView.ViewHolder {

    /**
     * Bing the widget id's
     */
    public TextView senderName, message, time, datelbl, fromname, toname;

    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock, starred, userprofile, ivDoc;
    public ProgressBar pbUpload;
    public RelativeLayout mainSent;

    public VHStarredDocumentsent(View view) {
        super(view);

        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);

        message = (TextView) view.findViewById(R.id.txtMsg);

        time = (TextView) view.findViewById(R.id.ts);
        userprofile = (ImageView) view.findViewById(R.id.userprofile);
        datelbl = (TextView) view.findViewById(R.id.datelbl);

        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);

        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);

        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);

        clock = (ImageView) view.findViewById(R.id.clock);
        pbUpload = (ProgressBar) view.findViewById(R.id.pbUpload);
        mainSent = (RelativeLayout) view.findViewById(R.id.mainSent);

        starred = (ImageView) view.findViewById(R.id.starredindicator);
        ivDoc = (ImageView) view.findViewById(R.id.ivDoc);

    }
}

