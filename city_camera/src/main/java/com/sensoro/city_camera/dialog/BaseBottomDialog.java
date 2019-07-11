package com.sensoro.city_camera.dialog;

import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.sensoro.common.utils.AppUtils;

/**
 * @author : bin.tian
 * date   : 2019-06-24
 */
public abstract class BaseBottomDialog extends BottomSheetDialogFragment {
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback
            = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            //禁止拖拽，
            if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                //设置为收缩状态
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        fixHeight();

        final View view = getView();
        view.post(() -> {
            View parent = (View) view.getParent();
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
            CoordinatorLayout.Behavior behavior = params.getBehavior();
            mBottomSheetBehavior = (BottomSheetBehavior) behavior;
            mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
        });

        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                onBackPressed();
                return true;
            }
            return false;
        });
    }

    private void fixHeight() {
        final View view = getView();
        if (null == view || null == getContext()) {
            return;
        }

        View parent = (View) view.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        view.measure(0, 0);
        int screenHeight = AppUtils.getAndroiodScreenHeight(getContext());
        if (screenHeight != -1) {
            int fixHeight = (int)(screenHeight * 0.92);
            if(view.getMeasuredHeight() > fixHeight){
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                behavior.setPeekHeight(fixHeight);
            }
        } else {
            behavior.setPeekHeight(view.getMeasuredHeight());
        }

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(params);
    }

    /**
     * 返回键处理
     */
    protected abstract void onBackPressed();
}
