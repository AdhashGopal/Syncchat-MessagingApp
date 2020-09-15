package com.chatapp.android.app.adapter;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.android.R;
import com.chatapp.android.app.MemberContactSearch;
import com.chatapp.android.app.MessageData;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.app.widget.CircleTransform;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.ContactSearchModel;
import com.chatapp.android.core.model.GroupMembersPojo;
import com.chatapp.android.core.service.Constants;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class GroupPeopleSearch extends RecyclerView.Adapter<GroupPeopleSearch.ViewHolder> {
    private MemberContactSearch context;
    private List<GroupMembersPojo> mDisplayedValues;
    private List<GroupMembersPojo> mListData;
    Session sessionmanager;
    Getcontactname getcontactname;
    private String filteredText;
    private int blockedContactColor, unblockedContactColor;


    /**
     * Create constructor
     *
     * @param memberContactSearch The activity object inherits the Context object
     * @param userlist            list of value will be shown
     */
    public GroupPeopleSearch(MemberContactSearch memberContactSearch, List<GroupMembersPojo> userlist) {
        this.context = memberContactSearch;
        this.mDisplayedValues = userlist;
        this.mListData = userlist;
        sessionmanager = new Session(context);
        getcontactname = new Getcontactname(context);

        blockedContactColor = ContextCompat.getColor(context, R.color.blocked_user_bg);
        unblockedContactColor = ContextCompat.getColor(context, R.color.white);
    }

    /**
     * layout binding
     *
     * @param parent   layout view group
     * @param viewType
     * @return view holder
     */
    @NonNull
    @Override
    public GroupPeopleSearch.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.group_item_selected, parent, false);
        return new GroupPeopleSearch.ViewHolder(itemView);
    }

    /**
     * binding view data
     *
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(@NonNull GroupPeopleSearch.ViewHolder holder, int position) {
        //        if (position == 0 || position > 0) {
        final GroupMembersPojo contact = mDisplayedValues.get(position);

//        holder.tvName.setText(decodeBase64(contact.getFirstName()));

        String userId = contact.getUserId();
        Log.e("userdp", contact.getUserDp());

        try {
            holder.tvName.setText(contact.getContactName());
            holder.tvStatus.setText(contact.getStatus());
            Glide.with(context).load(contact.getUserDp()).thumbnail(0.1f)
                    .dontAnimate()
                    .error(R.drawable.personprofile)
                    .dontTransform()
                    .into(holder.ivUser);

        } catch (Exception e) {
            holder.tvStatus.setText(context.getResources().getString(R.string.status_not_available));
        }
    }

    /**
     * filgter the arraylist value
     *
     * @return select specific value
     */
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                try {

                    ArrayList<GroupMembersPojo> FilteredArrList = new ArrayList<>();

                    if (mListData == null) {
                        mListData = new ArrayList<>(mDisplayedValues); // saves the original data in mOriginalValues
                    }

                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mListData.size();
                        results.values = mListData;
                        filteredText = null;

                    } else {
                        filteredText = constraint.toString();
                        constraint = constraint.toString().toLowerCase();

                        for (int i = 0; i < mListData.size(); i++) {

                            String senderName = mListData.get(i).getContactName();
                            if (senderName.toLowerCase().contains(constraint)) {
                                FilteredArrList.add(mListData.get(i));
                                Log.e("Result-->", mListData.get(i).getContactName() + "");
                            }
                        }


                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                try {
                    mDisplayedValues = (ArrayList<GroupMembersPojo>) results.values; // has the filtered values

                    notifyDataSetChanged();  // notifies the data with new filtered values
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        return filter;
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        if (mDisplayedValues == null)
            return 0;
        return mDisplayedValues.size();
    }

    /**
     * select value from arraylist
     *
     * @param position select specific value
     * @return value
     */
    public GroupMembersPojo getItem(int position) {
        return mDisplayedValues.get(position);
    }

    /**
     * widgets view holder
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvName;
        protected EmojiconTextView tvStatus;
        protected CircleImageView ivUser;
        CheckBox selected;
        private ImageView isFav;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            isFav = itemView.findViewById(R.id.img_fav);
            tvName = (TextView) itemView.findViewById(R.id.userName_contacts);
            tvStatus = (EmojiconTextView) itemView.findViewById(R.id.status_contacts);
            selected = (CheckBox) itemView.findViewById(R.id.selectedmember);
            ivUser = (CircleImageView) itemView.findViewById(R.id.userPhoto_contacts);
            selected = (CheckBox) itemView.findViewById(R.id.selectedmember);
            tvName.setTextColor(Color.parseColor("#3f3f3f"));
            tvStatus.setTextColor(Color.parseColor("#808080"));
        }
    }
}
