package com.sensoro.smartcity.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.gyf.barlibrary.ImmersionBar;

public class FixHeightBottomSheetDialog extends BottomSheetDialog {

    private Activity mActivity;
    private View mContentView;

    public FixHeightBottomSheetDialog(@NonNull Context context) {
        super(context);
        mActivity = (Activity) context;
    }

    public FixHeightBottomSheetDialog(@NonNull Context context, int theme) {
        super(context, theme);
        mActivity = (Activity) context;
    }

    @Override
    protected void onStart() {
        super.onStart();
        fixHeight();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        this.mContentView = view;
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        this.mContentView = view;
    }

    private void fixHeight() {
        if (null == mContentView) {
            return;
        }

        View parent = (View) mContentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        mContentView.measure(0, 0);
        behavior.setPeekHeight(mContentView.getMeasuredHeight());

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(params);
    }
}
