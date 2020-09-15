package com.chatapp.android.app.viewholder;

import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;


/**
 * created by  Adhash Team on 2/2/2017.
 */
public class VHStarredDocumentReceived extends RecyclerView.ViewHolder {

    /**
     * Bing the widget id's
     */
    public TextView senderName, message, time, datelbl, tvInfoMsg, fromname, toname;
    public RelativeLayout mainReceived;
    public ImageView starred, userprofile, ivDoc;
    public ImageButton iBtnDownload;
    public ProgressBar Pbdownload;
    public RelativeLayout relative_layout_message;


    public VHStarredDocumentReceived(View view) {
        super(view);

        relative_layout_message = (RelativeLayout) view.findViewById(R.id.relative_layout_message);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);

        message = (TextView) view.findViewById(R.id.txtMsg);

        time = (TextView) view.findViewById(R.id.ts);

        datelbl = (TextView) view.findViewById(R.id.datelbl);

        starred = (ImageView) view.findViewById(R.id.starredindicator);
        userprofile = (ImageView) view.findViewById(R.id.userprofile);
        ivDoc = (ImageView) view.findViewById(R.id.ivDoc);

        mainReceived = (RelativeLayout) view.findViewById(R.id.mainReceived);

        iBtnDownload = (ImageButton) view.findViewById(R.id.iBtnDownload);
        Pbdownload = (ProgressBar) view.findViewById(R.id.pbUpload);
        tvInfoMsg = (TextView) view.findViewById(R.id.tvInfoMsg);
        message.setText(Html.fromHtml(message.getText().toString() + " &#160;&#160;&#160;&#160;&#160;&#160;&#160;&#160;")); // 10 spaces

    }
}

