package com.sensoro.smartcity.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.sensoro.smartcity.util.AppUtils;


/**
 * 圆角图片
 */

public class GlideRoundTransform extends BitmapTransformation {

//    private final float radius;

    public GlideRoundTransform(Context context) {
        super(context);
//        radius = AppUtils.dp2px(context,1000);
    }

    @Override
    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return roundCrop(pool, toTransform);
    }

    private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();
        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        paint.setAntiAlias(true);
        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getWidth());
//        canvas.drawRoundRect(rectF, radius, radius, paint);
//        canvas.drawOval(rectF,paint);
        int cx = source.getWidth() / 2;
        canvas.drawCircle(cx,cx,cx,paint);
        return result;

    }

    @Override
    public String getId() {
        return getClass().getName() + "";
    }
}
