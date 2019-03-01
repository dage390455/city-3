package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sensoro.smartcity.R;

public class TipDeviceUpdateDialogUtils {

    //    private AlertDialog mDialog;
    private final TextView tvDialogTipTitle;
    private final TextView tvDialogUpdateVersion;
    private final TextView tvDialogUpdateDate;
    private final TextView tvDialogUpdate;
    private final ImageView ivCancel;
    private final LinearLayout llUpdating;
    private final ImageView imvDialogRotate;
    private final ProgressBar progressBarCircle;
    private final TextView tvDialogContent;
    private final LinearLayout llReadyUpdate;
    private TipDialogUpdateClickListener listener;
    private CustomCornerDialog mDialog;
    private final RotateAnimation rotateAnimation;

    public TipDeviceUpdateDialogUtils(Activity activity) {
        View view = View.inflate(activity, R.layout.item_device_update_dialog_tip, null);
        tvDialogTipTitle = view.findViewById(R.id.tv_dialog_tip_title);
        tvDialogUpdateVersion = view.findViewById(R.id.tv_dialog_update_version);
        tvDialogUpdateDate = view.findViewById(R.id.tv_dialog_update_date);
        tvDialogUpdate = view.findViewById(R.id.tv_dialog_update);
        llUpdating = view.findViewById(R.id.ll_updating);
        imvDialogRotate = view.findViewById(R.id.imv_dialog_rotate);
        progressBarCircle = view.findViewById(R.id.progress_bar_circle);
        tvDialogContent = view.findViewById(R.id.tv_dialog_content);
        llReadyUpdate = view.findViewById(R.id.ll_ready_update);
        ivCancel = view.findViewById(R.id.iv_cancel);

        progressBarCircle.setMax(100);

        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);

        rotateAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);

        tvDialogUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onUpdateClick();
                }
            }
        });
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (imvDialogRotate != null) {
                    imvDialogRotate.clearAnimation();
                }
            }
        });

    }

    public void setCanceledOnTouchOutside(boolean canceled) {
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(canceled);
        }
    }

    public void setCancelable(boolean flag) {
        if (mDialog != null) {
            mDialog.setCancelable(flag);
        }

    }

    public void setTipTitleText(String text) {
        if (!TextUtils.isEmpty(text)) {
            tvDialogTipTitle.setText(text);
        }
    }

    public void setTipButtonVisible(boolean isVisible) {
        tvDialogUpdate.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setTipNewVersionText(String text) {
        if (!TextUtils.isEmpty(text)) {
            tvDialogUpdateVersion.setText(text);
        }

    }

    public void setTipVersionDateText(String text) {
        if (TextUtils.isEmpty(text)) {
            tvDialogUpdateDate.setVisibility(View.GONE);
        } else {
            tvDialogUpdateDate.setVisibility(View.VISIBLE);
            tvDialogUpdateDate.setText(text);
        }

    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
            //TODO
            llReadyUpdate.setVisibility(View.VISIBLE);
            llUpdating.setVisibility(View.GONE);
//            WindowManager m = mDialog.getWindow().getWindowManager();
//            Display d = m.getDefaultDisplay();
//            WindowManager.LayoutParams p = mDialog.getWindow().getAttributes();
//            p.width = d.getWidth() - 100;
//            mDialog.getWindow().setAttributes(p);
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void destory() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
            imvDialogRotate.clearAnimation();
        }
    }

    public void setTipDialogUtilsClickListener(TipDialogUpdateClickListener listener) {
        this.listener = listener;
    }

    public void updateDialog(String msg, int progress, int status) {
        if (status == 0) {
            llReadyUpdate.setVisibility(View.GONE);
            llUpdating.setVisibility(View.VISIBLE);
            imvDialogRotate.setVisibility(View.VISIBLE);
            imvDialogRotate.startAnimation(rotateAnimation);
        } else if (status == 1) {
            imvDialogRotate.clearAnimation();
            progressBarCircle.setVisibility(View.VISIBLE);
            imvDialogRotate.setVisibility(View.GONE);
            progressBarCircle.setProgress(progress);
        } else if (status == 2) {
            imvDialogRotate.setVisibility(View.VISIBLE);
            progressBarCircle.setVisibility(View.GONE);
            imvDialogRotate.startAnimation(rotateAnimation);
        }

        tvDialogContent.setText(msg);
    }

    public interface TipDialogUpdateClickListener {
        void onUpdateClick();
    }

}
