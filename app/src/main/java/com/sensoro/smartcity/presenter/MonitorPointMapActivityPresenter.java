package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.TextUtils;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.DeployContactModel;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.EventLoginData;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.utils.ImageFactory;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.DeployMapActivity;
import com.sensoro.smartcity.imainviews.IMonitorPointMapActivityView;
import com.sensoro.common.utils.CityAppUtils;
import com.sensoro.common.utils.LogUtils;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import static com.amap.api.maps.AMap.MAP_TYPE_NORMAL;

public class MonitorPointMapActivityPresenter extends BasePresenter<IMonitorPointMapActivityView> implements
        AMap.OnMapLoadedListener, IOnCreate {

    private Activity mContext;
    private AMap aMap;
    private LatLng destPosition;
    private Bitmap tempUpBitmap;
    private DeviceInfo mDeviceInfo;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mDeviceInfo = (DeviceInfo) mContext.getIntent().getSerializableExtra(Constants.EXTRA_DEVICE_INFO);
        EventLoginData userData = PreferencesHelper.getInstance().getUserData();
        if (userData != null) {
            getView().setPositionCalibrationVisible(userData.hasDevicePositionCalibration);
        }
        initMap();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (tempUpBitmap != null) {
            tempUpBitmap.recycle();
            tempUpBitmap = null;
        }
    }

    public void getMap(AMap map) {
        aMap = map;
    }

    private void initMap() {
        //自定义地图风格
//        setMapCustomStyleFile();
        aMap.getUiSettings().setTiltGesturesEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.getUiSettings().setLogoBottomMargin(-100);
        aMap.setMapCustomEnable(true);
        aMap.setMyLocationEnabled(false);
//        aMap.getUiSettings().setCompassEnabled(true);
        aMap.setOnMapLoadedListener(this);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(16));
//        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.setMapType(MAP_TYPE_NORMAL);
//        aMap.setOnMapTouchListener(this);
//        String styleName = "custom_config.data";
//        aMap.setCustomMapStylePath(mContext.getFilesDir().getAbsolutePath() + "/" + styleName);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.radiusFillColor(Color.argb(25, 73, 144, 226));
        myLocationStyle.strokeWidth(0);
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.deploy_map_location));
//        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);

    }

