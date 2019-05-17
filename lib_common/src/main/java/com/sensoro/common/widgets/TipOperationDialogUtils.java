package com.sensoro.common.widgets;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sensoro.common.R;

public class TipOperationDialogUtils {

    //    private AlertDialog mDialog;
    private final TextView mTvMessage;
    private final TextView mTvCancel;
    private final TextView mTvConfirm;
    private final TextView mTvTitle;
    private final LinearLayout mLlEtRoot;
    private final LinearLayout mLlEtDiameter;
    private final EditText mEt;
    private final EditText mEtDiameter;
    private TipDialogUtilsClickListener listener;
    private CustomCornerDialog mDialog;
    private Activity mActivity;
    public static final int REQUEST_CODE_BLUETOOTH_ON = 0x222;

    public TipOperationDialogUtils(Activity activity, boolean cancelable) {
        this(activity);
        mDialog.setCancelable(cancelable);
    }

    public TipOperationDialogUtils(Activity activity) {
        mActivity = activity;
        final View view = View.inflate(activity, R.layout.item_dialog_monitor_point_operation, null);
        mTvTitle = view.findViewById(R.id.dialog_tip_operation_tv_title);
        mTvMessage = view.findViewById(R.id.dialog_tip_operation_tv_message);
        mTvCancel = view.findViewById(R.id.dialog_tip_operation_tv_cancel);
        mTvConfirm = view.findViewById(R.id.dialog_tip_operation_tv_confirm);
        mLlEtRoot = view.findViewById(R.id.dialog_operation_ll_et_root);
        mEt = view.findViewById(R.id.dialog_operation_et);
        mLlEtDiameter = view.findViewById(R.id.dialog_operation_ll_et_diameter);
        mEtDiameter = view.findViewById(R.id.dialog_operation_et_diameter);
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setView(view);
//        builder.setCancelable(false);
//        mDialog = builder.create();
//        Window window = mDialog.getWindow();
//        if (window != null) {
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }

        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view, true);

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
                if (mLlEtRoot.getVisibility() == View.VISIBLE) {
                    if (mLlEtDiameter.getVisibility() == View.VISIBLE) {
                        listener.onConfirmClick(mEt.getText().toString(), mEtDiameter.getText().toString());
                    } else {
                        listener.onConfirmClick(mEt.getText().toString(), null);
                    }

                } else {
                    listener.onConfirmClick(null, null);
                }
            }
        });

        mDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
//                AppUtils.dismissInputMethodManager(mActivity,mEt);
//                mEt.setEnabled(false);
            }
        });

    }

    public boolean isShowing() {
        if (mDialog != null) {
            return mDialog.isShowing();
        }
        return false;
    }

    public void setTipTitleText(CharSequence text) {
        mTvTitle.setText(text);
    }

    public void setTipMessageText(CharSequence text) {
        mTvMessage.setText(text);
    }

    public void setTipMessageText(CharSequence text, int color) {
        mTvMessage.setText(text);
        mTvMessage.setTextColor(mActivity.getResources().getColor(color));
    }

    public void setTipCancelText(CharSequence text, @ColorInt int color) {
        mTvCancel.setText(text);
        mTvCancel.setTextColor(color);
    }

    public void setTipConfirmText(CharSequence text, @ColorInt int color) {
        mTvConfirm.setText(text);
        mTvConfirm.setTextColor(color);
    }

    public void show() {
        if (mDialog != null) {
            mEt.getText().clear();
            mDialog.show();
            mEtDiameter.setCursorVisible(true);
            mEt.setCursorVisible(true);
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

    public void destroy() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
        }
    }

    public void setTipDialogUtilsClickListener(TipDialogUtilsClickListener listener) {
        this.listener = listener;
    }

    public void setTipConfirmVisible(boolean isVisible) {
        mTvConfirm.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setTipEtRootVisible(boolean isVisible) {
        mLlEtRoot.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setDiameterVisible(boolean isVisible) {
        mLlEtDiameter.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }


    public interface TipDialogUtilsClickListener {
        void onCancelClick();

        void onConfirmClick(String content, String diameter);
    }

}
