package com.sensoro.forestfire.activity;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.imainviews.IForestFireCameraDetailActivityView;
import com.sensoro.forestfire.presenter.ForestFireCameraDetailActivityPresenter;

/**
 * @Author: jack
 * 时  间: 2019-09-17
 * 包  名: com.sensoro.forestfire.activity
 * 简  述: <功能简述:森林防火管理监测点详情>
 */

@Route(path = ARouterConstants.ACTIVITY_FORESTFIRE_CAMERA_LIST)
public class ForestFireCameraDetailActivity extends BaseActivity<IForestFireCameraDetailActivityView, ForestFireCameraDetailActivityPresenter>
        implements IForestFireCameraDetailActivityView, View.OnClickListener {


    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forest_fire_camera_detail);
    }

    @Override
    protected ForestFireCameraDetailActivityPresenter createPresenter() {
        return null;
    }
}
