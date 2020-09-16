package com.chatapp.synchat.core.model;

/**
 * created by  Adhash Team on 5/31/2017.
 */
public class OfflineRetryEventPojo {
    /**
     * Getter & setter method for specific event
     */
    private String eventName, eventId;
    private Object eventObject;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Object getEventObject() {
        return eventObject;
    }

    public void setEventObject(Object eventObject) {
        this.eventObject = eventObject;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }
}
