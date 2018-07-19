package com.sensoro.smartcity.factory;

import android.text.TextUtils;

import com.sensoro.smartcity.R;
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
            .ic_menu_location, MenuPageInfo.MENU_PAGE_CONTRACT);

    public static List<MenuPageInfo> createMenuPageList(boolean isSuper, String roles, boolean hasStation) {
        ArrayList<MenuPageInfo> pageInfos = new ArrayList<>();
        //超级账户
        if (isSuper) {
            pageInfos.add(merchantPage);
            return pageInfos;
        } else {
            //商户账号
            if (roles.equalsIgnoreCase("business")) {
                if (hasStation) {
                    pageInfos.add(indexPage);
                    pageInfos.add(alarmPage);
                    pageInfos.add(pointPage);
                    pageInfos.add(stationPage);
                    return pageInfos;
                } else {
                    pageInfos.add(indexPage);
                    pageInfos.add(alarmPage);
                    pageInfos.add(pointPage);
                    return pageInfos;
                }
            } else {
                //管理员账号
                if (hasStation) {
                    pageInfos.add(indexPage);
                    pageInfos.add(alarmPage);
                    pageInfos.add(merchantPage);
                    pageInfos.add(pointPage);
                    pageInfos.add(stationPage);
                    return pageInfos;
                } else {
                    pageInfos.add(indexPage);
                    pageInfos.add(alarmPage);
                    pageInfos.add(merchantPage);
                    pageInfos.add(pointPage);
                    return pageInfos;
                }
            }
        }
    }

    public static boolean getHasStationDeploy(GrantsInfo grants) {
        if (grants != null) {
            List<String> station = grants.getStation();
            for (String str : station) {
                if (str.equals("deploy")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean getIsSupperAccount(String isSupperAccountStr) {
        return !TextUtils.isEmpty(isSupperAccountStr) && "true".equalsIgnoreCase(isSupperAccountStr);
    }
}
