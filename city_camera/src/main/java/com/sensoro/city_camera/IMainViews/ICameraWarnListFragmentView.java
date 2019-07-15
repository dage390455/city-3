package com.sensoro.city_camera.IMainViews;

import com.sensoro.city_camera.model.FilterModel;
import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.iwidget.IToast;
import com.sensoro.common.server.security.bean.SecurityAlarmInfo;

import java.util.List;

/**
 * @author qinghao.wang
 */
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
     * 安防预警列表移动到顶部
     */
    void SmoothToTopList();

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

    /**
     * 刷新 抓拍时间 选择列表
     * @param captureTimeList
     */
    void updateFilterCaptureTimeList(List<FilterModel> captureTimeList);

    /**
     * 刷新 处理状态 选择列表
     * @param processStatusList
     */
    void updateFilterProcessStatusList(List<FilterModel> processStatusList);

    /**
     * 设置抓拍时间View
     * @param captureTimeModel
     */
    void setFilterCaptureTimeView(FilterModel captureTimeModel);

    /**
     * 设置处理状态View
     * @param processStatusModel
     */
    void setFilterProcessStatusView(FilterModel processStatusModel);

    /**
     * 确认预警
     * @param securityAlarmInfo
     */
    void showConfirmDialog(SecurityAlarmInfo securityAlarmInfo);

    void dismissInput();
}
