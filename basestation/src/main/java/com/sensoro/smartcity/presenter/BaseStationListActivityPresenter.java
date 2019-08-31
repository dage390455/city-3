package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.basestation.R;
import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.BaseStationInfo;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.smartcity.activity.BaseStationDetailActivity;
import com.sensoro.smartcity.imainviews.ICameraListActivityView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BaseStationListActivityPresenter extends BasePresenter<ICameraListActivityView>  {
    private Activity mContext;
    private volatile int curPage = 1;
    private final List<BaseStationInfo> deviceCameraInfos = new ArrayList<>();
    private final List<String> mSearchHistoryList = new ArrayList<>();

    private final List<CameraFilterModel> cameraFilterModelList = new ArrayList<>();


    private final HashMap<String, String> selectedHashMap = new HashMap<>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;

        initData();
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_BASESTATION);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().updateSearchHistoryList(mSearchHistoryList);
        }
    }


    /**
     * 首先获取筛选项
     */
    public void initData() {


        if (cameraFilterModelList.size() == 0) {
            getView().showProgressDialog();
            RetrofitServiceHelper.getInstance().getStationFilter().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<CameraFilterModel>>>(this) {
                @Override
                public void onCompleted(ResponseResult<List<CameraFilterModel>> cameraFilterRsp) {
                    List<CameraFilterModel> data = cameraFilterRsp.getData();
                    if (data != null) {
                        cameraFilterModelList.addAll(data);
                        for (CameraFilterModel cameraFilterModel : cameraFilterModelList) {
                            if (cameraFilterModel.getKey().equals("type")) {
                                getView().setBaseStationType(cameraFilterModel.getList());
                                break;
                            }
                        }
                    }
                    requestDataByFilter(Constants.DIRECTION_DOWN, null);

                }

                @Override
                public void onErrorMsg(int errorCode, String errorMsg) {
                    getView().toastShort(errorMsg);
                    requestDataByFilter(Constants.DIRECTION_DOWN, null);

                }
            });
        }
    }


    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        List<String> warnList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_BASESTATION, text);
        mSearchHistoryList.clear();
        mSearchHistoryList.addAll(warnList);
        getView().updateSearchHistoryList(mSearchHistoryList);

    }

    public void clearSearchHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_BASESTATION);
        mSearchHistoryList.clear();
        getView().updateSearchHistoryList(mSearchHistoryList);
    }

    @Override
    public void onDestroy() {
        selectedHashMap.clear();
    }

    public void onClickDeviceCamera(final BaseStationInfo deviceCameraInfo) {

        Intent intent = new Intent();
        intent.putExtra("sn", deviceCameraInfo.getSn());
        intent.setClass(mContext, BaseStationDetailActivity.class);
        getView().startAC(intent);

    }


    public void requestDataByFilter(final int direction, String search) {
        switch (direction) {
            case Constants.DIRECTION_DOWN:
                curPage = 1;
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }
                RetrofitServiceHelper.getInstance().getBaseStationListByFilter(Constants.DEFAULT_PAGE_SIZE, curPage, search, selectedHashMap).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<BaseStationInfo>>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<List<BaseStationInfo>> deviceCameraListRsp) {

                        List<BaseStationInfo> data = deviceCameraListRsp.getData();
                        deviceCameraInfos.clear();
                        if (data != null && data.size() > 0) {
                            deviceCameraInfos.addAll(data);
                        }

                        getView().updateBaseStationAdapter(deviceCameraInfos);
                        getView().onPullRefreshComplete();
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        deviceCameraInfos.clear();
                        getView().updateBaseStationAdapter(deviceCameraInfos);
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                        getView().onPullRefreshComplete();

                    }
                });
                break;
            case Constants.DIRECTION_UP:
                curPage++;
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }
                RetrofitServiceHelper.getInstance().getBaseStationListByFilter(Constants.DEFAULT_PAGE_SIZE, curPage, search, selectedHashMap).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<BaseStationInfo>>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<List<BaseStationInfo>> deviceCameraListRsp) {

                        List<BaseStationInfo> data = deviceCameraListRsp.getData();
                        if (data != null && data.size() > 0) {
                            deviceCameraInfos.addAll(data);
                            getView().updateBaseStationAdapter(deviceCameraInfos);
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

    //展示
    public void doShowCameraListFilterPopupWindow(boolean isShowing) {
        //
        getView().setCameraListFilterPopupWindowSelectState(getCameraFilterModelListState());
        if (isShowing) {
            //TODO 当前正在展示,需要消失 考虑回显问题
            getView().dismissCameraListFilterPopupWindow();
        } else {
            //当前已经消失
            //第一次请求数据
            if (cameraFilterModelList.size() == 0) {
                getView().showProgressDialog();
                RetrofitServiceHelper.getInstance().getStationFilter().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<CameraFilterModel>>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<List<CameraFilterModel>> cameraFilterRsp) {
                        List<CameraFilterModel> data = cameraFilterRsp.getData();
                        if (data != null) {
                            cameraFilterModelList.addAll(data);
                        }
                        getView().showCameraListFilterPopupWindow(cameraFilterModelList);
                        getView().dismissProgressDialog();
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
            } else {
                //第二次直接展示
                getView().showCameraListFilterPopupWindow(cameraFilterModelList);

            }
        }
    }

    //判断是否有选中
    private boolean getCameraFilterModelListState() {
        for (CameraFilterModel cameraFilterModel : cameraFilterModelList) {
            List<CameraFilterModel.ListBean> list = cameraFilterModel.getList();
            if (list != null) {
                for (CameraFilterModel.ListBean listBean : list) {
                    if (listBean.isSelect()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    //判断是否有选择
    private boolean getHasSelect() {
        return selectedHashMap.size() > 0;
    }

    //dismiss处理
    public void onCameraListFilterPopupWindowDismiss() {
        if (!getHasSelect()) {
            clearCameraFilterData();
            getView().updateCameraListFilterPopupWindowStatusList(cameraFilterModelList);
        }
        getView().setCameraListFilterPopupWindowSelectState(getHasSelect());
        getView().dismissCameraListFilterPopupWindow();
    }

    //重置
    public void onResetCameraListFilterPopupWindowDismiss(String searchText) {
        selectedHashMap.clear();
        //TODO 清空model数据
        curPage = 1;
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().getBaseStationListByFilter(Constants.DEFAULT_PAGE_SIZE, curPage, searchText, selectedHashMap).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<BaseStationInfo>>>(this) {
            @Override
            public void onCompleted(ResponseResult<List<BaseStationInfo>> deviceCameraListRsp) {

                List<BaseStationInfo> data = deviceCameraListRsp.getData();
                deviceCameraInfos.clear();
                if (data != null && data.size() > 0) {
                    deviceCameraInfos.addAll(data);
                }
                //只在重置成功了进行刷新
                clearCameraFilterData();
                getView().updateCameraListFilterPopupWindowStatusList(cameraFilterModelList);
                getView().updateBaseStationAdapter(deviceCameraInfos);
                getView().onPullRefreshComplete();
                getView().dismissProgressDialog();
                getView().dismissCameraListFilterPopupWindow();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().onPullRefreshComplete();

            }
        });
    }

    private void clearCameraFilterData() {
        for (CameraFilterModel cameraFilterModel : cameraFilterModelList) {
            List<CameraFilterModel.ListBean> list = cameraFilterModel.getList();
            if (list != null) {
                for (CameraFilterModel.ListBean listBean : list) {
                    listBean.setSelect(false);
                }
            }
        }
    }

    //保存
    public void onSaveCameraListFilterPopupWindowDismiss(List<CameraFilterModel> list, String searchText) {
        selectedHashMap.clear();
        selectedHashMap.putAll(handleCameraListFilter(list));
        curPage = 1;
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().getBaseStationListByFilter(Constants.DEFAULT_PAGE_SIZE, curPage, searchText, selectedHashMap).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<BaseStationInfo>>>(this) {
            @Override
            public void onCompleted(ResponseResult<List<BaseStationInfo>> deviceCameraListRsp) {

                List<BaseStationInfo> data = deviceCameraListRsp.getData();
                deviceCameraInfos.clear();
                if (data != null && data.size() > 0) {
                    deviceCameraInfos.addAll(data);
                }
                getView().updateBaseStationAdapter(deviceCameraInfos);
                getView().setCameraListFilterPopupWindowSelectState(getHasSelect());
                getView().onPullRefreshComplete();
                getView().dismissProgressDialog();
                getView().dismissCameraListFilterPopupWindow();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
                getView().onPullRefreshComplete();

            }
        });
    }

    //处理集合数据
    private HashMap<String, String> handleCameraListFilter(List<CameraFilterModel> list) {
        HashMap<String, String> hashMap = new HashMap();
        if (null != list) {
            for (CameraFilterModel model : list) {
                String key = model.getKey();
                StringBuffer stringBuffer = new StringBuffer();
                for (CameraFilterModel.ListBean listBean : model.getList()) {
                    if (listBean.isSelect()) {
                        stringBuffer.append(listBean.getCode());
                        stringBuffer.append(",");
                    }
                }
                if (!TextUtils.isEmpty(stringBuffer.toString())) {
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1);
                    hashMap.put(key, stringBuffer.toString());
                }
            }
        }
        return hashMap;
    }
}
