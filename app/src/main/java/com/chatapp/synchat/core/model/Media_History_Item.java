package com.chatapp.synchat.core.model;

/**
 */
public class Media_History_Item {


    private String id, ts, imagepath, VideoPath, MessageType, Thumbnailpath;


    private boolean isSelf;
    private int downloadstatus;


    public String getMessageId() {
        return id;
    }

    public void setMessageId(String id) {
        this.id = id;
    }


    public String getTS() {
        return ts;
    }

    public void setTS(String ts) {
        this.ts = ts;
    }


    public boolean isSelf() {
        return isSelf;
    }

    public void setIsSelf(boolean isSelf) {
        this.isSelf = isSelf;
    }


    public void setVideoPath(String VideoPath) {
        this.VideoPath = VideoPath;
    }

    public String getVideoPath() {
        return VideoPath;
    }


    public String getMessageType() {
        return MessageType;
    }

    public void setMessageType(String MessageType) {
        this.MessageType = MessageType;
    }


    public void setImagePath(String imagepath) {
        this.imagepath = imagepath;
    }

    public String getImagePath() {
        return imagepath;
    }


    public void setThumbnailPath(String Thumbnailpath) {
        this.Thumbnailpath = Thumbnailpath;
    }

    public String getThumbnailPath() {
        return Thumbnailpath;
    }


    public void setDownloadstatus(int downloadstatus) {
        this.downloadstatus = downloadstatus;
    }

    public int getDownloadstatus() {
        return downloadstatus;
    }


}
