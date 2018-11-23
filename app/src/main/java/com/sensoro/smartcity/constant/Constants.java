package com.sensoro.smartcity.constant;

import com.sensoro.smartcity.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sensoro on 17/7/27.
 */

public interface Constants {
    //
    String SOCKET_EVENT_DEVICE_INFO = "city-device-update-series";
    String SOCKET_EVENT_DEVICE_ALARM_COUNT = "city.device.stat";
    String SOCKET_EVENT_DEVICE_ALARM_DISPLAY = "city.alarm.display";
    String SOCKET_EVENT_DEVICE_TASK_RESULT = "city.task.result";
    String PREFERENCE_SCOPE = "alpha_tool_scope";
    String PREFERENCE_KEY_URL = "url";
    String PREFERENCE_DEVICE_HISTORY = "city_device_history";
    String PREFERENCE_ALARM_SEARCH_HISTORY = "preference_alarm_search_history";
    String PREFERENCE_MERCHANT_HISTORY = "city_merchant_history";
    String PREFERENCE_DEPLOY_HISTORY = "preference_deploy_history";
    String PREFERENCE_DEPLOY_TAG_HISTORY = "city_deploy_tag_history";
    String PREFERENCE_DEPLOY_CONTACT_HISTORY = "city_deploy_contact_history";
    String PREFERENCE_DEPLOY_CONTENT_HISTORY = "city_deploy_content_history";
    String PREFERENCE_KEY_DEVICE = "key_search_history_keyword";
    //
    String PREFERENCE_KEY_DEPLOY_NAME = "preference_key_deploy_name";
    String PREFERENCE_KEY_DEPLOY_NAME_ADDRESS = "preference_key_deploy_name_address";
    String PREFERENCE_KEY_DEPLOY_TAG = "preference_key_deploy_tag";
    String PREFERENCE_KEY_LOCAL_DEVICES_MERGETYPES = "preference_key_local_devices_mergetypes";
    //
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
    int[] DEVICE_STATUS_ARRAY = {R.string.main_page_warm, R.string.normal, R.string.status_lost, R.string.status_inactive,R.string.status_malfunction};
    int[] STATION_STATUS_ARRAY = {R.string.status_inactive, R.string.normal, R.string.status_alarm_true, R.string.status_emergency_alarm, R.string.status_timeout_not_reported, R.string.status_offline,R.string.status_malfunction};
    String[] INDEX_STATUS_ARRAY = {"全部状态", "预警", "正常", "失联", "未激活"};
    int[] INDEX_STATUS_VALUES = {0, 1, 2, 3};
    //    String[] INDEX_TYPE_ARRAY = {
//            "全部类型",
//            "紧急呼叫", "追踪器", "甲烷",
//            "一氧化碳", "二氧化碳", "倾角",
//            "井位", "水位检测", "地磁", "门锁检测", "跑冒滴漏",
//            "火焰", "光线", "液化石油气", "二氧化氮",
//            "PM2.5/10", "烟感", "温湿度",
//            "消防液压", "温度贴片", "通断检测", "电表", "电气火灾", "红外线", "手动报警", "声光报警"
//    };
//    String[] SELECT_TYPE = {
//            "全部类型",
//            "甲烷", "一氧化碳", "二氧化碳",
//            "二氧化氮", "PM2.5/10", "光线",
//            "井位", "烟感", "温湿度", "倾角", "火焰",
//            "水位监测", "跑冒滴漏", "液化石油气", "紧急呼叫",
//            "追踪器", "消防液压", "地磁",
//            "门锁监测", "温度贴片", "通断检测", "电表", "电气火灾", "红外线", "手动报警", "声光报警"
//    };

//    Integer[] SELECT_TYPE_RESOURCE = {
//            R.drawable.type_all, R.drawable.type_ch4, R.drawable.type_co, R.drawable.type_co2,
//            R.drawable.type_no2, R.drawable.type_pm, R.drawable.type_light, R.drawable.type_well_position,
//            R.drawable.type_smoke, R.drawable.type_tempature_humidity, R.drawable.type_inclination,
//            R.drawable.type_flame, R.drawable.type_water_monitoring, R.drawable.type_leak,
//            R.drawable.type_gas, R.drawable.type_emergency_call, R.drawable.type_tracking_device,
//            R.drawable.type_fire_hydraulic, R.drawable.type_geomagnetic, R.drawable.type_lock_monitoring,
//            R.drawable.type_tempature, R.drawable.type_on_off_monitoring, R.drawable.type_ammeter
//            , R.mipmap.ic_sensor_electric_alarm, R.mipmap.ic_sensor_infrared,
//            R.mipmap.ic_sensor_manual_alarm, R.mipmap.ic_sensor_sound_light_alarm
//    };

//    String SELECT_TYPE_VALUES[] = {
//            "all", "ch4", "co", "co2", "no2", "pm10,pm2_5", "light", "cover,level", "smoke",
//            "humidity,temperature", "collision,pitch,roll", "flame", "distance", "drop", "lpg",
//            "alarm", "altitude,latitude,longitude", "waterPressure", "magnetic", "door",
//            "humidity,temp1,temperature", "connection", "CURRENT_A,CURRENT_B,CURRENT_C,ID," +
//            "TOTAL_POWER,VOLTAGE_A," +
//            "VOLTAGE_B,VOLTAGE_C", "curr_val,elec_energy_val,leakage_val,power_val,temp_val,vol_val", "infrared",
//            "manual_alarm", "sound_light_alarm"
//    };
//    String SENSOR_MENU_MATCHER_ARRAY[] = {
//            "all", "ch4", "co", "co2", "no2", "pm10|pm2_5",
//            "light", "cover|level", "smoke", "humidity|temperature", "collision|pitch|roll", "flame",
//            "distance", "drop", "lpg", "alarm", "latitude|longitude|altitude", "waterPressure", "magnetic",
//            "door", "temp1", "connection", "CURRENT_A|CURRENT_B|CURRENT_C|ID|TOTAL_POWER|VOLTAGE_A|VOLTAGE_B|VOLTAGE_C",
//            "curr_val|elec_energy_val|leakage_val|power_val|temp_val|vol_val", "infrared",
//            "manual_alarm", "sound_light_alarm"
//    };
    //    String INDEX_TYPE_VALUES[] = {
//            "all",
//            "alarm", "altitude,latitude,longitude", "ch4",
//            "co", "co2", "collision,pitch,roll",
//            "cover,level", "distance", "magnetic", "door", "drop",
//            "flame", "light", "lpg", "no2",
//            "pm10,pm2_5", "smoke", "humidity,temperature",
//            "waterPressure", "humidity,temp1,temperature", "connection", "CURRENT_A,CURRENT_B,CURRENT_C,ID," +
//            "TOTAL_POWER,VOLTAGE_A," +
//            "VOLTAGE_B,VOLTAGE_C", "curr_val,elec_energy_val,leakage_val,power_val,temp_val,vol_val", "infrared",
//            "manual_alarm", "sound_light_alarm"
//
//    };
//    String SENSOR_MENU_ARRAY[] = {
//            "all",
//            "alarm", "latitude|longitude|altitude", "ch4",
//            "co", "co2", "collision|pitch|roll",
//            "cover|level", "distance", "magnetic", "door", "drop",
//            "flame", "light", "lpg", "no2",
//            "pm10|pm2_5", "smoke", "humidity|temperature",
//            "waterPressure", "temp1", "connection",
//            "CURRENT_A|CURRENT_B|CURRENT_C|ID|TOTAL_POWER|VOLTAGE_A|VOLTAGE_B|VOLTAGE_C",
//            "curr_val|elec_energy_val|leakage_val|power_val|temp_val|vol_val", "infrared",
//            "manual_alarm", "sound_light_alarm"
//    };
//    Integer[] TYPE_MENU_RESOURCE = {
//            R.mipmap.ic_sensor_call, R.mipmap.ic_sensor_tracker, R.mipmap.ic_sensor_ch4,
//            R.mipmap.ic_sensor_co, R.mipmap.ic_sensor_co2, R.mipmap.ic_sensor_angle,
//            R.mipmap.ic_sensor_cover, R.mipmap.ic_sensor_level, R.mipmap.ic_sensor_magnetic, R.mipmap.ic_sensor_lock,
//            R.mipmap.ic_sensor_drop, R.mipmap.ic_sensor_flame, R.mipmap.ic_sensor_light,
//            R.mipmap.ic_sensor_lpg, R.mipmap.ic_sensor_no2, R.mipmap.ic_sensor_pm,
//            R.mipmap.ic_sensor_smoke, R.mipmap.ic_sensor_temp_humi, R.mipmap.ic_sensor_water_pressure, R.mipmap
//            .ic_sensor_temp_humi, R.mipmap.ic_sensor_connection, R.mipmap.ic_sensor_electric_meter, R.mipmap
//            .ic_sensor_electric_alarm, R.mipmap
//            .ic_sensor_infrared, R.mipmap.ic_sensor_manual_alarm, R.mipmap.ic_sensor_sound_light_alarm
//
//    };
//    String[] ALARM_TAG_ARRAY = {"一氧化碳", "二氧化碳", "甲烷", "液化石油气", "二氧化氮", "跑冒滴漏", "灯井监控",
//            "PM2.5/10", "烟雾", "温湿度", "火焰", "倾角", "水压",
//            "门锁检测", "追踪器", "水位检测", "光线"
//    };
//    String[] ALARM_TAG_EN_ARRAY = {"co", "co2", "ch4", "lpg", "no2", "drop", "cover,level",
//            "pm2_5,pm10", "smoke", "temperature,humidity", "flame", "collision|pitch|roll", "waterPressure",
//            "magnetic", "tracker", "distance", "light"
//    };

