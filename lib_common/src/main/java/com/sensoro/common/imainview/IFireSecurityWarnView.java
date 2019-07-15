package com.sensoro.common.imainview;

import androidx.fragment.app.Fragment;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;

import java.util.List;

/**
 * @author : bin.tian
 * date   : 2019-06-21
 */
public interface IFireSecurityWarnView extends IProgressDialog, IToast, IActivityIntent {
    /**
     * 更新消防与安防报警页面
     * @param fragmentTitleList
     * @param fragments
     */
    void updateFireSecurityPageAdapterData(List<String> fragmentTitleList, List<Fragment> fragments);

    void setHasFireSecurityView(boolean visible);
}
