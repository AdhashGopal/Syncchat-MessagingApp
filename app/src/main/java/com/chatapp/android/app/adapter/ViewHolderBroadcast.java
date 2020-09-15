package com.chatapp.android.app.adapter;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;


/**
 */
public class ViewHolderBroadcast extends RecyclerView.ViewHolder {
    public AvnNextLTProRegTextView newMessage;
    public AvnNextLTProDemiTextView storeName;

    public RelativeLayout rlChat;


    public ViewHolderBroadcast(View view) {
        super(view);

        newMessage = (AvnNextLTProRegTextView) view.findViewById(R.id.newMessage);

        storeName = (AvnNextLTProDemiTextView) view.findViewById(R.id.storeName);

        rlChat = (RelativeLayout) view.findViewById(R.id.rlChat);



    }
}
