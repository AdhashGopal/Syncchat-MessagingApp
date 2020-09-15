package com.chatapp.android.app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chatapp.android.R;
import com.chatapp.android.app.adapter.ForwardContactAdapter;
import com.chatapp.android.app.adapter.RItemAdapter;
import com.chatapp.android.app.utils.Getcontactname;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.database.MessageDbController;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.message.PictureMessage;
import com.chatapp.android.core.model.ChatappContactModel;
import com.chatapp.android.core.model.MessageItemChat;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.model.SendMessageEvent;
import com.chatapp.android.core.socket.SocketManager;
import com.chatapp.android.core.uploadtoserver.FileUploadDownloadManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class Forward_avater extends CoreActivity {

    RecyclerView lvContacts;
    ForwardContactAdapter adapter;
    InputMethodManager inputMethodManager;
    ProgressDialog dialog;
    AvnNextLTProRegTextView resevernameforward;
    //    private List<ChatappSocketModel> dataList;
    ImageView sendmessage;
    Uri imagefromvendor;
    RelativeLayout Sendlayout;
    String mCurrentUserId, textMsgFromVendor;
    List<MessageItemChat> aSelectedMessageInfo;
    Uri uri;
    private SessionManager sessionManager;
    private List<ChatappContactModel> selectedContactsList;
    private FileUploadDownloadManager uploadDownloadManager;
    private SearchView searchView;
    private boolean forwardFromChatapp;
    private String contact;
    private ArrayList<ChatappContactModel> ChatappEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forward_contact);

        sendmessage = (ImageView) findViewById(R.id.overlapImage);
        resevernameforward = (AvnNextLTProRegTextView) findViewById(R.id.chat_text_view);
        uploadDownloadManager = new FileUploadDownloadManager(Forward_avater.this);
        mCurrentUserId = SessionManager.getInstance(this).getCurrentUserID();
        setTitle("Forward to..");
        lvContacts = (RecyclerView) findViewById(R.id.listContacts);
        LinearLayoutManager mediaManager = new LinearLayoutManager(Forward_avater.this, LinearLayoutManager.VERTICAL, false);
        lvContacts.setLayoutManager(mediaManager);

        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(this);
        ChatappEntries = contactDB_sqlite.getSavedChatappContacts();

        if (ChatappEntries == null) {
            syncContacts();
        } else {
            Collections.sort(ChatappEntries, Getcontactname.nameAscComparator);
            adapter = new ForwardContactAdapter(Forward_avater.this, ChatappEntries);
            lvContacts.setAdapter(adapter);
        }

        selectedContactsList = new ArrayList<>();

        lvContacts.addOnItemTouchListener(new RItemAdapter(this, lvContacts, new RItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ChatappContactModel userData = ChatappEntries.get(position);
                userData.setSelected(!userData.isSelected());

//        if(!userData.getNumberInDevice().startsWith("+")) {
//            String formattedNumber = PhoneNumberUtils.formatNumber("+" + userData.getNumberInDevice(), "");
//            Log.d("formattedNumber", formattedNumber);
//        } else {
//            String formattedNumber = PhoneNumberUtils.formatNumber(userData.getNumberInDevice(), "");
//            Log.d("formattedNumber", formattedNumber);
//        }

                if (userData.isSelected()) {
                    selectedContactsList.add(userData);
                } else {
                    if (selectedContactsList.contains(userData)) {
                        selectedContactsList.remove(userData);
                    }
                }

                ChatappEntries.set(position, userData);
                adapter.notifyDataSetChanged();

                if (selectedContactsList.size() == 0) {
                    Sendlayout.setVisibility(View.GONE);
                    sendmessage.setVisibility(View.GONE);
                } else {

                    Sendlayout.setVisibility(View.VISIBLE);
                    sendmessage.setVisibility(View.VISIBLE);

                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.bottom_up);
                    Sendlayout.setAnimation(animation);

                    StringBuilder sb = new StringBuilder();
                    int nameIndex = 0;
                    for (ChatappContactModel contact : selectedContactsList) {
                        sb.append(contact.getFirstName());
                        nameIndex++;
                        if (selectedContactsList.size() > nameIndex) {
                            sb.append(",");
                        }
                    }

                    resevernameforward.setText(sb);

                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        }));

        final Intent intent = getIntent();

        String shareAction = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(shareAction) && type != null) {
            if ("text/plain".equals(type)) {
                textMsgFromVendor = intent.getExtras().getString(Intent.EXTRA_TEXT, "");
                uri = (Uri) intent.getExtras().get(Intent.EXTRA_STREAM);
            }
        }

        Sendlayout = (RelativeLayout) findViewById(R.id.sendlayout);

        forwardFromChatapp = getIntent().getBooleanExtra("FromChatapp", false);

        if (getIntent() != null) {
            aSelectedMessageInfo = (List<MessageItemChat>) intent.getSerializableExtra("MsgItemList");
        }
        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MessageDbController db = CoreController.getDBInstance(Forward_avater.this);

                String path = getFilePath(uri);
                if (path != null) {
                    File imageFile = new File(path);
                    //  String path = uri.getPath();// "file:///mnt/sdcard/FileName.mp3"

                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;
                    BitmapFactory.decodeFile(imageFile.toString(), options);
                    int imageHeight = options.outHeight;
                    int imageWidth = options.outWidth;

                    for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                        ChatappContactModel userData = selectedContactsList.get(contactIndex);

                        PictureMessage message = (PictureMessage) MessageFactory.getMessage(MessageFactory.picture, Forward_avater.this);
                        message.getMessageObject(userData.get_id(), imageFile.toString(), false);

                        MessageItemChat item = message.createMessageItem(true, "", imageFile.toString(), MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                userData.get_id(), userData.getFirstName(), imageWidth, imageHeight, "", "");

                        String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(imageFile.toString());
                        String imgName = item.getMessageId() + fileExtension;
                        String docId = mCurrentUserId + "-" + userData.get_id();

                        JSONObject uploadObj = (JSONObject) message.createImageUploadObject(item.getMessageId(), docId,
                                imgName, imageFile.toString(), userData.getFirstName(), "", MessageFactory.CHAT_TYPE_SINGLE, false);
                        uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);

                        item.setSenderMsisdn(userData.getNumberInDevice());
                        item.setSenderName(userData.getFirstName());

                        db.updateChatMessage(item, MessageFactory.CHAT_TYPE_SINGLE);
                    }

                    if (selectedContactsList.size() == 1) {
                        Intent intent = new Intent(Forward_avater.this, ChatViewActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                        ChatappContactModel userData = selectedContactsList.get(0);
                        intent.putExtra("receiverId", userData.get_id());
                        intent.putExtra("documentId", userData.get_id());
                        intent.putExtra("receiverName", userData.getFirstName());
                        intent.putExtra("Username", userData.getFirstName());
                        intent.putExtra("Image", userData.getAvatarImageUrl());
                        intent.putExtra("type", 0);
                        intent.putExtra("msisdn", userData.getNumberInDevice());
                        startActivity(intent);

                    }

                    finish();
                } else {
                    Toast.makeText(Forward_avater.this, "Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        /*sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int msgIndex = 0; msgIndex < aSelectedMessageInfo.size(); msgIndex++) {

                    MessageItemChat msgItem = aSelectedMessageInfo.get(msgIndex);

                    switch (msgItem.getMessageType()) {

                        case (MessageFactory.text + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappSocketModel userData = selectedContactsList.get(contactIndex);

                                SendMessageEvent messageEvent = new SendMessageEvent();
                                TextMessage message = (TextMessage) MessageFactory.getMessage(MessageFactory.text, Forward_avater.this);
                                JSONObject msgObj;
                                msgObj = (JSONObject) message.getMessageObject(userData.get_id(), msgItem.getTextMessage());
                                messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
                                messageEvent.setMessageObject(msgObj);
                                MessageItemChat item = message.createMessageItem(true, msgItem.getTextMessage(), MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName());
                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                db.updateChatMessage(message.getId(), item);

                                EventBus.getDefault().post(messageEvent);
                            }

                            break;

                        case (MessageFactory.contact + ""):
                            //   ArrayList<String> selected_data = getIntent().getStringArrayListExtra("message");
                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappSocketModel userData = selectedContactsList.get(contactIndex);

                                String contactName = msgItem.getContactName();
                                String contactNumber = msgItem.getContactNumber();
                                String contactChatappId = msgItem.getContactChatappId();

                                SendMessageEvent messageEvent = new SendMessageEvent();
                                ContactMessage message = (ContactMessage) MessageFactory.getMessage(MessageFactory.contact, Forward_avater.this);
                                JSONObject msgObj;
                                msgObj = (JSONObject) message.getMessageObject(userData.get_id(), "", contactChatappId, contactName, contactNumber);
                                messageEvent.setEventName(SocketManager.EVENT_MESSAGE);
                                messageEvent.setMessageObject(msgObj);
                                MessageItemChat item = message.createMessageItem(true, "", MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName(), contactName, contactNumber, contactChatappId);
                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                db.updateChatMessage(message.getId(), item);

                                EventBus.getDefault().post(messageEvent);
                            }
                            break;

                        case (MessageFactory.audio + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappSocketModel userData = selectedContactsList.get(contactIndex);

                                String filePath = msgItem.getChatFileLocalPath();
                                String duration = msgItem.getDuration();

                                AudioMessage message = (AudioMessage) MessageFactory.getMessage(MessageFactory.audio, Forward_avater.this);
                                message.getMessageObject(userData.get_id(), filePath);
                                MessageItemChat item = message.createMessageItem(true, filePath, duration, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName());

                                String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(filePath);
                                String audioName = item.getMessageId() + fileExtension;

                                String docId = mCurrentUserId + "-" + userData.get_id();
                                JSONObject uploadObj = (JSONObject) message.createAudioUploadObject(item.getMessageId(), docId,
                                        audioName, filePath, duration, userData.getFirstName());

                                uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                db.updateChatMessage(message.getId(), item);
                            }
                            break;

                        case (MessageFactory.video + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappSocketModel userData = selectedContactsList.get(contactIndex);

                                String videoPath = msgItem.getChatFileLocalPath();

                                VideoMessage message = (VideoMessage) MessageFactory.getMessage(MessageFactory.video, Forward_avater.this);
                                message.getMessageObject(userData.get_id(), videoPath);
                                MessageItemChat item = message.createMessageItem(true, videoPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName());


                                Bitmap thumbBmp = ThumbnailUtils.createVideoThumbnail(videoPath, MediaStore.Video.Thumbnails.MICRO_KIND);
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                thumbBmp.compress(Bitmap.CompressFormat.PNG, 10, out);
                                byte[] thumbArray = out.toByteArray();
                                String thumbData = Base64.encodeToString(thumbArray, Base64.DEFAULT);
                                if (thumbData != null) {
                                    item.setThumbnailData(thumbData);
                                }

                                String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(videoPath);
                                String videoName = item.getMessageId() + fileExtension;
                                String docId = mCurrentUserId + "-" + userData.get_id();
                                JSONObject uploadObj = (JSONObject) message.createVideoUploadObject(item.getMessageId(), docId,
                                        videoName, videoPath, userData.getFirstName());
                                uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);
                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                db.updateChatMessage(message.getId(), item);

                            }
                            break;

                        case (MessageFactory.picture + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappSocketModel userData = selectedContactsList.get(contactIndex);

                                String imgPath = msgItem.getChatFileLocalPath();
                                PictureMessage message = (PictureMessage) MessageFactory.getMessage(MessageFactory.picture, Forward_avater.this);
                                message.getMessageObject(userData.get_id(), imgPath);

                                BitmapFactory.Options options = new BitmapFactory.Options();
                                options.inJustDecodeBounds = true;
                                BitmapFactory.decodeFile(imgPath, options);
                                int imageHeight = options.outHeight;
                                int imageWidth = options.outWidth;

                                MessageItemChat item = message.createMessageItem(true, "", imgPath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName(), imageWidth, imageHeight);

                                String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(imgPath);
                                String imgName = item.getMessageId() + fileExtension;
                                String docId = mCurrentUserId + "-" + userData.get_id();

                                JSONObject uploadObj = (JSONObject) message.createImageUploadObject(item.getMessageId(), docId,
                                        imgName, imgPath, userData.getFirstName());
                                uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);

                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                db.updateChatMessage(message.getId(), item);
                            }
                            break;

                        case (MessageFactory.document + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappSocketModel userData = selectedContactsList.get(contactIndex);

                                String filePath = msgItem.getChatFileLocalPath();

                                DocumentMessage message = (DocumentMessage) MessageFactory.getMessage(MessageFactory.document, Forward_avater.this);
                                message.getMessageObject(userData.get_id(), filePath);
                                MessageItemChat item = message.createMessageItem(true, filePath, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName());

                                String fileExtension = FileUploadDownloadManager.getFileExtnFromPath(filePath);
                                String docName = item.getMessageId() + fileExtension;
                                String docId = mCurrentUserId + "-" + userData.get_id();

                                JSONObject uploadObj = (JSONObject) message.createDocUploadObject(item.getMessageId(), docId,
                                        docName, filePath, userData.getFirstName());
                                uploadDownloadManager.uploadFile(EventBus.getDefault(), uploadObj);

                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                db.updateChatMessage(message.getId(), item);

                            }
                            break;

                        case (MessageFactory.web_link + ""):

                            for (int contactIndex = 0; contactIndex < selectedContactsList.size(); contactIndex++) {
                                ChatappSocketModel userData = selectedContactsList.get(contactIndex);

                                String data = msgItem.getTextMessage();
                                String webLink = msgItem.getWebLink();
                                String webLinkTitle = msgItem.getWebLinkTitle();
                                String webLinkDesc = msgItem.getWebLinkDesc();
                                String webLinkImgUrl = msgItem.getWebLinkImgUrl();
                                String webLinkThumb = msgItem.getWebLinkImgThumb();

                                SendMessageEvent messageEvent = new SendMessageEvent();
                                WebLinkMessage message = (WebLinkMessage) MessageFactory.getMessage(MessageFactory.web_link, Forward_avater.this);
                                JSONObject msgObj = (JSONObject) message.getMessageObject(userData.get_id(), data);
                                messageEvent.setMessageObject(msgObj);

                                MessageItemChat item = message.createMessageItem(true, data, MessageFactory.DELIVERY_STATUS_NOT_SENT,
                                        userData.get_id(), userData.getFirstName(), webLink, webLinkTitle, webLinkDesc, webLinkImgUrl, webLinkThumb);
                                msgObj = (JSONObject) message.getWebLinkObject(msgObj, webLink, webLinkTitle, webLinkDesc, webLinkImgUrl, webLinkThumb);
                                messageEvent.setMessageObject(msgObj);

                                item.setSenderMsisdn(userData.getNumberInDevice());
                                item.setSenderName(userData.getFirstName());
                                db.updateChatMessage(message.getId(), item);
                            }

                            break;
                    }

                }

                if (selectedContactsList.size() == 1) {
                    Intent intent = new Intent(Forward_avater.this, ChatViewActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    ChatappSocketModel userData = selectedContactsList.get(0);
                    intent.putExtra("receiverId", userData.get_id());
                    intent.putExtra("documentId", userData.get_id());
                    intent.putExtra("receiverName", userData.getFirstName());
                    intent.putExtra("Username", userData.getFirstName());
                    intent.putExtra("Image", userData.getAvatarImageUrl());
                    intent.putExtra("type", 0);
                    intent.putExtra("msisdn", userData.getNumberInDevice());
                    startActivity(intent);

                }

                finish();
            }

        });*/
        /* Variables for serch */
        sessionManager = SessionManager.getInstance(this);
    }

    /*  @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

          ChatappSocketModel userData = dataList.get(position);
          userData.setSelected(!userData.isSelected());

  //        if(!userData.getNumberInDevice().startsWith("+")) {
  //            String formattedNumber = PhoneNumberUtils.formatNumber("+" + userData.getNumberInDevice(), "");
  //            Log.d("formattedNumber", formattedNumber);
  //        } else {
  //            String formattedNumber = PhoneNumberUtils.formatNumber(userData.getNumberInDevice(), "");
  //            Log.d("formattedNumber", formattedNumber);
  //        }

          if (userData.isSelected()) {
              selectedContactsList.add(userData);
          } else {
              if (selectedContactsList.contains(userData)) {
                  selectedContactsList.remove(userData);
              }
          }

          dataList.set(position, userData);
          adapter.notifyDataSetChanged();

          if (selectedContactsList.size() == 0) {
              Sendlayout.setVisibility(View.GONE);
              sendmessage.setVisibility(View.GONE);
          } else {

              Sendlayout.setVisibility(View.VISIBLE);
              sendmessage.setVisibility(View.VISIBLE);

              Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),
                      R.anim.bottom_up);
              Sendlayout.setAnimation(animation);

              StringBuilder sb = new StringBuilder();
              int nameIndex = 0;
              for (ChatappSocketModel contact : selectedContactsList) {
                  sb.append(contact.getFirstName());
                  nameIndex++;
                  if (selectedContactsList.size() > nameIndex) {
                      sb.append(",");
                  }
              }

              resevernameforward.setText(sb);

          }
      }
  */
    private void syncContacts() {
        contactsFromCursor();
    }

    private void contactsFromCursor() {
        ChatappContactsService.contactEntries = new ArrayList<>();
        showProgressDialog();

        Uri contactsUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI; // The content URI of the phone contacts
        String[] projection = {                                  // The columns to return for each row
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };
        String selection = null;                                 //Selection criteria
        String[] selectionArgs = {};                             //Selection criteria
        String sortOrder = null;                                 //The sort order for the returned rows

        Cursor cursor = getContentResolver().query(contactsUri, projection, selection, selectionArgs, sortOrder);

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            JSONArray arrContacts = new JSONArray();
            do {
                ChatappContactModel d = new ChatappContactModel();
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                d.setFirstName(name);
//                contact += phNumber.trim() + ",";

                try {
                    String phNo = phNumber.replace(" ", "").replace("(", "").replace(")", "").replace("-", "");
                    d.setNumberInDevice(phNo);
                    JSONObject contactObj = new JSONObject();
                    contactObj.put("Phno", phNo);
                    contactObj.put("Name", name);
                    arrContacts.put(contactObj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                ChatappContactsService.contactEntries.add(d);

            } while (cursor.moveToNext());
            contact = arrContacts.toString();
        }
        updateDataToTheServer();
    }

    private void updateDataToTheServer() {
        SendMessageEvent messageEvent = new SendMessageEvent();
        messageEvent.setEventName(SocketManager.EVENT_GET_FAVORITE);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("indexAt", "0");
            jsonObject.put("msisdn", SessionManager.getInstance(this).getPhoneNumberOfCurrentUser());
            jsonObject.put("Contacts", contact);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        messageEvent.setMessageObject(jsonObject);
        EventBus.getDefault().post(messageEvent);
    }

    private void storeContact(ReceviceMessageEvent event) throws JSONException {
        Object[] args = event.getObjectsArray();
        JSONObject data = new JSONObject(args[0].toString());
        JSONArray array = data.getJSONArray("Favorites");

        ChatappEntries = new ArrayList<>();

        for (int contactIndex = 0; contactIndex < ChatappContactsService.contactEntries.size(); contactIndex++) {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = new JSONObject(array.get(i).toString());
                String msisdn = obj.getString("msisdn");
                String id = obj.getString("_id");
                String profilePic = obj.getString("ProfilePic");

                ChatappContactModel entry = ChatappContactsService.contactEntries.get(contactIndex);
                if (msisdn.contains(entry.getNumberInDevice())) {
                    ChatappEntries.add(entry);

                }
            }
        }

        ChatappEntries = new ArrayList<>();
        for (ChatappContactModel contact : ChatappEntries) {
            contact.setSelected(false);
            ChatappEntries.add(contact);
        }

        Collections.sort(ChatappEntries, Getcontactname.nameAscComparator);
        adapter = new ForwardContactAdapter(Forward_avater.this, ChatappEntries);
        lvContacts.setAdapter(adapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (SocketManager.EVENT_GET_CONTACTS.equalsIgnoreCase(event.getEventName())) {
            try {
                storeContact(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (SocketManager.EVENT_GET_CONTACTS.equalsIgnoreCase(event.getEventName())) {
            try {
                storeContact(event);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    private String getFilePath(Uri contentUri) {
        String path = null;
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(contentUri, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            path = cursor.getString(columnIndex);
            cursor.close();

            if (path == null) {
                path = getRealFilePath(contentUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }

    private String getRealFilePath(Uri contentUri) {
        String wholeID = DocumentsContract.getDocumentId(contentUri);
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

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.menu_forward_contact, menu);
        MenuItem searchItem = menu.findItem(R.id.chats_searchIcon1);
        searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setIconifiedByDefault(true);
                searchView.setIconified(true);
                searchView.setQuery("", false);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    if (newText.equals("") && newText.isEmpty()) {
                        searchView.clearFocus();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                adapter.getFilter().filter(newText);
                return false;
            }
        });

        searchView.setIconifiedByDefault(true);
        searchView.setQuery("", false);
        searchView.clearFocus();
        searchView.setIconified(true);

        AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(R.id.search_src_text);
        try {
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchTextView, 0); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
        } catch (Exception e) {
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
