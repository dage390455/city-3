package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.smartcity.SensoroCityApplication;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.ISearchAlarmActivityView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.SearchAlarmResultModel;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.response.DeviceAlarmLogRsp;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchAlarmActivityPresenter extends BasePresenter<ISearchAlarmActivityView> implements Constants {
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;

    private final List<String> mHistoryKeywords_deviceName = new ArrayList<>();
    private final List<String> mHistoryKeywords_deviceNumber = new ArrayList<>();
    private final List<String> mHistoryKeywords_devicePhone = new ArrayList<>();
    //    private SearchAlarmTagAdapter mAlarmTagAdapter;
    //    private SearchAlarmPagerAdapter searchAlarmPagerAdapter;
    private Long mStartTime = null;
    private Long mEndTime = null;
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mPref = mContext.getSharedPreferences(PREFERENCE_ALARM_SEARCH_HISTORY, Activity.MODE_PRIVATE);
        long longStartTime = mContext.getIntent().getLongExtra(PREFERENCE_KEY_START_TIME, -1);
        long longEndTime = mContext.getIntent().getLongExtra(PREFERENCE_KEY_END_TIME, -1);
        if (longStartTime != -1) {
            mStartTime = longStartTime;
        }
        if (longEndTime != -1) {
            mEndTime = longEndTime;
        }
        mEditor = mPref.edit();
    }

    public List<String> getHistoryKeywords_deviceName() {
        return mHistoryKeywords_deviceName;
    }

    /**
     * 刷新数据
     */
    public void refreshHistory(int searchType) {
        List<String> searchStr;
        String ori;
        switch (searchType) {
            case Constants.TYPE_DEVICE_NAME:
                searchStr = mHistoryKeywords_deviceName;
                ori = mPref.getString(PREFERENCE_KEY_DEVICE_NAME, "");
                break;
            case Constants.TYPE_DEVICE_SN:
                searchStr = mHistoryKeywords_deviceNumber;
                ori = mPref.getString(PREFERENCE_KEY_DEVICE_NUM, "");
                break;
            case Constants.TYPE_DEVICE_PHONE_NUM:
                searchStr = mHistoryKeywords_devicePhone;
                ori = mPref.getString(PREFERENCE_KEY_DEVICE_PHONE, "");
                break;
            default:
                searchStr = mHistoryKeywords_deviceName;
                ori = mPref.getString(PREFERENCE_KEY_DEVICE_NAME, "");
                break;
        }
        searchStr.clear();
        if (!TextUtils.isEmpty(ori)) {
            List<String> strings = Arrays.asList(ori.split(","));
            searchStr.addAll(strings);
        }
        getView().updateSearchHistory(searchStr);
    }

    public void requestData(final int searchType, final String text) {
        switch (searchType) {
            case Constants.TYPE_DEVICE_NAME:
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(1, null, text, null, mStartTime, mEndTime, null)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        getView().dismissProgressDialog();
                        if (deviceAlarmLogRsp.getData().size() == 0) {
                            getView().setTipsLinearLayoutVisible(true);
                        } else {
                            SensoroCityApplication.getInstance().saveSearchType = searchType;
                            EventData eventData = new EventData();
                            eventData.code= EVENT_DATA_SEARCH_ALARM_RESULT;
                            SearchAlarmResultModel searchAlarmResultModel = new SearchAlarmResultModel();
                            searchAlarmResultModel.searchAlarmText=text;
                            searchAlarmResultModel.deviceAlarmLogRsp=deviceAlarmLogRsp;
                            eventData.data=searchAlarmResultModel;
                            EventBus.getDefault().post(eventData);

//                            Intent data = new Intent();
//                            data.putExtra(EXTRA_ALARM_INFO, deviceAlarmLogRsp);
//                            data.putExtra(EXTRA_ALARM_SEARCH_INDEX, 0);
//                            data.putExtra(EXTRA_ALARM_SEARCH_TEXT, text);
//                            getView().setIntentResult(RESULT_CODE_SEARCH_ALARM, data);
                            getView().finishAc();
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case Constants.TYPE_DEVICE_SN:
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(1, text, null, null, mStartTime, mEndTime, null)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onNext(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        getView().dismissProgressDialog();
                        if (deviceAlarmLogRsp.getData().size() == 0) {
                            getView().setTipsLinearLayoutVisible(true);
//                            tagLinearLayout.setVisibility(View.GONE);
                        } else {
                            SensoroCityApplication.getInstance().saveSearchType = searchType;
                            //
                            EventData eventData = new EventData();
                            eventData.code=EVENT_DATA_SEARCH_ALARM_RESULT;
                            SearchAlarmResultModel searchAlarmResultModel = new SearchAlarmResultModel();
                            searchAlarmResultModel.searchAlarmText=text;
                            searchAlarmResultModel.deviceAlarmLogRsp=deviceAlarmLogRsp;
                            eventData.data=searchAlarmResultModel;
                            EventBus.getDefault().post(eventData);
                            getView().finishAc();
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            case Constants.TYPE_DEVICE_PHONE_NUM:
                getView().showProgressDialog();
                RetrofitServiceHelper.INSTANCE.getDeviceAlarmLogList(1, null, null, text, mStartTime, mEndTime, null)
                        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DeviceAlarmLogRsp>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onNext(DeviceAlarmLogRsp deviceAlarmLogRsp) {
                        getView().dismissProgressDialog();
                        if (deviceAlarmLogRsp.getData().size() == 0) {
                            getView().setTipsLinearLayoutVisible(true);
//                            tagLinearLayout.setVisibility(View.GONE);
                        } else {
                            SensoroCityApplication.getInstance().saveSearchType = searchType;
                            EventData eventData = new EventData();
                            eventData.code=EVENT_DATA_SEARCH_ALARM_RESULT;
                            SearchAlarmResultModel searchAlarmResultModel = new SearchAlarmResultModel();
                            searchAlarmResultModel.searchAlarmText=text;
                            searchAlarmResultModel.deviceAlarmLogRsp=deviceAlarmLogRsp;
                            eventData.data=searchAlarmResultModel;
                            EventBus.getDefault().post(eventData);
                            //
                            getView().finishAc();
                        }
                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                    }
                });
                break;
            default:
                break;
        }

    }

    public void cleanHistory(int searchType) {
        switch (searchType) {
            case Constants.TYPE_DEVICE_NAME:
                mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, "");
                mHistoryKeywords_deviceName.clear();
                getView().updateSearchHistory(mHistoryKeywords_deviceName);
                break;
            case Constants.TYPE_DEVICE_SN:
                mEditor.putString(PREFERENCE_KEY_DEVICE_NUM, "");
                mHistoryKeywords_deviceNumber.clear();
                getView().updateSearchHistory(mHistoryKeywords_deviceNumber);
                break;
            case Constants.TYPE_DEVICE_PHONE_NUM:
                mEditor.putString(PREFERENCE_KEY_DEVICE_PHONE, "");
                mHistoryKeywords_devicePhone.clear();
                getView().updateSearchHistory(mHistoryKeywords_devicePhone);
                break;
            default:
                mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, "");
                mHistoryKeywords_deviceName.clear();
                getView().updateSearchHistory(mHistoryKeywords_deviceName);
                break;
        }
