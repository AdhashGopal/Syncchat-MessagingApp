package com.chatapp.synchat.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.chatapp.synchat.core.CoreController;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * created by  Adhash Team on 2/17/2017.
 */
public class AvnNextLTProDemiTextView extends EmojiconTextView {

    public AvnNextLTProDemiTextView(Context context) {
        super(context);
        init();
    }

    public AvnNextLTProDemiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvnNextLTProDemiTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface face = CoreController.getInstance().getAvnNextLTProDemiTypeface();
        setTypeface(face);
        setUseSystemDefault(false);
    }
}
