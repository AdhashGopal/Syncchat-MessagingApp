package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;


/**
 */
public class VHStarredMessageReceived extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView senderName, message, time, tvInfoMsg,lblMsgFrom2,fromname,toname,datelbl,relpaymessage_recevied,relpaymessage_receviedmedio,ts_below;
    public RelativeLayout  mainReceived;
    public ImageView starred,sentimage,cameraphoto,audioimage,pdfimage,personimage,starredindicator_below,userprofile;
    public RelativeLayout relative_layout_message,replaylayout_recevied;


    public VHStarredMessageReceived(View view) {
        super(view);

        datelbl = (TextView) view.findViewById(R.id.datelbl);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
       // 10 spaces
        pdfimage=(ImageView)view.findViewById(R.id.pdfimage);
        relative_layout_message = (RelativeLayout) view.findViewById(R.id.relative_layout_message);
        lblMsgFrom2 = (TextView) view.findViewById(R.id.lblMsgFrom2);
        relpaymessage_recevied= (TextView) view.findViewById(R.id.relpaymessage_recevied);
        relpaymessage_receviedmedio= (TextView) view.findViewById(R.id.relpaymessage_receviedmedio);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        replaylayout_recevied=(RelativeLayout)view.findViewById(R.id.replaylayout_recevied);
        message = (TextView) view.findViewById(R.id.txtMsg);
        cameraphoto=(ImageView)view.findViewById(R.id.cameraphoto);
        audioimage=(ImageView)view.findViewById(R.id.audioimage);
        sentimage=(ImageView)view.findViewById(R.id.sentimage);
        time = (TextView) view.findViewById(R.id.ts);
        personimage=(ImageView)view.findViewById(R.id.personimage);
        starred = (ImageView) view.findViewById(R.id.starredindicator);
        starredindicator_below=(ImageView)view.findViewById(R.id.starredindicator_below);
        mainReceived = (RelativeLayout) view.findViewById(R.id.mainReceived);
        ts_below=(TextView)view.findViewById(R.id.ts_below);
        userprofile=(ImageView)view.findViewById(R.id.userprofile);
        tvInfoMsg = (TextView)view.findViewById(R.id.tvInfoMsg);

    }
}
