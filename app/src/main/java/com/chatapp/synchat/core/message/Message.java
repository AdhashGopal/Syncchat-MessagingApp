package com.chatapp.synchat.core.message;

/**
 * Created by Administrator on 10/27/2016.
 */
public interface Message {

   Object getMessageObject(String to,String payload,Boolean isSecretchat);
   Object getGroupMessageObject(String to, String payload, String groupName);
}
