package com.chatapp.synchat.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.chatapp.synchat.core.CoreController;


/**
 * created by  Adhash Team on 2/17/2017.
 */
public class WhatsappRegularTextView extends AppCompatTextView {

    public WhatsappRegularTextView(Context context) {
        super(context);
        init();
    }

    public WhatsappRegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WhatsappRegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface face = CoreController.getInstance().getWhatsappRegularTypeFace();
        setTypeface(face);
    }
}
