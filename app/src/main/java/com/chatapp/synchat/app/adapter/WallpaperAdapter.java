package com.chatapp.synchat.app.adapter;

/**
 * created by  Adhash Team on 12/17/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class WallpaperAdapter extends BaseAdapter {
    private Context mContext;
    String[] mThumbIds;

    /**
     * Constructor
     *
     * @param c         The activity object inherits the Context object
     * @param ThumbsIds value
     */
    public WallpaperAdapter(Context c, String[] ThumbsIds) {
        mContext = c;
        mThumbIds = ThumbsIds;
    }


    /**
     * getCount
     *
     * @return value
     */
    public int getCount() {
        return mThumbIds.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    /**
     * binding view data
     *
     * @param position    view holder position
     * @param convertView view
     * @param parent      LayoutInflater
     * @return value
     */
    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(400, 440));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setBackgroundColor(Color.parseColor(mThumbIds[position]));
        return imageView;
    }


}