package com.chatapp.android.core.model;

import java.io.Serializable;

/**
 *
 */
public class ContactSearchModel implements Serializable {
    /**
     * Getter & setter method for specific event
     */
    private String Status, status, profile_photo, last_seen, BlockStatus;
    private int Fav_type = 0;

    public String getUserPrivacyStatus() {
        return status;
    }

    public void setUserPrivacyStatus(String privacyStatus) {
        this.status = privacyStatus;
    }

    public String getUserPrivacyProfilePhoto() {
        return profile_photo;
    }

    public void setUserPrivacyProfilePhoto(String profilePhoto) {
        this.profile_photo = profilePhoto;
    }

    public String getUserPrivacyLastSeen() {
        return last_seen;
    }

    public void setUserPrivacyLastSeen(String privacyLastSeen) {
        this.last_seen = privacyLastSeen;
    }

    public String getUserStatus() {
        return Status;
    }

    public void setUserStatus(String Status) {
        this.Status = Status;
    }

    private String Name = "", AvatarImageUrl = "";
    private String id, msisdn = "";
    private int RequestStatus;

    public String getId() {
        return id;
    }

    public void setId(String Id) {
        this.id = Id;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getAvatarImageUrl() {
        return AvatarImageUrl;
    }

    public void setAvatarImageUrl(String AvatarImageUrl) {
        this.AvatarImageUrl = AvatarImageUrl;
    }

    public String setMsisdn(String msisdn) {
        return msisdn;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public int getRequestStatus() {
        return RequestStatus;
    }

    public void setRequestStatus(int RequestStatus) {
        this.RequestStatus = RequestStatus;
    }

    public String getBlockStatus() {
        return BlockStatus;
    }

    public void setBlockStatus(String BlockStatus) {
        this.BlockStatus = BlockStatus;
    }


    public int getFavouriteStatus() {
        return Fav_type;
    }

    public void setFavouriteStatus(int FavouriteStatus) {
        this.Fav_type = FavouriteStatus;
    }
}