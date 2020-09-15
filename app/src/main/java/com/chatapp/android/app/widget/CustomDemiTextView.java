package com.chatapp.android.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.chatapp.android.core.CoreController;

/**
 * created by  Adhash Team on 4/17/2018.
 */

public class CustomDemiTextView extends TextView {

    public CustomDemiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomDemiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomDemiTextView(Context context) {
        super(context);
        init();
    }


    public void init() {
        Typeface tf = CoreController.getInstance().getAvnNextLTProDemiTypeface();
        setTypeface(tf);
    }
}