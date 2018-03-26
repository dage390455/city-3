package com.sensoro.smartcity.push;

/**
 * Created by sensoro on 17/2/24.
 */

public class SensoroPushManager {

    private SensoroPushListener sensoroPushListener;

    private static volatile SensoroPushManager instance;

    private SensoroPushManager() {

    }

    public static SensoroPushManager getInstance() {
        if (instance == null) {
            synchronized (SensoroPushManager.class) {
                if (instance == null) {
                    instance = new SensoroPushManager();
                }
            }
        }
        return instance;
    }

    public void registerSensoroPushListener(SensoroPushListener listener) {
        this.sensoroPushListener = listener;
    }

    public SensoroPushListener getSensoroPushListener() {
        return sensoroPushListener;
    }
}
