package com.sensoro.forestfire.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.bean.ForestFireCameraBean;
import com.sensoro.common.server.bean.ForestFireCameraDetailInfo;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.utils.ScreenUtils;
import com.sensoro.forestfire.Constants.ForestFireConstans;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.imainviews.IForestFireCameraDetailActivityView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: jack
 * 时  间: 2019-09-17
 * 包  名: com.sensoro.forestfire.presenter
 * 简  述: <功能简述>
 */
public class ForestFireCameraDetailActivityPresenter extends BasePresenter<IForestFireCameraDetailActivityView> {
    private Activity mContext;


    private ForestFireCameraBean mForestFireCameraBean;
    private ForestFireCameraDetailInfo mForestFireCameraDetailInfo;
    private AMap aMap;
    Marker deviceMarker;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        Bundle mBundle = getBundle(mContext);
        if (mBundle != null) {
            mForestFireCameraBean = (ForestFireCameraBean) mBundle.getSerializable(ForestFireConstans.DEVICE_CAMERA_INFO);
            mForestFireCameraDetailInfo = (ForestFireCameraDetailInfo) mBundle.getSerializable(ForestFireConstans.DEVICE_CAMERA_DETAIL);
        }
        getView().updateTitle(mContext.getString(R.string.forest_fire_camera_detail));

        if (null != mForestFireCameraDetailInfo.getList() && mForestFireCameraDetailInfo.getList().size() > 0) {
            ArrayList<ForestFireCameraDetailInfo.MultiVideoInfoBean> multiVideoInfoBeanList=mForestFireCameraDetailInfo.getList().get(0).getMultiVideoInfo();
            if(multiVideoInfoBeanList!=null){
                getView().updateData(multiVideoInfoBeanList);
            }
        }
        EventBus.getDefault().register(this);
    }

    public void initMapSetting(TextureMapView mTextureMapView) {
        aMap = mTextureMapView.getMap();
        mTextureMapView.getLayoutParams().height = ScreenUtils.getScreenWidth(mContext);
        UiSettings mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setScrollGesturesEnabled(false);
        mUiSettings.setRotateGesturesEnabled(false);
        mUiSettings.setTiltGesturesEnabled(false);

        mUiSettings.setTiltGesturesEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(false);

    }

    public void initView() {
        if (mForestFireCameraBean != null) {
            String   cameraName=mForestFireCameraBean.getName();
            if(TextUtils.isEmpty(cameraName)){
                getView().updateCameraName(cameraName);
            }
            getView().updateCameraType(mContext.getString(R.string.forest_fire_camera_detail_device_type_name));
            getView().updateDeviceSN(mForestFireCameraBean.getSn());

            ForestFireCameraBean.ForestGatewayBean forestGateway = mForestFireCameraBean.getForestGateway();
            if (forestGateway != null) {
                String name = forestGateway.getName();
                getView().updateGateway(name);
            }

            getView().updateTime(DateUtil.getStrTimeTodayByDevice(mContext, mForestFireCameraBean.getCreateTime()));
            ForestFireCameraBean.InfoBean info = mForestFireCameraBean.getInfo();
            if (info != null) {
                getView().updateLocation(info.getLongitude(), info.getLatitude());
                updateMap();
            }

            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.deploy_map_cur);
            MarkerOptions markerOption = new MarkerOptions().icon(bitmapDescriptor)
                    .anchor(0.5f, 1)
                    .draggable(true);
            deviceMarker = aMap.addMarker(markerOption);

            deviceMarker.setPosition(new LatLng(info.getLatitude(), info.getLongitude()));
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    public void freshLocation(ForestFireCameraBean result) {
        if (result != null && result.getInfo() != null) {
            mForestFireCameraBean.getInfo().setLocation(result.getInfo().getLocation());
            mForestFireCameraBean.getInfo().setLatitude(result.getInfo().getLatitude());
            mForestFireCameraBean.getInfo().setLongitude(result.getInfo().getLongitude());
            getView().updateLocation(mForestFireCameraBean.getInfo().getLongitude(), mForestFireCameraBean.getInfo().getLatitude());
            updateMap();
        }
    }


    public void updateMap() {
        LatLng mLatLng = new LatLng(mForestFireCameraBean.getInfo().getLatitude(), mForestFireCameraBean.getInfo().getLongitude());
        //参数依次是：视角调整区域的中心点坐标、希望调整到的缩放级别、俯仰角0°~45°（垂直与地图时为0）、偏航角 0~360° (正北方为0)
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng, 16, 30, 0));
        aMap.moveCamera(mCameraUpdate);
        if (deviceMarker != null) {
            deviceMarker.setPosition(mLatLng);
        }
    }

    public void startHistoryActivity() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_SENSOR_SN, mForestFireCameraBean.getSn());
        startActivity(ARouterConstants.ACTIVITY_ALARM_HISTORY_LOG, bundle, mContext);
    }


    public void startLocationActivity() {
        DeployAnalyzerModel deployAnalyzerModel = new DeployAnalyzerModel();
        deployAnalyzerModel.deviceType = Constants.FOREST_FIRE_DEVICE_TYPE;
        deployAnalyzerModel.sn = mForestFireCameraBean.getSn();
        deployAnalyzerModel.mapSourceType = Constants.FOREST_FIRE_DEVICE_DETAIL;
        if (mForestFireCameraBean != null && mForestFireCameraBean.getInfo() != null) {
            deployAnalyzerModel.latLng.add(mForestFireCameraBean.getInfo().getLongitude());
            deployAnalyzerModel.latLng.add(mForestFireCameraBean.getInfo().getLatitude());
        }

        Bundle bundle = new Bundle();
        bundle.putInt(Constants.EXTRA_DEPLOY_CONFIGURATION_ORIGIN_TYPE, Constants.FOREST_FIRE_DEVICE_DETAIL);
        bundle.putSerializable(Constants.EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);

        startActivityForResult(ARouterConstants.ACTIVITY_DEPLOY_MAP, bundle, mContext, Constants.REQUEST_FOREST_DETAIL_LOCATION);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ForestFireCameraBean mForestFireCameraBean) {
        freshLocation(mForestFireCameraBean);
    }


    /**
     * 网络改变状态
     *
     * @param eventData
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        if (code == Constants.NetworkInfo) {
            int data = (int) eventData.data;

            getView().updataAdapterState(data);

        }
    }
}
