package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.common.model.EventData;
import com.sensoro.smartcity.R;
import com.sensoro.smartcity.activity.ContractPreviewActivity;
import com.sensoro.smartcity.activity.ContractResultActivity;
import com.sensoro.smartcity.imainviews.IContractCreationSuccessView;

import org.greenrobot.eventbus.EventBus;

public class ContractCreationSuccessPresenter extends BasePresenter<IContractCreationSuccessView> {
    private Activity mActivity;
    private int id;
    private String url;

    @Override
    public void initData(Context context) {
        mActivity = (Activity) context;
        id = mActivity.getIntent().getIntExtra(Constants.EXTRA_CONTRACT_ID,0);
        url = mActivity.getIntent().getStringExtra(Constants.EXTRA_CONTRACT_PREVIEW_URL);

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

    public void doContractPreview() {
        if (TextUtils.isEmpty(url)) {
            getView().toastShort(mActivity.getString(R.string.preview_contract_failed));
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_CONTRACT_PREVIEW_URL, url);
        intent.setClass(mActivity, ContractPreviewActivity.class);
        getView().startAC(intent);
    }

    public void doShareCode() {
        if (id==0) {
            getView().toastShort(mActivity.getString(R.string.contract_id_failed));
            return;
        }
        Intent intent = new Intent();
        final String code = Constants.CONTRACT_WE_CHAT_BASE_URL + id;
        intent.putExtra(Constants.EXTRA_CONTRACT_ID_QRCODE, code);
        intent.setClass(mActivity, ContractResultActivity.class);
        getView().startAC(intent);
    }
}
