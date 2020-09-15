package com.chatapp.android.app.utils;

import com.chatapp.android.core.message.MessageFactory;

/**
 * created by  Adhash Team on 12/21/2017.
 */
public class ConstantMethods {

    /**
     * Check group or secret chat or not
     * @param docId input value (docId)
     * @return value
     */
    public static String getChatType(String docId) {
        if (docId.contains("-g")) {
            return MessageFactory.CHAT_TYPE_GROUP;
        } else if (docId.contains("-secret")) {
            return MessageFactory.CHAT_TYPE_SECRET;
        } else {
            return MessageFactory.CHAT_TYPE_SINGLE;
        }
    }

}
