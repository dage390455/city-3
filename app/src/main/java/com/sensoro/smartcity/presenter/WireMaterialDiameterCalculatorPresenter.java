package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IWireMaterialDiameterCalculatorView;
import com.sensoro.smartcity.model.MaterialValueModel;
import com.sensoro.smartcity.model.WireMaterialDiameterModel;

import java.util.ArrayList;
import java.util.List;

public class WireMaterialDiameterCalculatorPresenter extends BasePresenter<IWireMaterialDiameterCalculatorView> {
    private Activity mActivity;
    private ArrayList<WireMaterialDiameterModel> mInLineList;
    private ArrayList<WireMaterialDiameterModel> mOutLineList;
    private ArrayList<String> pickerStrings;
    private boolean mIsInlineClick;
    private int mClickPosition;
    private boolean mIsAction;


    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        mInLineList = new ArrayList<>(5);
        mOutLineList = new ArrayList<>(5);
        pickerStrings = new ArrayList<>();
        initPickerData();
        getView().updateInLineData(mInLineList);
        getView().updateOutLineData(mOutLineList);
    }

    @Override
    public void onDestroy() {
        if (mInLineList != null) {
            mInLineList.clear();
        }
        if (mOutLineList != null) {
            mOutLineList.clear();
        }
    }

    private void initPickerData() {
        pickerStrings.addAll(Constants.materialValueMap.keySet());
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
        mClickPosition = position - 1;
        mIsAction = isAction;
        if (isAction) {
            getView().setPickerViewSelectOptions(0, 6, 0);
        } else {
            WireMaterialDiameterModel model = mOutLineList.get(mClickPosition);
            model.isSelected = true;
            getView().setPickerViewSelectOptions(model.material - 1, pickerStrings.indexOf(String.valueOf(model.diameter)), model.count - 1);
        }
        getView().updateOutLineData(mOutLineList);
        getView().setPickerTitle(mActivity.getString(R.string.out_line));
        getView().showPickerView();
        getView().setResultVisible(false);
    }

    public void doInLineItemClick(int position, boolean isAction) {
        mIsInlineClick = true;
        mClickPosition = position - 1;
        mIsAction = isAction;
        if (isAction) {
            getView().setPickerViewSelectOptions(0, 6, 0);
        } else {
            WireMaterialDiameterModel model = mInLineList.get(mClickPosition);
            model.isSelected = true;
            getView().setPickerViewSelectOptions(model.material, pickerStrings.indexOf(String.valueOf(model.diameter)), model.count - 1);
        }
        getView().updateInLineData(mInLineList);
        getView().setPickerTitle(mActivity.getString(R.string.in_line));
        getView().showPickerView();
        getView().setResultVisible(false);
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
        checkRecommendTransformer();
        getView().dismissPickerView();
    }

    public void doDeleteGroup() {
        if (!mIsAction) {
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
        checkRecommendTransformer();
        getView().dismissPickerView();
    }

    public void checkRecommendTransformer() {
        getView().setRecommendBtnStatus(!TextUtils.isEmpty(getView().getEtInputText()) && mInLineList.size() > 0 && mOutLineList.size() > 0);

    }

    public void doRecommendTransformer() {
        int inLineTotal = 0;
        int outLineTotal = 0;
        for (WireMaterialDiameterModel model : mInLineList) {
            MaterialValueModel materialValueModel = Constants.materialValueMap.get(model.diameter);
            inLineTotal += model.material == 1 ? materialValueModel.alValue : materialValueModel.cuValue * model.count;
        }

        for (WireMaterialDiameterModel model : mOutLineList) {
            MaterialValueModel materialValueModel = Constants.materialValueMap.get(model.diameter);
            outLineTotal += model.material == 1 ? materialValueModel.alValue : materialValueModel.cuValue * model.count;
        }
        int ratedCurrent = -1;

        try {
            ratedCurrent = Integer.parseInt(getView().getEtInputText());
        } catch (Exception e) {
            e.printStackTrace();
            getView().toastShort(mActivity.getString(R.string.enter_the_correct_number_format));
            return;
        }

        if (ratedCurrent == -1) {
            getView().toastShort(mActivity.getString(R.string.please_input_air_rated_current));
            return;
        }

        int temp = Math.min(ratedCurrent, inLineTotal);
        int actualRatedCurrent = Math.min(temp, outLineTotal);

        if (actualRatedCurrent > 0 && actualRatedCurrent <= 120) {
            //120A/40mA
            getView().setRatedCurrentTransformer(String.format("%s%s", mActivity.getString(R.string.current_transformer), "120A/40mA"));
        } else if (actualRatedCurrent <= 200) {
            //200A/40mA
            getView().setRatedCurrentTransformer(String.format("%s%s", mActivity.getString(R.string.current_transformer), "200A/40mA"));
        } else if (actualRatedCurrent <= 400) {
            //400/40mA
            getView().setRatedCurrentTransformer(String.format("%s%s", mActivity.getString(R.string.current_transformer), "400/40mA"));
        } else {
            getView().toastShort(mActivity.getString(R.string.not_matched_current_transformer));
            return;
        }

        if (actualRatedCurrent <= 120) {
            //l45k
            getView().setLeakageCurrentTransformer(String.format("%s%s", mActivity.getString(R.string.leakage_current_transformer), "L45K"));
        } else {
            //l80k
            getView().setLeakageCurrentTransformer(String.format("%s%s", mActivity.getString(R.string.leakage_current_transformer), "L80K"));
        }
        getView().setResultVisible(true);

        getView().setAirRatedCurrentValue(ratedCurrent);
        getView().setInLineTotalCurrentValue(inLineTotal);
        getView().setOutLineTotalCurrentValue(outLineTotal);
        getView().setActualCurrentValue(actualRatedCurrent);


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
            }
        }


    }
}
