package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.TextureMapView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IMonitorPointMapActivityView;
import com.sensoro.smartcity.presenter.MonitorPointMapActivityPresenter;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MonitorPointMapActivity extends BaseActivity<IMonitorPointMapActivityView, MonitorPointMapActivityPresenter> implements IMonitorPointMapActivityView {
    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.tm_monitor_map)
    TextureMapView tmMonitorMap;
    @BindView(R.id.iv_monitor_map_location)
    ImageView ivMonitorMapLocation;
    @BindView(R.id.tv_monitor_map_share)
    TextView tvMonitorMapShare;
    @BindView(R.id.tv_monitor_map_guide)
    TextView tvMonitorMapGuide;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_monitor_point_map);
        ButterKnife.bind(this);
        tmMonitorMap.onCreate(savedInstanceState);// 此方法必须重写
        initView();
        mPresenter.initData(mActivity);
    }

    private void initView() {
        includeTextTitleTvTitle.setText(R.string.location_navigation);
        includeTextTitleTvSubtitle.setText(R.string.location_confirm);
        setPositionCalibrationVisible(false);
        mPresenter.getMap(tmMonitorMap.getMap());
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        tmMonitorMap.onLowMemory();
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        tmMonitorMap.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        tmMonitorMap.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        tmMonitorMap.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
//        if (mProgressUtils != null) {
//            mProgressUtils.destroyProgress();
//            mProgressUtils = null;
//        }
        super.onDestroy();
        tmMonitorMap.onDestroy();
    }

    @Override
    protected MonitorPointMapActivityPresenter createPresenter() {
        return new MonitorPointMapActivityPresenter();
    }

    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.iv_monitor_map_location, R.id.tv_monitor_map_share, R.id.tv_monitor_map_guide, R.id.include_text_title_tv_subtitle})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finishAc();
                break;
            case R.id.iv_monitor_map_location:
                mPresenter.backToCurrentLocation();
                break;
            case R.id.tv_monitor_map_share:
                mPresenter.doDetailShare();
                break;
            case R.id.tv_monitor_map_guide:
                mPresenter.doNavigation();
                break;
            case R.id.include_text_title_tv_subtitle:
                mPresenter.doPositionConfirm();
                break;

        }
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

    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void setPositionCalibrationVisible(boolean isVisible) {
        includeTextTitleTvSubtitle.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
