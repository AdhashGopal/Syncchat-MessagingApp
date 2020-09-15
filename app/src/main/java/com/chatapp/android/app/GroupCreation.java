package com.chatapp.android.app;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.chatapp.android.R;
import com.chatapp.android.app.adapter.CropingOptionAdapter;
import com.chatapp.android.app.dialog.CustomMultiTextItemsDialog;
import com.chatapp.android.app.utils.GroupInfoSession;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.app.widget.CircleImageView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.message.PictureMessage;
import com.chatapp.android.core.model.CropingOption;
import com.chatapp.android.core.model.GroupInfoPojo;
import com.chatapp.android.core.model.MultiTextDialogPojo;
import com.chatapp.android.core.model.PictureModel;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.chatapphelperclass.ChatappImageUtils;
import com.chatapp.android.core.socket.SocketManager;
import com.chatapp.android.core.uploadtoserver.FileUploadDownloadManager;
import com.soundcloud.android.crop.Crop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


/**
 */
public class GroupCreation extends CoreActivity {

    private ImageView creategroup;
    private String currentDateTimeString;
    ArrayList<String> myList = new ArrayList<>();
    EmojiconEditText Mygroupname;
    private CircleImageView myProfilePic;
    private ProgressDialog progress;
    private Uri mImageCaptureUri;
    private Boolean imageChanged = false;
    private File outPutFile = null;
    private PictureModel pictureModel = null;
    private String tag_string_req = "string_req";
    private String pictureurl = null;
    ImageView happyFace;

