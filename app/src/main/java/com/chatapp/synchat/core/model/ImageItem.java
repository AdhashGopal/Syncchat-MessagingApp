package com.chatapp.synchat.core.model;

/**
 * created by  Adhash Team on 3/13/2017.
 */

import android.graphics.Bitmap;

public class ImageItem {
    /**
     * Getter & setter method for specific event
     */
    private Bitmap image;
    private String title, duration;

    public ImageItem(Bitmap image, String title, String duration) {
        super();
        this.image = image;
        this.title = title;
        this.duration = duration;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}