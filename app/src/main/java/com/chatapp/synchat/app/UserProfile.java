
package com.chatapp.synchat.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.dialog.CustomMultiTextItemsDialog;
import com.chatapp.synchat.app.utils.ConnectivityInfo;

import org.appspot.apprtc.util.CryptLib;

import com.chatapp.synchat.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.app.widget.CircleImageView;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.message.PictureMessage;
import com.chatapp.synchat.core.model.MultiTextDialogPojo;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.socket.SocketManager;
import com.chatapp.synchat.core.uploadtoserver.FileUploadDownloadManager;

import com.soundcloud.android.crop.Crop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import id.zelory.compressor.Compressor;

import javax.crypto.NoSuchPaddingException;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * created by  Adhash Team on 12/1/2017.
 */
public class UserProfile extends CoreActivity {

    private CircleImageView profile;
    private ImageView ibProfilePic;
    private AvnNextLTProDemiTextView notification_actionbar_1;
    private AvnNextLTProRegTextView Status, usermessage, Username, userEmail;
    private ImageView backimg_uprofile;
    private ImageButton editStatus;
    private final int GALLERY_REQUEST_CODE = 1;
    private final int CAMERA_REQUEST_CODE = 2;
    private final int CHANGE_UNAME = 5;
    private Uri cameraImageUri;
    final Context context = this;
    RelativeLayout editStatus_layout;
    private String mCurrentUserId;
    Session session;
    ImageView myImage;
    TextView shareQR;
    private static final String TAG = "UserProfile";
    private String msidn;

    /* private final int REQUEST_PICK = 3;
     private final int REQUEST_CROP = 4;
 */

    /**
     * binding the widget id and getting value from another activity data
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        String uniqueCurrentID = SessionManager.getInstance(UserProfile.this).getCurrentUserID();
        if (savedInstanceState != null) {
            cameraImageUri = Uri.parse(savedInstanceState.getString("ImageUri"));
        } else {
            cameraImageUri = Uri.parse("");
        }

        shareQR = (TextView) findViewById(R.id.redeem_send);
        myImage = (ImageView) findViewById(R.id.qrCode);
        profile = (CircleImageView) findViewById(R.id.userprofile1);
        ibProfilePic = (ImageView) findViewById(R.id.ibProfilePic);
        Status = (AvnNextLTProRegTextView) findViewById(R.id.statustextview2);
        Username = (AvnNextLTProRegTextView) findViewById(R.id.username2);
        userEmail = (AvnNextLTProRegTextView) findViewById(R.id.user_email);
        userEmail.setText(SessionManager.getInstance(UserProfile.this).getCurrentUserEmailID());
//        usermessage = (AvnNextLTProRegTextView) findViewById(R.id.usermessage);
        editStatus = (ImageButton) findViewById(R.id.editStatus);
        editStatus_layout = (RelativeLayout) findViewById(R.id.editStatus_layout);

        backimg_uprofile = (ImageView) findViewById(R.id.backarrow_userprofile);
        //  phonenumber = (AvnNextLTProRegTextView) findViewById(R.id.contactnumber);
        notification_actionbar_1 = (AvnNextLTProDemiTextView) findViewById(R.id.notification_actionbar_1);
        msidn = SessionManager.getInstance(UserProfile.this).getPhoneNumberOfCurrentUser();
        CryptLib cryptLib = null;
        try {
            cryptLib = new CryptLib();
            msidn = cryptLib.encryptPlainTextWithRandomIV(msidn, kyGn());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


        //TODO remove frm tharani
        //final Bitmap myBitmap = QRCode.from(msidn).bitmap();
        //myImage.setImageBitmap(myBitmap);

        shareQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String bitmapPath = MediaStore.Images.Media.insertImage(getContentResolver(), myBitmap,"QRcode", null);
//                Uri bitmapUri = Uri.parse(bitmapPath);
//                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
//                shareIntent.setType("image/jpg");
//                shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri );
//                startActivity(Intent.createChooser(shareIntent, "Share image using"));

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_SUBJECT, "aChat");
                String url = msidn;
                try {
                    url = URLEncoder.encode(url, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                String shareText = "Use the link to add me " + Constants.SOCKET_IP + "linkfile.html?ID=" + url;
                // String shareText="For iOS: synctag://"+msidn+"\n"+"For Android: http://synctag.com/"+msidn;

                //  String sAux = "Check out the App at: http://www.chatapp.com/" + SessionManager.getInstance(UserProfile.this).getPhoneNumberOfCurrentUser();
                i.putExtra(Intent.EXTRA_TEXT, shareText);
                startActivity(i);

            }
        });

        getSupportActionBar().hide();

        backimg_uprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityLauncher.launchSettingScreen(UserProfile.this);
                finish();
            }
        });
        SessionManager sessionMgr = SessionManager.getInstance(UserProfile.this);
        mCurrentUserId = sessionMgr.getCurrentUserID();

        String mypic = sessionMgr.getUserProfilePic();
        Log.d("Whatsup Image Url", sessionMgr.getUserProfilePic() + " " + mypic);
        // Bitmap mybitmap=getBitmapFromurl();
        /*if (!mypic.isEmpty()) {
            Picasso.with(UserProfile.this).load(mypic).error(
                    R.mipmap.chat_attachment_profile_default_image_frame).into(profile);
            //new getBitmapFromurl().execute(mypic);
        }*/

