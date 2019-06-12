package com.sensoro.nameplate.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.common.server.response.DeleteNamePlateRsp;
import com.sensoro.common.server.response.NamePlateListRsp;
import com.sensoro.nameplate.IMainViews.INameplateListActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.activity.NameplateDetailActivity;
import com.sensoro.nameplate.model.FilterModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NameplateListActivityPresenter extends BasePresenter<INameplateListActivityView> implements Constants {
    private Activity mContext;
    private volatile int cur_page = 1;
    private final List<NamePlateInfo> plateInfos = new ArrayList<>();
    private final List<String> mSearchHistoryList = new ArrayList<>();

    private String deviceFlag = null;


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        if (code == EVENT_DATA_UPDATENAMEPALTELIST || EVENT_DATA_ASSOCIATE_SENSOR_FROM_DETAIL == code) {
            requestDataByFilter(DIRECTION_DOWN, null, deviceFlag);
        } else if (code == EVENT_DATA_DEPLOY_RESULT_FINISH) {
            getView().finishAc();
        }
    }

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        initFilterDialog();
        EventBus.getDefault().register(this);
        //TODO 如果需要传递 更改key
        Object bundleValue = getBundleValue(mContext, EXTRA_DEVICE_CAMERA_DETAIL_INFO_LIST);
        if (bundleValue instanceof ArrayList) {
            getView().setSmartRefreshEnable(false);
            plateInfos.clear();
            List<NamePlateInfo> data = (List<NamePlateInfo>) bundleValue;
            plateInfos.addAll(data);
            getView().updateNameplateAdapter(plateInfos);
            getView().onPullRefreshComplete();
            getView().dismissProgressDialog();
        } else {
            requestDataByFilter(DIRECTION_DOWN, null, deviceFlag);
        }
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_NAMEPLATE_LIST);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().updateSearchHistoryList(mSearchHistoryList);
        }


    }


    public void initFilterDialog() {
        List<FilterModel> list = new ArrayList<>();


        FilterModel filterModel = new FilterModel();

        filterModel.statusTitle = "全部";
        FilterModel filterModel1 = new FilterModel();

        filterModel1.statusTitle = "已关联";
        FilterModel filterModel2 = new FilterModel();

        filterModel2.statusTitle = "未关联";

        list.add(filterModel);
        list.add(filterModel1);
        list.add(filterModel2);
        getView().updateSelectDeviceStatusList(list);
    }

    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        List<String> warnList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_NAMEPLATE_LIST, text);
        mSearchHistoryList.clear();
        mSearchHistoryList.addAll(warnList);
        getView().updateSearchHistoryList(mSearchHistoryList);

    }

    public void clearSearchHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_NAMEPLATE_LIST);
        mSearchHistoryList.clear();
        getView().updateSearchHistoryList(mSearchHistoryList);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);

    }

    public void requestDataByFilter(final int direction, String search) {

        requestDataByFilter(direction, search, deviceFlag);
    }

    public void requestDataByFilter(final int direction, String search, String deviceFlag) {


        this.deviceFlag = deviceFlag;

        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 1;
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }
                RetrofitServiceHelper.getInstance().getNameplateList(20, cur_page, search, deviceFlag, "true").subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<NamePlateListRsp>(this) {
                    @Override
                    public void onCompleted(NamePlateListRsp deviceCameraListRsp) {

                        List<NamePlateInfo> data = deviceCameraListRsp.getData();
                        plateInfos.clear();
                        if (data != null && data.size() > 0) {
                            plateInfos.addAll(data);
                        }
                        getView().updateNameplateAdapter(plateInfos);
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
                RetrofitServiceHelper.getInstance().getNameplateList(20, cur_page, search, deviceFlag, "true").subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<NamePlateListRsp>(this) {
                    @Override
                    public void onCompleted(NamePlateListRsp deviceCameraListRsp) {

                        List<NamePlateInfo> data = deviceCameraListRsp.getData();
                        if (data != null && data.size() > 0) {
                            plateInfos.addAll(data);
                            getView().updateNameplateAdapter(plateInfos);
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
     * 删除铭牌
     *
     * @param position
     */
    public void deleteNamePlate(int position) {
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        if (null != plateInfos.get(position)) {
            RetrofitServiceHelper.getInstance().deleteNameplate(plateInfos.get(position).get_id()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeleteNamePlateRsp>(this) {
                @Override
                public void onCompleted(DeleteNamePlateRsp deviceCameraListRsp) {

                    Integer data = deviceCameraListRsp.getData();

                    if (data == 1) {
                        plateInfos.remove(position);
                        getView().updateDeleteNamePlateStatus(position);
                    }

                    getView().dismissProgressDialog();

                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().toastShort(errorMsg);
                    getView().onPullRefreshComplete();


                }
            });


        }

    }


    public void doScanSearch() {
        Bundle bundle1 = new Bundle();
        bundle1.putInt(EXTRA_SCAN_ORIGIN_TYPE, Constants.EVENT_DATA_SEARCH_NAMEPLATE);
        startActivity(ARouterConstants.ACTIVITY_SCAN, bundle1, mContext);
    }

    public void doNameplateDetail(int position) {

        if (null != plateInfos.get(position)) {
            getView().startAC(new Intent(mContext, NameplateDetailActivity.class).putExtra("nameplateId", plateInfos.get(position).get_id()));
        }

    }
}