    AvnNextLTProRegTextView label;
    AvnNextLTProRegTextView count;
    AvnNextLTProDemiTextView actionbarlabel;
    Uri cameraImageUri;
    EmojIconActions emojIcon;

    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_REQUEST_CODE = 2;


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groupcreation);

        if (savedInstanceState != null) {
            cameraImageUri = Uri.parse(savedInstanceState.getString("ImageUri"));
        } else {
            cameraImageUri = Uri.parse("");
        }

        initProgress("Uploading Image....", false);

        label = (AvnNextLTProRegTextView) findViewById(R.id.normaltext);
        actionbarlabel = (AvnNextLTProDemiTextView) findViewById(R.id.actionbar);
        getSupportActionBar().hide();
        count = (AvnNextLTProRegTextView) findViewById(R.id.count);
        myList = (ArrayList<String>) getIntent().getSerializableExtra("mylist");
        Log.d("send array", "" + myList);

        initialise();
        actionbarlabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Mygroupname.addTextChangedListener(watch);
    }


    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    TextWatcher watch = new TextWatcher() {

        @Override
        public void afterTextChanged(Editable arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                      int arg3) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onTextChanged(CharSequence cs, int a, int b, int c) {
            // TODO Auto-generated method stub
            int countgroupname = 25 - cs.length();
            count.setText(String.valueOf(countgroupname));


        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_GROUP)) {

            try {
                Object[] obj = event.getObjectsArray();
                JSONObject object = new JSONObject(obj[0].toString());
                String groupAction = object.getString("groupType");

                if (groupAction.equalsIgnoreCase(SocketManager.ACTION_NEW_GROUP)) {

                    String from = object.getString("from");
                    String currentUserId = SessionManager.getInstance(GroupCreation.this).getCurrentUserID();

                    if (from.equalsIgnoreCase(currentUserId)) {
                        String respMsg = object.getString("message");
                        String groupId = object.getString("groupId");
                        String members = object.getString("groupMembers");
                        String createdBy = object.getString("createdBy");
                        String profilePic = object.getString("profilePic");
                        String groupName = object.getString("groupName");
                        String ts = object.getString("timeStamp");
                        String admin = object.getString("admin");
                        String id = object.getString("id");

                        String docId = currentUserId.concat("-").concat(groupId).concat("-g");

                        GroupInfoSession groupInfoSession = new GroupInfoSession(GroupCreation.this);

                        GroupInfoPojo infoPojo = new GroupInfoPojo();
                        infoPojo.setGroupId(groupId);
                        infoPojo.setCreatedBy(createdBy);
                        infoPojo.setAvatarPath(profilePic);
                        infoPojo.setGroupName(groupName);
                        infoPojo.setGroupMembers(members);
                        infoPojo.setAdminMembers(admin);
                        infoPojo.setLiveGroup(true);
                        groupInfoSession.updateGroupInfo(docId, infoPojo);

                        /*int msgType = MessageFactory.text;
                        MessageItemChat groupMsgItem = new MessageItemChat();
                        groupMsgItem.setTextMessage(respMsg);
                        groupMsgItem.setSenderName(groupName);
                        groupMsgItem.setBroadcastName(groupName);
                        groupMsgItem.setIsInfoMsg(true);
                        groupMsgItem.setMessageId(docId.concat(id));
                        groupMsgItem.setReceiverUid(groupId);
                        groupMsgItem.setMessageType(String.valueOf(msgType));
                        db.updateChatMessage(docId, groupMsgItem);*/
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            if (progress != null)
                progress.dismiss();

            Intent intent = new Intent(GroupCreation.this, HomeScreen.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_IMAGE_UPLOAD)) {
            Object[] array = event.getObjectsArray();
            try {
                hideProgressDialog();
                JSONObject objects = new JSONObject(array[0].toString());
                String err = objects.getString("err");
                String message = objects.getString("message");

                if (err.equalsIgnoreCase("0")) {
                    String from = objects.getString("from");
                    String type = objects.getString("type");

                    if (from.equalsIgnoreCase(SessionManager.getInstance(GroupCreation.this).getCurrentUserID())
                            && type.equalsIgnoreCase("group")) {
                        pictureurl = objects.getString("file");
                        updateData();
                        hideProgressDialog();
//                        Toast.makeText(GroupCreation.this, message, Toast.LENGTH_SHORT).show();
                    }

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private void initialise() {

        creategroup = (ImageView) findViewById(R.id.creategroup);
        myProfilePic = (CircleImageView) findViewById(R.id.group_icon);
        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
        happyFace = (ImageView) findViewById(R.id.grouphappyFace);




        myProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<MultiTextDialogPojo> labelsList = new ArrayList<>();
                MultiTextDialogPojo label = new MultiTextDialogPojo();
                label.setImageResource(R.drawable.blue_camera);
                label.setLabelText("Take Image From Camera");
                labelsList.add(label);

                label = new MultiTextDialogPojo();
                label.setImageResource(R.drawable.gallery);
                label.setLabelText("Add Image From Gallery");
                labelsList.add(label);

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
                                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                                photoPickerIntent.setType("image/*");
                                startActivityForResult(photoPickerIntent, GALLERY_REQUEST_CODE);
                                break;

                        }
                    }
                });
                dialog.show(getSupportFragmentManager(), "Profile Pic");
            }
        });
        Mygroupname = (EmojiconEditText) findViewById(R.id.groupname);

        final View rootView = findViewById(R.id.mainlayoutofgroup);

        emojIcon = new EmojIconActions(this, rootView, Mygroupname, happyFace);

        emojIcon.ShowEmojIcon();


        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {

                Log.e("", "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e("", "Keyboard Closed!");

            }
        });


        creategroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internetcheck()) {

                    if (!Mygroupname.getText().toString().trim().isEmpty()) {

                        initProgress("Creating group", false);
                        showProgressDialog();

                        if (imageChanged) {
                            Bitmap profileImg = ((BitmapDrawable) myProfilePic.getDrawable()).getBitmap();
                            uploadImage(profileImg);
                        } else {

                            updateData();
                        }


                    } else {
                        Toast.makeText(GroupCreation.this, "Please provide group name", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(GroupCreation.this, "Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }

    private Boolean internetcheck() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplication().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }

    private void sendGroupCreateMessage() {
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
        myList.add(SessionManager.getInstance(GroupCreation.this).getCurrentUserID());
        JSONArray jsArray = new JSONArray(myList);
        SendMessageEvent messageEvent = new SendMessageEvent();
        JSONObject obj = new JSONObject();
        try {
            obj.put("id", Calendar.getInstance().getTimeInMillis());
            obj.put("from", SessionManager.getInstance(GroupCreation.this).getCurrentUserID());
            obj.put("groupId", "" + epoch);
            obj.put("groupMembers", jsArray);
            obj.put("groupName", Mygroupname.getText().toString());
            obj.put("profilePic", pictureurl);
            obj.put("groupType", SocketManager.ACTION_NEW_GROUP);

            Log.d("group creation", obj.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageEvent.setEventName(SocketManager.EVENT_GROUP);
        messageEvent.setMessageObject(obj);
        EventBus.getDefault().post(messageEvent);

    }

    private String createCameraImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp + ".jpg";
    }

/*
    private void uploadImage(Bitmap circleBmp) {
        showProgressDialog();

        if (circleBmp != null) {
            try {
                File imgDir = new File(MessageFactory.PROFILE_IMAGE_PATH);
                if (!imgDir.exists()) {
                    imgDir.mkdirs();
                }
                showProgressDialog();
                String profileImgPath = imgDir + "/" + Calendar.getInstance().getTimeInMillis() + "_pro.jpg";

                File file = new File(profileImgPath);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();

                OutputStream outStream = new FileOutputStream(file);
                circleBmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();

                String serverFileName = SessionManager.getInstance(GroupCreation.this).getCurrentUserID()
                        + "-g-" + Calendar.getInstance().getTimeInMillis() + ".jpg";

                PictureMessage message = new PictureMessage(GroupCreation.this);
                JSONObject object = (JSONObject) message.createGroupProfileImageObject(serverFileName, profileImgPath);
                FileUploadDownloadManager fileUploadDownloadMgnr = new FileUploadDownloadManager(GroupCreation.this);
                fileUploadDownloadMgnr.startFileUpload(EventBus.getDefault(), object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
*/


    private void uploadImage(Bitmap circleBmp) {

        if (circleBmp != null) {
            try {
                /*File imgDir = new File(MessageFactory.PROFILE_IMAGE_PATH);
                if (!imgDir.exists()) {
                    imgDir.mkdirs();
                }
                showProgressDialog();
                String profileImgPath = imgDir + "/" + Calendar.getInstance().getTimeInMillis() + "_pro.jpg";

                File file = new File(profileImgPath);
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();

                OutputStream outStream = new FileOutputStream(file);
                circleBmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
                outStream.flush();
                outStream.close();

                String serverFileName = mCurrentUserId + "-" + mGroupId + "-g-"
                        + Calendar.getInstance().getTimeInMillis() + ".jpg";

                PictureMessage message = new PictureMessage(GroupInfo.this);
                JSONObject object = (JSONObject) message.createGroupProfileImageObject(serverFileName, profileImgPath);
                FileUploadDownloadManager fileUploadDownloadMgnr = new FileUploadDownloadManager(GroupInfo.this);
                fileUploadDownloadMgnr.startFileUpload(EventBus.getDefault(), object);
*/





                showProgressDialog();

                File file = getBitmapFile(circleBmp, "pro_group_" + Calendar.getInstance().getTimeInMillis() + ".jpg");

                Log.d("USER_PROFILE", "uploadImage: " + file.getAbsolutePath());
                Log.d("USER_PROFILE", "file exist: " + file.exists());

                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getAbsolutePath())));
              /*  String serverFileName = mCurrentUserId + "-" + mGroupId + "-g-"
                        + Calendar.getInstance().getTimeInMillis() + ".jpg";*/

                String serverFileName = SessionManager.getInstance(GroupCreation.this).getCurrentUserID()
                        + "-g-" + Calendar.getInstance().getTimeInMillis() + ".jpg";
                PictureMessage message = new PictureMessage(GroupCreation.this);
                JSONObject object = (JSONObject) message.createGroupProfileImageObject(serverFileName, file.getAbsolutePath());
                FileUploadDownloadManager fileUploadDownloadMgnr = new FileUploadDownloadManager(GroupCreation.this);
                fileUploadDownloadMgnr.startFileUpload(EventBus.getDefault(), object);


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


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



    private void updateData() {

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
        myList.add(SessionManager.getInstance(GroupCreation.this).getCurrentUserID());
        JSONArray jsArray = new JSONArray(myList);

        JSONObject obj = new JSONObject();
        try {
            obj.put("from", SessionManager.getInstance(GroupCreation.this).getCurrentUserID());
            obj.put("groupId", "" + epoch);
            obj.put("groupMembers", jsArray);
            obj.put("groupName", Mygroupname.getText().toString());
            obj.put("profilePic", pictureurl);
            obj.put("groupType", SocketManager.ACTION_NEW_GROUP);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_GROUP);
        messageEvent.setMessageObject(obj);
        EventBus.getDefault().post(messageEvent);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("ImageUri", cameraImageUri.toString());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("Team TBT result code", "" + requestCode + "" + data);

        if (requestCode == GALLERY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri selectedImageUri = data.getData();
                    beginCrop(selectedImageUri);
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
                beginCrop(cameraImageUri);

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

            try {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
                Bitmap alignedBitmap = ChatappImageUtils.getAlignedBitmap(bitmap, filePath);
                myProfilePic.setImageBitmap(alignedBitmap);

                imageChanged = true;
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                Toast.makeText(GroupCreation.this, "Out of memory!", Toast.LENGTH_LONG).show();
            }
        }


    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
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

