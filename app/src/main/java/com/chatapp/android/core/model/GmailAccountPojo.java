package com.chatapp.android.core.model;

/**
 * created by  Adhash Team on 5/10/2017.
 */
public class GmailAccountPojo {
    /**
     * Getter & setter method for specific event
     */
    private String mailId;
    private boolean isSelected;

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
