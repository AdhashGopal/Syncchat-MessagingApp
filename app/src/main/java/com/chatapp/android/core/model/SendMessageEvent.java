package com.chatapp.android.core.model;

import org.json.JSONObject;

/**
 */
public class SendMessageEvent {
    /**
     * Getter & setter method for specific event
     */
    private String eventName;

    private JSONObject messageObject;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }


    public JSONObject getMessageObject() {
        return messageObject;
    }

    public void setMessageObject(JSONObject messageObject) {
        this.messageObject = messageObject;
    }
   /* public void setMessageObject(String messageObject) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("data", messageObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.messageObject = obj;
    }*/
}
