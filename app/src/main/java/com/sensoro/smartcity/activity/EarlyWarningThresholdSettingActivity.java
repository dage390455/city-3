package com.sensoro.smartcity.activity;

import android.os.Bundle;

import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IEarlyWarningThresholdSettingActivityView;
import com.sensoro.smartcity.presenter.EarlyWarningThresholdSettingActivityPresenter;

public class EarlyWarningThresholdSettingActivity extends BaseActivity<IEarlyWarningThresholdSettingActivityView, EarlyWarningThresholdSettingActivityPresenter> implements IEarlyWarningThresholdSettingActivityView {
    @Override
    protected void onCreateInit(Bundle savedInstanceState) {

    }

    @Override
    protected EarlyWarningThresholdSettingActivityPresenter createPresenter() {
        return new EarlyWarningThresholdSettingActivityPresenter();
    }
}
