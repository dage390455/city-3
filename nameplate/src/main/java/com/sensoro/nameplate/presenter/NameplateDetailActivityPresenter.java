package com.sensoro.nameplate.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceTypeStyles;
import com.sensoro.common.server.bean.MergeTypeStyles;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.common.server.response.NameplateBindDeviceRsp;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.nameplate.IMainViews.INameplateDetailActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.activity.DeployNameplateAddSensorFromListActivity;
import com.sensoro.nameplate.activity.EditNameplateDetailActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import static com.sensoro.common.constant.Constants.DIRECTION_DOWN;
import static com.sensoro.common.constant.Constants.DIRECTION_UP;
import static com.sensoro.common.constant.Constants.EVENT_DATA_UPDATENAMEPALTELIST;
import static com.sensoro.common.constant.Constants.EXTRA_SETTING_TAG_LIST;

public class NameplateDetailActivityPresenter extends BasePresenter<INameplateDetailActivityView> {
    private Activity mContext;
    private String nameplateId;
    private volatile int cur_page = 1;
    private List<NamePlateInfo> plateInfos = new ArrayList<>();
    NamePlateInfo namePlateInfo;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        if (code == EVENT_DATA_UPDATENAMEPALTELIST) {
            getNameplateDetail();


        }
    }

    @Override
    public void initData(Context context) {
        EventBus.getDefault().register(this);
        mContext = (Activity) context;
        nameplateId = mContext.getIntent().getStringExtra("nameplateId");
        if (!TextUtils.isEmpty(nameplateId)) {


            RetrofitServiceHelper.getInstance().getBaseUrlType();
            String s = RetrofitServiceHelper.getInstance().BASE_URL + "/nameplate/" + nameplateId;
            getView().setQrCodeUrl(s);

            getNameplateDetail();
        }
    }

    @Override
    public void onDestroy() {

        EventBus.getDefault().unregister(this);
    }

    /**
     * 解绑
     *
     * @param pos
     */
    public void unbindNameplateDevice(int pos) {
        if (isAttachedView()) {
            getView().showProgressDialog();
        }

        List<String> list = new ArrayList<>();
        if (null != plateInfos && plateInfos.size() > 0) {
            list.add(plateInfos.get(pos).get_id());
        }
        RetrofitServiceHelper.getInstance().unbindNameplateDevice(nameplateId, list).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<Integer>>(this) {

            @Override
            public void onCompleted(ResponseResult<Integer> result) {

                if (result.getData() == 1) {
                    getView().updateNamePlateStatus(pos);
                }


            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().onPullRefreshComplete();


            }
        });


    }

    public void getNameplateDetail() {
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().getNameplateDetail(nameplateId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<NamePlateInfo>>(this) {

            @Override
            public void onCompleted(ResponseResult<NamePlateInfo> namePlateInfoResponse) {

                namePlateInfo = namePlateInfoResponse.getData();
                getView().updateTopDetail(namePlateInfo);
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

                        dealData(plateInfos);
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
                            dealData(plateInfos);
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

    /**
     * icon对应
     *
     * @param data
     */
    private void dealData(List<NamePlateInfo> data) {
        for (NamePlateInfo model : data) {
            DeviceTypeStyles configDeviceType = PreferencesHelper.getInstance().getConfigDeviceType(model.getDeviceType());
            if (configDeviceType != null) {
                String category = configDeviceType.getCategory();
                String mergeType = configDeviceType.getMergeType();
                MergeTypeStyles mergeTypeStyles = PreferencesHelper.getInstance().getConfigMergeType(mergeType);
                if (mergeTypeStyles != null) {
                    model.deviceTypeName = mergeTypeStyles.getName();
                    if (!TextUtils.isEmpty(category)) {
                        model.deviceTypeName = model.deviceTypeName + category;
                    }
                    if (TextUtils.isEmpty(mergeTypeStyles.getImage())) {
                        model.iconUrl = "";
                    } else {
                        model.iconUrl = mergeTypeStyles.getImage();
                    }
                } else {
                    model.deviceTypeName = mContext.getString(R.string.unknown);
                    model.iconUrl = "";
                }

            } else {
                model.deviceTypeName = mContext.getString(R.string.unknown);
                model.iconUrl = "";
            }

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

        if (!TextUtils.isEmpty(nameplateId)) {
            intent.putExtra("nameplateId", nameplateId);
        }
        if (null != namePlateInfo) {
            if (null != namePlateInfo.getTags() && namePlateInfo.getTags().size() > 0) {
                intent.putStringArrayListExtra(EXTRA_SETTING_TAG_LIST, namePlateInfo.getTags());
            }
            if (!TextUtils.isEmpty(namePlateInfo.getName())) {
                intent.putExtra("nameplateName", namePlateInfo.getName());
            }
        }
        getView().startAC(intent);
    }
}
