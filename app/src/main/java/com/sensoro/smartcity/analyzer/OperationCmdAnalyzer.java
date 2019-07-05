package com.sensoro.smartcity.analyzer;

import com.sensoro.libbleserver.ble.callback.SensoroWriteCallback;
import com.sensoro.libbleserver.ble.connection.SensoroDeviceConnection;
import com.sensoro.libbleserver.ble.proto.MsgNode1V1M5;
import com.sensoro.smartcity.constant.MonitorPointOperationCode;

import java.util.Random;


/**
 * 执行命令操作
 */
public class OperationCmdAnalyzer {

    public static void doOperation(String deviceType, String mOperationType, Integer beepMuteTime, SensoroDeviceConnection sensoroDeviceConnection, SensoroWriteCallback callback) {

        switch (deviceType) {
            case "smoke":
                doSmoke(mOperationType, sensoroDeviceConnection, callback);
                break;
            case "fhsj_smoke":
            case "n16w_smoke":
//            case "lite_smoke":
                doFhsjSmoke(mOperationType, sensoroDeviceConnection, callback);
                break;
            case "cayman_smoke":
                //自研烟感
                doCaymanSmoke(mOperationType, sensoroDeviceConnection, callback);
                break;
            case "fhsj_elec_fires":
                doFhsjElecFires(mOperationType, sensoroDeviceConnection, callback);
                break;
            case "acrel_fires":
            case "acrel_alpha":
            case "acrel_single":
                doAcrel(mOperationType, sensoroDeviceConnection, callback);
                break;
            case "mantun_fires":
                doMantunFires(mOperationType, sensoroDeviceConnection, callback);
                break;
            case "baymax_lpg":
            case "baymax_ch4":
                doBaymax(mOperationType, sensoroDeviceConnection, callback);
                break;
            case "lite_smoke":
                doLiteSmoke(mOperationType, beepMuteTime,sensoroDeviceConnection, callback);
                break;

        }


    }

    private static void doLiteSmoke(String mOperationType, Integer beepMuteTime, SensoroDeviceConnection sensoroDeviceConnection, SensoroWriteCallback callback) {
        MsgNode1V1M5.Cayman.Builder builder = MsgNode1V1M5.Cayman.newBuilder();
        switch (mOperationType) {
            case MonitorPointOperationCode.ERASURE_STR:
                builder.setCmd(4);
                break;
            case MonitorPointOperationCode.RESET_STR:
                builder.setCmd(2);
                break;
            case MonitorPointOperationCode.SELF_CHECK_STR:
                builder.setCmd(1);
                break;
            case MonitorPointOperationCode.ERASURE_TIME_STR:
                sensoroDeviceConnection.writeAppBeepMuteTime(beepMuteTime,callback);
                return;
            default:
                callback.onWriteFailure(0, 0);
                return;
        }
        sensoroDeviceConnection.writeCaymanCmd(builder, callback);


    }

    private static void doBaymax(String mOperationType, SensoroDeviceConnection sensoroDeviceConnection, SensoroWriteCallback callback) {
        MsgNode1V1M5.Baymax.Builder builder = MsgNode1V1M5.Baymax.newBuilder();
        switch (mOperationType) {
            case MonitorPointOperationCode.ERASURE_STR:
                builder.setGasDeviceCMD(0b100);
                break;
            case MonitorPointOperationCode.ERASURE_LONG_STR:
                builder.setGasDeviceCMD(3);
                break;
            case MonitorPointOperationCode.SELF_CHECK_STR:
                builder.setGasDeviceCMD(0b01);
                break;
            default:
                callback.onWriteFailure(0, 0);
                return;
        }
        sensoroDeviceConnection.writeBaymaxCmd(builder, callback);
    }


    private static void doMantunFires(String mOperationType, SensoroDeviceConnection sensoroDeviceConnection, SensoroWriteCallback callback) {
        MsgNode1V1M5.MantunData.Builder builder = MsgNode1V1M5.MantunData.newBuilder();
        builder.setId(0);
        switch (mOperationType) {
            case MonitorPointOperationCode.ERASURE_STR:
                builder.setCmd(8);
                break;
            case MonitorPointOperationCode.AIR_SWITCH_POWER_ON_STR:
                builder.setCmd(2);
                break;
            case MonitorPointOperationCode.QUERY_STR:
                builder.setCmd(0);
                break;
            case MonitorPointOperationCode.AIR_SWITCH_POWER_OFF_STR:
                builder.setCmd(4);
                break;
            default:
                callback.onWriteFailure(0, 0);
                return;
        }
        sensoroDeviceConnection.writeMantunCmd(builder, callback);
    }

