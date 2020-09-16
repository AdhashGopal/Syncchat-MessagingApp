package com.chatapp.synchat.app.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.chatapp.synchat.R;
import com.chatapp.synchat.app.ChatViewActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.ContactDB_Sqlite;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.GroupMembersPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.service.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by  Adhash Team on 3/30/2017.
 */
public class Getcontactname {

    private static final String TAG = Getcontactname.class.getSimpleName();
    public static Comparator<ChatappContactModel> nameAscComparator = new Comparator<ChatappContactModel>() {

        @Override
        public int compare(ChatappContactModel lhs, ChatappContactModel rhs) {
            try {
                if (lhs.getFirstName() != null && rhs.getFirstName() != null) {
                    String name1 = lhs.getFirstName().toUpperCase();
                    String name2 = rhs.getFirstName().toUpperCase();

                    //ascending order
                    return name1.compareTo(name2);
                }
            } catch (Exception e) {
                Log.e(TAG, "compare: ", e);
            }
            return 0;
        }

    };
    public static Comparator<GroupMembersPojo> groupMemberAsc = new Comparator<GroupMembersPojo>() {

        @Override
        public int compare(GroupMembersPojo lhs, GroupMembersPojo rhs) {
            String name1 = lhs.getContactName().toUpperCase();
            String name2 = rhs.getContactName().toUpperCase();

            //ascending order
            return name1.compareTo(name2);
        }

    };
    public static Comparator<String> stringAsc = new Comparator<String>() {

        @Override
        public int compare(String lhs, String rhs) {
            String name1 = lhs.toUpperCase();
            String name2 = rhs.toUpperCase();

            //ascending order
            return name1.compareTo(name2);
        }

    };
    public Session session;
    Context context;
    String uniqueCurrentID, receiverDocumentID;
    ContactDB_Sqlite contactDB_sqlite;

    public Getcontactname(Context context) {
        this.context = context;
        session = new Session(context);
        uniqueCurrentID = SessionManager.getInstance(context).getCurrentUserID();
        contactDB_sqlite = CoreController.getContactSqliteDBintstance(context);
    }

   /* public void configProfilepic(final ImageView image, final String userId, final boolean needTransform,
                                 boolean isListItem, final int errorResId) {

        if (userId == null || userId.equals("")) {
//            Picasso.with(context).load(errorResId).into(image);
            Glide.with(context).load(errorResId).thumbnail(0.1f)
                    .dontAnimate()
                    .dontTransform()
                    .into(image);
        } else {

            if (contactDB_sqlite.getBlockedMineStatus(userId, false).equals(ContactDB_Sqlite.BLOCKED_STATUS)) {
//                Picasso.with(context).load(errorResId).into(image);
                Glide.with(context).load(errorResId).thumbnail(0.1f)
                        .dontAnimate()
                        .dontTransform()
                        .into(image);
            } else {

                String profilePicVisibility = contactDB_sqlite.getProfilePicVisibility(userId);

               *//* if (profilePicVisibility.equals(ContactDB_Sqlite.PRIVACY_STATUS_NOBODY)) {
//                    Picasso.with(context).load(errorResId).into(image);
                    Glide.with(context).load(errorResId).thumbnail(0.1f)
                            .dontAnimate()
                            .dontTransform()
                            .into(image);
                } else {*//*

     *//* boolean canShow = false;
                    if (profilePicVisibility.equals(ContactDB_Sqlite.PRIVACY_STATUS_EVERYONE)) {
                        canShow = true;
                    }

                   else if (profilePicVisibility.equals(ContactDB_Sqlite.PRIVACY_STATUS_NOBODY)) {
                        canShow = false;
                    }

                    else {
                        if (contactDB_sqlite.getMyContactStatus(userId).equals("1")) {
                            canShow = true;
                        }
                    }*//*

//                    if (canShow) {
                        String imageTS = contactDB_sqlite.getDpUpdatedTime(userId);
                        if (imageTS == null || imageTS.isEmpty() || imageTS.equalsIgnoreCase("0"))
                            imageTS = "1";
                        //  final String avatar = Constants.USER_PROFILE_URL + userId + ".jpg?id=" + imageTS;
                        String avatar = "";
                        String android_image = Constants.USER_PROFILE_URL + userId + "?id=" + imageTS;
                        String Ios_image = contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl();


                        if (!Ios_image.equalsIgnoreCase("")) {
                            avatar = Ios_image.replace("./uploads/users/", Constants.USER_PROFILE_URL);
                            avatar = avatar + "?id=" + imageTS;
                            System.out.println("ChangedAvater" + " " + " " + avatar);
                        } else {
                            avatar = Constants.USER_PROFILE_URL + userId + "?id=" + imageTS;
                        }

                        //  final String avatar=Constants.USER_PROFILE_URL +contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl()+ "?id=" + imageTS;
                        Log.d(TAG, "configProfilepic: canshow " + avatar);

*//*                        Picasso picasso = Picasso.with(context);
                        RequestCreator requestCreator = picasso.load(avatar).error(errorResId);*//*
                        if (isListItem) {
                            AppUtils.loadImage(context, avatar, image, 100, errorResId);
                        } else {
*//*                            Glide.with(context).load(avatar)
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .dontAnimate()
                                    .dontTransform()
                                    .error(errorResId)
                                    .into(image);*//*
                              //contacts load image
                            if (errorResId == R.mipmap.contact_off) {
                                //dont use it in RecyclerView
                                AppUtils.loadImageSmooth(context, avatar, image, 100, errorResId);
                            } else {
                                AppUtils.loadImage(context, avatar, image, 0, errorResId);
                            }
                        }
                  *//*  } else {
                        Log.d(TAG, "configProfilepic: not show");
                        Glide.with(context).load(errorResId)
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .dontAnimate()
                                .dontTransform()
                                .error(errorResId)
                                .into(image);
                    *//*}
                //}
//            }
        }

    }*/

