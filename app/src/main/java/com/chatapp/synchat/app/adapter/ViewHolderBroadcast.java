package com.chatapp.synchat.app.adapter;

import android.view.View;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;


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
