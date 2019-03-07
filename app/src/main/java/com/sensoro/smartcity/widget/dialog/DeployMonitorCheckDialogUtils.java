package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.sensoro.smartcity.R;

public class DeployMonitorCheckDialogUtils {
    private final TextView tvDeployCheckDialogForceUpload;
    private CustomCornerDialog mDialog;
    private final TextView tvDeployCheckDialogTest;
    private OnDeployCheckDialogListener listener;

    public DeployMonitorCheckDialogUtils(Activity activity) {
        View view = View.inflate(activity, R.layout.item_deploy_monitor_check_dialog, null);
        View ivDeployCheckCancel = view.findViewById(R.id.iv_deploy_check_cancel);
        tvDeployCheckDialogTest = view.findViewById(R.id.tv_deploy_check_dialog_test);
        tvDeployCheckDialogForceUpload = view.findViewById(R.id.tv_deploy_check_dialog_force_upload);
        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);

        ivDeployCheckCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        tvDeployCheckDialogTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickTest();
                }
            }
        });
        tvDeployCheckDialogForceUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickForceUpload();
                }
            }
        });
        //TODO 点击详细说明的回调

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

    public void setOnDeployCheckDialogListener(OnDeployCheckDialogListener listener) {
        this.listener = listener;
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void destroy() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
        }
    }

    public interface OnDeployCheckDialogListener {
        void onClickTest();

        void onClickForceUpload();

        void onClickDeviceDetailInfo();
    }
}
