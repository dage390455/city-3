package com.sensoro.smartcity.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by sensoro on 17/10/30.
 */

public class SensoroPagerAdapter extends PagerAdapter {

    private List<View> mViewList;
    public SensoroPagerAdapter(List<View> list) {
        this.mViewList = list;
    }
    @Override
    public int getCount() {//返回view数量
        return mViewList.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mViewList.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.addView(mViewList.get(position), 0);
        return mViewList.get(position);
    }
}
