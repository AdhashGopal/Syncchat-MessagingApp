package com.chatapp.android.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.chatapp.android.core.CoreController;

/**
 * created by  Adhash Team on 2/17/2017.
 */
public class AvnNextLTProDemiEditText extends EditText {

    public AvnNextLTProDemiEditText(Context context) {
        super(context);
        init();
    }

    public AvnNextLTProDemiEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvnNextLTProDemiEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface face = CoreController.getInstance().getAvnNextLTProDemiTypeface();
        setTypeface(face);
    }

}