    int[] confirmStatusArray = {R.string.to_be_confirmed, R.string.real_warning, R.string.false_positive, R.string.test_patrol, R.string.security_risks};
    int[] confirmStatusTextColorArray = {R.color.c_8058a5, R.color.c_f34a4a, R.color.c_8058a5, R.color.c_8058a5, R.color.c_ff8d34};

    int[] confirmAlarmResultInfoArray = {R.string.text_fire_alarm_empty, R.string.text_fire_alarm, R.string.text_no_fire_alarm, R.string.text_fire_alarm_test,
            R.string.text_fire_alarm_risk};
    int[] confirmAlarmTypeArray = {R.string.the_ohter, R.string.alarm_type_bnormal_power, R.string.alarm_type_production_operation, R.string.alarm_type_smoke, R.string.alarm_type_indoor_fire, R.string.alarm_type_cooking, R.string.alarm_type_gas_leak, R.string.alarm_type_artificial_arson};
    //    private final String[] confirmAlarmPlaceArray = {"其他", "小区", "工厂", "居民作坊", "仓库", "商铺店面", "商场", "出租房",};
    int[] confirmAlarmPlaceArray = {R.string.the_ohter, R.string.community, R.string.factory, R.string.resident_workshop, R.string.warehouse, R.string.shop_storefront, R.string.the_mall, R.string.rental_house};
//    String[] WEEK_TITLE_ARRAY = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};

