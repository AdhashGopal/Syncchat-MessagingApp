package com.chatapp.android.app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.ChatViewActivity;
import com.chatapp.android.app.utils.BroadcastInfoSession;
import com.chatapp.android.core.model.BroadcastInfoPojo;
import com.chatapp.android.core.model.ChatappContactModel;

import java.io.Serializable;
import java.util.List;

public class BroadcastListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    List<BroadcastInfoPojo> mListData;
    Context mContext;
    BroadcastInfoSession broadcastInfoSession;

    /**
     * Create constructor
     *
     * @param mContext  The activity object inherits the Context object
     * @param mListData list of value
     */
    public BroadcastListAdapter(Context mContext, List<BroadcastInfoPojo> mListData) {
        this.mListData = mListData;
        this.mContext = mContext;
        broadcastInfoSession = new BroadcastInfoSession(mContext);

    }

    /**
     * layout binding
     *
     * @param parent   layout view group
     * @param viewType
     * @return view holder
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.lp_f3_broadcast, parent, false);
        viewHolder = new ViewHolderBroadcast(v);
        return viewHolder;
    }

    /**
     * binding view data
     *
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        ViewHolderBroadcast vh2 = (ViewHolderBroadcast) holder;
        vh2.storeName.setText(mListData.get(position).getBroadcastName());

        vh2.rlChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ChatViewActivity.class);

//                String[] array = mListData.get(position).getBroadcastId().split("-");

                intent.putExtra("receiverUid", mListData.get(position).getBroadcastId());
                intent.putExtra("receiverName", mListData.get(position).getBroadcastName());


                intent.putExtra("contactModel", (Serializable) mListData.get(position).getContactsModel());
                intent.putExtra("documentId", mListData.get(position).getBroadcastId());
                intent.putExtra("Username", mListData.get(position).getBroadcastName());
                intent.putExtra("Image", "");
                intent.putExtra("type", 0);

                intent.putExtra("broadcast", true);
                mContext.startActivity(intent);
            }
        });


        StringBuilder csvBuilder = new StringBuilder();

        for (ChatappContactModel city : mListData.get(position).getContactsModel()) {

            csvBuilder.append(city.getFirstName());

            csvBuilder.append(",");

        }


        String csv = csvBuilder.toString();
        csv = csv.substring(0, csv.length() - 1);

        vh2.newMessage.setText(csv);

    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return mListData.size();
    }
}
