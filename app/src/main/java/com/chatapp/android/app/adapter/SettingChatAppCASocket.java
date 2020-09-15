package com.chatapp.android.app.adapter;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.chatapp.android.R;
import com.chatapp.android.app.ContactRefreshListener;
import com.chatapp.android.app.SettingContact;
import com.chatapp.android.app.utils.ConnectivityInfo;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.service.Constants;
import com.chatapp.android.core.service.ServiceRequest;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 */
public class SettingChatAppCASocket extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable, FastScrollRecyclerView.SectionedAdapter {

    private SettingContact context;
    public List<ChatappContactModel> mDisplayedValues;
    private List<ChatappContactModel> mOriginalValues;
    Session session;
    private Getcontactname getcontactname;
    private ProgressDialog progressDialog;
    ContactRefreshListener contactRefreshListener;

    private static final String TAG = SettingChatAppCASocket.class.getSimpleName()+">>";
    private ChatListHeaderClickListener listener;

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;
    private final int QR_REQUEST_CODE = 25;

    public SettingChatAppCASocket(SettingContact context, List<ChatappContactModel> data, ContactRefreshListener contactRefreshListener) {
        Log.d(TAG, "ChatappCASocket: ");
        this.context = context;
        this.mDisplayedValues = data;
        this.mOriginalValues = data;
        session = new Session(context);
        getcontactname = new Getcontactname(context);
        this.contactRefreshListener = contactRefreshListener;
        setHasStableIds(true);

        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminateDrawable(ContextCompat.getDrawable(context, R.drawable.color_primary_progress_dialog));
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(true);
    }

    public ChatappContactModel getItem(int position) {

//        List<ChatappContactModel> values = new ArrayList<ChatappContactModel>();
//
//        for (int i = 0; i < mDisplayedValues.size(); i++) {
//            if(!mDisplayedValues.get(i).getRequestStatus().equals("0")){
//                values.add(mDisplayedValues.get(i));
//            }
//        }

        return mDisplayedValues.get(position);
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        ChatappContactModel contact = mDisplayedValues.get(position);
        return contact.getFirstName().substring(0, 1);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        Typeface face2 = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        TextView tvName,tvStatus,mobileText;

        CircleImageView ivUser;

        public ImageView tick,acceptReq,cancelReq;
        public int pos;

        public MyViewHolder(View view) {
            super(view);
            tvName = (TextView) view.findViewById(R.id.userName_contacts);
            tvStatus = (TextView) view.findViewById(R.id.status_contacts);
            tick = (ImageView) view.findViewById(R.id.tick);
            cancelReq = (ImageView) view.findViewById(R.id.request_cancel);
            acceptReq = (ImageView) view.findViewById(R.id.request_access);


            ivUser = (CircleImageView) view.findViewById(R.id.userPhoto_contacts);
            mobileText = (TextView) view.findViewById(R.id.mobileText);
            tvName.setTextColor(Color.parseColor("#3f3f3f"));

            tvStatus.setTextColor(Color.parseColor("#808080"));
            tvStatus.setTypeface(face2);
            tvStatus.setTextSize(13);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder{

        public RelativeLayout newGroup,newContact;

        public HeaderViewHolder(View itemView) {
            super(itemView);

            newGroup = (RelativeLayout)itemView.findViewById(R.id.create_group_layout);
            newContact = (RelativeLayout)itemView.findViewById(R.id.add_contact_layout);
        }
    }

    public void updateInfo(List<ChatappContactModel> aitem) {
        this.mDisplayedValues = aitem;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");

        if(viewType == TYPE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.contacts_list_view1, parent, false);

            return new MyViewHolder(itemView);
        }else{
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_contact_header, parent, false);

            return new HeaderViewHolder(itemView);
        }
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder,  int position) {
        Log.d(TAG, "onBindViewHolder: "+position);


//        List<ChatappContactModel> values = new ArrayList<ChatappContactModel>();
//
//        for (int i = 0; i < mDisplayedValues.size(); i++) {
//            if(!mDisplayedValues.get(i).getRequestStatus().equals("0")){
//                values.add(mDisplayedValues.get(i));
//            }
//        }

        if (viewHolder instanceof MyViewHolder) {

            position=position-1;

            final ChatappContactModel contact = mDisplayedValues.get(position);

            if (contact.getRequestStatus().equals("0")) {
//            viewHolder.mobileText.setText("Send Request");
                ((MyViewHolder) viewHolder).mobileText.setText("Send Request");

            } else if (contact.getRequestStatus().equals("2")) {

                ((MyViewHolder) viewHolder).mobileText.setVisibility(View.GONE);
                ((MyViewHolder) viewHolder).cancelReq.setVisibility(View.VISIBLE);
                ((MyViewHolder) viewHolder).acceptReq.setVisibility(View.VISIBLE);

            } else if (contact.getRequestStatus().equals("1")) {
                ((MyViewHolder) viewHolder).mobileText.setText("Cancel Request");

            } else {

                ((MyViewHolder) viewHolder).mobileText.setVisibility(View.GONE);

            }


            ((MyViewHolder) viewHolder).mobileText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (((MyViewHolder) viewHolder).mobileText.getText().toString().equals("Send Request")) {

                        progressDialog.show();

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("from", SessionManager.getInstance(context).getPhoneNumberOfCurrentUser());
                        params.put("to", contact.getMsisdn());
                        params.put("status", "1");

                        ServiceRequest request = new ServiceRequest(context);
                        request.makeServiceRequest(Constants.CONTACT_REQUEST_SENT, Request.Method.POST, params, verifcationListener);
                    } else {
                        progressDialog.show();

                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("from", SessionManager.getInstance(context).getPhoneNumberOfCurrentUser());
                        params.put("to", contact.getMsisdn());
                        params.put("status", "0");

                        ServiceRequest request = new ServiceRequest(context);
                        request.makeServiceRequest(Constants.CONTACT_REQUEST_SENT, Request.Method.POST, params, verifcationListener);
                    }

                }
            });

            ((MyViewHolder) viewHolder).acceptReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("from", SessionManager.getInstance(context).getPhoneNumberOfCurrentUser());
                    params.put("to", contact.getMsisdn());
                    params.put("status", "3");

                    ServiceRequest request = new ServiceRequest(context);
                    request.makeServiceRequest(Constants.CONTACT_REQUEST_SENT, Request.Method.POST, params, verifcationListener);
                }
            });

            ((MyViewHolder) viewHolder).cancelReq.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    progressDialog.show();

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("from", SessionManager.getInstance(context).getPhoneNumberOfCurrentUser());
                    params.put("to", contact.getMsisdn());
                    params.put("status", "0");

                    ServiceRequest request = new ServiceRequest(context);
                    request.makeServiceRequest(Constants.CONTACT_REQUEST_SENT, Request.Method.POST, params, verifcationListener);
                }
            });

            ((MyViewHolder) viewHolder).tvStatus.setTextSize(13);
