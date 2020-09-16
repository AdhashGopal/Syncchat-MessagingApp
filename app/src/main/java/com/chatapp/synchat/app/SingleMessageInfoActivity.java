package com.chatapp.synchat.app;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.utils.TimeStampUtils;
import com.chatapp.synchat.app.widget.AvnNextLTProRegTextView;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.message.MessageFactory;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.chatapphelperclass.ChatappImageUtils;
import com.chatapp.synchat.core.chatapphelperclass.ChatappUtilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * created by  Adhash Team  on 3/10/2017.
 */
public class SingleMessageInfoActivity extends CoreActivity {

    private String thumbnail, contact, type;

    public ImageView imgshow, play, vidshow, ivPlay;

    private String seen, imagePath, message, myDocName;
    AvnNextLTProRegTextView read, delivered, Textmsg, contactName, docname, time, duration, videocaption, imagecaption;
    ImageView backnavigator, img_map;
    RelativeLayout single, group;
    Session session;
    SeekBar sbDuration;
    Uri uri;
    RelativeLayout head, contact_layout, audio_layout, video_layout, document_layout, map_layout;
    final Context context = this;
    String Audiopath, DocPath, videoPath;
    final static int RQS_OPEN_AUDIO_MP3 = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_info_screen);
        single = (RelativeLayout) findViewById(R.id.single);
        group = (RelativeLayout) findViewById(R.id.group);
        read = (AvnNextLTProRegTextView) findViewById(R.id.readby_data);
        delivered = (AvnNextLTProRegTextView) findViewById(R.id.deliveredto_data);
        Textmsg = (AvnNextLTProRegTextView) findViewById(R.id.txtMsg);
        backnavigator = (ImageView) findViewById(R.id.backnavigator);
        time = (AvnNextLTProRegTextView) findViewById(R.id.ts);
        img_map = (ImageView) findViewById(R.id.img_map);
        document_layout = (RelativeLayout) findViewById(R.id.document_layout);
        map_layout = (RelativeLayout) findViewById(R.id.map_layout);
        imgshow = (ImageView) findViewById(R.id.imgshow);
        head = (RelativeLayout) findViewById(R.id.head);
        duration = (AvnNextLTProRegTextView) findViewById(R.id.duration);
        contactName = (AvnNextLTProRegTextView) findViewById(R.id.contactName);
        docname = (AvnNextLTProRegTextView) findViewById(R.id.docname);
        play = (ImageView) findViewById(R.id.imageView26);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        contact_layout = (RelativeLayout) findViewById(R.id.contact_layout);
        sbDuration = (SeekBar) findViewById(R.id.sbDuration);
        videocaption = (AvnNextLTProRegTextView) findViewById(R.id.videocaption);
        imagecaption = (AvnNextLTProRegTextView) findViewById(R.id.imagecaption);
        audio_layout = (RelativeLayout) findViewById(R.id.audio_layout);
        video_layout = (RelativeLayout) findViewById(R.id.video_layout);
        vidshow = (ImageView) findViewById(R.id.vidshow);

        getSupportActionBar().hide();
        session = new Session(SingleMessageInfoActivity.this);
        single.setVisibility(View.VISIBLE);
        group.setVisibility(View.GONE);
        initData();
    }

    private void initData() {
        Intent i = getIntent();
        MessageItemChat myItems = (MessageItemChat) i.getSerializableExtra("selectedData");
        String msgId = myItems.getMessageId();
        String[] splitIds = msgId.split("-");
        String docId = splitIds[0] + "-" + splitIds[1];

        MessageDbController dbController = CoreController.getDBInstance(this);
        MessageItemChat msgItem = dbController.getParticularMessage(msgId);
        if (msgItem != null) {
            DisplayMessageInfo(msgItem);
        } else {
            DisplayMessageInfo(myItems);
        }
        backnavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void DisplayMessageInfo(MessageItemChat myItem) {
        Integer type = Integer.parseInt(myItem.getMessageType());
        if (type.equals(MessageFactory.text)) {
            Textmsg.setVisibility(View.VISIBLE);
            message = myItem.getTextMessage();
            Textmsg.setText(message);
        } else if (type.equals(MessageFactory.contact)) {
            contact_layout.setVisibility(View.VISIBLE);
            contact = myItem.getContactName();
            contactName.setText(contact);
        } else if (type.equals(MessageFactory.document)) {
            document_layout.setVisibility(View.VISIBLE);

            DocPath = myItem.getChatFileLocalPath();
            myDocName = myItem.getTextMessage();
            docname.setText(myDocName);

            document_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String extension = MimeTypeMap.getFileExtensionFromUrl(DocPath);
                    String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);

                    try {
                        File file = new File(DocPath);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(file), mimeType);
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(SingleMessageInfoActivity.this, "No app installed to view this document", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (type.equals(MessageFactory.audio)) {
            audio_layout.setVisibility(View.VISIBLE);
            Audiopath = myItem.getChatFileLocalPath();
            if (myItem.getDuration() != null) {
                duration.setText(myItem.getDuration());
            }

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playaudio(Audiopath);
                }
            });

        } else if (type.equals(MessageFactory.video)) {
            video_layout.setVisibility(View.VISIBLE);
            videoPath = myItem.getChatFileLocalPath();
            if (myItem.getTextMessage() != null) {
                if (!myItem.getTextMessage().equalsIgnoreCase("")) {
                    videocaption.setVisibility(View.VISIBLE);
                    videocaption.setText(myItem.getTextMessage());
                } else {
                    videocaption.setVisibility(View.GONE);
                }
            }
            if (!videoPath.equals("")) {
                uri = Uri.parse(videoPath);
            }
            try {
                vidshow.setImageBitmap(ThumbnailUtils.createVideoThumbnail(videoPath,
                        MediaStore.Images.Thumbnails.MINI_KIND));

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }
            ivPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.setDataAndType(uri, "video/*");
                    startActivity(intent);
                }
            });
        } else if (type.equals(MessageFactory.picture)) {
            imgshow.setVisibility(View.VISIBLE);
            imagePath = myItem.getChatFileLocalPath();
            if (myItem.getTextMessage() != null) {
                if (!myItem.getTextMessage().equalsIgnoreCase("")) {
                    imagecaption.setVisibility(View.VISIBLE);
                    imagecaption.setText(myItem.getTextMessage());
                } else {
                    imagecaption.setVisibility(View.GONE);
                }
            }
            imgshow.setImageBitmap(ChatappImageUtils.decodeBitmapFromFile(imagePath, 220, 150));


            imgshow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImageZoom.class);
