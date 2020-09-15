package com.chatapp.android.app.adapter;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.target.BitmapImageViewTarget;

import com.bumptech.glide.request.target.BitmapThumbnailImageViewTarget;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.chatapp.android.R;

import com.chatapp.android.core.model.Media_History_Item;

import android.content.Context;

import android.graphics.Bitmap;

import android.graphics.Color;

import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

import android.provider.MediaStore;

import android.view.View;

import android.view.ViewGroup;

import android.widget.BaseAdapter;

import android.widget.Gallery;

import android.widget.ImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import java.util.ArrayList;

//import com.threembed.utils.CircleTransform;

/**
 *
 */
public class GIAdapter extends BaseAdapter {


    public int getCount() {
        return imagelink.size();
    }

    public View getView(int index, View view, ViewGroup viewGroup) {
        // TODO Auto-generated method stub


        final ImageView i = new ImageView(mContext);

        i.setLayoutParams(new Gallery.LayoutParams(200, 200));
        i.setBackgroundColor(Color.parseColor("#FFe7e7e7"));

        i.setScaleType(ImageView.ScaleType.FIT_XY);


        if (imagelink.get(index).getMessageType().equals("1")) {

//TODO tharani map
            /*Glide.with(mContext).load(imagelink.get(index).getImagePath()).asBitmap()

                    .fitCenter().placeholder(R.mipmap.profile_image).
                    into(new BitmapImageViewTarget(i) {
                        @Override
                        protected void setResource(Bitmap resource) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(false);
                            i.setImageDrawable(circularBitmapDrawable);
                        }
                    });*/


            Glide.with(mContext)
                    .asBitmap()
                    .load(imagelink.get(index).getThumbnailPath())
                    .fitCenter()
                    .placeholder(R.mipmap.profile_image)
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            RoundedBitmapDrawable circularBitmapDrawable =
                                    RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                            circularBitmapDrawable.setCircular(false);
                            i.setImageDrawable(circularBitmapDrawable);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });

        } else if (imagelink.get(index).getMessageType().equals("2")) {

            if (imagelink.get(index).isSelf()) {


                i.setImageBitmap(ThumbnailUtils.createVideoThumbnail(imagelink.get(index).getVideoPath(),
                        MediaStore.Images.Thumbnails.MINI_KIND));
            } else {
                if (imagelink.get(index).getDownloadstatus() == 0) {
//TODO tharani map


                    /*Glide.with(mContext).load(imagelink.get(index).getThumbnailPath())
                            .asBitmap().fitCenter().placeholder(R.mipmap.profile_image).
                            into(new BitmapImageViewTarget(i) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    i.setImageDrawable(circularBitmapDrawable);
                                }
                            });
*/
                    Glide.with(mContext)
                            .asBitmap()
                            .load(imagelink.get(index).getThumbnailPath())
                            .fitCenter()
                            .placeholder(R.mipmap.profile_image)
                            .into(new CustomTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    RoundedBitmapDrawable circularBitmapDrawable =
                                            RoundedBitmapDrawableFactory.create(mContext.getResources(), resource);
                                    circularBitmapDrawable.setCircular(true);
                                    i.setImageDrawable(circularBitmapDrawable);
                                }

                                @Override
                                public void onLoadCleared(@Nullable Drawable placeholder) {
                                }
                            });

                } else {


                    i.setImageBitmap(ThumbnailUtils.createVideoThumbnail(imagelink.get(index).getVideoPath(),
                            MediaStore.Images.Thumbnails.MINI_KIND));


                }
            }
        }


        return i;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }


    private Context mContext;
    private ArrayList<Media_History_Item> imagelink;

    public GIAdapter(Context context, ArrayList<Media_History_Item> imagelink) {
        this.mContext = context;
        this.imagelink = imagelink;
    }


}
