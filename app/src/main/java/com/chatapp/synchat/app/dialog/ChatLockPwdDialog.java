package com.chatapp.synchat.app.dialog;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.ChatViewActivity;
import com.chatapp.synchat.app.utils.ConnectivityInfo;
import com.chatapp.synchat.app.utils.StringCryptUtils;
import com.chatapp.synchat.app.utils.UserInfoSession;
import com.chatapp.synchat.core.CoreActivity;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.Session;
import com.chatapp.synchat.core.SessionManager;
import com.chatapp.synchat.core.database.MessageDbController;
import com.chatapp.synchat.core.model.ChatLockPojo;
import com.chatapp.synchat.core.model.ChatappContactModel;
import com.chatapp.synchat.core.model.CommonInGroupPojo;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.model.ReceviceMessageEvent;
import com.chatapp.synchat.core.model.SendMessageEvent;
import com.chatapp.synchat.core.service.Constants;
import com.chatapp.synchat.core.socket.SocketManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * created by  Adhash Team on 5/3/2017.
 */
public class ChatLockPwdDialog extends DialogFragment implements View.OnClickListener {

    EditText newPassword_Et, confirmPassword_Et;
    Button button_setPwd;
    String newPwd, cnfrmPwd, from, convID, type, pwd, status, page, avatar, contactName, msisdn, docid;
    String etData1, etData2, txt1, txt2, BtnTitle, title, frgtpwd;
    TextView newPwdText, cnfrmPwdText, header, forgotChatpwd;
    private UserInfoSession userInfoSession;
    private Session session;
    private View view_1, view_2;
    private MessageItemChat msgItem;
    private ChatappContactModel socketitems;
    private CommonInGroupPojo commoningroup;
    String receiverUserId;
    MessageDbController db;

    private Handler eventHandler;
    private Runnable eventRunnable;

    private ProgressDialog progressDialog;

    /**
     * onCreateView layout binding
     *
     * @param inflater           make a view
     * @param container          parent of view
     * @param savedInstanceState
     * @return view
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        Bundle bundle = getArguments();
        from = bundle.getString("from");
        convID = bundle.getString("convID");
        type = bundle.getString("type");
        pwd = bundle.getString("pwd", "");
        status = bundle.getString("status");
        page = bundle.getString("page");
        contactName = bundle.getString("contactName");
        avatar = bundle.getString("avatar");
        msisdn = bundle.getString("msisdn");
        docid = bundle.getString("docid");
        msgItem = (MessageItemChat) bundle.getSerializable("MessageItem");
        socketitems = (ChatappContactModel) bundle.getSerializable("socketitems");
        commoningroup = (CommonInGroupPojo) bundle.getSerializable("commoningroup");
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        userInfoSession = new UserInfoSession(getContext());
        session = new Session(getContext());
        db = CoreController.getDBInstance(getActivity());

        View view = inflater.inflate(R.layout.chat_lock_pwd_dialog, container, false);
        newPassword_Et = (EditText) view.findViewById(R.id.newPassword_Et);
        confirmPassword_Et = (EditText) view.findViewById(R.id.confirmPassword_Et);
        button_setPwd = (Button) view.findViewById(R.id.button_setPwd);
        newPwdText = (TextView) view.findViewById(R.id.newPasswordLabel);
        cnfrmPwdText = (TextView) view.findViewById(R.id.confirmPasswordLabel);
        header = (TextView) view.findViewById(R.id.header);
        forgotChatpwd = (TextView) view.findViewById(R.id.forgotChatpwd);
        view_1 = view.findViewById(R.id.view_1);
        view_2 = view.findViewById(R.id.view_2);

        button_setPwd.setOnClickListener(this);
        forgotChatpwd.setOnClickListener(this);

        CoreActivity coreActivity = (CoreActivity) getActivity();
        progressDialog = coreActivity.getProgressDialogInstance();
        progressDialog.setMessage("Loading...");

        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (eventRunnable != null) {
                    eventHandler.removeCallbacks(eventRunnable);
                }
            }
        });

        return view;
    }


    /**
     * handling UI view based on data
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (etData1 != null && !etData1.equals("")) {
            newPassword_Et.setVisibility(View.VISIBLE);
            view_1.setVisibility(View.VISIBLE);
        } else {
            newPassword_Et.setVisibility(View.GONE);
            view_1.setVisibility(View.VISIBLE);

        }

        if (etData2 != null && !etData2.equals("")) {
            confirmPassword_Et.setVisibility(View.VISIBLE);
            view_2.setVisibility(View.VISIBLE);
        } else {
            confirmPassword_Et.setVisibility(View.GONE);
            view_2.setVisibility(View.GONE);
        }


        if (txt1 != null && !txt1.equals("")) {
            newPwdText.setText(txt1);
        } else {
            newPwdText.setVisibility(View.GONE);
        }


        if (txt2 != null && !txt2.equals("")) {
            cnfrmPwdText.setText(txt2);
        } else {
            cnfrmPwdText.setVisibility(View.GONE);
        }

        if (BtnTitle != null && !BtnTitle.equals("")) {
            button_setPwd.setText(BtnTitle);
        } else {
            button_setPwd.setVisibility(View.GONE);
        }


        if (title != null && !title.equals("")) {
            header.setText(title);
        } else {
            header.setVisibility(View.GONE);
        }


        if (frgtpwd != null && !frgtpwd.equals("")) {
            forgotChatpwd.setText(frgtpwd);
        } else {
            forgotChatpwd.setVisibility(View.GONE);
        }

    }


    public void setTextLabel1(String text) {
        this.txt1 = text;
    }

    public void setTextLabel2(String text) {
        this.txt2 = text;
    }

    public void setEditTextdata(String etData) {
        this.etData1 = etData;
    }

    public void setEditTextdata2(String etData) {
        this.etData2 = etData;
    }

    public void setHeader(String title) {
        this.title = title;
    }


    public void setforgotpwdlabel(String frgtpwd) {
        this.frgtpwd = frgtpwd;
    }

    public void setButtonText(String buttonTitle) {
        this.BtnTitle = buttonTitle;
    }


    /**
     * click action
     * based on id to perform the function
     *
     * @param v specific view
     */
    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.button_setPwd:
                newPwd = newPassword_Et.getText().toString();
                cnfrmPwd = confirmPassword_Et.getText().toString();

