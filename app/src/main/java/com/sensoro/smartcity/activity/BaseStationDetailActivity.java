package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.sensoro.common.adapter.TagAdapter;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.manger.SensoroLinearLayoutManager;
import com.sensoro.common.server.bean.BaseStationDetailModel;
import com.sensoro.common.server.bean.ScenesData;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.utils.DateUtil;
import com.sensoro.common.widgets.ProgressUtils;
import com.sensoro.common.widgets.SensoroToast;
import com.sensoro.common.widgets.SpacesItemDecoration;
import com.sensoro.common.widgets.TouchRecycleView;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.MonitorDeployDetailPhotoAdapter;
import com.sensoro.smartcity.imainviews.IBaseStationDetailActivityView;
import com.sensoro.smartcity.presenter.BaseStationDetailActivityPresenter;
import com.sensoro.smartcity.widget.CityLineChartRenderer;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;

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
    //    @BindView(R.id.chart1)
//    LineChart chart1;
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
    private LineChart twentyhourChart;
    private LineChart sevendaysChart;
    private DecimalFormat decimalFormat = new DecimalFormat(".00");
    MonitorDeployDetailPhotoAdapter mAdapter;
    private ProgressUtils mProgressUtils;
    private TagAdapter mTagAdapter;
    private int currentClick = 0;


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
        initRcDeployDeviceTag();
        initMonitorPhoto();
        initView();
        mPresenter.initData(mActivity);

    }


    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mActivity).build());

        includeTextTitleTvSubtitle.setVisibility(View.GONE);
        includeTextTitleTvTitle.setText(getResources().getString(R.string.base_station_detail));


        initChart();
        initChartRight();
    }

    private void initChart() {
        twentyhourChart = findViewById(R.id.twentyhour_chart);
        twentyhourChart.setOnChartValueSelectedListener(this);

        twentyhourChart.setRenderer(new CityLineChartRenderer(twentyhourChart, twentyhourChart.getAnimator(), twentyhourChart.getViewPortHandler()));
        // no description text
        twentyhourChart.getDescription().setEnabled(false);

        // enable touch gestures
        twentyhourChart.setTouchEnabled(true);

        twentyhourChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        twentyhourChart.getLegend().setEnabled(false);
        twentyhourChart.setDragEnabled(true);
        twentyhourChart.setScaleEnabled(true);
        twentyhourChart.setDrawGridBackground(false);
        twentyhourChart.setHighlightPerDragEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately

        // set an alternative background color
        twentyhourChart.setBackgroundColor(Color.WHITE);

        twentyhourChart.animateX(1500);

        twentyhourChart.setPinchZoom(false);
        twentyhourChart.setScaleYEnabled(false);
        twentyhourChart.setScaleXEnabled(false);

//        twentyhourChart.setViewPortOffsets(120, 20, 60, 100);
        XAxis xAxis = twentyhourChart.getXAxis();
        xAxis.setTypeface(DEFAULT_BOLD);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(getResources().getColor(R.color.c_252525));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        xAxis.setAxisLineColor(ContextCompat.getColor(this, R.color.c_dfdfdf));

        xAxis.setDrawAxisLine(true);
//        xAxis.setGranularity(4000000);
        xAxis.setValueFormatter(new MyXFormatter());

        xAxis.setLabelCount(3, true);
        xAxis.setAvoidFirstLastClipping(true);


        YAxis leftAxis = twentyhourChart.getAxisLeft();
        leftAxis.setTypeface(DEFAULT_BOLD);
        leftAxis.setTextSize(10f);

        leftAxis.setTextColor(getResources().getColor(R.color.c_252525));

        leftAxis.setLabelCount(5);

        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);

        leftAxis.enableGridDashedLine(10, 10, 0);