//        mEditor.clear();
        mEditor.commit();
    }

    public void save(int searchType, String text) {
        if (!TextUtils.isEmpty(text)) {
            switch (searchType) {
                case Constants.TYPE_DEVICE_NAME:
                    if (!mHistoryKeywords_deviceName.contains(text)) {
                        mHistoryKeywords_deviceName.add(text);
                    }
                    if (mHistoryKeywords_deviceName.size() > 0) {
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < mHistoryKeywords_deviceName.size(); i++) {
                            if (i == (mHistoryKeywords_deviceName.size() - 1)) {
                                stringBuffer.append(mHistoryKeywords_deviceName.get(i));
                            } else {
                                stringBuffer.append(mHistoryKeywords_deviceName.get(i) + ",");
                            }
                        }
                        mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, stringBuffer.toString());
                        mEditor.commit();
                    }
                    break;
                case Constants.TYPE_DEVICE_SN:
                    if (!mHistoryKeywords_deviceNumber.contains(text)) {
                        mHistoryKeywords_deviceNumber.add(text);
                    }
                    if (mHistoryKeywords_deviceNumber.size() > 0) {
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < mHistoryKeywords_deviceNumber.size(); i++) {
                            if (i == (mHistoryKeywords_deviceNumber.size() - 1)) {
                                stringBuffer.append(mHistoryKeywords_deviceNumber.get(i));
                            } else {
                                stringBuffer.append(mHistoryKeywords_deviceNumber.get(i) + ",");
                            }
                        }
                        mEditor.putString(PREFERENCE_KEY_DEVICE_NUM, stringBuffer.toString());
                        mEditor.commit();
                    }
                    break;
                case Constants.TYPE_DEVICE_PHONE_NUM:
                    if (!mHistoryKeywords_devicePhone.contains(text)) {
                        mHistoryKeywords_devicePhone.add(text);
                    }
                    if (mHistoryKeywords_devicePhone.size() > 0) {
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < mHistoryKeywords_devicePhone.size(); i++) {
                            if (i == (mHistoryKeywords_devicePhone.size() - 1)) {
                                stringBuffer.append(mHistoryKeywords_devicePhone.get(i));
                            } else {
                                stringBuffer.append(mHistoryKeywords_devicePhone.get(i) + ",");
                            }
                        }
                        mEditor.putString(PREFERENCE_KEY_DEVICE_PHONE, stringBuffer.toString());
                        mEditor.commit();
                    }
                    break;
                default:
                    if (!mHistoryKeywords_deviceName.contains(text)) {
                        mHistoryKeywords_deviceName.add(text);
                    }
                    if (mHistoryKeywords_deviceName.size() > 0) {
                        StringBuffer stringBuffer = new StringBuffer();
                        for (int i = 0; i < mHistoryKeywords_deviceName.size(); i++) {
                            if (i == (mHistoryKeywords_deviceName.size() - 1)) {
                                stringBuffer.append(mHistoryKeywords_deviceName.get(i));
                            } else {
                                stringBuffer.append(mHistoryKeywords_deviceName.get(i) + ",");
                            }
                        }
                        mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, stringBuffer.toString());
                        mEditor.commit();
                    }
                    //
