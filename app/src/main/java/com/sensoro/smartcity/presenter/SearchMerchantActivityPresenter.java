package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.factory.MenuPageFactory;
import com.sensoro.smartcity.imainviews.ISearchMerchantActivityView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.DeviceMergeTypesInfo;
import com.sensoro.smartcity.server.bean.GrantsInfo;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.server.response.DevicesMergeTypesRsp;
import com.sensoro.smartcity.server.response.UserAccountControlRsp;
import com.sensoro.smartcity.server.response.UserAccountRsp;
import com.sensoro.smartcity.util.LogUtils;
import com.sensoro.smartcity.util.PreferencesHelper;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class SearchMerchantActivityPresenter extends BasePresenter<ISearchMerchantActivityView> implements Constants {
    private SharedPreferences mPref;
    private SharedPreferences.Editor mEditor;
    private final List<String> mHistoryKeywords = new ArrayList<>();
    private Activity mContext;
    private final List<UserInfo> mUserInfoList = new ArrayList<>();
    private String phoneId = null;
    private String userName = null;
    private String phone = null;
    private EventLoginData eventLoginData;


    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mPref = mContext.getSharedPreferences(PREFERENCE_MERCHANT_HISTORY, Activity.MODE_PRIVATE);
        mEditor = mPref.edit();
        phoneId = mContext.getIntent().getStringExtra("phone_id");
        userName = mContext.getIntent().getStringExtra("user_name");
        phone = mContext.getIntent().getStringExtra("user_phone");
        initSearchHistory();
    }

    private void initSearchHistory() {
        String history = mPref.getString(PREFERENCE_KEY_DEVICE, "");
        if (!TextUtils.isEmpty(history)) {
            mHistoryKeywords.clear();
            mHistoryKeywords.addAll(Arrays.asList(history.split(",")));
        }
        if (mHistoryKeywords.size() > 0) {
            getView().setSearchHistoryLayoutVisible(true);
            getView().updateSearchHistory(mHistoryKeywords);
        } else {
            getView().setSearchHistoryLayoutVisible(false);
        }

        getView().setCurrentNameAndPhone(userName, phone);
        getView().setCurrentStatusImageViewVisible(true);
    }

    public void save(String text) {
        String oldText = mPref.getString(PREFERENCE_KEY_DEVICE, "");
        if (!TextUtils.isEmpty(text)) {
            if (mHistoryKeywords.contains(text)) {
                mHistoryKeywords.clear();
                for (String o : oldText.split(",")) {
                    if (!o.equalsIgnoreCase(text)) {
                        mHistoryKeywords.add(o);
                    }
                }
                mHistoryKeywords.add(0, text);
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < mHistoryKeywords.size(); i++) {
                    if (i == (mHistoryKeywords.size() - 1)) {
                        stringBuilder.append(mHistoryKeywords.get(i));
                    } else {
                        stringBuilder.append(mHistoryKeywords.get(i)).append(",");
                    }
                }
                mEditor.putString(PREFERENCE_KEY_DEVICE, stringBuilder.toString());
                mEditor.commit();
            } else {
                mEditor.putString(PREFERENCE_KEY_DEVICE, text + "," + oldText);
                mEditor.commit();
                mHistoryKeywords.add(0, text);
            }
            getView().updateSearchHistory(mHistoryKeywords);
        }
    }

    public void requestData(String text) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getUserAccountList(text, null, 0, 100000).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<UserAccountRsp>(this) {


            @Override
            public void onCompleted(UserAccountRsp userAccountRsp) {
                refreshUI(userAccountRsp);
//                List<UserInfo> list = userAccountRsp.getData();
//                if (list.size() == 0) {
//                    getView().setTipsLinearLayoutVisible(true);
//                } else {
//                    Intent data = new Intent();
//                    data.putExtra(EXTRA_MERCHANT_INFO, userAccountRsp);
//                    getView().setIntentResult(RESULT_CODE_CHANGE_MERCHANT, data);
//                    getView().finishAc();
//                }
                getView().dismissProgressDialog();
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    private void refreshUI(UserAccountRsp userAccountRsp) {
        List<UserInfo> list = userAccountRsp.getData();
        if (list.size() > 0) {
            mUserInfoList.clear();
            mUserInfoList.addAll(list);
            getView().updateMerchantInfo(mUserInfoList);
            getView().setSearchHistoryLayoutVisible(false);
            getView().setLlMerchantItemViewVisible(true);
        } else {
            getView().setTipsLinearLayoutVisible(true);
            getView().setLlMerchantItemViewVisible(false);
            getView().setSearchHistoryLayoutVisible(false);
        }

//        getView().setAdapterSelectedIndex(-1);
//        getView().updateAdapterUserInfo(list);
//        getView().showSeperatorView(list.size() != 0);
    }

    public void cleanHistory() {
        mEditor.clear();
        mHistoryKeywords.clear();
        mEditor.commit();
        getView().updateSearchHistory(mHistoryKeywords);
        getView().setSearchHistoryLayoutVisible(false);
    }


    private void doAccountSwitch(String uid) {
        getView().showProgressDialog();
        eventLoginData = null;
        RetrofitServiceHelper.INSTANCE.doAccountControl(uid, phoneId).subscribeOn(Schedulers.io()).flatMap(new Func1<UserAccountControlRsp, Observable<DevicesMergeTypesRsp>>() {
            @Override
            public Observable<DevicesMergeTypesRsp> call(UserAccountControlRsp userAccountControlRsp) {
                UserInfo userInfo = userAccountControlRsp.getData();
                RetrofitServiceHelper.INSTANCE.saveSessionId(userInfo.getSessionID());
                GrantsInfo grants = userInfo.getGrants();
                //修改loginData包装
                eventLoginData = new EventLoginData();
                eventLoginData.userId = userInfo.get_id();
                eventLoginData.userName = userInfo.getNickname();
                eventLoginData.phone = userInfo.getContacts();
                eventLoginData.phoneId = phoneId;
//            mCharacter = userInfo.getCharacter();
                String roles = userInfo.getRoles();
                eventLoginData.roles = roles;
                String isSpecific = userInfo.getIsSpecific();
                eventLoginData.isSupperAccount = MenuPageFactory.getIsSupperAccount(isSpecific);
                eventLoginData.hasStation = MenuPageFactory.getHasStationDeploy(grants);
                eventLoginData.hasContract = MenuPageFactory.getHasContract(grants);
                eventLoginData.hasScanLogin = MenuPageFactory.getHasScanLogin(grants);
                eventLoginData.hasSubMerchant = MenuPageFactory.getHasSubMerchant(roles, isSpecific);
                eventLoginData.hasInspection = MenuPageFactory.getHasInspection(grants);
                //
                return RetrofitServiceHelper.INSTANCE.getDevicesMergeTypes();
            }
        }).doOnNext(new Action1<DevicesMergeTypesRsp>() {
            @Override
            public void call(DevicesMergeTypesRsp devicesMergeTypesRsp) {
                DeviceMergeTypesInfo data = devicesMergeTypesRsp.getData();
                PreferencesHelper.getInstance().saveLocalDevicesMergeTypes(data);
                //
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new CityObserver<DevicesMergeTypesRsp>(this) {
            @Override
            public void onCompleted(DevicesMergeTypesRsp devicesMergeTypesRsp) {
                EventData eventData = new EventData();
                eventData.code = EVENT_DATA_SEARCH_MERCHANT;
                eventData.data = eventLoginData;
                EventBus.getDefault().post(eventData);
                getView().finishAc();
                LogUtils.loge("DevicesMergeTypesRsp ....." + eventLoginData.toString());
                getView().dismissProgressDialog();
            }


            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    public void clickItem(int position) {
        if (!mUserInfoList.get(position).isStop()) {
//            getView().setAdapterSelectedIndex(position);
//            mMerchantAdapter.setSelectedIndex(position);
//            mMerchantAdapter.notifyDataSetChanged();
//            getView().updateMerchantInfo();
//            mCurrentStatusImageView.setVisibility(View.GONE);
            String uid = mUserInfoList.get(position).get_id();
            doAccountSwitch(uid);
        } else {
            getView().toastShort("账户已停用");
        }
    }

    @Override
    public void onDestroy() {
        mHistoryKeywords.clear();
        mUserInfoList.clear();
    }
}