    public static boolean isEmailValid(String email) {
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
        }
        return isValid;
    }

    public static boolean isPasswordValid(String password) {
        boolean isValid = false;
        if (password.length() >= 4) {
            isValid = true;
        }
        return isValid;
    }

    public static JSONObject getUserDetailsObject(Context context, String userId) {

        JSONObject eventObj = new JSONObject();
        try {
            eventObj.put("userId", userId);
        } catch (JSONException ex) {
            ex.printStackTrace();
        }
        return eventObj;
    }

    public static String getAppVersion(Context context) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(
                    context.getPackageName(), 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

    }

/*
    public String getAvatarUrl(String userId) {
        String avatar = "";
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(context);

        if (contactDB_sqlite.getBlockedMineStatus(userId, false).equals(ContactDB_Sqlite.UN_BLOCKED_STATUS)) {

            String profilePicVisibility = contactDB_sqlite.getProfilePicVisibility(userId);

//            if (!profilePicVisibility.equals(ContactDB_Sqlite.PRIVACY_STATUS_NOBODY)) {

              */
/*  boolean canShow = false;
                if (profilePicVisibility.equals(ContactDB_Sqlite.PRIVACY_STATUS_EVERYONE)) {
                    canShow = true;
                } else {
                    if (contactDB_sqlite.getMyContactStatus(userId).equals("1")) {
                        canShow = true;
                    }
                }*//*


//                if (canShow) {
            String imageTS = contactDB_sqlite.getDpUpdatedTime(userId);
            if (imageTS == null || imageTS.isEmpty())
                imageTS = "1";

            String android_image = Constants.USER_PROFILE_URL + userId + "?id=" + imageTS;
            String Ios_image = contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl();


            if (!Ios_image.equalsIgnoreCase("")) {
                avatar = Ios_image.replace("./uploads/users/", Constants.USER_PROFILE_URL);
                avatar = avatar + "?id=" + imageTS;
                System.out.println("ChangedAvater" + " " + " " + avatar);
            } else {
                avatar = Constants.USER_PROFILE_URL + userId + "?id=" + imageTS;
            }

            // avatar = Constants.USER_PROFILE_URL + userId + ".jpg?id=" + imageTS;
//                }
//            }
        }
        return avatar;
    }
*/

    public String getAvatarUrl(String userId) {
        String avatar = "";
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(context);

        if (contactDB_sqlite.getBlockedMineStatus(userId, false).equals(ContactDB_Sqlite.UN_BLOCKED_STATUS)) {

            if (contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl().contains(Constants.SOCKET_IP)) {
                avatar = contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl();

            } else {
                avatar = Constants.SOCKET_IP + contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl();
            }

        }
        return avatar;
    }


    public String getSendername(String userid, String msisdn) {
        String sendername = msisdn;

        if (userid != null) {
            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(context);
            long contactRevision = contactDB_sqlite.getOpponenet_UserDetails_savedRevision(userid);
            long syncedRevision = SessionManager.getInstance(context).getContactSavedRevision();

//            if (contactRevision >= syncedRevision) {
            ChatappContactModel contactData = contactDB_sqlite.getUserOpponenetDetails(userid);
            if (contactData != null) {
                String firstName = "";
                if (contactData.getFirstName() != null)
                    firstName = contactData.getFirstName();
                sendername = firstName;
            }
//            }
        }

        return sendername;
    }

    public void userDetails(String userId, ChatappContactModel contactModel) {
        ChatappContactModel contact = new ChatappContactModel();

        contact.set_id(userId);
        contact.setStatus(contactModel.getStatus());
        contact.setAvatarImageUrl(contactModel.getAvatarImageUrl());
        contact.setMsisdn(contactModel.getMsisdn());
        contact.setRequestStatus("3");

        contact.setFirstName(contactModel.getFirstName());

        contactDB_sqlite.updateUserDetails(userId, contact);


    }

    public void configProfilepic(final ImageView image, final String userId, final boolean needTransform,
                                 boolean isListItem, final int errorResId) {

        if (userId == null || userId.equals("")) {
            //TODO tharani map
            Glide.with(context).load(errorResId).thumbnail(0.1f)
                    .dontAnimate()
                    .dontTransform()
                    .into(image);
        } else {
//            String avatar = "";
            if (contactDB_sqlite.getBlockedMineStatus(userId, false).equals(ContactDB_Sqlite.BLOCKED_STATUS)) {
//TODO tharani map
                Glide.with(context).load(errorResId).thumbnail(0.1f)
                        .dontAnimate()
                        .dontTransform()
                        .into(image);
            } else {


                String imageTS = contactDB_sqlite.getDpUpdatedTime(userId);
                if (imageTS == null || imageTS.isEmpty() || imageTS.equalsIgnoreCase("0"))
                    imageTS = "1";
                //  final String avatar = Constants.USER_PROFILE_URL + userId + ".jpg?id=" + imageTS;
                String avatar = "";
                String android_image = Constants.USER_PROFILE_URL + userId + "?id=" + imageTS;
                String Ios_image = contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl();
                if (!Ios_image.contains(Constants.SOCKET_IP)) {
                    avatar = Constants.SOCKET_IP + Ios_image;
                } else {
                    avatar = Ios_image;
                }


              /*  if (!Ios_image.equalsIgnoreCase("")) {
                    avatar = Ios_image.replace("./uploads/users/", Constants.USER_PROFILE_URL);
                    avatar = avatar + "?id=" + imageTS;
                    System.out.println("ChangedAvater" + " " + " " + avatar);
                } else {
                    avatar = Constants.USER_PROFILE_URL + userId + "?id=" + imageTS;
                }*/
                //  final String avatar=Constants.USER_PROFILE_URL +contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl()+ "?id=" + imageTS;
                Log.d(TAG, "configProfilepic: canshow " + avatar);

//                        Picasso picasso = Picasso.with(context);
//                        RequestCreator requestCreator = picasso.load(avatar).error(errorResId);
                if (isListItem) {
//                    AppUtils.loadImage(context, avatar, image, 100, errorResId);
                    //TODO tharani map
                    Glide.with(context).load(avatar).thumbnail(0.1f)
                            .dontAnimate()
                            .error(errorResId)
                            .dontTransform()
                            .into(image);
                } else {
                    //contacts load image
                    if (errorResId == R.mipmap.contact_off) {
                        //dont use it in RecyclerView
                        AppUtils.loadImageSmooth(context, avatar, image, 100, errorResId);
                    } else {
                        AppUtils.loadImage(context, avatar, image, 0, errorResId);
                    }
                }

/*


                if (contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl().contains(Constants.SOCKET_IP)) {
                    avatar = contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl();
                    *//*Glide.with(context).load(contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl()).thumbnail(0.1f)
                            .dontAnimate()
                            .error(errorResId)
                            .dontTransform()
                            .into(image);   *//*
                } else {
                    avatar = Constants.SOCKET_IP + contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl();

                    *//*Glide.with(context).load(Constants.SOCKET_IP+contactDB_sqlite.getUserOpponenetDetails(userId).getAvatarImageUrl()).thumbnail(0.1f)
                            .dontAnimate()
                            .error(errorResId)
                            .dontTransform()
                            .into(image);*//*
                }


                if (isListItem) {
                    AppUtils.loadImage(context, avatar, image, 100, errorResId);
                } else {
                    if (errorResId == R.mipmap.contact_off) {
                        //dont use it in RecyclerView
                        AppUtils.loadImageSmooth(context, avatar, image, 100, errorResId);
                    } else {
                        AppUtils.loadImage(context, avatar, image, 0, errorResId);
                    }
                }*/
            }

        }

    }

    public boolean setProfileStatusText(TextView tvStatus, String userId, String status, boolean isSecretChat) {
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(context);
        String statusVisibility = contactDB_sqlite.getProfileStatusVisibility(userId);

        boolean isStatusDisplayed = true;

       /* switch (statusVisibility) {

            case ContactDB_Sqlite.PRIVACY_STATUS_NOBODY:
                tvStatus.setText("");
                isStatusDisplayed = false;
                break;

            case ContactDB_Sqlite.PRIVACY_STATUS_MY_CONTACTS:
                if (contactDB_sqlite.getMyContactStatus(userId).equals("1")) {
                    tvStatus.setText(status);
                } else {
                    tvStatus.setText("");
                    isStatusDisplayed = false;
                }
                break;

            default:
                tvStatus.setText(status);
                break;
        }*/


        if (contactDB_sqlite.getBlockedMineStatus(userId, isSecretChat).equals("1")) {
            tvStatus.setText("");
            isStatusDisplayed = false;
        } else {
            tvStatus.setText(status);
        }

        return isStatusDisplayed;
    }

    public void navigateToChatviewPageforChatappModel(ChatappContactModel e) {

        Intent intent = new Intent(context, ChatViewActivity.class);
        intent.putExtra("receiverUid", e.getNumberInDevice());
        String firstName = "";
        if (e.getFirstName() != null)
            firstName = e.getFirstName();
        intent.putExtra("receiverName", firstName);
        intent.putExtra("documentId", e.get_id());
        intent.putExtra("Username", firstName);
        intent.putExtra("Image", e.getAvatarImageUrl());
        intent.putExtra("type", 0);
        String msisdn = e.getMsisdn();
        intent.putExtra("msisdn", msisdn);
        context.startActivity(intent);
//        ((Activity) context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void navigateToChatViewpagewithmessageitems(MessageItemChat e, String from) {

        String[] array = e.getMessageId().split("-");
        String msgid = "";
        Intent intent = new Intent(context, ChatViewActivity.class);
        intent.putExtra("receiverUid", e.getNumberInDevice());
        intent.putExtra("receiverName", e.getSenderName());
        if (array[0].equalsIgnoreCase(uniqueCurrentID)) {
            receiverDocumentID = array[1];
        } else if (array[1].equalsIgnoreCase(uniqueCurrentID)) {
            receiverDocumentID = array[0];
        }
        if (e.getMessageId().contains("-g")) {
            msgid = array[3];
        } else {
            msgid = array[2];
        }
        intent.putExtra("documentId", receiverDocumentID);
        intent.putExtra("Username", e.getSenderName());
        intent.putExtra("Image", e.getAvatarImageUrl());
        intent.putExtra("msisdn", e.getSenderMsisdn());
        intent.putExtra("searchingMessage", 1);
        if (from.equalsIgnoreCase("star") || from.equalsIgnoreCase("webLink")) {
            intent.putExtra("msgid", msgid);
        }
        intent.putExtra("type", 0);

        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.fast_enter, R.anim.fast_exit);
