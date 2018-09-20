package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.ArrayList;

public interface IInspectionExceptionDetailActivityView extends IToast,IActivityIntent,IProgressDialog{
    void updateTagsData(ArrayList<String> list);

    void updateExceptionTagsData(ArrayList<String> list);
}
