package com.sensoro.forestfire.activity;

import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.sensoro.common.base.BaseActivity;
import com.sensoro.common.constant.ARouterConstants;
import com.sensoro.forestfire.R;
import com.sensoro.forestfire.imainviews.IForestFireListView;
import com.sensoro.forestfire.presenter.ForestFireListPresenter;

import butterknife.ButterKnife;

/**
 * @Author: jack
 * 时  间: 2019-09-17
 * 包  名: com.sensoro.forestfire.activity
 * 简  述: <功能简述>
 */

@Route(path = ARouterConstants.ACTIVITY_FORESTFIRE_LIST)
public class ForestFireListActivity extends BaseActivity<IForestFireListView, ForestFireListPresenter> {
    @Override
    protected void onCreateInit(Bundle savedInstanceState) {
        setContentView(R.layout.activity_forestfire_list);
        ButterKnife.bind(this);
        initView();
        mPresenter.initData(mActivity);
    }

    @Override
    protected ForestFireListPresenter createPresenter() {
        return new ForestFireListPresenter();
    }


    private void initView() {

    }

}
