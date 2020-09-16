package com.chatapp.synchat.core.chatapphelperclass;

import java.util.regex.Pattern;

/**
 * created by  Adhash Team on 6/15/2017.
 */
public class ChatappRegularExp {

    /**
     * Encoded Base 64String from string value
     *
     * @param data input value(data)
     * @return response value
     */
    public static Boolean isEncodedBase64String(String data) {
        String base64Exp = "^([A-Za-z0-9+/]{4})*([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)$";
        Pattern base64Pattern = Pattern.compile(base64Exp);
        return base64Pattern.matcher(data).matches();
    }

}
