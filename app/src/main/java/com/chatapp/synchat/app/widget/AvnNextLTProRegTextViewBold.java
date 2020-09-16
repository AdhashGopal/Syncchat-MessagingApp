package com.chatapp.synchat.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.chatapp.synchat.core.CoreController;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * created by  Adhash Team on 3/1/2018.
 */
public class AvnNextLTProRegTextViewBold extends EmojiconTextView {

    public AvnNextLTProRegTextViewBold(Context context) {
        super(context);
        init();
    }

    public AvnNextLTProRegTextViewBold(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AvnNextLTProRegTextViewBold(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface face = CoreController.getInstance().getAvnNextLTProBoldTypeface();
        setTypeface(face);
        setUseSystemDefault(false);
    }
}
