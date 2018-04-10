package com.sensoro.smartcity.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.baidu.mobstat.StatService;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.adapter.BatteryAdapter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.DeviceRecentInfo;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.server.response.DeviceRecentRsp;
import com.sensoro.smartcity.util.DateUtil;
import com.sensoro.smartcity.util.Util;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.BatteryMarkerView;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.XYMarkerView;
import com.sensoro.smartcity.widget.statusbar.StatusBarCompat;
import com.sensoro.volleymanager.NumberDeserializer;
import com.tencent.mm.opensdk.modelmsg.GetMessageFromWX;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/11/20.
 */

public class SensorDetailActivity extends BaseActivity implements Constants, OnChartValueSelectedListener,
        AMapLocationListener, View.OnScrollChangeListener, AMap.OnMapTouchListener {

    @BindView(R.id.sensor_detail_back)
    ImageView backImageView;
    @BindView(R.id.sensor_detail_more)
    ImageView moreImageView;
    @BindView(R.id.sensor_detail_iv_status)
    ImageView statusImageView;
    @BindView(R.id.sensor_detail_iv_type)
    ImageView typeImageView;
    @BindView(R.id.sensor_detail_title)
    TextView titleTextView;
    @BindView(R.id.sensor_detail_date)
    TextView dateTextView;
    @BindView(R.id.sensor_detail_name)
    TextView nameTextView;
    @BindView(R.id.sensor_detail_sn)
    TextView snTextView;
    @BindView(R.id.struct_left_name)
    TextView leftNameTextView;
    @BindView(R.id.struct_left_value)
    TextView leftValueTextView;
    @BindView(R.id.struct_left_unit)
    TextView leftUnitTextView;
    @BindView(R.id.struct_right_name)
    TextView rightNameTextView;
    @BindView(R.id.struct_right_value)
    TextView rightValueTextView;
    @BindView(R.id.struct_right_unit)
    TextView rightUnitTextView;
    @BindView(R.id.recent_k_layout)
    LinearLayout recentKLayout;
    @BindView(R.id.recent_days_title)
    TextView recentDaysTitleTextView;
    @BindView(R.id.recent_days_info1)
    TextView recentDaysInfo1TextView;
    @BindView(R.id.recent_days_info2)
    TextView recentDaysInfo2TextView;
    @BindView(R.id.recent_days_battery_title)
    TextView recentDaysBatteryTitleTextView;
    @BindView(R.id.recent_days_battery_info1)
    TextView recentDaysBatteryInfo1TextView;
    @BindView(R.id.sensor_detail_struct_left)
    LinearLayout leftStructLayout;
    @BindView(R.id.sensor_detail_struct_right)
    LinearLayout rightStructLayout;
    @BindView(R.id.recent_days_battery_rv)
    RecyclerView batteryRecyclerView;
    @BindView(R.id.recent_days_battery_layout)
    RelativeLayout batteryLayout;
    @BindView(R.id.recent_days_power_supply)
    RelativeLayout powerLayout;
    @BindView(R.id.sensor_detail_map)
    MapView mMapView;
    @BindView(R.id.sensor_chart)
    CombinedChart mChart;
    @BindView(R.id.sensor_detail_scroll)
    ScrollView scrollView;
    @BindView(R.id.recent_battery_marker)
    BatteryMarkerView batteryMarkerView;
    @BindView(R.id.sensor_detail_not_deploy)
    RelativeLayout notDeployLayout;
    @BindView(R.id.sensor_detail_map_layout)
    RelativeLayout mapLayout;
    private AMap aMap;
    private Gson gson;
    private BatteryAdapter mBatteryAdapter;
    private GridLayoutManager gridLayoutManager;
    private ProgressDialog mProgressDialog;
    private CameraUpdate mUpdata;
    private DeviceInfo mDeviceInfo;
    private String[] sensorTypes;
    private float minValue = 0;
    private LatLng destPosition = null;
    private LatLng startPosition = null;
    private List<DeviceRecentInfo> mRecentInfoList = new ArrayList<>();
    private Bundle bundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_detail);
        ButterKnife.bind(this);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        mMapView.setVisibility(View.GONE);
        init();
        bundle = getIntent().getExtras();
        StatusBarCompat.setStatusBarColor(this);
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        bundle = intent.getExtras();
    }

    @Override
    protected boolean isNeedSlide() {
        return false;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        StatService.onResume(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
        StatService.onPause(this);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    private void init() {
        try {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.show();
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.registerTypeAdapter(double.class, new NumberDeserializer())
                    .registerTypeAdapter(int.class, new NumberDeserializer())
                    .registerTypeAdapter(Number.class, new NumberDeserializer());

            gson = gsonBuilder.create();
            mDeviceInfo = (DeviceInfo) this.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
            int textColor = getResources().getColor(R.color.sensoro_alarm);
            switch (mDeviceInfo.getStatus()) {
                case SENSOR_STATUS_ALARM:
                    statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.shape_status_alarm));
                    textColor = getResources().getColor(R.color.sensoro_alarm);
                    break;
                case SENSOR_STATUS_INACTIVE:
                    statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.shape_status_inactive));
                    textColor = getResources().getColor(R.color.sensoro_inactive);
                    break;
                case SENSOR_STATUS_LOST:
                    statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.shape_status_lost));
                    textColor = getResources().getColor(R.color.sensoro_lost);
                    break;
                case SENSOR_STATUS_NORMAL:
                    statusImageView.setImageDrawable(getResources().getDrawable(R.drawable.shape_status_normal));
                    textColor = getResources().getColor(R.color.sensoro_normal);
                    break;
                default:
                    break;
            }
            mBatteryAdapter = new BatteryAdapter(this, new RecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    batteryMarkerView.setVisibility(View.VISIBLE);
                    DeviceRecentInfo deviceRecentInfo = mBatteryAdapter.getData().get(position);
                    batteryMarkerView.refreshContent(view.getX(), view.getY(), deviceRecentInfo.getBatteryAvg(),
                            deviceRecentInfo.getDate());
                }
            });
            snTextView.setText(mDeviceInfo.getSn());
            snTextView.setTextColor(textColor);
            dateTextView.setTextColor(textColor);
            if (mDeviceInfo.getName() != null) {
                nameTextView.setText(mDeviceInfo.getName().equals("") ? mDeviceInfo.getSn() : mDeviceInfo.getName());
            } else {
                nameTextView.setText(mDeviceInfo.getSn());
            }
            if (mDeviceInfo.getSensoroDetails().getBattery() != null) {
                if (Float.parseFloat(mDeviceInfo.getSensoroDetails().getBattery().getValue().toString()) == -1) {
                    batteryLayout.setVisibility(View.GONE);
                    powerLayout.setVisibility(View.VISIBLE);
                } else {
                    batteryLayout.setVisibility(View.VISIBLE);
                    powerLayout.setVisibility(View.GONE);
                }
            } else {
                batteryLayout.setVisibility(View.VISIBLE);
                powerLayout.setVisibility(View.GONE);
            }

            batteryMarkerView.setVisibility(View.GONE);
            nameTextView.setTextColor(textColor);
            rightStructLayout.setVisibility(View.GONE);
            leftNameTextView.setTextColor(textColor);
            leftUnitTextView.setTextColor(textColor);
            leftValueTextView.setTextColor(textColor);
            rightUnitTextView.setTextColor(textColor);
            rightValueTextView.setTextColor(textColor);
            rightNameTextView.setTextColor(textColor);
            initMap();
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
            initChart();
            refreshStructLayout(sensorStructList);
            requestDeviceRecentLog();
            WidgetUtil.judgeSensorType(getApplicationContext(), typeImageView, mDeviceInfo.getSensorTypes()[0]);
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            Toast.makeText(this, R.string.tips_data_error, Toast.LENGTH_SHORT).show();
        }

    }

    private void initChart() {

        // no description text
        mChart.setBackgroundColor(Color.WHITE);

        mChart.getDescription().setEnabled(false);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);

        YAxis leftAxis = mChart.getAxisLeft();
