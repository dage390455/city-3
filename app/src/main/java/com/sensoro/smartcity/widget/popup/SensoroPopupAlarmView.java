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
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.server.response.CityObserver;
import com.sensoro.smartcity.server.response.DeviceAlarmItemRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.SensoroToast;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by sensoro on 17/11/14.
 */

public class SensoroPopupAlarmView extends LinearLayout implements View.OnClickListener, Constants {

    private Context mContext;
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

    public void show(DeviceAlarmLogInfo deviceAlarmLogInfo, SensoroShadowView shadowView, OnPopupCallbackListener
            listener) {
        this.mShadowView = shadowView;
        this.mShadowView.setVisibility(VISIBLE);
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
        String id = deviceAlarmLogInfo.get_id();
        String remark = remarkEditText.getText().toString();
//        byte[] bytes = new byte[0];
//        try {
//            bytes = remark.getBytes("UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        if (bytes.length > 30) {
//            Toast.makeText(mContext, "最大不能超过32个字符", Toast.LENGTH_SHORT).show();
//            return;
//        }
        if (remark.length() > 30) {
            toastShort("最大不能超过30个字符");
            return;
        }
        RetrofitServiceHelper.INSTANCE.doAlarmConfirm(id, displayStatus,
                remark).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmItemRsp>() {


            @Override
            public void onCompleted() {
                dismiss();
            }

            @Override
            public void onNext(DeviceAlarmItemRsp deviceAlarmItemRsp) {
                if (deviceAlarmItemRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {
                    DeviceAlarmLogInfo deviceAlarmLogInfo = deviceAlarmItemRsp.getData();
                    toastShort(mContext.getResources().getString(R.string.tips_commit_success));
                    mListener.onPopupCallback(deviceAlarmLogInfo);
                } else {
                    toastShort(mContext.getResources().getString(R.string.tips_commit_failed));
                }
            }

            @Override
            public void onErrorMsg(String errorMsg) {
                dismiss();
                toastShort(errorMsg);
            }
        });
//        NetUtils.INSTANCE.getServer().doAlarmConfirm(id, displayStatus,
//                remark, new Response.Listener<DeviceAlarmItemRsp>() {
//                    @Override
//                    public void onResponse(DeviceAlarmItemRsp response) {
//                        if (response.getErrcode() == ResponseBase.CODE_SUCCESS) {
//                            DeviceAlarmLogInfo deviceAlarmLogInfo = response.getData();
//                            Toast.makeText(mContext, R.string.tips_commit_success, Toast.LENGTH_SHORT).show();
//                            mListener.onPopupCallback(deviceAlarmLogInfo);
//                            dismiss();
//                        } else {
//                            Toast.makeText(mContext, R.string.tips_commit_failed, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        if (error.networkResponse != null) {
//                            String reason = new String(error.networkResponse.data);
//                            try {
//                                JSONObject jsonObject = new JSONObject(reason);
//                                Toast.makeText(mContext, jsonObject.getString("errmsg"), Toast.LENGTH_SHORT).show();
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            } catch (Exception e) {
//
//                            }
//                        } else {
//                            Toast.makeText(mContext, R.string.tips_network_error, Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
    }

    private void toastShort(String msg){
        SensoroToast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
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
        void onPopupCallback(DeviceAlarmLogInfo deviceAlarmLogInfo);
    }
}
