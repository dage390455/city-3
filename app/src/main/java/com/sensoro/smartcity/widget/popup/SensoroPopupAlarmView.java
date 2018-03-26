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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.response.DeviceAlarmItemRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.widget.SensoroShadowView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sensoro on 17/11/14.
 */

public class SensoroPopupAlarmView extends LinearLayout implements View.OnClickListener , Constants{

    private Context mContext;
    private SensoroCityApplication sensoroCityApplication;
    private DeviceAlarmLogInfo deviceAlarmLogInfo;
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

    public SensoroPopupAlarmView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    public SensoroPopupAlarmView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
    }

    public void init() {
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
        showAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alarm_fadein);   //得到一个LayoutAnimationController对象；
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

        dismissAnimation = AnimationUtils.loadAnimation(mContext, R.anim.alarm_fadeout);   //得到一个LayoutAnimationController对象；
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

    public void show(SensoroCityApplication sensoroCityApplication, DeviceAlarmLogInfo deviceAlarmLogInfo, SensoroShadowView shadowView, OnPopupCallbackListener listener) {
        this.mShadowView = shadowView;
        this.mShadowView.setVisibility(VISIBLE);
        this.sensoroCityApplication = sensoroCityApplication;
        this.deviceAlarmLogInfo = deviceAlarmLogInfo;
        this.mListener = listener;
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

    public void dismiss() {
        if (this.getVisibility() == VISIBLE) {
            this.startAnimation(dismissAnimation);
        }
    }

    public void doAlarmConfirm() {
        sensoroCityApplication.smartCityServer.doAlarmConfirm(deviceAlarmLogInfo.get_id(), displayStatus, remarkEditText.getText().toString(), new Response.Listener<DeviceAlarmItemRsp>() {
            @Override
            public void onResponse(DeviceAlarmItemRsp response) {
                if (response.getErrcode() == ResponseBase.CODE_SUCCESS) {
                    DeviceAlarmLogInfo deviceAlarmLogInfo = response.getData();
                    Toast.makeText(mContext, R.string.tips_commit_success, Toast.LENGTH_SHORT).show();
                    mListener.onPopupCallback(deviceAlarmLogInfo);
                    dismiss();
                } else {
                    Toast.makeText(mContext, R.string.tips_commit_failed, Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null) {
                    String reason = new String(error.networkResponse.data);
                    try {
                        JSONObject jsonObject = new JSONObject(reason);
                        Toast.makeText(mContext, jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(mContext, R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                    Toast.makeText(mContext, R.string.tips_choose_status, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.alarm_popup_true:
                mButton.setBackground(getResources().getDrawable(R.drawable.shape_button));
                trueTextView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_alarm_popup_selected));
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
                testTextView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_alarm_popup_selected));
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
                misTextView.setBackground(mContext.getResources().getDrawable(R.drawable.selector_alarm_popup_selected));
                trueTextView.setTextColor(mContext.getResources().getColor(R.color.c_626262));
                testTextView.setTextColor(mContext.getResources().getColor(R.color.c_626262));
                misTextView.setTextColor(mContext.getResources().getColor(R.color.white));
                displayStatus = DISPLAY_STATUS_MISDESCRIPTION;
                break;
        }
    }

    public interface OnPopupCallbackListener {
         void onPopupCallback(DeviceAlarmLogInfo deviceAlarmLogInfo);
    }
}
