package com.sensoro.smartcity.server.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DeviceDisplayOptionsSpection {

    /**
     * displayOptions : {"majors":["leakage_val"],"minors":["a_val","a_curr","t1_val","b_val","b_curr","t2_val","c_val","c_curr","t3_val","t4_val","total_sz","total_yg","total_wg","total_factor","elec_energy_val"],"special":{"type":"table","data":[[{"type":"label","name":""},{"type":"label","name":"A相"},{"type":"label","name":"B相"},{"type":"label","name":"C相"}],[{"type":"label","name":"电压"},{"type":"sensorType","value":"a_val"},{"type":"sensorType","value":"b_val"},{"type":"sensorType","value":"c_val"}],[{"type":"label","name":"电流"},{"type":"sensorType","value":"a_curr"},{"type":"sensorType","value":"b_curr"},{"type":"sensorType","value":"c_curr"}],[{"type":"label","name":"温度"},{"type":"sensorType","value":"t1_val"},{"type":"sensorType","value":"t2_val"},{"type":"sensorType","value":"t3_val"}]]}}
     * monitorOptions : [{"type":"electric","name":"电学量监测","sensorTypes":[{"id":"leakage_val","default":200,"name":"漏电流上限","conditionType":"gt"},{"id":"a_val","default":200,"name":"A相电压上限","conditionType":"gt"},{"id":"b_val","default":200,"name":"B相电压上限","conditionType":"gt"},{"id":"c_val","default":200,"name":"C相电压上限","conditionType":"gt"},{"id":"a_curr","default":200,"name":"A相电流上限","conditionType":"gt"},{"id":"b_curr","default":200,"name":"B相电流上限","conditionType":"gt"},{"id":"c_curr","default":200,"name":"C相电流上限","conditionType":"gt"},{"id":"total_sz","default":500,"name":"总功率上限","conditionType":"gt"},{"id":"total_yg","default":1000,"name":"用功功率上限","conditionType":"gt"},{"id":"total_wg","default":600,"name":"无功功率上限","conditionType":"gt"},{"id":"total_factor","default":2,"name":"功率因数上限","conditionType":"gt"},{"id":"elec_energy_val","default":300,"name":"用电量上限","conditionType":"gt"}]},{"type":"temp_humi","name":"温湿度监测","sensorTypes":[{"id":"t4_val","default":50,"name":"箱体温度上限","conditionType":"gt"},{"id":"t1_val","default":40,"name":"A相温度上限","conditionType":"gt"},{"id":"t2_val","default":45,"name":"B相温度上限","conditionType":"gt"},{"id":"t3_val","default":55,"name":"C相温度上限","conditionType":"gt"}]}]
     */

    private DisplayOptionsBean displayOptions;
    private List<MonitorOptionsBean> monitorOptions;

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

    public static class DisplayOptionsBean {
        /**
         * majors : ["leakage_val"]
         * minors : ["a_val","a_curr","t1_val","b_val","b_curr","t2_val","c_val","c_curr","t3_val","t4_val","total_sz","total_yg","total_wg","total_factor","elec_energy_val"]
         * special : {"type":"table","data":[[{"type":"label","name":""},{"type":"label","name":"A相"},{"type":"label","name":"B相"},{"type":"label","name":"C相"}],[{"type":"label","name":"电压"},{"type":"sensorType","value":"a_val"},{"type":"sensorType","value":"b_val"},{"type":"sensorType","value":"c_val"}],[{"type":"label","name":"电流"},{"type":"sensorType","value":"a_curr"},{"type":"sensorType","value":"b_curr"},{"type":"sensorType","value":"c_curr"}],[{"type":"label","name":"温度"},{"type":"sensorType","value":"t1_val"},{"type":"sensorType","value":"t2_val"},{"type":"sensorType","value":"t3_val"}]]}
         */

        private SpecialBean special;
        private List<String> majors;
        private List<String> minors;

        public SpecialBean getSpecial() {
            return special;
        }

        public void setSpecial(SpecialBean special) {
            this.special = special;
        }

        public List<String> getMajors() {
            return majors;
        }

        public void setMajors(List<String> majors) {
            this.majors = majors;
        }

        public List<String> getMinors() {
            return minors;
        }

        public void setMinors(List<String> minors) {
            this.minors = minors;
        }

        public static class SpecialBean {
            /**
             * type : table
             * data : [[{"type":"label","name":""},{"type":"label","name":"A相"},{"type":"label","name":"B相"},{"type":"label","name":"C相"}],[{"type":"label","name":"电压"},{"type":"sensorType","value":"a_val"},{"type":"sensorType","value":"b_val"},{"type":"sensorType","value":"c_val"}],[{"type":"label","name":"电流"},{"type":"sensorType","value":"a_curr"},{"type":"sensorType","value":"b_curr"},{"type":"sensorType","value":"c_curr"}],[{"type":"label","name":"温度"},{"type":"sensorType","value":"t1_val"},{"type":"sensorType","value":"t2_val"},{"type":"sensorType","value":"t3_val"}]]
             */

            private String type;
            private List<List<DataBean>> data;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public List<List<DataBean>> getData() {
                return data;
            }

            public void setData(List<List<DataBean>> data) {
                this.data = data;
            }

            public static class DataBean {
                /**
                 * type : label
                 * name :
                 */

                private String type;
                private String name;

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
            }
        }
    }

    public static class MonitorOptionsBean {
        /**
         * type : electric
         * name : 电学量监测
         * sensorTypes : [{"id":"leakage_val","default":200,"name":"漏电流上限","conditionType":"gt"},{"id":"a_val","default":200,"name":"A相电压上限","conditionType":"gt"},{"id":"b_val","default":200,"name":"B相电压上限","conditionType":"gt"},{"id":"c_val","default":200,"name":"C相电压上限","conditionType":"gt"},{"id":"a_curr","default":200,"name":"A相电流上限","conditionType":"gt"},{"id":"b_curr","default":200,"name":"B相电流上限","conditionType":"gt"},{"id":"c_curr","default":200,"name":"C相电流上限","conditionType":"gt"},{"id":"total_sz","default":500,"name":"总功率上限","conditionType":"gt"},{"id":"total_yg","default":1000,"name":"用功功率上限","conditionType":"gt"},{"id":"total_wg","default":600,"name":"无功功率上限","conditionType":"gt"},{"id":"total_factor","default":2,"name":"功率因数上限","conditionType":"gt"},{"id":"elec_energy_val","default":300,"name":"用电量上限","conditionType":"gt"}]
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
             * default : 200
             * name : 漏电流上限
             * conditionType : gt
             */

            private String id;
            @SerializedName("default")
            private int defaultX;
            private String name;
            private String conditionType;

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public int getDefaultX() {
                return defaultX;
            }

            public void setDefaultX(int defaultX) {
                this.defaultX = defaultX;
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
}