    //巡检任务异常标签
    int[] INSPECTION_EXCEPTION_TAGS = {R.string.inspection_exception_tag_equipment_disassembly, R.string.inspection_exception_tag_low_battery, R.string.inspection_exception_tag_device_exception, R.string.inspection_exception_tag_sensor_anomaly, R.string.inspection_exception_tag_damaged_indicator, R.string.inspection_exception_tag_screen_damage, R.string.inspection_exception_tag_button_damage, R.string.inspection_exception_tag_appearance_damage, R.string.inspection_exception_tag_device_lost, R.string.inspection_exception_tag_bluetooth_exception, R.string.inspection_exception_tag_loose_equipment};
    //巡检任务状态颜色值及对应文本
    int[] INSPECTION_STATUS_COLORS = {R.color.c_8058a5, R.color.c_3aa7f0, R.color.c_ff8d34, R.color.c_29c093, R.color.c_a6a6a6};
    int[] INSPECTION_STATUS_TEXTS = {R.string.inspection_status_text_pending_execution, R.string.inspection_status_text_executing, R.string.inspection_status_text_timeout_not_completed, R.string.inspection_status_text_completed, R.string.inspection_status_text_timeout_completed};
    //
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
    String EXTRA_EVENT_LOGIN_DATA = "extra_event_login_data";
    String EXTRA_GRANTS_HAS_STATION = "extra_grants_has_station";
    String EXTRA_GRANTS_HAS_CONTRACT = "extra_grants_has_contract";
    String EXTRA_GRANTS_HAS_SCAN_LOGIN = "extra_grants_has_scan_login";
    String EXTRA_GRANTS_HAS_SUB_MERCHANT = "extra_grants_has_sub_merchant";
    String EXTRA_GRANTS_HAS_MERCHANT_CHANGE = "extra_grants_has_merchant_change";
    String EXTRA_GRANTS_HAS_INSPECTION_TASK_LIST = "extra_grants_has_inspection_task_list";
    String EXTRA_GRANTS_HAS_INSPECTION_TASK_MODIFY = "extra_grants_has_inspection_task_modify";
    String EXTRA_GRANTS_HAS_INSPECTION_DEVICE_LIST = "extra_grants_has_inspection_device_list";
    String EXTRA_GRANTS_HAS_INSPECTION_DEVICE_MODIFY = "extra_grants_has_inspection_device_modify";
    String EXTRA_GRANTS_HAS_ALARM_LOG_INFO = "extra_grants_has_alarm_log_info";
    String EXTRA_GRANTS_HAS_MALFUNCTION_INFO = "extra_grants_has_malfunction_info";
    String EXTRA_GRANTS_HAS_DEVICE_BRIEF = "extra_grants_has_device_brief";
    String EXTRA_GRANTS_HAS_DEVICE_SIGNAL_CHECK = "extra_grants_has_device_signal_check";
    String EXTRA_GRANTS_HAS_DEVICE_SIGNAL_CONFIG = "extra_grants_has_device_signal_config";
    String EXTRA_DEVICE_INFO = "extra_device_info";
    String EXTRA_DEPLOY_ANALYZER_MODEL = "extra_deploy_analyzer_model";
    String EXTRA_INSPECTION_DEPLOY_OLD_DEVICE_INFO = "extra_inspection_deploy_old_device_info";
    //扫描来源
    String EXTRA_SCAN_ORIGIN_TYPE = "extra_scan_origin_type";
    String EXTRA_DEPLOY_SUCCESS_ADDRESS = "extra_deploy_success_address";
    String EXTRA_DEPLOY_RESULT_MODEL = "extra_deploy_result_model";
    //
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
    String EXTRA_SETTING_DEPLOY_CONTACT = "extra_setting_deploy_contact";
    String EXTRA_SETTING_CONTENT = "extra_setting_content";
    String EXTRA_SETTING_INDEX = "extra_setting_index";
    String EXTRA_SETTING_TAG_LIST = "extra_tag_list";
    String EXTRA_CONTAINS_DATA = "extra_contains_data";
    //部署相关
    String EXTRA_DEPLOY_PHOTO = "extra_deploy_photo";
    String EXTRA_DEPLOY_TO_PHOTO = "extra_deploy_to_photo";
    String EXTRA_DEPLOY_TO_MAP = "extra_deploy_to_map";
    String EXTRA_DEPLOY_TO_SN = "extra_deploy_to_sn";
    String EXTRA_INSPECTION_TASK_ITEM_DEVICE_DETAIL = "extra_inspection_task_item_device_detail";
    String EXTRA_DEPLOY_RECORD_DETAIL = "extra_deploy_record_detail";
    String EXTRA_JUST_DISPLAY_PIC = "extra_just_display_pic";
    String EXTRA_DEPLOY_DISPLAY_MAP = "extra_deploy_display_map";