        if (!SessionManager.getInstance(UserProfile.this).getcurrentUserstatus().isEmpty()) {
            Status.setText(SessionManager.getInstance(UserProfile.this).getcurrentUserstatus());
        }
        Username.setText(SessionManager.getInstance(UserProfile.this).getnameOfCurrentUser());
        //  phonenumber.setText(SessionManager.getInstance(UserProfile.this).getPhoneNumberOfCurrentUser());

        String pic = SessionManager.getInstance(UserProfile.this).getUserProfilePic();
        if (pic != null && !pic.isEmpty()) {
//TODO tharani map
            Glide.with(context).load(Constants.SOCKET_IP.concat(pic)).placeholder(R.mipmap.chat_attachment_profile_default_image_frame)
                    .centerCrop().dontAnimate().skipMemoryCache(true).into(profile);

//            Picasso.with(this).load(Constants.SOCKET_IP.concat(pic)).error(R.mipmap.chat_attachment_profile_default_image_frame)
//                    .transform(new CircleTransform()).into(profile);

        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userAvatar = SessionManager.getInstance(UserProfile.this).getUserProfilePic();
                System.out.println("===image userprofile" + userAvatar);
                String[] userpic = userAvatar.split("id=");
                System.out.println("===image userprofile" + userpic);
                if (!userpic[0].equalsIgnoreCase("?")) {
                    if (!userAvatar.equalsIgnoreCase("")) {
                        Intent intent = new Intent(UserProfile.this, ImageZoom.class);
                        intent.putExtra("ProfilePath", userAvatar);
                        startActivity(intent);
                    }
                }
            }
        });

        initProgress("Loading...", true);

        ibProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userAvatar = SessionManager.getInstance(UserProfile.this).getUserProfilePic();
                String[] userpic = userAvatar.split("id=");
                List<MultiTextDialogPojo> labelsList = new ArrayList<>();
                MultiTextDialogPojo label = new MultiTextDialogPojo();
                label.setImageResource(R.drawable.blue_camera);
                label.setLabelText("Take Image From Camera");
                labelsList.add(label);

                label = new MultiTextDialogPojo();
                label.setImageResource(R.drawable.gallery);
                label.setLabelText("Add Image From Gallery");
                labelsList.add(label);
                if (!userpic[0].equalsIgnoreCase("?")) {
                    if (!userAvatar.equalsIgnoreCase("")) {
                        label = new MultiTextDialogPojo();
                        label.setImageResource(R.drawable.gallery);
                        label.setLabelText("Remove Profile picture");
                        labelsList.add(label);
                    }
                }

                CustomMultiTextItemsDialog dialog = new CustomMultiTextItemsDialog();
                dialog.setTitleText("Profile Picture");
                dialog.setNegativeButtonText("Cancel");
                dialog.setLabelsList(labelsList);

                dialog.setDialogItemClickListener(new CustomMultiTextItemsDialog.DialogItemClickListener() {
                    @Override
                    public void onDialogItemClick(int position) {
                        switch (position) {

                            case 0:
                                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                                    StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                                    StrictMode.setVmPolicy(builder.build());
                                }

                                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                File cameraImageOutputFile = new File(
                                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                                        createCameraImageFileName());
                                cameraImageUri = Uri.fromFile(cameraImageOutputFile);
                                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                                intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
                                startActivityForResult(intent, CAMERA_REQUEST_CODE);

                                break;

                            case 1:
                                try {
                                    Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                    photoPickerIntent.setType("image/*");
                                    startActivityForResult(photoPickerIntent, GALLERY_REQUEST_CODE);
                                } catch (Exception e) {
                                    Intent photoPickerIntent = new Intent();
                                    photoPickerIntent.setType("image/*");
                                    photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                                    startActivityForResult(Intent.createChooser(photoPickerIntent, "Select Picture"), GALLERY_REQUEST_CODE);
                                    e.printStackTrace();
                                }


                                break;


                            case 2:
                                if (ConnectivityInfo.isInternetConnected(getApplication())) {
                                    FileUploadDownloadManager uploadDownloadManager = new FileUploadDownloadManager(context);
                                    uploadDownloadManager.setRemovePhoto(true);
                                    uploadDownloadManager.updatePropic(EventBus.getDefault());
                                    showProgressDialog();
                                } else {
                                    Toast.makeText(UserProfile.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                                }
                                break;

                        }
                    }
                });
                dialog.show(getSupportFragmentManager(), "Profile Pic");
            }
        });
        editStatus_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, ChangeNameScreen.class);
                String uname = Username.getText().toString();
                intent.putExtra("name", uname);
                startActivityForResult(intent, CHANGE_UNAME);

            }
        });
        Status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Status.class);
                startActivity(intent);
            }
        });
