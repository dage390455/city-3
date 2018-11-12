package com.sensoro.smartcity.widget.calendar.cardgallery;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * 防止卡片在第一页和最后一页因无法"居中"而一直循环调用onScrollStateChanged-->SnapHelper.snapToTargetExistingView-->onScrollStateChanged
 * Created by jameson on 9/3/16.
 */
public class CardLinearSnapHelper extends LinearSnapHelper {
    public boolean mNoNeedToScroll = false;
    public int[] finalSnapDistance = {0, 0};

    @Override
    public int[] calculateDistanceToFinalSnap(@NonNull RecyclerView.LayoutManager layoutManager, @NonNull View targetView) {
        //Log.e("TAG", "calculateDistanceToFinalSnap");
        if (mNoNeedToScroll) {
            finalSnapDistance[0] = 0;
            finalSnapDistance[1] = 0;
        } else {
            finalSnapDistance = super.calculateDistanceToFinalSnap(layoutManager, targetView);
        }
        return finalSnapDistance;
    }
}
