package com.sensoro.smartcity.analyzer;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.SecurityRisksAdapterModel;
import com.sensoro.common.server.bean.AlarmPopupDataBean;
import com.sensoro.common.server.bean.AlarmPopupDataConfigBean;
import com.sensoro.common.server.bean.AlarmPopupDataDisplayBean;
import com.sensoro.common.server.bean.AlarmPopupDataDisplayItemsBean;
import com.sensoro.common.server.bean.AlarmPopupDataGroupsBean;
import com.sensoro.common.server.bean.AlarmPopupDataLabelsBean;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.model.AlarmPopupModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.sensoro.smartcity.constant.CityConstants.confirmAlarmPlaceArray;
import static com.sensoro.smartcity.constant.CityConstants.confirmAlarmTypeArray;

public class AlarmPopupConfigAnalyzer {
    /**
     * 处理配置类型
     *
     * @param displayStatus
     * @param alarmPopupModel
     */
    public static void handleAlarmPopupModel(Integer displayStatus, final AlarmPopupModel alarmPopupModel) {
        //
        AlarmPopupDataBean alarmPopupDataBeanCache = PreferencesHelper.getInstance().getAlarmPopupDataBeanCache();

        if (alarmPopupDataBeanCache != null) {
            //展示的map
            List<AlarmPopupDataDisplayBean> display = alarmPopupDataBeanCache.getDisplay();
            Map<String, AlarmPopupDataConfigBean> config = alarmPopupDataBeanCache.getConfig();
            if (config != null && display != null) {
                HashMap<Integer, AlarmPopupDataDisplayBean> displayShowMap = new HashMap<>();
                //
                List<AlarmPopupModel.AlarmPopupTagModel> mainAlarmPopupTagModels = new ArrayList<>();
                //main 配置
                for (AlarmPopupDataDisplayBean value : display) {
                    if (value != null) {
                        AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = new AlarmPopupModel.AlarmPopupTagModel();
                        alarmPopupTagModel.name = value.getTitle();
                        alarmPopupTagModel.id = value.getDisplayStatus();
                        if (1 == alarmPopupTagModel.id) {
                            alarmPopupTagModel.resDrawable = R.drawable.shape_bg_solid_f3_20dp_corner;
                        } else {
                            alarmPopupTagModel.resDrawable = R.drawable.shape_bg_solid_29c_20dp_corner;
                        }
                        mainAlarmPopupTagModels.add(alarmPopupTagModel);
                        displayShowMap.put(alarmPopupTagModel.id, value);
                    }

                }
                alarmPopupModel.mainTags = mainAlarmPopupTagModels;

                if (displayStatus == null) {
                    //默认配置
                    //
                    AlarmPopupModel.AlarmPopupSubModel alarmPopupSubModelReason = new AlarmPopupModel.AlarmPopupSubModel();
                    alarmPopupSubModelReason.isRequire = true;
                    alarmPopupSubModelReason.title = SensoroCityApplication.getInstance().getString(R.string.alarm_popup_main_reason);
                    alarmPopupSubModelReason.tips = SensoroCityApplication.getInstance().getString(R.string.alarm_popup_main_reason_tip);
                    alarmPopupSubModelReason.key = "reason";
                    //
                    AlarmPopupModel.AlarmPopupSubModel alarmPopupSubModelPlace = new AlarmPopupModel.AlarmPopupSubModel();
                    alarmPopupSubModelPlace.key = "place";
                    ArrayList<AlarmPopupModel.AlarmPopupTagModel> subAlarmPopupTagModels = new ArrayList<>();
                    AlarmPopupDataConfigBean place = config.get("place");
                    if (place != null) {
                        alarmPopupSubModelPlace.title = place.getTitle();
                        List<AlarmPopupDataGroupsBean> groups = place.getGroups();
                        if (groups != null && groups.size() > 0) {
                            AlarmPopupDataGroupsBean alarmPopupDataGroupsBean = groups.get(0);
                            if (alarmPopupDataGroupsBean != null) {
                                List<AlarmPopupDataLabelsBean> labels = alarmPopupDataGroupsBean.getLabels();
                                if (labels != null) {
                                    for (AlarmPopupDataLabelsBean alarmPopupDataLabelsBean : labels) {
                                        AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = createAlarmPopupTagModel(-1);
                                        alarmPopupTagModel.name = alarmPopupDataLabelsBean.getTitle();
                                        alarmPopupTagModel.id = alarmPopupDataLabelsBean.getId();
                                        alarmPopupTagModel.isRequire = false;
                                        subAlarmPopupTagModels.add(alarmPopupTagModel);
                                    }
                                }
                            }

                        }
                        alarmPopupSubModelPlace.subTags = subAlarmPopupTagModels;
                    }
                    alarmPopupModel.subAlarmPopupModels = new ArrayList<>();
                    alarmPopupModel.subAlarmPopupModels.add(alarmPopupSubModelReason);
                    alarmPopupModel.subAlarmPopupModels.add(alarmPopupSubModelPlace);
                } else {
                    if (4 == displayStatus) {
                        alarmPopupModel.isSecurityRiskRequire = true;
                    } else {
                        alarmPopupModel.isSecurityRiskRequire = false;
                    }
                    if (1 == displayStatus) {
                        alarmPopupModel.securityRiskVisible = false;
                        alarmPopupModel.resButtonBg = R.drawable.shape_button_alarm_pup;
                    } else {
                        alarmPopupModel.securityRiskVisible = true;
                        alarmPopupModel.resButtonBg = R.drawable.shape_button;
                    }
                    //重新设置主标签的选择标记
                    for (AlarmPopupModel.AlarmPopupTagModel mainTag : alarmPopupModel.mainTags) {
                        try {
                            mainTag.isChose = displayStatus.equals(mainTag.id);
                        } catch (Exception e) {
                            mainTag.isChose = false;
                        }
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
                                        alarmPopupSubModel.key = alarmPopupDataDisplayItemsBean.getId();
                                        //查找符合条件的
                                        List<AlarmPopupDataGroupsBean> groups = alarmPopupDataConfigBean.getGroups();
                                        if (groups != null) {
                                            int flag = 0;
                                            List<AlarmPopupDataLabelsBean> labels = null;
                                            for (int i = 0; i < groups.size(); i++) {
                                                //TODO 算法修改
                                                AlarmPopupDataGroupsBean alarmPopupDataGroupsBean = groups.get(i);
                                                List<Integer> displayStatusGroup = alarmPopupDataGroupsBean.getDisplayStatus();
                                                if (displayStatusGroup != null && displayStatusGroup.contains(displayStatus)) {
                                                    if (flag < 1) {
                                                        flag = 1;
                                                        labels = alarmPopupDataGroupsBean.getLabels();
                                                    }
                                                    List<String> mergeTypesGroup = alarmPopupDataGroupsBean.getMergeTypes();
                                                    if (mergeTypesGroup != null && mergeTypesGroup.contains(alarmPopupModel.mergeType)) {
                                                        //第2层解
                                                        if (flag < 2) {
                                                            flag = 2;
                                                            labels = alarmPopupDataGroupsBean.getLabels();
                                                        }
                                                        List<String> sensorTypesGroup = alarmPopupDataGroupsBean.getSensorTypes();
                                                        if (sensorTypesGroup != null && sensorTypesGroup.contains(alarmPopupModel.sensorType)) {
                                                            //第3层解
                                                            labels = alarmPopupDataGroupsBean.getLabels();
                                                            break;
                                                        }
                                                    }

                                                }
                                            }
                                            if (labels != null) {
                                                ArrayList<AlarmPopupModel.AlarmPopupTagModel> subAlarmPopupTagModels = new ArrayList<>();
                                                for (AlarmPopupDataLabelsBean alarmPopupDataLabelsBean : labels) {
                                                    AlarmPopupModel.AlarmPopupTagModel tagModel = createAlarmPopupTagModel(displayStatus);
                                                    tagModel.name = alarmPopupDataLabelsBean.getTitle();
                                                    tagModel.id = alarmPopupDataLabelsBean.getId();
                                                    tagModel.isRequire = alarmPopupSubModel.isRequire;
                                                    subAlarmPopupTagModels.add(tagModel);
                                                }
                                                alarmPopupSubModel.subTags = subAlarmPopupTagModels;
                                                alarmPopupSubModelArrayList.add(alarmPopupSubModel);
                                            }
                                        }

                                    }
                                }
                            }
                            alarmPopupModel.subAlarmPopupModels = alarmPopupSubModelArrayList;
                        }
                    }
                }
            }
        }
    }

    private static AlarmPopupModel.AlarmPopupTagModel createAlarmPopupTagModel(Integer displayStatus) {
        AlarmPopupModel.AlarmPopupTagModel alarmPopupTagModel = new AlarmPopupModel.AlarmPopupTagModel();
        if (displayStatus != null && 1 == displayStatus) {
            alarmPopupTagModel.resDrawable = R.drawable.shape_bg_solid_f3_20dp_corner;
        } else {
            alarmPopupTagModel.resDrawable = R.drawable.shape_bg_solid_29c_20dp_corner;
        }
        return alarmPopupTagModel;
    }

    /**
     * 通过字典获取指定类型的名称
     *
     * @param type
     * @param id
     * @param context
     * @return
     */
    public static String gerAlarmPopModelName(@NonNull String type, int id, @NonNull Context context) {
        AlarmPopupDataBean alarmPopupDataBeanCache = PreferencesHelper.getInstance().getAlarmPopupDataBeanCache();
        String defaultText = context.getString(R.string.unknown);
        switch (type) {
            case "place":
                try {
                    defaultText = context.getString(confirmAlarmPlaceArray[id]);
                } catch (Exception e) {
                    if (alarmPopupDataBeanCache != null) {
                        Map<String, AlarmPopupDataConfigBean> config = alarmPopupDataBeanCache.getConfig();
                        if (config != null) {
                            String configAlarmPopModelName = getConfigAlarmPopModelName(type, id, config);
                            if (configAlarmPopModelName != null) {
                                defaultText = configAlarmPopModelName;
                            }
                        }
                    }

                }
                break;
            case "reason":
                try {
                    defaultText = context.getString(confirmAlarmTypeArray[id]);
                } catch (Exception e) {
                    if (alarmPopupDataBeanCache != null) {
                        Map<String, AlarmPopupDataConfigBean> config = alarmPopupDataBeanCache.getConfig();
                        if (config != null) {
                            String configAlarmPopModelName = getConfigAlarmPopModelName(type, id, config);
                            if (configAlarmPopModelName != null) {
                                defaultText = configAlarmPopModelName;
                            }
                        }
                    }

                }
                break;
            case "fireType":
                if (alarmPopupDataBeanCache != null) {
                    Map<String, AlarmPopupDataConfigBean> config = alarmPopupDataBeanCache.getConfig();
                    if (config != null) {
                        String configAlarmPopModelName = getConfigAlarmPopModelName(type, id, config);
                        if (configAlarmPopModelName != null) {
                            defaultText = configAlarmPopModelName;
                        }
                    }
                }
                break;
            case "fireStage":
                if (alarmPopupDataBeanCache != null) {
                    Map<String, AlarmPopupDataConfigBean> config = alarmPopupDataBeanCache.getConfig();
                    if (config != null) {
                        String configAlarmPopModelName = getConfigAlarmPopModelName(type, id, config);
                        if (configAlarmPopModelName != null) {
                            defaultText = configAlarmPopModelName;
                        }
                    }
                }

                break;
            default:
                if (alarmPopupDataBeanCache != null) {
                    Map<String, AlarmPopupDataConfigBean> config = alarmPopupDataBeanCache.getConfig();
                    if (config != null) {
                        String configAlarmPopModelName = getConfigAlarmPopModelName(type, id, config);
                        if (configAlarmPopModelName != null) {
                            defaultText = configAlarmPopModelName;
                        }
                    }
                }

                break;
        }


        return defaultText;
    }

    private static String getConfigAlarmPopModelName(@NonNull String type, int id, @NonNull Map<String, AlarmPopupDataConfigBean> config) {
        AlarmPopupDataConfigBean alarmPopupDataConfigBean = config.get(type);
        if (alarmPopupDataConfigBean != null) {
            List<AlarmPopupDataGroupsBean> groups = alarmPopupDataConfigBean.getGroups();
            if (groups != null && groups.size() > 0) {
                for (AlarmPopupDataGroupsBean group : groups) {
                    List<AlarmPopupDataLabelsBean> labels = group.getLabels();
                    if (labels != null && labels.size() > 0) {
                        for (AlarmPopupDataLabelsBean label : labels) {
                            if (id == label.getId()) {
                                return label.getTitle();
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 获取字典对应的安全隐患文本
     *
     * @param securityRisksList
     * @return
     */
    public static String getSecurityRisksText(List<SecurityRisksAdapterModel> securityRisksList) {
        String defaultText = null;
        if (securityRisksList != null && securityRisksList.size() > 0) {
            //分析list
            StringBuilder builder = new StringBuilder();
            for (SecurityRisksAdapterModel securityRisksAdapterModel : securityRisksList) {
                StringBuilder stringBuilder = new StringBuilder();
                String location = securityRisksAdapterModel.place;
                if (!TextUtils.isEmpty(location)) {
                    stringBuilder.append(location);
                }
                for (String behavior : securityRisksAdapterModel.action) {
                    stringBuilder.append(behavior).append("、");
                }
                String text = stringBuilder.toString();
                if (text.endsWith("、")) {
                    text = text.substring(0, text.lastIndexOf("、"));
                }
                builder.append(text).append(";").append("\n");
            }
            defaultText = builder.toString();
            if (defaultText.endsWith("\n")) {
                defaultText = defaultText.substring(0, defaultText.lastIndexOf("\n"));
            }
            if (defaultText.endsWith(";")) {
                defaultText = defaultText.substring(0, defaultText.lastIndexOf(";"));
            }
            if (!TextUtils.isEmpty(defaultText)) {
                return defaultText;
            }
        }
        return defaultText;
    }

    /**
     * 检查必填项
     *
     * @param alarmPopupModel
     * @return
     */
    public static boolean canGoOnNext(@NonNull AlarmPopupModel alarmPopupModel) {
        ArrayList<Boolean> canDoNexts = new ArrayList<>();
        if (alarmPopupModel.mainTags != null) {
            boolean canDoNext = false;
            for (AlarmPopupModel.AlarmPopupTagModel mainTag : alarmPopupModel.mainTags) {
                if (mainTag.isChose) {
                    canDoNext = true;
                    break;
                }
            }
            canDoNexts.add(canDoNext);
        }
        if (alarmPopupModel.subAlarmPopupModels != null) {
            for (AlarmPopupModel.AlarmPopupSubModel subAlarmPopupModel : alarmPopupModel.subAlarmPopupModels) {
                if (subAlarmPopupModel.isRequire) {
                    if (subAlarmPopupModel.subTags != null) {
                        boolean canDo = false;
                        for (AlarmPopupModel.AlarmPopupTagModel subTag : subAlarmPopupModel.subTags) {
                            if (subTag.isChose) {
                                canDo = true;
                                break;
                            }
                        }
                        canDoNexts.add(canDo);
                    }
                }
            }
        }
        if (alarmPopupModel.isSecurityRiskRequire) {
            boolean hasContent = alarmPopupModel.securityRisksList != null && alarmPopupModel.securityRisksList.size() > 0;
            canDoNexts.add(hasContent);
        }
        for (Boolean doNext : canDoNexts) {
            if (!doNext) {
                return false;
            }
        }
        return true;
    }

    public static Map<String, Integer> createAlarmPopupServerData(@NonNull final AlarmPopupModel alarmPopupModel) {
        Integer displayStatus = null;
        if (alarmPopupModel.mainTags != null) {
            for (AlarmPopupModel.AlarmPopupTagModel mainTag : alarmPopupModel.mainTags) {
                if (mainTag.isChose) {
                    displayStatus = mainTag.id;
                    break;
                }
            }
        }
        HashMap<String, Integer> map = new HashMap<>();
        map.put("displayStatus", displayStatus);
        if (alarmPopupModel.subAlarmPopupModels != null) {
            for (AlarmPopupModel.AlarmPopupSubModel subAlarmPopupModel : alarmPopupModel.subAlarmPopupModels) {
                if (subAlarmPopupModel.subTags != null) {
                    for (AlarmPopupModel.AlarmPopupTagModel subTag : subAlarmPopupModel.subTags) {
                        if (subTag.isChose) {
                            map.put(subAlarmPopupModel.key, subTag.id);
                        }
                    }
                }
            }
        }
        return map;
    }

}
