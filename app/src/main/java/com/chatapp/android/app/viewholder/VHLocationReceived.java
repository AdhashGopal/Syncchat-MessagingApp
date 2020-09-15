package com.chatapp.android.app.viewholder;

import android.view.View;
import android.widget.ImageView;
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
public class VHLocationReceived extends RecyclerView.ViewHolder implements OnMapReadyCallback {
    /**
     * Bing the widget id's
     */
    public TextView senderName, time, tvDateLbl, tvSecretLbl;
    public ImageView imageViewindicatior, starredindicator_below;
    public ImageView ivMap;
    public RelativeLayout  selection_layout;
    private LatLng positionSelected = new LatLng(0, 0);

    public VHLocationReceived(View view) {
        super(view);

        senderName = (TextView) view.findViewById(R.id.lblMsgFrom);
        time = (TextView) view.findViewById(R.id.ts);
        tvDateLbl = (TextView) view.findViewById(R.id.tvDateLbl);
        tvSecretLbl = (TextView) view.findViewById(R.id.tvSecretLbl);

        ivMap = (ImageView) view.findViewById(R.id.ivMap);
        imageViewindicatior = (ImageView) view.findViewById(R.id.imageView);
        selection_layout = (RelativeLayout) view.findViewById(R.id.selection_layout);
        starredindicator_below = (ImageView) view.findViewById(R.id.starredindicator_below);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMapToolbarEnabled(true);
        String nameSelected = "";
        googleMap.addMarker(new MarkerOptions().position(positionSelected).title(nameSelected));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(positionSelected, 16.0f));
    }
}
