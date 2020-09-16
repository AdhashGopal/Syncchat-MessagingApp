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
public class VHStarredMessageSent extends RecyclerView.ViewHolder {

        /**
         * Bing the widget id's
         */
        public TextView fromname,toname,datelbl;

    public TextView senderName, message, time,timebelow, tvDateLbl,replaymessage,lblMsgFrom2,replaymessagemedio;

    public ImageView userprofile,singleTick,singleTickbelow,doubleTickGreenbelow, doubleTickBluebelow,doubleTickGreen, doubleTickBlue,clockbelow, clock, starredbelow,starred,cameraphoto,sentimage,audioimage,pdfimage,personimage;

        public RelativeLayout mainSent,relative_layout_message,replaylayout;

    public VHStarredMessageSent(View view) {
            super(view);
            userprofile=(ImageView)view.findViewById(R.id.userprofile);
            fromname = (TextView) view.findViewById(R.id.fromname);
            toname = (TextView) view.findViewById(R.id.toname);
            senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
            lblMsgFrom2 = (TextView) view.findViewById(R.id.lblMsgFrom2);
            personimage=(ImageView)  view.findViewById(R.id.personimage);
            pdfimage=(ImageView)view.findViewById(R.id.pdfimage);
            message = (TextView) view.findViewById(R.id.txtMsg);

            time = (TextView) view.findViewById(R.id.ts);
            timebelow=(TextView)view.findViewById(R.id.ts_below);
            cameraphoto=(ImageView)view.findViewById(R.id.cameraphoto);
            sentimage=(ImageView)view.findViewById(R.id.sentimage);
            tvDateLbl = (TextView) view.findViewById(R.id.tvDateLbl);
            audioimage=(ImageView)view.findViewById(R.id.audioimage);
            singleTick = (ImageView) view.findViewById(R.id.single_tick_green);
            singleTickbelow=(ImageView)view.findViewById(R.id.single_tick_green_below_below);
            doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);
            doubleTickGreenbelow=(ImageView)view.findViewById(R.id.double_tick_green_below);
            doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);
            doubleTickBluebelow=(ImageView)view.findViewById(R.id.double_tick_blue_below);
            clock = (ImageView) view.findViewById(R.id.clock);
            clockbelow=(ImageView)view.findViewById(R.id.clock_below);
            mainSent = (RelativeLayout)view.findViewById(R.id.mainSent);
            replaylayout = (RelativeLayout)view.findViewById(R.id.replaylayout);
            starred = (ImageView)view.findViewById(R.id.starredindicator);
            starredbelow=(ImageView)view.findViewById(R.id.starredindicator_below);
            datelbl=(TextView)view.findViewById(R.id.datelbl);
        }
    }


