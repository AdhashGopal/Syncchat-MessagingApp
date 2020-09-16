package com.chatapp.synchat.core.model;

import java.io.Serializable;

/**
 *
 */
public class ChatappContactModel implements Serializable {
    /**
     * Getter & setter method for specific event
     */
    private String FirstName = "", Status = "", AvatarImageUrl = "", noInDevice = "", BlockStatus = "";
    private String _id, Msisdn = "";
    private String type;
    private int Fav_type = 0;
    private boolean isSelected = false;
    private String countryCode;
    private String RequestStatus;

    public String getGroupDocID() {
        return GroupDocID;
    }

    public void setGroupDocID(String groupDocID) {
        GroupDocID = groupDocID;
    }

    private String GroupDocID;

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    private boolean isGroup = false;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String FirstName) {
        this.FirstName = FirstName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String Status) {
        this.Status = Status;
    }

    public String getBlockStatus() {
        return BlockStatus;
    }

    public void setBlockStatus(String blockStatus) {
        this.BlockStatus = blockStatus;
    }

    public String getNumberInDevice() {
        return noInDevice;
    }

    public void setNumberInDevice(String noInDevice) {
        this.noInDevice = noInDevice;
    }

    public String getAvatarImageUrl() {
        return AvatarImageUrl;
    }

    public void setAvatarImageUrl(String AvatarImageUrl) {
        this.AvatarImageUrl = AvatarImageUrl;
    }

    public String getMsisdn() {
        return Msisdn;
    }

    public void setMsisdn(String Msisdn) {
        this.Msisdn = Msisdn;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String country_code) {
        this.countryCode = country_code;
    }

    public String getRequestStatus() {
        return RequestStatus;
    }

    public void setRequestStatus(String RequestStatus) {
        this.RequestStatus = RequestStatus;
    }


    public int getFavouriteStatus() {
        return Fav_type;
    }

    public void setFavouriteStatus(int FavouriteStatus) {
        this.Fav_type = FavouriteStatus;
    }
}