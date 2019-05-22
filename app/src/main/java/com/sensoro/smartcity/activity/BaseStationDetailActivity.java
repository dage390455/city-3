package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MonitorDeployDetailPhotoAdapter;
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IBaseStationDetailActivityView;
import com.sensoro.smartcity.presenter.BaseStationDetailActivityPresenter;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.widget.SensoroLinearLayoutManager;
import com.sensoro.smartcity.widget.SpacesItemDecoration;
import com.sensoro.smartcity.widget.TouchRecycleView;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.graphics.Typeface.DEFAULT_BOLD;

/**
 * 基站详情
 */
public class BaseStationDetailActivity extends BaseActivity<IBaseStationDetailActivityView, BaseStationDetailActivityPresenter> implements OnChartValueSelectedListener, IBaseStationDetailActivityView, MonitorDeployDetailPhotoAdapter.OnRecyclerViewItemClickListener {

    @BindView(R.id.include_text_title_imv_arrows_left)
    ImageView includeTextTitleImvArrowsLeft;
    @BindView(R.id.ac_basestation_scroll)
    NestedScrollView scrollView;
    @BindView(R.id.include_text_title_tv_title)
    TextView includeTextTitleTvTitle;
    @BindView(R.id.include_text_title_tv_subtitle)
    TextView includeTextTitleTvSubtitle;
    @BindView(R.id.include_text_title_divider)
    View includeTextTitleDivider;
    @BindView(R.id.include_text_title_cl_root)
    ConstraintLayout includeTextTitleClRoot;
    @BindView(R.id.v_top_margin)
    View vTopMargin;
    @BindView(R.id.ac_basestation_tv_name)
    TextView acBasestationTvName;
    @BindView(R.id.ac_basestation_tv_typetime)
    TextView acBasestationTvTypetime;
    @BindView(R.id.ac_basestation_tv_state)
    TextView acBasestationTvState;
    @BindView(R.id.ac_basestation_tv_netdelay_title)
    TextView acBasestationTvNetdelayTitle;
    @BindView(R.id.ac_basestation_tv_netdelay)
    TextView acBasestationTvNetdelay;
    @BindView(R.id.ac_monitoring_point_line)
    View acMonitoringPointLine;
    @BindView(R.id.ac_basestation_tv_channel_title)
    TextView acBasestationTvChannelTitle;
    @BindView(R.id.ac_basestation_tv_channel)
    TextView acBasestationTvChannel;
    @BindView(R.id.ac_basestation_rl_channel)
    RelativeLayout acBasestationRlChannel;
    @BindView(R.id.ac_basestation_rl_channel_line)
    View acBasestationRlChannelLine;
    @BindView(R.id.ac_basestation_rl_name_line)
    View acBasestationRlNameLine;
    @BindView(R.id.chartname_ll)
    LinearLayout chartnameLl;
    @BindView(R.id.chart1)
    LineChart chart1;
    @BindView(R.id.time_tv)
    TextView timeTv;
    @BindView(R.id.in_tv)
    TextView inTv;
    @BindView(R.id.chart_top_state_ll)
    LinearLayout chartTopStateLl;
    @BindView(R.id.out_tv)
    TextView outTv;
    @BindView(R.id.chart_bottom_state_ll)
    LinearLayout chartBottomStateLl;
    @BindView(R.id.top_state_rl)
    RelativeLayout topStateRl;
    @BindView(R.id.ac_basestation_tv_today)
    TextView acBasestationTvToday;
    @BindView(R.id.ac_basestation_tv_week)
    TextView acBasestationTvWeek;
    @BindView(R.id.ac_basestation_tv_location_navigation)
    TextView acBasestationTvLocationNavigation;
    @BindView(R.id.ac_basestation_imv_location)
    ImageView acBasestationImvLocation;
    @BindView(R.id.ac_monitoring_point_tv_location)
    TextView acMonitoringPointTvLocation;
    @BindView(R.id.ac_monitor_deploy_photo)
    TouchRecycleView acMonitorDeployPhoto;
    @BindView(R.id.tv_sn)
    TextView tvSn;
    @BindView(R.id.layout_sn)
    RelativeLayout layoutSn;
    @BindView(R.id.tv_tag)
    TextView tvTag;
    @BindView(R.id.rc_tag)
    TouchRecycleView rcTag;
    @BindView(R.id.tv_device_vision)
    TextView tvDeviceVision;
    @BindView(R.id.rl_device_version)
    RelativeLayout rlDeviceVersion;
    @BindView(R.id.rl_network_information)
    RelativeLayout rlNetworkInformation;
    @BindView(R.id.rl_self_check_state)
    RelativeLayout rlSelfCheckState;
    private LineChart chart;
    private TextView out_tv, in_tv, time_tv;
    private DecimalFormat decimalFormat = new DecimalFormat(".00");
    MonitorDeployDetailPhotoAdapter mAdapter;

