package com.sensoro.smartcity.imainviews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

import java.util.ArrayList;

public interface IFrequencyPointActivityView extends IToast, IProgressDialog, IActivityIntent {

    void updateData(ArrayList<String> arrayList);


}
