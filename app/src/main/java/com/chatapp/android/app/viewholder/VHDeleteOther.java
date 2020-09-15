package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;

/**
 * created by  Adhash Team on 4/6/2018.
 */

public class VHDeleteOther extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView tv_time;
    public View selection_layout;

    public VHDeleteOther(View itemView) {
        super(itemView);
        tv_time = (TextView)itemView.findViewById(R.id.tv_time);
        selection_layout=itemView.findViewById(R.id.selection_layout);
    }
}
