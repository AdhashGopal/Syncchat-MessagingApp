package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.CircleImageView;


/**
 * created by  Adhash Team on 1/10/2017.
 */
public class ViewHolderArchive extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView newMessage, storeName, newMessageDate, newMessageCount,archived;
    public CircleImageView storeImage;
    public ImageView ivMsgType, mute_chatlist, tick;

    public ViewHolderArchive(View aView) {
        super(aView);
        newMessage = (TextView) aView.findViewById(R.id.newMessage);
        newMessageDate = (TextView) aView.findViewById(R.id.newMessageDate);
        storeName = (TextView) aView.findViewById(R.id.storeName);
        storeImage = (CircleImageView) aView.findViewById(R.id.storeImage);
        newMessageCount = (TextView) aView.findViewById(R.id.newMessageCount);
        archived = (TextView) aView.findViewById(R.id.archived);
        tick = (ImageView) aView.findViewById(R.id.tick);
        mute_chatlist = (ImageView) aView.findViewById(R.id.mute_chatlist);
        ivMsgType = (ImageView) aView.findViewById(R.id.ivMsgType);
    }
}
