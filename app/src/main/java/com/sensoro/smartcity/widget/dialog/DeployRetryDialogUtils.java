package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;

public class DeployRetryDialogUtils {

    //    private AlertDialog mDialog;
    private final TextView dialogTipTvCancel;
    private final TextView dialogTipTvConfirm;
    private final TextView contentTv;
    private onRetrylickListener listener;

    public Activity getmActivity() {
        return mActivity;
    }

    private final Activity mActivity;
    //    private final TextView mTvConfirm;
    private CustomCornerDialog mDialog;
    private ProgressUtils mProgressUtils;

    public DeployRetryDialogUtils(Activity activity) {
        mActivity = activity;
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        View view = View.inflate(activity, R.layout.item_dialog_permission_change, null);
        dialogTipTvCancel = view.findViewById(R.id.dialog_tip_tv_cancel);
        dialogTipTvConfirm = view.findViewById(R.id.dialog_tip_tv_confirm);
        contentTv = view.findViewById(R.id.tv_permission_message);

        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        dialogTipTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onCancelClick();
                }
            }
        });
        dialogTipTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != listener) {
                    listener.onConfirmClick();
                }
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (listener != null) {
                    listener.onDismiss();
                }
            }
        });

    }


    public void show() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void show(String content, String leftText, String rightText) {
        if (mDialog != null && !mDialog.isShowing()) {

            contentTv.setText(content);
            dialogTipTvConfirm.setText(rightText);
            dialogTipTvCancel.setText(leftText);
            mDialog.show();
        }
    }

    public void showProgressDialog() {
        mProgressUtils.showProgress();
    }

    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
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
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
        }
    }


    public void setonRetrylickListener(onRetrylickListener listener) {
        this.listener = listener;
    }


    public interface onRetrylickListener {
        void onConfirmClick();

        void onCancelClick();

        void onDismiss();

    }

    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

}
