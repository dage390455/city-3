package com.sensoro.logintest;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.CustomCornerDialog;

public class LoginUrlDialogUtils implements View.OnClickListener {
    private final Activity mActivity;
    private final TextView mDialogTvTittle;
    private CustomCornerDialog mAddTagDialog;
    private EditText mDialogEtInput;
    private TextView mDialogTvCancel;
    private TextView tvMyBaseUrl;
    private TextView mDialogTvConfirm;
    private OnLoginUrlDialogListener onLoginUrlDialogListener;

    public LoginUrlDialogUtils(final Activity activity) {
        View view = View.inflate(activity, R.layout.dialog_add_my_base_url_test, null);
        mDialogEtInput = view.findViewById(R.id.dialog_add_tag_et_input);
        tvMyBaseUrl = view.findViewById(R.id.tv_my_base_url);
        mDialogTvTittle = view.findViewById(R.id.tv_title_dialog_add_tag);
        mDialogTvCancel = view.findViewById(R.id.dialog_add_tag_tv_cancel);
        mDialogTvConfirm = view.findViewById(R.id.dialog_add_tag_tv_confirm);
        mDialogTvConfirm.setOnClickListener(this);
        mDialogTvCancel.setOnClickListener(this);
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
        mDialogEtInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    StringBuilder stringBuilder = new StringBuilder(s);
                    stringBuilder.append("-api.sensoro.com/");
                    if (tvMyBaseUrl != null) {
                        tvMyBaseUrl.setText(stringBuilder);
                    }
                } else {
                    if (tvMyBaseUrl != null) {
                        tvMyBaseUrl.setText("请填写地址！");
                    }
                }
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
    public void onClick(View view) {
        int viewID=view.getId();
        if(viewID==R.id.dialog_add_tag_tv_cancel){
            AppUtils.dismissInputMethodManager(mActivity, mDialogEtInput);
            dismissDialog();
        }else if(viewID==R.id.dialog_add_tag_tv_confirm){
            if (mDialogEtInput != null) {
                AppUtils.dismissInputMethodManager(mActivity, mDialogEtInput);
                String tag = mDialogEtInput.getText().toString();
                if (onLoginUrlDialogListener != null) {
                    onLoginUrlDialogListener.onConfirm(tag);
                }
            }
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
