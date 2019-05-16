package com.sensoro.common.widgets;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.TextView;

import com.sensoro.common.R;

public class PermissionDialogUtils {

    //    private AlertDialog mDialog;
    private final TextView mTvMessage;
    private final TextView mTvCancel;
    private final TextView mTvConfirm;
    private final TextView mTvTitle;
    private TipDialogUtilsClickListener listener;
    private CustomCornerDialog mDialog;
    private Activity mActivity;
    public static final int REQUEST_CODE_BLUETOOTH_ON = 0x222;

    public PermissionDialogUtils(Activity activity, boolean cancelable) {
        this(activity);
        mDialog.setCancelable(cancelable);
    }

    public PermissionDialogUtils(Activity activity) {
        mActivity = activity;
        View view = View.inflate(activity, R.layout.item_dialog_permission_tip, null);
        mTvTitle = view.findViewById(R.id.dialog_tip_ble_tv_title);
        mTvMessage = view.findViewById(R.id.dialog_tip_ble_tv_message);
        mTvCancel = view.findViewById(R.id.dialog_tip_ble_tv_cancel);
        mTvConfirm = view.findViewById(R.id.dialog_tip_ble_tv_confirm);
        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCancelClick();
                }
            }
        });

        mTvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onConfirmClick();
                }
            }
        });

    }

    public boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }

    public PermissionDialogUtils setTipTitleText(String text) {
        mTvTitle.setText(text);
        return this;
    }

    public PermissionDialogUtils setTipMessageText(String text) {
        mTvMessage.setText(text);
        return this;
    }

    public PermissionDialogUtils setTipCacnleText(String text, @ColorInt int color) {
        mTvCancel.setText(text);
        mTvCancel.setTextColor(color);
        return this;
    }

    public PermissionDialogUtils setTipConfirmText(String text, @ColorInt int color) {
        mTvConfirm.setText(text);
        mTvConfirm.setTextColor(color);
        return this;
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void show(TipDialogUtilsClickListener listener) {
        if (mDialog != null) {
            this.listener = listener;
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

    public PermissionDialogUtils setTipDialogUtilsClickListener(TipDialogUtilsClickListener listener) {
        this.listener = listener;
        return this;
    }

    public PermissionDialogUtils setTipConfirmVisible(boolean isVisible) {
        mTvConfirm.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        return this;
    }


    public interface TipDialogUtilsClickListener {
        void onCancelClick();

        void onConfirmClick();
    }

}
