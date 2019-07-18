package com.sensoro.city_camera.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.sensoro.city_camera.IMainViews.ISecurityWarnDetailView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.activity.PhotoPreviewActivity;
import com.sensoro.city_camera.activity.SecurityWarnRecordDetailActivity;
import com.sensoro.city_camera.constants.SecurityConstants;
import com.sensoro.city_camera.dialog.SecurityWarnConfirmDialog;
import com.sensoro.city_camera.util.MapUtil;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.model.DeviceNotificationBean;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.HandleAlarmData;
import com.sensoro.common.server.bean.SecurityAlarmTimelineData;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.server.security.bean.SecurityAlarmDetailInfo;
import com.sensoro.common.server.security.bean.SecurityAlarmInfo;
import com.sensoro.common.server.security.bean.SecurityCameraInfo;
import com.sensoro.common.server.security.bean.SecurityContactsInfo;
import com.sensoro.common.server.security.bean.SecurityRecord;
import com.sensoro.common.server.security.bean.SecurityWarnRecord;
import com.sensoro.common.utils.AppUtils;
import com.sensoro.common.widgets.dialog.WarningContactDialogUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : bin.tian
 * date   : 2019-06-24
 */
public class SecurityWarnDetailPresenter extends BasePresenter<ISecurityWarnDetailView> implements SecurityWarnConfirmDialog.SecurityConfirmCallback {
    private Activity mActivity;
    private String mSecurityInfoId;
    private SecurityAlarmDetailInfo mSecurityAlarmDetailInfo;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        mSecurityInfoId = mActivity.getIntent().getStringExtra("id");

        updatePreData();

