package com.chatapp.android.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.model.ChatappContactModel;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 *
 */
public class SNGAdapter extends RecyclerView.Adapter<SNGAdapter.MyViewHolder>
        implements Filterable {

    private Context context;
    private List<ChatappContactModel> mDisplayedValues;
    private List<ChatappContactModel> mOriginalValues;
    private List<ChatappContactModel> mListData = new ArrayList<>();
    private Session session;
    Getcontactname getcontactname;
    private int blockedContactColor, unblockedContactColor;


    /**
     * widgets view holder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvName;
        protected EmojiconTextView tvStatus;
        protected CircleImageView ivUser;
        CheckBox selected;

        public MyViewHolder(View view) {
            super(view);
            Typeface face2 = CoreController.getInstance().getAvnNextLTProRegularTypeface();
            tvName = (TextView) view.findViewById(R.id.userName_contacts);
            tvStatus = (EmojiconTextView) view.findViewById(R.id.status_contacts);
            selected = (CheckBox) view.findViewById(R.id.selectedmember);
            ivUser = (CircleImageView) view.findViewById(R.id.userPhoto_contacts);
            selected = (CheckBox) view.findViewById(R.id.selectedmember);
            tvName.setTextColor(Color.parseColor("#3f3f3f"));
            tvStatus.setTypeface(face2);
            tvStatus.setTextColor(Color.parseColor("#808080"));

        }
    }

    /**
     * updated value
     *
     * @param aitem update arraylist value
     */
    public void updateInfo(List<ChatappContactModel> aitem) {
        this.mDisplayedValues = aitem;
        notifyDataSetChanged();
    }

    /**
     * binding view data
     *
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

//        if (position == 0 || position > 0) {
        final ChatappContactModel contact = mDisplayedValues.get(position);
        holder.tvName.setText(contact.getFirstName());

        String userId = contact.get_id();

        try {
            if (contact.getStatus().contentEquals("")) {
                /* Default Status of each user */
                holder.tvStatus.setText(context.getResources().getString(R.string.status_not_available));
            } else if (userId != null && !userId.equals("")) {
                getcontactname.setProfileStatusText(holder.tvStatus, userId, contact.getStatus(), false);
            } else {
                holder.tvStatus.setText("");
            }
        } catch (Exception e) {
            holder.tvStatus.setText(context.getResources().getString(R.string.status_not_available));
        }

        try {
            String to = contact.get_id();
            getcontactname.configProfilepic(holder.ivUser, to, false, false, R.mipmap.chat_attachment_profile_default_image_frame);
        } catch (Exception e) {
            e.printStackTrace();

        }

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(context);
        if (contactDB_sqlite.getBlockedStatus(userId, false).equals("1")) {
            holder.itemView.setBackgroundColor(blockedContactColor);
        } else {
            holder.itemView.setBackgroundColor(unblockedContactColor);
        }

    }

    /**
     * Create constructor
     *
     * @param context The activity object inherits the Context object
     * @param data    list of value
     */
    public SNGAdapter(Context context, List<ChatappContactModel> data) {
        this.context = context;
        this.mDisplayedValues = data;
        this.mOriginalValues = data;
        this.mListData = data;
        session = new Session(context);
        getcontactname = new Getcontactname(context);

        blockedContactColor = ContextCompat.getColor(context, R.color.blocked_user_bg);
        unblockedContactColor = ContextCompat.getColor(context, R.color.white);
    }

    /**
     * get specific item data
     *
     * @param position select specific item data
     * @return value
     */
    public ChatappContactModel getItem(int position) {
        return mDisplayedValues.get(position);
    }

    public List<ChatappContactModel> getDisplayedList(int position) {
        return mDisplayedValues;
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
                .inflate(R.layout.group_item_selected, parent, false);
        return new MyViewHolder(itemView);
    }


    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return mDisplayedValues.size();
    }


    /**
     * filter the arraylist value
     *
     * @return filter value
     */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                mDisplayedValues = (ArrayList<ChatappContactModel>) results.values; // has the filtered values
                if (mDisplayedValues.size() == 0) {
//                    Toast.makeText(context, "No Contacts Matching Your Query...", Toast.LENGTH_SHORT).show();
                }

                notifyDataSetChanged();  // notifies the data with new filtered values
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<ChatappContactModel> FilteredArrList = new ArrayList<>();

                if (mListData == null) {
                    mListData = new ArrayList<>(mDisplayedValues); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mListData.size();
                    results.values = mListData;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mListData.size(); i++) {

                        String name = mListData.get(i).getFirstName();
                        String msisdn = mListData.get(i).getMsisdn();
                        if (name.toLowerCase().contains(constraint) || msisdn.contains(constraint)) {
                            FilteredArrList.add(mListData.get(i));
                        }


                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;
                }
                return results;
            }
        };
        return filter;
    }


}