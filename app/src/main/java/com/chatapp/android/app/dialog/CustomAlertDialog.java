package com.chatapp.android.app.dialog;

import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.chatapp.android.R;
import com.chatapp.android.app.widget.AvnNextLTProDemiTextView;
import com.chatapp.android.app.widget.AvnNextLTProRegTextView;
import com.chatapp.android.core.CoreController;

/**
 * created by  Adhash Team on 12/30/2016.
 */
public class CustomAlertDialog extends DialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private AvnNextLTProDemiTextView tvNegative, tvPositive;
    private AvnNextLTProRegTextView tvMessage, tvTitle;
    private CheckBox checkBox;
    private EditText editText;
    private ImageView imageviewOk;

    private String text, message, title, positiveBtnTitle, negativeBtnTitle, etData, setImage;
    private boolean canShow = true;
    private OnCustomDialogCloseListener dialogCloseListener;
    private OnDialogCheckBoxCheckedChangeListener checkBoxCheckedChangeListener;


    /**
     * CustomDialog CloseListener interface (PositiveButton, NegativeButton)
     */
    public interface OnCustomDialogCloseListener {
        void onPositiveButtonClick();

        void onNegativeButtonClick();
    }

    /**
     * DialogCheckBoxCheckedChangeListener interface (getting boolean value)
     */
    public interface OnDialogCheckBoxCheckedChangeListener {
        void onCheckedChange(boolean isChecked);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();

        View view = inflater.inflate(R.layout.dialog_custom_alert, container, false);

        /*tvTitle = (TextView) view.findViewById(R.id.tvTitle);
        tvTitle.setTypeface(face);*/

        checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        checkBox.setTypeface(face);
        checkBox.setOnCheckedChangeListener(CustomAlertDialog.this);

        editText = (EditText) view.findViewById(R.id.etEnterpwd);
        editText.setTypeface(face);
        tvMessage = (AvnNextLTProRegTextView) view.findViewById(R.id.tvMessage);

        tvTitle = (AvnNextLTProRegTextView) view.findViewById(R.id.tvTitle);

        imageviewOk = (ImageView) view.findViewById(R.id.imageviewOk);

        tvNegative = (AvnNextLTProDemiTextView) view.findViewById(R.id.tvNegative);
        tvNegative.setOnClickListener(this);

        tvPositive = (AvnNextLTProDemiTextView) view.findViewById(R.id.tvPositive);
        tvPositive.setOnClickListener(this);

        return view;
    }

    /**
     * based on value updated UI
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       /* if(title != null && !title.equals("")) {
           tvTitle.setVisibility(View.INVISIBLE);
        } else {
            tvTitle.setText(title);
        }*/

        if (text != null && !text.equals("")) {
            checkBox.setVisibility(View.VISIBLE);
        } else {
            checkBox.setVisibility(View.GONE);
        }

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
        if (message != null && !message.equals("")) {
            tvMessage.setText(Html.fromHtml(message));
        }

        if (title != null && !title.equals("")) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(Html.fromHtml(title));
        } else {
            tvTitle.setVisibility(View.GONE);
        }


        if (etData != null && !etData.equals("")) {
            editText.setVisibility(View.VISIBLE);
        } else {
            editText.setVisibility(View.GONE);
        }

        if (setImage != null && !setImage.equals("")) {
            imageviewOk.setVisibility(View.VISIBLE);
        } else {
            imageviewOk.setVisibility(View.GONE);
        }
    }

   /* public void setTitle(String title) {
        this.title = title;
    }
*/

    public void setCheckBoxtext(String text) {
        this.text = text;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEditTextdata(String etData) {
        this.etData = etData;
    }

    public void setImagedrawable(String setImage) {
        this.setImage = setImage;
    }

    public void setPositiveButtonText(String buttonTitle) {
        this.positiveBtnTitle = buttonTitle;
    }

    public void setNegativeButtonText(String buttonTitle) {
        this.negativeBtnTitle = buttonTitle;
    }


    /**
     * click event
     *
     * @param dialogCloseListener
     */
    public void setCustomDialogCloseListener(OnCustomDialogCloseListener dialogCloseListener) {
        this.dialogCloseListener = dialogCloseListener;
    }

    /**
     * click event
     *
     * @param checkBoxCheckedChangeListener
     */
    public void setCheckBoxCheckedChangeListener(OnDialogCheckBoxCheckedChangeListener checkBoxCheckedChangeListener) {
        this.checkBoxCheckedChangeListener = checkBoxCheckedChangeListener;
    }

    /**
     * click action
     *
     * @param view specific view action
     */
    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.tvNegative:
                if (dialogCloseListener != null) {
                    dialogCloseListener.onNegativeButtonClick();
                }
                dismiss();
                break;

            case R.id.tvPositive:
                if (dialogCloseListener != null) {
                    dialogCloseListener.onPositiveButtonClick();
                }
                dismiss();
                break;

        }
    }

    /**
     * getting boolean value for CheckedChanged
     *
     * @param compoundButton
     * @param isChecked      getting value
     */
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (checkBoxCheckedChangeListener != null) {
            checkBoxCheckedChangeListener.onCheckedChange(isChecked);
        }
    }

}
