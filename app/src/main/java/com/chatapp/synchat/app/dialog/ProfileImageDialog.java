package com.chatapp.synchat.app.dialog;

import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chatapp.synchat.R;
import com.chatapp.synchat.app.ChatViewActivity;
import com.chatapp.synchat.app.GroupInfo;
import com.chatapp.synchat.app.ImageZoom;
import com.chatapp.synchat.app.UserInfo;
import com.chatapp.synchat.app.utils.Getcontactname;
import com.chatapp.synchat.core.CoreController;
import com.chatapp.synchat.core.model.MessageItemChat;
import com.chatapp.synchat.core.service.Constants;
import com.squareup.picasso.Picasso;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;


/**
 * created by  Adhash Team on 2/23/2017.
 */
public class ProfileImageDialog extends DialogFragment implements View.OnClickListener {

    private View view;
    private RelativeLayout rlParent;
    private EmojiconTextView tvName;
    private ImageView ivProfilePic;
    private ImageButton ibChat, ibInfo;
    private FrameLayout flChat, flInfo;

    private String receiverUserId, receiverName, receiverAvatar, receiverNumber, Uid;
    private boolean isGroupChat, fromSecretChat;
    private MessageItemChat msgItem;


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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;

        view = inflater.inflate(R.layout.dialog_profile_image, container, false);

        tvName = (EmojiconTextView) view.findViewById(R.id.tvName);


        rlParent = (RelativeLayout) view.findViewById(R.id.rlParent);

        ivProfilePic = (ImageView) view.findViewById(R.id.ivProfilePic);
        ivProfilePic.setOnClickListener(ProfileImageDialog.this);
        //ivProfilePic.setFitsSystemWindows(true);
        ibChat = (ImageButton) view.findViewById(R.id.ibChat);
        ibChat.setOnClickListener(ProfileImageDialog.this);

        flChat = (FrameLayout) view.findViewById(R.id.flChat);
        flChat.setOnClickListener(ProfileImageDialog.this);

        ibInfo = (ImageButton) view.findViewById(R.id.ibInfo);
        ibInfo.setOnClickListener(ProfileImageDialog.this);

        flInfo = (FrameLayout) view.findViewById(R.id.flInfo);
        flInfo.setOnClickListener(ProfileImageDialog.this);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        ViewGroup.LayoutParams params = rlParent.getLayoutParams();
        params.height = (height / 100) * 40;
        params.width = (width / 100) * 80;
        rlParent.setLayoutParams(params);

        return view;
    }

    /**
     * Getting the value from other class
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Bundle bundle = getArguments();
        msgItem = (MessageItemChat) bundle.getSerializable("MessageItem");
        Uid = bundle.getString("userID");
        isGroupChat = bundle.getBoolean("GroupChat");
//        imageTS = bundle.getLong("imageTS");
        fromSecretChat = bundle.getBoolean("FromSecretChat", false);
//        SerializeBitmap bmp = (SerializeBitmap) bundle.getSerializable("ProfilePic");

        initData();

    }

    /**
     * binding the data
     */
    private void initData() {
        Typeface typeface = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        tvName.setTypeface(typeface);
        String msgId = msgItem.getMessageId();
        String[] splitId = msgId.split("-");
        receiverUserId = splitId[1];
        receiverAvatar = msgItem.getAvatarImageUrl();
//        receiverAvatar = receiverAvatar.concat("?id=").concat(String.valueOf(Calendar.getInstance().getTimeInMillis()));
        receiverName = msgItem.getSenderName();
        receiverNumber = msgItem.getSenderMsisdn();
        if (isGroupChat) {
            if (receiverAvatar != null && !receiverAvatar.isEmpty()) {
                Picasso.with(getActivity()).load(Constants.SOCKET_IP + receiverAvatar).error(
                        R.mipmap.ic_group_icon)
                        .into(ivProfilePic);
            } else {
                ivProfilePic.setImageResource(R.mipmap.ic_group_icon);
                ivProfilePic.setScaleType(ImageView.ScaleType.FIT_CENTER);
            }
        } else {
            Getcontactname getcontactname = new Getcontactname(getActivity());
            getcontactname.configProfilepic(ivProfilePic, receiverUserId, false, fromSecretChat, R.mipmap.ic_single_user_icon);
        }

        tvName.setText(receiverName);
    }

    /**
     * Clicking action
     *
     * @param view specific view action
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.ivProfilePic:
                if (receiverAvatar != null && !receiverAvatar.isEmpty()) {
                    Intent imgIntent = new Intent(getActivity(), ImageZoom.class);
                    imgIntent.putExtra("ProfilePath", receiverAvatar);
                    imgIntent.putExtra("Profilepicname", receiverName);
                    startActivity(imgIntent);
                    getDialog().dismiss();
                }
                break;

            case R.id.flChat:
                goChatViewScreen();
                getDialog().dismiss();
                break;

            case R.id.flInfo:
                goInfoScreen();
                getDialog().dismiss();
                break;

            case R.id.ibChat:
                goChatViewScreen();
                getDialog().dismiss();
                break;

            case R.id.ibInfo:
                goInfoScreen();
                getDialog().dismiss();
                break;
        }

    }

    /**
     * based on selection move to Group / User info
     */
    private void goInfoScreen() {
        Intent infoIntent;
        if (isGroupChat) {
            infoIntent = new Intent(getActivity(), GroupInfo.class);
            infoIntent.putExtra("GroupId", receiverUserId);
            infoIntent.putExtra("GroupName", receiverName);
        } else {
            infoIntent = new Intent(getActivity(), UserInfo.class);
            infoIntent.putExtra("UserId", receiverUserId);
            infoIntent.putExtra("UserName", receiverName);
            infoIntent.putExtra("UserNumber", receiverNumber);
            infoIntent.putExtra("FromSecretChat", fromSecretChat);
        }

        infoIntent.putExtra("UserAvatar", Constants.SOCKET_IP.concat(receiverAvatar));
        startActivity(infoIntent);
    }

    /**
     * based on selection move to chatview screen
     */
    private void goChatViewScreen() {

        Intent intent = new Intent(getActivity(), ChatViewActivity.class);
        String profileimage = msgItem.getAvatarImageUrl();
        intent.putExtra("receiverUid", msgItem.getNumberInDevice());
        intent.putExtra("receiverName", msgItem.getSenderName());
        intent.putExtra("documentId", receiverUserId);

        intent.putExtra("Username", msgItem.getSenderName());
        intent.putExtra("Image", msgItem.getAvatarImageUrl());
        intent.putExtra("msisdn", msgItem.getSenderMsisdn());
        intent.putExtra("type", 0);

        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }
}
