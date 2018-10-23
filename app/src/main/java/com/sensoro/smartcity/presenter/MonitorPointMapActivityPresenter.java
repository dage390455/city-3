package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
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
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.MonitorPointMapActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IMonitorPointMapActivityView;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.model.PushData;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.ImageFactory;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.WidgetUtil;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static com.amap.api.maps.AMap.MAP_TYPE_NORMAL;

public class MonitorPointMapActivityPresenter extends BasePresenter<IMonitorPointMapActivityView> implements Constants, AMap.OnMapLoadedListener, IOnStart {

    private Activity mContext;
    private AMap aMap;
    private LatLng destPosition;
    private Bitmap tempUpBitmap;
    private DeviceInfo mDeviceInfo;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mDeviceInfo = (DeviceInfo) mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
        initMap();
    }

    @Override
    public void onDestroy() {
        if (tempUpBitmap != null) {
            tempUpBitmap.recycle();
            tempUpBitmap = null;
        }
    }

    public void getMap(AMap map) {
        aMap = map;
    }

    private void initMap() {
        setMapCustomStyleFile();
        aMap.getUiSettings().setTiltGesturesEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.setMapCustomEnable(true);
        aMap.setMyLocationEnabled(true);
        aMap.setOnMapLoadedListener(this);
        aMap.moveCamera(CameraUpdateFactory.zoomTo(15));
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
        myLocationStyle.showMyLocation(true);
        aMap.setMyLocationStyle(myLocationStyle);
    }

    private void setMapCustomStyleFile() {
        String styleName = "custom_config.data";
        FileOutputStream outputStream = null;
        InputStream inputStream = null;
        String filePath = null;
        try {
            inputStream = mContext.getAssets().open(styleName);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            filePath = mContext.getFilesDir().getAbsolutePath();
            File file = new File(filePath + "/" + styleName);
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = new FileOutputStream(file);
            outputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        aMap.setCustomMapStylePath(filePath + "/" + styleName);

    }
    @Override
    public void onMapLoaded() {
        refreshMap();
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(PushData data) {
        if (data != null) {
            List<DeviceInfo> deviceInfoList = data.getDeviceInfoList();
            String sn = mDeviceInfo.getSn();
            for (DeviceInfo deviceInfo : deviceInfoList) {
                if (sn.equals(deviceInfo.getSn())) {
                    mDeviceInfo = deviceInfo;
                    break;
                }
            }
            if (mDeviceInfo != null && AppUtils.isActivityTop(mContext, MonitorPointMapActivity.class)) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        freshTopData();
//                        freshStructData();
                        freshMarker();
                    }
                });


            }
        }
    }

    private void refreshMap() {
        double[] lonlat = mDeviceInfo.getLonlat();
        if (aMap != null && mDeviceInfo.getSensorTypes().length > 0) {
            UiSettings uiSettings = aMap.getUiSettings();
            // 通过UISettings.setZoomControlsEnabled(boolean)来设置缩放按钮是否能显示
            uiSettings.setZoomControlsEnabled(false);
            destPosition = new LatLng(lonlat[1], lonlat[0]);
            if (lonlat[0] == 0 && lonlat[1] == 0) {
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
                        .newCameraPosition(new CameraPosition(destPosition, 16, 0, 30));
                aMap.moveCamera(mUpdata);

                freshMarker();
//                RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(mDeviceInfo.getLonlat()[1], mDeviceInfo
//                        .getLonlat()[0]), 200, GeocodeSearch.AMAP);
//                geocoderSearch.getFromLocationAsyn(query);

            }

        }
    }

    private void freshMarker() {
        int statusId = R.mipmap.ic_sensor_status_normal;
        switch (mDeviceInfo.getStatus()) {
            case SENSOR_STATUS_ALARM://alarm
                statusId = R.mipmap.ic_sensor_status_alarm;
                break;
            case SENSOR_STATUS_NORMAL://normal
                statusId = R.mipmap.ic_sensor_status_normal;
                break;
            case SENSOR_STATUS_INACTIVE://inactive
                statusId = R.mipmap.ic_sensor_status_inactive;
                break;
            case SENSOR_STATUS_LOST://lost
                statusId = R.mipmap.ic_sensor_status_lost;
                break;
            default:
                break;
        }
        BitmapDescriptor bitmapDescriptor = null;
        Bitmap srcBitmap = BitmapFactory.decodeResource(mContext.getResources(), statusId);
        if (WidgetUtil.judgeSensorType(mDeviceInfo.getSensorTypes()) != 0) {
            Bitmap targetBitmap = BitmapFactory.decodeResource(mContext.getResources(), WidgetUtil
                    .judgeSensorType(mDeviceInfo.getSensorTypes()));
            Bitmap filterTargetBitmap = WidgetUtil.tintBitmap(targetBitmap, mContext.getResources().getColor
                    (R.color
                            .white));
            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(WidgetUtil.createBitmapDrawable(mContext,
                    mDeviceInfo.getSensorTypes()[0], srcBitmap, filterTargetBitmap).getBitmap());
        } else {
            bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(srcBitmap);
        }
        tempUpBitmap = bitmapDescriptor.getBitmap();
//        aMap.clear();
//        destPosition.latitude -=
        MarkerOptions markerOption = new MarkerOptions().icon(bitmapDescriptor)
                .anchor(0.5f,0.95f)
                .position(destPosition)
                .draggable(true);
        Marker marker = aMap.addMarker(markerOption);
        marker.setDraggable(false);
        marker.showInfoWindow();
    }

    public void doNavigation() {
        if (!AppUtils.doNavigation(mContext, destPosition)) {
            getView().toastShort("未获取到位置信息");
        }
    }


    public void doDetailShare() {
        boolean wxAppInstalled = SensoroCityApplication.getInstance().api.isWXAppInstalled();
        if (wxAppInstalled) {
//            boolean wxAppSupportAPI = SensoroCityApplication.getInstance().api.isWXAppSupportAPI();
//            if (wxAppSupportAPI) {
            toShareWeChat();
//            } else {
//                getView().toastShort("当前版的微信不支持分享功能");
//            }
        } else {
            getView().toastShort("当前手机未安装微信，请安装后重试");
        }
    }

    /**
     * 微信分享
     */
    private void toShareWeChat() {
        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
        miniProgramObj.miniprogramType = WXMiniProgramObject.MINIPTOGRAM_TYPE_RELEASE;
        miniProgramObj.webpageUrl = "https://www.sensoro.com"; // 兼容低版本的网页链接
        miniProgramObj.userName = "gh_6b7a86071f47";
        miniProgramObj.withShareTicket = false;
        String name = mDeviceInfo.getName();
        if (TextUtils.isEmpty(name)) {
            name = mDeviceInfo.getSn();
        }
        int status = mDeviceInfo.getStatus();
        String[] tags = mDeviceInfo.getTags();
        String tempTagStr = "";
        if (tags != null && tags.length > 0) {
            for (String tag : tags) {
                tempTagStr += tag + ",";
            }
            tempTagStr = tempTagStr.substring(0, tempTagStr.lastIndexOf(","));
        }
        long updatedTime = mDeviceInfo.getUpdatedTime();
        String tempAddress = mDeviceInfo.getAddress();
        if (TextUtils.isEmpty(tempAddress)) {
            tempAddress = "未知街道";
        }
        final String tempData = "/pages/index?lon=" + mDeviceInfo.getLonlat()[0] + "&lat=" + mDeviceInfo.getLonlat()
                [1] +
                "&name=" + name + "&address=" + tempAddress + "&status=" + status + "&tags=" + tempTagStr + "&uptime=" +
                updatedTime;
        miniProgramObj.path = tempData;            //小程序页面路径
        final WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
        msg.title = "传感器位置";                    // 小程序消息title
        msg.description = "通过此工具，可以查看，以及导航到相应的传感器设备";
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
                LogUtils.loge(this, "toShareWeChat: isSuc = " + b + ",bitmapLength = " + ratio);
            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int i) {
                LogUtils.loge(this, "onMapScreenShot: i = " + i);
            }
        });
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
    }

    public void backToCurrentLocation() {
        AMapLocation lastKnownLocation = SensoroCityApplication.getInstance().mLocationClient.getLastKnownLocation();
        if (lastKnownLocation != null) {
            double lat = lastKnownLocation.getLatitude();//获取纬度
            double lon = lastKnownLocation.getLongitude();//获取经度
            LatLng latLng = new LatLng(lat, lon);
            if (aMap != null) {
                //可视化区域，将指定位置指定到屏幕中心位置
                CameraUpdate update = CameraUpdateFactory
                        .newCameraPosition(new CameraPosition(latLng, 15, 0, 30));
                aMap.moveCamera(update);
            }

        } else {
            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
            Log.e("地图错误", "定位失败, 错误码:" + lastKnownLocation.getErrorCode() + ", 错误信息:"
                    + lastKnownLocation.getErrorInfo());
        }
    }
}
