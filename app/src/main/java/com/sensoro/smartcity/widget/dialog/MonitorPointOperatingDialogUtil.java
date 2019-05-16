package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.smartcity.R;

public class MonitorPointOperatingDialogUtil {

    private final ImageView imvLoading;
    private final TextView tvTip;
    private CustomCornerDialog mDialog;
    private final RotateAnimation rotateAnimation;

    public MonitorPointOperatingDialogUtil(Activity activity,boolean cancelable) {
        this(activity);
        mDialog.setCancelable(cancelable);
    }

    public MonitorPointOperatingDialogUtil(Activity activity) {
        View view = View.inflate(activity, R.layout.item_dialog_monitor_point_tip, null);
        imvLoading = view.findViewById(R.id.dialog_monitor_point_operating_imv_loading);
        tvTip = view.findViewById(R.id.dialog_monitor_point_operating_tip);
        mDialog = new CustomCornerDialog(activity, view,0.6f);
        rotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new LinearInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
    }

    public void setTipText(String text){
        tvTip.setText(text);
    }

    public void show(){
        if (mDialog != null) {
            mDialog.show();
            imvLoading.startAnimation(rotateAnimation);
        }
    }

    public void dismiss(){
        if (mDialog != null) {
            mDialog.dismiss();
            imvLoading.clearAnimation();
        }
    }

    public void destroy() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
        }
    }

    public boolean  isShowing(){
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }
}
