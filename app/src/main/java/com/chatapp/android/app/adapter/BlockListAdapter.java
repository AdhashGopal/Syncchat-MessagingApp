package com.chatapp.android.app.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;

import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.model.ChatappContactModel;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * created by  Adhash Team on 3/7/2017.
 */
public class BlockListAdapter extends RecyclerView.Adapter<BlockListAdapter.MyViewHolder> {

    private Context context;
    private Getcontactname getcontactname;
    private ItemFilter mFilter = new ItemFilter();

    private List<ChatappContactModel> mListData = new ArrayList<>();

    /**
     * widgets view holder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AvnNextLTProDemiTextView title;
        public EmojiconTextView user_status;
        public CircleImageView userImage;

        public MyViewHolder(View view) {
            super(view);
            title = (AvnNextLTProDemiTextView) view.findViewById(R.id.userName_block);
            user_status = (EmojiconTextView) view.findViewById(R.id.user_status);
            userImage = (CircleImageView) view.findViewById(R.id.userImage);

            Typeface tf = CoreController.getInstance().getAvnNextLTProRegularTypeface();
            user_status.setTypeface(tf);
        }
    }

    /**
     * get model class item
     *
     * @param position get position of value
     * @return value
     */
    public ChatappContactModel getItem(int position) {
        return mListData.get(position);
    }

    /**
     * Create constructor
     * @param context   The activity object inherits the Context object
     * @param mListData list of value
     */
    public BlockListAdapter(Context context, List<ChatappContactModel> mListData) {
        this.context = context;
        this.mListData = mListData;
        getcontactname = new Getcontactname(context);
    }

    /**
     * update the list data
     *
     * @param aitem array list data
     */
    public void updateInfo(ArrayList<ChatappContactModel> aitem) {
        this.mListData = aitem;
        notifyDataSetChanged();
    }

    /**
     * filter the array value
     *
     * @return filter value
     */
    public Filter getFilter() {
        return mFilter;
    }

    /**
     * Filter implementation
     */
    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String filterString = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            final ArrayList<ChatappContactModel> nlist = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                results.values = mListData;
                results.count = mListData.size();
                System.out.println("nlist size in if part" + results.count);
            } else {
                for (int i = 0; i < mListData.size(); i++) {
                    ChatappContactModel item = mListData.get(i);
                    Log.e("fgfg", filterString);
                    if (item.getFirstName().toLowerCase().contains(filterString)) {

                        nlist.add(item);
                    }
                }
                results.values = nlist;
                results.count = nlist.size();

            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            mListData = (ArrayList<ChatappContactModel>) results.values;
            notifyDataSetChanged();
               /* ArrayList<MessageItemChat> filtered = (ArrayList<MessageItemChat>) results.values;
                notifyDataSetChanged();*/
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
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.block_user_from_list, parent, false);

        return new MyViewHolder(itemView);
    }

    /**
     * binding view data
     *
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        ChatappContactModel chatappContactModel = mListData.get(position);
        holder.title.setText(chatappContactModel.getFirstName());
        holder.user_status.setText(chatappContactModel.getStatus());

        getcontactname.configProfilepic(holder.userImage, chatappContactModel.get_id(), false,
                false, R.mipmap.chat_attachment_profile_default_image_frame);
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return this.mListData.size();
    }
}
