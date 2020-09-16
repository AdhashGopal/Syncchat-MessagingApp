package com.chatapp.synchat.app.adapter;

/**
 *
 */

import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;


public class RItemAdapter implements RecyclerView.OnItemTouchListener {

    public boolean status;
    private OnItemClickListener mListener;
    private GestureDetector mGestureDetector;
    private boolean isTouchable;

    /**
     * OnItemClickListener interface
     */
    public interface OnItemClickListener {
        void onItemClick(View view, int position);

        void onItemLongClick(View view, int position);
    }


    /**
     * Recyclerview ontouch event
     * @param context The activity object inherits the Context object
     * @param recyclerView get child view
     * @param listener click event
     */
    public RItemAdapter(Context context, final RecyclerView recyclerView, OnItemClickListener listener) {
        mListener = listener;

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (childView != null && mListener != null) {
                    mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }
        });
    }


    /**
     * Recyclerview ontouch event
     * @param context The activity object inherits the Context object
     * @param recyclerView get child view
     * @param getStatus  boolean value
     * @param listener  click event
     */
    public RItemAdapter(Context context, final RecyclerView recyclerView, boolean getStatus, OnItemClickListener listener) {
        mListener = listener;
        status = getStatus;
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }

            @Override
            public void onLongPress(MotionEvent e) {
                View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                if (childView != null && mListener != null) {
                    mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                }
            }
        });
    }

    public boolean isTouchable() {
        return isTouchable;
    }

    public void setTouchable(boolean isTouchable) {
        this.isTouchable = isTouchable;
    }


    /**
     * getStatus of recyclerview
     * @param getStatus boolean value
     */
    public RItemAdapter(boolean getStatus) {
        status = getStatus;
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        View childView = view.findChildViewUnder(e.getX(), e.getY());

        if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
            mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
        }

//        return status;
//        if (isTouchable)
        return isTouchable; // Block touch event
//        else
//            return false; //  Enable touch event

    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}