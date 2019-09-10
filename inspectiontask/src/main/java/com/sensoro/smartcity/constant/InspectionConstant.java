package com.sensoro.smartcity.constant;

import com.sensoro.inspectiontask.R;

public class InspectionConstant {
    //巡检任务异常标签
    public static final int[] INSPECTION_EXCEPTION_TAGS = {R.string.inspection_exception_tag_equipment_disassembly, R.string.inspection_exception_tag_low_battery, R.string.inspection_exception_tag_device_exception, R.string.inspection_exception_tag_sensor_anomaly, R.string.inspection_exception_tag_damaged_indicator, R.string.inspection_exception_tag_screen_damage, R.string.inspection_exception_tag_button_damage, R.string.inspection_exception_tag_appearance_damage, R.string.inspection_exception_tag_device_lost, R.string.inspection_exception_tag_bluetooth_exception, R.string.inspection_exception_tag_loose_equipment};
    //巡检任务状态颜色值及对应文本
    public static final int[] INSPECTION_STATUS_COLORS = {R.color.c_8058a5, R.color.c_3aa7f0, R.color.c_ff8d34, R.color.c_1dbb99, R.color.c_a6a6a6};
    public static final int[] INSPECTION_STATUS_TEXTS = {R.string.inspection_status_text_pending_execution, R.string.inspection_status_text_executing, R.string.inspection_status_text_timeout_not_completed, R.string.inspection_status_text_completed, R.string.inspection_status_text_timeout_completed};
}
