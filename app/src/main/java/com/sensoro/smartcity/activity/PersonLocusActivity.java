package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.MarkerOptions;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IPersonLocusView;
import com.sensoro.smartcity.presenter.PersonLocusPresenter;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PersonLocusActivity extends BaseActivity<IPersonLocusView, PersonLocusPresenter>
        implements IPersonLocusView ,AMap.OnMapLoadedListener{

    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.mv_ac_person_locus)
    MapView mvAcPersonLocus;
    private ProgressUtils mProgressUtils;
    private AMap mMap;

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_person_locus);
        ButterKnife.bind(this);
        Log.e("cxy",":开始加载::"+System.currentTimeMillis());
        mvAcPersonLocus.onCreate(savedInstanceState);
        initView();
        mPresenter.initData(mActivity);
    }


    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(this).build());

        initMap();
    }

    private void initMap() {
        mMap = mvAcPersonLocus.getMap();
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setLogoBottomMargin(-100);
        mMap.setMapCustomEnable(true);
        mMap.setOnMapLoadedListener(this);
        mMap.moveCamera(CameraUpdateFactory.zoomTo(15f));
    }

    @Override
    protected PersonLocusPresenter createPresenter() {
        return new PersonLocusPresenter();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public void startAC(Intent intent) {

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
    public void showProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.showProgress();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.dismissProgress();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mvAcPersonLocus.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mvAcPersonLocus.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mvAcPersonLocus.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
        }
        super.onDestroy();
        mvAcPersonLocus.onDestroy();
    }


    @Override
    public void onMapLoaded() {
        Log.e("cxy",":加载结束::"+System.currentTimeMillis());
    }

    @Override
    public void setMapCenter(CameraUpdate cameraUpdate) {
        if (mMap != null) {
            mMap.moveCamera(cameraUpdate);
        }
    }

    @Override
    public void addMarker(MarkerOptions markerOptions) {
        if (mMap != null) {
            mMap.addMarker(markerOptions);
        }
    }
}
