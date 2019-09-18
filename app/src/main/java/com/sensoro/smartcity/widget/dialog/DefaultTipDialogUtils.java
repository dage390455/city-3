package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.ColorInt;

import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.smartcity.R;

public class DefaultTipDialogUtils {

    //    private AlertDialog mDialog;
    private final TextView dialogTipTvCancel;
    private final TextView dialogTipTvConfirm;
    private final TextView dialogTipTvContent;
    private OnPopupDismissListener dismissListener;

    public Activity getmActivity() {
        return mActivity;
    }

    private final Activity mActivity;
    //    private final TextView mTvConfirm;
    private CustomCornerDialog mDialog;
    private ProgressUtils mProgressUtils;
    private DialogUtilsClickListener listener;

    public DefaultTipDialogUtils(Activity activity) {
        mActivity = activity;
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        View view = View.inflate(activity, R.layout.item_dialog_permission_change, null);
        dialogTipTvCancel = view.findViewById(R.id.dialog_tip_tv_cancel);
        dialogTipTvConfirm = view.findViewById(R.id.dialog_tip_tv_confirm);
        dialogTipTvContent = view.findViewById(R.id.tv_permission_message);
//        mTvConfirm = view.findViewById(R.id.dialog_tip_tv_confirm);
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setView(view);
//        builder.setCancelable(false);
//        mDialog = builder.create();
//        Window window = mDialog.getWindow();
//        if (window != null) {
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }

        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        dialogTipTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
                if (dismissListener != null) {
                    dismissListener.onDismiss();
                }
            }
        });

    }


    public void show() {
        if (mDialog != null && !mDialog.isShowing()) {
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

    public void setTipMessageText(String text) {
        dialogTipTvContent.setText(text);
    }

    public void setTipCacnleText(String text, @ColorInt int color) {
        dialogTipTvCancel.setText(text);
//        mTvConfirm.setTextColor(color);
    }

    public void setTipConfirmText(String text, @ColorInt int color) {
        dialogTipTvConfirm.setText(text);
        dialogTipTvConfirm.setTextColor(color);
    }

    public void setDismissListener(OnPopupDismissListener listener) {

        dismissListener = listener;
    }

    public interface OnPopupDismissListener {


        void onDismiss();

    }

    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    public void setUtilsClickListener(DialogUtilsClickListener listener) {
        this.listener = listener;
    }

    public interface DialogUtilsClickListener {
        void onCancelClick();

        void onConfirmClick();
    }
}
