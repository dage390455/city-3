package com.sensoro.smartcity.imainviews;

import android.support.annotation.IdRes;

import com.sensoro.smartcity.adapter.MainFragmentPageAdapter;
import com.sensoro.smartcity.iwidget.IActivityIntent;
import com.sensoro.smartcity.iwidget.IProgressDialog;
import com.sensoro.smartcity.iwidget.IToast;

public interface IMainViewTest extends IProgressDialog,IToast,IActivityIntent {
    void setHpCurrentItem(int position);

    void setRbChecked(@IdRes int id);
}
