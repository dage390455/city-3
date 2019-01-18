package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.analyzer.DeployConfigurationAnalyzer;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployMonitorConfigurationView;
import com.sensoro.smartcity.model.DeployAnalyzerModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.bean.DeployControlSettingData;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

public class DeployMonitorConfigurationPresenter extends BasePresenter<IDeployMonitorConfigurationView> {
    private Activity mActivity;
    private boolean bleHasOpen;
    private DeployAnalyzerModel deployAnalyzerModel;
    private Integer mEnterValue;
    private int[] mMinMaxValue;
    private Double diameterValue;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        deployAnalyzerModel = (DeployAnalyzerModel) mActivity.getIntent().getSerializableExtra(Constants.EXTRA_DEPLOY_ANALYZER_MODEL);
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
    }

    @Override
    public void onDestroy() {
    }


    public void doConfiguration(String valueStr, String diameter) {
        checkAndConnect(valueStr, diameter);
    }

    private void checkAndConnect(String valueStr, String diameter) {
        if (mMinMaxValue == null) {
            getView().toastShort(mActivity.getString(R.string.deploy_configuration_analyze_failed));
            return;
        }
        try {
            mEnterValue = Integer.parseInt(valueStr);
            if (mEnterValue < mMinMaxValue[0] || mEnterValue > mMinMaxValue[1]) {
                getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.monitor_point_operation_error_value_range) + mMinMaxValue[0] + "-" + mMinMaxValue[1]);
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort(mActivity.getString(R.string.electric_current) + mActivity.getString(R.string.enter_the_correct_number_format));
            return;
        }
        if (needDiameter()) {
            if (TextUtils.isEmpty(diameter)) {
                getView().toastShort(mActivity.getString(R.string.enter_wire_diameter_tip));
                return;
            }
            try {
                diameterValue = Double.parseDouble(diameter);
                if (diameterValue < 0 || diameterValue >= 200) {
                    getView().toastShort(mActivity.getString(R.string.diameter) + String.format(Locale.CHINESE, "%s%d-%d", mActivity.getString(R.string.monitor_point_operation_error_value_range), 0, 200));
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
                getView().toastShort(mActivity.getString(R.string.diameter) + mActivity.getString(R.string.enter_the_correct_number_format));
                return;
            }
        }
        configCompleted();
    }

    public boolean needDiameter() {
        return "mantun_fires".equals(deployAnalyzerModel.deviceType);
    }


    private void configCompleted() {
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_INIT_CONFIG_CODE;
        DeployControlSettingData deployControlSettingData = new DeployControlSettingData();
        deployControlSettingData.setInitValue(mEnterValue);
        deployControlSettingData.setDiameterValue(diameterValue);
        eventData.data = deployControlSettingData;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }
}