    //巡检相关
    String EXTRA_INSPECTION_START_TIME = "extra_inspection_start_time";
    String EXTRA_INSPECTION_INDEX_TASK_INFO = "extra_inspection_index_task_info";
    String EXTRA_INSPECTION_INSTRUCTION_DEVICE_TYPE = "extra_inspection_instruction_device_type";

    //
    String EXTRA_CONTRACT_TYPE = "extra_contract_type";
    String EXTRA_CONTRACT_ID = "EXTRA_CONTRACT_ID";
    //故障
    String EXTRA_MALFUNCTION_INFO = "extra_malfunction_info";

    //
    String EXTRA_CONTRACT_RESULT_TYPE = "extra_contract_result_type";
    //
    String PREFERENCE_LOGIN_ID = "preference_login_id";
    String PREFERENCE_LOCAL_DEVICES_MERGETYPES = "preference_local_devices_mergetypes";
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
    int SENSOR_STATUS_MALFUNCTION = 4;
    int DISPLAY_STATUS_CONFIRM = 0;
    int DISPLAY_STATUS_ALARM = 1;
    int DISPLAY_STATUS_MIS_DESCRIPTION = 2;
    int DISPLAY_STATUS_TEST = 3;
    int DISPLAY_STATUS_RISKS = 4;
    int DIRECTION_DOWN = 0;
    int DIRECTION_UP = 1;
    int TYPE_LIST = 0;
    int TYPE_GRID = 1;

