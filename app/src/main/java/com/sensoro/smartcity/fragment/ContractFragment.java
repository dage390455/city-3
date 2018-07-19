package com.sensoro.smartcity.fragment;

import android.content.Context;

import com.sensoro.smartcity.base.BaseFragment;
import com.sensoro.smartcity.imainviews.IContractFragmentView;
import com.sensoro.smartcity.presenter.ContractFragmentPresenter;

public class ContractFragment extends BaseFragment<IContractFragmentView, ContractFragmentPresenter> implements
        IContractFragmentView {
    @Override
    protected void initData(Context activity) {
    }

    @Override
    protected int initRootViewId() {
        return 0;
    }

    @Override
    protected ContractFragmentPresenter createPresenter() {
        return new ContractFragmentPresenter();
    }
}
