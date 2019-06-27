package com.sensoro.city_camera.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.amap.api.maps.model.LatLng;
import com.sensoro.city_camera.IMainViews.ISecurityWarnDetailView;
import com.sensoro.city_camera.R;
import com.sensoro.city_camera.activity.SecurityWarnRecordDetailActivity;
import com.sensoro.city_camera.dialog.SecurityWarnConfirmDialog;
import com.sensoro.city_camera.util.MapUtil;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.security.bean.SecurityAlarmDetailInfo;
import com.sensoro.common.server.security.response.HandleAlarmRsp;
import com.sensoro.common.server.security.response.SecurityAlarmDetailRsp;
import com.sensoro.common.utils.AppUtils;

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
        requestSecurityWarnDetailData(mSecurityInfoId);
    }

    private void requestSecurityWarnDetailData(String id){
        getView().showProgressDialog();
        RetrofitServiceHelper
                .getInstance()
                .getSecurityAlarmDetails(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CityObserver<SecurityAlarmDetailRsp>(this) {
                    @Override
                    public void onCompleted(SecurityAlarmDetailRsp securityAlarmDetailRsp) {
                        if (securityAlarmDetailRsp != null){
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


    public void doContactOwner() {
        /*String tempNumber = deviceAlarmLogInfo.getDeviceNotification().getContent();

        if (TextUtils.isEmpty(tempNumber)) {
            if (isAttachedView()) {
                getView().toastShort(mContext.getString(R.string.no_find_contact_phone_number));
            }
        } else {
            AppUtils.diallPhone(tempNumber, mContext);
        }*/
    }

    public void doNavigation() {
        /*double[] deviceLonlat = deviceAlarmLogInfo.getDeviceLonlat();
        if (deviceLonlat != null && deviceLonlat.length > 1) {
            destPosition = new LatLng(deviceLonlat[1], deviceLonlat[0]);
            if (CityAppUtils.doNavigation(mContext, destPosition)) {
                return;
            } else {
                if (isAttachedView()) {
                    getView().toastShort(mContext.getString(R.string.location_not_obtained));
                }
            }
        } else {
            if (isAttachedView()) {
                getView().toastShort(mContext.getString(R.string.location_not_obtained));
            }
        }*/

//        MapUtil.locateAndNavigation(mContxt, new LatLng(116.39747132275389, 39.9086268928637));
    }

    public void doConfirm(){
        getView().showConfirmDialog(mSecurityAlarmDetailInfo);
    }

    public void doBack(){
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
                        // TODO 刷新item
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
        if(isAttachedView()){
            getView().startAC(intent);
        }
    }
}
