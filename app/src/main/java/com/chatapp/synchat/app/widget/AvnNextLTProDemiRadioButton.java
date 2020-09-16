package com.chatapp.synchat.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatRadioButton;

import com.chatapp.synchat.core.CoreController;

/**
 * created by  Adhash Team on 4/20/2018.
 */

public class AvnNextLTProDemiRadioButton extends AppCompatRadioButton {
    public AvnNextLTProDemiRadioButton(Context context) {
        super(context);
        init();
    }

    public AvnNextLTProDemiRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvnNextLTProDemiRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init() {
        Typeface face = CoreController.getInstance().getAvnNextLTProDemiTypeface();
        setTypeface(face);

    }

}
