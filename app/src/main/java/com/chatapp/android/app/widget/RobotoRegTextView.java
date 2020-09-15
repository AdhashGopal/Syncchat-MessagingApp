package com.chatapp.android.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.chatapp.android.core.chatapphelperclass.ChatappFontUtils;

/**
 * created by  Adhash Team on 2/17/2017.
 */
public class RobotoRegTextView extends TextView {

    public RobotoRegTextView(Context context) {
        super(context);
        init();
    }

    public RobotoRegTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RobotoRegTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface face = Typeface.createFromAsset(getContext().getAssets(), ChatappFontUtils.getRobotoRegularName());
        setTypeface(face);
    }
}
