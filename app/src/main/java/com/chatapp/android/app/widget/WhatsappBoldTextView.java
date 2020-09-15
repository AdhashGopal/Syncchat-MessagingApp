package com.chatapp.android.app.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.chatapp.android.core.CoreController;


/**
 * created by  Adhash Team on 2/17/2017.
 */
public class WhatsappBoldTextView extends AppCompatTextView {

    public WhatsappBoldTextView(Context context) {
        super(context);
        init();
    }

    public WhatsappBoldTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WhatsappBoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Typeface face = CoreController.getInstance().getWhatsappBoldTypeFace();
        setTypeface(face);
    }
}
