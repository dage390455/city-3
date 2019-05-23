package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployNameplateAddSensorFromListActivityView;
import com.sensoro.smartcity.model.AddSensorFromListModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DeployNameplateAddSensorFromListActivityPresenter extends BasePresenter<IDeployNameplateAddSensorFromListActivityView> {
    private Activity mActivity;
    private int page;
    private ArrayList<AddSensorFromListModel> mList = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
    }


    @Override
    public void onDestroy() {

    }
}
