package com.sensoro.smartcity.constant;

import com.sensoro.inspectiontask.R;

public class InspectionConstant {
    //巡检任务异常标签
    public static final  int[] INSPECTION_EXCEPTION_TAGS = {R.string.inspection_exception_tag_equipment_disassembly, R.string.inspection_exception_tag_low_battery, R.string.inspection_exception_tag_device_exception, R.string.inspection_exception_tag_sensor_anomaly, R.string.inspection_exception_tag_damaged_indicator, R.string.inspection_exception_tag_screen_damage, R.string.inspection_exception_tag_button_damage, R.string.inspection_exception_tag_appearance_damage, R.string.inspection_exception_tag_device_lost, R.string.inspection_exception_tag_bluetooth_exception, R.string.inspection_exception_tag_loose_equipment};

    public static final int TASK_STATUS_PEDNING_EXC=0;
    public static final int TASK_STATUS_EXCING=1;
    public static final int TASK_STATUS_TIMEOUE_UNDONE=2;
    public static final int TASK_STATUS_DONE=3;
    public static final int TASK_STATUS_TIMEOUE_DONE=4;
}
