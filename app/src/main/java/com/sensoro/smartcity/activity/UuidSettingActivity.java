package com.sensoro.smartcity.activity;

import android.os.Bundle;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IUuidSettingActivityView;
import com.sensoro.smartcity.presenter.UuidSettingActivityPresenter;

import butterknife.ButterKnife;

public class UuidSettingActivity extends BaseActivity<IUuidSettingActivityView, UuidSettingActivityPresenter> implements IUuidSettingActivityView {

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_uuid_setting);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {

    }

    @Override
    protected UuidSettingActivityPresenter createPresenter() {
        return new UuidSettingActivityPresenter();
    }
}
