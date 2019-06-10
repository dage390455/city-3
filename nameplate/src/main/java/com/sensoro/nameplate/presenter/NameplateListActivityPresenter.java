package com.sensoro.nameplate.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.model.CameraFilterModel;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceCameraDetailInfo;
import com.sensoro.common.server.bean.NamePlateInfo;
import com.sensoro.common.server.response.CameraFilterRsp;
import com.sensoro.common.server.response.DeleteNamePlateRsp;
import com.sensoro.common.server.response.DeviceCameraDetailRsp;
import com.sensoro.common.server.response.NamePlateListRsp;
import com.sensoro.common.utils.StringUtils;
import com.sensoro.nameplate.IMainViews.INameplateListActivityView;
import com.sensoro.nameplate.R;
import com.sensoro.nameplate.activity.NameplateDetailActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class NameplateListActivityPresenter extends BasePresenter<INameplateListActivityView> implements Constants {
    private Activity mContext;
    private volatile int cur_page = 1;
    private final List<NamePlateInfo> plateInfos = new ArrayList<>();
    private final List<String> mSearchHistoryList = new ArrayList<>();

    private final List<CameraFilterModel> cameraFilterModelList = new ArrayList<>();


    private final HashMap<String, String> selectedHashMap = new HashMap<String, String>();

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        Serializable serializableExtra = mContext.getIntent().getSerializableExtra(EXTRA_DEVICE_CAMERA_DETAIL_INFO_LIST);
        if (serializableExtra instanceof ArrayList) {
            getView().setSmartRefreshEnable(false);
            plateInfos.clear();
            List<NamePlateInfo> data = (List<NamePlateInfo>) serializableExtra;
            plateInfos.addAll(data);
            getView().updateDeviceCameraAdapter(plateInfos);
            getView().onPullRefreshComplete();
            getView().dismissProgressDialog();
        } else {
            requestDataByFilter(DIRECTION_DOWN, null);
        }
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_CAMERA_LIST);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().updateSearchHistoryList(mSearchHistoryList);
        }
    }

    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
        List<String> warnList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_CAMERA_LIST, text);
        mSearchHistoryList.clear();
        mSearchHistoryList.addAll(warnList);
        getView().updateSearchHistoryList(mSearchHistoryList);

    }

    public void clearSearchHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_CAMERA_LIST);
        mSearchHistoryList.clear();
        getView().updateSearchHistoryList(mSearchHistoryList);
    }

    @Override
    public void onDestroy() {
        selectedHashMap.clear();
    }

    public void onClickDeviceCamera(final NamePlateInfo NamePlateInfo) {
        final String sn = NamePlateInfo.getSn();
        final String cid = NamePlateInfo.getCid();
        getView().showProgressDialog();
        RetrofitServiceHelper.getInstance().getDeviceCamera(sn).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceCameraDetailRsp>(this) {
            @Override
            public void onCompleted(DeviceCameraDetailRsp deviceCameraDetailRsp) {
                DeviceCameraDetailInfo data = deviceCameraDetailRsp.getData();
                if (data != null) {
                    String hls = data.getHls();
                    DeviceCameraDetailInfo.CameraBean camera = data.getCamera();
                    String lastCover = data.getLastCover();
                    Intent intent = new Intent();
                    //TODO
//                    intent.setClass(mContext, CameraDetailActivity.class);
                    intent.putExtra("cid", cid);
                    intent.putExtra("hls", hls);
                    intent.putExtra("sn", sn);
                    if (camera != null) {
                        String name = camera.getName();
                        intent.putExtra("cameraName", name);
                    }
                    intent.putExtra("lastCover", lastCover);
                    intent.putExtra("deviceStatus", data.getDeviceStatus());
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
            case DIRECTION_DOWN:
                cur_page = 1;
                if (isAttachedView()) {
                    getView().showProgressDialog();
                }
                RetrofitServiceHelper.getInstance().getNameplateList(20, cur_page, search, selectedHashMap).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<NamePlateListRsp>(this) {
                    @Override
                    public void onCompleted(NamePlateListRsp deviceCameraListRsp) {

                        List<NamePlateInfo> data = deviceCameraListRsp.getData();
                        plateInfos.clear();
                        if (data != null && data.size() > 0) {
                            plateInfos.addAll(data);
                        }
                        getView().updateDeviceCameraAdapter(plateInfos);
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
                RetrofitServiceHelper.getInstance().getNameplateList(20, cur_page, search, selectedHashMap).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<NamePlateListRsp>(this) {
                    @Override
                    public void onCompleted(NamePlateListRsp deviceCameraListRsp) {

                        List<NamePlateInfo> data = deviceCameraListRsp.getData();
                        if (data != null && data.size() > 0) {
                            plateInfos.addAll(data);
                            getView().updateDeviceCameraAdapter(plateInfos);
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
                RetrofitServiceHelper.getInstance().getCameraFilter().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<CameraFilterRsp>(this) {
                    @Override
                    public void onCompleted(CameraFilterRsp cameraFilterRsp) {
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
        cur_page = 1;
        if (isAttachedView()) {
            getView().showProgressDialog();
        }
        RetrofitServiceHelper.getInstance().getNameplateList(20, cur_page, searchText, selectedHashMap).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<NamePlateListRsp>(this) {
            @Override
            public void onCompleted(NamePlateListRsp deviceCameraListRsp) {

                List<NamePlateInfo> data = deviceCameraListRsp.getData();
                plateInfos.clear();
                if (data != null && data.size() > 0) {
                    plateInfos.addAll(data);
                }
                //只在重置成功了进行刷新
                clearCameraFilterData();
                getView().updateCameraListFilterPopupWindowStatusList(cameraFilterModelList);
                getView().updateDeviceCameraAdapter(plateInfos);
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
        RetrofitServiceHelper.getInstance().getNameplateList(20, cur_page, searchText, selectedHashMap).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<NamePlateListRsp>(this) {
            @Override
            public void onCompleted(NamePlateListRsp deviceCameraListRsp) {

                List<NamePlateInfo> data = deviceCameraListRsp.getData();
                plateInfos.clear();
                if (data != null && data.size() > 0) {
                    plateInfos.addAll(data);
                }
                getView().updateDeviceCameraAdapter(plateInfos);
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
            RetrofitServiceHelper.getInstance().deleteNameplate(plateInfos.get(position).getId()).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeleteNamePlateRsp>(this) {
                @Override
                public void onCompleted(DeleteNamePlateRsp deviceCameraListRsp) {

                    Integer data = deviceCameraListRsp.getData();

                    if (data == 1) {
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
                if (!StringUtils.isEmpty(stringBuffer.toString())) {
                    stringBuffer.deleteCharAt(stringBuffer.length() - 1).toString();
                    hashMap.put(key, stringBuffer.toString());
                }
            }
        }
        return hashMap;
    }

    public void doNameplateDetail(int position) {

        if (null != plateInfos.get(position)) {
            getView().startAC(new Intent(mContext, NameplateDetailActivity.class).putExtra("nameplateId", plateInfos.get(position).getId()));
        }

    }
}
