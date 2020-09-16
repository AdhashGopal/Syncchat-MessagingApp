package com.chatapp.synchat.core.model;

/**
 * created by  Adhash Team on 12/21/2017.
 */
public class ChatLockPojo {
    /**
     * Getter & setter method for specific event
     */
    private String senderId, receiverId, password, status;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
