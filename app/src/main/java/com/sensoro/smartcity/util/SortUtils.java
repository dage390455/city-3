package com.sensoro.smartcity.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SortUtils {
    public static List<String> sortSensorTypes(String[] sensorTypes) {
        final List<String> tempSensorTypes = new ArrayList<>();
        //CURRENT_A|CURRENT_B|CURRENT_C|ID|TOTAL_POWER|VOLTAGE_A|VOLTAGE_B|VOLTAGE_C
        Arrays.sort(sensorTypes);
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
            } else if (originList.contains("altitude")) {
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
            tempSensorTypes.addAll(originList);
        }
        return tempSensorTypes;
    }
}
