package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.ContractsTemplateInfo;

import java.util.List;

public interface IContractServiceActivityView extends IActivityIntent,IProgressDialog,IToast {
    void showContentText(int type, String line1, String line2, String line3, String line4, String line5, String
            line6, int place);

    void updateContractTemplateAdapterInfo(List<ContractsTemplateInfo> data);
}
