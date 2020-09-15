package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.github.lzyzsd.circleprogress.DonutProgress;
import com.chatapp.android.R;


/**
 */
public class VHVideoSent extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */

    public TextView  time, duration, tvDateLbl, tvSecretLbl,duration_above,ts_abovecaption;
    public TextView captiontext;
    public ImageView  clock, starredindicator_below, imageViewindicatior;
    public ImageView starredindicator_above,clock_above;
    public ImageView ivTickAbove,ivTick;
    public View  selection_layout,videoabove_layout,video_belowlayout;

    public ImageView thumbnail, ivPlay,ivPauseResume;

    public DonutProgress pbUpload;

    public VHVideoSent(View view) {
        super(view);


        duration =  view.findViewById(R.id.duration);
        time =  view.findViewById(R.id.ts);
        imageViewindicatior =  view.findViewById(R.id.imageView);
        ivTick =  view.findViewById(R.id.iv_tick);

        clock =  view.findViewById(R.id.clock);
        starredindicator_below =  view.findViewById(R.id.starredindicator_below);
        thumbnail =  view.findViewById(R.id.vidshow);
        ivPlay =  view.findViewById(R.id.ivPlay);
        ivPauseResume=  view.findViewById(R.id.pause_resume_icon_video);

        captiontext =  view.findViewById(R.id.captiontext);

        pbUpload =  view.findViewById(R.id.pbUpload);
        pbUpload.setMax(100);
        tvDateLbl =  view.findViewById(R.id.tvDateLbl);
        tvSecretLbl =  view.findViewById(R.id.tvSecretLbl);
        selection_layout =  view.findViewById(R.id.video_main);

        duration_above= view.findViewById(R.id.duration_above);
        ivTickAbove =  view.findViewById(R.id.tick_above);
        clock_above =  view.findViewById(R.id.clock_above);
        starredindicator_above =  view.findViewById(R.id.starredindicator_above);
        ts_abovecaption =  view.findViewById(R.id.ts_abovecaption);
        videoabove_layout=view.findViewById(R.id.videoabove_layout);
        video_belowlayout=view.findViewById(R.id.vieo_below_group);

    }

}
