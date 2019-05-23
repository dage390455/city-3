package com.sensoro.smartcity.imainviews;

import androidx.fragment.app.Fragment;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

public interface IMainView extends IProgressDialog, IToast, IActivityIntent {
    void setHpCurrentItem(int position);

    void updateMainPageAdapterData(List<Fragment> fragments);

    void setHasDeviceBriefControl(boolean hasDeviceBriefControl);

    void setHasAlarmInfoControl(boolean hasDeviceAlarmInfoControl);

    void setHasMalfunctionControl(boolean hasManagerControl);

    void setAlarmWarnCount(int count);

    void setBottomBarSelected(int i);

    boolean isHomeFragmentChecked();
}
