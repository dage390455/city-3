package com.sensoro.smartcity.presenter;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

import com.sensoro.smartcity.adapter.MainWarnFragRcContentAdapter;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IWarnFragmentView;

public class WarnFragmentPresenter extends BasePresenter<IWarnFragmentView> {
    private Context mContext;

    @Override
    public void initData(Context context) {
        mContext = context;
        initRcContent();

    }

    private void initRcContent() {
        MainWarnFragRcContentAdapter mainWarnFragRcContentAdapter = new MainWarnFragRcContentAdapter(mContext);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        getView().setRcContentAdapter(mainWarnFragRcContentAdapter,linearLayoutManager);
    }

    @Override
    public void onDestroy() {

    }
}
