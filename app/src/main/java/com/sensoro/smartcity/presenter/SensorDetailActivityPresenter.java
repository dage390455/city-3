package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
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
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.SensorMoreActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISensorDetailActivityView;
import com.sensoro.smartcity.iwidget.IOndestroy;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.DeviceRecentInfo;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.server.response.CityObserver;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.server.response.DeviceRecentRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.ImageFactory;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.XYMarkerView;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXMiniProgramObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.amap.api.maps.AMap.MAP_TYPE_NORMAL;

public class SensorDetailActivityPresenter extends BasePresenter<ISensorDetailActivityView> implements Constants,
        GeocodeSearch.OnGeocodeSearchListener, IOndestroy, AMapLocationListener, AMap.OnMapLoadedListener {
    private Activity mContext;
    private AMap aMap;
    private DeviceInfo mDeviceInfo;
    private String[] sensorTypes;
    private float minValue = 0;
    private LatLng destPosition = null;
    private LatLng startPosition = null;
    private final List<DeviceRecentInfo> mRecentInfoList = new ArrayList<>();
    private Bitmap tempUpBitmap;
    private GeocodeSearch geocoderSearch;
    private String tempAddress = "未知街道";

    private final String TAG = getClass().getSimpleName();
    private AMapLocationClient mLocationClient;
    private CombinedChart mChart;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        geocoderSearch = new GeocodeSearch(mContext);
        geocoderSearch.setOnGeocodeSearchListener(this);
        mDeviceInfo = (DeviceInfo) mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
        initMap();
        init();
        requestDeviceRecentLog();
    }


    private void init() {
        try {
            //
            int textColor = mContext.getResources().getColor(R.color.sensoro_alarm);
            switch (mDeviceInfo.getStatus()) {
                case SENSOR_STATUS_ALARM:
                    getView().setStatusImageView(R.drawable.shape_status_alarm);
                    textColor = mContext.getResources().getColor(R.color.sensoro_alarm);
                    break;
                case SENSOR_STATUS_INACTIVE:
                    getView().setStatusImageView(R.drawable.shape_status_inactive);
                    textColor = mContext.getResources().getColor(R.color.sensoro_inactive);
                    break;
                case SENSOR_STATUS_LOST:
                    getView().setStatusImageView(R.drawable.shape_status_lost);
                    textColor = mContext.getResources().getColor(R.color.sensoro_lost);
                    break;
                case SENSOR_STATUS_NORMAL:
                    getView().setStatusImageView(R.drawable.shape_status_normal);
                    textColor = mContext.getResources().getColor(R.color.sensoro_normal);
                    break;
                default:
                    break;
            }
            getView().setSnTextView(mDeviceInfo.getSn(), textColor);
            getView().setDateTextView(DateUtil.getFullParseDate(mDeviceInfo.getUpdatedTime()), textColor);
            getView().setNameTextView(TextUtils.isEmpty(mDeviceInfo.getName()) ? mDeviceInfo.getSn() : mDeviceInfo
                    .getName(), textColor);

            if (mDeviceInfo.getSensoroDetails().getBattery() != null) {
                if (Float.parseFloat(mDeviceInfo.getSensoroDetails().getBattery().getValue().toString()) == -1) {
                    getView().setBatteryLayoutVisible(false);
//                    batteryLayout.setVisibility(View.GONE);
//                    powerLayout.setVisibility(View.VISIBLE);
                } else {
                    getView().setBatteryLayoutVisible(true);
                    getView().setPowerLayoutVisible(false);
//                    batteryLayout.setVisibility(View.VISIBLE);
//                    powerLayout.setVisibility(View.GONE);
                }
            } else {
                getView().setBatteryLayoutVisible(false);
//                llBatteryLayout.setVisibility(View.GONE);
//                batteryLayout.setVisibility(View.VISIBLE);
//                powerLayout.setVisibility(View.GONE);
            }
            getView().setBatteryMarkerViewVisible(false);
            getView().setRightStructLayoutVisible(false);
            getView().initValueColor(textColor);
            List<SensorStruct> sensorStructList = new ArrayList<>();
            String[] tempSensorTypes = mDeviceInfo.getSensorTypes();
            if (tempSensorTypes.length == 3) {
                List<String> tempList = Arrays.asList(tempSensorTypes);
                Collections.sort(tempList, String.CASE_INSENSITIVE_ORDER);
                if (tempList.contains("collision")) {//collision, pitch,roll
                    tempSensorTypes[0] = "pitch";
                    tempSensorTypes[1] = "roll";
                    tempSensorTypes[2] = "collision";
                } else if (tempList.contains("flame")) {//temperature,humidity,flame
                    tempSensorTypes[0] = "temperature";
                    tempSensorTypes[1] = "humidity";
                    tempSensorTypes[2] = "flame";
                }
            }
            for (int j = 0; j < tempSensorTypes.length; j++) {
                String sensorType = tempSensorTypes[j];
                SensorStruct struct = mDeviceInfo.getSensoroDetails().loadData().get(sensorType);
                if (struct != null) {
                    struct.setSensorType(sensorType);
                    sensorStructList.add(struct);
                } else {
                    struct = new SensorStruct();
                    struct.setSensorType(sensorType);
                    struct.setUnit("-");
                    struct.setValue("-");
                    sensorStructList.add(struct);
                }
            }
//            initChart();
            getView().setTypeImageView(mDeviceInfo.getSensorTypes()[0]);
            getView().refreshStructLayout(sensorStructList);
//            requestDeviceRecentLog();
        } catch (Exception e) {
            e.printStackTrace();
            getView().dismissProgressDialog();
            getView().toastShort(mContext.getResources().getString(R.string.tips_data_error));
        }
    }

    @Override
    public void onDestroy() {
        mRecentInfoList.clear();
        if (tempUpBitmap != null) {
            tempUpBitmap.recycle();
            tempUpBitmap = null;
        }
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    private void locate() {

        mLocationClient = new AMapLocationClient(mContext);
        //设置定位回调监听
        mLocationClient.setLocationListener(this);
        //初始化定位参数
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        //设置定位模式为Hight_Accuracy高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否只定位一次,默认为false
        mLocationOption.setOnceLocation(true);
        mLocationOption.setOnceLocationLatest(true);
        //设置是否强制刷新WIFI，默认为强制刷新
        mLocationOption.setWifiActiveScan(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);
        //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
    }

    private void refreshMap() {
        double[] lonlat = mDeviceInfo.getLonlat();
        if (aMap != null && mDeviceInfo.getSensorTypes().length > 0) {
            UiSettings uiSettings = aMap.getUiSettings();
            // 通过UISettings.setZoomControlsEnabled(boolean)来设置缩放按钮是否能显示
            uiSettings.setZoomControlsEnabled(false);
            destPosition = new LatLng(lonlat[1], lonlat[0]);
            if (lonlat[0] == 0 && lonlat[1] == 0) {
                getView().setMapLayoutVisible(false);
                getView().setNotDeployLayoutVisible(true);
//                mapLayout.setVisibility(View.GONE);
//                notDeployLayout.setVisibility(View.VISIBLE);
            } else {
                getView().setMapLayoutVisible(true);
                getView().setNotDeployLayoutVisible(false);
//                mapLayout.setVisibility(View.VISIBLE);
//                notDeployLayout.setVisibility(View.GONE);
                //可视化区域，将指定位置指定到屏幕中心位置
                final CameraUpdate mUpdata = CameraUpdateFactory
                        .newCameraPosition(new CameraPosition(destPosition, 16, 0, 30));
                aMap.moveCamera(mUpdata);

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
                MarkerOptions markerOption = new MarkerOptions().icon(bitmapDescriptor)
                        .position(destPosition)
                        .draggable(true);
                Marker marker = aMap.addMarker(markerOption);

                marker.showInfoWindow();
                RegeocodeQuery query = new RegeocodeQuery(new LatLonPoint(mDeviceInfo.getLonlat()[1], mDeviceInfo
                        .getLonlat()[0]), 200, GeocodeSearch.AMAP);
                geocoderSearch.getFromLocationAsyn(query);

            }

        }
    }

    private void requestDeviceRecentLog() {
        long endTime = mDeviceInfo.getUpdatedTime();
        long startTime = endTime - 2 * 1000 * 60 * 60 * 24;
        String sn = mDeviceInfo.getSn();
        getView().showProgressDialog();
        //合并请求
        Observable<DeviceInfoListRsp> deviceDetailInfoList = RetrofitServiceHelper.INSTANCE.getDeviceDetailInfoList
                (sn, null, 1);
        Observable<DeviceRecentRsp> deviceHistoryList = RetrofitServiceHelper.INSTANCE.getDeviceHistoryList(sn,
                startTime, endTime);
        Observable.merge(deviceDetailInfoList, deviceHistoryList).subscribeOn(Schedulers.io()).doOnNext(new Action1<ResponseBase>() {


            @Override
            public void call(ResponseBase responseBase) {
                if (responseBase instanceof DeviceInfoListRsp) {
                    DeviceInfoListRsp response = (DeviceInfoListRsp) responseBase;
                    if (response.getData().size() > 0) {
                        DeviceInfo deviceInfo = response.getData().get(0);
                        String[] tags = deviceInfo.getTags();
                        mDeviceInfo.setTags(tags);
                    }
                } else if (responseBase instanceof DeviceRecentRsp) {
                    DeviceRecentRsp response = ((DeviceRecentRsp) responseBase);
                    String data = response.getData().toString();
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        sensorTypes = response.getSensorTypes();
                        if (sensorTypes.length == 3) {
                            List<String> tempList = Arrays.asList(sensorTypes);
                            Collections.sort(tempList, String.CASE_INSENSITIVE_ORDER);
                            if (tempList.contains("collision")) {//collision, pitch,roll
                                sensorTypes[0] = "pitch";
                                sensorTypes[1] = "roll";
                                sensorTypes[2] = "collision";
                            } else if (tempList.contains("flame")) {//temperature,humidity,flame
                                sensorTypes[0] = "temperature";
                                sensorTypes[1] = "humidity";
                                sensorTypes[2] = "flame";
                            }
                        }
                        Iterator<String> iterator = jsonObject.keys();
                        while (iterator.hasNext()) {
                            String str = iterator.next();
                            JSONObject firstJsonObject = jsonObject.getJSONObject(str);
                            DeviceRecentInfo recentInfo = RetrofitServiceHelper.INSTANCE.getGson().fromJson
                                    (firstJsonObject.toString(),
                                            DeviceRecentInfo.class);
                            recentInfo.setDate(str);
                            mRecentInfoList.add(recentInfo);
                        }
                        Collections.sort(mRecentInfoList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseBase>() {


            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
            }

            @Override
            public void onNext(ResponseBase responseBase) {
//                getView().setMapLayoutVisible(true);
                getView().setMapViewVisible(true);
                refreshBatteryLayout();
                refreshKLayout();
            }

            @Override
            public void onErrorMsg(String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }


    private boolean isDrawKLayout(String sensorType) {
        if (sensorType.equals("artificialGas")) {
            return true;
        } else if (sensorType.equals("waterPressure")) {
            return true;
        } else if (sensorType.equals("pitch")) {
            return true;
        } else if (sensorType.equals("roll")) {
            return true;
        } else if (sensorType.equals("ch4")) {
            return true;
        } else if (sensorType.equals("co")) {
            return true;
        } else if (sensorType.equals("co2")) {
            return true;
        } else if (sensorType.equals("humidity")) {
            return true;
        } else if (sensorType.equals("distance")) {
            return true;
        } else if (sensorType.equals("light")) {
            return true;
        } else if (sensorType.equals("lpg")) {
            return true;
        } else if (sensorType.equals("light")) {
            return true;
        } else if (sensorType.equals("no2")) {
            return true;
        } else if (sensorType.equals("so2")) {
            return true;
        } else if (sensorType.equals("temperature")) {
            return true;
        } else if (sensorType.equals("pm10")) {
            return true;
        } else if (sensorType.equals("pm2_5")) {
            return true;
        } else {
            return false;
        }
    }

    private void refreshKLayout() {
        if (sensorTypes != null && sensorTypes.length > 0) {
            String sensorType = sensorTypes[0];
            boolean isDraw = isDrawKLayout(sensorType);
            getView().setRecentDaysTitleTextView(WidgetUtil.getSensorTypeChinese(sensorType));
            getView().setRecentDaysInfo1TextView(WidgetUtil.getSensorTypeChinese(sensorType));
            if (sensorTypes.length > 1) {
                String sensorType2 = sensorTypes[1];
                isDraw = isDrawKLayout(sensorType2);
                getView().setRecentDaysTitleTextView(WidgetUtil.getSensorTypeChinese(sensorType2));
                getView().setRecentDaysInfo2TextView(true, WidgetUtil.getSensorTypesChinese(sensorTypes));
            }
            getView().setRecentKLayoutVisible(isDraw);
            if (mRecentInfoList.size() > 0) {
                setChartData();
            }
        }

    }

    private CandleData generateCandleData1() {
        ArrayList<CandleEntry> vals1 = new ArrayList<CandleEntry>();
        for (int i = 0; i < mRecentInfoList.size(); i++) {
            String sensorType = sensorTypes[0];
            DeviceRecentInfo deviceRecentInfo = mRecentInfoList.get(i);
            if (deviceRecentInfo.getMaxValue(sensorType) != null && deviceRecentInfo.getMinValue(sensorType) != null &&
                    deviceRecentInfo.getAvgValue(sensorType) != null) {
                float max = deviceRecentInfo.getMaxValue(sensorType);
                float min = deviceRecentInfo.getMinValue(sensorType);
                float avg = deviceRecentInfo.getAvgValue(sensorType);
                if (minValue > min) {
                    minValue = min;
                }
//                boolean even = i % 2 == 0;
                float open = (float) (Math.random() * 6) + 1f;
                float close = (float) (Math.random() * 6) + 1f;
                vals1.add(new CandleEntry(i, max, min,
                        max,
                        min,
                        avg,
                        WidgetUtil.getSensorTypeChinese(sensorType),
                        deviceRecentInfo.getDate()
                ));
            }

        }
        if (vals1.size() > 0) {
            CandleDataSet set1 = new CandleDataSet(vals1, "Data Set");

            set1.setDrawIcons(false);
            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
            set1.setDecreasingColor(mContext.getResources().getColor(R.color.popup_selected_text_color));
            set1.setDecreasingPaintStyle(Paint.Style.FILL);
            set1.setNeutralColor(Color.BLUE);
            CandleData data = new CandleData(set1);
            data.setDrawValues(false);
//            data.setHighlightEnabled(false);
            return data;
        } else {
            return null;
        }
    }

    private CandleData generateCandleData2() {
        ArrayList<CandleEntry> vals2 = new ArrayList<CandleEntry>();
        if (sensorTypes.length > 1) {
            for (int i = 0; i < mRecentInfoList.size(); i++) {
                String sensorType = sensorTypes[1];
                DeviceRecentInfo deviceRecentInfo = mRecentInfoList.get(i);
                if (deviceRecentInfo.getMaxValue(sensorType) != null && deviceRecentInfo.getMinValue(sensorType) !=
                        null &&
                        deviceRecentInfo.getAvgValue(sensorType) != null) {
                    float max = (float) deviceRecentInfo.getMaxValue(sensorType);
                    float min = (float) deviceRecentInfo.getMinValue(sensorType);
                    float avg = (float) deviceRecentInfo.getAvgValue(sensorType);
                    if (minValue > min) {
                        minValue = min;
                    }
                    boolean even = i % 2 == 0;
                    float open = (float) (Math.random() * 6) + 1f;
                    float close = (float) (Math.random() * 6) + 1f;

                    vals2.add(new CandleEntry(i, max, min,
                            max,
                            min,
                            avg,
                            WidgetUtil.getSensorTypeChinese(sensorType),
                            deviceRecentInfo.getDate()
                    ));
                }

            }
            if (vals2.size() > 0) {
                CandleDataSet set2 = new CandleDataSet(vals2, "Data Set");

                set2.setDrawIcons(false);
                set2.setAxisDependency(YAxis.AxisDependency.LEFT);
                set2.setDecreasingColor(mContext.getResources().getColor(R.color.sensoro_normal));
                set2.setDecreasingPaintStyle(Paint.Style.FILL);
                set2.setNeutralColor(Color.BLUE);
                CandleData data = new CandleData(set2);
                data.setDrawValues(false);
//                data.setHighlightEnabled(false);
                return data;
            } else {
                return null;
            }
        }

        return null;
    }

    private void setChartData() {
        mChart.resetTracking();
        final String[] days = new String[mRecentInfoList.size()];
        for (int i = 0; i < mRecentInfoList.size(); i++) {
            DeviceRecentInfo deviceRecentInfo = mRecentInfoList.get(i);
            days[i] = deviceRecentInfo.getDate();
        }
        CandleData candleData1 = generateCandleData1();
        CandleData candleData2 = generateCandleData2();
        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(minValue); // this replaces setStartAtZero(true)

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(minValue); // this replaces setStartAtZero(true)
        leftAxis.setDrawLabels(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
        xAxis.setDrawLabels(true);
        xAxis.setTextColor(mContext.getResources().getColor(R.color.c_626262));
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.TRANSPARENT);
        IAxisValueFormatter iAxisValueFormatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                String date = days[(int) value % days.length];
                return DateUtil.getMonthDate(DateUtil.yearStringToDate(date).getTime());
            }
        };
        xAxis.setValueFormatter(iAxisValueFormatter);
        CombinedData data = new CombinedData();
        data.setData(candleData1);
        if (candleData2 != null) {
            data.setSecondCandleData(candleData2);
        }

        XYMarkerView mv = new XYMarkerView(mContext, iAxisValueFormatter);
        mv.setChartView(mChart); // For bounds control
        mChart.setMarker(mv); // Set the marker to the chart
        mChart.setData(data);
        mChart.invalidate();

    }


    private void refreshBatteryLayout() {
        List<DeviceRecentInfo> tempList = new ArrayList<>();
        String tempDateString = null;
        int counter = 0;
        for (int i = 0; i < mRecentInfoList.size(); i++) {
            DeviceRecentInfo deviceRecentInfo = mRecentInfoList.get(i);
            Date date = DateUtil.yearStringToDate(deviceRecentInfo.getDate());
            String dateString = DateUtil.getMonthDate(date.getTime());
            if (dateString != null) {
                if (dateString.equals(tempDateString)) {
                    if (counter < 3) {
                        counter++;
                        tempList.add(deviceRecentInfo);
                    }
                } else {
                    if (tempDateString != null) {
                        counter = 0;
                    }
                    counter++;
                    tempList.add(deviceRecentInfo);

                }
                tempDateString = dateString;
            }

        }
        getView().updateBatteryData(tempList);
    }

    private boolean isAppInstalled(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        //获取所有已安装程序的包信息
        List<PackageInfo> pInfo = packageManager.getInstalledPackages(0);
        //存储所有已安装程序的包名
        List<String> pName = new ArrayList<>();
        //从info中将报名字逐一取出
        if (pInfo != null) {
            for (int i = 0; i < pInfo.size(); i++) {
                String pn = pInfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }

    public void getMapAndChart(AMap map, CombinedChart chart) {
        aMap = map;
        this.mChart = chart;
    }

    private void initMap() {
        aMap.setMapCustomEnable(true);
        aMap.getUiSettings().setTiltGesturesEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
        aMap.setOnMapLoadedListener(this);
//        aMap.getUiSettings().setScaleControlsEnabled(true);
        aMap.setMapType(MAP_TYPE_NORMAL);
//        aMap.setOnMapTouchListener(this);
        String styleName = "custom_config.data";
        aMap.setCustomMapStylePath(mContext.getFilesDir().getAbsolutePath() + "/" + styleName);
    }

    //
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            double lat = aMapLocation.getLatitude();//获取纬度
            double lon = aMapLocation.getLongitude();//获取经度
            startPosition = new LatLng(lat, lon);
            System.out.println("lat===>" + lat);
            System.out.println("lon===>" + lon);
        } else {
            //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
            Log.e("地图错误", "定位失败, 错误码:" + aMapLocation.getErrorCode() + ", 错误信息:"
                    + aMapLocation.getErrorInfo());
        }
    }

    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        String formatAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress();
        Log.e(TAG, "onRegeocodeSearched: " + "code = " + i + ",address = " + formatAddress);
        tempAddress = formatAddress;
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        Log.e(TAG, "onGeocodeSearched: " + "onGeocodeSearched");
    }

    public void doNavigation() {
        if (startPosition == null) {
            getView().toastShort(mContext.getResources().getString(R.string.tips_location_permission));
            return;
        }
        if (isAppInstalled(mContext, "com.autonavi.minimap")) {
            openGaoDeMap();
        } else if (isAppInstalled(mContext, "com.baidu.BaiduMap")) {
            openBaiDuMap();
        } else {
            openOther();
        }
    }

    private void openGaoDeMap() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        Uri uri = Uri.parse("amapuri://route/plan/?sid=BGVIS1&slat=" + startPosition.latitude + "&slon=" +
                startPosition.longitude + "&sname=当前位置" + "&did=BGVIS2&dlat=" + destPosition.latitude + "&dlon=" +
                destPosition.longitude +
                "&dname=设备部署位置" + "&dev=0&t=0");
        intent.setData(uri);
        //启动该页面即可
        getView().startAC(intent);
    }

    private void openBaiDuMap() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/direction?origin=name:当前位置|latlng:" + startPosition.latitude + "," +
                startPosition.longitude +
                "&destination=name:设备部署位置|latlng:" + destPosition.latitude + "," + destPosition.longitude +
                "&mode=driving&coord_type=gcj02"));
        getView().startAC(intent);
    }

    private void openOther() {
        Intent intent = new Intent();
        intent.setAction("android.intent.action.VIEW");
        String url = "http://uri.amap.com/navigation?from=" + startPosition.longitude + "," + startPosition.latitude
                + ",当前位置" +
                "&to=" + destPosition.longitude + "," + destPosition.latitude + "," +
                "设备部署位置&mode=car&policy=1&src=mypage&coordinate=gaode&callnative=0";
        Uri content_url = Uri.parse(url);
        intent.setData(content_url);
        getView().startAC(intent);
    }

    public void doDetailShare() {
        boolean wxAppInstalled = SensoroCityApplication.getInstance().api.isWXAppInstalled();
        if (wxAppInstalled) {
            boolean wxAppSupportAPI = SensoroCityApplication.getInstance().api.isWXAppSupportAPI();
            if (wxAppSupportAPI) {
                toShareWeChat();
            } else {
                getView().toastShort("当前版的微信不支持分享功能");
            }
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
                Log.e(TAG, "toShareWeChat: isSuc = " + b + ",bitmapLength = " + ratio);
            }

            @Override
            public void onMapScreenShot(Bitmap bitmap, int i) {
                Log.e(TAG, "onMapScreenShot: i = " + i);
            }
        });
    }

    public void doMore() {
        Intent intent = new Intent(mContext, SensorMoreActivity.class);
        intent.putExtra(EXTRA_SENSOR_SN, mDeviceInfo.getSn());
        getView().startAC(intent);
    }

    @Override
    public void onMapLoaded() {
        locate();
        refreshMap();
    }
}
