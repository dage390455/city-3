package com.sensoro.smartcity.constant;

import com.sensoro.smartcity.R;

/**
 * Created by sensoro on 17/7/27.
 */

public interface Constants {
    String SOCKET_EVENT_DEVICE_INFO = "city-device-update-series";
    String PREFERENCE_SCOPE = "alpha_tool_scope";
    String PREFERENCE_KEY_URL = "url";
    String PREFERENCE_DEVICE_HISTORY = "city_device_history";
    String PREFERENCE_ALARM_HISTORY = "city_alarm_history";
    String PREFERENCE_ALARM_SEARCH_HISTORY = "preference_alarm_search_history";
    String PREFERENCE_MERCHANT_HISTORY = "city_merchant_history";
    String PREFERENCE_DEPLOY_NAME_HISTORY = "city_deploy_name_history";
    String PREFERENCE_DEPLOY_TAG_HISTORY = "city_deploy_tag_history";
    String PREFERENCE_DEPLOY_CONTACT_HISTORY = "city_deploy_contact_history";
    String PREFERENCE_DEPLOY_CONTENT_HISTORY = "city_deploy_content_history";
    String PREFERENCE_KEY_DEVICE = "key_search_history_keyword";
    String PREFERENCE_KEY_DEPLOY_NAME = "preference_key_deploy_name";
    String PREFERENCE_KEY_DEPLOY_PHONE = "preference_key_deploy_phone";
    //
    String PREFERENCE_KEY_DEVICE_NAME = "preference_key_device_name";
    String PREFERENCE_KEY_DEVICE_NUM = "preference_key_device_num";
    String PREFERENCE_KEY_DEVICE_PHONE = "preference_key_device_phone";
    String INPUT = "INPUT";
//    int LEFT_MENU_ICON_UNSELECT[] = {R.mipmap.ic_menu_index, R.mipmap.ic_menu_alarm, R.mipmap.ic_menu_switch,
//            R.mipmap.ic_menu_location, R.mipmap.ic_menu_location};
//    int LEFT_MENU_ICON_UNSELECT_BUSSIES[] = {R.mipmap.ic_menu_index, R.mipmap.ic_menu_alarm,
//            R.mipmap.ic_menu_location, R.mipmap.ic_menu_location};
//    //
//    int LEFT_MENU_ICON_UNSELECT_NO_STATION[] = {R.mipmap.ic_menu_index, R.mipmap.ic_menu_alarm, R.mipmap.ic_menu_switch,
//            R.mipmap.ic_menu_location};
//    int LEFT_MENU_ICON_UNSELECT_BUSSIES_NO_STATION[] = {R.mipmap.ic_menu_index, R.mipmap.ic_menu_alarm,
//            R.mipmap.ic_menu_location};
//    //
//    int LEFT_MENU_ICON_UNSELECT_SUPPER = R.mipmap.ic_menu_switch;
    String[] DEVICE_STATUS_ARRAY = {"预警", "正常", "失联", "未激活"};
    String[] STATION_STATUS_ARRAY = {"未激活", "正常", "报警", "紧急报警", "超时未上报", "离线"};
    String[] INDEX_STATUS_ARRAY = {"全部状态", "预警", "正常", "失联", "未激活"};
    int[] INDEX_STATUS_VALUES = {0, 1, 2, 3};
    String[] INDEX_TYPE_ARRAY = {
            "全部类型",
            "紧急呼叫", "追踪器", "甲烷",
            "一氧化碳", "二氧化碳", "倾角",
            "井位", "水位检测", "地磁", "跑冒滴漏",
            "火焰", "光线", "液化石油气", "二氧化氮",
            "PM2.5/10", "烟感", "温湿度",
            "消防液压", "温度贴片"
    };
    String INDEX_TYPE_VALUES[] = {
            "all",
            "alarm", "altitude,latitude,longitude", "ch4",
            "co", "co2", "collision,pitch,roll",
            "cover,level", "distance", "magnetic", "drop",
            "flame", "light", "lpg", "no2",
            "pm10,pm2_5", "smoke", "humidity,temperature",
            "waterPressure", "humidity,temp1,temperature"

    };
    String SENSOR_MENU_ARRAY[] = {
            "all",
            "alarm", "latitude|longitude|altitude", "ch4",
            "co", "co2", "collision|pitch|roll",
            "cover|level", "distance", "magnetic", "drop",
            "flame", "light", "lpg", "no2",
            "pm10|pm2_5", "smoke", "humidity|temperature",
            "waterPressure", "temp1"

    };
    Integer[] TYPE_MENU_RESOURCE = {
            R.mipmap.ic_sensor_call, R.mipmap.ic_sensor_tracker, R.mipmap.ic_sensor_ch4,
            R.mipmap.ic_sensor_co, R.mipmap.ic_sensor_co2, R.mipmap.ic_sensor_angle,
            R.mipmap.ic_sensor_cover, R.mipmap.ic_sensor_level, R.mipmap.ic_sensor_magnetic,
            R.mipmap.ic_sensor_drop, R.mipmap.ic_sensor_flame, R.mipmap.ic_sensor_light,
            R.mipmap.ic_sensor_lpg, R.mipmap.ic_sensor_no2, R.mipmap.ic_sensor_pm,
            R.mipmap.ic_sensor_smoke, R.mipmap.ic_sensor_temp_humi, R.mipmap.ic_sensor_water_pressure, R.mipmap
            .ic_sensor_temp_humi

    };
    String[] ALARM_TAG_ARRAY = {"一氧化碳", "二氧化碳", "甲烷", "液化石油气", "二氧化氮", "跑冒滴漏", "灯井监控",
            "PM2.5/10", "烟雾", "温湿度", "火焰", "倾角", "水压",
            "门锁检测", "追踪器", "水位检测", "光线"
    };
    String[] ALARM_TAG_EN_ARRAY = {"co", "co2", "ch4", "lpg", "no2", "drop", "cover,level",
            "pm2_5,pm10", "smoke", "temperature,humidity", "flame", "collision|pitch|roll", "waterPressure",
            "magnetic", "tracker", "distance", "light"
    };
    String[] WEEK_TITLE_ARRAY = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    String EXTRA_USER_ID = "extra_user_id";
    String EXTRA_USER_NAME = "extra_user_name";
    String EXTRA_USER_ROLES = "extra_user_roles";
    String EXTRA_PHONE = "extra_phone";
    String EXTRA_CHARACTER = "extra_character";
    String EXTRA_PHONE_ID = "extra_phone_id";
    String EXTRA_IS_SPECIFIC = "extra_is_specific";
    String EXTRA_GRANTS_INFO = "extra_grants_info";
    String EXTRA_DEVICE_INFO = "extra_device_info";
    String EXTRA_IS_STATION_DEPLOY = "extra_is_station_deploy";
    String EXTRA_SENSOR_SN = "extra_sn";
    String EXTRA_SENSOR_NAME = "extra_name";
    String EXTRA_SENSOR_TYPES = "extra_types";
    String EXTRA_SENSOR_TYPE = "extra_type";
    String EXTRA_SENSOR_RESULT = "extra_result";
    String EXTRA_SENSOR_RESULT_ERROR = "extra_sensor_result_error";
    String EXTRA_SENSOR_SN_RESULT = "extra_sensor_sn_result";
    String EXTRA_SENSOR_LON = "extra_lon";
    String EXTRA_SENSOR_LAN = "extra_lan";
    String EXTRA_SENSOR_STATUS = "extra_status";
    String EXTRA_SENSOR_INFO = "extra_sensor_info";
    String EXTRA_MERCHANT_INFO = "extra_merchant_info";
    String EXTRA_SENSOR_TIME = "extra_sensor_time";
    String EXTRA_SENSOR_LOCATION = "extra_sensor_location";
    String EXTRA_ALARM_INFO = "extra_alarm_info";
    String EXTRA_ALARM_IS_RE_CONFIRM = "extra_alarm_is_re_confirm";
    String EXTRA_ALARM_SEARCH_INDEX = "extra_alarm_search_index";
    String EXTRA_ALARM_SEARCH_TEXT = "extra_alarm_search_text";
    String EXTRA_FRAGMENT_INDEX = "extra_fragment_index";
    String EXTRA_ALARM_START_DATE = "extra_alarm_start_date";
    String EXTRA_ALARM_END_DATE = "extra_alarm_end_date";
    String EXTRA_ACTIVITY_CANCEL = "extra_activity_cancel";
    String EXTRA_SETTING_NAME_ADDRESS = "extra_setting_name_address";
    String EXTRA_SETTING_CONTACT = "extra_setting_contact";
    String EXTRA_SETTING_CONTENT = "extra_setting_content";
    String EXTRA_SETTING_INDEX = "extra_setting_index";
    String EXTRA_SETTING_TAG_LIST = "extra_tag_list";
    String EXTRA_CONTAINS_DATA = "extra_contains_data";
    String PREFERENCE_LOGIN = "preference_login";
    String PREFERENCE_KEY_NAME = "preference_key_name";
    String PREFERENCE_KEY_PASSWORD = "preference_key_password";

