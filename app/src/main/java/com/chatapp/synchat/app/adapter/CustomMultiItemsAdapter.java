package com.chatapp.synchat.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.core.model.MultiTextDialogPojo;

import java.util.List;

/**
 * created by  Adhash Team on 2/16/2017.
 */
public class CustomMultiItemsAdapter extends RecyclerView.Adapter<CustomMultiItemsAdapter.MultiItemsViewHolder> {

    private List<MultiTextDialogPojo> dataList;
    private Context mContext;

    /**
     * Create constructor
     *
     * @param mContext The activity object inherits the Context object
     * @param dataList list of value
     */
    public CustomMultiItemsAdapter(Context mContext, List<MultiTextDialogPojo> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    /**
     * widgets view holder
     */
    public class MultiItemsViewHolder extends RecyclerView.ViewHolder {

        public ImageView ivLabelIcon;
        public AvnNextLTProRegTextView tvLabel;
        public View dividerLine;

        public MultiItemsViewHolder(View itemView) {
            super(itemView);

            ivLabelIcon = (ImageView) itemView.findViewById(R.id.ivLabelIcon);
            tvLabel = (AvnNextLTProRegTextView) itemView.findViewById(R.id.tvLabel);
            dividerLine = itemView.findViewById(R.id.dividerLine);
        }
    }

    /**
     * layout binding
     *
     * @param parent   layout view group
     * @param viewType
     * @return view holder
     */
    @Override
    public MultiItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.adapter_multi_items, parent, false);
        MultiItemsViewHolder holder = new MultiItemsViewHolder(view);
        return holder;
    }

    /**
     * binding view data
     *
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(MultiItemsViewHolder holder, int position) {

        MultiTextDialogPojo data = dataList.get(position);

        holder.tvLabel.setText(data.getLabelText());

        // make image view visibility and set resource if required
        holder.ivLabelIcon.setVisibility(View.GONE);
        if (data.getImageResource() != null) {
            holder.ivLabelIcon.setVisibility(View.VISIBLE);
            holder.ivLabelIcon.setImageResource(data.getImageResource());
        }

        int lastItem = dataList.size() - 1;
        if (lastItem == position) {
            holder.dividerLine.setVisibility(View.GONE);
        }
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return dataList.size();
    }


}
