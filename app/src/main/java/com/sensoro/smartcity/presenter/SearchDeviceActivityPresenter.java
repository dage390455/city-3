package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.activity.SensorDetailActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISearchDeviceActivityView;
import com.sensoro.smartcity.iwidget.IOnStart;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceInfo;
import com.sensoro.smartcity.server.response.CityObserver;
import com.sensoro.smartcity.server.response.DeviceInfoListRsp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchDeviceActivityPresenter extends BasePresenter<ISearchDeviceActivityView> implements Constants,
        IOnStart {
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private String history;

    public List<String> getHistoryKeywords() {
        return mHistoryKeywords;
    }

    private final List<String> mHistoryKeywords = new ArrayList<>();
    private int mTypeSelectedIndex = 0;
    private int mStatusSelectedIndex = 0;

    public void setTypeSelectedIndex(int mTypeSelectedIndex) {
        this.mTypeSelectedIndex = mTypeSelectedIndex;
    }

    public void setStatusSelectedIndex(int mStatusSelectedIndex) {
        this.mStatusSelectedIndex = mStatusSelectedIndex;
    }

    private int page = 1;
    private final List<DeviceInfo> mDataList = new ArrayList<>();
    private final List<DeviceInfo> originHistoryList = new ArrayList<>();
    private final List<DeviceInfo> currentList = new ArrayList<>();

    private final List<String> searchStrList = new ArrayList<>();
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        originHistoryList.addAll(SensoroCityApplication.getInstance().getData());
        currentList.addAll(originHistoryList);
        mPref = mContext.getSharedPreferences(PREFERENCE_DEVICE_HISTORY, Activity.MODE_PRIVATE);
        mEditor = mPref.edit();
        history = mPref.getString(PREFERENCE_KEY_DEVICE, "");
    }

    @Override
    public void onStart() {
        if (!TextUtils.isEmpty(history)) {
            mHistoryKeywords.clear();
            mHistoryKeywords.addAll(Arrays.asList(history.split(",")));
        }

        boolean hasHistory = mHistoryKeywords.size() > 0;
        if (hasHistory) {
            getView().updateSearchHistoryData();
        }
        getView().setSearchHistoryLayoutVisible(hasHistory);
    }

    public void clickRelationItem(int position) {
        String s = searchStrList.get(position);
        save(s);
        requestWithDirection(DIRECTION_DOWN, s);
    }


    private void refreshCacheData() {
        this.mDataList.clear();
//        this.mDataList.clear();
//        this.mRelationLayout.setVisibility(View.GONE);
//        this.mIndexListLayout.setVisibility(VISIBLE);
        for (int i = 0; i < currentList.size(); i++) {
            DeviceInfo deviceInfo = currentList.get(i);
            switch (deviceInfo.getStatus()) {
                case SENSOR_STATUS_ALARM:
                    deviceInfo.setSort(1);
                    break;
                case SENSOR_STATUS_NORMAL:
                    deviceInfo.setSort(2);
                    break;
                case SENSOR_STATUS_LOST:
                    deviceInfo.setSort(3);
                    break;
                case SENSOR_STATUS_INACTIVE:
                    deviceInfo.setSort(4);
                    break;
                default:
                    break;
            }
            if (isMatcher(deviceInfo)) {
                mDataList.add(deviceInfo);
            }
        }
        getView().refreshData(mDataList);
    }

    private boolean isMatcher(DeviceInfo deviceInfo) {
        if (mTypeSelectedIndex == 0 && mStatusSelectedIndex == 0) {
            return true;
        } else {
            boolean isMatcherType = false;
            boolean isMatcherStatus = false;
            String unionType = deviceInfo.getUnionType();
            if (unionType != null) {
                String[] unionTypeArray = unionType.split("\\|");
                List<String> unionTypeList = Arrays.asList(unionTypeArray);
                String[] menuTypeArray = SENSOR_MENU_ARRAY[mTypeSelectedIndex].split("\\|");
                if (mTypeSelectedIndex == 0) {
                    isMatcherType = true;
                } else {
                    for (int j = 0; j < menuTypeArray.length; j++) {
                        String menuType = menuTypeArray[j];
                        if (unionTypeList.contains(menuType)) {
                            isMatcherType = true;
                            break;
                        }
                    }
                }
            }
            if (mStatusSelectedIndex != 0) {
                int status = INDEX_STATUS_VALUES[mStatusSelectedIndex - 1];
                if (deviceInfo.getStatus() == status) {
                    isMatcherStatus = true;
                }
            } else {
                isMatcherStatus = true;
            }
            return isMatcherStatus && isMatcherType;
        }
    }

    public void filterDeviceInfo(String filter) {
        List<DeviceInfo> originDeviceInfoList = new ArrayList<>();
        originDeviceInfoList.addAll(originHistoryList);
        ArrayList<DeviceInfo> deleteDeviceInfoList = new ArrayList<>();
        for (DeviceInfo deviceInfo : originDeviceInfoList) {
            String name = deviceInfo.getName();
            if (!TextUtils.isEmpty(name)) {
                if (!(name.contains(filter.toUpperCase()))) {
                    deleteDeviceInfoList.add(deviceInfo);
                }
            } else {
                deleteDeviceInfoList.add(deviceInfo);
            }

        }
        originDeviceInfoList.removeAll(deleteDeviceInfoList);
        List<String> tempList = new ArrayList<>();
        for (DeviceInfo deviceInfo : originDeviceInfoList) {
            String name = deviceInfo.getName();
            if (!TextUtils.isEmpty(name)) {
                tempList.add(name);
            }
        }
        searchStrList.clear();
        searchStrList.addAll(tempList);
        getView().updateRelationData(tempList);
        originDeviceInfoList.clear();
        tempList.clear();
        deleteDeviceInfoList.clear();


    }

    public void save(String text) {
        String oldText = mPref.getString(PREFERENCE_KEY_DEVICE, "");
        //
        List<String> oldHistoryList = new ArrayList<String>();
        if (!TextUtils.isEmpty(oldText)) {
            oldHistoryList.addAll(Arrays.asList(oldText.split(",")));
        }
        if (!oldHistoryList.contains(text)) {
            oldHistoryList.add(0, text);
        }
        ArrayList<String> tempList = new ArrayList<>();
        for (String str : oldHistoryList) {
            if (tempList.size() < 20) {
                tempList.add(str);
            }
        }
        mHistoryKeywords.clear();
        mHistoryKeywords.addAll(tempList);
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < tempList.size(); i++) {
            if (i == (tempList.size() - 1)) {
                stringBuffer.append(tempList.get(i));
            } else {
                stringBuffer.append(tempList.get(i) + ",");
            }
        }
        mEditor.putString(PREFERENCE_KEY_DEVICE, stringBuffer.toString());
        mEditor.commit();
        //
