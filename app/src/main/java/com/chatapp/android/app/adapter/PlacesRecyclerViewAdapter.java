package com.chatapp.android.app.adapter;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.chatapp.android.R;
import com.chatapp.android.app.GoogleMapData;
import com.chatapp.android.app.GoogleMapView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.recyclerview.widget.RecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class PlacesRecyclerViewAdapter extends RecyclerView.Adapter<PlacesRecyclerViewAdapter.ViewHolder> {
    private List<HashMap<String, String>> mPlacesList;
    private Context context;
    String name;
    List<Address> addresses;
    Geocoder geocoder;
    String address = "";

    /**
     * Create constructor
     *
     * @param googleMapView The activity object inherits the Context object
     */
    public PlacesRecyclerViewAdapter(GoogleMapView googleMapView) {
        mPlacesList = new ArrayList<>();
        context = googleMapView;
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
    public PlacesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.places_item, parent, false);

        PlacesRecyclerViewAdapter.ViewHolder viewHolder =
                new PlacesRecyclerViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    /**
     * binding view data
     *
     * @param holder   widget view
     * @param position view holder position
     */
    @Override
    public void onBindViewHolder(@NonNull PlacesRecyclerViewAdapter.ViewHolder holder, final int position) {
        HashMap<String, String> hmPlace = new HashMap<>();
        hmPlace = mPlacesList.get(position);
        final String s = mPlacesList.get(position).get("place_name");
        holder.mPlacename.setText(s);

        final double lat = Double.parseDouble(mPlacesList.get(position).get("lat"));
        // Getting longitude of the place
        final double lng = Double.parseDouble(mPlacesList.get(position).get("lng"));
        geocoder = new Geocoder(context, Locale.getDefault());


        holder.mPlacename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LatLng location = new LatLng(lat, lng);
                EventBus.getDefault().post(location);
            }
        });


       /* holder.address.setText(place.getAddress());
        holder.phone.setText(place.getPhoneNumber());
        if (place.getWebsiteUri() != null) {
            holder.website.setText(place.getWebsiteUri().toString());
        }*/
    }

    /**
     * update the arraylist data
     *
     * @param viewModels dynamic view
     */
    public void updateData(List<HashMap<String, String>> viewModels) {
        try {
            mPlacesList.clear();
            mPlacesList.addAll(viewModels);
            notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getItemCount() {
        if (mPlacesList == null)
            return 0;
        else
            return mPlacesList.size();
    }

    /**
     * widgets view holder
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mPlacename;
        public TextView address;
        public TextView phone;
        public TextView website;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPlacename = itemView.findViewById(R.id.name);
            /*address = itemView.findViewById(R.id.address);
            phone = itemView.findViewById(R.id.phone);
            website = itemView.findViewById(R.id.website);*/
        }
    }
}

