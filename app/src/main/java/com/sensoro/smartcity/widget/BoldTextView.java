package com.sensoro.smartcity.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

public class BoldTextView extends AppCompatTextView {

    public BoldTextView(Context context) {
        this(context,null);
    }

    public BoldTextView(Context context, AttributeSet attrs) {
        this(context, attrs,0);

    }

    public BoldTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getPaint().setFakeBoldText(true);
    }

}
