package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployMonitorCheckActivity;
import com.sensoro.smartcity.adapter.model.EarlyWarningthresholdDialogUtilsAdapterModel;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorLocalCheckFragmentView;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.WidgetUtil;

import java.util.ArrayList;

import static com.sensoro.smartcity.constant.Constants.DEVICE_CONTROL_DEVICE_TYPES;
import static com.sensoro.smartcity.constant.Constants.TYPE_SCAN_DEPLOY_DEVICE;
import static com.sensoro.smartcity.constant.Constants.TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE;
import static com.sensoro.smartcity.constant.Constants.TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE;
import static com.sensoro.smartcity.constant.Constants.TYPE_SCAN_DEPLOY_STATION;
import static com.sensoro.smartcity.constant.Constants.TYPE_SCAN_INSPECTION;
import static com.sensoro.smartcity.presenter.DeployMonitorCheckActivityPresenter.deployAnalyzerModel;

public class DeployMonitorLocalCheckFragmentPresenter extends BasePresenter<IDeployMonitorLocalCheckFragmentView> {
    private Activity mActivity;
    private final ArrayList<String> pickerStrings = new ArrayList<>();
    private ArrayList<EarlyWarningthresholdDialogUtilsAdapterModel> overCurrentDataList;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        init();
        initPickerData();
        initOverCurrentData();
    }

    private void initPickerData() {
        pickerStrings.addAll(Constants.materialValueMap.keySet());
        getView().updatePvCustomOptions(pickerStrings);
    }

    private void initOverCurrentData() {
        overCurrentDataList = new ArrayList<>();
        EarlyWarningthresholdDialogUtilsAdapterModel model = new EarlyWarningthresholdDialogUtilsAdapterModel();
        model.content = mActivity.getString(R.string.over_current_description_one);
        overCurrentDataList.add(model);
        EarlyWarningthresholdDialogUtilsAdapterModel model1 = new EarlyWarningthresholdDialogUtilsAdapterModel();
        model1.content = mActivity.getString(R.string.over_current_description_two);
        overCurrentDataList.add(model1);

    }

    private void init() {
        getView().setDeviceSn(deployAnalyzerModel.sn);
        switch (deployAnalyzerModel.deployType) {
            case TYPE_SCAN_DEPLOY_STATION:
                getView().setDeployDeviceType("基站");
                //基站部署
//                getView().setAlarmContactAndPicAndMiniProgramVisible(false);
//                getView().setDeployPhotoVisible(false);
//                getView().setDeviceSn(mActivity.getString(R.string.device_number) + deployAnalyzerModel.sn);
//                if (!TextUtils.isEmpty(deployAnalyzerModel.nameAndAddress)) {
//                    getView().setNameAddressText(deployAnalyzerModel.nameAndAddress);
//                }
                break;
            case TYPE_SCAN_DEPLOY_DEVICE:
            case TYPE_SCAN_DEPLOY_INSPECTION_DEVICE_CHANGE:
            case TYPE_SCAN_DEPLOY_MALFUNCTION_DEVICE_CHANGE:
                //不论更换还是部署都需要安装检测
                String deviceTypeName = WidgetUtil.getDeviceMainTypeName(deployAnalyzerModel.deviceType);
                getView().setDeployDeviceType(deviceTypeName);
                //TODO 暂时只针对ancre的电器火灾并且排除掉泛海三江电气火灾
                boolean isFire = DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
//                getView().setDeployDeviceConfigVisible(isFire);
                //巡检设备更换
//                getView().setAlarmContactAndPicAndMiniProgramVisible(true);
//                getView().setDeployPhotoVisible(true);
//                getView().updateUploadTvText(mActivity.getString(R.string.replacement_equipment));
//                getView().setDeployDetailArrowWeChatVisible(false);
                break;
            case TYPE_SCAN_INSPECTION:
                //扫描巡检设备
                break;
            default:
                break;
        }
//        getView().updateUploadState(true);
        String deviceTypeName = WidgetUtil.getDeviceMainTypeName(deployAnalyzerModel.deviceType);
        getView().setDeployDeviceType(deviceTypeName);
        //TODO 暂时只针对ancre的电器火灾并且排除掉泛海三江电气火灾
        boolean isFire = DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
        if (!AppUtils.isChineseLanguage()) {
            //TODO 英文版控制不显示小程序账号
            deployAnalyzerModel.weChatAccount = null;
        }
    }

    @Override
    public void onDestroy() {
        pickerStrings.clear();
    }

    public void doUploadDeployMonitorInfo() {
        DeployMonitorCheckActivityPresenter.deployAnalyzerModel.address = "1234";
        if (mActivity instanceof DeployMonitorCheckActivity) {
            ((DeployMonitorCheckActivity) mActivity).setDeployMonitorStep(2);
        }
    }

    public void showOverCurrentDialog() {
        if (isAttachedView()) {
            getView().showOverCurrentDialog(overCurrentDataList);
        }
    }

    public void doCustomOptionPickerItemSelect(int position) {
        String tx = pickerStrings.get(position);
        if (!TextUtils.isEmpty(tx)) {
            getView().setWireDiameterText(tx);
        }
    }
}