//        leftAxis.setEnabled(false);
        leftAxis.setLabelCount(7, false);
        leftAxis.setDrawGridLines(false);
        leftAxis.setDrawAxisLine(false);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);
//        rightAxis.setStartAtZero(false);
        mChart.setOnChartValueSelectedListener(this);
        mChart.getLegend().setEnabled(false);
    }

    private void initMap() {
        aMap = mMapView.getMap();
        aMap.setMapCustomEnable(true);
        aMap.getUiSettings().setTiltGesturesEnabled(false);
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setMyLocationButtonEnabled(false);
//        aMap.setOnMapTouchListener(this);
        String styleName = "custom_config.data";
        aMap.setCustomMapStylePath(getFilesDir().getAbsolutePath() + "/" + styleName);
        refreshMap();
        locate();
    }

    public void locate() {

        AMapLocationClient mLocationClient = new AMapLocationClient(this);
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
        dateTextView.setText(DateUtil.getFullParseDate(mDeviceInfo.getUpdatedTime()));
        double[] lonlat = mDeviceInfo.getLonlat();
        if (aMap != null && mDeviceInfo.getSensorTypes().length > 0) {
            UiSettings uiSettings = aMap.getUiSettings();
            // 通过UISettings.setZoomControlsEnabled(boolean)来设置缩放按钮是否能显示
            uiSettings.setZoomControlsEnabled(false);
            destPosition = new LatLng(lonlat[1], lonlat[0]);
            if (lonlat[0] == 0 && lonlat[1] == 0) {
                mapLayout.setVisibility(View.GONE);
                notDeployLayout.setVisibility(View.VISIBLE);
            } else {
                mapLayout.setVisibility(View.VISIBLE);
                notDeployLayout.setVisibility(View.GONE);
                //可视化区域，将指定位置指定到屏幕中心位置
                mUpdata = CameraUpdateFactory
                        .newCameraPosition(new CameraPosition(destPosition, 15, 0, 30));
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
                Bitmap srcBitmap = BitmapFactory.decodeResource(this.getResources(), statusId);
                if (WidgetUtil.judgeSensorType(mDeviceInfo.getSensorTypes()) != 0) {
                    Bitmap targetBitmap = BitmapFactory.decodeResource(this.getResources(), WidgetUtil
                            .judgeSensorType(mDeviceInfo.getSensorTypes()));
                    Bitmap filterTargetBitmap = WidgetUtil.tintBitmap(targetBitmap, getResources().getColor(R.color
                            .white));
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(WidgetUtil.createBitmapDrawable(this,
                            mDeviceInfo.getSensorTypes()[0], srcBitmap, filterTargetBitmap).getBitmap());
                } else {
                    bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(srcBitmap);
                }

                MarkerOptions markerOption = new MarkerOptions().icon(bitmapDescriptor)
                        .position(destPosition)
                        .draggable(true);
                Marker marker = aMap.addMarker(markerOption);
                marker.showInfoWindow();
            }

        }
    }

    private void requestDeviceRecentLog() {
        long endTime = mDeviceInfo.getUpdatedTime();
        long startTime = endTime - 2 * 1000 * 60 * 60 * 24;
        SensoroCityApplication sensoroCityApplication = (SensoroCityApplication) getApplication();
        sensoroCityApplication.smartCityServer.getDeviceHistoryList(mDeviceInfo.getSn(), startTime, endTime, new
                Response.Listener<DeviceRecentRsp>() {
                    @Override
                    public void onResponse(DeviceRecentRsp response) {
                        mProgressDialog.dismiss();
                        String data = response.getData().toString();
                        try {
                            mMapView.setVisibility(View.VISIBLE);
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
                                DeviceRecentInfo recentInfo = gson.fromJson(firstJsonObject.toString(),
                                        DeviceRecentInfo.class);
                                recentInfo.setDate(str);
                                mRecentInfoList.add(recentInfo);
                            }
                            Collections.sort(mRecentInfoList);
                            refreshBatteryLayout();
                            refreshKLayout();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError.networkResponse != null) {
                    String reason = new String(volleyError.networkResponse.data);
                    try {
                        JSONObject jsonObject = new JSONObject(reason);
                        Toast.makeText(getApplicationContext(), jsonObject.getString("errmsg"), Toast.LENGTH_SHORT)
                                .show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.tips_network_error, Toast.LENGTH_SHORT).show();
                }
                mProgressDialog.dismiss();
            }
        });
    }

    private void refreshStructLayout(List<SensorStruct> sensorStructList) {
        if (sensorStructList.size() > 0) {
            if (sensorStructList.get(0) != null) {
                WidgetUtil.judgeSensorType(sensorStructList.get(0), leftValueTextView, leftUnitTextView);
                leftNameTextView.setText(WidgetUtil.getSensorTypeChinese(sensorStructList.get(0).getSensorType()));
            }
            if (sensorStructList.size() > 1) {
                if (sensorStructList.get(1) != null) {
                    rightStructLayout.setVisibility(View.VISIBLE);
                    WidgetUtil.judgeSensorType(sensorStructList.get(1), rightValueTextView, rightUnitTextView);
                    rightNameTextView.setText(WidgetUtil.getSensorTypeChinese(sensorStructList.get(1).getSensorType()));
                }
            }
        }

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
            recentDaysInfo1TextView.setText(WidgetUtil.getSensorTypeChinese(sensorType));
            recentDaysTitleTextView.setText(WidgetUtil.getSensorTypeChinese(sensorType));
            if (sensorTypes.length > 1) {
                String sensorType2 = sensorTypes[1];
                isDraw = isDrawKLayout(sensorType2);
                recentDaysInfo2TextView.setVisibility(View.VISIBLE);
                recentDaysInfo2TextView.setText(WidgetUtil.getSensorTypeChinese(sensorType2));
                recentDaysTitleTextView.setText(WidgetUtil.getSensorTypesChinese(sensorTypes));
            }
            if (isDraw) {
                recentKLayout.setVisibility(View.VISIBLE);
            } else {
                recentKLayout.setVisibility(View.GONE);
            }
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
            set1.setDecreasingColor(getResources().getColor(R.color.popup_selected_text_color));
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
                set2.setDecreasingColor(getResources().getColor(R.color.sensoro_normal));
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
        xAxis.setTextColor(getResources().getColor(R.color.c_626262));
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


        XYMarkerView mv = new XYMarkerView(this, iAxisValueFormatter);
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
        if (tempList.size() != 0) {
            gridLayoutManager = new GridLayoutManager(this, tempList.size());
            batteryRecyclerView.setLayoutManager(gridLayoutManager);
            batteryRecyclerView.addItemDecoration(new SpacesItemDecoration(false, 10));
            batteryRecyclerView.setAdapter(mBatteryAdapter);
        }

        mBatteryAdapter.setData(tempList);
        mBatteryAdapter.notifyDataSetChanged();
    }

    public static boolean isAppInstalled(Context context, String packageName) {
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

    @OnClick(R.id.sensor_detail_navi_btn)
    public void navigation() {
        if (startPosition == null) {
            Toast.makeText(this, R.string.tips_location_permission, Toast.LENGTH_SHORT).show();
            return;
        }
        if (isAppInstalled(this, "com.autonavi.minimap")) {
            openGaoDeMap();
        } else if (isAppInstalled(this, "com.baidu.BaiduMap")) {
            openBaiDuMap();
        } else {
            openOther();
        }
    }

    @OnClick(R.id.sensor_detail_share_btn)
    public void detailShare() {
//        if (startPosition == null) {
//            Toast.makeText(this, R.string.tips_location_permission, Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (isAppInstalled(this, "com.autonavi.minimap")) {
//            openGaoDeMap();
//        } else if (isAppInstalled(this, "com.baidu.BaiduMap")) {
//            openBaiDuMap();
//        } else {
//            openOther();
//        }
//        Toast.makeText(this, "ddd", Toast.LENGTH_SHORT).show();
        toShareWeChat();
    }

    private void toShareWeChat() {
//        username : gh_6b7a86071f47
//        webpageUrl : https://www.sensoro.com
//        path : /pages/index + 查询条件，参考http://gitlab.sensoro.com/cloud/mini-city/blob/master/小程序调用参数.md
        WXMiniProgramObject miniProgramObj = new WXMiniProgramObject();
        miniProgramObj.miniprogramType =WXMiniProgramObject.MINIPROGRAM_TYPE_PREVIEW;
        miniProgramObj.webpageUrl = "https://www.sensoro.com"; // 兼容低版本的网页链接
        miniProgramObj.userName = "gh_6b7a86071f47";
        miniProgramObj.withShareTicket=false;
        // 小程序原始id
        final String tempData = "/pages/index?lon=144.3333&lat=74.3333&name=ddong1031&address=北京望京&status=0&tags=3，4" +
                "，5，6，7，8，9&uptime=" + SystemClock.currentThreadTimeMillis();
//        miniProgramObj.path = "/pages/media";            //小程序页面路径
        miniProgramObj.path = tempData;            //小程序页面路径
        WXMediaMessage msg = new WXMediaMessage(miniProgramObj);
        msg.title = "小程序";                    // 小程序消息title
        msg.description = "小程序消息Desc";
        // 小程序消息desc
//        msg.thumbData = getThumb();                      // 小程序消息封面图片，小于128k
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.send_music_thumb);
        Bitmap thumbBmp = Bitmap.createScaledBitmap(thumb, 150, 150, true);
        msg.thumbData = Util.bmpToByteArray(thumb, true);
        thumbBmp.recycle();
        thumb.recycle();
        thumb.recycle();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");

//        req.transaction = getTransaction();

        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;  // 目前支持会话
        SensoroCityApplication.getInstance().api.sendReq(req);

        //
//        WXMiniProgramObject miniProgram = new WXMiniProgramObject();
//        miniProgram.webpageUrl = "http://www.qq.com";
//        miniProgram.userName = "gh_d43f693ca31f";
//        miniProgram.path = "pages/play/index?cid=fvue88y1fsnk4w2&ptag=vicyao&seek=3219";
//        WXMediaMessage msg = new WXMediaMessage(miniProgram);
//        msg.title = "分享小程序Title";
//        msg.description = "分享小程序描述信息";
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.send_music_thumb);
//        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
//        bmp.recycle();
//        msg.thumbData = Util.bmpToByteArray(thumbBmp, true);
//
//        SendMessageToWX.Req req = new SendMessageToWX.Req();
//        req.transaction = buildTransaction("webpage");
//        req.message = msg;
//        req.scene = SendMessageToWX.Req.WXSceneTimeline;
//        api.sendReq(req);
//        finish();
    }
    private String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }
    private String getTransaction() {
        try {
            final GetMessageFromWX.Req req = new GetMessageFromWX.Req(bundle);
            return req.transaction;
        } catch (Exception e) {
            return "";
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
        startActivity(intent);
    }

    private void openBaiDuMap() {
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/direction?origin=name:当前位置|latlng:" + startPosition.latitude + "," +
                startPosition.longitude +
                "&destination=name:设备部署位置|latlng:" + destPosition.latitude + "," + destPosition.longitude +
                "&mode=driving&coord_type=gcj02"));
        startActivity(intent);
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
        startActivity(intent);
    }

    @OnClick(R.id.sensor_detail_more)
    public void doMore() {
        Intent intent = new Intent(this, SensorMoreActivity.class);
        intent.putExtra(EXTRA_SENSOR_SN, mDeviceInfo.getSn());
        startActivity(intent);
    }

    @OnClick(R.id.sensor_detail_back)
    public void doBack() {
        this.finish();
    }

    @OnClick(R.id.recent_k_title_layout)
    public void dismissKMarkerView() {
        mChart.getMarker().setVisible(false);
//        mChart.setDrawMarkers(false);
//        mChart.setHighlightFullBarEnabled(false);
        mChart.invalidate();
    }

    @OnClick(R.id.recent_battery_title_Layout)
    public void dismissBatteryMarkerView() {
        batteryMarkerView.setVisibility(View.GONE);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        System.out.println("===>onValueSelected");
//        mChart.setDrawMarkers(true);
//        mChart.setHighlightFullBarEnabled(true);
        mChart.invalidate();
    }

    @Override
    public void onNothingSelected() {
        System.out.println("===>onNothingSelected");
        mChart.getMarkerView().setVisible(false);
    }//694057

    @Override
    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
        if (Math.abs(scrollY - oldScrollY) > 50) {
            mChart.getMarkerView().setVisible(false);
        }
    }

    @Override
    public void onTouch(MotionEvent motionEvent) {

    }

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
}
