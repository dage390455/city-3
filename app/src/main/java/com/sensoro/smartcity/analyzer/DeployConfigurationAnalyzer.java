package com.sensoro.smartcity.analyzer;

import com.sensoro.libbleserver.ble.SensoroDevice;
import com.sensoro.libbleserver.ble.SensoroSensor;
import com.sensoro.smartcity.constant.Constants;

import java.util.Random;

public class DeployConfigurationAnalyzer {

    private int index = -1;

    /**
     * 获取配置的最大值，最小值
     */
    public int[] analyzeDeviceType(String deviceType) {
        for (int i = 0; i < Constants.DEVICE_CONTROL_DEVICE_TYPES.size(); i++) {
            if (Constants.DEVICE_CONTROL_DEVICE_TYPES.get(i).equals(deviceType)) {
                index = i;
                break;
            }
        }
        if (index != -1) {
            int[] result = new int[2];
            switch (index) {
                case 0:
                    //泛海三江电气火灾，没有配置
                    return null;
                case 1:
                    //安科瑞三相电
                    result[0] = 50;
                    result[1] = 560;
                    return result;

                case 2:
                    //安科瑞单相电
                    result[0] = 0;
                    result[1] = 84;
                    return result;
            }
        }
        return null;

    }

    public SensoroDevice configurationData(SensoroDevice sensoroDevice,int enterValue) {
        if (index == -1) {
            return null;
        }
        switch (index) {
            case 0:
                //泛海三江电气火灾，没有配置
                sensoroDevice = null;
                break;
            case 1:
                //安科瑞三相电
                configAcrelFires(sensoroDevice.getSensoroSensorTest(),enterValue);
                break;
            case 2:
                //安科瑞单相电
                configAcrelSingle(sensoroDevice.getSensoroSensorTest(),enterValue);
                break;
                default:
                    sensoroDevice = null;
                    break;
        }
        return sensoroDevice;
    }

    private void configAcrelFires(SensoroSensor sensoroSensor,int value) {
        //在开始配置的时候，已经校验过，mEnterValue的值是50 到560
        int dev;
        if (value <= 250) {
            dev = 250;
        } else {
            dev = 400;
        }
        sensoroSensor.acrelFires.leakageTh = 1000;//漏电
        sensoroSensor.acrelFires.t1Th = 80;//A项线温度
        sensoroSensor.acrelFires.t2Th = 80;//B项线温度
        sensoroSensor.acrelFires.t3Th = 80;//C项线温度
        sensoroSensor.acrelFires.t4Th = 80;//箱体温度
        sensoroSensor.acrelFires.valHighSet = 1200;
        sensoroSensor.acrelFires.valLowSet = 800;
        sensoroSensor.acrelFires.currHighSet = 1000 * value / dev;
        sensoroSensor.acrelFires.passwd = new Random().nextInt(9999) + 1;// 1-9999 4位随机数
        sensoroSensor.acrelFires.currHighType = 1;//打开保护，不关联脱扣
        sensoroSensor.acrelFires.valLowType = 0;//关闭保护，不关联脱扣
        sensoroSensor.acrelFires.valHighType = 1;//打开保护，不关联脱扣
        sensoroSensor.acrelFires.chEnable = 0x1F;//打开温度，打开漏电保护
        sensoroSensor.acrelFires.connectSw = 0;//关联脱扣器全部关闭
        sensoroSensor.acrelFires.ict = 2000;//漏电互感器变比 2000
        sensoroSensor.acrelFires.ct = dev / 5;
        sensoroSensor.acrelFires.cmd = 2;
    }

    private void configAcrelSingle(SensoroSensor sensoroSensor,int value) {
        sensoroSensor.acrelFires.leakageTh = 300;//漏电
        sensoroSensor.acrelFires.t1Th = 80;//电线温度 通道1
        sensoroSensor.acrelFires.t2Th = 60;//箱体温度 通道2
        sensoroSensor.acrelFires.valHighSet = 115;//过压
        sensoroSensor.acrelFires.currHighSet = 5 * value / 60;//过流
        sensoroSensor.acrelFires.valLowSet = 85;//欠压
        sensoroSensor.acrelFires.passwd = new Random().nextInt(9999) + 1;// 1-9999 4位随机数
        sensoroSensor.acrelFires.ict = 2000;//漏电互感器变比 2000
        sensoroSensor.acrelFires.ct = 1;
        sensoroSensor.acrelFires.cmd = 2;
    }
}
