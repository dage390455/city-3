package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IContractPreviewActivityView;

public class ContractPreviewActivityPresenter extends BasePresenter<IContractPreviewActivityView> {
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        String url = mContext.getIntent().getStringExtra(Constants.EXTRA_CONTRACT_PREVIEW_URL);
        if (!TextUtils.isEmpty(url)) {
            getView().loadUrl(url);
            return;
        }
//        String url ="https://testapi.fadada.com:8443/api//downLoadContract.action?app_id=401676&v=2.0&timestamp=20181226144442&contract_id=XYSTC-GDZS-181226-2-1545806681699&msg_digest=RDFEQUNBQkIzMkE0Mzk4OERGQzExRThFRDAwNUY1MTRCNzJFRDM3NQ==";
        String urlTest = "https://testapi.fadada.com:8443/api//viewContract.action?app_id=401676&v=2.0&timestamp=20181226152826&contract_id=1545809305470_76&msg_digest=MjVBMjQ2M0NFMEY4QkFERjgxMThFMkI3NkU2MjUwREMzMUNBMjkwRQ==";
        getView().loadUrl(urlTest);
    }

    @Override
    public void onDestroy() {

    }
}
