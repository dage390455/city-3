package com.sensoro.common.widgets;

import android.app.Activity;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.common.R;

import java.lang.ref.WeakReference;

public class ProgressUtils {
    private Builder builder;

    public ProgressUtils(Builder builder) {
        this.builder = builder;
    }

    public void showProgress() {
        CustomCornerDialog progressDialog = builder.getProgressDialog();
        if (progressDialog != null) {
            progressDialog.show();
            builder.startAnimation();
        }
    }

    public void dismissProgress() {
        CustomCornerDialog progressDialog = builder.getProgressDialog();
        if (progressDialog != null) {
            builder.stopAnimation();
            progressDialog.dismiss();
        }

    }

    public void destroyProgress() {
        builder.destroyProgressDialog();
    }

    public void updateMessage(String message) {
        if (builder != null) {
            builder.updateMessage(message);
        }
    }

    private static class MyRotateAnimation {
        private RotateAnimation rotateAnimation;

        private MyRotateAnimation() {
            rotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(1000);
//            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setInterpolator(new AccelerateInterpolator());
            rotateAnimation.setRepeatCount(Animation.INFINITE);
            rotateAnimation.setFillBefore(true);
            rotateAnimation.setFillAfter(true);
        }


        private static class MyRotateAnimationHolder {
            private static final MyRotateAnimation instance = new MyRotateAnimation();
        }

        public static MyRotateAnimation getInstance() {
            return MyRotateAnimationHolder.instance;
        }
    }

    public static final class Builder {
        private CustomCornerDialog progressDialog;
        private WeakReference<Activity> activity;
        private String message;
        private TextView mTv;
        private ImageView mImv;
        private boolean cancelable = true;

        public Builder(Activity ac) {
            activity = new WeakReference<>(ac);
            message = activity.get().getString(R.string.loading);
        }

        public Builder build() {
            View view = View.inflate(activity.get(), R.layout.item_progress_dilog, null);
            mImv = view.findViewById(R.id.progress_imv);
            mTv = view.findViewById(R.id.progress_tv);
            progressDialog = new CustomCornerDialog(activity.get(), view, R.style.ProgressDialogStyle);
            progressDialog.setCancelable(cancelable);
            progressDialog.setCanceledOnTouchOutside(cancelable);
            mTv.setText(message);
            Window window = progressDialog.getWindow();
            if (window != null) {
//                window.setType(WindowManager.LayoutParams.TYPE_TOAST);
//                window.setDimAmount(0f);
            }
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            if (progressDialog != null) {
                mTv.setText(message);
            }
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            if (progressDialog != null) {
                progressDialog.setCancelable(cancelable);
                progressDialog.setCanceledOnTouchOutside(cancelable);
            }
            return this;
        }

        private CustomCornerDialog getProgressDialog() {
            if (progressDialog != null) {
                return progressDialog;
            }
            return null;
        }

        private void destroyProgressDialog() {
            stopAnimation();
            if (progressDialog != null) {
                progressDialog.cancel();
                progressDialog = null;
            }
            if (activity != null) {
                activity.clear();
                activity = null;
            }
        }

        private void startAnimation() {
            if (mImv != null) {
                mImv.startAnimation(MyRotateAnimation.getInstance().rotateAnimation);
            }
        }

        private void stopAnimation() {
            if (mImv != null) {
                mImv.clearAnimation();
            }
        }

        private void updateMessage(String message) {
            if (mTv != null) {
                mTv.setText(message);
            }
        }
    }
}
