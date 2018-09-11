package com.sensoro.smartcity.constant;

import com.sensoro.smartcity.R;

/**
 * Created by sensoro on 17/7/27.
 */

public interface Constants {
    //
    String SOCKET_EVENT_DEVICE_INFO = "city-device-update-series";
    String SOCKET_EVENT_DEVICE_ALARM_COUNT = "city.device.stat";
    String PREFERENCE_SCOPE = "alpha_tool_scope";
    String PREFERENCE_KEY_URL = "url";
    String PREFERENCE_DEVICE_HISTORY = "city_device_history";
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
//    int LEFT_MENU_ICON_UNSELECT_NO_STATION[] = {R.mipmap.ic_menu_index, R.mipmap.ic_menu_alarm, R.mipmap
// .ic_menu_switch,
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
            "井位", "水位检测", "地磁", "门锁检测", "跑冒滴漏",
            "火焰", "光线", "液化石油气", "二氧化氮",
            "PM2.5/10", "烟感", "温湿度",
            "消防液压", "温度贴片", "通断检测", "电表", "电气火灾", "红外线", "手动报警", "声光报警"
    };
    String[] SELECT_TYPE = {
            "全部类型",
            "甲烷", "一氧化碳", "二氧化碳",
            "二氧化氮", "PM2.5/10", "光线",
            "井位", "烟感", "温湿度", "倾角", "火焰",
            "水位监测", "跑冒滴漏", "液化石油气", "紧急呼叫",
            "追踪器", "消防液压", "地磁",
            "门锁监测", "温度贴片", "通断检测", "电表","电气火灾", "红外线", "手动报警", "声光报警"
    };

    Integer[] SELECT_TYPE_RESOURCE = {
            R.drawable.type_all,R.drawable.type_ch4,R.drawable.type_co,R.drawable.type_co2,
            R.drawable.type_no2,R.drawable.type_pm,R.drawable.type_light,R.drawable.type_well_position,
            R.drawable.type_smoke,R.drawable.type_tempature_humidity,R.drawable.type_inclination,
            R.drawable.type_flame,R.drawable.type_water_monitoring,R.drawable.type_leak,
            R.drawable.type_gas,R.drawable.type_emergency_call,R.drawable.type_tracking_device,
            R.drawable.type_fire_hydraulic,R.drawable.type_geomagnetic,R.drawable.type_lock_monitoring,
            R.drawable.type_tempature_humidity,R.drawable.type_on_off_monitoring,R.drawable.type_ammeter
            , R.mipmap.ic_sensor_electric_alarm, R.mipmap.ic_sensor_infrared,
            R.mipmap.ic_sensor_manual_alarm, R.mipmap.ic_sensor_sound_light_alarm
    };

    String SELECT_TYPE_VALUES[] = {
            "all",  "ch4","co", "co2","no2","pm10,pm2_5","light","cover,level","smoke",
            "humidity,temperature","collision,pitch,roll","flame","distance","drop","lpg",
            "alarm","altitude,latitude,longitude","waterPressure","magnetic","door",
            "humidity,temp1,temperature","connection","CURRENT_A,CURRENT_B,CURRENT_C,ID," +
            "TOTAL_POWER,VOLTAGE_A," +
            "VOLTAGE_B,VOLTAGE_C", "curr_val,elec_energy_val,leakage_val,power_val,temp_val,vol_val", "infrared",
            "manual_alarm", "sound_light_alarm"
    };
    String INDEX_TYPE_VALUES[] = {
            "all",
            "alarm", "altitude,latitude,longitude", "ch4",
            "co", "co2", "collision,pitch,roll",
            "cover,level", "distance", "magnetic", "door", "drop",
            "flame", "light", "lpg", "no2",
            "pm10,pm2_5", "smoke", "humidity,temperature",
            "waterPressure", "humidity,temp1,temperature", "connection", "CURRENT_A,CURRENT_B,CURRENT_C,ID," +
            "TOTAL_POWER,VOLTAGE_A," +
            "VOLTAGE_B,VOLTAGE_C", "curr_val,elec_energy_val,leakage_val,power_val,temp_val,vol_val", "infrared",
            "manual_alarm", "sound_light_alarm"

    };
    String SENSOR_MENU_ARRAY[] = {
            "all",
            "alarm", "latitude|longitude|altitude", "ch4",
            "co", "co2", "collision|pitch|roll",
            "cover|level", "distance", "magnetic", "door", "drop",
            "flame", "light", "lpg", "no2",
            "pm10|pm2_5", "smoke", "humidity|temperature",
            "waterPressure", "temp1", "connection",
            "CURRENT_A|CURRENT_B|CURRENT_C|ID|TOTAL_POWER|VOLTAGE_A|VOLTAGE_B|VOLTAGE_C",
            "curr_val|elec_energy_val|leakage_val|power_val|temp_val|vol_val", "infrared",
            "manual_alarm", "sound_light_alarm"
    };
    Integer[] TYPE_MENU_RESOURCE = {
            R.mipmap.ic_sensor_call, R.mipmap.ic_sensor_tracker, R.mipmap.ic_sensor_ch4,
            R.mipmap.ic_sensor_co, R.mipmap.ic_sensor_co2, R.mipmap.ic_sensor_angle,
            R.mipmap.ic_sensor_cover, R.mipmap.ic_sensor_level, R.mipmap.ic_sensor_magnetic, R.mipmap.ic_sensor_lock,
            R.mipmap.ic_sensor_drop, R.mipmap.ic_sensor_flame, R.mipmap.ic_sensor_light,
            R.mipmap.ic_sensor_lpg, R.mipmap.ic_sensor_no2, R.mipmap.ic_sensor_pm,
            R.mipmap.ic_sensor_smoke, R.mipmap.ic_sensor_temp_humi, R.mipmap.ic_sensor_water_pressure, R.mipmap
            .ic_sensor_temp_humi, R.mipmap.ic_sensor_connection, R.mipmap.ic_sensor_electric_meter, R.mipmap
            .ic_sensor_electric_alarm, R.mipmap
            .ic_sensor_infrared, R.mipmap.ic_sensor_manual_alarm, R.mipmap.ic_sensor_sound_light_alarm

    };
    String[] ALARM_TAG_ARRAY = {"一氧化碳", "二氧化碳", "甲烷", "液化石油气", "二氧化氮", "跑冒滴漏", "灯井监控",
            "PM2.5/10", "烟雾", "温湿度", "火焰", "倾角", "水压",
            "门锁检测", "追踪器", "水位检测", "光线"
    };
    String[] ALARM_TAG_EN_ARRAY = {"co", "co2", "ch4", "lpg", "no2", "drop", "cover,level",
            "pm2_5,pm10", "smoke", "temperature,humidity", "flame", "collision|pitch|roll", "waterPressure",
            "magnetic", "tracker", "distance", "light"
    };

    String[] confirmStatusArray = {"待确认", "真实火警", "误报", "测试/巡检", "安全隐患"};
    String[] confirmAlarmResultInfoArray = {"", "监测点或附近发生着火，需要立即进行扑救", "无任何火情和烟雾", "相关人员主动测试发出的预警",
            "未发生着火，但现场确实存在隐患"};
    String[] confirmAlarmTypeArray = {"其他", "用电异常", "生产作业", "吸烟", "室内生火", "烹饪", "燃气泄漏", "人为放火", "易燃物自燃"};
    //    private final String[] confirmAlarmPlaceArray = {"其他", "小区", "工厂", "居民作坊", "仓库", "商铺店面", "商场", "出租房",};
    String[] confirmAlarmPlaceArray = {"其他", "小区", "工厂", "居民作坊", "仓库", "商铺店面", "商场", "出租房"};
    String[] WEEK_TITLE_ARRAY = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    String EXTRA_USER_ID = "extra_user_id";
    //
    String EXTRA_SAVE_NAME = "extra_save_name";
    String EXTRA_SAVE_PWD = "extra_save_pwd";
    String EXTRA_USER_NAME = "extra_user_name";
    String EXTRA_USER_ROLES = "extra_user_roles";
    String EXTRA_PHONE = "extra_phone";
    String EXTRA_CHARACTER = "extra_character";
    String EXTRA_PHONE_ID = "extra_phone_id";
    String EXTRA_IS_SPECIFIC = "extra_is_specific";
    String EXTRA_GRANTS_HAS_STATION = "extra_grants_has_station";
    String EXTRA_GRANTS_HAS_CONTRACT = "extra_grants_has_contract";
    String EXTRA_GRANTS_HAS_SCAN_LOGIN = "extra_grants_has_scan_login";
    String EXTRA_DEVICE_INFO = "extra_device_info";
    String EXTRA_IS_STATION_DEPLOY = "extra_is_station_deploy";
    String EXTRA_SENSOR_SN = "extra_sn";
    String EXTRA_SENSOR_NAME = "extra_name";
    String EXTRA_SENSOR_TYPES = "extra_types";
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
    //
    String EXTRA_DEPLOY_PHOTO = "extra_deploy_photo";
    String EXTRA_DEPLOY_TO_PHOTO = "extra_deploy_to_photo";
    //
    String EXTRA_CONTRACT_TYPE = "extra_contract_type";
    //
    String EXTRA_CONTRACT_RESULT_TYPE = "extra_contract_result_type";
    //
    String PREFERENCE_LOGIN_ID = "preference_login_id";
    String PREFERENCE_LOGIN_NAME_PWD = "preference_login_name_pwd";
    String PREFERENCE_SPLASH_LOGIN_DATA = "preference_main_login";
    String PREFERENCE_KEY_NAME = "preference_key_name";
    String PREFERENCE_KEY_SESSION_ID = "preference_key_session_id";
    String PREFERENCE_KEY_PASSWORD = "preference_key_password";

    String PREFERENCE_KEY_START_TIME = "preference_key_start_time";
    String PREFERENCE_KEY_END_TIME = "preference_key_end_time";
    //
    int SENSOR_STATUS_ALARM = 0;
    int SENSOR_STATUS_NORMAL = 1;
    int SENSOR_STATUS_LOST = 2;
    int SENSOR_STATUS_INACTIVE = 3;
    int DISPLAY_STATUS_CONFIRM = 0;
    int DISPLAY_STATUS_ALARM = 1;
    int DISPLAY_STATUS_MIS_DESCRIPTION = 2;
    int DISPLAY_STATUS_TEST = 3;
    int DISPLAY_STATUS_RISKS = 4;
    int DIRECTION_DOWN = 0;
    int DIRECTION_UP = 1;
    int TYPE_LIST = 0;
    int TYPE_GRID = 1;
    //
    String ENCODE = "HmacSHA512";
    String APP_ID = "wxa65d8bad62a982e1";
    //
    int TYPE_DEVICE_NAME = 0x10;
    int TYPE_DEVICE_SN = 0x11;
    int TYPE_DEVICE_PHONE_NUM = 0x12;
    //
    String EXTRA_SEARCH_CONTENT = "extra_search_content";
    //
    //合同扫描相关id
    int REQUEST_CODE_LICENSE_SERVICE = 0x111;
    int REQUEST_CODE_PERSON_SERVICE = 0x112;
    int REQUEST_CODE_BUSINESS_LICENSE = 0x113;
    int REQUEST_CODE_CAMERA = 102;
    //上传图片相关id
    int IMAGE_ITEM_ADD = -1;
    int REQUEST_CODE_PREVIEW = 0x101;
    int REQUEST_CODE_SELECT = 0x100;
    int REQUEST_CODE_RECORD = 0x99;
    int RESULT_CODE_RECORD = 0x98;
    int REQUEST_CODE_PLAY_RECORD = 0x97;
    //
    int EVENT_DATA_FINISH_CODE = 0x13;
    int EVENT_DATA_SOCKET_DATA_INFO = 0x14;
    int EVENT_DATA_DEPLOY_RESULT_FINISH = 0x15;

    int EVENT_DATA_DEPLOY_SETTING_NAME_ADDRESS = 0x16;
    int EVENT_DATA_DEPLOY_SETTING_TAG = 0x17;
    int EVENT_DATA_DEPLOY_SETTING_CONTACT = 0x18;
    int EVENT_DATA_DEPLOY_SETTING_PHOTO = 0x19;
    //
    int EVENT_DATA_DEPLOY_RESULT_CONTINUE = 0x20;
    //
    int EVENT_DATA_SEARCH_MERCHANT = 0x21;
    //
    int EVENT_DATA_SELECT_CALENDAR = 0x22;
    //
    int EVENT_DATA_ALARM_DETAIL_RESULT = 0x23;

    int EVENT_DATA_SEARCH_ALARM_RESULT = 0x24;
    int EVENT_DATA_SOCKET_DATA_COUNT = 0x25;
    int EVENT_DATA_SESSION_ID_OVERTIME = 0x26;
    int EVENT_DATA_CANCEL_AUTH = 0x27;
    int EVENT_DATA_AUTH_SUC = 0x28;
}
