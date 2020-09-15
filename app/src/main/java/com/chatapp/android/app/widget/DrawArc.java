


package com.chatapp.android.app.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.chatapp.android.R;

/**
 * created by  Adhash Team on 5/19/2017.
 */
public class DrawArc extends View {


    public DrawArc(Context context) {
        super(context);
    }

    public DrawArc(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DrawArc(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // TODO Auto-generated method stub
        super.onDraw(canvas);
        float width = (float) getWidth();
        float height = (float) getHeight();
        float radius;

        if (width > height) {
            radius = height / 2.5f;
        } else {
            radius = width / 2.5f;
        }

        Path path = new Path();
        path.addCircle(width / 2,
                height / 2, radius,
                Path.Direction.CW);

        Paint paint = new Paint();
        paint.setColor(ContextCompat.getColor(getContext(), R.color.draw));
        paint.setStrokeWidth(40);
        paint.setStyle(Paint.Style.FILL);

        float center_x, center_y;
        final RectF oval = new RectF();
        paint.setStyle(Paint.Style.STROKE);

        center_x = width / 2;
        center_y = height / 2;

        oval.set(center_x - radius,
                center_y - radius,
                center_x + radius,
                center_y + radius);
        canvas.drawArc(oval, 180, 180, false, paint);
    }
}