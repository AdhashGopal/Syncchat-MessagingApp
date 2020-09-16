package com.chatapp.synchat.core.model;

import java.io.Serializable;

/**
 * created by  Adhash Team on 11/30/2016.
 */
public class GroupMembersPojo implements Serializable {
    /**
     * Getter & setter method for specific event
     */
    private String userId, active, isDeleted, msisdn, phNumber, name, status, userDp,
            isAdminUser, contactName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getPhNumber() {
        return phNumber;
    }

    public void setPhNumber(String phNumber) {
        this.phNumber = phNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserDp() {
        return userDp;
    }

    public void setUserDp(String userDp) {
        this.userDp = userDp;
    }

    public String getIsAdminUser() {
        return isAdminUser;
    }

    public void setIsAdminUser(String isAdminUser) {
        this.isAdminUser = isAdminUser;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }
}
