package com.chatapp.android.app.adapter;

/**
 * created by  Adhash Team on 4/20/2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.chatapp.android.R;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.service.Constants;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


public class ForwardContactAdapter extends RecyclerView.Adapter<ForwardContactAdapter.MyViewHolder> implements Filterable, FastScrollRecyclerView.SectionedAdapter {
    private Context context;
    public ArrayList<ChatappContactModel> mDisplayedValues;
    private ArrayList<ChatappContactModel> mOriginalValues;
    Session session;
    private ChatListItemClickListener listener;
    Getcontactname getcontactname;
    ContactDB_Sqlite contactDB_sqlite;
    private UserInfoSession userInfoSession;
    private int blockedContactColor, unblockedContactColor;
    private String currentUserId = "";
    private String filteredText;

    /**
     * Create constructor
     *
     * @param context The activity object inherits the Context object
     * @param data    will be shown as list of value
     */
    public ForwardContactAdapter(Context context, ArrayList<ChatappContactModel> data) {
        this.context = context;
        this.mDisplayedValues = data;
        this.mOriginalValues = data;
        session = new Session(context);
        getcontactname = new Getcontactname(context);
        userInfoSession = new UserInfoSession(context);
        currentUserId = SessionManager.getInstance(context).getCurrentUserID();
        contactDB_sqlite = CoreController.getContactSqliteDBintstance(context);
        blockedContactColor = ContextCompat.getColor(context, R.color.blocked_user_bg);
        unblockedContactColor = ContextCompat.getColor(context, R.color.white);
    }

    /**
     * getting the specific value
     *
     * @param position select specific position
     * @return value
     */
    public ChatappContactModel getItem(int position) {
        return mDisplayedValues.get(position);
    }

    /**
     * get SectionName
     *
     * @param position select position
     * @return value
     */
    @NonNull
    @Override
    public String getSectionName(int position) {
        ChatappContactModel contact = mDisplayedValues.get(position);
        return contact.getFirstName().substring(0, 1);
    }

    /**
     * widgets view holder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {
        Typeface face2 = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        public AvnNextLTProDemiTextView tvName, mobileText;
        protected EmojiconTextView tvStatus;
        protected CircleImageView ivUser;
        public ImageView tick;
        public int pos;

        public MyViewHolder(View view) {
            super(view);
            tvName = (AvnNextLTProDemiTextView) view.findViewById(R.id.userName_contacts);
            tvStatus = (EmojiconTextView) view.findViewById(R.id.status_contacts);
            tick = (ImageView) view.findViewById(R.id.tick);

            ivUser = (CircleImageView) view.findViewById(R.id.userPhoto_contacts);
            mobileText = (AvnNextLTProDemiTextView) view.findViewById(R.id.mobileText);
            tvName.setTextColor(Color.parseColor("#3f3f3f"));

            tvStatus.setTextColor(Color.parseColor("#808080"));
            tvStatus.setTypeface(face2);
            tvStatus.setTextSize(11);
        }
    }

    /**
     * update the array list data
     *
     * @param aitem new data value
     */
    public void updateInfo(ArrayList<ChatappContactModel> aitem) {
        this.mDisplayedValues = aitem;
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
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.forward_contact_list, parent, false);

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
        ChatappContactModel contact = mDisplayedValues.get(position);
        viewHolder.tvStatus.setTextSize(11);
        viewHolder.mobileText.setText(contact.getType());
        viewHolder.tvName.setText(contact.getFirstName());
        viewHolder.pos = position;

        if (contact.isSelected()) {
            viewHolder.tick.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tick.setVisibility(View.GONE);
        }

        ContactDB_Sqlite contactDB = CoreController.getContactSqliteDBintstance(context);

        String getAvatarImageUrl = contactDB.getUserImage(contact.get_id());

        if (contact.isGroup()) {
            /*if (getAvatarImageUrl != null) {

                Glide.with(context)
                        .load(Constants.SOCKET_IP + getAvatarImageUrl)
                        .error(R.mipmap.group_chat_attachment_profile_icon)
                        .into(viewHolder.ivUser);
            } else {
                Glide.with(context).load(Constants.SOCKET_IP + getAvatarImageUrl).error(
                        R.mipmap.group_chat_attachment_profile_icon)
                        //.transform(new CircleTransform())
                        .into(viewHolder.ivUser);
                //vh2.userprofile.setBackgroundResource(R.mipmap.group_chat_attachment_profile_icon);
            }*/
            try {
                //String to = contact.get_id();
                String path = contact.getAvatarImageUrl();
                if (path != null && !path.equals("")) {
                    if (!path.contains(Constants.SOCKET_IP)) {
                        path = Constants.SOCKET_IP + path;
                    }
                    //RequestOptions options=new RequestOptions().error(R.mipmap.group_chat_attachment_profile_icon);
                    Glide.with(context).load(path)
                            .into(viewHolder.ivUser);
                } else {
                    Glide.with(context).load(R.mipmap.group_chat_attachment_profile_icon)
                            .into(viewHolder.ivUser);
                }
                // getcontactname.configProfilepic(viewHolder.ivUser, to, false, false, R.mipmap.chat_attachment_profile_default_image_frame);
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.with(context).load(R.mipmap.group_chat_attachment_profile_icon).into(viewHolder.ivUser);
            }
        } else {
           /* try {
                String userId = contact.get_id();
                if (!contact.getStatus().equals("")) {
                    Glide.with(context).load(getcontactname.getAvatarUrl(userId))
                            .error(R.mipmap.chat_attachment_profile_default_image_frame)
                            .into(viewHolder.ivUser);

                    //getcontactname.setProfileStatusText(viewHolder.tvStatus, userId, contact.getStatus(), false);
                } else {
                    viewHolder.tvStatus.setText(context.getResources().getString(R.string.status_not_available));
                }
                if (contactDB_sqlite.getBlockedStatus(userId, false).equals("1")) {
                    viewHolder.itemView.setBackgroundColor(blockedContactColor);
                } else {
                    viewHolder.itemView.setBackgroundColor(unblockedContactColor);
                }

            } catch (Exception e) {
                viewHolder.tvStatus.setText(context.getResources().getString(R.string.status_available));
            }*/

            try {
                //String to = contact.get_id();
                String path = contact.getAvatarImageUrl();
                if (path != null && !path.equals("")) {
                    if (!path.contains(Constants.SOCKET_IP)) {
                        path = Constants.SOCKET_IP + path;
                    }
                    //RequestOptions options=new RequestOptions().error(R.mipmap.group_chat_attachment_profile_icon);
                    Glide.with(context).load(path)
                            .into(viewHolder.ivUser);
                } else {
                    Glide.with(context).load(R.mipmap.chat_attachment_profile_default_image_frame)
                            .into(viewHolder.ivUser);
                }
                // getcontactname.configProfilepic(viewHolder.ivUser, to, false, false, R.mipmap.chat_attachment_profile_default_image_frame);
            } catch (Exception e) {
                e.printStackTrace();
                Picasso.with(context).load(R.drawable.personprofile).into(viewHolder.ivUser);
            }
        }
        Log.e("contact", contact.get_id() + " getAvatarImageUrl " + contact.getAvatarImageUrl() + " getGroupDocID " + contact.getGroupDocID() + " getMsisdn " + contact.getMsisdn() + " isGroup " + contact.isGroup());
        /**/
    }

    /**
     * getItemId
     *
     * @param position specific item
     * @return value
     */
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
        return mDisplayedValues.size();
    }


    /**
     * Filter the arraylist value
     *
     * @return specific value
     */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                try {
                    mDisplayedValues = (ArrayList<ChatappContactModel>) results.values; // has the filtered values
                    if (mDisplayedValues.size() == 0) {
//                    Toast.makeText(context, "No Contacts Matching Your Query...", Toast.LENGTH_SHORT).show();
                    }

                    notifyDataSetChanged();  // notifies the data with new filtered values
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                try {
                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<>(mDisplayedValues); // saves the original data in mOriginalValues
                    }
                    ArrayList<ChatappContactModel> FilteredArrList = new ArrayList<>();
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {


                            String contactName = mOriginalValues.get(i).getFirstName();
                            String contactNo = mOriginalValues.get(i).getNumberInDevice();

                            if (contactName.toLowerCase().contains(constraint) || contactNo.toLowerCase().contains(constraint)) {
                                FilteredArrList.add(mOriginalValues.get(i));
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
        };
        return filter;
    }


    /**
     * set ChatList ItemClickListener
     *
     * @param listener calling function
     */
    public void setChatListItemClickListener(ChatListItemClickListener listener) {
        this.listener = listener;
    }

    /**
     * ChatListItemClickListener interface (onItemClick, onItemLongClick)
     */
    public interface ChatListItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

}



