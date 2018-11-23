package com.sensoro.smartcity.imainviews;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

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
