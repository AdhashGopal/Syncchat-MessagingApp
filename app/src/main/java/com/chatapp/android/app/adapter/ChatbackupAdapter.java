package com.chatapp.android.app.adapter;

/**
 * created by  Adhash Team on 12/7/2016.
 */

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.utils.RecyclerViewItemClickListener;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.model.GmailAccountPojo;

public class ChatbackupAdapter extends RecyclerView.Adapter<ChatbackupAdapter.VHGmailAccount> {

    private List<GmailAccountPojo> accountList;
    private Context context;

    private View recyclerView;
    private RecyclerViewItemClickListener listener;

    public ChatbackupAdapter(Context context, List<GmailAccountPojo> accountList) {
        this.context = context;
        this.accountList = accountList;
    }

    public void setItemClickListener(View recyclerView, RecyclerViewItemClickListener listener) {
        this.recyclerView = recyclerView;
        this.listener = listener;
    }

    @Override
    public VHGmailAccount onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.account_view_layout, parent, false);

        VHGmailAccount holder = new VHGmailAccount(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final VHGmailAccount holder, int position) {

        GmailAccountPojo pojo = accountList.get(position);

        holder.rbAccount.setText(pojo.getMailId());
        holder.rbAccount.setChecked(pojo.isSelected());

        holder.rbAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null) {
                    listener.onRVItemClick(recyclerView, holder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    public class VHGmailAccount extends RecyclerView.ViewHolder {

        private RadioButton rbAccount;

        public VHGmailAccount(View itemView) {
            super(itemView);

            rbAccount = (RadioButton) itemView.findViewById(R.id.rbAccount);
            Typeface typeface = CoreController.getInstance().getAvnNextLTProRegularTypeface();
            rbAccount.setTypeface(typeface);
        }
    }

}