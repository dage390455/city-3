package com.sensoro.common.server.bean;

import androidx.annotation.NonNull;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sensoro on 17/11/21.
 */

public class DeviceRecentInfo implements Comparable<DeviceRecentInfo> {

    private Float altitudeAvg;
    private Float altitudeMax;
    private Float altitudeMin;
    private Float batteryAvg;
    private Float latitudeAvg;
    private Float longitudeAvg;
    private Float artificialGasAvg;
    private Float waterPressureAvg;
    private Float pitchAvg;
    private Float rollAvg;
    private Float ch4Avg;
    private Float coAvg;
    private Float co2Avg;
    private Float distanceAvg;
    private Float humidityAvg;
    private Float intervalAvg;
    private Float lightAvg;
    private Float lpgAvg;
    private Float no2Avg;
    private Float pm2_5Avg;
    private Float pm10Avg;
    private Float so2Avg;
    private Float temperatureAvg;

    private Float batteryMax;
    private Float latitudeMax;
    private Float longitudeMax;
    private Float artificialGasMax;
    private Float waterPressureMax;
    private Float pitchMax;
    private Float rollMax;
    private Float ch4Max;
    private Float coMax;
    private Float co2Max;
    private Float distanceMax;
    private Float humidityMax;
    private Float intervalMax;
    private Float lightMax;
    private Float lpgMax;
    private Float no2Max;
    private Float pm2_5Max;
    private Float pm10Max;
    private Float so2Max;
    private Float temperatureMax;

    private Float batteryMin;
    private Float latitudeMin;
    private Float longitudeMin;
    private Float artificialGasMin;
    private Float waterPressureMin;
    private Float pitchMin;
    private Float rollMin;
    private Float ch4Min;
    private Float coMin;
    private Float co2Min;
    private Float distanceMin;
    private Float humidityMin;
    private Float intervalMin;
    private Float lightMin;
    private Float lpgMin;
    private Float no2Min;
    private Float pm2_5Min;
    private Float pm10Min;
    private Float so2Min;
    private Float temperatureMin;
    private String date;

    public Float getMaxValue(String key) {
        if (key.equalsIgnoreCase("altitude")) {
            return altitudeMax;
        } else if (key.equalsIgnoreCase("battery")) {
            return batteryMax;
        } else if (key.equalsIgnoreCase("longitude")) {
            return longitudeMax;
        } else if (key.equalsIgnoreCase("latitude")) {
            return latitudeMax;
        } else if (key.equalsIgnoreCase("temperature")) {
            return temperatureMax;
        } else if (key.equalsIgnoreCase("so2")) {
            return so2Max;
        } else if (key.equalsIgnoreCase("no2")) {
            return no2Max;
        } else if (key.equalsIgnoreCase("pm2_5")) {
            return pm2_5Max;
        } else if (key.equalsIgnoreCase("pm10")) {
            return pm10Max;
        } else if (key.equalsIgnoreCase("interval")) {
            return intervalMax;
        } else if (key.equalsIgnoreCase("lpg")) {
            return lpgMax;
        } else if (key.equalsIgnoreCase("light")) {
            return lightMax;
        } else if (key.equalsIgnoreCase("humidity")) {
            return humidityMax;
        } else if (key.equalsIgnoreCase("distance")) {
            return distanceMax;
        } else if (key.equalsIgnoreCase("co")) {
            return coMax;
        } else if (key.equalsIgnoreCase("co2")) {
            return co2Max;
        } else if (key.equalsIgnoreCase("ch4")) {
            return ch4Max;
        } else if (key.equalsIgnoreCase("artificialGas")) {
            return artificialGasMax;
        } else if (key.equalsIgnoreCase("waterPressure")) {
            return waterPressureMax;
        } else if (key.equalsIgnoreCase("pitch")) {
            return pitchMax;
        } else if (key.equalsIgnoreCase("roll")) {
            return rollMax;
        } else {
            return 0f;
        }
    }

