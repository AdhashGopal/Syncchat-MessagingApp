package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.chatapp.android.R;


/**
 * created by  Adhash Team 02/04/16.
 */
public class VHLocationSent extends RecyclerView.ViewHolder implements OnMapReadyCallback {
    /**
     * Bing the widget id's
     */
    public TextView senderName, time, tvDateLbl, tvSecretLbl;
    public ImageView singleTick, doubleTickGreen, doubleTickBlue, clock, imageViewindicatior, starredindicator_below;
    public ImageView ivMap;
    public RelativeLayout location_main, selection_layout, relative_layout_message;
    public GoogleMap mMap;
    public String nameSelected = "";
    public LatLng positionSelected = new LatLng(0, 0);
    public ProgressBar pbUpload;

    public VHLocationSent(View view) {
        super(view);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        time = (TextView) view.findViewById(R.id.ts);
        singleTick = (ImageView) view.findViewById(R.id.single_tick_green);
        doubleTickGreen = (ImageView) view.findViewById(R.id.double_tick_green);
        doubleTickBlue = (ImageView) view.findViewById(R.id.double_tick_blue);
        location_main = (RelativeLayout) view.findViewById(R.id.location_main);
        tvDateLbl = (TextView) view.findViewById(R.id.tvDateLbl);
        tvSecretLbl = (TextView) view.findViewById(R.id.tvSecretLbl);
        clock = (ImageView) view.findViewById(R.id.clock);
        imageViewindicatior = (ImageView) view.findViewById(R.id.imageView);
        ivMap = (ImageView) view.findViewById(R.id.ivMap);
        selection_layout = (RelativeLayout) view.findViewById(R.id.selection_layout);
        relative_layout_message = (RelativeLayout) view.findViewById(R.id.relative_layout_message);
        pbUpload = (ProgressBar) view.findViewById(R.id.pbUpload);
        starredindicator_below = (ImageView) view.findViewById(R.id.starredindicator_below);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.addMarker(new MarkerOptions().position(positionSelected).title(nameSelected));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionSelected, 16.0f));


    }
}
