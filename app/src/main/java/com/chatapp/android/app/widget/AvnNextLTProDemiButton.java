package com.chatapp.android.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.chatapp.android.core.CoreController;

/**
 * created by  Adhash Team on 2/17/2017.
 */
public class AvnNextLTProDemiButton extends Button {

    public AvnNextLTProDemiButton(Context context) {
        super(context);
        init();
    }

    public AvnNextLTProDemiButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvnNextLTProDemiButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface face = CoreController.getInstance().getAvnNextLTProDemiTypeface();
        setTypeface(face);
    }
}
