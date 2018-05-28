package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.activity.SearchMerchantActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IMerchantSwitchFragmentView;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.server.response.CityObserver;
import com.sensoro.smartcity.server.response.ResponseBase;
import com.sensoro.smartcity.server.response.UserAccountControlRsp;
import com.sensoro.smartcity.server.response.UserAccountRsp;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MerchantSwitchFragmentPresenter extends BasePresenter<IMerchantSwitchFragmentView> implements Constants {
    private String phoneId = null;

    public List<UserInfo> getUserInfoList() {
        return mUserInfoList;
    }

    private List<UserInfo> mUserInfoList = new ArrayList<>();
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
    }

    public void refreshUserData(String username, String phone, String phoneId) {
        this.phoneId = phoneId;
        getView().setCurrentNameAndPhone(username, phone);
        getView().setCurrentStatusImageViewVisible(true);
    }

    public void requestData() {
        getView().showProgressDialog();
        RetrofitServiceHelper.INSTANCE.getUserAccountList(null).subscribeOn(Schedulers.io()).observeOn
                (AndroidSchedulers.mainThread()).subscribe(new CityObserver<UserAccountRsp>() {


            @Override
            public void onCompleted() {
                getView().dismissProgressDialog();
            }

            @Override
            public void onNext(UserAccountRsp userAccountRsp) {
                refreshUI(userAccountRsp);
            }

            @Override
            public void onErrorMsg(String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    public void refreshUI(UserAccountRsp userAccountRsp) {
        List<UserInfo> list = userAccountRsp.getData();
        mUserInfoList.clear();
        mUserInfoList.addAll(list);
        getView().setAdapterSelectedIndex(-1);
        getView().updateAdapterUserInfo(list);
        getView().showSeperatorBottomView(list.size() != 0);
    }

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
                    String sessionID = userAccountControlRsp.getData().getSessionID();
//                    NetUtils.INSTANCE.setSessionId(sessionID);
                    RetrofitServiceHelper.INSTANCE.setSessionId(sessionID);
                    String nickname = userAccountControlRsp.getData().getNickname();
                    String phone = userAccountControlRsp.getData().getPhone();
                    String roles = userAccountControlRsp.getData().getRoles();
                    String isSpecific = userAccountControlRsp.getData().getIsSpecific();
                    ((MainActivity) mContext).changeAccount(nickname, phone, roles,
                            isSpecific);
                } else {

                }
            }

            @Override
            public void onErrorMsg(String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    public void clickItem(int position) {
        if (!mUserInfoList.get(position).isStop()) {
            getView().setAdapterSelectedIndex(position);
//            mMerchantAdapter.setSelectedIndex(position);
//            mMerchantAdapter.notifyDataSetChanged();
            getView().updateAdapterUserInfo(mUserInfoList);
            getView().setCurrentStatusImageViewVisible(false);
//            mCurrentStatusImageView.setVisibility(View.GONE);
            String uid = mUserInfoList.get(position).get_id();
            doAccountSwitch(uid);
        }
    }

    public void startToSearchAC() {
        Intent searchIntent = new Intent(mContext, SearchMerchantActivity.class);
        getView().startACForResult(searchIntent, REQUEST_CODE_SEARCH_MERCHANT);
    }
}
