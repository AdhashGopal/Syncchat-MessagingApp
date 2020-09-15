package com.chatapp.android.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.model.CropingOption;

import java.util.ArrayList;

public class CropingOptionAdapter extends ArrayAdapter<CropingOption> {
    private ArrayList<CropingOption> mOptions;
    private LayoutInflater mInflater;

    public CropingOptionAdapter(Context context,
                                ArrayList<CropingOption> options) {
        super(context, R.layout.croping_selector, options);
        mOptions = options;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup group) {
        if (convertView == null)
            convertView = mInflater.inflate(R.layout.croping_selector, null);

        CropingOption item = mOptions.get(position);

        if (item != null) {
            ((ImageView) convertView.findViewById(R.id.img_icon))
                    .setImageDrawable(item.icon);
            ((AvnNextLTProRegTextView) convertView.findViewById(R.id.txt_name))
                    .setText(item.title);

            return convertView;
        }

        return null;
    }
}
