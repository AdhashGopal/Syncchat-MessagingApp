package com.chatapp.android.app.dialog;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chatapp.android.R;
import com.chatapp.android.app.utils.ConnectivityInfo;
import com.chatapp.android.app.utils.MuteUnmute;
import com.chatapp.android.app.utils.UserInfoSession;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.core.CoreActivity;
import com.chatapp.android.core.CoreController;
import com.chatapp.android.core.Session;
import com.chatapp.android.core.SessionManager;
import com.chatapp.android.core.database.ContactDB_Sqlite;
import com.chatapp.android.core.message.MessageFactory;
import com.chatapp.android.core.model.MuteStatusPojo;
import com.chatapp.android.core.model.MuteUserPojo;
import com.chatapp.android.core.model.ReceviceMessageEvent;
import com.chatapp.android.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * created by  Adhash Team on 6/23/2017.
 */
public class MuteAlertDialog extends DialogFragment {

    private View view;
    private RadioGroup rg;
    private RadioButton rb1, rb2, rb3;
    private AvnNextLTProDemiTextView cancel, ok, tvTitle;
    private CheckBox check;
    private CoreActivity mActivity;

    private Session session;
    private SessionManager sessionManager;
    private UserInfoSession userInfoSession;
    //    private String mCurrentUserId, mReceiverId, mLocDbDocId, muteDuration, chatType, secretType;
    private String mCurrentUserId, muteDuration, mLastMuteUserId;

    private MuteAlertCloseListener listener;
    private ArrayList<MuteUserPojo> muteUserList = new ArrayList<>();

    /**
     * MuteAlertClose Listener interface (onMuteDialogClosed for getting boolean value)
     */
    public interface MuteAlertCloseListener {
        void onMuteDialogClosed(boolean isMuted);
    }

    /**
     * onCreateView layout binding
     *
     * @param inflater           make a view
     * @param container          parent of view
     * @param savedInstanceState
     * @return view
     */
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        view = inflater.inflate(R.layout.dialog_mute_chat, container, false);

        rg = (RadioGroup) view.findViewById(R.id.radiogroupmain);
        rb1 = (RadioButton) view.findViewById(R.id.rb1);
        rb2 = (RadioButton) view.findViewById(R.id.rb2);
        rb3 = (RadioButton) view.findViewById(R.id.rb3);
        check = (CheckBox) view.findViewById(R.id.check1);
        cancel = (AvnNextLTProDemiTextView) view.findViewById(R.id.cancel);
        ok = (AvnNextLTProDemiTextView) view.findViewById(R.id.ok);
        tvTitle = (AvnNextLTProDemiTextView) view.findViewById(R.id.tvTitle);
        tvTitle.setText("Mute chat for...");

        Typeface f1 = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        rb1.setTypeface(f1);
        rb2.setTypeface(f1);
        rb3.setTypeface(f1);
        check.setTypeface(f1);

        mActivity = (CoreActivity) getActivity();
        mActivity.initProgress("Loading...", true);

