package com.chatapp.android.app.calls;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.utils.RecyclerViewItemClickListener;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.model.ChatappContactModel;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.List;


/**
 * created by  Adhash Team on 7/28/2017.
 */
public class CallContactAdapter extends RecyclerView.Adapter<CallContactAdapter.MyViewHolder> implements Filterable, FastScrollRecyclerView.SectionedAdapter {

    public List<ChatappContactModel> mDisplayedValues;
    private Context context;
    private List<ChatappContactModel> mOriginalValues;
    private Session session;
    private Getcontactname getcontactname;
    private String myid;
    private RecyclerViewItemClickListener myListener;

    /**
     * Create constructor
     *
     * @param context The activity object inherits the Context object
     * @param data    arraylist of value
     */
    public CallContactAdapter(Context context, List<ChatappContactModel> data) {
        this.context = context;
        this.mDisplayedValues = data;
        this.mOriginalValues = data;
        session = new Session(context);
        getcontactname = new Getcontactname(context);
        myid = SessionManager.getInstance(context).getCurrentUserID();
        setHasStableIds(true);
    }

    /**
     * get specific item value from arraylist
     *
     * @param position select item
     * @return value
     */
    public ChatappContactModel getItem(int position) {
        return mDisplayedValues.get(position);
    }

    /**
     * get Section Name
     *
     * @param position get specific selected user name
     * @return value
     */
    @NonNull
    @Override
    public String getSectionName(int position) {
        ChatappContactModel contact = mDisplayedValues.get(position);
        return contact.getFirstName().substring(0, 1);
    }

    /**
     * update new data to store arraylist
     *
     * @param aitem updated data
     */
    public void updateInfo(List<ChatappContactModel> aitem) {
        this.mDisplayedValues = aitem;
        this.mOriginalValues = aitem;
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
                .inflate(R.layout.layout_inflater_call_contacts_items, parent, false);

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
        setItemClickListener(holder, position);
        ChatappContactModel contact = mDisplayedValues.get(position);
        holder.tvStatus.setTextSize(13);
        holder.tvName.setText(contact.getFirstName());
        holder.tvStatus.setText(contact.getType());
        holder.pos = position;

        try {
            String to = contact.get_id();
            getcontactname.configProfilepic(holder.ivUser, to, false, false, R.mipmap.chat_attachment_profile_default_image_frame);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (contact.getRequestStatus().equals("3")) {
            holder.myAudioCallIMG.setVisibility(View.VISIBLE);
            holder.myVideoCallIMG.setVisibility(View.VISIBLE);
        }
    }

    /**
     * ItemClick Listener
     * @param listener click action
     */
    public void setCallContactsItemClickListener(RecyclerViewItemClickListener listener) {
        this.myListener = listener;
    }

    private void setItemClickListener(final MyViewHolder vh2, final int position) {
        if (myListener != null) {

            vh2.myAudioCallIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myListener.onRVItemClick(vh2.myAudioCallIMG, position);
                }
            });

            vh2.myVideoCallIMG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myListener.onRVItemClick(vh2.myVideoCallIMG, position);

                }
            });

        }
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
        return mDisplayedValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    /**
     * Filter the arraylist value
     * @return filter result
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


                    ArrayList<ChatappContactModel> FilteredArrList = new ArrayList<>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<>(mDisplayedValues); // saves the original data in mOriginalValues
                    }

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
                                //createnewgroupChatappSocketModel(FilteredArrList);
                                ChatappContactModel mycontact = new ChatappContactModel();
                                mycontact.setFirstName(mOriginalValues.get(i).getFirstName());
                                mycontact.setRequestStatus(mOriginalValues.get(i).getRequestStatus());
                                mycontact.set_id(mOriginalValues.get(i).get_id());
                                mycontact.setStatus(mOriginalValues.get(i).getStatus());
                                mycontact.setAvatarImageUrl(mOriginalValues.get(i).getAvatarImageUrl());
                                mycontact.setNumberInDevice(mOriginalValues.get(i).getNumberInDevice());
                                mycontact.setType(mOriginalValues.get(i).getType());
                                FilteredArrList.add(mycontact);
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
     * widgets view holder
     */
    public class MyViewHolder extends RecyclerView.ViewHolder {

        public AvnNextLTProDemiTextView tvName;
        public ImageView tick;
        public int pos;
        public ImageView myAudioCallIMG, myVideoCallIMG;
        protected AvnNextLTProRegTextView tvStatus;
        protected CircleImageView ivUser;

        public MyViewHolder(View view) {
            super(view);
            tvName = (AvnNextLTProDemiTextView) view.findViewById(R.id.layout_inflater_contacts_items_uNameTXT);
            tvStatus = (AvnNextLTProRegTextView) view.findViewById(R.id.layout_inflater_contacts_items_devstatusTXT);
            myAudioCallIMG = (ImageView) view.findViewById(R.id.layout_inflater_contacts_items_callIMG);
            myVideoCallIMG = (ImageView) view.findViewById(R.id.layout_inflater_contacts_items_videocallIMG);
            ivUser = (CircleImageView) view.findViewById(R.id.layout_inflater_contacts_items_userPhoto_contacts);

            tvName.setTextColor(Color.parseColor("#3f3f3f"));
            tvStatus.setTextColor(Color.parseColor("#808080"));
            Typeface face2 = CoreController.getInstance().getAvnNextLTProRegularTypeface();
            tvStatus.setTypeface(face2);
            tvStatus.setTextSize(13);
        }
    }


}