package com.chatapp.android.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.adapter.GroupMessageInfoAdapter;
import com.chatapp.android.app.utils.AppUtils;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.GroupMessageInfoPojo;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.chatapphelperclass.ChatappImageUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * created by  Adhash Team on 3/14/2017.
 */
public class GroupMessageInfoActivity extends CoreActivity {

    private RecyclerView rvMessageseen, rvDelivered;
    private GroupMessageInfoAdapter msgInfoAdapter;
    private GroupMessageInfoAdapter deliveryInfoAdapter;
    private RelativeLayout single, group;
    private ImageView backnavigator;
    public ImageView imgshow, play, vidshow, ivPlay;
    private String imagePath, message, myDocName, contact, DocPath, videoPath, Audiopath;
    private MessageItemChat msgItem;
    private List<GroupMessageInfoPojo> infoList;
    private List<GroupMessageInfoPojo> deliveryList;
    private Uri uri;
    private Getcontactname getcontactname;
    private RelativeLayout contact_layout, audio_layout, video_layout, document_layout, map_layout;
    private AvnNextLTProRegTextView txtMsg, contactName, docname, tvDeliverPending, duration, videocaption, imagecaption;
    private int readPendingCount = 0, deliverPendingCount = 0;
    private static final String TAG = GroupMessageInfoActivity.class.getSimpleName();
    private TextView tvReadByPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_info_screen);
        getcontactname = new Getcontactname(GroupMessageInfoActivity.this);

        tvDeliverPending = (AvnNextLTProRegTextView) findViewById(R.id.tvDeliverPending);
        txtMsg = (AvnNextLTProRegTextView) findViewById(R.id.txtMsg);
        backnavigator = (ImageView) findViewById(R.id.backnavigator);
        single = (RelativeLayout) findViewById(R.id.single);
        group = (RelativeLayout) findViewById(R.id.group);
        document_layout = (RelativeLayout) findViewById(R.id.document_layout);
        map_layout = (RelativeLayout) findViewById(R.id.map_layout);
        imgshow = (ImageView) findViewById(R.id.imgshow);
        duration = (AvnNextLTProRegTextView) findViewById(R.id.duration);
        contactName = (AvnNextLTProRegTextView) findViewById(R.id.contactName);
        docname = (AvnNextLTProRegTextView) findViewById(R.id.docname);
        play = (ImageView) findViewById(R.id.imageView26);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        contact_layout = (RelativeLayout) findViewById(R.id.contact_layout);
        videocaption = (AvnNextLTProRegTextView) findViewById(R.id.videocaption);
        imagecaption = (AvnNextLTProRegTextView) findViewById(R.id.imagecaption);
        audio_layout = (RelativeLayout) findViewById(R.id.audio_layout);
        video_layout = (RelativeLayout) findViewById(R.id.video_layout);
        vidshow = (ImageView) findViewById(R.id.vidshow);
        tvReadByPending = (TextView) findViewById(R.id.tvReadPending);
        rvMessageseen = (RecyclerView) findViewById(R.id.rvMessageseen);
        rvDelivered = (RecyclerView) findViewById(R.id.rvDelivered);
        // deliveredlist = (RecyclerView) findViewById(R.id.deliveredlist);

        backnavigator = (ImageView) findViewById(R.id.backnavigator);
        backnavigator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        single.setVisibility(View.GONE);
        group.setVisibility(View.VISIBLE);
        initData();
    }

    private void initData() {

        msgItem = (MessageItemChat) getIntent().getSerializableExtra("selectedData");
        String msgId = msgItem.getMessageId();
        String[] splitIds = msgId.split("-");
        String docId = splitIds[0] + "-" + splitIds[1] + "-g";
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);

        MessageDbController dbController = CoreController.getDBInstance(this);
        MessageItemChat dbItem = dbController.getParticularMessage(msgId);
        if (dbItem != null) {
            msgItem = dbItem;
        }
        String infoData = msgItem.getGroupMsgDeliverStatus();
        String currentUserId = SessionManager.getInstance(GroupMessageInfoActivity.this).getCurrentUserID();

        try {
            JSONObject object = new JSONObject(infoData);
            JSONArray arrMembers = object.getJSONArray("GroupMessageStatus");
            infoList = new ArrayList<>();
            deliveryList = new ArrayList<>();

            for (int i = 0; i < arrMembers.length(); i++) {
                JSONObject userObj = arrMembers.getJSONObject(i);
                String receiverId = userObj.getString("UserId");

                if (!receiverId.equals(currentUserId)) {
                    String deliverStatus = userObj.getString("DeliverStatus");
                    String deliverTime = userObj.getString("DeliverTime");
                    String readTime = userObj.getString("ReadTime");
                    if (!deliverStatus.equals(MessageFactory.DELIVERY_STATUS_DELIVERED) &&
                            !deliverStatus.equals(MessageFactory.DELIVERY_STATUS_READ)) {
                        deliverPendingCount += 1;
                        readPendingCount += 1;

                    } else if (!deliverStatus.equals(MessageFactory.DELIVERY_STATUS_READ)) {
                        readPendingCount += 1;
                    }

                    // Setting name and mobile number of receivers
                    ChatappContactModel contact = contactDB_sqlite.getUserOpponenetDetails(receiverId);
                    if (!deliverTime.equalsIgnoreCase("")) {
                        if (contact != null) {
                            GroupMessageInfoPojo msgInfoPojo = new GroupMessageInfoPojo();
                            msgInfoPojo.setReceiverId(receiverId);

                            String receiverName = null;
                            receiverName = getcontactname.getSendername(receiverId, contact.getMsisdn());

                            if (receiverName != null) {
                                msgInfoPojo.setReceiverMsisdn(receiverName);
                                msgInfoPojo.setReceiverName("");
                            } else {
                                msgInfoPojo.setReceiverMsisdn(contact.getMsisdn());
                                msgInfoPojo.setReceiverName(contact.getFirstName());
                            }

                            msgInfoPojo.setDeliverTS(deliverTime);
                            msgInfoPojo.setReadTS(readTime);
                            if (!readTime.equalsIgnoreCase(""))
                                infoList.add(msgInfoPojo);
                            else
                                deliveryList.add(msgInfoPojo);
                        }
                    }
                }
            }

            displayGroupMessageInfo();
        } catch (Exception e) {
            Log.e(TAG, "initData: ", e);
        }
    }

    private void displayGroupMessageInfo() {

        // myadapter = new GroupMessageInfoAdapter(this,selectedItem);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMessageseen.setLayoutManager(mLayoutManager);
        rvDelivered.setLayoutManager(AppUtils.getDefaultLayoutManger(this));

        msgInfoAdapter = new GroupMessageInfoAdapter(GroupMessageInfoActivity.this, infoList, readPendingCount, false);
        deliveryInfoAdapter = new GroupMessageInfoAdapter(GroupMessageInfoActivity.this, deliveryList, readPendingCount, true);
        rvMessageseen.setAdapter(msgInfoAdapter);
        rvDelivered.setAdapter(deliveryInfoAdapter);
        if (readPendingCount > 0) {
            tvReadByPending.setText(readPendingCount + " remaining");
        } else
            tvReadByPending.setVisibility(View.GONE);
        if (deliveryList.size() > 0 || deliverPendingCount > 0) {
            findViewById(R.id.delivery_info_container).setVisibility(View.VISIBLE);


            if (deliverPendingCount > 0) {
                String pendingText = deliverPendingCount + " remaining";
                tvDeliverPending.setText(pendingText);
            }
            else {
                tvDeliverPending.setVisibility(View.GONE);
            }

        } else {
            findViewById(R.id.delivery_info_container).setVisibility(View.GONE);
        }

        Integer type = Integer.parseInt(msgItem.getMessageType());
        if (type.equals(MessageFactory.text)) {
            txtMsg.setVisibility(View.VISIBLE);
            message = msgItem.getTextMessage();
            txtMsg.setText(message);
        } else if (type.equals(MessageFactory.contact)) {
            contact_layout.setVisibility(View.VISIBLE);
            contact = msgItem.getContactName();
            contactName.setText(contact);
        } else if (type.equals(MessageFactory.document)) {
            document_layout.setVisibility(View.VISIBLE);

            DocPath = msgItem.getChatFileLocalPath();
            myDocName = msgItem.getTextMessage();
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
                        Toast.makeText(GroupMessageInfoActivity.this, "No app installed to view this document", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else if (type.equals(MessageFactory.audio)) {
            audio_layout.setVisibility(View.VISIBLE);
            Audiopath = msgItem.getChatFileLocalPath();
            if (msgItem.getDuration() != null) {
                duration.setText(msgItem.getDuration());
            }
            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    playaudio(Audiopath);
                }
            });

        } else if (type.equals(MessageFactory.video)) {
            video_layout.setVisibility(View.VISIBLE);
            videoPath = msgItem.getChatFileLocalPath();
            if (msgItem.getTextMessage() != null) {
                if (!msgItem.getTextMessage().equalsIgnoreCase("")) {
                    videocaption.setVisibility(View.VISIBLE);
                    videocaption.setText(msgItem.getTextMessage());
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

            imagePath = msgItem.getChatFileLocalPath();
            imgshow.setImageBitmap(ChatappImageUtils.decodeBitmapFromFile(imagePath, 220, 150));


            if (msgItem.getTextMessage() != null) {
                if (!msgItem.getTextMessage().equalsIgnoreCase("")) {
                    imagecaption.setVisibility(View.VISIBLE);
                    imagecaption.setText(msgItem.getTextMessage());
                } else {
                    imagecaption.setVisibility(View.GONE);
                }
            }
            imgshow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GroupMessageInfoActivity.this, ImageZoom.class);
//                            intent.putExtra("IsSelf", isSelf);
                    intent.putExtra("image", imagePath);
                    startActivity(intent);
                }
            });
        }
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
            Log.e(TAG, "playaudio: ", e);
        }

    }


}
