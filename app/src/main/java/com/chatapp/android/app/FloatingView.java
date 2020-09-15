package com.chatapp.android.app;

/**
 *
 */

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;

import androidx.core.content.ContextCompat;

import com.chatapp.android.R;


public class FloatingView {

    private static PopupWindow popWindow;

    private FloatingView() {
    }

    /**
     * shown popup view
     * @param activity current activity
     * @param inflatedView layout inflatedView
     * @param include view binding
     */

    public static void onShowPopup(Activity activity, View inflatedView, View include) {

        // get device size
        Display display = activity.getWindowManager().getDefaultDisplay();
        final Point size = new Point();
        display.getSize(size);
        // fill the data to the list items
        // set height depends on the device size
        popWindow = new PopupWindow(inflatedView, size.x, ViewGroup.LayoutParams.WRAP_CONTENT,
                true);
        // set a background drawable with rounders corners
        popWindow.setBackgroundDrawable(ContextCompat.getDrawable(activity,
                R.drawable.comment_popup_bg));
        // make it focusable to show the keyboard to enter in EditText
        popWindow.setFocusable(true);
        // make it outside touchable to dismiss the popup window
        popWindow.setOutsideTouchable(true);

        popWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        // show the popup at bottom of the screen and set some margin at
        // bottom ie,

        //popWindow.showAtLocation(activity.getCurrentFocus(), Gravity.TOP, 0,
        //0);
        popWindow.showAsDropDown(include);

    }

    /**
     * Dismiss popup
     */
    public static void dismissWindow() {

        if (popWindow != null) {

            popWindow.dismiss();
        }
    }
}
