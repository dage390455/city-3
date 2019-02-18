package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;

public class TipDeviceUpdateDialogUtils {

    //    private AlertDialog mDialog;
    private final TextView tvDialogTipTitle;
    private final TextView tvDialogUpdateVersion;
    private final TextView tvDialogUpdateDate;
    private final TextView tvDialogUpdate;
    private final ImageView ivCancel;
    private TipDialogUpdateClickListener listener;
    private CustomCornerDialog mDialog;

    public TipDeviceUpdateDialogUtils(Activity activity) {
        View view = View.inflate(activity, R.layout.item_device_update_dialog_tip, null);
        tvDialogTipTitle = view.findViewById(R.id.tv_dialog_tip_title);
        tvDialogUpdateVersion = view.findViewById(R.id.tv_dialog_update_version);
        tvDialogUpdateDate = view.findViewById(R.id.tv_dialog_update_date);
        tvDialogUpdate = view.findViewById(R.id.tv_dialog_update);
        ivCancel = view.findViewById(R.id.iv_cancel);
        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);

        tvDialogUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onUpdateClick();
                }
            }
        });
        ivCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    public void setTipTitleText(String text) {
        tvDialogTipTitle.setText(text);
    }

    public void setTipButtonVisible(boolean isVisible) {
        tvDialogUpdate.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    public void setTipNewVersionText(String text) {
        tvDialogUpdateVersion.setText(text);
    }

    public void setTipVersionDateText(String text) {
        tvDialogUpdateDate.setText(text);
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
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

    public void destory() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
        }
    }

    public void setTipDialogUtilsClickListener(TipDialogUpdateClickListener listener) {
        this.listener = listener;
    }

    public interface TipDialogUpdateClickListener {
        void onUpdateClick();
    }

}
