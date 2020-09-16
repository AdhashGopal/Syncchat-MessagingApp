package com.chatapp.synchat.core.model;

/**
 * created by  Adhash Team on 3/7/2017.
 */
public class BlockListPojo {
    /**
     * Getter & setter method for specific event
     */
    private String name, ImagePath, Number, Id;
    private boolean isSecretChat;

    public boolean isSecretChat() {
        return isSecretChat;
    }

    public void setIsSecretChat(boolean secretChat) {
        isSecretChat = secretChat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImagePath() {
        return ImagePath;
    }

    public void setImagePath(String ImagePath) {
        this.ImagePath = ImagePath;
    }

    public String getNumber() {
        return Number;
    }

    public void setNumber(String Number) {
        this.Number = Number;
    }


    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }


}
