package com.sensoro.smartcity.server.bean;

import java.util.List;

public class DisplayOptionsBean {
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
