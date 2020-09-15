//package com.chatapp.android.app;
//
//import android.app.IntentService;
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.os.CountDownTimer;
//import android.os.IBinder;
//import android.support.annotation.Nullable;
//
//import com.chatapp.android.core.database.MessageDbController;
//import com.chatapp.android.core.model.MessageItemChat;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.locks.Condition;
//import java.util.concurrent.locks.Lock;
//import java.util.concurrent.locks.ReentrantLock;
//
//public class RefreshDatabaseIntentService extends IntentService {
//
//    private MessageDbController db;
//    CountDownTimer timer;
//    public static final String KEY_FROM_REFRESH_TIMER = "refreshTimer";
//    public static final String MESSAGEITEM = "messageItem";
//    MessageItemChat messageItemChat;
//    List<CountDownTimer>countDownTimers=new ArrayList<>();
//    List<MessageItemChat>messageItemChats=new ArrayList<>();
//
//    private final Lock lock = new ReentrantLock();
//    private final Condition waitForFinish = lock.newCondition();
//    long time;
//    private boolean started;
//
//
//    public RefreshDatabaseIntentService() {
//        super("RefreshDatabaseIntentService");
//    }
//
//    @Override
//    protected void onHandleIntent(@Nullable Intent intent) {
//
//
//
//        if (intent != null) {
//            messageItemChat = (MessageItemChat) intent.getSerializableExtra(MESSAGEITEM);
//            time = intent.getLongExtra(KEY_FROM_REFRESH_TIMER, 1000);
//
//            handleStartAction();
//
//            lock.lock();
//            try {
//                while (started) {
//                    waitForFinish.await();
//                }
//            } catch (InterruptedException ie) {
//                // log exceptions
//            } finally {
//                lock.unlock();
//            }
//
//
//
//        }
//    }
//
//
//    private void handleStartAction() {
//        started=true;
//        final CustomCountdownTimer customCountdownTimer = new CustomCountdownTimer(time, 1000, messageItemChat) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//
//                }
//
//                @Override
//                public void onFinish() {
//                    started = false;
//                    sendRefreshBroadcast(this.messageItemChat1);
//                    lock.lock();
//                    try {
//                        waitForFinish.signal();
//                    } finally {
//                        lock.unlock();
//                    }
//
//                }
//            };
//
//            customCountdownTimer.start();
//    }
//
//    boolean isStarted() {
//        return started;
//    }
//
//
//
//
//    public static void startRefreshService(Context context, long time, MessageItemChat messageItemChat) {
//
//        Intent intent =  new Intent();
//        intent.setClass(context, RefreshDatabaseIntentService.class);
//        intent.putExtra(KEY_FROM_REFRESH_TIMER, time);
//        intent.putExtra(MESSAGEITEM,messageItemChat);
//        context.startService(intent);
////
////            Intent intent = new Intent(context, RefreshDatabaseIntentService.class);
////            intent.putExtra(KEY_FROM_REFRESH_TIMER, time);
////            intent.putExtra(MESSAGEITEM,messageItemChat);
////            context.startService(intent);
//
//    }
//
//
//
//    private void sendRefreshBroadcast(MessageItemChat messageItemChat1) {
//        Intent broadcastIntent = new Intent("com.nowletschat.android.timerreceiver");
//        broadcastIntent.putExtra(MESSAGEITEM,messageItemChat1);
//        sendBroadcast(broadcastIntent);
//    }
//
//
//
//
//
//
//
//}
