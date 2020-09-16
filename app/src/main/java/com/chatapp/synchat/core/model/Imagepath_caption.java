package com.chatapp.synchat.core.model;


import java.io.Serializable;

/**
 * created by  Adhash Team on 4/20/2017.
 */
public class Imagepath_caption implements Serializable {
    /**
     * Getter & setter method for specific event
     */
    public String path = "", caption = "";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }


}
