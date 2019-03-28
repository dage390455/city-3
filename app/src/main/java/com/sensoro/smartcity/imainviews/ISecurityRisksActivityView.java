package com.sensoro.smartcity.imainviews;

import com.sensoro.smartcity.adapter.model.SecurityRisksAdapterModel;
import com.sensoro.smartcity.adapter.model.SecurityRisksTagModel;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IToast;

import java.util.ArrayList;
import java.util.List;

public interface ISecurityRisksActivityView extends IToast, IActivityIntent {
    void updateSecurityRisksContent(List<SecurityRisksAdapterModel> data);

    void setConstraintTagVisible(boolean isVisible);

    void updateSecurityRisksTag(ArrayList<SecurityRisksTagModel> locationTagList, boolean isLocation);

    void changLocationOrBehaviorColor(int position, boolean isLocation);

    void updateLocationTag(String tag, boolean check, int mAdapterPosition);

    void setTvName(String name);

    void showAddTagDialog(boolean mIsLocation);

    void dismissTagDialog();

    boolean getIsLocation();

}
