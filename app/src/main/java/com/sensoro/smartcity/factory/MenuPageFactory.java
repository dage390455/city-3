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
                for (String str : station) {
                    if (str.equals("deploy")) {
                        return true;
                    }
                }
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
                for (String str : contract) {
                    if ("list".equals(str) || "create".equals(str)) {
                        return true;
                    }
                }
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
                for (String str : tv) {
                    if ("qrcode".equals(str)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean getHasSubMerchant(String roles, String isSupperAccountStr) {
        return !TextUtils.isEmpty(isSupperAccountStr) && "true".equalsIgnoreCase(isSupperAccountStr) || !"business".equalsIgnoreCase(roles);
    }

}
