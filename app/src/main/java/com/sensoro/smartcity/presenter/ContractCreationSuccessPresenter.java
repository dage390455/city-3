package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractCreationSuccessView;
import com.sensoro.smartcity.model.EventData;

import org.greenrobot.eventbus.EventBus;

public class ContractCreationSuccessPresenter extends BasePresenter<IContractCreationSuccessView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
    }

    @Override
    public void onDestroy() {

    }

    public void finish() {
        EventData eventData = new EventData();
        eventData.code = Constants.EVENT_DATA_CONTRACT_CREATION_SUCCESS;
        EventBus.getDefault().post(eventData);
        getView().finishAc();
    }
}