//        ((Activity) context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public boolean isContactExists(String userId) {
        long savedRevision = SessionManager.getInstance(context).getContactSavedRevision();
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(context);
        long contactRevision = contactDB_sqlite.getOpponenet_UserDetails_savedRevision(userId);
        return contactRevision >= savedRevision;
    }

    public String getNameSortedUserIds(String userIds) {

        ArrayList<String> savedMembersList = new ArrayList<>();
        ArrayList<String> unsavedMembersList = new ArrayList<>();
        ArrayList<String> allMembersList = new ArrayList<>();
        String mineUserId = null;

        long savedRevision = SessionManager.getInstance(context).getContactSavedRevision();
        String currentUserId = SessionManager.getInstance(context).getCurrentUserID();
        ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(context);

        String[] splitIds = userIds.split(",");
        for (int i = 0; i < splitIds.length; i++) {
            String userId = splitIds[i];
            if (userId.equalsIgnoreCase(currentUserId)) {
                mineUserId = userId;
            } else {
                ChatappContactModel contactModel = contactDB_sqlite.getUserOpponenetDetails(userId);
                long contactRevision = contactDB_sqlite.getOpponenet_UserDetails_savedRevision(userId);
                if (contactModel != null && contactRevision >= savedRevision) {
                    savedMembersList.add(userId);
                } else {
                    unsavedMembersList.add(userId);
                }
            }
        }

        Collections.sort(savedMembersList, Getcontactname.stringAsc);
        Collections.sort(unsavedMembersList, Getcontactname.stringAsc);
        allMembersList.addAll(savedMembersList);
        allMembersList.addAll(unsavedMembersList);

        if (mineUserId != null) {
            allMembersList.add(mineUserId);
        }

        String groupContactNames = "";
        String groupUserIds = "";
        for (int i = 0; i < allMembersList.size(); i++) {
            if (i != 0) {
                groupContactNames = groupContactNames + ", ";
                groupUserIds = groupUserIds + ",";
            }
//            groupContactNames = groupContactNames + allMembersList.get(i).getContactName();
//            groupUserIds = groupUserIds + allMembersList.get(i).getUserId();
        }
        return "";
    }
}






