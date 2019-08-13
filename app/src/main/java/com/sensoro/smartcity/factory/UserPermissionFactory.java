package com.sensoro.smartcity.factory;

import android.text.TextUtils;

import com.sensoro.common.model.EventLoginData;
import com.sensoro.common.server.bean.GrantsInfo;
import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.common.utils.LogUtils;

import java.util.List;

public class UserPermissionFactory {
    public static EventLoginData createLoginData(UserInfo userInfo, String phoneId) {
        final EventLoginData eventLoginData = new EventLoginData();
        GrantsInfo grants = userInfo.getGrants();
        //
        eventLoginData.userId = userInfo.get_id();
        eventLoginData.userName = userInfo.getNickname();
        eventLoginData.phoneId = phoneId;
        try {
            LogUtils.loge("logPresenter", "phoneId = " + phoneId);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //TODO 处理Character信息
//                      mCharacter = userInfo.getCharacter();
        boolean addUserEnable = userInfo.isAddUserEnable();
        eventLoginData.roles = userInfo.getRoles();
        String isSpecific = userInfo.getIsSpecific();
        eventLoginData.isSupperAccount = getIsSupperAccount(isSpecific);
        eventLoginData.hasStationDeploy = getHasStationDeploy(grants);
        eventLoginData.hasStationList = getHasStationList(grants);
        eventLoginData.hasContract = getHasContract(grants);
        eventLoginData.hasContractCreate = getHasContractCreate(grants);
        eventLoginData.hasContractModify = getHasContractModify(grants);
        eventLoginData.hasScanLogin = getHasScanLogin(grants);
        eventLoginData.hasSubMerchant = getHasSubMerchant(grants) && addUserEnable;
        eventLoginData.hasMerchantChange = getHasMerchantChange(grants);
        eventLoginData.hasInspectionTaskList = getHasInspectionTaskList(grants);
        eventLoginData.hasInspectionTaskModify = getHasInspectionTaskModify(grants);
        eventLoginData.hasInspectionDeviceList = getHasInspectionDeviceList(grants);
        eventLoginData.hasInspectionDeviceModify = getHasInspectionDeviceModify(grants);
        eventLoginData.hasAlarmInfo = getHasAlarmInfo(grants);
        eventLoginData.hasMalfunction = getHasMalfunction(grants);
        eventLoginData.hasDeviceBrief = getHasDeviceBriefList(grants);
        eventLoginData.hasSignalCheck = getHasSignalCheck(grants);
        //TODO 统一去掉信号配置
//        eventLoginData.hasSignalConfig = getHasSignalConfig(grants);
        eventLoginData.hasSignalConfig = false;
        eventLoginData.hasForceUpload = getHasBadSignalUpload(grants);
        eventLoginData.hasDevicePositionCalibration = getHasDevicePositionCalibration(grants);
        //设备控制
        eventLoginData.hasDeviceMuteShort = getHasMuteShort(grants);
        eventLoginData.hasDeviceMuteLong = getHasMuteLong(grants);
        eventLoginData.hasDeviceMuteTime = getHasMuteTime(grants);
        eventLoginData.hasDeviceControlCheck = getHasControlCheck(grants);
        eventLoginData.hasDeviceControlReset = getHasControlReset(grants);
        eventLoginData.hasDeviceControlPassword = getHasControlPassword(grants);
        eventLoginData.hasDeviceControlView = getHasControlView(grants);
        eventLoginData.hasDeviceControlConfig = getHasControlConfig(grants);
        eventLoginData.hasDeviceControlOpen = getHasControlOpen(grants);
        eventLoginData.hasDeviceControlClose = getHasControlClose(grants);
        //
        eventLoginData.hasDeviceFirmwareUpdate = getHasDeviceFirmUpdate(grants);
        eventLoginData.hasDeviceDemoMode = getHasDeviceDemoMode(grants);
        eventLoginData.hasDeviceCameraList = getHasDeviceCameraList(grants);
        eventLoginData.hasNameplateList = getHasNameplateList(grants);
        eventLoginData.hasNameplateDeploy = getHasNameplateDeploy(grants);
        eventLoginData.hasDeviceCameraDeploy = getHasDeviceCameraDeploy(grants);
        eventLoginData.hasIBeaconSearchDemo = getHasIBeaconSearchDemo(grants);
        eventLoginData.hasMonitorTaskList = getHasMonitorTaskList(grants);
        eventLoginData.hasMonitorTaskConfirm = getHasMonitorTaskConfirm(grants);
        String controllerAid = userInfo.getControllerAid();
        //通过controllerAid来判断是否可以返回主账户
        eventLoginData.hasControllerAid = !TextUtils.isEmpty(controllerAid);
        try {
            LogUtils.loge("logPresenter", "eventLoginData = " + eventLoginData.toString());
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        //
        UserInfo.Account account = userInfo.getAccount();
        if (account != null) {
            String contacts = account.getContacts();
            String id = account.getId();
            boolean totpEnable = account.isTotpEnable();
            try {
                LogUtils.loge("login--->>> id = " + id + ",totpEnable = " + totpEnable + ",phone = " + contacts);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            eventLoginData.phone = contacts;
            eventLoginData.accountId = account.get_id();

            if (totpEnable) {
                eventLoginData.needAuth = true;
            }
        }
        return eventLoginData;
    }

    /**
     * 判断基站部署权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasStationDeploy(GrantsInfo grants) {
        if (grants != null) {
            List<String> station = grants.getStation();
            if (station != null) {
                return station.contains("deploy");
            }

        }
        return false;
    }

    /**
     * 基站列表权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasStationList(GrantsInfo grants) {
        if (grants != null) {
            List<String> station = grants.getStation();
            if (station != null) {
                return station.contains("list");
            }

        }
        return false;
    }

    /**
     * 判断是否超级账户
     *
     * @param isSupperAccountStr
     * @return
     */
    private static boolean getIsSupperAccount(String isSupperAccountStr) {
        return !TextUtils.isEmpty(isSupperAccountStr) && "true".equalsIgnoreCase(isSupperAccountStr);
    }

    /**
     * 判断合同查看权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasContract(GrantsInfo grants) {
        if (grants != null) {
            List<String> contract = grants.getContract();
            if (contract != null) {
                return contract.contains("list");
            }
        }
        return false;
    }

    /**
     * 判断合同创建权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasContractCreate(GrantsInfo grants) {
        if (grants != null) {
            List<String> contract = grants.getContract();
            if (contract != null) {
                return contract.contains("create");
            }
        }
        return false;
    }

    /**
     * 判断合同修改权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasContractModify(GrantsInfo grants) {
        if (grants != null) {
            List<String> contract = grants.getContract();
            if (contract != null) {
                return contract.contains("modify");
            }
        }
        return false;
    }

    /**
     * 判断扫码登录权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasScanLogin(GrantsInfo grants) {
        if (grants != null) {
            List<String> tv = grants.getTv();
            if (tv != null) {
                return tv.contains("qrcode");
            }
        }
        return false;
    }

    /**
     * 判断是否有子账户查看权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasSubMerchant(GrantsInfo grants) {
        if (grants != null) {
            List<String> user = grants.getUser();
            if (user != null) {
                return user.contains("list");
            }
        }
        return false;
    }

    /**
     * 判断是否有子账户切换权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasMerchantChange(GrantsInfo grants) {
        if (grants != null) {
            List<String> user = grants.getUser();
            if (user != null) {
                return user.contains("control");
            }
        }
        return false;
    }

    /**
     * 判断巡检任务列表权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasInspectionTaskList(GrantsInfo grants) {
        if (grants != null) {
            List<String> inspectTask = grants.getInspectTask();
            if (inspectTask != null) {
                return inspectTask.contains("list");
            }
        }
        return false;
    }

    /**
     * 是否有修改任务权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasInspectionTaskModify(GrantsInfo grants) {
        if (grants != null) {
            List<String> inspectTask = grants.getInspectTask();
            if (inspectTask != null) {
                return inspectTask.contains("modifyStatus");
            }
        }
        return false;
    }

    /**
     * 是否有设备任务列表权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasInspectionDeviceList(GrantsInfo grants) {
        if (grants != null) {
            List<String> inspectDevice = grants.getInspectDevice();
            if (inspectDevice != null) {
                return inspectDevice.contains("list");
            }
        }
        return false;
    }

    /**
     * 是否有设备巡检任务修改权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasInspectionDeviceModify(GrantsInfo grants) {
        if (grants != null) {
            List<String> inspectDevice = grants.getInspectDevice();
            if (inspectDevice != null) {
                return inspectDevice.contains("modify");
            }
        }
        return false;
    }


    /**
     * 判断是否有预警权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasAlarmInfo(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsAlarm = grants.getAlarm();
            if (grantsAlarm != null) {
                return grantsAlarm.contains("list");
            }
        }
        return false;
    }

    /**
     * 权限 briefList statusStatistics list 权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasDeviceBriefList(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsDevice = grants.getDevice();
            if (grantsDevice != null) {
                return grantsDevice.contains("briefList") && grantsDevice.contains("statusStatistics") && grantsDevice.contains("list");
            }
        }
        return false;
    }

    /**
     * 设备位置校准
     *
     * @param grants
     * @return
     */
    private static boolean getHasDevicePositionCalibration(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsDevice = grants.getDevice();
            if (grantsDevice != null) {
                return grantsDevice.contains("modify");
            }
        }
        return false;
    }

    /**
     * 信号测试权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasSignalCheck(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsDevice = grants.getDevice();
            if (grantsDevice != null) {
                return grantsDevice.contains("signalCheck");
            }
        }
        return false;
    }

    /**
     * 信号配置权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasSignalConfig(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsDevice = grants.getDevice();
            if (grantsDevice != null) {
                return grantsDevice.contains("signalConfig");
            }
        }
        return false;
    }

    /**
     * 故障权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasMalfunction(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsMalfunction = grants.getMalfunction();
            if (grantsMalfunction != null) {
                return grantsMalfunction.contains("list");
            }
        }
        return false;
    }

    /**
     * 是否强制上传
     *
     * @param grants
     * @return
     */
    private static boolean getHasBadSignalUpload(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsDeploy = grants.getDeploy();
            if (grantsDeploy != null) {
                return grantsDeploy.contains("_badSignalUpload");
            }
        }
        return false;
    }

    /**
     * 是否短消音权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasMuteShort(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsControl = grants.getControl();
            if (grantsControl != null) {
                return grantsControl.contains("mute");
            }
        }
        return false;
    }

    /**
     * 是否长消音权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasMuteLong(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsControl = grants.getControl();
            if (grantsControl != null) {
                return grantsControl.contains("mute2");
            }
        }
        return false;
    }

    /**
     * 是否有定时消音权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasMuteTime(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsControl = grants.getControl();
            if (grantsControl != null) {
                return grantsControl.contains("mute_time");
            }
        }
        return false;
    }

    /**
     * 是否有重置权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasControlReset(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsControl = grants.getControl();
            if (grantsControl != null) {
                return grantsControl.contains("reset");
            }
        }
        return false;
    }

    /**
     * 控制密码修改
     *
     * @param grants
     * @return
     */
    private static boolean getHasControlPassword(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsControl = grants.getControl();
            if (grantsControl != null) {
                return grantsControl.contains("password");
            }
        }
        return false;
    }