//        if (TextUtils.isEmpty(oldText)) {
//            mEditor.putString(PREFERENCE_KEY_DEVICE, text);
//        } else {
//            if (!TextUtils.isEmpty(text)) {
//                if (mHistoryKeywords.contains(text)) {
//                    List<String> list = new ArrayList<String>();
//                    for (String o : oldText.split(",")) {
//                        if (!o.equalsIgnoreCase(text)) {
//                            list.add(o);
//                        }
//                    }
//                    list.add(0, text);
//
//                    ArrayList<String> tempList = new ArrayList<>();
//                    for (String str : list) {
//                        if (tempList.size() < 20) {
//                            tempList.add(str);
//                        }
//                    }
//                    mHistoryKeywords.clear();
//                    mHistoryKeywords.addAll(tempList);
//                    StringBuffer stringBuffer = new StringBuffer();
//                    for (int i = 0; i < tempList.size(); i++) {
//                        if (i == (tempList.size() - 1)) {
//                            stringBuffer.append(tempList.get(i));
//                        } else {
//                            stringBuffer.append(tempList.get(i) + ",");
//                        }
//                    }
//                    mEditor.putString(PREFERENCE_KEY_DEVICE, stringBuffer.toString());
//                    mEditor.commit();
//                } else {
//                    mHistoryKeywords.add(0, text);
//                    ArrayList<String> tempList = new ArrayList<>();
//                    for (String str : mHistoryKeywords) {
//                        if (tempList.size() < 20) {
//                            tempList.add(str);
//                        }
//                    }
//                    mHistoryKeywords.clear();
//                    mHistoryKeywords.addAll(tempList);
//
//                    StringBuffer stringBuffer = new StringBuffer();
//                    for (int i = 0; i < tempList.size(); i++) {
//                        if (i == (tempList.size() - 1)) {
//                            stringBuffer.append(tempList.get(i));
//                        } else {
//                            stringBuffer.append(tempList.get(i) + ",");
//                        }
//                    }
//                    mEditor.putString(PREFERENCE_KEY_DEVICE, stringBuffer.toString());
//                    mEditor.commit();
//                }
//            }
//        }

    }

    public void cleanHistory() {
        mEditor.clear();
        mHistoryKeywords.clear();
        mEditor.commit();
        getView().updateSearchHistoryData();
        getView().setSearchHistoryLayoutVisible(false);
    }

    public void requestWithDirection(int direction, String searchText) {
        getView().setSearchHistoryLayoutVisible(false);
        getView().setRelationLayoutVisible(false);
        getView().setIndexListLayoutVisible(true);
        String type = mTypeSelectedIndex == 0 ? null : INDEX_TYPE_VALUES[mTypeSelectedIndex];
        Integer status = mStatusSelectedIndex == 0 ? null : INDEX_STATUS_VALUES[mStatusSelectedIndex - 1];
        getView().showProgressDialog();
        if (direction == DIRECTION_DOWN) {
            page = 1;
            RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, type, status, searchText).subscribeOn
                    (Schedulers.io
                            ()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {


                @Override
                public void onCompleted() {
                    getView().dismissProgressDialog();
                    getView().recycleViewRefreshComplete();
                }

                @Override
                public void onNext(DeviceInfoListRsp deviceInfoListRsp) {
                    try {
                        if (deviceInfoListRsp.getData().size() == 0) {
                            getView().setTipsLinearLayoutVisible(true);
                            mDataList.clear();
                            getView().refreshData(mDataList);
                        } else {
                            currentList.clear();
                            currentList.addAll(deviceInfoListRsp.getData());
                            refreshCacheData();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorMsg(String errorMsg) {
                    getView().dismissProgressDialog();
                    getView().recycleViewRefreshComplete();
                    getView().toastShort(errorMsg);
                }
            });
        } else {
            page++;
            RetrofitServiceHelper.INSTANCE.getDeviceBriefInfoList(page, type, status, searchText).subscribeOn
                    (Schedulers.io
                            ()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceInfoListRsp>() {


                @Override
                public void onCompleted() {
                    getView().dismissProgressDialog();
                    getView().recycleViewRefreshComplete();
                }

                @Override
                public void onNext(DeviceInfoListRsp deviceInfoListRsp) {
                    try {
                        if (deviceInfoListRsp.getData().size() == 0) {
                            page--;
                        } else {
                            currentList.addAll(deviceInfoListRsp.getData());
                            refreshCacheData();
                        }
                    } catch (Exception e) {
                        page--;
                        e.printStackTrace();
                    }
                }

                @Override
                public void onErrorMsg(String errorMsg) {
                    page--;
                    getView().dismissProgressDialog();
                    getView().recycleViewRefreshComplete();
                    getView().toastShort(errorMsg);
                }
            });
        }
    }

    public void clickItem(int position) {
        //            Log.e(TAG, "onItemClick: ", );
        int index = position - 1;
        if (position >= 0) {
//            int size = mDataList.size();
////            Log.e("", "onItemClick: "+mDataList.size());
            DeviceInfo deviceInfo = mDataList.get(index);
            Intent intent = new Intent(mContext, SensorDetailActivity.class);
            intent.putExtra(EXTRA_DEVICE_INFO, deviceInfo);
            intent.putExtra(EXTRA_SENSOR_NAME, deviceInfo.getName());
            intent.putExtra(EXTRA_SENSOR_TYPES, deviceInfo.getSensorTypes());
            intent.putExtra(EXTRA_SENSOR_STATUS, deviceInfo.getStatus());
            intent.putExtra(EXTRA_SENSOR_TIME, deviceInfo.getUpdatedTime());
            intent.putExtra(EXTRA_SENSOR_LOCATION, deviceInfo.getLonlat());
            getView().startAC(intent);

        }
    }

    public void switchIndexGridOrList(int switchType) {
        page = 1;
        if (switchType == TYPE_LIST) {
            getView().switchToTypeGrid();
        } else {
            getView().switchToTypeList();
        }
        getView().returnTop();
    }


}
