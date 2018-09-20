package com.sensoro.smartcity.widget;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;

public class TagDialogUtils implements View.OnClickListener {
    private final AlertDialog mAddTagDialog;
    private EditText mDialogEtInput;
    private ImageView mDialogImvClear;
    private TextView mDialogTvCancel;
    private TextView mDialogTvConfirm;
    private OnTagDialogListener onTagDialogListener;
    public static final int DIALOG_TAG_ADD = 1;
    public static final int DIALOG_TAG_EDIT = 2;
    private int mType = -1;
    private int currentPosition = -1;

    public TagDialogUtils(Activity activity) {
        View view = View.inflate(activity, R.layout.dialog_frag_deploy_device_add_tag, null);
        mDialogEtInput = view.findViewById(R.id.dialog_add_tag_et_input);
        mDialogImvClear = view.findViewById(R.id.dialog_add_tag_imv_clear);
        mDialogTvCancel = view.findViewById(R.id.dialog_add_tag_tv_cancel);
        mDialogTvConfirm = view.findViewById(R.id.dialog_add_tag_tv_confirm);
        mDialogTvConfirm.setOnClickListener(this);
        mDialogTvCancel.setOnClickListener(this);
        mDialogImvClear.setOnClickListener(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setView(view);
        builder.setCancelable(false);
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                currentPosition = -1;
                mDialogEtInput.getText().clear();
            }
        });
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                currentPosition = -1;
                mDialogEtInput.getText().clear();
            }
        });
        mAddTagDialog = builder.create();
        Window window = mAddTagDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


    }

    public void registerListener(OnTagDialogListener onTagDialogListener) {
        this.onTagDialogListener = onTagDialogListener;
    }

    public interface OnTagDialogListener {
        void onConfirm(int type, String text, int position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dialog_add_tag_tv_cancel:
                dismissDialog();
                break;
            case R.id.dialog_add_tag_tv_confirm:
                if (mDialogEtInput != null) {
                    String tag = mDialogEtInput.getText().toString();
                    if (onTagDialogListener != null) {
                        onTagDialogListener.onConfirm(mType, tag, currentPosition);
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
        if (this.onTagDialogListener != null) {
            this.onTagDialogListener = null;
        }
    }

    public void show() {
        if (mAddTagDialog != null) {
            mType = DIALOG_TAG_ADD;
            mAddTagDialog.show();
        }
    }

    public void show(String text, int position) {
        if (mAddTagDialog != null) {
            mType = DIALOG_TAG_EDIT;
            mDialogEtInput.setText(text);
            String string = mDialogEtInput.getText().toString();
            mDialogEtInput.setSelection(string.length());
            currentPosition = position;
            mAddTagDialog.show();
        }

    }

    public void dismissDialog() {
        if (mAddTagDialog != null) {
            mAddTagDialog.dismiss();
        }

    }
}
