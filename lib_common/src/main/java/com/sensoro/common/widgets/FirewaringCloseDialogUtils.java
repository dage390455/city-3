package com.sensoro.common.widgets;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.sensoro.common.R;

/**
 * 确认关闭火警对话框
 */
public class FirewaringCloseDialogUtils {

    private final TextView mTvMessage;
    private final TextView mTvCancel;
    private final TextView mTvConfirm;
    private final TextView mTvTitle;
    private TipDialogUtilsClickListener listener;
    private CustomCornerDialog mDialog;
    private Activity mActivity;
    public static final int REQUEST_CODE_BLUETOOTH_ON = 0x222;

    public FirewaringCloseDialogUtils(Activity activity, boolean cancelable) {
        this(activity);
        mDialog.setCancelable(cancelable);
    }

    public FirewaringCloseDialogUtils(Activity activity) {
        mActivity = activity;
        View view = View.inflate(activity, R.layout.item_dialog_firewarning_close_tip, null);
        mTvTitle = view.findViewById(R.id.dialog_tip_ble_tv_title);
        mTvMessage = view.findViewById(R.id.dialog_tip_ble_tv_message);
        mTvCancel = view.findViewById(R.id.dialog_tip_ble_tv_cancel);
        mTvConfirm = view.findViewById(R.id.dialog_tip_ble_tv_confirm);
        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        mTvCancel.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCancelClick();
            }
        });

        mTvConfirm.setOnClickListener(v -> {
            if (listener != null) {
                listener.onConfirmClick();
            }
        });

    }

    public boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }

    public FirewaringCloseDialogUtils setTipTitleText(String text) {
        mTvTitle.setText(text);
        return this;
    }

    public FirewaringCloseDialogUtils setTipMessageText(String text) {
        mTvMessage.setText(text);
        return this;
    }

    public FirewaringCloseDialogUtils setTipCacnleText(String text, @ColorInt int color) {
        mTvCancel.setText(text);
        mTvCancel.setTextColor(color);
        return this;
    }

    public FirewaringCloseDialogUtils setTipConfirmText(String text, @ColorInt int color) {
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

    public FirewaringCloseDialogUtils setTipDialogUtilsClickListener(TipDialogUtilsClickListener listener) {
        this.listener = listener;
        return this;
    }

    public FirewaringCloseDialogUtils setTipConfirmVisible(boolean isVisible) {
        mTvConfirm.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        return this;
    }


    public interface TipDialogUtilsClickListener {
        void onCancelClick();

        void onConfirmClick();
    }

}