    public Float getMinValue(String key) {
        if (key.equalsIgnoreCase("altitude")) {
            return altitudeMin;
        } else if (key.equalsIgnoreCase("battery")) {
            return batteryMin;
        } else if (key.equalsIgnoreCase("longitude")) {
            return longitudeMin;
        } else if (key.equalsIgnoreCase("latitude")) {
            return latitudeMin;
        } else if (key.equalsIgnoreCase("temperature")) {
            return temperatureMin;
        } else if (key.equalsIgnoreCase("so2")) {
            return so2Min;
        } else if (key.equalsIgnoreCase("no2")) {
            return no2Min;
        } else if (key.equalsIgnoreCase("pm2_5")) {
            return pm2_5Min;
        } else if (key.equalsIgnoreCase("pm10")) {
            return pm10Min;
        } else if (key.equalsIgnoreCase("interval")) {
            return intervalMin;
        } else if (key.equalsIgnoreCase("lpg")) {
            return lpgMin;
        } else if (key.equalsIgnoreCase("light")) {
            return lightMin;
        } else if (key.equalsIgnoreCase("humidity")) {
            return humidityMin;
        } else if (key.equalsIgnoreCase("distance")) {
            return distanceMin;
        } else if (key.equalsIgnoreCase("co")) {
            return coMin;
        } else if (key.equalsIgnoreCase("co2")) {
            return co2Min;
        } else if (key.equalsIgnoreCase("ch4")) {
            return ch4Min;
        } else if (key.equalsIgnoreCase("artificialGas")) {
            return artificialGasMin;
        } else if (key.equalsIgnoreCase("waterPressure")) {
            return waterPressureMin;
        } else if (key.equalsIgnoreCase("pitch")) {
            return pitchMin;
        } else if (key.equalsIgnoreCase("roll")) {
            return rollMin;
        } else {
            return 0f;
        }
    }
    public Float getAvgValue(String key) {
        if (key.equalsIgnoreCase("altitude")) {
            return altitudeAvg;
        } else if (key.equalsIgnoreCase("battery")) {
            return batteryAvg;
        } else if (key.equalsIgnoreCase("longitude")) {
            return longitudeAvg;
        } else if (key.equalsIgnoreCase("latitude")) {
            return latitudeAvg;
        } else if (key.equalsIgnoreCase("temperature")) {
            return temperatureAvg;
        } else if (key.equalsIgnoreCase("so2")) {
            return so2Avg;
        } else if (key.equalsIgnoreCase("no2")) {
            return no2Avg;
        } else if (key.equalsIgnoreCase("pm2_5")) {
            return pm2_5Avg;
        } else if (key.equalsIgnoreCase("pm10")) {
            return pm10Avg;
        } else if (key.equalsIgnoreCase("interval")) {
            return intervalAvg;
        } else if (key.equalsIgnoreCase("lpg")) {
            return lpgAvg;
        } else if (key.equalsIgnoreCase("light")) {
            return lightAvg;
        } else if (key.equalsIgnoreCase("humidity")) {
            return humidityAvg;
        } else if (key.equalsIgnoreCase("distance")) {
            return distanceAvg;
        } else if (key.equalsIgnoreCase("co")) {
            return coAvg;
        } else if (key.equalsIgnoreCase("co2")) {
            return co2Avg;
        } else if (key.equalsIgnoreCase("ch4")) {
            return ch4Avg;
        } else if (key.equalsIgnoreCase("artificialGas")) {
            return artificialGasAvg;
        } else if (key.equalsIgnoreCase("waterPressure")) {
            return waterPressureAvg;
        } else if (key.equalsIgnoreCase("pitch")) {
            return pitchAvg;
        } else if (key.equalsIgnoreCase("roll")) {
            return rollAvg;
        } else {
            return 0f;
        }
    }

    public Float getAltitudeAvg() {
        return altitudeAvg;
    }

    public void setAltitudeAvg(Float altitudeAvg) {
        this.altitudeAvg = altitudeAvg;
    }

    public Float getAltitudeMax() {
        return altitudeMax;
    }

    public void setAltitudeMax(Float altitudeMax) {
        this.altitudeMax = altitudeMax;
    }

    public Float getAltitudeMin() {
        return altitudeMin;
    }

    public void setAltitudeMin(Float altitudeMin) {
        this.altitudeMin = altitudeMin;
    }

    public Float getBatteryAvg() {
        return batteryAvg;
    }

    public void setBatteryAvg(Float batteryAvg) {
        this.batteryAvg = batteryAvg;
    }

    public Float getLatitudeAvg() {
        return latitudeAvg;
    }

    public void setLatitudeAvg(Float latitudeAvg) {
        this.latitudeAvg = latitudeAvg;
    }

    public Float getLongitudeAvg() {
        return longitudeAvg;
    }

    public void setLongitudeAvg(Float longitudeAvg) {
        this.longitudeAvg = longitudeAvg;
    }

    public Float getArtificialGasAvg() {
        return artificialGasAvg;
    }

    public void setArtificialGasAvg(Float artificialGasAvg) {
        this.artificialGasAvg = artificialGasAvg;
    }

    public Float getWaterPressureAvg() {
        return waterPressureAvg;
    }

    public void setWaterPressureAvg(Float waterPressureAvg) {
        this.waterPressureAvg = waterPressureAvg;
    }

    public Float getPitchAvg() {
        return pitchAvg;
    }

    public void setPitchAvg(Float pitchAvg) {
        this.pitchAvg = pitchAvg;
    }

    public Float getRollAvg() {
        return rollAvg;
    }

