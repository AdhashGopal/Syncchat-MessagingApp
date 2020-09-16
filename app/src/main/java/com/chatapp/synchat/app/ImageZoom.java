package com.chatapp.synchat.app;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.viewpager.widget.ViewPager;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.ZoomImageView;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.chatapphelperclass.ChatappImageUtils;
import com.chatapp.synchat.core.service.Constants;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * created by  Adhash Team on 12/28/2016.
 */
public class ImageZoom extends CoreActivity {
    private ImageView back, share, save;
    private ZoomImageView imagezoom_image;
    private Bitmap bitmap;
    private AvnNextLTProDemiTextView profilepic_usename;
    private Uri tempUri;
    private boolean fromProfile;
    private TextView captiontext;
    ArrayList<String> myImageList;
    ArrayList<String> myImageListCaptions;
    ViewPager viewPager;
    //SwipAdapter swipAdapter;


    /**
     * binding widget and gett the value from another acitivy data
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagezoom);

        back = (ImageView) findViewById(R.id.backarrow_imagezoom);
        save = (ImageView) findViewById(R.id.save);
        profilepic_usename = (AvnNextLTProDemiTextView) findViewById(R.id.profile_username);
        share = (ImageView) findViewById(R.id.share);
        getSupportActionBar().hide();
        imagezoom_image = (ZoomImageView) findViewById(R.id.imagezoom_image);
        captiontext = findViewById(R.id.captiontext);
        viewPager = (ViewPager) findViewById(R.id.view_pager);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int zoomImageHeight = displayMetrics.heightPixels;
        int zoomImageWidth = displayMetrics.widthPixels;


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {

            if (bundle.containsKey("ProfilePath")) {
                String pic = bundle.getString("ProfilePath", "");
                System.out.println("===image adapter clicked ChatList IMGVW" + pic);

                String receviername = bundle.getString("Profilepicname");

                System.out.println("===image adapter clicked ChatList IMGVW" + receviername);

                if (!pic.equals("")) {
                    if (!pic.startsWith(Constants.SOCKET_IP)) {
                        pic = Constants.SOCKET_IP + pic;
                        viewPager.setVisibility(View.GONE);
                        imagezoom_image.setVisibility(View.VISIBLE);
                        System.out.println("===image adapter clicked ChatList IMGVW" + pic);
                        Picasso.with(ImageZoom.this).load(pic)
                                .error(R.drawable.ic_profile_default).into(imagezoom_image);
                    } else {
                        viewPager.setVisibility(View.GONE);
                        imagezoom_image.setVisibility(View.VISIBLE);
                        System.out.println("===image adapter clicked ChatList IMGVW" + pic);
                        Picasso.with(ImageZoom.this).load(pic)
                                .error(R.drawable.ic_profile_default).into(imagezoom_image);
                    }
                    //AppUtils.loadImage(this, pic, imagezoom_image, 0, R.mipmap.chat_attachment_profile_default_image_frame);
                   /* Picasso.with(this).load(pic).error(R.mipmap.chat_attachment_profile_default_image_frame)
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    imagezoom_image.setImageBitmap(bitmap);
                                    share.setVisibility(View.VISIBLE);
                                    save.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onBitmapFailed(Drawable errorDrawable) {

                                }

                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {

                                }
                            });*/
                }

                profilepic_usename.setVisibility(View.VISIBLE);
                profilepic_usename.setText(receviername);

            } else {
                try {
                    if (bundle.containsKey("image_list")) {
                        myImageList = (ArrayList<String>) getIntent().getSerializableExtra("image_list");
                        myImageListCaptions = (ArrayList<String>) getIntent().getSerializableExtra("captiontext_list");
                        if (myImageList.size() > 1) {
                            viewPager.setVisibility(View.VISIBLE);
                            imagezoom_image.setVisibility(View.GONE);
                            captiontext.setVisibility(View.GONE);


                            //TODO tharani commant
                           /* swipAdapter = new SwipAdapter(this, myImageList, myImageListCaptions, zoomImageWidth, zoomImageHeight);
                            viewPager.setAdapter(swipAdapter);*/
//                            viewPager.setCurrentItem((bundle.getInt("image_position")-1)<0?0:(bundle.getInt("image_position")-1), true);
                            viewPager.setCurrentItem((bundle.getInt("image_position")) < 0 ? 0 : bundle.getInt("image_position"), true);
                            viewPager.setVisibility(View.GONE);
                            imagezoom_image.setVisibility(View.VISIBLE);


                            String imagePath1 = bundle.getString("image");
                            Bitmap bitmap1 = ChatappImageUtils.decodeBitmapFromFile(imagePath1, zoomImageWidth, zoomImageHeight);
                            imagezoom_image.setImageBitmap(bitmap1);

                        } else {
                            viewPager.setVisibility(View.GONE);
                            imagezoom_image.setVisibility(View.VISIBLE);
                            captiontext.setVisibility(View.VISIBLE);
                            String imagePath = bundle.getString("image");

                            captiontext.setText(bundle.getString("captiontext"));
                            //imagezoom_image.setImageURI(Uri.parse(image));
                            Bitmap bitmap = ChatappImageUtils.decodeBitmapFromFile(imagePath, zoomImageWidth, zoomImageHeight);
                            imagezoom_image.setImageBitmap(bitmap);
                        }

                    } else {
                        String imagePath = bundle.getString("image");

                        captiontext.setText(bundle.getString("captiontext"));
                        //imagezoom_image.setImageURI(Uri.parse(image));
                        Bitmap bitmap = ChatappImageUtils.decodeBitmapFromFile(imagePath, zoomImageWidth, zoomImageHeight);
                        imagezoom_image.setImageBitmap(bitmap);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


                //    share.setVisibility(View.VISIBLE);

            }
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    initProgress("Initializing...", true);
                    showProgressDialog();
                    bitmap = ((BitmapDrawable) imagezoom_image.getDrawable()).getBitmap();
                    if (bitmap != null) {
                        tempUri = getImageUri(bitmap);
                    }
                    //shareIt();
                    onShareClick();
                    hideProgressDialog();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });

    }


    /**
     * Profile picture update
     */
    private void saveImage() {

        if (imagezoom_image.getDrawable() != null) {
            Bitmap profileBmp = ((BitmapDrawable) imagezoom_image.getDrawable()).getBitmap();

            File myDir = new File(MessageFactory.PROFILE_IMAGE_PATH);
            if (!myDir.exists()) {
                myDir.mkdirs();
            }
            String fname = "Image-" + Calendar.getInstance().getTimeInMillis() + ".jpg";
            File file = new File(myDir, fname);
            if (file.exists()) file.delete();
            try {
                file.createNewFile();
                FileOutputStream out = new FileOutputStream(file);
                profileBmp.compress(Bitmap.CompressFormat.JPEG, 90, out);
                out.flush();
                out.close();
                Toast.makeText(ImageZoom.this, "profile picture saved", Toast.LENGTH_SHORT).show();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void shareIt() {
//sharing implementation here
       /* Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Chatapp App Invitation");
        startActivity(Intent.createChooser(sharingIntent, "Share via"));*/
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setType("application/image");
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Test Subject");
        emailIntent.putExtra(Intent.EXTRA_STREAM, tempUri);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "From My App");
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    /**
     * share image
     */
    public void onShareClick() {
        String[] blacklist = new String[]{"com.any.package", "net.other.package"};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM, tempUri);
        intent.putExtra(Intent.EXTRA_TEXT, "");
        startActivity(generateCustomChooserIntent(intent, blacklist));
    }

    /**
     * generate Custom ChooserIntent
     *
     * @param prototype        input value(prototype)
     * @param forbiddenChoices input value(forbiddenChoices)
     * @return value
     */
    private Intent generateCustomChooserIntent(Intent prototype, String[] forbiddenChoices) {
        List<Intent> targetedShareIntents = new ArrayList<Intent>();
        List<HashMap<String, String>> intentMetaInfo = new ArrayList<HashMap<String, String>>();
        Intent chooserIntent;

        Intent dummy = new Intent(prototype.getAction());
        dummy.setType(prototype.getType());
        List<ResolveInfo> resInfo = getPackageManager().queryIntentActivities(dummy, 0);

        if (!resInfo.isEmpty()) {
            for (ResolveInfo resolveInfo : resInfo) {
                if (resolveInfo.activityInfo == null || Arrays.asList(forbiddenChoices).contains(resolveInfo.activityInfo.packageName))
                    continue;
                HashMap<String, String> info = new HashMap<String, String>();
                info.put("packageName", resolveInfo.activityInfo.packageName);
                info.put("className", resolveInfo.activityInfo.name);
                info.put("simpleName", String.valueOf(resolveInfo.activityInfo.loadLabel(getPackageManager())));
                intentMetaInfo.add(info);
            }

            if (!intentMetaInfo.isEmpty()) {
                // sorting for nice readability
                Collections.sort(intentMetaInfo, new Comparator<HashMap<String, String>>() {
                    @Override
                    public int compare(HashMap<String, String> map, HashMap<String, String> map2) {
                        return map.get("simpleName").compareTo(map2.get("simpleName"));
                    }
                });

                // create the custom intent list
                for (HashMap<String, String> metaInfo : intentMetaInfo) {
                    Intent targetedShareIntent = (Intent) prototype.clone();
                    targetedShareIntent.setPackage(metaInfo.get("packageName"));
                    targetedShareIntent.setClassName(metaInfo.get("packageName"), metaInfo.get("className"));
                    targetedShareIntents.add(targetedShareIntent);
                }


                chooserIntent = Intent.createChooser(targetedShareIntents.remove(targetedShareIntents.size() - 1), "share");
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedShareIntents.toArray(new Parcelable[]{}));
                return chooserIntent;
            }
        }

        return Intent.createChooser(prototype, "Share it");
    }


    /**
     * get ImageUri
     *
     * @param inImage input value(inImage)
     * @return value
     */
    public Uri getImageUri(Bitmap inImage) {

        File profileDir = new File(MessageFactory.PROFILE_IMAGE_PATH);

        File fileDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), profileDir.getAbsolutePath());
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        if (!profileDir.exists()) {
            profileDir.mkdirs();
        }

        String filePath = MessageFactory.PROFILE_IMAGE_PATH + Calendar.getInstance().getTimeInMillis() + ".jpg";
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
        try {
            file.createNewFile();
            FileOutputStream ostream = new FileOutputStream(file);
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
            ostream.close();
//            Uri test = MediaStore.Images.Media.getContentUri(filePath);
            Uri test = Uri.fromFile(file);
            Log.d("TestURI", test.toString());
            return test;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
