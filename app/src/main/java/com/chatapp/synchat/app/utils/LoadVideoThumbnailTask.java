package com.chatapp.synchat.app.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

/**
 * created by  Adhash Team on 4/2/2018.
 */

public class LoadVideoThumbnailTask extends AsyncTask<Void,Void,Bitmap> {
    private static final String TAG = "LoadVideoThumbnailTask";

    private ImageView imageView;
    private String localPath;
    public LoadVideoThumbnailTask(String localPath, ImageView imageView){
        this.localPath=localPath;
        this.imageView=imageView;
    }
    @Override
    protected Bitmap doInBackground(Void... voids) {
        try{

            //MICRO_KIND --> low quality.. if Any issue change to MINI_KIND --> Medium quality
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(localPath, MediaStore.Video.Thumbnails.MICRO_KIND);
        return thumb;
        }
        catch (Exception e){
            Log.e(TAG, "doInBackground: ",e );
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap thumb) {
        super.onPostExecute(thumb);
        if(null!=imageView && null!=thumb)
            imageView.setImageBitmap(thumb);
    }
}
