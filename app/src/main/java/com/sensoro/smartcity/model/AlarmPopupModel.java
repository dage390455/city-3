package com.sensoro.smartcity.model;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.adapter.model.SecurityRisksAdapterModel;
import com.sensoro.smartcity.server.bean.AlarmPopupDataBean;

import java.util.ArrayList;
import java.util.List;

public class AlarmPopupModel {
    public boolean isSecurityRiskRequire;
    public boolean securityRiskVisible;
    public String title;
    public List<AlarmPopupSubModel> subAlarmPopupModels;
    public List<AlarmPopupTagModel> mainTags;
    public String desc;
    public String mRemark;
    public int resButtonBg = R.drawable.shape_button;
    public String mergeType;
    public String sensorType;
    public AlarmPopupDataBean configAlarmPopupDataBean;
    public ArrayList<SecurityRisksAdapterModel> securityRisksList;

    public static class AlarmPopupSubModel {
        public boolean isRequire;
        public String title;
        public String tips;
        public String key;
        public List<AlarmPopupTagModel> subTags;
    }

    public static class AlarmPopupTagModel {
        public Integer id;
        public boolean isChose;
        public String name;
        public int resDrawable = R.drawable.shape_bg_solid_29c_20dp_corner;
    }

}
