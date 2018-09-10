package com.sensoro.smartcity.imainviews;

import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;

import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;
import com.sensoro.smartcity.model.EventLoginData;

import java.util.List;

public interface IMainViewTest extends IProgressDialog, IToast, IActivityIntent {
    void setHpCurrentItem(int position);

    void setRbChecked(@IdRes int id);

    EventLoginData getLoginData();

    void updateMainPageAdapterData(List<Fragment> fragments);
}
