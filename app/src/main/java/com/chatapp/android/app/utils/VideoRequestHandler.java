package com.chatapp.android.app.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Request;
import com.squareup.picasso.RequestHandler;

/**
 * created by  Adhash Team on 4/2/2018.
 */

public class VideoRequestHandler extends RequestHandler {
    public static String SCHEME_VIDEO="video";
    private static final String TAG = "VideoRequestHandler";
    @Override
    public boolean canHandleRequest(Request data)
    {
        String scheme = data.uri.getScheme();
        return (SCHEME_VIDEO.equals(scheme));
    }

    @Override
    public Result load(Request data, int arg1)
    {
        try {
            Bitmap bm = ThumbnailUtils.createVideoThumbnail(data.uri.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
            return new Result(bm, Picasso.LoadedFrom.DISK);
        }
        catch (Exception e){
            Log.e(TAG, "load: ", e);
        }

        return null;
    }
}
