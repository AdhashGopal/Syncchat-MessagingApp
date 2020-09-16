package com.chatapp.synchat.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.dialog.CustomMultiTextItemsDialog;
import com.chatapp.synchat.app.utils.AppUtils;
import com.chatapp.synchat.app.widget.CircleImageView;
import com.chatapp.synchat.core.ActivityLauncher;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.chatapphelperclass.ChatappPermissionValidator;
import com.chatapp.synchat.core.model.MultiTextDialogPojo;
import com.chatapp.synchat.core.model.PictureModel;
import com.chatapp.synchat.core.model.SCLoginModel;
import com.chatapp.synchat.core.chatapphelperclass.ChatappImageUtils;
import com.chatapp.synchat.core.chatapphelperclass.ChatappDialogUtils;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.socket.SocketManager;
import com.soundcloud.android.crop.Crop;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


import io.socket.client.Socket;


public class ChatappProfileInfoScreen extends CoreActivity implements View.OnClickListener {
    private static final int REQUEST_CODE_GALLERY = 1;
    private static final int REQUEST_CODE_CAMERA = 2;
    private static final int REQUEST_CROP_ICON = 3;
    private Uri cameraImageUri;
    private CircleImageView choose_photo;

    private String tag_string_req = "string_req";
    protected static final String TAG = "ActionBarActivity";
    private PictureModel pictureModel = null;
    private SCLoginModel SCLoginModel = null;

    private String pictureUrl = "", mCurrentUserId;
    private EditText nameEditText;
    private SessionManager msessionmanager;

    private boolean isDeninedRTPs = false;
    private boolean showRationaleRTPs = false;
    private ArrayList<ChatappPermissionValidator.Constants> myPermissionConstantsArrayList;
    private final int REQUEST_CODE_PERMISSION_MULTIPLE = 123;
    private final AtomicInteger mRefreshRequestCounter = new AtomicInteger(0);
    /* Global variables for empjis */
    private FrameLayout emoji;
    private TextView title;
    //  LinearLayout emoji;
    private ImageButton happyFace;

    final Context context = this;

    private ImageView tvNext;

    private SocketManager mSocketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info_screen);


        if (savedInstanceState != null) {
            cameraImageUri = Uri.parse(savedInstanceState.getString("ImageUri"));
        } else {
            cameraImageUri = Uri.parse("");
        }

        mCurrentUserId = SessionManager.getInstance(ChatappProfileInfoScreen.this).getCurrentUserID();

        initSocketManagerCallback();
        mSocketManager.connect();

        nameEditText = (EditText) findViewById(R.id.typeName_profileInfo);
        tvNext = (ImageView) findViewById(R.id.tvNext);
        tvNext.setOnClickListener(ChatappProfileInfoScreen.this);
//        setTitle(R.string.profile_info);

        initProgress("Loading...", true);


        nameEditText.setImeOptions(EditorInfo.IME_ACTION_GO);
        msessionmanager = SessionManager.getInstance(ChatappProfileInfoScreen.this);
        //emoji = (FrameLayout) findViewById(R.id.emojicons);

        // happyFace = (ImageButton) findViewById(R.id.happyFace);
        //  final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //final View rootView = findViewById(R.id.mainLayout);
       /* emojIcon = new EmojIconActions(this, rootView, nameEditText, happyFace);
        happyFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojIcon.ShowEmojIcon();
            }
        });
        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {

                Log.e(TAG, "Keyboard opened!");
            }

            @Override
            public void onKeyboardClose() {
                Log.e(TAG, "Keyboard Closed!");

            }
        });
        emojIcon.setUseSystemEmoji(false);*/
       /* final EmojiconsPopup popup = new EmojiconsPopup(rootView, this);
        popup.setSizeForSoftKeyboard();

        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {
            @Override
            public void onKeyboardOpen(int keyBoardHeight) {
            }

            @Override
            public void onKeyboardClose() {
                if (popup.isShowing())
                    popup.dismiss();
            }
        });


        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {
            @Override
            public void onEmojiconClicked(github.ankushsachdeva.emojicon.emoji.Emojicon emojicon) {
                if (nameEditText == null || emojicon == null) {
                    return;
                }
                int start = nameEditText.getSelectionStart();
                int end = nameEditText.getSelectionEnd();
                if (start < 0) {
                    nameEditText.append(emojicon.getEmoji());
                } else {
                    nameEditText.getText().replace(Math.min(start, end),
                            Math.max(start, end), emojicon.getEmoji(), 0,
                            emojicon.getEmoji().length());
                }
            }
        });
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                nameEditText.dispatchKeyEvent(event);
            }
        });
        happyFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!popup.isShowing()) {
                    if (popup.isKeyBoardOpen()) {
                        popup.showAtBottom();
                    } else {
                        nameEditText.setFocusableInTouchMode(true);
                        nameEditText.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(nameEditText, InputMethodManager.SHOW_IMPLICIT);
                    }
                } else {
                    popup.dismiss();
                }
            }
        });
*/


