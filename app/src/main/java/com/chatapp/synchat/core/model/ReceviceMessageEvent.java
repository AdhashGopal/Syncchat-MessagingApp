package com.chatapp.synchat.core.model;

/**
 */
public class ReceviceMessageEvent {
    /**
     * Getter & setter method for specific event
     */
    private String eventName;

    private Object[] objectsArray;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public Object[] getObjectsArray() {
        return objectsArray;
    }

    public void setObjectsArray(Object[] objectsArray) {
        this.objectsArray = objectsArray;
    }
}
