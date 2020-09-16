package com.chatapp.synchat.core.model;

/**
 * created by  Adhash Team on 9/13/2017.
 */
public class MuteStatusPojo {
    /**
     * Getter & setter method for specific event
     */
    private long ts, expireTs;
    private String duration, notifyStatus, muteStatus;

    public long getTs() {
        return ts;
    }

    public void setTs(long ts) {
        this.ts = ts;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getNotifyStatus() {
        return notifyStatus;
    }

    public void setNotifyStatus(String notifyStatus) {
        this.notifyStatus = notifyStatus;
    }

    public String getMuteStatus() {
        return muteStatus;
    }

    public void setMuteStatus(String muteStatus) {
        this.muteStatus = muteStatus;
    }

    public long getExpireTs() {
        return expireTs;
    }

    public void setExpireTs(long expireTs) {
        this.expireTs = expireTs;
    }
}