    @OnClick({R.id.navigation_cl, R.id.include_text_title_imv_arrows_left, R.id.ac_basestation_rl_channel, R.id.ac_basestation_tv_today, R.id.ac_basestation_tv_week, R.id.rl_network_information, R.id.rl_self_check_state})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.navigation_cl:
                Intent intent = new Intent();
                if (AppUtils.isChineseLanguage()) {
                    intent.setClass(mActivity, MonitorPointMapActivity.class);
                } else {
                    intent.setClass(mActivity, MonitorPointMapENActivity.class);
                }

                break;

            case R.id.include_text_title_imv_arrows_left:
                finish();
                break;
            case R.id.ac_basestation_rl_channel:


                startActivity(new Intent(mActivity, FrequencyPointActivity.class));
                break;
            case R.id.ac_basestation_tv_today:


                acBasestationTvToday.setTextColor(Color.parseColor("#252525"));
                acBasestationTvWeek.setBackground(getResources().getDrawable(R.drawable.shape_bg_top));


                acBasestationTvWeek.setTextColor(Color.parseColor("#A6A6A6"));
                acBasestationTvToday.setBackground(null);


                mPresenter.requestData();


                break;
            case R.id.ac_basestation_tv_week:


                acBasestationTvWeek.setTextColor(Color.parseColor("#252525"));
                acBasestationTvToday.setBackground(getResources().getDrawable(R.drawable.shape_bg_top));


                acBasestationTvToday.setTextColor(Color.parseColor("#A6A6A6"));
                acBasestationTvWeek.setBackground(null);

                mPresenter.requestData();

                break;
            case R.id.rl_network_information:
                startActivity(new Intent(mActivity, NetWorkInfoActivity.class));
                break;
            case R.id.rl_self_check_state:
                startActivity(new Intent(mActivity, SelfCheckActivity.class));
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {

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

    }

    @Override
    public void dismissProgressDialog() {

    }

    @Override
    public void toastShort(String msg) {

    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void onItemClick(View view, int position) {

    }

    private void initMonitorPhoto() {
        //
        acMonitorDeployPhoto.setIntercept(false);
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        layoutManager.setReverseLayout(true);
        acMonitorDeployPhoto.addItemDecoration(new SpacesItemDecoration(false, AppUtils.dp2px(this, 8), false));
        acMonitorDeployPhoto.setLayoutManager(layoutManager);
        mAdapter = new MonitorDeployDetailPhotoAdapter(mActivity);
        acMonitorDeployPhoto.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    protected BaseStationDetailActivityPresenter createPresenter() {
        return new BaseStationDetailActivityPresenter();
    }

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_basestation_detail);
        ButterKnife.bind(this);
        mPresenter.initData(mActivity);

        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        includeTextTitleTvTitle.setText(getResources().getString(R.string.base_station_detail));

        out_tv = findViewById(R.id.out_tv);
        in_tv = findViewById(R.id.in_tv);
        time_tv = findViewById(R.id.time_tv);


        chart = findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(this);

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.getLegend().setEnabled(false);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);

        chart.animateX(1500);

        chart.setPinchZoom(false);
        chart.setScaleYEnabled(false);
        chart.setScaleXEnabled(false);


        XAxis xAxis = chart.getXAxis();
        xAxis.setTypeface(DEFAULT_BOLD);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.parseColor("#252525"));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisLineColor(Color.BLACK);
        xAxis.setDrawAxisLine(true);
        xAxis.setValueFormatter(new MyXFormatter());

        xAxis.setLabelCount(3);


        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(DEFAULT_BOLD);
        leftAxis.setTextSize(10f);

        leftAxis.setTextColor(Color.parseColor("#252525"));

        leftAxis.setAxisMaximum(70f);
        leftAxis.setAxisMinimum(10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);

