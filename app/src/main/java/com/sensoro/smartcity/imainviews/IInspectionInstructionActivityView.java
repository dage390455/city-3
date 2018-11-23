package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.server.bean.InspectionTaskInstructionModel;

import java.util.List;

public interface IInspectionInstructionActivityView extends IToast,IProgressDialog,IActivityIntent{
    void updateRcContentData(List<InspectionTaskInstructionModel.DataBean> data);

    void updateRcTag(List<String> deviceTypes);
}
