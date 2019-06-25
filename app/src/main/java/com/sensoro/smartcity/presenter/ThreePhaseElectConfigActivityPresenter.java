package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.DeployAnalyzerModel;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.RecommendedTransformerValueModel;
import com.sensoro.common.server.bean.DeployControlSettingData;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.imainviews.IThreePhaseElectConfigActivityView;
import com.sensoro.smartcity.model.MaterialValueModel;
import com.sensoro.smartcity.model.WireMaterialDiameterModel;
import com.sensoro.smartcity.widget.dialog.RecommendedTransformerDialogUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.sensoro.common.constant.Constants.DEPLOY_CONFIGURATION_SOURCE_TYPE_DEPLOY_DEVICE;
import static com.sensoro.common.constant.Constants.DEPLOY_CONFIGURATION_SOURCE_TYPE_DEVICE_DETAIL;
import static com.sensoro.smartcity.constant.CityConstants.MATERIAL_VALUE_MAP;


public class ThreePhaseElectConfigActivityPresenter extends BasePresenter<IThreePhaseElectConfigActivityView> implements RecommendedTransformerDialogUtils.OnRecommendedTransformerDialogUtilsListener {
    private Activity mActivity;
    private ArrayList<WireMaterialDiameterModel> mInLineList;
    private ArrayList<WireMaterialDiameterModel> mOutLineList;
    private ArrayList<String> pickerStrings;
    private boolean mIsInlineClick;
    private int mClickPosition;
    private boolean mIsAction;
    private RecommendedTransformerDialogUtils recommendedTransformerDialogUtils;
    private DeployControlSettingData deployControlSettingData = new DeployControlSettingData();
    private DeployAnalyzerModel deployAnalyzerModel;
    private int configurationSource;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        pickerStrings = new ArrayList<>();
        mInLineList = new ArrayList<>(5);
        mOutLineList = new ArrayList<>(5);
        initPickerData();
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
        //
        int tempValue = 0;
        switch (configurationSource) {
            case DEPLOY_CONFIGURATION_SOURCE_TYPE_DEPLOY_DEVICE:
                //部署
                //回显
                Integer inputValue = deployControlSettingData.getInputValue();
                if (inputValue!=null){
                    tempValue = inputValue;
                }
                List<DeployControlSettingData.wireData> input = deployControlSettingData.getInput();
                if (input != null && input.size() > 0) {
                    for (DeployControlSettingData.wireData wireData : input) {
                        WireMaterialDiameterModel wireMaterialDiameterModel = new WireMaterialDiameterModel();
                        Integer count = wireData.getCount();
                        if (count != null) {
                            wireMaterialDiameterModel.count = count;
                        }
                        Double wireDiameter = wireData.getWireDiameter();
                        if (wireDiameter != null) {
                            wireMaterialDiameterModel.diameter = String.valueOf(wireDiameter);
                        }
                        Integer wireMaterial = wireData.getWireMaterial();
                        if (wireMaterial != null) {
                            wireMaterialDiameterModel.material = wireMaterial;
                        }
                        mInLineList.add(wireMaterialDiameterModel);

                    }
                }
                List<DeployControlSettingData.wireData> output = deployControlSettingData.getOutput();
                if (output != null && output.size() > 0) {
                    for (DeployControlSettingData.wireData wireData : output) {
                        WireMaterialDiameterModel wireMaterialDiameterModel = new WireMaterialDiameterModel();
                        Integer count = wireData.getCount();
                        if (count != null) {
                            wireMaterialDiameterModel.count = count;
                        }
                        Double wireDiameter = wireData.getWireDiameter();
                        if (wireDiameter != null) {
                            wireMaterialDiameterModel.diameter = String.valueOf(wireDiameter);
                        }
                        Integer wireMaterial = wireData.getWireMaterial();
                        if (wireMaterial != null) {
                            wireMaterialDiameterModel.material = wireMaterial;
                        }
                        mOutLineList.add(wireMaterialDiameterModel);
                    }
                }
                Integer switchSpec = deployControlSettingData.getSwitchSpec();
                if (switchSpec != null) {
                    getView().setActualCurrentValue(switchSpec);
                }


                break;
            case DEPLOY_CONFIGURATION_SOURCE_TYPE_DEVICE_DETAIL:
                //设备详情 下行
                break;

        }
        //
        recommendedTransformerDialogUtils = new RecommendedTransformerDialogUtils(mActivity);
        recommendedTransformerDialogUtils.setOnRecommendedTransformerDialogUtilsListener(this);
        getView().updateInLineData(mInLineList);
        getView().updateOutLineData(mOutLineList);

        if (tempValue != 0) {
            getView().setInputRated(String.valueOf(tempValue));
        }

    }

    @Override
    public void onDestroy() {
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
        int inLineTotal = 0;
        int outLineTotal = 0;
        List<DeployControlSettingData.wireData> input = new ArrayList<>();
        List<DeployControlSettingData.wireData> output = new ArrayList<>();
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
        int ratedCurrent;

        try {
            ratedCurrent = (int) (Integer.parseInt(getView().getEtInputText()) * 1.25f);
            if (ratedCurrent < 1 || ratedCurrent > 560) {
                getView().toastShort(String.format(Locale.ROOT, "%s%s", mActivity.getString(R.string.rated_current_colon), "1-560"));
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort(String.format(Locale.ROOT, "%s%s", mActivity.getString(R.string.rated_current_colon), "1-560"));
            return;
        }


        int temp = Math.min(ratedCurrent, inLineTotal);
        int actualRatedCurrent = Math.min(temp, outLineTotal);
        //
        if (actualRatedCurrent > 0 && actualRatedCurrent <= 120) {
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
        } else if (actualRatedCurrent <= 200) {
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
        } else if (actualRatedCurrent <= 400) {
            //400/40mA
            RecommendedTransformerValueModel recommendedTransformerValueModel3 = new RecommendedTransformerValueModel();
            recommendedTransformerValueModel3.value = 400;
            recommendedTransformerValueModel3.isRecommend = true;
            deployControlSettingData.setRecommTrans(400);
            deployControlSettingData.getTransformerValueList().add(recommendedTransformerValueModel3);
        } else {
            getView().toastShort(mActivity.getString(R.string.not_matched_current_transformer));
            return;
        }
        deployControlSettingData.setSwitchSpec(actualRatedCurrent);
        deployControlSettingData.setInputValue(ratedCurrent);
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
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_DEPLOY_INIT_CONFIG_CODE;
        eventData.data = deployControlSettingData;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }
}
