package com.chatapp.synchat.core.socket;

import io.socket.client.Socket;

/**
 */
public enum SocketEvents {

    //group
    getHeartbeat("Heartbeat"),
    getMessageResponseFromServer("MessageRes"),
    sendMessageToServer("Message"),
    group("group"),
    chatHistory("GetMessages"),
    doubleTick("MessageStatusUpdate"),
    messageAck("MessageAck"),
    acknowledgementHistory("GetMessageAcks"),
    getCurrentTimeStatus("getCurrentTimeStatus"),
    changeOnlineStatus("ChangeOnlineStatus"),
    changeSt("changeSt"),
    typing("typing"),
    broadCastProfile("broadCastProfile"),
    connect(Socket.EVENT_CONNECT),
    disconnect(Socket.EVENT_DISCONNECT),
    connectError(Socket.EVENT_CONNECT_ERROR),
    timeout(Socket.EVENT_CONNECT_TIMEOUT);
    public String value;


    SocketEvents(String value) {
        this.value = value;
    }


}