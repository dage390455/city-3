package com.sensoro.smartcity.debug;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.sensoro.common.base.BaseApplication;
import com.sensoro.common.manger.ThreadPoolManager;
import com.sensoro.common.model.IbeaconSettingData;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.libbleserver.ble.entity.BLEDevice;
import com.sensoro.libbleserver.ble.entity.IBeacon;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceListener;
import com.sensoro.libbleserver.ble.scanner.BLEDeviceManager;
import com.sensoro.common.callback.BleObserver;
import com.sensoro.smartcity.widget.imagepicker.ImagePicker;
import com.sensoro.smartcity.widget.imagepicker.view.CropImageView;
import com.sensoro.smartcity.widget.popup.GlideImageLoader;
import com.tencent.bugly.beta.Beta;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Created by sensoro on 17/7/24.
 */

public class InspectionTestApplication extends BaseApplication implements  AMapLocationListener, Runnable {
    private static volatile InspectionTestApplication instance;
    private final Handler taskHandler = new Handler(Looper.getMainLooper());

    public static String VIDEO_PATH;
    private final BLEDeviceListener<BLEDevice> bleDeviceListener = new BLEDeviceListener<BLEDevice>() {
        @Override
        public void onNewDevice(BLEDevice bleDevice) {
            if (bleDevice != null) {
                String sn = bleDevice.getSn();
                if (!TextUtils.isEmpty(sn)) {
                    IBeacon iBeacon = bleDevice.iBeacon;
                    int iBeaconData = handleIBeaconData(iBeacon);
                    if (iBeaconData > 0) {
                        mNearByIBeaconMap.put(sn + iBeaconData, iBeacon);
                    }
                }
            }
        }

        @Override
        public void onGoneDevice(BLEDevice bleDevice) {
            if (bleDevice != null) {
                String sn = bleDevice.getSn();
                if (!TextUtils.isEmpty(sn)) {
                    IBeacon iBeacon = bleDevice.iBeacon;
                    int iBeaconData = handleIBeaconData(iBeacon);
                    if (iBeaconData > 0) {
                        mNearByIBeaconMap.remove(sn + iBeaconData);
                    }
                }

            }
        }

        @Override
        public void onUpdateDevices(ArrayList<BLEDevice> deviceList) {
            for (BLEDevice bleDevice : deviceList) {
                String sn = bleDevice.getSn();
                if (!TextUtils.isEmpty(sn)) {
                    IBeacon iBeacon = bleDevice.iBeacon;
                    int iBeaconData = handleIBeaconData(iBeacon);
                    if (iBeaconData > 0) {
                        mNearByIBeaconMap.put(sn + iBeaconData, iBeacon);
                    }
                }

            }
        }
    };

    private int handleIBeaconData(IBeacon iBeacon) {
        int state = 0;
        if (iBeacon != null) {
            String uuid = iBeacon.getUuid();
            int major = iBeacon.getMajor();
            int minor = iBeacon.getMinor();

            if (ibeaconSettingData.currentUUID != null) {
                if (ibeaconSettingData.currentUUID.equals(uuid)) {
                    state = 1;
                    if (ibeaconSettingData.currentMajor != null) {
                        if (ibeaconSettingData.currentMajor.equals(major)) {
                            state = 2;
                            if (ibeaconSettingData.currentMirror != null) {
                                if (ibeaconSettingData.currentMirror.equals(minor)) {
                                    state = 3;
                                }
                            }
                        }
                    }
                }
            }
        }
        return state;
    }

    private final LinkedHashMap<String, IBeacon> mNearByIBeaconMap = new LinkedHashMap<>();
    public IbeaconSettingData ibeaconSettingData = new IbeaconSettingData();

    //
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        init();

    }


    public static InspectionTestApplication getInstance() {
        return instance;
    }

    private void initSensoroSDK() {
        try {
            bleDeviceManager = BLEDeviceManager.getInstance(this);
            bleDeviceManager.setForegroundScanPeriod(7 * 1000);
            bleDeviceManager.setForegroundBetweenScanPeriod(2 * 1000);
            bleDeviceManager.setOutOfRangeDelay(10 * 1000);
            bleDeviceManager.setBackgroundMode(false);
            bleDeviceManager.setBLEDeviceListener(BleObserver.getInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void locate() {
        mLocationClient = new AMapLocationClient(this);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        mLocationOption.setHttpTimeOut(20000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        taskHandler.removeCallbacksAndMessages(null);
        if (BleObserver.getInstance().isRegisterBleObserver(bleDeviceListener)) {
            BleObserver.getInstance().unregisterBleObserver(bleDeviceListener);
        }
        mLocationClient.onDestroy();
        mNearByIBeaconMap.clear();
        Beta.unInit();
    }

    public void init() {


        if (AppUtils.isAppMainProcess(this, "com.sensoro.inspectiontask")) {
            VIDEO_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/camera/";
            initSensoroSDK();
            ThreadPoolManager.getInstance().execute(this);
        }

    }



    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);
        //显示拍照按钮
        //TODO 去掉裁剪
        imagePicker.setCrop(false);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(9);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    @Override
    protected void onMyApplicationResumed() {
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    @Override
    protected void onMyApplicationPaused() {
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
        }
    }


    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //可在其中解析amapLocation获取相应内容。
                double lat = aMapLocation.getLatitude();//获取纬度
                double lon = aMapLocation.getLongitude();//获取经度
//            mStartPosition = new LatLng(lat, lon);
                try {
                    LogUtils.loge(this, "定位信息------->lat = " + lat + ",lon = =" + lon);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("地图错误", "定位失败, 错误码:" + aMapLocation.getErrorCode() + ", 错误信息:"
                        + aMapLocation.getErrorInfo());
            }

        }
    }

    @Override
    public void run() {

        initImagePicker();
        ARouter.init(this);
        locate();
        if (!BleObserver.getInstance().isRegisterBleObserver(bleDeviceListener)) {
            BleObserver.getInstance().registerBleObserver(bleDeviceListener);
        }
    }

}
