package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.smartcity.R;

public class BleConfigurationDialogUtils {

    private final ImageView mImv;
    private final TextView mTv;
    private final CustomCornerDialog mDialog;
    private final RotateAnimation rotateAnimation;

    public BleConfigurationDialogUtils(Activity activity, String message) {
        View view = View.inflate(activity, R.layout.item_progress_dilog, null);
        mImv = view.findViewById(R.id.progress_imv);
        mTv = view.findViewById(R.id.progress_tv);
        mDialog = new CustomCornerDialog(activity, view);
        mDialog.setCancelable(false);
        mTv.setText(message);
        Window window = mDialog.getWindow();
        if (window != null) {
            window.setDimAmount(0f);
        }
        rotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
            mImv.startAnimation(rotateAnimation);
        }

    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
            mImv.clearAnimation();
        }
    }

    public void onDestroy() {
        if (mDialog != null) {
            mDialog.cancel();
            mImv.clearAnimation();
        }
    }

    public void updateTvText(String text) {
        if (mTv != null) {
            mTv.setText(text);
        }
    }

    public void showSuccessImv() {
        mImv.clearAnimation();
        mImv.setImageResource(R.drawable.dialog_success);
    }
}
