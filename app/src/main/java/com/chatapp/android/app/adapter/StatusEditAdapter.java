package com.chatapp.android.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chatapp.android.R;
import com.chatapp.android.app.StatusEditActivity;

import java.util.ArrayList;

/**
 * Created by karthik on 8/2/18.
 */

public class StatusEditAdapter extends BaseAdapter {

    ArrayList<String> values;
    private static LayoutInflater inflater=null;
    StatusEditActivity activity;

    public StatusEditAdapter(StatusEditActivity activity, ArrayList<String> values){

        this.activity=activity;
        this.values = values;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return values.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView;

        rowView = inflater.inflate(R.layout.status_edit_list_item, null);
        TextView status = (TextView)rowView.findViewById(R.id.status_text);
        ImageView del = (ImageView)rowView.findViewById(R.id.status_edit_delete);
        status.setText(values.get(position));

        del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.deleteStatus(position);
            }
        });

        return rowView;
    }
}
