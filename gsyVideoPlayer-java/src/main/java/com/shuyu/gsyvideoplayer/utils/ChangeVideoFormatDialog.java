package com.shuyu.gsyvideoplayer.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.shuyu.gsyvideoplayer.R;

/**
 * 更改视频格式
 */
public class ChangeVideoFormatDialog {
    private Dialog dialog;
    private View inflate;
    private TextView flvformatTv;
    private TextView hlsformatTv;

    public void showChangeVideoFormatDialog(Context context, int selectedPos, OnChangeVideoFormatDialogListener listener) {
        //自定义dialog显示布局
        inflate = LayoutInflater.from(context).inflate(R.layout.layout_changevideoformatdialog, null);
        flvformatTv = inflate.findViewById(R.id.flvformat_tv);
        hlsformatTv = inflate.findViewById(R.id.hlsformat_tv);
        //自定义dialog显示风格
        dialog = new Dialog(context, R.style.change_video_formate_dialog_style);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(inflate);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.RIGHT;
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);


        flvformatTv.setOnClickListener(v -> {

            if (null != listener) {
                listener.onChangeVideoItemClick(0);
                flvformatTv.setTextColor(context.getResources().getColor(R.color.c_1dbb99));
                hlsformatTv.setTextColor(context.getResources().getColor(R.color.white));
//                inflate.postDelayed(() -> dialog.dismiss(), 200);

            }

        });
        hlsformatTv.setOnClickListener(v -> {

            if (null != listener) {
                listener.onChangeVideoItemClick(1);
                flvformatTv.setTextColor(context.getResources().getColor(R.color.white));
                hlsformatTv.setTextColor(context.getResources().getColor(R.color.c_1dbb99));
//                inflate.postDelayed(() -> dialog.dismiss(), 200);
            }
        });
        dialog.show();
        if (selectedPos >= 0) {

            if (selectedPos == 0) {
                flvformatTv.setTextColor(context.getResources().getColor(R.color.c_1dbb99));
                hlsformatTv.setTextColor(context.getResources().getColor(R.color.white));

            } else if (selectedPos == 1) {

                flvformatTv.setTextColor(context.getResources().getColor(R.color.white));
                hlsformatTv.setTextColor(context.getResources().getColor(R.color.c_1dbb99));
            }
        }


    }

    public void disMiss() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }


    public interface OnChangeVideoFormatDialogListener {
        public void onChangeVideoItemClick(int position);
    }
}
