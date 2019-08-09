package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.common.analyzer.PreferencesSaveAnalyzer;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.constant.SearchHistoryTypeConstants;
import com.sensoro.common.helper.PreferencesHelper;
import com.sensoro.common.manger.RxApiManager;
import com.sensoro.common.model.EventData;
import com.sensoro.common.model.EventLoginData;
import com.sensoro.common.server.CityObserver;
import com.sensoro.common.server.RetrofitServiceHelper;
import com.sensoro.common.server.bean.DeviceMergeTypesInfo;
import com.sensoro.common.server.bean.UserInfo;
import com.sensoro.common.server.response.ResponseResult;
import com.sensoro.common.utils.LogUtils;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.factory.MerchantSubFactory;
import com.sensoro.smartcity.factory.UserPermissionFactory;
import com.sensoro.smartcity.imainviews.IMerchantSwitchActivityView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MerchantSwitchActivityPresenter extends BasePresenter<IMerchantSwitchActivityView> {

    private final List<UserInfo> mUserInfoList = new ArrayList<>();
    private Activity mContext;
    private volatile int cur_page = 0;
    private EventLoginData eventLoginData;
    private final List<String> mSearchHistoryList = new ArrayList<>();
    private String tempSearch;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        eventLoginData = (EventLoginData) mContext.getIntent().getSerializableExtra(Constants.EXTRA_EVENT_LOGIN_DATA);
        if (eventLoginData != null) {
            getView().setTvBackToMainMerchantVisible(eventLoginData.hasControllerAid);
            getView().setCurrentNameAndPhone(eventLoginData.userName, eventLoginData.phone);
            requestDataByDirection(Constants.DIRECTION_DOWN, true, null);
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
        //不为空是搜索状态
        boolean isSearch = !TextUtils.isEmpty(tempSearch);
        switch (direction) {
            case Constants.DIRECTION_DOWN:
                cur_page = 0;
                RetrofitServiceHelper.getInstance().getUserAccountList(tempSearch, null, cur_page * 20, 20).doOnNext(new Consumer<ResponseResult<List<UserInfo>>>() {
                    @Override
                    public void accept(ResponseResult<List<UserInfo>> listResponseResult) throws Exception {
                        //TODO 处理list中的子商户信息
                        List<UserInfo> data = listResponseResult.getData();
                        if (data != null) {
                            for (UserInfo userInfo : data) {
                                userInfo.merchantSubList = MerchantSubFactory.createMerchantSubList(userInfo);
                                if (userInfo.merchantSubList != null && userInfo.merchantSubList.size() > 0) {
                                    userInfo.expand = data.size() <= 1;
                                }
                            }
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<UserInfo>>>(this) {


                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        mUserInfoList.clear();

                        getView().updateAdapterUserInfo(mUserInfoList, isSearch);
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onCompleted(ResponseResult<List<UserInfo>> userAccountRsp) {
                        List<UserInfo> list = userAccountRsp.getData();
                        mUserInfoList.clear();
                        if (list != null) {
                            mUserInfoList.addAll(list);
                        }
                        getView().updateAdapterUserInfo(mUserInfoList, isSearch);
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }
                });
                break;
            case Constants.DIRECTION_UP:
                cur_page++;
                RetrofitServiceHelper.getInstance().getUserAccountList(tempSearch, null, cur_page * 20, 20).doOnNext(new Consumer<ResponseResult<List<UserInfo>>>() {
                    @Override
                    public void accept(ResponseResult<List<UserInfo>> listResponseResult) throws Exception {
                        //TODO 处理list中的子商户信息，处理子商户信息
                        List<UserInfo> data = listResponseResult.getData();
                        if (data != null) {
                            for (UserInfo userInfo : data) {
                                userInfo.merchantSubList = MerchantSubFactory.createMerchantSubList(userInfo);
                                if (userInfo.merchantSubList != null && userInfo.merchantSubList.size() > 0) {
                                    userInfo.expand = data.size() <= 1;
                                }
                            }
                        }
                    }
                }).subscribeOn(Schedulers.io()).observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<List<UserInfo>>>(this) {

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
                        getView().dismissProgressDialog();
                        getView().toastShort(errorMsg);
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onCompleted(ResponseResult<List<UserInfo>> userAccountRsp) {
                        List<UserInfo> list = userAccountRsp.getData();
                        if (list == null || list.size() == 0) {
                            cur_page--;
                            getView().toastShort(mContext.getString(R.string.no_more_data));
                        } else {
                            mUserInfoList.addAll(list);
                            getView().updateAdapterUserInfo(mUserInfoList, isSearch);
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
        RetrofitServiceHelper.getInstance().doAccountControl(uid, phoneId).subscribeOn(Schedulers.io()).flatMap(new Function<ResponseResult<UserInfo>, ObservableSource<ResponseResult<DeviceMergeTypesInfo>>>() {
            @Override
            public ObservableSource<ResponseResult<DeviceMergeTypesInfo>> apply(ResponseResult<UserInfo> userAccountControlRsp) throws Exception {
                UserInfo userInfo = userAccountControlRsp.getData();
                String sessionID = userInfo.getSessionID();
                String token = userInfo.getToken();
                RetrofitServiceHelper.getInstance().saveSessionId(sessionID, token);
                //
                eventLoginData = UserPermissionFactory.createLoginData(userInfo, phoneId);
                try {
                    LogUtils.loge("切换登录---->>> " + eventLoginData.toString());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return RetrofitServiceHelper.getInstance().getDevicesMergeTypes();
            }
        }).doAfterNext(new Consumer<ResponseResult<DeviceMergeTypesInfo>>() {
            @Override
            public void accept(ResponseResult<DeviceMergeTypesInfo> devicesMergeTypesRsp) throws Exception {
                DeviceMergeTypesInfo data = devicesMergeTypesRsp.getData();
                PreferencesHelper.getInstance().saveLocalDevicesMergeTypes(data);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<DeviceMergeTypesInfo>>(this) {
            @Override
            public void onCompleted(ResponseResult<DeviceMergeTypesInfo> devicesMergeTypesRsp) {
                PreferencesHelper.getInstance().saveUserData(eventLoginData);
                RxApiManager.getInstance().cancelAll();
                EventData eventData = new EventData();
                eventData.code = Constants.EVENT_DATA_SEARCH_MERCHANT;
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
        mUserInfoList.clear();
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
        RetrofitServiceHelper.getInstance().backMainAccount().subscribeOn(Schedulers.io()).flatMap(new Function<ResponseResult<UserInfo>, ObservableSource<ResponseResult<DeviceMergeTypesInfo>>>() {
            @Override
            public ObservableSource<ResponseResult<DeviceMergeTypesInfo>> apply(ResponseResult<UserInfo> loginRsp) throws Exception {
                //
                String sessionID = loginRsp.getData().getSessionID();
                String token = loginRsp.getData().getToken();
                RetrofitServiceHelper.getInstance().saveSessionId(sessionID, token);
                UserInfo userInfo = loginRsp.getData();
                eventLoginData = UserPermissionFactory.createLoginData(userInfo, phoneId);
                try {
                    LogUtils.loge("切换登录---->>> " + eventLoginData.toString());
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                return RetrofitServiceHelper.getInstance().getDevicesMergeTypes();
            }
        }).doOnNext(new Consumer<ResponseResult<DeviceMergeTypesInfo>>() {
            @Override
            public void accept(ResponseResult<DeviceMergeTypesInfo> devicesMergeTypesRsp) throws Exception {
                DeviceMergeTypesInfo data = devicesMergeTypesRsp.getData();
                PreferencesHelper.getInstance().saveLocalDevicesMergeTypes(data);
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<ResponseResult<DeviceMergeTypesInfo>>(this) {

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }

            @Override
            public void onCompleted(ResponseResult<DeviceMergeTypesInfo> devicesMergeTypesRsp) {
                if (eventLoginData != null) {
                    PreferencesHelper.getInstance().saveUserData(eventLoginData);
                }
                RxApiManager.getInstance().cancelAll();
                EventData eventData = new EventData();
                eventData.code = Constants.EVENT_DATA_SEARCH_MERCHANT;
                eventData.data = eventLoginData;
                EventBus.getDefault().post(eventData);
                getView().finishAc();
                getView().dismissProgressDialog();

            }
        });
    }
}
