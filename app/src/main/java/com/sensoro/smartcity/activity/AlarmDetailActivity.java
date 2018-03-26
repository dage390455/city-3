package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.adapter.TimerShaftAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceAlarmLogInfo;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.SensoroShadowView;
import com.sensoro.smartcity.widget.popup.SensoroPopupAlarmView;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/11/14.
 */

public class AlarmDetailActivity extends BaseActivity implements Constants, SensoroPopupAlarmView.OnPopupCallbackListener, View.OnClickListener, View.OnTouchListener {


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
    private DeviceAlarmLogInfo deviceAlarmLogInfo;
    private TimerShaftAdapter timerShaftAdapter;
    private List<AlarmInfo.RecordInfo> mList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_detail);
        ButterKnife.bind(this);
        init();
        StatusBarCompat.setStatusBarColor(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        StatService.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    private void init() {
        try {
            confirmTextView.setOnClickListener(this);
            confirmTextView.setOnTouchListener(this);
            deviceAlarmLogInfo = (DeviceAlarmLogInfo) getIntent().getSerializableExtra(EXTRA_ALARM_INFO);
            if (deviceAlarmLogInfo.getDeviceName() != null) {
                nameTextView.setText(deviceAlarmLogInfo.getDeviceName().equals("") ? deviceAlarmLogInfo.getDeviceSN() : deviceAlarmLogInfo.getDeviceName());
            } else {
                nameTextView.setText(deviceAlarmLogInfo.getDeviceSN());
            }
            dateTextView.setText(DateUtil.getFullParseDate(deviceAlarmLogInfo.getUpdatedTime()));
            AlarmInfo.RecordInfo []recordInfoArray = deviceAlarmLogInfo.getRecords();
            for (int i = 0; i < recordInfoArray.length; i++) {
                AlarmInfo.RecordInfo recordInfo = recordInfoArray[i];
                if (recordInfo.getType().equals("recovery")) {
                    statusTextView.setTextColor(getResources().getColor(R.color.sensoro_normal));
                    statusTextView.setText("于" + DateUtil.getFullParseDate(recordInfo.getUpdatedTime()) + "恢复正常");
                    statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.shape_status_normal));
                    break;
                } else {
                    statusTextView.setTextColor(getResources().getColor(R.color.sensoro_alarm));
                    statusTextView.setText(R.string.alarming);
                    statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.shape_status_alarm));
                }
            }
            switch (deviceAlarmLogInfo.getDisplayStatus()) {
                case DISPLAY_STATUS_CONFIRM:
                    confirmTextView.setVisibility(View.VISIBLE);
                    displayStatusTextView.setVisibility(View.GONE);
                    confirmTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                    break;
                case DISPLAY_STATUS_ALARM:
                    confirmTextView.setVisibility(View.GONE);
                    displayStatusTextView.setVisibility(View.VISIBLE);
                    displayStatusTextView.setText(R.string.true_alarm);
                    break;
                case DISPLAY_STATUS_MISDESCRIPTION:
                    confirmTextView.setVisibility(View.GONE);
                    displayStatusTextView.setVisibility(View.VISIBLE);
                    displayStatusTextView.setText(R.string.misdescription);
                    break;
                case DISPLAY_STATUS_TEST:
                    confirmTextView.setVisibility(View.GONE);
                    displayStatusTextView.setVisibility(View.VISIBLE);
                    displayStatusTextView.setText(R.string.alarm_test);
                    break;
                default:
                    break;
            }
            if (recordInfoArray != null) {
                for (int i = recordInfoArray.length - 1; i >= 0; i--) {
                    mList.add(recordInfoArray[i]);
                }
            }

            timerShaftAdapter = new TimerShaftAdapter(this, mList, new TimerShaftAdapter.OnGroupItemClickListener() {
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
            WidgetUtil.judgeSensorType(getApplicationContext(), detailIvType, deviceAlarmLogInfo.getSensorType());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected boolean isNeedSlide() {
        return true;
    }


    public void refresh() {

        String name = deviceAlarmLogInfo.getDeviceName() == null ? deviceAlarmLogInfo.getDeviceSN() : deviceAlarmLogInfo.getDeviceName();
        nameTextView.setText(name.equals("") ? deviceAlarmLogInfo.getDeviceSN() : name);
        dateTextView.setText(DateUtil.getFullParseDate(deviceAlarmLogInfo.getUpdatedTime()));
        AlarmInfo.RecordInfo []recordInfoArray = deviceAlarmLogInfo.getRecords();
        for (int i = 0; i < recordInfoArray.length; i++) {
            AlarmInfo.RecordInfo recordInfo = recordInfoArray[i];
            if (recordInfo.getType().equals("recovery")) {
                statusTextView.setText("于" + DateUtil.getFullParseDate(recordInfo.getUpdatedTime()) + "恢复正常");
                statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.shape_status_normal));
                break;
            } else {
                statusTextView.setText(R.string.alarming);
                statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.shape_status_alarm));
            }
        }
        if (recordInfoArray != null) {
            mList.clear();
            for (int i = recordInfoArray.length - 1; i >= 0; i--) {
                mList.add(recordInfoArray[i]);
            }
            timerShaftAdapter.setData(mList);
            timerShaftAdapter.notifyDataSetChanged();
        }
    }

    public void showConfirmPopup() {
        SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getApplication();
        mAlarmPopupView.show(sensoroCityApplication, deviceAlarmLogInfo, mShadowView, this);
    }

    @OnClick(R.id.alarm_detail_back)
    public void back() {
        Intent data = new Intent();
        data.putExtra(EXTRA_ALARM_INFO, deviceAlarmLogInfo);
        setResult(RESULT_CODE_ALARM_DETAIL, data);
        finish();
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
        this.deviceAlarmLogInfo = deviceAlarmLogInfo;
        refresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_detail_confirm_status:

                break;
            default:
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()) {
            case R.id.alarm_detail_confirm_status:
                showConfirmPopup();
                break;
            default:
                break;
        }
        return false;
    }
}
