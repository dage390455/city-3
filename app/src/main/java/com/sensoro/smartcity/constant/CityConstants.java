package com.sensoro.smartcity.constant;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.MaterialValueModel;

import java.util.LinkedHashMap;

/**
 * Created by sensoro on 17/7/27.
 */

public interface CityConstants {
    int[] DEVICE_STATUS_ARRAY = {R.string.main_page_warn, R.string.normal, R.string.status_lost, R.string.status_inactive, R.string.status_malfunction};
    int[] DEVICE_STATUS_COLOR_ARRAY = {R.color.c_f34a4a, R.color.c_1dbb99, R.color.c_5d5d5d, R.color.c_b6b6b6, R.color.c_fdc83b};
    int[] STATION_STATUS_ARRAY = {R.string.status_inactive, R.string.normal, R.string.status_alarm_true, R.string.status_emergency_alarm, R.string.status_timeout_not_reported, R.string.status_offline, R.string.status_malfunction};
    int[] STATION_STATUS_COLOR_ARRAY = {R.color.c_b6b6b6, R.color.c_1dbb99, R.color.c_f34a4a, R.color.c_f34a4a, R.color.c_5d5d5d, R.color.c_5d5d5d, R.color.c_fdc83b};
    int[] confirmStatusArray = {R.string.to_be_confirmed, R.string.real_warning, R.string.false_positive, R.string.test_patrol, R.string.security_risks};
    int[] confirmStatusTextColorArray = {R.color.c_8058a5, R.color.c_f34a4a, R.color.c_8058a5, R.color.c_8058a5, R.color.c_ff8d34};

    int[] confirmAlarmResultInfoArray = {R.string.text_fire_alarm_empty, R.string.text_fire_alarm, R.string.text_no_fire_alarm, R.string.text_fire_alarm_test,
            R.string.text_fire_alarm_risk};
    int[] confirmAlarmTypeArray = {R.string.the_ohter, R.string.alarm_type_bnormal_power, R.string.alarm_type_production_operation, R.string.alarm_type_smoke, R.string.alarm_type_indoor_fire, R.string.alarm_type_cooking, R.string.alarm_type_gas_leak, R.string.alarm_type_artificial_arson, R.string.alarm_type_combustible_self_ignition};
    //    private final String[] confirmAlarmPlaceArray = {"其他", "小区", "工厂", "居民作坊", "仓库", "商铺店面", "商场", "出租房",};
    int[] confirmAlarmPlaceArray = {R.string.the_ohter, R.string.community, R.string.factory, R.string.resident_workshop, R.string.warehouse, R.string.shop_storefront, R.string.the_mall, R.string.rental_house};
//    String[] WEEK_TITLE_ARRAY = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};

    //巡检任务异常标签
    int[] INSPECTION_EXCEPTION_TAGS = {R.string.inspection_exception_tag_equipment_disassembly, R.string.inspection_exception_tag_low_battery, R.string.inspection_exception_tag_device_exception, R.string.inspection_exception_tag_sensor_anomaly, R.string.inspection_exception_tag_damaged_indicator, R.string.inspection_exception_tag_screen_damage, R.string.inspection_exception_tag_button_damage, R.string.inspection_exception_tag_appearance_damage, R.string.inspection_exception_tag_device_lost, R.string.inspection_exception_tag_bluetooth_exception, R.string.inspection_exception_tag_loose_equipment};
    //巡检任务状态颜色值及对应文本
    int[] INSPECTION_STATUS_COLORS = {R.color.c_8058a5, R.color.c_3aa7f0, R.color.c_ff8d34, R.color.c_1dbb99, R.color.c_a6a6a6};
    int[] INSPECTION_STATUS_TEXTS = {R.string.inspection_status_text_pending_execution, R.string.inspection_status_text_executing, R.string.inspection_status_text_timeout_not_completed, R.string.inspection_status_text_completed, R.string.inspection_status_text_timeout_completed};

    LinkedHashMap<String, MaterialValueModel> MATERIAL_VALUE_MAP = new LinkedHashMap<String, MaterialValueModel>() {
        {
            put("1", new MaterialValueModel(16, 9));
            put("1.5", new MaterialValueModel(20, 15));
            put("2.5", new MaterialValueModel(27, 21));
            put("4", new MaterialValueModel(36, 27));
            put("6", new MaterialValueModel(47, 36));
            put("10", new MaterialValueModel(64, 51));
            put("16", new MaterialValueModel(90, 69));
            put("25", new MaterialValueModel(119, 90));
            put("35", new MaterialValueModel(147, 112));
            put("50", new MaterialValueModel(185, 142));
            put("70", new MaterialValueModel(229, 177));
            put("95", new MaterialValueModel(281, 216));
            put("120", new MaterialValueModel(324, 246));
            put("150", new MaterialValueModel(371, 281));
            put("185", new MaterialValueModel(423, 328));

        }
    };
}

