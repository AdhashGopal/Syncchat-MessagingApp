package com.chatapp.synchat.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.chatapp.synchat.core.CoreController;

/**
 * created by  Adhash Team on 2/17/2017.
 */
public class AvnNextLTProRegEditText extends EditText {

    public AvnNextLTProRegEditText(Context context) {
        super(context);
        init();
    }

    public AvnNextLTProRegEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvnNextLTProRegEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        setTypeface(face);
    }


}