                String enteredPwdEncrypt = getEncryptPwd(newPwd);

                if (page.equalsIgnoreCase("chatlist")) {
                    if (newPwd.equals("")) {
                        Toast.makeText(getContext(), "Field must not be empty", Toast.LENGTH_SHORT).show();
                    } else {
                        if (enteredPwdEncrypt != null && enteredPwdEncrypt.equals(pwd)) {
                            getDialog().dismiss();
                            if (msgItem != null) {
                                String msgId = msgItem.getMessageId();
                                String[] splitId = msgId.split("-");
                                receiverUserId = splitId[1];
                                Intent intent = new Intent(getContext(), ChatViewActivity.class);
                                intent.putExtra("receiverUid", msgItem.getNumberInDevice());
                                intent.putExtra("receiverName", msgItem.getSenderName());
                                intent.putExtra("documentId", receiverUserId);
                                intent.putExtra("Username", msgItem.getSenderName());
                                intent.putExtra("Image", msgItem.getAvatarImageUrl());
                                intent.putExtra("msisdn", msgItem.getSenderMsisdn());
                                intent.putExtra("type", 0);
                                startActivity(intent);
                            } else if (socketitems != null) {
                                String msgId = socketitems.get_id();
                                Intent intent = new Intent(getContext(), ChatViewActivity.class);
                                intent.putExtra("receiverUid", socketitems.getNumberInDevice());
                                intent.putExtra("receiverName", socketitems.getFirstName());
                                intent.putExtra("documentId", msgId);
                                intent.putExtra("Username", socketitems.getFirstName());
                                intent.putExtra("Image", socketitems.getAvatarImageUrl());
                                intent.putExtra("msisdn", socketitems.getNumberInDevice());
                                intent.putExtra("type", 0);
                                startActivity(intent);
                            } else if (commoningroup != null) {
                                Intent intent = new Intent(getContext(), ChatViewActivity.class);
                                intent.putExtra("receiverUid", commoningroup.getGroupId());
                                intent.putExtra("receiverName", commoningroup.getGroupName());
                                intent.putExtra("documentId", commoningroup.getGroupId());
                                intent.putExtra("Username", commoningroup.getGroupName());
                                intent.putExtra("Image", commoningroup.getAvatarPath());
                                intent.putExtra("msisdn", "");
                                intent.putExtra("type", 0);
                                startActivity(intent);
                            } else if (docid != null) {
                                Intent intent = new Intent(getContext(), ChatViewActivity.class);
                                intent.putExtra("msisdn", "");
                                intent.putExtra("Username", contactName);
                                intent.putExtra("documentId", docid);
                                intent.putExtra("receiverUid", "");
                                intent.putExtra("Image", avatar);
                                intent.putExtra("type", 0);
                                intent.putExtra("receiverName", contactName);
                                intent.putExtra("msisdn", msisdn);
                                startActivity(intent);
                            }
                        } else {
                            Toast.makeText(getContext(), "Incorrect password", Toast.LENGTH_SHORT).show();

                        }
                    }
                } else if (page.equalsIgnoreCase("chatview")) {

                    if (status.equals("0")) {
                        if (newPwd.equals("") || cnfrmPwd.equals("")) {
                            Toast.makeText(getContext(), "Field must not be empty", Toast.LENGTH_SHORT).show();
                        } else if (newPwd.contains(" ")) {
                            Toast.makeText(getContext(), "Your password can't contain spaces.", Toast.LENGTH_SHORT).show();
                        } else if (!newPwd.equals(cnfrmPwd)) {
                            Toast.makeText(getContext(), "Password doesn't match", Toast.LENGTH_SHORT).show();
                        } else if (newPwd.length() < 6) {
                            Toast.makeText(getContext(), "password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                        } else {
                            updateDatatoserver();
                        }
                    } else if (status.equals("1")) {
                        if (newPwd.equals("")) {
                            Toast.makeText(getContext(), "Field must not be empty", Toast.LENGTH_SHORT).show();
                        } else {
                            if (enteredPwdEncrypt == null || !enteredPwdEncrypt.equals(pwd)) {
                                Toast.makeText(getContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                            } else {
                                updateDatatoserver();
                            }
                        }
                    }
                }
                break;

            case R.id.forgotChatpwd:
                Uri uri = Uri.parse(Constants.SOCKET_IP + "fp"); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                break;


        }
    }

