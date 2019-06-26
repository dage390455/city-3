package com.sensoro.common.server.security.bean;

import java.io.Serializable;

public class SecurityAlarmInfo implements Serializable, Comparable<SecurityAlarmInfo> {
    public String id;
    public boolean isValid;//是否有效
    public int warnType; // 1:外来 2:重点 3：入侵
    public String  capturePhotoUrl;//抓拍图片
    public String  focusOriPhoto;//重点人脸
    public String  focusMatchrate;//重点人脸匹配度
    public String  warnName;
    public String  warnAddress;
    public long  warnTime;


    @Override
    public int compareTo(SecurityAlarmInfo o) {
        return 0;
    }
}