    String PREFERENCE_KEY_START_TIME = "preference_key_start_time";
    String PREFERENCE_KEY_END_TIME = "preference_key_end_time";
    int RESULT_CODE_MAP = 100;
    int RESULT_CODE_SEARCH_DEVICE = 101;
    int RESULT_CODE_SEARCH_ALARM = 102;
    int RESULT_CODE_CHANGE_MERCHANT = 111;
    int RESULT_CODE_SEARCH_MERCHANT = 113;
    int RESULT_CODE_DEPLOY = 103;
    int RESULT_CODE_ALARM = 104;
    int RESULT_CODE_CALENDAR = 105;
    int RESULT_CODE_ORIGIN = 106;
    int RESULT_CODE_SETTING_NAME_ADDRESS = 107;
    int RESULT_CODE_SETTING_TAG = 108;
    int RESULT_CODE_ALARM_DETAIL = 109;
    int RESULT_CODE_SETTING_CONTACT = 110;
    int REQUEST_CODE_SEARCH_DEVICE = 1;
    int REQUEST_CODE_SEARCH_ALARM = 2;
    int REQUEST_CODE_SEARCH_MERCHANT = 2;
    int REQUEST_CODE_POINT_DEPLOY = 3;
    int REQUEST_CODE_STATION_DEPLOY = 0x13;
    int REQUEST_CODE_ALARM = 4;
    int REQUEST_CODE_CALENDAR = 5;
    int REQUEST_SETTING_NAME_ADDRESS = 8;
    int REQUEST_SETTING_TAG = 9;
    int REQUEST_SETTING_CONTACT = 10;
    int SENSOR_STATUS_ALARM = 0;
    int SENSOR_STATUS_NORMAL = 1;
    int SENSOR_STATUS_LOST = 2;
    int SENSOR_STATUS_INACTIVE = 3;
    int DISPLAY_STATUS_CONFIRM = 0;
    int DISPLAY_STATUS_ALARM = 1;
    int DISPLAY_STATUS_MISDESCRIPTION = 2;
    int DISPLAY_STATUS_TEST = 3;
    int DISPLAY_STATUS_RISKS = 4;
    int DIRECTION_DOWN = 0;
    int DIRECTION_UP = 1;
    int TYPE_LIST = 0;
    int TYPE_GRID = 1;
    int SETTING_NAME_ADDRESS = 0;
    int SETTING_TAG = 1;
    int SETTING_CONTACT = 2;
    int SETTING_CONTENT = 3;
    String ENCODE = "HmacSHA512";
    String APP_ID = "wxa65d8bad62a982e1";
    int TYPE_DEVICE_NAME = 0x10;
    int TYPE_DEVICE_SN = 0x11;
    int TYPE_DEVICE_PHONE_NUM = 0x12;

    String EXTRA_SEARCH_CONTENT = "extra_search_content";
}
