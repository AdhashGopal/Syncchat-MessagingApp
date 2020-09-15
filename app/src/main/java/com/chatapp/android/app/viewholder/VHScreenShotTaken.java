package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;

/**
 * created by  Adhash Team on 4/6/2018.
 */

public class VHScreenShotTaken extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView tv_screen_shot_taken;


    public VHScreenShotTaken(View itemView) {
        super(itemView);
        tv_screen_shot_taken = (TextView)itemView.findViewById(R.id.tv_screen_shot_taken);
    }
}
