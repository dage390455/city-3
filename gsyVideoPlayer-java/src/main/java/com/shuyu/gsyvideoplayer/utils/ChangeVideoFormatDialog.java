package com.shuyu.gsyvideoplayer.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
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
        dialog.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        dialog.setContentView(inflate);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.RIGHT;
        wlp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wlp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wlp);


        dialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        //布局位于状态栏下方
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        //全屏
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        //隐藏导航栏
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                if (Build.VERSION.SDK_INT >= 19) {
                    uiOptions |= 0x00001000;
                } else {
                    uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
                }
                dialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
            }
        });
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
