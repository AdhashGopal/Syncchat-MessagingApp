package com.chatapp.synchat.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatapp.synchat.R;
import com.chatapp.synchat.core.model.ImageItem;

import java.util.ArrayList;

/**
 * created by  Adhash Team on 3/13/2017.
 */
public class GridViewAdapter extends ArrayAdapter<ImageItem> {

    private Context context;
    private int layoutResourceId;
    private ArrayList<ImageItem> data = new ArrayList<ImageItem>();

    /**
     * Create constructor
     *
     * @param context          The activity object inherits the Context object
     * @param layoutResourceId dynamic view
     * @param data             list of value will be shown
     */
    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<ImageItem> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    /**
     * binding view data
     *
     * @param position    value of position
     * @param convertView view
     * @param parent      LayoutInflater
     * @return value
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.duration = (TextView) row.findViewById(R.id.duration);
            holder.image = (ImageView) row.findViewById(R.id.image);
            holder.video = (ImageView) row.findViewById(R.id.video);
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }
        ImageItem item = data.get(position);
        if (item.getTitle().contains("video")) {
            holder.image.setImageBitmap(item.getImage());
            holder.video.setVisibility(View.VISIBLE);
            holder.duration.setVisibility(View.VISIBLE);
            holder.duration.setText(item.getDuration());
        } else {
            holder.video.setVisibility(View.GONE);
            holder.duration.setVisibility(View.GONE);
            holder.image.setImageBitmap(item.getImage());
        }
        return row;
    }

    /**
     * widgets view holder
     */
    static class ViewHolder {
        TextView duration;
        ImageView image, video;

    }
}