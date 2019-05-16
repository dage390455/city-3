
package com.sensoro.smartcity.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
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
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.temp.DemoBase;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static android.graphics.Typeface.DEFAULT_BOLD;

/**
 * 基站详情
 */
public class BaseStationDetailActivity extends DemoBase implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    private LineChart chart;
    private SeekBar seekBarX, seekBarY;
    private TextView tvX, tvY, out_tv, in_tv, time_tv;
    private DecimalFormat decimalFormat = new DecimalFormat(".00");

    public static String stampToDate(String stap) {

        String time;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
        long lt = Float.valueOf(stap).longValue();
        Date date = new Date(lt);
        time = simpleDateFormat.format(date);
        return time;
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_linechart);

        setTitle("LineChartActivity2");

        tvX = findViewById(R.id.tvXMax);
        tvY = findViewById(R.id.tvYMax);
        out_tv = findViewById(R.id.out_tv);
        in_tv = findViewById(R.id.in_tv);
        time_tv = findViewById(R.id.time_tv);

        seekBarX = findViewById(R.id.seekBar1);
        seekBarX.setOnSeekBarChangeListener(this);

        seekBarY = findViewById(R.id.seekBar2);
        seekBarY.setOnSeekBarChangeListener(this);

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
        chart.setPinchZoom(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);

        // add data
        seekBarX.setProgress(20);
        seekBarY.setProgress(30);

        chart.animateX(1500);

//        chart.setScaleYEnabled(false);
//        chart.setScaleXEnabled(false);


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


        setData();

        // redraw
        chart.invalidate();

        chart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                final LineDataSet set1 = (LineDataSet) chart.getData().getDataSetByIndex(0);
                final LineDataSet set2 = (LineDataSet) chart.getData().getDataSetByIndex(1);
                if (event.getAction() == MotionEvent.ACTION_DOWN) {


                    set1.setDrawVerticalHighlightIndicator(true);


                    set2.setDrawVerticalHighlightIndicator(true);
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {

                    chart.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            set1.setDrawHighlightIndicators(false);
                            set2.setDrawHighlightIndicators(false);
                            chart.invalidate();
                        }
                    }, 100);

                }

                return false;
            }
        });


    }

    private void setData() {


        ArrayList<Entry> values1 = new ArrayList<>();

        ArrayList<Entry> values2 = new ArrayList<>();
        Random rand = new Random();


        for (int i = 0; i < 60; i++) {
            float val = rand.nextInt(70 - 50 + 1) + 50;

            values1.add(new Entry(1557901082 + i * 100, val));

        }

        for (int i = 0; i < 60; i++) {
            float val = rand.nextInt(40 - 15 + 1) + 15;
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.line, menu);
        return true;
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        tvX.setText(String.valueOf(seekBarX.getProgress()));
        tvY.setText(String.valueOf(seekBarY.getProgress()));

//        setData(seekBarX.getProgress(), seekBarY.getProgress());
//
//        // redraw
//        chart.invalidate();
    }

    @Override
    protected void saveToGallery() {
        saveToGallery(chart, "LineChartActivity2");
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

        time_tv.setText(stampToDate(e.getX() + ""));

        int dataSetIndex = h.getDataSetIndex();

        float first = 0, second = 0;
        if (dataSetIndex == 0) {
            ILineDataSet dataSetByIndex = chart.getData().getDataSetByIndex(1);
            first = e.getY();
            List<Entry> entriesForXValue = dataSetByIndex.getEntriesForXValue(h.getX());

            second = entriesForXValue.get(entriesForXValue.size() - 1).getY();


        } else if (dataSetIndex == 1) {
            ILineDataSet dataSetByIndex = chart.getData().getDataSetByIndex(0);
            second = e.getY();
            List<Entry> entriesForXValue = dataSetByIndex.getEntriesForXValue(h.getX());

            first = entriesForXValue.get(entriesForXValue.size() - 1).getY();

        }

        out_tv.setText(decimalFormat.format(second) + "\u2103");
        in_tv.setText(decimalFormat.format(first) + "\u2103");

    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


}
