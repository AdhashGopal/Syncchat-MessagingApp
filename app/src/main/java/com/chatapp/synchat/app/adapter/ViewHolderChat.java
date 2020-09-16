package com.chatapp.synchat.app.adapter;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.app.widget.CircleImageView;


/**
 *
 */
public class ViewHolderChat extends RecyclerView.ViewHolder {
    public AvnNextLTProRegTextView newMessage;
    public AvnNextLTProDemiTextView storeName, newMessageDate, tvTyping;

    public CircleImageView storeImage;
    public ImageView ivMsgType, mute_chatlist, tick, ivChatIcon;
    public RelativeLayout rlChat;
    public TextView header_text, newMessageCount;


    public ViewHolderChat(View view) {
        super(view);
        ivChatIcon = (ImageView) view.findViewById(R.id.iv_chat_icon);
        newMessage = (AvnNextLTProRegTextView) view.findViewById(R.id.newMessage);
        newMessageDate = (AvnNextLTProDemiTextView) view.findViewById(R.id.newMessageDate);
        storeName = (AvnNextLTProDemiTextView) view.findViewById(R.id.storeName);
        storeImage = (CircleImageView) view.findViewById(R.id.storeImage);
        newMessageCount = view.findViewById(R.id.newMessageCount);
        tvTyping = (AvnNextLTProDemiTextView) view.findViewById(R.id.tvTyping);
        header_text = (TextView) view.findViewById(R.id.header_text);

        mute_chatlist = (ImageView) view.findViewById(R.id.mute_chatlist);
        ivMsgType = (ImageView) view.findViewById(R.id.ivMsgType);
        tick = (ImageView) view.findViewById(R.id.tick);
        rlChat = (RelativeLayout) view.findViewById(R.id.rlChat);
    }
}
