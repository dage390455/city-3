package com.sensoro.common.widgets.slideverify;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.sensoro.common.R;

import java.lang.ref.WeakReference;

public class SlidePopUtils {
    private SliderVertifyDialog mSliderVertifyDialog;
    private WeakReference<Activity> activity;
    private String title,desc;

    private boolean cancelable = false;
    Button btn_cancel;
    SlideVerifity mSlideVerifity;
    TextView tv_title,tv_desc;


    public SlidePopUtils(){

    }




    public  void showDialog(Activity mActivity) {
        if(mSliderVertifyDialog==null){
            activity=new WeakReference<>(mActivity);
            View view = View.inflate(activity.get(), R.layout.sliderverification_dialog, null);
            tv_title=view.findViewById(R.id.tv_title);
            tv_desc=view.findViewById(R.id.tv_desc);
            mSliderVertifyDialog = new SliderVertifyDialog(activity.get(), view,R.style.SliderVertifyDialogStyle);
            mSliderVertifyDialog.setCancelable(cancelable);
            mSliderVertifyDialog.setCanceledOnTouchOutside(cancelable);
            mSlideVerifity=view.findViewById(R.id.svf);
            btn_cancel=view.findViewById(R.id.btn_cancel);
            btn_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                        dismissDialog();
                }
            });

            mSlideVerifity.setCaptchaListener(new SlideVerifity.SlideVerifityListener() {
                @Override
                public String onAccess(long time) {
                    if (listener != null) {
                        listener.onAccess(time);
                    }

                    return mActivity.getResources().getString(R.string.slide_dialog_success);
                }

                @Override
                public String onFailed(int count) {
                    if (listener != null) {
                        listener.onFailed(count);
                    }

                    return mActivity.getResources().getString(R.string.slide_dialog_failed);
                }

                @Override
                public String onMaxFailed() {
                    if (listener != null) {
                        listener.onMaxFailed();
                    }
                    return mActivity.getResources().getString(R.string.slide_dialog_failed_maxcount);
                }

            });
        };

        mSliderVertifyDialog.show();
        tv_title.setText(title);
        tv_desc.setText(desc);
        mSlideVerifity.reset(true);


    }

    VerifityResultListener  listener;
    public VerifityResultListener getListener() {
        return listener;
    }


    public SlidePopUtils setListener(VerifityResultListener listener) {
        this.listener = listener;
        return this;
    }





    public void dismissDialog() {
        if (mSliderVertifyDialog != null) {
            mSliderVertifyDialog.dismiss();
        }
    }

    public SlidePopUtils setTitle(String title){
        this.title = title;
        if (mSliderVertifyDialog != null) {
            tv_title.setText(title);
        }
        return this;
    }

    public SlidePopUtils setDesc(String desc) {
        this.desc = desc;
        if (mSliderVertifyDialog != null) {
            tv_desc.setText(desc);
        }
        return this;
    }

    public void setCancelable(boolean cancelable) {
        this.cancelable = cancelable;
        if (mSliderVertifyDialog != null) {
            mSliderVertifyDialog.setCancelable(cancelable);
            mSliderVertifyDialog.setCanceledOnTouchOutside(cancelable);
        }
    }

    private SliderVertifyDialog getDialog() {
        if (mSliderVertifyDialog != null) {
            return mSliderVertifyDialog;
        }
        return null;
    }

    public  void destroySlideVerifyDialog() {
        if (mSliderVertifyDialog != null) {
            mSliderVertifyDialog.cancel();
            mSliderVertifyDialog = null;
        }
        if (activity != null) {
            activity.clear();
            activity = null;
        }
    }


    public interface VerifityResultListener {

        /**
         * Called when captcha access.
         *
         * @param time cost of access time
         * @return text to show,show default when return null
         */
        void onAccess(long time);

        /**
         * Called when captcha failed.
         *
         * @param failCount fail count
         * @return text to show,show default when return null
         */
        void onFailed(int failCount);

        /**
         * Called when captcha failed
         *
         * @return text to show,show default when return null
         */
        void onMaxFailed();

    }
}
