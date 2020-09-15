package com.chatapp.android.app.adapter;

/**
 * created by  Adhash Team on 11/18/2016.
 */

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;

public class AboutHelpAdapter extends BaseAdapter {
    Activity context;
    String title[];
    String description[];

    public AboutHelpAdapter(Activity context, String[] title, String[] description) {
        super();
        this.context = context;
        this.title = title;
        this.description = description;
    }

    public int getCount() {
        return title.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {
        AvnNextLTProDemiTextView txtViewTitle;
        AvnNextLTProRegTextView txtViewDescription;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        LayoutInflater inflater = context.getLayoutInflater();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.about_list_items, null);
            holder = new ViewHolder();
            holder.txtViewTitle = (AvnNextLTProDemiTextView) convertView.findViewById(R.id.textView_abouthelp);
            holder.txtViewDescription = (AvnNextLTProRegTextView) convertView.findViewById(R.id.textViewabt);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.txtViewTitle.setText(title[position]);
        holder.txtViewDescription.setText(description[position]);
        return convertView;
    }

}