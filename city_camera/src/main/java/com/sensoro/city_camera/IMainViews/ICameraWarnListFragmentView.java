package com.sensoro.city_camera.IMainViews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.security.bean.SecurityAlarmInfo;

import java.util.List;

public interface ICameraWarnListFragmentView extends IToast, IActivityIntent, IProgressDialog {
    /**
     * 取消搜索数据
     */
    void cancelSearchData();

    /**
     * 更新安防预警列表
     * @param securityAlarmInfoList
     */
    void updateCameraWarnsListAdapter(List<SecurityAlarmInfo> securityAlarmInfoList);

    /**
     * 下拉刷新完成
     */
    void onPullRefreshComplete();

    /**
     * 没有数据
     */

    void onPullRefreshCompleteNoMoreData();


    /**
     * 设置搜索取消按钮 显示/隐藏
     * @param isVisible
     */
    void setSearchButtonTextCancelVisible(boolean isVisible);


    /**
     * 搜索取消按钮显示状态
     * @return
     */
    boolean getSearchTextCancelVisible();

    /**
     * 显示/隐藏 空数据view
     * @param isVisible
     */
    void setNoContentVisible(boolean isVisible);


    /**
     * 搜索内容清除 显示隐藏
     * @param isVisible
     */
    void setSearchClearImvVisible(boolean isVisible);

    /**
     * 更新历史搜索记录
     * @param data
     */
    void updateSearchHistoryList(List<String> data);

    /**
     * 现实/隐藏历史搜索记录
     * @param isVisible
     */
    void setSearchHistoryVisible(boolean isVisible);

    /**
     * 现实清除历史记录确认弹框
     */
    void showHistoryClearDialog();

}
