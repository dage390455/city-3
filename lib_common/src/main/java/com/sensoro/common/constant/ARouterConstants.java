package com.sensoro.common.constant;

public interface ARouterConstants {

    String ACTIVITY_THREE_PHASE_ELECT_CONFIG_ACTIVITY = "/app/activity/ThreePhaseElectConfigActivity";
    String ACTIVITY_DEPLOY_RECORD_CONFIG_COMMON_ELECT_ACTIVITY = "/app/activity/DeployRecordConfigCommonElectActivity";
    String ACTIVITY_DEPLOY_RECORD_CONFIG_THREE_PHASE_ELECT_ACTIVITY = "/app/activity/DeployRecordConfigThreePhaseElectActivity";

    String ACTIVITY_NAMEPLATE_LIST = "/nameplate/activity/NameplateListActivity";
    String FRAGMENT_FIRE_WARN_FRAGMENT = "/common/activity/FireWarnFragment";

    String ACTIVITY_DEPLOY_DEVICE_TAG = "/app/activity/DeployDeviceTagActivity";

    String ACTIVITY_SCAN = "/app/activity/ScanActivity";
    String ACTIVITY_DEPLOYRESULT = "/app/activity/DeployResultActivity";

    String ACTIVITY_DEPLOY_DEVICE_PIC = "/app/activity/DeployMonitorDeployPicActivity";

    String ACTIVITY_DEPLOY_ASSOCIATE_SENSOR = "/nameplate/activity/DeployNameplateAddSensorActivity";

    String ACTIVITY_DEPLOY_ASSOCIATE_SENSOR_FROM_LIST = "/nameplate/activity/DeployNameplateAddSensorFromListActivity";

    String AROUTER_PATH = "arouter_path";
    String AROUTER_PATH_NAMEPLATE = "AROUTER_PATH_NAMEPLATE";

    String ACTIVITY_DEPLOY_NAMEPLATE = "/nameplate/activity/DeployNameplateActivity";

    String ACTIVITY_NAMEPLATE_DETAIL = "/nameplate/activity/NameplateDetailActivity";

    String FRAGMENT_CAMERA_WARN_LIST = "/city_camera/fragment/CameraWarnListFragment";


    String ACTIVITY_VIDEP_PLAY = "/app/activity/VideoPlayActivity";

    String ACTIVITY_TEST_UPDATE = "/app/activity/TestUpdateActivity";

    String ACTIVITY_TAKE_RECORD = "/app/activity/TakeRecordActivity";



//    摄像头组件相关路由配置
    String ACTIVITY_CITY_CAMERA_LAUNCHER = "/city_camera/activity/LauncherActivity";
//    巡检任务组件化相关路由配置
    String ACTIVITY_INSPECTIONTASK_List = "/inspectiontask/activity/InspectionTaskListActivity";
    String ACTIVITY_INSPECTION = "/inspectiontask/activity/InspectionActivity";
    String ACTIVITY_INSPECTION_EXCEPTION_DETAIL = "/inspectiontask/activity/InspectionExceptionDetailActivity";


//合同管理组件化路由配置
    String ACTIVITY_CONTRACT_MANAGER = "/contractmanager/activity/ContractManagerActivity";
    String ACTIVITY_CONTRACT_EDITOR="/contractmanager/activity/ContractEditorActivity";
    String ACTIVITY_BASESTATION_LIST= "/basestation/activity/BaseStationListActivity";


    String ACTIVITY_MonitorPointMap_Activity="/smartcity/activity/MonitorPointMapActivity";
    String ACTIVITY_MonitorPointMap_ENActivity="/smartcity/activity/MonitorPointMapENActivity";



    //组件化模拟登录过程使用
    String ACTIVITY_LOGIN_TEST = "/logintest/activity/LoginTestActivity";



//    森林防火组件化
    String ACTIVITY_FORESTFIRE_CAMERA_LIST = "/forestfire/activity/ForestFireCameraListActivity";


}