    private static void doAcrel(String mOperationType, SensoroDeviceConnection sensoroDeviceConnection, SensoroWriteCallback callback) {
        MsgNode1V1M5.AcrelData.Builder builder = MsgNode1V1M5.AcrelData.newBuilder();
        switch (mOperationType) {
            case MonitorPointOperationCode.ERASURE_STR:
                builder.setCmd(4);
                break;
            case MonitorPointOperationCode.RESET_STR:
                builder.setCmd(1);
                break;
            case MonitorPointOperationCode.SELF_CHECK_STR:
                builder.setCmd(2);
                break;
            case MonitorPointOperationCode.QUERY_STR:
                builder.setCmd(0);
                break;
            case MonitorPointOperationCode.PSD_STR:
                Random random = new Random();
                builder.setPasswd(random.nextInt(9999) + 1);
                break;
            default:
                callback.onWriteFailure(0, 0);
                return;
        }
        sensoroDeviceConnection.writeAcrelCmd(builder, callback);
    }

    private static void doFhsjElecFires(String mOperationType, SensoroDeviceConnection sensoroDeviceConnection, SensoroWriteCallback callback) {
        MsgNode1V1M5.ElecFireData.Builder builder = MsgNode1V1M5.ElecFireData.newBuilder();
        switch (mOperationType) {
            case MonitorPointOperationCode.ERASURE_STR:
                builder.setCmd(16);
                break;
            case MonitorPointOperationCode.RESET_STR:
                builder.setCmd(1);
                break;
            case MonitorPointOperationCode.SELF_CHECK_STR:
                builder.setCmd(8);
                break;
            case MonitorPointOperationCode.QUERY_STR:
                builder.setCmd(0);
                break;
            case MonitorPointOperationCode.PSD_STR:
                Random random = new Random();
                builder.setSensorPwd(random.nextInt(9999) + 1);
                break;
            default:
                callback.onWriteFailure(0, 0);
                return;
        }
        sensoroDeviceConnection.writeElecCmd(builder, callback);
    }

    private static void doCaymanSmoke(String mOperationType, SensoroDeviceConnection sensoroDeviceConnection, SensoroWriteCallback callback) {
        MsgNode1V1M5.Cayman.Builder builder = MsgNode1V1M5.Cayman.newBuilder();
        switch (mOperationType) {
            case MonitorPointOperationCode.ERASURE_STR:
                builder.setCmd(4);
                break;
            case MonitorPointOperationCode.RESET_STR:
                builder.setCmd(2);
                break;
            case MonitorPointOperationCode.SELF_CHECK_STR:
                builder.setCmd(1);
                break;
            default:
                callback.onWriteFailure(0, 0);
                return;
        }
        sensoroDeviceConnection.writeCaymanCmd(builder, callback);
    }

    private static void doFhsjSmoke(String mOperationType, SensoroDeviceConnection sensoroDeviceConnection, SensoroWriteCallback callback) {
        MsgNode1V1M5.AppParam.Builder builder = MsgNode1V1M5.AppParam.newBuilder();
        switch (mOperationType) {
            case MonitorPointOperationCode.ERASURE_STR:
                builder.setSmokeCtrl(MsgNode1V1M5.SmokeCtrl.SMOKE_ERASURE);
                break;
            case MonitorPointOperationCode.ERASURE_LONG_STR:
                builder.setSmokeCtrl(MsgNode1V1M5.SmokeCtrl.SMOKE_ERASURE_LONE);
                break;
            default:
                callback.onWriteFailure(0, 0);
                return;
        }
        sensoroDeviceConnection.writeSmokeCmd(builder, callback);
    }

    private static void doSmoke(String mOperationType, SensoroDeviceConnection sensoroDeviceConnection, SensoroWriteCallback callback) {
        MsgNode1V1M5.AppParam.Builder builder = MsgNode1V1M5.AppParam.newBuilder();
        switch (mOperationType) {
            case MonitorPointOperationCode.ERASURE_STR:
                builder.setSmokeCtrl(MsgNode1V1M5.SmokeCtrl.SMOKE_ERASURE);
                break;
            case MonitorPointOperationCode.ERASURE_LONG_STR:
                builder.setSmokeCtrl(MsgNode1V1M5.SmokeCtrl.SMOKE_ERASURE_LONE);
                break;
            default:
                callback.onWriteFailure(0, 0);
                return;
        }
        sensoroDeviceConnection.writeSmokeCmd(builder, callback);
    }

}
