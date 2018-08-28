package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.TimerShaftAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IAlarmDetailActivityView;
import com.sensoro.smartcity.presenter.AlarmDetailActivityPresenter;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.SensoroToast;
import com.sensoro.smartcity.widget.popup.SensoroPopupAlarmView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/11/14.
 */

public class AlarmDetailActivity extends BaseActivity<IAlarmDetailActivityView, AlarmDetailActivityPresenter>
        implements IAlarmDetailActivityView, View.OnClickListener, View.OnTouchListener, TimerShaftAdapter
        .OnPhotoClickListener {


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
    private ProgressUtils mProgressUtils;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_alarm_detail);
        ButterKnife.bind(mActivity);
        initView();
        mPresenter.initData(mActivity);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mPresenter.refreshData();
    }

    @Override
    protected AlarmDetailActivityPresenter createPresenter() {
        return new AlarmDetailActivityPresenter();
    }

    private void initView() {
        try {
            mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
            mAlarmPopupView.setOnPopupCallbackListener(mPresenter);
            mAlarmPopupView.setDialog(mActivity);
            confirmTextView.setOnClickListener(this);
            timerShaftAdapter = new TimerShaftAdapter(mActivity, new TimerShaftAdapter
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
            timerShaftAdapter.setOnPhotoClickListener(this);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        handlerActivityResult(requestCode, resultCode, data);
    }

    public void handlerActivityResult(int requestCode, int resultCode, Intent data) {
        if (mAlarmPopupView != null) {
            mAlarmPopupView.handlerActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void showConfirmPopup(boolean isReConfirm) {
        mAlarmPopupView.show(mShadowView);
    }

    @Override
    public void dismissConfirmPopup() {
        mAlarmPopupView.dismiss();
    }

    @Override
    public void setUpdateButtonClickable(boolean canClick) {
        if (mAlarmPopupView != null) {
            mAlarmPopupView.setUpdateButtonClickable(canClick);
        }
    }

    @Override
    protected void onDestroy() {
        if (mAlarmPopupView != null) {
            mAlarmPopupView.onDestroyPop();
        }
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroy();
    }

    @OnClick(R.id.alarm_detail_back)
    public void back() {
        mPresenter.doBack();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mAlarmPopupView.getVisibility() == View.VISIBLE) {
                mAlarmPopupView.dismiss();
            } else {
                back();
            }
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_detail_confirm_status:
                mPresenter.showConfirmPopup();
                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.alarm_detail_confirm_status:
                mPresenter.showConfirmPopup();
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);
    }

    @Override
    public void finishAc() {
        mActivity.finish();
    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mActivity.startActivityForResult(intent, requestCode);
    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {
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
            case Constants.DISPLAY_STATUS_MIS_DESCRIPTION:
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
            case Constants.DISPLAY_STATUS_RISKS:
                confirmTextView.setText(R.string.confirming_again);
//                    confirmTextView.setVisibility(View.GONE);
                displayStatusTextView.setVisibility(View.VISIBLE);
                displayStatusTextView.setText(R.string.alarm_risk);
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

    @Override
    public void showProgressDialog() {
        mProgressUtils.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.INSTANCE.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void onPhotoItemClick(int position, List<String> images) {
        mPresenter.clickPhotoItem(position, images);
    }
}
