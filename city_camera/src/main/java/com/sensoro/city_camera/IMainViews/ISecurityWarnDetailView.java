package com.sensoro.city_camera.IMainViews;

import com.sensoro.common.iwidget.IActivityIntent;
import com.sensoro.common.iwidget.IProgressDialog;
import com.sensoro.common.server.security.bean.SecurityAlarmDetailInfo;
import com.sensoro.common.server.security.bean.SecurityAlarmEventInfo;

import java.util.List;

/**
 * @author bin.tian
 */
public interface ISecurityWarnDetailView extends IActivityIntent, IProgressDialog {
    /**
     * 显示安防预警确认弹窗
     * @param securityAlarmDetailInfo
     */
    void showConfirmDialog(SecurityAlarmDetailInfo securityAlarmDetailInfo);
    /**
     * 显示摄像头信息详情弹窗
     * @param securityAlarmDetailInfo
     */
    void showCameraDetailsDialog(SecurityAlarmDetailInfo securityAlarmDetailInfo);


    /**
     * 更新安防预警详情数据
     * @param securityAlarmDetailInfo
     */
    void updateSecurityWarnDetail(SecurityAlarmDetailInfo securityAlarmDetailInfo);

    /**
     * 更新预警修改数据
     * @param list
     */
    void updateSecurityWarnTimeLine(List<SecurityAlarmEventInfo> list);

    /**
     * 更新确认信息
     * @param securityAlarmDetailInfo
     */
    void updateSecurityConfirmResult(SecurityAlarmDetailInfo securityAlarmDetailInfo);
}
