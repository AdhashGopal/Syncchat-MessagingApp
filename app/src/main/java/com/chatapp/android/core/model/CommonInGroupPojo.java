package com.chatapp.android.core.model;

import java.io.Serializable;

/**
 * created by  Adhash Team on 12/26/2016.
 */
public class CommonInGroupPojo implements Serializable {
    /**
     * Getter & setter method for specific event
     */
    private String avatarPath;
    private String groupName;
    private String groupContactNames;
    private String groupId;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupContactNames() {
        return groupContactNames;
    }

    public void setGroupContactNames(String groupContactNames) {
        this.groupContactNames = groupContactNames;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

}
