package com.chatapp.android.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chatapp.android.R;
import com.chatapp.android.app.adapter.CropingOptionAdapter;
import com.chatapp.android.app.adapter.GIAdapter;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.model.CropingOption;
import com.chatapp.android.core.model.Media_History_Item;
import com.chatapp.android.core.model.PictureModel;
import com.chatapp.android.core.service.Constants;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public class ChatappProfileScreen extends CoreActivity {

    private String receiverUid, receiverName, documentId, Image, username, currentDateTimeString;
    private ImageView profilepic, callSender, messageSender, editbutton;
    private Bitmap imagebtm;
    Gallery myGallery;
    public static String docId;
    ArrayList<Media_History_Item> mMessageData = new ArrayList<>();
    AvnNextLTProDemiTextView tvName;
    AvnNextLTProRegTextView tvPhoneNumber, tvStatus, textviewCount, Addparticipatent, exitgroup, numberofUser;
    private GIAdapter galleryImageAdapter;

    LinearLayout mediaBack;
    //RelativeLayout mediadownline;
    Map<String, String> miscallItem = new HashMap<>();

    int opengroupchat;
    LinearLayout singlechat, groupchat;
    LinearLayout groupmember;
    RelativeLayout container;
    Map<String, String> myUserDetail;

    Dialog dialog;

    private Uri mImageCaptureUri;
    Boolean imageChanged = false;
    private File outPutFile = null;
    private PictureModel pictureModel = null;
    private String tag_string_req = "string_req";
    private String pictureurl = null;
    Boolean isAdmin = false;
    Boolean iscurrentUserismember = false;
    CollapsingToolbarLayout collapsingToolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.profile_sender);

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        Bundle bundle = getIntent().getExtras();

        initProgress("Wait  :) ", false);

        if (bundle != null) {
            receiverUid = bundle.getString("receiverUidchat");
            receiverName = bundle.getString("receiverNamechat");
            documentId = bundle.getString("documentIdchat");
            Image = bundle.getString("Imagechat");
            username = bundle.getString("Usernamechat");
            docId = bundle.getString("docId");
            opengroupchat = bundle.getInt("isOpenforeGroupChat");


            Log.d("Team TBT check", receiverUid + " " + receiverName);
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor((getResources().getColor(R.color.Statusbar)));
        }


        intiliseCallback();

        collapsingToolbar.setTitle(username);
        profilepic = (ImageView) findViewById(R.id.backdrop);
        myGallery = (Gallery) findViewById(R.id.gallery);
        myGallery.setSpacing(1);

        singlechat = (LinearLayout) findViewById(R.id.linearlayoutforsinglechat);
        groupchat = (LinearLayout) findViewById(R.id.linearlayoutforgroup);
        groupmember = (LinearLayout) findViewById(R.id.groupmemberList);
        Addparticipatent = (AvnNextLTProRegTextView) findViewById(R.id.Addparticipatent);
        numberofUser = (AvnNextLTProRegTextView) findViewById(R.id.numberofUser);
        editbutton = (ImageView) findViewById(R.id.editbutton);

        editbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatappProfileScreen.this, ChangeNameScreen.class);
                startActivityForResult(intent, 5);
            }
        });

        exitgroup = (AvnNextLTProRegTextView) findViewById(R.id.exitgroup);

        if (opengroupchat == 0) {
            singlechat.setVisibility(View.VISIBLE);
            groupchat.setVisibility(View.GONE);

        } else {
            groupchat.setVisibility(View.VISIBLE);
            singlechat.setVisibility(View.GONE);

            outPutFile = new File(
                    android.os.Environment.getExternalStorageDirectory(),
                    "temp.jpg");

            profilepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (iscurrentUserismember) {
                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(profilepic.getWindowToken(), 0);

                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatappProfileScreen.this);
                        builder = new AlertDialog.Builder(ChatappProfileScreen.this);
                        builder.setTitle("Upload Image");
                        builder.setIcon(R.mipmap.ic_people);
                        builder.setItems(new CharSequence[]{"Add Image from gallery",
                                        "Take Image from Camera", "Cancel"},
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // The 'which' argument contains the index position
                                        // of the selected item
                                        switch (which) {
                                            case 0:
                                /* Call the alumni office here */
                                                Intent photoPickerIntent = new Intent(
                                                        Intent.ACTION_PICK);
                                                photoPickerIntent.setType("image/*");
                                                startActivityForResult(photoPickerIntent, 1);

                                                dialog.cancel();
                                                break;
                                            case 1:
                                /* Take image from camera here */
                                                if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                                    StrictMode.setVmPolicy(builder.build());
                                                }
                                                Intent intent = new Intent(
                                                        MediaStore.ACTION_IMAGE_CAPTURE);

                                                File f = new File(android.os.Environment
                                                        .getExternalStorageDirectory(),
                                                        "temp.jpg");

                                                mImageCaptureUri = Uri.fromFile(f);
                                                intent.putExtra(MediaStore.EXTRA_OUTPUT,
                                                        Uri.fromFile(f));
                                                // pic = f;

                                                startActivityForResult(intent, 2);
                                                break;
                                            case 2:
                                /* Do Nothing here */
                                                break;
                                        }
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();

                    }

                }
            });

        }


        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) myGallery.getLayoutParams();
        mlp.setMargins(-((metrics.widthPixels / 2) - 30),
                mlp.topMargin,
                mlp.rightMargin,
                mlp.bottomMargin
        );

        myGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

            }
        });

        galleryImageAdapter = new GIAdapter(ChatappProfileScreen.this, mMessageData);
        myGallery.setAdapter(galleryImageAdapter);
        myGallery.setSelection(1);
        tvName = (AvnNextLTProDemiTextView) findViewById(R.id.tvName);
        textviewCount = (AvnNextLTProRegTextView) findViewById(R.id.textviewCount);
        tvName.setText(username);

        tvPhoneNumber = (AvnNextLTProRegTextView) findViewById(R.id.tvPhoneNumber);
        tvPhoneNumber.setText(receiverUid);
        callSender = (ImageView) findViewById(R.id.callSender);
        messageSender = (ImageView) findViewById(R.id.messageSender);
        tvStatus = (AvnNextLTProRegTextView) findViewById(R.id.tvStatus);
        mediaBack = (LinearLayout) findViewById(R.id.mediaBack);
        mediaBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO mediaHistory
                /*Intent j = new Intent(ProfileScreen.this, MediaHistory.class);
                j.putExtra("docId", documentId);
                j.putExtra("receiverUid", receiverUid);
                j.putExtra("receiverName", receiverName);
                j.putExtra("documentId", documentId);
                j.putExtra("Username", username);
                j.putExtra("Image", Image);
                startActivity(j);*/


            }
        });

        callSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

        /* Open the dialing audio call screen here */
                final AlertDialog.Builder builder;


                // AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(ChatappProfileScreen.this, android.R.style.Theme_Material_Light_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(ChatappProfileScreen.this);
                }

                Calendar c = Calendar.getInstance();
                SimpleDateFormat df3 = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
                currentDateTimeString = df3.format(c.getTime());


                SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy HH:mm a");
                Date date = null;
                try {
                    date = df.parse(currentDateTimeString);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final long epoch = date.getTime();

                builder.setTitle("Start Call");

                builder.setMessage("Start Audio/Video call");
                builder.setPositiveButton("Audio Call", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        miscallItem.put("to_from", receiverName);
                        miscallItem.put("pictureUrl", Image);
                        miscallItem.put("Phonenumber", receiverUid);
                        miscallItem.put("tsNextLine", currentDateTimeString);
                        miscallItem.put("receive_called", "true");
                        miscallItem.put("id", String.valueOf(epoch));
                        miscallItem.put("call_type", "AUDIO CALL");
                        Log.d("Team TBT call detail", miscallItem.get("Phonenumber"));

                        Constants.callername = receiverName;


                    }
                });
                builder.setNegativeButton("Video Call", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        miscallItem.put("to_from", receiverName);
                        miscallItem.put("pictureUrl", Image);

                        miscallItem.put("Phonenumber", receiverUid);
                        miscallItem.put("tsNextLine", currentDateTimeString);

                        miscallItem.put("receive_called", "true");
                        miscallItem.put("id", String.valueOf(epoch));
                        miscallItem.put("call_type", "VIDEO CALL");

                        Constants.callername = receiverName;

                    }
                });

                ConnectivityManager conMgr =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                if (conMgr.getActiveNetworkInfo() != null
                        && conMgr.getActiveNetworkInfo().isAvailable()
                        && conMgr.getActiveNetworkInfo().isConnected()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            builder.show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ChatappProfileScreen.this, "No Internet Connection Available!", Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        messageSender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatappProfileScreen.this, ChatViewActivity.class);
                intent.putExtra("receiverUid", receiverUid);
                intent.putExtra("receiverName", receiverName);
                intent.putExtra("documentId", documentId);
                intent.putExtra("Username", username);
                intent.putExtra("Image", Image);
                intent.putExtra("msisdn", "");
                startActivity(intent);
            }
        });


        UserDetail(receiverUid);


        addImageMedia();


        if (Image != null && !Image.isEmpty()) {
//TODO tharani map
            Glide
                    .with(ChatappProfileScreen.this)
                    .load(Image)
                    .fitCenter()
                    .placeholder(R.mipmap.chat_attachment_profile_default_image_frame)
                    .into(profilepic);


        } else {
            profilepic.setImageResource(R.mipmap.chat_attachment_profile_default_image_frame);
        }


    }


    private void intiliseCallback() {

    }


    @Override
    protected void onResume() {
        super.onResume();
        groupmember.removeAllViews();
        GetGroupmember();


    }


    private void GetGroupmember() {

    }


    public String getDocId() {
        return docId;
    }


    public void UserDetail(String receiverUid) {

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(ChatappProfileScreen.this, ChatViewActivity.class);
                intent.putExtra("receiverUid", receiverUid);
                intent.putExtra("receiverName", receiverName);
                intent.putExtra("documentId", documentId);
                intent.putExtra("Username", username);
                intent.putExtra("Image", Image);
                intent.putExtra("type", opengroupchat);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void addImageMedia() {

    }


    public void uploadImageToServer() {


    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Team TBT result code", "" + resultCode + "" + data);

        if (resultCode == RESULT_OK && requestCode == 5) {
            String message = data.getStringExtra("MESSAGE");
            collapsingToolbar.setTitle(message);
            receiverName = message;
            UpdateToserver(message);
            return;


        }


        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        mImageCaptureUri = data.getData();
                        CropingIMG();
                    } catch (OutOfMemoryError e) {
                        Toast.makeText(ChatappProfileScreen.this, "Out of memory!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    if (resultCode == Activity.RESULT_CANCELED) {

                    } else {
                        Toast.makeText(this, "Sorry! Failed to capture image",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            } else if (requestCode == 2) {
                try {
                    CropingIMG();
                } catch (OutOfMemoryError e) {
                    Toast.makeText(ChatappProfileScreen.this, "Out of memory!", Toast.LENGTH_LONG).show();
                }
            } else if (requestCode == 3) {
                try {
                    if (outPutFile.exists()) {
                    /* Update a boolean here */


                        //new getBitmapFromurl().execute(outPutFile.getLocalPath());

                        Bitmap bitmap = BitmapFactory.decodeFile(outPutFile.getPath());
                        Bitmap circularImage = getCircleBitmap(bitmap);
                        profilepic.setImageBitmap(circularImage);

                        imageChanged = true;

                        uploadImageToServer();


                    } else {
                        Toast.makeText(this, "Error while loading image",
                                Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } catch (OutOfMemoryError e) {
                    Toast.makeText(ChatappProfileScreen.this, "Out of memory!", Toast.LENGTH_LONG).show();
                }
            }
        }


    }

    private void UpdateToserver(String message) {

    }


    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap circuleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getWidth(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(circuleBitmap);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return circuleBitmap;
    }


    private void CropingIMG() {

        final ArrayList<CropingOption> cropOptions = new ArrayList<CropingOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(
                intent, 0);
        int size = list.size();
        if (size == 0) {
            Toast.makeText(this, "Can't find image croping app",
                    Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            // TODO: don't use return-data tag because it's not return large
            // image data and crash not given any message
            // intent.putExtra("return-data", true);

            // Create output file here
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName,
                        res.activityInfo.name));

                startActivityForResult(i, 3);
            } else {
                for (ResolveInfo res : list) {
                    final CropingOption co = new CropingOption();

                    co.title = getPackageManager().getApplicationLabel(
                            res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(
                            res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent
                            .setComponent(new ComponentName(
                                    res.activityInfo.packageName,
                                    res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(
                        getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Cropping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                startActivityForResult(
                                        cropOptions.get(item).appIntent, 3);
                            }
                        });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getContentResolver().delete(mImageCaptureUri, null,
                                    null);
                            mImageCaptureUri = null;
                        }
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

}
