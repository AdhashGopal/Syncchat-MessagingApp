package com.chatapp.synchat.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.chatapp.synchat.core.CoreController;


/**
 * created by  Adhash Team on 2/17/2017.
 */
public class AvnNextLTProRegTextViewLight extends AppCompatTextView {

    public AvnNextLTProRegTextViewLight(Context context) {
        super(context);
        init();
    }

    public AvnNextLTProRegTextViewLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvnNextLTProRegTextViewLight(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        setTypeface(face);
    }
}
