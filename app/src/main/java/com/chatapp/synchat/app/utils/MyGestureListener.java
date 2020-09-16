package com.chatapp.synchat.app.utils;

import android.content.Context;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * created by  Adhash Team on 3/28/2018.
 */

public class MyGestureListener {
    private static final String TAG = "MyGestureListener";
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    public interface GestureListener{
    void swipeLeft();
    void swipeRight();
    void click();
    void unClick();
    }

    public static  void setGestureListener(Context context, MotionEvent motionEvent,final GestureListener gestureListener){

        GestureDetector gd = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){


            //here is the method for double tap



            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    Log.d(TAG, "onFling: ");
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                if(gestureListener!=null)
                                    gestureListener.swipeRight();
                                result = true;
                            } else {
                                if(gestureListener!=null)
                                    gestureListener.swipeLeft();

                                result=true;
                            }
                        }
                    }
                } catch (Exception exception) {
                    Log.e(TAG, "onFling: ",exception );
                }
                return result;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                switch (e.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                    if(gestureListener!=null)
                        gestureListener.click();
                        return true;
                    case MotionEvent.ACTION_UP:
                        if(gestureListener!=null)
                            gestureListener.unClick();
                        return true;

                }
                return false;

            }
        });

        gd.onTouchEvent(motionEvent);
    }
}
