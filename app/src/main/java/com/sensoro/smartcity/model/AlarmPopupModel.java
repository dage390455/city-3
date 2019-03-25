package com.sensoro.smartcity.model;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.server.bean.AlarmPopupDataBean;

import java.util.List;

public class AlarmPopupModel {
    public boolean isRequire;
    public String title;
    public List<AlarmPopupSubModel> subAlarmPopupModels;
    public List<AlarmPopupTagModel> mainTags;
    public String desc;
    public String mRemark;
    public int buttonColor = R.color.c_29c093;
    public String mergeType;
    public String sensorType;
    public AlarmPopupDataBean configAlarmPopupDataBean;

    public static class AlarmPopupSubModel {
        public boolean isRequire;
        public String title;
        public String tips;
        public List<AlarmPopupTagModel> subTags;
    }

    public static class AlarmPopupTagModel {
        public Integer id;
        public boolean isChose;
        public String name;
        public int color = R.color.c_29c093;
    }
}
