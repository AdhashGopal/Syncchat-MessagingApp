package com.chatapp.android.app.viewholder;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;


/**
 * created by  Adhash Team on 2/6/2017.
 */
public class VHStarredWebLinkReceived extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */
    public TextView senderName, message, time, tvDateLbl, tvInfoMsg, tvWebTitle, tvWebLink, tvWebLinkDesc,fromname,toname,datelbl;
    public RelativeLayout mainReceived;
    public ImageView starred, ivWebLink,userprofile;
    public RelativeLayout relative_layout_message, rlWebLink;


    public VHStarredWebLinkReceived(View view) {
        super(view);

        relative_layout_message = (RelativeLayout) view.findViewById(R.id.relative_layout_message);

        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);

        message = (TextView) view.findViewById(R.id.txtMsg);

        time = (TextView) view.findViewById(R.id.ts);

        tvDateLbl = (TextView) view.findViewById(R.id.tvDateLbl);

        userprofile = (ImageView) view.findViewById(R.id.userprofile);
        starred = (ImageView) view.findViewById(R.id.starredindicator);

        mainReceived = (RelativeLayout) view.findViewById(R.id.mainReceived);
        rlWebLink = (RelativeLayout) view.findViewById(R.id.rlWebLink);

        tvInfoMsg = (TextView) view.findViewById(R.id.tvInfoMsg);
        message.setText(Html.fromHtml(message.getText().toString() + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;")); // 10 spaces

        tvWebTitle = (TextView) view.findViewById(R.id.tvWebTitle);
        tvWebLink = (TextView) view.findViewById(R.id.tvWebLink);
        tvWebLinkDesc = (TextView) view.findViewById(R.id.tvWebLinkDesc);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
        datelbl = (TextView) view.findViewById(R.id.datelbl);
        ivWebLink = (ImageView) view.findViewById(R.id.ivWebLink);
    }
}