/*        nameEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showKeyboard();

            }
        });*/


        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                    showKeyboard();
            }
        });
        nameEditText.setOnKeyListener(new View.OnKeyListener() {

            public boolean onKey(View v, int keyCode, KeyEvent event) {

                // Just ignore the [Enter] key
// Handle all other keys in the default way
                return keyCode == KeyEvent.KEYCODE_ENTER;

            }


        });

        nameEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                return false;
            }
        });

        /* Initilise the dialog here */

        Bundle bundle = getIntent().getExtras();
        /* Get the mobile number from the previous activity */

        choose_photo = (CircleImageView) findViewById(R.id.selectImage_profileInfo);
        if (msessionmanager.getnameOfCurrentUser() != null && !msessionmanager.getnameOfCurrentUser().isEmpty()) {
            nameEditText.setText(msessionmanager.getnameOfCurrentUser());
        }
        if (msessionmanager.getUserProfilePic() != null) {
            String imgPath = Constants.SOCKET_IP + msessionmanager.getUserProfilePic();

            if (imgPath!=null && !imgPath.isEmpty()) {
                /*Glide.with(ChatappProfileInfoScreen.this).load(imgPath).error(
                        R.drawable.ic_profile_nav_header).into(choose_photo);*/

                AppUtils.loadImage(ChatappProfileInfoScreen.this,imgPath,choose_photo,150,R.drawable.ic_profile_default);
            } else {
                choose_photo.setImageResource(R.drawable.ic_profile_default);
            }
        }
        checkAndRequestPermissions();
    }

    private void initSocketManagerCallback() {
        mSocketManager = new SocketManager(ChatappProfileInfoScreen.this, new SocketManager.SocketCallBack() {
            @Override
            public void onSuccessListener(String eventName, Object... response) {
                if (eventName.equalsIgnoreCase(SocketManager.EVENT_FILE_RECEIVED)) {
                    try {
                        JSONObject object = new JSONObject(response[0].toString());
                        String from = object.getString("from");
                        String imgName = object.getString("ImageName");

                        if (from.equalsIgnoreCase(mCurrentUserId)) {
                            JSONObject imgObject = new JSONObject();

                            imgObject.put("from", mCurrentUserId);
                            imgObject.put("type", "single");
                            imgObject.put("ImageName", imgName);
                            imgObject.put("removePhoto", "");
                            mSocketManager.send(imgObject, SocketManager.EVENT_IMAGE_UPLOAD);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (eventName.equalsIgnoreCase(SocketManager.EVENT_IMAGE_UPLOAD)) {
                    try {
                        JSONObject objects = new JSONObject(response[0].toString());
                        final String message = objects.getString("message");
                        String from = objects.getString("from");
                        String type = objects.getString("type");

                        if (from.equalsIgnoreCase(mCurrentUserId) && type.equalsIgnoreCase("single")) {
                            String path = objects.getString("file") + "?id=" + Calendar.getInstance().getTimeInMillis();
                            SessionManager.getInstance(ChatappProfileInfoScreen.this).setUserProfilePic(path);

                            hideProgressDialog();
                            if(path.startsWith("./")){
                               path=path.replaceFirst("./","");
                            }
                          final  String imgPath = Constants.SOCKET_IP + path;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Toast.makeText(ChatappProfileInfoScreen.this, message, Toast.LENGTH_SHORT).show();

                                    AppUtils.loadImageSmooth(ChatappProfileInfoScreen.this,imgPath,choose_photo,150,R.drawable.ic_profile_default);
                                    Toast.makeText(ChatappProfileInfoScreen.this, "Profile Image Successfully Updated", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (eventName.equalsIgnoreCase(SocketManager.EVENT_CHANGE_USER_NAME)) {
                    try {
                        JSONObject object = new JSONObject(response[0].toString());
                        String err = object.getString("err");

                        if (err.equals("0")) {

                            String from = object.getString("from");
                            if (from.equalsIgnoreCase(mCurrentUserId)) {
                                goBackupRestoreScreen();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if (eventName.equalsIgnoreCase(Socket.EVENT_CONNECT)) {
                    createUser();
                }
            }
        });
    }

    private void createUser() {
        try {
            JSONObject object = new JSONObject();
            object.put("_id", mCurrentUserId);
            object.put("mode", "phone");
            object.put("chat_type", "single");
            mSocketManager.send(object, SocketManager.EVENT_CREATE_USER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            myPermissionConstantsArrayList = new ArrayList<>();
            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.PERMISSION_CAMERA);
            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.PERMISSION_READ_EXTERNAL_STORAGE);
//            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.Record_Audio);
            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.Record_setting);
            myPermissionConstantsArrayList.add(ChatappPermissionValidator.Constants.PERMISSION_WRITE_EXTERNAL_STORAGE);
            if (ChatappPermissionValidator.checkPermission(ChatappProfileInfoScreen.this, myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE)) {
                onPermissionGranted();
            }
        } else {
            onPermissionGranted();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString("ImageUri", cameraImageUri.toString());
    }


    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_MULTIPLE:
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++) {
                        String permission = permissions[i];
                        if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                            isDeninedRTPs = true;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                showRationaleRTPs = shouldShowRequestPermissionRationale(permission);
                            }
                        }
                        break;
                    }
                    onPermissionResult();
                } else {
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                break;
        }
    }

    private void onPermissionResult() {
        if (isDeninedRTPs) {
            if (!showRationaleRTPs) {
                //goToSettings();
                ChatappDialogUtils.showPermissionDeniedDialog(ChatappProfileInfoScreen.this);
            } else {
                isDeninedRTPs = false;
                ChatappPermissionValidator.checkPermission(this,
                        myPermissionConstantsArrayList, REQUEST_CODE_PERMISSION_MULTIPLE);
            }
        } else {
            onPermissionGranted();
        }
    }


    private void onPermissionGranted() {
        choose_photo.setOnClickListener(ChatappProfileInfoScreen.this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
  //    getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch (item.getItemId()) {
            case R.id.action_settings1:
                loadNext();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
        return super.onOptionsItemSelected(item);
    }

    private void loadNext() {
        if (nameEditText.getText().toString().trim().length() > 0) {

            uploadDataToServer();

//            final Dialog dialog = new Dialog(ChatappProfileInfoScreen.this);
//            AvnNextLTProDemiTextView enterchat;
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            dialog.setContentView(R.layout.dialog_verificationcode);
//            enterchat = (AvnNextLTProDemiTextView) dialog.findViewById(R.id.enterchat);
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.setCancelable(false);
//            dialog.show();
//            enterchat.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    uploadDataToServer();
//                    dialog.dismiss();
//                }
//            });


        } else {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadDataToServer() {
        String changedName = nameEditText.getText().toString().trim();
        if (!SessionManager.getInstance(ChatappProfileInfoScreen.this).getnameOfCurrentUser().equals(changedName)) {
            showProgressDialog();

            try {
                JSONObject object = new JSONObject();
                object.put("from", mCurrentUserId);
                object.put("name", changedName);
                mSocketManager.send(object, SocketManager.EVENT_CHANGE_USER_NAME);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            goBackupRestoreScreen();
        }
    }

    private void goBackupRestoreScreen() {
        mSocketManager.disconnect();

        SessionManager sessionMAnager = SessionManager.getInstance(ChatappProfileInfoScreen.this);
        sessionMAnager.setnameOfCurrentUser(nameEditText.getText().toString());
        sessionMAnager.IsprofileUpdate(true);

        String loginCount = sessionMAnager.getLoginCount();
        if (loginCount.equalsIgnoreCase("0")) {
            sessionMAnager.IsBackupRestored(true);
            ActivityLauncher.launchPinscreen(ChatappProfileInfoScreen.this, SessionManager.getInstance(ChatappProfileInfoScreen.this).getPhoneNumberOfCurrentUser());
//            ActivityLauncher.launchHomeScreen(ChatappProfileInfoScreen.this);
        } else {
            ActivityLauncher.launchPinscreen(ChatappProfileInfoScreen.this, SessionManager.getInstance(ChatappProfileInfoScreen.this).getPhoneNumberOfCurrentUser());

      //      ActivityLauncher.launchBackUpRestoreScreen(ChatappProfileInfoScreen.this);
        }
    }

    private void uploadImage(String selectedFilePath) {

        showProgressDialog();

        String currentUserId = SessionManager.getInstance(ChatappProfileInfoScreen.this).getCurrentUserID();
        String serverFileName = currentUserId.concat(".jpg");

        try {
            File file = new File(selectedFilePath);
            byte[] buffer = new byte[(int) file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(buffer); //read file into bytes[]
            fis.close();

            JSONObject imgObj = new JSONObject();
            imgObj.put("ImageName", serverFileName);
            imgObj.put("buffer", buffer);
            imgObj.put("bufferAt", 0);
            imgObj.put("from", currentUserId);
            imgObj.put("uploadType", "single");
            imgObj.put("removePhoto", "");
            imgObj.put("FileEnd", 1);

            mSocketManager.send(imgObj, SocketManager.EVENT_FILE_UPLOAD);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA) {
            if (resultCode == Activity.RESULT_OK) {
                beginCrop(cameraImageUri);
            } else {
                if (resultCode == Activity.RESULT_CANCELED) {

                } else {
                    Toast.makeText(this, "Sorry! Failed to capture image",
                            Toast.LENGTH_SHORT).show();
                }
            }
        } else if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                beginCrop(selectedImageUri);
            }
        } else if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
            Uri uri = Crop.getOutput(data);
            String selectedFilePath = uri.getPath();

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(selectedFilePath, options);
                Bitmap alignedBitmap = ChatappImageUtils.getAlignedBitmap(bitmap, selectedFilePath);
                /*try {
                    // make a new bitmap from your file
                    OutputStream outStream = null;
                    File file=new File(selectedFilePath);

                    outStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
                    outStream.flush();
                    outStream.close();
                    uploadImage(file.getPath());

                } catch (Exception e) {
                    uploadImage(selectedFilePath);
                }*/
                choose_photo.setImageBitmap(alignedBitmap);



               /* BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                choose_photo.setBackgroundDrawable(ob);*/


                uploadImage(selectedFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            choose_photo.setImageURI(Crop.getOutput(result));
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.selectImage_profileInfo) {
            hideKeyboard();
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
                            startActivityForResult(intent, REQUEST_CODE_CAMERA);
                            break;

                        case 1:
                            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                            photoPickerIntent.setType("image/*");
                            startActivityForResult(photoPickerIntent, REQUEST_CODE_GALLERY);
                            break;

                    }
                }
            });

            dialog.show(getSupportFragmentManager(), "Profile Pic");
        } else if (view.getId() == R.id.tvNext) {
            loadNext();
        }
    }


    private String createCameraImageFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return timeStamp + ".jpg";
    }

}
