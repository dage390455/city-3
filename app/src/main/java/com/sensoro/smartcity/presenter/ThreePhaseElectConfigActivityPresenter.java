package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.RecommendedTransformerValueModel;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeployControlSettingData;
import com.sensoro.common.server.bean.MonitorPointOperationTaskResultInfo;
import com.sensoro.common.server.response.MonitorPointOperationRequestRsp;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.analyzer.DeployConfigurationAnalyzer;
import com.sensoro.smartcity.imainviews.IThreePhaseElectConfigActivityView;
import com.sensoro.smartcity.model.MaterialValueModel;
import com.sensoro.smartcity.model.WireMaterialDiameterModel;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.WidgetUtil;
import com.sensoro.smartcity.widget.dialog.RecommendedTransformerDialogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sensoro.common.constant.Constants.DEPLOY_CONFIGURATION_SOURCE_TYPE_DEPLOY_DEVICE;
import static com.sensoro.common.constant.Constants.DEPLOY_CONFIGURATION_SOURCE_TYPE_DEVICE_DETAIL;
import static com.sensoro.smartcity.constant.CityConstants.MATERIAL_VALUE_MAP;


public class ThreePhaseElectConfigActivityPresenter extends BasePresenter<IThreePhaseElectConfigActivityView> implements RecommendedTransformerDialogUtils.OnRecommendedTransformerDialogUtilsListener, IOnCreate {
    private Activity mActivity;
    private ArrayList<WireMaterialDiameterModel> mInLineList;
    private ArrayList<WireMaterialDiameterModel> mOutLineList;
    private ArrayList<String> pickerStrings;
    private boolean mIsInlineClick;
    private int mClickPosition;
    private boolean mIsAction;
    private int[] mMinMaxValue;
    private RecommendedTransformerDialogUtils recommendedTransformerDialogUtils;
    private DeployControlSettingData deployControlSettingData = new DeployControlSettingData();
    private DeployAnalyzerModel deployAnalyzerModel;
    private int configurationSource;
    private String mScheduleNo;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private final Runnable DeviceTaskOvertime = new Runnable() {
        @Override
        public void run() {
            mHandler.removeCallbacks(DeviceTaskOvertime);
            mScheduleNo = null;
            getView().dismissOperatingLoadingDialog();
            getView().showErrorTipDialog(mActivity.getString(R.string.operation_request_time_out));

        }
    };

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        pickerStrings = new ArrayList<>();
        mInLineList = new ArrayList<>(5);
        mOutLineList = new ArrayList<>(5);
        initPickerData();
        recommendedTransformerDialogUtils = new RecommendedTransformerDialogUtils(mActivity);
        recommendedTransformerDialogUtils.setOnRecommendedTransformerDialogUtilsListener(this);
        //
        Object settingDataValue = getBundleValue(mActivity, Constants.EXTRA_DEPLOY_CONFIGURATION_SETTING_DATA);
        Object deviceValue = getBundleValue(mActivity, Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
        Object typeValue = getBundleValue(mActivity, Constants.EXTRA_DEPLOY_CONFIGURATION_ORIGIN_TYPE);
        if (settingDataValue instanceof DeployControlSettingData) {
            deployControlSettingData = (DeployControlSettingData) settingDataValue;
        }
        if (deviceValue instanceof DeployAnalyzerModel) {
            deployAnalyzerModel = (DeployAnalyzerModel) deviceValue;
        }
        if (typeValue instanceof Integer) {
            configurationSource = (int) typeValue;
        }
        mMinMaxValue = DeployConfigurationAnalyzer.analyzeDeviceType(deployAnalyzerModel.deviceType);
        if (mMinMaxValue == null) {
            getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_failed));
        } else {
            getView().setTvEnterValueRange(mMinMaxValue[0], mMinMaxValue[1]);
        }
        //
        int tempValue = 0;
        Integer inputValue = deployControlSettingData.getInputValue();
        List<DeployControlSettingData.wireData> input = deployControlSettingData.getInput();
        List<DeployControlSettingData.wireData> output = deployControlSettingData.getOutput();
        Integer switchSpec = deployControlSettingData.getSwitchSpec();
        switch (configurationSource) {
            case DEPLOY_CONFIGURATION_SOURCE_TYPE_DEPLOY_DEVICE:
                //部署
                getView().setSubtitleText(mActivity.getString(R.string.save));
                break;
            case DEPLOY_CONFIGURATION_SOURCE_TYPE_DEVICE_DETAIL:
                //设备详情 下行
                onCreate();
                getView().setSubtitleText(mActivity.getString(R.string.air_switch_config));
                break;

        }
        //回显值
        if (inputValue != null) {
            tempValue = inputValue;
        }
        if (input != null && input.size() > 0) {
            for (DeployControlSettingData.wireData wireData : input) {
                WireMaterialDiameterModel wireMaterialDiameterModel = new WireMaterialDiameterModel();
                Integer count = wireData.getCount();
                if (count != null) {
                    wireMaterialDiameterModel.count = count;
                }
                Double wireDiameter = wireData.getWireDiameter();
                if (wireDiameter != null) {
                    wireMaterialDiameterModel.diameter = WidgetUtil.getFormatDouble(wireDiameter);
                }
                Integer wireMaterial = wireData.getWireMaterial();
                if (wireMaterial != null) {
                    wireMaterialDiameterModel.material = wireMaterial;
                }
                mInLineList.add(wireMaterialDiameterModel);

            }
        }

        if (output != null && output.size() > 0) {
            for (DeployControlSettingData.wireData wireData : output) {
                WireMaterialDiameterModel wireMaterialDiameterModel = new WireMaterialDiameterModel();
                Integer count = wireData.getCount();
                if (count != null) {
                    wireMaterialDiameterModel.count = count;
                }
                Double wireDiameter = wireData.getWireDiameter();
                if (wireDiameter != null) {
                    wireMaterialDiameterModel.diameter = WidgetUtil.getFormatDouble(wireDiameter);
                }
                Integer wireMaterial = wireData.getWireMaterial();
                if (wireMaterial != null) {
                    wireMaterialDiameterModel.material = wireMaterial;
                }
                mOutLineList.add(wireMaterialDiameterModel);
            }
        }
        if (switchSpec != null) {
            getView().setActualCurrentValue(switchSpec);
        }
        getView().updateInLineData(mInLineList);
        getView().updateOutLineData(mOutLineList);

        if (tempValue != 0) {
            getView().setInputRated(String.valueOf(tempValue));
        }

    }

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        if (mInLineList != null) {
            mInLineList.clear();
        }
        if (mOutLineList != null) {
            mOutLineList.clear();
        }
        if (recommendedTransformerDialogUtils != null) {
            recommendedTransformerDialogUtils.destroy();
        }

    }

    private void initPickerData() {
        pickerStrings.addAll(MATERIAL_VALUE_MAP.keySet());
        List<String> mMaterials = new ArrayList(2);
        List<String> mCounts = new ArrayList(9);
        mMaterials.add(mActivity.getString(R.string.cu));
        mMaterials.add(mActivity.getString(R.string.al));
        mCounts.add("1");
        mCounts.add("2");
        mCounts.add("3");
        mCounts.add("4");
        mCounts.add("5");
        mCounts.add("6");
        mCounts.add("7");
        mCounts.add("8");
        mCounts.add("9");
        mCounts.add("10");
        getView().updatePvCustomOptions(mMaterials, pickerStrings, mCounts);
    }

    public void doOutLineItemClick(int position, boolean isAction) {
        mIsInlineClick = false;
        mClickPosition = position;
        mIsAction = isAction;
        if (!isAction && mClickPosition != -1) {
            WireMaterialDiameterModel model = mOutLineList.get(mClickPosition);
            model.isSelected = true;
            int index = model.count - 1;
            getView().setPickerViewSelectOptions(model.material, pickerStrings.indexOf(String.valueOf(model.diameter)), index);
        }
        getView().updateOutLineData(mOutLineList);
        getView().setPickerTitle(mActivity.getString(R.string.out_line));
        getView().showPickerView();
        //
        handleRecommendTransformer();
    }

    public void doInLineItemClick(int position, boolean isAction) {
        mIsInlineClick = true;
        mClickPosition = position;
        mIsAction = isAction;
        if (!isAction && mClickPosition != -1) {
            WireMaterialDiameterModel model = mInLineList.get(mClickPosition);
            model.isSelected = true;
            int index = model.count - 1;
            getView().setPickerViewSelectOptions(model.material, pickerStrings.indexOf(String.valueOf(model.diameter)), index);
        }
        getView().updateInLineData(mInLineList);
        getView().setPickerTitle(mActivity.getString(R.string.in_line));
        getView().showPickerView();
        //
        handleRecommendTransformer();
    }

    public void doSelectComplete(int material, int diameter, int count) {
        if (mIsAction) {
            WireMaterialDiameterModel model = new WireMaterialDiameterModel(material, pickerStrings.get(diameter), count);
            model.isSelected = false;
            if (mIsInlineClick) {
                mInLineList.add(model);
                getView().updateInLineData(mInLineList);
            } else {
                mOutLineList.add(model);
                getView().updateOutLineData(mOutLineList);
            }
        } else {
            if (mClickPosition != -1) {
                if (mIsInlineClick) {
                    WireMaterialDiameterModel model = mInLineList.get(mClickPosition);
                    model.material = material;
                    model.isSelected = false;
                    model.diameter = pickerStrings.get(diameter);
                    model.count = count;
                    getView().updateInLineData(mInLineList);
                } else {
                    WireMaterialDiameterModel model = mOutLineList.get(mClickPosition);
                    model.material = material;
                    model.isSelected = false;
                    model.diameter = pickerStrings.get(diameter);
                    model.count = count;
                    getView().updateOutLineData(mOutLineList);
                }
            }

        }
        getView().dismissPickerView();
        handleRecommendTransformer();
    }

    public void doDeleteGroup() {
        if (!mIsAction) {
            if (mClickPosition != -1) {
                if (mIsInlineClick) {
                    if (mClickPosition < mInLineList.size()) {
                        mInLineList.remove(mClickPosition);
                        getView().updateInLineData(mInLineList);
                    }
                } else {
                    if (mClickPosition < mOutLineList.size()) {
                        mOutLineList.remove(mClickPosition);
                        getView().updateOutLineData(mOutLineList);
                    }
                }
            }

        }
        getView().dismissPickerView();
        handleRecommendTransformer();
    }

    public void handleRecommendTransformer() {
        deployControlSettingData.getTransformerValueList().clear();
        int inputValue;
        try {
            if (mMinMaxValue == null) {
                getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_failed));
                return;
            }
            inputValue = Integer.parseInt(getView().getEtInputText());
            if (inputValue < mMinMaxValue[0] || inputValue > mMinMaxValue[1]) {
                getView().toastShort(mActivity.getString(R.string.empty_open_rated_current_is_out_of_range) + " " + mMinMaxValue[0] + "~" + mMinMaxValue[1]);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
//            getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.enter_the_correct_number_format));
            getView().toastShort(mActivity.getString(R.string.please_enter_the_configuration_parameters_correctly));
            return;
        }
        int inLineTotal = 0;
        int outLineTotal = 0;
        List<DeployControlSettingData.wireData> input = new ArrayList<>();
        List<DeployControlSettingData.wireData> output = new ArrayList<>();
        int temp;
        try {
            for (WireMaterialDiameterModel model : mInLineList) {
                MaterialValueModel materialValueModel = MATERIAL_VALUE_MAP.get(model.diameter);
                inLineTotal += model.material == 1 ? materialValueModel.alValue : materialValueModel.cuValue * model.count * 1.5f;
                //
                DeployControlSettingData.wireData wireData = new DeployControlSettingData.wireData();
                wireData.setWireMaterial(model.material);
                wireData.setCount(model.count);
                wireData.setWireDiameter(Double.parseDouble(model.diameter));
                input.add(wireData);
            }
            deployControlSettingData.setInput(input);

            for (WireMaterialDiameterModel model : mOutLineList) {
                MaterialValueModel materialValueModel = MATERIAL_VALUE_MAP.get(model.diameter);
                outLineTotal += model.material == 1 ? materialValueModel.alValue : materialValueModel.cuValue * model.count * 1.5f;
                //
                DeployControlSettingData.wireData wireData = new DeployControlSettingData.wireData();
                wireData.setWireMaterial(model.material);
                wireData.setCount(model.count);
                wireData.setWireDiameter(Double.parseDouble(model.diameter));
                output.add(wireData);
            }
            deployControlSettingData.setOutput(output);


            temp = (int) (inputValue * 1.25f);
        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort(mActivity.getString(R.string.empty_open_rated_current_is_out_of_range));
            return;
        }
        temp = Math.min(temp, inLineTotal);
        int actualRatedCurrent = Math.min(temp, outLineTotal);
        //
        if ("acrel_alpha".equals(deployAnalyzerModel.deviceType)) {
            if (inputValue > 0 && inputValue <= 120) {
                //120A/40mA
                //
                RecommendedTransformerValueModel recommendedTransformerValueModel1 = new RecommendedTransformerValueModel();
                recommendedTransformerValueModel1.value = 120;
                recommendedTransformerValueModel1.isRecommend = true;
                deployControlSettingData.setRecommTrans(120);
                //
                RecommendedTransformerValueModel recommendedTransformerValueModel2 = new RecommendedTransformerValueModel();
                recommendedTransformerValueModel2.value = 200;
                //
                RecommendedTransformerValueModel recommendedTransformerValueModel3 = new RecommendedTransformerValueModel();
                recommendedTransformerValueModel3.value = 400;
                deployControlSettingData.getTransformerValueList().add(recommendedTransformerValueModel1);
                deployControlSettingData.getTransformerValueList().add(recommendedTransformerValueModel2);
                deployControlSettingData.getTransformerValueList().add(recommendedTransformerValueModel3);
            } else if (inputValue <= 225) {
                //200A/40mA
                //
                RecommendedTransformerValueModel recommendedTransformerValueModel2 = new RecommendedTransformerValueModel();
                recommendedTransformerValueModel2.value = 200;
                recommendedTransformerValueModel2.isRecommend = true;
                deployControlSettingData.setRecommTrans(200);
                //
                RecommendedTransformerValueModel recommendedTransformerValueModel3 = new RecommendedTransformerValueModel();
                recommendedTransformerValueModel3.value = 400;
                deployControlSettingData.getTransformerValueList().add(recommendedTransformerValueModel2);
                deployControlSettingData.getTransformerValueList().add(recommendedTransformerValueModel3);
            }
//            else if (inputValue <= 400) {
//                //400/40mA
//                RecommendedTransformerValueModel recommendedTransformerValueModel3 = new RecommendedTransformerValueModel();
//                recommendedTransformerValueModel3.value = 400;
//                recommendedTransformerValueModel3.isRecommend = true;
//                deployControlSettingData.setRecommTrans(400);
//                deployControlSettingData.getTransformerValueList().add(recommendedTransformerValueModel3);
//            } else {
//                getView().toastShort(mActivity.getString(R.string.not_matched_current_transformer));
//                return;
//            }
            else {
                //TODO >400 按400算
                RecommendedTransformerValueModel recommendedTransformerValueModel3 = new RecommendedTransformerValueModel();
                recommendedTransformerValueModel3.value = 400;
                recommendedTransformerValueModel3.isRecommend = true;
                deployControlSettingData.setRecommTrans(400);
                deployControlSettingData.getTransformerValueList().add(recommendedTransformerValueModel3);
            }
        } else {
            if (inputValue > 0 && inputValue <= 250) {
                //
                RecommendedTransformerValueModel recommendedTransformerValueModel1 = new RecommendedTransformerValueModel();
                recommendedTransformerValueModel1.value = 250;
                recommendedTransformerValueModel1.isRecommend = true;
                deployControlSettingData.setRecommTrans(250);
                //
                RecommendedTransformerValueModel recommendedTransformerValueModel3 = new RecommendedTransformerValueModel();
                recommendedTransformerValueModel3.value = 400;
                deployControlSettingData.getTransformerValueList().add(recommendedTransformerValueModel1);
                deployControlSettingData.getTransformerValueList().add(recommendedTransformerValueModel3);
            } else {
                //400/40mA
                RecommendedTransformerValueModel recommendedTransformerValueModel3 = new RecommendedTransformerValueModel();
                recommendedTransformerValueModel3.value = 400;
                recommendedTransformerValueModel3.isRecommend = true;
                deployControlSettingData.setRecommTrans(400);
                deployControlSettingData.getTransformerValueList().add(recommendedTransformerValueModel3);

            }
//            else if (inputValue <= 400) {
//                //400/40mA
//                RecommendedTransformerValueModel recommendedTransformerValueModel3 = new RecommendedTransformerValueModel();
//                recommendedTransformerValueModel3.value = 400;
//                recommendedTransformerValueModel3.isRecommend = true;
//                deployControlSettingData.setRecommTrans(400);
//                deployControlSettingData.getTransformerValueList().add(recommendedTransformerValueModel3);
//            } else {
//                getView().toastShort(mActivity.getString(R.string.not_matched_current_transformer));
//                return;
//            }
        }

        deployControlSettingData.setSwitchSpec(actualRatedCurrent);
        deployControlSettingData.setInputValue(inputValue);
        getView().setActualCurrentValue(actualRatedCurrent == 0 ? null : actualRatedCurrent);
    }

    public void onPickerViewDismiss() {
        boolean isNeedUpdate = false;
        if (mIsInlineClick) {
            for (WireMaterialDiameterModel model : mInLineList) {
                if (model.isSelected) {
                    model.isSelected = false;
                    isNeedUpdate = true;
                }
            }
            if (isNeedUpdate) {
                getView().updateInLineData(mInLineList);
                //
                handleRecommendTransformer();
            }
        } else {
            for (WireMaterialDiameterModel model : mOutLineList) {
                if (model.isSelected) {
                    model.isSelected = false;
                    isNeedUpdate = true;
                }
            }

            if (isNeedUpdate) {
                getView().updateOutLineData(mOutLineList);
                //
                handleRecommendTransformer();
            }
        }


    }

    public void doAddInLine() {
        mIsInlineClick = true;
        mIsAction = true;
        mClickPosition = -1;
        getView().setPickerViewSelectOptions(0, 6, 0);
        getView().updateInLineData(mInLineList);
        getView().setPickerTitle(mActivity.getString(R.string.in_line));
        getView().showPickerView();
    }

    public void doAddOutLine() {
        mIsInlineClick = false;
        mClickPosition = -1;
        mIsAction = true;
        getView().setPickerViewSelectOptions(0, 6, 0);
        getView().updateOutLineData(mOutLineList);
        getView().setPickerTitle(mActivity.getString(R.string.out_line));
        getView().showPickerView();
    }

    public void doSave() {
        try {
            if (mMinMaxValue == null) {
                getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_failed));
                return;
            }
            int inputValue = Integer.parseInt(getView().getEtInputText());
            if (inputValue < mMinMaxValue[0] || inputValue > mMinMaxValue[1]) {
                getView().toastShort(mActivity.getString(R.string.empty_open_rated_current_is_out_of_range) + " " + mMinMaxValue[0] + "~" + mMinMaxValue[1]);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
//            getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.enter_the_correct_number_format));
            getView().toastShort(mActivity.getString(R.string.please_enter_the_configuration_parameters_correctly));
            return;
        }
        if (mInLineList.isEmpty()) {
            getView().toastShort(mActivity.getString(R.string.please_add_config_input));
            return;
        }
        if (mOutLineList.isEmpty()) {
            getView().toastShort(mActivity.getString(R.string.please_add_config_out));
            return;
        }
        if (recommendedTransformerDialogUtils != null) {
            List<RecommendedTransformerValueModel> transformerValueList = deployControlSettingData.getTransformerValueList();
            RecommendedTransformerValueModel recommendedTransformerValueModel = transformerValueList.get(0);
            recommendedTransformerDialogUtils.show(transformerValueList, recommendedTransformerValueModel.value + "A");
        }
    }

    @Override
    public void onCancel() {

    }

    @Override
    public void onItemChose(RecommendedTransformerValueModel recommendedTransformerValueModel) {
        deployControlSettingData.setTransformer(recommendedTransformerValueModel.value);
        switch (configurationSource) {
            case DEPLOY_CONFIGURATION_SOURCE_TYPE_DEPLOY_DEVICE:
                //部署
                EventData eventData = new EventData();
                eventData.code = Constants.EVENT_DATA_DEPLOY_INIT_CONFIG_CODE;
                eventData.data = deployControlSettingData;
                EventBus.getDefault().post(eventData);
                getView().finishAc();
                break;
            case DEPLOY_CONFIGURATION_SOURCE_TYPE_DEVICE_DETAIL:
                //设备详情 下行
                requestCmd();
                break;
        }

    }

    private void requestCmd() {
        ArrayList<String> sns = new ArrayList<>();
        sns.add(deployAnalyzerModel.sn);
        getView().showOperationTipLoadingDialog();
        mScheduleNo = null;
        RetrofitServiceHelper.getInstance().doMonitorPointOperation(sns, "config", deployControlSettingData)
                .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<MonitorPointOperationRequestRsp>(this) {
            @Override
            public void onCompleted(MonitorPointOperationRequestRsp response) {
                String scheduleNo = response.getScheduleNo();
                if (TextUtils.isEmpty(scheduleNo)) {
                    getView().dismissOperatingLoadingDialog();
                    getView().showErrorTipDialog(mActivity.getString(R.string.monitor_point_operation_schedule_no_error));
                } else {
                    String[] split = scheduleNo.split(",");
                    if (split.length > 0) {
                        mScheduleNo = split[0];
                        mHandler.removeCallbacks(DeviceTaskOvertime);
                        mHandler.postDelayed(DeviceTaskOvertime, 15 * 1000);
                    } else {
                        getView().dismissOperatingLoadingDialog();
                        getView().showErrorTipDialog(mActivity.getString(R.string.monitor_point_operation_schedule_no_error));

                    }
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissOperatingLoadingDialog();
                getView().showErrorTipDialog(errorMsg);
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(MonitorPointOperationTaskResultInfo monitorPointOperationTaskResultInfo) {
        try {
            LogUtils.loge("EVENT_DATA_SOCKET_MONITOR_POINT_OPERATION_TASK_RESULT --->>");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        final String scheduleNo = monitorPointOperationTaskResultInfo.getScheduleNo();
        if (!TextUtils.isEmpty(scheduleNo) && monitorPointOperationTaskResultInfo.getTotal() == monitorPointOperationTaskResultInfo.getComplete()) {
            String[] split = scheduleNo.split(",");
            if (split.length > 0) {
                final String temp = split[0];
                if (!TextUtils.isEmpty(temp)) {
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!TextUtils.isEmpty(mScheduleNo) && mScheduleNo.equals(temp)) {
                                mHandler.removeCallbacks(DeviceTaskOvertime);
                                if (isAttachedView()) {
                                    getView().dismissPickerView();
                                    getView().dismissOperatingLoadingDialog();
                                    getView().showOperationSuccessToast();
                                    if (recommendedTransformerDialogUtils != null) {
                                        recommendedTransformerDialogUtils.dismiss();
                                    }
                                    //
                                    pushConfigResult();
                                    mHandler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (isAttachedView()) {
                                                getView().finishAc();
                                            }
                                        }
                                    }, 1000);
                                }
                            }
                        }
                    });
                }
            }

        }
    }

    private void pushConfigResult() {
        EventData eventData = new EventData();
        eventData.data = deployControlSettingData;
        eventData.code = Constants.EVENT_DATA_DEPLOY_INIT_CONFIG_CODE;
        EventBus.getDefault().post(eventData);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}
