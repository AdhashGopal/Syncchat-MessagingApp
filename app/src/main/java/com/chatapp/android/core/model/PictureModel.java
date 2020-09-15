package com.chatapp.android.core.model;

import java.io.Serializable;

/**
 */
public class PictureModel implements Serializable {

    /**
     * Getter & setter method for specific event
     */
    private static final long serialVersionUID = 633956855520350887L;
    //{"message":"Verification Code Send to You Number","errNum":"0","code":"1143"}

    private String Message,errNum,Url;
    //String phoneNumber = VerifyPhoneNumber.message;
    //private String message2 = "We will be verifying the phone number:\n"+phoneNumber+"\nIs this OK,or would you like to edit the number?";

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message2) {
        this.Message = Message;
    }

    public String getErrNum() {
        return errNum;
    }

    public void setErrNum(String errNum) {
        this.errNum = errNum;
    }

    public String getUrl() {
        return Url;
    }

    public void setCode(String Url) {
        this.Url = Url;
    }

}