    public void setRollAvg(Float rollAvg) {
        this.rollAvg = rollAvg;
    }

    public Float getCh4Avg() {
        return ch4Avg;
    }

    public void setCh4Avg(Float ch4Avg) {
        this.ch4Avg = ch4Avg;
    }

    public Float getCoAvg() {
        return coAvg;
    }

    public void setCoAvg(Float coAvg) {
        this.coAvg = coAvg;
    }

    public Float getCo2Avg() {
        return co2Avg;
    }

    public void setCo2Avg(Float co2Avg) {
        this.co2Avg = co2Avg;
    }

    public Float getDistanceAvg() {
        return distanceAvg;
    }

    public void setDistanceAvg(Float distanceAvg) {
        this.distanceAvg = distanceAvg;
    }

    public Float getHumidityAvg() {
        return humidityAvg;
    }

    public void setHumidityAvg(Float humidityAvg) {
        this.humidityAvg = humidityAvg;
    }

    public Float getIntervalAvg() {
        return intervalAvg;
    }

    public void setIntervalAvg(Float intervalAvg) {
        this.intervalAvg = intervalAvg;
    }

    public Float getLightAvg() {
        return lightAvg;
    }

    public void setLightAvg(Float lightAvg) {
        this.lightAvg = lightAvg;
    }

    public Float getLpgAvg() {
        return lpgAvg;
    }

    public void setLpgAvg(Float lpgAvg) {
        this.lpgAvg = lpgAvg;
    }

    public Float getNo2Avg() {
        return no2Avg;
    }

    public void setNo2Avg(Float no2Avg) {
        this.no2Avg = no2Avg;
    }

    public Float getPm2_5Avg() {
        return pm2_5Avg;
    }

    public void setPm2_5Avg(Float pm2_5Avg) {
        this.pm2_5Avg = pm2_5Avg;
    }

    public Float getPm10Avg() {
        return pm10Avg;
    }

    public void setPm10Avg(Float pm10Avg) {
        this.pm10Avg = pm10Avg;
    }

    public Float getSo2Avg() {
        return so2Avg;
    }

    public void setSo2Avg(Float so2Avg) {
        this.so2Avg = so2Avg;
    }

    public Float getTemperatureAvg() {
        return temperatureAvg;
    }

    public void setTemperatureAvg(Float temperatureAvg) {
        this.temperatureAvg = temperatureAvg;
    }

    public Float getBatteryMax() {
        return batteryMax;
    }

    public void setBatteryMax(Float batteryMax) {
        this.batteryMax = batteryMax;
    }

    public Float getLatitudeMax() {
        return latitudeMax;
    }

    public void setLatitudeMax(Float latitudeMax) {
        this.latitudeMax = latitudeMax;
    }

    public Float getLongitudeMax() {
        return longitudeMax;
    }

    public void setLongitudeMax(Float longitudeMax) {
        this.longitudeMax = longitudeMax;
    }

    public Float getArtificialGasMax() {
        return artificialGasMax;
    }

    public void setArtificialGasMax(Float artificialGasMax) {
        this.artificialGasMax = artificialGasMax;
    }

    public Float getWaterPressureMax() {
        return waterPressureMax;
    }

    public void setWaterPressureMax(Float waterPressureMax) {
        this.waterPressureMax = waterPressureMax;
    }

    public Float getPitchMax() {
        return pitchMax;
    }

    public void setPitchMax(Float pitchMax) {
        this.pitchMax = pitchMax;
    }

    public Float getRollMax() {
        return rollMax;
    }

    public void setRollMax(Float rollMax) {
        this.rollMax = rollMax;
    }

    public Float getCh4Max() {
        return ch4Max;
    }

    public void setCh4Max(Float ch4Max) {
        this.ch4Max = ch4Max;
    }

    public Float getCoMax() {
        return coMax;
    }

    public void setCoMax(Float coMax) {
        this.coMax = coMax;
    }

    public Float getCo2Max() {
        return co2Max;
    }

    public void setCo2Max(Float co2Max) {
        this.co2Max = co2Max;
    }

    public Float getDistanceMax() {
        return distanceMax;
    }

    public void setDistanceMax(Float distanceMax) {
        this.distanceMax = distanceMax;
    }

    public Float getHumidityMax() {
        return humidityMax;
    }

    public void setHumidityMax(Float humidityMax) {
        this.humidityMax = humidityMax;
    }

    public Float getIntervalMax() {
        return intervalMax;
    }

    public void setIntervalMax(Float intervalMax) {
        this.intervalMax = intervalMax;
    }

    public Float getLightMax() {
        return lightMax;
    }

    public void setLightMax(Float lightMax) {
        this.lightMax = lightMax;
    }

    public Float getLpgMax() {
        return lpgMax;
    }

