package com.sensoro.smartcity.factory;

import android.text.TextUtils;

import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.server.bean.GrantsInfo;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.util.LogUtils;

import java.util.List;

public class UserPermissionFactory {
    public static EventLoginData createLoginData(UserInfo userInfo, String phoneId) {
        final EventLoginData eventLoginData = new EventLoginData();
        GrantsInfo grants = userInfo.getGrants();
        //
        eventLoginData.userId = userInfo.get_id();
        eventLoginData.userName = userInfo.getNickname();
        eventLoginData.phone = userInfo.getContacts();
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
        eventLoginData.hasStation = getHasStationDeploy(grants);
        eventLoginData.hasContract = getHasContract(grants);
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
        eventLoginData.hasSignalConfig = getHasSignalConfig(grants);
        eventLoginData.hasBadSignalUpload = getHasBadSignalUpload(grants);
        eventLoginData.hasDevicePositionCalibration = getHasDevicePositionCalibration(grants);
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
            String id = account.getId();
            boolean totpEnable = account.isTotpEnable();
            try {
                LogUtils.loge("id = " + id + ",totpEnable = " + totpEnable);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            if (totpEnable) {
                eventLoginData.needAuth = true;
            }
        }
        return eventLoginData;
    }

    /**
     * 判断基站权限
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
     * 判断是否超级账户
     *
     * @param isSupperAccountStr
     * @return
     */
    private static boolean getIsSupperAccount(String isSupperAccountStr) {
        return !TextUtils.isEmpty(isSupperAccountStr) && "true".equalsIgnoreCase(isSupperAccountStr);
    }

    /**
     * 判断合同权限
     *
     * @param grants
     * @return
     */
    private static boolean getHasContract(GrantsInfo grants) {
        if (grants != null) {
            List<String> contract = grants.getContract();
            if (contract != null) {
                return contract.contains("list") || contract.contains("create");
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
     * 判断是否有子账户权限
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
     * 判断是否有子账户权限
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

    private static boolean getHasDeviceBriefList(GrantsInfo grants) {
        if (grants != null) {
            List<String> grantsDevice = grants.getDevice();
            if (grantsDevice != null) {
                return grantsDevice.contains("briefList") && grantsDevice.contains("statusStatistics");
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

}
