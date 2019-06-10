package com.sensoro.common.widgets.dialog;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorInt;

import com.sensoro.common.R;
import com.sensoro.common.widgets.CustomCornerDialog;

public class TitleTipDialogUtils {

    //    private AlertDialog mDialog;
    private final TextView mTvMessage;
    private final TextView mTvCancel;
    private final TextView mTvConfirm;
    private final TextView mTvTitle;
    private TitleTipDialogUtilsClickListener listener;
    private CustomCornerDialog mDialog;
    private Activity mActivity;
    public static final int REQUEST_CODE_BLUETOOTH_ON = 0x222;

    public TitleTipDialogUtils(Activity activity, boolean cancelable) {
        this(activity);
        mDialog.setCancelable(cancelable);
    }

    public TitleTipDialogUtils(Activity activity) {
        mActivity = activity;
        View view = View.inflate(activity, R.layout.item_dialog_ble_tip, null);
        mTvTitle = view.findViewById(R.id.dialog_tip_ble_tv_title);
        mTvMessage = view.findViewById(R.id.dialog_tip_ble_tv_message);
        mTvCancel = view.findViewById(R.id.dialog_tip_ble_tv_cancel);
        mTvConfirm = view.findViewById(R.id.dialog_tip_ble_tv_confirm);
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setView(view);
//        builder.setCancelable(false);
//        mDialog = builder.create();
//        Window window = mDialog.getWindow();
//        if (window != null) {
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }

        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);

        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
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

    public void setTipTitleText(String text) {
        mTvTitle.setText(text);
    }

    public void setTipMessageText(String text) {
        mTvMessage.setText(text);
    }

    public void setTipCancelText(String text, @ColorInt int color) {
        mTvCancel.setText(text);
        mTvCancel.setTextColor(color);
    }

    public void setTipConfirmText(String text, @ColorInt int color) {
        mTvConfirm.setText(text);
        mTvConfirm.setTextColor(color);
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

    public void setTipDialogUtilsClickListener(TitleTipDialogUtilsClickListener listener) {
        this.listener = listener;
    }

    public void setTipConfirmVisible(boolean isVisible) {
        mTvConfirm.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }


    public interface TitleTipDialogUtilsClickListener {
        void onCancelClick();

        void onConfirmClick();
    }

}
