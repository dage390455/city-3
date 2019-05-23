package com.sensoro.smartcity.widget.popup;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sensoro.smartcity.util.AppUtils;

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

    public void fixHeight() {
        if (null == mContentView) {
            return;
        }

        View parent = (View) mContentView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        mContentView.measure(0, 0);

        int androiodScreenHeight = AppUtils.getAndroiodScreenHeight(mActivity);
        if (androiodScreenHeight != -1) {
            behavior.setPeekHeight((int) (androiodScreenHeight*0.92));
        }else{
            behavior.setPeekHeight(mContentView.getMeasuredHeight());
        }

        mActivity.getWindow();

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(params);
    }
}
