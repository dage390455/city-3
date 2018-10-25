package com.sensoro.smartcity.factory;

import android.text.TextUtils;

import com.sensoro.smartcity.server.bean.GrantsInfo;

import java.util.List;

public class MenuPageFactory {
    /**
     * 判断基站权限
     *
     * @param grants
     * @return
     */
    public static boolean getHasStationDeploy(GrantsInfo grants) {
        if (grants != null) {
            List<String> station = grants.getStation();
            if (station != null) {
                return station.contains("deploy");
            }

        }
        return false;
    }

    /**
     * 判断是否超级账户
     *
     * @param isSupperAccountStr
     * @return
     */
    public static boolean getIsSupperAccount(String isSupperAccountStr) {
        return !TextUtils.isEmpty(isSupperAccountStr) && "true".equalsIgnoreCase(isSupperAccountStr);
    }

    /**
     * 判断合同权限
     *
     * @param grants
     * @return
     */
    public static boolean getHasContract(GrantsInfo grants) {
        if (grants != null) {
            List<String> contract = grants.getContract();
            if (contract != null) {
                return contract.contains("list") || contract.contains("create");
            }
        }
        return false;
    }

    /**
     * 判断扫码登录权限
     *
     * @param grants
     * @return
     */
    public static boolean getHasScanLogin(GrantsInfo grants) {
        if (grants != null) {
            List<String> tv = grants.getTv();
            if (tv != null) {
                return tv.contains("qrcode");
            }
        }
        return false;
    }

    /**
     * 判断是否有子账户权限
     *
     * @param roles
     * @param isSupperAccountStr
     * @return
     */
    public static boolean getHasSubMerchant(String roles, String isSupperAccountStr) {
        return !TextUtils.isEmpty(isSupperAccountStr) && "true".equalsIgnoreCase(isSupperAccountStr) || !"business".equalsIgnoreCase(roles);
    }

    /**
     * 判断巡检权限
     *
     * @param grants
     * @return
     */
    public static boolean getHasInspection(GrantsInfo grants) {
        if (grants != null) {
            List<String> inspectTask = grants.getInspectTask();
            if (inspectTask != null) {
                return inspectTask.contains("modifyStatus");
            }
        }
        return false;
    }

    /**
     * 判断是否有预警权限
     *
     * @param grants
     * @return
     */
    public static boolean getHasAlarmInfo(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsAlarm = grants.getAlarm();
            if (grantsAlarm != null) {
                return grantsAlarm.contains("list");
            }
        }
        return false;
    }

    public static boolean getHasDeviceBriefList(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsDevice = grants.getDevice();
            if (grantsDevice != null) {
                return grantsDevice.contains("briefList") && grantsDevice.contains("statusStatistics");
            }
        }
        return false;
    }

    /**
     * 信号测试权限
     *
     * @param grants
     * @return
     */
    public static boolean getHasSignalCheck(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsDevice = grants.getDevice();
            if (grantsDevice != null) {
                return grantsDevice.contains("signalCheck");
            }
        }
        return false;
    }

    /**
     * 信号配置权限
     *
     * @param grants
     * @return
     */
    public static boolean getHasSignalConfig(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsDevice = grants.getDevice();
            if (grantsDevice != null) {
                return grantsDevice.contains("signalConfig");
            }
        }
        return false;
    }

}
