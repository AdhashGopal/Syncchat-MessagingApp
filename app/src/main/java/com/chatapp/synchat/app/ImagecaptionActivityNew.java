//package com.chatapp.android.app;
//
//import android.content.ActivityNotFoundException;
//import android.content.Context;
//import android.content.Intent;
//import android.database.Cursor;
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import android.graphics.Matrix;
//import android.media.ExifInterface;
//import android.media.ThumbnailUtils;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.StrictMode;
//import android.provider.DocumentsContract;
//import android.provider.MediaStore;
//import android.support.annotation.RequiresApi;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.Editable;
//import android.text.Html;
//import android.text.TextWatcher;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.chatapp.android.R;
//import com.chatapp.android.app.adapter.At_InfoAdapter;
//import com.chatapp.android.app.adapter.RItemAdapter;
//import com.chatapp.android.app.utils.AppUtils;
//import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
//import com.chatapp.android.core.CoreActivity;
//import com.chatapp.android.core.SessionManager;
//import com.chatapp.android.core.chatapphelperclass.ChatappImageUtils;
//import com.chatapp.android.core.message.MessageFactory;
//import com.chatapp.android.core.model.GroupMembersPojo;
//import com.chatapp.android.core.model.Imagepath_caption;
//import com.erikagtierrez.multiple_media_picker.Gallery;
//import com.soundcloud.android.crop.Crop;
//
//import java.io.ByteArrayOutputStream;
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
//import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
//import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
//import id.zelory.compressor.Compressor;
//import in.myinnos.awesomeimagepicker.models.Image;
//
//import static com.chatapp.android.app.ChatViewActivity.HTML_END_TAG;
//import static com.chatapp.android.app.ChatViewActivity.HTML_FRONT_TAG;
//
///**
// * created by  Adhash Team on 4/20/2017.
// */
//public class ImagecaptionActivityNew extends CoreActivity {
//    ImageView ibBack, delete, crop, rotate, keybordButton, sendbutton, selectimage, selEmoji, selectvideo, video_ic;
//    AvnNextLTProDemiTextView sendername;
//    AvnNextLTProDemiTextView Send_photo;
//    EmojiconEditText Edittext;
//    RecyclerView image_recyclerview;
//    ArrayList<Imagepath_caption> pathList;
//    List<Image> value;
//    View rootView;
//    Imagepath_caption imagepath_caption;
//    private static final int RESULT_LOAD_IMAGE = 1;
//    private static final int RESULT_LOAD_VIDEO = 2;
//    private static final int CAMERA_REQUEST_CODE = 3;
//    private static final int CAMERA_REQUEST = 1888;
//    private HorizontalAdapter horizontalAdapter;
//    EmojIconActions emojIcon;
//    int postionimage = 0;
//    Bitmap myBitmap;
//    private Uri cameraImageUri;
//    int rotateangle = 0;
//    String mReceiverName;
//    private Map<String, String> savedContactsMap;
//    ArrayList<Uri> uris = new ArrayList<Uri>();
//    String from = "";
//    public static List<GroupMembersPojo> at_memberlist = new ArrayList<GroupMembersPojo>();
//    private RecyclerView rvGroupMembers;
//    private At_InfoAdapter adapter;
//    private List<GroupMembersPojo> allMembersList;
//    boolean isImage = false;
//
//    private static final String TAG = ImagecaptionActivityNew.class.getSimpleName();
//
//    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (savedInstanceState != null) {
//            cameraImageUri = Uri.parse(savedInstanceState.getString("ImageUri"));
//        } else {
//            cameraImageUri = Uri.parse("");
//        }
//        setContentView(R.layout.image_captionadded_layout);
//        rvGroupMembers = (RecyclerView) findViewById(R.id.rvGroupMembers);
//        LinearLayoutManager llm = new LinearLayoutManager(this);
//        llm.setOrientation(LinearLayoutManager.VERTICAL);
//        rvGroupMembers.setLayoutManager(llm);
//        ibBack = (ImageView) findViewById(R.id.ibBack);
//        selectimage = (ImageView) findViewById(R.id.selectimage);
//        sendbutton = (ImageView) findViewById(R.id.enter_chat1);
//        delete = (ImageView) findViewById(R.id.delete);
//        selEmoji = (ImageView) findViewById(R.id.emojiButton);
//        crop = (ImageView) findViewById(R.id.crop);
//        rotate = (ImageView) findViewById(R.id.rotate);
//        pathList = new ArrayList<Imagepath_caption>();
//      //  getSupportActionBar().hide();
//        imagepath_caption = new Imagepath_caption();
//        rootView = findViewById(R.id.mainRelativeLayout);
//        //   popup = new EmojiconsPopup(rootView, this);
//        sendername = (AvnNextLTProDemiTextView) findViewById(R.id.sendername);
//        keybordButton = (ImageView) findViewById(R.id.keybordButton);
//        Edittext = (EmojiconEditText) findViewById(R.id.chat_edit_text1);
//        image_recyclerview = (RecyclerView) findViewById(R.id.image_recyclerview);
//        LinearLayoutManager mediaManager = new LinearLayoutManager(ImagecaptionActivityNew.this, LinearLayoutManager.HORIZONTAL, false);
//        image_recyclerview.setLayoutManager(mediaManager);
//        Send_photo = (AvnNextLTProDemiTextView) findViewById(R.id.Send_photo);
//        video_ic = (ImageView) findViewById(R.id.video_ic);
//        Intent intent_from = getIntent();
//        from = intent_from.getStringExtra("from");
//        mReceiverName = intent_from.getStringExtra("phoneno");
//        sendername.setText(mReceiverName);
//        if (intent_from.hasExtra("group")) {
////            Bundle bundle = getIntent().getExtras();
////            allMembersList = (ArrayList<GroupMembersPojo>) bundle.getSerializable("group");
//            allMembersList = ChatViewActivity.allMembersList;
//        }
//        sendername.setText(mReceiverName);
//
//        adapter = new At_InfoAdapter(ImagecaptionActivityNew.this, allMembersList);
//        rvGroupMembers.setAdapter(adapter);
//        adapter.notifyDataSetChanged();
//
//        adapter.setChatListItemClickListener(new At_InfoAdapter.AtInfoAdapterItemClickListener() {
//            @Override
//            public void onItemClick(GroupMembersPojo member, int position) {
//                Log.d("memberdetails", member.getName());
//                at_memberlist.add(member);
//                Edittext.append(Html.fromHtml("" + HTML_FRONT_TAG + member.getContactName() + HTML_END_TAG));
//                AppUtils.slideDown(rvGroupMembers);
//            }
//        });
//
//        if (from.contains("Gallary")) {
//
//            Intent intent= new Intent(this, Gallery.class);
//            // Set the title
//            intent.putExtra("title","Select media");
//            // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
//            intent.putExtra("mode",2);
//            intent.putExtra("maxSelection",10); // Optional
//            startActivityForResult(intent,RESULT_LOAD_IMAGE);
//
//            Send_photo.setText("Send Photo");
//        } else if (from.contains("Camera")) {
//            File parentDir = new File(MessageFactory.BASE_STORAGE_PATH);
//            File imgDir = new File(MessageFactory.IMAGE_STORAGE_PATH);
//            Send_photo.setText("Send Photo");
//            if (!parentDir.exists()) {
//                try {
//                    parentDir.createNewFile();
//                    if (!imgDir.exists()) {
//                        imgDir.createNewFile();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
//                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//                StrictMode.setVmPolicy(builder.build());
//            }
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            File cameraImageOutputFile = new File(
//                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                    createCameraImageFileName());
//            cameraImageUri = Uri.fromFile(cameraImageOutputFile);
//
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
//            intent.putExtra("android.intent.extras.CAMERA_FACING", 1);
//            startActivityForResult(intent, CAMERA_REQUEST_CODE);
//
//        } else {
//            Send_photo.setText("Send Video");
//            Intent intent= new Intent(this, Gallery.class);
//            // Set the title
//            intent.putExtra("title","Select media");
//            // Mode 1 for both images and videos selection, 2 for images only and 3 for videos!
//            intent.putExtra("mode",3);
//            intent.putExtra("maxSelection",10); // Optional
//            startActivityForResult(intent,RESULT_LOAD_VIDEO);
//        }
//
//
//        ibBack.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent cancelIntent = new Intent();
//                setResult(RESULT_CANCELED, cancelIntent);
//                finish();
//                pathList.clear();
//            }
//        });
//        emojIcon = new EmojIconActions(this, rootView, Edittext, selEmoji);
//        selEmoji.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                emojIcon.ShowEmojIcon();
//            }
//        });
//        emojIcon.ShowEmojIcon();
//        emojIcon.setIconsIds(R.drawable.ic_action_keyboard, R.drawable.smiley);
//        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
//            @Override
//            public void onKeyboardOpen() {
//
//                Log.e("", "Keyboard opened!");
//            }
//
//            @Override
//            public void onKeyboardClose() {
//                Log.e("", "Keyboard Closed!");
//
//            }
//        });
//
//        Edittext.addTextChangedListener(watch);
//        sendbutton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putSerializable("pathlist", pathList);
//                Intent okIntent = new Intent();
//                okIntent.putExtras(bundle);
//                setResult(RESULT_OK, okIntent);
//                finish();
//
//            }
//        });
//        crop.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                beginCrop(Uri.fromFile(new File(pathList.get(postionimage).getPath())));
//            }
//        });
//        rotate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                rotateangle = rotateangle + 90;
//                Bitmap rotatebitmap = RotateBitmap(myBitmap, rotateangle);
//                selectimage.setImageBitmap(rotatebitmap);
//                saveImage(rotatebitmap);
//            }
//        });
//        delete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (pathList == null)
//                    return;
//                if (pathList.size() > 1) {
//                    try {
//                        if (postionimage < pathList.size())
//                            pathList.remove(postionimage);
//                        if (pathList.size() > 0) {
//                            horizontalAdapter = new HorizontalAdapter();
//                            image_recyclerview.setAdapter(horizontalAdapter);
//                            if (pathList.size() > 0) {
//                                myBitmap = BitmapFactory.decodeFile(pathList.get(0).getPath());
//                                selectimage.setImageBitmap(myBitmap);
//                                if (pathList.size() == 1) {
//                                    image_recyclerview.setVisibility(View.GONE);
//                                    postionimage = 0;
//                                } else {
//                                    image_recyclerview.setVisibility(View.VISIBLE);
//                                }
//                            } else {
//                                finish();
//                            }
//                        } else {
//                            finish();
//                        }
//                    } catch (Exception e) {
//                        Log.e(TAG, "onClick: ", e);
//                    }
//
//                } else {
//                    finish();
//                }
//            }
//        });
//
//        image_recyclerview.addOnItemTouchListener(new RItemAdapter(ImagecaptionActivityNew.this, image_recyclerview, new RItemAdapter.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, int position) {
//
//                if(isImage)
//                {
//                    myBitmap = BitmapFactory.decodeFile(pathList.get(position).getPath());
//                }else{
//                    myBitmap = (ThumbnailUtils.createVideoThumbnail(pathList.get(position).getPath(),
//                            MediaStore.Images.Thumbnails.MINI_KIND));
//                }
//
//                selectimage.setImageBitmap(myBitmap);
//                postionimage = position;
//                Edittext.setText(pathList.get(position).getCaption());
//
//            }
//
//            @Override
//            public void onItemLongClick(View view, int position) {
//
//            }
//        }));
//    }
//
//    TextWatcher watch = new TextWatcher() {
//
//        @Override
//        public void afterTextChanged(Editable arg0) {
//            // TODO Auto-generated method stub
//            if (postionimage > -1 && pathList.size() > 0) {
//                pathList.get(postionimage).setCaption(Edittext.getText().toString().trim());
//            }
//        }
//
//        @Override
//        public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
//                                      int arg3) {
//            // TODO Auto-generated method stub
//
//        }
//
//        @Override
//        public void onTextChanged(CharSequence cs, int a, int b, int c) {
//            // TODO Auto-generated method
//
//            if (cs.length() > 0) {
//                if (ChatViewActivity.isGroupChat) {
//                    if (cs.length() == 1) {
//                        String value = cs.toString();
//                        if (value.equalsIgnoreCase("@")) {
//                            if (allMembersList != null && allMembersList.size() > 0)
//                                AppUtils.slideUp(rvGroupMembers);
//                        }
//                    } else {
//                        String value = cs.toString();
//                        String value1 = value.substring(value.length() - 2);
//                        if (value1.contains(" @")) {
//                            if (allMembersList != null && allMembersList.size() > 0)
//                                AppUtils.slideUp(rvGroupMembers);
//                        }
//                    }
//                }
//
//            } else {
//
//                if (ChatViewActivity.isGroupChat) {
//                    AppUtils.slideDown(rvGroupMembers);
//                }
//
//            }
//
//
//        }
//    };
//
//    private String createCameraImageFileName() {
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        return timeStamp + ".jpg";
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            try {
//                isImage = true;
//
//                ArrayList<String> selectionResult=data.getStringArrayListExtra("result");
//
//                if (selectionResult.size() == 0) {
//                    finish();
//                }
//                for (int i = 0; i < selectionResult.size(); i++) {
//                    imagepath_caption = new Imagepath_caption();
//                    imagepath_caption.setPath(selectionResult.get(i));
//                    imagepath_caption.setCaption("");
//                    pathList.add(imagepath_caption);
//                }
//
//                myBitmap = BitmapFactory.decodeFile(pathList.get(postionimage).getPath());
//
//                selectimage.setVisibility(View.VISIBLE);
//
//                selectimage.setImageBitmap(myBitmap);
//                if (pathList.size() == 1) {
//                    image_recyclerview.setVisibility(View.GONE);
//                } else {
//                    image_recyclerview.setVisibility(View.VISIBLE);
//                }
//
//                horizontalAdapter = new HorizontalAdapter();
//                image_recyclerview.setAdapter(horizontalAdapter);
//
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else if (resultCode == RESULT_CANCELED) {
//            pathList.clear();
//            finish();
//        } else if (requestCode == RESULT_LOAD_VIDEO && resultCode == RESULT_OK) {
//            Uri selectedVideUri = data.getData();
//            isImage = false;
//
//            ArrayList<String> selectionResult=data.getStringArrayListExtra("result");
//
//            if (selectionResult.size() == 0) {
//                finish();
//            }
//            for (int i = 0; i < selectionResult.size(); i++) {
//
//                File file = new File(selectionResult.get(i));
//                long fileSize = file.length();
//
//                String strMaxSize = SessionManager.getInstance(ImagecaptionActivityNew.this).getFileUploadMaxSize();
//                long maxSize = 5;
//                try {
//                    maxSize = Long.parseLong(strMaxSize);
//                } catch (NumberFormatException e) {
//                    e.printStackTrace();
//                }
//                long maxSizeInBytes = maxSize * 1024 * 1024;
////
//                if (fileSize < maxSizeInBytes) {
//                    imagepath_caption = new Imagepath_caption();
//                    imagepath_caption.setPath(selectionResult.get(i));
//                    imagepath_caption.setCaption("");
//                    pathList.add(imagepath_caption);
//                    myBitmap = (ThumbnailUtils.createVideoThumbnail(pathList.get(postionimage).getPath(),
//                            MediaStore.Images.Thumbnails.MINI_KIND));
//
//                    selectimage.setImageBitmap(myBitmap);
//                    crop.setVisibility(View.GONE);
//                    rotate.setVisibility(View.GONE);
//                    delete.setVisibility(View.GONE);
//                    video_ic.setVisibility(View.VISIBLE);
//
//
//                    video_ic.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            try {
//                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pathList.get(postionimage).getPath()));
//                                String path = "file://" + pathList.get(postionimage).getPath();
//                                intent.setDataAndType(Uri.parse(path), "video/*");
//                                startActivity(intent);
//                            } catch (ActivityNotFoundException e) {
//                                Toast.makeText(ImagecaptionActivityNew.this, "No app installed to play this video", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//                } else {
//                    Toast.makeText(ImagecaptionActivityNew.this, "You can upload maximum " + maxSize + " MB file only", Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//
//            myBitmap = (ThumbnailUtils.createVideoThumbnail(pathList.get(0).getPath(), MediaStore.Images.Thumbnails.MINI_KIND));
//
//            selectimage.setVisibility(View.VISIBLE);
//
//            selectimage.setImageBitmap(myBitmap);
//            if (pathList.size() == 1) {
//                image_recyclerview.setVisibility(View.GONE);
//            } else {
//                image_recyclerview.setVisibility(View.VISIBLE);
//            }
//
//            horizontalAdapter = new HorizontalAdapter();
//            image_recyclerview.setAdapter(horizontalAdapter);
//
//            }
//        if (requestCode == Crop.REQUEST_CROP && resultCode == RESULT_OK) {
//            Uri uri = Crop.getOutput(data);
//            String filePath = uri.getPath();
//            File image = new File(filePath);
//            Bitmap compressBmp = Compressor.getDefault(ImagecaptionActivityNew.this).compressToBitmap(image);
//            selectimage.setImageBitmap(compressBmp);
//            myBitmap = compressBmp;
//            saveImage(compressBmp);
//
//            } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
//
//            try {
//
//                String path = cameraImageUri.getPath();
//                if (path == null) {
//                    path = getRealFilePath(data);
//                }
//                Log.d("CameraResult", path);
//                imagepath_caption = new Imagepath_caption();
//                imagepath_caption.setPath(path);
//                imagepath_caption.setCaption("");
//                pathList.add(imagepath_caption);
//                myBitmap = BitmapFactory.decodeFile(pathList.get(0).getPath());
//                Matrix matrix = new Matrix();
//                matrix.postRotate(getImageOrientation(path));
//                Bitmap rotatedBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(),
//                        myBitmap.getHeight(), matrix, true);
//
//                selectimage.setImageBitmap(rotatedBitmap);
//
//
////                sendImageChatMessage(path);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//        }
//
//    }
//
//    private String getRealFilePath(Intent data) {
//        Uri selectedImage = data.getData();
//        String wholeID = DocumentsContract.getDocumentId(selectedImage);
//        // Split at colon, use second item in the array
//        String id = wholeID.split(":")[1];
//        String[] column = {MediaStore.Images.Media.DATA};
//        // where id is equal to
//        String sel = MediaStore.Images.Media._ID + "=?";
//        Cursor cursor = getContentResolver().
//                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        column, sel, new String[]{id}, null);
//        String filePath = "";
//        int columnIndex = cursor.getColumnIndex(column[0]);
//        if (cursor.moveToFirst()) {
//            filePath = cursor.getString(columnIndex);
//        }
//        cursor.close();
//        return filePath;
//    }
//
//    private String getVideoRealFilePath(Uri selectedUri) {
//
//        String wholeID = DocumentsContract.getDocumentId(selectedUri);
//        // Split at colon, use second item in the array
//        String id = wholeID.split(":")[1];
//        String[] column = {MediaStore.Video.Media.DATA};
//        // where id is equal to
//        String sel = MediaStore.Video.Media._ID + "=?";
//        Cursor cursor = getContentResolver().
//                query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
//                        column, sel, new String[]{id}, null);
//        String filePath = "";
//        int columnIndex = cursor.getColumnIndex(column[0]);
//        if (cursor.moveToFirst()) {
//            filePath = cursor.getString(columnIndex);
//        }
//        cursor.close();
//        return filePath;
//    }
//
//
//    private void saveImage(Bitmap finalBitmap) {
//
//        File imgDir = new File(MessageFactory.IMAGE_STORAGE_PATH);
//        if (!imgDir.exists()) {
//            imgDir.mkdirs();
//        }
//        Random generator = new Random();
//        int n = 10000;
//        n = generator.nextInt(n);
//        String fname = "Image-" + n + ".jpg";
//        File file = new File(imgDir, fname);
//        if (file.exists()) file.delete();
//        try {
//            FileOutputStream out = new FileOutputStream(file);
//            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//            out.flush();
//            out.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        pathList.get(postionimage).setPath(file.getAbsolutePath());
//
//        if (horizontalAdapter != null) {
//            horizontalAdapter.notifyDataSetChanged();
//        }
//    }
//
//    public class HorizontalAdapter extends RecyclerView.Adapter<HorizontalAdapter.MyViewHolder> {
//
//
//        public class MyViewHolder extends RecyclerView.ViewHolder {
//            ImageView listimage;
//
//            public MyViewHolder(View view) {
//                super(view);
//
//                listimage = (ImageView) view.findViewById(R.id.listimage);
//            }
//        }
//
//        @Override
//        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//            View itemView = LayoutInflater.from(parent.getContext())
//                    .inflate(R.layout.image_horizantalrecyclerview_layout, parent, false);
//
//            return new MyViewHolder(itemView);
//        }
//
//        @Override
//        public void onBindViewHolder(final MyViewHolder holder, final int position) {
//
//            Bitmap myBitmap1;
//            if(isImage){
//                myBitmap1 = ChatappImageUtils.decodeBitmapFromFile(pathList.get(position).getPath(), 80, 80);
//            }else{
//                myBitmap1 = (ThumbnailUtils.createVideoThumbnail(pathList.get(position).getPath(), MediaStore.Images.Thumbnails.MICRO_KIND));
//            }
//
//            holder.listimage.setImageBitmap(myBitmap1);
//        }
//
//        @Override
//        public int getItemCount() {
//            return pathList.size();
//        }
//    }
//
//    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId) {
//        iconToBeChanged.setImageResource(drawableResourceId);
//    }
//
//    private void beginCrop(Uri source) {
//        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
//        Crop.of(source, destination).asSquare().start(this);
//    }
//
//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        try {
//            bytes.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }
//
//    public static Bitmap RotateBitmap(Bitmap source, float angle) {
//        Matrix matrix = new Matrix();
//        matrix.postRotate(angle);
//        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
//    }
//
//    @Override
//    public void onSaveInstanceState(Bundle bundle) {
//        super.onSaveInstanceState(bundle);
//        bundle.putString("ImageUri", cameraImageUri.toString());
//    }
//
//    public static int getImageOrientation(String imagePath) {
//        int rotate = 0;
//        try {
//
//            File imageFile = new File(imagePath);
//            ExifInterface exif = new ExifInterface(
//                    imageFile.getAbsolutePath());
//            int orientation = exif.getAttributeInt(
//                    ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    rotate = 270;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    rotate = 180;
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    rotate = 90;
//                    break;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return rotate;
//    }
//}