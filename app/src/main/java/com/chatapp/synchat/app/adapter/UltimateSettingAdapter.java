package com.chatapp.synchat.app.adapter;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.core.model.ChatappSettingsModel;

import java.util.ArrayList;

/*
 *  * created by  Adhash Team on 12/26/2016.
 * */
public class UltimateSettingAdapter extends BaseAdapter {

    private ArrayList<ChatappSettingsModel> dataList;
    private AvnNextLTProDemiTextView textView_settings;

    /**
     * Create constructor
     *
     * @param c        The activity object inherits the Context object
     * @param dataList list of value
     */
    public UltimateSettingAdapter(Context c, ArrayList<ChatappSettingsModel> dataList) {
        context = c;
        this.dataList = dataList;
    }

    /**
     * getItemCount
     *
     * @return value
     */
    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * binding view data
     *
     * @param position    view holder position
     * @param convertView view
     * @param parent      LayoutInflater
     * @return value
     */
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.settings_list_view, parent, false);
        textView_settings = (AvnNextLTProDemiTextView) row.findViewById(R.id.textView_settings);
        imageView_settings = (ImageView) row.findViewById(R.id.imageView_settings);
        textView_settings.setText(dataList.get(position).getTitle());
        imageView_settings.setImageResource(dataList.get(position).getResourceId());
        imageView_settings.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary));
        return row;
    }

    private Context context;
    //private ArrayList<SingleRowSetting> list;
    private ImageView imageView_settings;


}