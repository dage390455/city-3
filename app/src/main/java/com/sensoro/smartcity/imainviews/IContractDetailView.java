package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;

import java.util.List;

public interface IContractDetailView extends IToast,IActivityIntent,IProgressDialog {
    void setSignStatus(boolean isSigned);

    void setCustomerEnterpriseName(String customerEnterpriseName);

    void setCustomerName(String customerName);

    void setCustomerPhone(String customerPhone);

    void setCustomerAddress(String customerAddress);

    void setPlaceType(String placeType);

    void setCardIdOrEnterpriseId(String cardOrEnterpriseId);

    void setTipText(int contractType);

    void setContractCreateTime(String createdAt);

    void updateContractTemplateAdapterInfo(List<ContractsTemplateInfo> devices);

    void setServerAge(String serverAge);

    void setPeriodAge(String PeriodAge);

    void setFirstAge(String firstAge);

    void setContractTime(String time);

    void setContractNumber(String contractNumber);

    void setContractOrder(boolean isSuccess, String payTime);
}
