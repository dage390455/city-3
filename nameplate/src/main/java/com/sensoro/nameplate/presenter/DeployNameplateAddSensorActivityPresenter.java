package com.sensoro.nameplate.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.common.server.response.NameplateBindDeviceRsp;
import com.sensoro.nameplate.activity.DeployNameplateAddSensorFromListActivity;
import com.sensoro.nameplate.IMainViews.IDeployNameplateAddSensorActivityView;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeployNameplateAddSensorActivityPresenter extends BasePresenter<IDeployNameplateAddSensorActivityView> {
    private Activity mActivity;
    private int page = 1;
    private List<NamePlateInfo> mBindList = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;

        getView().showProgressDialog();
        getBindDevice(Constants.DIRECTION_DOWN);
    }

    public void getBindDevice(int direction) {
        if(direction == Constants.DIRECTION_DOWN){
            page = 1;
        }else{
            page++;
        }

        RetrofitServiceHelper.getInstance().getNameplateBindDevices(20,page,"5cf5e9d1efef535e395e9621")
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<NameplateBindDeviceRsp>(this) {
            @Override
            public void onCompleted(NameplateBindDeviceRsp nameplateBindDeviceRsp) {
                List<NamePlateInfo> data = nameplateBindDeviceRsp.getData();
                if(direction == Constants.DIRECTION_DOWN){
                    mBindList.clear();
                }

                if (data != null && data.size() > 0) {
                    mBindList.addAll(data);
                }
                getView().setBindDeviceSize(mBindList.size());
                getView().updateBindData(mBindList);
                getView().onPullRefreshComplete();
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                if(direction == Constants.DIRECTION_UP){
                    page--;
                }
                getView().onPullRefreshComplete();
                getView().updateBindData(mBindList);
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);


            }
        });
    }

    @Override
    public void onDestroy() {

    }

    public void doAddFromList() {
        Intent intent = new Intent(mActivity, DeployNameplateAddSensorFromListActivity.class);
        getView().startAC(intent);
    }
}
