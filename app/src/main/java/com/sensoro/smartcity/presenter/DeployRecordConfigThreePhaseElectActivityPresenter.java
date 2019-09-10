package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.server.bean.DeployControlSettingData;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IDeployRecordConfigThreePhaseElectActivityView;
import com.sensoro.common.utils.WidgetUtil;

import java.util.List;

public class DeployRecordConfigThreePhaseElectActivityPresenter extends BasePresenter<IDeployRecordConfigThreePhaseElectActivityView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        Object bundleValue = getBundleValue(mActivity, Constants.EXTRA_DEPLOY_CONFIGURATION_SETTING_DATA);
        if (bundleValue instanceof DeployControlSettingData) {
            DeployControlSettingData settingData = (DeployControlSettingData) bundleValue;
            String[] values = {"-", "-", "-", "-", "-", "-"};
            Integer inputValue = settingData.getInputValue();
            if (inputValue != null) {
                values[0] = inputValue + "A";
            }
            List<DeployControlSettingData.wireData> input = settingData.getInput();
            if (input != null && input.size() > 0) {
                values[1] = getValue(input);
            }
            List<DeployControlSettingData.wireData> output = settingData.getOutput();
            if (output != null && output.size() > 0) {
                values[2] = getValue(output);
            }
            Integer switchSpec = settingData.getSwitchSpec();
            if (switchSpec != null) {
                values[3] = switchSpec + "A";
            }
            Integer recommTrans = settingData.getRecommTrans();
            if (recommTrans != null) {
                values[4] = recommTrans + "A";
            }
            Integer transformer = settingData.getTransformer();
            if (transformer != null) {
                values[5] = transformer + "A";
            }
            getView().setConfigAirRatedCurrentValue(values[0]);
            getView().setConfigInput(values[1]);
            getView().setConfigOutput(values[2]);
            getView().setConfigActualOverCurrentThreshold(values[3]);
            getView().setConfigRecommendTrans(values[4]);
            getView().setConfigActualTrans(values[5]);

        }
    }

    private String getValue(List<DeployControlSettingData.wireData> list) {
        StringBuilder inputStr = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            DeployControlSettingData.wireData wireData = list.get(i);
            Integer wireMaterial = wireData.getWireMaterial();
            if (wireMaterial != null) {
                switch (wireMaterial) {
                    case 0:
                        inputStr.append(mActivity.getString(R.string.cu)).append(" ");
                        break;
                    case 1:
                        inputStr.append(mActivity.getString(R.string.al)).append(" ");
                        break;
                    default:
                        break;
                }
            }
            Double wireDiameter = wireData.getWireDiameter();
            if (wireDiameter != null) {
                String formatDouble = WidgetUtil.getFormatDouble(wireDiameter, 2);
                inputStr.append(formatDouble).append("m㎡");
            }
            Integer count = wireData.getCount();
            if (count != null) {
                inputStr.append(" ").append("x").append(" ").append(count);
            }
            if (i != list.size() - 1) {
                inputStr.append("\n");

            }

        }
        return inputStr.toString();
    }

    @Override
    public void onDestroy() {

    }
}
