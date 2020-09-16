package com.chatapp.synchat.app.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.chatapp.synchat.app.utils.AppUtils;

import hani.momanii.supernova_emoji_library.Helper.EmojiconHandler;

/**
 * created by  Adhash Team on 6/5/2018.
 */

public class CustomEmojiTextView extends AppCompatTextView {
    private static final String TAG = "CustomEmojiTextView";

    private int mEmojiconSize;
    private int mEmojiconAlignment;
    private int mEmojiconTextSize;
    private int mTextStart = 0;
    private int mTextLength = -1;
    private boolean mUseSystemDefault = false;

    public CustomEmojiTextView(Context context) {
        super(context);
        this.init((AttributeSet) null);
    }

    public CustomEmojiTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(attrs);
    }

    public CustomEmojiTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(attrs);
    }

    private void init(AttributeSet attrs) {
        this.mEmojiconTextSize = (int) this.getTextSize();
        if (attrs == null) {
            this.mEmojiconSize = (int) this.getTextSize();
        } else {
            TypedArray a = this.getContext().obtainStyledAttributes(attrs, hani.momanii.supernova_emoji_library.R.styleable.Emojicon);
            this.mEmojiconSize = (int) a.getDimension(hani.momanii.supernova_emoji_library.R.styleable.Emojicon_emojiconSize, this.getTextSize());
            this.mEmojiconAlignment = a.getInt(hani.momanii.supernova_emoji_library.R.styleable.Emojicon_emojiconAlignment, 0);
            this.mTextStart = a.getInteger(hani.momanii.supernova_emoji_library.R.styleable.Emojicon_emojiconTextStart, 0);
            this.mTextLength = a.getInteger(hani.momanii.supernova_emoji_library.R.styleable.Emojicon_emojiconTextLength, -1);
            this.mUseSystemDefault = a.getBoolean(hani.momanii.supernova_emoji_library.R.styleable.Emojicon_emojiconUseSystemDefault, this.mUseSystemDefault);
            a.recycle();
        }

        this.setText(this.getText());
    }


    @Override
    public void setText(CharSequence text, BufferType type) {
        if (text != null && !TextUtils.isEmpty((CharSequence) text) && AppUtils.isEmoji(text.toString())) {
            //Log.d(TAG, "is Emoji: ");
            SpannableStringBuilder builder = new SpannableStringBuilder((CharSequence) text);
            EmojiconHandler.addEmojis(this.getContext(), builder, this.mEmojiconSize, this.mEmojiconAlignment, this.mEmojiconTextSize, this.mTextStart, this.mTextLength, this.mUseSystemDefault);
            text = builder;
            super.setText((CharSequence) text, type);
        } else {
            //Log.d(TAG, "setText: not emoji");
            super.setText(text, type);
        }
    }
}
