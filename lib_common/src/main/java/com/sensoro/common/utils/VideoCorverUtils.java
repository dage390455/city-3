package com.sensoro.common.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import com.sensoro.common.imagepicker.util.BitmapUtil;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: jack
 * 时  间: 2019-09-24
 * 包  名: com.sensoro.common.utils
 * 简  述: <功能简述,热成像视频或者直播封面图处理工具类>
 */
public class VideoCorverUtils {

    private   final Map<String, WeakReference<Bitmap>> bitmapMap = new HashMap<>();
    private   int screenWidth;

    public VideoCorverUtils(Context mContext){
         this.screenWidth = ScreenUtils.getScreenWidth(mContext);
    }

    public    Bitmap  getCorverBitmap(String  originUrl,Bitmap  originBitmap ,boolean originRecycled ){
        if (bitmapMap.containsKey(originUrl) && bitmapMap.get(originUrl).get() != null) {
            originBitmap = bitmapMap.get(originUrl).get();
        } else if (originBitmap != null && originBitmap.getHeight() > 0 && originBitmap.getWidth() > 0) {
            float rate = originBitmap.getWidth() * 1.0f / originBitmap.getHeight();
            if (rate < 16.0f / 9) {//说明要按照短边拉伸，横向无法充满
                float targetRate = screenWidth * 1.0f / 16 * 9 / originBitmap.getHeight();
                originBitmap = BitmapUtil.expandBitmapFull(BitmapUtil.scaleBitmap(originBitmap, targetRate,originRecycled), screenWidth);
                bitmapMap.put(originUrl, new WeakReference<>(originBitmap));
            }
        }
        return originBitmap;

    }

    public    void onDestory(){
        Bitmap mBitmap = null;
        for (String key : bitmapMap.keySet()) {
            mBitmap = bitmapMap.get(key).get();
            if (mBitmap != null) {
                mBitmap.recycle();
            }
            bitmapMap.get(key).clear();
        }
        bitmapMap.clear();
    }
}
