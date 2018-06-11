package com.sensoro.smartcity.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.TextureMapView;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.BatteryAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.ISensorDetailActivityView;
import com.sensoro.smartcity.presenter.SensorDetailActivityPresenter;
import com.sensoro.smartcity.server.bean.DeviceRecentInfo;
import com.sensoro.smartcity.server.bean.SensorStruct;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.BatteryMarkerView;
import com.sensoro.smartcity.widget.MapContainer;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.RecycleViewItemClickListener;
import com.sensoro.smartcity.widget.SpacesItemDecoration;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by sensoro on 17/11/20.
 */

@SuppressLint("NewApi")
@RequiresApi(api = Build.VERSION_CODES.M)
public class SensorDetailActivity extends BaseActivity<ISensorDetailActivityView, SensorDetailActivityPresenter>
        implements ISensorDetailActivityView, OnChartValueSelectedListener,
        View.OnScrollChangeListener {

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
    TextureMapView mMapView;
    @BindView(R.id.sensor_chart)
    CombinedChart mChart;
    @BindView(R.id.sensor_detail_scroll)
    ScrollView scrollView;
    @BindView(R.id.recent_battery_marker)
    BatteryMarkerView batteryMarkerView;
    @BindView(R.id.sensor_detail_not_deploy)
    RelativeLayout notDeployLayout;
    @BindView(R.id.sensor_detail_map_layout)
    MapContainer mapLayout;
    @BindView(R.id.ll_battery_layout)
    LinearLayout llBatteryLayout;
    private ProgressUtils mProgressUtils;
    private BatteryAdapter mBatteryAdapter;


    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_sensor_detail);
        ButterKnife.bind(mActivity);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        initView();
        mPrestener.initData(mActivity);

    }


    private void initView() {
        //获取当前控件的布局对象
        initChart();
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) mMapView.getLayoutParams();
        DisplayMetrics dm = getResources().getDisplayMetrics();
        params.width = dm.widthPixels;
        //设置当前控件布局的高度
        params.height = dm.widthPixels * 4 / 5;
        mMapView.setLayoutParams(params);//将设置好的布局参数应用到控件中
        setMapViewVisible(false);
//        mMapView.setVisibility(View.GONE);
        mapLayout.setScrollView(scrollView);
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());
        mBatteryAdapter = new BatteryAdapter(mActivity, new RecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                batteryMarkerView.setVisibility(View.VISIBLE);
                DeviceRecentInfo deviceRecentInfo = mBatteryAdapter.getData().get(position);
                batteryMarkerView.refreshContent(view.getX(), view.getY(), deviceRecentInfo.getBatteryAvg(),
                        deviceRecentInfo.getDate());
            }
        });
        mPrestener.getMapAndChart(mMapView.getMap(), mChart);
    }

    @Override
    public void refreshStructLayout(List<SensorStruct> sensorStructList) {
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

    @Override
    public void setMapViewVisible(boolean isVisible) {
        mMapView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void updateBatteryData(List<DeviceRecentInfo> batteryDataList) {
        if (batteryDataList.size() != 0) {
            final GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, batteryDataList.size());
            batteryRecyclerView.setLayoutManager(gridLayoutManager);
            batteryRecyclerView.addItemDecoration(new SpacesItemDecoration(false, 10));
            batteryRecyclerView.setAdapter(mBatteryAdapter);
        }

        mBatteryAdapter.setData(batteryDataList);
        mBatteryAdapter.notifyDataSetChanged();
    }

    @Override
    public void setRecentDaysTitleTextView(String recentDaysTitle) {
        recentDaysTitleTextView.setText(recentDaysTitle);
    }

    @Override
    public void setRecentDaysInfo1TextView(String text) {
        recentDaysInfo1TextView.setText(text);
    }

    @Override
    public void setRecentDaysInfo2TextView(boolean isVisible, String text) {
        if (isVisible) {
            recentDaysInfo2TextView.setVisibility(View.VISIBLE);
            recentDaysInfo2TextView.setText(text);
        }
    }

    @Override
    public void setTypeImageView(String type) {
        WidgetUtil.judgeSensorType(mActivity, typeImageView, type);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected SensorDetailActivityPresenter createPresenter() {
        return new SensorDetailActivityPresenter();
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
        mPrestener.onDestroy();
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroy();
        mMapView.onDestroy();

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


    @OnClick(R.id.sensor_detail_navi_btn)
    public void navigation() {
        mPrestener.doNavigation();
    }

    @OnClick(R.id.sensor_detail_share_btn)
    public void detailShare() {
        mPrestener.doDetailShare();
    }

    @OnClick(R.id.sensor_detail_more)
    public void doMore() {
        mPrestener.doMore();
    }

    @OnClick(R.id.sensor_detail_back)
    public void doBack() {
        finishAc();
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
    public void setIntentResult(int requestCode) {

    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {

    }

    @Override
    public void showProgressDialog() {
        mProgressUtils.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void setStatusImageView(int resId) {
        statusImageView.setImageDrawable(mActivity.getResources().getDrawable(resId));
    }

    @Override
    public void setSnTextView(String sn, int color) {
        snTextView.setText(sn);
        snTextView.setTextColor(color);
    }

    @Override
    public void setDateTextView(String date, int color) {
        dateTextView.setText(date);
        dateTextView.setTextColor(color);
    }

    @Override
    public void setNameTextView(String name, int color) {
        nameTextView.setText(name);
        nameTextView.setTextColor(color);
    }

    @Override
    public void setBatteryLayoutVisible(boolean isVisible) {
        batteryLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setPowerLayoutVisible(boolean isVisible) {
        powerLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setNotDeployLayoutVisible(boolean isVisible) {
        notDeployLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setMapLayoutVisible(boolean isVisible) {
        mapLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setBatteryMarkerViewVisible(boolean isVisible) {
        batteryMarkerView.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setRightStructLayoutVisible(boolean isVisible) {
        rightStructLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void initValueColor(int color) {
        leftNameTextView.setTextColor(color);
        leftUnitTextView.setTextColor(color);
        leftValueTextView.setTextColor(color);
        rightUnitTextView.setTextColor(color);
        rightValueTextView.setTextColor(color);
        rightNameTextView.setTextColor(color);
    }

    @Override
    public void setRecentKLayoutVisible(boolean isVisible) {
        recentKLayout.setVisibility(isVisible ? View.VISIBLE : View.GONE);
    }
}
