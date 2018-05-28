package com.sensoro.smartcity.widget;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;

import com.sensoro.smartcity.R;

/**
 * Created by sensoro on 17/11/13.
 */

public class ClearableTextView extends android.support.v7.widget.AppCompatTextView {

    public interface OnTextClearListener {
        public void onTextClear(ClearableTextView v);
    }

    private Context mContext;
    private Drawable mDrawableRight;
    private Rect mBounds;

    private OnTextClearListener mOnTextClearListener;

    public ClearableTextView(Context context) {
        super(context);
        initialize(context);
    }

    public ClearableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context context) {
        mContext = context;
        mDrawableRight = mContext.getResources().getDrawable(R.mipmap.ic_text_del);
        mDrawableRight.setBounds(0, 0, mDrawableRight.getMinimumWidth(), mDrawableRight.getMinimumWidth());
        setClickable(true);
        setMinWidth(120);
        setGravity(Gravity.CENTER_VERTICAL);
        setPadding(8, 8, 8, 8);
        setCompoundDrawablePadding(8);
    }

    @Override
    public void setCompoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (right != null) {
            mDrawableRight = right;
        }
        super.setCompoundDrawables(left, top, mDrawableRight, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_UP && mDrawableRight != null) {

            mBounds = mDrawableRight.getBounds();

            final int x = (int) event.getX();
            final int y = (int) event.getY();

            if (x >= (this.getWidth() - mBounds.width()) && x <= (this.getWidth() - this.getPaddingRight())
                    && y >= this.getPaddingTop() && y <= (this.getHeight() - this.getPaddingBottom())) {
                clear();
                event.setAction(MotionEvent.ACTION_CANCEL);
            }
        }

        return super.onTouchEvent(event);
    }

    public void setTextClearable(CharSequence text) {
        setText(text);
        if (text == null || text.length() == 0) {
            super.setCompoundDrawables(null, null, null, null);
        } else {
            super.setCompoundDrawables(null, null, mDrawableRight, null);
        }
    }

    private void clear() {
        if (mOnTextClearListener != null) {
            mOnTextClearListener.onTextClear(this);
            setTextClearable("");
        }
        super.setCompoundDrawables(null, null, null, null);
    }

    public void setOnTextClearListener(OnTextClearListener onTextClearListener) {
        mOnTextClearListener = onTextClearListener;
    }

    @Override
    public void finalize() throws Throwable {
        mDrawableRight = null;
        mBounds = null;
        super.finalize();
    }
}