//        leftAxis.setGranularityEnabled(true);
        leftAxis.setValueFormatter(new MyYFormatter());


        twentyhourChart.getAxisRight().setEnabled(false);


        twentyhourChart.setOnTouchListener(touchListener);
        twentyhourChart.setNoDataText("");

        twentyhourChart.setOnChartGestureListener(onChartGestureListener);
    }

    private void initChartRight() {
        sevendaysChart = findViewById(R.id.sevendays_chart);
        sevendaysChart.setOnChartValueSelectedListener(this);

        sevendaysChart.setRenderer(new CityLineChartRenderer(sevendaysChart, sevendaysChart.getAnimator(), sevendaysChart.getViewPortHandler()));
        // no description text
        sevendaysChart.getDescription().setEnabled(false);

        // enable touch gestures
        sevendaysChart.setTouchEnabled(true);

        sevendaysChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        sevendaysChart.getLegend().setEnabled(false);
        sevendaysChart.setDragEnabled(true);
        sevendaysChart.setScaleEnabled(true);
        sevendaysChart.setDrawGridBackground(false);
        sevendaysChart.setHighlightPerDragEnabled(true);

        sevendaysChart.setBackgroundColor(Color.WHITE);

        sevendaysChart.animateX(1500);

        sevendaysChart.setPinchZoom(false);
        sevendaysChart.setScaleYEnabled(false);
        sevendaysChart.setScaleXEnabled(false);

        XAxis xAxis = sevendaysChart.getXAxis();
        xAxis.setTypeface(DEFAULT_BOLD);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(getResources().getColor(R.color.c_252525));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        xAxis.setAxisLineColor(ContextCompat.getColor(this, R.color.c_dfdfdf));

        xAxis.setDrawAxisLine(true);
//        xAxis.setGranularity(4000000);
        xAxis.setValueFormatter(new MyXFormatter());

        xAxis.setLabelCount(3, true);
        xAxis.setAvoidFirstLastClipping(true);


        YAxis leftAxis = sevendaysChart.getAxisLeft();
        leftAxis.setTypeface(DEFAULT_BOLD);
        leftAxis.setTextSize(10f);
        leftAxis.setLabelCount(5);

        leftAxis.setTextColor(getResources().getColor(R.color.c_252525));


        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(true);

        leftAxis.enableGridDashedLine(10, 10, 0);
//        leftAxis.setGranularityEnabled(true);
        leftAxis.setValueFormatter(new MyYFormatter());


        sevendaysChart.getAxisRight().setEnabled(false);


        sevendaysChart.setOnTouchListener(touchListener);
        sevendaysChart.setNoDataText("");

        sevendaysChart.setOnChartGestureListener(onChartGestureListener);
    }


    /**
     * 手势处理，显示和隐藏高亮及topview
     */
    OnChartGestureListener onChartGestureListener = new OnChartGestureListener() {
        @Override
        public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {


        }

        @Override
        public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {

            fadeOut(topStateRl);
        }

        @Override
        public void onChartLongPressed(MotionEvent me) {

        }

        @Override
        public void onChartDoubleTapped(MotionEvent me) {

        }

        @Override
        public void onChartSingleTapped(MotionEvent me) {

        }

        @Override
        public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX,
                                 float velocityY) {

        }

        @Override
        public void onChartScale(MotionEvent me, float scaleX, float scaleY) {

        }

        @Override
        public void onChartTranslate(MotionEvent me, float dX, float dY) {

        }
    };


    View.OnTouchListener touchListener = new View.OnTouchListener() {
        float ratio = 12f;
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
                default:
                    break;
            }
            return false;
        }
    };


    private void initRcDeployDeviceTag() {
        rcTag.setIntercept(true);
        mTagAdapter = new TagAdapter(mActivity, R.color.c_252525, R.color.c_dfdfdf);
        //
        SensoroLinearLayoutManager layoutManager = new SensoroLinearLayoutManager(mActivity, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }
        };

        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        int spacingInPixels = mActivity.getResources().getDimensionPixelSize(R.dimen.x10);
        rcTag.addItemDecoration(new SpacesItemDecoration(false, spacingInPixels));
        rcTag.setLayoutManager(layoutManager);
        rcTag.setAdapter(mTagAdapter);
    }


    @Override
    public void updateMonitorPhotos(final List<ScenesData> data) {
        if (acMonitorDeployPhoto.isComputingLayout()) {
            acMonitorDeployPhoto.post(new Runnable() {
                @Override
                public void run() {
                    mAdapter.updateImages(data);
                }
            });
            return;
        }
        mAdapter.updateImages(data);
    }

    @Override
    public void updateChartData(LineData lineData, float max, float min) {

        if (currentClick == 0) {
            twentyhourChart.setVisibility(View.VISIBLE);
            sevendaysChart.setVisibility(View.INVISIBLE);

            YAxis leftAxis = twentyhourChart.getAxisLeft();
            leftAxis.resetAxisMaximum();
            leftAxis.resetAxisMinimum();
            leftAxis.setAxisMaximum(max);
            leftAxis.setAxisMinimum(min);
            twentyhourChart.setData(lineData);
            final LineDataSet set1 = (LineDataSet) twentyhourChart.getData().getDataSetByIndex(0);
            final LineDataSet set2 = (LineDataSet) twentyhourChart.getData().getDataSetByIndex(1);
            set1.setDrawVerticalHighlightIndicator(false);
            set2.setDrawVerticalHighlightIndicator(false);

            set1.notifyDataSetChanged();
            set2.notifyDataSetChanged();
            twentyhourChart.getData().notifyDataChanged();
            twentyhourChart.notifyDataSetChanged();

            twentyhourChart.invalidate();
        } else {
            twentyhourChart.setVisibility(View.INVISIBLE);
            sevendaysChart.setVisibility(View.VISIBLE);
            YAxis leftAxis = sevendaysChart.getAxisLeft();
            leftAxis.resetAxisMaximum();
            leftAxis.resetAxisMinimum();
            leftAxis.setAxisMaximum(max);
            leftAxis.setAxisMinimum(min);
            sevendaysChart.setData(lineData);
            final LineDataSet set1 = (LineDataSet) sevendaysChart.getData().getDataSetByIndex(0);
            final LineDataSet set2 = (LineDataSet) sevendaysChart.getData().getDataSetByIndex(1);
            set1.setDrawVerticalHighlightIndicator(false);
            set2.setDrawVerticalHighlightIndicator(false);

            set1.notifyDataSetChanged();
            set2.notifyDataSetChanged();
            sevendaysChart.getData().notifyDataChanged();
            sevendaysChart.notifyDataSetChanged();

            sevendaysChart.invalidate();

        }

    }

    @Override
    public void updateCharEmpty() {


        if (currentClick == 0) {
            updaTwentyhourChart();
        } else {
            updateSevenDaysChart();
        }


    }


    private void updateSevenDaysChart() {
        twentyhourChart.setVisibility(View.INVISIBLE);
        sevendaysChart.setVisibility(View.VISIBLE);
        LineData data = new LineData();

        YAxis leftAxis = sevendaysChart.getAxisLeft();
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        sevendaysChart.setData(data);


        XAxis xAxis = sevendaysChart.getXAxis();

        Date dayBegin = DateUtil.getPastDate(1);

        xAxis.setAxisMinimum(dayBegin.getTime() / 100000);
        xAxis.setAxisMaximum(System.currentTimeMillis() / 100000);


        sevendaysChart.invalidate();
    }

    private void updaTwentyhourChart() {
        twentyhourChart.setVisibility(View.VISIBLE);
        sevendaysChart.setVisibility(View.INVISIBLE);
        LineData data = new LineData();

        YAxis leftAxis = twentyhourChart.getAxisLeft();
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        twentyhourChart.setData(data);


        XAxis xAxis = twentyhourChart.getXAxis();

        Date dayBegin = DateUtil.getPastDate(1);

        xAxis.setAxisMinimum(dayBegin.getTime() / 100000);
        xAxis.setAxisMaximum(System.currentTimeMillis() / 100000);


        twentyhourChart.invalidate();
    }

    @Override
    public void updateDetailData(BaseStationDetailModel model) {
        if (!TextUtils.isEmpty(model.getName())) {
            acBasestationTvName.setText(model.getName());

        } else {
            acBasestationTvName.setText(model.getSn());

        }


        acBasestationTvTypetime.setText(model.getTypeName());

        if (!TextUtils.isEmpty(model.getUpdatedTime())) {
            acBasestationTvTypetime.append(" " + DateUtil.getHourFormatDate(Long.parseLong(model.getUpdatedTime())));
        }

        if (!TextUtils.isEmpty(model.getStatus())) {

            if ("offline".equals(model.getStatus())) {
                acBasestationTvState.setText(mActivity.getResources().getString(R.string.offline));
                acBasestationTvState.setTextColor(getResources().getColor(R.color.c_5d5d5d));
            } else if ("inactive".equals(model.getStatus())) {
                acBasestationTvState.setText(mActivity.getResources().getString(R.string.inactive));
                acBasestationTvState.setTextColor(getResources().getColor(R.color.c_a6a6a6));
            } else {
                acBasestationTvState.setText(mActivity.getResources().getString(R.string.normal));
                acBasestationTvState.setTextColor(getResources().getColor(R.color.c_1DBB99));

            }
        }


        if (model.getTags().size() > 0) {
            rcTag.setVisibility(View.GONE);
            rcTag.setVisibility(View.VISIBLE);
            mTagAdapter.updateTags(model.getTags());
        } else {
            rcTag.setVisibility(View.VISIBLE);
            rcTag.setVisibility(View.GONE);
        }
        tvSn.setText(model.getSn());

        tvDeviceVision.setText(model.getFirmwareVersion());


    }

    @Override
    public void updateNetDelay(String delay, int color) {
        acBasestationTvNetdelay.setTextColor(getResources().getColor(color));
        acBasestationTvNetdelay.setText(delay + "ms");


    }


    @OnClick({R.id.navigation_cl, R.id.include_text_title_imv_arrows_left, R.id.ac_basestation_rl_channel, R.id.ac_basestation_tv_today, R.id.ac_basestation_tv_week, R.id.rl_network_information, R.id.rl_self_check_state})

    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.navigation_cl:
                mPresenter.doNavigation();

                break;

            case R.id.include_text_title_imv_arrows_left:
                finish();
                break;
            case R.id.ac_basestation_rl_channel:


                mPresenter.startFrequencyPointActivity();

                break;
            case R.id.ac_basestation_tv_today:
                currentClick = 0;
                if (twentyhourChart.getData() != null) {
                    twentyhourChart.setVisibility(View.VISIBLE);
                    sevendaysChart.setVisibility(View.INVISIBLE);
                } else {
                    mPresenter.requestChartDetailData("day");

                }

                acBasestationTvToday.setTextColor(getResources().getColor(R.color.c_252525));
                acBasestationTvWeek.setBackground(getResources().getDrawable(R.drawable.shape_bg_top));


                acBasestationTvWeek.setTextColor(getResources().getColor(R.color.c_a6a6a6));
                acBasestationTvToday.setBackground(null);


                break;
            case R.id.ac_basestation_tv_week:
                currentClick = 1;
                if (sevendaysChart.getData() != null) {

                    sevendaysChart.setVisibility(View.VISIBLE);
                    twentyhourChart.setVisibility(View.INVISIBLE);
                } else {
                    mPresenter.requestChartDetailData("week");

                }
                acBasestationTvWeek.setTextColor(getResources().getColor(R.color.c_252525));
                acBasestationTvToday.setBackground(getResources().getDrawable(R.drawable.shape_bg_top));


                acBasestationTvToday.setTextColor(getResources().getColor(R.color.c_a6a6a6));
                acBasestationTvWeek.setBackground(null);


                break;
            case R.id.rl_network_information:


                mPresenter.startNetWorkInfoActivity();
                break;
            case R.id.rl_self_check_state:

                mPresenter.startSelfCheckActivity();

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
    public void updateTopView(String time, String first, String second) {

        timeTv.setText(time);
        outTv.setText(second);
        inTv.setText(first);

    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (currentClick == 0) {
            twentyhourSelected(e, h);
        } else {
            sevendaysSelected(e, h);

        }


    }

    private void twentyhourSelected(Entry e, Highlight h) {
        twentyhourChart.centerViewToAnimated(e.getX(), e.getY(), twentyhourChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
        mPresenter.drawHighlight(e, h, twentyhourChart.getData());
        e.setIcon(getResources().getDrawable(R.drawable.chart_black_dot));


        final LineDataSet set1 = (LineDataSet) twentyhourChart.getData().getDataSetByIndex(0);
        final LineDataSet set2 = (LineDataSet) twentyhourChart.getData().getDataSetByIndex(1);
        set1.setDrawVerticalHighlightIndicator(true);
        set2.setDrawVerticalHighlightIndicator(true);
        fadeIn(topStateRl);
        myHandler.removeCallbacksAndMessages(null);
    }


    private void sevendaysSelected(Entry e, Highlight h) {
        sevendaysChart.centerViewToAnimated(e.getX(), e.getY(), sevendaysChart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);
        mPresenter.drawHighlight(e, h, sevendaysChart.getData());
        e.setIcon(getResources().getDrawable(R.drawable.chart_black_dot));


        final LineDataSet set1 = (LineDataSet) sevendaysChart.getData().getDataSetByIndex(0);
        final LineDataSet set2 = (LineDataSet) sevendaysChart.getData().getDataSetByIndex(1);
        set1.setDrawVerticalHighlightIndicator(true);
        set2.setDrawVerticalHighlightIndicator(true);
        fadeIn(topStateRl);
        myHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onNothingSelected() {

    }

    public void fadeIn(final View view, final float startAlpha, final float endAlpha, final long duration) {


        if (currentClick == 0) {
            if (view.getVisibility() == View.VISIBLE) return;


            view.setVisibility(View.VISIBLE);
            Animation animation = new AlphaAnimation(startAlpha, endAlpha);
            animation.setDuration(duration);
            view.startAnimation(animation);


            twentyhourChart.invalidate();
        } else {

            if (view.getVisibility() == View.VISIBLE) return;


            view.setVisibility(View.VISIBLE);
            Animation animation = new AlphaAnimation(startAlpha, endAlpha);
            animation.setDuration(duration);
            view.startAnimation(animation);


            sevendaysChart.invalidate();
        }


    }

    public void fadeIn(View view) {
        fadeIn(view, 0F, 1F, 400);

        view.setEnabled(true);
    }


    private final Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (currentClick == 0) {
                if (null != twentyhourChart.getData()) {


                    LineDataSet set1 = (LineDataSet) twentyhourChart.getData().getDataSetByIndex(0);

                    LineDataSet set2 = (LineDataSet) twentyhourChart.getData().getDataSetByIndex(1);
                    if (null != set1) {
                        set1.setDrawVerticalHighlightIndicator(false);

                        for (int i = 0; i < set1.getValues().size(); i++) {
                            set1.getValues().get(i).setIcon(null);
                        }
                    }
                    if (null != set2) {
                        set2.setDrawVerticalHighlightIndicator(false);
                        for (int i = 0; i < set2.getValues().size(); i++) {
                            set2.getValues().get(i).setIcon(null);
                        }
                    }
                }

                twentyhourChart.invalidate();
                topStateRl.setEnabled(false);
                Animation animation = new AlphaAnimation(1F, 0F);
                animation.setDuration(400);
                topStateRl.startAnimation(animation);
                topStateRl.setVisibility(View.GONE);
            } else {
                if (null != sevendaysChart.getData()) {
                    LineDataSet set1 = (LineDataSet) sevendaysChart.getData().getDataSetByIndex(0);
                    LineDataSet set2 = (LineDataSet) sevendaysChart.getData().getDataSetByIndex(1);
                    if (null != set1) {
                        set1.setDrawVerticalHighlightIndicator(false);
                        for (int i = 0; i < set1.getValues().size(); i++) {
                            set1.getValues().get(i).setIcon(null);
                        }
                    }

                    if (null != set2) {
                        set2.setDrawVerticalHighlightIndicator(false);
                        for (int i = 0; i < set2.getValues().size(); i++) {
                            set2.getValues().get(i).setIcon(null);
                        }
                    }
                }
                sevendaysChart.invalidate();
                topStateRl.setEnabled(false);
                Animation animation = new AlphaAnimation(1F, 0F);
                animation.setDuration(400);
                topStateRl.startAnimation(animation);
                topStateRl.setVisibility(View.GONE);
            }


        }
    };

    public void fadeOut(final View view) {
        if (view.getVisibility() != View.VISIBLE) return;


        myHandler.sendEmptyMessageDelayed(0, 1000);

    }


    public class MyXFormatter extends ValueFormatter {


        @Override
        public String getFormattedValue(float value) {
            return mPresenter.stampToDate(value + "");

        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            return mPresenter.stampToDate(value + "");

        }
    }

    public class MyYFormatter extends ValueFormatter {

        @Override
        public String getFormattedValue(float value) {
            String p;
            if (value == 0f) {
                p = "0.00";
            } else {
                p = decimalFormat.format(value);
            }
            return (p + "\u2103");

        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {

            return super.getAxisLabel(value, axis);
        }
    }

    @Override
    public void startAC(Intent intent) {
        mActivity.startActivity(intent);

    }

    @Override
    public void finishAc() {

    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mActivity.startActivityForResult(intent, requestCode);

    }

    @Override
    public void setIntentResult(int resultCode) {

    }

    @Override
    public void setIntentResult(int resultCode, Intent data) {

    }

    @Override
    public void showProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.showProgress();
        }
    }

    @Override
    public void dismissProgressDialog() {
        if (mProgressUtils != null) {
            mProgressUtils.dismissProgress();
        }
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.getInstance().makeText(msg, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void onItemClick(View view, int position) {
        List<ScenesData> images = mAdapter.getImages();
        mPresenter.toPhotoDetail(position, images);
    }

    @Override
    public void setDeviceLocationTextColor(int color) {
        acMonitoringPointTvLocation.setTextColor(mActivity.getResources().getColor(color));
    }

    @Override
    public void setDeviceLocation(String location, boolean isArrowsRight) {
        acMonitoringPointTvLocation.setText(location);
        acBasestationImvLocation.setVisibility(isArrowsRight ? View.VISIBLE : View.GONE);
    }
}
