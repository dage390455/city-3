package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.bean.DeployControlSettingData;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IDeployRecordConfigCommonElectActivityView;
import com.sensoro.common.utils.WidgetUtil;

public class DeployRecordConfigCommonElectActivityPresenter extends BasePresenter<IDeployRecordConfigCommonElectActivityView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        Object bundleValue = getBundleValue(mActivity, Constants.EXTRA_DEPLOY_CONFIGURATION_SETTING_DATA);
        if (bundleValue instanceof DeployControlSettingData) {
            DeployControlSettingData settingData = (DeployControlSettingData) bundleValue;
            final String[] switchSpecStr = {"-", "-", "-", "-"};
            Integer inputValue = settingData.getInputValue();
            if (inputValue != null) {
                switchSpecStr[0] = inputValue + "A";
            }
            Integer wireMaterial = settingData.getWireMaterial();
            if (wireMaterial != null) {
                switch (wireMaterial) {
                    case 0:
                        switchSpecStr[1] = mActivity.getString(R.string.cu);
                        break;
                    case 1:
                        switchSpecStr[1] = mActivity.getString(R.string.al);
                        break;
                    default:
                        break;
                }
            }
            Double wireDiameter = settingData.getWireDiameter();
            if (wireDiameter != null) {
                String formatDouble = WidgetUtil.getFormatDouble(wireDiameter, 2);
                switchSpecStr[2] = formatDouble + "m㎡";
            }

            Integer switchSpec = settingData.getSwitchSpec();
            if (switchSpec != null) {
                switchSpecStr[3] = switchSpec + "A";
            }
            getView().setConfigAirRatedCurrentValue(switchSpecStr[0]);
            getView().setConfigMaterial(switchSpecStr[1]);
            getView().setConfigDiameter(switchSpecStr[2]);
            getView().setConfigActualOverCurrentThreshold(switchSpecStr[3]);
        }

    }


    @Override
    public void onDestroy() {

    }
}
