package com.sensoro.nameplate.IMainViews;

import android.graphics.drawable.Drawable;

import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.nameplate.model.AddSensorFromListModel;

import java.util.ArrayList;
import java.util.List;

public interface IDeployNameplateAddSensorFromListActivityView extends IToast, IProgressDialog {
    void updateData(ArrayList<AddSensorFromListModel> mList);

    void onPullRefreshComplete();

    void setCheckedDrawable(Drawable drawable);

    void notifyDataAll();

    void setSelectSize(String size);

    void setAddStatus(boolean canAdd);

    void setSearchHistoryVisible(boolean isVisible);

    void setSearchButtonTextVisible(boolean isVisible);

    void UpdateSearchHistoryList(List<String> mSearchHistoryList);
}