    /**
     * update the event chat lock to server
     */
    private void updateDatatoserver() {

        if (!ConnectivityInfo.isInternetConnected(getActivity())) {
            Toast.makeText(getActivity(), "Check your network connection", Toast.LENGTH_SHORT).show();
        } else {

            if (progressDialog != null && !progressDialog.isShowing()) {
                progressDialog.show();
                setEventTimeout();
            }

            SendMessageEvent messageEvent = new SendMessageEvent();
            messageEvent.setEventName(SocketManager.EVENT_CHAT_LOCK);
            try {
                JSONObject lockObj = new JSONObject();
                lockObj.put("from", from);
                lockObj.put("password", newPwd);
                lockObj.put("convId", convID);
                lockObj.put("type", type);
                lockObj.put("confirm_password", cnfrmPwd);
                lockObj.put("mobile_password", getEncryptPwd(newPwd));
                lockObj.put("mode", "phone");

                String receiverId = userInfoSession.getReceiverIdByConvId(convID);
               /* String userId = SessionManager.getInstance(getContext()).getCurrentUserID();
                String docId = "";
                if (type.equals("single")) {
                    docId = userId.concat("-").concat(receiverId);
                } else {
                    docId = userId.concat("-").concat(convID).concat("-g");
                }*/

                ChatLockPojo lockPojo = db.getChatLockData(receiverId, type);
                if (lockPojo != null && lockPojo.getStatus() != null) {
                    if (lockPojo.getStatus().equals("0")) {
                        lockObj.put("status", "1");
                    } else {
                        lockObj.put("status", "0");
                    }

                } else {
                    lockObj.put("status", "1");
                }

                messageEvent.setMessageObject(lockObj);
                EventBus.getDefault().post(messageEvent);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *  Event Timeout
     */
    private void setEventTimeout() {
        if (eventHandler == null) {
            eventHandler = new Handler();
            eventRunnable = new Runnable() {
                @Override
                public void run() {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        Toast.makeText(getActivity(), "Try again", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }
            };
        }

        eventHandler.postDelayed(eventRunnable, SocketManager.RESPONSE_TIMEOUT);
    }

    /**
     *get Encrypt Pwd
     * @param password input (password)
     * @return value
     */
    private String getEncryptPwd(String password) {
        try {
            String iv = getString(R.string.app_name);
            StringCryptUtils cryptLib = new StringCryptUtils();
            return cryptLib.encrypt(password, convID, iv);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * ChatLock response
     * @param event based on response check chat unlocked  / locked
     */
    private void loadChatLockResponse(ReceviceMessageEvent event) {
        Object[] array = event.getObjectsArray();

        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        try {
            JSONObject objects = new JSONObject(array[0].toString());
            String err = objects.getString("err");
            String from = objects.getString("from");
            String message = objects.getString("msg");
            String mode = objects.getString("mode");
            // TODO: 7/14/2017 get locked chat user ids on login
            if (from.equalsIgnoreCase(SessionManager.getInstance(getContext()).getCurrentUserID())
                    && err.equals("0") && mode.equalsIgnoreCase("phone")) {

                if (status.equals("1")) {
                    Toast.makeText(getContext(), "Chat unlocked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Chat locked successfully", Toast.LENGTH_SHORT).show();
                }
                getDialog().dismiss();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Getting value from Eventbus
     * @param event based on value to call chat lock socket
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReceviceMessageEvent event) {
        if (event.getEventName().equalsIgnoreCase(SocketManager.EVENT_CHAT_LOCK)) {
            loadChatLockResponse(event);
        }
    }

    /**
     * Start EventBus function
     */
    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    /**
     * Stop EventBus function
     */
    @Override
    public void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }
}
