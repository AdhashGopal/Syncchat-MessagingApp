/*
package com.chatapp.android.app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.chatapp.android.R;
import com.chatapp.android.app.ImageZoom;
import com.chatapp.android.app.widget.ZoomImageView;
import com.chatapp.android.core.chatapphelperclass.ChatappImageUtils;

import java.util.ArrayList;
import java.util.List;

public class SwipAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    ArrayList<String> myImageList;
    ArrayList<String> myImageListCaptions;
    int zoomImageWidth,zoomImageHeight;

    public SwipAdapter(Context context,ArrayList<String> getImageList,ArrayList<String> getImageListCaptions,int zoomImageWidth,int zoomImageHeight) {
        this.context = context;
        this.myImageList=getImageList;
        this.myImageListCaptions=getImageListCaptions;
        this.zoomImageWidth=zoomImageWidth;
        this.zoomImageHeight=zoomImageHeight;
    }

    @Override
    public int getCount() {
        return myImageList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == (RelativeLayout)object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.swipeimagezoom,container,false);
        ZoomImageView imageView = view.findViewById(R.id.imagezoom_image);
        TextView captiontext = view.findViewById(R.id.captiontext);
        try {
            Bitmap bitmap = ChatappImageUtils.decodeBitmapFromFile(myImageList.get(position), zoomImageWidth, zoomImageHeight);
            imageView.setImageBitmap(bitmap);

            captiontext.setText(myImageListCaptions.get(position));
            container.addView(view);
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}*/
