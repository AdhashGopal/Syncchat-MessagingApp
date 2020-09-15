package com.chatapp.android.core.model;

/**
 * created by  Adhash Team on 12/19/2016.
 */
public class GroupInfoPojo {
    /**
     * Getter & setter method for specific event
     */
    private String groupId, groupName, avatarPath, isAdminUser, groupMembers, adminMembers,
            createdBy, groupContactNames;

    private boolean isLiveGroup = false;

    public String getGroupContactNames() {
        return groupContactNames;
    }

    public void setGroupContactNames(String groupContactNames) {
        this.groupContactNames = groupContactNames;
    }

    public boolean isLiveGroup() {
        return isLiveGroup;
    }

    public void setLiveGroup(boolean liveGroup) {
        isLiveGroup = liveGroup;
    }

    public String getAdminMembers() {
        return adminMembers;
    }

    public void setAdminMembers(String adminMembers) {
        this.adminMembers = adminMembers;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
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
