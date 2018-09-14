package com.sensoro.smartcity.presenter;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IDeployMapActivityView;

public class DeployMapActivityPresenter extends BasePresenter<IDeployMapActivityView> {
    private AMap aMap;
    private Marker smoothMoveMarker;
    private LatLng latLng;
    private GeocodeSearch geocoderSearch;

    @Override
    public void initData(Context context) {

    }

    @Override
    public void onDestroy() {

    }

    public void initMap(AMap map) {

    }

    public void doSaveLocation() {

    }

    public void refreshSignal() {

    }
}
