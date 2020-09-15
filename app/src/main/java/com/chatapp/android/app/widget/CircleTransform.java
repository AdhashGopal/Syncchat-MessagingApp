package com.chatapp.android.app.widget;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.DisplayMetrics;

import com.squareup.picasso.Transformation;

/**
 * Created by jegan on 5/23/2017.
 */

public class CircleTransform implements Transformation {
    public static int dpToPx(Context mContext, int dp) {
        DisplayMetrics displayMetrics = mContext.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        try {
            int minEdge = Math.min(source.getWidth(), source.getHeight());
            int dx = (source.getWidth() - minEdge) / 2;
            int dy = (source.getHeight() - minEdge) / 2;

            // Init shader
            Shader shader = new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            Matrix matrix = new Matrix();
            matrix.setTranslate(-dx, -dy);   // Move the target area to center of the source bitmap
            shader.setLocalMatrix(matrix);

            // Init paint
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setShader(shader);

            // Create and draw circle bitmap
            Bitmap output = Bitmap.createBitmap(minEdge, minEdge, source.getConfig());
            Canvas canvas = new Canvas(output);
            canvas.drawOval(new RectF(0, 0, minEdge, minEdge), paint);
            // Recycle the source bitmap, because we already generate a new one
            source.recycle();
            return output;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String key() {
        return "circle" + Math.random();
    }
}
