package com.sensoro.nameplate.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceTypeStyles;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.common.server.response.NameplateAssociateDeviceRsp;
import com.sensoro.common.server.response.NameplateBindDeviceRsp;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.IMainViews.IDeployNameplateAddSensorFromListActivityView;
import com.sensoro.nameplate.R;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeployNameplateAddSensorFromListActivityPresenter extends BasePresenter<IDeployNameplateAddSensorFromListActivityView> {
    private Activity mActivity;
    private int page;
    private ArrayList<NamePlateInfo> mList = new ArrayList<>();
    public ArrayList<NamePlateInfo> mSelectList = new ArrayList<>();
    public ArrayList<NamePlateInfo> mBindList = new ArrayList<>();
    private final List<String> mSearchHistoryList = new ArrayList<>();
    private Drawable checkedDrawable;
    private Drawable unCheckedDrawable;
    private boolean isCheckAll = false;
    private String mOriginType;
    private String mNameplateId;
    private String mSearchText;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;

        Bundle bundle = getBundle(mActivity);
        if (bundle != null) {
            mOriginType = bundle.getString(Constants.EXTRA_ASSOCIATION_SENSOR_ORIGIN_TYPE);
            mNameplateId = bundle.getString(Constants.EXTRA_ASSOCIATION_SENSOR_NAMEPLATE_ID);
            Serializable serializable = bundle.getSerializable(Constants.EXTRA_ASSOCIATION_SENSOR_BIND_LIST);
            if (serializable instanceof ArrayList) {
                mBindList = (ArrayList<NamePlateInfo>) serializable;
            }
        }

        checkedDrawable = mActivity.getResources().getDrawable(R.mipmap.radio_btn_checked);
        checkedDrawable.setBounds(0, 0, checkedDrawable.getMinimumWidth(), checkedDrawable.getMinimumHeight());
        unCheckedDrawable = mActivity.getResources().getDrawable(R.mipmap.radio_btn_unchecked);
        unCheckedDrawable.setBounds(0, 0, unCheckedDrawable.getMinimumWidth(), unCheckedDrawable.getMinimumHeight());

        initSearchHistoryData();

        getView().showProgressDialog();
        requestWithDirection(Constants.DIRECTION_DOWN);
    }

    private void initSearchHistoryData() {
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_NAMEPLATE_ADD_FROM_LIST);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().UpdateSearchHistoryList(mSearchHistoryList);
        }
    }

    public void requestWithDirection(int direction) {
        if (TextUtils.isEmpty(mNameplateId)) {
            getView().toastShort(mActivity.getString(R.string.nameplate_name_empty));
            return;
        }

        if (direction == Constants.DIRECTION_DOWN) {
            page = 1;
        } else {
            page++;
        }

        RetrofitServiceHelper.getInstance().getNameplateUnbindDevices(page, 20, mNameplateId,mSearchText)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<NameplateBindDeviceRsp>(this) {
            @Override
            public void onCompleted(NameplateBindDeviceRsp nameplateBindDeviceRsp) {
                if (direction == Constants.DIRECTION_DOWN) {
                    mList.clear();
                    mSelectList.clear();
                    if (mBindList != null && mBindList.size() > 0) {
                        mList.addAll(mBindList);
                        mSelectList.addAll(mBindList);
                    }
                }
                List<NamePlateInfo> data = nameplateBindDeviceRsp.getData();
                if (data != null && data.size() > 0) {
                    //已绑定的设备添加到集合的开始位置，然后从下面的数据中剔除已添加的数据
                    dealData(data);
                }
                getView().updateData(mList);
                updateStatus();

                getView().onPullRefreshComplete();
                getView().dismissProgressDialog();

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                if (direction == Constants.DIRECTION_UP) {
                    page--;

                }
                getView().dismissProgressDialog();
                getView().onPullRefreshComplete();
                getView().toastShort(errorMsg);
            }
        });
    }

