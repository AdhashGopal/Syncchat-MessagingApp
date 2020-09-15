package com.chatapp.android.app.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;

/**
 * created by  Adhash Team on 4/5/2018.
 */

public class CustomDeleteDialog extends DialogFragment implements View.OnClickListener {

    private AvnNextLTProDemiTextView tvForMe, tvEveryOne,tvCancel;
    private AvnNextLTProRegTextView tvMessage;


    private String  message, ForMeBtnTitle, EveryOneBtnTitle,CancelBtnTitle;
    private boolean canShow = true;
    private CustomDeleteDialog.OnDeleteDialogCloseListener dialogCloseListener;


    /**
     * OnDeleteDialogCloseListener interface
     */
    public interface OnDeleteDialogCloseListener {
        void onForMeButtonClick();

        void onEveryOneButtonClick();

        void onCancelButtonClick();
    }


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));


        View view = inflater.inflate(R.layout.custom_delete_dialog, container, false);




        tvMessage = (AvnNextLTProRegTextView) view.findViewById(R.id.tvMessage);


        tvForMe = (AvnNextLTProDemiTextView) view.findViewById(R.id.tvforme);
        tvForMe.setOnClickListener(this);

        tvEveryOne = (AvnNextLTProDemiTextView) view.findViewById(R.id.tveveryone);
        tvEveryOne.setOnClickListener(this);

        tvCancel = (AvnNextLTProDemiTextView) view.findViewById(R.id.tvcancel);
        tvCancel.setOnClickListener(this);

        return view;
    }


    /**
     * Getting data to handling the UI
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if (ForMeBtnTitle != null && !ForMeBtnTitle.equals("")) {
            tvForMe.setText(ForMeBtnTitle);
        } else {
            tvForMe.setVisibility(View.GONE);
        }

        if (EveryOneBtnTitle != null && !EveryOneBtnTitle.equals("")) {
            tvEveryOne.setText(EveryOneBtnTitle);
        } else {
            tvEveryOne.setVisibility(View.GONE);
        }

        if (CancelBtnTitle != null && !CancelBtnTitle.equals("")) {
            tvCancel.setText(CancelBtnTitle);
        } else {
            tvCancel.setVisibility(View.GONE);
        }

        if (message != null && !message.equals("")) {
            tvMessage.setText(Html.fromHtml(message));
        }

    }


    public void setMessage(String message) {
        this.message = message;
    }

    public void setForMeButtonText(String buttonTitle) {
        this.ForMeBtnTitle = buttonTitle;
    }

    public void setEveryOneButtonText(String buttonTitle) {
        this.EveryOneBtnTitle = buttonTitle;
    }

    public void setCancelButtonText(String buttonTitle) {
        this.CancelBtnTitle = buttonTitle;
    }

    /**
     *  click event
     * @param dialogCloseListener
     */
    public void setDeleteDialogCloseListener(CustomDeleteDialog.OnDeleteDialogCloseListener dialogCloseListener) {
        this.dialogCloseListener = dialogCloseListener;
    }


    /**
     * click action
     * @param view select specific view
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tvforme:
                if (dialogCloseListener != null) {
                    dialogCloseListener.onForMeButtonClick();
                }
                dismiss();
                break;

            case R.id.tveveryone:
                if (dialogCloseListener != null) {
                    dialogCloseListener.onEveryOneButtonClick();
                }
                dismiss();
                break;

            case R.id.tvcancel:
                if (dialogCloseListener != null) {
                    dialogCloseListener.onCancelButtonClick();
                }
                dismiss();
                break;

        }
    }


}
