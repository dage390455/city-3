package com.sensoro.nameplate.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.common.server.response.NameplateBindDeviceRsp;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.nameplate.IMainViews.INameplateDetailActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.activity.DeployNameplateAddSensorFromListActivity;
import com.sensoro.nameplate.activity.EditNameplateDetailActivity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sensoro.common.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.common.constant.Constants.DIRECTION_UP;

public class NameplateDetailActivityPresenter extends BasePresenter<INameplateDetailActivityView> {
    private Activity mContext;
    private String nameplateId;
    private volatile int cur_page = 1;
    private List<NamePlateInfo> plateInfos = new ArrayList<>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        nameplateId = mContext.getIntent().getStringExtra("nameplateId");
        if (!TextUtils.isEmpty(nameplateId)) {

            getNameplateDetail();
        }
    }

    @Override
    public void onDestroy() {

    }


    public void getNameplateDetail() {
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().getNameplateDetail(nameplateId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<NamePlateInfo>>(this) {

            @Override
            public void onCompleted(ResponseResult<NamePlateInfo> namePlateInfoResponse) {


                getView().updateTopDetail(namePlateInfoResponse.getData());
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().onPullRefreshComplete();


            }
        });


    }

    public void requestData(final int direction) {
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 1;
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }
                RetrofitServiceHelper.getInstance().getNameplateBindDevices(20, cur_page, nameplateId).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<NameplateBindDeviceRsp>(this) {
                    @Override
                    public void onCompleted(NameplateBindDeviceRsp deviceCameraListRsp) {

                        List<NamePlateInfo> data = deviceCameraListRsp.getData();
                        plateInfos.clear();
                        if (data != null && data.size() > 0) {
                            plateInfos.addAll(data);
                        }
                        getView().updateBindDeviceAdapter(plateInfos);
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                        getView().onPullRefreshComplete();

                    }
                });
                break;
            case DIRECTION_UP:
                cur_page++;
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }
                RetrofitServiceHelper.getInstance().getNameplateBindDevices(20, cur_page, nameplateId).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<NameplateBindDeviceRsp>(this) {
                    @Override
                    public void onCompleted(NameplateBindDeviceRsp deviceCameraListRsp) {

                        List<NamePlateInfo> data = deviceCameraListRsp.getData();
                        if (data != null && data.size() > 0) {
                            plateInfos.addAll(data);
                            getView().updateBindDeviceAdapter(plateInfos);
                        } else {
                            getView().toastShort(mContext.getString(R.string.no_more_data));
                        }
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                        getView().onPullRefreshComplete();

                    }
                });
                break;
            default:
                break;

        }

    }

    public void doNesSensor(int position) {
        switch (position) {
            case 0:
                break;
            case 1:
                Intent intent = new Intent(mContext, DeployNameplateAddSensorFromListActivity.class);
                getView().startAC(intent);
                break;
        }

    }

    public void doEditNameplate() {
        Intent intent = new Intent(mContext, EditNameplateDetailActivity.class);
        getView().startAC(intent);
    }
}
