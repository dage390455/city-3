package com.sensoro.smartcity.widget;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by sensoro on 17/8/11.
 */

public class SensoroImageView extends View {
    private Bitmap mBitmap;
    private static final int BITMAP_WIDTH = 2000;
    private int display_width = 0;
    private int display_height = 0;
    private int offset = 0;
    private boolean isTurn = false;
    private boolean isTurnLeft = false;
    public SensoroImageView(Context context) {
        super(context);
        init();
    }

    public SensoroImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SensoroImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mBitmap = getImageFromAssetsFile("ic_login_bg.jpg");
        WindowManager wm = (WindowManager) getContext()
                .getSystemService(Context.WINDOW_SERVICE);
        display_width = wm.getDefaultDisplay().getWidth();
        display_height = wm.getDefaultDisplay().getHeight();
        offset = BITMAP_WIDTH - display_width;
//        isTurnLeft = false;
//        startScaleAnimation();
//        isTurnLeft = true;
//        isTurn = true;
    }

    public void startScaleAnimation() {
        ScaleAnimation scaleAnimation =new ScaleAnimation(2.0f, 1.0f, 2.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        scaleAnimation.setDuration(3000);                     //执行时间
        scaleAnimation.setRepeatCount(0);                   //重复执行动画
        scaleAnimation.setRepeatMode(Animation.REVERSE);     //重复 缩小和放大效果
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                isTurnLeft = true;
                isTurn = true;

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.setAnimation(scaleAnimation);
        scaleAnimation.start();

    }


    private Bitmap getImageFromAssetsFile(String fileName)
    {
        Bitmap image = null;
        AssetManager am = getResources().getAssets();
        try
        {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return image;

    }

    private int offset_x = 0;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawImage(canvas, mBitmap, 0, 0, display_width, display_height , offset_x, 0);
        if (isTurn) {
            if (isTurnLeft) {
                if (offset_x >= offset) {
                    isTurnLeft = false;
                } else {
                    offset_x += 1;
                }
            } else {
                if (offset_x < 1) {
                    isTurnLeft = true;
                } else {
                    offset_x -= 1;
                }
            }
        } else {
            offset_x = offset / 2;
        }

        invalidate();
    }


    public static void drawImage(Canvas canvas, Bitmap blt, int x, int y, int w, int h, int bx, int by)
    {                                                        //x,y表示绘画的起点，
        Rect src = new Rect();// 图片
        Rect dst = new Rect();// 屏幕位置及尺寸
        //src 这个是表示绘画图片的大小
        src.left = bx;   //0,0
        src.top = by;
        src.right = bx + w;// mBitDestTop.getWidth();,这个是桌面图的宽度，
        src.bottom = by + h;//mBitDestTop.getHeight()/2;// 这个是桌面图的高度的一半
        // 下面的 dst 是表示 绘画这个图片的位置
        dst.left = x;    //miDTX,//这个是可以改变的，也就是绘图的起点X位置
        dst.top = y;    //mBitQQ.getHeight();//这个是QQ图片的高度。 也就相当于 桌面图片绘画起点的Y坐标
        dst.right = x + w;    //miDTX + mBitDestTop.getWidth();// 表示需绘画的图片的右上角
        dst.bottom = y + h;    // mBitQQ.getHeight() + mBitDestTop.getHeight();//表示需绘画的图片的右下角
        canvas.drawBitmap(blt, src, dst, null);//这个方法  第一个参数是图片原来的大小，第二个参数是 绘画该图片需显示多少。也就是说你想绘画该图片的某一些地方，而不是全部图片，第三个参数表示该图片绘画的位置

        src = null;
        dst = null;
    }

    /**
     * 绘制一个Bitmap
     *
     * @param canvas 画布
     * @param bitmap 图片
     * @param x 屏幕上的x坐标
     * @param y 屏幕上的y坐标
     */

    public static void drawImage(Canvas canvas, Bitmap bitmap, int x, int y) {
        // 绘制图像 将bitmap对象显示在坐标 x,y上
        canvas.drawBitmap(bitmap, x, y, null);
    }
}
