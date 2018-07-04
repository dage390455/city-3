package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.activity.SearchMerchantActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IMerchantSwitchFragmentView;
import com.sensoro.smartcity.server.RetrofitServiceHelper;
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
        this.username = username;
        this.phone = phone;
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
            public void onErrorMsg(int errorCode,String errorMsg) {
                getView().dismissProgressDialog();
                getView().toastShort(errorMsg);
            }
        });
    }

    private void refreshUI(UserAccountRsp userAccountRsp) {
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
                    RetrofitServiceHelper.INSTANCE.setSessionId(sessionID);
                    String nickname = userAccountControlRsp.getData().getNickname();
                    String phone = userAccountControlRsp.getData().getContacts();
                    String roles = userAccountControlRsp.getData().getRoles();
                    String isSpecific = userAccountControlRsp.getData().getIsSpecific();

                    ((MainActivity) mContext).changeAccount(nickname, phone, roles,
                            isSpecific);
                } else {
                    getView().toastShort(userAccountControlRsp.getErrmsg());
                }
            }

            @Override
            public void onErrorMsg(int errorCode,String errorMsg) {
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
        getView().startACForResult(searchIntent, REQUEST_CODE_SEARCH_MERCHANT);
    }
}