//                            intent.putExtra("IsSelf", isSelf);
                    intent.putExtra("image", imagePath);
                    startActivity(intent);
                }
            });
        } else if (type.equals(MessageFactory.location)) {
            map_layout.setVisibility(View.VISIBLE);
            ImageLoader imageLoader = CoreController.getInstance().getImageLoader();
            imageLoader.get(myItem.getWebLinkImgUrl(), new ImageLoader.ImageListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }

                @Override
                public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                    if (response.getBitmap() != null) {
                        img_map.setImageBitmap(response.getBitmap());
                    }
                }

            });
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");
        Date date2 = new Date();
        String current_date = sdf.format(date2);
        current_date = current_date.substring(0, 10);

        if (myItem.getDeliveryTime() != null) {
            String deliveryTime = TimeStampUtils.getServerTimeStamp(SingleMessageInfoActivity.this,
                    Long.parseLong(myItem.getDeliveryTime()));

            long l = Long.parseLong(deliveryTime);
            Date d = new Date(l);
            String date = d.toString();
            String d1 = sdf.format(d);
            String mytime = ChatappUtilities.convert24to12hourformat(date.substring(11, 19));
            d1 = d1.substring(0, 10);
            if (current_date.equals(d1)) {
                delivered.setText(mytime);
            } else {
                String[] separated = d1.substring(0, 10).split("-");
                String d6 = separated[2] + "-" + separated[1] + "-" + separated[0];
                String finaldate = d6 + "" + "," + mytime;
                delivered.setText(finaldate);
            }

        } else {
            delivered.setText("-NA-");
        }

        if (myItem.getReadTime() != null) {
            String readTime = TimeStampUtils.getServerTimeStamp(SingleMessageInfoActivity.this,
                    Long.parseLong(myItem.getReadTime()));


            long l = Long.parseLong(readTime);
            Date d = new Date(l);
            String date = d.toString();
            String d1 = sdf.format(d);
            String mytime = ChatappUtilities.convert24to12hourformat(date.substring(11, 19));
            d1 = d1.substring(0, 10);
            if (current_date.equals(d1)) {
                read.setText(mytime);
            } else {
                String[] separated = d1.substring(0, 10).split("-");
                String d6 = separated[2] + "-" + separated[1] + "-" + separated[0];
                String finaldate = d6 + "" + "," + mytime;
                read.setText(finaldate);
            }

        } else {
            read.setText("-NA-");
        }


        String messageTime = ChatappUtilities.convert24to12hourformat(myItem.getTS());
        //tsNextLine.setText(messageTime);

    }

    public void playaudio(final String path) {

        try {
            Intent myIntent = new Intent(android.content.Intent.ACTION_VIEW);
            File file = new File(path);
            String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(file).toString());
            String mimetype = android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
            myIntent.setDataAndType(Uri.fromFile(file), mimetype);
            startActivity(myIntent);
        } catch (Exception e) {
            // TODO: handle exception
            String data = e.getMessage();
        }

    }


}
