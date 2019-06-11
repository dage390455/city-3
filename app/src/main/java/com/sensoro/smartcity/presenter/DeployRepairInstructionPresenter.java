package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.common.base.BasePresenter;
import com.sensoro.common.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployRepairInstructionView;

public class DeployRepairInstructionPresenter extends BasePresenter<IDeployRepairInstructionView> {
    private Activity mContext;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        String url = mContext.getIntent().getStringExtra(Constants.EXTRA_DEPLOY_CHECK_REPAIR_INSTRUCTION_URL);
        if (!TextUtils.isEmpty(url)) {
            getView().loadUrl(url);
        }else{
            getView().toastLong(mContext.getString(R.string.repair_instruction_obtain_failed));
        }
    }

    @Override
    public void onDestroy() {

    }
}
