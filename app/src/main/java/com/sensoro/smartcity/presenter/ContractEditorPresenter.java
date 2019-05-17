package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.sensoro.smartcity.R;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractEditorView;
import com.sensoro.common.model.EventData;
import com.sensoro.common.server.bean.ContractListInfo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

public class ContractEditorPresenter extends BasePresenter<IContractEditorView> {
    private Activity mActivity;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        EventBus.getDefault().register(this);
//        getView().showPersonalFragment();
        init();
    }

    private void init() {
        int origin = mActivity.getIntent().getIntExtra(Constants.EXTRA_CONTRACT_ORIGIN_TYPE, -1);
        switch (origin) {
            case 1:
                //创建合同
                getView().showPersonalFragment();
                getView().setOriginFormList(false);
                break;
            case 2:
                //编辑合同
                displayEditContract();
                getView().setOriginFormList(true);
                break;
            default:
                //默认创建合同
                getView().showPersonalFragment();
                getView().setOriginFormList(false);
                break;
        }

    }

    private void displayEditContract() {
        Serializable serializableExtra = mActivity.getIntent().getSerializableExtra(Constants.EXTRA_CONTRACT_INFO);
        if (serializableExtra instanceof ContractListInfo) {
            ContractListInfo contractListInfo = (ContractListInfo) serializableExtra;
            //隐藏上方tab
            getView().setTopTabVisible(false);
            getView().setTitleText(mActivity.getString(R.string.edit_contract));
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constants.EXTRA_CONTRACT_INFO, contractListInfo);
            switch (contractListInfo.getContract_type()) {
                case 1:
                    getView().businessFragmentSetArguments(bundle);
                    getView().showBusinessFragment();
                    break;
                case 2:
                    getView().personalFragmentSetArguments(bundle);
                    getView().showPersonalFragment();
                    break;
            }

        } else {
            getView().toastShort(mActivity.getString(R.string.contract_info_obtain_failed));
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        Object data = eventData.data;
        switch (code) {
            case Constants.EVENT_DATA_CONTRACT_CREATION_SUCCESS:
                getView().finishAc();
                break;
        }
    }
}
