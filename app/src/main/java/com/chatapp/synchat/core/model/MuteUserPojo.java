package com.chatapp.synchat.core.model;

import java.io.Serializable;

/**
 * created by  Adhash Team on 6/28/2017.
 */
public class MuteUserPojo implements Serializable {
    /**
     * Getter & setter method for specific event
     */
    private String receiverId, chatType, secretType;

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getChatType() {
        return chatType;
    }

    public void setChatType(String chatType) {
        this.chatType = chatType;
    }

    public String getSecretType() {
        return secretType;
    }

    public void setSecretType(String secretType) {
        this.secretType = secretType;
    }
}
