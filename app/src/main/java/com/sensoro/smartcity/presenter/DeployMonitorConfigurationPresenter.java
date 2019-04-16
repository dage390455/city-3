package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.DeployMonitorConfigurationActivity;
import com.sensoro.smartcity.adapter.model.EarlyWarningthresholdDialogUtilsAdapterModel;
import com.sensoro.smartcity.analyzer.DeployConfigurationAnalyzer;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorConfigurationView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeployControlSettingData;
import com.sensoro.smartcity.server.bean.MonitorPointOperationTaskResultInfo;
import com.sensoro.smartcity.server.response.MonitorPointOperationRequestRsp;
import com.sensoro.smartcity.util.AppUtils;
import com.sensoro.smartcity.util.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.NumberFormat;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DeployMonitorConfigurationPresenter extends BasePresenter<IDeployMonitorConfigurationView> implements Constants, IOnCreate {
    private Activity mActivity;
    private DeployAnalyzerModel deployAnalyzerModel;
    private int[] mMinMaxValue;
    private ArrayList<EarlyWarningthresholdDialogUtilsAdapterModel> overCurrentDataList;
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
    private final ArrayList<String> pickerStrings = new ArrayList<>();
    private DeployControlSettingData deployControlSettingData;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        deployAnalyzerModel = (DeployAnalyzerModel) mActivity.getIntent().getSerializableExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
        configurationSource = mActivity.getIntent().getIntExtra(EXTRA_DEPLOY_CONFIGURATION_ORIGIN_TYPE, DEPLOY_CONFIGURATION_SOURCE_TYPE_DEVICE_DETAIL);
        switch (configurationSource) {
            case DEPLOY_CONFIGURATION_SOURCE_TYPE_DEPLOY_DEVICE:
                //部署
                getView().setTitleImvArrowsLeftVisible(true);
                getView().setTitleTvSubtitleVisible(false);
                getView().setAcDeployConfigurationTvConfigurationText(mActivity.getString(R.string.save));
                deployControlSettingData = (DeployControlSettingData) mActivity.getIntent().getSerializableExtra(Constants.EXTRA_DEPLOY_CONFIGURATION_SETTING_DATA);
                if (deployControlSettingData != null) {
                    Double diameterValue = deployControlSettingData.getWireDiameter();
                    if (diameterValue != null) {
                        NumberFormat nf = NumberFormat.getInstance();
                        String formatStr = nf.format(diameterValue);
                        getView().setInputDiameterValueText(formatStr);
                    }
                    Integer initValue = deployControlSettingData.getSwitchSpec();
                    if (initValue != null) {
                        getView().setInputCurrentText(String.valueOf(initValue));
                    }
                    Integer wireMaterial = deployControlSettingData.getWireMaterial();
                    if (0 == wireMaterial) {
                        getView().setInputWireMaterialText(mActivity.getString(R.string.cu));
                    } else if (1 == wireMaterial) {
                        getView().setInputWireMaterialText(mActivity.getString(R.string.al));
                    }
                }
                break;
            case DEPLOY_CONFIGURATION_SOURCE_TYPE_DEVICE_DETAIL:
                getView().setTitleImvArrowsLeftVisible(false);
                getView().setTitleTvSubtitleVisible(true);
                onCreate();
                getView().setAcDeployConfigurationTvConfigurationText(mActivity.getString(R.string.air_switch_config));
                //详情
                break;

        }
        getView().setLlAcDeployConfigurationDiameterVisible(needDiameter());
        init();
    }

    private void init() {
        mMinMaxValue = DeployConfigurationAnalyzer.analyzeDeviceType(deployAnalyzerModel.deviceType);
        if (mMinMaxValue == null) {
            getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_failed));
        } else {
            getView().setTvEnterValueRange(mMinMaxValue[0], mMinMaxValue[1]);
        }
        initOverCurrentData();
        initPickerData();

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

    @Override
    public void onDestroy() {
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
        mHandler.removeCallbacksAndMessages(null);
        pickerStrings.clear();
    }


    public void doConfiguration(String inputCurrent, String material, String diameter, String actualCurrent) {
        int materialValue = 0;
        double diameterValue = 0;
        Integer mEnterValue;
        try {
            if (TextUtils.isEmpty(inputCurrent)) {
                getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.enter_the_correct_number_format));
                return;
            }
            if (mMinMaxValue == null) {
                getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_failed));
                return;
            }
            try {
                int tempValue = Integer.parseInt(inputCurrent);
                if (tempValue < mMinMaxValue[0] || tempValue > mMinMaxValue[1]) {
                    getView().toastShort(mActivity.getString(R.string.empty_open_rated_current_is_out_of_range));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.enter_the_correct_number_format));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.enter_the_correct_number_format));
            return;
        }
        if (needDiameter()) {
            if (mActivity.getString(R.string.cu).equals(material)) {
                materialValue = 0;
            } else if (mActivity.getString(R.string.al).equals(material)) {
                materialValue = 1;
            }
            if (TextUtils.isEmpty(diameter)) {
                getView().toastShort(mActivity.getString(R.string.enter_wire_diameter_tip));
                return;
            }
            try {
                diameterValue = Double.parseDouble(diameter);
            } catch (Exception e) {
                e.printStackTrace();
                getView().toastShort(mActivity.getString(R.string.diameter) + mActivity.getString(R.string.enter_the_correct_number_format));
                return;
            }
        }

        try {
            if (!TextUtils.isEmpty(actualCurrent) && actualCurrent.endsWith("A")) {
                actualCurrent = actualCurrent.substring(0, actualCurrent.lastIndexOf("A"));
            } else {
                getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.enter_the_correct_number_format));
                return;
            }
            if (mMinMaxValue == null) {
                getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_failed));
                return;
            }
            try {
                mEnterValue = Integer.parseInt(actualCurrent);
                if (mEnterValue < mMinMaxValue[0] || mEnterValue > mMinMaxValue[1]) {
//                    getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.monitor_point_operation_error_value_range) + mMinMaxValue[0] + "-" + mMinMaxValue[1]);
                    getView().toastShort(mActivity.getString(R.string.wire_current_carrying_capacity_is_not_within_the_open_range));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.enter_the_correct_number_format));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.enter_the_correct_number_format));
            return;
        }
        switch (configurationSource) {
            case DEPLOY_CONFIGURATION_SOURCE_TYPE_DEPLOY_DEVICE:
                //部署
                EventData eventData = new EventData();
                eventData.code = Constants.EVENT_DATA_DEPLOY_INIT_CONFIG_CODE;
                deployControlSettingData = new DeployControlSettingData();
                deployControlSettingData.setSwitchSpec(mEnterValue);
                deployControlSettingData.setWireDiameter(diameterValue);
                deployControlSettingData.setWireMaterial(materialValue);
                eventData.data = deployControlSettingData;
                EventBus.getDefault().post(eventData);
                getView().finishAc();
                break;
            case DEPLOY_CONFIGURATION_SOURCE_TYPE_DEVICE_DETAIL:
                //详情
                requestCmd(mEnterValue, materialValue, diameterValue);
                break;
        }

    }

    private void requestCmd(final Integer value, final int material, final Double diameter) {
        ArrayList<String> sns = new ArrayList<>();
        sns.add(deployAnalyzerModel.sn);
        getView().showOperationTipLoadingDialog();
        mScheduleNo = null;
        RetrofitServiceHelper.getInstance().doMonitorPointOperation(sns, "config", null, null, value, material, diameter)
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
                        mHandler.postDelayed(DeviceTaskOvertime, 10 * 1000);
                    } else {
                        getView().dismissOperatingLoadingDialog();
                        getView().showErrorTipDialog(mActivity.getString(R.string.monitor_point_operation_schedule_no_error));

                    }
                    deployControlSettingData = new DeployControlSettingData();
                    deployControlSettingData.setSwitchSpec(value);
                    deployControlSettingData.setWireDiameter(diameter);
                    deployControlSettingData.setWireMaterial(material);
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissOperatingLoadingDialog();
                getView().showErrorTipDialog(errorMsg);
            }
        });

    }

    public boolean needDiameter() {
        return Constants.DEVICE_CONTROL_DEVICE_TYPES.contains(deployAnalyzerModel.deviceType);
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_SOCKET_MONITOR_POINT_OPERATION_TASK_RESULT:
                if (data instanceof MonitorPointOperationTaskResultInfo) {
                    try {
                        LogUtils.loge("EVENT_DATA_SOCKET_MONITOR_POINT_OPERATION_TASK_RESULT --->>");
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                    MonitorPointOperationTaskResultInfo info = (MonitorPointOperationTaskResultInfo) data;
                    final String scheduleNo = info.getScheduleNo();
                    if (!TextUtils.isEmpty(scheduleNo) && info.getTotal() == info.getComplete()) {
                        String[] split = scheduleNo.split(",");
                        if (split.length > 0) {
                            final String temp = split[0];
                            if (!TextUtils.isEmpty(temp)) {
                                if (AppUtils.isActivityTop(mActivity, DeployMonitorConfigurationActivity.class)) {
                                    mActivity.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (!TextUtils.isEmpty(mScheduleNo) && mScheduleNo.equals(temp)) {
                                                mHandler.removeCallbacks(DeviceTaskOvertime);
                                                if (isAttachedView()) {
                                                    getView().dismissOperatingLoadingDialog();
                                                    getView().showOperationSuccessToast();
                                                    //
                                                    pushConfigResult();
                                                    mHandler.postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            getView().finishAc();
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
                }
                break;
        }
    }

    public void showOverCurrentDialog() {
        if (isAttachedView()) {
            getView().showOverCurrentDialog(overCurrentDataList);
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

    public void doCustomOptionPickerItemSelect(int position) {
        String tx = pickerStrings.get(position);
        if (!TextUtils.isEmpty(tx)) {
            getView().setInputDiameterValueText(tx);
        }
    }
}
