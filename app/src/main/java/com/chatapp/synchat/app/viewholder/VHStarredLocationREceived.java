package com.chatapp.synchat.app.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.chatapp.synchat.R;

/**
 * created by  Adhash Team on 2/6/2017.
 */
public class VHStarredLocationREceived extends RecyclerView.ViewHolder implements OnMapReadyCallback {
    /**
     * Bing the widget id's
     */
    public TextView senderName, time, fromname, toname, datelbl;
    public ImageView imageViewindicatior, starredindicator_below,userprofile;
    public ImageView ivMap;
    public GoogleMap mMap;
    public String nameSelected = "";
    public LatLng positionSelected = new LatLng(0, 0);

    public VHStarredLocationREceived(View view) {
        super(view);
        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        toname = (TextView) view.findViewById(R.id.toname);
        fromname = (TextView) view.findViewById(R.id.fromname);
        datelbl = (TextView) view.findViewById(R.id.datelbl);
        time = (TextView) view.findViewById(R.id.ts);
        ivMap = (ImageView) view.findViewById(R.id.ivMap);
        userprofile=(ImageView)view.findViewById(R.id.userprofile);
        imageViewindicatior = (ImageView) view.findViewById(R.id.imageView);
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
