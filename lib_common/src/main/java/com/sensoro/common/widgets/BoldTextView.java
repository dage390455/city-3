package com.sensoro.common.widgets;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

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
