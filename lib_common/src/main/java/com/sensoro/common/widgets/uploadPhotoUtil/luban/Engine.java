package com.sensoro.common.widgets.uploadPhotoUtil.luban;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import com.sensoro.common.R;
import com.sensoro.common.base.ContextUtils;
import com.sensoro.common.imagepicker.util.BitmapUtil;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.DpUtils;
import com.sensoro.common.widgets.uploadPhotoUtil.ImageUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Responsible for starting compress and managing active and cached resources.
 */
class Engine {
    private InputStreamProvider srcImg;
    private File tagImg;
    private int srcWidth;
    private int srcHeight;
    private boolean focusAlpha;

    Engine(InputStreamProvider srcImg, File tagImg, boolean focusAlpha) throws IOException {
        this.tagImg = tagImg;
        this.srcImg = srcImg;
        this.focusAlpha = focusAlpha;

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;

        BitmapFactory.decodeStream(srcImg.open(), null, options);
        this.srcWidth = options.outWidth;
        this.srcHeight = options.outHeight;
    }

    private int computeSize() {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    private Bitmap rotatingImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    File compress() throws Exception {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize();

        Bitmap tagBitmap = BitmapFactory.decodeStream(srcImg.open(), null, options);
        int mBitmapDegree=BitmapUtil.getBitmapDegree(srcImg.getPath());
        if(mBitmapDegree>0){
            Log.d("mBitmapDegree",mBitmapDegree+"");
            Log.d("mBitmapDegree","tagBitmap.width="+tagBitmap.getWidth());
            tagBitmap=BitmapUtil.rotateBitmapByDegree(tagBitmap,mBitmapDegree);
            Log.d("mBitmapDegree","tagBitmap.width="+tagBitmap.getWidth());
        }

        //TODO 按像素比例绘制
        int srcWidth = tagBitmap.getWidth();
        int srcHeight = tagBitmap.getHeight();
         int picPaddingBottom = (int) (50 * srcHeight / 667f + 0.5f);
         int textPaddingBottom = (int) (25 * srcHeight / 667f + 0.5f);
         int picPaddingRight = (int) (20 * srcWidth / 375f + 0.5f);
         int textPaddingRight = (int) (20 * srcWidth / 375f + 0.5f);
        Bitmap markBitmap = BitmapFactory.decodeResource(ContextUtils.getContext().getResources(), R.drawable.photo_mark);

        int tagFontSize= DpUtils.dp2px(ContextUtils.getContext(),21);
        try {
            float  rate=(srcWidth/5.0f)/markBitmap.getWidth();
            markBitmap= BitmapUtil.scaleBitmap(markBitmap,rate);
            tagFontSize= (int) (srcWidth/40.0f);

            picPaddingBottom= textPaddingBottom+tagFontSize+markBitmap.getHeight()/5;
        }catch (Exception e){
            e.printStackTrace();
        }

        tagBitmap = ImageUtil.createWaterMaskRightBottom(ContextUtils.getContext(), tagBitmap, markBitmap, picPaddingRight, picPaddingBottom);

        tagBitmap = ImageUtil.drawTextToRightBottom(ContextUtils.getContext(), tagBitmap, DateUtil.getStrTime_ymd(System.currentTimeMillis()),
                tagFontSize, ContextUtils.getContext().getResources().getColor(R.color.dcdffffff), textPaddingRight, textPaddingBottom);

        if(mBitmapDegree>0) {
            tagBitmap = BitmapUtil.rotateBitmapByDegree(tagBitmap, -mBitmapDegree);
        }

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        if (Checker.SINGLE.isJPG(srcImg.open())) {
            tagBitmap = rotatingImage(tagBitmap, Checker.SINGLE.getOrientation(srcImg.open()));
        }
        tagBitmap.compress(focusAlpha ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG, 60, stream);
        tagBitmap.recycle();
        markBitmap.recycle();
        FileOutputStream fos = new FileOutputStream(tagImg);
        fos.write(stream.toByteArray());
        fos.flush();
        fos.close();
        stream.close();

        return tagImg;
    }
}