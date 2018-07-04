package com.sensoro.smartcity.widget.popup;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.SensoroToast;

/**
 * Created by sensoro on 17/11/14.
 */

public class SensoroPopupAlarmView extends LinearLayout implements View.OnClickListener, Constants {

    private Context mContext;
    private OnPopupCallbackListener mListener;
    private Animation showAnimation;
    private Animation dismissAnimation;
    private View mView;
    private SensoroShadowView mShadowView;
    private ImageView closeImageView;
    private TextView trueTextView;
    private TextView misTextView;
    private TextView testTextView;
    private EditText remarkEditText;
    private Button mButton;
    private int displayStatus = DISPLAY_STATUS_CONFIRM;

    public SensoroPopupAlarmView(Context context) {
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

    public SensoroPopupAlarmView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public SensoroPopupAlarmView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    private void init() {
        mView = LayoutInflater.from(mContext).inflate(R.layout.layout_alarm_popup, this);
        trueTextView = (TextView) mView.findViewById(R.id.alarm_popup_true);
        misTextView = (TextView) mView.findViewById(R.id.alarm_popup_mis);
        testTextView = (TextView) mView.findViewById(R.id.alarm_popup_test);
        closeImageView = (ImageView) mView.findViewById(R.id.alarm_popup_close);
        remarkEditText = (EditText) mView.findViewById(R.id.alarm_popup_remark);
        mButton = (Button) mView.findViewById(R.id.alarm_popup_commit);
        mButton.setOnClickListener(this);
        closeImageView.setOnClickListener(this);
        trueTextView.setOnClickListener(this);
        misTextView.setOnClickListener(this);
        testTextView.setOnClickListener(this);
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
                if (mShadowView != null) {
                    mShadowView.setVisibility(GONE);
                }
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

    public void show(SensoroShadowView shadowView) {
        this.mShadowView = shadowView;
        this.mShadowView.setVisibility(VISIBLE);

        this.displayStatus = DISPLAY_STATUS_CONFIRM;
        this.remarkEditText.setText("");
        mButton.setBackground(getResources().getDrawable(R.drawable.shape_button_normal));
        setVisibility(View.VISIBLE);
        this.startAnimation(showAnimation);
        trueTextView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_alarm_popup));
        testTextView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_alarm_popup));
        misTextView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_alarm_popup));
        trueTextView.setTextColor(mContext.getResources().getColor(R.color.c_626262));
        testTextView.setTextColor(mContext.getResources().getColor(R.color.c_626262));
        misTextView.setTextColor(mContext.getResources().getColor(R.color.c_626262));
    }

    public void setOnPopupCallbackListener(OnPopupCallbackListener listener) {
        this.mListener = listener;
    }

    public void dismiss() {
        if (this.getVisibility() == VISIBLE) {
            this.startAnimation(dismissAnimation);
        }
    }

    private void doAlarmConfirm() {
        String remark = remarkEditText.getText().toString();
        if (mListener != null) {
            mListener.onPopupCallback(displayStatus, remark);
        }
    }

    private void toastShort(String msg) {
        SensoroToast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    private void dismissInputMethodManager(View view) {
        InputMethodManager imm = (InputMethodManager) this.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);//从控件所在的窗口中隐藏
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_popup_close:
                dismissInputMethodManager(v);
                remarkEditText.clearFocus();
                dismiss();
                break;
            case R.id.alarm_popup_commit:
                if (displayStatus != DISPLAY_STATUS_CONFIRM) {
                    dismissInputMethodManager(v);
                    remarkEditText.clearFocus();
                    doAlarmConfirm();
                } else {
                    toastShort(mContext.getResources().getString(R.string.tips_choose_status));
                }
                break;
            case R.id.alarm_popup_true:
                mButton.setBackground(getResources().getDrawable(R.drawable.shape_button));
                trueTextView.setBackground(mContext.getResources().getDrawable(R.drawable
                        .selector_alarm_popup_selected));
                testTextView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_alarm_popup));
                misTextView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_alarm_popup));
                trueTextView.setTextColor(mContext.getResources().getColor(R.color.white));
                testTextView.setTextColor(mContext.getResources().getColor(R.color.c_626262));
                misTextView.setTextColor(mContext.getResources().getColor(R.color.c_626262));
                displayStatus = DISPLAY_STATUS_ALARM;
                break;
            case R.id.alarm_popup_test:
                mButton.setBackground(getResources().getDrawable(R.drawable.shape_button));
                trueTextView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_alarm_popup));
                testTextView.setBackground(mContext.getResources().getDrawable(R.drawable
                        .selector_alarm_popup_selected));
                misTextView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_alarm_popup));
                trueTextView.setTextColor(mContext.getResources().getColor(R.color.c_626262));
                testTextView.setTextColor(mContext.getResources().getColor(R.color.white));
                misTextView.setTextColor(mContext.getResources().getColor(R.color.c_626262));
                displayStatus = DISPLAY_STATUS_TEST;
                break;
            case R.id.alarm_popup_mis:
                mButton.setBackground(getResources().getDrawable(R.drawable.shape_button));
                trueTextView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_alarm_popup));
                testTextView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_alarm_popup));
                misTextView.setBackground(mContext.getResources().getDrawable(R.drawable
                        .selector_alarm_popup_selected));
                trueTextView.setTextColor(mContext.getResources().getColor(R.color.c_626262));
                testTextView.setTextColor(mContext.getResources().getColor(R.color.c_626262));
                misTextView.setTextColor(mContext.getResources().getColor(R.color.white));
                displayStatus = DISPLAY_STATUS_MISDESCRIPTION;
                break;
        }
    }

    public interface OnPopupCallbackListener {
        void onPopupCallback(int status, String remark);
    }
}
