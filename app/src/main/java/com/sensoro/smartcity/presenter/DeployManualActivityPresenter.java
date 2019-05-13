package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.analyzer.DeployAnalyzerUtils;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployManualActivityView;
import com.sensoro.common.iwidget.IOnCreate;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.server.bean.InspectionIndexTaskInfo;
import com.sensoro.smartcity.server.bean.InspectionTaskDeviceDetail;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DeployManualActivityPresenter extends BasePresenter<IDeployManualActivityView> implements IOnCreate,
        Constants {
    private Activity mContext;
    private int scanType = -1;
    private InspectionIndexTaskInfo mTaskInfo;
    private InspectionTaskDeviceDetail mDeviceDetail;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        scanType = mContext.getIntent().getIntExtra(EXTRA_SCAN_ORIGIN_TYPE, -1);
        mTaskInfo = (InspectionIndexTaskInfo) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_INDEX_TASK_INFO);
        mDeviceDetail = (InspectionTaskDeviceDetail) mContext.getIntent().getSerializableExtra(EXTRA_INSPECTION_DEPLOY_OLD_DEVICE_INFO);
    }

    public void clickNext(String text) {
        if (!TextUtils.isEmpty(text) && text.length() == 16) {
            requestData(text);
        } else {
            getView().toastShort(mContext.getString(R.string.please_enter_the_correct_sn));
        }
    }

    private void requestData(final String scanSerialNumber) {
        getView().showProgressDialog();
        DeployAnalyzerUtils.getInstance().handlerDeployAnalyzerResult(this, scanType, scanSerialNumber, mContext, mTaskInfo, mDeviceDetail, new DeployAnalyzerUtils.OnDeployAnalyzerListener() {
            @Override
            public void onSuccess(Intent intent) {
                getView().dismissProgressDialog();
                getView().startAC(intent);
            }

            @Override
            public void onError(int errType, Intent intent, String errMsg) {
                getView().dismissProgressDialog();
                if (intent != null) {
                    getView().startAC(intent);
                } else {
                    getView().toastShort(errMsg);
                }
            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        int code = eventData.code;
        switch (code) {
            case EVENT_DATA_DEPLOY_RESULT_FINISH:
            case EVENT_DATA_DEPLOY_RESULT_CONTINUE:
                getView().finishAc();
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }
}
