package com.sensoro.forestfire.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.forestfire.Constants.ForestFireConstans;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.imainviews.IForestFireCameraDetailActivityView;
import com.sensoro.forestfire.imainviews.IForestFireCameraListActivityView;
import com.sensoro.forestfire.model.ForestFireCameraBean;
import com.sensoro.forestfire.model.ForestFireCameraListInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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
