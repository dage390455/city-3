package com.sensoro.city_camera.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.model.LatLng;
import com.sensoro.common.utils.GPSUtil;

import static com.sensoro.common.utils.AppUtils.isAppInstalled;
import static com.sensoro.common.utils.AppUtils.isChineseLanguage;

/**
 * @author : bin.tian
 * date   : 2019-06-25
 */
public class MapUtil {
    private static final String TAG = "MapUtil";
    private static AMapLocationClient mLocationClient;

    public static boolean doNavigation(Context activity, LatLng startPosition, LatLng destPosition) {
        if (destPosition.latitude == 0 || destPosition.longitude == 0) {
            return false;
        }
        if (isChineseLanguage()) {
            if (isAppInstalled(activity, "com.autonavi.minimap")) {
                openGaoDeMap(activity, startPosition, destPosition);
            } else if (isAppInstalled(activity, "com.baidu.BaiduMap")) {
                openBaiDuMap(activity, startPosition, destPosition);
            } else {
                openOther(activity, startPosition, destPosition);
            }
            return true;
        } else {
            return doNavigation(activity, GPSUtil.gcj02_To_Gps84(destPosition.latitude, destPosition.longitude), null);
        }


    }

    private static boolean doNavigation(Context activity, double[] destPosition, String text) {

        if (destPosition == null || destPosition.length != 2 || destPosition[0] == 0 || destPosition[1] == 0) {
            return false;
        }
        if (isAppInstalled(activity, "com.google.android.apps.maps")) {
            goNaviByGoogleMap(activity, destPosition[0], destPosition[1], null);
        } else {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            try {
                Log.e(TAG, "doNavigation = " + destPosition[1] + "," + destPosition[0]);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            String url = "https://www.google.com/maps/search/?api=1&query=" + destPosition[0] + "," + destPosition[1];
            try {
                Log.e(TAG, "doNavigation = " + url);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            activity.startActivity(intent);


        }
        return true;
    }

    private static void goNaviByGoogleMap(Context activity, double lat, double lon, String address) {
        final String GOOGLE_MAP_NAVI_URI = "google.navigation:q=";
        Uri uri;
        if (TextUtils.isEmpty(address)) {
            uri = Uri.parse(GOOGLE_MAP_NAVI_URI + lat + "," + lon);
        } else {
            uri = Uri.parse(GOOGLE_MAP_NAVI_URI + lat + "," + lon + "," + address);
        }
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
        mapIntent.setPackage("com.google.android.apps.maps");
        activity.startActivity(mapIntent);
    }


    private static void openGaoDeMap(Context activity, LatLng startPosition, LatLng destPosition) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        Uri uri = Uri.parse("amapuri://route/plan/?sid=BGVIS1&slat=" + startPosition.latitude + "&slon=" +
                startPosition.longitude + "&sname=当前位置" + "&did=BGVIS2&dlat=" + destPosition.latitude + "&dlon=" +
                destPosition.longitude +
                "&dname=设备部署位置" + "&dev=0&t=0");
        intent.setData(uri);
        //启动该页面即可
        activity.startActivity(intent);
    }

    private static void openBaiDuMap(Context activity, LatLng startPosition, LatLng destPosition) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/direction?origin=name:当前位置|latlng:" + startPosition.latitude + "," +
                startPosition.longitude +
                "&destination=name:设备部署位置|latlng:" + destPosition.latitude + "," + destPosition.longitude +
                "&mode=driving&coord_type=gcj02"));
        activity.startActivity(intent);
    }

    private static void openOther(Context activity, LatLng startPosition, LatLng destPosition) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        String url = "http://uri.amap.com/navigation?from=" + startPosition.longitude + "," + startPosition.latitude
                + ",当前位置" +
                "&to=" + destPosition.longitude + "," + destPosition.latitude + "," +
                "设备部署位置&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=0";
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        activity.startActivity(intent);
    }

    public static void openNetPage(Context activity, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        activity.startActivity(intent);
    }

    public static void locateAndNavigation(Context context, LatLng destPosition) {
        AMapLocation lastKnownLocation = mLocationClient.getLastKnownLocation();
        if (lastKnownLocation != null){
            doNavigation(context, new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()), destPosition);
        }
    }

    public static void startLocation(Context context){
        if(mLocationClient == null){
            mLocationClient = new AMapLocationClient(context.getApplicationContext());
            //设置定位回调监听
            mLocationClient.setLocationListener(new AMapLocationListener() {
                @Override
                public void onLocationChanged(AMapLocation aMapLocation) {
                    if (aMapLocation != null) {
                        if (aMapLocation.getErrorCode() == 0) {
                            //可在其中解析amapLocation获取相应内容。
                            double lat = aMapLocation.getLatitude();//获取纬度
                            double lon = aMapLocation.getLongitude();//获取经度
                            try {
                                Log.e(TAG, "定位信息------->lat = " + lat + ",lon = =" + lon);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        } else {
                            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                            Log.e(TAG, "定位失败, 错误码:" + aMapLocation.getErrorCode() + ", 错误信息:"
                                    + aMapLocation.getErrorInfo());
                        }

                    }
                }
            });
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
        }
        //启动定位
        mLocationClient.startLocation();
    }

    public static void stopLocation(){
        if (mLocationClient != null){
            mLocationClient.stopLocation();
        }
    }
}
