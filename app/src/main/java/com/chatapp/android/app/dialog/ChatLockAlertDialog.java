package com.chatapp.android.app.dialog;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;

/**
 * created by  Adhash Team on 5/30/2017.
 */
public class ChatLockAlertDialog extends DialogFragment implements View.OnClickListener {

    private AvnNextLTProDemiTextView tvNegative, tvPositive;
    private AvnNextLTProRegTextView tvTitle;
    private ImageView imageviewOk;

    private String title, positiveBtnTitle, negativeBtnTitle,setImage;

    private OnCustomDialogCloseListener dialogCloseListener;

    public interface OnCustomDialogCloseListener {
        void onPositiveButtonClick();

        void onNegativeButtonClick();
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        View view = inflater.inflate(R.layout.chat_lock_alert, container, false);

        tvTitle = (AvnNextLTProRegTextView) view.findViewById(R.id.tvTitle);

        imageviewOk = (ImageView) view .findViewById(R.id.imageviewOk);

        tvNegative = (AvnNextLTProDemiTextView) view.findViewById(R.id.tvNegative);
        tvNegative.setOnClickListener(this);

        tvPositive = (AvnNextLTProDemiTextView) view.findViewById(R.id.tvPositive);
        tvPositive.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       /* if(title != null && !title.equals("")) {
           tvTitle.setVisibility(View.INVISIBLE);
        } else {
            tvTitle.setText(title);
        }*/



        if (positiveBtnTitle != null && !positiveBtnTitle.equals("")) {
            tvPositive.setText(positiveBtnTitle);
        } else {
            tvPositive.setVisibility(View.GONE);
        }

        if (negativeBtnTitle != null && !negativeBtnTitle.equals("")) {
            tvNegative.setText(negativeBtnTitle);
        } else {
            tvNegative.setVisibility(View.GONE);
        }


        if (title != null && !title.equals("")) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(Html.fromHtml(title));
        } else {
            tvTitle.setVisibility(View.GONE);
        }


        if(setImage != null && ! setImage.equals("")){
            imageviewOk.setVisibility(View.VISIBLE);
        } else {
            imageviewOk.setVisibility(View.GONE);
        }
    }

   /* public void setTitle(String title) {
        this.title = title;
    }
*/



    public void setTitle(String title) {
        this.title = title;
    }


    public void setImagedrawable(String setImage)
    {
        this.setImage = setImage;
    }

    public void setPositiveButtonText(String buttonTitle) {
        this.positiveBtnTitle = buttonTitle;
    }

    public void setNegativeButtonText(String buttonTitle) {
        this.negativeBtnTitle = buttonTitle;
    }

    public void setCustomDialogCloseListener(OnCustomDialogCloseListener dialogCloseListener) {
        this.dialogCloseListener = dialogCloseListener;
    }



    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tvNegative:
                dialogCloseListener.onNegativeButtonClick();
                dismiss();
                break;

            case R.id.tvPositive:
                dialogCloseListener.onPositiveButtonClick();
                dismiss();
                break;

        }
    }


}
