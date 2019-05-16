package com.sensoro.smartcity.widget.dialog;

import android.app.Activity;
import android.content.res.Resources;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.MonthDisplayHelper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sensoro.smartcity.R;

import java.util.Locale;

public class VideoDownloadDialogUtils {

//    private AlertDialog mDialog;
    private final TextView mTvMessage;
    private final TextView mTvCancel;
    private final TextView mTvConfirm;
    private final TextView mTvTip;
    private final ProgressBar mPb;
    private final Activity mActivity;
    private final int colorGray;
    private TipDialogUtilsClickListener listener;
    private CustomCornerDialog mDialog;
    private final String cancelStr;
    private final int colorGreen;
    private final int colorRed;
    private String mVideoSize;

    public VideoDownloadDialogUtils(Activity activity) {
        mActivity = activity;
        cancelStr = mActivity.getString(R.string.cancel);
        View view = View.inflate(activity, R.layout.item_dialog_video_download, null);
        mTvMessage = view.findViewById(R.id.tv_title_item_dialog_video_download);
        mTvCancel = view.findViewById(R.id.tv_cancel_item_dialog_video_download);
        mTvConfirm = view.findViewById(R.id.tv_confirm_item_dialog_video_download);
        mTvTip = view.findViewById(R.id.tv_tip_item_dialog_video_download);
        mPb = view.findViewById(R.id.pb_download_item_dialog_video_download);

        Resources resources = mActivity.getResources();
        colorGreen = resources.getColor(R.color.c_1dbb99);
        colorGray = resources.getColor(R.color.c_a6a6a6);
        colorRed = mActivity.getResources().getColor(R.color.c_f34a4a);
//        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//        builder.setView(view);
//        builder.setCancelable(false);
//        mDialog = builder.create();
//        Window window = mDialog.getWindow();
//        if (window != null) {
//            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }

        mDialog = new CustomCornerDialog(activity, R.style.CustomCornerDialogStyle,view);
        mDialog.setCancelable(false);

        mTvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onCancelClick(mTvConfirm.getVisibility() != View.VISIBLE && cancelStr.equals(mTvCancel.getText().toString()));
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

    public void setTipMessageText(String text){
        mTvMessage.setText(text);
    }
    public void setTipCacnleText(String text, @ColorInt int color){
        mTvCancel.setText(text);
        mTvConfirm.setTextColor(color);
    }
    public void setTipConfirmText(String text,@ColorInt int color){
        mTvConfirm.setText(text);
        mTvConfirm.setTextColor(color);
    }

    public void show(String videoSize){
        if (mDialog != null) {
            mDialog.show();
            mVideoSize = videoSize;
            reset(videoSize);
//            WindowManager m = mDialog.getWindow().getWindowManager();
//            Display d = m.getDefaultDisplay();
//            WindowManager.LayoutParams p = mDialog.getWindow().getAttributes();
//            p.width = d.getWidth() - 100;
//            mDialog.getWindow().setAttributes(p);
        }
    }

    private void reset(String videoSize) {
        mTvCancel.setText(cancelStr);
        mTvConfirm.setVisibility(View.VISIBLE);
        mPb.setProgress(0);
        mPb.setVisibility(View.INVISIBLE);
        mTvTip.setText(String.format(Locale.ROOT,"%s%s",mActivity.getString(R.string.video_size),videoSize));
        mTvTip.setTextColor(colorGray);
        mTvConfirm.setText(R.string.download);
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

    public void setTipDialogUtilsClickListener(TipDialogUtilsClickListener listener){
        this.listener = listener;
    }

    public void setDownloadStartState(String videoSize) {
        mPb.setProgress(0);
        mPb.setVisibility(View.VISIBLE);
        mTvTip.setTextColor(colorGray);
        if (!TextUtils.isEmpty(mVideoSize)) {
            mTvTip.setText(String.format(Locale.ROOT,"%sMB/%s","0",mVideoSize));
        }
        mTvConfirm.setVisibility(View.GONE);
    }

    public void updateDownLoadProgress(int progress, String totalBytesRead, String fileSize) {
        if (TextUtils.isEmpty(mVideoSize)) {
            mTvTip.setText(String.format(Locale.ROOT,"%sMB/%sMB",totalBytesRead,fileSize));
        }else{
            mTvTip.setText(String.format(Locale.ROOT,"%sMB/%s",totalBytesRead,mVideoSize));
        }
        mTvTip.setTextColor(colorGray);
        mPb.setProgress(progress);
    }

    public void doDownloadFinish() {
        mTvTip.setText(mActivity.getString(R.string.download_finish));
        mTvTip.setTextColor(colorGreen);
        mTvCancel.setText(mActivity.getString(R.string.ok));
    }

    public void setDownloadErrorState() {
        mTvTip.setText(R.string.download_failed);
        mTvTip.setTextColor(colorRed);
        mPb.setProgress(0);
        mTvConfirm.setText(R.string.redownload);
        mTvConfirm.setVisibility(View.VISIBLE);
    }

    public boolean isShowing() {
        return mDialog.isShowing();
    }

    public interface TipDialogUtilsClickListener {
        void onCancelClick(boolean isCancelDownload);

        void onConfirmClick();
    }

}
