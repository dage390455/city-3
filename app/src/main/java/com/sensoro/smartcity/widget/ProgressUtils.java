package com.sensoro.smartcity.widget;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.widget.dialog.CustomCornerDialog;

import java.lang.ref.WeakReference;
import java.time.chrono.MinguoChronology;

import butterknife.BindView;

public class ProgressUtils {
    private Builder builder;

    public ProgressUtils(Builder builder) {
        this.builder = builder;
    }

    public void showProgress() {
        CustomCornerDialog progressDialog = builder.getProgressDialog();
        if (progressDialog != null) {
            builder.startAnimation();
            progressDialog.show();
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

    public static final class Builder {
        private CustomCornerDialog progressDialog;
        private WeakReference<Activity> activity;
        private String message;
        private TextView mTv;
        private ImageView mImv;
        private RotateAnimation rotateAnimation;

        public Builder(Activity ac) {
            activity = new WeakReference<>(ac);
            message = activity.get().getString(R.string.loading);
        }

        public Builder build() {
            View view = View.inflate(activity.get(), R.layout.item_progress_dilog, null);
            mImv = view.findViewById(R.id.progress_imv);
            mTv = view.findViewById(R.id.progress_tv);
            progressDialog = new CustomCornerDialog(activity.get(), view);
            mTv.setText(message);
            Window window = progressDialog.getWindow();
            if (window != null) {
                window.setDimAmount(0f);
            }
            rotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(1000);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            rotateAnimation.setRepeatCount(Animation.INFINITE);
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        private CustomCornerDialog getProgressDialog() {
            if (progressDialog != null) {
                return progressDialog;
            }
            return null;
        }

        private void destroyProgressDialog() {
            if (progressDialog != null) {
                progressDialog.cancel();
                progressDialog = null;
            }
            if (activity != null) {
                activity.clear();
                activity = null;
            }
        }

        public void startAnimation() {
            mImv.startAnimation(rotateAnimation);
        }

        public void stopAnimation() {
            mImv.clearAnimation();
        }

        public void updateMessage(String message) {
            if (mTv != null) {
                mTv.setText(message);
            }
        }
    }
}
