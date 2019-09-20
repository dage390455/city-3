package com.sensoro.forestfire.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.utils.ScreenUtils;
import com.sensoro.common.widgets.BoldTextView;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.R2;
import com.sensoro.forestfire.imainviews.IForestFireCameraDetailActivityView;
import com.sensoro.forestfire.presenter.ForestFireCameraDetailActivityPresenter;
import com.sensoro.forestfire.widgets.GestureMapView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @Author: jack
 * 时  间: 2019-09-17
 * 包  名: com.sensoro.forestfire.activity
 * 简  述: <功能简述:森林防火管理监测点详情>
 */

@Route(path = ARouterConstants.ACTIVITY_FORESTFIRE_CAMERA_DETAIL)
public class ForestFireCameraDetailActivity extends BaseActivity<IForestFireCameraDetailActivityView, ForestFireCameraDetailActivityPresenter>
        implements IForestFireCameraDetailActivityView {


    @BindView(R2.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R2.id.include_text_title_tv_title)
    BoldTextView includeTextTitleTvTitle;
    @BindView(R2.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R2.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R2.id.tv_forest_fire_camera_name)
    TextView tvForestFireCameraName;
    @BindView(R2.id.tv_forest_fire_camera_type)
    TextView tvForestFireCameraType;
    @BindView(R2.id.tv_forest_fire_camera_time)
    TextView tvForestFireCameraTime;

    @BindView(R2.id.tv_forest_fire_camera_detail_device_sn)
    TextView tvForestFireCameraDetailDeviceSn;
    @BindView(R2.id.tv_forest_fire_camera_detail_device_gatway)
    TextView tvForestFireCameraDetailDeviceGatway;
    @BindView(R2.id.tv_location)
    TextView tvLocation;
    @BindView(R2.id.tv_modify)
    TextView tvModify;
    @BindView(R2.id.ll_forest_fire_camera_detail_device_location)
    LinearLayout llForestFireCameraDetailDeviceLocation;
    @BindView(R2.id.textureMapView)
    TextureMapView textureMapView;



    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forest_fire_camera_detail);
        ButterKnife.bind(this);
        textureMapView.onCreate(savedInstanceState);
        initMapSetting();

        mPresenter.initData(mActivity);
    }

    private void initMapSetting() {
        textureMapView.getLayoutParams().height = ScreenUtils.getScreenWidth(mActivity);
        UiSettings mUiSettings = textureMapView.getMap().getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setScrollGesturesEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setTiltGesturesEnabled(false);
    }

    @Override
    protected ForestFireCameraDetailActivityPresenter createPresenter() {
        return new ForestFireCameraDetailActivityPresenter();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        textureMapView.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        textureMapView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        textureMapView.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        textureMapView.onSaveInstanceState(outState);

    }

    @Override
    public void updateTitle(String title) {
        includeTextTitleTvTitle.setText(title);
    }

    @Override
    public void updateCameraName(String name) {
        tvForestFireCameraName.setText(name);
    }

    @Override
    public void updateCameraType(String type) {
        tvForestFireCameraType.setText(type);
    }

    @Override
    public void updateTime(String time) {
        tvForestFireCameraTime.setText(time);
    }


    @Override
    public void updateDeviceSN(String sn) {
        tvForestFireCameraDetailDeviceSn.setText(sn);
    }

    @Override
    public void updateGateway(String gateway) {
        tvForestFireCameraDetailDeviceGatway.setText(gateway);
    }

    @Override
    public void updateLocation(double lon, double lat) {
        tvLocation.setText(lon + "；" + lat);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick({R2.id.include_text_title_imv_arrows_left, R2.id.include_text_title_tv_subtitle,R2.id.ll_forest_fire_camera_detail_device_location})
    public void onViewClicked(View view) {
        int viewID=view.getId();

        if(viewID==R.id.include_text_title_imv_arrows_left){
            finish();
        }else if(viewID==R.id.include_text_title_tv_subtitle){
            mPresenter.startHistoryActivity();
        }else if(viewID==R.id.ll_forest_fire_camera_detail_device_location){
            mPresenter.startLocationActivity();
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.REQUEST_FOREST_DETAIL_LOCATION){
            if(resultCode== Activity.RESULT_OK){
                mPresenter.freshLocation(data);
            }
        }
    }
}
