package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.activity.SearchMerchantActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.factory.MenuPageFactory;
import com.sensoro.smartcity.imainviews.IMerchantSwitchActivityView;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.model.EventLoginData;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.GrantsInfo;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.UserAccountControlRsp;
import com.sensoro.smartcity.server.response.UserAccountRsp;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MerchantSwitchActivityPresenter extends BasePresenter<IMerchantSwitchActivityView> implements Constants {

    private final List<UserInfo> mUserInfoList = new ArrayList<>();
    private Activity mContext;
    private volatile int cur_page = 1;
    private EventLoginData mLoginData;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        mLoginData = (EventLoginData) mContext.getIntent().getSerializableExtra("login_data");
        if (mLoginData != null) {
            getView().setCurrentNameAndPhone(mLoginData.userName, mLoginData.phone);
            getView().setCurrentStatusImageViewVisible(true);
            requestDataByDirection(DIRECTION_DOWN, true);
        }
    }

    public void requestDataByDirection(int direction, boolean isForce) {
        if (isForce) {
            getView().showProgressDialog();
        }
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 1;
                RetrofitServiceHelper.INSTANCE.getUserAccountList(null, cur_page, null, null).subscribeOn(Schedulers.io()).observeOn
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
                        mUserInfoList.clear();
                        mUserInfoList.addAll(list);
                        getView().setAdapterSelectedIndex(-1);
                        getView().updateAdapterUserInfo(mUserInfoList);
                        getView().showSeperatorView(list.size() != 0);
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }
                });
                break;
            case DIRECTION_UP:
                cur_page++;
                RetrofitServiceHelper.INSTANCE.getUserAccountList(null, cur_page, null, null).subscribeOn(Schedulers.io()).observeOn
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
                            getView().toastShort("没有更多数据了");
                            getView().showSeperatorView(false);
                        } else {
                            mUserInfoList.addAll(list);
                            getView().setAdapterSelectedIndex(-1);
                            getView().updateAdapterUserInfo(mUserInfoList);
                            getView().showSeperatorView(true);
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

//    private void refreshUI(UserAccountRsp userAccountRsp) {
//        List<UserInfo> list = userAccountRsp.getData();
//        mUserInfoList.clear();
//        mUserInfoList.addAll(list);
//        getView().setAdapterSelectedIndex(-1);
//        getView().updateAdapterUserInfo(mUserInfoList);
//        getView().showSeperatorView(list.size() != 0);
//    }

    private void doAccountSwitch(String uid) {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.doAccountControl(uid, mLoginData.phoneId).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<UserAccountControlRsp>(this) {

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }

            @Override
            public void onCompleted(UserAccountControlRsp userAccountControlRsp) {
                if (userAccountControlRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {
                    UserInfo userInfo = userAccountControlRsp.getData();
                    GrantsInfo grants = userInfo.getGrants();

                    String sessionID = userInfo.getSessionID();
                    RetrofitServiceHelper.INSTANCE.saveSessionId(sessionID);
                    //
                    EventLoginData eventLoginData = new EventLoginData();
                    //
                    eventLoginData.userId = userInfo.get_id();
                    eventLoginData.userName = userInfo.getNickname();
                    eventLoginData.phone = userInfo.getContacts();
                    eventLoginData.phoneId = mLoginData.phoneId;
//            mCharacter = userInfo.getCharacter();
                    eventLoginData.roles = userInfo.getRoles();
                    eventLoginData.isSupperAccount = MenuPageFactory.getIsSupperAccount(userInfo.getIsSpecific());
                    eventLoginData.hasStation = MenuPageFactory.getHasStationDeploy(grants);
                    eventLoginData.hasContract = MenuPageFactory.getHasContract(grants);
                    eventLoginData.hasScanLogin = MenuPageFactory.getHasScanLogin(grants);

//                    String nickname = data.getNickname();
//                    String phone = data.getContacts();
                    //
//                    String roles = data.getRoles();
                    //
//                    String isSpecific = data.getIsSpecific();
                    //grantsInfo
                    EventData eventData = new EventData();
                    eventData.code = EVENT_DATA_SEARCH_MERCHANT;
                    eventData.data = eventLoginData;
                    EventBus.getDefault().post(eventData);
                    getView().finishAc();
                    //TODO 包装类 进行账户切换
//                    ((MainActivity) mContext).changeAccount(eventLoginData);
                } else {
                    getView().toastShort(userAccountControlRsp.getErrmsg());
                }
                getView().dismissProgressDialog();
            }
        });
    }

    public void clickItem(int position) {
        position = position - 1;
        if (!mUserInfoList.get(position).isStop()) {
            getView().setAdapterSelectedIndex(position);
//            mMerchantAdapter.setSelectedIndex(position);
//            mMerchantAdapter.notifyDataSetChanged();
//            getView().updateAdapterUserInfo(mUserInfoList);
//            getView().setCurrentStatusImageViewVisible(false);
//            mCurrentStatusImageView.setVisibility(View.GONE);
            String uid = mUserInfoList.get(position).get_id();
            doAccountSwitch(uid);
        } else {
            getView().toastShort("账户已停用");
        }
    }

    public void startToSearchAC() {
        Intent searchIntent = new Intent(mContext, SearchMerchantActivity.class);
        searchIntent.putExtra("phone_id", mLoginData.phoneId);
        if (!TextUtils.isEmpty(mLoginData.userName)) {
            searchIntent.putExtra("user_name", mLoginData.userName);
        }
        if (!TextUtils.isEmpty(mLoginData.phone)) {
            searchIntent.putExtra("user_phone", mLoginData.phone);
        }
        getView().startAC(searchIntent);
    }

    @Override
    public void onDestroy() {
        mUserInfoList.clear();
    }
}
