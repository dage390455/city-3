package com.sensoro.city_camera.IMainViews;

import com.sensoro.common.server.security.bean.SecurityAlarmDetailInfo;

/**
 * @author bin.tian
 */
public interface ISecurityWarnDetailView {
    /**
     * 显示安防预警确认弹窗
     * @param securityAlarmDetailInfo
     */
    void showConfirmDialog(SecurityAlarmDetailInfo securityAlarmDetailInfo);

    /**
     * 更新安防预警详情数据
     * @param securityAlarmDetailInfo
     */
    void updateSecurityWarnDetail(SecurityAlarmDetailInfo securityAlarmDetailInfo);
}
