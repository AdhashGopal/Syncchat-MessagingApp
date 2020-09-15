package com.chatapp.android.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.chatapp.android.BuildConfig;
import com.chatapp.android.R;
import com.chatapp.android.app.adapter.GridViewAdapter;
import com.chatapp.android.app.utils.ConstantMethods;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.chatapphelperclass.ChatappImageUtils;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.ImageItem;
import com.chatapp.android.core.model.MessageItemChat;

import java.io.File;
import java.util.ArrayList;

/**
 * created by  Adhash Team on 3/10/2017.
 */
public class MediaFragment extends Fragment {
    MessageDbController db;
    GridView grid;
    private ArrayList<MessageItemChat> mChatData;
    private String docid;
    private GridViewAdapter gridlistadapter;
    private ArrayList<MessageItemChat> gridlist;
    private ArrayList<String> imgzoompath;
    private ArrayList<String> imgs = new ArrayList<String>();
    private ArrayList<String> imgscaption = new ArrayList<String>();


    /**
     * constructor
     */
    public MediaFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mediafragement_layout, container, false);
        Session session = new Session(getActivity());
        db = CoreController.getDBInstance(getActivity());
        grid = (GridView) view.findViewById(R.id.grid);
        docid = session.getMediaDocid();

        mChatData = new ArrayList<>();
        gridlist = new ArrayList<MessageItemChat>();
        imgzoompath = new ArrayList<String>();
        loadFromDB();

        return view;
    }

    /**
     * Get local database value
     */
    private void loadFromDB() {
        try {
            ArrayList<MessageItemChat> items;
            items = db.selectAllChatMessages(docid, ConstantMethods.getChatType(docid));
            mChatData.clear();
            mChatData.addAll(items);
            mediafile();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get media file from local database & move specific view
     */
    protected void mediafile() {
        for (int i = 0; i < mChatData.size(); i++) {
            String type = mChatData.get(i).getMessageType();
            int mtype = Integer.parseInt(type);
            if (MessageFactory.picture == mtype) {
                MessageItemChat msgItem = mChatData.get(i);
                if (msgItem.getImagePath() != null) {
                    String path = msgItem.getImagePath();
                    File file = new File(path);
                    if (file.exists()) {
                        imgzoompath.add(path);
                        imgs.add(path);
                        if (mChatData.get(i).getCaption() != null)
                            imgscaption.add(mChatData.get(i).getCaption());
                        else
                            imgscaption.add("");
                        gridlist.add(msgItem);
                    }
                } else if (msgItem.getChatFileLocalPath() != null) {
                    String path = msgItem.getChatFileLocalPath();
                    File file = new File(path);
                    if (file.exists()) {
                        imgzoompath.add(path);
                        imgs.add(path);
                        if (mChatData.get(i).getCaption() != null)
                            imgscaption.add(mChatData.get(i).getCaption());
                        else
                            imgscaption.add("");
                        gridlist.add(msgItem);
                    }
                }

            } else if (MessageFactory.video == mtype) {
                MessageItemChat msgItem = mChatData.get(i);
                if (msgItem.getVideoPath() != null) {

                    String path = msgItem.getVideoPath();
                    File file = new File(path);
                    if (file.exists()) {
                        imgzoompath.add(path);
                        gridlist.add(msgItem);
                    }
                } else if (msgItem.getChatFileLocalPath() != null) {

                    String path = msgItem.getChatFileLocalPath();
                    File file = new File(path);
                    if (file.exists()) {
                        imgzoompath.add(path);
                        gridlist.add(msgItem);
                    }
                }
            } else if (MessageFactory.audio == mtype) {
//                if (mChatData.get(i).getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {
                MessageItemChat msgItem = mChatData.get(i);
                if (msgItem.getAudioPath() != null) {
                    String path = msgItem.getAudioPath();
                    File file = new File(path);
                    if (file.exists()) {
                        imgzoompath.add(path);
                        gridlist.add(msgItem);
                    }

                } else if (msgItem.getChatFileLocalPath() != null) {

                    String path = msgItem.getChatFileLocalPath();
                    File file = new File(path);
                    if (file.exists()) {
                        imgzoompath.add(path);
                        gridlist.add(msgItem);
                    }
                }
                /*}else {

                }*/
            }

        }


        gridlistadapter = new GridViewAdapter(getActivity(), R.layout.grid_item_layout, getData());
        grid.setAdapter(gridlistadapter);
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageItem item = (ImageItem) parent.getItemAtPosition(position);

                switch (Integer.parseInt(gridlist.get(position).getMessageType())) {
                    case MessageFactory.picture:
                        try {
                            Intent intent = new Intent(getActivity(), ImageZoom.class);
                            intent.putExtra("from", "media");
                            intent.putExtra("image_list", imgs);
                            intent.putExtra("image_position", getImagePathPos(imgzoompath.get(position)));
                            intent.putExtra("captiontext_list", imgscaption);
                            intent.putExtra("captiontext", imgscaption.get(getImagePathPos(imgzoompath.get(position))));
                            intent.putExtra("image", imgzoompath.get(position));
                            Log.e("IMAGE", imgzoompath.get(position));

                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        break;
                    case MessageFactory.video:
                        CallIntentFileProvider(position);
                        break;
                    case MessageFactory.audio:
                        CallIntentFileProvider(position);
                        break;
                    default:
                        Toast.makeText(getActivity(), "No app installed to play this", Toast.LENGTH_SHORT).show();
                        break;
                }

/*
                if (Integer.parseInt(gridlist.get(position).getMessageType()) == (MessageFactory.picture)) {
                    try {
                        Intent intent = new Intent(getActivity(), ImageZoom.class);
                        intent.putExtra("from", "media");
                        intent.putExtra("image_list", imgs);
                        intent.putExtra("image_position", position);
                        intent.putExtra("captiontext_list", imgscaption);
                        intent.putExtra("captiontext", imgscaption.get(position));
                        intent.putExtra("image", imgzoompath.get(position));

                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else if (Integer.parseInt(gridlist.get(position).getMessageType()) == (MessageFactory.video)) {
                    *//*Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(imgzoompath.get(position)));
                    intent.setDataAndType(Uri.parse(imgzoompath.get(position)), "video/*");
                    startActivity(intent);*//*
                    CallIntentFileProvider(position);

                } else if (Integer.parseInt(mChatData.get(position).getMessageType()) == (MessageFactory.audio)) {
                    MessageItemChat msgItem = mChatData.get(position);
                    try {
//                        if (msgItem.getaudiotype() == MessageFactory.AUDIO_FROM_RECORD) {

                        String extension = MimeTypeMap.getFileExtensionFromUrl(imgzoompath.get(position));
                        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);


//                            Intent testIntent = new Intent(Intent.ACTION_VIEW);
//                            testIntent.setType(mimeType);


                           *//* File file = new File(imgzoompath.get(position));
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID, file);
                            intent.setDataAndType(uri, mimeType);
                            startActivity(intent);*//*

                        CallIntentFileProvider(position);
//

                       *//* } else {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse(imgzoompath.get(position)), "audio/*");
                            startActivity(intent);

                        }*//*
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getActivity(), "No app installed to play this audio", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity(), "No app installed to play this audio", Toast.LENGTH_SHORT).show();

                }*/


            }
        });

    }

    /**
     * get Image Path Position
     *
     * @param imagePath input value (imagePath)
     * @return value
     */
    private int getImagePathPos(String imagePath) {
        return imgs.indexOf(imagePath);
    }

    /**
     * Call Intent FileProvider
     *
     * @param position specific item
     */
    private void CallIntentFileProvider(int position) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(imgzoompath.get(position));
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

        File file = new File(imgzoompath.get(position));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID, file);
        intent.setDataAndType(uri, mimeType);
        startActivity(intent);
    }

    /**
     * get media based on type(image,audia,document,etc,.)
     *
     * @return value
     */
    private ArrayList<ImageItem> getData() {
        final ArrayList<ImageItem> imageItems = new ArrayList<>();
        try {

            Bitmap audioBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_media_audio);
//        audioBitmap = ChatappImageUtils.getResizedBitmap(audioBitmap, 100);

            for (int i = 0; i < mChatData.size(); i++) {
                if (Integer.parseInt(mChatData.get(i).getMessageType()) == (MessageFactory.picture)) {
                    MessageItemChat msgItem = mChatData.get(i);
                    if (msgItem.getImagePath() != null) {
                        String path = msgItem.getImagePath();
                        File file = new File(path);
                        if (file.exists()) {
                            Bitmap myBitmap = ChatappImageUtils.decodeBitmapFromFile(path, 100, 100);
                            imageItems.add(new ImageItem(myBitmap, "picture" + i, ""));
                        }
                    } else if (msgItem.getChatFileLocalPath() != null) {
                        String path = msgItem.getChatFileLocalPath();
                        File file = new File(path);
                        if (file.exists()) {
                            Bitmap myBitmap = ChatappImageUtils.decodeBitmapFromFile(path, 100, 100);// BitmapFactory.decodeFile(path);
                            imageItems.add(new ImageItem(myBitmap, "picture" + i, ""));
                        }
                    }
                }

                if (Integer.parseInt(mChatData.get(i).getMessageType()) == (MessageFactory.audio)
                    /*&& mChatData.get(i).getaudiotype() == MessageFactory.AUDIO_FROM_RECORD*/) {
                    MessageItemChat msgItem = mChatData.get(i);
                    if (msgItem.getAudioPath() != null) {
                        String path = msgItem.getAudioPath();
                        File file = new File(path);
                        if (file.exists()) {
                            imageItems.add(new ImageItem(audioBitmap, "picture" + i, ""));
                        }
                    } else if (msgItem.getChatFileLocalPath() != null) {
                        String path = msgItem.getChatFileLocalPath();
                        File file = new File(path);
                        if (file.exists()) {
                            imageItems.add(new ImageItem(audioBitmap, "picture" + i, ""));
                        }
                    }
                } else if (Integer.parseInt(mChatData.get(i).getMessageType()) == (MessageFactory.video)) {
                    MessageItemChat msgItem = mChatData.get(i);
                    if (msgItem.getImagePath() != null) {
                        String path = msgItem.getVideoPath();
                        File file = new File(path);
                        if (file.exists()) {
                            MediaMetadataRetriever mdr = new MediaMetadataRetriever();
                            mdr.setDataSource(path);
                            String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            String setduration = getTimeString(Long.parseLong(duration));
                            Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
                            imageItems.add(new ImageItem(thumbBmp, "video" + i, setduration));
                        }
                    } else if (msgItem.getChatFileLocalPath() != null) {
                        String path = msgItem.getChatFileLocalPath();
                        File file = new File(path);
                        if (file.exists()) {
                            MediaMetadataRetriever mdr = new MediaMetadataRetriever();
                            mdr.setDataSource(path);
                            String duration = mdr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                            String setduration = getTimeString(Long.parseLong(duration));
                            Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
                            imageItems.add(new ImageItem(thumbBmp, "video" + i, setduration));
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageItems;
    }

    /**
     * get TimeString
     *
     * @param millis input value(millis)
     * @return value
     */
    private String getTimeString(long millis) {
        StringBuffer buf = new StringBuffer();

        int hours = (int) (millis / (1000 * 60 * 60));
        int minutes = (int) ((millis % (1000 * 60 * 60)) / (1000 * 60));
        int seconds = (int) (((millis % (1000 * 60 * 60)) % (1000 * 60)) / 1000);

        buf

                .append(String.format("%02d", minutes))
                .append(":")
                .append(String.format("%02d", seconds));

        return buf.toString();
    }

}
