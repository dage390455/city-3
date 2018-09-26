package com.sensoro.smartcity.server.bean;

import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;

import java.util.List;

public class InspectionTaskModel {

  /*       * count : 2
         * tasks : [{"inspectorIds":["5ba218e768443250d09741d3"],"deviceSummary":[{"deviceType":"fhsj_smoke","num":1,"_id":"5ba9b155f11db9772ee33013"}],"identifier":"XJ201809251109571828","status":0,"createdTime":1537847637182,"createdBy":"590c235044aa4369905d455b","endTime":1538323200000,"beginTime":1536768000000,"name":"北京市望京soho一期巡检任务","id":"5ba9b155f11db9772ee33012","execution":"0/1"},{"inspectorIds":["5ba218e768443250d09741d3"],"deviceSummary":[{"deviceType":"fhsj_smoke","num":1,"_id":"5ba9b211f11db9772ee33019"}],"identifier":"XJ201809251109059898","status":0,"createdTime":1537847825989,"createdBy":"590c235044aa4369905d455b","endTime":1538323200000,"beginTime":1536768000000,"name":"北京市望京soho一期巡检任务","id":"5ba9b211f11db9772ee33018","execution":"0/1"}]
         */

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
