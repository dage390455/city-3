package com.sensoro.smartcity.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;

import com.sensoro.smartcity.R;

import java.io.File;
import java.io.FileOutputStream;

public class WaterImageTools {
    static int compressH;
    static int compressW;

    public static String createWaterMaskImage(Context context, String filePath) {

        calcuateCompressSize(filePath);
        //将要上传的图片转化为bitmap并压缩
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        final Bitmap comBit = Bitmap.createScaledBitmap(bitmap, compressW, compressH, true);

        //将水印压缩成和上传图片一样大小
        Bitmap watermark = BitmapFactory.decodeResource(context.getResources(), R.drawable.arrows_left);
        final Bitmap comWatermark = Bitmap.createScaledBitmap(watermark, compressW, compressH, true);

        if (comBit == null) {
            return null;
        }

        int w = comBit.getWidth();
        int h = comBit.getHeight();

        final Bitmap newb = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Canvas cv = new Canvas(newb);
                cv.drawBitmap(comBit, 0, 0, null);// 在 0，0坐标开始画入src
                cv.drawBitmap(comWatermark, 0, 0, null);// 在0，0坐标画入水印，可根据需求自行设置
                cv.save();// 保存
                cv.restore();// 存储

            }
        });
        thread.start();

        //将添加水印的bitmap转化为file并获取文件路径
        return saveMyBitmap(newb).getAbsolutePath();
    }

    public static File saveMyBitmap(Bitmap mBitmap) {
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File file = null;
        try {
            //TODO
//            file = File.createTempFile(UploadAccess.generateFileName(),  /* prefix */
//                    ".jpg",         /* suffix */
//                    storageDir      /* directory */
//            );
            file = File.createTempFile("",  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            FileOutputStream out = new FileOutputStream(file);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 20, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    //计算图片压缩尺寸，按宽高比进行压缩
    private static void calcuateCompressSize(String filePath) {
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        float h = bitmap.getHeight();
        float w = bitmap.getWidth();
        float expactH = 1920;
        float expactW = 1080;
        float ratioH;
        float ratioW;


        if (h < expactH && w < expactW) {
            compressH = (int) h;
            compressW = (int) w;
        } else {
            ratioH = h / expactH;
            ratioW = w / expactW;
            if (ratioH > ratioW) {
                compressH = (int) expactH;
                compressW = (int) (w / ratioH);
            } else {
                compressW = (int) expactW;
                compressH = (int) (h / ratioW);
            }

        }
    }
}
