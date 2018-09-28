package com.sensoro.smartcity.presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.sensoro.smartcity.R;
import com.sensoro.smartcity.base.BasePresenter;
import com.sensoro.smartcity.constant.Constants;
import com.sensoro.smartcity.imainviews.IDeployManualActivityView;
import com.sensoro.smartcity.iwidget.IOnCreate;
import com.sensoro.smartcity.model.EventData;
import com.sensoro.smartcity.util.DeployAnalyzerUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class DeployManualActivityPresenter extends BasePresenter<IDeployManualActivityView> implements IOnCreate,
        Constants {
    private Activity mContext;
    private int scanType = -1;

    @Override
    public void initData(Context context) {
        mContext = (Activity) context;
        onCreate();
        scanType = mContext.getIntent().getIntExtra(EXTRA_SCAN_ORIGIN_TYPE, -1);
    }

    public void clickNext(String text) {
        if (!TextUtils.isEmpty(text) && text.length() == 16) {
//            Intent intent = new Intent(this, DeployActivity.class);
//            intent.putExtra(EXTRA_SENSOR_SN, contentEditText.getText().toString().toUpperCase());
//            startActivity(intent);
            requestData(text);
        } else {
            getView().toastShort("请输入正确的SN,SN为16个字符");
        }
    }

    private void requestData(final String scanSerialNumber) {

        if (TextUtils.isEmpty(scanSerialNumber)) {
            getView().toastShort(mContext.getResources().getString(R.string.invalid_qr_code));
        } else {
            switch (scanType) {
                case TYPE_SCAN_DEPLOY_STATION:
                case TYPE_SCAN_DEPLOY_DEVICE:
                    getView().showProgressDialog();
                    DeployAnalyzerUtils.INSTANCE.getDeployAnalyzerResult(scanSerialNumber, mContext, new DeployAnalyzerUtils.OnDeployAnalyzerListener() {
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
                    break;
                case TYPE_SCAN_LOGIN:
                    break;
                case TYPE_SCAN_DEPLOY_DEVICE_CHANGE:
                    //TODO 巡检设备更换
                    break;
                case TYPE_SCAN_INSPECTION:
                    //TODO 扫描巡检设备
                    break;
                default:
                    break;
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(EventData eventData) {
        //TODO 可以修改以此种方式传递，方便管理
        int code = eventData.code;
        if (code == EVENT_DATA_DEPLOY_RESULT_FINISH || code == EVENT_DATA_DEPLOY_RESULT_CONTINUE) {
            getView().finishAc();
        }
//        LogUtils.loge(this, eventData.toString());
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