    public void setLpgMax(Float lpgMax) {
        this.lpgMax = lpgMax;
    }

    public Float getNo2Max() {
        return no2Max;
    }

    public void setNo2Max(Float no2Max) {
        this.no2Max = no2Max;
    }

    public Float getPm2_5Max() {
        return pm2_5Max;
    }

    public void setPm2_5Max(Float pm2_5Max) {
        this.pm2_5Max = pm2_5Max;
    }

    public Float getPm10Max() {
        return pm10Max;
    }

    public void setPm10Max(Float pm10Max) {
        this.pm10Max = pm10Max;
    }

    public Float getSo2Max() {
        return so2Max;
    }

    public void setSo2Max(Float so2Max) {
        this.so2Max = so2Max;
    }

    public Float getTemperatureMax() {
        return temperatureMax;
    }

    public void setTemperatureMax(Float temperatureMax) {
        this.temperatureMax = temperatureMax;
    }

    public Float getBatteryMin() {
        return batteryMin;
    }

    public void setBatteryMin(Float batteryMin) {
        this.batteryMin = batteryMin;
    }

    public Float getLatitudeMin() {
        return latitudeMin;
    }

    public void setLatitudeMin(Float latitudeMin) {
        this.latitudeMin = latitudeMin;
    }

    public Float getLongitudeMin() {
        return longitudeMin;
    }

    public void setLongitudeMin(Float longitudeMin) {
        this.longitudeMin = longitudeMin;
    }

    public Float getArtificialGasMin() {
        return artificialGasMin;
    }

    public void setArtificialGasMin(Float artificialGasMin) {
        this.artificialGasMin = artificialGasMin;
    }

    public Float getWaterPressureMin() {
        return waterPressureMin;
    }

    public void setWaterPressureMin(Float waterPressureMin) {
        this.waterPressureMin = waterPressureMin;
    }

    public Float getPitchMin() {
        return pitchMin;
    }

    public void setPitchMin(Float pitchMin) {
        this.pitchMin = pitchMin;
    }

    public Float getRollMin() {
        return rollMin;
    }

    public void setRollMin(Float rollMin) {
        this.rollMin = rollMin;
    }

    public Float getCh4Min() {
        return ch4Min;
    }

    public void setCh4Min(Float ch4Min) {
        this.ch4Min = ch4Min;
    }

    public Float getCoMin() {
        return coMin;
    }

    public void setCoMin(Float coMin) {
        this.coMin = coMin;
    }

    public Float getCo2Min() {
        return co2Min;
    }

    public void setCo2Min(Float co2Min) {
        this.co2Min = co2Min;
    }

    public Float getDistanceMin() {
        return distanceMin;
    }

    public void setDistanceMin(Float distanceMin) {
        this.distanceMin = distanceMin;
    }

    public Float getHumidityMin() {
        return humidityMin;
    }

    public void setHumidityMin(Float humidityMin) {
        this.humidityMin = humidityMin;
    }

    public Float getIntervalMin() {
        return intervalMin;
    }

    public void setIntervalMin(Float intervalMin) {
        this.intervalMin = intervalMin;
    }

    public Float getLightMin() {
        return lightMin;
    }

    public void setLightMin(Float lightMin) {
        this.lightMin = lightMin;
    }

    public Float getLpgMin() {
        return lpgMin;
    }

    public void setLpgMin(Float lpgMin) {
        this.lpgMin = lpgMin;
    }

    public Float getNo2Min() {
        return no2Min;
    }

    public void setNo2Min(Float no2Min) {
        this.no2Min = no2Min;
    }

    public Float getPm2_5Min() {
        return pm2_5Min;
    }

    public void setPm2_5Min(Float pm2_5Min) {
        this.pm2_5Min = pm2_5Min;
    }

    public Float getPm10Min() {
        return pm10Min;
    }

    public void setPm10Min(Float pm10Min) {
        this.pm10Min = pm10Min;
    }

    public Float getSo2Min() {
        return so2Min;
    }

    public void setSo2Min(Float so2Min) {
        this.so2Min = so2Min;
    }

    public Float getTemperatureMin() {
        return temperatureMin;
    }

    public void setTemperatureMin(Float temperatureMin) {
        this.temperatureMin = temperatureMin;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    @Override
    public int compareTo(@NonNull DeviceRecentInfo another) {
        if (strToDate(this.getDate()).getTime() < strToDate(another.getDate()).getTime()) {
            return -1;
        } else if (strToDate(this.getDate()).getTime() == strToDate(another.getDate()).getTime()) {
            return 0;
        } else {
            return 1;
        }
    }

    public  Date strToDate(String strDate) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH");
        ParsePosition pos = new ParsePosition(0);
        Date strtodate = formatter.parse(strDate, pos);
        return strtodate;
    }
}
