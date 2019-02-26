package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sensoro.smartcity.R;

public class MonitorPointDemoDialogUtils {
    //    private AlertDialog mDialog;
    private MonitorPointDemolickListener listener;
    private CustomCornerDialog mDialog;
    private final TextView tvDescription;
    private final TextView tvContent;
    private final TextView tvTitle;
    private final ImageView imvCancel;
    private final TextView tvDemo;

    public MonitorPointDemoDialogUtils(Activity activity) {
        View view = View.inflate(activity, R.layout.item_monitor_point_demo_dialog, null);
        tvTitle = view.findViewById(R.id.tv_dialog_demo_title);
        tvContent = view.findViewById(R.id.tv_dialog_demo_content);
        tvDescription = view.findViewById(R.id.tv_dialog_demo_description);
        tvDemo = view.findViewById(R.id.tv_dialog_demo);
        imvCancel = view.findViewById(R.id.iv_demo_cancel);


        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle, view);

        tvDemo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onConfirmClick();
            }
        });

        imvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    public void setCanceledOnTouchOutside(boolean canceled) {
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(canceled);
        }
    }

    public void setCancelable(boolean flag) {
        if (mDialog != null) {
            mDialog.setCancelable(flag);
        }

    }

    public void setDemoTitleText(String text) {
        if (!TextUtils.isEmpty(text)) {
            tvTitle.setText(text);
        }
    }

    public void setDemoDescription(String description) {
        if (TextUtils.isEmpty(description)) {
            tvDescription.setVisibility(View.GONE);
        }else{
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(description);
        }
    }

    public void setDemoContent(String text) {
        if (!TextUtils.isEmpty(text)) {
            tvContent.setText(text);
        }

    }

    public void setDemoBtnText(String text) {
        if (!TextUtils.isEmpty(text)) {
            tvDemo.setText(text);
        }

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

    public void destory() {
        if (mDialog != null) {
            mDialog.cancel();
            mDialog = null;
        }
    }

    public void setMonitorPointDemoClickListener(MonitorPointDemolickListener listener) {
        this.listener = listener;
    }


    public interface MonitorPointDemolickListener {
        void onConfirmClick();
    }

}
