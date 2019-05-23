package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IDeployNameplateAddSensorFromListActivityView;
import com.sensoro.smartcity.model.AddSensorFromListModel;

import java.util.ArrayList;

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


    public void requestWithDirection(int direction) {
//        getView().setRelationLayoutVisible(false);
        getView().showProgressDialog();
        if (direction == Constants.DIRECTION_DOWN) {
            page = 1;
            RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(page, null, null, null, null).subscribeOn
                    (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {


                @Override
                public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                    getView().dismissProgressDialog();
                    try {
//                        getView().refreshData(mDataList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
//                    getView().recycleViewRefreshComplete();
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
//                    getView().recycleViewRefreshComplete();
                    getView().toastShort(errorMsg);
                }
            });
        } else {
            page++;
            RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(page, null, null, null, null).subscribeOn
                    (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {


                @Override
                public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                    if (deviceInfoListRsp.getData().size() == 0) {
                        getView().toastShort(mActivity.getString(R.string.no_more_data));
                    } else {
//                        getView().refreshData(mDataList);
                    }
                    getView().dismissProgressDialog();
//                    getView().recycleViewRefreshComplete();
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    page--;
                    getView().dismissProgressDialog();
//                    getView().recycleViewRefreshComplete();
                    getView().toastShort(errorMsg);
                }
            });
        }
    }
    @Override
    public void onDestroy() {

    }
}
