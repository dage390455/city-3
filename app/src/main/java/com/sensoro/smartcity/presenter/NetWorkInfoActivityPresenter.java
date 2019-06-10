package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.server.bean.BaseStationDetailModel;
import com.sensoro.smartcity.imainviews.INetWorkInfoActivityView;

public class NetWorkInfoActivityPresenter extends BasePresenter<INetWorkInfoActivityView> {
    private Activity mContext;

    @Override
    public void initData(Context context) {

        mContext = (Activity) context;
        BaseStationDetailModel.NetWork netWork = mContext.getIntent().getParcelableExtra("network");
        if (null != netWork) {
            getView().updateNetWork(netWork);
        }
    }

    @Override
    public void onDestroy() {

    }
}