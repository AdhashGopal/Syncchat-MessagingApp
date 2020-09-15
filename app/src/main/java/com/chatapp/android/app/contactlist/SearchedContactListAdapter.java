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
import android.widget.Filter;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.android.R;
import com.chatapp.android.app.MessageData;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.model.ContactSearchModel;
import com.chatapp.android.core.service.Constants;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


public class SearchedContactListAdapter extends RecyclerView.Adapter<SearchedContactListAdapter.MyViewHolder> {
    public ArrayList<ContactSearchModel> contactSearchModelArrayList;
    private Context context;
    private int blockedContactColor, unblockedContactColor;
    private ArrayList<ContactSearchModel> mListData;
    private Session session;
    private ContactItemClickListener listener;
    private String filteredText;
    private UserInfoSession userInfoSession;

    /**
     * Create constructor
     *
     * @param context The activity object inherits the Context object
     * @param data    arraylist  value
     */
    public SearchedContactListAdapter(Context context, ArrayList<ContactSearchModel> data) {
        this.context = context;
        this.contactSearchModelArrayList = data;
        session = new Session(context);
        userInfoSession = new UserInfoSession(context);
        blockedContactColor = ContextCompat.getColor(context, R.color.blocked_user_bg);
        unblockedContactColor = ContextCompat.getColor(context, R.color.white);
    }

    /**
     * getItem in model class
     *
     * @param position select specific value
     * @return value
     */
    public ContactSearchModel getItem(int position) {
        return contactSearchModelArrayList.get(position);

    }

    /**
     * updated arraylist value
     *
     * @param contactSearchModelArrayList updated value
     */
    public void updateInfo(ArrayList<ContactSearchModel> contactSearchModelArrayList) {
        this.contactSearchModelArrayList = contactSearchModelArrayList;
        notifyDataSetChanged();
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
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_contact_list_item, parent, false);

        return new MyViewHolder(itemView);
    }

    /**
     * binding view data
     *
     * @param viewHolder widget view
     * @param position   view holder position
     */
    @Override
    public void onBindViewHolder(MyViewHolder viewHolder, int position) {
        ContactSearchModel contact = contactSearchModelArrayList.get(position);
        viewHolder.tvStatus.setTextSize(11);
        viewHolder.tvName.setText(contact.getName());
        viewHolder.pos = position;


        try {
            if (contact.getFavouriteStatus() == 0) {
                viewHolder.isFav.setVisibility(View.GONE);
            } else {
                viewHolder.isFav.setVisibility(View.VISIBLE);
            }


            if (contact.getBlockStatus().equalsIgnoreCase("1")) {
                viewHolder.tvStatus.setVisibility(View.GONE);
            } else {
                if (TextUtils.isEmpty(contact.getUserStatus())) {
                    viewHolder.tvStatus.setVisibility(View.GONE);
                } else {
                    viewHolder.tvStatus.setText(decodeBase64(contact.getUserStatus()));
                }
            }

            if (contact.getBlockStatus().equalsIgnoreCase("1")) {
                //TODO tharani map
                Glide.with(context).load(R.drawable.personprofile).thumbnail(0.1f)
                        .dontAnimate()
                        .dontTransform()
                        .into(viewHolder.ivUser);
            } else {
                if (!TextUtils.isEmpty(contact.getAvatarImageUrl())) {
//                Picasso.with(context).load(Constants.SOCKET_IP + contact.getAvatarImageUrl()).error(R.drawable.personprofile).into(viewHolder.ivUser);
                    //TODO tharani map
                    Glide.with(context).load(Constants.SOCKET_IP + contact.getAvatarImageUrl()).thumbnail(0.1f)
                            .dontAnimate()
                            .error(R.drawable.personprofile)
                            .dontTransform()
                            .into(viewHolder.ivUser);
                } else {
                    //TODO tharani map
                    Glide.with(context).load(R.drawable.personprofile).thumbnail(0.1f)
                            .dontAnimate()
                            .dontTransform()
                            .into(viewHolder.ivUser);
//                Picasso.with(context).load(R.drawable.personprofile).into(viewHolder.ivUser);
                }
            }

           /* if (contact.getBlockStatus().equalsIgnoreCase("1")) {
                viewHolder.itemView.setBackgroundColor(blockedContactColor);
            } else {
                viewHolder.itemView.setBackgroundColor(unblockedContactColor);
            }*/
            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(context);
            if (contactDB_sqlite.getBlockedStatus(contact.getId(), false).equals("1")) {
                viewHolder.itemView.setBackgroundColor(blockedContactColor);
            } else {
                viewHolder.itemView.setBackgroundColor(unblockedContactColor);
            }

        } catch (Exception e) {
            e.printStackTrace();

        }


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

                    ArrayList<ContactSearchModel> FilteredArrList = new ArrayList<>();

                    if (mListData == null) {
                        mListData = new ArrayList<>(contactSearchModelArrayList); // saves the original data in mOriginalValues
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

                            String senderName = mListData.get(i).getName();
                            if (senderName.toLowerCase().contains(constraint)) {
                                FilteredArrList.add(mListData.get(i));
                                Log.e("Result-->", mListData.get(i) + "");
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
                    contactSearchModelArrayList = (ArrayList<ContactSearchModel>) results.values; // has the filtered values
                    if (contactSearchModelArrayList.size() == 0) {
                        EventBus.getDefault().post(new MessageData("nodatafound"));
                    } else {
                        EventBus.getDefault().post(new MessageData("datafound"));
                    }
                    notifyDataSetChanged();  // notifies the data with new filtered values
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        return filter;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        return contactSearchModelArrayList.size();
    }


    /**
     * ItemClickListener
     *
     * @param listener clicking action
     */
    public void setContactItemClickListener(ContactItemClickListener listener) {
        this.listener = listener;
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
     * ItemClickListener interface (onItemClick, onItemLongClick)
     */
    public interface ContactItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    /**
     * widgets view holder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public AvnNextLTProDemiTextView tvName, mobileText;
        public int pos;
        public ImageView isFav;
        protected EmojiconTextView tvStatus;
        protected CircleImageView ivUser;
        Typeface face2 = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        private ImageButton contactStatusIcon;

        public MyViewHolder(View view) {
            super(view);
            isFav = view.findViewById(R.id.img_fav);
            contactStatusIcon = view.findViewById(R.id.contact_status_icon);
            tvName = (AvnNextLTProDemiTextView) view.findViewById(R.id.userName_contacts);
            tvStatus = (EmojiconTextView) view.findViewById(R.id.status_contacts);


            ivUser = (CircleImageView) view.findViewById(R.id.userPhoto_contacts);
            mobileText = (AvnNextLTProDemiTextView) view.findViewById(R.id.mobileText);
            tvName.setTextColor(Color.parseColor("#3f3f3f"));

            tvStatus.setTextColor(Color.parseColor("#808080"));
            tvStatus.setTypeface(face2);
            tvStatus.setTextSize(11);
        }
    }

}



