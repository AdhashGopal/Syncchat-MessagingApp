package com.chatapp.synchat.core.service;

import android.content.Context;

import com.chatapp.synchat.R;

/**
 * Created by Administrator on 10/6/2016.
 */
public class Constants {

    public static final String SOCKET_URL = "";

    public static final int PHONE_SETTINGS = 105;

    //Adani Live Build

//   public static final String SOCKET_IP_BASE ="http://164.52.196.78";

//       public static final String SOCKET_IP_BASE ="https://achat.adani.com";

    //Adhash private Build
//        public static final String SOCKET_IP_BASE = "http://52.172.4.166:4400";
    public static final String SOCKET_IP_BASE = "http://syncchat.in:4400";
//    public static final String SOCKET_IP_BASE = "http://104.211.181.142:4400";


    //Adhash private Build
//    public static final String SOCKET_IP = "http://52.172.4.166:4400/";
//    public static final String SOCKET_IP = "http://164.52.196.78/";
    public static final String SOCKET_IP = SOCKET_IP_BASE + "/";


    //Adani Live Build
//         public static final String SOCKET_IP = "https://achat.adani.com/";

//     public static final String SOCKET_IP = "https://syncchat.in/";

    // public static final String SOCKET_IP = "http://172.20.10.2:3003/";

    public static final String PRIVATE_SOCKET_IP = "http://172.18.18.200:3003/";

    public static final String SOCKET_URL_ = SOCKET_IP + "user";
    public static final String BASE_URL = SOCKET_IP + "api/";

    public static final boolean IS_ENCRYPTION_ENABLED = false;
    public static final String USER_PROFILE_URL = SOCKET_IP + "uploads/users/";
    public static final String FAQ = "https://chatappweb.com/faq";
    public static final String pricvacy_policy = "https://chatappweb.com/privacy-policy";
    public static final String VERIFY_NUMBER_REQUEST = BASE_URL + "LoginNew";
    public static final String PIN_RESET_COMPLETED = BASE_URL + "UserPinUpdate";

    public static final String CHECK_PIN_RESET_STATUS = BASE_URL + "UserResetCheck";

    public static final String CONTACT_REQUEST_SENT = BASE_URL + "statusChange";
    public static final String SEARCH_CONTACTS = BASE_URL + "searchContacts";
    public static final String SET_FAVOURITE = BASE_URL + "setFavourite";
    public static final String GET_FAVOURITE_STATUS = BASE_URL + "getFavouriteStatus";

    public static final String DUMMY_KEY = "passwordpasswordpasswordpassword";
    public static final String MESSAGES_DELETE = BASE_URL + "deleteUserData";
    public static final String NON_ORGANISER_REGISTER = BASE_URL + "registerNonOrg";
    public static final String REGISTRATION = BASE_URL + "registerUser";
    public static final String LOGIN = BASE_URL + "AppLogin";

    //  public static final String VERIFY_NUMBER_REQUEST = BASE_URL + "Login";
    public static final String VERIFY_SMS_CODE = BASE_URL + "VerifyMsisdn";
    public static final String RESEND_SMS_OTP = BASE_URL + "ResendSms";
    public static final String RESEND_EMAIL_OTP = BASE_URL + "ResendEmailOtp";

    public static final String UPDATE_DATA = BASE_URL + "UpdateData";
    public static final String UPLOAD_IMAGE = BASE_URL + "UploadImage";
    public static final String UPDATE_STATUS = BASE_URL + "UpdateStatus";
    public static final String EVENT_BASEURL = "https://www.theafricantalent.com/";
    public static final String CATEGORY = EVENT_BASEURL + "talent/getCategories";


    //-----------------Event Customization--------------------------
    public static final String LISTDATA = EVENT_BASEURL + "mobile/event-details";
    public static final String VOTE = EVENT_BASEURL + "mobile/receiveVote";
    public static final String SAVE_CELEBRITY_PROFILE = BASE_URL + "UpdateCelebrity";
    public static String callername;

    //-----------Celebrity Url's----------------------------------

    public static String getAppStoreLink(Context context) {
        String link = "Hey, check out " + context.getResources().getString(R.string.app_name)
                + " messenger to connect instantly. \nDownload: https://play.google.com/store/apps/details?id=" + context.getPackageName();

        return link;
    }
}
