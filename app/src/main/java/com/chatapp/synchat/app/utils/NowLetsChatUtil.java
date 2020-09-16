//package com.chatapp.android.app.utils;
//
//import android.app.Notification;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.net.Uri;
//import android.support.v4.app.NotificationCompat;
//import android.util.Log;
//
//import com.chatapp.android.R;
//import com.chatapp.android.app.model.CreatePost_Model;
//
//import com.chatapp.android.core.CoreController;
//import com.chatapp.android.core.SessionManager;
//import com.chatapp.android.core.socket.MessageService;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.List;
//
//import static com.chatapp.android.core.socket.MessageService.Post_Received;
//
//
///**
// * created by  Adhash Team on 5/16/2018.
// */
//
//public class NowLetsChatUtil {
//public static NowLetsChatUtil instance=new NowLetsChatUtil();
//public  static boolean isCommentActivityVisible=false;
//private NowLetsChatUtil(){
//
//}
//
//public static NowLetsChatUtil getInstance(){
//    return instance;
//}
//    public void CheckUploadDB(Context context) {
//     //   CelebrityUploadUtil.getInstance().NewcheckAndUploadImage(context);
//    }
//
//
////------------------------------Comments Uploading Status Checking-------------------------------------
//
//
//    public void CheckCommentUpload(Context context) {
//      //  CelebrityUploadUtil.getInstance().CheckandUpdateComments(context);
//    }
//
//    public void updateCommentStatus(Context context,String doc_id){
//        CoreController.getCelebrity_DB(context).CommentUploadStatusUpdate(doc_id, Celebrity_DB.UPLOAD_SUCCESS);
//    }
//
//
//
//    public void New_Post_received(String data,Context mcontext) {
//        /*try {
//            JSONObject objects = new JSONObject(data);
//            try {
//                Post_Received = objects.getString("from");
//
//                String Following_Ids = SessionManager.getInstance(mcontext).getFollowingIds();
//
//                if (!Following_Ids.isEmpty()) {
//                    if (Following_Ids.contains(Post_Received)) {
//                        if (Activity_NewsFeed.Activity_NewsFeed == null) {
//                            NotificationManager mNotificationManager = (NotificationManager) mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
//                            PendingIntent contentIntent = null;
//                            Intent intent = new Intent(mcontext, Activity_Celebrity_Post.class);
//                            intent.putExtra("Page_From", "MessageService");
//                            contentIntent = PendingIntent.getActivity(mcontext, 0, intent, 0);
//                            NotificationCompat.Builder mBuilder =
//                                    new NotificationCompat.Builder(mcontext)
//                                            .setSmallIcon(R.drawable.notificationlogo)
//                                            .setContentTitle("New Connect Post")
//                                            .setStyle(new NotificationCompat.BigTextStyle()
//                                                    .bigText("received"))
//                                            .setContentText("received").setAutoCancel(true)
//                                            .setSound(Uri.parse("android.resource://com.nowletschat.android/" + R.raw.notifysnd));
//
//
//                            mBuilder.setPriority(Notification.PRIORITY_HIGH);
//                            mBuilder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
//                            mBuilder.setContentIntent(contentIntent);
//                            mNotificationManager.notify(1, mBuilder.build());
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }*/
//    }
//
//    public void Celebrity_DB_Clear(String data,Context context) {
// /*       try {
//            JSONObject objects = new JSONObject(data);
//            Log.e("Celebrity DB", objects.toString(1));
//            String status = objects.getString("err");
//            if (status.equalsIgnoreCase("0")) {
//                CoreController.getCelebrity_DB(context).clear_Celebrity_DB();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        if (!Activity_Create_Post.isOpen)
//            Showuploading("Completed",context);
//
//
//
//        Activity_MyCelebrity_Post.Posting_pending_list.clear();
//        Activity_Create_Post.image_array.clear();
//        Activity_Create_Post.str_Website = "";
//        Activity_MyCelebrity_Post.Posting_content = "";*/
//    }
//
//    public static void Showuploading(String type,Context mcontext) {
//      /*  NotificationManager mNotificationManager = (NotificationManager)mcontext.getSystemService(Context.NOTIFICATION_SERVICE);
//        PendingIntent contentIntent = null;
//        Intent intent = new Intent(mcontext, Activity_Celebrity_Post.class);
//        intent.putExtra("Page_From", "MessageService");
//        contentIntent = PendingIntent.getActivity(mcontext, 0, intent, 0);
//        NotificationCompat.Builder mBuilder =
//                new NotificationCompat.Builder(mcontext)
//                        .setSmallIcon(R.drawable.notificationlogo)
//                        .setContentTitle("New Connect post")
//                        .setStyle(new NotificationCompat.BigTextStyle()
//                                .bigText(type))
//                        .setContentText(type).setAutoCancel(true)
//                        .setSound(Uri.parse("android.resource://com.maidac/"+ R.raw.notifysnd));
//        mBuilder.setContentIntent(contentIntent);
//        mBuilder.setPriority(Notification.PRIORITY_HIGH);
//        mNotificationManager.notify(1, mBuilder.build());*/
//    }
//
//
//    public List<CreatePost_Model> getCelebrityPosts(Context context){
//        return CoreController.getCelebrity_DB(context).NewgetMyUnUploadedPost();
//    }
//
//    public void uploadCelebrityPost(Context context) {
//    //    CelebrityUploadUtil.getInstance().NewcheckAndUploadImage(context);
//    }
//
//    public void uploadVideoThumbnail(Context mContext, String path, CreatePost_Model createPost_model) {
//      //  CelebrityUploadUtil.getInstance().uploadVideoThumbNail(mContext,path,createPost_model);
//    }
//}
