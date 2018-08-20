package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.activity.SearchMerchantActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.factory.MenuPageFactory;
import com.sensoro.smartcity.imainviews.IMerchantSwitchFragmentView;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.GrantsInfo;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.server.CityObserver;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.UserAccountControlRsp;
import com.sensoro.smartcity.server.response.UserAccountRsp;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MerchantSwitchFragmentPresenter extends BasePresenter<IMerchantSwitchFragmentView> implements Constants {
    private String phoneId = null;
    private String username = null;
    private String phone = null;

    private final List<UserInfo> mUserInfoList = new ArrayList<>();
    private Activity mContext;
    private volatile int cur_page = 1;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
    }

    public void refreshUserData(String username, String phone, String phoneId) {
        this.phoneId = phoneId;
        this.username = username;
        this.phone = phone;
        getView().setCurrentNameAndPhone(username, phone);
        getView().setCurrentStatusImageViewVisible(true);
    }

    public void requestDataByDirection(int direction, boolean isForce) {
        if (isForce) {
            getView().showProgressDialog();
        }
        switch (direction) {
            case DIRECTION_DOWN:
                cur_page = 1;
                RetrofitServiceHelper.INSTANCE.getUserAccountList(null, cur_page, null, null).subscribeOn(Schedulers.io()).observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new CityObserver<UserAccountRsp>() {


                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onNext(UserAccountRsp userAccountRsp) {
                        List<UserInfo> list = userAccountRsp.getData();
                        mUserInfoList.clear();
                        mUserInfoList.addAll(list);
                        getView().setAdapterSelectedIndex(-1);
                        getView().updateAdapterUserInfo(mUserInfoList);
                        getView().showSeperatorView(list.size() != 0);
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
                RetrofitServiceHelper.INSTANCE.getUserAccountList(null, cur_page, null, null).subscribeOn(Schedulers.io()).observeOn
                        (AndroidSchedulers.mainThread()).subscribe(new CityObserver<UserAccountRsp>() {


                    @Override
                    public void onCompleted() {
                        getView().dismissProgressDialog();
                        getView().onPullRefreshComplete();
                    }

                    @Override
                    public void onNext(UserAccountRsp userAccountRsp) {
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

                    }

                    @Override
                    public void onErrorMsg(int errorCode, String errorMsg) {
                        cur_page--;
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
        RetrofitServiceHelper.INSTANCE.doAccountControl(uid, phoneId).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<UserAccountControlRsp>() {
            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
            }

            @Override
            public void onNext(UserAccountControlRsp userAccountControlRsp) {
                if (userAccountControlRsp.getErrcode() == ResponseBase.CODE_SUCCESS) {
                    UserInfo data = userAccountControlRsp.getData();
                    String sessionID = data.getSessionID();
                    RetrofitServiceHelper.INSTANCE.setSessionId(sessionID);
                    String nickname = data.getNickname();
                    String phone = data.getContacts();
                    //
                    String roles = data.getRoles();
                    //
                    String isSpecific = data.getIsSpecific();
                    //grantsInfo
                    GrantsInfo grants = data.getGrants();
                    ((MainActivity) mContext).changeAccount(nickname, phone, roles,
                            MenuPageFactory.getIsSupperAccount(isSpecific), MenuPageFactory.getHasStationDeploy
                                    (grants), MenuPageFactory.getHasContract(grants), MenuPageFactory.getHasScanLogin
                                    (grants));
                } else {
                    getView().toastShort(userAccountControlRsp.getErrmsg());
                }
            }

            @Override
            public void onErrorMsg(int errorCode, String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
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
        searchIntent.putExtra("phone_id", phoneId);
        if (!TextUtils.isEmpty(username)) {
            searchIntent.putExtra("user_name", username);
        }
        if (!TextUtils.isEmpty(phone)) {
            searchIntent.putExtra("user_phone", phone);
        }
        getView().startAC(searchIntent);
    }

    @Override
    public void onDestroy() {
        mUserInfoList.clear();
    }
}
