package com.sensoro.smartcity.activity;

import android.os.Bundle;

import com.sensoro.smartcity.R;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IEarlyWarningThresholdSettingActivityView;
import com.sensoro.smartcity.presenter.EarlyWarningThresholdSettingActivityPresenter;

import butterknife.ButterKnife;

public class EarlyWarningThresholdSettingActivity extends BaseActivity<IEarlyWarningThresholdSettingActivityView, EarlyWarningThresholdSettingActivityPresenter> implements IEarlyWarningThresholdSettingActivityView {

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_ear_warn_threshold_setting);
        ButterKnife.bind(this);
        iniView();
        mPresenter.initData(mActivity);
    }

    private void iniView() {

    }

    @Override
    protected EarlyWarningThresholdSettingActivityPresenter createPresenter() {
        return new EarlyWarningThresholdSettingActivityPresenter();
    }
}
