package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.chatapp.synchat.R;


public class VHImageSent extends RecyclerView.ViewHolder {

    /**
     * Bing the widget id's
     */
    public TextView time, tvDateLbl, tvSecretLbl, ts_abovecaption;
    public TextView captiontext;
    public ImageView  clock, imageView, starredindicator_below,
            forward_image, imageViewindicatior;


    public ImageView ivTick,ivTickAbove;


    public ImageView starredindicator_above, clock_above;
    public DonutProgress pbUpload;

    public ImageView ivPauseResumeIcon;
    public View  selection_layout, relative_layout_message, rlDownload;
    public View ts_abovecaption_layout, rlMsgStatus_above, time_layout, rlMsgStatus;

    public VHImageSent(View view) {
        super(view);
        imageView =  view.findViewById(R.id.imgshow);
        starredindicator_below =  view.findViewById(R.id.starredindicator_below);

        time =  view.findViewById(R.id.ts);
        captiontext =  view.findViewById(R.id.captiontext);
        ivTick =  view.findViewById(R.id.ivTick);


        rlDownload =  view.findViewById(R.id.rlDownload);


        imageViewindicatior =  view.findViewById(R.id.imageView);
        clock =  view.findViewById(R.id.clock);

        forward_image =  view.findViewById(R.id.forward_image);
        pbUpload =  view.findViewById(R.id.pbUpload);
        ivPauseResumeIcon=  view.findViewById(R.id.pause_resume_icon);
        tvDateLbl =  view.findViewById(R.id.tvDateLbl);
        tvSecretLbl =  view.findViewById(R.id.tvSecretLbl);

        selection_layout =  view.findViewById(R.id.selection_layout);
        relative_layout_message =  view.findViewById(R.id.relative_layout_message);
        ivTickAbove =  view.findViewById(R.id.tick_above);
        clock_above =  view.findViewById(R.id.clock_above);
        starredindicator_above =  view.findViewById(R.id.starredindicator_above);
        ts_abovecaption =  view.findViewById(R.id.ts_abovecaption);
        ts_abovecaption_layout =  view.findViewById(R.id.ts_abovecaption_layout);
        rlMsgStatus_above =  view.findViewById(R.id.rlMsgStatus_above);
        time_layout =  view.findViewById(R.id.time);
        rlMsgStatus =  view.findViewById(R.id.rlMsgStatus);

    }
}
