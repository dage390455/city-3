package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractDetailView;
import com.sensoro.smartcity.server.bean.ContractListInfo;

import java.io.Serializable;

public class ContractDetailPresenter extends BasePresenter<IContractDetailView> {
    private Activity mActivity;
    private ContractListInfo mContractInfo;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        Serializable serializableExtra = mActivity.getIntent().getSerializableExtra(Constants.EXTRA_CONTRACT_LIST_INFO);
        if (serializableExtra instanceof ContractListInfo) {
            mContractInfo = (ContractListInfo) serializableExtra;
        }
        init();
    }

    private void init() {
        if (mContractInfo == null) {
//            getView().toastShort(mActivity.getString(R.string.));
        }
    }

    @Override
    public void onDestroy() {

    }
}
