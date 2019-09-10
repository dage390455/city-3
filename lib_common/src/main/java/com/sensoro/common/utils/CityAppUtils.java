package com.sensoro.common.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.amap.api.location.AMapLocation;
import com.amap.api.maps.model.LatLng;
import com.sensoro.common.base.BaseApplication;

import static com.sensoro.common.utils.AppUtils.isAppInstalled;
import static com.sensoro.common.utils.AppUtils.isChineseLanguage;

public class CityAppUtils {

    public static boolean doNavigation(Activity activity, LatLng destPosition) {
        if (destPosition.latitude == 0 || destPosition.longitude == 0) {
//            SensoroToast.INSTANCE.makeText("位置坐标信息错误", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isChineseLanguage()) {
            AMapLocation lastKnownLocation = BaseApplication.getInstance().mLocationClient.getLastKnownLocation();
            if (lastKnownLocation != null) {
                double lat = lastKnownLocation.getLatitude();//获取纬度
                double lon = lastKnownLocation.getLongitude();//获取经度
                LatLng startPosition = new LatLng(lat, lon);
                if (isAppInstalled(activity, "com.autonavi.minimap")) {
                    openGaoDeMap(activity, startPosition, destPosition);
                } else if (isAppInstalled(activity, "com.baidu.BaiduMap")) {
                    openBaiDuMap(activity, startPosition, destPosition);
                } else {
                    openOther(activity, startPosition, destPosition);
                }
                return true;
            }
            return false;
        } else {
            return doNavigation(activity, GPSUtil.gcj02_To_Gps84(destPosition.latitude, destPosition.longitude), null);
        }


    }

    public static boolean doNavigation(Activity activity, double[] destPosition, String text) {

        if (destPosition == null || destPosition.length != 2 || destPosition[0] == 0 || destPosition[1] == 0) {
//            SensoroToast.INSTANCE.makeText("位置坐标信息错误", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (isAppInstalled(activity, "com.google.android.apps.maps")) {
            goNaviByGoogleMap(activity, destPosition[0], destPosition[1], null);
        } else {
//            Toast.makeText(activity, "您尚未安装谷歌地图", Toast.LENGTH_LONG)
//                    .show();
//            Uri uri = Uri
//                    .parse("market://details?id=com.google.android.apps.maps");
//            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//            activity.startActivity(intent);
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
//            parameters
            try {
                LogUtils.loge("doNavigation = " + destPosition[1] + "," + destPosition[0]);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            String url = "https://www.google.com/maps/search/?api=1&query=" + destPosition[0] + "," + destPosition[1];
//            String url = "https://www.google.com/maps/search/?api=1&query=" + destPosition[1] + "," + destPosition[0];
//            String url = "http://uri.amap.com/navigation?from=" + startPosition.longitude + "," + startPosition.latitude
//                    + ",当前位置" +
//                    "&to=" + destPosition.longitude + "," + destPosition.latitude + "," +
//                    "设备部署位置&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=0";
            try {
                LogUtils.loge("doNavigation = " + url);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            Uri content_url = Uri.parse(url);
            intent.setData(content_url);
            activity.startActivity(intent);


        }
        return true;
    }

    private static void goNaviByGoogleMap(Activity activity, double lat, double lon, String address) {
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
        //        Intent intent = new Intent(Intent.ACTION_VIEW,
//                Uri.parse("http://maps.google.com/maps?"
//                        + "saddr="+ mCurrentLatLng.latitude+ "," + mCurrentLatLng.longitude
//                        + "&daddr=" + mEndLat+ "," + mEndLng
//                        +"&avoid=highway"
//                        +"&language=zh-CN")
//        );
//
//        intent.setClassName("com.google.android.apps.maps","com.google.android.maps.MapsActivity");
//        startActivity(intent);
    }


    private static void openGaoDeMap(Activity activity, LatLng startPosition, LatLng destPosition) {

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

    private static void openBaiDuMap(Activity activity, LatLng startPosition, LatLng destPosition) {
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/direction?origin=name:当前位置|latlng:" + startPosition.latitude + "," +
                startPosition.longitude +
                "&destination=name:设备部署位置|latlng:" + destPosition.latitude + "," + destPosition.longitude +
                "&mode=driving&coord_type=gcj02"));
        activity.startActivity(intent);
    }

    private static void openOther(Activity activity, LatLng startPosition, LatLng destPosition) {
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

    public static void openNetPage(Activity activity, String url) {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        activity.startActivity(intent);
    }
}
