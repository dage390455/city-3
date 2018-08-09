package com.sensoro.smartcity.server.bean;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by sensoro on 17/7/26.
 */

public class SensorDetailInfo implements Serializable {

    private SensorStruct light;
    private SensorStruct humidity;
    private SensorStruct temperature;
    private SensorStruct battery;
    private SensorStruct interval;
    private SensorStruct water;
    private SensorStruct smoke;
    private SensorStruct installed;
    private SensorStruct roll;
    private SensorStruct pm2_5;
    private SensorStruct pm10;
    private SensorStruct pitch;
    private SensorStruct collision;
    private SensorStruct no2;
    private SensorStruct lpg;
    private SensorStruct level;
    private SensorStruct leak;
    private SensorStruct jinggai;
    private SensorStruct drop;
    private SensorStruct cover;
    private SensorStruct alarm;
    private SensorStruct co2;
    private SensorStruct co;
    private SensorStruct ch4;
    private SensorStruct angle;
    private SensorStruct flame;
    private SensorStruct distance;
    private SensorStruct latitude;
    private SensorStruct longitude;
    private SensorStruct altitude;
    private SensorStruct artificialGas;
    private SensorStruct waterPressure;
    private SensorStruct magnetic;

    private SensorStruct door;
    private SensorStruct temp1;
    private SensorStruct connection;
    //
    private SensorStruct CURRENT_A;
    private SensorStruct CURRENT_B;
    private SensorStruct CURRENT_C;
    private SensorStruct ID;
    private SensorStruct TOTAL_POWER;
    private SensorStruct VOLTAGE_A;
    private SensorStruct VOLTAGE_B;


    // curr_val|elec_energy_val|leakage_val|power_val|temp_val|vol_val
    private SensorStruct curr_val;
    private SensorStruct elec_energy_val;
    private SensorStruct leakage_val;
    private SensorStruct power_val;
    private SensorStruct temp_val;
    private SensorStruct vol_val;
    //
    private SensorStruct infrared;
    private SensorStruct manual_alarm;
    private SensorStruct sound_light_alarm;


    public SensorStruct getConnection() {
        return connection;
    }

    public void setConnection(SensorStruct connection) {
        this.connection = connection;
    }

    public SensorStruct getDoor() {
        return door;
    }

    public void setDoor(SensorStruct door) {
        this.door = door;
    }

    public SensorStruct getInfrared() {
        return infrared;
    }

    public void setInfrared(SensorStruct infrared) {
        this.infrared = infrared;
    }

    public SensorStruct getManual_alarm() {
        return manual_alarm;
    }

    public void setManual_alarm(SensorStruct manual_alarm) {
        this.manual_alarm = manual_alarm;
    }

    public SensorStruct getSound_light_alarm() {
        return sound_light_alarm;
    }

    public void setSound_light_alarm(SensorStruct sound_light_alarm) {
        this.sound_light_alarm = sound_light_alarm;
    }

    public SensorStruct getCurr_val() {
        return curr_val;
    }

    public void setCurr_val(SensorStruct curr_val) {
        this.curr_val = curr_val;
    }

    public SensorStruct getElec_energy_val() {
        return elec_energy_val;
    }

    public void setElec_energy_val(SensorStruct elec_energy_val) {
        this.elec_energy_val = elec_energy_val;
    }

    public SensorStruct getLeakage_val() {
        return leakage_val;
    }

    public void setLeakage_val(SensorStruct leakage_val) {
        this.leakage_val = leakage_val;
    }

    public SensorStruct getPower_val() {
        return power_val;
    }

    public void setPower_val(SensorStruct power_val) {
        this.power_val = power_val;
    }

    public SensorStruct getTemp_val() {
        return temp_val;
    }

    public void setTemp_val(SensorStruct temp_val) {
        this.temp_val = temp_val;
    }

    public SensorStruct getVol_val() {
        return vol_val;
    }

    public void setVol_val(SensorStruct vol_val) {
        this.vol_val = vol_val;
    }

