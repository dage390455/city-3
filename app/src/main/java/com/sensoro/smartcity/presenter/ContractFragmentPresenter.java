package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.sensoro.smartcity.activity.ContractIndexActivity;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.imainviews.IContractFragmentView;

public class ContractFragmentPresenter extends BasePresenter<IContractFragmentView> {
    private Activity mContext;
    @Override
    public void initData(Context context) {
        mContext= (Activity) context;
    }
    public void startToAdd(){
        Intent intent = new Intent(mContext, ContractIndexActivity.class);
        getView().startAC(intent);
    }
}
