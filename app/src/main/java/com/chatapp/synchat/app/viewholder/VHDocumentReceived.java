package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;

public class VHDocumentReceived extends RecyclerView.ViewHolder {
    /**
     * Bing the widget id's
     */

    public TextView senderName, message, time, tvDateLbl, tvSecretLbl, tvInfoMsg,tvFileSize;

    public ImageView starred, imageViewindicatior,ivDoc;
    public ImageButton iBtnDownload;
    public ProgressBar Pbdownload;
    public View relative_layout_message, selection_layout;


    public VHDocumentReceived(View view) {
        super(view);

        relative_layout_message = view.findViewById(R.id.relative_layout_message);

        senderName =  view.findViewById(R.id.lblMsgFrom);
        imageViewindicatior =  view.findViewById(R.id.imageView);
        message =  view.findViewById(R.id.txtMsg);

        tvFileSize =  view.findViewById(R.id.tv_file_size);
        time =  view.findViewById(R.id.ts);

        tvDateLbl =  view.findViewById(R.id.tvDateLbl);
        tvSecretLbl =  view.findViewById(R.id.tvSecretLbl);

        starred =  view.findViewById(R.id.starredindicator);
        selection_layout = view.findViewById(R.id.selection_layout);

        ivDoc=view.findViewById(R.id.ivDoc);
        iBtnDownload =  view.findViewById(R.id.iBtnDownload);
        Pbdownload =  view.findViewById(R.id.pbUpload);
        tvInfoMsg =  view.findViewById(R.id.tvInfoMsg);

    }
}