    //信号测试 band
    String LORA_BAND_US915 = "US915";
    String LORA_BAND_EU433 = "EU433";
    String LORA_BAND_EU868 = "EU868";
    String LORA_BAND_AU915 = "AU915";
    String LORA_BAND_AS923 = "AS923";
    String LORA_BAND_SE433 = "SE433";
    String LORA_BAND_SE470 = "SE470";
    String LORA_BAND_SE915 = "SE915";
    String LORA_BAND_SE780 = "SE780";
    String LORA_BAND_CN470 = "CN470";
    //
    int MODEL_ALARM_STATUS_EVENT_CODE_CREATE = 0;
    int MODEL_ALARM_STATUS_EVENT_CODE_RECOVERY = 2;
    int MODEL_ALARM_STATUS_EVENT_CODE_CONFIRM = 3;
    int MODEL_ALARM_STATUS_EVENT_CODE_RECONFIRM = 4;
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
    int EVENT_DATA_DEPLOY_CHANGE_RESULT_CONTINUE = 0x43;
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
    int EVENT_DATA_DEPLOY_MAP = 0x31;
    int EVENT_DATA_INSPECTION_UPLOAD_EXCEPTION_CODE = 0x32;
    int EVENT_DATA_INSPECTION_UPLOAD_NORMAL_CODE = 0x36;
    int EVENT_DATA_INSPECTION_TASK_STATUS_CHANGE = 0x40;
    int EVENT_DATA_ALARM_FRESH_ALARM_DATA = 0x34;
    int EVENT_DATA_ALARM_SOCKET_DISPLAY_STATUS = 0x35;
    int EVENT_DATA_SOCKET_MONITOR_POINT_OPERATION_TASK_RESULT = 0x43;
    int EVENT_DATA_SCAN_LOGIN_SUCCESS = 0x39;

    int EVENT_DATA_ALARM_POP_IMAGES = 0x33;


    int TYPE_SCAN_DEPLOY_DEVICE = 0x29;
    int TYPE_SCAN_DEPLOY_STATION = 0x39;
    int TYPE_SCAN_LOGIN = 0x30;
    int TYPE_SCAN_DEPLOY_DEVICE_CHANGE = 0X37;
    int TYPE_SCAN_INSPECTION = 0X38;
    int TYPE_SCAN_DEPLOY_POINT_DISPLAY = 0x41;
    int TYPE_SCAN_SIGNAL_CHECK = 0x42;
    //部署结果
    int DEPLOY_RESULT_MODEL_CODE_DEPLOY_FAILED = -1;
    int DEPLOY_RESULT_MODEL_CODE_DEPLOY_NOT_UNDER_THE_ACCOUNT = -2;
    int DEPLOY_RESULT_MODEL_CODE_DEPLOY_SUCCESS = 0;

    ArrayList<String> DEVICE_CONTROL_DEVICE_TYPES = new ArrayList<String>(2){
        {
            add("fhsj_elec_fires");
            add("acrel_fires");
        }
    };
}
