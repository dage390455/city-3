package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.List;

public class AlarmPopupDataConfigBean implements Serializable {

    /**
     * title : 预警场所
     * groups : [{"displayStatus":[1,2,3,4],"mergeTypes":[],"sensorTypes":[],"labels":[{"title":"小区","id":20001},{"title":"出租房","id":20002},{"title":"工厂","id":20003},{"title":"居民作坊","id":20004},{"title":"仓库","id":20005},{"title":"店铺店面","id":20006},{"title":"商场","id":20007},{"title":"其他","id":20008}]}]
     */

    private String title;
    private List<AlarmPopupDataGroupsBean> groups;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<AlarmPopupDataGroupsBean> getGroups() {
        return groups;
    }

    public void setGroups(List<AlarmPopupDataGroupsBean> groups) {
        this.groups = groups;
    }

}
