package com.sensoro.smartcity.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.widget.NestedScrollView;
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
import com.sensoro.smartcity.base.BaseActivity;
import com.sensoro.smartcity.imainviews.IBaseStationDetailActivityView;
import com.sensoro.smartcity.presenter.BaseStationDetailActivityPresenter;
import com.sensoro.smartcity.widget.TouchRecycleView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.graphics.Typeface.DEFAULT_BOLD;

/**
 * 基站详情
 */
public class BaseStationDetailActivity extends BaseActivity<IBaseStationDetailActivityView, BaseStationDetailActivityPresenter> implements OnChartValueSelectedListener {

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
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");

    public String stampToDate(String stap) {
//        Float.toString(e.getX()
        String time;
        long lt = Float.valueOf(stap).longValue();
        Date date = new Date(lt);
        time = simpleDateFormat.format(date);

        Log.d("stampToDate", "----->stampToDate: " + time + "=====" + lt);

        return time;
    }


    @OnClick({R.id.include_text_title_imv_arrows_left, R.id.ac_basestation_rl_channel, R.id.ac_basestation_tv_today, R.id.ac_basestation_tv_week, R.id.rl_network_information, R.id.rl_self_check_state})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.include_text_title_imv_arrows_left:
                finish();
                break;
            case R.id.ac_basestation_rl_channel:


                startActivity(new Intent(mActivity, FrequencyPointActivity.class));
                break;
            case R.id.ac_basestation_tv_today:
                break;
            case R.id.ac_basestation_tv_week:
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


    public class MyXFormatter extends ValueFormatter {


        private static final String TAG = "MyXFormatter";


        @Override
        public String getFormattedValue(float value) {
            Log.d(TAG, "----->getFormattedValue: " + value);

            if (value > 0) {

                return value + "--";
            } else {

                return super.getFormattedValue(value);
            }

        }

        @Override
        public String getAxisLabel(float value, AxisBase axis) {
            Log.d(TAG, "----->getAxisLabel: " + value);

            return stampToDate(value + "");

//            return super.getAxisLabel(value, axis);
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


    @Override
    protected BaseStationDetailActivityPresenter createPresenter() {
        return new BaseStationDetailActivityPresenter();
    }

    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_basestation_detail);
        ButterKnife.bind(this);
        includeTextTitleTvSubtitle.setVisibility(View.GONE);

        setTitle("LineChartActivity2");

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


        // get the legend (only possible after setting data)
//        Legend l = chart.getLegend();
//
//        // modify the legend ...
//        l.setForm(LegendForm.LINE);
//        l.setTypeface(tfLight);
//        l.setTextSize(11f);
//        l.setTextColor(Color.WHITE);
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setYOffset(11f);

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

//        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setAxisMaximum(70f);
        leftAxis.setAxisMinimum(10f);
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawAxisLine(false);

        leftAxis.enableGridDashedLine(10, 10, 0);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setValueFormatter(new MyYFormatter());


        chart.getAxisRight().setEnabled(false);

//        chart.setOnTouchListener(touchListener);

        setData();

        // redraw
        chart.invalidate();


//        chart.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                final LineDataSet set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
//                final LineDataSet set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//
//                    set1.setDrawVerticalHighlightIndicator(true);
//
//
//                    set2.setDrawVerticalHighlightIndicator(true);
//                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
//
//                    chart.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            set1.setDrawHighlightIndicators(false);
//                            set2.setDrawHighlightIndicators(false);
//                            chart.invalidate();
//                        }
//                    }, 100);
//
//                }
//
//                return false;
//            }
//        });
        chart.setOnChartGestureListener(new OnChartGestureListener() {
            @Override
            public void onChartGestureStart(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
//                final LineDataSet set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
//                final LineDataSet set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
//                if (me.getAction() == MotionEvent.ACTION_DOWN) {
//
//
//                    set1.setDrawVerticalHighlightIndicator(true);
//
//
//                    set2.setDrawVerticalHighlightIndicator(true);
//                }
//                if (topStateRl.getVisibility() == View.GONE) {
//                    topStateRl.setVisibility(View.VISIBLE);
//
//                }


                fadeIn(topStateRl);

            }

            @Override
            public void onChartGestureEnd(MotionEvent me, ChartTouchListener.ChartGesture lastPerformedGesture) {
//                final LineDataSet set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
//                final LineDataSet set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
//
//                if (me.getAction() == MotionEvent.ACTION_UP || me.getAction() == MotionEvent.ACTION_CANCEL) {
////
////                    chart.postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
//                    set1.setDrawHighlightIndicators(false);
//                    set2.setDrawHighlightIndicators(false);
////                        }
////                    }, 100);
//
//                }


                fadeOut(topStateRl);

//                if (topStateRl.getVisibility() == View.VISIBLE) {
//                    topStateRl.setVisibility(View.GONE);
//                }
            }

            @Override
            public void onChartLongPressed(MotionEvent me) {
                Log.i("====onChartLongPressed", "=====" + me.getAction());

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
        });

    }

