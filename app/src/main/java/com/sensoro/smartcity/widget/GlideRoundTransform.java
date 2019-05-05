package com.sensoro.smartcity.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.sensoro.smartcity.util.AppUtils;


/**
 * 圆角图片
 */

public class GlideRoundTransform implements Transformation<Bitmap> {
    private  int radius = -1;

//    private final float radius;

//    public GlideRoundTransform(Context context) {
//        super(context);
////        radius = AppUtils.dp2px(context,1000);
//    }

//    @Override
//    protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
//        return roundCrop(pool, toTransform);
//    }
//
//    private Bitmap roundCrop(BitmapPool pool, Bitmap source) {
//        if (source == null) return null;
//
//        Bitmap result = pool.get(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
//        if (result == null) {
//            result = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
//        }
//
//        Canvas canvas = new Canvas(result);
//        Paint paint = new Paint();
//        paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
//        paint.setAntiAlias(true);
//        RectF rectF = new RectF(0f, 0f, source.getWidth(), source.getWidth());
////        canvas.drawRoundRect(rectF, radius, radius, paint);
//        canvas.drawOval(rectF,paint);
////        int cx = source.getWidth() / 2;
////        canvas.drawCircle(cx,cx,cx,paint);
//
//        return result;
//
//    }

    private BitmapPool mBitmapPool;

    public GlideRoundTransform(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public GlideRoundTransform(Context context,int radius) {
        this(Glide.get(context).getBitmapPool());
        this.radius = radius;
    }

    public GlideRoundTransform(BitmapPool pool) {
        this.mBitmapPool = pool;
    }
    @Override
    public Resource<Bitmap> transform(Resource<Bitmap> resource, int outWidth, int outHeight) {
        Bitmap source = resource.get();
        int size;
        if(radius == -1){
            size = Math.min(source.getWidth(), source.getHeight());
        }else{
            size = radius*2;
        }


        int width = (source.getWidth() - size) / 2;
        int height = (source.getHeight() - size) / 2;

        Bitmap bitmap = mBitmapPool.get(size, size, Bitmap.Config.ARGB_8888);
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader =
                new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        if (width != 0 || height != 0) {
            // source isn't square, move viewport to center
            Matrix matrix = new Matrix();
            matrix.setTranslate(-width, -height);
            shader.setLocalMatrix(matrix);
        }
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
//        if (radius == -1) {
//            canvas.drawCircle(r, r, r, paint);
//        }else{
//            canvas.drawCircle(r, radius, radius, paint);
//        }
        canvas.drawCircle(r, r, r, paint);


        return BitmapResource.obtain(bitmap, mBitmapPool);
    }

    @Override
    public String getId() {
        return getClass().getName() + "";
    }
}
