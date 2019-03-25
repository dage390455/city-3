package com.sensoro.smartcity.factory;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.model.AlarmPopupModel;
import com.sensoro.smartcity.server.bean.AlarmPopupDataConfigBean;
import com.sensoro.smartcity.server.bean.AlarmPopupDataDisplayBean;
import com.sensoro.smartcity.server.bean.AlarmPopupDataDisplayItemsBean;
import com.sensoro.smartcity.server.bean.AlarmPopupDataGroupsBean;
import com.sensoro.smartcity.server.bean.AlarmPopupDataLabelsBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AlarmPopupConfigFactory {

    public static AlarmPopupModel createAlarmPopupModel(Integer displayStatus, AlarmPopupModel alarmPopupModel) {
        alarmPopupModel.isRequire = true;
        alarmPopupModel.title = "预警结果类型";
        //
        Map<String, AlarmPopupDataDisplayBean> display = alarmPopupModel.configAlarmPopupDataBean.getDisplay();
        Map<String, AlarmPopupDataConfigBean> config = alarmPopupModel.configAlarmPopupDataBean.getConfig();
        if (config != null && display != null) {
            //展示的map
            HashMap<Integer, AlarmPopupDataDisplayBean> displayShowMap = new HashMap<>();
            //
            List<AlarmPopupModel.AlarmPopupTagModel> mainAlarmPopupTagModels = new ArrayList<>();
            //main 配置
            Set<Map.Entry<String, AlarmPopupDataDisplayBean>> entriesDefault = display.entrySet();
            for (Map.Entry<String, AlarmPopupDataDisplayBean> entryDefault : entriesDefault) {
                AlarmPopupDataDisplayBean value = entryDefault.getValue();
                if (value != null) {
                    AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = new AlarmPopupModel.AlarmPopupTagModel();
                    alarmPopupTagModel.name = value.getTitle();
                    alarmPopupTagModel.id = value.getDisplayStatus();
                    mainAlarmPopupTagModels.add(alarmPopupTagModel);
                    displayShowMap.put(alarmPopupTagModel.id, value);
                }

            }
            final Comparator<AlarmPopupModel.AlarmPopupTagModel> modelComparator = new Comparator<AlarmPopupModel.AlarmPopupTagModel>() {
                @Override
                public int compare(AlarmPopupModel.AlarmPopupTagModel o1, AlarmPopupModel.AlarmPopupTagModel o2) {
                    if (o1.id - o2.id > 0) {
                        return -1;
                    } else if (o1.id - o2.id == 0) {
                        return 0;
                    } else {
                        return 1;
                    }

                }
            };
            Collections.sort(mainAlarmPopupTagModels, modelComparator);
            alarmPopupModel.mainTags = mainAlarmPopupTagModels;
            if (displayStatus == null) {
                //默认配置
                //
                AlarmPopupModel.AlarmPopupSubModel alarmPopupSubModelReason = new AlarmPopupModel.AlarmPopupSubModel();
                alarmPopupSubModelReason.isRequire = true;
                alarmPopupSubModelReason.title = "预警成因";
                alarmPopupSubModelReason.tips = "请先选择预警结果类型";
                //
                AlarmPopupModel.AlarmPopupSubModel alarmPopupSubModelPlace = new AlarmPopupModel.AlarmPopupSubModel();
                AlarmPopupDataConfigBean place = config.get("place");
                if (place != null) {
                    alarmPopupSubModelPlace.title = place.getTitle();
                    ArrayList<AlarmPopupModel.AlarmPopupTagModel> subAlarmPopupTagModels = new ArrayList<>();
                    List<AlarmPopupDataGroupsBean> groups = place.getGroups();
                    if (groups != null && groups.size() > 0) {
                        AlarmPopupDataGroupsBean alarmPopupDataGroupsBean = groups.get(0);
                        List<AlarmPopupDataLabelsBean> labels = alarmPopupDataGroupsBean.getLabels();
                        if (labels != null) {
                            for (AlarmPopupDataLabelsBean alarmPopupDataLabelsBean : labels) {
                                AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = new AlarmPopupModel.AlarmPopupTagModel();
                                alarmPopupTagModel.name = alarmPopupDataLabelsBean.getTitle();
                                subAlarmPopupTagModels.add(alarmPopupTagModel);
                            }
                        }
                    }
                    alarmPopupSubModelPlace.subTags = subAlarmPopupTagModels;
                }
                alarmPopupModel.subAlarmPopupModels = new ArrayList<>();
                alarmPopupModel.subAlarmPopupModels.add(alarmPopupSubModelReason);
                alarmPopupModel.subAlarmPopupModels.add(alarmPopupSubModelPlace);
            } else {
                if (1 == displayStatus) {
                    alarmPopupModel.buttonColor = R.color.color_alarm_pup_red;
                }
                AlarmPopupDataDisplayBean alarmPopupDataDisplayBean = displayShowMap.get(displayStatus);
                ArrayList<AlarmPopupModel.AlarmPopupSubModel> alarmPopupSubModelArrayList = new ArrayList<>();

                if (alarmPopupDataDisplayBean != null) {
                    alarmPopupModel.desc = alarmPopupDataDisplayBean.getDescription();
                    List<AlarmPopupDataDisplayItemsBean> items = alarmPopupDataDisplayBean.getItems();
                    if (items != null) {
                        for (AlarmPopupDataDisplayItemsBean alarmPopupDataDisplayItemsBean : items) {
                            AlarmPopupModel.AlarmPopupSubModel alarmPopupSubModel = new AlarmPopupModel.AlarmPopupSubModel();
                            alarmPopupSubModel.isRequire = alarmPopupDataDisplayItemsBean.isRequire();
                            String id = alarmPopupDataDisplayItemsBean.getId();
                            if (id != null) {
                                AlarmPopupDataConfigBean alarmPopupDataConfigBean = config.get(id);
                                if (alarmPopupDataConfigBean != null) {
                                    alarmPopupSubModel.title = alarmPopupDataConfigBean.getTitle();
                                    //查找符合条件的
                                    List<AlarmPopupDataGroupsBean> groups = alarmPopupDataConfigBean.getGroups();
                                    if (groups != null) {
                                        List<AlarmPopupDataLabelsBean> labels = null;
                                        for (int i = 0; i < groups.size(); i++) {
                                            //TODO 算法修改
                                            AlarmPopupDataGroupsBean alarmPopupDataGroupsBean = groups.get(i);
                                            List<Integer> displayStatusGroup = alarmPopupDataGroupsBean.getDisplayStatus();
                                            if (displayStatusGroup != null && displayStatusGroup.contains(displayStatus)) {

                                            }
                                            List<String> mergeTypesGroup = alarmPopupDataGroupsBean.getMergeTypes();
                                            if (mergeTypesGroup != null && mergeTypesGroup.contains(alarmPopupModel.mergeType)) {
                                                //第2层解
                                            }
                                            List<String> sensorTypesGroup = alarmPopupDataGroupsBean.getSensorTypes();
                                            if (sensorTypesGroup != null && sensorTypesGroup.contains(alarmPopupModel.sensorType)) {
                                                //第3层解
                                            }
                                        }


                                    }
//                                    for (AlarmPopupDataGroupsBean alarmPopupDataGroupsBean : groups) {
//                                        List<Integer> displayStatusGroup = alarmPopupDataGroupsBean.getDisplayStatus();
//                                        if (displayStatusGroup != null && displayStatusGroup.contains(displayStatus)) {
//                                            //第1层解
//                                            labels = alarmPopupDataGroupsBean.getLabels();
//                                            List<String> mergeTypesGroup = alarmPopupDataGroupsBean.getMergeTypes();
//                                            if (mergeTypesGroup != null && mergeTypesGroup.contains(alarmPopupModel.mergeType)) {
//                                                //第2层解
//                                                labels = alarmPopupDataGroupsBean.getLabels();
//                                                List<String> sensorTypesGroup = alarmPopupDataGroupsBean.getSensorTypes();
//                                                if (sensorTypesGroup != null && sensorTypesGroup.contains(alarmPopupModel.sensorType)) {
//                                                    //第3层解
//                                                    labels = alarmPopupDataGroupsBean.getLabels();
//                                                    break;
//                                                }
//                                            }
//                                        }
//
//                                        if (flag == 1) {
//                                            List<String> mergeTypesGroup = alarmPopupDataGroupsBean.getMergeTypes();
//                                            if (mergeTypesGroup != null && mergeTypesGroup.contains(alarmPopupModel.mergeType)) {
//                                                //第2层解
//                                                flag = 2;
//                                                labels = alarmPopupDataGroupsBean.getLabels();
//                                            }
//                                        }
//                                        if (flag == 2) {
//                                            List<String> sensorTypesGroup = alarmPopupDataGroupsBean.getSensorTypes();
//                                            if (sensorTypesGroup != null && sensorTypesGroup.contains(alarmPopupModel.sensorType)) {
//                                                //第3层解
//                                                labels = alarmPopupDataGroupsBean.getLabels();
//                                                break;
//                                            }
//                                        }
//
//
//                                    }
                                    if (labels != null) {
                                        ArrayList<AlarmPopupModel.AlarmPopupTagModel> subAlarmPopupTagModels = new ArrayList<>();
                                        for (AlarmPopupDataLabelsBean alarmPopupDataLabelsBean : labels) {
                                            AlarmPopupModel.AlarmPopupTagModel tagModel = createAlarmPopupTagModel(displayStatus);
                                            tagModel.name = alarmPopupDataLabelsBean.getTitle();
                                            tagModel.id = alarmPopupDataLabelsBean.getId();
                                            subAlarmPopupTagModels.add(tagModel);
                                        }
                                        alarmPopupSubModel.subTags = subAlarmPopupTagModels;
                                    }
                                }
                            }


                        }
                        alarmPopupSubModelArrayList.add(alarmPopupSubModel);
                        alarmPopupModel.subAlarmPopupModels = alarmPopupSubModelArrayList;
                    }
                }
            }

        }


    }
        return alarmPopupModel;
}

    private static AlarmPopupModel.AlarmPopupTagModel createAlarmPopupTagModel(Integer displayStatus) {
        AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = new AlarmPopupModel.AlarmPopupTagModel();
        if (1 == displayStatus) {
            alarmPopupTagModel.color = R.color.color_alarm_pup_red;
        }
        return alarmPopupTagModel;
    }

}