//    private void setMapCustomStyleFile() {
//        String styleName = "custom_config.data";
//        FileOutputStream outputStream = null;
//        InputStream inputStream = null;
//        String filePath = null;
//        try {
//            inputStream = mContext.getAssets().open(styleName);
//            byte[] b = new byte[inputStream.available()];
//            inputStream.read(b);
//
//            filePath = mContext.getFilesDir().getAbsolutePath();
//            File file = new File(filePath + "/" + styleName);
//            if (!file.exists()) {
//                file.createNewFile();
//            }
//            outputStream = new FileOutputStream(file);
//            outputStream.write(b);
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                if (inputStream != null) {
//                    inputStream.close();
//                }
//
//                if (outputStream != null) {
//                    outputStream.close();
//                }
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        aMap.setCustomMapStylePath(filePath + "/" + styleName);
//
//    }

    @Override
    public void onMapLoaded() {
        refreshMap();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case Constants.EVENT_DATA_DEVICE_POSITION_CALIBRATION:
                if (data instanceof DeviceInfo) {
                    DeviceInfo pushDeviceInfo = (DeviceInfo) data;
                    if (pushDeviceInfo.getSn().equalsIgnoreCase(mDeviceInfo.getSn())) {
                        mDeviceInfo.cloneSocketData(pushDeviceInfo);
                        refreshMap();
                    }
                }
                break;

            case Constants.EVENT_DATA_UPDATE_BASE_STATION:
                final ArrayList<Double> pushDeviceInfo = (ArrayList<Double>) data;

                mDeviceInfo.setLonlat(pushDeviceInfo);
                refreshMap();
                break;
        }

    }

    private void refreshMap() {
        List<Double> lonlat = mDeviceInfo.getLonlat();
        if (aMap != null && lonlat != null && lonlat.size() > 1) {
            aMap.clear();
            UiSettings uiSettings = aMap.getUiSettings();
            // 通过UISettings.setZoomControlsEnabled(boolean)来设置缩放按钮是否能显示
            uiSettings.setZoomControlsEnabled(false);
            destPosition = new LatLng(lonlat.get(1), lonlat.get(0));
            if (lonlat.get(0) == 0 || lonlat.get(1) == 0) {
//                getView().setMapLayoutVisible(false);
//                getView().setNotDeployLayoutVisible(true);
//                mapLayout.setVisibility(View.GONE);
//                notDeployLayout.setVisibility(View.VISIBLE);
            } else {
//                getView().setMapLayoutVisible(true);
//                getView().setNotDeployLayoutVisible(false);
//                mapLayout.setVisibility(View.VISIBLE);
//                notDeployLayout.setVisibility(View.GONE);
                //可视化区域，将指定位置指定到屏幕中心位置
                final CameraUpdate mUpdata = CameraUpdateFactory
                        .newCameraPosition(new CameraPosition(destPosition, 16, 0, 0));
                aMap.moveCamera(mUpdata);

                freshMarker();
//                RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(mDeviceInfo.getLonlat()[1], mDeviceInfo
//                        .getLonlat()[0]), 200, GeocodeSearch.AMAP);
//                geocoderSearch.getFromLocationAsyn(query);

            }

        }

    }

    private void freshMarker() {
//        int statusId = R.mipmap.ic_sensor_status_normal;
//        switch (mDeviceInfo.getStatus()) {
//            case SENSOR_STATUS_ALARM://alarm
//                statusId = R.mipmap.ic_sensor_status_alarm;
//                break;
//            case SENSOR_STATUS_NORMAL://normal
//                statusId = R.mipmap.ic_sensor_status_normal;
//                break;
//            case SENSOR_STATUS_INACTIVE://inactive
//                statusId = R.mipmap.ic_sensor_status_inactive;
//                break;
//            case SENSOR_STATUS_LOST://lost
//                statusId = R.mipmap.ic_sensor_status_lost;
//                break;
//            default:
//                break;
//        }
        int statusId = R.drawable.deploy_map_cur;
        BitmapDescriptor bitmapDescriptor = null;
        Bitmap srcBitmap = BitmapFactory.decodeResource(mContext.getResources(), statusId);
//        if (WidgetUtil.judgeSensorType(mDeviceInfo.getSensorTypes()) != 0) {
//            Bitmap targetBitmap = BitmapFactory.decodeResource(mContext.getResources(), WidgetUtil
//                    .judgeSensorType(mDeviceInfo.getSensorTypes()));
//            Bitmap filterTargetBitmap = WidgetUtil.tintBitmap(targetBitmap, mContext.getResources().getColor
//                    (R.color
//                            .white));
//            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(WidgetUtil.createBitmapDrawable(mContext,
//                    mDeviceInfo.getSensorTypes()[0], srcBitmap, filterTargetBitmap).getBitmap());
//        } else {
//            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(srcBitmap);
//        }
        bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(srcBitmap);
        tempUpBitmap = bitmapDescriptor.getBitmap();
//        aMap.clear();
//        destPosition.latitude -=
        MarkerOptions markerOption = new MarkerOptions().icon(bitmapDescriptor)
                .anchor(0.5f, 1)
                .position(destPosition)
                .draggable(true);
        aMap.clear();
        Marker marker = aMap.addMarker(markerOption);
        marker.setDraggable(false);
        marker.showInfoWindow();
    }

    public void doNavigation() {
        if (!CityAppUtils.doNavigation(mContext, destPosition)) {
            getView().toastShort(mContext.getString(R.string.location_not_obtained));
        }
    }


    public void doDetailShare() {
        boolean wxAppInstalled = SensoroCityApplication.getInstance().api.isWXAppInstalled();
        if (wxAppInstalled) {
//            boolean wxAppSupportAPI = ContextUtils.getContext().api.isWXAppSupportAPI();
//            if (wxAppSupportAPI) {
            toShareWeChat();
//            } else {
//                getView().toastShort("当前版的微信不支持分享功能");
//            }
        } else {
            getView().toastShort(mContext.getString(R.string.wechat_not_installed));
        }
    }

    /**
     * 微信分享
     */
    private void toShareWeChat() {
        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
        miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;
        miniProgramObj.webpageUrl = "https://www.sensoro.com"; // 兼容低版本的网页链接
        miniProgramObj.userName = "gh_8c58c2d63459";
        miniProgramObj.withShareTicket = false;
        String name = mDeviceInfo.getName();
        if (TextUtils.isEmpty(name)) {
            name = mDeviceInfo.getSn();
        }
        int status = mDeviceInfo.getStatus();
        List<String> tags = mDeviceInfo.getTags();
        StringBuilder tempTagStr = new StringBuilder();
        if (tags != null && tags.size() > 0) {
            for (String tag : tags) {
                tempTagStr.append(tag).append(",");
            }
            tempTagStr = new StringBuilder(tempTagStr.substring(0, tempTagStr.lastIndexOf(",")));
        }
        long updatedTime = mDeviceInfo.getUpdatedTime();
        String tempAddress = mDeviceInfo.getAddress();
        if (TextUtils.isEmpty(tempAddress)) {
            tempAddress = mContext.getString(R.string.unknown_street);
        }
        final String tempData = "/pages/location?lon=" + mDeviceInfo.getLonlat().get(0) + "&lat=" + mDeviceInfo.getLonlat().get(1)
                + "&name=" + name + "&address=" + tempAddress + "&status=" + status + "&tags=" + tempTagStr + "&uptime=" +
                updatedTime;
        miniProgramObj.path = tempData;            //小程序页面路径
        final WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
        msg.title = mContext.getString(R.string.sensor_location);                    // 小程序消息title
        msg.description = mContext.getString(R.string.sensor_location_desc);
        aMap.getMapScreenShot(new AMap.OnMapScreenShotListener() {
            @Override
            public void onMapScreenShot(Bitmap bitmap) {
//                int allocationByteCount = bitmap.getAllocationByteCount();
//                Bitmap ratio = ImageFactory.ratio(bitmap, 500, 400);
//                int allocationByteCount1 = ratio.getAllocationByteCount();
//                msg.thumbData = Util.bmpToByteArray(ratio, true);
                byte[] ratio = ImageFactory.ratio(bitmap);
//                int length = ratio.length;
                msg.thumbData = ratio;
                bitmap.recycle();
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = SystemClock.currentThreadTimeMillis() + "";
                req.scene = SendMessageToWX.Req.WXSceneSession;
                req.message = msg;
                boolean b = SensoroCityApplication.getInstance().api.sendReq(req);
                try {
                    LogUtils.loge(this, "toShareWeChat: isSuc = " + b + ",bitmapLength = " + ratio);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int i) {
                try {
                    LogUtils.loge(this, "onMapScreenShot: i = " + i);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }


    public void backToCurrentLocation() {
        List<Double> lonlat = mDeviceInfo.getLonlat();
        if (lonlat != null && lonlat.size() == 2 && lonlat.get(0) != 0 && lonlat.get(1) != 0) {
            double lat = lonlat.get(1);//获取纬度
            double lon = lonlat.get(0);//获取经度
            LatLng latLng = new LatLng(lat, lon);
            if (aMap != null) {
                //可视化区域，将指定位置指定到屏幕中心位置
                CameraUpdate update = CameraUpdateFactory
                        .newCameraPosition(new CameraPosition(latLng, 16, 0, 0));
                aMap.moveCamera(update);
            } else {
                getView().toastShort(mContext.getString(R.string.tips_data_error));
            }
        }
    }

    public void doPositionConfirm() {


        Intent intent = new Intent();
        DeployAnalyzerModel deployAnalyzerModel = new DeployAnalyzerModel();
        deployAnalyzerModel.sn = mDeviceInfo.getSn();
        deployAnalyzerModel.status = mDeviceInfo.getStatus();
        deployAnalyzerModel.deviceType = mDeviceInfo.getDeviceType();
        String contact = mDeviceInfo.getContact();
        String content = mDeviceInfo.getContent();
        if (!TextUtils.isEmpty(content)) {
            DeployContactModel deployContactModel = new DeployContactModel();
            deployContactModel.phone = content;
            deployContactModel.name = contact;
            deployAnalyzerModel.deployContactModelList.add(deployContactModel);
        }
        List<Double> lonlat = mDeviceInfo.getLonlat();
        if (lonlat != null && lonlat.size() == 2) {
            deployAnalyzerModel.latLng.add(lonlat.get(0));
            deployAnalyzerModel.latLng.add(lonlat.get(1));
        }
        deployAnalyzerModel.updatedTime = mDeviceInfo.getUpdatedTime();
        deployAnalyzerModel.signal = mDeviceInfo.getSignal();
        String tempAddress = mDeviceInfo.getAddress();
        if (TextUtils.isEmpty(tempAddress)) {
            deployAnalyzerModel.address = tempAddress;
        }

        if (mDeviceInfo.getSourceType() > 0) {
            deployAnalyzerModel.mapSourceType = mDeviceInfo.getSourceType();
        } else {
            deployAnalyzerModel.mapSourceType = Constants.DEPLOY_MAP_SOURCE_TYPE_MONITOR_MAP_CONFIRM;

        }
        deployAnalyzerModel.deployType = Constants.TYPE_SCAN_DEPLOY_DEVICE;
        intent.setClass(mContext, DeployMapActivity.class);
        intent.putExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL, deployAnalyzerModel);
        getView().startAC(intent);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}
