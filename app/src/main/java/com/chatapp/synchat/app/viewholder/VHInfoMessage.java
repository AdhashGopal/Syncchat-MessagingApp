package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;

/**
 * created by  Adhash Team on 4/6/2018.
 */

public class VHInfoMessage extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView tvInfoMsg, tvDateLbl;

    public VHInfoMessage(View itemView) {
        super(itemView);
        tvInfoMsg = (TextView) itemView.findViewById(R.id.tvInfoMsg);
        tvDateLbl = (TextView) itemView.findViewById(R.id.tvDateLbl);

    }
}


