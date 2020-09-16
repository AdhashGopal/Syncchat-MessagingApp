package com.chatapp.synchat.app.adapter;

import android.app.ProgressDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.ContactRefreshListener;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.app.widget.CircleImageView;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.service.ServiceRequest;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 *
 */
public class ChatappCASocket extends RecyclerView.Adapter<ChatappCASocket.MyViewHolder> implements Filterable, FastScrollRecyclerView.SectionedAdapter {
    private static final String TAG = ChatappCASocket.class.getSimpleName() + ">>";
    public List<ChatappContactModel> mDisplayedValues;
    Session session;
    ContactRefreshListener contactRefreshListener;
    private Context context;
    private List<ChatappContactModel> mOriginalValues;
    private Getcontactname getcontactname;
    private ProgressDialog progressDialog;
    private ChatListItemClickListener listener;
    private ServiceRequest.ServiceListener verifcationListener = new ServiceRequest.ServiceListener() {
        @Override
        public void onCompleteListener(String response) {
            progressDialog.hide();

            try {

                JSONObject jsonObject = new JSONObject(response);
                if (jsonObject.has("message")) {
                    Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                }
                if (jsonObject.getString("errNum").equals("0")) {

                    contactRefreshListener.onSuccess();
                } else if (jsonObject.getString("errNum").equals("1")) {

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

    public ChatappCASocket(Context context, List<ChatappContactModel> data, ContactRefreshListener contactRefreshListener) {
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

    @Override
    public String getSectionName(int position) {
        ChatappContactModel contact = mDisplayedValues.get(position);
        return contact.getFirstName().substring(0, 1);
    }

    public void updateInfo(List<ChatappContactModel> aitem) {
        this.mDisplayedValues = aitem;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contacts_list_view, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder viewHolder, final int position) {
        Log.d(TAG, "onBindViewHolder: " + position);

//        List<ChatappContactModel> values = new ArrayList<ChatappContactModel>();
//
//        for (int i = 0; i < mDisplayedValues.size(); i++) {
//            if(!mDisplayedValues.get(i).getRequestStatus().equals("0")){
//                values.add(mDisplayedValues.get(i));
//            }
//        }

        final ChatappContactModel contact = mDisplayedValues.get(position);

        if (contact.getRequestStatus().equals("0")) {
            viewHolder.mobileText.setText("Send Request");

        } else if (contact.getRequestStatus().equals("2")) {

            viewHolder.mobileText.setVisibility(View.GONE);
            viewHolder.cancelReq.setVisibility(View.VISIBLE);
            viewHolder.acceptReq.setVisibility(View.VISIBLE);

        } else if (contact.getRequestStatus().equals("1")) {
            viewHolder.mobileText.setText("Cancel Request");

        } else {

            viewHolder.mobileText.setVisibility(View.GONE);

        }

//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if(contact.getRequestStatus().equals("3")) {
//                    if (listener != null) {
//                        listener.onItemClick(viewHolder.itemView, position);
//                    }
//                }
//            }
//        });

//        String webAddress = "http://example.com/api/helprequests/";
//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        JSONObject object = new JSONObject();
//        try {
//            object.put("Title", "my title");
//            object.put("Message", "my message");
//        } catch (JSONException e) {
//        }
//
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, webAddress,object, new Response.Listener<JSONObject>() {
//
//            @Override
//            public void onResponse(JSONObject object) {
//                Log.d("RESPONSE", object.toString());
//            }
//
//        }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//                Log.d("RESPONSE", "That didn't work!");
//            }
//
//        });
//        queue.add(request);


        viewHolder.mobileText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (viewHolder.mobileText.getText().toString().equals("Send Request")) {

                    progressDialog.show();

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("from", SessionManager.getInstance(context).getPhoneNumberOfCurrentUser());
                    params.put("to", contact.getMsisdn());
                    params.put("status", "1");

                    System.out.println("===m" + contact.getMsisdn());
                    System.out.println("===s" + SessionManager.getInstance(context).getPhoneNumberOfCurrentUser());

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

        viewHolder.acceptReq.setOnClickListener(new View.OnClickListener() {
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

        viewHolder.cancelReq.setOnClickListener(new View.OnClickListener() {
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

        viewHolder.tvStatus.setTextSize(13);
//        viewHolder.mobileText.setText(contact.getType());
        viewHolder.tvName.setText(contact.getFirstName());
        viewHolder.pos = position;


        try {
            String userId = contact.get_id();
            if (!contact.getStatus().contentEquals("")) {
                getcontactname.setProfileStatusText(viewHolder.tvStatus, userId, contact.getStatus(), false);
            } else {
                viewHolder.tvStatus.setText(context.getResources().getString(R.string.status_not_available));
            }
        } catch (Exception e) {
            viewHolder.tvStatus.setText(context.getResources().getString(R.string.status_available));
        }

        try {
            final String to = contact.get_id();
            System.out.println("MissedAvatar" + "" + contact.getAvatarImageUrl());
            viewHolder.ivUser.post(new Runnable() {
                @Override
                public void run() {
                    getcontactname.configProfilepic(viewHolder.ivUser, to, false, true, R.mipmap.chat_attachment_profile_default_image_frame);

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
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

        return mDisplayedValues.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

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
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return results;
            }
        };
        return filter;
    }


    public void setChatListItemClickListener(ChatListItemClickListener listener) {
        this.listener = listener;
    }

    public interface ChatListItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView tick, acceptReq, cancelReq;
        public int pos;
        Typeface face2 = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        TextView tvName, tvStatus, mobileText;
        CircleImageView ivUser;

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


}