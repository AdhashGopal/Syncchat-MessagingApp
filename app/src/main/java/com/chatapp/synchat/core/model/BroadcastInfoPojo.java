package com.chatapp.synchat.core.model;

import java.util.List;

/**
 * created by  Adhash Team on 12/19/2016.
 */
public class BroadcastInfoPojo {
    /**
     * Getter & setter method for specific event
     */
    private String broadcastId, broadcastName, avatarPath, isAdminUser, groupMembers, adminMembers,
            createdBy, groupContactNames;

    private boolean isLiveGroup = false,isbroadcast;


    public List<ChatappContactModel> chatappContactModels;

    public String getGroupContactNames() {
        return groupContactNames;
    }

    public void setGroupContactNames(String groupContactNames) {
        this.groupContactNames = groupContactNames;
    }


    public List<ChatappContactModel> getContactsModel() {
        return chatappContactModels;
    }

    public void setContactsModel(List<ChatappContactModel> chatappContactModels) {
        this.chatappContactModels = chatappContactModels;
    }


    public boolean isLiveGroup() {
        return isLiveGroup;
    }

    public void setLiveGroup(boolean liveGroup) {
        isLiveGroup = liveGroup;
    }

    public boolean hasbroadcast() {
        return isbroadcast;
    }

    public void setbroadcast(boolean isbroadcast) {
        isbroadcast = isbroadcast;
    }

    public String getAdminMembers() {
        return adminMembers;
    }

    public void setAdminMembers(String adminMembers) {
        this.adminMembers = adminMembers;
    }

    public String getBroadcastId() {
        return broadcastId;
    }

    public void setBroadcastId(String broadcastId) {
        this.broadcastId = broadcastId;
    }

    public String getBroadcastName() {
        return broadcastName;
    }

    public void setBroadcastName(String broadcastName) {
        this.broadcastName = broadcastName;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getIsAdminUser() {
        return isAdminUser;
    }

    public void setIsAdminUser(String isAdminUser) {
        this.isAdminUser = isAdminUser;
    }

    public String getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(String groupMembers) {
        this.groupMembers = groupMembers;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

}
