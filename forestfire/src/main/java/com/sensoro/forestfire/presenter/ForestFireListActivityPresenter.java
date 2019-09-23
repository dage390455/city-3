package com.sensoro.forestfire.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceCameraDetailInfo;
import com.sensoro.common.server.bean.ForestFireCameraDetailInfo;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.forestfire.Constants.ForestFireConstans;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.activity.ForestFireCameraDetailActivity;
import com.sensoro.forestfire.imainviews.IForestFireCameraListActivityView;
import com.sensoro.common.server.bean.ForestFireCameraListInfo;
import com.sensoro.common.server.bean.ForestFireCameraBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * @Author: jack
 * 时  间: 2019-09-17
 * 包  名: com.sensoro.forestfire.presenter
 * 简  述: <功能简述>
 */
public class ForestFireListActivityPresenter extends BasePresenter<IForestFireCameraListActivityView> {
    private Activity mContext;
    private volatile int cur_page = 1;
    private final List<ForestFireCameraBean> deviceCameraInfos = new ArrayList<>();
    private final List<String> mSearchHistoryList = new ArrayList<>();

    private final List<CameraFilterModel> cameraFilterModelList = new ArrayList<>();


    private final HashMap<String, String> selectedHashMap = new HashMap<String, String>();


    @Override
    public void initData(Context context) {
        mContext = (Activity) context;

        initFilterList();
        Serializable serializableExtra = mContext.getIntent().getSerializableExtra(Constants.EXTRA_DEVICE_CAMERA_DETAIL_INFO_LIST);
        if (serializableExtra instanceof ArrayList) {
            getView().setSmartRefreshEnable(false);
            deviceCameraInfos.clear();
            List<ForestFireCameraBean> data = (List<ForestFireCameraBean>) serializableExtra;
            deviceCameraInfos.addAll(data);
            getView().updateDeviceCameraAdapter(deviceCameraInfos);
            getView().onPullRefreshComplete();
            getView().dismissProgressDialog();
            getView().setTopTitleState();
        } else {
            requestDataByFilter(Constants.DIRECTION_DOWN, null);
        }


        requestDataByFilter(Constants.DIRECTION_DOWN, null);
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_FOREST_FIRE_CAMERA_LIST);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().updateSearchHistoryList(mSearchHistoryList);
        }

        EventBus.getDefault().register(this);
    }


    private void initFilterList(){
        CameraFilterModel mCameraFilterModel=   new CameraFilterModel();


        List<CameraFilterModel.ListBean>  beanList=new ArrayList<>();
        mCameraFilterModel.setKey("");
        mCameraFilterModel.setMulti(false);
        mCameraFilterModel.setTitle(mContext.getString(R.string.camera_state));


        CameraFilterModel.ListBean mListBean=new CameraFilterModel.ListBean();

        mListBean.setCode("");
        mListBean.setSelect(true);
        mListBean.setTitle(mContext.getString(R.string.word_all));
        mListBean.setName(mContext.getString(R.string.word_all));
        beanList.add(mListBean);

        mListBean=new CameraFilterModel.ListBean();
        mListBean.setCode(String.valueOf(ForestFireConstans.FOREST_STATE_OFFLINE));
        mListBean.setSelect(false);
        mListBean.setTitle(mContext.getString(R.string.offline));
        mListBean.setName(mContext.getString(R.string.offline));
        beanList.add(mListBean);

        mListBean=new CameraFilterModel.ListBean();
        mListBean.setCode(String.valueOf(ForestFireConstans.FOREST_STATE_ONLINE));
        mListBean.setSelect(false);
        mListBean.setTitle(mContext.getString(R.string.online));
        mListBean.setName(mContext.getString(R.string.online));
        beanList.add(mListBean);

        mCameraFilterModel.setListX(beanList);
        cameraFilterModelList.add(mCameraFilterModel);


    }

    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        List<String> warnList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_FOREST_FIRE_CAMERA_LIST, text);
        mSearchHistoryList.clear();
        mSearchHistoryList.addAll(warnList);
        getView().updateSearchHistoryList(mSearchHistoryList);

    }

    public void clearSearchHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_FOREST_FIRE_CAMERA_LIST);
        mSearchHistoryList.clear();
        getView().updateSearchHistoryList(mSearchHistoryList);
    }

    @Override
    public void onDestroy() {
        selectedHashMap.clear();
        EventBus.getDefault().unregister(this);
    }

    public void onClickDeviceCamera(final ForestFireCameraBean deviceCameraInfo) {
        final String sn = deviceCameraInfo.getSn();
//        final String cid = deviceCameraInfo.getCid();
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getForestFireDeviceCameraDetail(sn).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<ForestFireCameraDetailInfo>>(this) {
            @Override
            public void onCompleted(ResponseResult<ForestFireCameraDetailInfo> deviceCameraDetailRsp) {
                ForestFireCameraDetailInfo data = deviceCameraDetailRsp.getData();
                if (data != null) {
                    Intent intent = new Intent();
                    intent.setClass(mContext, ForestFireCameraDetailActivity.class);

                    intent.putExtra(ForestFireConstans.DEVICE_CAMERA_INFO,deviceCameraInfo);
                    intent.putExtra(ForestFireConstans.DEVICE_CAMERA_DETAIL,data);
                    getView().startAC(intent);
                } else {
                    getView().toastShort(mContext.getString(R.string.camera_info_get_failed));
                }

                getView().dismissProgressDialog();

            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });


    }


    public void requestDataByFilter(final int direction, String search) {
        switch (direction) {
            case Constants.DIRECTION_DOWN:
                cur_page = 1;
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }

                RetrofitServiceHelper.getInstance().getForestFireDeviceCameraListByFilter(Constants.DEFAULT_PAGE_SIZE, cur_page, search, selectedHashMap).subscribeOn
                        (Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<ForestFireCameraListInfo>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<ForestFireCameraListInfo> forestFireGatewayInfoResponseResult) {
                        ForestFireCameraListInfo data = forestFireGatewayInfoResponseResult.getData();
                        deviceCameraInfos.clear();
                        if (data != null && data.getList() != null && data.getList().size() > 0) {
                            deviceCameraInfos.addAll(data.getList());
                        }
                        getView().updateDeviceCameraAdapter(deviceCameraInfos);
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
            case Constants.DIRECTION_UP:
                cur_page++;
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }


                RetrofitServiceHelper.getInstance().getForestFireDeviceCameraListByFilter(Constants.DEFAULT_PAGE_SIZE, cur_page, search, selectedHashMap).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<ForestFireCameraListInfo>>(this) {
                    @Override
                    public void onCompleted(ResponseResult<ForestFireCameraListInfo> deviceCameraListRsp) {

                        ForestFireCameraListInfo data = deviceCameraListRsp.getData();
                        if (data != null && data.getList() != null && data.getList().size() > 0) {
                            deviceCameraInfos.addAll(data.getList());
                            getView().updateDeviceCameraAdapter(deviceCameraInfos);
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
//            if (cameraFilterModelList.size() == 0) {
//                getView().showProgressDialog();
//                RetrofitServiceHelper.getInstance().getCameraFilter().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<CameraFilterModel>>>(this) {
//                    @Override
//                    public void onCompleted(ResponseResult<List<CameraFilterModel>> cameraFilterRsp) {
//                        cameraFilterModelList.clear();
//                        List<CameraFilterModel> data = cameraFilterRsp.getData();
//                        if (data != null) {
//                            cameraFilterModelList.addAll(data);
//                        }
//                        getView().showCameraListFilterPopupWindow(cameraFilterModelList);
//                        getView().dismissProgressDialog();
//                    }
//
//                    @Override
//                    public void onErrorMsg(int errorCode, String errorMsg) {
//                        getView().dismissProgressDialog();
//                        getView().toastShort(errorMsg);
//                    }
//                });
//            } else {
//                //第二次直接展示
//                getView().showCameraListFilterPopupWindow(cameraFilterModelList);
//
//            }
            getView().showCameraListFilterPopupWindow(cameraFilterModelList);
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
        cur_page = 1;
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().getForestFireDeviceCameraListByFilter(Constants.DEFAULT_PAGE_SIZE, cur_page, searchText, selectedHashMap).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<ForestFireCameraListInfo>>(this) {
            @Override
            public void onCompleted(ResponseResult<ForestFireCameraListInfo> deviceCameraListRsp) {

                ForestFireCameraListInfo data = deviceCameraListRsp.getData();
                deviceCameraInfos.clear();
                if (data != null && data.getList() != null && data.getList().size() > 0) {
                    deviceCameraInfos.addAll(data.getList());
                }
                //只在重置成功了进行刷新
                clearCameraFilterData();
                getView().updateCameraListFilterPopupWindowStatusList(cameraFilterModelList);
                getView().updateDeviceCameraAdapter(deviceCameraInfos);
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
        cur_page = 1;
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().getForestFireDeviceCameraListByFilter(Constants.DEFAULT_PAGE_SIZE, cur_page, searchText, selectedHashMap).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<ForestFireCameraListInfo>>(this) {
            @Override
            public void onCompleted(ResponseResult<ForestFireCameraListInfo> deviceCameraListRsp) {

                ForestFireCameraListInfo data = deviceCameraListRsp.getData();

                deviceCameraInfos.clear();
                if (data != null && data.getList() != null && data.getList().size() > 0) {
                    deviceCameraInfos.addAll(data.getList());
                }
                getView().updateDeviceCameraAdapter(deviceCameraInfos);
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
//    private HashMap<String, String> handleCameraListFilter(List<CameraFilterModel> list) {
//        HashMap<String, String> hashMap = new HashMap();
//        if (null != list) {
//            for (CameraFilterModel model : list) {
//                String key = model.getKey();
//                StringBuffer stringBuffer = new StringBuffer();
//                for (CameraFilterModel.ListBean listBean : model.getList()) {
//                    if (listBean.isSelect()) {
//                        stringBuffer.append(listBean.getCode());
//                        stringBuffer.append(",");
//                    }
//                }
//                if (!TextUtils.isEmpty(stringBuffer.toString())) {
//                    stringBuffer.deleteCharAt(stringBuffer.length() - 1).toString();
//                    hashMap.put(key, stringBuffer.toString());
//                }
//            }
//        }
//        return hashMap;
//    }

    //处理集合数据
    private HashMap<String, String> handleCameraListFilter(List<CameraFilterModel> list) {
        HashMap<String, String> hashMap = new HashMap();
        if (null != list) {
            String key = "deviceStatus";
            for (CameraFilterModel model : list) {

                StringBuffer stringBuffer = new StringBuffer();
                for (CameraFilterModel.ListBean listBean : model.getList()) {
                    if (listBean.isSelect()) {
                        stringBuffer.append(listBean.getCode());
                        stringBuffer.append(",");
                    }
                }
                if (!TextUtils.isEmpty(stringBuffer.toString())) {
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1).toString();
                    if(stringBuffer.length()>0) {
                        hashMap.put(key, stringBuffer.toString());
                    }
                }
            }
        }
        return hashMap;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ForestFireCameraBean  mForestFireCameraBean){
        getView().updateDeviceCameraAdapterByItem(mForestFireCameraBean);
    }
}
