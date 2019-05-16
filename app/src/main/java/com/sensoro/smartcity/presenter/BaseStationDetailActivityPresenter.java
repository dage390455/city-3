package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.amap.api.services.geocoder.GeocodeSearch;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IBaseStationDetailActivityView;

public class BaseStationDetailActivityPresenter extends BasePresenter<IBaseStationDetailActivityView> {
    private Activity mContext;
    private GeocodeSearch geocoderSearch;

    @Override
    public void initData(Context context) {

    }

    @Override
    public void onDestroy() {

    }
}