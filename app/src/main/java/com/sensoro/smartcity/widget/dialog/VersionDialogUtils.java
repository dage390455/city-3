package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.TextView;

import com.sensoro.common.widgets.CustomCornerDialog;
import com.sensoro.smartcity.R;

public class VersionDialogUtils {

    private final TextView mTvTitle;
//    private AlertDialog mDialog;
    private final TextView mTvMessage;
    private final TextView mTvCancel;
//    private final TextView mTvConfirm;
    private VersionDialogUtilsClickListener listener;
    private CustomCornerDialog mDialog;

    public VersionDialogUtils(Activity activity) {
        View view = View.inflate(activity, R.layout.item_dialog_version, null);
        mTvTitle = view.findViewById(R.id.dialog_tip_tv_title);
        mTvMessage = view.findViewById(R.id.dialog_tip_tv_message);
        mTvCancel = view.findViewById(R.id.dialog_tip_tv_cancel);
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

        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onVersionCancelClick();
                }
            }
        });

//        mTvConfirm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null) {
//                    listener.onConfirmClick();
//                }
//            }
//        });

    }

    public void setTipMessageText(String text){
        mTvMessage.setText(text);
    }
    public void setTipCacnleText(String text, @ColorInt int color){
        mTvCancel.setText(text);
//        mTvConfirm.setTextColor(color);
    }
    public void setTipConfirmText(String text,@ColorInt int color){
//        mTvConfirm.setText(text);
//        mTvConfirm.setTextColor(color);
    }

    public void show(){
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void dismiss(){
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    public void destory(){
        if(mDialog != null){
            mDialog.cancel();
            mDialog = null;
        }
    }

    public void setVersionDialogUtilsClickListener(VersionDialogUtilsClickListener listener){
        this.listener = listener;
    }

    public interface VersionDialogUtilsClickListener {
        void onVersionCancelClick();

        void onVersionConfirmClick();
    }

}
