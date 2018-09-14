package com.sensoro.smartcity.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.LoginActivity;
import com.sensoro.smartcity.activity.LoginActivityTest;

public class SplashScreen {
    private Dialog splashDialog;
    private Activity activity;

    public SplashScreen(Activity activity) {
        this.activity = activity;
    }

    /**
     * 显示splash图片
     *
     * @param millis        停留时间 毫秒
     *
     */
    public void show(final int millis) {
        Runnable runnable = new Runnable() {
            public void run() {
                DisplayMetrics metrics = new DisplayMetrics();

                final ImageView root = new ImageView(activity);
                root.setMinimumHeight(metrics.heightPixels);
                root.setMinimumWidth(metrics.widthPixels);
                root.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT, 0.0F));
                root.setScaleType(ImageView.ScaleType.FIT_XY);

                //glide加载图片
                Glide.with(activity)
                        .load("")
                        .placeholder(R.drawable.splash)
                        .error(R.drawable.splash)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(root);

                splashDialog = new Dialog(activity, R.style.AppTheme_StartWithLogin);

                Window window = splashDialog.getWindow();
                window.setWindowAnimations(R.style.popupwindow_anim_style);

                splashDialog.setContentView(root);
                splashDialog.setCancelable(false);
                splashDialog.show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        removeSplash();
                        activity.startActivity(new Intent(activity, LoginActivityTest.class));
                        activity.finish();
                    }
                }, millis);
            }
        };
        activity.runOnUiThread(runnable);
    }

    private void removeSplash() {
        if (splashDialog != null && splashDialog.isShowing()) {
            splashDialog.dismiss();
            splashDialog = null;
        }
    }
}
