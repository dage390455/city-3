package com.sensoro.nameplate.activity;

import android.os.Bundle;

import com.sensoro.common.base.BaseActivity;
import com.sensoro.nameplate.IMainViews.INameplateDetailActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.presenter.NameplateDetailActivityPresenter;

public class NameplateDetailActivity extends BaseActivity<INameplateDetailActivityView, NameplateDetailActivityPresenter> implements INameplateDetailActivityView {

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_nameplate_detail);

    }

    @Override
    protected NameplateDetailActivityPresenter createPresenter() {
        return new NameplateDetailActivityPresenter();
    }
}
