package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.lzy.imagepicker.bean.ImageItem;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.AlarmContactActivity;
import com.sensoro.smartcity.activity.DeployDeviceTagActivity;
import com.sensoro.smartcity.activity.DeployResultActivity;
import com.sensoro.smartcity.activity.DeployResultActivityTest;
import com.sensoro.smartcity.activity.DeploySettingPhotoActivity;
import com.sensoro.smartcity.activity.NameAddressActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployDeviceDetailActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.DeployContactModel;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.AlarmInfo;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.bean.ScenesData;
import com.sensoro.smartcity.server.response.DeviceDeployRsp;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.StationInfo;
import com.sensoro.smartcity.server.response.StationInfoRsp;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.widget.popup.UpLoadPhotosUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DeployDeviceDetailActivityPresenter extends BasePresenter<IDeployDeviceDetailActivityView> implements IOnCreate, Constants {
    private Activity mContext;
    private LatLng latLng;
    private final List<String> tagList = new ArrayList<>();
    private final List<DeployContactModel> deployContactModelList = new ArrayList<>();
    private Handler mHandler;
    private DeviceInfo deviceInfo;
    private boolean hasStation;
    private String mAddress;
    private final ArrayList<ImageItem> images = new ArrayList<>();
    private UpLoadPhotosUtils upLoadPhotosUtils;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        mHandler = new Handler(Looper.getMainLooper());
        deviceInfo = (DeviceInfo) mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_INFO);
        hasStation = mContext.getIntent().getBooleanExtra(EXTRA_IS_STATION_DEPLOY, false);
        getView().setDeployContactRelativeLayoutVisible(!hasStation);
        getView().setDeployDeviceRlSignalVisible(!hasStation);
        getView().setDeployPhotoVisible(!hasStation);
        initMap();
    }

    private void initMap() {
        //TODO 如果添加地图
        if (deviceInfo == null) {
            Intent intent = new Intent();
            intent.setClass(mContext, DeployResultActivityTest.class);
            intent.putExtra(EXTRA_IS_STATION_DEPLOY, hasStation);
            intent.putExtra(EXTRA_SENSOR_RESULT, -1);
            getView().startAC(intent);
        } else {
            String sn = deviceInfo.getSn();
            getView().setDeviceTitleName(sn);
            String name = deviceInfo.getName();
            if (TextUtils.isEmpty(name)) {
                name = mContext.getResources().getString(R.string.tips_hint_name_address_set);
            }
            getView().setNameAddressText(name);
            if (deviceInfo.getAlarms() != null) {
                AlarmInfo alarmInfo = deviceInfo.getAlarms();
                AlarmInfo.NotificationInfo notification = alarmInfo.getNotification();
                if (notification != null) {
                    //TODO 设置多个联系人
                    String contact = notification.getContact();
                    String content = notification.getContent();
                    if (TextUtils.isEmpty(contact) || TextUtils.isEmpty(content)) {
//                        getView().setContactEditText(mContext.getResources().getString(R.string.tips_hint_contact));
                    } else {
                        deployContactModelList.clear();
                        DeployContactModel deployContactModel = new DeployContactModel();
                        deployContactModel.name = contact;
                        deployContactModel.phone = content;
                        deployContactModelList.add(deployContactModel);
                        getView().updateContactData(deployContactModelList);
                    }

                }


            }

            String tags[] = deviceInfo.getTags();
            if (tags != null) {
                for (String tag : tags) {
                    if (!TextUtils.isEmpty(tag)) {
                        tagList.add(tag);
                    }
                }
                getView().updateTagsData(tagList);
            }
            String signal = deviceInfo.getSignal();
            if (!hasStation) {
                getView().refreshSignal(deviceInfo.getUpdatedTime(), signal);
            }
            //
            getView().updateUploadState(true);
        }
    }

    private String tagListToString() {
        if (tagList.size() == 0) {
            return null;
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < tagList.size(); i++) {
                String tag = tagList.get(i);
                if (!TextUtils.isEmpty(tag)) {
                    if (i == tagList.size() - 1) {
                        stringBuilder.append(tag);
                    } else {
                        stringBuilder.append(tag + ",");
                    }
                }

            }
            return stringBuilder.toString();
        }
    }

    //
    public void requestUpload(final String sn, final String name) {
//        例：大悦城20层走廊2号配电箱
        final String tags = tagListToString();
        String name_default = mContext.getString(R.string.tips_hint_name_address);
        if (TextUtils.isEmpty(name) || name.equals(name_default) || name.equals(mContext.getResources().getString(R
                .string.tips_hint_name_address_set))) {
            getView().toastShort(mContext.getResources().getString(R.string.tips_input_name));
            getView().updateUploadState(true);
            return;
        } else {
            byte[] bytes = new byte[0];
            try {
                bytes = name.getBytes("UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (bytes.length > 48) {
                getView().toastShort("名称/地址最长不能超过16个汉字或48个字符");
                getView().updateUploadState(true);
                return;
            }
        }
        if (latLng == null) {
            getView().toastShort(mContext.getResources().getString(R.string.tips_hint_location));
            getView().updateUploadState(true);
        } else {
            final double lon = latLng.longitude;
            final double lan = latLng.latitude;
            if (hasStation) {
                LogUtils.loge(tags);
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.doStationDeploy(sn, lon, lan, tags, name).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CityObserver<StationInfoRsp>(this) {

                            @Override
                            public void onErrorMsg(int errorCode, String errorMsg) {
                                getView().dismissProgressDialog();
                                getView().updateUploadState(true);
                                if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                                    getView().toastShort(errorMsg);
                                } else if (errorCode == 4013101 || errorCode == 4000013) {
                                    freshError(sn, null);
                                } else {
                                    freshError(sn, errorMsg);
                                }
                            }

                            @Override
                            public void onCompleted(StationInfoRsp stationInfoRsp) {
                                freshStation(stationInfoRsp);
                                getView().dismissProgressDialog();
                                getView().finishAc();
                            }
                        });
            } else {
                //TODO 联系人上传
//                if (TextUtils.isEmpty(contact) || name.equals(content)) {
//                    getView().toastShort("请输入联系人名称和电话号码");
//                    getView().updateUploadState(true);
//                    return;
//                }
//                if (!RegexUtils.checkPhone(content)) {
//                    getView().toastShort(mContext.getResources().getString(R.string.tips_phone_empty));
//                    getView().updateUploadState(true);
//                    return;
//                }

                if (images.size() > 0) {
                    //TODO 图片提交
                    final UpLoadPhotosUtils.UpLoadPhotoListener upLoadPhotoListener = new UpLoadPhotosUtils
                            .UpLoadPhotoListener() {

                        @Override
                        public void onStart() {
                            getView().showStartUploadProgressDialog();
                        }

                        @Override
                        public void onComplete(List<ScenesData> scenesDataList) {
                            ArrayList<String> strings = new ArrayList<>();
                            for (ScenesData scenesData : scenesDataList) {
                                scenesData.type = "image";
                                strings.add(scenesData.url);
                            }
                            getView().dismissUploadProgressDialog();
                            LogUtils.loge(this, "上传成功--- size = " + strings.size());
                            //TODO 上传结果
//                            doDeployResult(sn, name, tags, lon, lan, contact, content, strings);
                        }

                        @Override
                        public void onError(String errMsg) {
                            getView().updateUploadState(true);
                            getView().dismissUploadProgressDialog();
                            getView().toastShort(errMsg);
                        }

                        @Override
                        public void onProgress(int index, double percent) {
                            getView().showUploadProgressDialog(index, images.size(), percent);
                        }
                    };
                    upLoadPhotosUtils = new UpLoadPhotosUtils(mContext, upLoadPhotoListener);
                    upLoadPhotosUtils.doUploadPhoto(images);
                } else {
//                    doDeployResult(sn, name, tags, lon, lan, contact, content, null);
                }
            }
        }

    }

    private void doDeployResult(final String sn, String name, String tags, double lon, double lan, String contact,
                                String content, List<String> imgUrls) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.doDevicePointDeploy(sn, lon, lan, tags, name,
                contact, content, imgUrls).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<DeviceDeployRsp>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().updateUploadState(true);
                        if (errorCode == ERR_CODE_NET_CONNECT_EX || errorCode == ERR_CODE_UNKNOWN_EX) {
                            getView().toastShort(errorMsg);
                        } else if (errorCode == 4013101 || errorCode == 4000013) {
                            freshError(sn, null);
                        } else {
                            freshError(sn, errorMsg);
                        }
                    }

                    @Override
                    public void onCompleted(DeviceDeployRsp deviceDeployRsp) {
                        freshPoint(deviceDeployRsp);
                        getView().dismissProgressDialog();
                        getView().finishAc();
                    }
                });
    }

    private void freshError(String scanSN, String errorInfo) {
        //
        Intent intent = new Intent();
        intent.setClass(mContext, DeployResultActivityTest.class);
        intent.putExtra(EXTRA_SENSOR_RESULT, -1);
        intent.putExtra(EXTRA_SENSOR_SN_RESULT, scanSN);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, hasStation);
        if (!TextUtils.isEmpty(errorInfo)) {
            intent.putExtra(EXTRA_SENSOR_RESULT_ERROR, errorInfo);
        }
        getView().startAC(intent);
    }

    private void freshPoint(DeviceDeployRsp deviceDeployRsp) {
        int errCode = deviceDeployRsp.getErrcode();
        int resultCode = 1;
        if (errCode != ResponseBase.CODE_SUCCESS) {
            resultCode = errCode;
        }
        Intent intent = new Intent(mContext, DeployResultActivity.class);
        intent.putExtra(EXTRA_SENSOR_RESULT, resultCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        DeviceInfo data = deviceDeployRsp.getData();
        data.setAddress(mAddress);
        intent.putExtra(EXTRA_DEVICE_INFO, data);
        //TODO 联系人
//        intent.putExtra(EXTRA_SETTING_CONTACT, contact);
//        intent.putExtra(EXTRA_SETTING_CONTENT, content);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, false);
        getView().startAC(intent);
    }

    private void freshStation(StationInfoRsp stationInfoRsp) {
        String s = stationInfoRsp.toString();
        LogUtils.loge(s);
        int errCode = stationInfoRsp.getErrcode();
        int resultCode = 1;
        if (errCode != ResponseBase.CODE_SUCCESS) {
            resultCode = errCode;
        }
        //
        StationInfo stationInfo = stationInfoRsp.getData();
        double[] lonLat = stationInfo.getLonlat();
        String name = stationInfo.getName();
        String sn = stationInfo.getSn();
        String[] tags = stationInfo.getTags();
        int normalStatus = stationInfo.getNormalStatus();
        long updatedTime = stationInfo.getUpdatedTime();
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSn(sn);
        deviceInfo.setTags(tags);
        deviceInfo.setLonlat(lonLat);
        deviceInfo.setStatus(normalStatus);
        deviceInfo.setAddress(mAddress);
        deviceInfo.setUpdatedTime(updatedTime);
        if (!TextUtils.isEmpty(name)) {
            deviceInfo.setName(name);
        }
        Intent intent = new Intent(mContext, DeployResultActivity.class);
        intent.putExtra(EXTRA_SENSOR_RESULT, resultCode);
        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
        intent.putExtra(EXTRA_IS_STATION_DEPLOY, true);
        intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
        getView().startAC(intent);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
        tagList.clear();
        images.clear();
    }

    public void doNameAddress(String nameAddress) {
        Intent intent = new Intent(mContext, NameAddressActivity.class);
        intent.putExtra(EXTRA_SETTING_NAME_ADDRESS, nameAddress);
        getView().startAC(intent);
    }

    public void doAlarmContact() {
        Intent intent = new Intent(mContext, AlarmContactActivity.class);
        intent.putExtra(EXTRA_SETTING_DEPLOY_CONTACT, (ArrayList<DeployContactModel>) deployContactModelList);
        getView().startAC(intent);
    }

    public void doTag() {
        Intent intent = new Intent(mContext, DeployDeviceTagActivity.class);
        intent.putStringArrayListExtra(EXTRA_SETTING_TAG_LIST, (ArrayList<String>) tagList);
        getView().startAC(intent);
    }

    public void doSettingPhoto() {
        Intent intent = new Intent(mContext, DeploySettingPhotoActivity.class);
        if (images.size() > 0) {
            intent.putExtra(EXTRA_DEPLOY_TO_PHOTO, images);
        }
        getView().startAC(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        Object data = eventData.data;
        if (code == EVENT_DATA_DEPLOY_RESULT_FINISH || code == EVENT_DATA_DEPLOY_RESULT_CONTINUE) {
            getView().finishAc();
        } else if (code == EVENT_DATA_DEPLOY_SETTING_NAME_ADDRESS) {
            if (data instanceof String) {
                getView().setNameAddressText((String) data);
            }
        } else if (code == EVENT_DATA_DEPLOY_SETTING_TAG) {
            if (data instanceof List) {
                tagList.clear();
                tagList.addAll((List<String>) data);
                getView().updateTagsData(tagList);
            }
        } else if (code == EVENT_DATA_DEPLOY_SETTING_CONTACT) {
            if (data instanceof List) {
                //TODO 联系人
                deployContactModelList.clear();
                deployContactModelList.addAll((List<DeployContactModel>) data);
                getView().updateContactData(deployContactModelList);
//                contact = ((DeployContactModel) data).name;
//                content = ((DeployContactModel) data).phone;
//                getView().setContactEditText(contact + ":" + content);
            }
        } else if (code == EVENT_DATA_DEPLOY_SETTING_PHOTO) {
            if (data instanceof List) {
                images.clear();
                images.addAll((ArrayList<ImageItem>) data);
                if (images.size() > 0) {
                    getView().setDeployPhotoText("已选择" + images.size() + "张图片");
                } else {
                    getView().setDeployPhotoText("未添加");
                }
            }
        }
//        LogUtils.loge(this, eventData.toString());
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}
