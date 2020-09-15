package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;


/**
 */
public class VHservermessage extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView tvServerMsgLbl, tvSecretLbl, tvDateLbl;


    public VHservermessage(View view) {
        super(view);

        tvServerMsgLbl = (TextView) view.findViewById(R.id.tvServerMsgLbl);
        tvSecretLbl = (TextView) view.findViewById(R.id.tvSecretLbl);
        tvDateLbl = (TextView) view.findViewById(R.id.tvDateLbl);

    }
}
