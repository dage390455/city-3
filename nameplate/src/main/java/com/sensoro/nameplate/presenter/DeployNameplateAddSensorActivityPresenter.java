package com.sensoro.nameplate.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceTypeStyles;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.common.server.response.NameplateBindDeviceRsp;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.IMainViews.IDeployNameplateAddSensorActivityView;
import com.sensoro.nameplate.model.AddSensorModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeployNameplateAddSensorActivityPresenter extends BasePresenter<IDeployNameplateAddSensorActivityView> {
    private Activity mActivity;
    private int page = 1;
    private ArrayList<NamePlateInfo> mBindList = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        EventBus.getDefault().register(this);

        Object bundleValue = getBundleValue(mActivity, Constants.EXTRA_ASSOCIATION_SENSOR_ADD_BIND_LIST);
        if (bundleValue instanceof ArrayList) {
            ArrayList<NamePlateInfo> bindList = (ArrayList<NamePlateInfo>) bundleValue;
            mBindList.addAll(bindList);
            getView().updateBindData(mBindList);
        }
//        getView().showProgressDialog();
//        getBindDevice(Constants.DIRECTION_DOWN);
    }

    public void getBindDevice(int direction) {
        if(direction == Constants.DIRECTION_DOWN){
            page = 1;
        }else{
            page++;
        }

        RetrofitServiceHelper.getInstance().getNameplateBindDevices(page,20,"5cf5e9d4efef53239b5e9625")
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<NameplateBindDeviceRsp>(this) {
            @Override
            public void onCompleted(NameplateBindDeviceRsp nameplateBindDeviceRsp) {
                List<NamePlateInfo> data = nameplateBindDeviceRsp.getData();
                if(direction == Constants.DIRECTION_DOWN){
                    mBindList.clear();
                }

                if (data != null && data.size() > 0) {
                    dealData(data);
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

    private void dealData(List<NamePlateInfo> data) {
        for (NamePlateInfo model : data) {
            DeviceTypeStyles configDeviceType = PreferencesHelper.getInstance().getConfigDeviceType(model.getDeviceType());
            if (configDeviceType != null) {
                String category = configDeviceType.getCategory();
                String mergeType = configDeviceType.getMergeType();
                MergeTypeStyles mergeTypeStyles = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
                if (mergeTypeStyles != null) {
                    model.deviceTypeName = mergeTypeStyles.getName();
                    if (!TextUtils.isEmpty(category)) {
                        model.deviceTypeName = model.deviceTypeName + category;
                    }
                    if (TextUtils.isEmpty(mergeTypeStyles.getImage())) {
                        model.iconUrl = "";
                    } else {
                        model.iconUrl = mergeTypeStyles.getImage();
                    }
                } else {
                    model.deviceTypeName = mActivity.getString(R.string.unknown);
                    model.iconUrl = "";
                }

            } else {
                model.deviceTypeName = mActivity.getString(R.string.unknown);
                model.iconUrl = "";
            }

        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData){
        if (eventData.code == Constants.EVENT_DATA_DEPLOY_ASSOCIATE_SENSOR_FROM_LIST) {
            Object data = eventData.data;
            if (data instanceof ArrayList) {
                mBindList.clear();
                ArrayList<NamePlateInfo> list = (ArrayList<NamePlateInfo>) data;
                mBindList.addAll(list);
            }

            getView().updateBindData(mBindList);


        }
    }

    public void doAddFromList() {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.EXTRA_ASSOCIATION_SENSOR_ORIGIN_TYPE,"deploy");
        bundle.putSerializable(Constants.EXTRA_ASSOCIATION_SENSOR_BIND_LIST,mBindList);
        startActivity(ARouterConstants.ACTIVITY_DEPLOY_ASSOCIATE_SENSOR_FROM_LIST,bundle,mActivity);
    }

    public void doDeleteItem(int position) {
        mBindList.remove(position);
        getView().updateBindData(mBindList);
    }

    public void doSave() {
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_BIND_LIST;
        eventData.data = mBindList;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void doAddFromScan() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.EXTRA_ASSOCIATION_SENSOR_BIND_LIST,mBindList);
//        startActivity(ARouterConstants.);
    }
}
