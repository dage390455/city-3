package com.sensoro.forestfire.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.forestfire.imainviews.IForestFireCameraDetailActivityView;

/**
 * @Author: jack
 * 时  间: 2019-09-17
 * 包  名: com.sensoro.forestfire.presenter
 * 简  述: <功能简述>
 */
public class ForestFireCameraDetailActivityPresenter extends BasePresenter<IForestFireCameraDetailActivityView> {
    private Activity mContext;


    @Override
    public void initData(Context context) {
        mContext = (Activity) context;

    }


    @Override
    public void onDestroy() {

    }
}