    public SensorStruct getCURRENT_A() {
        return CURRENT_A;
    }

    public void setCURRENT_A(SensorStruct CURRENT_A) {
        this.CURRENT_A = CURRENT_A;
    }

    public SensorStruct getCURRENT_B() {
        return CURRENT_B;
    }

    public void setCURRENT_B(SensorStruct CURRENT_B) {
        this.CURRENT_B = CURRENT_B;
    }

    public SensorStruct getCURRENT_C() {
        return CURRENT_C;
    }

    public void setCURRENT_C(SensorStruct CURRENT_C) {
        this.CURRENT_C = CURRENT_C;
    }

    public SensorStruct getID() {
        return ID;
    }

    public void setID(SensorStruct ID) {
        this.ID = ID;
    }

    public SensorStruct getTOTAL_POWER() {
        return TOTAL_POWER;
    }

    public void setTOTAL_POWER(SensorStruct TOTAL_POWER) {
        this.TOTAL_POWER = TOTAL_POWER;
    }

    public SensorStruct getVOLTAGE_A() {
        return VOLTAGE_A;
    }

    public void setVOLTAGE_A(SensorStruct VOLTAGE_A) {
        this.VOLTAGE_A = VOLTAGE_A;
    }

    public SensorStruct getVOLTAGE_B() {
        return VOLTAGE_B;
    }

    public void setVOLTAGE_B(SensorStruct VOLTAGE_B) {
        this.VOLTAGE_B = VOLTAGE_B;
    }

    public SensorStruct getVOLTAGE_C() {
        return VOLTAGE_C;
    }

    public void setVOLTAGE_C(SensorStruct VOLTAGE_C) {
        this.VOLTAGE_C = VOLTAGE_C;
    }

    private SensorStruct VOLTAGE_C;
    //CURRENT_A|CURRENT_B|CURRENT_C|ID|TOTAL_POWER|VOLTAGE_A|VOLTAGE_B|VOLTAGE_C

    public SensorStruct getTemp1() {
        return temp1;
    }

    public void setTemp1(SensorStruct temp1) {
        this.temp1 = temp1;
    }

    private HashMap<String, SensorStruct> mMap = new HashMap<>();

