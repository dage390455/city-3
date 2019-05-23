package com.sensoro.smartcity.widget;

import android.content.Context;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;

/**
 * Created by sensoro on 17/11/14.
 */

public class SensoroHomeAlarmView extends LinearLayout implements View.OnClickListener, Constants {
    private float lastX;
    private float lastY;
    private Context mContext;
    private Animation showAnimation;
    private Animation dismissAnimation;
    private View mView;
    private ImageView ivHomeAlarmClose;
    private LinearLayout llHomeAlarmCheck;

    public SensoroHomeAlarmView(Context context) {
        super(context);
        this.mContext = context;
    }

    public void onDestroyPop() {
        if (showAnimation != null) {
            showAnimation.cancel();
        }
        if (dismissAnimation != null) {
            dismissAnimation.cancel();
        }
    }

    public SensoroHomeAlarmView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public SensoroHomeAlarmView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void init() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_home_alarm_popup, this);
        ivHomeAlarmClose = (ImageView) mView.findViewById(R.id.iv_home_alarm_close);
        llHomeAlarmCheck = (LinearLayout) mView.findViewById(R.id.ll_home_alarm_check);
        ivHomeAlarmClose.setOnClickListener(this);
        llHomeAlarmCheck.setOnClickListener(this);
        showAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alarm_fadein);
        //得到一个LayoutAnimationController对象；
        showAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        dismissAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alarm_fadeout);
        //得到一个LayoutAnimationController对象；
        dismissAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void show(OnSensoroHomeAlarmViewListener onSensoroHomeAlarmViewListener) {
        this.onSensoroHomeAlarmViewListener = onSensoroHomeAlarmViewListener;
        setVisibility(View.VISIBLE);
        this.startAnimation(showAnimation);
    }

    private OnSensoroHomeAlarmViewListener onSensoroHomeAlarmViewListener;

    public void dismiss() {
        if (this.getVisibility() == VISIBLE) {
            this.startAnimation(dismissAnimation);
        }
    }

    public interface OnSensoroHomeAlarmViewListener {
        void onAlarmCheckClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_home_alarm_close:
                dismiss();
                break;
            case R.id.ll_home_alarm_check:
                if (onSensoroHomeAlarmViewListener != null) {
                    onSensoroHomeAlarmViewListener.onAlarmCheckClick();
                }
                dismiss();
                break;
        }
    }
}
