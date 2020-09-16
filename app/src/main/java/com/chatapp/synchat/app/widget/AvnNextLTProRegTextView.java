package com.chatapp.synchat.app.widget;

import android.content.Context;
import android.util.AttributeSet;

/**
 * created by  Adhash Team on 2/17/2017.
 */
public class AvnNextLTProRegTextView extends CustomEmojiTextView {

    public AvnNextLTProRegTextView(Context context) {
        super(context);
        init();
    }

    public AvnNextLTProRegTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvnNextLTProRegTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        /*Typeface face = CoreController.getInstance().getAvnNextLTProRegularTypeface();
        setTypeface(face);*/
        //setUseSystemDefault(false);
    }
}
