package com.chatapp.synchat.core.model;

import java.io.Serializable;

/* This model is used for verifying the phone number and verifying the code sent via SMS*/
public class SCLoginModel implements Serializable {

    /**
     * Getter & setter method for specific event
     */
    private static final long serialVersionUID = 633956855520350887L;
    //{"message":"Verification Code Send to You Number","errNum":"0","code":"1143"}

    private String message;
    private String errNum;
    private String code;
    private String Name;
    private String email;
    private String Status;
    private String _id;
    private String loginCount;
    private String token;
    private int reset_pin=0;
    private int type=0;

    public String getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(String loginCount) {
        this.loginCount = loginCount;
    }

    public String getProfilePic() {
        return ProfilePic;
    }

    public void setProfilePic(String profilePic) {
        ProfilePic = profilePic;
    }

    private String ProfilePic;
    //String phoneNumber = VerifyPhoneNumber.message;
    //private String message2 = "We will be verifying the phone number:\n"+phoneNumber+"\nIs this OK,or would you like to edit the number?";

    public String getMessage() {
        return message;
    }

    public void setMessage(String message2) {
        this.message = message;
    }

    public String getErrNum() {
        return errNum;
    }

    public void setErrNum(String errNum) {
        this.errNum = errNum;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLoginType() {
        return type;
    }

    public void setLoginType(int type) {
        this.type = type;
    }


    public int getResetPin() {
        return reset_pin;
    }

    public void setResetPin(int reset_pin) {
        this.reset_pin = reset_pin;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        this.Name = name;
    }

    public String getUserEmail() {
        return email;
    }

    public void setUserEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