//                    oldTestDeviceName = mPref.getString(PREFERENCE_KEY_DEVICE_NAME, "");
//                    if (mHistoryKeywords_deviceName.contains(text)) {
//                        List<String> list = new ArrayList<String>();
//                        for (String o : oldTestDeviceName.split(",")) {
//                            if (!o.equalsIgnoreCase(text)) {
//                                list.add(o);
//                            }
//                        }
//                        list.add(0, text);
//                        mHistoryKeywords_deviceName.clear();
//                        mHistoryKeywords_deviceName.addAll(list);
//                        StringBuffer stringBuffer = new StringBuffer();
//                        for (int i = 0; i < list.size(); i++) {
//                            if (i == (list.size() - 1)) {
//                                stringBuffer.append(list.get(i));
//                            } else {
//                                stringBuffer.append(list.get(i) + ",");
//                            }
//                        }
//                        mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, stringBuffer.toString());
//                        mEditor.commit();
//                    } else {
//                        if (TextUtils.isEmpty(oldTestDeviceName)) {
//                            mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, text);
//                        } else {
//                            mEditor.putString(PREFERENCE_KEY_DEVICE_NAME, text + "," + oldTestDeviceName);
//                        }
//                        mEditor.commit();
//                        mHistoryKeywords_deviceName.add(0, text);
//                    }
                    break;
            }
        }

    }

    public void clickSearchHistoryItem(int searchType, int position) {
        String text;
        switch (searchType) {
            case Constants.TYPE_DEVICE_NAME:
                text = mHistoryKeywords_deviceName.get(position);
                getView().setEditText(text);
                break;
            case Constants.TYPE_DEVICE_SN:
                text = mHistoryKeywords_deviceNumber.get(position);
                getView().setEditText(text);
                break;
            case Constants.TYPE_DEVICE_PHONE_NUM:
                text = mHistoryKeywords_devicePhone.get(position);
                getView().setEditText(text);
                break;
            default:
                text = mHistoryKeywords_deviceName.get(position);
                getView().setEditText(text);
                break;
        }

        getView().setClearKeywordIvVisible(true);
        save(searchType, text.trim());
        requestData(searchType, text.trim());
    }

    @Override
    public void onDestroy() {
        mHistoryKeywords_deviceName.clear();
        mHistoryKeywords_deviceNumber.clear();
        mHistoryKeywords_devicePhone.clear();
    }
}
