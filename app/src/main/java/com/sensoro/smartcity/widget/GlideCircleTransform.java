package com.sensoro.smartcity.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapResource;


/**
 * 圆角图片
 */

public class GlideCircleTransform implements Transformation<Bitmap> {
    private  int radius = -1;

//    private final float radius;

//    public GlideCircleTransform(Context context) {
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

    public GlideCircleTransform(Context context) {
        this(Glide.get(context).getBitmapPool());
    }

    public GlideCircleTransform(Context context, int radius) {
        this(Glide.get(context).getBitmapPool());
        this.radius = radius;
    }

    public GlideCircleTransform(BitmapPool pool) {
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
            int width = source.getWidth();
            int height = source.getHeight();

            float scaleWidth = (float)size / width;
            float scaleHeight = (float)size / height;

            float min = Math.max(scaleHeight, scaleWidth);
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth,scaleHeight);
            source = Bitmap.createBitmap(source,0,0,width,height,matrix,true);
////            BitmapFactory.
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inJustDecodeBounds = true;
//            ByteBuffer allocate = ByteBuffer.allocate(source.getByteCount());
//            byte[] array = allocate.array();
////            BitmapFactory.decodeByteArray(array,0,array.length,options);
//
//            options.inSampleSize = calculateInSampleSize(options,size,size);
//            options.inJustDecodeBounds = false;
//            source = BitmapFactory.decodeByteArray(array, 0, array.length, options);


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

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
    @Override
    public String getId() {
        return getClass().getName() + "";
    }
}
