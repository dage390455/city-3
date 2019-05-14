package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.constant.SearchHistoryTypeConstants;
import com.sensoro.smartcity.factory.UserPermissionFactory;
import com.sensoro.smartcity.imainviews.IMerchantSwitchActivityView;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.server.response.DevicesMergeTypesRsp;
import com.sensoro.smartcity.server.response.LoginRsp;
import com.sensoro.smartcity.server.response.UserAccountControlRsp;
import com.sensoro.smartcity.server.response.UserAccountRsp;
import com.sensoro.smartcity.util.PreferencesHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MerchantSwitchActivityPresenter extends BasePresenter<IMerchantSwitchActivityView> implements Constants, IOnCreate {

    private final List<UserInfo> mUserInfoList = new ArrayList<>();
    private Activity mContext;
    private volatile int cur_page = 0;
    private volatile EventLoginData eventLoginData;
    private final List<String> mSearchHistoryList = new ArrayList<>();
    private String tempSearch;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        eventLoginData = (EventLoginData) mContext.getIntent().getSerializableExtra(EXTRA_EVENT_LOGIN_DATA);
        if (eventLoginData != null) {
            getView().setTvBackToMainMerchantVisible(eventLoginData.hasControllerAid);
            getView().setCurrentNameAndPhone(eventLoginData.userName, eventLoginData.phone);
            requestDataByDirection(DIRECTION_DOWN, true, null);
        }
        List<String> list = PreferencesHelper.getInstance().getSearchHistoryData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MERCHANT);
        if (list != null) {
            mSearchHistoryList.addAll(list);
            getView().updateSearchHistoryList(mSearchHistoryList);
        }
    }

    public void requestDataByDirection(int direction, boolean isForce, String searchText) {
        if (!eventLoginData.hasSubMerchant) {
            return;
        }
        if (isForce) {
            getView().showProgressDialog();
        }
        if (TextUtils.isEmpty(searchText)) {
            tempSearch = null;
        } else {
            tempSearch = searchText;
        }
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 0;
                RetrofitServiceHelper.getInstance().getUserAccountList(tempSearch, null, cur_page * 20, 20).subscribeOn(Schedulers.io()).observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new CityObserver<UserAccountRsp>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onCompleted(UserAccountRsp userAccountRsp) {
                        List<UserInfo> list = userAccountRsp.getData();
                        if (list == null) {
                            mUserInfoList.clear();
                        } else {
                            mUserInfoList.clear();
                            mUserInfoList.addAll(list);
                        }
                        getView().updateAdapterUserInfo(mUserInfoList);
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }
                });
                break;
            case DIRECTION_UP:
                cur_page++;
                RetrofitServiceHelper.getInstance().getUserAccountList(tempSearch, null, cur_page * 20, 20).subscribeOn(Schedulers.io()).observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new CityObserver<UserAccountRsp>(this) {

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onCompleted(UserAccountRsp userAccountRsp) {
                        List<UserInfo> list = userAccountRsp.getData();
                        if (list == null || list.size() == 0) {
                            cur_page--;
                            getView().toastShort(mContext.getString(R.string.no_more_data));
                        } else {
                            mUserInfoList.addAll(list);
                            getView().updateAdapterUserInfo(mUserInfoList);
                        }
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }
                });
                break;
            default:
                break;
        }
    }

    private void doAccountSwitch(String uid) {
        getView().showProgressDialog();
        eventLoginData = null;
        final String phoneId = PreferencesHelper.getInstance().getUserData().phoneId;
        RetrofitServiceHelper.getInstance().doAccountControl(uid, phoneId).subscribeOn(Schedulers.io()).flatMap(new Function<UserAccountControlRsp, ObservableSource<DevicesMergeTypesRsp>>() {
            @Override
            public ObservableSource<DevicesMergeTypesRsp> apply(UserAccountControlRsp userAccountControlRsp) throws Exception {
                UserInfo userInfo = userAccountControlRsp.getData();
                RetrofitServiceHelper.getInstance().saveSessionId(userInfo.getSessionID());
                //
                eventLoginData = UserPermissionFactory.createLoginData(userInfo, phoneId);
                return RetrofitServiceHelper.getInstance().getDevicesMergeTypes();
            }
        }).doAfterNext(new Consumer<DevicesMergeTypesRsp>() {
            @Override
            public void accept(DevicesMergeTypesRsp devicesMergeTypesRsp) throws Exception {
                DeviceMergeTypesInfo data = devicesMergeTypesRsp.getData();
                PreferencesHelper.getInstance().saveLocalDevicesMergeTypes(data);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DevicesMergeTypesRsp>(this) {
            @Override
            public void onCompleted(DevicesMergeTypesRsp devicesMergeTypesRsp) {
                EventData eventData = new EventData();
                eventData.code = EVENT_DATA_SEARCH_MERCHANT;
                eventData.data = eventLoginData;
                EventBus.getDefault().post(eventData);
                getView().dismissProgressDialog();
                getView().finishAc();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    public void clickItem(UserInfo userInfo) {
        if (!PreferencesHelper.getInstance().getUserData().hasMerchantChange) {
            getView().toastShort(mContext.getString(R.string.merchant_has_no_change_permission));
            return;
        }
        if (!userInfo.isStop()) {
            String uid = userInfo.get_id();
            doAccountSwitch(uid);
        } else {
            getView().toastShort(mContext.getString(R.string.account_has_been_disabled));
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mUserInfoList.clear();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case EVENT_DATA_SEARCH_MERCHANT:
                //TODO 不需要注册,以后预留
//                if (data instanceof EventLoginData) {
//                    EventLoginData eventLoginData = (EventLoginData) data;
//                    getView().setTvBackToMainMerchantVisible(eventLoginData.hasControllerAid);
//                    getView().setCurrentNameAndPhone(eventLoginData.userName, eventLoginData.phone);
//                }
                break;
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    public void save(String text) {
        if (TextUtils.isEmpty(text)) {
            return;
        }
//        mSearchHistoryList.remove(text);
//        PreferencesHelper.getInstance().saveSearchHistoryText(text, SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MERCHANT);
//        mSearchHistoryList.add(0, text);
        List<String> merchantList = PreferencesSaveAnalyzer.handleDeployRecord(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MERCHANT, text);
        mSearchHistoryList.clear();
        mSearchHistoryList.addAll(merchantList);
        getView().updateSearchHistoryList(mSearchHistoryList);
    }

    public void requestSearchData(int direction, String text) {
        requestDataByDirection(direction, true, text);
    }

    public void clearSearchHistory() {
        PreferencesSaveAnalyzer.clearAllData(SearchHistoryTypeConstants.TYPE_SEARCH_HISTORY_MERCHANT);
        mSearchHistoryList.clear();
        getView().updateSearchHistoryList(mSearchHistoryList);
    }

    public void doBackToMainMerchant() {
        getView().showProgressDialog();
        tempSearch = null;
        final String phoneId = PreferencesHelper.getInstance().getUserData().phoneId;
//        RetrofitServiceHelper.INSTANCE.backMainAccount().subscribeOn(Schedulers.io()).flatMap(new Func1<LoginRsp, Observable<DevicesMergeTypesRsp>>() {
//            @Override
//            public Observable<DevicesMergeTypesRsp> call(LoginRsp loginRsp) {
//                //
//                String sessionID = loginRsp.getData().getSessionID();
//                RetrofitServiceHelper.INSTANCE.saveSessionId(sessionID);
//                UserInfo userInfo = loginRsp.getData();
//                eventLoginData = UserPermissionFactory.createLoginData(userInfo, phoneId);
//                PreferencesHelper.getInstance().saveUserData(eventLoginData);
//                return RetrofitServiceHelper.INSTANCE.getDevicesMergeTypes();
//            }
//        }).flatMap(new Func1<DevicesMergeTypesRsp, Observable<UserAccountRsp>>() {
//            @Override
//            public Observable<UserAccountRsp> call(DevicesMergeTypesRsp devicesMergeTypesRsp) {
//                DeviceMergeTypesInfo data = devicesMergeTypesRsp.getData();
//                PreferencesHelper.getInstance().saveLocalDevicesMergeTypes(data);
//                return RetrofitServiceHelper.INSTANCE.getUserAccountList(tempSearch, null, 0, 20);
//            }
//        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<UserAccountRsp>(this) {
//
//            @Override
//            public void onErrorMsg(int errorCode, String errorMsg) {
//                getView().dismissProgressDialog();
//                getView().toastShort(errorMsg);
//                getView().onPullRefreshComplete();
//            }
//
//            @Override
//            public void onCompleted(UserAccountRsp userAccountRsp) {
//                if (eventLoginData != null) {
//                    getView().setTvBackToMainMerchantVisible(eventLoginData.hasControllerAid);
//                    getView().setCurrentNameAndPhone(eventLoginData.userName, eventLoginData.phone);
//                    EventData eventData = new EventData();
//                    eventData.code = EVENT_DATA_SEARCH_MERCHANT;
//                    eventData.data = eventLoginData;
//                    EventBus.getDefault().post(eventData);
//                }
//                List<UserInfo> list = userAccountRsp.getData();
//                if (list == null) {
//                    mUserInfoList.clear();
//                } else {
//                    mUserInfoList.clear();
//                    mUserInfoList.addAll(list);
//                }
//                getView().updateAdapterUserInfo(mUserInfoList);
//                getView().dismissProgressDialog();
//                getView().onPullRefreshComplete();
//            }
//        });
        RetrofitServiceHelper.getInstance().backMainAccount().subscribeOn(Schedulers.io()).flatMap(new Function<LoginRsp, ObservableSource<DevicesMergeTypesRsp>>() {
            @Override
            public ObservableSource<DevicesMergeTypesRsp> apply(LoginRsp loginRsp) throws Exception {
                //
                String sessionID = loginRsp.getData().getSessionID();
                RetrofitServiceHelper.getInstance().saveSessionId(sessionID);
                UserInfo userInfo = loginRsp.getData();
                eventLoginData = UserPermissionFactory.createLoginData(userInfo, phoneId);
                PreferencesHelper.getInstance().saveUserData(eventLoginData);
                return RetrofitServiceHelper.getInstance().getDevicesMergeTypes();
            }
        }).doOnNext(new Consumer<DevicesMergeTypesRsp>() {
            @Override
            public void accept(DevicesMergeTypesRsp devicesMergeTypesRsp) throws Exception {
                DeviceMergeTypesInfo data = devicesMergeTypesRsp.getData();
                PreferencesHelper.getInstance().saveLocalDevicesMergeTypes(data);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DevicesMergeTypesRsp>(this) {

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }

            @Override
            public void onCompleted(DevicesMergeTypesRsp devicesMergeTypesRsp) {
                if (eventLoginData != null) {
                    EventData eventData = new EventData();
                    eventData.code = EVENT_DATA_SEARCH_MERCHANT;
                    eventData.data = eventLoginData;
                    EventBus.getDefault().post(eventData);
                    getView().finishAc();
                }
                getView().dismissProgressDialog();
            }
        });
    }
}
