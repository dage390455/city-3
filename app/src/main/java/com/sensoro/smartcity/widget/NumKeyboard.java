package com.sensoro.smartcity.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.util.AttributeSet;
import android.util.Log;

import com.sensoro.smartcity.R;

import java.util.List;

public class NumKeyboard extends KeyboardView implements KeyboardView.OnKeyboardActionListener{
    private Context mContext;
    //用于区分左下角空白按键,(要与xml里设置的数值相同)
    private int KEYCODE_EMPTY=-10;
    //删除按键背景图片
    private Drawable mDeleteDrawable;
    //最下面两个灰色的按键（空白按键跟删除按键）
    private int mBgColor;
    private Drawable mdecimalDrawable;


    public NumKeyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public NumKeyboard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);

    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mContext = context;
        TypedArray ta=context.obtainStyledAttributes(attrs, R.styleable.NumKeyView);
        mBgColor=ta.getColor(R.styleable.NumKeyView_gbColor, Color.RED);
        mDeleteDrawable=ta.getDrawable(R.styleable.NumKeyView_deleteDrawable);
        mdecimalDrawable = ta.getDrawable(R.styleable.NumKeyView_decimalDrawable);
        ta.recycle();
        //获取xml中的按键布局
        Keyboard keyboard=new Keyboard(context,R.xml.keyboardview);

        setKeyboard(keyboard);
        setEnabled(true);
        setPreviewEnabled(false);
        setOnKeyboardActionListener(this);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        List<Keyboard.Key> keys=getKeyboard().getKeys();
        for (Keyboard.Key key:keys){
            //绘制空白键
            /*if (key.codes[0]==KEYCODE_EMPTY){
                drawKeyBackGround(key,canvas);
                drawkeyDelete(key,canvas,mdecimalDrawable,20);

            }else*/ if (key.codes[0]==Keyboard.KEYCODE_DELETE){
                //绘制删除键背景
//                drawKeyBackGround(key,canvas);
                //绘制按键图片
                drawkeyDelete(key,canvas,mDeleteDrawable,4);
            }
        }
    }
    private void drawKeyBackGround(Keyboard.Key key, Canvas canvas) {
        ColorDrawable colordrawable=new ColorDrawable(mBgColor);
        colordrawable.setBounds(key.x,key.y,key.x+key.width,key.y+key.height);
        colordrawable.draw(canvas);
    }
    private void drawkeyDelete(Keyboard.Key key, Canvas canvas,Drawable drawable,int n) {
        Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.keyboard_delete);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = (key.width - width) / 2 + key.x;
        int top = (key.height - height) / 2 + key.y;
        canvas.drawBitmap(bitmap,left,top,new Paint(Paint.FILTER_BITMAP_FLAG));
    }
    //回调接口
    public interface OnKeyPressListener{
        //添加数据回调
        void onInertKey(String text);
        //删除数据回调
        void onDeleteKey();

        void onClearKey(String s);
    }
    private OnKeyPressListener mOnkeyPressListener;
    public void setOnKeyPressListener(OnKeyPressListener li){
        mOnkeyPressListener=li;
    }
    @Override
    public void onKey(int i, int[] ints) {
        if (i== Keyboard.KEYCODE_DELETE&&mOnkeyPressListener!=null){
            //添加数据回调
            mOnkeyPressListener.onDeleteKey();
        }else if (i!=KEYCODE_EMPTY){
            //删除数据回调
            mOnkeyPressListener.onInertKey(Character.toString((char) i));
        }else if(i==KEYCODE_EMPTY){
            mOnkeyPressListener.onClearKey("清除");
        }
    }

    public int dp2px(int dp){
         float scale = mContext.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    @Override
    public void onPress(int i) {

    }

    @Override
    public void onRelease(int i) {

    }



    @Override
    public void onText(CharSequence charSequence) {

    }

    @Override
    public void swipeRight() {
        super.swipeRight();
    }

    @Override
    public void swipeDown() {
        super.swipeDown();
    }

    @Override
    public void swipeLeft() {
        super.swipeLeft();
    }

    @Override
    public void swipeUp() {
        super.swipeUp();
    }
}
