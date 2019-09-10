package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.ContractsTemplateInfo;

import java.util.ArrayList;

public interface IContractServiceActivityView extends IActivityIntent,IProgressDialog,IToast {
    void showContentText(int originType,int type, String line1, String phone, String line2, String line3, String line4, String line5, String
            line6, String place, int service_life, int service_life_first, int service_life_period);

    void updateContractTemplateAdapterInfo(ArrayList<ContractsTemplateInfo> data);

    void showContentText(int serviceType, String line1, String phone, String line2, String line3, String line4, String line5, String line6, String place);

    void setBtnNextText(String content);

    String getPhoneNumber(int createType);

}