//        viewHolder.mobileText.setText(contact.getType());
            ((MyViewHolder) viewHolder).tvName.setText(contact.getFirstName());
            ((MyViewHolder) viewHolder).pos = position;


            try {
                String userId = contact.get_id();
                if (!contact.getStatus().contentEquals("")) {
                   if(!getcontactname.setProfileStatusText(((MyViewHolder) viewHolder).tvStatus, userId, contact.getStatus(), false))
                   {
                       ((MyViewHolder) viewHolder).tvStatus.setVisibility(View.GONE);
                       RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                       params.addRule(RelativeLayout.CENTER_VERTICAL);

                       ((MyViewHolder) viewHolder).tvName.setLayoutParams(params);

                   }
                   else
                   {
                       ((MyViewHolder) viewHolder).tvStatus.setVisibility(View.VISIBLE);
                       RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                       params.removeRule(RelativeLayout.CENTER_VERTICAL);

                       ((MyViewHolder) viewHolder).tvName.setLayoutParams(params);
                   }
                } else {
                    ((MyViewHolder) viewHolder).tvStatus.setText(context.getResources().getString(R.string.status_not_available));
                }
            } catch (Exception e) {
                ((MyViewHolder) viewHolder).tvStatus.setText(context.getResources().getString(R.string.status_available));
            }

            try {
                final String to = contact.get_id();
                System.out.println("MissedAvatar" + "" + contact.getAvatarImageUrl());
                ((MyViewHolder) viewHolder).ivUser.post(new Runnable() {
                    @Override
                    public void run() {
                        getcontactname.configProfilepic(((MyViewHolder) viewHolder).ivUser, to, false, true, R.mipmap.chat_attachment_profile_default_image_frame);

                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{

            ((HeaderViewHolder) viewHolder).newGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, "Group", Toast.LENGTH_SHORT).show();
                    listener.onItemHeaderClick(1);
//                    Intent i = new Intent(context, SelectPeopleForGroupChat.class);
//                    i.putExtra("type","grp");
//                    context.startActivity(i);
                }
            });

            ((HeaderViewHolder) viewHolder).newContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ConnectivityInfo.isInternetConnected(context)) {
                        listener.onItemHeaderClick(0);

//                        Intent qrIntent = new Intent(context, QRCodeScan.class);
//                        context.startActivityForResult(qrIntent, QR_REQUEST_CODE);
                    } else {
                        Toast.makeText(context, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
//        List<ChatappContactModel> values = new ArrayList<ChatappContactModel>();

//        for (int i = 0; i < mDisplayedValues.size(); i++) {
//            if(!mDisplayedValues.get(i).getRequestStatus().equals("0")){
//                values.add(mDisplayedValues.get(i));
//            }
//        }

        return mDisplayedValues.size()+1;
    }

    @Override
    public int getItemViewType(int position) {

        if(isPositionHeader(position)){
            return TYPE_HEADER;
        }
        return TYPE_ITEM;
    }


    private boolean isPositionHeader(int position) {
        return position == 0;
    }

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

                            mycontact.set_id(mOriginalValues.get(i).get_id());
                            mycontact.setStatus(mOriginalValues.get(i).getStatus());
                            mycontact.setAvatarImageUrl(mOriginalValues.get(i).getAvatarImageUrl());
                            mycontact.setNumberInDevice(mOriginalValues.get(i).getNumberInDevice());
                            mycontact.setRequestStatus(mOriginalValues.get(i).getRequestStatus());

                            FilteredArrList.add(mycontact);
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



    public void setChatListHeaderListener(ChatListHeaderClickListener listener) {
        this.listener = listener;
    }

    public interface ChatListHeaderClickListener {
        void onItemHeaderClick(int position);


    }


    private ServiceRequest.ServiceListener verifcationListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            progressDialog.hide();

            try {

                JSONObject jsonObject = new JSONObject(response);
                if(jsonObject.has("message")) {
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
                if(jsonObject.getString("errNum").equals("0")){

                    contactRefreshListener.onSuccess();
                }else if(jsonObject.getString("errNum").equals("1")){

                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onErrorListener(int state) {
            progressDialog.hide();

        }
    };


}