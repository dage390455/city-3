package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mobstat.StatService;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/8/4.
 */

public class DeployResultActivity extends BaseActivity implements Constants {

    @BindView(R.id.deploy_result_tip_tv)
    TextView tipsTextView;
    @BindView(R.id.deploy_result_sn_tv)
    TextView snTextView;
    @BindView(R.id.deploy_result_name_tv)
    TextView nameTextView;
    @BindView(R.id.deploy_result_lon_tv)
    TextView lonTextView;
    @BindView(R.id.deploy_result_lan_tv)
    TextView lanTextView;
    @BindView(R.id.deploy_result_contact_tv)
    TextView contactTextView;
    @BindView(R.id.deploy_result_content_tv)
    TextView contentTextView;
    @BindView(R.id.deploy_result_status_tv)
    TextView statusTextView;
    @BindView(R.id.deploy_result_signal_tv)
    TextView signalTextView;
    @BindView(R.id.deploy_result_update_tv)
    TextView updateTextView;
    @BindView(R.id.deploy_result_iv)
    ImageView resultImageView;
    private int resultCode = 0;
    private DeviceInfo deviceInfo = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deploy_result);
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
            resultCode = this.getIntent().getIntExtra(EXTRA_SENSOR_RESULT, 0);
            if (resultCode == -1) {
                resultImageView.setImageResource(R.mipmap.ic_deploy_failed);
                tipsTextView.setText(R.string.tips_deploy_not_exist);
            } else {
                deviceInfo = (DeviceInfo)this.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
                String sn = deviceInfo.getSn().toUpperCase();
                String name = deviceInfo.getName();
                String lon = this.getIntent().getStringExtra(EXTRA_SENSOR_LON);
                String lan = this.getIntent().getStringExtra(EXTRA_SENSOR_LAN);
                String contact = getIntent().getStringExtra(EXTRA_SETTING_CONTACT);
                String content = getIntent().getStringExtra(EXTRA_SETTING_CONTENT);
                if (resultCode == 1) {
                    resultImageView.setImageResource(R.mipmap.ic_deploy_success);
                    tipsTextView.setText(R.string.tips_deploy_success);
                } else {
                    resultImageView.setImageResource(R.mipmap.ic_deploy_failed);
                    tipsTextView.setText(R.string.tips_deploy_failed);
                }
                snTextView.setText(getString(R.string.sensor_detail_sn) + "：" + sn);
                nameTextView.setText(getString(R.string.sensor_detail_name) + "：" + name);
                lonTextView.setText(getString(R.string.sensor_detail_lon) + "：" + lon);
                lanTextView.setText(getString(R.string.sensor_detail_lan) + "：" + lan);
                contactTextView.setText(getString(R.string.name) + "：" + (contact == null ? "无" : contact));
                contentTextView.setText(getString(R.string.phone) + "：" + (content == null? "无" : content));
                statusTextView.setText(getString(R.string.sensor_detail_status) + "：" + Constants.DEVICE_STATUS_ARRAY[deviceInfo.getStatus()]);
                if (deviceInfo.getLastUpdatedTime() != null) {
                    updateTextView.setVisibility(View.VISIBLE);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.sss'Z'");
                    Date date = sdf.parse(deviceInfo.getLastUpdatedTime() );
                    updateTextView.setText(getString(R.string.update_time)  + "：" + DateUtil.getFullParseDate(date.getTime()));
                } else {
                    updateTextView.setVisibility(View.GONE);
                }
            }
            refreshSignal(deviceInfo.getUpdatedTime(), deviceInfo.getSignal());
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, R.string.tips_data_error, Toast.LENGTH_SHORT).show();
        }

    }

    private void refreshSignal(long updateTime, String signal) {
        String signal_text = null;
        long time_diff = System.currentTimeMillis() - updateTime;
        if (signal != null && (time_diff < 300000)) {
            switch (signal) {
                case "good":
                    signal_text = "信号质量：优";
                    signalTextView.setBackground(getResources().getDrawable(R.drawable.shape_signal_good));
                    break;
                case "normal":
                    signal_text = "信号质量：良";
                    signalTextView.setBackground(getResources().getDrawable(R.drawable.shape_signal_normal));
                    break;
                case "bad":
                    signal_text = "信号质量：差";
                    signalTextView.setBackground(getResources().getDrawable(R.drawable.shape_signal_bad));
                    break;
            }
        } else {
            signal_text = "无信号";
            signalTextView.setBackground(getResources().getDrawable(R.drawable.shape_signal_none));
        }
        signalTextView.setText(signal_text);
    }

    @Override
    protected boolean isNeedSlide() {
        return false;
    }

    @OnClick(R.id.deploy_result_back)
    public void back() {
        gotoContinue();
    }

    @OnClick(R.id.deploy_result_continue_btn)
    public void gotoContinue() {
        Intent intent = new Intent();
        if (resultCode == 1 && deviceInfo != null) {
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
            intent.putExtra(EXTRA_CONTAINS_DATA, true);
        } else {
            intent.putExtra(EXTRA_CONTAINS_DATA, false);
        }
        setResult(RESULT_CODE_DEPLOY, intent);
        this.finish();
    }

    @OnClick(R.id.deploy_result_back_home)
    public void backHome() {
        Intent intent = new Intent();
        if (resultCode == 1 && deviceInfo != null) {
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
        }
        setResult(RESULT_CODE_MAP, intent);
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            gotoContinue();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

}
