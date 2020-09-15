package com.chatapp.android.app.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.chatapp.android.R;


/**
 * created by  Adhash Team  on 4/2/2018.
 */

public class WhatsappStatusCircle extends View {

    private static final int STROKE_WIDTH = 6;
    private Paint  mDegreesPaint,mDegreePaintGray,mDegreePaintWhite;
    private RectF mRect;
    private int total=0, viewed=0;
    private static final String TAG = "WhatsappStatus";

    public WhatsappStatusCircle(Context context) {
        super(context);
        init();

    }

    public WhatsappStatusCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();

    }

    public WhatsappStatusCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        mDegreesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDegreesPaint.setStyle(Paint.Style.STROKE);
        mDegreesPaint.setStrokeWidth(STROKE_WIDTH);
        mDegreesPaint.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));

        mDegreePaintGray = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDegreePaintGray.setStyle(Paint.Style.STROKE);
        mDegreePaintGray.setStrokeWidth(STROKE_WIDTH);
        mDegreePaintGray.setColor(ContextCompat.getColor(getContext(), R.color.gray));


        mDegreePaintWhite = new Paint(Paint.ANTI_ALIAS_FLAG);
        mDegreePaintWhite.setStyle(Paint.Style.STROKE);
        mDegreePaintWhite.setStrokeWidth(STROKE_WIDTH);
        mDegreePaintWhite.setColor(ContextCompat.getColor(getContext(), R.color.white));

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // getHeight() is not reliable, use getMeasuredHeight() on first run:
        // Note: mRect will also be null after a configuration change,
        // so in this case the new measured height and width values will be used:
        if (mRect == null)
        {
            // take the minimum of width and height here to be on he safe side:
            int centerX = getMeasuredWidth() / 2;
            int centerY = getMeasuredHeight() / 2;
            int radius = Math.min(centerX, centerY);

            // mRect will define the drawing space for drawArc()
            // We have to take into account the STROKE_WIDTH with drawArc() as well as drawCircle():
            // circles as well as arcs are drawn 50% outside of the bounds defined by the radius (radius for arcs is calculated from the rectangle mRect).
            // So if mRect is too large, the lines will not fit into the View
            int startTop = STROKE_WIDTH / 2;
            int startLeft = startTop;

            int endBottom = 2 * radius - startTop;
            int endRight = endBottom;

            mRect = new RectF(startTop, startLeft, endRight, endBottom);
        }



    drawDynamicCircle(canvas,total,viewed);

    }

    private void drawDynamicCircle(Canvas canvas,int total, int viewed) {

        if (total<=0){
            return;
        }
        if(canvas==null)
            return;

        float currentAngle=0;
        float spacePercentage = 0.17f;
        if(total<=1){
            spacePercentage=0f;
        }
        else if(total<=5){
            spacePercentage=0.08f;
        }
        final float spacePercent= (float) ((360/total)*spacePercentage);
        float totalSpacePercent=spacePercent * total;
        float parts=(360-totalSpacePercent)/total;

        for(int i=0;i<total;i++) {

            canvas.drawArc(mRect,currentAngle, spacePercent, false, mDegreePaintWhite);
            currentAngle=currentAngle+spacePercent;

            if(i<viewed)
                canvas.drawArc(mRect, currentAngle, parts, false, mDegreePaintGray);
            else
                canvas.drawArc(mRect, currentAngle, parts, false, mDegreesPaint);

            currentAngle=currentAngle+parts;

        }
    }


    public void setViewed(int viewed) {
        this.viewed = viewed;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
