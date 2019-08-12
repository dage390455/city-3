package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.smartcity.R;
import com.sensoro.common.utils.AppUtils;

public class LoginUrlDialogUtils implements View.OnClickListener {
    private final Activity mActivity;
    private final TextView mDialogTvTittle;
    private CustomCornerDialog mAddTagDialog;
    private EditText mDialogEtInput;
    private ImageView mDialogImvClear;
    private TextView mDialogTvCancel;
    private TextView mDialogTvConfirm;
    private OnLoginUrlDialogListener onLoginUrlDialogListener;

    public LoginUrlDialogUtils(final Activity activity) {
        View view = View.inflate(activity, R.layout.dialog_frag_deploy_device_add_tag, null);
        mDialogEtInput = view.findViewById(R.id.dialog_add_tag_et_input);
        mDialogTvTittle = view.findViewById(R.id.tv_title_dialog_add_tag);
        mDialogImvClear = view.findViewById(R.id.dialog_add_tag_imv_clear);
        mDialogTvCancel = view.findViewById(R.id.dialog_add_tag_tv_cancel);
        mDialogTvConfirm = view.findViewById(R.id.dialog_add_tag_tv_confirm);
        mDialogTvConfirm.setOnClickListener(this);
        mDialogTvCancel.setOnClickListener(this);
        mDialogImvClear.setOnClickListener(this);
        mDialogEtInput.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mDialogEtInput.requestFocus();
                mDialogEtInput.setCursorVisible(true);
                return false;
            }
        });
        mDialogEtInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
            }
        });
        mAddTagDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setView(view);
//        builder.setCancelable(false);
        mActivity = activity;
        mAddTagDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mDialogEtInput.getText().clear();
            }
        });
        mAddTagDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mDialogEtInput.getText().clear();
            }
        });
//        mAddTagDialog = builder.create();
//        Window window = mAddTagDialog.getWindow();
//        if (window != null) {
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }


    }

    public void registerListener(OnLoginUrlDialogListener onLoginUrlDialogListener) {
        this.onLoginUrlDialogListener = onLoginUrlDialogListener;
    }

    public void setTitle(String title) {
        if (mDialogTvTittle != null) {
            mDialogTvTittle.setText(title);
        }
    }

    public interface OnLoginUrlDialogListener {
        void onConfirm(String text);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_add_tag_tv_cancel:
                AppUtils.dismissInputMethodManager(mActivity, mDialogEtInput);
                dismissDialog();
                break;
            case R.id.dialog_add_tag_tv_confirm:
                if (mDialogEtInput != null) {
                    AppUtils.dismissInputMethodManager(mActivity, mDialogEtInput);
                    String tag = mDialogEtInput.getText().toString();
                    if (onLoginUrlDialogListener != null) {
                        onLoginUrlDialogListener.onConfirm(tag);
                    }
                }
                break;
            case R.id.dialog_add_tag_imv_clear:
                if (mDialogEtInput != null) {
                    mDialogEtInput.getText().clear();
                }
                break;

        }
    }

    public void unregisterListener() {
        if (mAddTagDialog != null) {
            mAddTagDialog.cancel();
        }
        if (this.onLoginUrlDialogListener != null) {
            this.onLoginUrlDialogListener = null;
        }
    }

    public void show() {
        if (mAddTagDialog != null) {
            mAddTagDialog.show();
            mDialogEtInput.setCursorVisible(true);
        }
    }

    public void show(String text) {
        if (mAddTagDialog != null) {
            if (!TextUtils.isEmpty(text)) {
                mDialogEtInput.setText(text);
                String string = mDialogEtInput.getText().toString();
                mDialogEtInput.setSelection(string.length());
            }
            mAddTagDialog.show();
            mDialogEtInput.setCursorVisible(true);
        }

    }

    public void dismissDialog() {
        if (mAddTagDialog != null) {
            mAddTagDialog.dismiss();
        }

    }
}