    /**
     * 查询
     *
     * @param grants
     * @return
     */
    private static boolean getHasControlView(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsControl = grants.getControl();
            if (grantsControl != null) {
                return grantsControl.contains("view");
            }
        }
        return false;
    }

    /**
     * 自检
     *
     * @param grants
     * @return
     */
    private static boolean getHasControlCheck(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsControl = grants.getControl();
            if (grantsControl != null) {
                return grantsControl.contains("check");
            }
        }
        return false;
    }

    /**
     * 断电
     *
     * @param grants
     * @return
     */
    private static boolean getHasControlOpen(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsControl = grants.getControl();
            if (grantsControl != null) {
                return grantsControl.contains("open");
            }
        }
        return false;
    }

    /**
     * 上电
     *
     * @param grants
     * @return
     */
    private static boolean getHasControlClose(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsControl = grants.getControl();
            if (grantsControl != null) {
                return grantsControl.contains("close");
            }
        }
        return false;
    }

    /**
     * 配置
     *
     * @param grants
     * @return
     */
    private static boolean getHasControlConfig(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsControl = grants.getControl();
            if (grantsControl != null) {
                return grantsControl.contains("config");
            }
        }
        return false;
    }

    /**
     * 是否有设备升级权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasDeviceFirmUpdate(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsDevice = grants.getDevice();
            if (grantsDevice != null) {
                return grantsDevice.contains("_updateFirmware");
            }
        }
        return false;
    }

    /**
     * 用户是否有demo演示权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasDeviceDemoMode(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsDevice = grants.getDevice();
            if (grantsDevice != null) {
                return grantsDevice.contains("demo");
            }
        }
        return false;
    }

    /**
     * 用户是否有摄像头list权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasDeviceCameraList(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsCamera = grants.getCamera();
            if (grantsCamera != null) {
                return grantsCamera.contains("list");
            }
        }
        return false;
    }

    /**
     * 摄像头部署
     *
     * @param grants
     * @return
     */
    private static boolean getHasDeviceCameraDeploy(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsCamera = grants.getCamera();
            if (grantsCamera != null) {
                return grantsCamera.contains("deploy");
            }
        }
        return false;
    }

    /**
     * 铭牌查看
     *
     * @param grants
     * @return
     */
    private static boolean getHasNameplateList(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsNameplate = grants.getNameplate();
            if (grantsNameplate != null) {
                return grantsNameplate.contains("list");
            }
        }
        return false;
    }

    /**
     * 检查是否有铭牌部署权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasNameplateDeploy(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsNameplate = grants.getNameplate();
            if (grantsNameplate != null) {
                return grantsNameplate.contains("deploy");
            }
        }
        return false;
    }

    /**
     * 是否有ibeacon附近扫描权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasIBeaconSearchDemo(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsDevice = grants.getDevice();
            if (grantsDevice != null) {
                return grantsDevice.contains("_iBeaconSearchDemo");
            }
        }
        return false;
    }

    /**
     * 检查是否有安防布控查看权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasMonitorTaskList(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsMonitorTask = grants.getMonitorTask();
            if (grantsMonitorTask != null) {
                return grantsMonitorTask.contains("list");
            }
        }
        return false;
    }

    /**
     * 检查是否有安防布控预警确认权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasMonitorTaskConfirm(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsMonitorTask = grants.getMonitorTask();
            if (grantsMonitorTask != null) {
                return grantsMonitorTask.contains("confirm");
            }
        }
        return false;
    }


}
