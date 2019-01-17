package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;

import java.util.ArrayList;

public interface IPersonalContractView extends IToast,IProgressDialog,IActivityIntent {
    void updateContractTemplateAdapterInfo(ArrayList<ContractsTemplateInfo> data);

    void setOwnerName(String name);

    void setIdCardNumber(String idNumber);

    void setHomeAddress(String address);

    void setPartAName(String customerEnterpriseName);

    void setContactNumber(String contactNumber);

    void setSiteNature(String placeType);

    ArrayList<ContractsTemplateInfo> getContractTemplateList();

    void setServeAge(String serverAge);

    void setFirstAge(String firstAge);

    void setPeriodAge(String periodAge);

    void setTvSubmitText(String text);

    void showSaveSuccessToast();

    void cancelSuccessToast();

}
