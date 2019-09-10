package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.InspectionTaskInstructionModel;

import java.util.List;

public interface IInspectionInstructionActivityView extends IToast,IProgressDialog,IActivityIntent{
    void updateRcContentData(List<InspectionTaskInstructionModel.DataBean> data);

    void updateRcTag(List<String> deviceTypes);
}
