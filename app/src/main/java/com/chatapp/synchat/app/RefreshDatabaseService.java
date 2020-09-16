package com.chatapp.synchat.app;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.IBinder;

import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.model.MessageItemChat;

import java.util.ArrayList;
import java.util.List;

public class RefreshDatabaseService extends Service {

    private MessageDbController db;
    CountDownTimer timer;
    public static final String KEY_FROM_REFRESH_TIMER = "refreshTimer";
    public static final String MESSAGEITEM = "messageItem";
    MessageItemChat messageItemChat;
    List<CountDownTimer> countDownTimers = new ArrayList<>();
    List<MessageItemChat> messageItemChats = new ArrayList<>();

    /**
     * start searvicce
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        long time;

        if (intent != null) {
            messageItemChat = (MessageItemChat) intent.getSerializableExtra(MESSAGEITEM);
            time = intent.getLongExtra(KEY_FROM_REFRESH_TIMER, 1000);


            final CustomCountdownTimer customCountdownTimer = new CustomCountdownTimer(time, 1000, messageItemChat) {
                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    sendRefreshBroadcast(this.messageItemChat1);
                }
            };

            customCountdownTimer.start();
            //       timer = new CountDownTimer(time, 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//
//                }
//                @Override
//                public void onFinish() {
//
//                }
//            };
//
//            timer.start();
        }


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
//        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onCreate() {
        super.onCreate();


    }

    /**
     * cancel timer
     */
    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }

    /**
     * start RefreshService
     * @param context current activity
     * @param time timing
     * @param messageItemChat getting value from model class
     */
    public static void startRefreshService(Context context, long time, MessageItemChat messageItemChat) {

        Intent intent = new Intent(context, RefreshDatabaseService.class);
        intent.putExtra(KEY_FROM_REFRESH_TIMER, time);
        intent.putExtra(MESSAGEITEM, messageItemChat);
        context.startService(intent);

    }


    /**
     * send RefreshBroadcast
     *
     * @param messageItemChat1 (model value)
     */
    private void sendRefreshBroadcast(MessageItemChat messageItemChat1) {
        Intent broadcastIntent = new Intent("com.nowletschat.android.timerreceiver");
        broadcastIntent.putExtra(MESSAGEITEM, messageItemChat1);
        sendBroadcast(broadcastIntent);
    }


//    public void removeMessage()
//    {
//
//        db = CoreController.getDBInstance(this);
//        SendMessageEvent messageEvent = new SendMessageEvent();
//        messageEvent.setEventName(SocketManager.EVENT_REMOVE_MESSAGE);
//        try {
//            JSONObject deleteMsgObj = new JSONObject();
//            deleteMsgObj.put("from", from);
//            deleteMsgObj.put("type", chatType);
//            deleteMsgObj.put("convId", mConvId);
//            deleteMsgObj.put("status", "1");
//            deleteMsgObj.put("recordId", msgItem.getRecordId());
//            deleteMsgObj.put("last_msg", lastMsgStatus);
//            messageEvent.setMessageObject(deleteMsgObj);
//            EventBus.getDefault().post(messageEvent);
//
//            db.deleteChatMessage(docId, msgItem.getMessageId(), chatType);
//            db.updateTempDeletedMessage(msgItem.getRecordId(), deleteMsgObj);
//
//            int index = mChatData.indexOf(selectedChatItems.get(i));
//            if (index > -1) {
//                mChatData.remove(index);
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }


}
