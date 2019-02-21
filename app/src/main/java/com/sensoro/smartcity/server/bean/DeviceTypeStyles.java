package com.sensoro.smartcity.server.bean;

import java.util.List;

public class DeviceTypeStyles {
    private String id;
    private String category;
    private String unionType;
    private String mergeType;
    private String thresholdSupported;
    private String intervalSupported;
    private List<String> sensorTypes;
    private boolean alarmReceive;
    private boolean isOwn;
    /**
     * displayOptions : {"majors":["leakage_val"],"minors":["a_val","a_curr","t1_val","b_val","b_curr","t2_val","c_val","c_curr","t3_val","t4_val","total_sz","total_yg","total_wg","total_factor","elec_energy_val"],"special":{"type":"table","data":[[{"type":"label","name":""},{"type":"label","name":"A相"},{"type":"label","name":"B相"},{"type":"label","name":"C相"}],[{"type":"label","name":"电压"},{"type":"sensorType","value":"a_val"},{"type":"sensorType","value":"b_val"},{"type":"sensorType","value":"c_val"}],[{"type":"label","name":"电流"},{"type":"sensorType","value":"a_curr"},{"type":"sensorType","value":"b_curr"},{"type":"sensorType","value":"c_curr"}],[{"type":"label","name":"温度"},{"type":"sensorType","value":"t1_val"},{"type":"sensorType","value":"t2_val"},{"type":"sensorType","value":"t3_val"}]]}}
     * monitorOptions : [{"type":"electric","name":"电学量监测","sensorTypes":[{"id":"leakage_val","defaultValue":200,"name":"漏电流上限","conditionType":"gt"},{"id":"a_val","defaultValue":200,"name":"A相电压上限","conditionType":"gt"},{"id":"b_val","defaultValue":200,"name":"B相电压上限","conditionType":"gt"},{"id":"c_val","defaultValue":200,"name":"C相电压上限","conditionType":"gt"},{"id":"a_curr","defaultValue":200,"name":"A相电流上限","conditionType":"gt"},{"id":"b_curr","defaultValue":200,"name":"B相电流上限","conditionType":"gt"},{"id":"c_curr","defaultValue":200,"name":"C相电流上限","conditionType":"gt"},{"id":"total_sz","defaultValue":500,"name":"总功率上限","conditionType":"gt"},{"id":"total_yg","defaultValue":1000,"name":"用功功率上限","conditionType":"gt"},{"id":"total_wg","defaultValue":600,"name":"无功功率上限","conditionType":"gt"},{"id":"total_factor","defaultValue":2,"name":"功率因数上限","conditionType ":"gt "},{"id":"elec_energy_val","defaultValue":300,"name":"用电量上限","conditionType":"gt"}]},{"type":"temp_humi","name":"温湿度监测","sensorTypes":[{"id":"t4_val","defaultValue":50,"name":"箱体温度上限","conditionType":"gt"},{"id":"t1_val","defaultValue":40,"name":"A相温度上限","conditionType":"gt"},{"id":"t2_val","defaultValue":45,"name":"B相温度上限","conditionType":"gt"},{"id":"t3_val","defaultValue":55,"name":"C相温度上限","conditionType":"gt"}]}]
     */
    private DisplayOptionsBean displayOptions;
    private List<MonitorOptionsBean> monitorOptions;

    /**
     * "taskOptions": [
     * // 是否支持消音
     * "mute",
     * // 是否支持复位
     * "reset",
     * // 是否支持修改密码
     * "password",
     * // 是否支持查看设备
     * "view",
     * // 是否支持自检
     * "check",
     * // 是否支持配置
     * "config",
     * // 是否支持下行断电
     * "open",
     * // 是否支持下行上电
     * "close"
     * ]
     *
     * @return
     */
    private List<String> taskOptions;
    private List<DeployPicInfo> deployPicConfig;


    @Override
    public String toString() {
        return "DeviceTypeStyles{" +
                "id='" + id + '\'' +
                ", unionType='" + unionType + '\'' +
                ", mergeType='" + mergeType + '\'' +
                ", thresholdSupported='" + thresholdSupported + '\'' +
                ", intervalSupported='" + intervalSupported + '\'' +
                ", sensorTypes=" + sensorTypes +
                ", alarmReceive=" + alarmReceive +
                ", category=" + category +
                ", isOwn=" + isOwn +
                '}';
    }

    public boolean isOwn() {
        return isOwn;
    }

    public void setOwn(boolean own) {
        isOwn = own;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public String getMergeType() {
        return mergeType;
    }

    public void setMergeType(String mergeType) {
        this.mergeType = mergeType;
    }

    public String getThresholdSupported() {
        return thresholdSupported;
    }

    public void setThresholdSupported(String thresholdSupported) {
        this.thresholdSupported = thresholdSupported;
    }

    public String getIntervalSupported() {
        return intervalSupported;
    }

    public void setIntervalSupported(String intervalSupported) {
        this.intervalSupported = intervalSupported;
    }

    public List<String> getSensorTypes() {
        return sensorTypes;
    }

    public void setSensorTypes(List<String> sensorTypes) {
        this.sensorTypes = sensorTypes;
    }

    public boolean isAlarmReceive() {
        return alarmReceive;
    }

    public void setAlarmReceive(boolean alarmReceive) {
        this.alarmReceive = alarmReceive;
    }

    public DisplayOptionsBean getDisplayOptions() {
        return displayOptions;
    }

    public void setDisplayOptions(DisplayOptionsBean displayOptions) {
        this.displayOptions = displayOptions;
    }

    public List<MonitorOptionsBean> getMonitorOptions() {
        return monitorOptions;
    }

    public void setMonitorOptions(List<MonitorOptionsBean> monitorOptions) {
        this.monitorOptions = monitorOptions;
    }

    public List<String> getTaskOptions() {
        return taskOptions;
    }

    public void setTaskOptions(List<String> taskOptions) {
        this.taskOptions = taskOptions;
    }

    public List<DeployPicInfo> getDeployPicConfig() {
        return deployPicConfig;
    }

    public void setDeployPicConfig(List<DeployPicInfo> deployPicConfig) {
        this.deployPicConfig = deployPicConfig;
    }
}
