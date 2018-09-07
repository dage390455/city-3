package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.sensoro.smartcity.adapter.MainHomeFragRcContentAdapter;
import com.sensoro.smartcity.adapter.MainHomeFragRcTypeAdapter;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IHomeFragmentView;

public class HomeFragmentPresenter extends BasePresenter<IHomeFragmentView> {
    private Context mContext;

    @Override
    public void initData(Context context) {
        mContext = context;
        initRcType();
        initRcContent();

    }

    private void initRcContent() {
        MainHomeFragRcContentAdapter mainHomeFragRcContentAdapter = new MainHomeFragRcContentAdapter(mContext);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        getView().setRcContentAdapter(mainHomeFragRcContentAdapter,linearLayoutManager);
    }

    private void initRcType() {
        MainHomeFragRcTypeAdapter mainHomeFragRcTypeAdapter = new MainHomeFragRcTypeAdapter(mContext);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        getView().setRcTypeAdapter(mainHomeFragRcTypeAdapter,linearLayoutManager);
    }

    @Override
    public void onDestroy() {

    }
}
