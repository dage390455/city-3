package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.contractmanager.R;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractPreviewActivityView;

public class ContractPreviewActivityPresenter extends BasePresenter<IContractPreviewActivityView> {
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        String url = mContext.getIntent().getStringExtra(Constants.EXTRA_CONTRACT_PREVIEW_URL);
        if (!TextUtils.isEmpty(url)) {
            getView().loadUrl(url);
        }else{
            getView().toastLong(mContext.getString(R.string.preview_contract_failed));
        }
    }

    @Override
    public void onDestroy() {

    }
}
