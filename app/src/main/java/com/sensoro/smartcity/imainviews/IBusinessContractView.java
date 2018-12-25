package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;

import java.util.ArrayList;

public interface IBusinessContractView extends IToast,IActivityIntent,IProgressDialog {
    void updateContractTemplateAdapterInfo(ArrayList<ContractsTemplateInfo> data);

    void setBusinessMerchantName(String enterpriseName);

    void setOwnerName(String customerName);

    void setRegisterAddress(String customerAddress);

    void setSocialCreatedId(String enterpriseCardId);
}
