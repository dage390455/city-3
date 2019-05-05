package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;

import java.util.ArrayList;

public interface IBusinessContractView extends IToast,IActivityIntent,IProgressDialog {
    void updateContractTemplateAdapterInfo(ArrayList<ContractsTemplateInfo> data);

    void setBusinessMerchantName(String enterpriseName);

    void setOwnerName(String customerName);

    void setRegisterAddress(String customerAddress);

    void setSocialCreatedId(String enterpriseCardId);

    void setEnterpriseName(String customerEnterpriseName);

    void setContactNumber(String customerPhone);

    void setSiteNature(String placeType);

    ArrayList<ContractsTemplateInfo> getContractTemplateList();

    void setServeAge(String serverAge);

    void setFirstAge(String firstAge);

    void setPeriodAge(String periodAge);

    void setTvSubmitText(String text);

    void showSaveSuccessToast();

    void cancelSuccessToast();

}