    public HashMap<String, SensorStruct> loadData() {
        if (light != null) {
            mMap.put("light", light);
        }
        if (humidity != null) {
            mMap.put("humidity", humidity);
        }
        if (temperature != null) {
            mMap.put("temperature", temperature);
        }
        if (battery != null) {
            mMap.put("battery", battery);
        }
        if (interval != null) {
            mMap.put("interval", interval);
        }
        if (water != null) {
            mMap.put("water", water);
        }
        if (smoke != null) {
            mMap.put("smoke", smoke);
        }
        if (installed != null) {
            mMap.put("installed", installed);
        }
        if (roll != null) {
            mMap.put("roll", roll);
        }
        if (pm2_5 != null) {
            mMap.put("pm2_5", pm2_5);
        }
        if (pm10 != null) {
            mMap.put("pm10", pm10);
        }
        if (no2 != null) {
            mMap.put("no2", no2);
        }
        if (pitch != null) {
            mMap.put("pitch", pitch);
        }
        if (collision != null) {
            mMap.put("collision", collision);
        }
        if (lpg != null) {
            mMap.put("lpg", lpg);
        }
        if (leak != null) {
            mMap.put("leak", leak);
        }
        if (level != null) {
            mMap.put("level", level);
        }
        if (jinggai != null) {
            mMap.put("jinggai", jinggai);
        }
        if (drop != null) {
            mMap.put("drop", drop);
        }
        if (cover != null) {
            mMap.put("cover", cover);
        }
        if (alarm != null) {
            mMap.put("alarm", alarm);
        }
        if (co2 != null) {
            mMap.put("co2", co2);
        }
        if (angle != null) {
            mMap.put("angle", angle);
        }
        if (co != null) {
            mMap.put("co", co);
        }
        if (ch4 != null) {
            mMap.put("ch4", ch4);
        }
        if (flame != null) {
            mMap.put("flame", flame);
        }
        if (distance != null) {
            mMap.put("distance", distance);
        }
        if (latitude != null) {
            mMap.put("latitude", latitude);
        }
        if (longitude != null) {
            mMap.put("longitude", longitude);
        }
        if (altitude != null) {
            mMap.put("altitude", altitude);
        }
        if (artificialGas != null) {
            mMap.put("artificialGas", artificialGas);
        }
        if (waterPressure != null) {
            mMap.put("waterPressure", waterPressure);
        }
        if (magnetic != null) {
            mMap.put("magnetic", magnetic);
        }
        if (door != null) {
            mMap.put("door", door);
        }
        if (temp1 != null) {
            mMap.put("temp1", temp1);
        }
        if (connection != null) {
            mMap.put("connection", connection);
        }
        //CURRENT_A|CURRENT_B|CURRENT_C|ID|TOTAL_POWER|VOLTAGE_A|VOLTAGE_B|VOLTAGE_C
        //
        if (CURRENT_A != null) {
            mMap.put("CURRENT_A", CURRENT_A);
        }
        if (CURRENT_B != null) {
            mMap.put("CURRENT_B", CURRENT_B);
        }
        if (CURRENT_C != null) {
            mMap.put("CURRENT_C", CURRENT_C);
        }
        if (ID != null) {
            mMap.put("ID", ID);
        }
        if (TOTAL_POWER != null) {
            mMap.put("TOTAL_POWER", TOTAL_POWER);
        }
        if (VOLTAGE_A != null) {
            mMap.put("VOLTAGE_A", VOLTAGE_A);
        }
        if (VOLTAGE_B != null) {
            mMap.put("VOLTAGE_B", VOLTAGE_B);
        }
        if (VOLTAGE_C != null) {
            mMap.put("VOLTAGE_C", VOLTAGE_C);
        }
        //
        if (curr_val != null) {
            mMap.put("curr_val", curr_val);
        }
        if (elec_energy_val != null) {
            mMap.put("elec_energy_val", elec_energy_val);
        }
        if (leakage_val != null) {
            mMap.put("leakage_val", leakage_val);
        }
        if (power_val != null) {
            mMap.put("power_val", power_val);
        }
        if (temp_val != null) {
            mMap.put("temp_val", temp_val);
        }
        if (vol_val != null) {
            mMap.put("vol_val", vol_val);
        }
        //
        if (infrared != null) {
            mMap.put("infrared", infrared);
        }
        if (manual_alarm != null) {
            mMap.put("manual_alarm", manual_alarm);
        }
        if (sound_light_alarm != null) {
            mMap.put("sound_light_alarm", sound_light_alarm);
        }
        return mMap;
    }

    public SensorStruct getLight() {
        return light;
    }

    public void setLight(SensorStruct light) {
        this.light = light;
    }

    public SensorStruct getHumidity() {
        return humidity;
    }

    public void setHumidity(SensorStruct humidity) {
        this.humidity = humidity;
    }

    public SensorStruct getTemperature() {
        return temperature;
    }

    public void setTemperature(SensorStruct temperature) {
        this.temperature = temperature;
    }

    public SensorStruct getBattery() {
        return battery;
    }

    public void setBattery(SensorStruct battery) {
        this.battery = battery;
    }

    public SensorStruct getInterval() {
        return interval;
    }

    public void setInterval(SensorStruct interval) {
        this.interval = interval;
    }

    public SensorStruct getWater() {
        return water;
    }

    public void setWater(SensorStruct water) {
        this.water = water;
    }

    public SensorStruct getSmoke() {
        return smoke;
    }

    public void setSmoke(SensorStruct smoke) {
        this.smoke = smoke;
    }

    public SensorStruct getRoll() {
        return roll;
    }

