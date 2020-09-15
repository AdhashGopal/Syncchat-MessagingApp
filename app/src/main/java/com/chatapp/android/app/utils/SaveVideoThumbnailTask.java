package com.chatapp.android.app.utils;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * created by  Adhash Team on 4/2/2018.
 */

public class SaveVideoThumbnailTask extends AsyncTask<Void,Void,String> {
    private static final String TAG = SaveVideoThumbnailTask.class.getSimpleName();
private SaveFileResult saveFileResult;
public interface SaveFileResult{
    void result(String filePath);
}
    private String videoPath;
    public SaveVideoThumbnailTask(String videoPath,SaveFileResult saveFileResult){
        this.videoPath=videoPath;
        this.saveFileResult=saveFileResult;

    }
    @Override
    protected String doInBackground(Void... voids) {
        try{

            //MICRO_KIND --> low quality.. if Any issue change to MINI_KIND --> Medium quality
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MINI_KIND);

            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "VersionChat_Celebrity");
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("MyCameraApp", "failed to create directory");

                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            File mediaFile;
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
            try {
                FileOutputStream out = new FileOutputStream(mediaFile);
                thumb.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        return mediaFile.getAbsolutePath();
        }
        catch (Exception e){
            Log.e(TAG, "doInBackground: ",e );
        }
        return null;
    }

    @Override
    protected void onPostExecute(String filePath) {
        super.onPostExecute(filePath);
        if(saveFileResult!=null)
            saveFileResult.result(filePath);
    }
}
