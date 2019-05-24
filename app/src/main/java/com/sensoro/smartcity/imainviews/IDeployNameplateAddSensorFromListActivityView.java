package com.sensoro.smartcity.imainviews;

import android.graphics.drawable.Drawable;

import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.smartcity.model.AddSensorFromListModel;

import java.util.ArrayList;

public interface IDeployNameplateAddSensorFromListActivityView extends IToast, IProgressDialog {
    void updateData(ArrayList<AddSensorFromListModel> mList);

    void onPullRefreshComplete();

    void setCheckedDrawable(Drawable drawable);

    void notifyDataAll();

    void setSelectSize(String size);

    void setAddStatus(boolean canAdd);
}
