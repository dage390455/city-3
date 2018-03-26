
package com.sensoro.smartcity.widget;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.util.DateUtil;

import java.lang.ref.WeakReference;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class XYMarkerView extends MarkerView {

    private TextView tvTitle;
    private TextView tvMax;
    private TextView tvMin;
    private TextView tvAvg;
    private TextView tvDate;
    private MPPointF mOffset = new MPPointF();
    private MPPointF mOffset2 = new MPPointF();
    private WeakReference<Chart> mWeakChart;

    public XYMarkerView(Context context, IAxisValueFormatter xAxisValueFormatter) {
        super(context, R.layout.custom_marker_view);

        tvTitle = (TextView) findViewById(R.id.marker_title);
        tvMax = (TextView) findViewById(R.id.marker_max);
        tvMin = (TextView) findViewById(R.id.marker_min);
        tvAvg = (TextView) findViewById(R.id.marker_avg);
        tvDate = (TextView) findViewById(R.id.marker_date);
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        CandleEntry candleEntry = (CandleEntry) e;
        tvTitle.setText(candleEntry.getTitle());
        tvMax.setText("最大值:" + candleEntry.getHigh());
        tvMin.setText("最小值:" + candleEntry.getLow());
        tvAvg.setText("平均值:" + candleEntry.getAvg());
        tvDate.setText("" + DateUtil.parseDateToString(candleEntry.getDate()));
        super.refreshContent(e, highlight);
    }

//    @Override
//    public MPPointF getOffset() {
//        return new MPPointF(-(getWidth() / 2), -getHeight());
//    }

    @Override
    public MPPointF getOffset() {
        return mOffset;
    }

    public MPPointF getOffsetRight() {
        return mOffset;
    }

    public void setChartView(Chart chart) {
        mWeakChart = new WeakReference<>(chart);
    }

    public Chart getChartView() {
        return mWeakChart == null ? null : mWeakChart.get();
    }

    @Override
    public MPPointF getOffsetForDrawingAtPoint(float posX, float posY) {

        Chart chart = getChartView();
        MPPointF offset = getOffset();
        float width = getWidth();
        float height = getHeight();

        mOffset2.x = offset.x;

        if (chart != null && posX + width + mOffset2.x > chart.getWidth()) {
            offset = getOffsetRight();
            mOffset2.x = offset.x;
        }

        mOffset2.y = offset.y;

        if (posX + mOffset2.x < 0) {
            mOffset2.x = - posX;
        } /*else if (chart != null && posX + width + mOffset2.x > chart.getWidth()) {
            mOffset2.x = chart.getWidth() - posX - width;
        }*/

        if (posY + mOffset2.y < 0) {
            mOffset2.y = - posY;
        } else if (chart != null && posY + height + mOffset2.y > chart.getHeight()) {
            mOffset2.y = chart.getHeight() - posY - height;
        }

        return mOffset2;
    }

    public void setOffset(MPPointF offset) {
        mOffset = offset;

        if (mOffset == null) {
            mOffset = new MPPointF();
        }
    }

    public void setOffset(float offsetX, float offsetY) {
        mOffset.x = offsetX;
        mOffset.y = offsetY;
    }

    @Override
    public void setVisible(boolean isVisible) {
       super.setVisible(isVisible);
    }
}
