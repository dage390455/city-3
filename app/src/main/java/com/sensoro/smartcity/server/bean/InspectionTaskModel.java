package com.sensoro.smartcity.server.bean;

import java.util.List;

public class InspectionTaskModel {

        private int count;
        private List<InspectionIndexTaskInfo> tasks;

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public List<InspectionIndexTaskInfo> getTasks() {
            return tasks;
        }

        public void setTasks(List<InspectionIndexTaskInfo> tasks) {
            this.tasks = tasks;
        }


}
