package com.sensoro.nameplate.IMainViews;

import android.graphics.drawable.Drawable;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.nameplate.model.AddSensorModel;

import java.util.ArrayList;
import java.util.List;

public interface IDeployNameplateAddSensorFromListActivityView extends IToast, IProgressDialog,
        IActivityIntent {
    void updateData(ArrayList<NamePlateInfo> mList);

    void onPullRefreshComplete();

    void setCheckedDrawable(Drawable drawable);

    void notifyDataAll();

    void setSelectSize(int size);

    void setAddStatus(boolean canAdd);

    void setSearchHistoryVisible(boolean isVisible);

    void setSearchButtonTextVisible(boolean isVisible);

    void UpdateSearchHistoryList(List<String> mSearchHistoryList);

    void showConfirmDialog();
}