    View.OnTouchListener touchListener = new View.OnTouchListener() {
        float ratio = 1.8f; //水平和竖直方向滑动的灵敏度,偏大是水平方向灵敏
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

    private void setData() {


        ArrayList<Entry> values1 = new ArrayList<>();

        ArrayList<Entry> values2 = new ArrayList<>();
        Random rand = new Random();


        for (int i = 0; i < 15; i++) {
            float val = rand.nextInt(68 - 45 + 1) + 45;

            values1.add(new Entry(1557901082 + i * 100, val));

        }

        for (int i = 0; i < 15; i++) {
            float val = rand.nextInt(38 - 10 + 1) + 10;
            values2.add(new Entry(1557901082 + i * 100, val));
        }


        LineDataSet set1, set2;

        if (chart.getData() != null &&
                chart.getData().getDataSetCount() > 0) {
            set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
            set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
            set1.setValues(values1);
            set2.setValues(values2);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();
        } else {
            // create a dataset and give it a type
            set1 = new LineDataSet(values1, "DataSet 1");

//            set1.setAxisDependency(AxisDependency.LEFT);
            set1.setLineWidth(2f);
            set1.setCircleRadius(0f);
            set1.setFillAlpha(65);
            set1.setHighLightColor(Color.BLACK);
            set1.setFillColor(Color.parseColor("#6D5EAC"));

            set1.setColor(Color.parseColor("#6D5EAC"));
            set1.setDrawCircleHole(false);
            set1.setDrawValues(false);
            set1.setDrawCircles(false);


            set1.setDrawVerticalHighlightIndicator(true);
            set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setFillFormatter(new MyFillFormatter(0f));
            //set1.setDrawHorizontalHighlightIndicator(false);
            //set1.setVisible(false);
            //set1.setCircleHoleColor(Color.WHITE);

            // create a dataset and give it a type
            set2 = new LineDataSet(values2, "DataSet 2");
//            set2.setAxisDependency(AxisDependency.RIGHT);
            set2.setLineWidth(2f);
            set2.setFillAlpha(65);
            set2.setHighLightColor(Color.BLACK);

            set2.setFillColor(Color.parseColor("#37B0E9"));
            set2.setColor(Color.parseColor("#37B0E9"));
//            set2.setFillColor(Color.parseColor("#6D5EAC"));


            set2.setDrawCircleHole(false);

            set2.setDrawCircles(false);

            set2.setDrawHighlightIndicators(false);
            set2.setDrawVerticalHighlightIndicator(true);
            set2.setDrawHorizontalHighlightIndicator(false);
//            set2.setHighLightColor(Color.parseColor("#37B0E9"));

            //set2.setFillFormatter(new MyFillFormatter(900f));


            // create a data object with the data sets
            LineData data = new LineData(set1, set2);
            data.setValueTextColor(Color.WHITE);
            data.setValueTextSize(9f);
            data.setDrawValues(false);

            // set data
            chart.setData(data);
        }
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("e=====Entry selected", e.toString());


        Log.i("h=====Entry selected", h.toString());


        chart.centerViewToAnimated(e.getX(), e.getY(), chart.getData().getDataSetByIndex(h.getDataSetIndex())
                .getAxisDependency(), 500);


//        ILineDataSet dataSetByIndex1 = chart.getData().getDataSetByIndex(h.getDataSetIndex());
//
//        Log.i("list===Entry selected", dataSetByIndex1.toString());

        time_tv.setText(stampToDate(Float.toString(e.getX())));

        int dataSetIndex = h.getDataSetIndex();

//        e.setIcon(getResources().getDrawable(R.drawable.fade_red));

        float first = 0, second = 0;
        if (dataSetIndex == 0) {
            LineDataSet dataSetByIndex = (LineDataSet) chart.getData().getDataSetByIndex(1);

            LineDataSet dataSetByIndex0 = (LineDataSet) chart.getData().getDataSetByIndex(0);


//            if (chart.getScaleX() == 1) {


            for (int i = 0; i < dataSetByIndex0.getValues().size(); i++) {
                dataSetByIndex0.getValues().get(i).setIcon(null);
            }
            for (int i = 0; i < dataSetByIndex.getValues().size(); i++) {
                Entry entry = dataSetByIndex.getValues().get(i);
                if (e.getX() != entry.getX()) {
                    entry.setIcon(null);
                } else {
                    entry.setIcon(getResources().getDrawable(R.drawable.item_device_offline));
                }
            }

//            } else {
//                for (int i = 0; i < dataSetByIndex0.getValues().size(); i++) {
//                    dataSetByIndex0.getValues().get(i).setIcon(null);
//
//
//                }
//                for (int i = 0; i < dataSetByIndex.getValues().size(); i++) {
//                    Entry entry = dataSetByIndex.getValues().get(i);
//                    entry.setIcon(null);
//                }
//            }
            first = e.getY();
            List<Entry> entriesForXValue = dataSetByIndex.getEntriesForXValue(h.getX());

            second = entriesForXValue.get(entriesForXValue.size() - 1).getY();


        } else if (dataSetIndex == 1) {
            LineDataSet dataSetByIndex = (LineDataSet) chart.getData().getDataSetByIndex(0);


//            if (chart.getScaleX() == 1) {


            for (int i = 0; i < dataSetByIndex.getValues().size(); i++) {
                Entry entry = dataSetByIndex.getValues().get(i);
                if (e.getX() != entry.getX()) {
                    entry.setIcon(null);
                } else {
                    entry.setIcon(getResources().getDrawable(R.drawable.item_device_offline));


                }
            }


            LineDataSet dataSetByIndex1 = (LineDataSet) chart.getData().getDataSetByIndex(1);
            for (int i = 0; i < dataSetByIndex1.getValues().size(); i++) {
                dataSetByIndex1.getValues().get(i).setIcon(null);
            }

//            } else {
//                for (int i = 0; i < dataSetByIndex.getValues().size(); i++) {
//                    Entry entry = dataSetByIndex.getValues().get(i);
//                    entry.setIcon(null);
//                }
//
//
//                LineDataSet dataSetByIndex1 = (LineDataSet) chart.getData().getDataSetByIndex(1);
//                for (int i = 0; i < dataSetByIndex1.getValues().size(); i++) {
//                    dataSetByIndex1.getValues().get(i).setIcon(null);
//                }
//            }
            second = e.getY();
            List<Entry> entriesForXValue = dataSetByIndex.getEntriesForXValue(h.getX());

            first = entriesForXValue.get(entriesForXValue.size() - 1).getY();

        }
//        if (chart.getScaleX() == 1) {


        e.setIcon(getResources().getDrawable(R.drawable.item_device_offline));
//        }

        out_tv.setText(decimalFormat.format(second) + "\u2103");
        in_tv.setText(decimalFormat.format(first) + "\u2103");

    }

    @Override
    public void onNothingSelected() {

    }

    public static void fadeIn(View view, float startAlpha, float endAlpha, long duration) {
        if (view.getVisibility() == View.VISIBLE) return;

        view.setVisibility(View.VISIBLE);
        Animation animation = new AlphaAnimation(startAlpha, endAlpha);
        animation.setDuration(duration);
        view.startAnimation(animation);
    }

    public static void fadeIn(View view) {
        fadeIn(view, 0F, 1F, 400);

        // We disabled the button in fadeOut(), so enable it here.
        view.setEnabled(true);
    }

    public static void fadeOut(View view) {
        if (view.getVisibility() != View.VISIBLE) return;

        // Since the button is still clickable before fade-out animation
        // ends, we disable the button first to block click.
        view.setEnabled(false);
        Animation animation = new AlphaAnimation(1F, 0F);
        animation.setDuration(400);
        view.startAnimation(animation);
        view.setVisibility(View.GONE);
    }


}
