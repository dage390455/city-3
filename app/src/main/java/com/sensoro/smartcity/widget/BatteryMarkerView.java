package com.sensoro.smartcity.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.util.DateUtil;

/**
 * Created by sensoro on 17/12/21.
 */

public class BatteryMarkerView extends RelativeLayout {

    private TextView batteryTextView;
    private TextView dateTextView;
    public BatteryMarkerView(Context context) {
        super(context);
        init();
    }

    public BatteryMarkerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BatteryMarkerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        View inflated = LayoutInflater.from(getContext()).inflate(R.layout.battery_marker_view, this);

        inflated.setLayoutParams(new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        inflated.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));

        // measure(getWidth(), getHeight());
        inflated.layout(0, 0, inflated.getMeasuredWidth(), inflated.getMeasuredHeight());
        batteryTextView = (TextView) inflated.findViewById(R.id.marker_battery);
        dateTextView = (TextView) inflated.findViewById(R.id.marker_battery_date);
    }

    public void refreshContent(float x, float y, float battery, String date) {
        batteryTextView.setText("电量:" + battery + "%");
        dateTextView.setText("" + DateUtil.parseDateToString(date));
        this.setX(x);
        this.setY(y);
        invalidate();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
    }
}
