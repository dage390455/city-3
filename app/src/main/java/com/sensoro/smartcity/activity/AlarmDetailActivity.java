package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.TimerShaftAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlarmDetailActivityView;
import com.sensoro.smartcity.presenter.AlarmDetailActivityPresenter;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.popup.SensoroPopupAlarmView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/11/14.
 */

public class AlarmDetailActivity extends BaseActivity<IAlarmDetailActivityView, AlarmDetailActivityPresenter>
        implements IAlarmDetailActivityView, SensoroPopupAlarmView
        .OnPopupCallbackListener, View.OnClickListener, View.OnTouchListener {


    @BindView(R.id.alarm_detail_status_iv)
    ImageView statusImageView;
    @BindView(R.id.alarm_detail_iv_type)
    ImageView detailIvType;
    @BindView(R.id.alarm_detail_display_status)
    TextView displayStatusTextView;
    @BindView(R.id.alarm_detail_status)
    TextView statusTextView;
    @BindView(R.id.alarm_detail_back)
    ImageView backImageView;
    @BindView(R.id.alarm_detail_confirm_status)
    TextView confirmTextView;
    @BindView(R.id.alarm_detail_date)
    TextView dateTextView;
    @BindView(R.id.alarm_detail_name)
    TextView nameTextView;
    @BindView(R.id.alarm_detail_listview)
    ExpandableListView expandableListView;
    @BindView(R.id.alarm_detail_popup_shadow)
    SensoroShadowView mShadowView;
    @BindView(R.id.alarm_detail_popup_view)
    SensoroPopupAlarmView mAlarmPopupView;
    private TimerShaftAdapter timerShaftAdapter;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_alarm_detail);
        ButterKnife.bind(mActivity);
        initView();
        mPrestener.initData(mActivity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPrestener.refreshData();
    }

    @Override
    protected AlarmDetailActivityPresenter createPresenter() {
        return new AlarmDetailActivityPresenter();
    }

    private void initView() {
        try {
            confirmTextView.setOnClickListener(this);
            timerShaftAdapter = new TimerShaftAdapter(mActivity, mPrestener.getList(), new TimerShaftAdapter
                    .OnGroupItemClickListener() {
                @Override
                public void onGroupItemClick(int position, boolean isExpanded) {
                    if (!isExpanded) {
                        expandableListView.expandGroup(position);
                    } else {
                        expandableListView.collapseGroup(position);
                    }

                }
            });
            expandableListView.setAdapter(timerShaftAdapter);
            expandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                    return true;
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void showConfirmPopup(boolean isReConfirm) {
        mAlarmPopupView.show(mPrestener.getDeviceAlarmLogInfo(), isReConfirm, mShadowView, this);
    }

    @Override
    protected void onDestroy() {
        if (mAlarmPopupView != null) {
            mAlarmPopupView.onDestroyPop();
        }
        super.onDestroy();
    }

    @OnClick(R.id.alarm_detail_back)
    public void back() {
        mPrestener.doBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            back();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPopupCallback(DeviceAlarmLogInfo deviceAlarmLogInfo) {
        mPrestener.setDeviceAlarmLogInfo(deviceAlarmLogInfo);
        mPrestener.refreshData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_detail_confirm_status:
                mPrestener.showConfirmPopup();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.alarm_detail_confirm_status:
                mPrestener.showConfirmPopup();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {

    }

    @Override
    public void setIntentResult(int requestCode) {

    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {
        mActivity.setResult(requestCode, data);
    }

    @Override
    public void setNameTextView(String name) {
        nameTextView.setText(name);
    }

    @Override
    public void setDateTextView(String date) {
        dateTextView.setText(date);
    }

    @Override
    public void setStatusInfo(String text, int colorId, int resId) {
        statusTextView.setTextColor(getResources().getColor(colorId));
        statusTextView.setText(text);
        statusImageView.setImageDrawable(getResources().getDrawable(resId));
    }

    @Override
    public void setDisplayStatus(int displayStatus) {
        switch (displayStatus) {
            case Constants.DISPLAY_STATUS_CONFIRM:
//                    confirmTextView.setVisibility(View.VISIBLE);
                confirmTextView.setText(R.string.confirming);
                displayStatusTextView.setVisibility(View.GONE);
                break;
            case Constants.DISPLAY_STATUS_ALARM:
                confirmTextView.setText(R.string.confirming_again);
//                    confirmTextView.setVisibility(View.GONE);
                displayStatusTextView.setVisibility(View.VISIBLE);
                displayStatusTextView.setText(R.string.true_alarm);
                break;
            case Constants.DISPLAY_STATUS_MISDESCRIPTION:
                confirmTextView.setText(R.string.confirming_again);
//                    confirmTextView.setVisibility(View.GONE);
                displayStatusTextView.setVisibility(View.VISIBLE);
                displayStatusTextView.setText(R.string.misdescription);
                break;
            case Constants.DISPLAY_STATUS_TEST:
                confirmTextView.setText(R.string.confirming_again);
//                    confirmTextView.setVisibility(View.GONE);
                displayStatusTextView.setVisibility(View.VISIBLE);
                displayStatusTextView.setText(R.string.alarm_test);
                break;
            default:
                break;
        }
    }

    @Override
    public void updateTimerShaftAdapter(List<AlarmInfo.RecordInfo> recordInfoList) {
        timerShaftAdapter.setData(recordInfoList);
        timerShaftAdapter.notifyDataSetChanged();
    }

    @Override
    public void setSensoroIv(String sensoroType) {
        WidgetUtil.judgeSensorType(mActivity, detailIvType, sensoroType);
    }
}
