package moe.codeest.enviews;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.shuyu.gsyvideoplayer.R;

/**
 * 加载对话框
 */

public class CityENDownloadView extends RelativeLayout {

    private RotateAnimation rotateAnimation;
    private ImageView mImv;

    public CityENDownloadView(Context context) {
        super(context);
        initView();

    }

    public CityENDownloadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();

    }

    public CityENDownloadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }


    public void initView() {

        LayoutInflater.from(getContext()).inflate(R.layout.city_playprogress_dilog, this, true);
        mImv = findViewById(R.id.city_progress_imv);


        rotateAnimation = new RotateAnimation(0, 359, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setInterpolator(new AccelerateInterpolator());
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setFillBefore(true);
        rotateAnimation.setFillAfter(true);

    }

    public void start() {
        if (mImv != null) {
            mImv.startAnimation(rotateAnimation);
        }
    }

    public void reset() {
        if (mImv != null) {
            mImv.clearAnimation();
        }
    }
}