//    public void requestWithDirection(int direction,String text) {
////        getView().setRelationLayoutVisible(false);
//        getView().showProgressDialog();
//        if (direction == Constants.DIRECTION_DOWN) {
//            page = 1;
//            RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(page, null, null, null, text).subscribeOn
//                    (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {
//
//
//                @Override
//                public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
//                    mList.clear();
//                    List<DeviceInfo> data = deviceInfoListRsp.getData();
//                    if (data != null && data.size() > 0) {
//                        dealData(data);
//                    }
//                    getView().updateData(mList);
//                    getView().onPullRefreshComplete();
//                    getView().dismissProgressDialog();
//
//
//                    mSelectList.clear();
//                    updateStatus();
//
//                }
//
//                @Override
//                public void onErrorMsg(int errorCode, String errorMsg) {
//                    getView().dismissProgressDialog();
//                    getView().onPullRefreshComplete();
//                    getView().toastShort(errorMsg);
//                }
//            });
//        } else {
//            page++;
//            RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(page, null, null, null, null).subscribeOn
//                    (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {
//
//
//                @Override
//                public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
//                    List<DeviceInfo> data = deviceInfoListRsp.getData();
//                    if (data != null && data.size() > 0) {
//                        dealData(data);
//                    }
//                    getView().updateData(mList);
//                    getView().onPullRefreshComplete();
//                    getView().dismissProgressDialog();
//
//
//
//                    updateStatus();
//                }
//
//                @Override
//                public void onErrorMsg(int errorCode, String errorMsg) {
//                    page--;
//                    getView().dismissProgressDialog();
//                    getView().onPullRefreshComplete();
//                    getView().toastShort(errorMsg);
//                }
//            });
//        }
//    }

    private void dealData(List<NamePlateInfo> data) {

        for (NamePlateInfo datum : data) {

            if (isBindListContain(datum)) {
                continue;
            }

            DeviceTypeStyles configDeviceType = PreferencesHelper.getInstance().getConfigDeviceType(datum.getDeviceType());
            if (configDeviceType != null) {
                String category = configDeviceType.getCategory();
                String mergeType = configDeviceType.getMergeType();
                MergeTypeStyles mergeTypeStyles = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
                if (mergeTypeStyles != null) {
                    datum.deviceTypeName = mergeTypeStyles.getName();
                    if (!TextUtils.isEmpty(category)) {
                        datum.deviceTypeName = datum.deviceTypeName + category;
                    }
                    if (TextUtils.isEmpty(mergeTypeStyles.getImage())) {
                        datum.iconUrl = "";
                    } else {
                        datum.iconUrl = mergeTypeStyles.getImage();
                    }
                } else {
                    datum.deviceTypeName = mActivity.getString(R.string.unknown);
                    datum.iconUrl = "";
                }

            } else {
                datum.deviceTypeName = mActivity.getString(R.string.unknown);
                datum.iconUrl = "";
            }
            mList.add(datum);
        }
    }

    private boolean isBindListContain(NamePlateInfo datum) {
        if (mBindList == null || mBindList.size() < 1) {
            return false;
        }

        for (NamePlateInfo namePlateInfo : mBindList) {
            if (namePlateInfo.getSn().equals(datum.getSn())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        mSelectList.clear();
        if (mBindList != null) {
            mBindList.clear();
        }
        mList.clear();

    }

    public void doChecked(int position) {
        NamePlateInfo model = mList.get(position);
        model.isCheck = !model.isCheck;
        if (model.isCheck) {
            if (!mSelectList.contains(model)) {
                mSelectList.add(model);

            }
        } else {
            mSelectList.remove(model);
        }

        updateStatus();
    }

    private void updateStatus() {
        getView().setSelectSize(mSelectList.size());

        if (mSelectList.size() != mList.size() || mList.size() == 0) {
            isCheckAll = false;
            getView().setCheckedDrawable(unCheckedDrawable);
        } else {
            isCheckAll = true;
            getView().setCheckedDrawable(checkedDrawable);
        }

        getView().setAddStatus(mSelectList.size() != 0);
    }

    public void doSelectAll() {
        mSelectList.clear();
        if (isCheckAll) {
            isCheckAll = false;
            getView().setCheckedDrawable(unCheckedDrawable);
        } else {
            isCheckAll = true;
            getView().setCheckedDrawable(checkedDrawable);
            mSelectList.addAll(mList);
        }

        for (NamePlateInfo model : mList) {
            model.isCheck = isCheckAll;
        }

        getView().notifyDataAll();

        updateStatus();
    }

    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
//        PreferencesHelper.getInstance().saveSearchHistoryText(text, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_CONTRACT);
//        mSearchHistoryList.remove(text);
//        mSearchHistoryList.add(0, text);
        List<String> contractList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_NAMEPLATE_ADD_FROM_LIST, text);
        mSearchHistoryList.clear();
        mSearchHistoryList.addAll(contractList);
        getView().UpdateSearchHistoryList(mSearchHistoryList);
    }

    public void requestSearchData(int directionDown, String text) {
        mSearchText = text;
        getView().showProgressDialog();
        requestWithDirection(directionDown);

    }

    public void doAddSensorList() {
        if ("deploy".equals(mOriginType)) {
            doDeploy();
        } else if ("nameplate_detail".equals(mOriginType)) {
            getView().showConfirmDialog();
        } else {
            getView().toastShort(mActivity.getString(R.string.unknown_error));
        }
    }

    private void doDeploy() {
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_ASSOCIATE_SENSOR_FROM_LIST;
        eventData.data = mSelectList;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }

    public void doAssociateSensor() {
        if (TextUtils.isEmpty(mNameplateId)) {
            getView().toastShort(mActivity.getString(R.string.nameplate_id_null));
            return;
        }

        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().doBindDevices(mNameplateId,mSelectList)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<Integer>>(this) {
            @Override
            public void onCompleted(ResponseResult<Integer> nameplateAssociateDeviceRsp) {
                Integer data = nameplateAssociateDeviceRsp.getData();
                if (data != null && data > 0) {
                    getView().toastShort(mActivity.getString(R.string.associated_sensor_success));

                    EventData eventData = new EventData();
                    eventData.code = Constants.EVENT_DATA_ASSOCIATE_SENSOR_FROM_DETAIL;
                    EventBus.getDefault().post(eventData);
                    getView().dismissProgressDialog();
                    getView().finishAc();
                }else{
                    getView().dismissProgressDialog();
                    getView().toastShort(mActivity.getString(R.string.associated_sensor_fail));
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().toastShort(errorMsg);
                getView().dismissProgressDialog();
            }
        });
    }
}
