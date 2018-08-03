package com.sensoro.smartcity.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.MainActivity;
import com.sensoro.smartcity.adapter.MerchantAdapter;
import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IMerchantSwitchFragmentView;
import com.sensoro.smartcity.presenter.MerchantSwitchFragmentPresenter;
import com.sensoro.smartcity.server.bean.UserInfo;
import com.sensoro.smartcity.widget.ProgressUtils;
import com.sensoro.smartcity.widget.SensoroToast;

import java.util.List;

import static com.sensoro.smartcity.constant.Constants.INPUT;

/**
 * Created by sensoro on 17/7/24.
 */

public class MerchantSwitchFragment extends BaseFragment<IMerchantSwitchFragmentView,
        MerchantSwitchFragmentPresenter> implements IMerchantSwitchFragmentView, AdapterView
        .OnItemClickListener, View
        .OnClickListener {
    private ListView mListView;
    private ImageView mMenuListImageView;
    private ImageView mSearchImageView;
    private View seperatorView;
    private View seperatorBottomView;
    private TextView mCurrentNameTextView;
    private TextView mCurrentPhoneTextView;
    private ImageView mCurrentStatusImageView;
    private MerchantAdapter mMerchantAdapter;
    private RelativeLayout rlTitleAccount;
    //
    private ProgressUtils mProgressUtils;


    public static MerchantSwitchFragment newInstance(String input) {
        MerchantSwitchFragment merchantSwitchFragment = new MerchantSwitchFragment();
        Bundle args = new Bundle();
        args.putString(INPUT, input);
        merchantSwitchFragment.setArguments(args);
        return merchantSwitchFragment;
    }


    @Override
    protected void initData(Context activity) {
        initView();
        mPrestener.initData(activity);
    }


    @Override
    protected int initRootViewId() {
        return R.layout.fragment_merchant;
    }

    @Override
    protected MerchantSwitchFragmentPresenter createPresenter() {
        return new MerchantSwitchFragmentPresenter();
    }


    @Override
    public void onDestroyView() {
        if (mRootView != null) {
            ((ViewGroup) mRootView.getParent()).removeView(mRootView);
        }
        if (mProgressUtils != null) {
            mProgressUtils.destroyProgress();
            mProgressUtils = null;
        }
        super.onDestroyView();
    }

    private void initView() {
        mProgressUtils = new ProgressUtils(new ProgressUtils.Builder(mRootFragment.getActivity()).build());
        mListView = (ListView) mRootView.findViewById(R.id.merchant_list);
        mMenuListImageView = (ImageView) mRootView.findViewById(R.id.merchant_iv_menu_list);
        mMenuListImageView.setOnClickListener(this);
        mSearchImageView = (ImageView) mRootView.findViewById(R.id.merchant_iv_search);
        mSearchImageView.setOnClickListener(this);
        mCurrentNameTextView = (TextView) mRootView.findViewById(R.id.merchant_current_name);
        mCurrentPhoneTextView = (TextView) mRootView.findViewById(R.id.merchant_current_phone);
        mCurrentStatusImageView = (ImageView) mRootView.findViewById(R.id.merchant_current_status);
        seperatorView = mRootView.findViewById(R.id.merchant_list_sep);
        seperatorBottomView = mRootView.findViewById(R.id.merchant_list_bottom_sep);
        rlTitleAccount = (RelativeLayout) mRootView.findViewById(R.id.rl_title_account);
        mMerchantAdapter = new MerchantAdapter(mRootFragment.getContext(), mPrestener.getUserInfoList());
        mListView.setAdapter(mMerchantAdapter);
        mListView.setOnItemClickListener(this);
    }

    public void refreshData(String username, String phone, String phoneId) {
        if (mPrestener != null) {
            mPrestener.refreshUserData(username, phone, phoneId);
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPrestener.clickItem(position);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.merchant_iv_menu_list:
                ((MainActivity) mRootFragment.getActivity()).getMenuDrawer().openMenu();
                break;
            case R.id.merchant_iv_search:
                mPrestener.startToSearchAC();
                break;
        }
    }

    @Override
    public void startAC(Intent intent) {

    }

    @Override
    public void finishAc() {

    }

    @Override
    public void startACForResult(Intent intent, int requestCode) {
        mRootFragment.getActivity().startActivityForResult(intent, requestCode);
    }

    @Override
    public void setIntentResult(int requestCode) {

    }

    @Override
    public void setIntentResult(int requestCode, Intent data) {

    }

    @Override
    public void showProgressDialog() {
        mProgressUtils.showProgress();
    }

    @Override
    public void dismissProgressDialog() {
        mProgressUtils.dismissProgress();
    }

    @Override
    public void toastShort(String msg) {
        SensoroToast.makeText(mRootFragment.getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void toastLong(String msg) {

    }

    @Override
    public void setCurrentStatusImageViewVisible(boolean visible) {
        mCurrentStatusImageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    @Override
    public void setCurrentNameAndPhone(String name, String phone) {
        mCurrentNameTextView.setText(name);
        mCurrentPhoneTextView.setText(phone);
    }

    @Override
    public void showSeperatorBottomView(boolean isShow) {
        if (isShow) {
            seperatorView.setVisibility(View.VISIBLE);
        } else {
            seperatorView.setVisibility(View.GONE);
            seperatorBottomView.setVisibility(View.GONE);
        }
    }

    @Override
    public void setAdapterSelectedIndex(int index) {
        mMerchantAdapter.setSelectedIndex(index);
    }

    @Override
    public void updateAdapterUserInfo(List<UserInfo> data) {
//        mMerchantAdapter.setDataList(data);
        mMerchantAdapter.notifyDataSetChanged();
    }

    @Override
    public void requestData() {
        mPrestener.requestData();
    }
}
