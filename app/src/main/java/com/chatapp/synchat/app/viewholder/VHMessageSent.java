package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;


public class VHMessageSent extends RecyclerView.ViewHolder {

        /**
         * Bing the widget id's
         */
    public TextView  time, ts_new, timebelow, tvDateLbl, tvSecretLbl, replaymessage, lblMsgFrom2, replaymessagemedio, ts_reply, ts_reply_above;
    public TextView message;
    public ImageView   clock_new, clockbelow, clock, starredbelow, starred, star_new, cameraphoto, sentimage,
            starredindicator_reply, imageViewindicatior, clock_reply,
             starredindicator_reply_above, clock_reply_above;

    public ImageView
            doubleTickBlue,single_tick_green_below_reply,single_tick_green_above_reply;

    public ImageView ivSingleLineTick,ivTickBelow;
    public RelativeLayout   replaylayout, layout_reply_tsalign, layout_reply_tsalign_above,
            message_sent_singleChar_layout, below_layout;

    public View selection_layout,relative_layout_message;

    public VHMessageSent(View view) {
            super(view);
            lblMsgFrom2 = (TextView) view.findViewById(R.id.lblMsgFrom2);
            message = (TextView) view.findViewById(R.id.txtMsg);
            replaymessage = (TextView) view.findViewById(R.id.replaymessage);
            replaymessagemedio = (TextView) view.findViewById(R.id.replaymessagemedio);
            time = (TextView) view.findViewById(R.id.ts);
            ts_new = (TextView) view.findViewById(R.id.ts_new);
            ts_reply = (TextView) view.findViewById(R.id.ts_reply);
            ts_reply_above = (TextView) view.findViewById(R.id.ts_reply_above);
            timebelow = (TextView) view.findViewById(R.id.ts_below);
            cameraphoto = (ImageView) view.findViewById(R.id.cameraphoto);
            sentimage = (ImageView) view.findViewById(R.id.sentimage);
            tvDateLbl = (TextView) view.findViewById(R.id.tvDateLbl);
            tvSecretLbl = (TextView) view.findViewById(R.id.tvSecretLbl);
            ivSingleLineTick = (ImageView) view.findViewById(R.id.iv_singleline_tick);
            ivTickBelow = (ImageView) view.findViewById(R.id.tick_below);
            single_tick_green_below_reply = (ImageView) view.findViewById(R.id.single_tick_green_below_reply);
            single_tick_green_above_reply = (ImageView) view.findViewById(R.id.single_tick_green_above_reply);


            doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);

            clock = (ImageView) view.findViewById(R.id.clock);
            clock_reply = (ImageView) view.findViewById(R.id.clock_reply);
            clock_reply_above = (ImageView) view.findViewById(R.id.clock_reply_above);
            clockbelow = (ImageView) view.findViewById(R.id.clock_below);
            clock_new = (ImageView) view.findViewById(R.id.clock_new);
            replaylayout = (RelativeLayout) view.findViewById(R.id.replaylayout);
            layout_reply_tsalign = (RelativeLayout) view.findViewById(R.id.layout_reply_tsalign);
            starred = (ImageView) view.findViewById(R.id.starredindicator);
            star_new = (ImageView) view.findViewById(R.id.star_new);
            starredbelow = (ImageView) view.findViewById(R.id.starredindicator_below);
            starredindicator_reply_above = (ImageView) view.findViewById(R.id.starredindicator_reply_above);
            starredindicator_reply = (ImageView) view.findViewById(R.id.starredindicator_reply);
            imageViewindicatior = (ImageView) view.findViewById(R.id.imageView);
            relative_layout_message = view.findViewById(R.id.relative_layout_message);
            layout_reply_tsalign_above = (RelativeLayout) view.findViewById(R.id.layout_reply_tsalign_above);
            selection_layout = view.findViewById(R.id.selection_layout);
            message_sent_singleChar_layout = (RelativeLayout) view.findViewById(R.id.message_sent_singleChar_layout);
            below_layout = (RelativeLayout) view.findViewById(R.id.below_layout);
//        ivLocation = (ImageView) view.findViewById(R.id.ivLocation);

        }

}
