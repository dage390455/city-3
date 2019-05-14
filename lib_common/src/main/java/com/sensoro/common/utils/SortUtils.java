package com.sensoro.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SortUtils {
    public static List<String> sortSensorTypes(String[] sensorTypes) {
        final List<String> tempSensorTypes = new ArrayList<>();
        //CURRENT_A|CURRENT_B|CURRENT_C|ID|TOTAL_POWER|VOLTAGE_A|VOLTAGE_B|VOLTAGE_C
//        Arrays.sort(sensorTypes);
        try {
            final List<String> originList = Arrays.asList(sensorTypes);
            Collections.sort(originList, String.CASE_INSENSITIVE_ORDER);
            if (sensorTypes.length == 3) {
                if (originList.contains("collision")) {//collision, pitch,roll
                    sensorTypes[0] = "pitch";
                    sensorTypes[1] = "roll";
                    sensorTypes[2] = "collision";
                } else if (originList.contains("flame")) {//temperature,humidity,flame
                    sensorTypes[0] = "temperature";
                    sensorTypes[1] = "humidity";
                    sensorTypes[2] = "flame";
                } else if (originList.contains("altitude") || originList.contains("longitude") || originList.contains
                        ("latitude")) {
                    sensorTypes[0] = "longitude";
                    sensorTypes[1] = "latitude";
                    sensorTypes[2] = "altitude";
                }
                tempSensorTypes.addAll(originList);
            } else if (sensorTypes.length == 7) {
                if (originList.contains("TOTAL_POWER")) {
                    tempSensorTypes.add("TOTAL_POWER");
                }
            } else {
                if (sensorTypes.length == 2) {
                    if (originList.contains("longitude") || originList.contains
                            ("latitude")) {
                        sensorTypes[0] = "longitude";
                        sensorTypes[1] = "latitude";
                    }
                    if (originList.contains("smoke") && originList.contains("installed")) {
                        sensorTypes[0] = "smoke";
                        sensorTypes[1] = "installed";
                    }
                }
                if (originList.contains("leakage_val") && originList.contains("temp_val")) {
                    sensorTypes[0] = "leakage_val";
                    sensorTypes[1] = "temp_val";
                }
                tempSensorTypes.addAll(originList);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tempSensorTypes;
    }
}
