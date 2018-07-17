package com.sensoro.smartcity.activity;


import android.os.Bundle;

import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IDeployPhotoView;
import com.sensoro.smartcity.presenter.DeployPhotoActivityPresenter;

public class DeployPhotoActivity extends BaseActivity<IDeployPhotoView, DeployPhotoActivityPresenter> implements
        IDeployPhotoView {
    @Override
    protected void onCreateInit(Bundle savedInstanceState) {

    }

    @Override
    protected DeployPhotoActivityPresenter createPresenter() {
        return new DeployPhotoActivityPresenter();
    }
}
