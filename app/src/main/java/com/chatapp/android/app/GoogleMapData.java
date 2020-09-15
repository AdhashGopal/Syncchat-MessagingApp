package com.chatapp.android.app;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

public class GoogleMapData {

    /**
     * Google Map input data's
     */
    LatLng latLng;
    String name;
    String address;
    Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