        return view;
    }


    /**
     * getting data from other activity to set the data
     * select specific view
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        session = new Session(getActivity());
        sessionManager = SessionManager.getInstance(getActivity());
        userInfoSession = new UserInfoSession(getActivity());

        mCurrentUserId = sessionManager.getCurrentUserID();

        Bundle getBundle = getArguments();
        muteUserList = (ArrayList<MuteUserPojo>) getBundle.getSerializable("MuteUserList");

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (rb1.isChecked()) {
                    muteDuration = "8 Hours";
                } else if (rb2.isChecked()) {
                    muteDuration = "1 Week";
                } else if (rb3.isChecked()) {
                    muteDuration = "1 Year";
                }
            }
        });

        if (muteUserList != null && muteUserList.size() == 1) {
            MuteUserPojo muteUserItem = muteUserList.get(0);
            String toUserId = muteUserItem.getReceiverId();
            ContactDB_Sqlite contactDB_sqlite = CoreController.getContactSqliteDBintstance(getActivity());

            MuteStatusPojo muteData = null;

            if (muteUserItem.getChatType().equalsIgnoreCase("group")) {
                muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, null, toUserId, false);
            } else {
                String docId = mCurrentUserId + "-" + toUserId;
                String convId = userInfoSession.getChatConvId(docId);
                muteData = contactDB_sqlite.getMuteStatus(mCurrentUserId, toUserId, convId, false);
                Log.e("DataBase-->", muteData + "");
//                muteData = contactsDB.getMuteStatus(mCurrentUserId, toUserId, convId, false);
            }


            if (muteData != null && muteData.getMuteStatus().equals("1")) {
                if (muteData.getDuration().equalsIgnoreCase("8 Hours")) {
                    rb1.setChecked(true);
                } else if (muteData.getDuration().equalsIgnoreCase("1 Week")) {
                    rb2.setChecked(true);
                } else if (muteData.getDuration().equalsIgnoreCase("1 Year")) {
                    rb3.setChecked(true);
                }
                if (muteData.getNotifyStatus().equals("1")) {
                    check.setChecked(true);
                }
            }
        } else {
            rb1.setChecked(true);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityInfo.isInternetConnected(getActivity())) {
                    listener.onMuteDialogClosed(false);
                    getDialog().dismiss();
                } else {
                    Toast.makeText(getActivity(), "Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ConnectivityInfo.isInternetConnected(getActivity())) {
                    if (muteDuration == null || muteDuration.equals("")) {
                        Toast.makeText(getActivity(), "Please choose duration", Toast.LENGTH_SHORT).show();
                    } else {
                        for (MuteUserPojo muteUserItem : muteUserList) {
                            String receiverId = muteUserItem.getReceiverId();
                            String chatType = muteUserItem.getChatType();
                            String secretType = muteUserItem.getSecretType();
                            mLastMuteUserId = receiverId;

                            String locDbDocId = getLocDBDocId(muteUserItem);
//                            session.setMuteDuration(locDbDocId, muteDuration);

                            String convId = null;
                            if (chatType.equalsIgnoreCase(MessageFactory.CHAT_TYPE_SINGLE)) {
                                if (userInfoSession.hasChatConvId(locDbDocId)) {
                                    convId = userInfoSession.getChatConvId(locDbDocId);
                                }
                            } else {
                                // For group --- Group id and conversation id are same
                                convId = receiverId;
                            }

                            if (!muteDuration.equals("")) {
                                int notifyStatus = 0;
                                String value = getString(R.string.Default_ringtone);
                                session.putTone(value);
                                session.putgroupTone(value);
                                if (check.isChecked()) {
                                    notifyStatus = 1;
                                    session.putTone("None");
                                    session.putgroupTone("None");
                                }

                                MuteUnmute.muteUnmute(EventBus.getDefault(), mCurrentUserId, receiverId, convId,
                                        chatType, secretType, 1, muteDuration, notifyStatus);

                            } else {
                                listener.onMuteDialogClosed(false);
                            }

                            /*if (!check.isChecked()) {
                                session.setNotificationOnMute(locDbDocId, true);

                            } else {
                                session.setNotificationOnMute(locDbDocId, false);
                            }*/

                            mActivity.showProgressDialog();
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "Check Your Network Connection", Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    /**
     * setMuteAlertCloseListener
     *
     * @param listener click action function
     */
    public void setMuteAlertCloseListener(MuteAlertCloseListener listener) {
        this.listener = listener;
    }

    /**
     * Getting value from eventbus
     *
     * @param event based on value to call socket (mute)
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_MUTE)) {
            try {
                JSONObject objects = new JSONObject(event.getObjectsArray()[0].toString());

                String err = objects.getString("err");
                if (err.equalsIgnoreCase("1")) {
                    mActivity.hideProgressDialog();
                }

                String from = objects.getString("from");
                String to;
                if (objects.has("to")) {
                    to = objects.getString("to");
                } else {
                    to = objects.getString("convId");
                }

                if (from.equalsIgnoreCase(mCurrentUserId) && to.equalsIgnoreCase(mLastMuteUserId)) {
                    mActivity.hideProgressDialog();
                    listener.onMuteDialogClosed(true);
                    getDialog().dismiss();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * check the docid for single or group
     *
     * @param muteUserItem getting value from model class
     * @return value
     */
    private String getLocDBDocId(MuteUserPojo muteUserItem) {
        String docId = mCurrentUserId + "-" + muteUserItem.getReceiverId();

        if (muteUserItem.getChatType().equalsIgnoreCase(MessageFactory.CHAT_TYPE_GROUP)) {
            docId = docId + "-g";
        } else {
            if (muteUserItem.getSecretType().equalsIgnoreCase("yes")) {
                docId = docId + "-secret";
            }
        }

        return docId;
    }

    /**
     * Start eventbus function
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Stop eventbus function
     */
    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}
