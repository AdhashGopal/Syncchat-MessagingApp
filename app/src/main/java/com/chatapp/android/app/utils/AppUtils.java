package com.chatapp.android.app.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.ExifInterface;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.chatapp.android.R;
import com.chatapp.android.core.service.Constants;
import com.chatapp.android.core.socket.MessageService;
import com.google.common.base.Strings;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * created by  Adhash Team on 3/7/2018.
 */

public class AppUtils {
    private static final String TAG = "AppUtils";

    /**
     * convert parseLong
     *
     * @param value getting input value
     * @return value
     */
    public static Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            Log.e(TAG, "parse: ", e);
        }
        return 0L;
    }


    /**
     * convert parseInt
     *
     * @param value getting input value
     * @return value
     */
    public static int parseInt(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            Log.e(TAG, "parse: ", e);
        }
        return 0;
    }


    /**
     * share the app via email
     *
     * @param context current activity
     */
    public static void shareApp(Context context) {

        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        String appname = context.getResources().getString(R.string.app_name);
        //emailIntent.setType("application/image");
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_TEXT, Constants.getAppStoreLink(context));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, appname + " : Android");
        context.startActivity(Intent.createChooser(emailIntent, "Send via..."));
    }


    public static Bitmap getThumbnailFromVideo(String path) {
        try {

            // MINI_KIND, size: 512 x 384 thumbnail
            return ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MINI_KIND);
        } catch (Exception e) {
            Log.e(TAG, "getThumbnailFromVideo: ", e);
            return null;
        }
    }

    public static Bitmap getThumbnailFromVideo(Context context, Uri uri) {
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            String picturePath = "";
            Cursor cursor = context.getContentResolver().query(uri, filePathColumn, null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                picturePath = cursor.getString(columnIndex);
                cursor.close();
            }
            // MINI_KIND, size: 512 x 384 thumbnail
            return ThumbnailUtils.createVideoThumbnail(picturePath, MediaStore.Video.Thumbnails.MINI_KIND);
        } catch (Exception e) {
            Log.e(TAG, "getThumbnailFromVideo: ", e);
            return null;
        }
    }

    public static Bitmap getBitmapFromImageUri(Context context, Uri uri) {
        if (uri == null)
            return null;
        try {
            InputStream image_stream = context.getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(image_stream);
        } catch (Exception e) {
            return null;
        }
    }


    //TODO tharani map
    public static void LoadImage(Context context, String url, ImageView imageView, int placeHolderIconId) {
        try {
            if (url != null && !url.isEmpty() && context != null)
                Glide.with(context).load(url).placeholder(placeHolderIconId).dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontTransform()
                        .override(100, 100)
                        .into(imageView);
        } catch (Exception e) {
            Log.e(TAG, "LoadImage: ", e);
        }
    }


    public static String getFileNameFromPath(String imgPath) {
        String fileName = "";
        try {
            fileName = imgPath.substring(imgPath.lastIndexOf("/") + 1);
        } catch (Exception e) {
            Log.e(TAG, "getFileNameFromPath: ", e);
        }
        return fileName;
    }

    /**
     * load Image view
     *
     * @param context   current activity
     * @param path      image path
     * @param imageView view
     */
    public static void loadLocalImage(Context context, String path, ImageView imageView) {
        try {
            File localFile = new File(path);
            Glide.with(context).load(localFile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontTransform()
                    .dontAnimate()
                    .into(imageView);
        } catch (Exception e) {
            Log.e(TAG, "LoadImage: ", e);
        }
    }

    /**
     * load Image view
     *
     * @param context   current activity
     * @param path      image path
     * @param imageView view
     * @param width     image width
     * @param height    image height
     */
    public static void loadLocalImage(Context context, String path, ImageView imageView, int width, int height) {
        try {
            File localFile = new File(path);
            Glide.with(context).load(localFile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontTransform()
                    .dontAnimate()
                    .override(width, height)
                    .into(imageView);
        } catch (Exception e) {
            Log.e(TAG, "LoadImage: ", e);
        }
    }


    public static void loadVideoThumbnail(Context context, String path, ImageView imageView, float quality, int resize) {
        try {
            File localFile = new File(path);
            Glide.with(context).load(localFile)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontTransform()
                    .dontAnimate()
                    .thumbnail(quality)
                    .override(resize, resize)
                    .into(imageView);
        } catch (Exception e) {
            Log.e(TAG, "LoadImage: ", e);
        }
    }

    /**
     * load Image view
     *
     * @param context   current activity
     * @param path      image path
     * @param imageView view
     */
    public static void loadImage(Context context, String path, ImageView imageView) {
        try {
            if (path == null || path.isEmpty())
                return;
            Glide.with(context).load(path)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontTransform()
                    .dontAnimate()
                    .into(imageView);
        } catch (Exception e) {
            Log.e(TAG, "LoadImage: ", e);
        }


    }

    public static void loadThum(Context context, String path, ImageView imageView) {
//        try {
//            if(path==null || path.isEmpty())
//                return;
//            Glide.with(context).load(path)
//                    .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    .dontTransform()
//                    .dontAnimate()
//                    .into(imageView);
//        } catch (Exception e) {
//            Log.e(TAG, "LoadImage: ", e);
//        }

        Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        if (thumbBmp != null)
            thumbBmp.compress(Bitmap.CompressFormat.JPEG, 25, out);
//                Bitmap compressBmp = Compressor.getDefault(mContext).compressToBitmap(thumbBmp);
        byte[] thumbArray = out.toByteArray();
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
        thumbData = thumbData.replace("\n", "");
        thumbData = thumbData.replace(" ", "");
        if (!thumbData.startsWith("data:image/jpeg;base64,")) {
            thumbData = "data:image/jpeg;base64," + thumbData.trim();
        }
    }


    /**
     * load Image view
     *
     * @param context         current activity
     * @param path            image path
     * @param imageView       view
     * @param resize          image resize
     * @param placeHolderIcon error image
     */
    public static void loadImage(Context context, String path, final ImageView imageView, int resize, int placeHolderIcon) {
        try {
            if (path == null || path.isEmpty())
                return;
            //Glide.with(context).load(path);
            if (resize > 0) {
                //glideRequestmgr.override(resize, resize);

                RequestOptions myOptions = new RequestOptions()
                        .fitCenter()
                        .override(resize, resize);
                Glide.with(context)
                        .load(path)
                        .apply(myOptions)
                        .into(imageView);
            } else if (placeHolderIcon > 0) {

                RequestOptions options = new RequestOptions()
                        .centerCrop()
                        .placeholder(placeHolderIcon);
                //glideRequestmgr.placeholder(placeHolderIcon);

               /* glideRequestmgr.diskCacheStrategy(DiskCacheStrategy.ALL)
                        .dontTransform()
                        .dontAnimate()
                        .into(imageView);*/
                Glide.with(context).load(path)
                        .into(imageView);

            }

        } catch (Exception e) {
            Log.e(TAG, "LoadImage: ", e);
        }
    }

    /**
     * check emoji
     *
     * @param input input value
     * @return value
     */
    public static boolean isEmoji(String input) {
        boolean isEmoji = false;
        try {
            String EMOJI_RANGE_REGEX =
                    "[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]|[\u2700-\u27BF]";
            Pattern PATTERN = Pattern.compile(EMOJI_RANGE_REGEX);


            if (Strings.isNullOrEmpty(input)) {
                return false;
            }
            Matcher matcher = PATTERN.matcher(input);

            if (matcher.find()) {
                return true;
            }

            return false;

        } catch (Exception e) {
            Log.e(TAG, "isEmoji: ", e);
        }
        return false;
    }


    /**
     * restartService PendingIntent
     *
     * @param context   current activity
     * @param className specific class name
     */
    public static void startService(Context context, Class className) {
        try {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                Intent restartServiceIntent = new Intent(context, className);
                restartServiceIntent.setPackage(context.getPackageName());
                PendingIntent restartServicePendingIntent = PendingIntent.getService(context, 1, restartServiceIntent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmService = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                if (alarmService != null) {
                    alarmService.set(
                            AlarmManager.ELAPSED_REALTIME,
                            SystemClock.elapsedRealtime() + 500,
                            restartServicePendingIntent);
                }
            } else {
                //below Marshmallow{
                Intent i = new Intent(context, className);
                context.startService(i);
            }
        } catch (Exception e) {
            Log.e(TAG, "startService: ", e);
        }
    }

    /**
     * encrypt data
     *
     * @param key   key for encrypt
     * @param value input value
     * @return value
     */
    public static String encrypt(String key, String value) {
        try {
            SecretKey secretKey = new SecretKeySpec(Base64.decode(key.getBytes(), Base64.NO_WRAP), "AES");
            //  AlgorithmParameterSpec iv = new IvParameterSpec(Base64.decode(key.getBytes(), Base64.NO_WRAP));
            Random rand = new SecureRandom();
            byte[] bytes = new byte[16];
            rand.nextBytes(bytes);
            IvParameterSpec ivSpec = new IvParameterSpec(bytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            //cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            return new String(Base64.encode(cipher.doFinal(value.getBytes("UTF-8")), Base64.NO_WRAP));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * decrypt data
     *
     * @param key   key for decrypt
     * @param value input value
     * @return value
     */
    public static String decrypt(String key, String value) {
        try {
            SecretKey secretKey = new SecretKeySpec(Base64.decode(key.getBytes(), Base64.NO_WRAP), "AES");
            //  AlgorithmParameterSpec iv = new IvParameterSpec(Base64.decode(key.getBytes(), Base64.NO_WRAP));
            Random rand = new SecureRandom();
            byte[] bytes = new byte[16];
            rand.nextBytes(bytes);
            IvParameterSpec ivSpec = new IvParameterSpec(bytes);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            // cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decode = Base64.decode(value, Base64.NO_WRAP);
            return new String(cipher.doFinal(decode), "UTF-8");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // ***** Warning ******* --> don't use it in RecyclerView. It will produce problems in Recyclerview

    public static void loadImageSmooth(Context context, String path, final ImageView imageView, int resize, int placeHolderIcon) {
       /* try {
            if (path == null || path.isEmpty())
                return;
            if (imageView != null)
                Glide.clear(imageView);
            DrawableTypeRequest glideRequestmgr = Glide.with(context).load(path);
            if (resize > 0)
                glideRequestmgr.override(resize, resize);

            if (placeHolderIcon > 0)
                glideRequestmgr.placeholder(placeHolderIcon);

            glideRequestmgr.diskCacheStrategy(DiskCacheStrategy.ALL)
                    .dontTransform()
                    .dontAnimate()
                    .into(new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            try {
                                if (imageView != null) {
                                    imageView.setImageDrawable(resource.getCurrent());
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "onResourceReady: ", e);
                            }
                        }
                    });
        } catch (Exception e) {
            Log.e(TAG, "LoadImage: ", e);
        }*/
    }


    public static LinearLayoutManager getHorizontalLayoutManger(Context context) {
        if (context != null)
            return new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        ;
        return null;
    }

    /**
     * Default LayoutManger view
     *
     * @param context current activity
     * @return value
     */
    public static LinearLayoutManager getDefaultLayoutManger(Context context) {
        if (context != null)
            return new LinearLayoutManager(context);
        return null;
    }

    public static LinearLayoutManager getGridLayoutManger(Context context, int spanCount) {
        if (context != null)
            return new GridLayoutManager(context, spanCount);
        return null;
    }

    /**
     * check network connection
     *
     * @param context current activity
     * @return value
     */
    public static boolean isNetworkAvailable(Context context) {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE,
                ConnectivityManager.TYPE_WIFI};
        try {
/*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                context.registerReceiver(new ConnectivityReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
            }
*/

            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null &&
                        activeNetworkInfo.getType() == networkType)
                    return true;
            }
        } catch (Exception e) {
            MyLog.e(TAG, "isNetworkAvailable: ", e);
            return false;
        }
        return false;
    }


    /**
     * restart Service
     *
     * @param context current activity
     */
    public static void restartService(Context context) {
        Log.d(TAG, "restartService: ");
        Intent restartServiceIntent = new Intent(context, MessageService.class);
        restartServiceIntent.setPackage(context.getPackageName());
        restartServiceIntent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);

        PendingIntent restartServicePendingIntent =
                PendingIntent.getService(context, 1, restartServiceIntent,
                        PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmService =
                (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 2000,
                restartServicePendingIntent);
    }

    public static long getVideoDuration(String path, Context context) {
        long duration = 0L;
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
//use one of overloaded setDataSource() functions to set your data source
            retriever.setDataSource(context, Uri.parse(new File(path).toString()));
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            duration = Long.parseLong(time);
            retriever.release();
        } catch (Exception e) {
            Log.e(TAG, "getVideoDuration: ", e);
        }

        return duration;
    }

    public static boolean isDeviceSupportCamera(Context context) {

        if (context != null && context.getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    public static void refreshGalleryImage(String filePath, Context context) {
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(filePath))));
    }

    public static String getDateTimeByTimeStamp(Context context, long timeStamp) {
        String dateTime = "";
        try {

            Date today = TimeStampUtils.getDateFormat(Calendar.getInstance().getTimeInMillis());
            Date yesterday = TimeStampUtils.getYesterdayDate(today);

            Date currentItemDate = TimeStampUtils.getDateFormat(timeStamp);
            String time = TimeStampUtils.get12HrTimeFormat(context, String.valueOf(timeStamp));

            if (currentItemDate != null) {
                if (currentItemDate.equals(today)) {
                    dateTime = "Today ," + time;
                } else if (currentItemDate.equals(yesterday)) {

                    dateTime = "Yesterday ," + time;
                } else {
                    DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                    String formatDate = df.format(currentItemDate);
                    dateTime = formatDate + " " + time;

                }
            }

        } catch (Exception e) {
            Log.e(TAG, "getDateTimeByTimeStamp: ", e);
        }
        return dateTime;
    }


    public static String getStatusTime(Context context, long timeStamp) {
        String dateTime = "";
        try {

            Date today = TimeStampUtils.getDateFormat(Calendar.getInstance().getTimeInMillis());
            Date yesterday = TimeStampUtils.getYesterdayDate(today);

            Date currentItemDate = TimeStampUtils.getDateFormat(timeStamp);
            String time = TimeStampUtils.get12HrTimeFormat(context, String.valueOf(timeStamp));
            long currentMillis = System.currentTimeMillis();
            long difference = currentMillis - timeStamp;
            long differnceMinutes = TimeUnit.MILLISECONDS.toMinutes(difference);
            Log.d(TAG, "getStatusTime: differnceMinutes " + differnceMinutes);
            if (currentItemDate != null) {
                if (differnceMinutes < 60) {
                    if (differnceMinutes == 0 || differnceMinutes == 1) {
                        dateTime = "Just now";
                    } else {
                        String timeInMinutes = differnceMinutes + " minutes ago";
                        Log.d(TAG, "getStatusTime: " + timeInMinutes);
                        dateTime = timeInMinutes;
                    }
                } else if (currentItemDate.equals(today)) {
                    dateTime = "Today ," + time;
                } else if (currentItemDate.equals(yesterday)) {
                    dateTime = "Yesterday ," + time;
                } else {
                    DateFormat df = new SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH);
                    String formatDate = df.format(currentItemDate);
                    dateTime = formatDate + " " + time;

                }
            }

        } catch (Exception e) {
            Log.e(TAG, "getDateTimeByTimeStamp: ", e);
        }
        return dateTime;
    }

    /**
     * Camera Display Orientation
     *
     * @param activity current activity
     * @param cameraId input value (cameraId)
     * @param camera   input value (camera)
     * @return value
     */
    public static int setCameraDisplayOrientation(Activity activity,
                                                  int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);

        return result;
    }

    /**
     * slideUp animation
     *
     * @param view UI view
     */
    public static void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(600);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    /**
     * slideDown animation
     *
     * @param view UI view
     */
    // slide the view from its current position to below itself
    public static void slideDown(View view) {
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,                 // fromYDelta
                view.getHeight()); // toYDelta
        animate.setDuration(600);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    /**
     * convert image to Bitmap
     *
     * @param context current activity
     * @return value
     */
    public static Bitmap convertBitmap(Context context) {
        return BitmapFactory.decodeResource(context.getResources(),
                R.drawable.logo_initial_loader);
    }

    /**
     * Server Reachable
     *
     * @param context current activity
     * @param URL     input value (URL)
     * @return value
     */
    public static boolean isServerReachable(Context context, String URL) {
        ConnectivityManager connMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connMan.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected()) {
            try {
                URL urlServer = new URL(URL);
                HttpURLConnection urlConn = (HttpURLConnection) urlServer.openConnection();
                urlConn.setConnectTimeout(3000); //<- 3Seconds Timeout
                urlConn.connect();
                if (urlConn.getResponseCode() == 200) {
                    return true;
                } else {
                    return false;
                }
            } catch (MalformedURLException e1) {
                return false;
            } catch (IOException e) {
                return false;
            }
        }
        return false;
    }

    /**
     * Bitmap Convert for image
     */
    public static class BitmapConvert {


        public static Bitmap createBitmap(String getPath) throws IOException {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            URL url = new URL(getPath);
            Bitmap bitmapimg = BitmapFactory.decodeStream((InputStream) url.getContent());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmapimg.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
//            bitmap.recycle();
            Bitmap output = Bitmap.createBitmap(bitmapimg.getWidth(),
                    bitmapimg.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(output);

            final int color = 0xff424242;
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmapimg.getWidth(),
                    bitmapimg.getHeight());

            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawCircle(bitmapimg.getWidth() / 2,
                    bitmapimg.getHeight() / 2, bitmapimg.getWidth() / 2, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas.drawBitmap(bitmapimg, rect, rect, paint);

            return rotateImage(output, byteArray);
        }


        /**
         * rotate Image view
         *
         * @param bitmap    image in bitmap
         * @param byteArray image in byteArray format
         * @return value
         * @throws IOException error
         */
        static Bitmap rotateImage(Bitmap bitmap, byte[] byteArray) throws IOException {
            int rotate = 0;

            ExifInterface exif = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                exif = new ExifInterface(new ByteArrayInputStream(byteArray));
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
//            matrix.postScale(100, 100);
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true);
        }
    }

}