//        leftAxis.enableGridDashedLine(10, 10, 0);
//        leftAxis.setGranularityEnabled(true);
        leftAxis.setValueFormatter(new MyYFormatter());


        chart.getAxisRight().setEnabled(false);


        mPresenter.requestData();


        chart.setOnTouchListener(touchListener);

        chart.setOnChartGestureListener(onChartGestureListener);

    }


    /**
     * 手势处理，显示和隐藏高亮及topview
     */
    OnChartGestureListener onChartGestureListener = new OnChartGestureListener() {
        @Override
        public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            Log.i("====onChartGestureStart", "=====" + me.getAction());


        }

        @Override
        public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
            Log.i("====onChartGestureEnd", "=====" + me.getAction());

            fadeOut(topStateRl);
        }

        @Override
        public void onChartLongPressed(MotionEvent me) {
            Log.i("====onChartLongPressed", "=====" + me.getAction());

        }

        @Override
        public void onChartDoubleTapped(MotionEvent me) {
            Log.i("====onChartDoubleTapped", "=====" + me.getAction());

        }

        @Override
        public void onChartSingleTapped(MotionEvent me) {
            Log.i("====onChartSingleTapped", "=====" + me.getAction());

        }

        @Override
        public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX,
                                 float velocityY) {
            Log.i("====onChartFling", "=====" + me1.getAction());

        }

        @Override
        public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
            Log.i("====onChartScale", "=====" + scaleX);

        }

        @Override
        public void onChartTranslate(MotionEvent me, float dX, float dY) {
            Log.i("====onChartTranslate", "=====" + me.getAction());

        }
    };
    /**
     * 处理事件冲突
     */

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        float ratio = 1.8f;
        float x0 = 0f;
        float y0 = 0f;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x0 = event.getX();
                    y0 = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float dx = Math.abs(event.getX() - x0);
                    float dy = Math.abs(event.getY() - y0);
                    x0 = event.getX();
                    y0 = event.getY();
                    scrollView.requestDisallowInterceptTouchEvent(dx * ratio > dy);
                    break;
            }
            return false;
        }
    };


    @Override
    public void updateChartData(LineData lineData) {

        chart.setData(lineData);
        final LineDataSet set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
        final LineDataSet set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
        set1.setDrawVerticalHighlightIndicator(false);
        set2.setDrawVerticalHighlightIndicator(false);
        chart.invalidate();
    }

    @Override
    public void updateTopView(String time, String first, String second) {

        time_tv.setText(time);
        out_tv.setText(second);
        in_tv.setText(first);

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        chart.centerViewToAnimated(e.getX(), e.getY(), chart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
        mPresenter.drawHighlight(e, h, chart.getData());
        e.setIcon(getResources().getDrawable(R.drawable.chart_black_dot));


        final LineDataSet set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
        final LineDataSet set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
        set1.setDrawVerticalHighlightIndicator(true);
        set2.setDrawVerticalHighlightIndicator(true);
        fadeIn(topStateRl);
        myHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onNothingSelected() {

    }

    public void fadeIn(final View view, final float startAlpha, final float endAlpha, final long duration) {


        if (view.getVisibility() == View.VISIBLE) return;


        view.setVisibility(View.VISIBLE);
        Animation animation = new AlphaAnimation(startAlpha, endAlpha);
        animation.setDuration(duration);
        view.startAnimation(animation);


        chart.invalidate();


    }

    public void fadeIn(View view) {
        fadeIn(view, 0F, 1F, 400);

        view.setEnabled(true);
    }


    private final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            LineDataSet set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            LineDataSet set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);


            set1.setDrawVerticalHighlightIndicator(false);
//
//
            set2.setDrawVerticalHighlightIndicator(false);


            for (int i = 0; i < set1.getValues().size(); i++) {
                set1.getValues().get(i).setIcon(null);
            }
            for (int i = 0; i < set2.getValues().size(); i++) {
                set2.getValues().get(i).setIcon(null);
            }

            chart.invalidate();
            topStateRl.setEnabled(false);
            Animation animation = new AlphaAnimation(1F, 0F);
            animation.setDuration(400);
            topStateRl.startAnimation(animation);
            topStateRl.setVisibility(View.GONE);

        }
    };

    public void fadeOut(final View view) {
        if (view.getVisibility() != View.VISIBLE) return;


        //选中的高亮和顶部同时消失，高亮没有复位。只是位置消失了，LineChartRenderer  drawHighlightLines


        myHandler.sendEmptyMessageDelayed(0, 1000);

    }


    public class MyXFormatter extends ValueFormatter {


        private static final String TAG = "MyXFormatter";


        @Override
        public String getFormattedValue(float value) {
            Log.d(TAG, "----->getFormattedValue: " + value);

            if (value > 0) {

                return mPresenter.stampToDate(value + "");
            } else {

                return super.getFormattedValue(value);
            }

        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            Log.d(TAG, "----->getAxisLabel: " + value);

            return mPresenter.stampToDate(value + "");

        }
    }

    public class MyYFormatter extends ValueFormatter {


        private static final String TAG = "MyXFormatter";


        @Override
        public String getFormattedValue(float value) {
            Log.d(TAG, "----->getFormattedValue: " + value);


            String p = decimalFormat.format(value);
            return (p + "\u2103");

//            return super.getFormattedValue(value);
        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            Log.d(TAG, "----->getAxisLabel: " + value);

//            return stampToDate(value + "");

            return super.getAxisLabel(value, axis);
        }
    }


}
