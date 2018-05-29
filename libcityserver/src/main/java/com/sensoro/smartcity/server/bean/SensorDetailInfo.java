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
    private SensorStruct temp1;


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
        if (temp1 != null) {
            mMap.put("temp1", temp1);
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
}
