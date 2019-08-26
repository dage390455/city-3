package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.ContractsTemplateInfo;

import java.util.List;

public interface IContractInfoActivityView extends IActivityIntent, IProgressDialog, IToast {
    void showContentText(int type, String line1, String phone, String line2, String line3, String line4, String
            line5, String line6, String place, String serviceAge, String serviceAgeFirst, String serviceAgePeriod);

    void updateContractTemplateAdapterInfo(List<ContractsTemplateInfo> data);

    void setSignTime(String time);

    void setConfirmText(String text);

    void updateFirmOrPersonal(int contract_type);

    void setConfirmVisible(boolean isConfirmed);

    void setConfirmStatus(boolean confirmed);

    void setContractCreateTime(String createdAt);
}
