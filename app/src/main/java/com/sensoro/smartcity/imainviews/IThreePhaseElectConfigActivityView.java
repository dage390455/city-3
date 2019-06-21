package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.smartcity.model.WireMaterialDiameterModel;

import java.util.ArrayList;
import java.util.List;

public interface IThreePhaseElectConfigActivityView extends IToast , IActivityIntent {
    void updateInLineData(ArrayList<WireMaterialDiameterModel> mInLineList);

    void updateOutLineData(ArrayList<WireMaterialDiameterModel> mOutLineList);

    void updatePvCustomOptions(List<String> materials, List<String> pickerStrings, List<String> counts);

    void showPickerView();

    void setPickerViewSelectOptions(int material, int diameter, int count);

    void dismissPickerView();

    String getEtInputText();

    void setPickerTitle(String title);

    void setActualCurrentValue(String value);

}