        requestSecurityWarnDetailData(mSecurityInfoId);
        requestSecurityWarnTimeLineData(mSecurityInfoId);
        requestVideo(mSecurityInfoId);
    }

    private void updatePreData() {
        SecurityAlarmInfo securityAlarmInfo = (SecurityAlarmInfo) mActivity.getIntent().getSerializableExtra("SecurityAlarmInfo");
        if (securityAlarmInfo != null && isAttachedView()) {
            getView().updateSecurityWarnDetail(securityAlarmInfo);
        }
    }

    private void requestSecurityWarnDetailData(String id) {
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper
                .getInstance()
                .getSecurityAlarmDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ResponseResult<SecurityAlarmDetailInfo>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<SecurityAlarmDetailInfo> securityAlarmDetailRsp) {
                        if (securityAlarmDetailRsp != null) {
                            mSecurityAlarmDetailInfo = securityAlarmDetailRsp.getData();
                            getView().updateSecurityWarnDetail(mSecurityAlarmDetailInfo);
                        }
                        if (isAttachedView()) {
                            getView().dismissProgressDialog();
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (isAttachedView()) {
                            getView().dismissProgressDialog();
                        }
                    }
                });
    }

    private void requestSecurityWarnTimeLineData(String id) {
        RetrofitServiceHelper
                .getInstance()
                .getSecurityAlarmTimeLine(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ResponseResult<SecurityAlarmTimelineData>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<SecurityAlarmTimelineData> securityAlarmTimelineRsp) {
                        SecurityAlarmTimelineData securityAlarmTimelineRspData = securityAlarmTimelineRsp.getData();
                        if (securityAlarmTimelineRspData != null) {
                            getView().updateSecurityWarnTimeLine(securityAlarmTimelineRspData.list);
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {

                    }
                });
    }

    /**
     * 联系：相机联系人电话
     */
    public void doCameraContact() {
        if (null == mSecurityAlarmDetailInfo || null == mSecurityAlarmDetailInfo.getCamera()
                || null == mSecurityAlarmDetailInfo.getCamera().getContact()) {
            getView().toastShort(mActivity.getString(R.string.camera_contact_no_exist));
            return;
        }
        List<SecurityContactsInfo> contacts = mSecurityAlarmDetailInfo.getCamera().getContact();

        if (contacts.isEmpty()) {
            if (isAttachedView()) {
                getView().toastShort(mActivity.getString(R.string.no_find_contact_phone_number));
            }
        } else {
            AppUtils.diallPhone(contacts.get(0).getMobilePhone(), mActivity);
        }
    }


    public void doContactOwner() {
        if (null == mSecurityAlarmDetailInfo || null == mSecurityAlarmDetailInfo.getContacts()) {
            getView().toastShort(mActivity.getString(R.string.owner_contact_no_exist));
            return;
        }
        List<SecurityContactsInfo> contacts = mSecurityAlarmDetailInfo.getContacts();

        if (contacts == null || contacts.isEmpty()) {
            if (isAttachedView()) {
                getView().toastShort(mActivity.getString(R.string.no_find_contact_phone_number));
            }
        } else {
            AppUtils.diallPhone(contacts.get(0).getMobilePhone(), mActivity);
        }
    }

    public void doNavigation() {
        if (mSecurityAlarmDetailInfo == null) {
            getView().toastShort(mActivity.getString(R.string.security_camera_info_error));
            return;
        }
        SecurityCameraInfo camera = mSecurityAlarmDetailInfo.getCamera();
        if (camera != null) {
            if (!TextUtils.isEmpty(camera.getLatitude()) && !TextUtils.isEmpty(camera.getLongitude())) {
                LatLng destPosition = null;
                try {
                    destPosition = new LatLng(Double.parseDouble(camera.getLatitude()), Double.parseDouble(camera.getLongitude()));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
                if (destPosition != null) {
                    MapUtil.locateAndNavigation(mActivity, destPosition);
                } else {
                    if (isAttachedView()) {
                        getView().toastShort(mActivity.getString(R.string.location_not_obtained));
                    }
                }
            } else {
                if (isAttachedView()) {
                    getView().toastShort(mActivity.getString(R.string.location_not_obtained));
                }
            }
        } else {
            if (isAttachedView()) {
                getView().toastShort(mActivity.getString(R.string.location_not_obtained));
            }
        }
    }

    public void doConfirm() {
        if (isAttachedView()) {
            getView().showConfirmDialog(mSecurityAlarmDetailInfo);
        }
    }


    /**
     * 显示布控信息详情
     */
    public void showDeployDetail() {
        if (isAttachedView()) {
            getView().showDeployDetail(mSecurityAlarmDetailInfo);
        }
    }

    public void doBack() {
        if (isAttachedView()) {
            getView().finishAc();
        }
    }

    @Override
    public void onConfirmClick(String id, int isEffective, String operationDetail) {
        RetrofitServiceHelper.getInstance().handleSecurityAlarm(id, isEffective, operationDetail)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ResponseResult<HandleAlarmData>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<HandleAlarmData> handleAlarmRsp) {
                        mSecurityAlarmDetailInfo.setIsEffective(isEffective);
                        if (handleAlarmRsp.getData() != null && handleAlarmRsp.getData().status > 0) {
                            mSecurityAlarmDetailInfo.setIsHandle(handleAlarmRsp.getData().status);
                        } else {
                            mSecurityAlarmDetailInfo.setIsHandle(1);//消息是否处理过返回后台没有确认，只是说大于0，而且后台返回数据结构为{"data":{"status":1}}
                        }
                        getView().updateSecurityConfirmResult(mSecurityAlarmDetailInfo);
                        requestSecurityWarnTimeLineData(mSecurityInfoId);
                        getView().setIntentResult(Activity.RESULT_OK);
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        if (isAttachedView() && mActivity != null) {
                            getView().toastShort(errorMsg);
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {

    }

    public void toSecurityWarnRecord() {
        Intent intent = mActivity.getIntent();
        intent.setClass(mActivity, SecurityWarnRecordDetailActivity.class);
        if (isAttachedView()) {
            getView().startAC(intent);
        }
    }


    public void onNavi() {
        doNavigation();
    }

    public void onRefreshCameraDetailsData() {
        if (null == mSecurityAlarmDetailInfo) {
            getView().toastShort(mActivity.getString(R.string.camera_contact_no_exist));
            return;
        }
        getView().onRefreshCameraDetailsData(mSecurityAlarmDetailInfo.getCamera());
    }


    public void showContactsDetails() {
        if (null == mSecurityAlarmDetailInfo || null == mSecurityAlarmDetailInfo.getCamera()
                || null == mSecurityAlarmDetailInfo.getCamera().getContact()) {
            if (isAttachedView()) {
                getView().toastShort(mActivity.getString(R.string.camera_contact_no_exist));
            }
            return;
        }
        List<SecurityContactsInfo> contactsInfos = mSecurityAlarmDetailInfo.getCamera().getContact();
        if (contactsInfos.size() > 1) {
            List<DeviceNotificationBean> list = new ArrayList<>(contactsInfos.size());
            for (SecurityContactsInfo info : contactsInfos) {
                DeviceNotificationBean bean = new DeviceNotificationBean();
                bean.setTypes("phone");
                bean.setContact(info.getMobilePhone());
                list.add(bean);
            }

            new WarningContactDialogUtil(mActivity).show(list);
        } else {
            doCameraContact();
        }
    }

    public void doPreviewImages(int position) {
        if (mSecurityAlarmDetailInfo != null) {
            Intent intent = new Intent(mActivity, PhotoPreviewActivity.class);
            intent.putExtra(PhotoPreviewPresenter.EXTRA_KEY_POSITION, position);
            intent.putExtra(PhotoPreviewPresenter.EXTRA_KEY_SECURITY_INFO, mSecurityAlarmDetailInfo);
            if (isAttachedView()) {
                getView().startAC(intent);
            }
        }
    }

    private void requestVideo(String id) {
        RetrofitServiceHelper
                .getInstance()
                .getSecurityWarnRecord(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<ResponseResult<SecurityWarnRecord>>(null) {
                    @Override
                    public void onCompleted(ResponseResult<SecurityWarnRecord> securityWarnRecordResp) {
                        List<SecurityRecord> recordList = securityWarnRecordResp.getData().list;
                        if (recordList != null && !recordList.isEmpty()) {
                            SecurityRecord securityRecord = recordList.get(0);
                            if (securityRecord != null) {
                                if (isAttachedView()) {
                                    getView().updateVideoRecordEnable(securityRecord.status != SecurityConstants.VIDEO_STATUS_TRANSCODING);
                                }
                            }
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                    }
                });


    }


    /**
     * 显示摄像机详情
     */
    public void showCameraDetail() {
        if (isAttachedView()) {
            getView().showCameraDetailsDialog(mSecurityAlarmDetailInfo);
        }
    }

}
