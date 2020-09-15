package com.chatapp.android.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.chatapp.android.core.CoreController;

/**
 * created by  Adhash Team on 4/17/2018.
 */

public class CustomRegTextView extends TextView {

    public CustomRegTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomRegTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomRegTextView(Context context) {
        super(context);
        init();
    }


    public void init() {
        Typeface tf = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        setTypeface(tf);
    }
}