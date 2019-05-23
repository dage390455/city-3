package com.sensoro.nameplate.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.nameplate.IMainViews.INameplateDetailActivityView;
import com.sensoro.nameplate.activity.EditNameplateDetailActivity;

public class NameplateDetailActivityPresenter extends BasePresenter<INameplateDetailActivityView> {
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
    }

    @Override
    public void onDestroy() {

    }

    public void doNesSensor(int position) {

    }

    public void doEditNameplate() {
        Intent intent = new Intent(mContext, EditNameplateDetailActivity.class);
        getView().startAC(intent);
    }
}
