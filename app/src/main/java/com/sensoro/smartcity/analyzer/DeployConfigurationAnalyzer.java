package com.sensoro.smartcity.analyzer;

import android.text.TextUtils;
import android.widget.Toast;

import com.sensoro.libbleserver.ble.entity.SensoroDevice;
import com.sensoro.libbleserver.ble.entity.SensoroMantunData;
import com.sensoro.libbleserver.ble.entity.SensoroSensor;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.toast.SensoroToast;

import java.util.Random;

public class DeployConfigurationAnalyzer {
    private DeployConfigurationAnalyzer() {
    }

    /**
     * 获取配置的最大值，最小值;空开配置，不同的电气火灾，这个值应该是不一样的，但是默认给50-560
     */
    public static int[] analyzeDeviceType(String deviceType) {
        int[] result = new int[2];
        result[0] = 50;
        result[1] = 560;
        if (TextUtils.isEmpty(deviceType)) {
            return result;
        }
        switch (deviceType) {
            case "fhsj_elec_fires":
                //泛海三江电气火灾，没有配置
                result[0] = 0;
                result[1] = 50;
                break;
            case "acrel_fires":
                //安科瑞三相电，跟默认值一样
                break;
            case "acrel_single":
                //安科瑞单相电
                result[0] = 0;
                result[1] = 84;
                break;
            case "mantun_fires":
                //慢炖空开
                result[0] = 0;
                result[1] = 80;
                break;
        }
        return result;

    }

    public static SensoroDevice configurationData(String deviceType, SensoroDevice sensoroDevice, int enterValue) {
        if (TextUtils.isEmpty(deviceType)) {
            return null;
        }
        switch (deviceType) {
            case "fhsj_elec_fires":
                //泛海三江电气火灾，没有配置
                configFhsjElectFires(sensoroDevice.getSensoroSensorTest(), enterValue);
                break;
            case "acrel_fires":
                //安科瑞三相电
                configAcrelFires(sensoroDevice.getSensoroSensorTest(), enterValue);
                break;
            case "acrel_single":
                //安科瑞单相电
                configAcrelSingle(sensoroDevice.getSensoroSensorTest(), enterValue);
                break;
            case "mantun_fires":
                //慢炖空开
                configMantunFires(sensoroDevice.getSensoroSensorTest(), enterValue);
                break;
            default:
                sensoroDevice = null;
                break;
        }
        return sensoroDevice;
    }

    private static void configAcrelFires(SensoroSensor sensoroSensor, int value) {
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
        sensoroSensor.acrelFires.t4Th = 60;//箱体温度
        sensoroSensor.acrelFires.valHighSet = 1200;
        sensoroSensor.acrelFires.valLowSet = 800;
        sensoroSensor.acrelFires.currHighSet = 1000 * value / dev;
        sensoroSensor.acrelFires.passwd = new Random().nextInt(9999) + 1;// 1-9999 4位随机数
        sensoroSensor.acrelFires.currHighType = 1;//打开保护，不关联脱扣
        sensoroSensor.acrelFires.valLowType = 0;//关闭保护，不关联脱扣
        sensoroSensor.acrelFires.valHighType = 1;//打开保护，不关联脱扣
        sensoroSensor.acrelFires.chEnable = 0x1E;//打开温度，关闭漏电保护
        sensoroSensor.acrelFires.connectSw = 0;//关联脱扣器全部关闭
        sensoroSensor.acrelFires.ict = 2000;//漏电互感器变比 2000
        sensoroSensor.acrelFires.ct = dev / 5;
        sensoroSensor.acrelFires.cmd = 2;
    }

