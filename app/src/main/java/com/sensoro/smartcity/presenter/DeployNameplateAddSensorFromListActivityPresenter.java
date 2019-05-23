package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IDeployNameplateAddSensorFromListActivityView;
import com.sensoro.smartcity.model.AddSensorFromListModel;

import java.util.ArrayList;

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
