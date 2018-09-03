package com.sensoro.smartcity.factory;

import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.model.MenuPageInfo;
import com.sensoro.smartcity.server.bean.GrantsInfo;

import java.util.ArrayList;
import java.util.List;

public class MenuPageFactory {
    //    R.mipmap.ic_menu_index, R.mipmap.ic_menu_alarm, R.mipmap.ic_menu_switch,
//    R.mipmap.ic_menu_location, R.mipmap.ic_menu_location
    //主页
    private static final MenuPageInfo indexPage = new MenuPageInfo(R.string.menu_page_index, R.mipmap.ic_menu_index,
            MenuPageInfo.MENU_PAGE_INDEX);
    //预警记录
    private static final MenuPageInfo alarmPage = new MenuPageInfo(R.string.menu_page_alarm, R.mipmap.ic_menu_alarm,
            MenuPageInfo.MENU_PAGE_ALARM);
    //商户切换
    private static final MenuPageInfo merchantPage = new MenuPageInfo(R.string.menu_page_merchant, R.mipmap
            .ic_menu_switch,
            MenuPageInfo.MENU_PAGE_MERCHANT);
    //点位部署
    private static final MenuPageInfo pointPage = new MenuPageInfo(R.string.menu_page_point, R.mipmap.ic_menu_location,
            MenuPageInfo.MENU_PAGE_POINT);
    //基站部署
    private static final MenuPageInfo stationPage = new MenuPageInfo(R.string.menu_page_station, R.mipmap
            .ic_menu_location,
            MenuPageInfo.MENU_PAGE_STATION);
    //合同管理
    private static final MenuPageInfo contractPage = new MenuPageInfo(R.string.menu_page_contract, R.mipmap
            .ic_menu_contract, MenuPageInfo.MENU_PAGE_CONTRACT);
    //扫码登录
    private static final MenuPageInfo scanLoginPage = new MenuPageInfo(R.string.menu_page_scan_login, R.mipmap
            .ic_menu_scan_login, MenuPageInfo.MENU_PAGE_SCAN_LOGIN);

    public static List<MenuPageInfo> createMenuPageList(EventLoginData eventLoginData) {
//        hasContract = false;
//        boolean hasScanLogin = true;
        ArrayList<MenuPageInfo> pageInfos = new ArrayList<>();
        //超级账户
        if (eventLoginData.isSupperAccount) {
            pageInfos.add(merchantPage);
            return pageInfos;
        } else {
            //商户账号
            if ("business".equalsIgnoreCase(eventLoginData.roles)) {
                if (eventLoginData.hasStation) {
                    if (eventLoginData.hasContract) {
                        if (eventLoginData.hasScanLogin) {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(stationPage);
                            pageInfos.add(scanLoginPage);
                            pageInfos.add(contractPage);
                        } else {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(stationPage);
                            pageInfos.add(contractPage);
                        }

                    } else {
                        if (eventLoginData.hasScanLogin) {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(stationPage);
                            pageInfos.add(scanLoginPage);
                        } else {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(stationPage);
                        }

                    }

                    return pageInfos;
                } else {
                    if (eventLoginData.hasContract) {
                        if (eventLoginData.hasScanLogin) {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(scanLoginPage);
                            pageInfos.add(contractPage);
                        } else {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(contractPage);
                        }

                    } else {
                        if (eventLoginData.hasScanLogin) {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(scanLoginPage);
                        } else {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(pointPage);
                        }

                    }

                    return pageInfos;
                }
            } else {
                //管理员账号
                if (eventLoginData.hasStation) {
                    if (eventLoginData.hasContract) {
                        if (eventLoginData.hasScanLogin) {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(merchantPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(stationPage);
                            pageInfos.add(scanLoginPage);
                            pageInfos.add(contractPage);
                        } else {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(merchantPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(stationPage);
                            pageInfos.add(contractPage);
                        }

                    } else {
                        if (eventLoginData.hasScanLogin) {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(merchantPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(stationPage);
                            pageInfos.add(scanLoginPage);
                        } else {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(merchantPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(stationPage);
                        }

                    }

                    return pageInfos;
                } else {
                    if (eventLoginData.hasContract) {
                        if (eventLoginData.hasScanLogin) {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(merchantPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(scanLoginPage);
                            pageInfos.add(contractPage);
                        } else {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(merchantPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(contractPage);
                        }

                    } else {
                        if (eventLoginData.hasScanLogin) {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(merchantPage);
                            pageInfos.add(pointPage);
                            pageInfos.add(scanLoginPage);
                        } else {
                            pageInfos.add(indexPage);
                            pageInfos.add(alarmPage);
                            pageInfos.add(merchantPage);
                            pageInfos.add(pointPage);
                        }

                    }

                    return pageInfos;
                }
            }
        }
    }

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

}
