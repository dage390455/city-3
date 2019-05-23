package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import com.amap.api.maps.model.Text;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceInfo;
import com.sensoro.common.server.bean.DeviceTypeStyles;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.server.response.DeviceInfoListRsp;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IDeployNameplateAddSensorFromListActivityView;
import com.sensoro.smartcity.model.AddSensorFromListModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DeployNameplateAddSensorFromListActivityPresenter extends BasePresenter<IDeployNameplateAddSensorFromListActivityView> {
    private Activity mActivity;
    private int page;
    private ArrayList<AddSensorFromListModel> mList = new ArrayList<>();
    private ArrayList<AddSensorFromListModel> mSelectList = new ArrayList<>();
    private Drawable checkedDrawable;
    private Drawable unCheckedDrawable;
    private boolean isCheckAll = false;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        checkedDrawable = mActivity.getResources().getDrawable(R.drawable.radio_btn_checked);
        checkedDrawable.setBounds(0, 0, checkedDrawable.getMinimumWidth(), checkedDrawable.getMinimumHeight());
        unCheckedDrawable = mActivity.getResources().getDrawable(R.drawable.radio_btn_unchecked);
        unCheckedDrawable.setBounds(0, 0, unCheckedDrawable.getMinimumWidth(), unCheckedDrawable.getMinimumHeight());


        getView().showProgressDialog();
        requestWithDirection(Constants.DIRECTION_DOWN);
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
                    mList.clear();
                    List<DeviceInfo> data = deviceInfoListRsp.getData();
                    if (data != null && data.size() > 0) {
                        dealData(data);
                    }
                    getView().updateData(mList);
                    getView().onPullRefreshComplete();
                    getView().dismissProgressDialog();



                    mSelectList.clear();
                    updateStatus();

                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().onPullRefreshComplete();
                    getView().toastShort(errorMsg);
                }
            });
        } else {
            page++;
            RetrofitServiceHelper.getInstance().getDeviceBriefInfoList(page, null, null, null, null).subscribeOn
                    (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>(this) {


                @Override
                public void onCompleted(DeviceInfoListRsp deviceInfoListRsp) {
                    List<DeviceInfo> data = deviceInfoListRsp.getData();
                    if (data != null && data.size() > 0) {
                        dealData(data);
                    }
                    getView().updateData(mList);
                    getView().onPullRefreshComplete();
                    getView().dismissProgressDialog();



                    updateStatus();
                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    page--;
                    getView().dismissProgressDialog();
                    getView().onPullRefreshComplete();
                    getView().toastShort(errorMsg);
                }
            });
        }
    }

    private void dealData(List<DeviceInfo> data) {
        for (DeviceInfo datum : data) {
            AddSensorFromListModel model = new AddSensorFromListModel();
            model.sn = datum.getSn();
            if (TextUtils.isEmpty(datum.getName())) {
                model.name = datum.getSn();

            } else {
                model.name = datum.getName();

            }
            DeviceTypeStyles configDeviceType = PreferencesHelper.getInstance().getConfigDeviceType(datum.getDeviceType());
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

            mList.add(model);
        }
    }

    @Override
    public void onDestroy() {

    }

    public void doChecked(int position) {
        AddSensorFromListModel model = mList.get(position);
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
        getView().setSelectSize(""+mSelectList.size());

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

        for (AddSensorFromListModel model : mList) {
            model.isCheck = isCheckAll;
        }

        getView().notifyDataAll();

        updateStatus();
    }

}