    public void setRoll(SensorStruct roll) {
        this.roll = roll;
    }

    public SensorStruct getPm2_5() {
        return pm2_5;
    }

    public void setPm2_5(SensorStruct pm2_5) {
        this.pm2_5 = pm2_5;
    }

    public SensorStruct getPm10() {
        return pm10;
    }

    public void setPm10(SensorStruct pm10) {
        this.pm10 = pm10;
    }

    public SensorStruct getPitch() {
        return pitch;
    }

    public void setPitch(SensorStruct pitch) {
        this.pitch = pitch;
    }

    public SensorStruct getNo2() {
        return no2;
    }

    public void setNo2(SensorStruct no2) {
        this.no2 = no2;
    }

    public SensorStruct getLpg() {
        return lpg;
    }

    public void setLpg(SensorStruct lpg) {
        this.lpg = lpg;
    }

    public SensorStruct getLevel() {
        return level;
    }

    public void setLevel(SensorStruct level) {
        this.level = level;
    }

    public SensorStruct getLeak() {
        return leak;
    }

    public void setLeak(SensorStruct leak) {
        this.leak = leak;
    }

    public SensorStruct getJinggai() {
        return jinggai;
    }

    public void setJinggai(SensorStruct jinggai) {
        this.jinggai = jinggai;
    }

    public SensorStruct getDrop() {
        return drop;
    }

    public void setDrop(SensorStruct drop) {
        this.drop = drop;
    }

    public SensorStruct getCover() {
        return cover;
    }

    public void setCover(SensorStruct cover) {
        this.cover = cover;
    }

    public SensorStruct getAlarm() {
        return alarm;
    }

    public void setAlarm(SensorStruct alarm) {
        this.alarm = alarm;
    }

    public SensorStruct getCo2() {
        return co2;
    }

    public void setCo2(SensorStruct co2) {
        this.co2 = co2;
    }

    public SensorStruct getCo() {
        return co;
    }

    public void setCo(SensorStruct co) {
        this.co = co;
    }

    public SensorStruct getCh4() {
        return ch4;
    }

    public void setCh4(SensorStruct ch4) {
        this.ch4 = ch4;
    }

    public SensorStruct getAngle() {
        return angle;
    }

    public void setAngle(SensorStruct angle) {
        this.angle = angle;
    }

    public SensorStruct getFlame() {
        return flame;
    }

    public SensorStruct getDistance() {
        return distance;
    }

    public SensorDetailInfo setDistance(SensorStruct distance) {
        this.distance = distance;
        return this;
    }

    public SensorDetailInfo setFlame(SensorStruct flame) {
        this.flame = flame;
        return this;
    }

    public SensorStruct getCollision() {
        return collision;
    }

    public void setCollision(SensorStruct collision) {
        this.collision = collision;
    }

    public SensorStruct getLatitude() {
        return latitude;
    }

    public void setLatitude(SensorStruct latitude) {
        this.latitude = latitude;
    }

    public SensorStruct getLongitude() {
        return longitude;
    }

    public void setLongitude(SensorStruct longitude) {
        this.longitude = longitude;
    }

    public SensorStruct getAltitude() {
        return altitude;
    }

    public void setAltitude(SensorStruct altitude) {
        this.altitude = altitude;
    }

    public SensorStruct getArtificialGas() {
        return artificialGas;
    }

    public void setArtificialGas(SensorStruct artificialGas) {
        this.artificialGas = artificialGas;
    }

    public SensorStruct getWaterPressure() {
        return waterPressure;
    }

    public void setWaterPressure(SensorStruct waterPressure) {
        this.waterPressure = waterPressure;
    }

    public SensorStruct getMagnetic() {
        return magnetic;
    }

    public void setMagnetic(SensorStruct magnetic) {
        this.magnetic = magnetic;
    }

    public SensorStruct getInstalled() {
        return installed;
    }

    public void setInstalled(SensorStruct installed) {
        this.installed = installed;
    }
}