    private static void configFhsjElectFires(SensoroSensor sensoroSensor, int value) {
        //在开始配置的时候，已经校验过，mEnterValue的值是50 到560
        sensoroSensor.elecFireData.leakageTh = 300; //漏电(通道 1)
        sensoroSensor.elecFireData.tempTh = 80; //电线温度，通道2
        sensoroSensor.elecFireData.currentTh = value; //过流
        sensoroSensor.elecFireData.loadTh = (int) (value * 220 / 1000 + 0.5f); //过载
        try {
            SensoroToast.INSTANCE.makeText("value = " + sensoroSensor.elecFireData.loadTh, Toast.LENGTH_SHORT).show();
            LogUtils.loge("sensoroSensor.elecFireData.loadTh = " + sensoroSensor.elecFireData.loadTh);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        sensoroSensor.elecFireData.volHighTh = 253; //过压 V
        sensoroSensor.elecFireData.volLowTh = 187; //欠压 V
        sensoroSensor.elecFireData.sensorPwd = new Random().nextInt(9999) + 1;// 1-9999 4位随机数
        sensoroSensor.elecFireData.cmd = 8; //自检命令
        //
//        sensoroSensor.elecFireData.leakageTh = 300;//漏电
//        sensoroSensor.elecFireData.t1Th = 80;//A项线温度
//        sensoroSensor.elecFireData.t2Th = 80;//B项线温度
//        sensoroSensor.elecFireData.t3Th = 80;//C项线温度
//        sensoroSensor.elecFireData.t4Th = 80;//箱体温度
//        sensoroSensor.elecFireData.valHighSet = 1200;
//        sensoroSensor.elecFireData.valLowSet = 800;
//        sensoroSensor.elecFireData.currHighSet = 1000 * value / dev;
//        sensoroSensor.elecFireData.passwd = new Random().nextInt(9999) + 1;// 1-9999 4位随机数
//        sensoroSensor.elecFireData.currHighType = 1;//打开保护，不关联脱扣
//        sensoroSensor.elecFireData.valLowType = 0;//关闭保护，不关联脱扣
//        sensoroSensor.elecFireData.valHighType = 1;//打开保护，不关联脱扣
//        sensoroSensor.elecFireData.chEnable = 0x1F;//打开温度，打开漏电保护
//        sensoroSensor.elecFireData.connectSw = 0;//关联脱扣器全部关闭
//        sensoroSensor.elecFireData.ict = 2000;//漏电互感器变比 2000
//        sensoroSensor.elecFireData.ct = dev / 5;
//        sensoroSensor.elecFireData.cmd = 2;
    }

    private static void configAcrelSingle(SensoroSensor sensoroSensor, int value) {
        sensoroSensor.acrelFires.leakageTh = 300;//漏电
        sensoroSensor.acrelFires.t1Th = 80;//电线温度 通道1
        sensoroSensor.acrelFires.t2Th = 60;//箱体温度 通道2
        sensoroSensor.acrelFires.valHighSet = 1150;//过压
        sensoroSensor.acrelFires.currHighSet = value * 100 * 10 / 60; //过流 10 ： 放大比，100 ： 转换为百分数
        sensoroSensor.acrelFires.valLowSet = 850;//欠压
        sensoroSensor.acrelFires.passwd = new Random().nextInt(9999) + 1;// 1-9999 4位随机数
        sensoroSensor.acrelFires.ict = 2000;//漏电互感器变比 2000
        sensoroSensor.acrelFires.ct = 1;
        sensoroSensor.acrelFires.cmd = 2;
        sensoroSensor.acrelFires.currHighType = 1;
        sensoroSensor.acrelFires.valLowType = 1; //
        sensoroSensor.acrelFires.valHighType = 1; //
        sensoroSensor.acrelFires.chEnable = 0x07; // 打开温度，打开漏电保护
        //
//        param.leakageTh = 300; //漏电
//        param.t1Th = 80; //电线温度，通道1
//        param.t2Th = 60; //箱体温度，通道2
//        param.currHighSet = initialValue * 100 * 10 / 60; //过流 10 ： 放大比，100 ： 转换为百分数
//        param.valHighSet = 1150; //过压
//        param.valLowSet = 850; //欠压
//        param.currHighType = 1;//打开过流开关
//        param.valLowType = 1; //
//        param.valHighType = 1; //
//        param.chEnable = 0x07 // 打开温度，打开漏电保护
//        param.passwd = UInt32.random(in:1.. .9999); //生成4位随机数
//        param.ict = 2000; //漏电互感器变比 2000
//        param.ct = 1; //
//        param.cmd = 2; //自检命令
    }

    private static void configMantunFires(SensoroSensor sensoroSensor, int value) {
        if (sensoroSensor.mantunDatas != null && sensoroSensor.mantunDatas.size() > 0) {
            for (SensoroMantunData mantunData : sensoroSensor.mantunDatas) {
                mantunData.id = 0; //现阶段只有一组，所以id为0，如果多组，则依次赋值
                mantunData.currentTh = value; //过流
                mantunData.powerTh = value * 220; //过载
            }
        }
    }
}
