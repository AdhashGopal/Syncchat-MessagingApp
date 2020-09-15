package com.chatapp.android.app.contactlist;

import android.content.Context;
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

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.android.R;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.service.Constants;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 *
 */
public class AddGroupChatAdapter extends RecyclerView.Adapter<AddGroupChatAdapter.MyViewHolder> {

    Getcontactname getcontactname;
    private int blockedContactColor, unblockedContactColor;
    private Context context;
    private List<ChatappContactModel> mDisplayedValues;
    private List<ChatappContactModel> mOriginalValues;
    private List<ChatappContactModel> mListData;
    private Session session;
    private String filteredText;

    /**
     * Create constructor
     *
     * @param context The activity object inherits the Context object
     * @param data    arraylist value
     */
    public AddGroupChatAdapter(Context context, List<ChatappContactModel> data) {
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
     * updated arraylist value
     *
     * @param aitem updated value
     */
    public void updateInfo(List<ChatappContactModel> aitem) {
        this.mDisplayedValues = aitem;
        notifyDataSetChanged();
    }

    /**
     * decode Base64
     *
     * @param encodedStatus value of string data
     * @return value
     */
    private String decodeBase64(String encodedStatus) {
        return new String(Base64.decode(encodedStatus, Base64.DEFAULT));
    }

    /**
     * Filter the arraylist value
     *
     * @return filter value
     */
    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                try {

                    ArrayList<ChatappContactModel> FilteredArrList = new ArrayList<>();

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

                            String senderName = mListData.get(i).getFirstName();
                            if (senderName.toLowerCase().contains(constraint)) {
                                FilteredArrList.add(mListData.get(i));
                                Log.e("Result-->", mListData.get(i) + "");
                            }
                        }
                        /*MessageDbController db = CoreController.getDBInstance(mContext);
                        List<MessageItemChat> messages = db.getSearchMessages(constraint);
                        FilteredArrList.addAll(messages);*/

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
                    mDisplayedValues = (ArrayList<ChatappContactModel>) results.values; // has the filtered values

                    notifyDataSetChanged();  // notifies the data with new filtered values
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        return filter;
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

//        holder.tvName.setText(decodeBase64(contact.getFirstName()));

        String userId = contact.get_id();

        try {

            holder.tvName.setText(contact.getFirstName());
            /*if (contact.getStatus().contentEquals("")) {

                holder.tvStatus.setText(context.getResources().getString(R.string.status_not_available));
            } else if (userId != null && !userId.equals("")) {
                getcontactname.setProfileStatusText(holder.tvStatus, userId, contact.getStatus(), false);
            } else {
                holder.tvStatus.setText("");
            }*/
            if (contact.getFavouriteStatus() == 0) {
                holder.isFav.setVisibility(View.GONE);

            } else {
                holder.isFav.setVisibility(View.VISIBLE);
            }

            if (contact.getBlockStatus().equalsIgnoreCase("1")) {
                holder.tvStatus.setVisibility(View.GONE);

            } else {
                if (TextUtils.isEmpty(contact.getStatus())) {
                    holder.tvStatus.setVisibility(View.GONE);
                } else {

                    holder.tvStatus.setText(decodeBase64(contact.getStatus()));

//                getcontactname.setProfileStatusText(holder.tvStatus, userId, contact.getStatus(), false);
                }
            }
           /* if (TextUtils.isEmpty(contact.getStatus())) {
                holder.tvStatus.setVisibility(View.GONE);
            } else {

                holder.tvStatus.setText(decodeBase64(contact.getStatus()));

//                getcontactname.setProfileStatusText(holder.tvStatus, userId, contact.getStatus(), false);
            }*/
        } catch (Exception e) {
            holder.tvStatus.setText(context.getResources().getString(R.string.status_not_available));
        }

        try {
            if (contact.getBlockStatus().equalsIgnoreCase("1")) {
                //TODO tharani map
                Glide.with(context).load(R.drawable.personprofile).thumbnail(0.1f)
                        .dontAnimate()
                        .dontTransform()
                        .into(holder.ivUser);
            } else {
                if (!TextUtils.isEmpty(contact.getAvatarImageUrl())) {
//                Picasso.with(context).load(Constants.SOCKET_IP + contact.getAvatarImageUrl()).error(R.drawable.personprofile).into(holder.ivUser);
                    //TODO tharani map
                    Glide.with(context).load(Constants.SOCKET_IP + contact.getAvatarImageUrl()).thumbnail(0.1f)
                            .dontAnimate()
                            .error(R.drawable.personprofile)
                            .dontTransform()
                            .into(holder.ivUser);
                } else {
                    //TODO tharani map
                    Glide.with(context).load(R.drawable.personprofile).thumbnail(0.1f)
                            .dontAnimate()
                            .dontTransform()
                            .into(holder.ivUser);
                }
            }

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
     * getItem in model class
     *
     * @param position select specific value
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
     * widgets view holder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        protected TextView tvName;
        protected EmojiconTextView tvStatus;
        protected CircleImageView ivUser;
        CheckBox selected;
        private ImageView isFav;

        public MyViewHolder(View view) {
            super(view);
            Typeface face2 = CoreController.getInstance().getAvnNextLTProRegularTypeface();
            isFav = view.findViewById(R.id.img_fav);
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

}