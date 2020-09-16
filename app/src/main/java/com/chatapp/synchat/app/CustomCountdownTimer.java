package com.chatapp.synchat.app;

import android.os.CountDownTimer;

import com.chatapp.synchat.core.model.MessageItemChat;

/**
 * Created by karthik on 6/30/18.
 */

public abstract class CustomCountdownTimer extends CountDownTimer {

    public MessageItemChat messageItemChat1;

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CustomCountdownTimer(long millisInFuture, long countDownInterval,MessageItemChat messageItemChat ) {

        super(millisInFuture, countDownInterval);
        this.messageItemChat1=messageItemChat;
    }

}
