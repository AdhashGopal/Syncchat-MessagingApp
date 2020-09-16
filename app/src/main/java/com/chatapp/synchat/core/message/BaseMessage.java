package com.chatapp.synchat.core.message;

import android.content.Context;

import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.chatapphelperclass.ChatappUtilities;

import java.util.Calendar;

/**
 * Created by Administrator on 10/27/2016.
 */
public class BaseMessage {

    public static final String STATUS_DELIVERED = "0";
    protected String id;
    String tsForServerEpoch, tsForServer;
    int type;
    protected String from;
    protected String to;
    protected String status;
    protected MessageItemChat item;

    private SessionManager sessionManager;


    public BaseMessage(Context context) {
        tsForServer = ChatappUtilities.tsInGmt();
        tsForServerEpoch = new ChatappUtilities().gmtToEpoch(tsForServer);
        sessionManager = SessionManager.getInstance(context);
        from = sessionManager.getCurrentUserID();

        getShortTimeFormat();
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public MessageItemChat getItem() {
        return item;
    }

    public void setItem(MessageItemChat item) {
        this.item = item;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getShortTimeFormat() {
        long deviceTS = Calendar.getInstance().getTimeInMillis();
        long timeDiff = sessionManager.getServerTimeDifference();

        long localTime = deviceTS - timeDiff;
        return String.valueOf(localTime);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
