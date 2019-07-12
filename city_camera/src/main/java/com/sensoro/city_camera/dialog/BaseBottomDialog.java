package com.sensoro.city_camera.dialog;

import android.app.Dialog;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

/**
 * @author : bin.tian
 * date   : 2019-06-24
 */
public abstract class BaseBottomDialog extends BottomSheetDialogFragment {
    private boolean mCanSlideDismiss = true;
    private BottomSheetBehavior<View> mBottomSheetBehavior;
    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback
            = new BottomSheetBehavior.BottomSheetCallback() {

        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            //禁止拖拽
            if (newState == BottomSheetBehavior.STATE_DRAGGING  && !mCanSlideDismiss) {
                //设置为收缩状态
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }

            if (newState == BottomSheetBehavior.STATE_HIDDEN && mCanSlideDismiss) {
                dismiss();
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
        final View view = getView();
        if(view != null){
            view.post(() -> {
                View parent = (View) view.getParent();
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) (parent).getLayoutParams();
                CoordinatorLayout.Behavior behavior = params.getBehavior();
                mBottomSheetBehavior = (BottomSheetBehavior) behavior;
                mBottomSheetBehavior.setBottomSheetCallback(mBottomSheetBehaviorCallback);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            });
        }

        Dialog currentDialog = getDialog();
        if(currentDialog != null){
            currentDialog.setCanceledOnTouchOutside(mCanSlideDismiss);
            currentDialog.setOnKeyListener((dialog, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    onBackPressed();
                    return true;
                }
                return false;
            });
        }
    }


    /**
     * 返回键处理
     */
    protected abstract void onBackPressed();


    /**
     * 禁用下拉消失
     */
    void disableSlideDismiss(){
        mCanSlideDismiss = false;
    }
}
