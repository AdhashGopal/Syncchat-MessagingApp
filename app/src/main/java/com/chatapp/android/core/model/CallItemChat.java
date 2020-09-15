package com.chatapp.android.core.model;

import java.io.Serializable;

/**
 * created by  Adhash Team on 7/27/2017.
 */
public class CallItemChat implements Serializable {
    /**
     * Getter & setter method for specific event
     */
    private String id, callId, opponentUserId, recordId, callerName, ts, callStatus, callType,
            calledAtObj, opponentUserMsisdn, callDuration;
    private int callCount;
    private boolean isSelf;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpponentUserMsisdn() {
        return opponentUserMsisdn;
    }

    public void setOpponentUserMsisdn(String opponentUserMsisdn) {
        this.opponentUserMsisdn = opponentUserMsisdn;
    }

    public String getCalledAtObj() {
        return calledAtObj;
    }

    public void setCalledAtObj(String calledAtObj) {
        this.calledAtObj = calledAtObj;
    }

    public String getCallId() {
        return callId;
    }

    public void setCallId(String callId) {
        this.callId = callId;
    }

    public String getOpponentUserId() {
        return opponentUserId;
    }

    public void setOpponentUserId(String opponentUserId) {
        this.opponentUserId = opponentUserId;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getTS() {
        return ts;
    }

    public void setTS(String ts) {
        this.ts = ts;
    }

    public String getCallStatus() {
        return callStatus;
    }

    public void setCallStatus(String deliveryStatus) {
        this.callStatus = deliveryStatus;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public int getCallCount() {
        return callCount;
    }

    public void setCallCount(int callCount) {
        this.callCount = callCount;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public void setIsSelf(boolean self) {
        isSelf = self;
    }
}
