package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;


public class VHCallReceived extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView tvCallLbl, tvDateLbl;


    public VHCallReceived(View itemView) {
        super(itemView);

        tvCallLbl =  itemView.findViewById(R.id.tvCallLbl);
        tvDateLbl = itemView.findViewById(R.id.tvDateLbl);
    }
}