//        phonenumber.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//               /* Intent intent = new Intent(getApplicationContext(), ChangeNumber.class);
//                startActivity(intent);*/
//            }
//        });

        initProfilePicData();
    }

    private String kyGn() {
        return getResources().getString(R.string.chatapp) + getResources().getString(R.string.chatapp) + getResources().getString(R.string.adani);
    }

    /**
     * ProfilePic Data
     */
    private void initProfilePicData() {
        if (getIntent().getAction() != null && getIntent().getData() != null
                && getIntent().getAction().equalsIgnoreCase(Intent.ACTION_ATTACH_DATA)) {
            String path = getIntent().getData().getPath();
            if (path == null) {
                path = getRealFilePath(getIntent());
            }

            if (path != null && !path.equals("")) {
                beginCrop(getIntent().getData());
            }
        }
    }

    /**
     * get RealFilePath
     *
     * @param data input value(data)
     * @return value
     */
    private String getRealFilePath(Intent data) {
        Uri selectedImage = data.getData();
        String wholeID = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            wholeID = DocumentsContract.getDocumentId(selectedImage);
        }
        // Split at colon, use second item in the array
        String id = wholeID.split(":")[1];
        String[] column = {MediaStore.Images.Media.DATA};
        // where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";
        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{id}, null);
        String filePath = "";
        int columnIndex = cursor.getColumnIndex(column[0]);
        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }


    /**
     * create CameraImage FileName
     *
     * @return value
     */
    private String createCameraImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp + ".jpg";
    }

    /**
     * getting image data
     *
     * @param bundle
     */
    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("ImageUri", cameraImageUri.toString());
    }

    /**
     * getting value from activity based on image croping function
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    if (ConnectivityInfo.isInternetConnected(getApplication())) {
                        beginCrop(selectedImageUri);
                    } else {
                        Toast.makeText(UserProfile.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (resultCode == Activity.RESULT_CANCELED) {

                } else {
                    Toast.makeText(this, "Sorry! Failed to capture image",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (ConnectivityInfo.isInternetConnected(getApplication())) {
                    beginCrop(cameraImageUri);
                } else {
                    Toast.makeText(UserProfile.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }


            } else {
                if (resultCode == Activity.RESULT_CANCELED) {

                } else {
                    Toast.makeText(this, "Sorry! Failed to capture image",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(data);

            String filePath = uri.getPath();
            File image = new File(filePath);
            Bitmap compressBmp = null;
            try {
                compressBmp = new Compressor(UserProfile.this).compressToBitmap(image);
            } catch (IOException e) {
                e.printStackTrace();
            }

//                Bitmap alignedBitmap = ChatappImageUtils.getAlignedBitmap(compressBmp, filePath);
//            profile.setImageBitmap(compressBmp);
            uploadImage(compressBmp);
        } else if (resultCode == RESULT_OK && requestCode == CHANGE_UNAME) {
            Boolean message = data.getBooleanExtra("name", false);
            if (message) {
                Username.setText(SessionManager.getInstance(UserProfile.this).getnameOfCurrentUser());

            }

        }
    }


    /**
     * upload image
     *
     * @param circleBmp input value(circleBmp)
     */
    private void uploadImage(Bitmap circleBmp) {

        if (circleBmp != null) {
            try {


                showProgressDialog();
                //String profileImgPath = imgDir + "/" + Calendar.getInstance().getTimeInMillis() + "_pro.jpg";
                File file = getBitmapFile(circleBmp, "pro_" + Calendar.getInstance().getTimeInMillis() + ".jpg");

                Log.d("USER_PROFILE", "uploadImage: " + file.getAbsolutePath());
                Log.d("USER_PROFILE", "file exist: " + file.exists());

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
                String serverFileName = mCurrentUserId.concat("-").concat(String.valueOf(System.currentTimeMillis())).concat(".jpg");
                PictureMessage message = new PictureMessage(UserProfile.this);
                JSONObject object = (JSONObject) message.createUserProfileImageObject(serverFileName, file.getAbsolutePath());
                FileUploadDownloadManager fileUploadDownloadMgnr = new FileUploadDownloadManager(UserProfile.this);
                fileUploadDownloadMgnr.startFileUpload(EventBus.getDefault(), object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * get BitmapFile
     *
     * @param imageToSave input value(imageToSave)
     * @param fileName    input value(fileName)
     * @return value
     */
    private File getBitmapFile(Bitmap imageToSave, String fileName) {


        String path;
        path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString();
/*        File direct = new File(Environment.getExternalStorageDirectory() + "/chatapp");

        if (!direct.exists()) {

           boolean created= direct.mkdirs();
           direct.setWritable(true);

            Log.d("USER_PROFILE", "dirCreated: "+created);


        }
        else {
            Log.d("USER_PROFILE", "dir exists ");
        }*/
        File file = new File(path + "/" + fileName);

        try {
            FileOutputStream out = new FileOutputStream(file);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            Log.e("USER_PROFILE", "getBitmapFile: " + e.getMessage());
        }

        return file;
    }

    /**
     * Eventbus data
     *
     * @param event getting value based on call socket(image upload,change profile image and change user name)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        hideProgressDialog();
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_IMAGE_UPLOAD)) {
            Object[] array = event.getObjectsArray();
            try {
                JSONObject objects = new JSONObject(array[0].toString());
                String err = objects.getString("err");
                String message = objects.getString("message");
//                String removePhoto = objects.getString("removePhoto");
                String from = objects.getString("from");
                String type = objects.getString("type");

                String removePhoto = null;
                if (objects.has("removePhoto")) {
                    removePhoto = objects.getString("removePhoto");

                    if (removePhoto.equalsIgnoreCase("yes")) {
                        message = "Profile image removed successfully";
                    }
                }

                if (from.equalsIgnoreCase(SessionManager.getInstance(UserProfile.this).getCurrentUserID())
                        && type.equalsIgnoreCase("single")) {
                    if (!objects.getString("file").equals("") || removePhoto != null) {
                        String path = objects.getString("file") + "?id=" + Calendar.getInstance().getTimeInMillis();

                        final String finalMessage = message;


                        if (removePhoto.equalsIgnoreCase("yes")) {
                            profile.setImageResource(R.mipmap.chat_attachment_profile_default_image_frame);
                        } else {
                            //TODO tharani map
                            Glide.with(context).load(Constants.SOCKET_IP.concat(path)).placeholder(R.mipmap.chat_attachment_profile_default_image_frame)
                                    .centerCrop().dontAnimate().skipMemoryCache(true).into(profile);
                        }

                    }


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CHANGE_PROFILE_STATUS)) {
            Object[] array = event.getObjectsArray();
            try {
                JSONObject object = new JSONObject(array[0].toString());

                String err = object.getString("err");
                if (err.equalsIgnoreCase("0")) {

                    String from = object.getString("from");
                    if (from.equalsIgnoreCase(mCurrentUserId)) {
                        String message = object.getString("message");
                        String status = object.getString("status");

                        try {
                            byte[] decodeStatus = Base64.decode(status, Base64.DEFAULT);
                            status = new String(decodeStatus, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Status.setText(status);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CHANGE_USER_NAME)) {
            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                String err = object.getString("err");
                // byte[] uname=name.getBytes();

                if (err.equals("0")) {

                    String from = object.getString("from");
                    if (from.equalsIgnoreCase(mCurrentUserId)) {

                        String name = object.getString("name");
                        byte[] data = Base64.decode(name, Base64.DEFAULT);
                        try {
                            name = new String(data, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        Username.setText(name);
                    }
                }
            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Start EventBus function
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(UserProfile.this);
    }

    /**
     * Stop EventBus function
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(UserProfile.this);
    }

    /**
     * getting CircleBitmap
     *
     * @param bitmap input value(bitmap)
     * @return value
     */
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

    /**
     * beginCrop function
     * @param source input value(source)
     */
    private void beginCrop(Uri source) {
        Log.d(TAG, "beginCrop: ");
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
       /* if (resultCode == RESULT_OK) {
            profile.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }*/
    }

    /**
     * Maintainthe status on UI stage
     */
    @Override
    protected void onResume() {
        super.onResume();
        Status.setText(SessionManager.getInstance(UserProfile.this).getcurrentUserstatus());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    /**
     * get ImageUri
     * @param inContext current activity
     * @param inImage input value(inImage)
     * @return value
     */
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        try {
            bytes.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}