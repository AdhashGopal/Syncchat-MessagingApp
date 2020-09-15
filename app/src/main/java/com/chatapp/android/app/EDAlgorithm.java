package com.chatapp.android.app;

import android.util.Base64;

public class EDAlgorithm {


//    public String enc(String text)
//    {
//        byte[] data = new byte[0];
//        try {
//            data = text.getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//
//        return Base64.encodeToString(data, Base64.DEFAULT);
//
//    }
//
//
//    public String dec(String base64)
//    {
//        String text="";
//        byte[] data = Base64.decode(base64, Base64.DEFAULT);
//        try {
//             text = new String(data, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return text;
//    }


    public byte[] encAttachment( byte[] data )
    {
        return Base64.encode(data, Base64.DEFAULT);

    }


    public byte[] decAttachment( byte[] data)
    {

        return Base64.decode(data, Base64.DEFAULT);
//        String text="";
//        byte[] data = Base64.decode(base64, Base64.DEFAULT);
//        try {
//            text = new String(data, "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        return text;
    }


}
