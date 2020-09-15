package com.chatapp.android.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

import com.chatapp.android.core.CoreController;

/**
 * created by  Adhash Team on 2/17/2017.
 */
public class AvnNextLTProRegButton extends Button {

    public AvnNextLTProRegButton(Context context) {
        super(context);
        init();
    }

    public AvnNextLTProRegButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvnNextLTProRegButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        setTypeface(face);
    }
}
