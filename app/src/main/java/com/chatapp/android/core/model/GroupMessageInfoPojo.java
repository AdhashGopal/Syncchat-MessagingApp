package com.chatapp.android.core.model;

/**
 * created by  Adhash Team on 3/30/2017.
 */
public class GroupMessageInfoPojo {
    /**
     * Getter & setter method for specific event
     */
    private String receiverId, receiverMsisdn, receiverName, deliverTS, readTS;

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverMsisdn() {
        return receiverMsisdn;
    }

    public void setReceiverMsisdn(String receiverMsisdn) {
        this.receiverMsisdn = receiverMsisdn;
    }

    public String getDeliverTS() {
        return deliverTS;
    }

    public void setDeliverTS(String deliverTS) {
        this.deliverTS = deliverTS;
    }

    public String getReadTS() {
        return readTS;
    }

    public void setReadTS(String readTS) {
        this.readTS = readTS;
    }
}
