package com.sensoro.smartcity.server.bean;

import java.util.List;

public class MonitorOptionsBean {
    /**
     * type : electric
     * name : 电学量监测
     * sensorTypes : [{"id":"leakage_val","defaultValue":200,"name":"漏电流上限","conditionType":"gt"},{"id":"a_val","defaultValue":200,"name":"A相电压上限","conditionType":"gt"},{"id":"b_val","defaultValue":200,"name":"B相电压上限","conditionType":"gt"},{"id":"c_val","defaultValue":200,"name":"C相电压上限","conditionType":"gt"},{"id":"a_curr","defaultValue":200,"name":"A相电流上限","conditionType":"gt"},{"id":"b_curr","defaultValue":200,"name":"B相电流上限","conditionType":"gt"},{"id":"c_curr","defaultValue":200,"name":"C相电流上限","conditionType":"gt"},{"id":"total_sz","defaultValue":500,"name":"总功率上限","conditionType":"gt"},{"id":"total_yg","defaultValue":1000,"name":"用功功率上限","conditionType":"gt"},{"id":"total_wg","defaultValue":600,"name":"无功功率上限","conditionType":"gt"},{"id":"total_factor","defaultValue":2,"name":"功率因数上限","conditionType ":"gt "},{"id":"elec_energy_val","defaultValue":300,"name":"用电量上限","conditionType":"gt"}]
     */

    private String type;
    private String name;
    private List<SensorTypesBean> sensorTypes;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SensorTypesBean> getSensorTypes() {
        return sensorTypes;
    }

    public void setSensorTypes(List<SensorTypesBean> sensorTypes) {
        this.sensorTypes = sensorTypes;
    }

    public static class SensorTypesBean {
        /**
         * id : leakage_val
         * defaultValue : 200
         * name : 漏电流上限
         * conditionType : gt
         * conditionType  : gt
         */

        private String id;
        private int defaultValue;
        private String name;
        private String conditionType;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(int defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getConditionType() {
            return conditionType;
        }

        public void setConditionType(String conditionType) {
            this.conditionType = conditionType;
        }
    }
}
