package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.chatapp.android.R;

/**
 * created by  Adhash Team on 2/6/2017.
 */
public class VHStarredLocationSent extends RecyclerView.ViewHolder implements OnMapReadyCallback {
    /**
     * Bing the widget id's
     */
    public TextView senderName, time,toname,fromname,datelbl;
    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock,imageViewindicatior,starredindicator_below,ivMap,userprofile;
    public MapView mapView;
    public GoogleMap mMap;
    public String nameSelected = "";
    public LatLng positionSelected = new LatLng(0, 0);

    public VHStarredLocationSent(View view) {
        super(view);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        time = (TextView) view.findViewById(R.id.ts);
        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);
        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);
        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
        datelbl = (TextView) view.findViewById(R.id.datelbl);
        clock = (ImageView) view.findViewById(R.id.clock);
        ivMap = (ImageView) view.findViewById(R.id.ivMap);
        userprofile=(ImageView)view.findViewById(R.id.userprofile);
        imageViewindicatior = (ImageView) view.findViewById(R.id.imageView);
        starredindicator_below=(ImageView)view.findViewById(R.id.starredindicator_below);



    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.addMarker(new MarkerOptions().position(positionSelected).title(nameSelected));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionSelected, 16.0f));


    }
}
