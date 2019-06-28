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
import com.sensoro.city_camera.dialog.SecurityCameraDetailsDialog;
import com.sensoro.city_camera.dialog.SecurityWarnConfirmDialog;
import com.sensoro.city_camera.util.MapUtil;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.security.bean.SecurityAlarmDetailInfo;
import com.sensoro.common.server.security.bean.SecurityCameraInfo;
import com.sensoro.common.server.security.bean.SecurityContactsInfo;
import com.sensoro.common.server.security.response.HandleAlarmRsp;
import com.sensoro.common.server.security.response.SecurityAlarmDetailRsp;
import com.sensoro.common.server.security.response.SecurityAlarmTimelineRsp;
import com.sensoro.common.utils.AppUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @author : bin.tian
 * date   : 2019-06-24
 */
public class SecurityWarnDetailPresenter extends BasePresenter<ISecurityWarnDetailView> implements SecurityWarnConfirmDialog.SecurityConfirmCallback, SecurityCameraDetailsDialog.SecurityCameraDetailsCallback {
    private Activity mActivity;
    private String mSecurityInfoId;
    private SecurityAlarmDetailInfo mSecurityAlarmDetailInfo;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        mSecurityInfoId = mActivity.getIntent().getStringExtra("id");
        requestSecurityWarnDetailData(mSecurityInfoId);
        requestSecurityWarnTimeLineData(mSecurityInfoId);
    }

    private void requestSecurityWarnDetailData(String id) {
        getView().showProgressDialog();
        RetrofitServiceHelper
                .getInstance()
                .getSecurityAlarmDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<SecurityAlarmDetailRsp>(this) {
                    @Override
                    public void onCompleted(SecurityAlarmDetailRsp securityAlarmDetailRsp) {
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
                .subscribe(new CityObserver<SecurityAlarmTimelineRsp>(this) {
                    @Override
                    public void onCompleted(SecurityAlarmTimelineRsp securityAlarmTimelineRsp) {
                        SecurityAlarmTimelineRsp.SecurityAlarmTimelineData securityAlarmTimelineRspData = securityAlarmTimelineRsp.getData();
                        if(securityAlarmTimelineRspData != null){
                            getView().updateSecurityWarnTimeLine(securityAlarmTimelineRspData.list);
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {

                    }
                });
    }


    public void doContactOwner() {
        List<SecurityContactsInfo> contacts = mSecurityAlarmDetailInfo.getContacts();

        if (contacts.isEmpty()) {
            if (isAttachedView()) {
                getView().toastShort(mActivity.getString(R.string.no_find_contact_phone_number));
            }
        } else {
            AppUtils.diallPhone(contacts.get(0).getMobilePhone(), mActivity);
        }
    }

    public void doNavigation() {
        SecurityCameraInfo camera = mSecurityAlarmDetailInfo.getCamera();
        if (camera!= null){
            LatLng destPosition = new LatLng(Double.parseDouble(camera.getLatitude()), Double.parseDouble(camera.getLongitude()));
            MapUtil.locateAndNavigation(mActivity, destPosition);
        } else {
            if (isAttachedView()) {
                getView().toastShort(mActivity.getString(R.string.location_not_obtained));
            }
        }
    }

    public void doConfirm() {
        getView().showConfirmDialog(mSecurityAlarmDetailInfo);
    }

    /**
     * 显示摄像机详情
     */
    public void showCameraDetail(){
        getView().showCameraDetailsDialog(mSecurityAlarmDetailInfo);
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
                .subscribe(new CityObserver<HandleAlarmRsp>(this) {
                    @Override
                    public void onCompleted(HandleAlarmRsp handleAlarmRsp) {
                        mSecurityAlarmDetailInfo.setIsEffective(isEffective);
                        getView().updateSecurityConfirmResult(mSecurityAlarmDetailInfo);
                        requestSecurityWarnTimeLineData(mSecurityInfoId);
                        getView().setIntentResult(Activity.RESULT_OK);
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {

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


    @Override
    public void onNavi() {

    }

    @Override
    public void showContactsDetails() {

    }

    public void doPreviewImages(int position) {
        Intent intent = new Intent(mActivity, PhotoPreviewActivity.class);
        intent.putExtra(PhotoPreviewPresenter.EXTRA_KEY_POSITION, position);
        intent.putExtra(PhotoPreviewPresenter.EXTRA_KEY_SECURITY_INFO, mSecurityAlarmDetailInfo);
        getView().startAC(intent);
    }
}
